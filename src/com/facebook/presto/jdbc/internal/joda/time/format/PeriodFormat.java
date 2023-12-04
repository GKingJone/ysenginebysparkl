/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.ResourceBundle;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PeriodFormat
/*     */ {
/*     */   private static final String BUNDLE_NAME = "com.facebook.presto.jdbc.internal.joda.time.format.messages";
/*  53 */   private static final ConcurrentMap<Locale, PeriodFormatter> FORMATTERS = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodFormatter getDefault()
/*     */   {
/*  73 */     return wordBased(Locale.ENGLISH);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodFormatter wordBased()
/*     */   {
/*  85 */     return wordBased(Locale.getDefault());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodFormatter wordBased(Locale paramLocale)
/*     */   {
/* 203 */     Object localObject = (PeriodFormatter)FORMATTERS.get(paramLocale);
/* 204 */     if (localObject == null) {
/* 205 */       DynamicWordBased localDynamicWordBased = new DynamicWordBased(buildWordBased(paramLocale));
/* 206 */       localObject = new PeriodFormatter(localDynamicWordBased, localDynamicWordBased, paramLocale, null);
/* 207 */       PeriodFormatter localPeriodFormatter = (PeriodFormatter)FORMATTERS.putIfAbsent(paramLocale, localObject);
/* 208 */       if (localPeriodFormatter != null) {
/* 209 */         localObject = localPeriodFormatter;
/*     */       }
/*     */     }
/* 212 */     return (PeriodFormatter)localObject;
/*     */   }
/*     */   
/*     */   private static PeriodFormatter buildWordBased(Locale paramLocale)
/*     */   {
/* 217 */     ResourceBundle localResourceBundle = ResourceBundle.getBundle("com.facebook.presto.jdbc.internal.joda.time.format.messages", paramLocale);
/* 218 */     if (containsKey(localResourceBundle, "PeriodFormat.regex.separator")) {
/* 219 */       return buildRegExFormatter(localResourceBundle, paramLocale);
/*     */     }
/* 221 */     return buildNonRegExFormatter(localResourceBundle, paramLocale);
/*     */   }
/*     */   
/*     */   private static PeriodFormatter buildRegExFormatter(ResourceBundle paramResourceBundle, Locale paramLocale)
/*     */   {
/* 226 */     String[] arrayOfString = retrieveVariants(paramResourceBundle);
/* 227 */     String str = paramResourceBundle.getString("PeriodFormat.regex.separator");
/*     */     
/* 229 */     PeriodFormatterBuilder localPeriodFormatterBuilder = new PeriodFormatterBuilder();
/* 230 */     localPeriodFormatterBuilder.appendYears();
/* 231 */     if (containsKey(paramResourceBundle, "PeriodFormat.years.regex")) {
/* 232 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.years.regex").split(str), paramResourceBundle.getString("PeriodFormat.years.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 236 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.year"), paramResourceBundle.getString("PeriodFormat.years"));
/*     */     }
/*     */     
/* 239 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 240 */     localPeriodFormatterBuilder.appendMonths();
/* 241 */     if (containsKey(paramResourceBundle, "PeriodFormat.months.regex")) {
/* 242 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.months.regex").split(str), paramResourceBundle.getString("PeriodFormat.months.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 246 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.month"), paramResourceBundle.getString("PeriodFormat.months"));
/*     */     }
/*     */     
/* 249 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 250 */     localPeriodFormatterBuilder.appendWeeks();
/* 251 */     if (containsKey(paramResourceBundle, "PeriodFormat.weeks.regex")) {
/* 252 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.weeks.regex").split(str), paramResourceBundle.getString("PeriodFormat.weeks.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 256 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.week"), paramResourceBundle.getString("PeriodFormat.weeks"));
/*     */     }
/*     */     
/* 259 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 260 */     localPeriodFormatterBuilder.appendDays();
/* 261 */     if (containsKey(paramResourceBundle, "PeriodFormat.days.regex")) {
/* 262 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.days.regex").split(str), paramResourceBundle.getString("PeriodFormat.days.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 266 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.day"), paramResourceBundle.getString("PeriodFormat.days"));
/*     */     }
/*     */     
/* 269 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 270 */     localPeriodFormatterBuilder.appendHours();
/* 271 */     if (containsKey(paramResourceBundle, "PeriodFormat.hours.regex")) {
/* 272 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.hours.regex").split(str), paramResourceBundle.getString("PeriodFormat.hours.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 276 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.hour"), paramResourceBundle.getString("PeriodFormat.hours"));
/*     */     }
/*     */     
/* 279 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 280 */     localPeriodFormatterBuilder.appendMinutes();
/* 281 */     if (containsKey(paramResourceBundle, "PeriodFormat.minutes.regex")) {
/* 282 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.minutes.regex").split(str), paramResourceBundle.getString("PeriodFormat.minutes.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 286 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.minute"), paramResourceBundle.getString("PeriodFormat.minutes"));
/*     */     }
/*     */     
/* 289 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 290 */     localPeriodFormatterBuilder.appendSeconds();
/* 291 */     if (containsKey(paramResourceBundle, "PeriodFormat.seconds.regex")) {
/* 292 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.seconds.regex").split(str), paramResourceBundle.getString("PeriodFormat.seconds.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 296 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.second"), paramResourceBundle.getString("PeriodFormat.seconds"));
/*     */     }
/*     */     
/* 299 */     localPeriodFormatterBuilder.appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString);
/* 300 */     localPeriodFormatterBuilder.appendMillis();
/* 301 */     if (containsKey(paramResourceBundle, "PeriodFormat.milliseconds.regex")) {
/* 302 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.milliseconds.regex").split(str), paramResourceBundle.getString("PeriodFormat.milliseconds.list").split(str));
/*     */     }
/*     */     else
/*     */     {
/* 306 */       localPeriodFormatterBuilder.appendSuffix(paramResourceBundle.getString("PeriodFormat.millisecond"), paramResourceBundle.getString("PeriodFormat.milliseconds"));
/*     */     }
/* 308 */     return localPeriodFormatterBuilder.toFormatter().withLocale(paramLocale);
/*     */   }
/*     */   
/*     */   private static PeriodFormatter buildNonRegExFormatter(ResourceBundle paramResourceBundle, Locale paramLocale) {
/* 312 */     String[] arrayOfString = retrieveVariants(paramResourceBundle);
/* 313 */     return new PeriodFormatterBuilder().appendYears().appendSuffix(paramResourceBundle.getString("PeriodFormat.year"), paramResourceBundle.getString("PeriodFormat.years")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendMonths().appendSuffix(paramResourceBundle.getString("PeriodFormat.month"), paramResourceBundle.getString("PeriodFormat.months")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendWeeks().appendSuffix(paramResourceBundle.getString("PeriodFormat.week"), paramResourceBundle.getString("PeriodFormat.weeks")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendDays().appendSuffix(paramResourceBundle.getString("PeriodFormat.day"), paramResourceBundle.getString("PeriodFormat.days")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendHours().appendSuffix(paramResourceBundle.getString("PeriodFormat.hour"), paramResourceBundle.getString("PeriodFormat.hours")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendMinutes().appendSuffix(paramResourceBundle.getString("PeriodFormat.minute"), paramResourceBundle.getString("PeriodFormat.minutes")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendSeconds().appendSuffix(paramResourceBundle.getString("PeriodFormat.second"), paramResourceBundle.getString("PeriodFormat.seconds")).appendSeparator(paramResourceBundle.getString("PeriodFormat.commaspace"), paramResourceBundle.getString("PeriodFormat.spaceandspace"), arrayOfString).appendMillis().appendSuffix(paramResourceBundle.getString("PeriodFormat.millisecond"), paramResourceBundle.getString("PeriodFormat.milliseconds")).toFormatter().withLocale(paramLocale);
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
/*     */   private static String[] retrieveVariants(ResourceBundle paramResourceBundle)
/*     */   {
/* 341 */     return new String[] { paramResourceBundle.getString("PeriodFormat.space"), paramResourceBundle.getString("PeriodFormat.comma"), paramResourceBundle.getString("PeriodFormat.commandand"), paramResourceBundle.getString("PeriodFormat.commaspaceand") };
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean containsKey(ResourceBundle paramResourceBundle, String paramString)
/*     */   {
/* 347 */     for (Enumeration localEnumeration = paramResourceBundle.getKeys(); localEnumeration.hasMoreElements();) {
/* 348 */       if (((String)localEnumeration.nextElement()).equals(paramString)) {
/* 349 */         return true;
/*     */       }
/*     */     }
/* 352 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class DynamicWordBased
/*     */     implements PeriodPrinter, PeriodParser
/*     */   {
/*     */     private final PeriodFormatter iFormatter;
/*     */     
/*     */ 
/*     */ 
/*     */     DynamicWordBased(PeriodFormatter paramPeriodFormatter)
/*     */     {
/* 367 */       this.iFormatter = paramPeriodFormatter;
/*     */     }
/*     */     
/*     */     public int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale) {
/* 371 */       return getPrinter(paramLocale).countFieldsToPrint(paramReadablePeriod, paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     public int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 375 */       return getPrinter(paramLocale).calculatePrintedLength(paramReadablePeriod, paramLocale);
/*     */     }
/*     */     
/*     */     public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale) {
/* 379 */       getPrinter(paramLocale).printTo(paramStringBuffer, paramReadablePeriod, paramLocale);
/*     */     }
/*     */     
/*     */     public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale) throws IOException {
/* 383 */       getPrinter(paramLocale).printTo(paramWriter, paramReadablePeriod, paramLocale);
/*     */     }
/*     */     
/*     */     private PeriodPrinter getPrinter(Locale paramLocale) {
/* 387 */       if ((paramLocale != null) && (!paramLocale.equals(this.iFormatter.getLocale()))) {
/* 388 */         return PeriodFormat.wordBased(paramLocale).getPrinter();
/*     */       }
/* 390 */       return this.iFormatter.getPrinter();
/*     */     }
/*     */     
/*     */ 
/*     */     public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale)
/*     */     {
/* 396 */       return getParser(paramLocale).parseInto(paramReadWritablePeriod, paramString, paramInt, paramLocale);
/*     */     }
/*     */     
/*     */     private PeriodParser getParser(Locale paramLocale) {
/* 400 */       if ((paramLocale != null) && (!paramLocale.equals(this.iFormatter.getLocale()))) {
/* 401 */         return PeriodFormat.wordBased(paramLocale).getParser();
/*     */       }
/* 403 */       return this.iFormatter.getParser();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\PeriodFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */