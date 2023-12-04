/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import java.io.IOException;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DateTimeFormat
/*     */ {
/*     */   static final int FULL = 0;
/*     */   static final int LONG = 1;
/*     */   static final int MEDIUM = 2;
/*     */   static final int SHORT = 3;
/*     */   static final int NONE = 4;
/*     */   static final int DATE = 0;
/*     */   static final int TIME = 1;
/*     */   static final int DATETIME = 2;
/*     */   private static final int PATTERN_CACHE_SIZE = 500;
/* 154 */   private static final ConcurrentHashMap<String, DateTimeFormatter> cPatternCache = new ConcurrentHashMap();
/*     */   
/* 156 */   private static final AtomicReferenceArray<DateTimeFormatter> cStyleCache = new AtomicReferenceArray(25);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter forPattern(String paramString)
/*     */   {
/* 177 */     return createFormatterForPattern(paramString);
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
/*     */   public static DateTimeFormatter forStyle(String paramString)
/*     */   {
/* 201 */     return createFormatterForStyle(paramString);
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
/*     */   public static String patternForStyle(String paramString, Locale paramLocale)
/*     */   {
/* 219 */     DateTimeFormatter localDateTimeFormatter = createFormatterForStyle(paramString);
/* 220 */     if (paramLocale == null) {
/* 221 */       paramLocale = Locale.getDefault();
/*     */     }
/*     */     
/* 224 */     return ((StyleFormatter)localDateTimeFormatter.getPrinter0()).getPattern(paramLocale);
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
/*     */   public static DateTimeFormatter shortDate()
/*     */   {
/* 237 */     return createFormatterForStyleIndex(3, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter shortTime()
/*     */   {
/* 249 */     return createFormatterForStyleIndex(4, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter shortDateTime()
/*     */   {
/* 261 */     return createFormatterForStyleIndex(3, 3);
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
/*     */   public static DateTimeFormatter mediumDate()
/*     */   {
/* 274 */     return createFormatterForStyleIndex(2, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter mediumTime()
/*     */   {
/* 286 */     return createFormatterForStyleIndex(4, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter mediumDateTime()
/*     */   {
/* 298 */     return createFormatterForStyleIndex(2, 2);
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
/*     */   public static DateTimeFormatter longDate()
/*     */   {
/* 311 */     return createFormatterForStyleIndex(1, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter longTime()
/*     */   {
/* 323 */     return createFormatterForStyleIndex(4, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter longDateTime()
/*     */   {
/* 335 */     return createFormatterForStyleIndex(1, 1);
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
/*     */   public static DateTimeFormatter fullDate()
/*     */   {
/* 348 */     return createFormatterForStyleIndex(0, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter fullTime()
/*     */   {
/* 360 */     return createFormatterForStyleIndex(4, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DateTimeFormatter fullDateTime()
/*     */   {
/* 372 */     return createFormatterForStyleIndex(0, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void appendPatternTo(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, String paramString)
/*     */   {
/* 384 */     parsePatternTo(paramDateTimeFormatterBuilder, paramString);
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
/*     */   private static void parsePatternTo(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, String paramString)
/*     */   {
/* 407 */     int i = paramString.length();
/* 408 */     int[] arrayOfInt = new int[1];
/*     */     
/* 410 */     for (int j = 0; j < i; j++) {
/* 411 */       arrayOfInt[0] = j;
/* 412 */       String str1 = parseToken(paramString, arrayOfInt);
/* 413 */       j = arrayOfInt[0];
/*     */       
/* 415 */       int k = str1.length();
/* 416 */       if (k == 0) {
/*     */         break;
/*     */       }
/* 419 */       int m = str1.charAt(0);
/*     */       
/* 421 */       switch (m) {
/*     */       case 71: 
/* 423 */         paramDateTimeFormatterBuilder.appendEraText();
/* 424 */         break;
/*     */       case 67: 
/* 426 */         paramDateTimeFormatterBuilder.appendCenturyOfEra(k, k);
/* 427 */         break;
/*     */       case 89: 
/*     */       case 120: 
/*     */       case 121: 
/* 431 */         if (k == 2) {
/* 432 */           boolean bool = true;
/*     */           
/*     */ 
/* 435 */           if (j + 1 < i) {
/* 436 */             arrayOfInt[0] += 1;
/* 437 */             if (isNumericToken(parseToken(paramString, arrayOfInt)))
/*     */             {
/*     */ 
/*     */ 
/* 441 */               bool = false;
/*     */             }
/* 443 */             arrayOfInt[0] -= 1;
/*     */           }
/*     */           
/*     */ 
/* 447 */           switch (m) {
/*     */           case 120: 
/* 449 */             paramDateTimeFormatterBuilder.appendTwoDigitWeekyear(new DateTime().getWeekyear() - 30, bool);
/*     */             
/* 451 */             break;
/*     */           case 89: 
/*     */           case 121: 
/*     */           default: 
/* 455 */             paramDateTimeFormatterBuilder.appendTwoDigitYear(new DateTime().getYear() - 30, bool);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 460 */           int n = 9;
/*     */           
/*     */ 
/* 463 */           if (j + 1 < i) {
/* 464 */             arrayOfInt[0] += 1;
/* 465 */             if (isNumericToken(parseToken(paramString, arrayOfInt)))
/*     */             {
/* 467 */               n = k;
/*     */             }
/* 469 */             arrayOfInt[0] -= 1;
/*     */           }
/*     */           
/* 472 */           switch (m) {
/*     */           case 120: 
/* 474 */             paramDateTimeFormatterBuilder.appendWeekyear(k, n);
/* 475 */             break;
/*     */           case 121: 
/* 477 */             paramDateTimeFormatterBuilder.appendYear(k, n);
/* 478 */             break;
/*     */           case 89: 
/* 480 */             paramDateTimeFormatterBuilder.appendYearOfEra(k, n);
/*     */           }
/*     */           
/*     */         }
/* 484 */         break;
/*     */       case 77: 
/* 486 */         if (k >= 3) {
/* 487 */           if (k >= 4) {
/* 488 */             paramDateTimeFormatterBuilder.appendMonthOfYearText();
/*     */           } else {
/* 490 */             paramDateTimeFormatterBuilder.appendMonthOfYearShortText();
/*     */           }
/*     */         } else {
/* 493 */           paramDateTimeFormatterBuilder.appendMonthOfYear(k);
/*     */         }
/* 495 */         break;
/*     */       case 100: 
/* 497 */         paramDateTimeFormatterBuilder.appendDayOfMonth(k);
/* 498 */         break;
/*     */       case 97: 
/* 500 */         paramDateTimeFormatterBuilder.appendHalfdayOfDayText();
/* 501 */         break;
/*     */       case 104: 
/* 503 */         paramDateTimeFormatterBuilder.appendClockhourOfHalfday(k);
/* 504 */         break;
/*     */       case 72: 
/* 506 */         paramDateTimeFormatterBuilder.appendHourOfDay(k);
/* 507 */         break;
/*     */       case 107: 
/* 509 */         paramDateTimeFormatterBuilder.appendClockhourOfDay(k);
/* 510 */         break;
/*     */       case 75: 
/* 512 */         paramDateTimeFormatterBuilder.appendHourOfHalfday(k);
/* 513 */         break;
/*     */       case 109: 
/* 515 */         paramDateTimeFormatterBuilder.appendMinuteOfHour(k);
/* 516 */         break;
/*     */       case 115: 
/* 518 */         paramDateTimeFormatterBuilder.appendSecondOfMinute(k);
/* 519 */         break;
/*     */       case 83: 
/* 521 */         paramDateTimeFormatterBuilder.appendFractionOfSecond(k, k);
/* 522 */         break;
/*     */       case 101: 
/* 524 */         paramDateTimeFormatterBuilder.appendDayOfWeek(k);
/* 525 */         break;
/*     */       case 69: 
/* 527 */         if (k >= 4) {
/* 528 */           paramDateTimeFormatterBuilder.appendDayOfWeekText();
/*     */         } else {
/* 530 */           paramDateTimeFormatterBuilder.appendDayOfWeekShortText();
/*     */         }
/* 532 */         break;
/*     */       case 68: 
/* 534 */         paramDateTimeFormatterBuilder.appendDayOfYear(k);
/* 535 */         break;
/*     */       case 119: 
/* 537 */         paramDateTimeFormatterBuilder.appendWeekOfWeekyear(k);
/* 538 */         break;
/*     */       case 122: 
/* 540 */         if (k >= 4) {
/* 541 */           paramDateTimeFormatterBuilder.appendTimeZoneName();
/*     */         } else {
/* 543 */           paramDateTimeFormatterBuilder.appendTimeZoneShortName(null);
/*     */         }
/* 545 */         break;
/*     */       case 90: 
/* 547 */         if (k == 1) {
/* 548 */           paramDateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", false, 2, 2);
/* 549 */         } else if (k == 2) {
/* 550 */           paramDateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", true, 2, 2);
/*     */         } else {
/* 552 */           paramDateTimeFormatterBuilder.appendTimeZoneId();
/*     */         }
/* 554 */         break;
/*     */       case 39: 
/* 556 */         String str2 = str1.substring(1);
/* 557 */         if (str2.length() == 1) {
/* 558 */           paramDateTimeFormatterBuilder.appendLiteral(str2.charAt(0));
/*     */         }
/*     */         else
/*     */         {
/* 562 */           paramDateTimeFormatterBuilder.appendLiteral(new String(str2));
/*     */         }
/* 564 */         break;
/*     */       case 40: case 41: case 42: case 43: case 44: case 45: case 46: case 47: case 48: case 49: case 50: case 51: case 52: case 53: case 54: case 55: case 56: case 57: case 58: case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66: case 70: case 73: case 74: case 76: case 78: case 79: case 80: case 81: case 82: case 84: case 85: case 86: case 87: case 88: case 91: case 92: case 93: case 94: case 95: case 96: case 98: case 99: case 102: case 103: case 105: case 106: case 108: case 110: case 111: case 112: case 113: case 114: case 116: case 117: case 118: default: 
/* 566 */         throw new IllegalArgumentException("Illegal pattern component: " + str1);
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String parseToken(String paramString, int[] paramArrayOfInt)
/*     */   {
/* 581 */     StringBuilder localStringBuilder = new StringBuilder();
/*     */     
/* 583 */     int i = paramArrayOfInt[0];
/* 584 */     int j = paramString.length();
/*     */     
/* 586 */     char c1 = paramString.charAt(i);
/* 587 */     if (((c1 >= 'A') && (c1 <= 'Z')) || ((c1 >= 'a') && (c1 <= 'z')))
/*     */     {
/*     */ 
/* 590 */       localStringBuilder.append(c1);
/*     */     }
/* 592 */     while (i + 1 < j) {
/* 593 */       char c2 = paramString.charAt(i + 1);
/* 594 */       if (c2 == c1) {
/* 595 */         localStringBuilder.append(c1);
/* 596 */         i++;
/*     */         
/*     */ 
/*     */ 
/* 600 */         continue;
/*     */         
/*     */ 
/* 603 */         localStringBuilder.append('\'');
/*     */         
/* 605 */         c2 = '\000';
/* 607 */         for (; 
/* 607 */             i < j; i++) {
/* 608 */           c1 = paramString.charAt(i);
/*     */           
/* 610 */           if (c1 == '\'') {
/* 611 */             if ((i + 1 < j) && (paramString.charAt(i + 1) == '\''))
/*     */             {
/* 613 */               i++;
/* 614 */               localStringBuilder.append(c1);
/*     */             } else {
/* 616 */               c2 = c2 == 0 ? '\001' : '\000';
/*     */             }
/* 618 */           } else { if ((c2 == 0) && (((c1 >= 'A') && (c1 <= 'Z')) || ((c1 >= 'a') && (c1 <= 'z'))))
/*     */             {
/* 620 */               i--;
/* 621 */               break;
/*     */             }
/* 623 */             localStringBuilder.append(c1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 628 */     paramArrayOfInt[0] = i;
/* 629 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isNumericToken(String paramString)
/*     */   {
/* 639 */     int i = paramString.length();
/* 640 */     if (i > 0) {
/* 641 */       int j = paramString.charAt(0);
/* 642 */       switch (j) {
/*     */       case 67: 
/*     */       case 68: 
/*     */       case 70: 
/*     */       case 72: 
/*     */       case 75: 
/*     */       case 83: 
/*     */       case 87: 
/*     */       case 89: 
/*     */       case 99: 
/*     */       case 100: 
/*     */       case 101: 
/*     */       case 104: 
/*     */       case 107: 
/*     */       case 109: 
/*     */       case 115: 
/*     */       case 119: 
/*     */       case 120: 
/*     */       case 121: 
/* 661 */         return true;
/*     */       case 77: 
/* 663 */         if (i <= 2) {
/* 664 */           return true;
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 669 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static DateTimeFormatter createFormatterForPattern(String paramString)
/*     */   {
/* 681 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 682 */       throw new IllegalArgumentException("Invalid pattern specification");
/*     */     }
/* 684 */     Object localObject = (DateTimeFormatter)cPatternCache.get(paramString);
/* 685 */     if (localObject == null) {
/* 686 */       DateTimeFormatterBuilder localDateTimeFormatterBuilder = new DateTimeFormatterBuilder();
/* 687 */       parsePatternTo(localDateTimeFormatterBuilder, paramString);
/* 688 */       localObject = localDateTimeFormatterBuilder.toFormatter();
/* 689 */       if (cPatternCache.size() < 500)
/*     */       {
/*     */ 
/* 692 */         DateTimeFormatter localDateTimeFormatter = (DateTimeFormatter)cPatternCache.putIfAbsent(paramString, localObject);
/* 693 */         if (localDateTimeFormatter != null) {
/* 694 */           localObject = localDateTimeFormatter;
/*     */         }
/*     */       }
/*     */     }
/* 698 */     return (DateTimeFormatter)localObject;
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
/*     */   private static DateTimeFormatter createFormatterForStyle(String paramString)
/*     */   {
/* 711 */     if ((paramString == null) || (paramString.length() != 2)) {
/* 712 */       throw new IllegalArgumentException("Invalid style specification: " + paramString);
/*     */     }
/* 714 */     int i = selectStyle(paramString.charAt(0));
/* 715 */     int j = selectStyle(paramString.charAt(1));
/* 716 */     if ((i == 4) && (j == 4)) {
/* 717 */       throw new IllegalArgumentException("Style '--' is invalid");
/*     */     }
/* 719 */     return createFormatterForStyleIndex(i, j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static DateTimeFormatter createFormatterForStyleIndex(int paramInt1, int paramInt2)
/*     */   {
/* 730 */     int i = (paramInt1 << 2) + paramInt1 + paramInt2;
/*     */     
/* 732 */     if (i >= cStyleCache.length()) {
/* 733 */       return createDateTimeFormatter(paramInt1, paramInt2);
/*     */     }
/* 735 */     DateTimeFormatter localDateTimeFormatter = (DateTimeFormatter)cStyleCache.get(i);
/* 736 */     if (localDateTimeFormatter == null) {
/* 737 */       localDateTimeFormatter = createDateTimeFormatter(paramInt1, paramInt2);
/* 738 */       if (!cStyleCache.compareAndSet(i, null, localDateTimeFormatter)) {
/* 739 */         localDateTimeFormatter = (DateTimeFormatter)cStyleCache.get(i);
/*     */       }
/*     */     }
/* 742 */     return localDateTimeFormatter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static DateTimeFormatter createDateTimeFormatter(int paramInt1, int paramInt2)
/*     */   {
/* 753 */     int i = 2;
/* 754 */     if (paramInt1 == 4) {
/* 755 */       i = 1;
/* 756 */     } else if (paramInt2 == 4) {
/* 757 */       i = 0;
/*     */     }
/* 759 */     StyleFormatter localStyleFormatter = new StyleFormatter(paramInt1, paramInt2, i);
/* 760 */     return new DateTimeFormatter(localStyleFormatter, localStyleFormatter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int selectStyle(char paramChar)
/*     */   {
/* 770 */     switch (paramChar) {
/*     */     case 'S': 
/* 772 */       return 3;
/*     */     case 'M': 
/* 774 */       return 2;
/*     */     case 'L': 
/* 776 */       return 1;
/*     */     case 'F': 
/* 778 */       return 0;
/*     */     case '-': 
/* 780 */       return 4;
/*     */     }
/* 782 */     throw new IllegalArgumentException("Invalid style character: " + paramChar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class StyleFormatter
/*     */     implements InternalPrinter, InternalParser
/*     */   {
/* 790 */     private static final ConcurrentHashMap<StyleFormatterCacheKey, DateTimeFormatter> cCache = new ConcurrentHashMap();
/*     */     
/*     */     private final int iDateStyle;
/*     */     private final int iTimeStyle;
/*     */     private final int iType;
/*     */     
/*     */     StyleFormatter(int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 798 */       this.iDateStyle = paramInt1;
/* 799 */       this.iTimeStyle = paramInt2;
/* 800 */       this.iType = paramInt3;
/*     */     }
/*     */     
/*     */     public int estimatePrintedLength() {
/* 804 */       return 40;
/*     */     }
/*     */     
/*     */     public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
/*     */       throws IOException
/*     */     {
/* 810 */       InternalPrinter localInternalPrinter = getFormatter(paramLocale).getPrinter0();
/* 811 */       localInternalPrinter.printTo(paramAppendable, paramLong, paramChronology, paramInt, paramDateTimeZone, paramLocale);
/*     */     }
/*     */     
/*     */     public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 815 */       InternalPrinter localInternalPrinter = getFormatter(paramLocale).getPrinter0();
/* 816 */       localInternalPrinter.printTo(paramAppendable, paramReadablePartial, paramLocale);
/*     */     }
/*     */     
/*     */     public int estimateParsedLength() {
/* 820 */       return 40;
/*     */     }
/*     */     
/*     */     public int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt) {
/* 824 */       InternalParser localInternalParser = getFormatter(paramDateTimeParserBucket.getLocale()).getParser0();
/* 825 */       return localInternalParser.parseInto(paramDateTimeParserBucket, paramCharSequence, paramInt);
/*     */     }
/*     */     
/*     */     private DateTimeFormatter getFormatter(Locale paramLocale) {
/* 829 */       paramLocale = paramLocale == null ? Locale.getDefault() : paramLocale;
/* 830 */       StyleFormatterCacheKey localStyleFormatterCacheKey = new StyleFormatterCacheKey(this.iType, this.iDateStyle, this.iTimeStyle, paramLocale);
/* 831 */       Object localObject = (DateTimeFormatter)cCache.get(localStyleFormatterCacheKey);
/* 832 */       if (localObject == null) {
/* 833 */         localObject = DateTimeFormat.forPattern(getPattern(paramLocale));
/* 834 */         DateTimeFormatter localDateTimeFormatter = (DateTimeFormatter)cCache.putIfAbsent(localStyleFormatterCacheKey, localObject);
/* 835 */         if (localDateTimeFormatter != null) {
/* 836 */           localObject = localDateTimeFormatter;
/*     */         }
/*     */       }
/* 839 */       return (DateTimeFormatter)localObject;
/*     */     }
/*     */     
/*     */     String getPattern(Locale paramLocale) {
/* 843 */       DateFormat localDateFormat = null;
/* 844 */       switch (this.iType) {
/*     */       case 0: 
/* 846 */         localDateFormat = DateFormat.getDateInstance(this.iDateStyle, paramLocale);
/* 847 */         break;
/*     */       case 1: 
/* 849 */         localDateFormat = DateFormat.getTimeInstance(this.iTimeStyle, paramLocale);
/* 850 */         break;
/*     */       case 2: 
/* 852 */         localDateFormat = DateFormat.getDateTimeInstance(this.iDateStyle, this.iTimeStyle, paramLocale);
/*     */       }
/*     */       
/* 855 */       if (!(localDateFormat instanceof SimpleDateFormat)) {
/* 856 */         throw new IllegalArgumentException("No datetime pattern for locale: " + paramLocale);
/*     */       }
/* 858 */       return ((SimpleDateFormat)localDateFormat).toPattern();
/*     */     }
/*     */   }
/*     */   
/*     */   static class StyleFormatterCacheKey
/*     */   {
/*     */     private final int combinedTypeAndStyle;
/*     */     private final Locale locale;
/*     */     
/*     */     public StyleFormatterCacheKey(int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
/* 868 */       this.locale = paramLocale;
/*     */       
/* 870 */       this.combinedTypeAndStyle = (paramInt1 + (paramInt2 << 4) + (paramInt3 << 8));
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 876 */       int i = 1;
/* 877 */       i = 31 * i + this.combinedTypeAndStyle;
/* 878 */       i = 31 * i + (this.locale == null ? 0 : this.locale.hashCode());
/* 879 */       return i;
/*     */     }
/*     */     
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 884 */       if (this == paramObject) {
/* 885 */         return true;
/*     */       }
/* 887 */       if (paramObject == null) {
/* 888 */         return false;
/*     */       }
/* 890 */       if (!(paramObject instanceof StyleFormatterCacheKey)) {
/* 891 */         return false;
/*     */       }
/* 893 */       StyleFormatterCacheKey localStyleFormatterCacheKey = (StyleFormatterCacheKey)paramObject;
/* 894 */       if (this.combinedTypeAndStyle != localStyleFormatterCacheKey.combinedTypeAndStyle) {
/* 895 */         return false;
/*     */       }
/* 897 */       if (this.locale == null) {
/* 898 */         if (localStyleFormatterCacheKey.locale != null) {
/* 899 */           return false;
/*     */         }
/* 901 */       } else if (!this.locale.equals(localStyleFormatterCacheKey.locale)) {
/* 902 */         return false;
/*     */       }
/* 904 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimeFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */