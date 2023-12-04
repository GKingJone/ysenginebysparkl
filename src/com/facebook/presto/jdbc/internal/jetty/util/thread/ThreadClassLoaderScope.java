/*    */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ThreadClassLoaderScope
/*    */   implements Closeable
/*    */ {
/*    */   private final ClassLoader old;
/*    */   private final ClassLoader scopedClassLoader;
/*    */   
/*    */   public ThreadClassLoaderScope(ClassLoader cl)
/*    */   {
/* 30 */     this.old = Thread.currentThread().getContextClassLoader();
/* 31 */     this.scopedClassLoader = cl;
/* 32 */     Thread.currentThread().setContextClassLoader(this.scopedClassLoader);
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */   {
/* 38 */     Thread.currentThread().setContextClassLoader(this.old);
/*    */   }
/*    */   
/*    */   public ClassLoader getScopedClassLoader()
/*    */   {
/* 43 */     return this.scopedClassLoader;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ThreadClassLoaderScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */