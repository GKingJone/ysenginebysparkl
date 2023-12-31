/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Objects;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public final class LinkedHashMultimap<K, V>
/*     */   extends AbstractSetMultimap<K, V>
/*     */ {
/*     */   private static final int DEFAULT_KEY_CAPACITY = 16;
/*     */   private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
/*     */   @VisibleForTesting
/*     */   static final double VALUE_SET_LOAD_FACTOR = 1.0D;
/*     */   
/*     */   public static <K, V> LinkedHashMultimap<K, V> create()
/*     */   {
/*  89 */     return new LinkedHashMultimap(16, 2);
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
/*     */   public static <K, V> LinkedHashMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey)
/*     */   {
/* 103 */     return new LinkedHashMultimap(Maps.capacity(expectedKeys), Maps.capacity(expectedValuesPerKey));
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
/*     */   public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 119 */     LinkedHashMultimap<K, V> result = create(multimap.keySet().size(), 2);
/* 120 */     result.putAll(multimap);
/* 121 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <K, V> void succeedsInValueSet(ValueSetLink<K, V> pred, ValueSetLink<K, V> succ)
/*     */   {
/* 133 */     pred.setSuccessorInValueSet(succ);
/* 134 */     succ.setPredecessorInValueSet(pred);
/*     */   }
/*     */   
/*     */   private static <K, V> void succeedsInMultimap(ValueEntry<K, V> pred, ValueEntry<K, V> succ)
/*     */   {
/* 139 */     pred.setSuccessorInMultimap(succ);
/* 140 */     succ.setPredecessorInMultimap(pred);
/*     */   }
/*     */   
/*     */   private static <K, V> void deleteFromValueSet(ValueSetLink<K, V> entry) {
/* 144 */     succeedsInValueSet(entry.getPredecessorInValueSet(), entry.getSuccessorInValueSet());
/*     */   }
/*     */   
/*     */   private static <K, V> void deleteFromMultimap(ValueEntry<K, V> entry) {
/* 148 */     succeedsInMultimap(entry.getPredecessorInMultimap(), entry.getSuccessorInMultimap());
/*     */   }
/*     */   
/*     */   private static abstract interface ValueSetLink<K, V> {
/*     */     public abstract ValueSetLink<K, V> getPredecessorInValueSet();
/*     */     
/*     */     public abstract ValueSetLink<K, V> getSuccessorInValueSet();
/*     */     
/*     */     public abstract void setPredecessorInValueSet(ValueSetLink<K, V> paramValueSetLink);
/*     */     
/*     */     public abstract void setSuccessorInValueSet(ValueSetLink<K, V> paramValueSetLink);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements ValueSetLink<K, V> {
/*     */     final int smearedValueHash;
/*     */     @Nullable
/*     */     ValueEntry<K, V> nextInValueBucket;
/*     */     ValueSetLink<K, V> predecessorInValueSet;
/*     */     ValueSetLink<K, V> successorInValueSet;
/*     */     ValueEntry<K, V> predecessorInMultimap;
/*     */     ValueEntry<K, V> successorInMultimap;
/*     */     
/*     */     ValueEntry(@Nullable K key, @Nullable V value, int smearedValueHash, @Nullable ValueEntry<K, V> nextInValueBucket) {
/* 172 */       super(value);
/* 173 */       this.smearedValueHash = smearedValueHash;
/* 174 */       this.nextInValueBucket = nextInValueBucket;
/*     */     }
/*     */     
/*     */     boolean matchesValue(@Nullable Object v, int smearedVHash) {
/* 178 */       return (this.smearedValueHash == smearedVHash) && (Objects.equal(getValue(), v));
/*     */     }
/*     */     
/*     */     public ValueSetLink<K, V> getPredecessorInValueSet()
/*     */     {
/* 183 */       return this.predecessorInValueSet;
/*     */     }
/*     */     
/*     */     public ValueSetLink<K, V> getSuccessorInValueSet()
/*     */     {
/* 188 */       return this.successorInValueSet;
/*     */     }
/*     */     
/*     */     public void setPredecessorInValueSet(ValueSetLink<K, V> entry)
/*     */     {
/* 193 */       this.predecessorInValueSet = entry;
/*     */     }
/*     */     
/*     */     public void setSuccessorInValueSet(ValueSetLink<K, V> entry)
/*     */     {
/* 198 */       this.successorInValueSet = entry;
/*     */     }
/*     */     
/*     */     public ValueEntry<K, V> getPredecessorInMultimap() {
/* 202 */       return this.predecessorInMultimap;
/*     */     }
/*     */     
/*     */     public ValueEntry<K, V> getSuccessorInMultimap() {
/* 206 */       return this.successorInMultimap;
/*     */     }
/*     */     
/*     */     public void setSuccessorInMultimap(ValueEntry<K, V> multimapSuccessor) {
/* 210 */       this.successorInMultimap = multimapSuccessor;
/*     */     }
/*     */     
/*     */     public void setPredecessorInMultimap(ValueEntry<K, V> multimapPredecessor) {
/* 214 */       this.predecessorInMultimap = multimapPredecessor;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/* 222 */   transient int valueSetCapacity = 2;
/*     */   private transient ValueEntry<K, V> multimapHeaderEntry;
/*     */   
/*     */   private LinkedHashMultimap(int keyCapacity, int valueSetCapacity) {
/* 226 */     super(new LinkedHashMap(keyCapacity));
/* 227 */     CollectPreconditions.checkNonnegative(valueSetCapacity, "expectedValuesPerKey");
/*     */     
/* 229 */     this.valueSetCapacity = valueSetCapacity;
/* 230 */     this.multimapHeaderEntry = new ValueEntry(null, null, 0, null);
/* 231 */     succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
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
/*     */   Set<V> createCollection()
/*     */   {
/* 245 */     return new LinkedHashSet(this.valueSetCapacity);
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
/*     */   Collection<V> createCollection(K key)
/*     */   {
/* 259 */     return new ValueSet(key, this.valueSetCapacity);
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
/*     */   public Set<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/* 272 */     return super.replaceValues(key, values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java serialization not supported")
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<Entry<K, V>> entries()
/*     */   {
/* 288 */     return super.entries();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 299 */     return super.values();
/*     */   }
/*     */   
/*     */ 
/*     */   @VisibleForTesting
/*     */   final class ValueSet
/*     */     extends Sets.ImprovedAbstractSet<V>
/*     */     implements ValueSetLink<K, V>
/*     */   {
/*     */     private final K key;
/*     */     @VisibleForTesting
/*     */     ValueEntry<K, V>[] hashTable;
/* 311 */     private int size = 0;
/* 312 */     private int modCount = 0;
/*     */     
/*     */     private ValueSetLink<K, V> firstEntry;
/*     */     
/*     */     private ValueSetLink<K, V> lastEntry;
/*     */     
/*     */     ValueSet(int key)
/*     */     {
/* 320 */       this.key = key;
/* 321 */       this.firstEntry = this;
/* 322 */       this.lastEntry = this;
/*     */       
/* 324 */       int tableSize = Hashing.closedTableSize(expectedValues, 1.0D);
/*     */       
/*     */ 
/* 327 */       ValueEntry<K, V>[] hashTable = new ValueEntry[tableSize];
/* 328 */       this.hashTable = hashTable;
/*     */     }
/*     */     
/*     */     private int mask() {
/* 332 */       return this.hashTable.length - 1;
/*     */     }
/*     */     
/*     */     public ValueSetLink<K, V> getPredecessorInValueSet()
/*     */     {
/* 337 */       return this.lastEntry;
/*     */     }
/*     */     
/*     */     public ValueSetLink<K, V> getSuccessorInValueSet()
/*     */     {
/* 342 */       return this.firstEntry;
/*     */     }
/*     */     
/*     */     public void setPredecessorInValueSet(ValueSetLink<K, V> entry)
/*     */     {
/* 347 */       this.lastEntry = entry;
/*     */     }
/*     */     
/*     */     public void setSuccessorInValueSet(ValueSetLink<K, V> entry)
/*     */     {
/* 352 */       this.firstEntry = entry;
/*     */     }
/*     */     
/*     */     public Iterator<V> iterator()
/*     */     {
/* 357 */       new Iterator() {
/* 358 */         ValueSetLink<K, V> nextEntry = ValueSet.this.firstEntry;
/*     */         ValueEntry<K, V> toRemove;
/* 360 */         int expectedModCount = ValueSet.this.modCount;
/*     */         
/*     */         private void checkForComodification() {
/* 363 */           if (ValueSet.this.modCount != this.expectedModCount) {
/* 364 */             throw new ConcurrentModificationException();
/*     */           }
/*     */         }
/*     */         
/*     */         public boolean hasNext()
/*     */         {
/* 370 */           checkForComodification();
/* 371 */           return this.nextEntry != ValueSet.this;
/*     */         }
/*     */         
/*     */         public V next()
/*     */         {
/* 376 */           if (!hasNext()) {
/* 377 */             throw new NoSuchElementException();
/*     */           }
/* 379 */           ValueEntry<K, V> entry = (ValueEntry)this.nextEntry;
/* 380 */           V result = entry.getValue();
/* 381 */           this.toRemove = entry;
/* 382 */           this.nextEntry = entry.getSuccessorInValueSet();
/* 383 */           return result;
/*     */         }
/*     */         
/*     */         public void remove()
/*     */         {
/* 388 */           checkForComodification();
/* 389 */           CollectPreconditions.checkRemove(this.toRemove != null);
/* 390 */           ValueSet.this.remove(this.toRemove.getValue());
/* 391 */           this.expectedModCount = ValueSet.this.modCount;
/* 392 */           this.toRemove = null;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 399 */       return this.size;
/*     */     }
/*     */     
/*     */     public boolean contains(@Nullable Object o)
/*     */     {
/* 404 */       int smearedHash = Hashing.smearedHash(o);
/* 405 */       for (ValueEntry<K, V> entry = this.hashTable[(smearedHash & mask())]; entry != null;
/* 406 */           entry = entry.nextInValueBucket) {
/* 407 */         if (entry.matchesValue(o, smearedHash)) {
/* 408 */           return true;
/*     */         }
/*     */       }
/* 411 */       return false;
/*     */     }
/*     */     
/*     */     public boolean add(@Nullable V value)
/*     */     {
/* 416 */       int smearedHash = Hashing.smearedHash(value);
/* 417 */       int bucket = smearedHash & mask();
/* 418 */       ValueEntry<K, V> rowHead = this.hashTable[bucket];
/* 419 */       for (ValueEntry<K, V> entry = rowHead; entry != null;
/* 420 */           entry = entry.nextInValueBucket) {
/* 421 */         if (entry.matchesValue(value, smearedHash)) {
/* 422 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 426 */       ValueEntry<K, V> newEntry = new ValueEntry(this.key, value, smearedHash, rowHead);
/* 427 */       LinkedHashMultimap.succeedsInValueSet(this.lastEntry, newEntry);
/* 428 */       LinkedHashMultimap.succeedsInValueSet(newEntry, this);
/* 429 */       LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), newEntry);
/* 430 */       LinkedHashMultimap.succeedsInMultimap(newEntry, LinkedHashMultimap.this.multimapHeaderEntry);
/* 431 */       this.hashTable[bucket] = newEntry;
/* 432 */       this.size += 1;
/* 433 */       this.modCount += 1;
/* 434 */       rehashIfNecessary();
/* 435 */       return true;
/*     */     }
/*     */     
/*     */     private void rehashIfNecessary() {
/* 439 */       if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0D))
/*     */       {
/* 441 */         ValueEntry<K, V>[] hashTable = new ValueEntry[this.hashTable.length * 2];
/* 442 */         this.hashTable = hashTable;
/* 443 */         int mask = hashTable.length - 1;
/* 444 */         for (ValueSetLink<K, V> entry = this.firstEntry;
/* 445 */             entry != this; entry = entry.getSuccessorInValueSet()) {
/* 446 */           ValueEntry<K, V> valueEntry = (ValueEntry)entry;
/* 447 */           int bucket = valueEntry.smearedValueHash & mask;
/* 448 */           valueEntry.nextInValueBucket = hashTable[bucket];
/* 449 */           hashTable[bucket] = valueEntry;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean remove(@Nullable Object o)
/*     */     {
/* 456 */       int smearedHash = Hashing.smearedHash(o);
/* 457 */       int bucket = smearedHash & mask();
/* 458 */       ValueEntry<K, V> prev = null;
/* 459 */       for (ValueEntry<K, V> entry = this.hashTable[bucket]; entry != null;
/* 460 */           entry = entry.nextInValueBucket) {
/* 461 */         if (entry.matchesValue(o, smearedHash)) {
/* 462 */           if (prev == null)
/*     */           {
/* 464 */             this.hashTable[bucket] = entry.nextInValueBucket;
/*     */           } else {
/* 466 */             prev.nextInValueBucket = entry.nextInValueBucket;
/*     */           }
/* 468 */           LinkedHashMultimap.deleteFromValueSet(entry);
/* 469 */           LinkedHashMultimap.deleteFromMultimap(entry);
/* 470 */           this.size -= 1;
/* 471 */           this.modCount += 1;
/* 472 */           return true;
/*     */         }
/* 460 */         prev = entry;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 475 */       return false;
/*     */     }
/*     */     
/*     */     public void clear()
/*     */     {
/* 480 */       Arrays.fill(this.hashTable, null);
/* 481 */       this.size = 0;
/* 482 */       for (ValueSetLink<K, V> entry = this.firstEntry;
/* 483 */           entry != this; entry = entry.getSuccessorInValueSet()) {
/* 484 */         ValueEntry<K, V> valueEntry = (ValueEntry)entry;
/* 485 */         LinkedHashMultimap.deleteFromMultimap(valueEntry);
/*     */       }
/* 487 */       LinkedHashMultimap.succeedsInValueSet(this, this);
/* 488 */       this.modCount += 1;
/*     */     }
/*     */   }
/*     */   
/*     */   Iterator<Entry<K, V>> entryIterator()
/*     */   {
/* 494 */     new Iterator() {
/* 495 */       ValueEntry<K, V> nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
/*     */       ValueEntry<K, V> toRemove;
/*     */       
/*     */       public boolean hasNext()
/*     */       {
/* 500 */         return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
/*     */       }
/*     */       
/*     */       public Entry<K, V> next()
/*     */       {
/* 505 */         if (!hasNext()) {
/* 506 */           throw new NoSuchElementException();
/*     */         }
/* 508 */         ValueEntry<K, V> result = this.nextEntry;
/* 509 */         this.toRemove = result;
/* 510 */         this.nextEntry = this.nextEntry.successorInMultimap;
/* 511 */         return result;
/*     */       }
/*     */       
/*     */       public void remove()
/*     */       {
/* 516 */         CollectPreconditions.checkRemove(this.toRemove != null);
/* 517 */         LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
/* 518 */         this.toRemove = null;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   Iterator<V> valueIterator()
/*     */   {
/* 525 */     return Maps.valueIterator(entryIterator());
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 530 */     super.clear();
/* 531 */     succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 540 */     stream.defaultWriteObject();
/* 541 */     stream.writeInt(this.valueSetCapacity);
/* 542 */     stream.writeInt(keySet().size());
/* 543 */     for (K key : keySet()) {
/* 544 */       stream.writeObject(key);
/*     */     }
/* 546 */     stream.writeInt(size());
/* 547 */     for (Entry<K, V> entry : entries()) {
/* 548 */       stream.writeObject(entry.getKey());
/* 549 */       stream.writeObject(entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 556 */     stream.defaultReadObject();
/* 557 */     this.multimapHeaderEntry = new ValueEntry(null, null, 0, null);
/* 558 */     succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
/* 559 */     this.valueSetCapacity = stream.readInt();
/* 560 */     int distinctKeys = stream.readInt();
/* 561 */     Map<K, Collection<V>> map = new LinkedHashMap(Maps.capacity(distinctKeys));
/*     */     
/* 563 */     for (int i = 0; i < distinctKeys; i++)
/*     */     {
/* 565 */       K key = stream.readObject();
/* 566 */       map.put(key, createCollection(key));
/*     */     }
/* 568 */     int entries = stream.readInt();
/* 569 */     for (int i = 0; i < entries; i++)
/*     */     {
/* 571 */       K key = stream.readObject();
/*     */       
/* 573 */       V value = stream.readObject();
/* 574 */       ((Collection)map.get(key)).add(value);
/*     */     }
/* 576 */     setMap(map);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\LinkedHashMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */