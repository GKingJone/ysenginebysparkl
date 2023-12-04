/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.FailingDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ConcreteBeanPropertyBase;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ViewMatcher;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SettableBeanProperty
/*     */   extends ConcreteBeanPropertyBase
/*     */   implements Serializable
/*     */ {
/*  36 */   protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final PropertyName _propName;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JavaType _type;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final PropertyName _wrapperName;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final transient Annotations _contextAnnotations;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String _managedReferenceName;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ObjectIdInfo _objectIdInfo;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ViewMatcher _viewMatcher;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */   protected int _propertyIndex = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations)
/*     */   {
/* 128 */     this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef.getMetadata());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected SettableBeanProperty(String propName, JavaType type, PropertyName wrapper, TypeDeserializer typeDeser, Annotations contextAnnotations, boolean isRequired)
/*     */   {
/* 137 */     this(new PropertyName(propName), type, wrapper, typeDeser, contextAnnotations, PropertyMetadata.construct(isRequired, null, null, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyName wrapper, TypeDeserializer typeDeser, Annotations contextAnnotations, PropertyMetadata metadata)
/*     */   {
/* 145 */     super(metadata);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 151 */     if (propName == null) {
/* 152 */       this._propName = PropertyName.NO_NAME;
/*     */     } else {
/* 154 */       this._propName = propName.internSimpleName();
/*     */     }
/* 156 */     this._type = type;
/* 157 */     this._wrapperName = wrapper;
/* 158 */     this._contextAnnotations = contextAnnotations;
/* 159 */     this._viewMatcher = null;
/*     */     
/*     */ 
/* 162 */     if (typeDeser != null) {
/* 163 */       typeDeser = typeDeser.forProperty(this);
/*     */     }
/* 165 */     this._valueTypeDeserializer = typeDeser;
/* 166 */     this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyMetadata metadata, JsonDeserializer<Object> valueDeser)
/*     */   {
/* 177 */     super(metadata);
/*     */     
/* 179 */     if (propName == null) {
/* 180 */       this._propName = PropertyName.NO_NAME;
/*     */     } else {
/* 182 */       this._propName = propName.internSimpleName();
/*     */     }
/* 184 */     this._type = type;
/* 185 */     this._wrapperName = null;
/* 186 */     this._contextAnnotations = null;
/* 187 */     this._viewMatcher = null;
/* 188 */     this._valueTypeDeserializer = null;
/* 189 */     this._valueDeserializer = valueDeser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(SettableBeanProperty src)
/*     */   {
/* 197 */     super(src);
/* 198 */     this._propName = src._propName;
/* 199 */     this._type = src._type;
/* 200 */     this._wrapperName = src._wrapperName;
/* 201 */     this._contextAnnotations = src._contextAnnotations;
/* 202 */     this._valueDeserializer = src._valueDeserializer;
/* 203 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 204 */     this._managedReferenceName = src._managedReferenceName;
/* 205 */     this._propertyIndex = src._propertyIndex;
/* 206 */     this._viewMatcher = src._viewMatcher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(SettableBeanProperty src, JsonDeserializer<?> deser)
/*     */   {
/* 215 */     super(src);
/* 216 */     this._propName = src._propName;
/* 217 */     this._type = src._type;
/* 218 */     this._wrapperName = src._wrapperName;
/* 219 */     this._contextAnnotations = src._contextAnnotations;
/* 220 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 221 */     this._managedReferenceName = src._managedReferenceName;
/* 222 */     this._propertyIndex = src._propertyIndex;
/*     */     
/* 224 */     if (deser == null) {
/* 225 */       this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
/*     */     } else {
/* 227 */       this._valueDeserializer = deser;
/*     */     }
/* 229 */     this._viewMatcher = src._viewMatcher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty(SettableBeanProperty src, PropertyName newName)
/*     */   {
/* 237 */     super(src);
/* 238 */     this._propName = newName;
/* 239 */     this._type = src._type;
/* 240 */     this._wrapperName = src._wrapperName;
/* 241 */     this._contextAnnotations = src._contextAnnotations;
/* 242 */     this._valueDeserializer = src._valueDeserializer;
/* 243 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 244 */     this._managedReferenceName = src._managedReferenceName;
/* 245 */     this._propertyIndex = src._propertyIndex;
/* 246 */     this._viewMatcher = src._viewMatcher;
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
/*     */   public abstract SettableBeanProperty withValueDeserializer(JsonDeserializer<?> paramJsonDeserializer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract SettableBeanProperty withName(PropertyName paramPropertyName);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SettableBeanProperty withSimpleName(String simpleName)
/*     */   {
/* 277 */     PropertyName n = this._propName == null ? new PropertyName(simpleName) : this._propName.withSimpleName(simpleName);
/*     */     
/* 279 */     return n == this._propName ? this : withName(n);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public SettableBeanProperty withName(String simpleName) {
/* 284 */     return withName(new PropertyName(simpleName));
/*     */   }
/*     */   
/*     */   public void setManagedReferenceName(String n) {
/* 288 */     this._managedReferenceName = n;
/*     */   }
/*     */   
/*     */   public void setObjectIdInfo(ObjectIdInfo objectIdInfo) {
/* 292 */     this._objectIdInfo = objectIdInfo;
/*     */   }
/*     */   
/*     */   public void setViews(Class<?>[] views) {
/* 296 */     if (views == null) {
/* 297 */       this._viewMatcher = null;
/*     */     } else {
/* 299 */       this._viewMatcher = ViewMatcher.construct(views);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void assignIndex(int index)
/*     */   {
/* 307 */     if (this._propertyIndex != -1) {
/* 308 */       throw new IllegalStateException("Property '" + getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
/*     */     }
/* 310 */     this._propertyIndex = index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 321 */     return this._propName.getSimpleName();
/*     */   }
/*     */   
/*     */   public PropertyName getFullName()
/*     */   {
/* 326 */     return this._propName;
/*     */   }
/*     */   
/*     */   public JavaType getType() {
/* 330 */     return this._type;
/*     */   }
/*     */   
/*     */   public PropertyName getWrapperName() {
/* 334 */     return this._wrapperName;
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract AnnotatedMember getMember();
/*     */   
/*     */ 
/*     */   public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
/*     */   
/*     */   public <A extends Annotation> A getContextAnnotation(Class<A> acls)
/*     */   {
/* 345 */     return this._contextAnnotations.get(acls);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 353 */     if (isRequired()) {
/* 354 */       objectVisitor.property(this);
/*     */     } else {
/* 356 */       objectVisitor.optionalProperty(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Class<?> getDeclaringClass()
/*     */   {
/* 367 */     return getMember().getDeclaringClass();
/*     */   }
/*     */   
/* 370 */   public String getManagedReferenceName() { return this._managedReferenceName; }
/*     */   
/* 372 */   public ObjectIdInfo getObjectIdInfo() { return this._objectIdInfo; }
/*     */   
/*     */   public boolean hasValueDeserializer() {
/* 375 */     return (this._valueDeserializer != null) && (this._valueDeserializer != MISSING_VALUE_DESERIALIZER);
/*     */   }
/*     */   
/* 378 */   public boolean hasValueTypeDeserializer() { return this._valueTypeDeserializer != null; }
/*     */   
/*     */   public JsonDeserializer<Object> getValueDeserializer() {
/* 381 */     JsonDeserializer<Object> deser = this._valueDeserializer;
/* 382 */     if (deser == MISSING_VALUE_DESERIALIZER) {
/* 383 */       return null;
/*     */     }
/* 385 */     return deser;
/*     */   }
/*     */   
/* 388 */   public TypeDeserializer getValueTypeDeserializer() { return this._valueTypeDeserializer; }
/*     */   
/*     */   public boolean visibleInView(Class<?> activeView) {
/* 391 */     return (this._viewMatcher == null) || (this._viewMatcher.isVisibleForView(activeView));
/*     */   }
/*     */   
/* 394 */   public boolean hasViews() { return this._viewMatcher != null; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPropertyIndex()
/*     */   {
/* 403 */     return this._propertyIndex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCreatorIndex()
/*     */   {
/* 411 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getInjectableValueId()
/*     */   {
/* 417 */     return null;
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
/*     */   public abstract void deserializeAndSet(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext, Object paramObject)
/*     */     throws IOException;
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
/*     */   public abstract Object deserializeSetAndReturn(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext, Object paramObject)
/*     */     throws IOException;
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
/*     */   public abstract void set(Object paramObject1, Object paramObject2)
/*     */     throws IOException;
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
/*     */   public abstract Object setAndReturn(Object paramObject1, Object paramObject2)
/*     */     throws IOException;
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
/*     */   public final Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 482 */     JsonToken t = p.getCurrentToken();
/*     */     
/* 484 */     if (t == JsonToken.VALUE_NULL) {
/* 485 */       return this._valueDeserializer.getNullValue(ctxt);
/*     */     }
/* 487 */     if (this._valueTypeDeserializer != null) {
/* 488 */       return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
/*     */     }
/* 490 */     return this._valueDeserializer.deserialize(p, ctxt);
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
/*     */   protected void _throwAsIOE(JsonParser p, Exception e, Object value)
/*     */     throws IOException
/*     */   {
/* 505 */     if ((e instanceof IllegalArgumentException)) {
/* 506 */       String actType = value == null ? "[NULL]" : value.getClass().getName();
/* 507 */       StringBuilder msg = new StringBuilder("Problem deserializing property '").append(getName());
/* 508 */       msg.append("' (expected type: ").append(getType());
/* 509 */       msg.append("; actual type: ").append(actType).append(")");
/* 510 */       String origMsg = e.getMessage();
/* 511 */       if (origMsg != null) {
/* 512 */         msg.append(", problem: ").append(origMsg);
/*     */       } else {
/* 514 */         msg.append(" (no error message provided)");
/*     */       }
/* 516 */       throw JsonMappingException.from(p, msg.toString(), e);
/*     */     }
/* 518 */     _throwAsIOE(p, e);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected IOException _throwAsIOE(JsonParser p, Exception e)
/*     */     throws IOException
/*     */   {
/* 526 */     if ((e instanceof IOException)) {
/* 527 */       throw ((IOException)e);
/*     */     }
/* 529 */     if ((e instanceof RuntimeException)) {
/* 530 */       throw ((RuntimeException)e);
/*     */     }
/*     */     
/* 533 */     Throwable th = e;
/* 534 */     while (th.getCause() != null) {
/* 535 */       th = th.getCause();
/*     */     }
/* 537 */     throw JsonMappingException.from(p, th.getMessage(), th);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected IOException _throwAsIOE(Exception e) throws IOException {
/* 542 */     return _throwAsIOE((JsonParser)null, e);
/*     */   }
/*     */   
/*     */   protected void _throwAsIOE(Exception e, Object value)
/*     */     throws IOException
/*     */   {
/* 548 */     _throwAsIOE((JsonParser)null, e, value);
/*     */   }
/*     */   
/* 551 */   public String toString() { return "[property '" + getName() + "']"; }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\SettableBeanProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */