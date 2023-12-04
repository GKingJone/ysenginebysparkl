/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class BasicFixedMonthChronology
/*     */   extends BasicChronology
/*     */ {
/*     */   private static final long serialVersionUID = 261387371998L;
/*     */   static final int MONTH_LENGTH = 30;
/*     */   static final long MILLIS_PER_YEAR = 31557600000L;
/*     */   static final long MILLIS_PER_MONTH = 2592000000L;
/*     */   
/*     */   BasicFixedMonthChronology(Chronology paramChronology, Object paramObject, int paramInt)
/*     */   {
/*  59 */     super(paramChronology, paramObject, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */   long setYear(long paramLong, int paramInt)
/*     */   {
/*  65 */     int i = getYear(paramLong);
/*  66 */     int j = getDayOfYear(paramLong, i);
/*  67 */     int k = getMillisOfDay(paramLong);
/*     */     
/*  69 */     if (j > 365)
/*     */     {
/*  71 */       if (!isLeapYear(paramInt))
/*     */       {
/*  73 */         j--;
/*     */       }
/*     */     }
/*     */     
/*  77 */     paramLong = getYearMonthDayMillis(paramInt, 1, j);
/*  78 */     paramLong += k;
/*  79 */     return paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */   long getYearDifference(long paramLong1, long paramLong2)
/*     */   {
/*  85 */     int i = getYear(paramLong1);
/*  86 */     int j = getYear(paramLong2);
/*     */     
/*     */ 
/*  89 */     long l1 = paramLong1 - getYearMillis(i);
/*  90 */     long l2 = paramLong2 - getYearMillis(j);
/*     */     
/*  92 */     int k = i - j;
/*  93 */     if (l1 < l2) {
/*  94 */       k--;
/*     */     }
/*  96 */     return k;
/*     */   }
/*     */   
/*     */   long getTotalMillisByYearMonth(int paramInt1, int paramInt2)
/*     */   {
/* 101 */     return (paramInt2 - 1) * 2592000000L;
/*     */   }
/*     */   
/*     */ 
/*     */   int getDayOfMonth(long paramLong)
/*     */   {
/* 107 */     return (getDayOfYear(paramLong) - 1) % 30 + 1;
/*     */   }
/*     */   
/*     */   boolean isLeapYear(int paramInt)
/*     */   {
/* 112 */     return (paramInt & 0x3) == 3;
/*     */   }
/*     */   
/*     */   int getDaysInYearMonth(int paramInt1, int paramInt2)
/*     */   {
/* 117 */     return isLeapYear(paramInt1) ? 6 : paramInt2 != 13 ? 30 : 5;
/*     */   }
/*     */   
/*     */   int getDaysInMonthMax()
/*     */   {
/* 122 */     return 30;
/*     */   }
/*     */   
/*     */   int getDaysInMonthMax(int paramInt)
/*     */   {
/* 127 */     return paramInt != 13 ? 30 : 6;
/*     */   }
/*     */   
/*     */   int getMonthOfYear(long paramLong)
/*     */   {
/* 132 */     return (getDayOfYear(paramLong) - 1) / 30 + 1;
/*     */   }
/*     */   
/*     */   int getMonthOfYear(long paramLong, int paramInt)
/*     */   {
/* 137 */     long l = (paramLong - getYearMillis(paramInt)) / 2592000000L;
/* 138 */     return (int)l + 1;
/*     */   }
/*     */   
/*     */   int getMaxMonth()
/*     */   {
/* 143 */     return 13;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerYear()
/*     */   {
/* 148 */     return 31557600000L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerYearDividedByTwo()
/*     */   {
/* 153 */     return 15778800000L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerMonth()
/*     */   {
/* 158 */     return 2592000000L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicFixedMonthChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */