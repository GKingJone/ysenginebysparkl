/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.JsonSerializableSchema;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.AnyGetterWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanSerializerBuilder;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContainerSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.PropertyFilter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.ObjectIdWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.WritableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class BeanSerializerBase extends StdSerializer<Object> implements com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer, com.facebook.presto.jdbc.internal.jackson.databind.ser.ResolvableSerializer, com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitable, com.facebook.presto.jdbc.internal.jackson.databind.jsonschema.SchemaAware
/*     */ {
/*  39 */   protected static final PropertyName NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
/*     */   
/*  41 */   protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final BeanPropertyWriter[] _props;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final BeanPropertyWriter[] _filteredProps;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final AnyGetterWriter _anyGetterWriter;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Object _propertyFilterId;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final AnnotatedMember _typeId;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final ObjectIdWriter _objectIdWriter;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final JsonFormat.Shape _serializationShape;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanSerializerBase(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
/*     */   {
/* 105 */     super(type);
/* 106 */     this._props = properties;
/* 107 */     this._filteredProps = filteredProperties;
/* 108 */     if (builder == null) {
/* 109 */       this._typeId = null;
/* 110 */       this._anyGetterWriter = null;
/* 111 */       this._propertyFilterId = null;
/* 112 */       this._objectIdWriter = null;
/* 113 */       this._serializationShape = null;
/*     */     } else {
/* 115 */       this._typeId = builder.getTypeId();
/* 116 */       this._anyGetterWriter = builder.getAnyGetter();
/* 117 */       this._propertyFilterId = builder.getFilterId();
/* 118 */       this._objectIdWriter = builder.getObjectIdWriter();
/* 119 */       JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
/* 120 */       this._serializationShape = (format == null ? null : format.getShape());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public BeanSerializerBase(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
/*     */   {
/* 127 */     super(src._handledType);
/* 128 */     this._props = properties;
/* 129 */     this._filteredProps = filteredProperties;
/*     */     
/* 131 */     this._typeId = src._typeId;
/* 132 */     this._anyGetterWriter = src._anyGetterWriter;
/* 133 */     this._objectIdWriter = src._objectIdWriter;
/* 134 */     this._propertyFilterId = src._propertyFilterId;
/* 135 */     this._serializationShape = src._serializationShape;
/*     */   }
/*     */   
/*     */ 
/*     */   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter)
/*     */   {
/* 141 */     this(src, objectIdWriter, src._propertyFilterId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId)
/*     */   {
/* 150 */     super(src._handledType);
/* 151 */     this._props = src._props;
/* 152 */     this._filteredProps = src._filteredProps;
/*     */     
/* 154 */     this._typeId = src._typeId;
/* 155 */     this._anyGetterWriter = src._anyGetterWriter;
/* 156 */     this._objectIdWriter = objectIdWriter;
/* 157 */     this._propertyFilterId = filterId;
/* 158 */     this._serializationShape = src._serializationShape;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected BeanSerializerBase(BeanSerializerBase src, String[] toIgnore)
/*     */   {
/* 164 */     this(src, ArrayBuilders.arrayToSet(toIgnore));
/*     */   }
/*     */   
/*     */   protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore)
/*     */   {
/* 169 */     super(src._handledType);
/*     */     
/* 171 */     BeanPropertyWriter[] propsIn = src._props;
/* 172 */     BeanPropertyWriter[] fpropsIn = src._filteredProps;
/* 173 */     int len = propsIn.length;
/*     */     
/* 175 */     ArrayList<BeanPropertyWriter> propsOut = new ArrayList(len);
/* 176 */     ArrayList<BeanPropertyWriter> fpropsOut = fpropsIn == null ? null : new ArrayList(len);
/*     */     
/* 178 */     for (int i = 0; i < len; i++) {
/* 179 */       BeanPropertyWriter bpw = propsIn[i];
/*     */       
/* 181 */       if ((toIgnore == null) || (!toIgnore.contains(bpw.getName())))
/*     */       {
/*     */ 
/* 184 */         propsOut.add(bpw);
/* 185 */         if (fpropsIn != null)
/* 186 */           fpropsOut.add(fpropsIn[i]);
/*     */       }
/*     */     }
/* 189 */     this._props = ((BeanPropertyWriter[])propsOut.toArray(new BeanPropertyWriter[propsOut.size()]));
/* 190 */     this._filteredProps = (fpropsOut == null ? null : (BeanPropertyWriter[])fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()]));
/*     */     
/* 192 */     this._typeId = src._typeId;
/* 193 */     this._anyGetterWriter = src._anyGetterWriter;
/* 194 */     this._objectIdWriter = src._objectIdWriter;
/* 195 */     this._propertyFilterId = src._propertyFilterId;
/* 196 */     this._serializationShape = src._serializationShape;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract BeanSerializerBase withObjectIdWriter(ObjectIdWriter paramObjectIdWriter);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract BeanSerializerBase withIgnorals(Set<String> paramSet);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected BeanSerializerBase withIgnorals(String[] toIgnore)
/*     */   {
/* 223 */     return withIgnorals(ArrayBuilders.arrayToSet(toIgnore));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract BeanSerializerBase asArraySerializer();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract BeanSerializerBase withFilterId(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanSerializerBase(BeanSerializerBase src)
/*     */   {
/* 249 */     this(src, src._props, src._filteredProps);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanSerializerBase(BeanSerializerBase src, NameTransformer unwrapper)
/*     */   {
/* 257 */     this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
/*     */   }
/*     */   
/*     */ 
/*     */   private static final BeanPropertyWriter[] rename(BeanPropertyWriter[] props, NameTransformer transformer)
/*     */   {
/* 263 */     if ((props == null) || (props.length == 0) || (transformer == null) || (transformer == NameTransformer.NOP)) {
/* 264 */       return props;
/*     */     }
/* 266 */     int len = props.length;
/* 267 */     BeanPropertyWriter[] result = new BeanPropertyWriter[len];
/* 268 */     for (int i = 0; i < len; i++) {
/* 269 */       BeanPropertyWriter bpw = props[i];
/* 270 */       if (bpw != null) {
/* 271 */         result[i] = bpw.rename(transformer);
/*     */       }
/*     */     }
/* 274 */     return result;
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
/*     */   public void resolve(SerializerProvider provider)
/*     */     throws JsonMappingException
/*     */   {
/* 291 */     int filteredCount = this._filteredProps == null ? 0 : this._filteredProps.length;
/* 292 */     int i = 0; for (int len = this._props.length; i < len; i++) {
/* 293 */       BeanPropertyWriter prop = this._props[i];
/*     */       
/* 295 */       if ((!prop.willSuppressNulls()) && (!prop.hasNullSerializer())) {
/* 296 */         JsonSerializer<Object> nullSer = provider.findNullValueSerializer(prop);
/* 297 */         if (nullSer != null) {
/* 298 */           prop.assignNullSerializer(nullSer);
/*     */           
/* 300 */           if (i < filteredCount) {
/* 301 */             BeanPropertyWriter w2 = this._filteredProps[i];
/* 302 */             if (w2 != null) {
/* 303 */               w2.assignNullSerializer(nullSer);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 309 */       if (!prop.hasSerializer())
/*     */       {
/*     */ 
/*     */ 
/* 313 */         JsonSerializer<Object> ser = findConvertingSerializer(provider, prop);
/* 314 */         if (ser == null)
/*     */         {
/* 316 */           JavaType type = prop.getSerializationType();
/*     */           
/*     */ 
/*     */ 
/* 320 */           if (type == null)
/*     */           {
/*     */ 
/*     */ 
/* 324 */             type = prop.getType();
/* 325 */             if (!type.isFinal()) {
/* 326 */               if ((!type.isContainerType()) && (type.containedTypeCount() <= 0)) continue;
/* 327 */               prop.setNonTrivialBaseType(type); continue;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 332 */           ser = provider.findValueSerializer(type, prop);
/*     */           
/*     */ 
/*     */ 
/* 336 */           if (type.isContainerType()) {
/* 337 */             TypeSerializer typeSer = (TypeSerializer)type.getContentType().getTypeHandler();
/* 338 */             if (typeSer != null)
/*     */             {
/* 340 */               if ((ser instanceof ContainerSerializer))
/*     */               {
/*     */ 
/* 343 */                 JsonSerializer<Object> ser2 = ((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
/* 344 */                 ser = ser2;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 349 */         prop.assignSerializer(ser);
/*     */         
/* 351 */         if (i < filteredCount) {
/* 352 */           BeanPropertyWriter w2 = this._filteredProps[i];
/* 353 */           if (w2 != null) {
/* 354 */             w2.assignSerializer(ser);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 360 */     if (this._anyGetterWriter != null)
/*     */     {
/* 362 */       this._anyGetterWriter.resolve(provider);
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
/*     */   protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider, BeanPropertyWriter prop)
/*     */     throws JsonMappingException
/*     */   {
/* 377 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 378 */     if (intr != null) {
/* 379 */       AnnotatedMember m = prop.getMember();
/* 380 */       if (m != null) {
/* 381 */         Object convDef = intr.findSerializationConverter(m);
/* 382 */         if (convDef != null) {
/* 383 */           Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
/* 384 */           JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
/*     */           
/* 386 */           JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer(delegateType, prop);
/*     */           
/* 388 */           return new StdDelegatingSerializer(conv, delegateType, ser);
/*     */         }
/*     */       }
/*     */     }
/* 392 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 401 */     AnnotationIntrospector intr = provider.getAnnotationIntrospector();
/* 402 */     AnnotatedMember accessor = (property == null) || (intr == null) ? null : property.getMember();
/*     */     
/* 404 */     com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig config = provider.getConfig();
/*     */     
/*     */ 
/*     */ 
/* 408 */     JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
/* 409 */     JsonFormat.Shape shape = null;
/* 410 */     if ((format != null) && (format.hasShape())) {
/* 411 */       shape = format.getShape();
/*     */       
/* 413 */       if ((shape != JsonFormat.Shape.ANY) && (shape != this._serializationShape) && 
/* 414 */         (this._handledType.isEnum())) {
/* 415 */         switch (shape)
/*     */         {
/*     */ 
/*     */         case STRING: 
/*     */         case NUMBER: 
/*     */         case NUMBER_INT: 
/* 421 */           BeanDescription desc = config.introspectClassAnnotations(this._handledType);
/* 422 */           JsonSerializer<?> ser = EnumSerializer.construct(this._handledType, provider.getConfig(), desc, format);
/*     */           
/* 424 */           return provider.handlePrimaryContextualization(ser, property);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/* 430 */     ObjectIdWriter oiw = this._objectIdWriter;
/* 431 */     Set<String> ignoredProps = null;
/* 432 */     Object newFilterId = null;
/*     */     
/*     */ 
/* 435 */     if (accessor != null) {
/* 436 */       JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
/* 437 */       if (ignorals != null) {
/* 438 */         ignoredProps = ignorals.findIgnoredForSerialization();
/*     */       }
/* 440 */       ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
/* 441 */       if (objectIdInfo == null)
/*     */       {
/* 443 */         if (oiw != null) {
/* 444 */           objectIdInfo = intr.findObjectReferenceInfo(accessor, new ObjectIdInfo(NAME_FOR_OBJECT_REF, null, null, null));
/*     */           
/* 446 */           oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 455 */         objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
/*     */         
/* 457 */         Class<?> implClass = objectIdInfo.getGeneratorType();
/* 458 */         JavaType type = provider.constructType(implClass);
/* 459 */         JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/*     */         
/* 461 */         if (implClass == com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class) {
/* 462 */           String propName = objectIdInfo.getPropertyName().getSimpleName();
/* 463 */           BeanPropertyWriter idProp = null;
/*     */           
/* 465 */           int i = 0; for (int len = this._props.length;; i++) {
/* 466 */             if (i == len) {
/* 467 */               throw new IllegalArgumentException("Invalid Object Id definition for " + this._handledType.getName() + ": can not find property with name '" + propName + "'");
/*     */             }
/*     */             
/* 470 */             BeanPropertyWriter prop = this._props[i];
/* 471 */             if (propName.equals(prop.getName())) {
/* 472 */               idProp = prop;
/*     */               
/*     */ 
/*     */ 
/* 476 */               if (i <= 0) break;
/* 477 */               System.arraycopy(this._props, 0, this._props, 1, i);
/* 478 */               this._props[0] = idProp;
/* 479 */               if (this._filteredProps == null) break;
/* 480 */               BeanPropertyWriter fp = this._filteredProps[i];
/* 481 */               System.arraycopy(this._filteredProps, 0, this._filteredProps, 1, i);
/* 482 */               this._filteredProps[0] = fp;
/* 483 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 488 */           idType = idProp.getType();
/* 489 */           ObjectIdGenerator<?> gen = new com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
/* 490 */           oiw = ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
/*     */         } else {
/* 492 */           ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
/* 493 */           oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 499 */       Object filterId = intr.findFilterId(accessor);
/* 500 */       if (filterId != null)
/*     */       {
/* 502 */         if ((this._propertyFilterId == null) || (!filterId.equals(this._propertyFilterId))) {
/* 503 */           newFilterId = filterId;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 508 */     BeanSerializerBase contextual = this;
/* 509 */     if (oiw != null) {
/* 510 */       JsonSerializer<?> ser = provider.findValueSerializer(oiw.idType, property);
/* 511 */       oiw = oiw.withSerializer(ser);
/* 512 */       if (oiw != this._objectIdWriter) {
/* 513 */         contextual = contextual.withObjectIdWriter(oiw);
/*     */       }
/*     */     }
/*     */     
/* 517 */     if ((ignoredProps != null) && (!ignoredProps.isEmpty())) {
/* 518 */       contextual = contextual.withIgnorals(ignoredProps);
/*     */     }
/* 520 */     if (newFilterId != null) {
/* 521 */       contextual = contextual.withFilterId(newFilterId);
/*     */     }
/* 523 */     if (shape == null) {
/* 524 */       shape = this._serializationShape;
/*     */     }
/* 526 */     if (shape == JsonFormat.Shape.ARRAY) {
/* 527 */       return contextual.asArraySerializer();
/*     */     }
/* 529 */     return contextual;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public java.util.Iterator<com.facebook.presto.jdbc.internal.jackson.databind.ser.PropertyWriter> properties()
/*     */   {
/* 540 */     return java.util.Arrays.asList(this._props).iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean usesObjectId()
/*     */   {
/* 551 */     return this._objectIdWriter != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void serialize(Object paramObject, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 565 */     if (this._objectIdWriter != null) {
/* 566 */       gen.setCurrentValue(bean);
/* 567 */       _serializeWithObjectId(bean, gen, provider, typeSer);
/* 568 */       return;
/*     */     }
/*     */     
/* 571 */     String typeStr = this._typeId == null ? null : _customTypeId(bean);
/* 572 */     if (typeStr == null) {
/* 573 */       typeSer.writeTypePrefixForObject(bean, gen);
/*     */     } else {
/* 575 */       typeSer.writeCustomTypePrefixForObject(bean, gen, typeStr);
/*     */     }
/* 577 */     gen.setCurrentValue(bean);
/* 578 */     if (this._propertyFilterId != null) {
/* 579 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 581 */       serializeFields(bean, gen, provider);
/*     */     }
/* 583 */     if (typeStr == null) {
/* 584 */       typeSer.writeTypeSuffixForObject(bean, gen);
/*     */     } else {
/* 586 */       typeSer.writeCustomTypeSuffixForObject(bean, gen, typeStr);
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, boolean startEndObject)
/*     */     throws IOException
/*     */   {
/* 593 */     ObjectIdWriter w = this._objectIdWriter;
/* 594 */     WritableObjectId objectId = provider.findObjectId(bean, w.generator);
/*     */     
/* 596 */     if (objectId.writeAsId(gen, provider, w)) {
/* 597 */       return;
/*     */     }
/*     */     
/* 600 */     Object id = objectId.generateId(bean);
/* 601 */     if (w.alwaysAsId) {
/* 602 */       w.serializer.serialize(id, gen, provider);
/* 603 */       return;
/*     */     }
/* 605 */     if (startEndObject) {
/* 606 */       gen.writeStartObject(bean);
/*     */     }
/* 608 */     objectId.writeAsField(gen, provider, w);
/* 609 */     if (this._propertyFilterId != null) {
/* 610 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 612 */       serializeFields(bean, gen, provider);
/*     */     }
/* 614 */     if (startEndObject) {
/* 615 */       gen.writeEndObject();
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*     */     throws IOException
/*     */   {
/* 622 */     ObjectIdWriter w = this._objectIdWriter;
/* 623 */     WritableObjectId objectId = provider.findObjectId(bean, w.generator);
/*     */     
/* 625 */     if (objectId.writeAsId(gen, provider, w)) {
/* 626 */       return;
/*     */     }
/*     */     
/* 629 */     Object id = objectId.generateId(bean);
/* 630 */     if (w.alwaysAsId) {
/* 631 */       w.serializer.serialize(id, gen, provider);
/* 632 */       return;
/*     */     }
/*     */     
/* 635 */     _serializeObjectId(bean, gen, provider, typeSer, objectId);
/*     */   }
/*     */   
/*     */   protected void _serializeObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer, WritableObjectId objectId)
/*     */     throws IOException
/*     */   {
/* 641 */     ObjectIdWriter w = this._objectIdWriter;
/* 642 */     String typeStr = this._typeId == null ? null : _customTypeId(bean);
/* 643 */     if (typeStr == null) {
/* 644 */       typeSer.writeTypePrefixForObject(bean, gen);
/*     */     } else {
/* 646 */       typeSer.writeCustomTypePrefixForObject(bean, gen, typeStr);
/*     */     }
/* 648 */     objectId.writeAsField(gen, provider, w);
/* 649 */     if (this._propertyFilterId != null) {
/* 650 */       serializeFieldsFiltered(bean, gen, provider);
/*     */     } else {
/* 652 */       serializeFields(bean, gen, provider);
/*     */     }
/* 654 */     if (typeStr == null) {
/* 655 */       typeSer.writeTypeSuffixForObject(bean, gen);
/*     */     } else {
/* 657 */       typeSer.writeCustomTypeSuffixForObject(bean, gen, typeStr);
/*     */     }
/*     */   }
/*     */   
/*     */   protected final String _customTypeId(Object bean)
/*     */   {
/* 663 */     Object typeId = this._typeId.getValue(bean);
/* 664 */     if (typeId == null) {
/* 665 */       return "";
/*     */     }
/* 667 */     return (typeId instanceof String) ? (String)typeId : typeId.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*     */     BeanPropertyWriter[] props;
/*     */     
/*     */ 
/*     */     BeanPropertyWriter[] props;
/*     */     
/* 680 */     if ((this._filteredProps != null) && (provider.getActiveView() != null)) {
/* 681 */       props = this._filteredProps;
/*     */     } else {
/* 683 */       props = this._props;
/*     */     }
/* 685 */     int i = 0;
/*     */     try {
/* 687 */       for (int len = props.length; i < len; i++) {
/* 688 */         BeanPropertyWriter prop = props[i];
/* 689 */         if (prop != null) {
/* 690 */           prop.serializeAsField(bean, gen, provider);
/*     */         }
/*     */       }
/* 693 */       if (this._anyGetterWriter != null) {
/* 694 */         this._anyGetterWriter.getAndSerialize(bean, gen, provider);
/*     */       }
/*     */     } catch (Exception e) {
/* 697 */       String name = i == props.length ? "[anySetter]" : props[i].getName();
/* 698 */       wrapAndThrow(provider, e, bean, name);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (StackOverflowError e)
/*     */     {
/*     */ 
/*     */ 
/* 706 */       JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e);
/*     */       
/* 708 */       String name = i == props.length ? "[anySetter]" : props[i].getName();
/* 709 */       mapE.prependPath(new com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException.Reference(bean, name));
/* 710 */       throw mapE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException, com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException
/*     */   {
/*     */     BeanPropertyWriter[] props;
/*     */     
/*     */ 
/*     */ 
/*     */     BeanPropertyWriter[] props;
/*     */     
/*     */ 
/* 727 */     if ((this._filteredProps != null) && (provider.getActiveView() != null)) {
/* 728 */       props = this._filteredProps;
/*     */     } else {
/* 730 */       props = this._props;
/*     */     }
/* 732 */     PropertyFilter filter = findPropertyFilter(provider, this._propertyFilterId, bean);
/*     */     
/* 734 */     if (filter == null) {
/* 735 */       serializeFields(bean, gen, provider);
/* 736 */       return;
/*     */     }
/* 738 */     int i = 0;
/*     */     try {
/* 740 */       for (int len = props.length; i < len; i++) {
/* 741 */         BeanPropertyWriter prop = props[i];
/* 742 */         if (prop != null) {
/* 743 */           filter.serializeAsField(bean, gen, provider, prop);
/*     */         }
/*     */       }
/* 746 */       if (this._anyGetterWriter != null) {
/* 747 */         this._anyGetterWriter.getAndFilter(bean, gen, provider, filter);
/*     */       }
/*     */     } catch (Exception e) {
/* 750 */       String name = i == props.length ? "[anySetter]" : props[i].getName();
/* 751 */       wrapAndThrow(provider, e, bean, name);
/*     */     }
/*     */     catch (StackOverflowError e)
/*     */     {
/* 755 */       JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", e);
/* 756 */       String name = i == props.length ? "[anySetter]" : props[i].getName();
/* 757 */       mapE.prependPath(new com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException.Reference(bean, name));
/* 758 */       throw mapE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public com.facebook.presto.jdbc.internal.jackson.databind.JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 767 */     ObjectNode o = createSchemaNode("object", true);
/*     */     
/*     */ 
/* 770 */     JsonSerializableSchema ann = (JsonSerializableSchema)this._handledType.getAnnotation(JsonSerializableSchema.class);
/* 771 */     if (ann != null) {
/* 772 */       String id = ann.id();
/* 773 */       if ((id != null) && (id.length() > 0)) {
/* 774 */         o.put("id", id);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 780 */     ObjectNode propertiesNode = o.objectNode();
/*     */     PropertyFilter filter;
/* 782 */     PropertyFilter filter; if (this._propertyFilterId != null) {
/* 783 */       filter = findPropertyFilter(provider, this._propertyFilterId, null);
/*     */     } else {
/* 785 */       filter = null;
/*     */     }
/*     */     
/* 788 */     for (int i = 0; i < this._props.length; i++) {
/* 789 */       BeanPropertyWriter prop = this._props[i];
/* 790 */       if (filter == null) {
/* 791 */         prop.depositSchemaProperty(propertiesNode, provider);
/*     */       } else {
/* 793 */         filter.depositSchemaProperty(prop, propertiesNode, provider);
/*     */       }
/*     */     }
/*     */     
/* 797 */     o.set("properties", propertiesNode);
/* 798 */     return o;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 806 */     if (visitor == null) {
/* 807 */       return;
/*     */     }
/* 809 */     com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
/* 810 */     if (objectVisitor == null) {
/* 811 */       return;
/*     */     }
/* 813 */     SerializerProvider provider = visitor.getProvider();
/* 814 */     if (this._propertyFilterId != null) {
/* 815 */       PropertyFilter filter = findPropertyFilter(visitor.getProvider(), this._propertyFilterId, null);
/*     */       
/* 817 */       int i = 0; for (int end = this._props.length; i < end; i++) {
/* 818 */         filter.depositSchemaProperty(this._props[i], objectVisitor, provider);
/*     */       }
/*     */     } else {
/* 821 */       Class<?> view = (this._filteredProps == null) || (provider == null) ? null : provider.getActiveView();
/*     */       BeanPropertyWriter[] props;
/*     */       BeanPropertyWriter[] props;
/* 824 */       if (view != null) {
/* 825 */         props = this._filteredProps;
/*     */       } else {
/* 827 */         props = this._props;
/*     */       }
/*     */       
/* 830 */       int i = 0; for (int end = props.length; i < end; i++) {
/* 831 */         BeanPropertyWriter prop = props[i];
/* 832 */         if (prop != null) {
/* 833 */           prop.depositSchemaProperty(objectVisitor, provider);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\BeanSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */