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
/*     */ public final class EthiopicChronology
/*     */   extends BasicFixedMonthChronology
/*     */ {
/*     */   private static final long serialVersionUID = -5972804258688333942L;
/*     */   public static final int EE = 1;
/*  62 */   private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("EE");
/*     */   
/*     */ 
/*     */   private static final int MIN_YEAR = -292269337;
/*     */   
/*     */ 
/*     */   private static final int MAX_YEAR = 292272984;
/*     */   
/*     */ 
/*  71 */   private static final ConcurrentHashMap<DateTimeZone, EthiopicChronology[]> cCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */   private static final EthiopicChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EthiopicChronology getInstanceUTC()
/*     */   {
/*  88 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EthiopicChronology getInstance()
/*     */   {
/*  97 */     return getInstance(DateTimeZone.getDefault(), 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EthiopicChronology getInstance(DateTimeZone paramDateTimeZone)
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
/*     */   public static EthiopicChronology getInstance(DateTimeZone paramDateTimeZone, int paramInt)
/*     */   {
/* 118 */     if (paramDateTimeZone == null) {
/* 119 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*     */     
/* 122 */     Object localObject1 = (EthiopicChronology[])cCache.get(paramDateTimeZone);
/* 123 */     if (localObject1 == null) {
/* 124 */       localObject1 = new EthiopicChronology[7];
/* 125 */       EthiopicChronology[] arrayOfEthiopicChronology = (EthiopicChronology[])cCache.putIfAbsent(paramDateTimeZone, localObject1);
/* 126 */       if (arrayOfEthiopicChronology != null)
/* 127 */         localObject1 = arrayOfEthiopicChronology;
/*     */     }
/*     */     EthiopicChronology localEthiopicChronology;
/*     */     try {
/* 131 */       localEthiopicChronology = localObject1[(paramInt - 1)];
/*     */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 133 */       throw new IllegalArgumentException("Invalid min days in first week: " + paramInt);
/*     */     }
/*     */     
/*     */ 
/* 137 */     if (localEthiopicChronology == null) {
/* 138 */       synchronized (localObject1) {
/* 139 */         localEthiopicChronology = localObject1[(paramInt - 1)];
/* 140 */         if (localEthiopicChronology == null) {
/* 141 */           if (paramDateTimeZone == DateTimeZone.UTC)
/*     */           {
/* 143 */             localEthiopicChronology = new EthiopicChronology(null, null, paramInt);
/*     */             
/* 145 */             DateTime localDateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, localEthiopicChronology);
/* 146 */             localEthiopicChronology = new EthiopicChronology(LimitChronology.getInstance(localEthiopicChronology, localDateTime, null), null, paramInt);
/*     */           }
/*     */           else
/*     */           {
/* 150 */             localEthiopicChronology = getInstance(DateTimeZone.UTC, paramInt);
/* 151 */             localEthiopicChronology = new EthiopicChronology(ZonedChronology.getInstance(localEthiopicChronology, paramDateTimeZone), null, paramInt);
/*     */           }
/*     */           
/* 154 */           localObject1[(paramInt - 1)] = localEthiopicChronology;
/*     */         }
/*     */       }
/*     */     }
/* 158 */     return localEthiopicChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   EthiopicChronology(Chronology paramChronology, Object paramObject, int paramInt)
/*     */   {
/* 167 */     super(paramChronology, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 174 */     Chronology localChronology = getBase();
/* 175 */     return localChronology == null ? getInstance(DateTimeZone.UTC, getMinimumDaysInFirstWeek()) : getInstance(localChronology.getZone(), getMinimumDaysInFirstWeek());
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
/* 188 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 198 */     if (paramDateTimeZone == null) {
/* 199 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 201 */     if (paramDateTimeZone == getZone()) {
/* 202 */       return this;
/*     */     }
/* 204 */     return getInstance(paramDateTimeZone);
/*     */   }
/*     */   
/*     */ 
/*     */   boolean isLeapDay(long paramLong)
/*     */   {
/* 210 */     return (dayOfMonth().get(paramLong) == 6) && (monthOfYear().isLeap(paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long calculateFirstDayOfYearMillis(int paramInt)
/*     */   {
/* 219 */     int i = paramInt - 1963;
/*     */     int j;
/* 221 */     if (i <= 0)
/*     */     {
/*     */ 
/* 224 */       j = i + 3 >> 2;
/*     */     } else {
/* 226 */       j = i >> 2;
/*     */       
/* 228 */       if (!isLeapYear(paramInt)) {
/* 229 */         j++;
/*     */       }
/*     */     }
/*     */     
/* 233 */     long l = (i * 365L + j) * 86400000L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 238 */     return l + 21859200000L;
/*     */   }
/*     */   
/*     */   int getMinYear()
/*     */   {
/* 243 */     return -292269337;
/*     */   }
/*     */   
/*     */   int getMaxYear()
/*     */   {
/* 248 */     return 292272984;
/*     */   }
/*     */   
/*     */   long getApproxMillisAtEpochDividedByTwo()
/*     */   {
/* 253 */     return 30962844000000L;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields)
/*     */   {
/* 258 */     if (getBase() == null) {
/* 259 */       super.assemble(paramFields);
/*     */       
/*     */ 
/* 262 */       paramFields.year = new SkipDateTimeField(this, paramFields.year);
/* 263 */       paramFields.weekyear = new SkipDateTimeField(this, paramFields.weekyear);
/*     */       
/* 265 */       paramFields.era = ERA_FIELD;
/* 266 */       paramFields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 13);
/* 267 */       paramFields.months = paramFields.monthOfYear.getDurationField();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\EthiopicChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */