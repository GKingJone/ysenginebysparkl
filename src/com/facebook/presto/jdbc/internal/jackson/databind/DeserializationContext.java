/*      */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ContextAttributes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualKeyDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializerCache;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.UnresolvedForwardReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.TypeWrappedDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.exc.InvalidFormatException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.exc.InvalidTypeIdException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.exc.UnrecognizedPropertyException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.LinkedNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ObjectBuffer;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.atomic.AtomicReference;
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
/*      */ 
/*      */ 
/*      */ public abstract class DeserializationContext
/*      */   extends DatabindContext
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   private static final int MAX_ERROR_STR_LEN = 500;
/*      */   protected final DeserializerCache _cache;
/*      */   protected final DeserializerFactory _factory;
/*      */   protected final DeserializationConfig _config;
/*      */   protected final int _featureFlags;
/*      */   protected final Class<?> _view;
/*      */   protected transient JsonParser _parser;
/*      */   protected final InjectableValues _injectableValues;
/*      */   protected transient ArrayBuilders _arrayBuilders;
/*      */   protected transient ObjectBuffer _objectBuffer;
/*      */   protected transient DateFormat _dateFormat;
/*      */   protected transient ContextAttributes _attributes;
/*      */   protected LinkedNode<JavaType> _currentType;
/*      */   
/*      */   protected DeserializationContext(DeserializerFactory df)
/*      */   {
/*  152 */     this(df, null);
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeserializationContext(DeserializerFactory df, DeserializerCache cache)
/*      */   {
/*  158 */     if (df == null) {
/*  159 */       throw new IllegalArgumentException("Can not pass null DeserializerFactory");
/*      */     }
/*  161 */     this._factory = df;
/*  162 */     this._cache = (cache == null ? new DeserializerCache() : cache);
/*      */     
/*  164 */     this._featureFlags = 0;
/*  165 */     this._config = null;
/*  166 */     this._injectableValues = null;
/*  167 */     this._view = null;
/*  168 */     this._attributes = null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeserializationContext(DeserializationContext src, DeserializerFactory factory)
/*      */   {
/*  174 */     this._cache = src._cache;
/*  175 */     this._factory = factory;
/*      */     
/*  177 */     this._config = src._config;
/*  178 */     this._featureFlags = src._featureFlags;
/*  179 */     this._view = src._view;
/*  180 */     this._parser = src._parser;
/*  181 */     this._injectableValues = src._injectableValues;
/*  182 */     this._attributes = src._attributes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeserializationContext(DeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues injectableValues)
/*      */   {
/*  192 */     this._cache = src._cache;
/*  193 */     this._factory = src._factory;
/*      */     
/*  195 */     this._config = config;
/*  196 */     this._featureFlags = config.getDeserializationFeatures();
/*  197 */     this._view = config.getActiveView();
/*  198 */     this._parser = p;
/*  199 */     this._injectableValues = injectableValues;
/*  200 */     this._attributes = config.getAttributes();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DeserializationContext(DeserializationContext src)
/*      */   {
/*  207 */     this._cache = new DeserializerCache();
/*  208 */     this._factory = src._factory;
/*      */     
/*  210 */     this._config = src._config;
/*  211 */     this._featureFlags = src._featureFlags;
/*  212 */     this._view = src._view;
/*  213 */     this._injectableValues = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DeserializationConfig getConfig()
/*      */   {
/*  223 */     return this._config;
/*      */   }
/*      */   
/*  226 */   public final Class<?> getActiveView() { return this._view; }
/*      */   
/*      */   public final boolean canOverrideAccessModifiers()
/*      */   {
/*  230 */     return this._config.canOverrideAccessModifiers();
/*      */   }
/*      */   
/*      */   public final boolean isEnabled(MapperFeature feature)
/*      */   {
/*  235 */     return this._config.isEnabled(feature);
/*      */   }
/*      */   
/*      */   public final JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType)
/*      */   {
/*  240 */     return this._config.getDefaultPropertyFormat(baseType);
/*      */   }
/*      */   
/*      */   public final AnnotationIntrospector getAnnotationIntrospector()
/*      */   {
/*  245 */     return this._config.getAnnotationIntrospector();
/*      */   }
/*      */   
/*      */   public final TypeFactory getTypeFactory()
/*      */   {
/*  250 */     return this._config.getTypeFactory();
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
/*  261 */     return this._config.getLocale();
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
/*  272 */     return this._config.getTimeZone();
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
/*  283 */     return this._attributes.getAttribute(key);
/*      */   }
/*      */   
/*      */ 
/*      */   public DeserializationContext setAttribute(Object key, Object value)
/*      */   {
/*  289 */     this._attributes = this._attributes.withPerCallAttribute(key, value);
/*  290 */     return this;
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
/*      */   public JavaType getContextualType()
/*      */   {
/*  307 */     return this._currentType == null ? null : (JavaType)this._currentType.value();
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
/*      */   public DeserializerFactory getFactory()
/*      */   {
/*  320 */     return this._factory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean isEnabled(DeserializationFeature feat)
/*      */   {
/*  331 */     return (this._featureFlags & feat.getMask()) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final int getDeserializationFeatures()
/*      */   {
/*  341 */     return this._featureFlags;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean hasDeserializationFeatures(int featureMask)
/*      */   {
/*  351 */     return (this._featureFlags & featureMask) == featureMask;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final boolean hasSomeOfFeatures(int featureMask)
/*      */   {
/*  361 */     return (this._featureFlags & featureMask) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final JsonParser getParser()
/*      */   {
/*  372 */     return this._parser;
/*      */   }
/*      */   
/*      */   public final Object findInjectableValue(Object valueId, BeanProperty forProperty, Object beanInstance)
/*      */   {
/*  377 */     if (this._injectableValues == null) {
/*  378 */       throw new IllegalStateException("No 'injectableValues' configured, can not inject value with id [" + valueId + "]");
/*      */     }
/*  380 */     return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final Base64Variant getBase64Variant()
/*      */   {
/*  392 */     return this._config.getBase64Variant();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final JsonNodeFactory getNodeFactory()
/*      */   {
/*  402 */     return this._config.getNodeFactory();
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
/*      */   public boolean hasValueDeserializerFor(JavaType type, AtomicReference<Throwable> cause)
/*      */   {
/*      */     try
/*      */     {
/*  420 */       return this._cache.hasValueDeserializerFor(this, this._factory, type);
/*      */     } catch (JsonMappingException e) {
/*  422 */       if (cause != null) {
/*  423 */         cause.set(e);
/*      */       }
/*      */     } catch (RuntimeException e) {
/*  426 */       if (cause == null) {
/*  427 */         throw e;
/*      */       }
/*  429 */       cause.set(e);
/*      */     }
/*  431 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final JsonDeserializer<Object> findContextualValueDeserializer(JavaType type, BeanProperty prop)
/*      */     throws JsonMappingException
/*      */   {
/*  442 */     JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/*  443 */     if (deser != null) {
/*  444 */       deser = handleSecondaryContextualization(deser, prop, type);
/*      */     }
/*  446 */     return deser;
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
/*      */   public final JsonDeserializer<Object> findNonContextualValueDeserializer(JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*  465 */     return this._cache.findValueDeserializer(this, this._factory, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final JsonDeserializer<Object> findRootValueDeserializer(JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*  475 */     JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
/*      */     
/*  477 */     if (deser == null) {
/*  478 */       return null;
/*      */     }
/*  480 */     deser = handleSecondaryContextualization(deser, null, type);
/*  481 */     TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
/*  482 */     if (typeDeser != null)
/*      */     {
/*  484 */       typeDeser = typeDeser.forProperty(null);
/*  485 */       return new TypeWrappedDeserializer(typeDeser, deser);
/*      */     }
/*  487 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final KeyDeserializer findKeyDeserializer(JavaType keyType, BeanProperty prop)
/*      */     throws JsonMappingException
/*      */   {
/*  498 */     KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
/*      */     
/*      */ 
/*  501 */     if ((kd instanceof ContextualKeyDeserializer)) {
/*  502 */       kd = ((ContextualKeyDeserializer)kd).createContextual(this, prop);
/*      */     }
/*  504 */     return kd;
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
/*      */   public abstract ReadableObjectId findObjectId(Object paramObject, ObjectIdGenerator<?> paramObjectIdGenerator, ObjectIdResolver paramObjectIdResolver);
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
/*      */   public abstract void checkUnresolvedObjectId()
/*      */     throws UnresolvedForwardReference;
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
/*      */   public final JavaType constructType(Class<?> cls)
/*      */   {
/*  541 */     return this._config.constructType(cls);
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
/*      */   public Class<?> findClass(String className)
/*      */     throws ClassNotFoundException
/*      */   {
/*  555 */     return getTypeFactory().findClass(className);
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
/*      */   public final ObjectBuffer leaseObjectBuffer()
/*      */   {
/*  572 */     ObjectBuffer buf = this._objectBuffer;
/*  573 */     if (buf == null) {
/*  574 */       buf = new ObjectBuffer();
/*      */     } else {
/*  576 */       this._objectBuffer = null;
/*      */     }
/*  578 */     return buf;
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
/*      */   public final void returnObjectBuffer(ObjectBuffer buf)
/*      */   {
/*  592 */     if ((this._objectBuffer == null) || (buf.initialCapacity() >= this._objectBuffer.initialCapacity()))
/*      */     {
/*  594 */       this._objectBuffer = buf;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final ArrayBuilders getArrayBuilders()
/*      */   {
/*  604 */     if (this._arrayBuilders == null) {
/*  605 */       this._arrayBuilders = new ArrayBuilders();
/*      */     }
/*  607 */     return this._arrayBuilders;
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
/*      */   public abstract JsonDeserializer<Object> deserializerInstance(Annotated paramAnnotated, Object paramObject)
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
/*      */   public abstract KeyDeserializer keyDeserializerInstance(Annotated paramAnnotated, Object paramObject)
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
/*      */   public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*  646 */     if ((deser instanceof ContextualDeserializer)) {
/*  647 */       this._currentType = new LinkedNode(type, this._currentType);
/*      */       try {
/*  649 */         deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*      */       } finally {
/*  651 */         this._currentType = this._currentType.next();
/*      */       }
/*      */     }
/*  654 */     return deser;
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
/*      */   public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*  677 */     if ((deser instanceof ContextualDeserializer)) {
/*  678 */       this._currentType = new LinkedNode(type, this._currentType);
/*      */       try {
/*  680 */         deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*      */       } finally {
/*  682 */         this._currentType = this._currentType.next();
/*      */       }
/*      */     }
/*  685 */     return deser;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop) throws JsonMappingException {
/*  690 */     return handlePrimaryContextualization(deser, prop, TypeFactory.unknownType());
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop) throws JsonMappingException {
/*  695 */     if ((deser instanceof ContextualDeserializer)) {
/*  696 */       deser = ((ContextualDeserializer)deser).createContextual(this, prop);
/*      */     }
/*  698 */     return deser;
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
/*      */   public Date parseDate(String dateStr)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     try
/*      */     {
/*  720 */       DateFormat df = getDateFormat();
/*  721 */       return df.parse(dateStr);
/*      */     } catch (ParseException e) {
/*  723 */       throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", new Object[] { dateStr, e.getMessage() }));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Calendar constructCalendar(Date d)
/*      */   {
/*  734 */     Calendar c = Calendar.getInstance(getTimeZone());
/*  735 */     c.setTime(d);
/*  736 */     return c;
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
/*      */   public <T> T readValue(JsonParser p, Class<T> type)
/*      */     throws IOException
/*      */   {
/*  757 */     return (T)readValue(p, getTypeFactory().constructType(type));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readValue(JsonParser p, JavaType type)
/*      */     throws IOException
/*      */   {
/*  765 */     JsonDeserializer<Object> deser = findRootValueDeserializer(type);
/*  766 */     if (deser == null) {
/*  767 */       reportMappingException("Could not find JsonDeserializer for type %s", new Object[] { type });
/*      */     }
/*  769 */     return (T)deser.deserialize(p, this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readPropertyValue(JsonParser p, BeanProperty prop, Class<T> type)
/*      */     throws IOException
/*      */   {
/*  781 */     return (T)readPropertyValue(p, prop, getTypeFactory().constructType(type));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readPropertyValue(JsonParser p, BeanProperty prop, JavaType type)
/*      */     throws IOException
/*      */   {
/*  789 */     JsonDeserializer<Object> deser = findContextualValueDeserializer(type, prop);
/*  790 */     if (deser == null) {
/*  791 */       String propName = "'" + prop.getName() + "'";
/*  792 */       reportMappingException("Could not find JsonDeserializer for type %s (via property %s)", new Object[] { type, propName });
/*      */     }
/*      */     
/*      */ 
/*  796 */     return (T)deser.deserialize(p, this);
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
/*      */   public boolean handleUnknownProperty(JsonParser p, JsonDeserializer<?> deser, Object instanceOrClass, String propName)
/*      */     throws IOException
/*      */   {
/*  818 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*  819 */     while (h != null)
/*      */     {
/*  821 */       if (((DeserializationProblemHandler)h.value()).handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
/*  822 */         return true;
/*      */       }
/*  824 */       h = h.next();
/*      */     }
/*      */     
/*  827 */     if (!isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/*  828 */       p.skipChildren();
/*  829 */       return true;
/*      */     }
/*      */     
/*  832 */     Collection<Object> propIds = deser == null ? null : deser.getKnownPropertyNames();
/*  833 */     throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, propIds);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object handleWeirdKey(Class<?> keyClass, String keyValue, String msg, Object... msgArgs)
/*      */     throws IOException
/*      */   {
/*  861 */     if (msgArgs.length > 0) {
/*  862 */       msg = String.format(msg, msgArgs);
/*      */     }
/*  864 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*  865 */     while (h != null)
/*      */     {
/*  867 */       Object key = ((DeserializationProblemHandler)h.value()).handleWeirdKey(this, keyClass, keyValue, msg);
/*  868 */       if (key != DeserializationProblemHandler.NOT_HANDLED)
/*      */       {
/*  870 */         if ((key == null) || (keyClass.isInstance(key))) {
/*  871 */           return key;
/*      */         }
/*  873 */         throw weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] { keyClass, key.getClass() }));
/*      */       }
/*      */       
/*      */ 
/*  877 */       h = h.next();
/*      */     }
/*  879 */     throw weirdKeyException(keyClass, keyValue, msg);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs)
/*      */     throws IOException
/*      */   {
/*  907 */     if (msgArgs.length > 0) {
/*  908 */       msg = String.format(msg, msgArgs);
/*      */     }
/*  910 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*  911 */     while (h != null)
/*      */     {
/*  913 */       Object instance = ((DeserializationProblemHandler)h.value()).handleWeirdStringValue(this, targetClass, value, msg);
/*  914 */       if (instance != DeserializationProblemHandler.NOT_HANDLED)
/*      */       {
/*  916 */         if ((instance == null) || (targetClass.isInstance(instance))) {
/*  917 */           return instance;
/*      */         }
/*  919 */         throw weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[] { targetClass, instance.getClass() }));
/*      */       }
/*      */       
/*      */ 
/*  923 */       h = h.next();
/*      */     }
/*  925 */     throw weirdStringException(value, targetClass, msg);
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
/*      */ 
/*      */ 
/*      */   public Object handleWeirdNumberValue(Class<?> targetClass, Number value, String msg, Object... msgArgs)
/*      */     throws IOException
/*      */   {
/*  952 */     if (msgArgs.length > 0) {
/*  953 */       msg = String.format(msg, msgArgs);
/*      */     }
/*  955 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*  956 */     while (h != null)
/*      */     {
/*  958 */       Object key = ((DeserializationProblemHandler)h.value()).handleWeirdNumberValue(this, targetClass, value, msg);
/*  959 */       if (key != DeserializationProblemHandler.NOT_HANDLED)
/*      */       {
/*  961 */         if ((key == null) || (targetClass.isInstance(key))) {
/*  962 */           return key;
/*      */         }
/*  964 */         throw weirdNumberException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", new Object[] { targetClass, key.getClass() }));
/*      */       }
/*      */       
/*      */ 
/*  968 */       h = h.next();
/*      */     }
/*  970 */     throw weirdNumberException(value, targetClass, msg);
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
/*      */   public Object handleMissingInstantiator(Class<?> instClass, JsonParser p, String msg, Object... msgArgs)
/*      */     throws IOException
/*      */   {
/*  992 */     if (msgArgs.length > 0) {
/*  993 */       msg = String.format(msg, msgArgs);
/*      */     }
/*  995 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/*  996 */     while (h != null)
/*      */     {
/*  998 */       Object instance = ((DeserializationProblemHandler)h.value()).handleMissingInstantiator(this, instClass, p, msg);
/*      */       
/* 1000 */       if (instance != DeserializationProblemHandler.NOT_HANDLED)
/*      */       {
/* 1002 */         if ((instance == null) || (instClass.isInstance(instance))) {
/* 1003 */           return instance;
/*      */         }
/* 1005 */         throw instantiationException(instClass, String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", new Object[] { instClass, instance.getClass() }));
/*      */       }
/*      */       
/*      */ 
/* 1009 */       h = h.next();
/*      */     }
/* 1011 */     throw instantiationException(instClass, msg);
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
/*      */   public Object handleInstantiationProblem(Class<?> instClass, Object argument, Throwable t)
/*      */     throws IOException
/*      */   {
/* 1035 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1036 */     while (h != null)
/*      */     {
/* 1038 */       Object instance = ((DeserializationProblemHandler)h.value()).handleInstantiationProblem(this, instClass, argument, t);
/* 1039 */       if (instance != DeserializationProblemHandler.NOT_HANDLED)
/*      */       {
/* 1041 */         if ((instance == null) || (instClass.isInstance(instance))) {
/* 1042 */           return instance;
/*      */         }
/* 1044 */         throw instantiationException(instClass, String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", new Object[] { instClass, instance.getClass() }));
/*      */       }
/*      */       
/*      */ 
/* 1048 */       h = h.next();
/*      */     }
/*      */     
/* 1051 */     if ((t instanceof IOException)) {
/* 1052 */       throw ((IOException)t);
/*      */     }
/* 1054 */     throw instantiationException(instClass, t);
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
/*      */   public Object handleUnexpectedToken(Class<?> instClass, JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1074 */     return handleUnexpectedToken(instClass, p.getCurrentToken(), p, null, new Object[0]);
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
/*      */   public Object handleUnexpectedToken(Class<?> instClass, JsonToken t, JsonParser p, String msg, Object... msgArgs)
/*      */     throws IOException
/*      */   {
/* 1096 */     if (msgArgs.length > 0) {
/* 1097 */       msg = String.format(msg, msgArgs);
/*      */     }
/* 1099 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1100 */     while (h != null) {
/* 1101 */       Object instance = ((DeserializationProblemHandler)h.value()).handleUnexpectedToken(this, instClass, t, p, msg);
/*      */       
/* 1103 */       if (instance != DeserializationProblemHandler.NOT_HANDLED) {
/* 1104 */         if ((instance == null) || (instClass.isInstance(instance))) {
/* 1105 */           return instance;
/*      */         }
/* 1107 */         reportMappingException("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", new Object[] { instClass, instance.getClass() });
/*      */       }
/*      */       
/* 1110 */       h = h.next();
/*      */     }
/* 1112 */     if (msg == null) {
/* 1113 */       if (t == null) {
/* 1114 */         msg = String.format("Unexpected end-of-input when binding data into %s", new Object[] { _calcName(instClass) });
/*      */       }
/*      */       else {
/* 1117 */         msg = String.format("Can not deserialize instance of %s out of %s token", new Object[] { _calcName(instClass), t });
/*      */       }
/*      */     }
/*      */     
/* 1121 */     reportMappingException(msg, new Object[0]);
/* 1122 */     return null;
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
/*      */ 
/*      */   public JavaType handleUnknownTypeId(JavaType baseType, String id, TypeIdResolver idResolver, String extraDesc)
/*      */     throws IOException
/*      */   {
/* 1148 */     LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
/* 1149 */     while (h != null)
/*      */     {
/* 1151 */       JavaType type = ((DeserializationProblemHandler)h.value()).handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
/* 1152 */       if (type != null) {
/* 1153 */         if (type.hasRawClass(Void.class)) {
/* 1154 */           return null;
/*      */         }
/*      */         
/* 1157 */         if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
/* 1158 */           return type;
/*      */         }
/* 1160 */         throw unknownTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + type);
/*      */       }
/*      */       
/* 1163 */       h = h.next();
/*      */     }
/*      */     
/* 1166 */     if (!isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
/* 1167 */       return null;
/*      */     }
/* 1169 */     throw unknownTypeIdException(baseType, id, extraDesc);
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
/*      */   public void reportWrongTokenException(JsonParser p, JsonToken expToken, String msg, Object... msgArgs)
/*      */     throws JsonMappingException
/*      */   {
/* 1193 */     if ((msg != null) && (msgArgs.length > 0)) {
/* 1194 */       msg = String.format(msg, msgArgs);
/*      */     }
/* 1196 */     throw wrongTokenException(p, expToken, msg);
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
/*      */   @Deprecated
/*      */   public void reportUnknownProperty(Object instanceOrClass, String fieldName, JsonDeserializer<?> deser)
/*      */     throws JsonMappingException
/*      */   {
/* 1215 */     if (!isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
/* 1216 */       return;
/*      */     }
/*      */     
/* 1219 */     Collection<Object> propIds = deser == null ? null : deser.getKnownPropertyNames();
/* 1220 */     throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, propIds);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportMappingException(String msg, Object... msgArgs)
/*      */     throws JsonMappingException
/*      */   {
/* 1230 */     if (msgArgs.length > 0) {
/* 1231 */       msg = String.format(msg, msgArgs);
/*      */     }
/* 1233 */     throw JsonMappingException.from(getParser(), msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportMissingContent(String msg, Object... msgArgs)
/*      */     throws JsonMappingException
/*      */   {
/* 1242 */     if (msg == null) {
/* 1243 */       msg = "No content to map due to end-of-input";
/* 1244 */     } else if (msgArgs.length > 0) {
/* 1245 */       msg = String.format(msg, msgArgs);
/*      */     }
/* 1247 */     throw JsonMappingException.from(getParser(), msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reportUnresolvedObjectId(ObjectIdReader oidReader, Object bean)
/*      */     throws JsonMappingException
/*      */   {
/* 1256 */     String msg = String.format("No Object Id found for an instance of %s, to assign to property '%s'", new Object[] { bean.getClass().getName(), oidReader.propertyName });
/*      */     
/* 1258 */     throw JsonMappingException.from(getParser(), msg);
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
/*      */   public JsonMappingException mappingException(String message)
/*      */   {
/* 1277 */     return JsonMappingException.from(getParser(), message);
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
/*      */   public JsonMappingException mappingException(String msgTemplate, Object... args)
/*      */   {
/* 1290 */     if ((args != null) && (args.length > 0)) {
/* 1291 */       msgTemplate = String.format(msgTemplate, args);
/*      */     }
/* 1293 */     return JsonMappingException.from(getParser(), msgTemplate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(Class<?> targetClass)
/*      */   {
/* 1303 */     return mappingException(targetClass, this._parser.getCurrentToken());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JsonMappingException mappingException(Class<?> targetClass, JsonToken token)
/*      */   {
/* 1311 */     String tokenDesc = token == null ? "<end of input>" : String.format("%s token", new Object[] { token });
/* 1312 */     return JsonMappingException.from(this._parser, String.format("Can not deserialize instance of %s out of %s", new Object[] { _calcName(targetClass), tokenDesc }));
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
/*      */   public JsonMappingException wrongTokenException(JsonParser p, JsonToken expToken, String msg0)
/*      */   {
/* 1335 */     String msg = String.format("Unexpected token (%s), expected %s", new Object[] { p.getCurrentToken(), expToken });
/*      */     
/* 1337 */     if (msg0 != null) {
/* 1338 */       msg = msg + ": " + msg0;
/*      */     }
/* 1340 */     return JsonMappingException.from(p, msg);
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
/*      */   public JsonMappingException weirdKeyException(Class<?> keyClass, String keyValue, String msg)
/*      */   {
/* 1353 */     return InvalidFormatException.from(this._parser, String.format("Can not deserialize Map key of type %s from String %s: %s", new Object[] { keyClass.getName(), _quotedString(keyValue), msg }), keyValue, keyClass);
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
/*      */   public JsonMappingException weirdStringException(String value, Class<?> instClass, String msg)
/*      */   {
/* 1374 */     return InvalidFormatException.from(this._parser, String.format("Can not deserialize value of type %s from String %s: %s", new Object[] { instClass.getName(), _quotedString(value), msg }), value, instClass);
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
/*      */   public JsonMappingException weirdNumberException(Number value, Class<?> instClass, String msg)
/*      */   {
/* 1389 */     return InvalidFormatException.from(this._parser, String.format("Can not deserialize value of type %s from number %s: %s", new Object[] { instClass.getName(), String.valueOf(value), msg }), value, instClass);
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
/*      */   public JsonMappingException instantiationException(Class<?> instClass, Throwable t)
/*      */   {
/* 1405 */     return JsonMappingException.from(this._parser, String.format("Can not construct instance of %s, problem: %s", new Object[] { instClass.getName(), t.getMessage() }), t);
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
/*      */   public JsonMappingException instantiationException(Class<?> instClass, String msg)
/*      */   {
/* 1420 */     return JsonMappingException.from(this._parser, String.format("Can not construct instance of %s: %s", new Object[] { instClass.getName(), msg }));
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
/*      */   public JsonMappingException unknownTypeIdException(JavaType baseType, String typeId, String extraDesc)
/*      */   {
/* 1436 */     String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[] { typeId, baseType });
/*      */     
/* 1438 */     if (extraDesc != null) {
/* 1439 */       msg = msg + ": " + extraDesc;
/*      */     }
/* 1441 */     return InvalidTypeIdException.from(this._parser, msg, baseType, typeId);
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
/*      */   @Deprecated
/*      */   public JsonMappingException unknownTypeException(JavaType type, String id, String extraDesc)
/*      */   {
/* 1458 */     String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[] { id, type });
/*      */     
/* 1460 */     if (extraDesc != null) {
/* 1461 */       msg = msg + ": " + extraDesc;
/*      */     }
/* 1463 */     return JsonMappingException.from(this._parser, msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JsonMappingException endOfInputException(Class<?> instClass)
/*      */   {
/* 1474 */     return JsonMappingException.from(this._parser, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DateFormat getDateFormat()
/*      */   {
/* 1486 */     if (this._dateFormat != null) {
/* 1487 */       return this._dateFormat;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1494 */     DateFormat df = this._config.getDateFormat();
/* 1495 */     this._dateFormat = (df = (DateFormat)df.clone());
/* 1496 */     return df;
/*      */   }
/*      */   
/*      */   protected String determineClassName(Object instance) {
/* 1500 */     return ClassUtil.getClassDescription(instance);
/*      */   }
/*      */   
/*      */   protected String _calcName(Class<?> cls) {
/* 1504 */     if (cls.isArray()) {
/* 1505 */       return _calcName(cls.getComponentType()) + "[]";
/*      */     }
/* 1507 */     return cls.getName();
/*      */   }
/*      */   
/*      */   protected String _valueDesc() {
/*      */     try {
/* 1512 */       return _desc(this._parser.getText());
/*      */     } catch (Exception e) {}
/* 1514 */     return "[N/A]";
/*      */   }
/*      */   
/*      */   protected String _desc(String desc)
/*      */   {
/* 1519 */     if (desc == null) {
/* 1520 */       return "[N/A]";
/*      */     }
/*      */     
/* 1523 */     if (desc.length() > 500) {
/* 1524 */       desc = desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
/*      */     }
/* 1526 */     return desc;
/*      */   }
/*      */   
/*      */   protected String _quotedString(String desc)
/*      */   {
/* 1531 */     if (desc == null) {
/* 1532 */       return "[N/A]";
/*      */     }
/*      */     
/* 1535 */     if (desc.length() > 500) {
/* 1536 */       return String.format("\"%s]...[%s\"", new Object[] { desc.substring(0, 500), desc.substring(desc.length() - 500) });
/*      */     }
/*      */     
/*      */ 
/* 1540 */     return "\"" + desc + "\"";
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\DeserializationContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */