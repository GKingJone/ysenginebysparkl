/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalInstantException;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.BaseDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.BaseDurationField;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ZonedChronology
/*     */   extends AssembledChronology
/*     */ {
/*     */   private static final long serialVersionUID = -1079258847191166848L;
/*     */   
/*     */   public static ZonedChronology getInstance(Chronology paramChronology, DateTimeZone paramDateTimeZone)
/*     */   {
/*  55 */     if (paramChronology == null) {
/*  56 */       throw new IllegalArgumentException("Must supply a chronology");
/*     */     }
/*  58 */     paramChronology = paramChronology.withUTC();
/*  59 */     if (paramChronology == null) {
/*  60 */       throw new IllegalArgumentException("UTC chronology must not be null");
/*     */     }
/*  62 */     if (paramDateTimeZone == null) {
/*  63 */       throw new IllegalArgumentException("DateTimeZone must not be null");
/*     */     }
/*  65 */     return new ZonedChronology(paramChronology, paramDateTimeZone);
/*     */   }
/*     */   
/*     */ 
/*     */   static boolean useTimeArithmetic(DurationField paramDurationField)
/*     */   {
/*  71 */     return (paramDurationField != null) && (paramDurationField.getUnitMillis() < 43200000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ZonedChronology(Chronology paramChronology, DateTimeZone paramDateTimeZone)
/*     */   {
/*  81 */     super(paramChronology, paramDateTimeZone);
/*     */   }
/*     */   
/*     */   public DateTimeZone getZone() {
/*  85 */     return (DateTimeZone)getParam();
/*     */   }
/*     */   
/*     */   public Chronology withUTC() {
/*  89 */     return getBase();
/*     */   }
/*     */   
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone) {
/*  93 */     if (paramDateTimeZone == null) {
/*  94 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*  96 */     if (paramDateTimeZone == getParam()) {
/*  97 */       return this;
/*     */     }
/*  99 */     if (paramDateTimeZone == DateTimeZone.UTC) {
/* 100 */       return getBase();
/*     */     }
/* 102 */     return new ZonedChronology(getBase(), paramDateTimeZone);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/* 109 */     return localToUTC(getBase().getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */     throws IllegalArgumentException
/*     */   {
/* 118 */     return localToUTC(getBase().getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getDateTimeMillis(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/* 128 */     return localToUTC(getBase().getDateTimeMillis(paramLong + getZone().getOffset(paramLong), paramInt1, paramInt2, paramInt3, paramInt4));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long localToUTC(long paramLong)
/*     */   {
/* 138 */     DateTimeZone localDateTimeZone = getZone();
/* 139 */     int i = localDateTimeZone.getOffsetFromLocal(paramLong);
/* 140 */     long l = paramLong - i;
/* 141 */     int j = localDateTimeZone.getOffset(l);
/* 142 */     if (i != j) {
/* 143 */       throw new IllegalInstantException(paramLong, localDateTimeZone.getID());
/*     */     }
/* 145 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void assemble(AssembledChronology.Fields paramFields)
/*     */   {
/* 151 */     HashMap localHashMap = new HashMap();
/*     */     
/*     */ 
/*     */ 
/* 155 */     paramFields.eras = convertField(paramFields.eras, localHashMap);
/* 156 */     paramFields.centuries = convertField(paramFields.centuries, localHashMap);
/* 157 */     paramFields.years = convertField(paramFields.years, localHashMap);
/* 158 */     paramFields.months = convertField(paramFields.months, localHashMap);
/* 159 */     paramFields.weekyears = convertField(paramFields.weekyears, localHashMap);
/* 160 */     paramFields.weeks = convertField(paramFields.weeks, localHashMap);
/* 161 */     paramFields.days = convertField(paramFields.days, localHashMap);
/*     */     
/* 163 */     paramFields.halfdays = convertField(paramFields.halfdays, localHashMap);
/* 164 */     paramFields.hours = convertField(paramFields.hours, localHashMap);
/* 165 */     paramFields.minutes = convertField(paramFields.minutes, localHashMap);
/* 166 */     paramFields.seconds = convertField(paramFields.seconds, localHashMap);
/* 167 */     paramFields.millis = convertField(paramFields.millis, localHashMap);
/*     */     
/*     */ 
/*     */ 
/* 171 */     paramFields.year = convertField(paramFields.year, localHashMap);
/* 172 */     paramFields.yearOfEra = convertField(paramFields.yearOfEra, localHashMap);
/* 173 */     paramFields.yearOfCentury = convertField(paramFields.yearOfCentury, localHashMap);
/* 174 */     paramFields.centuryOfEra = convertField(paramFields.centuryOfEra, localHashMap);
/* 175 */     paramFields.era = convertField(paramFields.era, localHashMap);
/* 176 */     paramFields.dayOfWeek = convertField(paramFields.dayOfWeek, localHashMap);
/* 177 */     paramFields.dayOfMonth = convertField(paramFields.dayOfMonth, localHashMap);
/* 178 */     paramFields.dayOfYear = convertField(paramFields.dayOfYear, localHashMap);
/* 179 */     paramFields.monthOfYear = convertField(paramFields.monthOfYear, localHashMap);
/* 180 */     paramFields.weekOfWeekyear = convertField(paramFields.weekOfWeekyear, localHashMap);
/* 181 */     paramFields.weekyear = convertField(paramFields.weekyear, localHashMap);
/* 182 */     paramFields.weekyearOfCentury = convertField(paramFields.weekyearOfCentury, localHashMap);
/*     */     
/* 184 */     paramFields.millisOfSecond = convertField(paramFields.millisOfSecond, localHashMap);
/* 185 */     paramFields.millisOfDay = convertField(paramFields.millisOfDay, localHashMap);
/* 186 */     paramFields.secondOfMinute = convertField(paramFields.secondOfMinute, localHashMap);
/* 187 */     paramFields.secondOfDay = convertField(paramFields.secondOfDay, localHashMap);
/* 188 */     paramFields.minuteOfHour = convertField(paramFields.minuteOfHour, localHashMap);
/* 189 */     paramFields.minuteOfDay = convertField(paramFields.minuteOfDay, localHashMap);
/* 190 */     paramFields.hourOfDay = convertField(paramFields.hourOfDay, localHashMap);
/* 191 */     paramFields.hourOfHalfday = convertField(paramFields.hourOfHalfday, localHashMap);
/* 192 */     paramFields.clockhourOfDay = convertField(paramFields.clockhourOfDay, localHashMap);
/* 193 */     paramFields.clockhourOfHalfday = convertField(paramFields.clockhourOfHalfday, localHashMap);
/* 194 */     paramFields.halfdayOfDay = convertField(paramFields.halfdayOfDay, localHashMap);
/*     */   }
/*     */   
/*     */   private DurationField convertField(DurationField paramDurationField, HashMap<Object, Object> paramHashMap) {
/* 198 */     if ((paramDurationField == null) || (!paramDurationField.isSupported())) {
/* 199 */       return paramDurationField;
/*     */     }
/* 201 */     if (paramHashMap.containsKey(paramDurationField)) {
/* 202 */       return (DurationField)paramHashMap.get(paramDurationField);
/*     */     }
/* 204 */     ZonedDurationField localZonedDurationField = new ZonedDurationField(paramDurationField, getZone());
/* 205 */     paramHashMap.put(paramDurationField, localZonedDurationField);
/* 206 */     return localZonedDurationField;
/*     */   }
/*     */   
/*     */   private DateTimeField convertField(DateTimeField paramDateTimeField, HashMap<Object, Object> paramHashMap) {
/* 210 */     if ((paramDateTimeField == null) || (!paramDateTimeField.isSupported())) {
/* 211 */       return paramDateTimeField;
/*     */     }
/* 213 */     if (paramHashMap.containsKey(paramDateTimeField)) {
/* 214 */       return (DateTimeField)paramHashMap.get(paramDateTimeField);
/*     */     }
/* 216 */     ZonedDateTimeField localZonedDateTimeField = new ZonedDateTimeField(paramDateTimeField, getZone(), convertField(paramDateTimeField.getDurationField(), paramHashMap), convertField(paramDateTimeField.getRangeDurationField(), paramHashMap), convertField(paramDateTimeField.getLeapDurationField(), paramHashMap));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     paramHashMap.put(paramDateTimeField, localZonedDateTimeField);
/* 222 */     return localZonedDateTimeField;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 235 */     if (this == paramObject) {
/* 236 */       return true;
/*     */     }
/* 238 */     if (!(paramObject instanceof ZonedChronology)) {
/* 239 */       return false;
/*     */     }
/* 241 */     ZonedChronology localZonedChronology = (ZonedChronology)paramObject;
/* 242 */     return (getBase().equals(localZonedChronology.getBase())) && (getZone().equals(localZonedChronology.getZone()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 254 */     return 326565 + getZone().hashCode() * 11 + getBase().hashCode() * 7;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 263 */     return "ZonedChronology[" + getBase() + ", " + getZone().getID() + ']';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class ZonedDurationField
/*     */     extends BaseDurationField
/*     */   {
/*     */     private static final long serialVersionUID = -485345310999208286L;
/*     */     
/*     */     final DurationField iField;
/*     */     
/*     */     final boolean iTimeField;
/*     */     
/*     */     final DateTimeZone iZone;
/*     */     
/*     */ 
/*     */     ZonedDurationField(DurationField paramDurationField, DateTimeZone paramDateTimeZone)
/*     */     {
/* 282 */       super();
/* 283 */       if (!paramDurationField.isSupported()) {
/* 284 */         throw new IllegalArgumentException();
/*     */       }
/* 286 */       this.iField = paramDurationField;
/* 287 */       this.iTimeField = ZonedChronology.useTimeArithmetic(paramDurationField);
/* 288 */       this.iZone = paramDateTimeZone;
/*     */     }
/*     */     
/*     */     public boolean isPrecise() {
/* 292 */       return (this.iField.isPrecise()) && (this.iZone.isFixed()) ? true : this.iTimeField ? this.iField.isPrecise() : false;
/*     */     }
/*     */     
/*     */     public long getUnitMillis() {
/* 296 */       return this.iField.getUnitMillis();
/*     */     }
/*     */     
/*     */     public int getValue(long paramLong1, long paramLong2) {
/* 300 */       return this.iField.getValue(paramLong1, addOffset(paramLong2));
/*     */     }
/*     */     
/*     */     public long getValueAsLong(long paramLong1, long paramLong2) {
/* 304 */       return this.iField.getValueAsLong(paramLong1, addOffset(paramLong2));
/*     */     }
/*     */     
/*     */     public long getMillis(int paramInt, long paramLong) {
/* 308 */       return this.iField.getMillis(paramInt, addOffset(paramLong));
/*     */     }
/*     */     
/*     */     public long getMillis(long paramLong1, long paramLong2) {
/* 312 */       return this.iField.getMillis(paramLong1, addOffset(paramLong2));
/*     */     }
/*     */     
/*     */     public long add(long paramLong, int paramInt) {
/* 316 */       int i = getOffsetToAdd(paramLong);
/* 317 */       paramLong = this.iField.add(paramLong + i, paramInt);
/* 318 */       return paramLong - (this.iTimeField ? i : getOffsetFromLocalToSubtract(paramLong));
/*     */     }
/*     */     
/*     */     public long add(long paramLong1, long paramLong2) {
/* 322 */       int i = getOffsetToAdd(paramLong1);
/* 323 */       paramLong1 = this.iField.add(paramLong1 + i, paramLong2);
/* 324 */       return paramLong1 - (this.iTimeField ? i : getOffsetFromLocalToSubtract(paramLong1));
/*     */     }
/*     */     
/*     */     public int getDifference(long paramLong1, long paramLong2) {
/* 328 */       int i = getOffsetToAdd(paramLong2);
/* 329 */       return this.iField.getDifference(paramLong1 + (this.iTimeField ? i : getOffsetToAdd(paramLong1)), paramLong2 + i);
/*     */     }
/*     */     
/*     */ 
/*     */     public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */     {
/* 335 */       int i = getOffsetToAdd(paramLong2);
/* 336 */       return this.iField.getDifferenceAsLong(paramLong1 + (this.iTimeField ? i : getOffsetToAdd(paramLong1)), paramLong2 + i);
/*     */     }
/*     */     
/*     */ 
/*     */     private int getOffsetToAdd(long paramLong)
/*     */     {
/* 342 */       int i = this.iZone.getOffset(paramLong);
/* 343 */       long l = paramLong + i;
/*     */       
/* 345 */       if (((paramLong ^ l) < 0L) && ((paramLong ^ i) >= 0L)) {
/* 346 */         throw new ArithmeticException("Adding time zone offset caused overflow");
/*     */       }
/* 348 */       return i;
/*     */     }
/*     */     
/*     */     private int getOffsetFromLocalToSubtract(long paramLong) {
/* 352 */       int i = this.iZone.getOffsetFromLocal(paramLong);
/* 353 */       long l = paramLong - i;
/*     */       
/* 355 */       if (((paramLong ^ l) < 0L) && ((paramLong ^ i) < 0L)) {
/* 356 */         throw new ArithmeticException("Subtracting time zone offset caused overflow");
/*     */       }
/* 358 */       return i;
/*     */     }
/*     */     
/*     */     private long addOffset(long paramLong) {
/* 362 */       return this.iZone.convertUTCToLocal(paramLong);
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 367 */       if (this == paramObject)
/* 368 */         return true;
/* 369 */       if ((paramObject instanceof ZonedDurationField)) {
/* 370 */         ZonedDurationField localZonedDurationField = (ZonedDurationField)paramObject;
/* 371 */         return (this.iField.equals(localZonedDurationField.iField)) && (this.iZone.equals(localZonedDurationField.iZone));
/*     */       }
/*     */       
/* 374 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 379 */       return this.iField.hashCode() ^ this.iZone.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static final class ZonedDateTimeField
/*     */     extends BaseDateTimeField
/*     */   {
/*     */     private static final long serialVersionUID = -3968986277775529794L;
/*     */     
/*     */     final DateTimeField iField;
/*     */     
/*     */     final DateTimeZone iZone;
/*     */     
/*     */     final DurationField iDurationField;
/*     */     
/*     */     final boolean iTimeField;
/*     */     
/*     */     final DurationField iRangeDurationField;
/*     */     
/*     */     final DurationField iLeapDurationField;
/*     */     
/*     */ 
/*     */     ZonedDateTimeField(DateTimeField paramDateTimeField, DateTimeZone paramDateTimeZone, DurationField paramDurationField1, DurationField paramDurationField2, DurationField paramDurationField3)
/*     */     {
/* 405 */       super();
/* 406 */       if (!paramDateTimeField.isSupported()) {
/* 407 */         throw new IllegalArgumentException();
/*     */       }
/* 409 */       this.iField = paramDateTimeField;
/* 410 */       this.iZone = paramDateTimeZone;
/* 411 */       this.iDurationField = paramDurationField1;
/* 412 */       this.iTimeField = ZonedChronology.useTimeArithmetic(paramDurationField1);
/* 413 */       this.iRangeDurationField = paramDurationField2;
/* 414 */       this.iLeapDurationField = paramDurationField3;
/*     */     }
/*     */     
/*     */     public boolean isLenient() {
/* 418 */       return this.iField.isLenient();
/*     */     }
/*     */     
/*     */     public int get(long paramLong) {
/* 422 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 423 */       return this.iField.get(l);
/*     */     }
/*     */     
/*     */     public String getAsText(long paramLong, Locale paramLocale) {
/* 427 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 428 */       return this.iField.getAsText(l, paramLocale);
/*     */     }
/*     */     
/*     */     public String getAsShortText(long paramLong, Locale paramLocale) {
/* 432 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 433 */       return this.iField.getAsShortText(l, paramLocale);
/*     */     }
/*     */     
/*     */     public String getAsText(int paramInt, Locale paramLocale) {
/* 437 */       return this.iField.getAsText(paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public String getAsShortText(int paramInt, Locale paramLocale) {
/* 441 */       return this.iField.getAsShortText(paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public long add(long paramLong, int paramInt) {
/* 445 */       if (this.iTimeField) {
/* 446 */         int i = getOffsetToAdd(paramLong);
/* 447 */         long l1 = this.iField.add(paramLong + i, paramInt);
/* 448 */         return l1 - i;
/*     */       }
/* 450 */       long l2 = this.iZone.convertUTCToLocal(paramLong);
/* 451 */       l2 = this.iField.add(l2, paramInt);
/* 452 */       return this.iZone.convertLocalToUTC(l2, false, paramLong);
/*     */     }
/*     */     
/*     */     public long add(long paramLong1, long paramLong2)
/*     */     {
/* 457 */       if (this.iTimeField) {
/* 458 */         int i = getOffsetToAdd(paramLong1);
/* 459 */         long l1 = this.iField.add(paramLong1 + i, paramLong2);
/* 460 */         return l1 - i;
/*     */       }
/* 462 */       long l2 = this.iZone.convertUTCToLocal(paramLong1);
/* 463 */       l2 = this.iField.add(l2, paramLong2);
/* 464 */       return this.iZone.convertLocalToUTC(l2, false, paramLong1);
/*     */     }
/*     */     
/*     */     public long addWrapField(long paramLong, int paramInt)
/*     */     {
/* 469 */       if (this.iTimeField) {
/* 470 */         int i = getOffsetToAdd(paramLong);
/* 471 */         long l1 = this.iField.addWrapField(paramLong + i, paramInt);
/* 472 */         return l1 - i;
/*     */       }
/* 474 */       long l2 = this.iZone.convertUTCToLocal(paramLong);
/* 475 */       l2 = this.iField.addWrapField(l2, paramInt);
/* 476 */       return this.iZone.convertLocalToUTC(l2, false, paramLong);
/*     */     }
/*     */     
/*     */     public long set(long paramLong, int paramInt)
/*     */     {
/* 481 */       long l1 = this.iZone.convertUTCToLocal(paramLong);
/* 482 */       l1 = this.iField.set(l1, paramInt);
/* 483 */       long l2 = this.iZone.convertLocalToUTC(l1, false, paramLong);
/* 484 */       if (get(l2) != paramInt) {
/* 485 */         IllegalInstantException localIllegalInstantException = new IllegalInstantException(l1, this.iZone.getID());
/* 486 */         IllegalFieldValueException localIllegalFieldValueException = new IllegalFieldValueException(this.iField.getType(), Integer.valueOf(paramInt), localIllegalInstantException.getMessage());
/* 487 */         localIllegalFieldValueException.initCause(localIllegalInstantException);
/* 488 */         throw localIllegalFieldValueException;
/*     */       }
/* 490 */       return l2;
/*     */     }
/*     */     
/*     */     public long set(long paramLong, String paramString, Locale paramLocale)
/*     */     {
/* 495 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 496 */       l = this.iField.set(l, paramString, paramLocale);
/* 497 */       return this.iZone.convertLocalToUTC(l, false, paramLong);
/*     */     }
/*     */     
/*     */     public int getDifference(long paramLong1, long paramLong2) {
/* 501 */       int i = getOffsetToAdd(paramLong2);
/* 502 */       return this.iField.getDifference(paramLong1 + (this.iTimeField ? i : getOffsetToAdd(paramLong1)), paramLong2 + i);
/*     */     }
/*     */     
/*     */ 
/*     */     public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */     {
/* 508 */       int i = getOffsetToAdd(paramLong2);
/* 509 */       return this.iField.getDifferenceAsLong(paramLong1 + (this.iTimeField ? i : getOffsetToAdd(paramLong1)), paramLong2 + i);
/*     */     }
/*     */     
/*     */ 
/*     */     public final DurationField getDurationField()
/*     */     {
/* 515 */       return this.iDurationField;
/*     */     }
/*     */     
/*     */     public final DurationField getRangeDurationField() {
/* 519 */       return this.iRangeDurationField;
/*     */     }
/*     */     
/*     */     public boolean isLeap(long paramLong) {
/* 523 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 524 */       return this.iField.isLeap(l);
/*     */     }
/*     */     
/*     */     public int getLeapAmount(long paramLong) {
/* 528 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 529 */       return this.iField.getLeapAmount(l);
/*     */     }
/*     */     
/*     */     public final DurationField getLeapDurationField() {
/* 533 */       return this.iLeapDurationField;
/*     */     }
/*     */     
/*     */     public long roundFloor(long paramLong) {
/* 537 */       if (this.iTimeField) {
/* 538 */         int i = getOffsetToAdd(paramLong);
/* 539 */         paramLong = this.iField.roundFloor(paramLong + i);
/* 540 */         return paramLong - i;
/*     */       }
/* 542 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 543 */       l = this.iField.roundFloor(l);
/* 544 */       return this.iZone.convertLocalToUTC(l, false, paramLong);
/*     */     }
/*     */     
/*     */     public long roundCeiling(long paramLong)
/*     */     {
/* 549 */       if (this.iTimeField) {
/* 550 */         int i = getOffsetToAdd(paramLong);
/* 551 */         paramLong = this.iField.roundCeiling(paramLong + i);
/* 552 */         return paramLong - i;
/*     */       }
/* 554 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 555 */       l = this.iField.roundCeiling(l);
/* 556 */       return this.iZone.convertLocalToUTC(l, false, paramLong);
/*     */     }
/*     */     
/*     */     public long remainder(long paramLong)
/*     */     {
/* 561 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 562 */       return this.iField.remainder(l);
/*     */     }
/*     */     
/*     */     public int getMinimumValue() {
/* 566 */       return this.iField.getMinimumValue();
/*     */     }
/*     */     
/*     */     public int getMinimumValue(long paramLong) {
/* 570 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 571 */       return this.iField.getMinimumValue(l);
/*     */     }
/*     */     
/*     */     public int getMinimumValue(ReadablePartial paramReadablePartial) {
/* 575 */       return this.iField.getMinimumValue(paramReadablePartial);
/*     */     }
/*     */     
/*     */     public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/* 579 */       return this.iField.getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*     */     }
/*     */     
/*     */     public int getMaximumValue() {
/* 583 */       return this.iField.getMaximumValue();
/*     */     }
/*     */     
/*     */     public int getMaximumValue(long paramLong) {
/* 587 */       long l = this.iZone.convertUTCToLocal(paramLong);
/* 588 */       return this.iField.getMaximumValue(l);
/*     */     }
/*     */     
/*     */     public int getMaximumValue(ReadablePartial paramReadablePartial) {
/* 592 */       return this.iField.getMaximumValue(paramReadablePartial);
/*     */     }
/*     */     
/*     */     public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/* 596 */       return this.iField.getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*     */     }
/*     */     
/*     */     public int getMaximumTextLength(Locale paramLocale) {
/* 600 */       return this.iField.getMaximumTextLength(paramLocale);
/*     */     }
/*     */     
/*     */     public int getMaximumShortTextLength(Locale paramLocale) {
/* 604 */       return this.iField.getMaximumShortTextLength(paramLocale);
/*     */     }
/*     */     
/*     */     private int getOffsetToAdd(long paramLong) {
/* 608 */       int i = this.iZone.getOffset(paramLong);
/* 609 */       long l = paramLong + i;
/*     */       
/* 611 */       if (((paramLong ^ l) < 0L) && ((paramLong ^ i) >= 0L)) {
/* 612 */         throw new ArithmeticException("Adding time zone offset caused overflow");
/*     */       }
/* 614 */       return i;
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 619 */       if (this == paramObject)
/* 620 */         return true;
/* 621 */       if ((paramObject instanceof ZonedDateTimeField)) {
/* 622 */         ZonedDateTimeField localZonedDateTimeField = (ZonedDateTimeField)paramObject;
/* 623 */         return (this.iField.equals(localZonedDateTimeField.iField)) && (this.iZone.equals(localZonedDateTimeField.iZone)) && (this.iDurationField.equals(localZonedDateTimeField.iDurationField)) && (this.iRangeDurationField.equals(localZonedDateTimeField.iRangeDurationField));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 628 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 633 */       return this.iField.hashCode() ^ this.iZone.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\ZonedChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */