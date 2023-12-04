/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.util.Optional;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class OptionalDeserializer
/*     */   extends StdDeserializer<Optional<?>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _fullType;
/*     */   protected final JsonDeserializer<?> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   
/*     */   public OptionalDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*     */   {
/*  33 */     super(fullType);
/*  34 */     this._fullType = fullType;
/*  35 */     this._valueTypeDeserializer = typeDeser;
/*  36 */     this._valueDeserializer = valueDeser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected OptionalDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*     */   {
/*  46 */     if ((valueDeser == this._valueDeserializer) && (typeDeser == this._valueTypeDeserializer)) {
/*  47 */       return this;
/*     */     }
/*  49 */     return new OptionalDeserializer(this._fullType, typeDeser, valueDeser);
/*     */   }
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
/*  61 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  62 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*  63 */     JavaType refType = this._fullType.getReferencedType();
/*     */     
/*  65 */     if (deser == null) {
/*  66 */       deser = ctxt.findContextualValueDeserializer(refType, property);
/*     */     } else {
/*  68 */       deser = ctxt.handleSecondaryContextualization(deser, property, refType);
/*     */     }
/*  70 */     if (typeDeser != null) {
/*  71 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/*  73 */     return withResolved(typeDeser, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getValueType()
/*     */   {
/*  83 */     return this._fullType;
/*     */   }
/*     */   
/*     */   public Optional<?> getNullValue(DeserializationContext ctxt) {
/*  87 */     return Optional.empty();
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
/*     */   public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 101 */     Object refd = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/* 102 */     return Optional.ofNullable(refd);
/*     */   }
/*     */   
/*     */ 
/*     */   public Optional<?> deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 109 */     JsonToken t = p.getCurrentToken();
/* 110 */     if (t == JsonToken.VALUE_NULL) {
/* 111 */       return getNullValue(ctxt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 121 */     if ((t != null) && (t.isScalarValue())) {
/* 122 */       return deserialize(p, ctxt);
/*     */     }
/* 124 */     return (Optional)typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */