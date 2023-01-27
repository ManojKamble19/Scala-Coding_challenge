package pack
import org.apache.spark._
import scala.io.Source._
import org.apache.spark.sql.{Dataset, SparkSession}
import scala.io.Source
import org.apache.spark.SparkFiles
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession

import java.net.URL
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.Window.orderBy
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.window

object obj2 {


	def main(args:Array[String]):Unit={


			val conf = new SparkConf().setMaster("local[*]").setAppName("first")

					val sc = new SparkContext(conf)
					sc.setLogLevel("ERROR")

					val spark = SparkSession.builder.getOrCreate()
					import spark.implicits._

					//Part 1 -
					//Task 1

					val url = "https://raw.githubusercontent.com/stedy/Machine-Learning-with-R-datasets/master/groceries.csv"
					sc.addFile(url)
					val dataRDD = sc.textFile(SparkFiles.get("groceries.csv"))
					val data1 = dataRDD.map(x => x.split(","))
					val data = data1.take(5)
					//data.foreach(x=>println(x.mkString(",")))


					//Task 2 (a)
					val distinctRDD = dataRDD.flatMap(x => x.split(","))
					val distinctRDD1 = distinctRDD.distinct
					//distinctRDD1.coalesce(1).saveAsTextFile("File:///D://out/out_1_2a.txt")

					// (b)
					val productRDD = distinctRDD.count()
					//sc.parallelize(Seq(productRDD)).coalesce(1).saveAsTextFile("File:///D://out/out_1_2b.txt")



					//Part2
					//Task 1

					val src_data = spark.read.load("file:///D://sf-airbnb-clean.parquet")
					//src_data.show(5)


					//Task 2
					src_data.agg(min($"price") as "min_price", max($"price") as "max_price", count($"price") as "row_count").write.mode("overwrite").option("header","true").csv("File:///D://out/out_2_2.txt")

					//task 3

					src_data.filter(col("price")> "5000").filter(col("review_scores_value")==="10").agg(avg($"bathrooms") as "avg_bathrooms", avg($"bedrooms")as "avg_bedrooms" )
					.write.mode("overwrite").option("header", "true").csv("file:///D://out/out_2_3.txt")
					
					
					//Task 4
					
					val data2 = src_data.withColumn("min_price",min("price").over(orderBy("price")))
					.withColumn("high_rating",max("reviews_scores_rating").over(Window.orderBy(desc("reviews_scores_rating"))))
					
					data2.filter("price=min_price AND reviews_scores_rating=high_rating").select("accomodates").write.mode("overwrite").option("header","true").csv("file:///D://out/out_2_4.txt")




	}

}
