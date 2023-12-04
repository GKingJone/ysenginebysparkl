/*     */ package com.mchange.v1.identicator;
/*     */ 
/*     */ import com.mchange.v1.util.WrapperIterator;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
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
/*     */ public final class IdWeakHashMap
/*     */   extends IdMap
/*     */   implements Map
/*     */ {
/*     */   ReferenceQueue rq;
/*     */   
/*     */   public IdWeakHashMap(Identicator id)
/*     */   {
/*  39 */     super(new HashMap(), id);
/*  40 */     this.rq = new ReferenceQueue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/*  51 */     cleanCleared();
/*  52 */     return super.size();
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/*     */     try {
/*  58 */       return super.isEmpty();
/*     */     } finally {
/*  60 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean containsKey(Object o) {
/*     */     try {
/*  66 */       return super.containsKey(o);
/*     */     } finally {
/*  68 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object o) {
/*     */     try {
/*  74 */       return super.containsValue(o);
/*     */     } finally {
/*  76 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object get(Object o) {
/*     */     try {
/*  82 */       return super.get(o);
/*     */     } finally {
/*  84 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object put(Object k, Object v) {
/*     */     try {
/*  90 */       return super.put(k, v);
/*     */     } finally {
/*  92 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object remove(Object o) {
/*     */     try {
/*  98 */       return super.remove(o);
/*     */     } finally {
/* 100 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public void putAll(Map m) {
/*     */     try {
/* 106 */       super.putAll(m);
/*     */     } finally {
/* 108 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear() {
/*     */     try {
/* 114 */       super.clear();
/*     */     } finally {
/* 116 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public Set keySet() {
/*     */     try {
/* 122 */       return super.keySet();
/*     */     } finally {
/* 124 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public Collection values() {
/*     */     try {
/* 130 */       return super.values();
/*     */     } finally {
/* 132 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set entrySet()
/*     */   {
/*     */     try
/*     */     {
/* 143 */       return new WeakUserEntrySet(null);
/*     */     } finally {
/* 145 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/*     */     try {
/* 151 */       return super.equals(o);
/*     */     } finally {
/* 153 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*     */     try {
/* 159 */       return super.hashCode();
/*     */     } finally {
/* 161 */       cleanCleared();
/*     */     }
/*     */   }
/*     */   
/*     */   protected IdHashKey createIdKey(Object o) {
/* 166 */     return new WeakIdHashKey(o, this.id, this.rq);
/*     */   }
/*     */   
/*     */   private void cleanCleared() {
/*     */     WeakIdHashKey.Ref ref;
/* 171 */     while ((ref = (WeakIdHashKey.Ref)this.rq.poll()) != null)
/* 172 */       removeIdHashKey(ref.getKey());
/*     */   }
/*     */   
/* 175 */   private final class WeakUserEntrySet extends AbstractSet { WeakUserEntrySet(IdWeakHashMap.1 x1) { this(); }
/*     */     
/* 177 */     Set innerEntries = IdWeakHashMap.this.internalEntrySet();
/*     */     
/*     */     public Iterator iterator()
/*     */     {
/*     */       try
/*     */       {
/* 183 */         new WrapperIterator(this.innerEntries.iterator(), true)
/*     */         {
/*     */           protected Object transformObject(Object o)
/*     */           {
/* 187 */             Entry innerEntry = (Entry)o;
/* 188 */             final Object userKey = ((IdHashKey)innerEntry.getKey()).getKeyObj();
/* 189 */             if (userKey == null) {
/* 190 */               return WrapperIterator.SKIP_TOKEN;
/*     */             }
/* 192 */             new UserEntry(innerEntry) {
/* 193 */               Object preventRefClear = userKey;
/*     */             };
/*     */           }
/*     */         };
/*     */       } finally {
/* 198 */         IdWeakHashMap.this.cleanCleared();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int size()
/*     */     {
/* 208 */       IdWeakHashMap.this.cleanCleared();
/* 209 */       return this.innerEntries.size();
/*     */     }
/*     */     
/*     */     public boolean contains(Object o)
/*     */     {
/*     */       try {
/*     */         Entry entry;
/* 216 */         if ((o instanceof Map.Entry))
/*     */         {
/* 218 */           entry = (Entry)o;
/* 219 */           return this.innerEntries.contains(IdWeakHashMap.this.createIdEntry(entry));
/*     */         }
/*     */         
/* 222 */         return 0;
/*     */       }
/*     */       finally {
/* 225 */         IdWeakHashMap.this.cleanCleared();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean remove(Object o) {
/*     */       try {
/*     */         Entry entry;
/* 232 */         if ((o instanceof Map.Entry))
/*     */         {
/* 234 */           entry = (Entry)o;
/* 235 */           return this.innerEntries.remove(IdWeakHashMap.this.createIdEntry(entry));
/*     */         }
/*     */         
/* 238 */         return 0;
/*     */       }
/*     */       finally {
/* 241 */         IdWeakHashMap.this.cleanCleared();
/*     */       }
/*     */     }
/*     */     
/*     */     public void clear() {
/*     */       try {
/* 247 */         IdWeakHashMap.this.inner.clear();
/*     */       } finally {
/* 249 */         IdWeakHashMap.this.cleanCleared();
/*     */       }
/*     */     }
/*     */     
/*     */     private WeakUserEntrySet() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\IdWeakHashMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */