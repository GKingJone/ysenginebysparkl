/*     */ package com.yisa.engine.sparksql;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.Properties;
/*     */ import org.apache.spark.sql.Dataset;
/*     */ import org.apache.spark.sql.Row;
/*     */ import scala.Predef.;
/*     */ import scala.collection.mutable.StringBuilder;
/*     */ import scala.reflect.api.Internals.InternalApi;
/*     */ import scala.reflect.api.Internals.ReificationSupportApi;
/*     */ import scala.reflect.api.Mirror;
/*     */ import scala.reflect.api.Symbols.ModuleSymbolApi;
/*     */ import scala.reflect.api.Universe;
/*     */ import scala.reflect.runtime.package.;
/*     */ 
/*     */ public final class SparkSQLKafka$
/*     */ {
/*     */   public static final  MODULE$;
/*     */   
/*     */   static
/*     */   {
/*     */     new ();
/*     */   }
/*     */   
/*     */   public void main(String[] args)
/*     */   {
/*  27 */     String hdfsPath = "hdfs://gpu10:8020";
/*  28 */     String kafka = "gpu3:9092";
/*  29 */     String topic = "test";
/*  30 */     String kafkagroupid = "test";
/*  31 */     String jdbcurl = "jdbc:mysql://128.127.120.200:3306/wifi_pass?useUnicode=true&characterEncoding=UTF-8";
/*  32 */     String jdbcTable = "SearchCar";
/*  33 */     String jdbcUser = "root";
/*  34 */     String jdbcPwd = "root";
/*     */     
/*     */ 
/*  37 */     Properties connectionProperties = new Properties();
/*  38 */     connectionProperties.put("user", jdbcUser);
/*  39 */     connectionProperties.put("password", jdbcPwd);
/*     */     
/*     */ 
/*  42 */     org.apache.spark.SparkConf sprakConf = new org.apache.spark.SparkConf().setAppName("test");
/*  43 */     org.apache.spark.SparkContext sc = new org.apache.spark.SparkContext(sprakConf);
/*     */     
/*  45 */     sc.getConf().set("spark.executor.extraClassPath", "/moma/mysql-connector-java-5.1.39.jar");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  52 */     org.apache.spark.sql.SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  57 */     Dataset inittable = sqlContext.read().parquet(new StringBuilder().append(hdfsPath).append("/moma/pass_info0824800wan").toString());
/*     */     
/*     */ 
/*  60 */     inittable.createOrReplaceTempView("passinfo");
/*     */     
/*  62 */     inittable.cache();
/*     */     
/*     */ 
/*  65 */     scala.reflect.api.JavaUniverse $u = ;;;; {
/*  66 */       public final long apply(String text, final String test2) { final byte[] byteData = java.util.Base64.getDecoder().decode(text);
/*  67 */         final byte[] oldData = java.util.Base64.getDecoder().decode(test2);
/*  68 */         final scala.runtime.LongRef num = scala.runtime.LongRef.create(0L);scala.runtime.RichInt..MODULE$
/*  69 */           .until$extension0(Predef..MODULE$.intWrapper(0), byteData.length).withFilter(new scala.runtime.AbstractFunction1.mcZI.sp() { public boolean apply$mcZI$sp(int i) { return test2.length() > 30; } public final boolean apply(int i) { return apply$mcZI$sp(i); } }).foreach(new scala.runtime.AbstractFunction1.mcVI.sp() { public final void apply(int i) { apply$mcVI$sp(i); }
/*  70 */             public void apply$mcVI$sp(int i) { int n = (byteData[i] & 0xFF) - (oldData[i] & 0xFF);
/*  71 */               num.elem += n * n;
/*  72 */             } });
/*  73 */         return num.elem; } }, ((scala.reflect.api.TypeTags)package..MODULE$.universe()).TypeTag().Long(), ((scala.reflect.api.TypeTags)$u).TypeTag().apply((Mirror)$m, new scala.reflect.api.TypeCreator()
/*     */     {
/*     */       public static final long serialVersionUID = 0L;
/*     */       public static final long serialVersionUID = 0L;
/*     */       public static final long serialVersionUID = 0L;
/*     */       public <U extends Universe, > scala.reflect.api.Types.TypeApi apply(Mirror<U> $m$untyped)
/*     */       {
/*  65 */         Universe $u = $m$untyped.universe();Mirror $m = $m$untyped;return $u.internal().reificationSupport().TypeRef($u.internal().reificationSupport().SingleType($u.internal().reificationSupport().ThisType($m.staticPackage("scala").asModule().moduleClass()), $m.staticModule("scala.Predef")), $u.internal().reificationSupport().selectType($m.staticModule("scala.Predef").asModule().moduleClass(), "String"), scala.collection.immutable.Nil..MODULE$); } }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */       ), ((scala.reflect.api.TypeTags)$u).TypeTag().apply((Mirror)$m, new scala.reflect.api.TypeCreator() { public <U extends Universe, > scala.reflect.api.Types.TypeApi apply(Mirror<U> $m$untyped) { Universe $u = $m$untyped.universe();Mirror $m = $m$untyped;return $u.internal().reificationSupport().TypeRef($u.internal().reificationSupport().SingleType($u.internal().reificationSupport().ThisType($m.staticPackage("scala").asModule().moduleClass()), $m.staticModule("scala.Predef")), $u.internal().reificationSupport().selectType($m.staticModule("scala.Predef").asModule().moduleClass(), "String"), scala.collection.immutable.Nil..MODULE$);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */       }));
/*  77 */     Properties props = new Properties();
/*  78 */     props.put("bootstrap.servers", kafka);
/*  79 */     props.put("group.id", kafkagroupid);
/*  80 */     props.put("enable.auto.commit", "true");
/*  81 */     props.put("auto.commit.interval.ms", "1000");
/*  82 */     props.put("session.timeout.ms", "30000");
/*  83 */     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
/*  84 */     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
/*  85 */     org.apache.kafka.clients.consumer.KafkaConsumer consumer = new org.apache.kafka.clients.consumer.KafkaConsumer(props);
/*     */     
/*  87 */     java.util.ArrayList topics = new java.util.ArrayList();
/*  88 */     topics.add(topic);
/*  89 */     consumer.subscribe(topics);
/*     */     for (;;)
/*     */     {
/*  92 */       org.apache.kafka.clients.consumer.ConsumerRecords records = consumer.poll(100L);
/*     */       
/*     */ 
/*  95 */       java.util.Iterator rei = records.iterator();
/*     */       
/*  97 */       while (rei.hasNext()) {
/*  98 */         long now3 = new Date().getTime();
/*  99 */         org.apache.kafka.clients.consumer.ConsumerRecord record = (org.apache.kafka.clients.consumer.ConsumerRecord)rei.next();
/*     */         
/* 101 */         Predef..MODULE$.println(new StringBuilder().append("line:").append(record.value()).toString());
/* 102 */         String line = record.value().toString();
/* 103 */         String[] line_arr = line.split(",");
/* 104 */         String feature = line_arr[0];
/* 105 */         String modelId = line_arr[1];
/* 106 */         String brandId = line_arr[2];
/*     */         
/* 108 */         Dataset teenagers = sqlContext.sql(new StringBuilder().append("SELECT id  FROM passinfo where  recFeature != '' and recFeature != 'null' and brandId = ").append(brandId).append("  and modelId  = ").append(modelId).append(" and recFeature ='").append(feature).append("'").toString());
/*     */         
/* 110 */         teenagers.foreach(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */           
/* 112 */           public final void apply(Row t) { Predef..MODULE$.println(new StringBuilder().append("id: ").append(t.apply(0)).toString()); }
/* 113 */         });
/* 114 */         long now4 = new Date().getTime();
/* 115 */         teenagers.write().mode(org.apache.spark.sql.SaveMode.Append).jdbc(jdbcurl, jdbcTable, connectionProperties);
/* 116 */         long now5 = new Date().getTime();
/* 117 */         Dataset teenagers2 = sqlContext.sql(new StringBuilder().append("SELECT id,getSimilarity('").append(feature).append("',recFeature) as similarity FROM passinfo where  recFeature != '' and recFeature != 'null' and brandId = ").append(brandId).append("  and modelId  = ").append(modelId).append(" order by similarity  limit 100").toString());
/* 118 */         teenagers2.foreach(new scala.runtime.AbstractFunction1() { public static final long serialVersionUID = 0L;
/*     */           
/* 120 */           public final void apply(Row t) { Predef..MODULE$.println(new StringBuilder().append("id: ").append(t.apply(0)).append(" similarity:").append(t.apply(1)).toString());
/* 121 */             long now = new Date().getTime();
/* 122 */             Predef..MODULE$.println(scala.runtime.BoxesRunTime.boxToLong(now - this.now4$1));
/* 123 */           } });
/* 124 */         long now6 = new Date().getTime();
/* 125 */         teenagers2.write().mode(org.apache.spark.sql.SaveMode.Append).jdbc(jdbcurl, jdbcTable, connectionProperties);
/*     */         
/* 127 */         long now7 = new Date().getTime();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 132 */         Dataset teenagers3 = sqlContext.sql(new StringBuilder().append("SELECT id,getSimilarity('").append(feature).append("',recFeature) as similarity FROM passinfo where  recFeature != '' and recFeature != 'null' and brandId = ").append(brandId).append("  and modelId  = ").append(modelId).append(" order by similarity  limit 10000").toString());
/* 133 */         teenagers3.write().mode(org.apache.spark.sql.SaveMode.Append).jdbc(jdbcurl, jdbcTable, connectionProperties);
/* 134 */         long now8 = new Date().getTime();
/*     */         
/*     */ 
/* 137 */         Predef..MODULE$.println(new StringBuilder().append("first:").append(scala.runtime.BoxesRunTime.boxToLong(now4 - now3)).toString());
/* 138 */         Predef..MODULE$.println(new StringBuilder().append("first to mysql:").append(scala.runtime.BoxesRunTime.boxToLong(now5 - now4)).toString());
/* 139 */         Predef..MODULE$.println(new StringBuilder().append("second:").append(scala.runtime.BoxesRunTime.boxToLong(now6 - now5)).toString());
/* 140 */         Predef..MODULE$.println(new StringBuilder().append("second to mysql :").append(scala.runtime.BoxesRunTime.boxToLong(now7 - now6)).toString());
/* 141 */         Predef..MODULE$.println(new StringBuilder().append("10 thousand to mysql  :").append(scala.runtime.BoxesRunTime.boxToLong(now8 - now7)).toString());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final long now4$1;
/*     */   private SparkSQLKafka$() {
/* 148 */     MODULE$ = this;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\sparksql\SparkSQLKafka$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */