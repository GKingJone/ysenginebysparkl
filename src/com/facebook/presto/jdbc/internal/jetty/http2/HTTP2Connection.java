/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.parser.Parser;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.AbstractConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ConcurrentArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Factory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Producer;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public class HTTP2Connection
/*     */   extends AbstractConnection
/*     */ {
/*  39 */   protected static final Logger LOG = Log.getLogger(HTTP2Connection.class);
/*     */   
/*  41 */   private final Queue<Runnable> tasks = new ConcurrentArrayQueue();
/*     */   private final ByteBufferPool byteBufferPool;
/*     */   private final Parser parser;
/*     */   private final ISession session;
/*     */   private final int bufferSize;
/*  46 */   private final HTTP2Producer producer = new HTTP2Producer();
/*     */   private final ExecutionStrategy executionStrategy;
/*     */   
/*     */   public HTTP2Connection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, Parser parser, ISession session, int bufferSize)
/*     */   {
/*  51 */     super(endPoint, executor);
/*  52 */     this.byteBufferPool = byteBufferPool;
/*  53 */     this.parser = parser;
/*  54 */     this.session = session;
/*  55 */     this.bufferSize = bufferSize;
/*  56 */     this.executionStrategy = Factory.instanceFor(this.producer, executor);
/*     */   }
/*     */   
/*     */   public ISession getSession()
/*     */   {
/*  61 */     return this.session;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Parser getParser()
/*     */   {
/*  67 */     return this.parser;
/*     */   }
/*     */   
/*     */   protected void setInputBuffer(ByteBuffer buffer)
/*     */   {
/*  72 */     this.producer.buffer = buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/*  78 */     if (LOG.isDebugEnabled())
/*  79 */       LOG.debug("HTTP2 Open {} ", new Object[] { this });
/*  80 */     super.onOpen();
/*  81 */     this.executionStrategy.execute();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onClose()
/*     */   {
/*  87 */     if (LOG.isDebugEnabled())
/*  88 */       LOG.debug("HTTP2 Close {} ", new Object[] { this });
/*  89 */     super.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onFillable()
/*     */   {
/*  95 */     if (LOG.isDebugEnabled())
/*  96 */       LOG.debug("HTTP2 onFillable {} ", new Object[] { this });
/*  97 */     this.executionStrategy.execute();
/*     */   }
/*     */   
/*     */   private int fill(EndPoint endPoint, ByteBuffer buffer)
/*     */   {
/*     */     try
/*     */     {
/* 104 */       if (endPoint.isInputShutdown())
/* 105 */         return -1;
/* 106 */       return endPoint.fill(buffer);
/*     */     }
/*     */     catch (IOException x)
/*     */     {
/* 110 */       LOG.debug("Could not read from " + endPoint, x); }
/* 111 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean onIdleExpired()
/*     */   {
/* 118 */     boolean close = this.session.onIdleTimeout();
/* 119 */     boolean idle = isFillInterested();
/* 120 */     if ((close) && (idle))
/* 121 */       this.session.close(ErrorCode.NO_ERROR.code, "idle_timeout", Callback.NOOP);
/* 122 */     return false;
/*     */   }
/*     */   
/*     */   protected void offerTask(Runnable task, boolean dispatch)
/*     */   {
/* 127 */     this.tasks.offer(task);
/* 128 */     if (dispatch) {
/* 129 */       this.executionStrategy.dispatch();
/*     */     } else {
/* 131 */       this.executionStrategy.execute();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 139 */     this.session.close(ErrorCode.NO_ERROR.code, "close", Callback.NOOP);
/*     */   }
/*     */   
/*     */   protected class HTTP2Producer implements Producer
/*     */   {
/*     */     private ByteBuffer buffer;
/*     */     
/*     */     protected HTTP2Producer() {}
/*     */     
/*     */     public Runnable produce() {
/* 149 */       Runnable task = (Runnable)HTTP2Connection.this.tasks.poll();
/* 150 */       if (HTTP2Connection.LOG.isDebugEnabled())
/* 151 */         HTTP2Connection.LOG.debug("Dequeued task {}", new Object[] { task });
/* 152 */       if (task != null) {
/* 153 */         return task;
/*     */       }
/* 155 */       if (HTTP2Connection.this.isFillInterested()) {
/* 156 */         return null;
/*     */       }
/* 158 */       if (this.buffer == null)
/* 159 */         this.buffer = HTTP2Connection.this.byteBufferPool.acquire(HTTP2Connection.this.bufferSize, false);
/* 160 */       boolean looping = BufferUtil.hasContent(this.buffer);
/*     */       for (;;)
/*     */       {
/* 163 */         if (looping)
/*     */         {
/* 165 */           while (this.buffer.hasRemaining()) {
/* 166 */             HTTP2Connection.this.parser.parse(this.buffer);
/*     */           }
/* 168 */           task = (Runnable)HTTP2Connection.this.tasks.poll();
/* 169 */           if (HTTP2Connection.LOG.isDebugEnabled())
/* 170 */             HTTP2Connection.LOG.debug("Dequeued task {}", new Object[] { task });
/* 171 */           if (task != null)
/*     */           {
/* 173 */             release();
/* 174 */             return task;
/*     */           }
/*     */         }
/*     */         
/* 178 */         int filled = HTTP2Connection.this.fill(HTTP2Connection.this.getEndPoint(), this.buffer);
/* 179 */         if (HTTP2Connection.LOG.isDebugEnabled()) {
/* 180 */           HTTP2Connection.LOG.debug("Filled {} bytes", filled);
/*     */         }
/* 182 */         if (filled == 0)
/*     */         {
/* 184 */           release();
/* 185 */           HTTP2Connection.this.fillInterested();
/* 186 */           return null;
/*     */         }
/* 188 */         if (filled < 0)
/*     */         {
/* 190 */           release();
/* 191 */           HTTP2Connection.this.session.onShutdown();
/* 192 */           return null;
/*     */         }
/*     */         
/* 195 */         looping = true;
/*     */       }
/*     */     }
/*     */     
/*     */     private void release()
/*     */     {
/* 201 */       if ((this.buffer != null) && (!this.buffer.hasRemaining()))
/*     */       {
/* 203 */         HTTP2Connection.this.byteBufferPool.release(this.buffer);
/* 204 */         this.buffer = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\HTTP2Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */