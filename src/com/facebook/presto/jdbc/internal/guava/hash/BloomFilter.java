/*     */ package com.facebook.presto.jdbc.internal.guava.hash;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.SignedBytes;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.UnsignedBytes;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public final class BloomFilter<T>
/*     */   implements Predicate<T>, Serializable
/*     */ {
/*     */   private final BloomFilterStrategies.BitArray bits;
/*     */   private final int numHashFunctions;
/*     */   private final Funnel<? super T> funnel;
/*     */   private final Strategy strategy;
/*     */   
/*     */   private BloomFilter(BloomFilterStrategies.BitArray bits, int numHashFunctions, Funnel<? super T> funnel, Strategy strategy)
/*     */   {
/* 113 */     Preconditions.checkArgument(numHashFunctions > 0, "numHashFunctions (%s) must be > 0", new Object[] { Integer.valueOf(numHashFunctions) });
/*     */     
/* 115 */     Preconditions.checkArgument(numHashFunctions <= 255, "numHashFunctions (%s) must be <= 255", new Object[] { Integer.valueOf(numHashFunctions) });
/*     */     
/* 117 */     this.bits = ((BloomFilterStrategies.BitArray)Preconditions.checkNotNull(bits));
/* 118 */     this.numHashFunctions = numHashFunctions;
/* 119 */     this.funnel = ((Funnel)Preconditions.checkNotNull(funnel));
/* 120 */     this.strategy = ((Strategy)Preconditions.checkNotNull(strategy));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BloomFilter<T> copy()
/*     */   {
/* 130 */     return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean mightContain(T object)
/*     */   {
/* 138 */     return this.strategy.mightContain(object, this.funnel, this.numHashFunctions, this.bits);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public boolean apply(T input)
/*     */   {
/* 148 */     return mightContain(input);
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
/*     */   public boolean put(T object)
/*     */   {
/* 164 */     return this.strategy.put(object, this.funnel, this.numHashFunctions, this.bits);
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
/*     */   public double expectedFpp()
/*     */   {
/* 180 */     return Math.pow(this.bits.bitCount() / bitSize(), this.numHashFunctions);
/*     */   }
/*     */   
/*     */ 
/*     */   @VisibleForTesting
/*     */   long bitSize()
/*     */   {
/* 187 */     return this.bits.bitSize();
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
/*     */   public boolean isCompatible(BloomFilter<T> that)
/*     */   {
/* 206 */     Preconditions.checkNotNull(that);
/* 207 */     return (this != that) && (this.numHashFunctions == that.numHashFunctions) && (bitSize() == that.bitSize()) && (this.strategy.equals(that.strategy)) && (this.funnel.equals(that.funnel));
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
/*     */   public void putAll(BloomFilter<T> that)
/*     */   {
/* 225 */     Preconditions.checkNotNull(that);
/* 226 */     Preconditions.checkArgument(this != that, "Cannot combine a BloomFilter with itself.");
/* 227 */     Preconditions.checkArgument(this.numHashFunctions == that.numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", new Object[] { Integer.valueOf(this.numHashFunctions), Integer.valueOf(that.numHashFunctions) });
/*     */     
/*     */ 
/* 230 */     Preconditions.checkArgument(bitSize() == that.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", new Object[] { Long.valueOf(bitSize()), Long.valueOf(that.bitSize()) });
/*     */     
/*     */ 
/* 233 */     Preconditions.checkArgument(this.strategy.equals(that.strategy), "BloomFilters must have equal strategies (%s != %s)", new Object[] { this.strategy, that.strategy });
/*     */     
/*     */ 
/* 236 */     Preconditions.checkArgument(this.funnel.equals(that.funnel), "BloomFilters must have equal funnels (%s != %s)", new Object[] { this.funnel, that.funnel });
/*     */     
/*     */ 
/* 239 */     this.bits.putAll(that.bits);
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 244 */     if (object == this) {
/* 245 */       return true;
/*     */     }
/* 247 */     if ((object instanceof BloomFilter)) {
/* 248 */       BloomFilter<?> that = (BloomFilter)object;
/* 249 */       return (this.numHashFunctions == that.numHashFunctions) && (this.funnel.equals(that.funnel)) && (this.bits.equals(that.bits)) && (this.strategy.equals(that.strategy));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 254 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 259 */     return Objects.hashCode(new Object[] { Integer.valueOf(this.numHashFunctions), this.funnel, this.strategy, this.bits });
/*     */   }
/*     */   
/* 262 */   private static final Strategy DEFAULT_STRATEGY = BloomFilterStrategies.MURMUR128_MITZ_64;
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
/*     */   public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions, double fpp)
/*     */   {
/* 288 */     return create(funnel, expectedInsertions, fpp, DEFAULT_STRATEGY);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions, double fpp, Strategy strategy)
/*     */   {
/* 294 */     Preconditions.checkNotNull(funnel);
/* 295 */     Preconditions.checkArgument(expectedInsertions >= 0, "Expected insertions (%s) must be >= 0", new Object[] { Integer.valueOf(expectedInsertions) });
/*     */     
/* 297 */     Preconditions.checkArgument(fpp > 0.0D, "False positive probability (%s) must be > 0.0", new Object[] { Double.valueOf(fpp) });
/* 298 */     Preconditions.checkArgument(fpp < 1.0D, "False positive probability (%s) must be < 1.0", new Object[] { Double.valueOf(fpp) });
/* 299 */     Preconditions.checkNotNull(strategy);
/*     */     
/* 301 */     if (expectedInsertions == 0) {
/* 302 */       expectedInsertions = 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 310 */     long numBits = optimalNumOfBits(expectedInsertions, fpp);
/* 311 */     int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
/*     */     try {
/* 313 */       return new BloomFilter(new BloomFilterStrategies.BitArray(numBits), numHashFunctions, funnel, strategy);
/*     */     } catch (IllegalArgumentException e) {
/* 315 */       long l1 = numBits;throw new IllegalArgumentException(57 + "Could not create BloomFilter of " + l1 + " bits", e);
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
/*     */   public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions)
/*     */   {
/* 337 */     return create(funnel, expectedInsertions, 0.03D);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int optimalNumOfHashFunctions(long n, long m)
/*     */   {
/* 366 */     return Math.max(1, (int)Math.round(m / n * Math.log(2.0D)));
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
/*     */   @VisibleForTesting
/*     */   static long optimalNumOfBits(long n, double p)
/*     */   {
/* 380 */     if (p == 0.0D) {
/* 381 */       p = Double.MIN_VALUE;
/*     */     }
/* 383 */     return (-n * Math.log(p) / (Math.log(2.0D) * Math.log(2.0D)));
/*     */   }
/*     */   
/*     */ 
/* 387 */   private Object writeReplace() { return new SerialForm(this); }
/*     */   
/*     */   private static class SerialForm<T> implements Serializable {
/*     */     final long[] data;
/*     */     final int numHashFunctions;
/*     */     final Funnel<? super T> funnel;
/*     */     final Strategy strategy;
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     SerialForm(BloomFilter<T> bf) {
/* 397 */       this.data = bf.bits.data;
/* 398 */       this.numHashFunctions = bf.numHashFunctions;
/* 399 */       this.funnel = bf.funnel;
/* 400 */       this.strategy = bf.strategy;
/*     */     }
/*     */     
/* 403 */     Object readResolve() { return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy, null); }
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
/*     */   public void writeTo(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 423 */     DataOutputStream dout = new DataOutputStream(out);
/* 424 */     dout.writeByte(SignedBytes.checkedCast(this.strategy.ordinal()));
/* 425 */     dout.writeByte(UnsignedBytes.checkedCast(this.numHashFunctions));
/* 426 */     dout.writeInt(this.bits.data.length);
/* 427 */     for (long value : this.bits.data) {
/* 428 */       dout.writeLong(value);
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
/*     */   public static <T> BloomFilter<T> readFrom(InputStream in, Funnel<T> funnel)
/*     */     throws IOException
/*     */   {
/* 445 */     Preconditions.checkNotNull(in, "InputStream");
/* 446 */     Preconditions.checkNotNull(funnel, "Funnel");
/* 447 */     int strategyOrdinal = -1;
/* 448 */     int numHashFunctions = -1;
/* 449 */     int dataLength = -1;
/*     */     try {
/* 451 */       DataInputStream din = new DataInputStream(in);
/*     */       
/*     */ 
/*     */ 
/* 455 */       strategyOrdinal = din.readByte();
/* 456 */       numHashFunctions = UnsignedBytes.toInt(din.readByte());
/* 457 */       dataLength = din.readInt();
/*     */       
/* 459 */       Strategy strategy = BloomFilterStrategies.values()[strategyOrdinal];
/* 460 */       data = new long[dataLength];
/* 461 */       for (i = 0; i < data.length; i++) {
/* 462 */         data[i] = din.readLong();
/*     */       }
/* 464 */       return new BloomFilter(new BloomFilterStrategies.BitArray(data), numHashFunctions, funnel, strategy);
/*     */     } catch (RuntimeException e) {
/* 466 */       long[] data = String.valueOf(String.valueOf("Unable to deserialize BloomFilter from InputStream. strategyOrdinal: "));int i = strategyOrdinal;int i = numHashFunctions;int j = dataLength;IOException ioException = new IOException(65 + data.length() + data + i + " numHashFunctions: " + i + " dataLength: " + j);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 471 */       ioException.initCause(e);
/* 472 */       throw ioException;
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract interface Strategy
/*     */     extends Serializable
/*     */   {
/*     */     public abstract <T> boolean put(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
/*     */     
/*     */     public abstract <T> boolean mightContain(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
/*     */     
/*     */     public abstract int ordinal();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\hash\BloomFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */