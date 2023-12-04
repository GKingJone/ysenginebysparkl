/*    */ package com.mysql.jdbc;
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
/*    */ public class ConnectionFeatureNotAvailableException
/*    */   extends CommunicationsException
/*    */ {
/*    */   static final long serialVersionUID = -5065030488729238287L;
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
/*    */   public ConnectionFeatureNotAvailableException(MySQLConnection conn, long lastPacketSentTimeMs, Exception underlyingException)
/*    */   {
/* 40 */     super(conn, lastPacketSentTimeMs, 0L, underlyingException);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 50 */     return "Feature not available in this distribution of Connector/J";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 60 */     return "01S00";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionFeatureNotAvailableException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */