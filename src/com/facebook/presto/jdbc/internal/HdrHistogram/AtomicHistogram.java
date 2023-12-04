/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.util.concurrent.atomic.AtomicLongArray;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import java.util.zip.DataFormatException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AtomicHistogram
/*     */   extends Histogram
/*     */ {
/*  37 */   static final AtomicLongFieldUpdater<AtomicHistogram> totalCountUpdater = AtomicLongFieldUpdater.newUpdater(AtomicHistogram.class, "totalCount");
/*     */   volatile long totalCount;
/*     */   volatile AtomicLongArray counts;
/*     */   
/*     */   long getCountAtIndex(int index)
/*     */   {
/*  43 */     return this.counts.get(index);
/*     */   }
/*     */   
/*     */   long getCountAtNormalizedIndex(int index)
/*     */   {
/*  48 */     return this.counts.get(index);
/*     */   }
/*     */   
/*     */   void incrementCountAtIndex(int index)
/*     */   {
/*  53 */     this.counts.getAndIncrement(index);
/*     */   }
/*     */   
/*     */   void addToCountAtIndex(int index, long value)
/*     */   {
/*  58 */     this.counts.getAndAdd(index, value);
/*     */   }
/*     */   
/*     */   void setCountAtIndex(int index, long value)
/*     */   {
/*  63 */     this.counts.lazySet(index, value);
/*     */   }
/*     */   
/*     */   void setCountAtNormalizedIndex(int index, long value)
/*     */   {
/*  68 */     this.counts.lazySet(index, value);
/*     */   }
/*     */   
/*     */   int getNormalizingIndexOffset()
/*     */   {
/*  73 */     return 0;
/*     */   }
/*     */   
/*     */   void setNormalizingIndexOffset(int normalizingIndexOffset)
/*     */   {
/*  78 */     if (normalizingIndexOffset != 0) {
/*  79 */       throw new IllegalStateException("AtomicHistogram does not support non-zero normalizing index settings. Use ConcurrentHistogram Instead.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated)
/*     */   {
/*  87 */     throw new IllegalStateException("AtomicHistogram does not support Shifting operations. Use ConcurrentHistogram Instead.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void resize(long newHighestTrackableValue)
/*     */   {
/*  94 */     throw new IllegalStateException("AtomicHistogram does not support resizing operations. Use ConcurrentHistogram Instead.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAutoResize(boolean autoResize)
/*     */   {
/* 101 */     throw new IllegalStateException("AtomicHistogram does not support AutoResize operation. Use ConcurrentHistogram Instead.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void clearCounts()
/*     */   {
/* 108 */     for (int i = 0; i < this.counts.length(); i++) {
/* 109 */       this.counts.lazySet(i, 0L);
/*     */     }
/* 111 */     totalCountUpdater.set(this, 0L);
/*     */   }
/*     */   
/*     */   public AtomicHistogram copy()
/*     */   {
/* 116 */     AtomicHistogram copy = new AtomicHistogram(this);
/* 117 */     copy.add(this);
/* 118 */     return copy;
/*     */   }
/*     */   
/*     */   public AtomicHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 123 */     AtomicHistogram toHistogram = new AtomicHistogram(this);
/* 124 */     toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 125 */     return toHistogram;
/*     */   }
/*     */   
/*     */   public long getTotalCount()
/*     */   {
/* 130 */     return totalCountUpdater.get(this);
/*     */   }
/*     */   
/*     */   void setTotalCount(long totalCount)
/*     */   {
/* 135 */     totalCountUpdater.set(this, totalCount);
/*     */   }
/*     */   
/*     */   void incrementTotalCount()
/*     */   {
/* 140 */     totalCountUpdater.incrementAndGet(this);
/*     */   }
/*     */   
/*     */   void addToTotalCount(long value)
/*     */   {
/* 145 */     totalCountUpdater.addAndGet(this, value);
/*     */   }
/*     */   
/*     */   int _getEstimatedFootprintInBytes()
/*     */   {
/* 150 */     return 512 + 8 * this.counts.length();
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
/*     */   public AtomicHistogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 164 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public AtomicHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 184 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
/* 185 */     this.counts = new AtomicLongArray(this.countsArrayLength);
/* 186 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AtomicHistogram(AbstractHistogram source)
/*     */   {
/* 195 */     super(source, false);
/* 196 */     this.counts = new AtomicLongArray(this.countsArrayLength);
/* 197 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static AtomicHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 208 */     return (AtomicHistogram)decodeFromByteBuffer(buffer, AtomicHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static AtomicHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 221 */     return (AtomicHistogram)decodeFromCompressedByteBuffer(buffer, AtomicHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*     */   {
/* 226 */     o.defaultReadObject();
/*     */   }
/*     */   
/*     */   synchronized void fillCountsArrayFromBuffer(ByteBuffer buffer, int length)
/*     */   {
/* 231 */     LongBuffer logbuffer = buffer.asLongBuffer();
/* 232 */     for (int i = 0; i < length; i++) {
/* 233 */       this.counts.lazySet(i, logbuffer.get());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AtomicHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */