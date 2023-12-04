/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
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
/*     */ public final class CopticChronology
/*     */   extends BasicFixedMonthChronology
/*     */ {
/*     */   private static final long serialVersionUID = -5972804258688333942L;
/*     */   public static final int AM = 1;
/*  62 */   private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("AM");
/*     */   
/*     */ 
/*     */   private static final int MIN_YEAR = -292269337;
/*     */   
/*     */ 
/*     */   private static final int MAX_YEAR = 292272708;
/*     */   
/*     */ 
/*  71 */   private static final ConcurrentHashMap<DateTimeZone, CopticChronology[]> cCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private static final CopticChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CopticChronology getInstanceUTC()
/*     */   {
/*  88 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CopticChronology getInstance()
/*     */   {
/*  97 */     return getInstance(DateTimeZone.getDefault(), 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CopticChronology getInstance(DateTimeZone paramDateTimeZone)
/*     */   {
/* 107 */     return getInstance(paramDateTimeZone, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CopticChronology getInstance(DateTimeZone paramDateTimeZone, int paramInt)
/*     */   {
/* 118 */     if (paramDateTimeZone == null) {
/* 119 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*     */     
/* 122 */     Object localObject1 = (CopticChronology[])cCache.get(paramDateTimeZone);
/* 123 */     if (localObject1 == null) {
/* 124 */       localObject1 = new CopticChronology[7];
/* 125 */       CopticChronology[] arrayOfCopticChronology = (CopticChronology[])cCache.putIfAbsent(paramDateTimeZone, localObject1);
/* 126 */       if (arrayOfCopticChronology != null)
/* 127 */         localObject1 = arrayOfCopticChronology;
/*     */     }
/*     */     CopticChronology localCopticChronology;
/*     */     try {
/* 131 */       localCopticChronology = localObject1[(paramInt - 1)];
/*     */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 133 */       throw new IllegalArgumentException("Invalid min days in first week: " + paramInt);
/*     */     }
/*     */     
/* 136 */     if (localCopticChronology == null) {
/* 137 */       synchronized (localObject1) {
/* 138 */         localCopticChronology = localObject1[(paramInt - 1)];
/* 139 */         if (localCopticChronology == null) {
/* 140 */           if (paramDateTimeZone == DateTimeZone.UTC)
/*     */           {
/* 142 */             localCopticChronology = new CopticChronology(null, null, paramInt);
/*     */             
/* 144 */             DateTime localDateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, localCopticChronology);
/* 145 */             localCopticChronology = new CopticChronology(LimitChronology.getInstance(localCopticChronology, localDateTime, null), null, paramInt);
/*     */           }
/*     */           else
/*     */           {
/* 149 */             localCopticChronology = getInstance(DateTimeZone.UTC, paramInt);
/* 150 */             localCopticChronology = new CopticChronology(ZonedChronology.getInstance(localCopticChronology, paramDateTimeZone), null, paramInt);
/*     */           }
/*     */           
/* 153 */           localObject1[(paramInt - 1)] = localCopticChronology;
/*     */         }
/*     */       }
/*     */     }
/* 157 */     return localCopticChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   CopticChronology(Chronology paramChronology, Object paramObject, int paramInt)
/*     */   {
/* 166 */     super(paramChronology, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 173 */     Chronology localChronology = getBase();
/* 174 */     int i = getMinimumDaysInFirstWeek();
/* 175 */     i = i == 0 ? 4 : i;
/* 176 */     return localChronology == null ? getInstance(DateTimeZone.UTC, i) : getInstance(localChronology.getZone(), i);
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
/*     */   public Chronology withUTC()
/*     */   {
/* 189 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 199 */     if (paramDateTimeZone == null) {
/* 200 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 202 */     if (paramDateTimeZone == getZone()) {
/* 203 */       return this;
/*     */     }
/* 205 */     return getInstance(paramDateTimeZone);
/*     */   }
/*     */   
/*     */ 
/*     */   boolean isLeapDay(long paramLong)
/*     */   {
/* 211 */     return (dayOfMonth().get(paramLong) == 6) && (monthOfYear().isLeap(paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long calculateFirstDayOfYearMillis(int paramInt)
/*     */   {
/* 220 */     int i = paramInt - 1687;
/*     */     int j;
/* 222 */     if (i <= 0)
/*     */     {
/*     */ 
/* 225 */       j = i + 3 >> 2;
/*     */     } else {
/* 227 */       j = i >> 2;
/*     */       
/* 229 */       if (!isLeapYear(paramInt)) {
/* 230 */         j++;
/*     */       }
/*     */     }
/*     */     
/* 234 */     long l = (i * 365L + j) * 86400000L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 239 */     return l + 21859200000L;
/*     */   }
/*     */   
/*     */   int getMinYear()
/*     */   {
/* 244 */     return -292269337;
/*     */   }
/*     */   
/*     */   int getMaxYear()
/*     */   {
/* 249 */     return 292272708;
/*     */   }
/*     */   
/*     */   long getApproxMillisAtEpochDividedByTwo()
/*     */   {
/* 254 */     return 26607895200000L;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields)
/*     */   {
/* 259 */     if (getBase() == null) {
/* 260 */       super.assemble(paramFields);
/*     */       
/*     */ 
/* 263 */       paramFields.year = new SkipDateTimeField(this, paramFields.year);
/* 264 */       paramFields.weekyear = new SkipDateTimeField(this, paramFields.weekyear);
/*     */       
/* 266 */       paramFields.era = ERA_FIELD;
/* 267 */       paramFields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 13);
/* 268 */       paramFields.months = paramFields.monthOfYear.getDurationField();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\CopticChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */