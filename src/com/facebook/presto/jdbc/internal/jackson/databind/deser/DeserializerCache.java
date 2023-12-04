/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer.None;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDelegatingDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public final class DeserializerCache
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  42 */   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers = new ConcurrentHashMap(64, 0.75F, 4);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  50 */   protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap(8);
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
/*     */   Object writeReplace()
/*     */   {
/*  69 */     this._incompleteDeserializers.clear();
/*     */     
/*  71 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int cachedDeserializersCount()
/*     */   {
/*  93 */     return this._cachedDeserializers.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flushCachedDeserializers()
/*     */   {
/* 104 */     this._cachedDeserializers.clear();
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
/*     */   public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType)
/*     */     throws JsonMappingException
/*     */   {
/* 139 */     JsonDeserializer<Object> deser = _findCachedDeserializer(propertyType);
/* 140 */     if (deser == null)
/*     */     {
/* 142 */       deser = _createAndCacheValueDeserializer(ctxt, factory, propertyType);
/* 143 */       if (deser == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 148 */         deser = _handleUnknownValueDeserializer(ctxt, propertyType);
/*     */       }
/*     */     }
/* 151 */     return deser;
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
/*     */   public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 166 */     KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
/* 167 */     if (kd == null) {
/* 168 */       return _handleUnknownKeyDeserializer(ctxt, type);
/*     */     }
/*     */     
/* 171 */     if ((kd instanceof ResolvableDeserializer)) {
/* 172 */       ((ResolvableDeserializer)kd).resolve(ctxt);
/*     */     }
/* 174 */     return kd;
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
/*     */   public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 189 */     JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 190 */     if (deser == null) {
/* 191 */       deser = _createAndCacheValueDeserializer(ctxt, factory, type);
/*     */     }
/* 193 */     return deser != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type)
/*     */   {
/* 204 */     if (type == null) {
/* 205 */       throw new IllegalArgumentException("Null JavaType passed");
/*     */     }
/* 207 */     if (_hasCustomValueHandler(type)) {
/* 208 */       return null;
/*     */     }
/* 210 */     return (JsonDeserializer)this._cachedDeserializers.get(type);
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
/*     */   protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 228 */     synchronized (this._incompleteDeserializers)
/*     */     {
/* 230 */       JsonDeserializer<Object> deser = _findCachedDeserializer(type);
/* 231 */       if (deser != null) {
/* 232 */         return deser;
/*     */       }
/* 234 */       int count = this._incompleteDeserializers.size();
/*     */       
/* 236 */       if (count > 0) {
/* 237 */         deser = (JsonDeserializer)this._incompleteDeserializers.get(type);
/* 238 */         if (deser != null) {
/* 239 */           return deser;
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 244 */         JsonDeserializer localJsonDeserializer = _createAndCache2(ctxt, factory, type);
/*     */         
/*     */ 
/* 247 */         if ((count == 0) && (this._incompleteDeserializers.size() > 0))
/* 248 */           this._incompleteDeserializers.clear(); return localJsonDeserializer;
/*     */       }
/*     */       finally
/*     */       {
/* 247 */         if ((count == 0) && (this._incompleteDeserializers.size() > 0)) {
/* 248 */           this._incompleteDeserializers.clear();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/*     */     JsonDeserializer<Object> deser;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 264 */       deser = _createDeserializer(ctxt, factory, type);
/*     */ 
/*     */     }
/*     */     catch (IllegalArgumentException iae)
/*     */     {
/* 269 */       throw JsonMappingException.from(ctxt, iae.getMessage(), iae);
/*     */     }
/* 271 */     if (deser == null) {
/* 272 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 278 */     boolean isResolvable = deser instanceof ResolvableDeserializer;
/*     */     
/* 280 */     boolean addToCache = (!_hasCustomValueHandler(type)) && (deser.isCachable());
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
/* 294 */     if (isResolvable) {
/* 295 */       this._incompleteDeserializers.put(type, deser);
/* 296 */       ((ResolvableDeserializer)deser).resolve(ctxt);
/* 297 */       this._incompleteDeserializers.remove(type);
/*     */     }
/* 299 */     if (addToCache) {
/* 300 */       this._cachedDeserializers.put(type, deser);
/*     */     }
/* 302 */     return deser;
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
/*     */   protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 321 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/*     */ 
/* 324 */     if ((type.isAbstract()) || (type.isMapLikeType()) || (type.isCollectionLikeType())) {
/* 325 */       type = factory.mapAbstractType(config, type);
/*     */     }
/* 327 */     BeanDescription beanDesc = config.introspect(type);
/*     */     
/* 329 */     JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/*     */     
/* 331 */     if (deser != null) {
/* 332 */       return deser;
/*     */     }
/*     */     
/*     */ 
/* 336 */     JavaType newType = modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
/* 337 */     if (newType != type) {
/* 338 */       type = newType;
/* 339 */       beanDesc = config.introspect(newType);
/*     */     }
/*     */     
/*     */ 
/* 343 */     Class<?> builder = beanDesc.findPOJOBuilder();
/* 344 */     if (builder != null) {
/* 345 */       return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 350 */     Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
/* 351 */     if (conv == null) {
/* 352 */       return _createDeserializer2(ctxt, factory, type, beanDesc);
/*     */     }
/*     */     
/* 355 */     JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/*     */     
/* 357 */     if (!delegateType.hasRawClass(type.getRawClass())) {
/* 358 */       beanDesc = config.introspect(delegateType);
/*     */     }
/* 360 */     return new StdDelegatingDeserializer(conv, delegateType, _createDeserializer2(ctxt, factory, delegateType, beanDesc));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 368 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/* 370 */     if (type.isEnumType()) {
/* 371 */       return factory.createEnumDeserializer(ctxt, type, beanDesc);
/*     */     }
/* 373 */     if (type.isContainerType()) {
/* 374 */       if (type.isArrayType()) {
/* 375 */         return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
/*     */       }
/* 377 */       if (type.isMapLikeType()) {
/* 378 */         MapLikeType mlt = (MapLikeType)type;
/* 379 */         if (mlt.isTrueMapType()) {
/* 380 */           return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
/*     */         }
/* 382 */         return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
/*     */       }
/* 384 */       if (type.isCollectionLikeType())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 390 */         JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 391 */         if ((format == null) || (format.getShape() != JsonFormat.Shape.OBJECT)) {
/* 392 */           CollectionLikeType clt = (CollectionLikeType)type;
/* 393 */           if (clt.isTrueCollectionType()) {
/* 394 */             return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
/*     */           }
/* 396 */           return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
/*     */         }
/*     */       }
/*     */     }
/* 400 */     if (type.isReferenceType()) {
/* 401 */       return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc);
/*     */     }
/* 403 */     if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
/* 404 */       return factory.createTreeDeserializer(config, type, beanDesc);
/*     */     }
/* 406 */     return factory.createBeanDeserializer(ctxt, type, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
/*     */     throws JsonMappingException
/*     */   {
/* 418 */     Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
/* 419 */     if (deserDef == null) {
/* 420 */       return null;
/*     */     }
/* 422 */     JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
/*     */     
/* 424 */     return findConvertingDeserializer(ctxt, ann, deser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser)
/*     */     throws JsonMappingException
/*     */   {
/* 437 */     Converter<Object, Object> conv = findConverter(ctxt, a);
/* 438 */     if (conv == null) {
/* 439 */       return deser;
/*     */     }
/* 441 */     JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 442 */     return new StdDelegatingDeserializer(conv, delegateType, deser);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a)
/*     */     throws JsonMappingException
/*     */   {
/* 449 */     Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
/* 450 */     if (convDef == null) {
/* 451 */       return null;
/*     */     }
/* 453 */     return ctxt.converterInstance(a, convDef);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 475 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 476 */     if (intr == null) {
/* 477 */       return type;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 483 */     if (type.isMapLikeType()) {
/* 484 */       JavaType keyType = type.getKeyType();
/*     */       
/*     */ 
/*     */ 
/* 488 */       if ((keyType != null) && (keyType.getValueHandler() == null)) {
/* 489 */         Object kdDef = intr.findKeyDeserializer(a);
/* 490 */         if (kdDef != null) {
/* 491 */           KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
/* 492 */           if (kd != null) {
/* 493 */             type = ((MapLikeType)type).withKeyValueHandler(kd);
/* 494 */             keyType = type.getKeyType();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 499 */     JavaType contentType = type.getContentType();
/* 500 */     if ((contentType != null) && 
/* 501 */       (contentType.getValueHandler() == null)) {
/* 502 */       Object cdDef = intr.findContentDeserializer(a);
/* 503 */       if (cdDef != null) {
/* 504 */         JsonDeserializer<?> cd = null;
/* 505 */         if ((cdDef instanceof JsonDeserializer)) {
/* 506 */           cdDef = (JsonDeserializer)cdDef;
/*     */         } else {
/* 508 */           Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
/* 509 */           if (cdClass != null) {
/* 510 */             cd = ctxt.deserializerInstance(a, cdClass);
/*     */           }
/*     */         }
/* 513 */         if (cd != null) {
/* 514 */           type = type.withContentValueHandler(cd);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 522 */     type = intr.refineDeserializationType(ctxt.getConfig(), a, type);
/*     */     
/* 524 */     return type;
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
/*     */   private boolean _hasCustomValueHandler(JavaType t)
/*     */   {
/* 540 */     if (t.isContainerType()) {
/* 541 */       JavaType ct = t.getContentType();
/* 542 */       if (ct != null) {
/* 543 */         return (ct.getValueHandler() != null) || (ct.getTypeHandler() != null);
/*     */       }
/*     */     }
/* 546 */     return false;
/*     */   }
/*     */   
/*     */   private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass)
/*     */   {
/* 551 */     if (src == null) {
/* 552 */       return null;
/*     */     }
/* 554 */     if (!(src instanceof Class)) {
/* 555 */       throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
/*     */     }
/* 557 */     Class<?> cls = (Class)src;
/* 558 */     if ((cls == noneClass) || (ClassUtil.isBogusClass(cls))) {
/* 559 */       return null;
/*     */     }
/* 561 */     return cls;
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
/*     */   protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 577 */     Class<?> rawClass = type.getRawClass();
/* 578 */     if (!ClassUtil.isConcrete(rawClass)) {
/* 579 */       ctxt.reportMappingException("Can not find a Value deserializer for abstract type %s", new Object[] { type });
/*     */     }
/* 581 */     ctxt.reportMappingException("Can not find a Value deserializer for type %s", new Object[] { type });
/* 582 */     return null;
/*     */   }
/*     */   
/*     */   protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 588 */     ctxt.reportMappingException("Can not find a (Map) Key deserializer for type %s", new Object[] { type });
/* 589 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\DeserializerCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */