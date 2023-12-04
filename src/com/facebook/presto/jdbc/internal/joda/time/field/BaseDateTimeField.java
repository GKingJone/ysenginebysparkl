/*      */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*      */ import java.util.Locale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class BaseDateTimeField
/*      */   extends DateTimeField
/*      */ {
/*      */   private final DateTimeFieldType iType;
/*      */   
/*      */   protected BaseDateTimeField(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*   51 */     if (paramDateTimeFieldType == null) {
/*   52 */       throw new IllegalArgumentException("The type must not be null");
/*      */     }
/*   54 */     this.iType = paramDateTimeFieldType;
/*      */   }
/*      */   
/*      */   public final DateTimeFieldType getType() {
/*   58 */     return this.iType;
/*      */   }
/*      */   
/*      */   public final String getName() {
/*   62 */     return this.iType.getName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public final boolean isSupported()
/*      */   {
/*   69 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int get(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsText(long paramLong, Locale paramLocale)
/*      */   {
/*   94 */     return getAsText(get(paramLong), paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getAsText(long paramLong)
/*      */   {
/*  106 */     return getAsText(paramLong, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale)
/*      */   {
/*  121 */     return getAsText(paramInt, paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getAsText(ReadablePartial paramReadablePartial, Locale paramLocale)
/*      */   {
/*  136 */     return getAsText(paramReadablePartial, paramReadablePartial.get(getType()), paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsText(int paramInt, Locale paramLocale)
/*      */   {
/*  153 */     return Integer.toString(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsShortText(long paramLong, Locale paramLocale)
/*      */   {
/*  168 */     return getAsShortText(get(paramLong), paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getAsShortText(long paramLong)
/*      */   {
/*  180 */     return getAsShortText(paramLong, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsShortText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale)
/*      */   {
/*  195 */     return getAsShortText(paramInt, paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getAsShortText(ReadablePartial paramReadablePartial, Locale paramLocale)
/*      */   {
/*  210 */     return getAsShortText(paramReadablePartial, paramReadablePartial.get(getType()), paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getAsShortText(int paramInt, Locale paramLocale)
/*      */   {
/*  227 */     return getAsText(paramInt, paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long add(long paramLong, int paramInt)
/*      */   {
/*  253 */     return getDurationField().add(paramLong, paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long add(long paramLong1, long paramLong2)
/*      */   {
/*  267 */     return getDurationField().add(paramLong1, paramLong2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*      */   {
/*  298 */     if (paramInt2 == 0) {
/*  299 */       return paramArrayOfInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  304 */     DateTimeField localDateTimeField = null;
/*      */     int i;
/*  306 */     long l; while (paramInt2 > 0) {
/*  307 */       i = getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*  308 */       l = paramArrayOfInt[paramInt1] + paramInt2;
/*  309 */       if (l <= i) {
/*  310 */         paramArrayOfInt[paramInt1] = ((int)l);
/*  311 */         break;
/*      */       }
/*  313 */       if (localDateTimeField == null) {
/*  314 */         if (paramInt1 == 0) {
/*  315 */           throw new IllegalArgumentException("Maximum value exceeded for add");
/*      */         }
/*  317 */         localDateTimeField = paramReadablePartial.getField(paramInt1 - 1);
/*      */         
/*  319 */         if (getRangeDurationField().getType() != localDateTimeField.getDurationField().getType()) {
/*  320 */           throw new IllegalArgumentException("Fields invalid for add");
/*      */         }
/*      */       }
/*  323 */       paramInt2 -= i + 1 - paramArrayOfInt[paramInt1];
/*  324 */       paramArrayOfInt = localDateTimeField.add(paramReadablePartial, paramInt1 - 1, paramArrayOfInt, 1);
/*  325 */       paramArrayOfInt[paramInt1] = getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*      */     }
/*  327 */     while (paramInt2 < 0) {
/*  328 */       i = getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*  329 */       l = paramArrayOfInt[paramInt1] + paramInt2;
/*  330 */       if (l >= i) {
/*  331 */         paramArrayOfInt[paramInt1] = ((int)l);
/*  332 */         break;
/*      */       }
/*  334 */       if (localDateTimeField == null) {
/*  335 */         if (paramInt1 == 0) {
/*  336 */           throw new IllegalArgumentException("Maximum value exceeded for add");
/*      */         }
/*  338 */         localDateTimeField = paramReadablePartial.getField(paramInt1 - 1);
/*  339 */         if (getRangeDurationField().getType() != localDateTimeField.getDurationField().getType()) {
/*  340 */           throw new IllegalArgumentException("Fields invalid for add");
/*      */         }
/*      */       }
/*  343 */       paramInt2 -= i - 1 - paramArrayOfInt[paramInt1];
/*  344 */       paramArrayOfInt = localDateTimeField.add(paramReadablePartial, paramInt1 - 1, paramArrayOfInt, -1);
/*  345 */       paramArrayOfInt[paramInt1] = getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*      */     }
/*      */     
/*  348 */     return set(paramReadablePartial, paramInt1, paramArrayOfInt, paramArrayOfInt[paramInt1]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] addWrapPartial(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*      */   {
/*  379 */     if (paramInt2 == 0) {
/*  380 */       return paramArrayOfInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  385 */     DateTimeField localDateTimeField = null;
/*      */     int i;
/*  387 */     long l; while (paramInt2 > 0) {
/*  388 */       i = getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*  389 */       l = paramArrayOfInt[paramInt1] + paramInt2;
/*  390 */       if (l <= i) {
/*  391 */         paramArrayOfInt[paramInt1] = ((int)l);
/*  392 */         break;
/*      */       }
/*  394 */       if (localDateTimeField == null) {
/*  395 */         if (paramInt1 == 0) {
/*  396 */           paramInt2 -= i + 1 - paramArrayOfInt[paramInt1];
/*  397 */           paramArrayOfInt[paramInt1] = getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*  398 */           continue;
/*      */         }
/*  400 */         localDateTimeField = paramReadablePartial.getField(paramInt1 - 1);
/*      */         
/*  402 */         if (getRangeDurationField().getType() != localDateTimeField.getDurationField().getType()) {
/*  403 */           throw new IllegalArgumentException("Fields invalid for add");
/*      */         }
/*      */       }
/*  406 */       paramInt2 -= i + 1 - paramArrayOfInt[paramInt1];
/*  407 */       paramArrayOfInt = localDateTimeField.addWrapPartial(paramReadablePartial, paramInt1 - 1, paramArrayOfInt, 1);
/*  408 */       paramArrayOfInt[paramInt1] = getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*      */     }
/*  410 */     while (paramInt2 < 0) {
/*  411 */       i = getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*  412 */       l = paramArrayOfInt[paramInt1] + paramInt2;
/*  413 */       if (l >= i) {
/*  414 */         paramArrayOfInt[paramInt1] = ((int)l);
/*  415 */         break;
/*      */       }
/*  417 */       if (localDateTimeField == null) {
/*  418 */         if (paramInt1 == 0) {
/*  419 */           paramInt2 -= i - 1 - paramArrayOfInt[paramInt1];
/*  420 */           paramArrayOfInt[paramInt1] = getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*  421 */           continue;
/*      */         }
/*  423 */         localDateTimeField = paramReadablePartial.getField(paramInt1 - 1);
/*  424 */         if (getRangeDurationField().getType() != localDateTimeField.getDurationField().getType()) {
/*  425 */           throw new IllegalArgumentException("Fields invalid for add");
/*      */         }
/*      */       }
/*  428 */       paramInt2 -= i - 1 - paramArrayOfInt[paramInt1];
/*  429 */       paramArrayOfInt = localDateTimeField.addWrapPartial(paramReadablePartial, paramInt1 - 1, paramArrayOfInt, -1);
/*  430 */       paramArrayOfInt[paramInt1] = getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*      */     }
/*      */     
/*  433 */     return set(paramReadablePartial, paramInt1, paramArrayOfInt, paramArrayOfInt[paramInt1]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long addWrapField(long paramLong, int paramInt)
/*      */   {
/*  461 */     int i = get(paramLong);
/*  462 */     int j = FieldUtils.getWrappedValue(i, paramInt, getMinimumValue(paramLong), getMaximumValue(paramLong));
/*      */     
/*  464 */     return set(paramLong, j);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*      */   {
/*  495 */     int i = paramArrayOfInt[paramInt1];
/*  496 */     int j = FieldUtils.getWrappedValue(i, paramInt2, getMinimumValue(paramReadablePartial), getMaximumValue(paramReadablePartial));
/*      */     
/*  498 */     return set(paramReadablePartial, paramInt1, paramArrayOfInt, j);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDifference(long paramLong1, long paramLong2)
/*      */   {
/*  522 */     return getDurationField().getDifference(paramLong1, paramLong2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*      */   {
/*  545 */     return getDurationField().getDifferenceAsLong(paramLong1, paramLong2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long set(long paramLong, int paramInt);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] set(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*      */   {
/*  585 */     FieldUtils.verifyValueBounds(this, paramInt2, getMinimumValue(paramReadablePartial, paramArrayOfInt), getMaximumValue(paramReadablePartial, paramArrayOfInt));
/*  586 */     paramArrayOfInt[paramInt1] = paramInt2;
/*      */     
/*      */ 
/*  589 */     for (int i = paramInt1 + 1; i < paramReadablePartial.size(); i++) {
/*  590 */       DateTimeField localDateTimeField = paramReadablePartial.getField(i);
/*  591 */       if (paramArrayOfInt[i] > localDateTimeField.getMaximumValue(paramReadablePartial, paramArrayOfInt)) {
/*  592 */         paramArrayOfInt[i] = localDateTimeField.getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*      */       }
/*  594 */       if (paramArrayOfInt[i] < localDateTimeField.getMinimumValue(paramReadablePartial, paramArrayOfInt)) {
/*  595 */         paramArrayOfInt[i] = localDateTimeField.getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*      */       }
/*      */     }
/*  598 */     return paramArrayOfInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long set(long paramLong, String paramString, Locale paramLocale)
/*      */   {
/*  618 */     int i = convertText(paramString, paramLocale);
/*  619 */     return set(paramLong, i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final long set(long paramLong, String paramString)
/*      */   {
/*  635 */     return set(paramLong, paramString, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[] set(ReadablePartial paramReadablePartial, int paramInt, int[] paramArrayOfInt, String paramString, Locale paramLocale)
/*      */   {
/*  654 */     int i = convertText(paramString, paramLocale);
/*  655 */     return set(paramReadablePartial, paramInt, paramArrayOfInt, i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int convertText(String paramString, Locale paramLocale)
/*      */   {
/*      */     try
/*      */     {
/*  668 */       return Integer.parseInt(paramString);
/*      */     } catch (NumberFormatException localNumberFormatException) {
/*  670 */       throw new IllegalFieldValueException(getType(), paramString);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract DurationField getDurationField();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract DurationField getRangeDurationField();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isLeap(long paramLong)
/*      */   {
/*  704 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getLeapAmount(long paramLong)
/*      */   {
/*  716 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DurationField getLeapDurationField()
/*      */   {
/*  726 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int getMinimumValue();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinimumValue(long paramLong)
/*      */   {
/*  746 */     return getMinimumValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinimumValue(ReadablePartial paramReadablePartial)
/*      */   {
/*  758 */     return getMinimumValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt)
/*      */   {
/*  772 */     return getMinimumValue(paramReadablePartial);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int getMaximumValue();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumValue(long paramLong)
/*      */   {
/*  792 */     return getMaximumValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumValue(ReadablePartial paramReadablePartial)
/*      */   {
/*  804 */     return getMaximumValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt)
/*      */   {
/*  818 */     return getMaximumValue(paramReadablePartial);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumTextLength(Locale paramLocale)
/*      */   {
/*  829 */     int i = getMaximumValue();
/*  830 */     if (i >= 0) {
/*  831 */       if (i < 10)
/*  832 */         return 1;
/*  833 */       if (i < 100)
/*  834 */         return 2;
/*  835 */       if (i < 1000) {
/*  836 */         return 3;
/*      */       }
/*      */     }
/*  839 */     return Integer.toString(i).length();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaximumShortTextLength(Locale paramLocale)
/*      */   {
/*  850 */     return getMaximumTextLength(paramLocale);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long roundFloor(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long roundCeiling(long paramLong)
/*      */   {
/*  886 */     long l = roundFloor(paramLong);
/*  887 */     if (l != paramLong) {
/*  888 */       paramLong = add(l, 1);
/*      */     }
/*  890 */     return paramLong;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long roundHalfFloor(long paramLong)
/*      */   {
/*  903 */     long l1 = roundFloor(paramLong);
/*  904 */     long l2 = roundCeiling(paramLong);
/*      */     
/*  906 */     long l3 = paramLong - l1;
/*  907 */     long l4 = l2 - paramLong;
/*      */     
/*  909 */     if (l3 <= l4)
/*      */     {
/*  911 */       return l1;
/*      */     }
/*  913 */     return l2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long roundHalfCeiling(long paramLong)
/*      */   {
/*  927 */     long l1 = roundFloor(paramLong);
/*  928 */     long l2 = roundCeiling(paramLong);
/*      */     
/*  930 */     long l3 = paramLong - l1;
/*  931 */     long l4 = l2 - paramLong;
/*      */     
/*  933 */     if (l4 <= l3)
/*      */     {
/*  935 */       return l2;
/*      */     }
/*  937 */     return l1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long roundHalfEven(long paramLong)
/*      */   {
/*  955 */     long l1 = roundFloor(paramLong);
/*  956 */     long l2 = roundCeiling(paramLong);
/*      */     
/*  958 */     long l3 = paramLong - l1;
/*  959 */     long l4 = l2 - paramLong;
/*      */     
/*  961 */     if (l3 < l4)
/*      */     {
/*  963 */       return l1; }
/*  964 */     if (l4 < l3)
/*      */     {
/*  966 */       return l2;
/*      */     }
/*      */     
/*      */ 
/*  970 */     if ((get(l2) & 0x1) == 0) {
/*  971 */       return l2;
/*      */     }
/*  973 */     return l1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long remainder(long paramLong)
/*      */   {
/*  994 */     return paramLong - roundFloor(paramLong);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1003 */     return "DateTimeField[" + getName() + ']';
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\BaseDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */