/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkTrafficSelectChannelEndPoint
/*     */   extends SelectChannelEndPoint
/*     */ {
/*  34 */   private static final Logger LOG = Log.getLogger(NetworkTrafficSelectChannelEndPoint.class);
/*     */   private final List<NetworkTrafficListener> listeners;
/*     */   
/*     */   public NetworkTrafficSelectChannelEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key, Scheduler scheduler, long idleTimeout, List<NetworkTrafficListener> listeners)
/*     */     throws IOException
/*     */   {
/*  40 */     super(channel, selectSet, key, scheduler, idleTimeout);
/*  41 */     this.listeners = listeners;
/*     */   }
/*     */   
/*     */   public int fill(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/*  47 */     int read = super.fill(buffer);
/*  48 */     notifyIncoming(buffer, read);
/*  49 */     return read;
/*     */   }
/*     */   
/*     */   public boolean flush(ByteBuffer... buffers)
/*     */     throws IOException
/*     */   {
/*  55 */     boolean flushed = true;
/*  56 */     for (ByteBuffer b : buffers)
/*     */     {
/*  58 */       if (b.hasRemaining())
/*     */       {
/*  60 */         int position = b.position();
/*  61 */         ByteBuffer view = b.slice();
/*  62 */         flushed &= super.flush(new ByteBuffer[] { b });
/*  63 */         int l = b.position() - position;
/*  64 */         view.limit(view.position() + l);
/*  65 */         notifyOutgoing(view);
/*  66 */         if (!flushed)
/*     */           break;
/*     */       }
/*     */     }
/*  70 */     return flushed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onOpen()
/*     */   {
/*  78 */     super.onOpen();
/*  79 */     if ((this.listeners != null) && (!this.listeners.isEmpty()))
/*     */     {
/*  81 */       for (NetworkTrafficListener listener : this.listeners)
/*     */       {
/*     */         try
/*     */         {
/*  85 */           listener.opened(getSocket());
/*     */         }
/*     */         catch (Exception x)
/*     */         {
/*  89 */           LOG.warn(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onClose()
/*     */   {
/*  98 */     super.onClose();
/*  99 */     if ((this.listeners != null) && (!this.listeners.isEmpty()))
/*     */     {
/* 101 */       for (NetworkTrafficListener listener : this.listeners)
/*     */       {
/*     */         try
/*     */         {
/* 105 */           listener.closed(getSocket());
/*     */         }
/*     */         catch (Exception x)
/*     */         {
/* 109 */           LOG.warn(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyIncoming(ByteBuffer buffer, int read)
/*     */   {
/* 118 */     if ((this.listeners != null) && (!this.listeners.isEmpty()) && (read > 0))
/*     */     {
/* 120 */       for (NetworkTrafficListener listener : this.listeners)
/*     */       {
/*     */         try
/*     */         {
/* 124 */           ByteBuffer view = buffer.asReadOnlyBuffer();
/* 125 */           listener.incoming(getSocket(), view);
/*     */         }
/*     */         catch (Exception x)
/*     */         {
/* 129 */           LOG.warn(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void notifyOutgoing(ByteBuffer view) {
/*     */     Socket socket;
/* 137 */     if ((this.listeners != null) && (!this.listeners.isEmpty()) && (view.hasRemaining()))
/*     */     {
/* 139 */       socket = getSocket();
/* 140 */       for (NetworkTrafficListener listener : this.listeners)
/*     */       {
/*     */         try
/*     */         {
/* 144 */           listener.outgoing(socket, view);
/*     */         }
/*     */         catch (Exception x)
/*     */         {
/* 148 */           LOG.warn(x);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\NetworkTrafficSelectChannelEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */