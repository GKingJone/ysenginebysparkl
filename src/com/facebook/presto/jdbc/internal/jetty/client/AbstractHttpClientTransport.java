/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ManagedSelector;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.SelectChannelEndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.SelectorManager;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.SocketException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Map;
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
/*     */ @ManagedObject
/*     */ public abstract class AbstractHttpClientTransport
/*     */   extends ContainerLifeCycle
/*     */   implements HttpClientTransport
/*     */ {
/*  45 */   protected static final Logger LOG = Log.getLogger(HttpClientTransport.class);
/*     */   
/*     */   private final int selectors;
/*     */   private volatile HttpClient client;
/*     */   private volatile SelectorManager selectorManager;
/*     */   
/*     */   protected AbstractHttpClientTransport(int selectors)
/*     */   {
/*  53 */     this.selectors = selectors;
/*     */   }
/*     */   
/*     */   protected HttpClient getHttpClient()
/*     */   {
/*  58 */     return this.client;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setHttpClient(HttpClient client)
/*     */   {
/*  64 */     this.client = client;
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of selectors", readonly=true)
/*     */   public int getSelectors()
/*     */   {
/*  70 */     return this.selectors;
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  76 */     this.selectorManager = newSelectorManager(this.client);
/*  77 */     this.selectorManager.setConnectTimeout(this.client.getConnectTimeout());
/*  78 */     addBean(this.selectorManager);
/*  79 */     super.doStart();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/*  85 */     super.doStop();
/*  86 */     removeBean(this.selectorManager);
/*     */   }
/*     */   
/*     */ 
/*     */   public void connect(InetSocketAddress address, Map<String, Object> context)
/*     */   {
/*  92 */     SocketChannel channel = null;
/*     */     try
/*     */     {
/*  95 */       channel = SocketChannel.open();
/*  96 */       HttpDestination destination = (HttpDestination)context.get("http.destination");
/*  97 */       HttpClient client = destination.getHttpClient();
/*  98 */       SocketAddress bindAddress = client.getBindAddress();
/*  99 */       if (bindAddress != null)
/* 100 */         channel.bind(bindAddress);
/* 101 */       configure(client, channel);
/*     */       
/* 103 */       context.put("ssl.peer.host", destination.getHost());
/* 104 */       context.put("ssl.peer.port", Integer.valueOf(destination.getPort()));
/*     */       
/* 106 */       if (client.isConnectBlocking())
/*     */       {
/* 108 */         channel.socket().connect(address, (int)client.getConnectTimeout());
/* 109 */         channel.configureBlocking(false);
/* 110 */         this.selectorManager.accept(channel, context);
/*     */       }
/*     */       else
/*     */       {
/* 114 */         channel.configureBlocking(false);
/* 115 */         if (channel.connect(address)) {
/* 116 */           this.selectorManager.accept(channel, context);
/*     */         } else {
/* 118 */           this.selectorManager.connect(channel, context);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 127 */       if (x.getClass() == SocketException.class) {
/* 128 */         x = new SocketException("Could not connect to " + address).initCause(x);
/*     */       }
/*     */       try
/*     */       {
/* 132 */         if (channel != null) {
/* 133 */           channel.close();
/*     */         }
/*     */       }
/*     */       catch (IOException xx) {
/* 137 */         LOG.ignore(xx);
/*     */       }
/*     */       finally
/*     */       {
/* 141 */         connectFailed(context, x);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void connectFailed(Map<String, Object> context, Throwable x)
/*     */   {
/* 148 */     if (LOG.isDebugEnabled()) {
/* 149 */       LOG.debug("Could not connect to {}", new Object[] { context.get("http.destination") });
/*     */     }
/* 151 */     Promise<com.facebook.presto.jdbc.internal.jetty.client.api.Connection> promise = (Promise)context.get("http.connection.promise");
/* 152 */     promise.failed(x);
/*     */   }
/*     */   
/*     */   protected void configure(HttpClient client, SocketChannel channel) throws IOException
/*     */   {
/* 157 */     channel.socket().setTcpNoDelay(client.isTCPNoDelay());
/*     */   }
/*     */   
/*     */   protected SelectorManager newSelectorManager(HttpClient client)
/*     */   {
/* 162 */     return new ClientSelectorManager(client, this.selectors);
/*     */   }
/*     */   
/*     */   protected class ClientSelectorManager extends SelectorManager
/*     */   {
/*     */     private final HttpClient client;
/*     */     
/*     */     protected ClientSelectorManager(HttpClient client, int selectors)
/*     */     {
/* 171 */       super(client.getScheduler(), selectors);
/* 172 */       this.client = client;
/*     */     }
/*     */     
/*     */ 
/*     */     protected EndPoint newEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key)
/*     */     {
/* 178 */       return new SelectChannelEndPoint(channel, selector, key, getScheduler(), this.client.getIdleTimeout());
/*     */     }
/*     */     
/*     */ 
/*     */     public com.facebook.presto.jdbc.internal.jetty.io.Connection newConnection(SocketChannel channel, EndPoint endPoint, Object attachment)
/*     */       throws IOException
/*     */     {
/* 185 */       Map<String, Object> context = (Map)attachment;
/* 186 */       HttpDestination destination = (HttpDestination)context.get("http.destination");
/* 187 */       return destination.getClientConnectionFactory().newConnection(endPoint, context);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void connectionFailed(SocketChannel channel, Throwable x, Object attachment)
/*     */     {
/* 194 */       Map<String, Object> context = (Map)attachment;
/* 195 */       AbstractHttpClientTransport.this.connectFailed(context, x);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\AbstractHttpClientTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */