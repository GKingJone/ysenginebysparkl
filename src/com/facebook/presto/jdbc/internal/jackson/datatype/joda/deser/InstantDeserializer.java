/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Instant;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InstantDeserializer
/*    */   extends JodaDeserializerBase<Instant>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public InstantDeserializer()
/*    */   {
/* 22 */     super(Instant.class);
/*    */   }
/*    */   
/*    */   public Instant deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 28 */     JsonToken t = p.getCurrentToken();
/* 29 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/* 30 */       return new Instant(p.getLongValue());
/*    */     }
/* 32 */     if (t == JsonToken.VALUE_STRING) {
/* 33 */       String str = p.getText().trim();
/* 34 */       if (str.length() == 0) {
/* 35 */         return null;
/*    */       }
/* 37 */       return new Instant(str);
/*    */     }
/* 39 */     return (Instant)ctxt.handleUnexpectedToken(handledType(), p);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\InstantDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */