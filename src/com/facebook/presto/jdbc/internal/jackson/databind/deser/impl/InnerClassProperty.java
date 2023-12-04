/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedConstructor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InnerClassProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final SettableBeanProperty _delegate;
/*     */   protected final transient Constructor<?> _creator;
/*     */   protected AnnotatedConstructor _annotated;
/*     */   
/*     */   public InnerClassProperty(SettableBeanProperty delegate, Constructor<?> ctor)
/*     */   {
/*  44 */     super(delegate);
/*  45 */     this._delegate = delegate;
/*  46 */     this._creator = ctor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InnerClassProperty(InnerClassProperty src, AnnotatedConstructor ann)
/*     */   {
/*  55 */     super(src);
/*  56 */     this._delegate = src._delegate;
/*  57 */     this._annotated = ann;
/*  58 */     this._creator = (this._annotated == null ? null : this._annotated.getAnnotated());
/*  59 */     if (this._creator == null) {
/*  60 */       throw new IllegalArgumentException("Missing constructor (broken JDK (de)serialization?)");
/*     */     }
/*     */   }
/*     */   
/*     */   protected InnerClassProperty(InnerClassProperty src, JsonDeserializer<?> deser)
/*     */   {
/*  66 */     super(src, deser);
/*  67 */     this._delegate = src._delegate.withValueDeserializer(deser);
/*  68 */     this._creator = src._creator;
/*     */   }
/*     */   
/*     */   protected InnerClassProperty(InnerClassProperty src, PropertyName newName) {
/*  72 */     super(src, newName);
/*  73 */     this._delegate = src._delegate.withName(newName);
/*  74 */     this._creator = src._creator;
/*     */   }
/*     */   
/*     */   public InnerClassProperty withName(PropertyName newName)
/*     */   {
/*  79 */     return new InnerClassProperty(this, newName);
/*     */   }
/*     */   
/*     */   public InnerClassProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/*  84 */     return new InnerClassProperty(this, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/*  91 */     return this._delegate.getAnnotation(acls);
/*     */   }
/*     */   
/*  94 */   public AnnotatedMember getMember() { return this._delegate.getMember(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 106 */     JsonToken t = jp.getCurrentToken();
/*     */     Object value;
/* 108 */     Object value; if (t == JsonToken.VALUE_NULL) {
/* 109 */       value = this._valueDeserializer.getNullValue(ctxt); } else { Object value;
/* 110 */       if (this._valueTypeDeserializer != null) {
/* 111 */         value = this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
/*     */       } else {
/*     */         try {
/* 114 */           value = this._creator.newInstance(new Object[] { bean });
/*     */         } catch (Exception e) {
/* 116 */           ClassUtil.unwrapAndThrowAsIAE(e, "Failed to instantiate class " + this._creator.getDeclaringClass().getName() + ", problem: " + e.getMessage());
/* 117 */           value = null;
/*     */         }
/* 119 */         this._valueDeserializer.deserialize(jp, ctxt, value);
/*     */       } }
/* 121 */     set(bean, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object deserializeSetAndReturn(JsonParser jp, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/* 129 */     return setAndReturn(instance, deserialize(jp, ctxt));
/*     */   }
/*     */   
/*     */   public final void set(Object instance, Object value) throws IOException
/*     */   {
/* 134 */     this._delegate.set(instance, value);
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value) throws IOException
/*     */   {
/* 139 */     return this._delegate.setAndReturn(instance, value);
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
/* 150 */     return new InnerClassProperty(this, this._annotated);
/*     */   }
/*     */   
/*     */   Object writeReplace()
/*     */   {
/* 155 */     if (this._annotated != null) {
/* 156 */       return this;
/*     */     }
/* 158 */     return new InnerClassProperty(this, new AnnotatedConstructor(null, this._creator, null, null));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\InnerClassProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */