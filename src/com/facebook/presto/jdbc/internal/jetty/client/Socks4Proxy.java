/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.AbstractConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ssl.SslClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class Socks4Proxy
/*     */   extends ProxyConfiguration.Proxy
/*     */ {
/*     */   public Socks4Proxy(String host, int port)
/*     */   {
/*  45 */     this(new Origin.Address(host, port), false);
/*     */   }
/*     */   
/*     */   public Socks4Proxy(Origin.Address address, boolean secure)
/*     */   {
/*  50 */     super(address, secure);
/*     */   }
/*     */   
/*     */ 
/*     */   public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory)
/*     */   {
/*  56 */     return new Socks4ProxyClientConnectionFactory(connectionFactory);
/*     */   }
/*     */   
/*     */   public static class Socks4ProxyClientConnectionFactory implements ClientConnectionFactory
/*     */   {
/*     */     private final ClientConnectionFactory connectionFactory;
/*     */     
/*     */     public Socks4ProxyClientConnectionFactory(ClientConnectionFactory connectionFactory)
/*     */     {
/*  65 */       this.connectionFactory = connectionFactory;
/*     */     }
/*     */     
/*     */     public com.facebook.presto.jdbc.internal.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context)
/*     */       throws IOException
/*     */     {
/*  71 */       HttpDestination destination = (HttpDestination)context.get("http.destination");
/*  72 */       Executor executor = destination.getHttpClient().getExecutor();
/*  73 */       return new Socks4ProxyConnection(endPoint, executor, this.connectionFactory, context);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Socks4ProxyConnection extends AbstractConnection implements Callback
/*     */   {
/*  79 */     private static final Pattern IPv4_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
/*  80 */     private static final Logger LOG = Log.getLogger(Socks4ProxyConnection.class);
/*     */     
/*  82 */     private final Socks4Parser parser = new Socks4Parser(null);
/*     */     private final ClientConnectionFactory connectionFactory;
/*     */     private final Map<String, Object> context;
/*     */     
/*     */     public Socks4ProxyConnection(EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, Map<String, Object> context)
/*     */     {
/*  88 */       super(executor);
/*  89 */       this.connectionFactory = connectionFactory;
/*  90 */       this.context = context;
/*     */     }
/*     */     
/*     */ 
/*     */     public void onOpen()
/*     */     {
/*  96 */       super.onOpen();
/*  97 */       writeSocks4Connect();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void writeSocks4Connect()
/*     */     {
/* 106 */       HttpDestination destination = (HttpDestination)this.context.get("http.destination");
/* 107 */       String host = destination.getHost();
/* 108 */       short port = (short)destination.getPort();
/* 109 */       Matcher matcher = IPv4_PATTERN.matcher(host);
/* 110 */       if (matcher.matches())
/*     */       {
/*     */ 
/* 113 */         ByteBuffer buffer = ByteBuffer.allocate(9);
/* 114 */         buffer.put((byte)4).put((byte)1).putShort(port);
/* 115 */         for (int i = 1; i <= 4; i++)
/* 116 */           buffer.put((byte)Integer.parseInt(matcher.group(i)));
/* 117 */         buffer.put((byte)0);
/* 118 */         buffer.flip();
/* 119 */         getEndPoint().write(this, new ByteBuffer[] { buffer });
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 124 */         byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
/* 125 */         ByteBuffer buffer = ByteBuffer.allocate(9 + hostBytes.length + 1);
/* 126 */         buffer.put((byte)4).put((byte)1).putShort(port);
/* 127 */         buffer.put((byte)0).put((byte)0).put((byte)0).put((byte)1).put((byte)0);
/* 128 */         buffer.put(hostBytes).put((byte)0);
/* 129 */         buffer.flip();
/* 130 */         getEndPoint().write(this, new ByteBuffer[] { buffer });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 137 */       if (LOG.isDebugEnabled())
/* 138 */         LOG.debug("Written SOCKS4 connect request", new Object[0]);
/* 139 */       fillInterested();
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/* 145 */       close();
/*     */       
/* 147 */       Promise<com.facebook.presto.jdbc.internal.jetty.client.api.Connection> promise = (Promise)this.context.get("http.connection.promise");
/* 148 */       promise.failed(x);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void onFillable()
/*     */     {
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/* 160 */           ByteBuffer buffer = BufferUtil.allocate(this.parser.expected());
/* 161 */           int filled = getEndPoint().fill(buffer);
/* 162 */           if (LOG.isDebugEnabled()) {
/* 163 */             LOG.debug("Read SOCKS4 connect response, {} bytes", filled);
/*     */           }
/* 165 */           if (filled < 0) {
/* 166 */             throw new IOException("SOCKS4 tunnel failed, connection closed");
/*     */           }
/* 168 */           if (filled == 0)
/*     */           {
/* 170 */             fillInterested();
/* 171 */             return;
/*     */           }
/*     */           
/* 174 */           if (this.parser.parse(buffer)) {
/* 175 */             return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 182 */         return;
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 180 */         failed(x);
/*     */       }
/*     */     }
/*     */     
/*     */     private void onSocks4Response(int responseCode) throws IOException
/*     */     {
/* 186 */       if (responseCode == 90) {
/* 187 */         tunnel();
/*     */       } else {
/* 189 */         throw new IOException("SOCKS4 tunnel failed with code " + responseCode);
/*     */       }
/*     */     }
/*     */     
/*     */     private void tunnel()
/*     */     {
/*     */       try {
/* 196 */         HttpDestination destination = (HttpDestination)this.context.get("http.destination");
/* 197 */         HttpClient client = destination.getHttpClient();
/* 198 */         ClientConnectionFactory connectionFactory = this.connectionFactory;
/* 199 */         if (HttpScheme.HTTPS.is(destination.getScheme()))
/* 200 */           connectionFactory = new SslClientConnectionFactory(client.getSslContextFactory(), client.getByteBufferPool(), client.getExecutor(), connectionFactory);
/* 201 */         com.facebook.presto.jdbc.internal.jetty.io.Connection newConnection = connectionFactory.newConnection(getEndPoint(), this.context);
/* 202 */         getEndPoint().upgrade(newConnection);
/* 203 */         if (LOG.isDebugEnabled()) {
/* 204 */           LOG.debug("SOCKS4 tunnel established: {} over {}", new Object[] { this, newConnection });
/*     */         }
/*     */       }
/*     */       catch (Throwable x) {
/* 208 */         failed(x);
/*     */       }
/*     */     }
/*     */     
/*     */     private class Socks4Parser {
/*     */       private static final int EXPECTED_LENGTH = 8;
/*     */       private int cursor;
/*     */       private int response;
/*     */       
/*     */       private Socks4Parser() {}
/*     */       
/*     */       private boolean parse(ByteBuffer buffer) throws IOException {
/* 220 */         while (buffer.hasRemaining())
/*     */         {
/* 222 */           byte current = buffer.get();
/* 223 */           if (this.cursor == 1)
/* 224 */             this.response = (current & 0xFF);
/* 225 */           this.cursor += 1;
/* 226 */           if (this.cursor == 8)
/*     */           {
/* 228 */             Socks4ProxyConnection.this.onSocks4Response(this.response);
/* 229 */             return true;
/*     */           }
/*     */         }
/* 232 */         return false;
/*     */       }
/*     */       
/*     */       private int expected()
/*     */       {
/* 237 */         return 8 - this.cursor;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\Socks4Proxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */