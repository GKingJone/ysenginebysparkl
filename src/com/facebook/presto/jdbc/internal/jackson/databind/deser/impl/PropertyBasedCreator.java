/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
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
/*     */ public final class PropertyBasedCreator
/*     */ {
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final HashMap<String, SettableBeanProperty> _propertyLookup;
/*     */   protected final int _propertyCount;
/*     */   protected final SettableBeanProperty[] _allProperties;
/*     */   
/*     */   protected PropertyBasedCreator(ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps)
/*     */   {
/*  54 */     this._valueInstantiator = valueInstantiator;
/*  55 */     this._propertyLookup = new HashMap();
/*  56 */     int len = creatorProps.length;
/*  57 */     this._propertyCount = len;
/*  58 */     this._allProperties = new SettableBeanProperty[len];
/*  59 */     for (int i = 0; i < len; i++) {
/*  60 */       SettableBeanProperty prop = creatorProps[i];
/*  61 */       this._allProperties[i] = prop;
/*  62 */       this._propertyLookup.put(prop.getName(), prop);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcProps)
/*     */     throws JsonMappingException
/*     */   {
/*  74 */     int len = srcProps.length;
/*  75 */     SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
/*  76 */     for (int i = 0; i < len; i++) {
/*  77 */       SettableBeanProperty prop = srcProps[i];
/*  78 */       if (!prop.hasValueDeserializer()) {
/*  79 */         prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
/*     */       }
/*  81 */       creatorProps[i] = prop;
/*     */     }
/*  83 */     return new PropertyBasedCreator(valueInstantiator, creatorProps);
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
/*     */   public Collection<SettableBeanProperty> properties()
/*     */   {
/* 101 */     return this._propertyLookup.values();
/*     */   }
/*     */   
/*     */   public SettableBeanProperty findCreatorProperty(String name) {
/* 105 */     return (SettableBeanProperty)this._propertyLookup.get(name);
/*     */   }
/*     */   
/*     */   public SettableBeanProperty findCreatorProperty(int propertyIndex) {
/* 109 */     for (SettableBeanProperty prop : this._propertyLookup.values()) {
/* 110 */       if (prop.getPropertyIndex() == propertyIndex) {
/* 111 */         return prop;
/*     */       }
/*     */     }
/* 114 */     return null;
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
/*     */   public PropertyValueBuffer startBuilding(JsonParser p, DeserializationContext ctxt, ObjectIdReader oir)
/*     */   {
/* 130 */     return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir);
/*     */   }
/*     */   
/*     */   public Object build(DeserializationContext ctxt, PropertyValueBuffer buffer) throws IOException
/*     */   {
/* 135 */     Object bean = this._valueInstantiator.createFromObjectWith(ctxt, this._allProperties, buffer);
/*     */     
/*     */ 
/* 138 */     if (bean != null)
/*     */     {
/* 140 */       bean = buffer.handleIdValue(ctxt, bean);
/*     */       
/*     */ 
/* 143 */       for (PropertyValue pv = buffer.buffered(); pv != null; pv = pv.next) {
/* 144 */         pv.assign(bean);
/*     */       }
/*     */     }
/* 147 */     return bean;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\PropertyBasedCreator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */