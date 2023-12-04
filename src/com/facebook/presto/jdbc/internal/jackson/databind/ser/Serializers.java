/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface Serializers
/*     */ {
/*     */   public abstract JsonSerializer<?> findSerializer(SerializationConfig paramSerializationConfig, JavaType paramJavaType, BeanDescription paramBeanDescription);
/*     */   
/*     */   public abstract JsonSerializer<?> findReferenceSerializer(SerializationConfig paramSerializationConfig, ReferenceType paramReferenceType, BeanDescription paramBeanDescription, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer);
/*     */   
/*     */   public abstract JsonSerializer<?> findArraySerializer(SerializationConfig paramSerializationConfig, ArrayType paramArrayType, BeanDescription paramBeanDescription, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer);
/*     */   
/*     */   public abstract JsonSerializer<?> findCollectionSerializer(SerializationConfig paramSerializationConfig, CollectionType paramCollectionType, BeanDescription paramBeanDescription, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer);
/*     */   
/*     */   public abstract JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig paramSerializationConfig, CollectionLikeType paramCollectionLikeType, BeanDescription paramBeanDescription, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer);
/*     */   
/*     */   public abstract JsonSerializer<?> findMapSerializer(SerializationConfig paramSerializationConfig, MapType paramMapType, BeanDescription paramBeanDescription, JsonSerializer<Object> paramJsonSerializer1, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer2);
/*     */   
/*     */   public abstract JsonSerializer<?> findMapLikeSerializer(SerializationConfig paramSerializationConfig, MapLikeType paramMapLikeType, BeanDescription paramBeanDescription, JsonSerializer<Object> paramJsonSerializer1, TypeSerializer paramTypeSerializer, JsonSerializer<Object> paramJsonSerializer2);
/*     */   
/*     */   public static class Base
/*     */     implements Serializers
/*     */   {
/*     */     public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
/*     */     {
/* 107 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType type, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
/*     */     {
/* 116 */       return findSerializer(config, type, beanDesc);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */     {
/* 124 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */     {
/* 132 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */     {
/* 140 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */     {
/* 149 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */     {
/* 158 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\Serializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */