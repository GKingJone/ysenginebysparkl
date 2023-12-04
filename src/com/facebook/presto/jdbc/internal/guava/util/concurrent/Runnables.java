/*    */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class Runnables
/*    */ {
/* 31 */   private static final Runnable EMPTY_RUNNABLE = new Runnable()
/*    */   {
/*    */     public void run() {}
/*    */   };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Runnable doNothing()
/*    */   {
/* 41 */     return EMPTY_RUNNABLE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\Runnables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */