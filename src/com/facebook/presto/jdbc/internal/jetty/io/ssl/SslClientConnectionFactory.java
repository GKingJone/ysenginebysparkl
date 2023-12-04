/*    */ package com.facebook.presto.jdbc.internal.jetty.io.ssl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.Connection;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.ssl.SslContextFactory;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.Executor;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SslClientConnectionFactory
/*    */   implements ClientConnectionFactory
/*    */ {
/*    */   public static final String SSL_CONTEXT_FACTORY_CONTEXT_KEY = "ssl.context.factory";
/*    */   public static final String SSL_PEER_HOST_CONTEXT_KEY = "ssl.peer.host";
/*    */   public static final String SSL_PEER_PORT_CONTEXT_KEY = "ssl.peer.port";
/*    */   public static final String SSL_ENGINE_CONTEXT_KEY = "ssl.engine";
/*    */   private final SslContextFactory sslContextFactory;
/*    */   private final ByteBufferPool byteBufferPool;
/*    */   private final Executor executor;
/*    */   private final ClientConnectionFactory connectionFactory;
/*    */   
/*    */   public SslClientConnectionFactory(SslContextFactory sslContextFactory, ByteBufferPool byteBufferPool, Executor executor, ClientConnectionFactory connectionFactory)
/*    */   {
/* 46 */     this.sslContextFactory = sslContextFactory;
/* 47 */     this.byteBufferPool = byteBufferPool;
/* 48 */     this.executor = executor;
/* 49 */     this.connectionFactory = connectionFactory;
/*    */   }
/*    */   
/*    */   public Connection newConnection(EndPoint endPoint, Map<String, Object> context)
/*    */     throws IOException
/*    */   {
/* 55 */     String host = (String)context.get("ssl.peer.host");
/* 56 */     int port = ((Integer)context.get("ssl.peer.port")).intValue();
/* 57 */     SSLEngine engine = this.sslContextFactory.newSSLEngine(host, port);
/* 58 */     engine.setUseClientMode(true);
/* 59 */     context.put("ssl.engine", engine);
/*    */     
/* 61 */     SslConnection sslConnection = newSslConnection(this.byteBufferPool, this.executor, endPoint, engine);
/* 62 */     sslConnection.setRenegotiationAllowed(this.sslContextFactory.isRenegotiationAllowed());
/* 63 */     endPoint.setConnection(sslConnection);
/* 64 */     EndPoint appEndPoint = sslConnection.getDecryptedEndPoint();
/* 65 */     appEndPoint.setConnection(this.connectionFactory.newConnection(appEndPoint, context));
/*    */     
/* 67 */     return sslConnection;
/*    */   }
/*    */   
/*    */   protected SslConnection newSslConnection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, SSLEngine engine)
/*    */   {
/* 72 */     return new SslConnection(byteBufferPool, executor, endPoint, engine);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ssl\SslClientConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */