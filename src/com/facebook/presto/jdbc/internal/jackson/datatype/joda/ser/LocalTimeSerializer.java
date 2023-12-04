/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime.Property;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class LocalTimeSerializer
/*    */   extends JodaDateSerializerBase<LocalTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 18 */   public LocalTimeSerializer() { this(com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig.DEFAULT_LOCAL_TIMEONLY_PRINTER); }
/*    */   
/* 20 */   public LocalTimeSerializer(JacksonJodaDateFormat format) { super(LocalTime.class, format, true, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 26 */     return this._format == formatter ? this : new LocalTimeSerializer(formatter);
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
/*    */   public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 40 */     if (_useTimestamp(provider))
/*    */     {
/* 42 */       gen.writeStartArray();
/* 43 */       gen.writeNumber(value.hourOfDay().get());
/* 44 */       gen.writeNumber(value.minuteOfHour().get());
/* 45 */       gen.writeNumber(value.secondOfMinute().get());
/* 46 */       gen.writeNumber(value.millisOfSecond().get());
/* 47 */       gen.writeEndArray();
/*    */     } else {
/* 49 */       gen.writeString(this._format.createFormatter(provider).print(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\LocalTimeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */