/*     */ package com.facebook.presto.jdbc.internal.jetty.io.ssl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.AbstractConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.AbstractEndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.FillInterest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.WriteFlusher;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult.HandshakeStatus;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSession;
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
/*     */ public class SslConnection
/*     */   extends AbstractConnection
/*     */ {
/*  78 */   private static final Logger LOG = Log.getLogger(SslConnection.class);
/*  79 */   private static final ByteBuffer __FILL_CALLED_FLUSH = BufferUtil.allocate(0);
/*  80 */   private static final ByteBuffer __FLUSH_CALLED_FILL = BufferUtil.allocate(0);
/*     */   private final ByteBufferPool _bufferPool;
/*     */   private final SSLEngine _sslEngine;
/*     */   private final DecryptedEndPoint _decryptedEndPoint;
/*     */   private ByteBuffer _decryptedInput;
/*     */   private ByteBuffer _encryptedInput;
/*     */   private ByteBuffer _encryptedOutput;
/*  87 */   private final boolean _encryptedDirectBuffers = true;
/*  88 */   private final boolean _decryptedDirectBuffers = false;
/*     */   private boolean _renegotiationAllowed;
/*  90 */   private final Runnable _runCompletWrite = new Runnable()
/*     */   {
/*     */ 
/*     */     public void run()
/*     */     {
/*  95 */       SslConnection.this._decryptedEndPoint.getWriteFlusher().completeWrite();
/*     */     }
/*     */   };
/*  98 */   private final Runnable _runFillable = new Runnable()
/*     */   {
/*     */ 
/*     */     public void run()
/*     */     {
/* 103 */       SslConnection.this._decryptedEndPoint.getFillInterest().fillable();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   public SslConnection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, SSLEngine sslEngine)
/*     */   {
/* 111 */     super(endPoint, executor);
/* 112 */     this._bufferPool = byteBufferPool;
/* 113 */     this._sslEngine = sslEngine;
/* 114 */     this._decryptedEndPoint = newDecryptedEndPoint();
/*     */   }
/*     */   
/*     */   protected DecryptedEndPoint newDecryptedEndPoint()
/*     */   {
/* 119 */     return new DecryptedEndPoint();
/*     */   }
/*     */   
/*     */   public SSLEngine getSSLEngine()
/*     */   {
/* 124 */     return this._sslEngine;
/*     */   }
/*     */   
/*     */   public DecryptedEndPoint getDecryptedEndPoint()
/*     */   {
/* 129 */     return this._decryptedEndPoint;
/*     */   }
/*     */   
/*     */   public boolean isRenegotiationAllowed()
/*     */   {
/* 134 */     return this._renegotiationAllowed;
/*     */   }
/*     */   
/*     */   public void setRenegotiationAllowed(boolean renegotiationAllowed)
/*     */   {
/* 139 */     this._renegotiationAllowed = renegotiationAllowed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/* 145 */     super.onOpen();
/* 146 */     getDecryptedEndPoint().getConnection().onOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onClose()
/*     */   {
/* 152 */     this._decryptedEndPoint.getConnection().onClose();
/* 153 */     super.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 159 */     getDecryptedEndPoint().getConnection().close();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean onIdleExpired()
/*     */   {
/* 165 */     return getDecryptedEndPoint().getConnection().onIdleExpired();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onFillable()
/*     */   {
/* 177 */     if (LOG.isDebugEnabled()) {
/* 178 */       LOG.debug("onFillable enter {}", new Object[] { this._decryptedEndPoint });
/*     */     }
/*     */     
/* 181 */     if (this._decryptedEndPoint.isInputShutdown()) {
/* 182 */       this._decryptedEndPoint.close();
/*     */     }
/*     */     
/*     */ 
/* 186 */     this._decryptedEndPoint.getFillInterest().fillable();
/*     */     
/*     */ 
/* 189 */     synchronized (this._decryptedEndPoint)
/*     */     {
/* 191 */       if (this._decryptedEndPoint._flushRequiresFillToProgress)
/*     */       {
/* 193 */         this._decryptedEndPoint._flushRequiresFillToProgress = false;
/* 194 */         this._runCompletWrite.run();
/*     */       }
/*     */     }
/*     */     
/* 198 */     if (LOG.isDebugEnabled()) {
/* 199 */       LOG.debug("onFillable exit {}", new Object[] { this._decryptedEndPoint });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onFillInterestedFailed(Throwable cause)
/*     */   {
/* 210 */     this._decryptedEndPoint.getFillInterest().onFail(cause);
/*     */     
/* 212 */     boolean failFlusher = false;
/* 213 */     synchronized (this._decryptedEndPoint)
/*     */     {
/* 215 */       if (this._decryptedEndPoint._flushRequiresFillToProgress)
/*     */       {
/* 217 */         this._decryptedEndPoint._flushRequiresFillToProgress = false;
/* 218 */         failFlusher = true;
/*     */       }
/*     */     }
/* 221 */     if (failFlusher) {
/* 222 */       this._decryptedEndPoint.getWriteFlusher().onFail(cause);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 228 */     ByteBuffer b = this._encryptedInput;
/* 229 */     int ei = b == null ? -1 : b.remaining();
/* 230 */     b = this._encryptedOutput;
/* 231 */     int eo = b == null ? -1 : b.remaining();
/* 232 */     b = this._decryptedInput;
/* 233 */     int di = b == null ? -1 : b.remaining();
/*     */     
/* 235 */     return String.format("SslConnection@%x{%s,eio=%d/%d,di=%d} -> %s", new Object[] {
/* 236 */       Integer.valueOf(hashCode()), this._sslEngine
/* 237 */       .getHandshakeStatus(), 
/* 238 */       Integer.valueOf(ei), Integer.valueOf(eo), Integer.valueOf(di), this._decryptedEndPoint
/* 239 */       .getConnection() });
/*     */   }
/*     */   
/*     */   public class DecryptedEndPoint
/*     */     extends AbstractEndPoint
/*     */   {
/*     */     private boolean _fillRequiresFlushToProgress;
/*     */     private boolean _flushRequiresFillToProgress;
/*     */     private boolean _cannotAcceptMoreAppDataToFlush;
/*     */     private boolean _handshaken;
/*     */     private boolean _underFlown;
/* 250 */     private final Callback _writeCallback = new Callback()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void succeeded()
/*     */       {
/*     */ 
/*     */ 
/* 258 */         boolean fillable = false;
/* 259 */         synchronized (DecryptedEndPoint.this)
/*     */         {
/* 261 */           if (SslConnection.LOG.isDebugEnabled()) {
/* 262 */             SslConnection.LOG.debug("write.complete {}", new Object[] { SslConnection.this.getEndPoint() });
/*     */           }
/* 264 */           DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
/*     */           
/* 266 */           DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
/*     */           
/* 268 */           if (DecryptedEndPoint.this._fillRequiresFlushToProgress)
/*     */           {
/* 270 */             DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
/* 271 */             fillable = true;
/*     */           }
/*     */         }
/* 274 */         if (fillable)
/* 275 */           DecryptedEndPoint.this.getFillInterest().fillable();
/* 276 */         SslConnection.this._runCompletWrite.run();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void failed(Throwable x)
/*     */       {
/* 285 */         boolean fail_filler = false;
/* 286 */         synchronized (DecryptedEndPoint.this)
/*     */         {
/* 288 */           if (SslConnection.LOG.isDebugEnabled())
/* 289 */             SslConnection.LOG.debug("{} write.failed", new Object[] { SslConnection.this, x });
/* 290 */           BufferUtil.clear(SslConnection.this._encryptedOutput);
/* 291 */           DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
/*     */           
/* 293 */           DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
/*     */           
/* 295 */           if (DecryptedEndPoint.this._fillRequiresFlushToProgress)
/*     */           {
/* 297 */             DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
/* 298 */             fail_filler = true;
/*     */           }
/*     */         }
/*     */         
/* 302 */         final boolean filler_failed = fail_filler;
/*     */         
/* 304 */         SslConnection.this.failedCallback(new Callback()
/*     */         {
/*     */ 
/*     */           public void failed(Throwable x)
/*     */           {
/* 309 */             if (filler_failed)
/* 310 */               DecryptedEndPoint.this.getFillInterest().onFail(x);
/* 311 */             DecryptedEndPoint.this.getWriteFlusher().onFail(x); } }, x);
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public DecryptedEndPoint()
/*     */     {
/* 320 */       super(SslConnection.this.getEndPoint().getLocalAddress(), SslConnection.this.getEndPoint().getRemoteAddress());
/* 321 */       super.setIdleTimeout(-1L);
/*     */     }
/*     */     
/*     */ 
/*     */     public long getIdleTimeout()
/*     */     {
/* 327 */       return SslConnection.this.getEndPoint().getIdleTimeout();
/*     */     }
/*     */     
/*     */ 
/*     */     public void setIdleTimeout(long idleTimeout)
/*     */     {
/* 333 */       SslConnection.this.getEndPoint().setIdleTimeout(idleTimeout);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isOpen()
/*     */     {
/* 339 */       return SslConnection.this.getEndPoint().isOpen();
/*     */     }
/*     */     
/*     */ 
/*     */     protected WriteFlusher getWriteFlusher()
/*     */     {
/* 345 */       return super.getWriteFlusher();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void onIncompleteFlush()
/*     */     {
/* 355 */       boolean try_again = false;
/* 356 */       synchronized (this)
/*     */       {
/* 358 */         if (SslConnection.LOG.isDebugEnabled()) {
/* 359 */           SslConnection.LOG.debug("onIncompleteFlush {}", new Object[] { SslConnection.this });
/*     */         }
/* 361 */         if (BufferUtil.hasContent(SslConnection.this._encryptedOutput))
/*     */         {
/*     */ 
/* 364 */           this._cannotAcceptMoreAppDataToFlush = true;
/* 365 */           SslConnection.this.getEndPoint().write(this._writeCallback, new ByteBuffer[] { SslConnection.this._encryptedOutput });
/*     */ 
/*     */         }
/* 368 */         else if (SslConnection.this._sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)
/*     */         {
/*     */ 
/* 371 */           this._flushRequiresFillToProgress = true;
/*     */           
/* 373 */           ensureFillInterested();
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/*     */ 
/* 382 */           try_again = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 387 */       if (try_again)
/*     */       {
/*     */ 
/* 390 */         if (isOutputShutdown())
/*     */         {
/*     */ 
/* 393 */           getWriteFlusher().onClose();
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 400 */           SslConnection.this.getExecutor().execute(SslConnection.this._runCompletWrite);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void needsFillInterest()
/*     */       throws IOException
/*     */     {
/* 413 */       synchronized (this)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 418 */         boolean fillable = (BufferUtil.hasContent(SslConnection.this._decryptedInput)) || ((BufferUtil.hasContent(SslConnection.this._encryptedInput)) && (!this._underFlown));
/*     */         
/*     */ 
/* 421 */         if (!fillable)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 426 */           if (this._fillRequiresFlushToProgress)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 431 */             if (BufferUtil.hasContent(SslConnection.this._encryptedOutput))
/*     */             {
/*     */ 
/* 434 */               this._cannotAcceptMoreAppDataToFlush = true;
/* 435 */               SslConnection.this.getEndPoint().write(this._writeCallback, new ByteBuffer[] { SslConnection.this._encryptedOutput });
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 441 */               this._fillRequiresFlushToProgress = false;
/* 442 */               fillable = true;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 447 */         if (fillable) {
/* 448 */           SslConnection.this.getExecutor().execute(SslConnection.this._runFillable);
/*     */         } else {
/* 450 */           ensureFillInterested();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void setConnection(Connection connection)
/*     */     {
/* 457 */       if ((connection instanceof AbstractConnection))
/*     */       {
/* 459 */         AbstractConnection a = (AbstractConnection)connection;
/* 460 */         if (a.getInputBufferSize() < SslConnection.this._sslEngine.getSession().getApplicationBufferSize())
/* 461 */           a.setInputBufferSize(SslConnection.this._sslEngine.getSession().getApplicationBufferSize());
/*     */       }
/* 463 */       super.setConnection(connection);
/*     */     }
/*     */     
/*     */     public SslConnection getSslConnection()
/*     */     {
/* 468 */       return SslConnection.this;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public synchronized int fill(ByteBuffer buffer)
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   4: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   7: invokestatic 107	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:hasContent	(Ljava/nio/ByteBuffer;)Z
/*     */       //   10: ifeq +150 -> 160
/*     */       //   13: aload_1
/*     */       //   14: aload_0
/*     */       //   15: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   18: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   21: invokestatic 219	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:append	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
/*     */       //   24: istore_2
/*     */       //   25: aload_0
/*     */       //   26: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   29: ifeq +27 -> 56
/*     */       //   32: aload_0
/*     */       //   33: iconst_0
/*     */       //   34: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   37: aload_0
/*     */       //   38: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   41: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   44: aload_0
/*     */       //   45: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   48: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   51: invokeinterface 159 2 0
/*     */       //   56: aload_0
/*     */       //   57: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   60: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   63: ifnull +44 -> 107
/*     */       //   66: aload_0
/*     */       //   67: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   70: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   73: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   76: ifne +31 -> 107
/*     */       //   79: aload_0
/*     */       //   80: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   83: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   86: aload_0
/*     */       //   87: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   90: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   93: invokeinterface 235 2 0
/*     */       //   98: aload_0
/*     */       //   99: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   102: aconst_null
/*     */       //   103: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   106: pop
/*     */       //   107: aload_0
/*     */       //   108: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   111: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   114: ifnull +44 -> 158
/*     */       //   117: aload_0
/*     */       //   118: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   121: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   124: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   127: ifne +31 -> 158
/*     */       //   130: aload_0
/*     */       //   131: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   134: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   137: aload_0
/*     */       //   138: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   141: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   144: invokeinterface 235 2 0
/*     */       //   149: aload_0
/*     */       //   150: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   153: aconst_null
/*     */       //   154: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   157: pop
/*     */       //   158: iload_2
/*     */       //   159: ireturn
/*     */       //   160: aload_0
/*     */       //   161: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   164: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   167: ifnonnull +42 -> 209
/*     */       //   170: aload_0
/*     */       //   171: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   174: aload_0
/*     */       //   175: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   178: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   181: aload_0
/*     */       //   182: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   185: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   188: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   191: invokeinterface 245 1 0
/*     */       //   196: iconst_1
/*     */       //   197: invokeinterface 249 3 0
/*     */       //   202: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   205: pop
/*     */       //   206: goto +14 -> 220
/*     */       //   209: aload_0
/*     */       //   210: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   213: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   216: invokestatic 252	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:compact	(Ljava/nio/ByteBuffer;)Z
/*     */       //   219: pop
/*     */       //   220: aload_1
/*     */       //   221: invokestatic 255	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:space	(Ljava/nio/ByteBuffer;)I
/*     */       //   224: aload_0
/*     */       //   225: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   228: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   231: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   234: invokeinterface 197 1 0
/*     */       //   239: if_icmple +8 -> 247
/*     */       //   242: aload_1
/*     */       //   243: astore_2
/*     */       //   244: goto +60 -> 304
/*     */       //   247: aload_0
/*     */       //   248: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   251: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   254: ifnonnull +42 -> 296
/*     */       //   257: aload_0
/*     */       //   258: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   261: aload_0
/*     */       //   262: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   265: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   268: aload_0
/*     */       //   269: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   272: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   275: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   278: invokeinterface 197 1 0
/*     */       //   283: iconst_0
/*     */       //   284: invokeinterface 249 3 0
/*     */       //   289: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   292: astore_2
/*     */       //   293: goto +11 -> 304
/*     */       //   296: aload_0
/*     */       //   297: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   300: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   303: astore_2
/*     */       //   304: aload_0
/*     */       //   305: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   308: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   311: aload_0
/*     */       //   312: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   315: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   318: invokeinterface 257 2 0
/*     */       //   323: istore_3
/*     */       //   324: aload_2
/*     */       //   325: invokestatic 260	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFill	(Ljava/nio/ByteBuffer;)I
/*     */       //   328: istore 4
/*     */       //   330: aload_0
/*     */       //   331: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   334: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   337: aload_0
/*     */       //   338: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   341: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   344: aload_2
/*     */       //   345: invokevirtual 264	javax/net/ssl/SSLEngine:unwrap	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)Ljavax/net/ssl/SSLEngineResult;
/*     */       //   348: astore 5
/*     */       //   350: aload_2
/*     */       //   351: iload 4
/*     */       //   353: invokestatic 268	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*     */       //   356: goto +14 -> 370
/*     */       //   359: astore 6
/*     */       //   361: aload_2
/*     */       //   362: iload 4
/*     */       //   364: invokestatic 268	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*     */       //   367: aload 6
/*     */       //   369: athrow
/*     */       //   370: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   373: invokeinterface 89 1 0
/*     */       //   378: ifeq +76 -> 454
/*     */       //   381: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   384: ldc_w 270
/*     */       //   387: iconst_3
/*     */       //   388: anewarray 93	java/lang/Object
/*     */       //   391: dup
/*     */       //   392: iconst_0
/*     */       //   393: aload_0
/*     */       //   394: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   397: aastore
/*     */       //   398: dup
/*     */       //   399: iconst_1
/*     */       //   400: iload_3
/*     */       //   401: invokestatic 276	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*     */       //   404: aastore
/*     */       //   405: dup
/*     */       //   406: iconst_2
/*     */       //   407: aload 5
/*     */       //   409: invokevirtual 280	javax/net/ssl/SSLEngineResult:toString	()Ljava/lang/String;
/*     */       //   412: bipush 10
/*     */       //   414: bipush 32
/*     */       //   416: invokevirtual 286	java/lang/String:replace	(CC)Ljava/lang/String;
/*     */       //   419: aastore
/*     */       //   420: invokeinterface 97 3 0
/*     */       //   425: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   428: ldc_w 288
/*     */       //   431: iconst_2
/*     */       //   432: anewarray 93	java/lang/Object
/*     */       //   435: dup
/*     */       //   436: iconst_0
/*     */       //   437: aload_0
/*     */       //   438: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   441: aastore
/*     */       //   442: dup
/*     */       //   443: iconst_1
/*     */       //   444: aload_1
/*     */       //   445: invokestatic 292	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:toHexSummary	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*     */       //   448: aastore
/*     */       //   449: invokeinterface 97 3 0
/*     */       //   454: aload_0
/*     */       //   455: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   458: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   461: invokevirtual 125	javax/net/ssl/SSLEngine:getHandshakeStatus	()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   464: astore 6
/*     */       //   466: aload 5
/*     */       //   468: invokevirtual 293	javax/net/ssl/SSLEngineResult:getHandshakeStatus	()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   471: astore 7
/*     */       //   473: aload 5
/*     */       //   475: invokevirtual 297	javax/net/ssl/SSLEngineResult:getStatus	()Ljavax/net/ssl/SSLEngineResult$Status;
/*     */       //   478: astore 8
/*     */       //   480: aload_0
/*     */       //   481: aload 8
/*     */       //   483: getstatic 301	javax/net/ssl/SSLEngineResult$Status:BUFFER_UNDERFLOW	Ljavax/net/ssl/SSLEngineResult$Status;
/*     */       //   486: if_acmpeq +27 -> 513
/*     */       //   489: aload 8
/*     */       //   491: getstatic 304	javax/net/ssl/SSLEngineResult$Status:OK	Ljavax/net/ssl/SSLEngineResult$Status;
/*     */       //   494: if_acmpne +23 -> 517
/*     */       //   497: aload 5
/*     */       //   499: invokevirtual 307	javax/net/ssl/SSLEngineResult:bytesConsumed	()I
/*     */       //   502: ifne +15 -> 517
/*     */       //   505: aload 5
/*     */       //   507: invokevirtual 310	javax/net/ssl/SSLEngineResult:bytesProduced	()I
/*     */       //   510: ifne +7 -> 517
/*     */       //   513: iconst_1
/*     */       //   514: goto +4 -> 518
/*     */       //   517: iconst_0
/*     */       //   518: putfield 171	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_underFlown	Z
/*     */       //   521: aload_0
/*     */       //   522: getfield 171	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_underFlown	Z
/*     */       //   525: ifeq +154 -> 679
/*     */       //   528: iload_3
/*     */       //   529: ifge +7 -> 536
/*     */       //   532: aload_0
/*     */       //   533: invokespecial 313	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:closeInbound	()V
/*     */       //   536: iload_3
/*     */       //   537: ifgt +142 -> 679
/*     */       //   540: iload_3
/*     */       //   541: istore 9
/*     */       //   543: aload_0
/*     */       //   544: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   547: ifeq +27 -> 574
/*     */       //   550: aload_0
/*     */       //   551: iconst_0
/*     */       //   552: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   555: aload_0
/*     */       //   556: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   559: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   562: aload_0
/*     */       //   563: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   566: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   569: invokeinterface 159 2 0
/*     */       //   574: aload_0
/*     */       //   575: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   578: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   581: ifnull +44 -> 625
/*     */       //   584: aload_0
/*     */       //   585: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   588: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   591: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   594: ifne +31 -> 625
/*     */       //   597: aload_0
/*     */       //   598: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   601: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   604: aload_0
/*     */       //   605: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   608: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   611: invokeinterface 235 2 0
/*     */       //   616: aload_0
/*     */       //   617: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   620: aconst_null
/*     */       //   621: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   624: pop
/*     */       //   625: aload_0
/*     */       //   626: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   629: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   632: ifnull +44 -> 676
/*     */       //   635: aload_0
/*     */       //   636: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   639: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   642: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   645: ifne +31 -> 676
/*     */       //   648: aload_0
/*     */       //   649: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   652: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   655: aload_0
/*     */       //   656: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   659: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   662: invokeinterface 235 2 0
/*     */       //   667: aload_0
/*     */       //   668: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   671: aconst_null
/*     */       //   672: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   675: pop
/*     */       //   676: iload 9
/*     */       //   678: ireturn
/*     */       //   679: getstatic 317	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$3:$SwitchMap$javax$net$ssl$SSLEngineResult$Status	[I
/*     */       //   682: aload 8
/*     */       //   684: invokevirtual 320	javax/net/ssl/SSLEngineResult$Status:ordinal	()I
/*     */       //   687: iaload
/*     */       //   688: tableswitch	default:+1544->2232, 1:+28->716, 2:+511->1199, 3:+511->1199
/*     */       //   716: getstatic 323	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$3:$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus	[I
/*     */       //   719: aload 6
/*     */       //   721: invokevirtual 324	javax/net/ssl/SSLEngineResult$HandshakeStatus:ordinal	()I
/*     */       //   724: iaload
/*     */       //   725: tableswitch	default:+466->1191, 1:+31->756, 2:+170->895, 3:+188->913, 4:+327->1052
/*     */       //   756: iconst_m1
/*     */       //   757: istore 9
/*     */       //   759: aload_0
/*     */       //   760: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   763: ifeq +27 -> 790
/*     */       //   766: aload_0
/*     */       //   767: iconst_0
/*     */       //   768: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   771: aload_0
/*     */       //   772: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   775: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   778: aload_0
/*     */       //   779: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   782: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   785: invokeinterface 159 2 0
/*     */       //   790: aload_0
/*     */       //   791: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   794: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   797: ifnull +44 -> 841
/*     */       //   800: aload_0
/*     */       //   801: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   804: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   807: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   810: ifne +31 -> 841
/*     */       //   813: aload_0
/*     */       //   814: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   817: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   820: aload_0
/*     */       //   821: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   824: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   827: invokeinterface 235 2 0
/*     */       //   832: aload_0
/*     */       //   833: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   836: aconst_null
/*     */       //   837: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   840: pop
/*     */       //   841: aload_0
/*     */       //   842: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   845: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   848: ifnull +44 -> 892
/*     */       //   851: aload_0
/*     */       //   852: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   855: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   858: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   861: ifne +31 -> 892
/*     */       //   864: aload_0
/*     */       //   865: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   868: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   871: aload_0
/*     */       //   872: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   875: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   878: invokeinterface 235 2 0
/*     */       //   883: aload_0
/*     */       //   884: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   887: aconst_null
/*     */       //   888: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   891: pop
/*     */       //   892: iload 9
/*     */       //   894: ireturn
/*     */       //   895: aload_0
/*     */       //   896: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   899: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   902: invokevirtual 328	javax/net/ssl/SSLEngine:getDelegatedTask	()Ljava/lang/Runnable;
/*     */       //   905: invokeinterface 333 1 0
/*     */       //   910: goto -586 -> 324
/*     */       //   913: iconst_m1
/*     */       //   914: istore 9
/*     */       //   916: aload_0
/*     */       //   917: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   920: ifeq +27 -> 947
/*     */       //   923: aload_0
/*     */       //   924: iconst_0
/*     */       //   925: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   928: aload_0
/*     */       //   929: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   932: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   935: aload_0
/*     */       //   936: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   939: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   942: invokeinterface 159 2 0
/*     */       //   947: aload_0
/*     */       //   948: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   951: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   954: ifnull +44 -> 998
/*     */       //   957: aload_0
/*     */       //   958: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   961: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   964: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   967: ifne +31 -> 998
/*     */       //   970: aload_0
/*     */       //   971: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   974: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   977: aload_0
/*     */       //   978: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   981: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   984: invokeinterface 235 2 0
/*     */       //   989: aload_0
/*     */       //   990: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   993: aconst_null
/*     */       //   994: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   997: pop
/*     */       //   998: aload_0
/*     */       //   999: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1002: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1005: ifnull +44 -> 1049
/*     */       //   1008: aload_0
/*     */       //   1009: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1012: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1015: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1018: ifne +31 -> 1049
/*     */       //   1021: aload_0
/*     */       //   1022: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1025: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1028: aload_0
/*     */       //   1029: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1032: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1035: invokeinterface 235 2 0
/*     */       //   1040: aload_0
/*     */       //   1041: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1044: aconst_null
/*     */       //   1045: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1048: pop
/*     */       //   1049: iload 9
/*     */       //   1051: ireturn
/*     */       //   1052: iconst_m1
/*     */       //   1053: istore 9
/*     */       //   1055: aload_0
/*     */       //   1056: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1059: ifeq +27 -> 1086
/*     */       //   1062: aload_0
/*     */       //   1063: iconst_0
/*     */       //   1064: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1067: aload_0
/*     */       //   1068: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1071: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   1074: aload_0
/*     */       //   1075: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1078: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   1081: invokeinterface 159 2 0
/*     */       //   1086: aload_0
/*     */       //   1087: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1090: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1093: ifnull +44 -> 1137
/*     */       //   1096: aload_0
/*     */       //   1097: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1100: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1103: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1106: ifne +31 -> 1137
/*     */       //   1109: aload_0
/*     */       //   1110: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1113: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1116: aload_0
/*     */       //   1117: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1120: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1123: invokeinterface 235 2 0
/*     */       //   1128: aload_0
/*     */       //   1129: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1132: aconst_null
/*     */       //   1133: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1136: pop
/*     */       //   1137: aload_0
/*     */       //   1138: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1141: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1144: ifnull +44 -> 1188
/*     */       //   1147: aload_0
/*     */       //   1148: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1151: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1154: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1157: ifne +31 -> 1188
/*     */       //   1160: aload_0
/*     */       //   1161: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1164: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1167: aload_0
/*     */       //   1168: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1171: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1174: invokeinterface 235 2 0
/*     */       //   1179: aload_0
/*     */       //   1180: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1183: aconst_null
/*     */       //   1184: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1187: pop
/*     */       //   1188: iload 9
/*     */       //   1190: ireturn
/*     */       //   1191: new 213	java/lang/IllegalStateException
/*     */       //   1194: dup
/*     */       //   1195: invokespecial 335	java/lang/IllegalStateException:<init>	()V
/*     */       //   1198: athrow
/*     */       //   1199: aload 7
/*     */       //   1201: getstatic 338	javax/net/ssl/SSLEngineResult$HandshakeStatus:FINISHED	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   1204: if_acmpne +109 -> 1313
/*     */       //   1207: aload_0
/*     */       //   1208: getfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   1211: ifne +102 -> 1313
/*     */       //   1214: aload_0
/*     */       //   1215: iconst_1
/*     */       //   1216: putfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   1219: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   1222: invokeinterface 89 1 0
/*     */       //   1227: ifeq +86 -> 1313
/*     */       //   1230: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   1233: ldc_w 342
/*     */       //   1236: iconst_4
/*     */       //   1237: anewarray 93	java/lang/Object
/*     */       //   1240: dup
/*     */       //   1241: iconst_0
/*     */       //   1242: aload_0
/*     */       //   1243: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1246: aastore
/*     */       //   1247: dup
/*     */       //   1248: iconst_1
/*     */       //   1249: aload_0
/*     */       //   1250: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1253: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   1256: invokevirtual 345	javax/net/ssl/SSLEngine:getUseClientMode	()Z
/*     */       //   1259: ifeq +9 -> 1268
/*     */       //   1262: ldc_w 347
/*     */       //   1265: goto +6 -> 1271
/*     */       //   1268: ldc_w 351
/*     */       //   1271: aastore
/*     */       //   1272: dup
/*     */       //   1273: iconst_2
/*     */       //   1274: aload_0
/*     */       //   1275: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1278: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   1281: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   1284: invokeinterface 354 1 0
/*     */       //   1289: aastore
/*     */       //   1290: dup
/*     */       //   1291: iconst_3
/*     */       //   1292: aload_0
/*     */       //   1293: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1296: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   1299: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   1302: invokeinterface 357 1 0
/*     */       //   1307: aastore
/*     */       //   1308: invokeinterface 97 3 0
/*     */       //   1313: aload_0
/*     */       //   1314: getfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   1317: ifeq +197 -> 1514
/*     */       //   1320: aload 6
/*     */       //   1322: getstatic 360	javax/net/ssl/SSLEngineResult$HandshakeStatus:NOT_HANDSHAKING	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   1325: if_acmpeq +189 -> 1514
/*     */       //   1328: aload_0
/*     */       //   1329: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1332: invokevirtual 363	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:isRenegotiationAllowed	()Z
/*     */       //   1335: ifne +179 -> 1514
/*     */       //   1338: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   1341: invokeinterface 89 1 0
/*     */       //   1346: ifeq +25 -> 1371
/*     */       //   1349: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   1352: ldc_w 365
/*     */       //   1355: iconst_1
/*     */       //   1356: anewarray 93	java/lang/Object
/*     */       //   1359: dup
/*     */       //   1360: iconst_0
/*     */       //   1361: aload_0
/*     */       //   1362: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1365: aastore
/*     */       //   1366: invokeinterface 97 3 0
/*     */       //   1371: aload_0
/*     */       //   1372: invokespecial 313	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:closeInbound	()V
/*     */       //   1375: iconst_m1
/*     */       //   1376: istore 9
/*     */       //   1378: aload_0
/*     */       //   1379: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1382: ifeq +27 -> 1409
/*     */       //   1385: aload_0
/*     */       //   1386: iconst_0
/*     */       //   1387: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1390: aload_0
/*     */       //   1391: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1394: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   1397: aload_0
/*     */       //   1398: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1401: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   1404: invokeinterface 159 2 0
/*     */       //   1409: aload_0
/*     */       //   1410: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1413: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1416: ifnull +44 -> 1460
/*     */       //   1419: aload_0
/*     */       //   1420: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1423: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1426: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1429: ifne +31 -> 1460
/*     */       //   1432: aload_0
/*     */       //   1433: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1436: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1439: aload_0
/*     */       //   1440: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1443: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1446: invokeinterface 235 2 0
/*     */       //   1451: aload_0
/*     */       //   1452: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1455: aconst_null
/*     */       //   1456: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1459: pop
/*     */       //   1460: aload_0
/*     */       //   1461: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1464: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1467: ifnull +44 -> 1511
/*     */       //   1470: aload_0
/*     */       //   1471: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1474: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1477: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1480: ifne +31 -> 1511
/*     */       //   1483: aload_0
/*     */       //   1484: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1487: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1490: aload_0
/*     */       //   1491: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1494: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1497: invokeinterface 235 2 0
/*     */       //   1502: aload_0
/*     */       //   1503: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1506: aconst_null
/*     */       //   1507: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1510: pop
/*     */       //   1511: iload 9
/*     */       //   1513: ireturn
/*     */       //   1514: aload 5
/*     */       //   1516: invokevirtual 310	javax/net/ssl/SSLEngineResult:bytesProduced	()I
/*     */       //   1519: ifle +300 -> 1819
/*     */       //   1522: aload_2
/*     */       //   1523: aload_1
/*     */       //   1524: if_acmpne +146 -> 1670
/*     */       //   1527: aload 5
/*     */       //   1529: invokevirtual 310	javax/net/ssl/SSLEngineResult:bytesProduced	()I
/*     */       //   1532: istore 9
/*     */       //   1534: aload_0
/*     */       //   1535: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1538: ifeq +27 -> 1565
/*     */       //   1541: aload_0
/*     */       //   1542: iconst_0
/*     */       //   1543: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1546: aload_0
/*     */       //   1547: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1550: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   1553: aload_0
/*     */       //   1554: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1557: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   1560: invokeinterface 159 2 0
/*     */       //   1565: aload_0
/*     */       //   1566: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1569: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1572: ifnull +44 -> 1616
/*     */       //   1575: aload_0
/*     */       //   1576: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1579: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1582: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1585: ifne +31 -> 1616
/*     */       //   1588: aload_0
/*     */       //   1589: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1592: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1595: aload_0
/*     */       //   1596: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1599: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1602: invokeinterface 235 2 0
/*     */       //   1607: aload_0
/*     */       //   1608: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1611: aconst_null
/*     */       //   1612: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1615: pop
/*     */       //   1616: aload_0
/*     */       //   1617: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1620: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1623: ifnull +44 -> 1667
/*     */       //   1626: aload_0
/*     */       //   1627: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1630: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1633: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1636: ifne +31 -> 1667
/*     */       //   1639: aload_0
/*     */       //   1640: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1643: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1646: aload_0
/*     */       //   1647: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1650: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1653: invokeinterface 235 2 0
/*     */       //   1658: aload_0
/*     */       //   1659: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1662: aconst_null
/*     */       //   1663: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1666: pop
/*     */       //   1667: iload 9
/*     */       //   1669: ireturn
/*     */       //   1670: aload_1
/*     */       //   1671: aload_0
/*     */       //   1672: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1675: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1678: invokestatic 219	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:append	(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I
/*     */       //   1681: istore 9
/*     */       //   1683: aload_0
/*     */       //   1684: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1687: ifeq +27 -> 1714
/*     */       //   1690: aload_0
/*     */       //   1691: iconst_0
/*     */       //   1692: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1695: aload_0
/*     */       //   1696: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1699: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   1702: aload_0
/*     */       //   1703: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1706: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   1709: invokeinterface 159 2 0
/*     */       //   1714: aload_0
/*     */       //   1715: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1718: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1721: ifnull +44 -> 1765
/*     */       //   1724: aload_0
/*     */       //   1725: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1728: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1731: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1734: ifne +31 -> 1765
/*     */       //   1737: aload_0
/*     */       //   1738: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1741: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1744: aload_0
/*     */       //   1745: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1748: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1751: invokeinterface 235 2 0
/*     */       //   1756: aload_0
/*     */       //   1757: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1760: aconst_null
/*     */       //   1761: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1764: pop
/*     */       //   1765: aload_0
/*     */       //   1766: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1769: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1772: ifnull +44 -> 1816
/*     */       //   1775: aload_0
/*     */       //   1776: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1779: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1782: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1785: ifne +31 -> 1816
/*     */       //   1788: aload_0
/*     */       //   1789: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1792: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1795: aload_0
/*     */       //   1796: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1799: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1802: invokeinterface 235 2 0
/*     */       //   1807: aload_0
/*     */       //   1808: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1811: aconst_null
/*     */       //   1812: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1815: pop
/*     */       //   1816: iload 9
/*     */       //   1818: ireturn
/*     */       //   1819: getstatic 323	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$3:$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus	[I
/*     */       //   1822: aload 6
/*     */       //   1824: invokevirtual 324	javax/net/ssl/SSLEngineResult$HandshakeStatus:ordinal	()I
/*     */       //   1827: iaload
/*     */       //   1828: tableswitch	default:+396->2224, 1:+32->1860, 2:+42->1870, 3:+60->1888, 4:+386->2214
/*     */       //   1860: aload_0
/*     */       //   1861: getfield 171	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_underFlown	Z
/*     */       //   1864: ifeq -1540 -> 324
/*     */       //   1867: goto +373 -> 2240
/*     */       //   1870: aload_0
/*     */       //   1871: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1874: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   1877: invokevirtual 328	javax/net/ssl/SSLEngine:getDelegatedTask	()Ljava/lang/Runnable;
/*     */       //   1880: invokeinterface 333 1 0
/*     */       //   1885: goto -1561 -> 324
/*     */       //   1888: aload_1
/*     */       //   1889: invokestatic 369	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1600	()Ljava/nio/ByteBuffer;
/*     */       //   1892: if_acmpne +142 -> 2034
/*     */       //   1895: iconst_0
/*     */       //   1896: istore 9
/*     */       //   1898: aload_0
/*     */       //   1899: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1902: ifeq +27 -> 1929
/*     */       //   1905: aload_0
/*     */       //   1906: iconst_0
/*     */       //   1907: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   1910: aload_0
/*     */       //   1911: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1914: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   1917: aload_0
/*     */       //   1918: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1921: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   1924: invokeinterface 159 2 0
/*     */       //   1929: aload_0
/*     */       //   1930: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1933: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1936: ifnull +44 -> 1980
/*     */       //   1939: aload_0
/*     */       //   1940: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1943: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1946: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   1949: ifne +31 -> 1980
/*     */       //   1952: aload_0
/*     */       //   1953: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1956: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   1959: aload_0
/*     */       //   1960: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1963: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1966: invokeinterface 235 2 0
/*     */       //   1971: aload_0
/*     */       //   1972: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1975: aconst_null
/*     */       //   1976: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   1979: pop
/*     */       //   1980: aload_0
/*     */       //   1981: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1984: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1987: ifnull +44 -> 2031
/*     */       //   1990: aload_0
/*     */       //   1991: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   1994: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   1997: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   2000: ifne +31 -> 2031
/*     */       //   2003: aload_0
/*     */       //   2004: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2007: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   2010: aload_0
/*     */       //   2011: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2014: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2017: invokeinterface 235 2 0
/*     */       //   2022: aload_0
/*     */       //   2023: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2026: aconst_null
/*     */       //   2027: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   2030: pop
/*     */       //   2031: iload 9
/*     */       //   2033: ireturn
/*     */       //   2034: aload_0
/*     */       //   2035: iconst_1
/*     */       //   2036: putfield 173	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_fillRequiresFlushToProgress	Z
/*     */       //   2039: aload_0
/*     */       //   2040: iconst_1
/*     */       //   2041: anewarray 111	java/nio/ByteBuffer
/*     */       //   2044: dup
/*     */       //   2045: iconst_0
/*     */       //   2046: invokestatic 372	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1700	()Ljava/nio/ByteBuffer;
/*     */       //   2049: aastore
/*     */       //   2050: invokevirtual 376	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:flush	([Ljava/nio/ByteBuffer;)Z
/*     */       //   2053: pop
/*     */       //   2054: aload_0
/*     */       //   2055: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2058: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2061: invokestatic 379	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:isEmpty	(Ljava/nio/ByteBuffer;)Z
/*     */       //   2064: ifeq +11 -> 2075
/*     */       //   2067: aload_0
/*     */       //   2068: iconst_0
/*     */       //   2069: putfield 173	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_fillRequiresFlushToProgress	Z
/*     */       //   2072: goto -1748 -> 324
/*     */       //   2075: iconst_0
/*     */       //   2076: istore 9
/*     */       //   2078: aload_0
/*     */       //   2079: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   2082: ifeq +27 -> 2109
/*     */       //   2085: aload_0
/*     */       //   2086: iconst_0
/*     */       //   2087: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   2090: aload_0
/*     */       //   2091: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2094: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   2097: aload_0
/*     */       //   2098: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2101: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   2104: invokeinterface 159 2 0
/*     */       //   2109: aload_0
/*     */       //   2110: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2113: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2116: ifnull +44 -> 2160
/*     */       //   2119: aload_0
/*     */       //   2120: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2123: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2126: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   2129: ifne +31 -> 2160
/*     */       //   2132: aload_0
/*     */       //   2133: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2136: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   2139: aload_0
/*     */       //   2140: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2143: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2146: invokeinterface 235 2 0
/*     */       //   2151: aload_0
/*     */       //   2152: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2155: aconst_null
/*     */       //   2156: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   2159: pop
/*     */       //   2160: aload_0
/*     */       //   2161: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2164: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2167: ifnull +44 -> 2211
/*     */       //   2170: aload_0
/*     */       //   2171: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2174: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2177: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   2180: ifne +31 -> 2211
/*     */       //   2183: aload_0
/*     */       //   2184: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2187: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   2190: aload_0
/*     */       //   2191: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2194: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2197: invokeinterface 235 2 0
/*     */       //   2202: aload_0
/*     */       //   2203: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2206: aconst_null
/*     */       //   2207: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   2210: pop
/*     */       //   2211: iload 9
/*     */       //   2213: ireturn
/*     */       //   2214: aload_0
/*     */       //   2215: getfield 171	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_underFlown	Z
/*     */       //   2218: ifeq -1894 -> 324
/*     */       //   2221: goto +19 -> 2240
/*     */       //   2224: new 213	java/lang/IllegalStateException
/*     */       //   2227: dup
/*     */       //   2228: invokespecial 335	java/lang/IllegalStateException:<init>	()V
/*     */       //   2231: athrow
/*     */       //   2232: new 213	java/lang/IllegalStateException
/*     */       //   2235: dup
/*     */       //   2236: invokespecial 335	java/lang/IllegalStateException:<init>	()V
/*     */       //   2239: athrow
/*     */       //   2240: goto -1936 -> 304
/*     */       //   2243: astore_2
/*     */       //   2244: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   2247: aload_2
/*     */       //   2248: invokeinterface 382 2 0
/*     */       //   2253: aload_0
/*     */       //   2254: invokevirtual 385	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:close	()V
/*     */       //   2257: new 387	com/facebook/presto/jdbc/internal/jetty/io/EofException
/*     */       //   2260: dup
/*     */       //   2261: aload_2
/*     */       //   2262: invokespecial 389	com/facebook/presto/jdbc/internal/jetty/io/EofException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   2265: athrow
/*     */       //   2266: astore_2
/*     */       //   2267: aload_0
/*     */       //   2268: invokevirtual 385	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:close	()V
/*     */       //   2271: aload_2
/*     */       //   2272: athrow
/*     */       //   2273: astore 10
/*     */       //   2275: aload_0
/*     */       //   2276: getfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   2279: ifeq +27 -> 2306
/*     */       //   2282: aload_0
/*     */       //   2283: iconst_0
/*     */       //   2284: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   2287: aload_0
/*     */       //   2288: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2291: invokestatic 222	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1800	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/util/concurrent/Executor;
/*     */       //   2294: aload_0
/*     */       //   2295: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2298: invokestatic 153	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/lang/Runnable;
/*     */       //   2301: invokeinterface 159 2 0
/*     */       //   2306: aload_0
/*     */       //   2307: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2310: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2313: ifnull +44 -> 2357
/*     */       //   2316: aload_0
/*     */       //   2317: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2320: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2323: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   2326: ifne +31 -> 2357
/*     */       //   2329: aload_0
/*     */       //   2330: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2333: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   2336: aload_0
/*     */       //   2337: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2340: invokestatic 169	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1200	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2343: invokeinterface 235 2 0
/*     */       //   2348: aload_0
/*     */       //   2349: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2352: aconst_null
/*     */       //   2353: invokestatic 239	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1202	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   2356: pop
/*     */       //   2357: aload_0
/*     */       //   2358: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2361: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2364: ifnull +44 -> 2408
/*     */       //   2367: aload_0
/*     */       //   2368: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2371: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2374: invokevirtual 225	java/nio/ByteBuffer:hasRemaining	()Z
/*     */       //   2377: ifne +31 -> 2408
/*     */       //   2380: aload_0
/*     */       //   2381: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2384: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   2387: aload_0
/*     */       //   2388: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2391: invokestatic 166	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1100	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   2394: invokeinterface 235 2 0
/*     */       //   2399: aload_0
/*     */       //   2400: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   2403: aconst_null
/*     */       //   2404: invokestatic 242	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1102	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   2407: pop
/*     */       //   2408: aload 10
/*     */       //   2410: athrow
/*     */       // Line number table:
/*     */       //   Java source line #477	-> byte code offset #0
/*     */       //   Java source line #478	-> byte code offset #13
/*     */       //   Java source line #673	-> byte code offset #25
/*     */       //   Java source line #675	-> byte code offset #32
/*     */       //   Java source line #676	-> byte code offset #37
/*     */       //   Java source line #679	-> byte code offset #56
/*     */       //   Java source line #681	-> byte code offset #79
/*     */       //   Java source line #682	-> byte code offset #98
/*     */       //   Java source line #684	-> byte code offset #107
/*     */       //   Java source line #686	-> byte code offset #130
/*     */       //   Java source line #687	-> byte code offset #149
/*     */       //   Java source line #481	-> byte code offset #160
/*     */       //   Java source line #482	-> byte code offset #170
/*     */       //   Java source line #484	-> byte code offset #209
/*     */       //   Java source line #488	-> byte code offset #220
/*     */       //   Java source line #489	-> byte code offset #242
/*     */       //   Java source line #490	-> byte code offset #247
/*     */       //   Java source line #491	-> byte code offset #257
/*     */       //   Java source line #493	-> byte code offset #296
/*     */       //   Java source line #499	-> byte code offset #304
/*     */       //   Java source line #505	-> byte code offset #324
/*     */       //   Java source line #509	-> byte code offset #330
/*     */       //   Java source line #513	-> byte code offset #350
/*     */       //   Java source line #514	-> byte code offset #356
/*     */       //   Java source line #513	-> byte code offset #359
/*     */       //   Java source line #515	-> byte code offset #370
/*     */       //   Java source line #517	-> byte code offset #381
/*     */       //   Java source line #518	-> byte code offset #425
/*     */       //   Java source line #521	-> byte code offset #454
/*     */       //   Java source line #522	-> byte code offset #466
/*     */       //   Java source line #523	-> byte code offset #473
/*     */       //   Java source line #527	-> byte code offset #480
/*     */       //   Java source line #529	-> byte code offset #521
/*     */       //   Java source line #531	-> byte code offset #528
/*     */       //   Java source line #532	-> byte code offset #532
/*     */       //   Java source line #533	-> byte code offset #536
/*     */       //   Java source line #534	-> byte code offset #540
/*     */       //   Java source line #673	-> byte code offset #543
/*     */       //   Java source line #675	-> byte code offset #550
/*     */       //   Java source line #676	-> byte code offset #555
/*     */       //   Java source line #679	-> byte code offset #574
/*     */       //   Java source line #681	-> byte code offset #597
/*     */       //   Java source line #682	-> byte code offset #616
/*     */       //   Java source line #684	-> byte code offset #625
/*     */       //   Java source line #686	-> byte code offset #648
/*     */       //   Java source line #687	-> byte code offset #667
/*     */       //   Java source line #537	-> byte code offset #679
/*     */       //   Java source line #541	-> byte code offset #716
/*     */       //   Java source line #546	-> byte code offset #756
/*     */       //   Java source line #673	-> byte code offset #759
/*     */       //   Java source line #675	-> byte code offset #766
/*     */       //   Java source line #676	-> byte code offset #771
/*     */       //   Java source line #679	-> byte code offset #790
/*     */       //   Java source line #681	-> byte code offset #813
/*     */       //   Java source line #682	-> byte code offset #832
/*     */       //   Java source line #684	-> byte code offset #841
/*     */       //   Java source line #686	-> byte code offset #864
/*     */       //   Java source line #687	-> byte code offset #883
/*     */       //   Java source line #550	-> byte code offset #895
/*     */       //   Java source line #551	-> byte code offset #910
/*     */       //   Java source line #558	-> byte code offset #913
/*     */       //   Java source line #673	-> byte code offset #916
/*     */       //   Java source line #675	-> byte code offset #923
/*     */       //   Java source line #676	-> byte code offset #928
/*     */       //   Java source line #679	-> byte code offset #947
/*     */       //   Java source line #681	-> byte code offset #970
/*     */       //   Java source line #682	-> byte code offset #989
/*     */       //   Java source line #684	-> byte code offset #998
/*     */       //   Java source line #686	-> byte code offset #1021
/*     */       //   Java source line #687	-> byte code offset #1040
/*     */       //   Java source line #564	-> byte code offset #1052
/*     */       //   Java source line #673	-> byte code offset #1055
/*     */       //   Java source line #675	-> byte code offset #1062
/*     */       //   Java source line #676	-> byte code offset #1067
/*     */       //   Java source line #679	-> byte code offset #1086
/*     */       //   Java source line #681	-> byte code offset #1109
/*     */       //   Java source line #682	-> byte code offset #1128
/*     */       //   Java source line #684	-> byte code offset #1137
/*     */       //   Java source line #686	-> byte code offset #1160
/*     */       //   Java source line #687	-> byte code offset #1179
/*     */       //   Java source line #568	-> byte code offset #1191
/*     */       //   Java source line #575	-> byte code offset #1199
/*     */       //   Java source line #577	-> byte code offset #1214
/*     */       //   Java source line #578	-> byte code offset #1219
/*     */       //   Java source line #579	-> byte code offset #1230
/*     */       //   Java source line #580	-> byte code offset #1253
/*     */       //   Java source line #581	-> byte code offset #1278
/*     */       //   Java source line #579	-> byte code offset #1308
/*     */       //   Java source line #585	-> byte code offset #1313
/*     */       //   Java source line #587	-> byte code offset #1338
/*     */       //   Java source line #588	-> byte code offset #1349
/*     */       //   Java source line #589	-> byte code offset #1371
/*     */       //   Java source line #590	-> byte code offset #1375
/*     */       //   Java source line #673	-> byte code offset #1378
/*     */       //   Java source line #675	-> byte code offset #1385
/*     */       //   Java source line #676	-> byte code offset #1390
/*     */       //   Java source line #679	-> byte code offset #1409
/*     */       //   Java source line #681	-> byte code offset #1432
/*     */       //   Java source line #682	-> byte code offset #1451
/*     */       //   Java source line #684	-> byte code offset #1460
/*     */       //   Java source line #686	-> byte code offset #1483
/*     */       //   Java source line #687	-> byte code offset #1502
/*     */       //   Java source line #596	-> byte code offset #1514
/*     */       //   Java source line #598	-> byte code offset #1522
/*     */       //   Java source line #599	-> byte code offset #1527
/*     */       //   Java source line #673	-> byte code offset #1534
/*     */       //   Java source line #675	-> byte code offset #1541
/*     */       //   Java source line #676	-> byte code offset #1546
/*     */       //   Java source line #679	-> byte code offset #1565
/*     */       //   Java source line #681	-> byte code offset #1588
/*     */       //   Java source line #682	-> byte code offset #1607
/*     */       //   Java source line #684	-> byte code offset #1616
/*     */       //   Java source line #686	-> byte code offset #1639
/*     */       //   Java source line #687	-> byte code offset #1658
/*     */       //   Java source line #600	-> byte code offset #1670
/*     */       //   Java source line #673	-> byte code offset #1683
/*     */       //   Java source line #675	-> byte code offset #1690
/*     */       //   Java source line #676	-> byte code offset #1695
/*     */       //   Java source line #679	-> byte code offset #1714
/*     */       //   Java source line #681	-> byte code offset #1737
/*     */       //   Java source line #682	-> byte code offset #1756
/*     */       //   Java source line #684	-> byte code offset #1765
/*     */       //   Java source line #686	-> byte code offset #1788
/*     */       //   Java source line #687	-> byte code offset #1807
/*     */       //   Java source line #603	-> byte code offset #1819
/*     */       //   Java source line #607	-> byte code offset #1860
/*     */       //   Java source line #608	-> byte code offset #1867
/*     */       //   Java source line #613	-> byte code offset #1870
/*     */       //   Java source line #614	-> byte code offset #1885
/*     */       //   Java source line #620	-> byte code offset #1888
/*     */       //   Java source line #621	-> byte code offset #1895
/*     */       //   Java source line #673	-> byte code offset #1898
/*     */       //   Java source line #675	-> byte code offset #1905
/*     */       //   Java source line #676	-> byte code offset #1910
/*     */       //   Java source line #679	-> byte code offset #1929
/*     */       //   Java source line #681	-> byte code offset #1952
/*     */       //   Java source line #682	-> byte code offset #1971
/*     */       //   Java source line #684	-> byte code offset #1980
/*     */       //   Java source line #686	-> byte code offset #2003
/*     */       //   Java source line #687	-> byte code offset #2022
/*     */       //   Java source line #623	-> byte code offset #2034
/*     */       //   Java source line #624	-> byte code offset #2039
/*     */       //   Java source line #625	-> byte code offset #2054
/*     */       //   Java source line #628	-> byte code offset #2067
/*     */       //   Java source line #629	-> byte code offset #2072
/*     */       //   Java source line #635	-> byte code offset #2075
/*     */       //   Java source line #673	-> byte code offset #2078
/*     */       //   Java source line #675	-> byte code offset #2085
/*     */       //   Java source line #676	-> byte code offset #2090
/*     */       //   Java source line #679	-> byte code offset #2109
/*     */       //   Java source line #681	-> byte code offset #2132
/*     */       //   Java source line #682	-> byte code offset #2151
/*     */       //   Java source line #684	-> byte code offset #2160
/*     */       //   Java source line #686	-> byte code offset #2183
/*     */       //   Java source line #687	-> byte code offset #2202
/*     */       //   Java source line #640	-> byte code offset #2214
/*     */       //   Java source line #641	-> byte code offset #2221
/*     */       //   Java source line #646	-> byte code offset #2224
/*     */       //   Java source line #652	-> byte code offset #2232
/*     */       //   Java source line #656	-> byte code offset #2240
/*     */       //   Java source line #658	-> byte code offset #2243
/*     */       //   Java source line #661	-> byte code offset #2244
/*     */       //   Java source line #662	-> byte code offset #2253
/*     */       //   Java source line #663	-> byte code offset #2257
/*     */       //   Java source line #665	-> byte code offset #2266
/*     */       //   Java source line #667	-> byte code offset #2267
/*     */       //   Java source line #668	-> byte code offset #2271
/*     */       //   Java source line #673	-> byte code offset #2273
/*     */       //   Java source line #675	-> byte code offset #2282
/*     */       //   Java source line #676	-> byte code offset #2287
/*     */       //   Java source line #679	-> byte code offset #2306
/*     */       //   Java source line #681	-> byte code offset #2329
/*     */       //   Java source line #682	-> byte code offset #2348
/*     */       //   Java source line #684	-> byte code offset #2357
/*     */       //   Java source line #686	-> byte code offset #2380
/*     */       //   Java source line #687	-> byte code offset #2399
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	2411	0	this	DecryptedEndPoint
/*     */       //   0	2411	1	buffer	ByteBuffer
/*     */       //   24	135	2	i	int
/*     */       //   243	2	2	app_in	ByteBuffer
/*     */       //   292	2	2	app_in	ByteBuffer
/*     */       //   303	1220	2	app_in	ByteBuffer
/*     */       //   2243	19	2	e	IllegalStateException
/*     */       //   2266	6	2	e	Exception
/*     */       //   323	218	3	net_filled	int
/*     */       //   328	35	4	pos	int
/*     */       //   348	3	5	unwrapResult	javax.net.ssl.SSLEngineResult
/*     */       //   370	1158	5	unwrapResult	javax.net.ssl.SSLEngineResult
/*     */       //   359	9	6	localObject1	Object
/*     */       //   464	1359	6	handshakeStatus	SSLEngineResult.HandshakeStatus
/*     */       //   471	729	7	unwrapHandshakeStatus	SSLEngineResult.HandshakeStatus
/*     */       //   478	205	8	unwrapResultStatus	javax.net.ssl.SSLEngineResult.Status
/*     */       //   541	1671	9	j	int
/*     */       //   2273	136	10	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   330	350	359	finally
/*     */       //   359	361	359	finally
/*     */       //   0	25	2243	java/lang/IllegalStateException
/*     */       //   160	543	2243	java/lang/IllegalStateException
/*     */       //   679	759	2243	java/lang/IllegalStateException
/*     */       //   895	916	2243	java/lang/IllegalStateException
/*     */       //   1052	1055	2243	java/lang/IllegalStateException
/*     */       //   1191	1378	2243	java/lang/IllegalStateException
/*     */       //   1514	1534	2243	java/lang/IllegalStateException
/*     */       //   1670	1683	2243	java/lang/IllegalStateException
/*     */       //   1819	1898	2243	java/lang/IllegalStateException
/*     */       //   2034	2078	2243	java/lang/IllegalStateException
/*     */       //   2214	2243	2243	java/lang/IllegalStateException
/*     */       //   0	25	2266	java/lang/Exception
/*     */       //   160	543	2266	java/lang/Exception
/*     */       //   679	759	2266	java/lang/Exception
/*     */       //   895	916	2266	java/lang/Exception
/*     */       //   1052	1055	2266	java/lang/Exception
/*     */       //   1191	1378	2266	java/lang/Exception
/*     */       //   1514	1534	2266	java/lang/Exception
/*     */       //   1670	1683	2266	java/lang/Exception
/*     */       //   1819	1898	2266	java/lang/Exception
/*     */       //   2034	2078	2266	java/lang/Exception
/*     */       //   2214	2243	2266	java/lang/Exception
/*     */       //   0	25	2273	finally
/*     */       //   160	543	2273	finally
/*     */       //   679	759	2273	finally
/*     */       //   895	916	2273	finally
/*     */       //   1052	1055	2273	finally
/*     */       //   1191	1378	2273	finally
/*     */       //   1514	1534	2273	finally
/*     */       //   1670	1683	2273	finally
/*     */       //   1819	1898	2273	finally
/*     */       //   2034	2078	2273	finally
/*     */       //   2214	2275	2273	finally
/*     */     }
/*     */     
/*     */     private void closeInbound()
/*     */     {
/*     */       try
/*     */       {
/* 696 */         SslConnection.this._sslEngine.closeInbound();
/*     */       }
/*     */       catch (SSLException x)
/*     */       {
/* 700 */         SslConnection.LOG.ignore(x);
/*     */       }
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public synchronized boolean flush(ByteBuffer... appOuts)
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   3: invokeinterface 89 1 0
/*     */       //   8: ifeq +59 -> 67
/*     */       //   11: aload_1
/*     */       //   12: astore_2
/*     */       //   13: aload_2
/*     */       //   14: arraylength
/*     */       //   15: istore_3
/*     */       //   16: iconst_0
/*     */       //   17: istore 4
/*     */       //   19: iload 4
/*     */       //   21: iload_3
/*     */       //   22: if_icmpge +45 -> 67
/*     */       //   25: aload_2
/*     */       //   26: iload 4
/*     */       //   28: aaload
/*     */       //   29: astore 5
/*     */       //   31: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   34: ldc_w 415
/*     */       //   37: iconst_2
/*     */       //   38: anewarray 93	java/lang/Object
/*     */       //   41: dup
/*     */       //   42: iconst_0
/*     */       //   43: aload_0
/*     */       //   44: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   47: aastore
/*     */       //   48: dup
/*     */       //   49: iconst_1
/*     */       //   50: aload 5
/*     */       //   52: invokestatic 292	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:toHexSummary	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*     */       //   55: aastore
/*     */       //   56: invokeinterface 97 3 0
/*     */       //   61: iinc 4 1
/*     */       //   64: goto -45 -> 19
/*     */       //   67: aload_0
/*     */       //   68: getfield 109	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_cannotAcceptMoreAppDataToFlush	Z
/*     */       //   71: ifeq +39 -> 110
/*     */       //   74: aload_0
/*     */       //   75: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   78: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   81: invokevirtual 418	javax/net/ssl/SSLEngine:isOutboundDone	()Z
/*     */       //   84: ifeq +18 -> 102
/*     */       //   87: new 387	com/facebook/presto/jdbc/internal/jetty/io/EofException
/*     */       //   90: dup
/*     */       //   91: new 420	java/nio/channels/ClosedChannelException
/*     */       //   94: dup
/*     */       //   95: invokespecial 421	java/nio/channels/ClosedChannelException:<init>	()V
/*     */       //   98: invokespecial 389	com/facebook/presto/jdbc/internal/jetty/io/EofException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   101: athrow
/*     */       //   102: iconst_0
/*     */       //   103: istore_2
/*     */       //   104: aload_0
/*     */       //   105: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   108: iload_2
/*     */       //   109: ireturn
/*     */       //   110: aload_0
/*     */       //   111: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   114: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   117: ifnonnull +39 -> 156
/*     */       //   120: aload_0
/*     */       //   121: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   124: aload_0
/*     */       //   125: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   128: invokestatic 229	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1500	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Lcom/facebook/presto/jdbc/internal/jetty/io/ByteBufferPool;
/*     */       //   131: aload_0
/*     */       //   132: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   135: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   138: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   141: invokeinterface 245 1 0
/*     */       //   146: iconst_1
/*     */       //   147: invokeinterface 249 3 0
/*     */       //   152: invokestatic 427	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$702	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
/*     */       //   155: pop
/*     */       //   156: aload_0
/*     */       //   157: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   160: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   163: invokestatic 252	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:compact	(Ljava/nio/ByteBuffer;)Z
/*     */       //   166: pop
/*     */       //   167: aload_0
/*     */       //   168: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   171: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   174: invokestatic 260	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFill	(Ljava/nio/ByteBuffer;)I
/*     */       //   177: istore_2
/*     */       //   178: aload_0
/*     */       //   179: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   182: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   185: aload_1
/*     */       //   186: aload_0
/*     */       //   187: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   190: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   193: invokevirtual 431	javax/net/ssl/SSLEngine:wrap	([Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)Ljavax/net/ssl/SSLEngineResult;
/*     */       //   196: astore_3
/*     */       //   197: aload_0
/*     */       //   198: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   201: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   204: iload_2
/*     */       //   205: invokestatic 268	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*     */       //   208: goto +19 -> 227
/*     */       //   211: astore 6
/*     */       //   213: aload_0
/*     */       //   214: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   217: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   220: iload_2
/*     */       //   221: invokestatic 268	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*     */       //   224: aload 6
/*     */       //   226: athrow
/*     */       //   227: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   230: invokeinterface 89 1 0
/*     */       //   235: ifeq +39 -> 274
/*     */       //   238: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   241: ldc_w 433
/*     */       //   244: iconst_2
/*     */       //   245: anewarray 93	java/lang/Object
/*     */       //   248: dup
/*     */       //   249: iconst_0
/*     */       //   250: aload_0
/*     */       //   251: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   254: aastore
/*     */       //   255: dup
/*     */       //   256: iconst_1
/*     */       //   257: aload_3
/*     */       //   258: invokevirtual 280	javax/net/ssl/SSLEngineResult:toString	()Ljava/lang/String;
/*     */       //   261: bipush 10
/*     */       //   263: bipush 32
/*     */       //   265: invokevirtual 286	java/lang/String:replace	(CC)Ljava/lang/String;
/*     */       //   268: aastore
/*     */       //   269: invokeinterface 97 3 0
/*     */       //   274: aload_3
/*     */       //   275: invokevirtual 297	javax/net/ssl/SSLEngineResult:getStatus	()Ljavax/net/ssl/SSLEngineResult$Status;
/*     */       //   278: astore 4
/*     */       //   280: iconst_1
/*     */       //   281: istore 5
/*     */       //   283: aload_1
/*     */       //   284: astore 6
/*     */       //   286: aload 6
/*     */       //   288: arraylength
/*     */       //   289: istore 7
/*     */       //   291: iconst_0
/*     */       //   292: istore 8
/*     */       //   294: iload 8
/*     */       //   296: iload 7
/*     */       //   298: if_icmpge +27 -> 325
/*     */       //   301: aload 6
/*     */       //   303: iload 8
/*     */       //   305: aaload
/*     */       //   306: astore 9
/*     */       //   308: aload 9
/*     */       //   310: invokestatic 107	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:hasContent	(Ljava/nio/ByteBuffer;)Z
/*     */       //   313: ifeq +6 -> 319
/*     */       //   316: iconst_0
/*     */       //   317: istore 5
/*     */       //   319: iinc 8 1
/*     */       //   322: goto -28 -> 294
/*     */       //   325: getstatic 317	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$3:$SwitchMap$javax$net$ssl$SSLEngineResult$Status	[I
/*     */       //   328: aload 4
/*     */       //   330: invokevirtual 320	javax/net/ssl/SSLEngineResult$Status:ordinal	()I
/*     */       //   333: iaload
/*     */       //   334: lookupswitch	default:+137->471, 1:+26->360, 2:+129->463
/*     */       //   360: aload_0
/*     */       //   361: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   364: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   367: invokestatic 107	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:hasContent	(Ljava/nio/ByteBuffer;)Z
/*     */       //   370: ifeq +70 -> 440
/*     */       //   373: aload_0
/*     */       //   374: iconst_1
/*     */       //   375: putfield 109	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_cannotAcceptMoreAppDataToFlush	Z
/*     */       //   378: aload_0
/*     */       //   379: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   382: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   385: iconst_1
/*     */       //   386: anewarray 111	java/nio/ByteBuffer
/*     */       //   389: dup
/*     */       //   390: iconst_0
/*     */       //   391: aload_0
/*     */       //   392: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   395: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   398: aastore
/*     */       //   399: invokeinterface 434 2 0
/*     */       //   404: pop
/*     */       //   405: aload_0
/*     */       //   406: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   409: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   412: invokeinterface 437 1 0
/*     */       //   417: aload_0
/*     */       //   418: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   421: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   424: invokestatic 107	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:hasContent	(Ljava/nio/ByteBuffer;)Z
/*     */       //   427: ifeq +25 -> 452
/*     */       //   430: iconst_0
/*     */       //   431: istore 6
/*     */       //   433: aload_0
/*     */       //   434: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   437: iload 6
/*     */       //   439: ireturn
/*     */       //   440: aload_0
/*     */       //   441: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   444: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   447: invokeinterface 437 1 0
/*     */       //   452: iload 5
/*     */       //   454: istore 6
/*     */       //   456: aload_0
/*     */       //   457: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   460: iload 6
/*     */       //   462: ireturn
/*     */       //   463: new 213	java/lang/IllegalStateException
/*     */       //   466: dup
/*     */       //   467: invokespecial 335	java/lang/IllegalStateException:<init>	()V
/*     */       //   470: athrow
/*     */       //   471: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   474: invokeinterface 89 1 0
/*     */       //   479: ifeq +43 -> 522
/*     */       //   482: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   485: ldc_w 439
/*     */       //   488: iconst_3
/*     */       //   489: anewarray 93	java/lang/Object
/*     */       //   492: dup
/*     */       //   493: iconst_0
/*     */       //   494: aload_0
/*     */       //   495: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   498: aastore
/*     */       //   499: dup
/*     */       //   500: iconst_1
/*     */       //   501: aload 4
/*     */       //   503: aastore
/*     */       //   504: dup
/*     */       //   505: iconst_2
/*     */       //   506: aload_0
/*     */       //   507: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   510: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   513: invokestatic 292	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:toHexSummary	(Ljava/nio/ByteBuffer;)Ljava/lang/String;
/*     */       //   516: aastore
/*     */       //   517: invokeinterface 97 3 0
/*     */       //   522: aload_3
/*     */       //   523: invokevirtual 293	javax/net/ssl/SSLEngineResult:getHandshakeStatus	()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   526: getstatic 338	javax/net/ssl/SSLEngineResult$HandshakeStatus:FINISHED	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   529: if_acmpne +84 -> 613
/*     */       //   532: aload_0
/*     */       //   533: getfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   536: ifne +77 -> 613
/*     */       //   539: aload_0
/*     */       //   540: iconst_1
/*     */       //   541: putfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   544: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   547: invokeinterface 89 1 0
/*     */       //   552: ifeq +61 -> 613
/*     */       //   555: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   558: ldc_w 441
/*     */       //   561: iconst_3
/*     */       //   562: anewarray 93	java/lang/Object
/*     */       //   565: dup
/*     */       //   566: iconst_0
/*     */       //   567: aload_0
/*     */       //   568: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   571: aastore
/*     */       //   572: dup
/*     */       //   573: iconst_1
/*     */       //   574: aload_0
/*     */       //   575: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   578: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   581: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   584: invokeinterface 354 1 0
/*     */       //   589: aastore
/*     */       //   590: dup
/*     */       //   591: iconst_2
/*     */       //   592: aload_0
/*     */       //   593: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   596: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   599: invokevirtual 192	javax/net/ssl/SSLEngine:getSession	()Ljavax/net/ssl/SSLSession;
/*     */       //   602: invokeinterface 357 1 0
/*     */       //   607: aastore
/*     */       //   608: invokeinterface 97 3 0
/*     */       //   613: aload_0
/*     */       //   614: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   617: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   620: invokevirtual 125	javax/net/ssl/SSLEngine:getHandshakeStatus	()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   623: astore 6
/*     */       //   625: aload_0
/*     */       //   626: getfield 340	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_handshaken	Z
/*     */       //   629: ifeq +77 -> 706
/*     */       //   632: aload 6
/*     */       //   634: getstatic 360	javax/net/ssl/SSLEngineResult$HandshakeStatus:NOT_HANDSHAKING	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   637: if_acmpeq +69 -> 706
/*     */       //   640: aload_0
/*     */       //   641: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   644: invokevirtual 363	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:isRenegotiationAllowed	()Z
/*     */       //   647: ifne +59 -> 706
/*     */       //   650: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   653: invokeinterface 89 1 0
/*     */       //   658: ifeq +25 -> 683
/*     */       //   661: invokestatic 84	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$200	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   664: ldc_w 365
/*     */       //   667: iconst_1
/*     */       //   668: anewarray 93	java/lang/Object
/*     */       //   671: dup
/*     */       //   672: iconst_0
/*     */       //   673: aload_0
/*     */       //   674: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   677: aastore
/*     */       //   678: invokeinterface 97 3 0
/*     */       //   683: aload_0
/*     */       //   684: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   687: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   690: invokeinterface 437 1 0
/*     */       //   695: iload 5
/*     */       //   697: istore 7
/*     */       //   699: aload_0
/*     */       //   700: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   703: iload 7
/*     */       //   705: ireturn
/*     */       //   706: aload_0
/*     */       //   707: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   710: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   713: invokestatic 107	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:hasContent	(Ljava/nio/ByteBuffer;)Z
/*     */       //   716: ifeq +59 -> 775
/*     */       //   719: aload_0
/*     */       //   720: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   723: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   726: iconst_1
/*     */       //   727: anewarray 111	java/nio/ByteBuffer
/*     */       //   730: dup
/*     */       //   731: iconst_0
/*     */       //   732: aload_0
/*     */       //   733: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   736: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   739: aastore
/*     */       //   740: invokeinterface 434 2 0
/*     */       //   745: ifne +30 -> 775
/*     */       //   748: aload_0
/*     */       //   749: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   752: invokevirtual 38	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:getEndPoint	()Lcom/facebook/presto/jdbc/internal/jetty/io/EndPoint;
/*     */       //   755: iconst_1
/*     */       //   756: anewarray 111	java/nio/ByteBuffer
/*     */       //   759: dup
/*     */       //   760: iconst_0
/*     */       //   761: aload_0
/*     */       //   762: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   765: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   768: aastore
/*     */       //   769: invokeinterface 434 2 0
/*     */       //   774: pop
/*     */       //   775: getstatic 323	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$3:$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus	[I
/*     */       //   778: aload 6
/*     */       //   780: invokevirtual 324	javax/net/ssl/SSLEngineResult$HandshakeStatus:ordinal	()I
/*     */       //   783: iaload
/*     */       //   784: tableswitch	default:+203->987, 1:+36->820, 2:+99->883, 3:+117->901, 4:+120->904, 5:+195->979
/*     */       //   820: iload 5
/*     */       //   822: ifne +29 -> 851
/*     */       //   825: aload_3
/*     */       //   826: invokevirtual 293	javax/net/ssl/SSLEngineResult:getHandshakeStatus	()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   829: getstatic 338	javax/net/ssl/SSLEngineResult$HandshakeStatus:FINISHED	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   832: if_acmpne +19 -> 851
/*     */       //   835: aload_0
/*     */       //   836: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   839: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   842: invokestatic 379	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:isEmpty	(Ljava/nio/ByteBuffer;)Z
/*     */       //   845: ifeq +6 -> 851
/*     */       //   848: goto -692 -> 156
/*     */       //   851: iload 5
/*     */       //   853: ifeq +20 -> 873
/*     */       //   856: aload_0
/*     */       //   857: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   860: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   863: invokestatic 379	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:isEmpty	(Ljava/nio/ByteBuffer;)Z
/*     */       //   866: ifeq +7 -> 873
/*     */       //   869: iconst_1
/*     */       //   870: goto +4 -> 874
/*     */       //   873: iconst_0
/*     */       //   874: istore 7
/*     */       //   876: aload_0
/*     */       //   877: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   880: iload 7
/*     */       //   882: ireturn
/*     */       //   883: aload_0
/*     */       //   884: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   887: invokestatic 119	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljavax/net/ssl/SSLEngine;
/*     */       //   890: invokevirtual 328	javax/net/ssl/SSLEngine:getDelegatedTask	()Ljava/lang/Runnable;
/*     */       //   893: invokeinterface 333 1 0
/*     */       //   898: goto -742 -> 156
/*     */       //   901: goto -745 -> 156
/*     */       //   904: aload_1
/*     */       //   905: iconst_0
/*     */       //   906: aaload
/*     */       //   907: invokestatic 372	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1700	()Ljava/nio/ByteBuffer;
/*     */       //   910: if_acmpeq +37 -> 947
/*     */       //   913: aload_0
/*     */       //   914: invokevirtual 445	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:getFillInterest	()Lcom/facebook/presto/jdbc/internal/jetty/io/FillInterest;
/*     */       //   917: invokevirtual 450	com/facebook/presto/jdbc/internal/jetty/io/FillInterest:isInterested	()Z
/*     */       //   920: ifne +27 -> 947
/*     */       //   923: aload_0
/*     */       //   924: iconst_1
/*     */       //   925: putfield 131	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:_flushRequiresFillToProgress	Z
/*     */       //   928: aload_0
/*     */       //   929: invokestatic 369	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$1600	()Ljava/nio/ByteBuffer;
/*     */       //   932: invokevirtual 451	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:fill	(Ljava/nio/ByteBuffer;)I
/*     */       //   935: pop
/*     */       //   936: aload 6
/*     */       //   938: getstatic 454	javax/net/ssl/SSLEngineResult$HandshakeStatus:NEED_WRAP	Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
/*     */       //   941: if_acmpne +6 -> 947
/*     */       //   944: goto -788 -> 156
/*     */       //   947: iload 5
/*     */       //   949: ifeq +20 -> 969
/*     */       //   952: aload_0
/*     */       //   953: getfield 34	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:this$0	Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;
/*     */       //   956: invokestatic 101	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection;)Ljava/nio/ByteBuffer;
/*     */       //   959: invokestatic 379	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:isEmpty	(Ljava/nio/ByteBuffer;)Z
/*     */       //   962: ifeq +7 -> 969
/*     */       //   965: iconst_1
/*     */       //   966: goto +4 -> 970
/*     */       //   969: iconst_0
/*     */       //   970: istore 7
/*     */       //   972: aload_0
/*     */       //   973: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   976: iload 7
/*     */       //   978: ireturn
/*     */       //   979: new 213	java/lang/IllegalStateException
/*     */       //   982: dup
/*     */       //   983: invokespecial 335	java/lang/IllegalStateException:<init>	()V
/*     */       //   986: athrow
/*     */       //   987: goto -831 -> 156
/*     */       //   990: astore 10
/*     */       //   992: aload_0
/*     */       //   993: invokespecial 424	com/facebook/presto/jdbc/internal/jetty/io/ssl/SslConnection$DecryptedEndPoint:releaseEncryptedOutputBuffer	()V
/*     */       //   996: aload 10
/*     */       //   998: athrow
/*     */       // Line number table:
/*     */       //   Java source line #714	-> byte code offset #0
/*     */       //   Java source line #716	-> byte code offset #11
/*     */       //   Java source line #717	-> byte code offset #31
/*     */       //   Java source line #716	-> byte code offset #61
/*     */       //   Java source line #722	-> byte code offset #67
/*     */       //   Java source line #724	-> byte code offset #74
/*     */       //   Java source line #725	-> byte code offset #87
/*     */       //   Java source line #726	-> byte code offset #102
/*     */       //   Java source line #855	-> byte code offset #104
/*     */       //   Java source line #730	-> byte code offset #110
/*     */       //   Java source line #731	-> byte code offset #120
/*     */       //   Java source line #736	-> byte code offset #156
/*     */       //   Java source line #737	-> byte code offset #167
/*     */       //   Java source line #741	-> byte code offset #178
/*     */       //   Java source line #745	-> byte code offset #197
/*     */       //   Java source line #746	-> byte code offset #208
/*     */       //   Java source line #745	-> byte code offset #211
/*     */       //   Java source line #747	-> byte code offset #227
/*     */       //   Java source line #748	-> byte code offset #238
/*     */       //   Java source line #750	-> byte code offset #274
/*     */       //   Java source line #752	-> byte code offset #280
/*     */       //   Java source line #753	-> byte code offset #283
/*     */       //   Java source line #754	-> byte code offset #308
/*     */       //   Java source line #755	-> byte code offset #316
/*     */       //   Java source line #753	-> byte code offset #319
/*     */       //   Java source line #758	-> byte code offset #325
/*     */       //   Java source line #762	-> byte code offset #360
/*     */       //   Java source line #764	-> byte code offset #373
/*     */       //   Java source line #765	-> byte code offset #378
/*     */       //   Java source line #766	-> byte code offset #405
/*     */       //   Java source line #771	-> byte code offset #417
/*     */       //   Java source line #772	-> byte code offset #430
/*     */       //   Java source line #855	-> byte code offset #433
/*     */       //   Java source line #777	-> byte code offset #440
/*     */       //   Java source line #779	-> byte code offset #452
/*     */       //   Java source line #855	-> byte code offset #456
/*     */       //   Java source line #782	-> byte code offset #463
/*     */       //   Java source line #785	-> byte code offset #471
/*     */       //   Java source line #786	-> byte code offset #482
/*     */       //   Java source line #788	-> byte code offset #522
/*     */       //   Java source line #790	-> byte code offset #539
/*     */       //   Java source line #791	-> byte code offset #544
/*     */       //   Java source line #792	-> byte code offset #555
/*     */       //   Java source line #795	-> byte code offset #613
/*     */       //   Java source line #798	-> byte code offset #625
/*     */       //   Java source line #800	-> byte code offset #650
/*     */       //   Java source line #801	-> byte code offset #661
/*     */       //   Java source line #802	-> byte code offset #683
/*     */       //   Java source line #803	-> byte code offset #695
/*     */       //   Java source line #855	-> byte code offset #699
/*     */       //   Java source line #807	-> byte code offset #706
/*     */       //   Java source line #808	-> byte code offset #719
/*     */       //   Java source line #809	-> byte code offset #748
/*     */       //   Java source line #812	-> byte code offset #775
/*     */       //   Java source line #818	-> byte code offset #820
/*     */       //   Java source line #819	-> byte code offset #848
/*     */       //   Java source line #822	-> byte code offset #851
/*     */       //   Java source line #855	-> byte code offset #876
/*     */       //   Java source line #826	-> byte code offset #883
/*     */       //   Java source line #827	-> byte code offset #898
/*     */       //   Java source line #831	-> byte code offset #901
/*     */       //   Java source line #836	-> byte code offset #904
/*     */       //   Java source line #839	-> byte code offset #923
/*     */       //   Java source line #840	-> byte code offset #928
/*     */       //   Java source line #842	-> byte code offset #936
/*     */       //   Java source line #843	-> byte code offset #944
/*     */       //   Java source line #845	-> byte code offset #947
/*     */       //   Java source line #855	-> byte code offset #972
/*     */       //   Java source line #848	-> byte code offset #979
/*     */       //   Java source line #851	-> byte code offset #987
/*     */       //   Java source line #855	-> byte code offset #990
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	999	0	this	DecryptedEndPoint
/*     */       //   0	999	1	appOuts	ByteBuffer[]
/*     */       //   12	14	2	arrayOfByteBuffer1	ByteBuffer[]
/*     */       //   103	6	2	bool1	boolean
/*     */       //   177	44	2	pos	int
/*     */       //   15	8	3	i	int
/*     */       //   196	2	3	wrapResult	javax.net.ssl.SSLEngineResult
/*     */       //   227	599	3	wrapResult	javax.net.ssl.SSLEngineResult
/*     */       //   17	45	4	j	int
/*     */       //   278	224	4	wrapResultStatus	javax.net.ssl.SSLEngineResult.Status
/*     */       //   29	22	5	b	ByteBuffer
/*     */       //   281	667	5	allConsumed	boolean
/*     */       //   211	14	6	localObject1	Object
/*     */       //   284	18	6	arrayOfByteBuffer2	ByteBuffer[]
/*     */       //   431	30	6	bool2	boolean
/*     */       //   623	314	6	handshakeStatus	SSLEngineResult.HandshakeStatus
/*     */       //   289	10	7	k	int
/*     */       //   697	280	7	bool3	boolean
/*     */       //   292	28	8	m	int
/*     */       //   306	3	9	b	ByteBuffer
/*     */       //   990	7	10	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   178	197	211	finally
/*     */       //   211	213	211	finally
/*     */       //   67	104	990	finally
/*     */       //   110	433	990	finally
/*     */       //   440	456	990	finally
/*     */       //   463	699	990	finally
/*     */       //   706	876	990	finally
/*     */       //   883	972	990	finally
/*     */       //   979	992	990	finally
/*     */     }
/*     */     
/*     */     private void releaseEncryptedOutputBuffer()
/*     */     {
/* 861 */       if (!Thread.holdsLock(this))
/* 862 */         throw new IllegalStateException();
/* 863 */       if ((SslConnection.this._encryptedOutput != null) && (!SslConnection.this._encryptedOutput.hasRemaining()))
/*     */       {
/* 865 */         SslConnection.this._bufferPool.release(SslConnection.this._encryptedOutput);
/* 866 */         SslConnection.this._encryptedOutput = null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void shutdownOutput()
/*     */     {
/* 873 */       boolean ishut = isInputShutdown();
/* 874 */       boolean oshut = isOutputShutdown();
/* 875 */       if (SslConnection.LOG.isDebugEnabled())
/* 876 */         SslConnection.LOG.debug("{} shutdownOutput: oshut={}, ishut={}", new Object[] { SslConnection.this, Boolean.valueOf(oshut), Boolean.valueOf(ishut) });
/* 877 */       if (ishut)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 883 */         SslConnection.this.getEndPoint().close();
/*     */       }
/* 885 */       else if (!oshut)
/*     */       {
/*     */         try
/*     */         {
/* 889 */           synchronized (this)
/*     */           {
/* 891 */             SslConnection.this._sslEngine.closeOutbound();
/* 892 */             flush(new ByteBuffer[] { BufferUtil.EMPTY_BUFFER });
/* 893 */             ensureFillInterested();
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 898 */           SslConnection.LOG.ignore(e);
/* 899 */           SslConnection.this.getEndPoint().close();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private void ensureFillInterested()
/*     */     {
/* 906 */       if (!SslConnection.this.isFillInterested()) {
/* 907 */         SslConnection.this.fillInterested();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isOutputShutdown()
/*     */     {
/* 913 */       return (SslConnection.this._sslEngine.isOutboundDone()) || (SslConnection.this.getEndPoint().isOutputShutdown());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close()
/*     */     {
/* 920 */       shutdownOutput();
/* 921 */       SslConnection.this.getEndPoint().close();
/* 922 */       super.close();
/*     */     }
/*     */     
/*     */ 
/*     */     public Object getTransport()
/*     */     {
/* 928 */       return SslConnection.this.getEndPoint();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isInputShutdown()
/*     */     {
/* 934 */       return SslConnection.this._sslEngine.isInboundDone();
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 940 */       return super.toString() + "->" + SslConnection.this.getEndPoint().toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ssl\SslConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */