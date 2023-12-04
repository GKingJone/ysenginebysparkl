/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.MonthDay;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonthDayDeserializer
/*    */   extends JodaDeserializerBase<MonthDay>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public MonthDayDeserializer()
/*    */   {
/* 22 */     super(MonthDay.class);
/*    */   }
/*    */   
/*    */   public MonthDay deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 28 */     JsonToken t = jp.getCurrentToken();
/* 29 */     if (t == JsonToken.VALUE_STRING)
/*    */     {
/* 31 */       String str = jp.getText().trim();
/* 32 */       if (str.isEmpty()) {
/* 33 */         return null;
/*    */       }
/* 35 */       return MonthDay.parse(str);
/*    */     }
/* 37 */     throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "expected JSON String");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\MonthDayDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */