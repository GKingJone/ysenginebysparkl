/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator.IdKey;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*    */ 
/*    */ 
/*    */ public class PropertyBasedObjectIdGenerator
/*    */   extends ObjectIdGenerators.PropertyGenerator
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final BeanPropertyWriter _property;
/*    */   
/*    */   public PropertyBasedObjectIdGenerator(ObjectIdInfo oid, BeanPropertyWriter prop)
/*    */   {
/* 18 */     this(oid.getScope(), prop);
/*    */   }
/*    */   
/*    */   protected PropertyBasedObjectIdGenerator(Class<?> scope, BeanPropertyWriter prop)
/*    */   {
/* 23 */     super(scope);
/* 24 */     this._property = prop;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean canUseFor(ObjectIdGenerator<?> gen)
/*    */   {
/* 33 */     if (gen.getClass() == getClass()) {
/* 34 */       PropertyBasedObjectIdGenerator other = (PropertyBasedObjectIdGenerator)gen;
/* 35 */       if (other.getScope() == this._scope)
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 42 */         return other._property == this._property;
/*    */       }
/*    */     }
/* 45 */     return false;
/*    */   }
/*    */   
/*    */   public Object generateId(Object forPojo)
/*    */   {
/*    */     try {
/* 51 */       return this._property.get(forPojo);
/*    */     } catch (RuntimeException e) {
/* 53 */       throw e;
/*    */     } catch (Exception e) {
/* 55 */       throw new IllegalStateException("Problem accessing property '" + this._property.getName() + "': " + e.getMessage(), e);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public ObjectIdGenerator<Object> forScope(Class<?> scope)
/*    */   {
/* 62 */     return scope == this._scope ? this : new PropertyBasedObjectIdGenerator(scope, this._property);
/*    */   }
/*    */   
/*    */ 
/*    */   public ObjectIdGenerator<Object> newForSerialization(Object context)
/*    */   {
/* 68 */     return this;
/*    */   }
/*    */   
/*    */   public ObjectIdGenerator.IdKey key(Object key)
/*    */   {
/* 73 */     if (key == null) {
/* 74 */       return null;
/*    */     }
/*    */     
/* 77 */     return new ObjectIdGenerator.IdKey(getClass(), this._scope, key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\PropertyBasedObjectIdGenerator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */