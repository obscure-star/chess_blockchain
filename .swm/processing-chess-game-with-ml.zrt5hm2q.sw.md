---
id: zrt5hm2q
title: Processing Chess game with ML
file_version: 1.1.3
app_version: 1.18.42
---

We need two tables to determine the next move for our chess game.

This would involve getting the FEN position of pieces and the next move made in the training data

FEN position: [https://www.wikiwand.com/en/Forsyth%E2%80%93Edwards\_Notation](https://www.wikiwand.com/en/Forsyth%E2%80%93Edwards_Notation)

"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

nbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR is the board representation

"w" is the player that is currently playing

"KQkq" describes black and white kings' ability to castle

For adding the next\_move column the following sql scripts should run:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/chess_database_columns.sql
```plsql
2      CREATE TABLE CHESS_DATA (
3                                  ID SERIAL PRIMARY KEY,
4                                  GAME_ID VARCHAR(36),
5                                  ROUND INT,
6                                  BOARD_REPRESENTATION VARCHAR(64),
7                                  BOARD_REPRESENTATION_INT INT,
8                                  PIECE_COUNT JSON,
9                                  THREATS_AND_ATTACKS JSON,
10                                 PIECE_ACTIVITY JSON,
11                                 KING_SAFETY JSON,
12                                 PAWN_STRUCTURE JSON,
13                                 MATERIAL_BALANCE JSON,
14                                 CENTER_CONTROL JSON,
15                                 PREVIOUS_MOVES JSON,
16                                 MOVE VARCHAR(36),
17                                 WHITE_WINS BOOLEAN,
18                                 BLACK_WINS BOOLEAN,
19                                 WINNER INT,
20                                 NEXT_MOVE VARCHAR(36),
21                                 NEXT_MOVE_INDEX INT,
22                                 LENGTH_LEGAL_MOVES INT,
23                                 LEGAL_MOVES JSON
24     );
25     
26     DROP TABLE CHESS_DATA;
```

<br/>

CNN can be used to determine the best move - example use case: [https://www.kaggle.com/code/ancientaxe/simple-cnn-model-using-torch](https://www.kaggle.com/code/ancientaxe/simple-cnn-model-using-torch)

For figuring out a library to integrate with machine learning: [https://www.reddit.com/r/Kotlin/comments/8w2fld/comment/e1tjnzd/?utm\_source=share&utm\_medium=web3x&utm\_name=web3xcss&utm\_term=1&utm\_content=share\_button](https://www.reddit.com/r/Kotlin/comments/8w2fld/comment/e1tjnzd/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button)

Using deepLearning4j:

[https://deeplearning4j.konduit.ai/](https://deeplearning4j.konduit.ai/)

imports for mac arm processor:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/build.gradle.kts
```kotlin
29         implementation("org.deeplearning4j:deeplearning4j-core:1.0.0-M2.1")
30         implementation("org.datavec:datavec-api:1.0.0-M2.1")
31     
32         implementation("org.nd4j:nd4j-native:1.0.0-M2.1")
33         implementation("org.nd4j:nd4j-native:1.0.0-M2.1:macosx-arm64")
34         implementation("org.bytedeco:openblas:0.3.21-1.5.8:macosx-arm64")
```

<br/>

<div align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2F9cbb2fa0-e488-4272-9a74-85efe436a9d4.png?alt=media&token=b58b0db2-c141-4ac0-a662-5154a1237c07" style="width:'100%'"/></div>

<br/>

The output should look similar to this

Convolutional Neural Network configuration:

Using the features listed here:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
38                 val boardRepresentation = data.getString("BOARD_REPRESENTATION_INT").toDouble()
39                 val round = data.getString("ROUND").toDouble()
40                 val pieceCount =
41                     dataProcessing.convertJsonToPieceCount(
42                         data.getString("PIECE_COUNT"),
43                     ).toList().toDoubleArray()
44                 val pieceActivity =
45                     dataProcessing.convertJsonToPieceActivity(
46                         data.getString("PIECE_ACTIVITY"),
47                     ).toList().toDoubleArray()
48                 val kingSafety =
49                     dataProcessing.convertJsonToKingSafety(
50                         data.getString("KING_SAFETY"),
51                     ).toList().toDoubleArray()
52                 val materialBalance =
53                     dataProcessing.convertJsonToMaterialBalance(
54                         data.getString("MATERIAL_BALANCE"),
55                     ).toList().toDoubleArray()
56                 val centerControl =
57                     dataProcessing.convertJsonToCenterControl(
58                         data.getString("CENTER_CONTROL"),
59                     ).toList().toDoubleArray()
```

<br/>

And the Next move label here:<br/>

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
56                 val nextMove = data.getString("NEXT_MOVE")
```

<br/>

The features and the labels needed for processing the ML feature can be aggregated from the CHESS\_DATA table and be fed into the Neural Network model.

First the features need to be turned into an Nd4j NDArray. An NDArray is a n-dimensional array (a rectangular array of numbers). Documentation on Nd4j: [https://deeplearning4j.konduit.ai/nd4j/tutorials/quickstart](https://deeplearning4j.konduit.ai/nd4j/tutorials/quickstart)

The features and the labels are converted into NDArrays to be compatible with the Nd4j operations.

Creating an Nd4j NDArray for the features:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
68             val featuresMatrix = Nd4j.create(features.toTypedArray())
```

<br/>

For the Labels a map is created that maps distinct labels in the String format (e.g. "e2-e4") to their corresponding index.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
69             val labelMap = labels.distinct().withIndex().associate { it.value to it.index }
```

<br/>

The categorical labels are then converted to values of type Double and converted into a DoubleArray which is required for one hot encoding.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
70             val numericalLabels = labels.map { labelMap[it]?.toDouble()!! }.toDoubleArray()
71             // For the number of classifications. Assumes labels are 0-indexed
72             val numClasses = numericalLabels.maxOrNull()!! + 1
```

<br/>

One hot labels are created by creating a row of zeros with a column number of numClasses and setting the elements in the indexes of each label's value to 1 as seen here:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
74             val oneHotLabels =
75                 numericalLabels.map { label ->
76                     val oneHotLabel = Nd4j.zeros(1, numClasses.toInt())
77                     oneHotLabel.putScalar(0, label.toLong(), 1.0) // Set the corresponding index to 1
78                     oneHotLabel
79                 }
```

<br/>

The one hot labels are stacked with vstack to add each NDArray in the oneHotLabels list into a vertical stack to make an NDArray matrix to use for the model.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
81             val labelsMatrix = Nd4j.vstack(oneHotLabels)
```

<br/>

The NDarrays for the features and the labels are then merged into a dataset for the model. The `DataSet` class in DL4J is designed to hold pairs of input features and corresponding labels for supervised learning tasks.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
84             val dataSet = DataSet(featuresMatrix, labelsMatrix)
```

<br/>

We then need to Normalize the data to ensure that the features have similar scale during the model training phase. This mitigates numerical instabilities and also allows for interpreting different measurement units.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
86             // Normalize the data
87             val normalizer: DataNormalization = NormalizerStandardize()
88             normalizer.fit(dataSet)
89             normalizer.transform(dataSet)
```

<br/>

The dataset is then split (not random) into a training set and a test set where 80% of the dataset is training and 20% of the dataset is for testing

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
91             // Split the data into training and testing sets
92             val split = dataSet.splitTestAndTrain(0.8) // 80% training, 20% testing
93     
94             // Get training and testing data sets
95             val trainingData = split.train
96             val testingData = split.test
```

<br/>

An iterator is created which turns the training data dataset into an array list and uses a batch size to indicate how many training examples should be processed in 1 iteration which allows for efficiency and convergence

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
98             // Create a DataSetIterator for training
99             val batchSize = 32
100            val iterator: DataSetIterator = ListDataSetIterator(trainingData.toList(), batchSize)
```

<br/>

We will be using the deeplearning4j's Neural Network configuration to configure the model. This configuration will describe the weight init between each neuron, the activation function used to calculate the output on the neuron based on the inputs to the neuron and the weight associated with the connection to the neuron. The weights to each neuron can be optimized using an optimization algorithm (such as gradient descent) and a backpropagation algorithm which uses a loss function to determine the deviation of the prediction from the actual result.

We will be using a weightInit of RELU made specifically for the RELU activation function which will initializes the weights with values drawn from a Gaussian distribution with mean 0 and variance 2/nin​, where nin​ is the number of input neurons.

[Study](https://arxiv.org/abs/1502.01852) that shows how RELU supports the RELU activation function

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
103                NeuralNetConfiguration.Builder()
104                    .weightInit(WeightInit.RELU)
105                    .activation(Activation.RELU)
```

<br/>

Stochastic gradient descent is selected as the optimization algo as it will allow for the model parameters to be updated after each mini batch of training examples. This will allow frequent adjustments. The updater takes the gradients used by the stochastic gradient descent and adjust the weights to minimize the loss function.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
106                    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
```

<br/>

The learning rate is adaptive and determines the steps size for weight updates that will be taken by the optimizer to determine the loss function

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
109                    .updater(Adam(0.001))
```

<br/>

The learning rate mentioned is the starting point and is adjusted during training depending on the mean and variance of the gradients.

We are now going to define the layers using the DeepLearning4j Listbuilder class

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
110                    .list()
```

<br/>

### First layer

Our first layer of nodes will be a dense layer. This type of layer was chosen because it handles categorical features.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
130                        DenseLayer.Builder()
131                            .nIn(featuresMatrix.columns())
132                            .nOut(128)
133                            .build(),
```

<br/>

### Output Layer

For the output layer we will be using the NegativeLogLikelihood loss function which is commonly used for multi class classification problems. The softmax activation function is also used to convert the raw output values into a probability distribution over multiple classes.

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
136                        OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
137                            .activation(Activation.SOFTMAX)
138                            .nIn(128)
139                            .nOut(labelMap.size) // Number of classes
140                            .build(),
```

<br/>

The model is instantiated and runs over 50 epochs (the amount of rounds that the model will try to fit).

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/mlmodels/NeuralNetworkImplementation.kt
```kotlin
143            val model = MultiLayerNetwork(conf)
144            model.init()
145    
146            for (epoch in 0..50) {
147                model.fit(trainingData) // 50 epochs
148            }
```

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/zrt5hm2q).
