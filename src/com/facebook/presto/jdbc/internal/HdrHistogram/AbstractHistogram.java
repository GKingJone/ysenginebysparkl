/*      */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*      */ import java.util.zip.DataFormatException;
/*      */ import java.util.zip.Deflater;
/*      */ import java.util.zip.Inflater;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractHistogram
/*      */   extends AbstractHistogramBase
/*      */   implements Serializable
/*      */ {
/*      */   int leadingZeroCountBase;
/*      */   int subBucketHalfCountMagnitude;
/*      */   int unitMagnitude;
/*      */   int subBucketHalfCount;
/*      */   long subBucketMask;
/*      */   long unitMagnitudeMask;
/*  117 */   volatile long maxValue = 0L;
/*  118 */   volatile long minNonZeroValue = Long.MAX_VALUE;
/*      */   
/*      */ 
/*  121 */   private static final AtomicLongFieldUpdater<AbstractHistogram> maxValueUpdater = AtomicLongFieldUpdater.newUpdater(AbstractHistogram.class, "maxValue");
/*      */   
/*  123 */   private static final AtomicLongFieldUpdater<AbstractHistogram> minNonZeroValueUpdater = AtomicLongFieldUpdater.newUpdater(AbstractHistogram.class, "minNonZeroValue");
/*      */   
/*      */   private static final long serialVersionUID = 478450434L;
/*      */   
/*      */   private static final int ENCODING_HEADER_SIZE = 40;
/*      */   
/*      */   private static final int V0_ENCODING_HEADER_SIZE = 32;
/*      */   
/*      */   private static final int V0EncodingCookieBase = 478450440;
/*      */   
/*      */   private static final int V0CompressedEncodingCookieBase = 478450441;
/*      */   
/*      */   private static final int V1EncodingCookieBase = 478450433;
/*      */   
/*      */   private static final int V1CompressedEncodingCookieBase = 478450434;
/*      */   private static final int V2EncodingCookieBase = 478450435;
/*      */   private static final int V2CompressedEncodingCookieBase = 478450436;
/*      */   private static final int V2maxWordSizeInBytes = 9;
/*      */   private static final int encodingCookieBase = 478450435;
/*      */   private static final int compressedEncodingCookieBase = 478450436;
/*      */   
/*      */   abstract long getCountAtIndex(int paramInt);
/*      */   
/*      */   abstract long getCountAtNormalizedIndex(int paramInt);
/*      */   
/*      */   abstract void incrementCountAtIndex(int paramInt);
/*      */   
/*      */   abstract void addToCountAtIndex(int paramInt, long paramLong);
/*      */   
/*      */   abstract void setCountAtIndex(int paramInt, long paramLong);
/*      */   
/*      */   abstract void setCountAtNormalizedIndex(int paramInt, long paramLong);
/*      */   
/*      */   abstract int getNormalizingIndexOffset();
/*      */   
/*      */   abstract void setNormalizingIndexOffset(int paramInt);
/*      */   
/*      */   abstract void shiftNormalizingIndexByOffset(int paramInt, boolean paramBoolean);
/*      */   
/*      */   abstract void setTotalCount(long paramLong);
/*      */   
/*      */   abstract void incrementTotalCount();
/*      */   
/*      */   abstract void addToTotalCount(long paramLong);
/*      */   
/*      */   abstract void clearCounts();
/*      */   
/*      */   abstract int _getEstimatedFootprintInBytes();
/*      */   
/*      */   abstract void resize(long paramLong);
/*      */   
/*      */   public abstract long getTotalCount();
/*      */   
/*      */   void updatedMaxValue(long value)
/*      */   {
/*  178 */     long internalValue = value | this.unitMagnitudeMask;
/*      */     long sampledMaxValue;
/*  180 */     while (internalValue > (sampledMaxValue = this.maxValue)) {
/*  181 */       maxValueUpdater.compareAndSet(this, sampledMaxValue, internalValue);
/*      */     }
/*      */   }
/*      */   
/*      */   final void resetMaxValue(long maxValue) {
/*  186 */     this.maxValue = (maxValue | this.unitMagnitudeMask);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void updateMinNonZeroValue(long value)
/*      */   {
/*  195 */     if (value <= this.unitMagnitudeMask) {
/*  196 */       return;
/*      */     }
/*  198 */     long internalValue = value & (this.unitMagnitudeMask ^ 0xFFFFFFFFFFFFFFFF);
/*      */     long sampledMinNonZeroValue;
/*  200 */     while (internalValue < (sampledMinNonZeroValue = this.minNonZeroValue)) {
/*  201 */       minNonZeroValueUpdater.compareAndSet(this, sampledMinNonZeroValue, internalValue);
/*      */     }
/*      */   }
/*      */   
/*      */   void resetMinNonZeroValue(long minNonZeroValue) {
/*  206 */     long internalValue = minNonZeroValue & (this.unitMagnitudeMask ^ 0xFFFFFFFFFFFFFFFF);
/*  207 */     this.minNonZeroValue = (minNonZeroValue == Long.MAX_VALUE ? minNonZeroValue : internalValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AbstractHistogram(int numberOfSignificantValueDigits)
/*      */   {
/*  228 */     this(1L, 2L, numberOfSignificantValueDigits);
/*  229 */     this.autoResize = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AbstractHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*      */   {
/*  251 */     if (lowestDiscernibleValue < 1L) {
/*  252 */       throw new IllegalArgumentException("lowestDiscernibleValue must be >= 1");
/*      */     }
/*  254 */     if (highestTrackableValue < 2L * lowestDiscernibleValue) {
/*  255 */       throw new IllegalArgumentException("highestTrackableValue must be >= 2 * lowestDiscernibleValue");
/*      */     }
/*  257 */     if ((numberOfSignificantValueDigits < 0) || (numberOfSignificantValueDigits > 5)) {
/*  258 */       throw new IllegalArgumentException("numberOfSignificantValueDigits must be between 0 and 5");
/*      */     }
/*  260 */     this.identity = constructionIdentityCount.getAndIncrement();
/*      */     
/*  262 */     init(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, 1.0D, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AbstractHistogram(AbstractHistogram source)
/*      */   {
/*  271 */     this(source.getLowestDiscernibleValue(), source.getHighestTrackableValue(), source
/*  272 */       .getNumberOfSignificantValueDigits());
/*  273 */     setStartTimeStamp(source.getStartTimeStamp());
/*  274 */     setEndTimeStamp(source.getEndTimeStamp());
/*  275 */     this.autoResize = source.autoResize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void init(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, double integerToDoubleValueConversionRatio, int normalizingIndexOffset)
/*      */   {
/*  284 */     this.lowestDiscernibleValue = lowestDiscernibleValue;
/*  285 */     this.highestTrackableValue = highestTrackableValue;
/*  286 */     this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
/*  287 */     this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
/*  288 */     if (normalizingIndexOffset != 0) {
/*  289 */       setNormalizingIndexOffset(normalizingIndexOffset);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  297 */     long largestValueWithSingleUnitResolution = 2L * Math.pow(10.0D, numberOfSignificantValueDigits);
/*      */     
/*  299 */     this.unitMagnitude = ((int)Math.floor(Math.log(lowestDiscernibleValue) / Math.log(2.0D)));
/*  300 */     this.unitMagnitudeMask = ((1 << this.unitMagnitude) - 1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  305 */     int subBucketCountMagnitude = (int)Math.ceil(Math.log(largestValueWithSingleUnitResolution) / Math.log(2.0D));
/*  306 */     this.subBucketHalfCountMagnitude = ((subBucketCountMagnitude > 1 ? subBucketCountMagnitude : 1) - 1);
/*  307 */     this.subBucketCount = ((int)Math.pow(2.0D, this.subBucketHalfCountMagnitude + 1));
/*  308 */     this.subBucketHalfCount = (this.subBucketCount / 2);
/*  309 */     this.subBucketMask = (this.subBucketCount - 1L << this.unitMagnitude);
/*      */     
/*      */ 
/*      */ 
/*  313 */     establishSize(highestTrackableValue);
/*      */     
/*      */ 
/*      */ 
/*  317 */     this.leadingZeroCountBase = (64 - this.unitMagnitude - this.subBucketHalfCountMagnitude - 1);
/*      */     
/*  319 */     this.percentileIterator = new PercentileIterator(this, 1);
/*  320 */     this.recordedValuesIterator = new RecordedValuesIterator(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final void establishSize(long newHighestTrackableValue)
/*      */   {
/*  341 */     this.countsArrayLength = determineArrayLengthNeeded(newHighestTrackableValue);
/*      */     
/*  343 */     this.bucketCount = getBucketsNeededToCoverValue(newHighestTrackableValue);
/*      */     
/*  345 */     this.highestTrackableValue = newHighestTrackableValue;
/*      */   }
/*      */   
/*      */   final int determineArrayLengthNeeded(long highestTrackableValue) {
/*  349 */     if (highestTrackableValue < 2L * this.lowestDiscernibleValue) {
/*  350 */       throw new IllegalArgumentException("highestTrackableValue (" + highestTrackableValue + ") cannot be < (2 * lowestDiscernibleValue)");
/*      */     }
/*      */     
/*      */ 
/*  354 */     int countsArrayLength = getLengthForNumberOfBuckets(getBucketsNeededToCoverValue(highestTrackableValue));
/*  355 */     return countsArrayLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAutoResize()
/*      */   {
/*  370 */     return this.autoResize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAutoResize(boolean autoResize)
/*      */   {
/*  379 */     this.autoResize = autoResize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void recordValue(long value)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  397 */     recordSingleValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void recordValueWithCount(long value, long count)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  408 */     recordCountAtValue(count, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  434 */     recordSingleValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public void recordValue(long value, long expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  451 */     recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*      */   }
/*      */   
/*      */   private void updateMinAndMax(long value) {
/*  455 */     if (value > this.maxValue) {
/*  456 */       updatedMaxValue(value);
/*      */     }
/*  458 */     if ((value < this.minNonZeroValue) && (value != 0L)) {
/*  459 */       updateMinNonZeroValue(value);
/*      */     }
/*      */   }
/*      */   
/*      */   private void recordCountAtValue(long count, long value) throws ArrayIndexOutOfBoundsException
/*      */   {
/*  465 */     int countsIndex = countsArrayIndex(value);
/*      */     try {
/*  467 */       addToCountAtIndex(countsIndex, count);
/*      */     } catch (ArrayIndexOutOfBoundsException ex) {
/*  469 */       handleRecordException(count, value, ex);
/*      */     } catch (IndexOutOfBoundsException ex) {
/*  471 */       handleRecordException(count, value, ex);
/*      */     }
/*  473 */     updateMinAndMax(value);
/*  474 */     addToTotalCount(count);
/*      */   }
/*      */   
/*      */   private void recordSingleValue(long value) throws ArrayIndexOutOfBoundsException {
/*  478 */     int countsIndex = countsArrayIndex(value);
/*      */     try {
/*  480 */       incrementCountAtIndex(countsIndex);
/*      */     } catch (ArrayIndexOutOfBoundsException ex) {
/*  482 */       handleRecordException(1L, value, ex);
/*      */     } catch (IndexOutOfBoundsException ex) {
/*  484 */       handleRecordException(1L, value, ex);
/*      */     }
/*  486 */     updateMinAndMax(value);
/*  487 */     incrementTotalCount();
/*      */   }
/*      */   
/*      */   private void handleRecordException(long count, long value, Exception ex) {
/*  491 */     if (!this.autoResize) {
/*  492 */       throw new ArrayIndexOutOfBoundsException("value outside of histogram covered range. Caused by: " + ex);
/*      */     }
/*  494 */     resize(value);
/*  495 */     int countsIndex = countsArrayIndex(value);
/*  496 */     addToCountAtIndex(countsIndex, count);
/*  497 */     this.highestTrackableValue = highestEquivalentValue(valueFromIndex(this.countsArrayLength - 1));
/*      */   }
/*      */   
/*      */   private void recordValueWithCountAndExpectedInterval(long value, long count, long expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  503 */     recordCountAtValue(count, value);
/*  504 */     if (expectedIntervalBetweenValueSamples <= 0L)
/*  505 */       return;
/*  506 */     for (long missingValue = value - expectedIntervalBetweenValueSamples; 
/*  507 */         missingValue >= expectedIntervalBetweenValueSamples; 
/*  508 */         missingValue -= expectedIntervalBetweenValueSamples) {
/*  509 */       recordCountAtValue(count, missingValue);
/*      */     }
/*      */   }
/*      */   
/*      */   private void recordSingleValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  516 */     recordSingleValue(value);
/*  517 */     if (expectedIntervalBetweenValueSamples <= 0L)
/*  518 */       return;
/*  519 */     for (long missingValue = value - expectedIntervalBetweenValueSamples; 
/*  520 */         missingValue >= expectedIntervalBetweenValueSamples; 
/*  521 */         missingValue -= expectedIntervalBetweenValueSamples) {
/*  522 */       recordSingleValue(missingValue);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reset()
/*      */   {
/*  538 */     clearCounts();
/*  539 */     resetMaxValue(0L);
/*  540 */     resetMinNonZeroValue(Long.MAX_VALUE);
/*  541 */     setNormalizingIndexOffset(0);
/*  542 */     this.startTimeStampMsec = Long.MAX_VALUE;
/*  543 */     this.endTimeStampMsec = 0L;
/*  544 */     this.tag = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract AbstractHistogram copy();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract AbstractHistogram copyCorrectedForCoordinatedOmission(long paramLong);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyInto(AbstractHistogram targetHistogram)
/*      */   {
/*  592 */     targetHistogram.reset();
/*  593 */     targetHistogram.add(this);
/*  594 */     targetHistogram.setStartTimeStamp(this.startTimeStampMsec);
/*  595 */     targetHistogram.setEndTimeStamp(this.endTimeStampMsec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyIntoCorrectedForCoordinatedOmission(AbstractHistogram targetHistogram, long expectedIntervalBetweenValueSamples)
/*      */   {
/*  609 */     targetHistogram.reset();
/*  610 */     targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/*  611 */     targetHistogram.setStartTimeStamp(this.startTimeStampMsec);
/*  612 */     targetHistogram.setEndTimeStamp(this.endTimeStampMsec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void add(AbstractHistogram otherHistogram)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  634 */     long highestRecordableValue = highestEquivalentValue(valueFromIndex(this.countsArrayLength - 1));
/*  635 */     if (highestRecordableValue < otherHistogram.getMaxValue()) {
/*  636 */       if (!isAutoResize()) {
/*  637 */         throw new ArrayIndexOutOfBoundsException("The other histogram includes values that do not fit in this histogram's range.");
/*      */       }
/*      */       
/*  640 */       resize(otherHistogram.getMaxValue());
/*      */     }
/*  642 */     if ((this.bucketCount == otherHistogram.bucketCount) && (this.subBucketCount == otherHistogram.subBucketCount) && (this.unitMagnitude == otherHistogram.unitMagnitude))
/*      */     {
/*      */ 
/*  645 */       if ((getNormalizingIndexOffset() == otherHistogram.getNormalizingIndexOffset()) && (!(otherHistogram instanceof ConcurrentHistogram)))
/*      */       {
/*      */ 
/*  648 */         long observedOtherTotalCount = 0L;
/*  649 */         for (int i = 0; i < otherHistogram.countsArrayLength; i++) {
/*  650 */           long otherCount = otherHistogram.getCountAtIndex(i);
/*  651 */           if (otherCount > 0L) {
/*  652 */             addToCountAtIndex(i, otherCount);
/*  653 */             observedOtherTotalCount += otherCount;
/*      */           }
/*      */         }
/*  656 */         setTotalCount(getTotalCount() + observedOtherTotalCount);
/*  657 */         updatedMaxValue(Math.max(getMaxValue(), otherHistogram.getMaxValue()));
/*  658 */         updateMinNonZeroValue(Math.min(getMinNonZeroValue(), otherHistogram.getMinNonZeroValue()));
/*      */         
/*      */ 
/*      */         break label269;
/*      */       }
/*      */     }
/*      */     
/*  665 */     int otherMaxIndex = otherHistogram.countsArrayIndex(otherHistogram.getMaxValue());
/*  666 */     long otherCount = otherHistogram.getCountAtIndex(otherMaxIndex);
/*  667 */     recordValueWithCount(otherHistogram.valueFromIndex(otherMaxIndex), otherCount);
/*      */     
/*      */ 
/*  670 */     for (int i = 0; i < otherMaxIndex; i++) {
/*  671 */       otherCount = otherHistogram.getCountAtIndex(i);
/*  672 */       if (otherCount > 0L) {
/*  673 */         recordValueWithCount(otherHistogram.valueFromIndex(i), otherCount);
/*      */       }
/*      */     }
/*      */     label269:
/*  677 */     setStartTimeStamp(Math.min(this.startTimeStampMsec, otherHistogram.startTimeStampMsec));
/*  678 */     setEndTimeStamp(Math.max(this.endTimeStampMsec, otherHistogram.endTimeStampMsec));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void subtract(AbstractHistogram otherHistogram)
/*      */     throws ArrayIndexOutOfBoundsException, IllegalArgumentException
/*      */   {
/*  692 */     long highestRecordableValue = valueFromIndex(this.countsArrayLength - 1);
/*  693 */     if (highestRecordableValue < otherHistogram.getMaxValue()) {
/*  694 */       if (!isAutoResize()) {
/*  695 */         throw new ArrayIndexOutOfBoundsException("The other histogram includes values that do not fit in this histogram's range.");
/*      */       }
/*      */       
/*  698 */       resize(otherHistogram.getMaxValue());
/*      */     }
/*  700 */     for (int i = 0; i < otherHistogram.countsArrayLength; i++) {
/*  701 */       long otherCount = otherHistogram.getCountAtIndex(i);
/*  702 */       if (otherCount > 0L) {
/*  703 */         long otherValue = otherHistogram.valueFromIndex(i);
/*  704 */         if (getCountAtValue(otherValue) < otherCount)
/*      */         {
/*  706 */           throw new IllegalArgumentException("otherHistogram count (" + otherCount + ") at value " + otherValue + " is larger than this one's (" + getCountAtValue(otherValue) + ")");
/*      */         }
/*  708 */         recordValueWithCount(otherValue, -otherCount);
/*      */       }
/*      */     }
/*      */     
/*  712 */     if ((getCountAtValue(getMaxValue()) <= 0L) || (getCountAtValue(getMinNonZeroValue()) <= 0L)) {
/*  713 */       establishInternalTackingValues();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addWhileCorrectingForCoordinatedOmission(AbstractHistogram otherHistogram, long expectedIntervalBetweenValueSamples)
/*      */   {
/*  742 */     AbstractHistogram toHistogram = this;
/*      */     
/*  744 */     for (HistogramIterationValue v : otherHistogram.recordedValues()) {
/*  745 */       toHistogram.recordValueWithCountAndExpectedInterval(v.getValueIteratedTo(), v
/*  746 */         .getCountAtValueIteratedTo(), expectedIntervalBetweenValueSamples);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void shiftValuesLeft(int numberOfBinaryOrdersOfMagnitude)
/*      */   {
/*  770 */     if (numberOfBinaryOrdersOfMagnitude < 0) {
/*  771 */       throw new IllegalArgumentException("Cannot shift by a negative number of magnitudes");
/*      */     }
/*      */     
/*  774 */     if (numberOfBinaryOrdersOfMagnitude == 0) {
/*  775 */       return;
/*      */     }
/*  777 */     if (getTotalCount() == getCountAtIndex(0))
/*      */     {
/*  779 */       return;
/*      */     }
/*      */     
/*  782 */     int shiftAmount = numberOfBinaryOrdersOfMagnitude << this.subBucketHalfCountMagnitude;
/*  783 */     int maxValueIndex = countsArrayIndex(getMaxValue());
/*      */     
/*  785 */     if (maxValueIndex >= this.countsArrayLength - shiftAmount) {
/*  786 */       throw new ArrayIndexOutOfBoundsException("Operation would overflow, would discard recorded value counts");
/*      */     }
/*      */     
/*      */ 
/*  790 */     long maxValueBeforeShift = maxValueUpdater.getAndSet(this, 0L);
/*  791 */     long minNonZeroValueBeforeShift = minNonZeroValueUpdater.getAndSet(this, Long.MAX_VALUE);
/*      */     
/*  793 */     boolean lowestHalfBucketPopulated = minNonZeroValueBeforeShift < this.subBucketHalfCount;
/*      */     
/*      */ 
/*  796 */     shiftNormalizingIndexByOffset(shiftAmount, lowestHalfBucketPopulated);
/*      */     
/*      */ 
/*  799 */     updateMinAndMax(maxValueBeforeShift << numberOfBinaryOrdersOfMagnitude);
/*  800 */     if (minNonZeroValueBeforeShift < Long.MAX_VALUE) {
/*  801 */       updateMinAndMax(minNonZeroValueBeforeShift << numberOfBinaryOrdersOfMagnitude);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   void nonConcurrentNormalizingIndexShift(int shiftAmount, boolean lowestHalfBucketPopulated)
/*      */   {
/*  808 */     long zeroValueCount = getCountAtIndex(0);
/*  809 */     setCountAtIndex(0, 0L);
/*      */     
/*  811 */     setNormalizingIndexOffset(getNormalizingIndexOffset() + shiftAmount);
/*      */     
/*      */ 
/*  814 */     if (lowestHalfBucketPopulated) {
/*  815 */       shiftLowestHalfBucketContentsLeft(shiftAmount);
/*      */     }
/*      */     
/*      */ 
/*  819 */     setCountAtIndex(0, zeroValueCount);
/*      */   }
/*      */   
/*      */   void shiftLowestHalfBucketContentsLeft(int shiftAmount) {
/*  823 */     int numberOfBinaryOrdersOfMagnitude = shiftAmount >> this.subBucketHalfCountMagnitude;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  840 */     for (int fromIndex = 1; fromIndex < this.subBucketHalfCount; fromIndex++) {
/*  841 */       long toValue = valueFromIndex(fromIndex) << numberOfBinaryOrdersOfMagnitude;
/*  842 */       int toIndex = countsArrayIndex(toValue);
/*  843 */       long countAtFromIndex = getCountAtNormalizedIndex(fromIndex);
/*  844 */       setCountAtIndex(toIndex, countAtFromIndex);
/*  845 */       setCountAtNormalizedIndex(fromIndex, 0L);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void shiftValuesRight(int numberOfBinaryOrdersOfMagnitude)
/*      */   {
/*  869 */     if (numberOfBinaryOrdersOfMagnitude < 0) {
/*  870 */       throw new IllegalArgumentException("Cannot shift by a negative number of magnitudes");
/*      */     }
/*      */     
/*  873 */     if (numberOfBinaryOrdersOfMagnitude == 0) {
/*  874 */       return;
/*      */     }
/*  876 */     if (getTotalCount() == getCountAtIndex(0))
/*      */     {
/*  878 */       return;
/*      */     }
/*      */     
/*  881 */     int shiftAmount = this.subBucketHalfCount * numberOfBinaryOrdersOfMagnitude;
/*      */     
/*      */ 
/*  884 */     int minNonZeroValueIndex = countsArrayIndex(getMinNonZeroValue());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  910 */     if (minNonZeroValueIndex < shiftAmount + this.subBucketHalfCount) {
/*  911 */       throw new ArrayIndexOutOfBoundsException("Operation would underflow and lose precision of already recorded value counts");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  917 */     long maxValueBeforeShift = maxValueUpdater.getAndSet(this, 0L);
/*  918 */     long minNonZeroValueBeforeShift = minNonZeroValueUpdater.getAndSet(this, Long.MAX_VALUE);
/*      */     
/*      */ 
/*  921 */     shiftNormalizingIndexByOffset(-shiftAmount, false);
/*      */     
/*      */ 
/*  924 */     updateMinAndMax(maxValueBeforeShift >> numberOfBinaryOrdersOfMagnitude);
/*  925 */     if (minNonZeroValueBeforeShift < Long.MAX_VALUE) {
/*  926 */       updateMinAndMax(minNonZeroValueBeforeShift >> numberOfBinaryOrdersOfMagnitude);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object other)
/*      */   {
/*  945 */     if (this == other) {
/*  946 */       return true;
/*      */     }
/*  948 */     if (!(other instanceof AbstractHistogram)) {
/*  949 */       return false;
/*      */     }
/*  951 */     AbstractHistogram that = (AbstractHistogram)other;
/*  952 */     if ((this.lowestDiscernibleValue != that.lowestDiscernibleValue) || (this.numberOfSignificantValueDigits != that.numberOfSignificantValueDigits) || (this.integerToDoubleValueConversionRatio != that.integerToDoubleValueConversionRatio))
/*      */     {
/*      */ 
/*  955 */       return false;
/*      */     }
/*  957 */     if (getTotalCount() != that.getTotalCount()) {
/*  958 */       return false;
/*      */     }
/*  960 */     if (getMaxValue() != that.getMaxValue()) {
/*  961 */       return false;
/*      */     }
/*  963 */     if (getMinNonZeroValue() != that.getMinNonZeroValue()) {
/*  964 */       return false;
/*      */     }
/*  966 */     for (int i = 0; i < this.countsArrayLength; i++) {
/*  967 */       if (getCountAtIndex(i) != that.getCountAtIndex(i)) {
/*  968 */         return false;
/*      */       }
/*      */     }
/*  971 */     return true;
/*      */   }
/*      */   
/*      */   public int hashCode()
/*      */   {
/*  976 */     int h = 0;
/*  977 */     h = oneAtATimeHashStep(h, this.unitMagnitude);
/*  978 */     h = oneAtATimeHashStep(h, this.numberOfSignificantValueDigits);
/*  979 */     h = oneAtATimeHashStep(h, (int)getTotalCount());
/*  980 */     h = oneAtATimeHashStep(h, (int)getMaxValue());
/*  981 */     h = oneAtATimeHashStep(h, (int)getMinNonZeroValue());
/*  982 */     h += (h << 3);
/*  983 */     h ^= h >> 11;
/*  984 */     h += (h << 15);
/*  985 */     return h;
/*      */   }
/*      */   
/*      */   private int oneAtATimeHashStep(int h, int v) {
/*  989 */     h += v;
/*  990 */     h += (h << 10);
/*  991 */     h ^= h >> 6;
/*  992 */     return h;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLowestDiscernibleValue()
/*      */   {
/* 1008 */     return this.lowestDiscernibleValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getHighestTrackableValue()
/*      */   {
/* 1016 */     return this.highestTrackableValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNumberOfSignificantValueDigits()
/*      */   {
/* 1024 */     return this.numberOfSignificantValueDigits;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long sizeOfEquivalentValueRange(long value)
/*      */   {
/* 1036 */     int bucketIndex = getBucketIndex(value);
/* 1037 */     int subBucketIndex = getSubBucketIndex(value, bucketIndex);
/* 1038 */     long distanceToNextValue = 1L << this.unitMagnitude + (subBucketIndex >= this.subBucketCount ? bucketIndex + 1 : bucketIndex);
/*      */     
/* 1040 */     return distanceToNextValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long lowestEquivalentValue(long value)
/*      */   {
/* 1052 */     int bucketIndex = getBucketIndex(value);
/* 1053 */     int subBucketIndex = getSubBucketIndex(value, bucketIndex);
/* 1054 */     long thisValueBaseLevel = valueFromIndex(bucketIndex, subBucketIndex);
/* 1055 */     return thisValueBaseLevel;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long highestEquivalentValue(long value)
/*      */   {
/* 1067 */     return nextNonEquivalentValue(value) - 1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long medianEquivalentValue(long value)
/*      */   {
/* 1079 */     return lowestEquivalentValue(value) + (sizeOfEquivalentValueRange(value) >> 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long nextNonEquivalentValue(long value)
/*      */   {
/* 1091 */     return lowestEquivalentValue(value) + sizeOfEquivalentValueRange(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean valuesAreEquivalent(long value1, long value2)
/*      */   {
/* 1104 */     return lowestEquivalentValue(value1) == lowestEquivalentValue(value2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getEstimatedFootprintInBytes()
/*      */   {
/* 1113 */     return _getEstimatedFootprintInBytes();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getStartTimeStamp()
/*      */   {
/* 1130 */     return this.startTimeStampMsec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStartTimeStamp(long timeStampMsec)
/*      */   {
/* 1139 */     this.startTimeStampMsec = timeStampMsec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getEndTimeStamp()
/*      */   {
/* 1148 */     return this.endTimeStampMsec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEndTimeStamp(long timeStampMsec)
/*      */   {
/* 1157 */     this.endTimeStampMsec = timeStampMsec;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTag()
/*      */   {
/* 1165 */     return this.tag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTag(String tag)
/*      */   {
/* 1173 */     this.tag = tag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getMinValue()
/*      */   {
/* 1191 */     if ((getCountAtIndex(0) > 0L) || (getTotalCount() == 0L)) {
/* 1192 */       return 0L;
/*      */     }
/* 1194 */     return getMinNonZeroValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getMaxValue()
/*      */   {
/* 1204 */     return this.maxValue == 0L ? 0L : highestEquivalentValue(this.maxValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getMinNonZeroValue()
/*      */   {
/* 1215 */     return this.minNonZeroValue == Long.MAX_VALUE ? Long.MAX_VALUE : lowestEquivalentValue(this.minNonZeroValue);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMaxValueAsDouble()
/*      */   {
/* 1225 */     return getMaxValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMean()
/*      */   {
/* 1234 */     if (getTotalCount() == 0L) {
/* 1235 */       return 0.0D;
/*      */     }
/* 1237 */     this.recordedValuesIterator.reset();
/* 1238 */     double totalValue = 0.0D;
/* 1239 */     while (this.recordedValuesIterator.hasNext()) {
/* 1240 */       HistogramIterationValue iterationValue = this.recordedValuesIterator.next();
/*      */       
/* 1242 */       totalValue = totalValue + medianEquivalentValue(iterationValue.getValueIteratedTo()) * iterationValue.getCountAtValueIteratedTo();
/*      */     }
/* 1244 */     return totalValue * 1.0D / getTotalCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getStdDeviation()
/*      */   {
/* 1253 */     if (getTotalCount() == 0L) {
/* 1254 */       return 0.0D;
/*      */     }
/* 1256 */     double mean = getMean();
/* 1257 */     double geometric_deviation_total = 0.0D;
/* 1258 */     this.recordedValuesIterator.reset();
/* 1259 */     while (this.recordedValuesIterator.hasNext()) {
/* 1260 */       HistogramIterationValue iterationValue = this.recordedValuesIterator.next();
/* 1261 */       Double deviation = Double.valueOf(medianEquivalentValue(iterationValue.getValueIteratedTo()) * 1.0D - mean);
/* 1262 */       geometric_deviation_total += deviation.doubleValue() * deviation.doubleValue() * iterationValue.getCountAddedInThisIterationStep();
/*      */     }
/* 1264 */     double std_deviation = Math.sqrt(geometric_deviation_total / getTotalCount());
/* 1265 */     return std_deviation;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getValueAtPercentile(double percentile)
/*      */   {
/* 1284 */     double requestedPercentile = Math.min(percentile, 100.0D);
/* 1285 */     long countAtPercentile = (requestedPercentile / 100.0D * getTotalCount() + 0.5D);
/* 1286 */     countAtPercentile = Math.max(countAtPercentile, 1L);
/* 1287 */     long totalToCurrentIndex = 0L;
/* 1288 */     for (int i = 0; i < this.countsArrayLength; i++) {
/* 1289 */       totalToCurrentIndex += getCountAtIndex(i);
/* 1290 */       if (totalToCurrentIndex >= countAtPercentile) {
/* 1291 */         long valueAtIndex = valueFromIndex(i);
/*      */         
/*      */ 
/* 1294 */         return percentile == 0.0D ? lowestEquivalentValue(valueAtIndex) : highestEquivalentValue(valueAtIndex);
/*      */       }
/*      */     }
/* 1297 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getPercentileAtOrBelowValue(long value)
/*      */   {
/* 1313 */     if (getTotalCount() == 0L) {
/* 1314 */       return 100.0D;
/*      */     }
/* 1316 */     int targetIndex = Math.min(countsArrayIndex(value), this.countsArrayLength - 1);
/* 1317 */     long totalToCurrentIndex = 0L;
/* 1318 */     for (int i = 0; i <= targetIndex; i++) {
/* 1319 */       totalToCurrentIndex += getCountAtIndex(i);
/*      */     }
/* 1321 */     return 100.0D * totalToCurrentIndex / getTotalCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getCountBetweenValues(long lowValue, long highValue)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/* 1336 */     int lowIndex = Math.max(0, countsArrayIndex(lowValue));
/* 1337 */     int highIndex = Math.min(countsArrayIndex(highValue), this.countsArrayLength - 1);
/* 1338 */     long count = 0L;
/* 1339 */     for (int i = lowIndex; i <= highIndex; i++) {
/* 1340 */       count += getCountAtIndex(i);
/*      */     }
/* 1342 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getCountAtValue(long value)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/* 1353 */     int index = Math.min(Math.max(0, countsArrayIndex(value)), this.countsArrayLength - 1);
/* 1354 */     return getCountAtIndex(index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Percentiles percentiles(int percentileTicksPerHalfDistance)
/*      */   {
/* 1369 */     return new Percentiles(this, percentileTicksPerHalfDistance, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LinearBucketValues linearBucketValues(long valueUnitsPerBucket)
/*      */   {
/* 1383 */     return new LinearBucketValues(this, valueUnitsPerBucket, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LogarithmicBucketValues logarithmicBucketValues(long valueUnitsInFirstBucket, double logBase)
/*      */   {
/* 1398 */     return new LogarithmicBucketValues(this, valueUnitsInFirstBucket, logBase, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public RecordedValues recordedValues()
/*      */   {
/* 1411 */     return new RecordedValues(this, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public AllValues allValues()
/*      */   {
/* 1425 */     return new AllValues(this, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public class Percentiles
/*      */     implements Iterable<HistogramIterationValue>
/*      */   {
/*      */     final AbstractHistogram histogram;
/*      */     
/*      */     final int percentileTicksPerHalfDistance;
/*      */     
/*      */ 
/*      */     private Percentiles(AbstractHistogram histogram, int percentileTicksPerHalfDistance)
/*      */     {
/* 1439 */       this.histogram = histogram;
/* 1440 */       this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<HistogramIterationValue> iterator()
/*      */     {
/* 1447 */       return new PercentileIterator(this.histogram, this.percentileTicksPerHalfDistance);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class LinearBucketValues
/*      */     implements Iterable<HistogramIterationValue>
/*      */   {
/*      */     final AbstractHistogram histogram;
/*      */     
/*      */     final long valueUnitsPerBucket;
/*      */     
/*      */ 
/*      */     private LinearBucketValues(AbstractHistogram histogram, long valueUnitsPerBucket)
/*      */     {
/* 1462 */       this.histogram = histogram;
/* 1463 */       this.valueUnitsPerBucket = valueUnitsPerBucket;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<HistogramIterationValue> iterator()
/*      */     {
/* 1470 */       return new LinearIterator(this.histogram, this.valueUnitsPerBucket);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class LogarithmicBucketValues
/*      */     implements Iterable<HistogramIterationValue>
/*      */   {
/*      */     final AbstractHistogram histogram;
/*      */     
/*      */     final long valueUnitsInFirstBucket;
/*      */     
/*      */     final double logBase;
/*      */     
/*      */ 
/*      */     private LogarithmicBucketValues(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase)
/*      */     {
/* 1487 */       this.histogram = histogram;
/* 1488 */       this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
/* 1489 */       this.logBase = logBase;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<HistogramIterationValue> iterator()
/*      */     {
/* 1496 */       return new LogarithmicIterator(this.histogram, this.valueUnitsInFirstBucket, this.logBase);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class RecordedValues
/*      */     implements Iterable<HistogramIterationValue>
/*      */   {
/*      */     final AbstractHistogram histogram;
/*      */     
/*      */ 
/*      */     private RecordedValues(AbstractHistogram histogram)
/*      */     {
/* 1510 */       this.histogram = histogram;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<HistogramIterationValue> iterator()
/*      */     {
/* 1517 */       return new RecordedValuesIterator(this.histogram);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class AllValues
/*      */     implements Iterable<HistogramIterationValue>
/*      */   {
/*      */     final AbstractHistogram histogram;
/*      */     
/*      */ 
/*      */     private AllValues(AbstractHistogram histogram)
/*      */     {
/* 1531 */       this.histogram = histogram;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<HistogramIterationValue> iterator()
/*      */     {
/* 1538 */       return new AllValuesIterator(this.histogram);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio)
/*      */   {
/* 1555 */     outputPercentileDistribution(printStream, 5, outputValueUnitScalingRatio);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio)
/*      */   {
/* 1581 */     outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat)
/*      */   {
/* 1602 */     if (useCsvFormat) {
/* 1603 */       printStream.format("\"Value\",\"Percentile\",\"TotalCount\",\"1/(1-Percentile)\"\n", new Object[0]);
/*      */     } else {
/* 1605 */       printStream.format("%12s %14s %10s %14s\n\n", new Object[] { "Value", "Percentile", "TotalCount", "1/(1-Percentile)" });
/*      */     }
/*      */     
/* 1608 */     PercentileIterator iterator = this.percentileIterator;
/* 1609 */     iterator.reset(percentileTicksPerHalfDistance);
/*      */     String lastLinePercentileFormatString;
/*      */     String percentileFormatString;
/*      */     String lastLinePercentileFormatString;
/* 1613 */     if (useCsvFormat) {
/* 1614 */       String percentileFormatString = "%." + this.numberOfSignificantValueDigits + "f,%.12f,%d,%.2f\n";
/* 1615 */       lastLinePercentileFormatString = "%." + this.numberOfSignificantValueDigits + "f,%.12f,%d,Infinity\n";
/*      */     } else {
/* 1617 */       percentileFormatString = "%12." + this.numberOfSignificantValueDigits + "f %2.12f %10d %14.2f\n";
/* 1618 */       lastLinePercentileFormatString = "%12." + this.numberOfSignificantValueDigits + "f %2.12f %10d\n";
/*      */     }
/*      */     
/* 1621 */     while (iterator.hasNext()) {
/* 1622 */       HistogramIterationValue iterationValue = iterator.next();
/* 1623 */       if (iterationValue.getPercentileLevelIteratedTo() != 100.0D) {
/* 1624 */         printStream.format(Locale.US, percentileFormatString, new Object[] {
/* 1625 */           Double.valueOf(iterationValue.getValueIteratedTo() / outputValueUnitScalingRatio.doubleValue()), 
/* 1626 */           Double.valueOf(iterationValue.getPercentileLevelIteratedTo() / 100.0D), 
/* 1627 */           Long.valueOf(iterationValue.getTotalCountToThisValue()), 
/* 1628 */           Double.valueOf(1.0D / (1.0D - iterationValue.getPercentileLevelIteratedTo() / 100.0D)) });
/*      */       } else {
/* 1630 */         printStream.format(Locale.US, lastLinePercentileFormatString, new Object[] {
/* 1631 */           Double.valueOf(iterationValue.getValueIteratedTo() / outputValueUnitScalingRatio.doubleValue()), 
/* 1632 */           Double.valueOf(iterationValue.getPercentileLevelIteratedTo() / 100.0D), 
/* 1633 */           Long.valueOf(iterationValue.getTotalCountToThisValue()) });
/*      */       }
/*      */     }
/*      */     
/* 1637 */     if (!useCsvFormat)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1650 */       double mean = getMean() / outputValueUnitScalingRatio.doubleValue();
/* 1651 */       double std_deviation = getStdDeviation() / outputValueUnitScalingRatio.doubleValue();
/* 1652 */       printStream.format(Locale.US, "#[Mean    = %12." + this.numberOfSignificantValueDigits + "f, StdDeviation   = %12." + this.numberOfSignificantValueDigits + "f]\n", new Object[] {
/*      */       
/*      */ 
/* 1655 */         Double.valueOf(mean), Double.valueOf(std_deviation) });
/* 1656 */       printStream.format(Locale.US, "#[Max     = %12." + this.numberOfSignificantValueDigits + "f, Total count    = %12d]\n", new Object[] {
/*      */       
/* 1658 */         Double.valueOf(getMaxValue() / outputValueUnitScalingRatio.doubleValue()), Long.valueOf(getTotalCount()) });
/* 1659 */       printStream.format(Locale.US, "#[Buckets = %12d, SubBuckets     = %12d]\n", new Object[] {
/* 1660 */         Integer.valueOf(this.bucketCount), Integer.valueOf(this.subBucketCount) });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void writeObject(ObjectOutputStream o)
/*      */     throws IOException
/*      */   {
/* 1677 */     o.writeLong(this.lowestDiscernibleValue);
/* 1678 */     o.writeLong(this.highestTrackableValue);
/* 1679 */     o.writeInt(this.numberOfSignificantValueDigits);
/* 1680 */     o.writeInt(getNormalizingIndexOffset());
/* 1681 */     o.writeDouble(this.integerToDoubleValueConversionRatio);
/* 1682 */     o.writeLong(getTotalCount());
/*      */     
/*      */ 
/*      */ 
/* 1686 */     o.writeLong(this.maxValue);
/* 1687 */     o.writeLong(this.minNonZeroValue);
/* 1688 */     o.writeLong(this.startTimeStampMsec);
/* 1689 */     o.writeLong(this.endTimeStampMsec);
/* 1690 */     o.writeBoolean(this.autoResize);
/* 1691 */     o.writeInt(this.wordSizeInBytes);
/*      */   }
/*      */   
/*      */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*      */   {
/* 1696 */     long lowestDiscernibleValue = o.readLong();
/* 1697 */     long highestTrackableValue = o.readLong();
/* 1698 */     int numberOfSignificantValueDigits = o.readInt();
/* 1699 */     int normalizingIndexOffset = o.readInt();
/* 1700 */     double integerToDoubleValueConversionRatio = o.readDouble();
/* 1701 */     long indicatedTotalCount = o.readLong();
/* 1702 */     long indicatedMaxValue = o.readLong();
/* 1703 */     long indicatedMinNonZeroValue = o.readLong();
/* 1704 */     long indicatedStartTimeStampMsec = o.readLong();
/* 1705 */     long indicatedEndTimeStampMsec = o.readLong();
/* 1706 */     boolean indicatedAutoResize = o.readBoolean();
/* 1707 */     int indicatedwordSizeInBytes = o.readInt();
/*      */     
/* 1709 */     init(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, integerToDoubleValueConversionRatio, normalizingIndexOffset);
/*      */     
/*      */ 
/* 1712 */     setTotalCount(indicatedTotalCount);
/* 1713 */     this.maxValue = indicatedMaxValue;
/* 1714 */     this.minNonZeroValue = indicatedMinNonZeroValue;
/* 1715 */     this.startTimeStampMsec = indicatedStartTimeStampMsec;
/* 1716 */     this.endTimeStampMsec = indicatedEndTimeStampMsec;
/* 1717 */     this.autoResize = indicatedAutoResize;
/* 1718 */     this.wordSizeInBytes = indicatedwordSizeInBytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNeededByteBufferCapacity()
/*      */   {
/* 1735 */     return getNeededByteBufferCapacity(this.countsArrayLength);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   int getNeededByteBufferCapacity(int relevantLength)
/*      */   {
/* 1742 */     return getNeededPayloadByteBufferCapacity(relevantLength) + 40;
/*      */   }
/*      */   
/*      */   int getNeededPayloadByteBufferCapacity(int relevantLength) {
/* 1746 */     return relevantLength * 9;
/*      */   }
/*      */   
/*      */   int getNeededV0PayloadByteBufferCapacity(int relevantLength) {
/* 1750 */     return relevantLength * this.wordSizeInBytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   abstract void fillCountsArrayFromBuffer(ByteBuffer paramByteBuffer, int paramInt);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int getEncodingCookie()
/*      */   {
/* 1770 */     return 478450451;
/*      */   }
/*      */   
/*      */   private int getCompressedEncodingCookie() {
/* 1774 */     return 478450452;
/*      */   }
/*      */   
/*      */   private static int getCookieBase(int cookie) {
/* 1778 */     return cookie & 0xFF0F;
/*      */   }
/*      */   
/*      */   private static int getWordSizeInBytesFromCookie(int cookie) {
/* 1782 */     if ((getCookieBase(cookie) == 478450435) || 
/* 1783 */       (getCookieBase(cookie) == 478450436)) {
/* 1784 */       return 9;
/*      */     }
/* 1786 */     int sizeByte = (cookie & 0xF0) >> 4;
/* 1787 */     return sizeByte & 0xE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized int encodeIntoByteBuffer(ByteBuffer buffer)
/*      */   {
/* 1796 */     long maxValue = getMaxValue();
/* 1797 */     int relevantLength = countsArrayIndex(maxValue) + 1;
/* 1798 */     if (buffer.capacity() < getNeededByteBufferCapacity(relevantLength))
/*      */     {
/* 1800 */       throw new ArrayIndexOutOfBoundsException("buffer does not have capacity for " + getNeededByteBufferCapacity(relevantLength) + " bytes");
/*      */     }
/* 1802 */     int initialPosition = buffer.position();
/* 1803 */     buffer.putInt(getEncodingCookie());
/* 1804 */     buffer.putInt(0);
/* 1805 */     buffer.putInt(getNormalizingIndexOffset());
/* 1806 */     buffer.putInt(this.numberOfSignificantValueDigits);
/* 1807 */     buffer.putLong(this.lowestDiscernibleValue);
/* 1808 */     buffer.putLong(this.highestTrackableValue);
/* 1809 */     buffer.putDouble(getIntegerToDoubleValueConversionRatio());
/*      */     
/* 1811 */     int payloadStartPosition = buffer.position();
/* 1812 */     fillBufferFromCountsArray(buffer);
/* 1813 */     buffer.putInt(initialPosition + 4, buffer.position() - payloadStartPosition);
/*      */     
/*      */ 
/* 1816 */     return buffer.position() - initialPosition;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel)
/*      */   {
/* 1829 */     int neededCapacity = getNeededByteBufferCapacity(this.countsArrayLength);
/* 1830 */     if ((this.intermediateUncompressedByteBuffer == null) || (this.intermediateUncompressedByteBuffer.capacity() < neededCapacity)) {
/* 1831 */       this.intermediateUncompressedByteBuffer = ByteBuffer.allocate(neededCapacity).order(ByteOrder.BIG_ENDIAN);
/*      */     }
/* 1833 */     this.intermediateUncompressedByteBuffer.clear();
/* 1834 */     int initialTargetPosition = targetBuffer.position();
/*      */     
/* 1836 */     int uncompressedLength = encodeIntoByteBuffer(this.intermediateUncompressedByteBuffer);
/* 1837 */     targetBuffer.putInt(getCompressedEncodingCookie());
/*      */     
/* 1839 */     targetBuffer.putInt(0);
/*      */     
/* 1841 */     Deflater compressor = new Deflater(compressionLevel);
/* 1842 */     compressor.setInput(this.intermediateUncompressedByteBuffer.array(), 0, uncompressedLength);
/* 1843 */     compressor.finish();
/*      */     
/*      */     byte[] targetArray;
/*      */     byte[] targetArray;
/* 1847 */     if (targetBuffer.hasArray()) {
/* 1848 */       targetArray = targetBuffer.array();
/*      */     } else {
/* 1850 */       if ((this.intermediateUncompressedByteArray == null) || 
/* 1851 */         (this.intermediateUncompressedByteArray.length < targetBuffer.capacity())) {
/* 1852 */         this.intermediateUncompressedByteArray = new byte[targetBuffer.capacity()];
/*      */       }
/* 1854 */       targetArray = this.intermediateUncompressedByteArray;
/*      */     }
/*      */     
/* 1857 */     int compressedTargetOffset = initialTargetPosition + 8;
/*      */     
/* 1859 */     int compressedDataLength = compressor.deflate(targetArray, compressedTargetOffset, targetArray.length - compressedTargetOffset);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1864 */     compressor.end();
/*      */     
/* 1866 */     if (!targetBuffer.hasArray()) {
/* 1867 */       targetBuffer.put(targetArray, compressedTargetOffset, compressedDataLength);
/*      */     }
/*      */     
/* 1870 */     targetBuffer.putInt(initialTargetPosition + 4, compressedDataLength);
/* 1871 */     int bytesWritten = compressedDataLength + 8;
/* 1872 */     targetBuffer.position(initialTargetPosition + bytesWritten);
/* 1873 */     return bytesWritten;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer)
/*      */   {
/* 1882 */     return encodeIntoCompressedByteBuffer(targetBuffer, -1);
/*      */   }
/*      */   
/* 1885 */   private static final Class[] constructorArgsTypes = { Long.TYPE, Long.TYPE, Integer.TYPE };
/*      */   
/*      */ 
/*      */   static <T extends AbstractHistogram> T decodeFromByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue)
/*      */   {
/*      */     try
/*      */     {
/* 1892 */       return decodeFromByteBuffer(buffer, histogramClass, minBarForHighestTrackableValue, null);
/*      */     } catch (DataFormatException ex) {
/* 1894 */       throw new RuntimeException(ex);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static <T extends AbstractHistogram> T decodeFromByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue, Inflater decompressor)
/*      */     throws DataFormatException
/*      */   {
/* 1904 */     int cookie = buffer.getInt();
/*      */     
/*      */ 
/*      */ 
/*      */     Double integerToDoubleValueConversionRatio;
/*      */     
/*      */ 
/*      */ 
/* 1912 */     if ((getCookieBase(cookie) == 478450435) || 
/* 1913 */       (getCookieBase(cookie) == 478450433)) {
/* 1914 */       if ((getCookieBase(cookie) == 478450435) && 
/* 1915 */         (getWordSizeInBytesFromCookie(cookie) != 9)) {
/* 1916 */         throw new IllegalArgumentException("The buffer does not contain a Histogram (no valid cookie found)");
/*      */       }
/*      */       
/*      */ 
/* 1920 */       int payloadLengthInBytes = buffer.getInt();
/* 1921 */       int normalizingIndexOffset = buffer.getInt();
/* 1922 */       int numberOfSignificantValueDigits = buffer.getInt();
/* 1923 */       long lowestTrackableUnitValue = buffer.getLong();
/* 1924 */       long highestTrackableValue = buffer.getLong();
/* 1925 */       integerToDoubleValueConversionRatio = Double.valueOf(buffer.getDouble()); } else { int normalizingIndexOffset;
/* 1926 */       if (getCookieBase(cookie) == 478450440) {
/* 1927 */         int numberOfSignificantValueDigits = buffer.getInt();
/* 1928 */         long lowestTrackableUnitValue = buffer.getLong();
/* 1929 */         long highestTrackableValue = buffer.getLong();
/* 1930 */         buffer.getLong();
/* 1931 */         int payloadLengthInBytes = Integer.MAX_VALUE;
/* 1932 */         Double integerToDoubleValueConversionRatio = Double.valueOf(1.0D);
/* 1933 */         normalizingIndexOffset = 0;
/*      */       } else {
/* 1935 */         throw new IllegalArgumentException("The buffer does not contain a Histogram (no valid cookie found)"); } }
/*      */     Double integerToDoubleValueConversionRatio;
/* 1937 */     long lowestTrackableUnitValue; int numberOfSignificantValueDigits; int normalizingIndexOffset; int payloadLengthInBytes; long highestTrackableValue = Math.max(highestTrackableValue, minBarForHighestTrackableValue);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1944 */       Constructor<T> constructor = histogramClass.getConstructor(constructorArgsTypes);
/* 1945 */       T histogram = (AbstractHistogram)constructor.newInstance(new Object[] { Long.valueOf(lowestTrackableUnitValue), Long.valueOf(highestTrackableValue), 
/* 1946 */         Integer.valueOf(numberOfSignificantValueDigits) });
/* 1947 */       histogram.setIntegerToDoubleValueConversionRatio(integerToDoubleValueConversionRatio.doubleValue());
/* 1948 */       histogram.setNormalizingIndexOffset(normalizingIndexOffset);
/*      */     } catch (IllegalAccessException ex) {
/* 1950 */       throw new IllegalArgumentException(ex);
/*      */     } catch (NoSuchMethodException ex) {
/* 1952 */       throw new IllegalArgumentException(ex);
/*      */     } catch (InstantiationException ex) {
/* 1954 */       throw new IllegalArgumentException(ex);
/*      */     } catch (InvocationTargetException ex) {
/* 1956 */       throw new IllegalArgumentException(ex);
/*      */     }
/*      */     
/*      */ 
/*      */     T histogram;
/*      */     
/* 1962 */     int expectedCapacity = Math.min(histogram
/* 1963 */       .getNeededV0PayloadByteBufferCapacity(histogram.countsArrayLength), payloadLengthInBytes);
/*      */     
/*      */     ByteBuffer payLoadSourceBuffer;
/*      */     ByteBuffer payLoadSourceBuffer;
/* 1967 */     if (decompressor == null)
/*      */     {
/* 1969 */       if (expectedCapacity > buffer.remaining()) {
/* 1970 */         throw new IllegalArgumentException("The buffer does not contain the full Histogram payload");
/*      */       }
/* 1972 */       payLoadSourceBuffer = buffer;
/*      */     }
/*      */     else {
/* 1975 */       payLoadSourceBuffer = ByteBuffer.allocate(expectedCapacity).order(ByteOrder.BIG_ENDIAN);
/* 1976 */       int decompressedByteCount = decompressor.inflate(payLoadSourceBuffer.array());
/* 1977 */       if ((payloadLengthInBytes != Integer.MAX_VALUE) && (decompressedByteCount < payloadLengthInBytes)) {
/* 1978 */         throw new IllegalArgumentException("The buffer does not contain the indicated payload amount");
/*      */       }
/*      */     }
/*      */     
/* 1982 */     int filledLength = histogram.fillCountsArrayFromSourceBuffer(payLoadSourceBuffer, expectedCapacity, 
/*      */     
/*      */ 
/* 1985 */       getWordSizeInBytesFromCookie(cookie));
/*      */     
/*      */ 
/* 1988 */     histogram.establishInternalTackingValues(filledLength);
/*      */     
/* 1990 */     return histogram;
/*      */   }
/*      */   
/*      */   private int fillCountsArrayFromSourceBuffer(ByteBuffer sourceBuffer, int lengthInBytes, int wordSizeInBytes) {
/* 1994 */     if ((wordSizeInBytes != 2) && (wordSizeInBytes != 4) && (wordSizeInBytes != 8) && (wordSizeInBytes != 9))
/*      */     {
/* 1996 */       throw new IllegalArgumentException("word size must be 2, 4, 8, or V2maxWordSizeInBytes (9) bytes");
/*      */     }
/*      */     
/* 1999 */     long maxAllowableCountInHistigram = this.wordSizeInBytes == 4 ? 2147483647L : this.wordSizeInBytes == 2 ? 32767L : Long.MAX_VALUE;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2004 */     int dstIndex = 0;
/* 2005 */     int endPosition = sourceBuffer.position() + lengthInBytes;
/* 2006 */     while (sourceBuffer.position() < endPosition)
/*      */     {
/* 2008 */       int zerosCount = 0;
/* 2009 */       long count; if (wordSizeInBytes == 9)
/*      */       {
/* 2011 */         long count = ZigZagEncoding.getLong(sourceBuffer);
/* 2012 */         if (count < 0L) {
/* 2013 */           long zc = -count;
/* 2014 */           if (zc > 2147483647L) {
/* 2015 */             throw new IllegalArgumentException("An encoded zero count of > Integer.MAX_VALUE was encountered in the source");
/*      */           }
/*      */           
/* 2018 */           zerosCount = (int)zc;
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2025 */         count = wordSizeInBytes == 4 ? sourceBuffer.getInt() : wordSizeInBytes == 2 ? sourceBuffer.getShort() : sourceBuffer.getLong();
/*      */       }
/*      */       
/*      */ 
/* 2029 */       if (count > maxAllowableCountInHistigram) {
/* 2030 */         throw new IllegalArgumentException("An encoded count (" + count + ") does not fit in the Histogram's (" + this.wordSizeInBytes + " bytes) was encountered in the source");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2035 */       if (zerosCount > 0) {
/* 2036 */         dstIndex += zerosCount;
/*      */       } else {
/* 2038 */         setCountAtIndex(dstIndex++, count);
/*      */       }
/*      */     }
/* 2041 */     return dstIndex;
/*      */   }
/*      */   
/*      */   synchronized void fillBufferFromCountsArray(ByteBuffer buffer) {
/* 2045 */     int countsLimit = countsArrayIndex(this.maxValue) + 1;
/* 2046 */     int srcIndex = 0;
/*      */     
/* 2048 */     while (srcIndex < countsLimit)
/*      */     {
/*      */ 
/* 2051 */       long count = getCountAtIndex(srcIndex++);
/* 2052 */       if (count < 0L)
/*      */       {
/*      */ 
/*      */ 
/* 2056 */         throw new RuntimeException("Cannot encode histogram containing negative counts (" + count + ") at index " + srcIndex + ", corresponding the value range [" + lowestEquivalentValue(valueFromIndex(srcIndex)) + "," + nextNonEquivalentValue(valueFromIndex(srcIndex)) + ")");
/*      */       }
/*      */       
/* 2059 */       long zerosCount = 0L;
/* 2060 */       if (count == 0L) {
/* 2061 */         zerosCount = 1L;
/* 2062 */         while ((srcIndex < countsLimit) && (getCountAtIndex(srcIndex) == 0L)) {
/* 2063 */           zerosCount += 1L;
/* 2064 */           srcIndex++;
/*      */         }
/*      */       }
/* 2067 */       if (zerosCount > 1L) {
/* 2068 */         ZigZagEncoding.putLong(buffer, -zerosCount);
/*      */       } else {
/* 2070 */         ZigZagEncoding.putLong(buffer, count);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static <T extends AbstractHistogram> T decodeFromCompressedByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue)
/*      */     throws DataFormatException
/*      */   {
/* 2080 */     int initialTargetPosition = buffer.position();
/* 2081 */     int cookie = buffer.getInt();
/*      */     int headerSize;
/* 2083 */     if ((getCookieBase(cookie) == 478450436) || 
/* 2084 */       (getCookieBase(cookie) == 478450434)) {
/* 2085 */       headerSize = 40; } else { int headerSize;
/* 2086 */       if (getCookieBase(cookie) == 478450441) {
/* 2087 */         headerSize = 32;
/*      */       } else
/* 2089 */         throw new IllegalArgumentException("The buffer does not contain a compressed Histogram");
/*      */     }
/*      */     int headerSize;
/* 2092 */     int lengthOfCompressedContents = buffer.getInt();
/* 2093 */     Inflater decompressor = new Inflater();
/*      */     
/* 2095 */     if (buffer.hasArray()) {
/* 2096 */       decompressor.setInput(buffer.array(), initialTargetPosition + 8, lengthOfCompressedContents);
/*      */     } else {
/* 2098 */       byte[] compressedContents = new byte[lengthOfCompressedContents];
/* 2099 */       buffer.get(compressedContents);
/* 2100 */       decompressor.setInput(compressedContents);
/*      */     }
/*      */     
/* 2103 */     ByteBuffer headerBuffer = ByteBuffer.allocate(headerSize).order(ByteOrder.BIG_ENDIAN);
/* 2104 */     decompressor.inflate(headerBuffer.array());
/* 2105 */     T histogram = decodeFromByteBuffer(headerBuffer, histogramClass, minBarForHighestTrackableValue, decompressor);
/*      */     
/*      */ 
/* 2108 */     decompressor.end();
/*      */     
/* 2110 */     return histogram;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void establishInternalTackingValues()
/*      */   {
/* 2122 */     establishInternalTackingValues(this.countsArrayLength);
/*      */   }
/*      */   
/*      */   void establishInternalTackingValues(int lengthToCover) {
/* 2126 */     resetMaxValue(0L);
/* 2127 */     resetMinNonZeroValue(Long.MAX_VALUE);
/* 2128 */     int maxIndex = -1;
/* 2129 */     int minNonZeroIndex = -1;
/* 2130 */     long observedTotalCount = 0L;
/* 2131 */     for (int index = 0; index < lengthToCover; index++) {
/*      */       long countAtIndex;
/* 2133 */       if ((countAtIndex = getCountAtIndex(index)) > 0L) {
/* 2134 */         observedTotalCount += countAtIndex;
/* 2135 */         maxIndex = index;
/* 2136 */         if ((minNonZeroIndex == -1) && (index != 0)) {
/* 2137 */           minNonZeroIndex = index;
/*      */         }
/*      */       }
/*      */     }
/* 2141 */     if (maxIndex >= 0) {
/* 2142 */       updatedMaxValue(highestEquivalentValue(valueFromIndex(maxIndex)));
/*      */     }
/* 2144 */     if (minNonZeroIndex >= 0) {
/* 2145 */       updateMinNonZeroValue(valueFromIndex(minNonZeroIndex));
/*      */     }
/* 2147 */     setTotalCount(observedTotalCount);
/*      */   }
/*      */   
/*      */   int getBucketsNeededToCoverValue(long value)
/*      */   {
/* 2152 */     long smallestUntrackableValue = this.subBucketCount << this.unitMagnitude;
/*      */     
/*      */ 
/* 2155 */     int bucketsNeeded = 1;
/* 2156 */     while (smallestUntrackableValue <= value) {
/* 2157 */       if (smallestUntrackableValue > 4611686018427387903L)
/*      */       {
/*      */ 
/* 2160 */         return bucketsNeeded + 1;
/*      */       }
/* 2162 */       smallestUntrackableValue <<= 1;
/* 2163 */       bucketsNeeded++;
/*      */     }
/* 2165 */     return bucketsNeeded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   int getLengthForNumberOfBuckets(int numberOfBuckets)
/*      */   {
/* 2175 */     int lengthNeeded = (numberOfBuckets + 1) * (this.subBucketCount / 2);
/* 2176 */     return lengthNeeded;
/*      */   }
/*      */   
/*      */   int countsArrayIndex(long value) {
/* 2180 */     if (value < 0L) {
/* 2181 */       throw new ArrayIndexOutOfBoundsException("Histogram recorded value cannot be negative.");
/*      */     }
/* 2183 */     int bucketIndex = getBucketIndex(value);
/* 2184 */     int subBucketIndex = getSubBucketIndex(value, bucketIndex);
/* 2185 */     return countsArrayIndex(bucketIndex, subBucketIndex);
/*      */   }
/*      */   
/*      */   private int countsArrayIndex(int bucketIndex, int subBucketIndex) {
/* 2189 */     assert (subBucketIndex < this.subBucketCount);
/* 2190 */     assert ((bucketIndex == 0) || (subBucketIndex >= this.subBucketHalfCount));
/*      */     
/*      */ 
/* 2193 */     int bucketBaseIndex = bucketIndex + 1 << this.subBucketHalfCountMagnitude;
/*      */     
/*      */ 
/*      */ 
/* 2197 */     int offsetInBucket = subBucketIndex - this.subBucketHalfCount;
/*      */     
/* 2199 */     return bucketBaseIndex + offsetInBucket;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   int getBucketIndex(long value)
/*      */   {
/* 2209 */     return this.leadingZeroCountBase - Long.numberOfLeadingZeros(value | this.subBucketMask);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   int getSubBucketIndex(long value, int bucketIndex)
/*      */   {
/* 2219 */     return (int)(value >>> bucketIndex + this.unitMagnitude);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   int normalizeIndex(int index, int normalizingIndexOffset, int arrayLength)
/*      */   {
/* 2226 */     if (normalizingIndexOffset == 0)
/*      */     {
/*      */ 
/* 2229 */       return index;
/*      */     }
/* 2231 */     if ((index > arrayLength) || (index < 0)) {
/* 2232 */       throw new ArrayIndexOutOfBoundsException("index out of covered value range");
/*      */     }
/* 2234 */     int normalizedIndex = index - normalizingIndexOffset;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2239 */     if (normalizedIndex < 0) {
/* 2240 */       normalizedIndex += arrayLength;
/* 2241 */     } else if (normalizedIndex >= arrayLength) {
/* 2242 */       normalizedIndex -= arrayLength;
/*      */     }
/* 2244 */     return normalizedIndex;
/*      */   }
/*      */   
/*      */   final long valueFromIndex(int bucketIndex, int subBucketIndex) {
/* 2248 */     return subBucketIndex << bucketIndex + this.unitMagnitude;
/*      */   }
/*      */   
/*      */   final long valueFromIndex(int index) {
/* 2252 */     int bucketIndex = (index >> this.subBucketHalfCountMagnitude) - 1;
/* 2253 */     int subBucketIndex = (index & this.subBucketHalfCount - 1) + this.subBucketHalfCount;
/* 2254 */     if (bucketIndex < 0) {
/* 2255 */       subBucketIndex -= this.subBucketHalfCount;
/* 2256 */       bucketIndex = 0;
/*      */     }
/* 2258 */     return valueFromIndex(bucketIndex, subBucketIndex);
/*      */   }
/*      */   
/*      */   static int numberOfSubbuckets(int numberOfSignificantValueDigits) {
/* 2262 */     long largestValueWithSingleUnitResolution = 2L * Math.pow(10.0D, numberOfSignificantValueDigits);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2267 */     int subBucketCountMagnitude = (int)Math.ceil(Math.log(largestValueWithSingleUnitResolution) / Math.log(2.0D));
/* 2268 */     int subBucketCount = (int)Math.pow(2.0D, subBucketCountMagnitude);
/* 2269 */     return subBucketCount;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AbstractHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */