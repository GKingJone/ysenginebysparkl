/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.jetty;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.JavaUtilLog;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JettyLogging
/*    */ {
/*    */   public static void setup()
/*    */   {
/* 13 */     Log.setLog(new NoOpLogger(null));
/* 14 */     Log.initialized();
/* 15 */     Log.setLog(new JavaUtilLog());
/*    */   }
/*    */   
/*    */ 
/*    */   private static class NoOpLogger
/*    */     implements Logger
/*    */   {
/*    */     public String getName()
/*    */     {
/* 24 */       return "";
/*    */     }
/*    */     
/*    */ 
/*    */     public void warn(String msg, Object... args) {}
/*    */     
/*    */ 
/*    */     public void warn(Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public void warn(String msg, Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public void info(String msg, Object... args) {}
/*    */     
/*    */ 
/*    */     public void info(Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public void info(String msg, Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public boolean isDebugEnabled()
/*    */     {
/* 48 */       return false;
/*    */     }
/*    */     
/*    */ 
/*    */     public void setDebugEnabled(boolean enabled) {}
/*    */     
/*    */ 
/*    */     public void debug(String msg, Object... args) {}
/*    */     
/*    */ 
/*    */     public void debug(String msg, long value) {}
/*    */     
/*    */ 
/*    */     public void debug(Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public void debug(String msg, Throwable thrown) {}
/*    */     
/*    */ 
/*    */     public Logger getLogger(String name)
/*    */     {
/* 69 */       return this;
/*    */     }
/*    */     
/*    */     public void ignore(Throwable ignored) {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\jetty\JettyLogging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */