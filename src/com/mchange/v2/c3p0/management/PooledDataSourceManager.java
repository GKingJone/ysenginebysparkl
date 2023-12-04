/*     */ package com.mchange.v2.c3p0.management;
/*     */ 
/*     */ import com.mchange.v2.c3p0.PooledDataSource;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
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
/*     */ public class PooledDataSourceManager
/*     */   implements PooledDataSourceManagerMBean
/*     */ {
/*     */   PooledDataSource pds;
/*     */   
/*     */   public PooledDataSourceManager(PooledDataSource pds)
/*     */   {
/*  35 */     this.pds = pds;
/*     */   }
/*     */   
/*  38 */   public String getIdentityToken() { return this.pds.getIdentityToken(); }
/*     */   
/*     */   public String getDataSourceName() {
/*  41 */     return this.pds.getDataSourceName();
/*     */   }
/*     */   
/*  44 */   public void setDataSourceName(String dataSourceName) { this.pds.setDataSourceName(dataSourceName); }
/*     */   
/*     */   public int getNumConnectionsDefaultUser() throws SQLException {
/*  47 */     return this.pds.getNumConnectionsDefaultUser();
/*     */   }
/*     */   
/*  50 */   public int getNumIdleConnectionsDefaultUser() throws SQLException { return this.pds.getNumIdleConnectionsDefaultUser(); }
/*     */   
/*     */   public int getNumBusyConnectionsDefaultUser() throws SQLException {
/*  53 */     return this.pds.getNumBusyConnectionsDefaultUser();
/*     */   }
/*     */   
/*  56 */   public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException { return this.pds.getNumUnclosedOrphanedConnectionsDefaultUser(); }
/*     */   
/*     */   public float getEffectivePropertyCycleDefaultUser() throws SQLException {
/*  59 */     return this.pds.getEffectivePropertyCycleDefaultUser();
/*     */   }
/*     */   
/*  62 */   public int getThreadPoolSize() throws SQLException { return this.pds.getThreadPoolSize(); }
/*     */   
/*     */   public int getThreadPoolNumActiveThreads() throws SQLException {
/*  65 */     return this.pds.getThreadPoolNumActiveThreads();
/*     */   }
/*     */   
/*  68 */   public int getThreadPoolNumIdleThreads() throws SQLException { return this.pds.getThreadPoolNumIdleThreads(); }
/*     */   
/*     */   public int getThreadPoolNumTasksPending() throws SQLException {
/*  71 */     return this.pds.getThreadPoolNumTasksPending();
/*     */   }
/*     */   
/*  74 */   public String sampleThreadPoolStackTraces() throws SQLException { return this.pds.sampleThreadPoolStackTraces(); }
/*     */   
/*     */   public String sampleThreadPoolStatus() throws SQLException {
/*  77 */     return this.pds.sampleThreadPoolStatus();
/*     */   }
/*     */   
/*  80 */   public void softResetDefaultUser() throws SQLException { this.pds.softResetDefaultUser(); }
/*     */   
/*     */   public int getNumConnections(String username, String password) throws SQLException {
/*  83 */     return this.pds.getNumConnections(username, password);
/*     */   }
/*     */   
/*  86 */   public int getNumIdleConnections(String username, String password) throws SQLException { return this.pds.getNumIdleConnections(username, password); }
/*     */   
/*     */   public int getNumBusyConnections(String username, String password) throws SQLException {
/*  89 */     return this.pds.getNumBusyConnections(username, password);
/*     */   }
/*     */   
/*  92 */   public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException { return this.pds.getNumUnclosedOrphanedConnections(username, password); }
/*     */   
/*     */   public float getEffectivePropertyCycle(String username, String password) throws SQLException {
/*  95 */     return this.pds.getEffectivePropertyCycle(username, password);
/*     */   }
/*     */   
/*  98 */   public void softReset(String username, String password) throws SQLException { this.pds.softReset(username, password); }
/*     */   
/*     */   public int getNumBusyConnectionsAllUsers() throws SQLException {
/* 101 */     return this.pds.getNumBusyConnectionsAllUsers();
/*     */   }
/*     */   
/* 104 */   public int getNumIdleConnectionsAllUsers() throws SQLException { return this.pds.getNumIdleConnectionsAllUsers(); }
/*     */   
/*     */   public int getNumConnectionsAllUsers() throws SQLException {
/* 107 */     return this.pds.getNumConnectionsAllUsers();
/*     */   }
/*     */   
/* 110 */   public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException { return this.pds.getNumUnclosedOrphanedConnectionsAllUsers(); }
/*     */   
/*     */   public void softResetAllUsers() throws SQLException {
/* 113 */     this.pds.softResetAllUsers();
/*     */   }
/*     */   
/* 116 */   public int getNumUserPools() throws SQLException { return this.pds.getNumUserPools(); }
/*     */   
/*     */   public Collection getAllUsers() throws SQLException {
/* 119 */     return this.pds.getAllUsers();
/*     */   }
/*     */   
/* 122 */   public void hardReset() throws SQLException { this.pds.hardReset(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 125 */     this.pds.close();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\PooledDataSourceManager.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */