/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class DateTimeUtils
/*     */ {
/*  42 */   private static final SystemMillisProvider SYSTEM_MILLIS_PROVIDER = new SystemMillisProvider();
/*     */   
/*  44 */   private static volatile MillisProvider cMillisProvider = SYSTEM_MILLIS_PROVIDER;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  49 */   private static final AtomicReference<Map<String, DateTimeZone>> cZoneNames = new AtomicReference();
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
/*     */   public static final long currentTimeMillis()
/*     */   {
/*  69 */     return cMillisProvider.getMillis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void setCurrentMillisSystem()
/*     */     throws SecurityException
/*     */   {
/*  81 */     checkPermission();
/*  82 */     cMillisProvider = SYSTEM_MILLIS_PROVIDER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void setCurrentMillisFixed(long paramLong)
/*     */     throws SecurityException
/*     */   {
/*  95 */     checkPermission();
/*  96 */     cMillisProvider = new FixedMillisProvider(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void setCurrentMillisOffset(long paramLong)
/*     */     throws SecurityException
/*     */   {
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 111 */     if (paramLong == 0L) {
/* 112 */       cMillisProvider = SYSTEM_MILLIS_PROVIDER;
/*     */     } else {
/* 114 */       cMillisProvider = new OffsetMillisProvider(paramLong);
/*     */     }
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
/*     */   public static final void setCurrentMillisProvider(MillisProvider paramMillisProvider)
/*     */     throws SecurityException
/*     */   {
/* 129 */     if (paramMillisProvider == null) {
/* 130 */       throw new IllegalArgumentException("The MillisProvider must not be null");
/*     */     }
/* 132 */     checkPermission();
/* 133 */     cMillisProvider = paramMillisProvider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void checkPermission()
/*     */     throws SecurityException
/*     */   {
/* 142 */     SecurityManager localSecurityManager = System.getSecurityManager();
/* 143 */     if (localSecurityManager != null) {
/* 144 */       localSecurityManager.checkPermission(new JodaTimePermission("CurrentTime.setProvider"));
/*     */     }
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
/*     */   public static final long getInstantMillis(ReadableInstant paramReadableInstant)
/*     */   {
/* 159 */     if (paramReadableInstant == null) {
/* 160 */       return currentTimeMillis();
/*     */     }
/* 162 */     return paramReadableInstant.getMillis();
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
/*     */   public static final Chronology getInstantChronology(ReadableInstant paramReadableInstant)
/*     */   {
/* 177 */     if (paramReadableInstant == null) {
/* 178 */       return ISOChronology.getInstance();
/*     */     }
/* 180 */     Chronology localChronology = paramReadableInstant.getChronology();
/* 181 */     if (localChronology == null) {
/* 182 */       return ISOChronology.getInstance();
/*     */     }
/* 184 */     return localChronology;
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
/*     */   public static final Chronology getIntervalChronology(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 200 */     Object localObject = null;
/* 201 */     if (paramReadableInstant1 != null) {
/* 202 */       localObject = paramReadableInstant1.getChronology();
/* 203 */     } else if (paramReadableInstant2 != null) {
/* 204 */       localObject = paramReadableInstant2.getChronology();
/*     */     }
/* 206 */     if (localObject == null) {
/* 207 */       localObject = ISOChronology.getInstance();
/*     */     }
/* 209 */     return (Chronology)localObject;
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
/*     */   public static final Chronology getIntervalChronology(ReadableInterval paramReadableInterval)
/*     */   {
/* 224 */     if (paramReadableInterval == null) {
/* 225 */       return ISOChronology.getInstance();
/*     */     }
/* 227 */     Chronology localChronology = paramReadableInterval.getChronology();
/* 228 */     if (localChronology == null) {
/* 229 */       return ISOChronology.getInstance();
/*     */     }
/* 231 */     return localChronology;
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
/*     */   public static final ReadableInterval getReadableInterval(ReadableInterval paramReadableInterval)
/*     */   {
/* 247 */     if (paramReadableInterval == null) {
/* 248 */       long l = currentTimeMillis();
/* 249 */       paramReadableInterval = new Interval(l, l);
/*     */     }
/* 251 */     return paramReadableInterval;
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
/*     */   public static final Chronology getChronology(Chronology paramChronology)
/*     */   {
/* 265 */     if (paramChronology == null) {
/* 266 */       return ISOChronology.getInstance();
/*     */     }
/* 268 */     return paramChronology;
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
/*     */   public static final DateTimeZone getZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 282 */     if (paramDateTimeZone == null) {
/* 283 */       return DateTimeZone.getDefault();
/*     */     }
/* 285 */     return paramDateTimeZone;
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
/*     */   public static final PeriodType getPeriodType(PeriodType paramPeriodType)
/*     */   {
/* 299 */     if (paramPeriodType == null) {
/* 300 */       return PeriodType.standard();
/*     */     }
/* 302 */     return paramPeriodType;
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
/*     */   public static final long getDurationMillis(ReadableDuration paramReadableDuration)
/*     */   {
/* 316 */     if (paramReadableDuration == null) {
/* 317 */       return 0L;
/*     */     }
/* 319 */     return paramReadableDuration.getMillis();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean isContiguous(ReadablePartial paramReadablePartial)
/*     */   {
/* 347 */     if (paramReadablePartial == null) {
/* 348 */       throw new IllegalArgumentException("Partial must not be null");
/*     */     }
/* 350 */     DurationFieldType localDurationFieldType = null;
/* 351 */     for (int i = 0; i < paramReadablePartial.size(); i++) {
/* 352 */       DateTimeField localDateTimeField = paramReadablePartial.getField(i);
/* 353 */       if ((i > 0) && (
/* 354 */         (localDateTimeField.getRangeDurationField() == null) || (localDateTimeField.getRangeDurationField().getType() != localDurationFieldType))) {
/* 355 */         return false;
/*     */       }
/*     */       
/* 358 */       localDurationFieldType = localDateTimeField.getDurationField().getType();
/*     */     }
/* 360 */     return true;
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
/*     */   public static final DateFormatSymbols getDateFormatSymbols(Locale paramLocale)
/*     */   {
/*     */     try
/*     */     {
/* 378 */       Method localMethod = DateFormatSymbols.class.getMethod("getInstance", new Class[] { Locale.class });
/* 379 */       return (DateFormatSymbols)localMethod.invoke(null, new Object[] { paramLocale });
/*     */     } catch (Exception localException) {}
/* 381 */     return new DateFormatSymbols(paramLocale);
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
/*     */   public static final Map<String, DateTimeZone> getDefaultTimeZoneNames()
/*     */   {
/* 410 */     Map localMap = (Map)cZoneNames.get();
/* 411 */     if (localMap == null) {
/* 412 */       localMap = buildDefaultTimeZoneNames();
/* 413 */       if (!cZoneNames.compareAndSet(null, localMap)) {
/* 414 */         localMap = (Map)cZoneNames.get();
/*     */       }
/*     */     }
/* 417 */     return localMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final void setDefaultTimeZoneNames(Map<String, DateTimeZone> paramMap)
/*     */   {
/* 429 */     cZoneNames.set(Collections.unmodifiableMap(new HashMap(paramMap)));
/*     */   }
/*     */   
/*     */ 
/*     */   private static Map<String, DateTimeZone> buildDefaultTimeZoneNames()
/*     */   {
/* 435 */     LinkedHashMap localLinkedHashMap = new LinkedHashMap();
/* 436 */     localLinkedHashMap.put("UT", DateTimeZone.UTC);
/* 437 */     localLinkedHashMap.put("UTC", DateTimeZone.UTC);
/* 438 */     localLinkedHashMap.put("GMT", DateTimeZone.UTC);
/* 439 */     put(localLinkedHashMap, "EST", "America/New_York");
/* 440 */     put(localLinkedHashMap, "EDT", "America/New_York");
/* 441 */     put(localLinkedHashMap, "CST", "America/Chicago");
/* 442 */     put(localLinkedHashMap, "CDT", "America/Chicago");
/* 443 */     put(localLinkedHashMap, "MST", "America/Denver");
/* 444 */     put(localLinkedHashMap, "MDT", "America/Denver");
/* 445 */     put(localLinkedHashMap, "PST", "America/Los_Angeles");
/* 446 */     put(localLinkedHashMap, "PDT", "America/Los_Angeles");
/* 447 */     return Collections.unmodifiableMap(localLinkedHashMap);
/*     */   }
/*     */   
/*     */   private static void put(Map<String, DateTimeZone> paramMap, String paramString1, String paramString2) {
/* 451 */     try { paramMap.put(paramString1, DateTimeZone.forID(paramString2));
/*     */     }
/*     */     catch (RuntimeException localRuntimeException) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final double toJulianDay(long paramLong)
/*     */   {
/* 480 */     double d = paramLong / 8.64E7D;
/* 481 */     return d + 2440587.5D;
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
/*     */   public static final long toJulianDayNumber(long paramLong)
/*     */   {
/* 500 */     return Math.floor(toJulianDay(paramLong) + 0.5D);
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
/*     */   public static final long fromJulianDay(double paramDouble)
/*     */   {
/* 513 */     double d = paramDouble - 2440587.5D;
/* 514 */     return (d * 8.64E7D);
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
/*     */   public static abstract interface MillisProvider
/*     */   {
/*     */     public abstract long getMillis();
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
/*     */   static class SystemMillisProvider
/*     */     implements MillisProvider
/*     */   {
/*     */     public long getMillis()
/*     */     {
/* 544 */       return System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class FixedMillisProvider
/*     */     implements MillisProvider
/*     */   {
/*     */     private final long iMillis;
/*     */     
/*     */ 
/*     */ 
/*     */     FixedMillisProvider(long paramLong)
/*     */     {
/* 560 */       this.iMillis = paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getMillis()
/*     */     {
/* 568 */       return this.iMillis;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class OffsetMillisProvider
/*     */     implements MillisProvider
/*     */   {
/*     */     private final long iMillis;
/*     */     
/*     */ 
/*     */ 
/*     */     OffsetMillisProvider(long paramLong)
/*     */     {
/* 584 */       this.iMillis = paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getMillis()
/*     */     {
/* 592 */       return System.currentTimeMillis() + this.iMillis;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\DateTimeUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */