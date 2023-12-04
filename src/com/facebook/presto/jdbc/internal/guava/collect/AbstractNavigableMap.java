/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NavigableSet;
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
/*     */ abstract class AbstractNavigableMap<K, V>
/*     */   extends AbstractMap<K, V>
/*     */   implements NavigableMap<K, V>
/*     */ {
/*     */   @Nullable
/*     */   public abstract V get(@Nullable Object paramObject);
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> firstEntry()
/*     */   {
/*  44 */     return (Entry)Iterators.getNext(entryIterator(), null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> lastEntry()
/*     */   {
/*  50 */     return (Entry)Iterators.getNext(descendingEntryIterator(), null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> pollFirstEntry()
/*     */   {
/*  56 */     return (Entry)Iterators.pollNext(entryIterator());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> pollLastEntry()
/*     */   {
/*  62 */     return (Entry)Iterators.pollNext(descendingEntryIterator());
/*     */   }
/*     */   
/*     */   public K firstKey()
/*     */   {
/*  67 */     Entry<K, V> entry = firstEntry();
/*  68 */     if (entry == null) {
/*  69 */       throw new NoSuchElementException();
/*     */     }
/*  71 */     return (K)entry.getKey();
/*     */   }
/*     */   
/*     */ 
/*     */   public K lastKey()
/*     */   {
/*  77 */     Entry<K, V> entry = lastEntry();
/*  78 */     if (entry == null) {
/*  79 */       throw new NoSuchElementException();
/*     */     }
/*  81 */     return (K)entry.getKey();
/*     */   }
/*     */   
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> lowerEntry(K key)
/*     */   {
/*  88 */     return headMap(key, false).lastEntry();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> floorEntry(K key)
/*     */   {
/*  94 */     return headMap(key, true).lastEntry();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> ceilingEntry(K key)
/*     */   {
/* 100 */     return tailMap(key, true).firstEntry();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Map.Entry<K, V> higherEntry(K key)
/*     */   {
/* 106 */     return tailMap(key, false).firstEntry();
/*     */   }
/*     */   
/*     */   public K lowerKey(K key)
/*     */   {
/* 111 */     return (K)Maps.keyOrNull(lowerEntry(key));
/*     */   }
/*     */   
/*     */   public K floorKey(K key)
/*     */   {
/* 116 */     return (K)Maps.keyOrNull(floorEntry(key));
/*     */   }
/*     */   
/*     */   public K ceilingKey(K key)
/*     */   {
/* 121 */     return (K)Maps.keyOrNull(ceilingEntry(key));
/*     */   }
/*     */   
/*     */   public K higherKey(K key)
/*     */   {
/* 126 */     return (K)Maps.keyOrNull(higherEntry(key));
/*     */   }
/*     */   
/*     */   abstract Iterator<Entry<K, V>> entryIterator();
/*     */   
/*     */   abstract Iterator<Entry<K, V>> descendingEntryIterator();
/*     */   
/*     */   public SortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/* 135 */     return subMap(fromKey, true, toKey, false);
/*     */   }
/*     */   
/*     */   public SortedMap<K, V> headMap(K toKey)
/*     */   {
/* 140 */     return headMap(toKey, false);
/*     */   }
/*     */   
/*     */   public SortedMap<K, V> tailMap(K fromKey)
/*     */   {
/* 145 */     return tailMap(fromKey, true);
/*     */   }
/*     */   
/*     */   public NavigableSet<K> navigableKeySet()
/*     */   {
/* 150 */     return new Maps.NavigableKeySet(this);
/*     */   }
/*     */   
/*     */   public Set<K> keySet()
/*     */   {
/* 155 */     return navigableKeySet();
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract int size();
/*     */   
/*     */   public Set<Entry<K, V>> entrySet()
/*     */   {
/* 163 */     new Maps.EntrySet()
/*     */     {
/*     */       Map<K, V> map() {
/* 166 */         return AbstractNavigableMap.this;
/*     */       }
/*     */       
/*     */       public Iterator<Entry<K, V>> iterator()
/*     */       {
/* 171 */         return AbstractNavigableMap.this.entryIterator();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public NavigableSet<K> descendingKeySet()
/*     */   {
/* 178 */     return descendingMap().navigableKeySet();
/*     */   }
/*     */   
/*     */   public NavigableMap<K, V> descendingMap()
/*     */   {
/* 183 */     return new DescendingMap(null);
/*     */   }
/*     */   
/*     */   private final class DescendingMap extends Maps.DescendingMap<K, V> {
/*     */     private DescendingMap() {}
/*     */     
/* 189 */     NavigableMap<K, V> forward() { return AbstractNavigableMap.this; }
/*     */     
/*     */ 
/*     */     Iterator<Entry<K, V>> entryIterator()
/*     */     {
/* 194 */       return AbstractNavigableMap.this.descendingEntryIterator();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractNavigableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */