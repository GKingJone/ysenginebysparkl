/*     */ package com.mysql.jdbc.jmx;
/*     */ 
/*     */ import com.mysql.jdbc.ReplicationConnectionGroup;
/*     */ import com.mysql.jdbc.ReplicationConnectionGroupManager;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
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
/*     */ public class ReplicationGroupManager
/*     */   implements ReplicationGroupManagerMBean
/*     */ {
/*  37 */   private boolean isJmxRegistered = false;
/*     */   
/*     */   public synchronized void registerJmx() throws SQLException {
/*  40 */     if (this.isJmxRegistered) {
/*  41 */       return;
/*     */     }
/*  43 */     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
/*     */     try {
/*  45 */       ObjectName name = new ObjectName("com.mysql.jdbc.jmx:type=ReplicationGroupManager");
/*  46 */       mbs.registerMBean(this, name);
/*  47 */       this.isJmxRegistered = true;
/*     */     } catch (Exception e) {
/*  49 */       throw SQLError.createSQLException("Unable to register replication host management bean with JMX", null, e, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addSlaveHost(String groupFilter, String host) throws SQLException
/*     */   {
/*  55 */     ReplicationConnectionGroupManager.addSlaveHost(groupFilter, host);
/*     */   }
/*     */   
/*     */   public void removeSlaveHost(String groupFilter, String host) throws SQLException {
/*  59 */     ReplicationConnectionGroupManager.removeSlaveHost(groupFilter, host);
/*     */   }
/*     */   
/*     */   public void promoteSlaveToMaster(String groupFilter, String host) throws SQLException {
/*  63 */     ReplicationConnectionGroupManager.promoteSlaveToMaster(groupFilter, host);
/*     */   }
/*     */   
/*     */   public void removeMasterHost(String groupFilter, String host) throws SQLException
/*     */   {
/*  68 */     ReplicationConnectionGroupManager.removeMasterHost(groupFilter, host);
/*     */   }
/*     */   
/*     */   public String getMasterHostsList(String group)
/*     */   {
/*  73 */     StringBuilder sb = new StringBuilder("");
/*  74 */     boolean found = false;
/*  75 */     for (String host : ReplicationConnectionGroupManager.getMasterHosts(group)) {
/*  76 */       if (found) {
/*  77 */         sb.append(",");
/*     */       }
/*  79 */       found = true;
/*  80 */       sb.append(host);
/*     */     }
/*  82 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String getSlaveHostsList(String group) {
/*  86 */     StringBuilder sb = new StringBuilder("");
/*  87 */     boolean found = false;
/*  88 */     for (String host : ReplicationConnectionGroupManager.getSlaveHosts(group)) {
/*  89 */       if (found) {
/*  90 */         sb.append(",");
/*     */       }
/*  92 */       found = true;
/*  93 */       sb.append(host);
/*     */     }
/*  95 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public String getRegisteredConnectionGroups()
/*     */   {
/* 100 */     StringBuilder sb = new StringBuilder("");
/* 101 */     boolean found = false;
/* 102 */     for (ReplicationConnectionGroup group : ReplicationConnectionGroupManager.getGroupsMatching(null)) {
/* 103 */       if (found) {
/* 104 */         sb.append(",");
/*     */       }
/* 106 */       found = true;
/* 107 */       sb.append(group.getGroupName());
/*     */     }
/* 109 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public int getActiveMasterHostCount(String group) {
/* 113 */     return ReplicationConnectionGroupManager.getMasterHosts(group).size();
/*     */   }
/*     */   
/*     */   public int getActiveSlaveHostCount(String group) {
/* 117 */     return ReplicationConnectionGroupManager.getSlaveHosts(group).size();
/*     */   }
/*     */   
/*     */   public int getSlavePromotionCount(String group) {
/* 121 */     return ReplicationConnectionGroupManager.getNumberOfMasterPromotion(group);
/*     */   }
/*     */   
/*     */   public long getTotalLogicalConnectionCount(String group) {
/* 125 */     return ReplicationConnectionGroupManager.getTotalConnectionCount(group);
/*     */   }
/*     */   
/*     */   public long getActiveLogicalConnectionCount(String group) {
/* 129 */     return ReplicationConnectionGroupManager.getActiveConnectionCount(group);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jmx\ReplicationGroupManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */