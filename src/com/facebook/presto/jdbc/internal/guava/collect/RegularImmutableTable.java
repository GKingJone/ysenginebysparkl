/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
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
/*     */ @GwtCompatible
/*     */ abstract class RegularImmutableTable<R, C, V>
/*     */   extends ImmutableTable<R, C, V>
/*     */ {
/*     */   abstract Cell<R, C, V> getCell(int paramInt);
/*     */   
/*  41 */   final ImmutableSet<Cell<R, C, V>> createCellSet() { return isEmpty() ? ImmutableSet.of() : new CellSet(null); }
/*     */   
/*     */   abstract V getValue(int paramInt);
/*     */   
/*     */   private final class CellSet extends ImmutableSet<Cell<R, C, V>> { private CellSet() {}
/*     */     
/*  47 */     public int size() { return RegularImmutableTable.this.size(); }
/*     */     
/*     */ 
/*     */     public UnmodifiableIterator<Cell<R, C, V>> iterator()
/*     */     {
/*  52 */       return asList().iterator();
/*     */     }
/*     */     
/*     */     ImmutableList<Cell<R, C, V>> createAsList()
/*     */     {
/*  57 */       new ImmutableAsList()
/*     */       {
/*     */         public Cell<R, C, V> get(int index) {
/*  60 */           return RegularImmutableTable.this.getCell(index);
/*     */         }
/*     */         
/*     */         ImmutableCollection<Cell<R, C, V>> delegateCollection()
/*     */         {
/*  65 */           return CellSet.this;
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/*  72 */       if ((object instanceof Table.Cell)) {
/*  73 */         Cell<?, ?, ?> cell = (Cell)object;
/*  74 */         Object value = RegularImmutableTable.this.get(cell.getRowKey(), cell.getColumnKey());
/*  75 */         return (value != null) && (value.equals(cell.getValue()));
/*     */       }
/*  77 */       return false;
/*     */     }
/*     */     
/*     */     boolean isPartialView()
/*     */     {
/*  82 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   final ImmutableCollection<V> createValues()
/*     */   {
/*  90 */     return isEmpty() ? ImmutableList.of() : new Values(null);
/*     */   }
/*     */   
/*     */   private final class Values extends ImmutableList<V> {
/*     */     private Values() {}
/*     */     
/*  96 */     public int size() { return RegularImmutableTable.this.size(); }
/*     */     
/*     */ 
/*     */     public V get(int index)
/*     */     {
/* 101 */       return (V)RegularImmutableTable.this.getValue(index);
/*     */     }
/*     */     
/*     */     boolean isPartialView()
/*     */     {
/* 106 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable final Comparator<? super C> columnComparator)
/*     */   {
/* 114 */     Preconditions.checkNotNull(cells);
/* 115 */     if ((rowComparator != null) || (columnComparator != null))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */       Comparator<Cell<R, C, V>> comparator = new Comparator() {
/*     */         public int compare(Cell<R, C, V> cell1, Cell<R, C, V> cell2) {
/* 125 */           int rowCompare = this.val$rowComparator == null ? 0 : this.val$rowComparator.compare(cell1.getRowKey(), cell2.getRowKey());
/*     */           
/* 127 */           if (rowCompare != 0) {
/* 128 */             return rowCompare;
/*     */           }
/* 130 */           return columnComparator == null ? 0 : columnComparator.compare(cell1.getColumnKey(), cell2.getColumnKey());
/*     */         }
/*     */         
/* 133 */       };
/* 134 */       Collections.sort(cells, comparator);
/*     */     }
/* 136 */     return forCellsInternal(cells, rowComparator, columnComparator);
/*     */   }
/*     */   
/*     */   static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Cell<R, C, V>> cells)
/*     */   {
/* 141 */     return forCellsInternal(cells, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator)
/*     */   {
/* 152 */     ImmutableSet.Builder<R> rowSpaceBuilder = ImmutableSet.builder();
/* 153 */     ImmutableSet.Builder<C> columnSpaceBuilder = ImmutableSet.builder();
/* 154 */     ImmutableList<Cell<R, C, V>> cellList = ImmutableList.copyOf(cells);
/* 155 */     for (Cell<R, C, V> cell : cellList) {
/* 156 */       rowSpaceBuilder.add(cell.getRowKey());
/* 157 */       columnSpaceBuilder.add(cell.getColumnKey());
/*     */     }
/*     */     
/* 160 */     ImmutableSet<R> rowSpace = rowSpaceBuilder.build();
/* 161 */     if (rowComparator != null) {
/* 162 */       List<R> rowList = Lists.newArrayList(rowSpace);
/* 163 */       Collections.sort(rowList, rowComparator);
/* 164 */       rowSpace = ImmutableSet.copyOf(rowList);
/*     */     }
/* 166 */     ImmutableSet<C> columnSpace = columnSpaceBuilder.build();
/* 167 */     if (columnComparator != null) {
/* 168 */       List<C> columnList = Lists.newArrayList(columnSpace);
/* 169 */       Collections.sort(columnList, columnComparator);
/* 170 */       columnSpace = ImmutableSet.copyOf(columnList);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 175 */     return cellList.size() > rowSpace.size() * columnSpace.size() / 2L ? new DenseImmutableTable(cellList, rowSpace, columnSpace) : new SparseImmutableTable(cellList, rowSpace, columnSpace);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RegularImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */