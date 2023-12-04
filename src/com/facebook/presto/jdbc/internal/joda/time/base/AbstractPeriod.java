/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.ISOPeriodFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
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
/*     */ public abstract class AbstractPeriod
/*     */   implements ReadablePeriod
/*     */ {
/*     */   public int size()
/*     */   {
/*  56 */     return getPeriodType().size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType(int paramInt)
/*     */   {
/*  68 */     return getPeriodType().getFieldType(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType[] getFieldTypes()
/*     */   {
/*  79 */     DurationFieldType[] arrayOfDurationFieldType = new DurationFieldType[size()];
/*  80 */     for (int i = 0; i < arrayOfDurationFieldType.length; i++) {
/*  81 */       arrayOfDurationFieldType[i] = getFieldType(i);
/*     */     }
/*  83 */     return arrayOfDurationFieldType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getValues()
/*     */   {
/*  95 */     int[] arrayOfInt = new int[size()];
/*  96 */     for (int i = 0; i < arrayOfInt.length; i++) {
/*  97 */       arrayOfInt[i] = getValue(i);
/*     */     }
/*  99 */     return arrayOfInt;
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
/*     */   public int get(DurationFieldType paramDurationFieldType)
/*     */   {
/* 113 */     int i = indexOf(paramDurationFieldType);
/* 114 */     if (i == -1) {
/* 115 */       return 0;
/*     */     }
/* 117 */     return getValue(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupported(DurationFieldType paramDurationFieldType)
/*     */   {
/* 127 */     return getPeriodType().isSupported(paramDurationFieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int indexOf(DurationFieldType paramDurationFieldType)
/*     */   {
/* 137 */     return getPeriodType().indexOf(paramDurationFieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Period toPeriod()
/*     */   {
/* 147 */     return new Period(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MutablePeriod toMutablePeriod()
/*     */   {
/* 158 */     return new MutablePeriod(this);
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 183 */     if (this == paramObject) {
/* 184 */       return true;
/*     */     }
/* 186 */     if (!(paramObject instanceof ReadablePeriod)) {
/* 187 */       return false;
/*     */     }
/* 189 */     ReadablePeriod localReadablePeriod = (ReadablePeriod)paramObject;
/* 190 */     if (size() != localReadablePeriod.size()) {
/* 191 */       return false;
/*     */     }
/* 193 */     int i = 0; for (int j = size(); i < j; i++) {
/* 194 */       if ((getValue(i) != localReadablePeriod.getValue(i)) || (getFieldType(i) != localReadablePeriod.getFieldType(i))) {
/* 195 */         return false;
/*     */       }
/*     */     }
/* 198 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 207 */     int i = 17;
/* 208 */     int j = 0; for (int k = size(); j < k; j++) {
/* 209 */       i = 27 * i + getValue(j);
/* 210 */       i = 27 * i + getFieldType(j).hashCode();
/*     */     }
/* 212 */     return i;
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
/*     */   @ToString
/*     */   public String toString()
/*     */   {
/* 228 */     return ISOPeriodFormat.standard().print(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(PeriodFormatter paramPeriodFormatter)
/*     */   {
/* 240 */     if (paramPeriodFormatter == null) {
/* 241 */       return toString();
/*     */     }
/* 243 */     return paramPeriodFormatter.print(this);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\AbstractPeriod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */