/*    */ package com.mchange.v2.c3p0.stmt;
/*    */ 
/*    */ import com.mchange.v2.async.AsynchronousRunner;
/*    */ import java.sql.Connection;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PerConnectionMaxOnlyStatementCache
/*    */   extends GooGooStatementCache
/*    */ {
/*    */   int max_statements_per_connection;
/*    */   DeathmarchConnectionStatementManager dcsm;
/*    */   
/*    */   public PerConnectionMaxOnlyStatementCache(AsynchronousRunner blockingTaskAsyncRunner, int max_statements_per_connection)
/*    */   {
/* 37 */     super(blockingTaskAsyncRunner);
/* 38 */     this.max_statements_per_connection = max_statements_per_connection;
/*    */   }
/*    */   
/*    */   protected ConnectionStatementManager createConnectionStatementManager()
/*    */   {
/* 43 */     return this.dcsm = new DeathmarchConnectionStatementManager(this);
/*    */   }
/*    */   
/*    */   void addStatementToDeathmarches(Object pstmt, Connection physicalConnection) {
/* 47 */     this.dcsm.getDeathmarch(physicalConnection).deathmarchStatement(pstmt);
/*    */   }
/*    */   
/* 50 */   void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection) { this.dcsm.getDeathmarch(physicalConnection).undeathmarchStatement(pstmt); }
/*    */   
/*    */   boolean prepareAssimilateNewStatement(Connection pcon)
/*    */   {
/* 54 */     int cxn_stmt_count = this.dcsm.getNumStatementsForConnection(pcon);
/* 55 */     return (cxn_stmt_count < this.max_statements_per_connection) || ((cxn_stmt_count == this.max_statements_per_connection) && (this.dcsm.getDeathmarch(pcon).cullNext()));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\PerConnectionMaxOnlyStatementCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */