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
/*    */ 
/*    */ abstract class SetManagedResultSet
/*    */   extends FilterResultSet
/*    */ {
/*    */   Set activeResultSets;
/*    */   
/*    */   SetManagedResultSet(Set activeResultSets)
/*    */   {
/* 36 */     this.activeResultSets = activeResultSets;
/*    */   }
/*    */   
/*    */   SetManagedResultSet(ResultSet inner, Set activeResultSets)
/*    */   {
/* 41 */     super(inner);
/* 42 */     this.activeResultSets = activeResultSets;
/*    */   }
/*    */   
/*    */   public synchronized void setInner(ResultSet inner)
/*    */   {
/* 47 */     this.inner = inner;
/* 48 */     this.activeResultSets.add(inner);
/*    */   }
/*    */   
/*    */   public synchronized void close() throws SQLException
/*    */   {
/* 53 */     if (this.inner != null)
/*    */     {
/* 55 */       this.inner.close();
/* 56 */       this.activeResultSets.remove(this.inner);
/* 57 */       this.inner = null;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\SetManagedResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */