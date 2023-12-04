/*     */ package com.facebook.presto.jdbc.internal.spi.connector;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.SystemTable;
/*     */ import com.facebook.presto.jdbc.internal.spi.procedure.Procedure;
/*     */ import com.facebook.presto.jdbc.internal.spi.session.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.transaction.IsolationLevel;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public abstract interface Connector
/*     */ {
/*     */   public abstract ConnectorTransactionHandle beginTransaction(IsolationLevel paramIsolationLevel, boolean paramBoolean);
/*     */   
/*     */   public abstract ConnectorMetadata getMetadata(ConnectorTransactionHandle paramConnectorTransactionHandle);
/*     */   
/*     */   public abstract ConnectorSplitManager getSplitManager();
/*     */   
/*     */   public ConnectorPageSourceProvider getPageSourceProvider()
/*     */   {
/*  44 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorRecordSetProvider getRecordSetProvider()
/*     */   {
/*  52 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorPageSinkProvider getPageSinkProvider()
/*     */   {
/*  60 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorRecordSinkProvider getRecordSinkProvider()
/*     */   {
/*  68 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorIndexProvider getIndexProvider()
/*     */   {
/*  76 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorNodePartitioningProvider getNodePartitioningProvider()
/*     */   {
/*  84 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<SystemTable> getSystemTables()
/*     */   {
/*  92 */     return Collections.emptySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<Procedure> getProcedures()
/*     */   {
/* 100 */     return Collections.emptySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getSessionProperties()
/*     */   {
/* 108 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getSchemaProperties()
/*     */   {
/* 116 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getTableProperties()
/*     */   {
/* 124 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorAccessControl getAccessControl()
/*     */   {
/* 132 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void commit(ConnectorTransactionHandle transactionHandle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void rollback(ConnectorTransactionHandle transactionHandle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSingleStatementWritesOnly()
/*     */   {
/* 157 */     return false;
/*     */   }
/*     */   
/*     */   public void shutdown() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\Connector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */