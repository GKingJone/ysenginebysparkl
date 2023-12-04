/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import com.mchange.v2.sql.filter.FilterResultSet;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
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
/*    */ final class SnatchFromSetResultSet
/*    */   extends FilterResultSet
/*    */ {
/*    */   Set activeResultSets;
/*    */   
/*    */   SnatchFromSetResultSet(Set activeResultSets)
/*    */   {
/* 35 */     this.activeResultSets = activeResultSets;
/*    */   }
/*    */   
/*    */   public synchronized void setInner(ResultSet inner) {
/* 39 */     this.inner = inner;
/* 40 */     this.activeResultSets.add(inner);
/*    */   }
/*    */   
/*    */   public synchronized void close() throws SQLException
/*    */   {
/* 45 */     this.inner.close();
/* 46 */     this.activeResultSets.remove(this.inner);
/* 47 */     this.inner = null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\SnatchFromSetResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */