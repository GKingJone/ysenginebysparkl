/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Instant;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InstantSerializer
/*    */   extends JodaDateSerializerBase<Instant>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 19 */   public InstantSerializer() { this(FormatConfig.DEFAULT_TIMEONLY_FORMAT); }
/*    */   
/* 21 */   public InstantSerializer(JacksonJodaDateFormat format) { super(Instant.class, format, false, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); }
/*    */   
/*    */ 
/*    */ 
/*    */   public InstantSerializer withFormat(JacksonJodaDateFormat formatter)
/*    */   {
/* 27 */     return this._format == formatter ? this : new InstantSerializer(formatter);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isEmpty(SerializerProvider prov, Instant value)
/*    */   {
/* 33 */     return value.getMillis() == 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 40 */     if (_useTimestamp(provider)) {
/* 41 */       gen.writeNumber(value.getMillis());
/*    */     } else {
/* 43 */       gen.writeString(value.toString());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\InstantSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */