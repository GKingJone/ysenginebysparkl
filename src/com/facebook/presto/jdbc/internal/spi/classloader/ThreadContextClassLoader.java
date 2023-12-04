/*    */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*    */ 
/*    */ import java.io.Closeable;
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
/*    */ public class ThreadContextClassLoader
/*    */   implements Closeable
/*    */ {
/*    */   private final ClassLoader originalThreadContextClassLoader;
/*    */   
/*    */   public ThreadContextClassLoader(ClassLoader newThreadContextClassLoader)
/*    */   {
/* 25 */     this.originalThreadContextClassLoader = Thread.currentThread().getContextClassLoader();
/* 26 */     Thread.currentThread().setContextClassLoader(newThreadContextClassLoader);
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */   {
/* 32 */     Thread.currentThread().setContextClassLoader(this.originalThreadContextClassLoader);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ThreadContextClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */