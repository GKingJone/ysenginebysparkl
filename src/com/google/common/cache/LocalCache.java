/*      */ package com.google.common.cache;
/*      */ 
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.annotations.VisibleForTesting;
/*      */ import com.google.common.base.Equivalence;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Stopwatch;
/*      */ import com.google.common.base.Supplier;
/*      */ import com.google.common.base.Ticker;
/*      */ import com.google.common.collect.AbstractSequentialIterator;
/*      */ import com.google.common.collect.ImmutableMap;
/*      */ import com.google.common.collect.Iterators;
/*      */ import com.google.common.collect.Maps;
/*      */ import com.google.common.collect.Sets;
/*      */ import com.google.common.primitives.Ints;
/*      */ import com.google.common.util.concurrent.ExecutionError;
/*      */ import com.google.common.util.concurrent.Futures;
/*      */ import com.google.common.util.concurrent.ListenableFuture;
/*      */ import com.google.common.util.concurrent.ListeningExecutorService;
/*      */ import com.google.common.util.concurrent.MoreExecutors;
/*      */ import com.google.common.util.concurrent.SettableFuture;
/*      */ import com.google.common.util.concurrent.UncheckedExecutionException;
/*      */ import com.google.common.util.concurrent.Uninterruptibles;
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
/*  158 */   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
/*      */   
/*  160 */   static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
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
/*  240 */     this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
/*      */     
/*  242 */     this.keyStrength = builder.getKeyStrength();
/*  243 */     this.valueStrength = builder.getValueStrength();
/*      */     
/*  245 */     this.keyEquivalence = builder.getKeyEquivalence();
/*  246 */     this.valueEquivalence = builder.getValueEquivalence();
/*      */     
/*  248 */     this.maxWeight = builder.getMaximumWeight();
/*  249 */     this.weigher = builder.getWeigher();
/*  250 */     this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
/*  251 */     this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
/*  252 */     this.refreshNanos = builder.getRefreshNanos();
/*      */     
/*  254 */     this.removalListener = builder.getRemovalListener();
/*  255 */     this.removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
/*      */     
/*      */ 
/*      */ 
/*  259 */     this.ticker = builder.getTicker(recordsTime());
/*  260 */     this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
/*  261 */     this.globalStatsCounter = ((AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*  262 */     this.defaultLoader = loader;
/*      */     
/*  264 */     int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
/*  265 */     if ((evictsBySize()) && (!customWeigher())) {
/*  266 */       initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  274 */     int segmentShift = 0;
/*  275 */     int segmentCount = 1;
/*      */     
/*  277 */     while ((segmentCount < this.concurrencyLevel) && ((!evictsBySize()) || (segmentCount * 20 <= this.maxWeight))) {
/*  278 */       segmentShift++;
/*  279 */       segmentCount <<= 1;
/*      */     }
/*  281 */     this.segmentShift = (32 - segmentShift);
/*  282 */     this.segmentMask = (segmentCount - 1);
/*      */     
/*  284 */     this.segments = newSegmentArray(segmentCount);
/*      */     
/*  286 */     int segmentCapacity = initialCapacity / segmentCount;
/*  287 */     if (segmentCapacity * segmentCount < initialCapacity) {
/*  288 */       segmentCapacity++;
/*      */     }
/*      */     
/*  291 */     int segmentSize = 1;
/*  292 */     while (segmentSize < segmentCapacity) {
/*  293 */       segmentSize <<= 1;
/*      */     }
/*      */     
/*  296 */     if (evictsBySize())
/*      */     {
/*  298 */       long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
/*  299 */       long remainder = this.maxWeight % segmentCount;
/*  300 */       for (int i = 0; i < this.segments.length; i++) {
/*  301 */         if (i == remainder) {
/*  302 */           maxSegmentWeight -= 1L;
/*      */         }
/*  304 */         this.segments[i] = createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */     else {
/*  308 */       for (int i = 0; i < this.segments.length; i++) {
/*  309 */         this.segments[i] = createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   boolean evictsBySize()
/*      */   {
/*  316 */     return this.maxWeight >= 0L;
/*      */   }
/*      */   
/*      */   boolean customWeigher() {
/*  320 */     return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
/*      */   }
/*      */   
/*      */   boolean expires() {
/*  324 */     return (expiresAfterWrite()) || (expiresAfterAccess());
/*      */   }
/*      */   
/*      */   boolean expiresAfterWrite() {
/*  328 */     return this.expireAfterWriteNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean expiresAfterAccess() {
/*  332 */     return this.expireAfterAccessNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean refreshes() {
/*  336 */     return this.refreshNanos > 0L;
/*      */   }
/*      */   
/*      */   boolean usesAccessQueue() {
/*  340 */     return (expiresAfterAccess()) || (evictsBySize());
/*      */   }
/*      */   
/*      */   boolean usesWriteQueue() {
/*  344 */     return expiresAfterWrite();
/*      */   }
/*      */   
/*      */   boolean recordsWrite() {
/*  348 */     return (expiresAfterWrite()) || (refreshes());
/*      */   }
/*      */   
/*      */   boolean recordsAccess() {
/*  352 */     return expiresAfterAccess();
/*      */   }
/*      */   
/*      */   boolean recordsTime() {
/*  356 */     return (recordsWrite()) || (recordsAccess());
/*      */   }
/*      */   
/*      */   boolean usesWriteEntries() {
/*  360 */     return (usesWriteQueue()) || (recordsWrite());
/*      */   }
/*      */   
/*      */   boolean usesAccessEntries() {
/*  364 */     return (usesAccessQueue()) || (recordsAccess());
/*      */   }
/*      */   
/*      */   boolean usesKeyReferences() {
/*  368 */     return this.keyStrength != Strength.STRONG;
/*      */   }
/*      */   
/*      */   boolean usesValueReferences() {
/*  372 */     return this.valueStrength != Strength.STRONG;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static abstract enum Strength
/*      */   {
/*  381 */     STRONG, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  396 */     SOFT, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  412 */     WEAK;
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
/*  446 */     STRONG, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  453 */     STRONG_ACCESS, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  468 */     STRONG_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  483 */     STRONG_ACCESS_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  500 */     WEAK, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  507 */     WEAK_ACCESS, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  522 */     WEAK_WRITE, 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  537 */     WEAK_ACCESS_WRITE;
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
/*  564 */     static final EntryFactory[] factories = { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
/*      */     
/*      */ 
/*      */     private EntryFactory() {}
/*      */     
/*      */     static EntryFactory getFactory(Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue)
/*      */     {
/*  571 */       int flags = (keyStrength == Strength.WEAK ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
/*      */       
/*      */ 
/*  574 */       return factories[flags];
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
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */     {
/*  597 */       return newEntry(segment, original.getKey(), original.getHash(), newNext);
/*      */     }
/*      */     
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyAccessEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry)
/*      */     {
/*  604 */       newEntry.setAccessTime(original.getAccessTime());
/*      */       
/*  606 */       LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
/*  607 */       LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
/*      */       
/*  609 */       LocalCache.nullifyAccessOrder(original);
/*      */     }
/*      */     
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyWriteEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry)
/*      */     {
/*  616 */       newEntry.setWriteTime(original.getWriteTime());
/*      */       
/*  618 */       LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
/*  619 */       LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
/*      */       
/*  621 */       LocalCache.nullifyWriteOrder(original);
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
/*  690 */   static final ValueReference<Object, Object> UNSET = new ValueReference()
/*      */   {
/*      */     public Object get() {
/*  693 */       return null;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/*  698 */       return 0;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<Object, Object> getEntry()
/*      */     {
/*  703 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, @Nullable Object value, ReferenceEntry<Object, Object> entry)
/*      */     {
/*  709 */       return this;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/*  714 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/*  719 */       return false;
/*      */     }
/*      */     
/*      */     public Object waitForValue()
/*      */     {
/*  724 */       return null;
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
/*  736 */     return UNSET;
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
/*  855 */     INSTANCE;
/*      */     
/*      */     private NullEntry() {}
/*      */     
/*  859 */     public ValueReference<Object, Object> getValueReference() { return null; }
/*      */     
/*      */ 
/*      */     public void setValueReference(ValueReference<Object, Object> valueReference) {}
/*      */     
/*      */ 
/*      */     public ReferenceEntry<Object, Object> getNext()
/*      */     {
/*  867 */       return null;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/*  872 */       return 0;
/*      */     }
/*      */     
/*      */     public Object getKey()
/*      */     {
/*  877 */       return null;
/*      */     }
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/*  882 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setAccessTime(long time) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getNextInAccessQueue()
/*      */     {
/*  890 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setNextInAccessQueue(ReferenceEntry<Object, Object> next) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getPreviousInAccessQueue()
/*      */     {
/*  898 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<Object, Object> previous) {}
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/*  906 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setWriteTime(long time) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getNextInWriteQueue()
/*      */     {
/*  914 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setNextInWriteQueue(ReferenceEntry<Object, Object> next) {}
/*      */     
/*      */     public ReferenceEntry<Object, Object> getPreviousInWriteQueue()
/*      */     {
/*  922 */       return this;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<Object, Object> previous) {}
/*      */   }
/*      */   
/*      */   static abstract class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V>
/*      */   {
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/*  932 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/*  937 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/*  942 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/*  947 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/*  952 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/*  957 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/*  962 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/*  967 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/*  972 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/*  977 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/*  982 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/*  987 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/*  992 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/*  997 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1002 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1007 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1012 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */   
/*      */   static <K, V> ReferenceEntry<K, V> nullEntry()
/*      */   {
/* 1018 */     return NullEntry.INSTANCE;
/*      */   }
/*      */   
/* 1021 */   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue()
/*      */   {
/*      */     public boolean offer(Object o) {
/* 1024 */       return true;
/*      */     }
/*      */     
/*      */     public Object peek()
/*      */     {
/* 1029 */       return null;
/*      */     }
/*      */     
/*      */     public Object poll()
/*      */     {
/* 1034 */       return null;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 1039 */       return 0;
/*      */     }
/*      */     
/*      */     public Iterator<Object> iterator()
/*      */     {
/* 1044 */       return Iterators.emptyIterator();
/*      */     }
/*      */   };
/*      */   Set<K> keySet;
/*      */   Collection<V> values;
/*      */   Set<Entry<K, V>> entrySet;
/*      */   
/*      */   static <E> Queue<E> discardingQueue()
/*      */   {
/* 1053 */     return DISCARDING_QUEUE;
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
/* 1071 */       this.key = key;
/* 1072 */       this.hash = hash;
/* 1073 */       this.next = next;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 1078 */       return (K)this.key;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1085 */     volatile ValueReference<K, V> valueReference = LocalCache.unset();
/*      */     
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/* 1089 */       return this.valueReference;
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/* 1094 */       this.valueReference = valueReference;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/* 1099 */       return this.hash;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/* 1104 */       return this.next;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongAccessEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongAccessEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1110 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1115 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1119 */       return this.accessTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1124 */     public void setAccessTime(long time) { this.accessTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1127 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1132 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1137 */     public void setNextInAccessQueue(ReferenceEntry<K, V> next) { this.nextAccess = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1140 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1145 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1150 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongWriteEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1156 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1161 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1165 */       return this.writeTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1170 */     public void setWriteTime(long time) { this.writeTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1173 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1178 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1183 */     public void setNextInWriteQueue(ReferenceEntry<K, V> next) { this.nextWrite = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1186 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1191 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1196 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class StrongAccessWriteEntry<K, V> extends StrongEntry<K, V> {
/*      */     StrongAccessWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1202 */       super(hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1207 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1211 */       return this.accessTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1216 */     public void setAccessTime(long time) { this.accessTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1219 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1224 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1229 */     public void setNextInAccessQueue(ReferenceEntry<K, V> next) { this.nextAccess = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1232 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1237 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1242 */       this.previousAccess = previous;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1247 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1251 */       return this.writeTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1256 */     public void setWriteTime(long time) { this.writeTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1259 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1264 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1269 */     public void setNextInWriteQueue(ReferenceEntry<K, V> next) { this.nextWrite = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1272 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1277 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1282 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
/*      */     final int hash;
/*      */     final ReferenceEntry<K, V> next;
/*      */     
/*      */     WeakEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1291 */       super(queue);
/* 1292 */       this.hash = hash;
/* 1293 */       this.next = next;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 1298 */       return (K)get();
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
/* 1310 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1315 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1320 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1325 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1330 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1335 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1342 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1347 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1352 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */     {
/* 1357 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1362 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1367 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1374 */     volatile ValueReference<K, V> valueReference = LocalCache.unset();
/*      */     
/*      */     public ValueReference<K, V> getValueReference()
/*      */     {
/* 1378 */       return this.valueReference;
/*      */     }
/*      */     
/*      */     public void setValueReference(ValueReference<K, V> valueReference)
/*      */     {
/* 1383 */       this.valueReference = valueReference;
/*      */     }
/*      */     
/*      */     public int getHash()
/*      */     {
/* 1388 */       return this.hash;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getNext()
/*      */     {
/* 1393 */       return this.next;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakAccessEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakAccessEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1400 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1405 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1409 */       return this.accessTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1414 */     public void setAccessTime(long time) { this.accessTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1417 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1422 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1427 */     public void setNextInAccessQueue(ReferenceEntry<K, V> next) { this.nextAccess = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1430 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1435 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1440 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakWriteEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1447 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1452 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1456 */       return this.writeTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1461 */     public void setWriteTime(long time) { this.writeTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1464 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1469 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1474 */     public void setNextInWriteQueue(ReferenceEntry<K, V> next) { this.nextWrite = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1477 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1482 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1487 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */   
/*      */   static final class WeakAccessWriteEntry<K, V> extends WeakEntry<K, V>
/*      */   {
/*      */     WeakAccessWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 1494 */       super(key, hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1499 */     volatile long accessTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getAccessTime()
/*      */     {
/* 1503 */       return this.accessTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1508 */     public void setAccessTime(long time) { this.accessTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1511 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1516 */       return this.nextAccess;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1521 */     public void setNextInAccessQueue(ReferenceEntry<K, V> next) { this.nextAccess = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1524 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1529 */       return this.previousAccess;
/*      */     }
/*      */     
/*      */     public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1534 */       this.previousAccess = previous;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1539 */     volatile long writeTime = Long.MAX_VALUE;
/*      */     
/*      */     public long getWriteTime()
/*      */     {
/* 1543 */       return this.writeTime;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1548 */     public void setWriteTime(long time) { this.writeTime = time; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1551 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1556 */       return this.nextWrite;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1561 */     public void setNextInWriteQueue(ReferenceEntry<K, V> next) { this.nextWrite = next; }
/*      */     
/*      */     @GuardedBy("Segment.this")
/* 1564 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */     
/*      */ 
/*      */     public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1569 */       return this.previousWrite;
/*      */     }
/*      */     
/*      */     public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */     {
/* 1574 */       this.previousWrite = previous;
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
/* 1586 */       super(queue);
/* 1587 */       this.entry = entry;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1592 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1597 */       return this.entry;
/*      */     }
/*      */     
/*      */ 
/*      */     public void notifyNewValue(V newValue) {}
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1606 */       return new WeakValueReference(queue, value, entry);
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1611 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1616 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1621 */       return (V)get();
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
/* 1633 */       super(queue);
/* 1634 */       this.entry = entry;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1639 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1644 */       return this.entry;
/*      */     }
/*      */     
/*      */ 
/*      */     public void notifyNewValue(V newValue) {}
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1653 */       return new SoftValueReference(queue, value, entry);
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1658 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1663 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1668 */       return (V)get();
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
/* 1679 */       this.referent = referent;
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/* 1684 */       return (V)this.referent;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1689 */       return 1;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1694 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1700 */       return this;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 1705 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 1710 */       return true;
/*      */     }
/*      */     
/*      */     public V waitForValue()
/*      */     {
/* 1715 */       return (V)get();
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
/* 1730 */       super(referent, entry);
/* 1731 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1736 */       return this.weight;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1742 */       return new WeightedWeakValueReference(queue, value, entry, this.weight);
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
/* 1754 */       super(referent, entry);
/* 1755 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1760 */       return this.weight;
/*      */     }
/*      */     
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 1765 */       return new WeightedSoftValueReference(queue, value, entry, this.weight);
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
/* 1777 */       super();
/* 1778 */       this.weight = weight;
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 1783 */       return this.weight;
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
/* 1799 */     h += (h << 15 ^ 0xCD7D);
/* 1800 */     h ^= h >>> 10;
/* 1801 */     h += (h << 3);
/* 1802 */     h ^= h >>> 6;
/* 1803 */     h += (h << 2) + (h << 14);
/* 1804 */     return h ^ h >>> 16;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next)
/*      */   {
/* 1813 */     return segmentFor(hash).newEntry(key, hash, next);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */   {
/* 1822 */     int hash = original.getHash();
/* 1823 */     return segmentFor(hash).copyEntry(original, newNext);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight)
/*      */   {
/* 1832 */     int hash = entry.getHash();
/* 1833 */     return this.valueStrength.referenceValue(segmentFor(hash), entry, Preconditions.checkNotNull(value), weight);
/*      */   }
/*      */   
/*      */   int hash(@Nullable Object key) {
/* 1837 */     int h = this.keyEquivalence.hash(key);
/* 1838 */     return rehash(h);
/*      */   }
/*      */   
/*      */   void reclaimValue(ValueReference<K, V> valueReference) {
/* 1842 */     ReferenceEntry<K, V> entry = valueReference.getEntry();
/* 1843 */     int hash = entry.getHash();
/* 1844 */     segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
/*      */   }
/*      */   
/*      */   void reclaimKey(ReferenceEntry<K, V> entry) {
/* 1848 */     int hash = entry.getHash();
/* 1849 */     segmentFor(hash).reclaimKey(entry, hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @VisibleForTesting
/*      */   boolean isLive(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1858 */     return segmentFor(entry.getHash()).getLiveValue(entry, now) != null;
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
/* 1869 */     return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
/*      */   }
/*      */   
/*      */   Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */   {
/* 1874 */     return new Segment(this, initialCapacity, maxSegmentWeight, statsCounter);
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
/* 1885 */     if (entry.getKey() == null) {
/* 1886 */       return null;
/*      */     }
/* 1888 */     V value = entry.getValueReference().get();
/* 1889 */     if (value == null) {
/* 1890 */       return null;
/*      */     }
/*      */     
/* 1893 */     if (isExpired(entry, now)) {
/* 1894 */       return null;
/*      */     }
/* 1896 */     return value;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1905 */     Preconditions.checkNotNull(entry);
/* 1906 */     if ((expiresAfterAccess()) && (now - entry.getAccessTime() >= this.expireAfterAccessNanos))
/*      */     {
/* 1908 */       return true;
/*      */     }
/* 1910 */     if ((expiresAfterWrite()) && (now - entry.getWriteTime() >= this.expireAfterWriteNanos))
/*      */     {
/* 1912 */       return true;
/*      */     }
/* 1914 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 1921 */     previous.setNextInAccessQueue(next);
/* 1922 */     next.setPreviousInAccessQueue(previous);
/*      */   }
/*      */   
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled) {
/* 1927 */     ReferenceEntry<K, V> nullEntry = nullEntry();
/* 1928 */     nulled.setNextInAccessQueue(nullEntry);
/* 1929 */     nulled.setPreviousInAccessQueue(nullEntry);
/*      */   }
/*      */   
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
/* 1934 */     previous.setNextInWriteQueue(next);
/* 1935 */     next.setPreviousInWriteQueue(previous);
/*      */   }
/*      */   
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled) {
/* 1940 */     ReferenceEntry<K, V> nullEntry = nullEntry();
/* 1941 */     nulled.setNextInWriteQueue(nullEntry);
/* 1942 */     nulled.setPreviousInWriteQueue(nullEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void processPendingNotifications()
/*      */   {
/*      */     RemovalNotification<K, V> notification;
/*      */     
/*      */ 
/* 1952 */     while ((notification = (RemovalNotification)this.removalNotificationQueue.poll()) != null) {
/*      */       try {
/* 1954 */         this.removalListener.onRemoval(notification);
/*      */       } catch (Throwable e) {
/* 1956 */         logger.log(Level.WARNING, "Exception thrown by removal listener", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   final Segment<K, V>[] newSegmentArray(int ssize)
/*      */   {
/* 1963 */     return new Segment[ssize];
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
/*      */     @GuardedBy("Segment.this")
/*      */     int totalWeight;
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
/* 2068 */     final AtomicInteger readCount = new AtomicInteger();
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<ReferenceEntry<K, V>> writeQueue;
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
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
/* 2089 */       this.map = map;
/* 2090 */       this.maxSegmentWeight = maxSegmentWeight;
/* 2091 */       this.statsCounter = ((AbstractCache.StatsCounter)Preconditions.checkNotNull(statsCounter));
/* 2092 */       initTable(newEntryArray(initialCapacity));
/*      */       
/* 2094 */       this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue() : null);
/*      */       
/*      */ 
/* 2097 */       this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue() : null);
/*      */       
/*      */ 
/* 2100 */       this.recencyQueue = (map.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
/*      */       
/*      */ 
/*      */ 
/* 2104 */       this.writeQueue = (map.usesWriteQueue() ? new WriteQueue() : LocalCache.discardingQueue());
/*      */       
/*      */ 
/*      */ 
/* 2108 */       this.accessQueue = (map.usesAccessQueue() ? new AccessQueue() : LocalCache.discardingQueue());
/*      */     }
/*      */     
/*      */ 
/*      */     AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(int size)
/*      */     {
/* 2114 */       return new AtomicReferenceArray(size);
/*      */     }
/*      */     
/*      */     void initTable(AtomicReferenceArray<ReferenceEntry<K, V>> newTable) {
/* 2118 */       this.threshold = (newTable.length() * 3 / 4);
/* 2119 */       if ((!this.map.customWeigher()) && (this.threshold == this.maxSegmentWeight))
/*      */       {
/* 2121 */         this.threshold += 1;
/*      */       }
/* 2123 */       this.table = newTable;
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 2128 */       return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(key), hash, next);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */     {
/* 2137 */       if (original.getKey() == null)
/*      */       {
/* 2139 */         return null;
/*      */       }
/*      */       
/* 2142 */       ValueReference<K, V> valueReference = original.getValueReference();
/* 2143 */       V value = valueReference.get();
/* 2144 */       if ((value == null) && (valueReference.isActive()))
/*      */       {
/* 2146 */         return null;
/*      */       }
/*      */       
/* 2149 */       ReferenceEntry<K, V> newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
/* 2150 */       newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
/* 2151 */       return newEntry;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void setValue(ReferenceEntry<K, V> entry, K key, V value, long now)
/*      */     {
/* 2159 */       ValueReference<K, V> previous = entry.getValueReference();
/* 2160 */       int weight = this.map.weigher.weigh(key, value);
/* 2161 */       Preconditions.checkState(weight >= 0, "Weights must be non-negative");
/*      */       
/* 2163 */       ValueReference<K, V> valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
/*      */       
/* 2165 */       entry.setValueReference(valueReference);
/* 2166 */       recordWrite(entry, weight, now);
/* 2167 */       previous.notifyNewValue(value);
/*      */     }
/*      */     
/*      */     V get(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2173 */       Preconditions.checkNotNull(key);
/* 2174 */       Preconditions.checkNotNull(loader);
/*      */       try { ReferenceEntry<K, V> e;
/* 2176 */         if (this.count != 0)
/*      */         {
/* 2178 */           e = getEntry(key, hash);
/* 2179 */           if (e != null) {
/* 2180 */             long now = this.map.ticker.read();
/* 2181 */             V value = getLiveValue(e, now);
/* 2182 */             if (value != null) {
/* 2183 */               recordRead(e, now);
/* 2184 */               this.statsCounter.recordHits(1);
/* 2185 */               return (V)scheduleRefresh(e, key, hash, value, now, loader);
/*      */             }
/* 2187 */             Object valueReference = e.getValueReference();
/* 2188 */             if (((ValueReference)valueReference).isLoading()) {
/* 2189 */               return (V)waitForLoadingValue(e, key, (ValueReference)valueReference);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2195 */         return (V)lockedGetOrLoad(key, hash, loader);
/*      */       } catch (ExecutionException ee) {
/* 2197 */         Throwable cause = ee.getCause();
/* 2198 */         if ((cause instanceof Error))
/* 2199 */           throw new ExecutionError((Error)cause);
/* 2200 */         if ((cause instanceof RuntimeException)) {
/* 2201 */           throw new UncheckedExecutionException(cause);
/*      */         }
/* 2203 */         throw ee;
/*      */       } finally {
/* 2205 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     V lockedGetOrLoad(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2212 */       valueReference = null;
/* 2213 */       LoadingValueReference<K, V> loadingValueReference = null;
/* 2214 */       boolean createNewEntry = true;
/*      */       
/* 2216 */       lock();
/*      */       try
/*      */       {
/* 2219 */         long now = this.map.ticker.read();
/* 2220 */         preWriteCleanup(now);
/*      */         
/* 2222 */         int newCount = this.count - 1;
/* 2223 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2224 */         int index = hash & table.length() - 1;
/* 2225 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 2227 */         for (e = first; e != null; e = e.getNext()) {
/* 2228 */           K entryKey = e.getKey();
/* 2229 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2231 */             valueReference = e.getValueReference();
/* 2232 */             if (valueReference.isLoading()) {
/* 2233 */               createNewEntry = false; break;
/*      */             }
/* 2235 */             V value = valueReference.get();
/* 2236 */             if (value == null) {
/* 2237 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
/* 2238 */             } else if (this.map.isExpired(e, now))
/*      */             {
/*      */ 
/* 2241 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
/*      */             } else {
/* 2243 */               recordLockedRead(e, now);
/* 2244 */               this.statsCounter.recordHits(1);
/*      */               
/* 2246 */               return value;
/*      */             }
/*      */             
/*      */ 
/* 2250 */             this.writeQueue.remove(e);
/* 2251 */             this.accessQueue.remove(e);
/* 2252 */             this.count = newCount;
/*      */             
/* 2254 */             break;
/*      */           }
/*      */         }
/*      */         
/* 2258 */         if (createNewEntry) {
/* 2259 */           loadingValueReference = new LoadingValueReference();
/*      */           
/* 2261 */           if (e == null) {
/* 2262 */             e = newEntry(key, hash, first);
/* 2263 */             e.setValueReference(loadingValueReference);
/* 2264 */             table.set(index, e);
/*      */           } else {
/* 2266 */             e.setValueReference(loadingValueReference);
/*      */           }
/*      */         }
/*      */       } finally {
/* 2270 */         unlock();
/* 2271 */         postWriteCleanup();
/*      */       }
/*      */       
/* 2274 */       if (createNewEntry)
/*      */       {
/*      */         try
/*      */         {
/*      */ 
/* 2279 */           synchronized (e) {
/* 2280 */             return (V)loadSync(key, hash, loadingValueReference, loader);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2287 */           return (V)waitForLoadingValue(e, key, valueReference);
/*      */         }
/*      */         finally
/*      */         {
/* 2283 */           this.statsCounter.recordMisses(1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     V waitForLoadingValue(ReferenceEntry<K, V> e, K key, ValueReference<K, V> valueReference)
/*      */       throws ExecutionException
/*      */     {
/* 2293 */       if (!valueReference.isLoading()) {
/* 2294 */         throw new AssertionError();
/*      */       }
/*      */       
/* 2297 */       Preconditions.checkState(!Thread.holdsLock(e), "Recursive load of: %s", new Object[] { key });
/*      */       try
/*      */       {
/* 2300 */         V value = valueReference.waitForValue();
/* 2301 */         if (value == null) {
/* 2302 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/*      */         
/* 2305 */         long now = this.map.ticker.read();
/* 2306 */         recordRead(e, now);
/* 2307 */         return value;
/*      */       } finally {
/* 2309 */         this.statsCounter.recordMisses(1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     V loadSync(K key, int hash, LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2317 */       ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2318 */       return (V)getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */     }
/*      */     
/*      */     ListenableFuture<V> loadAsync(final K key, final int hash, final LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */     {
/* 2323 */       final ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2324 */       loadingFuture.addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/* 2329 */             newValue = Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */           } catch (Throwable t) { V newValue;
/* 2331 */             LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
/* 2332 */             loadingValueReference.setException(t); } } }, LocalCache.sameThreadExecutor);
/*      */       
/*      */ 
/*      */ 
/* 2336 */       return loadingFuture;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     V getAndRecordStats(K key, int hash, LoadingValueReference<K, V> loadingValueReference, ListenableFuture<V> newValue)
/*      */       throws ExecutionException
/*      */     {
/* 2344 */       V value = null;
/*      */       try {
/* 2346 */         value = Uninterruptibles.getUninterruptibly(newValue);
/* 2347 */         if (value == null) {
/* 2348 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/* 2350 */         this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
/* 2351 */         storeLoadedValue(key, hash, loadingValueReference, value);
/* 2352 */         return value;
/*      */       } finally {
/* 2354 */         if (value == null) {
/* 2355 */           this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
/* 2356 */           removeLoadingValue(key, hash, loadingValueReference);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     V scheduleRefresh(ReferenceEntry<K, V> entry, K key, int hash, V oldValue, long now, CacheLoader<? super K, V> loader)
/*      */     {
/* 2363 */       if ((this.map.refreshes()) && (now - entry.getWriteTime() > this.map.refreshNanos) && (!entry.getValueReference().isLoading()))
/*      */       {
/* 2365 */         V newValue = refresh(key, hash, loader, true);
/* 2366 */         if (newValue != null) {
/* 2367 */           return newValue;
/*      */         }
/*      */       }
/* 2370 */       return oldValue;
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
/* 2381 */       LoadingValueReference<K, V> loadingValueReference = insertLoadingValueReference(key, hash, checkTime);
/*      */       
/* 2383 */       if (loadingValueReference == null) {
/* 2384 */         return null;
/*      */       }
/*      */       
/* 2387 */       ListenableFuture<V> result = loadAsync(key, hash, loadingValueReference, loader);
/* 2388 */       if (result.isDone()) {
/*      */         try {
/* 2390 */           return (V)Uninterruptibles.getUninterruptibly(result);
/*      */         }
/*      */         catch (Throwable t) {}
/*      */       }
/*      */       
/* 2395 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K key, int hash, boolean checkTime)
/*      */     {
/* 2405 */       ReferenceEntry<K, V> e = null;
/* 2406 */       lock();
/*      */       try {
/* 2408 */         long now = this.map.ticker.read();
/* 2409 */         preWriteCleanup(now);
/*      */         
/* 2411 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2412 */         int index = hash & table.length() - 1;
/* 2413 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/*      */         ValueReference<K, V> valueReference;
/* 2416 */         for (e = first; e != null; e = e.getNext()) {
/* 2417 */           K entryKey = e.getKey();
/* 2418 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/*      */ 
/*      */ 
/* 2422 */             valueReference = e.getValueReference();
/* 2423 */             if ((valueReference.isLoading()) || ((checkTime) && (now - e.getWriteTime() < this.map.refreshNanos)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2428 */               return null;
/*      */             }
/*      */             
/*      */ 
/* 2432 */             this.modCount += 1;
/* 2433 */             Object loadingValueReference = new LoadingValueReference(valueReference);
/*      */             
/* 2435 */             e.setValueReference((ValueReference)loadingValueReference);
/* 2436 */             return (LoadingValueReference<K, V>)loadingValueReference;
/*      */           }
/*      */         }
/*      */         
/* 2440 */         this.modCount += 1;
/* 2441 */         LoadingValueReference<K, V> loadingValueReference = new LoadingValueReference();
/* 2442 */         e = newEntry(key, hash, first);
/* 2443 */         e.setValueReference(loadingValueReference);
/* 2444 */         table.set(index, e);
/* 2445 */         return loadingValueReference;
/*      */       } finally {
/* 2447 */         unlock();
/* 2448 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void tryDrainReferenceQueues()
/*      */     {
/* 2458 */       if (tryLock()) {
/*      */         try {
/* 2460 */           drainReferenceQueues();
/*      */         } finally {
/* 2462 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainReferenceQueues()
/*      */     {
/* 2473 */       if (this.map.usesKeyReferences()) {
/* 2474 */         drainKeyReferenceQueue();
/*      */       }
/* 2476 */       if (this.map.usesValueReferences()) {
/* 2477 */         drainValueReferenceQueue();
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     void drainKeyReferenceQueue()
/*      */     {
/* 2484 */       int i = 0;
/* 2485 */       Reference<? extends K> ref; for (; (ref = this.keyReferenceQueue.poll()) != null; 
/*      */           
/*      */ 
/*      */ 
/* 2489 */           i == 16)
/*      */       {
/* 2487 */         ReferenceEntry<K, V> entry = (ReferenceEntry)ref;
/* 2488 */         this.map.reclaimKey(entry);
/* 2489 */         i++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainValueReferenceQueue()
/*      */     {
/* 2498 */       int i = 0;
/* 2499 */       Reference<? extends V> ref; for (; (ref = this.valueReferenceQueue.poll()) != null; 
/*      */           
/*      */ 
/*      */ 
/* 2503 */           i == 16)
/*      */       {
/* 2501 */         ValueReference<K, V> valueReference = (ValueReference)ref;
/* 2502 */         this.map.reclaimValue(valueReference);
/* 2503 */         i++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     void clearReferenceQueues()
/*      */     {
/* 2513 */       if (this.map.usesKeyReferences()) {
/* 2514 */         clearKeyReferenceQueue();
/*      */       }
/* 2516 */       if (this.map.usesValueReferences()) {
/* 2517 */         clearValueReferenceQueue();
/*      */       }
/*      */     }
/*      */     
/*      */     void clearKeyReferenceQueue() {
/* 2522 */       while (this.keyReferenceQueue.poll() != null) {}
/*      */     }
/*      */     
/*      */     void clearValueReferenceQueue() {
/* 2526 */       while (this.valueReferenceQueue.poll() != null) {}
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
/* 2539 */       if (this.map.recordsAccess()) {
/* 2540 */         entry.setAccessTime(now);
/*      */       }
/* 2542 */       this.recencyQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordLockedRead(ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2554 */       if (this.map.recordsAccess()) {
/* 2555 */         entry.setAccessTime(now);
/*      */       }
/* 2557 */       this.accessQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordWrite(ReferenceEntry<K, V> entry, int weight, long now)
/*      */     {
/* 2567 */       drainRecencyQueue();
/* 2568 */       this.totalWeight += weight;
/*      */       
/* 2570 */       if (this.map.recordsAccess()) {
/* 2571 */         entry.setAccessTime(now);
/*      */       }
/* 2573 */       if (this.map.recordsWrite()) {
/* 2574 */         entry.setWriteTime(now);
/*      */       }
/* 2576 */       this.accessQueue.add(entry);
/* 2577 */       this.writeQueue.add(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainRecencyQueue()
/*      */     {
/*      */       ReferenceEntry<K, V> e;
/*      */       
/*      */ 
/* 2589 */       while ((e = (ReferenceEntry)this.recencyQueue.poll()) != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2594 */         if (this.accessQueue.contains(e)) {
/* 2595 */           this.accessQueue.add(e);
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
/* 2606 */       if (tryLock()) {
/*      */         try {
/* 2608 */           expireEntries(now);
/*      */         } finally {
/* 2610 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     void expireEntries(long now)
/*      */     {
/* 2618 */       drainRecencyQueue();
/*      */       
/*      */       ReferenceEntry<K, V> e;
/* 2621 */       while (((e = (ReferenceEntry)this.writeQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2622 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2623 */           throw new AssertionError();
/*      */         }
/*      */       }
/* 2626 */       while (((e = (ReferenceEntry)this.accessQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2627 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2628 */           throw new AssertionError();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(ReferenceEntry<K, V> entry, RemovalCause cause)
/*      */     {
/* 2637 */       enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(@Nullable K key, int hash, ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 2643 */       this.totalWeight -= valueReference.getWeight();
/* 2644 */       if (cause.wasEvicted()) {
/* 2645 */         this.statsCounter.recordEviction();
/*      */       }
/* 2647 */       if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
/* 2648 */         V value = valueReference.get();
/* 2649 */         RemovalNotification<K, V> notification = new RemovalNotification(key, value, cause);
/* 2650 */         this.map.removalNotificationQueue.offer(notification);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void evictEntries()
/*      */     {
/* 2660 */       if (!this.map.evictsBySize()) {
/* 2661 */         return;
/*      */       }
/*      */       
/* 2664 */       drainRecencyQueue();
/* 2665 */       while (this.totalWeight > this.maxSegmentWeight) {
/* 2666 */         ReferenceEntry<K, V> e = getNextEvictable();
/* 2667 */         if (!removeEntry(e, e.getHash(), RemovalCause.SIZE)) {
/* 2668 */           throw new AssertionError();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 2675 */       for (ReferenceEntry<K, V> e : this.accessQueue) {
/* 2676 */         int weight = e.getValueReference().getWeight();
/* 2677 */         if (weight > 0) {
/* 2678 */           return e;
/*      */         }
/*      */       }
/* 2681 */       throw new AssertionError();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     ReferenceEntry<K, V> getFirst(int hash)
/*      */     {
/* 2689 */       AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2690 */       return (ReferenceEntry)table.get(hash & table.length() - 1);
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getEntry(Object key, int hash)
/*      */     {
/* 2697 */       for (ReferenceEntry<K, V> e = getFirst(hash); e != null; e = e.getNext()) {
/* 2698 */         if (e.getHash() == hash)
/*      */         {
/*      */ 
/*      */ 
/* 2702 */           K entryKey = e.getKey();
/* 2703 */           if (entryKey == null) {
/* 2704 */             tryDrainReferenceQueues();
/*      */ 
/*      */ 
/*      */           }
/* 2708 */           else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
/* 2709 */             return e;
/*      */           }
/*      */         }
/*      */       }
/* 2713 */       return null;
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getLiveEntry(Object key, int hash, long now) {
/* 2718 */       ReferenceEntry<K, V> e = getEntry(key, hash);
/* 2719 */       if (e == null)
/* 2720 */         return null;
/* 2721 */       if (this.map.isExpired(e, now)) {
/* 2722 */         tryExpireEntries(now);
/* 2723 */         return null;
/*      */       }
/* 2725 */       return e;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     V getLiveValue(ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2733 */       if (entry.getKey() == null) {
/* 2734 */         tryDrainReferenceQueues();
/* 2735 */         return null;
/*      */       }
/* 2737 */       V value = entry.getValueReference().get();
/* 2738 */       if (value == null) {
/* 2739 */         tryDrainReferenceQueues();
/* 2740 */         return null;
/*      */       }
/*      */       
/* 2743 */       if (this.map.isExpired(entry, now)) {
/* 2744 */         tryExpireEntries(now);
/* 2745 */         return null;
/*      */       }
/* 2747 */       return value;
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V get(Object key, int hash) {
/*      */       try { long now;
/* 2753 */         if (this.count != 0) {
/* 2754 */           now = this.map.ticker.read();
/* 2755 */           ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
/* 2756 */           if (e == null) {
/* 2757 */             return null;
/*      */           }
/*      */           
/* 2760 */           Object value = e.getValueReference().get();
/* 2761 */           if (value != null) {
/* 2762 */             recordRead(e, now);
/* 2763 */             return (V)scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
/*      */           }
/* 2765 */           tryDrainReferenceQueues();
/*      */         }
/* 2767 */         return null;
/*      */       } finally {
/* 2769 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean containsKey(Object key, int hash) {
/*      */       try { long now;
/* 2775 */         if (this.count != 0) {
/* 2776 */           now = this.map.ticker.read();
/* 2777 */           ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
/* 2778 */           boolean bool; if (e == null) {
/* 2779 */             return false;
/*      */           }
/* 2781 */           return e.getValueReference().get() != null;
/*      */         }
/*      */         
/* 2784 */         return 0;
/*      */       } finally {
/* 2786 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     @VisibleForTesting
/*      */     boolean containsValue(Object value)
/*      */     {
/*      */       try
/*      */       {
/*      */         long now;
/* 2797 */         if (this.count != 0) {
/* 2798 */           now = this.map.ticker.read();
/* 2799 */           AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2800 */           int length = table.length();
/* 2801 */           for (int i = 0; i < length; i++) {
/* 2802 */             for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
/* 2803 */               V entryValue = getLiveValue(e, now);
/* 2804 */               if (entryValue != null)
/*      */               {
/*      */ 
/* 2807 */                 if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2808 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2814 */         return 0;
/*      */       } finally {
/* 2816 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V put(K key, int hash, V value, boolean onlyIfAbsent) {
/* 2822 */       lock();
/*      */       try {
/* 2824 */         long now = this.map.ticker.read();
/* 2825 */         preWriteCleanup(now);
/*      */         
/* 2827 */         int newCount = this.count + 1;
/* 2828 */         if (newCount > this.threshold) {
/* 2829 */           expand();
/* 2830 */           newCount = this.count + 1;
/*      */         }
/*      */         
/* 2833 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2834 */         int index = hash & table.length() - 1;
/* 2835 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/*      */         K entryKey;
/* 2838 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 2839 */           entryKey = e.getKey();
/* 2840 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/*      */ 
/*      */ 
/* 2844 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 2845 */             V entryValue = valueReference.get();
/*      */             V ?;
/* 2847 */             if (entryValue == null) {
/* 2848 */               this.modCount += 1;
/* 2849 */               if (valueReference.isActive()) {
/* 2850 */                 enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
/* 2851 */                 setValue(e, key, value, now);
/* 2852 */                 newCount = this.count;
/*      */               } else {
/* 2854 */                 setValue(e, key, value, now);
/* 2855 */                 newCount = this.count + 1;
/*      */               }
/* 2857 */               this.count = newCount;
/* 2858 */               evictEntries();
/* 2859 */               return null; }
/* 2860 */             if (onlyIfAbsent)
/*      */             {
/*      */ 
/*      */ 
/* 2864 */               recordLockedRead(e, now);
/* 2865 */               return entryValue;
/*      */             }
/*      */             
/* 2868 */             this.modCount += 1;
/* 2869 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2870 */             setValue(e, key, value, now);
/* 2871 */             evictEntries();
/* 2872 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2878 */         this.modCount += 1;
/* 2879 */         ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
/* 2880 */         setValue(newEntry, key, value, now);
/* 2881 */         table.set(index, newEntry);
/* 2882 */         newCount = this.count + 1;
/* 2883 */         this.count = newCount;
/* 2884 */         evictEntries();
/* 2885 */         return null;
/*      */       } finally {
/* 2887 */         unlock();
/* 2888 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expand()
/*      */     {
/* 2897 */       AtomicReferenceArray<ReferenceEntry<K, V>> oldTable = this.table;
/* 2898 */       int oldCapacity = oldTable.length();
/* 2899 */       if (oldCapacity >= 1073741824) {
/* 2900 */         return;
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
/* 2913 */       int newCount = this.count;
/* 2914 */       AtomicReferenceArray<ReferenceEntry<K, V>> newTable = newEntryArray(oldCapacity << 1);
/* 2915 */       this.threshold = (newTable.length() * 3 / 4);
/* 2916 */       int newMask = newTable.length() - 1;
/* 2917 */       for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++)
/*      */       {
/*      */ 
/* 2920 */         ReferenceEntry<K, V> head = (ReferenceEntry)oldTable.get(oldIndex);
/*      */         
/* 2922 */         if (head != null) {
/* 2923 */           ReferenceEntry<K, V> next = head.getNext();
/* 2924 */           int headIndex = head.getHash() & newMask;
/*      */           
/*      */ 
/* 2927 */           if (next == null) {
/* 2928 */             newTable.set(headIndex, head);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 2933 */             ReferenceEntry<K, V> tail = head;
/* 2934 */             int tailIndex = headIndex;
/* 2935 */             for (ReferenceEntry<K, V> e = next; e != null; e = e.getNext()) {
/* 2936 */               int newIndex = e.getHash() & newMask;
/* 2937 */               if (newIndex != tailIndex)
/*      */               {
/* 2939 */                 tailIndex = newIndex;
/* 2940 */                 tail = e;
/*      */               }
/*      */             }
/* 2943 */             newTable.set(tailIndex, tail);
/*      */             
/*      */ 
/* 2946 */             for (ReferenceEntry<K, V> e = head; e != tail; e = e.getNext()) {
/* 2947 */               int newIndex = e.getHash() & newMask;
/* 2948 */               ReferenceEntry<K, V> newNext = (ReferenceEntry)newTable.get(newIndex);
/* 2949 */               ReferenceEntry<K, V> newFirst = copyEntry(e, newNext);
/* 2950 */               if (newFirst != null) {
/* 2951 */                 newTable.set(newIndex, newFirst);
/*      */               } else {
/* 2953 */                 removeCollectedEntry(e);
/* 2954 */                 newCount--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2960 */       this.table = newTable;
/* 2961 */       this.count = newCount;
/*      */     }
/*      */     
/*      */     boolean replace(K key, int hash, V oldValue, V newValue) {
/* 2965 */       lock();
/*      */       try {
/* 2967 */         long now = this.map.ticker.read();
/* 2968 */         preWriteCleanup(now);
/*      */         
/* 2970 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 2971 */         int index = hash & table.length() - 1;
/* 2972 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 2974 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 2975 */           K entryKey = e.getKey();
/* 2976 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2978 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 2979 */             V entryValue = valueReference.get();
/* 2980 */             int newCount; if (entryValue == null) {
/* 2981 */               if (valueReference.isActive())
/*      */               {
/* 2983 */                 newCount = this.count - 1;
/* 2984 */                 this.modCount += 1;
/* 2985 */                 ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */                 
/* 2987 */                 newCount = this.count - 1;
/* 2988 */                 table.set(index, newFirst);
/* 2989 */                 this.count = newCount;
/*      */               }
/* 2991 */               return 0;
/*      */             }
/*      */             
/* 2994 */             if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
/* 2995 */               this.modCount += 1;
/* 2996 */               enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2997 */               setValue(e, key, newValue, now);
/* 2998 */               evictEntries();
/* 2999 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 3003 */             recordLockedRead(e, now);
/* 3004 */             return 0;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 3009 */         return 0;
/*      */       } finally {
/* 3011 */         unlock();
/* 3012 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V replace(K key, int hash, V newValue) {
/* 3018 */       lock();
/*      */       try {
/* 3020 */         long now = this.map.ticker.read();
/* 3021 */         preWriteCleanup(now);
/*      */         
/* 3023 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3024 */         int index = hash & table.length() - 1;
/* 3025 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3027 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3028 */           K entryKey = e.getKey();
/* 3029 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3031 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3032 */             V entryValue = valueReference.get();
/* 3033 */             int newCount; if (entryValue == null) {
/* 3034 */               if (valueReference.isActive())
/*      */               {
/* 3036 */                 newCount = this.count - 1;
/* 3037 */                 this.modCount += 1;
/* 3038 */                 ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */                 
/* 3040 */                 newCount = this.count - 1;
/* 3041 */                 table.set(index, newFirst);
/* 3042 */                 this.count = newCount;
/*      */               }
/* 3044 */               return null;
/*      */             }
/*      */             
/* 3047 */             this.modCount += 1;
/* 3048 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3049 */             setValue(e, key, newValue, now);
/* 3050 */             evictEntries();
/* 3051 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/* 3055 */         return null;
/*      */       } finally {
/* 3057 */         unlock();
/* 3058 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     V remove(Object key, int hash) {
/* 3064 */       lock();
/*      */       try {
/* 3066 */         long now = this.map.ticker.read();
/* 3067 */         preWriteCleanup(now);
/*      */         
/* 3069 */         int newCount = this.count - 1;
/* 3070 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3071 */         int index = hash & table.length() - 1;
/* 3072 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3074 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3075 */           K entryKey = e.getKey();
/* 3076 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3078 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3079 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3082 */             if (entryValue != null) {
/* 3083 */               cause = RemovalCause.EXPLICIT; } else { RemovalCause cause;
/* 3084 */               if (valueReference.isActive()) {
/* 3085 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3088 */                 return null;
/*      */             }
/*      */             RemovalCause cause;
/* 3091 */             this.modCount += 1;
/* 3092 */             Object newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */             
/* 3094 */             newCount = this.count - 1;
/* 3095 */             table.set(index, newFirst);
/* 3096 */             this.count = newCount;
/* 3097 */             return entryValue;
/*      */           }
/*      */         }
/*      */         
/* 3101 */         return null;
/*      */       } finally {
/* 3103 */         unlock();
/* 3104 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean storeLoadedValue(K key, int hash, LoadingValueReference<K, V> oldValueReference, V newValue)
/*      */     {
/* 3110 */       lock();
/*      */       try {
/* 3112 */         long now = this.map.ticker.read();
/* 3113 */         preWriteCleanup(now);
/*      */         
/* 3115 */         int newCount = this.count + 1;
/* 3116 */         if (newCount > this.threshold) {
/* 3117 */           expand();
/* 3118 */           newCount = this.count + 1;
/*      */         }
/*      */         
/* 3121 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3122 */         int index = hash & table.length() - 1;
/* 3123 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         K entryKey;
/* 3125 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3126 */           entryKey = e.getKey();
/* 3127 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3129 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3130 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3133 */             if ((oldValueReference == valueReference) || ((entryValue == null) && (valueReference != LocalCache.UNSET)))
/*      */             {
/* 3135 */               this.modCount += 1;
/* 3136 */               if (oldValueReference.isActive()) {
/* 3137 */                 cause = entryValue == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
/*      */                 
/* 3139 */                 enqueueNotification(key, hash, oldValueReference, cause);
/* 3140 */                 newCount--;
/*      */               }
/* 3142 */               setValue(e, key, newValue, now);
/* 3143 */               this.count = newCount;
/* 3144 */               evictEntries();
/* 3145 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 3149 */             valueReference = new WeightedStrongValueReference(newValue, 0);
/* 3150 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3151 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3155 */         this.modCount += 1;
/* 3156 */         ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
/* 3157 */         setValue(newEntry, key, newValue, now);
/* 3158 */         table.set(index, newEntry);
/* 3159 */         this.count = newCount;
/* 3160 */         evictEntries();
/* 3161 */         return 1;
/*      */       } finally {
/* 3163 */         unlock();
/* 3164 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     boolean remove(Object key, int hash, Object value) {
/* 3169 */       lock();
/*      */       try {
/* 3171 */         long now = this.map.ticker.read();
/* 3172 */         preWriteCleanup(now);
/*      */         
/* 3174 */         int newCount = this.count - 1;
/* 3175 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3176 */         int index = hash & table.length() - 1;
/* 3177 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3179 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3180 */           K entryKey = e.getKey();
/* 3181 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3183 */             ValueReference<K, V> valueReference = e.getValueReference();
/* 3184 */             V entryValue = valueReference.get();
/*      */             
/*      */             RemovalCause cause;
/* 3187 */             if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 3188 */               cause = RemovalCause.EXPLICIT; } else { RemovalCause cause;
/* 3189 */               if ((entryValue == null) && (valueReference.isActive())) {
/* 3190 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3193 */                 return false;
/*      */             }
/*      */             RemovalCause cause;
/* 3196 */             this.modCount += 1;
/* 3197 */             Object newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */             
/* 3199 */             newCount = this.count - 1;
/* 3200 */             table.set(index, newFirst);
/* 3201 */             this.count = newCount;
/* 3202 */             return cause == RemovalCause.EXPLICIT;
/*      */           }
/*      */         }
/*      */         
/* 3206 */         return 0;
/*      */       } finally {
/* 3208 */         unlock();
/* 3209 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     void clear() {
/* 3214 */       if (this.count != 0) {
/* 3215 */         lock();
/*      */         try {
/* 3217 */           AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3218 */           for (int i = 0; i < table.length(); i++) {
/* 3219 */             for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(i); e != null; e = e.getNext())
/*      */             {
/* 3221 */               if (e.getValueReference().isActive()) {
/* 3222 */                 enqueueNotification(e, RemovalCause.EXPLICIT);
/*      */               }
/*      */             }
/*      */           }
/* 3226 */           for (int i = 0; i < table.length(); i++) {
/* 3227 */             table.set(i, null);
/*      */           }
/* 3229 */           clearReferenceQueues();
/* 3230 */           this.writeQueue.clear();
/* 3231 */           this.accessQueue.clear();
/* 3232 */           this.readCount.set(0);
/*      */           
/* 3234 */           this.modCount += 1;
/* 3235 */           this.count = 0;
/*      */         } finally {
/* 3237 */           unlock();
/* 3238 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeValueFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry, @Nullable K key, int hash, ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 3248 */       enqueueNotification(key, hash, valueReference, cause);
/* 3249 */       this.writeQueue.remove(entry);
/* 3250 */       this.accessQueue.remove(entry);
/*      */       
/* 3252 */       if (valueReference.isLoading()) {
/* 3253 */         valueReference.notifyNewValue(null);
/* 3254 */         return first;
/*      */       }
/* 3256 */       return removeEntryFromChain(first, entry);
/*      */     }
/*      */     
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeEntryFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry)
/*      */     {
/* 3264 */       int newCount = this.count;
/* 3265 */       ReferenceEntry<K, V> newFirst = entry.getNext();
/* 3266 */       for (ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
/* 3267 */         ReferenceEntry<K, V> next = copyEntry(e, newFirst);
/* 3268 */         if (next != null) {
/* 3269 */           newFirst = next;
/*      */         } else {
/* 3271 */           removeCollectedEntry(e);
/* 3272 */           newCount--;
/*      */         }
/*      */       }
/* 3275 */       this.count = newCount;
/* 3276 */       return newFirst;
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     void removeCollectedEntry(ReferenceEntry<K, V> entry) {
/* 3281 */       enqueueNotification(entry, RemovalCause.COLLECTED);
/* 3282 */       this.writeQueue.remove(entry);
/* 3283 */       this.accessQueue.remove(entry);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean reclaimKey(ReferenceEntry<K, V> entry, int hash)
/*      */     {
/* 3290 */       lock();
/*      */       try {
/* 3292 */         int newCount = this.count - 1;
/* 3293 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3294 */         int index = hash & table.length() - 1;
/* 3295 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3297 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3298 */           if (e == entry) {
/* 3299 */             this.modCount += 1;
/* 3300 */             ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
/*      */             
/* 3302 */             newCount = this.count - 1;
/* 3303 */             table.set(index, newFirst);
/* 3304 */             this.count = newCount;
/* 3305 */             return true;
/*      */           }
/*      */         }
/*      */         
/* 3309 */         return 0;
/*      */       } finally {
/* 3311 */         unlock();
/* 3312 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean reclaimValue(K key, int hash, ValueReference<K, V> valueReference)
/*      */     {
/* 3320 */       lock();
/*      */       try {
/* 3322 */         int newCount = this.count - 1;
/* 3323 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3324 */         int index = hash & table.length() - 1;
/* 3325 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3327 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3328 */           K entryKey = e.getKey();
/* 3329 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3331 */             ValueReference<K, V> v = e.getValueReference();
/* 3332 */             ReferenceEntry<K, V> newFirst; if (v == valueReference) {
/* 3333 */               this.modCount += 1;
/* 3334 */               newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */               
/* 3336 */               newCount = this.count - 1;
/* 3337 */               table.set(index, newFirst);
/* 3338 */               this.count = newCount;
/* 3339 */               return true;
/*      */             }
/* 3341 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3345 */         return 0;
/*      */       } finally {
/* 3347 */         unlock();
/* 3348 */         if (!isHeldByCurrentThread()) {
/* 3349 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     boolean removeLoadingValue(K key, int hash, LoadingValueReference<K, V> valueReference) {
/* 3355 */       lock();
/*      */       try {
/* 3357 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3358 */         int index = hash & table.length() - 1;
/* 3359 */         ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */         
/* 3361 */         for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3362 */           K entryKey = e.getKey();
/* 3363 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3365 */             ValueReference<K, V> v = e.getValueReference();
/* 3366 */             ReferenceEntry<K, V> newFirst; if (v == valueReference) {
/* 3367 */               if (valueReference.isActive()) {
/* 3368 */                 e.setValueReference(valueReference.getOldValue());
/*      */               } else {
/* 3370 */                 newFirst = removeEntryFromChain(first, e);
/* 3371 */                 table.set(index, newFirst);
/*      */               }
/* 3373 */               return 1;
/*      */             }
/* 3375 */             return 0;
/*      */           }
/*      */         }
/*      */         
/* 3379 */         return 0;
/*      */       } finally {
/* 3381 */         unlock();
/* 3382 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     @GuardedBy("Segment.this")
/*      */     boolean removeEntry(ReferenceEntry<K, V> entry, int hash, RemovalCause cause) {
/* 3388 */       int newCount = this.count - 1;
/* 3389 */       AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 3390 */       int index = hash & table.length() - 1;
/* 3391 */       ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*      */       
/* 3393 */       for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
/* 3394 */         if (e == entry) {
/* 3395 */           this.modCount += 1;
/* 3396 */           ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
/*      */           
/* 3398 */           newCount = this.count - 1;
/* 3399 */           table.set(index, newFirst);
/* 3400 */           this.count = newCount;
/* 3401 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 3405 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     void postReadCleanup()
/*      */     {
/* 3413 */       if ((this.readCount.incrementAndGet() & 0x3F) == 0) {
/* 3414 */         cleanUp();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void preWriteCleanup(long now)
/*      */     {
/* 3426 */       runLockedCleanup(now);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     void postWriteCleanup()
/*      */     {
/* 3433 */       runUnlockedCleanup();
/*      */     }
/*      */     
/*      */     void cleanUp() {
/* 3437 */       long now = this.map.ticker.read();
/* 3438 */       runLockedCleanup(now);
/* 3439 */       runUnlockedCleanup();
/*      */     }
/*      */     
/*      */     void runLockedCleanup(long now) {
/* 3443 */       if (tryLock()) {
/*      */         try {
/* 3445 */           drainReferenceQueues();
/* 3446 */           expireEntries(now);
/* 3447 */           this.readCount.set(0);
/*      */         } finally {
/* 3449 */           unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     void runUnlockedCleanup()
/*      */     {
/* 3456 */       if (!isHeldByCurrentThread()) {
/* 3457 */         this.map.processPendingNotifications();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   static class LoadingValueReference<K, V>
/*      */     implements ValueReference<K, V>
/*      */   {
/*      */     volatile ValueReference<K, V> oldValue;
/* 3467 */     final SettableFuture<V> futureValue = SettableFuture.create();
/* 3468 */     final Stopwatch stopwatch = Stopwatch.createUnstarted();
/*      */     
/*      */     public LoadingValueReference() {
/* 3471 */       this(LocalCache.unset());
/*      */     }
/*      */     
/*      */     public LoadingValueReference(ValueReference<K, V> oldValue) {
/* 3475 */       this.oldValue = oldValue;
/*      */     }
/*      */     
/*      */     public boolean isLoading()
/*      */     {
/* 3480 */       return true;
/*      */     }
/*      */     
/*      */     public boolean isActive()
/*      */     {
/* 3485 */       return this.oldValue.isActive();
/*      */     }
/*      */     
/*      */     public int getWeight()
/*      */     {
/* 3490 */       return this.oldValue.getWeight();
/*      */     }
/*      */     
/*      */     public boolean set(@Nullable V newValue) {
/* 3494 */       return this.futureValue.set(newValue);
/*      */     }
/*      */     
/*      */     public boolean setException(Throwable t) {
/* 3498 */       return this.futureValue.setException(t);
/*      */     }
/*      */     
/*      */     private ListenableFuture<V> fullyFailedFuture(Throwable t) {
/* 3502 */       return Futures.immediateFailedFuture(t);
/*      */     }
/*      */     
/*      */     public void notifyNewValue(@Nullable V newValue)
/*      */     {
/* 3507 */       if (newValue != null)
/*      */       {
/*      */ 
/* 3510 */         set(newValue);
/*      */       }
/*      */       else {
/* 3513 */         this.oldValue = LocalCache.unset();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader)
/*      */     {
/* 3520 */       this.stopwatch.start();
/* 3521 */       V previousValue = this.oldValue.get();
/*      */       try {
/* 3523 */         if (previousValue == null) {
/* 3524 */           V newValue = loader.load(key);
/* 3525 */           return set(newValue) ? this.futureValue : Futures.immediateFuture(newValue);
/*      */         }
/* 3527 */         ListenableFuture<V> newValue = loader.reload(key, previousValue);
/* 3528 */         if (newValue == null) {
/* 3529 */           return Futures.immediateFuture(null);
/*      */         }
/*      */         
/*      */ 
/* 3533 */         Futures.transform(newValue, new Function()
/*      */         {
/*      */           public V apply(V newValue) {
/* 3536 */             LoadingValueReference.this.set(newValue);
/* 3537 */             return newValue;
/*      */           }
/*      */         });
/*      */       } catch (Throwable t) {
/* 3541 */         if ((t instanceof InterruptedException)) {
/* 3542 */           Thread.currentThread().interrupt();
/*      */         }
/* 3544 */         return setException(t) ? this.futureValue : fullyFailedFuture(t);
/*      */       }
/*      */     }
/*      */     
/*      */     public long elapsedNanos() {
/* 3549 */       return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
/*      */     }
/*      */     
/*      */     public V waitForValue() throws ExecutionException
/*      */     {
/* 3554 */       return (V)Uninterruptibles.getUninterruptibly(this.futureValue);
/*      */     }
/*      */     
/*      */     public V get()
/*      */     {
/* 3559 */       return (V)this.oldValue.get();
/*      */     }
/*      */     
/*      */     public ValueReference<K, V> getOldValue() {
/* 3563 */       return this.oldValue;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> getEntry()
/*      */     {
/* 3568 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, @Nullable V value, ReferenceEntry<K, V> entry)
/*      */     {
/* 3574 */       return this;
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
/* 3592 */     final ReferenceEntry<K, V> head = new AbstractReferenceEntry()
/*      */     {
/*      */       public long getWriteTime()
/*      */       {
/* 3596 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3602 */       ReferenceEntry<K, V> nextWrite = this;
/*      */       
/*      */       public void setWriteTime(long time) {}
/*      */       
/* 3606 */       public ReferenceEntry<K, V> getNextInWriteQueue() { return this.nextWrite; }
/*      */       
/*      */ 
/*      */       public void setNextInWriteQueue(ReferenceEntry<K, V> next)
/*      */       {
/* 3611 */         this.nextWrite = next;
/*      */       }
/*      */       
/* 3614 */       ReferenceEntry<K, V> previousWrite = this;
/*      */       
/*      */       public ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */       {
/* 3618 */         return this.previousWrite;
/*      */       }
/*      */       
/*      */       public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous)
/*      */       {
/* 3623 */         this.previousWrite = previous;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean offer(ReferenceEntry<K, V> entry)
/*      */     {
/* 3632 */       LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
/*      */       
/*      */ 
/* 3635 */       LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
/* 3636 */       LocalCache.connectWriteOrder(entry, this.head);
/*      */       
/* 3638 */       return true;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> peek()
/*      */     {
/* 3643 */       ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
/* 3644 */       return next == this.head ? null : next;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> poll()
/*      */     {
/* 3649 */       ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
/* 3650 */       if (next == this.head) {
/* 3651 */         return null;
/*      */       }
/*      */       
/* 3654 */       remove(next);
/* 3655 */       return next;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3661 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3662 */       ReferenceEntry<K, V> previous = e.getPreviousInWriteQueue();
/* 3663 */       ReferenceEntry<K, V> next = e.getNextInWriteQueue();
/* 3664 */       LocalCache.connectWriteOrder(previous, next);
/* 3665 */       LocalCache.nullifyWriteOrder(e);
/*      */       
/* 3667 */       return next != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3673 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3674 */       return e.getNextInWriteQueue() != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 3679 */       return this.head.getNextInWriteQueue() == this.head;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 3684 */       int size = 0;
/* 3685 */       for (ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head;
/* 3686 */           e = e.getNextInWriteQueue()) {
/* 3687 */         size++;
/*      */       }
/* 3689 */       return size;
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 3694 */       ReferenceEntry<K, V> e = this.head.getNextInWriteQueue();
/* 3695 */       while (e != this.head) {
/* 3696 */         ReferenceEntry<K, V> next = e.getNextInWriteQueue();
/* 3697 */         LocalCache.nullifyWriteOrder(e);
/* 3698 */         e = next;
/*      */       }
/*      */       
/* 3701 */       this.head.setNextInWriteQueue(this.head);
/* 3702 */       this.head.setPreviousInWriteQueue(this.head);
/*      */     }
/*      */     
/*      */     public Iterator<ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3707 */       new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
/* 3710 */           ReferenceEntry<K, V> next = previous.getNextInWriteQueue();
/* 3711 */           return next == WriteQueue.this.head ? null : next;
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
/* 3729 */     final ReferenceEntry<K, V> head = new AbstractReferenceEntry()
/*      */     {
/*      */       public long getAccessTime()
/*      */       {
/* 3733 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3739 */       ReferenceEntry<K, V> nextAccess = this;
/*      */       
/*      */       public void setAccessTime(long time) {}
/*      */       
/* 3743 */       public ReferenceEntry<K, V> getNextInAccessQueue() { return this.nextAccess; }
/*      */       
/*      */ 
/*      */       public void setNextInAccessQueue(ReferenceEntry<K, V> next)
/*      */       {
/* 3748 */         this.nextAccess = next;
/*      */       }
/*      */       
/* 3751 */       ReferenceEntry<K, V> previousAccess = this;
/*      */       
/*      */       public ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */       {
/* 3755 */         return this.previousAccess;
/*      */       }
/*      */       
/*      */       public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous)
/*      */       {
/* 3760 */         this.previousAccess = previous;
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean offer(ReferenceEntry<K, V> entry)
/*      */     {
/* 3769 */       LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
/*      */       
/*      */ 
/* 3772 */       LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
/* 3773 */       LocalCache.connectAccessOrder(entry, this.head);
/*      */       
/* 3775 */       return true;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> peek()
/*      */     {
/* 3780 */       ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
/* 3781 */       return next == this.head ? null : next;
/*      */     }
/*      */     
/*      */     public ReferenceEntry<K, V> poll()
/*      */     {
/* 3786 */       ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
/* 3787 */       if (next == this.head) {
/* 3788 */         return null;
/*      */       }
/*      */       
/* 3791 */       remove(next);
/* 3792 */       return next;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3798 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3799 */       ReferenceEntry<K, V> previous = e.getPreviousInAccessQueue();
/* 3800 */       ReferenceEntry<K, V> next = e.getNextInAccessQueue();
/* 3801 */       LocalCache.connectAccessOrder(previous, next);
/* 3802 */       LocalCache.nullifyAccessOrder(e);
/*      */       
/* 3804 */       return next != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3810 */       ReferenceEntry<K, V> e = (ReferenceEntry)o;
/* 3811 */       return e.getNextInAccessQueue() != NullEntry.INSTANCE;
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 3816 */       return this.head.getNextInAccessQueue() == this.head;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 3821 */       int size = 0;
/* 3822 */       for (ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head;
/* 3823 */           e = e.getNextInAccessQueue()) {
/* 3824 */         size++;
/*      */       }
/* 3826 */       return size;
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 3831 */       ReferenceEntry<K, V> e = this.head.getNextInAccessQueue();
/* 3832 */       while (e != this.head) {
/* 3833 */         ReferenceEntry<K, V> next = e.getNextInAccessQueue();
/* 3834 */         LocalCache.nullifyAccessOrder(e);
/* 3835 */         e = next;
/*      */       }
/*      */       
/* 3838 */       this.head.setNextInAccessQueue(this.head);
/* 3839 */       this.head.setPreviousInAccessQueue(this.head);
/*      */     }
/*      */     
/*      */     public Iterator<ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3844 */       new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
/* 3847 */           ReferenceEntry<K, V> next = previous.getNextInAccessQueue();
/* 3848 */           return next == AccessQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void cleanUp()
/*      */   {
/* 3857 */     for (Segment<?, ?> segment : this.segments) {
/* 3858 */       segment.cleanUp();
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
/* 3873 */     long sum = 0L;
/* 3874 */     Segment<K, V>[] segments = this.segments;
/* 3875 */     for (int i = 0; i < segments.length; i++) {
/* 3876 */       if (segments[i].count != 0) {
/* 3877 */         return false;
/*      */       }
/* 3879 */       sum += segments[i].modCount;
/*      */     }
/*      */     
/* 3882 */     if (sum != 0L) {
/* 3883 */       for (int i = 0; i < segments.length; i++) {
/* 3884 */         if (segments[i].count != 0) {
/* 3885 */           return false;
/*      */         }
/* 3887 */         sum -= segments[i].modCount;
/*      */       }
/* 3889 */       if (sum != 0L) {
/* 3890 */         return false;
/*      */       }
/*      */     }
/* 3893 */     return true;
/*      */   }
/*      */   
/*      */   long longSize() {
/* 3897 */     Segment<K, V>[] segments = this.segments;
/* 3898 */     long sum = 0L;
/* 3899 */     for (int i = 0; i < segments.length; i++) {
/* 3900 */       sum += segments[i].count;
/*      */     }
/* 3902 */     return sum;
/*      */   }
/*      */   
/*      */   public int size()
/*      */   {
/* 3907 */     return Ints.saturatedCast(longSize());
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   public V get(@Nullable Object key)
/*      */   {
/* 3913 */     if (key == null) {
/* 3914 */       return null;
/*      */     }
/* 3916 */     int hash = hash(key);
/* 3917 */     return (V)segmentFor(hash).get(key, hash);
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   public V getIfPresent(Object key) {
/* 3922 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3923 */     V value = segmentFor(hash).get(key, hash);
/* 3924 */     if (value == null) {
/* 3925 */       this.globalStatsCounter.recordMisses(1);
/*      */     } else {
/* 3927 */       this.globalStatsCounter.recordHits(1);
/*      */     }
/* 3929 */     return value;
/*      */   }
/*      */   
/*      */   V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 3933 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3934 */     return (V)segmentFor(hash).get(key, hash, loader);
/*      */   }
/*      */   
/*      */   V getOrLoad(K key) throws ExecutionException {
/* 3938 */     return (V)get(key, this.defaultLoader);
/*      */   }
/*      */   
/*      */   ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
/* 3942 */     int hits = 0;
/* 3943 */     int misses = 0;
/*      */     
/* 3945 */     Map<K, V> result = Maps.newLinkedHashMap();
/* 3946 */     for (Object key : keys) {
/* 3947 */       V value = get(key);
/* 3948 */       if (value == null) {
/* 3949 */         misses++;
/*      */       }
/*      */       else
/*      */       {
/* 3953 */         K castKey = (K)key;
/* 3954 */         result.put(castKey, value);
/* 3955 */         hits++;
/*      */       }
/*      */     }
/* 3958 */     this.globalStatsCounter.recordHits(hits);
/* 3959 */     this.globalStatsCounter.recordMisses(misses);
/* 3960 */     return ImmutableMap.copyOf(result);
/*      */   }
/*      */   
/*      */   ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
/* 3964 */     int hits = 0;
/* 3965 */     int misses = 0;
/*      */     
/* 3967 */     Map<K, V> result = Maps.newLinkedHashMap();
/* 3968 */     Set<K> keysToLoad = Sets.newLinkedHashSet();
/* 3969 */     for (K key : keys) {
/* 3970 */       V value = get(key);
/* 3971 */       if (!result.containsKey(key)) {
/* 3972 */         result.put(key, value);
/* 3973 */         if (value == null) {
/* 3974 */           misses++;
/* 3975 */           keysToLoad.add(key);
/*      */         } else {
/* 3977 */           hits++;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3983 */       if (!keysToLoad.isEmpty()) {
/*      */         Iterator i$;
/* 3985 */         try { newEntries = loadAll(keysToLoad, this.defaultLoader);
/* 3986 */           for (K key : keysToLoad) {
/* 3987 */             V value = newEntries.get(key);
/* 3988 */             if (value == null) {
/* 3989 */               throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + key);
/*      */             }
/* 3991 */             result.put(key, value);
/*      */           }
/*      */         } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/*      */           Map<K, V> newEntries;
/* 3995 */           i$ = keysToLoad.iterator(); } while (i$.hasNext()) { K key = i$.next();
/* 3996 */           misses--;
/* 3997 */           result.put(key, get(key, this.defaultLoader));
/*      */         }
/*      */       }
/*      */       
/* 4001 */       return ImmutableMap.copyOf(result);
/*      */     } finally {
/* 4003 */       this.globalStatsCounter.recordHits(hits);
/* 4004 */       this.globalStatsCounter.recordMisses(misses);
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
/* 4015 */     Preconditions.checkNotNull(loader);
/* 4016 */     Preconditions.checkNotNull(keys);
/* 4017 */     Stopwatch stopwatch = Stopwatch.createStarted();
/*      */     
/* 4019 */     boolean success = false;
/*      */     Map<K, V> result;
/*      */     try {
/* 4022 */       Map<K, V> map = loader.loadAll(keys);
/* 4023 */       result = map;
/* 4024 */       success = true;
/*      */     } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/* 4026 */       success = true;
/* 4027 */       throw e;
/*      */     } catch (InterruptedException e) {
/* 4029 */       Thread.currentThread().interrupt();
/* 4030 */       throw new ExecutionException(e);
/*      */     } catch (RuntimeException e) {
/* 4032 */       throw new UncheckedExecutionException(e);
/*      */     } catch (Exception e) {
/* 4034 */       throw new ExecutionException(e);
/*      */     } catch (Error e) {
/* 4036 */       throw new ExecutionError(e);
/*      */     } finally {
/* 4038 */       if (!success) {
/* 4039 */         this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/*      */       }
/*      */     }
/*      */     
/* 4043 */     if (result == null) {
/* 4044 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4045 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null map from loadAll");
/*      */     }
/*      */     
/* 4048 */     stopwatch.stop();
/*      */     
/* 4050 */     boolean nullsPresent = false;
/* 4051 */     for (Entry<K, V> entry : result.entrySet()) {
/* 4052 */       K key = entry.getKey();
/* 4053 */       V value = entry.getValue();
/* 4054 */       if ((key == null) || (value == null))
/*      */       {
/* 4056 */         nullsPresent = true;
/*      */       } else {
/* 4058 */         put(key, value);
/*      */       }
/*      */     }
/*      */     
/* 4062 */     if (nullsPresent) {
/* 4063 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4064 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null keys or values from loadAll");
/*      */     }
/*      */     
/*      */ 
/* 4068 */     this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
/* 4069 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   ReferenceEntry<K, V> getEntry(@Nullable Object key)
/*      */   {
/* 4078 */     if (key == null) {
/* 4079 */       return null;
/*      */     }
/* 4081 */     int hash = hash(key);
/* 4082 */     return segmentFor(hash).getEntry(key, hash);
/*      */   }
/*      */   
/*      */   void refresh(K key) {
/* 4086 */     int hash = hash(Preconditions.checkNotNull(key));
/* 4087 */     segmentFor(hash).refresh(key, hash, this.defaultLoader, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/* 4093 */     if (key == null) {
/* 4094 */       return false;
/*      */     }
/* 4096 */     int hash = hash(key);
/* 4097 */     return segmentFor(hash).containsKey(key, hash);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/* 4103 */     if (value == null) {
/* 4104 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4112 */     long now = this.ticker.read();
/* 4113 */     Segment<K, V>[] segments = this.segments;
/* 4114 */     long last = -1L;
/* 4115 */     for (int i = 0; i < 3; i++) {
/* 4116 */       long sum = 0L;
/* 4117 */       for (Segment<K, V> segment : segments)
/*      */       {
/*      */ 
/* 4120 */         int c = segment.count;
/*      */         
/* 4122 */         AtomicReferenceArray<ReferenceEntry<K, V>> table = segment.table;
/* 4123 */         for (int j = 0; j < table.length(); j++) {
/* 4124 */           for (ReferenceEntry<K, V> e = (ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
/* 4125 */             V v = segment.getLiveValue(e, now);
/* 4126 */             if ((v != null) && (this.valueEquivalence.equivalent(value, v))) {
/* 4127 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 4131 */         sum += segment.modCount;
/*      */       }
/* 4133 */       if (sum == last) {
/*      */         break;
/*      */       }
/* 4136 */       last = sum;
/*      */     }
/* 4138 */     return false;
/*      */   }
/*      */   
/*      */   public V put(K key, V value)
/*      */   {
/* 4143 */     Preconditions.checkNotNull(key);
/* 4144 */     Preconditions.checkNotNull(value);
/* 4145 */     int hash = hash(key);
/* 4146 */     return (V)segmentFor(hash).put(key, hash, value, false);
/*      */   }
/*      */   
/*      */   public V putIfAbsent(K key, V value)
/*      */   {
/* 4151 */     Preconditions.checkNotNull(key);
/* 4152 */     Preconditions.checkNotNull(value);
/* 4153 */     int hash = hash(key);
/* 4154 */     return (V)segmentFor(hash).put(key, hash, value, true);
/*      */   }
/*      */   
/*      */   public void putAll(Map<? extends K, ? extends V> m)
/*      */   {
/* 4159 */     for (Entry<? extends K, ? extends V> e : m.entrySet()) {
/* 4160 */       put(e.getKey(), e.getValue());
/*      */     }
/*      */   }
/*      */   
/*      */   public V remove(@Nullable Object key)
/*      */   {
/* 4166 */     if (key == null) {
/* 4167 */       return null;
/*      */     }
/* 4169 */     int hash = hash(key);
/* 4170 */     return (V)segmentFor(hash).remove(key, hash);
/*      */   }
/*      */   
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/* 4175 */     if ((key == null) || (value == null)) {
/* 4176 */       return false;
/*      */     }
/* 4178 */     int hash = hash(key);
/* 4179 */     return segmentFor(hash).remove(key, hash, value);
/*      */   }
/*      */   
/*      */   public boolean replace(K key, @Nullable V oldValue, V newValue)
/*      */   {
/* 4184 */     Preconditions.checkNotNull(key);
/* 4185 */     Preconditions.checkNotNull(newValue);
/* 4186 */     if (oldValue == null) {
/* 4187 */       return false;
/*      */     }
/* 4189 */     int hash = hash(key);
/* 4190 */     return segmentFor(hash).replace(key, hash, oldValue, newValue);
/*      */   }
/*      */   
/*      */   public V replace(K key, V value)
/*      */   {
/* 4195 */     Preconditions.checkNotNull(key);
/* 4196 */     Preconditions.checkNotNull(value);
/* 4197 */     int hash = hash(key);
/* 4198 */     return (V)segmentFor(hash).replace(key, hash, value);
/*      */   }
/*      */   
/*      */   public void clear()
/*      */   {
/* 4203 */     for (Segment<K, V> segment : this.segments) {
/* 4204 */       segment.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   void invalidateAll(Iterable<?> keys)
/*      */   {
/* 4210 */     for (Object key : keys) {
/* 4211 */       remove(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/* 4220 */     Set<K> ks = this.keySet;
/* 4221 */     return ks != null ? ks : (this.keySet = new KeySet(this));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 4229 */     Collection<V> vs = this.values;
/* 4230 */     return vs != null ? vs : (this.values = new Values(this));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @GwtIncompatible("Not supported.")
/*      */   public Set<Entry<K, V>> entrySet()
/*      */   {
/* 4239 */     Set<Entry<K, V>> es = this.entrySet;
/* 4240 */     return es != null ? es : (this.entrySet = new EntrySet(this));
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
/* 4256 */       this.nextSegmentIndex = (LocalCache.this.segments.length - 1);
/* 4257 */       this.nextTableIndex = -1;
/* 4258 */       advance();
/*      */     }
/*      */     
/*      */     public abstract T next();
/*      */     
/*      */     final void advance()
/*      */     {
/* 4265 */       this.nextExternal = null;
/*      */       
/* 4267 */       if (nextInChain()) {
/* 4268 */         return;
/*      */       }
/*      */       
/* 4271 */       if (nextInTable()) {
/* 4272 */         return;
/*      */       }
/*      */       
/* 4275 */       while (this.nextSegmentIndex >= 0) {
/* 4276 */         this.currentSegment = LocalCache.this.segments[(this.nextSegmentIndex--)];
/* 4277 */         if (this.currentSegment.count != 0) {
/* 4278 */           this.currentTable = this.currentSegment.table;
/* 4279 */           this.nextTableIndex = (this.currentTable.length() - 1);
/* 4280 */           if (nextInTable()) {}
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
/* 4291 */       if (this.nextEntry != null) {
/* 4292 */         for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
/* 4293 */           if (advanceTo(this.nextEntry)) {
/* 4294 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 4298 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean nextInTable()
/*      */     {
/* 4305 */       while (this.nextTableIndex >= 0) {
/* 4306 */         if (((this.nextEntry = (ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && (
/* 4307 */           (advanceTo(this.nextEntry)) || (nextInChain()))) {
/* 4308 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 4312 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     boolean advanceTo(ReferenceEntry<K, V> entry)
/*      */     {
/*      */       try
/*      */       {
/* 4321 */         long now = LocalCache.this.ticker.read();
/* 4322 */         K key = entry.getKey();
/* 4323 */         V value = LocalCache.this.getLiveValue(entry, now);
/* 4324 */         boolean bool; if (value != null) {
/* 4325 */           this.nextExternal = new WriteThroughEntry(LocalCache.this, key, value);
/* 4326 */           return true;
/*      */         }
/*      */         
/* 4329 */         return false;
/*      */       }
/*      */       finally {
/* 4332 */         this.currentSegment.postReadCleanup();
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean hasNext()
/*      */     {
/* 4338 */       return this.nextExternal != null;
/*      */     }
/*      */     
/*      */     WriteThroughEntry nextEntry() {
/* 4342 */       if (this.nextExternal == null) {
/* 4343 */         throw new NoSuchElementException();
/*      */       }
/* 4345 */       this.lastReturned = this.nextExternal;
/* 4346 */       advance();
/* 4347 */       return this.lastReturned;
/*      */     }
/*      */     
/*      */     public void remove()
/*      */     {
/* 4352 */       Preconditions.checkState(this.lastReturned != null);
/* 4353 */       LocalCache.this.remove(this.lastReturned.getKey());
/* 4354 */       this.lastReturned = null;
/*      */     }
/*      */   }
/*      */   
/* 4358 */   final class KeyIterator extends HashIterator<K> { KeyIterator() { super(); }
/*      */     
/*      */ 
/*      */ 
/* 4362 */     public K next() { return (K)nextEntry().getKey(); }
/*      */   }
/*      */   
/*      */   final class ValueIterator extends HashIterator<V> {
/* 4366 */     ValueIterator() { super(); }
/*      */     
/*      */     public V next()
/*      */     {
/* 4370 */       return (V)nextEntry().getValue();
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
/* 4383 */       this.key = key;
/* 4384 */       this.value = value;
/*      */     }
/*      */     
/*      */     public K getKey()
/*      */     {
/* 4389 */       return (K)this.key;
/*      */     }
/*      */     
/*      */     public V getValue()
/*      */     {
/* 4394 */       return (V)this.value;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 4400 */       if ((object instanceof Map.Entry)) {
/* 4401 */         Entry<?, ?> that = (Entry)object;
/* 4402 */         return (this.key.equals(that.getKey())) && (this.value.equals(that.getValue()));
/*      */       }
/* 4404 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4410 */       return this.key.hashCode() ^ this.value.hashCode();
/*      */     }
/*      */     
/*      */     public V setValue(V newValue)
/*      */     {
/* 4415 */       throw new UnsupportedOperationException();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4422 */     public String toString() { return getKey() + "=" + getValue(); }
/*      */   }
/*      */   
/*      */   final class EntryIterator extends HashIterator<Entry<K, V>> {
/* 4426 */     EntryIterator() { super(); }
/*      */     
/*      */     public Entry<K, V> next()
/*      */     {
/* 4430 */       return nextEntry();
/*      */     }
/*      */   }
/*      */   
/*      */   abstract class AbstractCacheSet<T> extends AbstractSet<T> {
/*      */     final ConcurrentMap<?, ?> map;
/*      */     
/*      */     AbstractCacheSet() {
/* 4438 */       this.map = map;
/*      */     }
/*      */     
/*      */     public int size()
/*      */     {
/* 4443 */       return this.map.size();
/*      */     }
/*      */     
/*      */     public boolean isEmpty()
/*      */     {
/* 4448 */       return this.map.isEmpty();
/*      */     }
/*      */     
/*      */     public void clear()
/*      */     {
/* 4453 */       this.map.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   final class KeySet extends AbstractCacheSet<K>
/*      */   {
/*      */     KeySet() {
/* 4460 */       super(map);
/*      */     }
/*      */     
/*      */     public Iterator<K> iterator()
/*      */     {
/* 4465 */       return new KeyIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4470 */       return this.map.containsKey(o);
/*      */     }
/*      */     
/*      */     public boolean remove(Object o)
/*      */     {
/* 4475 */       return this.map.remove(o) != null;
/*      */     }
/*      */   }
/*      */   
/*      */   final class Values extends AbstractCollection<V> {
/*      */     private final ConcurrentMap<?, ?> map;
/*      */     
/*      */     Values() {
/* 4483 */       this.map = map;
/*      */     }
/*      */     
/*      */     public int size() {
/* 4487 */       return this.map.size();
/*      */     }
/*      */     
/*      */     public boolean isEmpty() {
/* 4491 */       return this.map.isEmpty();
/*      */     }
/*      */     
/*      */     public void clear() {
/* 4495 */       this.map.clear();
/*      */     }
/*      */     
/*      */     public Iterator<V> iterator()
/*      */     {
/* 4500 */       return new ValueIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4505 */       return this.map.containsValue(o);
/*      */     }
/*      */   }
/*      */   
/*      */   final class EntrySet extends AbstractCacheSet<Entry<K, V>>
/*      */   {
/*      */     EntrySet() {
/* 4512 */       super(map);
/*      */     }
/*      */     
/*      */     public Iterator<Entry<K, V>> iterator()
/*      */     {
/* 4517 */       return new EntryIterator(LocalCache.this);
/*      */     }
/*      */     
/*      */     public boolean contains(Object o)
/*      */     {
/* 4522 */       if (!(o instanceof Map.Entry)) {
/* 4523 */         return false;
/*      */       }
/* 4525 */       Entry<?, ?> e = (Entry)o;
/* 4526 */       Object key = e.getKey();
/* 4527 */       if (key == null) {
/* 4528 */         return false;
/*      */       }
/* 4530 */       V v = LocalCache.this.get(key);
/*      */       
/* 4532 */       return (v != null) && (LocalCache.this.valueEquivalence.equivalent(e.getValue(), v));
/*      */     }
/*      */     
/*      */     public boolean remove(Object o)
/*      */     {
/* 4537 */       if (!(o instanceof Map.Entry)) {
/* 4538 */         return false;
/*      */       }
/* 4540 */       Entry<?, ?> e = (Entry)o;
/* 4541 */       Object key = e.getKey();
/* 4542 */       return (key != null) && (LocalCache.this.remove(key, e.getValue()));
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
/* 4576 */       this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
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
/* 4598 */       this.keyStrength = keyStrength;
/* 4599 */       this.valueStrength = valueStrength;
/* 4600 */       this.keyEquivalence = keyEquivalence;
/* 4601 */       this.valueEquivalence = valueEquivalence;
/* 4602 */       this.expireAfterWriteNanos = expireAfterWriteNanos;
/* 4603 */       this.expireAfterAccessNanos = expireAfterAccessNanos;
/* 4604 */       this.maxWeight = maxWeight;
/* 4605 */       this.weigher = weigher;
/* 4606 */       this.concurrencyLevel = concurrencyLevel;
/* 4607 */       this.removalListener = removalListener;
/* 4608 */       this.ticker = ((ticker == Ticker.systemTicker()) || (ticker == CacheBuilder.NULL_TICKER) ? null : ticker);
/*      */       
/* 4610 */       this.loader = loader;
/*      */     }
/*      */     
/*      */     CacheBuilder<K, V> recreateCacheBuilder() {
/* 4614 */       CacheBuilder<K, V> builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4621 */       builder.strictParsing = false;
/* 4622 */       if (this.expireAfterWriteNanos > 0L) {
/* 4623 */         builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4625 */       if (this.expireAfterAccessNanos > 0L) {
/* 4626 */         builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4628 */       if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
/* 4629 */         builder.weigher(this.weigher);
/* 4630 */         if (this.maxWeight != -1L) {
/* 4631 */           builder.maximumWeight(this.maxWeight);
/*      */         }
/*      */       }
/* 4634 */       else if (this.maxWeight != -1L) {
/* 4635 */         builder.maximumSize(this.maxWeight);
/*      */       }
/*      */       
/* 4638 */       if (this.ticker != null) {
/* 4639 */         builder.ticker(this.ticker);
/*      */       }
/* 4641 */       return builder;
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4645 */       in.defaultReadObject();
/* 4646 */       CacheBuilder<K, V> builder = recreateCacheBuilder();
/* 4647 */       this.delegate = builder.build();
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/* 4651 */       return this.delegate;
/*      */     }
/*      */     
/*      */     protected Cache<K, V> delegate()
/*      */     {
/* 4656 */       return this.delegate;
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
/* 4675 */       super();
/*      */     }
/*      */     
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4679 */       in.defaultReadObject();
/* 4680 */       CacheBuilder<K, V> builder = recreateCacheBuilder();
/* 4681 */       this.autoDelegate = builder.build(this.loader);
/*      */     }
/*      */     
/*      */     public V get(K key) throws ExecutionException
/*      */     {
/* 4686 */       return (V)this.autoDelegate.get(key);
/*      */     }
/*      */     
/*      */     public V getUnchecked(K key)
/*      */     {
/* 4691 */       return (V)this.autoDelegate.getUnchecked(key);
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4696 */       return this.autoDelegate.getAll(keys);
/*      */     }
/*      */     
/*      */     public final V apply(K key)
/*      */     {
/* 4701 */       return (V)this.autoDelegate.apply(key);
/*      */     }
/*      */     
/*      */     public void refresh(K key)
/*      */     {
/* 4706 */       this.autoDelegate.refresh(key);
/*      */     }
/*      */     
/*      */     private Object readResolve() {
/* 4710 */       return this.autoDelegate;
/*      */     }
/*      */   }
/*      */   
/*      */   static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {
/*      */     final LocalCache<K, V> localCache;
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/* 4718 */     LocalManualCache(CacheBuilder<? super K, ? super V> builder) { this(new LocalCache(builder, null)); }
/*      */     
/*      */     private LocalManualCache(LocalCache<K, V> localCache)
/*      */     {
/* 4722 */       this.localCache = localCache;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     @Nullable
/*      */     public V getIfPresent(Object key)
/*      */     {
/* 4730 */       return (V)this.localCache.getIfPresent(key);
/*      */     }
/*      */     
/*      */     public V get(K key, final Callable<? extends V> valueLoader) throws ExecutionException
/*      */     {
/* 4735 */       Preconditions.checkNotNull(valueLoader);
/* 4736 */       (V)this.localCache.get(key, new CacheLoader()
/*      */       {
/*      */         public V load(Object key) throws Exception {
/* 4739 */           return (V)valueLoader.call();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*      */     {
/* 4746 */       return this.localCache.getAllPresent(keys);
/*      */     }
/*      */     
/*      */     public void put(K key, V value)
/*      */     {
/* 4751 */       this.localCache.put(key, value);
/*      */     }
/*      */     
/*      */     public void putAll(Map<? extends K, ? extends V> m)
/*      */     {
/* 4756 */       this.localCache.putAll(m);
/*      */     }
/*      */     
/*      */     public void invalidate(Object key)
/*      */     {
/* 4761 */       Preconditions.checkNotNull(key);
/* 4762 */       this.localCache.remove(key);
/*      */     }
/*      */     
/*      */     public void invalidateAll(Iterable<?> keys)
/*      */     {
/* 4767 */       this.localCache.invalidateAll(keys);
/*      */     }
/*      */     
/*      */     public void invalidateAll()
/*      */     {
/* 4772 */       this.localCache.clear();
/*      */     }
/*      */     
/*      */     public long size()
/*      */     {
/* 4777 */       return this.localCache.longSize();
/*      */     }
/*      */     
/*      */     public ConcurrentMap<K, V> asMap()
/*      */     {
/* 4782 */       return this.localCache;
/*      */     }
/*      */     
/*      */     public CacheStats stats()
/*      */     {
/* 4787 */       AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
/* 4788 */       aggregator.incrementBy(this.localCache.globalStatsCounter);
/* 4789 */       for (Segment<K, V> segment : this.localCache.segments) {
/* 4790 */         aggregator.incrementBy(segment.statsCounter);
/*      */       }
/* 4792 */       return aggregator.snapshot();
/*      */     }
/*      */     
/*      */     public void cleanUp()
/*      */     {
/* 4797 */       this.localCache.cleanUp();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4805 */       return new ManualSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */   
/*      */   static class LocalLoadingCache<K, V> extends LocalManualCache<K, V> implements LoadingCache<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     LocalLoadingCache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader) {
/* 4814 */       super(null);
/*      */     }
/*      */     
/*      */ 
/*      */     public V get(K key)
/*      */       throws ExecutionException
/*      */     {
/* 4821 */       return (V)this.localCache.getOrLoad(key);
/*      */     }
/*      */     
/*      */     public V getUnchecked(K key)
/*      */     {
/*      */       try {
/* 4827 */         return (V)get(key);
/*      */       } catch (ExecutionException e) {
/* 4829 */         throw new UncheckedExecutionException(e.getCause());
/*      */       }
/*      */     }
/*      */     
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4835 */       return this.localCache.getAll(keys);
/*      */     }
/*      */     
/*      */     public void refresh(K key)
/*      */     {
/* 4840 */       this.localCache.refresh(key);
/*      */     }
/*      */     
/*      */     public final V apply(K key)
/*      */     {
/* 4845 */       return (V)getUnchecked(key);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4854 */       return new LoadingSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\LocalCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */