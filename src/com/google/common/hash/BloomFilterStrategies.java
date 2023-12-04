/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.math.LongMath;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */  enum BloomFilterStrategies
/*     */   implements BloomFilter.Strategy
/*     */ {
/*  42 */   MURMUR128_MITZ_32;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private BloomFilterStrategies() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static class BitArray
/*     */   {
/*     */     final long[] data;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     int bitCount;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     BitArray(long bits)
/*     */     {
/*  83 */       this(new long[Ints.checkedCast(LongMath.divide(bits, 64L, RoundingMode.CEILING))]);
/*     */     }
/*     */     
/*     */     BitArray(long[] data)
/*     */     {
/*  88 */       Preconditions.checkArgument(data.length > 0, "data length is zero!");
/*  89 */       this.data = data;
/*  90 */       int bitCount = 0;
/*  91 */       for (long value : data) {
/*  92 */         bitCount += Long.bitCount(value);
/*     */       }
/*  94 */       this.bitCount = bitCount;
/*     */     }
/*     */     
/*     */     boolean set(int index)
/*     */     {
/*  99 */       if (!get(index)) {
/* 100 */         this.data[(index >> 6)] |= 1L << index;
/* 101 */         this.bitCount += 1;
/* 102 */         return true;
/*     */       }
/* 104 */       return false;
/*     */     }
/*     */     
/*     */     boolean get(int index) {
/* 108 */       return (this.data[(index >> 6)] & 1L << index) != 0L;
/*     */     }
/*     */     
/*     */     int bitSize()
/*     */     {
/* 113 */       return this.data.length * 64;
/*     */     }
/*     */     
/*     */     int bitCount()
/*     */     {
/* 118 */       return this.bitCount;
/*     */     }
/*     */     
/*     */     BitArray copy() {
/* 122 */       return new BitArray((long[])this.data.clone());
/*     */     }
/*     */     
/*     */     void putAll(BitArray array)
/*     */     {
/* 127 */       Preconditions.checkArgument(this.data.length == array.data.length, "BitArrays must be of equal length (%s != %s)", new Object[] { Integer.valueOf(this.data.length), Integer.valueOf(array.data.length) });
/*     */       
/* 129 */       this.bitCount = 0;
/* 130 */       for (int i = 0; i < this.data.length; i++) {
/* 131 */         this.data[i] |= array.data[i];
/* 132 */         this.bitCount += Long.bitCount(this.data[i]);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 137 */       if ((o instanceof BitArray)) {
/* 138 */         BitArray bitArray = (BitArray)o;
/* 139 */         return Arrays.equals(this.data, bitArray.data);
/*     */       }
/* 141 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 145 */       return Arrays.hashCode(this.data);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\hash\BloomFilterStrategies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */