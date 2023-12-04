/*     */ package com.mchange.v1.identicator;
/*     */ 
/*     */ import com.mchange.v1.util.AbstractMapEntry;
/*     */ import com.mchange.v1.util.SimpleMapEntry;
/*     */ import com.mchange.v1.util.WrapperIterator;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
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
/*     */ abstract class IdMap
/*     */   extends AbstractMap
/*     */   implements Map
/*     */ {
/*     */   Map inner;
/*     */   Identicator id;
/*     */   
/*     */   protected IdMap(Map inner, Identicator id)
/*     */   {
/*  40 */     this.inner = inner;
/*  41 */     this.id = id;
/*     */   }
/*     */   
/*     */   public Object put(Object key, Object value) {
/*  45 */     return this.inner.put(createIdKey(key), value);
/*     */   }
/*     */   
/*  48 */   public boolean containsKey(Object key) { return this.inner.containsKey(createIdKey(key)); }
/*     */   
/*     */   public Object get(Object key) {
/*  51 */     return this.inner.get(createIdKey(key));
/*     */   }
/*     */   
/*  54 */   public Object remove(Object key) { return this.inner.remove(createIdKey(key)); }
/*     */   
/*     */   protected Object removeIdHashKey(IdHashKey idhk) {
/*  57 */     return this.inner.remove(idhk);
/*     */   }
/*     */   
/*  60 */   public Set entrySet() { return new UserEntrySet(null); }
/*     */   
/*     */   protected final Set internalEntrySet() {
/*  63 */     return this.inner.entrySet();
/*     */   }
/*     */   
/*     */   protected abstract IdHashKey createIdKey(Object paramObject);
/*     */   
/*  68 */   protected final Entry createIdEntry(Object key, Object val) { return new SimpleMapEntry(createIdKey(key), val); }
/*     */   
/*     */ 
/*  71 */   protected final Entry createIdEntry(Entry entry) { return createIdEntry(entry.getKey(), entry.getValue()); }
/*     */   
/*  73 */   private final class UserEntrySet extends AbstractSet { UserEntrySet(IdMap.1 x1) { this(); }
/*     */     
/*  75 */     Set innerEntries = IdMap.this.inner.entrySet();
/*     */     
/*     */     public Iterator iterator()
/*     */     {
/*  79 */       new WrapperIterator(this.innerEntries.iterator(), true)
/*     */       {
/*     */         protected Object transformObject(Object o) {
/*  82 */           return new UserEntry((Entry)o);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*  87 */     public int size() { return this.innerEntries.size(); }
/*     */     
/*     */     public boolean contains(Object o)
/*     */     {
/*  91 */       if ((o instanceof Map.Entry))
/*     */       {
/*  93 */         Entry entry = (Entry)o;
/*  94 */         return this.innerEntries.contains(IdMap.this.createIdEntry(entry));
/*     */       }
/*     */       
/*  97 */       return false;
/*     */     }
/*     */     
/*     */     public boolean remove(Object o)
/*     */     {
/* 102 */       if ((o instanceof Map.Entry))
/*     */       {
/* 104 */         Entry entry = (Entry)o;
/* 105 */         return this.innerEntries.remove(IdMap.this.createIdEntry(entry));
/*     */       }
/*     */       
/* 108 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 112 */     public void clear() { IdMap.this.inner.clear(); }
/*     */     
/*     */     private UserEntrySet() {}
/*     */   }
/*     */   
/*     */   protected static class UserEntry extends AbstractMapEntry {
/*     */     private Entry innerEntry;
/*     */     
/* 120 */     UserEntry(Entry innerEntry) { this.innerEntry = innerEntry; }
/*     */     
/*     */     public final Object getKey() {
/* 123 */       return ((IdHashKey)this.innerEntry.getKey()).getKeyObj();
/*     */     }
/*     */     
/* 126 */     public final Object getValue() { return this.innerEntry.getValue(); }
/*     */     
/*     */     public final Object setValue(Object value) {
/* 129 */       return this.innerEntry.setValue(value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\IdMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */