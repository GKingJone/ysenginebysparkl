/*     */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedConstructor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BeanDescription
/*     */ {
/*     */   protected final JavaType _type;
/*     */   
/*     */   protected BeanDescription(JavaType type)
/*     */   {
/*  37 */     this._type = type;
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
/*  50 */   public JavaType getType() { return this._type; }
/*     */   
/*  52 */   public Class<?> getBeanClass() { return this._type.getRawClass(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedClass getClassInfo();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ObjectIdInfo getObjectIdInfo();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean hasKnownClassAnnotations();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public abstract TypeBindings bindingsForBeanType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public abstract JavaType resolveType(Type paramType);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Annotations getClassAnnotations();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<BeanPropertyDefinition> findProperties();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Map<String, AnnotatedMember> findBackReferenceProperties();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Set<String> getIgnoredPropertyNames();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<AnnotatedConstructor> getConstructors();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<AnnotatedMethod> getFactoryMethods();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedConstructor findDefaultConstructor();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Constructor<?> findSingleArgConstructor(Class<?>... paramVarArgs);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Method findFactoryMethod(Class<?>... paramVarArgs);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember findAnyGetter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMethod findAnySetter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMember findAnySetterField();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMethod findJsonValueMethod();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract AnnotatedMethod findMethod(String paramString, Class<?>[] paramArrayOfClass);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonInclude.Value findPropertyInclusion(JsonInclude.Value paramValue);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonFormat.Value findExpectedFormat(JsonFormat.Value paramValue);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Converter<Object, Object> findSerializationConverter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Converter<Object, Object> findDeserializationConverter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String findClassDescription()
/*     */   {
/* 233 */     return null;
/*     */   }
/*     */   
/*     */   public abstract Map<Object, AnnotatedMember> findInjectables();
/*     */   
/*     */   public abstract Class<?> findPOJOBuilder();
/*     */   
/*     */   public abstract JsonPOJOBuilder.Value findPOJOBuilderConfig();
/*     */   
/*     */   public abstract Object instantiateBean(boolean paramBoolean);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\BeanDescription.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */