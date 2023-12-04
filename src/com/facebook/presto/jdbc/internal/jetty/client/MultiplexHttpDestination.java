/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public abstract class MultiplexHttpDestination<C extends Connection>
/*     */   extends HttpDestination
/*     */   implements Promise<Connection>
/*     */ {
/*  30 */   private final AtomicReference<ConnectState> connect = new AtomicReference(ConnectState.DISCONNECTED);
/*  31 */   private final AtomicInteger requestsPerConnection = new AtomicInteger();
/*  32 */   private int maxRequestsPerConnection = 1024;
/*     */   private C connection;
/*     */   
/*     */   protected MultiplexHttpDestination(HttpClient client, Origin origin)
/*     */   {
/*  37 */     super(client, origin);
/*     */   }
/*     */   
/*     */   public int getMaxRequestsPerConnection()
/*     */   {
/*  42 */     return this.maxRequestsPerConnection;
/*     */   }
/*     */   
/*     */   public void setMaxRequestsPerConnection(int maxRequestsPerConnection)
/*     */   {
/*  47 */     this.maxRequestsPerConnection = maxRequestsPerConnection;
/*     */   }
/*     */   
/*     */ 
/*     */   public void send()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  55 */       ConnectState current = (ConnectState)this.connect.get();
/*  56 */       switch (current)
/*     */       {
/*     */ 
/*     */       case DISCONNECTED: 
/*  60 */         if (this.connect.compareAndSet(current, ConnectState.CONNECTING))
/*     */         {
/*  62 */           newConnection(this);
/*  63 */           return;
/*     */         }
/*     */         
/*     */         break;
/*     */       case CONNECTING: 
/*  68 */         return;
/*     */       
/*     */ 
/*     */       case CONNECTED: 
/*  72 */         if (!process(this.connection))
/*     */         {
/*  74 */           return;
/*     */         }
/*     */         break;
/*     */       default: 
/*  78 */         abort(new IllegalStateException("Invalid connection state " + current));
/*  79 */         return;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void succeeded(Connection result)
/*     */   {
/*  89 */     C connection = this.connection = result;
/*  90 */     if (this.connect.compareAndSet(ConnectState.CONNECTING, ConnectState.CONNECTED))
/*     */     {
/*  92 */       send();
/*     */     }
/*     */     else
/*     */     {
/*  96 */       connection.close();
/*  97 */       failed(new IllegalStateException("Invalid connection state " + this.connect));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable x)
/*     */   {
/* 104 */     this.connect.set(ConnectState.DISCONNECTED);
/* 105 */     abort(x);
/*     */   }
/*     */   
/*     */   protected boolean process(C connection)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 112 */       int max = getMaxRequestsPerConnection();
/* 113 */       int count = this.requestsPerConnection.get();
/* 114 */       int next = count + 1;
/* 115 */       if (next > max) {
/* 116 */         return false;
/*     */       }
/* 118 */       if (this.requestsPerConnection.compareAndSet(count, next))
/*     */       {
/* 120 */         HttpExchange exchange = (HttpExchange)getHttpExchanges().poll();
/* 121 */         if (LOG.isDebugEnabled())
/* 122 */           LOG.debug("Processing {}/{} {} on {}", new Object[] { Integer.valueOf(next), Integer.valueOf(max), exchange, connection });
/* 123 */         if (exchange == null)
/*     */         {
/* 125 */           this.requestsPerConnection.decrementAndGet();
/* 126 */           return false;
/*     */         }
/*     */         
/* 129 */         Request request = exchange.getRequest();
/* 130 */         Throwable cause = request.getAbortCause();
/* 131 */         if (cause != null)
/*     */         {
/* 133 */           if (LOG.isDebugEnabled())
/* 134 */             LOG.debug("Aborted before processing {}: {}", new Object[] { exchange, cause });
/* 135 */           this.requestsPerConnection.decrementAndGet();
/*     */           
/*     */ 
/*     */ 
/* 139 */           exchange.abort(cause);
/*     */         }
/*     */         else
/*     */         {
/* 143 */           SendFailure result = send(connection, exchange);
/* 144 */           if (result != null)
/*     */           {
/* 146 */             if (LOG.isDebugEnabled())
/* 147 */               LOG.debug("Send failed {} for {}", new Object[] { result, exchange });
/* 148 */             this.requestsPerConnection.decrementAndGet();
/* 149 */             if (result.retry)
/*     */             {
/* 151 */               if (enqueue(getHttpExchanges(), exchange))
/* 152 */                 return true;
/*     */             }
/* 154 */             request.abort(result.failure);
/*     */           }
/*     */         }
/* 157 */         return getHttpExchanges().peek() != null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void release(Connection connection)
/*     */   {
/* 165 */     this.requestsPerConnection.decrementAndGet();
/* 166 */     send();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 172 */     super.close();
/* 173 */     C connection = this.connection;
/* 174 */     if (connection != null) {
/* 175 */       connection.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void close(Connection connection)
/*     */   {
/* 181 */     super.close(connection);
/*     */     for (;;)
/*     */     {
/* 184 */       ConnectState current = (ConnectState)this.connect.get();
/* 185 */       if (this.connect.compareAndSet(current, ConnectState.DISCONNECTED))
/*     */       {
/* 187 */         if (!getHttpClient().isRemoveIdleDestinations()) break;
/* 188 */         getHttpClient().removeDestination(this); break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected abstract SendFailure send(C paramC, HttpExchange paramHttpExchange);
/*     */   
/*     */   private static enum ConnectState
/*     */   {
/* 198 */     DISCONNECTED,  CONNECTING,  CONNECTED;
/*     */     
/*     */     private ConnectState() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\MultiplexHttpDestination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */