/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DriverManager;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Driver
/*    */   extends NonRegisteringDriver
/*    */   implements java.sql.Driver
/*    */ {
/*    */   public Driver()
/*    */     throws SQLException
/*    */   {}
/*    */   
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 49 */       DriverManager.registerDriver(new Driver());
/*    */     } catch (SQLException E) {
/* 51 */       throw new RuntimeException("Can't register driver!");
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Driver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */