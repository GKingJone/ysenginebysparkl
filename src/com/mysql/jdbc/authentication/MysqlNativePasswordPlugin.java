/*    */ package com.mysql.jdbc.authentication;
/*    */ 
/*    */ import com.mysql.jdbc.AuthenticationPlugin;
/*    */ import com.mysql.jdbc.Buffer;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.Messages;
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import com.mysql.jdbc.Security;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
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
/*    */ public class MysqlNativePasswordPlugin
/*    */   implements AuthenticationPlugin
/*    */ {
/*    */   private Connection connection;
/* 45 */   private String password = null;
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 48 */     this.connection = conn;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 52 */     this.password = null;
/*    */   }
/*    */   
/*    */   public String getProtocolPluginName() {
/* 56 */     return "mysql_native_password";
/*    */   }
/*    */   
/*    */   public boolean requiresConfidentiality() {
/* 60 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isReusable() {
/* 64 */     return true;
/*    */   }
/*    */   
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 68 */     this.password = password;
/*    */   }
/*    */   
/*    */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException
/*    */   {
/*    */     try {
/* 74 */       toServer.clear();
/*    */       
/* 76 */       Buffer bresp = null;
/*    */       
/* 78 */       String pwd = this.password;
/*    */       
/* 80 */       if ((fromServer == null) || (pwd == null) || (pwd.length() == 0)) {
/* 81 */         bresp = new Buffer(new byte[0]);
/*    */       } else {
/* 83 */         bresp = new Buffer(Security.scramble411(pwd, fromServer.readString(), this.connection.getPasswordCharacterEncoding()));
/*    */       }
/* 85 */       toServer.add(bresp);
/*    */     }
/*    */     catch (NoSuchAlgorithmException nse) {
/* 88 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", null);
/*    */     } catch (UnsupportedEncodingException e) {
/* 90 */       throw SQLError.createSQLException(Messages.getString("MysqlNativePasswordPlugin.1", new Object[] { this.connection.getPasswordCharacterEncoding() }), "S1000", null);
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 95 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\authentication\MysqlNativePasswordPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */