/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
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
/*    */ class WatchableOutputStream
/*    */   extends ByteArrayOutputStream
/*    */ {
/*    */   private OutputStreamWatcher watcher;
/*    */   
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 40 */     super.close();
/*    */     
/* 42 */     if (this.watcher != null) {
/* 43 */       this.watcher.streamClosed(this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setWatcher(OutputStreamWatcher watcher)
/*    */   {
/* 51 */     this.watcher = watcher;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\WatchableOutputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */