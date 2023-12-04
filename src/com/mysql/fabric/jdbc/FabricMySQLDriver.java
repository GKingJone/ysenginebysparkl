/*     */ package com.mysql.fabric.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
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
/*     */ public class FabricMySQLDriver
/*     */   extends NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   public static final String FABRIC_URL_PREFIX = "jdbc:mysql:fabric://";
/*     */   public static final String FABRIC_SHARD_KEY_PROPERTY_KEY = "fabricShardKey";
/*     */   public static final String FABRIC_SHARD_TABLE_PROPERTY_KEY = "fabricShardTable";
/*     */   public static final String FABRIC_SERVER_GROUP_PROPERTY_KEY = "fabricServerGroup";
/*     */   public static final String FABRIC_PROTOCOL_PROPERTY_KEY = "fabricProtocol";
/*     */   public static final String FABRIC_USERNAME_PROPERTY_KEY = "fabricUsername";
/*     */   public static final String FABRIC_PASSWORD_PROPERTY_KEY = "fabricPassword";
/*     */   public static final String FABRIC_REPORT_ERRORS_PROPERTY_KEY = "fabricReportErrors";
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  56 */       DriverManager.registerDriver(new FabricMySQLDriver());
/*     */     } catch (SQLException ex) {
/*  58 */       throw new RuntimeException("Can't register driver", ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/*  67 */     Properties parsedProps = parseFabricURL(url, info);
/*     */     
/*  69 */     if (parsedProps == null) {
/*  70 */       return null;
/*     */     }
/*     */     
/*  73 */     parsedProps.setProperty("fabricProtocol", "http");
/*  74 */     if (Util.isJdbc4()) {
/*     */       try {
/*  76 */         Constructor<?> jdbc4proxy = Class.forName("com.mysql.fabric.jdbc.JDBC4FabricMySQLConnectionProxy").getConstructor(new Class[] { Properties.class });
/*     */         
/*  78 */         return (Connection)Util.handleNewInstance(jdbc4proxy, new Object[] { parsedProps }, null);
/*     */       } catch (Exception e) {
/*  80 */         throw ((SQLException)new SQLException(e.getMessage()).initCause(e));
/*     */       }
/*     */     }
/*     */     
/*  84 */     return new FabricMySQLConnectionProxy(parsedProps);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/*  93 */     return parseFabricURL(url, null) != null;
/*     */   }
/*     */   
/*     */   Properties parseFabricURL(String url, Properties defaults) throws SQLException {
/*  97 */     if (!url.startsWith("jdbc:mysql:fabric://")) {
/*  98 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 103 */     return super.parseURL(url.replaceAll("fabric:", ""), defaults);
/*     */   }
/*     */   
/*     */   public Logger getParentLogger() throws SQLException {
/* 107 */     throw new SQLException("no logging");
/*     */   }
/*     */   
/*     */   public FabricMySQLDriver()
/*     */     throws SQLException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\FabricMySQLDriver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */