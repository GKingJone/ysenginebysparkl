/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
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
/*    */ @Deprecated
/*    */ public class ConnectionPool
/*    */   extends DuplexConnectionPool
/*    */ {
/*    */   public ConnectionPool(Destination destination, int maxConnections, Callback requester)
/*    */   {
/* 32 */     super(destination, maxConnections, requester);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */