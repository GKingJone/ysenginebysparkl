/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NonRegisteringReplicationDriver
/*    */   extends NonRegisteringDriver
/*    */ {
/*    */   public NonRegisteringReplicationDriver()
/*    */     throws SQLException
/*    */   {}
/*    */   
/*    */   public Connection connect(String url, Properties info)
/*    */     throws SQLException
/*    */   {
/* 46 */     return connectReplicationConnection(url, info);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NonRegisteringReplicationDriver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */