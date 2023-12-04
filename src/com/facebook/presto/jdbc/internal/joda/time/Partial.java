/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.AbstractPartial;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractPartialFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Partial
/*      */   extends AbstractPartial
/*      */   implements ReadablePartial, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 12324121189002L;
/*      */   private final Chronology iChronology;
/*      */   private final DateTimeFieldType[] iTypes;
/*      */   private final int[] iValues;
/*      */   private transient DateTimeFormatter[] iFormatter;
/*      */   
/*      */   public Partial()
/*      */   {
/*  103 */     this((Chronology)null);
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
/*      */   public Partial(Chronology paramChronology)
/*      */   {
/*  124 */     this.iChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  125 */     this.iTypes = new DateTimeFieldType[0];
/*  126 */     this.iValues = new int[0];
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
/*      */   public Partial(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  139 */     this(paramDateTimeFieldType, paramInt, null);
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
/*      */   public Partial(DateTimeFieldType paramDateTimeFieldType, int paramInt, Chronology paramChronology)
/*      */   {
/*  154 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  155 */     this.iChronology = paramChronology;
/*  156 */     if (paramDateTimeFieldType == null) {
/*  157 */       throw new IllegalArgumentException("The field type must not be null");
/*      */     }
/*  159 */     this.iTypes = new DateTimeFieldType[] { paramDateTimeFieldType };
/*  160 */     this.iValues = new int[] { paramInt };
/*  161 */     paramChronology.validate(this, this.iValues);
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
/*      */   public Partial(DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfInt)
/*      */   {
/*  177 */     this(paramArrayOfDateTimeFieldType, paramArrayOfInt, null);
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
/*      */   public Partial(DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfInt, Chronology paramChronology)
/*      */   {
/*  195 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  196 */     this.iChronology = paramChronology;
/*  197 */     if (paramArrayOfDateTimeFieldType == null) {
/*  198 */       throw new IllegalArgumentException("Types array must not be null");
/*      */     }
/*  200 */     if (paramArrayOfInt == null) {
/*  201 */       throw new IllegalArgumentException("Values array must not be null");
/*      */     }
/*  203 */     if (paramArrayOfInt.length != paramArrayOfDateTimeFieldType.length) {
/*  204 */       throw new IllegalArgumentException("Values array must be the same length as the types array");
/*      */     }
/*  206 */     if (paramArrayOfDateTimeFieldType.length == 0) {
/*  207 */       this.iTypes = paramArrayOfDateTimeFieldType;
/*  208 */       this.iValues = paramArrayOfInt;
/*  209 */       return;
/*      */     }
/*  211 */     for (int i = 0; i < paramArrayOfDateTimeFieldType.length; i++) {
/*  212 */       if (paramArrayOfDateTimeFieldType[i] == null) {
/*  213 */         throw new IllegalArgumentException("Types array must not contain null: index " + i);
/*      */       }
/*      */     }
/*  216 */     Object localObject = null;
/*  217 */     for (int j = 0; j < paramArrayOfDateTimeFieldType.length; j++) {
/*  218 */       DateTimeFieldType localDateTimeFieldType = paramArrayOfDateTimeFieldType[j];
/*  219 */       DurationField localDurationField1 = localDateTimeFieldType.getDurationType().getField(this.iChronology);
/*  220 */       if (j > 0) {
/*  221 */         if (!localDurationField1.isSupported()) {
/*  222 */           if (((DurationField)localObject).isSupported()) {
/*  223 */             throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " < " + localDateTimeFieldType.getName());
/*      */           }
/*      */           
/*  226 */           throw new IllegalArgumentException("Types array must not contain duplicate unsupported: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " and " + localDateTimeFieldType.getName());
/*      */         }
/*      */         
/*      */ 
/*  230 */         int k = ((DurationField)localObject).compareTo(localDurationField1);
/*  231 */         if (k < 0) {
/*  232 */           throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " < " + localDateTimeFieldType.getName());
/*      */         }
/*  234 */         if (k == 0) {
/*  235 */           if (localObject.equals(localDurationField1)) {
/*  236 */             DurationFieldType localDurationFieldType1 = paramArrayOfDateTimeFieldType[(j - 1)].getRangeDurationType();
/*  237 */             DurationFieldType localDurationFieldType2 = localDateTimeFieldType.getRangeDurationType();
/*  238 */             if (localDurationFieldType1 == null) {
/*  239 */               if (localDurationFieldType2 == null) {
/*  240 */                 throw new IllegalArgumentException("Types array must not contain duplicate: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " and " + localDateTimeFieldType.getName());
/*      */               }
/*      */             }
/*      */             else {
/*  244 */               if (localDurationFieldType2 == null) {
/*  245 */                 throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " < " + localDateTimeFieldType.getName());
/*      */               }
/*      */               
/*  248 */               DurationField localDurationField2 = localDurationFieldType1.getField(this.iChronology);
/*  249 */               DurationField localDurationField3 = localDurationFieldType2.getField(this.iChronology);
/*  250 */               if (localDurationField2.compareTo(localDurationField3) < 0) {
/*  251 */                 throw new IllegalArgumentException("Types array must be in order largest-smallest: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " < " + localDateTimeFieldType.getName());
/*      */               }
/*      */               
/*  254 */               if (localDurationField2.compareTo(localDurationField3) == 0) {
/*  255 */                 throw new IllegalArgumentException("Types array must not contain duplicate: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " and " + localDateTimeFieldType.getName());
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*  260 */           else if ((((DurationField)localObject).isSupported()) && (((DurationField)localObject).getType() != DurationFieldType.YEARS_TYPE)) {
/*  261 */             throw new IllegalArgumentException("Types array must be in order largest-smallest, for year-based fields, years is defined as being largest: " + paramArrayOfDateTimeFieldType[(j - 1)].getName() + " < " + localDateTimeFieldType.getName());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  268 */       localObject = localDurationField1;
/*      */     }
/*      */     
/*  271 */     this.iTypes = ((DateTimeFieldType[])paramArrayOfDateTimeFieldType.clone());
/*  272 */     paramChronology.validate(this, paramArrayOfInt);
/*  273 */     this.iValues = ((int[])paramArrayOfInt.clone());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Partial(ReadablePartial paramReadablePartial)
/*      */   {
/*  284 */     if (paramReadablePartial == null) {
/*  285 */       throw new IllegalArgumentException("The partial must not be null");
/*      */     }
/*  287 */     this.iChronology = DateTimeUtils.getChronology(paramReadablePartial.getChronology()).withUTC();
/*  288 */     this.iTypes = new DateTimeFieldType[paramReadablePartial.size()];
/*  289 */     this.iValues = new int[paramReadablePartial.size()];
/*  290 */     for (int i = 0; i < paramReadablePartial.size(); i++) {
/*  291 */       this.iTypes[i] = paramReadablePartial.getFieldType(i);
/*  292 */       this.iValues[i] = paramReadablePartial.getValue(i);
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
/*      */   Partial(Partial paramPartial, int[] paramArrayOfInt)
/*      */   {
/*  306 */     this.iChronology = paramPartial.iChronology;
/*  307 */     this.iTypes = paramPartial.iTypes;
/*  308 */     this.iValues = paramArrayOfInt;
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
/*      */   Partial(Chronology paramChronology, DateTimeFieldType[] paramArrayOfDateTimeFieldType, int[] paramArrayOfInt)
/*      */   {
/*  322 */     this.iChronology = paramChronology;
/*  323 */     this.iTypes = paramArrayOfDateTimeFieldType;
/*  324 */     this.iValues = paramArrayOfInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  334 */     return this.iTypes.length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology getChronology()
/*      */   {
/*  346 */     return this.iChronology;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DateTimeField getField(int paramInt, Chronology paramChronology)
/*      */   {
/*  358 */     return this.iTypes[paramInt].getField(paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFieldType getFieldType(int paramInt)
/*      */   {
/*  369 */     return this.iTypes[paramInt];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFieldType[] getFieldTypes()
/*      */   {
/*  381 */     return (DateTimeFieldType[])this.iTypes.clone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getValue(int paramInt)
/*      */   {
/*  393 */     return this.iValues[paramInt];
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
/*      */   public int[] getValues()
/*      */   {
/*  406 */     return (int[])this.iValues.clone();
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
/*      */   public Partial withChronologyRetainFields(Chronology paramChronology)
/*      */   {
/*  425 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  426 */     paramChronology = paramChronology.withUTC();
/*  427 */     if (paramChronology == getChronology()) {
/*  428 */       return this;
/*      */     }
/*  430 */     Partial localPartial = new Partial(paramChronology, this.iTypes, this.iValues);
/*  431 */     paramChronology.validate(localPartial, this.iValues);
/*  432 */     return localPartial;
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
/*      */   public Partial with(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  452 */     if (paramDateTimeFieldType == null) {
/*  453 */       throw new IllegalArgumentException("The field type must not be null");
/*      */     }
/*  455 */     int i = indexOf(paramDateTimeFieldType);
/*  456 */     if (i == -1) {
/*  457 */       localObject1 = new DateTimeFieldType[this.iTypes.length + 1];
/*  458 */       int[] arrayOfInt = new int[localObject1.length];
/*      */       
/*      */ 
/*  461 */       int j = 0;
/*  462 */       DurationField localDurationField1 = paramDateTimeFieldType.getDurationType().getField(this.iChronology);
/*  463 */       if (localDurationField1.isSupported()) {
/*  464 */         for (; j < this.iTypes.length; j++) {
/*  465 */           localObject2 = this.iTypes[j];
/*  466 */           DurationField localDurationField2 = ((DateTimeFieldType)localObject2).getDurationType().getField(this.iChronology);
/*  467 */           if (localDurationField2.isSupported()) {
/*  468 */             int k = localDurationField1.compareTo(localDurationField2);
/*  469 */             if (k > 0)
/*      */               break;
/*  471 */             if (k == 0) {
/*  472 */               if (paramDateTimeFieldType.getRangeDurationType() == null) {
/*      */                 break;
/*      */               }
/*  475 */               if (((DateTimeFieldType)localObject2).getRangeDurationType() != null)
/*      */               {
/*      */ 
/*  478 */                 DurationField localDurationField3 = paramDateTimeFieldType.getRangeDurationType().getField(this.iChronology);
/*  479 */                 DurationField localDurationField4 = ((DateTimeFieldType)localObject2).getRangeDurationType().getField(this.iChronology);
/*  480 */                 if (localDurationField3.compareTo(localDurationField4) > 0)
/*      */                   break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  487 */       System.arraycopy(this.iTypes, 0, localObject1, 0, j);
/*  488 */       System.arraycopy(this.iValues, 0, arrayOfInt, 0, j);
/*  489 */       localObject1[j] = paramDateTimeFieldType;
/*  490 */       arrayOfInt[j] = paramInt;
/*  491 */       System.arraycopy(this.iTypes, j, localObject1, j + 1, localObject1.length - j - 1);
/*  492 */       System.arraycopy(this.iValues, j, arrayOfInt, j + 1, arrayOfInt.length - j - 1);
/*      */       
/*      */ 
/*  495 */       Object localObject2 = new Partial((DateTimeFieldType[])localObject1, arrayOfInt, this.iChronology);
/*  496 */       this.iChronology.validate((ReadablePartial)localObject2, arrayOfInt);
/*  497 */       return (Partial)localObject2;
/*      */     }
/*  499 */     if (paramInt == getValue(i)) {
/*  500 */       return this;
/*      */     }
/*  502 */     Object localObject1 = getValues();
/*  503 */     localObject1 = getField(i).set(this, i, (int[])localObject1, paramInt);
/*  504 */     return new Partial(this, (int[])localObject1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Partial without(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  516 */     int i = indexOf(paramDateTimeFieldType);
/*  517 */     if (i != -1) {
/*  518 */       DateTimeFieldType[] arrayOfDateTimeFieldType = new DateTimeFieldType[size() - 1];
/*  519 */       int[] arrayOfInt = new int[size() - 1];
/*  520 */       System.arraycopy(this.iTypes, 0, arrayOfDateTimeFieldType, 0, i);
/*  521 */       System.arraycopy(this.iTypes, i + 1, arrayOfDateTimeFieldType, i, arrayOfDateTimeFieldType.length - i);
/*  522 */       System.arraycopy(this.iValues, 0, arrayOfInt, 0, i);
/*  523 */       System.arraycopy(this.iValues, i + 1, arrayOfInt, i, arrayOfInt.length - i);
/*  524 */       Partial localPartial = new Partial(this.iChronology, arrayOfDateTimeFieldType, arrayOfInt);
/*  525 */       this.iChronology.validate(localPartial, arrayOfInt);
/*  526 */       return localPartial;
/*      */     }
/*  528 */     return this;
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
/*      */   public Partial withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  547 */     int i = indexOfSupported(paramDateTimeFieldType);
/*  548 */     if (paramInt == getValue(i)) {
/*  549 */       return this;
/*      */     }
/*  551 */     int[] arrayOfInt = getValues();
/*  552 */     arrayOfInt = getField(i).set(this, i, arrayOfInt, paramInt);
/*  553 */     return new Partial(this, arrayOfInt);
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
/*      */   public Partial withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  571 */     int i = indexOfSupported(paramDurationFieldType);
/*  572 */     if (paramInt == 0) {
/*  573 */       return this;
/*      */     }
/*  575 */     int[] arrayOfInt = getValues();
/*  576 */     arrayOfInt = getField(i).add(this, i, arrayOfInt, paramInt);
/*  577 */     return new Partial(this, arrayOfInt);
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
/*      */   public Partial withFieldAddWrapped(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  595 */     int i = indexOfSupported(paramDurationFieldType);
/*  596 */     if (paramInt == 0) {
/*  597 */       return this;
/*      */     }
/*  599 */     int[] arrayOfInt = getValues();
/*  600 */     arrayOfInt = getField(i).addWrapPartial(this, i, arrayOfInt, paramInt);
/*  601 */     return new Partial(this, arrayOfInt);
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
/*      */   public Partial withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/*  620 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/*  621 */       return this;
/*      */     }
/*  623 */     int[] arrayOfInt = getValues();
/*  624 */     for (int i = 0; i < paramReadablePeriod.size(); i++) {
/*  625 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/*  626 */       int j = indexOf(localDurationFieldType);
/*  627 */       if (j >= 0) {
/*  628 */         arrayOfInt = getField(j).add(this, j, arrayOfInt, FieldUtils.safeMultiply(paramReadablePeriod.getValue(i), paramInt));
/*      */       }
/*      */     }
/*      */     
/*  632 */     return new Partial(this, arrayOfInt);
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
/*      */   public Partial plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  645 */     return withPeriodAdded(paramReadablePeriod, 1);
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
/*      */   public Partial minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  658 */     return withPeriodAdded(paramReadablePeriod, -1);
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
/*      */   public Property property(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  673 */     return new Property(this, indexOfSupported(paramDateTimeFieldType));
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
/*      */   public boolean isMatch(ReadableInstant paramReadableInstant)
/*      */   {
/*  687 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/*  688 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/*  689 */     for (int i = 0; i < this.iTypes.length; i++) {
/*  690 */       int j = this.iTypes[i].getField(localChronology).get(l);
/*  691 */       if (j != this.iValues[i]) {
/*  692 */         return false;
/*      */       }
/*      */     }
/*  695 */     return true;
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
/*      */   public boolean isMatch(ReadablePartial paramReadablePartial)
/*      */   {
/*  711 */     if (paramReadablePartial == null) {
/*  712 */       throw new IllegalArgumentException("The partial must not be null");
/*      */     }
/*  714 */     for (int i = 0; i < this.iTypes.length; i++) {
/*  715 */       int j = paramReadablePartial.get(this.iTypes[i]);
/*  716 */       if (j != this.iValues[i]) {
/*  717 */         return false;
/*      */       }
/*      */     }
/*  720 */     return true;
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
/*      */   public DateTimeFormatter getFormatter()
/*      */   {
/*  736 */     DateTimeFormatter[] arrayOfDateTimeFormatter = this.iFormatter;
/*  737 */     if (arrayOfDateTimeFormatter == null) {
/*  738 */       if (size() == 0) {
/*  739 */         return null;
/*      */       }
/*  741 */       arrayOfDateTimeFormatter = new DateTimeFormatter[2];
/*      */       try {
/*  743 */         ArrayList localArrayList = new ArrayList(Arrays.asList(this.iTypes));
/*  744 */         arrayOfDateTimeFormatter[0] = ISODateTimeFormat.forFields(localArrayList, true, false);
/*  745 */         if (localArrayList.size() == 0) {
/*  746 */           arrayOfDateTimeFormatter[1] = arrayOfDateTimeFormatter[0];
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */       
/*  751 */       this.iFormatter = arrayOfDateTimeFormatter;
/*      */     }
/*  753 */     return arrayOfDateTimeFormatter[0];
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
/*      */   public String toString()
/*      */   {
/*  769 */     DateTimeFormatter[] arrayOfDateTimeFormatter = this.iFormatter;
/*  770 */     if (arrayOfDateTimeFormatter == null) {
/*  771 */       getFormatter();
/*  772 */       arrayOfDateTimeFormatter = this.iFormatter;
/*  773 */       if (arrayOfDateTimeFormatter == null) {
/*  774 */         return toStringList();
/*      */       }
/*      */     }
/*  777 */     DateTimeFormatter localDateTimeFormatter = arrayOfDateTimeFormatter[1];
/*  778 */     if (localDateTimeFormatter == null) {
/*  779 */       return toStringList();
/*      */     }
/*  781 */     return localDateTimeFormatter.print(this);
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
/*      */   public String toStringList()
/*      */   {
/*  794 */     int i = size();
/*  795 */     StringBuilder localStringBuilder = new StringBuilder(20 * i);
/*  796 */     localStringBuilder.append('[');
/*  797 */     for (int j = 0; j < i; j++) {
/*  798 */       if (j > 0) {
/*  799 */         localStringBuilder.append(',').append(' ');
/*      */       }
/*  801 */       localStringBuilder.append(this.iTypes[j].getName());
/*  802 */       localStringBuilder.append('=');
/*  803 */       localStringBuilder.append(this.iValues[j]);
/*      */     }
/*  805 */     localStringBuilder.append(']');
/*  806 */     return localStringBuilder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString)
/*      */   {
/*  817 */     if (paramString == null) {
/*  818 */       return toString();
/*      */     }
/*  820 */     return DateTimeFormat.forPattern(paramString).print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString, Locale paramLocale)
/*      */   {
/*  832 */     if (paramString == null) {
/*  833 */       return toString();
/*      */     }
/*  835 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class Property
/*      */     extends AbstractPartialFieldProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 53278362873888L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final Partial iPartial;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final int iFieldIndex;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Property(Partial paramPartial, int paramInt)
/*      */     {
/*  865 */       this.iPartial = paramPartial;
/*  866 */       this.iFieldIndex = paramInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/*  875 */       return this.iPartial.getField(this.iFieldIndex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected ReadablePartial getReadablePartial()
/*      */     {
/*  884 */       return this.iPartial;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Partial getPartial()
/*      */     {
/*  893 */       return this.iPartial;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int get()
/*      */     {
/*  902 */       return this.iPartial.getValue(this.iFieldIndex);
/*      */     }
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
/*      */     public Partial addToCopy(int paramInt)
/*      */     {
/*  924 */       int[] arrayOfInt = this.iPartial.getValues();
/*  925 */       arrayOfInt = getField().add(this.iPartial, this.iFieldIndex, arrayOfInt, paramInt);
/*  926 */       return new Partial(this.iPartial, arrayOfInt);
/*      */     }
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
/*      */     public Partial addWrapFieldToCopy(int paramInt)
/*      */     {
/*  948 */       int[] arrayOfInt = this.iPartial.getValues();
/*  949 */       arrayOfInt = getField().addWrapField(this.iPartial, this.iFieldIndex, arrayOfInt, paramInt);
/*  950 */       return new Partial(this.iPartial, arrayOfInt);
/*      */     }
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
/*      */     public Partial setCopy(int paramInt)
/*      */     {
/*  965 */       int[] arrayOfInt = this.iPartial.getValues();
/*  966 */       arrayOfInt = getField().set(this.iPartial, this.iFieldIndex, arrayOfInt, paramInt);
/*  967 */       return new Partial(this.iPartial, arrayOfInt);
/*      */     }
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
/*      */     public Partial setCopy(String paramString, Locale paramLocale)
/*      */     {
/*  982 */       int[] arrayOfInt = this.iPartial.getValues();
/*  983 */       arrayOfInt = getField().set(this.iPartial, this.iFieldIndex, arrayOfInt, paramString, paramLocale);
/*  984 */       return new Partial(this.iPartial, arrayOfInt);
/*      */     }
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
/*      */     public Partial setCopy(String paramString)
/*      */     {
/*  998 */       return setCopy(paramString, null);
/*      */     }
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
/*      */     public Partial withMaximumValue()
/*      */     {
/* 1012 */       return setCopy(getMaximumValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Partial withMinimumValue()
/*      */     {
/* 1025 */       return setCopy(getMinimumValue());
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Partial.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */