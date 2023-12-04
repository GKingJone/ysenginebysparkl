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
/*      */ import java.util.Iterator;
/*      */ import java.util.zip.DataFormatException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DoubleHistogram
/*      */   extends EncodableHistogram
/*      */   implements Serializable
/*      */ {
/*      */   static final double highestAllowedValueEver;
/*      */   private long configuredHighestToLowestValueRatio;
/*      */   private volatile double currentLowestValueInAutoRange;
/*      */   private volatile double currentHighestValueLimitInAutoRange;
/*      */   AbstractHistogram integerValuesHistogram;
/*      */   volatile double doubleToIntegerValueConversionRatio;
/*      */   volatile double integerToDoubleValueConversionRatio;
/*   66 */   private boolean autoResize = false;
/*      */   
/*      */   private static final long serialVersionUID = 42L;
/*      */   
/*      */   private static final int DHIST_encodingCookie = 208802382;
/*      */   
/*      */   private static final int DHIST_compressedEncodingCookie = 208802383;
/*      */   
/*      */ 
/*      */   public DoubleHistogram(int numberOfSignificantValueDigits)
/*      */   {
/*   77 */     this(2L, numberOfSignificantValueDigits, Histogram.class, null);
/*   78 */     setAutoResize(true);
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
/*      */   public DoubleHistogram(int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass)
/*      */   {
/*   97 */     this(2L, numberOfSignificantValueDigits, internalCountsHistogramClass, null);
/*   98 */     setAutoResize(true);
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
/*      */   public DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*      */   {
/*  112 */     this(highestToLowestValueRatio, numberOfSignificantValueDigits, Histogram.class);
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
/*      */   protected DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass)
/*      */   {
/*  134 */     this(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram)
/*      */   {
/*  141 */     this(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, internalCountsHistogram, false);
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
/*      */   private DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram, boolean mimicInternalModel)
/*      */   {
/*      */     try
/*      */     {
/*  156 */       if (highestToLowestValueRatio < 2L) {
/*  157 */         throw new IllegalArgumentException("highestToLowestValueRatio must be >= 2");
/*      */       }
/*      */       
/*  160 */       if (highestToLowestValueRatio * Math.pow(10.0D, numberOfSignificantValueDigits) >= 2.305843009213694E18D) {
/*  161 */         throw new IllegalArgumentException("highestToLowestValueRatio * (10^numberOfSignificantValueDigits) must be < (1L << 61)");
/*      */       }
/*      */       
/*  164 */       if (internalCountsHistogramClass == AtomicHistogram.class) {
/*  165 */         throw new IllegalArgumentException("AtomicHistogram cannot be used as an internal counts histogram (does not support shifting). Use ConcurrentHistogram instead.");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  170 */       long integerValueRange = deriveIntegerValueRange(highestToLowestValueRatio, numberOfSignificantValueDigits);
/*      */       
/*      */       double initialLowestValueInAutoRange;
/*      */       AbstractHistogram valuesHistogram;
/*      */       double initialLowestValueInAutoRange;
/*  175 */       if (internalCountsHistogram == null)
/*      */       {
/*      */ 
/*  178 */         Constructor<? extends AbstractHistogram> histogramConstructor = internalCountsHistogramClass.getConstructor(new Class[] { Long.TYPE, Long.TYPE, Integer.TYPE });
/*      */         
/*      */ 
/*  181 */         AbstractHistogram valuesHistogram = (AbstractHistogram)histogramConstructor.newInstance(new Object[] {
/*  182 */           Long.valueOf(1L), 
/*  183 */           Long.valueOf(integerValueRange - 1L), 
/*  184 */           Integer.valueOf(numberOfSignificantValueDigits) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  193 */         initialLowestValueInAutoRange = Math.pow(2.0D, 800.0D); } else { double initialLowestValueInAutoRange;
/*  194 */         if (mimicInternalModel)
/*      */         {
/*  196 */           Constructor<? extends AbstractHistogram> histogramConstructor = internalCountsHistogramClass.getConstructor(new Class[] { AbstractHistogram.class });
/*      */           
/*  198 */           AbstractHistogram valuesHistogram = (AbstractHistogram)histogramConstructor.newInstance(new Object[] { internalCountsHistogram });
/*      */           
/*  200 */           initialLowestValueInAutoRange = Math.pow(2.0D, 800.0D);
/*      */         }
/*      */         else {
/*  203 */           if ((internalCountsHistogram.getLowestDiscernibleValue() != 1L) || 
/*  204 */             (internalCountsHistogram.getHighestTrackableValue() != integerValueRange - 1L) || 
/*  205 */             (internalCountsHistogram.getNumberOfSignificantValueDigits() != numberOfSignificantValueDigits)) {
/*  206 */             throw new IllegalStateException("integer values histogram does not match stated parameters.");
/*      */           }
/*  208 */           valuesHistogram = internalCountsHistogram;
/*      */           
/*      */ 
/*  211 */           initialLowestValueInAutoRange = internalCountsHistogram.getIntegerToDoubleValueConversionRatio() * internalCountsHistogram.subBucketHalfCount;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  216 */       init(highestToLowestValueRatio, initialLowestValueInAutoRange, valuesHistogram);
/*      */     }
/*      */     catch (NoSuchMethodException ex) {
/*  219 */       throw new IllegalArgumentException(ex);
/*      */     } catch (IllegalAccessException ex) {
/*  221 */       throw new IllegalArgumentException(ex);
/*      */     } catch (InstantiationException ex) {
/*  223 */       throw new IllegalArgumentException(ex);
/*      */     } catch (InvocationTargetException ex) {
/*  225 */       throw new IllegalArgumentException(ex);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DoubleHistogram(DoubleHistogram source)
/*      */   {
/*  235 */     this(source.configuredHighestToLowestValueRatio, source
/*  236 */       .getNumberOfSignificantValueDigits(), source.integerValuesHistogram
/*  237 */       .getClass(), source.integerValuesHistogram, true);
/*      */     
/*      */ 
/*      */ 
/*  241 */     setTrackableValueRange(source.currentLowestValueInAutoRange, source.currentHighestValueLimitInAutoRange);
/*      */   }
/*      */   
/*      */   private void init(long configuredHighestToLowestValueRatio, double lowestTrackableUnitValue, AbstractHistogram integerValuesHistogram)
/*      */   {
/*  246 */     this.configuredHighestToLowestValueRatio = configuredHighestToLowestValueRatio;
/*  247 */     this.integerValuesHistogram = integerValuesHistogram;
/*      */     
/*  249 */     long internalHighestToLowestValueRatio = deriveInternalHighestToLowestValueRatio(configuredHighestToLowestValueRatio);
/*  250 */     setTrackableValueRange(lowestTrackableUnitValue, lowestTrackableUnitValue * internalHighestToLowestValueRatio);
/*      */   }
/*      */   
/*      */   private void setTrackableValueRange(double lowestValueInAutoRange, double highestValueInAutoRange) {
/*  254 */     this.currentLowestValueInAutoRange = lowestValueInAutoRange;
/*  255 */     this.currentHighestValueLimitInAutoRange = highestValueInAutoRange;
/*  256 */     this.integerToDoubleValueConversionRatio = (lowestValueInAutoRange / getLowestTrackingIntegerValue());
/*  257 */     this.doubleToIntegerValueConversionRatio = (1.0D / this.integerToDoubleValueConversionRatio);
/*  258 */     this.integerValuesHistogram.setIntegerToDoubleValueConversionRatio(this.integerToDoubleValueConversionRatio);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isAutoResize()
/*      */   {
/*  268 */     return this.autoResize;
/*      */   }
/*      */   
/*      */   public void setAutoResize(boolean autoResize) {
/*  272 */     this.autoResize = autoResize;
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
/*      */   public void recordValue(double value)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  290 */     recordSingleValue(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void recordValueWithCount(double value, long count)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  301 */     recordCountAtValue(count, value);
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
/*      */   public void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  327 */     recordValueWithCountAndExpectedInterval(value, 1L, expectedIntervalBetweenValueSamples);
/*      */   }
/*      */   
/*      */   private void recordCountAtValue(long count, double value) throws ArrayIndexOutOfBoundsException {
/*  331 */     if ((value < this.currentLowestValueInAutoRange) || (value >= this.currentHighestValueLimitInAutoRange))
/*      */     {
/*      */ 
/*  334 */       autoAdjustRangeForValue(value);
/*      */     }
/*      */     
/*  337 */     long integerValue = (value * this.doubleToIntegerValueConversionRatio);
/*  338 */     this.integerValuesHistogram.recordValueWithCount(integerValue, count);
/*      */   }
/*      */   
/*      */   private void recordSingleValue(double value) throws ArrayIndexOutOfBoundsException {
/*  342 */     if ((value < this.currentLowestValueInAutoRange) || (value >= this.currentHighestValueLimitInAutoRange))
/*      */     {
/*      */ 
/*  345 */       autoAdjustRangeForValue(value);
/*      */     }
/*      */     
/*  348 */     long integerValue = (value * this.doubleToIntegerValueConversionRatio);
/*  349 */     this.integerValuesHistogram.recordValue(integerValue);
/*      */   }
/*      */   
/*      */   private void recordValueWithCountAndExpectedInterval(double value, long count, double expectedIntervalBetweenValueSamples)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  355 */     recordCountAtValue(count, value);
/*  356 */     if (expectedIntervalBetweenValueSamples <= 0.0D)
/*  357 */       return;
/*  358 */     for (double missingValue = value - expectedIntervalBetweenValueSamples; 
/*  359 */         missingValue >= expectedIntervalBetweenValueSamples; 
/*  360 */         missingValue -= expectedIntervalBetweenValueSamples) {
/*  361 */       recordCountAtValue(count, missingValue);
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
/*      */   private void autoAdjustRangeForValue(double value)
/*      */   {
/*  375 */     if (value == 0.0D) {
/*  376 */       return;
/*      */     }
/*  378 */     autoAdjustRangeForValueSlowPath(value);
/*      */   }
/*      */   
/*      */   private synchronized void autoAdjustRangeForValueSlowPath(double value) {
/*      */     try {
/*  383 */       if (value < this.currentLowestValueInAutoRange) {
/*  384 */         if (value < 0.0D) {
/*  385 */           throw new ArrayIndexOutOfBoundsException("Negative values cannot be recorded");
/*      */         }
/*      */         do
/*      */         {
/*  389 */           int shiftAmount = findCappedContainingBinaryOrderOfMagnitude(
/*  390 */             Math.ceil(this.currentLowestValueInAutoRange / value) - 1.0D);
/*  391 */           shiftCoveredRangeToTheRight(shiftAmount);
/*      */         }
/*  393 */         while (value < this.currentLowestValueInAutoRange);
/*  394 */       } else if (value >= this.currentHighestValueLimitInAutoRange) {
/*  395 */         if (value > highestAllowedValueEver) {
/*  396 */           throw new ArrayIndexOutOfBoundsException("Values above " + highestAllowedValueEver + " cannot be recorded");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         do
/*      */         {
/*  405 */           int shiftAmount = findCappedContainingBinaryOrderOfMagnitude(
/*  406 */             Math.ceil((value + Math.ulp(value)) / this.currentHighestValueLimitInAutoRange) - 1.0D);
/*  407 */           shiftCoveredRangeToTheLeft(shiftAmount);
/*      */         }
/*  409 */         while (value >= this.currentHighestValueLimitInAutoRange);
/*      */       }
/*      */     } catch (ArrayIndexOutOfBoundsException ex) {
/*  412 */       throw new ArrayIndexOutOfBoundsException("The value " + value + " is out of bounds for histogram, current covered range [" + this.currentLowestValueInAutoRange + ", " + this.currentHighestValueLimitInAutoRange + ") cannot be extended any further.\n" + "Caused by: " + ex);
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
/*      */   private void shiftCoveredRangeToTheRight(int numberOfBinaryOrdersOfMagnitude)
/*      */   {
/*  430 */     double newLowestValueInAutoRange = this.currentLowestValueInAutoRange;
/*  431 */     double newHighestValueLimitInAutoRange = this.currentHighestValueLimitInAutoRange;
/*      */     try
/*      */     {
/*  434 */       double shiftMultiplier = 1.0D / (1L << numberOfBinaryOrdersOfMagnitude);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  441 */       this.currentHighestValueLimitInAutoRange *= shiftMultiplier;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  449 */       if (getTotalCount() > this.integerValuesHistogram.getCountAtIndex(0)) {
/*      */         try
/*      */         {
/*  452 */           this.integerValuesHistogram.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude);
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException ex) {
/*  455 */           handleShiftValuesException(numberOfBinaryOrdersOfMagnitude, ex);
/*      */           
/*  457 */           newHighestValueLimitInAutoRange /= shiftMultiplier;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  463 */           this.integerValuesHistogram.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude);
/*      */         }
/*      */       }
/*      */       
/*  467 */       newLowestValueInAutoRange *= shiftMultiplier;
/*  468 */       newHighestValueLimitInAutoRange *= shiftMultiplier;
/*      */     }
/*      */     finally {
/*  471 */       setTrackableValueRange(newLowestValueInAutoRange, newHighestValueLimitInAutoRange);
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
/*      */   private void shiftCoveredRangeToTheLeft(int numberOfBinaryOrdersOfMagnitude)
/*      */   {
/*  485 */     double newLowestValueInAutoRange = this.currentLowestValueInAutoRange;
/*  486 */     double newHighestValueLimitInAutoRange = this.currentHighestValueLimitInAutoRange;
/*      */     try
/*      */     {
/*  489 */       double shiftMultiplier = 1.0D * (1L << numberOfBinaryOrdersOfMagnitude);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  496 */       this.currentLowestValueInAutoRange *= shiftMultiplier;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  504 */       if (getTotalCount() > this.integerValuesHistogram.getCountAtIndex(0)) {
/*      */         try
/*      */         {
/*  507 */           this.integerValuesHistogram.shiftValuesRight(numberOfBinaryOrdersOfMagnitude);
/*      */           
/*  509 */           newLowestValueInAutoRange *= shiftMultiplier;
/*  510 */           newHighestValueLimitInAutoRange *= shiftMultiplier;
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException ex) {
/*  513 */           handleShiftValuesException(numberOfBinaryOrdersOfMagnitude, ex);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  523 */           newLowestValueInAutoRange /= shiftMultiplier;
/*      */         }
/*      */       }
/*      */       
/*  527 */       newLowestValueInAutoRange *= shiftMultiplier;
/*  528 */       newHighestValueLimitInAutoRange *= shiftMultiplier;
/*      */     }
/*      */     finally {
/*  531 */       setTrackableValueRange(newLowestValueInAutoRange, newHighestValueLimitInAutoRange);
/*      */     }
/*      */   }
/*      */   
/*      */   private void handleShiftValuesException(int numberOfBinaryOrdersOfMagnitude, Exception ex) {
/*  536 */     if (!this.autoResize) {
/*  537 */       throw new ArrayIndexOutOfBoundsException("Value outside of histogram covered range.\nCaused by: " + ex);
/*      */     }
/*      */     
/*  540 */     long highestTrackableValue = this.integerValuesHistogram.getHighestTrackableValue();
/*  541 */     int currentContainingOrderOfMagnitude = findContainingBinaryOrderOfMagnitude(highestTrackableValue);
/*  542 */     int newContainingOrderOfMagnitude = numberOfBinaryOrdersOfMagnitude + currentContainingOrderOfMagnitude;
/*  543 */     if (newContainingOrderOfMagnitude > 63) {
/*  544 */       throw new ArrayIndexOutOfBoundsException("Cannot resize histogram covered range beyond (1L << 63) / (1L << " + this.integerValuesHistogram.subBucketHalfCountMagnitude + ") - 1.\n" + "Caused by: " + ex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  549 */     long newHighestTrackableValue = (1L << newContainingOrderOfMagnitude) - 1L;
/*  550 */     this.integerValuesHistogram.resize(newHighestTrackableValue);
/*  551 */     this.integerValuesHistogram.highestTrackableValue = newHighestTrackableValue;
/*  552 */     this.configuredHighestToLowestValueRatio <<= numberOfBinaryOrdersOfMagnitude;
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
/*  567 */     this.integerValuesHistogram.clearCounts();
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
/*      */   public DoubleHistogram copy()
/*      */   {
/*  585 */     DoubleHistogram targetHistogram = new DoubleHistogram(this.configuredHighestToLowestValueRatio, getNumberOfSignificantValueDigits());
/*  586 */     targetHistogram.setTrackableValueRange(this.currentLowestValueInAutoRange, this.currentHighestValueLimitInAutoRange);
/*  587 */     this.integerValuesHistogram.copyInto(targetHistogram.integerValuesHistogram);
/*  588 */     return targetHistogram;
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
/*      */   public DoubleHistogram copyCorrectedForCoordinatedOmission(double expectedIntervalBetweenValueSamples)
/*      */   {
/*  615 */     DoubleHistogram targetHistogram = new DoubleHistogram(this.configuredHighestToLowestValueRatio, getNumberOfSignificantValueDigits());
/*  616 */     targetHistogram.setTrackableValueRange(this.currentLowestValueInAutoRange, this.currentHighestValueLimitInAutoRange);
/*  617 */     targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/*  618 */     return targetHistogram;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyInto(DoubleHistogram targetHistogram)
/*      */   {
/*  627 */     targetHistogram.reset();
/*  628 */     targetHistogram.add(this);
/*  629 */     targetHistogram.setStartTimeStamp(this.integerValuesHistogram.startTimeStampMsec);
/*  630 */     targetHistogram.setEndTimeStamp(this.integerValuesHistogram.endTimeStampMsec);
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
/*      */   public void copyIntoCorrectedForCoordinatedOmission(DoubleHistogram targetHistogram, double expectedIntervalBetweenValueSamples)
/*      */   {
/*  644 */     targetHistogram.reset();
/*  645 */     targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
/*  646 */     targetHistogram.setStartTimeStamp(this.integerValuesHistogram.startTimeStampMsec);
/*  647 */     targetHistogram.setEndTimeStamp(this.integerValuesHistogram.endTimeStampMsec);
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
/*      */   public void add(DoubleHistogram fromHistogram)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/*  666 */     int arrayLength = fromHistogram.integerValuesHistogram.countsArrayLength;
/*  667 */     AbstractHistogram fromIntegerHistogram = fromHistogram.integerValuesHistogram;
/*  668 */     for (int i = 0; i < arrayLength; i++) {
/*  669 */       long count = fromIntegerHistogram.getCountAtIndex(i);
/*  670 */       if (count > 0L) {
/*  671 */         recordValueWithCount(fromIntegerHistogram
/*  672 */           .valueFromIndex(i) * fromHistogram.integerToDoubleValueConversionRatio, count);
/*      */       }
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
/*      */ 
/*      */ 
/*      */   public void addWhileCorrectingForCoordinatedOmission(DoubleHistogram fromHistogram, double expectedIntervalBetweenValueSamples)
/*      */   {
/*  704 */     DoubleHistogram toHistogram = this;
/*      */     
/*  706 */     for (HistogramIterationValue v : fromHistogram.integerValuesHistogram.recordedValues()) {
/*  707 */       toHistogram.recordValueWithCountAndExpectedInterval(v
/*  708 */         .getValueIteratedTo() * this.integerToDoubleValueConversionRatio, v
/*  709 */         .getCountAtValueIteratedTo(), expectedIntervalBetweenValueSamples);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void subtract(DoubleHistogram otherHistogram)
/*      */   {
/*  721 */     int arrayLength = otherHistogram.integerValuesHistogram.countsArrayLength;
/*  722 */     AbstractHistogram otherIntegerHistogram = otherHistogram.integerValuesHistogram;
/*  723 */     for (int i = 0; i < arrayLength; i++) {
/*  724 */       long otherCount = otherIntegerHistogram.getCountAtIndex(i);
/*  725 */       if (otherCount > 0L) {
/*  726 */         double otherValue = otherIntegerHistogram.valueFromIndex(i) * otherHistogram.integerToDoubleValueConversionRatio;
/*      */         
/*  728 */         if (getCountAtValue(otherValue) < otherCount)
/*      */         {
/*  730 */           throw new IllegalArgumentException("otherHistogram count (" + otherCount + ") at value " + otherValue + " is larger than this one's (" + getCountAtValue(otherValue) + ")");
/*      */         }
/*  732 */         recordValueWithCount(otherValue, -otherCount);
/*      */       }
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
/*  752 */     if (this == other) {
/*  753 */       return true;
/*      */     }
/*  755 */     if (!(other instanceof DoubleHistogram)) {
/*  756 */       return false;
/*      */     }
/*  758 */     DoubleHistogram that = (DoubleHistogram)other;
/*  759 */     return this.integerValuesHistogram.equals(that.integerValuesHistogram);
/*      */   }
/*      */   
/*      */   public int hashCode()
/*      */   {
/*  764 */     return this.integerValuesHistogram.hashCode();
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
/*      */   public long getTotalCount()
/*      */   {
/*  780 */     return this.integerValuesHistogram.getTotalCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   double getCurrentLowestTrackableNonZeroValue()
/*      */   {
/*  789 */     return this.currentLowestValueInAutoRange;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   double getCurrentHighestTrackableValue()
/*      */   {
/*  798 */     return this.currentHighestValueLimitInAutoRange;
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
/*      */   public double getIntegerToDoubleValueConversionRatio()
/*      */   {
/*  814 */     return this.integerToDoubleValueConversionRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNumberOfSignificantValueDigits()
/*      */   {
/*  822 */     return this.integerValuesHistogram.numberOfSignificantValueDigits;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getHighestToLowestValueRatio()
/*      */   {
/*  832 */     return this.configuredHighestToLowestValueRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double sizeOfEquivalentValueRange(double value)
/*      */   {
/*  844 */     return this.integerValuesHistogram.sizeOfEquivalentValueRange((value * this.doubleToIntegerValueConversionRatio)) * this.integerToDoubleValueConversionRatio;
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
/*      */   public double lowestEquivalentValue(double value)
/*      */   {
/*  857 */     return this.integerValuesHistogram.lowestEquivalentValue((value * this.doubleToIntegerValueConversionRatio)) * this.integerToDoubleValueConversionRatio;
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
/*      */   public double highestEquivalentValue(double value)
/*      */   {
/*  870 */     double nextNonEquivalentValue = nextNonEquivalentValue(value);
/*      */     
/*      */ 
/*      */ 
/*  874 */     double highestEquivalentValue = nextNonEquivalentValue - 2.0D * Math.ulp(nextNonEquivalentValue);
/*  875 */     while (highestEquivalentValue + Math.ulp(highestEquivalentValue) < nextNonEquivalentValue) {
/*  876 */       highestEquivalentValue += Math.ulp(highestEquivalentValue);
/*      */     }
/*      */     
/*  879 */     return highestEquivalentValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double medianEquivalentValue(double value)
/*      */   {
/*  891 */     return this.integerValuesHistogram.medianEquivalentValue((value * this.doubleToIntegerValueConversionRatio)) * this.integerToDoubleValueConversionRatio;
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
/*      */   public double nextNonEquivalentValue(double value)
/*      */   {
/*  904 */     return this.integerValuesHistogram.nextNonEquivalentValue((value * this.doubleToIntegerValueConversionRatio)) * this.integerToDoubleValueConversionRatio;
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
/*      */   public boolean valuesAreEquivalent(double value1, double value2)
/*      */   {
/*  917 */     return lowestEquivalentValue(value1) == lowestEquivalentValue(value2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getEstimatedFootprintInBytes()
/*      */   {
/*  926 */     return this.integerValuesHistogram._getEstimatedFootprintInBytes();
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
/*      */   public long getStartTimeStamp()
/*      */   {
/*  942 */     return this.integerValuesHistogram.getStartTimeStamp();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStartTimeStamp(long timeStampMsec)
/*      */   {
/*  950 */     this.integerValuesHistogram.setStartTimeStamp(timeStampMsec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getEndTimeStamp()
/*      */   {
/*  958 */     return this.integerValuesHistogram.getEndTimeStamp();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEndTimeStamp(long timeStampMsec)
/*      */   {
/*  966 */     this.integerValuesHistogram.setEndTimeStamp(timeStampMsec);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTag()
/*      */   {
/*  974 */     return this.integerValuesHistogram.getTag();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTag(String tag)
/*      */   {
/*  982 */     this.integerValuesHistogram.setTag(tag);
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
/*      */   public double getMinValue()
/*      */   {
/*  999 */     return this.integerValuesHistogram.getMinValue() * this.integerToDoubleValueConversionRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMaxValue()
/*      */   {
/* 1008 */     return this.integerValuesHistogram.getMaxValue() * this.integerToDoubleValueConversionRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMinNonZeroValue()
/*      */   {
/* 1017 */     return this.integerValuesHistogram.getMinNonZeroValue() * this.integerToDoubleValueConversionRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMaxValueAsDouble()
/*      */   {
/* 1027 */     return getMaxValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getMean()
/*      */   {
/* 1036 */     return this.integerValuesHistogram.getMean() * this.integerToDoubleValueConversionRatio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getStdDeviation()
/*      */   {
/* 1045 */     return this.integerValuesHistogram.getStdDeviation() * this.integerToDoubleValueConversionRatio;
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
/*      */   public double getValueAtPercentile(double percentile)
/*      */   {
/* 1064 */     return this.integerValuesHistogram.getValueAtPercentile(percentile) * this.integerToDoubleValueConversionRatio;
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
/*      */   public double getPercentileAtOrBelowValue(double value)
/*      */   {
/* 1080 */     return this.integerValuesHistogram.getPercentileAtOrBelowValue((value * this.doubleToIntegerValueConversionRatio));
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
/*      */   public double getCountBetweenValues(double lowValue, double highValue)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/* 1096 */     return this.integerValuesHistogram.getCountBetweenValues((lowValue * this.doubleToIntegerValueConversionRatio), (highValue * this.doubleToIntegerValueConversionRatio));
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
/*      */   public long getCountAtValue(double value)
/*      */     throws ArrayIndexOutOfBoundsException
/*      */   {
/* 1110 */     return this.integerValuesHistogram.getCountAtValue((value * this.doubleToIntegerValueConversionRatio));
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
/* 1125 */     return new Percentiles(this, percentileTicksPerHalfDistance, null);
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
/*      */   public LinearBucketValues linearBucketValues(double valueUnitsPerBucket)
/*      */   {
/* 1139 */     return new LinearBucketValues(this, valueUnitsPerBucket, null);
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
/*      */   public LogarithmicBucketValues logarithmicBucketValues(double valueUnitsInFirstBucket, double logBase)
/*      */   {
/* 1155 */     return new LogarithmicBucketValues(this, valueUnitsInFirstBucket, logBase, null);
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
/* 1168 */     return new RecordedValues(this, null);
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
/*      */   public AllValues allValues()
/*      */   {
/* 1181 */     return new AllValues(this, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class Percentiles
/*      */     implements Iterable<DoubleHistogramIterationValue>
/*      */   {
/*      */     final DoubleHistogram histogram;
/*      */     
/*      */     final int percentileTicksPerHalfDistance;
/*      */     
/*      */ 
/*      */     private Percentiles(DoubleHistogram histogram, int percentileTicksPerHalfDistance)
/*      */     {
/* 1196 */       this.histogram = histogram;
/* 1197 */       this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<DoubleHistogramIterationValue> iterator()
/*      */     {
/* 1204 */       return new DoublePercentileIterator(this.histogram, this.percentileTicksPerHalfDistance);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class LinearBucketValues
/*      */     implements Iterable<DoubleHistogramIterationValue>
/*      */   {
/*      */     final DoubleHistogram histogram;
/*      */     
/*      */     final double valueUnitsPerBucket;
/*      */     
/*      */ 
/*      */     private LinearBucketValues(DoubleHistogram histogram, double valueUnitsPerBucket)
/*      */     {
/* 1219 */       this.histogram = histogram;
/* 1220 */       this.valueUnitsPerBucket = valueUnitsPerBucket;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<DoubleHistogramIterationValue> iterator()
/*      */     {
/* 1227 */       return new DoubleLinearIterator(this.histogram, this.valueUnitsPerBucket);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public class LogarithmicBucketValues
/*      */     implements Iterable<DoubleHistogramIterationValue>
/*      */   {
/*      */     final DoubleHistogram histogram;
/*      */     
/*      */     final double valueUnitsInFirstBucket;
/*      */     
/*      */     final double logBase;
/*      */     
/*      */ 
/*      */     private LogarithmicBucketValues(DoubleHistogram histogram, double valueUnitsInFirstBucket, double logBase)
/*      */     {
/* 1244 */       this.histogram = histogram;
/* 1245 */       this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
/* 1246 */       this.logBase = logBase;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<DoubleHistogramIterationValue> iterator()
/*      */     {
/* 1253 */       return new DoubleLogarithmicIterator(this.histogram, this.valueUnitsInFirstBucket, this.logBase);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class RecordedValues
/*      */     implements Iterable<DoubleHistogramIterationValue>
/*      */   {
/*      */     final DoubleHistogram histogram;
/*      */     
/*      */ 
/*      */     private RecordedValues(DoubleHistogram histogram)
/*      */     {
/* 1267 */       this.histogram = histogram;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<DoubleHistogramIterationValue> iterator()
/*      */     {
/* 1274 */       return new DoubleRecordedValuesIterator(this.histogram);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class AllValues
/*      */     implements Iterable<DoubleHistogramIterationValue>
/*      */   {
/*      */     final DoubleHistogram histogram;
/*      */     
/*      */ 
/*      */     private AllValues(DoubleHistogram histogram)
/*      */     {
/* 1288 */       this.histogram = histogram;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Iterator<DoubleHistogramIterationValue> iterator()
/*      */     {
/* 1295 */       return new DoubleAllValuesIterator(this.histogram);
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
/*      */   public void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio)
/*      */   {
/* 1313 */     outputPercentileDistribution(printStream, 5, outputValueUnitScalingRatio);
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
/* 1339 */     outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, false);
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
/*      */   public void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat)
/*      */   {
/* 1359 */     this.integerValuesHistogram.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, 
/*      */     
/* 1361 */       Double.valueOf(outputValueUnitScalingRatio.doubleValue() / this.integerToDoubleValueConversionRatio), useCsvFormat);
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
/*      */   private void writeObject(ObjectOutputStream o)
/*      */     throws IOException
/*      */   {
/* 1378 */     o.writeLong(this.configuredHighestToLowestValueRatio);
/* 1379 */     o.writeDouble(this.currentLowestValueInAutoRange);
/* 1380 */     o.writeObject(this.integerValuesHistogram);
/*      */   }
/*      */   
/*      */   private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException
/*      */   {
/* 1385 */     long configuredHighestToLowestValueRatio = o.readLong();
/* 1386 */     double lowestValueInAutoRange = o.readDouble();
/* 1387 */     AbstractHistogram integerValuesHistogram = (AbstractHistogram)o.readObject();
/* 1388 */     init(configuredHighestToLowestValueRatio, lowestValueInAutoRange, integerValuesHistogram);
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
/* 1405 */     return this.integerValuesHistogram.getNeededByteBufferCapacity();
/*      */   }
/*      */   
/*      */   private int getNeededByteBufferCapacity(int relevantLength) {
/* 1409 */     return this.integerValuesHistogram.getNeededByteBufferCapacity(relevantLength);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static boolean isDoubleHistogramCookie(int cookie)
/*      */   {
/* 1416 */     return (isCompressedDoubleHistogramCookie(cookie)) || (isNonCompressedDoubleHistogramCookie(cookie));
/*      */   }
/*      */   
/*      */   static boolean isCompressedDoubleHistogramCookie(int cookie) {
/* 1420 */     return cookie == 208802383;
/*      */   }
/*      */   
/*      */   static boolean isNonCompressedDoubleHistogramCookie(int cookie) {
/* 1424 */     return cookie == 208802382;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized int encodeIntoByteBuffer(ByteBuffer buffer)
/*      */   {
/* 1433 */     long maxValue = this.integerValuesHistogram.getMaxValue();
/* 1434 */     int relevantLength = this.integerValuesHistogram.getLengthForNumberOfBuckets(this.integerValuesHistogram
/* 1435 */       .getBucketsNeededToCoverValue(maxValue));
/* 1436 */     if (buffer.capacity() < getNeededByteBufferCapacity(relevantLength))
/*      */     {
/* 1438 */       throw new ArrayIndexOutOfBoundsException("buffer does not have capacity for " + getNeededByteBufferCapacity(relevantLength) + " bytes");
/*      */     }
/* 1440 */     buffer.putInt(208802382);
/* 1441 */     buffer.putInt(getNumberOfSignificantValueDigits());
/* 1442 */     buffer.putLong(this.configuredHighestToLowestValueRatio);
/* 1443 */     return this.integerValuesHistogram.encodeIntoByteBuffer(buffer) + 16;
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
/* 1456 */     targetBuffer.putInt(208802383);
/* 1457 */     targetBuffer.putInt(getNumberOfSignificantValueDigits());
/* 1458 */     targetBuffer.putLong(this.configuredHighestToLowestValueRatio);
/* 1459 */     return this.integerValuesHistogram.encodeIntoCompressedByteBuffer(targetBuffer, compressionLevel) + 16;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer)
/*      */   {
/* 1468 */     return encodeIntoCompressedByteBuffer(targetBuffer, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static DoubleHistogram constructHistogramFromBuffer(int cookie, ByteBuffer buffer, Class<? extends AbstractHistogram> histogramClass, long minBarForHighestToLowestValueRatio)
/*      */     throws DataFormatException
/*      */   {
/* 1476 */     int numberOfSignificantValueDigits = buffer.getInt();
/* 1477 */     long configuredHighestToLowestValueRatio = buffer.getLong();
/*      */     AbstractHistogram valuesHistogram;
/* 1479 */     if (isNonCompressedDoubleHistogramCookie(cookie))
/*      */     {
/* 1481 */       valuesHistogram = AbstractHistogram.decodeFromByteBuffer(buffer, histogramClass, minBarForHighestToLowestValueRatio); } else { AbstractHistogram valuesHistogram;
/* 1482 */       if (isCompressedDoubleHistogramCookie(cookie))
/*      */       {
/* 1484 */         valuesHistogram = AbstractHistogram.decodeFromCompressedByteBuffer(buffer, histogramClass, minBarForHighestToLowestValueRatio);
/*      */       } else
/* 1486 */         throw new IllegalStateException("The buffer does not contain a DoubleHistogram"); }
/*      */     AbstractHistogram valuesHistogram;
/* 1488 */     DoubleHistogram histogram = new DoubleHistogram(configuredHighestToLowestValueRatio, numberOfSignificantValueDigits, histogramClass, valuesHistogram);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1495 */     return histogram;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio)
/*      */   {
/* 1507 */     return decodeFromByteBuffer(buffer, Histogram.class, minBarForHighestToLowestValueRatio);
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
/*      */   public static DoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, Class<? extends AbstractHistogram> internalCountsHistogramClass, long minBarForHighestToLowestValueRatio)
/*      */   {
/*      */     try
/*      */     {
/* 1526 */       int cookie = buffer.getInt();
/* 1527 */       if (!isNonCompressedDoubleHistogramCookie(cookie)) {
/* 1528 */         throw new IllegalArgumentException("The buffer does not contain a DoubleHistogram");
/*      */       }
/* 1530 */       return constructHistogramFromBuffer(cookie, buffer, internalCountsHistogramClass, minBarForHighestToLowestValueRatio);
/*      */     }
/*      */     catch (DataFormatException ex)
/*      */     {
/* 1534 */       throw new RuntimeException(ex);
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
/*      */   public static DoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio)
/*      */     throws DataFormatException
/*      */   {
/* 1548 */     return decodeFromCompressedByteBuffer(buffer, Histogram.class, minBarForHighestToLowestValueRatio);
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
/*      */   public static DoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, Class<? extends AbstractHistogram> internalCountsHistogramClass, long minBarForHighestToLowestValueRatio)
/*      */     throws DataFormatException
/*      */   {
/* 1567 */     int cookie = buffer.getInt();
/* 1568 */     if (!isCompressedDoubleHistogramCookie(cookie)) {
/* 1569 */       throw new IllegalArgumentException("The buffer does not contain a compressed DoubleHistogram");
/*      */     }
/* 1571 */     DoubleHistogram histogram = constructHistogramFromBuffer(cookie, buffer, internalCountsHistogramClass, minBarForHighestToLowestValueRatio);
/*      */     
/* 1573 */     return histogram;
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
/*      */   private long deriveInternalHighestToLowestValueRatio(long externalHighestToLowestValueRatio)
/*      */   {
/* 1590 */     long internalHighestToLowestValueRatio = 1L << findContainingBinaryOrderOfMagnitude(externalHighestToLowestValueRatio) + 1;
/* 1591 */     return internalHighestToLowestValueRatio;
/*      */   }
/*      */   
/*      */ 
/*      */   private long deriveIntegerValueRange(long externalHighestToLowestValueRatio, int numberOfSignificantValueDigits)
/*      */   {
/* 1597 */     long internalHighestToLowestValueRatio = deriveInternalHighestToLowestValueRatio(externalHighestToLowestValueRatio);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1604 */     long lowestTackingIntegerValue = AbstractHistogram.numberOfSubbuckets(numberOfSignificantValueDigits) / 2;
/* 1605 */     long integerValueRange = lowestTackingIntegerValue * internalHighestToLowestValueRatio;
/*      */     
/* 1607 */     return integerValueRange;
/*      */   }
/*      */   
/*      */   private long getLowestTrackingIntegerValue() {
/* 1611 */     return this.integerValuesHistogram.subBucketHalfCount;
/*      */   }
/*      */   
/*      */   private static int findContainingBinaryOrderOfMagnitude(long longNumber) {
/* 1615 */     int pow2ceiling = 64 - Long.numberOfLeadingZeros(longNumber);
/* 1616 */     return pow2ceiling;
/*      */   }
/*      */   
/*      */   private static int findContainingBinaryOrderOfMagnitude(double doubleNumber) {
/* 1620 */     long longNumber = Math.ceil(doubleNumber);
/* 1621 */     return findContainingBinaryOrderOfMagnitude(longNumber);
/*      */   }
/*      */   
/*      */   private int findCappedContainingBinaryOrderOfMagnitude(double doubleNumber) {
/* 1625 */     if (doubleNumber > this.configuredHighestToLowestValueRatio) {
/* 1626 */       return (int)(Math.log(this.configuredHighestToLowestValueRatio) / Math.log(2.0D));
/*      */     }
/* 1628 */     if (doubleNumber > Math.pow(2.0D, 50.0D)) {
/* 1629 */       return 50;
/*      */     }
/* 1631 */     return findContainingBinaryOrderOfMagnitude(doubleNumber);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/* 1640 */     double value = 1.0D;
/* 1641 */     while (value < 4.4942328371557893E307D) {
/* 1642 */       value *= 2.0D;
/*      */     }
/* 1644 */     highestAllowedValueEver = value;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */