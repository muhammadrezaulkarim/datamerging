
A. Why Apache Spark and Maven?

Apache Spark is an open source distributed cluster-computing framework for processing big data. It is a unified analytics engine for big data and supports batch and stream data processing, machine learning and distributed graph processing. 

Even though the given csv, xml and json data sets are very small, Apache Spark has been used considering its abilty to process data of any size.

Maven is a widely used build automation tool.

B. Execute in Windows 10 without Setting Up any Apache Spark Cluster:

Set Up Windows Binaries for Hadoop:

1. Download the content of the entire folder in your local desktop/laptop from GitHub (Link: https://github.com/steveloughran/winutils/tree/master/hadoop-3.0.0):

Following command can also be used instead to download with the git tool:

git clone https://github.com/steveloughran/winutils.git


2. Copy the content in your destination directory. For example: C:\Users\UserX\hadoop-3.0.0

'UserX' needs to be replaced with actual Windows user name.

3. Set up a Windows environment variable for Hadoop:

variable name: HADOOP_HOME

variable value: C:\Users\UserX\hadoop-3.0.0

Add the following entry in the Windows system variable path:

%HADOOP_HOME%\bin


Download Source Code:

1. Download the 'datamerging' project in your local desktop/laptop running the following command:

git clone git@github.com:muhammadrezaulkarim/datamerging.git

Assume the project has been downloaded here: 'C:\Users\UserX\datamerging-master'. 'UserX' needs to be replaced with actual Windows user name.


Build and Execute the Program:

1. Open a Windows Command Prompt in administrative mode (Run as administrator). Move to the 'com.problemset.rezaul.datamerging' directory with the following command:

cd C:\Users\UserX\datamerging-master\com.problemset.rezaul.datamerging


2. Build the project in the command prompt:

mvn -U clean package

3. Execute the program in the command prompt:

java -jar target/datamerging-1.0.0-SNAPSHOT-jar-with-dependencies.jar local

The argument 'local' is required to run the program in local desktop/laptop without the need for setting up Apache Spark Cluster. Java 8 is a pre-requisite for this project. Java can be downloaded from this Link: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

4. The output csv file will be stored in the "data\output.csv" directory within the project. 'service-guid' wise record count will be displayed on the command prompt


C. Execute in Apache Spark Linux Cluster (Optional):

1. Build the project with Maven as described earlier

2. Copy the 'config' and 'data' directories from your project main directory to the 'target' directory

3. Copy the 'target' directory to the master node of the Apache Spark Linux Cluster. Assume it has been copied to the '/tmp' directory in the master node

4. Move to the 'target' directory running the following command:

cd target

5. Submit the program to the Spark cluster with 'spark-submit' running the following command from the '/tmp/target' directory:

/opt/spark/bin/spark-submit --verbose --class solutions.datamerging.DataMergingApp --master spark://spark-master:7077 /tmp/target/datamerging-1.0.0-SNAPSHOT-jar-with-dependencies.jar

You need to replace 'spark://spark-master:7077' with your Apache Spark Cluster Master node hostname. 

You might also have to replace the path for spark-submit command '/opt/spark/bin/spark-submit' depending on where it has been installed in the master node.


D. Execute in Apache Spark Cluster on Amazon EMR (Cloud):

If you already have a Apache Spark Cluster in Amazon EMR, you can also use 'spark-submit' to execute the program.


E. Set Up Maven in Windows 10:

1. Download Apache Maven from the following link:

https://www-eu.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip

2. Unzip and place the the files a directory like this:

C:\Users\UserX\apache-maven-3.6.3

'UserX' needs to be replaced with actual Windows user name.

3. Set up a Windows environment variable for Maven:

variable name: MAVEN_HOME

variable value: C:\Users\UserX\apache-maven-3.6.3

Add the following entry in Windows system variable path:

%MAVEN_HOME%\bin


F. Sample Output:

1. service-guid wise record count:   

| service-guid | count |

| 0342f6df-6e8c-48c3-8390-667eb678638b | 12 |

| 0a5f0733-d058-4738-8666-f0468c5ee9c3 | 20 |

| 0d5157f7-8012-4455-b66f-4f81d14092b3 | 18 |

| 0ee908d7-d767-4da5-9cbe-fc78e9c6a192 | 10 |

| 147fe620-dd62-4eb6-9a0d-8b8ca1e743b1 | 20 |

Only 5 rows displayed here as sample. The program will output record count for all service-guid on the command line.

2. Output CSV file:
https://github.com/muhammadrezaulkarim/datamerging/blob/master/com.problemset.rezaul.datamerging/data/output.csv/part-00000-9f4adc11-1ecf-4bb3-8c19-95fd0cafc155-c000.csv
