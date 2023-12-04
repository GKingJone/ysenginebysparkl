/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Murmur3Hash32
/*     */ {
/*     */   private static final int C1 = -862048943;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int C2 = 461845907;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int DEFAULT_SEED = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int hash(Slice data)
/*     */   {
/*  27 */     return hash(data, 0, data.length());
/*     */   }
/*     */   
/*     */   public static int hash(Slice data, int offset, int length)
/*     */   {
/*  32 */     return hash(0, data, offset, length);
/*     */   }
/*     */   
/*     */   public static int hash(int seed, Slice data, int offset, int length)
/*     */   {
/*  37 */     int fastLimit = offset + length - 4 + 1;
/*     */     
/*  39 */     int h1 = seed;
/*     */     
/*  41 */     int current = offset;
/*  42 */     while (current < fastLimit) {
/*  43 */       int k1 = mixK1(data.getInt(current));
/*  44 */       current += 4;
/*  45 */       h1 = mixH1(h1, k1);
/*     */     }
/*     */     
/*  48 */     int k1 = 0;
/*     */     
/*  50 */     switch (length & 0x3) {
/*     */     case 3: 
/*  52 */       k1 ^= data.getUnsignedByte(current + 2) << 16;
/*     */     case 2: 
/*  54 */       k1 ^= data.getUnsignedByte(current + 1) << 8;
/*     */     case 1: 
/*  56 */       k1 ^= data.getUnsignedByte(current + 0) << 0;
/*     */     }
/*     */     
/*  59 */     h1 ^= mixK1(k1);
/*     */     
/*  61 */     return fmix(h1, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int hash(int input)
/*     */   {
/*  69 */     int k1 = mixK1(input);
/*  70 */     int h1 = mixH1(0, k1);
/*     */     
/*  72 */     return fmix(h1, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int hash(long input)
/*     */   {
/*  80 */     int low = (int)input;
/*  81 */     int high = (int)(input >>> 32);
/*     */     
/*  83 */     int k1 = mixK1(low);
/*  84 */     int h1 = mixH1(0, k1);
/*     */     
/*  86 */     k1 = mixK1(high);
/*  87 */     h1 = mixH1(h1, k1);
/*     */     
/*  89 */     return fmix(h1, 8);
/*     */   }
/*     */   
/*     */   private static int mixK1(int k1)
/*     */   {
/*  94 */     k1 *= -862048943;
/*  95 */     k1 = Integer.rotateLeft(k1, 15);
/*  96 */     k1 *= 461845907;
/*  97 */     return k1;
/*     */   }
/*     */   
/*     */   private static int mixH1(int h1, int k1)
/*     */   {
/* 102 */     h1 ^= k1;
/* 103 */     h1 = Integer.rotateLeft(h1, 13);
/* 104 */     h1 = h1 * 5 + -430675100;
/* 105 */     return h1;
/*     */   }
/*     */   
/*     */   private static int fmix(int h1, int length)
/*     */   {
/* 110 */     h1 ^= length;
/* 111 */     h1 ^= h1 >>> 16;
/* 112 */     h1 *= -2048144789;
/* 113 */     h1 ^= h1 >>> 13;
/* 114 */     h1 *= -1028477387;
/* 115 */     h1 ^= h1 >>> 16;
/* 116 */     return h1;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Murmur3Hash32.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */