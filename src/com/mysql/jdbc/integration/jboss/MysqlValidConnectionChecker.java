/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import org.jboss.resource.adapter.jdbc.ValidConnectionChecker;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class MysqlValidConnectionChecker
/*    */   implements ValidConnectionChecker, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8909421133577519177L;
/*    */   
/*    */   public SQLException isValidConnection(Connection conn)
/*    */   {
/* 52 */     Statement pingStatement = null;
/*    */     try
/*    */     {
/* 55 */       pingStatement = conn.createStatement();
/*    */       
/* 57 */       pingStatement.executeQuery("/* ping */ SELECT 1").close();
/*    */       
/* 59 */       return null;
/*    */     } catch (SQLException sqlEx) {
/* 61 */       return sqlEx;
/*    */     } finally {
/* 63 */       if (pingStatement != null) {
/*    */         try {
/* 65 */           pingStatement.close();
/*    */         }
/*    */         catch (SQLException sqlEx) {}
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\integration\jboss\MysqlValidConnectionChecker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */