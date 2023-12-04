/*    */ package com.mysql.jdbc.authentication;
/*    */ 
/*    */ import com.mysql.jdbc.AuthenticationPlugin;
/*    */ import com.mysql.jdbc.Buffer;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.StringUtils;
/*    */ import com.mysql.jdbc.Util;
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
/*    */ public class MysqlOldPasswordPlugin
/*    */   implements AuthenticationPlugin
/*    */ {
/*    */   private Connection connection;
/* 42 */   private String password = null;
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 45 */     this.connection = conn;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 49 */     this.password = null;
/*    */   }
/*    */   
/*    */   public String getProtocolPluginName() {
/* 53 */     return "mysql_old_password";
/*    */   }
/*    */   
/*    */   public boolean requiresConfidentiality() {
/* 57 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isReusable() {
/* 61 */     return true;
/*    */   }
/*    */   
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 65 */     this.password = password;
/*    */   }
/*    */   
/*    */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
/* 69 */     toServer.clear();
/*    */     
/* 71 */     Buffer bresp = null;
/*    */     
/* 73 */     String pwd = this.password;
/*    */     
/* 75 */     if ((fromServer == null) || (pwd == null) || (pwd.length() == 0)) {
/* 76 */       bresp = new Buffer(new byte[0]);
/*    */     } else {
/* 78 */       bresp = new Buffer(StringUtils.getBytes(Util.newCrypt(pwd, fromServer.readString().substring(0, 8), this.connection.getPasswordCharacterEncoding())));
/*    */       
/*    */ 
/* 81 */       bresp.setPosition(bresp.getBufLength());
/* 82 */       int oldBufLength = bresp.getBufLength();
/*    */       
/* 84 */       bresp.writeByte((byte)0);
/*    */       
/* 86 */       bresp.setBufLength(oldBufLength + 1);
/* 87 */       bresp.setPosition(0);
/*    */     }
/* 89 */     toServer.add(bresp);
/*    */     
/* 91 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\authentication\MysqlOldPasswordPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */