/*      */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.MutableDateTime;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.MutableDateTime.Property;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.MillisDurationField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.PreciseDateTimeField;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
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
/*      */ public class DateTimeFormatterBuilder
/*      */ {
/*      */   private ArrayList<Object> iElementPairs;
/*      */   private Object iFormatter;
/*      */   
/*      */   public DateTimeFormatterBuilder()
/*      */   {
/*   83 */     this.iElementPairs = new ArrayList();
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
/*      */   public DateTimeFormatter toFormatter()
/*      */   {
/*  103 */     Object localObject = getFormatter();
/*  104 */     InternalPrinter localInternalPrinter = null;
/*  105 */     if (isPrinter(localObject)) {
/*  106 */       localInternalPrinter = (InternalPrinter)localObject;
/*      */     }
/*  108 */     InternalParser localInternalParser = null;
/*  109 */     if (isParser(localObject)) {
/*  110 */       localInternalParser = (InternalParser)localObject;
/*      */     }
/*  112 */     if ((localInternalPrinter != null) || (localInternalParser != null)) {
/*  113 */       return new DateTimeFormatter(localInternalPrinter, localInternalParser);
/*      */     }
/*  115 */     throw new UnsupportedOperationException("Both printing and parsing not supported");
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
/*      */   public DateTimePrinter toPrinter()
/*      */   {
/*  131 */     Object localObject = getFormatter();
/*  132 */     if (isPrinter(localObject)) {
/*  133 */       InternalPrinter localInternalPrinter = (InternalPrinter)localObject;
/*  134 */       return InternalPrinterDateTimePrinter.of(localInternalPrinter);
/*      */     }
/*  136 */     throw new UnsupportedOperationException("Printing is not supported");
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
/*      */   public DateTimeParser toParser()
/*      */   {
/*  152 */     Object localObject = getFormatter();
/*  153 */     if (isParser(localObject)) {
/*  154 */       InternalParser localInternalParser = (InternalParser)localObject;
/*  155 */       return InternalParserDateTimeParser.of(localInternalParser);
/*      */     }
/*  157 */     throw new UnsupportedOperationException("Parsing is not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canBuildFormatter()
/*      */   {
/*  168 */     return isFormatter(getFormatter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canBuildPrinter()
/*      */   {
/*  178 */     return isPrinter(getFormatter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canBuildParser()
/*      */   {
/*  188 */     return isParser(getFormatter());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clear()
/*      */   {
/*  197 */     this.iFormatter = null;
/*  198 */     this.iElementPairs.clear();
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
/*      */   public DateTimeFormatterBuilder append(DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  217 */     if (paramDateTimeFormatter == null) {
/*  218 */       throw new IllegalArgumentException("No formatter supplied");
/*      */     }
/*  220 */     return append0(paramDateTimeFormatter.getPrinter0(), paramDateTimeFormatter.getParser0());
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
/*      */   public DateTimeFormatterBuilder append(DateTimePrinter paramDateTimePrinter)
/*      */   {
/*  238 */     checkPrinter(paramDateTimePrinter);
/*  239 */     return append0(DateTimePrinterInternalPrinter.of(paramDateTimePrinter), null);
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
/*      */   public DateTimeFormatterBuilder append(DateTimeParser paramDateTimeParser)
/*      */   {
/*  257 */     checkParser(paramDateTimeParser);
/*  258 */     return append0(null, DateTimeParserInternalParser.of(paramDateTimeParser));
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
/*      */   public DateTimeFormatterBuilder append(DateTimePrinter paramDateTimePrinter, DateTimeParser paramDateTimeParser)
/*      */   {
/*  276 */     checkPrinter(paramDateTimePrinter);
/*  277 */     checkParser(paramDateTimeParser);
/*  278 */     return append0(DateTimePrinterInternalPrinter.of(paramDateTimePrinter), DateTimeParserInternalParser.of(paramDateTimeParser));
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
/*      */   public DateTimeFormatterBuilder append(DateTimePrinter paramDateTimePrinter, DateTimeParser[] paramArrayOfDateTimeParser)
/*      */   {
/*  305 */     if (paramDateTimePrinter != null) {
/*  306 */       checkPrinter(paramDateTimePrinter);
/*      */     }
/*  308 */     if (paramArrayOfDateTimeParser == null) {
/*  309 */       throw new IllegalArgumentException("No parsers supplied");
/*      */     }
/*  311 */     int i = paramArrayOfDateTimeParser.length;
/*  312 */     if (i == 1) {
/*  313 */       if (paramArrayOfDateTimeParser[0] == null) {
/*  314 */         throw new IllegalArgumentException("No parser supplied");
/*      */       }
/*  316 */       return append0(DateTimePrinterInternalPrinter.of(paramDateTimePrinter), DateTimeParserInternalParser.of(paramArrayOfDateTimeParser[0]));
/*      */     }
/*      */     
/*  319 */     InternalParser[] arrayOfInternalParser = new InternalParser[i];
/*      */     
/*  321 */     for (int j = 0; j < i - 1; j++) {
/*  322 */       if ((arrayOfInternalParser[j] = DateTimeParserInternalParser.of(paramArrayOfDateTimeParser[j])) == null) {
/*  323 */         throw new IllegalArgumentException("Incomplete parser array");
/*      */       }
/*      */     }
/*  326 */     arrayOfInternalParser[j] = DateTimeParserInternalParser.of(paramArrayOfDateTimeParser[j]);
/*      */     
/*  328 */     return append0(DateTimePrinterInternalPrinter.of(paramDateTimePrinter), new MatchingParser(arrayOfInternalParser));
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
/*      */   public DateTimeFormatterBuilder appendOptional(DateTimeParser paramDateTimeParser)
/*      */   {
/*  345 */     checkParser(paramDateTimeParser);
/*  346 */     InternalParser[] arrayOfInternalParser = { DateTimeParserInternalParser.of(paramDateTimeParser), null };
/*  347 */     return append0(null, new MatchingParser(arrayOfInternalParser));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkParser(DateTimeParser paramDateTimeParser)
/*      */   {
/*  357 */     if (paramDateTimeParser == null) {
/*  358 */       throw new IllegalArgumentException("No parser supplied");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkPrinter(DateTimePrinter paramDateTimePrinter)
/*      */   {
/*  368 */     if (paramDateTimePrinter == null) {
/*  369 */       throw new IllegalArgumentException("No printer supplied");
/*      */     }
/*      */   }
/*      */   
/*      */   private DateTimeFormatterBuilder append0(Object paramObject) {
/*  374 */     this.iFormatter = null;
/*      */     
/*  376 */     this.iElementPairs.add(paramObject);
/*  377 */     this.iElementPairs.add(paramObject);
/*  378 */     return this;
/*      */   }
/*      */   
/*      */   private DateTimeFormatterBuilder append0(InternalPrinter paramInternalPrinter, InternalParser paramInternalParser)
/*      */   {
/*  383 */     this.iFormatter = null;
/*  384 */     this.iElementPairs.add(paramInternalPrinter);
/*  385 */     this.iElementPairs.add(paramInternalParser);
/*  386 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendLiteral(char paramChar)
/*      */   {
/*  397 */     return append0(new CharacterLiteral(paramChar));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendLiteral(String paramString)
/*      */   {
/*  408 */     if (paramString == null) {
/*  409 */       throw new IllegalArgumentException("Literal must not be null");
/*      */     }
/*  411 */     switch (paramString.length()) {
/*      */     case 0: 
/*  413 */       return this;
/*      */     case 1: 
/*  415 */       return append0(new CharacterLiteral(paramString.charAt(0)));
/*      */     }
/*  417 */     return append0(new StringLiteral(paramString));
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
/*      */   public DateTimeFormatterBuilder appendDecimal(DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2)
/*      */   {
/*  434 */     if (paramDateTimeFieldType == null) {
/*  435 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  437 */     if (paramInt2 < paramInt1) {
/*  438 */       paramInt2 = paramInt1;
/*      */     }
/*  440 */     if ((paramInt1 < 0) || (paramInt2 <= 0)) {
/*  441 */       throw new IllegalArgumentException();
/*      */     }
/*  443 */     if (paramInt1 <= 1) {
/*  444 */       return append0(new UnpaddedNumber(paramDateTimeFieldType, paramInt2, false));
/*      */     }
/*  446 */     return append0(new PaddedNumber(paramDateTimeFieldType, paramInt2, false, paramInt1));
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
/*      */   public DateTimeFormatterBuilder appendFixedDecimal(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  464 */     if (paramDateTimeFieldType == null) {
/*  465 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  467 */     if (paramInt <= 0) {
/*  468 */       throw new IllegalArgumentException("Illegal number of digits: " + paramInt);
/*      */     }
/*  470 */     return append0(new FixedNumber(paramDateTimeFieldType, paramInt, false));
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
/*      */   public DateTimeFormatterBuilder appendSignedDecimal(DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2)
/*      */   {
/*  486 */     if (paramDateTimeFieldType == null) {
/*  487 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  489 */     if (paramInt2 < paramInt1) {
/*  490 */       paramInt2 = paramInt1;
/*      */     }
/*  492 */     if ((paramInt1 < 0) || (paramInt2 <= 0)) {
/*  493 */       throw new IllegalArgumentException();
/*      */     }
/*  495 */     if (paramInt1 <= 1) {
/*  496 */       return append0(new UnpaddedNumber(paramDateTimeFieldType, paramInt2, true));
/*      */     }
/*  498 */     return append0(new PaddedNumber(paramDateTimeFieldType, paramInt2, true, paramInt1));
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
/*      */   public DateTimeFormatterBuilder appendFixedSignedDecimal(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  516 */     if (paramDateTimeFieldType == null) {
/*  517 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  519 */     if (paramInt <= 0) {
/*  520 */       throw new IllegalArgumentException("Illegal number of digits: " + paramInt);
/*      */     }
/*  522 */     return append0(new FixedNumber(paramDateTimeFieldType, paramInt, true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendText(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  534 */     if (paramDateTimeFieldType == null) {
/*  535 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  537 */     return append0(new TextField(paramDateTimeFieldType, false));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendShortText(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  549 */     if (paramDateTimeFieldType == null) {
/*  550 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  552 */     return append0(new TextField(paramDateTimeFieldType, true));
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
/*      */   public DateTimeFormatterBuilder appendFraction(DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2)
/*      */   {
/*  570 */     if (paramDateTimeFieldType == null) {
/*  571 */       throw new IllegalArgumentException("Field type must not be null");
/*      */     }
/*  573 */     if (paramInt2 < paramInt1) {
/*  574 */       paramInt2 = paramInt1;
/*      */     }
/*  576 */     if ((paramInt1 < 0) || (paramInt2 <= 0)) {
/*  577 */       throw new IllegalArgumentException();
/*      */     }
/*  579 */     return append0(new Fraction(paramDateTimeFieldType, paramInt1, paramInt2));
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
/*      */   public DateTimeFormatterBuilder appendFractionOfSecond(int paramInt1, int paramInt2)
/*      */   {
/*  597 */     return appendFraction(DateTimeFieldType.secondOfDay(), paramInt1, paramInt2);
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
/*      */   public DateTimeFormatterBuilder appendFractionOfMinute(int paramInt1, int paramInt2)
/*      */   {
/*  614 */     return appendFraction(DateTimeFieldType.minuteOfDay(), paramInt1, paramInt2);
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
/*      */   public DateTimeFormatterBuilder appendFractionOfHour(int paramInt1, int paramInt2)
/*      */   {
/*  631 */     return appendFraction(DateTimeFieldType.hourOfDay(), paramInt1, paramInt2);
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
/*      */   public DateTimeFormatterBuilder appendFractionOfDay(int paramInt1, int paramInt2)
/*      */   {
/*  648 */     return appendFraction(DateTimeFieldType.dayOfYear(), paramInt1, paramInt2);
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
/*      */   public DateTimeFormatterBuilder appendMillisOfSecond(int paramInt)
/*      */   {
/*  665 */     return appendDecimal(DateTimeFieldType.millisOfSecond(), paramInt, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMillisOfDay(int paramInt)
/*      */   {
/*  675 */     return appendDecimal(DateTimeFieldType.millisOfDay(), paramInt, 8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendSecondOfMinute(int paramInt)
/*      */   {
/*  685 */     return appendDecimal(DateTimeFieldType.secondOfMinute(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendSecondOfDay(int paramInt)
/*      */   {
/*  695 */     return appendDecimal(DateTimeFieldType.secondOfDay(), paramInt, 5);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMinuteOfHour(int paramInt)
/*      */   {
/*  705 */     return appendDecimal(DateTimeFieldType.minuteOfHour(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMinuteOfDay(int paramInt)
/*      */   {
/*  715 */     return appendDecimal(DateTimeFieldType.minuteOfDay(), paramInt, 4);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendHourOfDay(int paramInt)
/*      */   {
/*  725 */     return appendDecimal(DateTimeFieldType.hourOfDay(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendClockhourOfDay(int paramInt)
/*      */   {
/*  735 */     return appendDecimal(DateTimeFieldType.clockhourOfDay(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendHourOfHalfday(int paramInt)
/*      */   {
/*  745 */     return appendDecimal(DateTimeFieldType.hourOfHalfday(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendClockhourOfHalfday(int paramInt)
/*      */   {
/*  755 */     return appendDecimal(DateTimeFieldType.clockhourOfHalfday(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendDayOfWeek(int paramInt)
/*      */   {
/*  765 */     return appendDecimal(DateTimeFieldType.dayOfWeek(), paramInt, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendDayOfMonth(int paramInt)
/*      */   {
/*  775 */     return appendDecimal(DateTimeFieldType.dayOfMonth(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendDayOfYear(int paramInt)
/*      */   {
/*  785 */     return appendDecimal(DateTimeFieldType.dayOfYear(), paramInt, 3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendWeekOfWeekyear(int paramInt)
/*      */   {
/*  795 */     return appendDecimal(DateTimeFieldType.weekOfWeekyear(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendWeekyear(int paramInt1, int paramInt2)
/*      */   {
/*  807 */     return appendSignedDecimal(DateTimeFieldType.weekyear(), paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMonthOfYear(int paramInt)
/*      */   {
/*  817 */     return appendDecimal(DateTimeFieldType.monthOfYear(), paramInt, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendYear(int paramInt1, int paramInt2)
/*      */   {
/*  829 */     return appendSignedDecimal(DateTimeFieldType.year(), paramInt1, paramInt2);
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
/*      */   public DateTimeFormatterBuilder appendTwoDigitYear(int paramInt)
/*      */   {
/*  851 */     return appendTwoDigitYear(paramInt, false);
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
/*      */   public DateTimeFormatterBuilder appendTwoDigitYear(int paramInt, boolean paramBoolean)
/*      */   {
/*  869 */     return append0(new TwoDigitYear(DateTimeFieldType.year(), paramInt, paramBoolean));
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
/*      */   public DateTimeFormatterBuilder appendTwoDigitWeekyear(int paramInt)
/*      */   {
/*  891 */     return appendTwoDigitWeekyear(paramInt, false);
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
/*      */   public DateTimeFormatterBuilder appendTwoDigitWeekyear(int paramInt, boolean paramBoolean)
/*      */   {
/*  909 */     return append0(new TwoDigitYear(DateTimeFieldType.weekyear(), paramInt, paramBoolean));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendYearOfEra(int paramInt1, int paramInt2)
/*      */   {
/*  921 */     return appendDecimal(DateTimeFieldType.yearOfEra(), paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendYearOfCentury(int paramInt1, int paramInt2)
/*      */   {
/*  933 */     return appendDecimal(DateTimeFieldType.yearOfCentury(), paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendCenturyOfEra(int paramInt1, int paramInt2)
/*      */   {
/*  945 */     return appendSignedDecimal(DateTimeFieldType.centuryOfEra(), paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendHalfdayOfDayText()
/*      */   {
/*  955 */     return appendText(DateTimeFieldType.halfdayOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendDayOfWeekText()
/*      */   {
/*  965 */     return appendText(DateTimeFieldType.dayOfWeek());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendDayOfWeekShortText()
/*      */   {
/*  976 */     return appendShortText(DateTimeFieldType.dayOfWeek());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMonthOfYearText()
/*      */   {
/*  987 */     return appendText(DateTimeFieldType.monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendMonthOfYearShortText()
/*      */   {
/*  997 */     return appendShortText(DateTimeFieldType.monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendEraText()
/*      */   {
/* 1007 */     return appendText(DateTimeFieldType.era());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendTimeZoneName()
/*      */   {
/* 1018 */     return append0(new TimeZoneName(0, null), null);
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
/*      */   public DateTimeFormatterBuilder appendTimeZoneName(Map<String, DateTimeZone> paramMap)
/*      */   {
/* 1031 */     TimeZoneName localTimeZoneName = new TimeZoneName(0, paramMap);
/* 1032 */     return append0(localTimeZoneName, localTimeZoneName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendTimeZoneShortName()
/*      */   {
/* 1043 */     return append0(new TimeZoneName(1, null), null);
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
/*      */   public DateTimeFormatterBuilder appendTimeZoneShortName(Map<String, DateTimeZone> paramMap)
/*      */   {
/* 1057 */     TimeZoneName localTimeZoneName = new TimeZoneName(1, paramMap);
/* 1058 */     return append0(localTimeZoneName, localTimeZoneName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFormatterBuilder appendTimeZoneId()
/*      */   {
/* 1068 */     return append0(TimeZoneId.INSTANCE, TimeZoneId.INSTANCE);
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
/*      */   public DateTimeFormatterBuilder appendTimeZoneOffset(String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */   {
/* 1091 */     return append0(new TimeZoneOffset(paramString, paramString, paramBoolean, paramInt1, paramInt2));
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
/*      */   public DateTimeFormatterBuilder appendTimeZoneOffset(String paramString1, String paramString2, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */   {
/* 1118 */     return append0(new TimeZoneOffset(paramString1, paramString2, paramBoolean, paramInt1, paramInt2));
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
/*      */   public DateTimeFormatterBuilder appendPattern(String paramString)
/*      */   {
/* 1132 */     DateTimeFormat.appendPatternTo(this, paramString);
/* 1133 */     return this;
/*      */   }
/*      */   
/*      */   private Object getFormatter()
/*      */   {
/* 1138 */     Object localObject1 = this.iFormatter;
/*      */     
/* 1140 */     if (localObject1 == null) {
/* 1141 */       if (this.iElementPairs.size() == 2) {
/* 1142 */         Object localObject2 = this.iElementPairs.get(0);
/* 1143 */         Object localObject3 = this.iElementPairs.get(1);
/*      */         
/* 1145 */         if (localObject2 != null) {
/* 1146 */           if ((localObject2 == localObject3) || (localObject3 == null)) {
/* 1147 */             localObject1 = localObject2;
/*      */           }
/*      */         } else {
/* 1150 */           localObject1 = localObject3;
/*      */         }
/*      */       }
/*      */       
/* 1154 */       if (localObject1 == null) {
/* 1155 */         localObject1 = new Composite(this.iElementPairs);
/*      */       }
/*      */       
/* 1158 */       this.iFormatter = localObject1;
/*      */     }
/*      */     
/* 1161 */     return localObject1;
/*      */   }
/*      */   
/*      */   private boolean isPrinter(Object paramObject) {
/* 1165 */     if ((paramObject instanceof InternalPrinter)) {
/* 1166 */       if ((paramObject instanceof Composite)) {
/* 1167 */         return ((Composite)paramObject).isPrinter();
/*      */       }
/* 1169 */       return true;
/*      */     }
/* 1171 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isParser(Object paramObject) {
/* 1175 */     if ((paramObject instanceof InternalParser)) {
/* 1176 */       if ((paramObject instanceof Composite)) {
/* 1177 */         return ((Composite)paramObject).isParser();
/*      */       }
/* 1179 */       return true;
/*      */     }
/* 1181 */     return false;
/*      */   }
/*      */   
/*      */   private boolean isFormatter(Object paramObject) {
/* 1185 */     return (isPrinter(paramObject)) || (isParser(paramObject));
/*      */   }
/*      */   
/*      */   static void appendUnknownString(Appendable paramAppendable, int paramInt) throws IOException {
/* 1189 */     int i = paramInt; for (;;) { i--; if (i < 0) break;
/* 1190 */       paramAppendable.append(65533);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class CharacterLiteral
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final char iValue;
/*      */     
/*      */     CharacterLiteral(char paramChar)
/*      */     {
/* 1202 */       this.iValue = paramChar;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1206 */       return 1;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1212 */       paramAppendable.append(this.iValue);
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 1216 */       paramAppendable.append(this.iValue);
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 1220 */       return 1;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 1224 */       if (paramInt >= paramCharSequence.length()) {
/* 1225 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 1228 */       char c1 = paramCharSequence.charAt(paramInt);
/* 1229 */       char c2 = this.iValue;
/*      */       
/* 1231 */       if (c1 != c2) {
/* 1232 */         c1 = Character.toUpperCase(c1);
/* 1233 */         c2 = Character.toUpperCase(c2);
/* 1234 */         if (c1 != c2) {
/* 1235 */           c1 = Character.toLowerCase(c1);
/* 1236 */           c2 = Character.toLowerCase(c2);
/* 1237 */           if (c1 != c2) {
/* 1238 */             return paramInt ^ 0xFFFFFFFF;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1243 */       return paramInt + 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class StringLiteral
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final String iValue;
/*      */     
/*      */     StringLiteral(String paramString)
/*      */     {
/* 1255 */       this.iValue = paramString;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1259 */       return this.iValue.length();
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1265 */       paramAppendable.append(this.iValue);
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 1269 */       paramAppendable.append(this.iValue);
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 1273 */       return this.iValue.length();
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 1277 */       if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(paramCharSequence, paramInt, this.iValue)) {
/* 1278 */         return paramInt + this.iValue.length();
/*      */       }
/* 1280 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static abstract class NumberFormatter
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     protected final DateTimeFieldType iFieldType;
/*      */     protected final int iMaxParsedDigits;
/*      */     protected final boolean iSigned;
/*      */     
/*      */     NumberFormatter(DateTimeFieldType paramDateTimeFieldType, int paramInt, boolean paramBoolean)
/*      */     {
/* 1294 */       this.iFieldType = paramDateTimeFieldType;
/* 1295 */       this.iMaxParsedDigits = paramInt;
/* 1296 */       this.iSigned = paramBoolean;
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 1300 */       return this.iMaxParsedDigits;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 1304 */       int i = Math.min(this.iMaxParsedDigits, paramCharSequence.length() - paramInt);
/*      */       
/* 1306 */       int j = 0;
/* 1307 */       int k = 0;
/* 1308 */       int m; while (k < i) {
/* 1309 */         m = paramCharSequence.charAt(paramInt + k);
/* 1310 */         if ((k == 0) && ((m == 45) || (m == 43)) && (this.iSigned)) {
/* 1311 */           j = m == 45 ? 1 : 0;
/*      */           
/*      */ 
/* 1314 */           if ((k + 1 >= i) || ((m = paramCharSequence.charAt(paramInt + k + 1)) < '0') || (m > 57)) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1320 */           if (j != 0) {
/* 1321 */             k++;
/*      */           }
/*      */           else {
/* 1324 */             paramInt++;
/*      */           }
/*      */           
/* 1327 */           i = Math.min(i + 1, paramCharSequence.length() - paramInt);
/*      */         }
/*      */         else {
/* 1330 */           if ((m < 48) || (m > 57)) {
/*      */             break;
/*      */           }
/* 1333 */           k++;
/*      */         }
/*      */       }
/* 1336 */       if (k == 0) {
/* 1337 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/*      */ 
/* 1341 */       if (k >= 9)
/*      */       {
/*      */ 
/* 1344 */         m = Integer.parseInt(paramCharSequence.subSequence(paramInt, paramInt += k).toString());
/*      */       } else {
/* 1346 */         int n = paramInt;
/* 1347 */         if (j != 0) {
/* 1348 */           n++;
/*      */         }
/*      */         try {
/* 1351 */           m = paramCharSequence.charAt(n++) - '0';
/*      */         } catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
/* 1353 */           return paramInt ^ 0xFFFFFFFF;
/*      */         }
/* 1355 */         paramInt += k;
/* 1356 */         while (n < paramInt) {
/* 1357 */           m = (m << 3) + (m << 1) + paramCharSequence.charAt(n++) - 48;
/*      */         }
/* 1359 */         if (j != 0) {
/* 1360 */           m = -m;
/*      */         }
/*      */       }
/*      */       
/* 1364 */       paramDateTimeParserBucket.saveField(this.iFieldType, m);
/* 1365 */       return paramInt;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class UnpaddedNumber
/*      */     extends NumberFormatter
/*      */   {
/*      */     protected UnpaddedNumber(DateTimeFieldType paramDateTimeFieldType, int paramInt, boolean paramBoolean)
/*      */     {
/* 1375 */       super(paramInt, paramBoolean);
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1379 */       return this.iMaxParsedDigits;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale) throws IOException
/*      */     {
/*      */       try
/*      */       {
/* 1386 */         DateTimeField localDateTimeField = this.iFieldType.getField(paramChronology);
/* 1387 */         FormatUtils.appendUnpaddedInteger(paramAppendable, localDateTimeField.get(paramLong));
/*      */       } catch (RuntimeException localRuntimeException) {
/* 1389 */         paramAppendable.append(65533);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 1394 */       if (paramReadablePartial.isSupported(this.iFieldType)) {
/*      */         try {
/* 1396 */           FormatUtils.appendUnpaddedInteger(paramAppendable, paramReadablePartial.get(this.iFieldType));
/*      */         } catch (RuntimeException localRuntimeException) {
/* 1398 */           paramAppendable.append(65533);
/*      */         }
/*      */       } else {
/* 1401 */         paramAppendable.append(65533);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class PaddedNumber
/*      */     extends NumberFormatter
/*      */   {
/*      */     protected final int iMinPrintedDigits;
/*      */     
/*      */     protected PaddedNumber(DateTimeFieldType paramDateTimeFieldType, int paramInt1, boolean paramBoolean, int paramInt2)
/*      */     {
/* 1414 */       super(paramInt1, paramBoolean);
/* 1415 */       this.iMinPrintedDigits = paramInt2;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1419 */       return this.iMaxParsedDigits;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale) throws IOException
/*      */     {
/*      */       try
/*      */       {
/* 1426 */         DateTimeField localDateTimeField = this.iFieldType.getField(paramChronology);
/* 1427 */         FormatUtils.appendPaddedInteger(paramAppendable, localDateTimeField.get(paramLong), this.iMinPrintedDigits);
/*      */       } catch (RuntimeException localRuntimeException) {
/* 1429 */         DateTimeFormatterBuilder.appendUnknownString(paramAppendable, this.iMinPrintedDigits);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 1434 */       if (paramReadablePartial.isSupported(this.iFieldType)) {
/*      */         try {
/* 1436 */           FormatUtils.appendPaddedInteger(paramAppendable, paramReadablePartial.get(this.iFieldType), this.iMinPrintedDigits);
/*      */         } catch (RuntimeException localRuntimeException) {
/* 1438 */           DateTimeFormatterBuilder.appendUnknownString(paramAppendable, this.iMinPrintedDigits);
/*      */         }
/*      */       } else {
/* 1441 */         DateTimeFormatterBuilder.appendUnknownString(paramAppendable, this.iMinPrintedDigits);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static class FixedNumber extends PaddedNumber
/*      */   {
/*      */     protected FixedNumber(DateTimeFieldType paramDateTimeFieldType, int paramInt, boolean paramBoolean)
/*      */     {
/* 1450 */       super(paramInt, paramBoolean, paramInt);
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt)
/*      */     {
/* 1455 */       int i = super.parseInto(paramDateTimeParserBucket, paramCharSequence, paramInt);
/* 1456 */       if (i < 0) {
/* 1457 */         return i;
/*      */       }
/* 1459 */       int j = paramInt + this.iMaxParsedDigits;
/* 1460 */       if (i != j) {
/* 1461 */         if (this.iSigned) {
/* 1462 */           int k = paramCharSequence.charAt(paramInt);
/* 1463 */           if ((k == 45) || (k == 43)) {
/* 1464 */             j++;
/*      */           }
/*      */         }
/* 1467 */         if (i > j)
/*      */         {
/* 1469 */           return j + 1 ^ 0xFFFFFFFF; }
/* 1470 */         if (i < j)
/*      */         {
/* 1472 */           return i ^ 0xFFFFFFFF;
/*      */         }
/*      */       }
/* 1475 */       return i;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class TwoDigitYear
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final DateTimeFieldType iType;
/*      */     
/*      */     private final int iPivot;
/*      */     
/*      */     private final boolean iLenientParse;
/*      */     
/*      */     TwoDigitYear(DateTimeFieldType paramDateTimeFieldType, int paramInt, boolean paramBoolean)
/*      */     {
/* 1491 */       this.iType = paramDateTimeFieldType;
/* 1492 */       this.iPivot = paramInt;
/* 1493 */       this.iLenientParse = paramBoolean;
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 1497 */       return this.iLenientParse ? 4 : 2;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 1501 */       int i = paramCharSequence.length() - paramInt;
/*      */       int i1;
/* 1503 */       if (!this.iLenientParse) {
/* 1504 */         i = Math.min(2, i);
/* 1505 */         if (i < 2) {
/* 1506 */           return paramInt ^ 0xFFFFFFFF;
/*      */         }
/*      */       } else {
/* 1509 */         j = 0;
/* 1510 */         k = 0;
/* 1511 */         m = 0;
/* 1512 */         while (m < i) {
/* 1513 */           n = paramCharSequence.charAt(paramInt + m);
/* 1514 */           if ((m == 0) && ((n == 45) || (n == 43))) {
/* 1515 */             j = 1;
/* 1516 */             k = n == 45 ? 1 : 0;
/* 1517 */             if (k != 0) {
/* 1518 */               m++;
/*      */             }
/*      */             else {
/* 1521 */               paramInt++;
/* 1522 */               i--;
/*      */             }
/*      */           }
/*      */           else {
/* 1526 */             if ((n < 48) || (n > 57)) {
/*      */               break;
/*      */             }
/* 1529 */             m++;
/*      */           }
/*      */         }
/* 1532 */         if (m == 0) {
/* 1533 */           return paramInt ^ 0xFFFFFFFF;
/*      */         }
/*      */         
/* 1536 */         if ((j != 0) || (m != 2))
/*      */         {
/* 1538 */           if (m >= 9)
/*      */           {
/*      */ 
/* 1541 */             n = Integer.parseInt(paramCharSequence.subSequence(paramInt, paramInt += m).toString());
/*      */           } else {
/* 1543 */             i1 = paramInt;
/* 1544 */             if (k != 0) {
/* 1545 */               i1++;
/*      */             }
/*      */             try {
/* 1548 */               n = paramCharSequence.charAt(i1++) - '0';
/*      */             } catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
/* 1550 */               return paramInt ^ 0xFFFFFFFF;
/*      */             }
/* 1552 */             paramInt += m;
/* 1553 */             while (i1 < paramInt) {
/* 1554 */               n = (n << 3) + (n << 1) + paramCharSequence.charAt(i1++) - 48;
/*      */             }
/* 1556 */             if (k != 0) {
/* 1557 */               n = -n;
/*      */             }
/*      */           }
/*      */           
/* 1561 */           paramDateTimeParserBucket.saveField(this.iType, n);
/* 1562 */           return paramInt;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1567 */       int k = paramCharSequence.charAt(paramInt);
/* 1568 */       if ((k < 48) || (k > 57)) {
/* 1569 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/* 1571 */       int j = k - 48;
/* 1572 */       k = paramCharSequence.charAt(paramInt + 1);
/* 1573 */       if ((k < 48) || (k > 57)) {
/* 1574 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/* 1576 */       j = (j << 3) + (j << 1) + k - 48;
/*      */       
/* 1578 */       int m = this.iPivot;
/*      */       
/* 1580 */       if (paramDateTimeParserBucket.getPivotYear() != null) {
/* 1581 */         m = paramDateTimeParserBucket.getPivotYear().intValue();
/*      */       }
/*      */       
/* 1584 */       int n = m - 50;
/*      */       
/*      */ 
/* 1587 */       if (n >= 0) {
/* 1588 */         i1 = n % 100;
/*      */       } else {
/* 1590 */         i1 = 99 + (n + 1) % 100;
/*      */       }
/*      */       
/* 1593 */       j += n + (j < i1 ? 100 : 0) - i1;
/*      */       
/* 1595 */       paramDateTimeParserBucket.saveField(this.iType, j);
/* 1596 */       return paramInt + 2;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1600 */       return 2;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1606 */       int i = getTwoDigitYear(paramLong, paramChronology);
/* 1607 */       if (i < 0) {
/* 1608 */         paramAppendable.append(65533);
/* 1609 */         paramAppendable.append(65533);
/*      */       } else {
/* 1611 */         FormatUtils.appendPaddedInteger(paramAppendable, i, 2);
/*      */       }
/*      */     }
/*      */     
/*      */     private int getTwoDigitYear(long paramLong, Chronology paramChronology) {
/*      */       try {
/* 1617 */         int i = this.iType.getField(paramChronology).get(paramLong);
/* 1618 */         if (i < 0) {
/* 1619 */           i = -i;
/*      */         }
/* 1621 */         return i % 100;
/*      */       } catch (RuntimeException localRuntimeException) {}
/* 1623 */       return -1;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException
/*      */     {
/* 1628 */       int i = getTwoDigitYear(paramReadablePartial);
/* 1629 */       if (i < 0) {
/* 1630 */         paramAppendable.append(65533);
/* 1631 */         paramAppendable.append(65533);
/*      */       } else {
/* 1633 */         FormatUtils.appendPaddedInteger(paramAppendable, i, 2);
/*      */       }
/*      */     }
/*      */     
/*      */     private int getTwoDigitYear(ReadablePartial paramReadablePartial) {
/* 1638 */       if (paramReadablePartial.isSupported(this.iType)) {
/*      */         try {
/* 1640 */           int i = paramReadablePartial.get(this.iType);
/* 1641 */           if (i < 0) {
/* 1642 */             i = -i;
/*      */           }
/* 1644 */           return i % 100;
/*      */         } catch (RuntimeException localRuntimeException) {}
/*      */       }
/* 1647 */       return -1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class TextField
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/* 1655 */     private static Map<Locale, Map<DateTimeFieldType, Object[]>> cParseCache = new ConcurrentHashMap();
/*      */     
/*      */     private final DateTimeFieldType iFieldType;
/*      */     private final boolean iShort;
/*      */     
/*      */     TextField(DateTimeFieldType paramDateTimeFieldType, boolean paramBoolean)
/*      */     {
/* 1662 */       this.iFieldType = paramDateTimeFieldType;
/* 1663 */       this.iShort = paramBoolean;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1667 */       return this.iShort ? 6 : 20;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale) throws IOException
/*      */     {
/*      */       try
/*      */       {
/* 1674 */         paramAppendable.append(print(paramLong, paramChronology, paramLocale));
/*      */       } catch (RuntimeException localRuntimeException) {
/* 1676 */         paramAppendable.append(65533);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/*      */       try {
/* 1682 */         paramAppendable.append(print(paramReadablePartial, paramLocale));
/*      */       } catch (RuntimeException localRuntimeException) {
/* 1684 */         paramAppendable.append(65533);
/*      */       }
/*      */     }
/*      */     
/*      */     private String print(long paramLong, Chronology paramChronology, Locale paramLocale) {
/* 1689 */       DateTimeField localDateTimeField = this.iFieldType.getField(paramChronology);
/* 1690 */       if (this.iShort) {
/* 1691 */         return localDateTimeField.getAsShortText(paramLong, paramLocale);
/*      */       }
/* 1693 */       return localDateTimeField.getAsText(paramLong, paramLocale);
/*      */     }
/*      */     
/*      */     private String print(ReadablePartial paramReadablePartial, Locale paramLocale)
/*      */     {
/* 1698 */       if (paramReadablePartial.isSupported(this.iFieldType)) {
/* 1699 */         DateTimeField localDateTimeField = this.iFieldType.getField(paramReadablePartial.getChronology());
/* 1700 */         if (this.iShort) {
/* 1701 */           return localDateTimeField.getAsShortText(paramReadablePartial, paramLocale);
/*      */         }
/* 1703 */         return localDateTimeField.getAsText(paramReadablePartial, paramLocale);
/*      */       }
/*      */       
/* 1706 */       return "�";
/*      */     }
/*      */     
/*      */     public int estimateParsedLength()
/*      */     {
/* 1711 */       return estimatePrintedLength();
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt)
/*      */     {
/* 1716 */       Locale localLocale = paramDateTimeParserBucket.getLocale();
/*      */       
/*      */ 
/* 1719 */       Object localObject1 = null;
/* 1720 */       int i = 0;
/* 1721 */       Object localObject2 = (Map)cParseCache.get(localLocale);
/* 1722 */       if (localObject2 == null) {
/* 1723 */         localObject2 = new ConcurrentHashMap();
/* 1724 */         cParseCache.put(localLocale, localObject2);
/*      */       }
/* 1726 */       Object[] arrayOfObject = (Object[])((Map)localObject2).get(this.iFieldType);
/* 1727 */       if (arrayOfObject == null) {
/* 1728 */         localObject1 = new ConcurrentHashMap(32);
/* 1729 */         MutableDateTime localMutableDateTime = new MutableDateTime(0L, DateTimeZone.UTC);
/* 1730 */         localProperty2 = localMutableDateTime.property(this.iFieldType);
/* 1731 */         int j = localProperty2.getMinimumValueOverall();
/* 1732 */         int k = localProperty2.getMaximumValueOverall();
/* 1733 */         if (k - j > 32) {
/* 1734 */           return paramInt ^ 0xFFFFFFFF;
/*      */         }
/* 1736 */         i = localProperty2.getMaximumTextLength(localLocale);
/* 1737 */         for (int m = j; m <= k; m++) {
/* 1738 */           localProperty2.set(m);
/* 1739 */           ((Map)localObject1).put(localProperty2.getAsShortText(localLocale), Boolean.TRUE);
/* 1740 */           ((Map)localObject1).put(localProperty2.getAsShortText(localLocale).toLowerCase(localLocale), Boolean.TRUE);
/* 1741 */           ((Map)localObject1).put(localProperty2.getAsShortText(localLocale).toUpperCase(localLocale), Boolean.TRUE);
/* 1742 */           ((Map)localObject1).put(localProperty2.getAsText(localLocale), Boolean.TRUE);
/* 1743 */           ((Map)localObject1).put(localProperty2.getAsText(localLocale).toLowerCase(localLocale), Boolean.TRUE);
/* 1744 */           ((Map)localObject1).put(localProperty2.getAsText(localLocale).toUpperCase(localLocale), Boolean.TRUE);
/*      */         }
/* 1746 */         if (("en".equals(localLocale.getLanguage())) && (this.iFieldType == DateTimeFieldType.era()))
/*      */         {
/* 1748 */           ((Map)localObject1).put("BCE", Boolean.TRUE);
/* 1749 */           ((Map)localObject1).put("bce", Boolean.TRUE);
/* 1750 */           ((Map)localObject1).put("CE", Boolean.TRUE);
/* 1751 */           ((Map)localObject1).put("ce", Boolean.TRUE);
/* 1752 */           i = 3;
/*      */         }
/* 1754 */         arrayOfObject = new Object[] { localObject1, Integer.valueOf(i) };
/* 1755 */         ((Map)localObject2).put(this.iFieldType, arrayOfObject);
/*      */       } else {
/* 1757 */         localObject1 = (Map)arrayOfObject[0];
/* 1758 */         i = ((Integer)arrayOfObject[1]).intValue();
/*      */       }
/*      */       
/* 1761 */       MutableDateTime.Property localProperty1 = Math.min(paramCharSequence.length(), paramInt + i);
/* 1762 */       for (MutableDateTime.Property localProperty2 = localProperty1; localProperty2 > paramInt; localProperty2--) {
/* 1763 */         String str = paramCharSequence.subSequence(paramInt, localProperty2).toString();
/* 1764 */         if (((Map)localObject1).containsKey(str)) {
/* 1765 */           paramDateTimeParserBucket.saveField(this.iFieldType, str, localLocale);
/* 1766 */           return localProperty2;
/*      */         }
/*      */       }
/* 1769 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class Fraction
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final DateTimeFieldType iFieldType;
/*      */     protected int iMinDigits;
/*      */     protected int iMaxDigits;
/*      */     
/*      */     protected Fraction(DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2)
/*      */     {
/* 1783 */       this.iFieldType = paramDateTimeFieldType;
/*      */       
/* 1785 */       if (paramInt2 > 18) {
/* 1786 */         paramInt2 = 18;
/*      */       }
/* 1788 */       this.iMinDigits = paramInt1;
/* 1789 */       this.iMaxDigits = paramInt2;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1793 */       return this.iMaxDigits;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1799 */       printTo(paramAppendable, paramLong, paramChronology);
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1805 */       long l = paramReadablePartial.getChronology().set(paramReadablePartial, 0L);
/* 1806 */       printTo(paramAppendable, l, paramReadablePartial.getChronology());
/*      */     }
/*      */     
/*      */     protected void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology)
/*      */       throws IOException
/*      */     {
/* 1812 */       DateTimeField localDateTimeField = this.iFieldType.getField(paramChronology);
/* 1813 */       int i = this.iMinDigits;
/*      */       long l1;
/*      */       try
/*      */       {
/* 1817 */         l1 = localDateTimeField.remainder(paramLong);
/*      */       } catch (RuntimeException localRuntimeException) {
/* 1819 */         DateTimeFormatterBuilder.appendUnknownString(paramAppendable, i);
/* 1820 */         return;
/*      */       }
/*      */       
/* 1823 */       if (l1 == 0L) {
/* 1824 */         for (;;) { i--; if (i < 0) break;
/* 1825 */           paramAppendable.append('0');
/*      */         }
/* 1827 */         return;
/*      */       }
/*      */       
/*      */ 
/* 1831 */       long[] arrayOfLong = getFractionData(l1, localDateTimeField);
/* 1832 */       long l2 = arrayOfLong[0];
/* 1833 */       int j = (int)arrayOfLong[1];
/*      */       String str;
/* 1835 */       if ((l2 & 0x7FFFFFFF) == l2) {
/* 1836 */         str = Integer.toString((int)l2);
/*      */       } else {
/* 1838 */         str = Long.toString(l2);
/*      */       }
/*      */       
/* 1841 */       int k = str.length();
/* 1842 */       int m = j;
/* 1843 */       while (k < m) {
/* 1844 */         paramAppendable.append('0');
/* 1845 */         i--;
/* 1846 */         m--;
/*      */       }
/*      */       
/* 1849 */       if (i < m)
/*      */       {
/* 1851 */         while ((i < m) && 
/* 1852 */           (k > 1) && (str.charAt(k - 1) == '0'))
/*      */         {
/*      */ 
/* 1855 */           m--;
/* 1856 */           k--;
/*      */         }
/* 1858 */         if (k < str.length()) {
/* 1859 */           for (int n = 0; n < k; n++) {
/* 1860 */             paramAppendable.append(str.charAt(n));
/*      */           }
/* 1862 */           return;
/*      */         }
/*      */       }
/*      */       
/* 1866 */       paramAppendable.append(str);
/*      */     }
/*      */     
/*      */     private long[] getFractionData(long paramLong, DateTimeField paramDateTimeField) {
/* 1870 */       long l1 = paramDateTimeField.getDurationField().getUnitMillis();
/*      */       
/* 1872 */       int i = this.iMaxDigits;
/*      */       long l2;
/* 1874 */       for (;;) { switch (i) {
/* 1875 */         default:  l2 = 1L; break;
/* 1876 */         case 1:  l2 = 10L; break;
/* 1877 */         case 2:  l2 = 100L; break;
/* 1878 */         case 3:  l2 = 1000L; break;
/* 1879 */         case 4:  l2 = 10000L; break;
/* 1880 */         case 5:  l2 = 100000L; break;
/* 1881 */         case 6:  l2 = 1000000L; break;
/* 1882 */         case 7:  l2 = 10000000L; break;
/* 1883 */         case 8:  l2 = 100000000L; break;
/* 1884 */         case 9:  l2 = 1000000000L; break;
/* 1885 */         case 10:  l2 = 10000000000L; break;
/* 1886 */         case 11:  l2 = 100000000000L; break;
/* 1887 */         case 12:  l2 = 1000000000000L; break;
/* 1888 */         case 13:  l2 = 10000000000000L; break;
/* 1889 */         case 14:  l2 = 100000000000000L; break;
/* 1890 */         case 15:  l2 = 1000000000000000L; break;
/* 1891 */         case 16:  l2 = 10000000000000000L; break;
/* 1892 */         case 17:  l2 = 100000000000000000L; break;
/* 1893 */         case 18:  l2 = 1000000000000000000L;
/*      */         }
/* 1895 */         if (l1 * l2 / l2 == l1) {
/*      */           break;
/*      */         }
/*      */         
/* 1899 */         i--;
/*      */       }
/*      */       
/* 1902 */       return new long[] { paramLong * l2 / l1, i };
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 1906 */       return this.iMaxDigits;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 1910 */       DateTimeField localDateTimeField = this.iFieldType.getField(paramDateTimeParserBucket.getChronology());
/*      */       
/* 1912 */       int i = Math.min(this.iMaxDigits, paramCharSequence.length() - paramInt);
/*      */       
/* 1914 */       long l1 = 0L;
/* 1915 */       long l2 = localDateTimeField.getDurationField().getUnitMillis() * 10L;
/* 1916 */       int j = 0;
/* 1917 */       while (j < i) {
/* 1918 */         int k = paramCharSequence.charAt(paramInt + j);
/* 1919 */         if ((k < 48) || (k > 57)) {
/*      */           break;
/*      */         }
/* 1922 */         j++;
/* 1923 */         long l3 = l2 / 10L;
/* 1924 */         l1 += (k - 48) * l3;
/* 1925 */         l2 = l3;
/*      */       }
/*      */       
/* 1928 */       l1 /= 10L;
/*      */       
/* 1930 */       if (j == 0) {
/* 1931 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 1934 */       if (l1 > 2147483647L) {
/* 1935 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 1938 */       PreciseDateTimeField localPreciseDateTimeField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), MillisDurationField.INSTANCE, localDateTimeField.getDurationField());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1943 */       paramDateTimeParserBucket.saveField(localPreciseDateTimeField, (int)l1);
/*      */       
/* 1945 */       return paramInt + j;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class TimeZoneOffset
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final String iZeroOffsetPrintText;
/*      */     
/*      */     private final String iZeroOffsetParseText;
/*      */     
/*      */     private final boolean iShowSeparators;
/*      */     
/*      */     private final int iMinFields;
/*      */     private final int iMaxFields;
/*      */     
/*      */     TimeZoneOffset(String paramString1, String paramString2, boolean paramBoolean, int paramInt1, int paramInt2)
/*      */     {
/* 1964 */       this.iZeroOffsetPrintText = paramString1;
/* 1965 */       this.iZeroOffsetParseText = paramString2;
/* 1966 */       this.iShowSeparators = paramBoolean;
/* 1967 */       if ((paramInt1 <= 0) || (paramInt2 < paramInt1)) {
/* 1968 */         throw new IllegalArgumentException();
/*      */       }
/* 1970 */       if (paramInt1 > 4) {
/* 1971 */         paramInt1 = 4;
/* 1972 */         paramInt2 = 4;
/*      */       }
/* 1974 */       this.iMinFields = paramInt1;
/* 1975 */       this.iMaxFields = paramInt2;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 1979 */       int i = 1 + this.iMinFields << 1;
/* 1980 */       if (this.iShowSeparators) {
/* 1981 */         i += this.iMinFields - 1;
/*      */       }
/* 1983 */       if ((this.iZeroOffsetPrintText != null) && (this.iZeroOffsetPrintText.length() > i)) {
/* 1984 */         i = this.iZeroOffsetPrintText.length();
/*      */       }
/* 1986 */       return i;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 1992 */       if (paramDateTimeZone == null) {
/* 1993 */         return;
/*      */       }
/* 1995 */       if ((paramInt == 0) && (this.iZeroOffsetPrintText != null)) {
/* 1996 */         paramAppendable.append(this.iZeroOffsetPrintText);
/* 1997 */         return;
/*      */       }
/* 1999 */       if (paramInt >= 0) {
/* 2000 */         paramAppendable.append('+');
/*      */       } else {
/* 2002 */         paramAppendable.append('-');
/* 2003 */         paramInt = -paramInt;
/*      */       }
/*      */       
/* 2006 */       int i = paramInt / 3600000;
/* 2007 */       FormatUtils.appendPaddedInteger(paramAppendable, i, 2);
/* 2008 */       if (this.iMaxFields == 1) {
/* 2009 */         return;
/*      */       }
/* 2011 */       paramInt -= i * 3600000;
/* 2012 */       if ((paramInt == 0) && (this.iMinFields <= 1)) {
/* 2013 */         return;
/*      */       }
/*      */       
/* 2016 */       int j = paramInt / 60000;
/* 2017 */       if (this.iShowSeparators) {
/* 2018 */         paramAppendable.append(':');
/*      */       }
/* 2020 */       FormatUtils.appendPaddedInteger(paramAppendable, j, 2);
/* 2021 */       if (this.iMaxFields == 2) {
/* 2022 */         return;
/*      */       }
/* 2024 */       paramInt -= j * 60000;
/* 2025 */       if ((paramInt == 0) && (this.iMinFields <= 2)) {
/* 2026 */         return;
/*      */       }
/*      */       
/* 2029 */       int k = paramInt / 1000;
/* 2030 */       if (this.iShowSeparators) {
/* 2031 */         paramAppendable.append(':');
/*      */       }
/* 2033 */       FormatUtils.appendPaddedInteger(paramAppendable, k, 2);
/* 2034 */       if (this.iMaxFields == 3) {
/* 2035 */         return;
/*      */       }
/* 2037 */       paramInt -= k * 1000;
/* 2038 */       if ((paramInt == 0) && (this.iMinFields <= 3)) {
/* 2039 */         return;
/*      */       }
/*      */       
/* 2042 */       if (this.iShowSeparators) {
/* 2043 */         paramAppendable.append('.');
/*      */       }
/* 2045 */       FormatUtils.appendPaddedInteger(paramAppendable, paramInt, 3);
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException
/*      */     {}
/*      */     
/*      */     public int estimateParsedLength()
/*      */     {
/* 2053 */       return estimatePrintedLength();
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 2057 */       int i = paramCharSequence.length() - paramInt;
/*      */       
/*      */       int j;
/* 2060 */       if (this.iZeroOffsetParseText != null) {
/* 2061 */         if (this.iZeroOffsetParseText.length() == 0)
/*      */         {
/* 2063 */           if (i > 0) {
/* 2064 */             j = paramCharSequence.charAt(paramInt);
/* 2065 */             if ((j == 45) || (j == 43)) {}
/*      */           }
/*      */           else
/*      */           {
/* 2069 */             paramDateTimeParserBucket.setOffset(Integer.valueOf(0));
/* 2070 */             return paramInt;
/*      */           }
/* 2072 */         } else if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(paramCharSequence, paramInt, this.iZeroOffsetParseText)) {
/* 2073 */           paramDateTimeParserBucket.setOffset(Integer.valueOf(0));
/* 2074 */           return paramInt + this.iZeroOffsetParseText.length();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2080 */       if (i <= 1) {
/* 2081 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/*      */ 
/* 2085 */       int k = paramCharSequence.charAt(paramInt);
/* 2086 */       if (k == 45) {
/* 2087 */         j = 1;
/* 2088 */       } else if (k == 43) {
/* 2089 */         j = 0;
/*      */       } else {
/* 2091 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 2094 */       i--;
/* 2095 */       paramInt++;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2109 */       if (digitCount(paramCharSequence, paramInt, 2) < 2)
/*      */       {
/* 2111 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2116 */       int m = FormatUtils.parseTwoDigits(paramCharSequence, paramInt);
/* 2117 */       if (m > 23) {
/* 2118 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/* 2120 */       int n = m * 3600000;
/* 2121 */       i -= 2;
/* 2122 */       paramInt += 2;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2128 */       if (i > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2133 */         k = paramCharSequence.charAt(paramInt);
/* 2134 */         int i1; if (k == 58) {
/* 2135 */           i1 = 1;
/* 2136 */           i--;
/* 2137 */           paramInt++;
/* 2138 */         } else { if ((k < 48) || (k > 57)) break label569;
/* 2139 */           i1 = 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2146 */         int i2 = digitCount(paramCharSequence, paramInt, 2);
/* 2147 */         if ((i2 != 0) || (i1 != 0))
/*      */         {
/* 2149 */           if (i2 < 2)
/*      */           {
/* 2151 */             return paramInt ^ 0xFFFFFFFF;
/*      */           }
/*      */           
/* 2154 */           int i3 = FormatUtils.parseTwoDigits(paramCharSequence, paramInt);
/* 2155 */           if (i3 > 59) {
/* 2156 */             return paramInt ^ 0xFFFFFFFF;
/*      */           }
/* 2158 */           n += i3 * 60000;
/* 2159 */           i -= 2;
/* 2160 */           paramInt += 2;
/*      */           
/*      */ 
/*      */ 
/* 2164 */           if (i > 0)
/*      */           {
/*      */ 
/*      */ 
/* 2168 */             if (i1 != 0) {
/* 2169 */               if (paramCharSequence.charAt(paramInt) == ':')
/*      */               {
/*      */ 
/* 2172 */                 i--;
/* 2173 */                 paramInt++;
/*      */               }
/*      */             } else {
/* 2176 */               i2 = digitCount(paramCharSequence, paramInt, 2);
/* 2177 */               if ((i2 != 0) || (i1 != 0))
/*      */               {
/* 2179 */                 if (i2 < 2)
/*      */                 {
/* 2181 */                   return paramInt ^ 0xFFFFFFFF;
/*      */                 }
/*      */                 
/* 2184 */                 int i4 = FormatUtils.parseTwoDigits(paramCharSequence, paramInt);
/* 2185 */                 if (i4 > 59) {
/* 2186 */                   return paramInt ^ 0xFFFFFFFF;
/*      */                 }
/* 2188 */                 n += i4 * 1000;
/* 2189 */                 i -= 2;
/* 2190 */                 paramInt += 2;
/*      */                 
/*      */ 
/*      */ 
/* 2194 */                 if (i > 0)
/*      */                 {
/*      */ 
/*      */ 
/* 2198 */                   if (i1 != 0) {
/* 2199 */                     if ((paramCharSequence.charAt(paramInt) == '.') || (paramCharSequence.charAt(paramInt) == ','))
/*      */                     {
/*      */ 
/* 2202 */                       i--;
/* 2203 */                       paramInt++;
/*      */                     }
/*      */                   } else {
/* 2206 */                     i2 = digitCount(paramCharSequence, paramInt, 3);
/* 2207 */                     if ((i2 != 0) || (i1 != 0))
/*      */                     {
/* 2209 */                       if (i2 < 1)
/*      */                       {
/* 2211 */                         return paramInt ^ 0xFFFFFFFF;
/*      */                       }
/*      */                       
/* 2214 */                       n += (paramCharSequence.charAt(paramInt++) - '0') * 100;
/* 2215 */                       if (i2 > 1) {
/* 2216 */                         n += (paramCharSequence.charAt(paramInt++) - '0') * 10;
/* 2217 */                         if (i2 > 2)
/* 2218 */                           n += paramCharSequence.charAt(paramInt++) - '0';
/*      */                       }
/*      */                     }
/*      */                   } } } } } } }
/*      */       label569:
/* 2223 */       paramDateTimeParserBucket.setOffset(Integer.valueOf(j != 0 ? -n : n));
/* 2224 */       return paramInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int digitCount(CharSequence paramCharSequence, int paramInt1, int paramInt2)
/*      */     {
/* 2232 */       int i = Math.min(paramCharSequence.length() - paramInt1, paramInt2);
/* 2233 */       paramInt2 = 0;
/* 2234 */       for (; i > 0; i--) {
/* 2235 */         int j = paramCharSequence.charAt(paramInt1 + paramInt2);
/* 2236 */         if ((j < 48) || (j > 57)) {
/*      */           break;
/*      */         }
/* 2239 */         paramInt2++;
/*      */       }
/* 2241 */       return paramInt2;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class TimeZoneName
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/*      */     static final int LONG_NAME = 0;
/*      */     
/*      */     static final int SHORT_NAME = 1;
/*      */     private final Map<String, DateTimeZone> iParseLookup;
/*      */     private final int iType;
/*      */     
/*      */     TimeZoneName(int paramInt, Map<String, DateTimeZone> paramMap)
/*      */     {
/* 2257 */       this.iType = paramInt;
/* 2258 */       this.iParseLookup = paramMap;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 2262 */       return this.iType == 1 ? 4 : 20;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 2268 */       paramAppendable.append(print(paramLong - paramInt, paramDateTimeZone, paramLocale));
/*      */     }
/*      */     
/*      */     private String print(long paramLong, DateTimeZone paramDateTimeZone, Locale paramLocale) {
/* 2272 */       if (paramDateTimeZone == null) {
/* 2273 */         return "";
/*      */       }
/* 2275 */       switch (this.iType) {
/*      */       case 0: 
/* 2277 */         return paramDateTimeZone.getName(paramLong, paramLocale);
/*      */       case 1: 
/* 2279 */         return paramDateTimeZone.getShortName(paramLong, paramLocale);
/*      */       }
/* 2281 */       return "";
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException
/*      */     {}
/*      */     
/*      */     public int estimateParsedLength()
/*      */     {
/* 2289 */       return this.iType == 1 ? 4 : 20;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 2293 */       Map localMap = this.iParseLookup;
/* 2294 */       localMap = localMap != null ? localMap : DateTimeUtils.getDefaultTimeZoneNames();
/* 2295 */       Object localObject = null;
/* 2296 */       for (String str : localMap.keySet()) {
/* 2297 */         if ((DateTimeFormatterBuilder.csStartsWith(paramCharSequence, paramInt, str)) && (
/* 2298 */           (localObject == null) || (str.length() > ((String)localObject).length()))) {
/* 2299 */           localObject = str;
/*      */         }
/*      */       }
/*      */       
/* 2303 */       if (localObject != null) {
/* 2304 */         paramDateTimeParserBucket.setZone((DateTimeZone)localMap.get(localObject));
/* 2305 */         return paramInt + ((String)localObject).length();
/*      */       }
/* 2307 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static enum TimeZoneId
/*      */     implements InternalPrinter, InternalParser
/*      */   {
/* 2315 */     INSTANCE;
/* 2316 */     static { ALL_IDS = DateTimeZone.getAvailableIDs();
/*      */       
/*      */ 
/* 2319 */       int i = 0;
/* 2320 */       for (String str : ALL_IDS) {
/* 2321 */         i = Math.max(i, str.length());
/*      */       }
/* 2323 */       MAX_LENGTH = i;
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 2327 */       return MAX_LENGTH;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 2333 */       paramAppendable.append(paramDateTimeZone != null ? paramDateTimeZone.getID() : "");
/*      */     }
/*      */     
/*      */ 
/*      */     static final Set<String> ALL_IDS;
/*      */     static final int MAX_LENGTH;
/*      */     public int estimateParsedLength()
/*      */     {
/* 2341 */       return MAX_LENGTH;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 2345 */       Object localObject = null;
/* 2346 */       for (String str : ALL_IDS) {
/* 2347 */         if ((DateTimeFormatterBuilder.csStartsWith(paramCharSequence, paramInt, str)) && (
/* 2348 */           (localObject == null) || (str.length() > ((String)localObject).length()))) {
/* 2349 */           localObject = str;
/*      */         }
/*      */       }
/*      */       
/* 2353 */       if (localObject != null) {
/* 2354 */         paramDateTimeParserBucket.setZone(DateTimeZone.forID((String)localObject));
/* 2355 */         return paramInt + ((String)localObject).length();
/*      */       }
/* 2357 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     private TimeZoneId() {}
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException
/*      */     {}
/*      */   }
/*      */   
/*      */   static class Composite implements InternalPrinter, InternalParser
/*      */   {
/*      */     private final InternalPrinter[] iPrinters;
/*      */     private final InternalParser[] iParsers;
/*      */     private final int iPrintedLengthEstimate;
/*      */     private final int iParsedLengthEstimate;
/*      */     
/*      */     Composite(List<Object> paramList) {
/* 2374 */       ArrayList localArrayList1 = new ArrayList();
/* 2375 */       ArrayList localArrayList2 = new ArrayList();
/*      */       
/* 2377 */       decompose(paramList, localArrayList1, localArrayList2);
/*      */       int i;
/* 2379 */       int j; int k; Object localObject; if ((localArrayList1.contains(null)) || (localArrayList1.isEmpty())) {
/* 2380 */         this.iPrinters = null;
/* 2381 */         this.iPrintedLengthEstimate = 0;
/*      */       } else {
/* 2383 */         i = localArrayList1.size();
/* 2384 */         this.iPrinters = new InternalPrinter[i];
/* 2385 */         j = 0;
/* 2386 */         for (k = 0; k < i; k++) {
/* 2387 */           localObject = (InternalPrinter)localArrayList1.get(k);
/* 2388 */           j += ((InternalPrinter)localObject).estimatePrintedLength();
/* 2389 */           this.iPrinters[k] = localObject;
/*      */         }
/* 2391 */         this.iPrintedLengthEstimate = j;
/*      */       }
/*      */       
/* 2394 */       if ((localArrayList2.contains(null)) || (localArrayList2.isEmpty())) {
/* 2395 */         this.iParsers = null;
/* 2396 */         this.iParsedLengthEstimate = 0;
/*      */       } else {
/* 2398 */         i = localArrayList2.size();
/* 2399 */         this.iParsers = new InternalParser[i];
/* 2400 */         j = 0;
/* 2401 */         for (k = 0; k < i; k++) {
/* 2402 */           localObject = (InternalParser)localArrayList2.get(k);
/* 2403 */           j += ((InternalParser)localObject).estimateParsedLength();
/* 2404 */           this.iParsers[k] = localObject;
/*      */         }
/* 2406 */         this.iParsedLengthEstimate = j;
/*      */       }
/*      */     }
/*      */     
/*      */     public int estimatePrintedLength() {
/* 2411 */       return this.iPrintedLengthEstimate;
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*      */       throws IOException
/*      */     {
/* 2417 */       InternalPrinter[] arrayOfInternalPrinter = this.iPrinters;
/* 2418 */       if (arrayOfInternalPrinter == null) {
/* 2419 */         throw new UnsupportedOperationException();
/*      */       }
/*      */       
/* 2422 */       if (paramLocale == null)
/*      */       {
/* 2424 */         paramLocale = Locale.getDefault();
/*      */       }
/*      */       
/* 2427 */       int i = arrayOfInternalPrinter.length;
/* 2428 */       for (int j = 0; j < i; j++) {
/* 2429 */         arrayOfInternalPrinter[j].printTo(paramAppendable, paramLong, paramChronology, paramInt, paramDateTimeZone, paramLocale);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 2434 */       InternalPrinter[] arrayOfInternalPrinter = this.iPrinters;
/* 2435 */       if (arrayOfInternalPrinter == null) {
/* 2436 */         throw new UnsupportedOperationException();
/*      */       }
/*      */       
/* 2439 */       if (paramLocale == null)
/*      */       {
/* 2441 */         paramLocale = Locale.getDefault();
/*      */       }
/*      */       
/* 2444 */       int i = arrayOfInternalPrinter.length;
/* 2445 */       for (int j = 0; j < i; j++) {
/* 2446 */         arrayOfInternalPrinter[j].printTo(paramAppendable, paramReadablePartial, paramLocale);
/*      */       }
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 2451 */       return this.iParsedLengthEstimate;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 2455 */       InternalParser[] arrayOfInternalParser = this.iParsers;
/* 2456 */       if (arrayOfInternalParser == null) {
/* 2457 */         throw new UnsupportedOperationException();
/*      */       }
/*      */       
/* 2460 */       int i = arrayOfInternalParser.length;
/* 2461 */       for (int j = 0; (j < i) && (paramInt >= 0); j++) {
/* 2462 */         paramInt = arrayOfInternalParser[j].parseInto(paramDateTimeParserBucket, paramCharSequence, paramInt);
/*      */       }
/* 2464 */       return paramInt;
/*      */     }
/*      */     
/*      */     boolean isPrinter() {
/* 2468 */       return this.iPrinters != null;
/*      */     }
/*      */     
/*      */     boolean isParser() {
/* 2472 */       return this.iParsers != null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void decompose(List<Object> paramList1, List<Object> paramList2, List<Object> paramList3)
/*      */     {
/* 2480 */       int i = paramList1.size();
/* 2481 */       for (int j = 0; j < i; j += 2) {
/* 2482 */         Object localObject = paramList1.get(j);
/* 2483 */         if ((localObject instanceof Composite)) {
/* 2484 */           addArrayToList(paramList2, ((Composite)localObject).iPrinters);
/*      */         } else {
/* 2486 */           paramList2.add(localObject);
/*      */         }
/*      */         
/* 2489 */         localObject = paramList1.get(j + 1);
/* 2490 */         if ((localObject instanceof Composite)) {
/* 2491 */           addArrayToList(paramList3, ((Composite)localObject).iParsers);
/*      */         } else {
/* 2493 */           paramList3.add(localObject);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     private void addArrayToList(List<Object> paramList, Object[] paramArrayOfObject) {
/* 2499 */       if (paramArrayOfObject != null) {
/* 2500 */         for (int i = 0; i < paramArrayOfObject.length; i++) {
/* 2501 */           paramList.add(paramArrayOfObject[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class MatchingParser
/*      */     implements InternalParser
/*      */   {
/*      */     private final InternalParser[] iParsers;
/*      */     private final int iParsedLengthEstimate;
/*      */     
/*      */     MatchingParser(InternalParser[] paramArrayOfInternalParser)
/*      */     {
/* 2516 */       this.iParsers = paramArrayOfInternalParser;
/* 2517 */       int i = 0;
/* 2518 */       int j = paramArrayOfInternalParser.length; for (;;) { j--; if (j < 0) break;
/* 2519 */         InternalParser localInternalParser = paramArrayOfInternalParser[j];
/* 2520 */         if (localInternalParser != null) {
/* 2521 */           int k = localInternalParser.estimateParsedLength();
/* 2522 */           if (k > i) {
/* 2523 */             i = k;
/*      */           }
/*      */         }
/*      */       }
/* 2527 */       this.iParsedLengthEstimate = i;
/*      */     }
/*      */     
/*      */     public int estimateParsedLength() {
/* 2531 */       return this.iParsedLengthEstimate;
/*      */     }
/*      */     
/*      */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 2535 */       InternalParser[] arrayOfInternalParser = this.iParsers;
/* 2536 */       int i = arrayOfInternalParser.length;
/*      */       
/* 2538 */       Object localObject1 = paramDateTimeParserBucket.saveState();
/* 2539 */       int j = 0;
/*      */       
/* 2541 */       int k = paramInt;
/* 2542 */       Object localObject2 = null;
/*      */       
/* 2544 */       int m = paramInt;
/*      */       
/* 2546 */       for (int n = 0; n < i; n++) {
/* 2547 */         InternalParser localInternalParser = arrayOfInternalParser[n];
/* 2548 */         if (localInternalParser == null)
/*      */         {
/* 2550 */           if (k <= paramInt) {
/* 2551 */             return paramInt;
/*      */           }
/* 2553 */           j = 1;
/* 2554 */           break;
/*      */         }
/* 2556 */         int i1 = localInternalParser.parseInto(paramDateTimeParserBucket, paramCharSequence, paramInt);
/* 2557 */         if (i1 >= paramInt) {
/* 2558 */           if (i1 > k) {
/* 2559 */             if ((i1 >= paramCharSequence.length()) || (n + 1 >= i) || (arrayOfInternalParser[(n + 1)] == null))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2564 */               return i1;
/*      */             }
/* 2566 */             k = i1;
/* 2567 */             localObject2 = paramDateTimeParserBucket.saveState();
/*      */           }
/*      */         }
/* 2570 */         else if (i1 < 0) {
/* 2571 */           i1 ^= 0xFFFFFFFF;
/* 2572 */           if (i1 > m) {
/* 2573 */             m = i1;
/*      */           }
/*      */         }
/*      */         
/* 2577 */         paramDateTimeParserBucket.restoreState(localObject1);
/*      */       }
/*      */       
/* 2580 */       if ((k > paramInt) || ((k == paramInt) && (j != 0)))
/*      */       {
/* 2582 */         if (localObject2 != null) {
/* 2583 */           paramDateTimeParserBucket.restoreState(localObject2);
/*      */         }
/* 2585 */         return k;
/*      */       }
/*      */       
/* 2588 */       return m ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */   
/*      */   static boolean csStartsWith(CharSequence paramCharSequence, int paramInt, String paramString) {
/* 2593 */     int i = paramString.length();
/* 2594 */     if (paramCharSequence.length() - paramInt < i) {
/* 2595 */       return false;
/*      */     }
/* 2597 */     for (int j = 0; j < i; j++) {
/* 2598 */       if (paramCharSequence.charAt(paramInt + j) != paramString.charAt(j)) {
/* 2599 */         return false;
/*      */       }
/*      */     }
/* 2602 */     return true;
/*      */   }
/*      */   
/*      */   static boolean csStartsWithIgnoreCase(CharSequence paramCharSequence, int paramInt, String paramString) {
/* 2606 */     int i = paramString.length();
/* 2607 */     if (paramCharSequence.length() - paramInt < i) {
/* 2608 */       return false;
/*      */     }
/* 2610 */     for (int j = 0; j < i; j++) {
/* 2611 */       char c1 = paramCharSequence.charAt(paramInt + j);
/* 2612 */       char c2 = paramString.charAt(j);
/* 2613 */       if (c1 != c2) {
/* 2614 */         char c3 = Character.toUpperCase(c1);
/* 2615 */         char c4 = Character.toUpperCase(c2);
/* 2616 */         if ((c3 != c4) && (Character.toLowerCase(c3) != Character.toLowerCase(c4))) {
/* 2617 */           return false;
/*      */         }
/*      */       }
/*      */     }
/* 2621 */     return true;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimeFormatterBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */