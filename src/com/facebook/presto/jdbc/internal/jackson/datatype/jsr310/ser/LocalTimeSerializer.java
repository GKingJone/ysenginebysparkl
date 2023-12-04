/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ import java.time.LocalTime;
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
/*    */ public class LocalTimeSerializer
/*    */   extends JSR310FormattedSerializerBase<LocalTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 38 */   public static final LocalTimeSerializer INSTANCE = new LocalTimeSerializer();
/*    */   
/*    */   protected LocalTimeSerializer() {
/* 41 */     this(null);
/*    */   }
/*    */   
/*    */   public LocalTimeSerializer(DateTimeFormatter formatter) {
/* 45 */     super(LocalTime.class, formatter);
/*    */   }
/*    */   
/*    */   protected LocalTimeSerializer(LocalTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
/* 49 */     super(base, useTimestamp, formatter);
/*    */   }
/*    */   
/*    */   protected JSR310FormattedSerializerBase<LocalTime> withFormat(Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 54 */     return new LocalTimeSerializer(this, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   public void serialize(LocalTime value, JsonGenerator g, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 60 */     if (useTimestamp(provider)) {
/* 61 */       g.writeStartArray();
/* 62 */       g.writeNumber(value.getHour());
/* 63 */       g.writeNumber(value.getMinute());
/* 64 */       if ((value.getSecond() > 0) || (value.getNano() > 0))
/*    */       {
/* 66 */         g.writeNumber(value.getSecond());
/* 67 */         if (value.getNano() > 0)
/*    */         {
/* 69 */           if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/* 70 */             g.writeNumber(value.getNano());
/*    */           } else
/* 72 */             g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
/*    */         }
/*    */       }
/* 75 */       g.writeEndArray();
/*    */     } else {
/* 77 */       DateTimeFormatter dtf = this._formatter;
/* 78 */       if (dtf == null) {
/* 79 */         dtf = _defaultFormatter();
/*    */       }
/* 81 */       g.writeString(value.format(dtf));
/*    */     }
/*    */   }
/*    */   
/*    */   protected DateTimeFormatter _defaultFormatter()
/*    */   {
/* 87 */     return DateTimeFormatter.ISO_LOCAL_TIME;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\LocalTimeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */