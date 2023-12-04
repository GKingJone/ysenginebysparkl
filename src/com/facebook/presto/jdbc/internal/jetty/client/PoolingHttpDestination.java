/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Sweeper;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ @ManagedObject
/*     */ public abstract class PoolingHttpDestination<C extends Connection>
/*     */   extends HttpDestination
/*     */   implements Callback
/*     */ {
/*     */   private DuplexConnectionPool connectionPool;
/*     */   
/*     */   public PoolingHttpDestination(HttpClient client, Origin origin)
/*     */   {
/*  39 */     super(client, origin);
/*  40 */     this.connectionPool = newConnectionPool(client);
/*  41 */     addBean(this.connectionPool);
/*  42 */     Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
/*  43 */     if (sweeper != null) {
/*  44 */       sweeper.offer(this.connectionPool);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doStart() throws Exception
/*     */   {
/*  50 */     HttpClient client = getHttpClient();
/*  51 */     this.connectionPool = newConnectionPool(client);
/*  52 */     addBean(this.connectionPool);
/*  53 */     super.doStart();
/*  54 */     Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
/*  55 */     if (sweeper != null) {
/*  56 */       sweeper.offer(this.connectionPool);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doStop() throws Exception
/*     */   {
/*  62 */     HttpClient client = getHttpClient();
/*  63 */     Sweeper sweeper = (Sweeper)client.getBean(Sweeper.class);
/*  64 */     if (sweeper != null)
/*  65 */       sweeper.remove(this.connectionPool);
/*  66 */     super.doStop();
/*  67 */     removeBean(this.connectionPool);
/*     */   }
/*     */   
/*     */   protected DuplexConnectionPool newConnectionPool(HttpClient client)
/*     */   {
/*  72 */     return new DuplexConnectionPool(this, client.getMaxConnectionsPerDestination(), this);
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The connection pool", readonly=true)
/*     */   public DuplexConnectionPool getConnectionPool()
/*     */   {
/*  78 */     return this.connectionPool;
/*     */   }
/*     */   
/*     */ 
/*     */   public void succeeded()
/*     */   {
/*  84 */     send();
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable x)
/*     */   {
/*  90 */     abort(x);
/*     */   }
/*     */   
/*     */   public void send()
/*     */   {
/*  95 */     if (getHttpExchanges().isEmpty())
/*  96 */       return;
/*  97 */     process();
/*     */   }
/*     */   
/*     */ 
/*     */   public C acquire()
/*     */   {
/* 103 */     return this.connectionPool.acquire();
/*     */   }
/*     */   
/*     */   private void process()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 110 */       C connection = acquire();
/* 111 */       if (connection == null)
/*     */         break;
/* 113 */       boolean proceed = process(connection);
/* 114 */       if (!proceed) {
/*     */         break;
/*     */       }
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
/*     */   public boolean process(C connection)
/*     */   {
/* 131 */     HttpClient client = getHttpClient();
/* 132 */     HttpExchange exchange = (HttpExchange)getHttpExchanges().poll();
/* 133 */     if (LOG.isDebugEnabled())
/* 134 */       LOG.debug("Processing exchange {} on {} of {}", new Object[] { exchange, connection, this });
/* 135 */     if (exchange == null)
/*     */     {
/* 137 */       if (!this.connectionPool.release(connection))
/* 138 */         connection.close();
/* 139 */       if (!client.isRunning())
/*     */       {
/* 141 */         if (LOG.isDebugEnabled())
/* 142 */           LOG.debug("{} is stopping", new Object[] { client });
/* 143 */         connection.close();
/*     */       }
/* 145 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 149 */     Request request = exchange.getRequest();
/* 150 */     Throwable cause = request.getAbortCause();
/* 151 */     if (cause != null)
/*     */     {
/* 153 */       if (LOG.isDebugEnabled()) {
/* 154 */         LOG.debug("Aborted before processing {}: {}", new Object[] { exchange, cause });
/*     */       }
/* 156 */       if (!this.connectionPool.release(connection)) {
/* 157 */         connection.close();
/*     */       }
/*     */       
/*     */ 
/* 161 */       exchange.abort(cause);
/*     */     }
/*     */     else
/*     */     {
/* 165 */       SendFailure result = send(connection, exchange);
/* 166 */       if (result != null)
/*     */       {
/* 168 */         if (LOG.isDebugEnabled())
/* 169 */           LOG.debug("Send failed {} for {}", new Object[] { result, exchange });
/* 170 */         if (result.retry)
/*     */         {
/* 172 */           if (enqueue(getHttpExchanges(), exchange)) {
/* 173 */             return true;
/*     */           }
/*     */         }
/* 176 */         request.abort(result.failure);
/*     */       }
/*     */     }
/* 179 */     return getHttpExchanges().peek() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract SendFailure send(C paramC, HttpExchange paramHttpExchange);
/*     */   
/*     */ 
/*     */   public void release(Connection c)
/*     */   {
/* 189 */     C connection = c;
/* 190 */     if (LOG.isDebugEnabled())
/* 191 */       LOG.debug("Released {}", new Object[] { connection });
/* 192 */     HttpClient client = getHttpClient();
/* 193 */     if (client.isRunning())
/*     */     {
/* 195 */       if (this.connectionPool.isActive(connection))
/*     */       {
/* 197 */         if (this.connectionPool.release(connection)) {
/* 198 */           send();
/*     */         } else {
/* 200 */           connection.close();
/*     */         }
/*     */         
/*     */       }
/* 204 */       else if (LOG.isDebugEnabled()) {
/* 205 */         LOG.debug("Released explicit {}", new Object[] { connection });
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 210 */       if (LOG.isDebugEnabled())
/* 211 */         LOG.debug("{} is stopped", new Object[] { client });
/* 212 */       connection.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void close(Connection connection)
/*     */   {
/* 219 */     super.close(connection);
/*     */     
/* 221 */     boolean removed = this.connectionPool.remove(connection);
/*     */     
/* 223 */     if (getHttpExchanges().isEmpty())
/*     */     {
/* 225 */       if ((getHttpClient().isRemoveIdleDestinations()) && (this.connectionPool.isEmpty()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 233 */         getHttpClient().removeDestination(this);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/* 241 */     else if (removed) {
/* 242 */       process();
/*     */     }
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/* 248 */     super.close();
/* 249 */     this.connectionPool.close();
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 255 */     super.dump(out, indent);
/* 256 */     ContainerLifeCycle.dump(out, indent, new Collection[] { Collections.singletonList(this.connectionPool) });
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 262 */     return String.format("%s,pool=%s", new Object[] { super.toString(), this.connectionPool });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\PoolingHttpDestination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */