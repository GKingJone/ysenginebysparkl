/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Equivalence;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ComputingConcurrentHashMap<K, V>
/*     */   extends MapMakerInternalMap<K, V>
/*     */ {
/*     */   final Function<? super K, ? extends V> computingFunction;
/*     */   private static final long serialVersionUID = 4L;
/*     */   
/*     */   ComputingConcurrentHashMap(MapMaker builder, Function<? super K, ? extends V> computingFunction)
/*     */   {
/*  51 */     super(builder);
/*  52 */     this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */   }
/*     */   
/*     */   Segment<K, V> createSegment(int initialCapacity, int maxSegmentSize)
/*     */   {
/*  57 */     return new ComputingSegment(this, initialCapacity, maxSegmentSize);
/*     */   }
/*     */   
/*     */   ComputingSegment<K, V> segmentFor(int hash)
/*     */   {
/*  62 */     return (ComputingSegment)super.segmentFor(hash);
/*     */   }
/*     */   
/*     */   V getOrCompute(K key) throws ExecutionException {
/*  66 */     int hash = hash(Preconditions.checkNotNull(key));
/*  67 */     return (V)segmentFor(hash).getOrCompute(key, hash, this.computingFunction);
/*     */   }
/*     */   
/*     */   static final class ComputingSegment<K, V>
/*     */     extends Segment<K, V>
/*     */   {
/*  73 */     ComputingSegment(MapMakerInternalMap<K, V> map, int initialCapacity, int maxSegmentSize) { super(initialCapacity, maxSegmentSize); }
/*     */     
/*     */     V getOrCompute(K key, int hash, Function<? super K, ? extends V> computingFunction) throws ExecutionException {
/*     */       try {
/*     */         ReferenceEntry<K, V> e;
/*     */         Object computingValueReference;
/*     */         V value;
/*     */         do {
/*  81 */           e = getEntry(key, hash);
/*  82 */           if (e != null) {
/*  83 */             V value = getLiveValue(e);
/*  84 */             if (value != null) {
/*  85 */               recordRead(e);
/*  86 */               return value;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*  92 */           if ((e == null) || (!e.getValueReference().isComputingReference())) {
/*  93 */             boolean createNewEntry = true;
/*  94 */             computingValueReference = null;
/*  95 */             lock();
/*     */             int newCount;
/*  97 */             try { preWriteCleanup();
/*     */               
/*  99 */               newCount = this.count - 1;
/* 100 */               AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
/* 101 */               int index = hash & table.length() - 1;
/* 102 */               ReferenceEntry<K, V> first = (ReferenceEntry)table.get(index);
/*     */               
/* 104 */               for (e = first; e != null; e = e.getNext()) {
/* 105 */                 K entryKey = e.getKey();
/* 106 */                 if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*     */                 {
/* 108 */                   ValueReference<K, V> valueReference = e.getValueReference();
/* 109 */                   if (valueReference.isComputingReference()) {
/* 110 */                     createNewEntry = false; break;
/*     */                   }
/* 112 */                   V value = e.getValueReference().get();
/* 113 */                   if (value == null) {
/* 114 */                     enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.COLLECTED);
/* 115 */                   } else if ((this.map.expires()) && (this.map.isExpired(e)))
/*     */                   {
/*     */ 
/* 118 */                     enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.EXPIRED);
/*     */                   } else {
/* 120 */                     recordLockedRead(e);
/* 121 */                     return value;
/*     */                   }
/*     */                   
/*     */ 
/* 125 */                   this.evictionQueue.remove(e);
/* 126 */                   this.expirationQueue.remove(e);
/* 127 */                   this.count = newCount;
/*     */                   
/* 129 */                   break;
/*     */                 }
/*     */               }
/*     */               
/* 133 */               if (createNewEntry) {
/* 134 */                 computingValueReference = new ComputingValueReference(computingFunction);
/*     */                 
/* 136 */                 if (e == null) {
/* 137 */                   e = newEntry(key, hash, first);
/* 138 */                   e.setValueReference((ValueReference)computingValueReference);
/* 139 */                   table.set(index, e);
/*     */                 } else {
/* 141 */                   e.setValueReference((ValueReference)computingValueReference);
/*     */                 }
/*     */               }
/*     */             } finally {
/* 145 */               unlock();
/*     */             }
/*     */             
/*     */ 
/* 149 */             if (createNewEntry)
/*     */             {
/* 151 */               return (V)compute(key, hash, e, (ComputingValueReference)computingValueReference);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 156 */           Preconditions.checkState(!Thread.holdsLock(e), "Recursive computation");
/*     */           
/* 158 */           value = e.getValueReference().waitForValue();
/* 159 */         } while (value == null);
/* 160 */         recordRead(e);
/* 161 */         return value;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 167 */         postReadCleanup();
/*     */       }
/*     */     }
/*     */     
/*     */     V compute(K key, int hash, ReferenceEntry<K, V> e, ComputingValueReference<K, V> computingValueReference)
/*     */       throws ExecutionException
/*     */     {
/* 174 */       V value = null;
/* 175 */       long start = System.nanoTime();
/* 176 */       long end = 0L;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 181 */         synchronized (e) {
/* 182 */           value = computingValueReference.compute(key, hash);
/* 183 */           end = System.nanoTime(); }
/*     */         V oldValue;
/* 185 */         if (value != null)
/*     */         {
/* 187 */           oldValue = put(key, hash, value, true);
/* 188 */           if (oldValue != null)
/*     */           {
/* 190 */             enqueueNotification(key, hash, value, MapMaker.RemovalCause.REPLACED);
/*     */           }
/*     */         }
/* 193 */         return value;
/*     */       } finally {
/* 195 */         if (end == 0L) {
/* 196 */           end = System.nanoTime();
/*     */         }
/* 198 */         if (value == null) {
/* 199 */           clearValue(key, hash, computingValueReference);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ComputationExceptionReference<K, V>
/*     */     implements ValueReference<K, V>
/*     */   {
/*     */     final Throwable t;
/*     */     
/*     */     ComputationExceptionReference(Throwable t)
/*     */     {
/* 212 */       this.t = t;
/*     */     }
/*     */     
/*     */     public V get()
/*     */     {
/* 217 */       return null;
/*     */     }
/*     */     
/*     */     public ReferenceEntry<K, V> getEntry()
/*     */     {
/* 222 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*     */     {
/* 228 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isComputingReference()
/*     */     {
/* 233 */       return false;
/*     */     }
/*     */     
/*     */     public V waitForValue() throws ExecutionException
/*     */     {
/* 238 */       throw new ExecutionException(this.t);
/*     */     }
/*     */     
/*     */ 
/*     */     public void clear(ValueReference<K, V> newValue) {}
/*     */   }
/*     */   
/*     */   private static final class ComputedReference<K, V>
/*     */     implements ValueReference<K, V>
/*     */   {
/*     */     final V value;
/*     */     
/*     */     ComputedReference(@Nullable V value)
/*     */     {
/* 252 */       this.value = value;
/*     */     }
/*     */     
/*     */     public V get()
/*     */     {
/* 257 */       return (V)this.value;
/*     */     }
/*     */     
/*     */     public ReferenceEntry<K, V> getEntry()
/*     */     {
/* 262 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry)
/*     */     {
/* 268 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isComputingReference()
/*     */     {
/* 273 */       return false;
/*     */     }
/*     */     
/*     */     public V waitForValue()
/*     */     {
/* 278 */       return (V)get();
/*     */     }
/*     */     
/*     */     public void clear(ValueReference<K, V> newValue) {}
/*     */   }
/*     */   
/*     */   private static final class ComputingValueReference<K, V> implements ValueReference<K, V>
/*     */   {
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */     @GuardedBy("ComputingValueReference.this")
/* 288 */     volatile MapMakerInternalMap.ValueReference<K, V> computedReference = MapMakerInternalMap.unset();
/*     */     
/*     */     public ComputingValueReference(Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 292 */       this.computingFunction = computingFunction;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public V get()
/*     */     {
/* 299 */       return null;
/*     */     }
/*     */     
/*     */     public ReferenceEntry<K, V> getEntry()
/*     */     {
/* 304 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public ValueReference<K, V> copyFor(ReferenceQueue<V> queue, @Nullable V value, ReferenceEntry<K, V> entry)
/*     */     {
/* 310 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isComputingReference()
/*     */     {
/* 315 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public V waitForValue()
/*     */       throws ExecutionException
/*     */     {
/* 323 */       if (this.computedReference == MapMakerInternalMap.UNSET) {
/* 324 */         boolean interrupted = false;
/*     */         try {
/* 326 */           synchronized (this) {
/* 327 */             while (this.computedReference == MapMakerInternalMap.UNSET) {
/*     */               try {
/* 329 */                 wait();
/*     */               } catch (InterruptedException ie) {
/* 331 */                 interrupted = true;
/*     */               }
/*     */             }
/*     */           }
/*     */         } finally {
/* 336 */           if (interrupted) {
/* 337 */             Thread.currentThread().interrupt();
/*     */           }
/*     */         }
/*     */       }
/* 341 */       return (V)this.computedReference.waitForValue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void clear(ValueReference<K, V> newValue)
/*     */     {
/* 348 */       setValueReference(newValue);
/*     */     }
/*     */     
/*     */     V compute(K key, int hash) throws ExecutionException
/*     */     {
/*     */       V value;
/*     */       try
/*     */       {
/* 356 */         value = this.computingFunction.apply(key);
/*     */       } catch (Throwable t) {
/* 358 */         setValueReference(new ComputationExceptionReference(t));
/* 359 */         throw new ExecutionException(t);
/*     */       }
/*     */       
/* 362 */       setValueReference(new ComputedReference(value));
/* 363 */       return value;
/*     */     }
/*     */     
/*     */     void setValueReference(ValueReference<K, V> valueReference) {
/* 367 */       synchronized (this) {
/* 368 */         if (this.computedReference == MapMakerInternalMap.UNSET) {
/* 369 */           this.computedReference = valueReference;
/* 370 */           notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 382 */     return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
/*     */   }
/*     */   
/*     */ 
/*     */   static final class ComputingSerializationProxy<K, V>
/*     */     extends AbstractSerializationProxy<K, V>
/*     */   {
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */     
/*     */     private static final long serialVersionUID = 4L;
/*     */     
/*     */ 
/*     */     ComputingSerializationProxy(Strength keyStrength, Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> delegate, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 396 */       super(valueStrength, keyEquivalence, valueEquivalence, expireAfterWriteNanos, expireAfterAccessNanos, maximumSize, concurrencyLevel, removalListener, delegate);
/*     */       
/* 398 */       this.computingFunction = computingFunction;
/*     */     }
/*     */     
/*     */     private void writeObject(ObjectOutputStream out) throws IOException {
/* 402 */       out.defaultWriteObject();
/* 403 */       writeMapTo(out);
/*     */     }
/*     */     
/*     */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */     {
/* 408 */       in.defaultReadObject();
/* 409 */       MapMaker mapMaker = readMapMaker(in);
/* 410 */       this.delegate = mapMaker.makeComputingMap(this.computingFunction);
/* 411 */       readEntries(in);
/*     */     }
/*     */     
/*     */     Object readResolve() {
/* 415 */       return this.delegate;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ComputingConcurrentHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */