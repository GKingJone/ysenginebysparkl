/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.security.ConnectorAccessControl;
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
/*     */ @Deprecated
/*     */ public abstract interface Connector
/*     */ {
/*     */   public abstract ConnectorMetadata getMetadata();
/*     */   
/*     */   public abstract ConnectorSplitManager getSplitManager();
/*     */   
/*     */   public ConnectorPageSourceProvider getPageSourceProvider()
/*     */   {
/*  38 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorRecordSetProvider getRecordSetProvider()
/*     */   {
/*  46 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorPageSinkProvider getPageSinkProvider()
/*     */   {
/*  54 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorRecordSinkProvider getRecordSinkProvider()
/*     */   {
/*  62 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorIndexResolver getIndexResolver()
/*     */   {
/*  70 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<SystemTable> getSystemTables()
/*     */   {
/*  78 */     return Collections.emptySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getSessionProperties()
/*     */   {
/*  86 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getSchemaProperties()
/*     */   {
/*  94 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PropertyMetadata<?>> getTableProperties()
/*     */   {
/* 102 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorAccessControl getAccessControl()
/*     */   {
/* 110 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IsolationLevel getIsolationLevel()
/*     */   {
/* 118 */     return IsolationLevel.READ_UNCOMMITTED;
/*     */   }
/*     */   
/*     */   public void shutdown() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\Connector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */