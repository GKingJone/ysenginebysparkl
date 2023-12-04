/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPartitioningHandle;
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorTransactionHandle;
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
/*    */ public abstract interface ConnectorHandleResolver
/*    */ {
/*    */   public abstract Class<? extends ConnectorTableHandle> getTableHandleClass();
/*    */   
/*    */   public abstract Class<? extends ConnectorTableLayoutHandle> getTableLayoutHandleClass();
/*    */   
/*    */   public abstract Class<? extends ColumnHandle> getColumnHandleClass();
/*    */   
/*    */   public abstract Class<? extends ConnectorSplit> getSplitClass();
/*    */   
/*    */   public Class<? extends ConnectorIndexHandle> getIndexHandleClass()
/*    */   {
/* 31 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public Class<? extends ConnectorOutputTableHandle> getOutputTableHandleClass()
/*    */   {
/* 36 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public Class<? extends ConnectorInsertTableHandle> getInsertTableHandleClass()
/*    */   {
/* 41 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public Class<? extends ConnectorPartitioningHandle> getPartitioningHandleClass()
/*    */   {
/* 46 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public Class<? extends ConnectorTransactionHandle> getTransactionHandleClass()
/*    */   {
/* 51 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorHandleResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */