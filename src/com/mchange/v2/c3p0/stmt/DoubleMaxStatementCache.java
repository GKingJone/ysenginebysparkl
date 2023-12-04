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
/*    */ public final class DoubleMaxStatementCache
/*    */   extends GooGooStatementCache
/*    */ {
/*    */   int max_statements;
/* 33 */   Deathmarch globalDeathmarch = new Deathmarch(this);
/*    */   
/*    */   int max_statements_per_connection;
/*    */   DeathmarchConnectionStatementManager dcsm;
/*    */   
/*    */   public DoubleMaxStatementCache(AsynchronousRunner blockingTaskAsyncRunner, int max_statements, int max_statements_per_connection)
/*    */   {
/* 40 */     super(blockingTaskAsyncRunner);
/* 41 */     this.max_statements = max_statements;
/* 42 */     this.max_statements_per_connection = max_statements_per_connection;
/*    */   }
/*    */   
/*    */   protected ConnectionStatementManager createConnectionStatementManager()
/*    */   {
/* 47 */     return this.dcsm = new DeathmarchConnectionStatementManager(this);
/*    */   }
/*    */   
/*    */   void addStatementToDeathmarches(Object pstmt, Connection physicalConnection)
/*    */   {
/* 52 */     this.globalDeathmarch.deathmarchStatement(pstmt);
/* 53 */     this.dcsm.getDeathmarch(physicalConnection).deathmarchStatement(pstmt);
/*    */   }
/*    */   
/*    */   void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection)
/*    */   {
/* 58 */     this.globalDeathmarch.undeathmarchStatement(pstmt);
/* 59 */     this.dcsm.getDeathmarch(physicalConnection).undeathmarchStatement(pstmt);
/*    */   }
/*    */   
/*    */   boolean prepareAssimilateNewStatement(Connection pcon)
/*    */   {
/* 64 */     int cxn_stmt_count = this.dcsm.getNumStatementsForConnection(pcon);
/* 65 */     if (cxn_stmt_count < this.max_statements_per_connection)
/*    */     {
/* 67 */       int global_size = countCachedStatements();
/* 68 */       return (global_size < this.max_statements) || ((global_size == this.max_statements) && (this.globalDeathmarch.cullNext()));
/*    */     }
/*    */     
/* 71 */     return (cxn_stmt_count == this.max_statements_per_connection) && (this.dcsm.getDeathmarch(pcon).cullNext());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\DoubleMaxStatementCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */