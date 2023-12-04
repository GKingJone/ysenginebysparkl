/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ObjectIdValueProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final ObjectIdReader _objectIdReader;
/*     */   
/*     */   public ObjectIdValueProperty(ObjectIdReader objectIdReader, PropertyMetadata metadata)
/*     */   {
/*  27 */     super(objectIdReader.propertyName, objectIdReader.getIdType(), metadata, objectIdReader.getDeserializer());
/*     */     
/*  29 */     this._objectIdReader = objectIdReader;
/*     */   }
/*     */   
/*     */   protected ObjectIdValueProperty(ObjectIdValueProperty src, JsonDeserializer<?> deser)
/*     */   {
/*  34 */     super(src, deser);
/*  35 */     this._objectIdReader = src._objectIdReader;
/*     */   }
/*     */   
/*     */   protected ObjectIdValueProperty(ObjectIdValueProperty src, PropertyName newName) {
/*  39 */     super(src, newName);
/*  40 */     this._objectIdReader = src._objectIdReader;
/*     */   }
/*     */   
/*     */   public ObjectIdValueProperty withName(PropertyName newName)
/*     */   {
/*  45 */     return new ObjectIdValueProperty(this, newName);
/*     */   }
/*     */   
/*     */   public ObjectIdValueProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/*  50 */     return new ObjectIdValueProperty(this, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/*  57 */     return null;
/*     */   }
/*     */   
/*  60 */   public AnnotatedMember getMember() { return null; }
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
/*  72 */     deserializeSetAndReturn(p, ctxt, instance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance)
/*     */     throws IOException
/*     */   {
/*  85 */     if (p.hasToken(JsonToken.VALUE_NULL)) {
/*  86 */       return null;
/*     */     }
/*  88 */     Object id = this._valueDeserializer.deserialize(p, ctxt);
/*  89 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*  90 */     roid.bindItem(instance);
/*     */     
/*  92 */     SettableBeanProperty idProp = this._objectIdReader.idProperty;
/*  93 */     if (idProp != null) {
/*  94 */       return idProp.setAndReturn(instance, id);
/*     */     }
/*  96 */     return instance;
/*     */   }
/*     */   
/*     */   public void set(Object instance, Object value) throws IOException
/*     */   {
/* 101 */     setAndReturn(instance, value);
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/* 107 */     SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 108 */     if (idProp == null) {
/* 109 */       throw new UnsupportedOperationException("Should not call set() on ObjectIdProperty that has no SettableBeanProperty");
/*     */     }
/*     */     
/* 112 */     return idProp.setAndReturn(instance, value);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\ObjectIdValueProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */