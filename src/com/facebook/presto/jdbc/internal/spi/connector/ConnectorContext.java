/*    */ package com.facebook.presto.jdbc.internal.spi.connector;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.NodeManager;
/*    */ import com.facebook.presto.jdbc.internal.spi.PageIndexerFactory;
/*    */ import com.facebook.presto.jdbc.internal.spi.PageSorter;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
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
/*    */ public abstract interface ConnectorContext
/*    */ {
/*    */   public NodeManager getNodeManager()
/*    */   {
/* 25 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public TypeManager getTypeManager()
/*    */   {
/* 30 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public PageSorter getPageSorter()
/*    */   {
/* 35 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public PageIndexerFactory getPageIndexerFactory()
/*    */   {
/* 40 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */