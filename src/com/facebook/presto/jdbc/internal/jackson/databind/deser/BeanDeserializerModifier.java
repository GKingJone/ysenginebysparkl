/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
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
/*     */ public abstract class BeanDeserializerModifier
/*     */ {
/*     */   public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs)
/*     */   {
/*  64 */     return propDefs;
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
/*     */   public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder)
/*     */   {
/*  77 */     return builder;
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
/*     */   public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/*  90 */     return deserializer;
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
/*     */   public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 104 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> modifyReferenceDeserializer(DeserializationConfig config, ReferenceType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 112 */     return deserializer;
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
/*     */   public JsonDeserializer<?> modifyArrayDeserializer(DeserializationConfig config, ArrayType valueType, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 134 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> modifyCollectionDeserializer(DeserializationConfig config, CollectionType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 142 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> modifyCollectionLikeDeserializer(DeserializationConfig config, CollectionLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 150 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 158 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> modifyMapLikeDeserializer(DeserializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
/*     */   {
/* 166 */     return deserializer;
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
/*     */   public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config, JavaType type, KeyDeserializer deserializer)
/*     */   {
/* 180 */     return deserializer;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BeanDeserializerModifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */