/*    */ package com.yisa.engine.other;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ public final class KafkaTest$
/*    */ {
/*    */   public static final  MODULE$;
/*    */   
/*    */   static
/*    */   {
/*    */     new ();
/*    */   }
/*    */   
/*    */   public void main(String[] args) {
/* 15 */     String kafka = "bigdata1:9092";
/* 16 */     String topic = "test";
/* 17 */     String kafkagroupid = "test12";
/*    */     
/*    */ 
/* 20 */     Properties props = new Properties();
/* 21 */     props.put("bootstrap.servers", kafka);
/* 22 */     props.put("group.id", kafkagroupid);
/* 23 */     props.put("enable.auto.commit", "true");
/*    */     
/* 25 */     props.put("auto.offset.reset", "latest");
/* 26 */     props.put("session.timeout.ms", "30000");
/* 27 */     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
/* 28 */     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
/* 29 */     org.apache.kafka.clients.consumer.KafkaConsumer consumer = new org.apache.kafka.clients.consumer.KafkaConsumer(props);
/*    */     
/* 31 */     java.util.ArrayList topics = new java.util.ArrayList();
/* 32 */     topics.add(topic);
/* 33 */     consumer.subscribe(topics);
/*    */     for (;;)
/*    */     {
/* 36 */       org.apache.kafka.clients.consumer.ConsumerRecords records = consumer.poll(100L);
/*    */       
/*    */ 
/* 39 */       java.util.Iterator rei = records.iterator();
/*    */       
/* 41 */       while (rei.hasNext()) {
/* 42 */         long now3 = new java.util.Date().getTime();
/* 43 */         org.apache.kafka.clients.consumer.ConsumerRecord record = (org.apache.kafka.clients.consumer.ConsumerRecord)rei.next();
/*    */         
/* 45 */         scala.Predef..MODULE$.println(new scala.collection.mutable.StringBuilder().append("line:").append(record.value()).toString());
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   private KafkaTest$()
/*    */   {
/* 53 */     MODULE$ = this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\other\KafkaTest$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */