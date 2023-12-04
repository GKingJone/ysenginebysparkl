/*      */ package com.facebook.presto.jdbc.internal.guava.cache;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*      */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Equivalence;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Stopwatch;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Ticker;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.AbstractSequentialIterator;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*      */ import com.facebook.presto.jdbc.internal.guava.collect.Sets;
/*      */ import com.facebook.presto.jdbc.internal.guava.primitives.Ints;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.ExecutionError;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.Futures;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.ListenableFuture;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.MoreExecutors;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.SettableFuture;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.UncheckedExecutionException;
/*      */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.Uninterruptibles;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractQueue;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.annotation.Nullable;
/*      */ import javax.annotation.concurrent.GuardedBy;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ class LocalCache<K, V>
/*      */   extends AbstractMap<K, V>
/*      */   implements ConcurrentMap<K, V>
/*      */ {
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final int MAX_SEGMENTS = 65536;
/*      */   static final int CONTAINS_VALUE_RETRIES = 3;
/*      */   static final int DRAIN_THRESHOLD = 63;
/*      */   static final int DRAIN_MAX = 16;
/*  157 */   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
/*      */   
/*      */ 
/*      */ 
/*      */   final int segmentMask;
/*      */   
/*      */ 
/*      */ 
/*      */   final int segmentShift;
/*      */   
/*      */ 
/*      */ 
/*      */   final Segment<K, V>[] segments;
/*      */   
/*      */ 
/*      */ 
/*      */   final int concurrencyLevel;
/*      */   
/*      */ 
/*      */ 
/*      */   final Equivalence<Object> keyEquivalence;
/*      */   
/*      */ 
/*      */ 
/*      */   final Equivalence<Object> valueEquivalence;
/*      */   
/*      */ 
/*      */ 
/*      */   final Strength keyStrength;
/*      */   
/*      */ 
/*      */ 
/*      */   final Strength valueStrength;
/*      */   
/*      */ 
/*      */ 
/*      */   final long maxWeight;
/*      */   
/*      */ 
/*      */ 
/*      */   final Weigher<K, V> weigher;
/*      */   
/*      */ 
/*      */ 
/*      */   final long expireAfterAccessNanos;
/*      */   
/*      */ 
/*      */ 
/*      */   final long expireAfterWriteNanos;
/*      */   
/*      */ 
/*      */ 
/*      */   final long refreshNanos;
/*      */   
/*      */ 
/*      */ 
/*      */   final Queue<RemovalNotification<K, V>> removalNotificationQueue;
/*      */   
/*      */ 
/*      */ 
/*      */   final RemovalListener<K, V> removalListener;
/*      */   
/*      */ 
/*      */ 
/*      */   final Ticker ticker;
/*      */   
/*      */ 
/*      */   final EntryFactory entryFactory;
/*      */   
/*      */ 
/*      */   final AbstractCache.StatsCounter globalStatsCounter;
/*      */   
/*      */ 
/*      */   @Nullable
/*      */   final CacheLoader<? super K, V> defaultLoader;
/*      */   
/*      */ 
/*      */ 
/*      */   LocalCache(CacheBuilder<? super K, ? super V> builder, @Nullable CacheLoader<? super K, V> loader)
/*      */   {
/*  237 */     this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
/*      */     
/*  239 */     this.keyStrength = builder.getKeyStrength();
/*  240 */     this.valueStrength = builder.getValueStrength();
/*      */     
/*  242 */     this.keyEquivalence = builder.getKeyEquivalence();
/*  243 */     this.valueEquivalence = builder.getValueEquivalence();
/*      */     
/*  245 */     this.maxWeight = builder.getMaximumWeight();
/*  246 */     this.weigher = builder.getWeigher();
/*  247 */     this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
/*  248 */     this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
/*  249 */     this.refreshNanos = builder.getRefreshNanos();
/*      */     
/*  251 */     this.removalListener = builder.getRemovalListener();
/*  252 */     this.removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
/*      */     
/*      */ 
/*      */ 
/*  256 */     this.ticker = builder.getTicker(recordsTime());
/*  257 */     this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
/*  258 */     this.globalStatsCounter = ((AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*  259 */     this.defaultLoader = loader;
/*      */     
/*  261 */     int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
/*  262 */     if ((evictsBySize()) && (!customWeigher())) {
/*  263 */       initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  271 */     int segmentShift = 0;
/*  272 */     int segmentCount = 1;
/*      */     
/*  274 */     while ((segmentCount < this.concurrencyLevel) && ((!evictsBySize()) || (segmentCount * 20 <= this.maxWeight))) {
/*  275 */       segmentShift++;
/*  276 */       segmentCount <<= 1;
/*      */     }
/*  278 */     this.segmentShift = (32 - segmentShift);
/*  279 */     this.segmentMask = (segmentCount - 1);
/*      */     
/*  281 */     this.segments = newSegmentArray(segmentCount);
/*      */     
/*  283 */     int segmentCapacity = initialCapacity / segmentCount;
/*  284 */     if (segmentCapacity * segmentCount < initialCapacity) {
/*  285 */       segmentCapacity++;
/*      */     }
/*      */     
/*  288 */     int segmentSize = 1;
/*  289 */     while (segmentSize < segmentCapacity) {
/*  290 */       segmentSize <<= 1;
/*      */     }
/*      */     
/*  293 */     if (evictsBySize())
/*      */     {
/*  295 */       long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
/*  296 */       long remainder = this.maxWeight % segmentCount;
/*  297 */       for (int i = 0; i < this.segments.length; i++) {
/*  298 */         if (i == remainder) {
/*  299 */           maxSegmentWeight -= 1L;
/*      */         }
/*  301 */         this.segments[i] = createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */     else {
/*  305 */       for (int i = 0; i < this.segments.length; i++) {
/*  306 */         this.segments[i] = createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   boolean evictsBySize()
/*      */   {
/*  313 */     return this.maxWeight >= 0L;
/*      */   }
/*      */   
/*      */   boolean customWeigher() {
/*  317 */     return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
/*      */   }
/*      */   
/*      */   boolean expires() {
/*  321 */     return (expiresAfterWrite()) || (expiresAfterAccess());
/*      */   }
/*      */   
/*      */   boolean expiresAfterWrite() {
/*  325 */     return this.expireAfterWriteNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean expiresAfterAccess() {
/*  329 */     return this.expireAfterAccessNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean refreshes() {
/*  333 */     return this.refreshNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean usesAccessQueue() {
/*  337 */     return (expiresAfterAccess()) || (evictsBySize());
/*      */   }
/*      */   
/*      */   boolean usesWriteQueue() {
/*  341 */     return expiresAfterWrite();
/*      */   }
/*      */   
/*      */   boolean recordsWrite() {
/*  345 */     return (expiresAfterWrite()) || (refreshes());
/*      */   }
/*      */   
/*      */   boolean recordsAccess() {
/*  349 */     return expiresAfterAccess();
/*      */   }
/*      */   
/*      */   boolean recordsTime() {
/*  353 */     return (recordsWrite()) || (recordsAccess());
/*      */   }
/*      */   
/*      */   boolean usesWriteEntries() {
/*  357 */     return (usesWriteQueue()) || (recordsWrite());
/*      */   }
/*      */   
/*      */   boolean usesAccessEntries() {
/*  361 */     return (usesAccessQueue()) || (recordsAccess());
/*      */   }
/*      */   
/*      */   boolean usesKeyReferences() {
/*  365 */     return this.keyStrength != Strength.STRONG;
/*      */   }
/*      */   
/*      */   boolean usesValueReferences() {
/*  369 */     return this.valueStrength != Strength.STRONG;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static abstract enum Strength
/*      */   {
/*  378 */     STRONG, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  393 */     SOFT, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  409 */     WEAK;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Strength() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     abstract <K, V> ValueReference<K, V> referenceValue(Segment<K, V> paramSegment, ReferenceEntry<K, V> paramReferenceEntry, V paramV, int paramInt);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     abstract Equivalence<Object> defaultEquivalence();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static abstract enum EntryFactory
/*      */   {
/*  443 */     STRONG, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  450 */     STRONG_ACCESS, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  465 */     STRONG_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  480 */     STRONG_ACCESS_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  497 */     WEAK, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  504 */     WEAK_ACCESS, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  519 */     WEAK_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  534 */     WEAK_ACCESS_WRITE;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     static final int ACCESS_MASK = 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     static final int WRITE_MASK = 2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     static final int WEAK_MASK = 4;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  561 */     static final EntryFactory[] factories = { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
/*      */     
/*      */ 
/*      */     private EntryFactory() {}
/*      */     
/*      */     static EntryFactory getFactory(Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue)
/*      */     {
/*  568 */       int flags = (keyStrength == Strength.WEAK ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
/*      */       
/*      */ 
/*  571 */       return factories[flags];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     abstract <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> paramSegment, K paramK, int paramInt, @Nullable LocalCache.ReferenceEntry<K, V> paramReferenceEntry);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */     {
/*  594 */       return newEntry(segment, original.getKey(), original.getHash(), newNext);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     <K, V> void copyAccessEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry)
/*      */     {
/*  601 */       newEntry.setAccessTime(original.getAccessTime());
/*      */       
/*  603 */       LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
/*  604 */       LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
/*      */       
/*  606 */       LocalCache.nullifyAccessOrder(original);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     <K, V> void copyWriteEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry)
/*      */     {
/*  613 */       newEntry.setWriteTime(original.getWriteTime());
/*      */       
/*  615 */       LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
/*  616 */       LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
/*      */       
/*  618 */       LocalCache.nullifyWriteOrder(original);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  687 */   static final ValueReference<Object, Object> UNSET = new ValueReference()
/*      */   {
/*      */     public Object get() {
/*  690 */       return null;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/*  695 */       return 0;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<Object, Object> getEntry()
/*      */     {
/*  700 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, @Nullable Object value, ReferenceEntry<Object, Object> entry)
/*      */     {
/*  706 */       return this;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/*  711 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/*  716 */       return false;
/*      */     }
/*      */     
/*      */     public Object waitForValue()
/*      */     {
/*  721 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void notifyNewValue(Object newValue) {}
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */   static <K, V> ValueReference<K, V> unset()
/*      */   {
/*  733 */     return UNSET;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static abstract interface ValueReference<K, V>
/*      */   {
/*      */     @Nullable
/*      */     public abstract V get();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract V waitForValue()
/*      */       throws ExecutionException;
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract int getWeight();
/*      */     
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     public abstract LocalCache.ReferenceEntry<K, V> getEntry();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract ValueReference<K, V> copyFor(ReferenceQueue<V> paramReferenceQueue, @Nullable V paramV, ReferenceEntry<K, V> paramReferenceEntry);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void notifyNewValue(@Nullable V paramV);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract boolean isLoading();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract boolean isActive();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static abstract interface ReferenceEntry<K, V>
/*      */   {
/*      */     public abstract ValueReference<K, V> getValueReference();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setValueReference(ValueReference<K, V> paramValueReference);
/*      */     
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     public abstract ReferenceEntry<K, V> getNext();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract int getHash();
/*      */     
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     public abstract K getKey();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract long getAccessTime();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setAccessTime(long paramLong);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInAccessQueue();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setNextInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInAccessQueue();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setPreviousInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract long getWriteTime();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setWriteTime(long paramLong);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInWriteQueue();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setNextInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInWriteQueue();
/*      */     
/*      */ 
/*      */ 
/*      */     public abstract void setPreviousInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static enum NullEntry
/*      */     implements ReferenceEntry<Object, Object>
/*      */   {
/*  852 */     INSTANCE;
/*      */     
/*      */     private NullEntry() {}
/*      */     
/*  856 */     public ValueReference<Object, Object> getValueReference() { return null; }
/*      */     
/*      */ 
/*      */     public void setValueReference(ValueReference<Object, Object> valueReference) {}
/*      */     
/*      */ 
/*      */     public ReferenceEntry<Object, Object> getNext()
/*      */     {
/*  864 */       return null;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/*  869 */       return 0;
/*      */     }
/*      */     
/*      */     public Object getKey()
/*      */     {
/*  874 */       return null;
/*      */     }
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/*  879 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setAccessTime(long time) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getNextInAccessQueue()
/*      */     {
/*  887 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setNextInAccessQueue(ReferenceEntry<Object, Object> next) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getPreviousInAccessQueue()
/*      */     {
/*  895 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<Object, Object> previous) {}
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/*  903 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setWriteTime(long time) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getNextInWriteQueue()
/*      */     {
/*  911 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setNextInWriteQueue(ReferenceEntry<Object, Object> next) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getPreviousInWriteQueue()
/*      */     {
/*  919 */       return this;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<Object, Object> previous) {}
/*      */   }
/*      */   
/*      */   static abstract class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V>
/*      */   {
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/*  929 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/*  934 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/*  939 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/*  944 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/*  949 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/*  954 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/*  959 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/*  964 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/*  969 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/*  974 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/*  979 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/*  984 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/*  989 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/*  994 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/*  999 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1004 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1009 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> ReferenceEntry<K, V> nullEntry()
/*      */   {
/* 1015 */     return NullEntry.INSTANCE;
/*      */   }
/*      */   
/* 1018 */   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue()
/*      */   {
/*      */     public boolean offer(Object o) {
/* 1021 */       return true;
/*      */     }
/*      */     
/*      */     public Object peek()
/*      */     {
/* 1026 */       return null;
/*      */     }
/*      */     
/*      */     public Object poll()
/*      */     {
/* 1031 */       return null;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 1036 */       return 0;
/*      */     }
/*      */     
/*      */     public Iterator<Object> iterator()
/*      */     {
/* 1041 */       return ImmutableSet.of().iterator();
/*      */     }
/*      */   };
/*      */   Set<K> keySet;
/*      */   Collection<V> values;
/*      */   Set<Entry<K, V>> entrySet;
/*      */   
/*      */   static <E> Queue<E> discardingQueue()
/*      */   {
/* 1050 */     return DISCARDING_QUEUE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static class StrongEntry<K, V>
/*      */     extends AbstractReferenceEntry<K, V>
/*      */   {
/*      */     final K key;
/*      */     
/*      */ 
/*      */     final int hash;
/*      */     
/*      */     final ReferenceEntry<K, V> next;
/*      */     
/*      */ 
/*      */     StrongEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1068 */       this.key = key;
/* 1069 */       this.hash = hash;
/* 1070 */       this.next = next;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 1075 */       return (K)this.key;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1082 */     volatile ValueReference<K, V> valueReference = LocalCache.unset();
/*      */     
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/* 1086 */       return this.valueReference;
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/* 1091 */       this.valueReference = valueReference;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/* 1096 */       return this.hash;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/* 1101 */       return this.next;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongAccessEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongAccessEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1107 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1112 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1116 */       return this.accessTime;
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1121 */       this.accessTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1125 */     ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1129 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1134 */       this.nextAccess = next;
/*      */     }
/*      */     
/*      */ 
/* 1138 */     ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1142 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1147 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongWriteEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1153 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1158 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1162 */       return this.writeTime;
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1167 */       this.writeTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1171 */     ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1175 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1180 */       this.nextWrite = next;
/*      */     }
/*      */     
/*      */ 
/* 1184 */     ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1188 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1193 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongAccessWriteEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongAccessWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1199 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1204 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1208 */       return this.accessTime;
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1213 */       this.accessTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1217 */     ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1221 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1226 */       this.nextAccess = next;
/*      */     }
/*      */     
/*      */ 
/* 1230 */     ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1234 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1239 */       this.previousAccess = previous;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1244 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1248 */       return this.writeTime;
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1253 */       this.writeTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1257 */     ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1261 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1266 */       this.nextWrite = next;
/*      */     }
/*      */     
/*      */ 
/* 1270 */     ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1274 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1279 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
/*      */     final int hash;
/*      */     final ReferenceEntry<K, V> next;
/*      */     
/*      */     WeakEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1288 */       super(queue);
/* 1289 */       this.hash = hash;
/* 1290 */       this.next = next;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 1295 */       return (K)get();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1307 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1312 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1317 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1322 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1327 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1332 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1339 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1344 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1349 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1354 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1359 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1364 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1371 */     volatile ValueReference<K, V> valueReference = LocalCache.unset();
/*      */     
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/* 1375 */       return this.valueReference;
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/* 1380 */       this.valueReference = valueReference;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/* 1385 */       return this.hash;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/* 1390 */       return this.next;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakAccessEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakAccessEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1397 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1402 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1406 */       return this.accessTime;
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1411 */       this.accessTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1415 */     ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1419 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1424 */       this.nextAccess = next;
/*      */     }
/*      */     
/*      */ 
/* 1428 */     ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1432 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1437 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakWriteEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1444 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1449 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1453 */       return this.writeTime;
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1458 */       this.writeTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1462 */     ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1466 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1471 */       this.nextWrite = next;
/*      */     }
/*      */     
/*      */ 
/* 1475 */     ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1479 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1484 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakAccessWriteEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakAccessWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1491 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1496 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1500 */       return this.accessTime;
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1505 */       this.accessTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1509 */     ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1513 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1518 */       this.nextAccess = next;
/*      */     }
/*      */     
/*      */ 
/* 1522 */     ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1526 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1531 */       this.previousAccess = previous;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1536 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1540 */       return this.writeTime;
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1545 */       this.writeTime = time;
/*      */     }
/*      */     
/*      */ 
/* 1549 */     ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1553 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1558 */       this.nextWrite = next;
/*      */     }
/*      */     
/*      */ 
/* 1562 */     ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1566 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1571 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static class WeakValueReference<K, V>
/*      */     extends WeakReference<V>
/*      */     implements ValueReference<K, V>
/*      */   {
/*      */     final ReferenceEntry<K, V> entry;
/*      */     
/*      */     WeakValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry)
/*      */     {
/* 1583 */       super(queue);
/* 1584 */       this.entry = entry;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1589 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1594 */       return this.entry;
/*      */     }
/*      */     
/*      */ 
/*      */     public void notifyNewValue(V newValue) {}
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1603 */       return new WeakValueReference(queue, value, entry);
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1608 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1613 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1618 */       return (V)get();
/*      */     }
/*      */   }
/*      */   
/*      */   static class SoftValueReference<K, V>
/*      */     extends SoftReference<V>
/*      */     implements ValueReference<K, V>
/*      */   {
/*      */     final ReferenceEntry<K, V> entry;
/*      */     
/*      */     SoftValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry)
/*      */     {
/* 1630 */       super(queue);
/* 1631 */       this.entry = entry;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1636 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1641 */       return this.entry;
/*      */     }
/*      */     
/*      */ 
/*      */     public void notifyNewValue(V newValue) {}
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1650 */       return new SoftValueReference(queue, value, entry);
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1655 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1660 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1665 */       return (V)get();
/*      */     }
/*      */   }
/*      */   
/*      */   static class StrongValueReference<K, V>
/*      */     implements ValueReference<K, V>
/*      */   {
/*      */     final V referent;
/*      */     
/*      */     StrongValueReference(V referent)
/*      */     {
/* 1676 */       this.referent = referent;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/* 1681 */       return (V)this.referent;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1686 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1691 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1697 */       return this;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1702 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1707 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1712 */       return (V)get();
/*      */     }
/*      */     
/*      */ 
/*      */     public void notifyNewValue(V newValue) {}
/*      */   }
/*      */   
/*      */ 
/*      */   static final class WeightedWeakValueReference<K, V>
/*      */     extends WeakValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */     
/*      */     WeightedWeakValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1727 */       super(referent, entry);
/* 1728 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1733 */       return this.weight;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1739 */       return new WeightedWeakValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static final class WeightedSoftValueReference<K, V>
/*      */     extends SoftValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */     
/*      */     WeightedSoftValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1751 */       super(referent, entry);
/* 1752 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1757 */       return this.weight;
/*      */     }
/*      */     
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1762 */       return new WeightedSoftValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static final class WeightedStrongValueReference<K, V>
/*      */     extends StrongValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */     
/*      */     WeightedStrongValueReference(V referent, int weight)
/*      */     {
/* 1774 */       super();
/* 1775 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1780 */       return this.weight;
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
/*      */   static int rehash(int h)
/*      */   {
/* 1796 */     h += (h << 15 ^ 0xCD7D);
/* 1797 */     h ^= h >>> 10;
/* 1798 */     h += (h << 3);
/* 1799 */     h ^= h >>> 6;
/* 1800 */     h += (h << 2) + (h << 14);
/* 1801 */     return h ^ h >>> 16;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next)
/*      */   {
/* 1809 */     Segment<K, V> segment = segmentFor(hash);
/* 1810 */     segment.lock();
/*      */     try {
/* 1812 */       return segment.newEntry(key, hash, next);
/*      */     } finally {
/* 1814 */       segment.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */   {
/* 1824 */     int hash = original.getHash();
/* 1825 */     return segmentFor(hash).copyEntry(original, newNext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight)
/*      */   {
/* 1834 */     int hash = entry.getHash();
/* 1835 */     return this.valueStrength.referenceValue(segmentFor(hash), entry, Preconditions.checkNotNull(value), weight);
/*      */   }
/*      */   
/*      */   int hash(@Nullable Object key) {
/* 1839 */     int h = this.keyEquivalence.hash(key);
/* 1840 */     return rehash(h);
/*      */   }
/*      */   
/*      */   void reclaimValue(ValueReference<K, V> valueReference) {
/* 1844 */     ReferenceEntry<K, V> entry = valueReference.getEntry();
/* 1845 */     int hash = entry.getHash();
/* 1846 */     segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
/*      */   }
/*      */   
/*      */   void reclaimKey(ReferenceEntry<K, V> entry) {
/* 1850 */     int hash = entry.getHash();
/* 1851 */     segmentFor(hash).reclaimKey(entry, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   boolean isLive(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1860 */     return segmentFor(entry.getHash()).getLiveValue(entry, now) != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Segment<K, V> segmentFor(int hash)
/*      */   {
/* 1871 */     return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
/*      */   }
/*      */   
/*      */   Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */   {
/* 1876 */     return new Segment(this, initialCapacity, maxSegmentWeight, statsCounter);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   V getLiveValue(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1887 */     if (entry.getKey() == null) {
/* 1888 */       return null;
/*      */     }
/* 1890 */     V value = entry.getValueReference().get();
/* 1891 */     if (value == null) {
/* 1892 */       return null;
/*      */     }
/*      */     
/* 1895 */     if (isExpired(entry, now)) {
/* 1896 */       return null;
/*      */     }
/* 1898 */     return value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1907 */     Preconditions.checkNotNull(entry);
/* 1908 */     if ((expiresAfterAccess()) && (now - entry.getAccessTime() >= this.expireAfterAccessNanos))
/*      */     {
/* 1910 */       return true;
/*      */     }
/* 1912 */     if ((expiresAfterWrite()) && (now - entry.getWriteTime() >= this.expireAfterWriteNanos))
/*      */     {
/* 1914 */       return true;
/*      */     }
/* 1916 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 1923 */     previous.setNextInAccessQueue(next);
/* 1924 */     next.setPreviousInAccessQueue(previous);
/*      */   }
/*      */   
/*      */   static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled)
/*      */   {
/* 1929 */     ReferenceEntry<K, V> nullEntry = nullEntry();
/* 1930 */     nulled.setNextInAccessQueue(nullEntry);
/* 1931 */     nulled.setPreviousInAccessQueue(nullEntry);
/*      */   }
/*      */   
/*      */   static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 1936 */     previous.setNextInWriteQueue(next);
/* 1937 */     next.setPreviousInWriteQueue(previous);
/*      */   }
/*      */   
/*      */   static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled)
/*      */   {
/* 1942 */     ReferenceEntry<K, V> nullEntry = nullEntry();
/* 1943 */     nulled.setNextInWriteQueue(nullEntry);
/* 1944 */     nulled.setPreviousInWriteQueue(nullEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void processPendingNotifications()
/*      */   {
/*      */     RemovalNotification<K, V> notification;
/*      */     
/*      */ 
/* 1954 */     while ((notification = (RemovalNotification)this.removalNotificationQueue.poll()) != null) {
/*      */       try {
/* 1956 */         this.removalListener.onRemoval(notification);
/*      */       } catch (Throwable e) {
/* 1958 */         logger.log(Level.WARNING, "Exception thrown by removal listener", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   final Segment<K, V>[] newSegmentArray(int ssize)
/*      */   {
/* 1965 */     return new Segment[ssize];
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
/*      */   static class Segment<K, V>
/*      */     extends ReentrantLock
/*      */   {
/*      */     final LocalCache<K, V> map;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     volatile int count;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     long totalWeight;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int modCount;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int threshold;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     final long maxSegmentWeight;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     final ReferenceQueue<K> keyReferenceQueue;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     final ReferenceQueue<V> valueReferenceQueue;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     final Queue<ReferenceEntry<K, V>> recencyQueue;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2070 */     final AtomicInteger readCount = new AtomicInteger();
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     final Queue<ReferenceEntry<K, V>> writeQueue;
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     final Queue<ReferenceEntry<K, V>> accessQueue;
/*      */     
/*      */ 
/*      */ 
/*      */     final AbstractCache.StatsCounter statsCounter;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     Segment(LocalCache<K, V> map, int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */     {
/* 2091 */       this.map = map;
/* 2092 */       this.maxSegmentWeight = maxSegmentWeight;
/* 2093 */       this.statsCounter = ((AbstractCache.StatsCounter)Preconditions.checkNotNull(statsCounter));
/* 2094 */       initTable(newEntryArray(initialCapacity));
/*      */       
/* 2096 */       this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue() : null);
/*      */       
/*      */ 
/* 2099 */       this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue() : null);
/*      */       
/*      */ 
/* 2102 */       this.recencyQueue = (map.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
/*      */       
/*      */ 
/*      */ 
/* 2106 */       this.writeQueue = (map.usesWriteQueue() ? new WriteQueue() : LocalCache.discardingQueue());
/*      */       
/*      */ 
/*      */ 
/* 2110 */       this.accessQueue = (map.usesAccessQueue() ? new AccessQueue() : LocalCache.discardingQueue());
/*      */     }
/*      */     
/*      */ 
/*      */     AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(int size)
/*      */     {
/* 2116 */       return new AtomicReferenceArray(size);
/*      */     }
/*      */     
/*      */     void initTable(AtomicReferenceArray<ReferenceEntry<K, V>> newTable) {
/* 2120 */       this.threshold = (newTable.length() * 3 / 4);
/* 2121 */       if ((!this.map.customWeigher()) && (this.threshold == this.maxSegmentWeight))
/*      */       {
/* 2123 */         this.threshold += 1;
/*      */       }
/* 2125 */       this.table = newTable;
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     LocalCache.ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 2130 */       return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(key), hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     LocalCache.ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */     {
/* 2139 */       if (original.getKey() == null)
/*      */       {
/* 2141 */         return null;
/*      */       }
/*      */       
/* 2144 */       ValueReference<K, V> valueReference = original.getValueReference();
/* 2145 */       V value = valueReference.get();
/* 2146 */       if ((value == null) && (valueReference.isActive()))
/*      */       {
/* 2148 */         return null;
/*      */       }
/*      */       
/* 2151 */       ReferenceEntry<K, V> newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
/* 2152 */       newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
/* 2153 */       return newEntry;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void setValue(ReferenceEntry<K, V> entry, K key, V value, long now)
/*      */     {
/* 2161 */       ValueReference<K, V> previous = entry.getValueReference();
/* 2162 */       int weight = this.map.weigher.weigh(key, value);
/* 2163 */       Preconditions.checkState(weight >= 0, "Weights must be non-negative");
/*      */       
/* 2165 */       ValueReference<K, V> valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
/*      */       
/* 2167 */       entry.setValueReference(valueReference);
/* 2168 */       recordWrite(entry, weight, now);
/* 2169 */       previous.notifyNewValue(value);
/*      */     }
/*      */     
/*      */     V get(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2175 */       Preconditions.checkNotNull(key);
/* 2176 */       Preconditions.checkNotNull(loader);
/*      */       try { ReferenceEntry<K, V> e;
/* 2178 */         if (this.count != 0)
/*      */         {
/* 2180 */           e = getEntry(key, hash);
/* 2181 */           if (e != null) {
/* 2182 */             long now = this.map.ticker.read();
/* 2183 */             V value = getLiveValue(e, now);
/* 2184 */             if (value != null) {
/* 2185 */               recordRead(e, now);
/* 2186 */               this.statsCounter.recordHits(1);
/* 2187 */               return (V)scheduleRefresh(e, key, hash, value, now, loader);
/*      */             }
/* 2189 */             Object valueReference = e.getValueReference();
/* 2190 */             if (((ValueReference)valueReference).isLoading()) {
/* 2191 */               return (V)waitForLoadingValue(e, key, (ValueReference)valueReference);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2197 */         return (V)lockedGetOrLoad(key, hash, loader);
/*      */       } catch (ExecutionException ee) {
/* 2199 */         Throwable cause = ee.getCause();
/* 2200 */         if ((cause instanceof Error))
/* 2201 */           throw new ExecutionError((Error)cause);
/* 2202 */         if ((cause instanceof RuntimeException)) {
/* 2203 */           throw new UncheckedExecutionException(cause);
/*      */         }
/* 2205 */         throw ee;
/*      */       } finally {
/* 2207 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     V lockedGetOrLoad(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2214 */       valueReference = null;
/* 2215 */       LoadingValueReference<K, V> loadingValueReference = null;
/* 2216 */       boolean createNewEntry = true;
/*      */       
/* 2218 */       lock();
/*      */       try
/*      */       {
/* 2221 */         long now = this.map.ticker.read();
/* 2222 */         preWriteCleanup(now);
/*      */         
/* 2224 */         int newCount = this.count - 1;
/* 2225 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2226 */         int index = hash & table.length() - 1;
/* 2227 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 2229 */         for (e = first; e != null; e = e.getNext()) {
/* 2230 */           K entryKey = e.getKey();
/* 2231 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2233 */             valueReference = e.getValueReference();
/* 2234 */             if (valueReference.isLoading()) {
/* 2235 */               createNewEntry = false; break;
/*      */             }
/* 2237 */             V value = valueReference.get();
/* 2238 */             if (value == null) {
/* 2239 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
/* 2240 */             } else if (this.map.isExpired(e, now))
/*      */             {
/*      */ 
/* 2243 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
/*      */             } else {
/* 2245 */               recordLockedRead(e, now);
/* 2246 */               this.statsCounter.recordHits(1);
/*      */               
/* 2248 */               return value;
/*      */             }
/*      */             
/*      */ 
/* 2252 */             this.writeQueue.remove(e);
/* 2253 */             this.accessQueue.remove(e);
/* 2254 */             this.count = newCount;
/*      */             
/* 2256 */             break;
/*      */           }
/*      */         }
/*      */         
/* 2260 */         if (createNewEntry) {
/* 2261 */           loadingValueReference = new LoadingValueReference();
/*      */           
/* 2263 */           if (e == null) {
/* 2264 */             e = newEntry(key, hash, first);
/* 2265 */             e.setValueReference(loadingValueReference);
/* 2266 */             table.set(index, e);
/*      */           } else {
/* 2268 */             e.setValueReference(loadingValueReference);
/*      */           }
/*      */         }
/*      */       } finally {
/* 2272 */         unlock();
/* 2273 */         postWriteCleanup();
/*      */       }
/*      */       
/* 2276 */       if (createNewEntry)
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 2281 */           synchronized (e) {
/* 2282 */             return (V)loadSync(key, hash, loadingValueReference, loader);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2289 */           return (V)waitForLoadingValue(e, key, valueReference);
/*      */         }
/*      */         finally
/*      */         {
/* 2285 */           this.statsCounter.recordMisses(1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     V waitForLoadingValue(ReferenceEntry<K, V> e, K key, ValueReference<K, V> valueReference)
/*      */       throws ExecutionException
/*      */     {
/* 2295 */       if (!valueReference.isLoading()) {
/* 2296 */         throw new AssertionError();
/*      */       }
/*      */       
/* 2299 */       Preconditions.checkState(!Thread.holdsLock(e), "Recursive load of: %s", new Object[] { key });
/*      */       try
/*      */       {
/* 2302 */         V value = valueReference.waitForValue();
/* 2303 */         if (value == null) {
/* 2304 */           String str = String.valueOf(String.valueOf(key));throw new CacheLoader.InvalidCacheLoadException(35 + str.length() + "CacheLoader returned null for key " + str + ".");
/*      */         }
/*      */         
/* 2307 */         long now = this.map.ticker.read();
/* 2308 */         recordRead(e, now);
/* 2309 */         return value;
/*      */       } finally {
/* 2311 */         this.statsCounter.recordMisses(1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     V loadSync(K key, int hash, LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2319 */       ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2320 */       return (V)getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */     }
/*      */     
/*      */     ListenableFuture<V> loadAsync(final K key, final int hash, final LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */     {
/* 2325 */       final ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2326 */       loadingFuture.addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/* 2331 */             newValue = Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */           } catch (Throwable t) { V newValue;
/* 2333 */             LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
/* 2334 */             loadingValueReference.setException(t); } } }, MoreExecutors.directExecutor());
/*      */       
/*      */ 
/*      */ 
/* 2338 */       return loadingFuture;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     V getAndRecordStats(K key, int hash, LoadingValueReference<K, V> loadingValueReference, ListenableFuture<V> newValue)
/*      */       throws ExecutionException
/*      */     {
/* 2346 */       V value = null;
/*      */       try {
/* 2348 */         value = Uninterruptibles.getUninterruptibly(newValue);
/* 2349 */         Object localObject1; if (value == null) {
/* 2350 */           localObject1 = String.valueOf(String.valueOf(key));throw new CacheLoader.InvalidCacheLoadException(35 + ((String)localObject1).length() + "CacheLoader returned null for key " + (String)localObject1 + ".");
/*      */         }
/* 2352 */         this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
/* 2353 */         storeLoadedValue(key, hash, loadingValueReference, value);
/* 2354 */         return value;
/*      */       } finally {
/* 2356 */         if (value == null) {
/* 2357 */           this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
/* 2358 */           removeLoadingValue(key, hash, loadingValueReference);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     V scheduleRefresh(ReferenceEntry<K, V> entry, K key, int hash, V oldValue, long now, CacheLoader<? super K, V> loader)
/*      */     {
/* 2365 */       if ((this.map.refreshes()) && (now - entry.getWriteTime() > this.map.refreshNanos) && (!entry.getValueReference().isLoading()))
/*      */       {
/* 2367 */         V newValue = refresh(key, hash, loader, true);
/* 2368 */         if (newValue != null) {
/* 2369 */           return newValue;
/*      */         }
/*      */       }
/* 2372 */       return oldValue;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     V refresh(K key, int hash, CacheLoader<? super K, V> loader, boolean checkTime)
/*      */     {
/* 2383 */       LoadingValueReference<K, V> loadingValueReference = insertLoadingValueReference(key, hash, checkTime);
/*      */       
/* 2385 */       if (loadingValueReference == null) {
/* 2386 */         return null;
/*      */       }
/*      */       
/* 2389 */       ListenableFuture<V> result = loadAsync(key, hash, loadingValueReference, loader);
/* 2390 */       if (result.isDone()) {
/*      */         try {
/* 2392 */           return (V)Uninterruptibles.getUninterruptibly(result);
/*      */         }
/*      */         catch (Throwable t) {}
/*      */       }
/*      */       
/* 2397 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K key, int hash, boolean checkTime)
/*      */     {
/* 2407 */       ReferenceEntry<K, V> e = null;
/* 2408 */       lock();
/*      */       try {
/* 2410 */         long now = this.map.ticker.read();
/* 2411 */         preWriteCleanup(now);
/*      */         
/* 2413 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2414 */         int index = hash & table.length() - 1;
/* 2415 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/*      */         ValueReference<K, V> valueReference;
/* 2418 */         for (e = first; e != null; e = e.getNext()) {
/* 2419 */           K entryKey = e.getKey();
/* 2420 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/*      */ 
/*      */ 
/* 2424 */             valueReference = e.getValueReference();
/* 2425 */             if ((valueReference.isLoading()) || ((checkTime) && (now - e.getWriteTime() < this.map.refreshNanos)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2430 */               return null;
/*      */             }
/*      */             
/*      */ 
/* 2434 */             this.modCount += 1;
/* 2435 */             Object loadingValueReference = new LoadingValueReference(valueReference);
/*      */             
/* 2437 */             e.setValueReference((ValueReference)loadingValueReference);
/* 2438 */             return (LoadingValueReference<K, V>)loadingValueReference;
/*      */           }
/*      */         }
/*      */         
/* 2442 */         this.modCount += 1;
/* 2443 */         LoadingValueReference<K, V> loadingValueReference = new LoadingValueReference();
/* 2444 */         e = newEntry(key, hash, first);
/* 2445 */         e.setValueReference(loadingValueReference);
/* 2446 */         table.set(index, e);
/* 2447 */         return loadingValueReference;
/*      */       } finally {
/* 2449 */         unlock();
/* 2450 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void tryDrainReferenceQueues()
/*      */     {
/* 2460 */       if (tryLock()) {
/*      */         try {
/* 2462 */           drainReferenceQueues();
/*      */         } finally {
/* 2464 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void drainReferenceQueues()
/*      */     {
/* 2475 */       if (this.map.usesKeyReferences()) {
/* 2476 */         drainKeyReferenceQueue();
/*      */       }
/* 2478 */       if (this.map.usesValueReferences()) {
/* 2479 */         drainValueReferenceQueue();
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     void drainKeyReferenceQueue()
/*      */     {
/* 2486 */       int i = 0;
/* 2487 */       Reference<? extends K> ref; for (; (ref = this.keyReferenceQueue.poll()) != null; 
/*      */           
/*      */ 
/*      */ 
/* 2491 */           i == 16)
/*      */       {
/* 2489 */         ReferenceEntry<K, V> entry = (ReferenceEntry)ref;
/* 2490 */         this.map.reclaimKey(entry);
/* 2491 */         i++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void drainValueReferenceQueue()
/*      */     {
/* 2500 */       int i = 0;
/* 2501 */       Reference<? extends V> ref; for (; (ref = this.valueReferenceQueue.poll()) != null; 
/*      */           
/*      */ 
/*      */ 
/* 2505 */           i == 16)
/*      */       {
/* 2503 */         ValueReference<K, V> valueReference = (ValueReference)ref;
/* 2504 */         this.map.reclaimValue(valueReference);
/* 2505 */         i++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void clearReferenceQueues()
/*      */     {
/* 2515 */       if (this.map.usesKeyReferences()) {
/* 2516 */         clearKeyReferenceQueue();
/*      */       }
/* 2518 */       if (this.map.usesValueReferences()) {
/* 2519 */         clearValueReferenceQueue();
/*      */       }
/*      */     }
/*      */     
/*      */     void clearKeyReferenceQueue() {
/* 2524 */       while (this.keyReferenceQueue.poll() != null) {}
/*      */     }
/*      */     
/*      */     void clearValueReferenceQueue() {
/* 2528 */       while (this.valueReferenceQueue.poll() != null) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void recordRead(ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2541 */       if (this.map.recordsAccess()) {
/* 2542 */         entry.setAccessTime(now);
/*      */       }
/* 2544 */       this.recencyQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void recordLockedRead(ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2556 */       if (this.map.recordsAccess()) {
/* 2557 */         entry.setAccessTime(now);
/*      */       }
/* 2559 */       this.accessQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void recordWrite(ReferenceEntry<K, V> entry, int weight, long now)
/*      */     {
/* 2569 */       drainRecencyQueue();
/* 2570 */       this.totalWeight += weight;
/*      */       
/* 2572 */       if (this.map.recordsAccess()) {
/* 2573 */         entry.setAccessTime(now);
/*      */       }
/* 2575 */       if (this.map.recordsWrite()) {
/* 2576 */         entry.setWriteTime(now);
/*      */       }
/* 2578 */       this.accessQueue.add(entry);
/* 2579 */       this.writeQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void drainRecencyQueue()
/*      */     {
/*      */       ReferenceEntry<K, V> e;
/*      */       
/*      */ 
/* 2591 */       while ((e = (ReferenceEntry)this.recencyQueue.poll()) != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2596 */         if (this.accessQueue.contains(e)) {
/* 2597 */           this.accessQueue.add(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void tryExpireEntries(long now)
/*      */     {
/* 2608 */       if (tryLock()) {
/*      */         try {
/* 2610 */           expireEntries(now);
/*      */         } finally {
/* 2612 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     void expireEntries(long now)
/*      */     {
/* 2620 */       drainRecencyQueue();
/*      */       
/*      */       ReferenceEntry<K, V> e;
/* 2623 */       while (((e = (ReferenceEntry)this.writeQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2624 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2625 */           throw new AssertionError();
/*      */         }
/*      */       }
/* 2628 */       while (((e = (ReferenceEntry)this.accessQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2629 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2630 */           throw new AssertionError();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void enqueueNotification(ReferenceEntry<K, V> entry, RemovalCause cause)
/*      */     {
/* 2639 */       enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     void enqueueNotification(@Nullable K key, int hash, ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 2645 */       this.totalWeight -= valueReference.getWeight();
/* 2646 */       if (cause.wasEvicted()) {
/* 2647 */         this.statsCounter.recordEviction();
/*      */       }
/* 2649 */       if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
/* 2650 */         V value = valueReference.get();
/* 2651 */         RemovalNotification<K, V> notification = new RemovalNotification(key, value, cause);
/* 2652 */         this.map.removalNotificationQueue.offer(notification);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void evictEntries()
/*      */     {
/* 2662 */       if (!this.map.evictsBySize()) {
/* 2663 */         return;
/*      */       }
/*      */       
/* 2666 */       drainRecencyQueue();
/* 2667 */       while (this.totalWeight > this.maxSegmentWeight) {
/* 2668 */         ReferenceEntry<K, V> e = getNextEvictable();
/* 2669 */         if (!removeEntry(e, e.getHash(), RemovalCause.SIZE)) {
/* 2670 */           throw new AssertionError();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     LocalCache.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 2678 */       for (ReferenceEntry<K, V> e : this.accessQueue) {
/* 2679 */         int weight = e.getValueReference().getWeight();
/* 2680 */         if (weight > 0) {
/* 2681 */           return e;
/*      */         }
/*      */       }
/* 2684 */       throw new AssertionError();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     ReferenceEntry<K, V> getFirst(int hash)
/*      */     {
/* 2692 */       AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2693 */       return (ReferenceEntry)table.get(hash & table.length() - 1);
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getEntry(Object key, int hash)
/*      */     {
/* 2700 */       for (ReferenceEntry<K, V> e = getFirst(hash); e != null; e = e.getNext()) {
/* 2701 */         if (e.getHash() == hash)
/*      */         {
/*      */ 
/*      */ 
/* 2705 */           K entryKey = e.getKey();
/* 2706 */           if (entryKey == null) {
/* 2707 */             tryDrainReferenceQueues();
/*      */ 
/*      */ 
/*      */           }
/* 2711 */           else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
/* 2712 */             return e;
/*      */           }
/*      */         }
/*      */       }
/* 2716 */       return null;
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getLiveEntry(Object key, int hash, long now) {
/* 2721 */       ReferenceEntry<K, V> e = getEntry(key, hash);
/* 2722 */       if (e == null)
/* 2723 */         return null;
/* 2724 */       if (this.map.isExpired(e, now)) {
/* 2725 */         tryExpireEntries(now);
/* 2726 */         return null;
/*      */       }
/* 2728 */       return e;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     V getLiveValue(ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2736 */       if (entry.getKey() == null) {
/* 2737 */         tryDrainReferenceQueues();
/* 2738 */         return null;
/*      */       }
/* 2740 */       V value = entry.getValueReference().get();
/* 2741 */       if (value == null) {
/* 2742 */         tryDrainReferenceQueues();
/* 2743 */         return null;
/*      */       }
/*      */       
/* 2746 */       if (this.map.isExpired(entry, now)) {
/* 2747 */         tryExpireEntries(now);
/* 2748 */         return null;
/*      */       }
/* 2750 */       return value;
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V get(Object key, int hash) {
/*      */       try {
/* 2756 */         if (this.count != 0) {
/* 2757 */           long now = this.map.ticker.read();
/* 2758 */           ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
/* 2759 */           if (e == null) {
/* 2760 */             return null;
/*      */           }
/*      */           
/* 2763 */           Object value = e.getValueReference().get();
/* 2764 */           if (value != null) {
/* 2765 */             recordRead(e, now);
/* 2766 */             return (V)scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
/*      */           }
/* 2768 */           tryDrainReferenceQueues();
/*      */         }
/* 2770 */         return null;
/*      */       } finally {
/* 2772 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean containsKey(Object key, int hash) {
/*      */       try {
/* 2778 */         if (this.count != 0) {
/* 2779 */           long now = this.map.ticker.read();
/* 2780 */           ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
/* 2781 */           boolean bool1; if (e == null) {
/* 2782 */             return false;
/*      */           }
/* 2784 */           return e.getValueReference().get() != null;
/*      */         }
/*      */         
/* 2787 */         return false;
/*      */       } finally {
/* 2789 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @VisibleForTesting
/*      */     boolean containsValue(Object value)
/*      */     {
/*      */       try
/*      */       {
/* 2800 */         if (this.count != 0) {
/* 2801 */           long now = this.map.ticker.read();
/* 2802 */           AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2803 */           int length = table.length();
/* 2804 */           for (int i = 0; i < length; i++) {
/* 2805 */             for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
/* 2806 */               V entryValue = getLiveValue(e, now);
/* 2807 */               if (entryValue != null)
/*      */               {
/*      */ 
/* 2810 */                 if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2811 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2817 */         return false;
/*      */       } finally {
/* 2819 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V put(K key, int hash, V value, boolean onlyIfAbsent) {
/* 2825 */       lock();
/*      */       try {
/* 2827 */         long now = this.map.ticker.read();
/* 2828 */         preWriteCleanup(now);
/*      */         
/* 2830 */         int newCount = this.count + 1;
/* 2831 */         if (newCount > this.threshold) {
/* 2832 */           expand();
/* 2833 */           newCount = this.count + 1;
/*      */         }
/*      */         
/* 2836 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2837 */         int index = hash & table.length() - 1;
/* 2838 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/*      */         K entryKey;
/* 2841 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 2842 */           entryKey = e.getKey();
/* 2843 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/*      */ 
/*      */ 
/* 2847 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 2848 */             V entryValue = valueReference.get();
/*      */             V ?;
/* 2850 */             if (entryValue == null) {
/* 2851 */               this.modCount += 1;
/* 2852 */               if (valueReference.isActive()) {
/* 2853 */                 enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
/* 2854 */                 setValue(e, key, value, now);
/* 2855 */                 newCount = this.count;
/*      */               } else {
/* 2857 */                 setValue(e, key, value, now);
/* 2858 */                 newCount = this.count + 1;
/*      */               }
/* 2860 */               this.count = newCount;
/* 2861 */               evictEntries();
/* 2862 */               return null; }
/* 2863 */             if (onlyIfAbsent)
/*      */             {
/*      */ 
/*      */ 
/* 2867 */               recordLockedRead(e, now);
/* 2868 */               return entryValue;
/*      */             }
/*      */             
/* 2871 */             this.modCount += 1;
/* 2872 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2873 */             setValue(e, key, value, now);
/* 2874 */             evictEntries();
/* 2875 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2881 */         this.modCount += 1;
/* 2882 */         ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
/* 2883 */         setValue(newEntry, key, value, now);
/* 2884 */         table.set(index, newEntry);
/* 2885 */         newCount = this.count + 1;
/* 2886 */         this.count = newCount;
/* 2887 */         evictEntries();
/* 2888 */         return null;
/*      */       } finally {
/* 2890 */         unlock();
/* 2891 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void expand()
/*      */     {
/* 2900 */       AtomicReferenceArray<ReferenceEntry<K, V>> oldTable = this.table;
/* 2901 */       int oldCapacity = oldTable.length();
/* 2902 */       if (oldCapacity >= 1073741824) {
/* 2903 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2916 */       int newCount = this.count;
/* 2917 */       AtomicReferenceArray<ReferenceEntry<K, V>> newTable = newEntryArray(oldCapacity << 1);
/* 2918 */       this.threshold = (newTable.length() * 3 / 4);
/* 2919 */       int newMask = newTable.length() - 1;
/* 2920 */       for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++)
/*      */       {
/*      */ 
/* 2923 */         ReferenceEntry<K, V> head = (ReferenceEntry)oldTable.get(oldIndex);
/*      */         
/* 2925 */         if (head != null) {
/* 2926 */           ReferenceEntry<K, V> next = head.getNext();
/* 2927 */           int headIndex = head.getHash() & newMask;
/*      */           
/*      */ 
/* 2930 */           if (next == null) {
/* 2931 */             newTable.set(headIndex, head);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 2936 */             ReferenceEntry<K, V> tail = head;
/* 2937 */             int tailIndex = headIndex;
/* 2938 */             for (ReferenceEntry<K, V> e = next; e != null; e = e.getNext()) {
/* 2939 */               int newIndex = e.getHash() & newMask;
/* 2940 */               if (newIndex != tailIndex)
/*      */               {
/* 2942 */                 tailIndex = newIndex;
/* 2943 */                 tail = e;
/*      */               }
/*      */             }
/* 2946 */             newTable.set(tailIndex, tail);
/*      */             
/*      */ 
/* 2949 */             for (ReferenceEntry<K, V> e = head; e != tail; e = e.getNext()) {
/* 2950 */               int newIndex = e.getHash() & newMask;
/* 2951 */               ReferenceEntry<K, V> newNext = (ReferenceEntry)newTable.get(newIndex);
/* 2952 */               ReferenceEntry<K, V> newFirst = copyEntry(e, newNext);
/* 2953 */               if (newFirst != null) {
/* 2954 */                 newTable.set(newIndex, newFirst);
/*      */               } else {
/* 2956 */                 removeCollectedEntry(e);
/* 2957 */                 newCount--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2963 */       this.table = newTable;
/* 2964 */       this.count = newCount;
/*      */     }
/*      */     
/*      */     boolean replace(K key, int hash, V oldValue, V newValue) {
/* 2968 */       lock();
/*      */       try {
/* 2970 */         long now = this.map.ticker.read();
/* 2971 */         preWriteCleanup(now);
/*      */         
/* 2973 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2974 */         int index = hash & table.length() - 1;
/* 2975 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 2977 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 2978 */           K entryKey = e.getKey();
/* 2979 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2981 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 2982 */             V entryValue = valueReference.get();
/* 2983 */             int newCount; if (entryValue == null) {
/* 2984 */               if (valueReference.isActive())
/*      */               {
/* 2986 */                 newCount = this.count - 1;
/* 2987 */                 this.modCount += 1;
/* 2988 */                 ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */                 
/* 2990 */                 newCount = this.count - 1;
/* 2991 */                 table.set(index, newFirst);
/* 2992 */                 this.count = newCount;
/*      */               }
/* 2994 */               return 0;
/*      */             }
/*      */             
/* 2997 */             if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
/* 2998 */               this.modCount += 1;
/* 2999 */               enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3000 */               setValue(e, key, newValue, now);
/* 3001 */               evictEntries();
/* 3002 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 3006 */             recordLockedRead(e, now);
/* 3007 */             return 0;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 3012 */         return 0;
/*      */       } finally {
/* 3014 */         unlock();
/* 3015 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V replace(K key, int hash, V newValue) {
/* 3021 */       lock();
/*      */       try {
/* 3023 */         long now = this.map.ticker.read();
/* 3024 */         preWriteCleanup(now);
/*      */         
/* 3026 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3027 */         int index = hash & table.length() - 1;
/* 3028 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3030 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3031 */           K entryKey = e.getKey();
/* 3032 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3034 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3035 */             V entryValue = valueReference.get();
/* 3036 */             int newCount; if (entryValue == null) {
/* 3037 */               if (valueReference.isActive())
/*      */               {
/* 3039 */                 newCount = this.count - 1;
/* 3040 */                 this.modCount += 1;
/* 3041 */                 ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */                 
/* 3043 */                 newCount = this.count - 1;
/* 3044 */                 table.set(index, newFirst);
/* 3045 */                 this.count = newCount;
/*      */               }
/* 3047 */               return null;
/*      */             }
/*      */             
/* 3050 */             this.modCount += 1;
/* 3051 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3052 */             setValue(e, key, newValue, now);
/* 3053 */             evictEntries();
/* 3054 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/* 3058 */         return null;
/*      */       } finally {
/* 3060 */         unlock();
/* 3061 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V remove(Object key, int hash) {
/* 3067 */       lock();
/*      */       try {
/* 3069 */         long now = this.map.ticker.read();
/* 3070 */         preWriteCleanup(now);
/*      */         
/* 3072 */         int newCount = this.count - 1;
/* 3073 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3074 */         int index = hash & table.length() - 1;
/* 3075 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3077 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3078 */           K entryKey = e.getKey();
/* 3079 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3081 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3082 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3085 */             if (entryValue != null) {
/* 3086 */               cause = RemovalCause.EXPLICIT; } else { RemovalCause cause;
/* 3087 */               if (valueReference.isActive()) {
/* 3088 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3091 */                 return null;
/*      */             }
/*      */             RemovalCause cause;
/* 3094 */             this.modCount += 1;
/* 3095 */             Object newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */             
/* 3097 */             newCount = this.count - 1;
/* 3098 */             table.set(index, newFirst);
/* 3099 */             this.count = newCount;
/* 3100 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/* 3104 */         return null;
/*      */       } finally {
/* 3106 */         unlock();
/* 3107 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean storeLoadedValue(K key, int hash, LoadingValueReference<K, V> oldValueReference, V newValue)
/*      */     {
/* 3113 */       lock();
/*      */       try {
/* 3115 */         long now = this.map.ticker.read();
/* 3116 */         preWriteCleanup(now);
/*      */         
/* 3118 */         int newCount = this.count + 1;
/* 3119 */         if (newCount > this.threshold) {
/* 3120 */           expand();
/* 3121 */           newCount = this.count + 1;
/*      */         }
/*      */         
/* 3124 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3125 */         int index = hash & table.length() - 1;
/* 3126 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         K entryKey;
/* 3128 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3129 */           entryKey = e.getKey();
/* 3130 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3132 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3133 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3136 */             if ((oldValueReference == valueReference) || ((entryValue == null) && (valueReference != LocalCache.UNSET)))
/*      */             {
/* 3138 */               this.modCount += 1;
/* 3139 */               if (oldValueReference.isActive()) {
/* 3140 */                 cause = entryValue == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
/*      */                 
/* 3142 */                 enqueueNotification(key, hash, oldValueReference, cause);
/* 3143 */                 newCount--;
/*      */               }
/* 3145 */               setValue(e, key, newValue, now);
/* 3146 */               this.count = newCount;
/* 3147 */               evictEntries();
/* 3148 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 3152 */             valueReference = new WeightedStrongValueReference(newValue, 0);
/* 3153 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3154 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3158 */         this.modCount += 1;
/* 3159 */         ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
/* 3160 */         setValue(newEntry, key, newValue, now);
/* 3161 */         table.set(index, newEntry);
/* 3162 */         this.count = newCount;
/* 3163 */         evictEntries();
/* 3164 */         return 1;
/*      */       } finally {
/* 3166 */         unlock();
/* 3167 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean remove(Object key, int hash, Object value) {
/* 3172 */       lock();
/*      */       try {
/* 3174 */         long now = this.map.ticker.read();
/* 3175 */         preWriteCleanup(now);
/*      */         
/* 3177 */         int newCount = this.count - 1;
/* 3178 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3179 */         int index = hash & table.length() - 1;
/* 3180 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3182 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3183 */           K entryKey = e.getKey();
/* 3184 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3186 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3187 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3190 */             if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 3191 */               cause = RemovalCause.EXPLICIT; } else { RemovalCause cause;
/* 3192 */               if ((entryValue == null) && (valueReference.isActive())) {
/* 3193 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3196 */                 return false;
/*      */             }
/*      */             RemovalCause cause;
/* 3199 */             this.modCount += 1;
/* 3200 */             Object newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */             
/* 3202 */             newCount = this.count - 1;
/* 3203 */             table.set(index, newFirst);
/* 3204 */             this.count = newCount;
/* 3205 */             return cause == RemovalCause.EXPLICIT;
/*      */           }
/*      */         }
/*      */         
/* 3209 */         return 0;
/*      */       } finally {
/* 3211 */         unlock();
/* 3212 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     void clear() {
/* 3217 */       if (this.count != 0) {
/* 3218 */         lock();
/*      */         try {
/* 3220 */           AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3221 */           for (int i = 0; i < table.length(); i++) {
/* 3222 */             for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(i); e != null; e = e.getNext())
/*      */             {
/* 3224 */               if (e.getValueReference().isActive()) {
/* 3225 */                 enqueueNotification(e, RemovalCause.EXPLICIT);
/*      */               }
/*      */             }
/*      */           }
/* 3229 */           for (int i = 0; i < table.length(); i++) {
/* 3230 */             table.set(i, null);
/*      */           }
/* 3232 */           clearReferenceQueues();
/* 3233 */           this.writeQueue.clear();
/* 3234 */           this.accessQueue.clear();
/* 3235 */           this.readCount.set(0);
/*      */           
/* 3237 */           this.modCount += 1;
/* 3238 */           this.count = 0;
/*      */         } finally {
/* 3240 */           unlock();
/* 3241 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("this")
/*      */     LocalCache.ReferenceEntry<K, V> removeValueFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry, @Nullable K key, int hash, ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 3251 */       enqueueNotification(key, hash, valueReference, cause);
/* 3252 */       this.writeQueue.remove(entry);
/* 3253 */       this.accessQueue.remove(entry);
/*      */       
/* 3255 */       if (valueReference.isLoading()) {
/* 3256 */         valueReference.notifyNewValue(null);
/* 3257 */         return first;
/*      */       }
/* 3259 */       return removeEntryFromChain(first, entry);
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("this")
/*      */     LocalCache.ReferenceEntry<K, V> removeEntryFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry)
/*      */     {
/* 3267 */       int newCount = this.count;
/* 3268 */       ReferenceEntry<K, V> newFirst = entry.getNext();
/* 3269 */       for (ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
/* 3270 */         ReferenceEntry<K, V> next = copyEntry(e, newFirst);
/* 3271 */         if (next != null) {
/* 3272 */           newFirst = next;
/*      */         } else {
/* 3274 */           removeCollectedEntry(e);
/* 3275 */           newCount--;
/*      */         }
/*      */       }
/* 3278 */       this.count = newCount;
/* 3279 */       return newFirst;
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     void removeCollectedEntry(ReferenceEntry<K, V> entry) {
/* 3284 */       enqueueNotification(entry, RemovalCause.COLLECTED);
/* 3285 */       this.writeQueue.remove(entry);
/* 3286 */       this.accessQueue.remove(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean reclaimKey(ReferenceEntry<K, V> entry, int hash)
/*      */     {
/* 3293 */       lock();
/*      */       try {
/* 3295 */         int newCount = this.count - 1;
/* 3296 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3297 */         int index = hash & table.length() - 1;
/* 3298 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3300 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3301 */           if (e == entry) {
/* 3302 */             this.modCount += 1;
/* 3303 */             ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
/*      */             
/* 3305 */             newCount = this.count - 1;
/* 3306 */             table.set(index, newFirst);
/* 3307 */             this.count = newCount;
/* 3308 */             return true;
/*      */           }
/*      */         }
/*      */         
/* 3312 */         return 0;
/*      */       } finally {
/* 3314 */         unlock();
/* 3315 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean reclaimValue(K key, int hash, ValueReference<K, V> valueReference)
/*      */     {
/* 3323 */       lock();
/*      */       try {
/* 3325 */         int newCount = this.count - 1;
/* 3326 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3327 */         int index = hash & table.length() - 1;
/* 3328 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3330 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3331 */           K entryKey = e.getKey();
/* 3332 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3334 */             ValueReference<K, V> v = e.getValueReference();
/* 3335 */             ReferenceEntry<K, V> newFirst; if (v == valueReference) {
/* 3336 */               this.modCount += 1;
/* 3337 */               newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */               
/* 3339 */               newCount = this.count - 1;
/* 3340 */               table.set(index, newFirst);
/* 3341 */               this.count = newCount;
/* 3342 */               return true;
/*      */             }
/* 3344 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3348 */         return 0;
/*      */       } finally {
/* 3350 */         unlock();
/* 3351 */         if (!isHeldByCurrentThread()) {
/* 3352 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     boolean removeLoadingValue(K key, int hash, LoadingValueReference<K, V> valueReference) {
/* 3358 */       lock();
/*      */       try {
/* 3360 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3361 */         int index = hash & table.length() - 1;
/* 3362 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3364 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3365 */           K entryKey = e.getKey();
/* 3366 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3368 */             ValueReference<K, V> v = e.getValueReference();
/* 3369 */             ReferenceEntry<K, V> newFirst; if (v == valueReference) {
/* 3370 */               if (valueReference.isActive()) {
/* 3371 */                 e.setValueReference(valueReference.getOldValue());
/*      */               } else {
/* 3373 */                 newFirst = removeEntryFromChain(first, e);
/* 3374 */                 table.set(index, newFirst);
/*      */               }
/* 3376 */               return 1;
/*      */             }
/* 3378 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3382 */         return 0;
/*      */       } finally {
/* 3384 */         unlock();
/* 3385 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("this")
/*      */     boolean removeEntry(ReferenceEntry<K, V> entry, int hash, RemovalCause cause) {
/* 3391 */       int newCount = this.count - 1;
/* 3392 */       AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3393 */       int index = hash & table.length() - 1;
/* 3394 */       ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */       
/* 3396 */       for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3397 */         if (e == entry) {
/* 3398 */           this.modCount += 1;
/* 3399 */           ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
/*      */           
/* 3401 */           newCount = this.count - 1;
/* 3402 */           table.set(index, newFirst);
/* 3403 */           this.count = newCount;
/* 3404 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 3408 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     void postReadCleanup()
/*      */     {
/* 3416 */       if ((this.readCount.incrementAndGet() & 0x3F) == 0) {
/* 3417 */         cleanUp();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("this")
/*      */     void preWriteCleanup(long now)
/*      */     {
/* 3429 */       runLockedCleanup(now);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void postWriteCleanup()
/*      */     {
/* 3436 */       runUnlockedCleanup();
/*      */     }
/*      */     
/*      */     void cleanUp() {
/* 3440 */       long now = this.map.ticker.read();
/* 3441 */       runLockedCleanup(now);
/* 3442 */       runUnlockedCleanup();
/*      */     }
/*      */     
/*      */     void runLockedCleanup(long now) {
/* 3446 */       if (tryLock()) {
/*      */         try {
/* 3448 */           drainReferenceQueues();
/* 3449 */           expireEntries(now);
/* 3450 */           this.readCount.set(0);
/*      */         } finally {
/* 3452 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     void runUnlockedCleanup()
/*      */     {
/* 3459 */       if (!isHeldByCurrentThread()) {
/* 3460 */         this.map.processPendingNotifications();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class LoadingValueReference<K, V>
/*      */     implements ValueReference<K, V>
/*      */   {
/*      */     volatile ValueReference<K, V> oldValue;
/* 3470 */     final SettableFuture<V> futureValue = SettableFuture.create();
/* 3471 */     final Stopwatch stopwatch = Stopwatch.createUnstarted();
/*      */     
/*      */     public LoadingValueReference() {
/* 3474 */       this(LocalCache.unset());
/*      */     }
/*      */     
/*      */     public LoadingValueReference(ValueReference<K, V> oldValue) {
/* 3478 */       this.oldValue = oldValue;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 3483 */       return true;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 3488 */       return this.oldValue.isActive();
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 3493 */       return this.oldValue.getWeight();
/*      */     }
/*      */     
/*      */     public boolean set(@Nullable V newValue) {
/* 3497 */       return this.futureValue.set(newValue);
/*      */     }
/*      */     
/*      */     public boolean setException(Throwable t) {
/* 3501 */       return this.futureValue.setException(t);
/*      */     }
/*      */     
/*      */     private ListenableFuture<V> fullyFailedFuture(Throwable t) {
/* 3505 */       return Futures.immediateFailedFuture(t);
/*      */     }
/*      */     
/*      */     public void notifyNewValue(@Nullable V newValue)
/*      */     {
/* 3510 */       if (newValue != null)
/*      */       {
/*      */ 
/* 3513 */         set(newValue);
/*      */       }
/*      */       else {
/* 3516 */         this.oldValue = LocalCache.unset();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader)
/*      */     {
/* 3523 */       this.stopwatch.start();
/* 3524 */       V previousValue = this.oldValue.get();
/*      */       try {
/* 3526 */         if (previousValue == null) {
/* 3527 */           V newValue = loader.load(key);
/* 3528 */           return set(newValue) ? this.futureValue : Futures.immediateFuture(newValue);
/*      */         }
/* 3530 */         ListenableFuture<V> newValue = loader.reload(key, previousValue);
/* 3531 */         if (newValue == null) {
/* 3532 */           return Futures.immediateFuture(null);
/*      */         }
/*      */         
/*      */ 
/* 3536 */         Futures.transform(newValue, new Function()
/*      */         {
/*      */           public V apply(V newValue) {
/* 3539 */             LoadingValueReference.this.set(newValue);
/* 3540 */             return newValue;
/*      */           }
/*      */         });
/*      */       } catch (Throwable t) {
/* 3544 */         if ((t instanceof InterruptedException)) {
/* 3545 */           Thread.currentThread().interrupt();
/*      */         }
/* 3547 */         return setException(t) ? this.futureValue : fullyFailedFuture(t);
/*      */       }
/*      */     }
/*      */     
/*      */     public long elapsedNanos() {
/* 3552 */       return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
/*      */     }
/*      */     
/*      */     public V waitForValue() throws ExecutionException
/*      */     {
/* 3557 */       return (V)Uninterruptibles.getUninterruptibly(this.futureValue);
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/* 3562 */       return (V)this.oldValue.get();
/*      */     }
/*      */     
/*      */     public ValueReference<K, V> getOldValue() {
/* 3566 */       return this.oldValue;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 3571 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, @Nullable V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 3577 */       return this;
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
/*      */   static final class WriteQueue<K, V>
/*      */     extends AbstractQueue<ReferenceEntry<K, V>>
/*      */   {
/* 3595 */     final ReferenceEntry<K, V> head = new AbstractReferenceEntry()
/*      */     {
/*      */       public long getWriteTime()
/*      */       {
/* 3599 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3605 */       ReferenceEntry<K, V> nextWrite = this;
/*      */       
/*      */       public void setWriteTime(long time) {}
/*      */       
/* 3609 */       public ReferenceEntry<K, V> getNextInWriteQueue() { return this.nextWrite; }
/*      */       
/*      */ 
/*      */       public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */       {
/* 3614 */         this.nextWrite = next;
/*      */       }
/*      */       
/* 3617 */       ReferenceEntry<K, V> previousWrite = this;
/*      */       
/*      */       public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */       {
/* 3621 */         return this.previousWrite;
/*      */       }
/*      */       
/*      */       public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */       {
/* 3626 */         this.previousWrite = previous;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean offer(ReferenceEntry<K, V> entry)
/*      */     {
/* 3635 */       LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
/*      */       
/*      */ 
/* 3638 */       LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
/* 3639 */       LocalCache.connectWriteOrder(entry, this.head);
/*      */       
/* 3641 */       return true;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> peek()
/*      */     {
/* 3646 */       ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
/* 3647 */       return next == this.head ? null : next;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> poll()
/*      */     {
/* 3652 */       ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
/* 3653 */       if (next == this.head) {
/* 3654 */         return null;
/*      */       }
/*      */       
/* 3657 */       remove(next);
/* 3658 */       return next;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3664 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3665 */       ReferenceEntry<K, V> previous = e.getPreviousInWriteQueue();
/* 3666 */       ReferenceEntry<K, V> next = e.getNextInWriteQueue();
/* 3667 */       LocalCache.connectWriteOrder(previous, next);
/* 3668 */       LocalCache.nullifyWriteOrder(e);
/*      */       
/* 3670 */       return next != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3676 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3677 */       return e.getNextInWriteQueue() != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 3682 */       return this.head.getNextInWriteQueue() == this.head;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 3687 */       int size = 0;
/* 3688 */       for (ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head;
/* 3689 */           e = e.getNextInWriteQueue()) {
/* 3690 */         size++;
/*      */       }
/* 3692 */       return size;
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 3697 */       ReferenceEntry<K, V> e = this.head.getNextInWriteQueue();
/* 3698 */       while (e != this.head) {
/* 3699 */         ReferenceEntry<K, V> next = e.getNextInWriteQueue();
/* 3700 */         LocalCache.nullifyWriteOrder(e);
/* 3701 */         e = next;
/*      */       }
/*      */       
/* 3704 */       this.head.setNextInWriteQueue(this.head);
/* 3705 */       this.head.setPreviousInWriteQueue(this.head);
/*      */     }
/*      */     
/*      */     public Iterator<ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3710 */       new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
/* 3713 */           ReferenceEntry<K, V> next = previous.getNextInWriteQueue();
/* 3714 */           return next == WriteQueue.this.head ? null : next;
/*      */         }
/*      */       };
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
/*      */   static final class AccessQueue<K, V>
/*      */     extends AbstractQueue<ReferenceEntry<K, V>>
/*      */   {
/* 3732 */     final ReferenceEntry<K, V> head = new AbstractReferenceEntry()
/*      */     {
/*      */       public long getAccessTime()
/*      */       {
/* 3736 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3742 */       ReferenceEntry<K, V> nextAccess = this;
/*      */       
/*      */       public void setAccessTime(long time) {}
/*      */       
/* 3746 */       public ReferenceEntry<K, V> getNextInAccessQueue() { return this.nextAccess; }
/*      */       
/*      */ 
/*      */       public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */       {
/* 3751 */         this.nextAccess = next;
/*      */       }
/*      */       
/* 3754 */       ReferenceEntry<K, V> previousAccess = this;
/*      */       
/*      */       public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */       {
/* 3758 */         return this.previousAccess;
/*      */       }
/*      */       
/*      */       public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */       {
/* 3763 */         this.previousAccess = previous;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean offer(ReferenceEntry<K, V> entry)
/*      */     {
/* 3772 */       LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
/*      */       
/*      */ 
/* 3775 */       LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
/* 3776 */       LocalCache.connectAccessOrder(entry, this.head);
/*      */       
/* 3778 */       return true;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> peek()
/*      */     {
/* 3783 */       ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
/* 3784 */       return next == this.head ? null : next;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> poll()
/*      */     {
/* 3789 */       ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
/* 3790 */       if (next == this.head) {
/* 3791 */         return null;
/*      */       }
/*      */       
/* 3794 */       remove(next);
/* 3795 */       return next;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3801 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3802 */       ReferenceEntry<K, V> previous = e.getPreviousInAccessQueue();
/* 3803 */       ReferenceEntry<K, V> next = e.getNextInAccessQueue();
/* 3804 */       LocalCache.connectAccessOrder(previous, next);
/* 3805 */       LocalCache.nullifyAccessOrder(e);
/*      */       
/* 3807 */       return next != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3813 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3814 */       return e.getNextInAccessQueue() != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 3819 */       return this.head.getNextInAccessQueue() == this.head;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 3824 */       int size = 0;
/* 3825 */       for (ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head;
/* 3826 */           e = e.getNextInAccessQueue()) {
/* 3827 */         size++;
/*      */       }
/* 3829 */       return size;
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 3834 */       ReferenceEntry<K, V> e = this.head.getNextInAccessQueue();
/* 3835 */       while (e != this.head) {
/* 3836 */         ReferenceEntry<K, V> next = e.getNextInAccessQueue();
/* 3837 */         LocalCache.nullifyAccessOrder(e);
/* 3838 */         e = next;
/*      */       }
/*      */       
/* 3841 */       this.head.setNextInAccessQueue(this.head);
/* 3842 */       this.head.setPreviousInAccessQueue(this.head);
/*      */     }
/*      */     
/*      */     public Iterator<ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3847 */       new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
/* 3850 */           ReferenceEntry<K, V> next = previous.getNextInAccessQueue();
/* 3851 */           return next == AccessQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void cleanUp()
/*      */   {
/* 3860 */     for (Segment<?, ?> segment : this.segments) {
/* 3861 */       segment.cleanUp();
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
/*      */   public boolean isEmpty()
/*      */   {
/* 3876 */     long sum = 0L;
/* 3877 */     Segment<K, V>[] segments = this.segments;
/* 3878 */     for (int i = 0; i < segments.length; i++) {
/* 3879 */       if (segments[i].count != 0) {
/* 3880 */         return false;
/*      */       }
/* 3882 */       sum += segments[i].modCount;
/*      */     }
/*      */     
/* 3885 */     if (sum != 0L) {
/* 3886 */       for (int i = 0; i < segments.length; i++) {
/* 3887 */         if (segments[i].count != 0) {
/* 3888 */           return false;
/*      */         }
/* 3890 */         sum -= segments[i].modCount;
/*      */       }
/* 3892 */       if (sum != 0L) {
/* 3893 */         return false;
/*      */       }
/*      */     }
/* 3896 */     return true;
/*      */   }
/*      */   
/*      */   long longSize() {
/* 3900 */     Segment<K, V>[] segments = this.segments;
/* 3901 */     long sum = 0L;
/* 3902 */     for (int i = 0; i < segments.length; i++) {
/* 3903 */       sum += segments[i].count;
/*      */     }
/* 3905 */     return sum;
/*      */   }
/*      */   
/*      */   public int size()
/*      */   {
/* 3910 */     return Ints.saturatedCast(longSize());
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   public V get(@Nullable Object key)
/*      */   {
/* 3916 */     if (key == null) {
/* 3917 */       return null;
/*      */     }
/* 3919 */     int hash = hash(key);
/* 3920 */     return (V)segmentFor(hash).get(key, hash);
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   public V getIfPresent(Object key) {
/* 3925 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3926 */     V value = segmentFor(hash).get(key, hash);
/* 3927 */     if (value == null) {
/* 3928 */       this.globalStatsCounter.recordMisses(1);
/*      */     } else {
/* 3930 */       this.globalStatsCounter.recordHits(1);
/*      */     }
/* 3932 */     return value;
/*      */   }
/*      */   
/*      */   V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 3936 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3937 */     return (V)segmentFor(hash).get(key, hash, loader);
/*      */   }
/*      */   
/*      */   V getOrLoad(K key) throws ExecutionException {
/* 3941 */     return (V)get(key, this.defaultLoader);
/*      */   }
/*      */   
/*      */   ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
/* 3945 */     int hits = 0;
/* 3946 */     int misses = 0;
/*      */     
/* 3948 */     Map<K, V> result = Maps.newLinkedHashMap();
/* 3949 */     for (Object key : keys) {
/* 3950 */       V value = get(key);
/* 3951 */       if (value == null) {
/* 3952 */         misses++;
/*      */       }
/*      */       else
/*      */       {
/* 3956 */         K castKey = (K)key;
/* 3957 */         result.put(castKey, value);
/* 3958 */         hits++;
/*      */       }
/*      */     }
/* 3961 */     this.globalStatsCounter.recordHits(hits);
/* 3962 */     this.globalStatsCounter.recordMisses(misses);
/* 3963 */     return ImmutableMap.copyOf(result);
/*      */   }
/*      */   
/*      */   ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
/* 3967 */     int hits = 0;
/* 3968 */     int misses = 0;
/*      */     
/* 3970 */     Map<K, V> result = Maps.newLinkedHashMap();
/* 3971 */     Set<K> keysToLoad = Sets.newLinkedHashSet();
/* 3972 */     for (K key : keys) {
/* 3973 */       V value = get(key);
/* 3974 */       if (!result.containsKey(key)) {
/* 3975 */         result.put(key, value);
/* 3976 */         if (value == null) {
/* 3977 */           misses++;
/* 3978 */           keysToLoad.add(key);
/*      */         } else {
/* 3980 */           hits++;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3986 */       if (!keysToLoad.isEmpty()) {
/*      */         Iterator i$;
/* 3988 */         try { newEntries = loadAll(keysToLoad, this.defaultLoader);
/* 3989 */           for (K key : keysToLoad) {
/* 3990 */             V value = newEntries.get(key);
/* 3991 */             if (value == null) {
/* 3992 */               String str = String.valueOf(String.valueOf(key));throw new CacheLoader.InvalidCacheLoadException(37 + str.length() + "loadAll failed to return a value for " + str);
/*      */             }
/* 3994 */             result.put(key, value);
/*      */           }
/*      */         } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/*      */           Map<K, V> newEntries;
/* 3998 */           i$ = keysToLoad.iterator(); } while (i$.hasNext()) { K key = i$.next();
/* 3999 */           misses--;
/* 4000 */           result.put(key, get(key, this.defaultLoader));
/*      */         }
/*      */       }
/*      */       
/* 4004 */       return ImmutableMap.copyOf(result);
/*      */     } finally {
/* 4006 */       this.globalStatsCounter.recordHits(hits);
/* 4007 */       this.globalStatsCounter.recordMisses(misses);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Nullable
/*      */   Map<K, V> loadAll(Set<? extends K> keys, CacheLoader<? super K, V> loader)
/*      */     throws ExecutionException
/*      */   {
/* 4018 */     Preconditions.checkNotNull(loader);
/* 4019 */     Preconditions.checkNotNull(keys);
/* 4020 */     Stopwatch stopwatch = Stopwatch.createStarted();
/*      */     
/* 4022 */     boolean success = false;
/*      */     Map<K, V> result;
/*      */     try {
/* 4025 */       Map<K, V> map = loader.loadAll(keys);
/* 4026 */       result = map;
/* 4027 */       success = true;
/*      */     } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/* 4029 */       success = true;
/* 4030 */       throw e;
/*      */     } catch (InterruptedException e) {
/* 4032 */       Thread.currentThread().interrupt();
/* 4033 */       throw new ExecutionException(e);
/*      */     } catch (RuntimeException e) {
/* 4035 */       throw new UncheckedExecutionException(e);
/*      */     } catch (Exception e) {
/* 4037 */       throw new ExecutionException(e);
/*      */     } catch (Error e) {
/* 4039 */       throw new ExecutionError(e);
/*      */     } finally {
/* 4041 */       if (!success) {
/* 4042 */         this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/*      */       }
/*      */     }
/*      */     
/* 4046 */     if (result == null) {
/* 4047 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4048 */       e = String.valueOf(String.valueOf(loader));throw new CacheLoader.InvalidCacheLoadException(31 + e.length() + e + " returned null map from loadAll");
/*      */     }
/*      */     
/* 4051 */     stopwatch.stop();
/*      */     
/* 4053 */     boolean nullsPresent = false;
/* 4054 */     for (Object i$ = result.entrySet().iterator(); ((Iterator)i$).hasNext();) { Entry<K, V> entry = (Entry)((Iterator)i$).next();
/* 4055 */       K key = entry.getKey();
/* 4056 */       V value = entry.getValue();
/* 4057 */       if ((key == null) || (value == null))
/*      */       {
/* 4059 */         nullsPresent = true;
/*      */       } else {
/* 4061 */         put(key, value);
/*      */       }
/*      */     }
/*      */     
/* 4065 */     if (nullsPresent) {
/* 4066 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4067 */       i$ = String.valueOf(String.valueOf(loader));throw new CacheLoader.InvalidCacheLoadException(42 + ((String)i$).length() + (String)i$ + " returned null keys or values from loadAll");
/*      */     }
/*      */     
/*      */ 
/* 4071 */     this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4072 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   ReferenceEntry<K, V> getEntry(@Nullable Object key)
/*      */   {
/* 4081 */     if (key == null) {
/* 4082 */       return null;
/*      */     }
/* 4084 */     int hash = hash(key);
/* 4085 */     return segmentFor(hash).getEntry(key, hash);
/*      */   }
/*      */   
/*      */   void refresh(K key) {
/* 4089 */     int hash = hash(Preconditions.checkNotNull(key));
/* 4090 */     segmentFor(hash).refresh(key, hash, this.defaultLoader, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/* 4096 */     if (key == null) {
/* 4097 */       return false;
/*      */     }
/* 4099 */     int hash = hash(key);
/* 4100 */     return segmentFor(hash).containsKey(key, hash);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/* 4106 */     if (value == null) {
/* 4107 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4115 */     long now = this.ticker.read();
/* 4116 */     Segment<K, V>[] segments = this.segments;
/* 4117 */     long last = -1L;
/* 4118 */     for (int i = 0; i < 3; i++) {
/* 4119 */       long sum = 0L;
/* 4120 */       for (Segment<K, V> segment : segments)
/*      */       {
/*      */ 
/* 4123 */         int c = segment.count;
/*      */         
/* 4125 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = segment.table;
/* 4126 */         for (int j = 0; j < table.length(); j++) {
/* 4127 */           for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
/* 4128 */             V v = segment.getLiveValue(e, now);
/* 4129 */             if ((v != null) && (this.valueEquivalence.equivalent(value, v))) {
/* 4130 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 4134 */         sum += segment.modCount;
/*      */       }
/* 4136 */       if (sum == last) {
/*      */         break;
/*      */       }
/* 4139 */       last = sum;
/*      */     }
/* 4141 */     return false;
/*      */   }
/*      */   
/*      */   public V put(K key, V value)
/*      */   {
/* 4146 */     Preconditions.checkNotNull(key);
/* 4147 */     Preconditions.checkNotNull(value);
/* 4148 */     int hash = hash(key);
/* 4149 */     return (V)segmentFor(hash).put(key, hash, value, false);
/*      */   }
/*      */   
/*      */   public V putIfAbsent(K key, V value)
/*      */   {
/* 4154 */     Preconditions.checkNotNull(key);
/* 4155 */     Preconditions.checkNotNull(value);
/* 4156 */     int hash = hash(key);
/* 4157 */     return (V)segmentFor(hash).put(key, hash, value, true);
/*      */   }
/*      */   
/*      */   public void putAll(Map<? extends K, ? extends V> m)
/*      */   {
/* 4162 */     for (Entry<? extends K, ? extends V> e : m.entrySet()) {
/* 4163 */       put(e.getKey(), e.getValue());
/*      */     }
/*      */   }
/*      */   
/*      */   public V remove(@Nullable Object key)
/*      */   {
/* 4169 */     if (key == null) {
/* 4170 */       return null;
/*      */     }
/* 4172 */     int hash = hash(key);
/* 4173 */     return (V)segmentFor(hash).remove(key, hash);
/*      */   }
/*      */   
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/* 4178 */     if ((key == null) || (value == null)) {
/* 4179 */       return false;
/*      */     }
/* 4181 */     int hash = hash(key);
/* 4182 */     return segmentFor(hash).remove(key, hash, value);
/*      */   }
/*      */   
/*      */   public boolean replace(K key, @Nullable V oldValue, V newValue)
/*      */   {
/* 4187 */     Preconditions.checkNotNull(key);
/* 4188 */     Preconditions.checkNotNull(newValue);
/* 4189 */     if (oldValue == null) {
/* 4190 */       return false;
/*      */     }
/* 4192 */     int hash = hash(key);
/* 4193 */     return segmentFor(hash).replace(key, hash, oldValue, newValue);
/*      */   }
/*      */   
/*      */   public V replace(K key, V value)
/*      */   {
/* 4198 */     Preconditions.checkNotNull(key);
/* 4199 */     Preconditions.checkNotNull(value);
/* 4200 */     int hash = hash(key);
/* 4201 */     return (V)segmentFor(hash).replace(key, hash, value);
/*      */   }
/*      */   
/*      */   public void clear()
/*      */   {
/* 4206 */     for (Segment<K, V> segment : this.segments) {
/* 4207 */       segment.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   void invalidateAll(Iterable<?> keys)
/*      */   {
/* 4213 */     for (Object key : keys) {
/* 4214 */       remove(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/* 4223 */     Set<K> ks = this.keySet;
/* 4224 */     return ks != null ? ks : (this.keySet = new KeySet(this));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 4232 */     Collection<V> vs = this.values;
/* 4233 */     return vs != null ? vs : (this.values = new Values(this));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("Not supported.")
/*      */   public Set<Entry<K, V>> entrySet()
/*      */   {
/* 4242 */     Set<Entry<K, V>> es = this.entrySet;
/* 4243 */     return es != null ? es : (this.entrySet = new EntrySet(this));
/*      */   }
/*      */   
/*      */   abstract class HashIterator<T>
/*      */     implements Iterator<T>
/*      */   {
/*      */     int nextSegmentIndex;
/*      */     int nextTableIndex;
/*      */     Segment<K, V> currentSegment;
/*      */     AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
/*      */     ReferenceEntry<K, V> nextEntry;
/*      */     WriteThroughEntry nextExternal;
/*      */     WriteThroughEntry lastReturned;
/*      */     
/*      */     HashIterator()
/*      */     {
/* 4259 */       this.nextSegmentIndex = (LocalCache.this.segments.length - 1);
/* 4260 */       this.nextTableIndex = -1;
/* 4261 */       advance();
/*      */     }
/*      */     
/*      */     public abstract T next();
/*      */     
/*      */     final void advance()
/*      */     {
/* 4268 */       this.nextExternal = null;
/*      */       
/* 4270 */       if (nextInChain()) {
/* 4271 */         return;
/*      */       }
/*      */       
/* 4274 */       if (nextInTable()) {
/* 4275 */         return;
/*      */       }
/*      */       
/* 4278 */       while (this.nextSegmentIndex >= 0) {
/* 4279 */         this.currentSegment = LocalCache.this.segments[(this.nextSegmentIndex--)];
/* 4280 */         if (this.currentSegment.count != 0) {
/* 4281 */           this.currentTable = this.currentSegment.table;
/* 4282 */           this.nextTableIndex = (this.currentTable.length() - 1);
/* 4283 */           if (nextInTable()) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     boolean nextInChain()
/*      */     {
/* 4294 */       if (this.nextEntry != null) {
/* 4295 */         for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
/* 4296 */           if (advanceTo(this.nextEntry)) {
/* 4297 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 4301 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean nextInTable()
/*      */     {
/* 4308 */       while (this.nextTableIndex >= 0) {
/* 4309 */         if (((this.nextEntry = (ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && (
/* 4310 */           (advanceTo(this.nextEntry)) || (nextInChain()))) {
/* 4311 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 4315 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean advanceTo(ReferenceEntry<K, V> entry)
/*      */     {
/*      */       try
/*      */       {
/* 4324 */         long now = LocalCache.this.ticker.read();
/* 4325 */         K key = entry.getKey();
/* 4326 */         V value = LocalCache.this.getLiveValue(entry, now);
/* 4327 */         boolean bool; if (value != null) {
/* 4328 */           this.nextExternal = new WriteThroughEntry(LocalCache.this, key, value);
/* 4329 */           return true;
/*      */         }
/*      */         
/* 4332 */         return false;
/*      */       }
/*      */       finally {
/* 4335 */         this.currentSegment.postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean hasNext()
/*      */     {
/* 4341 */       return this.nextExternal != null;
/*      */     }
/*      */     
/*      */     WriteThroughEntry nextEntry() {
/* 4345 */       if (this.nextExternal == null) {
/* 4346 */         throw new NoSuchElementException();
/*      */       }
/* 4348 */       this.lastReturned = this.nextExternal;
/* 4349 */       advance();
/* 4350 */       return this.lastReturned;
/*      */     }
/*      */     
/*      */     public void remove()
/*      */     {
/* 4355 */       Preconditions.checkState(this.lastReturned != null);
/* 4356 */       LocalCache.this.remove(this.lastReturned.getKey());
/* 4357 */       this.lastReturned = null;
/*      */     }
/*      */   }
/*      */   
/* 4361 */   final class KeyIterator extends HashIterator<K> { KeyIterator() { super(); }
/*      */     
/*      */ 
/*      */ 
/* 4365 */     public K next() { return (K)nextEntry().getKey(); }
/*      */   }
/*      */   
/*      */   final class ValueIterator extends HashIterator<V> {
/* 4369 */     ValueIterator() { super(); }
/*      */     
/*      */     public V next()
/*      */     {
/* 4373 */       return (V)nextEntry().getValue();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   final class WriteThroughEntry
/*      */     implements Entry<K, V>
/*      */   {
/*      */     final K key;
/*      */     V value;
/*      */     
/*      */     WriteThroughEntry(V key)
/*      */     {
/* 4386 */       this.key = key;
/* 4387 */       this.value = value;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 4392 */       return (K)this.key;
/*      */     }
/*      */     
/*      */     public V getValue()
/*      */     {
/* 4397 */       return (V)this.value;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 4403 */       if ((object instanceof Map.Entry)) {
/* 4404 */         Entry<?, ?> that = (Entry)object;
/* 4405 */         return (this.key.equals(that.getKey())) && (this.value.equals(that.getValue()));
/*      */       }
/* 4407 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4413 */       return this.key.hashCode() ^ this.value.hashCode();
/*      */     }
/*      */     
/*      */     public V setValue(V newValue)
/*      */     {
/* 4418 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/* 4425 */       String str1 = String.valueOf(String.valueOf(getKey()));String str2 = String.valueOf(String.valueOf(getValue()));return 1 + str1.length() + str2.length() + str1 + "=" + str2;
/*      */     }
/*      */   }
/*      */   
/* 4429 */   final class EntryIterator extends HashIterator<Entry<K, V>> { EntryIterator() { super(); }
/*      */     
/*      */     public Entry<K, V> next()
/*      */     {
/* 4433 */       return nextEntry();
/*      */     }
/*      */   }
/*      */   
/*      */   abstract class AbstractCacheSet<T> extends AbstractSet<T> {
/*      */     final ConcurrentMap<?, ?> map;
/*      */     
/*      */     AbstractCacheSet() {
/* 4441 */       this.map = map;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 4446 */       return this.map.size();
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 4451 */       return this.map.isEmpty();
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 4456 */       this.map.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   final class KeySet extends AbstractCacheSet<K>
/*      */   {
/*      */     KeySet() {
/* 4463 */       super(map);
/*      */     }
/*      */     
/*      */     public Iterator<K> iterator()
/*      */     {
/* 4468 */       return new KeyIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4473 */       return this.map.containsKey(o);
/*      */     }
/*      */     
/*      */     public boolean remove(Object o)
/*      */     {
/* 4478 */       return this.map.remove(o) != null;
/*      */     }
/*      */   }
/*      */   
/*      */   final class Values extends AbstractCollection<V> {
/*      */     private final ConcurrentMap<?, ?> map;
/*      */     
/*      */     Values() {
/* 4486 */       this.map = map;
/*      */     }
/*      */     
/*      */     public int size() {
/* 4490 */       return this.map.size();
/*      */     }
/*      */     
/*      */     public boolean isEmpty() {
/* 4494 */       return this.map.isEmpty();
/*      */     }
/*      */     
/*      */     public void clear() {
/* 4498 */       this.map.clear();
/*      */     }
/*      */     
/*      */     public Iterator<V> iterator()
/*      */     {
/* 4503 */       return new ValueIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4508 */       return this.map.containsValue(o);
/*      */     }
/*      */   }
/*      */   
/*      */   final class EntrySet extends AbstractCacheSet<Entry<K, V>>
/*      */   {
/*      */     EntrySet() {
/* 4515 */       super(map);
/*      */     }
/*      */     
/*      */     public Iterator<Entry<K, V>> iterator()
/*      */     {
/* 4520 */       return new EntryIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4525 */       if (!(o instanceof Map.Entry)) {
/* 4526 */         return false;
/*      */       }
/* 4528 */       Entry<?, ?> e = (Entry)o;
/* 4529 */       Object key = e.getKey();
/* 4530 */       if (key == null) {
/* 4531 */         return false;
/*      */       }
/* 4533 */       V v = LocalCache.this.get(key);
/*      */       
/* 4535 */       return (v != null) && (LocalCache.this.valueEquivalence.equivalent(e.getValue(), v));
/*      */     }
/*      */     
/*      */     public boolean remove(Object o)
/*      */     {
/* 4540 */       if (!(o instanceof Map.Entry)) {
/* 4541 */         return false;
/*      */       }
/* 4543 */       Entry<?, ?> e = (Entry)o;
/* 4544 */       Object key = e.getKey();
/* 4545 */       return (key != null) && (LocalCache.this.remove(key, e.getValue()));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class ManualSerializationProxy<K, V>
/*      */     extends ForwardingCache<K, V>
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     final Strength keyStrength;
/*      */     
/*      */     final Strength valueStrength;
/*      */     
/*      */     final Equivalence<Object> keyEquivalence;
/*      */     
/*      */     final Equivalence<Object> valueEquivalence;
/*      */     
/*      */     final long expireAfterWriteNanos;
/*      */     
/*      */     final long expireAfterAccessNanos;
/*      */     
/*      */     final long maxWeight;
/*      */     
/*      */     final Weigher<K, V> weigher;
/*      */     final int concurrencyLevel;
/*      */     final RemovalListener<? super K, ? super V> removalListener;
/*      */     final Ticker ticker;
/*      */     final CacheLoader<? super K, V> loader;
/*      */     transient Cache<K, V> delegate;
/*      */     
/*      */     ManualSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4579 */       this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ManualSerializationProxy(Strength keyStrength, Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, long maxWeight, Weigher<K, V> weigher, int concurrencyLevel, RemovalListener<? super K, ? super V> removalListener, Ticker ticker, CacheLoader<? super K, V> loader)
/*      */     {
/* 4601 */       this.keyStrength = keyStrength;
/* 4602 */       this.valueStrength = valueStrength;
/* 4603 */       this.keyEquivalence = keyEquivalence;
/* 4604 */       this.valueEquivalence = valueEquivalence;
/* 4605 */       this.expireAfterWriteNanos = expireAfterWriteNanos;
/* 4606 */       this.expireAfterAccessNanos = expireAfterAccessNanos;
/* 4607 */       this.maxWeight = maxWeight;
/* 4608 */       this.weigher = weigher;
/* 4609 */       this.concurrencyLevel = concurrencyLevel;
/* 4610 */       this.removalListener = removalListener;
/* 4611 */       this.ticker = ((ticker == Ticker.systemTicker()) || (ticker == CacheBuilder.NULL_TICKER) ? null : ticker);
/*      */       
/* 4613 */       this.loader = loader;
/*      */     }
/*      */     
/*      */     CacheBuilder<K, V> recreateCacheBuilder() {
/* 4617 */       CacheBuilder<K, V> builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4624 */       builder.strictParsing = false;
/* 4625 */       if (this.expireAfterWriteNanos > 0L) {
/* 4626 */         builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4628 */       if (this.expireAfterAccessNanos > 0L) {
/* 4629 */         builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4631 */       if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
/* 4632 */         builder.weigher(this.weigher);
/* 4633 */         if (this.maxWeight != -1L) {
/* 4634 */           builder.maximumWeight(this.maxWeight);
/*      */         }
/*      */       }
/* 4637 */       else if (this.maxWeight != -1L) {
/* 4638 */         builder.maximumSize(this.maxWeight);
/*      */       }
/*      */       
/* 4641 */       if (this.ticker != null) {
/* 4642 */         builder.ticker(this.ticker);
/*      */       }
/* 4644 */       return builder;
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4648 */       in.defaultReadObject();
/* 4649 */       CacheBuilder<K, V> builder = recreateCacheBuilder();
/* 4650 */       this.delegate = builder.build();
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/* 4654 */       return this.delegate;
/*      */     }
/*      */     
/*      */     protected Cache<K, V> delegate()
/*      */     {
/* 4659 */       return this.delegate;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static final class LoadingSerializationProxy<K, V>
/*      */     extends ManualSerializationProxy<K, V>
/*      */     implements LoadingCache<K, V>, Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */ 
/*      */     transient LoadingCache<K, V> autoDelegate;
/*      */     
/*      */ 
/*      */ 
/*      */     LoadingSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4678 */       super();
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4682 */       in.defaultReadObject();
/* 4683 */       CacheBuilder<K, V> builder = recreateCacheBuilder();
/* 4684 */       this.autoDelegate = builder.build(this.loader);
/*      */     }
/*      */     
/*      */     public V get(K key) throws ExecutionException
/*      */     {
/* 4689 */       return (V)this.autoDelegate.get(key);
/*      */     }
/*      */     
/*      */     public V getUnchecked(K key)
/*      */     {
/* 4694 */       return (V)this.autoDelegate.getUnchecked(key);
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4699 */       return this.autoDelegate.getAll(keys);
/*      */     }
/*      */     
/*      */     public final V apply(K key)
/*      */     {
/* 4704 */       return (V)this.autoDelegate.apply(key);
/*      */     }
/*      */     
/*      */     public void refresh(K key)
/*      */     {
/* 4709 */       this.autoDelegate.refresh(key);
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/* 4713 */       return this.autoDelegate;
/*      */     }
/*      */   }
/*      */   
/*      */   static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {
/*      */     final LocalCache<K, V> localCache;
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/* 4721 */     LocalManualCache(CacheBuilder<? super K, ? super V> builder) { this(new LocalCache(builder, null)); }
/*      */     
/*      */     private LocalManualCache(LocalCache<K, V> localCache)
/*      */     {
/* 4725 */       this.localCache = localCache;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     public V getIfPresent(Object key)
/*      */     {
/* 4733 */       return (V)this.localCache.getIfPresent(key);
/*      */     }
/*      */     
/*      */     public V get(K key, final Callable<? extends V> valueLoader) throws ExecutionException
/*      */     {
/* 4738 */       Preconditions.checkNotNull(valueLoader);
/* 4739 */       (V)this.localCache.get(key, new CacheLoader()
/*      */       {
/*      */         public V load(Object key) throws Exception {
/* 4742 */           return (V)valueLoader.call();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*      */     {
/* 4749 */       return this.localCache.getAllPresent(keys);
/*      */     }
/*      */     
/*      */     public void put(K key, V value)
/*      */     {
/* 4754 */       this.localCache.put(key, value);
/*      */     }
/*      */     
/*      */     public void putAll(Map<? extends K, ? extends V> m)
/*      */     {
/* 4759 */       this.localCache.putAll(m);
/*      */     }
/*      */     
/*      */     public void invalidate(Object key)
/*      */     {
/* 4764 */       Preconditions.checkNotNull(key);
/* 4765 */       this.localCache.remove(key);
/*      */     }
/*      */     
/*      */     public void invalidateAll(Iterable<?> keys)
/*      */     {
/* 4770 */       this.localCache.invalidateAll(keys);
/*      */     }
/*      */     
/*      */     public void invalidateAll()
/*      */     {
/* 4775 */       this.localCache.clear();
/*      */     }
/*      */     
/*      */     public long size()
/*      */     {
/* 4780 */       return this.localCache.longSize();
/*      */     }
/*      */     
/*      */     public ConcurrentMap<K, V> asMap()
/*      */     {
/* 4785 */       return this.localCache;
/*      */     }
/*      */     
/*      */     public CacheStats stats()
/*      */     {
/* 4790 */       AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
/* 4791 */       aggregator.incrementBy(this.localCache.globalStatsCounter);
/* 4792 */       for (Segment<K, V> segment : this.localCache.segments) {
/* 4793 */         aggregator.incrementBy(segment.statsCounter);
/*      */       }
/* 4795 */       return aggregator.snapshot();
/*      */     }
/*      */     
/*      */     public void cleanUp()
/*      */     {
/* 4800 */       this.localCache.cleanUp();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4808 */       return new ManualSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */   
/*      */   static class LocalLoadingCache<K, V> extends LocalManualCache<K, V> implements LoadingCache<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     LocalLoadingCache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader) {
/* 4817 */       super(null);
/*      */     }
/*      */     
/*      */ 
/*      */     public V get(K key)
/*      */       throws ExecutionException
/*      */     {
/* 4824 */       return (V)this.localCache.getOrLoad(key);
/*      */     }
/*      */     
/*      */     public V getUnchecked(K key)
/*      */     {
/*      */       try {
/* 4830 */         return (V)get(key);
/*      */       } catch (ExecutionException e) {
/* 4832 */         throw new UncheckedExecutionException(e.getCause());
/*      */       }
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4838 */       return this.localCache.getAll(keys);
/*      */     }
/*      */     
/*      */     public void refresh(K key)
/*      */     {
/* 4843 */       this.localCache.refresh(key);
/*      */     }
/*      */     
/*      */     public final V apply(K key)
/*      */     {
/* 4848 */       return (V)getUnchecked(key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4857 */       return new LoadingSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\LocalCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */