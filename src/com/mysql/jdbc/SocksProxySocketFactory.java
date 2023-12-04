/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Proxy;
/*    */ import java.net.Proxy.Type;
/*    */ import java.net.Socket;
/*    */ import java.util.Properties;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SocksProxySocketFactory
/*    */   extends StandardSocketFactory
/*    */ {
/* 35 */   public static int SOCKS_DEFAULT_PORT = 1080;
/*    */   
/*    */   protected Socket createSocket(Properties props)
/*    */   {
/* 39 */     String socksProxyHost = props.getProperty("socksProxyHost");
/* 40 */     String socksProxyPortString = props.getProperty("socksProxyPort", String.valueOf(SOCKS_DEFAULT_PORT));
/* 41 */     int socksProxyPort = SOCKS_DEFAULT_PORT;
/*    */     try {
/* 43 */       socksProxyPort = Integer.valueOf(socksProxyPortString).intValue();
/*    */     }
/*    */     catch (NumberFormatException ex) {}
/*    */     
/*    */ 
/* 48 */     return new Socket(new Proxy(Type.SOCKS, new InetSocketAddress(socksProxyHost, socksProxyPort)));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\SocksProxySocketFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */