/*     */ package com.google.common.math;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DoubleUtils
/*     */ {
/*     */   static final long SIGNIFICAND_MASK = 4503599627370495L;
/*     */   static final long EXPONENT_MASK = 9218868437227405312L;
/*     */   static final long SIGN_MASK = Long.MIN_VALUE;
/*     */   static final int SIGNIFICAND_BITS = 52;
/*     */   static final int EXPONENT_BIAS = 1023;
/*     */   static final long IMPLICIT_BIT = 4503599627370496L;
/*     */   
/*     */   static double nextDown(double d)
/*     */   {
/*  40 */     return -Math.nextUp(-d);
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
/*     */   static long getSignificand(double d)
/*     */   {
/*  65 */     Preconditions.checkArgument(isFinite(d), "not a normal value");
/*  66 */     int exponent = Math.getExponent(d);
/*  67 */     long bits = Double.doubleToRawLongBits(d);
/*  68 */     bits &= 0xFFFFFFFFFFFFF;
/*  69 */     return exponent == 64513 ? bits << 1 : bits | 0x10000000000000;
/*     */   }
/*     */   
/*     */ 
/*     */   static boolean isFinite(double d)
/*     */   {
/*  75 */     return Math.getExponent(d) <= 1023;
/*     */   }
/*     */   
/*     */   static boolean isNormal(double d) {
/*  79 */     return Math.getExponent(d) >= 64514;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static double scaleNormalize(double x)
/*     */   {
/*  87 */     long significand = Double.doubleToRawLongBits(x) & 0xFFFFFFFFFFFFF;
/*  88 */     return Double.longBitsToDouble(significand | ONE_BITS);
/*     */   }
/*     */   
/*     */   static double bigToDouble(BigInteger x)
/*     */   {
/*  93 */     BigInteger absX = x.abs();
/*  94 */     int exponent = absX.bitLength() - 1;
/*     */     
/*  96 */     if (exponent < 63)
/*  97 */       return x.longValue();
/*  98 */     if (exponent > 1023) {
/*  99 */       return x.signum() * Double.POSITIVE_INFINITY;
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
/* 110 */     int shift = exponent - 52 - 1;
/* 111 */     long twiceSignifFloor = absX.shiftRight(shift).longValue();
/* 112 */     long signifFloor = twiceSignifFloor >> 1;
/* 113 */     signifFloor &= 0xFFFFFFFFFFFFF;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     boolean increment = ((twiceSignifFloor & 1L) != 0L) && (((signifFloor & 1L) != 0L) || (absX.getLowestSetBit() < shift));
/*     */     
/* 122 */     long signifRounded = increment ? signifFloor + 1L : signifFloor;
/* 123 */     long bits = exponent + 1023 << 52;
/* 124 */     bits += signifRounded;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */     bits |= x.signum() & 0x8000000000000000;
/* 132 */     return Double.longBitsToDouble(bits);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static double ensureNonNegative(double value)
/*     */   {
/* 139 */     Preconditions.checkArgument(!Double.isNaN(value));
/* 140 */     if (value > 0.0D) {
/* 141 */       return value;
/*     */     }
/* 143 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/* 147 */   private static final long ONE_BITS = Double.doubleToRawLongBits(1.0D);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\math\DoubleUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */