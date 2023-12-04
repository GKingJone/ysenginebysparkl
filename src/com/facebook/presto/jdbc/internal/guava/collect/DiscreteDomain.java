/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public abstract class DiscreteDomain<C extends Comparable>
/*     */ {
/*     */   public static DiscreteDomain<Integer> integers()
/*     */   {
/*  54 */     return IntegerDomain.INSTANCE;
/*     */   }
/*     */   
/*     */   private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable
/*     */   {
/*  59 */     private static final IntegerDomain INSTANCE = new IntegerDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*  62 */     public Integer next(Integer value) { int i = value.intValue();
/*  63 */       return i == Integer.MAX_VALUE ? null : Integer.valueOf(i + 1);
/*     */     }
/*     */     
/*     */     public Integer previous(Integer value) {
/*  67 */       int i = value.intValue();
/*  68 */       return i == Integer.MIN_VALUE ? null : Integer.valueOf(i - 1);
/*     */     }
/*     */     
/*     */     public long distance(Integer start, Integer end) {
/*  72 */       return end.intValue() - start.intValue();
/*     */     }
/*     */     
/*     */     public Integer minValue() {
/*  76 */       return Integer.valueOf(Integer.MIN_VALUE);
/*     */     }
/*     */     
/*     */     public Integer maxValue() {
/*  80 */       return Integer.valueOf(Integer.MAX_VALUE);
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/*  84 */       return INSTANCE;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/*  89 */       return "DiscreteDomain.integers()";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DiscreteDomain<Long> longs()
/*     */   {
/* 101 */     return LongDomain.INSTANCE;
/*     */   }
/*     */   
/*     */   private static final class LongDomain extends DiscreteDomain<Long> implements Serializable
/*     */   {
/* 106 */     private static final LongDomain INSTANCE = new LongDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 109 */     public Long next(Long value) { long l = value.longValue();
/* 110 */       return l == Long.MAX_VALUE ? null : Long.valueOf(l + 1L);
/*     */     }
/*     */     
/*     */     public Long previous(Long value) {
/* 114 */       long l = value.longValue();
/* 115 */       return l == Long.MIN_VALUE ? null : Long.valueOf(l - 1L);
/*     */     }
/*     */     
/*     */     public long distance(Long start, Long end) {
/* 119 */       long result = end.longValue() - start.longValue();
/* 120 */       if ((end.longValue() > start.longValue()) && (result < 0L)) {
/* 121 */         return Long.MAX_VALUE;
/*     */       }
/* 123 */       if ((end.longValue() < start.longValue()) && (result > 0L)) {
/* 124 */         return Long.MIN_VALUE;
/*     */       }
/* 126 */       return result;
/*     */     }
/*     */     
/*     */     public Long minValue() {
/* 130 */       return Long.valueOf(Long.MIN_VALUE);
/*     */     }
/*     */     
/*     */     public Long maxValue() {
/* 134 */       return Long.valueOf(Long.MAX_VALUE);
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/* 138 */       return INSTANCE;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 143 */       return "DiscreteDomain.longs()";
/*     */     }
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
/* 155 */   public static DiscreteDomain<BigInteger> bigIntegers() { return BigIntegerDomain.INSTANCE; }
/*     */   
/*     */   public abstract C next(C paramC);
/*     */   
/*     */   private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
/* 160 */     private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
/*     */     
/* 162 */     private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
/*     */     
/* 164 */     private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public BigInteger next(BigInteger value) {
/* 168 */       return value.add(BigInteger.ONE);
/*     */     }
/*     */     
/*     */     public BigInteger previous(BigInteger value) {
/* 172 */       return value.subtract(BigInteger.ONE);
/*     */     }
/*     */     
/*     */     public long distance(BigInteger start, BigInteger end) {
/* 176 */       return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/* 180 */       return INSTANCE;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 185 */       return "DiscreteDomain.bigIntegers()";
/*     */     }
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
/*     */   public abstract C previous(C paramC);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long distance(C paramC1, C paramC2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public C minValue()
/*     */   {
/* 245 */     throw new NoSuchElementException();
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
/*     */   public C maxValue()
/*     */   {
/* 260 */     throw new NoSuchElementException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\DiscreteDomain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */