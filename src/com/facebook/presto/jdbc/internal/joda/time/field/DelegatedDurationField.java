/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DelegatedDurationField
/*     */   extends DurationField
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -5576443481242007829L;
/*     */   private final DurationField iField;
/*     */   private final DurationFieldType iType;
/*     */   
/*     */   protected DelegatedDurationField(DurationField paramDurationField)
/*     */   {
/*  50 */     this(paramDurationField, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DelegatedDurationField(DurationField paramDurationField, DurationFieldType paramDurationFieldType)
/*     */   {
/*  61 */     if (paramDurationField == null) {
/*  62 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/*  64 */     this.iField = paramDurationField;
/*  65 */     this.iType = (paramDurationFieldType == null ? paramDurationField.getType() : paramDurationFieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final DurationField getWrappedField()
/*     */   {
/*  75 */     return this.iField;
/*     */   }
/*     */   
/*     */   public DurationFieldType getType() {
/*  79 */     return this.iType;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  83 */     return this.iType.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSupported()
/*     */   {
/*  90 */     return this.iField.isSupported();
/*     */   }
/*     */   
/*     */   public boolean isPrecise() {
/*  94 */     return this.iField.isPrecise();
/*     */   }
/*     */   
/*     */   public int getValue(long paramLong) {
/*  98 */     return this.iField.getValue(paramLong);
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long paramLong) {
/* 102 */     return this.iField.getValueAsLong(paramLong);
/*     */   }
/*     */   
/*     */   public int getValue(long paramLong1, long paramLong2) {
/* 106 */     return this.iField.getValue(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long paramLong1, long paramLong2) {
/* 110 */     return this.iField.getValueAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getMillis(int paramInt) {
/* 114 */     return this.iField.getMillis(paramInt);
/*     */   }
/*     */   
/*     */   public long getMillis(long paramLong) {
/* 118 */     return this.iField.getMillis(paramLong);
/*     */   }
/*     */   
/*     */   public long getMillis(int paramInt, long paramLong) {
/* 122 */     return this.iField.getMillis(paramInt, paramLong);
/*     */   }
/*     */   
/*     */   public long getMillis(long paramLong1, long paramLong2) {
/* 126 */     return this.iField.getMillis(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/* 130 */     return this.iField.add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/* 134 */     return this.iField.add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public int getDifference(long paramLong1, long paramLong2) {
/* 138 */     return this.iField.getDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/* 142 */     return this.iField.getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getUnitMillis() {
/* 146 */     return this.iField.getUnitMillis();
/*     */   }
/*     */   
/*     */   public int compareTo(DurationField paramDurationField) {
/* 150 */     return this.iField.compareTo(paramDurationField);
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 154 */     if ((paramObject instanceof DelegatedDurationField)) {
/* 155 */       return this.iField.equals(((DelegatedDurationField)paramObject).iField);
/*     */     }
/* 157 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 161 */     return this.iField.hashCode() ^ this.iType.hashCode();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 165 */     return "DurationField[" + this.iType + ']';
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\DelegatedDurationField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */