/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDuration;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.FormatUtils;
/*     */ import org.joda.convert.ToString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractDuration
/*     */   implements ReadableDuration
/*     */ {
/*     */   public Duration toDuration()
/*     */   {
/*  54 */     return new Duration(getMillis());
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
/*     */   public Period toPeriod()
/*     */   {
/*  80 */     return new Period(getMillis());
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
/*     */   public int compareTo(ReadableDuration paramReadableDuration)
/*     */   {
/*  93 */     long l1 = getMillis();
/*  94 */     long l2 = paramReadableDuration.getMillis();
/*     */     
/*     */ 
/*  97 */     if (l1 < l2) {
/*  98 */       return -1;
/*     */     }
/* 100 */     if (l1 > l2) {
/* 101 */       return 1;
/*     */     }
/* 103 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEqual(ReadableDuration paramReadableDuration)
/*     */   {
/* 113 */     if (paramReadableDuration == null) {
/* 114 */       paramReadableDuration = Duration.ZERO;
/*     */     }
/* 116 */     return compareTo(paramReadableDuration) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLongerThan(ReadableDuration paramReadableDuration)
/*     */   {
/* 126 */     if (paramReadableDuration == null) {
/* 127 */       paramReadableDuration = Duration.ZERO;
/*     */     }
/* 129 */     return compareTo(paramReadableDuration) > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isShorterThan(ReadableDuration paramReadableDuration)
/*     */   {
/* 139 */     if (paramReadableDuration == null) {
/* 140 */       paramReadableDuration = Duration.ZERO;
/*     */     }
/* 142 */     return compareTo(paramReadableDuration) < 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 154 */     if (this == paramObject) {
/* 155 */       return true;
/*     */     }
/* 157 */     if (!(paramObject instanceof ReadableDuration)) {
/* 158 */       return false;
/*     */     }
/* 160 */     ReadableDuration localReadableDuration = (ReadableDuration)paramObject;
/* 161 */     return getMillis() == localReadableDuration.getMillis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 171 */     long l = getMillis();
/* 172 */     return (int)(l ^ l >>> 32);
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
/*     */   @ToString
/*     */   public String toString()
/*     */   {
/* 189 */     long l = getMillis();
/* 190 */     StringBuffer localStringBuffer = new StringBuffer();
/* 191 */     localStringBuffer.append("PT");
/* 192 */     int i = l < 0L ? 1 : 0;
/* 193 */     FormatUtils.appendUnpaddedInteger(localStringBuffer, l);
/* 194 */     while (localStringBuffer.length() < (i != 0 ? 7 : 6)) {
/* 195 */       localStringBuffer.insert(i != 0 ? 3 : 2, "0");
/*     */     }
/* 197 */     if (l / 1000L * 1000L == l) {
/* 198 */       localStringBuffer.setLength(localStringBuffer.length() - 3);
/*     */     } else {
/* 200 */       localStringBuffer.insert(localStringBuffer.length() - 3, ".");
/*     */     }
/* 202 */     localStringBuffer.append('S');
/* 203 */     return localStringBuffer.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\AbstractDuration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */