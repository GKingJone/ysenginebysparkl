/*    */ package com.facebook.presto.jdbc.internal.jetty.alpn.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.alpn.ALPN;
/*    */ import com.facebook.presto.jdbc.internal.jetty.alpn.ALPN.ClientProvider;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.NegotiatingClientConnection;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.util.List;
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
/*    */ public class ALPNClientConnection
/*    */   extends NegotiatingClientConnection
/*    */   implements ALPN.ClientProvider
/*    */ {
/* 36 */   private static final Logger LOG = Log.getLogger(ALPNClientConnection.class);
/*    */   
/*    */   private final List<String> protocols;
/*    */   
/*    */   public ALPNClientConnection(EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, SSLEngine sslEngine, Map<String, Object> context, List<String> protocols)
/*    */   {
/* 42 */     super(endPoint, executor, sslEngine, connectionFactory, context);
/* 43 */     this.protocols = protocols;
/* 44 */     ALPN.put(sslEngine, this);
/*    */   }
/*    */   
/*    */ 
/*    */   public void unsupported()
/*    */   {
/* 50 */     ALPN.remove(getSSLEngine());
/* 51 */     completed();
/*    */   }
/*    */   
/*    */ 
/*    */   public List<String> protocols()
/*    */   {
/* 57 */     return this.protocols;
/*    */   }
/*    */   
/*    */ 
/*    */   public void selected(String protocol)
/*    */   {
/* 63 */     if (this.protocols.contains(protocol))
/*    */     {
/* 65 */       ALPN.remove(getSSLEngine());
/* 66 */       completed();
/*    */     }
/*    */     else
/*    */     {
/* 70 */       LOG.info("Could not negotiate protocol: server [{}] - client {}", new Object[] { protocol, this.protocols });
/* 71 */       close();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */   {
/* 78 */     ALPN.remove(getSSLEngine());
/* 79 */     super.close();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\alpn\client\ALPNClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */