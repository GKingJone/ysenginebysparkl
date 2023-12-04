/*    */ package com.mchange.v1.io;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ public final class OutputStreamUtils
/*    */ {
/* 32 */   private static final MLogger logger = MLog.getLogger(OutputStreamUtils.class);
/*    */   
/*    */   public static void attemptClose(OutputStream os)
/*    */   {
/*    */     try {
/* 37 */       if (os != null) os.close();
/*    */     }
/*    */     catch (IOException e) {
/* 40 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 41 */         logger.log(MLevel.WARNING, "OutputStream close FAILED.", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\io\OutputStreamUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */