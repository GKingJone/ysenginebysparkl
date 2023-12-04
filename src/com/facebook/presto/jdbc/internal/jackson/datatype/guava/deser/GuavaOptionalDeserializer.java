/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GuavaOptionalDeserializer
/*     */   extends StdDeserializer<Optional<?>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _fullType;
/*     */   protected final JsonDeserializer<?> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   
/*     */   public GuavaOptionalDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*     */   {
/*  35 */     super(fullType);
/*  36 */     this._fullType = fullType;
/*  37 */     this._valueTypeDeserializer = typeDeser;
/*  38 */     this._valueDeserializer = valueDeser;
/*     */   }
/*     */   
/*     */   public JavaType getValueType() {
/*  42 */     return this._fullType;
/*     */   }
/*     */   
/*     */   public Optional<?> getNullValue(DeserializationContext ctxt) {
/*  46 */     return Optional.absent();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public Optional<?> getNullValue()
/*     */   {
/*  52 */     return Optional.absent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GuavaOptionalDeserializer withResolved(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*     */   {
/*  62 */     if ((this._fullType == fullType) && (valueDeser == this._valueDeserializer) && (typeDeser == this._valueTypeDeserializer))
/*     */     {
/*  64 */       return this;
/*     */     }
/*  66 */     return new GuavaOptionalDeserializer(this._fullType, typeDeser, valueDeser);
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
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  84 */     JsonDeserializer<?> deser = this._valueDeserializer;
/*  85 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*  86 */     JavaType fullType = this._fullType;
/*     */     
/*  88 */     if (deser == null)
/*     */     {
/*  90 */       if (property != null) {
/*  91 */         AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  92 */         AnnotatedMember member = property.getMember();
/*  93 */         if ((intr != null) && (member != null)) {
/*  94 */           fullType = intr.refineDeserializationType(ctxt.getConfig(), member, fullType);
/*     */         }
/*     */       }
/*  97 */       JavaType refdType = fullType.getContentType();
/*  98 */       if (refdType == null) {
/*  99 */         refdType = TypeFactory.unknownType();
/*     */       }
/* 101 */       deser = ctxt.findContextualValueDeserializer(refdType, property);
/*     */     } else {
/* 103 */       JavaType refdType = fullType.getContentType();
/* 104 */       if (refdType == null) {
/* 105 */         refdType = TypeFactory.unknownType();
/*     */       }
/* 107 */       deser = ctxt.handleSecondaryContextualization(deser, property, refdType);
/*     */     }
/* 109 */     if (typeDeser != null) {
/* 110 */       typeDeser = typeDeser.forProperty(property);
/*     */     }
/* 112 */     return withResolved(fullType, typeDeser, deser);
/*     */   }
/*     */   
/*     */   public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 118 */     Object refd = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */     
/*     */ 
/* 121 */     return Optional.fromNullable(refd);
/*     */   }
/*     */   
/*     */ 
/*     */   public Optional<?> deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 128 */     JsonToken t = p.getCurrentToken();
/* 129 */     if (t == JsonToken.VALUE_NULL) {
/* 130 */       return getNullValue(ctxt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */     if ((t != null) && (t.isScalarValue())) {
/* 141 */       return deserialize(p, ctxt);
/*     */     }
/* 143 */     return (Optional)typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaOptionalDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */