/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Iterables;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.MapMaker;
/*     */ import com.facebook.presto.jdbc.internal.guava.math.IntMath;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.Semaphore;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
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
/*     */ @Beta
/*     */ public abstract class Striped<L>
/*     */ {
/*     */   private static final int LARGE_LAZY_CUTOFF = 1024;
/*     */   
/*     */   public abstract L get(Object paramObject);
/*     */   
/*     */   public abstract L getAt(int paramInt);
/*     */   
/*     */   abstract int indexFor(Object paramObject);
/*     */   
/*     */   public abstract int size();
/*     */   
/*     */   public Iterable<L> bulkGet(Iterable<?> keys)
/*     */   {
/* 146 */     Object[] array = Iterables.toArray(keys, Object.class);
/* 147 */     if (array.length == 0) {
/* 148 */       return ImmutableList.of();
/*     */     }
/* 150 */     int[] stripes = new int[array.length];
/* 151 */     for (int i = 0; i < array.length; i++) {
/* 152 */       stripes[i] = indexFor(array[i]);
/*     */     }
/* 154 */     Arrays.sort(stripes);
/*     */     
/* 156 */     int previousStripe = stripes[0];
/* 157 */     array[0] = getAt(previousStripe);
/* 158 */     for (int i = 1; i < array.length; i++) {
/* 159 */       int currentStripe = stripes[i];
/* 160 */       if (currentStripe == previousStripe) {
/* 161 */         array[i] = array[(i - 1)];
/*     */       } else {
/* 163 */         array[i] = getAt(currentStripe);
/* 164 */         previousStripe = currentStripe;
/*     */       }
/*     */     }
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
/* 185 */     List<L> asList = Arrays.asList(array);
/* 186 */     return Collections.unmodifiableList(asList);
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
/*     */   public static Striped<Lock> lock(int stripes)
/*     */   {
/* 199 */     new CompactStriped(stripes, new Supplier()
/*     */     {
/* 201 */       public Lock get() { return new PaddedLock(); } }, null);
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
/*     */   public static Striped<Lock> lazyWeakLock(int stripes)
/*     */   {
/* 214 */     lazy(stripes, new Supplier() {
/*     */       public Lock get() {
/* 216 */         return new ReentrantLock(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static <L> Striped<L> lazy(int stripes, Supplier<L> supplier) {
/* 222 */     return stripes < 1024 ? new SmallLazyStriped(stripes, supplier) : new LargeLazyStriped(stripes, supplier);
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
/*     */   public static Striped<Semaphore> semaphore(int stripes, int permits)
/*     */   {
/* 236 */     new CompactStriped(stripes, new Supplier()
/*     */     {
/* 238 */       public Semaphore get() { return new PaddedSemaphore(this.val$permits); } }, null);
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
/*     */   public static Striped<Semaphore> lazyWeakSemaphore(int stripes, int permits)
/*     */   {
/* 252 */     lazy(stripes, new Supplier() {
/*     */       public Semaphore get() {
/* 254 */         return new Semaphore(this.val$permits, false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Striped<ReadWriteLock> readWriteLock(int stripes)
/*     */   {
/* 267 */     return new CompactStriped(stripes, READ_WRITE_LOCK_SUPPLIER, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int stripes)
/*     */   {
/* 278 */     return lazy(stripes, READ_WRITE_LOCK_SUPPLIER);
/*     */   }
/*     */   
/*     */ 
/* 282 */   private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier()
/*     */   {
/*     */ 
/* 285 */     public ReadWriteLock get() { return new ReentrantReadWriteLock(); }
/*     */   };
/*     */   private static final int ALL_SET = -1;
/*     */   
/*     */   private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
/*     */     final int mask;
/*     */     
/*     */     PowerOfTwoStriped(int stripes) {
/* 293 */       super();
/* 294 */       Preconditions.checkArgument(stripes > 0, "Stripes must be positive");
/* 295 */       this.mask = (stripes > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(stripes) - 1);
/*     */     }
/*     */     
/*     */     final int indexFor(Object key) {
/* 299 */       int hash = Striped.smear(key.hashCode());
/* 300 */       return hash & this.mask;
/*     */     }
/*     */     
/*     */     public final L get(Object key) {
/* 304 */       return (L)getAt(indexFor(key));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class CompactStriped<L>
/*     */     extends PowerOfTwoStriped<L>
/*     */   {
/*     */     private final Object[] array;
/*     */     
/*     */ 
/*     */     private CompactStriped(int stripes, Supplier<L> supplier)
/*     */     {
/* 317 */       super();
/* 318 */       Preconditions.checkArgument(stripes <= 1073741824, "Stripes must be <= 2^30)");
/*     */       
/* 320 */       this.array = new Object[this.mask + 1];
/* 321 */       for (int i = 0; i < this.array.length; i++) {
/* 322 */         this.array[i] = supplier.get();
/*     */       }
/*     */     }
/*     */     
/*     */     public L getAt(int index)
/*     */     {
/* 328 */       return (L)this.array[index];
/*     */     }
/*     */     
/*     */     public int size() {
/* 332 */       return this.array.length;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class SmallLazyStriped<L>
/*     */     extends PowerOfTwoStriped<L>
/*     */   {
/*     */     final AtomicReferenceArray<ArrayReference<? extends L>> locks;
/*     */     
/*     */     final Supplier<L> supplier;
/*     */     final int size;
/* 345 */     final ReferenceQueue<L> queue = new ReferenceQueue();
/*     */     
/*     */     SmallLazyStriped(int stripes, Supplier<L> supplier) {
/* 348 */       super();
/* 349 */       this.size = (this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1);
/* 350 */       this.locks = new AtomicReferenceArray(this.size);
/* 351 */       this.supplier = supplier;
/*     */     }
/*     */     
/*     */     public L getAt(int index) {
/* 355 */       if (this.size != Integer.MAX_VALUE) {
/* 356 */         Preconditions.checkElementIndex(index, size());
/*     */       }
/* 358 */       ArrayReference<? extends L> existingRef = (ArrayReference)this.locks.get(index);
/* 359 */       L existing = existingRef == null ? null : existingRef.get();
/* 360 */       if (existing != null) {
/* 361 */         return existing;
/*     */       }
/* 363 */       L created = this.supplier.get();
/* 364 */       ArrayReference<L> newRef = new ArrayReference(created, index, this.queue);
/* 365 */       while (!this.locks.compareAndSet(index, existingRef, newRef))
/*     */       {
/* 367 */         existingRef = (ArrayReference)this.locks.get(index);
/* 368 */         existing = existingRef == null ? null : existingRef.get();
/* 369 */         if (existing != null) {
/* 370 */           return existing;
/*     */         }
/*     */       }
/* 373 */       drainQueue();
/* 374 */       return created;
/*     */     }
/*     */     
/*     */ 
/*     */     private void drainQueue()
/*     */     {
/*     */       Reference<? extends L> ref;
/*     */       
/* 382 */       while ((ref = this.queue.poll()) != null)
/*     */       {
/* 384 */         ArrayReference<? extends L> arrayRef = (ArrayReference)ref;
/*     */         
/*     */ 
/* 387 */         this.locks.compareAndSet(arrayRef.index, arrayRef, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public int size() {
/* 392 */       return this.size;
/*     */     }
/*     */     
/*     */     private static final class ArrayReference<L> extends WeakReference<L> {
/*     */       final int index;
/*     */       
/*     */       ArrayReference(L referent, int index, ReferenceQueue<L> queue) {
/* 399 */         super(queue);
/* 400 */         this.index = index;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class LargeLazyStriped<L>
/*     */     extends PowerOfTwoStriped<L>
/*     */   {
/*     */     final ConcurrentMap<Integer, L> locks;
/*     */     final Supplier<L> supplier;
/*     */     final int size;
/*     */     
/*     */     LargeLazyStriped(int stripes, Supplier<L> supplier)
/*     */     {
/* 416 */       super();
/* 417 */       this.size = (this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1);
/* 418 */       this.supplier = supplier;
/* 419 */       this.locks = new MapMaker().weakValues().makeMap();
/*     */     }
/*     */     
/*     */     public L getAt(int index) {
/* 423 */       if (this.size != Integer.MAX_VALUE) {
/* 424 */         Preconditions.checkElementIndex(index, size());
/*     */       }
/* 426 */       L existing = this.locks.get(Integer.valueOf(index));
/* 427 */       if (existing != null) {
/* 428 */         return existing;
/*     */       }
/* 430 */       L created = this.supplier.get();
/* 431 */       existing = this.locks.putIfAbsent(Integer.valueOf(index), created);
/* 432 */       return (L)MoreObjects.firstNonNull(existing, created);
/*     */     }
/*     */     
/*     */     public int size() {
/* 436 */       return this.size;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int ceilToPowerOfTwo(int x)
/*     */   {
/* 446 */     return 1 << IntMath.log2(x, RoundingMode.CEILING);
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
/*     */   private static int smear(int hashCode)
/*     */   {
/* 459 */     hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
/* 460 */     return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
/*     */   }
/*     */   
/*     */ 
/*     */   private static class PaddedLock
/*     */     extends ReentrantLock
/*     */   {
/*     */     long q1;
/*     */     long q2;
/*     */     long q3;
/*     */     
/*     */     PaddedLock()
/*     */     {
/* 473 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PaddedSemaphore extends Semaphore {
/*     */     long q1;
/*     */     long q2;
/*     */     long q3;
/*     */     
/*     */     PaddedSemaphore(int permits) {
/* 483 */       super(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\Striped.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */