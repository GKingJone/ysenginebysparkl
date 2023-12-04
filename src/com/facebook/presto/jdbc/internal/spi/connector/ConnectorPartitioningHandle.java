/*    */ package com.facebook.presto.jdbc.internal.spi.connector;
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
/*    */ public abstract interface ConnectorPartitioningHandle
/*    */ {
/*    */   public boolean isSingleNode()
/*    */   {
/* 20 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isCoordinatorOnly()
/*    */   {
/* 25 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorPartitioningHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */