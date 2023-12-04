/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
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
/*    */ public abstract class IteratingNestedCallback
/*    */   extends IteratingCallback
/*    */ {
/*    */   final Callback _callback;
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
/*    */   public IteratingNestedCallback(Callback callback)
/*    */   {
/* 47 */     this._callback = callback;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isNonBlocking()
/*    */   {
/* 53 */     return this._callback.isNonBlocking();
/*    */   }
/*    */   
/*    */ 
/*    */   protected void onCompleteSuccess()
/*    */   {
/* 59 */     this._callback.succeeded();
/*    */   }
/*    */   
/*    */ 
/*    */   protected void onCompleteFailure(Throwable x)
/*    */   {
/* 65 */     this._callback.failed(x);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     return String.format("%s@%x", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IteratingNestedCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */