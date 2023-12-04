/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.YearMonth;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class YearMonthDeserializer
/*    */   extends JodaDeserializerBase<YearMonth>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public YearMonthDeserializer()
/*    */   {
/* 22 */     super(YearMonth.class);
/*    */   }
/*    */   
/*    */   public YearMonth deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 28 */     JsonToken t = jp.getCurrentToken();
/* 29 */     if (t == JsonToken.VALUE_STRING) {
/* 30 */       String str = jp.getText().trim();
/* 31 */       if (str.isEmpty()) {
/* 32 */         return null;
/*    */       }
/* 34 */       return YearMonth.parse(str);
/*    */     }
/* 36 */     throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "expected JSON String");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\YearMonthDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */