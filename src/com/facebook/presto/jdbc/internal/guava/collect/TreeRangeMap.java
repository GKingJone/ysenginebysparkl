/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicates;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
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
/*     */ @Beta
/*     */ @GwtIncompatible("NavigableMap")
/*     */ public final class TreeRangeMap<K extends Comparable, V>
/*     */   implements RangeMap<K, V>
/*     */ {
/*     */   private final NavigableMap<Cut<K>, RangeMapEntry<K, V>> entriesByLowerBound;
/*     */   
/*     */   public static <K extends Comparable, V> TreeRangeMap<K, V> create()
/*     */   {
/*  61 */     return new TreeRangeMap();
/*     */   }
/*     */   
/*     */   private TreeRangeMap() {
/*  65 */     this.entriesByLowerBound = Maps.newTreeMap();
/*     */   }
/*     */   
/*     */   private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V>
/*     */   {
/*     */     private final Range<K> range;
/*     */     private final V value;
/*     */     
/*     */     RangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value) {
/*  74 */       this(Range.create(lowerBound, upperBound), value);
/*     */     }
/*     */     
/*     */     RangeMapEntry(Range<K> range, V value) {
/*  78 */       this.range = range;
/*  79 */       this.value = value;
/*     */     }
/*     */     
/*     */     public Range<K> getKey()
/*     */     {
/*  84 */       return this.range;
/*     */     }
/*     */     
/*     */     public V getValue()
/*     */     {
/*  89 */       return (V)this.value;
/*     */     }
/*     */     
/*     */     public boolean contains(K value) {
/*  93 */       return this.range.contains(value);
/*     */     }
/*     */     
/*     */     Cut<K> getLowerBound() {
/*  97 */       return this.range.lowerBound;
/*     */     }
/*     */     
/*     */     Cut<K> getUpperBound() {
/* 101 */       return this.range.upperBound;
/*     */     }
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public V get(K key)
/*     */   {
/* 108 */     Entry<Range<K>, V> entry = getEntry(key);
/* 109 */     return entry == null ? null : entry.getValue();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<Range<K>, V> getEntry(K key)
/*     */   {
/* 115 */     Entry<Cut<K>, RangeMapEntry<K, V>> mapEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(key));
/*     */     
/* 117 */     if ((mapEntry != null) && (((RangeMapEntry)mapEntry.getValue()).contains(key))) {
/* 118 */       return (Entry)mapEntry.getValue();
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void put(Range<K> range, V value)
/*     */   {
/* 126 */     if (!range.isEmpty()) {
/* 127 */       Preconditions.checkNotNull(value);
/* 128 */       remove(range);
/* 129 */       this.entriesByLowerBound.put(range.lowerBound, new RangeMapEntry(range, value));
/*     */     }
/*     */   }
/*     */   
/*     */   public void putAll(RangeMap<K, V> rangeMap)
/*     */   {
/* 135 */     for (Entry<Range<K>, V> entry : rangeMap.asMapOfRanges().entrySet()) {
/* 136 */       put((Range)entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 142 */     this.entriesByLowerBound.clear();
/*     */   }
/*     */   
/*     */   public Range<K> span()
/*     */   {
/* 147 */     Entry<Cut<K>, RangeMapEntry<K, V>> firstEntry = this.entriesByLowerBound.firstEntry();
/* 148 */     Entry<Cut<K>, RangeMapEntry<K, V>> lastEntry = this.entriesByLowerBound.lastEntry();
/* 149 */     if (firstEntry == null) {
/* 150 */       throw new NoSuchElementException();
/*     */     }
/* 152 */     return Range.create(((RangeMapEntry)firstEntry.getValue()).getKey().lowerBound, ((RangeMapEntry)lastEntry.getValue()).getKey().upperBound);
/*     */   }
/*     */   
/*     */ 
/*     */   private void putRangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value)
/*     */   {
/* 158 */     this.entriesByLowerBound.put(lowerBound, new RangeMapEntry(lowerBound, upperBound, value));
/*     */   }
/*     */   
/*     */   public void remove(Range<K> rangeToRemove)
/*     */   {
/* 163 */     if (rangeToRemove.isEmpty()) {
/* 164 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */     Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryBelowToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
/*     */     
/* 173 */     if (mapEntryBelowToTruncate != null)
/*     */     {
/* 175 */       RangeMapEntry<K, V> rangeMapEntry = (RangeMapEntry)mapEntryBelowToTruncate.getValue();
/* 176 */       if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.lowerBound) > 0)
/*     */       {
/* 178 */         if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
/*     */         {
/*     */ 
/* 181 */           putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
/*     */         }
/*     */         
/*     */ 
/* 185 */         putRangeMapEntry(rangeMapEntry.getLowerBound(), rangeToRemove.lowerBound, ((RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 190 */     Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryAboveToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.upperBound);
/*     */     
/* 192 */     if (mapEntryAboveToTruncate != null)
/*     */     {
/* 194 */       RangeMapEntry<K, V> rangeMapEntry = (RangeMapEntry)mapEntryAboveToTruncate.getValue();
/* 195 */       if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
/*     */       {
/*     */ 
/* 198 */         putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((RangeMapEntry)mapEntryAboveToTruncate.getValue()).getValue());
/*     */         
/* 200 */         this.entriesByLowerBound.remove(rangeToRemove.lowerBound);
/*     */       }
/*     */     }
/* 203 */     this.entriesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
/*     */   }
/*     */   
/*     */   public Map<Range<K>, V> asMapOfRanges()
/*     */   {
/* 208 */     return new AsMapOfRanges(null);
/*     */   }
/*     */   
/*     */   private final class AsMapOfRanges extends AbstractMap<Range<K>, V> {
/*     */     private AsMapOfRanges() {}
/*     */     
/*     */     public boolean containsKey(@Nullable Object key) {
/* 215 */       return get(key) != null;
/*     */     }
/*     */     
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 220 */       if ((key instanceof Range)) {
/* 221 */         Range<?> range = (Range)key;
/* 222 */         RangeMapEntry<K, V> rangeMapEntry = (RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);
/* 223 */         if ((rangeMapEntry != null) && (rangeMapEntry.getKey().equals(range))) {
/* 224 */           return (V)rangeMapEntry.getValue();
/*     */         }
/*     */       }
/* 227 */       return null;
/*     */     }
/*     */     
/*     */     public Set<Entry<Range<K>, V>> entrySet()
/*     */     {
/* 232 */       new AbstractSet()
/*     */       {
/*     */ 
/*     */         public Iterator<Entry<Range<K>, V>> iterator()
/*     */         {
/* 237 */           return TreeRangeMap.this.entriesByLowerBound.values().iterator();
/*     */         }
/*     */         
/*     */         public int size()
/*     */         {
/* 242 */           return TreeRangeMap.this.entriesByLowerBound.size();
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */   
/*     */   public RangeMap<K, V> subRangeMap(Range<K> subRange)
/*     */   {
/* 250 */     if (subRange.equals(Range.all())) {
/* 251 */       return this;
/*     */     }
/* 253 */     return new SubRangeMap(subRange);
/*     */   }
/*     */   
/*     */ 
/*     */   private RangeMap<K, V> emptySubRangeMap()
/*     */   {
/* 259 */     return EMPTY_SUB_RANGE_MAP;
/*     */   }
/*     */   
/* 262 */   private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap()
/*     */   {
/*     */     @Nullable
/*     */     public Object get(Comparable key)
/*     */     {
/* 267 */       return null;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Map.Entry<Range, Object> getEntry(Comparable key)
/*     */     {
/* 273 */       return null;
/*     */     }
/*     */     
/*     */     public Range span()
/*     */     {
/* 278 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*     */     public void put(Range range, Object value)
/*     */     {
/* 283 */       Preconditions.checkNotNull(range);
/* 284 */       String str = String.valueOf(String.valueOf(range));throw new IllegalArgumentException(46 + str.length() + "Cannot insert range " + str + " into an empty subRangeMap");
/*     */     }
/*     */     
/*     */ 
/*     */     public void putAll(RangeMap rangeMap)
/*     */     {
/* 290 */       if (!rangeMap.asMapOfRanges().isEmpty()) {
/* 291 */         throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void clear() {}
/*     */     
/*     */ 
/*     */     public void remove(Range range)
/*     */     {
/* 301 */       Preconditions.checkNotNull(range);
/*     */     }
/*     */     
/*     */     public Map<Range, Object> asMapOfRanges()
/*     */     {
/* 306 */       return Collections.emptyMap();
/*     */     }
/*     */     
/*     */     public RangeMap subRangeMap(Range range)
/*     */     {
/* 311 */       Preconditions.checkNotNull(range);
/* 312 */       return this;
/*     */     }
/*     */   };
/*     */   
/*     */   private class SubRangeMap implements RangeMap<K, V>
/*     */   {
/*     */     private final Range<K> subRange;
/*     */     
/*     */     SubRangeMap() {
/* 321 */       this.subRange = subRange;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public V get(K key)
/*     */     {
/* 327 */       return (V)(this.subRange.contains(key) ? TreeRangeMap.this.get(key) : null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     @Nullable
/*     */     public Map.Entry<Range<K>, V> getEntry(K key)
/*     */     {
/* 335 */       if (this.subRange.contains(key)) {
/* 336 */         Entry<Range<K>, V> entry = TreeRangeMap.this.getEntry(key);
/* 337 */         if (entry != null) {
/* 338 */           return Maps.immutableEntry(((Range)entry.getKey()).intersection(this.subRange), entry.getValue());
/*     */         }
/*     */       }
/* 341 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public Range<K> span()
/*     */     {
/* 347 */       Entry<Cut<K>, RangeMapEntry<K, V>> lowerEntry = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
/*     */       Cut<K> lowerBound;
/* 349 */       Cut<K> lowerBound; if ((lowerEntry != null) && (((RangeMapEntry)lowerEntry.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0))
/*     */       {
/* 351 */         lowerBound = this.subRange.lowerBound;
/*     */       } else {
/* 353 */         lowerBound = (Cut)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
/* 354 */         if ((lowerBound == null) || (lowerBound.compareTo(this.subRange.upperBound) >= 0)) {
/* 355 */           throw new NoSuchElementException();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 360 */       Entry<Cut<K>, RangeMapEntry<K, V>> upperEntry = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
/*     */       
/* 362 */       if (upperEntry == null)
/* 363 */         throw new NoSuchElementException();
/* 364 */       Cut<K> upperBound; Cut<K> upperBound; if (((RangeMapEntry)upperEntry.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
/* 365 */         upperBound = this.subRange.upperBound;
/*     */       } else {
/* 367 */         upperBound = ((RangeMapEntry)upperEntry.getValue()).getUpperBound();
/*     */       }
/* 369 */       return Range.create(lowerBound, upperBound);
/*     */     }
/*     */     
/*     */     public void put(Range<K> range, V value)
/*     */     {
/* 374 */       Preconditions.checkArgument(this.subRange.encloses(range), "Cannot put range %s into a subRangeMap(%s)", new Object[] { range, this.subRange });
/*     */       
/* 376 */       TreeRangeMap.this.put(range, value);
/*     */     }
/*     */     
/*     */     public void putAll(RangeMap<K, V> rangeMap)
/*     */     {
/* 381 */       if (rangeMap.asMapOfRanges().isEmpty()) {
/* 382 */         return;
/*     */       }
/* 384 */       Range<K> span = rangeMap.span();
/* 385 */       Preconditions.checkArgument(this.subRange.encloses(span), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", new Object[] { span, this.subRange });
/*     */       
/* 387 */       TreeRangeMap.this.putAll(rangeMap);
/*     */     }
/*     */     
/*     */     public void clear()
/*     */     {
/* 392 */       TreeRangeMap.this.remove(this.subRange);
/*     */     }
/*     */     
/*     */     public void remove(Range<K> range)
/*     */     {
/* 397 */       if (range.isConnected(this.subRange)) {
/* 398 */         TreeRangeMap.this.remove(range.intersection(this.subRange));
/*     */       }
/*     */     }
/*     */     
/*     */     public RangeMap<K, V> subRangeMap(Range<K> range)
/*     */     {
/* 404 */       if (!range.isConnected(this.subRange)) {
/* 405 */         return TreeRangeMap.this.emptySubRangeMap();
/*     */       }
/* 407 */       return TreeRangeMap.this.subRangeMap(range.intersection(this.subRange));
/*     */     }
/*     */     
/*     */ 
/*     */     public Map<Range<K>, V> asMapOfRanges()
/*     */     {
/* 413 */       return new SubRangeMapAsMap();
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object o)
/*     */     {
/* 418 */       if ((o instanceof RangeMap)) {
/* 419 */         RangeMap<?, ?> rangeMap = (RangeMap)o;
/* 420 */         return asMapOfRanges().equals(rangeMap.asMapOfRanges());
/*     */       }
/* 422 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 427 */       return asMapOfRanges().hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 432 */       return asMapOfRanges().toString();
/*     */     }
/*     */     
/*     */     class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {
/*     */       SubRangeMapAsMap() {}
/*     */       
/*     */       public boolean containsKey(Object key) {
/* 439 */         return get(key) != null;
/*     */       }
/*     */       
/*     */       public V get(Object key)
/*     */       {
/*     */         try {
/* 445 */           if ((key instanceof Range))
/*     */           {
/* 447 */             Range<K> r = (Range)key;
/* 448 */             if ((!SubRangeMap.this.subRange.encloses(r)) || (r.isEmpty())) {
/* 449 */               return null;
/*     */             }
/* 451 */             RangeMapEntry<K, V> candidate = null;
/* 452 */             if (r.lowerBound.compareTo(SubRangeMap.this.subRange.lowerBound) == 0)
/*     */             {
/* 454 */               Entry<Cut<K>, RangeMapEntry<K, V>> entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(r.lowerBound);
/*     */               
/* 456 */               if (entry != null) {
/* 457 */                 candidate = (RangeMapEntry)entry.getValue();
/*     */               }
/*     */             } else {
/* 460 */               candidate = (RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(r.lowerBound);
/*     */             }
/*     */             
/* 463 */             if ((candidate != null) && (candidate.getKey().isConnected(SubRangeMap.this.subRange)) && (candidate.getKey().intersection(SubRangeMap.this.subRange).equals(r)))
/*     */             {
/* 465 */               return (V)candidate.getValue();
/*     */             }
/*     */           }
/*     */         } catch (ClassCastException e) {
/* 469 */           return null;
/*     */         }
/* 471 */         return null;
/*     */       }
/*     */       
/*     */       public V remove(Object key)
/*     */       {
/* 476 */         V value = get(key);
/* 477 */         if (value != null)
/*     */         {
/* 479 */           Range<K> range = (Range)key;
/* 480 */           TreeRangeMap.this.remove(range);
/* 481 */           return value;
/*     */         }
/* 483 */         return null;
/*     */       }
/*     */       
/*     */       public void clear()
/*     */       {
/* 488 */         SubRangeMap.this.clear();
/*     */       }
/*     */       
/*     */       private boolean removeEntryIf(Predicate<? super Entry<Range<K>, V>> predicate) {
/* 492 */         List<Range<K>> toRemove = Lists.newArrayList();
/* 493 */         for (Entry<Range<K>, V> entry : entrySet()) {
/* 494 */           if (predicate.apply(entry)) {
/* 495 */             toRemove.add(entry.getKey());
/*     */           }
/*     */         }
/* 498 */         for (Range<K> range : toRemove) {
/* 499 */           TreeRangeMap.this.remove(range);
/*     */         }
/* 501 */         return !toRemove.isEmpty();
/*     */       }
/*     */       
/*     */       public Set<Range<K>> keySet()
/*     */       {
/* 506 */         new Maps.KeySet(this)
/*     */         {
/*     */           public boolean remove(@Nullable Object o) {
/* 509 */             return SubRangeMapAsMap.this.remove(o) != null;
/*     */           }
/*     */           
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 514 */             return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.keyFunction()));
/*     */           }
/*     */         };
/*     */       }
/*     */       
/*     */       public Set<Entry<Range<K>, V>> entrySet()
/*     */       {
/* 521 */         new Maps.EntrySet()
/*     */         {
/*     */           Map<Range<K>, V> map() {
/* 524 */             return SubRangeMapAsMap.this;
/*     */           }
/*     */           
/*     */           public Iterator<Entry<Range<K>, V>> iterator()
/*     */           {
/* 529 */             if (SubRangeMap.this.subRange.isEmpty()) {
/* 530 */               return Iterators.emptyIterator();
/*     */             }
/* 532 */             Cut<K> cutToStart = (Cut)MoreObjects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(SubRangeMap.this.subRange.lowerBound), SubRangeMap.this.subRange.lowerBound);
/*     */             
/* 534 */             final Iterator<RangeMapEntry<K, V>> backingItr = TreeRangeMap.this.entriesByLowerBound.tailMap(cutToStart, true).values().iterator();
/*     */             
/* 536 */             new AbstractIterator()
/*     */             {
/*     */               protected Entry<Range<K>, V> computeNext()
/*     */               {
/* 540 */                 while (backingItr.hasNext()) {
/* 541 */                   RangeMapEntry<K, V> entry = (RangeMapEntry)backingItr.next();
/* 542 */                   if (entry.getLowerBound().compareTo(SubRangeMap.this.subRange.upperBound) >= 0)
/*     */                     break;
/* 544 */                   if (entry.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) > 0)
/*     */                   {
/* 546 */                     return Maps.immutableEntry(entry.getKey().intersection(SubRangeMap.this.subRange), entry.getValue());
/*     */                   }
/*     */                 }
/*     */                 
/* 550 */                 return (Entry)endOfData();
/*     */               }
/*     */             };
/*     */           }
/*     */           
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 557 */             return SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(c)));
/*     */           }
/*     */           
/*     */           public int size()
/*     */           {
/* 562 */             return Iterators.size(iterator());
/*     */           }
/*     */           
/*     */           public boolean isEmpty()
/*     */           {
/* 567 */             return !iterator().hasNext();
/*     */           }
/*     */         };
/*     */       }
/*     */       
/*     */       public Collection<V> values()
/*     */       {
/* 574 */         new Maps.Values(this)
/*     */         {
/*     */           public boolean removeAll(Collection<?> c) {
/* 577 */             return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(c), Maps.valueFunction()));
/*     */           }
/*     */           
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 582 */             return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.valueFunction()));
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object o)
/*     */   {
/* 591 */     if ((o instanceof RangeMap)) {
/* 592 */       RangeMap<?, ?> rangeMap = (RangeMap)o;
/* 593 */       return asMapOfRanges().equals(rangeMap.asMapOfRanges());
/*     */     }
/* 595 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 600 */     return asMapOfRanges().hashCode();
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 605 */     return this.entriesByLowerBound.values().toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\TreeRangeMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */