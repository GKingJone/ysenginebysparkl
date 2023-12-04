/*    */ package com.facebook.presto.jdbc.internal.jetty.io;
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
/*    */ public abstract class NegotiatingClientConnectionFactory
/*    */   implements ClientConnectionFactory
/*    */ {
/*    */   private final ClientConnectionFactory connectionFactory;
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
/*    */   protected NegotiatingClientConnectionFactory(ClientConnectionFactory connectionFactory)
/*    */   {
/* 28 */     this.connectionFactory = connectionFactory;
/*    */   }
/*    */   
/*    */   public ClientConnectionFactory getClientConnectionFactory()
/*    */   {
/* 33 */     return this.connectionFactory;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\NegotiatingClientConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */