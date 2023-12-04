/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ascii;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Equivalence;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ticker;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Collections;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class MapMaker
/*     */   extends GenericMapMaker<Object, Object>
/*     */ {
/*     */   private static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
/*     */   private static final int DEFAULT_EXPIRATION_NANOS = 0;
/*     */   static final int UNSET_INT = -1;
/*     */   boolean useCustomMap;
/* 116 */   int initialCapacity = -1;
/* 117 */   int concurrencyLevel = -1;
/* 118 */   int maximumSize = -1;
/*     */   
/*     */   MapMakerInternalMap.Strength keyStrength;
/*     */   
/*     */   MapMakerInternalMap.Strength valueStrength;
/* 123 */   long expireAfterWriteNanos = -1L;
/* 124 */   long expireAfterAccessNanos = -1L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   RemovalCause nullRemovalCause;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Equivalence<Object> keyEquivalence;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Ticker ticker;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker keyEquivalence(Equivalence<Object> equivalence)
/*     */   {
/* 148 */     Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
/* 149 */     this.keyEquivalence = ((Equivalence)Preconditions.checkNotNull(equivalence));
/* 150 */     this.useCustomMap = true;
/* 151 */     return this;
/*     */   }
/*     */   
/*     */   Equivalence<Object> getKeyEquivalence() {
/* 155 */     return (Equivalence)MoreObjects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
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
/*     */   public MapMaker initialCapacity(int initialCapacity)
/*     */   {
/* 170 */     Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[] { Integer.valueOf(this.initialCapacity) });
/*     */     
/* 172 */     Preconditions.checkArgument(initialCapacity >= 0);
/* 173 */     this.initialCapacity = initialCapacity;
/* 174 */     return this;
/*     */   }
/*     */   
/*     */   int getInitialCapacity() {
/* 178 */     return this.initialCapacity == -1 ? 16 : this.initialCapacity;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   MapMaker maximumSize(int size)
/*     */   {
/* 207 */     Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", new Object[] { Integer.valueOf(this.maximumSize) });
/*     */     
/* 209 */     Preconditions.checkArgument(size >= 0, "maximum size must not be negative");
/* 210 */     this.maximumSize = size;
/* 211 */     this.useCustomMap = true;
/* 212 */     if (this.maximumSize == 0)
/*     */     {
/* 214 */       this.nullRemovalCause = RemovalCause.SIZE;
/*     */     }
/* 216 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MapMaker concurrencyLevel(int concurrencyLevel)
/*     */   {
/* 240 */     Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[] { Integer.valueOf(this.concurrencyLevel) });
/*     */     
/* 242 */     Preconditions.checkArgument(concurrencyLevel > 0);
/* 243 */     this.concurrencyLevel = concurrencyLevel;
/* 244 */     return this;
/*     */   }
/*     */   
/*     */   int getConcurrencyLevel() {
/* 248 */     return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
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
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public MapMaker weakKeys()
/*     */   {
/* 265 */     return setKeyStrength(MapMakerInternalMap.Strength.WEAK);
/*     */   }
/*     */   
/*     */   MapMaker setKeyStrength(MapMakerInternalMap.Strength strength) {
/* 269 */     Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[] { this.keyStrength });
/* 270 */     this.keyStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength));
/* 271 */     Preconditions.checkArgument(this.keyStrength != MapMakerInternalMap.Strength.SOFT, "Soft keys are not supported");
/* 272 */     if (strength != MapMakerInternalMap.Strength.STRONG)
/*     */     {
/* 274 */       this.useCustomMap = true;
/*     */     }
/* 276 */     return this;
/*     */   }
/*     */   
/*     */   MapMakerInternalMap.Strength getKeyStrength() {
/* 280 */     return (MapMakerInternalMap.Strength)MoreObjects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
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
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public MapMaker weakValues()
/*     */   {
/* 303 */     return setValueStrength(MapMakerInternalMap.Strength.WEAK);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public MapMaker softValues()
/*     */   {
/* 335 */     return setValueStrength(MapMakerInternalMap.Strength.SOFT);
/*     */   }
/*     */   
/*     */   MapMaker setValueStrength(MapMakerInternalMap.Strength strength) {
/* 339 */     Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[] { this.valueStrength });
/* 340 */     this.valueStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength));
/* 341 */     if (strength != MapMakerInternalMap.Strength.STRONG)
/*     */     {
/* 343 */       this.useCustomMap = true;
/*     */     }
/* 345 */     return this;
/*     */   }
/*     */   
/*     */   MapMakerInternalMap.Strength getValueStrength() {
/* 349 */     return (MapMakerInternalMap.Strength)MoreObjects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   MapMaker expireAfterWrite(long duration, TimeUnit unit)
/*     */   {
/* 380 */     checkExpiration(duration, unit);
/* 381 */     this.expireAfterWriteNanos = unit.toNanos(duration);
/* 382 */     if ((duration == 0L) && (this.nullRemovalCause == null))
/*     */     {
/* 384 */       this.nullRemovalCause = RemovalCause.EXPIRED;
/*     */     }
/* 386 */     this.useCustomMap = true;
/* 387 */     return this;
/*     */   }
/*     */   
/*     */   private void checkExpiration(long duration, TimeUnit unit) {
/* 391 */     Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
/*     */     
/* 393 */     Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
/*     */     
/* 395 */     Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(duration), unit });
/*     */   }
/*     */   
/*     */   long getExpireAfterWriteNanos() {
/* 399 */     return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker expireAfterAccess(long duration, TimeUnit unit)
/*     */   {
/* 431 */     checkExpiration(duration, unit);
/* 432 */     this.expireAfterAccessNanos = unit.toNanos(duration);
/* 433 */     if ((duration == 0L) && (this.nullRemovalCause == null))
/*     */     {
/* 435 */       this.nullRemovalCause = RemovalCause.EXPIRED;
/*     */     }
/* 437 */     this.useCustomMap = true;
/* 438 */     return this;
/*     */   }
/*     */   
/*     */   long getExpireAfterAccessNanos() {
/* 442 */     return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
/*     */   }
/*     */   
/*     */   Ticker getTicker()
/*     */   {
/* 447 */     return (Ticker)MoreObjects.firstNonNull(this.ticker, Ticker.systemTicker());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("To be supported")
/*     */   <K, V> GenericMapMaker<K, V> removalListener(RemovalListener<K, V> listener)
/*     */   {
/* 482 */     Preconditions.checkState(this.removalListener == null);
/*     */     
/*     */ 
/*     */ 
/* 486 */     GenericMapMaker<K, V> me = this;
/* 487 */     me.removalListener = ((RemovalListener)Preconditions.checkNotNull(listener));
/* 488 */     this.useCustomMap = true;
/* 489 */     return me;
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
/*     */   public <K, V> ConcurrentMap<K, V> makeMap()
/*     */   {
/* 506 */     if (!this.useCustomMap) {
/* 507 */       return new ConcurrentHashMap(getInitialCapacity(), 0.75F, getConcurrencyLevel());
/*     */     }
/* 509 */     return (ConcurrentMap)(this.nullRemovalCause == null ? new MapMakerInternalMap(this) : new NullConcurrentMap(this));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("MapMakerInternalMap")
/*     */   <K, V> MapMakerInternalMap<K, V> makeCustomMap()
/*     */   {
/* 521 */     return new MapMakerInternalMap(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   <K, V> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> computingFunction)
/*     */   {
/* 585 */     return (ConcurrentMap)(this.nullRemovalCause == null ? new ComputingMapAdapter(this, computingFunction) : new NullComputingConcurrentMap(this, computingFunction));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 596 */     ToStringHelper s = MoreObjects.toStringHelper(this);
/* 597 */     if (this.initialCapacity != -1) {
/* 598 */       s.add("initialCapacity", this.initialCapacity);
/*     */     }
/* 600 */     if (this.concurrencyLevel != -1) {
/* 601 */       s.add("concurrencyLevel", this.concurrencyLevel);
/*     */     }
/* 603 */     if (this.maximumSize != -1)
/* 604 */       s.add("maximumSize", this.maximumSize);
/*     */     long l;
/* 606 */     if (this.expireAfterWriteNanos != -1L) {
/* 607 */       l = this.expireAfterWriteNanos;s.add("expireAfterWrite", 22 + l + "ns");
/*     */     }
/* 609 */     if (this.expireAfterAccessNanos != -1L) {
/* 610 */       l = this.expireAfterAccessNanos;s.add("expireAfterAccess", 22 + l + "ns");
/*     */     }
/* 612 */     if (this.keyStrength != null) {
/* 613 */       s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
/*     */     }
/* 615 */     if (this.valueStrength != null) {
/* 616 */       s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
/*     */     }
/* 618 */     if (this.keyEquivalence != null) {
/* 619 */       s.addValue("keyEquivalence");
/*     */     }
/* 621 */     if (this.removalListener != null) {
/* 622 */       s.addValue("removalListener");
/*     */     }
/* 624 */     return s.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static abstract interface RemovalListener<K, V>
/*     */   {
/*     */     public abstract void onRemoval(RemovalNotification<K, V> paramRemovalNotification);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class RemovalNotification<K, V>
/*     */     extends ImmutableEntry<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private final RemovalCause cause;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     RemovalNotification(@Nullable K key, @Nullable V value, RemovalCause cause)
/*     */     {
/* 662 */       super(value);
/* 663 */       this.cause = cause;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public RemovalCause getCause()
/*     */     {
/* 670 */       return this.cause;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean wasEvicted()
/*     */     {
/* 678 */       return this.cause.wasEvicted();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static abstract enum RemovalCause
/*     */   {
/* 690 */     EXPLICIT, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 703 */     REPLACED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 714 */     COLLECTED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 725 */     EXPIRED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 736 */     SIZE;
/*     */     
/*     */ 
/*     */     private RemovalCause() {}
/*     */     
/*     */ 
/*     */     abstract boolean wasEvicted();
/*     */   }
/*     */   
/*     */ 
/*     */   static class NullConcurrentMap<K, V>
/*     */     extends AbstractMap<K, V>
/*     */     implements ConcurrentMap<K, V>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private final RemovalListener<K, V> removalListener;
/*     */     
/*     */     private final RemovalCause removalCause;
/*     */     
/*     */ 
/*     */     NullConcurrentMap(MapMaker mapMaker)
/*     */     {
/* 759 */       this.removalListener = mapMaker.getRemovalListener();
/* 760 */       this.removalCause = mapMaker.nullRemovalCause;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 767 */       return false;
/*     */     }
/*     */     
/*     */     public boolean containsValue(@Nullable Object value)
/*     */     {
/* 772 */       return false;
/*     */     }
/*     */     
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 777 */       return null;
/*     */     }
/*     */     
/*     */     void notifyRemoval(K key, V value) {
/* 781 */       RemovalNotification<K, V> notification = new RemovalNotification(key, value, this.removalCause);
/*     */       
/* 783 */       this.removalListener.onRemoval(notification);
/*     */     }
/*     */     
/*     */     public V put(K key, V value)
/*     */     {
/* 788 */       Preconditions.checkNotNull(key);
/* 789 */       Preconditions.checkNotNull(value);
/* 790 */       notifyRemoval(key, value);
/* 791 */       return null;
/*     */     }
/*     */     
/*     */     public V putIfAbsent(K key, V value)
/*     */     {
/* 796 */       return (V)put(key, value);
/*     */     }
/*     */     
/*     */     public V remove(@Nullable Object key)
/*     */     {
/* 801 */       return null;
/*     */     }
/*     */     
/*     */     public boolean remove(@Nullable Object key, @Nullable Object value)
/*     */     {
/* 806 */       return false;
/*     */     }
/*     */     
/*     */     public V replace(K key, V value)
/*     */     {
/* 811 */       Preconditions.checkNotNull(key);
/* 812 */       Preconditions.checkNotNull(value);
/* 813 */       return null;
/*     */     }
/*     */     
/*     */     public boolean replace(K key, @Nullable V oldValue, V newValue)
/*     */     {
/* 818 */       Preconditions.checkNotNull(key);
/* 819 */       Preconditions.checkNotNull(newValue);
/* 820 */       return false;
/*     */     }
/*     */     
/*     */     public Set<Map.Entry<K, V>> entrySet()
/*     */     {
/* 825 */       return Collections.emptySet();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class NullComputingConcurrentMap<K, V>
/*     */     extends NullConcurrentMap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */     
/*     */     NullComputingConcurrentMap(MapMaker mapMaker, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 837 */       super();
/* 838 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */     
/*     */ 
/*     */     public V get(Object k)
/*     */     {
/* 844 */       K key = (K)k;
/* 845 */       V value = compute(key);
/* 846 */       Preconditions.checkNotNull(value, "%s returned null for key %s.", new Object[] { this.computingFunction, key });
/* 847 */       notifyRemoval(key, value);
/* 848 */       return value;
/*     */     }
/*     */     
/*     */     private V compute(K key) {
/* 852 */       Preconditions.checkNotNull(key);
/*     */       try {
/* 854 */         return (V)this.computingFunction.apply(key);
/*     */       } catch (ComputationException e) {
/* 856 */         throw e;
/*     */       } catch (Throwable t) {
/* 858 */         throw new ComputationException(t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static final class ComputingMapAdapter<K, V>
/*     */     extends ComputingConcurrentHashMap<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     ComputingMapAdapter(MapMaker mapMaker, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 877 */       super(computingFunction);
/*     */     }
/*     */     
/*     */     public V get(Object key)
/*     */     {
/*     */       V value;
/*     */       Throwable cause;
/*     */       try {
/* 885 */         value = getOrCompute(key);
/*     */       } catch (ExecutionException e) {
/* 887 */         cause = e.getCause();
/* 888 */         Throwables.propagateIfInstanceOf(cause, ComputationException.class);
/* 889 */         throw new ComputationException(cause);
/*     */       }
/*     */       
/* 892 */       if (value == null) {
/* 893 */         e = String.valueOf(String.valueOf(this.computingFunction));cause = String.valueOf(String.valueOf(key));throw new NullPointerException(24 + e.length() + cause.length() + e + " returned null for key " + cause + ".");
/*     */       }
/* 895 */       return value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\MapMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */