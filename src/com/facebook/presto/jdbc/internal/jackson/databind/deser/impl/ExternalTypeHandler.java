/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalTypeHandler
/*     */ {
/*     */   private final ExtTypedProperty[] _properties;
/*     */   private final HashMap<String, Integer> _nameToPropertyIndex;
/*     */   private final String[] _typeIds;
/*     */   private final TokenBuffer[] _tokens;
/*     */   
/*     */   protected ExternalTypeHandler(ExtTypedProperty[] properties, HashMap<String, Integer> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens)
/*     */   {
/*  31 */     this._properties = properties;
/*  32 */     this._nameToPropertyIndex = nameToPropertyIndex;
/*  33 */     this._typeIds = typeIds;
/*  34 */     this._tokens = tokens;
/*     */   }
/*     */   
/*     */   protected ExternalTypeHandler(ExternalTypeHandler h)
/*     */   {
/*  39 */     this._properties = h._properties;
/*  40 */     this._nameToPropertyIndex = h._nameToPropertyIndex;
/*  41 */     int len = this._properties.length;
/*  42 */     this._typeIds = new String[len];
/*  43 */     this._tokens = new TokenBuffer[len];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExternalTypeHandler start()
/*     */   {
/*  51 */     return new ExternalTypeHandler(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean)
/*     */     throws IOException
/*     */   {
/*  64 */     Integer I = (Integer)this._nameToPropertyIndex.get(propName);
/*  65 */     if (I == null) {
/*  66 */       return false;
/*     */     }
/*  68 */     int index = I.intValue();
/*  69 */     ExtTypedProperty prop = this._properties[index];
/*  70 */     if (!prop.hasTypePropertyName(propName)) {
/*  71 */       return false;
/*     */     }
/*  73 */     String typeId = p.getText();
/*     */     
/*  75 */     boolean canDeserialize = (bean != null) && (this._tokens[index] != null);
/*     */     
/*  77 */     if (canDeserialize) {
/*  78 */       _deserializeAndSet(p, ctxt, bean, index, typeId);
/*     */       
/*  80 */       this._tokens[index] = null;
/*     */     } else {
/*  82 */       this._typeIds[index] = typeId;
/*     */     }
/*  84 */     return true;
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
/*     */   public boolean handlePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean)
/*     */     throws IOException
/*     */   {
/*  98 */     Integer I = (Integer)this._nameToPropertyIndex.get(propName);
/*  99 */     if (I == null) {
/* 100 */       return false;
/*     */     }
/* 102 */     int index = I.intValue();
/* 103 */     ExtTypedProperty prop = this._properties[index];
/*     */     boolean canDeserialize;
/* 105 */     boolean canDeserialize; if (prop.hasTypePropertyName(propName)) {
/* 106 */       this._typeIds[index] = p.getText();
/* 107 */       p.skipChildren();
/* 108 */       canDeserialize = (bean != null) && (this._tokens[index] != null);
/*     */     }
/*     */     else {
/* 111 */       TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 112 */       tokens.copyCurrentStructure(p);
/* 113 */       this._tokens[index] = tokens;
/* 114 */       canDeserialize = (bean != null) && (this._typeIds[index] != null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 119 */     if (canDeserialize) {
/* 120 */       String typeId = this._typeIds[index];
/*     */       
/* 122 */       this._typeIds[index] = null;
/* 123 */       _deserializeAndSet(p, ctxt, bean, index, typeId);
/* 124 */       this._tokens[index] = null;
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object complete(JsonParser p, DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 137 */     int i = 0; for (int len = this._properties.length; i < len; i++) {
/* 138 */       String typeId = this._typeIds[i];
/* 139 */       if (typeId == null) {
/* 140 */         TokenBuffer tokens = this._tokens[i];
/*     */         
/*     */ 
/* 143 */         if (tokens == null) {
/*     */           continue;
/*     */         }
/*     */         
/*     */ 
/* 148 */         JsonToken t = tokens.firstToken();
/* 149 */         if ((t != null) && (t.isScalarValue())) {
/* 150 */           JsonParser buffered = tokens.asParser(p);
/* 151 */           buffered.nextToken();
/* 152 */           SettableBeanProperty extProp = this._properties[i].getProperty();
/* 153 */           Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
/* 154 */           if (result != null) {
/* 155 */             extProp.set(bean, result);
/* 156 */             continue;
/*     */           }
/*     */           
/* 159 */           if (!this._properties[i].hasDefaultType()) {
/* 160 */             ctxt.reportMappingException("Missing external type id property '%s'", new Object[] { this._properties[i].getTypePropertyName() });
/*     */           }
/*     */           else {
/* 163 */             typeId = this._properties[i].getDefaultTypeId();
/*     */           }
/*     */         }
/* 166 */       } else if (this._tokens[i] == null) {
/* 167 */         SettableBeanProperty prop = this._properties[i].getProperty();
/* 168 */         ctxt.reportMappingException("Missing property '%s' for external type id '%s'", new Object[] { prop.getName(), this._properties[i].getTypePropertyName() });
/*     */       }
/*     */       
/* 171 */       _deserializeAndSet(p, ctxt, bean, i, typeId);
/*     */     }
/* 173 */     return bean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object complete(JsonParser p, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator)
/*     */     throws IOException
/*     */   {
/* 185 */     int len = this._properties.length;
/* 186 */     Object[] values = new Object[len];
/* 187 */     for (int i = 0; i < len; i++) {
/* 188 */       String typeId = this._typeIds[i];
/* 189 */       ExtTypedProperty extProp = this._properties[i];
/*     */       
/* 191 */       if (typeId == null)
/*     */       {
/* 193 */         if (this._tokens[i] == null) {
/*     */           continue;
/*     */         }
/*     */         
/*     */ 
/* 198 */         if (!extProp.hasDefaultType()) {
/* 199 */           ctxt.reportMappingException("Missing external type id property '%s'", new Object[] { extProp.getTypePropertyName() });
/*     */         }
/*     */         else {
/* 202 */           typeId = extProp.getDefaultTypeId();
/*     */         }
/* 204 */       } else if (this._tokens[i] == null) {
/* 205 */         SettableBeanProperty prop = extProp.getProperty();
/* 206 */         ctxt.reportMappingException("Missing property '%s' for external type id '%s'", new Object[] { prop.getName(), this._properties[i].getTypePropertyName() });
/*     */       }
/*     */       
/* 209 */       values[i] = _deserialize(p, ctxt, i, typeId);
/*     */       
/* 211 */       SettableBeanProperty prop = extProp.getProperty();
/*     */       
/* 213 */       if (prop.getCreatorIndex() >= 0) {
/* 214 */         buffer.assignParameter(prop, values[i]);
/*     */         
/*     */ 
/* 217 */         SettableBeanProperty typeProp = extProp.getTypeProperty();
/*     */         
/* 219 */         if ((typeProp != null) && (typeProp.getCreatorIndex() >= 0)) {
/* 220 */           buffer.assignParameter(typeProp, typeId);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 225 */     Object bean = creator.build(ctxt, buffer);
/*     */     
/* 227 */     for (int i = 0; i < len; i++) {
/* 228 */       SettableBeanProperty prop = this._properties[i].getProperty();
/* 229 */       if (prop.getCreatorIndex() < 0) {
/* 230 */         prop.set(bean, values[i]);
/*     */       }
/*     */     }
/* 233 */     return bean;
/*     */   }
/*     */   
/*     */ 
/*     */   protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, int index, String typeId)
/*     */     throws IOException
/*     */   {
/* 240 */     JsonParser p2 = this._tokens[index].asParser(p);
/* 241 */     JsonToken t = p2.nextToken();
/*     */     
/* 243 */     if (t == JsonToken.VALUE_NULL) {
/* 244 */       return null;
/*     */     }
/* 246 */     TokenBuffer merged = new TokenBuffer(p, ctxt);
/* 247 */     merged.writeStartArray();
/* 248 */     merged.writeString(typeId);
/* 249 */     merged.copyCurrentStructure(p2);
/* 250 */     merged.writeEndArray();
/*     */     
/*     */ 
/* 253 */     JsonParser mp = merged.asParser(p);
/* 254 */     mp.nextToken();
/* 255 */     return this._properties[index].getProperty().deserialize(mp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, int index, String typeId)
/*     */     throws IOException
/*     */   {
/* 265 */     JsonParser p2 = this._tokens[index].asParser(p);
/* 266 */     JsonToken t = p2.nextToken();
/*     */     
/* 268 */     if (t == JsonToken.VALUE_NULL) {
/* 269 */       this._properties[index].getProperty().set(bean, null);
/* 270 */       return;
/*     */     }
/* 272 */     TokenBuffer merged = new TokenBuffer(p, ctxt);
/* 273 */     merged.writeStartArray();
/* 274 */     merged.writeString(typeId);
/*     */     
/* 276 */     merged.copyCurrentStructure(p2);
/* 277 */     merged.writeEndArray();
/*     */     
/* 279 */     JsonParser mp = merged.asParser(p);
/* 280 */     mp.nextToken();
/* 281 */     this._properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class Builder
/*     */   {
/* 292 */     private final ArrayList<ExtTypedProperty> _properties = new ArrayList();
/* 293 */     private final HashMap<String, Integer> _nameToPropertyIndex = new HashMap();
/*     */     
/*     */     public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser)
/*     */     {
/* 297 */       Integer index = Integer.valueOf(this._properties.size());
/* 298 */       this._properties.add(new ExtTypedProperty(property, typeDeser));
/* 299 */       this._nameToPropertyIndex.put(property.getName(), index);
/* 300 */       this._nameToPropertyIndex.put(typeDeser.getPropertyName(), index);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ExternalTypeHandler build(BeanPropertyMap otherProps)
/*     */     {
/* 312 */       int len = this._properties.size();
/* 313 */       ExtTypedProperty[] extProps = new ExtTypedProperty[len];
/* 314 */       for (int i = 0; i < len; i++) {
/* 315 */         ExtTypedProperty extProp = (ExtTypedProperty)this._properties.get(i);
/* 316 */         String typePropId = extProp.getTypePropertyName();
/* 317 */         SettableBeanProperty typeProp = otherProps.find(typePropId);
/* 318 */         if (typeProp != null) {
/* 319 */           extProp.linkTypeProperty(typeProp);
/*     */         }
/* 321 */         extProps[i] = extProp;
/*     */       }
/* 323 */       return new ExternalTypeHandler(extProps, this._nameToPropertyIndex, null, null);
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public ExternalTypeHandler build() {
/* 328 */       return new ExternalTypeHandler((ExtTypedProperty[])this._properties.toArray(new ExtTypedProperty[this._properties.size()]), this._nameToPropertyIndex, null, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class ExtTypedProperty
/*     */   {
/*     */     private final SettableBeanProperty _property;
/*     */     
/*     */     private final TypeDeserializer _typeDeserializer;
/*     */     
/*     */     private final String _typePropertyName;
/*     */     
/*     */     private SettableBeanProperty _typeProperty;
/*     */     
/*     */ 
/*     */     public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser)
/*     */     {
/* 346 */       this._property = property;
/* 347 */       this._typeDeserializer = typeDeser;
/* 348 */       this._typePropertyName = typeDeser.getPropertyName();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void linkTypeProperty(SettableBeanProperty p)
/*     */     {
/* 355 */       this._typeProperty = p;
/*     */     }
/*     */     
/*     */     public boolean hasTypePropertyName(String n) {
/* 359 */       return n.equals(this._typePropertyName);
/*     */     }
/*     */     
/*     */     public boolean hasDefaultType() {
/* 363 */       return this._typeDeserializer.getDefaultImpl() != null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getDefaultTypeId()
/*     */     {
/* 372 */       Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
/* 373 */       if (defaultType == null) {
/* 374 */         return null;
/*     */       }
/* 376 */       return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
/*     */     }
/*     */     
/* 379 */     public String getTypePropertyName() { return this._typePropertyName; }
/*     */     
/*     */     public SettableBeanProperty getProperty() {
/* 382 */       return this._property;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public SettableBeanProperty getTypeProperty()
/*     */     {
/* 389 */       return this._typeProperty;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\ExternalTypeHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */