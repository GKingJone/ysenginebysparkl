/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import java.util.List;
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
/*     */ public abstract class BeanSerializerModifier
/*     */ {
/*     */   public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
/*     */   {
/*  49 */     return beanProperties;
/*     */   }
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
/*     */   public List<BeanPropertyWriter> orderProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties)
/*     */   {
/*  64 */     return beanProperties;
/*     */   }
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
/*     */   public BeanSerializerBuilder updateBuilder(SerializationConfig config, BeanDescription beanDesc, BeanSerializerBuilder builder)
/*     */   {
/*  78 */     return builder;
/*     */   }
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
/*     */   public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/*  95 */     return serializer;
/*     */   }
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
/*     */   public JsonSerializer<?> modifyArraySerializer(SerializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 123 */     return serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> modifyCollectionSerializer(SerializationConfig config, CollectionType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 131 */     return serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> modifyCollectionLikeSerializer(SerializationConfig config, CollectionLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 139 */     return serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> modifyMapSerializer(SerializationConfig config, MapType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 147 */     return serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> modifyMapLikeSerializer(SerializationConfig config, MapLikeType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 155 */     return serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 163 */     return serializer;
/*     */   }
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
/*     */   public JsonSerializer<?> modifyKeySerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer)
/*     */   {
/* 185 */     return serializer;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\BeanSerializerModifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */