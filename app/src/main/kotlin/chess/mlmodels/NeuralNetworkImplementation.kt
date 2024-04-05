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
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions
import java.sql.Connection
import java.sql.ResultSet

class NeuralNetworkImplementation(val connection: Connection) {
    private val dataProcessing = DataProcessing()
    private lateinit var labelMap: Map<Double, Int>
    private val normalizer: DataNormalization = NormalizerStandardize()
    lateinit var model: MultiLayerNetwork

    init {
        buildModel()
    }

    fun buildModel() {
        // Data Processing
        val data = processRecords()
        val features = data.first
        val labels = data.second

        val featuresMatrix = Nd4j.create(features.toTypedArray())
        labelMap = labels.distinct().withIndex().associate { it.value to it.index }
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

        val conf: MultiLayerConfiguration =
            NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.RELU)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Adam(0.0001))
                .list()
                .layer(
                    DenseLayer.Builder()
                        .nIn(featuresMatrix.columns())
                        .nOut(4096)
                        .build(),
                )
                .layer(
                    DenseLayer.Builder()
                        .nIn(4096)
                        .nOut(2048)
                        .build(),
                )
                .layer(
                    OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(2048)
                        .nOut(labelMap.size) // Number of classes
                        .build(),
                )
                .build()

        // Build and train the model
        model = MultiLayerNetwork(conf)
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
    }

    private fun processRecords(): Pair<MutableList<DoubleArray>, MutableList<Double>> {
        // Data Processing
        val data = connection.createStatement().executeQuery("SELECT * FROM Chess_database.CHESS_DATA")
        val features = mutableListOf<DoubleArray>()
        val labels = mutableListOf<Double>()

        while (data.next() && !data.isLast) {
            features.add(getFeatureRow(data))
            labels.add(getLabel(data))
        }
        return Pair(features, labels)
    }

    private fun getFeatureRow(data: ResultSet): DoubleArray {
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

        return listOf(boardRepresentation).toDoubleArray() + round + lengthLegalMoves +
            pieceCount + pieceActivity + kingSafety + materialBalance + centerControl
    }

    private fun getLabel(data: ResultSet): Double {
        val nextMoveIndex = data.getDouble("NEXT_MOVE_INDEX")
        if (nextMoveIndex < 0) {
            return 0.0
        }
        return nextMoveIndex
    }

    fun predictMove(
        aiPlayerName: String,
        legalMoves: MutableList<String>?,
    ): String {
        if (legalMoves?.size == 1) {
            return legalMoves[0]
        }
        // get legal moves of the last row
        val data = connection.createStatement().executeQuery("SELECT * FROM Chess_database.CHESS_DATA ORDER BY ID DESC LIMIT 1;")

        data.next()
        val features = mutableListOf<DoubleArray>()
        val labels = mutableListOf<Double>()

        features.add(getFeatureRow(data))
        labels.add(getLabel(data))

        // val nextMove = data.getString("NEXT_MOVE")
        // val legalMoves =
        //    Json.decodeFromString<LegalMoves>(data.getString("LEGAL_MOVES")).getLegalMoves(aiPlayerName).filter {
        //        "${it[0]}${it[1]}" != "${nextMove[3]}${nextMove[4]}"
        //    }

        val featuresMatrix = Nd4j.create(features.toTypedArray())
        val labelsMatrix = Nd4j.create(labels)

        val dataSet = DataSet(featuresMatrix, labelsMatrix)

        // Normalize the data
        normalizer.transform(dataSet)

        val predictedLabel = model.predict(dataSet.features)
        val predictedLabelsOriginal = predictedLabel.map { labelMap.entries.find { entry -> entry.value == it }!!.key }

        val predictedIndex = predictedLabelsOriginal.lastOrNull()?.toInt()

        // Check if the index is within the valid range
        if (predictedIndex != null && predictedIndex in 0 until (legalMoves?.size ?: 0)) {
            return legalMoves?.get(predictedIndex) ?: run {
                // Handle the case where the index is out of bounds
                println("Invalid predicted index: $predictedIndex")
                // Rebuild model and then predict
                buildModel()
                return predictMove(aiPlayerName, legalMoves)
            }
        }

        // Handle the case where the index is out of bounds
        println("Invalid predicted index: $predictedIndex")
        // Rebuild model and then predict
        buildModel()
        return predictMove(aiPlayerName, legalMoves)
    }
}
