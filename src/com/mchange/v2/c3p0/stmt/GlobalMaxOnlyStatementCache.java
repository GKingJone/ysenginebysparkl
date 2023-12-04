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
/*    */ public final class GlobalMaxOnlyStatementCache
/*    */   extends GooGooStatementCache
/*    */ {
/*    */   int max_statements;
/* 33 */   Deathmarch globalDeathmarch = new Deathmarch(this);
/*    */   
/*    */   public GlobalMaxOnlyStatementCache(AsynchronousRunner blockingTaskAsyncRunner, int max_statements)
/*    */   {
/* 37 */     super(blockingTaskAsyncRunner);
/* 38 */     this.max_statements = max_statements;
/*    */   }
/*    */   
/*    */   protected ConnectionStatementManager createConnectionStatementManager()
/*    */   {
/* 43 */     return new SimpleConnectionStatementManager();
/*    */   }
/*    */   
/*    */   void addStatementToDeathmarches(Object pstmt, Connection physicalConnection) {
/* 47 */     this.globalDeathmarch.deathmarchStatement(pstmt);
/*    */   }
/*    */   
/* 50 */   void removeStatementFromDeathmarches(Object pstmt, Connection physicalConnection) { this.globalDeathmarch.undeathmarchStatement(pstmt); }
/*    */   
/*    */   boolean prepareAssimilateNewStatement(Connection pcon)
/*    */   {
/* 54 */     int global_size = countCachedStatements();
/* 55 */     return (global_size < this.max_statements) || ((global_size == this.max_statements) && (this.globalDeathmarch.cullNext()));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\GlobalMaxOnlyStatementCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */