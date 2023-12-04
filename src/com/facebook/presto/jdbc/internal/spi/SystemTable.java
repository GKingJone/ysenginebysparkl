/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorTransactionHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
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
/*    */ public abstract interface SystemTable
/*    */ {
/*    */   public abstract Distribution getDistribution();
/*    */   
/*    */   public abstract ConnectorTableMetadata getTableMetadata();
/*    */   
/*    */   public static enum Distribution
/*    */   {
/* 26 */     ALL_NODES,  ALL_COORDINATORS,  SINGLE_COORDINATOR;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     private Distribution() {}
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public RecordCursor cursor(ConnectorTransactionHandle transactionHandle, ConnectorSession session, TupleDomain<Integer> constraint)
/*    */   {
/* 41 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ConnectorPageSource pageSource(ConnectorTransactionHandle transactionHandle, ConnectorSession session, TupleDomain<Integer> constraint)
/*    */   {
/* 52 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SystemTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */