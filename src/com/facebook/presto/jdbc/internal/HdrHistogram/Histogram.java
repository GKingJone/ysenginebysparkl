/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Histogram
/*     */   extends AbstractHistogram
/*     */ {
/*     */   long totalCount;
/*     */   long[] counts;
/*     */   int normalizingIndexOffset;
/*     */   
/*     */   long getCountAtIndex(int index)
/*     */   {
/*  52 */     return this.counts[normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)];
/*     */   }
/*     */   
/*     */   long getCountAtNormalizedIndex(int index)
/*     */   {
/*  57 */     return this.counts[index];
/*     */   }
/*     */   
/*     */   void incrementCountAtIndex(int index)
/*     */   {
/*  62 */     this.counts[normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)] += 1L;
/*     */   }
/*     */   
/*     */   void addToCountAtIndex(int index, long value)
/*     */   {
/*  67 */     this.counts[normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)] += value;
/*     */   }
/*     */   
/*     */   void setCountAtIndex(int index, long value)
/*     */   {
/*  72 */     this.counts[normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)] = value;
/*     */   }
/*     */   
/*     */   void setCountAtNormalizedIndex(int index, long value)
/*     */   {
/*  77 */     this.counts[index] = value;
/*     */   }
/*     */   
/*     */   int getNormalizingIndexOffset()
/*     */   {
/*  82 */     return this.normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void setNormalizingIndexOffset(int normalizingIndexOffset)
/*     */   {
/*  87 */     this.normalizingIndexOffset = normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated)
/*     */   {
/*  92 */     nonConcurrentNormalizingIndexShift(offsetToAdd, lowestHalfBucketPopulated);
/*     */   }
/*     */   
/*     */   void clearCounts()
/*     */   {
/*  97 */     Arrays.fill(this.counts, 0L);
/*  98 */     this.totalCount = 0L;
/*     */   }
/*     */   
/*     */   public Histogram copy()
/*     */   {
/* 103 */     Histogram copy = new Histogram(this);
/* 104 */     copy.add(this);
/* 105 */     return copy;
/*     */   }
/*     */   
/*     */   public Histogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 110 */     Histogram copy = new Histogram(this);
/* 111 */     copy.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 112 */     return copy;
/*     */   }
/*     */   
/*     */   public long getTotalCount()
/*     */   {
/* 117 */     return this.totalCount;
/*     */   }
/*     */   
/*     */   void setTotalCount(long totalCount)
/*     */   {
/* 122 */     this.totalCount = totalCount;
/*     */   }
/*     */   
/*     */   void incrementTotalCount()
/*     */   {
/* 127 */     this.totalCount += 1L;
/*     */   }
/*     */   
/*     */   void addToTotalCount(long value)
/*     */   {
/* 132 */     this.totalCount += value;
/*     */   }
/*     */   
/*     */   int _getEstimatedFootprintInBytes()
/*     */   {
/* 137 */     return 512 + 8 * this.counts.length;
/*     */   }
/*     */   
/*     */   void resize(long newHighestTrackableValue)
/*     */   {
/* 142 */     int oldNormalizedZeroIndex = normalizeIndex(0, this.normalizingIndexOffset, this.countsArrayLength);
/*     */     
/* 144 */     establishSize(newHighestTrackableValue);
/*     */     
/* 146 */     int countsDelta = this.countsArrayLength - this.counts.length;
/*     */     
/* 148 */     this.counts = Arrays.copyOf(this.counts, this.countsArrayLength);
/*     */     
/* 150 */     if (oldNormalizedZeroIndex != 0)
/*     */     {
/* 152 */       int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
/* 153 */       int lengthToCopy = this.countsArrayLength - countsDelta - oldNormalizedZeroIndex;
/* 154 */       System.arraycopy(this.counts, oldNormalizedZeroIndex, this.counts, newNormalizedZeroIndex, lengthToCopy);
/* 155 */       Arrays.fill(this.counts, oldNormalizedZeroIndex, newNormalizedZeroIndex, 0L);
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
/*     */   public Histogram(int numberOfSignificantValueDigits)
/*     */   {
/* 168 */     this(1L, 2L, numberOfSignificantValueDigits);
/* 169 */     setAutoResize(true);
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
/*     */   public Histogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 183 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public Histogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 204 */     this(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Histogram(AbstractHistogram source)
/*     */   {
/* 213 */     this(source, true);
/*     */   }
/*     */   
/*     */   Histogram(AbstractHistogram source, boolean allocateCountsArray) {
/* 217 */     super(source);
/* 218 */     if (allocateCountsArray) {
/* 219 */       this.counts = new long[this.countsArrayLength];
/*     */     }
/* 221 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */   Histogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, boolean allocateCountsArray)
/*     */   {
/* 226 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
/* 227 */     if (allocateCountsArray) {
/* 228 */       this.counts = new long[this.countsArrayLength];
/*     */     }
/* 230 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Histogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 241 */     return (Histogram)decodeFromByteBuffer(buffer, Histogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Histogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 253 */     return (Histogram)decodeFromCompressedByteBuffer(buffer, Histogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*     */   {
/* 258 */     o.defaultReadObject();
/*     */   }
/*     */   
/*     */   synchronized void fillCountsArrayFromBuffer(ByteBuffer buffer, int length)
/*     */   {
/* 263 */     buffer.asLongBuffer().get(this.counts, 0, length);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\Histogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */