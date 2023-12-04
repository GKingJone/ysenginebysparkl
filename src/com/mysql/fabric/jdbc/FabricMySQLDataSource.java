/*     */ package com.mysql.fabric.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public class FabricMySQLDataSource
/*     */   extends MysqlDataSource
/*     */   implements FabricMySQLConnectionProperties
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final Driver driver;
/*     */   private String fabricShardKey;
/*     */   private String fabricShardTable;
/*     */   private String fabricServerGroup;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  46 */       driver = new FabricMySQLDriver();
/*     */     } catch (Exception ex) {
/*  48 */       throw new RuntimeException("Can create driver", ex);
/*     */     }
/*     */   }
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
/*     */   protected Connection getConnection(Properties props)
/*     */     throws SQLException
/*     */   {
/*  68 */     String jdbcUrlToUse = null;
/*     */     
/*  70 */     if (!this.explicitUrl) {
/*  71 */       StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql:fabric://");
/*     */       
/*  73 */       if (this.hostName != null) {
/*  74 */         jdbcUrl.append(this.hostName);
/*     */       }
/*     */       
/*  77 */       jdbcUrl.append(":");
/*  78 */       jdbcUrl.append(this.port);
/*  79 */       jdbcUrl.append("/");
/*     */       
/*  81 */       if (this.databaseName != null) {
/*  82 */         jdbcUrl.append(this.databaseName);
/*     */       }
/*     */       
/*  85 */       jdbcUrlToUse = jdbcUrl.toString();
/*     */     } else {
/*  87 */       jdbcUrlToUse = this.url;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  94 */     Properties urlProps = ((FabricMySQLDriver)driver).parseFabricURL(jdbcUrlToUse, null);
/*  95 */     urlProps.remove("DBNAME");
/*  96 */     urlProps.remove("HOST");
/*  97 */     urlProps.remove("PORT");
/*     */     
/*  99 */     Iterator<Object> keys = urlProps.keySet().iterator();
/*     */     
/* 101 */     while (keys.hasNext()) {
/* 102 */       String key = (String)keys.next();
/*     */       
/* 104 */       props.setProperty(key, urlProps.getProperty(key));
/*     */     }
/*     */     
/* 107 */     if (this.fabricShardKey != null) {
/* 108 */       props.setProperty("fabricShardKey", this.fabricShardKey);
/*     */     }
/* 110 */     if (this.fabricShardTable != null) {
/* 111 */       props.setProperty("fabricShardTable", this.fabricShardTable);
/*     */     }
/* 113 */     if (this.fabricServerGroup != null) {
/* 114 */       props.setProperty("fabricServerGroup", this.fabricServerGroup);
/*     */     }
/* 116 */     props.setProperty("fabricProtocol", this.fabricProtocol);
/* 117 */     if (this.fabricUsername != null) {
/* 118 */       props.setProperty("fabricUsername", this.fabricUsername);
/*     */     }
/* 120 */     if (this.fabricPassword != null) {
/* 121 */       props.setProperty("fabricPassword", this.fabricPassword);
/*     */     }
/* 123 */     props.setProperty("fabricReportErrors", Boolean.toString(this.fabricReportErrors));
/*     */     
/* 125 */     return driver.connect(jdbcUrlToUse, props);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 131 */   private String fabricProtocol = "http";
/*     */   private String fabricUsername;
/*     */   private String fabricPassword;
/* 134 */   private boolean fabricReportErrors = false;
/*     */   
/*     */   public void setFabricShardKey(String value) {
/* 137 */     this.fabricShardKey = value;
/*     */   }
/*     */   
/*     */   public String getFabricShardKey() {
/* 141 */     return this.fabricShardKey;
/*     */   }
/*     */   
/*     */   public void setFabricShardTable(String value) {
/* 145 */     this.fabricShardTable = value;
/*     */   }
/*     */   
/*     */   public String getFabricShardTable() {
/* 149 */     return this.fabricShardTable;
/*     */   }
/*     */   
/*     */   public void setFabricServerGroup(String value) {
/* 153 */     this.fabricServerGroup = value;
/*     */   }
/*     */   
/*     */   public String getFabricServerGroup() {
/* 157 */     return this.fabricServerGroup;
/*     */   }
/*     */   
/*     */   public void setFabricProtocol(String value) {
/* 161 */     this.fabricProtocol = value;
/*     */   }
/*     */   
/*     */   public String getFabricProtocol() {
/* 165 */     return this.fabricProtocol;
/*     */   }
/*     */   
/*     */   public void setFabricUsername(String value) {
/* 169 */     this.fabricUsername = value;
/*     */   }
/*     */   
/*     */   public String getFabricUsername() {
/* 173 */     return this.fabricUsername;
/*     */   }
/*     */   
/*     */   public void setFabricPassword(String value) {
/* 177 */     this.fabricPassword = value;
/*     */   }
/*     */   
/*     */   public String getFabricPassword() {
/* 181 */     return this.fabricPassword;
/*     */   }
/*     */   
/*     */   public void setFabricReportErrors(boolean value) {
/* 185 */     this.fabricReportErrors = value;
/*     */   }
/*     */   
/*     */   public boolean getFabricReportErrors() {
/* 189 */     return this.fabricReportErrors;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\FabricMySQLDataSource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */