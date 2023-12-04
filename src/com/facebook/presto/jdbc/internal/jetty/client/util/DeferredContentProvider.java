/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.AsyncContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.AsyncContentProvider.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.Synchronizable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ public class DeferredContentProvider
/*     */   implements AsyncContentProvider, Callback, Closeable
/*     */ {
/*  90 */   private static final Chunk CLOSE = new Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
/*     */   
/*  92 */   private final Object lock = this;
/*  93 */   private final ArrayQueue<Chunk> chunks = new ArrayQueue(4, 64, this.lock);
/*  94 */   private final AtomicReference<AsyncContentProvider.Listener> listener = new AtomicReference();
/*  95 */   private final DeferredContentProviderIterator iterator = new DeferredContentProviderIterator(null);
/*  96 */   private final AtomicBoolean closed = new AtomicBoolean();
/*  97 */   private long length = -1L;
/*     */   
/*     */ 
/*     */   private int size;
/*     */   
/*     */ 
/*     */   private Throwable failure;
/*     */   
/*     */ 
/*     */   public DeferredContentProvider(ByteBuffer... buffers)
/*     */   {
/* 108 */     for (ByteBuffer buffer : buffers) {
/* 109 */       offer(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setListener(AsyncContentProvider.Listener listener)
/*     */   {
/* 115 */     if (!this.listener.compareAndSet(null, listener)) {
/* 116 */       throw new IllegalStateException(String.format("The same %s instance cannot be used in multiple requests", new Object[] {AsyncContentProvider.class
/* 117 */         .getName() }));
/*     */     }
/* 119 */     if (isClosed())
/*     */     {
/* 121 */       synchronized (this.lock)
/*     */       {
/* 123 */         long total = 0L;
/* 124 */         for (Chunk chunk : this.chunks)
/* 125 */           total += chunk.buffer.remaining();
/* 126 */         this.length = total;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 134 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean offer(ByteBuffer buffer)
/*     */   {
/* 146 */     return offer(buffer, Callback.NOOP);
/*     */   }
/*     */   
/*     */   public boolean offer(ByteBuffer buffer, Callback callback)
/*     */   {
/* 151 */     return offer(new Chunk(buffer, callback));
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean offer(Chunk chunk)
/*     */   {
/* 157 */     boolean result = false;
/* 158 */     synchronized (this.lock)
/*     */     {
/* 160 */       Throwable failure = this.failure;
/* 161 */       if (failure == null)
/*     */       {
/* 163 */         result = this.chunks.offer(chunk);
/* 164 */         if ((result) && (chunk != CLOSE))
/* 165 */           this.size += 1;
/*     */       } }
/*     */     Throwable failure;
/* 168 */     if (failure != null) {
/* 169 */       chunk.callback.failed(failure);
/* 170 */     } else if (result)
/* 171 */       notifyListener();
/* 172 */     return result;
/*     */   }
/*     */   
/*     */   private void clear()
/*     */   {
/* 177 */     synchronized (this.lock)
/*     */     {
/* 179 */       this.chunks.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public void flush() throws IOException
/*     */   {
/* 185 */     synchronized (this.lock)
/*     */     {
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/* 191 */           if (this.failure != null)
/* 192 */             throw new IOException(this.failure);
/* 193 */           if (this.size == 0)
/*     */             break;
/* 195 */           this.lock.wait();
/*     */         }
/*     */       }
/*     */       catch (InterruptedException x)
/*     */       {
/* 200 */         throw new InterruptedIOException();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 211 */     if (this.closed.compareAndSet(false, true)) {
/* 212 */       offer(CLOSE);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 217 */     return this.closed.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable failure)
/*     */   {
/* 223 */     this.iterator.failed(failure);
/*     */   }
/*     */   
/*     */   private void notifyListener()
/*     */   {
/* 228 */     AsyncContentProvider.Listener listener = (AsyncContentProvider.Listener)this.listener.get();
/* 229 */     if (listener != null) {
/* 230 */       listener.onContent();
/*     */     }
/*     */   }
/*     */   
/*     */   public Iterator<ByteBuffer> iterator()
/*     */   {
/* 236 */     return this.iterator;
/*     */   }
/*     */   
/*     */   private class DeferredContentProviderIterator implements Iterator<ByteBuffer>, Callback, Synchronizable
/*     */   {
/*     */     private Chunk current;
/*     */     
/*     */     private DeferredContentProviderIterator() {}
/*     */     
/*     */     public boolean hasNext() {
/* 246 */       synchronized (DeferredContentProvider.this.lock)
/*     */       {
/* 248 */         return DeferredContentProvider.this.chunks.peek() != DeferredContentProvider.CLOSE;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public ByteBuffer next()
/*     */     {
/* 255 */       synchronized (DeferredContentProvider.this.lock)
/*     */       {
/* 257 */         Chunk chunk = this.current = (Chunk)DeferredContentProvider.this.chunks.poll();
/* 258 */         if (chunk == DeferredContentProvider.CLOSE)
/*     */         {
/*     */ 
/*     */ 
/* 262 */           DeferredContentProvider.this.chunks.add(0, DeferredContentProvider.CLOSE);
/* 263 */           throw new NoSuchElementException();
/*     */         }
/* 265 */         return chunk == null ? null : chunk.buffer;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void remove()
/*     */     {
/* 272 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 279 */       synchronized (DeferredContentProvider.this.lock)
/*     */       {
/* 281 */         Chunk chunk = this.current;
/* 282 */         if (chunk != null)
/*     */         {
/* 284 */           DeferredContentProvider.access$406(DeferredContentProvider.this);
/* 285 */           DeferredContentProvider.this.lock.notify();
/*     */         } }
/*     */       Chunk chunk;
/* 288 */       if (chunk != null) {
/* 289 */         chunk.callback.succeeded();
/*     */       }
/*     */     }
/*     */     
/*     */     public void failed(Throwable x)
/*     */     {
/* 295 */       List<Chunk> chunks = new ArrayList();
/* 296 */       synchronized (DeferredContentProvider.this.lock)
/*     */       {
/* 298 */         DeferredContentProvider.this.failure = x;
/*     */         
/* 300 */         Chunk chunk = this.current;
/* 301 */         this.current = null;
/* 302 */         if (chunk != null)
/* 303 */           chunks.add(chunk);
/* 304 */         chunks.addAll(DeferredContentProvider.this.chunks);
/* 305 */         DeferredContentProvider.this.clear();
/* 306 */         DeferredContentProvider.this.lock.notify();
/*     */       }
/* 308 */       for (??? = chunks.iterator(); ((Iterator)???).hasNext();) { Chunk chunk = (Chunk)((Iterator)???).next();
/* 309 */         chunk.callback.failed(x);
/*     */       }
/*     */     }
/*     */     
/*     */     public Object getLock()
/*     */     {
/* 315 */       return DeferredContentProvider.this.lock;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Chunk
/*     */   {
/*     */     public final ByteBuffer buffer;
/*     */     public final Callback callback;
/*     */     
/*     */     public Chunk(ByteBuffer buffer, Callback callback)
/*     */     {
/* 326 */       this.buffer = ((ByteBuffer)Objects.requireNonNull(buffer));
/* 327 */       this.callback = ((Callback)Objects.requireNonNull(callback));
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 333 */       return String.format("%s@%x", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\DeferredContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */