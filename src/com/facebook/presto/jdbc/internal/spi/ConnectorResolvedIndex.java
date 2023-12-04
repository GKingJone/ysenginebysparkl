/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
/*    */ import java.util.Objects;
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
/*    */ public class ConnectorResolvedIndex
/*    */ {
/*    */   private final ConnectorIndexHandle indexHandle;
/*    */   private final TupleDomain<ColumnHandle> unresolvedTupleDomain;
/*    */   
/*    */   public ConnectorResolvedIndex(ConnectorIndexHandle indexHandle, TupleDomain<ColumnHandle> unresolvedTupleDomain)
/*    */   {
/* 27 */     this.indexHandle = ((ConnectorIndexHandle)Objects.requireNonNull(indexHandle, "indexHandle is null"));
/* 28 */     this.unresolvedTupleDomain = ((TupleDomain)Objects.requireNonNull(unresolvedTupleDomain, "unresolvedTupleDomain is null"));
/*    */   }
/*    */   
/*    */   public ConnectorIndexHandle getIndexHandle()
/*    */   {
/* 33 */     return this.indexHandle;
/*    */   }
/*    */   
/*    */   public TupleDomain<ColumnHandle> getUnresolvedTupleDomain()
/*    */   {
/* 38 */     return this.unresolvedTupleDomain;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorResolvedIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */