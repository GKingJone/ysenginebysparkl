/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Properties;
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
/*     */ public class TimeUtil
/*     */ {
/*  42 */   static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
/*     */   
/*     */ 
/*  45 */   private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
/*     */   
/*     */ 
/*     */   private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/jdbc/TimeZoneMapping.properties";
/*     */   
/*  50 */   private static Properties timeZoneMappings = null;
/*     */   protected static final Method systemNanoTimeMethod;
/*     */   
/*     */   static
/*     */   {
/*     */     Method aMethod;
/*     */     try
/*     */     {
/*  58 */       aMethod = System.class.getMethod("nanoTime", (Class[])null);
/*     */     } catch (SecurityException e) {
/*  60 */       aMethod = null;
/*     */     } catch (NoSuchMethodException e) {
/*  62 */       aMethod = null;
/*     */     }
/*     */     
/*  65 */     systemNanoTimeMethod = aMethod;
/*     */   }
/*     */   
/*     */   public static boolean nanoTimeAvailable() {
/*  69 */     return systemNanoTimeMethod != null;
/*     */   }
/*     */   
/*     */   public static final TimeZone getDefaultTimeZone(boolean useCache) {
/*  73 */     return (TimeZone)(useCache ? DEFAULT_TIMEZONE.clone() : TimeZone.getDefault().clone());
/*     */   }
/*     */   
/*     */   public static long getCurrentTimeNanosOrMillis() {
/*  77 */     if (systemNanoTimeMethod != null) {
/*     */       try {
/*  79 */         return ((Long)systemNanoTimeMethod.invoke(null, (Object[])null)).longValue();
/*     */       }
/*     */       catch (IllegalArgumentException e) {}catch (IllegalAccessException e) {}catch (InvocationTargetException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  89 */     return System.currentTimeMillis();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Time changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*     */   {
/* 108 */     if (conn != null) {
/* 109 */       if ((conn.getUseTimezone()) && (!conn.getNoTimezoneConversionForTimeType()))
/*     */       {
/* 111 */         Calendar fromCal = Calendar.getInstance(fromTz);
/* 112 */         fromCal.setTime(t);
/*     */         
/* 114 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/* 115 */         Calendar toCal = Calendar.getInstance(toTz);
/* 116 */         toCal.setTime(t);
/*     */         
/* 118 */         int toOffset = toCal.get(15) + toCal.get(16);
/* 119 */         int offsetDiff = fromOffset - toOffset;
/* 120 */         long toTime = toCal.getTime().getTime();
/*     */         
/* 122 */         if (rollForward) {
/* 123 */           toTime += offsetDiff;
/*     */         } else {
/* 125 */           toTime -= offsetDiff;
/*     */         }
/*     */         
/* 128 */         Time changedTime = new Time(toTime);
/*     */         
/* 130 */         return changedTime; }
/* 131 */       if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/* 132 */         (targetCalendar != null))
/*     */       {
/* 134 */         Time adjustedTime = new Time(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, t));
/*     */         
/* 136 */         return adjustedTime;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 141 */     return t;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Timestamp changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward)
/*     */   {
/* 160 */     if (conn != null) {
/* 161 */       if (conn.getUseTimezone())
/*     */       {
/* 163 */         Calendar fromCal = Calendar.getInstance(fromTz);
/* 164 */         fromCal.setTime(tstamp);
/*     */         
/* 166 */         int fromOffset = fromCal.get(15) + fromCal.get(16);
/* 167 */         Calendar toCal = Calendar.getInstance(toTz);
/* 168 */         toCal.setTime(tstamp);
/*     */         
/* 170 */         int toOffset = toCal.get(15) + toCal.get(16);
/* 171 */         int offsetDiff = fromOffset - toOffset;
/* 172 */         long toTime = toCal.getTime().getTime();
/*     */         
/* 174 */         if (rollForward) {
/* 175 */           toTime += offsetDiff;
/*     */         } else {
/* 177 */           toTime -= offsetDiff;
/*     */         }
/*     */         
/* 180 */         Timestamp changedTimestamp = new Timestamp(toTime);
/*     */         
/* 182 */         return changedTimestamp; }
/* 183 */       if ((conn.getUseJDBCCompliantTimezoneShift()) && 
/* 184 */         (targetCalendar != null))
/*     */       {
/* 186 */         Timestamp adjustedTimestamp = new Timestamp(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, tstamp));
/*     */         
/* 188 */         adjustedTimestamp.setNanos(tstamp.getNanos());
/*     */         
/* 190 */         return adjustedTimestamp;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 195 */     return tstamp;
/*     */   }
/*     */   
/*     */   private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, java.util.Date dt) {
/* 199 */     if (sessionCalendar == null) {
/* 200 */       sessionCalendar = new GregorianCalendar();
/*     */     }
/*     */     
/* 203 */     synchronized (sessionCalendar)
/*     */     {
/*     */ 
/* 206 */       java.util.Date origCalDate = targetCalendar.getTime();
/* 207 */       java.util.Date origSessionDate = sessionCalendar.getTime();
/*     */       try
/*     */       {
/* 210 */         sessionCalendar.setTime(dt);
/*     */         
/* 212 */         targetCalendar.set(1, sessionCalendar.get(1));
/* 213 */         targetCalendar.set(2, sessionCalendar.get(2));
/* 214 */         targetCalendar.set(5, sessionCalendar.get(5));
/*     */         
/* 216 */         targetCalendar.set(11, sessionCalendar.get(11));
/* 217 */         targetCalendar.set(12, sessionCalendar.get(12));
/* 218 */         targetCalendar.set(13, sessionCalendar.get(13));
/* 219 */         targetCalendar.set(14, sessionCalendar.get(14));
/*     */         
/* 221 */         long l = targetCalendar.getTime().getTime();jsr 16;return l;
/*     */       }
/*     */       finally {
/* 224 */         jsr 6; } localObject2 = returnAddress;sessionCalendar.setTime(origSessionDate);
/* 225 */       targetCalendar.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static final java.sql.Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day)
/*     */   {
/* 232 */     Calendar dateCal = cal;
/*     */     
/* 234 */     if (useGmtConversion)
/*     */     {
/* 236 */       if (gmtCalIfNeeded == null) {
/* 237 */         gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*     */       }
/*     */       
/* 240 */       dateCal = gmtCalIfNeeded;
/*     */     }
/*     */     
/* 243 */     synchronized (dateCal) {
/* 244 */       java.util.Date origCalDate = dateCal.getTime();
/*     */       try {
/* 246 */         dateCal.clear();
/* 247 */         dateCal.set(14, 0);
/*     */         
/*     */ 
/* 250 */         dateCal.set(year, month - 1, day, 0, 0, 0);
/*     */         
/* 252 */         long dateAsMillis = dateCal.getTimeInMillis();
/*     */         
/* 254 */         java.sql.Date localDate = new java.sql.Date(dateAsMillis);jsr 17;return localDate;
/*     */       } finally {
/* 256 */         jsr 6; } localObject2 = returnAddress;dateCal.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static final java.sql.Date fastDateCreate(int year, int month, int day, Calendar targetCalendar)
/*     */   {
/* 264 */     Calendar dateCal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/*     */     
/* 266 */     synchronized (dateCal) {
/* 267 */       java.util.Date origCalDate = dateCal.getTime();
/*     */       try {
/* 269 */         dateCal.clear();
/*     */         
/*     */ 
/* 272 */         dateCal.set(year, month - 1, day, 0, 0, 0);
/* 273 */         dateCal.set(14, 0);
/*     */         
/* 275 */         long dateAsMillis = dateCal.getTimeInMillis();
/*     */         
/* 277 */         java.sql.Date localDate = new java.sql.Date(dateAsMillis);jsr 17;return localDate;
/*     */       } finally {
/* 279 */         jsr 6; } localObject2 = returnAddress;dateCal.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */   static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*     */   {
/* 285 */     if ((hour < 0) || (hour > 24)) {
/* 286 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 291 */     if ((minute < 0) || (minute > 59)) {
/* 292 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 297 */     if ((second < 0) || (second > 59)) {
/* 298 */       throw SQLError.createSQLException("Illegal minute value '" + second + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 303 */     synchronized (cal) {
/* 304 */       java.util.Date origCalDate = cal.getTime();
/*     */       try {
/* 306 */         cal.clear();
/*     */         
/*     */ 
/* 309 */         cal.set(1970, 0, 1, hour, minute, second);
/*     */         
/* 311 */         long timeAsMillis = cal.getTimeInMillis();
/*     */         
/* 313 */         Time localTime = new Time(timeAsMillis);jsr 17;return localTime;
/*     */       } finally {
/* 315 */         jsr 6; } localObject2 = returnAddress;cal.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */   static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*     */   {
/* 321 */     if ((hour < 0) || (hour > 23)) {
/* 322 */       throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 327 */     if ((minute < 0) || (minute > 59)) {
/* 328 */       throw SQLError.createSQLException("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 333 */     if ((second < 0) || (second > 59)) {
/* 334 */       throw SQLError.createSQLException("Illegal minute value '" + second + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 339 */     Calendar cal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
/*     */     
/* 341 */     synchronized (cal) {
/* 342 */       java.util.Date origCalDate = cal.getTime();
/*     */       try {
/* 344 */         cal.clear();
/*     */         
/*     */ 
/* 347 */         cal.set(1970, 0, 1, hour, minute, second);
/*     */         
/* 349 */         long timeAsMillis = cal.getTimeInMillis();
/*     */         
/* 351 */         Time localTime = new Time(timeAsMillis);jsr 17;return localTime;
/*     */       } finally {
/* 353 */         jsr 6; } localObject2 = returnAddress;cal.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*     */   {
/* 361 */     synchronized (cal) {
/* 362 */       java.util.Date origCalDate = cal.getTime();
/*     */       try {
/* 364 */         cal.clear();
/*     */         
/*     */ 
/* 367 */         cal.set(year, month - 1, day, hour, minute, seconds);
/*     */         
/* 369 */         int offsetDiff = 0;
/*     */         
/* 371 */         if (useGmtConversion) {
/* 372 */           int fromOffset = cal.get(15) + cal.get(16);
/*     */           
/* 374 */           if (gmtCalIfNeeded == null) {
/* 375 */             gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*     */           }
/* 377 */           gmtCalIfNeeded.clear();
/*     */           
/* 379 */           gmtCalIfNeeded.setTimeInMillis(cal.getTimeInMillis());
/*     */           
/* 381 */           int toOffset = gmtCalIfNeeded.get(15) + gmtCalIfNeeded.get(16);
/* 382 */           offsetDiff = fromOffset - toOffset;
/*     */         }
/*     */         
/* 385 */         if (secondsPart != 0) {
/* 386 */           cal.set(14, secondsPart / 1000000);
/*     */         }
/*     */         
/* 389 */         long tsAsMillis = cal.getTimeInMillis();
/*     */         
/* 391 */         Timestamp ts = new Timestamp(tsAsMillis + offsetDiff);
/*     */         
/* 393 */         ts.setNanos(secondsPart);
/*     */         
/* 395 */         Timestamp localTimestamp1 = ts;jsr 17;return localTimestamp1;
/*     */       } finally {
/* 397 */         jsr 6; } localObject2 = returnAddress;cal.setTime(origCalDate);ret;
/*     */     }
/*     */   }
/*     */   
/*     */   static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*     */   {
/* 403 */     Calendar cal = tz == null ? new GregorianCalendar() : new GregorianCalendar(tz);
/* 404 */     cal.clear();
/*     */     
/*     */ 
/* 407 */     cal.set(year, month - 1, day, hour, minute, seconds);
/*     */     
/* 409 */     long tsAsMillis = cal.getTimeInMillis();
/*     */     
/* 411 */     Timestamp ts = new Timestamp(tsAsMillis);
/* 412 */     ts.setNanos(secondsPart);
/*     */     
/* 414 */     return ts;
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
/*     */   public static String getCanonicalTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 429 */     if (timezoneStr == null) {
/* 430 */       return null;
/*     */     }
/*     */     
/* 433 */     timezoneStr = timezoneStr.trim();
/*     */     
/*     */ 
/* 436 */     if ((timezoneStr.length() > 2) && 
/* 437 */       ((timezoneStr.charAt(0) == '+') || (timezoneStr.charAt(0) == '-')) && (Character.isDigit(timezoneStr.charAt(1)))) {
/* 438 */       return "GMT" + timezoneStr;
/*     */     }
/*     */     
/*     */ 
/* 442 */     synchronized (TimeUtil.class) {
/* 443 */       if (timeZoneMappings == null) {
/* 444 */         loadTimeZoneMappings(exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     
/*     */     String canonicalTz;
/* 449 */     if ((canonicalTz = timeZoneMappings.getProperty(timezoneStr)) != null) {
/* 450 */       return canonicalTz;
/*     */     }
/*     */     
/* 453 */     throw SQLError.createSQLException(Messages.getString("TimeUtil.UnrecognizedTimezoneId", new Object[] { timezoneStr }), "01S00", exceptionInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static String timeFormattedString(int hours, int minutes, int seconds)
/*     */   {
/* 460 */     StringBuilder buf = new StringBuilder(8);
/* 461 */     if (hours < 10) {
/* 462 */       buf.append("0");
/*     */     }
/*     */     
/* 465 */     buf.append(hours);
/* 466 */     buf.append(":");
/*     */     
/* 468 */     if (minutes < 10) {
/* 469 */       buf.append("0");
/*     */     }
/*     */     
/* 472 */     buf.append(minutes);
/* 473 */     buf.append(":");
/*     */     
/* 475 */     if (seconds < 10) {
/* 476 */       buf.append("0");
/*     */     }
/*     */     
/* 479 */     buf.append(seconds);
/*     */     
/* 481 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String formatNanos(int nanos, boolean serverSupportsFracSecs, boolean usingMicros)
/*     */   {
/* 487 */     if (nanos > 999999999) {
/* 488 */       nanos %= 100000000;
/*     */     }
/*     */     
/* 491 */     if (usingMicros) {
/* 492 */       nanos /= 1000;
/*     */     }
/*     */     
/* 495 */     if ((!serverSupportsFracSecs) || (nanos == 0)) {
/* 496 */       return "0";
/*     */     }
/*     */     
/* 499 */     int digitCount = usingMicros ? 6 : 9;
/*     */     
/* 501 */     String nanosString = Integer.toString(nanos);
/* 502 */     String zeroPadding = usingMicros ? "000000" : "000000000";
/*     */     
/* 504 */     nanosString = zeroPadding.substring(0, digitCount - nanosString.length()) + nanosString;
/*     */     
/* 506 */     int pos = digitCount - 1;
/*     */     
/* 508 */     while (nanosString.charAt(pos) == '0') {
/* 509 */       pos--;
/*     */     }
/*     */     
/* 512 */     nanosString = nanosString.substring(0, pos + 1);
/*     */     
/* 514 */     return nanosString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 524 */     timeZoneMappings = new Properties();
/*     */     try {
/* 526 */       timeZoneMappings.load(TimeUtil.class.getResourceAsStream("/com/mysql/jdbc/TimeZoneMapping.properties"));
/*     */     } catch (IOException e) {
/* 528 */       throw SQLError.createSQLException(Messages.getString("TimeUtil.LoadTimeZoneMappingError"), "01S00", exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 532 */     for (String tz : TimeZone.getAvailableIDs()) {
/* 533 */       if (!timeZoneMappings.containsKey(tz)) {
/* 534 */         timeZoneMappings.put(tz, tz);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
/* 540 */     Timestamp truncatedTimestamp = new Timestamp(timestamp.getTime());
/* 541 */     truncatedTimestamp.setNanos(0);
/* 542 */     return truncatedTimestamp;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\TimeUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */