/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class JacksonJodaFormatBase
/*    */ {
/* 16 */   protected static final Locale DEFAULT_LOCALE = ;
/*    */   
/*    */ 
/*    */   protected final Boolean _useTimestamp;
/*    */   
/*    */ 
/*    */   protected final Locale _locale;
/*    */   
/*    */ 
/*    */   protected final boolean _explicitLocale;
/*    */   
/*    */ 
/*    */ 
/*    */   protected JacksonJodaFormatBase(Boolean useTimestamp, Locale locale, boolean explicitLocale)
/*    */   {
/* 31 */     this._useTimestamp = useTimestamp;
/* 32 */     this._locale = locale;
/* 33 */     this._explicitLocale = explicitLocale;
/*    */   }
/*    */   
/*    */   protected JacksonJodaFormatBase() {
/* 37 */     this._useTimestamp = null;
/* 38 */     this._locale = DEFAULT_LOCALE;
/* 39 */     this._explicitLocale = false;
/*    */   }
/*    */   
/*    */   protected JacksonJodaFormatBase(JacksonJodaFormatBase base)
/*    */   {
/* 44 */     this._useTimestamp = base._useTimestamp;
/* 45 */     this._locale = base._locale;
/* 46 */     this._explicitLocale = base._explicitLocale;
/*    */   }
/*    */   
/*    */   protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Boolean useTimestamp)
/*    */   {
/* 51 */     this._useTimestamp = useTimestamp;
/* 52 */     this._locale = base._locale;
/* 53 */     this._explicitLocale = base._explicitLocale;
/*    */   }
/*    */   
/*    */   protected JacksonJodaFormatBase(JacksonJodaFormatBase base, TimeZone jdkTimezone)
/*    */   {
/* 58 */     this._useTimestamp = base._useTimestamp;
/* 59 */     this._locale = base._locale;
/* 60 */     this._explicitLocale = base._explicitLocale;
/*    */   }
/*    */   
/*    */   protected JacksonJodaFormatBase(JacksonJodaFormatBase base, Locale locale)
/*    */   {
/* 65 */     this._useTimestamp = base._useTimestamp;
/* 66 */     if (locale == null) {
/* 67 */       this._locale = DEFAULT_LOCALE;
/* 68 */       this._explicitLocale = false;
/*    */     } else {
/* 70 */       this._locale = locale;
/* 71 */       this._explicitLocale = true;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean useTimestamp(SerializerProvider provider, SerializationFeature feat)
/*    */   {
/* 83 */     if (this._useTimestamp != null) {
/* 84 */       return this._useTimestamp.booleanValue();
/*    */     }
/* 86 */     return provider.isEnabled(feat);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\cfg\JacksonJodaFormatBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */