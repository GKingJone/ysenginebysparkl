/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
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
/*     */ public class DecoratedDurationField
/*     */   extends BaseDurationField
/*     */ {
/*     */   private static final long serialVersionUID = 8019982251647420015L;
/*     */   private final DurationField iField;
/*     */   
/*     */   public DecoratedDurationField(DurationField paramDurationField, DurationFieldType paramDurationFieldType)
/*     */   {
/*  52 */     super(paramDurationFieldType);
/*  53 */     if (paramDurationField == null) {
/*  54 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/*  56 */     if (!paramDurationField.isSupported()) {
/*  57 */       throw new IllegalArgumentException("The field must be supported");
/*     */     }
/*  59 */     this.iField = paramDurationField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final DurationField getWrappedField()
/*     */   {
/*  69 */     return this.iField;
/*     */   }
/*     */   
/*     */   public boolean isPrecise() {
/*  73 */     return this.iField.isPrecise();
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long paramLong1, long paramLong2) {
/*  77 */     return this.iField.getValueAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getMillis(int paramInt, long paramLong) {
/*  81 */     return this.iField.getMillis(paramInt, paramLong);
/*     */   }
/*     */   
/*     */   public long getMillis(long paramLong1, long paramLong2) {
/*  85 */     return this.iField.getMillis(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/*  89 */     return this.iField.add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  93 */     return this.iField.add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  97 */     return this.iField.getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getUnitMillis() {
/* 101 */     return this.iField.getUnitMillis();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\DecoratedDurationField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */