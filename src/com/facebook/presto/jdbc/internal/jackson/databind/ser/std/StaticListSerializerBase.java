/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StaticListSerializerBase<T extends Collection<?>>
/*     */   extends StdSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final JsonSerializer<String> _serializer;
/*     */   protected final Boolean _unwrapSingle;
/*     */   
/*     */   protected StaticListSerializerBase(Class<?> cls)
/*     */   {
/*  34 */     super(cls, false);
/*  35 */     this._serializer = null;
/*  36 */     this._unwrapSingle = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected StaticListSerializerBase(StaticListSerializerBase<?> src, JsonSerializer<?> ser, Boolean unwrapSingle)
/*     */   {
/*  45 */     super(src);
/*  46 */     this._serializer = ser;
/*  47 */     this._unwrapSingle = unwrapSingle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract JsonSerializer<?> _withResolved(BeanProperty paramBeanProperty, JsonSerializer<?> paramJsonSerializer, Boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  67 */     JsonSerializer<?> ser = null;
/*  68 */     Boolean unwrapSingle = null;
/*     */     
/*  70 */     if (property != null) {
/*  71 */       AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
/*  72 */       AnnotatedMember m = property.getMember();
/*  73 */       if (m != null) {
/*  74 */         Object serDef = intr.findContentSerializer(m);
/*  75 */         if (serDef != null) {
/*  76 */           ser = serializers.serializerInstance(m, serDef);
/*     */         }
/*     */       }
/*     */     }
/*  80 */     JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/*  81 */     if (format != null) {
/*  82 */       unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
/*     */     }
/*  84 */     if (ser == null) {
/*  85 */       ser = this._serializer;
/*     */     }
/*     */     
/*  88 */     ser = findConvertingContentSerializer(serializers, property, ser);
/*  89 */     if (ser == null) {
/*  90 */       ser = serializers.findValueSerializer(String.class, property);
/*     */     } else {
/*  92 */       ser = serializers.handleSecondaryContextualization(ser, property);
/*     */     }
/*     */     
/*  95 */     if (isDefaultSerializer(ser)) {
/*  96 */       ser = null;
/*     */     }
/*     */     
/*  99 */     if ((ser == this._serializer) && (unwrapSingle == this._unwrapSingle)) {
/* 100 */       return this;
/*     */     }
/* 102 */     return _withResolved(property, ser, unwrapSingle);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isEmpty(T value)
/*     */   {
/* 108 */     return isEmpty(null, value);
/*     */   }
/*     */   
/*     */   public boolean isEmpty(SerializerProvider provider, T value)
/*     */   {
/* 113 */     return (value == null) || (value.size() == 0);
/*     */   }
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */   {
/* 118 */     return createSchemaNode("array", true).set("items", contentSchema());
/*     */   }
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
/*     */   {
/* 123 */     acceptContentVisitor(visitor.expectArrayFormat(typeHint));
/*     */   }
/*     */   
/*     */   protected abstract JsonNode contentSchema();
/*     */   
/*     */   protected abstract void acceptContentVisitor(JsonArrayFormatVisitor paramJsonArrayFormatVisitor)
/*     */     throws JsonMappingException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\StaticListSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */