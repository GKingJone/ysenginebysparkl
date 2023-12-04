/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class PeriodKeyDeserializer extends JodaKeyDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Period deserialize(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 13 */     return Period.parse(key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\PeriodKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */