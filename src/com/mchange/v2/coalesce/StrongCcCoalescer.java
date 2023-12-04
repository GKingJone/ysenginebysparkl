/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import com.mchange.v1.identicator.IdHashMap;
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
/*    */ final class StrongCcCoalescer
/*    */   extends AbstractStrongCoalescer
/*    */   implements Coalescer
/*    */ {
/*    */   StrongCcCoalescer(CoalesceChecker cc)
/*    */   {
/* 33 */     super(new IdHashMap(new CoalesceIdenticator(cc)));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\StrongCcCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */