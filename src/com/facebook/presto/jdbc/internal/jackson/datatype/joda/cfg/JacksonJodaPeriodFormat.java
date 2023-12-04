/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JacksonJodaPeriodFormat
/*    */   extends JacksonJodaFormatBase
/*    */ {
/*    */   protected final PeriodFormatter _formatter;
/*    */   
/*    */   public JacksonJodaPeriodFormat(PeriodFormatter defaultFormatter)
/*    */   {
/* 20 */     this._formatter = defaultFormatter;
/*    */   }
/*    */   
/*    */   public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Locale locale)
/*    */   {
/* 25 */     super(base, locale);
/* 26 */     PeriodFormatter f = base._formatter;
/* 27 */     if (locale != null) {
/* 28 */       f = f.withLocale(locale);
/*    */     }
/* 30 */     this._formatter = f;
/*    */   }
/*    */   
/*    */   public JacksonJodaPeriodFormat(JacksonJodaPeriodFormat base, Boolean useTimestamp)
/*    */   {
/* 35 */     super(base, useTimestamp);
/* 36 */     this._formatter = base._formatter;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JacksonJodaPeriodFormat withUseTimestamp(Boolean useTimestamp)
/*    */   {
/* 47 */     if ((this._useTimestamp != null) && (this._useTimestamp.equals(useTimestamp))) {
/* 48 */       return this;
/*    */     }
/* 50 */     return new JacksonJodaPeriodFormat(this, useTimestamp);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JacksonJodaPeriodFormat withFormat(String format)
/*    */   {
/* 58 */     return this;
/*    */   }
/*    */   
/*    */   public JacksonJodaPeriodFormat withLocale(Locale locale) {
/* 62 */     if ((locale == null) || ((this._locale != null) && (this._locale.equals(locale)))) {
/* 63 */       return this;
/*    */     }
/* 65 */     return new JacksonJodaPeriodFormat(this, locale);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PeriodFormatter createFormatter(SerializerProvider provider)
/*    */   {
/* 76 */     PeriodFormatter formatter = this._formatter;
/*    */     
/* 78 */     if (!this._explicitLocale) {
/* 79 */       Locale loc = provider.getLocale();
/* 80 */       if ((loc != null) && (!loc.equals(this._locale))) {
/* 81 */         formatter = formatter.withLocale(loc);
/*    */       }
/*    */     }
/* 84 */     return formatter;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\cfg\JacksonJodaPeriodFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */