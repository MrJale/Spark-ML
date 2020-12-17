#!/bin/bash
source env.sh
mvn clean package
/usr/local/hadoop/bin/hdfs dfs -rm -r /kmeans/input/
/usr/local/hadoop/bin/hdfs dfs -mkdir -p /kmeans/input/
/usr/local/hadoop/bin/hdfs dfs -copyFromLocal ./train.csv /kmeans/input/
/usr/local/hadoop/bin/hdfs dfs -copyFromLocal ./test.csv /kmeans/input/
/usr/local/spark/bin/spark-submit --class CensusIncome --master=spark://$SPARK_MASTER:7077 target/CensusIncome-1.0-SNAPSHOT.jar hdfs://$SPARK_MASTER:9000/kmeans/input/ 
mvn clean
