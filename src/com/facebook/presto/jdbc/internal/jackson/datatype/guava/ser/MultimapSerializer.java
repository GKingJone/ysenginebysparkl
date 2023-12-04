/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Multimap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContainerSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.PropertyFilter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.MapProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ public class MultimapSerializer
/*     */   extends ContainerSerializer<Multimap<?, ?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final MapLikeType _type;
/*     */   private final BeanProperty _property;
/*     */   private final JsonSerializer<Object> _keySerializer;
/*     */   private final TypeSerializer _valueTypeSerializer;
/*     */   private final JsonSerializer<Object> _valueSerializer;
/*     */   protected final Set<String> _ignoredEntries;
/*     */   protected PropertySerializerMap _dynamicValueSerializers;
/*     */   protected final Object _filterId;
/*     */   protected final boolean _sortKeys;
/*     */   
/*     */   public MultimapSerializer(MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer, Set<String> ignoredEntries, Object filterId)
/*     */   {
/*  86 */     super(type.getRawClass(), false);
/*  87 */     this._type = type;
/*  88 */     this._property = null;
/*  89 */     this._keySerializer = keySerializer;
/*  90 */     this._valueTypeSerializer = vts;
/*  91 */     this._valueSerializer = valueSerializer;
/*  92 */     this._ignoredEntries = ignoredEntries;
/*  93 */     this._filterId = filterId;
/*  94 */     this._sortKeys = false;
/*     */     
/*  96 */     this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MultimapSerializer(MultimapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries, Object filterId, boolean sortKeys)
/*     */   {
/* 107 */     super(src);
/* 108 */     this._type = src._type;
/* 109 */     this._property = property;
/* 110 */     this._keySerializer = keySerializer;
/* 111 */     this._valueTypeSerializer = vts;
/* 112 */     this._valueSerializer = valueSerializer;
/* 113 */     this._dynamicValueSerializers = src._dynamicValueSerializers;
/* 114 */     this._ignoredEntries = ignoredEntries;
/* 115 */     this._filterId = filterId;
/* 116 */     this._sortKeys = sortKeys;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected MultimapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer, Set<String> ignored, Object filterId, boolean sortKeys)
/*     */   {
/* 123 */     return new MultimapSerializer(this, property, keySer, vts, valueSer, ignored, filterId, sortKeys);
/*     */   }
/*     */   
/*     */ 
/*     */   protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer)
/*     */   {
/* 129 */     return new MultimapSerializer(this, this._property, this._keySerializer, typeSer, this._valueSerializer, this._ignoredEntries, this._filterId, this._sortKeys);
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
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 143 */     JsonSerializer<?> valueSer = this._valueSerializer;
/* 144 */     if (valueSer == null) {
/* 145 */       JavaType valueType = this._type.getContentType();
/* 146 */       if (valueType.isFinal()) {
/* 147 */         valueSer = provider.findValueSerializer(valueType, property);
/*     */       }
/* 149 */     } else if ((valueSer instanceof ContextualSerializer)) {
/* 150 */       valueSer = ((ContextualSerializer)valueSer).createContextual(provider, property);
/*     */     }
/*     */     
/* 153 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 154 */     AnnotatedMember propertyAcc = property == null ? null : property.getMember();
/* 155 */     JsonSerializer<?> keySer = null;
/* 156 */     Object filterId = this._filterId;
/*     */     
/*     */ 
/* 159 */     if ((propertyAcc != null) && (intr != null)) {
/* 160 */       Object serDef = intr.findKeySerializer(propertyAcc);
/* 161 */       if (serDef != null) {
/* 162 */         keySer = provider.serializerInstance(propertyAcc, serDef);
/*     */       }
/* 164 */       serDef = intr.findContentSerializer(propertyAcc);
/* 165 */       if (serDef != null) {
/* 166 */         valueSer = provider.serializerInstance(propertyAcc, serDef);
/*     */       }
/* 168 */       filterId = intr.findFilterId(propertyAcc);
/*     */     }
/* 170 */     if (valueSer == null) {
/* 171 */       valueSer = this._valueSerializer;
/*     */     }
/*     */     
/* 174 */     valueSer = findConvertingContentSerializer(provider, property, valueSer);
/* 175 */     if (valueSer == null)
/*     */     {
/*     */ 
/* 178 */       JavaType valueType = this._type.getContentType();
/* 179 */       if (valueType.useStaticType()) {
/* 180 */         valueSer = provider.findValueSerializer(valueType, property);
/*     */       }
/*     */     } else {
/* 183 */       valueSer = provider.handleSecondaryContextualization(valueSer, property);
/*     */     }
/* 185 */     if (keySer == null) {
/* 186 */       keySer = this._keySerializer;
/*     */     }
/* 188 */     if (keySer == null) {
/* 189 */       keySer = provider.findKeySerializer(this._type.getKeyType(), property);
/*     */     } else {
/* 191 */       keySer = provider.handleSecondaryContextualization(keySer, property);
/*     */     }
/*     */     
/* 194 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 195 */     if (typeSer != null) {
/* 196 */       typeSer = typeSer.forProperty(property);
/*     */     }
/*     */     
/* 199 */     Set<String> ignored = this._ignoredEntries;
/* 200 */     boolean sortKeys = false;
/*     */     
/* 202 */     if ((intr != null) && (propertyAcc != null)) {
/* 203 */       JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(propertyAcc);
/* 204 */       if (ignorals != null) {
/* 205 */         Set<String> newIgnored = ignorals.findIgnoredForSerialization();
/* 206 */         if ((newIgnored != null) && (!newIgnored.isEmpty())) {
/* 207 */           ignored = ignored == null ? new HashSet() : new HashSet(ignored);
/* 208 */           for (String str : newIgnored) {
/* 209 */             ignored.add(str);
/*     */           }
/*     */         }
/*     */       }
/* 213 */       Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
/* 214 */       sortKeys = (b != null) && (b.booleanValue());
/*     */     }
/*     */     
/*     */ 
/* 218 */     JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
/* 219 */     if (format != null) {
/* 220 */       Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
/* 221 */       if (B != null) {
/* 222 */         sortKeys = B.booleanValue();
/*     */       }
/*     */     }
/* 225 */     return withResolved(property, keySer, typeSer, valueSer, ignored, filterId, sortKeys);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> getContentSerializer()
/*     */   {
/* 237 */     return this._valueSerializer;
/*     */   }
/*     */   
/*     */   public JavaType getContentType()
/*     */   {
/* 242 */     return this._type.getContentType();
/*     */   }
/*     */   
/*     */   public boolean hasSingleElement(Multimap<?, ?> map)
/*     */   {
/* 247 */     return map.size() == 1;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEmpty(Multimap<?, ?> value)
/*     */   {
/* 253 */     return isEmpty(null, value);
/*     */   }
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, Multimap<?, ?> value)
/*     */   {
/* 258 */     return value.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialize(Multimap<?, ?> value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 271 */     gen.writeStartObject();
/*     */     
/* 273 */     gen.setCurrentValue(value);
/* 274 */     if (!value.isEmpty()) {
/* 275 */       if (this._filterId != null) {
/* 276 */         serializeFilteredFields(value, gen, provider);
/*     */       } else {
/* 278 */         serializeFields(value, gen, provider);
/*     */       }
/*     */     }
/* 281 */     gen.writeEndObject();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Multimap<?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 289 */     typeSer.writeTypePrefixForObject(value, gen);
/* 290 */     gen.setCurrentValue(value);
/* 291 */     if (!value.isEmpty()) {
/* 292 */       if (this._filterId != null) {
/* 293 */         serializeFilteredFields(value, gen, provider);
/*     */       } else {
/* 295 */         serializeFields(value, gen, provider);
/*     */       }
/*     */     }
/* 298 */     typeSer.writeTypeSuffixForObject(value, gen);
/*     */   }
/*     */   
/*     */   private final void serializeFields(Multimap<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 304 */     Set<String> ignored = this._ignoredEntries;
/* 305 */     PropertySerializerMap serializers = this._dynamicValueSerializers;
/* 306 */     for (Entry<?, ? extends Collection<?>> entry : mmap.asMap().entrySet())
/*     */     {
/* 308 */       Object key = entry.getKey();
/* 309 */       if ((ignored == null) || (!ignored.contains(key)))
/*     */       {
/*     */ 
/* 312 */         if (key == null) {
/* 313 */           provider.findNullKeySerializer(this._type.getKeyType(), this._property).serialize(null, gen, provider);
/*     */         }
/*     */         else {
/* 316 */           this._keySerializer.serialize(key, gen, provider);
/*     */         }
/*     */         
/* 319 */         gen.writeStartArray();
/* 320 */         for (Object vv : (Collection)entry.getValue()) {
/* 321 */           if (vv == null) {
/* 322 */             provider.defaultSerializeNull(gen);
/*     */           }
/*     */           else {
/* 325 */             JsonSerializer<Object> valueSer = this._valueSerializer;
/* 326 */             if (valueSer == null) {
/* 327 */               Class<?> cc = vv.getClass();
/* 328 */               valueSer = serializers.serializerFor(cc);
/* 329 */               if (valueSer == null) {
/* 330 */                 valueSer = _findAndAddDynamic(serializers, cc, provider);
/* 331 */                 serializers = this._dynamicValueSerializers;
/*     */               }
/*     */             }
/* 334 */             if (this._valueTypeSerializer == null) {
/* 335 */               valueSer.serialize(vv, gen, provider);
/*     */             } else
/* 337 */               valueSer.serializeWithType(vv, gen, provider, this._valueTypeSerializer);
/*     */           }
/*     */         }
/* 340 */         gen.writeEndArray();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final void serializeFilteredFields(Multimap<?, ?> mmap, JsonGenerator gen, SerializerProvider provider) throws IOException
/*     */   {
/* 347 */     Set<String> ignored = this._ignoredEntries;
/* 348 */     PropertyFilter filter = findPropertyFilter(provider, this._filterId, mmap);
/* 349 */     MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
/* 350 */     for (Entry<?, ? extends Collection<?>> entry : mmap.asMap().entrySet())
/*     */     {
/* 352 */       Object key = entry.getKey();
/* 353 */       if ((ignored == null) || (!ignored.contains(key)))
/*     */       {
/*     */ 
/* 356 */         Collection<?> value = (Collection)entry.getValue();
/*     */         JsonSerializer<Object> valueSer;
/* 358 */         JsonSerializer<Object> valueSer; if (value == null)
/*     */         {
/* 360 */           valueSer = provider.getDefaultNullValueSerializer();
/*     */         } else {
/* 362 */           valueSer = this._valueSerializer;
/*     */         }
/* 364 */         prop.reset(key, this._keySerializer, valueSer);
/*     */         try {
/* 366 */           filter.serializeAsField(value, gen, provider, prop);
/*     */         } catch (Exception e) {
/* 368 */           String keyDesc = "" + key;
/* 369 */           wrapAndThrow(provider, e, value, keyDesc);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 384 */     JsonMapFormatVisitor v2 = visitor == null ? null : visitor.expectMapFormat(typeHint);
/* 385 */     if (v2 != null) {
/* 386 */       v2.keyFormat(this._keySerializer, this._type.getKeyType());
/* 387 */       JsonSerializer<?> valueSer = this._valueSerializer;
/* 388 */       JavaType vt = this._type.getContentType();
/* 389 */       if (valueSer == null) {
/* 390 */         valueSer = _findAndAddDynamic(this._dynamicValueSerializers, vt, visitor.getProvider());
/*     */       }
/*     */       
/* 393 */       v2.valueFormat(valueSer, vt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 406 */     SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/*     */     
/* 408 */     if (map != result.map) {
/* 409 */       this._dynamicValueSerializers = result.map;
/*     */     }
/* 411 */     return result.serializer;
/*     */   }
/*     */   
/*     */   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 417 */     SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
/* 418 */     if (map != result.map) {
/* 419 */       this._dynamicValueSerializers = result.map;
/*     */     }
/* 421 */     return result.serializer;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\MultimapSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */