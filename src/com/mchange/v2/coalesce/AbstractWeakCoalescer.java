/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
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
/*    */ class AbstractWeakCoalescer
/*    */   implements Coalescer
/*    */ {
/*    */   Map wcoalesced;
/*    */   
/*    */   AbstractWeakCoalescer(Map wcoalesced)
/*    */   {
/* 34 */     this.wcoalesced = wcoalesced;
/*    */   }
/*    */   
/*    */   public Object coalesce(Object o)
/*    */   {
/* 39 */     Object out = null;
/*    */     
/* 41 */     WeakReference wr = (WeakReference)this.wcoalesced.get(o);
/* 42 */     if (wr != null) {
/* 43 */       out = wr.get();
/*    */     }
/* 45 */     if (out == null)
/*    */     {
/* 47 */       this.wcoalesced.put(o, new WeakReference(o));
/* 48 */       out = o;
/*    */     }
/* 50 */     return out;
/*    */   }
/*    */   
/*    */   public int countCoalesced() {
/* 54 */     return this.wcoalesced.size();
/*    */   }
/*    */   
/* 57 */   public Iterator iterator() { return new CoalescerIterator(this.wcoalesced.keySet().iterator()); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\AbstractWeakCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */