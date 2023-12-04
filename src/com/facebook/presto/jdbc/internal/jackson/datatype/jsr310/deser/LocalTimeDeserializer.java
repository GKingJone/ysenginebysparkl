/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.LocalTime;
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
/*     */ public class LocalTimeDeserializer
/*     */   extends JSR310DateTimeDeserializerBase<LocalTime>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  40 */   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
/*     */   
/*  42 */   public static final LocalTimeDeserializer INSTANCE = new LocalTimeDeserializer();
/*     */   
/*     */   private LocalTimeDeserializer() {
/*  45 */     this(DEFAULT_FORMATTER);
/*     */   }
/*     */   
/*     */   public LocalTimeDeserializer(DateTimeFormatter formatter) {
/*  49 */     super(LocalTime.class, formatter);
/*     */   }
/*     */   
/*     */   protected JsonDeserializer<LocalTime> withDateFormat(DateTimeFormatter formatter)
/*     */   {
/*  54 */     return new LocalTimeDeserializer(formatter);
/*     */   }
/*     */   
/*     */   public LocalTime deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/*  60 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/*  61 */       String string = parser.getText().trim();
/*  62 */       if (string.length() == 0) {
/*  63 */         return null;
/*     */       }
/*  65 */       DateTimeFormatter format = this._formatter;
/*     */       try {
/*  67 */         if ((format == DEFAULT_FORMATTER) && 
/*  68 */           (string.contains("T"))) {
/*  69 */           return LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
/*     */         }
/*     */         
/*  72 */         return LocalTime.parse(string, format);
/*     */       } catch (DateTimeException e) {
/*  74 */         _rethrowDateTimeException(parser, context, e, string);
/*     */       }
/*     */     }
/*  77 */     if (parser.isExpectedStartArrayToken()) {
/*  78 */       if (parser.nextToken() == JsonToken.END_ARRAY) {
/*  79 */         return null;
/*     */       }
/*  81 */       int hour = parser.getIntValue();
/*     */       
/*  83 */       parser.nextToken();
/*  84 */       int minute = parser.getIntValue();
/*     */       
/*  86 */       if (parser.nextToken() != JsonToken.END_ARRAY)
/*     */       {
/*  88 */         int second = parser.getIntValue();
/*     */         
/*  90 */         if (parser.nextToken() != JsonToken.END_ARRAY)
/*     */         {
/*  92 */           int partialSecond = parser.getIntValue();
/*  93 */           if ((partialSecond < 1000) && 
/*  94 */             (!context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))) {
/*  95 */             partialSecond *= 1000000;
/*     */           }
/*  97 */           if (parser.nextToken() != JsonToken.END_ARRAY) {
/*  98 */             throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
/*     */           }
/* 100 */           return LocalTime.of(hour, minute, second, partialSecond);
/*     */         }
/*     */         
/* 103 */         return LocalTime.of(hour, minute, second);
/*     */       }
/* 105 */       return LocalTime.of(hour, minute);
/*     */     }
/* 107 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 108 */       return (LocalTime)parser.getEmbeddedObject();
/*     */     }
/* 110 */     throw context.wrongTokenException(parser, JsonToken.START_ARRAY, "Expected array or string.");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\LocalTimeDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */