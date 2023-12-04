/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;
/*    */ 
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
/*    */ 
/*    */ public abstract interface JsonArrayFormatVisitor
/*    */   extends JsonFormatVisitorWithSerializerProvider
/*    */ {
/*    */   public abstract void itemsFormat(JsonFormatVisitable paramJsonFormatVisitable, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract void itemsFormat(JsonFormatTypes paramJsonFormatTypes)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public static class Base
/*    */     implements JsonArrayFormatVisitor
/*    */   {
/*    */     protected SerializerProvider _provider;
/*    */     
/*    */     public Base() {}
/*    */     
/*    */     public Base(SerializerProvider p)
/*    */     {
/* 37 */       this._provider = p;
/*    */     }
/*    */     
/* 40 */     public SerializerProvider getProvider() { return this._provider; }
/*    */     
/*    */     public void setProvider(SerializerProvider p) {
/* 43 */       this._provider = p;
/*    */     }
/*    */     
/*    */     public void itemsFormat(JsonFormatVisitable handler, JavaType elementType)
/*    */       throws JsonMappingException
/*    */     {}
/*    */     
/*    */     public void itemsFormat(JsonFormatTypes format)
/*    */       throws JsonMappingException
/*    */     {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonArrayFormatVisitor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */