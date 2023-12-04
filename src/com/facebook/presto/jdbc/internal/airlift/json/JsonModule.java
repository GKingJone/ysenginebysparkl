/*    */ package com.facebook.presto.jdbc.internal.airlift.json;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper;
/*    */ import com.google.inject.Binder;
/*    */ import com.google.inject.Module;
/*    */ import com.google.inject.Scopes;
/*    */ import com.google.inject.binder.AnnotatedBindingBuilder;
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
/*    */ public class JsonModule
/*    */   implements Module
/*    */ {
/*    */   public void configure(Binder binder)
/*    */   {
/* 28 */     binder.disableCircularProxies();
/*    */     
/*    */ 
/*    */ 
/* 32 */     binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class);
/*    */     
/* 34 */     binder.bind(JsonCodecFactory.class).in(Scopes.SINGLETON);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */