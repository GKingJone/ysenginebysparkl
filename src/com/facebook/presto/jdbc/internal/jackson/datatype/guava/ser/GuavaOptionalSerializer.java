/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
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
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
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
/*     */ public final class GuavaOptionalSerializer
/*     */   extends StdSerializer<Optional<?>>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final JavaType _referredType;
/*     */   protected final BeanProperty _property;
/*     */   protected final TypeSerializer _valueTypeSerializer;
/*     */   protected final JsonSerializer<Object> _valueSerializer;
/*     */   protected final JsonInclude.Include _contentInclusion;
/*     */   protected final NameTransformer _unwrapper;
/*     */   protected transient PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */   public GuavaOptionalSerializer(ReferenceType fullType, TypeSerializer vts, JsonSerializer<?> valueSer)
/*     */   {
/*  66 */     super(fullType);
/*  67 */     this._referredType = fullType.getReferencedType();
/*  68 */     this._property = null;
/*  69 */     this._valueTypeSerializer = vts;
/*  70 */     this._valueSerializer = valueSer;
/*  71 */     this._unwrapper = null;
/*  72 */     this._contentInclusion = null;
/*  73 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GuavaOptionalSerializer(GuavaOptionalSerializer base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*     */   {
/*  82 */     super(base);
/*  83 */     this._referredType = base._referredType;
/*  84 */     this._dynamicSerializers = base._dynamicSerializers;
/*  85 */     this._property = property;
/*  86 */     this._valueTypeSerializer = vts;
/*  87 */     this._valueSerializer = valueSer;
/*  88 */     this._unwrapper = unwrapper;
/*  89 */     if ((contentIncl == JsonInclude.Include.USE_DEFAULTS) || (contentIncl == JsonInclude.Include.ALWAYS))
/*     */     {
/*  91 */       this._contentInclusion = null;
/*     */     } else {
/*  93 */       this._contentInclusion = contentIncl;
/*     */     }
/*     */   }
/*     */   
/*     */   public JsonSerializer<Optional<?>> unwrappingSerializer(NameTransformer transformer)
/*     */   {
/*  99 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 100 */     if (ser != null) {
/* 101 */       ser = ser.unwrappingSerializer(transformer);
/*     */     }
/* 103 */     NameTransformer unwrapper = this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
/*     */     
/* 105 */     return withResolved(this._property, this._valueTypeSerializer, ser, unwrapper, this._contentInclusion);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected GuavaOptionalSerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*     */   {
/* 112 */     if ((this._property == prop) && (contentIncl == this._contentInclusion) && (this._valueTypeSerializer == vts) && (this._valueSerializer == valueSer) && (this._unwrapper == unwrapper))
/*     */     {
/*     */ 
/* 115 */       return this;
/*     */     }
/* 117 */     return new GuavaOptionalSerializer(this, prop, vts, valueSer, unwrapper, contentIncl);
/*     */   }
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
/* 130 */     TypeSerializer vts = this._valueTypeSerializer;
/* 131 */     if (vts != null) {
/* 132 */       vts = vts.forProperty(property);
/*     */     }
/* 134 */     JsonSerializer<?> ser = findContentSerializer(provider, property);
/* 135 */     if (ser == null) {
/* 136 */       ser = this._valueSerializer;
/* 137 */       if (ser == null)
/*     */       {
/* 139 */         if (_useStatic(provider, property, this._referredType)) {
/* 140 */           ser = _findSerializer(provider, this._referredType, property);
/*     */         }
/*     */       } else {
/* 143 */         ser = provider.handlePrimaryContextualization(ser, property);
/*     */       }
/*     */     }
/*     */     
/* 147 */     JsonInclude.Include contentIncl = this._contentInclusion;
/* 148 */     if (property != null) {
/* 149 */       JsonInclude.Value incl = property.findPropertyInclusion(provider.getConfig(), Optional.class);
/*     */       
/* 151 */       JsonInclude.Include newIncl = incl.getContentInclusion();
/* 152 */       if ((newIncl != contentIncl) && (newIncl != JsonInclude.Include.USE_DEFAULTS)) {
/* 153 */         contentIncl = newIncl;
/*     */       }
/*     */     }
/* 156 */     return withResolved(property, vts, ser, this._unwrapper, contentIncl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType)
/*     */   {
/* 163 */     if (referredType.isJavaLangObject()) {
/* 164 */       return false;
/*     */     }
/*     */     
/* 167 */     if (referredType.isFinal()) {
/* 168 */       return true;
/*     */     }
/*     */     
/* 171 */     if (referredType.useStaticType()) {
/* 172 */       return true;
/*     */     }
/*     */     
/* 175 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 176 */     if ((intr != null) && (property != null)) {
/* 177 */       Annotated ann = property.getMember();
/* 178 */       if (ann != null) {
/* 179 */         JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
/* 180 */         if (t == JsonSerialize.Typing.STATIC) {
/* 181 */           return true;
/*     */         }
/* 183 */         if (t == JsonSerialize.Typing.DYNAMIC) {
/* 184 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 189 */     return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty(SerializerProvider provider, Optional<?> value)
/*     */   {
/* 201 */     if ((value == null) || (!value.isPresent())) {
/* 202 */       return true;
/*     */     }
/* 204 */     if (this._contentInclusion == null) {
/* 205 */       return false;
/*     */     }
/* 207 */     Object contents = value.get();
/* 208 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 209 */     if (ser == null) {
/*     */       try {
/* 211 */         ser = _findCachedSerializer(provider, contents.getClass());
/*     */       } catch (JsonMappingException e) {
/* 213 */         throw new RuntimeJsonMappingException(e);
/*     */       }
/*     */     }
/* 216 */     return ser.isEmpty(provider, contents);
/*     */   }
/*     */   
/*     */   public boolean isUnwrappingSerializer()
/*     */   {
/* 221 */     return this._unwrapper != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialize(Optional<?> opt, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 234 */     if (!opt.isPresent())
/*     */     {
/* 236 */       if (this._unwrapper == null) {
/* 237 */         provider.defaultSerializeNull(gen);
/*     */       }
/* 239 */       return;
/*     */     }
/* 241 */     Object value = opt.get();
/* 242 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 243 */     if (ser == null) {
/* 244 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 246 */     if (this._valueTypeSerializer != null) {
/* 247 */       ser.serializeWithType(value, gen, provider, this._valueTypeSerializer);
/*     */     } else {
/* 249 */       ser.serialize(value, gen, provider);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Optional<?> opt, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 258 */     if (!opt.isPresent()) {
/* 259 */       if (this._unwrapper == null) {
/* 260 */         provider.defaultSerializeNull(gen);
/*     */       }
/* 262 */       return;
/*     */     }
/*     */     
/* 265 */     typeSer.writeTypePrefixForScalar(opt, gen, Optional.class);
/* 266 */     serialize(opt, gen, provider);
/* 267 */     typeSer.writeTypeSuffixForScalar(opt, gen);
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
/* 279 */     JsonSerializer<?> ser = this._valueSerializer;
/* 280 */     if (ser == null)
/*     */     {
/*     */ 
/* 283 */       ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
/* 284 */       if (this._unwrapper != null) {
/* 285 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/*     */     }
/* 288 */     ser.acceptJsonFormatVisitor(visitor, this._referredType);
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
/*     */   protected final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> type)
/*     */     throws JsonMappingException
/*     */   {
/* 304 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type);
/* 305 */     if (ser == null) {
/* 306 */       ser = _findSerializer(provider, type, this._property);
/* 307 */       if (this._unwrapper != null) {
/* 308 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/* 310 */       this._dynamicSerializers = this._dynamicSerializers.newWith(type, ser);
/*     */     }
/* 312 */     return ser;
/*     */   }
/*     */   
/*     */ 
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Class<?> type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 319 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */   
/*     */ 
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 326 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JsonSerializer<?> findContentSerializer(SerializerProvider serializers, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 334 */     if (property != null) {
/* 335 */       AnnotatedMember m = property.getMember();
/* 336 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/* 337 */       if (m != null) {
/* 338 */         Object serDef = intr.findContentSerializer(m);
/* 339 */         if (serDef != null) {
/* 340 */           return serializers.serializerInstance(m, serDef);
/*     */         }
/*     */       }
/*     */     }
/* 344 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\ser\GuavaOptionalSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */