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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConcurrentHistogram
/*     */   extends Histogram
/*     */ {
/*  44 */   static final AtomicLongFieldUpdater<ConcurrentHistogram> totalCountUpdater = AtomicLongFieldUpdater.newUpdater(ConcurrentHistogram.class, "totalCount");
/*     */   
/*     */   volatile long totalCount;
/*     */   volatile AtomicLongArrayWithNormalizingOffset activeCounts;
/*     */   volatile AtomicLongArrayWithNormalizingOffset inactiveCounts;
/*  49 */   transient WriterReaderPhaser wrp = new WriterReaderPhaser();
/*     */   
/*     */   long getCountAtIndex(int index)
/*     */   {
/*     */     try {
/*  54 */       this.wrp.readerLock();
/*  55 */       assert (this.countsArrayLength == this.activeCounts.length());
/*  56 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/*  57 */       long activeCount = this.activeCounts.get(
/*  58 */         normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()));
/*  59 */       long inactiveCount = this.inactiveCounts.get(
/*  60 */         normalizeIndex(index, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length()));
/*  61 */       return activeCount + inactiveCount;
/*     */     } finally {
/*  63 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   long getCountAtNormalizedIndex(int index)
/*     */   {
/*     */     try {
/*  70 */       this.wrp.readerLock();
/*  71 */       assert (this.countsArrayLength == this.activeCounts.length());
/*  72 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/*  73 */       long activeCount = this.activeCounts.get(index);
/*  74 */       long inactiveCount = this.inactiveCounts.get(index);
/*  75 */       return activeCount + inactiveCount;
/*     */     } finally {
/*  77 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   void incrementCountAtIndex(int index)
/*     */   {
/*  83 */     long criticalValue = this.wrp.writerCriticalSectionEnter();
/*     */     try {
/*  85 */       this.activeCounts.incrementAndGet(
/*  86 */         normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()));
/*     */     } finally {
/*  88 */       this.wrp.writerCriticalSectionExit(criticalValue);
/*     */     }
/*     */   }
/*     */   
/*     */   void addToCountAtIndex(int index, long value)
/*     */   {
/*  94 */     long criticalValue = this.wrp.writerCriticalSectionEnter();
/*     */     try {
/*  96 */       this.activeCounts.addAndGet(
/*  97 */         normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()), value);
/*     */     } finally {
/*  99 */       this.wrp.writerCriticalSectionExit(criticalValue);
/*     */     }
/*     */   }
/*     */   
/*     */   void setCountAtIndex(int index, long value)
/*     */   {
/*     */     try {
/* 106 */       this.wrp.readerLock();
/* 107 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 108 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/* 109 */       this.activeCounts.lazySet(
/* 110 */         normalizeIndex(index, this.activeCounts.getNormalizingIndexOffset(), this.activeCounts.length()), value);
/* 111 */       this.inactiveCounts.lazySet(
/* 112 */         normalizeIndex(index, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length()), 0L);
/*     */     } finally {
/* 114 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   void setCountAtNormalizedIndex(int index, long value)
/*     */   {
/*     */     try {
/* 121 */       this.wrp.readerLock();
/* 122 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 123 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/* 124 */       this.inactiveCounts.lazySet(index, value);
/* 125 */       this.activeCounts.lazySet(index, 0L);
/*     */     } finally {
/* 127 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   int getNormalizingIndexOffset()
/*     */   {
/* 134 */     return this.activeCounts.getNormalizingIndexOffset();
/*     */   }
/*     */   
/*     */   void setNormalizingIndexOffset(int normalizingIndexOffset)
/*     */   {
/* 139 */     setNormalizingIndexOffset(normalizingIndexOffset, 0, false);
/*     */   }
/*     */   
/*     */ 
/*     */   private void setNormalizingIndexOffset(int normalizingIndexOffset, int shiftedAmount, boolean lowestHalfBucketPopulated)
/*     */   {
/*     */     try
/*     */     {
/* 147 */       this.wrp.readerLock();
/*     */       
/* 149 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 150 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/*     */       
/* 152 */       if (normalizingIndexOffset == this.activeCounts.getNormalizingIndexOffset()) {
/* 153 */         return;
/*     */       }
/*     */       
/*     */ 
/* 157 */       int zeroIndex = normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/* 158 */       long inactiveZeroValueCount = this.inactiveCounts.get(zeroIndex);
/* 159 */       this.inactiveCounts.lazySet(zeroIndex, 0L);
/*     */       
/*     */ 
/* 162 */       this.inactiveCounts.setNormalizingIndexOffset(normalizingIndexOffset);
/*     */       
/*     */ 
/* 165 */       if ((shiftedAmount > 0) && (lowestHalfBucketPopulated)) {
/* 166 */         shiftLowestInactiveHalfBucketContentsLeft(shiftedAmount);
/*     */       }
/*     */       
/*     */ 
/* 170 */       zeroIndex = normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/* 171 */       this.inactiveCounts.lazySet(zeroIndex, inactiveZeroValueCount);
/*     */       
/*     */ 
/* 174 */       AtomicLongArrayWithNormalizingOffset tmp = this.activeCounts;
/* 175 */       this.activeCounts = this.inactiveCounts;
/* 176 */       this.inactiveCounts = tmp;
/*     */       
/* 178 */       this.wrp.flipPhase();
/*     */       
/*     */ 
/* 181 */       zeroIndex = normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/* 182 */       inactiveZeroValueCount = this.inactiveCounts.get(zeroIndex);
/* 183 */       this.inactiveCounts.lazySet(zeroIndex, 0L);
/*     */       
/*     */ 
/* 186 */       this.inactiveCounts.setNormalizingIndexOffset(normalizingIndexOffset);
/*     */       
/*     */ 
/* 189 */       if ((shiftedAmount > 0) && (lowestHalfBucketPopulated)) {
/* 190 */         shiftLowestInactiveHalfBucketContentsLeft(shiftedAmount);
/*     */       }
/*     */       
/*     */ 
/* 194 */       zeroIndex = normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/* 195 */       this.inactiveCounts.lazySet(zeroIndex, inactiveZeroValueCount);
/*     */       
/*     */ 
/* 198 */       tmp = this.activeCounts;
/* 199 */       this.activeCounts = this.inactiveCounts;
/* 200 */       this.inactiveCounts = tmp;
/*     */       
/* 202 */       this.wrp.flipPhase();
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 208 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private void shiftLowestInactiveHalfBucketContentsLeft(int shiftAmount) {
/* 213 */     int numberOfBinaryOrdersOfMagnitude = shiftAmount >> this.subBucketHalfCountMagnitude;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 230 */     for (int fromIndex = 1; fromIndex < this.subBucketHalfCount; fromIndex++) {
/* 231 */       long toValue = valueFromIndex(fromIndex) << numberOfBinaryOrdersOfMagnitude;
/* 232 */       int toIndex = countsArrayIndex(toValue);
/*     */       
/* 234 */       int normalizedToIndex = normalizeIndex(toIndex, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/* 235 */       long countAtFromIndex = this.inactiveCounts.get(fromIndex);
/* 236 */       this.inactiveCounts.lazySet(normalizedToIndex, countAtFromIndex);
/* 237 */       this.inactiveCounts.lazySet(fromIndex, 0L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void shiftNormalizingIndexByOffset(int offsetToAdd, boolean lowestHalfBucketPopulated)
/*     */   {
/*     */     try
/*     */     {
/* 249 */       this.wrp.readerLock();
/* 250 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 251 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/* 252 */       int newNormalizingIndexOffset = getNormalizingIndexOffset() + offsetToAdd;
/* 253 */       setNormalizingIndexOffset(newNormalizingIndexOffset, offsetToAdd, lowestHalfBucketPopulated);
/*     */     } finally {
/* 255 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   void resize(long newHighestTrackableValue)
/*     */   {
/*     */     try {
/* 262 */       this.wrp.readerLock();
/*     */       
/* 264 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 265 */       assert (this.countsArrayLength == this.inactiveCounts.length());
/*     */       
/* 267 */       int newArrayLength = determineArrayLengthNeeded(newHighestTrackableValue);
/* 268 */       int countsDelta = newArrayLength - this.countsArrayLength;
/*     */       
/* 270 */       if (countsDelta <= 0)
/*     */       {
/* 272 */         return;
/*     */       }
/*     */       
/*     */ 
/* 276 */       int oldNormalizedZeroIndex = normalizeIndex(0, this.inactiveCounts.getNormalizingIndexOffset(), this.inactiveCounts.length());
/*     */       
/*     */ 
/* 279 */       AtomicLongArray oldInactiveCounts = this.inactiveCounts;
/*     */       
/*     */ 
/*     */ 
/* 283 */       this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(newArrayLength, this.inactiveCounts.getNormalizingIndexOffset());
/*     */       
/*     */ 
/* 286 */       for (int i = 0; i < oldInactiveCounts.length(); i++) {
/* 287 */         this.inactiveCounts.lazySet(i, oldInactiveCounts.get(i));
/*     */       }
/* 289 */       if (oldNormalizedZeroIndex != 0)
/*     */       {
/* 291 */         int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
/* 292 */         int lengthToCopy = newArrayLength - countsDelta - oldNormalizedZeroIndex;
/*     */         
/* 294 */         int src = oldNormalizedZeroIndex; for (int dst = newNormalizedZeroIndex; 
/* 295 */             src < oldNormalizedZeroIndex + lengthToCopy; 
/* 296 */             dst++) {
/* 297 */           this.inactiveCounts.lazySet(dst, oldInactiveCounts.get(src));src++;
/*     */         }
/* 299 */         for (dst = oldNormalizedZeroIndex; dst < newNormalizedZeroIndex; dst++) {
/* 300 */           this.inactiveCounts.lazySet(dst, 0L);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 305 */       AtomicLongArrayWithNormalizingOffset tmp = this.activeCounts;
/* 306 */       this.activeCounts = this.inactiveCounts;
/* 307 */       this.inactiveCounts = tmp;
/*     */       
/* 309 */       this.wrp.flipPhase();
/*     */       
/*     */ 
/* 312 */       oldInactiveCounts = this.inactiveCounts;
/*     */       
/*     */ 
/*     */ 
/* 316 */       this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(newArrayLength, this.inactiveCounts.getNormalizingIndexOffset());
/*     */       
/*     */ 
/* 319 */       for (int i = 0; i < oldInactiveCounts.length(); i++) {
/* 320 */         this.inactiveCounts.lazySet(i, oldInactiveCounts.get(i));
/*     */       }
/* 322 */       if (oldNormalizedZeroIndex != 0)
/*     */       {
/* 324 */         int newNormalizedZeroIndex = oldNormalizedZeroIndex + countsDelta;
/* 325 */         int lengthToCopy = newArrayLength - countsDelta - oldNormalizedZeroIndex;
/*     */         
/* 327 */         int src = oldNormalizedZeroIndex; for (int dst = newNormalizedZeroIndex; 
/* 328 */             src < oldNormalizedZeroIndex + lengthToCopy; 
/* 329 */             dst++) {
/* 330 */           this.inactiveCounts.lazySet(dst, oldInactiveCounts.get(src));src++;
/*     */         }
/* 332 */         for (dst = oldNormalizedZeroIndex; dst < newNormalizedZeroIndex; dst++) {
/* 333 */           this.inactiveCounts.lazySet(dst, 0L);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 338 */       tmp = this.activeCounts;
/* 339 */       this.activeCounts = this.inactiveCounts;
/* 340 */       this.inactiveCounts = tmp;
/*     */       
/* 342 */       this.wrp.flipPhase();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 348 */       establishSize(newHighestTrackableValue);
/*     */       
/* 350 */       assert (this.countsArrayLength == this.activeCounts.length());
/* 351 */       if ((!$assertionsDisabled) && (this.countsArrayLength != this.inactiveCounts.length())) throw new AssertionError();
/*     */     }
/*     */     finally {
/* 354 */       this.wrp.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAutoResize(boolean autoResize)
/*     */   {
/* 360 */     this.autoResize = true;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   void clearCounts()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   4: invokevirtual 30	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerLock	()V
/*     */     //   7: getstatic 32	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:$assertionsDisabled	Z
/*     */     //   10: ifne +25 -> 35
/*     */     //   13: aload_0
/*     */     //   14: getfield 36	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:countsArrayLength	I
/*     */     //   17: aload_0
/*     */     //   18: getfield 38	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:activeCounts	Lcom/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset;
/*     */     //   21: invokevirtual 42	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset:length	()I
/*     */     //   24: if_icmpeq +11 -> 35
/*     */     //   27: new 44	java/lang/AssertionError
/*     */     //   30: dup
/*     */     //   31: invokespecial 47	java/lang/AssertionError:<init>	()V
/*     */     //   34: athrow
/*     */     //   35: getstatic 32	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:$assertionsDisabled	Z
/*     */     //   38: ifne +25 -> 63
/*     */     //   41: aload_0
/*     */     //   42: getfield 36	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:countsArrayLength	I
/*     */     //   45: aload_0
/*     */     //   46: getfield 49	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:inactiveCounts	Lcom/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset;
/*     */     //   49: invokevirtual 42	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset:length	()I
/*     */     //   52: if_icmpeq +11 -> 63
/*     */     //   55: new 44	java/lang/AssertionError
/*     */     //   58: dup
/*     */     //   59: invokespecial 47	java/lang/AssertionError:<init>	()V
/*     */     //   62: athrow
/*     */     //   63: iconst_0
/*     */     //   64: istore_1
/*     */     //   65: iload_1
/*     */     //   66: aload_0
/*     */     //   67: getfield 38	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:activeCounts	Lcom/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset;
/*     */     //   70: invokevirtual 42	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset:length	()I
/*     */     //   73: if_icmpge +27 -> 100
/*     */     //   76: aload_0
/*     */     //   77: getfield 38	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:activeCounts	Lcom/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset;
/*     */     //   80: iload_1
/*     */     //   81: lconst_0
/*     */     //   82: invokevirtual 95	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset:lazySet	(IJ)V
/*     */     //   85: aload_0
/*     */     //   86: getfield 49	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:inactiveCounts	Lcom/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset;
/*     */     //   89: iload_1
/*     */     //   90: lconst_0
/*     */     //   91: invokevirtual 95	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram$AtomicLongArrayWithNormalizingOffset:lazySet	(IJ)V
/*     */     //   94: iinc 1 1
/*     */     //   97: goto -32 -> 65
/*     */     //   100: getstatic 172	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:totalCountUpdater	Ljava/util/concurrent/atomic/AtomicLongFieldUpdater;
/*     */     //   103: aload_0
/*     */     //   104: lconst_0
/*     */     //   105: invokevirtual 178	java/util/concurrent/atomic/AtomicLongFieldUpdater:set	(Ljava/lang/Object;J)V
/*     */     //   108: aload_0
/*     */     //   109: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   112: invokevirtual 62	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerUnlock	()V
/*     */     //   115: goto +13 -> 128
/*     */     //   118: astore_2
/*     */     //   119: aload_0
/*     */     //   120: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   123: invokevirtual 62	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerUnlock	()V
/*     */     //   126: aload_2
/*     */     //   127: athrow
/*     */     //   128: return
/*     */     // Line number table:
/*     */     //   Java source line #366	-> byte code offset #0
/*     */     //   Java source line #367	-> byte code offset #7
/*     */     //   Java source line #368	-> byte code offset #35
/*     */     //   Java source line #369	-> byte code offset #63
/*     */     //   Java source line #370	-> byte code offset #76
/*     */     //   Java source line #371	-> byte code offset #85
/*     */     //   Java source line #369	-> byte code offset #94
/*     */     //   Java source line #373	-> byte code offset #100
/*     */     //   Java source line #375	-> byte code offset #108
/*     */     //   Java source line #376	-> byte code offset #115
/*     */     //   Java source line #375	-> byte code offset #118
/*     */     //   Java source line #377	-> byte code offset #128
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	129	0	this	ConcurrentHistogram
/*     */     //   64	31	1	i	int
/*     */     //   118	9	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	108	118	finally
/*     */   }
/*     */   
/*     */   public ConcurrentHistogram copy()
/*     */   {
/* 381 */     ConcurrentHistogram copy = new ConcurrentHistogram(this);
/* 382 */     copy.add(this);
/* 383 */     return copy;
/*     */   }
/*     */   
/*     */   public ConcurrentHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 388 */     ConcurrentHistogram toHistogram = new ConcurrentHistogram(this);
/* 389 */     toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 390 */     return toHistogram;
/*     */   }
/*     */   
/*     */   public long getTotalCount()
/*     */   {
/* 395 */     return totalCountUpdater.get(this);
/*     */   }
/*     */   
/*     */   void setTotalCount(long totalCount)
/*     */   {
/* 400 */     totalCountUpdater.set(this, totalCount);
/*     */   }
/*     */   
/*     */   void incrementTotalCount()
/*     */   {
/* 405 */     totalCountUpdater.incrementAndGet(this);
/*     */   }
/*     */   
/*     */   void addToTotalCount(long value)
/*     */   {
/* 410 */     totalCountUpdater.addAndGet(this, value);
/*     */   }
/*     */   
/*     */   int _getEstimatedFootprintInBytes()
/*     */   {
/* 415 */     return 512 + 16 * this.activeCounts.length();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConcurrentHistogram(int numberOfSignificantValueDigits)
/*     */   {
/* 427 */     this(1L, 2L, numberOfSignificantValueDigits);
/* 428 */     setAutoResize(true);
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
/*     */   public ConcurrentHistogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 442 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public ConcurrentHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/* 463 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, false);
/* 464 */     this.activeCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
/* 465 */     this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
/* 466 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConcurrentHistogram(AbstractHistogram source)
/*     */   {
/* 475 */     super(source, false);
/* 476 */     this.activeCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
/* 477 */     this.inactiveCounts = new AtomicLongArrayWithNormalizingOffset(this.countsArrayLength, 0);
/* 478 */     this.wordSizeInBytes = 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ConcurrentHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 489 */     return (ConcurrentHistogram)decodeFromByteBuffer(buffer, ConcurrentHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ConcurrentHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 502 */     return (ConcurrentHistogram)decodeFromCompressedByteBuffer(buffer, ConcurrentHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 508 */     o.defaultReadObject();
/* 509 */     this.wrp = new WriterReaderPhaser();
/*     */   }
/*     */   
/*     */   synchronized void fillCountsArrayFromBuffer(ByteBuffer buffer, int length)
/*     */   {
/* 514 */     LongBuffer logbuffer = buffer.asLongBuffer();
/* 515 */     for (int i = 0; i < length; i++) {
/* 516 */       this.inactiveCounts.lazySet(i, logbuffer.get());
/* 517 */       this.activeCounts.lazySet(i, 0L);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   synchronized void fillBufferFromCountsArray(ByteBuffer buffer)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   4: invokevirtual 30	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerLock	()V
/*     */     //   7: aload_0
/*     */     //   8: aload_1
/*     */     //   9: invokespecial 274	com/facebook/presto/jdbc/internal/HdrHistogram/Histogram:fillBufferFromCountsArray	(Ljava/nio/ByteBuffer;)V
/*     */     //   12: aload_0
/*     */     //   13: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   16: invokevirtual 62	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerUnlock	()V
/*     */     //   19: goto +13 -> 32
/*     */     //   22: astore_2
/*     */     //   23: aload_0
/*     */     //   24: getfield 24	com/facebook/presto/jdbc/internal/HdrHistogram/ConcurrentHistogram:wrp	Lcom/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser;
/*     */     //   27: invokevirtual 62	com/facebook/presto/jdbc/internal/HdrHistogram/WriterReaderPhaser:readerUnlock	()V
/*     */     //   30: aload_2
/*     */     //   31: athrow
/*     */     //   32: return
/*     */     // Line number table:
/*     */     //   Java source line #524	-> byte code offset #0
/*     */     //   Java source line #525	-> byte code offset #7
/*     */     //   Java source line #527	-> byte code offset #12
/*     */     //   Java source line #528	-> byte code offset #19
/*     */     //   Java source line #527	-> byte code offset #22
/*     */     //   Java source line #529	-> byte code offset #32
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	33	0	this	ConcurrentHistogram
/*     */     //   0	33	1	buffer	ByteBuffer
/*     */     //   22	9	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	12	22	finally
/*     */   }
/*     */   
/*     */   static class AtomicLongArrayWithNormalizingOffset
/*     */     extends AtomicLongArray
/*     */   {
/*     */     private int normalizingIndexOffset;
/*     */     
/*     */     AtomicLongArrayWithNormalizingOffset(int length, int normalizingIndexOffset)
/*     */     {
/* 536 */       super();
/* 537 */       this.normalizingIndexOffset = normalizingIndexOffset;
/*     */     }
/*     */     
/*     */     public int getNormalizingIndexOffset() {
/* 541 */       return this.normalizingIndexOffset;
/*     */     }
/*     */     
/*     */     public void setNormalizingIndexOffset(int normalizingIndexOffset) {
/* 545 */       this.normalizingIndexOffset = normalizingIndexOffset;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\ConcurrentHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */