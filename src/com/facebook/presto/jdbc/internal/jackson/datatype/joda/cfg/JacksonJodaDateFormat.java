/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JacksonJodaDateFormat
/*     */   extends JacksonJodaFormatBase
/*     */ {
/*     */   private static final String JODA_STYLE_CHARS = "SMLF-";
/*     */   protected final DateTimeFormatter _formatter;
/*     */   protected final TimeZone _jdkTimezone;
/*     */   protected transient DateTimeZone _jodaTimezone;
/*     */   protected final boolean _explicitTimezone;
/*     */   protected final Boolean _adjustToContextTZOverride;
/*     */   protected final Boolean _writeZoneId;
/*     */   
/*     */   public JacksonJodaDateFormat(DateTimeFormatter defaultFormatter)
/*     */   {
/*  49 */     this._formatter = defaultFormatter;
/*  50 */     DateTimeZone tz = defaultFormatter.getZone();
/*  51 */     this._jdkTimezone = (tz == null ? null : tz.toTimeZone());
/*  52 */     this._explicitTimezone = false;
/*  53 */     this._adjustToContextTZOverride = null;
/*  54 */     this._writeZoneId = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public JacksonJodaDateFormat(JacksonJodaDateFormat base, Boolean useTimestamp)
/*     */   {
/*  60 */     super(base, useTimestamp);
/*  61 */     this._formatter = base._formatter;
/*  62 */     this._jdkTimezone = base._jdkTimezone;
/*  63 */     this._explicitTimezone = base._explicitTimezone;
/*  64 */     this._adjustToContextTZOverride = base._adjustToContextTZOverride;
/*  65 */     this._writeZoneId = base._writeZoneId;
/*     */   }
/*     */   
/*     */ 
/*     */   public JacksonJodaDateFormat(JacksonJodaDateFormat base, DateTimeFormatter formatter)
/*     */   {
/*  71 */     super(base);
/*  72 */     this._formatter = formatter;
/*  73 */     this._jdkTimezone = base._jdkTimezone;
/*  74 */     this._explicitTimezone = base._explicitTimezone;
/*  75 */     this._adjustToContextTZOverride = base._adjustToContextTZOverride;
/*  76 */     this._writeZoneId = base._writeZoneId;
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat(JacksonJodaDateFormat base, TimeZone jdkTimezone)
/*     */   {
/*  81 */     super(base, jdkTimezone);
/*  82 */     this._formatter = base._formatter.withZone(DateTimeZone.forTimeZone(jdkTimezone));
/*  83 */     this._jdkTimezone = jdkTimezone;
/*  84 */     this._explicitTimezone = true;
/*  85 */     this._adjustToContextTZOverride = base._adjustToContextTZOverride;
/*  86 */     this._writeZoneId = base._writeZoneId;
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat(JacksonJodaDateFormat base, Locale locale)
/*     */   {
/*  91 */     super(base, locale);
/*  92 */     this._formatter = base._formatter.withLocale(locale);
/*  93 */     this._jdkTimezone = base._jdkTimezone;
/*  94 */     this._explicitTimezone = base._explicitTimezone;
/*  95 */     this._adjustToContextTZOverride = base._adjustToContextTZOverride;
/*  96 */     this._writeZoneId = base._writeZoneId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JacksonJodaDateFormat(JacksonJodaDateFormat base, Boolean adjustToContextTZOverride, Boolean writeZoneId)
/*     */   {
/* 105 */     super(base);
/* 106 */     this._formatter = base._formatter;
/* 107 */     this._jdkTimezone = base._jdkTimezone;
/* 108 */     this._explicitTimezone = base._explicitTimezone;
/* 109 */     this._adjustToContextTZOverride = adjustToContextTZOverride;
/* 110 */     this._writeZoneId = writeZoneId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JacksonJodaDateFormat with(JsonFormat.Value ann)
/*     */   {
/* 120 */     JacksonJodaDateFormat format = this;
/* 121 */     format = format.withLocale(ann.getLocale());
/* 122 */     format = format.withTimeZone(ann.getTimeZone());
/* 123 */     format = format.withFormat(ann.getPattern().trim());
/* 124 */     Boolean adjustTZ = ann.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
/* 125 */     Boolean writeZoneId = ann.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
/* 126 */     if ((adjustTZ != this._adjustToContextTZOverride) || (writeZoneId != this._writeZoneId))
/*     */     {
/* 128 */       format = new JacksonJodaDateFormat(format, adjustTZ, writeZoneId);
/*     */     }
/* 130 */     return format;
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat withUseTimestamp(Boolean useTimestamp) {
/* 134 */     if ((this._useTimestamp != null) && (this._useTimestamp.equals(useTimestamp))) {
/* 135 */       return this;
/*     */     }
/* 137 */     return new JacksonJodaDateFormat(this, useTimestamp);
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat withFormat(String format) {
/* 141 */     if ((format == null) || (format.isEmpty())) {
/* 142 */       return this;
/*     */     }
/*     */     DateTimeFormatter formatter;
/*     */     DateTimeFormatter formatter;
/* 146 */     if (_isStyle(format)) {
/* 147 */       formatter = DateTimeFormat.forStyle(format);
/*     */     } else {
/* 149 */       formatter = DateTimeFormat.forPattern(format);
/*     */     }
/* 151 */     if (this._locale != null) {
/* 152 */       formatter = formatter.withLocale(this._locale);
/*     */     }
/* 154 */     return new JacksonJodaDateFormat(this, formatter);
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat withTimeZone(TimeZone tz) {
/* 158 */     if ((tz == null) || ((this._jdkTimezone != null) && (this._jdkTimezone.equals(tz)))) {
/* 159 */       return this;
/*     */     }
/* 161 */     return new JacksonJodaDateFormat(this, tz);
/*     */   }
/*     */   
/*     */   public JacksonJodaDateFormat withLocale(Locale locale) {
/* 165 */     if ((locale == null) || ((this._locale != null) && (this._locale.equals(locale)))) {
/* 166 */       return this;
/*     */     }
/* 168 */     return new JacksonJodaDateFormat(this, locale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JacksonJodaDateFormat withAdjustToContextTZOverride(Boolean adjustToContextTZOverride)
/*     */   {
/* 176 */     if (adjustToContextTZOverride == this._adjustToContextTZOverride) {
/* 177 */       return this;
/*     */     }
/* 179 */     return new JacksonJodaDateFormat(this, adjustToContextTZOverride, this._writeZoneId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JacksonJodaDateFormat withWriteZoneId(Boolean writeZoneId)
/*     */   {
/* 187 */     if (writeZoneId == this._writeZoneId) {
/* 188 */       return this;
/*     */     }
/* 190 */     return new JacksonJodaDateFormat(this, this._adjustToContextTZOverride, writeZoneId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeZone getTimeZone()
/*     */   {
/* 203 */     if (this._jodaTimezone != null) {
/* 204 */       return this._jodaTimezone;
/*     */     }
/* 206 */     if (this._jdkTimezone == null) {
/* 207 */       return null;
/*     */     }
/* 209 */     DateTimeZone tz = DateTimeZone.forTimeZone(this._jdkTimezone);
/* 210 */     this._jodaTimezone = tz;
/* 211 */     return tz;
/*     */   }
/*     */   
/*     */   public Locale getLocale() {
/* 215 */     return this._locale;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFormatter rawFormatter()
/*     */   {
/* 225 */     return this._formatter;
/*     */   }
/*     */   
/*     */   public DateTimeFormatter createFormatter(SerializerProvider ctxt)
/*     */   {
/* 230 */     DateTimeFormatter formatter = createFormatterWithLocale(ctxt);
/* 231 */     if (!this._explicitTimezone) {
/* 232 */       TimeZone tz = ctxt.getTimeZone();
/* 233 */       if ((tz != null) && (!tz.equals(this._jdkTimezone))) {
/* 234 */         formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
/*     */       }
/*     */     }
/* 237 */     return formatter;
/*     */   }
/*     */   
/*     */   public DateTimeFormatter createFormatterWithLocale(SerializerProvider ctxt)
/*     */   {
/* 242 */     DateTimeFormatter formatter = this._formatter;
/* 243 */     if (!this._explicitLocale) {
/* 244 */       Locale loc = ctxt.getLocale();
/* 245 */       if ((loc != null) && (!loc.equals(this._locale))) {
/* 246 */         formatter = formatter.withLocale(loc);
/*     */       }
/*     */     }
/* 249 */     return formatter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFormatter createParser(DeserializationContext ctxt)
/*     */   {
/* 257 */     DateTimeFormatter formatter = this._formatter;
/* 258 */     if (!this._explicitLocale) {
/* 259 */       Locale loc = ctxt.getLocale();
/* 260 */       if ((loc != null) && (!loc.equals(this._locale))) {
/* 261 */         formatter = formatter.withLocale(loc);
/*     */       }
/*     */     }
/* 264 */     if (!this._explicitTimezone) {
/* 265 */       if (shouldAdjustToContextTimeZone(ctxt)) {
/* 266 */         TimeZone tz = ctxt.getTimeZone();
/* 267 */         if ((tz != null) && (!tz.equals(this._jdkTimezone))) {
/* 268 */           formatter = formatter.withZone(DateTimeZone.forTimeZone(tz));
/*     */         }
/*     */       } else {
/* 271 */         formatter = formatter.withOffsetParsed();
/*     */       }
/*     */     }
/* 274 */     return formatter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean shouldAdjustToContextTimeZone(DeserializationContext ctxt)
/*     */   {
/* 281 */     return this._adjustToContextTZOverride != null ? this._adjustToContextTZOverride.booleanValue() : ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean shouldWriteWithZoneId(SerializerProvider ctxt)
/*     */   {
/* 289 */     return this._writeZoneId != null ? this._writeZoneId.booleanValue() : ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTimezoneExplicit()
/*     */   {
/* 298 */     return this._explicitTimezone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean _isStyle(String formatStr)
/*     */   {
/* 308 */     if (formatStr.length() != 2) {
/* 309 */       return false;
/*     */     }
/* 311 */     return ("SMLF-".indexOf(formatStr.charAt(0)) >= 0) && ("SMLF-".indexOf(formatStr.charAt(0)) >= 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 317 */     return String.format("[JacksonJodaFormat, explicitTZ? %s, JDK tz = %s, formatter = %s]", new Object[] { Boolean.valueOf(this._explicitTimezone), this._jdkTimezone.getID(), this._formatter });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\cfg\JacksonJodaDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */