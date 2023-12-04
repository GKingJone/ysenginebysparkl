/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractPartial
/*     */   implements ReadablePartial, Comparable<ReadablePartial>
/*     */ {
/*     */   protected abstract DateTimeField getField(int paramInt, Chronology paramChronology);
/*     */   
/*     */   public DateTimeFieldType getFieldType(int paramInt)
/*     */   {
/*  79 */     return getField(paramInt, getChronology()).getType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFieldType[] getFieldTypes()
/*     */   {
/*  90 */     DateTimeFieldType[] arrayOfDateTimeFieldType = new DateTimeFieldType[size()];
/*  91 */     for (int i = 0; i < arrayOfDateTimeFieldType.length; i++) {
/*  92 */       arrayOfDateTimeFieldType[i] = getFieldType(i);
/*     */     }
/*  94 */     return arrayOfDateTimeFieldType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField getField(int paramInt)
/*     */   {
/* 105 */     return getField(paramInt, getChronology());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField[] getFields()
/*     */   {
/* 116 */     DateTimeField[] arrayOfDateTimeField = new DateTimeField[size()];
/* 117 */     for (int i = 0; i < arrayOfDateTimeField.length; i++) {
/* 118 */       arrayOfDateTimeField[i] = getField(i);
/*     */     }
/* 120 */     return arrayOfDateTimeField;
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
/* 132 */     int[] arrayOfInt = new int[size()];
/* 133 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 134 */       arrayOfInt[i] = getValue(i);
/*     */     }
/* 136 */     return arrayOfInt;
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
/*     */   public int get(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 150 */     return getValue(indexOfSupported(paramDateTimeFieldType));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupported(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 160 */     return indexOf(paramDateTimeFieldType) != -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int indexOf(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 170 */     int i = 0; for (int j = size(); i < j; i++) {
/* 171 */       if (getFieldType(i) == paramDateTimeFieldType) {
/* 172 */         return i;
/*     */       }
/*     */     }
/* 175 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int indexOfSupported(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 187 */     int i = indexOf(paramDateTimeFieldType);
/* 188 */     if (i == -1) {
/* 189 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*     */     }
/* 191 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int indexOf(DurationFieldType paramDurationFieldType)
/*     */   {
/* 202 */     int i = 0; for (int j = size(); i < j; i++) {
/* 203 */       if (getFieldType(i).getDurationType() == paramDurationFieldType) {
/* 204 */         return i;
/*     */       }
/*     */     }
/* 207 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int indexOfSupported(DurationFieldType paramDurationFieldType)
/*     */   {
/* 219 */     int i = indexOf(paramDurationFieldType);
/* 220 */     if (i == -1) {
/* 221 */       throw new IllegalArgumentException("Field '" + paramDurationFieldType + "' is not supported");
/*     */     }
/* 223 */     return i;
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
/*     */   public DateTime toDateTime(ReadableInstant paramReadableInstant)
/*     */   {
/* 240 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 241 */     long l1 = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 242 */     long l2 = localChronology.set(this, l1);
/* 243 */     return new DateTime(l2, localChronology);
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
/* 255 */     if (this == paramObject) {
/* 256 */       return true;
/*     */     }
/* 258 */     if (!(paramObject instanceof ReadablePartial)) {
/* 259 */       return false;
/*     */     }
/* 261 */     ReadablePartial localReadablePartial = (ReadablePartial)paramObject;
/* 262 */     if (size() != localReadablePartial.size()) {
/* 263 */       return false;
/*     */     }
/* 265 */     int i = 0; for (int j = size(); i < j; i++) {
/* 266 */       if ((getValue(i) != localReadablePartial.getValue(i)) || (getFieldType(i) != localReadablePartial.getFieldType(i))) {
/* 267 */         return false;
/*     */       }
/*     */     }
/* 270 */     return FieldUtils.equals(getChronology(), localReadablePartial.getChronology());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 280 */     int i = 157;
/* 281 */     int j = 0; for (int k = size(); j < k; j++) {
/* 282 */       i = 23 * i + getValue(j);
/* 283 */       i = 23 * i + getFieldType(j).hashCode();
/*     */     }
/* 285 */     i += getChronology().hashCode();
/* 286 */     return i;
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
/*     */   public int compareTo(ReadablePartial paramReadablePartial)
/*     */   {
/* 311 */     if (this == paramReadablePartial) {
/* 312 */       return 0;
/*     */     }
/* 314 */     if (size() != paramReadablePartial.size()) {
/* 315 */       throw new ClassCastException("ReadablePartial objects must have matching field types");
/*     */     }
/* 317 */     int i = 0; for (int j = size(); i < j; i++) {
/* 318 */       if (getFieldType(i) != paramReadablePartial.getFieldType(i)) {
/* 319 */         throw new ClassCastException("ReadablePartial objects must have matching field types");
/*     */       }
/*     */     }
/*     */     
/* 323 */     i = 0; for (j = size(); i < j; i++) {
/* 324 */       if (getValue(i) > paramReadablePartial.getValue(i)) {
/* 325 */         return 1;
/*     */       }
/* 327 */       if (getValue(i) < paramReadablePartial.getValue(i)) {
/* 328 */         return -1;
/*     */       }
/*     */     }
/* 331 */     return 0;
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
/*     */   public boolean isAfter(ReadablePartial paramReadablePartial)
/*     */   {
/* 350 */     if (paramReadablePartial == null) {
/* 351 */       throw new IllegalArgumentException("Partial cannot be null");
/*     */     }
/* 353 */     return compareTo(paramReadablePartial) > 0;
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
/*     */   public boolean isBefore(ReadablePartial paramReadablePartial)
/*     */   {
/* 372 */     if (paramReadablePartial == null) {
/* 373 */       throw new IllegalArgumentException("Partial cannot be null");
/*     */     }
/* 375 */     return compareTo(paramReadablePartial) < 0;
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
/*     */   public boolean isEqual(ReadablePartial paramReadablePartial)
/*     */   {
/* 394 */     if (paramReadablePartial == null) {
/* 395 */       throw new IllegalArgumentException("Partial cannot be null");
/*     */     }
/* 397 */     return compareTo(paramReadablePartial) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(DateTimeFormatter paramDateTimeFormatter)
/*     */   {
/* 409 */     if (paramDateTimeFormatter == null) {
/* 410 */       return toString();
/*     */     }
/* 412 */     return paramDateTimeFormatter.print(this);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\AbstractPartial.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */