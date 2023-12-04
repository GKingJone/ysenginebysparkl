/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableAnyProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import java.io.IOException;
/*     */ import java.util.BitSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertyValueBuffer
/*     */ {
/*     */   protected final JsonParser _parser;
/*     */   protected final DeserializationContext _context;
/*     */   protected final ObjectIdReader _objectIdReader;
/*     */   protected final Object[] _creatorParameters;
/*     */   protected int _paramsNeeded;
/*     */   protected int _paramsSeen;
/*     */   protected final BitSet _paramsSeenBig;
/*     */   protected PropertyValue _buffered;
/*     */   protected Object _idValue;
/*     */   
/*     */   public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir)
/*     */   {
/*  88 */     this._parser = p;
/*  89 */     this._context = ctxt;
/*  90 */     this._paramsNeeded = paramCount;
/*  91 */     this._objectIdReader = oir;
/*  92 */     this._creatorParameters = new Object[paramCount];
/*  93 */     if (paramCount < 32) {
/*  94 */       this._paramsSeenBig = null;
/*     */     } else {
/*  96 */       this._paramsSeenBig = new BitSet();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean hasParameter(SettableBeanProperty prop)
/*     */   {
/* 108 */     if (this._paramsSeenBig == null) {
/* 109 */       return (this._paramsSeen >> prop.getCreatorIndex() & 0x1) == 1;
/*     */     }
/* 111 */     return this._paramsSeenBig.get(prop.getCreatorIndex());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getParameter(SettableBeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/*     */     Object value;
/*     */     
/*     */ 
/*     */ 
/*     */     Object value;
/*     */     
/*     */ 
/*     */ 
/* 129 */     if (hasParameter(prop)) {
/* 130 */       value = this._creatorParameters[prop.getCreatorIndex()];
/*     */     } else {
/* 132 */       value = this._creatorParameters[prop.getCreatorIndex()] = _findMissing(prop);
/*     */     }
/* 134 */     if ((value == null) && (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES))) {
/* 135 */       throw this._context.mappingException("Null value for creator property '%s'; DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS enabled", new Object[] { prop.getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/*     */     
/*     */ 
/* 139 */     return value;
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
/*     */   public Object[] getParameters(SettableBeanProperty[] props)
/*     */     throws JsonMappingException
/*     */   {
/* 153 */     if (this._paramsNeeded > 0) {
/* 154 */       if (this._paramsSeenBig == null) {
/* 155 */         int mask = this._paramsSeen;
/*     */         
/*     */ 
/* 158 */         int ix = 0; for (int len = this._creatorParameters.length; ix < len; mask >>= 1) {
/* 159 */           if ((mask & 0x1) == 0) {
/* 160 */             this._creatorParameters[ix] = _findMissing(props[ix]);
/*     */           }
/* 158 */           ix++;
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 164 */         int len = this._creatorParameters.length;
/* 165 */         for (int ix = 0; (ix = this._paramsSeenBig.nextClearBit(ix)) < len; ix++) {
/* 166 */           this._creatorParameters[ix] = _findMissing(props[ix]);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 171 */     if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
/* 172 */       for (int ix = 0; ix < props.length; ix++) {
/* 173 */         if (this._creatorParameters[ix] == null) {
/* 174 */           this._context.reportMappingException("Null value for creator property '%s'; DeserializationFeature.FAIL_ON_NULL_FOR_CREATOR_PARAMETERS enabled", new Object[] { props[ix].getName(), Integer.valueOf(props[ix].getCreatorIndex()) });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 180 */     return this._creatorParameters;
/*     */   }
/*     */   
/*     */   protected Object _findMissing(SettableBeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 186 */     Object injectableValueId = prop.getInjectableValueId();
/* 187 */     if (injectableValueId != null) {
/* 188 */       return this._context.findInjectableValue(prop.getInjectableValueId(), prop, null);
/*     */     }
/*     */     
/*     */ 
/* 192 */     if (prop.isRequired()) {
/* 193 */       this._context.reportMappingException("Missing required creator property '%s' (index %d)", new Object[] { prop.getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/*     */     
/* 196 */     if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
/* 197 */       this._context.reportMappingException("Missing creator property '%s' (index %d); DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES enabled", new Object[] { prop.getName(), Integer.valueOf(prop.getCreatorIndex()) });
/*     */     }
/*     */     
/*     */ 
/* 201 */     JsonDeserializer<Object> deser = prop.getValueDeserializer();
/* 202 */     return deser.getNullValue(this._context);
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
/*     */   public boolean readIdProperty(String propName)
/*     */     throws IOException
/*     */   {
/* 219 */     if ((this._objectIdReader != null) && (propName.equals(this._objectIdReader.propertyName.getSimpleName()))) {
/* 220 */       this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
/* 221 */       return true;
/*     */     }
/* 223 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object handleIdValue(DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 231 */     if (this._objectIdReader != null) {
/* 232 */       if (this._idValue != null) {
/* 233 */         ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
/* 234 */         roid.bindItem(bean);
/*     */         
/* 236 */         SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 237 */         if (idProp != null) {
/* 238 */           return idProp.setAndReturn(bean, this._idValue);
/*     */         }
/*     */       }
/*     */       else {
/* 242 */         ctxt.reportUnresolvedObjectId(this._objectIdReader, bean);
/*     */       }
/*     */     }
/* 245 */     return bean;
/*     */   }
/*     */   
/* 248 */   protected PropertyValue buffered() { return this._buffered; }
/*     */   
/* 250 */   public boolean isComplete() { return this._paramsNeeded <= 0; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean assignParameter(SettableBeanProperty prop, Object value)
/*     */   {
/* 259 */     int ix = prop.getCreatorIndex();
/* 260 */     this._creatorParameters[ix] = value;
/*     */     
/* 262 */     if (this._paramsSeenBig == null) {
/* 263 */       int old = this._paramsSeen;
/* 264 */       int newValue = old | 1 << ix;
/* 265 */       if (old != newValue) {
/* 266 */         this._paramsSeen = newValue;
/* 267 */         if (--this._paramsNeeded <= 0) {
/* 268 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 272 */     else if (!this._paramsSeenBig.get(ix)) {
/* 273 */       if (--this._paramsNeeded <= 0) {
/* 274 */         return true;
/*     */       }
/* 276 */       this._paramsSeenBig.set(ix);
/*     */     }
/*     */     
/* 279 */     return false;
/*     */   }
/*     */   
/*     */   public void bufferProperty(SettableBeanProperty prop, Object value) {
/* 283 */     this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
/*     */   }
/*     */   
/*     */   public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
/* 287 */     this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
/*     */   }
/*     */   
/*     */   public void bufferMapProperty(Object key, Object value) {
/* 291 */     this._buffered = new PropertyValue.Map(this._buffered, value, key);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\PropertyValueBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */