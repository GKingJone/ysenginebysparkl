/*    */ package com.yisa.engine.other;
/*    */ 
/*    */ import org.apache.spark.SparkConf;
/*    */ 
/*    */ public final class Test1$ {
/*    */   public static final  MODULE$;
/*    */   
/*    */   static {
/*    */     new ();
/*    */   }
/*    */   
/*    */   public void tet1() {
/* 13 */     SparkConf sprakConf = new SparkConf().setAppName("test").setMaster("local").set("spark.sql.warehouse.dir", "file:///D:/spark-warehouse");
/* 14 */     org.apache.spark.SparkContext sc = new org.apache.spark.SparkContext(sprakConf);
/* 15 */     org.apache.spark.sql.SparkSession sparkSession = org.apache.spark.sql.SparkSession..MODULE$
/* 16 */       .builder()
/* 17 */       .appName("Spark SQL Example")
/* 18 */       .getOrCreate();
/* 19 */     org.apache.spark.sql.SparkSession sqlContext = sparkSession;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 24 */     org.apache.spark.sql.Dataset inittable = sqlContext.read().parquet("hdfs://cdh2:8020/user/hive/warehouse/people_test1");
/*    */     
/* 26 */     inittable.createOrReplaceTempView("perople_test1");
/*    */     
/* 28 */     org.apache.spark.sql.Dataset resultData = sqlContext.sql("select name from perople_test1");
/* 29 */     resultData.show();
/*    */     
/* 31 */     java.util.concurrent.TimeUnit.SECONDS.sleep(30L);
/*    */     
/* 33 */     inittable = sqlContext.read().parquet("hdfs://cdh2:8020/user/hive/warehouse/people_test1");
/* 34 */     inittable.createOrReplaceTempView("perople_test1");
/*    */     
/* 36 */     org.apache.spark.sql.Dataset resultData2 = sqlContext.sql("select name from perople_test1");
/* 37 */     resultData2.show();
/*    */   }
/*    */   
/*    */   public void tet2()
/*    */   {
/* 42 */     SparkConf sprakConf = new SparkConf().setAppName("test").setMaster("local").set("spark.sql.warehouse.dir", "file:///D:/spark-warehouse");
/* 43 */     org.apache.spark.SparkContext sc = new org.apache.spark.SparkContext(sprakConf);
/* 44 */     org.apache.spark.sql.SparkSession sparkSession = org.apache.spark.sql.SparkSession..MODULE$
/* 45 */       .builder()
/* 46 */       .appName("Spark SQL Example")
/* 47 */       .getOrCreate();
/* 48 */     org.apache.spark.sql.SparkSession sqlContext = sparkSession;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 54 */     org.apache.spark.sql.Dataset inittable = sqlContext.read().parquet("hdfs://cdh2:8020/moma/test1/");
/*    */     
/* 56 */     inittable.createOrReplaceTempView("people_test2");
/* 57 */     inittable.printSchema();
/*    */     
/* 59 */     org.apache.spark.sql.Dataset resultData = sqlContext.sql("select * from people_test2 limit 1 ");
/* 60 */     resultData.show();
/*    */   }
/*    */   
/*    */   public void tet3() {
/* 64 */     SparkConf sprakConf = new SparkConf().setAppName("test").setMaster("spark://cdh1:7077").set("spark.sql.warehouse.dir", "file:///D:/spark-warehouse");
/* 65 */     org.apache.spark.SparkContext sc = new org.apache.spark.SparkContext(sprakConf);
/* 66 */     String warehouseLocation = "hdfs://cdh2:8020/user/hive/warehouse";
/*    */     
/* 68 */     org.apache.spark.sql.SparkSession spark = org.apache.spark.sql.SparkSession..MODULE$
/* 69 */       .builder()
/* 70 */       .appName("Spark Hive Example")
/* 71 */       .config("spark.sql.warehouse.dir", warehouseLocation)
/* 72 */       .enableHiveSupport()
/* 73 */       .getOrCreate();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 79 */     spark.sql("SELECT * FROM people_test2").show();
/*    */   }
/*    */   
/*    */   public void main(String[] args)
/*    */   {
/* 84 */     tet2();
/*    */   }
/*    */   
/* 87 */   private Test1$() { MODULE$ = this; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\other\Test1$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */