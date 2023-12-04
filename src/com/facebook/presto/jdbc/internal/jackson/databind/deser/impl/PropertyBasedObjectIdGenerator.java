/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator.IdKey;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
/*    */ 
/*    */ public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public PropertyBasedObjectIdGenerator(Class<?> scope)
/*    */   {
/* 13 */     super(scope);
/*    */   }
/*    */   
/*    */   public Object generateId(Object forPojo)
/*    */   {
/* 18 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public ObjectIdGenerator<Object> forScope(Class<?> scope)
/*    */   {
/* 23 */     return scope == this._scope ? this : new PropertyBasedObjectIdGenerator(scope);
/*    */   }
/*    */   
/*    */   public ObjectIdGenerator<Object> newForSerialization(Object context)
/*    */   {
/* 28 */     return this;
/*    */   }
/*    */   
/*    */   public ObjectIdGenerator.IdKey key(Object key)
/*    */   {
/* 33 */     if (key == null) {
/* 34 */       return null;
/*    */     }
/*    */     
/* 37 */     return new ObjectIdGenerator.IdKey(getClass(), this._scope, key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\PropertyBasedObjectIdGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */