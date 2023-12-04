/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
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
/*    */ class AbstractStrongCoalescer
/*    */   implements Coalescer
/*    */ {
/*    */   Map coalesced;
/*    */   
/*    */   AbstractStrongCoalescer(Map coalesced)
/*    */   {
/* 33 */     this.coalesced = coalesced;
/*    */   }
/*    */   
/*    */   public Object coalesce(Object o) {
/* 37 */     Object out = this.coalesced.get(o);
/* 38 */     if (out == null)
/*    */     {
/* 40 */       this.coalesced.put(o, o);
/* 41 */       out = o;
/*    */     }
/* 43 */     return out;
/*    */   }
/*    */   
/*    */   public int countCoalesced() {
/* 47 */     return this.coalesced.size();
/*    */   }
/*    */   
/* 50 */   public Iterator iterator() { return new CoalescerIterator(this.coalesced.keySet().iterator()); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\AbstractStrongCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */