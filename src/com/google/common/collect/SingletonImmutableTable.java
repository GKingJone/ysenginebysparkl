/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ class SingletonImmutableTable<R, C, V>
/*    */   extends ImmutableTable<R, C, V>
/*    */ {
/*    */   final R singleRowKey;
/*    */   final C singleColumnKey;
/*    */   final V singleValue;
/*    */   
/*    */   SingletonImmutableTable(R rowKey, C columnKey, V value)
/*    */   {
/* 37 */     this.singleRowKey = Preconditions.checkNotNull(rowKey);
/* 38 */     this.singleColumnKey = Preconditions.checkNotNull(columnKey);
/* 39 */     this.singleValue = Preconditions.checkNotNull(value);
/*    */   }
/*    */   
/*    */   SingletonImmutableTable(Cell<R, C, V> cell) {
/* 43 */     this(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*    */   }
/*    */   
/*    */   public ImmutableMap<R, V> column(C columnKey) {
/* 47 */     Preconditions.checkNotNull(columnKey);
/* 48 */     return containsColumn(columnKey) ? ImmutableMap.of(this.singleRowKey, this.singleValue) : ImmutableMap.of();
/*    */   }
/*    */   
/*    */ 
/*    */   public ImmutableMap<C, Map<R, V>> columnMap()
/*    */   {
/* 54 */     return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
/*    */   }
/*    */   
/*    */   public ImmutableMap<R, Map<C, V>> rowMap()
/*    */   {
/* 59 */     return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 64 */     return 1;
/*    */   }
/*    */   
/*    */   ImmutableSet<Cell<R, C, V>> createCellSet()
/*    */   {
/* 69 */     return ImmutableSet.of(cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
/*    */   }
/*    */   
/*    */   ImmutableCollection<V> createValues()
/*    */   {
/* 74 */     return ImmutableSet.of(this.singleValue);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\SingletonImmutableTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */