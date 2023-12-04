/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutableDateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public class DateTimeFormatter
/*     */ {
/*     */   private final InternalPrinter iPrinter;
/*     */   private final InternalParser iParser;
/*     */   private final Locale iLocale;
/*     */   private final boolean iOffsetParsed;
/*     */   private final Chronology iChrono;
/*     */   private final DateTimeZone iZone;
/*     */   private final Integer iPivotYear;
/*     */   private final int iDefaultYear;
/*     */   
/*     */   public DateTimeFormatter(DateTimePrinter paramDateTimePrinter, DateTimeParser paramDateTimeParser)
/*     */   {
/* 118 */     this(DateTimePrinterInternalPrinter.of(paramDateTimePrinter), DateTimeParserInternalParser.of(paramDateTimeParser));
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
/*     */   DateTimeFormatter(InternalPrinter paramInternalPrinter, InternalParser paramInternalParser)
/*     */   {
/* 131 */     this.iPrinter = paramInternalPrinter;
/* 132 */     this.iParser = paramInternalParser;
/* 133 */     this.iLocale = null;
/* 134 */     this.iOffsetParsed = false;
/* 135 */     this.iChrono = null;
/* 136 */     this.iZone = null;
/* 137 */     this.iPivotYear = null;
/* 138 */     this.iDefaultYear = 2000;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DateTimeFormatter(InternalPrinter paramInternalPrinter, InternalParser paramInternalParser, Locale paramLocale, boolean paramBoolean, Chronology paramChronology, DateTimeZone paramDateTimeZone, Integer paramInteger, int paramInt)
/*     */   {
/* 150 */     this.iPrinter = paramInternalPrinter;
/* 151 */     this.iParser = paramInternalParser;
/* 152 */     this.iLocale = paramLocale;
/* 153 */     this.iOffsetParsed = paramBoolean;
/* 154 */     this.iChrono = paramChronology;
/* 155 */     this.iZone = paramDateTimeZone;
/* 156 */     this.iPivotYear = paramInteger;
/* 157 */     this.iDefaultYear = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isPrinter()
/*     */   {
/* 167 */     return this.iPrinter != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimePrinter getPrinter()
/*     */   {
/* 176 */     return InternalPrinterDateTimePrinter.of(this.iPrinter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   InternalPrinter getPrinter0()
/*     */   {
/* 185 */     return this.iPrinter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isParser()
/*     */   {
/* 194 */     return this.iParser != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeParser getParser()
/*     */   {
/* 203 */     return InternalParserDateTimeParser.of(this.iParser);
/*     */   }
/*     */   
/*     */   InternalParser getParser0() {
/* 207 */     return this.iParser;
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
/*     */   public DateTimeFormatter withLocale(Locale paramLocale)
/*     */   {
/* 223 */     if ((paramLocale == getLocale()) || ((paramLocale != null) && (paramLocale.equals(getLocale())))) {
/* 224 */       return this;
/*     */     }
/* 226 */     return new DateTimeFormatter(this.iPrinter, this.iParser, paramLocale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, this.iDefaultYear);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 237 */     return this.iLocale;
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
/*     */   public DateTimeFormatter withOffsetParsed()
/*     */   {
/* 256 */     if (this.iOffsetParsed == true) {
/* 257 */       return this;
/*     */     }
/* 259 */     return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, true, this.iChrono, null, this.iPivotYear, this.iDefaultYear);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isOffsetParsed()
/*     */   {
/* 270 */     return this.iOffsetParsed;
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
/*     */   public DateTimeFormatter withChronology(Chronology paramChronology)
/*     */   {
/* 291 */     if (this.iChrono == paramChronology) {
/* 292 */       return this;
/*     */     }
/* 294 */     return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, paramChronology, this.iZone, this.iPivotYear, this.iDefaultYear);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology getChronology()
/*     */   {
/* 304 */     return this.iChrono;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public Chronology getChronolgy()
/*     */   {
/* 315 */     return this.iChrono;
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
/*     */   public DateTimeFormatter withZoneUTC()
/*     */   {
/* 335 */     return withZone(DateTimeZone.UTC);
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
/*     */   public DateTimeFormatter withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 355 */     if (this.iZone == paramDateTimeZone) {
/* 356 */       return this;
/*     */     }
/* 358 */     return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, false, this.iChrono, paramDateTimeZone, this.iPivotYear, this.iDefaultYear);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeZone getZone()
/*     */   {
/* 368 */     return this.iZone;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFormatter withPivotYear(Integer paramInteger)
/*     */   {
/* 402 */     if ((this.iPivotYear == paramInteger) || ((this.iPivotYear != null) && (this.iPivotYear.equals(paramInteger)))) {
/* 403 */       return this;
/*     */     }
/* 405 */     return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, paramInteger, this.iDefaultYear);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFormatter withPivotYear(int paramInt)
/*     */   {
/* 439 */     return withPivotYear(Integer.valueOf(paramInt));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getPivotYear()
/*     */   {
/* 449 */     return this.iPivotYear;
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
/*     */   public DateTimeFormatter withDefaultYear(int paramInt)
/*     */   {
/* 472 */     return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDefaultYear()
/*     */   {
/* 483 */     return this.iDefaultYear;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(StringBuffer paramStringBuffer, ReadableInstant paramReadableInstant)
/*     */   {
/*     */     try
/*     */     {
/* 495 */       printTo(paramStringBuffer, paramReadableInstant);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(Writer paramWriter, ReadableInstant paramReadableInstant)
/*     */     throws IOException
/*     */   {
/* 508 */     printTo(paramWriter, paramReadableInstant);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(Appendable paramAppendable, ReadableInstant paramReadableInstant)
/*     */     throws IOException
/*     */   {
/* 519 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 520 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 521 */     printTo(paramAppendable, l, localChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(StringBuffer paramStringBuffer, long paramLong)
/*     */   {
/*     */     try
/*     */     {
/* 534 */       printTo(paramStringBuffer, paramLong);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(Writer paramWriter, long paramLong)
/*     */     throws IOException
/*     */   {
/* 548 */     printTo(paramWriter, paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(Appendable paramAppendable, long paramLong)
/*     */     throws IOException
/*     */   {
/* 560 */     printTo(paramAppendable, paramLong, null);
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
/*     */   public void printTo(StringBuffer paramStringBuffer, ReadablePartial paramReadablePartial)
/*     */   {
/*     */     try
/*     */     {
/* 575 */       printTo(paramStringBuffer, paramReadablePartial);
/*     */     }
/*     */     catch (IOException localIOException) {}
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
/*     */   public void printTo(Writer paramWriter, ReadablePartial paramReadablePartial)
/*     */     throws IOException
/*     */   {
/* 591 */     printTo(paramWriter, paramReadablePartial);
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
/*     */   public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial)
/*     */     throws IOException
/*     */   {
/* 605 */     InternalPrinter localInternalPrinter = requirePrinter();
/* 606 */     if (paramReadablePartial == null) {
/* 607 */       throw new IllegalArgumentException("The partial must not be null");
/*     */     }
/* 609 */     localInternalPrinter.printTo(paramAppendable, paramReadablePartial, this.iLocale);
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
/*     */   public String print(ReadableInstant paramReadableInstant)
/*     */   {
/* 623 */     StringBuilder localStringBuilder = new StringBuilder(requirePrinter().estimatePrintedLength());
/*     */     try {
/* 625 */       printTo(localStringBuilder, paramReadableInstant);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */     
/* 629 */     return localStringBuilder.toString();
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
/*     */   public String print(long paramLong)
/*     */   {
/* 642 */     StringBuilder localStringBuilder = new StringBuilder(requirePrinter().estimatePrintedLength());
/*     */     try {
/* 644 */       printTo(localStringBuilder, paramLong);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */     
/* 648 */     return localStringBuilder.toString();
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
/*     */   public String print(ReadablePartial paramReadablePartial)
/*     */   {
/* 661 */     StringBuilder localStringBuilder = new StringBuilder(requirePrinter().estimatePrintedLength());
/*     */     try {
/* 663 */       printTo(localStringBuilder, paramReadablePartial);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */     
/* 667 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */   private void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology) throws IOException {
/* 671 */     InternalPrinter localInternalPrinter = requirePrinter();
/* 672 */     paramChronology = selectChronology(paramChronology);
/*     */     
/*     */ 
/* 675 */     DateTimeZone localDateTimeZone = paramChronology.getZone();
/* 676 */     int i = localDateTimeZone.getOffset(paramLong);
/* 677 */     long l = paramLong + i;
/* 678 */     if (((paramLong ^ l) < 0L) && ((paramLong ^ i) >= 0L))
/*     */     {
/* 680 */       localDateTimeZone = DateTimeZone.UTC;
/* 681 */       i = 0;
/* 682 */       l = paramLong;
/*     */     }
/* 684 */     localInternalPrinter.printTo(paramAppendable, l, paramChronology.withUTC(), i, localDateTimeZone, this.iLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private InternalPrinter requirePrinter()
/*     */   {
/* 693 */     InternalPrinter localInternalPrinter = this.iPrinter;
/* 694 */     if (localInternalPrinter == null) {
/* 695 */       throw new UnsupportedOperationException("Printing not supported");
/*     */     }
/* 697 */     return localInternalPrinter;
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
/*     */   public int parseInto(ReadWritableInstant paramReadWritableInstant, String paramString, int paramInt)
/*     */   {
/* 735 */     InternalParser localInternalParser = requireParser();
/* 736 */     if (paramReadWritableInstant == null) {
/* 737 */       throw new IllegalArgumentException("Instant must not be null");
/*     */     }
/*     */     
/* 740 */     long l1 = paramReadWritableInstant.getMillis();
/* 741 */     Chronology localChronology = paramReadWritableInstant.getChronology();
/* 742 */     int i = DateTimeUtils.getChronology(localChronology).year().get(l1);
/* 743 */     long l2 = l1 + localChronology.getZone().getOffset(l1);
/* 744 */     localChronology = selectChronology(localChronology);
/*     */     
/* 746 */     DateTimeParserBucket localDateTimeParserBucket = new DateTimeParserBucket(l2, localChronology, this.iLocale, this.iPivotYear, i);
/*     */     
/* 748 */     int j = localInternalParser.parseInto(localDateTimeParserBucket, paramString, paramInt);
/* 749 */     paramReadWritableInstant.setMillis(localDateTimeParserBucket.computeMillis(false, paramString));
/* 750 */     if ((this.iOffsetParsed) && (localDateTimeParserBucket.getOffsetInteger() != null)) {
/* 751 */       int k = localDateTimeParserBucket.getOffsetInteger().intValue();
/* 752 */       DateTimeZone localDateTimeZone = DateTimeZone.forOffsetMillis(k);
/* 753 */       localChronology = localChronology.withZone(localDateTimeZone);
/* 754 */     } else if (localDateTimeParserBucket.getZone() != null) {
/* 755 */       localChronology = localChronology.withZone(localDateTimeParserBucket.getZone());
/*     */     }
/* 757 */     paramReadWritableInstant.setChronology(localChronology);
/* 758 */     if (this.iZone != null) {
/* 759 */       paramReadWritableInstant.setZone(this.iZone);
/*     */     }
/* 761 */     return j;
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
/*     */   public long parseMillis(String paramString)
/*     */   {
/* 777 */     InternalParser localInternalParser = requireParser();
/* 778 */     Chronology localChronology = selectChronology(this.iChrono);
/* 779 */     DateTimeParserBucket localDateTimeParserBucket = new DateTimeParserBucket(0L, localChronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
/* 780 */     return localDateTimeParserBucket.doParseMillis(localInternalParser, paramString);
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
/*     */   public LocalDate parseLocalDate(String paramString)
/*     */   {
/* 798 */     return parseLocalDateTime(paramString).toLocalDate();
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
/*     */   public LocalTime parseLocalTime(String paramString)
/*     */   {
/* 816 */     return parseLocalDateTime(paramString).toLocalTime();
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
/*     */   public LocalDateTime parseLocalDateTime(String paramString)
/*     */   {
/* 834 */     InternalParser localInternalParser = requireParser();
/*     */     
/* 836 */     Chronology localChronology = selectChronology(null).withUTC();
/* 837 */     DateTimeParserBucket localDateTimeParserBucket = new DateTimeParserBucket(0L, localChronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
/* 838 */     int i = localInternalParser.parseInto(localDateTimeParserBucket, paramString, 0);
/* 839 */     if (i >= 0) {
/* 840 */       if (i >= paramString.length()) {
/* 841 */         long l = localDateTimeParserBucket.computeMillis(true, paramString);
/* 842 */         if (localDateTimeParserBucket.getOffsetInteger() != null) {
/* 843 */           int j = localDateTimeParserBucket.getOffsetInteger().intValue();
/* 844 */           DateTimeZone localDateTimeZone = DateTimeZone.forOffsetMillis(j);
/* 845 */           localChronology = localChronology.withZone(localDateTimeZone);
/* 846 */         } else if (localDateTimeParserBucket.getZone() != null) {
/* 847 */           localChronology = localChronology.withZone(localDateTimeParserBucket.getZone());
/*     */         }
/* 849 */         return new LocalDateTime(l, localChronology);
/*     */       }
/*     */     } else {
/* 852 */       i ^= 0xFFFFFFFF;
/*     */     }
/* 854 */     throw new IllegalArgumentException(FormatUtils.createErrorMessage(paramString, i));
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
/*     */   public DateTime parseDateTime(String paramString)
/*     */   {
/* 875 */     InternalParser localInternalParser = requireParser();
/*     */     
/* 877 */     Chronology localChronology = selectChronology(null);
/* 878 */     DateTimeParserBucket localDateTimeParserBucket = new DateTimeParserBucket(0L, localChronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
/* 879 */     int i = localInternalParser.parseInto(localDateTimeParserBucket, paramString, 0);
/* 880 */     if (i >= 0) {
/* 881 */       if (i >= paramString.length()) {
/* 882 */         long l = localDateTimeParserBucket.computeMillis(true, paramString);
/* 883 */         if ((this.iOffsetParsed) && (localDateTimeParserBucket.getOffsetInteger() != null)) {
/* 884 */           int j = localDateTimeParserBucket.getOffsetInteger().intValue();
/* 885 */           DateTimeZone localDateTimeZone = DateTimeZone.forOffsetMillis(j);
/* 886 */           localChronology = localChronology.withZone(localDateTimeZone);
/* 887 */         } else if (localDateTimeParserBucket.getZone() != null) {
/* 888 */           localChronology = localChronology.withZone(localDateTimeParserBucket.getZone());
/*     */         }
/* 890 */         DateTime localDateTime = new DateTime(l, localChronology);
/* 891 */         if (this.iZone != null) {
/* 892 */           localDateTime = localDateTime.withZone(this.iZone);
/*     */         }
/* 894 */         return localDateTime;
/*     */       }
/*     */     } else {
/* 897 */       i ^= 0xFFFFFFFF;
/*     */     }
/* 899 */     throw new IllegalArgumentException(FormatUtils.createErrorMessage(paramString, i));
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
/*     */   public MutableDateTime parseMutableDateTime(String paramString)
/*     */   {
/* 920 */     InternalParser localInternalParser = requireParser();
/*     */     
/* 922 */     Chronology localChronology = selectChronology(null);
/* 923 */     DateTimeParserBucket localDateTimeParserBucket = new DateTimeParserBucket(0L, localChronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
/* 924 */     int i = localInternalParser.parseInto(localDateTimeParserBucket, paramString, 0);
/* 925 */     if (i >= 0) {
/* 926 */       if (i >= paramString.length()) {
/* 927 */         long l = localDateTimeParserBucket.computeMillis(true, paramString);
/* 928 */         if ((this.iOffsetParsed) && (localDateTimeParserBucket.getOffsetInteger() != null)) {
/* 929 */           int j = localDateTimeParserBucket.getOffsetInteger().intValue();
/* 930 */           DateTimeZone localDateTimeZone = DateTimeZone.forOffsetMillis(j);
/* 931 */           localChronology = localChronology.withZone(localDateTimeZone);
/* 932 */         } else if (localDateTimeParserBucket.getZone() != null) {
/* 933 */           localChronology = localChronology.withZone(localDateTimeParserBucket.getZone());
/*     */         }
/* 935 */         MutableDateTime localMutableDateTime = new MutableDateTime(l, localChronology);
/* 936 */         if (this.iZone != null) {
/* 937 */           localMutableDateTime.setZone(this.iZone);
/*     */         }
/* 939 */         return localMutableDateTime;
/*     */       }
/*     */     } else {
/* 942 */       i ^= 0xFFFFFFFF;
/*     */     }
/* 944 */     throw new IllegalArgumentException(FormatUtils.createErrorMessage(paramString, i));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private InternalParser requireParser()
/*     */   {
/* 953 */     InternalParser localInternalParser = this.iParser;
/* 954 */     if (localInternalParser == null) {
/* 955 */       throw new UnsupportedOperationException("Parsing not supported");
/*     */     }
/* 957 */     return localInternalParser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Chronology selectChronology(Chronology paramChronology)
/*     */   {
/* 968 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 969 */     if (this.iChrono != null) {
/* 970 */       paramChronology = this.iChrono;
/*     */     }
/* 972 */     if (this.iZone != null) {
/* 973 */       paramChronology = paramChronology.withZone(this.iZone);
/*     */     }
/* 975 */     return paramChronology;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimeFormatter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */