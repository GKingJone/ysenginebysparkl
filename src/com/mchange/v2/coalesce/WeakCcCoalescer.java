/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import com.mchange.v1.identicator.IdWeakHashMap;
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
/*    */ final class WeakCcCoalescer
/*    */   extends AbstractWeakCoalescer
/*    */   implements Coalescer
/*    */ {
/*    */   WeakCcCoalescer(CoalesceChecker cc)
/*    */   {
/* 33 */     super(new IdWeakHashMap(new CoalesceIdenticator(cc)));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\WeakCcCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */