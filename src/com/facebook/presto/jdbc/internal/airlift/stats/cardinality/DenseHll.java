/*     */ package com.facebook.presto.jdbc.internal.airlift.stats.cardinality;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.BasicSliceInput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.DynamicSliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SizeOf;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.Bytes;
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.Ints;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.annotation.concurrent.NotThreadSafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NotThreadSafe
/*     */ final class DenseHll
/*     */   implements HllInstance
/*     */ {
/*     */   private static final double LINEAR_COUNTING_MIN_EMPTY_BUCKETS = 0.4D;
/*     */   private static final int BITS_PER_BUCKET = 4;
/*     */   private static final int MAX_DELTA = 15;
/*     */   private static final int BUCKET_MASK = 15;
/*  49 */   private static final int DENSE_INSTANCE_SIZE = ClassLayout.parseClass(DenseHll.class).instanceSize();
/*     */   
/*     */   private static final int OVERFLOW_GROW_INCREMENT = 5;
/*     */   
/*     */   private final byte indexBitLength;
/*     */   private byte baseline;
/*     */   private int baselineCount;
/*     */   private final byte[] deltas;
/*     */   private int overflows;
/*     */   private int[] overflowBuckets;
/*     */   private byte[] overflowValues;
/*     */   
/*     */   public DenseHll(int indexBitLength)
/*     */   {
/*  63 */     validatePrefixLength(indexBitLength);
/*     */     
/*  65 */     int numberOfBuckets = Utils.numberOfBuckets(indexBitLength);
/*     */     
/*  67 */     this.indexBitLength = ((byte)indexBitLength);
/*  68 */     this.baselineCount = numberOfBuckets;
/*  69 */     this.deltas = new byte[numberOfBuckets * 4 / 8];
/*  70 */     this.overflowBuckets = new int[0];
/*  71 */     this.overflowValues = new byte[0];
/*     */   }
/*     */   
/*     */   public DenseHll(Slice serialized)
/*     */   {
/*  76 */     BasicSliceInput input = serialized.getInput();
/*     */     
/*  78 */     byte formatTag = input.readByte();
/*  79 */     Preconditions.checkArgument((formatTag == Format.DENSE_V1.getTag()) || (formatTag == Format.DENSE_V2.getTag()), "Invalid format tag");
/*     */     
/*  81 */     this.indexBitLength = input.readByte();
/*  82 */     validatePrefixLength(this.indexBitLength);
/*  83 */     int numberOfBuckets = Utils.numberOfBuckets(this.indexBitLength);
/*     */     
/*  85 */     this.baseline = input.readByte();
/*  86 */     this.deltas = new byte[numberOfBuckets / 2];
/*  87 */     input.readBytes(this.deltas);
/*     */     
/*  89 */     if (formatTag == Format.DENSE_V1.getTag())
/*     */     {
/*  91 */       int bucket = input.readShort();
/*  92 */       byte value = input.readByte();
/*  93 */       if ((bucket >= 0) && (value > 0)) {
/*  94 */         Preconditions.checkArgument(bucket <= numberOfBuckets, "Overflow bucket index is out of range");
/*  95 */         this.overflows = 1;
/*  96 */         this.overflowBuckets = new int[] { bucket };
/*  97 */         this.overflowValues = new byte[] { value };
/*     */       }
/*     */       else {
/* 100 */         this.overflows = 0;
/* 101 */         this.overflowBuckets = new int[0];
/* 102 */         this.overflowValues = new byte[0];
/*     */       }
/*     */     }
/* 105 */     else if (formatTag == Format.DENSE_V2.getTag()) {
/* 106 */       this.overflows = input.readUnsignedShort();
/* 107 */       Preconditions.checkArgument(this.overflows <= numberOfBuckets, "Overflow entries is greater than actual number of buckets (possibly corrupt input)");
/*     */       
/* 109 */       this.overflowBuckets = new int[this.overflows];
/* 110 */       this.overflowValues = new byte[this.overflows];
/*     */       
/* 112 */       for (int i = 0; i < this.overflows; i++) {
/* 113 */         this.overflowBuckets[i] = input.readUnsignedShort();
/* 114 */         Preconditions.checkArgument(this.overflowBuckets[i] <= numberOfBuckets, "Overflow bucket index is out of range");
/*     */       }
/*     */       
/* 117 */       for (int i = 0; i < this.overflows; i++) {
/* 118 */         this.overflowValues[i] = input.readByte();
/* 119 */         Preconditions.checkArgument(this.overflowValues[i] > 0, "Overflow bucket value must be > 0");
/*     */       }
/*     */     }
/*     */     else {
/* 123 */       throw new IllegalArgumentException(String.format("Invalid format tag: %d", new Object[] { Byte.valueOf(formatTag) }));
/*     */     }
/*     */     
/* 126 */     this.baselineCount = 0;
/* 127 */     for (int i = 0; i < numberOfBuckets; i++) {
/* 128 */       if (getDelta(i) == 0) {
/* 129 */         this.baselineCount += 1;
/*     */       }
/*     */     }
/*     */     
/* 133 */     Preconditions.checkArgument(!input.isReadable(), "input is too big");
/*     */   }
/*     */   
/*     */   public static boolean canDeserialize(Slice serialized)
/*     */   {
/* 138 */     byte formatTag = serialized.getByte(0);
/* 139 */     return (formatTag == Format.DENSE_V1.getTag()) || (formatTag == Format.DENSE_V2.getTag());
/*     */   }
/*     */   
/*     */   public void insertHash(long hash)
/*     */   {
/* 144 */     int index = Utils.computeIndex(hash, this.indexBitLength);
/* 145 */     int value = Utils.computeValue(hash, this.indexBitLength);
/*     */     
/* 147 */     insert(index, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public int estimatedInMemorySize()
/*     */   {
/* 153 */     return 
/*     */     
/*     */ 
/* 156 */       (int)(DENSE_INSTANCE_SIZE + SizeOf.sizeOf(this.deltas) + SizeOf.sizeOf(this.overflowBuckets) + SizeOf.sizeOf(this.overflowValues));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndexBitLength()
/*     */   {
/* 162 */     return this.indexBitLength;
/*     */   }
/*     */   
/*     */ 
/*     */   public long cardinality()
/*     */   {
/* 168 */     int numberOfBuckets = Utils.numberOfBuckets(this.indexBitLength);
/*     */     
/*     */ 
/* 171 */     if ((this.baseline == 0) && (this.baselineCount > 0.4D * numberOfBuckets)) {
/* 172 */       return Math.round(Utils.linearCounting(this.baselineCount, numberOfBuckets));
/*     */     }
/*     */     
/* 175 */     double sum = 0.0D;
/* 176 */     for (int i = 0; i < numberOfBuckets; i++) {
/* 177 */       int value = getValue(i);
/* 178 */       sum += 1.0D / (1L << value);
/*     */     }
/*     */     
/* 181 */     double estimate = Utils.alpha(this.indexBitLength) * numberOfBuckets * numberOfBuckets / sum;
/* 182 */     estimate = correctBias(estimate);
/*     */     
/* 184 */     return Math.round(estimate);
/*     */   }
/*     */   
/*     */   private double correctBias(double rawEstimate)
/*     */   {
/* 189 */     double[] estimates = BiasCorrection.RAW_ESTIMATES[(this.indexBitLength - 4)];
/* 190 */     if ((rawEstimate < estimates[0]) || (rawEstimate > estimates[(estimates.length - 1)])) {
/* 191 */       return rawEstimate;
/*     */     }
/*     */     
/* 194 */     double[] biases = BiasCorrection.BIAS[(this.indexBitLength - 4)];
/*     */     
/* 196 */     int position = search(rawEstimate, estimates);
/*     */     double bias;
/*     */     double bias;
/* 199 */     if (position >= 0) {
/* 200 */       bias = biases[position];
/*     */     }
/*     */     else
/*     */     {
/* 204 */       int insertionPoint = -(position + 1);
/*     */       
/* 206 */       double x0 = estimates[(insertionPoint - 1)];
/* 207 */       double y0 = biases[(insertionPoint - 1)];
/* 208 */       double x1 = estimates[insertionPoint];
/* 209 */       double y1 = biases[insertionPoint];
/*     */       
/* 211 */       bias = (rawEstimate - x0) * (y1 - y0) / (x1 - x0) + y0;
/*     */     }
/*     */     
/* 214 */     return rawEstimate - bias;
/*     */   }
/*     */   
/*     */   private int search(double rawEstimate, double[] estimateCurve)
/*     */   {
/* 219 */     int low = 0;
/* 220 */     int high = estimateCurve.length - 1;
/*     */     
/* 222 */     while (low <= high) {
/* 223 */       int middle = low + high >>> 1;
/*     */       
/* 225 */       double middleValue = estimateCurve[middle];
/*     */       
/* 227 */       if (rawEstimate > middleValue) {
/* 228 */         low = middle + 1;
/*     */       }
/* 230 */       else if (rawEstimate < middleValue) {
/* 231 */         high = middle - 1;
/*     */       }
/*     */       else {
/* 234 */         return middle;
/*     */       }
/*     */     }
/*     */     
/* 238 */     return -(low + 1);
/*     */   }
/*     */   
/*     */   public void insert(int bucket, int value)
/*     */   {
/* 243 */     int delta = value - this.baseline;
/* 244 */     int oldDelta = getDelta(bucket);
/*     */     
/* 246 */     if ((delta <= oldDelta) || ((oldDelta == 15) && (delta <= oldDelta + getOverflow(bucket))))
/*     */     {
/* 248 */       return;
/*     */     }
/*     */     
/* 251 */     if (delta > 15) {
/* 252 */       int overflow = delta - 15;
/*     */       
/* 254 */       if (!setOverflow(bucket, overflow))
/*     */       {
/* 256 */         this.overflowBuckets = Ints.ensureCapacity(this.overflowBuckets, this.overflows + 1, 5);
/* 257 */         this.overflowValues = Bytes.ensureCapacity(this.overflowValues, this.overflows + 1, 5);
/*     */         
/* 259 */         this.overflowBuckets[this.overflows] = bucket;
/* 260 */         this.overflowValues[this.overflows] = ((byte)overflow);
/* 261 */         this.overflows += 1;
/*     */       }
/*     */       
/* 264 */       delta = 15;
/*     */     }
/*     */     
/* 267 */     setDelta(bucket, delta);
/*     */     
/* 269 */     if (oldDelta == 0) {
/* 270 */       this.baselineCount -= 1;
/* 271 */       adjustBaselineIfNeeded();
/*     */     }
/*     */   }
/*     */   
/*     */   private int getOverflow(int bucket)
/*     */   {
/* 277 */     for (int i = 0; i < this.overflows; i++) {
/* 278 */       if (this.overflowBuckets[i] == bucket) {
/* 279 */         return this.overflowValues[i];
/*     */       }
/*     */     }
/* 282 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean setOverflow(int bucket, int overflow)
/*     */   {
/* 290 */     for (int i = 0; i < this.overflows; i++) {
/* 291 */       if (this.overflowBuckets[i] == bucket) {
/* 292 */         this.overflowValues[i] = ((byte)overflow);
/* 293 */         return true;
/*     */       }
/*     */     }
/* 296 */     return false;
/*     */   }
/*     */   
/*     */   public Slice serialize()
/*     */   {
/* 301 */     int size = estimatedSerializedSize();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */     DynamicSliceOutput output = new DynamicSliceOutput(size).appendByte(Format.DENSE_V2.getTag()).appendByte(this.indexBitLength).appendByte(this.baseline).appendBytes(this.deltas).appendShort(this.overflows);
/*     */     
/*     */ 
/* 311 */     sortOverflows();
/*     */     
/* 313 */     for (int i = 0; i < this.overflows; i++) {
/* 314 */       output.appendShort(this.overflowBuckets[i]);
/*     */     }
/* 316 */     for (int i = 0; i < this.overflows; i++) {
/* 317 */       output.appendByte(this.overflowValues[i]);
/*     */     }
/*     */     
/* 320 */     return output.slice();
/*     */   }
/*     */   
/*     */ 
/*     */   private void sortOverflows()
/*     */   {
/* 326 */     for (int i = 1; i < this.overflows; i++) {
/* 327 */       for (int j = i; (j > 0) && (this.overflowBuckets[(j - 1)] > this.overflowBuckets[j]); j--) {
/* 328 */         int bucket = this.overflowBuckets[j];
/* 329 */         int value = this.overflowValues[j];
/*     */         
/* 331 */         this.overflowBuckets[j] = this.overflowBuckets[(j - 1)];
/* 332 */         this.overflowValues[j] = this.overflowValues[(j - 1)];
/*     */         
/* 334 */         this.overflowBuckets[(j - 1)] = bucket;
/* 335 */         this.overflowValues[(j - 1)] = ((byte)value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DenseHll toDense()
/*     */   {
/* 343 */     return this;
/*     */   }
/*     */   
/*     */   public int estimatedSerializedSize()
/*     */   {
/* 348 */     return 
/*     */     
/*     */ 
/* 351 */       3 + Utils.numberOfBuckets(this.indexBitLength) * 1 / 2 + 2 + 2 * this.overflows + 1 * this.overflows;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setDelta(int bucket, int value)
/*     */   {
/* 360 */     int slot = bucketToSlot(bucket);
/*     */     
/*     */ 
/* 363 */     byte clearMask = (byte)(15 << shiftForBucket(bucket)); int 
/* 364 */       tmp20_19 = slot; byte[] tmp20_16 = this.deltas;tmp20_16[tmp20_19] = ((byte)(tmp20_16[tmp20_19] & (clearMask ^ 0xFFFFFFFF)));
/*     */     
/*     */ 
/* 367 */     byte setMask = (byte)(value << shiftForBucket(bucket)); int 
/* 368 */       tmp43_42 = slot; byte[] tmp43_39 = this.deltas;tmp43_39[tmp43_42] = ((byte)(tmp43_39[tmp43_42] | setMask));
/*     */   }
/*     */   
/*     */   private int getDelta(int bucket)
/*     */   {
/* 373 */     int slot = bucketToSlot(bucket);
/*     */     
/* 375 */     return this.deltas[slot] >> shiftForBucket(bucket) & 0xF;
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   int getValue(int bucket)
/*     */   {
/* 381 */     int delta = getDelta(bucket);
/*     */     
/* 383 */     if (delta == 15) {
/* 384 */       delta += getOverflow(bucket);
/*     */     }
/*     */     
/* 387 */     return this.baseline + delta;
/*     */   }
/*     */   
/*     */   private void adjustBaselineIfNeeded()
/*     */   {
/* 392 */     while (this.baselineCount == 0) {
/* 393 */       this.baseline = ((byte)(this.baseline + 1));
/*     */       
/* 395 */       for (int bucket = 0; bucket < Utils.numberOfBuckets(this.indexBitLength); bucket++) {
/* 396 */         int delta = getDelta(bucket);
/*     */         
/* 398 */         boolean hasOverflow = false;
/* 399 */         if (delta == 15)
/*     */         {
/* 401 */           for (int i = 0; i < this.overflows; i++) {
/* 402 */             if (this.overflowBuckets[i] == bucket) {
/* 403 */               hasOverflow = true; int 
/* 404 */                 tmp76_74 = i; byte[] tmp76_71 = this.overflowValues;tmp76_71[tmp76_74] = ((byte)(tmp76_71[tmp76_74] - 1));
/*     */               
/* 406 */               if (this.overflowValues[i] != 0) break;
/* 407 */               int lastEntry = this.overflows - 1;
/* 408 */               if (i < lastEntry)
/*     */               {
/* 410 */                 this.overflowBuckets[i] = this.overflowBuckets[lastEntry];
/* 411 */                 this.overflowValues[i] = this.overflowValues[lastEntry];
/*     */                 
/*     */ 
/* 414 */                 this.overflowBuckets[lastEntry] = -1;
/* 415 */                 this.overflowValues[lastEntry] = 0;
/*     */               }
/* 417 */               this.overflows -= 1;
/* 418 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 424 */         if (!hasOverflow)
/*     */         {
/*     */ 
/*     */ 
/* 428 */           delta--;
/* 429 */           setDelta(bucket, delta);
/*     */         }
/*     */         
/* 432 */         if (delta == 0) {
/* 433 */           this.baselineCount += 1;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DenseHll mergeWith(DenseHll other)
/*     */   {
/* 444 */     if (this.indexBitLength != other.indexBitLength) {
/* 445 */       throw new IllegalArgumentException(String.format("Cannot merge HLLs with different number of buckets: %s vs %s", new Object[] {
/*     */       
/* 447 */         Integer.valueOf(Utils.numberOfBuckets(this.indexBitLength)), 
/* 448 */         Integer.valueOf(Utils.numberOfBuckets(other.indexBitLength)) }));
/*     */     }
/*     */     
/* 451 */     int baseline = Math.max(this.baseline, other.baseline);
/* 452 */     int baselineCount = 0;
/*     */     
/* 454 */     int overflows = 0;
/* 455 */     int[] overflowBuckets = new int[5];
/* 456 */     byte[] overflowValues = new byte[5];
/*     */     
/* 458 */     int numberOfBuckets = Utils.numberOfBuckets(this.indexBitLength);
/* 459 */     for (int i = 0; i < numberOfBuckets; i++) {
/* 460 */       int value = Math.max(getValue(i), other.getValue(i));
/*     */       
/* 462 */       int delta = value - baseline;
/* 463 */       if (delta == 0) {
/* 464 */         baselineCount++;
/*     */       }
/* 466 */       else if (delta > 15)
/*     */       {
/* 468 */         overflowBuckets = Ints.ensureCapacity(overflowBuckets, overflows + 1, 5);
/* 469 */         overflowValues = Bytes.ensureCapacity(overflowValues, overflows + 1, 5);
/*     */         
/* 471 */         overflowBuckets[overflows] = i;
/* 472 */         overflowValues[overflows] = ((byte)(delta - 15));
/*     */         
/* 474 */         overflows++;
/*     */         
/* 476 */         delta = 15;
/*     */       }
/*     */       
/* 479 */       setDelta(i, delta);
/*     */     }
/*     */     
/* 482 */     this.baseline = ((byte)baseline);
/* 483 */     this.baselineCount = baselineCount;
/* 484 */     this.overflows = overflows;
/* 485 */     this.overflowBuckets = overflowBuckets;
/* 486 */     this.overflowValues = overflowValues;
/*     */     
/*     */ 
/*     */ 
/* 490 */     adjustBaselineIfNeeded();
/*     */     
/* 492 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int estimatedInMemorySize(int indexBitLength)
/*     */   {
/* 498 */     return (int)(DENSE_INSTANCE_SIZE + SizeOf.sizeOfByteArray(Utils.numberOfBuckets(indexBitLength) / 2));
/*     */   }
/*     */   
/*     */   private static int bucketToSlot(int bucket)
/*     */   {
/* 503 */     return bucket >> 1;
/*     */   }
/*     */   
/*     */ 
/*     */   private static int shiftForBucket(int bucket)
/*     */   {
/* 509 */     return ((bucket ^ 0xFFFFFFFF) & 0x1) << 2;
/*     */   }
/*     */   
/*     */   private static void validatePrefixLength(int indexBitLength)
/*     */   {
/* 514 */     Preconditions.checkArgument((indexBitLength >= 1) && (indexBitLength <= 16), "indexBitLength is out of range");
/*     */   }
/*     */   
/*     */ 
/*     */   public void verify()
/*     */   {
/* 520 */     int zeroDeltas = 0;
/* 521 */     for (int i = 0; i < Utils.numberOfBuckets(this.indexBitLength); i++) {
/* 522 */       if (getDelta(i) == 0) {
/* 523 */         zeroDeltas++;
/*     */       }
/*     */     }
/*     */     
/* 527 */     Preconditions.checkState(zeroDeltas == this.baselineCount, "baselineCount (%s) doesn't match number of zero deltas (%s)", new Object[] {
/* 528 */       Integer.valueOf(this.baselineCount), Integer.valueOf(zeroDeltas) });
/*     */     
/* 530 */     Set<Integer> overflows = new HashSet();
/* 531 */     for (int i = 0; i < this.overflows; i++) {
/* 532 */       int bucket = this.overflowBuckets[i];
/* 533 */       overflows.add(Integer.valueOf(bucket));
/*     */       
/* 535 */       Preconditions.checkState(this.overflowValues[i] > 0, "Overflow at %s for bucket %s is 0", new Object[] { Integer.valueOf(i), Integer.valueOf(bucket) });
/* 536 */       Preconditions.checkState(getDelta(bucket) == 15, "delta in bucket %s is less than MAX_DELTA (%s < %s) even though there's an associated overflow entry", new Object[] {
/*     */       
/* 538 */         Integer.valueOf(bucket), Integer.valueOf(getDelta(bucket)), Integer.valueOf(15) });
/*     */     }
/*     */     
/* 541 */     Preconditions.checkState(overflows.size() == this.overflows, "Duplicate overflow buckets: %s", new Object[] {
/* 542 */       Ints.asList(Arrays.copyOf(this.overflowBuckets, this.overflows)) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\cardinality\DenseHll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */