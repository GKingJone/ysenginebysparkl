/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
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
/*     */ public class JDBC4ClientInfoProviderSP
/*     */   implements JDBC4ClientInfoProvider
/*     */ {
/*     */   PreparedStatement setClientInfoSp;
/*     */   PreparedStatement getClientInfoSp;
/*     */   PreparedStatement getClientInfoBulkSp;
/*     */   
/*     */   public synchronized void initialize(java.sql.Connection conn, Properties configurationProps)
/*     */     throws SQLException
/*     */   {
/*  41 */     String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
/*  42 */     String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
/*  43 */     String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
/*  44 */     String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
/*  45 */     String clientInfoCatalog = configurationProps.getProperty("clientInfoCatalog", "");
/*     */     
/*     */ 
/*  48 */     String catalog = "".equals(clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
/*     */     
/*  50 */     this.setClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
/*     */     
/*     */ 
/*  53 */     this.getClientInfoSp = ((Connection)conn).clientPrepareStatement("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
/*     */     
/*     */ 
/*  56 */     this.getClientInfoBulkSp = ((Connection)conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
/*     */   }
/*     */   
/*     */   public synchronized void destroy() throws SQLException
/*     */   {
/*  61 */     if (this.setClientInfoSp != null) {
/*  62 */       this.setClientInfoSp.close();
/*  63 */       this.setClientInfoSp = null;
/*     */     }
/*     */     
/*  66 */     if (this.getClientInfoSp != null) {
/*  67 */       this.getClientInfoSp.close();
/*  68 */       this.getClientInfoSp = null;
/*     */     }
/*     */     
/*  71 */     if (this.getClientInfoBulkSp != null) {
/*  72 */       this.getClientInfoBulkSp.close();
/*  73 */       this.getClientInfoBulkSp = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized Properties getClientInfo(java.sql.Connection conn) throws SQLException {
/*  78 */     ResultSet rs = null;
/*     */     
/*  80 */     Properties props = new Properties();
/*     */     try
/*     */     {
/*  83 */       this.getClientInfoBulkSp.execute();
/*     */       
/*  85 */       rs = this.getClientInfoBulkSp.getResultSet();
/*     */       
/*  87 */       while (rs.next()) {
/*  88 */         props.setProperty(rs.getString(1), rs.getString(2));
/*     */       }
/*     */     } finally {
/*  91 */       if (rs != null) {
/*  92 */         rs.close();
/*     */       }
/*     */     }
/*     */     
/*  96 */     return props;
/*     */   }
/*     */   
/*     */   public synchronized String getClientInfo(java.sql.Connection conn, String name) throws SQLException {
/* 100 */     ResultSet rs = null;
/*     */     
/* 102 */     String clientInfo = null;
/*     */     try
/*     */     {
/* 105 */       this.getClientInfoSp.setString(1, name);
/* 106 */       this.getClientInfoSp.execute();
/*     */       
/* 108 */       rs = this.getClientInfoSp.getResultSet();
/*     */       
/* 110 */       if (rs.next()) {
/* 111 */         clientInfo = rs.getString(1);
/*     */       }
/*     */     } finally {
/* 114 */       if (rs != null) {
/* 115 */         rs.close();
/*     */       }
/*     */     }
/*     */     
/* 119 */     return clientInfo;
/*     */   }
/*     */   
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 124 */       Enumeration<?> propNames = properties.propertyNames();
/*     */       
/* 126 */       while (propNames.hasMoreElements()) {
/* 127 */         String name = (String)propNames.nextElement();
/* 128 */         String value = properties.getProperty(name);
/*     */         
/* 130 */         setClientInfo(conn, name, value);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 133 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 134 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 136 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setClientInfo(java.sql.Connection conn, String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 142 */       this.setClientInfoSp.setString(1, name);
/* 143 */       this.setClientInfoSp.setString(2, value);
/* 144 */       this.setClientInfoSp.execute();
/*     */     } catch (SQLException sqlEx) {
/* 146 */       SQLClientInfoException clientInfoEx = new SQLClientInfoException();
/* 147 */       clientInfoEx.initCause(sqlEx);
/*     */       
/* 149 */       throw clientInfoEx;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4ClientInfoProviderSP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */