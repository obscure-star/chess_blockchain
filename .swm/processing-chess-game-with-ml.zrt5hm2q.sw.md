---
title: Processing Chess game with ML
---
We need two tables to determine the next move for our chess game.

This would involve getting the FEN position of pieces and the next move made in the training data

FEN position: <https://www.wikiwand.com/en/Forsyth%E2%80%93Edwards_Notation>

"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR is the board representation

"w" is the player that is currently playing

"KQkq" describes black and white kings' ability to castle

For adding the next_move column the following sql scripts should run:

<SwmSnippet path="/app/src/main/kotlin/chess/database/chess_database_columns.sql" line="2">

---

&nbsp;

```plsql
CREATE TABLE CHESS_DATA (
                            ID SERIAL PRIMARY KEY,
                            GAME_ID VARCHAR(36),
                            ROUND INT,
                            BOARD_REPRESENTATION VARCHAR(64),
                            BOARD_REPRESENTATION_INT INT,
                            PIECE_COUNT JSON,
                            THREATS_AND_ATTACKS JSON,
                            PIECE_ACTIVITY JSON,
                            KING_SAFETY JSON,
                            PAWN_STRUCTURE JSON,
                            MATERIAL_BALANCE JSON,
                            CENTER_CONTROL JSON,
                            PREVIOUS_MOVES JSON,
                            MOVE VARCHAR(36),
                            WHITE_WINS BOOLEAN,
                            BLACK_WINS BOOLEAN,
                            WINNER INT,
                            NEXT_MOVE VARCHAR(36),
                            NEXT_MOVE_INDEX INT,
                            LENGTH_LEGAL_MOVES INT,
                            LEGAL_MOVES JSON
);

DROP TABLE CHESS_DATA;
```

---

</SwmSnippet>

CNN can be used to determine the best move - example use case: <https://www.kaggle.com/code/ancientaxe/simple-cnn-model-using-torch>

For figuring out a library to integrate with machine learning: <https://www.reddit.com/r/Kotlin/comments/8w2fld/comment/e1tjnzd/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button>

Using deepLearning4j:

<https://deeplearning4j.konduit.ai/>

imports for mac arm processor:

<SwmSnippet path="/app/build.gradle.kts" line="29">

---

&nbsp;

```kotlin
    implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M2.1")
    implementation("org.datavec:datavec-api:1.0.0-M2.1")

    implementation("org.nd4j:nd4j-native:1.0.0-M2.1")
    implementation("org.nd4j:nd4j-native:1.0.0-M2.1:macosx-arm64")
    implementation("org.bytedeco:openblas:0.3.21-1.5.8:macosx-arm64")
```

---

</SwmSnippet>

<p align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2F9cbb2fa0-e488-4272-9a74-85efe436a9d4.png?alt=media&amp;token=b58b0db2-c141-4ac0-a662-5154a1237c07" style="width: 100%"></p>

The output should look similar to this

Convolutional Neural Network configuration:

Using the features listed here:

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="136">

---

&nbsp;

```kotlin
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
```

---

</SwmSnippet>

And the Next move label here:

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="189">

---

&nbsp;

```kotlin
        // val nextMove = data.getString("NEXT_MOVE")
```

---

</SwmSnippet>

The features and the labels needed for processing the ML feature can be aggregated from the CHESS_DATA table and be fed into the Neural Network model.

First the features need to be turned into an Nd4j NDArray. An NDArray is a n-dimensional array (a rectangular array of numbers). Documentation on Nd4j: <https://deeplearning4j.konduit.ai/nd4j/tutorials/quickstart>

The features and the labels are converted into NDArrays to be compatible with the Nd4j operations.

Creating an Nd4j NDArray for the features:

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="40">

---

&nbsp;

```kotlin
        val featuresMatrix = Nd4j.create(features.toTypedArray())
```

---

</SwmSnippet>

For the Labels a map is created that maps distinct labels in the String format (e.g. "e2-e4") to their corresponding index.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="41">

---

&nbsp;

```kotlin
        labelMap = labels.distinct().withIndex().associate { it.value to it.index }
```

---

</SwmSnippet>

The categorical labels are then converted to values of type Double and converted into a DoubleArray which is required for one hot encoding.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="42">

---

&nbsp;

```kotlin
        val numericalLabels = labels.map { labelMap[it]?.toDouble()!! }.toDoubleArray()

        // For the number of classifications. Assumes labels are 0-indexed
        val numClasses = numericalLabels.maxOrNull()!! + 1
```

---

</SwmSnippet>

One hot labels are created by creating a row of zeros with a column number of numClasses and setting the elements in the indexes of each label's value to 1 as seen here:

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="47">

---

&nbsp;

```kotlin
        val oneHotLabels =
            numericalLabels.map { label ->
                val oneHotLabel = Nd4j.zeros(1, numClasses.toInt())
                oneHotLabel.putScalar(0, label.toLong(), 1.0) // Set the corresponding index to 1
                oneHotLabel
            }
```

---

</SwmSnippet>

The one hot labels are stacked with vstack to add each NDArray in the oneHotLabels list into a vertical stack to make an NDArray matrix to use for the model.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="54">

---

&nbsp;

```kotlin
        val labelsMatrix = Nd4j.vstack(oneHotLabels)
```

---

</SwmSnippet>

The NDarrays for the features and the labels are then merged into a dataset for the model. The `DataSet` class in DL4J is designed to hold pairs of input features and corresponding labels for supervised learning tasks.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="57">

---

&nbsp;

```kotlin
        val dataSet = DataSet(featuresMatrix, labelsMatrix)
```

---

</SwmSnippet>

We then need to Normalize the data to ensure that the features have similar scale during the model training phase. This mitigates numerical instabilities and also allows for interpreting different measurement units.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="59">

---

&nbsp;

```kotlin
        // Normalize the data
        normalizer.fit(dataSet)
        normalizer.transform(dataSet)
```

---

</SwmSnippet>

The dataset is then split (not random) into a training set and a test set where 80% of the dataset is training and 20% of the dataset is for testing

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="63">

---

&nbsp;

```kotlin
        // Split the data into training and testing sets
        val split = dataSet.splitTestAndTrain(0.8) // 80% training, 20% testing

        // Get training and testing data sets
        val trainingData = split.train
        val testingData = split.test
```

---

</SwmSnippet>

An iterator is created which turns the training data dataset into an array list and uses a batch size to indicate how many training examples should be processed in 1 iteration which allows for efficiency and convergence

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="70">

---

&nbsp;

```kotlin
        // Create a DataSetIterator for training
        val batchSize = 32
        val iterator: DataSetIterator = ListDataSetIterator(trainingData.toList(), batchSize)
```

---

</SwmSnippet>

We will be using the deeplearning4j's Neural Network configuration to configure the model. This configuration will describe the weight init between each neuron, the activation function used to calculate the output on the neuron based on the inputs to the neuron and the weight associated with the connection to the neuron. The weights to each neuron can be optimized using an optimization algorithm (such as gradient descent) and a backpropagation algorithm which uses a loss function to determine the deviation of the prediction from the actual result.

We will be using a weightInit of RELU made specifically for the RELU activation function which will initializes the weights with values drawn from a Gaussian distribution with mean 0 and variance 2/nin​, where nin​ is the number of input neurons.

[Study](https://arxiv.org/abs/1502.01852) that shows how RELU supports the RELU activation function

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="75">

---

&nbsp;

```kotlin
            NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.RELU)
```

---

</SwmSnippet>

Stochastic gradient descent is selected as the optimization algo as it will allow for the model parameters to be updated after each mini batch of training examples. This will allow frequent adjustments. The updater takes the gradients used by the stochastic gradient descent and adjust the weights to minimize the loss function.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="78">

---

&nbsp;

```kotlin
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
```

---

</SwmSnippet>

The learning rate is adaptive and determines the steps size for weight updates that will be taken by the optimizer to determine the loss function

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="79">

---

&nbsp;

```kotlin
                .updater(Adam(0.0001))
```

---

</SwmSnippet>

The learning rate mentioned is the starting point and is adjusted during training depending on the mean and variance of the gradients.

We are now going to define the layers using the DeepLearning4j Listbuilder class

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="80">

---

&nbsp;

```kotlin
                .list()
```

---

</SwmSnippet>

### First layer

Our first layer of nodes will be a dense layer. This type of layer was chosen because it handles categorical features.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="82">

---

&nbsp;

```kotlin
                    DenseLayer.Builder()
                        .nIn(featuresMatrix.columns())
                        .nOut(4096)
                        .build(),
```

---

</SwmSnippet>

### Output Layer

For the output layer we will be using the NegativeLogLikelihood loss function which is commonly used for multi class classification problems. The softmax activation function is also used to convert the raw output values into a probability distribution over multiple classes.

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="94">

---

&nbsp;

```kotlin
                    OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(2048)
                        .nOut(labelMap.size) // Number of classes
                        .build(),
```

---

</SwmSnippet>

The model is instantiated and runs over 50 epochs (the amount of rounds that the model will try to fit).

<SwmSnippet path="/app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt" line="103">

---

&nbsp;

```kotlin
        model = MultiLayerNetwork(conf)
        model.init()

        for (epoch in 0..50) {
            model.fit(trainingData) // 50 epochs
        }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
