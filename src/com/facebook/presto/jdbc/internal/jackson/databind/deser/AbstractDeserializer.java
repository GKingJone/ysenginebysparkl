/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
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
/*     */ public class AbstractDeserializer
/*     */   extends JsonDeserializer<Object>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _baseType;
/*     */   protected final ObjectIdReader _objectIdReader;
/*     */   protected final Map<String, SettableBeanProperty> _backRefProperties;
/*     */   protected final boolean _acceptString;
/*     */   protected final boolean _acceptBoolean;
/*     */   protected final boolean _acceptInt;
/*     */   protected final boolean _acceptDouble;
/*     */   
/*     */   public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps)
/*     */   {
/*  40 */     this._baseType = beanDesc.getType();
/*  41 */     this._objectIdReader = builder.getObjectIdReader();
/*  42 */     this._backRefProperties = backRefProps;
/*  43 */     Class<?> cls = this._baseType.getRawClass();
/*  44 */     this._acceptString = cls.isAssignableFrom(String.class);
/*  45 */     this._acceptBoolean = ((cls == Boolean.TYPE) || (cls.isAssignableFrom(Boolean.class)));
/*  46 */     this._acceptInt = ((cls == Integer.TYPE) || (cls.isAssignableFrom(Integer.class)));
/*  47 */     this._acceptDouble = ((cls == Double.TYPE) || (cls.isAssignableFrom(Double.class)));
/*     */   }
/*     */   
/*     */   protected AbstractDeserializer(BeanDescription beanDesc)
/*     */   {
/*  52 */     this._baseType = beanDesc.getType();
/*  53 */     this._objectIdReader = null;
/*  54 */     this._backRefProperties = null;
/*  55 */     Class<?> cls = this._baseType.getRawClass();
/*  56 */     this._acceptString = cls.isAssignableFrom(String.class);
/*  57 */     this._acceptBoolean = ((cls == Boolean.TYPE) || (cls.isAssignableFrom(Boolean.class)));
/*  58 */     this._acceptInt = ((cls == Integer.TYPE) || (cls.isAssignableFrom(Integer.class)));
/*  59 */     this._acceptDouble = ((cls == Double.TYPE) || (cls.isAssignableFrom(Double.class)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static AbstractDeserializer constructForNonPOJO(BeanDescription beanDesc)
/*     */   {
/*  69 */     return new AbstractDeserializer(beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> handledType()
/*     */   {
/*  80 */     return this._baseType.getRawClass();
/*     */   }
/*     */   
/*     */   public boolean isCachable() {
/*  84 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ObjectIdReader getObjectIdReader()
/*     */   {
/*  93 */     return this._objectIdReader;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SettableBeanProperty findBackReference(String logicalName)
/*     */   {
/* 102 */     return this._backRefProperties == null ? null : (SettableBeanProperty)this._backRefProperties.get(logicalName);
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
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 118 */     if (this._objectIdReader != null) {
/* 119 */       JsonToken t = p.getCurrentToken();
/* 120 */       if (t != null)
/*     */       {
/* 122 */         if (t.isScalarValue()) {
/* 123 */           return _deserializeFromObjectId(p, ctxt);
/*     */         }
/*     */         
/* 126 */         if (t == JsonToken.START_OBJECT) {
/* 127 */           t = p.nextToken();
/*     */         }
/* 129 */         if ((t == JsonToken.FIELD_NAME) && (this._objectIdReader.maySerializeAsObject()) && (this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)))
/*     */         {
/* 131 */           return _deserializeFromObjectId(p, ctxt);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 138 */     Object result = _deserializeIfNatural(p, ctxt);
/* 139 */     if (result != null) {
/* 140 */       return result;
/*     */     }
/* 142 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 149 */     return ctxt.handleMissingInstantiator(this._baseType.getRawClass(), p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
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
/*     */   protected Object _deserializeIfNatural(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 167 */     switch (p.getCurrentTokenId()) {
/*     */     case 6: 
/* 169 */       if (this._acceptString) {
/* 170 */         return p.getText();
/*     */       }
/*     */       break;
/*     */     case 7: 
/* 174 */       if (this._acceptInt) {
/* 175 */         return Integer.valueOf(p.getIntValue());
/*     */       }
/*     */       break;
/*     */     case 8: 
/* 179 */       if (this._acceptDouble) {
/* 180 */         return Double.valueOf(p.getDoubleValue());
/*     */       }
/*     */       break;
/*     */     case 9: 
/* 184 */       if (this._acceptBoolean) {
/* 185 */         return Boolean.TRUE;
/*     */       }
/*     */       break;
/*     */     case 10: 
/* 189 */       if (this._acceptBoolean) {
/* 190 */         return Boolean.FALSE;
/*     */       }
/*     */       break;
/*     */     }
/* 194 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object _deserializeFromObjectId(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 203 */     Object id = this._objectIdReader.readObjectReference(p, ctxt);
/* 204 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*     */     
/* 206 */     Object pojo = roid.resolve();
/* 207 */     if (pojo == null) {
/* 208 */       throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p.getCurrentLocation(), roid);
/*     */     }
/*     */     
/* 211 */     return pojo;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\AbstractDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */