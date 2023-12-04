/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
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
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
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
/*     */ abstract class JSR310FormattedSerializerBase<T>
/*     */   extends JSR310SerializerBase<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final Boolean _useTimestamp;
/*     */   protected final DateTimeFormatter _formatter;
/*     */   
/*     */   protected JSR310FormattedSerializerBase(Class<T> supportedType)
/*     */   {
/*  59 */     this(supportedType, null);
/*     */   }
/*     */   
/*     */   protected JSR310FormattedSerializerBase(Class<T> supportedType, DateTimeFormatter formatter)
/*     */   {
/*  64 */     super(supportedType);
/*  65 */     this._useTimestamp = null;
/*  66 */     this._formatter = formatter;
/*     */   }
/*     */   
/*     */ 
/*     */   protected JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base, Boolean useTimestamp, DateTimeFormatter dtf)
/*     */   {
/*  72 */     super(base.handledType());
/*  73 */     this._useTimestamp = useTimestamp;
/*  74 */     this._formatter = dtf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean paramBoolean, DateTimeFormatter paramDateTimeFormatter);
/*     */   
/*     */ 
/*     */ 
/*     */   protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId)
/*     */   {
/*  85 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  92 */     JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
/*  93 */     if (format != null) {
/*  94 */       Boolean useTimestamp = null;
/*     */       
/*     */ 
/*  97 */       JsonFormat.Shape shape = format.getShape();
/*  98 */       if ((shape == JsonFormat.Shape.ARRAY) || (shape.isNumeric())) {
/*  99 */         useTimestamp = Boolean.TRUE;
/*     */       } else {
/* 101 */         useTimestamp = shape == JsonFormat.Shape.STRING ? Boolean.FALSE : null;
/*     */       }
/* 103 */       DateTimeFormatter dtf = this._formatter;
/*     */       
/*     */ 
/* 106 */       if (format.hasPattern()) {
/* 107 */         String pattern = format.getPattern();
/* 108 */         Locale locale = format.hasLocale() ? format.getLocale() : prov.getLocale();
/* 109 */         if (locale == null) {
/* 110 */           dtf = DateTimeFormatter.ofPattern(pattern);
/*     */         } else {
/* 112 */           dtf = DateTimeFormatter.ofPattern(pattern, locale);
/*     */         }
/*     */         
/*     */ 
/* 116 */         if (format.hasTimeZone()) {
/* 117 */           dtf = dtf.withZone(format.getTimeZone().toZoneId());
/*     */         }
/*     */       }
/* 120 */       JSR310FormattedSerializerBase<?> ser = this;
/* 121 */       if ((useTimestamp != this._useTimestamp) || (dtf != this._formatter)) {
/* 122 */         ser = ser.withFormat(useTimestamp, dtf);
/*     */       }
/* 124 */       Boolean writeZoneId = format.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
/* 125 */       if (writeZoneId != null) {
/* 126 */         ser = ser.withFeatures(writeZoneId);
/*     */       }
/* 128 */       return ser;
/*     */     }
/* 130 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*     */   {
/* 136 */     return createSchemaNode(provider
/* 137 */       .isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 144 */     SerializerProvider provider = visitor.getProvider();
/* 145 */     boolean useTimestamp = (provider != null) && (useTimestamp(provider));
/* 146 */     if (useTimestamp) {
/* 147 */       _acceptTimestampVisitor(visitor, typeHint);
/*     */     } else {
/* 149 */       JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
/* 150 */       if (v2 != null) {
/* 151 */         v2.format(JsonValueFormat.DATE_TIME);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 159 */     JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/* 160 */     if (v2 != null) {
/* 161 */       v2.itemsFormat(JsonFormatTypes.INTEGER);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean useTimestamp(SerializerProvider provider) {
/* 166 */     if (this._useTimestamp != null) {
/* 167 */       return this._useTimestamp.booleanValue();
/*     */     }
/*     */     
/* 170 */     if (this._formatter != null) {
/* 171 */       return false;
/*     */     }
/* 173 */     return provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*     */   }
/*     */   
/*     */   protected boolean _useTimestampExplicitOnly(SerializerProvider provider) {
/* 177 */     if (this._useTimestamp != null) {
/* 178 */       return this._useTimestamp.booleanValue();
/*     */     }
/* 180 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\JSR310FormattedSerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */