/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ReferenceTypeDeserializer<T>
/*     */   extends StdDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _fullType;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final JsonDeserializer<?> _valueDeserializer;
/*     */   
/*     */   public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*     */   {
/*  45 */     super(fullType);
/*  46 */     this._fullType = fullType;
/*  47 */     this._valueDeserializer = deser;
/*  48 */     this._valueTypeDeserializer = typeDeser;
/*     */   }
/*     */   
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  54 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  55 */     if (deser == null) {
/*  56 */       deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
/*     */     } else {
/*  58 */       deser = ctxt.handleSecondaryContextualization(deser, property, this._fullType.getReferencedType());
/*     */     }
/*  60 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*  61 */     if (typeDeser != null) {
/*  62 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/*  64 */     if ((deser == this._valueDeserializer) && (typeDeser == this._valueTypeDeserializer)) {
/*  65 */       return this;
/*     */     }
/*  67 */     return withResolved(typeDeser, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract T getNullValue(DeserializationContext paramDeserializationContext);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract T referenceValue(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getValueType()
/*     */   {
/*  90 */     return this._fullType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 100 */     Object contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */     
/*     */ 
/* 103 */     return (T)referenceValue(contents);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 110 */     JsonToken t = p.getCurrentToken();
/* 111 */     if (t == JsonToken.VALUE_NULL) {
/* 112 */       return getNullValue(ctxt);
/*     */     }
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
/* 126 */     if (this._valueTypeDeserializer == null) {
/* 127 */       return deserialize(p, ctxt);
/*     */     }
/* 129 */     return referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\ReferenceTypeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */