/*    */ package com.mysql.jdbc.authentication;
/*    */ 
/*    */ import com.mysql.jdbc.AuthenticationPlugin;
/*    */ import com.mysql.jdbc.Buffer;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.Messages;
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import com.mysql.jdbc.StringUtils;
/*    */ import java.io.UnsupportedEncodingException;
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
/*    */ public class MysqlClearPasswordPlugin
/*    */   implements AuthenticationPlugin
/*    */ {
/*    */   private Connection connection;
/* 44 */   private String password = null;
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 47 */     this.connection = conn;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 51 */     this.password = null;
/*    */   }
/*    */   
/*    */   public String getProtocolPluginName() {
/* 55 */     return "mysql_clear_password";
/*    */   }
/*    */   
/*    */   public boolean requiresConfidentiality() {
/* 59 */     return true;
/*    */   }
/*    */   
/*    */   public boolean isReusable() {
/* 63 */     return true;
/*    */   }
/*    */   
/*    */   public void setAuthenticationParameters(String user, String password) {
/* 67 */     this.password = password;
/*    */   }
/*    */   
/*    */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
/* 71 */     toServer.clear();
/*    */     Buffer bresp;
/*    */     try
/*    */     {
/* 75 */       String encoding = this.connection.versionMeetsMinimum(5, 7, 6) ? this.connection.getPasswordCharacterEncoding() : "UTF-8";
/* 76 */       bresp = new Buffer(StringUtils.getBytes(this.password != null ? this.password : "", encoding));
/*    */     } catch (UnsupportedEncodingException e) {
/* 78 */       throw SQLError.createSQLException(Messages.getString("MysqlClearPasswordPlugin.1", new Object[] { this.connection.getPasswordCharacterEncoding() }), "S1000", null);
/*    */     }
/*    */     
/*    */ 
/* 82 */     bresp.setPosition(bresp.getBufLength());
/* 83 */     int oldBufLength = bresp.getBufLength();
/*    */     
/* 85 */     bresp.writeByte((byte)0);
/*    */     
/* 87 */     bresp.setBufLength(oldBufLength + 1);
/* 88 */     bresp.setPosition(0);
/*    */     
/* 90 */     toServer.add(bresp);
/* 91 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\authentication\MysqlClearPasswordPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */