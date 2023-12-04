/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.format.DateTimeFormatter;
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
/*     */ public class OffsetTimeDeserializer
/*     */   extends JSR310DateTimeDeserializerBase<OffsetTime>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  37 */   public static final OffsetTimeDeserializer INSTANCE = new OffsetTimeDeserializer();
/*     */   
/*     */   private OffsetTimeDeserializer() {
/*  40 */     this(DateTimeFormatter.ISO_OFFSET_TIME);
/*     */   }
/*     */   
/*     */   protected OffsetTimeDeserializer(DateTimeFormatter dtf) {
/*  44 */     super(OffsetTime.class, dtf);
/*     */   }
/*     */   
/*     */   protected JsonDeserializer<OffsetTime> withDateFormat(DateTimeFormatter dtf)
/*     */   {
/*  49 */     return new OffsetTimeDeserializer(dtf);
/*     */   }
/*     */   
/*     */   public OffsetTime deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/*  55 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/*  56 */       String string = parser.getText().trim();
/*  57 */       if (string.length() == 0) {
/*  58 */         return null;
/*     */       }
/*     */       try {
/*  61 */         return OffsetTime.parse(string, this._formatter);
/*     */       } catch (DateTimeException e) {
/*  63 */         _rethrowDateTimeException(parser, context, e, string);
/*     */       }
/*     */     }
/*  66 */     if (!parser.isExpectedStartArrayToken()) {
/*  67 */       if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/*  68 */         return (OffsetTime)parser.getEmbeddedObject();
/*     */       }
/*  70 */       throw context.wrongTokenException(parser, JsonToken.START_ARRAY, "Expected array or string.");
/*     */     }
/*  72 */     int hour = parser.nextIntValue(-1);
/*  73 */     if (hour == -1) {
/*  74 */       JsonToken t = parser.getCurrentToken();
/*  75 */       if (t == JsonToken.END_ARRAY) {
/*  76 */         return null;
/*     */       }
/*  78 */       if (t != JsonToken.VALUE_NUMBER_INT) {
/*  79 */         _reportWrongToken(parser, context, JsonToken.VALUE_NUMBER_INT, "hours");
/*     */       }
/*  81 */       hour = parser.getIntValue();
/*     */     }
/*  83 */     int minute = parser.nextIntValue(-1);
/*  84 */     if (minute == -1) {
/*  85 */       JsonToken t = parser.getCurrentToken();
/*  86 */       if (t == JsonToken.END_ARRAY) {
/*  87 */         return null;
/*     */       }
/*  89 */       if (t != JsonToken.VALUE_NUMBER_INT) {
/*  90 */         _reportWrongToken(parser, context, JsonToken.VALUE_NUMBER_INT, "minutes");
/*     */       }
/*  92 */       minute = parser.getIntValue();
/*     */     }
/*  94 */     int partialSecond = 0;
/*  95 */     int second = 0;
/*  96 */     if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
/*  97 */       second = parser.getIntValue();
/*  98 */       if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
/*  99 */         partialSecond = parser.getIntValue();
/* 100 */         if ((partialSecond < 1000) && 
/* 101 */           (!context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))) {
/* 102 */           partialSecond *= 1000000;
/*     */         }
/* 104 */         parser.nextToken();
/*     */       }
/*     */     }
/* 107 */     if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
/* 108 */       OffsetTime t = OffsetTime.of(hour, minute, second, partialSecond, ZoneOffset.of(parser.getText()));
/* 109 */       if (parser.nextToken() != JsonToken.END_ARRAY) {
/* 110 */         _reportWrongToken(parser, context, JsonToken.END_ARRAY, "timezone");
/*     */       }
/* 112 */       return t;
/*     */     }
/* 114 */     throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected string for TimeZone after numeric values");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\OffsetTimeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */