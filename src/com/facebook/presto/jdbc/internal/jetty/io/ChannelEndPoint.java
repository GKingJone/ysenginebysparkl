/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ByteChannel;
/*     */ import java.nio.channels.SocketChannel;
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
/*     */ public class ChannelEndPoint
/*     */   extends AbstractEndPoint
/*     */ {
/*  39 */   private static final Logger LOG = Log.getLogger(ChannelEndPoint.class);
/*     */   
/*     */   private final SocketChannel _channel;
/*     */   private final Socket _socket;
/*     */   private volatile boolean _ishut;
/*     */   private volatile boolean _oshut;
/*     */   
/*     */   public ChannelEndPoint(Scheduler scheduler, SocketChannel channel)
/*     */   {
/*  48 */     super(scheduler, 
/*  49 */       (InetSocketAddress)channel.socket().getLocalSocketAddress(), 
/*  50 */       (InetSocketAddress)channel.socket().getRemoteSocketAddress());
/*  51 */     this._channel = channel;
/*  52 */     this._socket = channel.socket();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOptimizedForDirectBuffers()
/*     */   {
/*  58 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/*  64 */     return this._channel.isOpen();
/*     */   }
/*     */   
/*     */   protected void shutdownInput()
/*     */   {
/*  69 */     if (LOG.isDebugEnabled())
/*  70 */       LOG.debug("ishut {}", new Object[] { this });
/*  71 */     this._ishut = true;
/*  72 */     if (this._oshut) {
/*  73 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void shutdownOutput()
/*     */   {
/*  79 */     if (LOG.isDebugEnabled())
/*  80 */       LOG.debug("oshut {}", new Object[] { this });
/*  81 */     this._oshut = true;
/*  82 */     if (this._channel.isOpen())
/*     */     {
/*     */       try
/*     */       {
/*  86 */         if (!this._socket.isOutputShutdown()) {
/*  87 */           this._socket.shutdownOutput();
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/*  91 */         LOG.debug(e);
/*     */       }
/*     */       finally
/*     */       {
/*  95 */         if (this._ishut)
/*     */         {
/*  97 */           close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOutputShutdown()
/*     */   {
/* 106 */     return (this._oshut) || (!this._channel.isOpen()) || (this._socket.isOutputShutdown());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInputShutdown()
/*     */   {
/* 112 */     return (this._ishut) || (!this._channel.isOpen()) || (this._socket.isInputShutdown());
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 118 */     super.close();
/* 119 */     if (LOG.isDebugEnabled()) {
/* 120 */       LOG.debug("close {}", new Object[] { this });
/*     */     }
/*     */     try {
/* 123 */       this._channel.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 127 */       LOG.debug(e);
/*     */     }
/*     */     finally
/*     */     {
/* 131 */       this._ishut = true;
/* 132 */       this._oshut = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public int fill(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 139 */     if (this._ishut) {
/* 140 */       return -1;
/*     */     }
/* 142 */     int pos = BufferUtil.flipToFill(buffer);
/*     */     try
/*     */     {
/* 145 */       int filled = this._channel.read(buffer);
/* 146 */       if (LOG.isDebugEnabled()) {
/* 147 */         LOG.debug("filled {} {}", new Object[] { Integer.valueOf(filled), this });
/*     */       }
/* 149 */       if (filled > 0) {
/* 150 */         notIdle();
/* 151 */       } else if (filled == -1) {
/* 152 */         shutdownInput();
/*     */       }
/* 154 */       return filled;
/*     */     }
/*     */     catch (IOException e) {
/*     */       int i;
/* 158 */       LOG.debug(e);
/* 159 */       shutdownInput();
/* 160 */       return -1;
/*     */     }
/*     */     finally
/*     */     {
/* 164 */       BufferUtil.flipToFlush(buffer, pos);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean flush(ByteBuffer... buffers)
/*     */     throws IOException
/*     */   {
/* 171 */     long flushed = 0L;
/*     */     try
/*     */     {
/* 174 */       if (buffers.length == 1) {
/* 175 */         flushed = this._channel.write(buffers[0]);
/* 176 */       } else if (buffers.length > 1) {
/* 177 */         flushed = this._channel.write(buffers, 0, buffers.length);
/*     */       }
/*     */       else {
/* 180 */         for (ByteBuffer b : buffers)
/*     */         {
/* 182 */           if (b.hasRemaining())
/*     */           {
/* 184 */             int l = this._channel.write(b);
/* 185 */             if (l > 0)
/* 186 */               flushed += l;
/* 187 */             if (b.hasRemaining())
/*     */               break;
/*     */           }
/*     */         }
/*     */       }
/* 192 */       if (LOG.isDebugEnabled()) {
/* 193 */         LOG.debug("flushed {} {}", new Object[] { Long.valueOf(flushed), this });
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 197 */       throw new EofException((Throwable)e);
/*     */     }
/*     */     
/* 200 */     if (flushed > 0L) {
/* 201 */       notIdle();
/*     */     }
/* 203 */     for (ByteBuffer b : buffers) {
/* 204 */       if (!BufferUtil.isEmpty(b))
/* 205 */         return false;
/*     */     }
/* 207 */     return true;
/*     */   }
/*     */   
/*     */   public ByteChannel getChannel()
/*     */   {
/* 212 */     return this._channel;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getTransport()
/*     */   {
/* 218 */     return this._channel;
/*     */   }
/*     */   
/*     */   public Socket getSocket()
/*     */   {
/* 223 */     return this._socket;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void onIncompleteFlush()
/*     */   {
/* 229 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   protected void needsFillInterest()
/*     */     throws IOException
/*     */   {
/* 235 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ChannelEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */