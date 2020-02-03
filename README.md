
A. Why Apache Spark and Maven?

Apache Spark is an open source distributed cluster-computing framework for processing big data. It is a unified analytics engine for big data and supports batch and stream data processing, machine learning and distributed graph processing. 

Even though the given csv, xml and json data sets are very small, Apache Spark has been used considering its abilty to process data of any size.

Maven is a widely used build automation tool.

B. Execute in Windows 10 without Setting Up any Apache Spark Cluster:

Set up Hadoop for Windows Binaries:

1. Download the content of the entire folder in your local desktop/laptop from GitHub (Link: https://github.com/steveloughran/winutils/tree/master/hadoop-3.0.0):

Following command can aslo be used to download with git tool:

git clone https://github.com/steveloughran/winutils.git


2. Copy the content in your destination directory. For example: C:\Users\UserX\hadoop-3.0.0

'UserX' needs to be replaced with actual Windows user name.

3. Set up a Windows environment variable for Hadoop:

variable name: HADOOP_HOME

variable value: C:\Users\UserX\hadoop-3.0.0

Add the following entry in Windows system variable path:

%HADOOP_HOME%\bin


Download Source Code:

1. Download the 'datamerging' project in your local desktop/laptop running the following command:

git clone git@github.com:muhammadrezaulkarim/datamerging.git

Assume the project has been downloaded here: 'C:\Users\UserX\datamerging-master'. 'UserX' needs to be replaced with actual Windows user.


Build and Execute the Program:

1. Open a Windows Command Prompt in administrative mode (Run as administrator). Move to the 'com.problemset.rezaul.datamerging' directory with the following command:

cd C:\Users\UserX\datamerging-master\com.problemset.rezaul.datamerging


2. Build the project in the command prompt:

mvn -U clean package

3. Execute the program in the command prompt:

java -jar target/datamerging-1.0.0-SNAPSHOT-jar-with-dependencies.jar local

The argument 'local' is required to run the program in local desktop/laptop without the need for setting up Apache Spark Cluster.

4. The output csv file will be stored in the "data\output.csv" directory with the project. 'service-guid' wise record count will be displayed on the command prompt


C. Execute in Apache Spark Linux Cluster (Optional):

1. Build the project with Maven as described earlier

2. Copy the 'config' and 'data' directories from your project main directory to the 'target' directory

3. Copy the target directory to the master node of the Apache Spark Linux Cluster. Assume copied to the '/tmp' directory in the master node

4. Move to the target directory running the following command:

cd target

5. Submit the program to the Spark cluster with 'spark-submit' running the following command from the '/tmp/target' directory:

/opt/spark/bin/spark-submit --verbose --class solutions.datamerging.DataMergingApp --master spark://spark-master:7077 /tmp/target/datamerging-1.0.0-SNAPSHOT-jar-with-dependencies.jar

You need to replace 'spark://spark-master:7077' with your Apache Spark Cluster Master node hostname. 

You might also have to replace the path for spark-submit command '/opt/spark/bin/spark-submit' depending on where it has been installed in the master node.


D. Execute in Apache Spark on Amazon EMR (Cloud):

If you already have a Apache Spark Cluster in Amazon EMR, you can also use spark-submit to execute the program.


E. Set Up Maven in Windows 10:

1. Download Apache Maven from here:

https://www-eu.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip

2. Unzip and place the 'apache-maven-3.6.3' directory here:

C:\Users\UserX\apache-maven-3.6.3

'UserX' needs to be replaced with actual Windows user name.

3. Set up a Windows environment variable for Maven:

variable name: MAVEN_HOME

variable value: C:\Users\UserX\apache-maven-3.6.3

Add the following entry in Windows system variable path:

%MAVEN_HOME%\bin
