/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.Statement;
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
/*    */ final class NullStatementSetManagedResultSet
/*    */   extends SetManagedResultSet
/*    */ {
/*    */   NullStatementSetManagedResultSet(Set activeResultSets)
/*    */   {
/* 40 */     super(activeResultSets);
/*    */   }
/*    */   
/* 43 */   NullStatementSetManagedResultSet(ResultSet inner, Set activeResultSets) { super(inner, activeResultSets); }
/*    */   
/*    */   public Statement getStatement() {
/* 46 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NullStatementSetManagedResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */