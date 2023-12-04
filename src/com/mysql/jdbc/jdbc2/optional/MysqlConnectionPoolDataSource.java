/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.ConnectionPoolDataSource;
/*    */ import javax.sql.PooledConnection;
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
/*    */ 
/*    */ 
/*    */ public class MysqlConnectionPoolDataSource
/*    */   extends MysqlDataSource
/*    */   implements ConnectionPoolDataSource
/*    */ {
/*    */   static final long serialVersionUID = -7767325445592304961L;
/*    */   
/*    */   public synchronized PooledConnection getPooledConnection()
/*    */     throws SQLException
/*    */   {
/* 48 */     java.sql.Connection connection = getConnection();
/* 49 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */     
/* 51 */     return mysqlPooledConnection;
/*    */   }
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
/*    */   public synchronized PooledConnection getPooledConnection(String s, String s1)
/*    */     throws SQLException
/*    */   {
/* 67 */     java.sql.Connection connection = getConnection(s, s1);
/* 68 */     MysqlPooledConnection mysqlPooledConnection = MysqlPooledConnection.getInstance((com.mysql.jdbc.Connection)connection);
/*    */     
/* 70 */     return mysqlPooledConnection;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlConnectionPoolDataSource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */