/*     */ package com.mchange.v2.util;
/*     */ 
/*     */ import com.mchange.v1.util.AbstractMapEntry;
/*     */ import com.mchange.v1.util.WrapperIterator;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DoubleWeakHashMap
/*     */   implements Map
/*     */ {
/*     */   HashMap inner;
/*  57 */   ReferenceQueue keyQ = new ReferenceQueue();
/*  58 */   ReferenceQueue valQ = new ReferenceQueue();
/*     */   
/*  60 */   CheckKeyHolder holder = new CheckKeyHolder();
/*     */   
/*  62 */   Set userKeySet = null;
/*  63 */   Collection valuesCollection = null;
/*     */   
/*     */   public DoubleWeakHashMap() {
/*  66 */     this.inner = new HashMap();
/*     */   }
/*     */   
/*  69 */   public DoubleWeakHashMap(int initialCapacity) { this.inner = new HashMap(initialCapacity); }
/*     */   
/*     */   public DoubleWeakHashMap(int initialCapacity, float loadFactor) {
/*  72 */     this.inner = new HashMap(initialCapacity, loadFactor);
/*     */   }
/*     */   
/*     */   public DoubleWeakHashMap(Map m) {
/*  76 */     this();
/*  77 */     putAll(m);
/*     */   }
/*     */   
/*     */   public void cleanCleared()
/*     */   {
/*     */     WKey wk;
/*  83 */     while ((wk = (WKey)this.keyQ.poll()) != null) {
/*  84 */       this.inner.remove(wk);
/*     */     }
/*     */     WVal wv;
/*  87 */     while ((wv = (WVal)this.valQ.poll()) != null) {
/*  88 */       this.inner.remove(wv.getWKey());
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear() {
/*  93 */     cleanCleared();
/*  94 */     this.inner.clear();
/*     */   }
/*     */   
/*     */   public boolean containsKey(Object key)
/*     */   {
/*  99 */     cleanCleared();
/*     */     try {
/* 101 */       return this.inner.containsKey(this.holder.set(key));
/*     */     } finally {
/* 103 */       this.holder.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object val) {
/* 108 */     for (Iterator ii = this.inner.values().iterator(); ii.hasNext();)
/*     */     {
/* 110 */       WVal wval = (WVal)ii.next();
/* 111 */       if (val.equals(wval.get()))
/* 112 */         return true;
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */   
/*     */   public Set entrySet()
/*     */   {
/* 119 */     cleanCleared();
/* 120 */     return new UserEntrySet(null);
/*     */   }
/*     */   
/*     */   public Object get(Object key)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       cleanCleared();
/* 128 */       WVal wval = (WVal)this.inner.get(this.holder.set(key));
/* 129 */       return wval == null ? null : wval.get();
/*     */     }
/*     */     finally {
/* 132 */       this.holder.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 137 */     cleanCleared();
/* 138 */     return this.inner.isEmpty();
/*     */   }
/*     */   
/*     */   public Set keySet()
/*     */   {
/* 143 */     cleanCleared();
/* 144 */     if (this.userKeySet == null)
/* 145 */       this.userKeySet = new UserKeySet();
/* 146 */     return this.userKeySet;
/*     */   }
/*     */   
/*     */   public Object put(Object key, Object val)
/*     */   {
/* 151 */     cleanCleared();
/* 152 */     WVal wout = doPut(key, val);
/* 153 */     if (wout != null) {
/* 154 */       return wout.get();
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */   
/*     */   private WVal doPut(Object key, Object val)
/*     */   {
/* 161 */     WKey wk = new WKey(key, this.keyQ);
/* 162 */     WVal wv = new WVal(wk, val, this.valQ);
/* 163 */     return (WVal)this.inner.put(wk, wv);
/*     */   }
/*     */   
/*     */   public void putAll(Map m)
/*     */   {
/* 168 */     cleanCleared();
/* 169 */     for (Iterator ii = m.entrySet().iterator(); ii.hasNext();)
/*     */     {
/* 171 */       Entry entry = (Entry)ii.next();
/* 172 */       doPut(entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public Object remove(Object key)
/*     */   {
/*     */     try
/*     */     {
/* 180 */       cleanCleared();
/* 181 */       WVal wv = (WVal)this.inner.remove(this.holder.set(key));
/* 182 */       return wv == null ? null : wv.get();
/*     */     }
/*     */     finally {
/* 185 */       this.holder.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public int size() {
/* 190 */     cleanCleared();
/* 191 */     return this.inner.size();
/*     */   }
/*     */   
/*     */   public Collection values()
/*     */   {
/* 196 */     if (this.valuesCollection == null)
/* 197 */       this.valuesCollection = new ValuesCollection();
/* 198 */     return this.valuesCollection;
/*     */   }
/*     */   
/*     */   static final class CheckKeyHolder
/*     */   {
/*     */     Object checkKey;
/*     */     
/*     */     public Object get() {
/* 206 */       return this.checkKey;
/*     */     }
/*     */     
/*     */     public CheckKeyHolder set(Object ck) {
/* 210 */       assert (this.checkKey == null) : "Illegal concurrenct use of DoubleWeakHashMap!";
/*     */       
/* 212 */       this.checkKey = ck;
/* 213 */       return this;
/*     */     }
/*     */     
/*     */     public void clear() {
/* 217 */       this.checkKey = null;
/*     */     }
/*     */     
/* 220 */     public int hashCode() { return this.checkKey.hashCode(); }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/* 224 */       assert (get() != null) : "CheckedKeyHolder should never do an equality check while its value is null.";
/*     */       
/* 226 */       if (this == o)
/* 227 */         return true;
/* 228 */       if ((o instanceof CheckKeyHolder))
/* 229 */         return get().equals(((CheckKeyHolder)o).get());
/* 230 */       if ((o instanceof WKey)) {
/* 231 */         return get().equals(((WKey)o).get());
/*     */       }
/* 233 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class WKey extends WeakReference
/*     */   {
/*     */     int cachedHash;
/*     */     
/*     */     WKey(Object keyObj, ReferenceQueue rq)
/*     */     {
/* 243 */       super(rq);
/* 244 */       this.cachedHash = keyObj.hashCode();
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 248 */       return this.cachedHash;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 252 */       if (this == o)
/* 253 */         return true;
/* 254 */       if ((o instanceof WKey))
/*     */       {
/* 256 */         WKey oo = (WKey)o;
/* 257 */         Object myVal = get();
/* 258 */         Object ooVal = oo.get();
/* 259 */         if ((myVal == null) || (ooVal == null)) {
/* 260 */           return false;
/*     */         }
/* 262 */         return myVal.equals(ooVal);
/*     */       }
/* 264 */       if ((o instanceof CheckKeyHolder))
/*     */       {
/* 266 */         CheckKeyHolder oo = (CheckKeyHolder)o;
/* 267 */         Object myVal = get();
/* 268 */         Object ooVal = oo.get();
/* 269 */         if ((myVal == null) || (ooVal == null)) {
/* 270 */           return false;
/*     */         }
/* 272 */         return myVal.equals(ooVal);
/*     */       }
/*     */       
/* 275 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class WVal extends WeakReference
/*     */   {
/*     */     WKey key;
/*     */     
/*     */     WVal(WKey key, Object valObj, ReferenceQueue rq) {
/* 284 */       super(rq);
/* 285 */       this.key = key;
/*     */     }
/*     */     
/*     */ 
/* 289 */     public WKey getWKey() { return this.key; }
/*     */   }
/*     */   
/*     */   private final class UserEntrySet extends AbstractSet {
/* 293 */     UserEntrySet(DoubleWeakHashMap.1 x1) { this(); }
/*     */     
/*     */     private Set innerEntrySet()
/*     */     {
/* 297 */       DoubleWeakHashMap.this.cleanCleared();
/* 298 */       return DoubleWeakHashMap.this.inner.entrySet();
/*     */     }
/*     */     
/*     */     public Iterator iterator()
/*     */     {
/* 303 */       new WrapperIterator(innerEntrySet().iterator(), true)
/*     */       {
/*     */         protected Object transformObject(Object o)
/*     */         {
/* 307 */           Entry innerEntry = (Entry)o;
/* 308 */           Object key = ((WKey)innerEntry.getKey()).get();
/* 309 */           Object val = ((WVal)innerEntry.getValue()).get();
/*     */           
/* 311 */           if ((key == null) || (val == null)) {
/* 312 */             return WrapperIterator.SKIP_TOKEN;
/*     */           }
/* 314 */           return new UserEntry(DoubleWeakHashMap.this, innerEntry, key, val);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/* 320 */     public int size() { return innerEntrySet().size(); }
/*     */     
/*     */     private UserEntrySet() {}
/*     */   }
/*     */   
/*     */   class UserEntry extends AbstractMapEntry {
/*     */     Entry innerEntry;
/*     */     Object key;
/*     */     Object val;
/*     */     
/*     */     UserEntry(Entry innerEntry, Object key, Object val) {
/* 331 */       this.innerEntry = innerEntry;
/* 332 */       this.key = key;
/* 333 */       this.val = val;
/*     */     }
/*     */     
/*     */     public final Object getKey() {
/* 337 */       return this.key;
/*     */     }
/*     */     
/* 340 */     public final Object getValue() { return this.val; }
/*     */     
/*     */ 
/* 343 */     public final Object setValue(Object value) { return this.innerEntry.setValue(new WVal((WKey)this.innerEntry.getKey(), value, DoubleWeakHashMap.this.valQ)); }
/*     */   }
/*     */   
/*     */   class UserKeySet implements Set {
/*     */     UserKeySet() {}
/*     */     
/*     */     public boolean add(Object o) {
/* 350 */       DoubleWeakHashMap.this.cleanCleared();
/* 351 */       throw new UnsupportedOperationException("You cannot add to a Map's key set.");
/*     */     }
/*     */     
/*     */     public boolean addAll(Collection c)
/*     */     {
/* 356 */       DoubleWeakHashMap.this.cleanCleared();
/* 357 */       throw new UnsupportedOperationException("You cannot add to a Map's key set.");
/*     */     }
/*     */     
/*     */     public void clear() {
/* 361 */       DoubleWeakHashMap.this.clear();
/*     */     }
/*     */     
/*     */     public boolean contains(Object o) {
/* 365 */       return DoubleWeakHashMap.this.containsKey(o);
/*     */     }
/*     */     
/*     */     public boolean containsAll(Collection c)
/*     */     {
/* 370 */       for (Iterator ii = c.iterator(); ii.hasNext();)
/* 371 */         if (!contains(ii.next()))
/* 372 */           return false;
/* 373 */       return true;
/*     */     }
/*     */     
/*     */     public boolean isEmpty() {
/* 377 */       return DoubleWeakHashMap.this.isEmpty();
/*     */     }
/*     */     
/*     */     public Iterator iterator() {
/* 381 */       DoubleWeakHashMap.this.cleanCleared();
/* 382 */       new WrapperIterator(DoubleWeakHashMap.this.inner.keySet().iterator(), true)
/*     */       {
/*     */         protected Object transformObject(Object o)
/*     */         {
/* 386 */           Object key = ((WKey)o).get();
/*     */           
/* 388 */           if (key == null) {
/* 389 */             return WrapperIterator.SKIP_TOKEN;
/*     */           }
/* 391 */           return key;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public boolean remove(Object o)
/*     */     {
/* 398 */       return DoubleWeakHashMap.this.remove(o) != null;
/*     */     }
/*     */     
/*     */     public boolean removeAll(Collection c)
/*     */     {
/* 403 */       boolean out = false;
/* 404 */       for (Iterator ii = c.iterator(); ii.hasNext();)
/* 405 */         out |= remove(ii.next());
/* 406 */       return out;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean retainAll(Collection c)
/*     */     {
/* 412 */       boolean out = false;
/* 413 */       for (Iterator ii = iterator(); ii.hasNext();)
/*     */       {
/* 415 */         if (!c.contains(ii.next()))
/*     */         {
/* 417 */           ii.remove();
/* 418 */           out = true;
/*     */         }
/*     */       }
/* 421 */       return out;
/*     */     }
/*     */     
/*     */     public int size() {
/* 425 */       return DoubleWeakHashMap.this.size();
/*     */     }
/*     */     
/*     */     public Object[] toArray() {
/* 429 */       DoubleWeakHashMap.this.cleanCleared();
/* 430 */       return new HashSet(this).toArray();
/*     */     }
/*     */     
/*     */     public Object[] toArray(Object[] array)
/*     */     {
/* 435 */       DoubleWeakHashMap.this.cleanCleared();
/* 436 */       return new HashSet(this).toArray(array);
/*     */     }
/*     */   }
/*     */   
/*     */   class ValuesCollection implements Collection
/*     */   {
/*     */     ValuesCollection() {}
/*     */     
/*     */     public boolean add(Object o) {
/* 445 */       DoubleWeakHashMap.this.cleanCleared();
/* 446 */       throw new UnsupportedOperationException("DoubleWeakHashMap does not support adding to its values Collection.");
/*     */     }
/*     */     
/*     */     public boolean addAll(Collection c)
/*     */     {
/* 451 */       DoubleWeakHashMap.this.cleanCleared();
/* 452 */       throw new UnsupportedOperationException("DoubleWeakHashMap does not support adding to its values Collection.");
/*     */     }
/*     */     
/*     */     public void clear() {
/* 456 */       DoubleWeakHashMap.this.clear();
/*     */     }
/*     */     
/* 459 */     public boolean contains(Object o) { return DoubleWeakHashMap.this.containsValue(o); }
/*     */     
/*     */     public boolean containsAll(Collection c)
/*     */     {
/* 463 */       for (Iterator ii = c.iterator(); ii.hasNext();)
/* 464 */         if (!contains(ii.next()))
/* 465 */           return false;
/* 466 */       return true;
/*     */     }
/*     */     
/*     */     public boolean isEmpty() {
/* 470 */       return DoubleWeakHashMap.this.isEmpty();
/*     */     }
/*     */     
/*     */     public Iterator iterator() {
/* 474 */       new WrapperIterator(DoubleWeakHashMap.this.inner.values().iterator(), true)
/*     */       {
/*     */         protected Object transformObject(Object o)
/*     */         {
/* 478 */           Object val = ((WVal)o).get();
/*     */           
/* 480 */           if (val == null) {
/* 481 */             return WrapperIterator.SKIP_TOKEN;
/*     */           }
/* 483 */           return val;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public boolean remove(Object o)
/*     */     {
/* 490 */       DoubleWeakHashMap.this.cleanCleared();
/* 491 */       return removeValue(o);
/*     */     }
/*     */     
/*     */     public boolean removeAll(Collection c)
/*     */     {
/* 496 */       DoubleWeakHashMap.this.cleanCleared();
/* 497 */       boolean out = false;
/* 498 */       for (Iterator ii = c.iterator(); ii.hasNext();)
/* 499 */         out |= removeValue(ii.next());
/* 500 */       return out;
/*     */     }
/*     */     
/*     */     public boolean retainAll(Collection c)
/*     */     {
/* 505 */       DoubleWeakHashMap.this.cleanCleared();
/* 506 */       return retainValues(c);
/*     */     }
/*     */     
/*     */     public int size() {
/* 510 */       return DoubleWeakHashMap.this.size();
/*     */     }
/*     */     
/*     */     public Object[] toArray() {
/* 514 */       DoubleWeakHashMap.this.cleanCleared();
/* 515 */       return new ArrayList(this).toArray();
/*     */     }
/*     */     
/*     */     public Object[] toArray(Object[] array)
/*     */     {
/* 520 */       DoubleWeakHashMap.this.cleanCleared();
/* 521 */       return new ArrayList(this).toArray(array);
/*     */     }
/*     */     
/*     */     private boolean removeValue(Object val)
/*     */     {
/* 526 */       boolean out = false;
/* 527 */       for (Iterator ii = DoubleWeakHashMap.this.inner.values().iterator(); ii.hasNext();)
/*     */       {
/* 529 */         WVal wv = (WVal)ii.next();
/* 530 */         if (val.equals(wv.get()))
/*     */         {
/* 532 */           ii.remove();
/* 533 */           out = true;
/*     */         }
/*     */       }
/* 536 */       return out;
/*     */     }
/*     */     
/*     */     private boolean retainValues(Collection c)
/*     */     {
/* 541 */       boolean out = false;
/* 542 */       for (Iterator ii = DoubleWeakHashMap.this.inner.values().iterator(); ii.hasNext();)
/*     */       {
/* 544 */         WVal wv = (WVal)ii.next();
/* 545 */         if (!c.contains(wv.get()))
/*     */         {
/* 547 */           ii.remove();
/* 548 */           out = true;
/*     */         }
/*     */       }
/* 551 */       return out;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\util\DoubleWeakHashMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */