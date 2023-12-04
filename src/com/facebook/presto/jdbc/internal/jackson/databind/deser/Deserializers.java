/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
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
/*     */ public abstract interface Deserializers
/*     */ {
/*     */   public abstract JsonDeserializer<?> findEnumDeserializer(Class<?> paramClass, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> paramClass, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findBeanDeserializer(JavaType paramJavaType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findReferenceDeserializer(ReferenceType paramReferenceType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findArrayDeserializer(ArrayType paramArrayType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findCollectionDeserializer(CollectionType paramCollectionType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType paramCollectionLikeType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findMapDeserializer(MapType paramMapType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, KeyDeserializer paramKeyDeserializer, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public abstract JsonDeserializer<?> findMapLikeDeserializer(MapLikeType paramMapLikeType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription, KeyDeserializer paramKeyDeserializer, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer)
/*     */     throws JsonMappingException;
/*     */   
/*     */   public static class Base
/*     */     implements Deserializers
/*     */   {
/*     */     public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc)
/*     */       throws JsonMappingException
/*     */     {
/* 258 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc)
/*     */       throws JsonMappingException
/*     */     {
/* 266 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 276 */       return findBeanDeserializer(refType, config, beanDesc);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*     */       throws JsonMappingException
/*     */     {
/* 284 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 293 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 302 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 311 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 321 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */       throws JsonMappingException
/*     */     {
/* 331 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\Deserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */