/*      */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*      */ import java.io.IOException;
/*      */ import java.io.Writer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PeriodFormatterBuilder
/*      */ {
/*      */   private static final int PRINT_ZERO_RARELY_FIRST = 1;
/*      */   private static final int PRINT_ZERO_RARELY_LAST = 2;
/*      */   private static final int PRINT_ZERO_IF_SUPPORTED = 3;
/*      */   private static final int PRINT_ZERO_ALWAYS = 4;
/*      */   private static final int PRINT_ZERO_NEVER = 5;
/*      */   private static final int YEARS = 0;
/*      */   private static final int MONTHS = 1;
/*      */   private static final int WEEKS = 2;
/*      */   private static final int DAYS = 3;
/*      */   private static final int HOURS = 4;
/*      */   private static final int MINUTES = 5;
/*      */   private static final int SECONDS = 6;
/*      */   private static final int MILLIS = 7;
/*      */   private static final int SECONDS_MILLIS = 8;
/*      */   private static final int SECONDS_OPTIONAL_MILLIS = 9;
/*      */   private static final int MAX_FIELD = 9;
/*   91 */   private static final ConcurrentMap<String, Pattern> PATTERNS = new ConcurrentHashMap();
/*      */   
/*      */   private int iMinPrintedDigits;
/*      */   
/*      */   private int iPrintZeroSetting;
/*      */   
/*      */   private int iMaxParsedDigits;
/*      */   
/*      */   private boolean iRejectSignedValues;
/*      */   
/*      */   private PeriodFieldAffix iPrefix;
/*      */   
/*      */   private List<Object> iElementPairs;
/*      */   
/*      */   private boolean iNotPrinter;
/*      */   private boolean iNotParser;
/*      */   private FieldFormatter[] iFieldFormatters;
/*      */   
/*      */   public PeriodFormatterBuilder()
/*      */   {
/*  111 */     clear();
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
/*      */   public PeriodFormatter toFormatter()
/*      */   {
/*  132 */     PeriodFormatter localPeriodFormatter = toFormatter(this.iElementPairs, this.iNotPrinter, this.iNotParser);
/*  133 */     for (FieldFormatter localFieldFormatter : this.iFieldFormatters) {
/*  134 */       if (localFieldFormatter != null) {
/*  135 */         localFieldFormatter.finish(this.iFieldFormatters);
/*      */       }
/*      */     }
/*  138 */     this.iFieldFormatters = ((FieldFormatter[])this.iFieldFormatters.clone());
/*  139 */     return localPeriodFormatter;
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
/*      */   public PeriodPrinter toPrinter()
/*      */   {
/*  155 */     if (this.iNotPrinter) {
/*  156 */       return null;
/*      */     }
/*  158 */     return toFormatter().getPrinter();
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
/*      */   public PeriodParser toParser()
/*      */   {
/*  174 */     if (this.iNotParser) {
/*  175 */       return null;
/*      */     }
/*  177 */     return toFormatter().getParser();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clear()
/*      */   {
/*  185 */     this.iMinPrintedDigits = 1;
/*  186 */     this.iPrintZeroSetting = 2;
/*  187 */     this.iMaxParsedDigits = 10;
/*  188 */     this.iRejectSignedValues = false;
/*  189 */     this.iPrefix = null;
/*  190 */     if (this.iElementPairs == null) {
/*  191 */       this.iElementPairs = new ArrayList();
/*      */     } else {
/*  193 */       this.iElementPairs.clear();
/*      */     }
/*  195 */     this.iNotPrinter = false;
/*  196 */     this.iNotParser = false;
/*  197 */     this.iFieldFormatters = new FieldFormatter[10];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder append(PeriodFormatter paramPeriodFormatter)
/*      */   {
/*  206 */     if (paramPeriodFormatter == null) {
/*  207 */       throw new IllegalArgumentException("No formatter supplied");
/*      */     }
/*  209 */     clearPrefix();
/*  210 */     append0(paramPeriodFormatter.getPrinter(), paramPeriodFormatter.getParser());
/*  211 */     return this;
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
/*      */   public PeriodFormatterBuilder append(PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser)
/*      */   {
/*  226 */     if ((paramPeriodPrinter == null) && (paramPeriodParser == null)) {
/*  227 */       throw new IllegalArgumentException("No printer or parser supplied");
/*      */     }
/*  229 */     clearPrefix();
/*  230 */     append0(paramPeriodPrinter, paramPeriodParser);
/*  231 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendLiteral(String paramString)
/*      */   {
/*  242 */     if (paramString == null) {
/*  243 */       throw new IllegalArgumentException("Literal must not be null");
/*      */     }
/*  245 */     clearPrefix();
/*  246 */     Literal localLiteral = new Literal(paramString);
/*  247 */     append0(localLiteral, localLiteral);
/*  248 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder minimumPrintedDigits(int paramInt)
/*      */   {
/*  259 */     this.iMinPrintedDigits = paramInt;
/*  260 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder maximumParsedDigits(int paramInt)
/*      */   {
/*  270 */     this.iMaxParsedDigits = paramInt;
/*  271 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder rejectSignedValues(boolean paramBoolean)
/*      */   {
/*  280 */     this.iRejectSignedValues = paramBoolean;
/*  281 */     return this;
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
/*      */   public PeriodFormatterBuilder printZeroRarelyLast()
/*      */   {
/*  294 */     this.iPrintZeroSetting = 2;
/*  295 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder printZeroRarelyFirst()
/*      */   {
/*  306 */     this.iPrintZeroSetting = 1;
/*  307 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder printZeroIfSupported()
/*      */   {
/*  317 */     this.iPrintZeroSetting = 3;
/*  318 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder printZeroAlways()
/*      */   {
/*  329 */     this.iPrintZeroSetting = 4;
/*  330 */     return this;
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
/*      */   public PeriodFormatterBuilder printZeroNever()
/*      */   {
/*  343 */     this.iPrintZeroSetting = 5;
/*  344 */     return this;
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
/*      */   public PeriodFormatterBuilder appendPrefix(String paramString)
/*      */   {
/*  357 */     if (paramString == null) {
/*  358 */       throw new IllegalArgumentException();
/*      */     }
/*  360 */     return appendPrefix(new SimpleAffix(paramString));
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
/*      */   public PeriodFormatterBuilder appendPrefix(String paramString1, String paramString2)
/*      */   {
/*  377 */     if ((paramString1 == null) || (paramString2 == null)) {
/*  378 */       throw new IllegalArgumentException();
/*      */     }
/*  380 */     return appendPrefix(new PluralAffix(paramString1, paramString2));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendPrefix(String[] paramArrayOfString1, String[] paramArrayOfString2)
/*      */   {
/*  417 */     if ((paramArrayOfString1 == null) || (paramArrayOfString2 == null) || (paramArrayOfString1.length < 1) || (paramArrayOfString1.length != paramArrayOfString2.length))
/*      */     {
/*  419 */       throw new IllegalArgumentException();
/*      */     }
/*  421 */     return appendPrefix(new RegExAffix(paramArrayOfString1, paramArrayOfString2));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private PeriodFormatterBuilder appendPrefix(PeriodFieldAffix paramPeriodFieldAffix)
/*      */   {
/*  433 */     if (paramPeriodFieldAffix == null) {
/*  434 */       throw new IllegalArgumentException();
/*      */     }
/*  436 */     if (this.iPrefix != null) {
/*  437 */       paramPeriodFieldAffix = new CompositeAffix(this.iPrefix, paramPeriodFieldAffix);
/*      */     }
/*  439 */     this.iPrefix = paramPeriodFieldAffix;
/*  440 */     return this;
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
/*      */   public PeriodFormatterBuilder appendYears()
/*      */   {
/*  453 */     appendField(0);
/*  454 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendMonths()
/*      */   {
/*  466 */     appendField(1);
/*  467 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendWeeks()
/*      */   {
/*  479 */     appendField(2);
/*  480 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendDays()
/*      */   {
/*  492 */     appendField(3);
/*  493 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendHours()
/*      */   {
/*  505 */     appendField(4);
/*  506 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendMinutes()
/*      */   {
/*  518 */     appendField(5);
/*  519 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendSeconds()
/*      */   {
/*  531 */     appendField(6);
/*  532 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendSecondsWithMillis()
/*      */   {
/*  543 */     appendField(8);
/*  544 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendSecondsWithOptionalMillis()
/*      */   {
/*  555 */     appendField(9);
/*  556 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendMillis()
/*      */   {
/*  568 */     appendField(7);
/*  569 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendMillis3Digit()
/*      */   {
/*  580 */     appendField(7, 3);
/*  581 */     return this;
/*      */   }
/*      */   
/*      */   private void appendField(int paramInt) {
/*  585 */     appendField(paramInt, this.iMinPrintedDigits);
/*      */   }
/*      */   
/*      */   private void appendField(int paramInt1, int paramInt2) {
/*  589 */     FieldFormatter localFieldFormatter = new FieldFormatter(paramInt2, this.iPrintZeroSetting, this.iMaxParsedDigits, this.iRejectSignedValues, paramInt1, this.iFieldFormatters, this.iPrefix, null);
/*      */     
/*  591 */     append0(localFieldFormatter, localFieldFormatter);
/*  592 */     this.iFieldFormatters[paramInt1] = localFieldFormatter;
/*  593 */     this.iPrefix = null;
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
/*      */   public PeriodFormatterBuilder appendSuffix(String paramString)
/*      */   {
/*  607 */     if (paramString == null) {
/*  608 */       throw new IllegalArgumentException();
/*      */     }
/*  610 */     return appendSuffix(new SimpleAffix(paramString));
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
/*      */   public PeriodFormatterBuilder appendSuffix(String paramString1, String paramString2)
/*      */   {
/*  628 */     if ((paramString1 == null) || (paramString2 == null)) {
/*  629 */       throw new IllegalArgumentException();
/*      */     }
/*  631 */     return appendSuffix(new PluralAffix(paramString1, paramString2));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PeriodFormatterBuilder appendSuffix(String[] paramArrayOfString1, String[] paramArrayOfString2)
/*      */   {
/*  668 */     if ((paramArrayOfString1 == null) || (paramArrayOfString2 == null) || (paramArrayOfString1.length < 1) || (paramArrayOfString1.length != paramArrayOfString2.length))
/*      */     {
/*  670 */       throw new IllegalArgumentException();
/*      */     }
/*  672 */     return appendSuffix(new RegExAffix(paramArrayOfString1, paramArrayOfString2));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private PeriodFormatterBuilder appendSuffix(PeriodFieldAffix paramPeriodFieldAffix)
/*      */   {
/*      */     Object localObject1;
/*      */     
/*      */ 
/*      */ 
/*      */     Object localObject2;
/*      */     
/*      */ 
/*  687 */     if (this.iElementPairs.size() > 0) {
/*  688 */       localObject1 = this.iElementPairs.get(this.iElementPairs.size() - 2);
/*  689 */       localObject2 = this.iElementPairs.get(this.iElementPairs.size() - 1);
/*      */     } else {
/*  691 */       localObject1 = null;
/*  692 */       localObject2 = null;
/*      */     }
/*      */     
/*  695 */     if ((localObject1 == null) || (localObject2 == null) || (localObject1 != localObject2) || (!(localObject1 instanceof FieldFormatter)))
/*      */     {
/*      */ 
/*  698 */       throw new IllegalStateException("No field to apply suffix to");
/*      */     }
/*      */     
/*  701 */     clearPrefix();
/*  702 */     FieldFormatter localFieldFormatter = new FieldFormatter((FieldFormatter)localObject1, paramPeriodFieldAffix);
/*  703 */     this.iElementPairs.set(this.iElementPairs.size() - 2, localFieldFormatter);
/*  704 */     this.iElementPairs.set(this.iElementPairs.size() - 1, localFieldFormatter);
/*  705 */     this.iFieldFormatters[localFieldFormatter.getFieldType()] = localFieldFormatter;
/*      */     
/*  707 */     return this;
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
/*      */   public PeriodFormatterBuilder appendSeparator(String paramString)
/*      */   {
/*  728 */     return appendSeparator(paramString, paramString, null, true, true);
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
/*      */   public PeriodFormatterBuilder appendSeparatorIfFieldsAfter(String paramString)
/*      */   {
/*  748 */     return appendSeparator(paramString, paramString, null, false, true);
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
/*      */   public PeriodFormatterBuilder appendSeparatorIfFieldsBefore(String paramString)
/*      */   {
/*  768 */     return appendSeparator(paramString, paramString, null, true, false);
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
/*      */   public PeriodFormatterBuilder appendSeparator(String paramString1, String paramString2)
/*      */   {
/*  793 */     return appendSeparator(paramString1, paramString2, null, true, true);
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
/*      */   public PeriodFormatterBuilder appendSeparator(String paramString1, String paramString2, String[] paramArrayOfString)
/*      */   {
/*  820 */     return appendSeparator(paramString1, paramString2, paramArrayOfString, true, true);
/*      */   }
/*      */   
/*      */ 
/*      */   private PeriodFormatterBuilder appendSeparator(String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  826 */     if ((paramString1 == null) || (paramString2 == null)) {
/*  827 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*  830 */     clearPrefix();
/*      */     
/*      */ 
/*  833 */     List localList = this.iElementPairs;
/*  834 */     if (localList.size() == 0) {
/*  835 */       if ((paramBoolean2) && (!paramBoolean1)) {
/*  836 */         Separator localSeparator1 = new Separator(paramString1, paramString2, paramArrayOfString, Literal.EMPTY, Literal.EMPTY, paramBoolean1, paramBoolean2);
/*      */         
/*      */ 
/*  839 */         append0(localSeparator1, localSeparator1);
/*      */       }
/*  841 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  846 */     Separator localSeparator2 = null;
/*  847 */     int i = localList.size(); for (;;) { i--; if (i < 0) break;
/*  848 */       if ((localList.get(i) instanceof Separator)) {
/*  849 */         localSeparator2 = (Separator)localList.get(i);
/*  850 */         localList = localList.subList(i + 1, localList.size());
/*  851 */         break;
/*      */       }
/*  853 */       i--;
/*      */     }
/*      */     
/*      */ 
/*  857 */     if ((localSeparator2 != null) && (localList.size() == 0)) {
/*  858 */       throw new IllegalStateException("Cannot have two adjacent separators");
/*      */     }
/*  860 */     Object[] arrayOfObject = createComposite(localList);
/*  861 */     localList.clear();
/*  862 */     Separator localSeparator3 = new Separator(paramString1, paramString2, paramArrayOfString, (PeriodPrinter)arrayOfObject[0], (PeriodParser)arrayOfObject[1], paramBoolean1, paramBoolean2);
/*      */     
/*      */ 
/*      */ 
/*  866 */     localList.add(localSeparator3);
/*  867 */     localList.add(localSeparator3);
/*      */     
/*      */ 
/*  870 */     return this;
/*      */   }
/*      */   
/*      */   private void clearPrefix() throws IllegalStateException
/*      */   {
/*  875 */     if (this.iPrefix != null) {
/*  876 */       throw new IllegalStateException("Prefix not followed by field");
/*      */     }
/*  878 */     this.iPrefix = null;
/*      */   }
/*      */   
/*      */   private PeriodFormatterBuilder append0(PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser) {
/*  882 */     this.iElementPairs.add(paramPeriodPrinter);
/*  883 */     this.iElementPairs.add(paramPeriodParser);
/*  884 */     this.iNotPrinter |= paramPeriodPrinter == null;
/*  885 */     this.iNotParser |= paramPeriodParser == null;
/*  886 */     return this;
/*      */   }
/*      */   
/*      */   private static PeriodFormatter toFormatter(List<Object> paramList, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  891 */     if ((paramBoolean1) && (paramBoolean2)) {
/*  892 */       throw new IllegalStateException("Builder has created neither a printer nor a parser");
/*      */     }
/*  894 */     int i = paramList.size();
/*  895 */     if ((i >= 2) && ((paramList.get(0) instanceof Separator))) {
/*  896 */       localObject = (Separator)paramList.get(0);
/*  897 */       if ((((Separator)localObject).iAfterParser == null) && (((Separator)localObject).iAfterPrinter == null)) {
/*  898 */         PeriodFormatter localPeriodFormatter = toFormatter(paramList.subList(2, i), paramBoolean1, paramBoolean2);
/*  899 */         localObject = ((Separator)localObject).finish(localPeriodFormatter.getPrinter(), localPeriodFormatter.getParser());
/*  900 */         return new PeriodFormatter((PeriodPrinter)localObject, (PeriodParser)localObject);
/*      */       }
/*      */     }
/*  903 */     Object localObject = createComposite(paramList);
/*  904 */     if (paramBoolean1)
/*  905 */       return new PeriodFormatter(null, (PeriodParser)localObject[1]);
/*  906 */     if (paramBoolean2) {
/*  907 */       return new PeriodFormatter((PeriodPrinter)localObject[0], null);
/*      */     }
/*  909 */     return new PeriodFormatter((PeriodPrinter)localObject[0], (PeriodParser)localObject[1]);
/*      */   }
/*      */   
/*      */   private static Object[] createComposite(List<Object> paramList)
/*      */   {
/*  914 */     switch (paramList.size()) {
/*      */     case 0: 
/*  916 */       return new Object[] { Literal.EMPTY, Literal.EMPTY };
/*      */     case 1: 
/*  918 */       return new Object[] { paramList.get(0), paramList.get(1) };
/*      */     }
/*  920 */     Composite localComposite = new Composite(paramList);
/*  921 */     return new Object[] { localComposite, localComposite };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static abstract interface PeriodFieldAffix
/*      */   {
/*      */     public abstract int calculatePrintedLength(int paramInt);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract void printTo(StringBuffer paramStringBuffer, int paramInt);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract void printTo(Writer paramWriter, int paramInt)
/*      */       throws IOException;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract int parse(String paramString, int paramInt);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract int scan(String paramString, int paramInt);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract String[] getAffixes();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void finish(Set<PeriodFieldAffix> paramSet);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static abstract class IgnorableAffix
/*      */     implements PeriodFieldAffix
/*      */   {
/*      */     private volatile String[] iOtherAffixes;
/*      */     
/*      */ 
/*      */ 
/*      */     public void finish(Set<PeriodFieldAffix> paramSet)
/*      */     {
/*  973 */       if (this.iOtherAffixes == null)
/*      */       {
/*  975 */         int i = Integer.MAX_VALUE;
/*  976 */         Object localObject1 = null;
/*  977 */         String[] arrayOfString; for (arrayOfString : getAffixes()) {
/*  978 */           if (arrayOfString.length() < i) {
/*  979 */             i = arrayOfString.length();
/*  980 */             localObject1 = arrayOfString;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  987 */         ??? = new HashSet();
/*  988 */         for (PeriodFieldAffix localPeriodFieldAffix : paramSet) {
/*  989 */           if (localPeriodFieldAffix != null) {
/*  990 */             for (String str : localPeriodFieldAffix.getAffixes()) {
/*  991 */               if ((str.length() > i) || ((str.equalsIgnoreCase((String)localObject1)) && (!str.equals(localObject1))))
/*      */               {
/*  993 */                 ((Set)???).add(str);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  998 */         this.iOtherAffixes = ((String[])((Set)???).toArray(new String[((Set)???).size()]));
/*      */       }
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
/*      */     protected boolean matchesOtherAffix(int paramInt1, String paramString, int paramInt2)
/*      */     {
/* 1013 */       if (this.iOtherAffixes != null)
/*      */       {
/*      */ 
/* 1016 */         for (String str : this.iOtherAffixes) {
/* 1017 */           int k = str.length();
/* 1018 */           if (((paramInt1 < k) && (paramString.regionMatches(true, paramInt2, str, 0, k))) || ((paramInt1 == k) && (paramString.regionMatches(false, paramInt2, str, 0, k))))
/*      */           {
/* 1020 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 1024 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class SimpleAffix
/*      */     extends IgnorableAffix
/*      */   {
/*      */     private final String iText;
/*      */     
/*      */     SimpleAffix(String paramString)
/*      */     {
/* 1036 */       this.iText = paramString;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(int paramInt) {
/* 1040 */       return this.iText.length();
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, int paramInt) {
/* 1044 */       paramStringBuffer.append(this.iText);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, int paramInt) throws IOException {
/* 1048 */       paramWriter.write(this.iText);
/*      */     }
/*      */     
/*      */     public int parse(String paramString, int paramInt) {
/* 1052 */       String str = this.iText;
/* 1053 */       int i = str.length();
/* 1054 */       if ((paramString.regionMatches(true, paramInt, str, 0, i)) && 
/* 1055 */         (!matchesOtherAffix(i, paramString, paramInt))) {
/* 1056 */         return paramInt + i;
/*      */       }
/*      */       
/* 1059 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public int scan(String paramString, int paramInt) {
/* 1063 */       String str = this.iText;
/* 1064 */       int i = str.length();
/* 1065 */       int j = paramString.length();
/*      */       
/* 1067 */       for (int k = paramInt; k < j; k++) {
/* 1068 */         if ((paramString.regionMatches(true, k, str, 0, i)) && 
/* 1069 */           (!matchesOtherAffix(i, paramString, k))) {
/* 1070 */           return k;
/*      */         }
/*      */         
/*      */ 
/* 1074 */         switch (paramString.charAt(k)) {
/*      */         case '+': case ',': case '-': case '.': case '0': case '1': case '2': 
/*      */         case '3': case '4': case '5': case '6': case '7': case '8': case '9': 
/*      */           break;
/*      */         case '/': default: 
/*      */           break label148;
/*      */         }
/*      */       }
/*      */       label148:
/* 1083 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public String[] getAffixes() {
/* 1087 */       return new String[] { this.iText };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class PluralAffix
/*      */     extends IgnorableAffix
/*      */   {
/*      */     private final String iSingularText;
/*      */     
/*      */     private final String iPluralText;
/*      */     
/*      */     PluralAffix(String paramString1, String paramString2)
/*      */     {
/* 1101 */       this.iSingularText = paramString1;
/* 1102 */       this.iPluralText = paramString2;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(int paramInt) {
/* 1106 */       return (paramInt == 1 ? this.iSingularText : this.iPluralText).length();
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, int paramInt) {
/* 1110 */       paramStringBuffer.append(paramInt == 1 ? this.iSingularText : this.iPluralText);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, int paramInt) throws IOException {
/* 1114 */       paramWriter.write(paramInt == 1 ? this.iSingularText : this.iPluralText);
/*      */     }
/*      */     
/*      */     public int parse(String paramString, int paramInt) {
/* 1118 */       Object localObject1 = this.iPluralText;
/* 1119 */       Object localObject2 = this.iSingularText;
/*      */       
/* 1121 */       if (((String)localObject1).length() < ((String)localObject2).length())
/*      */       {
/* 1123 */         Object localObject3 = localObject1;
/* 1124 */         localObject1 = localObject2;
/* 1125 */         localObject2 = localObject3;
/*      */       }
/*      */       
/* 1128 */       if ((paramString.regionMatches(true, paramInt, (String)localObject1, 0, ((String)localObject1).length())) && 
/* 1129 */         (!matchesOtherAffix(((String)localObject1).length(), paramString, paramInt))) {
/* 1130 */         return paramInt + ((String)localObject1).length();
/*      */       }
/*      */       
/* 1133 */       if ((paramString.regionMatches(true, paramInt, (String)localObject2, 0, ((String)localObject2).length())) && 
/* 1134 */         (!matchesOtherAffix(((String)localObject2).length(), paramString, paramInt))) {
/* 1135 */         return paramInt + ((String)localObject2).length();
/*      */       }
/*      */       
/*      */ 
/* 1139 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public int scan(String paramString, int paramInt) {
/* 1143 */       Object localObject1 = this.iPluralText;
/* 1144 */       Object localObject2 = this.iSingularText;
/*      */       
/* 1146 */       if (((String)localObject1).length() < ((String)localObject2).length())
/*      */       {
/* 1148 */         Object localObject3 = localObject1;
/* 1149 */         localObject1 = localObject2;
/* 1150 */         localObject2 = localObject3;
/*      */       }
/*      */       
/* 1153 */       int i = ((String)localObject1).length();
/* 1154 */       int j = ((String)localObject2).length();
/*      */       
/* 1156 */       int k = paramString.length();
/* 1157 */       for (int m = paramInt; m < k; m++) {
/* 1158 */         if ((paramString.regionMatches(true, m, (String)localObject1, 0, i)) && 
/* 1159 */           (!matchesOtherAffix(((String)localObject1).length(), paramString, m))) {
/* 1160 */           return m;
/*      */         }
/*      */         
/* 1163 */         if ((paramString.regionMatches(true, m, (String)localObject2, 0, j)) && 
/* 1164 */           (!matchesOtherAffix(((String)localObject2).length(), paramString, m))) {
/* 1165 */           return m;
/*      */         }
/*      */       }
/*      */       
/* 1169 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public String[] getAffixes() {
/* 1173 */       return new String[] { this.iSingularText, this.iPluralText };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static class RegExAffix
/*      */     extends IgnorableAffix
/*      */   {
/* 1183 */     private static final Comparator<String> LENGTH_DESC_COMPARATOR = new Comparator() {
/*      */       public int compare(String paramAnonymousString1, String paramAnonymousString2) {
/* 1185 */         return paramAnonymousString2.length() - paramAnonymousString1.length();
/*      */       }
/*      */     };
/*      */     
/*      */     private final String[] iSuffixes;
/*      */     
/*      */     private final Pattern[] iPatterns;
/*      */     
/*      */     private final String[] iSuffixesSortedDescByLength;
/*      */     
/*      */     RegExAffix(String[] paramArrayOfString1, String[] paramArrayOfString2)
/*      */     {
/* 1197 */       this.iSuffixes = ((String[])paramArrayOfString2.clone());
/* 1198 */       this.iPatterns = new Pattern[paramArrayOfString1.length];
/* 1199 */       for (int i = 0; i < paramArrayOfString1.length; i++) {
/* 1200 */         Pattern localPattern = (Pattern)PeriodFormatterBuilder.PATTERNS.get(paramArrayOfString1[i]);
/* 1201 */         if (localPattern == null) {
/* 1202 */           localPattern = Pattern.compile(paramArrayOfString1[i]);
/* 1203 */           PeriodFormatterBuilder.PATTERNS.putIfAbsent(paramArrayOfString1[i], localPattern);
/*      */         }
/* 1205 */         this.iPatterns[i] = localPattern;
/*      */       }
/* 1207 */       this.iSuffixesSortedDescByLength = ((String[])this.iSuffixes.clone());
/* 1208 */       Arrays.sort(this.iSuffixesSortedDescByLength, LENGTH_DESC_COMPARATOR);
/*      */     }
/*      */     
/*      */     private int selectSuffixIndex(int paramInt) {
/* 1212 */       String str = String.valueOf(paramInt);
/* 1213 */       for (int i = 0; i < this.iPatterns.length; i++) {
/* 1214 */         if (this.iPatterns[i].matcher(str).matches()) {
/* 1215 */           return i;
/*      */         }
/*      */       }
/* 1218 */       return this.iPatterns.length - 1;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(int paramInt) {
/* 1222 */       return this.iSuffixes[selectSuffixIndex(paramInt)].length();
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, int paramInt) {
/* 1226 */       paramStringBuffer.append(this.iSuffixes[selectSuffixIndex(paramInt)]);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, int paramInt) throws IOException {
/* 1230 */       paramWriter.write(this.iSuffixes[selectSuffixIndex(paramInt)]);
/*      */     }
/*      */     
/*      */     public int parse(String paramString, int paramInt) {
/* 1234 */       for (String str : this.iSuffixesSortedDescByLength) {
/* 1235 */         if ((paramString.regionMatches(true, paramInt, str, 0, str.length())) && 
/* 1236 */           (!matchesOtherAffix(str.length(), paramString, paramInt))) {
/* 1237 */           return paramInt + str.length();
/*      */         }
/*      */       }
/*      */       
/* 1241 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public int scan(String paramString, int paramInt) {
/* 1245 */       int i = paramString.length();
/* 1246 */       for (int j = paramInt; j < i; j++) {
/* 1247 */         for (String str : this.iSuffixesSortedDescByLength) {
/* 1248 */           if ((paramString.regionMatches(true, j, str, 0, str.length())) && 
/* 1249 */             (!matchesOtherAffix(str.length(), paramString, j))) {
/* 1250 */             return j;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1255 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public String[] getAffixes() {
/* 1259 */       return (String[])this.iSuffixes.clone();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class CompositeAffix
/*      */     extends IgnorableAffix
/*      */   {
/*      */     private final PeriodFieldAffix iLeft;
/*      */     private final PeriodFieldAffix iRight;
/*      */     private final String[] iLeftRightCombinations;
/*      */     
/*      */     CompositeAffix(PeriodFieldAffix paramPeriodFieldAffix1, PeriodFieldAffix paramPeriodFieldAffix2)
/*      */     {
/* 1273 */       this.iLeft = paramPeriodFieldAffix1;
/* 1274 */       this.iRight = paramPeriodFieldAffix2;
/*      */       
/*      */ 
/*      */ 
/* 1278 */       HashSet localHashSet = new HashSet();
/* 1279 */       for (String str1 : this.iLeft.getAffixes()) {
/* 1280 */         for (String str2 : this.iRight.getAffixes()) {
/* 1281 */           localHashSet.add(str1 + str2);
/*      */         }
/*      */       }
/* 1284 */       this.iLeftRightCombinations = ((String[])localHashSet.toArray(new String[localHashSet.size()]));
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(int paramInt) {
/* 1288 */       return this.iLeft.calculatePrintedLength(paramInt) + this.iRight.calculatePrintedLength(paramInt);
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, int paramInt)
/*      */     {
/* 1293 */       this.iLeft.printTo(paramStringBuffer, paramInt);
/* 1294 */       this.iRight.printTo(paramStringBuffer, paramInt);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, int paramInt) throws IOException {
/* 1298 */       this.iLeft.printTo(paramWriter, paramInt);
/* 1299 */       this.iRight.printTo(paramWriter, paramInt);
/*      */     }
/*      */     
/*      */     public int parse(String paramString, int paramInt) {
/* 1303 */       int i = this.iLeft.parse(paramString, paramInt);
/* 1304 */       if (i >= 0) {
/* 1305 */         i = this.iRight.parse(paramString, i);
/* 1306 */         if ((i >= 0) && (matchesOtherAffix(parse(paramString, i) - i, paramString, paramInt))) {
/* 1307 */           return paramInt ^ 0xFFFFFFFF;
/*      */         }
/*      */       }
/* 1310 */       return i;
/*      */     }
/*      */     
/*      */     public int scan(String paramString, int paramInt) {
/* 1314 */       int i = this.iLeft.scan(paramString, paramInt);
/* 1315 */       if (i >= 0) {
/* 1316 */         int j = this.iRight.scan(paramString, this.iLeft.parse(paramString, i));
/* 1317 */         if ((j < 0) || (!matchesOtherAffix(this.iRight.parse(paramString, j) - i, paramString, paramInt))) {
/* 1318 */           if (i > 0) {
/* 1319 */             return i;
/*      */           }
/* 1321 */           return j;
/*      */         }
/*      */       }
/*      */       
/* 1325 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */     
/*      */     public String[] getAffixes() {
/* 1329 */       return (String[])this.iLeftRightCombinations.clone();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static class FieldFormatter
/*      */     implements PeriodPrinter, PeriodParser
/*      */   {
/*      */     private final int iMinPrintedDigits;
/*      */     
/*      */ 
/*      */     private final int iPrintZeroSetting;
/*      */     
/*      */ 
/*      */     private final int iMaxParsedDigits;
/*      */     
/*      */     private final boolean iRejectSignedValues;
/*      */     
/*      */     private final int iFieldType;
/*      */     
/*      */     private final FieldFormatter[] iFieldFormatters;
/*      */     
/*      */     private final PeriodFieldAffix iPrefix;
/*      */     
/*      */     private final PeriodFieldAffix iSuffix;
/*      */     
/*      */ 
/*      */     FieldFormatter(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, FieldFormatter[] paramArrayOfFieldFormatter, PeriodFieldAffix paramPeriodFieldAffix1, PeriodFieldAffix paramPeriodFieldAffix2)
/*      */     {
/* 1359 */       this.iMinPrintedDigits = paramInt1;
/* 1360 */       this.iPrintZeroSetting = paramInt2;
/* 1361 */       this.iMaxParsedDigits = paramInt3;
/* 1362 */       this.iRejectSignedValues = paramBoolean;
/* 1363 */       this.iFieldType = paramInt4;
/* 1364 */       this.iFieldFormatters = paramArrayOfFieldFormatter;
/* 1365 */       this.iPrefix = paramPeriodFieldAffix1;
/* 1366 */       this.iSuffix = paramPeriodFieldAffix2;
/*      */     }
/*      */     
/*      */     FieldFormatter(FieldFormatter paramFieldFormatter, PeriodFieldAffix paramPeriodFieldAffix) {
/* 1370 */       this.iMinPrintedDigits = paramFieldFormatter.iMinPrintedDigits;
/* 1371 */       this.iPrintZeroSetting = paramFieldFormatter.iPrintZeroSetting;
/* 1372 */       this.iMaxParsedDigits = paramFieldFormatter.iMaxParsedDigits;
/* 1373 */       this.iRejectSignedValues = paramFieldFormatter.iRejectSignedValues;
/* 1374 */       this.iFieldType = paramFieldFormatter.iFieldType;
/* 1375 */       this.iFieldFormatters = paramFieldFormatter.iFieldFormatters;
/* 1376 */       this.iPrefix = paramFieldFormatter.iPrefix;
/* 1377 */       if (paramFieldFormatter.iSuffix != null) {
/* 1378 */         paramPeriodFieldAffix = new CompositeAffix(paramFieldFormatter.iSuffix, paramPeriodFieldAffix);
/*      */       }
/* 1380 */       this.iSuffix = paramPeriodFieldAffix;
/*      */     }
/*      */     
/*      */     public void finish(FieldFormatter[] paramArrayOfFieldFormatter)
/*      */     {
/* 1385 */       HashSet localHashSet1 = new HashSet();
/* 1386 */       HashSet localHashSet2 = new HashSet();
/* 1387 */       for (FieldFormatter localFieldFormatter : paramArrayOfFieldFormatter) {
/* 1388 */         if ((localFieldFormatter != null) && (!equals(localFieldFormatter))) {
/* 1389 */           localHashSet1.add(localFieldFormatter.iPrefix);
/* 1390 */           localHashSet2.add(localFieldFormatter.iSuffix);
/*      */         }
/*      */       }
/*      */       
/* 1394 */       if (this.iPrefix != null) {
/* 1395 */         this.iPrefix.finish(localHashSet1);
/*      */       }
/*      */       
/* 1398 */       if (this.iSuffix != null) {
/* 1399 */         this.iSuffix.finish(localHashSet2);
/*      */       }
/*      */     }
/*      */     
/*      */     public int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale) {
/* 1404 */       if (paramInt <= 0) {
/* 1405 */         return 0;
/*      */       }
/* 1407 */       if ((this.iPrintZeroSetting == 4) || (getFieldValue(paramReadablePeriod) != Long.MAX_VALUE)) {
/* 1408 */         return 1;
/*      */       }
/* 1410 */       return 0;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1414 */       long l = getFieldValue(paramReadablePeriod);
/* 1415 */       if (l == Long.MAX_VALUE) {
/* 1416 */         return 0;
/*      */       }
/*      */       
/* 1419 */       int i = Math.max(FormatUtils.calculateDigitCount(l), this.iMinPrintedDigits);
/* 1420 */       if (this.iFieldType >= 8)
/*      */       {
/*      */ 
/* 1423 */         i = l < 0L ? Math.max(i, 5) : Math.max(i, 4);
/*      */         
/* 1425 */         i++;
/* 1426 */         if ((this.iFieldType == 9) && (Math.abs(l) % 1000L == 0L))
/*      */         {
/* 1428 */           i -= 4;
/*      */         }
/*      */         
/* 1431 */         l /= 1000L;
/*      */       }
/* 1433 */       int j = (int)l;
/*      */       
/* 1435 */       if (this.iPrefix != null) {
/* 1436 */         i += this.iPrefix.calculatePrintedLength(j);
/*      */       }
/* 1438 */       if (this.iSuffix != null) {
/* 1439 */         i += this.iSuffix.calculatePrintedLength(j);
/*      */       }
/*      */       
/* 1442 */       return i;
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1446 */       long l = getFieldValue(paramReadablePeriod);
/* 1447 */       if (l == Long.MAX_VALUE) {
/* 1448 */         return;
/*      */       }
/* 1450 */       int i = (int)l;
/* 1451 */       if (this.iFieldType >= 8) {
/* 1452 */         i = (int)(l / 1000L);
/*      */       }
/*      */       
/* 1455 */       if (this.iPrefix != null) {
/* 1456 */         this.iPrefix.printTo(paramStringBuffer, i);
/*      */       }
/* 1458 */       int j = paramStringBuffer.length();
/* 1459 */       int k = this.iMinPrintedDigits;
/* 1460 */       if (k <= 1) {
/* 1461 */         FormatUtils.appendUnpaddedInteger(paramStringBuffer, i);
/*      */       } else {
/* 1463 */         FormatUtils.appendPaddedInteger(paramStringBuffer, i, k);
/*      */       }
/* 1465 */       if (this.iFieldType >= 8) {
/* 1466 */         int m = (int)(Math.abs(l) % 1000L);
/* 1467 */         if ((this.iFieldType == 8) || (m > 0)) {
/* 1468 */           if ((l < 0L) && (l > -1000L)) {
/* 1469 */             paramStringBuffer.insert(j, '-');
/*      */           }
/* 1471 */           paramStringBuffer.append('.');
/* 1472 */           FormatUtils.appendPaddedInteger(paramStringBuffer, m, 3);
/*      */         }
/*      */       }
/* 1475 */       if (this.iSuffix != null) {
/* 1476 */         this.iSuffix.printTo(paramStringBuffer, i);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale) throws IOException {
/* 1481 */       long l = getFieldValue(paramReadablePeriod);
/* 1482 */       if (l == Long.MAX_VALUE) {
/* 1483 */         return;
/*      */       }
/* 1485 */       int i = (int)l;
/* 1486 */       if (this.iFieldType >= 8) {
/* 1487 */         i = (int)(l / 1000L);
/*      */       }
/*      */       
/* 1490 */       if (this.iPrefix != null) {
/* 1491 */         this.iPrefix.printTo(paramWriter, i);
/*      */       }
/* 1493 */       int j = this.iMinPrintedDigits;
/* 1494 */       if (j <= 1) {
/* 1495 */         FormatUtils.writeUnpaddedInteger(paramWriter, i);
/*      */       } else {
/* 1497 */         FormatUtils.writePaddedInteger(paramWriter, i, j);
/*      */       }
/* 1499 */       if (this.iFieldType >= 8) {
/* 1500 */         int k = (int)(Math.abs(l) % 1000L);
/* 1501 */         if ((this.iFieldType == 8) || (k > 0)) {
/* 1502 */           paramWriter.write(46);
/* 1503 */           FormatUtils.writePaddedInteger(paramWriter, k, 3);
/*      */         }
/*      */       }
/* 1506 */       if (this.iSuffix != null) {
/* 1507 */         this.iSuffix.printTo(paramWriter, i);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale)
/*      */     {
/* 1515 */       int i = this.iPrintZeroSetting == 4 ? 1 : 0;
/*      */       
/*      */ 
/* 1518 */       if (paramInt >= paramString.length()) {
/* 1519 */         return i != 0 ? paramInt ^ 0xFFFFFFFF : paramInt;
/*      */       }
/*      */       
/* 1522 */       if (this.iPrefix != null) {
/* 1523 */         paramInt = this.iPrefix.parse(paramString, paramInt);
/* 1524 */         if (paramInt >= 0)
/*      */         {
/* 1526 */           i = 1;
/*      */         }
/*      */         else {
/* 1529 */           if (i == 0)
/*      */           {
/*      */ 
/*      */ 
/* 1533 */             return paramInt ^ 0xFFFFFFFF;
/*      */           }
/* 1535 */           return paramInt;
/*      */         }
/*      */       }
/*      */       
/* 1539 */       int j = -1;
/* 1540 */       if ((this.iSuffix != null) && (i == 0))
/*      */       {
/*      */ 
/* 1543 */         j = this.iSuffix.scan(paramString, paramInt);
/* 1544 */         if (j >= 0)
/*      */         {
/* 1546 */           i = 1;
/*      */         }
/*      */         else {
/* 1549 */           if (i == 0)
/*      */           {
/*      */ 
/*      */ 
/* 1553 */             return j ^ 0xFFFFFFFF;
/*      */           }
/* 1555 */           return j;
/*      */         }
/*      */       }
/*      */       
/* 1559 */       if ((i == 0) && (!isSupported(paramReadWritablePeriod.getPeriodType(), this.iFieldType)))
/*      */       {
/*      */ 
/* 1562 */         return paramInt;
/*      */       }
/*      */       
/*      */       int k;
/* 1566 */       if (j > 0) {
/* 1567 */         k = Math.min(this.iMaxParsedDigits, j - paramInt);
/*      */       } else {
/* 1569 */         k = Math.min(this.iMaxParsedDigits, paramString.length() - paramInt);
/*      */       }
/*      */       
/*      */ 
/* 1573 */       int m = 0;
/* 1574 */       int n = -1;
/* 1575 */       int i1 = 0;
/* 1576 */       int i2 = 0;
/* 1577 */       int i3; while (m < k) {
/* 1578 */         i3 = paramString.charAt(paramInt + m);
/*      */         
/* 1580 */         if ((m == 0) && ((i3 == 45) || (i3 == 43)) && (!this.iRejectSignedValues)) {
/* 1581 */           i2 = i3 == 45 ? 1 : 0;
/*      */           
/*      */ 
/* 1584 */           if ((m + 1 >= k) || ((i3 = paramString.charAt(paramInt + m + 1)) < '0') || (i3 > 57)) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1590 */           if (i2 != 0) {
/* 1591 */             m++;
/*      */           }
/*      */           else {
/* 1594 */             paramInt++;
/*      */           }
/*      */           
/* 1597 */           k = Math.min(k + 1, paramString.length() - paramInt);
/*      */         }
/*      */         else
/*      */         {
/* 1601 */           if ((i3 >= 48) && (i3 <= 57)) {
/* 1602 */             i1 = 1;
/*      */           } else {
/* 1604 */             if (((i3 != 46) && (i3 != 44)) || ((this.iFieldType != 8) && (this.iFieldType != 9)))
/*      */               break;
/* 1606 */             if (n >= 0) {
/*      */               break;
/*      */             }
/*      */             
/* 1610 */             n = paramInt + m + 1;
/*      */             
/* 1612 */             k = Math.min(k + 1, paramString.length() - paramInt);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1617 */           m++;
/*      */         }
/*      */       }
/* 1620 */       if (i1 == 0) {
/* 1621 */         return paramInt ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 1624 */       if ((j >= 0) && (paramInt + m != j))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1629 */         return paramInt;
/*      */       }
/*      */       
/* 1632 */       if ((this.iFieldType != 8) && (this.iFieldType != 9))
/*      */       {
/* 1634 */         setFieldValue(paramReadWritablePeriod, this.iFieldType, parseInt(paramString, paramInt, m));
/* 1635 */       } else if (n < 0) {
/* 1636 */         setFieldValue(paramReadWritablePeriod, 6, parseInt(paramString, paramInt, m));
/* 1637 */         setFieldValue(paramReadWritablePeriod, 7, 0);
/*      */       } else {
/* 1639 */         i3 = parseInt(paramString, paramInt, n - paramInt - 1);
/* 1640 */         setFieldValue(paramReadWritablePeriod, 6, i3);
/*      */         
/* 1642 */         int i4 = paramInt + m - n;
/*      */         int i5;
/* 1644 */         if (i4 <= 0) {
/* 1645 */           i5 = 0;
/*      */         } else {
/* 1647 */           if (i4 >= 3) {
/* 1648 */             i5 = parseInt(paramString, n, 3);
/*      */           } else {
/* 1650 */             i5 = parseInt(paramString, n, i4);
/* 1651 */             if (i4 == 1) {
/* 1652 */               i5 *= 100;
/*      */             } else {
/* 1654 */               i5 *= 10;
/*      */             }
/*      */           }
/* 1657 */           if ((i2 != 0) || (i3 < 0)) {
/* 1658 */             i5 = -i5;
/*      */           }
/*      */         }
/*      */         
/* 1662 */         setFieldValue(paramReadWritablePeriod, 7, i5);
/*      */       }
/*      */       
/* 1665 */       paramInt += m;
/*      */       
/* 1667 */       if ((paramInt >= 0) && (this.iSuffix != null)) {
/* 1668 */         paramInt = this.iSuffix.parse(paramString, paramInt);
/*      */       }
/*      */       
/* 1671 */       return paramInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private int parseInt(String paramString, int paramInt1, int paramInt2)
/*      */     {
/* 1681 */       if (paramInt2 >= 10)
/*      */       {
/* 1683 */         return Integer.parseInt(paramString.substring(paramInt1, paramInt1 + paramInt2));
/*      */       }
/* 1685 */       if (paramInt2 <= 0) {
/* 1686 */         return 0;
/*      */       }
/* 1688 */       int i = paramString.charAt(paramInt1++);
/* 1689 */       paramInt2--;
/*      */       int j;
/* 1691 */       if (i == 45) {
/* 1692 */         paramInt2--; if (paramInt2 < 0) {
/* 1693 */           return 0;
/*      */         }
/* 1695 */         j = 1;
/* 1696 */         i = paramString.charAt(paramInt1++);
/*      */       } else {
/* 1698 */         j = 0;
/*      */       }
/* 1700 */       i -= 48;
/* 1701 */       while (paramInt2-- > 0) {
/* 1702 */         i = (i << 3) + (i << 1) + paramString.charAt(paramInt1++) - 48;
/*      */       }
/* 1704 */       return j != 0 ? -i : i;
/*      */     }
/*      */     
/*      */ 
/*      */     long getFieldValue(ReadablePeriod paramReadablePeriod)
/*      */     {
/*      */       PeriodType localPeriodType;
/*      */       
/* 1712 */       if (this.iPrintZeroSetting == 4) {
/* 1713 */         localPeriodType = null;
/*      */       } else {
/* 1715 */         localPeriodType = paramReadablePeriod.getPeriodType();
/*      */       }
/* 1717 */       if ((localPeriodType != null) && (!isSupported(localPeriodType, this.iFieldType))) {
/* 1718 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*      */       long l;
/*      */       int i;
/* 1723 */       switch (this.iFieldType) {
/*      */       default: 
/* 1725 */         return Long.MAX_VALUE;
/*      */       case 0: 
/* 1727 */         l = paramReadablePeriod.get(DurationFieldType.years());
/* 1728 */         break;
/*      */       case 1: 
/* 1730 */         l = paramReadablePeriod.get(DurationFieldType.months());
/* 1731 */         break;
/*      */       case 2: 
/* 1733 */         l = paramReadablePeriod.get(DurationFieldType.weeks());
/* 1734 */         break;
/*      */       case 3: 
/* 1736 */         l = paramReadablePeriod.get(DurationFieldType.days());
/* 1737 */         break;
/*      */       case 4: 
/* 1739 */         l = paramReadablePeriod.get(DurationFieldType.hours());
/* 1740 */         break;
/*      */       case 5: 
/* 1742 */         l = paramReadablePeriod.get(DurationFieldType.minutes());
/* 1743 */         break;
/*      */       case 6: 
/* 1745 */         l = paramReadablePeriod.get(DurationFieldType.seconds());
/* 1746 */         break;
/*      */       case 7: 
/* 1748 */         l = paramReadablePeriod.get(DurationFieldType.millis());
/* 1749 */         break;
/*      */       case 8: 
/*      */       case 9: 
/* 1752 */         i = paramReadablePeriod.get(DurationFieldType.seconds());
/* 1753 */         int j = paramReadablePeriod.get(DurationFieldType.millis());
/* 1754 */         l = i * 1000L + j;
/*      */       }
/*      */       
/*      */       
/*      */ 
/* 1759 */       if (l == 0L) {
/* 1760 */         switch (this.iPrintZeroSetting) {
/*      */         case 5: 
/* 1762 */           return Long.MAX_VALUE;
/*      */         case 2: 
/* 1764 */           if ((isZero(paramReadablePeriod)) && (this.iFieldFormatters[this.iFieldType] == this)) {
/* 1765 */             for (i = this.iFieldType + 1; i <= 9; i++) {
/* 1766 */               if ((isSupported(localPeriodType, i)) && (this.iFieldFormatters[i] != null)) {
/* 1767 */                 return Long.MAX_VALUE;
/*      */               }
/*      */             }
/*      */           } else {
/* 1771 */             return Long.MAX_VALUE;
/*      */           }
/*      */           break;
/*      */         case 1: 
/* 1775 */           if ((isZero(paramReadablePeriod)) && (this.iFieldFormatters[this.iFieldType] == this)) {
/* 1776 */             i = Math.min(this.iFieldType, 8);
/* 1777 */             i--;
/* 1778 */             for (; (i >= 0) && (i <= 9); i--) {
/* 1779 */               if ((isSupported(localPeriodType, i)) && (this.iFieldFormatters[i] != null)) {
/* 1780 */                 return Long.MAX_VALUE;
/*      */               }
/*      */             }
/*      */           } else {
/* 1784 */             return Long.MAX_VALUE;
/*      */           }
/*      */           break;
/*      */         }
/*      */         
/*      */       }
/* 1790 */       return l;
/*      */     }
/*      */     
/*      */     boolean isZero(ReadablePeriod paramReadablePeriod) {
/* 1794 */       int i = 0; for (int j = paramReadablePeriod.size(); i < j; i++) {
/* 1795 */         if (paramReadablePeriod.getValue(i) != 0) {
/* 1796 */           return false;
/*      */         }
/*      */       }
/* 1799 */       return true;
/*      */     }
/*      */     
/*      */     boolean isSupported(PeriodType paramPeriodType, int paramInt) {
/* 1803 */       switch (paramInt) {
/*      */       default: 
/* 1805 */         return false;
/*      */       case 0: 
/* 1807 */         return paramPeriodType.isSupported(DurationFieldType.years());
/*      */       case 1: 
/* 1809 */         return paramPeriodType.isSupported(DurationFieldType.months());
/*      */       case 2: 
/* 1811 */         return paramPeriodType.isSupported(DurationFieldType.weeks());
/*      */       case 3: 
/* 1813 */         return paramPeriodType.isSupported(DurationFieldType.days());
/*      */       case 4: 
/* 1815 */         return paramPeriodType.isSupported(DurationFieldType.hours());
/*      */       case 5: 
/* 1817 */         return paramPeriodType.isSupported(DurationFieldType.minutes());
/*      */       case 6: 
/* 1819 */         return paramPeriodType.isSupported(DurationFieldType.seconds());
/*      */       case 7: 
/* 1821 */         return paramPeriodType.isSupported(DurationFieldType.millis());
/*      */       }
/*      */       
/* 1824 */       return (paramPeriodType.isSupported(DurationFieldType.seconds())) || (paramPeriodType.isSupported(DurationFieldType.millis()));
/*      */     }
/*      */     
/*      */ 
/*      */     void setFieldValue(ReadWritablePeriod paramReadWritablePeriod, int paramInt1, int paramInt2)
/*      */     {
/* 1830 */       switch (paramInt1) {
/*      */       default: 
/*      */         break;
/*      */       case 0: 
/* 1834 */         paramReadWritablePeriod.setYears(paramInt2);
/* 1835 */         break;
/*      */       case 1: 
/* 1837 */         paramReadWritablePeriod.setMonths(paramInt2);
/* 1838 */         break;
/*      */       case 2: 
/* 1840 */         paramReadWritablePeriod.setWeeks(paramInt2);
/* 1841 */         break;
/*      */       case 3: 
/* 1843 */         paramReadWritablePeriod.setDays(paramInt2);
/* 1844 */         break;
/*      */       case 4: 
/* 1846 */         paramReadWritablePeriod.setHours(paramInt2);
/* 1847 */         break;
/*      */       case 5: 
/* 1849 */         paramReadWritablePeriod.setMinutes(paramInt2);
/* 1850 */         break;
/*      */       case 6: 
/* 1852 */         paramReadWritablePeriod.setSeconds(paramInt2);
/* 1853 */         break;
/*      */       case 7: 
/* 1855 */         paramReadWritablePeriod.setMillis(paramInt2);
/*      */       }
/*      */     }
/*      */     
/*      */     int getFieldType()
/*      */     {
/* 1861 */       return this.iFieldType;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static class Literal
/*      */     implements PeriodPrinter, PeriodParser
/*      */   {
/* 1871 */     static final Literal EMPTY = new Literal("");
/*      */     private final String iText;
/*      */     
/*      */     Literal(String paramString) {
/* 1875 */       this.iText = paramString;
/*      */     }
/*      */     
/*      */     public int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale) {
/* 1879 */       return 0;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1883 */       return this.iText.length();
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1887 */       paramStringBuffer.append(this.iText);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale) throws IOException {
/* 1891 */       paramWriter.write(this.iText);
/*      */     }
/*      */     
/*      */ 
/*      */     public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale)
/*      */     {
/* 1897 */       if (paramString.regionMatches(true, paramInt, this.iText, 0, this.iText.length())) {
/* 1898 */         return paramInt + this.iText.length();
/*      */       }
/* 1900 */       return paramInt ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class Separator
/*      */     implements PeriodPrinter, PeriodParser
/*      */   {
/*      */     private final String iText;
/*      */     
/*      */     private final String iFinalText;
/*      */     
/*      */     private final String[] iParsedForms;
/*      */     
/*      */     private final boolean iUseBefore;
/*      */     
/*      */     private final boolean iUseAfter;
/*      */     
/*      */     private final PeriodPrinter iBeforePrinter;
/*      */     
/*      */     private volatile PeriodPrinter iAfterPrinter;
/*      */     private final PeriodParser iBeforeParser;
/*      */     private volatile PeriodParser iAfterParser;
/*      */     
/*      */     Separator(String paramString1, String paramString2, String[] paramArrayOfString, PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser, boolean paramBoolean1, boolean paramBoolean2)
/*      */     {
/* 1926 */       this.iText = paramString1;
/* 1927 */       this.iFinalText = paramString2;
/*      */       
/* 1929 */       if (((paramString2 == null) || (paramString1.equals(paramString2))) && ((paramArrayOfString == null) || (paramArrayOfString.length == 0)))
/*      */       {
/*      */ 
/* 1932 */         this.iParsedForms = new String[] { paramString1 };
/*      */       }
/*      */       else {
/* 1935 */         TreeSet localTreeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
/* 1936 */         localTreeSet.add(paramString1);
/* 1937 */         localTreeSet.add(paramString2);
/* 1938 */         if (paramArrayOfString != null) {
/* 1939 */           int i = paramArrayOfString.length; for (;;) { i--; if (i < 0) break;
/* 1940 */             localTreeSet.add(paramArrayOfString[i]);
/*      */           }
/*      */         }
/* 1943 */         ArrayList localArrayList = new ArrayList(localTreeSet);
/* 1944 */         Collections.reverse(localArrayList);
/* 1945 */         this.iParsedForms = ((String[])localArrayList.toArray(new String[localArrayList.size()]));
/*      */       }
/*      */       
/* 1948 */       this.iBeforePrinter = paramPeriodPrinter;
/* 1949 */       this.iBeforeParser = paramPeriodParser;
/* 1950 */       this.iUseBefore = paramBoolean1;
/* 1951 */       this.iUseAfter = paramBoolean2;
/*      */     }
/*      */     
/*      */     public int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale) {
/* 1955 */       int i = this.iBeforePrinter.countFieldsToPrint(paramReadablePeriod, paramInt, paramLocale);
/* 1956 */       if (i < paramInt) {
/* 1957 */         i += this.iAfterPrinter.countFieldsToPrint(paramReadablePeriod, paramInt, paramLocale);
/*      */       }
/* 1959 */       return i;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1963 */       PeriodPrinter localPeriodPrinter1 = this.iBeforePrinter;
/* 1964 */       PeriodPrinter localPeriodPrinter2 = this.iAfterPrinter;
/*      */       
/* 1966 */       int i = localPeriodPrinter1.calculatePrintedLength(paramReadablePeriod, paramLocale) + localPeriodPrinter2.calculatePrintedLength(paramReadablePeriod, paramLocale);
/*      */       
/*      */ 
/* 1969 */       if (this.iUseBefore) {
/* 1970 */         if (localPeriodPrinter1.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0) {
/* 1971 */           if (this.iUseAfter) {
/* 1972 */             int j = localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 2, paramLocale);
/* 1973 */             if (j > 0) {
/* 1974 */               i += (j > 1 ? this.iText : this.iFinalText).length();
/*      */             }
/*      */           } else {
/* 1977 */             i += this.iText.length();
/*      */           }
/*      */         }
/* 1980 */       } else if ((this.iUseAfter) && (localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0)) {
/* 1981 */         i += this.iText.length();
/*      */       }
/*      */       
/* 1984 */       return i;
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 1988 */       PeriodPrinter localPeriodPrinter1 = this.iBeforePrinter;
/* 1989 */       PeriodPrinter localPeriodPrinter2 = this.iAfterPrinter;
/*      */       
/* 1991 */       localPeriodPrinter1.printTo(paramStringBuffer, paramReadablePeriod, paramLocale);
/* 1992 */       if (this.iUseBefore) {
/* 1993 */         if (localPeriodPrinter1.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0) {
/* 1994 */           if (this.iUseAfter) {
/* 1995 */             int i = localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 2, paramLocale);
/* 1996 */             if (i > 0) {
/* 1997 */               paramStringBuffer.append(i > 1 ? this.iText : this.iFinalText);
/*      */             }
/*      */           } else {
/* 2000 */             paramStringBuffer.append(this.iText);
/*      */           }
/*      */         }
/* 2003 */       } else if ((this.iUseAfter) && (localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0)) {
/* 2004 */         paramStringBuffer.append(this.iText);
/*      */       }
/* 2006 */       localPeriodPrinter2.printTo(paramStringBuffer, paramReadablePeriod, paramLocale);
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale) throws IOException {
/* 2010 */       PeriodPrinter localPeriodPrinter1 = this.iBeforePrinter;
/* 2011 */       PeriodPrinter localPeriodPrinter2 = this.iAfterPrinter;
/*      */       
/* 2013 */       localPeriodPrinter1.printTo(paramWriter, paramReadablePeriod, paramLocale);
/* 2014 */       if (this.iUseBefore) {
/* 2015 */         if (localPeriodPrinter1.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0) {
/* 2016 */           if (this.iUseAfter) {
/* 2017 */             int i = localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 2, paramLocale);
/* 2018 */             if (i > 0) {
/* 2019 */               paramWriter.write(i > 1 ? this.iText : this.iFinalText);
/*      */             }
/*      */           } else {
/* 2022 */             paramWriter.write(this.iText);
/*      */           }
/*      */         }
/* 2025 */       } else if ((this.iUseAfter) && (localPeriodPrinter2.countFieldsToPrint(paramReadablePeriod, 1, paramLocale) > 0)) {
/* 2026 */         paramWriter.write(this.iText);
/*      */       }
/* 2028 */       localPeriodPrinter2.printTo(paramWriter, paramReadablePeriod, paramLocale);
/*      */     }
/*      */     
/*      */ 
/*      */     public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale)
/*      */     {
/* 2034 */       int i = paramInt;
/* 2035 */       paramInt = this.iBeforeParser.parseInto(paramReadWritablePeriod, paramString, paramInt, paramLocale);
/*      */       
/* 2037 */       if (paramInt < 0) {
/* 2038 */         return paramInt;
/*      */       }
/*      */       
/* 2041 */       int j = 0;
/* 2042 */       int k = -1;
/* 2043 */       if (paramInt > i)
/*      */       {
/* 2045 */         String[] arrayOfString = this.iParsedForms;
/* 2046 */         int m = arrayOfString.length;
/* 2047 */         for (int n = 0; n < m; n++) {
/* 2048 */           String str = arrayOfString[n];
/* 2049 */           if ((str == null) || (str.length() == 0) || (paramString.regionMatches(true, paramInt, str, 0, str.length())))
/*      */           {
/*      */ 
/* 2052 */             k = str == null ? 0 : str.length();
/* 2053 */             paramInt += k;
/* 2054 */             j = 1;
/* 2055 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2060 */       i = paramInt;
/* 2061 */       paramInt = this.iAfterParser.parseInto(paramReadWritablePeriod, paramString, paramInt, paramLocale);
/*      */       
/* 2063 */       if (paramInt < 0) {
/* 2064 */         return paramInt;
/*      */       }
/*      */       
/* 2067 */       if ((j != 0) && (paramInt == i) && (k > 0))
/*      */       {
/* 2069 */         return i ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 2072 */       if ((paramInt > i) && (j == 0) && (!this.iUseBefore))
/*      */       {
/* 2074 */         return i ^ 0xFFFFFFFF;
/*      */       }
/*      */       
/* 2077 */       return paramInt;
/*      */     }
/*      */     
/*      */     Separator finish(PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser) {
/* 2081 */       this.iAfterPrinter = paramPeriodPrinter;
/* 2082 */       this.iAfterParser = paramPeriodParser;
/* 2083 */       return this;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class Composite
/*      */     implements PeriodPrinter, PeriodParser
/*      */   {
/*      */     private final PeriodPrinter[] iPrinters;
/*      */     
/*      */     private final PeriodParser[] iParsers;
/*      */     
/*      */ 
/*      */     Composite(List<Object> paramList)
/*      */     {
/* 2098 */       ArrayList localArrayList1 = new ArrayList();
/* 2099 */       ArrayList localArrayList2 = new ArrayList();
/*      */       
/* 2101 */       decompose(paramList, localArrayList1, localArrayList2);
/*      */       
/* 2103 */       if (localArrayList1.size() <= 0) {
/* 2104 */         this.iPrinters = null;
/*      */       } else {
/* 2106 */         this.iPrinters = ((PeriodPrinter[])localArrayList1.toArray(new PeriodPrinter[localArrayList1.size()]));
/*      */       }
/*      */       
/*      */ 
/* 2110 */       if (localArrayList2.size() <= 0) {
/* 2111 */         this.iParsers = null;
/*      */       } else {
/* 2113 */         this.iParsers = ((PeriodParser[])localArrayList2.toArray(new PeriodParser[localArrayList2.size()]));
/*      */       }
/*      */     }
/*      */     
/*      */     public int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale)
/*      */     {
/* 2119 */       int i = 0;
/* 2120 */       PeriodPrinter[] arrayOfPeriodPrinter = this.iPrinters;
/* 2121 */       for (int j = arrayOfPeriodPrinter.length; i < paramInt;) { j--; if (j < 0) break;
/* 2122 */         i += arrayOfPeriodPrinter[j].countFieldsToPrint(paramReadablePeriod, Integer.MAX_VALUE, paramLocale);
/*      */       }
/* 2124 */       return i;
/*      */     }
/*      */     
/*      */     public int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 2128 */       int i = 0;
/* 2129 */       PeriodPrinter[] arrayOfPeriodPrinter = this.iPrinters;
/* 2130 */       int j = arrayOfPeriodPrinter.length; for (;;) { j--; if (j < 0) break;
/* 2131 */         i += arrayOfPeriodPrinter[j].calculatePrintedLength(paramReadablePeriod, paramLocale);
/*      */       }
/* 2133 */       return i;
/*      */     }
/*      */     
/*      */     public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 2137 */       PeriodPrinter[] arrayOfPeriodPrinter = this.iPrinters;
/* 2138 */       int i = arrayOfPeriodPrinter.length;
/* 2139 */       for (int j = 0; j < i; j++) {
/* 2140 */         arrayOfPeriodPrinter[j].printTo(paramStringBuffer, paramReadablePeriod, paramLocale);
/*      */       }
/*      */     }
/*      */     
/*      */     public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale) throws IOException {
/* 2145 */       PeriodPrinter[] arrayOfPeriodPrinter = this.iPrinters;
/* 2146 */       int i = arrayOfPeriodPrinter.length;
/* 2147 */       for (int j = 0; j < i; j++) {
/* 2148 */         arrayOfPeriodPrinter[j].printTo(paramWriter, paramReadablePeriod, paramLocale);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale)
/*      */     {
/* 2155 */       PeriodParser[] arrayOfPeriodParser = this.iParsers;
/* 2156 */       if (arrayOfPeriodParser == null) {
/* 2157 */         throw new UnsupportedOperationException();
/*      */       }
/*      */       
/* 2160 */       int i = arrayOfPeriodParser.length;
/* 2161 */       for (int j = 0; (j < i) && (paramInt >= 0); j++) {
/* 2162 */         paramInt = arrayOfPeriodParser[j].parseInto(paramReadWritablePeriod, paramString, paramInt, paramLocale);
/*      */       }
/* 2164 */       return paramInt;
/*      */     }
/*      */     
/*      */     private void decompose(List<Object> paramList1, List<Object> paramList2, List<Object> paramList3) {
/* 2168 */       int i = paramList1.size();
/* 2169 */       for (int j = 0; j < i; j += 2) {
/* 2170 */         Object localObject = paramList1.get(j);
/* 2171 */         if ((localObject instanceof PeriodPrinter)) {
/* 2172 */           if ((localObject instanceof Composite)) {
/* 2173 */             addArrayToList(paramList2, ((Composite)localObject).iPrinters);
/*      */           } else {
/* 2175 */             paramList2.add(localObject);
/*      */           }
/*      */         }
/*      */         
/* 2179 */         localObject = paramList1.get(j + 1);
/* 2180 */         if ((localObject instanceof PeriodParser)) {
/* 2181 */           if ((localObject instanceof Composite)) {
/* 2182 */             addArrayToList(paramList3, ((Composite)localObject).iParsers);
/*      */           } else {
/* 2184 */             paramList3.add(localObject);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     private void addArrayToList(List<Object> paramList, Object[] paramArrayOfObject) {
/* 2191 */       if (paramArrayOfObject != null) {
/* 2192 */         for (int i = 0; i < paramArrayOfObject.length; i++) {
/* 2193 */           paramList.add(paramArrayOfObject[i]);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\PeriodFormatterBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */