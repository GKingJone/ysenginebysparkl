/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
/*    */ import java.util.List;
/*    */ import java.util.Set;
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
/*    */ @Deprecated
/*    */ public abstract interface ConnectorIndexResolver
/*    */ {
/*    */   public abstract ConnectorResolvedIndex resolveIndex(ConnectorSession paramConnectorSession, ConnectorTableHandle paramConnectorTableHandle, Set<ColumnHandle> paramSet, TupleDomain<ColumnHandle> paramTupleDomain);
/*    */   
/*    */   public ConnectorResolvedIndex resolveIndex(ConnectorSession session, ConnectorTableHandle tableHandle, Set<ColumnHandle> indexableColumns, Set<ColumnHandle> outputColumns, TupleDomain<ColumnHandle> tupleDomain)
/*    */   {
/* 30 */     return resolveIndex(session, tableHandle, indexableColumns, tupleDomain);
/*    */   }
/*    */   
/*    */   public abstract ConnectorIndex getIndex(ConnectorSession paramConnectorSession, ConnectorIndexHandle paramConnectorIndexHandle, List<ColumnHandle> paramList1, List<ColumnHandle> paramList2);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorIndexResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */