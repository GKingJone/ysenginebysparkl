/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DurationSerializer
/*    */   extends JodaDateSerializerBase<Duration>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DurationSerializer()
/*    */   {
/* 25 */     this(FormatConfig.DEFAULT_DATEONLY_FORMAT);
/*    */   }
/*    */   
/* 28 */   public DurationSerializer(JacksonJodaDateFormat formatter) { super(Duration.class, formatter, false, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public DurationSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 34 */     return this._format == formatter ? this : new DurationSerializer(formatter);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isEmpty(SerializerProvider prov, Duration value)
/*    */   {
/* 40 */     return value.getMillis() == 0L;
/*    */   }
/*    */   
/*    */   public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 46 */     if (_useTimestamp(provider)) {
/* 47 */       gen.writeNumber(value.getMillis());
/*    */     } else {
/* 49 */       gen.writeString(value.toString());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\DurationSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */