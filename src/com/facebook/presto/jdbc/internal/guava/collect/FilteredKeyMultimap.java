/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
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
/*     */ @GwtCompatible
/*     */ class FilteredKeyMultimap<K, V>
/*     */   extends AbstractMultimap<K, V>
/*     */   implements FilteredMultimap<K, V>
/*     */ {
/*     */   final Multimap<K, V> unfiltered;
/*     */   final Predicate<? super K> keyPredicate;
/*     */   
/*     */   FilteredKeyMultimap(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*     */   {
/*  44 */     this.unfiltered = ((Multimap)Preconditions.checkNotNull(unfiltered));
/*  45 */     this.keyPredicate = ((Predicate)Preconditions.checkNotNull(keyPredicate));
/*     */   }
/*     */   
/*     */   public Multimap<K, V> unfiltered()
/*     */   {
/*  50 */     return this.unfiltered;
/*     */   }
/*     */   
/*     */   public Predicate<? super Entry<K, V>> entryPredicate()
/*     */   {
/*  55 */     return Maps.keyPredicateOnEntries(this.keyPredicate);
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  60 */     int size = 0;
/*  61 */     for (Collection<V> collection : asMap().values()) {
/*  62 */       size += collection.size();
/*     */     }
/*  64 */     return size;
/*     */   }
/*     */   
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  69 */     if (this.unfiltered.containsKey(key))
/*     */     {
/*  71 */       K k = (K)key;
/*  72 */       return this.keyPredicate.apply(k);
/*     */     }
/*  74 */     return false;
/*     */   }
/*     */   
/*     */   public Collection<V> removeAll(Object key)
/*     */   {
/*  79 */     return containsKey(key) ? this.unfiltered.removeAll(key) : unmodifiableEmptyCollection();
/*     */   }
/*     */   
/*     */   Collection<V> unmodifiableEmptyCollection() {
/*  83 */     if ((this.unfiltered instanceof SetMultimap)) {
/*  84 */       return ImmutableSet.of();
/*     */     }
/*  86 */     return ImmutableList.of();
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/*  92 */     keySet().clear();
/*     */   }
/*     */   
/*     */   Set<K> createKeySet()
/*     */   {
/*  97 */     return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
/*     */   }
/*     */   
/*     */   public Collection<V> get(K key)
/*     */   {
/* 102 */     if (this.keyPredicate.apply(key))
/* 103 */       return this.unfiltered.get(key);
/* 104 */     if ((this.unfiltered instanceof SetMultimap)) {
/* 105 */       return new AddRejectingSet(key);
/*     */     }
/* 107 */     return new AddRejectingList(key);
/*     */   }
/*     */   
/*     */   static class AddRejectingSet<K, V> extends ForwardingSet<V>
/*     */   {
/*     */     final K key;
/*     */     
/*     */     AddRejectingSet(K key) {
/* 115 */       this.key = key;
/*     */     }
/*     */     
/*     */     public boolean add(V element)
/*     */     {
/* 120 */       String str = String.valueOf(String.valueOf(this.key));throw new IllegalArgumentException(32 + str.length() + "Key does not satisfy predicate: " + str);
/*     */     }
/*     */     
/*     */     public boolean addAll(Collection<? extends V> collection)
/*     */     {
/* 125 */       Preconditions.checkNotNull(collection);
/* 126 */       String str = String.valueOf(String.valueOf(this.key));throw new IllegalArgumentException(32 + str.length() + "Key does not satisfy predicate: " + str);
/*     */     }
/*     */     
/*     */     protected Set<V> delegate()
/*     */     {
/* 131 */       return Collections.emptySet();
/*     */     }
/*     */   }
/*     */   
/*     */   static class AddRejectingList<K, V> extends ForwardingList<V> {
/*     */     final K key;
/*     */     
/*     */     AddRejectingList(K key) {
/* 139 */       this.key = key;
/*     */     }
/*     */     
/*     */     public boolean add(V v)
/*     */     {
/* 144 */       add(0, v);
/* 145 */       return true;
/*     */     }
/*     */     
/*     */     public boolean addAll(Collection<? extends V> collection)
/*     */     {
/* 150 */       addAll(0, collection);
/* 151 */       return true;
/*     */     }
/*     */     
/*     */     public void add(int index, V element)
/*     */     {
/* 156 */       Preconditions.checkPositionIndex(index, 0);
/* 157 */       String str = String.valueOf(String.valueOf(this.key));throw new IllegalArgumentException(32 + str.length() + "Key does not satisfy predicate: " + str);
/*     */     }
/*     */     
/*     */     public boolean addAll(int index, Collection<? extends V> elements)
/*     */     {
/* 162 */       Preconditions.checkNotNull(elements);
/* 163 */       Preconditions.checkPositionIndex(index, 0);
/* 164 */       String str = String.valueOf(String.valueOf(this.key));throw new IllegalArgumentException(32 + str.length() + "Key does not satisfy predicate: " + str);
/*     */     }
/*     */     
/*     */     protected List<V> delegate()
/*     */     {
/* 169 */       return Collections.emptyList();
/*     */     }
/*     */   }
/*     */   
/*     */   Iterator<Entry<K, V>> entryIterator()
/*     */   {
/* 175 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */   Collection<Entry<K, V>> createEntries()
/*     */   {
/* 180 */     return new Entries();
/*     */   }
/*     */   
/*     */   class Entries extends ForwardingCollection<Entry<K, V>> {
/*     */     Entries() {}
/*     */     
/* 186 */     protected Collection<Entry<K, V>> delegate() { return Collections2.filter(FilteredKeyMultimap.this.unfiltered.entries(), FilteredKeyMultimap.this.entryPredicate()); }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean remove(@Nullable Object o)
/*     */     {
/* 192 */       if ((o instanceof Map.Entry)) {
/* 193 */         Entry<?, ?> entry = (Entry)o;
/* 194 */         if ((FilteredKeyMultimap.this.unfiltered.containsKey(entry.getKey())) && (FilteredKeyMultimap.this.keyPredicate.apply(entry.getKey())))
/*     */         {
/*     */ 
/* 197 */           return FilteredKeyMultimap.this.unfiltered.remove(entry.getKey(), entry.getValue());
/*     */         }
/*     */       }
/* 200 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   Collection<V> createValues()
/*     */   {
/* 206 */     return new FilteredMultimapValues(this);
/*     */   }
/*     */   
/*     */   Map<K, Collection<V>> createAsMap()
/*     */   {
/* 211 */     return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
/*     */   }
/*     */   
/*     */   Multiset<K> createKeys()
/*     */   {
/* 216 */     return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredKeyMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */