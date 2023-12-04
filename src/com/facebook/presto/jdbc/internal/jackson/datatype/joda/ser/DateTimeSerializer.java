/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DateTimeSerializer extends JodaDateSerializerBase<DateTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateTimeSerializer()
/*    */   {
/* 18 */     this(FormatConfig.DEFAULT_DATETIME_PRINTER);
/*    */   }
/*    */   
/* 21 */   public DateTimeSerializer(JacksonJodaDateFormat format) { super(DateTime.class, format, false, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public DateTimeSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 27 */     return this._format == formatter ? this : new DateTimeSerializer(formatter);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider prov, DateTime value)
/*    */   {
/* 32 */     return value.getMillis() == 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 39 */     if (!writeWithZoneId(provider)) {
/* 40 */       if (_useTimestamp(provider)) {
/* 41 */         gen.writeNumber(value.getMillis());
/*    */       } else {
/* 43 */         gen.writeString(this._format.createFormatter(provider).print(value));
/*    */       }
/*    */     }
/*    */     else {
/* 47 */       if (_useTimestamp(provider))
/*    */       {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 57 */         gen.writeNumber(value.getMillis());
/* 58 */         return;
/*    */       }
/* 60 */       StringBuilder sb = new StringBuilder(40).append(this._format.createFormatter(provider).withOffsetParsed().print(value));
/*    */       
/* 62 */       sb = sb.append('[').append(value.getZone()).append(']');
/*    */       
/*    */ 
/* 65 */       gen.writeString(sb.toString());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\DateTimeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */