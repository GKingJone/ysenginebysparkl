/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.DecimalUtils;
/*     */ import java.io.IOException;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.temporal.Temporal;
/*     */ import java.util.function.ToIntFunction;
/*     */ import java.util.function.ToLongFunction;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class InstantSerializerBase<T extends Temporal>
/*     */   extends JSR310FormattedSerializerBase<T>
/*     */ {
/*     */   private final DateTimeFormatter defaultFormat;
/*     */   private final ToLongFunction<T> getEpochMillis;
/*     */   private final ToLongFunction<T> getEpochSeconds;
/*     */   private final ToIntFunction<T> getNanoseconds;
/*     */   
/*     */   protected InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis, ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds, DateTimeFormatter formatter)
/*     */   {
/*  57 */     super(supportedType, null);
/*  58 */     this.defaultFormat = formatter;
/*  59 */     this.getEpochMillis = getEpochMillis;
/*  60 */     this.getEpochSeconds = getEpochSeconds;
/*  61 */     this.getNanoseconds = getNanoseconds;
/*     */   }
/*     */   
/*     */ 
/*     */   protected InstantSerializerBase(InstantSerializerBase<T> base, Boolean useTimestamp, DateTimeFormatter dtf)
/*     */   {
/*  67 */     super(base, useTimestamp, dtf);
/*  68 */     this.defaultFormat = base.defaultFormat;
/*  69 */     this.getEpochMillis = base.getEpochMillis;
/*  70 */     this.getEpochSeconds = base.getEpochSeconds;
/*  71 */     this.getNanoseconds = base.getNanoseconds;
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean paramBoolean, DateTimeFormatter paramDateTimeFormatter);
/*     */   
/*     */ 
/*     */   public void serialize(T value, JsonGenerator generator, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  81 */     if (useTimestamp(provider)) {
/*  82 */       if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/*  83 */         generator.writeNumber(DecimalUtils.toBigDecimal(this.getEpochSeconds
/*  84 */           .applyAsLong(value), this.getNanoseconds.applyAsInt(value)));
/*     */         
/*  86 */         return;
/*     */       }
/*  88 */       generator.writeNumber(this.getEpochMillis.applyAsLong(value)); return;
/*     */     }
/*     */     
/*     */     String str;
/*     */     String str;
/*  93 */     if (this._formatter != null) {
/*  94 */       str = this._formatter.format(value); } else { String str;
/*  95 */       if (this.defaultFormat != null) {
/*  96 */         str = this.defaultFormat.format(value);
/*     */       } else
/*  98 */         str = value.toString();
/*     */     }
/* 100 */     generator.writeString(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*     */     throws JsonMappingException
/*     */   {
/* 108 */     SerializerProvider prov = visitor.getProvider();
/* 109 */     if ((prov != null) && 
/* 110 */       (prov.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS))) {
/* 111 */       JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
/* 112 */       if (v2 != null) {
/* 113 */         v2.numberType(JsonParser.NumberType.BIG_DECIMAL);
/*     */       }
/*     */     } else {
/* 116 */       JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
/* 117 */       if (v2 != null) {
/* 118 */         v2.numberType(JsonParser.NumberType.LONG);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\InstantSerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */