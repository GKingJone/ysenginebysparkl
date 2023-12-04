/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.BeanPropertyMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdValueProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ValueInjector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ public class BeanDeserializerBuilder
/*     */ {
/*     */   protected final BeanDescription _beanDesc;
/*     */   protected final boolean _defaultViewInclusion;
/*     */   protected final boolean _caseInsensitivePropertyComparison;
/*  53 */   protected final Map<String, SettableBeanProperty> _properties = new LinkedHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List<ValueInjector> _injectables;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected HashMap<String, SettableBeanProperty> _backRefProperties;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected HashSet<String> _ignorableProps;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ValueInstantiator _valueInstantiator;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ObjectIdReader _objectIdReader;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableAnyProperty _anySetter;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _ignoreAllUnknown;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AnnotatedMethod _buildMethod;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonPOJOBuilder.Value _builderConfig;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BeanDeserializerBuilder(BeanDescription beanDesc, DeserializationConfig config)
/*     */   {
/* 116 */     this._beanDesc = beanDesc;
/* 117 */     this._defaultViewInclusion = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
/* 118 */     this._caseInsensitivePropertyComparison = config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanDeserializerBuilder(BeanDeserializerBuilder src)
/*     */   {
/* 127 */     this._beanDesc = src._beanDesc;
/* 128 */     this._defaultViewInclusion = src._defaultViewInclusion;
/* 129 */     this._caseInsensitivePropertyComparison = src._caseInsensitivePropertyComparison;
/*     */     
/*     */ 
/* 132 */     this._properties.putAll(src._properties);
/* 133 */     this._injectables = _copy(src._injectables);
/* 134 */     this._backRefProperties = _copy(src._backRefProperties);
/*     */     
/* 136 */     this._ignorableProps = src._ignorableProps;
/* 137 */     this._valueInstantiator = src._valueInstantiator;
/* 138 */     this._objectIdReader = src._objectIdReader;
/*     */     
/* 140 */     this._anySetter = src._anySetter;
/* 141 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*     */     
/* 143 */     this._buildMethod = src._buildMethod;
/* 144 */     this._builderConfig = src._builderConfig;
/*     */   }
/*     */   
/*     */   private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
/* 148 */     return src == null ? null : new HashMap(src);
/*     */   }
/*     */   
/*     */   private static <T> List<T> _copy(List<T> src)
/*     */   {
/* 153 */     return src == null ? null : new ArrayList(src);
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
/*     */   public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride)
/*     */   {
/* 166 */     this._properties.put(prop.getName(), prop);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addProperty(SettableBeanProperty prop)
/*     */   {
/* 176 */     SettableBeanProperty old = (SettableBeanProperty)this._properties.put(prop.getName(), prop);
/* 177 */     if ((old != null) && (old != prop)) {
/* 178 */       throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addBackReferenceProperty(String referenceName, SettableBeanProperty prop)
/*     */   {
/* 189 */     if (this._backRefProperties == null) {
/* 190 */       this._backRefProperties = new HashMap(4);
/*     */     }
/* 192 */     this._backRefProperties.put(referenceName, prop);
/*     */     
/* 194 */     if (this._properties != null) {
/* 195 */       this._properties.remove(prop.getName());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addInjectable(PropertyName propName, JavaType propType, Annotations contextAnnotations, AnnotatedMember member, Object valueId)
/*     */   {
/* 205 */     if (this._injectables == null) {
/* 206 */       this._injectables = new ArrayList();
/*     */     }
/* 208 */     this._injectables.add(new ValueInjector(propName, propType, contextAnnotations, member, valueId));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addIgnorable(String propName)
/*     */   {
/* 218 */     if (this._ignorableProps == null) {
/* 219 */       this._ignorableProps = new HashSet();
/*     */     }
/* 221 */     this._ignorableProps.add(propName);
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
/*     */   public void addCreatorProperty(SettableBeanProperty prop)
/*     */   {
/* 236 */     addProperty(prop);
/*     */   }
/*     */   
/*     */   public void setAnySetter(SettableAnyProperty s)
/*     */   {
/* 241 */     if ((this._anySetter != null) && (s != null)) {
/* 242 */       throw new IllegalStateException("_anySetter already set to non-null");
/*     */     }
/* 244 */     this._anySetter = s;
/*     */   }
/*     */   
/*     */   public void setIgnoreUnknownProperties(boolean ignore) {
/* 248 */     this._ignoreAllUnknown = ignore;
/*     */   }
/*     */   
/*     */   public void setValueInstantiator(ValueInstantiator inst) {
/* 252 */     this._valueInstantiator = inst;
/*     */   }
/*     */   
/*     */   public void setObjectIdReader(ObjectIdReader r) {
/* 256 */     this._objectIdReader = r;
/*     */   }
/*     */   
/*     */   public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
/* 260 */     this._buildMethod = buildMethod;
/* 261 */     this._builderConfig = config;
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
/*     */   public Iterator<SettableBeanProperty> getProperties()
/*     */   {
/* 279 */     return this._properties.values().iterator();
/*     */   }
/*     */   
/*     */   public SettableBeanProperty findProperty(PropertyName propertyName) {
/* 283 */     return (SettableBeanProperty)this._properties.get(propertyName.getSimpleName());
/*     */   }
/*     */   
/*     */   public boolean hasProperty(PropertyName propertyName) {
/* 287 */     return findProperty(propertyName) != null;
/*     */   }
/*     */   
/*     */   public SettableBeanProperty removeProperty(PropertyName name) {
/* 291 */     return (SettableBeanProperty)this._properties.remove(name.getSimpleName());
/*     */   }
/*     */   
/*     */   public SettableAnyProperty getAnySetter() {
/* 295 */     return this._anySetter;
/*     */   }
/*     */   
/*     */   public ValueInstantiator getValueInstantiator() {
/* 299 */     return this._valueInstantiator;
/*     */   }
/*     */   
/*     */   public List<ValueInjector> getInjectables() {
/* 303 */     return this._injectables;
/*     */   }
/*     */   
/*     */   public ObjectIdReader getObjectIdReader() {
/* 307 */     return this._objectIdReader;
/*     */   }
/*     */   
/*     */   public AnnotatedMethod getBuildMethod() {
/* 311 */     return this._buildMethod;
/*     */   }
/*     */   
/*     */   public JsonPOJOBuilder.Value getBuilderConfig() {
/* 315 */     return this._builderConfig;
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
/*     */   public JsonDeserializer<?> build()
/*     */   {
/* 330 */     Collection<SettableBeanProperty> props = this._properties.values();
/* 331 */     BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._caseInsensitivePropertyComparison);
/* 332 */     propertyMap.assignIndexes();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 337 */     boolean anyViews = !this._defaultViewInclusion;
/*     */     
/* 339 */     if (!anyViews) {
/* 340 */       for (SettableBeanProperty prop : props) {
/* 341 */         if (prop.hasViews()) {
/* 342 */           anyViews = true;
/* 343 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 349 */     if (this._objectIdReader != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 354 */       ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
/* 355 */       propertyMap = propertyMap.withProperty(prop);
/*     */     }
/*     */     
/* 358 */     return new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
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
/*     */   public AbstractDeserializer buildAbstract()
/*     */   {
/* 371 */     return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName)
/*     */   {
/* 382 */     if (this._buildMethod == null)
/*     */     {
/* 384 */       if (!expBuildMethodName.isEmpty()) {
/* 385 */         throw new IllegalArgumentException("Builder class " + this._beanDesc.getBeanClass().getName() + " does not have build method (name: '" + expBuildMethodName + "')");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 390 */       Class<?> rawBuildType = this._buildMethod.getRawReturnType();
/* 391 */       Class<?> rawValueType = valueType.getRawClass();
/* 392 */       if ((rawBuildType != rawValueType) && (!rawBuildType.isAssignableFrom(rawValueType)) && (!rawValueType.isAssignableFrom(rawBuildType)))
/*     */       {
/*     */ 
/* 395 */         throw new IllegalArgumentException("Build method '" + this._buildMethod.getFullName() + " has bad return type (" + rawBuildType.getName() + "), not compatible with POJO type (" + valueType.getRawClass().getName() + ")");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 401 */     Collection<SettableBeanProperty> props = this._properties.values();
/* 402 */     BeanPropertyMap propertyMap = BeanPropertyMap.construct(props, this._caseInsensitivePropertyComparison);
/* 403 */     propertyMap.assignIndexes();
/*     */     
/* 405 */     boolean anyViews = !this._defaultViewInclusion;
/*     */     
/* 407 */     if (!anyViews) {
/* 408 */       for (SettableBeanProperty prop : props) {
/* 409 */         if (prop.hasViews()) {
/* 410 */           anyViews = true;
/* 411 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 416 */     if (this._objectIdReader != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 421 */       ObjectIdValueProperty prop = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
/*     */       
/* 423 */       propertyMap = propertyMap.withProperty(prop);
/*     */     }
/*     */     
/* 426 */     return new BuilderBasedDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BeanDeserializerBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */