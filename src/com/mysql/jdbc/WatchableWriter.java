/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.CharArrayWriter;
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
/*    */ class WatchableWriter
/*    */   extends CharArrayWriter
/*    */ {
/*    */   private WriterWatcher watcher;
/*    */   
/*    */   public void close()
/*    */   {
/* 39 */     super.close();
/*    */     
/*    */ 
/* 42 */     if (this.watcher != null) {
/* 43 */       this.watcher.writerClosed(this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setWatcher(WriterWatcher watcher)
/*    */   {
/* 51 */     this.watcher = watcher;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\WatchableWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */