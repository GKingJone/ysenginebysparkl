/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate.Property;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class LocalDateSerializer
/*    */   extends JodaDateSerializerBase<LocalDate>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 19 */   public LocalDateSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT); }
/*    */   
/* 21 */   public LocalDateSerializer(JacksonJodaDateFormat format) { super(LocalDate.class, format, true, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public LocalDateSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 27 */     return this._format == formatter ? this : new LocalDateSerializer(formatter);
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
/*    */   public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 41 */     if (_useTimestamp(provider))
/*    */     {
/* 43 */       gen.writeStartArray();
/* 44 */       gen.writeNumber(value.year().get());
/* 45 */       gen.writeNumber(value.monthOfYear().get());
/* 46 */       gen.writeNumber(value.dayOfMonth().get());
/* 47 */       gen.writeEndArray();
/*    */     } else {
/* 49 */       gen.writeString(this._format.createFormatter(provider).print(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\LocalDateSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */