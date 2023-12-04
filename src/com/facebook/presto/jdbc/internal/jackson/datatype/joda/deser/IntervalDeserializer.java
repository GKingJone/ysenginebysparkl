/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Interval;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class IntervalDeserializer
/*    */   extends JodaDateDeserializerBase<Interval>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public IntervalDeserializer()
/*    */   {
/* 21 */     this(FormatConfig.DEFAULT_DATETIME_PARSER);
/*    */   }
/*    */   
/*    */   public IntervalDeserializer(JacksonJodaDateFormat format) {
/* 25 */     super(Interval.class, format);
/*    */   }
/*    */   
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 30 */     return new IntervalDeserializer(format);
/*    */   }
/*    */   
/*    */   public Interval deserialize(JsonParser jsonParser, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 36 */     String v = jsonParser.getText().trim();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 41 */     int index = v.indexOf('/', 1);
/* 42 */     boolean hasSlash = index > 0;
/* 43 */     if (!hasSlash) {
/* 44 */       index = v.indexOf('-', 1);
/*    */     }
/* 46 */     if (index < 0) {
/* 47 */       throw ctxt.weirdStringException(v, handledType(), "no slash or hyphen found to separate start, end");
/*    */     }
/*    */     
/* 50 */     String str = v.substring(0, index);
/*    */     Interval result;
/*    */     try
/*    */     {
/*    */       Interval result;
/* 55 */       if (hasSlash) {
/* 56 */         result = Interval.parse(v);
/*    */       } else {
/* 58 */         long start = Long.valueOf(str).longValue();
/* 59 */         str = v.substring(index + 1);
/* 60 */         long end = Long.valueOf(str).longValue();
/* 61 */         result = new Interval(start, end);
/*    */       }
/*    */     } catch (NumberFormatException e) {
/* 64 */       throw JsonMappingException.from(jsonParser, "Failed to parse number from '" + str + "' (full source String '" + v + "') to construct " + handledType().getName());
/*    */     }
/*    */     
/*    */ 
/* 68 */     DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
/* 69 */     if ((tz != null) && 
/* 70 */       (!tz.equals(result.getStart().getZone()))) {
/* 71 */       result = new Interval(result.getStartMillis(), result.getEndMillis(), tz);
/*    */     }
/*    */     
/* 74 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\IntervalDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */