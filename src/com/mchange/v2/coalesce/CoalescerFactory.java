/*    */ package com.mchange.v2.coalesce;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class CoalescerFactory
/*    */ {
/*    */   public static Coalescer createCoalescer()
/*    */   {
/* 38 */     return createCoalescer(true, true);
/*    */   }
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
/*    */   public static Coalescer createCoalescer(boolean weak, boolean synced)
/*    */   {
/* 55 */     return createCoalescer(null, weak, synced);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Coalescer createCoalescer(CoalesceChecker cc, boolean weak, boolean synced)
/*    */   {
/*    */     Coalescer out;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     Coalescer out;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 83 */     if (cc == null)
/*    */     {
/* 85 */       out = weak ? new WeakEqualsCoalescer() : new StrongEqualsCoalescer();
/*    */ 
/*    */     }
/*    */     else
/*    */     {
/*    */ 
/* 91 */       out = weak ? new WeakCcCoalescer(cc) : new StrongCcCoalescer(cc);
/*    */     }
/*    */     
/*    */ 
/* 95 */     return synced ? new SyncedCoalescer(out) : out;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\CoalescerFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */