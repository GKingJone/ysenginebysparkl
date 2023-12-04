/*    */ package com.yisa.wifi.common;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SQLUtil
/*    */ {
/* 19 */   private Connection conn = null;
/*    */   
/*    */   public void closeConnect(Connection conn) {
/*    */     try {
/* 23 */       conn.close();
/*    */     }
/*    */     catch (SQLException e) {
/* 26 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */   public Connection connectDB(String dbAddr, String dbUser, String dbPassword)
/*    */   {
/*    */     try
/*    */     {
/* 34 */       System.out.println("--------------------------");
/* 35 */       if ((this.conn == null) || (this.conn.isClosed())) {
/* 36 */         Class.forName("com.mysql.jdbc.Driver");
/*    */         
/* 38 */         String url = "jdbc:mysql://" + dbAddr + "?useUnicode=true&characterEncoding=UTF8";
/*    */         
/*    */ 
/* 41 */         this.conn = DriverManager.getConnection(url, dbUser, dbPassword);
/* 42 */         System.out.println("Mysql connected!");
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 47 */       e.printStackTrace();
/*    */     }
/*    */     
/* 50 */     return this.conn;
/*    */   }
/*    */   
/*    */   public ArrayList<HashMap<String, String>> executeSql(String query, Connection conn)
/*    */     throws Exception
/*    */   {
/* 56 */     ArrayList<HashMap<String, String>> result_list = new ArrayList();
/* 57 */     ResultSet rs = null;
/* 58 */     Statement statement = null;
/*    */     try {
/* 60 */       statement = conn.createStatement();
/* 61 */       rs = statement.executeQuery(query);
/* 62 */       while (rs.next()) {
/* 63 */         ResultSetMetaData rsmd = rs.getMetaData();
/* 64 */         HashMap<String, String> result = new HashMap();
/* 65 */         for (int i = 1; i <= rsmd.getColumnCount(); i++) {
/* 66 */           result.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
/*    */         }
/* 68 */         result_list.add(result);
/*    */       }
/*    */     } finally {
/* 71 */       statement.close();
/*    */     }
/* 73 */     System.out.println("sql executed!");
/* 74 */     return result_list;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\common\SQLUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */