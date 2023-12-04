/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
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
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingTable<R, C, V>
/*     */   extends ForwardingObject
/*     */   implements Table<R, C, V>
/*     */ {
/*     */   protected abstract Table<R, C, V> delegate();
/*     */   
/*     */   public Set<Cell<R, C, V>> cellSet()
/*     */   {
/*  44 */     return delegate().cellSet();
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*  49 */     delegate().clear();
/*     */   }
/*     */   
/*     */   public Map<R, V> column(C columnKey)
/*     */   {
/*  54 */     return delegate().column(columnKey);
/*     */   }
/*     */   
/*     */   public Set<C> columnKeySet()
/*     */   {
/*  59 */     return delegate().columnKeySet();
/*     */   }
/*     */   
/*     */   public Map<C, Map<R, V>> columnMap()
/*     */   {
/*  64 */     return delegate().columnMap();
/*     */   }
/*     */   
/*     */   public boolean contains(Object rowKey, Object columnKey)
/*     */   {
/*  69 */     return delegate().contains(rowKey, columnKey);
/*     */   }
/*     */   
/*     */   public boolean containsColumn(Object columnKey)
/*     */   {
/*  74 */     return delegate().containsColumn(columnKey);
/*     */   }
/*     */   
/*     */   public boolean containsRow(Object rowKey)
/*     */   {
/*  79 */     return delegate().containsRow(rowKey);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object value)
/*     */   {
/*  84 */     return delegate().containsValue(value);
/*     */   }
/*     */   
/*     */   public V get(Object rowKey, Object columnKey)
/*     */   {
/*  89 */     return (V)delegate().get(rowKey, columnKey);
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/*  94 */     return delegate().isEmpty();
/*     */   }
/*     */   
/*     */   public V put(R rowKey, C columnKey, V value)
/*     */   {
/*  99 */     return (V)delegate().put(rowKey, columnKey, value);
/*     */   }
/*     */   
/*     */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 104 */     delegate().putAll(table);
/*     */   }
/*     */   
/*     */   public V remove(Object rowKey, Object columnKey)
/*     */   {
/* 109 */     return (V)delegate().remove(rowKey, columnKey);
/*     */   }
/*     */   
/*     */   public Map<C, V> row(R rowKey)
/*     */   {
/* 114 */     return delegate().row(rowKey);
/*     */   }
/*     */   
/*     */   public Set<R> rowKeySet()
/*     */   {
/* 119 */     return delegate().rowKeySet();
/*     */   }
/*     */   
/*     */   public Map<R, Map<C, V>> rowMap()
/*     */   {
/* 124 */     return delegate().rowMap();
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/* 129 */     return delegate().size();
/*     */   }
/*     */   
/*     */   public Collection<V> values()
/*     */   {
/* 134 */     return delegate().values();
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj) {
/* 138 */     return (obj == this) || (delegate().equals(obj));
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 142 */     return delegate().hashCode();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */