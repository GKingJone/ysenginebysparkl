/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalDateTimeDeserializer
/*     */   extends JSR310DateTimeDeserializerBase<LocalDateTime>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  44 */   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
/*     */   
/*  46 */   public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();
/*     */   
/*     */   private LocalDateTimeDeserializer() {
/*  49 */     this(DEFAULT_FORMATTER);
/*     */   }
/*     */   
/*     */   public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
/*  53 */     super(LocalDateTime.class, formatter);
/*     */   }
/*     */   
/*     */   protected JsonDeserializer<LocalDateTime> withDateFormat(DateTimeFormatter formatter)
/*     */   {
/*  58 */     return new LocalDateTimeDeserializer(formatter);
/*     */   }
/*     */   
/*     */   public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/*  64 */     if (parser.hasTokenId(6)) {
/*  65 */       String string = parser.getText().trim();
/*  66 */       if (string.length() == 0) {
/*  67 */         return null;
/*     */       }
/*     */       try
/*     */       {
/*  71 */         if (this._formatter == DEFAULT_FORMATTER)
/*     */         {
/*  73 */           if ((string.length() > 10) && (string.charAt(10) == 'T')) {
/*  74 */             if (string.endsWith("Z")) {
/*  75 */               return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
/*     */             }
/*  77 */             return LocalDateTime.parse(string, DEFAULT_FORMATTER);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*  82 */         return LocalDateTime.parse(string, this._formatter);
/*     */       } catch (DateTimeException e) {
/*  84 */         _rethrowDateTimeException(parser, context, e, string);
/*     */       }
/*     */     }
/*  87 */     if (parser.isExpectedStartArrayToken()) {
/*  88 */       if (parser.nextToken() == JsonToken.END_ARRAY) {
/*  89 */         return null;
/*     */       }
/*  91 */       int year = parser.getIntValue();
/*  92 */       int month = parser.nextIntValue(-1);
/*  93 */       int day = parser.nextIntValue(-1);
/*  94 */       int hour = parser.nextIntValue(-1);
/*  95 */       int minute = parser.nextIntValue(-1);
/*     */       
/*  97 */       if (parser.nextToken() != JsonToken.END_ARRAY) {
/*  98 */         int second = parser.getIntValue();
/*     */         
/* 100 */         if (parser.nextToken() != JsonToken.END_ARRAY) {
/* 101 */           int partialSecond = parser.getIntValue();
/* 102 */           if ((partialSecond < 1000) && 
/* 103 */             (!context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))) {
/* 104 */             partialSecond *= 1000000;
/*     */           }
/* 106 */           if (parser.nextToken() != JsonToken.END_ARRAY) {
/* 107 */             throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
/*     */           }
/* 109 */           return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
/*     */         }
/* 111 */         return LocalDateTime.of(year, month, day, hour, minute, second);
/*     */       }
/* 113 */       return LocalDateTime.of(year, month, day, hour, minute);
/*     */     }
/* 115 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 116 */       return (LocalDateTime)parser.getEmbeddedObject();
/*     */     }
/* 118 */     throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected array or string.");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\LocalDateTimeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */