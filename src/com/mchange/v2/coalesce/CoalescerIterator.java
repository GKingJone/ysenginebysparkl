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
/*    */ class CoalescerIterator
/*    */   implements Iterator
/*    */ {
/*    */   Iterator inner;
/*    */   
/*    */   CoalescerIterator(Iterator inner)
/*    */   {
/* 33 */     this.inner = inner;
/*    */   }
/*    */   
/* 36 */   public boolean hasNext() { return this.inner.hasNext(); }
/*    */   
/*    */   public Object next() {
/* 39 */     return this.inner.next();
/*    */   }
/*    */   
/* 42 */   public void remove() { throw new UnsupportedOperationException("Objects cannot be removed from a coalescer!"); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\CoalescerIterator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */