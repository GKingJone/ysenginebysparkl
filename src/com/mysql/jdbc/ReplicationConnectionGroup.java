/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReplicationConnectionGroup
/*     */ {
/*     */   private String groupName;
/*  39 */   private long connections = 0L;
/*  40 */   private long slavesAdded = 0L;
/*  41 */   private long slavesRemoved = 0L;
/*  42 */   private long slavesPromoted = 0L;
/*  43 */   private long activeConnections = 0L;
/*  44 */   private HashMap<Long, ReplicationConnection> replicationConnections = new HashMap();
/*  45 */   private Set<String> slaveHostList = new HashSet();
/*  46 */   private boolean isInitialized = false;
/*  47 */   private Set<String> masterHostList = new HashSet();
/*     */   
/*     */   ReplicationConnectionGroup(String groupName) {
/*  50 */     this.groupName = groupName;
/*     */   }
/*     */   
/*     */   public long getConnectionCount() {
/*  54 */     return this.connections;
/*     */   }
/*     */   
/*     */   public long registerReplicationConnection(ReplicationConnection conn, List<String> localMasterList, List<String> localSlaveList)
/*     */   {
/*     */     long currentConnectionId;
/*  60 */     synchronized (this) {
/*  61 */       if (!this.isInitialized) {
/*  62 */         if (localMasterList != null) {
/*  63 */           this.masterHostList.addAll(localMasterList);
/*     */         }
/*  65 */         if (localSlaveList != null) {
/*  66 */           this.slaveHostList.addAll(localSlaveList);
/*     */         }
/*  68 */         this.isInitialized = true;
/*     */       }
/*  70 */       currentConnectionId = ++this.connections;
/*  71 */       this.replicationConnections.put(Long.valueOf(currentConnectionId), conn);
/*     */     }
/*  73 */     this.activeConnections += 1L;
/*     */     
/*  75 */     return currentConnectionId;
/*     */   }
/*     */   
/*     */   public String getGroupName() {
/*  79 */     return this.groupName;
/*     */   }
/*     */   
/*     */   public Collection<String> getMasterHosts() {
/*  83 */     return this.masterHostList;
/*     */   }
/*     */   
/*     */   public Collection<String> getSlaveHosts() {
/*  87 */     return this.slaveHostList;
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
/*     */   public void addSlaveHost(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 105 */     if (this.slaveHostList.add(hostPortPair)) {
/* 106 */       this.slavesAdded += 1L;
/*     */       
/*     */ 
/* 109 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 110 */         c.addSlaveHost(hostPortPair);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleCloseConnection(ReplicationConnection conn) {
/* 116 */     this.replicationConnections.remove(Long.valueOf(conn.getConnectionGroupId()));
/* 117 */     this.activeConnections -= 1L;
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
/*     */   public void removeSlaveHost(String hostPortPair, boolean closeGently)
/*     */     throws SQLException
/*     */   {
/* 135 */     if (this.slaveHostList.remove(hostPortPair)) {
/* 136 */       this.slavesRemoved += 1L;
/*     */       
/*     */ 
/* 139 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 140 */         c.removeSlave(hostPortPair, closeGently);
/*     */       }
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
/*     */   public void promoteSlaveToMaster(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 160 */     if ((this.slaveHostList.remove(hostPortPair) | this.masterHostList.add(hostPortPair))) {
/* 161 */       this.slavesPromoted += 1L;
/*     */       
/* 163 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 164 */         c.promoteSlaveToMaster(hostPortPair);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeMasterHost(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 175 */     removeMasterHost(hostPortPair, true);
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
/*     */   public void removeMasterHost(String hostPortPair, boolean closeGently)
/*     */     throws SQLException
/*     */   {
/* 193 */     if (this.masterHostList.remove(hostPortPair))
/*     */     {
/* 195 */       for (ReplicationConnection c : this.replicationConnections.values()) {
/* 196 */         c.removeMasterHost(hostPortPair, closeGently);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getConnectionCountWithHostAsSlave(String hostPortPair) {
/* 202 */     int matched = 0;
/*     */     
/* 204 */     for (ReplicationConnection c : this.replicationConnections.values()) {
/* 205 */       if (c.isHostSlave(hostPortPair)) {
/* 206 */         matched++;
/*     */       }
/*     */     }
/* 209 */     return matched;
/*     */   }
/*     */   
/*     */   public int getConnectionCountWithHostAsMaster(String hostPortPair) {
/* 213 */     int matched = 0;
/*     */     
/* 215 */     for (ReplicationConnection c : this.replicationConnections.values()) {
/* 216 */       if (c.isHostMaster(hostPortPair)) {
/* 217 */         matched++;
/*     */       }
/*     */     }
/* 220 */     return matched;
/*     */   }
/*     */   
/*     */   public long getNumberOfSlavesAdded() {
/* 224 */     return this.slavesAdded;
/*     */   }
/*     */   
/*     */   public long getNumberOfSlavesRemoved() {
/* 228 */     return this.slavesRemoved;
/*     */   }
/*     */   
/*     */   public long getNumberOfSlavePromotions() {
/* 232 */     return this.slavesPromoted;
/*     */   }
/*     */   
/*     */   public long getTotalConnectionCount() {
/* 236 */     return this.connections;
/*     */   }
/*     */   
/*     */   public long getActiveConnectionCount() {
/* 240 */     return this.activeConnections;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 245 */     return "ReplicationConnectionGroup[groupName=" + this.groupName + ",masterHostList=" + this.masterHostList + ",slaveHostList=" + this.slaveHostList + "]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ReplicationConnectionGroup.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */