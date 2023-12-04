/*      */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ISODateTimeFormat
/*      */ {
/*      */   public static DateTimeFormatter forFields(Collection<DateTimeFieldType> paramCollection, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  156 */     if ((paramCollection == null) || (paramCollection.size() == 0)) {
/*  157 */       throw new IllegalArgumentException("The fields must not be null or empty");
/*      */     }
/*  159 */     HashSet localHashSet = new HashSet(paramCollection);
/*  160 */     int i = localHashSet.size();
/*  161 */     boolean bool1 = false;
/*  162 */     DateTimeFormatterBuilder localDateTimeFormatterBuilder = new DateTimeFormatterBuilder();
/*      */     
/*  164 */     if (localHashSet.contains(DateTimeFieldType.monthOfYear())) {
/*  165 */       bool1 = dateByMonth(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2);
/*  166 */     } else if (localHashSet.contains(DateTimeFieldType.dayOfYear())) {
/*  167 */       bool1 = dateByOrdinal(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2);
/*  168 */     } else if (localHashSet.contains(DateTimeFieldType.weekOfWeekyear())) {
/*  169 */       bool1 = dateByWeek(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2);
/*  170 */     } else if (localHashSet.contains(DateTimeFieldType.dayOfMonth())) {
/*  171 */       bool1 = dateByMonth(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2);
/*  172 */     } else if (localHashSet.contains(DateTimeFieldType.dayOfWeek())) {
/*  173 */       bool1 = dateByWeek(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2);
/*  174 */     } else if (localHashSet.remove(DateTimeFieldType.year())) {
/*  175 */       localDateTimeFormatterBuilder.append(Constants.ye);
/*  176 */       bool1 = true;
/*  177 */     } else if (localHashSet.remove(DateTimeFieldType.weekyear())) {
/*  178 */       localDateTimeFormatterBuilder.append(Constants.we);
/*  179 */       bool1 = true;
/*      */     }
/*  181 */     boolean bool2 = localHashSet.size() < i;
/*      */     
/*      */ 
/*  184 */     time(localDateTimeFormatterBuilder, localHashSet, paramBoolean1, paramBoolean2, bool1, bool2);
/*      */     
/*      */ 
/*  187 */     if (!localDateTimeFormatterBuilder.canBuildFormatter()) {
/*  188 */       throw new IllegalArgumentException("No valid format for fields: " + paramCollection);
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  194 */       paramCollection.retainAll(localHashSet);
/*      */     }
/*      */     catch (UnsupportedOperationException localUnsupportedOperationException) {}
/*      */     
/*  198 */     return localDateTimeFormatterBuilder.toFormatter();
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
/*      */   private static boolean dateByMonth(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, Collection<DateTimeFieldType> paramCollection, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  219 */     boolean bool = false;
/*  220 */     if (paramCollection.remove(DateTimeFieldType.year())) {
/*  221 */       paramDateTimeFormatterBuilder.append(Constants.ye);
/*  222 */       if (paramCollection.remove(DateTimeFieldType.monthOfYear())) {
/*  223 */         if (paramCollection.remove(DateTimeFieldType.dayOfMonth()))
/*      */         {
/*  225 */           appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  226 */           paramDateTimeFormatterBuilder.appendMonthOfYear(2);
/*  227 */           appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  228 */           paramDateTimeFormatterBuilder.appendDayOfMonth(2);
/*      */         }
/*      */         else {
/*  231 */           paramDateTimeFormatterBuilder.appendLiteral('-');
/*  232 */           paramDateTimeFormatterBuilder.appendMonthOfYear(2);
/*  233 */           bool = true;
/*      */         }
/*      */       }
/*  236 */       else if (paramCollection.remove(DateTimeFieldType.dayOfMonth()))
/*      */       {
/*  238 */         checkNotStrictISO(paramCollection, paramBoolean2);
/*  239 */         paramDateTimeFormatterBuilder.appendLiteral('-');
/*  240 */         paramDateTimeFormatterBuilder.appendLiteral('-');
/*  241 */         paramDateTimeFormatterBuilder.appendDayOfMonth(2);
/*      */       }
/*      */       else {
/*  244 */         bool = true;
/*      */       }
/*      */       
/*      */     }
/*  248 */     else if (paramCollection.remove(DateTimeFieldType.monthOfYear())) {
/*  249 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  250 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  251 */       paramDateTimeFormatterBuilder.appendMonthOfYear(2);
/*  252 */       if (paramCollection.remove(DateTimeFieldType.dayOfMonth()))
/*      */       {
/*  254 */         appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  255 */         paramDateTimeFormatterBuilder.appendDayOfMonth(2);
/*      */       }
/*      */       else {
/*  258 */         bool = true;
/*      */       }
/*  260 */     } else if (paramCollection.remove(DateTimeFieldType.dayOfMonth()))
/*      */     {
/*  262 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  263 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  264 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  265 */       paramDateTimeFormatterBuilder.appendDayOfMonth(2);
/*      */     }
/*  267 */     return bool;
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
/*      */   private static boolean dateByOrdinal(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, Collection<DateTimeFieldType> paramCollection, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  287 */     boolean bool = false;
/*  288 */     if (paramCollection.remove(DateTimeFieldType.year())) {
/*  289 */       paramDateTimeFormatterBuilder.append(Constants.ye);
/*  290 */       if (paramCollection.remove(DateTimeFieldType.dayOfYear()))
/*      */       {
/*  292 */         appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  293 */         paramDateTimeFormatterBuilder.appendDayOfYear(3);
/*      */       }
/*      */       else {
/*  296 */         bool = true;
/*      */       }
/*      */     }
/*  299 */     else if (paramCollection.remove(DateTimeFieldType.dayOfYear()))
/*      */     {
/*  301 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  302 */       paramDateTimeFormatterBuilder.appendDayOfYear(3);
/*      */     }
/*  304 */     return bool;
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
/*      */   private static boolean dateByWeek(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, Collection<DateTimeFieldType> paramCollection, boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  324 */     boolean bool = false;
/*  325 */     if (paramCollection.remove(DateTimeFieldType.weekyear())) {
/*  326 */       paramDateTimeFormatterBuilder.append(Constants.we);
/*  327 */       if (paramCollection.remove(DateTimeFieldType.weekOfWeekyear())) {
/*  328 */         appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  329 */         paramDateTimeFormatterBuilder.appendLiteral('W');
/*  330 */         paramDateTimeFormatterBuilder.appendWeekOfWeekyear(2);
/*  331 */         if (paramCollection.remove(DateTimeFieldType.dayOfWeek()))
/*      */         {
/*  333 */           appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  334 */           paramDateTimeFormatterBuilder.appendDayOfWeek(1);
/*      */         }
/*      */         else {
/*  337 */           bool = true;
/*      */         }
/*      */       }
/*  340 */       else if (paramCollection.remove(DateTimeFieldType.dayOfWeek()))
/*      */       {
/*  342 */         checkNotStrictISO(paramCollection, paramBoolean2);
/*  343 */         appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  344 */         paramDateTimeFormatterBuilder.appendLiteral('W');
/*  345 */         paramDateTimeFormatterBuilder.appendLiteral('-');
/*  346 */         paramDateTimeFormatterBuilder.appendDayOfWeek(1);
/*      */       }
/*      */       else {
/*  349 */         bool = true;
/*      */       }
/*      */       
/*      */     }
/*  353 */     else if (paramCollection.remove(DateTimeFieldType.weekOfWeekyear())) {
/*  354 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  355 */       paramDateTimeFormatterBuilder.appendLiteral('W');
/*  356 */       paramDateTimeFormatterBuilder.appendWeekOfWeekyear(2);
/*  357 */       if (paramCollection.remove(DateTimeFieldType.dayOfWeek()))
/*      */       {
/*  359 */         appendSeparator(paramDateTimeFormatterBuilder, paramBoolean1);
/*  360 */         paramDateTimeFormatterBuilder.appendDayOfWeek(1);
/*      */       }
/*      */       else {
/*  363 */         bool = true;
/*      */       }
/*  365 */     } else if (paramCollection.remove(DateTimeFieldType.dayOfWeek()))
/*      */     {
/*  367 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  368 */       paramDateTimeFormatterBuilder.appendLiteral('W');
/*  369 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*  370 */       paramDateTimeFormatterBuilder.appendDayOfWeek(1);
/*      */     }
/*  372 */     return bool;
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
/*      */   private static void time(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, Collection<DateTimeFieldType> paramCollection, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*      */   {
/*  396 */     boolean bool1 = paramCollection.remove(DateTimeFieldType.hourOfDay());
/*  397 */     boolean bool2 = paramCollection.remove(DateTimeFieldType.minuteOfHour());
/*  398 */     boolean bool3 = paramCollection.remove(DateTimeFieldType.secondOfMinute());
/*  399 */     boolean bool4 = paramCollection.remove(DateTimeFieldType.millisOfSecond());
/*  400 */     if ((!bool1) && (!bool2) && (!bool3) && (!bool4)) {
/*  401 */       return;
/*      */     }
/*  403 */     if ((bool1) || (bool2) || (bool3) || (bool4)) {
/*  404 */       if ((paramBoolean2) && (paramBoolean3)) {
/*  405 */         throw new IllegalArgumentException("No valid ISO8601 format for fields because Date was reduced precision: " + paramCollection);
/*      */       }
/*  407 */       if (paramBoolean4) {
/*  408 */         paramDateTimeFormatterBuilder.appendLiteral('T');
/*      */       }
/*      */     }
/*  411 */     if (((!bool1) || (!bool2) || (!bool3)) && ((!bool1) || (bool3) || (bool4)))
/*      */     {
/*      */ 
/*  414 */       if ((paramBoolean2) && (paramBoolean4)) {
/*  415 */         throw new IllegalArgumentException("No valid ISO8601 format for fields because Time was truncated: " + paramCollection);
/*      */       }
/*  417 */       if ((bool1) || (((!bool2) || (!bool3)) && ((!bool2) || (bool4)) && (!bool3)))
/*      */       {
/*      */ 
/*  420 */         if (paramBoolean2) {
/*  421 */           throw new IllegalArgumentException("No valid ISO8601 format for fields: " + paramCollection);
/*      */         }
/*      */       }
/*      */     }
/*  425 */     if (bool1) {
/*  426 */       paramDateTimeFormatterBuilder.appendHourOfDay(2);
/*  427 */     } else if ((bool2) || (bool3) || (bool4)) {
/*  428 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*      */     }
/*  430 */     if ((paramBoolean1) && (bool1) && (bool2)) {
/*  431 */       paramDateTimeFormatterBuilder.appendLiteral(':');
/*      */     }
/*  433 */     if (bool2) {
/*  434 */       paramDateTimeFormatterBuilder.appendMinuteOfHour(2);
/*  435 */     } else if ((bool3) || (bool4)) {
/*  436 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*      */     }
/*  438 */     if ((paramBoolean1) && (bool2) && (bool3)) {
/*  439 */       paramDateTimeFormatterBuilder.appendLiteral(':');
/*      */     }
/*  441 */     if (bool3) {
/*  442 */       paramDateTimeFormatterBuilder.appendSecondOfMinute(2);
/*  443 */     } else if (bool4) {
/*  444 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*      */     }
/*  446 */     if (bool4) {
/*  447 */       paramDateTimeFormatterBuilder.appendLiteral('.');
/*  448 */       paramDateTimeFormatterBuilder.appendMillisOfSecond(3);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void checkNotStrictISO(Collection<DateTimeFieldType> paramCollection, boolean paramBoolean)
/*      */   {
/*  461 */     if (paramBoolean) {
/*  462 */       throw new IllegalArgumentException("No valid ISO8601 format for fields: " + paramCollection);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void appendSeparator(DateTimeFormatterBuilder paramDateTimeFormatterBuilder, boolean paramBoolean)
/*      */   {
/*  475 */     if (paramBoolean) {
/*  476 */       paramDateTimeFormatterBuilder.appendLiteral('-');
/*      */     }
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
/*      */   public static DateTimeFormatter dateParser()
/*      */   {
/*  497 */     return Constants.dp;
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
/*      */   public static DateTimeFormatter localDateParser()
/*      */   {
/*  517 */     return Constants.ldp;
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
/*      */   public static DateTimeFormatter dateElementParser()
/*      */   {
/*  534 */     return Constants.dpe;
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
/*      */   public static DateTimeFormatter timeParser()
/*      */   {
/*  555 */     return Constants.tp;
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
/*      */   public static DateTimeFormatter localTimeParser()
/*      */   {
/*  577 */     return Constants.ltp;
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
/*      */   public static DateTimeFormatter timeElementParser()
/*      */   {
/*  596 */     return Constants.tpe;
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
/*      */   public static DateTimeFormatter dateTimeParser()
/*      */   {
/*  623 */     return Constants.dtp;
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
/*      */   public static DateTimeFormatter dateOptionalTimeParser()
/*      */   {
/*  649 */     return Constants.dotp;
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
/*      */   public static DateTimeFormatter localDateOptionalTimeParser()
/*      */   {
/*  676 */     return Constants.ldotp;
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
/*      */   public static DateTimeFormatter date()
/*      */   {
/*  690 */     return yearMonthDay();
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
/*      */   public static DateTimeFormatter time()
/*      */   {
/*  707 */     return Constants.t;
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
/*      */   public static DateTimeFormatter timeNoMillis()
/*      */   {
/*  723 */     return Constants.tx;
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
/*      */   public static DateTimeFormatter tTime()
/*      */   {
/*  740 */     return Constants.tt;
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
/*      */   public static DateTimeFormatter tTimeNoMillis()
/*      */   {
/*  757 */     return Constants.ttx;
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
/*      */   public static DateTimeFormatter dateTime()
/*      */   {
/*  773 */     return Constants.dt;
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
/*      */   public static DateTimeFormatter dateTimeNoMillis()
/*      */   {
/*  789 */     return Constants.dtx;
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
/*      */   public static DateTimeFormatter ordinalDate()
/*      */   {
/*  803 */     return Constants.od;
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
/*      */   public static DateTimeFormatter ordinalDateTime()
/*      */   {
/*  820 */     return Constants.odt;
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
/*      */   public static DateTimeFormatter ordinalDateTimeNoMillis()
/*      */   {
/*  837 */     return Constants.odtx;
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
/*      */   public static DateTimeFormatter weekDate()
/*      */   {
/*  850 */     return Constants.wwd;
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
/*      */   public static DateTimeFormatter weekDateTime()
/*      */   {
/*  866 */     return Constants.wdt;
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
/*      */   public static DateTimeFormatter weekDateTimeNoMillis()
/*      */   {
/*  882 */     return Constants.wdtx;
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
/*      */   public static DateTimeFormatter basicDate()
/*      */   {
/*  895 */     return Constants.bd;
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
/*      */   public static DateTimeFormatter basicTime()
/*      */   {
/*  911 */     return Constants.bt;
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
/*      */   public static DateTimeFormatter basicTimeNoMillis()
/*      */   {
/*  926 */     return Constants.btx;
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
/*      */   public static DateTimeFormatter basicTTime()
/*      */   {
/*  942 */     return Constants.btt;
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
/*      */   public static DateTimeFormatter basicTTimeNoMillis()
/*      */   {
/*  958 */     return Constants.bttx;
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
/*      */   public static DateTimeFormatter basicDateTime()
/*      */   {
/*  973 */     return Constants.bdt;
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
/*      */   public static DateTimeFormatter basicDateTimeNoMillis()
/*      */   {
/*  988 */     return Constants.bdtx;
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
/*      */   public static DateTimeFormatter basicOrdinalDate()
/*      */   {
/* 1001 */     return Constants.bod;
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
/*      */   public static DateTimeFormatter basicOrdinalDateTime()
/*      */   {
/* 1017 */     return Constants.bodt;
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
/*      */   public static DateTimeFormatter basicOrdinalDateTimeNoMillis()
/*      */   {
/* 1033 */     return Constants.bodtx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter basicWeekDate()
/*      */   {
/* 1045 */     return Constants.bwd;
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
/*      */   public static DateTimeFormatter basicWeekDateTime()
/*      */   {
/* 1060 */     return Constants.bwdt;
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
/*      */   public static DateTimeFormatter basicWeekDateTimeNoMillis()
/*      */   {
/* 1075 */     return Constants.bwdtx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter year()
/*      */   {
/* 1085 */     return Constants.ye;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter yearMonth()
/*      */   {
/* 1095 */     return Constants.ym;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter yearMonthDay()
/*      */   {
/* 1105 */     return Constants.ymd;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter weekyear()
/*      */   {
/* 1114 */     return Constants.we;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter weekyearWeek()
/*      */   {
/* 1124 */     return Constants.ww;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter weekyearWeekDay()
/*      */   {
/* 1134 */     return Constants.wwd;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter hour()
/*      */   {
/* 1143 */     return Constants.hde;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter hourMinute()
/*      */   {
/* 1153 */     return Constants.hm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter hourMinuteSecond()
/*      */   {
/* 1163 */     return Constants.hms;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter hourMinuteSecondMillis()
/*      */   {
/* 1175 */     return Constants.hmsl;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter hourMinuteSecondFraction()
/*      */   {
/* 1187 */     return Constants.hmsf;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter dateHour()
/*      */   {
/* 1197 */     return Constants.dh;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter dateHourMinute()
/*      */   {
/* 1207 */     return Constants.dhm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter dateHourMinuteSecond()
/*      */   {
/* 1218 */     return Constants.dhms;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter dateHourMinuteSecondMillis()
/*      */   {
/* 1230 */     return Constants.dhmsl;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeFormatter dateHourMinuteSecondFraction()
/*      */   {
/* 1242 */     return Constants.dhmsf;
/*      */   }
/*      */   
/*      */ 
/*      */   static final class Constants
/*      */   {
/* 1248 */     private static final DateTimeFormatter ye = ;
/* 1249 */     private static final DateTimeFormatter mye = monthElement();
/* 1250 */     private static final DateTimeFormatter dme = dayOfMonthElement();
/* 1251 */     private static final DateTimeFormatter we = weekyearElement();
/* 1252 */     private static final DateTimeFormatter wwe = weekElement();
/* 1253 */     private static final DateTimeFormatter dwe = dayOfWeekElement();
/* 1254 */     private static final DateTimeFormatter dye = dayOfYearElement();
/* 1255 */     private static final DateTimeFormatter hde = hourElement();
/* 1256 */     private static final DateTimeFormatter mhe = minuteElement();
/* 1257 */     private static final DateTimeFormatter sme = secondElement();
/* 1258 */     private static final DateTimeFormatter fse = fractionElement();
/* 1259 */     private static final DateTimeFormatter ze = offsetElement();
/* 1260 */     private static final DateTimeFormatter lte = literalTElement();
/*      */     
/*      */ 
/* 1263 */     private static final DateTimeFormatter ym = yearMonth();
/* 1264 */     private static final DateTimeFormatter ymd = yearMonthDay();
/*      */     
/*      */ 
/* 1267 */     private static final DateTimeFormatter ww = weekyearWeek();
/* 1268 */     private static final DateTimeFormatter wwd = weekyearWeekDay();
/*      */     
/*      */ 
/* 1271 */     private static final DateTimeFormatter hm = hourMinute();
/* 1272 */     private static final DateTimeFormatter hms = hourMinuteSecond();
/* 1273 */     private static final DateTimeFormatter hmsl = hourMinuteSecondMillis();
/* 1274 */     private static final DateTimeFormatter hmsf = hourMinuteSecondFraction();
/*      */     
/* 1276 */     private static final DateTimeFormatter dh = dateHour();
/* 1277 */     private static final DateTimeFormatter dhm = dateHourMinute();
/* 1278 */     private static final DateTimeFormatter dhms = dateHourMinuteSecond();
/* 1279 */     private static final DateTimeFormatter dhmsl = dateHourMinuteSecondMillis();
/* 1280 */     private static final DateTimeFormatter dhmsf = dateHourMinuteSecondFraction();
/*      */     
/*      */ 
/* 1283 */     private static final DateTimeFormatter t = time();
/* 1284 */     private static final DateTimeFormatter tx = timeNoMillis();
/* 1285 */     private static final DateTimeFormatter tt = tTime();
/* 1286 */     private static final DateTimeFormatter ttx = tTimeNoMillis();
/* 1287 */     private static final DateTimeFormatter dt = dateTime();
/* 1288 */     private static final DateTimeFormatter dtx = dateTimeNoMillis();
/*      */     
/*      */ 
/* 1291 */     private static final DateTimeFormatter wdt = weekDateTime();
/* 1292 */     private static final DateTimeFormatter wdtx = weekDateTimeNoMillis();
/*      */     
/* 1294 */     private static final DateTimeFormatter od = ordinalDate();
/* 1295 */     private static final DateTimeFormatter odt = ordinalDateTime();
/* 1296 */     private static final DateTimeFormatter odtx = ordinalDateTimeNoMillis();
/*      */     
/* 1298 */     private static final DateTimeFormatter bd = basicDate();
/* 1299 */     private static final DateTimeFormatter bt = basicTime();
/* 1300 */     private static final DateTimeFormatter btx = basicTimeNoMillis();
/* 1301 */     private static final DateTimeFormatter btt = basicTTime();
/* 1302 */     private static final DateTimeFormatter bttx = basicTTimeNoMillis();
/* 1303 */     private static final DateTimeFormatter bdt = basicDateTime();
/* 1304 */     private static final DateTimeFormatter bdtx = basicDateTimeNoMillis();
/*      */     
/* 1306 */     private static final DateTimeFormatter bod = basicOrdinalDate();
/* 1307 */     private static final DateTimeFormatter bodt = basicOrdinalDateTime();
/* 1308 */     private static final DateTimeFormatter bodtx = basicOrdinalDateTimeNoMillis();
/*      */     
/* 1310 */     private static final DateTimeFormatter bwd = basicWeekDate();
/* 1311 */     private static final DateTimeFormatter bwdt = basicWeekDateTime();
/* 1312 */     private static final DateTimeFormatter bwdtx = basicWeekDateTimeNoMillis();
/*      */     
/* 1314 */     private static final DateTimeFormatter dpe = dateElementParser();
/* 1315 */     private static final DateTimeFormatter tpe = timeElementParser();
/* 1316 */     private static final DateTimeFormatter dp = dateParser();
/* 1317 */     private static final DateTimeFormatter ldp = localDateParser();
/* 1318 */     private static final DateTimeFormatter tp = timeParser();
/* 1319 */     private static final DateTimeFormatter ltp = localTimeParser();
/* 1320 */     private static final DateTimeFormatter dtp = dateTimeParser();
/* 1321 */     private static final DateTimeFormatter dotp = dateOptionalTimeParser();
/* 1322 */     private static final DateTimeFormatter ldotp = localDateOptionalTimeParser();
/*      */     
/*      */     private static DateTimeFormatter dateParser()
/*      */     {
/* 1326 */       if (dp == null) {
/* 1327 */         DateTimeParser localDateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(offsetElement()).toParser();
/*      */         
/*      */ 
/* 1330 */         return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(localDateTimeParser).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1335 */       return dp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter localDateParser() {
/* 1339 */       if (ldp == null) {
/* 1340 */         return dateElementParser().withZoneUTC();
/*      */       }
/* 1342 */       return ldp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateElementParser() {
/* 1346 */       if (dpe == null) {
/* 1347 */         return new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(yearElement()).appendOptional(new DateTimeFormatterBuilder().append(monthElement()).appendOptional(dayOfMonthElement().getParser()).toParser()).toParser(), new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).appendOptional(dayOfWeekElement().getParser()).toParser(), new DateTimeFormatterBuilder().append(yearElement()).append(dayOfYearElement()).toParser() }).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1369 */       return dpe;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter timeParser() {
/* 1373 */       if (tp == null) {
/* 1374 */         return new DateTimeFormatterBuilder().appendOptional(literalTElement().getParser()).append(timeElementParser()).appendOptional(offsetElement().getParser()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1380 */       return tp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter localTimeParser() {
/* 1384 */       if (ltp == null) {
/* 1385 */         return new DateTimeFormatterBuilder().appendOptional(literalTElement().getParser()).append(timeElementParser()).toFormatter().withZoneUTC();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1390 */       return ltp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter timeElementParser() {
/* 1394 */       if (tpe == null)
/*      */       {
/* 1396 */         DateTimeParser localDateTimeParser = new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().appendLiteral('.').toParser(), new DateTimeFormatterBuilder().appendLiteral(',').toParser() }).toParser();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1407 */         return new DateTimeFormatterBuilder().append(hourElement()).append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(minuteElement()).append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(secondElement()).appendOptional(new DateTimeFormatterBuilder().append(localDateTimeParser).appendFractionOfSecond(1, 9).toParser()).toParser(), new DateTimeFormatterBuilder().append(localDateTimeParser).appendFractionOfMinute(1, 9).toParser(), null }).toParser(), new DateTimeFormatterBuilder().append(localDateTimeParser).appendFractionOfHour(1, 9).toParser(), null }).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1443 */       return tpe;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateTimeParser() {
/* 1447 */       if (dtp == null)
/*      */       {
/*      */ 
/* 1450 */         DateTimeParser localDateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(timeElementParser()).appendOptional(offsetElement().getParser()).toParser();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1455 */         return new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { localDateTimeParser, dateOptionalTimeParser().getParser() }).toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 1459 */       return dtp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateOptionalTimeParser() {
/* 1463 */       if (dotp == null) {
/* 1464 */         DateTimeParser localDateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').appendOptional(timeElementParser().getParser()).appendOptional(offsetElement().getParser()).toParser();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1469 */         return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(localDateTimeParser).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1474 */       return dotp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter localDateOptionalTimeParser() {
/* 1478 */       if (ldotp == null) {
/* 1479 */         DateTimeParser localDateTimeParser = new DateTimeFormatterBuilder().appendLiteral('T').append(timeElementParser()).toParser();
/*      */         
/*      */ 
/*      */ 
/* 1483 */         return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(localDateTimeParser).toFormatter().withZoneUTC();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1488 */       return ldotp;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter time()
/*      */     {
/* 1493 */       if (t == null) {
/* 1494 */         return new DateTimeFormatterBuilder().append(hourMinuteSecondFraction()).append(offsetElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1499 */       return t;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter timeNoMillis() {
/* 1503 */       if (tx == null) {
/* 1504 */         return new DateTimeFormatterBuilder().append(hourMinuteSecond()).append(offsetElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1509 */       return tx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter tTime() {
/* 1513 */       if (tt == null) {
/* 1514 */         return new DateTimeFormatterBuilder().append(literalTElement()).append(time()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1519 */       return tt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter tTimeNoMillis() {
/* 1523 */       if (ttx == null) {
/* 1524 */         return new DateTimeFormatterBuilder().append(literalTElement()).append(timeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1529 */       return ttx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateTime() {
/* 1533 */       if (dt == null) {
/* 1534 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(tTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1539 */       return dt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateTimeNoMillis() {
/* 1543 */       if (dtx == null) {
/* 1544 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(tTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1549 */       return dtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter ordinalDate() {
/* 1553 */       if (od == null) {
/* 1554 */         return new DateTimeFormatterBuilder().append(yearElement()).append(dayOfYearElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1559 */       return od;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter ordinalDateTime() {
/* 1563 */       if (odt == null) {
/* 1564 */         return new DateTimeFormatterBuilder().append(ordinalDate()).append(tTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1569 */       return odt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter ordinalDateTimeNoMillis() {
/* 1573 */       if (odtx == null) {
/* 1574 */         return new DateTimeFormatterBuilder().append(ordinalDate()).append(tTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1579 */       return odtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekDateTime() {
/* 1583 */       if (wdt == null) {
/* 1584 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(tTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1589 */       return wdt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekDateTimeNoMillis() {
/* 1593 */       if (wdtx == null) {
/* 1594 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(tTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1599 */       return wdtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicDate()
/*      */     {
/* 1604 */       if (bd == null) {
/* 1605 */         return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1611 */       return bd;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicTime() {
/* 1615 */       if (bt == null) {
/* 1616 */         return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendLiteral('.').appendFractionOfSecond(3, 9).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1625 */       return bt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicTimeNoMillis() {
/* 1629 */       if (btx == null) {
/* 1630 */         return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1637 */       return btx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicTTime() {
/* 1641 */       if (btt == null) {
/* 1642 */         return new DateTimeFormatterBuilder().append(literalTElement()).append(basicTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1647 */       return btt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicTTimeNoMillis() {
/* 1651 */       if (bttx == null) {
/* 1652 */         return new DateTimeFormatterBuilder().append(literalTElement()).append(basicTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1657 */       return bttx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicDateTime() {
/* 1661 */       if (bdt == null) {
/* 1662 */         return new DateTimeFormatterBuilder().append(basicDate()).append(basicTTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1667 */       return bdt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicDateTimeNoMillis() {
/* 1671 */       if (bdtx == null) {
/* 1672 */         return new DateTimeFormatterBuilder().append(basicDate()).append(basicTTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1677 */       return bdtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicOrdinalDate() {
/* 1681 */       if (bod == null) {
/* 1682 */         return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.dayOfYear(), 3).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1687 */       return bod;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicOrdinalDateTime() {
/* 1691 */       if (bodt == null) {
/* 1692 */         return new DateTimeFormatterBuilder().append(basicOrdinalDate()).append(basicTTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1697 */       return bodt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicOrdinalDateTimeNoMillis() {
/* 1701 */       if (bodtx == null) {
/* 1702 */         return new DateTimeFormatterBuilder().append(basicOrdinalDate()).append(basicTTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1707 */       return bodtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicWeekDate() {
/* 1711 */       if (bwd == null) {
/* 1712 */         return new DateTimeFormatterBuilder().appendWeekyear(4, 4).appendLiteral('W').appendFixedDecimal(DateTimeFieldType.weekOfWeekyear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfWeek(), 1).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1719 */       return bwd;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicWeekDateTime() {
/* 1723 */       if (bwdt == null) {
/* 1724 */         return new DateTimeFormatterBuilder().append(basicWeekDate()).append(basicTTime()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1729 */       return bwdt;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter basicWeekDateTimeNoMillis() {
/* 1733 */       if (bwdtx == null) {
/* 1734 */         return new DateTimeFormatterBuilder().append(basicWeekDate()).append(basicTTimeNoMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1739 */       return bwdtx;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter yearMonth()
/*      */     {
/* 1744 */       if (ym == null) {
/* 1745 */         return new DateTimeFormatterBuilder().append(yearElement()).append(monthElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1750 */       return ym;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter yearMonthDay() {
/* 1754 */       if (ymd == null) {
/* 1755 */         return new DateTimeFormatterBuilder().append(yearElement()).append(monthElement()).append(dayOfMonthElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1761 */       return ymd;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekyearWeek() {
/* 1765 */       if (ww == null) {
/* 1766 */         return new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1771 */       return ww;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekyearWeekDay() {
/* 1775 */       if (wwd == null) {
/* 1776 */         return new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).append(dayOfWeekElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1782 */       return wwd;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter hourMinute() {
/* 1786 */       if (hm == null) {
/* 1787 */         return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1792 */       return hm;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter hourMinuteSecond() {
/* 1796 */       if (hms == null) {
/* 1797 */         return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1803 */       return hms;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter hourMinuteSecondMillis() {
/* 1807 */       if (hmsl == null) {
/* 1808 */         return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).appendLiteral('.').appendFractionOfSecond(3, 3).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1816 */       return hmsl;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter hourMinuteSecondFraction() {
/* 1820 */       if (hmsf == null) {
/* 1821 */         return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).append(fractionElement()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1828 */       return hmsf;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateHour() {
/* 1832 */       if (dh == null) {
/* 1833 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(ISODateTimeFormat.hour()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1839 */       return dh;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateHourMinute() {
/* 1843 */       if (dhm == null) {
/* 1844 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinute()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1850 */       return dhm;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateHourMinuteSecond() {
/* 1854 */       if (dhms == null) {
/* 1855 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecond()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1861 */       return dhms;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateHourMinuteSecondMillis() {
/* 1865 */       if (dhmsl == null) {
/* 1866 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecondMillis()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1872 */       return dhmsl;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dateHourMinuteSecondFraction() {
/* 1876 */       if (dhmsf == null) {
/* 1877 */         return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecondFraction()).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1883 */       return dhmsf;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter yearElement()
/*      */     {
/* 1888 */       if (ye == null) {
/* 1889 */         return new DateTimeFormatterBuilder().appendYear(4, 9).toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 1893 */       return ye;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter monthElement() {
/* 1897 */       if (mye == null) {
/* 1898 */         return new DateTimeFormatterBuilder().appendLiteral('-').appendMonthOfYear(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1903 */       return mye;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dayOfMonthElement() {
/* 1907 */       if (dme == null) {
/* 1908 */         return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfMonth(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1913 */       return dme;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekyearElement() {
/* 1917 */       if (we == null) {
/* 1918 */         return new DateTimeFormatterBuilder().appendWeekyear(4, 9).toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 1922 */       return we;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter weekElement() {
/* 1926 */       if (wwe == null) {
/* 1927 */         return new DateTimeFormatterBuilder().appendLiteral("-W").appendWeekOfWeekyear(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1932 */       return wwe;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dayOfWeekElement() {
/* 1936 */       if (dwe == null) {
/* 1937 */         return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfWeek(1).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1942 */       return dwe;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter dayOfYearElement() {
/* 1946 */       if (dye == null) {
/* 1947 */         return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfYear(3).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1952 */       return dye;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter literalTElement() {
/* 1956 */       if (lte == null) {
/* 1957 */         return new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 1961 */       return lte;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter hourElement() {
/* 1965 */       if (hde == null) {
/* 1966 */         return new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 1970 */       return hde;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter minuteElement() {
/* 1974 */       if (mhe == null) {
/* 1975 */         return new DateTimeFormatterBuilder().appendLiteral(':').appendMinuteOfHour(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1980 */       return mhe;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter secondElement() {
/* 1984 */       if (sme == null) {
/* 1985 */         return new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1990 */       return sme;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter fractionElement() {
/* 1994 */       if (fse == null) {
/* 1995 */         return new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toFormatter();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2002 */       return fse;
/*      */     }
/*      */     
/*      */     private static DateTimeFormatter offsetElement() {
/* 2006 */       if (ze == null) {
/* 2007 */         return new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 4).toFormatter();
/*      */       }
/*      */       
/*      */ 
/* 2011 */       return ze;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\ISODateTimeFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */