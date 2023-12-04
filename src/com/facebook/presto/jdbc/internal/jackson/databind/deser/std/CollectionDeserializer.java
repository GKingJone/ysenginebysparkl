/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.UnresolvedForwardReference;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId.Referring;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ @JacksonStdImpl
/*     */ public class CollectionDeserializer
/*     */   extends ContainerDeserializerBase<Collection<Object>>
/*     */   implements ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = -1L;
/*     */   protected final JavaType _collectionType;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final JsonDeserializer<Object> _delegateDeserializer;
/*     */   protected final Boolean _unwrapSingle;
/*     */   
/*     */   public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator)
/*     */   {
/*  83 */     this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, Boolean unwrapSingle)
/*     */   {
/*  95 */     super(collectionType);
/*  96 */     this._collectionType = collectionType;
/*  97 */     this._valueDeserializer = valueDeser;
/*  98 */     this._valueTypeDeserializer = valueTypeDeser;
/*  99 */     this._valueInstantiator = valueInstantiator;
/* 100 */     this._delegateDeserializer = delegateDeser;
/* 101 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CollectionDeserializer(CollectionDeserializer src)
/*     */   {
/* 110 */     super(src._collectionType);
/* 111 */     this._collectionType = src._collectionType;
/* 112 */     this._valueDeserializer = src._valueDeserializer;
/* 113 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 114 */     this._valueInstantiator = src._valueInstantiator;
/* 115 */     this._delegateDeserializer = src._delegateDeserializer;
/* 116 */     this._unwrapSingle = src._unwrapSingle;
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
/*     */   protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, Boolean unwrapSingle)
/*     */   {
/* 129 */     if ((dd == this._delegateDeserializer) && (vd == this._valueDeserializer) && (vtd == this._valueTypeDeserializer) && (this._unwrapSingle == unwrapSingle))
/*     */     {
/* 131 */       return this;
/*     */     }
/* 133 */     return new CollectionDeserializer(this._collectionType, vd, vtd, this._valueInstantiator, dd, unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd)
/*     */   {
/* 145 */     return withResolved(dd, vd, vtd, this._unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isCachable()
/*     */   {
/* 152 */     return (this._valueDeserializer == null) && (this._valueTypeDeserializer == null) && (this._delegateDeserializer == null);
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
/*     */   public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 174 */     JsonDeserializer<Object> delegateDeser = null;
/* 175 */     if ((this._valueInstantiator != null) && (this._valueInstantiator.canCreateUsingDelegate())) {
/* 176 */       JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 177 */       if (delegateType == null) {
/* 178 */         throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._collectionType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
/*     */       }
/*     */       
/*     */ 
/* 182 */       delegateDeser = findDeserializer(ctxt, delegateType, property);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 187 */     Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
/*     */     
/*     */ 
/* 190 */     JsonDeserializer<?> valueDeser = this._valueDeserializer;
/*     */     
/*     */ 
/* 193 */     valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
/* 194 */     JavaType vt = this._collectionType.getContentType();
/* 195 */     if (valueDeser == null) {
/* 196 */       valueDeser = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 198 */       valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
/*     */     }
/*     */     
/* 201 */     TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
/* 202 */     if (valueTypeDeser != null) {
/* 203 */       valueTypeDeser = valueTypeDeser.forProperty(property);
/*     */     }
/* 205 */     return withResolved(delegateDeser, valueDeser, valueTypeDeser, unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getContentType()
/*     */   {
/* 216 */     return this._collectionType.getContentType();
/*     */   }
/*     */   
/*     */   public JsonDeserializer<Object> getContentDeserializer()
/*     */   {
/* 221 */     return this._valueDeserializer;
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
/*     */   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 235 */     if (this._delegateDeserializer != null) {
/* 236 */       return (Collection)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 243 */     if (p.hasToken(JsonToken.VALUE_STRING)) {
/* 244 */       String str = p.getText();
/* 245 */       if (str.length() == 0) {
/* 246 */         return (Collection)this._valueInstantiator.createFromString(ctxt, str);
/*     */       }
/*     */     }
/* 249 */     return deserialize(p, ctxt, (Collection)this._valueInstantiator.createUsingDefault(ctxt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result)
/*     */     throws IOException
/*     */   {
/* 258 */     if (!p.isExpectedStartArrayToken()) {
/* 259 */       return handleNonArray(p, ctxt, result);
/*     */     }
/*     */     
/* 262 */     p.setCurrentValue(result);
/*     */     
/* 264 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 265 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/* 266 */     CollectionReferringAccumulator referringAccumulator = valueDes.getObjectIdReader() == null ? null : new CollectionReferringAccumulator(this._collectionType.getContentType().getRawClass(), result);
/*     */     
/*     */ 
/*     */     JsonToken t;
/*     */     
/* 271 */     while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
/*     */       try { Object value;
/*     */         Object value;
/* 274 */         if (t == JsonToken.VALUE_NULL) {
/* 275 */           value = valueDes.getNullValue(ctxt); } else { Object value;
/* 276 */           if (typeDeser == null) {
/* 277 */             value = valueDes.deserialize(p, ctxt);
/*     */           } else
/* 279 */             value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         }
/* 281 */         if (referringAccumulator != null) {
/* 282 */           referringAccumulator.add(value);
/*     */         } else {
/* 284 */           result.add(value);
/*     */         }
/*     */       } catch (UnresolvedForwardReference reference) {
/* 287 */         if (referringAccumulator == null) {
/* 288 */           throw JsonMappingException.from(p, "Unresolved forward reference but no identity info", reference);
/*     */         }
/*     */         
/* 291 */         ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
/* 292 */         reference.getRoid().appendReferring(ref);
/*     */       } catch (Exception e) {
/* 294 */         boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/* 295 */         if ((!wrap) && ((e instanceof RuntimeException))) {
/* 296 */           throw ((RuntimeException)e);
/*     */         }
/* 298 */         throw JsonMappingException.wrapWithPath(e, result, result.size());
/*     */       }
/*     */     }
/* 301 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 310 */     return typeDeserializer.deserializeTypedFromArray(p, ctxt);
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
/*     */   protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result)
/*     */     throws IOException
/*     */   {
/* 324 */     boolean canWrap = (this._unwrapSingle == Boolean.TRUE) || ((this._unwrapSingle == null) && (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)));
/*     */     
/*     */ 
/* 327 */     if (!canWrap) {
/* 328 */       return (Collection)ctxt.handleUnexpectedToken(this._collectionType.getRawClass(), p);
/*     */     }
/* 330 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 331 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/* 332 */     JsonToken t = p.getCurrentToken();
/*     */     Object value;
/*     */     try
/*     */     {
/*     */       Object value;
/* 337 */       if (t == JsonToken.VALUE_NULL) {
/* 338 */         value = valueDes.getNullValue(ctxt); } else { Object value;
/* 339 */         if (typeDeser == null) {
/* 340 */           value = valueDes.deserialize(p, ctxt);
/*     */         } else {
/* 342 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 346 */       throw JsonMappingException.wrapWithPath(e, Object.class, result.size());
/*     */     }
/* 348 */     result.add(value);
/* 349 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class CollectionReferringAccumulator
/*     */   {
/*     */     private final Class<?> _elementType;
/*     */     
/*     */     private final Collection<Object> _result;
/*     */     
/* 359 */     private List<CollectionReferring> _accumulator = new ArrayList();
/*     */     
/*     */     public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
/* 362 */       this._elementType = elementType;
/* 363 */       this._result = result;
/*     */     }
/*     */     
/*     */     public void add(Object value)
/*     */     {
/* 368 */       if (this._accumulator.isEmpty()) {
/* 369 */         this._result.add(value);
/*     */       } else {
/* 371 */         CollectionReferring ref = (CollectionReferring)this._accumulator.get(this._accumulator.size() - 1);
/* 372 */         ref.next.add(value);
/*     */       }
/*     */     }
/*     */     
/*     */     public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference)
/*     */     {
/* 378 */       CollectionReferring id = new CollectionReferring(this, reference, this._elementType);
/* 379 */       this._accumulator.add(id);
/* 380 */       return id;
/*     */     }
/*     */     
/*     */     public void resolveForwardReference(Object id, Object value) throws IOException
/*     */     {
/* 385 */       Iterator<CollectionReferring> iterator = this._accumulator.iterator();
/*     */       
/*     */ 
/*     */ 
/* 389 */       Collection<Object> previous = this._result;
/* 390 */       while (iterator.hasNext()) {
/* 391 */         CollectionReferring ref = (CollectionReferring)iterator.next();
/* 392 */         if (ref.hasId(id)) {
/* 393 */           iterator.remove();
/* 394 */           previous.add(value);
/* 395 */           previous.addAll(ref.next);
/* 396 */           return;
/*     */         }
/* 398 */         previous = ref.next;
/*     */       }
/*     */       
/* 401 */       throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final class CollectionReferring
/*     */     extends ReadableObjectId.Referring
/*     */   {
/*     */     private final CollectionReferringAccumulator _parent;
/*     */     
/*     */ 
/* 413 */     public final List<Object> next = new ArrayList();
/*     */     
/*     */ 
/*     */     CollectionReferring(CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType)
/*     */     {
/* 418 */       super(contentType);
/* 419 */       this._parent = parent;
/*     */     }
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value) throws IOException
/*     */     {
/* 424 */       this._parent.resolveForwardReference(id, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\CollectionDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */