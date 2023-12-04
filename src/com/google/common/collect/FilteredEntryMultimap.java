/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.base.Predicates;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ @GwtCompatible
/*     */ class FilteredEntryMultimap<K, V>
/*     */   extends AbstractMultimap<K, V>
/*     */   implements FilteredMultimap<K, V>
/*     */ {
/*     */   final Multimap<K, V> unfiltered;
/*     */   final Predicate<? super Entry<K, V>> predicate;
/*     */   
/*     */   FilteredEntryMultimap(Multimap<K, V> unfiltered, Predicate<? super Entry<K, V>> predicate)
/*     */   {
/*  51 */     this.unfiltered = ((Multimap)Preconditions.checkNotNull(unfiltered));
/*  52 */     this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */   }
/*     */   
/*     */   public Multimap<K, V> unfiltered()
/*     */   {
/*  57 */     return this.unfiltered;
/*     */   }
/*     */   
/*     */   public Predicate<? super Entry<K, V>> entryPredicate()
/*     */   {
/*  62 */     return this.predicate;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  67 */     return entries().size();
/*     */   }
/*     */   
/*     */   private boolean satisfies(K key, V value) {
/*  71 */     return this.predicate.apply(Maps.immutableEntry(key, value));
/*     */   }
/*     */   
/*     */   final class ValuePredicate implements Predicate<V>
/*     */   {
/*     */     private final K key;
/*     */     
/*     */     ValuePredicate() {
/*  79 */       this.key = key;
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable V value)
/*     */     {
/*  84 */       return FilteredEntryMultimap.this.satisfies(this.key, value);
/*     */     }
/*     */   }
/*     */   
/*     */   static <E> Collection<E> filterCollection(Collection<E> collection, Predicate<? super E> predicate)
/*     */   {
/*  90 */     if ((collection instanceof Set)) {
/*  91 */       return Sets.filter((Set)collection, predicate);
/*     */     }
/*  93 */     return Collections2.filter(collection, predicate);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  99 */     return asMap().get(key) != null;
/*     */   }
/*     */   
/*     */   public Collection<V> removeAll(@Nullable Object key)
/*     */   {
/* 104 */     return (Collection)Objects.firstNonNull(asMap().remove(key), unmodifiableEmptyCollection());
/*     */   }
/*     */   
/*     */   Collection<V> unmodifiableEmptyCollection()
/*     */   {
/* 109 */     return (this.unfiltered instanceof SetMultimap) ? Collections.emptySet() : Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 116 */     entries().clear();
/*     */   }
/*     */   
/*     */   public Collection<V> get(K key)
/*     */   {
/* 121 */     return filterCollection(this.unfiltered.get(key), new ValuePredicate(key));
/*     */   }
/*     */   
/*     */   Collection<Entry<K, V>> createEntries()
/*     */   {
/* 126 */     return filterCollection(this.unfiltered.entries(), this.predicate);
/*     */   }
/*     */   
/*     */   Collection<V> createValues()
/*     */   {
/* 131 */     return new FilteredMultimapValues(this);
/*     */   }
/*     */   
/*     */   Iterator<Entry<K, V>> entryIterator()
/*     */   {
/* 136 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */   Map<K, Collection<V>> createAsMap()
/*     */   {
/* 141 */     return new AsMap();
/*     */   }
/*     */   
/*     */   public Set<K> keySet()
/*     */   {
/* 146 */     return asMap().keySet();
/*     */   }
/*     */   
/*     */   boolean removeEntriesIf(Predicate<? super Entry<K, Collection<V>>> predicate) {
/* 150 */     Iterator<Entry<K, Collection<V>>> entryIterator = this.unfiltered.asMap().entrySet().iterator();
/* 151 */     boolean changed = false;
/* 152 */     while (entryIterator.hasNext()) {
/* 153 */       Entry<K, Collection<V>> entry = (Entry)entryIterator.next();
/* 154 */       K key = entry.getKey();
/* 155 */       Collection<V> collection = filterCollection((Collection)entry.getValue(), new ValuePredicate(key));
/* 156 */       if ((!collection.isEmpty()) && (predicate.apply(Maps.immutableEntry(key, collection)))) {
/* 157 */         if (collection.size() == ((Collection)entry.getValue()).size()) {
/* 158 */           entryIterator.remove();
/*     */         } else {
/* 160 */           collection.clear();
/*     */         }
/* 162 */         changed = true;
/*     */       }
/*     */     }
/* 165 */     return changed;
/*     */   }
/*     */   
/*     */   class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>> {
/*     */     AsMap() {}
/*     */     
/* 171 */     public boolean containsKey(@Nullable Object key) { return get(key) != null; }
/*     */     
/*     */ 
/*     */     public void clear()
/*     */     {
/* 176 */       FilteredEntryMultimap.this.clear();
/*     */     }
/*     */     
/*     */     public Collection<V> get(@Nullable Object key)
/*     */     {
/* 181 */       Collection<V> result = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 182 */       if (result == null) {
/* 183 */         return null;
/*     */       }
/*     */       
/* 186 */       K k = (K)key;
/* 187 */       result = FilteredEntryMultimap.filterCollection(result, new ValuePredicate(FilteredEntryMultimap.this, k));
/* 188 */       return result.isEmpty() ? null : result;
/*     */     }
/*     */     
/*     */     public Collection<V> remove(@Nullable Object key)
/*     */     {
/* 193 */       Collection<V> collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 194 */       if (collection == null) {
/* 195 */         return null;
/*     */       }
/*     */       
/* 198 */       K k = (K)key;
/* 199 */       List<V> result = Lists.newArrayList();
/* 200 */       Iterator<V> itr = collection.iterator();
/* 201 */       while (itr.hasNext()) {
/* 202 */         V v = itr.next();
/* 203 */         if (FilteredEntryMultimap.this.satisfies(k, v)) {
/* 204 */           itr.remove();
/* 205 */           result.add(v);
/*     */         }
/*     */       }
/* 208 */       if (result.isEmpty())
/* 209 */         return null;
/* 210 */       if ((FilteredEntryMultimap.this.unfiltered instanceof SetMultimap)) {
/* 211 */         return Collections.unmodifiableSet(Sets.newLinkedHashSet(result));
/*     */       }
/* 213 */       return Collections.unmodifiableList(result);
/*     */     }
/*     */     
/*     */ 
/*     */     Set<K> createKeySet()
/*     */     {
/* 219 */       new Maps.KeySet(this)
/*     */       {
/*     */         public boolean removeAll(Collection<?> c) {
/* 222 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(c)));
/*     */         }
/*     */         
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 227 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(c))));
/*     */         }
/*     */         
/*     */         public boolean remove(@Nullable Object o)
/*     */         {
/* 232 */           return AsMap.this.remove(o) != null;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     Set<Entry<K, Collection<V>>> createEntrySet()
/*     */     {
/* 239 */       new Maps.EntrySet()
/*     */       {
/*     */         Map<K, Collection<V>> map() {
/* 242 */           return AsMap.this;
/*     */         }
/*     */         
/*     */         public Iterator<Entry<K, Collection<V>>> iterator()
/*     */         {
/* 247 */           new AbstractIterator() {
/* 248 */             final Iterator<Entry<K, Collection<V>>> backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
/*     */             
/*     */ 
/*     */             protected Entry<K, Collection<V>> computeNext()
/*     */             {
/* 253 */               while (this.backingIterator.hasNext()) {
/* 254 */                 Entry<K, Collection<V>> entry = (Entry)this.backingIterator.next();
/* 255 */                 K key = entry.getKey();
/* 256 */                 Collection<V> collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), new ValuePredicate(FilteredEntryMultimap.this, key));
/*     */                 
/* 258 */                 if (!collection.isEmpty()) {
/* 259 */                   return Maps.immutableEntry(key, collection);
/*     */                 }
/*     */               }
/* 262 */               return (Entry)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */         
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 269 */           return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(c));
/*     */         }
/*     */         
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 274 */           return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(c)));
/*     */         }
/*     */         
/*     */         public int size()
/*     */         {
/* 279 */           return Iterators.size(iterator());
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     Collection<Collection<V>> createValues()
/*     */     {
/* 286 */       new Maps.Values(this)
/*     */       {
/*     */         public boolean remove(@Nullable Object o) {
/* 289 */           if ((o instanceof Collection)) {
/* 290 */             Collection<?> c = (Collection)o;
/* 291 */             Iterator<Entry<K, Collection<V>>> entryIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
/*     */             
/* 293 */             while (entryIterator.hasNext()) {
/* 294 */               Entry<K, Collection<V>> entry = (Entry)entryIterator.next();
/* 295 */               K key = entry.getKey();
/* 296 */               Collection<V> collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), new ValuePredicate(FilteredEntryMultimap.this, key));
/*     */               
/* 298 */               if ((!collection.isEmpty()) && (c.equals(collection))) {
/* 299 */                 if (collection.size() == ((Collection)entry.getValue()).size()) {
/* 300 */                   entryIterator.remove();
/*     */                 } else {
/* 302 */                   collection.clear();
/*     */                 }
/* 304 */                 return true;
/*     */               }
/*     */             }
/*     */           }
/* 308 */           return false;
/*     */         }
/*     */         
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 313 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(c)));
/*     */         }
/*     */         
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 318 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c))));
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */   
/*     */   Multiset<K> createKeys()
/*     */   {
/* 326 */     return new Keys();
/*     */   }
/*     */   
/*     */   class Keys extends Multimaps.Keys<K, V> {
/*     */     Keys() {
/* 331 */       super();
/*     */     }
/*     */     
/*     */     public int remove(@Nullable Object key, int occurrences)
/*     */     {
/* 336 */       CollectPreconditions.checkNonnegative(occurrences, "occurrences");
/* 337 */       if (occurrences == 0) {
/* 338 */         return count(key);
/*     */       }
/* 340 */       Collection<V> collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 341 */       if (collection == null) {
/* 342 */         return 0;
/*     */       }
/*     */       
/* 345 */       K k = (K)key;
/* 346 */       int oldCount = 0;
/* 347 */       Iterator<V> itr = collection.iterator();
/* 348 */       while (itr.hasNext()) {
/* 349 */         V v = itr.next();
/* 350 */         if (FilteredEntryMultimap.this.satisfies(k, v)) {
/* 351 */           oldCount++;
/* 352 */           if (oldCount <= occurrences) {
/* 353 */             itr.remove();
/*     */           }
/*     */         }
/*     */       }
/* 357 */       return oldCount;
/*     */     }
/*     */     
/*     */     public Set<Entry<K>> entrySet()
/*     */     {
/* 362 */       new Multisets.EntrySet()
/*     */       {
/*     */         Multiset<K> multiset()
/*     */         {
/* 366 */           return Keys.this;
/*     */         }
/*     */         
/*     */         public Iterator<Entry<K>> iterator()
/*     */         {
/* 371 */           return Keys.this.entryIterator();
/*     */         }
/*     */         
/*     */         public int size()
/*     */         {
/* 376 */           return FilteredEntryMultimap.this.keySet().size();
/*     */         }
/*     */         
/*     */         private boolean removeEntriesIf(final Predicate<? super Entry<K>> predicate) {
/* 380 */           FilteredEntryMultimap.this.removeEntriesIf(new Predicate()
/*     */           {
/*     */             public boolean apply(Map.Entry<K, Collection<V>> entry)
/*     */             {
/* 384 */               return predicate.apply(Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size()));
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 392 */           return removeEntriesIf(Predicates.in(c));
/*     */         }
/*     */         
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 397 */           return removeEntriesIf(Predicates.not(Predicates.in(c)));
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\FilteredEntryMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */