/*    */ package com.facebook.presto.jdbc.internal.jetty.util.component;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.io.FileWriter;
/*    */ import java.io.Writer;
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
/*    */ public class FileNoticeLifeCycleListener
/*    */   implements LifeCycle.Listener
/*    */ {
/* 34 */   private static final Logger LOG = Log.getLogger(FileNoticeLifeCycleListener.class);
/*    */   
/*    */   private final String _filename;
/*    */   
/*    */   public FileNoticeLifeCycleListener(String filename)
/*    */   {
/* 40 */     this._filename = filename;
/*    */   }
/*    */   
/*    */   private void writeState(String action, LifeCycle lifecycle) {
/*    */     try {
/* 45 */       Writer out = new FileWriter(this._filename, true);Throwable localThrowable3 = null;
/*    */       try {
/* 47 */         out.append(action).append(" ").append(lifecycle.toString()).append("\n");
/*    */       }
/*    */       catch (Throwable localThrowable1)
/*    */       {
/* 45 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*    */       }
/*    */       finally {
/* 48 */         if (out != null) if (localThrowable3 != null) try { out.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else out.close();
/*    */       }
/*    */     } catch (Exception e) {
/* 51 */       LOG.warn(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public void lifeCycleStarting(LifeCycle event)
/*    */   {
/* 57 */     writeState("STARTING", event);
/*    */   }
/*    */   
/*    */   public void lifeCycleStarted(LifeCycle event)
/*    */   {
/* 62 */     writeState("STARTED", event);
/*    */   }
/*    */   
/*    */   public void lifeCycleFailure(LifeCycle event, Throwable cause)
/*    */   {
/* 67 */     writeState("FAILED", event);
/*    */   }
/*    */   
/*    */   public void lifeCycleStopping(LifeCycle event)
/*    */   {
/* 72 */     writeState("STOPPING", event);
/*    */   }
/*    */   
/*    */   public void lifeCycleStopped(LifeCycle event)
/*    */   {
/* 77 */     writeState("STOPPED", event);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\FileNoticeLifeCycleListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */