/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.spi.ObjectFactory;
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
/*     */ public class MysqlDataSourceFactory
/*     */   implements ObjectFactory
/*     */ {
/*     */   protected static final String DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
/*     */   protected static final String POOL_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource";
/*     */   protected static final String XA_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";
/*     */   
/*     */   public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable<?, ?> env)
/*     */     throws Exception
/*     */   {
/*  64 */     Reference ref = (Reference)refObj;
/*  65 */     String className = ref.getClassName();
/*     */     
/*  67 */     if ((className != null) && ((className.equals("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")) || (className.equals("com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource")) || (className.equals("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"))))
/*     */     {
/*  69 */       MysqlDataSource dataSource = null;
/*     */       try
/*     */       {
/*  72 */         dataSource = (MysqlDataSource)Class.forName(className).newInstance();
/*     */       } catch (Exception ex) {
/*  74 */         throw new RuntimeException("Unable to create DataSource of class '" + className + "', reason: " + ex.toString());
/*     */       }
/*     */       
/*  77 */       int portNumber = 3306;
/*     */       
/*  79 */       String portNumberAsString = nullSafeRefAddrStringGet("port", ref);
/*     */       
/*  81 */       if (portNumberAsString != null) {
/*  82 */         portNumber = Integer.parseInt(portNumberAsString);
/*     */       }
/*     */       
/*  85 */       dataSource.setPort(portNumber);
/*     */       
/*  87 */       String user = nullSafeRefAddrStringGet("user", ref);
/*     */       
/*  89 */       if (user != null) {
/*  90 */         dataSource.setUser(user);
/*     */       }
/*     */       
/*  93 */       String password = nullSafeRefAddrStringGet("password", ref);
/*     */       
/*  95 */       if (password != null) {
/*  96 */         dataSource.setPassword(password);
/*     */       }
/*     */       
/*  99 */       String serverName = nullSafeRefAddrStringGet("serverName", ref);
/*     */       
/* 101 */       if (serverName != null) {
/* 102 */         dataSource.setServerName(serverName);
/*     */       }
/*     */       
/* 105 */       String databaseName = nullSafeRefAddrStringGet("databaseName", ref);
/*     */       
/* 107 */       if (databaseName != null) {
/* 108 */         dataSource.setDatabaseName(databaseName);
/*     */       }
/*     */       
/* 111 */       String explicitUrlAsString = nullSafeRefAddrStringGet("explicitUrl", ref);
/*     */       
/* 113 */       if ((explicitUrlAsString != null) && 
/* 114 */         (Boolean.valueOf(explicitUrlAsString).booleanValue())) {
/* 115 */         dataSource.setUrl(nullSafeRefAddrStringGet("url", ref));
/*     */       }
/*     */       
/*     */ 
/* 119 */       dataSource.setPropertiesViaRef(ref);
/*     */       
/* 121 */       return dataSource;
/*     */     }
/*     */     
/*     */ 
/* 125 */     return null;
/*     */   }
/*     */   
/*     */   private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
/* 129 */     RefAddr refAddr = ref.get(referenceName);
/*     */     
/* 131 */     String asString = refAddr != null ? (String)refAddr.getContent() : null;
/*     */     
/* 133 */     return asString;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlDataSourceFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */