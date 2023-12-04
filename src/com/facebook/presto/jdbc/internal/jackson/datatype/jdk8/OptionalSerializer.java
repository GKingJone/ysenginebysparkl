/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
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
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.util.Optional;
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
/*     */ public class OptionalSerializer
/*     */   extends StdSerializer<Optional<?>>
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
/*     */   @Deprecated
/*     */   public OptionalSerializer(JavaType type)
/*     */   {
/*  67 */     this((ReferenceType)type, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected OptionalSerializer(ReferenceType optionalType, TypeSerializer vts, JsonSerializer<?> valueSer)
/*     */   {
/*  74 */     super(optionalType);
/*  75 */     this._referredType = optionalType.getReferencedType();
/*  76 */     this._property = null;
/*  77 */     this._valueTypeSerializer = vts;
/*  78 */     this._valueSerializer = valueSer;
/*  79 */     this._unwrapper = null;
/*  80 */     this._contentInclusion = null;
/*  81 */     this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected OptionalSerializer(OptionalSerializer base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
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
/*     */   public JsonSerializer<Optional<?>> unwrappingSerializer(NameTransformer transformer)
/*     */   {
/* 106 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 107 */     if (ser != null) {
/* 108 */       ser = ser.unwrappingSerializer(transformer);
/*     */     }
/*     */     
/* 111 */     NameTransformer unwrapper = this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
/* 112 */     return withResolved(this._property, this._valueTypeSerializer, ser, unwrapper, this._contentInclusion);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected OptionalSerializer withResolved(BeanProperty prop, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, JsonInclude.Include contentIncl)
/*     */   {
/* 119 */     if ((this._property == prop) && (contentIncl == this._contentInclusion) && (this._valueTypeSerializer == vts) && (this._valueSerializer == valueSer) && (this._unwrapper == unwrapper))
/*     */     {
/*     */ 
/* 122 */       return this;
/*     */     }
/* 124 */     return new OptionalSerializer(this, prop, vts, valueSer, unwrapper, contentIncl);
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
/* 137 */     TypeSerializer vts = this._valueTypeSerializer;
/* 138 */     if (vts != null) {
/* 139 */       vts = vts.forProperty(property);
/*     */     }
/* 141 */     JsonSerializer<?> ser = findContentSerializer(provider, property);
/* 142 */     if (ser == null) {
/* 143 */       ser = this._valueSerializer;
/* 144 */       if (ser == null)
/*     */       {
/* 146 */         if (_useStatic(provider, property, this._referredType)) {
/* 147 */           ser = _findSerializer(provider, this._referredType, property);
/*     */         }
/*     */       } else {
/* 150 */         ser = provider.handlePrimaryContextualization(ser, property);
/*     */       }
/*     */     }
/*     */     
/* 154 */     JsonInclude.Include contentIncl = this._contentInclusion;
/* 155 */     if (property != null) {
/* 156 */       JsonInclude.Value incl = property.findPropertyInclusion(provider.getConfig(), Optional.class);
/*     */       
/* 158 */       JsonInclude.Include newIncl = incl.getContentInclusion();
/* 159 */       if ((newIncl != contentIncl) && (newIncl != JsonInclude.Include.USE_DEFAULTS)) {
/* 160 */         contentIncl = newIncl;
/*     */       }
/*     */     }
/* 163 */     return withResolved(property, vts, ser, this._unwrapper, contentIncl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType)
/*     */   {
/* 170 */     if (referredType.isJavaLangObject()) {
/* 171 */       return false;
/*     */     }
/*     */     
/* 174 */     if (referredType.isFinal()) {
/* 175 */       return true;
/*     */     }
/*     */     
/* 178 */     if (referredType.useStaticType()) {
/* 179 */       return true;
/*     */     }
/*     */     
/* 182 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 183 */     if ((intr != null) && (property != null)) {
/* 184 */       Annotated ann = property.getMember();
/* 185 */       if (ann != null) {
/* 186 */         JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
/* 187 */         if (t == JsonSerialize.Typing.STATIC) {
/* 188 */           return true;
/*     */         }
/* 190 */         if (t == JsonSerialize.Typing.DYNAMIC) {
/* 191 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 196 */     return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
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
/* 208 */     if ((value == null) || (!value.isPresent())) {
/* 209 */       return true;
/*     */     }
/* 211 */     if (this._contentInclusion == null) {
/* 212 */       return false;
/*     */     }
/* 214 */     Object contents = value.get();
/* 215 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 216 */     if (ser == null) {
/*     */       try {
/* 218 */         ser = _findCachedSerializer(provider, contents.getClass());
/*     */       } catch (JsonMappingException e) {
/* 220 */         throw new RuntimeJsonMappingException(e);
/*     */       }
/*     */     }
/* 223 */     return ser.isEmpty(provider, contents);
/*     */   }
/*     */   
/*     */   public boolean isUnwrappingSerializer()
/*     */   {
/* 228 */     return this._unwrapper != null;
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
/* 241 */     if (!opt.isPresent())
/*     */     {
/* 243 */       if (this._unwrapper == null) {
/* 244 */         provider.defaultSerializeNull(gen);
/*     */       }
/* 246 */       return;
/*     */     }
/* 248 */     Object value = opt.get();
/* 249 */     JsonSerializer<Object> ser = this._valueSerializer;
/* 250 */     if (ser == null) {
/* 251 */       ser = _findCachedSerializer(provider, value.getClass());
/*     */     }
/* 253 */     if (this._valueTypeSerializer != null) {
/* 254 */       ser.serializeWithType(value, gen, provider, this._valueTypeSerializer);
/*     */     } else {
/* 256 */       ser.serialize(value, gen, provider);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Optional<?> opt, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 265 */     if (!opt.isPresent())
/*     */     {
/* 267 */       if (this._unwrapper == null) {
/* 268 */         provider.defaultSerializeNull(gen);
/*     */       }
/* 270 */       return;
/*     */     }
/*     */     
/* 273 */     typeSer.writeTypePrefixForScalar(opt, gen, Optional.class);
/* 274 */     serialize(opt, gen, provider);
/* 275 */     typeSer.writeTypeSuffixForScalar(opt, gen);
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
/* 287 */     JsonSerializer<?> ser = this._valueSerializer;
/* 288 */     if (ser == null) {
/* 289 */       ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
/* 290 */       if (this._unwrapper != null) {
/* 291 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/*     */     }
/* 294 */     ser.acceptJsonFormatVisitor(visitor, this._referredType);
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
/* 310 */     JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type);
/* 311 */     if (ser == null) {
/* 312 */       ser = _findSerializer(provider, type, this._property);
/* 313 */       if (this._unwrapper != null) {
/* 314 */         ser = ser.unwrappingSerializer(this._unwrapper);
/*     */       }
/* 316 */       this._dynamicSerializers = this._dynamicSerializers.newWith(type, ser);
/*     */     }
/* 318 */     return ser;
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Class<?> type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 324 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */   
/*     */   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop)
/*     */     throws JsonMappingException
/*     */   {
/* 330 */     return provider.findTypedValueSerializer(type, true, prop);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonSerializer<?> findContentSerializer(SerializerProvider serializers, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 339 */     if (property != null)
/*     */     {
/* 341 */       AnnotatedMember m = property.getMember();
/* 342 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/* 343 */       if (m != null) {
/* 344 */         Object serDef = intr.findContentSerializer(m);
/* 345 */         if (serDef != null) {
/* 346 */           return serializers.serializerInstance(m, serDef);
/*     */         }
/*     */       }
/*     */     }
/* 350 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */