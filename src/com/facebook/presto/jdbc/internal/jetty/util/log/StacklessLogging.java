/*    */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*    */ 
/*    */ import java.util.HashSet;
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
/*    */ public class StacklessLogging
/*    */   implements AutoCloseable
/*    */ {
/* 41 */   private final Set<StdErrLog> squelched = new HashSet();
/*    */   
/*    */   public StacklessLogging(Class<?>... classesToSquelch)
/*    */   {
/* 45 */     for (Class<?> clazz : classesToSquelch)
/*    */     {
/* 47 */       Logger log = Log.getLogger(clazz);
/*    */       
/* 49 */       if ((log instanceof StdErrLog))
/*    */       {
/* 51 */         StdErrLog stdErrLog = (StdErrLog)log;
/* 52 */         if (!stdErrLog.isHideStacks())
/*    */         {
/* 54 */           stdErrLog.setHideStacks(true);
/* 55 */           this.squelched.add(stdErrLog);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public StacklessLogging(Logger... logs)
/*    */   {
/* 63 */     for (Logger log : logs)
/*    */     {
/*    */ 
/* 66 */       if ((log instanceof StdErrLog))
/*    */       {
/* 68 */         StdErrLog stdErrLog = (StdErrLog)log;
/* 69 */         if (!stdErrLog.isHideStacks())
/*    */         {
/* 71 */           stdErrLog.setHideStacks(true);
/* 72 */           this.squelched.add(stdErrLog);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */   {
/* 81 */     for (StdErrLog log : this.squelched) {
/* 82 */       log.setHideStacks(false);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\StacklessLogging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */