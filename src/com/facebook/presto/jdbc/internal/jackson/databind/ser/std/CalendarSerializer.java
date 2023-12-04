/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import java.io.IOException;
/*    */ import java.text.DateFormat;
/*    */ import java.util.Calendar;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class CalendarSerializer
/*    */   extends DateTimeSerializerBase<Calendar>
/*    */ {
/* 21 */   public static final CalendarSerializer instance = new CalendarSerializer();
/*    */   
/* 23 */   public CalendarSerializer() { this(null, null); }
/*    */   
/*    */   public CalendarSerializer(Boolean useTimestamp, DateFormat customFormat) {
/* 26 */     super(Calendar.class, useTimestamp, customFormat);
/*    */   }
/*    */   
/*    */   public CalendarSerializer withFormat(Boolean timestamp, DateFormat customFormat)
/*    */   {
/* 31 */     return new CalendarSerializer(timestamp, customFormat);
/*    */   }
/*    */   
/*    */   protected long _timestamp(Calendar value)
/*    */   {
/* 36 */     return value == null ? 0L : value.getTimeInMillis();
/*    */   }
/*    */   
/*    */   public void serialize(Calendar value, JsonGenerator jgen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 42 */     if (_asTimestamp(provider)) {
/* 43 */       jgen.writeNumber(_timestamp(value));
/* 44 */     } else if (this._customFormat != null)
/*    */     {
/* 46 */       synchronized (this._customFormat)
/*    */       {
/* 48 */         jgen.writeString(this._customFormat.format(value.getTime()));
/*    */       }
/*    */     } else {
/* 51 */       provider.defaultSerializeDateValue(value.getTime(), jgen);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\CalendarSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */