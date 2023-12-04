/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ @GwtCompatible
/*     */ abstract class AbstractTable<R, C, V>
/*     */   implements Table<R, C, V>
/*     */ {
/*     */   private transient Set<Cell<R, C, V>> cellSet;
/*     */   private transient Collection<V> values;
/*     */   
/*     */   public boolean containsRow(@Nullable Object rowKey)
/*     */   {
/*  38 */     return Maps.safeContainsKey(rowMap(), rowKey);
/*     */   }
/*     */   
/*     */   public boolean containsColumn(@Nullable Object columnKey)
/*     */   {
/*  43 */     return Maps.safeContainsKey(columnMap(), columnKey);
/*     */   }
/*     */   
/*     */   public Set<R> rowKeySet()
/*     */   {
/*  48 */     return rowMap().keySet();
/*     */   }
/*     */   
/*     */   public Set<C> columnKeySet()
/*     */   {
/*  53 */     return columnMap().keySet();
/*     */   }
/*     */   
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/*  58 */     for (Map<C, V> row : rowMap().values()) {
/*  59 */       if (row.containsValue(value)) {
/*  60 */         return true;
/*     */       }
/*     */     }
/*  63 */     return false;
/*     */   }
/*     */   
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  68 */     Map<C, V> row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  69 */     return (row != null) && (Maps.safeContainsKey(row, columnKey));
/*     */   }
/*     */   
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  74 */     Map<C, V> row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  75 */     return row == null ? null : Maps.safeGet(row, columnKey);
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/*  80 */     return size() == 0;
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*  85 */     Iterators.clear(cellSet().iterator());
/*     */   }
/*     */   
/*     */   public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  90 */     Map<C, V> row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  91 */     return row == null ? null : Maps.safeRemove(row, columnKey);
/*     */   }
/*     */   
/*     */   public V put(R rowKey, C columnKey, V value)
/*     */   {
/*  96 */     return (V)row(rowKey).put(columnKey, value);
/*     */   }
/*     */   
/*     */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 101 */     for (Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
/* 102 */       put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Set<Cell<R, C, V>> cellSet()
/*     */   {
/* 110 */     Set<Cell<R, C, V>> result = this.cellSet;
/* 111 */     return result == null ? (this.cellSet = createCellSet()) : result;
/*     */   }
/*     */   
/*     */ 
/* 115 */   Set<Cell<R, C, V>> createCellSet() { return new CellSet(); }
/*     */   
/*     */   abstract Iterator<Cell<R, C, V>> cellIterator();
/*     */   
/*     */   class CellSet extends AbstractSet<Cell<R, C, V>> {
/*     */     CellSet() {}
/*     */     
/*     */     public boolean contains(Object o) {
/* 123 */       if ((o instanceof Table.Cell)) {
/* 124 */         Cell<?, ?, ?> cell = (Cell)o;
/* 125 */         Map<C, V> row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
/* 126 */         return (row != null) && (Collections2.safeContains(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue())));
/*     */       }
/*     */       
/* 129 */       return false;
/*     */     }
/*     */     
/*     */     public boolean remove(@Nullable Object o)
/*     */     {
/* 134 */       if ((o instanceof Table.Cell)) {
/* 135 */         Cell<?, ?, ?> cell = (Cell)o;
/* 136 */         Map<C, V> row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
/* 137 */         return (row != null) && (Collections2.safeRemove(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue())));
/*     */       }
/*     */       
/* 140 */       return false;
/*     */     }
/*     */     
/*     */     public void clear()
/*     */     {
/* 145 */       AbstractTable.this.clear();
/*     */     }
/*     */     
/*     */     public Iterator<Cell<R, C, V>> iterator()
/*     */     {
/* 150 */       return AbstractTable.this.cellIterator();
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 155 */       return AbstractTable.this.size();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 163 */     Collection<V> result = this.values;
/* 164 */     return result == null ? (this.values = createValues()) : result;
/*     */   }
/*     */   
/*     */   Collection<V> createValues() {
/* 168 */     return new Values();
/*     */   }
/*     */   
/*     */   Iterator<V> valuesIterator() {
/* 172 */     new TransformedIterator(cellSet().iterator())
/*     */     {
/*     */       V transform(Cell<R, C, V> cell) {
/* 175 */         return (V)cell.getValue();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   class Values extends AbstractCollection<V> {
/*     */     Values() {}
/*     */     
/* 183 */     public Iterator<V> iterator() { return AbstractTable.this.valuesIterator(); }
/*     */     
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 188 */       return AbstractTable.this.containsValue(o);
/*     */     }
/*     */     
/*     */     public void clear()
/*     */     {
/* 193 */       AbstractTable.this.clear();
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 198 */       return AbstractTable.this.size();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object obj) {
/* 203 */     return Tables.equalsImpl(this, obj);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 207 */     return cellSet().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     return rowMap().toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */