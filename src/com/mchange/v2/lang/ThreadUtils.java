/*    */ package com.mchange.v2.lang;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
/*    */ import java.lang.reflect.Method;
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
/*    */ public final class ThreadUtils
/*    */ {
/* 31 */   private static final MLogger logger = MLog.getLogger(ThreadUtils.class);
/*    */   static final Method holdsLock;
/*    */   
/*    */   static
/*    */   {
/*    */     Method _holdsLock;
/*    */     try
/*    */     {
/* 39 */       _holdsLock = class$java$lang$Thread.getMethod("holdsLock", new Class[] { Object.class });
/*    */     } catch (NoSuchMethodException e) {
/* 41 */       _holdsLock = null;
/*    */     }
/* 43 */     holdsLock = _holdsLock;
/*    */   }
/*    */   
/*    */   public static void enumerateAll(Thread[] threads) {
/* 47 */     ThreadGroupUtils.rootThreadGroup().enumerate(threads);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static Boolean reflectiveHoldsLock(Object o)
/*    */   {
/*    */     try
/*    */     {
/* 56 */       if (holdsLock == null) {
/* 57 */         return null;
/*    */       }
/* 59 */       return (Boolean)holdsLock.invoke(null, new Object[] { o });
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 63 */       if (logger.isLoggable(MLevel.FINER))
/* 64 */         logger.log(MLevel.FINER, "An Exception occurred while trying to call Thread.holdsLock( ... ) reflectively.", e); }
/* 65 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\lang\ThreadUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */