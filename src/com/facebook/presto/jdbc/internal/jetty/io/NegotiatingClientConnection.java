/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.net.ssl.SSLEngine;
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
/*     */ public abstract class NegotiatingClientConnection
/*     */   extends AbstractConnection
/*     */ {
/*  33 */   private static final Logger LOG = Log.getLogger(NegotiatingClientConnection.class);
/*     */   
/*     */   private final SSLEngine engine;
/*     */   private final ClientConnectionFactory connectionFactory;
/*     */   private final Map<String, Object> context;
/*     */   private volatile boolean completed;
/*     */   
/*     */   protected NegotiatingClientConnection(EndPoint endp, Executor executor, SSLEngine sslEngine, ClientConnectionFactory connectionFactory, Map<String, Object> context)
/*     */   {
/*  42 */     super(endp, executor);
/*  43 */     this.engine = sslEngine;
/*  44 */     this.connectionFactory = connectionFactory;
/*  45 */     this.context = context;
/*     */   }
/*     */   
/*     */   protected SSLEngine getSSLEngine()
/*     */   {
/*  50 */     return this.engine;
/*     */   }
/*     */   
/*     */   protected void completed()
/*     */   {
/*  55 */     this.completed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/*  61 */     super.onOpen();
/*     */     try
/*     */     {
/*  64 */       getEndPoint().flush(new ByteBuffer[] { BufferUtil.EMPTY_BUFFER });
/*  65 */       if (this.completed) {
/*  66 */         replaceConnection();
/*     */       } else {
/*  68 */         fillInterested();
/*     */       }
/*     */     }
/*     */     catch (IOException x) {
/*  72 */       close();
/*  73 */       throw new RuntimeIOException(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onFillable()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  82 */       int filled = fill();
/*  83 */       if ((filled == 0) && (!this.completed))
/*  84 */         fillInterested();
/*  85 */       if ((filled <= 0) || (this.completed))
/*     */         break;
/*     */     }
/*  88 */     if (this.completed) {
/*  89 */       replaceConnection();
/*     */     }
/*     */   }
/*     */   
/*     */   private int fill()
/*     */   {
/*     */     try {
/*  96 */       return getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
/*     */     }
/*     */     catch (IOException x)
/*     */     {
/* 100 */       LOG.debug(x);
/* 101 */       close(); }
/* 102 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   private void replaceConnection()
/*     */   {
/* 108 */     EndPoint endPoint = getEndPoint();
/*     */     try
/*     */     {
/* 111 */       endPoint.upgrade(this.connectionFactory.newConnection(endPoint, this.context));
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 115 */       LOG.debug(x);
/* 116 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 124 */     getEndPoint().shutdownOutput();
/* 125 */     super.close();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\NegotiatingClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */