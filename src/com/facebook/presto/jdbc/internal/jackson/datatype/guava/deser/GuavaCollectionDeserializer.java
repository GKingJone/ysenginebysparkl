/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import java.io.IOException;
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
/*     */ public abstract class GuavaCollectionDeserializer<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final CollectionType _containerType;
/*     */   protected final JsonDeserializer<?> _valueDeserializer;
/*     */   protected final TypeDeserializer _typeDeserializerForValue;
/*     */   
/*     */   protected GuavaCollectionDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*     */   {
/*  36 */     super(type);
/*  37 */     this._containerType = type;
/*  38 */     this._typeDeserializerForValue = typeDeser;
/*  39 */     this._valueDeserializer = deser;
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
/*     */   public abstract GuavaCollectionDeserializer<T> withResolved(TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  64 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  65 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/*  66 */     if (deser == null) {
/*  67 */       deser = ctxt.findContextualValueDeserializer(this._containerType.getContentType(), property);
/*     */     }
/*  69 */     if (typeDeser != null) {
/*  70 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/*  72 */     if ((deser == this._valueDeserializer) && (typeDeser == this._typeDeserializerForValue)) {
/*  73 */       return this;
/*     */     }
/*  75 */     return withResolved(typeDeser, deser);
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
/*     */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/*  94 */     return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 102 */     if (jp.isExpectedStartArrayToken()) {
/* 103 */       return (T)_deserializeContents(jp, ctxt);
/*     */     }
/*     */     
/* 106 */     if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
/* 107 */       return (T)_deserializeFromSingleValue(jp, ctxt);
/*     */     }
/* 109 */     throw ctxt.mappingException(this._containerType.getRawClass());
/*     */   }
/*     */   
/*     */   protected abstract T _deserializeContents(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException, JsonProcessingException;
/*     */   
/*     */   protected abstract T _deserializeFromSingleValue(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException, JsonProcessingException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaCollectionDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */