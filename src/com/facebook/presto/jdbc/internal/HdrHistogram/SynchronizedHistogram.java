/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SynchronizedHistogram
/*     */   extends Histogram
/*     */ {
/*     */   public SynchronizedHistogram(int numberOfSignificantValueDigits)
/*     */   {
/*  50 */     this(1L, 2L, numberOfSignificantValueDigits);
/*  51 */     setAutoResize(true);
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
/*     */   public SynchronizedHistogram(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  65 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public SynchronizedHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  85 */     super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SynchronizedHistogram(AbstractHistogram source)
/*     */   {
/*  94 */     super(source);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SynchronizedHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */   {
/* 105 */     return (SynchronizedHistogram)decodeFromByteBuffer(buffer, SynchronizedHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SynchronizedHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*     */     throws DataFormatException
/*     */   {
/* 117 */     return (SynchronizedHistogram)decodeFromCompressedByteBuffer(buffer, SynchronizedHistogram.class, minBarForHighestTrackableValue);
/*     */   }
/*     */   
/*     */   public synchronized long getTotalCount()
/*     */   {
/* 122 */     return super.getTotalCount();
/*     */   }
/*     */   
/*     */   public synchronized boolean isAutoResize()
/*     */   {
/* 127 */     return super.isAutoResize();
/*     */   }
/*     */   
/*     */   public synchronized void setAutoResize(boolean autoResize)
/*     */   {
/* 132 */     super.setAutoResize(autoResize);
/*     */   }
/*     */   
/*     */   public synchronized void recordValue(long value) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 137 */     super.recordValue(value);
/*     */   }
/*     */   
/*     */   public synchronized void recordValueWithCount(long value, long count) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 142 */     super.recordValueWithCount(value, count);
/*     */   }
/*     */   
/*     */   public synchronized void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 148 */     super.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public synchronized void recordValue(long value, long expectedIntervalBetweenValueSamples)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 157 */     super.recordValue(value, expectedIntervalBetweenValueSamples);
/*     */   }
/*     */   
/*     */   public synchronized void reset()
/*     */   {
/* 162 */     super.reset();
/*     */   }
/*     */   
/*     */   public synchronized SynchronizedHistogram copy()
/*     */   {
/* 167 */     SynchronizedHistogram toHistogram = new SynchronizedHistogram(this);
/* 168 */     toHistogram.add(this);
/* 169 */     return toHistogram;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized SynchronizedHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples)
/*     */   {
/* 175 */     SynchronizedHistogram toHistogram = new SynchronizedHistogram(this);
/* 176 */     toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/* 177 */     return toHistogram;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void copyInto(AbstractHistogram targetHistogram)
/*     */   {
/* 184 */     if (this.identity < targetHistogram.identity) {
/* 185 */       synchronized (this) {
/* 186 */         synchronized (targetHistogram) {
/* 187 */           super.copyInto(targetHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 191 */       synchronized (targetHistogram) {
/* 192 */         synchronized (this) {
/* 193 */           super.copyInto(targetHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyIntoCorrectedForCoordinatedOmission(AbstractHistogram targetHistogram, long expectedIntervalBetweenValueSamples)
/*     */   {
/* 204 */     if (this.identity < targetHistogram.identity) {
/* 205 */       synchronized (this) {
/* 206 */         synchronized (targetHistogram) {
/* 207 */           super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     } else {
/* 211 */       synchronized (targetHistogram) {
/* 212 */         synchronized (this) {
/* 213 */           super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(AbstractHistogram otherHistogram)
/*     */   {
/* 222 */     if (this.identity < otherHistogram.identity) {
/* 223 */       synchronized (this) {
/* 224 */         synchronized (otherHistogram) {
/* 225 */           super.add(otherHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 229 */       synchronized (otherHistogram) {
/* 230 */         synchronized (this) {
/* 231 */           super.add(otherHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void subtract(AbstractHistogram otherHistogram)
/*     */     throws ArrayIndexOutOfBoundsException, IllegalArgumentException
/*     */   {
/* 241 */     if (this.identity < otherHistogram.identity) {
/* 242 */       synchronized (this) {
/* 243 */         synchronized (otherHistogram) {
/* 244 */           super.subtract(otherHistogram);
/*     */         }
/*     */       }
/*     */     } else {
/* 248 */       synchronized (otherHistogram) {
/* 249 */         synchronized (this) {
/* 250 */           super.subtract(otherHistogram);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addWhileCorrectingForCoordinatedOmission(AbstractHistogram fromHistogram, long expectedIntervalBetweenValueSamples)
/*     */   {
/* 261 */     if (this.identity < fromHistogram.identity) {
/* 262 */       synchronized (this) {
/* 263 */         synchronized (fromHistogram) {
/* 264 */           super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     } else {
/* 268 */       synchronized (fromHistogram) {
/* 269 */         synchronized (this) {
/* 270 */           super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void shiftValuesLeft(int numberOfBinaryOrdersOfMagnitude) {
/* 277 */     super.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude);
/*     */   }
/*     */   
/*     */   public synchronized void shiftValuesRight(int numberOfBinaryOrdersOfMagnitude)
/*     */   {
/* 282 */     super.shiftValuesRight(numberOfBinaryOrdersOfMagnitude);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean equals(Object other)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: if_acmpne +5 -> 7
/*     */     //   5: iconst_1
/*     */     //   6: ireturn
/*     */     //   7: aload_1
/*     */     //   8: instanceof 9
/*     */     //   11: ifeq +90 -> 101
/*     */     //   14: aload_1
/*     */     //   15: checkcast 9	com/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram
/*     */     //   18: astore_2
/*     */     //   19: aload_0
/*     */     //   20: getfield 110	com/facebook/presto/jdbc/internal/HdrHistogram/SynchronizedHistogram:identity	J
/*     */     //   23: aload_2
/*     */     //   24: getfield 111	com/facebook/presto/jdbc/internal/HdrHistogram/AbstractHistogram:identity	J
/*     */     //   27: lcmp
/*     */     //   28: ifge +38 -> 66
/*     */     //   31: aload_0
/*     */     //   32: dup
/*     */     //   33: astore_3
/*     */     //   34: monitorenter
/*     */     //   35: aload_2
/*     */     //   36: dup
/*     */     //   37: astore 4
/*     */     //   39: monitorenter
/*     */     //   40: aload_0
/*     */     //   41: aload_2
/*     */     //   42: invokespecial 141	com/facebook/presto/jdbc/internal/HdrHistogram/Histogram:equals	(Ljava/lang/Object;)Z
/*     */     //   45: aload 4
/*     */     //   47: monitorexit
/*     */     //   48: aload_3
/*     */     //   49: monitorexit
/*     */     //   50: ireturn
/*     */     //   51: astore 5
/*     */     //   53: aload 4
/*     */     //   55: monitorexit
/*     */     //   56: aload 5
/*     */     //   58: athrow
/*     */     //   59: astore 6
/*     */     //   61: aload_3
/*     */     //   62: monitorexit
/*     */     //   63: aload 6
/*     */     //   65: athrow
/*     */     //   66: aload_2
/*     */     //   67: dup
/*     */     //   68: astore_3
/*     */     //   69: monitorenter
/*     */     //   70: aload_0
/*     */     //   71: dup
/*     */     //   72: astore 4
/*     */     //   74: monitorenter
/*     */     //   75: aload_0
/*     */     //   76: aload_2
/*     */     //   77: invokespecial 141	com/facebook/presto/jdbc/internal/HdrHistogram/Histogram:equals	(Ljava/lang/Object;)Z
/*     */     //   80: aload 4
/*     */     //   82: monitorexit
/*     */     //   83: aload_3
/*     */     //   84: monitorexit
/*     */     //   85: ireturn
/*     */     //   86: astore 7
/*     */     //   88: aload 4
/*     */     //   90: monitorexit
/*     */     //   91: aload 7
/*     */     //   93: athrow
/*     */     //   94: astore 8
/*     */     //   96: aload_3
/*     */     //   97: monitorexit
/*     */     //   98: aload 8
/*     */     //   100: athrow
/*     */     //   101: aload_0
/*     */     //   102: dup
/*     */     //   103: astore_2
/*     */     //   104: monitorenter
/*     */     //   105: aload_0
/*     */     //   106: aload_1
/*     */     //   107: invokespecial 141	com/facebook/presto/jdbc/internal/HdrHistogram/Histogram:equals	(Ljava/lang/Object;)Z
/*     */     //   110: aload_2
/*     */     //   111: monitorexit
/*     */     //   112: ireturn
/*     */     //   113: astore 9
/*     */     //   115: aload_2
/*     */     //   116: monitorexit
/*     */     //   117: aload 9
/*     */     //   119: athrow
/*     */     // Line number table:
/*     */     //   Java source line #287	-> byte code offset #0
/*     */     //   Java source line #288	-> byte code offset #5
/*     */     //   Java source line #290	-> byte code offset #7
/*     */     //   Java source line #291	-> byte code offset #14
/*     */     //   Java source line #292	-> byte code offset #19
/*     */     //   Java source line #293	-> byte code offset #31
/*     */     //   Java source line #294	-> byte code offset #35
/*     */     //   Java source line #295	-> byte code offset #40
/*     */     //   Java source line #296	-> byte code offset #51
/*     */     //   Java source line #297	-> byte code offset #59
/*     */     //   Java source line #299	-> byte code offset #66
/*     */     //   Java source line #300	-> byte code offset #70
/*     */     //   Java source line #301	-> byte code offset #75
/*     */     //   Java source line #302	-> byte code offset #86
/*     */     //   Java source line #303	-> byte code offset #94
/*     */     //   Java source line #306	-> byte code offset #101
/*     */     //   Java source line #307	-> byte code offset #105
/*     */     //   Java source line #308	-> byte code offset #113
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	120	0	this	SynchronizedHistogram
/*     */     //   0	120	1	other	Object
/*     */     //   18	59	2	otherHistogram	AbstractHistogram
/*     */     //   103	13	2	Ljava/lang/Object;	Object
/*     */     //   33	29	3	Ljava/lang/Object;	Object
/*     */     //   68	29	3	Ljava/lang/Object;	Object
/*     */     //   37	17	4	Ljava/lang/Object;	Object
/*     */     //   72	17	4	Ljava/lang/Object;	Object
/*     */     //   51	6	5	localObject1	Object
/*     */     //   59	5	6	localObject2	Object
/*     */     //   86	6	7	localObject3	Object
/*     */     //   94	5	8	localObject4	Object
/*     */     //   113	5	9	localObject5	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   40	48	51	finally
/*     */     //   51	56	51	finally
/*     */     //   35	50	59	finally
/*     */     //   51	63	59	finally
/*     */     //   75	83	86	finally
/*     */     //   86	91	86	finally
/*     */     //   70	85	94	finally
/*     */     //   86	98	94	finally
/*     */     //   105	112	113	finally
/*     */     //   113	117	113	finally
/*     */   }
/*     */   
/*     */   public synchronized long getLowestDiscernibleValue()
/*     */   {
/* 314 */     return super.getLowestDiscernibleValue();
/*     */   }
/*     */   
/*     */   public synchronized long getHighestTrackableValue()
/*     */   {
/* 319 */     return super.getHighestTrackableValue();
/*     */   }
/*     */   
/*     */   public synchronized int getNumberOfSignificantValueDigits()
/*     */   {
/* 324 */     return super.getNumberOfSignificantValueDigits();
/*     */   }
/*     */   
/*     */   public synchronized long sizeOfEquivalentValueRange(long value)
/*     */   {
/* 329 */     return super.sizeOfEquivalentValueRange(value);
/*     */   }
/*     */   
/*     */   public synchronized long lowestEquivalentValue(long value)
/*     */   {
/* 334 */     return super.lowestEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized long highestEquivalentValue(long value)
/*     */   {
/* 339 */     return super.highestEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized long medianEquivalentValue(long value)
/*     */   {
/* 344 */     return super.medianEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized long nextNonEquivalentValue(long value)
/*     */   {
/* 349 */     return super.nextNonEquivalentValue(value);
/*     */   }
/*     */   
/*     */   public synchronized boolean valuesAreEquivalent(long value1, long value2)
/*     */   {
/* 354 */     return super.valuesAreEquivalent(value1, value2);
/*     */   }
/*     */   
/*     */   public synchronized int getEstimatedFootprintInBytes()
/*     */   {
/* 359 */     return super.getEstimatedFootprintInBytes();
/*     */   }
/*     */   
/*     */   public synchronized long getStartTimeStamp()
/*     */   {
/* 364 */     return super.getStartTimeStamp();
/*     */   }
/*     */   
/*     */   public synchronized void setStartTimeStamp(long timeStampMsec)
/*     */   {
/* 369 */     super.setStartTimeStamp(timeStampMsec);
/*     */   }
/*     */   
/*     */   public synchronized long getEndTimeStamp()
/*     */   {
/* 374 */     return super.getEndTimeStamp();
/*     */   }
/*     */   
/*     */   public synchronized void setEndTimeStamp(long timeStampMsec)
/*     */   {
/* 379 */     super.setEndTimeStamp(timeStampMsec);
/*     */   }
/*     */   
/*     */   public synchronized long getMinValue()
/*     */   {
/* 384 */     return super.getMinValue();
/*     */   }
/*     */   
/*     */   public synchronized long getMaxValue()
/*     */   {
/* 389 */     return super.getMaxValue();
/*     */   }
/*     */   
/*     */   public synchronized long getMinNonZeroValue()
/*     */   {
/* 394 */     return super.getMinNonZeroValue();
/*     */   }
/*     */   
/*     */   public synchronized double getMaxValueAsDouble()
/*     */   {
/* 399 */     return super.getMaxValueAsDouble();
/*     */   }
/*     */   
/*     */   public synchronized double getMean()
/*     */   {
/* 404 */     return super.getMean();
/*     */   }
/*     */   
/*     */   public synchronized double getStdDeviation()
/*     */   {
/* 409 */     return super.getStdDeviation();
/*     */   }
/*     */   
/*     */   public synchronized long getValueAtPercentile(double percentile)
/*     */   {
/* 414 */     return super.getValueAtPercentile(percentile);
/*     */   }
/*     */   
/*     */   public synchronized double getPercentileAtOrBelowValue(long value)
/*     */   {
/* 419 */     return super.getPercentileAtOrBelowValue(value);
/*     */   }
/*     */   
/*     */   public synchronized long getCountBetweenValues(long lowValue, long highValue) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 424 */     return super.getCountBetweenValues(lowValue, highValue);
/*     */   }
/*     */   
/*     */   public synchronized long getCountAtValue(long value) throws ArrayIndexOutOfBoundsException
/*     */   {
/* 429 */     return super.getCountAtValue(value);
/*     */   }
/*     */   
/*     */   public synchronized Percentiles percentiles(int percentileTicksPerHalfDistance)
/*     */   {
/* 434 */     return super.percentiles(percentileTicksPerHalfDistance);
/*     */   }
/*     */   
/*     */   public synchronized LinearBucketValues linearBucketValues(long valueUnitsPerBucket)
/*     */   {
/* 439 */     return super.linearBucketValues(valueUnitsPerBucket);
/*     */   }
/*     */   
/*     */   public synchronized LogarithmicBucketValues logarithmicBucketValues(long valueUnitsInFirstBucket, double logBase)
/*     */   {
/* 444 */     return super.logarithmicBucketValues(valueUnitsInFirstBucket, logBase);
/*     */   }
/*     */   
/*     */   public synchronized RecordedValues recordedValues()
/*     */   {
/* 449 */     return super.recordedValues();
/*     */   }
/*     */   
/*     */   public synchronized AllValues allValues()
/*     */   {
/* 454 */     return super.allValues();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio)
/*     */   {
/* 460 */     super.outputPercentileDistribution(printStream, outputValueUnitScalingRatio);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio)
/*     */   {
/* 467 */     super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat)
/*     */   {
/* 475 */     super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, useCsvFormat);
/*     */   }
/*     */   
/*     */   public synchronized int getNeededByteBufferCapacity()
/*     */   {
/* 480 */     return super.getNeededByteBufferCapacity();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized int encodeIntoByteBuffer(ByteBuffer buffer)
/*     */   {
/* 486 */     return super.encodeIntoByteBuffer(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel)
/*     */   {
/* 493 */     return super.encodeIntoCompressedByteBuffer(targetBuffer, compressionLevel);
/*     */   }
/*     */   
/*     */   public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer)
/*     */   {
/* 498 */     return super.encodeIntoCompressedByteBuffer(targetBuffer);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*     */   {
/* 503 */     o.defaultReadObject();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\SynchronizedHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */