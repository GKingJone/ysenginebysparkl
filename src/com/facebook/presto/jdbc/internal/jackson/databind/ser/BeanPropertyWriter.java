/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.SerializableString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.SerializedString;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedField;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.JsonSchema;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.SchemaAware;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.BeanSerializerBase;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class BeanPropertyWriter
/*     */   extends PropertyWriter
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  47 */   public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final SerializedString _name;
/*     */   
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
/*     */ 
/*     */   protected final JavaType _declaredType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JavaType _cfgSerializationType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JavaType _nonTrivialBaseType;
/*     */   
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
/*     */ 
/*     */   protected final AnnotatedMember _member;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected transient Method _accessorMethod;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected transient Field _field;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonSerializer<Object> _serializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonSerializer<Object> _nullSerializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TypeSerializer _typeSerializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected transient PropertySerializerMap _dynamicSerializers;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _suppressNulls;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Object _suppressableValue;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Class<?>[] _includeInViews;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected transient HashMap<Object, Object> _internalSettings;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BeanPropertyWriter(BeanPropertyDefinition propDef, AnnotatedMember member, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, boolean suppressNulls, Object suppressableValue)
/*     */   {
/* 210 */     super(propDef);
/* 211 */     this._member = member;
/* 212 */     this._contextAnnotations = contextAnnotations;
/*     */     
/* 214 */     this._name = new SerializedString(propDef.getName());
/* 215 */     this._wrapperName = propDef.getWrapperName();
/* 216 */     this._includeInViews = propDef.findViews();
/*     */     
/* 218 */     this._declaredType = declaredType;
/* 219 */     this._serializer = ser;
/* 220 */     this._dynamicSerializers = (ser == null ? PropertySerializerMap.emptyForProperties() : null);
/*     */     
/* 222 */     this._typeSerializer = typeSer;
/* 223 */     this._cfgSerializationType = serType;
/*     */     
/* 225 */     if ((member instanceof AnnotatedField)) {
/* 226 */       this._accessorMethod = null;
/* 227 */       this._field = ((Field)member.getMember());
/* 228 */     } else if ((member instanceof AnnotatedMethod)) {
/* 229 */       this._accessorMethod = ((Method)member.getMember());
/* 230 */       this._field = null;
/*     */     }
/*     */     else
/*     */     {
/* 234 */       this._accessorMethod = null;
/* 235 */       this._field = null;
/*     */     }
/* 237 */     this._suppressNulls = suppressNulls;
/* 238 */     this._suppressableValue = suppressableValue;
/*     */     
/*     */ 
/* 241 */     this._nullSerializer = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanPropertyWriter()
/*     */   {
/* 252 */     super(PropertyMetadata.STD_REQUIRED_OR_OPTIONAL);
/* 253 */     this._member = null;
/* 254 */     this._contextAnnotations = null;
/*     */     
/* 256 */     this._name = null;
/* 257 */     this._wrapperName = null;
/* 258 */     this._includeInViews = null;
/*     */     
/* 260 */     this._declaredType = null;
/* 261 */     this._serializer = null;
/* 262 */     this._dynamicSerializers = null;
/* 263 */     this._typeSerializer = null;
/* 264 */     this._cfgSerializationType = null;
/*     */     
/* 266 */     this._accessorMethod = null;
/* 267 */     this._field = null;
/* 268 */     this._suppressNulls = false;
/* 269 */     this._suppressableValue = null;
/*     */     
/* 271 */     this._nullSerializer = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base)
/*     */   {
/* 278 */     this(base, base._name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base, PropertyName name)
/*     */   {
/* 285 */     super(base);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 292 */     this._name = new SerializedString(name.getSimpleName());
/* 293 */     this._wrapperName = base._wrapperName;
/*     */     
/* 295 */     this._contextAnnotations = base._contextAnnotations;
/* 296 */     this._declaredType = base._declaredType;
/*     */     
/* 298 */     this._member = base._member;
/* 299 */     this._accessorMethod = base._accessorMethod;
/* 300 */     this._field = base._field;
/*     */     
/* 302 */     this._serializer = base._serializer;
/* 303 */     this._nullSerializer = base._nullSerializer;
/*     */     
/* 305 */     if (base._internalSettings != null) {
/* 306 */       this._internalSettings = new HashMap(base._internalSettings);
/*     */     }
/*     */     
/* 309 */     this._cfgSerializationType = base._cfgSerializationType;
/* 310 */     this._dynamicSerializers = base._dynamicSerializers;
/* 311 */     this._suppressNulls = base._suppressNulls;
/* 312 */     this._suppressableValue = base._suppressableValue;
/* 313 */     this._includeInViews = base._includeInViews;
/* 314 */     this._typeSerializer = base._typeSerializer;
/* 315 */     this._nonTrivialBaseType = base._nonTrivialBaseType;
/*     */   }
/*     */   
/*     */   protected BeanPropertyWriter(BeanPropertyWriter base, SerializedString name) {
/* 319 */     super(base);
/* 320 */     this._name = name;
/* 321 */     this._wrapperName = base._wrapperName;
/*     */     
/* 323 */     this._member = base._member;
/* 324 */     this._contextAnnotations = base._contextAnnotations;
/* 325 */     this._declaredType = base._declaredType;
/* 326 */     this._accessorMethod = base._accessorMethod;
/* 327 */     this._field = base._field;
/* 328 */     this._serializer = base._serializer;
/* 329 */     this._nullSerializer = base._nullSerializer;
/* 330 */     if (base._internalSettings != null) {
/* 331 */       this._internalSettings = new HashMap(base._internalSettings);
/*     */     }
/*     */     
/* 334 */     this._cfgSerializationType = base._cfgSerializationType;
/* 335 */     this._dynamicSerializers = base._dynamicSerializers;
/* 336 */     this._suppressNulls = base._suppressNulls;
/* 337 */     this._suppressableValue = base._suppressableValue;
/* 338 */     this._includeInViews = base._includeInViews;
/* 339 */     this._typeSerializer = base._typeSerializer;
/* 340 */     this._nonTrivialBaseType = base._nonTrivialBaseType;
/*     */   }
/*     */   
/*     */   public BeanPropertyWriter rename(NameTransformer transformer) {
/* 344 */     String newName = transformer.transform(this._name.getValue());
/* 345 */     if (newName.equals(this._name.toString())) {
/* 346 */       return this;
/*     */     }
/* 348 */     return _new(PropertyName.construct(newName));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanPropertyWriter _new(PropertyName newName)
/*     */   {
/* 357 */     return new BeanPropertyWriter(this, newName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void assignTypeSerializer(TypeSerializer typeSer)
/*     */   {
/* 367 */     this._typeSerializer = typeSer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void assignSerializer(JsonSerializer<Object> ser)
/*     */   {
/* 375 */     if ((this._serializer != null) && (this._serializer != ser)) {
/* 376 */       throw new IllegalStateException("Can not override serializer");
/*     */     }
/* 378 */     this._serializer = ser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void assignNullSerializer(JsonSerializer<Object> nullSer)
/*     */   {
/* 386 */     if ((this._nullSerializer != null) && (this._nullSerializer != nullSer)) {
/* 387 */       throw new IllegalStateException("Can not override null serializer");
/*     */     }
/* 389 */     this._nullSerializer = nullSer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper)
/*     */   {
/* 397 */     return new UnwrappingBeanPropertyWriter(this, unwrapper);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNonTrivialBaseType(JavaType t)
/*     */   {
/* 406 */     this._nonTrivialBaseType = t;
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
/*     */   Object readResolve()
/*     */   {
/* 421 */     if ((this._member instanceof AnnotatedField)) {
/* 422 */       this._accessorMethod = null;
/* 423 */       this._field = ((Field)this._member.getMember());
/* 424 */     } else if ((this._member instanceof AnnotatedMethod)) {
/* 425 */       this._accessorMethod = ((Method)this._member.getMember());
/* 426 */       this._field = null;
/*     */     }
/* 428 */     if (this._serializer == null) {
/* 429 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */     }
/* 431 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 443 */     return this._name.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public PropertyName getFullName()
/*     */   {
/* 449 */     return new PropertyName(this._name.getValue());
/*     */   }
/*     */   
/*     */   public JavaType getType()
/*     */   {
/* 454 */     return this._declaredType;
/*     */   }
/*     */   
/*     */   public PropertyName getWrapperName()
/*     */   {
/* 459 */     return this._wrapperName;
/*     */   }
/*     */   
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> acls)
/*     */   {
/* 465 */     return this._member == null ? null : this._member.getAnnotation(acls);
/*     */   }
/*     */   
/*     */ 
/*     */   public <A extends Annotation> A getContextAnnotation(Class<A> acls)
/*     */   {
/* 471 */     return this._contextAnnotations == null ? null : this._contextAnnotations.get(acls);
/*     */   }
/*     */   
/*     */ 
/*     */   public AnnotatedMember getMember()
/*     */   {
/* 477 */     return this._member;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void _depositSchemaProperty(ObjectNode propertiesNode, JsonNode schemaNode)
/*     */   {
/* 483 */     propertiesNode.set(getName(), schemaNode);
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
/*     */   public Object getInternalSetting(Object key)
/*     */   {
/* 499 */     return this._internalSettings == null ? null : this._internalSettings.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object setInternalSetting(Object key, Object value)
/*     */   {
/* 508 */     if (this._internalSettings == null) {
/* 509 */       this._internalSettings = new HashMap();
/*     */     }
/* 511 */     return this._internalSettings.put(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object removeInternalSetting(Object key)
/*     */   {
/* 520 */     Object removed = null;
/* 521 */     if (this._internalSettings != null) {
/* 522 */       removed = this._internalSettings.remove(key);
/*     */       
/* 524 */       if (this._internalSettings.size() == 0) {
/* 525 */         this._internalSettings = null;
/*     */       }
/*     */     }
/* 528 */     return removed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SerializableString getSerializedName()
/*     */   {
/* 538 */     return this._name;
/*     */   }
/*     */   
/*     */   public boolean hasSerializer() {
/* 542 */     return this._serializer != null;
/*     */   }
/*     */   
/*     */   public boolean hasNullSerializer() {
/* 546 */     return this._nullSerializer != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TypeSerializer getTypeSerializer()
/*     */   {
/* 553 */     return this._typeSerializer;
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
/*     */   public boolean isUnwrapping()
/*     */   {
/* 567 */     return false;
/*     */   }
/*     */   
/*     */   public boolean willSuppressNulls() {
/* 571 */     return this._suppressNulls;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean wouldConflictWithName(PropertyName name)
/*     */   {
/* 581 */     if (this._wrapperName != null) {
/* 582 */       return this._wrapperName.equals(name);
/*     */     }
/*     */     
/* 585 */     return (name.hasSimpleName(this._name.getValue())) && (!name.hasNamespace());
/*     */   }
/*     */   
/*     */   public JsonSerializer<Object> getSerializer()
/*     */   {
/* 590 */     return this._serializer;
/*     */   }
/*     */   
/*     */   public JavaType getSerializationType() {
/* 594 */     return this._cfgSerializationType;
/*     */   }
/*     */   
/*     */   public Class<?> getRawSerializationType() {
/* 598 */     return this._cfgSerializationType == null ? null : this._cfgSerializationType.getRawClass();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public Class<?> getPropertyType()
/*     */   {
/* 607 */     if (this._accessorMethod != null) {
/* 608 */       return this._accessorMethod.getReturnType();
/*     */     }
/* 610 */     if (this._field != null) {
/* 611 */       return this._field.getType();
/*     */     }
/* 613 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public Type getGenericPropertyType()
/*     */   {
/* 625 */     if (this._accessorMethod != null) {
/* 626 */       return this._accessorMethod.getGenericReturnType();
/*     */     }
/* 628 */     if (this._field != null) {
/* 629 */       return this._field.getGenericType();
/*     */     }
/* 631 */     return null;
/*     */   }
/*     */   
/*     */   public Class<?>[] getViews() {
/* 635 */     return this._includeInViews;
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
/*     */   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov)
/*     */     throws Exception
/*     */   {
/* 653 */     Object value = this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, new Object[0]);
/*     */     
/*     */ 
/*     */ 
/* 657 */     if (value == null) {
/* 658 */       if (this._nullSerializer != null) {
/* 659 */         gen.writeFieldName(this._name);
/* 660 */         this._nullSerializer.serialize(null, gen, prov);
/*     */       }
/* 662 */       return;
/*     */     }
/*     */     
/* 665 */     JsonSerializer<Object> ser = this._serializer;
/* 666 */     if (ser == null) {
/* 667 */       Class<?> cls = value.getClass();
/* 668 */       PropertySerializerMap m = this._dynamicSerializers;
/* 669 */       ser = m.serializerFor(cls);
/* 670 */       if (ser == null) {
/* 671 */         ser = _findAndAddDynamic(m, cls, prov);
/*     */       }
/*     */     }
/*     */     
/* 675 */     if (this._suppressableValue != null) {
/* 676 */       if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 677 */         if (!ser.isEmpty(prov, value)) {}
/*     */ 
/*     */       }
/* 680 */       else if (this._suppressableValue.equals(value)) {
/* 681 */         return;
/*     */       }
/*     */     }
/*     */     
/* 685 */     if (value == bean)
/*     */     {
/* 687 */       if (_handleSelfReference(bean, gen, prov, ser)) {
/* 688 */         return;
/*     */       }
/*     */     }
/* 691 */     gen.writeFieldName(this._name);
/* 692 */     if (this._typeSerializer == null) {
/* 693 */       ser.serialize(value, gen, prov);
/*     */     } else {
/* 695 */       ser.serializeWithType(value, gen, prov, this._typeSerializer);
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
/*     */   public void serializeAsOmittedField(Object bean, JsonGenerator gen, SerializerProvider prov)
/*     */     throws Exception
/*     */   {
/* 709 */     if (!gen.canOmitFields()) {
/* 710 */       gen.writeOmittedField(this._name.getValue());
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
/*     */   public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov)
/*     */     throws Exception
/*     */   {
/* 725 */     Object value = this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, new Object[0]);
/*     */     
/* 727 */     if (value == null) {
/* 728 */       if (this._nullSerializer != null) {
/* 729 */         this._nullSerializer.serialize(null, gen, prov);
/*     */       } else {
/* 731 */         gen.writeNull();
/*     */       }
/* 733 */       return;
/*     */     }
/*     */     
/* 736 */     JsonSerializer<Object> ser = this._serializer;
/* 737 */     if (ser == null) {
/* 738 */       Class<?> cls = value.getClass();
/* 739 */       PropertySerializerMap map = this._dynamicSerializers;
/* 740 */       ser = map.serializerFor(cls);
/* 741 */       if (ser == null) {
/* 742 */         ser = _findAndAddDynamic(map, cls, prov);
/*     */       }
/*     */     }
/*     */     
/* 746 */     if (this._suppressableValue != null) {
/* 747 */       if (MARKER_FOR_EMPTY == this._suppressableValue) {
/* 748 */         if (ser.isEmpty(prov, value))
/*     */         {
/* 750 */           serializeAsPlaceholder(bean, gen, prov);
/*     */         }
/*     */       }
/* 753 */       else if (this._suppressableValue.equals(value))
/*     */       {
/*     */ 
/* 756 */         serializeAsPlaceholder(bean, gen, prov);
/* 757 */         return;
/*     */       }
/*     */     }
/*     */     
/* 761 */     if ((value == bean) && 
/* 762 */       (_handleSelfReference(bean, gen, prov, ser))) {
/* 763 */       return;
/*     */     }
/*     */     
/* 766 */     if (this._typeSerializer == null) {
/* 767 */       ser.serialize(value, gen, prov);
/*     */     } else {
/* 769 */       ser.serializeWithType(value, gen, prov, this._typeSerializer);
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
/*     */   public void serializeAsPlaceholder(Object bean, JsonGenerator gen, SerializerProvider prov)
/*     */     throws Exception
/*     */   {
/* 784 */     if (this._nullSerializer != null) {
/* 785 */       this._nullSerializer.serialize(null, gen, prov);
/*     */     } else {
/* 787 */       gen.writeNull();
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
/*     */   public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 801 */     if (v != null) {
/* 802 */       if (isRequired()) {
/* 803 */         v.property(this);
/*     */       } else {
/* 805 */         v.optionalProperty(this);
/*     */       }
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
/*     */   @Deprecated
/*     */   public void depositSchemaProperty(ObjectNode propertiesNode, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 827 */     JavaType propType = getSerializationType();
/*     */     
/*     */ 
/* 830 */     Type hint = (Type)(propType == null ? getType() : propType.getRawClass());
/*     */     
/*     */ 
/* 833 */     JsonSerializer<Object> ser = getSerializer();
/* 834 */     if (ser == null) {
/* 835 */       ser = provider.findValueSerializer(getType(), this);
/*     */     }
/* 837 */     boolean isOptional = !isRequired();
/* 838 */     JsonNode schemaNode; JsonNode schemaNode; if ((ser instanceof SchemaAware)) {
/* 839 */       schemaNode = ((SchemaAware)ser).getSchema(provider, hint, isOptional);
/*     */     }
/*     */     else {
/* 842 */       schemaNode = JsonSchema.getDefaultSchemaNode();
/*     */     }
/*     */     
/* 845 */     _depositSchemaProperty(propertiesNode, schemaNode);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/*     */     SerializerAndMapResult result;
/*     */     
/*     */ 
/*     */     SerializerAndMapResult result;
/*     */     
/* 858 */     if (this._nonTrivialBaseType != null) {
/* 859 */       JavaType t = provider.constructSpecializedType(this._nonTrivialBaseType, type);
/*     */       
/* 861 */       result = map.findAndAddPrimarySerializer(t, provider, this);
/*     */     } else {
/* 863 */       result = map.findAndAddPrimarySerializer(type, provider, this);
/*     */     }
/*     */     
/* 866 */     if (map != result.map) {
/* 867 */       this._dynamicSerializers = result.map;
/*     */     }
/* 869 */     return result.serializer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Object get(Object bean)
/*     */     throws Exception
/*     */   {
/* 881 */     return this._accessorMethod == null ? this._field.get(bean) : this._accessorMethod.invoke(bean, new Object[0]);
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
/*     */   protected boolean _handleSelfReference(Object bean, JsonGenerator gen, SerializerProvider prov, JsonSerializer<?> ser)
/*     */     throws JsonMappingException
/*     */   {
/* 902 */     if ((prov.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES)) && (!ser.usesObjectId()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 909 */       if ((ser instanceof BeanSerializerBase)) {
/* 910 */         prov.reportMappingProblem("Direct self-reference leading to cycle", new Object[0]);
/*     */       }
/*     */     }
/* 913 */     return false;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 918 */     StringBuilder sb = new StringBuilder(40);
/* 919 */     sb.append("property '").append(getName()).append("' (");
/* 920 */     if (this._accessorMethod != null) {
/* 921 */       sb.append("via method ").append(this._accessorMethod.getDeclaringClass().getName()).append("#").append(this._accessorMethod.getName());
/*     */ 
/*     */     }
/* 924 */     else if (this._field != null) {
/* 925 */       sb.append("field \"").append(this._field.getDeclaringClass().getName()).append("#").append(this._field.getName());
/*     */     }
/*     */     else {
/* 928 */       sb.append("virtual");
/*     */     }
/* 930 */     if (this._serializer == null) {
/* 931 */       sb.append(", no static serializer");
/*     */     } else {
/* 933 */       sb.append(", static serializer of type " + this._serializer.getClass().getName());
/*     */     }
/*     */     
/* 936 */     sb.append(')');
/* 937 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\BeanPropertyWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */