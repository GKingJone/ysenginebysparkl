/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoClassDefFoundDeserializer<T>
/*    */   extends JsonDeserializer<T>
/*    */ {
/*    */   private final NoClassDefFoundError _cause;
/*    */   
/*    */   public NoClassDefFoundDeserializer(NoClassDefFoundError cause)
/*    */   {
/* 22 */     this._cause = cause;
/*    */   }
/*    */   
/*    */   public T deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 28 */     throw this._cause;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\NoClassDefFoundDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */