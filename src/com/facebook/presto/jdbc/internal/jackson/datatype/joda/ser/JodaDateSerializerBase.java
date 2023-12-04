/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ public abstract class JodaDateSerializerBase<T> extends JodaSerializerBase<T> implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JacksonJodaDateFormat _format;
/*     */   protected final boolean _usesArrays;
/*     */   protected final SerializationFeature _featureForNumeric;
/*     */   
/*     */   protected JodaDateSerializerBase(Class<T> type, JacksonJodaDateFormat format, boolean usesArrays, SerializationFeature numericFeature)
/*     */   {
/*  32 */     super(type);
/*  33 */     this._format = format;
/*  34 */     this._usesArrays = usesArrays;
/*  35 */     this._featureForNumeric = numericFeature;
/*     */   }
/*     */   
/*     */   public abstract JodaDateSerializerBase<T> withFormat(JacksonJodaDateFormat paramJacksonJodaDateFormat);
/*     */   
/*     */   public boolean isEmpty(SerializerProvider prov, T value)
/*     */   {
/*  42 */     return value == null;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  49 */     JsonFormat.Value ann = findFormatOverrides(prov, property, handledType());
/*  50 */     if (ann != null) {
/*  51 */       JacksonJodaDateFormat format = this._format;
/*     */       
/*     */       Boolean useTimestamp;
/*     */       
/*     */       Boolean useTimestamp;
/*  56 */       if (ann.getShape().isNumeric()) {
/*  57 */         useTimestamp = Boolean.TRUE; } else { Boolean useTimestamp;
/*  58 */         if (ann.getShape() == JsonFormat.Shape.STRING) {
/*  59 */           useTimestamp = Boolean.FALSE; } else { Boolean useTimestamp;
/*  60 */           if (ann.getShape() == JsonFormat.Shape.ARRAY)
/*     */           {
/*  62 */             useTimestamp = Boolean.TRUE;
/*     */           } else
/*  64 */             useTimestamp = null;
/*     */         }
/*     */       }
/*  67 */       if (useTimestamp != null) {
/*  68 */         format = format.withUseTimestamp(useTimestamp);
/*     */       }
/*  70 */       format = format.with(ann);
/*  71 */       if (format != this._format) {
/*  72 */         return withFormat(format);
/*     */       }
/*     */     }
/*  75 */     return this;
/*     */   }
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */   {
/*  80 */     if (_useTimestamp(provider)) {
/*  81 */       return createSchemaNode(this._usesArrays ? "array" : "number", true);
/*     */     }
/*  83 */     return createSchemaNode("string", true);
/*     */   }
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/*  89 */     _acceptJsonFormatVisitor(visitor, typeHint, _useTimestamp(visitor.getProvider()));
/*     */   }
/*     */   
/*     */   protected void _acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint, boolean asNumber)
/*     */     throws JsonMappingException
/*     */   {
/*  95 */     if (asNumber) {
/*  96 */       if (this._usesArrays) {
/*  97 */         JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/*  98 */         if (v2 != null) {
/*  99 */           v2.itemsFormat(JsonFormatTypes.INTEGER);
/*     */         }
/*     */       } else {
/* 102 */         JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
/* 103 */         if (v2 != null) {
/* 104 */           v2.numberType(JsonParser.NumberType.LONG);
/* 105 */           v2.format(JsonValueFormat.UTC_MILLISEC);
/*     */         }
/*     */       }
/*     */     } else {
/* 109 */       JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/* 110 */       if (v2 != null) {
/* 111 */         v2.format(JsonValueFormat.DATE_TIME);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _useTimestamp(SerializerProvider provider)
/*     */   {
/* 123 */     return this._format.useTimestamp(provider, this._featureForNumeric);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean writeWithZoneId(SerializerProvider provider)
/*     */   {
/* 130 */     return this._format.shouldWriteWithZoneId(provider);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\JodaDateSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */