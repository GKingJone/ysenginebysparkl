/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SkipDateTimeField
/*     */   extends DelegatedDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -8869148464118507846L;
/*     */   private final Chronology iChronology;
/*     */   private final int iSkip;
/*     */   private transient int iMinValue;
/*     */   
/*     */   public SkipDateTimeField(Chronology paramChronology, DateTimeField paramDateTimeField)
/*     */   {
/*  54 */     this(paramChronology, paramDateTimeField, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SkipDateTimeField(Chronology paramChronology, DateTimeField paramDateTimeField, int paramInt)
/*     */   {
/*  65 */     super(paramDateTimeField);
/*  66 */     this.iChronology = paramChronology;
/*  67 */     int i = super.getMinimumValue();
/*  68 */     if (i < paramInt) {
/*  69 */       this.iMinValue = (i - 1);
/*  70 */     } else if (i == paramInt) {
/*  71 */       this.iMinValue = (paramInt + 1);
/*     */     } else {
/*  73 */       this.iMinValue = i;
/*     */     }
/*  75 */     this.iSkip = paramInt;
/*     */   }
/*     */   
/*     */   public int get(long paramLong)
/*     */   {
/*  80 */     int i = super.get(paramLong);
/*  81 */     if (i <= this.iSkip) {
/*  82 */       i--;
/*     */     }
/*  84 */     return i;
/*     */   }
/*     */   
/*     */   public long set(long paramLong, int paramInt) {
/*  88 */     FieldUtils.verifyValueBounds(this, paramInt, this.iMinValue, getMaximumValue());
/*  89 */     if (paramInt <= this.iSkip) {
/*  90 */       if (paramInt == this.iSkip) {
/*  91 */         throw new IllegalFieldValueException(DateTimeFieldType.year(), Integer.valueOf(paramInt), null, null);
/*     */       }
/*     */       
/*  94 */       paramInt++;
/*     */     }
/*  96 */     return super.set(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 100 */     return this.iMinValue;
/*     */   }
/*     */   
/*     */   private Object readResolve() {
/* 104 */     return getType().getField(this.iChronology);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\SkipDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */