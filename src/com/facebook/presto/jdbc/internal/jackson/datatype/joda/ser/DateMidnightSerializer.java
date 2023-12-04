/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateMidnight;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateMidnight.Property;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class DateMidnightSerializer
/*    */   extends JodaDateSerializerBase<DateMidnight>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateMidnightSerializer()
/*    */   {
/* 24 */     this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT);
/*    */   }
/*    */   
/* 27 */   public DateMidnightSerializer(JacksonJodaDateFormat format) { super(DateMidnight.class, format, true, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public DateMidnightSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 33 */     return this._format == formatter ? this : new DateMidnightSerializer(formatter);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider provider, DateMidnight value)
/*    */   {
/* 38 */     return value.getMillis() == 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(DateMidnight value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 45 */     if (_useTimestamp(provider))
/*    */     {
/* 47 */       gen.writeStartArray();
/* 48 */       gen.writeNumber(value.year().get());
/* 49 */       gen.writeNumber(value.monthOfYear().get());
/* 50 */       gen.writeNumber(value.dayOfMonth().get());
/* 51 */       gen.writeEndArray();
/*    */     } else {
/* 53 */       gen.writeString(this._format.createFormatterWithLocale(provider).print(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\DateMidnightSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */