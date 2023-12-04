/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CommunicationsException
/*    */   extends SQLException
/*    */   implements StreamingNotifiable
/*    */ {
/*    */   static final long serialVersionUID = 3193864990663398317L;
/* 37 */   private String exceptionMessage = null;
/*    */   
/*    */   public CommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException) {
/* 40 */     this.exceptionMessage = SQLError.createLinkFailureMessageBasedOnHeuristics(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException);
/*    */     
/* 42 */     if (underlyingException != null) {
/* 43 */       initCause(underlyingException);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 52 */     return this.exceptionMessage;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 60 */     return "08S01";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setWasStreamingResults()
/*    */   {
/* 68 */     this.exceptionMessage = Messages.getString("CommunicationsException.ClientWasStreaming");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CommunicationsException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */