#!/bin/bash
source env.sh
mvn clean package
/usr/local/hadoop/bin/hdfs dfs -rm -r /user/root/data/mllib/
/usr/local/hadoop/bin/hdfs dfs -mkdir -p /user/root/data/mllib/
/usr/local/hadoop/bin/hdfs dfs -copyFromLocal ./train.csv /user/root/data/mllib/
/usr/local/hadoop/bin/hdfs dfs -copyFromLocal ./test.csv /user/root/data/mllib/
/usr/local/spark/bin/spark-submit --class CensusIncome --master=spark://$SPARK_MASTER:7077 target/LogisticRegressionWithElasticNetExample-1.0-SNAPSHOT.jar hdfs://$SPARK_MASTER:9000/user/root/data/mllib/train.csv hdfs://$SPARK_MASTER:9000/user/root/data/mllib/test.csv
mvn clean


