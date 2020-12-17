import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql._
import org.apache.spark.ml.clustering.KMeans
import org.apache.log4j._
import org.apache.spark.ml.classification.{DecisionTreeClassifier,DecisionTreeClassificationModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator

object CensusIncome2{

  case class myschema(age:Integer,
                      workclass:Integer,
                      fnlwgt:Integer,
                      education:Integer,
                      educationnum: Integer,
                      maritalstatus:Integer,
                      occupation:Integer,
                      relationship:Integer,
                      race:Integer,
                      sex:Integer,
                      capitalgain:Integer,
                      capitalloss:Integer,
                      hourperweek:Integer,
                      country:Integer,
                      target:Integer)

  def main (args: Array[String]){
    //Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession.builder.appName("CensusIncome").master("local[*]").getOrCreate()

    import spark.implicits._
    val train=spark.read
      .option("sep",",")
      .option("header","true")
      .option("inferSchema","true")
      .csv("data/train.csv")
      .as[myschema]

    val test=spark.read
      .option("sep",",")
      .option("header","true")
      .option("inferSchema","true")
      .csv("data/test.csv")
      .as[myschema]

    val cols = Array("age","workclass","fnlwgt","education","educationnum","maritalstatus","occupation","relationship","race",
    "sex","capitalgain","capitalloss","hourperweek","country")

    val assembler = new VectorAssembler()
      .setInputCols(cols)
      .setOutputCol("features")
    val featureTrain = assembler.transform(train)
    val featureTest = assembler.transform(test)

    val indexer = new StringIndexer()
      .setInputCol("target")
      .setOutputCol("label")
    val labelTrain = indexer.fit(featureTrain).transform(featureTrain)
    val labelTest = indexer.fit(featureTest).transform(featureTest)

    val seed = 5043

    val DecisionTreeClassifier = new DecisionTreeClassifier()
    val DecisionTreeClassificationModel = DecisionTreeClassifier.fit(labelTrain)

    val predictions = DecisionTreeClassificationModel.transform(labelTest)
    predictions.show(10)

    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setRawPredictionCol("prediction")
      .setMetricName("areaUnderROC")

    val accuracy = evaluator.evaluate(predictions)
    println(accuracy)




    spark.stop()

  }


}
