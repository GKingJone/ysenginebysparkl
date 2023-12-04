/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.time.DateTimeException;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
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
/*     */ public class LocalDateDeserializer
/*     */   extends JSR310DateTimeDeserializerBase<LocalDate>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  41 */   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
/*     */   
/*  43 */   public static final LocalDateDeserializer INSTANCE = new LocalDateDeserializer();
/*     */   
/*     */   private LocalDateDeserializer() {
/*  46 */     this(DEFAULT_FORMATTER);
/*     */   }
/*     */   
/*     */   public LocalDateDeserializer(DateTimeFormatter dtf) {
/*  50 */     super(LocalDate.class, dtf);
/*     */   }
/*     */   
/*     */   protected JsonDeserializer<LocalDate> withDateFormat(DateTimeFormatter dtf)
/*     */   {
/*  55 */     return new LocalDateDeserializer(dtf);
/*     */   }
/*     */   
/*     */   public LocalDate deserialize(JsonParser parser, DeserializationContext context)
/*     */     throws IOException
/*     */   {
/*  61 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/*  62 */       String string = parser.getText().trim();
/*  63 */       if (string.length() == 0) {
/*  64 */         return null;
/*     */       }
/*     */       
/*     */ 
/*  68 */       DateTimeFormatter format = this._formatter;
/*     */       try {
/*  70 */         if (format == DEFAULT_FORMATTER)
/*     */         {
/*  72 */           if ((string.length() > 10) && (string.charAt(10) == 'T')) {
/*  73 */             if (string.endsWith("Z")) {
/*  74 */               return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC).toLocalDate();
/*     */             }
/*  76 */             return LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
/*     */           }
/*     */         }
/*     */         
/*  80 */         return LocalDate.parse(string, format);
/*     */       } catch (DateTimeException e) {
/*  82 */         _rethrowDateTimeException(parser, context, e, string);
/*     */       }
/*     */     }
/*  85 */     if (parser.isExpectedStartArrayToken()) {
/*  86 */       if (parser.nextToken() == JsonToken.END_ARRAY) {
/*  87 */         return null;
/*     */       }
/*  89 */       int year = parser.getIntValue();
/*  90 */       int month = parser.nextIntValue(-1);
/*  91 */       int day = parser.nextIntValue(-1);
/*     */       
/*  93 */       if (parser.nextToken() != JsonToken.END_ARRAY) {
/*  94 */         throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
/*     */       }
/*  96 */       return LocalDate.of(year, month, day);
/*     */     }
/*  98 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/*  99 */       return (LocalDate)parser.getEmbeddedObject();
/*     */     }
/* 101 */     throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected array or string.");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\LocalDateDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */