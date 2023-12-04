/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class ConcurrentHashSet<E>
/*     */   extends AbstractSet<E>
/*     */   implements Set<E>
/*     */ {
/*  30 */   private final Map<E, Boolean> _map = new ConcurrentHashMap();
/*  31 */   private transient Set<E> _keys = this._map.keySet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean add(E e)
/*     */   {
/*  40 */     return this._map.put(e, Boolean.TRUE) == null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/*  46 */     this._map.clear();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/*  52 */     return this._map.containsKey(o);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsAll(Collection<?> c)
/*     */   {
/*  58 */     return this._keys.containsAll(c);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  64 */     return (o == this) || (this._keys.equals(o));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  70 */     return this._keys.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  76 */     return this._map.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<E> iterator()
/*     */   {
/*  82 */     return this._keys.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/*  88 */     return this._map.remove(o) != null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean removeAll(Collection<?> c)
/*     */   {
/*  94 */     return this._keys.removeAll(c);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean retainAll(Collection<?> c)
/*     */   {
/* 100 */     return this._keys.retainAll(c);
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 106 */     return this._map.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 112 */     return this._keys.toArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 118 */     return this._keys.toArray(a);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 124 */     return this._keys.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ConcurrentHashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */