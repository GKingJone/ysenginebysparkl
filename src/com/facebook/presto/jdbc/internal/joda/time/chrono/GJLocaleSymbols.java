/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ import java.text.DateFormatSymbols;
/*     */ import java.util.Locale;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class GJLocaleSymbols
/*     */ {
/*  36 */   private static ConcurrentMap<Locale, GJLocaleSymbols> cCache = new ConcurrentHashMap();
/*     */   private final String[] iEras;
/*     */   private final String[] iDaysOfWeek;
/*     */   private final String[] iShortDaysOfWeek;
/*     */   private final String[] iMonths;
/*     */   private final String[] iShortMonths;
/*     */   private final String[] iHalfday;
/*     */   private final TreeMap<String, Integer> iParseEras;
/*     */   
/*  45 */   static GJLocaleSymbols forLocale(Locale paramLocale) { if (paramLocale == null) {
/*  46 */       paramLocale = Locale.getDefault();
/*     */     }
/*  48 */     Object localObject = (GJLocaleSymbols)cCache.get(paramLocale);
/*  49 */     if (localObject == null) {
/*  50 */       localObject = new GJLocaleSymbols(paramLocale);
/*  51 */       GJLocaleSymbols localGJLocaleSymbols = (GJLocaleSymbols)cCache.putIfAbsent(paramLocale, localObject);
/*  52 */       if (localGJLocaleSymbols != null) {
/*  53 */         localObject = localGJLocaleSymbols;
/*     */       }
/*     */     }
/*  56 */     return (GJLocaleSymbols)localObject;
/*     */   }
/*     */   
/*     */   private static String[] realignMonths(String[] paramArrayOfString) {
/*  60 */     String[] arrayOfString = new String[13];
/*  61 */     for (int i = 1; i < 13; i++) {
/*  62 */       arrayOfString[i] = paramArrayOfString[(i - 1)];
/*     */     }
/*  64 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   private static String[] realignDaysOfWeek(String[] paramArrayOfString) {
/*  68 */     String[] arrayOfString = new String[8];
/*  69 */     for (int i = 1; i < 8; i++) {
/*  70 */       arrayOfString[i] = paramArrayOfString[1];
/*     */     }
/*  72 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   private static void addSymbols(TreeMap<String, Integer> paramTreeMap, String[] paramArrayOfString, Integer[] paramArrayOfInteger) {
/*  76 */     int i = paramArrayOfString.length; for (;;) { i--; if (i < 0) break;
/*  77 */       String str = paramArrayOfString[i];
/*  78 */       if (str != null) {
/*  79 */         paramTreeMap.put(str, paramArrayOfInteger[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void addNumerals(TreeMap<String, Integer> paramTreeMap, int paramInt1, int paramInt2, Integer[] paramArrayOfInteger) {
/*  85 */     for (int i = paramInt1; i <= paramInt2; i++) {
/*  86 */       paramTreeMap.put(String.valueOf(i).intern(), paramArrayOfInteger[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   private static int maxLength(String[] paramArrayOfString) {
/*  91 */     int i = 0;
/*  92 */     int j = paramArrayOfString.length; for (;;) { j--; if (j < 0) break;
/*  93 */       String str = paramArrayOfString[j];
/*  94 */       if (str != null) {
/*  95 */         int k = str.length();
/*  96 */         if (k > i) {
/*  97 */           i = k;
/*     */         }
/*     */       }
/*     */     }
/* 101 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private final TreeMap<String, Integer> iParseDaysOfWeek;
/*     */   
/*     */ 
/*     */   private final TreeMap<String, Integer> iParseMonths;
/*     */   
/*     */ 
/*     */   private final int iMaxEraLength;
/*     */   
/*     */   private final int iMaxDayOfWeekLength;
/*     */   
/*     */   private final int iMaxShortDayOfWeekLength;
/*     */   
/*     */   private final int iMaxMonthLength;
/*     */   
/*     */   private final int iMaxShortMonthLength;
/*     */   
/*     */   private final int iMaxHalfdayLength;
/*     */   
/*     */   private GJLocaleSymbols(Locale paramLocale)
/*     */   {
/* 126 */     DateFormatSymbols localDateFormatSymbols = DateTimeUtils.getDateFormatSymbols(paramLocale);
/*     */     
/* 128 */     this.iEras = localDateFormatSymbols.getEras();
/* 129 */     this.iDaysOfWeek = realignDaysOfWeek(localDateFormatSymbols.getWeekdays());
/* 130 */     this.iShortDaysOfWeek = realignDaysOfWeek(localDateFormatSymbols.getShortWeekdays());
/* 131 */     this.iMonths = realignMonths(localDateFormatSymbols.getMonths());
/* 132 */     this.iShortMonths = realignMonths(localDateFormatSymbols.getShortMonths());
/* 133 */     this.iHalfday = localDateFormatSymbols.getAmPmStrings();
/*     */     
/* 135 */     Integer[] arrayOfInteger = new Integer[13];
/* 136 */     for (int i = 0; i < 13; i++) {
/* 137 */       arrayOfInteger[i] = Integer.valueOf(i);
/*     */     }
/*     */     
/* 140 */     this.iParseEras = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 141 */     addSymbols(this.iParseEras, this.iEras, arrayOfInteger);
/* 142 */     if ("en".equals(paramLocale.getLanguage()))
/*     */     {
/*     */ 
/*     */ 
/* 146 */       this.iParseEras.put("BCE", arrayOfInteger[0]);
/* 147 */       this.iParseEras.put("CE", arrayOfInteger[1]);
/*     */     }
/*     */     
/* 150 */     this.iParseDaysOfWeek = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 151 */     addSymbols(this.iParseDaysOfWeek, this.iDaysOfWeek, arrayOfInteger);
/* 152 */     addSymbols(this.iParseDaysOfWeek, this.iShortDaysOfWeek, arrayOfInteger);
/* 153 */     addNumerals(this.iParseDaysOfWeek, 1, 7, arrayOfInteger);
/*     */     
/* 155 */     this.iParseMonths = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 156 */     addSymbols(this.iParseMonths, this.iMonths, arrayOfInteger);
/* 157 */     addSymbols(this.iParseMonths, this.iShortMonths, arrayOfInteger);
/* 158 */     addNumerals(this.iParseMonths, 1, 12, arrayOfInteger);
/*     */     
/* 160 */     this.iMaxEraLength = maxLength(this.iEras);
/* 161 */     this.iMaxDayOfWeekLength = maxLength(this.iDaysOfWeek);
/* 162 */     this.iMaxShortDayOfWeekLength = maxLength(this.iShortDaysOfWeek);
/* 163 */     this.iMaxMonthLength = maxLength(this.iMonths);
/* 164 */     this.iMaxShortMonthLength = maxLength(this.iShortMonths);
/* 165 */     this.iMaxHalfdayLength = maxLength(this.iHalfday);
/*     */   }
/*     */   
/*     */   public String eraValueToText(int paramInt) {
/* 169 */     return this.iEras[paramInt];
/*     */   }
/*     */   
/*     */   public int eraTextToValue(String paramString) {
/* 173 */     Integer localInteger = (Integer)this.iParseEras.get(paramString);
/* 174 */     if (localInteger != null) {
/* 175 */       return localInteger.intValue();
/*     */     }
/* 177 */     throw new IllegalFieldValueException(DateTimeFieldType.era(), paramString);
/*     */   }
/*     */   
/*     */   public int getEraMaxTextLength() {
/* 181 */     return this.iMaxEraLength;
/*     */   }
/*     */   
/*     */   public String monthOfYearValueToText(int paramInt) {
/* 185 */     return this.iMonths[paramInt];
/*     */   }
/*     */   
/*     */   public String monthOfYearValueToShortText(int paramInt) {
/* 189 */     return this.iShortMonths[paramInt];
/*     */   }
/*     */   
/*     */   public int monthOfYearTextToValue(String paramString) {
/* 193 */     Integer localInteger = (Integer)this.iParseMonths.get(paramString);
/* 194 */     if (localInteger != null) {
/* 195 */       return localInteger.intValue();
/*     */     }
/* 197 */     throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), paramString);
/*     */   }
/*     */   
/*     */   public int getMonthMaxTextLength() {
/* 201 */     return this.iMaxMonthLength;
/*     */   }
/*     */   
/*     */   public int getMonthMaxShortTextLength() {
/* 205 */     return this.iMaxShortMonthLength;
/*     */   }
/*     */   
/*     */   public String dayOfWeekValueToText(int paramInt) {
/* 209 */     return this.iDaysOfWeek[paramInt];
/*     */   }
/*     */   
/*     */   public String dayOfWeekValueToShortText(int paramInt) {
/* 213 */     return this.iShortDaysOfWeek[paramInt];
/*     */   }
/*     */   
/*     */   public int dayOfWeekTextToValue(String paramString) {
/* 217 */     Integer localInteger = (Integer)this.iParseDaysOfWeek.get(paramString);
/* 218 */     if (localInteger != null) {
/* 219 */       return localInteger.intValue();
/*     */     }
/* 221 */     throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), paramString);
/*     */   }
/*     */   
/*     */   public int getDayOfWeekMaxTextLength() {
/* 225 */     return this.iMaxDayOfWeekLength;
/*     */   }
/*     */   
/*     */   public int getDayOfWeekMaxShortTextLength() {
/* 229 */     return this.iMaxShortDayOfWeekLength;
/*     */   }
/*     */   
/*     */   public String halfdayValueToText(int paramInt) {
/* 233 */     return this.iHalfday[paramInt];
/*     */   }
/*     */   
/*     */   public int halfdayTextToValue(String paramString) {
/* 237 */     String[] arrayOfString = this.iHalfday;
/* 238 */     int i = arrayOfString.length; do { i--; if (i < 0) break;
/* 239 */     } while (!arrayOfString[i].equalsIgnoreCase(paramString));
/* 240 */     return i;
/*     */     
/*     */ 
/* 243 */     throw new IllegalFieldValueException(DateTimeFieldType.halfdayOfDay(), paramString);
/*     */   }
/*     */   
/*     */   public int getHalfdayMaxTextLength() {
/* 247 */     return this.iMaxHalfdayLength;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJLocaleSymbols.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */