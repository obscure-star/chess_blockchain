package chess.mlmodels

import chess.mlmodels.dataframes.DataProcessing
import chess.mlmodels.dataframes.LegalMoves
import kotlinx.serialization.json.Json
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
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions
import java.sql.Connection
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max

class NeuralNetworkImplementation {
    private val dataProcessing = DataProcessing()

    fun implementation(
        connection: Connection,
        aiPlayerName: String,
    ): String {
        // Data Processing
        val data = connection.createStatement().executeQuery("SELECT * FROM Chess_database.CHESS_DATA")
        val features = mutableListOf<DoubleArray>()
        val labels = mutableListOf<Double>()

        while (data.next() && !data.isLast) {
            val boardRepresentation = data.getString("BOARD_REPRESENTATION_INT").toDouble()
            val round = data.getString("ROUND").toDouble()
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
            val lengthLegalMoves = data.getDouble("LENGTH_LEGAL_MOVES")
            val nextMoveIndex = data.getDouble("NEXT_MOVE_INDEX")
            val nextMove = data.getString("NEXT_MOVE")

            val featureRow =
                listOf(boardRepresentation).toDoubleArray() + round + lengthLegalMoves +
                    pieceCount + pieceActivity + kingSafety + materialBalance + centerControl
            features.add(featureRow)
            if (nextMoveIndex > 0) {
                labels.add(nextMoveIndex)
            } else {
                labels.add(0.0)
            }
        }

        // get legal moves of the last row
        val legalMoves = Json.decodeFromString<LegalMoves>(data.getString("LEGAL_MOVES")).getLegalMoves(aiPlayerName)

        val featuresMatrix = Nd4j.create(features.toTypedArray())
        val labelMap = labels.distinct().withIndex().associate { it.value to it.index }
        val numericalLabels = labels.map { labelMap[it]?.toDouble()!! }.toDoubleArray()
        // For the number of classifications. Assumes labels are 0-indexed
        val numClasses = numericalLabels.maxOrNull()!! + 1

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
        val split = dataSet.splitTestAndTrain(0.8) // 80% training, 20% testing

        // Get training and testing data sets
        val trainingData = split.train
        val testingData = split.test

        // Create a DataSetIterator for training
        val batchSize = 32
        val iterator: DataSetIterator = ListDataSetIterator(trainingData.toList(), batchSize)

        val firstLayerSize =
            max(
                8.0,
                ceil(ln(featuresMatrix.columns().toDouble()) / ln(2.0)),
            ).toInt() // Minimum size of 4
        val secondLayerSize =
            max(
                4.0,
                ceil(ln(firstLayerSize.toDouble()) / ln(2.0)),
            ).toInt() // Minimum size of 4

        val conf: MultiLayerConfiguration =
            NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.RELU)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Adam(0.001))
                .list()
                .layer(
                    DenseLayer.Builder()
                        .nIn(featuresMatrix.columns())
                        .nOut(128)
                        .build(),
                )
                .layer(
                    OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(128)
                        .nOut(labelMap.size) // Number of classes
                        .build(),
                )
                .build()

        // Build and train the model
        val model = MultiLayerNetwork(conf)
        model.init()

        for (epoch in 0..50) {
            model.fit(trainingData) // 50 epochs
        }

        // Evaluate the model
        val evaluation = model.evaluate<Evaluation>(iterator)
        println("Accuracy: ${evaluation.accuracy()}")
        println("Precision: ${evaluation.precision()}")

        // Make predictions
        val predictedLabels = model.predict(testingData.features)
        // Convert numerical predictions back to original labels
        val predictedLabelsOriginal = predictedLabels.map { labelMap.entries.find { entry -> entry.value == it }!!.key }
        println(predictedLabelsOriginal)

        val predictedIndex = predictedLabelsOriginal.lastOrNull()?.toInt()

        // Check if the index is within the valid range
        if (predictedIndex != null && predictedIndex >= 0 && predictedIndex < legalMoves.size) {
            return legalMoves[predictedIndex]
        } else {
            // Handle the case where the index is out of bounds
            println("Invalid predicted index: $predictedIndex")
            // You might return a default move or throw an exception, depending on your requirements
            return implementation(connection, aiPlayerName)
        }
    }
}
