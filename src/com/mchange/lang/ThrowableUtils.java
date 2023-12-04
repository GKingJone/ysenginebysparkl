/*    */ package com.mchange.lang;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
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
/*    */ public final class ThrowableUtils
/*    */ {
/*    */   public static String extractStackTrace(Throwable t)
/*    */   {
/* 32 */     StringWriter me = new StringWriter();
/* 33 */     PrintWriter pw = new PrintWriter(me);
/* 34 */     t.printStackTrace(pw);
/* 35 */     pw.flush();
/* 36 */     return me.toString();
/*    */   }
/*    */   
/*    */   public static boolean isChecked(Throwable t)
/*    */   {
/* 41 */     return ((t instanceof Exception)) && (!(t instanceof RuntimeException));
/*    */   }
/*    */   
/*    */ 
/*    */   public static boolean isUnchecked(Throwable t)
/*    */   {
/* 47 */     return !isChecked(t);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\lang\ThrowableUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */