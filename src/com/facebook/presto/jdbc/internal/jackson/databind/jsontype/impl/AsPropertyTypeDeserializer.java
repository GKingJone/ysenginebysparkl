/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.JsonParserSequence;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
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
/*     */ public class AsPropertyTypeDeserializer
/*     */   extends AsArrayTypeDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JsonTypeInfo.As _inclusion;
/*     */   
/*     */   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl)
/*     */   {
/*  33 */     this(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, JsonTypeInfo.As.PROPERTY);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsPropertyTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl, JsonTypeInfo.As inclusion)
/*     */   {
/*  43 */     super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
/*  44 */     this._inclusion = inclusion;
/*     */   }
/*     */   
/*     */   public AsPropertyTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
/*  48 */     super(src, property);
/*  49 */     this._inclusion = src._inclusion;
/*     */   }
/*     */   
/*     */   public TypeDeserializer forProperty(BeanProperty prop)
/*     */   {
/*  54 */     return prop == this._property ? this : new AsPropertyTypeDeserializer(this, prop);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.As getTypeInclusion() {
/*  58 */     return this._inclusion;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/*  69 */     if (p.canReadTypeId()) {
/*  70 */       Object typeId = p.getTypeId();
/*  71 */       if (typeId != null) {
/*  72 */         return _deserializeWithNativeTypeId(p, ctxt, typeId);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  77 */     JsonToken t = p.getCurrentToken();
/*  78 */     if (t == JsonToken.START_OBJECT) {
/*  79 */       t = p.nextToken();
/*  80 */     } else if (t != JsonToken.FIELD_NAME)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */       return _deserializeTypedUsingDefaultImpl(p, ctxt, null);
/*     */     }
/*     */     
/*  91 */     TokenBuffer tb = null;
/*  93 */     for (; 
/*  93 */         t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/*  94 */       String name = p.getCurrentName();
/*  95 */       p.nextToken();
/*  96 */       if (name.equals(this._typePropertyName)) {
/*  97 */         return _deserializeTypedForId(p, ctxt, tb);
/*     */       }
/*  99 */       if (tb == null) {
/* 100 */         tb = new TokenBuffer(p, ctxt);
/*     */       }
/* 102 */       tb.writeFieldName(name);
/* 103 */       tb.copyCurrentStructure(p);
/*     */     }
/* 105 */     return _deserializeTypedUsingDefaultImpl(p, ctxt, tb);
/*     */   }
/*     */   
/*     */   protected Object _deserializeTypedForId(JsonParser p, DeserializationContext ctxt, TokenBuffer tb)
/*     */     throws IOException
/*     */   {
/* 111 */     String typeId = p.getText();
/* 112 */     JsonDeserializer<Object> deser = _findDeserializer(ctxt, typeId);
/* 113 */     if (this._typeIdVisible) {
/* 114 */       if (tb == null) {
/* 115 */         tb = new TokenBuffer(p, ctxt);
/*     */       }
/* 117 */       tb.writeFieldName(p.getCurrentName());
/* 118 */       tb.writeString(typeId);
/*     */     }
/* 120 */     if (tb != null)
/*     */     {
/*     */ 
/* 123 */       p.clearCurrentToken();
/* 124 */       p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
/*     */     }
/*     */     
/* 127 */     p.nextToken();
/*     */     
/* 129 */     return deser.deserialize(p, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object _deserializeTypedUsingDefaultImpl(JsonParser p, DeserializationContext ctxt, TokenBuffer tb)
/*     */     throws IOException
/*     */   {
/* 138 */     JsonDeserializer<Object> deser = _findDefaultImplDeserializer(ctxt);
/* 139 */     if (deser != null) {
/* 140 */       if (tb != null) {
/* 141 */         tb.writeEndObject();
/* 142 */         p = tb.asParser(p);
/*     */         
/* 144 */         p.nextToken();
/*     */       }
/* 146 */       return deser.deserialize(p, ctxt);
/*     */     }
/*     */     
/* 149 */     Object result = TypeDeserializer.deserializeIfNatural(p, ctxt, this._baseType);
/* 150 */     if (result != null) {
/* 151 */       return result;
/*     */     }
/*     */     
/* 154 */     if (p.getCurrentToken() == JsonToken.START_ARRAY) {
/* 155 */       return super.deserializeTypedFromAny(p, ctxt);
/*     */     }
/* 157 */     ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, "missing property '" + this._typePropertyName + "' that is to contain type id  (for class " + baseTypeName() + ")", new Object[0]);
/*     */     
/* 159 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeTypedFromAny(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 171 */     if (p.getCurrentToken() == JsonToken.START_ARRAY) {
/* 172 */       return super.deserializeTypedFromArray(p, ctxt);
/*     */     }
/* 174 */     return deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\AsPropertyTypeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */