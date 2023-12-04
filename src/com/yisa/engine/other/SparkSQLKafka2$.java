/*    */ package com.yisa.engine.other;
/*    */ 
/*    */ import java.util.Date;
/*    */ import java.util.Properties;
/*    */ import org.apache.spark.SparkConf;
/*    */ import org.apache.spark.SparkContext;
/*    */ import org.apache.spark.sql.Dataset;
/*    */ import org.apache.spark.sql.SQLContext;
/*    */ 
/*    */ public final class SparkSQLKafka2$
/*    */ {
/*    */   public static final  MODULE$;
/*    */   
/*    */   static
/*    */   {
/*    */     new ();
/*    */   }
/*    */   
/*    */   public void main(String[] args)
/*    */   {
/* 21 */     String hdfsPath = "hdfs://gpu10:8020";
/* 22 */     String kafka = "gpu3:9092";
/* 23 */     String topic = "test";
/* 24 */     String kafkagroupid = "test";
/* 25 */     String jdbcurl = "jdbc:mysql://bigdata1:3306/test";
/* 26 */     String jdbcTable = "test";
/* 27 */     String jdbcUser = "root";
/* 28 */     String jdbcPwd = "root";
/*    */     
/*    */ 
/* 31 */     Properties connectionProperties = new Properties();
/* 32 */     connectionProperties.put("user", jdbcUser);
/* 33 */     connectionProperties.put("password", jdbcPwd);
/*    */     
/* 35 */     SparkConf sprakConf = new SparkConf().setAppName("test").setMaster("local");
/* 36 */     SparkContext sc = new SparkContext(sprakConf);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 43 */     SQLContext sqlContext = new SQLContext(sc);
/*    */     
/* 45 */     long now = new Date().getTime();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 52 */     Dataset usersDf = sqlContext.read().json("E://moma//test.json");
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 57 */     usersDf.write().jdbc(jdbcurl, jdbcTable, connectionProperties);
/*    */   }
/*    */   
/* 60 */   private SparkSQLKafka2$() { MODULE$ = this; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\other\SparkSQLKafka2$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */