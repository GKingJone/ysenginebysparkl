/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.Condition;
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
/*     */ public class SharedBlockingCallback
/*     */ {
/*  51 */   static final Logger LOG = Log.getLogger(SharedBlockingCallback.class);
/*  52 */   private static Throwable IDLE = new Throwable()
/*     */   {
/*     */ 
/*     */     public String toString()
/*     */     {
/*  57 */       return "IDLE";
/*     */     }
/*     */   };
/*  60 */   private static Throwable SUCCEEDED = new Throwable()
/*     */   {
/*     */ 
/*     */     public String toString()
/*     */     {
/*  65 */       return "SUCCEEDED";
/*     */     }
/*     */   };
/*  68 */   private static Throwable FAILED = new Throwable()
/*     */   {
/*     */ 
/*     */     public String toString()
/*     */     {
/*  73 */       return "FAILED";
/*     */     }
/*     */   };
/*     */   
/*  77 */   private final ReentrantLock _lock = new ReentrantLock();
/*  78 */   private final Condition _idle = this._lock.newCondition();
/*  79 */   private final Condition _complete = this._lock.newCondition();
/*  80 */   private Blocker _blocker = new Blocker();
/*     */   
/*     */   protected long getIdleTimeout()
/*     */   {
/*  84 */     return -1L;
/*     */   }
/*     */   
/*     */   public Blocker acquire() throws IOException
/*     */   {
/*  89 */     this._lock.lock();
/*  90 */     long idle = getIdleTimeout();
/*     */     try
/*     */     {
/*  93 */       while (this._blocker._state != IDLE)
/*     */       {
/*  95 */         if ((idle > 0L) && (idle < 4611686018427387903L))
/*     */         {
/*     */ 
/*  98 */           if (!this._idle.await(idle * 2L, TimeUnit.MILLISECONDS)) {
/*  99 */             throw new IOException(new TimeoutException());
/*     */           }
/*     */         } else
/* 102 */           this._idle.await();
/*     */       }
/* 104 */       this._blocker._state = null;
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 108 */       throw new InterruptedIOException();
/*     */     }
/*     */     finally
/*     */     {
/* 112 */       this._lock.unlock();
/*     */     }
/* 114 */     return this._blocker;
/*     */   }
/*     */   
/*     */   protected void notComplete(Blocker blocker)
/*     */   {
/* 119 */     LOG.warn("Blocker not complete {}", new Object[] { blocker });
/* 120 */     if (LOG.isDebugEnabled()) {
/* 121 */       LOG.debug(new Throwable());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class BlockerTimeoutException
/*     */     extends TimeoutException
/*     */   {}
/*     */   
/*     */   public class Blocker
/*     */     implements Callback.NonBlocking, Closeable
/*     */   {
/* 133 */     private Throwable _state = SharedBlockingCallback.IDLE;
/*     */     
/*     */     protected Blocker() {}
/*     */     
/*     */     /* Error */
/*     */     public void succeeded()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   4: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   7: invokevirtual 49	java/util/concurrent/locks/ReentrantLock:lock	()V
/*     */       //   10: aload_0
/*     */       //   11: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   14: ifnonnull +25 -> 39
/*     */       //   17: aload_0
/*     */       //   18: invokestatic 52	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$300	()Ljava/lang/Throwable;
/*     */       //   21: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   24: aload_0
/*     */       //   25: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   28: invokestatic 56	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   31: invokeinterface 61 1 0
/*     */       //   36: goto +15 -> 51
/*     */       //   39: new 63	java/lang/IllegalStateException
/*     */       //   42: dup
/*     */       //   43: aload_0
/*     */       //   44: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   47: invokespecial 66	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   50: athrow
/*     */       //   51: aload_0
/*     */       //   52: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   55: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   58: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   61: goto +16 -> 77
/*     */       //   64: astore_1
/*     */       //   65: aload_0
/*     */       //   66: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   69: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   72: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   75: aload_1
/*     */       //   76: athrow
/*     */       //   77: return
/*     */       // Line number table:
/*     */       //   Java source line #142	-> byte code offset #0
/*     */       //   Java source line #145	-> byte code offset #10
/*     */       //   Java source line #147	-> byte code offset #17
/*     */       //   Java source line #148	-> byte code offset #24
/*     */       //   Java source line #151	-> byte code offset #39
/*     */       //   Java source line #155	-> byte code offset #51
/*     */       //   Java source line #156	-> byte code offset #61
/*     */       //   Java source line #155	-> byte code offset #64
/*     */       //   Java source line #157	-> byte code offset #77
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	78	0	this	Blocker
/*     */       //   64	12	1	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   10	51	64	finally
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void failed(Throwable cause)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   4: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   7: invokevirtual 49	java/util/concurrent/locks/ReentrantLock:lock	()V
/*     */       //   10: aload_0
/*     */       //   11: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   14: ifnonnull +59 -> 73
/*     */       //   17: aload_1
/*     */       //   18: ifnonnull +13 -> 31
/*     */       //   21: aload_0
/*     */       //   22: invokestatic 75	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$500	()Ljava/lang/Throwable;
/*     */       //   25: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   28: goto +30 -> 58
/*     */       //   31: aload_1
/*     */       //   32: instanceof 14
/*     */       //   35: ifeq +18 -> 53
/*     */       //   38: aload_0
/*     */       //   39: new 77	java/io/IOException
/*     */       //   42: dup
/*     */       //   43: aload_1
/*     */       //   44: invokespecial 78	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   47: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   50: goto +8 -> 58
/*     */       //   53: aload_0
/*     */       //   54: aload_1
/*     */       //   55: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   58: aload_0
/*     */       //   59: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   62: invokestatic 56	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   65: invokeinterface 61 1 0
/*     */       //   70: goto +15 -> 85
/*     */       //   73: new 63	java/lang/IllegalStateException
/*     */       //   76: dup
/*     */       //   77: aload_0
/*     */       //   78: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   81: invokespecial 66	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   84: athrow
/*     */       //   85: aload_0
/*     */       //   86: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   89: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   92: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   95: goto +16 -> 111
/*     */       //   98: astore_2
/*     */       //   99: aload_0
/*     */       //   100: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   103: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   106: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   109: aload_2
/*     */       //   110: athrow
/*     */       //   111: return
/*     */       // Line number table:
/*     */       //   Java source line #162	-> byte code offset #0
/*     */       //   Java source line #165	-> byte code offset #10
/*     */       //   Java source line #167	-> byte code offset #17
/*     */       //   Java source line #168	-> byte code offset #21
/*     */       //   Java source line #169	-> byte code offset #31
/*     */       //   Java source line #171	-> byte code offset #38
/*     */       //   Java source line #173	-> byte code offset #53
/*     */       //   Java source line #174	-> byte code offset #58
/*     */       //   Java source line #177	-> byte code offset #73
/*     */       //   Java source line #181	-> byte code offset #85
/*     */       //   Java source line #182	-> byte code offset #95
/*     */       //   Java source line #181	-> byte code offset #98
/*     */       //   Java source line #183	-> byte code offset #111
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	112	0	this	Blocker
/*     */       //   0	112	1	cause	Throwable
/*     */       //   98	12	2	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   10	85	98	finally
/*     */     }
/*     */     
/*     */     public void block()
/*     */       throws IOException
/*     */     {
/* 194 */       SharedBlockingCallback.this._lock.lock();
/* 195 */       long idle = SharedBlockingCallback.this.getIdleTimeout();
/*     */       try
/*     */       {
/* 198 */         while (this._state == null)
/*     */         {
/* 200 */           if ((idle > 0L) && (idle < 4611686018427387903L))
/*     */           {
/*     */ 
/* 203 */             if (!SharedBlockingCallback.this._complete.await(idle + idle / 2L, TimeUnit.MILLISECONDS))
/*     */             {
/*     */ 
/* 206 */               this._state = new BlockerTimeoutException(null);
/*     */             }
/*     */           }
/*     */           else {
/* 210 */             SharedBlockingCallback.this._complete.await();
/*     */           }
/*     */         }
/*     */         
/* 214 */         if (this._state == SharedBlockingCallback.SUCCEEDED)
/* 215 */           return;
/* 216 */         if (this._state == SharedBlockingCallback.IDLE)
/* 217 */           throw new IllegalStateException("IDLE");
/* 218 */         if ((this._state instanceof IOException))
/* 219 */           throw ((IOException)this._state);
/* 220 */         if ((this._state instanceof CancellationException))
/* 221 */           throw ((CancellationException)this._state);
/* 222 */         if ((this._state instanceof RuntimeException))
/* 223 */           throw ((RuntimeException)this._state);
/* 224 */         if ((this._state instanceof Error))
/* 225 */           throw ((Error)this._state);
/* 226 */         throw new IOException(this._state);
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/* 230 */         throw new InterruptedIOException();
/*     */       }
/*     */       finally
/*     */       {
/* 234 */         SharedBlockingCallback.this._lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void close()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   4: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   7: invokevirtual 49	java/util/concurrent/locks/ReentrantLock:lock	()V
/*     */       //   10: aload_0
/*     */       //   11: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   14: invokestatic 35	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$100	()Ljava/lang/Throwable;
/*     */       //   17: if_acmpne +13 -> 30
/*     */       //   20: new 63	java/lang/IllegalStateException
/*     */       //   23: dup
/*     */       //   24: ldc 107
/*     */       //   26: invokespecial 110	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
/*     */       //   29: athrow
/*     */       //   30: aload_0
/*     */       //   31: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   34: ifnonnull +11 -> 45
/*     */       //   37: aload_0
/*     */       //   38: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   41: aload_0
/*     */       //   42: invokevirtual 128	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:notComplete	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker;)V
/*     */       //   45: aload_0
/*     */       //   46: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   49: instanceof 14
/*     */       //   52: ifeq +25 -> 77
/*     */       //   55: aload_0
/*     */       //   56: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   59: new 2	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker
/*     */       //   62: dup
/*     */       //   63: aload_0
/*     */       //   64: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   67: invokespecial 130	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:<init>	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)V
/*     */       //   70: invokestatic 134	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$702	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker;)Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker;
/*     */       //   73: pop
/*     */       //   74: goto +10 -> 84
/*     */       //   77: aload_0
/*     */       //   78: invokestatic 35	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$100	()Ljava/lang/Throwable;
/*     */       //   81: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   84: aload_0
/*     */       //   85: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   88: invokestatic 137	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$800	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   91: invokeinterface 61 1 0
/*     */       //   96: aload_0
/*     */       //   97: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   100: invokestatic 56	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   103: invokeinterface 61 1 0
/*     */       //   108: aload_0
/*     */       //   109: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   112: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   115: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   118: goto +16 -> 134
/*     */       //   121: astore_1
/*     */       //   122: aload_0
/*     */       //   123: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   126: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   129: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   132: aload_1
/*     */       //   133: athrow
/*     */       //   134: goto +95 -> 229
/*     */       //   137: astore_2
/*     */       //   138: aload_0
/*     */       //   139: getfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   142: instanceof 14
/*     */       //   145: ifeq +25 -> 170
/*     */       //   148: aload_0
/*     */       //   149: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   152: new 2	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker
/*     */       //   155: dup
/*     */       //   156: aload_0
/*     */       //   157: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   160: invokespecial 130	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:<init>	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)V
/*     */       //   163: invokestatic 134	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$702	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker;)Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker;
/*     */       //   166: pop
/*     */       //   167: goto +10 -> 177
/*     */       //   170: aload_0
/*     */       //   171: invokestatic 35	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$100	()Ljava/lang/Throwable;
/*     */       //   174: putfield 37	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:_state	Ljava/lang/Throwable;
/*     */       //   177: aload_0
/*     */       //   178: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   181: invokestatic 137	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$800	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   184: invokeinterface 61 1 0
/*     */       //   189: aload_0
/*     */       //   190: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   193: invokestatic 56	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/Condition;
/*     */       //   196: invokeinterface 61 1 0
/*     */       //   201: aload_0
/*     */       //   202: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   205: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   208: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   211: goto +16 -> 227
/*     */       //   214: astore_3
/*     */       //   215: aload_0
/*     */       //   216: getfield 28	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback$Blocker:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;
/*     */       //   219: invokestatic 44	com/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/SharedBlockingCallback;)Ljava/util/concurrent/locks/ReentrantLock;
/*     */       //   222: invokevirtual 69	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*     */       //   225: aload_3
/*     */       //   226: athrow
/*     */       //   227: aload_2
/*     */       //   228: athrow
/*     */       //   229: return
/*     */       // Line number table:
/*     */       //   Java source line #244	-> byte code offset #0
/*     */       //   Java source line #247	-> byte code offset #10
/*     */       //   Java source line #248	-> byte code offset #20
/*     */       //   Java source line #249	-> byte code offset #30
/*     */       //   Java source line #250	-> byte code offset #37
/*     */       //   Java source line #257	-> byte code offset #45
/*     */       //   Java source line #259	-> byte code offset #55
/*     */       //   Java source line #262	-> byte code offset #77
/*     */       //   Java source line #263	-> byte code offset #84
/*     */       //   Java source line #264	-> byte code offset #96
/*     */       //   Java source line #268	-> byte code offset #108
/*     */       //   Java source line #269	-> byte code offset #118
/*     */       //   Java source line #268	-> byte code offset #121
/*     */       //   Java source line #270	-> byte code offset #134
/*     */       //   Java source line #254	-> byte code offset #137
/*     */       //   Java source line #257	-> byte code offset #138
/*     */       //   Java source line #259	-> byte code offset #148
/*     */       //   Java source line #262	-> byte code offset #170
/*     */       //   Java source line #263	-> byte code offset #177
/*     */       //   Java source line #264	-> byte code offset #189
/*     */       //   Java source line #268	-> byte code offset #201
/*     */       //   Java source line #269	-> byte code offset #211
/*     */       //   Java source line #268	-> byte code offset #214
/*     */       //   Java source line #271	-> byte code offset #229
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	230	0	this	Blocker
/*     */       //   121	12	1	localObject1	Object
/*     */       //   137	91	2	localObject2	Object
/*     */       //   214	12	3	localObject3	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   45	108	121	finally
/*     */       //   10	45	137	finally
/*     */       //   138	201	214	finally
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 276 */       SharedBlockingCallback.this._lock.lock();
/*     */       try
/*     */       {
/* 279 */         return String.format("%s@%x{%s}", new Object[] { Blocker.class.getSimpleName(), Integer.valueOf(hashCode()), this._state });
/*     */       }
/*     */       finally
/*     */       {
/* 283 */         SharedBlockingCallback.this._lock.unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\SharedBlockingCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */