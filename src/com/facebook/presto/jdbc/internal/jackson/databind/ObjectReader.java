/*      */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.FormatFeature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.FormatSchema;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonPointer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.TreeNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.filter.FilteringParserDelegate;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.filter.JsonPointerBasedFilter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.filter.TokenFilter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.type.ResolvedType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.type.TypeReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ContextAttributes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DataFormatReaders;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DataFormatReaders.Match;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DefaultDeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import java.io.DataInput;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URL;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ public class ObjectReader extends com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec implements com.facebook.presto.jdbc.internal.jackson.core.Versioned, java.io.Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   42 */   private static final JavaType JSON_NODE_TYPE = com.facebook.presto.jdbc.internal.jackson.databind.type.SimpleType.constructUnsafe(JsonNode.class);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final DeserializationConfig _config;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final DefaultDeserializationContext _context;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonFactory _parserFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _unwrapRoot;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final TokenFilter _filter;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JavaType _valueType;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonDeserializer<Object> _rootDeserializer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Object _valueToUpdate;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final FormatSchema _schema;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final InjectableValues _injectableValues;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final DataFormatReaders _dataFormatReaders;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config)
/*      */   {
/*  161 */     this(mapper, config, null, null, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader(ObjectMapper mapper, DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues)
/*      */   {
/*  172 */     this._config = config;
/*  173 */     this._context = mapper._deserializationContext;
/*  174 */     this._rootDeserializers = mapper._rootDeserializers;
/*  175 */     this._parserFactory = mapper._jsonFactory;
/*  176 */     this._valueType = valueType;
/*  177 */     this._valueToUpdate = valueToUpdate;
/*  178 */     if ((valueToUpdate != null) && (valueType.isArrayType())) {
/*  179 */       throw new IllegalArgumentException("Can not update an array value");
/*      */     }
/*  181 */     this._schema = schema;
/*  182 */     this._injectableValues = injectableValues;
/*  183 */     this._unwrapRoot = config.useRootWrapping();
/*      */     
/*  185 */     this._rootDeserializer = _prefetchRootDeserializer(valueType);
/*  186 */     this._dataFormatReaders = null;
/*  187 */     this._filter = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders)
/*      */   {
/*  198 */     this._config = config;
/*  199 */     this._context = base._context;
/*      */     
/*  201 */     this._rootDeserializers = base._rootDeserializers;
/*  202 */     this._parserFactory = base._parserFactory;
/*      */     
/*  204 */     this._valueType = valueType;
/*  205 */     this._rootDeserializer = rootDeser;
/*  206 */     this._valueToUpdate = valueToUpdate;
/*  207 */     if ((valueToUpdate != null) && (valueType.isArrayType())) {
/*  208 */       throw new IllegalArgumentException("Can not update an array value");
/*      */     }
/*  210 */     this._schema = schema;
/*  211 */     this._injectableValues = injectableValues;
/*  212 */     this._unwrapRoot = config.useRootWrapping();
/*  213 */     this._dataFormatReaders = dataFormatReaders;
/*  214 */     this._filter = base._filter;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader(ObjectReader base, DeserializationConfig config)
/*      */   {
/*  222 */     this._config = config;
/*  223 */     this._context = base._context;
/*      */     
/*  225 */     this._rootDeserializers = base._rootDeserializers;
/*  226 */     this._parserFactory = base._parserFactory;
/*      */     
/*  228 */     this._valueType = base._valueType;
/*  229 */     this._rootDeserializer = base._rootDeserializer;
/*  230 */     this._valueToUpdate = base._valueToUpdate;
/*  231 */     this._schema = base._schema;
/*  232 */     this._injectableValues = base._injectableValues;
/*  233 */     this._unwrapRoot = config.useRootWrapping();
/*  234 */     this._dataFormatReaders = base._dataFormatReaders;
/*  235 */     this._filter = base._filter;
/*      */   }
/*      */   
/*      */ 
/*      */   protected ObjectReader(ObjectReader base, JsonFactory f)
/*      */   {
/*  241 */     this._config = base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
/*      */     
/*  243 */     this._context = base._context;
/*      */     
/*  245 */     this._rootDeserializers = base._rootDeserializers;
/*  246 */     this._parserFactory = f;
/*      */     
/*  248 */     this._valueType = base._valueType;
/*  249 */     this._rootDeserializer = base._rootDeserializer;
/*  250 */     this._valueToUpdate = base._valueToUpdate;
/*  251 */     this._schema = base._schema;
/*  252 */     this._injectableValues = base._injectableValues;
/*  253 */     this._unwrapRoot = base._unwrapRoot;
/*  254 */     this._dataFormatReaders = base._dataFormatReaders;
/*  255 */     this._filter = base._filter;
/*      */   }
/*      */   
/*      */   protected ObjectReader(ObjectReader base, TokenFilter filter) {
/*  259 */     this._config = base._config;
/*  260 */     this._context = base._context;
/*  261 */     this._rootDeserializers = base._rootDeserializers;
/*  262 */     this._parserFactory = base._parserFactory;
/*  263 */     this._valueType = base._valueType;
/*  264 */     this._rootDeserializer = base._rootDeserializer;
/*  265 */     this._valueToUpdate = base._valueToUpdate;
/*  266 */     this._schema = base._schema;
/*  267 */     this._injectableValues = base._injectableValues;
/*  268 */     this._unwrapRoot = base._unwrapRoot;
/*  269 */     this._dataFormatReaders = base._dataFormatReaders;
/*  270 */     this._filter = filter;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.core.Version version()
/*      */   {
/*  279 */     return com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion.VERSION;
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
/*      */   protected ObjectReader _new(ObjectReader base, JsonFactory f)
/*      */   {
/*  296 */     return new ObjectReader(base, f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader _new(ObjectReader base, DeserializationConfig config)
/*      */   {
/*  305 */     return new ObjectReader(base, config);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader _new(ObjectReader base, DeserializationConfig config, JavaType valueType, JsonDeserializer<Object> rootDeser, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues, DataFormatReaders dataFormatReaders)
/*      */   {
/*  317 */     return new ObjectReader(base, config, valueType, rootDeser, valueToUpdate, schema, injectableValues, dataFormatReaders);
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
/*      */   protected <T> MappingIterator<T> _newIterator(JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean parserManaged)
/*      */   {
/*  330 */     return new MappingIterator(this._valueType, p, ctxt, deser, parserManaged, this._valueToUpdate);
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
/*      */   protected JsonToken _initForReading(DeserializationContext ctxt, JsonParser p)
/*      */     throws IOException
/*      */   {
/*  344 */     if (this._schema != null) {
/*  345 */       p.setSchema(this._schema);
/*      */     }
/*  347 */     this._config.initialize(p);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  353 */     JsonToken t = p.getCurrentToken();
/*  354 */     if (t == null) {
/*  355 */       t = p.nextToken();
/*  356 */       if (t == null)
/*      */       {
/*  358 */         ctxt.reportMissingContent(null, new Object[0]);
/*      */       }
/*      */     }
/*  361 */     return t;
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
/*      */   protected void _initForMultiRead(DeserializationContext ctxt, JsonParser p)
/*      */     throws IOException
/*      */   {
/*  376 */     if (this._schema != null) {
/*  377 */       p.setSchema(this._schema);
/*      */     }
/*  379 */     this._config.initialize(p);
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
/*      */   public ObjectReader with(DeserializationFeature feature)
/*      */   {
/*  393 */     return _with(this._config.with(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader with(DeserializationFeature first, DeserializationFeature... other)
/*      */   {
/*  403 */     return _with(this._config.with(first, other));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withFeatures(DeserializationFeature... features)
/*      */   {
/*  411 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader without(DeserializationFeature feature)
/*      */   {
/*  419 */     return _with(this._config.without(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader without(DeserializationFeature first, DeserializationFeature... other)
/*      */   {
/*  428 */     return _with(this._config.without(first, other));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withoutFeatures(DeserializationFeature... features)
/*      */   {
/*  436 */     return _with(this._config.withoutFeatures(features));
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
/*      */   public ObjectReader with(Feature feature)
/*      */   {
/*  450 */     return _with(this._config.with(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withFeatures(Feature... features)
/*      */   {
/*  458 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader without(Feature feature)
/*      */   {
/*  466 */     return _with(this._config.without(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withoutFeatures(Feature... features)
/*      */   {
/*  474 */     return _with(this._config.withoutFeatures(features));
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
/*      */   public ObjectReader with(FormatFeature feature)
/*      */   {
/*  490 */     return _with(this._config.with(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withFeatures(FormatFeature... features)
/*      */   {
/*  500 */     return _with(this._config.withFeatures(features));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader without(FormatFeature feature)
/*      */   {
/*  510 */     return _with(this._config.without(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withoutFeatures(FormatFeature... features)
/*      */   {
/*  520 */     return _with(this._config.withoutFeatures(features));
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
/*      */   public ObjectReader at(String value)
/*      */   {
/*  535 */     return new ObjectReader(this, new JsonPointerBasedFilter(value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader at(JsonPointer pointer)
/*      */   {
/*  544 */     return new ObjectReader(this, new JsonPointerBasedFilter(pointer));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader with(DeserializationConfig config)
/*      */   {
/*  555 */     return _with(config);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader with(InjectableValues injectableValues)
/*      */   {
/*  567 */     if (this._injectableValues == injectableValues) {
/*  568 */       return this;
/*      */     }
/*  570 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, injectableValues, this._dataFormatReaders);
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
/*      */   public ObjectReader with(JsonNodeFactory f)
/*      */   {
/*  584 */     return _with(this._config.with(f));
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
/*      */   public ObjectReader with(JsonFactory f)
/*      */   {
/*  599 */     if (f == this._parserFactory) {
/*  600 */       return this;
/*      */     }
/*  602 */     ObjectReader r = _new(this, f);
/*      */     
/*  604 */     if (f.getCodec() == null) {
/*  605 */       f.setCodec(r);
/*      */     }
/*  607 */     return r;
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
/*      */   public ObjectReader withRootName(String rootName)
/*      */   {
/*  620 */     return _with((DeserializationConfig)this._config.withRootName(rootName));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectReader withRootName(PropertyName rootName)
/*      */   {
/*  627 */     return _with(this._config.withRootName(rootName));
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
/*      */   public ObjectReader withoutRootName()
/*      */   {
/*  641 */     return _with(this._config.withRootName(PropertyName.NO_NAME));
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
/*      */   public ObjectReader with(FormatSchema schema)
/*      */   {
/*  654 */     if (this._schema == schema) {
/*  655 */       return this;
/*      */     }
/*  657 */     _verifySchemaType(schema);
/*  658 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, schema, this._injectableValues, this._dataFormatReaders);
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
/*      */   public ObjectReader forType(JavaType valueType)
/*      */   {
/*  673 */     if ((valueType != null) && (valueType.equals(this._valueType))) {
/*  674 */       return this;
/*      */     }
/*  676 */     JsonDeserializer<Object> rootDeser = _prefetchRootDeserializer(valueType);
/*      */     
/*  678 */     DataFormatReaders det = this._dataFormatReaders;
/*  679 */     if (det != null) {
/*  680 */       det = det.withType(valueType);
/*      */     }
/*  682 */     return _new(this, this._config, valueType, rootDeser, this._valueToUpdate, this._schema, this._injectableValues, det);
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
/*      */   public ObjectReader forType(Class<?> valueType)
/*      */   {
/*  696 */     return forType(this._config.constructType(valueType));
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
/*      */   public ObjectReader forType(TypeReference<?> valueTypeRef)
/*      */   {
/*  709 */     return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader withType(JavaType valueType)
/*      */   {
/*  717 */     return forType(valueType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader withType(Class<?> valueType)
/*      */   {
/*  725 */     return forType(this._config.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader withType(Type valueType)
/*      */   {
/*  733 */     return forType(this._config.getTypeFactory().constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader withType(TypeReference<?> valueTypeRef)
/*      */   {
/*  741 */     return forType(this._config.getTypeFactory().constructType(valueTypeRef.getType()));
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
/*      */   public ObjectReader withValueToUpdate(Object value)
/*      */   {
/*  754 */     if (value == this._valueToUpdate) return this;
/*  755 */     if (value == null) {
/*  756 */       throw new IllegalArgumentException("cat not update null value");
/*      */     }
/*      */     
/*      */ 
/*      */     JavaType t;
/*      */     
/*      */     JavaType t;
/*      */     
/*  764 */     if (this._valueType == null) {
/*  765 */       t = this._config.constructType(value.getClass());
/*      */     } else {
/*  767 */       t = this._valueType;
/*      */     }
/*  769 */     return _new(this, this._config, t, this._rootDeserializer, value, this._schema, this._injectableValues, this._dataFormatReaders);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader withView(Class<?> activeView)
/*      */   {
/*  781 */     return _with(this._config.withView(activeView));
/*      */   }
/*      */   
/*      */   public ObjectReader with(Locale l) {
/*  785 */     return _with(this._config.with(l));
/*      */   }
/*      */   
/*      */   public ObjectReader with(TimeZone tz) {
/*  789 */     return _with(this._config.with(tz));
/*      */   }
/*      */   
/*      */   public ObjectReader withHandler(DeserializationProblemHandler h) {
/*  793 */     return _with(this._config.withHandler(h));
/*      */   }
/*      */   
/*      */   public ObjectReader with(Base64Variant defaultBase64) {
/*  797 */     return _with(this._config.with(defaultBase64));
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
/*      */   public ObjectReader withFormatDetection(ObjectReader... readers)
/*      */   {
/*  823 */     return withFormatDetection(new DataFormatReaders(readers));
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
/*      */   public ObjectReader withFormatDetection(DataFormatReaders readers)
/*      */   {
/*  842 */     return _new(this, this._config, this._valueType, this._rootDeserializer, this._valueToUpdate, this._schema, this._injectableValues, readers);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader with(ContextAttributes attrs)
/*      */   {
/*  850 */     return _with(this._config.with(attrs));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectReader withAttributes(Map<?, ?> attrs)
/*      */   {
/*  857 */     return _with((DeserializationConfig)this._config.withAttributes(attrs));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectReader withAttribute(Object key, Object value)
/*      */   {
/*  864 */     return _with((DeserializationConfig)this._config.withAttribute(key, value));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectReader withoutAttribute(Object key)
/*      */   {
/*  871 */     return _with((DeserializationConfig)this._config.withoutAttribute(key));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader _with(DeserializationConfig newConfig)
/*      */   {
/*  881 */     if (newConfig == this._config) {
/*  882 */       return this;
/*      */     }
/*  884 */     ObjectReader r = _new(this, newConfig);
/*  885 */     if (this._dataFormatReaders != null) {
/*  886 */       r = r.withFormatDetection(this._dataFormatReaders.with(newConfig));
/*      */     }
/*  888 */     return r;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEnabled(DeserializationFeature f)
/*      */   {
/*  898 */     return this._config.isEnabled(f);
/*      */   }
/*      */   
/*      */   public boolean isEnabled(MapperFeature f) {
/*  902 */     return this._config.isEnabled(f);
/*      */   }
/*      */   
/*      */   public boolean isEnabled(Feature f) {
/*  906 */     return this._parserFactory.isEnabled(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DeserializationConfig getConfig()
/*      */   {
/*  913 */     return this._config;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonFactory getFactory()
/*      */   {
/*  921 */     return this._parserFactory;
/*      */   }
/*      */   
/*      */   public TypeFactory getTypeFactory() {
/*  925 */     return this._config.getTypeFactory();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ContextAttributes getAttributes()
/*      */   {
/*  932 */     return this._config.getAttributes();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InjectableValues getInjectableValues()
/*      */   {
/*  939 */     return this._injectableValues;
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
/*      */   public <T> T readValue(JsonParser p)
/*      */     throws IOException
/*      */   {
/*  961 */     return (T)_bind(p, this._valueToUpdate);
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
/*      */   public <T> T readValue(JsonParser p, Class<T> valueType)
/*      */     throws IOException
/*      */   {
/*  978 */     return (T)forType(valueType).readValue(p);
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
/*      */   public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef)
/*      */     throws IOException
/*      */   {
/*  995 */     return (T)forType(valueTypeRef).readValue(p);
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
/*      */   public <T> T readValue(JsonParser p, ResolvedType valueType)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1011 */     return (T)forType((JavaType)valueType).readValue(p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(JsonParser p, JavaType valueType)
/*      */     throws IOException
/*      */   {
/* 1022 */     return (T)forType(valueType).readValue(p);
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
/*      */   public <T> Iterator<T> readValues(JsonParser p, Class<T> valueType)
/*      */     throws IOException
/*      */   {
/* 1046 */     return forType(valueType).readValues(p);
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
/*      */   public <T> Iterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef)
/*      */     throws IOException
/*      */   {
/* 1070 */     return forType(valueTypeRef).readValues(p);
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
/*      */   public <T> Iterator<T> readValues(JsonParser p, ResolvedType valueType)
/*      */     throws IOException
/*      */   {
/* 1094 */     return readValues(p, (JavaType)valueType);
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
/*      */   public <T> Iterator<T> readValues(JsonParser p, JavaType valueType)
/*      */     throws IOException
/*      */   {
/* 1117 */     return forType(valueType).readValues(p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonNode createArrayNode()
/*      */   {
/* 1128 */     return this._config.getNodeFactory().arrayNode();
/*      */   }
/*      */   
/*      */   public JsonNode createObjectNode()
/*      */   {
/* 1133 */     return this._config.getNodeFactory().objectNode();
/*      */   }
/*      */   
/*      */   public JsonParser treeAsTokens(TreeNode n)
/*      */   {
/* 1138 */     return new com.facebook.presto.jdbc.internal.jackson.databind.node.TreeTraversingParser((JsonNode)n, this);
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
/*      */   public <T extends TreeNode> T readTree(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1155 */     return _bindAsTree(p);
/*      */   }
/*      */   
/*      */   public void writeTree(JsonGenerator jgen, TreeNode rootNode)
/*      */   {
/* 1160 */     throw new UnsupportedOperationException();
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
/*      */   public <T> T readValue(InputStream src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1179 */     if (this._dataFormatReaders != null) {
/* 1180 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(src), false);
/*      */     }
/*      */     
/* 1183 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(Reader src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1196 */     if (this._dataFormatReaders != null) {
/* 1197 */       _reportUndetectableSource(src);
/*      */     }
/*      */     
/* 1200 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(String src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1213 */     if (this._dataFormatReaders != null) {
/* 1214 */       _reportUndetectableSource(src);
/*      */     }
/*      */     
/* 1217 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(byte[] src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1230 */     if (this._dataFormatReaders != null) {
/* 1231 */       return (T)_detectBindAndClose(src, 0, src.length);
/*      */     }
/*      */     
/* 1234 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(byte[] src, int offset, int length)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1247 */     if (this._dataFormatReaders != null) {
/* 1248 */       return (T)_detectBindAndClose(src, offset, length);
/*      */     }
/*      */     
/* 1251 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src, offset, length), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readValue(File src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1259 */     if (this._dataFormatReaders != null) {
/* 1260 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
/*      */     }
/*      */     
/* 1263 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(URL src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1276 */     if (this._dataFormatReaders != null) {
/* 1277 */       return (T)_detectBindAndClose(this._dataFormatReaders.findFormat(_inputStream(src)), true);
/*      */     }
/*      */     
/* 1280 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
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
/*      */   public <T> T readValue(JsonNode src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1294 */     if (this._dataFormatReaders != null) {
/* 1295 */       _reportUndetectableSource(src);
/*      */     }
/*      */     
/* 1298 */     return (T)_bindAndClose(_considerFilter(treeAsTokens(src), false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(DataInput src)
/*      */     throws IOException
/*      */   {
/* 1307 */     if (this._dataFormatReaders != null) {
/* 1308 */       _reportUndetectableSource(src);
/*      */     }
/* 1310 */     return (T)_bindAndClose(_considerFilter(this._parserFactory.createParser(src), false));
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
/*      */   public JsonNode readTree(InputStream in)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1325 */     if (this._dataFormatReaders != null) {
/* 1326 */       return _detectBindAndCloseAsTree(in);
/*      */     }
/* 1328 */     return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(in), false));
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
/*      */   public JsonNode readTree(Reader r)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1343 */     if (this._dataFormatReaders != null) {
/* 1344 */       _reportUndetectableSource(r);
/*      */     }
/* 1346 */     return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(r), false));
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
/*      */   public JsonNode readTree(String json)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1361 */     if (this._dataFormatReaders != null) {
/* 1362 */       _reportUndetectableSource(json);
/*      */     }
/* 1364 */     return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(json), false));
/*      */   }
/*      */   
/*      */   public JsonNode readTree(DataInput src) throws IOException
/*      */   {
/* 1369 */     if (this._dataFormatReaders != null) {
/* 1370 */       _reportUndetectableSource(src);
/*      */     }
/* 1372 */     return _bindAndCloseAsTree(_considerFilter(this._parserFactory.createParser(src), false));
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1395 */     DeserializationContext ctxt = createDeserializationContext(p);
/*      */     
/* 1397 */     return _newIterator(p, ctxt, _findRootDeserializer(ctxt), false);
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
/*      */   public <T> MappingIterator<T> readValues(InputStream src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1423 */     if (this._dataFormatReaders != null) {
/* 1424 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src), false);
/*      */     }
/*      */     
/* 1427 */     return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(Reader src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1437 */     if (this._dataFormatReaders != null) {
/* 1438 */       _reportUndetectableSource(src);
/*      */     }
/* 1440 */     JsonParser p = _considerFilter(this._parserFactory.createParser(src), true);
/* 1441 */     DeserializationContext ctxt = createDeserializationContext(p);
/* 1442 */     _initForMultiRead(ctxt, p);
/* 1443 */     p.nextToken();
/* 1444 */     return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(String json)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1456 */     if (this._dataFormatReaders != null) {
/* 1457 */       _reportUndetectableSource(json);
/*      */     }
/* 1459 */     JsonParser p = _considerFilter(this._parserFactory.createParser(json), true);
/* 1460 */     DeserializationContext ctxt = createDeserializationContext(p);
/* 1461 */     _initForMultiRead(ctxt, p);
/* 1462 */     p.nextToken();
/* 1463 */     return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(byte[] src, int offset, int length)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1472 */     if (this._dataFormatReaders != null) {
/* 1473 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(src, offset, length), false);
/*      */     }
/* 1475 */     return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final <T> MappingIterator<T> readValues(byte[] src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1483 */     return readValues(src, 0, src.length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(File src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1492 */     if (this._dataFormatReaders != null) {
/* 1493 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), false);
/*      */     }
/*      */     
/* 1496 */     return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(URL src)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1507 */     if (this._dataFormatReaders != null) {
/* 1508 */       return _detectBindAndReadValues(this._dataFormatReaders.findFormat(_inputStream(src)), true);
/*      */     }
/*      */     
/* 1511 */     return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(DataInput src)
/*      */     throws IOException
/*      */   {
/* 1519 */     if (this._dataFormatReaders != null) {
/* 1520 */       _reportUndetectableSource(src);
/*      */     }
/* 1522 */     return _bindAndReadValues(_considerFilter(this._parserFactory.createParser(src), true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T treeToValue(TreeNode n, Class<T> valueType)
/*      */     throws JsonProcessingException
/*      */   {
/*      */     try
/*      */     {
/* 1535 */       return (T)readValue(treeAsTokens(n), valueType);
/*      */     } catch (JsonProcessingException e) {
/* 1537 */       throw e;
/*      */     } catch (IOException e) {
/* 1539 */       throw new IllegalArgumentException(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */   
/*      */   public void writeValue(JsonGenerator gen, Object value) throws IOException, JsonProcessingException
/*      */   {
/* 1545 */     throw new UnsupportedOperationException("Not implemented for ObjectReader");
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
/*      */   protected Object _bind(JsonParser p, Object valueToUpdate)
/*      */     throws IOException
/*      */   {
/* 1563 */     DeserializationContext ctxt = createDeserializationContext(p);
/* 1564 */     JsonToken t = _initForReading(ctxt, p);
/* 1565 */     Object result; Object result; if (t == JsonToken.VALUE_NULL) { Object result;
/* 1566 */       if (valueToUpdate == null) {
/* 1567 */         result = _findRootDeserializer(ctxt).getNullValue(ctxt);
/*      */       } else
/* 1569 */         result = valueToUpdate;
/*      */     } else { Object result;
/* 1571 */       if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 1572 */         result = valueToUpdate;
/*      */       } else {
/* 1574 */         JsonDeserializer<Object> deser = _findRootDeserializer(ctxt);
/* 1575 */         Object result; if (this._unwrapRoot) {
/* 1576 */           result = _unwrapAndDeserialize(p, ctxt, this._valueType, deser);
/*      */         } else { Object result;
/* 1578 */           if (valueToUpdate == null) {
/* 1579 */             result = deser.deserialize(p, ctxt);
/*      */           } else {
/* 1581 */             deser.deserialize(p, ctxt, valueToUpdate);
/* 1582 */             result = valueToUpdate;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1587 */     p.clearCurrentToken();
/* 1588 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonParser _considerFilter(JsonParser p, boolean multiValue)
/*      */   {
/* 1597 */     return (this._filter == null) || (FilteringParserDelegate.class.isInstance(p)) ? p : new FilteringParserDelegate(p, this._filter, false, multiValue);
/*      */   }
/*      */   
/*      */   protected Object _bindAndClose(JsonParser p0)
/*      */     throws IOException
/*      */   {
/* 1603 */     JsonParser p = p0;Throwable localThrowable2 = null;
/*      */     try
/*      */     {
/* 1606 */       DeserializationContext ctxt = createDeserializationContext(p);
/* 1607 */       JsonToken t = _initForReading(ctxt, p);
/* 1608 */       Object result; JsonDeserializer<Object> deser; Object result; if (t == JsonToken.VALUE_NULL) { Object result;
/* 1609 */         if (this._valueToUpdate == null) {
/* 1610 */           result = _findRootDeserializer(ctxt).getNullValue(ctxt);
/*      */         } else
/* 1612 */           result = this._valueToUpdate;
/*      */       } else { Object result;
/* 1614 */         if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 1615 */           result = this._valueToUpdate;
/*      */         } else {
/* 1617 */           deser = _findRootDeserializer(ctxt);
/* 1618 */           Object result; if (this._unwrapRoot) {
/* 1619 */             result = _unwrapAndDeserialize(p, ctxt, this._valueType, deser);
/*      */           } else { Object result;
/* 1621 */             if (this._valueToUpdate == null) {
/* 1622 */               result = deser.deserialize(p, ctxt);
/*      */             } else {
/* 1624 */               deser.deserialize(p, ctxt, this._valueToUpdate);
/* 1625 */               result = this._valueToUpdate;
/*      */             }
/*      */           }
/*      */         } }
/* 1629 */       return (JsonDeserializer<Object>)result;
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 1603 */       localThrowable2 = localThrowable1;throw localThrowable1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1630 */       if (p != null) if (localThrowable2 != null) try { p.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else p.close();
/*      */     }
/*      */   }
/*      */   
/* 1634 */   protected JsonNode _bindAndCloseAsTree(JsonParser p0) throws IOException { JsonParser p = p0;Throwable localThrowable2 = null;
/* 1635 */     try { return _bindAsTree(p);
/*      */     }
/*      */     catch (Throwable localThrowable3)
/*      */     {
/* 1634 */       localThrowable2 = localThrowable3;throw localThrowable3;
/*      */     } finally {
/* 1636 */       if (p != null) if (localThrowable2 != null) try { p.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else p.close();
/*      */     }
/*      */   }
/*      */   
/*      */   protected JsonNode _bindAsTree(JsonParser p) throws IOException
/*      */   {
/* 1642 */     DeserializationContext ctxt = createDeserializationContext(p);
/* 1643 */     JsonToken t = _initForReading(ctxt, p);
/* 1644 */     JsonNode result; JsonNode result; if ((t == JsonToken.VALUE_NULL) || (t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 1645 */       result = com.facebook.presto.jdbc.internal.jackson.databind.node.NullNode.instance;
/*      */     } else {
/* 1647 */       JsonDeserializer<Object> deser = _findTreeDeserializer(ctxt);
/* 1648 */       JsonNode result; if (this._unwrapRoot) {
/* 1649 */         result = (JsonNode)_unwrapAndDeserialize(p, ctxt, JSON_NODE_TYPE, deser);
/*      */       } else {
/* 1651 */         result = (JsonNode)deser.deserialize(p, ctxt);
/*      */       }
/*      */     }
/*      */     
/* 1655 */     p.clearCurrentToken();
/* 1656 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected <T> MappingIterator<T> _bindAndReadValues(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 1664 */     DeserializationContext ctxt = createDeserializationContext(p);
/* 1665 */     _initForMultiRead(ctxt, p);
/* 1666 */     p.nextToken();
/* 1667 */     return _newIterator(p, ctxt, _findRootDeserializer(ctxt), true);
/*      */   }
/*      */   
/*      */   protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, JavaType rootType, JsonDeserializer<Object> deser)
/*      */     throws IOException
/*      */   {
/* 1673 */     PropertyName expRootName = this._config.findRootName(rootType);
/*      */     
/* 1675 */     String expSimpleName = expRootName.getSimpleName();
/*      */     
/* 1677 */     if (p.getCurrentToken() != JsonToken.START_OBJECT) {
/* 1678 */       ctxt.reportWrongTokenException(p, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() });
/*      */     }
/*      */     
/*      */ 
/* 1682 */     if (p.nextToken() != JsonToken.FIELD_NAME) {
/* 1683 */       ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() });
/*      */     }
/*      */     
/*      */ 
/* 1687 */     String actualName = p.getCurrentName();
/* 1688 */     if (!expSimpleName.equals(actualName)) {
/* 1689 */       ctxt.reportMappingException("Root name '%s' does not match expected ('%s') for type %s", new Object[] { actualName, expSimpleName, rootType });
/*      */     }
/*      */     
/*      */ 
/* 1693 */     p.nextToken();
/*      */     Object result;
/* 1695 */     Object result; if (this._valueToUpdate == null) {
/* 1696 */       result = deser.deserialize(p, ctxt);
/*      */     } else {
/* 1698 */       deser.deserialize(p, ctxt, this._valueToUpdate);
/* 1699 */       result = this._valueToUpdate;
/*      */     }
/*      */     
/* 1702 */     if (p.nextToken() != JsonToken.END_OBJECT) {
/* 1703 */       ctxt.reportWrongTokenException(p, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() });
/*      */     }
/*      */     
/*      */ 
/* 1707 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object _detectBindAndClose(byte[] src, int offset, int length)
/*      */     throws IOException
/*      */   {
/* 1719 */     Match match = this._dataFormatReaders.findFormat(src, offset, length);
/* 1720 */     if (!match.hasMatch()) {
/* 1721 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 1723 */     JsonParser p = match.createParserWithMatch();
/* 1724 */     return match.getReader()._bindAndClose(p);
/*      */   }
/*      */   
/*      */ 
/*      */   protected Object _detectBindAndClose(Match match, boolean forceClosing)
/*      */     throws IOException
/*      */   {
/* 1731 */     if (!match.hasMatch()) {
/* 1732 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 1734 */     JsonParser p = match.createParserWithMatch();
/*      */     
/*      */ 
/* 1737 */     if (forceClosing) {
/* 1738 */       p.enable(Feature.AUTO_CLOSE_SOURCE);
/*      */     }
/*      */     
/* 1741 */     return match.getReader()._bindAndClose(p);
/*      */   }
/*      */   
/*      */ 
/*      */   protected <T> MappingIterator<T> _detectBindAndReadValues(Match match, boolean forceClosing)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 1748 */     if (!match.hasMatch()) {
/* 1749 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 1751 */     JsonParser p = match.createParserWithMatch();
/*      */     
/*      */ 
/* 1754 */     if (forceClosing) {
/* 1755 */       p.enable(Feature.AUTO_CLOSE_SOURCE);
/*      */     }
/*      */     
/* 1758 */     return match.getReader()._bindAndReadValues(p);
/*      */   }
/*      */   
/*      */   protected JsonNode _detectBindAndCloseAsTree(InputStream in)
/*      */     throws IOException
/*      */   {
/* 1764 */     Match match = this._dataFormatReaders.findFormat(in);
/* 1765 */     if (!match.hasMatch()) {
/* 1766 */       _reportUnkownFormat(this._dataFormatReaders, match);
/*      */     }
/* 1768 */     JsonParser p = match.createParserWithMatch();
/* 1769 */     p.enable(Feature.AUTO_CLOSE_SOURCE);
/* 1770 */     return match.getReader()._bindAndCloseAsTree(p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _reportUnkownFormat(DataFormatReaders detector, Match match)
/*      */     throws JsonProcessingException
/*      */   {
/* 1780 */     throw new com.facebook.presto.jdbc.internal.jackson.core.JsonParseException(null, "Can not detect format from input, does not look like any of detectable formats " + detector.toString());
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
/*      */   protected void _verifySchemaType(FormatSchema schema)
/*      */   {
/* 1795 */     if ((schema != null) && 
/* 1796 */       (!this._parserFactory.canUseSchema(schema))) {
/* 1797 */       throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._parserFactory.getFormatName());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DefaultDeserializationContext createDeserializationContext(JsonParser p)
/*      */   {
/* 1809 */     return this._context.createInstance(this._config, p, this._injectableValues);
/*      */   }
/*      */   
/*      */   protected void _reportUndetectableSource(Object src)
/*      */     throws JsonProcessingException
/*      */   {
/* 1815 */     throw new com.facebook.presto.jdbc.internal.jackson.core.JsonParseException(null, "Can not use source of type " + src.getClass().getName() + " with format auto-detection: must be byte- not char-based");
/*      */   }
/*      */   
/*      */   protected InputStream _inputStream(URL src) throws IOException
/*      */   {
/* 1820 */     return src.openStream();
/*      */   }
/*      */   
/*      */   protected InputStream _inputStream(File f) throws IOException {
/* 1824 */     return new java.io.FileInputStream(f);
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
/*      */   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt)
/*      */     throws JsonMappingException
/*      */   {
/* 1839 */     if (this._rootDeserializer != null) {
/* 1840 */       return this._rootDeserializer;
/*      */     }
/*      */     
/*      */ 
/* 1844 */     JavaType t = this._valueType;
/* 1845 */     if (t == null) {
/* 1846 */       ctxt.reportMappingException("No value type configured for ObjectReader", new Object[0]);
/*      */     }
/*      */     
/*      */ 
/* 1850 */     JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(t);
/* 1851 */     if (deser != null) {
/* 1852 */       return deser;
/*      */     }
/*      */     
/* 1855 */     deser = ctxt.findRootValueDeserializer(t);
/* 1856 */     if (deser == null) {
/* 1857 */       ctxt.reportMappingException("Can not find a deserializer for type %s", new Object[] { t });
/*      */     }
/* 1859 */     this._rootDeserializers.put(t, deser);
/* 1860 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> _findTreeDeserializer(DeserializationContext ctxt)
/*      */     throws JsonMappingException
/*      */   {
/* 1869 */     JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(JSON_NODE_TYPE);
/* 1870 */     if (deser == null)
/*      */     {
/* 1872 */       deser = ctxt.findRootValueDeserializer(JSON_NODE_TYPE);
/* 1873 */       if (deser == null) {
/* 1874 */         ctxt.reportMappingException("Can not find a deserializer for type %s", new Object[] { JSON_NODE_TYPE });
/*      */       }
/*      */       
/* 1877 */       this._rootDeserializers.put(JSON_NODE_TYPE, deser);
/*      */     }
/* 1879 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> _prefetchRootDeserializer(JavaType valueType)
/*      */   {
/* 1889 */     if ((valueType == null) || (!this._config.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH))) {
/* 1890 */       return null;
/*      */     }
/*      */     
/* 1893 */     JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(valueType);
/* 1894 */     if (deser == null) {
/*      */       try
/*      */       {
/* 1897 */         DeserializationContext ctxt = createDeserializationContext(null);
/* 1898 */         deser = ctxt.findRootValueDeserializer(valueType);
/* 1899 */         if (deser != null) {
/* 1900 */           this._rootDeserializers.put(valueType, deser);
/*      */         }
/* 1902 */         return deser;
/*      */       }
/*      */       catch (JsonProcessingException e) {}
/*      */     }
/*      */     
/* 1907 */     return deser;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ObjectReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */