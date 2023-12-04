/*    */ package com.facebook.presto.jdbc.internal.jetty.alpn.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.Connection;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.NegotiatingClientConnectionFactory;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ALPNClientConnectionFactory
/*    */   extends NegotiatingClientConnectionFactory
/*    */ {
/*    */   private final Executor executor;
/*    */   private final List<String> protocols;
/*    */   
/*    */   public ALPNClientConnectionFactory(Executor executor, ClientConnectionFactory connectionFactory, List<String> protocols)
/*    */   {
/* 41 */     super(connectionFactory);
/* 42 */     this.executor = executor;
/* 43 */     this.protocols = protocols;
/* 44 */     if (protocols.isEmpty()) {
/* 45 */       throw new IllegalArgumentException("ALPN protocol list cannot be empty");
/*    */     }
/*    */   }
/*    */   
/*    */   public Connection newConnection(EndPoint endPoint, Map<String, Object> context)
/*    */     throws IOException
/*    */   {
/* 52 */     return new ALPNClientConnection(endPoint, this.executor, getClientConnectionFactory(), (SSLEngine)context.get("ssl.engine"), context, this.protocols);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\alpn\client\ALPNClientConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */