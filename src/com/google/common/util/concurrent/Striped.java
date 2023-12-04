/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.MapMaker;
/*     */ import com.google.common.math.IntMath;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.Semaphore;
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
/*     */ @Beta
/*     */ public abstract class Striped<L>
/*     */ {
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
/* 134 */     Object[] array = Iterables.toArray(keys, Object.class);
/* 135 */     int[] stripes = new int[array.length];
/* 136 */     for (int i = 0; i < array.length; i++) {
/* 137 */       stripes[i] = indexFor(array[i]);
/*     */     }
/* 139 */     Arrays.sort(stripes);
/* 140 */     for (int i = 0; i < array.length; i++) {
/* 141 */       array[i] = getAt(stripes[i]);
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
/* 161 */     List<L> asList = Arrays.asList(array);
/* 162 */     return Collections.unmodifiableList(asList);
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
/* 175 */     new CompactStriped(stripes, new Supplier()
/*     */     {
/* 177 */       public Lock get() { return new PaddedLock(); } }, null);
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
/* 190 */     new LazyStriped(stripes, new Supplier() {
/*     */       public Lock get() {
/* 192 */         return new ReentrantLock(false);
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
/*     */ 
/*     */   public static Striped<Semaphore> semaphore(int stripes, int permits)
/*     */   {
/* 206 */     new CompactStriped(stripes, new Supplier()
/*     */     {
/* 208 */       public Semaphore get() { return new PaddedSemaphore(this.val$permits); } }, null);
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
/* 222 */     new LazyStriped(stripes, new Supplier() {
/*     */       public Semaphore get() {
/* 224 */         return new Semaphore(this.val$permits, false);
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
/* 237 */     return new CompactStriped(stripes, READ_WRITE_LOCK_SUPPLIER, null);
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
/* 248 */     return new LazyStriped(stripes, READ_WRITE_LOCK_SUPPLIER);
/*     */   }
/*     */   
/*     */ 
/* 252 */   private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier()
/*     */   {
/*     */ 
/* 255 */     public ReadWriteLock get() { return new ReentrantReadWriteLock(); }
/*     */   };
/*     */   private static final int ALL_SET = -1;
/*     */   
/*     */   private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
/*     */     final int mask;
/*     */     
/*     */     PowerOfTwoStriped(int stripes) {
/* 263 */       super();
/* 264 */       Preconditions.checkArgument(stripes > 0, "Stripes must be positive");
/* 265 */       this.mask = (stripes > 1073741824 ? -1 : Striped.ceilToPowerOfTwo(stripes) - 1);
/*     */     }
/*     */     
/*     */     final int indexFor(Object key) {
/* 269 */       int hash = Striped.smear(key.hashCode());
/* 270 */       return hash & this.mask;
/*     */     }
/*     */     
/*     */     public final L get(Object key) {
/* 274 */       return (L)getAt(indexFor(key));
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
/* 287 */       super();
/* 288 */       Preconditions.checkArgument(stripes <= 1073741824, "Stripes must be <= 2^30)");
/*     */       
/* 290 */       this.array = new Object[this.mask + 1];
/* 291 */       for (int i = 0; i < this.array.length; i++) {
/* 292 */         this.array[i] = supplier.get();
/*     */       }
/*     */     }
/*     */     
/*     */     public L getAt(int index)
/*     */     {
/* 298 */       return (L)this.array[index];
/*     */     }
/*     */     
/*     */     public int size() {
/* 302 */       return this.array.length;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class LazyStriped<L>
/*     */     extends PowerOfTwoStriped<L>
/*     */   {
/*     */     final ConcurrentMap<Integer, L> locks;
/*     */     
/*     */     final Supplier<L> supplier;
/*     */     final int size;
/*     */     
/*     */     LazyStriped(int stripes, Supplier<L> supplier)
/*     */     {
/* 317 */       super();
/* 318 */       this.size = (this.mask == -1 ? Integer.MAX_VALUE : this.mask + 1);
/* 319 */       this.supplier = supplier;
/* 320 */       this.locks = new MapMaker().weakValues().makeMap();
/*     */     }
/*     */     
/*     */     public L getAt(int index) {
/* 324 */       if (this.size != Integer.MAX_VALUE) {
/* 325 */         Preconditions.checkElementIndex(index, size());
/*     */       }
/* 327 */       L existing = this.locks.get(Integer.valueOf(index));
/* 328 */       if (existing != null) {
/* 329 */         return existing;
/*     */       }
/* 331 */       L created = this.supplier.get();
/* 332 */       existing = this.locks.putIfAbsent(Integer.valueOf(index), created);
/* 333 */       return (L)Objects.firstNonNull(existing, created);
/*     */     }
/*     */     
/*     */     public int size() {
/* 337 */       return this.size;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int ceilToPowerOfTwo(int x)
/*     */   {
/* 347 */     return 1 << IntMath.log2(x, RoundingMode.CEILING);
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
/* 360 */     hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
/* 361 */     return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
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
/* 374 */       super();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PaddedSemaphore extends Semaphore {
/*     */     long q1;
/*     */     long q2;
/*     */     long q3;
/*     */     
/*     */     PaddedSemaphore(int permits) {
/* 384 */       super(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\Striped.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */