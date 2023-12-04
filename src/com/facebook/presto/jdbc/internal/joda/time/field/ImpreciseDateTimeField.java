/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ImpreciseDateTimeField
/*     */   extends BaseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 7190739608550251860L;
/*     */   final long iUnitMillis;
/*     */   private final DurationField iDurationField;
/*     */   
/*     */   public ImpreciseDateTimeField(DateTimeFieldType paramDateTimeFieldType, long paramLong)
/*     */   {
/*  56 */     super(paramDateTimeFieldType);
/*  57 */     this.iUnitMillis = paramLong;
/*  58 */     this.iDurationField = new LinkedDurationField(paramDateTimeFieldType.getDurationType());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract int get(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long set(long paramLong, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long add(long paramLong, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long add(long paramLong1, long paramLong2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDifference(long paramLong1, long paramLong2)
/*     */   {
/*  92 */     return FieldUtils.safeToInt(getDifferenceAsLong(paramLong1, paramLong2));
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
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */   {
/* 119 */     if (paramLong1 < paramLong2) {
/* 120 */       return -getDifferenceAsLong(paramLong2, paramLong1);
/*     */     }
/*     */     
/* 123 */     long l = (paramLong1 - paramLong2) / this.iUnitMillis;
/* 124 */     if (add(paramLong2, l) < paramLong1) {
/*     */       do {
/* 126 */         l += 1L;
/* 127 */       } while (add(paramLong2, l) <= paramLong1);
/* 128 */       l -= 1L;
/* 129 */     } else if (add(paramLong2, l) > paramLong1) {
/*     */       do {
/* 131 */         l -= 1L;
/* 132 */       } while (add(paramLong2, l) > paramLong1);
/*     */     }
/* 134 */     return l;
/*     */   }
/*     */   
/*     */   public final DurationField getDurationField() {
/* 138 */     return this.iDurationField;
/*     */   }
/*     */   
/*     */   public abstract DurationField getRangeDurationField();
/*     */   
/*     */   public abstract long roundFloor(long paramLong);
/*     */   
/*     */   protected final long getDurationUnitMillis() {
/* 146 */     return this.iUnitMillis;
/*     */   }
/*     */   
/*     */   private final class LinkedDurationField extends BaseDurationField {
/*     */     private static final long serialVersionUID = -203813474600094134L;
/*     */     
/*     */     LinkedDurationField(DurationFieldType paramDurationFieldType) {
/* 153 */       super();
/*     */     }
/*     */     
/*     */     public boolean isPrecise() {
/* 157 */       return false;
/*     */     }
/*     */     
/*     */     public long getUnitMillis() {
/* 161 */       return ImpreciseDateTimeField.this.iUnitMillis;
/*     */     }
/*     */     
/*     */     public int getValue(long paramLong1, long paramLong2) {
/* 165 */       return ImpreciseDateTimeField.this.getDifference(paramLong2 + paramLong1, paramLong2);
/*     */     }
/*     */     
/*     */     public long getValueAsLong(long paramLong1, long paramLong2)
/*     */     {
/* 170 */       return ImpreciseDateTimeField.this.getDifferenceAsLong(paramLong2 + paramLong1, paramLong2);
/*     */     }
/*     */     
/*     */     public long getMillis(int paramInt, long paramLong)
/*     */     {
/* 175 */       return ImpreciseDateTimeField.this.add(paramLong, paramInt) - paramLong;
/*     */     }
/*     */     
/*     */     public long getMillis(long paramLong1, long paramLong2) {
/* 179 */       return ImpreciseDateTimeField.this.add(paramLong2, paramLong1) - paramLong2;
/*     */     }
/*     */     
/*     */     public long add(long paramLong, int paramInt) {
/* 183 */       return ImpreciseDateTimeField.this.add(paramLong, paramInt);
/*     */     }
/*     */     
/*     */     public long add(long paramLong1, long paramLong2) {
/* 187 */       return ImpreciseDateTimeField.this.add(paramLong1, paramLong2);
/*     */     }
/*     */     
/*     */     public int getDifference(long paramLong1, long paramLong2) {
/* 191 */       return ImpreciseDateTimeField.this.getDifference(paramLong1, paramLong2);
/*     */     }
/*     */     
/*     */     public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */     {
/* 196 */       return ImpreciseDateTimeField.this.getDifferenceAsLong(paramLong1, paramLong2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\ImpreciseDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */