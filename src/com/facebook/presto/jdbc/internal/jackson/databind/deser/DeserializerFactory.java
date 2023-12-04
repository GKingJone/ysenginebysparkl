/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.AbstractTypeResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
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
/*    */ public abstract class DeserializerFactory
/*    */ {
/* 43 */   protected static final Deserializers[] NO_DESERIALIZERS = new Deserializers[0];
/*    */   
/*    */   public abstract DeserializerFactory withAdditionalDeserializers(Deserializers paramDeserializers);
/*    */   
/*    */   public abstract DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers paramKeyDeserializers);
/*    */   
/*    */   public abstract DeserializerFactory withDeserializerModifier(BeanDeserializerModifier paramBeanDeserializerModifier);
/*    */   
/*    */   public abstract DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver paramAbstractTypeResolver);
/*    */   
/*    */   public abstract DeserializerFactory withValueInstantiators(ValueInstantiators paramValueInstantiators);
/*    */   
/*    */   public abstract JavaType mapAbstractType(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract ValueInstantiator findValueInstantiator(DeserializationContext paramDeserializationContext, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<Object> createBeanDeserializer(DeserializationContext paramDeserializationContext, JavaType paramJavaType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext paramDeserializationContext, JavaType paramJavaType, BeanDescription paramBeanDescription, Class<?> paramClass)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createEnumDeserializer(DeserializationContext paramDeserializationContext, JavaType paramJavaType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createReferenceDeserializer(DeserializationContext paramDeserializationContext, ReferenceType paramReferenceType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createTreeDeserializer(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createArrayDeserializer(DeserializationContext paramDeserializationContext, ArrayType paramArrayType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createCollectionDeserializer(DeserializationContext paramDeserializationContext, CollectionType paramCollectionType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext paramDeserializationContext, CollectionLikeType paramCollectionLikeType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createMapDeserializer(DeserializationContext paramDeserializationContext, MapType paramMapType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext paramDeserializationContext, MapLikeType paramMapLikeType, BeanDescription paramBeanDescription)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract KeyDeserializer createKeyDeserializer(DeserializationContext paramDeserializationContext, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */   
/*    */   public abstract TypeDeserializer findTypeDeserializer(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType)
/*    */     throws JsonMappingException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\DeserializerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */