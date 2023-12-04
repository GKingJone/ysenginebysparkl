/*     */ package com.facebook.presto.jdbc.internal.airlift.stats.cardinality;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Murmur3;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
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
/*     */ public class HyperLogLog
/*     */ {
/*     */   private static final int MAX_NUMBER_OF_BUCKETS = 65536;
/*     */   private HllInstance instance;
/*     */   
/*     */   private HyperLogLog(HllInstance instance)
/*     */   {
/*  30 */     this.instance = instance;
/*     */   }
/*     */   
/*     */   public static HyperLogLog newInstance(int numberOfBuckets)
/*     */   {
/*  35 */     Preconditions.checkArgument(numberOfBuckets <= 65536, "numberOfBuckets must be <= %s, actual: %s", new Object[] { Integer.valueOf(65536), Integer.valueOf(numberOfBuckets) });
/*     */     
/*  37 */     return new HyperLogLog(new SparseHll(Utils.indexBitLength(numberOfBuckets)));
/*     */   }
/*     */   
/*     */   public static HyperLogLog newInstance(Slice serialized)
/*     */   {
/*  42 */     Preconditions.checkArgument(serialized.getByte(0) != Format.SPARSE_V1.getTag(), "Sparse v1 encoding no longer supported");
/*     */     
/*  44 */     if (SparseHll.canDeserialize(serialized)) {
/*  45 */       return new HyperLogLog(new SparseHll(serialized));
/*     */     }
/*  47 */     if (DenseHll.canDeserialize(serialized)) {
/*  48 */       return new HyperLogLog(new DenseHll(serialized));
/*     */     }
/*     */     
/*  51 */     throw new IllegalArgumentException("Cannot deserialize HyperLogLog");
/*     */   }
/*     */   
/*     */   public void add(long value)
/*     */   {
/*  56 */     addHash(Murmur3.hash64(value));
/*     */   }
/*     */   
/*     */   public void add(Slice value)
/*     */   {
/*  61 */     addHash(Murmur3.hash64(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addHash(long hash)
/*     */   {
/*  72 */     this.instance.insertHash(hash);
/*     */     
/*  74 */     if ((this.instance instanceof SparseHll)) {
/*  75 */       this.instance = makeDenseIfNecessary((SparseHll)this.instance);
/*     */     }
/*     */   }
/*     */   
/*     */   public void mergeWith(HyperLogLog other)
/*     */   {
/*  81 */     if (((this.instance instanceof SparseHll)) && ((other.instance instanceof SparseHll))) {
/*  82 */       ((SparseHll)this.instance).mergeWith((SparseHll)other.instance);
/*  83 */       this.instance = makeDenseIfNecessary((SparseHll)this.instance);
/*     */     }
/*     */     else {
/*  86 */       DenseHll dense = this.instance.toDense();
/*  87 */       dense.mergeWith(other.instance.toDense());
/*     */       
/*  89 */       this.instance = dense;
/*     */     }
/*     */   }
/*     */   
/*     */   public long cardinality()
/*     */   {
/*  95 */     return this.instance.cardinality();
/*     */   }
/*     */   
/*     */   public int estimatedInMemorySize()
/*     */   {
/* 100 */     return this.instance.estimatedInMemorySize();
/*     */   }
/*     */   
/*     */   public int estimatedSerializedSize()
/*     */   {
/* 105 */     return this.instance.estimatedSerializedSize();
/*     */   }
/*     */   
/*     */   public Slice serialize()
/*     */   {
/* 110 */     return this.instance.serialize();
/*     */   }
/*     */   
/*     */   public void makeDense()
/*     */   {
/* 115 */     this.instance = this.instance.toDense();
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   void verify()
/*     */   {
/* 121 */     this.instance.verify();
/*     */   }
/*     */   
/*     */   private static HllInstance makeDenseIfNecessary(SparseHll instance)
/*     */   {
/* 126 */     if (instance.estimatedInMemorySize() > DenseHll.estimatedInMemorySize(instance.getIndexBitLength())) {
/* 127 */       return instance.toDense();
/*     */     }
/*     */     
/* 130 */     return instance;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\cardinality\HyperLogLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */