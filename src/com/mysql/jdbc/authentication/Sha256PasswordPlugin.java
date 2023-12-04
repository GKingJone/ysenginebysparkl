/*     */ package com.mysql.jdbc.authentication;
/*     */ 
/*     */ import com.mysql.jdbc.AuthenticationPlugin;
/*     */ import com.mysql.jdbc.Buffer;
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.ExportControlled;
/*     */ import com.mysql.jdbc.Messages;
/*     */ import com.mysql.jdbc.MySQLConnection;
/*     */ import com.mysql.jdbc.MysqlIO;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Security;
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Sha256PasswordPlugin
/*     */   implements AuthenticationPlugin
/*     */ {
/*  50 */   public static String PLUGIN_NAME = "sha256_password";
/*     */   
/*     */   private Connection connection;
/*  53 */   private String password = null;
/*  54 */   private String seed = null;
/*  55 */   private boolean publicKeyRequested = false;
/*  56 */   private String publicKeyString = null;
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException {
/*  59 */     this.connection = conn;
/*     */     
/*  61 */     String pkURL = this.connection.getServerRSAPublicKeyFile();
/*  62 */     if (pkURL != null) {
/*  63 */       this.publicKeyString = readRSAKey(this.connection, pkURL);
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroy() {
/*  68 */     this.password = null;
/*  69 */     this.seed = null;
/*  70 */     this.publicKeyRequested = false;
/*     */   }
/*     */   
/*     */   public String getProtocolPluginName() {
/*  74 */     return PLUGIN_NAME;
/*     */   }
/*     */   
/*     */   public boolean requiresConfidentiality() {
/*  78 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isReusable() {
/*  82 */     return true;
/*     */   }
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/*  86 */     this.password = password;
/*     */   }
/*     */   
/*     */   public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
/*  90 */     toServer.clear();
/*     */     
/*  92 */     if ((this.password == null) || (this.password.length() == 0) || (fromServer == null))
/*     */     {
/*  94 */       Buffer bresp = new Buffer(new byte[] { 0 });
/*  95 */       toServer.add(bresp);
/*     */     }
/*  97 */     else if (((MySQLConnection)this.connection).getIO().isSSLEstablished())
/*     */     {
/*     */       Buffer bresp;
/*     */       try {
/* 101 */         bresp = new Buffer(StringUtils.getBytes(this.password, this.connection.getPasswordCharacterEncoding()));
/*     */       } catch (UnsupportedEncodingException e) {
/* 103 */         throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.3", new Object[] { this.connection.getPasswordCharacterEncoding() }), "S1000", null);
/*     */       }
/*     */       
/* 106 */       bresp.setPosition(bresp.getBufLength());
/* 107 */       int oldBufLength = bresp.getBufLength();
/* 108 */       bresp.writeByte((byte)0);
/* 109 */       bresp.setBufLength(oldBufLength + 1);
/* 110 */       bresp.setPosition(0);
/* 111 */       toServer.add(bresp);
/*     */     }
/* 113 */     else if (this.connection.getServerRSAPublicKeyFile() != null)
/*     */     {
/* 115 */       this.seed = fromServer.readString();
/* 116 */       Buffer bresp = new Buffer(encryptPassword(this.password, this.seed, this.connection, this.publicKeyString));
/* 117 */       toServer.add(bresp);
/*     */     }
/*     */     else {
/* 120 */       if (!this.connection.getAllowPublicKeyRetrieval()) {
/* 121 */         throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.2"), "08001", this.connection.getExceptionInterceptor());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 126 */       if ((this.publicKeyRequested) && (fromServer.getBufLength() > 20))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 131 */         Buffer bresp = new Buffer(encryptPassword(this.password, this.seed, this.connection, fromServer.readString()));
/* 132 */         toServer.add(bresp);
/* 133 */         this.publicKeyRequested = false;
/*     */       }
/*     */       else {
/* 136 */         this.seed = fromServer.readString();
/* 137 */         Buffer bresp = new Buffer(new byte[] { 1 });
/* 138 */         toServer.add(bresp);
/* 139 */         this.publicKeyRequested = true;
/*     */       }
/*     */     }
/* 142 */     return true;
/*     */   }
/*     */   
/*     */   private static byte[] encryptPassword(String password, String seed, Connection connection, String key) throws SQLException {
/* 146 */     byte[] input = null;
/*     */     try {
/* 148 */       input = new byte[] { password != null ? StringUtils.getBytesNullTerminated(password, connection.getPasswordCharacterEncoding()) : 0 };
/*     */     } catch (UnsupportedEncodingException e) {
/* 150 */       throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.3", new Object[] { connection.getPasswordCharacterEncoding() }), "S1000", null);
/*     */     }
/*     */     
/* 153 */     byte[] mysqlScrambleBuff = new byte[input.length];
/* 154 */     Security.xorString(input, mysqlScrambleBuff, seed.getBytes(), input.length);
/* 155 */     return ExportControlled.encryptWithRSAPublicKey(mysqlScrambleBuff, ExportControlled.decodeRSAPublicKey(key, ((MySQLConnection)connection).getExceptionInterceptor()), ((MySQLConnection)connection).getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   private static String readRSAKey(Connection connection, String pkPath)
/*     */     throws SQLException
/*     */   {
/* 161 */     String res = null;
/* 162 */     byte[] fileBuf = new byte['ࠀ'];
/*     */     
/* 164 */     BufferedInputStream fileIn = null;
/*     */     try
/*     */     {
/* 167 */       File f = new File(pkPath);
/* 168 */       String canonicalPath = f.getCanonicalPath();
/* 169 */       fileIn = new BufferedInputStream(new FileInputStream(canonicalPath));
/*     */       
/* 171 */       int bytesRead = 0;
/*     */       
/* 173 */       StringBuilder sb = new StringBuilder();
/* 174 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 175 */         sb.append(StringUtils.toAsciiString(fileBuf, 0, bytesRead));
/*     */       }
/* 177 */       res = sb.toString();
/*     */     }
/*     */     catch (IOException ioEx)
/*     */     {
/* 181 */       if (connection.getParanoid()) {
/* 182 */         throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.0", new Object[] { "" }), "S1009", connection.getExceptionInterceptor());
/*     */       }
/*     */       
/* 185 */       throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.0", new Object[] { "'" + pkPath + "'" }), "S1009", ioEx, connection.getExceptionInterceptor());
/*     */     }
/*     */     finally
/*     */     {
/* 189 */       if (fileIn != null) {
/*     */         try {
/* 191 */           fileIn.close();
/*     */         } catch (Exception ex) {
/* 193 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.1"), "S1000", ex, connection.getExceptionInterceptor());
/*     */           
/*     */ 
/* 196 */           throw sqlEx;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 201 */     return res;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\authentication\Sha256PasswordPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */