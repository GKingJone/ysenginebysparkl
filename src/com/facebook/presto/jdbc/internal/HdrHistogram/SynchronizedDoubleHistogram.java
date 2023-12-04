/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SynchronizedDoubleHistogram
/*     */   extends DoubleHistogram
/*     */ {
/*     */   public SynchronizedDoubleHistogram(int numberOfSignificantValueDigits)
/*     */   {
/*  71 */     this(2L, numberOfSignificantValueDigits);
/*  72 */     setAutoResize(true);
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
/*     */   public SynchronizedDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*     */   {
/*  86 */     super(highestToLowestValueRatio, numberOfSignificantValueDigits, SynchronizedHistogram.class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SynchronizedDoubleHistogram(ConcurrentDoubleHistogram source)
/*     */   {
/*  95 */     super(source);
/*     */   }
/*     */   
/*     */   public synchronized boolean isAutoResize()
/*     */   {
/* 100 */     return super.isAutoResize();
/*     */   }
/*     */   
/*     */   public synchronized void setAutoResize(boolean autoResize)
/*     */   {
/* 105 */     super.setAutoResize(autoResize);
/*     */   }
/*     */   
/*     */   public synchronized void recordValue(double value) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 110 */     super.recordValue(value);
/*     */   }
/*     */   
/*     */   public synchronized void recordValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 115 */     super.recordValueWithCount(value, count);
/*     */   }
/*     */   
/*     */   public synchronized void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 121 */     super.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */   }
/*     */   
/*     */   public synchronized void reset()
/*     */   {
/* 126 */     super.reset();
/*     */   }
/*     */   
/*     */   public synchronized DoubleHistogram copy()
/*     */   {
/* 131 */     DoubleHistogram targetHistogram = new DoubleHistogram(this);
/*     */     
/* 133 */     this.integerValuesHistogram.copyInto(targetHistogram.integerValuesHistogram);
/* 134 */     return targetHistogram;
/*     */   }
/*     */   
/*     */   public synchronized DoubleHistogram copyCorrectedForCoordinatedOmission(double expectedIntervalBetweenValueSamples)
/*     */   {
/* 139 */     DoubleHistogram targetHistogram = new DoubleHistogram(this);
/*     */     
/* 141 */     targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 142 */     return targetHistogram;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void copyInto(DoubleHistogram targetHistogram)
/*     */   {
/* 148 */     if (this.integerValuesHistogram.identity < targetHistogram.integerValuesHistogram.identity) {
/* 149 */       synchronized (this) {
/* 150 */         synchronized (targetHistogram) {
/* 151 */           super.copyInto(targetHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 155 */       synchronized (targetHistogram) {
/* 156 */         synchronized (this) {
/* 157 */           super.copyInto(targetHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void copyIntoCorrectedForCoordinatedOmission(DoubleHistogram targetHistogram, double expectedIntervalBetweenValueSamples)
/*     */   {
/* 168 */     if (this.integerValuesHistogram.identity < targetHistogram.integerValuesHistogram.identity) {
/* 169 */       synchronized (this) {
/* 170 */         synchronized (targetHistogram) {
/* 171 */           super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     } else {
/* 175 */       synchronized (targetHistogram) {
/* 176 */         synchronized (this) {
/* 177 */           super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void add(DoubleHistogram fromHistogram)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 186 */     if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
/* 187 */       synchronized (this) {
/* 188 */         synchronized (fromHistogram) {
/* 189 */           super.add(fromHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 193 */       synchronized (fromHistogram) {
/* 194 */         synchronized (this) {
/* 195 */           super.add(fromHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void subtract(DoubleHistogram fromHistogram)
/*     */   {
/* 205 */     if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
/* 206 */       synchronized (this) {
/* 207 */         synchronized (fromHistogram) {
/* 208 */           super.subtract(fromHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 212 */       synchronized (fromHistogram) {
/* 213 */         synchronized (this) {
/* 214 */           super.subtract(fromHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addWhileCorrectingForCoordinatedOmission(DoubleHistogram fromHistogram, double expectedIntervalBetweenValueSamples)
/*     */   {
/* 225 */     if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
/* 226 */       synchronized (this) {
/* 227 */         synchronized (fromHistogram) {
/* 228 */           super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     } else {
/* 232 */       synchronized (fromHistogram) {
/* 233 */         synchronized (this) {
/* 234 */           super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public synchronized boolean equals(Object other)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: if_acmpne +5 -> 7
/*     */     //   5: iconst_1
/*     */     //   6: ireturn
/*     */     //   7: aload_1
/*     */     //   8: instanceof 4
/*     */     //   11: ifeq +96 -> 107
/*     */     //   14: aload_1
/*     */     //   15: checkcast 4	com/facebook/presto/jdbc/internal/HdrHistogram/DoubleHistogram
/*     */     //   18: astore_2
/*     */     //   19: aload_0
/*     */     //   20: getfield 83	com/facebook/presto/jdbc/internal/HdrHistogram/SynchronizedDoubleHistogram:integerValuesHistogram	Lcom/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram;
/*     */     //   23: getfield 101	com/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram:identity	J
/*     */     //   26: aload_2
/*     */     //   27: getfield 84	com/facebook/presto/jdbc/internal/HdrHistogram/DoubleHistogram:integerValuesHistogram	Lcom/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram;
/*     */     //   30: getfield 101	com/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram:identity	J
/*     */     //   33: lcmp
/*     */     //   34: ifge +38 -> 72
/*     */     //   37: aload_0
/*     */     //   38: dup
/*     */     //   39: astore_3
/*     */     //   40: monitorenter
/*     */     //   41: aload_2
/*     */     //   42: dup
/*     */     //   43: astore 4
/*     */     //   45: monitorenter
/*     */     //   46: aload_0
/*     */     //   47: aload_2
/*     */     //   48: invokespecial 121	com/facebook/presto/jdbc/internal/HdrHistogram/DoubleHistogram:equals	(Ljava/lang/Object;)Z
/*     */     //   51: aload 4
/*     */     //   53: monitorexit
/*     */     //   54: aload_3
/*     */     //   55: monitorexit
/*     */     //   56: ireturn
/*     */     //   57: astore 5
/*     */     //   59: aload 4
/*     */     //   61: monitorexit
/*     */     //   62: aload 5
/*     */     //   64: athrow
/*     */     //   65: astore 6
/*     */     //   67: aload_3
/*     */     //   68: monitorexit
/*     */     //   69: aload 6
/*     */     //   71: athrow
/*     */     //   72: aload_2
/*     */     //   73: dup
/*     */     //   74: astore_3
/*     */     //   75: monitorenter
/*     */     //   76: aload_0
/*     */     //   77: dup
/*     */     //   78: astore 4
/*     */     //   80: monitorenter
/*     */     //   81: aload_0
/*     */     //   82: aload_2
/*     */     //   83: invokespecial 121	com/facebook/presto/jdbc/internal/HdrHistogram/DoubleHistogram:equals	(Ljava/lang/Object;)Z
/*     */     //   86: aload 4
/*     */     //   88: monitorexit
/*     */     //   89: aload_3
/*     */     //   90: monitorexit
/*     */     //   91: ireturn
/*     */     //   92: astore 7
/*     */     //   94: aload 4
/*     */     //   96: monitorexit
/*     */     //   97: aload 7
/*     */     //   99: athrow
/*     */     //   100: astore 8
/*     */     //   102: aload_3
/*     */     //   103: monitorexit
/*     */     //   104: aload 8
/*     */     //   106: athrow
/*     */     //   107: aload_0
/*     */     //   108: dup
/*     */     //   109: astore_2
/*     */     //   110: monitorenter
/*     */     //   111: aload_0
/*     */     //   112: aload_1
/*     */     //   113: invokespecial 121	com/facebook/presto/jdbc/internal/HdrHistogram/DoubleHistogram:equals	(Ljava/lang/Object;)Z
/*     */     //   116: aload_2
/*     */     //   117: monitorexit
/*     */     //   118: ireturn
/*     */     //   119: astore 9
/*     */     //   121: aload_2
/*     */     //   122: monitorexit
/*     */     //   123: aload 9
/*     */     //   125: athrow
/*     */     // Line number table:
/*     */     //   Java source line #242	-> byte code offset #0
/*     */     //   Java source line #243	-> byte code offset #5
/*     */     //   Java source line #245	-> byte code offset #7
/*     */     //   Java source line #246	-> byte code offset #14
/*     */     //   Java source line #247	-> byte code offset #19
/*     */     //   Java source line #248	-> byte code offset #37
/*     */     //   Java source line #249	-> byte code offset #41
/*     */     //   Java source line #250	-> byte code offset #46
/*     */     //   Java source line #251	-> byte code offset #57
/*     */     //   Java source line #252	-> byte code offset #65
/*     */     //   Java source line #254	-> byte code offset #72
/*     */     //   Java source line #255	-> byte code offset #76
/*     */     //   Java source line #256	-> byte code offset #81
/*     */     //   Java source line #257	-> byte code offset #92
/*     */     //   Java source line #258	-> byte code offset #100
/*     */     //   Java source line #261	-> byte code offset #107
/*     */     //   Java source line #262	-> byte code offset #111
/*     */     //   Java source line #263	-> byte code offset #119
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	126	0	this	SynchronizedDoubleHistogram
/*     */     //   0	126	1	other	Object
/*     */     //   18	65	2	otherHistogram	DoubleHistogram
/*     */     //   109	13	2	Ljava/lang/Object;	Object
/*     */     //   39	29	3	Ljava/lang/Object;	Object
/*     */     //   74	29	3	Ljava/lang/Object;	Object
/*     */     //   43	17	4	Ljava/lang/Object;	Object
/*     */     //   78	17	4	Ljava/lang/Object;	Object
/*     */     //   57	6	5	localObject1	Object
/*     */     //   65	5	6	localObject2	Object
/*     */     //   92	6	7	localObject3	Object
/*     */     //   100	5	8	localObject4	Object
/*     */     //   119	5	9	localObject5	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   46	54	57	finally
/*     */     //   57	62	57	finally
/*     */     //   41	56	65	finally
/*     */     //   57	69	65	finally
/*     */     //   81	89	92	finally
/*     */     //   92	97	92	finally
/*     */     //   76	91	100	finally
/*     */     //   92	104	100	finally
/*     */     //   111	118	119	finally
/*     */     //   119	123	119	finally
/*     */   }
/*     */   
/*     */   public synchronized long getTotalCount()
/*     */   {
/* 269 */     return super.getTotalCount();
/*     */   }
/*     */   
/*     */   public synchronized double getIntegerToDoubleValueConversionRatio()
/*     */   {
/* 274 */     return super.getIntegerToDoubleValueConversionRatio();
/*     */   }
/*     */   
/*     */   public synchronized int getNumberOfSignificantValueDigits()
/*     */   {
/* 279 */     return super.getNumberOfSignificantValueDigits();
/*     */   }
/*     */   
/*     */   public synchronized long getHighestToLowestValueRatio()
/*     */   {
/* 284 */     return super.getHighestToLowestValueRatio();
/*     */   }
/*     */   
/*     */   public synchronized double sizeOfEquivalentValueRange(double value)
/*     */   {
/* 289 */     return super.sizeOfEquivalentValueRange(value);
/*     */   }
/*     */   
/*     */   public synchronized double lowestEquivalentValue(double value)
/*     */   {
/* 294 */     return super.lowestEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized double highestEquivalentValue(double value)
/*     */   {
/* 299 */     return super.highestEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized double medianEquivalentValue(double value)
/*     */   {
/* 304 */     return super.medianEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized double nextNonEquivalentValue(double value)
/*     */   {
/* 309 */     return super.nextNonEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized boolean valuesAreEquivalent(double value1, double value2)
/*     */   {
/* 314 */     return super.valuesAreEquivalent(value1, value2);
/*     */   }
/*     */   
/*     */   public synchronized int getEstimatedFootprintInBytes()
/*     */   {
/* 319 */     return super.getEstimatedFootprintInBytes();
/*     */   }
/*     */   
/*     */   public synchronized long getStartTimeStamp()
/*     */   {
/* 324 */     return super.getStartTimeStamp();
/*     */   }
/*     */   
/*     */   public synchronized void setStartTimeStamp(long timeStampMsec)
/*     */   {
/* 329 */     super.setStartTimeStamp(timeStampMsec);
/*     */   }
/*     */   
/*     */   public synchronized long getEndTimeStamp()
/*     */   {
/* 334 */     return super.getEndTimeStamp();
/*     */   }
/*     */   
/*     */   public synchronized void setEndTimeStamp(long timeStampMsec)
/*     */   {
/* 339 */     super.setEndTimeStamp(timeStampMsec);
/*     */   }
/*     */   
/*     */   public synchronized double getMinValue()
/*     */   {
/* 344 */     return super.getMinValue();
/*     */   }
/*     */   
/*     */   public synchronized double getMaxValue()
/*     */   {
/* 349 */     return super.getMaxValue();
/*     */   }
/*     */   
/*     */   public synchronized double getMinNonZeroValue()
/*     */   {
/* 354 */     return super.getMinNonZeroValue();
/*     */   }
/*     */   
/*     */   public synchronized double getMaxValueAsDouble()
/*     */   {
/* 359 */     return super.getMaxValueAsDouble();
/*     */   }
/*     */   
/*     */   public synchronized double getMean()
/*     */   {
/* 364 */     return super.getMean();
/*     */   }
/*     */   
/*     */   public synchronized double getStdDeviation()
/*     */   {
/* 369 */     return super.getStdDeviation();
/*     */   }
/*     */   
/*     */   public synchronized double getValueAtPercentile(double percentile)
/*     */   {
/* 374 */     return super.getValueAtPercentile(percentile);
/*     */   }
/*     */   
/*     */   public synchronized double getPercentileAtOrBelowValue(double value)
/*     */   {
/* 379 */     return super.getPercentileAtOrBelowValue(value);
/*     */   }
/*     */   
/*     */   public synchronized double getCountBetweenValues(double lowValue, double highValue)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 385 */     return super.getCountBetweenValues(lowValue, highValue);
/*     */   }
/*     */   
/*     */   public synchronized long getCountAtValue(double value) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 390 */     return super.getCountAtValue(value);
/*     */   }
/*     */   
/*     */   public synchronized Percentiles percentiles(int percentileTicksPerHalfDistance)
/*     */   {
/* 395 */     return super.percentiles(percentileTicksPerHalfDistance);
/*     */   }
/*     */   
/*     */   public synchronized LinearBucketValues linearBucketValues(double valueUnitsPerBucket)
/*     */   {
/* 400 */     return super.linearBucketValues(valueUnitsPerBucket);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized LogarithmicBucketValues logarithmicBucketValues(double valueUnitsInFirstBucket, double logBase)
/*     */   {
/* 406 */     return super.logarithmicBucketValues(valueUnitsInFirstBucket, logBase);
/*     */   }
/*     */   
/*     */   public synchronized RecordedValues recordedValues()
/*     */   {
/* 411 */     return super.recordedValues();
/*     */   }
/*     */   
/*     */   public synchronized AllValues allValues()
/*     */   {
/* 416 */     return super.allValues();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio)
/*     */   {
/* 422 */     super.outputPercentileDistribution(printStream, outputValueUnitScalingRatio);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio)
/*     */   {
/* 429 */     super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat)
/*     */   {
/* 437 */     super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, useCsvFormat);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized int getNeededByteBufferCapacity()
/*     */   {
/* 446 */     return super.getNeededByteBufferCapacity();
/*     */   }
/*     */   
/*     */   public synchronized int encodeIntoByteBuffer(ByteBuffer buffer)
/*     */   {
/* 451 */     return super.encodeIntoByteBuffer(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel)
/*     */   {
/* 458 */     return super.encodeIntoCompressedByteBuffer(targetBuffer, compressionLevel);
/*     */   }
/*     */   
/*     */   public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer)
/*     */   {
/* 463 */     return super.encodeIntoCompressedByteBuffer(targetBuffer);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\SynchronizedDoubleHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */