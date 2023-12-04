/*      */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ContextAttributes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.FilterProvider;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ResolvableSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.SerializerCache;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.SerializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.FailingSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.TypeWrappedSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.UnknownSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.WritableObjectId;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.NullSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import java.io.IOException;
/*      */ import java.text.DateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class SerializerProvider
/*      */   extends DatabindContext
/*      */ {
/*      */   protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
/*   54 */   public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   66 */   protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final SerializationConfig _config;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Class<?> _serializationView;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final SerializerFactory _serializerFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final SerializerCache _serializerCache;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected transient ContextAttributes _attributes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  128 */   protected JsonSerializer<Object> _unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonSerializer<Object> _keySerializer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  141 */   protected JsonSerializer<Object> _nullValueSerializer = NullSerializer.instance;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  150 */   protected JsonSerializer<Object> _nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ReadOnlyClassToSerializerMap _knownSerializers;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DateFormat _dateFormat;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _stdNullValueSerializer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SerializerProvider()
/*      */   {
/*  191 */     this._config = null;
/*  192 */     this._serializerFactory = null;
/*  193 */     this._serializerCache = new SerializerCache();
/*      */     
/*  195 */     this._knownSerializers = null;
/*      */     
/*  197 */     this._serializationView = null;
/*  198 */     this._attributes = null;
/*      */     
/*      */ 
/*  201 */     this._stdNullValueSerializer = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f)
/*      */   {
/*  213 */     if (config == null) {
/*  214 */       throw new NullPointerException();
/*      */     }
/*  216 */     this._serializerFactory = f;
/*  217 */     this._config = config;
/*      */     
/*  219 */     this._serializerCache = src._serializerCache;
/*  220 */     this._unknownTypeSerializer = src._unknownTypeSerializer;
/*  221 */     this._keySerializer = src._keySerializer;
/*  222 */     this._nullValueSerializer = src._nullValueSerializer;
/*  223 */     this._nullKeySerializer = src._nullKeySerializer;
/*      */     
/*  225 */     this._stdNullValueSerializer = (this._nullValueSerializer == DEFAULT_NULL_KEY_SERIALIZER);
/*      */     
/*  227 */     this._serializationView = config.getActiveView();
/*  228 */     this._attributes = config.getAttributes();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  233 */     this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializerProvider(SerializerProvider src)
/*      */   {
/*  244 */     this._config = null;
/*  245 */     this._serializationView = null;
/*  246 */     this._serializerFactory = null;
/*  247 */     this._knownSerializers = null;
/*      */     
/*      */ 
/*  250 */     this._serializerCache = new SerializerCache();
/*      */     
/*  252 */     this._unknownTypeSerializer = src._unknownTypeSerializer;
/*  253 */     this._keySerializer = src._keySerializer;
/*  254 */     this._nullValueSerializer = src._nullValueSerializer;
/*  255 */     this._nullKeySerializer = src._nullKeySerializer;
/*      */     
/*  257 */     this._stdNullValueSerializer = src._stdNullValueSerializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDefaultKeySerializer(JsonSerializer<Object> ks)
/*      */   {
/*  274 */     if (ks == null) {
/*  275 */       throw new IllegalArgumentException("Can not pass null JsonSerializer");
/*      */     }
/*  277 */     this._keySerializer = ks;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNullValueSerializer(JsonSerializer<Object> nvs)
/*      */   {
/*  291 */     if (nvs == null) {
/*  292 */       throw new IllegalArgumentException("Can not pass null JsonSerializer");
/*      */     }
/*  294 */     this._nullValueSerializer = nvs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNullKeySerializer(JsonSerializer<Object> nks)
/*      */   {
/*  308 */     if (nks == null) {
/*  309 */       throw new IllegalArgumentException("Can not pass null JsonSerializer");
/*      */     }
/*  311 */     this._nullKeySerializer = nks;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final SerializationConfig getConfig()
/*      */   {
/*  325 */     return this._config;
/*      */   }
/*      */   
/*      */   public final AnnotationIntrospector getAnnotationIntrospector() {
/*  329 */     return this._config.getAnnotationIntrospector();
/*      */   }
/*      */   
/*      */   public final TypeFactory getTypeFactory()
/*      */   {
/*  334 */     return this._config.getTypeFactory();
/*      */   }
/*      */   
/*      */   public final Class<?> getActiveView() {
/*  338 */     return this._serializationView;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public final Class<?> getSerializationView()
/*      */   {
/*  344 */     return this._serializationView;
/*      */   }
/*      */   
/*      */   public final boolean canOverrideAccessModifiers() {
/*  348 */     return this._config.canOverrideAccessModifiers();
/*      */   }
/*      */   
/*      */   public final boolean isEnabled(MapperFeature feature)
/*      */   {
/*  353 */     return this._config.isEnabled(feature);
/*      */   }
/*      */   
/*      */   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType)
/*      */   {
/*  358 */     return this._config.getDefaultPropertyFormat(baseType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType)
/*      */   {
/*  365 */     return this._config.getDefaultPropertyInclusion();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Locale getLocale()
/*      */   {
/*  376 */     return this._config.getLocale();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TimeZone getTimeZone()
/*      */   {
/*  387 */     return this._config.getTimeZone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getAttribute(Object key)
/*      */   {
/*  398 */     return this._attributes.getAttribute(key);
/*      */   }
/*      */   
/*      */ 
/*      */   public SerializerProvider setAttribute(Object key, Object value)
/*      */   {
/*  404 */     this._attributes = this._attributes.withPerCallAttribute(key, value);
/*  405 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean isEnabled(SerializationFeature feature)
/*      */   {
/*  423 */     return this._config.isEnabled(feature);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean hasSerializationFeatures(int featureMask)
/*      */   {
/*  433 */     return this._config.hasSerializationFeatures(featureMask);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final FilterProvider getFilterProvider()
/*      */   {
/*  444 */     return this._config.getFilterProvider();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract WritableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  490 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  491 */     if (ser == null)
/*      */     {
/*  493 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  494 */       if (ser == null)
/*      */       {
/*  496 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  497 */         if (ser == null)
/*      */         {
/*  499 */           ser = _createAndCacheUntypedSerializer(valueType);
/*      */           
/*  501 */           if (ser == null) {
/*  502 */             ser = getUnknownTypeSerializer(valueType);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  507 */             return ser;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  513 */     return handleSecondaryContextualization(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  531 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  532 */     if (ser == null) {
/*  533 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  534 */       if (ser == null) {
/*  535 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  536 */         if (ser == null) {
/*  537 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */           
/*      */ 
/*      */ 
/*  541 */           return ser;
/*      */         }
/*      */       }
/*      */     }
/*  545 */     return handleSecondaryContextualization(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findValueSerializer(Class<?> valueType)
/*      */     throws JsonMappingException
/*      */   {
/*  558 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  559 */     if (ser == null) {
/*  560 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  561 */       if (ser == null) {
/*  562 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  563 */         if (ser == null) {
/*  564 */           ser = _createAndCacheUntypedSerializer(valueType);
/*  565 */           if (ser == null) {
/*  566 */             ser = getUnknownTypeSerializer(valueType);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  574 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findValueSerializer(JavaType valueType)
/*      */     throws JsonMappingException
/*      */   {
/*  588 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  589 */     if (ser == null) {
/*  590 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  591 */       if (ser == null) {
/*  592 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  593 */         if (ser == null) {
/*  594 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  601 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findPrimaryPropertySerializer(JavaType valueType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  620 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  621 */     if (ser == null) {
/*  622 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  623 */       if (ser == null) {
/*  624 */         ser = _createAndCacheUntypedSerializer(valueType);
/*  625 */         if (ser == null) {
/*  626 */           ser = getUnknownTypeSerializer(valueType.getRawClass());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  631 */           return ser;
/*      */         }
/*      */       }
/*      */     }
/*  635 */     return handlePrimaryContextualization(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findPrimaryPropertySerializer(Class<?> valueType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  646 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
/*  647 */     if (ser == null) {
/*  648 */       ser = this._serializerCache.untypedValueSerializer(valueType);
/*  649 */       if (ser == null) {
/*  650 */         ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
/*  651 */         if (ser == null) {
/*  652 */           ser = _createAndCacheUntypedSerializer(valueType);
/*  653 */           if (ser == null) {
/*  654 */             ser = getUnknownTypeSerializer(valueType);
/*      */             
/*      */ 
/*      */ 
/*  658 */             return ser;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  663 */     return handlePrimaryContextualization(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  686 */     JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
/*  687 */     if (ser != null) {
/*  688 */       return ser;
/*      */     }
/*      */     
/*  691 */     ser = this._serializerCache.typedValueSerializer(valueType);
/*  692 */     if (ser != null) {
/*  693 */       return ser;
/*      */     }
/*      */     
/*      */ 
/*  697 */     ser = findValueSerializer(valueType, property);
/*  698 */     TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType(valueType));
/*      */     
/*  700 */     if (typeSer != null) {
/*  701 */       typeSer = typeSer.forProperty(property);
/*  702 */       ser = new TypeWrappedSerializer(typeSer, ser);
/*      */     }
/*  704 */     if (cache) {
/*  705 */       this._serializerCache.addTypedSerializer(valueType, ser);
/*      */     }
/*  707 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  731 */     JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
/*  732 */     if (ser != null) {
/*  733 */       return ser;
/*      */     }
/*      */     
/*  736 */     ser = this._serializerCache.typedValueSerializer(valueType);
/*  737 */     if (ser != null) {
/*  738 */       return ser;
/*      */     }
/*      */     
/*      */ 
/*  742 */     ser = findValueSerializer(valueType, property);
/*  743 */     TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
/*  744 */     if (typeSer != null) {
/*  745 */       typeSer = typeSer.forProperty(property);
/*  746 */       ser = new TypeWrappedSerializer(typeSer, ser);
/*      */     }
/*  748 */     if (cache) {
/*  749 */       this._serializerCache.addTypedSerializer(valueType, ser);
/*      */     }
/*  751 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TypeSerializer findTypeSerializer(JavaType javaType)
/*      */     throws JsonMappingException
/*      */   {
/*  762 */     return this._serializerFactory.createTypeSerializer(this._config, javaType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  778 */     JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this._config, keyType, this._keySerializer);
/*      */     
/*  780 */     return _handleContextualResolvable(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findKeySerializer(Class<?> rawKeyType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  789 */     return findKeySerializer(this._config.constructType(rawKeyType), property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> getDefaultNullKeySerializer()
/*      */   {
/*  802 */     return this._nullKeySerializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> getDefaultNullValueSerializer()
/*      */   {
/*  809 */     return this._nullValueSerializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  833 */     return this._nullKeySerializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> findNullValueSerializer(BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  849 */     return this._nullValueSerializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType)
/*      */   {
/*  866 */     if (unknownType == Object.class) {
/*  867 */       return this._unknownTypeSerializer;
/*      */     }
/*      */     
/*  870 */     return new UnknownSerializer(unknownType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isUnknownTypeSerializer(JsonSerializer<?> ser)
/*      */   {
/*  881 */     if ((ser == this._unknownTypeSerializer) || (ser == null)) {
/*  882 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  886 */     if ((isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) && 
/*  887 */       (ser.getClass() == UnknownSerializer.class)) {
/*  888 */       return true;
/*      */     }
/*      */     
/*  891 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract JsonSerializer<Object> serializerInstance(Annotated paramAnnotated, Object paramObject)
/*      */     throws JsonMappingException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  935 */     if ((ser != null) && 
/*  936 */       ((ser instanceof ContextualSerializer))) {
/*  937 */       ser = ((ContextualSerializer)ser).createContextual(this, property);
/*      */     }
/*      */     
/*  940 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  963 */     if ((ser != null) && 
/*  964 */       ((ser instanceof ContextualSerializer))) {
/*  965 */       ser = ((ContextualSerializer)ser).createContextual(this, property);
/*      */     }
/*      */     
/*  968 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void defaultSerializeValue(Object value, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/*  986 */     if (value == null) {
/*  987 */       if (this._stdNullValueSerializer) {
/*  988 */         gen.writeNull();
/*      */       } else {
/*  990 */         this._nullValueSerializer.serialize(null, gen, this);
/*      */       }
/*      */     } else {
/*  993 */       Class<?> cls = value.getClass();
/*  994 */       findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void defaultSerializeField(String fieldName, Object value, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/* 1006 */     gen.writeFieldName(fieldName);
/* 1007 */     if (value == null)
/*      */     {
/*      */ 
/*      */ 
/* 1011 */       if (this._stdNullValueSerializer) {
/* 1012 */         gen.writeNull();
/*      */       } else {
/* 1014 */         this._nullValueSerializer.serialize(null, gen, this);
/*      */       }
/*      */     } else {
/* 1017 */       Class<?> cls = value.getClass();
/* 1018 */       findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void defaultSerializeDateValue(long timestamp, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/* 1032 */     if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
/* 1033 */       gen.writeNumber(timestamp);
/*      */     } else {
/* 1035 */       gen.writeString(_dateFormat().format(new Date(timestamp)));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final void defaultSerializeDateValue(Date date, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/* 1048 */     if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
/* 1049 */       gen.writeNumber(date.getTime());
/*      */     } else {
/* 1051 */       gen.writeString(_dateFormat().format(date));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void defaultSerializeDateKey(long timestamp, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/* 1062 */     if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
/* 1063 */       gen.writeFieldName(String.valueOf(timestamp));
/*      */     } else {
/* 1065 */       gen.writeFieldName(_dateFormat().format(new Date(timestamp)));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void defaultSerializeDateKey(Date date, JsonGenerator gen)
/*      */     throws IOException
/*      */   {
/* 1076 */     if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
/* 1077 */       gen.writeFieldName(String.valueOf(date.getTime()));
/*      */     } else {
/* 1079 */       gen.writeFieldName(_dateFormat().format(date));
/*      */     }
/*      */   }
/*      */   
/*      */   public final void defaultSerializeNull(JsonGenerator gen) throws IOException
/*      */   {
/* 1085 */     if (this._stdNullValueSerializer) {
/* 1086 */       gen.writeNull();
/*      */     } else {
/* 1088 */       this._nullValueSerializer.serialize(null, gen, this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonMappingException mappingException(String message, Object... args)
/*      */   {
/* 1106 */     if ((args != null) && (args.length > 0)) {
/* 1107 */       message = String.format(message, args);
/*      */     }
/* 1109 */     return JsonMappingException.from(getGenerator(), message);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonMappingException mappingException(Throwable t, String message, Object... args)
/*      */   {
/* 1120 */     if ((args != null) && (args.length > 0)) {
/* 1121 */       message = String.format(message, args);
/*      */     }
/* 1123 */     return JsonMappingException.from(getGenerator(), message, t);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportMappingProblem(String message, Object... args)
/*      */     throws JsonMappingException
/*      */   {
/* 1134 */     throw mappingException(message, args);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportMappingProblem(Throwable t, String message, Object... args)
/*      */     throws JsonMappingException
/*      */   {
/* 1145 */     throw mappingException(t, message, args);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public JsonGenerator getGenerator()
/*      */   {
/* 1152 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportIncompatibleRootType(Object value, JavaType rootType)
/*      */     throws IOException
/*      */   {
/* 1164 */     if (rootType.isPrimitive()) {
/* 1165 */       Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
/*      */       
/* 1167 */       if (wrapperType.isAssignableFrom(value.getClass())) {
/* 1168 */         return;
/*      */       }
/*      */     }
/* 1171 */     reportMappingProblem("Incompatible types: declared root type (%s) vs %s", new Object[] { rootType, value.getClass().getName() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType)
/*      */     throws JsonMappingException
/*      */   {
/* 1186 */     JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
/* 1187 */     if (ser == null)
/*      */     {
/* 1189 */       ser = this._serializerCache.untypedValueSerializer(runtimeType);
/* 1190 */       if (ser == null) {
/* 1191 */         ser = _createAndCacheUntypedSerializer(runtimeType);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1199 */     if (isUnknownTypeSerializer(ser)) {
/* 1200 */       return null;
/*      */     }
/* 1202 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType)
/*      */     throws JsonMappingException
/*      */   {
/* 1219 */     JavaType fullType = this._config.constructType(rawType);
/*      */     JsonSerializer<Object> ser;
/*      */     try {
/* 1222 */       ser = _createUntypedSerializer(fullType);
/*      */ 
/*      */     }
/*      */     catch (IllegalArgumentException iae)
/*      */     {
/* 1227 */       reportMappingProblem(iae, iae.getMessage(), new Object[0]);
/* 1228 */       return null;
/*      */     }
/*      */     
/* 1231 */     if (ser != null)
/*      */     {
/* 1233 */       this._serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
/*      */     }
/* 1235 */     return ser;
/*      */   }
/*      */   
/*      */   protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type) throws JsonMappingException
/*      */   {
/*      */     JsonSerializer<Object> ser;
/*      */     try
/*      */     {
/* 1243 */       ser = _createUntypedSerializer(type);
/*      */ 
/*      */     }
/*      */     catch (IllegalArgumentException iae)
/*      */     {
/* 1248 */       reportMappingProblem(iae, iae.getMessage(), new Object[0]);
/* 1249 */       return null;
/*      */     }
/*      */     
/* 1252 */     if (ser != null)
/*      */     {
/* 1254 */       this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
/*      */     }
/* 1256 */     return ser;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected JsonSerializer<Object> _createUntypedSerializer(JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 70	com/facebook/presto/jdbc/internal/jackson/databind/SerializerProvider:_serializerCache	Lcom/facebook/presto/jdbc/internal/jackson/databind/ser/SerializerCache;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 65	com/facebook/presto/jdbc/internal/jackson/databind/SerializerProvider:_serializerFactory	Lcom/facebook/presto/jdbc/internal/jackson/databind/ser/SerializerFactory;
/*      */     //   11: aload_0
/*      */     //   12: aload_1
/*      */     //   13: invokevirtual 482	com/facebook/presto/jdbc/internal/jackson/databind/ser/SerializerFactory:createSerializer	(Lcom/facebook/presto/jdbc/internal/jackson/databind/SerializerProvider;Lcom/facebook/presto/jdbc/internal/jackson/databind/JavaType;)Lcom/facebook/presto/jdbc/internal/jackson/databind/JsonSerializer;
/*      */     //   16: aload_2
/*      */     //   17: monitorexit
/*      */     //   18: areturn
/*      */     //   19: astore_3
/*      */     //   20: aload_2
/*      */     //   21: monitorexit
/*      */     //   22: aload_3
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1270	-> byte code offset #0
/*      */     //   Java source line #1272	-> byte code offset #7
/*      */     //   Java source line #1273	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	SerializerProvider
/*      */     //   0	24	1	type	JavaType
/*      */     //   5	16	2	Ljava/lang/Object;	Object
/*      */     //   19	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/* 1285 */     if ((ser instanceof ResolvableSerializer)) {
/* 1286 */       ((ResolvableSerializer)ser).resolve(this);
/*      */     }
/* 1288 */     return handleSecondaryContextualization(ser, property);
/*      */   }
/*      */   
/*      */ 
/*      */   protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser)
/*      */     throws JsonMappingException
/*      */   {
/* 1295 */     if ((ser instanceof ResolvableSerializer)) {
/* 1296 */       ((ResolvableSerializer)ser).resolve(this);
/*      */     }
/* 1298 */     return ser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final DateFormat _dateFormat()
/*      */   {
/* 1309 */     if (this._dateFormat != null) {
/* 1310 */       return this._dateFormat;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1316 */     DateFormat df = this._config.getDateFormat();
/* 1317 */     this._dateFormat = (df = (DateFormat)df.clone());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1326 */     return df;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\SerializerProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */