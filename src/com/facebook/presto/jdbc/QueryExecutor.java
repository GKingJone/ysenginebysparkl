/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpClientConfig;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpUriBuilder;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.JsonResponseHandler;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request.Builder;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyHttpClient;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyIoPool;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.jetty.JettyIoPoolConfig;
/*     */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.client.ClientSession;
/*     */ import com.facebook.presto.jdbc.internal.client.QueryResults;
/*     */ import com.facebook.presto.jdbc.internal.client.ServerInfo;
/*     */ import com.facebook.presto.jdbc.internal.client.StatementClient;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.HostAndPort;
/*     */ import java.io.Closeable;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.URI;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
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
/*     */ class QueryExecutor
/*     */   implements Closeable
/*     */ {
/*     */   private final JsonCodec<QueryResults> queryInfoCodec;
/*     */   private final JsonCodec<ServerInfo> serverInfoCodec;
/*     */   private final HttpClient httpClient;
/*     */   
/*     */   private QueryExecutor(JsonCodec<QueryResults> queryResultsCodec, JsonCodec<ServerInfo> serverInfoCodec, HttpClient httpClient)
/*     */   {
/*  55 */     this.queryInfoCodec = ((JsonCodec)Objects.requireNonNull(queryResultsCodec, "queryResultsCodec is null"));
/*  56 */     this.serverInfoCodec = ((JsonCodec)Objects.requireNonNull(serverInfoCodec, "serverInfoCodec is null"));
/*  57 */     this.httpClient = ((HttpClient)Objects.requireNonNull(httpClient, "httpClient is null"));
/*     */   }
/*     */   
/*     */   public StatementClient startQuery(ClientSession session, String query)
/*     */   {
/*  62 */     return new StatementClient(this.httpClient, this.queryInfoCodec, session, query);
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/*  68 */     this.httpClient.close();
/*     */   }
/*     */   
/*     */   public ServerInfo getServerInfo(URI server)
/*     */   {
/*  73 */     URI uri = HttpUriBuilder.uriBuilderFrom(server).replacePath("/v1/info").build();
/*  74 */     Request request = Builder.prepareGet().setUri(uri).build();
/*  75 */     return (ServerInfo)this.httpClient.execute(request, JsonResponseHandler.createJsonResponseHandler(this.serverInfoCodec));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*  83 */     close();
/*     */   }
/*     */   
/*     */   static QueryExecutor create(String userAgent)
/*     */   {
/*  88 */     return create(new JettyHttpClient(new HttpClientConfig()
/*     */     
/*  90 */       .setConnectTimeout(new Duration(10.0D, TimeUnit.SECONDS))
/*  91 */       .setSocksProxy(getSystemSocksProxy()), new JettyIoPool("presto-jdbc", new JettyIoPoolConfig()), 
/*     */       
/*  93 */       ImmutableSet.of(new UserAgentRequestFilter(userAgent))));
/*     */   }
/*     */   
/*     */   static QueryExecutor create(HttpClient httpClient)
/*     */   {
/*  98 */     return new QueryExecutor(JsonCodec.jsonCodec(QueryResults.class), JsonCodec.jsonCodec(ServerInfo.class), httpClient);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static HostAndPort getSystemSocksProxy()
/*     */   {
/* 104 */     URI uri = URI.create("socket://0.0.0.0:80");
/* 105 */     for (Proxy proxy : ProxySelector.getDefault().select(uri)) {
/* 106 */       if ((proxy.type() == Type.SOCKS) &&
/* 107 */         ((proxy.address() instanceof InetSocketAddress))) {
/* 108 */         InetSocketAddress address = (InetSocketAddress)proxy.address();
/* 109 */         return HostAndPort.fromParts(address.getHostString(), address.getPort());
/*     */       }
/*     */     }
/*     */     
/* 113 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\QueryExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */