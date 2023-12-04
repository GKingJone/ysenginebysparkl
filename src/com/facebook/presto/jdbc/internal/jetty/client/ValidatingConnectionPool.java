/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler.Task;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ValidatingConnectionPool
/*     */   extends ConnectionPool
/*     */ {
/*  61 */   private static final Logger LOG = Log.getLogger(ValidatingConnectionPool.class);
/*     */   
/*     */   private final Scheduler scheduler;
/*     */   private final long timeout;
/*     */   private final Map<Connection, Holder> quarantine;
/*     */   
/*     */   public ValidatingConnectionPool(Destination destination, int maxConnections, Callback requester, Scheduler scheduler, long timeout)
/*     */   {
/*  69 */     super(destination, maxConnections, requester);
/*  70 */     this.scheduler = scheduler;
/*  71 */     this.timeout = timeout;
/*  72 */     this.quarantine = new HashMap(maxConnections);
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of validating connections", readonly=true)
/*     */   public int getValidatingConnectionCount()
/*     */   {
/*  78 */     return this.quarantine.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean release(Connection connection)
/*     */   {
/*  84 */     lock();
/*     */     try
/*     */     {
/*  87 */       if (!getActiveConnections().remove(connection))
/*  88 */         return false;
/*  89 */       Holder holder = new Holder(connection);
/*  90 */       holder.task = this.scheduler.schedule(holder, this.timeout, TimeUnit.MILLISECONDS);
/*  91 */       this.quarantine.put(connection, holder);
/*  92 */       if (LOG.isDebugEnabled()) {
/*  93 */         LOG.debug("Validating for {}ms {}", new Object[] { Long.valueOf(this.timeout), connection });
/*     */       }
/*     */     }
/*     */     finally {
/*  97 */       unlock();
/*     */     }
/*     */     
/* 100 */     released(connection);
/* 101 */     return true;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean remove(Connection connection)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 64	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:lock	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 38	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:quarantine	Ljava/util/Map;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 133 2 0
/*     */     //   14: checkcast 7	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder
/*     */     //   17: astore_2
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual 77	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */     //   22: goto +10 -> 32
/*     */     //   25: astore_3
/*     */     //   26: aload_0
/*     */     //   27: invokevirtual 77	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */     //   30: aload_3
/*     */     //   31: athrow
/*     */     //   32: aload_2
/*     */     //   33: ifnonnull +9 -> 42
/*     */     //   36: aload_0
/*     */     //   37: aload_1
/*     */     //   38: invokespecial 135	com/facebook/presto/jdbc/internal/jetty/client/ConnectionPool:remove	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;)Z
/*     */     //   41: ireturn
/*     */     //   42: getstatic 100	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:LOG	Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */     //   45: invokeinterface 106 1 0
/*     */     //   50: ifeq +21 -> 71
/*     */     //   53: getstatic 100	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:LOG	Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */     //   56: ldc -119
/*     */     //   58: iconst_1
/*     */     //   59: anewarray 110	java/lang/Object
/*     */     //   62: dup
/*     */     //   63: iconst_0
/*     */     //   64: aload_1
/*     */     //   65: aastore
/*     */     //   66: invokeinterface 120 3 0
/*     */     //   71: aload_2
/*     */     //   72: invokevirtual 140	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:cancel	()Z
/*     */     //   75: istore_3
/*     */     //   76: iload_3
/*     */     //   77: ifeq +10 -> 87
/*     */     //   80: aload_0
/*     */     //   81: aload_1
/*     */     //   82: iconst_1
/*     */     //   83: invokevirtual 143	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:remove	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;Z)Z
/*     */     //   86: ireturn
/*     */     //   87: aload_0
/*     */     //   88: aload_1
/*     */     //   89: invokespecial 135	com/facebook/presto/jdbc/internal/jetty/client/ConnectionPool:remove	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;)Z
/*     */     //   92: ireturn
/*     */     // Line number table:
/*     */     //   Java source line #108	-> byte code offset #0
/*     */     //   Java source line #111	-> byte code offset #4
/*     */     //   Java source line #115	-> byte code offset #18
/*     */     //   Java source line #116	-> byte code offset #22
/*     */     //   Java source line #115	-> byte code offset #25
/*     */     //   Java source line #118	-> byte code offset #32
/*     */     //   Java source line #119	-> byte code offset #36
/*     */     //   Java source line #121	-> byte code offset #42
/*     */     //   Java source line #122	-> byte code offset #53
/*     */     //   Java source line #124	-> byte code offset #71
/*     */     //   Java source line #125	-> byte code offset #76
/*     */     //   Java source line #126	-> byte code offset #80
/*     */     //   Java source line #128	-> byte code offset #87
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	93	0	this	ValidatingConnectionPool
/*     */     //   0	93	1	connection	Connection
/*     */     //   17	2	2	holder	Holder
/*     */     //   32	40	2	holder	Holder
/*     */     //   25	6	3	localObject	Object
/*     */     //   75	2	3	cancelled	boolean
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	18	25	finally
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 134 */     super.dump(out, indent);
/* 135 */     ContainerLifeCycle.dump(out, indent, new Collection[] { this.quarantine.values() });
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String toString()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 64	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:lock	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 38	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:quarantine	Ljava/util/Map;
/*     */     //   8: invokeinterface 58 1 0
/*     */     //   13: istore_1
/*     */     //   14: aload_0
/*     */     //   15: invokevirtual 77	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */     //   18: goto +10 -> 28
/*     */     //   21: astore_2
/*     */     //   22: aload_0
/*     */     //   23: invokevirtual 77	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */     //   26: aload_2
/*     */     //   27: athrow
/*     */     //   28: ldc -86
/*     */     //   30: iconst_2
/*     */     //   31: anewarray 110	java/lang/Object
/*     */     //   34: dup
/*     */     //   35: iconst_0
/*     */     //   36: aload_0
/*     */     //   37: invokespecial 172	com/facebook/presto/jdbc/internal/jetty/client/ConnectionPool:toString	()Ljava/lang/String;
/*     */     //   40: aastore
/*     */     //   41: dup
/*     */     //   42: iconst_1
/*     */     //   43: iload_1
/*     */     //   44: invokestatic 177	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   47: aastore
/*     */     //   48: invokestatic 183	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   51: areturn
/*     */     // Line number table:
/*     */     //   Java source line #142	-> byte code offset #0
/*     */     //   Java source line #145	-> byte code offset #4
/*     */     //   Java source line #149	-> byte code offset #14
/*     */     //   Java source line #150	-> byte code offset #18
/*     */     //   Java source line #149	-> byte code offset #21
/*     */     //   Java source line #151	-> byte code offset #28
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	52	0	this	ValidatingConnectionPool
/*     */     //   13	2	1	size	int
/*     */     //   28	16	1	size	int
/*     */     //   21	6	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	14	21	finally
/*     */   }
/*     */   
/*     */   private class Holder
/*     */     implements Runnable
/*     */   {
/* 156 */     private final long timestamp = System.nanoTime();
/* 157 */     private final AtomicBoolean latch = new AtomicBoolean();
/*     */     private final Connection connection;
/*     */     public Task task;
/*     */     
/*     */     public Holder(Connection connection)
/*     */     {
/* 163 */       this.connection = connection;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 45	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:latch	Ljava/util/concurrent/atomic/AtomicBoolean;
/*     */       //   4: iconst_0
/*     */       //   5: iconst_1
/*     */       //   6: invokevirtual 54	java/util/concurrent/atomic/AtomicBoolean:compareAndSet	(ZZ)Z
/*     */       //   9: ifeq +113 -> 122
/*     */       //   12: aload_0
/*     */       //   13: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   16: invokevirtual 57	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:lock	()V
/*     */       //   19: aload_0
/*     */       //   20: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   23: invokestatic 61	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:access$000	(Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;)Ljava/util/Map;
/*     */       //   26: aload_0
/*     */       //   27: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:connection	Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;
/*     */       //   30: invokeinterface 67 2 0
/*     */       //   35: pop
/*     */       //   36: aload_0
/*     */       //   37: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   40: aload_0
/*     */       //   41: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:connection	Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;
/*     */       //   44: invokevirtual 71	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:offerIdle	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;)Z
/*     */       //   47: istore_1
/*     */       //   48: invokestatic 75	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:access$100	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   51: invokeinterface 81 1 0
/*     */       //   56: ifeq +24 -> 80
/*     */       //   59: invokestatic 75	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:access$100	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   62: ldc 83
/*     */       //   64: iconst_1
/*     */       //   65: anewarray 4	java/lang/Object
/*     */       //   68: dup
/*     */       //   69: iconst_0
/*     */       //   70: aload_0
/*     */       //   71: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:connection	Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;
/*     */       //   74: aastore
/*     */       //   75: invokeinterface 87 3 0
/*     */       //   80: aload_0
/*     */       //   81: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   84: invokevirtual 90	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */       //   87: goto +13 -> 100
/*     */       //   90: astore_2
/*     */       //   91: aload_0
/*     */       //   92: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   95: invokevirtual 90	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:unlock	()V
/*     */       //   98: aload_2
/*     */       //   99: athrow
/*     */       //   100: aload_0
/*     */       //   101: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   104: aload_0
/*     */       //   105: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:connection	Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;
/*     */       //   108: iload_1
/*     */       //   109: invokevirtual 96	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:idle	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;Z)Z
/*     */       //   112: ifeq +10 -> 122
/*     */       //   115: aload_0
/*     */       //   116: getfield 29	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool$Holder:this$0	Lcom/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool;
/*     */       //   119: invokevirtual 99	com/facebook/presto/jdbc/internal/jetty/client/ValidatingConnectionPool:proceed	()V
/*     */       //   122: return
/*     */       // Line number table:
/*     */       //   Java source line #169	-> byte code offset #0
/*     */       //   Java source line #172	-> byte code offset #12
/*     */       //   Java source line #175	-> byte code offset #19
/*     */       //   Java source line #176	-> byte code offset #36
/*     */       //   Java source line #177	-> byte code offset #48
/*     */       //   Java source line #178	-> byte code offset #59
/*     */       //   Java source line #182	-> byte code offset #80
/*     */       //   Java source line #183	-> byte code offset #87
/*     */       //   Java source line #182	-> byte code offset #90
/*     */       //   Java source line #185	-> byte code offset #100
/*     */       //   Java source line #186	-> byte code offset #115
/*     */       //   Java source line #188	-> byte code offset #122
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	123	0	this	Holder
/*     */       //   47	2	1	idle	boolean
/*     */       //   100	9	1	idle	boolean
/*     */       //   90	9	2	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   19	80	90	finally
/*     */     }
/*     */     
/*     */     public boolean cancel()
/*     */     {
/* 192 */       if (this.latch.compareAndSet(false, true))
/*     */       {
/* 194 */         this.task.cancel();
/* 195 */         return true;
/*     */       }
/* 197 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 203 */       return String.format("%s[validationLeft=%dms]", new Object[] { this.connection, 
/*     */       
/* 205 */         Long.valueOf(ValidatingConnectionPool.this.timeout - TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.timestamp)) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ValidatingConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */