/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SubtypeResolver
/*     */ {
/*     */   public abstract void registerSubtypes(NamedType... paramVarArgs);
/*     */   
/*     */   public abstract void registerSubtypes(Class<?>... paramVarArgs);
/*     */   
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedMember property, JavaType baseType)
/*     */   {
/*  53 */     return collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedClass baseType)
/*     */   {
/*  71 */     return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedMember property, JavaType baseType)
/*     */   {
/*  89 */     return collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
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
/*     */   public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedClass baseType)
/*     */   {
/* 107 */     return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public abstract Collection<NamedType> collectAndResolveSubtypes(AnnotatedMember paramAnnotatedMember, MapperConfig<?> paramMapperConfig, AnnotationIntrospector paramAnnotationIntrospector, JavaType paramJavaType);
/*     */   
/*     */   @Deprecated
/*     */   public abstract Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass paramAnnotatedClass, MapperConfig<?> paramMapperConfig, AnnotationIntrospector paramAnnotationIntrospector);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\SubtypeResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */