/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CreatorProperty
/*     */   extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final AnnotatedParameter _annotated;
/*     */   protected final Object _injectableValueId;
/*     */   protected final int _creatorIndex;
/*     */   protected SettableBeanProperty _fallbackSetter;
/*     */   
/*     */   public CreatorProperty(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, Object injectableValueId, PropertyMetadata metadata)
/*     */   {
/*  81 */     super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
/*  82 */     this._annotated = param;
/*  83 */     this._creatorIndex = index;
/*  84 */     this._injectableValueId = injectableValueId;
/*  85 */     this._fallbackSetter = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected CreatorProperty(CreatorProperty src, PropertyName newName)
/*     */   {
/*  92 */     super(src, newName);
/*  93 */     this._annotated = src._annotated;
/*  94 */     this._creatorIndex = src._creatorIndex;
/*  95 */     this._injectableValueId = src._injectableValueId;
/*  96 */     this._fallbackSetter = src._fallbackSetter;
/*     */   }
/*     */   
/*     */   protected CreatorProperty(CreatorProperty src, JsonDeserializer<?> deser) {
/* 100 */     super(src, deser);
/* 101 */     this._annotated = src._annotated;
/* 102 */     this._creatorIndex = src._creatorIndex;
/* 103 */     this._injectableValueId = src._injectableValueId;
/* 104 */     this._fallbackSetter = src._fallbackSetter;
/*     */   }
/*     */   
/*     */   public CreatorProperty withName(PropertyName newName)
/*     */   {
/* 109 */     return new CreatorProperty(this, newName);
/*     */   }
/*     */   
/*     */   public CreatorProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/* 114 */     return new CreatorProperty(this, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFallbackSetter(SettableBeanProperty fallbackSetter)
/*     */   {
/* 124 */     this._fallbackSetter = fallbackSetter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object findInjectableValue(DeserializationContext context, Object beanInstance)
/*     */   {
/* 133 */     if (this._injectableValueId == null) {
/* 134 */       throw new IllegalStateException("Property '" + getName() + "' (type " + getClass().getName() + ") has no injectable value id configured");
/*     */     }
/*     */     
/* 137 */     return context.findInjectableValue(this._injectableValueId, this, beanInstance);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void inject(DeserializationContext context, Object beanInstance)
/*     */     throws IOException
/*     */   {
/* 146 */     set(beanInstance, findInjectableValue(context, beanInstance));
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
/* 157 */     if (this._annotated == null) {
/* 158 */       return null;
/*     */     }
/* 160 */     return this._annotated.getAnnotation(acls);
/*     */   }
/*     */   
/* 163 */   public AnnotatedMember getMember() { return this._annotated; }
/*     */   
/*     */   public int getCreatorIndex() {
/* 166 */     return this._creatorIndex;
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
/*     */   public void deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object instance)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 180 */     set(instance, deserialize(jp, ctxt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object deserializeSetAndReturn(JsonParser jp, DeserializationContext ctxt, Object instance)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 188 */     return setAndReturn(instance, deserialize(jp, ctxt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/* 197 */     if (this._fallbackSetter == null) {
/* 198 */       throw new IllegalStateException("No fallback setter/field defined: can not use creator property for " + getClass().getName());
/*     */     }
/*     */     
/* 201 */     this._fallbackSetter.set(instance, value);
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/* 207 */     if (this._fallbackSetter == null) {
/* 208 */       throw new IllegalStateException("No fallback setter/field defined: can not use creator property for " + getClass().getName());
/*     */     }
/*     */     
/* 211 */     return this._fallbackSetter.setAndReturn(instance, value);
/*     */   }
/*     */   
/*     */   public Object getInjectableValueId()
/*     */   {
/* 216 */     return this._injectableValueId;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 220 */     return "[creator property, name '" + getName() + "'; inject id '" + this._injectableValueId + "']";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\CreatorProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */