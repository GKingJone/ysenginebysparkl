/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonSerialize.Typing;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ 
/*     */ public class PropertyBuilder
/*     */ {
/*  18 */   private static final Object NO_DEFAULT_MARKER = Boolean.FALSE;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final SerializationConfig _config;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final BeanDescription _beanDesc;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final JsonInclude.Value _defaultInclusion;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final AnnotationIntrospector _annotationIntrospector;
/*     */   
/*     */ 
/*     */ 
/*     */   protected Object _defaultBean;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc)
/*     */   {
/*  45 */     this._config = config;
/*  46 */     this._beanDesc = beanDesc;
/*  47 */     this._defaultInclusion = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion(beanDesc.getBeanClass()));
/*     */     
/*  49 */     this._annotationIntrospector = this._config.getAnnotationIntrospector();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations getClassAnnotations()
/*     */   {
/*  59 */     return this._beanDesc.getClassAnnotations();
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
/*     */   protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping)
/*     */     throws com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException
/*     */   {
/*  75 */     JavaType serializationType = findSerializationType(am, defaultUseStaticTyping, declaredType);
/*     */     
/*     */ 
/*  78 */     if (contentTypeSer != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  83 */       if (serializationType == null)
/*     */       {
/*  85 */         serializationType = declaredType;
/*     */       }
/*  87 */       JavaType ct = serializationType.getContentType();
/*     */       
/*  89 */       if (ct == null) {
/*  90 */         throw new IllegalStateException("Problem trying to create BeanPropertyWriter for property '" + propDef.getName() + "' (of type " + this._beanDesc.getType() + "); serialization type " + serializationType + " has no content");
/*     */       }
/*     */       
/*  93 */       serializationType = serializationType.withContentTypeHandler(contentTypeSer);
/*  94 */       ct = serializationType.getContentType();
/*     */     }
/*     */     
/*  97 */     Object valueToSuppress = null;
/*  98 */     boolean suppressNulls = false;
/*     */     
/* 100 */     JsonInclude.Value inclV = this._defaultInclusion.withOverrides(propDef.findInclusion());
/* 101 */     JsonInclude.Include inclusion = inclV.getValueInclusion();
/* 102 */     if (inclusion == JsonInclude.Include.USE_DEFAULTS) {
/* 103 */       inclusion = JsonInclude.Include.ALWAYS;
/*     */     }
/*     */     
/*     */ 
/* 107 */     JavaType actualType = serializationType == null ? declaredType : serializationType;
/*     */     
/* 109 */     switch (inclusion)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case NON_DEFAULT: 
/* 116 */       if (this._defaultInclusion.getValueInclusion() == JsonInclude.Include.NON_DEFAULT) {
/* 117 */         valueToSuppress = getPropertyDefaultValue(propDef.getName(), am, actualType);
/*     */       } else {
/* 119 */         valueToSuppress = getDefaultValue(actualType);
/*     */       }
/* 121 */       if (valueToSuppress == null) {
/* 122 */         suppressNulls = true;
/*     */       }
/* 124 */       else if (valueToSuppress.getClass().isArray()) {
/* 125 */         valueToSuppress = com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders.getArrayComparator(valueToSuppress);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       break;
/*     */     case NON_ABSENT: 
/* 132 */       suppressNulls = true;
/*     */       
/* 134 */       if (actualType.isReferenceType()) {
/* 135 */         valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/*     */       }
/*     */       
/*     */       break;
/*     */     case NON_EMPTY: 
/* 140 */       suppressNulls = true;
/*     */       
/* 142 */       valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
/* 143 */       break;
/*     */     case NON_NULL: 
/* 145 */       suppressNulls = true;
/*     */     
/*     */ 
/*     */     case ALWAYS: 
/*     */     default: 
/* 150 */       if ((actualType.isContainerType()) && (!this._config.isEnabled(com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)))
/*     */       {
/* 152 */         valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY; }
/*     */       break;
/*     */     }
/*     */     
/* 156 */     BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 161 */     Object serDef = this._annotationIntrospector.findNullSerializer(am);
/* 162 */     if (serDef != null) {
/* 163 */       bpw.assignNullSerializer(prov.serializerInstance(am, serDef));
/*     */     }
/*     */     
/* 166 */     com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
/* 167 */     if (unwrapper != null) {
/* 168 */       bpw = bpw.unwrappingWriter(unwrapper);
/*     */     }
/* 170 */     return bpw;
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
/*     */   protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType)
/*     */     throws com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException
/*     */   {
/* 188 */     JavaType secondary = this._annotationIntrospector.refineSerializationType(this._config, a, declaredType);
/*     */     
/*     */ 
/* 191 */     if (secondary != declaredType) {
/* 192 */       Class<?> serClass = secondary.getRawClass();
/*     */       
/* 194 */       Class<?> rawDeclared = declaredType.getRawClass();
/* 195 */       if (!serClass.isAssignableFrom(rawDeclared))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 204 */         if (!rawDeclared.isAssignableFrom(serClass)) {
/* 205 */           throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 212 */       useStaticTyping = true;
/* 213 */       declaredType = secondary;
/*     */     }
/*     */     
/* 216 */     JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
/* 217 */     if ((typing != null) && (typing != JsonSerialize.Typing.DEFAULT_TYPING)) {
/* 218 */       useStaticTyping = typing == JsonSerialize.Typing.STATIC;
/*     */     }
/* 220 */     if (useStaticTyping)
/*     */     {
/* 222 */       return declaredType.withStaticTyping();
/*     */     }
/*     */     
/* 225 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object getDefaultBean()
/*     */   {
/* 236 */     Object def = this._defaultBean;
/* 237 */     if (def == null)
/*     */     {
/*     */ 
/*     */ 
/* 241 */       def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
/* 242 */       if (def == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 250 */         def = NO_DEFAULT_MARKER;
/*     */       }
/* 252 */       this._defaultBean = def;
/*     */     }
/* 254 */     return def == NO_DEFAULT_MARKER ? null : this._defaultBean;
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
/*     */   protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type)
/*     */   {
/* 272 */     Object defaultBean = getDefaultBean();
/* 273 */     if (defaultBean == null) {
/* 274 */       return getDefaultValue(type);
/*     */     }
/*     */     try {
/* 277 */       return member.getValue(defaultBean);
/*     */     } catch (Exception e) {
/* 279 */       return _throwWrapped(e, name, defaultBean);
/*     */     }
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
/*     */   protected Object getDefaultValue(JavaType type)
/*     */   {
/* 300 */     Class<?> cls = type.getRawClass();
/*     */     
/* 302 */     Class<?> prim = com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.primitiveType(cls);
/* 303 */     if (prim != null) {
/* 304 */       return com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.defaultValue(prim);
/*     */     }
/* 306 */     if ((type.isContainerType()) || (type.isReferenceType())) {
/* 307 */       return JsonInclude.Include.NON_EMPTY;
/*     */     }
/* 309 */     if (cls == String.class) {
/* 310 */       return "";
/*     */     }
/* 312 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object _throwWrapped(Exception e, String propName, Object defaultBean)
/*     */   {
/* 323 */     Throwable t = e;
/* 324 */     while (t.getCause() != null) {
/* 325 */       t = t.getCause();
/*     */     }
/* 327 */     if ((t instanceof Error)) throw ((Error)t);
/* 328 */     if ((t instanceof RuntimeException)) throw ((RuntimeException)t);
/* 329 */     throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\PropertyBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */