---
id: wvpiwo7l
title: Setting up a SQL database connection
file_version: 1.1.3
app_version: 1.18.42
---

To set up a connection to the Chess database a connection to the SQL server is needed. To initiate a connection an SQL server must be running on Docker.

If you don't already have an SQL server running on docker you can follow the instructions mentioned here:

*   Install docker on your system

*   Install mySQL server 2022 on your local docker using this command:

```
docker pull mysql:latest
```

*   Run the following command to allow the docker container to run in daemon mode

```
docker run -d --name mysql-server -e MYSQL_ROOT_PASSWORD=your_password -p 3306:3306 -d mysql:latest
```

Ensure that the JDBC driver for MySQL is in the classpath of the application

_JDBC Driver stands for Java Database Connectivity Driver which is a component that enables Java applications to interact with a database using the Java Database Connectivity API. They act as a bridge between the Java application and the database_

The application will then connect to the SQL server using the connection command mentioned in the below snippet.

```
@JvmStatic
fun connection(){
    try {
        val url = "jdbc:mysql://localhost:3306/"
        val user = "root"
        val password = "obscure-star1234"
        Class.forName("com.mysql.cj.jdbc.Driver")
        val c = DriverManager.getConnection(
            url,
            user,
            password
        )
        fancyPrintln("Connected to SQL database!")
    }
    catch (e: SQLException){
        e.printStackTrace()
    }
}
```

If this is the output when you run your app then you have connected to your MySQL server successfully.

<br/>

<div align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2Fee7a29bf-d81e-4c45-b520-a2523cb057b9.png?alt=media&token=39e21128-42b0-4e19-8c54-69bf06c1a87d" style="width:'50%'"/></div>

<br/>

It is essential to connect to the database and run database functions asynchronously to allow the application to continue to function quickly while running other tasks. It enables

*   Improved responsiveness to perform other tasks while executing database functions

*   Scalability by allowing large numbers of concurrent connections or requests to operate and to use system resources efficiently

*   Mitigation of latency

*   Non-blocking I/O

The kotlin coroutines library provides a withContext function that can choose which dispatcher to use to run a specific function. In the code snippet below that snippet that is used is the IO dispatcher. The function defined in the snippet will run the connection() function in the IO thread which is separate from the main thread.

Here is [documentation](https://medium.com/@humzakhalid94/understanding-the-power-of-withcontext-in-coroutines-15155f19518a#:~:text=The%20withContext%20function%20respects%20the,the%20parent%20coroutine%20is%20cancelled.) explaining the use of multiple dispachers

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/SQLConnection.kt
```kotlin
11         @JvmStatic
12         suspend fun connection(): Connection? =
13             withContext(Dispatchers.IO) {
14                 try {
15                     val url = "jdbc:mysql://localhost:3306/Chess_database"
16                     val user = "root"
17                     val password = "obscure-star1234"
18     
19                     fancyPrintln("Connected to SQL database!")
20                     DriverManager.getConnection(
21                         url,
22                         user,
23                         password,
24                     )
25                 } catch (e: SQLException) {
26                     e.printStackTrace()
27                     return@withContext null
28                 }
29             }
30     }
```

<br/>

Now that we have connected to the server. Let's create our database.

To create a new database run the below command:

```
CREATE DATABASE Chess_database;
```

To grant privileges you can run the below command:

```
GRANT ALL PRIVILEGES ON your_database.* TO '<username>' IDENTIFIED BY '<password>';
FLUSH PRIVILEGES;
```

Queries are ran here

If you want to configure a connection to the database you can set up your environment with the settings shown in the below screenshot:

<br/>

<div align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2F4d6fa0cd-81fd-4bd8-a888-112ba3262730.png?alt=media&token=011bbcbc-8f8a-405b-95aa-13b7a3e84484" style="width:'100%'"/></div>

<br/>

To add columns to the empty database this sql query was ran:

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/chess_database_columns.sql
```plsql
2      CREATE TABLE CHESS_DATA (
3                                  ROUND_ID VARCHAR(36) PRIMARY KEY,
4                                  GAME_ID VARCHAR(36),
5                                  ROUND INT,
6                                  BOARD_REPRESENTATION VARCHAR(64),
7                                  PIECE_COUNT JSON,
8                                  LEGAL_MOVES JSON,
9                                  THREATS_AND_ATTACKS JSON,
10                                 PIECE_ACTIVITY JSON,
11                                 KING_SAFETY JSON,
12                                 PAWN_STRUCTURE JSON,
13                                 MATERIAL_BALANCE JSON,
14                                 CENTER_CONTROL JSON,
15                                 PREVIOUS_MOVES JSON,
16                                 WHITE_WINS BOOLEAN,
17                                 BLACK_WINS BOOLEAN
18     );
```

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/wvpiwo7l).
