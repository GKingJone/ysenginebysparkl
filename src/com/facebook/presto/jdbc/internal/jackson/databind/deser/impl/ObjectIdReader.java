/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.SimpleObjectIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class ObjectIdReader
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _idType;
/*     */   public final PropertyName propertyName;
/*     */   public final ObjectIdGenerator<?> generator;
/*     */   public final ObjectIdResolver resolver;
/*     */   protected final JsonDeserializer<Object> _deserializer;
/*     */   public final SettableBeanProperty idProperty;
/*     */   
/*     */   protected ObjectIdReader(JavaType t, PropertyName propName, ObjectIdGenerator<?> gen, JsonDeserializer<?> deser, SettableBeanProperty idProp, ObjectIdResolver resolver)
/*     */   {
/*  50 */     this._idType = t;
/*  51 */     this.propertyName = propName;
/*  52 */     this.generator = gen;
/*  53 */     this.resolver = resolver;
/*  54 */     this._deserializer = deser;
/*  55 */     this.idProperty = idProp;
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   protected ObjectIdReader(JavaType t, PropertyName propName, ObjectIdGenerator<?> gen, JsonDeserializer<?> deser, SettableBeanProperty idProp)
/*     */   {
/*  62 */     this(t, propName, gen, deser, idProp, new SimpleObjectIdResolver());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ObjectIdReader construct(JavaType idType, PropertyName propName, ObjectIdGenerator<?> generator, JsonDeserializer<?> deser, SettableBeanProperty idProp, ObjectIdResolver resolver)
/*     */   {
/*  74 */     return new ObjectIdReader(idType, propName, generator, deser, idProp, resolver);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static ObjectIdReader construct(JavaType idType, PropertyName propName, ObjectIdGenerator<?> generator, JsonDeserializer<?> deser, SettableBeanProperty idProp)
/*     */   {
/*  82 */     return construct(idType, propName, generator, deser, idProp, new SimpleObjectIdResolver());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<Object> getDeserializer()
/*     */   {
/*  92 */     return this._deserializer;
/*     */   }
/*     */   
/*     */   public JavaType getIdType() {
/*  96 */     return this._idType;
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
/*     */   public boolean maySerializeAsObject()
/*     */   {
/* 111 */     return this.generator.maySerializeAsObject();
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
/*     */   public boolean isValidReferencePropertyName(String name, JsonParser parser)
/*     */   {
/* 126 */     return this.generator.isValidReferencePropertyName(name, parser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object readObjectReference(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 136 */     return this._deserializer.deserialize(jp, ctxt);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\ObjectIdReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */