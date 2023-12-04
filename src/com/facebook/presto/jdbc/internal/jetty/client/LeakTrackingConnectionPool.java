/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.LeakDetector;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.LeakDetector.LeakInfo;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*    */ public class LeakTrackingConnectionPool
/*    */   extends ConnectionPool
/*    */ {
/* 30 */   private static final Logger LOG = Log.getLogger(LeakTrackingConnectionPool.class);
/*    */   
/* 32 */   private final LeakDetector<Connection> leakDetector = new LeakDetector()
/*    */   {
/*    */ 
/*    */     protected void leaked(LeakInfo leakInfo)
/*    */     {
/* 37 */       LeakTrackingConnectionPool.this.leaked(leakInfo);
/*    */     }
/*    */   };
/*    */   
/*    */   public LeakTrackingConnectionPool(Destination destination, int maxConnections, Callback requester)
/*    */   {
/* 43 */     super(destination, maxConnections, requester);
/* 44 */     start();
/*    */   }
/*    */   
/*    */   private void start()
/*    */   {
/*    */     try
/*    */     {
/* 51 */       this.leakDetector.start();
/*    */     }
/*    */     catch (Exception x)
/*    */     {
/* 55 */       throw new RuntimeException(x);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */   {
/* 62 */     stop();
/* 63 */     super.close();
/*    */   }
/*    */   
/*    */   private void stop()
/*    */   {
/*    */     try
/*    */     {
/* 70 */       this.leakDetector.stop();
/*    */     }
/*    */     catch (Exception x)
/*    */     {
/* 74 */       throw new RuntimeException(x);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   protected void acquired(Connection connection)
/*    */   {
/* 81 */     if (!this.leakDetector.acquired(connection)) {
/* 82 */       LOG.info("Connection {}@{} not tracked", new Object[] { connection, this.leakDetector.id(connection) });
/*    */     }
/*    */   }
/*    */   
/*    */   protected void released(Connection connection)
/*    */   {
/* 88 */     if (!this.leakDetector.released(connection)) {
/* 89 */       LOG.info("Connection {}@{} released but not acquired", new Object[] { connection, this.leakDetector.id(connection) });
/*    */     }
/*    */   }
/*    */   
/*    */   protected void leaked(LeakInfo leakInfo) {
/* 94 */     LOG.info("Connection " + leakInfo.getResourceDescription() + " leaked at:", leakInfo.getStackFrames());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\LeakTrackingConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */