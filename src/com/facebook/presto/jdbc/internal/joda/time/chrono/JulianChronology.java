/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.SkipDateTimeField;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public final class JulianChronology
/*     */   extends BasicGJChronology
/*     */ {
/*  70 */   private static final ConcurrentHashMap<DateTimeZone, JulianChronology[]> cCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*  73 */   private static final JulianChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);
/*     */   private static final int MAX_YEAR = 292272992;
/*     */   private static final int MIN_YEAR = -292269054;
/*     */   
/*  77 */   static int adjustYearForSet(int paramInt) { if (paramInt <= 0) {
/*  78 */       if (paramInt == 0) {
/*  79 */         throw new IllegalFieldValueException(DateTimeFieldType.year(), Integer.valueOf(paramInt), null, null);
/*     */       }
/*     */       
/*  82 */       paramInt++;
/*     */     }
/*  84 */     return paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JulianChronology getInstanceUTC()
/*     */   {
/*  94 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JulianChronology getInstance()
/*     */   {
/* 103 */     return getInstance(DateTimeZone.getDefault(), 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JulianChronology getInstance(DateTimeZone paramDateTimeZone)
/*     */   {
/* 113 */     return getInstance(paramDateTimeZone, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JulianChronology getInstance(DateTimeZone paramDateTimeZone, int paramInt)
/*     */   {
/* 124 */     if (paramDateTimeZone == null) {
/* 125 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*     */     
/* 128 */     Object localObject1 = (JulianChronology[])cCache.get(paramDateTimeZone);
/* 129 */     if (localObject1 == null) {
/* 130 */       localObject1 = new JulianChronology[7];
/* 131 */       JulianChronology[] arrayOfJulianChronology = (JulianChronology[])cCache.putIfAbsent(paramDateTimeZone, localObject1);
/* 132 */       if (arrayOfJulianChronology != null)
/* 133 */         localObject1 = arrayOfJulianChronology;
/*     */     }
/*     */     JulianChronology localJulianChronology;
/*     */     try {
/* 137 */       localJulianChronology = localObject1[(paramInt - 1)];
/*     */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 139 */       throw new IllegalArgumentException("Invalid min days in first week: " + paramInt);
/*     */     }
/*     */     
/* 142 */     if (localJulianChronology == null) {
/* 143 */       synchronized (localObject1) {
/* 144 */         localJulianChronology = localObject1[(paramInt - 1)];
/* 145 */         if (localJulianChronology == null) {
/* 146 */           if (paramDateTimeZone == DateTimeZone.UTC) {
/* 147 */             localJulianChronology = new JulianChronology(null, null, paramInt);
/*     */           } else {
/* 149 */             localJulianChronology = getInstance(DateTimeZone.UTC, paramInt);
/* 150 */             localJulianChronology = new JulianChronology(ZonedChronology.getInstance(localJulianChronology, paramDateTimeZone), null, paramInt);
/*     */           }
/*     */           
/* 153 */           localObject1[(paramInt - 1)] = localJulianChronology;
/*     */         }
/*     */       }
/*     */     }
/* 157 */     return localJulianChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   JulianChronology(Chronology paramChronology, Object paramObject, int paramInt)
/*     */   {
/* 167 */     super(paramChronology, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 174 */     Chronology localChronology = getBase();
/* 175 */     int i = getMinimumDaysInFirstWeek();
/* 176 */     i = i == 0 ? 4 : i;
/* 177 */     return localChronology == null ? getInstance(DateTimeZone.UTC, i) : getInstance(localChronology.getZone(), i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_MONTH = 2629800000L;
/*     */   
/*     */   private static final long MILLIS_PER_YEAR = 31557600000L;
/*     */   
/*     */   private static final long serialVersionUID = -8731039522547897247L;
/*     */   
/*     */   public Chronology withUTC()
/*     */   {
/* 190 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 200 */     if (paramDateTimeZone == null) {
/* 201 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 203 */     if (paramDateTimeZone == getZone()) {
/* 204 */       return this;
/*     */     }
/* 206 */     return getInstance(paramDateTimeZone);
/*     */   }
/*     */   
/*     */   long getDateMidnightMillis(int paramInt1, int paramInt2, int paramInt3)
/*     */     throws IllegalArgumentException
/*     */   {
/* 212 */     return super.getDateMidnightMillis(adjustYearForSet(paramInt1), paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */   boolean isLeapYear(int paramInt) {
/* 216 */     return (paramInt & 0x3) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   long calculateFirstDayOfYearMillis(int paramInt)
/*     */   {
/* 224 */     int i = paramInt - 1968;
/*     */     int j;
/* 226 */     if (i <= 0)
/*     */     {
/*     */ 
/* 229 */       j = i + 3 >> 2;
/*     */     } else {
/* 231 */       j = i >> 2;
/*     */       
/* 233 */       if (!isLeapYear(paramInt)) {
/* 234 */         j++;
/*     */       }
/*     */     }
/*     */     
/* 238 */     long l = (i * 365L + j) * 86400000L;
/*     */     
/*     */ 
/*     */ 
/* 242 */     return l - 62035200000L;
/*     */   }
/*     */   
/*     */   int getMinYear() {
/* 246 */     return -292269054;
/*     */   }
/*     */   
/*     */   int getMaxYear() {
/* 250 */     return 292272992;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerYear() {
/* 254 */     return 31557600000L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerYearDividedByTwo() {
/* 258 */     return 15778800000L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerMonth() {
/* 262 */     return 2629800000L;
/*     */   }
/*     */   
/*     */   long getApproxMillisAtEpochDividedByTwo() {
/* 266 */     return 31083663600000L;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields) {
/* 270 */     if (getBase() == null) {
/* 271 */       super.assemble(paramFields);
/*     */       
/* 273 */       paramFields.year = new SkipDateTimeField(this, paramFields.year);
/* 274 */       paramFields.weekyear = new SkipDateTimeField(this, paramFields.weekyear);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\JulianChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */