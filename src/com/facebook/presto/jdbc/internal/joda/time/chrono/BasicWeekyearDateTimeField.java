/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.ImpreciseDateTimeField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class BasicWeekyearDateTimeField
/*     */   extends ImpreciseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 6215066916806820644L;
/*     */   private static final long WEEK_53 = 31449600000L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   BasicWeekyearDateTimeField(BasicChronology paramBasicChronology)
/*     */   {
/*  45 */     super(DateTimeFieldType.weekyear(), paramBasicChronology.getAverageMillisPerYear());
/*  46 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */   public boolean isLenient() {
/*  50 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/*  61 */     return this.iChronology.getWeekyear(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(long paramLong, int paramInt)
/*     */   {
/*  73 */     if (paramInt == 0) {
/*  74 */       return paramLong;
/*     */     }
/*  76 */     return set(paramLong, get(paramLong) + paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  80 */     return add(paramLong1, FieldUtils.safeToInt(paramLong2));
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
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/*  93 */     return add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  97 */     if (paramLong1 < paramLong2) {
/*  98 */       return -getDifference(paramLong2, paramLong1);
/*     */     }
/*     */     
/* 101 */     int i = get(paramLong1);
/* 102 */     int j = get(paramLong2);
/*     */     
/* 104 */     long l1 = remainder(paramLong1);
/* 105 */     long l2 = remainder(paramLong2);
/*     */     
/*     */ 
/* 108 */     if ((l2 >= 31449600000L) && (this.iChronology.getWeeksInYear(i) <= 52)) {
/* 109 */       l2 -= 604800000L;
/*     */     }
/*     */     
/* 112 */     int k = i - j;
/* 113 */     if (l1 < l2) {
/* 114 */       k--;
/*     */     }
/* 116 */     return k;
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
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/* 129 */     FieldUtils.verifyValueBounds(this, Math.abs(paramInt), this.iChronology.getMinYear(), this.iChronology.getMaxYear());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 134 */     int i = get(paramLong);
/* 135 */     if (i == paramInt) {
/* 136 */       return paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 141 */     int j = this.iChronology.getDayOfWeek(paramLong);
/*     */     
/*     */ 
/*     */ 
/* 145 */     int k = this.iChronology.getWeeksInYear(i);
/* 146 */     int m = this.iChronology.getWeeksInYear(paramInt);
/* 147 */     int n = m < k ? m : k;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 155 */     int i1 = this.iChronology.getWeekOfWeekyear(paramLong);
/* 156 */     if (i1 > n) {
/* 157 */       i1 = n;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 163 */     long l = paramLong;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 169 */     l = this.iChronology.setYear(l, paramInt);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 174 */     int i2 = get(l);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */     if (i2 < paramInt) {
/* 181 */       l += 604800000L;
/* 182 */     } else if (i2 > paramInt) {
/* 183 */       l -= 604800000L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */     int i3 = this.iChronology.getWeekOfWeekyear(l);
/*     */     
/* 192 */     l += (i1 - i3) * 604800000L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 201 */     l = this.iChronology.dayOfWeek().set(l, j);
/*     */     
/*     */ 
/*     */ 
/* 205 */     return l;
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/* 209 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong) {
/* 213 */     return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(paramLong)) > 52;
/*     */   }
/*     */   
/*     */   public int getLeapAmount(long paramLong) {
/* 217 */     return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(paramLong)) - 52;
/*     */   }
/*     */   
/*     */   public DurationField getLeapDurationField() {
/* 221 */     return this.iChronology.weeks();
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 225 */     return this.iChronology.getMinYear();
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/* 229 */     return this.iChronology.getMaxYear();
/*     */   }
/*     */   
/*     */ 
/*     */   public long roundFloor(long paramLong)
/*     */   {
/* 235 */     paramLong = this.iChronology.weekOfWeekyear().roundFloor(paramLong);
/* 236 */     int i = this.iChronology.getWeekOfWeekyear(paramLong);
/* 237 */     if (i > 1) {
/* 238 */       paramLong -= 604800000L * (i - 1);
/*     */     }
/* 240 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 244 */     return paramLong - roundFloor(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 251 */     return this.iChronology.weekyear();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicWeekyearDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */