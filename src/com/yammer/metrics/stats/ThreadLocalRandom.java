/*     */ package com.yammer.metrics.stats;
/*     */ 
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ThreadLocalRandom
/*     */   extends Random
/*     */ {
/*     */   private static final long multiplier = 25214903917L;
/*     */   private static final long addend = 11L;
/*     */   private static final long mask = 281474976710655L;
/*     */   private long rnd;
/*     */   boolean initialized;
/*     */   private long pad0;
/*     */   private long pad1;
/*     */   private long pad2;
/*     */   private long pad3;
/*     */   private long pad4;
/*     */   private long pad5;
/*     */   private long pad6;
/*     */   private long pad7;
/*  44 */   private static final ThreadLocal<ThreadLocalRandom> localRandom = new ThreadLocal()
/*     */   {
/*     */     protected ThreadLocalRandom initialValue() {
/*  47 */       return new ThreadLocalRandom();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */   private static final long serialVersionUID = -5851777807851030925L;
/*     */   
/*     */ 
/*     */   ThreadLocalRandom()
/*     */   {
/*  57 */     this.initialized = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ThreadLocalRandom current()
/*     */   {
/*  66 */     return (ThreadLocalRandom)localRandom.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSeed(long seed)
/*     */   {
/*  76 */     if (this.initialized)
/*  77 */       throw new UnsupportedOperationException();
/*  78 */     this.rnd = ((seed ^ 0x5DEECE66D) & 0xFFFFFFFFFFFF);
/*     */   }
/*     */   
/*     */   protected int next(int bits) {
/*  82 */     this.rnd = (this.rnd * 25214903917L + 11L & 0xFFFFFFFFFFFF);
/*  83 */     return (int)(this.rnd >>> 48 - bits);
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
/*     */   public int nextInt(int least, int bound)
/*     */   {
/*  96 */     if (least >= bound)
/*  97 */       throw new IllegalArgumentException();
/*  98 */     return nextInt(bound - least) + least;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long nextLong(long n)
/*     */   {
/* 110 */     if (n <= 0L) {
/* 111 */       throw new IllegalArgumentException("n must be positive");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 117 */     long offset = 0L;
/* 118 */     while (n >= 2147483647L) {
/* 119 */       int bits = next(2);
/* 120 */       long half = n >>> 1;
/* 121 */       long nextn = (bits & 0x2) == 0 ? half : n - half;
/* 122 */       if ((bits & 0x1) == 0)
/* 123 */         offset += n - nextn;
/* 124 */       n = nextn;
/*     */     }
/* 126 */     return offset + nextInt((int)n);
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
/*     */   public long nextLong(long least, long bound)
/*     */   {
/* 139 */     if (least >= bound)
/* 140 */       throw new IllegalArgumentException();
/* 141 */     return nextLong(bound - least) + least;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double nextDouble(double n)
/*     */   {
/* 153 */     if (n <= 0.0D)
/* 154 */       throw new IllegalArgumentException("n must be positive");
/* 155 */     return nextDouble() * n;
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
/*     */   public double nextDouble(double least, double bound)
/*     */   {
/* 168 */     if (least >= bound)
/* 169 */       throw new IllegalArgumentException();
/* 170 */     return nextDouble() * (bound - least) + least;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\ThreadLocalRandom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */