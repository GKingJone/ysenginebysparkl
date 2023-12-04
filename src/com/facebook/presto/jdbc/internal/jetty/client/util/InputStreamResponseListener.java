/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener.Adapter;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class InputStreamResponseListener
/*     */   extends Adapter
/*     */ {
/*  73 */   private static final Logger LOG = Log.getLogger(InputStreamResponseListener.class);
/*  74 */   private static final DeferredContentProvider.Chunk EOF = new DeferredContentProvider.Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
/*  75 */   private final Object lock = this;
/*  76 */   private final CountDownLatch responseLatch = new CountDownLatch(1);
/*  77 */   private final CountDownLatch resultLatch = new CountDownLatch(1);
/*  78 */   private final AtomicReference<InputStream> stream = new AtomicReference();
/*     */   
/*     */   private Response response;
/*     */   
/*     */   private Result result;
/*     */   
/*     */   private Throwable failure;
/*     */   
/*     */   private boolean closed;
/*     */   
/*     */   private DeferredContentProvider.Chunk chunk;
/*     */   
/*     */ 
/*     */   public InputStreamResponseListener() {}
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public InputStreamResponseListener(long maxBufferSize) {}
/*     */   
/*     */ 
/*     */   public void onHeaders(Response response)
/*     */   {
/* 100 */     synchronized (this.lock)
/*     */     {
/* 102 */       this.response = response;
/* 103 */       this.responseLatch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onContent(Response response, ByteBuffer content, Callback callback)
/*     */   {
/* 110 */     if (content.remaining() == 0)
/*     */     {
/* 112 */       if (LOG.isDebugEnabled())
/* 113 */         LOG.debug("Skipped empty content {}", new Object[] { content });
/* 114 */       callback.succeeded();
/* 115 */       return;
/*     */     }
/*     */     
/*     */ 
/* 119 */     synchronized (this.lock)
/*     */     {
/* 121 */       boolean closed = this.closed;
/* 122 */       if (!closed)
/*     */       {
/* 124 */         this.chunk = new DeferredContentProvider.Chunk(content, callback);
/* 125 */         this.lock.notifyAll();
/*     */       }
/*     */     }
/*     */     boolean closed;
/* 129 */     if (closed)
/*     */     {
/* 131 */       if (LOG.isDebugEnabled())
/* 132 */         LOG.debug("InputStream closed, ignored content {}", new Object[] { content });
/* 133 */       callback.failed(new AsynchronousCloseException());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onSuccess(Response response)
/*     */   {
/* 140 */     synchronized (this.lock)
/*     */     {
/* 142 */       this.chunk = EOF;
/* 143 */       this.lock.notifyAll();
/*     */     }
/*     */     
/* 146 */     if (LOG.isDebugEnabled()) {
/* 147 */       LOG.debug("End of content", new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onFailure(Response response, Throwable failure)
/*     */   {
/* 153 */     Callback callback = null;
/* 154 */     synchronized (this.lock)
/*     */     {
/* 156 */       if (this.failure != null)
/* 157 */         return;
/* 158 */       this.failure = failure;
/* 159 */       if (this.chunk != null)
/* 160 */         callback = this.chunk.callback;
/* 161 */       this.lock.notifyAll();
/*     */     }
/*     */     
/* 164 */     if (LOG.isDebugEnabled()) {
/* 165 */       LOG.debug("Content failure", failure);
/*     */     }
/* 167 */     if (callback != null) {
/* 168 */       callback.failed(failure);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onComplete(Result result)
/*     */   {
/* 174 */     Throwable failure = result.getFailure();
/* 175 */     Callback callback = null;
/* 176 */     synchronized (this.lock)
/*     */     {
/* 178 */       this.result = result;
/* 179 */       if ((result.isFailed()) && (this.failure == null))
/*     */       {
/* 181 */         this.failure = failure;
/* 182 */         if (this.chunk != null) {
/* 183 */           callback = this.chunk.callback;
/*     */         }
/*     */       }
/* 186 */       this.responseLatch.countDown();
/* 187 */       this.resultLatch.countDown();
/* 188 */       this.lock.notifyAll();
/*     */     }
/*     */     
/* 191 */     if (LOG.isDebugEnabled())
/*     */     {
/* 193 */       if (failure == null) {
/* 194 */         LOG.debug("Result success", new Object[0]);
/*     */       } else {
/* 196 */         LOG.debug("Result failure", failure);
/*     */       }
/*     */     }
/* 199 */     if (callback != null) {
/* 200 */       callback.failed(failure);
/*     */     }
/*     */   }
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
/*     */   public Response get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, TimeoutException, ExecutionException
/*     */   {
/* 218 */     boolean expired = !this.responseLatch.await(timeout, unit);
/* 219 */     if (expired)
/* 220 */       throw new TimeoutException();
/* 221 */     synchronized (this.lock)
/*     */     {
/*     */ 
/* 224 */       if (this.response == null)
/* 225 */         throw new ExecutionException(this.failure);
/* 226 */       return this.response;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Result await(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, TimeoutException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 57	com/facebook/presto/jdbc/internal/jetty/client/util/InputStreamResponseListener:resultLatch	Ljava/util/concurrent/CountDownLatch;
/*     */     //   4: lload_1
/*     */     //   5: aload_3
/*     */     //   6: invokevirtual 174	java/util/concurrent/CountDownLatch:await	(JLjava/util/concurrent/TimeUnit;)Z
/*     */     //   9: ifne +7 -> 16
/*     */     //   12: iconst_1
/*     */     //   13: goto +4 -> 17
/*     */     //   16: iconst_0
/*     */     //   17: istore 4
/*     */     //   19: iload 4
/*     */     //   21: ifeq +11 -> 32
/*     */     //   24: new 168	java/util/concurrent/TimeoutException
/*     */     //   27: dup
/*     */     //   28: invokespecial 175	java/util/concurrent/TimeoutException:<init>	()V
/*     */     //   31: athrow
/*     */     //   32: aload_0
/*     */     //   33: getfield 48	com/facebook/presto/jdbc/internal/jetty/client/util/InputStreamResponseListener:lock	Ljava/lang/Object;
/*     */     //   36: dup
/*     */     //   37: astore 5
/*     */     //   39: monitorenter
/*     */     //   40: aload_0
/*     */     //   41: getfield 155	com/facebook/presto/jdbc/internal/jetty/client/util/InputStreamResponseListener:result	Lcom/facebook/presto/jdbc/internal/jetty/client/api/Result;
/*     */     //   44: aload 5
/*     */     //   46: monitorexit
/*     */     //   47: areturn
/*     */     //   48: astore 6
/*     */     //   50: aload 5
/*     */     //   52: monitorexit
/*     */     //   53: aload 6
/*     */     //   55: athrow
/*     */     // Line number table:
/*     */     //   Java source line #244	-> byte code offset #0
/*     */     //   Java source line #245	-> byte code offset #19
/*     */     //   Java source line #246	-> byte code offset #24
/*     */     //   Java source line #247	-> byte code offset #32
/*     */     //   Java source line #249	-> byte code offset #40
/*     */     //   Java source line #250	-> byte code offset #48
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	56	0	this	InputStreamResponseListener
/*     */     //   0	56	1	timeout	long
/*     */     //   0	56	3	unit	TimeUnit
/*     */     //   17	3	4	expired	boolean
/*     */     //   37	14	5	Ljava/lang/Object;	Object
/*     */     //   48	6	6	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   40	47	48	finally
/*     */     //   48	53	48	finally
/*     */   }
/*     */   
/*     */   public InputStream getInputStream()
/*     */   {
/* 262 */     InputStream result = new Input(null);
/* 263 */     if (this.stream.compareAndSet(null, result))
/* 264 */       return result;
/* 265 */     return IO.getClosedStream();
/*     */   }
/*     */   
/*     */   private class Input extends InputStream
/*     */   {
/*     */     private Input() {}
/*     */     
/*     */     public int read() throws IOException {
/* 273 */       byte[] tmp = new byte[1];
/* 274 */       int read = read(tmp);
/* 275 */       if (read < 0)
/* 276 */         return read;
/* 277 */       return tmp[0] & 0xFF;
/*     */     }
/*     */     
/*     */ 
/*     */     public int read(byte[] b, int offset, int length)
/*     */       throws IOException
/*     */     {
/*     */       try
/*     */       {
/* 286 */         Callback callback = null;
/* 287 */         synchronized (InputStreamResponseListener.this.lock)
/*     */         {
/*     */           for (;;)
/*     */           {
/* 291 */             if (InputStreamResponseListener.this.failure != null)
/* 292 */               throw toIOException(InputStreamResponseListener.this.failure);
/* 293 */             if (InputStreamResponseListener.this.chunk == InputStreamResponseListener.EOF)
/* 294 */               return -1;
/* 295 */             if (InputStreamResponseListener.this.closed)
/* 296 */               throw new AsynchronousCloseException();
/* 297 */             if (InputStreamResponseListener.this.chunk != null)
/*     */               break;
/* 299 */             InputStreamResponseListener.this.lock.wait();
/*     */           }
/*     */           
/* 302 */           ByteBuffer buffer = InputStreamResponseListener.this.chunk.buffer;
/* 303 */           int result = Math.min(buffer.remaining(), length);
/* 304 */           buffer.get(b, offset, result);
/* 305 */           if (!buffer.hasRemaining())
/*     */           {
/* 307 */             callback = InputStreamResponseListener.this.chunk.callback;
/* 308 */             InputStreamResponseListener.this.chunk = null;
/*     */           } }
/*     */         int result;
/* 311 */         if (callback != null)
/* 312 */           callback.succeeded();
/* 313 */         return result;
/*     */       }
/*     */       catch (InterruptedException x)
/*     */       {
/* 317 */         throw new InterruptedIOException();
/*     */       }
/*     */     }
/*     */     
/*     */     private IOException toIOException(Throwable failure)
/*     */     {
/* 323 */       if ((failure instanceof IOException)) {
/* 324 */         return (IOException)failure;
/*     */       }
/* 326 */       return new IOException(failure);
/*     */     }
/*     */     
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 332 */       Callback callback = null;
/* 333 */       synchronized (InputStreamResponseListener.this.lock)
/*     */       {
/* 335 */         if (InputStreamResponseListener.this.closed)
/* 336 */           return;
/* 337 */         InputStreamResponseListener.this.closed = true;
/* 338 */         if (InputStreamResponseListener.this.chunk != null)
/* 339 */           callback = InputStreamResponseListener.this.chunk.callback;
/* 340 */         InputStreamResponseListener.this.lock.notifyAll();
/*     */       }
/*     */       
/* 343 */       if (InputStreamResponseListener.LOG.isDebugEnabled()) {
/* 344 */         InputStreamResponseListener.LOG.debug("InputStream close", new Object[0]);
/*     */       }
/* 346 */       if (callback != null) {
/* 347 */         callback.failed(new AsynchronousCloseException());
/*     */       }
/* 349 */       super.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\InputStreamResponseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */