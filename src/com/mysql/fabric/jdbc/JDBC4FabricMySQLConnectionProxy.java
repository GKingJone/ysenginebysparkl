/*     */ package com.mysql.fabric.jdbc;
/*     */ 
/*     */ import com.mysql.fabric.FabricConnection;
/*     */ import com.mysql.jdbc.Connection;
/*     */ import com.mysql.jdbc.JDBC4ClientInfoProvider;
/*     */ import com.mysql.jdbc.JDBC4MySQLConnection;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.Map;
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
/*     */ public class JDBC4FabricMySQLConnectionProxy
/*     */   extends FabricMySQLConnectionProxy
/*     */   implements JDBC4FabricMySQLConnection, FabricMySQLConnectionProperties
/*     */ {
/*     */   private static final long serialVersionUID = 5845485979107347258L;
/*     */   private FabricConnection fabricConnection;
/*     */   
/*     */   public JDBC4FabricMySQLConnectionProxy(Properties props)
/*     */     throws SQLException
/*     */   {
/*  93 */     super(props);
/*     */   }
/*     */   
/*     */   public Blob createBlob() {
/*     */     try {
/*  98 */       transactionBegun();
/*  99 */       return getActiveConnection().createBlob();
/*     */     } catch (SQLException ex) {
/* 101 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public Clob createClob() {
/*     */     try {
/* 107 */       transactionBegun();
/* 108 */       return getActiveConnection().createClob();
/*     */     } catch (SQLException ex) {
/* 110 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public NClob createNClob() {
/*     */     try {
/* 116 */       transactionBegun();
/* 117 */       return getActiveConnection().createNClob();
/*     */     } catch (SQLException ex) {
/* 119 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException {
/* 124 */     transactionBegun();
/* 125 */     return getActiveConnection().createSQLXML();
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/* 129 */     for (Connection c : this.serverConnections.values())
/* 130 */       c.setClientInfo(properties);
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/* 134 */     for (Connection c : this.serverConnections.values())
/* 135 */       c.setClientInfo(name, value);
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/* 139 */     return getActiveConnection().createArrayOf(typeName, elements);
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/* 143 */     transactionBegun();
/* 144 */     return getActiveConnection().createStruct(typeName, attributes);
/*     */   }
/*     */   
/*     */   public JDBC4ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
/* 148 */     return ((JDBC4MySQLConnection)getActiveConnection()).getClientInfoProviderImpl();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\JDBC4FabricMySQLConnectionProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */