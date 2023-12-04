/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.XAConnection;
/*    */ import javax.sql.XADataSource;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MysqlXADataSource
/*    */   extends MysqlDataSource
/*    */   implements XADataSource
/*    */ {
/*    */   static final long serialVersionUID = 7911390333152247455L;
/*    */   
/*    */   public XAConnection getXAConnection()
/*    */     throws SQLException
/*    */   {
/* 46 */     java.sql.Connection conn = getConnection();
/*    */     
/* 48 */     return wrapConnection(conn);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public XAConnection getXAConnection(String u, String p)
/*    */     throws SQLException
/*    */   {
/* 56 */     java.sql.Connection conn = getConnection(u, p);
/*    */     
/* 58 */     return wrapConnection(conn);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private XAConnection wrapConnection(java.sql.Connection conn)
/*    */     throws SQLException
/*    */   {
/* 66 */     if ((getPinGlobalTxToPhysicalConnection()) || (((com.mysql.jdbc.Connection)conn).getPinGlobalTxToPhysicalConnection())) {
/* 67 */       return SuspendableXAConnection.getInstance((com.mysql.jdbc.Connection)conn);
/*    */     }
/*    */     
/* 70 */     return MysqlXAConnection.getInstance((com.mysql.jdbc.Connection)conn, getLogXaCommands());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlXADataSource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */