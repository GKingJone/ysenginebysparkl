/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContextualSerializer;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
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
/*     */ public abstract class DateTimeSerializerBase<T>
/*     */   extends StdScalarSerializer<T>
/*     */   implements ContextualSerializer
/*     */ {
/*     */   protected final Boolean _useTimestamp;
/*     */   protected final DateFormat _customFormat;
/*     */   
/*     */   protected DateTimeSerializerBase(Class<T> type, Boolean useTimestamp, DateFormat customFormat)
/*     */   {
/*  41 */     super(type);
/*  42 */     this._useTimestamp = useTimestamp;
/*  43 */     this._customFormat = customFormat;
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract DateTimeSerializerBase<T> withFormat(Boolean paramBoolean, DateFormat paramDateFormat);
/*     */   
/*     */   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/*  52 */     if (property != null) {
/*  53 */       JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
/*  54 */       if (format != null)
/*     */       {
/*  56 */         JsonFormat.Shape shape = format.getShape();
/*  57 */         if (shape.isNumeric()) {
/*  58 */           return withFormat(Boolean.TRUE, null);
/*     */         }
/*  60 */         if ((shape == JsonFormat.Shape.STRING) || (format.hasPattern()) || (format.hasLocale()) || (format.hasTimeZone()))
/*     */         {
/*  62 */           TimeZone tz = format.getTimeZone();
/*  63 */           String pattern = format.hasPattern() ? format.getPattern() : "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
/*     */           
/*     */ 
/*  66 */           Locale loc = format.hasLocale() ? format.getLocale() : serializers.getLocale();
/*     */           
/*     */ 
/*  69 */           SimpleDateFormat df = new SimpleDateFormat(pattern, loc);
/*  70 */           if (tz == null) {
/*  71 */             tz = serializers.getTimeZone();
/*     */           }
/*  73 */           df.setTimeZone(tz);
/*  74 */           return withFormat(Boolean.FALSE, df);
/*     */         }
/*     */       }
/*     */     }
/*  78 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isEmpty(T value)
/*     */   {
/*  91 */     return (value == null) || (_timestamp(value) == 0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty(SerializerProvider serializers, T value)
/*     */   {
/*  97 */     return (value == null) || (_timestamp(value) == 0L);
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract long _timestamp(T paramT);
/*     */   
/*     */   public JsonNode getSchema(SerializerProvider serializers, Type typeHint)
/*     */   {
/* 105 */     return createSchemaNode(_asTimestamp(serializers) ? "number" : "string", true);
/*     */   }
/*     */   
/*     */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 111 */     _acceptJsonFormatVisitor(visitor, typeHint, _asTimestamp(visitor.getProvider()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void serialize(T paramT, JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _asTimestamp(SerializerProvider serializers)
/*     */   {
/* 132 */     if (this._useTimestamp != null) {
/* 133 */       return this._useTimestamp.booleanValue();
/*     */     }
/* 135 */     if (this._customFormat == null) {
/* 136 */       if (serializers != null) {
/* 137 */         return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*     */       }
/*     */       
/* 140 */       throw new IllegalArgumentException("Null SerializerProvider passed for " + handledType().getName());
/*     */     }
/* 142 */     return false;
/*     */   }
/*     */   
/*     */   protected void _acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint, boolean asNumber)
/*     */     throws JsonMappingException
/*     */   {
/* 148 */     if (asNumber) {
/* 149 */       visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG, JsonValueFormat.UTC_MILLISEC);
/*     */     }
/*     */     else {
/* 152 */       visitStringFormat(visitor, typeHint, JsonValueFormat.DATE_TIME);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\DateTimeSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */