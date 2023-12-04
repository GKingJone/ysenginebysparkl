/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client.jetty;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.MappedByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.LifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.QueuedThreadPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ScheduledExecutorScheduler;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.Closeable;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JettyIoPool
/*     */   implements Closeable
/*     */ {
/*     */   private final String name;
/*     */   private final QueuedThreadPool executor;
/*     */   private final ByteBufferPool byteBufferPool;
/*     */   private final Scheduler scheduler;
/*     */   
/*     */   public JettyIoPool(String name, JettyIoPoolConfig config)
/*     */   {
/*  31 */     this.name = name;
/*     */     try {
/*  33 */       String baseName = "http-client-" + name;
/*     */       
/*  35 */       QueuedThreadPool threadPool = new QueuedThreadPool(config.getMaxThreads(), config.getMinThreads(), 60000, null);
/*  36 */       threadPool.setName(baseName);
/*  37 */       threadPool.setDaemon(true);
/*  38 */       threadPool.start();
/*  39 */       threadPool.setStopTimeout(2000L);
/*  40 */       this.executor = threadPool;
/*     */       
/*  42 */       this.scheduler = new ScheduledExecutorScheduler(baseName + "-scheduler", true, Thread.currentThread().getContextClassLoader());
/*  43 */       this.scheduler.start();
/*     */       
/*  45 */       this.byteBufferPool = new MappedByteBufferPool();
/*     */     }
/*     */     catch (Exception e) {
/*  48 */       close();
/*  49 */       throw Throwables.propagate(e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 76	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:executor	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */     //   4: invokestatic 126	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:closeQuietly	(Lcom/facebook/presto/jdbc/internal/jetty/util/component/LifeCycle;)V
/*     */     //   7: aload_0
/*     */     //   8: getfield 95	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:scheduler	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/Scheduler;
/*     */     //   11: invokestatic 126	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:closeQuietly	(Lcom/facebook/presto/jdbc/internal/jetty/util/component/LifeCycle;)V
/*     */     //   14: goto +13 -> 27
/*     */     //   17: astore_1
/*     */     //   18: aload_0
/*     */     //   19: getfield 95	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:scheduler	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/Scheduler;
/*     */     //   22: invokestatic 126	com/facebook/presto/jdbc/internal/airlift/http/client/jetty/JettyIoPool:closeQuietly	(Lcom/facebook/presto/jdbc/internal/jetty/util/component/LifeCycle;)V
/*     */     //   25: aload_1
/*     */     //   26: athrow
/*     */     //   27: return
/*     */     // Line number table:
/*     */     //   Java source line #57	-> byte code offset #0
/*     */     //   Java source line #60	-> byte code offset #7
/*     */     //   Java source line #61	-> byte code offset #14
/*     */     //   Java source line #60	-> byte code offset #17
/*     */     //   Java source line #62	-> byte code offset #27
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	28	0	this	JettyIoPool
/*     */     //   17	9	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	7	17	finally
/*     */   }
/*     */   
/*     */   private static void closeQuietly(LifeCycle service)
/*     */   {
/*     */     try
/*     */     {
/*  67 */       if (service != null) {
/*  68 */         service.stop();
/*     */       }
/*     */     }
/*     */     catch (Exception localException) {}
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  77 */     return this.name;
/*     */   }
/*     */   
/*     */   public Executor getExecutor()
/*     */   {
/*  82 */     return this.executor;
/*     */   }
/*     */   
/*     */   public ByteBufferPool getByteBufferPool()
/*     */   {
/*  87 */     return this.byteBufferPool;
/*     */   }
/*     */   
/*     */   public Scheduler getScheduler()
/*     */   {
/*  92 */     return this.scheduler;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  98 */     return 
/*     */     
/* 100 */       MoreObjects.toStringHelper(this).add("name", this.name).toString();
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\jetty\JettyIoPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */