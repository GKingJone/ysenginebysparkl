/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.module;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiators.Base;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey;
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
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
/*    */ public class SimpleValueInstantiators
/*    */   extends ValueInstantiators.Base
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -8929386427526115130L;
/*    */   protected HashMap<ClassKey, ValueInstantiator> _classMappings;
/*    */   
/*    */   public SimpleValueInstantiators()
/*    */   {
/* 31 */     this._classMappings = new HashMap();
/*    */   }
/*    */   
/*    */ 
/*    */   public SimpleValueInstantiators addValueInstantiator(Class<?> forType, ValueInstantiator inst)
/*    */   {
/* 37 */     this._classMappings.put(new ClassKey(forType), inst);
/* 38 */     return this;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator)
/*    */   {
/* 45 */     ValueInstantiator inst = (ValueInstantiator)this._classMappings.get(new ClassKey(beanDesc.getBeanClass()));
/* 46 */     return inst == null ? defaultInstantiator : inst;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\module\SimpleValueInstantiators.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */