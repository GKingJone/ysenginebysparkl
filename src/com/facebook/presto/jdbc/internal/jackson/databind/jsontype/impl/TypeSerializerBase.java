/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ 
/*    */ public abstract class TypeSerializerBase
/*    */   extends TypeSerializer
/*    */ {
/*    */   protected final TypeIdResolver _idResolver;
/*    */   protected final BeanProperty _property;
/*    */   
/*    */   protected TypeSerializerBase(TypeIdResolver idRes, BeanProperty property)
/*    */   {
/* 16 */     this._idResolver = idRes;
/* 17 */     this._property = property;
/*    */   }
/*    */   
/*    */   public abstract JsonTypeInfo.As getTypeInclusion();
/*    */   
/*    */   public String getPropertyName()
/*    */   {
/* 24 */     return null;
/*    */   }
/*    */   
/* 27 */   public TypeIdResolver getTypeIdResolver() { return this._idResolver; }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected String idFromValue(Object value)
/*    */   {
/* 36 */     String id = this._idResolver.idFromValue(value);
/* 37 */     if (id == null) {
/* 38 */       handleMissingId(value);
/*    */     }
/* 40 */     return id;
/*    */   }
/*    */   
/*    */   protected String idFromValueAndType(Object value, Class<?> type) {
/* 44 */     String id = this._idResolver.idFromValueAndType(value, type);
/* 45 */     if (id == null) {
/* 46 */       handleMissingId(value);
/*    */     }
/* 48 */     return id;
/*    */   }
/*    */   
/*    */   protected void handleMissingId(Object value) {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\TypeSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */