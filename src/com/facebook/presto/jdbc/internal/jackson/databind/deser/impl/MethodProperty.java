/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MethodProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedMethod _annotated;
/*     */   protected final transient Method _setter;
/*     */   
/*     */   public MethodProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method)
/*     */   {
/*  36 */     super(propDef, type, typeDeser, contextAnnotations);
/*  37 */     this._annotated = method;
/*  38 */     this._setter = method.getAnnotated();
/*     */   }
/*     */   
/*     */   protected MethodProperty(MethodProperty src, JsonDeserializer<?> deser) {
/*  42 */     super(src, deser);
/*  43 */     this._annotated = src._annotated;
/*  44 */     this._setter = src._setter;
/*     */   }
/*     */   
/*     */   protected MethodProperty(MethodProperty src, PropertyName newName) {
/*  48 */     super(src, newName);
/*  49 */     this._annotated = src._annotated;
/*  50 */     this._setter = src._setter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected MethodProperty(MethodProperty src, Method m)
/*     */   {
/*  57 */     super(src);
/*  58 */     this._annotated = src._annotated;
/*  59 */     this._setter = m;
/*     */   }
/*     */   
/*     */   public MethodProperty withName(PropertyName newName)
/*     */   {
/*  64 */     return new MethodProperty(this, newName);
/*     */   }
/*     */   
/*     */   public MethodProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/*  69 */     return new MethodProperty(this, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/*  80 */     return this._annotated == null ? null : this._annotated.getAnnotation(acls);
/*     */   }
/*     */   
/*  83 */   public AnnotatedMember getMember() { return this._annotated; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/*  95 */     Object value = deserialize(p, ctxt);
/*     */     try {
/*  97 */       this._setter.invoke(instance, new Object[] { value });
/*     */     } catch (Exception e) {
/*  99 */       _throwAsIOE(p, e, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/* 107 */     Object value = deserialize(p, ctxt);
/*     */     try {
/* 109 */       Object result = this._setter.invoke(instance, new Object[] { value });
/* 110 */       return result == null ? instance : result;
/*     */     } catch (Exception e) {
/* 112 */       _throwAsIOE(p, e, value); }
/* 113 */     return null;
/*     */   }
/*     */   
/*     */   public final void set(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 121 */       this._setter.invoke(instance, new Object[] { value });
/*     */     }
/*     */     catch (Exception e) {
/* 124 */       _throwAsIOE(e, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value) throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 132 */       Object result = this._setter.invoke(instance, new Object[] { value });
/* 133 */       return result == null ? instance : result;
/*     */     }
/*     */     catch (Exception e) {
/* 136 */       _throwAsIOE(e, value); }
/* 137 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Object readResolve()
/*     */   {
/* 148 */     return new MethodProperty(this, this._annotated.getAnnotated());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\MethodProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */