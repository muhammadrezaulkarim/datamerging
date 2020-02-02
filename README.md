Execute in Windows 10:

Set up Hadoop for Windows:

1. Download the content of the entire folder in your local desktop/laptop

https://github.com/steveloughran/winutils/tree/master/hadoop-3.0.0

2. Copy the content in your destination directory. For example: C:\Users\UserX\hadoop-3.0.0

3. Set up Windows environment variables for Hadoop:

HADOOP_HOME: C:\Users\UserX\hadoop-3.0.0

Add the following entry in Windows environment variable path:

%HADOOP_HOME%\bin


1. Download the datamerging project in your local desktop/laptop:

git clone git@github.com:muhammadrezaulkarim/datamerging.git

Assume the project has been downloaded here: C:\Users\UserX\datamerging-master


2. Open a Windows Command Prompt in administrative mode (Run as administrator). Move to the com.problemset.rezaul.datamerging directory. 

cd C:\Users\UserX\datamerging-master\com.problemset.rezaul.datamerging


3. Execute the following commands in commadn prompt in order:

mvn -U clean package

java -jar target/datamerging-1.0.0-SNAPSHOT-jar-with-dependencies.jar local

'local' is required to run in local desktop/laoptop without the need for setting up Apache Spark Cluster.
