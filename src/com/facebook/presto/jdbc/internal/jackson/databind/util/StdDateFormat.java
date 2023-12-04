/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberInput;
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
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
/*     */ public class StdDateFormat
/*     */   extends DateFormat
/*     */ {
/*     */   public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
/*     */   protected static final String DATE_FORMAT_STR_ISO8601_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/*     */   protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
/*     */   protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
/*  56 */   protected static final String[] ALL_FORMATS = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd" };
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
/*  69 */   private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
/*     */   
/*     */ 
/*  72 */   private static final Locale DEFAULT_LOCALE = Locale.US;
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
/*  90 */   protected static final DateFormat DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", DEFAULT_LOCALE);
/*  91 */   static { DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
/*  92 */     DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DEFAULT_LOCALE);
/*  93 */     DATE_FORMAT_ISO8601.setTimeZone(DEFAULT_TIMEZONE);
/*  94 */     DATE_FORMAT_ISO8601_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", DEFAULT_LOCALE);
/*  95 */     DATE_FORMAT_ISO8601_Z.setTimeZone(DEFAULT_TIMEZONE);
/*  96 */     DATE_FORMAT_PLAIN = new SimpleDateFormat("yyyy-MM-dd", DEFAULT_LOCALE);
/*  97 */     DATE_FORMAT_PLAIN.setTimeZone(DEFAULT_TIMEZONE);
/*     */   }
/*     */   
/*     */   protected static final DateFormat DATE_FORMAT_ISO8601;
/*     */   protected static final DateFormat DATE_FORMAT_ISO8601_Z;
/*     */   protected static final DateFormat DATE_FORMAT_PLAIN;
/* 103 */   public static final StdDateFormat instance = new StdDateFormat();
/*     */   
/*     */ 
/*     */ 
/*     */   protected transient TimeZone _timezone;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final Locale _locale;
/*     */   
/*     */ 
/*     */ 
/*     */   protected Boolean _lenient;
/*     */   
/*     */ 
/*     */ 
/*     */   protected transient DateFormat _formatRFC1123;
/*     */   
/*     */ 
/*     */ 
/*     */   protected transient DateFormat _formatISO8601;
/*     */   
/*     */ 
/*     */   protected transient DateFormat _formatISO8601_z;
/*     */   
/*     */ 
/*     */   protected transient DateFormat _formatPlain;
/*     */   
/*     */ 
/*     */ 
/*     */   public StdDateFormat()
/*     */   {
/* 135 */     this._locale = DEFAULT_LOCALE;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public StdDateFormat(TimeZone tz, Locale loc) {
/* 140 */     this._timezone = tz;
/* 141 */     this._locale = loc;
/*     */   }
/*     */   
/*     */   protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
/* 145 */     this._timezone = tz;
/* 146 */     this._locale = loc;
/* 147 */     this._lenient = lenient;
/*     */   }
/*     */   
/*     */   public static TimeZone getDefaultTimeZone() {
/* 151 */     return DEFAULT_TIMEZONE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdDateFormat withTimeZone(TimeZone tz)
/*     */   {
/* 159 */     if (tz == null) {
/* 160 */       tz = DEFAULT_TIMEZONE;
/*     */     }
/* 162 */     if ((tz == this._timezone) || (tz.equals(this._timezone))) {
/* 163 */       return this;
/*     */     }
/* 165 */     return new StdDateFormat(tz, this._locale, this._lenient);
/*     */   }
/*     */   
/*     */   public StdDateFormat withLocale(Locale loc) {
/* 169 */     if (loc.equals(this._locale)) {
/* 170 */       return this;
/*     */     }
/* 172 */     return new StdDateFormat(this._timezone, loc, this._lenient);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdDateFormat clone()
/*     */   {
/* 180 */     return new StdDateFormat(this._timezone, this._locale, this._lenient);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static DateFormat getISO8601Format(TimeZone tz)
/*     */   {
/* 188 */     return getISO8601Format(tz, DEFAULT_LOCALE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateFormat getISO8601Format(TimeZone tz, Locale loc)
/*     */   {
/* 199 */     return _cloneFormat(DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", tz, loc, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateFormat getRFC1123Format(TimeZone tz, Locale loc)
/*     */   {
/* 210 */     return _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static DateFormat getRFC1123Format(TimeZone tz)
/*     */   {
/* 219 */     return getRFC1123Format(tz, DEFAULT_LOCALE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TimeZone getTimeZone()
/*     */   {
/* 230 */     return this._timezone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTimeZone(TimeZone tz)
/*     */   {
/* 239 */     if (!tz.equals(this._timezone)) {
/* 240 */       _clearFormats();
/* 241 */       this._timezone = tz;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLenient(boolean enabled)
/*     */   {
/* 252 */     Boolean newValue = Boolean.valueOf(enabled);
/* 253 */     if (this._lenient != newValue) {
/* 254 */       this._lenient = newValue;
/*     */       
/* 256 */       _clearFormats();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isLenient()
/*     */   {
/* 262 */     if (this._lenient == null)
/*     */     {
/* 264 */       return true;
/*     */     }
/* 266 */     return this._lenient.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date parse(String dateStr)
/*     */     throws ParseException
/*     */   {
/* 278 */     dateStr = dateStr.trim();
/* 279 */     ParsePosition pos = new ParsePosition(0);
/*     */     
/*     */     Date dt;
/*     */     Date dt;
/* 283 */     if (looksLikeISO8601(dateStr)) {
/* 284 */       dt = parseAsISO8601(dateStr, pos, true);
/*     */     }
/*     */     else {
/* 287 */       int i = dateStr.length();
/* 288 */       for (;;) { i--; if (i < 0) break;
/* 289 */         char ch = dateStr.charAt(i);
/* 290 */         if (((ch < '0') || (ch > '9')) && (
/*     */         
/* 292 */           (i > 0) || (ch != '-'))) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       Date dt;
/* 297 */       if ((i < 0) && ((dateStr.charAt(0) == '-') || (NumberInput.inLongRange(dateStr, false))))
/*     */       {
/*     */ 
/* 300 */         dt = new Date(Long.parseLong(dateStr));
/*     */       }
/*     */       else {
/* 303 */         dt = parseAsRFC1123(dateStr, pos);
/*     */       }
/*     */     }
/* 306 */     if (dt != null) {
/* 307 */       return dt;
/*     */     }
/*     */     
/* 310 */     StringBuilder sb = new StringBuilder();
/* 311 */     for (String f : ALL_FORMATS) {
/* 312 */       if (sb.length() > 0) {
/* 313 */         sb.append("\", \"");
/*     */       } else {
/* 315 */         sb.append('"');
/*     */       }
/* 317 */       sb.append(f);
/*     */     }
/* 319 */     sb.append('"');
/* 320 */     throw new ParseException(String.format("Can not parse date \"%s\": not compatible with any of standard forms (%s)", new Object[] { dateStr, sb.toString() }), pos.getErrorIndex());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Date parse(String dateStr, ParsePosition pos)
/*     */   {
/* 328 */     if (looksLikeISO8601(dateStr)) {
/*     */       try {
/* 330 */         return parseAsISO8601(dateStr, pos, false);
/*     */       } catch (ParseException e) {
/* 332 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 336 */     int i = dateStr.length();
/* 337 */     for (;;) { i--; if (i < 0) break;
/* 338 */       char ch = dateStr.charAt(i);
/* 339 */       if (((ch < '0') || (ch > '9')) && (
/*     */       
/* 341 */         (i > 0) || (ch != '-'))) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/* 346 */     if (i < 0)
/*     */     {
/* 348 */       if ((dateStr.charAt(0) == '-') || (NumberInput.inLongRange(dateStr, false))) {
/* 349 */         return new Date(Long.parseLong(dateStr));
/*     */       }
/*     */     }
/*     */     
/* 353 */     return parseAsRFC1123(dateStr, pos);
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
/*     */   public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
/*     */   {
/* 366 */     if (this._formatISO8601 == null) {
/* 367 */       this._formatISO8601 = _cloneFormat(DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", this._timezone, this._locale, this._lenient);
/*     */     }
/*     */     
/* 370 */     return this._formatISO8601.format(date, toAppendTo, fieldPosition);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 381 */     String str = "DateFormat " + getClass().getName();
/* 382 */     TimeZone tz = this._timezone;
/* 383 */     if (tz != null) {
/* 384 */       str = str + " (timezone: " + tz + ")";
/*     */     }
/* 386 */     str = str + "(locale: " + this._locale + ")";
/* 387 */     return str;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 392 */     return o == this;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 397 */     return System.identityHashCode(this);
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
/*     */ 
/*     */ 
/*     */   protected boolean looksLikeISO8601(String dateStr)
/*     */   {
/* 412 */     if ((dateStr.length() >= 5) && (Character.isDigit(dateStr.charAt(0))) && (Character.isDigit(dateStr.charAt(3))) && (dateStr.charAt(4) == '-'))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 417 */       return true;
/*     */     }
/* 419 */     return false;
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
/*     */   protected Date parseAsISO8601(String dateStr, ParsePosition pos, boolean throwErrors)
/*     */     throws ParseException
/*     */   {
/* 433 */     int len = dateStr.length();
/* 434 */     char c = dateStr.charAt(len - 1);
/*     */     
/*     */     DateFormat df;
/*     */     
/*     */     String formatStr;
/* 439 */     if ((len <= 10) && (Character.isDigit(c))) {
/* 440 */       DateFormat df = this._formatPlain;
/* 441 */       String formatStr = "yyyy-MM-dd";
/* 442 */       if (df == null) {
/* 443 */         df = this._formatPlain = _cloneFormat(DATE_FORMAT_PLAIN, formatStr, this._timezone, this._locale, this._lenient);
/*     */       }
/*     */     }
/* 446 */     else if (c == 'Z') {
/* 447 */       DateFormat df = this._formatISO8601_z;
/* 448 */       String formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/* 449 */       if (df == null) {
/* 450 */         df = this._formatISO8601_z = _cloneFormat(DATE_FORMAT_ISO8601_Z, formatStr, this._timezone, this._locale, this._lenient);
/*     */       }
/*     */       
/*     */ 
/* 454 */       if (dateStr.charAt(len - 4) == ':') {
/* 455 */         StringBuilder sb = new StringBuilder(dateStr);
/* 456 */         sb.insert(len - 1, ".000");
/* 457 */         dateStr = sb.toString();
/*     */       }
/*     */       
/*     */     }
/* 461 */     else if (hasTimeZone(dateStr)) {
/* 462 */       c = dateStr.charAt(len - 3);
/* 463 */       if (c == ':')
/*     */       {
/* 465 */         StringBuilder sb = new StringBuilder(dateStr);
/* 466 */         sb.delete(len - 3, len - 2);
/* 467 */         dateStr = sb.toString();
/* 468 */       } else if ((c == '+') || (c == '-'))
/*     */       {
/* 470 */         dateStr = dateStr + "00";
/*     */       }
/*     */       
/* 473 */       len = dateStr.length();
/*     */       
/* 475 */       int timeLen = len - dateStr.lastIndexOf('T') - 6;
/* 476 */       if (timeLen < 12) {
/* 477 */         int offset = len - 5;
/* 478 */         StringBuilder sb = new StringBuilder(dateStr);
/* 479 */         switch (timeLen) {
/*     */         case 11: 
/* 481 */           sb.insert(offset, '0'); break;
/*     */         case 10: 
/* 483 */           sb.insert(offset, "00"); break;
/*     */         case 9: 
/* 485 */           sb.insert(offset, "000"); break;
/*     */         case 8: 
/* 487 */           sb.insert(offset, ".000"); break;
/*     */         case 7: 
/*     */           break;
/*     */         case 6: 
/* 491 */           sb.insert(offset, "00.000");
/*     */         case 5: 
/* 493 */           sb.insert(offset, ":00.000");
/*     */         }
/* 495 */         dateStr = sb.toString();
/*     */       }
/* 497 */       DateFormat df = this._formatISO8601;
/* 498 */       String formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
/* 499 */       if (this._formatISO8601 == null) {
/* 500 */         df = this._formatISO8601 = _cloneFormat(DATE_FORMAT_ISO8601, formatStr, this._timezone, this._locale, this._lenient);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 505 */       StringBuilder sb = new StringBuilder(dateStr);
/*     */       
/* 507 */       int timeLen = len - dateStr.lastIndexOf('T') - 1;
/* 508 */       if (timeLen < 12) {
/* 509 */         switch (timeLen) {
/* 510 */         case 11:  sb.append('0');
/* 511 */         case 10:  sb.append('0');
/* 512 */         case 9:  sb.append('0');
/* 513 */           break;
/*     */         default: 
/* 515 */           sb.append(".000");
/*     */         }
/*     */       }
/* 518 */       sb.append('Z');
/* 519 */       dateStr = sb.toString();
/* 520 */       df = this._formatISO8601_z;
/* 521 */       formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/* 522 */       if (df == null) {
/* 523 */         df = this._formatISO8601_z = _cloneFormat(DATE_FORMAT_ISO8601_Z, formatStr, this._timezone, this._locale, this._lenient);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 528 */     Date dt = df.parse(dateStr, pos);
/*     */     
/* 530 */     if (dt == null) {
/* 531 */       throw new ParseException(String.format("Can not parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", new Object[] { dateStr, formatStr, this._lenient }), pos.getErrorIndex());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 536 */     return dt;
/*     */   }
/*     */   
/*     */   protected Date parseAsRFC1123(String dateStr, ParsePosition pos)
/*     */   {
/* 541 */     if (this._formatRFC1123 == null) {
/* 542 */       this._formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", this._timezone, this._locale, this._lenient);
/*     */     }
/*     */     
/* 545 */     return this._formatRFC1123.parse(dateStr, pos);
/*     */   }
/*     */   
/*     */ 
/*     */   private static final boolean hasTimeZone(String str)
/*     */   {
/* 551 */     int len = str.length();
/* 552 */     if (len >= 6) {
/* 553 */       char c = str.charAt(len - 6);
/* 554 */       if ((c == '+') || (c == '-')) return true;
/* 555 */       c = str.charAt(len - 5);
/* 556 */       if ((c == '+') || (c == '-')) return true;
/* 557 */       c = str.charAt(len - 3);
/* 558 */       if ((c == '+') || (c == '-')) return true;
/*     */     }
/* 560 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient)
/*     */   {
/* 566 */     if (!loc.equals(DEFAULT_LOCALE)) {
/* 567 */       df = new SimpleDateFormat(format, loc);
/* 568 */       df.setTimeZone(tz == null ? DEFAULT_TIMEZONE : tz);
/*     */     } else {
/* 570 */       df = (DateFormat)df.clone();
/* 571 */       if (tz != null) {
/* 572 */         df.setTimeZone(tz);
/*     */       }
/*     */     }
/* 575 */     if (lenient != null) {
/* 576 */       df.setLenient(lenient.booleanValue());
/*     */     }
/* 578 */     return df;
/*     */   }
/*     */   
/*     */   protected void _clearFormats() {
/* 582 */     this._formatRFC1123 = null;
/* 583 */     this._formatISO8601 = null;
/* 584 */     this._formatISO8601_z = null;
/* 585 */     this._formatPlain = null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\StdDateFormat.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */