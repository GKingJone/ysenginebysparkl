/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BlockingArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Sweeper.Sweepable;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.LinkedBlockingDeque;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ @ManagedObject("The connection pool")
/*     */ public class DuplexConnectionPool
/*     */   implements Closeable, Dumpable, Sweeper.Sweepable
/*     */ {
/*  47 */   private static final Logger LOG = Log.getLogger(DuplexConnectionPool.class);
/*     */   
/*  49 */   private final AtomicInteger connectionCount = new AtomicInteger();
/*  50 */   private final ReentrantLock lock = new ReentrantLock();
/*     */   private final Destination destination;
/*     */   private final int maxConnections;
/*     */   private final Callback requester;
/*     */   private final Deque<Connection> idleConnections;
/*     */   private final Queue<Connection> activeConnections;
/*     */   
/*     */   public DuplexConnectionPool(Destination destination, int maxConnections, Callback requester)
/*     */   {
/*  59 */     this.destination = destination;
/*  60 */     this.maxConnections = maxConnections;
/*  61 */     this.requester = requester;
/*  62 */     this.idleConnections = new LinkedBlockingDeque(maxConnections);
/*  63 */     this.activeConnections = new BlockingArrayQueue(maxConnections);
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of connections", readonly=true)
/*     */   public int getConnectionCount()
/*     */   {
/*  69 */     return this.connectionCount.get();
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of idle connections", readonly=true)
/*     */   public int getIdleConnectionCount()
/*     */   {
/*  75 */     lock();
/*     */     try
/*     */     {
/*  78 */       return this.idleConnections.size();
/*     */     }
/*     */     finally
/*     */     {
/*  82 */       unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of active connections", readonly=true)
/*     */   public int getActiveConnectionCount()
/*     */   {
/*  89 */     lock();
/*     */     try
/*     */     {
/*  92 */       return this.activeConnections.size();
/*     */     }
/*     */     finally
/*     */     {
/*  96 */       unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public Queue<Connection> getIdleConnections()
/*     */   {
/* 102 */     return this.idleConnections;
/*     */   }
/*     */   
/*     */   public Queue<Connection> getActiveConnections()
/*     */   {
/* 107 */     return this.activeConnections;
/*     */   }
/*     */   
/*     */   public Connection acquire()
/*     */   {
/* 112 */     Connection connection = activateIdle();
/* 113 */     if (connection == null)
/* 114 */       connection = tryCreate();
/* 115 */     return connection;
/*     */   }
/*     */   
/*     */   private Connection tryCreate()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 122 */       int current = getConnectionCount();
/* 123 */       final int next = current + 1;
/*     */       
/* 125 */       if (next > this.maxConnections)
/*     */       {
/* 127 */         if (LOG.isDebugEnabled()) {
/* 128 */           LOG.debug("Max connections {}/{} reached", new Object[] { Integer.valueOf(current), Integer.valueOf(this.maxConnections) });
/*     */         }
/* 130 */         return activateIdle();
/*     */       }
/*     */       
/* 133 */       if (this.connectionCount.compareAndSet(current, next))
/*     */       {
/* 135 */         if (LOG.isDebugEnabled()) {
/* 136 */           LOG.debug("Connection {}/{} creation", new Object[] { Integer.valueOf(next), Integer.valueOf(this.maxConnections) });
/*     */         }
/* 138 */         this.destination.newConnection(new Promise()
/*     */         {
/*     */ 
/*     */           public void succeeded(Connection connection)
/*     */           {
/* 143 */             if (DuplexConnectionPool.LOG.isDebugEnabled()) {
/* 144 */               DuplexConnectionPool.LOG.debug("Connection {}/{} creation succeeded {}", new Object[] { Integer.valueOf(next), Integer.valueOf(DuplexConnectionPool.this.maxConnections), connection });
/*     */             }
/* 146 */             DuplexConnectionPool.this.idleCreated(connection);
/*     */             
/* 148 */             DuplexConnectionPool.this.proceed();
/*     */           }
/*     */           
/*     */ 
/*     */           public void failed(Throwable x)
/*     */           {
/* 154 */             if (DuplexConnectionPool.LOG.isDebugEnabled()) {
/* 155 */               DuplexConnectionPool.LOG.debug("Connection " + next + "/" + DuplexConnectionPool.this.maxConnections + " creation failed", x);
/*     */             }
/* 157 */             DuplexConnectionPool.this.connectionCount.decrementAndGet();
/*     */             
/* 159 */             DuplexConnectionPool.this.requester.failed(x);
/*     */           }
/*     */           
/*     */ 
/* 163 */         });
/* 164 */         return activateIdle();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void proceed()
/*     */   {
/* 171 */     this.requester.succeeded();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected void idleCreated(Connection connection)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 85	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:lock	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 65	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:idleConnections	Ljava/util/Deque;
/*     */     //   8: aload_1
/*     */     //   9: invokeinterface 166 2 0
/*     */     //   14: istore_2
/*     */     //   15: aload_0
/*     */     //   16: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   19: goto +10 -> 29
/*     */     //   22: astore_3
/*     */     //   23: aload_0
/*     */     //   24: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   27: aload_3
/*     */     //   28: athrow
/*     */     //   29: aload_0
/*     */     //   30: aload_1
/*     */     //   31: iload_2
/*     */     //   32: invokevirtual 170	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:idle	(Lcom/facebook/presto/jdbc/internal/jetty/client/api/Connection;Z)Z
/*     */     //   35: pop
/*     */     //   36: return
/*     */     // Line number table:
/*     */     //   Java source line #177	-> byte code offset #0
/*     */     //   Java source line #181	-> byte code offset #4
/*     */     //   Java source line #185	-> byte code offset #15
/*     */     //   Java source line #186	-> byte code offset #19
/*     */     //   Java source line #185	-> byte code offset #22
/*     */     //   Java source line #188	-> byte code offset #29
/*     */     //   Java source line #189	-> byte code offset #36
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	37	0	this	DuplexConnectionPool
/*     */     //   0	37	1	connection	Connection
/*     */     //   14	2	2	idle	boolean
/*     */     //   29	3	2	idle	boolean
/*     */     //   22	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	15	22	finally
/*     */   }
/*     */   
/*     */   private Connection activateIdle()
/*     */   {
/* 195 */     lock();
/*     */     try
/*     */     {
/* 198 */       Connection connection = (Connection)this.idleConnections.pollFirst();
/* 199 */       if (connection == null)
/* 200 */         return null;
/* 201 */       acquired = this.activeConnections.offer(connection);
/*     */     }
/*     */     finally {
/*     */       boolean acquired;
/* 205 */       unlock(); }
/*     */     boolean acquired;
/*     */     Connection connection;
/* 208 */     if (acquired)
/*     */     {
/* 210 */       if (LOG.isDebugEnabled())
/* 211 */         LOG.debug("Connection active {}", new Object[] { connection });
/* 212 */       acquired(connection);
/* 213 */       return connection;
/*     */     }
/*     */     
/*     */ 
/* 217 */     if (LOG.isDebugEnabled())
/* 218 */       LOG.debug("Connection active overflow {}", new Object[] { connection });
/* 219 */     connection.close();
/* 220 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void acquired(Connection connection) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean release(Connection connection)
/*     */   {
/* 231 */     lock();
/*     */     try
/*     */     {
/* 234 */       if (!this.activeConnections.remove(connection)) {
/* 235 */         return false;
/*     */       }
/* 237 */       idle = offerIdle(connection);
/*     */     }
/*     */     finally {
/*     */       boolean idle;
/* 241 */       unlock();
/*     */     }
/*     */     boolean idle;
/* 244 */     released(connection);
/* 245 */     return idle(connection, idle);
/*     */   }
/*     */   
/*     */   protected boolean offerIdle(Connection connection)
/*     */   {
/* 250 */     return this.idleConnections.offerFirst(connection);
/*     */   }
/*     */   
/*     */   protected boolean idle(Connection connection, boolean idle)
/*     */   {
/* 255 */     if (idle)
/*     */     {
/* 257 */       if (LOG.isDebugEnabled())
/* 258 */         LOG.debug("Connection idle {}", new Object[] { connection });
/* 259 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 263 */     if (LOG.isDebugEnabled())
/* 264 */       LOG.debug("Connection idle overflow {}", new Object[] { connection });
/* 265 */     connection.close();
/* 266 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void released(Connection connection) {}
/*     */   
/*     */ 
/*     */   public boolean remove(Connection connection)
/*     */   {
/* 276 */     return remove(connection, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean remove(Connection connection, boolean force)
/*     */   {
/* 283 */     lock();
/*     */     try
/*     */     {
/* 286 */       boolean activeRemoved = this.activeConnections.remove(connection);
/* 287 */       idleRemoved = this.idleConnections.remove(connection);
/*     */     }
/*     */     finally {
/*     */       boolean idleRemoved;
/* 291 */       unlock(); }
/*     */     boolean idleRemoved;
/*     */     boolean activeRemoved;
/* 294 */     if ((activeRemoved) || (force))
/* 295 */       released(connection);
/* 296 */     boolean removed = (activeRemoved) || (idleRemoved) || (force);
/* 297 */     if (removed)
/*     */     {
/* 299 */       int pooled = this.connectionCount.decrementAndGet();
/* 300 */       if (LOG.isDebugEnabled())
/* 301 */         LOG.debug("Connection removed {} - pooled: {}", new Object[] { connection, Integer.valueOf(pooled) });
/*     */     }
/* 303 */     return removed;
/*     */   }
/*     */   
/*     */   public boolean isActive(Connection connection)
/*     */   {
/* 308 */     lock();
/*     */     try
/*     */     {
/* 311 */       return this.activeConnections.contains(connection);
/*     */     }
/*     */     finally
/*     */     {
/* 315 */       unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isIdle(Connection connection)
/*     */   {
/* 321 */     lock();
/*     */     try
/*     */     {
/* 324 */       return this.idleConnections.contains(connection);
/*     */     }
/*     */     finally
/*     */     {
/* 328 */       unlock();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/* 334 */     return this.connectionCount.get() == 0;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 228	java/util/ArrayList
/*     */     //   3: dup
/*     */     //   4: invokespecial 229	java/util/ArrayList:<init>	()V
/*     */     //   7: astore_1
/*     */     //   8: new 228	java/util/ArrayList
/*     */     //   11: dup
/*     */     //   12: invokespecial 229	java/util/ArrayList:<init>	()V
/*     */     //   15: astore_2
/*     */     //   16: aload_0
/*     */     //   17: invokevirtual 85	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:lock	()V
/*     */     //   20: aload_1
/*     */     //   21: aload_0
/*     */     //   22: getfield 65	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:idleConnections	Ljava/util/Deque;
/*     */     //   25: invokeinterface 235 2 0
/*     */     //   30: pop
/*     */     //   31: aload_0
/*     */     //   32: getfield 65	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:idleConnections	Ljava/util/Deque;
/*     */     //   35: invokeinterface 238 1 0
/*     */     //   40: aload_2
/*     */     //   41: aload_0
/*     */     //   42: getfield 70	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:activeConnections	Ljava/util/Queue;
/*     */     //   45: invokeinterface 235 2 0
/*     */     //   50: pop
/*     */     //   51: aload_0
/*     */     //   52: getfield 70	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:activeConnections	Ljava/util/Queue;
/*     */     //   55: invokeinterface 239 1 0
/*     */     //   60: aload_0
/*     */     //   61: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   64: goto +10 -> 74
/*     */     //   67: astore_3
/*     */     //   68: aload_0
/*     */     //   69: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   72: aload_3
/*     */     //   73: athrow
/*     */     //   74: aload_0
/*     */     //   75: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:connectionCount	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   78: iconst_0
/*     */     //   79: invokevirtual 242	java/util/concurrent/atomic/AtomicInteger:set	(I)V
/*     */     //   82: aload_1
/*     */     //   83: invokeinterface 246 1 0
/*     */     //   88: astore_3
/*     */     //   89: aload_3
/*     */     //   90: invokeinterface 251 1 0
/*     */     //   95: ifeq +24 -> 119
/*     */     //   98: aload_3
/*     */     //   99: invokeinterface 253 1 0
/*     */     //   104: checkcast 113	com/facebook/presto/jdbc/internal/jetty/client/api/Connection
/*     */     //   107: astore 4
/*     */     //   109: aload 4
/*     */     //   111: invokeinterface 188 1 0
/*     */     //   116: goto -27 -> 89
/*     */     //   119: aload_2
/*     */     //   120: invokeinterface 246 1 0
/*     */     //   125: astore_3
/*     */     //   126: aload_3
/*     */     //   127: invokeinterface 251 1 0
/*     */     //   132: ifeq +24 -> 156
/*     */     //   135: aload_3
/*     */     //   136: invokeinterface 253 1 0
/*     */     //   141: checkcast 113	com/facebook/presto/jdbc/internal/jetty/client/api/Connection
/*     */     //   144: astore 4
/*     */     //   146: aload 4
/*     */     //   148: invokeinterface 188 1 0
/*     */     //   153: goto -27 -> 126
/*     */     //   156: return
/*     */     // Line number table:
/*     */     //   Java source line #339	-> byte code offset #0
/*     */     //   Java source line #340	-> byte code offset #8
/*     */     //   Java source line #341	-> byte code offset #16
/*     */     //   Java source line #344	-> byte code offset #20
/*     */     //   Java source line #345	-> byte code offset #31
/*     */     //   Java source line #346	-> byte code offset #40
/*     */     //   Java source line #347	-> byte code offset #51
/*     */     //   Java source line #351	-> byte code offset #60
/*     */     //   Java source line #352	-> byte code offset #64
/*     */     //   Java source line #351	-> byte code offset #67
/*     */     //   Java source line #354	-> byte code offset #74
/*     */     //   Java source line #356	-> byte code offset #82
/*     */     //   Java source line #357	-> byte code offset #109
/*     */     //   Java source line #360	-> byte code offset #119
/*     */     //   Java source line #361	-> byte code offset #146
/*     */     //   Java source line #362	-> byte code offset #156
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	157	0	this	DuplexConnectionPool
/*     */     //   7	76	1	idles	List<Connection>
/*     */     //   15	105	2	actives	List<Connection>
/*     */     //   67	6	3	localObject	Object
/*     */     //   88	48	3	localIterator	java.util.Iterator
/*     */     //   107	3	4	connection	Connection
/*     */     //   144	3	4	connection	Connection
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	60	67	finally
/*     */   }
/*     */   
/*     */   public String dump()
/*     */   {
/* 367 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 373 */     List<Connection> actives = new ArrayList();
/* 374 */     List<Connection> idles = new ArrayList();
/* 375 */     lock();
/*     */     try
/*     */     {
/* 378 */       actives.addAll(this.activeConnections);
/* 379 */       idles.addAll(this.idleConnections);
/*     */     }
/*     */     finally
/*     */     {
/* 383 */       unlock();
/*     */     }
/*     */     
/* 386 */     ContainerLifeCycle.dumpObject(out, this);
/* 387 */     ContainerLifeCycle.dump(out, indent, new Collection[] { actives, idles });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean sweep()
/*     */   {
/* 393 */     List<Connection> toSweep = new ArrayList();
/* 394 */     lock();
/*     */     try
/*     */     {
/* 397 */       for (Connection connection : this.activeConnections)
/*     */       {
/* 399 */         if ((connection instanceof Sweeper.Sweepable)) {
/* 400 */           toSweep.add(connection);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 405 */       unlock();
/*     */     }
/*     */     
/* 408 */     for (Connection connection : toSweep)
/*     */     {
/* 410 */       if (((Sweeper.Sweepable)connection).sweep())
/*     */       {
/* 412 */         boolean removed = remove(connection, true);
/* 413 */         LOG.warn("Connection swept: {}{}{} from active connections{}{}", new Object[] { connection, 
/*     */         
/* 415 */           System.lineSeparator(), removed ? "Removed" : "Not removed", 
/*     */           
/* 417 */           System.lineSeparator(), 
/* 418 */           dump() });
/*     */       }
/*     */     }
/*     */     
/* 422 */     return false;
/*     */   }
/*     */   
/*     */   protected void lock()
/*     */   {
/* 427 */     this.lock.lock();
/*     */   }
/*     */   
/*     */   protected void unlock()
/*     */   {
/* 432 */     this.lock.unlock();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String toString()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 85	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:lock	()V
/*     */     //   4: aload_0
/*     */     //   5: getfield 70	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:activeConnections	Ljava/util/Queue;
/*     */     //   8: invokeinterface 100 1 0
/*     */     //   13: istore_1
/*     */     //   14: aload_0
/*     */     //   15: getfield 65	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:idleConnections	Ljava/util/Deque;
/*     */     //   18: invokeinterface 90 1 0
/*     */     //   23: istore_2
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   28: goto +10 -> 38
/*     */     //   31: astore_3
/*     */     //   32: aload_0
/*     */     //   33: invokevirtual 93	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:unlock	()V
/*     */     //   36: aload_3
/*     */     //   37: athrow
/*     */     //   38: ldc_w 315
/*     */     //   41: iconst_5
/*     */     //   42: anewarray 4	java/lang/Object
/*     */     //   45: dup
/*     */     //   46: iconst_0
/*     */     //   47: aload_0
/*     */     //   48: invokevirtual 319	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   51: invokevirtual 324	java/lang/Class:getSimpleName	()Ljava/lang/String;
/*     */     //   54: aastore
/*     */     //   55: dup
/*     */     //   56: iconst_1
/*     */     //   57: aload_0
/*     */     //   58: getfield 47	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:connectionCount	Ljava/util/concurrent/atomic/AtomicInteger;
/*     */     //   61: invokevirtual 81	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */     //   64: invokestatic 133	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   67: aastore
/*     */     //   68: dup
/*     */     //   69: iconst_2
/*     */     //   70: aload_0
/*     */     //   71: getfield 56	com/facebook/presto/jdbc/internal/jetty/client/DuplexConnectionPool:maxConnections	I
/*     */     //   74: invokestatic 133	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   77: aastore
/*     */     //   78: dup
/*     */     //   79: iconst_3
/*     */     //   80: iload_1
/*     */     //   81: invokestatic 133	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   84: aastore
/*     */     //   85: dup
/*     */     //   86: iconst_4
/*     */     //   87: iload_2
/*     */     //   88: invokestatic 133	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */     //   91: aastore
/*     */     //   92: invokestatic 328	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   95: areturn
/*     */     // Line number table:
/*     */     //   Java source line #440	-> byte code offset #0
/*     */     //   Java source line #443	-> byte code offset #4
/*     */     //   Java source line #444	-> byte code offset #14
/*     */     //   Java source line #448	-> byte code offset #24
/*     */     //   Java source line #449	-> byte code offset #28
/*     */     //   Java source line #448	-> byte code offset #31
/*     */     //   Java source line #451	-> byte code offset #38
/*     */     //   Java source line #452	-> byte code offset #48
/*     */     //   Java source line #453	-> byte code offset #61
/*     */     //   Java source line #454	-> byte code offset #74
/*     */     //   Java source line #455	-> byte code offset #81
/*     */     //   Java source line #456	-> byte code offset #88
/*     */     //   Java source line #451	-> byte code offset #92
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	96	0	this	DuplexConnectionPool
/*     */     //   13	2	1	activeSize	int
/*     */     //   38	43	1	activeSize	int
/*     */     //   23	2	2	idleSize	int
/*     */     //   38	50	2	idleSize	int
/*     */     //   31	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	24	31	finally
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\DuplexConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */