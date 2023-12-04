/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ import java.time.OffsetTime;
/*    */ import java.time.ZoneOffset;
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
/*    */ public class OffsetTimeSerializer
/*    */   extends JSR310FormattedSerializerBase<OffsetTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 38 */   public static final OffsetTimeSerializer INSTANCE = new OffsetTimeSerializer();
/*    */   
/*    */   protected OffsetTimeSerializer() {
/* 41 */     super(OffsetTime.class);
/*    */   }
/*    */   
/*    */   protected OffsetTimeSerializer(OffsetTimeSerializer base, Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 46 */     super(base, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   protected OffsetTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf)
/*    */   {
/* 51 */     return new OffsetTimeSerializer(this, useTimestamp, dtf);
/*    */   }
/*    */   
/*    */   public void serialize(OffsetTime time, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 57 */     if (useTimestamp(provider)) {
/* 58 */       gen.writeStartArray();
/* 59 */       gen.writeNumber(time.getHour());
/* 60 */       gen.writeNumber(time.getMinute());
/* 61 */       int secs = time.getSecond();
/* 62 */       int nanos = time.getNano();
/* 63 */       if ((secs > 0) || (nanos > 0))
/*    */       {
/* 65 */         gen.writeNumber(secs);
/* 66 */         if (nanos > 0)
/*    */         {
/* 68 */           if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/* 69 */             gen.writeNumber(nanos);
/*    */           } else
/* 71 */             gen.writeNumber(time.get(ChronoField.MILLI_OF_SECOND));
/*    */         }
/*    */       }
/* 74 */       gen.writeString(time.getOffset().toString());
/* 75 */       gen.writeEndArray();
/*    */     } else {
/* 77 */       String str = this._formatter == null ? time.toString() : time.format(this._formatter);
/* 78 */       gen.writeString(str);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\OffsetTimeSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */