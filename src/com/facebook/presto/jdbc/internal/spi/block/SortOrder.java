/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
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
/*    */ public enum SortOrder
/*    */ {
/* 20 */   ASC_NULLS_FIRST(true, true), 
/* 21 */   ASC_NULLS_LAST(true, false), 
/* 22 */   DESC_NULLS_FIRST(false, true), 
/* 23 */   DESC_NULLS_LAST(false, false);
/*    */   
/*    */   private final boolean ascending;
/*    */   private final boolean nullsFirst;
/*    */   
/*    */   private SortOrder(boolean ascending, boolean nullsFirst)
/*    */   {
/* 30 */     this.ascending = ascending;
/* 31 */     this.nullsFirst = nullsFirst;
/*    */   }
/*    */   
/*    */   public boolean isAscending()
/*    */   {
/* 36 */     return this.ascending;
/*    */   }
/*    */   
/*    */   public boolean isNullsFirst()
/*    */   {
/* 41 */     return this.nullsFirst;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int compareBlockValue(Type type, Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 49 */     boolean leftIsNull = leftBlock.isNull(leftPosition);
/* 50 */     boolean rightIsNull = rightBlock.isNull(rightPosition);
/*    */     
/* 52 */     if ((leftIsNull) && (rightIsNull)) {
/* 53 */       return 0;
/*    */     }
/* 55 */     if (leftIsNull) {
/* 56 */       return this.nullsFirst ? -1 : 1;
/*    */     }
/* 58 */     if (rightIsNull) {
/* 59 */       return this.nullsFirst ? 1 : -1;
/*    */     }
/*    */     
/* 62 */     int result = type.compareTo(leftBlock, leftPosition, rightBlock, rightPosition);
/* 63 */     return this.ascending ? result : -result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\SortOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */