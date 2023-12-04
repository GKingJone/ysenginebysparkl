/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.time.temporal.ChronoField;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocalDateTimeSerializer
/*    */   extends JSR310FormattedSerializerBase<LocalDateTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 38 */   public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();
/*    */   
/*    */   protected LocalDateTimeSerializer() {
/* 41 */     this(null);
/*    */   }
/*    */   
/*    */   public LocalDateTimeSerializer(DateTimeFormatter f) {
/* 45 */     super(LocalDateTime.class, f);
/*    */   }
/*    */   
/*    */   private LocalDateTimeSerializer(LocalDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter f) {
/* 49 */     super(base, useTimestamp, f);
/*    */   }
/*    */   
/*    */   protected JSR310FormattedSerializerBase<LocalDateTime> withFormat(Boolean useTimestamp, DateTimeFormatter f)
/*    */   {
/* 54 */     return new LocalDateTimeSerializer(this, useTimestamp, f);
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 61 */     if (useTimestamp(provider)) {
/* 62 */       g.writeStartArray();
/* 63 */       g.writeNumber(value.getYear());
/* 64 */       g.writeNumber(value.getMonthValue());
/* 65 */       g.writeNumber(value.getDayOfMonth());
/* 66 */       g.writeNumber(value.getHour());
/* 67 */       g.writeNumber(value.getMinute());
/* 68 */       if ((value.getSecond() > 0) || (value.getNano() > 0)) {
/* 69 */         g.writeNumber(value.getSecond());
/* 70 */         if (value.getNano() > 0) {
/* 71 */           if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/* 72 */             g.writeNumber(value.getNano());
/*    */           } else
/* 74 */             g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
/*    */         }
/*    */       }
/* 77 */       g.writeEndArray();
/*    */     } else {
/* 79 */       DateTimeFormatter dtf = this._formatter;
/* 80 */       if (dtf == null) {
/* 81 */         dtf = _defaultFormatter();
/*    */       }
/* 83 */       g.writeString(value.format(dtf));
/*    */     }
/*    */   }
/*    */   
/*    */   protected DateTimeFormatter _defaultFormatter()
/*    */   {
/* 89 */     return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\LocalDateTimeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */