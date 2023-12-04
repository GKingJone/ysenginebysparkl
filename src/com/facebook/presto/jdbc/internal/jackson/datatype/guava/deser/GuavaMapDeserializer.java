/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GuavaMapDeserializer<T>
/*     */   extends JsonDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   protected final MapType _mapType;
/*     */   protected KeyDeserializer _keyDeserializer;
/*     */   protected JsonDeserializer<?> _valueDeserializer;
/*     */   protected final TypeDeserializer _typeDeserializerForValue;
/*     */   
/*     */   protected GuavaMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*     */   {
/*  44 */     this._mapType = type;
/*  45 */     this._keyDeserializer = keyDeser;
/*  46 */     this._typeDeserializerForValue = typeDeser;
/*  47 */     this._valueDeserializer = deser;
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
/*     */   public abstract GuavaMapDeserializer<T> withResolved(KeyDeserializer paramKeyDeserializer, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer);
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
/*  72 */     KeyDeserializer keyDeser = this._keyDeserializer;
/*  73 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  74 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/*     */     
/*  76 */     if ((keyDeser != null) && (deser != null) && (typeDeser == null)) {
/*  77 */       return this;
/*     */     }
/*  79 */     if (keyDeser == null) {
/*  80 */       keyDeser = ctxt.findKeyDeserializer(this._mapType.getKeyType(), property);
/*     */     }
/*  82 */     if (deser == null) {
/*  83 */       deser = ctxt.findContextualValueDeserializer(this._mapType.getContentType(), property);
/*     */     }
/*  85 */     if (typeDeser != null) {
/*  86 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/*  88 */     return withResolved(keyDeser, typeDeser, deser);
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
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 109 */     return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 117 */     JsonToken t = jp.getCurrentToken();
/* 118 */     if (t == JsonToken.START_OBJECT) {
/* 119 */       t = jp.nextToken();
/*     */     }
/* 121 */     if ((t != JsonToken.FIELD_NAME) && (t != JsonToken.END_OBJECT)) {
/* 122 */       throw ctxt.mappingException(this._mapType.getRawClass());
/*     */     }
/* 124 */     return (T)_deserializeEntries(jp, ctxt);
/*     */   }
/*     */   
/*     */   protected abstract T _deserializeEntries(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException, JsonProcessingException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaMapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */