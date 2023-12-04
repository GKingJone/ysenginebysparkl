/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*    */ public abstract interface Promise<C>
/*    */ {
/*    */   public abstract void succeeded(C paramC);
/*    */   
/*    */   public abstract void failed(Throwable paramThrowable);
/*    */   
/*    */   public static class Adapter<C>
/*    */     implements Promise<C>
/*    */   {
/*    */     public void succeeded(C result) {}
/*    */     
/*    */     public void failed(Throwable x)
/*    */     {
/* 61 */       Log.getLogger(getClass()).warn(x);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Promise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */