/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.IntBuffer;
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
/*     */ public class IntCountsHistogram
/*     */   extends AbstractHistogram
/*     */ {
/*     */   long totalCount;
/*     */   int[] counts;
/*     */   int normalizingIndexOffset;
/*     */   
/*     */   long getCountAtIndex(int index)
/*     */   {
/*  30 */     return this.counts[normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength)];
/*     */   }
/*     */   
/*     */   long getCountAtNormalizedIndex(int index)
/*     */   {
/*  35 */     return this.counts[index];
/*     */   }
/*     */   
/*     */   void incrementCountAtIndex(int index)
/*     */   {
/*  40 */     int normalizedIndex = normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
/*  41 */     int currentCount = this.counts[normalizedIndex];
/*  42 */     int newCount = currentCount + 1;
/*  43 */     if (newCount < 0) {
/*  44 */       throw new IllegalStateException("would overflow integer count");
/*     */     }
/*  46 */     this.counts[normalizedIndex] = newCount;
/*     */   }
/*     */   
/*     */   void addToCountAtIndex(int index, long value)
/*     */   {
/*  51 */     int normalizedIndex = normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
/*     */     
/*  53 */     long currentCount = this.counts[normalizedIndex];
/*  54 */     long newCount = currentCount + value;
/*  55 */     if ((newCount < -2147483648L) || (newCount > 2147483647L)) {
/*  56 */       throw new IllegalArgumentException("would overflow integer count");
/*     */     }
/*  58 */     this.counts[normalizedIndex] = ((int)newCount);
/*     */   }
/*     */   
/*     */   void setCountAtIndex(int index, long value)
/*     */   {
/*  63 */     setCountAtNormalizedIndex(normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength), value);
/*     */   }
/*     */   
/*     */   void setCountAtNormalizedIndex(int index, long value)
/*     */   {
/*  68 */     if ((value < 0L) || (value > 2147483647L)) {
/*  69 */       throw new IllegalArgumentException("would overflow short integer count");
/*     */     }
/*  71 */     this.counts[index] = ((int)value);
/*     */   }
/*     */   
/*     */   int getNormalizingIndexOffset()
/*     */   {
/*  76 */     return this.normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void setNormalizingIndexOffset(int normalizingIndexOffset)
/*     */   {
/*  81 */     this.normalizingIndexOffset = normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated)
/*     */   {
/*  86 */     nonConcurrentNormalizingIndexShift(offsetToAdd, lowestHalfBucketPopulated);
/*     */   }
/*     */   
/*     */   void clearCounts()
/*     */   {
/*  91 */     Arrays.fill(this.counts, 0);
/*  92 */     this.totalCount = 0L;
/*     */   }
/*     */   
/*     */   public IntCountsHistogram copy()
/*     */   {
/*  97 */     IntCountsHistogram copy = new IntCountsHistogram(this);
/*  98 */     copy.add(this);
/*  99 */     return copy;
/*     */   }
/*     */   
/*     */   public IntCountsHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 104 */     IntCountsHistogram toHistogram = new IntCountsHistogram(this);
/* 105 */     toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 106 */     return toHistogram;
/*     */   }
/*     */   
/*     */   public long getTotalCount()
/*     */   {
/* 111 */     return this.totalCount;
/*     */   }
/*     */   
/*     */   void setTotalCount(long totalCount)
/*     */   {
/* 116 */     this.totalCount = totalCount;
/*     */   }
/*     */   
/*     */   void incrementTotalCount()
/*     */   {
/* 121 */     this.totalCount += 1L;
/*     */   }
/*     */   
/*     */   void addToTotalCount(long value)
/*     */   {
/* 126 */     this.totalCount += value;
/*     */   }
/*     */   
/*     */   int _getEstimatedFootprintInBytes()
/*     */   {
/* 131 */     return 512 + 4 * this.counts.length;
/*     */   }
/*     */   
/*     */   void resize(long newHighestTrackableValue)
/*     */   {
/* 136 */     int oldNormalizedZeroIndex = normalizeIndex(0, this.normalizingIndexOffset, this.countsArrayLength);
/*     */     
/* 138 */     establishSize(newHighestTrackableValue);
/*     */     
/* 140 */     int countsDelta = this.countsArrayLength - this.counts.length;
/*     */     
/* 142 */     this.counts = Arrays.copyOf(this.counts, this.countsArrayLength);
/*     */     
/* 144 */     if (oldNormalizedZeroIndex != 0)
/*     */     {
/* 146 */       int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
/* 147 */       int lengthToCopy = this.countsArrayLength - countsDelta - oldNormalizedZeroIndex;
/* 148 */       System.arraycopy(this.counts, oldNormalizedZeroIndex, this.counts, newNormalizedZeroIndex, lengthToCopy);
/* 149 */       Arrays.fill(this.counts, oldNormalizedZeroIndex, newNormalizedZeroIndex, 0);
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
/*     */   public IntCountsHistogram(int numberOfSignificantValueDigits)
/*     */   {
/* 162 */     this(1L, 2L, numberOfSignificantValueDigits);
/* 163 */     setAutoResize(true);
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
/*     */   public IntCountsHistogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 177 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public IntCountsHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 198 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
/* 199 */     this.counts = new int[this.countsArrayLength];
/* 200 */     this.wordSizeInBytes = 4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntCountsHistogram(AbstractHistogram source)
/*     */   {
/* 209 */     super(source);
/* 210 */     this.counts = new int[this.countsArrayLength];
/* 211 */     this.wordSizeInBytes = 4;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IntCountsHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 222 */     return (IntCountsHistogram)decodeFromByteBuffer(buffer, IntCountsHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IntCountsHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 235 */     return (IntCountsHistogram)decodeFromCompressedByteBuffer(buffer, IntCountsHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*     */   {
/* 240 */     o.defaultReadObject();
/*     */   }
/*     */   
/*     */   synchronized void fillCountsArrayFromBuffer(ByteBuffer buffer, int length)
/*     */   {
/* 245 */     buffer.asIntBuffer().get(this.counts, 0, length);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\IntCountsHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */