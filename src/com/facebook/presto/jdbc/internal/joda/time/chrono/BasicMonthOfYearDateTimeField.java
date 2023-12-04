/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class BasicMonthOfYearDateTimeField
/*     */   extends ImpreciseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -8258715387168736L;
/*     */   private static final int MIN = 1;
/*     */   private final BasicChronology iChronology;
/*     */   private final int iMax;
/*     */   private final int iLeapMonth;
/*     */   
/*     */   BasicMonthOfYearDateTimeField(BasicChronology paramBasicChronology, int paramInt)
/*     */   {
/*  52 */     super(DateTimeFieldType.monthOfYear(), paramBasicChronology.getAverageMillisPerMonth());
/*  53 */     this.iChronology = paramBasicChronology;
/*  54 */     this.iMax = this.iChronology.getMaxMonth();
/*  55 */     this.iLeapMonth = paramInt;
/*     */   }
/*     */   
/*     */   public boolean isLenient()
/*     */   {
/*  60 */     return false;
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
/*     */   public int get(long paramLong)
/*     */   {
/*  73 */     return this.iChronology.getMonthOfYear(paramLong);
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
/*     */   public long add(long paramLong, int paramInt)
/*     */   {
/*  93 */     if (paramInt == 0) {
/*  94 */       return paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  99 */     long l1 = this.iChronology.getMillisOfDay(paramLong);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 104 */     int i = this.iChronology.getYear(paramLong);
/* 105 */     int j = this.iChronology.getMonthOfYear(paramLong, i);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 113 */     int k = j - 1 + paramInt;
/* 114 */     int m; if (k >= 0) {
/* 115 */       m = i + k / this.iMax;
/* 116 */       k = k % this.iMax + 1;
/*     */     } else {
/* 118 */       m = i + k / this.iMax - 1;
/* 119 */       k = Math.abs(k);
/* 120 */       n = k % this.iMax;
/*     */       
/* 122 */       if (n == 0) {
/* 123 */         n = this.iMax;
/*     */       }
/* 125 */       k = this.iMax - n + 1;
/*     */       
/* 127 */       if (k == 1) {
/* 128 */         m++;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */     int n = this.iChronology.getDayOfMonth(paramLong, i, j);
/* 138 */     int i1 = this.iChronology.getDaysInYearMonth(m, k);
/* 139 */     if (n > i1) {
/* 140 */       n = i1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 145 */     long l2 = this.iChronology.getYearMonthDayMillis(m, k, n);
/*     */     
/* 147 */     return l2 + l1;
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2)
/*     */   {
/* 152 */     int i = (int)paramLong2;
/* 153 */     if (i == paramLong2) {
/* 154 */       return add(paramLong1, i);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 159 */     long l1 = this.iChronology.getMillisOfDay(paramLong1);
/*     */     
/* 161 */     int j = this.iChronology.getYear(paramLong1);
/* 162 */     int k = this.iChronology.getMonthOfYear(paramLong1, j);
/*     */     
/*     */ 
/* 165 */     long l2 = k - 1 + paramLong2;
/* 166 */     long l3; if (l2 >= 0L) {
/* 167 */       l3 = j + l2 / this.iMax;
/* 168 */       l2 = l2 % this.iMax + 1L;
/*     */     } else {
/* 170 */       l3 = j + l2 / this.iMax - 1L;
/* 171 */       l2 = Math.abs(l2);
/* 172 */       m = (int)(l2 % this.iMax);
/* 173 */       if (m == 0) {
/* 174 */         m = this.iMax;
/*     */       }
/* 176 */       l2 = this.iMax - m + 1;
/* 177 */       if (l2 == 1L) {
/* 178 */         l3 += 1L;
/*     */       }
/*     */     }
/*     */     
/* 182 */     if ((l3 < this.iChronology.getMinYear()) || (l3 > this.iChronology.getMaxYear()))
/*     */     {
/*     */ 
/* 185 */       throw new IllegalArgumentException("Magnitude of add amount is too large: " + paramLong2);
/*     */     }
/*     */     
/*     */ 
/* 189 */     int m = (int)l3;
/* 190 */     int n = (int)l2;
/*     */     
/* 192 */     int i1 = this.iChronology.getDayOfMonth(paramLong1, j, k);
/* 193 */     int i2 = this.iChronology.getDaysInYearMonth(m, n);
/* 194 */     if (i1 > i2) {
/* 195 */       i1 = i2;
/*     */     }
/*     */     
/* 198 */     long l4 = this.iChronology.getYearMonthDayMillis(m, n, i1);
/*     */     
/* 200 */     return l4 + l1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 207 */     if (paramInt2 == 0) {
/* 208 */       return paramArrayOfInt;
/*     */     }
/* 210 */     if ((paramReadablePartial.size() > 0) && (paramReadablePartial.getFieldType(0).equals(DateTimeFieldType.monthOfYear())) && (paramInt1 == 0))
/*     */     {
/* 212 */       int i = paramArrayOfInt[0] - 1;
/* 213 */       int j = (i + paramInt2 % 12 + 12) % 12 + 1;
/* 214 */       return set(paramReadablePartial, 0, paramArrayOfInt, j);
/*     */     }
/* 216 */     if (DateTimeUtils.isContiguous(paramReadablePartial)) {
/* 217 */       long l = 0L;
/* 218 */       int k = 0; for (int m = paramReadablePartial.size(); k < m; k++) {
/* 219 */         l = paramReadablePartial.getFieldType(k).getField(this.iChronology).set(l, paramArrayOfInt[k]);
/*     */       }
/* 221 */       l = add(l, paramInt2);
/* 222 */       return this.iChronology.get(paramReadablePartial, l);
/*     */     }
/* 224 */     return super.add(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
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
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/* 239 */     return set(paramLong, FieldUtils.getWrappedValue(get(paramLong), paramInt, 1, this.iMax));
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */   {
/* 244 */     if (paramLong1 < paramLong2) {
/* 245 */       return -getDifference(paramLong2, paramLong1);
/*     */     }
/*     */     
/* 248 */     int i = this.iChronology.getYear(paramLong1);
/* 249 */     int j = this.iChronology.getMonthOfYear(paramLong1, i);
/* 250 */     int k = this.iChronology.getYear(paramLong2);
/* 251 */     int m = this.iChronology.getMonthOfYear(paramLong2, k);
/*     */     
/* 253 */     long l1 = (i - k) * this.iMax + j - m;
/*     */     
/*     */ 
/*     */ 
/* 257 */     int n = this.iChronology.getDayOfMonth(paramLong1, i, j);
/*     */     
/* 259 */     if (n == this.iChronology.getDaysInYearMonth(i, j))
/*     */     {
/* 261 */       int i1 = this.iChronology.getDayOfMonth(paramLong2, k, m);
/*     */       
/* 263 */       if (i1 > n)
/*     */       {
/*     */ 
/*     */ 
/* 267 */         paramLong2 = this.iChronology.dayOfMonth().set(paramLong2, n);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 272 */     long l2 = paramLong1 - this.iChronology.getYearMonthMillis(i, j);
/*     */     
/* 274 */     long l3 = paramLong2 - this.iChronology.getYearMonthMillis(k, m);
/*     */     
/*     */ 
/* 277 */     if (l2 < l3) {
/* 278 */       l1 -= 1L;
/*     */     }
/*     */     
/* 281 */     return l1;
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
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/* 299 */     FieldUtils.verifyValueBounds(this, paramInt, 1, this.iMax);
/*     */     
/* 301 */     int i = this.iChronology.getYear(paramLong);
/*     */     
/* 303 */     int j = this.iChronology.getDayOfMonth(paramLong, i);
/* 304 */     int k = this.iChronology.getDaysInYearMonth(i, paramInt);
/* 305 */     if (j > k)
/*     */     {
/* 307 */       j = k;
/*     */     }
/*     */     
/* 310 */     return this.iChronology.getYearMonthDayMillis(i, paramInt, j) + this.iChronology.getMillisOfDay(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 316 */     return this.iChronology.years();
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong)
/*     */   {
/* 321 */     int i = this.iChronology.getYear(paramLong);
/* 322 */     if (this.iChronology.isLeapYear(i)) {
/* 323 */       return this.iChronology.getMonthOfYear(paramLong, i) == this.iLeapMonth;
/*     */     }
/* 325 */     return false;
/*     */   }
/*     */   
/*     */   public int getLeapAmount(long paramLong)
/*     */   {
/* 330 */     return isLeap(paramLong) ? 1 : 0;
/*     */   }
/*     */   
/*     */   public DurationField getLeapDurationField()
/*     */   {
/* 335 */     return this.iChronology.days();
/*     */   }
/*     */   
/*     */   public int getMinimumValue()
/*     */   {
/* 340 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumValue()
/*     */   {
/* 345 */     return this.iMax;
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong)
/*     */   {
/* 350 */     int i = this.iChronology.getYear(paramLong);
/* 351 */     int j = this.iChronology.getMonthOfYear(paramLong, i);
/* 352 */     return this.iChronology.getYearMonthMillis(i, j);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong)
/*     */   {
/* 357 */     return paramLong - roundFloor(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 365 */     return this.iChronology.monthOfYear();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicMonthOfYearDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */