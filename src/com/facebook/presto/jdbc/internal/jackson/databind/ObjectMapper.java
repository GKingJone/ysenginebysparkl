/*      */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonAutoDetect.Visibility;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.PropertyAccessor;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variant;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Base64Variants;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.FormatSchema;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonEncoding;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonFactory.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParseException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.PrettyPrinter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.TreeNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.Versioned;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.SegmentedStringWriter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.type.ResolvedType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.type.TypeReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.util.ByteArrayBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.BaseSettings;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ConfigOverrides;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ContextAttributes;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.HandlerInstantiator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MutableConfigOverride;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.BeanDeserializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.BeanDeserializerModifier;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DefaultDeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DefaultDeserializationContext.Impl;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializationProblemHandler;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.DeserializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.Deserializers;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.KeyDeserializers;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiators;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BasicClassIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ClassIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ClassIntrospector.MixInResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.JacksonAnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.SimpleMixInResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.VisibilityChecker;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.VisibilityChecker.Std;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.JsonSchema;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.SubtypeResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdSubtypeResolver;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ArrayNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.JsonNodeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.NullNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.POJONode;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.node.TreeTraversingParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerModifier;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.DefaultSerializerProvider;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.DefaultSerializerProvider.Impl;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.FilterProvider;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.SerializerFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.SimpleType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeModifier;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.RootNameLookup;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.StdDateFormat;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataOutput;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URL;
/*      */ import java.text.DateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.ServiceLoader;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
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
/*      */ public class ObjectMapper
/*      */   extends ObjectCodec
/*      */   implements Versioned, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   
/*      */   public static enum DefaultTyping
/*      */   {
/*  145 */     JAVA_LANG_OBJECT, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  155 */     OBJECT_AND_NON_CONCRETE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  164 */     NON_CONCRETE_AND_ARRAYS, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  175 */     NON_FINAL;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private DefaultTyping() {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class DefaultTypeResolverBuilder
/*      */     extends StdTypeResolverBuilder
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */ 
/*      */ 
/*      */     protected final DefaultTyping _appliesFor;
/*      */     
/*      */ 
/*      */ 
/*      */     public DefaultTypeResolverBuilder(DefaultTyping t)
/*      */     {
/*  200 */       this._appliesFor = t;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
/*      */     {
/*  207 */       return useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes)
/*      */     {
/*  214 */       return useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean useForType(JavaType t)
/*      */     {
/*  227 */       switch (ObjectMapper.2.$SwitchMap$com$fasterxml$jackson$databind$ObjectMapper$DefaultTyping[this._appliesFor.ordinal()]) {
/*      */       case 1: 
/*  229 */         while (t.isArrayType()) {
/*  230 */           t = t.getContentType();
/*      */         }
/*      */       
/*      */ 
/*      */       case 2: 
/*  235 */         while (t.isReferenceType()) {
/*  236 */           t = t.getReferencedType();
/*      */         }
/*  238 */         return (t.isJavaLangObject()) || ((!t.isConcrete()) && (!TreeNode.class.isAssignableFrom(t.getRawClass())));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       case 3: 
/*  244 */         while (t.isArrayType()) {
/*  245 */           t = t.getContentType();
/*      */         }
/*      */         
/*  248 */         while (t.isReferenceType()) {
/*  249 */           t = t.getReferencedType();
/*      */         }
/*      */         
/*  252 */         return (!t.isFinal()) && (!TreeNode.class.isAssignableFrom(t.getRawClass()));
/*      */       }
/*      */       
/*  255 */       return t.isJavaLangObject();
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
/*  269 */   private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  274 */   protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
/*      */   
/*  276 */   protected static final VisibilityChecker<?> STD_VISIBILITY_CHECKER = Std.defaultInstance();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  282 */   protected static final BaseSettings DEFAULT_BASE = new BaseSettings(null, DEFAULT_ANNOTATION_INTROSPECTOR, STD_VISIBILITY_CHECKER, null, TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, Locale.getDefault(), null, Base64Variants.getDefaultVariant());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JsonFactory _jsonFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TypeFactory _typeFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InjectableValues _injectableValues;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubtypeResolver _subtypeResolver;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ConfigOverrides _propertyOverrides;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SimpleMixInResolver _mixIns;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializationConfig _serializationConfig;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DefaultSerializerProvider _serializerProvider;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SerializerFactory _serializerFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeserializationConfig _deserializationConfig;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DefaultDeserializationContext _deserializationContext;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Set<Object> _registeredModuleTypes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  443 */   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap(64, 0.6F, 2);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper()
/*      */   {
/*  465 */     this(null, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper(JsonFactory jf)
/*      */   {
/*  474 */     this(jf, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectMapper(ObjectMapper src)
/*      */   {
/*  484 */     this._jsonFactory = src._jsonFactory.copy();
/*  485 */     this._jsonFactory.setCodec(this);
/*  486 */     this._subtypeResolver = src._subtypeResolver;
/*  487 */     this._typeFactory = src._typeFactory;
/*  488 */     this._injectableValues = src._injectableValues;
/*  489 */     this._propertyOverrides = src._propertyOverrides.copy();
/*  490 */     this._mixIns = src._mixIns.copy();
/*      */     
/*  492 */     RootNameLookup rootNames = new RootNameLookup();
/*  493 */     this._serializationConfig = new SerializationConfig(src._serializationConfig, this._mixIns, rootNames, this._propertyOverrides);
/*  494 */     this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, this._mixIns, rootNames, this._propertyOverrides);
/*  495 */     this._serializerProvider = src._serializerProvider.copy();
/*  496 */     this._deserializationContext = src._deserializationContext.copy();
/*      */     
/*      */ 
/*  499 */     this._serializerFactory = src._serializerFactory;
/*      */     
/*      */ 
/*  502 */     Set<Object> reg = src._registeredModuleTypes;
/*  503 */     if (reg == null) {
/*  504 */       this._registeredModuleTypes = null;
/*      */     } else {
/*  506 */       this._registeredModuleTypes = new LinkedHashSet(reg);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc)
/*      */   {
/*  531 */     if (jf == null) {
/*  532 */       this._jsonFactory = new MappingJsonFactory(this);
/*      */     } else {
/*  534 */       this._jsonFactory = jf;
/*  535 */       if (jf.getCodec() == null) {
/*  536 */         this._jsonFactory.setCodec(this);
/*      */       }
/*      */     }
/*  539 */     this._subtypeResolver = new StdSubtypeResolver();
/*  540 */     RootNameLookup rootNames = new RootNameLookup();
/*      */     
/*  542 */     this._typeFactory = TypeFactory.defaultInstance();
/*      */     
/*  544 */     SimpleMixInResolver mixins = new SimpleMixInResolver(null);
/*  545 */     this._mixIns = mixins;
/*  546 */     BaseSettings base = DEFAULT_BASE.withClassIntrospector(defaultClassIntrospector());
/*  547 */     ConfigOverrides propOverrides = new ConfigOverrides();
/*  548 */     this._propertyOverrides = propOverrides;
/*  549 */     this._serializationConfig = new SerializationConfig(base, this._subtypeResolver, mixins, rootNames, propOverrides);
/*      */     
/*  551 */     this._deserializationConfig = new DeserializationConfig(base, this._subtypeResolver, mixins, rootNames, propOverrides);
/*      */     
/*      */ 
/*      */ 
/*  555 */     boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
/*  556 */     if ((needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY))) {
/*  557 */       configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
/*      */     }
/*      */     
/*  560 */     this._serializerProvider = (sp == null ? new DefaultSerializerProvider.Impl() : sp);
/*  561 */     this._deserializationContext = (dc == null ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
/*      */     
/*      */ 
/*      */ 
/*  565 */     this._serializerFactory = BeanSerializerFactory.instance;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ClassIntrospector defaultClassIntrospector()
/*      */   {
/*  575 */     return new BasicClassIntrospector();
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
/*      */   public ObjectMapper copy()
/*      */   {
/*  600 */     _checkInvalidCopy(ObjectMapper.class);
/*  601 */     return new ObjectMapper(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _checkInvalidCopy(Class<?> exp)
/*      */   {
/*  609 */     if (getClass() != exp) {
/*  610 */       throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to");
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
/*      */ 
/*      */   protected ObjectReader _newReader(DeserializationConfig config)
/*      */   {
/*  629 */     return new ObjectReader(this, config);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectReader _newReader(DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues)
/*      */   {
/*  641 */     return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectWriter _newWriter(SerializationConfig config)
/*      */   {
/*  651 */     return new ObjectWriter(this, config);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema)
/*      */   {
/*  661 */     return new ObjectWriter(this, config, schema);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp)
/*      */   {
/*  672 */     return new ObjectWriter(this, config, rootType, pp);
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
/*      */   public Version version()
/*      */   {
/*  687 */     return PackageVersion.VERSION;
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
/*      */   public ObjectMapper registerModule(Module module)
/*      */   {
/*  705 */     if (isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
/*  706 */       Object typeId = module.getTypeId();
/*  707 */       if (typeId != null) {
/*  708 */         if (this._registeredModuleTypes == null)
/*      */         {
/*      */ 
/*  711 */           this._registeredModuleTypes = new LinkedHashSet();
/*      */         }
/*      */         
/*  714 */         if (!this._registeredModuleTypes.add(typeId)) {
/*  715 */           return this;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  724 */     String name = module.getModuleName();
/*  725 */     if (name == null) {
/*  726 */       throw new IllegalArgumentException("Module without defined name");
/*      */     }
/*  728 */     Version version = module.version();
/*  729 */     if (version == null) {
/*  730 */       throw new IllegalArgumentException("Module without defined version");
/*      */     }
/*      */     
/*  733 */     final ObjectMapper mapper = this;
/*      */     
/*      */ 
/*  736 */     module.setupModule(new Module.SetupContext()
/*      */     {
/*      */ 
/*      */       public Version getMapperVersion()
/*      */       {
/*      */ 
/*  742 */         return ObjectMapper.this.version();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public <C extends ObjectCodec> C getOwner()
/*      */       {
/*  749 */         return mapper;
/*      */       }
/*      */       
/*      */       public TypeFactory getTypeFactory()
/*      */       {
/*  754 */         return ObjectMapper.this._typeFactory;
/*      */       }
/*      */       
/*      */       public boolean isEnabled(MapperFeature f)
/*      */       {
/*  759 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */       public boolean isEnabled(DeserializationFeature f)
/*      */       {
/*  764 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */       public boolean isEnabled(SerializationFeature f)
/*      */       {
/*  769 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */       public boolean isEnabled(JsonFactory.Feature f)
/*      */       {
/*  774 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */       public boolean isEnabled(JsonParser.Feature f)
/*      */       {
/*  779 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */       public boolean isEnabled(JsonGenerator.Feature f)
/*      */       {
/*  784 */         return mapper.isEnabled(f);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public MutableConfigOverride configOverride(Class<?> type)
/*      */       {
/*  791 */         return mapper.configOverride(type);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addDeserializers(Deserializers d)
/*      */       {
/*  798 */         DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalDeserializers(d);
/*  799 */         mapper._deserializationContext = mapper._deserializationContext.with(df);
/*      */       }
/*      */       
/*      */       public void addKeyDeserializers(KeyDeserializers d)
/*      */       {
/*  804 */         DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalKeyDeserializers(d);
/*  805 */         mapper._deserializationContext = mapper._deserializationContext.with(df);
/*      */       }
/*      */       
/*      */       public void addBeanDeserializerModifier(BeanDeserializerModifier modifier)
/*      */       {
/*  810 */         DeserializerFactory df = mapper._deserializationContext._factory.withDeserializerModifier(modifier);
/*  811 */         mapper._deserializationContext = mapper._deserializationContext.with(df);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addSerializers(Serializers s)
/*      */       {
/*  818 */         mapper._serializerFactory = mapper._serializerFactory.withAdditionalSerializers(s);
/*      */       }
/*      */       
/*      */       public void addKeySerializers(Serializers s)
/*      */       {
/*  823 */         mapper._serializerFactory = mapper._serializerFactory.withAdditionalKeySerializers(s);
/*      */       }
/*      */       
/*      */       public void addBeanSerializerModifier(BeanSerializerModifier modifier)
/*      */       {
/*  828 */         mapper._serializerFactory = mapper._serializerFactory.withSerializerModifier(modifier);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addAbstractTypeResolver(AbstractTypeResolver resolver)
/*      */       {
/*  835 */         DeserializerFactory df = mapper._deserializationContext._factory.withAbstractTypeResolver(resolver);
/*  836 */         mapper._deserializationContext = mapper._deserializationContext.with(df);
/*      */       }
/*      */       
/*      */       public void addTypeModifier(TypeModifier modifier)
/*      */       {
/*  841 */         TypeFactory f = mapper._typeFactory;
/*  842 */         f = f.withModifier(modifier);
/*  843 */         mapper.setTypeFactory(f);
/*      */       }
/*      */       
/*      */       public void addValueInstantiators(ValueInstantiators instantiators)
/*      */       {
/*  848 */         DeserializerFactory df = mapper._deserializationContext._factory.withValueInstantiators(instantiators);
/*  849 */         mapper._deserializationContext = mapper._deserializationContext.with(df);
/*      */       }
/*      */       
/*      */       public void setClassIntrospector(ClassIntrospector ci)
/*      */       {
/*  854 */         mapper._deserializationConfig = mapper._deserializationConfig.with(ci);
/*  855 */         mapper._serializationConfig = mapper._serializationConfig.with(ci);
/*      */       }
/*      */       
/*      */       public void insertAnnotationIntrospector(AnnotationIntrospector ai)
/*      */       {
/*  860 */         mapper._deserializationConfig = mapper._deserializationConfig.withInsertedAnnotationIntrospector(ai);
/*  861 */         mapper._serializationConfig = mapper._serializationConfig.withInsertedAnnotationIntrospector(ai);
/*      */       }
/*      */       
/*      */       public void appendAnnotationIntrospector(AnnotationIntrospector ai)
/*      */       {
/*  866 */         mapper._deserializationConfig = mapper._deserializationConfig.withAppendedAnnotationIntrospector(ai);
/*  867 */         mapper._serializationConfig = mapper._serializationConfig.withAppendedAnnotationIntrospector(ai);
/*      */       }
/*      */       
/*      */       public void registerSubtypes(Class<?>... subtypes)
/*      */       {
/*  872 */         mapper.registerSubtypes(subtypes);
/*      */       }
/*      */       
/*      */       public void registerSubtypes(NamedType... subtypes)
/*      */       {
/*  877 */         mapper.registerSubtypes(subtypes);
/*      */       }
/*      */       
/*      */       public void setMixInAnnotations(Class<?> target, Class<?> mixinSource)
/*      */       {
/*  882 */         mapper.addMixIn(target, mixinSource);
/*      */       }
/*      */       
/*      */       public void addDeserializationProblemHandler(DeserializationProblemHandler handler)
/*      */       {
/*  887 */         mapper.addHandler(handler);
/*      */       }
/*      */       
/*      */       public void setNamingStrategy(PropertyNamingStrategy naming)
/*      */       {
/*  892 */         mapper.setPropertyNamingStrategy(naming);
/*      */       }
/*  894 */     });
/*  895 */     return this;
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
/*      */   public ObjectMapper registerModules(Module... modules)
/*      */   {
/*  911 */     for (Module module : modules) {
/*  912 */       registerModule(module);
/*      */     }
/*  914 */     return this;
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
/*      */   public ObjectMapper registerModules(Iterable<Module> modules)
/*      */   {
/*  930 */     for (Module module : modules) {
/*  931 */       registerModule(module);
/*      */     }
/*  933 */     return this;
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
/*      */   public static List<Module> findModules()
/*      */   {
/*  946 */     return findModules(null);
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
/*      */   public static List<Module> findModules(ClassLoader classLoader)
/*      */   {
/*  960 */     ArrayList<Module> modules = new ArrayList();
/*  961 */     ServiceLoader<Module> loader = classLoader == null ? ServiceLoader.load(Module.class) : ServiceLoader.load(Module.class, classLoader);
/*      */     
/*  963 */     for (Module module : loader) {
/*  964 */       modules.add(module);
/*      */     }
/*  966 */     return modules;
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
/*      */   public ObjectMapper findAndRegisterModules()
/*      */   {
/*  982 */     return registerModules(findModules());
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
/*      */   public SerializationConfig getSerializationConfig()
/*      */   {
/* 1000 */     return this._serializationConfig;
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
/*      */   public DeserializationConfig getDeserializationConfig()
/*      */   {
/* 1013 */     return this._deserializationConfig;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DeserializationContext getDeserializationContext()
/*      */   {
/* 1024 */     return this._deserializationContext;
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
/*      */   public ObjectMapper setSerializerFactory(SerializerFactory f)
/*      */   {
/* 1038 */     this._serializerFactory = f;
/* 1039 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SerializerFactory getSerializerFactory()
/*      */   {
/* 1050 */     return this._serializerFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setSerializerProvider(DefaultSerializerProvider p)
/*      */   {
/* 1059 */     this._serializerProvider = p;
/* 1060 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SerializerProvider getSerializerProvider()
/*      */   {
/* 1071 */     return this._serializerProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SerializerProvider getSerializerProviderInstance()
/*      */   {
/* 1083 */     return _serializerProvider(this._serializationConfig);
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
/*      */ 
/*      */ 
/*      */   public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins)
/*      */   {
/* 1112 */     this._mixIns.setLocalDefinitions(sourceMixins);
/* 1113 */     return this;
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
/*      */   public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource)
/*      */   {
/* 1130 */     this._mixIns.addLocalDefinition(target, mixinSource);
/* 1131 */     return this;
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
/*      */   public ObjectMapper setMixInResolver(MixInResolver resolver)
/*      */   {
/* 1144 */     SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
/* 1145 */     if (r != this._mixIns) {
/* 1146 */       this._mixIns = r;
/* 1147 */       this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
/* 1148 */       this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
/*      */     }
/* 1150 */     return this;
/*      */   }
/*      */   
/*      */   public Class<?> findMixInClassFor(Class<?> cls) {
/* 1154 */     return this._mixIns.findMixInClassFor(cls);
/*      */   }
/*      */   
/*      */   public int mixInCount()
/*      */   {
/* 1159 */     return this._mixIns.localSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins)
/*      */   {
/* 1167 */     setMixIns(sourceMixins);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource)
/*      */   {
/* 1175 */     addMixIn(target, mixinSource);
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
/*      */   public VisibilityChecker<?> getVisibilityChecker()
/*      */   {
/* 1190 */     return this._serializationConfig.getDefaultVisibilityChecker();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void setVisibilityChecker(VisibilityChecker<?> vc)
/*      */   {
/* 1198 */     setVisibility(vc);
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
/*      */   public ObjectMapper setVisibility(VisibilityChecker<?> vc)
/*      */   {
/* 1211 */     this._deserializationConfig = this._deserializationConfig.with(vc);
/* 1212 */     this._serializationConfig = this._serializationConfig.with(vc);
/* 1213 */     return this;
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
/*      */ 
/*      */ 
/*      */   public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility)
/*      */   {
/* 1242 */     this._deserializationConfig = this._deserializationConfig.withVisibility(forMethod, visibility);
/* 1243 */     this._serializationConfig = this._serializationConfig.withVisibility(forMethod, visibility);
/* 1244 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SubtypeResolver getSubtypeResolver()
/*      */   {
/* 1251 */     return this._subtypeResolver;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectMapper setSubtypeResolver(SubtypeResolver str)
/*      */   {
/* 1258 */     this._subtypeResolver = str;
/* 1259 */     this._deserializationConfig = this._deserializationConfig.with(str);
/* 1260 */     this._serializationConfig = this._serializationConfig.with(str);
/* 1261 */     return this;
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
/*      */   public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai)
/*      */   {
/* 1275 */     this._serializationConfig = this._serializationConfig.with(ai);
/* 1276 */     this._deserializationConfig = this._deserializationConfig.with(ai);
/* 1277 */     return this;
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
/*      */   public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI)
/*      */   {
/* 1297 */     this._serializationConfig = this._serializationConfig.with(serializerAI);
/* 1298 */     this._deserializationConfig = this._deserializationConfig.with(deserializerAI);
/* 1299 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s)
/*      */   {
/* 1306 */     this._serializationConfig = this._serializationConfig.with(s);
/* 1307 */     this._deserializationConfig = this._deserializationConfig.with(s);
/* 1308 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PropertyNamingStrategy getPropertyNamingStrategy()
/*      */   {
/* 1316 */     return this._serializationConfig.getPropertyNamingStrategy();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setSerializationInclusion(JsonInclude.Include incl)
/*      */   {
/* 1326 */     setPropertyInclusion(JsonInclude.Value.construct(incl, JsonInclude.Include.USE_DEFAULTS));
/* 1327 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setPropertyInclusion(JsonInclude.Value incl)
/*      */   {
/* 1336 */     this._serializationConfig = this._serializationConfig.withPropertyInclusion(incl);
/* 1337 */     return this;
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
/*      */   public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp)
/*      */   {
/* 1351 */     this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
/* 1352 */     return this;
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
/*      */   public ObjectMapper enableDefaultTyping()
/*      */   {
/* 1368 */     return enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enableDefaultTyping(DefaultTyping dti)
/*      */   {
/* 1378 */     return enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
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
/*      */   public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs)
/*      */   {
/* 1398 */     if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/* 1399 */       throw new IllegalArgumentException("Can not use includeAs of " + includeAs);
/*      */     }
/*      */     
/* 1402 */     TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
/*      */     
/* 1404 */     typer = typer.init(JsonTypeInfo.Id.CLASS, null);
/* 1405 */     typer = typer.inclusion(includeAs);
/* 1406 */     return setDefaultTyping(typer);
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
/*      */   public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName)
/*      */   {
/* 1419 */     TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
/*      */     
/* 1421 */     typer = typer.init(JsonTypeInfo.Id.CLASS, null);
/* 1422 */     typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
/* 1423 */     typer = typer.typeProperty(propertyName);
/* 1424 */     return setDefaultTyping(typer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disableDefaultTyping()
/*      */   {
/* 1434 */     return setDefaultTyping(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer)
/*      */   {
/* 1445 */     this._deserializationConfig = this._deserializationConfig.with(typer);
/* 1446 */     this._serializationConfig = this._serializationConfig.with(typer);
/* 1447 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerSubtypes(Class<?>... classes)
/*      */   {
/* 1458 */     getSubtypeResolver().registerSubtypes(classes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerSubtypes(NamedType... types)
/*      */   {
/* 1470 */     getSubtypeResolver().registerSubtypes(types);
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
/*      */   public MutableConfigOverride configOverride(Class<?> type)
/*      */   {
/* 1497 */     return this._propertyOverrides.findOrCreateOverride(type);
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
/*      */   public TypeFactory getTypeFactory()
/*      */   {
/* 1510 */     return this._typeFactory;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setTypeFactory(TypeFactory f)
/*      */   {
/* 1522 */     this._typeFactory = f;
/* 1523 */     this._deserializationConfig = this._deserializationConfig.with(f);
/* 1524 */     this._serializationConfig = this._serializationConfig.with(f);
/* 1525 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructType(Type t)
/*      */   {
/* 1534 */     return this._typeFactory.constructType(t);
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
/*      */   public JsonNodeFactory getNodeFactory()
/*      */   {
/* 1554 */     return this._deserializationConfig.getNodeFactory();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setNodeFactory(JsonNodeFactory f)
/*      */   {
/* 1563 */     this._deserializationConfig = this._deserializationConfig.with(f);
/* 1564 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper addHandler(DeserializationProblemHandler h)
/*      */   {
/* 1572 */     this._deserializationConfig = this._deserializationConfig.withHandler(h);
/* 1573 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper clearProblemHandlers()
/*      */   {
/* 1581 */     this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
/* 1582 */     return this;
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
/*      */   public ObjectMapper setConfig(DeserializationConfig config)
/*      */   {
/* 1600 */     this._deserializationConfig = config;
/* 1601 */     return this;
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
/*      */   @Deprecated
/*      */   public void setFilters(FilterProvider filterProvider)
/*      */   {
/* 1615 */     this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
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
/*      */   public ObjectMapper setFilterProvider(FilterProvider filterProvider)
/*      */   {
/* 1630 */     this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
/* 1631 */     return this;
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
/*      */   public ObjectMapper setBase64Variant(Base64Variant v)
/*      */   {
/* 1645 */     this._serializationConfig = this._serializationConfig.with(v);
/* 1646 */     this._deserializationConfig = this._deserializationConfig.with(v);
/* 1647 */     return this;
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
/*      */   public ObjectMapper setConfig(SerializationConfig config)
/*      */   {
/* 1665 */     this._serializationConfig = config;
/* 1666 */     return this;
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
/*      */   public JsonFactory getFactory()
/*      */   {
/* 1684 */     return this._jsonFactory;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public JsonFactory getJsonFactory()
/*      */   {
/* 1691 */     return getFactory();
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
/*      */   public ObjectMapper setDateFormat(DateFormat dateFormat)
/*      */   {
/* 1705 */     this._deserializationConfig = this._deserializationConfig.with(dateFormat);
/* 1706 */     this._serializationConfig = this._serializationConfig.with(dateFormat);
/* 1707 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateFormat getDateFormat()
/*      */   {
/* 1715 */     return this._serializationConfig.getDateFormat();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object setHandlerInstantiator(HandlerInstantiator hi)
/*      */   {
/* 1727 */     this._deserializationConfig = this._deserializationConfig.with(hi);
/* 1728 */     this._serializationConfig = this._serializationConfig.with(hi);
/* 1729 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setInjectableValues(InjectableValues injectableValues)
/*      */   {
/* 1737 */     this._injectableValues = injectableValues;
/* 1738 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public InjectableValues getInjectableValues()
/*      */   {
/* 1745 */     return this._injectableValues;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setLocale(Locale l)
/*      */   {
/* 1753 */     this._deserializationConfig = this._deserializationConfig.with(l);
/* 1754 */     this._serializationConfig = this._serializationConfig.with(l);
/* 1755 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper setTimeZone(TimeZone tz)
/*      */   {
/* 1763 */     this._deserializationConfig = this._deserializationConfig.with(tz);
/* 1764 */     this._serializationConfig = this._serializationConfig.with(tz);
/* 1765 */     return this;
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
/*      */   public boolean isEnabled(MapperFeature f)
/*      */   {
/* 1779 */     return this._serializationConfig.isEnabled(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper configure(MapperFeature f, boolean state)
/*      */   {
/* 1787 */     this._serializationConfig = (state ? this._serializationConfig.with(new MapperFeature[] { f }) : this._serializationConfig.without(new MapperFeature[] { f }));
/*      */     
/* 1789 */     this._deserializationConfig = (state ? this._deserializationConfig.with(new MapperFeature[] { f }) : this._deserializationConfig.without(new MapperFeature[] { f }));
/*      */     
/* 1791 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(MapperFeature... f)
/*      */   {
/* 1799 */     this._deserializationConfig = this._deserializationConfig.with(f);
/* 1800 */     this._serializationConfig = this._serializationConfig.with(f);
/* 1801 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(MapperFeature... f)
/*      */   {
/* 1809 */     this._deserializationConfig = this._deserializationConfig.without(f);
/* 1810 */     this._serializationConfig = this._serializationConfig.without(f);
/* 1811 */     return this;
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
/*      */   public boolean isEnabled(SerializationFeature f)
/*      */   {
/* 1825 */     return this._serializationConfig.isEnabled(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper configure(SerializationFeature f, boolean state)
/*      */   {
/* 1833 */     this._serializationConfig = (state ? this._serializationConfig.with(f) : this._serializationConfig.without(f));
/*      */     
/* 1835 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(SerializationFeature f)
/*      */   {
/* 1843 */     this._serializationConfig = this._serializationConfig.with(f);
/* 1844 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(SerializationFeature first, SerializationFeature... f)
/*      */   {
/* 1853 */     this._serializationConfig = this._serializationConfig.with(first, f);
/* 1854 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(SerializationFeature f)
/*      */   {
/* 1862 */     this._serializationConfig = this._serializationConfig.without(f);
/* 1863 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(SerializationFeature first, SerializationFeature... f)
/*      */   {
/* 1872 */     this._serializationConfig = this._serializationConfig.without(first, f);
/* 1873 */     return this;
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
/*      */   public boolean isEnabled(DeserializationFeature f)
/*      */   {
/* 1887 */     return this._deserializationConfig.isEnabled(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper configure(DeserializationFeature f, boolean state)
/*      */   {
/* 1895 */     this._deserializationConfig = (state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f));
/*      */     
/* 1897 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(DeserializationFeature feature)
/*      */   {
/* 1905 */     this._deserializationConfig = this._deserializationConfig.with(feature);
/* 1906 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f)
/*      */   {
/* 1915 */     this._deserializationConfig = this._deserializationConfig.with(first, f);
/* 1916 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(DeserializationFeature feature)
/*      */   {
/* 1924 */     this._deserializationConfig = this._deserializationConfig.without(feature);
/* 1925 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f)
/*      */   {
/* 1934 */     this._deserializationConfig = this._deserializationConfig.without(first, f);
/* 1935 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEnabled(JsonParser.Feature f)
/*      */   {
/* 1945 */     return this._deserializationConfig.isEnabled(f, this._jsonFactory);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper configure(JsonParser.Feature f, boolean state)
/*      */   {
/* 1956 */     this._jsonFactory.configure(f, state);
/* 1957 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(JsonParser.Feature... features)
/*      */   {
/* 1969 */     for (JsonParser.Feature f : features) {
/* 1970 */       this._jsonFactory.enable(f);
/*      */     }
/* 1972 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(JsonParser.Feature... features)
/*      */   {
/* 1984 */     for (JsonParser.Feature f : features) {
/* 1985 */       this._jsonFactory.disable(f);
/*      */     }
/* 1987 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEnabled(JsonGenerator.Feature f)
/*      */   {
/* 1997 */     return this._serializationConfig.isEnabled(f, this._jsonFactory);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper configure(JsonGenerator.Feature f, boolean state)
/*      */   {
/* 2008 */     this._jsonFactory.configure(f, state);
/* 2009 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper enable(JsonGenerator.Feature... features)
/*      */   {
/* 2021 */     for (JsonGenerator.Feature f : features) {
/* 2022 */       this._jsonFactory.enable(f);
/*      */     }
/* 2024 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectMapper disable(JsonGenerator.Feature... features)
/*      */   {
/* 2036 */     for (JsonGenerator.Feature f : features) {
/* 2037 */       this._jsonFactory.disable(f);
/*      */     }
/* 2039 */     return this;
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
/*      */   public boolean isEnabled(JsonFactory.Feature f)
/*      */   {
/* 2055 */     return this._jsonFactory.isEnabled(f);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T readValue(JsonParser p, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2090 */     return (T)_readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(JsonParser p, TypeReference<?> valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2114 */     return (T)_readValue(getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
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
/*      */   public final <T> T readValue(JsonParser p, ResolvedType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2137 */     return (T)_readValue(getDeserializationConfig(), p, (JavaType)valueType);
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
/*      */   public <T> T readValue(JsonParser p, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2156 */     return (T)_readValue(getDeserializationConfig(), p, valueType);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T extends TreeNode> T readTree(JsonParser p)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2190 */     DeserializationConfig cfg = getDeserializationConfig();
/* 2191 */     JsonToken t = p.getCurrentToken();
/* 2192 */     if (t == null) {
/* 2193 */       t = p.nextToken();
/* 2194 */       if (t == null) {
/* 2195 */         return null;
/*      */       }
/*      */     }
/* 2198 */     JsonNode n = (JsonNode)_readValue(cfg, p, JSON_NODE_TYPE);
/* 2199 */     if (n == null) {
/* 2200 */       n = getNodeFactory().nullNode();
/*      */     }
/*      */     
/* 2203 */     T result = n;
/* 2204 */     return result;
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2229 */     return readValues(p, (JavaType)valueType);
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2243 */     DeserializationConfig config = getDeserializationConfig();
/* 2244 */     DeserializationContext ctxt = createDeserializationContext(p, config);
/* 2245 */     JsonDeserializer<?> deser = _findRootDeserializer(ctxt, valueType);
/*      */     
/* 2247 */     return new MappingIterator(valueType, p, ctxt, deser, false, null);
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
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2263 */     return readValues(p, this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<?> valueTypeRef)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2273 */     return readValues(p, this._typeFactory.constructType(valueTypeRef));
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
/*      */ 
/*      */ 
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
/* 2312 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(in), JSON_NODE_TYPE);
/* 2313 */     return n == null ? NullNode.instance : n;
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
/*      */ 
/*      */   public JsonNode readTree(Reader r)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2342 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(r), JSON_NODE_TYPE);
/* 2343 */     return n == null ? NullNode.instance : n;
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
/*      */ 
/*      */   public JsonNode readTree(String content)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2372 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(content), JSON_NODE_TYPE);
/* 2373 */     return n == null ? NullNode.instance : n;
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
/*      */   public JsonNode readTree(byte[] content)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2395 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(content), JSON_NODE_TYPE);
/* 2396 */     return n == null ? NullNode.instance : n;
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
/*      */   public JsonNode readTree(File file)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2422 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(file), JSON_NODE_TYPE);
/* 2423 */     return n == null ? NullNode.instance : n;
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
/*      */   public JsonNode readTree(URL source)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2449 */     JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(source), JSON_NODE_TYPE);
/* 2450 */     return n == null ? NullNode.instance : n;
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
/*      */   public void writeValue(JsonGenerator g, Object value)
/*      */     throws IOException, JsonGenerationException, JsonMappingException
/*      */   {
/* 2468 */     SerializationConfig config = getSerializationConfig();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2476 */     if ((config.isEnabled(SerializationFeature.INDENT_OUTPUT)) && 
/* 2477 */       (g.getPrettyPrinter() == null)) {
/* 2478 */       g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
/*      */     }
/*      */     
/* 2481 */     if ((config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable))) {
/* 2482 */       _writeCloseableValue(g, value, config);
/*      */     } else {
/* 2484 */       _serializerProvider(config).serializeValue(g, value);
/* 2485 */       if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 2486 */         g.flush();
/*      */       }
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
/*      */   public void writeTree(JsonGenerator jgen, TreeNode rootNode)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2501 */     SerializationConfig config = getSerializationConfig();
/* 2502 */     _serializerProvider(config).serializeValue(jgen, rootNode);
/* 2503 */     if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 2504 */       jgen.flush();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeTree(JsonGenerator jgen, JsonNode rootNode)
/*      */     throws IOException, JsonProcessingException
/*      */   {
/* 2515 */     SerializationConfig config = getSerializationConfig();
/* 2516 */     _serializerProvider(config).serializeValue(jgen, rootNode);
/* 2517 */     if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 2518 */       jgen.flush();
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
/*      */   public ObjectNode createObjectNode()
/*      */   {
/* 2531 */     return this._deserializationConfig.getNodeFactory().objectNode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ArrayNode createArrayNode()
/*      */   {
/* 2543 */     return this._deserializationConfig.getNodeFactory().arrayNode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonParser treeAsTokens(TreeNode n)
/*      */   {
/* 2554 */     return new TreeTraversingParser((JsonNode)n, this);
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
/*      */   public <T> T treeToValue(TreeNode n, Class<T> valueType)
/*      */     throws JsonProcessingException
/*      */   {
/*      */     try
/*      */     {
/* 2574 */       if ((valueType != Object.class) && (valueType.isAssignableFrom(n.getClass()))) {
/* 2575 */         return n;
/*      */       }
/*      */       
/*      */ 
/* 2579 */       if ((n.asToken() == JsonToken.VALUE_EMBEDDED_OBJECT) && 
/* 2580 */         ((n instanceof POJONode))) {
/* 2581 */         Object ob = ((POJONode)n).getPojo();
/* 2582 */         if ((ob == null) || (valueType.isInstance(ob))) {
/* 2583 */           return (T)ob;
/*      */         }
/*      */       }
/*      */       
/* 2587 */       return (T)readValue(treeAsTokens(n), valueType);
/*      */     } catch (JsonProcessingException e) {
/* 2589 */       throw e;
/*      */     } catch (IOException e) {
/* 2591 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T extends JsonNode> T valueToTree(Object fromValue)
/*      */     throws IllegalArgumentException
/*      */   {
/* 2619 */     if (fromValue == null) return null;
/* 2620 */     TokenBuffer buf = new TokenBuffer(this, false);
/* 2621 */     if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 2622 */       buf = buf.forceUseOfBigDecimal(true);
/*      */     }
/*      */     JsonNode result;
/*      */     try {
/* 2626 */       writeValue(buf, fromValue);
/* 2627 */       JsonParser p = buf.asParser();
/* 2628 */       result = (JsonNode)readTree(p);
/* 2629 */       p.close();
/*      */     } catch (IOException e) {
/* 2631 */       throw new IllegalArgumentException(e.getMessage(), e);
/*      */     }
/* 2633 */     return result;
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
/*      */   public boolean canSerialize(Class<?> type)
/*      */   {
/* 2658 */     return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause)
/*      */   {
/* 2669 */     return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, cause);
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
/*      */   public boolean canDeserialize(JavaType type)
/*      */   {
/* 2691 */     return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, null);
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
/*      */   public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause)
/*      */   {
/* 2704 */     return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, cause);
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
/*      */   public <T> T readValue(File src, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2731 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(File src, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2750 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
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
/*      */   public <T> T readValue(File src, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2769 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public <T> T readValue(URL src, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2788 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(URL src, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2807 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(URL src, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2814 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public <T> T readValue(String content, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2833 */     return (T)_readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueType));
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
/*      */   public <T> T readValue(String content, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2852 */     return (T)_readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
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
/*      */   public <T> T readValue(String content, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2871 */     return (T)_readMapAndClose(this._jsonFactory.createParser(content), valueType);
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(Reader src, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2878 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(Reader src, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2885 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(Reader src, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2892 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(InputStream src, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2899 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(InputStream src, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2906 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(InputStream src, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2913 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(byte[] src, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2920 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2928 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(byte[] src, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2935 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readValue(byte[] src, int offset, int len, TypeReference valueTypeRef)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2943 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(byte[] src, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2950 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T readValue(byte[] src, int offset, int len, JavaType valueType)
/*      */     throws IOException, JsonParseException, JsonMappingException
/*      */   {
/* 2958 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
/*      */   }
/*      */   
/*      */   public <T> T readValue(DataInput src, Class<T> valueType)
/*      */     throws IOException
/*      */   {
/* 2964 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T readValue(DataInput src, JavaType valueType)
/*      */     throws IOException
/*      */   {
/* 2971 */     return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
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
/*      */   public void writeValue(File resultFile, Object value)
/*      */     throws IOException, JsonGenerationException, JsonMappingException
/*      */   {
/* 2988 */     _configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
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
/*      */   public void writeValue(OutputStream out, Object value)
/*      */     throws IOException, JsonGenerationException, JsonMappingException
/*      */   {
/* 3005 */     _configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeValue(DataOutput out, Object value)
/*      */     throws IOException
/*      */   {
/* 3014 */     _configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
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
/*      */   public void writeValue(Writer w, Object value)
/*      */     throws IOException, JsonGenerationException, JsonMappingException
/*      */   {
/* 3030 */     _configAndWriteValue(this._jsonFactory.createGenerator(w), value);
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
/*      */   public String writeValueAsString(Object value)
/*      */     throws JsonProcessingException
/*      */   {
/* 3046 */     SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
/*      */     try {
/* 3048 */       _configAndWriteValue(this._jsonFactory.createGenerator(sw), value);
/*      */     } catch (JsonProcessingException e) {
/* 3050 */       throw e;
/*      */     } catch (IOException e) {
/* 3052 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     }
/* 3054 */     return sw.getAndClear();
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
/*      */   public byte[] writeValueAsBytes(Object value)
/*      */     throws JsonProcessingException
/*      */   {
/* 3070 */     ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
/*      */     try {
/* 3072 */       _configAndWriteValue(this._jsonFactory.createGenerator(bb, JsonEncoding.UTF8), value);
/*      */     } catch (JsonProcessingException e) {
/* 3074 */       throw e;
/*      */     } catch (IOException e) {
/* 3076 */       throw JsonMappingException.fromUnexpectedIOE(e);
/*      */     }
/* 3078 */     byte[] result = bb.toByteArray();
/* 3079 */     bb.release();
/* 3080 */     return result;
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
/*      */   public ObjectWriter writer()
/*      */   {
/* 3095 */     return _newWriter(getSerializationConfig());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(SerializationFeature feature)
/*      */   {
/* 3104 */     return _newWriter(getSerializationConfig().with(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(SerializationFeature first, SerializationFeature... other)
/*      */   {
/* 3114 */     return _newWriter(getSerializationConfig().with(first, other));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(DateFormat df)
/*      */   {
/* 3123 */     return _newWriter(getSerializationConfig().with(df));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writerWithView(Class<?> serializationView)
/*      */   {
/* 3131 */     return _newWriter(getSerializationConfig().withView(serializationView));
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
/*      */   public ObjectWriter writerFor(Class<?> rootType)
/*      */   {
/* 3146 */     return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
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
/*      */   public ObjectWriter writerFor(TypeReference<?> rootType)
/*      */   {
/* 3163 */     return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
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
/*      */   public ObjectWriter writerFor(JavaType rootType)
/*      */   {
/* 3180 */     return _newWriter(getSerializationConfig(), rootType, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(PrettyPrinter pp)
/*      */   {
/* 3189 */     if (pp == null) {
/* 3190 */       pp = ObjectWriter.NULL_PRETTY_PRINTER;
/*      */     }
/* 3192 */     return _newWriter(getSerializationConfig(), null, pp);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writerWithDefaultPrettyPrinter()
/*      */   {
/* 3200 */     SerializationConfig config = getSerializationConfig();
/* 3201 */     return _newWriter(config, null, config.getDefaultPrettyPrinter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(FilterProvider filterProvider)
/*      */   {
/* 3210 */     return _newWriter(getSerializationConfig().withFilters(filterProvider));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(FormatSchema schema)
/*      */   {
/* 3221 */     _verifySchemaType(schema);
/* 3222 */     return _newWriter(getSerializationConfig(), schema);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(Base64Variant defaultBase64)
/*      */   {
/* 3232 */     return _newWriter(getSerializationConfig().with(defaultBase64));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(CharacterEscapes escapes)
/*      */   {
/* 3242 */     return _newWriter(getSerializationConfig()).with(escapes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectWriter writer(ContextAttributes attrs)
/*      */   {
/* 3252 */     return _newWriter(getSerializationConfig().with(attrs));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(Class<?> rootType)
/*      */   {
/* 3260 */     return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(TypeReference<?> rootType)
/*      */   {
/* 3271 */     return _newWriter(getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectWriter writerWithType(JavaType rootType)
/*      */   {
/* 3282 */     return _newWriter(getSerializationConfig(), rootType, null);
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
/*      */   public ObjectReader reader()
/*      */   {
/* 3298 */     return _newReader(getDeserializationConfig()).with(this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(DeserializationFeature feature)
/*      */   {
/* 3309 */     return _newReader(getDeserializationConfig().with(feature));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other)
/*      */   {
/* 3321 */     return _newReader(getDeserializationConfig().with(first, other));
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
/*      */   public ObjectReader readerForUpdating(Object valueToUpdate)
/*      */   {
/* 3335 */     JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
/* 3336 */     return _newReader(getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader readerFor(JavaType type)
/*      */   {
/* 3347 */     return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader readerFor(Class<?> type)
/*      */   {
/* 3358 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader readerFor(TypeReference<?> type)
/*      */   {
/* 3369 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(JsonNodeFactory f)
/*      */   {
/* 3378 */     return _newReader(getDeserializationConfig()).with(f);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(FormatSchema schema)
/*      */   {
/* 3389 */     _verifySchemaType(schema);
/* 3390 */     return _newReader(getDeserializationConfig(), null, null, schema, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(InjectableValues injectableValues)
/*      */   {
/* 3401 */     return _newReader(getDeserializationConfig(), null, null, null, injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader readerWithView(Class<?> view)
/*      */   {
/* 3410 */     return _newReader(getDeserializationConfig().withView(view));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(Base64Variant defaultBase64)
/*      */   {
/* 3420 */     return _newReader(getDeserializationConfig().with(defaultBase64));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectReader reader(ContextAttributes attrs)
/*      */   {
/* 3430 */     return _newReader(getDeserializationConfig().with(attrs));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader reader(JavaType type)
/*      */   {
/* 3438 */     return _newReader(getDeserializationConfig(), type, null, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader reader(Class<?> type)
/*      */   {
/* 3447 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public ObjectReader reader(TypeReference<?> type)
/*      */   {
/* 3456 */     return _newReader(getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T convertValue(Object fromValue, Class<T> toValueType)
/*      */     throws IllegalArgumentException
/*      */   {
/* 3498 */     if (fromValue == null) return null;
/* 3499 */     return (T)_convert(fromValue, this._typeFactory.constructType(toValueType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T convertValue(Object fromValue, TypeReference<?> toValueTypeRef)
/*      */     throws IllegalArgumentException
/*      */   {
/* 3509 */     return (T)convertValue(fromValue, this._typeFactory.constructType(toValueTypeRef));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public <T> T convertValue(Object fromValue, JavaType toValueType)
/*      */     throws IllegalArgumentException
/*      */   {
/* 3520 */     if (fromValue == null) return null;
/* 3521 */     return (T)_convert(fromValue, toValueType);
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
/*      */   protected Object _convert(Object fromValue, JavaType toValueType)
/*      */     throws IllegalArgumentException
/*      */   {
/* 3541 */     Class<?> targetType = toValueType.getRawClass();
/* 3542 */     if ((targetType != Object.class) && (!toValueType.hasGenericTypes()) && (targetType.isAssignableFrom(fromValue.getClass())))
/*      */     {
/*      */ 
/* 3545 */       return fromValue;
/*      */     }
/*      */     
/*      */ 
/* 3549 */     TokenBuffer buf = new TokenBuffer(this, false);
/* 3550 */     if (isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 3551 */       buf = buf.forceUseOfBigDecimal(true);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 3556 */       SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
/*      */       
/* 3558 */       _serializerProvider(config).serializeValue(buf, fromValue);
/*      */       
/*      */ 
/* 3561 */       JsonParser p = buf.asParser();
/*      */       
/*      */ 
/* 3564 */       DeserializationConfig deserConfig = getDeserializationConfig();
/* 3565 */       JsonToken t = _initForReading(p);
/* 3566 */       Object result; Object result; if (t == JsonToken.VALUE_NULL) {
/* 3567 */         DeserializationContext ctxt = createDeserializationContext(p, deserConfig);
/* 3568 */         result = _findRootDeserializer(ctxt, toValueType).getNullValue(ctxt); } else { Object result;
/* 3569 */         if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 3570 */           result = null;
/*      */         } else {
/* 3572 */           DeserializationContext ctxt = createDeserializationContext(p, deserConfig);
/* 3573 */           JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, toValueType);
/*      */           
/* 3575 */           result = deser.deserialize(p, ctxt);
/*      */         } }
/* 3577 */       p.close();
/* 3578 */       return result;
/*      */     } catch (IOException e) {
/* 3580 */       throw new IllegalArgumentException(e.getMessage(), e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JsonSchema generateJsonSchema(Class<?> t)
/*      */     throws JsonMappingException
/*      */   {
/* 3603 */     return _serializerProvider(getSerializationConfig()).generateJsonSchema(t);
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
/*      */   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor)
/*      */     throws JsonMappingException
/*      */   {
/* 3620 */     acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
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
/*      */   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor)
/*      */     throws JsonMappingException
/*      */   {
/* 3638 */     if (type == null) {
/* 3639 */       throw new IllegalArgumentException("type must be provided");
/*      */     }
/* 3641 */     _serializerProvider(getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
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
/*      */   protected DefaultSerializerProvider _serializerProvider(SerializationConfig config)
/*      */   {
/* 3655 */     return this._serializerProvider.createInstance(config, this._serializerFactory);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void _configAndWriteValue(JsonGenerator g, Object value)
/*      */     throws IOException
/*      */   {
/* 3665 */     SerializationConfig cfg = getSerializationConfig();
/* 3666 */     cfg.initialize(g);
/* 3667 */     if ((cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)) && ((value instanceof Closeable))) {
/* 3668 */       _configAndWriteCloseable(g, value, cfg);
/* 3669 */       return;
/*      */     }
/*      */     try {
/* 3672 */       _serializerProvider(cfg).serializeValue(g, value);
/*      */     } catch (Exception e) {
/* 3674 */       ClassUtil.closeOnFailAndThrowAsIAE(g, e);
/* 3675 */       return;
/*      */     }
/* 3677 */     g.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _configAndWriteCloseable(JsonGenerator g, Object value, SerializationConfig cfg)
/*      */     throws IOException
/*      */   {
/* 3687 */     Closeable toClose = (Closeable)value;
/*      */     try {
/* 3689 */       _serializerProvider(cfg).serializeValue(g, value);
/* 3690 */       Closeable tmpToClose = toClose;
/* 3691 */       toClose = null;
/* 3692 */       tmpToClose.close();
/*      */     } catch (Exception e) {
/* 3694 */       ClassUtil.closeOnFailAndThrowAsIAE(g, toClose, e);
/* 3695 */       return;
/*      */     }
/* 3697 */     g.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg)
/*      */     throws IOException
/*      */   {
/* 3707 */     Closeable toClose = (Closeable)value;
/*      */     try {
/* 3709 */       _serializerProvider(cfg).serializeValue(g, value);
/* 3710 */       if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/* 3711 */         g.flush();
/*      */       }
/*      */     } catch (Exception e) {
/* 3714 */       ClassUtil.closeOnFailAndThrowAsIAE(null, toClose, e);
/* 3715 */       return;
/*      */     }
/* 3717 */     toClose.close();
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
/*      */   protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg)
/*      */   {
/* 3733 */     return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
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
/*      */   protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType)
/*      */     throws IOException
/*      */   {
/* 3747 */     JsonToken t = _initForReading(p);
/* 3748 */     Object result; Object result; if (t == JsonToken.VALUE_NULL)
/*      */     {
/* 3750 */       DeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 3751 */       result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt); } else { Object result;
/* 3752 */       if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 3753 */         result = null;
/*      */       } else {
/* 3755 */         DeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 3756 */         JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
/*      */         Object result;
/* 3758 */         if (cfg.useRootWrapping()) {
/* 3759 */           result = _unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
/*      */         } else {
/* 3761 */           result = deser.deserialize(p, ctxt);
/*      */         }
/*      */       }
/*      */     }
/* 3765 */     p.clearCurrentToken();
/* 3766 */     return result;
/*      */   }
/*      */   
/*      */   protected Object _readMapAndClose(JsonParser p0, JavaType valueType)
/*      */     throws IOException
/*      */   {
/* 3772 */     JsonParser p = p0;Throwable localThrowable2 = null;
/*      */     try {
/* 3774 */       JsonToken t = _initForReading(p);
/* 3775 */       Object result; DeserializationConfig cfg; Object result; if (t == JsonToken.VALUE_NULL)
/*      */       {
/* 3777 */         DeserializationContext ctxt = createDeserializationContext(p, getDeserializationConfig());
/*      */         
/* 3779 */         result = _findRootDeserializer(ctxt, valueType).getNullValue(ctxt); } else { Object result;
/* 3780 */         if ((t == JsonToken.END_ARRAY) || (t == JsonToken.END_OBJECT)) {
/* 3781 */           result = null;
/*      */         } else {
/* 3783 */           cfg = getDeserializationConfig();
/* 3784 */           DeserializationContext ctxt = createDeserializationContext(p, cfg);
/* 3785 */           JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
/* 3786 */           Object result; if (cfg.useRootWrapping()) {
/* 3787 */             result = _unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
/*      */           } else {
/* 3789 */             result = deser.deserialize(p, ctxt);
/*      */           }
/* 3791 */           ctxt.checkUnresolvedObjectId();
/*      */         }
/*      */       }
/* 3794 */       p.clearCurrentToken();
/* 3795 */       return (DeserializationConfig)result;
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 3772 */       localThrowable2 = localThrowable1;throw localThrowable1;
/*      */ 
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
/* 3796 */       if (p != null) { if (localThrowable2 != null) try { p.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else { p.close();
/*      */         }
/*      */       }
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
/*      */   protected JsonToken _initForReading(JsonParser p)
/*      */     throws IOException
/*      */   {
/* 3816 */     this._deserializationConfig.initialize(p);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3822 */     JsonToken t = p.getCurrentToken();
/* 3823 */     if (t == null)
/*      */     {
/* 3825 */       t = p.nextToken();
/* 3826 */       if (t == null)
/*      */       {
/*      */ 
/* 3829 */         throw JsonMappingException.from(p, "No content to map due to end-of-input");
/*      */       }
/*      */     }
/* 3832 */     return t;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Object _unwrapAndDeserialize(JsonParser p, DeserializationContext ctxt, DeserializationConfig config, JavaType rootType, JsonDeserializer<Object> deser)
/*      */     throws IOException
/*      */   {
/* 3840 */     PropertyName expRootName = config.findRootName(rootType);
/*      */     
/* 3842 */     String expSimpleName = expRootName.getSimpleName();
/* 3843 */     if (p.getCurrentToken() != JsonToken.START_OBJECT) {
/* 3844 */       ctxt.reportWrongTokenException(p, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3849 */     if (p.nextToken() != JsonToken.FIELD_NAME) {
/* 3850 */       ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '" + expSimpleName + "'), but " + p.getCurrentToken(), new Object[0]);
/*      */     }
/*      */     
/*      */ 
/* 3854 */     String actualName = p.getCurrentName();
/* 3855 */     if (!expSimpleName.equals(actualName)) {
/* 3856 */       ctxt.reportMappingException("Root name '%s' does not match expected ('%s') for type %s", new Object[] { actualName, expSimpleName, rootType });
/*      */     }
/*      */     
/*      */ 
/* 3860 */     p.nextToken();
/* 3861 */     Object result = deser.deserialize(p, ctxt);
/*      */     
/* 3863 */     if (p.nextToken() != JsonToken.END_OBJECT) {
/* 3864 */       ctxt.reportWrongTokenException(p, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", new Object[] { expSimpleName, p.getCurrentToken() });
/*      */     }
/*      */     
/*      */ 
/* 3868 */     return result;
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
/*      */   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType)
/*      */     throws JsonMappingException
/*      */   {
/* 3885 */     JsonDeserializer<Object> deser = (JsonDeserializer)this._rootDeserializers.get(valueType);
/* 3886 */     if (deser != null) {
/* 3887 */       return deser;
/*      */     }
/*      */     
/* 3890 */     deser = ctxt.findRootValueDeserializer(valueType);
/* 3891 */     if (deser == null) {
/* 3892 */       throw JsonMappingException.from(ctxt, "Can not find a deserializer for type " + valueType);
/*      */     }
/*      */     
/* 3895 */     this._rootDeserializers.put(valueType, deser);
/* 3896 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _verifySchemaType(FormatSchema schema)
/*      */   {
/* 3904 */     if ((schema != null) && 
/* 3905 */       (!this._jsonFactory.canUseSchema(schema))) {
/* 3906 */       throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName());
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ObjectMapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */