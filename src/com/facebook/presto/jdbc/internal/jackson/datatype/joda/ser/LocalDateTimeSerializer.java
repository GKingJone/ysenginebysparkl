/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime.Property;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class LocalDateTimeSerializer
/*    */   extends JodaDateSerializerBase<LocalDateTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 18 */   public LocalDateTimeSerializer() { this(com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig.DEFAULT_LOCAL_DATETIME_PRINTER); }
/*    */   
/* 20 */   public LocalDateTimeSerializer(JacksonJodaDateFormat format) { super(LocalDateTime.class, format, true, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public LocalDateTimeSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 26 */     return this._format == formatter ? this : new LocalDateTimeSerializer(formatter);
/*    */   }
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
/*    */   public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 41 */     if (_useTimestamp(provider))
/*    */     {
/* 43 */       gen.writeStartArray();
/* 44 */       gen.writeNumber(value.year().get());
/* 45 */       gen.writeNumber(value.monthOfYear().get());
/* 46 */       gen.writeNumber(value.dayOfMonth().get());
/* 47 */       gen.writeNumber(value.hourOfDay().get());
/* 48 */       gen.writeNumber(value.minuteOfHour().get());
/* 49 */       gen.writeNumber(value.secondOfMinute().get());
/* 50 */       gen.writeNumber(value.millisOfSecond().get());
/* 51 */       gen.writeEndArray();
/*    */     } else {
/* 53 */       gen.writeString(this._format.createFormatter(provider).print(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\LocalDateTimeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */