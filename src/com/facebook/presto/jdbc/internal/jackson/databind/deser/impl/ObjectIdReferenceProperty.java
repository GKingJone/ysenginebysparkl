/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.UnresolvedForwardReference;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import java.io.IOException;
/*     */ import java.lang.annotation.Annotation;
/*     */ 
/*     */ public class ObjectIdReferenceProperty extends SettableBeanProperty
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final SettableBeanProperty _forward;
/*     */   
/*     */   public ObjectIdReferenceProperty(SettableBeanProperty forward, ObjectIdInfo objectIdInfo)
/*     */   {
/*  22 */     super(forward);
/*  23 */     this._forward = forward;
/*  24 */     this._objectIdInfo = objectIdInfo;
/*     */   }
/*     */   
/*     */   public ObjectIdReferenceProperty(ObjectIdReferenceProperty src, JsonDeserializer<?> deser)
/*     */   {
/*  29 */     super(src, deser);
/*  30 */     this._forward = src._forward;
/*  31 */     this._objectIdInfo = src._objectIdInfo;
/*     */   }
/*     */   
/*     */   public ObjectIdReferenceProperty(ObjectIdReferenceProperty src, PropertyName newName)
/*     */   {
/*  36 */     super(src, newName);
/*  37 */     this._forward = src._forward;
/*  38 */     this._objectIdInfo = src._objectIdInfo;
/*     */   }
/*     */   
/*     */   public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser)
/*     */   {
/*  43 */     return new ObjectIdReferenceProperty(this, deser);
/*     */   }
/*     */   
/*     */   public SettableBeanProperty withName(PropertyName newName)
/*     */   {
/*  48 */     return new ObjectIdReferenceProperty(this, newName);
/*     */   }
/*     */   
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/*  53 */     return this._forward.getAnnotation(acls);
/*     */   }
/*     */   
/*     */   public AnnotatedMember getMember()
/*     */   {
/*  58 */     return this._forward.getMember();
/*     */   }
/*     */   
/*     */   public int getCreatorIndex()
/*     */   {
/*  63 */     return this._forward.getCreatorIndex();
/*     */   }
/*     */   
/*     */   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException
/*     */   {
/*  68 */     deserializeSetAndReturn(p, ctxt, instance);
/*     */   }
/*     */   
/*     */   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  75 */       return setAndReturn(instance, deserialize(p, ctxt));
/*     */     } catch (UnresolvedForwardReference reference) {
/*  77 */       boolean usingIdentityInfo = (this._objectIdInfo != null) || (this._valueDeserializer.getObjectIdReader() != null);
/*  78 */       if (!usingIdentityInfo) {
/*  79 */         throw com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException.from(p, "Unresolved forward reference but no identity info.", reference);
/*     */       }
/*  81 */       reference.getRoid().appendReferring(new PropertyReferring(this, reference, this._type.getRawClass(), instance)); }
/*  82 */     return null;
/*     */   }
/*     */   
/*     */   public void set(Object instance, Object value)
/*     */     throws IOException
/*     */   {
/*  88 */     this._forward.set(instance, value);
/*     */   }
/*     */   
/*     */   public Object setAndReturn(Object instance, Object value) throws IOException
/*     */   {
/*  93 */     return this._forward.setAndReturn(instance, value);
/*     */   }
/*     */   
/*     */   public static final class PropertyReferring extends ReadableObjectId.Referring
/*     */   {
/*     */     private final ObjectIdReferenceProperty _parent;
/*     */     public final Object _pojo;
/*     */     
/*     */     public PropertyReferring(ObjectIdReferenceProperty parent, UnresolvedForwardReference ref, Class<?> type, Object ob)
/*     */     {
/* 103 */       super(type);
/* 104 */       this._parent = parent;
/* 105 */       this._pojo = ob;
/*     */     }
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value)
/*     */       throws IOException
/*     */     {
/* 111 */       if (!hasId(id)) {
/* 112 */         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
/*     */       }
/*     */       
/* 115 */       this._parent.set(this._pojo, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\ObjectIdReferenceProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */