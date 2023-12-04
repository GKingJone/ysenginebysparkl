/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class AbstractBiMap<K, V>
/*     */   extends ForwardingMap<K, V>
/*     */   implements BiMap<K, V>, Serializable
/*     */ {
/*     */   private transient Map<K, V> delegate;
/*     */   transient AbstractBiMap<V, K> inverse;
/*     */   private transient Set<K> keySet;
/*     */   private transient Set<V> valueSet;
/*     */   private transient Set<Entry<K, V>> entrySet;
/*     */   @GwtIncompatible("Not needed in emulated source.")
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   AbstractBiMap(Map<K, V> forward, Map<V, K> backward)
/*     */   {
/*  57 */     setDelegates(forward, backward);
/*     */   }
/*     */   
/*     */   private AbstractBiMap(Map<K, V> backward, AbstractBiMap<V, K> forward)
/*     */   {
/*  62 */     this.delegate = backward;
/*  63 */     this.inverse = forward;
/*     */   }
/*     */   
/*     */   protected Map<K, V> delegate() {
/*  67 */     return this.delegate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   K checkKey(@Nullable K key)
/*     */   {
/*  74 */     return key;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   V checkValue(@Nullable V value)
/*     */   {
/*  81 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void setDelegates(Map<K, V> forward, Map<V, K> backward)
/*     */   {
/*  89 */     Preconditions.checkState(this.delegate == null);
/*  90 */     Preconditions.checkState(this.inverse == null);
/*  91 */     Preconditions.checkArgument(forward.isEmpty());
/*  92 */     Preconditions.checkArgument(backward.isEmpty());
/*  93 */     Preconditions.checkArgument(forward != backward);
/*  94 */     this.delegate = forward;
/*  95 */     this.inverse = new Inverse(backward, this, null);
/*     */   }
/*     */   
/*     */   void setInverse(AbstractBiMap<V, K> inverse) {
/*  99 */     this.inverse = inverse;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 105 */     return this.inverse.containsKey(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public V put(@Nullable K key, @Nullable V value)
/*     */   {
/* 111 */     return (V)putInBothMaps(key, value, false);
/*     */   }
/*     */   
/*     */   public V forcePut(@Nullable K key, @Nullable V value)
/*     */   {
/* 116 */     return (V)putInBothMaps(key, value, true);
/*     */   }
/*     */   
/*     */   private V putInBothMaps(@Nullable K key, @Nullable V value, boolean force) {
/* 120 */     checkKey(key);
/* 121 */     checkValue(value);
/* 122 */     boolean containedKey = containsKey(key);
/* 123 */     if ((containedKey) && (Objects.equal(value, get(key)))) {
/* 124 */       return value;
/*     */     }
/* 126 */     if (force) {
/* 127 */       inverse().remove(value);
/*     */     } else {
/* 129 */       Preconditions.checkArgument(!containsValue(value), "value already present: %s", new Object[] { value });
/*     */     }
/* 131 */     V oldValue = this.delegate.put(key, value);
/* 132 */     updateInverseMap(key, containedKey, oldValue, value);
/* 133 */     return oldValue;
/*     */   }
/*     */   
/*     */   private void updateInverseMap(K key, boolean containedKey, V oldValue, V newValue)
/*     */   {
/* 138 */     if (containedKey) {
/* 139 */       removeFromInverseMap(oldValue);
/*     */     }
/* 141 */     this.inverse.delegate.put(newValue, key);
/*     */   }
/*     */   
/*     */   public V remove(@Nullable Object key) {
/* 145 */     return (V)(containsKey(key) ? removeFromBothMaps(key) : null);
/*     */   }
/*     */   
/*     */   private V removeFromBothMaps(Object key) {
/* 149 */     V oldValue = this.delegate.remove(key);
/* 150 */     removeFromInverseMap(oldValue);
/* 151 */     return oldValue;
/*     */   }
/*     */   
/*     */   private void removeFromInverseMap(V oldValue) {
/* 155 */     this.inverse.delegate.remove(oldValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> map)
/*     */   {
/* 161 */     for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
/* 162 */       put(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear() {
/* 167 */     this.delegate.clear();
/* 168 */     this.inverse.delegate.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BiMap<V, K> inverse()
/*     */   {
/* 175 */     return this.inverse;
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 181 */     Set<K> result = this.keySet;
/* 182 */     return result == null ? (this.keySet = new KeySet(null)) : result;
/*     */   }
/*     */   
/*     */   private class KeySet extends ForwardingSet<K> { private KeySet() {}
/*     */     
/* 187 */     protected Set<K> delegate() { return AbstractBiMap.this.delegate.keySet(); }
/*     */     
/*     */     public void clear()
/*     */     {
/* 191 */       AbstractBiMap.this.clear();
/*     */     }
/*     */     
/*     */     public boolean remove(Object key) {
/* 195 */       if (!contains(key)) {
/* 196 */         return false;
/*     */       }
/* 198 */       AbstractBiMap.this.removeFromBothMaps(key);
/* 199 */       return true;
/*     */     }
/*     */     
/*     */     public boolean removeAll(Collection<?> keysToRemove) {
/* 203 */       return standardRemoveAll(keysToRemove);
/*     */     }
/*     */     
/*     */     public boolean retainAll(Collection<?> keysToRetain) {
/* 207 */       return standardRetainAll(keysToRetain);
/*     */     }
/*     */     
/*     */     public Iterator<K> iterator() {
/* 211 */       return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<V> values()
/*     */   {
/* 222 */     Set<V> result = this.valueSet;
/* 223 */     return result == null ? (this.valueSet = new ValueSet(null)) : result; }
/*     */   
/*     */   private class ValueSet extends ForwardingSet<V> { private ValueSet() {}
/*     */     
/* 227 */     final Set<V> valuesDelegate = AbstractBiMap.this.inverse.keySet();
/*     */     
/*     */     protected Set<V> delegate() {
/* 230 */       return this.valuesDelegate;
/*     */     }
/*     */     
/*     */     public Iterator<V> iterator() {
/* 234 */       return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
/*     */     }
/*     */     
/*     */     public Object[] toArray() {
/* 238 */       return standardToArray();
/*     */     }
/*     */     
/*     */     public <T> T[] toArray(T[] array) {
/* 242 */       return standardToArray(array);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 246 */       return standardToString();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<Entry<K, V>> entrySet()
/*     */   {
/* 253 */     Set<Entry<K, V>> result = this.entrySet;
/* 254 */     return result == null ? (this.entrySet = new EntrySet(null)) : result; }
/*     */   
/*     */   private class EntrySet extends ForwardingSet<Entry<K, V>> { private EntrySet() {}
/*     */     
/* 258 */     final Set<Entry<K, V>> esDelegate = AbstractBiMap.this.delegate.entrySet();
/*     */     
/*     */     protected Set<Entry<K, V>> delegate() {
/* 261 */       return this.esDelegate;
/*     */     }
/*     */     
/*     */     public void clear() {
/* 265 */       AbstractBiMap.this.clear();
/*     */     }
/*     */     
/*     */     public boolean remove(Object object) {
/* 269 */       if (!this.esDelegate.contains(object)) {
/* 270 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 274 */       Entry<?, ?> entry = (Entry)object;
/* 275 */       AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 281 */       this.esDelegate.remove(entry);
/* 282 */       return true;
/*     */     }
/*     */     
/*     */     public Iterator<Entry<K, V>> iterator() {
/* 286 */       final Iterator<Entry<K, V>> iterator = this.esDelegate.iterator();
/* 287 */       new Iterator() {
/*     */         Entry<K, V> entry;
/*     */         
/*     */         public boolean hasNext() {
/* 291 */           return iterator.hasNext();
/*     */         }
/*     */         
/*     */         public Entry<K, V> next() {
/* 295 */           this.entry = ((Entry)iterator.next());
/* 296 */           final Entry<K, V> finalEntry = this.entry;
/*     */           
/* 298 */           new ForwardingMapEntry() {
/*     */             protected Entry<K, V> delegate() {
/* 300 */               return finalEntry;
/*     */             }
/*     */             
/*     */             public V setValue(V value)
/*     */             {
/* 305 */               Preconditions.checkState(EntrySet.this.contains(this), "entry no longer in map");
/*     */               
/* 307 */               if (Objects.equal(value, getValue())) {
/* 308 */                 return value;
/*     */               }
/* 310 */               Preconditions.checkArgument(!AbstractBiMap.this.containsValue(value), "value already present: %s", new Object[] { value });
/*     */               
/* 312 */               V oldValue = finalEntry.setValue(value);
/* 313 */               Preconditions.checkState(Objects.equal(value, AbstractBiMap.this.get(getKey())), "entry no longer in map");
/*     */               
/* 315 */               AbstractBiMap.this.updateInverseMap(getKey(), true, oldValue, value);
/* 316 */               return oldValue;
/*     */             }
/*     */           };
/*     */         }
/*     */         
/*     */         public void remove() {
/* 322 */           CollectPreconditions.checkRemove(this.entry != null);
/* 323 */           V value = this.entry.getValue();
/* 324 */           iterator.remove();
/* 325 */           AbstractBiMap.this.removeFromInverseMap(value);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 333 */       return standardToArray();
/*     */     }
/*     */     
/* 336 */     public <T> T[] toArray(T[] array) { return standardToArray(array); }
/*     */     
/*     */     public boolean contains(Object o) {
/* 339 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */     
/* 342 */     public boolean containsAll(Collection<?> c) { return standardContainsAll(c); }
/*     */     
/*     */     public boolean removeAll(Collection<?> c) {
/* 345 */       return standardRemoveAll(c);
/*     */     }
/*     */     
/* 348 */     public boolean retainAll(Collection<?> c) { return standardRetainAll(c); }
/*     */   }
/*     */   
/*     */   private static class Inverse<K, V> extends AbstractBiMap<K, V> {
/*     */     @GwtIncompatible("Not needed in emulated source.")
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 355 */     private Inverse(Map<K, V> backward, AbstractBiMap<V, K> forward) { super(forward, null); }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     K checkKey(K key)
/*     */     {
/* 369 */       return (K)this.inverse.checkValue(key);
/*     */     }
/*     */     
/*     */     V checkValue(V value)
/*     */     {
/* 374 */       return (V)this.inverse.checkKey(value);
/*     */     }
/*     */     
/*     */ 
/*     */     @GwtIncompatible("java.io.ObjectOuputStream")
/*     */     private void writeObject(ObjectOutputStream stream)
/*     */       throws IOException
/*     */     {
/* 382 */       stream.defaultWriteObject();
/* 383 */       stream.writeObject(inverse());
/*     */     }
/*     */     
/*     */     @GwtIncompatible("java.io.ObjectInputStream")
/*     */     private void readObject(ObjectInputStream stream)
/*     */       throws IOException, ClassNotFoundException
/*     */     {
/* 390 */       stream.defaultReadObject();
/* 391 */       setInverse((AbstractBiMap)stream.readObject());
/*     */     }
/*     */     
/*     */     @GwtIncompatible("Not needed in the emulated source.")
/*     */     Object readResolve() {
/* 396 */       return inverse().inverse();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */