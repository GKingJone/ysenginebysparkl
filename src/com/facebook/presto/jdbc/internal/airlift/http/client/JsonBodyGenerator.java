/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public class JsonBodyGenerator<T>
/*    */   extends StaticBodyGenerator
/*    */ {
/*    */   public static <T> JsonBodyGenerator<T> jsonBodyGenerator(JsonCodec<T> jsonCodec, T instance)
/*    */   {
/* 26 */     return new JsonBodyGenerator(jsonCodec, instance);
/*    */   }
/*    */   
/*    */   private JsonBodyGenerator(JsonCodec<T> jsonCodec, T instance)
/*    */   {
/* 31 */     super(jsonCodec.toJsonBytes(instance));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\JsonBodyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */