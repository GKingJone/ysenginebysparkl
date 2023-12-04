/*     */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutableDateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.LenientChronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZoneInfoCompiler
/*     */ {
/*     */   static DateTimeOfYear cStartOfYear;
/*     */   static Chronology cLenientISO;
/*     */   private Map<String, RuleSet> iRuleSets;
/*     */   private List<Zone> iZones;
/*     */   private List<String> iGoodLinks;
/*     */   private List<String> iBackLinks;
/*     */   
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws Exception
/*     */   {
/*  81 */     if (paramArrayOfString.length == 0) {
/*  82 */       printUsage();
/*  83 */       return;
/*     */     }
/*     */     
/*  86 */     File localFile1 = null;
/*  87 */     File localFile2 = null;
/*  88 */     boolean bool = false;
/*     */     
/*     */ 
/*  91 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*     */       try {
/*  93 */         if ("-src".equals(paramArrayOfString[i])) {
/*  94 */           localFile1 = new File(paramArrayOfString[(++i)]);
/*  95 */         } else if ("-dst".equals(paramArrayOfString[i])) {
/*  96 */           localFile2 = new File(paramArrayOfString[(++i)]);
/*  97 */         } else if ("-verbose".equals(paramArrayOfString[i])) {
/*  98 */           bool = true;
/*  99 */         } else { if ("-?".equals(paramArrayOfString[i])) {
/* 100 */             printUsage();
/* 101 */             return;
/*     */           }
/* 103 */           break;
/*     */         }
/*     */       } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {
/* 106 */         printUsage();
/* 107 */         return;
/*     */       }
/*     */     }
/*     */     
/* 111 */     if (i >= paramArrayOfString.length) {
/* 112 */       printUsage();
/* 113 */       return;
/*     */     }
/*     */     
/* 116 */     File[] arrayOfFile = new File[paramArrayOfString.length - i];
/* 117 */     for (int j = 0; i < paramArrayOfString.length; j++) {
/* 118 */       arrayOfFile[j] = (localFile1 == null ? new File(paramArrayOfString[i]) : new File(localFile1, paramArrayOfString[i]));i++;
/*     */     }
/*     */     
/* 121 */     ZoneInfoLogger.set(bool);
/* 122 */     ZoneInfoCompiler localZoneInfoCompiler = new ZoneInfoCompiler();
/* 123 */     localZoneInfoCompiler.compile(localFile2, arrayOfFile);
/*     */   }
/*     */   
/*     */   private static void printUsage() {
/* 127 */     System.out.println("Usage: java org.joda.time.tz.ZoneInfoCompiler <options> <source files>");
/* 128 */     System.out.println("where possible options include:");
/* 129 */     System.out.println("  -src <directory>    Specify where to read source files");
/* 130 */     System.out.println("  -dst <directory>    Specify where to write generated files");
/* 131 */     System.out.println("  -verbose            Output verbosely (default false)");
/*     */   }
/*     */   
/*     */   static DateTimeOfYear getStartOfYear() {
/* 135 */     if (cStartOfYear == null) {
/* 136 */       cStartOfYear = new DateTimeOfYear();
/*     */     }
/* 138 */     return cStartOfYear;
/*     */   }
/*     */   
/*     */   static Chronology getLenientISOChronology() {
/* 142 */     if (cLenientISO == null) {
/* 143 */       cLenientISO = LenientChronology.getInstance(ISOChronology.getInstanceUTC());
/*     */     }
/* 145 */     return cLenientISO;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void writeZoneInfoMap(DataOutputStream paramDataOutputStream, Map<String, DateTimeZone> paramMap)
/*     */     throws IOException
/*     */   {
/* 153 */     HashMap localHashMap = new HashMap(paramMap.size());
/* 154 */     TreeMap localTreeMap = new TreeMap();
/*     */     
/* 156 */     short s = 0;
/* 157 */     for (Iterator localIterator = paramMap.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/* 158 */       str = (String)((Entry)localObject).getKey();
/* 159 */       Short localShort; if (!localHashMap.containsKey(str)) {
/* 160 */         localShort = Short.valueOf(s);
/* 161 */         localHashMap.put(str, localShort);
/* 162 */         localTreeMap.put(localShort, str);
/* 163 */         s = (short)(s + 1); if (s == 0) {
/* 164 */           throw new InternalError("Too many time zone ids");
/*     */         }
/*     */       }
/* 167 */       str = ((DateTimeZone)((Entry)localObject).getValue()).getID();
/* 168 */       if (!localHashMap.containsKey(str)) {
/* 169 */         localShort = Short.valueOf(s);
/* 170 */         localHashMap.put(str, localShort);
/* 171 */         localTreeMap.put(localShort, str);
/* 172 */         s = (short)(s + 1); if (s == 0) {
/* 173 */           throw new InternalError("Too many time zone ids");
/*     */         }
/*     */       }
/*     */     }
/*     */     Object localObject;
/*     */     String str;
/* 179 */     paramDataOutputStream.writeShort(localTreeMap.size());
/* 180 */     for (localIterator = localTreeMap.values().iterator(); localIterator.hasNext();) { localObject = (String)localIterator.next();
/* 181 */       paramDataOutputStream.writeUTF((String)localObject);
/*     */     }
/*     */     
/*     */ 
/* 185 */     paramDataOutputStream.writeShort(paramMap.size());
/* 186 */     for (localIterator = paramMap.entrySet().iterator(); localIterator.hasNext();) { localObject = (Entry)localIterator.next();
/* 187 */       str = (String)((Entry)localObject).getKey();
/* 188 */       paramDataOutputStream.writeShort(((Short)localHashMap.get(str)).shortValue());
/* 189 */       str = ((DateTimeZone)((Entry)localObject).getValue()).getID();
/* 190 */       paramDataOutputStream.writeShort(((Short)localHashMap.get(str)).shortValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static int parseYear(String paramString, int paramInt) {
/* 195 */     paramString = paramString.toLowerCase();
/* 196 */     if ((paramString.equals("minimum")) || (paramString.equals("min")))
/* 197 */       return Integer.MIN_VALUE;
/* 198 */     if ((paramString.equals("maximum")) || (paramString.equals("max")))
/* 199 */       return Integer.MAX_VALUE;
/* 200 */     if (paramString.equals("only")) {
/* 201 */       return paramInt;
/*     */     }
/* 203 */     return Integer.parseInt(paramString);
/*     */   }
/*     */   
/*     */   static int parseMonth(String paramString) {
/* 207 */     DateTimeField localDateTimeField = ISOChronology.getInstanceUTC().monthOfYear();
/* 208 */     return localDateTimeField.get(localDateTimeField.set(0L, paramString, Locale.ENGLISH));
/*     */   }
/*     */   
/*     */   static int parseDayOfWeek(String paramString) {
/* 212 */     DateTimeField localDateTimeField = ISOChronology.getInstanceUTC().dayOfWeek();
/* 213 */     return localDateTimeField.get(localDateTimeField.set(0L, paramString, Locale.ENGLISH));
/*     */   }
/*     */   
/*     */   static String parseOptional(String paramString) {
/* 217 */     return paramString.equals("-") ? null : paramString;
/*     */   }
/*     */   
/*     */   static int parseTime(String paramString) {
/* 221 */     DateTimeFormatter localDateTimeFormatter = ISODateTimeFormat.hourMinuteSecondFraction();
/* 222 */     MutableDateTime localMutableDateTime = new MutableDateTime(0L, getLenientISOChronology());
/* 223 */     int i = 0;
/* 224 */     if (paramString.startsWith("-")) {
/* 225 */       i = 1;
/*     */     }
/* 227 */     int j = localDateTimeFormatter.parseInto(localMutableDateTime, paramString, i);
/* 228 */     if (j == (i ^ 0xFFFFFFFF)) {
/* 229 */       throw new IllegalArgumentException(paramString);
/*     */     }
/* 231 */     int k = (int)localMutableDateTime.getMillis();
/* 232 */     if (i == 1) {
/* 233 */       k = -k;
/*     */     }
/* 235 */     return k;
/*     */   }
/*     */   
/*     */   static char parseZoneChar(char paramChar) {
/* 239 */     switch (paramChar) {
/*     */     case 'S': 
/*     */     case 's': 
/* 242 */       return 's';
/*     */     case 'G': case 'U': case 'Z': 
/*     */     case 'g': case 'u': case 'z': 
/* 245 */       return 'u';
/*     */     }
/*     */     
/* 248 */     return 'w';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static boolean test(String paramString, DateTimeZone paramDateTimeZone)
/*     */   {
/* 256 */     if (!paramString.equals(paramDateTimeZone.getID())) {
/* 257 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 262 */     long l1 = ISOChronology.getInstanceUTC().year().set(0L, 1850);
/* 263 */     long l2 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
/*     */     
/* 265 */     int i = paramDateTimeZone.getOffset(l1);
/* 266 */     Object localObject = paramDateTimeZone.getNameKey(l1);
/*     */     
/* 268 */     ArrayList localArrayList = new ArrayList();
/*     */     for (;;)
/*     */     {
/* 271 */       long l3 = paramDateTimeZone.nextTransition(l1);
/* 272 */       if ((l3 == l1) || (l3 > l2)) {
/*     */         break;
/*     */       }
/*     */       
/* 276 */       l1 = l3;
/*     */       
/* 278 */       int j = paramDateTimeZone.getOffset(l1);
/* 279 */       String str = paramDateTimeZone.getNameKey(l1);
/*     */       
/* 281 */       if ((i == j) && (((String)localObject).equals(str)))
/*     */       {
/* 283 */         System.out.println("*d* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()));
/*     */         
/*     */ 
/* 286 */         return false;
/*     */       }
/*     */       
/* 289 */       if ((str == null) || ((str.length() < 3) && (!"??".equals(str)))) {
/* 290 */         System.out.println("*s* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()) + ", nameKey=" + str);
/*     */         
/*     */ 
/*     */ 
/* 294 */         return false;
/*     */       }
/*     */       
/* 297 */       localArrayList.add(Long.valueOf(l1));
/*     */       
/* 299 */       i = j;
/* 300 */       localObject = str;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 305 */     l1 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
/* 306 */     l2 = ISOChronology.getInstanceUTC().year().set(0L, 1850);
/*     */     
/* 308 */     int k = localArrayList.size(); for (;;) { k--; if (k < 0) break;
/* 309 */       long l4 = paramDateTimeZone.previousTransition(l1);
/* 310 */       if ((l4 == l1) || (l4 < l2)) {
/*     */         break;
/*     */       }
/*     */       
/* 314 */       l1 = l4;
/*     */       
/* 316 */       long l5 = ((Long)localArrayList.get(k)).longValue();
/*     */       
/* 318 */       if (l5 - 1L != l1) {
/* 319 */         System.out.println("*r* Error in " + paramDateTimeZone.getID() + " " + new DateTime(l1, ISOChronology.getInstanceUTC()) + " != " + new DateTime(l5 - 1L, ISOChronology.getInstanceUTC()));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 325 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 329 */     return true;
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
/*     */   public ZoneInfoCompiler()
/*     */   {
/* 345 */     this.iRuleSets = new HashMap();
/* 346 */     this.iZones = new ArrayList();
/* 347 */     this.iGoodLinks = new ArrayList();
/* 348 */     this.iBackLinks = new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, DateTimeZone> compile(File paramFile, File[] paramArrayOfFile)
/*     */     throws IOException
/*     */   {
/* 358 */     if (paramArrayOfFile != null) {
/* 359 */       for (int i = 0; i < paramArrayOfFile.length; i++) {
/* 360 */         localObject1 = new BufferedReader(new FileReader(paramArrayOfFile[i]));
/* 361 */         parseDataFile((BufferedReader)localObject1, "backward".equals(paramArrayOfFile[i].getName()));
/* 362 */         ((BufferedReader)localObject1).close();
/*     */       }
/*     */     }
/*     */     
/* 366 */     if (paramFile != null) {
/* 367 */       if ((!paramFile.exists()) && 
/* 368 */         (!paramFile.mkdirs())) {
/* 369 */         throw new IOException("Destination directory doesn't exist and cannot be created: " + paramFile);
/*     */       }
/*     */       
/* 372 */       if (!paramFile.isDirectory()) {
/* 373 */         throw new IOException("Destination is not a directory: " + paramFile);
/*     */       }
/*     */     }
/*     */     
/* 377 */     TreeMap localTreeMap = new TreeMap();
/* 378 */     Object localObject1 = new TreeMap();
/*     */     
/* 380 */     System.out.println("Writing zoneinfo files");
/*     */     Object localObject2;
/* 382 */     Object localObject3; Object localObject4; for (int j = 0; j < this.iZones.size(); j++) {
/* 383 */       localObject2 = (Zone)this.iZones.get(j);
/* 384 */       localObject3 = new DateTimeZoneBuilder();
/* 385 */       ((Zone)localObject2).addToBuilder((DateTimeZoneBuilder)localObject3, this.iRuleSets);
/* 386 */       localObject4 = ((DateTimeZoneBuilder)localObject3).toDateTimeZone(((Zone)localObject2).iName, true);
/* 387 */       if (test(((DateTimeZone)localObject4).getID(), (DateTimeZone)localObject4)) {
/* 388 */         localTreeMap.put(((DateTimeZone)localObject4).getID(), localObject4);
/* 389 */         ((Map)localObject1).put(((DateTimeZone)localObject4).getID(), localObject2);
/* 390 */         if (paramFile != null) {
/* 391 */           writeZone(paramFile, (DateTimeZoneBuilder)localObject3, (DateTimeZone)localObject4);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     Object localObject5;
/* 397 */     for (j = 0; j < this.iGoodLinks.size(); j += 2) {
/* 398 */       localObject2 = (String)this.iGoodLinks.get(j);
/* 399 */       localObject3 = (String)this.iGoodLinks.get(j + 1);
/* 400 */       localObject4 = (Zone)((Map)localObject1).get(localObject2);
/* 401 */       if (localObject4 == null) {
/* 402 */         System.out.println("Cannot find source zone '" + (String)localObject2 + "' to link alias '" + (String)localObject3 + "' to");
/*     */       } else {
/* 404 */         localObject5 = new DateTimeZoneBuilder();
/* 405 */         ((Zone)localObject4).addToBuilder((DateTimeZoneBuilder)localObject5, this.iRuleSets);
/* 406 */         DateTimeZone localDateTimeZone = ((DateTimeZoneBuilder)localObject5).toDateTimeZone((String)localObject3, true);
/* 407 */         if (test(localDateTimeZone.getID(), localDateTimeZone)) {
/* 408 */           localTreeMap.put(localDateTimeZone.getID(), localDateTimeZone);
/* 409 */           if (paramFile != null) {
/* 410 */             writeZone(paramFile, (DateTimeZoneBuilder)localObject5, localDateTimeZone);
/*     */           }
/*     */         }
/* 413 */         localTreeMap.put(localDateTimeZone.getID(), localDateTimeZone);
/* 414 */         if (ZoneInfoLogger.verbose()) {
/* 415 */           System.out.println("Good link: " + (String)localObject3 + " -> " + (String)localObject2 + " revived");
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 421 */     for (j = 0; j < 2; j++) {
/* 422 */       for (int k = 0; k < this.iBackLinks.size(); k += 2) {
/* 423 */         localObject3 = (String)this.iBackLinks.get(k);
/* 424 */         localObject4 = (String)this.iBackLinks.get(k + 1);
/* 425 */         localObject5 = (DateTimeZone)localTreeMap.get(localObject3);
/* 426 */         if (localObject5 == null) {
/* 427 */           if (j > 0) {
/* 428 */             System.out.println("Cannot find time zone '" + (String)localObject3 + "' to link alias '" + (String)localObject4 + "' to");
/*     */           }
/*     */         } else {
/* 431 */           localTreeMap.put(localObject4, localObject5);
/* 432 */           if (ZoneInfoLogger.verbose()) {
/* 433 */             System.out.println("Back link: " + (String)localObject4 + " -> " + ((DateTimeZone)localObject5).getID());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 440 */     if (paramFile != null) {
/* 441 */       System.out.println("Writing ZoneInfoMap");
/* 442 */       File localFile = new File(paramFile, "ZoneInfoMap");
/* 443 */       if (!localFile.getParentFile().exists()) {
/* 444 */         localFile.getParentFile().mkdirs();
/*     */       }
/*     */       
/* 447 */       FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
/* 448 */       localObject3 = new DataOutputStream(localFileOutputStream);
/*     */       try
/*     */       {
/* 451 */         localObject4 = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 452 */         ((Map)localObject4).putAll(localTreeMap);
/* 453 */         writeZoneInfoMap((DataOutputStream)localObject3, (Map)localObject4);
/*     */       } finally {
/* 455 */         ((DataOutputStream)localObject3).close();
/*     */       }
/*     */     }
/*     */     
/* 459 */     return localTreeMap;
/*     */   }
/*     */   
/*     */   private void writeZone(File paramFile, DateTimeZoneBuilder paramDateTimeZoneBuilder, DateTimeZone paramDateTimeZone) throws IOException {
/* 463 */     if (ZoneInfoLogger.verbose()) {
/* 464 */       System.out.println("Writing " + paramDateTimeZone.getID());
/*     */     }
/* 466 */     File localFile = new File(paramFile, paramDateTimeZone.getID());
/* 467 */     if (!localFile.getParentFile().exists()) {
/* 468 */       localFile.getParentFile().mkdirs();
/*     */     }
/* 470 */     FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
/*     */     try {
/* 472 */       paramDateTimeZoneBuilder.writeTo(paramDateTimeZone.getID(), localFileOutputStream);
/*     */     } finally {
/* 474 */       localFileOutputStream.close();
/*     */     }
/*     */     
/*     */ 
/* 478 */     FileInputStream localFileInputStream = new FileInputStream(localFile);
/* 479 */     DateTimeZone localDateTimeZone = DateTimeZoneBuilder.readFrom(localFileInputStream, paramDateTimeZone.getID());
/* 480 */     localFileInputStream.close();
/*     */     
/* 482 */     if (!paramDateTimeZone.equals(localDateTimeZone)) {
/* 483 */       System.out.println("*e* Error in " + paramDateTimeZone.getID() + ": Didn't read properly from file");
/*     */     }
/*     */   }
/*     */   
/*     */   public void parseDataFile(BufferedReader paramBufferedReader, boolean paramBoolean) throws IOException
/*     */   {
/* 489 */     Object localObject1 = null;
/*     */     String str1;
/* 491 */     while ((str1 = paramBufferedReader.readLine()) != null) {
/* 492 */       String str2 = str1.trim();
/* 493 */       if ((str2.length() != 0) && (str2.charAt(0) != '#'))
/*     */       {
/*     */ 
/*     */ 
/* 497 */         int i = str1.indexOf('#');
/* 498 */         if (i >= 0) {
/* 499 */           str1 = str1.substring(0, i);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 504 */         StringTokenizer localStringTokenizer = new StringTokenizer(str1, " \t");
/*     */         
/* 506 */         if ((Character.isWhitespace(str1.charAt(0))) && (localStringTokenizer.hasMoreTokens())) {
/* 507 */           if (localObject1 != null)
/*     */           {
/* 509 */             ((Zone)localObject1).chain(localStringTokenizer);
/*     */           }
/*     */         }
/*     */         else {
/* 513 */           if (localObject1 != null) {
/* 514 */             this.iZones.add(localObject1);
/*     */           }
/* 516 */           localObject1 = null;
/*     */           
/*     */ 
/* 519 */           if (localStringTokenizer.hasMoreTokens()) {
/* 520 */             String str3 = localStringTokenizer.nextToken();
/* 521 */             Object localObject2; Object localObject3; if (str3.equalsIgnoreCase("Rule")) {
/* 522 */               localObject2 = new Rule(localStringTokenizer);
/* 523 */               localObject3 = (RuleSet)this.iRuleSets.get(((Rule)localObject2).iName);
/* 524 */               if (localObject3 == null) {
/* 525 */                 localObject3 = new RuleSet((Rule)localObject2);
/* 526 */                 this.iRuleSets.put(((Rule)localObject2).iName, localObject3);
/*     */               } else {
/* 528 */                 ((RuleSet)localObject3).addRule((Rule)localObject2);
/*     */               }
/* 530 */             } else if (str3.equalsIgnoreCase("Zone")) {
/* 531 */               localObject1 = new Zone(localStringTokenizer);
/* 532 */             } else if (str3.equalsIgnoreCase("Link")) {
/* 533 */               localObject2 = localStringTokenizer.nextToken();
/* 534 */               localObject3 = localStringTokenizer.nextToken();
/*     */               
/*     */ 
/*     */ 
/* 538 */               if ((paramBoolean) || (((String)localObject3).equals("US/Pacific-New")) || (((String)localObject3).startsWith("Etc/")) || (((String)localObject3).equals("GMT"))) {
/* 539 */                 this.iBackLinks.add(localObject2);
/* 540 */                 this.iBackLinks.add(localObject3);
/*     */               } else {
/* 542 */                 this.iGoodLinks.add(localObject2);
/* 543 */                 this.iGoodLinks.add(localObject3);
/*     */               }
/*     */             } else {
/* 546 */               System.out.println("Unknown line: " + str1);
/*     */             }
/*     */           }
/*     */         }
/*     */       } }
/* 551 */     if (localObject1 != null) {
/* 552 */       this.iZones.add(localObject1);
/*     */     }
/*     */   }
/*     */   
/*     */   static class DateTimeOfYear {
/*     */     public final int iMonthOfYear;
/*     */     public final int iDayOfMonth;
/*     */     public final int iDayOfWeek;
/*     */     public final boolean iAdvanceDayOfWeek;
/*     */     public final int iMillisOfDay;
/*     */     public final char iZoneChar;
/*     */     
/*     */     DateTimeOfYear() {
/* 565 */       this.iMonthOfYear = 1;
/* 566 */       this.iDayOfMonth = 1;
/* 567 */       this.iDayOfWeek = 0;
/* 568 */       this.iAdvanceDayOfWeek = false;
/* 569 */       this.iMillisOfDay = 0;
/* 570 */       this.iZoneChar = 'w';
/*     */     }
/*     */     
/*     */     DateTimeOfYear(StringTokenizer paramStringTokenizer) {
/* 574 */       int i = 1;
/* 575 */       int j = 1;
/* 576 */       int k = 0;
/* 577 */       int m = 0;
/* 578 */       boolean bool = false;
/* 579 */       char c = 'w';
/*     */       
/* 581 */       if (paramStringTokenizer.hasMoreTokens()) {
/* 582 */         i = ZoneInfoCompiler.parseMonth(paramStringTokenizer.nextToken());
/*     */         
/* 584 */         if (paramStringTokenizer.hasMoreTokens()) {
/* 585 */           String str = paramStringTokenizer.nextToken();
/* 586 */           if (str.startsWith("last")) {
/* 587 */             j = -1;
/* 588 */             k = ZoneInfoCompiler.parseDayOfWeek(str.substring(4));
/* 589 */             bool = false;
/*     */           } else {
/*     */             try {
/* 592 */               j = Integer.parseInt(str);
/* 593 */               k = 0;
/* 594 */               bool = false;
/*     */             } catch (NumberFormatException localNumberFormatException) {
/* 596 */               int n = str.indexOf(">=");
/* 597 */               if (n > 0) {
/* 598 */                 j = Integer.parseInt(str.substring(n + 2));
/* 599 */                 k = ZoneInfoCompiler.parseDayOfWeek(str.substring(0, n));
/* 600 */                 bool = true;
/*     */               } else {
/* 602 */                 n = str.indexOf("<=");
/* 603 */                 if (n > 0) {
/* 604 */                   j = Integer.parseInt(str.substring(n + 2));
/* 605 */                   k = ZoneInfoCompiler.parseDayOfWeek(str.substring(0, n));
/* 606 */                   bool = false;
/*     */                 } else {
/* 608 */                   throw new IllegalArgumentException(str);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 614 */           if (paramStringTokenizer.hasMoreTokens()) {
/* 615 */             str = paramStringTokenizer.nextToken();
/* 616 */             c = ZoneInfoCompiler.parseZoneChar(str.charAt(str.length() - 1));
/* 617 */             if (str.equals("24:00"))
/*     */             {
/* 619 */               if ((i == 12) && (j == 31)) {
/* 620 */                 m = ZoneInfoCompiler.parseTime("23:59:59.999");
/*     */               } else {
/* 622 */                 LocalDate localLocalDate = j == -1 ? new LocalDate(2001, i, 1).plusMonths(1) : new LocalDate(2001, i, j).plusDays(1);
/*     */                 
/*     */ 
/* 625 */                 bool = (j != -1) && (k != 0);
/* 626 */                 i = localLocalDate.getMonthOfYear();
/* 627 */                 j = localLocalDate.getDayOfMonth();
/* 628 */                 if (k != 0) {
/* 629 */                   k = (k - 1 + 1) % 7 + 1;
/*     */                 }
/*     */               }
/*     */             } else {
/* 633 */               m = ZoneInfoCompiler.parseTime(str);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 639 */       this.iMonthOfYear = i;
/* 640 */       this.iDayOfMonth = j;
/* 641 */       this.iDayOfWeek = k;
/* 642 */       this.iAdvanceDayOfWeek = bool;
/* 643 */       this.iMillisOfDay = m;
/* 644 */       this.iZoneChar = c;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addRecurring(DateTimeZoneBuilder paramDateTimeZoneBuilder, String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */     {
/* 653 */       paramDateTimeZoneBuilder.addRecurringSavings(paramString, paramInt1, paramInt2, paramInt3, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addCutover(DateTimeZoneBuilder paramDateTimeZoneBuilder, int paramInt)
/*     */     {
/* 667 */       paramDateTimeZoneBuilder.addCutover(paramInt, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 677 */       return "MonthOfYear: " + this.iMonthOfYear + "\n" + "DayOfMonth: " + this.iDayOfMonth + "\n" + "DayOfWeek: " + this.iDayOfWeek + "\n" + "AdvanceDayOfWeek: " + this.iAdvanceDayOfWeek + "\n" + "MillisOfDay: " + this.iMillisOfDay + "\n" + "ZoneChar: " + this.iZoneChar + "\n";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class Rule
/*     */   {
/*     */     public final String iName;
/*     */     
/*     */     public final int iFromYear;
/*     */     
/*     */     public final int iToYear;
/*     */     
/*     */     public final String iType;
/*     */     public final DateTimeOfYear iDateTimeOfYear;
/*     */     public final int iSaveMillis;
/*     */     public final String iLetterS;
/*     */     
/*     */     Rule(StringTokenizer paramStringTokenizer)
/*     */     {
/* 697 */       this.iName = paramStringTokenizer.nextToken().intern();
/* 698 */       this.iFromYear = ZoneInfoCompiler.parseYear(paramStringTokenizer.nextToken(), 0);
/* 699 */       this.iToYear = ZoneInfoCompiler.parseYear(paramStringTokenizer.nextToken(), this.iFromYear);
/* 700 */       if (this.iToYear < this.iFromYear) {
/* 701 */         throw new IllegalArgumentException();
/*     */       }
/* 703 */       this.iType = ZoneInfoCompiler.parseOptional(paramStringTokenizer.nextToken());
/* 704 */       this.iDateTimeOfYear = new DateTimeOfYear(paramStringTokenizer);
/* 705 */       this.iSaveMillis = ZoneInfoCompiler.parseTime(paramStringTokenizer.nextToken());
/* 706 */       this.iLetterS = ZoneInfoCompiler.parseOptional(paramStringTokenizer.nextToken());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void addRecurring(DateTimeZoneBuilder paramDateTimeZoneBuilder, String paramString)
/*     */     {
/* 713 */       String str = formatName(paramString);
/* 714 */       this.iDateTimeOfYear.addRecurring(paramDateTimeZoneBuilder, str, this.iSaveMillis, this.iFromYear, this.iToYear);
/*     */     }
/*     */     
/*     */     private String formatName(String paramString)
/*     */     {
/* 719 */       int i = paramString.indexOf('/');
/* 720 */       if (i > 0) {
/* 721 */         if (this.iSaveMillis == 0)
/*     */         {
/* 723 */           return paramString.substring(0, i).intern();
/*     */         }
/* 725 */         return paramString.substring(i + 1).intern();
/*     */       }
/*     */       
/* 728 */       i = paramString.indexOf("%s");
/* 729 */       if (i < 0) {
/* 730 */         return paramString;
/*     */       }
/* 732 */       String str1 = paramString.substring(0, i);
/* 733 */       String str2 = paramString.substring(i + 2);
/*     */       String str3;
/* 735 */       if (this.iLetterS == null) {
/* 736 */         str3 = str1.concat(str2);
/*     */       } else {
/* 738 */         str3 = str1 + this.iLetterS + str2;
/*     */       }
/* 740 */       return str3.intern();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 744 */       return "[Rule]\nName: " + this.iName + "\n" + "FromYear: " + this.iFromYear + "\n" + "ToYear: " + this.iToYear + "\n" + "Type: " + this.iType + "\n" + this.iDateTimeOfYear + "SaveMillis: " + this.iSaveMillis + "\n" + "LetterS: " + this.iLetterS + "\n";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class RuleSet
/*     */   {
/*     */     private List<Rule> iRules;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     RuleSet(Rule paramRule)
/*     */     {
/* 760 */       this.iRules = new ArrayList();
/* 761 */       this.iRules.add(paramRule);
/*     */     }
/*     */     
/*     */     void addRule(Rule paramRule) {
/* 765 */       if (!paramRule.iName.equals(((Rule)this.iRules.get(0)).iName)) {
/* 766 */         throw new IllegalArgumentException("Rule name mismatch");
/*     */       }
/* 768 */       this.iRules.add(paramRule);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void addRecurring(DateTimeZoneBuilder paramDateTimeZoneBuilder, String paramString)
/*     */     {
/* 775 */       for (int i = 0; i < this.iRules.size(); i++) {
/* 776 */         Rule localRule = (Rule)this.iRules.get(i);
/* 777 */         localRule.addRecurring(paramDateTimeZoneBuilder, paramString);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Zone
/*     */   {
/*     */     public final String iName;
/*     */     public final int iOffsetMillis;
/*     */     public final String iRules;
/*     */     public final String iFormat;
/*     */     public final int iUntilYear;
/*     */     public final DateTimeOfYear iUntilDateTimeOfYear;
/*     */     private Zone iNext;
/*     */     
/*     */     Zone(StringTokenizer paramStringTokenizer) {
/* 793 */       this(paramStringTokenizer.nextToken(), paramStringTokenizer);
/*     */     }
/*     */     
/*     */     private Zone(String paramString, StringTokenizer paramStringTokenizer) {
/* 797 */       this.iName = paramString.intern();
/* 798 */       this.iOffsetMillis = ZoneInfoCompiler.parseTime(paramStringTokenizer.nextToken());
/* 799 */       this.iRules = ZoneInfoCompiler.parseOptional(paramStringTokenizer.nextToken());
/* 800 */       this.iFormat = paramStringTokenizer.nextToken().intern();
/*     */       
/* 802 */       int i = Integer.MAX_VALUE;
/* 803 */       DateTimeOfYear localDateTimeOfYear = ZoneInfoCompiler.getStartOfYear();
/*     */       
/* 805 */       if (paramStringTokenizer.hasMoreTokens()) {
/* 806 */         i = Integer.parseInt(paramStringTokenizer.nextToken());
/* 807 */         if (paramStringTokenizer.hasMoreTokens()) {
/* 808 */           localDateTimeOfYear = new DateTimeOfYear(paramStringTokenizer);
/*     */         }
/*     */       }
/*     */       
/* 812 */       this.iUntilYear = i;
/* 813 */       this.iUntilDateTimeOfYear = localDateTimeOfYear;
/*     */     }
/*     */     
/*     */     void chain(StringTokenizer paramStringTokenizer) {
/* 817 */       if (this.iNext != null) {
/* 818 */         this.iNext.chain(paramStringTokenizer);
/*     */       } else {
/* 820 */         this.iNext = new Zone(this.iName, paramStringTokenizer);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addToBuilder(DateTimeZoneBuilder paramDateTimeZoneBuilder, Map<String, RuleSet> paramMap)
/*     */     {
/* 836 */       addToBuilder(this, paramDateTimeZoneBuilder, paramMap);
/*     */     }
/*     */     
/*     */     private static void addToBuilder(Zone paramZone, DateTimeZoneBuilder paramDateTimeZoneBuilder, Map<String, RuleSet> paramMap)
/*     */     {
/* 843 */       for (; 
/*     */           
/* 843 */           paramZone != null; paramZone = paramZone.iNext) {
/* 844 */         paramDateTimeZoneBuilder.setStandardOffset(paramZone.iOffsetMillis);
/*     */         
/* 846 */         if (paramZone.iRules == null) {
/* 847 */           paramDateTimeZoneBuilder.setFixedSavings(paramZone.iFormat, 0);
/*     */         } else {
/*     */           try
/*     */           {
/* 851 */             int i = ZoneInfoCompiler.parseTime(paramZone.iRules);
/* 852 */             paramDateTimeZoneBuilder.setFixedSavings(paramZone.iFormat, i);
/*     */           }
/*     */           catch (Exception localException) {
/* 855 */             RuleSet localRuleSet = (RuleSet)paramMap.get(paramZone.iRules);
/* 856 */             if (localRuleSet == null) {
/* 857 */               throw new IllegalArgumentException("Rules not found: " + paramZone.iRules);
/*     */             }
/*     */             
/* 860 */             localRuleSet.addRecurring(paramDateTimeZoneBuilder, paramZone.iFormat);
/*     */           }
/*     */         }
/*     */         
/* 864 */         if (paramZone.iUntilYear == Integer.MAX_VALUE) {
/*     */           break;
/*     */         }
/*     */         
/* 868 */         paramZone.iUntilDateTimeOfYear.addCutover(paramDateTimeZoneBuilder, paramZone.iUntilYear);
/*     */       }
/*     */     }
/*     */     
/*     */     public String toString() {
/* 873 */       String str = "[Zone]\nName: " + this.iName + "\n" + "OffsetMillis: " + this.iOffsetMillis + "\n" + "Rules: " + this.iRules + "\n" + "Format: " + this.iFormat + "\n" + "UntilYear: " + this.iUntilYear + "\n" + this.iUntilDateTimeOfYear;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 882 */       if (this.iNext == null) {
/* 883 */         return str;
/*     */       }
/*     */       
/* 886 */       return str + "...\n" + this.iNext.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\ZoneInfoCompiler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */