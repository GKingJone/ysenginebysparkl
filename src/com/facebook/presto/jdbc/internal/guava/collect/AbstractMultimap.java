/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
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
/*     */ @GwtCompatible
/*     */ abstract class AbstractMultimap<K, V>
/*     */   implements Multimap<K, V>
/*     */ {
/*     */   private transient Collection<Entry<K, V>> entries;
/*     */   private transient Set<K> keySet;
/*     */   private transient Multiset<K> keys;
/*     */   private transient Collection<V> values;
/*     */   private transient Map<K, Collection<V>> asMap;
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/*  41 */     return size() == 0;
/*     */   }
/*     */   
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/*  46 */     for (Collection<V> collection : asMap().values()) {
/*  47 */       if (collection.contains(value)) {
/*  48 */         return true;
/*     */       }
/*     */     }
/*     */     
/*  52 */     return false;
/*     */   }
/*     */   
/*     */   public boolean containsEntry(@Nullable Object key, @Nullable Object value)
/*     */   {
/*  57 */     Collection<V> collection = (Collection)asMap().get(key);
/*  58 */     return (collection != null) && (collection.contains(value));
/*     */   }
/*     */   
/*     */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*     */   {
/*  63 */     Collection<V> collection = (Collection)asMap().get(key);
/*  64 */     return (collection != null) && (collection.remove(value));
/*     */   }
/*     */   
/*     */   public boolean put(@Nullable K key, @Nullable V value)
/*     */   {
/*  69 */     return get(key).add(value);
/*     */   }
/*     */   
/*     */   public boolean putAll(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  74 */     Preconditions.checkNotNull(values);
/*     */     
/*     */ 
/*  77 */     if ((values instanceof Collection)) {
/*  78 */       Collection<? extends V> valueCollection = (Collection)values;
/*  79 */       return (!valueCollection.isEmpty()) && (get(key).addAll(valueCollection));
/*     */     }
/*  81 */     Iterator<? extends V> valueItr = values.iterator();
/*  82 */     return (valueItr.hasNext()) && (Iterators.addAll(get(key), valueItr));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/*  88 */     boolean changed = false;
/*  89 */     for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
/*  90 */       changed |= put(entry.getKey(), entry.getValue());
/*     */     }
/*  92 */     return changed;
/*     */   }
/*     */   
/*     */   public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  97 */     Preconditions.checkNotNull(values);
/*  98 */     Collection<V> result = removeAll(key);
/*  99 */     putAll(key, values);
/* 100 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<Entry<K, V>> entries()
/*     */   {
/* 107 */     Collection<Entry<K, V>> result = this.entries;
/* 108 */     return result == null ? (this.entries = createEntries()) : result;
/*     */   }
/*     */   
/*     */   Collection<Entry<K, V>> createEntries() {
/* 112 */     if ((this instanceof SetMultimap)) {
/* 113 */       return new EntrySet(null);
/*     */     }
/* 115 */     return new Entries(null);
/*     */   }
/*     */   
/*     */   abstract Iterator<Entry<K, V>> entryIterator();
/*     */   
/*     */   private class Entries extends Multimaps.Entries<K, V> { private Entries() {}
/*     */     
/* 122 */     Multimap<K, V> multimap() { return AbstractMultimap.this; }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 127 */     public Iterator<Entry<K, V>> iterator() { return AbstractMultimap.this.entryIterator(); }
/*     */   }
/*     */   
/*     */   private class EntrySet extends Entries implements Set<Entry<K, V>> {
/* 131 */     private EntrySet() { super(null); }
/*     */     
/*     */     public int hashCode() {
/* 134 */       return Sets.hashCodeImpl(this);
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj)
/*     */     {
/* 139 */       return Sets.equalsImpl(this, obj);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 149 */     Set<K> result = this.keySet;
/* 150 */     return result == null ? (this.keySet = createKeySet()) : result;
/*     */   }
/*     */   
/*     */   Set<K> createKeySet() {
/* 154 */     return new Maps.KeySet(asMap());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Multiset<K> keys()
/*     */   {
/* 161 */     Multiset<K> result = this.keys;
/* 162 */     return result == null ? (this.keys = createKeys()) : result;
/*     */   }
/*     */   
/*     */   Multiset<K> createKeys() {
/* 166 */     return new Multimaps.Keys(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 173 */     Collection<V> result = this.values;
/* 174 */     return result == null ? (this.values = createValues()) : result;
/*     */   }
/*     */   
/*     */ 
/* 178 */   Collection<V> createValues() { return new Values(); }
/*     */   
/*     */   class Values extends AbstractCollection<V> {
/*     */     Values() {}
/*     */     
/* 183 */     public Iterator<V> iterator() { return AbstractMultimap.this.valueIterator(); }
/*     */     
/*     */     public int size()
/*     */     {
/* 187 */       return AbstractMultimap.this.size();
/*     */     }
/*     */     
/*     */     public boolean contains(@Nullable Object o) {
/* 191 */       return AbstractMultimap.this.containsValue(o);
/*     */     }
/*     */     
/*     */     public void clear() {
/* 195 */       AbstractMultimap.this.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   Iterator<V> valueIterator() {
/* 200 */     return Maps.valueIterator(entries().iterator());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 207 */     Map<K, Collection<V>> result = this.asMap;
/* 208 */     return result == null ? (this.asMap = createAsMap()) : result;
/*     */   }
/*     */   
/*     */ 
/*     */   abstract Map<K, Collection<V>> createAsMap();
/*     */   
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 216 */     return Multimaps.equalsImpl(this, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 228 */     return asMap().hashCode();
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
/* 239 */     return asMap().toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */