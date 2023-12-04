/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
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
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class MapConstraints
/*     */ {
/*     */   public static MapConstraint<Object, Object> notNull()
/*     */   {
/*  54 */     return NotNullMapConstraint.INSTANCE;
/*     */   }
/*     */   
/*     */   private static enum NotNullMapConstraint implements MapConstraint<Object, Object>
/*     */   {
/*  59 */     INSTANCE;
/*     */     
/*     */     private NotNullMapConstraint() {}
/*     */     
/*  63 */     public void checkKeyValue(Object key, Object value) { Preconditions.checkNotNull(key);
/*  64 */       Preconditions.checkNotNull(value);
/*     */     }
/*     */     
/*     */     public String toString() {
/*  68 */       return "Not null";
/*     */     }
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
/*     */   public static <K, V> Map<K, V> constrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/*  86 */     return new ConstrainedMap(map, constraint);
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
/*     */   public static <K, V> Multimap<K, V> constrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 107 */     return new ConstrainedMultimap(multimap, constraint);
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
/*     */   public static <K, V> ListMultimap<K, V> constrainedListMultimap(ListMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 129 */     return new ConstrainedListMultimap(multimap, constraint);
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
/*     */   public static <K, V> SetMultimap<K, V> constrainedSetMultimap(SetMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 150 */     return new ConstrainedSetMultimap(multimap, constraint);
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
/*     */   public static <K, V> SortedSetMultimap<K, V> constrainedSortedSetMultimap(SortedSetMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 171 */     return new ConstrainedSortedSetMultimap(multimap, constraint);
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
/*     */   private static <K, V> Entry<K, V> constrainedEntry(Entry<K, V> entry, final MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 186 */     Preconditions.checkNotNull(entry);
/* 187 */     Preconditions.checkNotNull(constraint);
/* 188 */     new ForwardingMapEntry()
/*     */     {
/* 190 */       protected Entry<K, V> delegate() { return this.val$entry; }
/*     */       
/*     */       public V setValue(V value) {
/* 193 */         constraint.checkKeyValue(getKey(), value);
/* 194 */         return (V)this.val$entry.setValue(value);
/*     */       }
/*     */     };
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
/*     */   private static <K, V> Entry<K, Collection<V>> constrainedAsMapEntry(Entry<K, Collection<V>> entry, final MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 212 */     Preconditions.checkNotNull(entry);
/* 213 */     Preconditions.checkNotNull(constraint);
/* 214 */     new ForwardingMapEntry() {
/*     */       protected Entry<K, Collection<V>> delegate() {
/* 216 */         return this.val$entry;
/*     */       }
/*     */       
/* 219 */       public Collection<V> getValue() { Constraints.constrainedTypePreservingCollection((Collection)this.val$entry.getValue(), new Constraint()
/*     */         {
/*     */           public V checkElement(V value)
/*     */           {
/* 223 */             MapConstraints.2.this.val$constraint.checkKeyValue(MapConstraints.2.this.getKey(), value);
/* 224 */             return value;
/*     */           }
/*     */         }); }
/*     */     };
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
/*     */   private static <K, V> Set<Entry<K, Collection<V>>> constrainedAsMapEntries(Set<Entry<K, Collection<V>>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 246 */     return new ConstrainedAsMapEntries(entries, constraint);
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
/*     */   private static <K, V> Collection<Entry<K, V>> constrainedEntries(Collection<Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 264 */     if ((entries instanceof Set)) {
/* 265 */       return constrainedEntrySet((Set)entries, constraint);
/*     */     }
/* 267 */     return new ConstrainedEntries(entries, constraint);
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
/*     */   private static <K, V> Set<Entry<K, V>> constrainedEntrySet(Set<Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 287 */     return new ConstrainedEntrySet(entries, constraint);
/*     */   }
/*     */   
/*     */   static class ConstrainedMap<K, V> extends ForwardingMap<K, V>
/*     */   {
/*     */     private final Map<K, V> delegate;
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     private transient Set<Entry<K, V>> entrySet;
/*     */     
/*     */     ConstrainedMap(Map<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 298 */       this.delegate = ((Map)Preconditions.checkNotNull(delegate));
/* 299 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     
/* 302 */     protected Map<K, V> delegate() { return this.delegate; }
/*     */     
/*     */     public Set<Entry<K, V>> entrySet() {
/* 305 */       Set<Entry<K, V>> result = this.entrySet;
/* 306 */       if (result == null) {
/* 307 */         this.entrySet = (result = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint));
/*     */       }
/*     */       
/* 310 */       return result;
/*     */     }
/*     */     
/* 313 */     public V put(K key, V value) { this.constraint.checkKeyValue(key, value);
/* 314 */       return (V)this.delegate.put(key, value);
/*     */     }
/*     */     
/* 317 */     public void putAll(Map<? extends K, ? extends V> map) { this.delegate.putAll(MapConstraints.checkMap(map, this.constraint)); }
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
/*     */   public static <K, V> BiMap<K, V> constrainedBiMap(BiMap<K, V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 334 */     return new ConstrainedBiMap(map, null, constraint);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class ConstrainedBiMap<K, V>
/*     */     extends ConstrainedMap<K, V>
/*     */     implements BiMap<K, V>
/*     */   {
/*     */     volatile BiMap<V, K> inverse;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     ConstrainedBiMap(BiMap<K, V> delegate, @Nullable BiMap<V, K> inverse, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 356 */       super(constraint);
/* 357 */       this.inverse = inverse;
/*     */     }
/*     */     
/*     */     protected BiMap<K, V> delegate() {
/* 361 */       return (BiMap)super.delegate();
/*     */     }
/*     */     
/*     */     public V forcePut(K key, V value)
/*     */     {
/* 366 */       this.constraint.checkKeyValue(key, value);
/* 367 */       return (V)delegate().forcePut(key, value);
/*     */     }
/*     */     
/*     */     public BiMap<V, K> inverse()
/*     */     {
/* 372 */       if (this.inverse == null) {
/* 373 */         this.inverse = new ConstrainedBiMap(delegate().inverse(), this, new InverseConstraint(this.constraint));
/*     */       }
/*     */       
/* 376 */       return this.inverse;
/*     */     }
/*     */     
/*     */     public Set<V> values() {
/* 380 */       return delegate().values();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class InverseConstraint<K, V> implements MapConstraint<K, V>
/*     */   {
/*     */     final MapConstraint<? super V, ? super K> constraint;
/*     */     
/*     */     public InverseConstraint(MapConstraint<? super V, ? super K> constraint) {
/* 389 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     
/*     */     public void checkKeyValue(K key, V value) {
/* 393 */       this.constraint.checkKeyValue(value, key);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConstrainedMultimap<K, V>
/*     */     extends ForwardingMultimap<K, V> implements Serializable
/*     */   {
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     final Multimap<K, V> delegate;
/*     */     transient Collection<Entry<K, V>> entries;
/*     */     transient Map<K, Collection<V>> asMap;
/*     */     
/*     */     public ConstrainedMultimap(Multimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 407 */       this.delegate = ((Multimap)Preconditions.checkNotNull(delegate));
/* 408 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     
/*     */     protected Multimap<K, V> delegate() {
/* 412 */       return this.delegate;
/*     */     }
/*     */     
/*     */     public Map<K, Collection<V>> asMap() {
/* 416 */       Map<K, Collection<V>> result = this.asMap;
/* 417 */       if (result == null) {
/* 418 */         final Map<K, Collection<V>> asMapDelegate = this.delegate.asMap();
/*     */         
/* 420 */         this.asMap = ( = new ForwardingMap() {
/*     */           Set<Entry<K, Collection<V>>> entrySet;
/*     */           Collection<Collection<V>> values;
/*     */           
/*     */           protected Map<K, Collection<V>> delegate() {
/* 425 */             return asMapDelegate;
/*     */           }
/*     */           
/*     */           public Set<Entry<K, Collection<V>>> entrySet() {
/* 429 */             Set<Entry<K, Collection<V>>> result = this.entrySet;
/* 430 */             if (result == null) {
/* 431 */               this.entrySet = (result = MapConstraints.constrainedAsMapEntries(asMapDelegate.entrySet(), ConstrainedMultimap.this.constraint));
/*     */             }
/*     */             
/* 434 */             return result;
/*     */           }
/*     */           
/*     */           public Collection<V> get(Object key)
/*     */           {
/*     */             try {
/* 440 */               Collection<V> collection = ConstrainedMultimap.this.get(key);
/* 441 */               return collection.isEmpty() ? null : collection;
/*     */             } catch (ClassCastException e) {}
/* 443 */             return null;
/*     */           }
/*     */           
/*     */           public Collection<Collection<V>> values()
/*     */           {
/* 448 */             Collection<Collection<V>> result = this.values;
/* 449 */             if (result == null) {
/* 450 */               this.values = (result = new ConstrainedAsMapValues(delegate().values(), entrySet()));
/*     */             }
/*     */             
/* 453 */             return result;
/*     */           }
/*     */           
/*     */           public boolean containsValue(Object o) {
/* 457 */             return values().contains(o);
/*     */           }
/*     */         });
/*     */       }
/* 461 */       return result;
/*     */     }
/*     */     
/*     */     public Collection<Entry<K, V>> entries() {
/* 465 */       Collection<Entry<K, V>> result = this.entries;
/* 466 */       if (result == null) {
/* 467 */         this.entries = (result = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint));
/*     */       }
/* 469 */       return result;
/*     */     }
/*     */     
/*     */     public Collection<V> get(final K key) {
/* 473 */       Constraints.constrainedTypePreservingCollection(this.delegate.get(key), new Constraint()
/*     */       {
/*     */         public V checkElement(V value)
/*     */         {
/* 477 */           ConstrainedMultimap.this.constraint.checkKeyValue(key, value);
/* 478 */           return value;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     public boolean put(K key, V value) {
/* 484 */       this.constraint.checkKeyValue(key, value);
/* 485 */       return this.delegate.put(key, value);
/*     */     }
/*     */     
/*     */     public boolean putAll(K key, Iterable<? extends V> values) {
/* 489 */       return this.delegate.putAll(key, MapConstraints.checkValues(key, values, this.constraint));
/*     */     }
/*     */     
/*     */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 494 */       boolean changed = false;
/* 495 */       for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
/* 496 */         changed |= put(entry.getKey(), entry.getValue());
/*     */       }
/* 498 */       return changed;
/*     */     }
/*     */     
/*     */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*     */     {
/* 503 */       return this.delegate.replaceValues(key, MapConstraints.checkValues(key, values, this.constraint));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class ConstrainedAsMapValues<K, V>
/*     */     extends ForwardingCollection<Collection<V>>
/*     */   {
/*     */     final Collection<Collection<V>> delegate;
/*     */     
/*     */     final Set<Entry<K, Collection<V>>> entrySet;
/*     */     
/*     */ 
/*     */     ConstrainedAsMapValues(Collection<Collection<V>> delegate, Set<Entry<K, Collection<V>>> entrySet)
/*     */     {
/* 519 */       this.delegate = delegate;
/* 520 */       this.entrySet = entrySet;
/*     */     }
/*     */     
/* 523 */     protected Collection<Collection<V>> delegate() { return this.delegate; }
/*     */     
/*     */     public Iterator<Collection<V>> iterator()
/*     */     {
/* 527 */       final Iterator<Entry<K, Collection<V>>> iterator = this.entrySet.iterator();
/* 528 */       new Iterator()
/*     */       {
/*     */         public boolean hasNext() {
/* 531 */           return iterator.hasNext();
/*     */         }
/*     */         
/*     */         public Collection<V> next() {
/* 535 */           return (Collection)((Entry)iterator.next()).getValue();
/*     */         }
/*     */         
/*     */         public void remove() {
/* 539 */           iterator.remove();
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public Object[] toArray() {
/* 545 */       return standardToArray();
/*     */     }
/*     */     
/* 548 */     public <T> T[] toArray(T[] array) { return standardToArray(array); }
/*     */     
/*     */     public boolean contains(Object o) {
/* 551 */       return standardContains(o);
/*     */     }
/*     */     
/* 554 */     public boolean containsAll(Collection<?> c) { return standardContainsAll(c); }
/*     */     
/*     */     public boolean remove(Object o) {
/* 557 */       return standardRemove(o);
/*     */     }
/*     */     
/* 560 */     public boolean removeAll(Collection<?> c) { return standardRemoveAll(c); }
/*     */     
/*     */     public boolean retainAll(Collection<?> c) {
/* 563 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConstrainedEntries<K, V>
/*     */     extends ForwardingCollection<Entry<K, V>>
/*     */   {
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     final Collection<Entry<K, V>> entries;
/*     */     
/*     */     ConstrainedEntries(Collection<Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 575 */       this.entries = entries;
/* 576 */       this.constraint = constraint;
/*     */     }
/*     */     
/* 579 */     protected Collection<Entry<K, V>> delegate() { return this.entries; }
/*     */     
/*     */     public Iterator<Entry<K, V>> iterator()
/*     */     {
/* 583 */       final Iterator<Entry<K, V>> iterator = this.entries.iterator();
/* 584 */       new ForwardingIterator() {
/*     */         public Entry<K, V> next() {
/* 586 */           return MapConstraints.constrainedEntry((Entry)iterator.next(), ConstrainedEntries.this.constraint);
/*     */         }
/*     */         
/* 589 */         protected Iterator<Entry<K, V>> delegate() { return iterator; }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 597 */       return standardToArray();
/*     */     }
/*     */     
/* 600 */     public <T> T[] toArray(T[] array) { return standardToArray(array); }
/*     */     
/*     */     public boolean contains(Object o) {
/* 603 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */     
/* 606 */     public boolean containsAll(Collection<?> c) { return standardContainsAll(c); }
/*     */     
/*     */     public boolean remove(Object o) {
/* 609 */       return Maps.removeEntryImpl(delegate(), o);
/*     */     }
/*     */     
/* 612 */     public boolean removeAll(Collection<?> c) { return standardRemoveAll(c); }
/*     */     
/*     */     public boolean retainAll(Collection<?> c) {
/* 615 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConstrainedEntrySet<K, V>
/*     */     extends ConstrainedEntries<K, V> implements Set<Entry<K, V>>
/*     */   {
/*     */     ConstrainedEntrySet(Set<Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 624 */       super(constraint);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 630 */       return Sets.equalsImpl(this, object);
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 634 */       return Sets.hashCodeImpl(this);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConstrainedAsMapEntries<K, V>
/*     */     extends ForwardingSet<Entry<K, Collection<V>>>
/*     */   {
/*     */     private final MapConstraint<? super K, ? super V> constraint;
/*     */     private final Set<Entry<K, Collection<V>>> entries;
/*     */     
/*     */     ConstrainedAsMapEntries(Set<Entry<K, Collection<V>>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 646 */       this.entries = entries;
/* 647 */       this.constraint = constraint;
/*     */     }
/*     */     
/*     */     protected Set<Entry<K, Collection<V>>> delegate() {
/* 651 */       return this.entries;
/*     */     }
/*     */     
/*     */     public Iterator<Entry<K, Collection<V>>> iterator() {
/* 655 */       final Iterator<Entry<K, Collection<V>>> iterator = this.entries.iterator();
/* 656 */       new ForwardingIterator() {
/*     */         public Entry<K, Collection<V>> next() {
/* 658 */           return MapConstraints.constrainedAsMapEntry((Entry)iterator.next(), ConstrainedAsMapEntries.this.constraint);
/*     */         }
/*     */         
/* 661 */         protected Iterator<Entry<K, Collection<V>>> delegate() { return iterator; }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 669 */       return standardToArray();
/*     */     }
/*     */     
/*     */     public <T> T[] toArray(T[] array) {
/* 673 */       return standardToArray(array);
/*     */     }
/*     */     
/*     */     public boolean contains(Object o) {
/* 677 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */     
/*     */     public boolean containsAll(Collection<?> c) {
/* 681 */       return standardContainsAll(c);
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object) {
/* 685 */       return standardEquals(object);
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 689 */       return standardHashCode();
/*     */     }
/*     */     
/*     */     public boolean remove(Object o) {
/* 693 */       return Maps.removeEntryImpl(delegate(), o);
/*     */     }
/*     */     
/*     */     public boolean removeAll(Collection<?> c) {
/* 697 */       return standardRemoveAll(c);
/*     */     }
/*     */     
/*     */     public boolean retainAll(Collection<?> c) {
/* 701 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConstrainedListMultimap<K, V> extends ConstrainedMultimap<K, V> implements ListMultimap<K, V>
/*     */   {
/*     */     ConstrainedListMultimap(ListMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 709 */       super(constraint);
/*     */     }
/*     */     
/* 712 */     public List<V> get(K key) { return (List)super.get(key); }
/*     */     
/*     */     public List<V> removeAll(Object key) {
/* 715 */       return (List)super.removeAll(key);
/*     */     }
/*     */     
/*     */     public List<V> replaceValues(K key, Iterable<? extends V> values) {
/* 719 */       return (List)super.replaceValues(key, values);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConstrainedSetMultimap<K, V> extends ConstrainedMultimap<K, V> implements SetMultimap<K, V>
/*     */   {
/*     */     ConstrainedSetMultimap(SetMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 727 */       super(constraint);
/*     */     }
/*     */     
/* 730 */     public Set<V> get(K key) { return (Set)super.get(key); }
/*     */     
/*     */     public Set<Entry<K, V>> entries() {
/* 733 */       return (Set)super.entries();
/*     */     }
/*     */     
/* 736 */     public Set<V> removeAll(Object key) { return (Set)super.removeAll(key); }
/*     */     
/*     */     public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*     */     {
/* 740 */       return (Set)super.replaceValues(key, values);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConstrainedSortedSetMultimap<K, V> extends ConstrainedSetMultimap<K, V> implements SortedSetMultimap<K, V>
/*     */   {
/*     */     ConstrainedSortedSetMultimap(SortedSetMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 748 */       super(constraint);
/*     */     }
/*     */     
/* 751 */     public SortedSet<V> get(K key) { return (SortedSet)super.get(key); }
/*     */     
/*     */     public SortedSet<V> removeAll(Object key) {
/* 754 */       return (SortedSet)super.removeAll(key);
/*     */     }
/*     */     
/*     */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values) {
/* 758 */       return (SortedSet)super.replaceValues(key, values);
/*     */     }
/*     */     
/*     */     public Comparator<? super V> valueComparator() {
/* 762 */       return ((SortedSetMultimap)delegate()).valueComparator();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static <K, V> Collection<V> checkValues(K key, Iterable<? extends V> values, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 769 */     Collection<V> copy = Lists.newArrayList(values);
/* 770 */     for (V value : copy) {
/* 771 */       constraint.checkKeyValue(key, value);
/*     */     }
/* 773 */     return copy;
/*     */   }
/*     */   
/*     */   private static <K, V> Map<K, V> checkMap(Map<? extends K, ? extends V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 778 */     Map<K, V> copy = new LinkedHashMap(map);
/* 779 */     for (Entry<K, V> entry : copy.entrySet()) {
/* 780 */       constraint.checkKeyValue(entry.getKey(), entry.getValue());
/*     */     }
/* 782 */     return copy;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\MapConstraints.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */