/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ 
/*    */ public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AtomicBooleanDeserializer()
/*    */   {
/* 13 */     super(AtomicBoolean.class);
/*    */   }
/*    */   
/*    */   public AtomicBoolean deserialize(JsonParser jp, DeserializationContext ctxt) throws java.io.IOException {
/* 17 */     return new AtomicBoolean(_parseBooleanPrimitive(jp, ctxt));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\AtomicBooleanDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */