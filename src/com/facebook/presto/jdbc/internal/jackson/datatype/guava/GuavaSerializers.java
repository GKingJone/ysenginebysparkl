/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*     */ import com.facebook.presto.jdbc.internal.guava.cache.CacheBuilder;
/*     */ import com.facebook.presto.jdbc.internal.guava.cache.CacheBuilderSpec;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.FluentIterable;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Multimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Range;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Table;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers.Base;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdDelegatingSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.ToStringSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.StdConverter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser.MultimapSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser.RangeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser.TableSerializer;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class GuavaSerializers extends Serializers.Base
/*     */ {
/*     */   static class FluentConverter extends StdConverter<Object, Iterable<?>>
/*     */   {
/*  33 */     static final FluentConverter instance = new FluentConverter();
/*     */     
/*     */     public Iterable<?> convert(Object value)
/*     */     {
/*  37 */       return (Iterable)value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType refType, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
/*     */   {
/*  46 */     Class<?> raw = refType.getRawClass();
/*  47 */     if (Optional.class.isAssignableFrom(raw)) {
/*  48 */       return new com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser.GuavaOptionalSerializer(refType, contentTypeSerializer, contentValueSerializer);
/*     */     }
/*  50 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
/*     */   {
/*  56 */     Class<?> raw = type.getRawClass();
/*  57 */     if (Range.class.isAssignableFrom(raw)) {
/*  58 */       return new RangeSerializer(_findDeclared(type, Range.class));
/*     */     }
/*  60 */     if (Table.class.isAssignableFrom(raw)) {
/*  61 */       return new TableSerializer(_findDeclared(type, Table.class));
/*     */     }
/*     */     
/*     */ 
/*  65 */     if (HostAndPort.class.isAssignableFrom(raw)) {
/*  66 */       return ToStringSerializer.instance;
/*     */     }
/*  68 */     if (com.facebook.presto.jdbc.internal.guava.net.InternetDomainName.class.isAssignableFrom(raw)) {
/*  69 */       return ToStringSerializer.instance;
/*     */     }
/*     */     
/*  72 */     if ((CacheBuilderSpec.class.isAssignableFrom(raw)) || (CacheBuilder.class.isAssignableFrom(raw))) {
/*  73 */       return ToStringSerializer.instance;
/*     */     }
/*  75 */     if (com.facebook.presto.jdbc.internal.guava.hash.HashCode.class.isAssignableFrom(raw)) {
/*  76 */       return ToStringSerializer.instance;
/*     */     }
/*  78 */     if (FluentIterable.class.isAssignableFrom(raw)) {
/*  79 */       JavaType iterableType = _findDeclared(type, Iterable.class);
/*  80 */       return new StdDelegatingSerializer(FluentConverter.instance, iterableType, null);
/*     */     }
/*  82 */     return super.findSerializer(config, type, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
/*     */   {
/*  90 */     if (Multimap.class.isAssignableFrom(type.getRawClass())) {
/*  91 */       AnnotationIntrospector intr = config.getAnnotationIntrospector();
/*  92 */       Object filterId = intr.findFilterId(beanDesc.getClassInfo());
/*  93 */       JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Multimap.class, beanDesc.getClassInfo());
/*     */       
/*  95 */       Set<String> ignored = ignorals == null ? null : ignorals.getIgnored();
/*  96 */       return new MultimapSerializer(type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
/*     */     }
/*     */     
/*  99 */     return null;
/*     */   }
/*     */   
/*     */   private JavaType _findDeclared(JavaType subtype, Class<?> target) {
/* 103 */     JavaType decl = subtype.findSuperType(target);
/* 104 */     if (decl == null) {
/* 105 */       throw new IllegalArgumentException("Strange " + target.getName() + " sub-type, " + subtype + ", can not find type parameters");
/*     */     }
/* 107 */     return decl;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\GuavaSerializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */