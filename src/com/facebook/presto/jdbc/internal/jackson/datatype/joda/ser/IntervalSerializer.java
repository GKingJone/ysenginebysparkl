/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Interval;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IntervalSerializer
/*    */   extends JodaDateSerializerBase<Interval>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 19 */   public IntervalSerializer() { this(FormatConfig.DEFAULT_DATETIME_PRINTER); }
/*    */   
/* 21 */   public IntervalSerializer(JacksonJodaDateFormat format) { super(Interval.class, format, false, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public IntervalSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 27 */     return this._format == formatter ? this : new IntervalSerializer(formatter);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider prov, Interval value)
/*    */   {
/* 32 */     return value.getStartMillis() == value.getEndMillis();
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(Interval interval, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/*    */     String repr;
/*    */     String repr;
/* 41 */     if (_useTimestamp(provider))
/*    */     {
/* 43 */       repr = interval.getStartMillis() + "-" + interval.getEndMillis();
/*    */     } else {
/* 45 */       DateTimeFormatter f = this._format.createFormatter(provider);
/* 46 */       repr = f.print(interval.getStart()) + "/" + f.print(interval.getEnd());
/*    */     }
/* 48 */     gen.writeString(repr);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\IntervalSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */