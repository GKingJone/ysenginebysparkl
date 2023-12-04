/*    */ package com.facebook.presto.jdbc.internal.spi.connector;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSplitSource;
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayoutHandle;
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
/*    */ public abstract interface ConnectorSplitManager
/*    */ {
/*    */   public ConnectorSplitSource getSplits(ConnectorTransactionHandle transactionHandle, ConnectorSession session, ConnectorTableLayoutHandle layout)
/*    */   {
/* 24 */     throw new UnsupportedOperationException("not yet implemented");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorSplitManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */