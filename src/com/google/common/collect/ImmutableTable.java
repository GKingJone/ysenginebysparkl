/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ @GwtCompatible
/*     */ public abstract class ImmutableTable<R, C, V>
/*     */   extends AbstractTable<R, C, V>
/*     */ {
/*  49 */   private static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <R, C, V> ImmutableTable<R, C, V> of()
/*     */   {
/*  57 */     return EMPTY;
/*     */   }
/*     */   
/*     */ 
/*     */   public static <R, C, V> ImmutableTable<R, C, V> of(R rowKey, C columnKey, V value)
/*     */   {
/*  63 */     return new SingletonImmutableTable(rowKey, columnKey, value);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/*  82 */     if ((table instanceof ImmutableTable))
/*     */     {
/*  84 */       ImmutableTable<R, C, V> parameterizedTable = (ImmutableTable)table;
/*     */       
/*  86 */       return parameterizedTable;
/*     */     }
/*  88 */     int size = table.size();
/*  89 */     switch (size) {
/*     */     case 0: 
/*  91 */       return of();
/*     */     case 1: 
/*  93 */       Cell<? extends R, ? extends C, ? extends V> onlyCell = (Cell)Iterables.getOnlyElement(table.cellSet());
/*     */       
/*  95 */       return of(onlyCell.getRowKey(), onlyCell.getColumnKey(), onlyCell.getValue());
/*     */     }
/*     */     
/*  98 */     ImmutableSet.Builder<Cell<R, C, V>> cellSetBuilder = ImmutableSet.builder();
/*     */     
/*     */ 
/* 101 */     for (Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 106 */       cellSetBuilder.add(cellOf(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
/*     */     }
/*     */     
/* 109 */     return RegularImmutableTable.forCells(cellSetBuilder.build());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <R, C, V> Builder<R, C, V> builder()
/*     */   {
/* 119 */     return new Builder();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static <R, C, V> Cell<R, C, V> cellOf(R rowKey, C columnKey, V value)
/*     */   {
/* 127 */     return Tables.immutableCell(Preconditions.checkNotNull(rowKey), Preconditions.checkNotNull(columnKey), Preconditions.checkNotNull(value));
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
/*     */   public static final class Builder<R, C, V>
/*     */   {
/* 158 */     private final List<Cell<R, C, V>> cells = Lists.newArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */     private Comparator<? super R> rowComparator;
/*     */     
/*     */ 
/*     */ 
/*     */     private Comparator<? super C> columnComparator;
/*     */     
/*     */ 
/*     */ 
/*     */     public Builder<R, C, V> orderRowsBy(Comparator<? super R> rowComparator)
/*     */     {
/* 172 */       this.rowComparator = ((Comparator)Preconditions.checkNotNull(rowComparator));
/* 173 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<R, C, V> orderColumnsBy(Comparator<? super C> columnComparator)
/*     */     {
/* 181 */       this.columnComparator = ((Comparator)Preconditions.checkNotNull(columnComparator));
/* 182 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<R, C, V> put(R rowKey, C columnKey, V value)
/*     */     {
/* 191 */       this.cells.add(ImmutableTable.cellOf(rowKey, columnKey, value));
/* 192 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<R, C, V> put(Cell<? extends R, ? extends C, ? extends V> cell)
/*     */     {
/* 202 */       if ((cell instanceof Tables.ImmutableCell)) {
/* 203 */         Preconditions.checkNotNull(cell.getRowKey());
/* 204 */         Preconditions.checkNotNull(cell.getColumnKey());
/* 205 */         Preconditions.checkNotNull(cell.getValue());
/*     */         
/* 207 */         Cell<R, C, V> immutableCell = cell;
/* 208 */         this.cells.add(immutableCell);
/*     */       } else {
/* 210 */         put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */       }
/* 212 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Builder<R, C, V> putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */     {
/* 224 */       for (Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
/* 225 */         put(cell);
/*     */       }
/* 227 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public ImmutableTable<R, C, V> build()
/*     */     {
/* 236 */       int size = this.cells.size();
/* 237 */       switch (size) {
/*     */       case 0: 
/* 239 */         return ImmutableTable.of();
/*     */       case 1: 
/* 241 */         return new SingletonImmutableTable((Cell)Iterables.getOnlyElement(this.cells));
/*     */       }
/*     */       
/* 244 */       return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableSet<Cell<R, C, V>> cellSet()
/*     */   {
/* 253 */     return (ImmutableSet)super.cellSet();
/*     */   }
/*     */   
/*     */ 
/*     */   abstract ImmutableSet<Cell<R, C, V>> createCellSet();
/*     */   
/*     */   final UnmodifiableIterator<Cell<R, C, V>> cellIterator()
/*     */   {
/* 261 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */   public ImmutableCollection<V> values()
/*     */   {
/* 266 */     return (ImmutableCollection)super.values();
/*     */   }
/*     */   
/*     */ 
/*     */   abstract ImmutableCollection<V> createValues();
/*     */   
/*     */   final Iterator<V> valuesIterator()
/*     */   {
/* 274 */     throw new AssertionError("should never be called");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableMap<R, V> column(C columnKey)
/*     */   {
/* 283 */     Preconditions.checkNotNull(columnKey);
/* 284 */     return (ImmutableMap)Objects.firstNonNull((ImmutableMap)columnMap().get(columnKey), ImmutableMap.of());
/*     */   }
/*     */   
/*     */ 
/*     */   public ImmutableSet<C> columnKeySet()
/*     */   {
/* 290 */     return columnMap().keySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ImmutableMap<C, Map<R, V>> columnMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableMap<C, V> row(R rowKey)
/*     */   {
/* 307 */     Preconditions.checkNotNull(rowKey);
/* 308 */     return (ImmutableMap)Objects.firstNonNull((ImmutableMap)rowMap().get(rowKey), ImmutableMap.of());
/*     */   }
/*     */   
/*     */ 
/*     */   public ImmutableSet<R> rowKeySet()
/*     */   {
/* 314 */     return rowMap().keySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ImmutableMap<R, Map<C, V>> rowMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 327 */     return get(rowKey, columnKey) != null;
/*     */   }
/*     */   
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 332 */     return values().contains(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final void clear()
/*     */   {
/* 342 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final V put(R rowKey, C columnKey, V value)
/*     */   {
/* 352 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 363 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final V remove(Object rowKey, Object columnKey)
/*     */   {
/* 373 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */