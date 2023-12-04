/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker.Lock;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Queue;
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
/*     */ public class ByteArrayEndPoint
/*     */   extends AbstractEndPoint
/*     */ {
/*  44 */   static final Logger LOG = Log.getLogger(ByteArrayEndPoint.class);
/*  45 */   public static final InetSocketAddress NOIP = new InetSocketAddress(0);
/*  46 */   private static final ByteBuffer EOF = BufferUtil.allocate(0);
/*     */   
/*  48 */   private final Runnable _runFillable = new Runnable()
/*     */   {
/*     */ 
/*     */     public void run()
/*     */     {
/*  53 */       ByteArrayEndPoint.this.getFillInterest().fillable();
/*     */     }
/*     */   };
/*     */   
/*  57 */   private final Locker _locker = new Locker();
/*  58 */   private final Queue<ByteBuffer> _inQ = new ArrayQueue();
/*     */   
/*     */   private ByteBuffer _out;
/*     */   
/*     */   private boolean _ishut;
/*     */   
/*     */   private boolean _oshut;
/*     */   
/*     */   private boolean _closed;
/*     */   private boolean _growOutput;
/*     */   
/*     */   public ByteArrayEndPoint()
/*     */   {
/*  71 */     this(null, 0L, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteArrayEndPoint(byte[] input, int outputSize)
/*     */   {
/*  81 */     this(null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteArrayEndPoint(String input, int outputSize)
/*     */   {
/*  91 */     this(null, 0L, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteArrayEndPoint(Scheduler scheduler, long idleTimeoutMs)
/*     */   {
/*  97 */     this(scheduler, idleTimeoutMs, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, byte[] input, int outputSize)
/*     */   {
/* 103 */     this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, String input, int outputSize)
/*     */   {
/* 109 */     this(timer, idleTimeoutMs, input != null ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, ByteBuffer input, ByteBuffer output)
/*     */   {
/* 115 */     super(timer, NOIP, NOIP);
/* 116 */     if (BufferUtil.hasContent(input))
/* 117 */       addInput(input);
/* 118 */     this._out = (output == null ? BufferUtil.allocate(1024) : output);
/* 119 */     setIdleTimeout(idleTimeoutMs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onIncompleteFlush() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void execute(Runnable task)
/*     */   {
/* 132 */     new Thread(task, "BAEPoint-" + Integer.toHexString(hashCode())).start();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void needsFillInterest()
/*     */     throws IOException
/*     */   {
/* 139 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 141 */       if (this._closed) {
/* 142 */         throw new ClosedChannelException();
/*     */       }
/* 144 */       ByteBuffer in = (ByteBuffer)this._inQ.peek();
/* 145 */       if ((BufferUtil.hasContent(in)) || (in == EOF)) {
/* 146 */         execute(this._runFillable);
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 139 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/* 147 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInputEOF()
/*     */   {
/* 155 */     addInput((ByteBuffer)null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addInput(ByteBuffer in)
/*     */   {
/* 164 */     boolean fillable = false;
/* 165 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 167 */       if (this._inQ.peek() == EOF)
/* 168 */         throw new RuntimeIOException(new EOFException());
/* 169 */       boolean was_empty = this._inQ.isEmpty();
/* 170 */       if (in == null)
/*     */       {
/* 172 */         this._inQ.add(EOF);
/* 173 */         fillable = true;
/*     */       }
/* 175 */       if (BufferUtil.hasContent(in))
/*     */       {
/* 177 */         this._inQ.add(in);
/* 178 */         fillable = was_empty;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 165 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 181 */     if (fillable) {
/* 182 */       this._runFillable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInputAndExecute(ByteBuffer in) {
/* 187 */     boolean fillable = false;
/* 188 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 190 */       if (this._inQ.peek() == EOF)
/* 191 */         throw new RuntimeIOException(new EOFException());
/* 192 */       boolean was_empty = this._inQ.isEmpty();
/* 193 */       if (in == null)
/*     */       {
/* 195 */         this._inQ.add(EOF);
/* 196 */         fillable = true;
/*     */       }
/* 198 */       if (BufferUtil.hasContent(in))
/*     */       {
/* 200 */         this._inQ.add(in);
/* 201 */         fillable = was_empty;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 188 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 204 */     if (fillable) {
/* 205 */       execute(this._runFillable);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInput(String s)
/*     */   {
/* 211 */     addInput(BufferUtil.toBuffer(s, StandardCharsets.UTF_8));
/*     */   }
/*     */   
/*     */ 
/*     */   public void addInput(String s, Charset charset)
/*     */   {
/* 217 */     addInput(BufferUtil.toBuffer(s, charset));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteBuffer getOutput()
/*     */   {
/* 226 */     return this._out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOutputString()
/*     */   {
/* 235 */     return getOutputString(StandardCharsets.UTF_8);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOutputString(Charset charset)
/*     */   {
/* 245 */     return BufferUtil.toString(this._out, charset);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteBuffer takeOutput()
/*     */   {
/* 254 */     ByteBuffer b = this._out;
/* 255 */     this._out = BufferUtil.allocate(b.capacity());
/* 256 */     getWriteFlusher().completeWrite();
/* 257 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String takeOutputString()
/*     */   {
/* 266 */     return takeOutputString(StandardCharsets.UTF_8);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String takeOutputString(Charset charset)
/*     */   {
/* 276 */     ByteBuffer buffer = takeOutput();
/* 277 */     return BufferUtil.toString(buffer, charset);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOutput(ByteBuffer out)
/*     */   {
/* 286 */     this._out = out;
/* 287 */     getWriteFlusher().completeWrite();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 297 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 299 */       return !this._closed;
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 297 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 300 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInputShutdown()
/*     */   {
/* 309 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 311 */       return (this._ishut) || (this._closed);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 309 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 312 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOutputShutdown()
/*     */   {
/* 321 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 323 */       return (this._oshut) || (this._closed);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 321 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 324 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void shutdownInput()
/*     */   {
/* 330 */     boolean close = false;
/* 331 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 333 */       this._ishut = true;
/* 334 */       if ((this._oshut) && (!this._closed)) {
/* 335 */         close = this._closed = 1;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 331 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 336 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 337 */     if (close) {
/* 338 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void shutdownOutput()
/*     */   {
/* 348 */     boolean close = false;
/* 349 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 351 */       this._oshut = true;
/* 352 */       if ((this._ishut) && (!this._closed)) {
/* 353 */         close = this._closed = 1;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 349 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 354 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 355 */     if (close) {
/* 356 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 366 */     boolean close = false;
/* 367 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 369 */       if (!this._closed) {
/* 370 */         close = this._closed = this._ishut = this._oshut = 1;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 367 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     }
/*     */     finally
/*     */     {
/* 371 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 372 */     if (close) {
/* 373 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasMore()
/*     */   {
/* 382 */     return getOutput().position() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int fill(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 392 */     int filled = 0;
/* 393 */     boolean close = false;
/* 394 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable4 = null;
/*     */     try
/*     */     {
/*     */       for (;;) {
/* 398 */         if (this._closed) {
/* 399 */           throw new EofException("CLOSED");
/*     */         }
/* 401 */         if (this._ishut) {
/* 402 */           return -1;
/*     */         }
/* 404 */         if (this._inQ.isEmpty()) {
/*     */           break;
/*     */         }
/* 407 */         ByteBuffer in = (ByteBuffer)this._inQ.peek();
/* 408 */         if (in == EOF)
/*     */         {
/* 410 */           this._ishut = true;
/* 411 */           if (this._oshut)
/* 412 */             close = this._closed = 1;
/* 413 */           filled = -1;
/* 414 */           break;
/*     */         }
/*     */         
/* 417 */         if (BufferUtil.hasContent(in))
/*     */         {
/* 419 */           filled = BufferUtil.append(buffer, in);
/* 420 */           if (!BufferUtil.isEmpty(in)) break;
/* 421 */           this._inQ.poll(); break;
/*     */         }
/*     */         
/* 424 */         this._inQ.poll();
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable6)
/*     */     {
/* 394 */       localThrowable4 = localThrowable6;throw localThrowable6;
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
/*     */     }
/*     */     finally
/*     */     {
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
/* 426 */       if (lock != null) if (localThrowable4 != null) try { lock.close(); } catch (Throwable localThrowable3) { localThrowable4.addSuppressed(localThrowable3); } else lock.close();
/*     */     }
/* 428 */     if (close)
/* 429 */       super.close();
/* 430 */     if (filled > 0)
/* 431 */       notIdle();
/* 432 */     return filled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean flush(ByteBuffer... buffers)
/*     */     throws IOException
/*     */   {
/* 442 */     if (this._closed)
/* 443 */       throw new IOException("CLOSED");
/* 444 */     if (this._oshut) {
/* 445 */       throw new IOException("OSHUT");
/*     */     }
/* 447 */     boolean flushed = true;
/* 448 */     boolean idle = true;
/*     */     
/* 450 */     for (ByteBuffer b : buffers)
/*     */     {
/* 452 */       if (BufferUtil.hasContent(b))
/*     */       {
/* 454 */         if ((this._growOutput) && (b.remaining() > BufferUtil.space(this._out)))
/*     */         {
/* 456 */           BufferUtil.compact(this._out);
/* 457 */           if (b.remaining() > BufferUtil.space(this._out))
/*     */           {
/* 459 */             ByteBuffer n = BufferUtil.allocate(this._out.capacity() + b.remaining() * 2);
/* 460 */             BufferUtil.append(n, this._out);
/* 461 */             this._out = n;
/*     */           }
/*     */         }
/*     */         
/* 465 */         if (BufferUtil.append(this._out, b) > 0) {
/* 466 */           idle = false;
/*     */         }
/* 468 */         if (BufferUtil.hasContent(b))
/*     */         {
/* 470 */           flushed = false;
/* 471 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 475 */     if (!idle)
/* 476 */       notIdle();
/* 477 */     return flushed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 486 */     getFillInterest().onClose();
/* 487 */     getWriteFlusher().onClose();
/* 488 */     this._ishut = false;
/* 489 */     this._oshut = false;
/* 490 */     this._closed = false;
/* 491 */     this._inQ.clear();
/* 492 */     BufferUtil.clear(this._out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getTransport()
/*     */   {
/* 502 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGrowOutput()
/*     */   {
/* 511 */     return this._growOutput;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setGrowOutput(boolean growOutput)
/*     */   {
/* 520 */     this._growOutput = growOutput;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ByteArrayEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */