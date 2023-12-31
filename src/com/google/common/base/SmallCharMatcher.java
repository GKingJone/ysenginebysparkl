/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import java.util.BitSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtIncompatible("no precomputation is done in GWT")
/*     */ final class SmallCharMatcher
/*     */   extends CharMatcher.FastMatcher
/*     */ {
/*     */   static final int MAX_SIZE = 1023;
/*     */   private final char[] table;
/*     */   private final boolean containsZero;
/*     */   private final long filter;
/*     */   private static final int C1 = -862048943;
/*     */   private static final int C2 = 461845907;
/*     */   private static final double DESIRED_LOAD_FACTOR = 0.5D;
/*     */   
/*     */   private SmallCharMatcher(char[] table, long filter, boolean containsZero, String description)
/*     */   {
/*  40 */     super(description);
/*  41 */     this.table = table;
/*  42 */     this.filter = filter;
/*  43 */     this.containsZero = containsZero;
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
/*     */   static int smear(int hashCode)
/*     */   {
/*  58 */     return 461845907 * Integer.rotateLeft(hashCode * -862048943, 15);
/*     */   }
/*     */   
/*     */   private boolean checkFilter(int c) {
/*  62 */     return 1L == (1L & this.filter >> c);
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
/*     */   @VisibleForTesting
/*     */   static int chooseTableSize(int setSize)
/*     */   {
/*  78 */     if (setSize == 1) {
/*  79 */       return 2;
/*     */     }
/*     */     
/*     */ 
/*  83 */     int tableSize = Integer.highestOneBit(setSize - 1) << 1;
/*  84 */     while (tableSize * 0.5D < setSize) {
/*  85 */       tableSize <<= 1;
/*     */     }
/*  87 */     return tableSize;
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.util.BitSet")
/*     */   static CharMatcher from(BitSet chars, String description)
/*     */   {
/*  93 */     long filter = 0L;
/*  94 */     int size = chars.cardinality();
/*  95 */     boolean containsZero = chars.get(0);
/*     */     
/*  97 */     char[] table = new char[chooseTableSize(size)];
/*  98 */     int mask = table.length - 1;
/*  99 */     for (int c = chars.nextSetBit(0); c != -1; c = chars.nextSetBit(c + 1))
/*     */     {
/* 101 */       filter |= 1L << c;
/* 102 */       int index = smear(c) & mask;
/*     */       for (;;)
/*     */       {
/* 105 */         if (table[index] == 0) {
/* 106 */           table[index] = ((char)c);
/* 107 */           break;
/*     */         }
/*     */         
/* 110 */         index = index + 1 & mask;
/*     */       }
/*     */     }
/* 113 */     return new SmallCharMatcher(table, filter, containsZero, description);
/*     */   }
/*     */   
/*     */   public boolean matches(char c)
/*     */   {
/* 118 */     if (c == 0) {
/* 119 */       return this.containsZero;
/*     */     }
/* 121 */     if (!checkFilter(c)) {
/* 122 */       return false;
/*     */     }
/* 124 */     int mask = this.table.length - 1;
/* 125 */     int startingIndex = smear(c) & mask;
/* 126 */     int index = startingIndex;
/*     */     do
/*     */     {
/* 129 */       if (this.table[index] == 0) {
/* 130 */         return false;
/*     */       }
/* 132 */       if (this.table[index] == c) {
/* 133 */         return true;
/*     */       }
/*     */       
/* 136 */       index = index + 1 & mask;
/*     */ 
/*     */     }
/* 139 */     while (index != startingIndex);
/* 140 */     return false;
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.util.BitSet")
/*     */   void setBits(BitSet table)
/*     */   {
/* 146 */     if (this.containsZero) {
/* 147 */       table.set(0);
/*     */     }
/* 149 */     for (char c : this.table) {
/* 150 */       if (c != 0) {
/* 151 */         table.set(c);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\SmallCharMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */