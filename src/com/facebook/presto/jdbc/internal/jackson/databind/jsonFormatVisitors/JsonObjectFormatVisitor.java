/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
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
/*    */ public abstract interface JsonObjectFormatVisitor
/*    */   extends JsonFormatVisitorWithSerializerProvider
/*    */ {
/*    */   public abstract void property(BeanProperty paramBeanProperty)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract void property(String paramString, JsonFormatVisitable paramJsonFormatVisitable, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract void optionalProperty(BeanProperty paramBeanProperty)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract void optionalProperty(String paramString, JsonFormatVisitable paramJsonFormatVisitable, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public static class Base
/*    */     implements JsonObjectFormatVisitor
/*    */   {
/*    */     protected SerializerProvider _provider;
/*    */     
/*    */     public Base() {}
/*    */     
/*    */     public Base(SerializerProvider p)
/*    */     {
/* 43 */       this._provider = p;
/*    */     }
/*    */     
/* 46 */     public SerializerProvider getProvider() { return this._provider; }
/*    */     
/*    */     public void setProvider(SerializerProvider p) {
/* 49 */       this._provider = p;
/*    */     }
/*    */     
/*    */     public void property(BeanProperty prop)
/*    */       throws JsonMappingException
/*    */     {}
/*    */     
/*    */     public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint)
/*    */       throws JsonMappingException
/*    */     {}
/*    */     
/*    */     public void optionalProperty(BeanProperty prop)
/*    */       throws JsonMappingException
/*    */     {}
/*    */     
/*    */     public void optionalProperty(String name, JsonFormatVisitable handler, JavaType propertyTypeHint)
/*    */       throws JsonMappingException
/*    */     {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonObjectFormatVisitor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */