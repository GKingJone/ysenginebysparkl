/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.util.concurrent.CancellationException;
/*    */ import java.util.concurrent.CountDownLatch;
/*    */ import java.util.concurrent.atomic.AtomicReference;
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
/*    */ @Deprecated
/*    */ public class BlockingCallback
/*    */   implements Callback.NonBlocking
/*    */ {
/* 36 */   private static final Logger LOG = Log.getLogger(BlockingCallback.class);
/* 37 */   private static Throwable SUCCEEDED = new Throwable()
/*    */   {
/*    */     public String toString() {
/* 40 */       return "SUCCEEDED";
/*    */     }
/*    */   };
/* 43 */   private final CountDownLatch _latch = new CountDownLatch(1);
/* 44 */   private final AtomicReference<Throwable> _state = new AtomicReference();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void succeeded()
/*    */   {
/* 53 */     if (this._state.compareAndSet(null, SUCCEEDED)) {
/* 54 */       this._latch.countDown();
/*    */     }
/*    */   }
/*    */   
/*    */   public void failed(Throwable cause)
/*    */   {
/* 60 */     if (this._state.compareAndSet(null, cause)) {
/* 61 */       this._latch.countDown();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void block()
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 75 */       this._latch.await();
/* 76 */       Throwable state = (Throwable)this._state.get();
/* 77 */       if (state == SUCCEEDED)
/* 78 */         return;
/* 79 */       if ((state instanceof IOException))
/* 80 */         throw ((IOException)state);
/* 81 */       if ((state instanceof CancellationException))
/* 82 */         throw ((CancellationException)state);
/* 83 */       throw new IOException(state);
/*    */     }
/*    */     catch (InterruptedException e)
/*    */     {
/* 87 */       throw new InterruptedIOException() {};
/*    */     }
/*    */     finally
/*    */     {
/* 91 */       this._state.set(null);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 98 */     return String.format("%s@%x{%s}", new Object[] { BlockingCallback.class.getSimpleName(), Integer.valueOf(hashCode()), this._state.get() });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\BlockingCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */