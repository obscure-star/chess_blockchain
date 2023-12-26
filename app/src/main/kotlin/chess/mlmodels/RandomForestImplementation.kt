package chess.mlmodels

import chess.mlmodels.dataframes.DataProcessing
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.evaluation.classification.Evaluation
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.nd4j.linalg.schedule.MapSchedule
import org.nd4j.linalg.schedule.ScheduleType
import java.sql.Connection

class RandomForestImplementation {
    private val dataProcessing = DataProcessing()

    fun implementation(connection: Connection): String {
        // Data Processing
        val data = connection.createStatement().executeQuery("SELECT * FROM Chess_database.CHESS_DATA")
        val features = mutableListOf<DoubleArray>()
        val labels = mutableListOf<String>()

        while (data.next()) {
            val boardRepresentation = data.getString("BOARD_REPRESENTATION_INT").toDouble()
            val pieceCount =
                dataProcessing.convertJsonToPieceCount(
                    data.getString("PIECE_COUNT"),
                ).toList().toDoubleArray()
            val pieceActivity =
                dataProcessing.convertJsonToPieceActivity(
                    data.getString("PIECE_ACTIVITY"),
                ).toList().toDoubleArray()
            val kingSafety =
                dataProcessing.convertJsonToKingSafety(
                    data.getString("KING_SAFETY"),
                ).toList().toDoubleArray()
            val materialBalance =
                dataProcessing.convertJsonToMaterialBalance(
                    data.getString("MATERIAL_BALANCE"),
                ).toList().toDoubleArray()
            val centerControl =
                dataProcessing.convertJsonToCenterControl(
                    data.getString("CENTER_CONTROL"),
                ).toList().toDoubleArray()
            val nextMove = data.getString("NEXT_MOVE")

            val featureRow =
                listOf(boardRepresentation).toDoubleArray() +
                    pieceCount + pieceActivity + kingSafety + materialBalance + centerControl
            features.add(featureRow)
            if (nextMove.length > 1) {
                labels.add(nextMove)
            } else {
                labels.add("END")
            }
        }
        val featuresMatrix = Nd4j.create(features.toTypedArray())
        val labelMap = labels.distinct().withIndex().associate { it.value to it.index }
        val numericalLabels = labels.map { labelMap[it]?.toDouble()!! }.toDoubleArray()
        val numClasses = numericalLabels.maxOrNull()!! + 1 // Assumes labels are 0-indexed

        val oneHotLabels =
            numericalLabels.map { label ->
                val oneHotLabel = Nd4j.zeros(1, numClasses.toInt())
                oneHotLabel.putScalar(0, label.toLong(), 1.0) // Set the corresponding index to 1
                oneHotLabel
            }

        val labelsMatrix = Nd4j.vstack(oneHotLabels)

        // Create DataSet
        val dataSet = DataSet(featuresMatrix, labelsMatrix)

        // Normalize the data
        val normalizer: DataNormalization = NormalizerStandardize()
        normalizer.fit(dataSet)
        normalizer.transform(dataSet)

        // Split the data into training and testing sets
        val split = dataSet.splitTestAndTrain(0.8) // 80% training, 20% testing issue

        // Get training and testing data sets
        val trainingData = split.train
        val testingData = split.test

        // Create a DataSetIterator for training
        val batchSize = 32
        val iterator: DataSetIterator = ListDataSetIterator(trainingData.toList(), batchSize)

        val conf: MultiLayerConfiguration =
            NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .activation(Activation.RELU)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Sgd(0.05))
                .list()
                .layer(
                    DenseLayer.Builder()
                        .nIn(featuresMatrix.columns())
                        .nOut(64)
                        .build(),
                )
                .layer(
                    OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(64)
                        .nOut(labelMap.size) // Number of classes
                        .build(),
                )
                .build()

        // Build and train the model
        val model = MultiLayerNetwork(conf)
        model.init()

        // Schedule learning rate
        val learningRateSchedule =
            MapSchedule.Builder(ScheduleType.EPOCH)
                .add(0, 0.05)
                .add(10, 0.01)
                .add(20, 0.001)
                .add(30, 0.0001)
                .build()
        model.setLearningRate(learningRateSchedule)

        for (epoch in 0..50) {
            model.fit(trainingData) // 50 epochs
        }

        // Evaluate the model
        val evaluation = model.evaluate<Evaluation>(iterator)
        println("Accuracy: ${evaluation.accuracy()}")

        // Make predictions
        val predictedLabels = model.predict(testingData.features)
        // Convert numerical predictions back to original labels
        val predictedLabelsOriginal = predictedLabels.map { labelMap.entries.find { entry -> entry.value == it }!!.key }
        println(predictedLabelsOriginal)
        return predictedLabelsOriginal[0]
    }
}
