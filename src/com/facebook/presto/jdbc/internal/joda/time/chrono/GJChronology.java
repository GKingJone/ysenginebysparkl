/*      */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.Instant;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.BaseDateTimeField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.DecoratedDurationField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.util.Locale;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class GJChronology
/*      */   extends AssembledChronology
/*      */ {
/*      */   private static final long serialVersionUID = -2545574827706931671L;
/*      */   
/*      */   private static long convertByYear(long paramLong, Chronology paramChronology1, Chronology paramChronology2)
/*      */   {
/*   82 */     return paramChronology2.getDateTimeMillis(paramChronology1.year().get(paramLong), paramChronology1.monthOfYear().get(paramLong), paramChronology1.dayOfMonth().get(paramLong), paramChronology1.millisOfDay().get(paramLong));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static long convertByWeekyear(long paramLong, Chronology paramChronology1, Chronology paramChronology2)
/*      */   {
/*   94 */     long l = paramChronology2.weekyear().set(0L, paramChronology1.weekyear().get(paramLong));
/*   95 */     l = paramChronology2.weekOfWeekyear().set(l, paramChronology1.weekOfWeekyear().get(paramLong));
/*   96 */     l = paramChronology2.dayOfWeek().set(l, paramChronology1.dayOfWeek().get(paramLong));
/*   97 */     l = paramChronology2.millisOfDay().set(l, paramChronology1.millisOfDay().get(paramLong));
/*   98 */     return l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  104 */   static final Instant DEFAULT_CUTOVER = new Instant(-12219292800000L);
/*      */   
/*      */ 
/*  107 */   private static final ConcurrentHashMap<GJCacheKey, GJChronology> cCache = new ConcurrentHashMap();
/*      */   
/*      */   private JulianChronology iJulianChronology;
/*      */   
/*      */   private GregorianChronology iGregorianChronology;
/*      */   
/*      */   private Instant iCutoverInstant;
/*      */   
/*      */   private long iCutoverMillis;
/*      */   
/*      */   private long iGapDuration;
/*      */   
/*      */ 
/*      */   public static GJChronology getInstanceUTC()
/*      */   {
/*  122 */     return getInstance(DateTimeZone.UTC, DEFAULT_CUTOVER, 4);
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
/*      */   public static GJChronology getInstance()
/*      */   {
/*  138 */     return getInstance(DateTimeZone.getDefault(), DEFAULT_CUTOVER, 4);
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
/*      */   public static GJChronology getInstance(DateTimeZone paramDateTimeZone)
/*      */   {
/*  154 */     return getInstance(paramDateTimeZone, DEFAULT_CUTOVER, 4);
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
/*      */   public static GJChronology getInstance(DateTimeZone paramDateTimeZone, ReadableInstant paramReadableInstant)
/*      */   {
/*  172 */     return getInstance(paramDateTimeZone, paramReadableInstant, 4);
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
/*      */   public static GJChronology getInstance(DateTimeZone paramDateTimeZone, ReadableInstant paramReadableInstant, int paramInt)
/*      */   {
/*  188 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*      */     Instant localInstant;
/*  190 */     if (paramReadableInstant == null) {
/*  191 */       localInstant = DEFAULT_CUTOVER;
/*      */     } else {
/*  193 */       localInstant = paramReadableInstant.toInstant();
/*  194 */       localObject1 = new LocalDate(localInstant.getMillis(), GregorianChronology.getInstance(paramDateTimeZone));
/*  195 */       if (((LocalDate)localObject1).getYear() <= 0) {
/*  196 */         throw new IllegalArgumentException("Cutover too early. Must be on or after 0001-01-01.");
/*      */       }
/*      */     }
/*      */     
/*  200 */     Object localObject1 = new GJCacheKey(paramDateTimeZone, localInstant, paramInt);
/*  201 */     Object localObject2 = (GJChronology)cCache.get(localObject1);
/*  202 */     if (localObject2 == null) {
/*  203 */       if (paramDateTimeZone == DateTimeZone.UTC) {
/*  204 */         localObject2 = new GJChronology(JulianChronology.getInstance(paramDateTimeZone, paramInt), GregorianChronology.getInstance(paramDateTimeZone, paramInt), localInstant);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  209 */         localObject2 = getInstance(DateTimeZone.UTC, localInstant, paramInt);
/*  210 */         localObject2 = new GJChronology(ZonedChronology.getInstance((Chronology)localObject2, paramDateTimeZone), ((GJChronology)localObject2).iJulianChronology, ((GJChronology)localObject2).iGregorianChronology, ((GJChronology)localObject2).iCutoverInstant);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  216 */       GJChronology localGJChronology = (GJChronology)cCache.putIfAbsent(localObject1, localObject2);
/*  217 */       if (localGJChronology != null) {
/*  218 */         localObject2 = localGJChronology;
/*      */       }
/*      */     }
/*  221 */     return (GJChronology)localObject2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static GJChronology getInstance(DateTimeZone paramDateTimeZone, long paramLong, int paramInt)
/*      */   {
/*      */     Instant localInstant;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  238 */     if (paramLong == DEFAULT_CUTOVER.getMillis()) {
/*  239 */       localInstant = null;
/*      */     } else {
/*  241 */       localInstant = new Instant(paramLong);
/*      */     }
/*  243 */     return getInstance(paramDateTimeZone, localInstant, paramInt);
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
/*      */   private GJChronology(JulianChronology paramJulianChronology, GregorianChronology paramGregorianChronology, Instant paramInstant)
/*      */   {
/*  262 */     super(null, new Object[] { paramJulianChronology, paramGregorianChronology, paramInstant });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private GJChronology(Chronology paramChronology, JulianChronology paramJulianChronology, GregorianChronology paramGregorianChronology, Instant paramInstant)
/*      */   {
/*  272 */     super(paramChronology, new Object[] { paramJulianChronology, paramGregorianChronology, paramInstant });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Object readResolve()
/*      */   {
/*  279 */     return getInstance(getZone(), this.iCutoverInstant, getMinimumDaysInFirstWeek());
/*      */   }
/*      */   
/*      */   public DateTimeZone getZone() {
/*      */     Chronology localChronology;
/*  284 */     if ((localChronology = getBase()) != null) {
/*  285 */       return localChronology.getZone();
/*      */     }
/*  287 */     return DateTimeZone.UTC;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology withUTC()
/*      */   {
/*  298 */     return withZone(DateTimeZone.UTC);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*      */   {
/*  308 */     if (paramDateTimeZone == null) {
/*  309 */       paramDateTimeZone = DateTimeZone.getDefault();
/*      */     }
/*  311 */     if (paramDateTimeZone == getZone()) {
/*  312 */       return this;
/*      */     }
/*  314 */     return getInstance(paramDateTimeZone, this.iCutoverInstant, getMinimumDaysInFirstWeek());
/*      */   }
/*      */   
/*      */ 
/*      */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     Chronology localChronology;
/*  322 */     if ((localChronology = getBase()) != null) {
/*  323 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     }
/*      */     
/*      */ 
/*  327 */     long l = this.iGregorianChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     
/*  329 */     if (l < this.iCutoverMillis)
/*      */     {
/*  331 */       l = this.iJulianChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*      */       
/*  333 */       if (l >= this.iCutoverMillis)
/*      */       {
/*  335 */         throw new IllegalArgumentException("Specified date does not exist");
/*      */       }
/*      */     }
/*  338 */     return l;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     Chronology localChronology;
/*      */     
/*  347 */     if ((localChronology = getBase()) != null) {
/*  348 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */     }
/*      */     
/*      */ 
/*      */     long l;
/*      */     
/*      */     try
/*      */     {
/*  356 */       l = this.iGregorianChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */     }
/*      */     catch (IllegalFieldValueException localIllegalFieldValueException)
/*      */     {
/*  360 */       if ((paramInt2 != 2) || (paramInt3 != 29)) {
/*  361 */         throw localIllegalFieldValueException;
/*      */       }
/*  363 */       l = this.iGregorianChronology.getDateTimeMillis(paramInt1, paramInt2, 28, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */       
/*      */ 
/*  366 */       if (l >= this.iCutoverMillis) {
/*  367 */         throw localIllegalFieldValueException;
/*      */       }
/*      */     }
/*  370 */     if (l < this.iCutoverMillis)
/*      */     {
/*  372 */       l = this.iJulianChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */       
/*      */ 
/*  375 */       if (l >= this.iCutoverMillis)
/*      */       {
/*  377 */         throw new IllegalArgumentException("Specified date does not exist");
/*      */       }
/*      */     }
/*  380 */     return l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Instant getGregorianCutover()
/*      */   {
/*  388 */     return this.iCutoverInstant;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinimumDaysInFirstWeek()
/*      */   {
/*  397 */     return this.iGregorianChronology.getMinimumDaysInFirstWeek();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  409 */     if (this == paramObject) {
/*  410 */       return true;
/*      */     }
/*  412 */     if ((paramObject instanceof GJChronology)) {
/*  413 */       GJChronology localGJChronology = (GJChronology)paramObject;
/*  414 */       return (this.iCutoverMillis == localGJChronology.iCutoverMillis) && (getMinimumDaysInFirstWeek() == localGJChronology.getMinimumDaysInFirstWeek()) && (getZone().equals(localGJChronology.getZone()));
/*      */     }
/*      */     
/*      */ 
/*  418 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  428 */     return "GJ".hashCode() * 11 + getZone().hashCode() + getMinimumDaysInFirstWeek() + this.iCutoverInstant.hashCode();
/*      */   }
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
/*  440 */     StringBuffer localStringBuffer = new StringBuffer(60);
/*  441 */     localStringBuffer.append("GJChronology");
/*  442 */     localStringBuffer.append('[');
/*  443 */     localStringBuffer.append(getZone().getID());
/*      */     
/*  445 */     if (this.iCutoverMillis != DEFAULT_CUTOVER.getMillis()) {
/*  446 */       localStringBuffer.append(",cutover=");
/*      */       DateTimeFormatter localDateTimeFormatter;
/*  448 */       if (withUTC().dayOfYear().remainder(this.iCutoverMillis) == 0L) {
/*  449 */         localDateTimeFormatter = ISODateTimeFormat.date();
/*      */       } else {
/*  451 */         localDateTimeFormatter = ISODateTimeFormat.dateTime();
/*      */       }
/*  453 */       localDateTimeFormatter.withChronology(withUTC()).printTo(localStringBuffer, this.iCutoverMillis);
/*      */     }
/*      */     
/*  456 */     if (getMinimumDaysInFirstWeek() != 4) {
/*  457 */       localStringBuffer.append(",mdfw=");
/*  458 */       localStringBuffer.append(getMinimumDaysInFirstWeek());
/*      */     }
/*  460 */     localStringBuffer.append(']');
/*      */     
/*  462 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */   protected void assemble(AssembledChronology.Fields paramFields) {
/*  466 */     Object[] arrayOfObject = (Object[])getParam();
/*      */     
/*  468 */     JulianChronology localJulianChronology = (JulianChronology)arrayOfObject[0];
/*  469 */     GregorianChronology localGregorianChronology = (GregorianChronology)arrayOfObject[1];
/*  470 */     Instant localInstant = (Instant)arrayOfObject[2];
/*  471 */     this.iCutoverMillis = localInstant.getMillis();
/*      */     
/*  473 */     this.iJulianChronology = localJulianChronology;
/*  474 */     this.iGregorianChronology = localGregorianChronology;
/*  475 */     this.iCutoverInstant = localInstant;
/*      */     
/*  477 */     if (getBase() != null) {
/*  478 */       return;
/*      */     }
/*      */     
/*  481 */     if (localJulianChronology.getMinimumDaysInFirstWeek() != localGregorianChronology.getMinimumDaysInFirstWeek()) {
/*  482 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*      */ 
/*  486 */     this.iGapDuration = (this.iCutoverMillis - julianToGregorianByYear(this.iCutoverMillis));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  492 */     paramFields.copyFieldsFrom(localGregorianChronology);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  498 */     if (localGregorianChronology.millisOfDay().get(this.iCutoverMillis) == 0)
/*      */     {
/*      */ 
/*      */ 
/*  502 */       paramFields.millisOfSecond = new CutoverField(localJulianChronology.millisOfSecond(), paramFields.millisOfSecond, this.iCutoverMillis);
/*  503 */       paramFields.millisOfDay = new CutoverField(localJulianChronology.millisOfDay(), paramFields.millisOfDay, this.iCutoverMillis);
/*  504 */       paramFields.secondOfMinute = new CutoverField(localJulianChronology.secondOfMinute(), paramFields.secondOfMinute, this.iCutoverMillis);
/*  505 */       paramFields.secondOfDay = new CutoverField(localJulianChronology.secondOfDay(), paramFields.secondOfDay, this.iCutoverMillis);
/*  506 */       paramFields.minuteOfHour = new CutoverField(localJulianChronology.minuteOfHour(), paramFields.minuteOfHour, this.iCutoverMillis);
/*  507 */       paramFields.minuteOfDay = new CutoverField(localJulianChronology.minuteOfDay(), paramFields.minuteOfDay, this.iCutoverMillis);
/*  508 */       paramFields.hourOfDay = new CutoverField(localJulianChronology.hourOfDay(), paramFields.hourOfDay, this.iCutoverMillis);
/*  509 */       paramFields.hourOfHalfday = new CutoverField(localJulianChronology.hourOfHalfday(), paramFields.hourOfHalfday, this.iCutoverMillis);
/*  510 */       paramFields.clockhourOfDay = new CutoverField(localJulianChronology.clockhourOfDay(), paramFields.clockhourOfDay, this.iCutoverMillis);
/*  511 */       paramFields.clockhourOfHalfday = new CutoverField(localJulianChronology.clockhourOfHalfday(), paramFields.clockhourOfHalfday, this.iCutoverMillis);
/*      */       
/*  513 */       paramFields.halfdayOfDay = new CutoverField(localJulianChronology.halfdayOfDay(), paramFields.halfdayOfDay, this.iCutoverMillis);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  518 */     paramFields.era = new CutoverField(localJulianChronology.era(), paramFields.era, this.iCutoverMillis);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  525 */     paramFields.year = new ImpreciseCutoverField(localJulianChronology.year(), paramFields.year, this.iCutoverMillis);
/*      */     
/*  527 */     paramFields.years = paramFields.year.getDurationField();
/*  528 */     paramFields.yearOfEra = new ImpreciseCutoverField(localJulianChronology.yearOfEra(), paramFields.yearOfEra, paramFields.years, this.iCutoverMillis);
/*      */     
/*      */ 
/*  531 */     paramFields.centuryOfEra = new ImpreciseCutoverField(localJulianChronology.centuryOfEra(), paramFields.centuryOfEra, this.iCutoverMillis);
/*      */     
/*  533 */     paramFields.centuries = paramFields.centuryOfEra.getDurationField();
/*      */     
/*  535 */     paramFields.yearOfCentury = new ImpreciseCutoverField(localJulianChronology.yearOfCentury(), paramFields.yearOfCentury, paramFields.years, paramFields.centuries, this.iCutoverMillis);
/*      */     
/*      */ 
/*  538 */     paramFields.monthOfYear = new ImpreciseCutoverField(localJulianChronology.monthOfYear(), paramFields.monthOfYear, null, paramFields.years, this.iCutoverMillis);
/*      */     
/*  540 */     paramFields.months = paramFields.monthOfYear.getDurationField();
/*      */     
/*  542 */     paramFields.weekyear = new ImpreciseCutoverField(localJulianChronology.weekyear(), paramFields.weekyear, null, this.iCutoverMillis, true);
/*      */     
/*  544 */     paramFields.weekyears = paramFields.weekyear.getDurationField();
/*  545 */     paramFields.weekyearOfCentury = new ImpreciseCutoverField(localJulianChronology.weekyearOfCentury(), paramFields.weekyearOfCentury, paramFields.weekyears, paramFields.centuries, this.iCutoverMillis);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  555 */     long l = localGregorianChronology.year().roundCeiling(this.iCutoverMillis);
/*  556 */     paramFields.dayOfYear = new CutoverField(localJulianChronology.dayOfYear(), paramFields.dayOfYear, paramFields.years, l, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  561 */     l = localGregorianChronology.weekyear().roundCeiling(this.iCutoverMillis);
/*  562 */     paramFields.weekOfWeekyear = new CutoverField(localJulianChronology.weekOfWeekyear(), paramFields.weekOfWeekyear, paramFields.weekyears, l, true);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  569 */     CutoverField localCutoverField = new CutoverField(localJulianChronology.dayOfMonth(), paramFields.dayOfMonth, this.iCutoverMillis);
/*      */     
/*  571 */     localCutoverField.iRangeDurationField = paramFields.months;
/*  572 */     paramFields.dayOfMonth = localCutoverField;
/*      */   }
/*      */   
/*      */   long julianToGregorianByYear(long paramLong)
/*      */   {
/*  577 */     return convertByYear(paramLong, this.iJulianChronology, this.iGregorianChronology);
/*      */   }
/*      */   
/*      */   long gregorianToJulianByYear(long paramLong) {
/*  581 */     return convertByYear(paramLong, this.iGregorianChronology, this.iJulianChronology);
/*      */   }
/*      */   
/*      */   long julianToGregorianByWeekyear(long paramLong) {
/*  585 */     return convertByWeekyear(paramLong, this.iJulianChronology, this.iGregorianChronology);
/*      */   }
/*      */   
/*      */   long gregorianToJulianByWeekyear(long paramLong) {
/*  589 */     return convertByWeekyear(paramLong, this.iGregorianChronology, this.iJulianChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class CutoverField
/*      */     extends BaseDateTimeField
/*      */   {
/*      */     private static final long serialVersionUID = 3528501219481026402L;
/*      */     
/*      */ 
/*      */     final DateTimeField iJulianField;
/*      */     
/*      */     final DateTimeField iGregorianField;
/*      */     
/*      */     final long iCutover;
/*      */     
/*      */     final boolean iConvertByWeekyear;
/*      */     
/*      */     protected DurationField iDurationField;
/*      */     
/*      */     protected DurationField iRangeDurationField;
/*      */     
/*      */ 
/*      */     CutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, long paramLong)
/*      */     {
/*  615 */       this(paramDateTimeField1, paramDateTimeField2, paramLong, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     CutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, long paramLong, boolean paramBoolean)
/*      */     {
/*  626 */       this(paramDateTimeField1, paramDateTimeField2, null, paramLong, paramBoolean);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     CutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, DurationField paramDurationField, long paramLong, boolean paramBoolean)
/*      */     {
/*  638 */       super();
/*  639 */       this.iJulianField = paramDateTimeField1;
/*  640 */       this.iGregorianField = paramDateTimeField2;
/*  641 */       this.iCutover = paramLong;
/*  642 */       this.iConvertByWeekyear = paramBoolean;
/*      */       
/*      */ 
/*  645 */       this.iDurationField = paramDateTimeField2.getDurationField();
/*  646 */       if (paramDurationField == null) {
/*  647 */         paramDurationField = paramDateTimeField2.getRangeDurationField();
/*  648 */         if (paramDurationField == null) {
/*  649 */           paramDurationField = paramDateTimeField1.getRangeDurationField();
/*      */         }
/*      */       }
/*  652 */       this.iRangeDurationField = paramDurationField;
/*      */     }
/*      */     
/*      */     public boolean isLenient() {
/*  656 */       return false;
/*      */     }
/*      */     
/*      */     public int get(long paramLong) {
/*  660 */       if (paramLong >= this.iCutover) {
/*  661 */         return this.iGregorianField.get(paramLong);
/*      */       }
/*  663 */       return this.iJulianField.get(paramLong);
/*      */     }
/*      */     
/*      */     public String getAsText(long paramLong, Locale paramLocale)
/*      */     {
/*  668 */       if (paramLong >= this.iCutover) {
/*  669 */         return this.iGregorianField.getAsText(paramLong, paramLocale);
/*      */       }
/*  671 */       return this.iJulianField.getAsText(paramLong, paramLocale);
/*      */     }
/*      */     
/*      */     public String getAsText(int paramInt, Locale paramLocale)
/*      */     {
/*  676 */       return this.iGregorianField.getAsText(paramInt, paramLocale);
/*      */     }
/*      */     
/*      */     public String getAsShortText(long paramLong, Locale paramLocale) {
/*  680 */       if (paramLong >= this.iCutover) {
/*  681 */         return this.iGregorianField.getAsShortText(paramLong, paramLocale);
/*      */       }
/*  683 */       return this.iJulianField.getAsShortText(paramLong, paramLocale);
/*      */     }
/*      */     
/*      */     public String getAsShortText(int paramInt, Locale paramLocale)
/*      */     {
/*  688 */       return this.iGregorianField.getAsShortText(paramInt, paramLocale);
/*      */     }
/*      */     
/*      */     public long add(long paramLong, int paramInt) {
/*  692 */       return this.iGregorianField.add(paramLong, paramInt);
/*      */     }
/*      */     
/*      */     public long add(long paramLong1, long paramLong2) {
/*  696 */       return this.iGregorianField.add(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */ 
/*      */     public int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*      */     {
/*  702 */       if (paramInt2 == 0) {
/*  703 */         return paramArrayOfInt;
/*      */       }
/*  705 */       if (DateTimeUtils.isContiguous(paramReadablePartial)) {
/*  706 */         long l = 0L;
/*  707 */         int i = 0; for (int j = paramReadablePartial.size(); i < j; i++) {
/*  708 */           l = paramReadablePartial.getFieldType(i).getField(GJChronology.this).set(l, paramArrayOfInt[i]);
/*      */         }
/*  710 */         l = add(l, paramInt2);
/*  711 */         return GJChronology.this.get(paramReadablePartial, l);
/*      */       }
/*  713 */       return super.add(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*      */     }
/*      */     
/*      */     public int getDifference(long paramLong1, long paramLong2)
/*      */     {
/*  718 */       return this.iGregorianField.getDifference(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */     public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  722 */       return this.iGregorianField.getDifferenceAsLong(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */     public long set(long paramLong, int paramInt) {
/*  726 */       if (paramLong >= this.iCutover) {
/*  727 */         paramLong = this.iGregorianField.set(paramLong, paramInt);
/*  728 */         if (paramLong < this.iCutover)
/*      */         {
/*  730 */           if (paramLong + GJChronology.this.iGapDuration < this.iCutover) {
/*  731 */             paramLong = gregorianToJulian(paramLong);
/*      */           }
/*      */           
/*  734 */           if (get(paramLong) != paramInt) {
/*  735 */             throw new IllegalFieldValueException(this.iGregorianField.getType(), Integer.valueOf(paramInt), null, null);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  740 */         paramLong = this.iJulianField.set(paramLong, paramInt);
/*  741 */         if (paramLong >= this.iCutover)
/*      */         {
/*  743 */           if (paramLong - GJChronology.this.iGapDuration >= this.iCutover) {
/*  744 */             paramLong = julianToGregorian(paramLong);
/*      */           }
/*      */           
/*  747 */           if (get(paramLong) != paramInt) {
/*  748 */             throw new IllegalFieldValueException(this.iJulianField.getType(), Integer.valueOf(paramInt), null, null);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  753 */       return paramLong;
/*      */     }
/*      */     
/*      */     public long set(long paramLong, String paramString, Locale paramLocale) {
/*  757 */       if (paramLong >= this.iCutover) {
/*  758 */         paramLong = this.iGregorianField.set(paramLong, paramString, paramLocale);
/*  759 */         if (paramLong < this.iCutover)
/*      */         {
/*  761 */           if (paramLong + GJChronology.this.iGapDuration < this.iCutover) {
/*  762 */             paramLong = gregorianToJulian(paramLong);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  767 */         paramLong = this.iJulianField.set(paramLong, paramString, paramLocale);
/*  768 */         if (paramLong >= this.iCutover)
/*      */         {
/*  770 */           if (paramLong - GJChronology.this.iGapDuration >= this.iCutover) {
/*  771 */             paramLong = julianToGregorian(paramLong);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  776 */       return paramLong;
/*      */     }
/*      */     
/*      */     public DurationField getDurationField() {
/*  780 */       return this.iDurationField;
/*      */     }
/*      */     
/*      */     public DurationField getRangeDurationField() {
/*  784 */       return this.iRangeDurationField;
/*      */     }
/*      */     
/*      */     public boolean isLeap(long paramLong) {
/*  788 */       if (paramLong >= this.iCutover) {
/*  789 */         return this.iGregorianField.isLeap(paramLong);
/*      */       }
/*  791 */       return this.iJulianField.isLeap(paramLong);
/*      */     }
/*      */     
/*      */     public int getLeapAmount(long paramLong)
/*      */     {
/*  796 */       if (paramLong >= this.iCutover) {
/*  797 */         return this.iGregorianField.getLeapAmount(paramLong);
/*      */       }
/*  799 */       return this.iJulianField.getLeapAmount(paramLong);
/*      */     }
/*      */     
/*      */     public DurationField getLeapDurationField()
/*      */     {
/*  804 */       return this.iGregorianField.getLeapDurationField();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getMinimumValue()
/*      */     {
/*  811 */       return this.iJulianField.getMinimumValue();
/*      */     }
/*      */     
/*      */     public int getMinimumValue(ReadablePartial paramReadablePartial) {
/*  815 */       return this.iJulianField.getMinimumValue(paramReadablePartial);
/*      */     }
/*      */     
/*      */     public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/*  819 */       return this.iJulianField.getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*      */     }
/*      */     
/*      */     public int getMinimumValue(long paramLong) {
/*  823 */       if (paramLong < this.iCutover) {
/*  824 */         return this.iJulianField.getMinimumValue(paramLong);
/*      */       }
/*      */       
/*  827 */       int i = this.iGregorianField.getMinimumValue(paramLong);
/*      */       
/*      */ 
/*      */ 
/*  831 */       paramLong = this.iGregorianField.set(paramLong, i);
/*  832 */       if (paramLong < this.iCutover) {
/*  833 */         i = this.iGregorianField.get(this.iCutover);
/*      */       }
/*      */       
/*  836 */       return i;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMaximumValue()
/*      */     {
/*  842 */       return this.iGregorianField.getMaximumValue();
/*      */     }
/*      */     
/*      */     public int getMaximumValue(long paramLong) {
/*  846 */       if (paramLong >= this.iCutover) {
/*  847 */         return this.iGregorianField.getMaximumValue(paramLong);
/*      */       }
/*      */       
/*  850 */       int i = this.iJulianField.getMaximumValue(paramLong);
/*      */       
/*      */ 
/*      */ 
/*  854 */       paramLong = this.iJulianField.set(paramLong, i);
/*  855 */       if (paramLong >= this.iCutover) {
/*  856 */         i = this.iJulianField.get(this.iJulianField.add(this.iCutover, -1));
/*      */       }
/*      */       
/*  859 */       return i;
/*      */     }
/*      */     
/*      */     public int getMaximumValue(ReadablePartial paramReadablePartial) {
/*  863 */       long l = GJChronology.getInstanceUTC().set(paramReadablePartial, 0L);
/*  864 */       return getMaximumValue(l);
/*      */     }
/*      */     
/*      */     public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/*  868 */       GJChronology localGJChronology = GJChronology.getInstanceUTC();
/*  869 */       long l = 0L;
/*  870 */       int i = 0; for (int j = paramReadablePartial.size(); i < j; i++) {
/*  871 */         DateTimeField localDateTimeField = paramReadablePartial.getFieldType(i).getField(localGJChronology);
/*  872 */         if (paramArrayOfInt[i] <= localDateTimeField.getMaximumValue(l)) {
/*  873 */           l = localDateTimeField.set(l, paramArrayOfInt[i]);
/*      */         }
/*      */       }
/*  876 */       return getMaximumValue(l);
/*      */     }
/*      */     
/*      */     public long roundFloor(long paramLong) {
/*  880 */       if (paramLong >= this.iCutover) {
/*  881 */         paramLong = this.iGregorianField.roundFloor(paramLong);
/*  882 */         if (paramLong < this.iCutover)
/*      */         {
/*  884 */           if (paramLong + GJChronology.this.iGapDuration < this.iCutover) {
/*  885 */             paramLong = gregorianToJulian(paramLong);
/*      */           }
/*      */         }
/*      */       } else {
/*  889 */         paramLong = this.iJulianField.roundFloor(paramLong);
/*      */       }
/*  891 */       return paramLong;
/*      */     }
/*      */     
/*      */     public long roundCeiling(long paramLong) {
/*  895 */       if (paramLong >= this.iCutover) {
/*  896 */         paramLong = this.iGregorianField.roundCeiling(paramLong);
/*      */       } else {
/*  898 */         paramLong = this.iJulianField.roundCeiling(paramLong);
/*  899 */         if (paramLong >= this.iCutover)
/*      */         {
/*  901 */           if (paramLong - GJChronology.this.iGapDuration >= this.iCutover) {
/*  902 */             paramLong = julianToGregorian(paramLong);
/*      */           }
/*      */         }
/*      */       }
/*  906 */       return paramLong;
/*      */     }
/*      */     
/*      */     public int getMaximumTextLength(Locale paramLocale) {
/*  910 */       return Math.max(this.iJulianField.getMaximumTextLength(paramLocale), this.iGregorianField.getMaximumTextLength(paramLocale));
/*      */     }
/*      */     
/*      */     public int getMaximumShortTextLength(Locale paramLocale)
/*      */     {
/*  915 */       return Math.max(this.iJulianField.getMaximumShortTextLength(paramLocale), this.iGregorianField.getMaximumShortTextLength(paramLocale));
/*      */     }
/*      */     
/*      */     protected long julianToGregorian(long paramLong)
/*      */     {
/*  920 */       if (this.iConvertByWeekyear) {
/*  921 */         return GJChronology.this.julianToGregorianByWeekyear(paramLong);
/*      */       }
/*  923 */       return GJChronology.this.julianToGregorianByYear(paramLong);
/*      */     }
/*      */     
/*      */     protected long gregorianToJulian(long paramLong)
/*      */     {
/*  928 */       if (this.iConvertByWeekyear) {
/*  929 */         return GJChronology.this.gregorianToJulianByWeekyear(paramLong);
/*      */       }
/*  931 */       return GJChronology.this.gregorianToJulianByYear(paramLong);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final class ImpreciseCutoverField
/*      */     extends CutoverField
/*      */   {
/*      */     private static final long serialVersionUID = 3410248757173576441L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ImpreciseCutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, long paramLong)
/*      */     {
/*  951 */       this(paramDateTimeField1, paramDateTimeField2, null, paramLong, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ImpreciseCutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, DurationField paramDurationField, long paramLong)
/*      */     {
/*  962 */       this(paramDateTimeField1, paramDateTimeField2, paramDurationField, paramLong, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ImpreciseCutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, DurationField paramDurationField1, DurationField paramDurationField2, long paramLong)
/*      */     {
/*  973 */       this(paramDateTimeField1, paramDateTimeField2, paramDurationField1, paramLong, false);
/*  974 */       this.iRangeDurationField = paramDurationField2;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     ImpreciseCutoverField(DateTimeField paramDateTimeField1, DateTimeField paramDateTimeField2, DurationField paramDurationField, long paramLong, boolean paramBoolean)
/*      */     {
/*  986 */       super(paramDateTimeField1, paramDateTimeField2, paramLong, paramBoolean);
/*  987 */       if (paramDurationField == null) {
/*  988 */         paramDurationField = new LinkedDurationField(this.iDurationField, this);
/*      */       }
/*  990 */       this.iDurationField = paramDurationField;
/*      */     }
/*      */     
/*      */     public long add(long paramLong, int paramInt) {
/*  994 */       if (paramLong >= this.iCutover) {
/*  995 */         paramLong = this.iGregorianField.add(paramLong, paramInt);
/*  996 */         if (paramLong < this.iCutover)
/*      */         {
/*  998 */           if (paramLong + GJChronology.this.iGapDuration < this.iCutover) { int i;
/*  999 */             if (this.iConvertByWeekyear) {
/* 1000 */               i = GJChronology.this.iGregorianChronology.weekyear().get(paramLong);
/* 1001 */               if (i <= 0) {
/* 1002 */                 paramLong = GJChronology.this.iGregorianChronology.weekyear().add(paramLong, -1);
/*      */               }
/*      */             } else {
/* 1005 */               i = GJChronology.this.iGregorianChronology.year().get(paramLong);
/* 1006 */               if (i <= 0) {
/* 1007 */                 paramLong = GJChronology.this.iGregorianChronology.year().add(paramLong, -1);
/*      */               }
/*      */             }
/* 1010 */             paramLong = gregorianToJulian(paramLong);
/*      */           }
/*      */         }
/*      */       } else {
/* 1014 */         paramLong = this.iJulianField.add(paramLong, paramInt);
/* 1015 */         if (paramLong >= this.iCutover)
/*      */         {
/* 1017 */           if (paramLong - GJChronology.this.iGapDuration >= this.iCutover)
/*      */           {
/* 1019 */             paramLong = julianToGregorian(paramLong);
/*      */           }
/*      */         }
/*      */       }
/* 1023 */       return paramLong;
/*      */     }
/*      */     
/*      */     public long add(long paramLong1, long paramLong2) {
/* 1027 */       if (paramLong1 >= this.iCutover) {
/* 1028 */         paramLong1 = this.iGregorianField.add(paramLong1, paramLong2);
/* 1029 */         if (paramLong1 < this.iCutover)
/*      */         {
/* 1031 */           if (paramLong1 + GJChronology.this.iGapDuration < this.iCutover) { int i;
/* 1032 */             if (this.iConvertByWeekyear) {
/* 1033 */               i = GJChronology.this.iGregorianChronology.weekyear().get(paramLong1);
/* 1034 */               if (i <= 0) {
/* 1035 */                 paramLong1 = GJChronology.this.iGregorianChronology.weekyear().add(paramLong1, -1);
/*      */               }
/*      */             } else {
/* 1038 */               i = GJChronology.this.iGregorianChronology.year().get(paramLong1);
/* 1039 */               if (i <= 0) {
/* 1040 */                 paramLong1 = GJChronology.this.iGregorianChronology.year().add(paramLong1, -1);
/*      */               }
/*      */             }
/* 1043 */             paramLong1 = gregorianToJulian(paramLong1);
/*      */           }
/*      */         }
/*      */       } else {
/* 1047 */         paramLong1 = this.iJulianField.add(paramLong1, paramLong2);
/* 1048 */         if (paramLong1 >= this.iCutover)
/*      */         {
/* 1050 */           if (paramLong1 - GJChronology.this.iGapDuration >= this.iCutover)
/*      */           {
/* 1052 */             paramLong1 = julianToGregorian(paramLong1);
/*      */           }
/*      */         }
/*      */       }
/* 1056 */       return paramLong1;
/*      */     }
/*      */     
/*      */     public int getDifference(long paramLong1, long paramLong2) {
/* 1060 */       if (paramLong1 >= this.iCutover) {
/* 1061 */         if (paramLong2 >= this.iCutover) {
/* 1062 */           return this.iGregorianField.getDifference(paramLong1, paramLong2);
/*      */         }
/*      */         
/*      */ 
/* 1066 */         paramLong1 = gregorianToJulian(paramLong1);
/* 1067 */         return this.iJulianField.getDifference(paramLong1, paramLong2);
/*      */       }
/* 1069 */       if (paramLong2 < this.iCutover) {
/* 1070 */         return this.iJulianField.getDifference(paramLong1, paramLong2);
/*      */       }
/*      */       
/*      */ 
/* 1074 */       paramLong1 = julianToGregorian(paramLong1);
/* 1075 */       return this.iGregorianField.getDifference(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */     public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*      */     {
/* 1080 */       if (paramLong1 >= this.iCutover) {
/* 1081 */         if (paramLong2 >= this.iCutover) {
/* 1082 */           return this.iGregorianField.getDifferenceAsLong(paramLong1, paramLong2);
/*      */         }
/*      */         
/*      */ 
/* 1086 */         paramLong1 = gregorianToJulian(paramLong1);
/* 1087 */         return this.iJulianField.getDifferenceAsLong(paramLong1, paramLong2);
/*      */       }
/* 1089 */       if (paramLong2 < this.iCutover) {
/* 1090 */         return this.iJulianField.getDifferenceAsLong(paramLong1, paramLong2);
/*      */       }
/*      */       
/*      */ 
/* 1094 */       paramLong1 = julianToGregorian(paramLong1);
/* 1095 */       return this.iGregorianField.getDifferenceAsLong(paramLong1, paramLong2);
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
/*      */     public int getMinimumValue(long paramLong)
/*      */     {
/* 1110 */       if (paramLong >= this.iCutover) {
/* 1111 */         return this.iGregorianField.getMinimumValue(paramLong);
/*      */       }
/* 1113 */       return this.iJulianField.getMinimumValue(paramLong);
/*      */     }
/*      */     
/*      */     public int getMaximumValue(long paramLong)
/*      */     {
/* 1118 */       if (paramLong >= this.iCutover) {
/* 1119 */         return this.iGregorianField.getMaximumValue(paramLong);
/*      */       }
/* 1121 */       return this.iJulianField.getMaximumValue(paramLong);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class LinkedDurationField
/*      */     extends DecoratedDurationField
/*      */   {
/*      */     private static final long serialVersionUID = 4097975388007713084L;
/*      */     
/*      */     private final ImpreciseCutoverField iField;
/*      */     
/*      */ 
/*      */     LinkedDurationField(DurationField paramDurationField, ImpreciseCutoverField paramImpreciseCutoverField)
/*      */     {
/* 1136 */       super(paramDurationField.getType());
/* 1137 */       this.iField = paramImpreciseCutoverField;
/*      */     }
/*      */     
/*      */     public long add(long paramLong, int paramInt) {
/* 1141 */       return this.iField.add(paramLong, paramInt);
/*      */     }
/*      */     
/*      */     public long add(long paramLong1, long paramLong2) {
/* 1145 */       return this.iField.add(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */     public int getDifference(long paramLong1, long paramLong2) {
/* 1149 */       return this.iField.getDifference(paramLong1, paramLong2);
/*      */     }
/*      */     
/*      */     public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/* 1153 */       return this.iField.getDifferenceAsLong(paramLong1, paramLong2);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */