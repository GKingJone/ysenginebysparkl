/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import com.mchange.v1.identicator.Identicator;
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
/*    */ class CoalesceIdenticator
/*    */   implements Identicator
/*    */ {
/*    */   CoalesceChecker cc;
/*    */   
/*    */   CoalesceIdenticator(CoalesceChecker cc)
/*    */   {
/* 33 */     this.cc = cc;
/*    */   }
/*    */   
/* 36 */   public boolean identical(Object a, Object b) { return this.cc.checkCoalesce(a, b); }
/*    */   
/*    */   public int hash(Object o) {
/* 39 */     return this.cc.coalesceHash(o);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\CoalesceIdenticator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */