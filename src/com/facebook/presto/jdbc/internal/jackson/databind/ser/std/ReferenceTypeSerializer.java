/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.RuntimeJsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonSerialize.Typing;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
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
/*     */ public abstract class ReferenceTypeSerializer<T>
/*     */   extends StdSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _referredType;
/*     */   protected final BeanProperty _property;
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */   protected final JsonSerializer<Object> _valueSerializer;
/*     */   protected final NameTransformer _unwrapper;
/*     */   protected final JsonInclude.Include _contentInclusion;
/*     */   protected transient PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */   public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser)
/*     */   {
/*  73 */     super(fullType);
/*  74 */     this._referredType = fullType.getReferencedType();
/*  75 */     this._property = null;
/*  76 */     this._valueTypeSerializer = vts;
/*  77 */     this._valueSerializer = ser;
/*  78 */     this._unwrapper = null;
/*  79 */     this._contentInclusion = null;
/*  80 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*     */   {
/*  89 */     super(base);
/*  90 */     this._referredType = base._referredType;
/*  91 */     this._dynamicSerializers = base._dynamicSerializers;
/*  92 */     this._property = property;
/*  93 */     this._valueTypeSerializer = vts;
/*  94 */     this._valueSerializer = valueSer;
/*  95 */     this._unwrapper = unwrapper;
/*  96 */     if ((contentIncl == JsonInclude.Include.USE_DEFAULTS) || (contentIncl == JsonInclude.Include.ALWAYS))
/*     */     {
/*  98 */       this._contentInclusion = null;
/*     */     } else {
/* 100 */       this._contentInclusion = contentIncl;
/*     */     }
/*     */   }
/*     */   
/*     */   public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer)
/*     */   {
/* 106 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 107 */     if (ser != null) {
/* 108 */       ser = ser.unwrappingSerializer(transformer);
/*     */     }
/* 110 */     NameTransformer unwrapper = this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
/*     */     
/* 112 */     return withResolved(this._property, this._valueTypeSerializer, ser, unwrapper, this._contentInclusion);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty paramBeanProperty, TypeSerializer paramTypeSerializer, JsonSerializer<?> paramJsonSerializer, NameTransformer paramNameTransformer, JsonInclude.Include paramInclude);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract boolean _isValueEmpty(T paramT);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Object _getReferenced(T paramT);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Object _getReferencedIfPresent(T paramT);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 142 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 143 */     if (typeSer != null) {
/* 144 */       typeSer = typeSer.forProperty(property);
/*     */     }
/*     */     
/* 147 */     JsonSerializer<?> ser = findAnnotatedContentSerializer(provider, property);
/* 148 */     if (ser == null)
/*     */     {
/* 150 */       ser = this._valueSerializer;
/* 151 */       if (ser == null)
/*     */       {
/* 153 */         if (_useStatic(provider, property, this._referredType)) {
/* 154 */           ser = _findSerializer(provider, this._referredType, property);
/*     */         }
/*     */       } else {
/* 157 */         ser = provider.handlePrimaryContextualization(ser, property);
/*     */       }
/*     */     }
/*     */     
/* 161 */     JsonInclude.Include contentIncl = this._contentInclusion;
/* 162 */     JsonInclude.Value incl = findIncludeOverrides(provider, property, handledType());
/* 163 */     JsonInclude.Include newIncl = incl.getContentInclusion();
/* 164 */     if ((newIncl != contentIncl) && (newIncl != JsonInclude.Include.USE_DEFAULTS)) {
/* 165 */       contentIncl = newIncl;
/*     */     }
/* 167 */     return withResolved(property, typeSer, ser, this._unwrapper, contentIncl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType)
/*     */   {
/* 174 */     if (referredType.isJavaLangObject()) {
/* 175 */       return false;
/*     */     }
/*     */     
/* 178 */     if (referredType.isFinal()) {
/* 179 */       return true;
/*     */     }
/*     */     
/* 182 */     if (referredType.useStaticType()) {
/* 183 */       return true;
/*     */     }
/*     */     
/* 186 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 187 */     if ((intr != null) && (property != null)) {
/* 188 */       Annotated ann = property.getMember();
/* 189 */       if (ann != null) {
/* 190 */         JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
/* 191 */         if (t == JsonSerialize.Typing.STATIC) {
/* 192 */           return true;
/*     */         }
/* 194 */         if (t == JsonSerialize.Typing.DYNAMIC) {
/* 195 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 200 */     return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty(SerializerProvider provider, T value)
/*     */   {
/* 212 */     if ((value == null) || (_isValueEmpty(value))) {
/* 213 */       return true;
/*     */     }
/* 215 */     if (this._contentInclusion == null) {
/* 216 */       return false;
/*     */     }
/* 218 */     Object contents = _getReferenced(value);
/* 219 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 220 */     if (ser == null) {
/*     */       try {
/* 222 */         ser = _findCachedSerializer(provider, contents.getClass());
/*     */       } catch (JsonMappingException e) {
/* 224 */         throw new RuntimeJsonMappingException(e);
/*     */       }
/*     */     }
/* 227 */     return ser.isEmpty(provider, contents);
/*     */   }
/*     */   
/*     */   public boolean isUnwrappingSerializer()
/*     */   {
/* 232 */     return this._unwrapper != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialize(T ref, JsonGenerator g, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 245 */     Object value = _getReferencedIfPresent(ref);
/* 246 */     if (value == null) {
/* 247 */       if (this._unwrapper == null) {
/* 248 */         provider.defaultSerializeNull(g);
/*     */       }
/* 250 */       return;
/*     */     }
/* 252 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 253 */     if (ser == null) {
/* 254 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 256 */     if (this._valueTypeSerializer != null) {
/* 257 */       ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
/*     */     } else {
/* 259 */       ser.serialize(value, g, provider);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 268 */     Object value = _getReferencedIfPresent(ref);
/* 269 */     if (value == null) {
/* 270 */       if (this._unwrapper == null) {
/* 271 */         provider.defaultSerializeNull(g);
/*     */       }
/* 273 */       return;
/*     */     }
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
/* 286 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 287 */     if (ser == null) {
/* 288 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 290 */     ser.serializeWithType(value, g, provider, typeSer);
/*     */   }
/*     */   
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
/* 303 */     JsonSerializer<?> ser = this._valueSerializer;
/* 304 */     if (ser == null) {
/* 305 */       ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
/* 306 */       if (this._unwrapper != null) {
/* 307 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/*     */     }
/* 310 */     ser.acceptJsonFormatVisitor(visitor, this._referredType);
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
/*     */   private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> type)
/*     */     throws JsonMappingException
/*     */   {
/* 326 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type);
/* 327 */     if (ser == null) {
/* 328 */       ser = _findSerializer(provider, type, this._property);
/* 329 */       if (this._unwrapper != null) {
/* 330 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/* 332 */       this._dynamicSerializers = this._dynamicSerializers.newWith(type, ser);
/*     */     }
/* 334 */     return ser;
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Class<?> type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 340 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 346 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\ReferenceTypeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */