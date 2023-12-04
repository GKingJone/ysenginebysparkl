/*      */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.text.DateFormatSymbols;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
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
/*      */ public class DateTimeZoneBuilder
/*      */ {
/*      */   private final ArrayList<RuleSet> iRuleSets;
/*      */   
/*      */   public static DateTimeZone readFrom(InputStream paramInputStream, String paramString)
/*      */     throws IOException
/*      */   {
/*   95 */     if ((paramInputStream instanceof DataInput)) {
/*   96 */       return readFrom((DataInput)paramInputStream, paramString);
/*      */     }
/*   98 */     return readFrom(new DataInputStream(paramInputStream), paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTimeZone readFrom(DataInput paramDataInput, String paramString)
/*      */     throws IOException
/*      */   {
/*  110 */     switch (paramDataInput.readUnsignedByte()) {
/*      */     case 70: 
/*  112 */       Object localObject = new FixedDateTimeZone(paramString, paramDataInput.readUTF(), (int)readMillis(paramDataInput), (int)readMillis(paramDataInput));
/*      */       
/*  114 */       if (((DateTimeZone)localObject).equals(DateTimeZone.UTC)) {
/*  115 */         localObject = DateTimeZone.UTC;
/*      */       }
/*  117 */       return (DateTimeZone)localObject;
/*      */     case 67: 
/*  119 */       return CachedDateTimeZone.forZone(PrecalculatedZone.readFrom(paramDataInput, paramString));
/*      */     case 80: 
/*  121 */       return PrecalculatedZone.readFrom(paramDataInput, paramString);
/*      */     }
/*  123 */     throw new IOException("Invalid encoding");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static void writeMillis(DataOutput paramDataOutput, long paramLong)
/*      */     throws IOException
/*      */   {
/*      */     long l;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  140 */     if (paramLong % 1800000L == 0L)
/*      */     {
/*  142 */       l = paramLong / 1800000L;
/*  143 */       if (l << 58 >> 58 == l)
/*      */       {
/*  145 */         paramDataOutput.writeByte((int)(l & 0x3F));
/*  146 */         return;
/*      */       }
/*      */     }
/*      */     
/*  150 */     if (paramLong % 60000L == 0L)
/*      */     {
/*  152 */       l = paramLong / 60000L;
/*  153 */       if (l << 34 >> 34 == l)
/*      */       {
/*  155 */         paramDataOutput.writeInt(0x40000000 | (int)(l & 0x3FFFFFFF));
/*  156 */         return;
/*      */       }
/*      */     }
/*      */     
/*  160 */     if (paramLong % 1000L == 0L)
/*      */     {
/*  162 */       l = paramLong / 1000L;
/*  163 */       if (l << 26 >> 26 == l)
/*      */       {
/*  165 */         paramDataOutput.writeByte(0x80 | (int)(l >> 32 & 0x3F));
/*  166 */         paramDataOutput.writeInt((int)(l & 0xFFFFFFFFFFFFFFFF));
/*  167 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  175 */     paramDataOutput.writeByte(paramLong < 0L ? 255 : 192);
/*  176 */     paramDataOutput.writeLong(paramLong);
/*      */   }
/*      */   
/*      */ 
/*      */   static long readMillis(DataInput paramDataInput)
/*      */     throws IOException
/*      */   {
/*  183 */     int i = paramDataInput.readUnsignedByte();
/*  184 */     switch (i >> 6) {
/*      */     case 0: 
/*      */     default: 
/*  187 */       i = i << 26 >> 26;
/*  188 */       return i * 1800000L;
/*      */     
/*      */ 
/*      */     case 1: 
/*  192 */       i = i << 26 >> 2;
/*  193 */       i |= paramDataInput.readUnsignedByte() << 16;
/*  194 */       i |= paramDataInput.readUnsignedByte() << 8;
/*  195 */       i |= paramDataInput.readUnsignedByte();
/*  196 */       return i * 60000L;
/*      */     
/*      */ 
/*      */     case 2: 
/*  200 */       long l = i << 58 >> 26;
/*  201 */       l |= paramDataInput.readUnsignedByte() << 24;
/*  202 */       l |= paramDataInput.readUnsignedByte() << 16;
/*  203 */       l |= paramDataInput.readUnsignedByte() << 8;
/*  204 */       l |= paramDataInput.readUnsignedByte();
/*  205 */       return l * 1000L;
/*      */     }
/*      */     
/*      */     
/*  209 */     return paramDataInput.readLong();
/*      */   }
/*      */   
/*      */ 
/*      */   private static DateTimeZone buildFixedZone(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*      */   {
/*  215 */     if (("UTC".equals(paramString1)) && (paramString1.equals(paramString2)) && (paramInt1 == 0) && (paramInt2 == 0))
/*      */     {
/*  217 */       return DateTimeZone.UTC;
/*      */     }
/*  219 */     return new FixedDateTimeZone(paramString1, paramString2, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DateTimeZoneBuilder()
/*      */   {
/*  226 */     this.iRuleSets = new ArrayList(10);
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
/*      */   public DateTimeZoneBuilder addCutover(int paramInt1, char paramChar, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5)
/*      */   {
/*  252 */     if (this.iRuleSets.size() > 0) {
/*  253 */       OfYear localOfYear = new OfYear(paramChar, paramInt2, paramInt3, paramInt4, paramBoolean, paramInt5);
/*      */       
/*  255 */       RuleSet localRuleSet = (RuleSet)this.iRuleSets.get(this.iRuleSets.size() - 1);
/*  256 */       localRuleSet.setUpperLimit(paramInt1, localOfYear);
/*      */     }
/*  258 */     this.iRuleSets.add(new RuleSet());
/*  259 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeZoneBuilder setStandardOffset(int paramInt)
/*      */   {
/*  268 */     getLastRuleSet().setStandardOffset(paramInt);
/*  269 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DateTimeZoneBuilder setFixedSavings(String paramString, int paramInt)
/*      */   {
/*  276 */     getLastRuleSet().setFixedSavings(paramString, paramInt);
/*  277 */     return this;
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
/*      */   public DateTimeZoneBuilder addRecurringSavings(String paramString, int paramInt1, int paramInt2, int paramInt3, char paramChar, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, int paramInt7)
/*      */   {
/*  309 */     if (paramInt2 <= paramInt3) {
/*  310 */       OfYear localOfYear = new OfYear(paramChar, paramInt4, paramInt5, paramInt6, paramBoolean, paramInt7);
/*      */       
/*  312 */       Recurrence localRecurrence = new Recurrence(localOfYear, paramString, paramInt1);
/*  313 */       Rule localRule = new Rule(localRecurrence, paramInt2, paramInt3);
/*  314 */       getLastRuleSet().addRule(localRule);
/*      */     }
/*  316 */     return this;
/*      */   }
/*      */   
/*      */   private RuleSet getLastRuleSet() {
/*  320 */     if (this.iRuleSets.size() == 0) {
/*  321 */       addCutover(Integer.MIN_VALUE, 'w', 1, 1, 0, false, 0);
/*      */     }
/*  323 */     return (RuleSet)this.iRuleSets.get(this.iRuleSets.size() - 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeZone toDateTimeZone(String paramString, boolean paramBoolean)
/*      */   {
/*  333 */     if (paramString == null) {
/*  334 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  339 */     ArrayList localArrayList = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*  343 */     DSTZone localDSTZone = null;
/*      */     
/*  345 */     long l = Long.MIN_VALUE;
/*  346 */     int i = 0;
/*      */     
/*  348 */     int j = this.iRuleSets.size();
/*  349 */     for (int k = 0; k < j; k++) {
/*  350 */       RuleSet localRuleSet = (RuleSet)this.iRuleSets.get(k);
/*  351 */       Transition localTransition = localRuleSet.firstTransition(l);
/*  352 */       if (localTransition != null)
/*      */       {
/*      */ 
/*  355 */         addTransition(localArrayList, localTransition);
/*  356 */         l = localTransition.getMillis();
/*  357 */         i = localTransition.getSaveMillis();
/*      */         
/*      */ 
/*  360 */         localRuleSet = new RuleSet(localRuleSet);
/*      */         
/*  362 */         while (((localTransition = localRuleSet.nextTransition(l, i)) != null) && (
/*  363 */           (!addTransition(localArrayList, localTransition)) || 
/*  364 */           (localDSTZone == null)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  369 */           l = localTransition.getMillis();
/*  370 */           i = localTransition.getSaveMillis();
/*  371 */           if ((localDSTZone == null) && (k == j - 1)) {
/*  372 */             localDSTZone = localRuleSet.buildTailZone(paramString);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  379 */         l = localRuleSet.getUpperLimit(i);
/*      */       }
/*      */     }
/*      */     
/*  383 */     if (localArrayList.size() == 0) {
/*  384 */       if (localDSTZone != null)
/*      */       {
/*  386 */         return localDSTZone;
/*      */       }
/*  388 */       return buildFixedZone(paramString, "UTC", 0, 0);
/*      */     }
/*  390 */     if ((localArrayList.size() == 1) && (localDSTZone == null)) {
/*  391 */       localObject = (Transition)localArrayList.get(0);
/*  392 */       return buildFixedZone(paramString, ((Transition)localObject).getNameKey(), ((Transition)localObject).getWallOffset(), ((Transition)localObject).getStandardOffset());
/*      */     }
/*      */     
/*      */ 
/*  396 */     Object localObject = PrecalculatedZone.create(paramString, paramBoolean, localArrayList, localDSTZone);
/*  397 */     if (((PrecalculatedZone)localObject).isCachable()) {
/*  398 */       return CachedDateTimeZone.forZone((DateTimeZone)localObject);
/*      */     }
/*  400 */     return (DateTimeZone)localObject;
/*      */   }
/*      */   
/*      */   private boolean addTransition(ArrayList<Transition> paramArrayList, Transition paramTransition) {
/*  404 */     int i = paramArrayList.size();
/*  405 */     if (i == 0) {
/*  406 */       paramArrayList.add(paramTransition);
/*  407 */       return true;
/*      */     }
/*      */     
/*  410 */     Transition localTransition = (Transition)paramArrayList.get(i - 1);
/*  411 */     if (!paramTransition.isTransitionFrom(localTransition)) {
/*  412 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  417 */     int j = 0;
/*  418 */     if (i >= 2) {
/*  419 */       j = ((Transition)paramArrayList.get(i - 2)).getWallOffset();
/*      */     }
/*  421 */     int k = localTransition.getWallOffset();
/*      */     
/*  423 */     long l1 = localTransition.getMillis() + j;
/*  424 */     long l2 = paramTransition.getMillis() + k;
/*      */     
/*  426 */     if (l2 != l1) {
/*  427 */       paramArrayList.add(paramTransition);
/*  428 */       return true;
/*      */     }
/*      */     
/*  431 */     paramArrayList.remove(i - 1);
/*  432 */     return addTransition(paramArrayList, paramTransition);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeTo(String paramString, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  443 */     if ((paramOutputStream instanceof DataOutput)) {
/*  444 */       writeTo(paramString, (DataOutput)paramOutputStream);
/*      */     } else {
/*  446 */       writeTo(paramString, new DataOutputStream(paramOutputStream));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeTo(String paramString, DataOutput paramDataOutput)
/*      */     throws IOException
/*      */   {
/*  459 */     DateTimeZone localDateTimeZone = toDateTimeZone(paramString, false);
/*      */     
/*  461 */     if ((localDateTimeZone instanceof FixedDateTimeZone)) {
/*  462 */       paramDataOutput.writeByte(70);
/*  463 */       paramDataOutput.writeUTF(localDateTimeZone.getNameKey(0L));
/*  464 */       writeMillis(paramDataOutput, localDateTimeZone.getOffset(0L));
/*  465 */       writeMillis(paramDataOutput, localDateTimeZone.getStandardOffset(0L));
/*      */     } else {
/*  467 */       if ((localDateTimeZone instanceof CachedDateTimeZone)) {
/*  468 */         paramDataOutput.writeByte(67);
/*  469 */         localDateTimeZone = ((CachedDateTimeZone)localDateTimeZone).getUncachedZone();
/*      */       } else {
/*  471 */         paramDataOutput.writeByte(80);
/*      */       }
/*  473 */       ((PrecalculatedZone)localDateTimeZone).writeTo(paramDataOutput); } }
/*      */   
/*      */   private static final class OfYear { final char iMode;
/*      */     final int iMonthOfYear;
/*      */     final int iDayOfMonth;
/*      */     final int iDayOfWeek;
/*      */     final boolean iAdvance;
/*      */     final int iMillisOfDay;
/*      */     
/*  482 */     static OfYear readFrom(DataInput paramDataInput) throws IOException { return new OfYear((char)paramDataInput.readUnsignedByte(), paramDataInput.readUnsignedByte(), paramDataInput.readByte(), paramDataInput.readUnsignedByte(), paramDataInput.readBoolean(), (int)DateTimeZoneBuilder.readMillis(paramDataInput)); }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     OfYear(char paramChar, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4)
/*      */     {
/*  505 */       if ((paramChar != 'u') && (paramChar != 'w') && (paramChar != 's')) {
/*  506 */         throw new IllegalArgumentException("Unknown mode: " + paramChar);
/*      */       }
/*      */       
/*  509 */       this.iMode = paramChar;
/*  510 */       this.iMonthOfYear = paramInt1;
/*  511 */       this.iDayOfMonth = paramInt2;
/*  512 */       this.iDayOfWeek = paramInt3;
/*  513 */       this.iAdvance = paramBoolean;
/*  514 */       this.iMillisOfDay = paramInt4;
/*      */     }
/*      */     
/*      */ 
/*      */     public long setInstant(int paramInt1, int paramInt2, int paramInt3)
/*      */     {
/*      */       int i;
/*      */       
/*  522 */       if (this.iMode == 'w') {
/*  523 */         i = paramInt2 + paramInt3;
/*  524 */       } else if (this.iMode == 's') {
/*  525 */         i = paramInt2;
/*      */       } else {
/*  527 */         i = 0;
/*      */       }
/*      */       
/*  530 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*  531 */       long l = localISOChronology.year().set(0L, paramInt1);
/*  532 */       l = localISOChronology.monthOfYear().set(l, this.iMonthOfYear);
/*  533 */       l = localISOChronology.millisOfDay().set(l, this.iMillisOfDay);
/*  534 */       l = setDayOfMonth(localISOChronology, l);
/*      */       
/*  536 */       if (this.iDayOfWeek != 0) {
/*  537 */         l = setDayOfWeek(localISOChronology, l);
/*      */       }
/*      */       
/*      */ 
/*  541 */       return l - i;
/*      */     }
/*      */     
/*      */ 
/*      */     public long next(long paramLong, int paramInt1, int paramInt2)
/*      */     {
/*      */       int i;
/*      */       
/*  549 */       if (this.iMode == 'w') {
/*  550 */         i = paramInt1 + paramInt2;
/*  551 */       } else if (this.iMode == 's') {
/*  552 */         i = paramInt1;
/*      */       } else {
/*  554 */         i = 0;
/*      */       }
/*      */       
/*      */ 
/*  558 */       paramLong += i;
/*      */       
/*  560 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*  561 */       long l = localISOChronology.monthOfYear().set(paramLong, this.iMonthOfYear);
/*      */       
/*  563 */       l = localISOChronology.millisOfDay().set(l, 0);
/*  564 */       l = localISOChronology.millisOfDay().add(l, this.iMillisOfDay);
/*  565 */       l = setDayOfMonthNext(localISOChronology, l);
/*      */       
/*  567 */       if (this.iDayOfWeek == 0) {
/*  568 */         if (l <= paramLong) {
/*  569 */           l = localISOChronology.year().add(l, 1);
/*  570 */           l = setDayOfMonthNext(localISOChronology, l);
/*      */         }
/*      */       } else {
/*  573 */         l = setDayOfWeek(localISOChronology, l);
/*  574 */         if (l <= paramLong) {
/*  575 */           l = localISOChronology.year().add(l, 1);
/*  576 */           l = localISOChronology.monthOfYear().set(l, this.iMonthOfYear);
/*  577 */           l = setDayOfMonthNext(localISOChronology, l);
/*  578 */           l = setDayOfWeek(localISOChronology, l);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  583 */       return l - i;
/*      */     }
/*      */     
/*      */ 
/*      */     public long previous(long paramLong, int paramInt1, int paramInt2)
/*      */     {
/*      */       int i;
/*      */       
/*  591 */       if (this.iMode == 'w') {
/*  592 */         i = paramInt1 + paramInt2;
/*  593 */       } else if (this.iMode == 's') {
/*  594 */         i = paramInt1;
/*      */       } else {
/*  596 */         i = 0;
/*      */       }
/*      */       
/*      */ 
/*  600 */       paramLong += i;
/*      */       
/*  602 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*  603 */       long l = localISOChronology.monthOfYear().set(paramLong, this.iMonthOfYear);
/*      */       
/*  605 */       l = localISOChronology.millisOfDay().set(l, 0);
/*  606 */       l = localISOChronology.millisOfDay().add(l, this.iMillisOfDay);
/*  607 */       l = setDayOfMonthPrevious(localISOChronology, l);
/*      */       
/*  609 */       if (this.iDayOfWeek == 0) {
/*  610 */         if (l >= paramLong) {
/*  611 */           l = localISOChronology.year().add(l, -1);
/*  612 */           l = setDayOfMonthPrevious(localISOChronology, l);
/*      */         }
/*      */       } else {
/*  615 */         l = setDayOfWeek(localISOChronology, l);
/*  616 */         if (l >= paramLong) {
/*  617 */           l = localISOChronology.year().add(l, -1);
/*  618 */           l = localISOChronology.monthOfYear().set(l, this.iMonthOfYear);
/*  619 */           l = setDayOfMonthPrevious(localISOChronology, l);
/*  620 */           l = setDayOfWeek(localISOChronology, l);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  625 */       return l - i;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject) {
/*  629 */       if (this == paramObject) {
/*  630 */         return true;
/*      */       }
/*  632 */       if ((paramObject instanceof OfYear)) {
/*  633 */         OfYear localOfYear = (OfYear)paramObject;
/*  634 */         return (this.iMode == localOfYear.iMode) && (this.iMonthOfYear == localOfYear.iMonthOfYear) && (this.iDayOfMonth == localOfYear.iDayOfMonth) && (this.iDayOfWeek == localOfYear.iDayOfWeek) && (this.iAdvance == localOfYear.iAdvance) && (this.iMillisOfDay == localOfYear.iMillisOfDay);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  642 */       return false;
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
/*      */ 
/*      */     public void writeTo(DataOutput paramDataOutput)
/*      */       throws IOException
/*      */     {
/*  659 */       paramDataOutput.writeByte(this.iMode);
/*  660 */       paramDataOutput.writeByte(this.iMonthOfYear);
/*  661 */       paramDataOutput.writeByte(this.iDayOfMonth);
/*  662 */       paramDataOutput.writeByte(this.iDayOfWeek);
/*  663 */       paramDataOutput.writeBoolean(this.iAdvance);
/*  664 */       DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iMillisOfDay);
/*      */     }
/*      */     
/*      */ 
/*      */     private long setDayOfMonthNext(Chronology paramChronology, long paramLong)
/*      */     {
/*      */       try
/*      */       {
/*  672 */         paramLong = setDayOfMonth(paramChronology, paramLong);
/*      */       } catch (IllegalArgumentException localIllegalArgumentException) {
/*  674 */         if ((this.iMonthOfYear == 2) && (this.iDayOfMonth == 29)) {
/*  675 */           while (!paramChronology.year().isLeap(paramLong)) {
/*  676 */             paramLong = paramChronology.year().add(paramLong, 1);
/*      */           }
/*  678 */           paramLong = setDayOfMonth(paramChronology, paramLong);
/*      */         } else {
/*  680 */           throw localIllegalArgumentException;
/*      */         }
/*      */       }
/*  683 */       return paramLong;
/*      */     }
/*      */     
/*      */ 
/*      */     private long setDayOfMonthPrevious(Chronology paramChronology, long paramLong)
/*      */     {
/*      */       try
/*      */       {
/*  691 */         paramLong = setDayOfMonth(paramChronology, paramLong);
/*      */       } catch (IllegalArgumentException localIllegalArgumentException) {
/*  693 */         if ((this.iMonthOfYear == 2) && (this.iDayOfMonth == 29)) {
/*  694 */           while (!paramChronology.year().isLeap(paramLong)) {
/*  695 */             paramLong = paramChronology.year().add(paramLong, -1);
/*      */           }
/*  697 */           paramLong = setDayOfMonth(paramChronology, paramLong);
/*      */         } else {
/*  699 */           throw localIllegalArgumentException;
/*      */         }
/*      */       }
/*  702 */       return paramLong;
/*      */     }
/*      */     
/*      */     private long setDayOfMonth(Chronology paramChronology, long paramLong) {
/*  706 */       if (this.iDayOfMonth >= 0) {
/*  707 */         paramLong = paramChronology.dayOfMonth().set(paramLong, this.iDayOfMonth);
/*      */       } else {
/*  709 */         paramLong = paramChronology.dayOfMonth().set(paramLong, 1);
/*  710 */         paramLong = paramChronology.monthOfYear().add(paramLong, 1);
/*  711 */         paramLong = paramChronology.dayOfMonth().add(paramLong, this.iDayOfMonth);
/*      */       }
/*  713 */       return paramLong;
/*      */     }
/*      */     
/*      */     private long setDayOfWeek(Chronology paramChronology, long paramLong) {
/*  717 */       int i = paramChronology.dayOfWeek().get(paramLong);
/*  718 */       int j = this.iDayOfWeek - i;
/*  719 */       if (j != 0) {
/*  720 */         if (this.iAdvance) {
/*  721 */           if (j < 0) {
/*  722 */             j += 7;
/*      */           }
/*      */         }
/*  725 */         else if (j > 0) {
/*  726 */           j -= 7;
/*      */         }
/*      */         
/*  729 */         paramLong = paramChronology.dayOfWeek().add(paramLong, j);
/*      */       }
/*  731 */       return paramLong;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class Recurrence {
/*      */     final OfYear iOfYear;
/*      */     final String iNameKey;
/*      */     final int iSaveMillis;
/*      */     
/*  740 */     static Recurrence readFrom(DataInput paramDataInput) throws IOException { return new Recurrence(OfYear.readFrom(paramDataInput), paramDataInput.readUTF(), (int)DateTimeZoneBuilder.readMillis(paramDataInput)); }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Recurrence(OfYear paramOfYear, String paramString, int paramInt)
/*      */     {
/*  748 */       this.iOfYear = paramOfYear;
/*  749 */       this.iNameKey = paramString;
/*  750 */       this.iSaveMillis = paramInt;
/*      */     }
/*      */     
/*      */     public OfYear getOfYear() {
/*  754 */       return this.iOfYear;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long next(long paramLong, int paramInt1, int paramInt2)
/*      */     {
/*  761 */       return this.iOfYear.next(paramLong, paramInt1, paramInt2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long previous(long paramLong, int paramInt1, int paramInt2)
/*      */     {
/*  768 */       return this.iOfYear.previous(paramLong, paramInt1, paramInt2);
/*      */     }
/*      */     
/*      */     public String getNameKey() {
/*  772 */       return this.iNameKey;
/*      */     }
/*      */     
/*      */     public int getSaveMillis() {
/*  776 */       return this.iSaveMillis;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject) {
/*  780 */       if (this == paramObject) {
/*  781 */         return true;
/*      */       }
/*  783 */       if ((paramObject instanceof Recurrence)) {
/*  784 */         Recurrence localRecurrence = (Recurrence)paramObject;
/*  785 */         return (this.iSaveMillis == localRecurrence.iSaveMillis) && (this.iNameKey.equals(localRecurrence.iNameKey)) && (this.iOfYear.equals(localRecurrence.iOfYear));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  790 */       return false;
/*      */     }
/*      */     
/*      */     public void writeTo(DataOutput paramDataOutput) throws IOException {
/*  794 */       this.iOfYear.writeTo(paramDataOutput);
/*  795 */       paramDataOutput.writeUTF(this.iNameKey);
/*  796 */       DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iSaveMillis);
/*      */     }
/*      */     
/*      */     Recurrence rename(String paramString) {
/*  800 */       return new Recurrence(this.iOfYear, paramString, this.iSaveMillis);
/*      */     }
/*      */     
/*      */     Recurrence renameAppend(String paramString) {
/*  804 */       return rename((this.iNameKey + paramString).intern());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static final class Rule
/*      */   {
/*      */     final Recurrence iRecurrence;
/*      */     final int iFromYear;
/*      */     final int iToYear;
/*      */     
/*      */     Rule(Recurrence paramRecurrence, int paramInt1, int paramInt2)
/*      */     {
/*  817 */       this.iRecurrence = paramRecurrence;
/*  818 */       this.iFromYear = paramInt1;
/*  819 */       this.iToYear = paramInt2;
/*      */     }
/*      */     
/*      */     public int getFromYear()
/*      */     {
/*  824 */       return this.iFromYear;
/*      */     }
/*      */     
/*      */     public int getToYear() {
/*  828 */       return this.iToYear;
/*      */     }
/*      */     
/*      */     public OfYear getOfYear()
/*      */     {
/*  833 */       return this.iRecurrence.getOfYear();
/*      */     }
/*      */     
/*      */     public String getNameKey() {
/*  837 */       return this.iRecurrence.getNameKey();
/*      */     }
/*      */     
/*      */     public int getSaveMillis() {
/*  841 */       return this.iRecurrence.getSaveMillis();
/*      */     }
/*      */     
/*      */     public long next(long paramLong, int paramInt1, int paramInt2) {
/*  845 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*      */       
/*  847 */       int i = paramInt1 + paramInt2;
/*  848 */       long l1 = paramLong;
/*      */       
/*      */       int j;
/*  851 */       if (paramLong == Long.MIN_VALUE) {
/*  852 */         j = Integer.MIN_VALUE;
/*      */       } else {
/*  854 */         j = localISOChronology.year().get(paramLong + i);
/*      */       }
/*      */       
/*  857 */       if (j < this.iFromYear)
/*      */       {
/*  859 */         l1 = localISOChronology.year().set(0L, this.iFromYear) - i;
/*      */         
/*      */ 
/*  862 */         l1 -= 1L;
/*      */       }
/*      */       
/*  865 */       long l2 = this.iRecurrence.next(l1, paramInt1, paramInt2);
/*      */       
/*  867 */       if (l2 > paramLong) {
/*  868 */         j = localISOChronology.year().get(l2 + i);
/*  869 */         if (j > this.iToYear)
/*      */         {
/*  871 */           l2 = paramLong;
/*      */         }
/*      */       }
/*      */       
/*  875 */       return l2;
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class Transition {
/*      */     private final long iMillis;
/*      */     private final String iNameKey;
/*      */     private final int iWallOffset;
/*      */     private final int iStandardOffset;
/*      */     
/*      */     Transition(long paramLong, Transition paramTransition) {
/*  886 */       this.iMillis = paramLong;
/*  887 */       this.iNameKey = paramTransition.iNameKey;
/*  888 */       this.iWallOffset = paramTransition.iWallOffset;
/*  889 */       this.iStandardOffset = paramTransition.iStandardOffset;
/*      */     }
/*      */     
/*      */     Transition(long paramLong, Rule paramRule, int paramInt) {
/*  893 */       this.iMillis = paramLong;
/*  894 */       this.iNameKey = paramRule.getNameKey();
/*  895 */       this.iWallOffset = (paramInt + paramRule.getSaveMillis());
/*  896 */       this.iStandardOffset = paramInt;
/*      */     }
/*      */     
/*      */     Transition(long paramLong, String paramString, int paramInt1, int paramInt2)
/*      */     {
/*  901 */       this.iMillis = paramLong;
/*  902 */       this.iNameKey = paramString;
/*  903 */       this.iWallOffset = paramInt1;
/*  904 */       this.iStandardOffset = paramInt2;
/*      */     }
/*      */     
/*      */     public long getMillis() {
/*  908 */       return this.iMillis;
/*      */     }
/*      */     
/*      */     public String getNameKey() {
/*  912 */       return this.iNameKey;
/*      */     }
/*      */     
/*      */     public int getWallOffset() {
/*  916 */       return this.iWallOffset;
/*      */     }
/*      */     
/*      */     public int getStandardOffset() {
/*  920 */       return this.iStandardOffset;
/*      */     }
/*      */     
/*      */     public int getSaveMillis() {
/*  924 */       return this.iWallOffset - this.iStandardOffset;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean isTransitionFrom(Transition paramTransition)
/*      */     {
/*  931 */       if (paramTransition == null) {
/*  932 */         return true;
/*      */       }
/*  934 */       return (this.iMillis > paramTransition.iMillis) && ((this.iWallOffset != paramTransition.iWallOffset) || (!this.iNameKey.equals(paramTransition.iNameKey)));
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class RuleSet
/*      */   {
/*      */     private static final int YEAR_LIMIT;
/*      */     private int iStandardOffset;
/*      */     private ArrayList<Rule> iRules;
/*      */     private String iInitialNameKey;
/*      */     private int iInitialSaveMillis;
/*      */     private int iUpperYear;
/*      */     private OfYear iUpperOfYear;
/*      */     
/*      */     static
/*      */     {
/*  950 */       long l = DateTimeUtils.currentTimeMillis();
/*  951 */       YEAR_LIMIT = ISOChronology.getInstanceUTC().year().get(l) + 100;
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
/*      */     RuleSet()
/*      */     {
/*  966 */       this.iRules = new ArrayList(10);
/*  967 */       this.iUpperYear = Integer.MAX_VALUE;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     RuleSet(RuleSet paramRuleSet)
/*      */     {
/*  974 */       this.iStandardOffset = paramRuleSet.iStandardOffset;
/*  975 */       this.iRules = new ArrayList(paramRuleSet.iRules);
/*  976 */       this.iInitialNameKey = paramRuleSet.iInitialNameKey;
/*  977 */       this.iInitialSaveMillis = paramRuleSet.iInitialSaveMillis;
/*  978 */       this.iUpperYear = paramRuleSet.iUpperYear;
/*  979 */       this.iUpperOfYear = paramRuleSet.iUpperOfYear;
/*      */     }
/*      */     
/*      */     public int getStandardOffset()
/*      */     {
/*  984 */       return this.iStandardOffset;
/*      */     }
/*      */     
/*      */     public void setStandardOffset(int paramInt) {
/*  988 */       this.iStandardOffset = paramInt;
/*      */     }
/*      */     
/*      */     public void setFixedSavings(String paramString, int paramInt) {
/*  992 */       this.iInitialNameKey = paramString;
/*  993 */       this.iInitialSaveMillis = paramInt;
/*      */     }
/*      */     
/*      */     public void addRule(Rule paramRule) {
/*  997 */       if (!this.iRules.contains(paramRule)) {
/*  998 */         this.iRules.add(paramRule);
/*      */       }
/*      */     }
/*      */     
/*      */     public void setUpperLimit(int paramInt, OfYear paramOfYear) {
/* 1003 */       this.iUpperYear = paramInt;
/* 1004 */       this.iUpperOfYear = paramOfYear;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Transition firstTransition(long paramLong)
/*      */     {
/* 1014 */       if (this.iInitialNameKey != null)
/*      */       {
/* 1016 */         return new Transition(paramLong, this.iInitialNameKey, this.iStandardOffset + this.iInitialSaveMillis, this.iStandardOffset);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1021 */       ArrayList localArrayList = new ArrayList(this.iRules);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1027 */       long l = Long.MIN_VALUE;
/* 1028 */       int i = 0;
/* 1029 */       Transition localTransition1 = null;
/*      */       
/*      */       Transition localTransition2;
/* 1032 */       while ((localTransition2 = nextTransition(l, i)) != null) {
/* 1033 */         l = localTransition2.getMillis();
/*      */         
/* 1035 */         if (l == paramLong) {
/* 1036 */           localTransition1 = new Transition(paramLong, localTransition2);
/* 1037 */           break;
/*      */         }
/*      */         
/* 1040 */         if (l > paramLong) {
/* 1041 */           if (localTransition1 == null)
/*      */           {
/*      */ 
/*      */ 
/* 1045 */             for (Rule localRule : localArrayList) {
/* 1046 */               if (localRule.getSaveMillis() == 0) {
/* 1047 */                 localTransition1 = new Transition(paramLong, localRule, this.iStandardOffset);
/* 1048 */                 break;
/*      */               }
/*      */             }
/*      */           }
/* 1052 */           if (localTransition1 != null) {
/*      */             break;
/*      */           }
/*      */           
/* 1056 */           localTransition1 = new Transition(paramLong, localTransition2.getNameKey(), this.iStandardOffset, this.iStandardOffset); break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1064 */         localTransition1 = new Transition(paramLong, localTransition2);
/*      */         
/* 1066 */         i = localTransition2.getSaveMillis();
/*      */       }
/*      */       
/* 1069 */       this.iRules = localArrayList;
/* 1070 */       return localTransition1;
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
/*      */     public Transition nextTransition(long paramLong, int paramInt)
/*      */     {
/* 1085 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*      */       
/*      */ 
/* 1088 */       Object localObject = null;
/* 1089 */       long l1 = Long.MAX_VALUE;
/*      */       
/* 1091 */       Iterator localIterator = this.iRules.iterator();
/* 1092 */       while (localIterator.hasNext()) {
/* 1093 */         Rule localRule = (Rule)localIterator.next();
/* 1094 */         long l2 = localRule.next(paramLong, this.iStandardOffset, paramInt);
/* 1095 */         if (l2 <= paramLong) {
/* 1096 */           localIterator.remove();
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/* 1101 */         else if (l2 <= l1)
/*      */         {
/* 1103 */           localObject = localRule;
/* 1104 */           l1 = l2;
/*      */         }
/*      */       }
/*      */       
/* 1108 */       if (localObject == null) {
/* 1109 */         return null;
/*      */       }
/*      */       
/*      */ 
/* 1113 */       if (localISOChronology.year().get(l1) >= YEAR_LIMIT) {
/* 1114 */         return null;
/*      */       }
/*      */       
/*      */ 
/* 1118 */       if (this.iUpperYear < Integer.MAX_VALUE) {
/* 1119 */         long l3 = this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, paramInt);
/*      */         
/* 1121 */         if (l1 >= l3)
/*      */         {
/* 1123 */           return null;
/*      */         }
/*      */       }
/*      */       
/* 1127 */       return new Transition(l1, (Rule)localObject, this.iStandardOffset);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long getUpperLimit(int paramInt)
/*      */     {
/* 1134 */       if (this.iUpperYear == Integer.MAX_VALUE) {
/* 1135 */         return Long.MAX_VALUE;
/*      */       }
/* 1137 */       return this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, paramInt);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public DSTZone buildTailZone(String paramString)
/*      */     {
/* 1144 */       if (this.iRules.size() == 2) {
/* 1145 */         Rule localRule1 = (Rule)this.iRules.get(0);
/* 1146 */         Rule localRule2 = (Rule)this.iRules.get(1);
/* 1147 */         if ((localRule1.getToYear() == Integer.MAX_VALUE) && (localRule2.getToYear() == Integer.MAX_VALUE))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1157 */           return new DSTZone(paramString, this.iStandardOffset, localRule1.iRecurrence, localRule2.iRecurrence);
/*      */         }
/*      */       }
/*      */       
/* 1161 */       return null;
/*      */     } }
/*      */   
/*      */   private static final class DSTZone extends DateTimeZone { private static final long serialVersionUID = 6941492635554961361L;
/*      */     final int iStandardOffset;
/*      */     final Recurrence iStartRecurrence;
/*      */     final Recurrence iEndRecurrence;
/*      */     
/* 1169 */     static DSTZone readFrom(DataInput paramDataInput, String paramString) throws IOException { return new DSTZone(paramString, (int)DateTimeZoneBuilder.readMillis(paramDataInput), Recurrence.readFrom(paramDataInput), Recurrence.readFrom(paramDataInput)); }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     DSTZone(String paramString, int paramInt, Recurrence paramRecurrence1, Recurrence paramRecurrence2)
/*      */     {
/* 1179 */       super();
/* 1180 */       this.iStandardOffset = paramInt;
/* 1181 */       this.iStartRecurrence = paramRecurrence1;
/* 1182 */       this.iEndRecurrence = paramRecurrence2;
/*      */     }
/*      */     
/*      */     public String getNameKey(long paramLong) {
/* 1186 */       return findMatchingRecurrence(paramLong).getNameKey();
/*      */     }
/*      */     
/*      */     public int getOffset(long paramLong) {
/* 1190 */       return this.iStandardOffset + findMatchingRecurrence(paramLong).getSaveMillis();
/*      */     }
/*      */     
/*      */     public int getStandardOffset(long paramLong) {
/* 1194 */       return this.iStandardOffset;
/*      */     }
/*      */     
/*      */     public boolean isFixed() {
/* 1198 */       return false;
/*      */     }
/*      */     
/*      */     public long nextTransition(long paramLong) {
/* 1202 */       int i = this.iStandardOffset;
/* 1203 */       Recurrence localRecurrence1 = this.iStartRecurrence;
/* 1204 */       Recurrence localRecurrence2 = this.iEndRecurrence;
/*      */       
/*      */       long l1;
/*      */       try
/*      */       {
/* 1209 */         l1 = localRecurrence1.next(paramLong, i, localRecurrence2.getSaveMillis());
/*      */         
/* 1211 */         if ((paramLong > 0L) && (l1 < 0L))
/*      */         {
/* 1213 */           l1 = paramLong;
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException1) {
/* 1217 */         l1 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException1) {
/* 1220 */         l1 = paramLong;
/*      */       }
/*      */       long l2;
/*      */       try {
/* 1224 */         l2 = localRecurrence2.next(paramLong, i, localRecurrence1.getSaveMillis());
/*      */         
/* 1226 */         if ((paramLong > 0L) && (l2 < 0L))
/*      */         {
/* 1228 */           l2 = paramLong;
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException2) {
/* 1232 */         l2 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException2) {
/* 1235 */         l2 = paramLong;
/*      */       }
/*      */       
/* 1238 */       return l1 > l2 ? l2 : l1;
/*      */     }
/*      */     
/*      */ 
/*      */     public long previousTransition(long paramLong)
/*      */     {
/* 1244 */       paramLong += 1L;
/*      */       
/* 1246 */       int i = this.iStandardOffset;
/* 1247 */       Recurrence localRecurrence1 = this.iStartRecurrence;
/* 1248 */       Recurrence localRecurrence2 = this.iEndRecurrence;
/*      */       
/*      */       long l1;
/*      */       try
/*      */       {
/* 1253 */         l1 = localRecurrence1.previous(paramLong, i, localRecurrence2.getSaveMillis());
/*      */         
/* 1255 */         if ((paramLong < 0L) && (l1 > 0L))
/*      */         {
/* 1257 */           l1 = paramLong;
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException1) {
/* 1261 */         l1 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException1) {
/* 1264 */         l1 = paramLong;
/*      */       }
/*      */       long l2;
/*      */       try {
/* 1268 */         l2 = localRecurrence2.previous(paramLong, i, localRecurrence1.getSaveMillis());
/*      */         
/* 1270 */         if ((paramLong < 0L) && (l2 > 0L))
/*      */         {
/* 1272 */           l2 = paramLong;
/*      */         }
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException2) {
/* 1276 */         l2 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException2) {
/* 1279 */         l2 = paramLong;
/*      */       }
/*      */       
/* 1282 */       return (l1 > l2 ? l1 : l2) - 1L;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject) {
/* 1286 */       if (this == paramObject) {
/* 1287 */         return true;
/*      */       }
/* 1289 */       if ((paramObject instanceof DSTZone)) {
/* 1290 */         DSTZone localDSTZone = (DSTZone)paramObject;
/* 1291 */         return (getID().equals(localDSTZone.getID())) && (this.iStandardOffset == localDSTZone.iStandardOffset) && (this.iStartRecurrence.equals(localDSTZone.iStartRecurrence)) && (this.iEndRecurrence.equals(localDSTZone.iEndRecurrence));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1297 */       return false;
/*      */     }
/*      */     
/*      */     public void writeTo(DataOutput paramDataOutput) throws IOException {
/* 1301 */       DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iStandardOffset);
/* 1302 */       this.iStartRecurrence.writeTo(paramDataOutput);
/* 1303 */       this.iEndRecurrence.writeTo(paramDataOutput);
/*      */     }
/*      */     
/*      */     private Recurrence findMatchingRecurrence(long paramLong) {
/* 1307 */       int i = this.iStandardOffset;
/* 1308 */       Recurrence localRecurrence1 = this.iStartRecurrence;
/* 1309 */       Recurrence localRecurrence2 = this.iEndRecurrence;
/*      */       
/*      */       long l1;
/*      */       try
/*      */       {
/* 1314 */         l1 = localRecurrence1.next(paramLong, i, localRecurrence2.getSaveMillis());
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException1)
/*      */       {
/* 1318 */         l1 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException1) {
/* 1321 */         l1 = paramLong;
/*      */       }
/*      */       long l2;
/*      */       try {
/* 1325 */         l2 = localRecurrence2.next(paramLong, i, localRecurrence1.getSaveMillis());
/*      */       }
/*      */       catch (IllegalArgumentException localIllegalArgumentException2)
/*      */       {
/* 1329 */         l2 = paramLong;
/*      */       }
/*      */       catch (ArithmeticException localArithmeticException2) {
/* 1332 */         l2 = paramLong;
/*      */       }
/*      */       
/* 1335 */       return l1 > l2 ? localRecurrence1 : localRecurrence2; } }
/*      */   
/*      */   private static final class PrecalculatedZone extends DateTimeZone { private static final long serialVersionUID = 7811976468055766265L;
/*      */     private final long[] iTransitions;
/*      */     private final int[] iWallOffsets;
/*      */     private final int[] iStandardOffsets;
/*      */     private final String[] iNameKeys;
/*      */     private final DSTZone iTailZone;
/*      */     
/* 1344 */     static PrecalculatedZone readFrom(DataInput paramDataInput, String paramString) throws IOException { int i = paramDataInput.readUnsignedShort();
/* 1345 */       String[] arrayOfString1 = new String[i];
/* 1346 */       for (int j = 0; j < i; j++) {
/* 1347 */         arrayOfString1[j] = paramDataInput.readUTF();
/*      */       }
/*      */       
/* 1350 */       j = paramDataInput.readInt();
/* 1351 */       long[] arrayOfLong = new long[j];
/* 1352 */       int[] arrayOfInt1 = new int[j];
/* 1353 */       int[] arrayOfInt2 = new int[j];
/* 1354 */       String[] arrayOfString2 = new String[j];
/*      */       
/* 1356 */       for (int k = 0; k < j; k++) {
/* 1357 */         arrayOfLong[k] = DateTimeZoneBuilder.readMillis(paramDataInput);
/* 1358 */         arrayOfInt1[k] = ((int)DateTimeZoneBuilder.readMillis(paramDataInput));
/* 1359 */         arrayOfInt2[k] = ((int)DateTimeZoneBuilder.readMillis(paramDataInput));
/*      */         try {
/*      */           int m;
/* 1362 */           if (i < 256) {
/* 1363 */             m = paramDataInput.readUnsignedByte();
/*      */           } else {
/* 1365 */             m = paramDataInput.readUnsignedShort();
/*      */           }
/* 1367 */           arrayOfString2[k] = arrayOfString1[m];
/*      */         } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 1369 */           throw new IOException("Invalid encoding");
/*      */         }
/*      */       }
/*      */       
/* 1373 */       DSTZone localDSTZone = null;
/* 1374 */       if (paramDataInput.readBoolean()) {
/* 1375 */         localDSTZone = DSTZone.readFrom(paramDataInput, paramString);
/*      */       }
/*      */       
/* 1378 */       return new PrecalculatedZone(paramString, arrayOfLong, arrayOfInt1, arrayOfInt2, arrayOfString2, localDSTZone);
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
/*      */     static PrecalculatedZone create(String paramString, boolean paramBoolean, ArrayList<Transition> paramArrayList, DSTZone paramDSTZone)
/*      */     {
/* 1392 */       int i = paramArrayList.size();
/* 1393 */       if (i == 0) {
/* 1394 */         throw new IllegalArgumentException();
/*      */       }
/*      */       
/* 1397 */       long[] arrayOfLong = new long[i];
/* 1398 */       int[] arrayOfInt1 = new int[i];
/* 1399 */       int[] arrayOfInt2 = new int[i];
/* 1400 */       String[] arrayOfString = new String[i];
/*      */       
/* 1402 */       Object localObject1 = null;
/* 1403 */       for (int j = 0; j < i; j++) {
/* 1404 */         localObject3 = (Transition)paramArrayList.get(j);
/*      */         
/* 1406 */         if (!((Transition)localObject3).isTransitionFrom((Transition)localObject1)) {
/* 1407 */           throw new IllegalArgumentException(paramString);
/*      */         }
/*      */         
/* 1410 */         arrayOfLong[j] = ((Transition)localObject3).getMillis();
/* 1411 */         arrayOfInt1[j] = ((Transition)localObject3).getWallOffset();
/* 1412 */         arrayOfInt2[j] = ((Transition)localObject3).getStandardOffset();
/* 1413 */         arrayOfString[j] = ((Transition)localObject3).getNameKey();
/*      */         
/* 1415 */         localObject1 = localObject3;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1420 */       Object localObject2 = new String[5];
/* 1421 */       Object localObject3 = new DateFormatSymbols(Locale.ENGLISH).getZoneStrings();
/* 1422 */       for (int k = 0; k < localObject3.length; k++) {
/* 1423 */         Object localObject4 = localObject3[k];
/* 1424 */         if ((localObject4 != null) && (localObject4.length == 5) && (paramString.equals(localObject4[0]))) {
/* 1425 */           localObject2 = localObject4;
/*      */         }
/*      */       }
/*      */       
/* 1429 */       ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/*      */       
/* 1431 */       for (int m = 0; m < arrayOfString.length - 1; m++) {
/* 1432 */         String str1 = arrayOfString[m];
/* 1433 */         String str2 = arrayOfString[(m + 1)];
/* 1434 */         long l1 = arrayOfInt1[m];
/* 1435 */         long l2 = arrayOfInt1[(m + 1)];
/* 1436 */         long l3 = arrayOfInt2[m];
/* 1437 */         long l4 = arrayOfInt2[(m + 1)];
/* 1438 */         Period localPeriod = new Period(arrayOfLong[m], arrayOfLong[(m + 1)], PeriodType.yearMonthDay(), localISOChronology);
/* 1439 */         if ((l1 != l2) && (l3 == l4) && (str1.equals(str2)) && (localPeriod.getYears() == 0) && (localPeriod.getMonths() > 4) && (localPeriod.getMonths() < 8) && (str1.equals(localObject2[2])) && (str1.equals(localObject2[4])))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1446 */           if (ZoneInfoLogger.verbose()) {
/* 1447 */             System.out.println("Fixing duplicate name key - " + str2);
/* 1448 */             System.out.println("     - " + new DateTime(arrayOfLong[m], localISOChronology) + " - " + new DateTime(arrayOfLong[(m + 1)], localISOChronology));
/*      */           }
/*      */           
/* 1451 */           if (l1 > l2) {
/* 1452 */             arrayOfString[m] = (str1 + "-Summer").intern();
/* 1453 */           } else if (l1 < l2) {
/* 1454 */             arrayOfString[(m + 1)] = (str2 + "-Summer").intern();
/* 1455 */             m++;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1460 */       if ((paramDSTZone != null) && 
/* 1461 */         (paramDSTZone.iStartRecurrence.getNameKey().equals(paramDSTZone.iEndRecurrence.getNameKey())))
/*      */       {
/* 1463 */         if (ZoneInfoLogger.verbose()) {
/* 1464 */           System.out.println("Fixing duplicate recurrent name key - " + paramDSTZone.iStartRecurrence.getNameKey());
/*      */         }
/*      */         
/* 1467 */         if (paramDSTZone.iStartRecurrence.getSaveMillis() > 0) {
/* 1468 */           paramDSTZone = new DSTZone(paramDSTZone.getID(), paramDSTZone.iStandardOffset, paramDSTZone.iStartRecurrence.renameAppend("-Summer"), paramDSTZone.iEndRecurrence);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1474 */           paramDSTZone = new DSTZone(paramDSTZone.getID(), paramDSTZone.iStandardOffset, paramDSTZone.iStartRecurrence, paramDSTZone.iEndRecurrence.renameAppend("-Summer"));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1483 */       return new PrecalculatedZone(paramBoolean ? paramString : "", arrayOfLong, arrayOfInt1, arrayOfInt2, arrayOfString, paramDSTZone);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private PrecalculatedZone(String paramString, long[] paramArrayOfLong, int[] paramArrayOfInt1, int[] paramArrayOfInt2, String[] paramArrayOfString, DSTZone paramDSTZone)
/*      */     {
/* 1503 */       super();
/* 1504 */       this.iTransitions = paramArrayOfLong;
/* 1505 */       this.iWallOffsets = paramArrayOfInt1;
/* 1506 */       this.iStandardOffsets = paramArrayOfInt2;
/* 1507 */       this.iNameKeys = paramArrayOfString;
/* 1508 */       this.iTailZone = paramDSTZone;
/*      */     }
/*      */     
/*      */     public String getNameKey(long paramLong) {
/* 1512 */       long[] arrayOfLong = this.iTransitions;
/* 1513 */       int i = Arrays.binarySearch(arrayOfLong, paramLong);
/* 1514 */       if (i >= 0) {
/* 1515 */         return this.iNameKeys[i];
/*      */       }
/* 1517 */       i ^= 0xFFFFFFFF;
/* 1518 */       if (i < arrayOfLong.length) {
/* 1519 */         if (i > 0) {
/* 1520 */           return this.iNameKeys[(i - 1)];
/*      */         }
/* 1522 */         return "UTC";
/*      */       }
/* 1524 */       if (this.iTailZone == null) {
/* 1525 */         return this.iNameKeys[(i - 1)];
/*      */       }
/* 1527 */       return this.iTailZone.getNameKey(paramLong);
/*      */     }
/*      */     
/*      */     public int getOffset(long paramLong) {
/* 1531 */       long[] arrayOfLong = this.iTransitions;
/* 1532 */       int i = Arrays.binarySearch(arrayOfLong, paramLong);
/* 1533 */       if (i >= 0) {
/* 1534 */         return this.iWallOffsets[i];
/*      */       }
/* 1536 */       i ^= 0xFFFFFFFF;
/* 1537 */       if (i < arrayOfLong.length) {
/* 1538 */         if (i > 0) {
/* 1539 */           return this.iWallOffsets[(i - 1)];
/*      */         }
/* 1541 */         return 0;
/*      */       }
/* 1543 */       if (this.iTailZone == null) {
/* 1544 */         return this.iWallOffsets[(i - 1)];
/*      */       }
/* 1546 */       return this.iTailZone.getOffset(paramLong);
/*      */     }
/*      */     
/*      */     public int getStandardOffset(long paramLong) {
/* 1550 */       long[] arrayOfLong = this.iTransitions;
/* 1551 */       int i = Arrays.binarySearch(arrayOfLong, paramLong);
/* 1552 */       if (i >= 0) {
/* 1553 */         return this.iStandardOffsets[i];
/*      */       }
/* 1555 */       i ^= 0xFFFFFFFF;
/* 1556 */       if (i < arrayOfLong.length) {
/* 1557 */         if (i > 0) {
/* 1558 */           return this.iStandardOffsets[(i - 1)];
/*      */         }
/* 1560 */         return 0;
/*      */       }
/* 1562 */       if (this.iTailZone == null) {
/* 1563 */         return this.iStandardOffsets[(i - 1)];
/*      */       }
/* 1565 */       return this.iTailZone.getStandardOffset(paramLong);
/*      */     }
/*      */     
/*      */     public boolean isFixed() {
/* 1569 */       return false;
/*      */     }
/*      */     
/*      */     public long nextTransition(long paramLong) {
/* 1573 */       long[] arrayOfLong = this.iTransitions;
/* 1574 */       int i = Arrays.binarySearch(arrayOfLong, paramLong);
/* 1575 */       i = i >= 0 ? i + 1 : i ^ 0xFFFFFFFF;
/* 1576 */       if (i < arrayOfLong.length) {
/* 1577 */         return arrayOfLong[i];
/*      */       }
/* 1579 */       if (this.iTailZone == null) {
/* 1580 */         return paramLong;
/*      */       }
/* 1582 */       long l = arrayOfLong[(arrayOfLong.length - 1)];
/* 1583 */       if (paramLong < l) {
/* 1584 */         paramLong = l;
/*      */       }
/* 1586 */       return this.iTailZone.nextTransition(paramLong);
/*      */     }
/*      */     
/*      */     public long previousTransition(long paramLong) {
/* 1590 */       long[] arrayOfLong = this.iTransitions;
/* 1591 */       int i = Arrays.binarySearch(arrayOfLong, paramLong);
/* 1592 */       if (i >= 0) {
/* 1593 */         if (paramLong > Long.MIN_VALUE) {
/* 1594 */           return paramLong - 1L;
/*      */         }
/* 1596 */         return paramLong;
/*      */       }
/* 1598 */       i ^= 0xFFFFFFFF;
/* 1599 */       if (i < arrayOfLong.length) {
/* 1600 */         if (i > 0) {
/* 1601 */           l = arrayOfLong[(i - 1)];
/* 1602 */           if (l > Long.MIN_VALUE) {
/* 1603 */             return l - 1L;
/*      */           }
/*      */         }
/* 1606 */         return paramLong;
/*      */       }
/* 1608 */       if (this.iTailZone != null) {
/* 1609 */         l = this.iTailZone.previousTransition(paramLong);
/* 1610 */         if (l < paramLong) {
/* 1611 */           return l;
/*      */         }
/*      */       }
/* 1614 */       long l = arrayOfLong[(i - 1)];
/* 1615 */       if (l > Long.MIN_VALUE) {
/* 1616 */         return l - 1L;
/*      */       }
/* 1618 */       return paramLong;
/*      */     }
/*      */     
/*      */     public boolean equals(Object paramObject) {
/* 1622 */       if (this == paramObject) {
/* 1623 */         return true;
/*      */       }
/* 1625 */       if ((paramObject instanceof PrecalculatedZone)) {
/* 1626 */         PrecalculatedZone localPrecalculatedZone = (PrecalculatedZone)paramObject;
/* 1627 */         return (getID().equals(localPrecalculatedZone.getID())) && (Arrays.equals(this.iTransitions, localPrecalculatedZone.iTransitions)) && (Arrays.equals(this.iNameKeys, localPrecalculatedZone.iNameKeys)) && (Arrays.equals(this.iWallOffsets, localPrecalculatedZone.iWallOffsets)) && (Arrays.equals(this.iStandardOffsets, localPrecalculatedZone.iStandardOffsets)) && (this.iTailZone == null ? null == localPrecalculatedZone.iTailZone : this.iTailZone.equals(localPrecalculatedZone.iTailZone));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1637 */       return false;
/*      */     }
/*      */     
/*      */     public void writeTo(DataOutput paramDataOutput) throws IOException {
/* 1641 */       int i = this.iTransitions.length;
/*      */       
/*      */ 
/* 1644 */       HashSet localHashSet = new HashSet();
/* 1645 */       for (int j = 0; j < i; j++) {
/* 1646 */         localHashSet.add(this.iNameKeys[j]);
/*      */       }
/*      */       
/* 1649 */       j = localHashSet.size();
/* 1650 */       if (j > 65535) {
/* 1651 */         throw new UnsupportedOperationException("String pool is too large");
/*      */       }
/* 1653 */       String[] arrayOfString = new String[j];
/* 1654 */       Iterator localIterator = localHashSet.iterator();
/* 1655 */       for (int k = 0; localIterator.hasNext(); k++) {
/* 1656 */         arrayOfString[k] = ((String)localIterator.next());
/*      */       }
/*      */       
/*      */ 
/* 1660 */       paramDataOutput.writeShort(j);
/* 1661 */       for (k = 0; k < j; k++) {
/* 1662 */         paramDataOutput.writeUTF(arrayOfString[k]);
/*      */       }
/*      */       
/* 1665 */       paramDataOutput.writeInt(i);
/*      */       
/* 1667 */       for (k = 0; k < i; k++) {
/* 1668 */         DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iTransitions[k]);
/* 1669 */         DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iWallOffsets[k]);
/* 1670 */         DateTimeZoneBuilder.writeMillis(paramDataOutput, this.iStandardOffsets[k]);
/*      */         
/*      */ 
/* 1673 */         String str = this.iNameKeys[k];
/* 1674 */         for (int m = 0; m < j; m++) {
/* 1675 */           if (arrayOfString[m].equals(str)) {
/* 1676 */             if (j < 256) {
/* 1677 */               paramDataOutput.writeByte(m); break;
/*      */             }
/* 1679 */             paramDataOutput.writeShort(m);
/*      */             
/* 1681 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1686 */       paramDataOutput.writeBoolean(this.iTailZone != null);
/* 1687 */       if (this.iTailZone != null) {
/* 1688 */         this.iTailZone.writeTo(paramDataOutput);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean isCachable() {
/* 1693 */       if (this.iTailZone != null) {
/* 1694 */         return true;
/*      */       }
/* 1696 */       long[] arrayOfLong = this.iTransitions;
/* 1697 */       if (arrayOfLong.length <= 1) {
/* 1698 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1703 */       double d1 = 0.0D;
/* 1704 */       int i = 0;
/*      */       
/* 1706 */       for (int j = 1; j < arrayOfLong.length; j++) {
/* 1707 */         long l = arrayOfLong[j] - arrayOfLong[(j - 1)];
/* 1708 */         if (l < 63158400000L) {
/* 1709 */           d1 += l;
/* 1710 */           i++;
/*      */         }
/*      */       }
/*      */       
/* 1714 */       if (i > 0) {
/* 1715 */         double d2 = d1 / i;
/* 1716 */         d2 /= 8.64E7D;
/* 1717 */         if (d2 >= 25.0D)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1724 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 1728 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\DateTimeZoneBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */