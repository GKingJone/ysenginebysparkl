/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.module;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.KeyDeserializers;
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
/*    */ public class SimpleKeyDeserializers
/*    */   implements KeyDeserializers, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 27 */   protected HashMap<ClassKey, KeyDeserializer> _classMappings = null;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleKeyDeserializers addDeserializer(Class<?> forClass, KeyDeserializer deser)
/*    */   {
/* 39 */     if (this._classMappings == null) {
/* 40 */       this._classMappings = new HashMap();
/*    */     }
/* 42 */     this._classMappings.put(new ClassKey(forClass), deser);
/* 43 */     return this;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*    */   {
/* 56 */     if (this._classMappings == null) {
/* 57 */       return null;
/*    */     }
/* 59 */     return (KeyDeserializer)this._classMappings.get(new ClassKey(type.getRawClass()));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\module\SimpleKeyDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */