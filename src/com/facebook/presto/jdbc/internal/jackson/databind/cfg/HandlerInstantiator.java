/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.cfg;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyNamingStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.VirtualBeanPropertyWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HandlerInstantiator
/*     */ {
/*     */   public abstract JsonDeserializer<?> deserializerInstance(DeserializationConfig paramDeserializationConfig, Annotated paramAnnotated, Class<?> paramClass);
/*     */   
/*     */   public abstract KeyDeserializer keyDeserializerInstance(DeserializationConfig paramDeserializationConfig, Annotated paramAnnotated, Class<?> paramClass);
/*     */   
/*     */   public abstract JsonSerializer<?> serializerInstance(SerializationConfig paramSerializationConfig, Annotated paramAnnotated, Class<?> paramClass);
/*     */   
/*     */   public abstract TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> paramMapperConfig, Annotated paramAnnotated, Class<?> paramClass);
/*     */   
/*     */   public abstract TypeIdResolver typeIdResolverInstance(MapperConfig<?> paramMapperConfig, Annotated paramAnnotated, Class<?> paramClass);
/*     */   
/*     */   public ValueInstantiator valueInstantiatorInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass)
/*     */   {
/* 114 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdGenerator<?> objectIdGeneratorInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass)
/*     */   {
/* 125 */     return null;
/*     */   }
/*     */   
/*     */   public ObjectIdResolver resolverIdGeneratorInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass) {
/* 129 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PropertyNamingStrategy namingStrategyInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass)
/*     */   {
/* 140 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Converter<?, ?> converterInstance(MapperConfig<?> config, Annotated annotated, Class<?> implClass)
/*     */   {
/* 150 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public VirtualBeanPropertyWriter virtualPropertyWriterInstance(MapperConfig<?> config, Class<?> implClass)
/*     */   {
/* 161 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\cfg\HandlerInstantiator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */