/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.BaseChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatterBuilder;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.FormatUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.DefaultNameProvider;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.FixedDateTimeZone;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.NameProvider;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.Provider;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.UTCProvider;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.tz.ZoneInfoProvider;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectStreamException;
/*      */ import java.io.Serializable;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import org.joda.convert.FromString;
/*      */ import org.joda.convert.ToString;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class DateTimeZone
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 5546345482340108586L;
/*  108 */   public static final DateTimeZone UTC = UTCDateTimeZone.INSTANCE;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int MAX_MILLIS = 86399999;
/*      */   
/*      */ 
/*      */ 
/*  116 */   private static final AtomicReference<Provider> cProvider = new AtomicReference();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  122 */   private static final AtomicReference<NameProvider> cNameProvider = new AtomicReference();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  128 */   private static final AtomicReference<DateTimeZone> cDefault = new AtomicReference();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final String iID;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeZone getDefault()
/*      */   {
/*  145 */     DateTimeZone localDateTimeZone = (DateTimeZone)cDefault.get();
/*  146 */     if (localDateTimeZone == null) {
/*      */       try {
/*      */         try {
/*  149 */           String str = System.getProperty("user.timezone");
/*  150 */           if (str != null) {
/*  151 */             localDateTimeZone = forID(str);
/*      */           }
/*      */         }
/*      */         catch (RuntimeException localRuntimeException) {}
/*      */         
/*  156 */         if (localDateTimeZone == null) {
/*  157 */           localDateTimeZone = forTimeZone(TimeZone.getDefault());
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */       
/*  162 */       if (localDateTimeZone == null) {
/*  163 */         localDateTimeZone = UTC;
/*      */       }
/*  165 */       if (!cDefault.compareAndSet(null, localDateTimeZone)) {
/*  166 */         localDateTimeZone = (DateTimeZone)cDefault.get();
/*      */       }
/*      */     }
/*  169 */     return localDateTimeZone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setDefault(DateTimeZone paramDateTimeZone)
/*      */     throws SecurityException
/*      */   {
/*  182 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  183 */     if (localSecurityManager != null) {
/*  184 */       localSecurityManager.checkPermission(new JodaTimePermission("DateTimeZone.setDefault"));
/*      */     }
/*  186 */     if (paramDateTimeZone == null) {
/*  187 */       throw new IllegalArgumentException("The datetime zone must not be null");
/*      */     }
/*  189 */     cDefault.set(paramDateTimeZone);
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
/*      */   @FromString
/*      */   public static DateTimeZone forID(String paramString)
/*      */   {
/*  210 */     if (paramString == null) {
/*  211 */       return getDefault();
/*      */     }
/*  213 */     if (paramString.equals("UTC")) {
/*  214 */       return UTC;
/*      */     }
/*  216 */     DateTimeZone localDateTimeZone = getProvider().getZone(paramString);
/*  217 */     if (localDateTimeZone != null) {
/*  218 */       return localDateTimeZone;
/*      */     }
/*  220 */     if ((paramString.startsWith("+")) || (paramString.startsWith("-"))) {
/*  221 */       int i = parseOffset(paramString);
/*  222 */       if (i == 0L) {
/*  223 */         return UTC;
/*      */       }
/*  225 */       paramString = printOffset(i);
/*  226 */       return fixedOffsetZone(paramString, i);
/*      */     }
/*      */     
/*  229 */     throw new IllegalArgumentException("The datetime zone id '" + paramString + "' is not recognised");
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
/*      */   public static DateTimeZone forOffsetHours(int paramInt)
/*      */     throws IllegalArgumentException
/*      */   {
/*  243 */     return forOffsetHoursMinutes(paramInt, 0);
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
/*      */   public static DateTimeZone forOffsetHoursMinutes(int paramInt1, int paramInt2)
/*      */     throws IllegalArgumentException
/*      */   {
/*  278 */     if ((paramInt1 == 0) && (paramInt2 == 0)) {
/*  279 */       return UTC;
/*      */     }
/*  281 */     if ((paramInt1 < -23) || (paramInt1 > 23)) {
/*  282 */       throw new IllegalArgumentException("Hours out of range: " + paramInt1);
/*      */     }
/*  284 */     if ((paramInt2 < -59) || (paramInt2 > 59)) {
/*  285 */       throw new IllegalArgumentException("Minutes out of range: " + paramInt2);
/*      */     }
/*  287 */     if ((paramInt1 > 0) && (paramInt2 < 0)) {
/*  288 */       throw new IllegalArgumentException("Positive hours must not have negative minutes: " + paramInt2);
/*      */     }
/*  290 */     int i = 0;
/*      */     try {
/*  292 */       int j = paramInt1 * 60;
/*  293 */       if (j < 0) {
/*  294 */         paramInt2 = j - Math.abs(paramInt2);
/*      */       } else {
/*  296 */         paramInt2 = j + paramInt2;
/*      */       }
/*  298 */       i = FieldUtils.safeMultiply(paramInt2, 60000);
/*      */     } catch (ArithmeticException localArithmeticException) {
/*  300 */       throw new IllegalArgumentException("Offset is too large");
/*      */     }
/*  302 */     return forOffsetMillis(i);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeZone forOffsetMillis(int paramInt)
/*      */   {
/*  312 */     if ((paramInt < -86399999) || (paramInt > 86399999)) {
/*  313 */       throw new IllegalArgumentException("Millis out of range: " + paramInt);
/*      */     }
/*  315 */     String str = printOffset(paramInt);
/*  316 */     return fixedOffsetZone(str, paramInt);
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
/*      */   public static DateTimeZone forTimeZone(TimeZone paramTimeZone)
/*      */   {
/*  335 */     if (paramTimeZone == null) {
/*  336 */       return getDefault();
/*      */     }
/*  338 */     String str1 = paramTimeZone.getID();
/*  339 */     if (str1 == null) {
/*  340 */       throw new IllegalArgumentException("The TimeZone id must not be null");
/*      */     }
/*  342 */     if (str1.equals("UTC")) {
/*  343 */       return UTC;
/*      */     }
/*      */     
/*      */ 
/*  347 */     DateTimeZone localDateTimeZone = null;
/*  348 */     String str2 = getConvertedId(str1);
/*  349 */     Provider localProvider = getProvider();
/*  350 */     if (str2 != null) {
/*  351 */       localDateTimeZone = localProvider.getZone(str2);
/*      */     }
/*  353 */     if (localDateTimeZone == null) {
/*  354 */       localDateTimeZone = localProvider.getZone(str1);
/*      */     }
/*  356 */     if (localDateTimeZone != null) {
/*  357 */       return localDateTimeZone;
/*      */     }
/*      */     
/*      */ 
/*  361 */     if (str2 == null) {
/*  362 */       str2 = str1;
/*  363 */       if ((str2.startsWith("GMT+")) || (str2.startsWith("GMT-"))) {
/*  364 */         str2 = str2.substring(3);
/*  365 */         int i = parseOffset(str2);
/*  366 */         if (i == 0L) {
/*  367 */           return UTC;
/*      */         }
/*  369 */         str2 = printOffset(i);
/*  370 */         return fixedOffsetZone(str2, i);
/*      */       }
/*      */     }
/*      */     
/*  374 */     throw new IllegalArgumentException("The datetime zone id '" + str1 + "' is not recognised");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static DateTimeZone fixedOffsetZone(String paramString, int paramInt)
/*      */   {
/*  386 */     if (paramInt == 0) {
/*  387 */       return UTC;
/*      */     }
/*  389 */     return new FixedDateTimeZone(paramString, null, paramInt, paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Set<String> getAvailableIDs()
/*      */   {
/*  398 */     return getProvider().getAvailableIDs();
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
/*      */   public static Provider getProvider()
/*      */   {
/*  411 */     Provider localProvider = (Provider)cProvider.get();
/*  412 */     if (localProvider == null) {
/*  413 */       localProvider = getDefaultProvider();
/*  414 */       if (!cProvider.compareAndSet(null, localProvider)) {
/*  415 */         localProvider = (Provider)cProvider.get();
/*      */       }
/*      */     }
/*  418 */     return localProvider;
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
/*      */   public static void setProvider(Provider paramProvider)
/*      */     throws SecurityException
/*      */   {
/*  432 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  433 */     if (localSecurityManager != null) {
/*  434 */       localSecurityManager.checkPermission(new JodaTimePermission("DateTimeZone.setProvider"));
/*      */     }
/*  436 */     if (paramProvider == null) {
/*  437 */       paramProvider = getDefaultProvider();
/*      */     } else {
/*  439 */       validateProvider(paramProvider);
/*      */     }
/*  441 */     cProvider.set(paramProvider);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Provider validateProvider(Provider paramProvider)
/*      */   {
/*  452 */     Set localSet = paramProvider.getAvailableIDs();
/*  453 */     if ((localSet == null) || (localSet.size() == 0)) {
/*  454 */       throw new IllegalArgumentException("The provider doesn't have any available ids");
/*      */     }
/*  456 */     if (!localSet.contains("UTC")) {
/*  457 */       throw new IllegalArgumentException("The provider doesn't support UTC");
/*      */     }
/*  459 */     if (!UTC.equals(paramProvider.getZone("UTC"))) {
/*  460 */       throw new IllegalArgumentException("Invalid UTC zone provided");
/*      */     }
/*  462 */     return paramProvider;
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
/*      */   private static Provider getDefaultProvider()
/*      */   {
/*      */     try
/*      */     {
/*  486 */       String str1 = System.getProperty("com.facebook.presto.jdbc.internal.joda.time.DateTimeZone.Provider");
/*  487 */       if (str1 != null) {
/*      */         try {
/*  489 */           Provider localProvider = (Provider)Class.forName(str1).newInstance();
/*  490 */           return validateProvider(localProvider);
/*      */         } catch (Exception localException2) {
/*  492 */           throw new RuntimeException(localException2);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SecurityException localSecurityException1) {}
/*      */     
/*      */     try
/*      */     {
/*  500 */       String str2 = System.getProperty("com.facebook.presto.jdbc.internal.joda.time.DateTimeZone.Folder");
/*  501 */       if (str2 != null) {
/*      */         try {
/*  503 */           ZoneInfoProvider localZoneInfoProvider2 = new ZoneInfoProvider(new File(str2));
/*  504 */           return validateProvider(localZoneInfoProvider2);
/*      */         } catch (Exception localException3) {
/*  506 */           throw new RuntimeException(localException3);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SecurityException localSecurityException2) {}
/*      */     
/*      */     try
/*      */     {
/*  514 */       ZoneInfoProvider localZoneInfoProvider1 = new ZoneInfoProvider("com/facebook/presto/jdbc/internal/joda/time/tz/data");
/*  515 */       return validateProvider(localZoneInfoProvider1);
/*      */     } catch (Exception localException1) {
/*  517 */       localException1.printStackTrace();
/*      */     }
/*      */     
/*  520 */     return new UTCProvider();
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
/*      */   public static NameProvider getNameProvider()
/*      */   {
/*  533 */     NameProvider localNameProvider = (NameProvider)cNameProvider.get();
/*  534 */     if (localNameProvider == null) {
/*  535 */       localNameProvider = getDefaultNameProvider();
/*  536 */       if (!cNameProvider.compareAndSet(null, localNameProvider)) {
/*  537 */         localNameProvider = (NameProvider)cNameProvider.get();
/*      */       }
/*      */     }
/*  540 */     return localNameProvider;
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
/*      */   public static void setNameProvider(NameProvider paramNameProvider)
/*      */     throws SecurityException
/*      */   {
/*  554 */     SecurityManager localSecurityManager = System.getSecurityManager();
/*  555 */     if (localSecurityManager != null) {
/*  556 */       localSecurityManager.checkPermission(new JodaTimePermission("DateTimeZone.setNameProvider"));
/*      */     }
/*  558 */     if (paramNameProvider == null) {
/*  559 */       paramNameProvider = getDefaultNameProvider();
/*      */     }
/*  561 */     cNameProvider.set(paramNameProvider);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static NameProvider getDefaultNameProvider()
/*      */   {
/*  573 */     Object localObject = null;
/*      */     try {
/*  575 */       String str = System.getProperty("com.facebook.presto.jdbc.internal.joda.time.DateTimeZone.NameProvider");
/*  576 */       if (str != null) {
/*      */         try {
/*  578 */           localObject = (NameProvider)Class.forName(str).newInstance();
/*      */         } catch (Exception localException) {
/*  580 */           throw new RuntimeException(localException);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SecurityException localSecurityException) {}
/*      */     
/*      */ 
/*  587 */     if (localObject == null) {
/*  588 */       localObject = new DefaultNameProvider();
/*      */     }
/*      */     
/*  591 */     return (NameProvider)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getConvertedId(String paramString)
/*      */   {
/*  602 */     return (String)LazyInit.CONVERSION_MAP.get(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int parseOffset(String paramString)
/*      */   {
/*  612 */     return -(int)LazyInit.OFFSET_FORMATTER.parseMillis(paramString);
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
/*      */   private static String printOffset(int paramInt)
/*      */   {
/*  625 */     StringBuffer localStringBuffer = new StringBuffer();
/*  626 */     if (paramInt >= 0) {
/*  627 */       localStringBuffer.append('+');
/*      */     } else {
/*  629 */       localStringBuffer.append('-');
/*  630 */       paramInt = -paramInt;
/*      */     }
/*      */     
/*  633 */     int i = paramInt / 3600000;
/*  634 */     FormatUtils.appendPaddedInteger(localStringBuffer, i, 2);
/*  635 */     paramInt -= i * 3600000;
/*      */     
/*  637 */     int j = paramInt / 60000;
/*  638 */     localStringBuffer.append(':');
/*  639 */     FormatUtils.appendPaddedInteger(localStringBuffer, j, 2);
/*  640 */     paramInt -= j * 60000;
/*  641 */     if (paramInt == 0) {
/*  642 */       return localStringBuffer.toString();
/*      */     }
/*      */     
/*  645 */     int k = paramInt / 1000;
/*  646 */     localStringBuffer.append(':');
/*  647 */     FormatUtils.appendPaddedInteger(localStringBuffer, k, 2);
/*  648 */     paramInt -= k * 1000;
/*  649 */     if (paramInt == 0) {
/*  650 */       return localStringBuffer.toString();
/*      */     }
/*      */     
/*  653 */     localStringBuffer.append('.');
/*  654 */     FormatUtils.appendPaddedInteger(localStringBuffer, paramInt, 3);
/*  655 */     return localStringBuffer.toString();
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
/*      */   protected DateTimeZone(String paramString)
/*      */   {
/*  670 */     if (paramString == null) {
/*  671 */       throw new IllegalArgumentException("Id must not be null");
/*      */     }
/*  673 */     this.iID = paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @ToString
/*      */   public final String getID()
/*      */   {
/*  686 */     return this.iID;
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
/*      */   public abstract String getNameKey(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final String getShortName(long paramLong)
/*      */   {
/*  709 */     return getShortName(paramLong, null);
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
/*      */   public String getShortName(long paramLong, Locale paramLocale)
/*      */   {
/*  724 */     if (paramLocale == null) {
/*  725 */       paramLocale = Locale.getDefault();
/*      */     }
/*  727 */     String str1 = getNameKey(paramLong);
/*  728 */     if (str1 == null) {
/*  729 */       return this.iID;
/*      */     }
/*      */     
/*  732 */     NameProvider localNameProvider = getNameProvider();
/*  733 */     String str2; if ((localNameProvider instanceof DefaultNameProvider)) {
/*  734 */       str2 = ((DefaultNameProvider)localNameProvider).getShortName(paramLocale, this.iID, str1, isStandardOffset(paramLong));
/*      */     } else {
/*  736 */       str2 = localNameProvider.getShortName(paramLocale, this.iID, str1);
/*      */     }
/*  738 */     if (str2 != null) {
/*  739 */       return str2;
/*      */     }
/*  741 */     return printOffset(getOffset(paramLong));
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
/*      */   public final String getName(long paramLong)
/*      */   {
/*  755 */     return getName(paramLong, null);
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
/*      */   public String getName(long paramLong, Locale paramLocale)
/*      */   {
/*  770 */     if (paramLocale == null) {
/*  771 */       paramLocale = Locale.getDefault();
/*      */     }
/*  773 */     String str1 = getNameKey(paramLong);
/*  774 */     if (str1 == null) {
/*  775 */       return this.iID;
/*      */     }
/*      */     
/*  778 */     NameProvider localNameProvider = getNameProvider();
/*  779 */     String str2; if ((localNameProvider instanceof DefaultNameProvider)) {
/*  780 */       str2 = ((DefaultNameProvider)localNameProvider).getName(paramLocale, this.iID, str1, isStandardOffset(paramLong));
/*      */     } else {
/*  782 */       str2 = localNameProvider.getName(paramLocale, this.iID, str1);
/*      */     }
/*  784 */     if (str2 != null) {
/*  785 */       return str2;
/*      */     }
/*  787 */     return printOffset(getOffset(paramLong));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int getOffset(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final int getOffset(ReadableInstant paramReadableInstant)
/*      */   {
/*  805 */     if (paramReadableInstant == null) {
/*  806 */       return getOffset(DateTimeUtils.currentTimeMillis());
/*      */     }
/*  808 */     return getOffset(paramReadableInstant.getMillis());
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
/*      */   public abstract int getStandardOffset(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isStandardOffset(long paramLong)
/*      */   {
/*  836 */     return getOffset(paramLong) == getStandardOffset(paramLong);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getOffsetFromLocal(long paramLong)
/*      */   {
/*  877 */     int i = getOffset(paramLong);
/*      */     
/*  879 */     long l1 = paramLong - i;
/*  880 */     int j = getOffset(l1);
/*      */     long l2;
/*  882 */     if (i != j)
/*      */     {
/*      */ 
/*  885 */       if (i - j < 0)
/*      */       {
/*      */ 
/*      */ 
/*  889 */         l2 = nextTransition(l1);
/*  890 */         if (l2 == paramLong - i) {
/*  891 */           l2 = Long.MAX_VALUE;
/*      */         }
/*  893 */         long l3 = nextTransition(paramLong - j);
/*  894 */         if (l3 == paramLong - j) {
/*  895 */           l3 = Long.MAX_VALUE;
/*      */         }
/*  897 */         if (l2 != l3) {
/*  898 */           return i;
/*      */         }
/*      */       }
/*  901 */     } else if (i >= 0) {
/*  902 */       l2 = previousTransition(l1);
/*  903 */       if (l2 < l1) {
/*  904 */         int k = getOffset(l2);
/*  905 */         int m = k - i;
/*  906 */         if (l1 - l2 <= m) {
/*  907 */           return k;
/*      */         }
/*      */       }
/*      */     }
/*  911 */     return j;
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
/*      */   public long convertUTCToLocal(long paramLong)
/*      */   {
/*  925 */     int i = getOffset(paramLong);
/*  926 */     long l = paramLong + i;
/*      */     
/*  928 */     if (((paramLong ^ l) < 0L) && ((paramLong ^ i) >= 0L)) {
/*  929 */       throw new ArithmeticException("Adding time zone offset caused overflow");
/*      */     }
/*  931 */     return l;
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
/*      */   public long convertLocalToUTC(long paramLong1, boolean paramBoolean, long paramLong2)
/*      */   {
/*  952 */     int i = getOffset(paramLong2);
/*  953 */     long l = paramLong1 - i;
/*  954 */     int j = getOffset(l);
/*  955 */     if (j == i) {
/*  956 */       return l;
/*      */     }
/*  958 */     return convertLocalToUTC(paramLong1, paramBoolean);
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
/*      */   public long convertLocalToUTC(long paramLong, boolean paramBoolean)
/*      */   {
/*  975 */     int i = getOffset(paramLong);
/*      */     
/*  977 */     int j = getOffset(paramLong - i);
/*      */     
/*  979 */     if (i != j)
/*      */     {
/*      */ 
/*      */ 
/*  983 */       if ((paramBoolean) || (i < 0))
/*      */       {
/*  985 */         l1 = nextTransition(paramLong - i);
/*  986 */         if (l1 == paramLong - i) {
/*  987 */           l1 = Long.MAX_VALUE;
/*      */         }
/*  989 */         long l2 = nextTransition(paramLong - j);
/*  990 */         if (l2 == paramLong - j) {
/*  991 */           l2 = Long.MAX_VALUE;
/*      */         }
/*  993 */         if (l1 != l2)
/*      */         {
/*  995 */           if (paramBoolean)
/*      */           {
/*  997 */             throw new IllegalInstantException(paramLong, getID());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1002 */           j = i;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1008 */     long l1 = paramLong - j;
/*      */     
/* 1010 */     if (((paramLong ^ l1) < 0L) && ((paramLong ^ j) < 0L)) {
/* 1011 */       throw new ArithmeticException("Subtracting time zone offset caused overflow");
/*      */     }
/* 1013 */     return l1;
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
/*      */   public long getMillisKeepLocal(DateTimeZone paramDateTimeZone, long paramLong)
/*      */   {
/* 1027 */     if (paramDateTimeZone == null) {
/* 1028 */       paramDateTimeZone = getDefault();
/*      */     }
/* 1030 */     if (paramDateTimeZone == this) {
/* 1031 */       return paramLong;
/*      */     }
/* 1033 */     long l = convertUTCToLocal(paramLong);
/* 1034 */     return paramDateTimeZone.convertLocalToUTC(l, false, paramLong);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isLocalDateTimeGap(LocalDateTime paramLocalDateTime)
/*      */   {
/* 1144 */     if (isFixed()) {
/* 1145 */       return false;
/*      */     }
/*      */     try {
/* 1148 */       paramLocalDateTime.toDateTime(this);
/* 1149 */       return false;
/*      */     } catch (IllegalInstantException localIllegalInstantException) {}
/* 1151 */     return true;
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
/*      */   public long adjustOffset(long paramLong, boolean paramBoolean)
/*      */   {
/* 1166 */     long l1 = paramLong - 10800000L;
/* 1167 */     long l2 = paramLong + 10800000L;
/* 1168 */     long l3 = getOffset(l1);
/* 1169 */     long l4 = getOffset(l2);
/* 1170 */     if (l3 <= l4) {
/* 1171 */       return paramLong;
/*      */     }
/*      */     
/*      */ 
/* 1175 */     long l5 = l3 - l4;
/* 1176 */     long l6 = nextTransition(l1);
/* 1177 */     long l7 = l6 - l5;
/* 1178 */     long l8 = l6 + l5;
/* 1179 */     if ((paramLong < l7) || (paramLong >= l8)) {
/* 1180 */       return paramLong;
/*      */     }
/*      */     
/*      */ 
/* 1184 */     long l9 = paramLong - l7;
/* 1185 */     if (l9 >= l5)
/*      */     {
/* 1187 */       return paramBoolean ? paramLong : paramLong - l5;
/*      */     }
/*      */     
/* 1190 */     return paramBoolean ? paramLong + l5 : paramLong;
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
/*      */   public abstract boolean isFixed();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long nextTransition(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long previousTransition(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TimeZone toTimeZone()
/*      */   {
/* 1232 */     return TimeZone.getTimeZone(this.iID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract boolean equals(Object paramObject);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1249 */     return 57 + getID().hashCode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1257 */     return getID();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object writeReplace()
/*      */     throws ObjectStreamException
/*      */   {
/* 1267 */     return new Stub(this.iID);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final class Stub
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = -6471952376487863581L;
/*      */     
/*      */ 
/*      */     private transient String iID;
/*      */     
/*      */ 
/*      */ 
/*      */     Stub(String paramString)
/*      */     {
/* 1284 */       this.iID = paramString;
/*      */     }
/*      */     
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
/* 1288 */       paramObjectOutputStream.writeUTF(this.iID);
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
/* 1292 */       this.iID = paramObjectInputStream.readUTF();
/*      */     }
/*      */     
/*      */     private Object readResolve() throws ObjectStreamException {
/* 1296 */       return DateTimeZone.forID(this.iID);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static final class LazyInit
/*      */   {
/* 1307 */     static final Map<String, String> CONVERSION_MAP = ;
/*      */     
/* 1309 */     static final DateTimeFormatter OFFSET_FORMATTER = buildFormatter();
/*      */     
/*      */ 
/*      */     private static DateTimeFormatter buildFormatter()
/*      */     {
/* 1314 */       BaseChronology local1 = new BaseChronology() {
/*      */         private static final long serialVersionUID = -3128740902654445468L;
/*      */         
/* 1317 */         public DateTimeZone getZone() { return null; }
/*      */         
/*      */         public Chronology withUTC() {
/* 1320 */           return this;
/*      */         }
/*      */         
/* 1323 */         public Chronology withZone(DateTimeZone paramAnonymousDateTimeZone) { return this; }
/*      */         
/*      */         public String toString() {
/* 1326 */           return getClass().getName();
/*      */         }
/* 1328 */       };
/* 1329 */       return new DateTimeFormatterBuilder().appendTimeZoneOffset(null, true, 2, 4).toFormatter().withChronology(local1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private static Map<String, String> buildMap()
/*      */     {
/* 1337 */       HashMap localHashMap = new HashMap();
/* 1338 */       localHashMap.put("GMT", "UTC");
/* 1339 */       localHashMap.put("WET", "WET");
/* 1340 */       localHashMap.put("CET", "CET");
/* 1341 */       localHashMap.put("MET", "CET");
/* 1342 */       localHashMap.put("ECT", "CET");
/* 1343 */       localHashMap.put("EET", "EET");
/* 1344 */       localHashMap.put("MIT", "Pacific/Apia");
/* 1345 */       localHashMap.put("HST", "Pacific/Honolulu");
/* 1346 */       localHashMap.put("AST", "America/Anchorage");
/* 1347 */       localHashMap.put("PST", "America/Los_Angeles");
/* 1348 */       localHashMap.put("MST", "America/Denver");
/* 1349 */       localHashMap.put("PNT", "America/Phoenix");
/* 1350 */       localHashMap.put("CST", "America/Chicago");
/* 1351 */       localHashMap.put("EST", "America/New_York");
/* 1352 */       localHashMap.put("IET", "America/Indiana/Indianapolis");
/* 1353 */       localHashMap.put("PRT", "America/Puerto_Rico");
/* 1354 */       localHashMap.put("CNT", "America/St_Johns");
/* 1355 */       localHashMap.put("AGT", "America/Argentina/Buenos_Aires");
/* 1356 */       localHashMap.put("BET", "America/Sao_Paulo");
/* 1357 */       localHashMap.put("ART", "Africa/Cairo");
/* 1358 */       localHashMap.put("CAT", "Africa/Harare");
/* 1359 */       localHashMap.put("EAT", "Africa/Addis_Ababa");
/* 1360 */       localHashMap.put("NET", "Asia/Yerevan");
/* 1361 */       localHashMap.put("PLT", "Asia/Karachi");
/* 1362 */       localHashMap.put("IST", "Asia/Kolkata");
/* 1363 */       localHashMap.put("BST", "Asia/Dhaka");
/* 1364 */       localHashMap.put("VST", "Asia/Ho_Chi_Minh");
/* 1365 */       localHashMap.put("CTT", "Asia/Shanghai");
/* 1366 */       localHashMap.put("JST", "Asia/Tokyo");
/* 1367 */       localHashMap.put("ACT", "Australia/Darwin");
/* 1368 */       localHashMap.put("AET", "Australia/Sydney");
/* 1369 */       localHashMap.put("SST", "Pacific/Guadalcanal");
/* 1370 */       localHashMap.put("NST", "Pacific/Auckland");
/* 1371 */       return Collections.unmodifiableMap(localHashMap);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\DateTimeZone.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */