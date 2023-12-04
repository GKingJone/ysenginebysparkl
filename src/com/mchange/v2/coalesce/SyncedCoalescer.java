/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import java.util.Iterator;
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
/*    */ class SyncedCoalescer
/*    */   implements Coalescer
/*    */ {
/*    */   Coalescer inner;
/*    */   
/*    */   public SyncedCoalescer(Coalescer inner)
/*    */   {
/* 33 */     this.inner = inner;
/*    */   }
/*    */   
/* 36 */   public synchronized Object coalesce(Object o) { return this.inner.coalesce(o); }
/*    */   
/*    */   public synchronized int countCoalesced() {
/* 39 */     return this.inner.countCoalesced();
/*    */   }
/*    */   
/* 42 */   public synchronized Iterator iterator() { return this.inner.iterator(); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\SyncedCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */