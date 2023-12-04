/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
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
/*     */ public final class SetterlessProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedMethod _annotated;
/*     */   protected final Method _getter;
/*     */   
/*     */   public SetterlessProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedMethod method)
/*     */   {
/*  38 */     super(propDef, type, typeDeser, contextAnnotations);
/*  39 */     this._annotated = method;
/*  40 */     this._getter = method.getAnnotated();
/*     */   }
/*     */   
/*     */   protected SetterlessProperty(SetterlessProperty src, JsonDeserializer<?> deser) {
/*  44 */     super(src, deser);
/*  45 */     this._annotated = src._annotated;
/*  46 */     this._getter = src._getter;
/*     */   }
/*     */   
/*     */   protected SetterlessProperty(SetterlessProperty src, PropertyName newName) {
/*  50 */     super(src, newName);
/*  51 */     this._annotated = src._annotated;
/*  52 */     this._getter = src._getter;
/*     */   }
/*     */   
/*     */   public SetterlessProperty withName(PropertyName newName)
/*     */   {
/*  57 */     return new SetterlessProperty(this, newName);
/*     */   }
/*     */   
/*     */   public SetterlessProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/*  62 */     return new SetterlessProperty(this, deser);
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
/*  73 */     return this._annotated.getAnnotation(acls);
/*     */   }
/*     */   
/*  76 */   public AnnotatedMember getMember() { return this._annotated; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/*  88 */     JsonToken t = p.getCurrentToken();
/*  89 */     if (t == JsonToken.VALUE_NULL)
/*     */     {
/*     */ 
/*     */ 
/*  93 */       return;
/*     */     }
/*     */     
/*     */ 
/*  97 */     if (this._valueTypeDeserializer != null) {
/*  98 */       ctxt.reportMappingException("Problem deserializing 'setterless' property (\"%s\"): no way to handle typed deser with setterless yet", new Object[] { getName() });
/*     */     }
/*     */     
/*     */ 
/*     */     Object toModify;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 107 */       toModify = this._getter.invoke(instance, new Object[0]);
/*     */     } catch (Exception e) {
/* 109 */       _throwAsIOE(p, e);
/* 110 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */     if (toModify == null) {
/* 118 */       throw JsonMappingException.from(p, "Problem deserializing 'setterless' property '" + getName() + "': get method returned null");
/*     */     }
/*     */     
/* 121 */     this._valueDeserializer.deserialize(p, ctxt, toModify);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/* 128 */     deserializeAndSet(p, ctxt, instance);
/* 129 */     return instance;
/*     */   }
/*     */   
/*     */   public final void set(Object instance, Object value) throws IOException
/*     */   {
/* 134 */     throw new UnsupportedOperationException("Should never call 'set' on setterless property");
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/* 140 */     set(instance, value);
/* 141 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\SetterlessProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */