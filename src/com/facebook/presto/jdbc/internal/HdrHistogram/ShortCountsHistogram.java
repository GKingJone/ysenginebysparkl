/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ShortBuffer;
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
/*     */ public class ShortCountsHistogram
/*     */   extends AbstractHistogram
/*     */ {
/*     */   long totalCount;
/*     */   short[] counts;
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
/*  41 */     short currentCount = this.counts[normalizedIndex];
/*  42 */     short newCount = (short)(currentCount + 1);
/*  43 */     if (newCount < 0) {
/*  44 */       throw new IllegalStateException("would overflow short integer count");
/*     */     }
/*  46 */     this.counts[normalizedIndex] = newCount;
/*     */   }
/*     */   
/*     */   void addToCountAtIndex(int index, long value)
/*     */   {
/*  51 */     int normalizedIndex = normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength);
/*  52 */     long currentCount = this.counts[normalizedIndex];
/*  53 */     long newCount = currentCount + value;
/*  54 */     if ((newCount < -32768L) || (newCount > 32767L)) {
/*  55 */       throw new IllegalArgumentException("would overflow integer count");
/*     */     }
/*  57 */     this.counts[normalizedIndex] = ((short)(int)newCount);
/*     */   }
/*     */   
/*     */   void setCountAtIndex(int index, long value)
/*     */   {
/*  62 */     setCountAtNormalizedIndex(normalizeIndex(index, this.normalizingIndexOffset, this.countsArrayLength), value);
/*     */   }
/*     */   
/*     */   void setCountAtNormalizedIndex(int index, long value)
/*     */   {
/*  67 */     if ((value < 0L) || (value > 32767L)) {
/*  68 */       throw new IllegalArgumentException("would overflow short integer count");
/*     */     }
/*  70 */     this.counts[index] = ((short)(int)value);
/*     */   }
/*     */   
/*     */   int getNormalizingIndexOffset()
/*     */   {
/*  75 */     return this.normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void setNormalizingIndexOffset(int normalizingIndexOffset)
/*     */   {
/*  80 */     this.normalizingIndexOffset = normalizingIndexOffset;
/*     */   }
/*     */   
/*     */   void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated)
/*     */   {
/*  85 */     nonConcurrentNormalizingIndexShift(offsetToAdd, lowestHalfBucketPopulated);
/*     */   }
/*     */   
/*     */   void clearCounts()
/*     */   {
/*  90 */     Arrays.fill(this.counts, (short)0);
/*  91 */     this.totalCount = 0L;
/*     */   }
/*     */   
/*     */   public ShortCountsHistogram copy() {
/*  95 */     ShortCountsHistogram copy = new ShortCountsHistogram(this);
/*  96 */     copy.add(this);
/*  97 */     return copy;
/*     */   }
/*     */   
/*     */   public ShortCountsHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 102 */     ShortCountsHistogram toHistogram = new ShortCountsHistogram(this);
/* 103 */     toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 104 */     return toHistogram;
/*     */   }
/*     */   
/*     */   public long getTotalCount()
/*     */   {
/* 109 */     return this.totalCount;
/*     */   }
/*     */   
/*     */   void setTotalCount(long totalCount)
/*     */   {
/* 114 */     this.totalCount = totalCount;
/*     */   }
/*     */   
/*     */   void incrementTotalCount()
/*     */   {
/* 119 */     this.totalCount += 1L;
/*     */   }
/*     */   
/*     */   void addToTotalCount(long value)
/*     */   {
/* 124 */     this.totalCount += value;
/*     */   }
/*     */   
/*     */   int _getEstimatedFootprintInBytes()
/*     */   {
/* 129 */     return 512 + 2 * this.counts.length;
/*     */   }
/*     */   
/*     */   void resize(long newHighestTrackableValue)
/*     */   {
/* 134 */     int oldNormalizedZeroIndex = normalizeIndex(0, this.normalizingIndexOffset, this.countsArrayLength);
/*     */     
/* 136 */     establishSize(newHighestTrackableValue);
/*     */     
/* 138 */     int countsDelta = this.countsArrayLength - this.counts.length;
/*     */     
/* 140 */     this.counts = Arrays.copyOf(this.counts, this.countsArrayLength);
/*     */     
/* 142 */     if (oldNormalizedZeroIndex != 0)
/*     */     {
/* 144 */       int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
/* 145 */       int lengthToCopy = this.countsArrayLength - countsDelta - oldNormalizedZeroIndex;
/* 146 */       System.arraycopy(this.counts, oldNormalizedZeroIndex, this.counts, newNormalizedZeroIndex, lengthToCopy);
/* 147 */       Arrays.fill(this.counts, oldNormalizedZeroIndex, newNormalizedZeroIndex, (short)0);
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
/*     */   public ShortCountsHistogram(int numberOfSignificantValueDigits)
/*     */   {
/* 160 */     this(1L, 2L, numberOfSignificantValueDigits);
/* 161 */     setAutoResize(true);
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
/*     */   public ShortCountsHistogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 175 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public ShortCountsHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 196 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
/* 197 */     this.counts = new short[this.countsArrayLength];
/* 198 */     this.wordSizeInBytes = 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ShortCountsHistogram(AbstractHistogram source)
/*     */   {
/* 207 */     super(source);
/* 208 */     this.counts = new short[this.countsArrayLength];
/* 209 */     this.wordSizeInBytes = 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ShortCountsHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 220 */     return (ShortCountsHistogram)decodeFromByteBuffer(buffer, ShortCountsHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ShortCountsHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 233 */     return (ShortCountsHistogram)decodeFromCompressedByteBuffer(buffer, ShortCountsHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*     */   {
/* 238 */     o.defaultReadObject();
/*     */   }
/*     */   
/*     */   synchronized void fillCountsArrayFromBuffer(ByteBuffer buffer, int length)
/*     */   {
/* 243 */     buffer.asShortBuffer().get(this.counts, 0, length);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\ShortCountsHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */