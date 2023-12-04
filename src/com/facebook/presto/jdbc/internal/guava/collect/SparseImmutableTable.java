/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.concurrent.Immutable;
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
/*     */ @Immutable
/*     */ final class SparseImmutableTable<R, C, V>
/*     */   extends RegularImmutableTable<R, C, V>
/*     */ {
/*     */   private final ImmutableMap<R, Map<C, V>> rowMap;
/*     */   private final ImmutableMap<C, Map<R, V>> columnMap;
/*     */   private final int[] iterationOrderRow;
/*     */   private final int[] iterationOrderColumn;
/*     */   
/*     */   SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace)
/*     */   {
/*  39 */     Map<R, Integer> rowIndex = Maps.newHashMap();
/*  40 */     Map<R, Map<C, V>> rows = Maps.newLinkedHashMap();
/*  41 */     for (R row : rowSpace) {
/*  42 */       rowIndex.put(row, Integer.valueOf(rows.size()));
/*  43 */       rows.put(row, new LinkedHashMap());
/*     */     }
/*  45 */     Map<C, Map<R, V>> columns = Maps.newLinkedHashMap();
/*  46 */     for (C col : columnSpace) {
/*  47 */       columns.put(col, new LinkedHashMap());
/*     */     }
/*  49 */     int[] iterationOrderRow = new int[cellList.size()];
/*  50 */     int[] iterationOrderColumn = new int[cellList.size()];
/*  51 */     for (int i = 0; i < cellList.size(); i++) {
/*  52 */       Table.Cell<R, C, V> cell = (Table.Cell)cellList.get(i);
/*  53 */       R rowKey = cell.getRowKey();
/*  54 */       C columnKey = cell.getColumnKey();
/*  55 */       V value = cell.getValue();
/*     */       
/*  57 */       iterationOrderRow[i] = ((Integer)rowIndex.get(rowKey)).intValue();
/*  58 */       Map<C, V> thisRow = (Map)rows.get(rowKey);
/*  59 */       iterationOrderColumn[i] = thisRow.size();
/*  60 */       V oldValue = thisRow.put(columnKey, value);
/*  61 */       if (oldValue != null) {
/*  62 */         String str1 = String.valueOf(String.valueOf(rowKey));String str2 = String.valueOf(String.valueOf(columnKey));String str3 = String.valueOf(String.valueOf(value));String str4 = String.valueOf(String.valueOf(oldValue));throw new IllegalArgumentException(37 + str1.length() + str2.length() + str3.length() + str4.length() + "Duplicate value for row=" + str1 + ", column=" + str2 + ": " + str3 + ", " + str4);
/*     */       }
/*     */       
/*  65 */       ((Map)columns.get(columnKey)).put(rowKey, value);
/*     */     }
/*  67 */     this.iterationOrderRow = iterationOrderRow;
/*  68 */     this.iterationOrderColumn = iterationOrderColumn;
/*  69 */     ImmutableMap.Builder<R, Map<C, V>> rowBuilder = ImmutableMap.builder();
/*  70 */     for (Entry<R, Map<C, V>> row : rows.entrySet()) {
/*  71 */       rowBuilder.put(row.getKey(), ImmutableMap.copyOf((Map)row.getValue()));
/*     */     }
/*  73 */     this.rowMap = rowBuilder.build();
/*     */     
/*  75 */     ImmutableMap.Builder<C, Map<R, V>> columnBuilder = ImmutableMap.builder();
/*  76 */     for (Entry<C, Map<R, V>> col : columns.entrySet()) {
/*  77 */       columnBuilder.put(col.getKey(), ImmutableMap.copyOf((Map)col.getValue()));
/*     */     }
/*  79 */     this.columnMap = columnBuilder.build();
/*     */   }
/*     */   
/*     */   public ImmutableMap<C, Map<R, V>> columnMap() {
/*  83 */     return this.columnMap;
/*     */   }
/*     */   
/*     */   public ImmutableMap<R, Map<C, V>> rowMap() {
/*  87 */     return this.rowMap;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  92 */     return this.iterationOrderRow.length;
/*     */   }
/*     */   
/*     */   Table.Cell<R, C, V> getCell(int index)
/*     */   {
/*  97 */     int rowIndex = this.iterationOrderRow[index];
/*  98 */     Entry<R, Map<C, V>> rowEntry = (Entry)this.rowMap.entrySet().asList().get(rowIndex);
/*  99 */     ImmutableMap<C, V> row = (ImmutableMap)rowEntry.getValue();
/* 100 */     int columnIndex = this.iterationOrderColumn[index];
/* 101 */     Entry<C, V> colEntry = (Entry)row.entrySet().asList().get(columnIndex);
/* 102 */     return cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
/*     */   }
/*     */   
/*     */   V getValue(int index)
/*     */   {
/* 107 */     int rowIndex = this.iterationOrderRow[index];
/* 108 */     ImmutableMap<C, V> row = (ImmutableMap)this.rowMap.values().asList().get(rowIndex);
/* 109 */     int columnIndex = this.iterationOrderColumn[index];
/* 110 */     return (V)row.values().asList().get(columnIndex);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\SparseImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */