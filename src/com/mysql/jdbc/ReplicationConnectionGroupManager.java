/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.jmx.ReplicationGroupManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class ReplicationConnectionGroupManager
/*     */ {
/*  34 */   private static HashMap<String, ReplicationConnectionGroup> GROUP_MAP = new HashMap();
/*     */   
/*  36 */   private static ReplicationGroupManager mbean = new ReplicationGroupManager();
/*     */   
/*  38 */   private static boolean hasRegisteredJmx = false;
/*     */   
/*     */   public static synchronized ReplicationConnectionGroup getConnectionGroupInstance(String groupName) {
/*  41 */     if (GROUP_MAP.containsKey(groupName)) {
/*  42 */       return (ReplicationConnectionGroup)GROUP_MAP.get(groupName);
/*     */     }
/*  44 */     ReplicationConnectionGroup group = new ReplicationConnectionGroup(groupName);
/*  45 */     GROUP_MAP.put(groupName, group);
/*  46 */     return group;
/*     */   }
/*     */   
/*     */   public static void registerJmx() throws SQLException {
/*  50 */     if (hasRegisteredJmx) {
/*  51 */       return;
/*     */     }
/*     */     
/*  54 */     mbean.registerJmx();
/*  55 */     hasRegisteredJmx = true;
/*     */   }
/*     */   
/*     */   public static ReplicationConnectionGroup getConnectionGroup(String groupName) {
/*  59 */     return (ReplicationConnectionGroup)GROUP_MAP.get(groupName);
/*     */   }
/*     */   
/*     */   public static Collection<ReplicationConnectionGroup> getGroupsMatching(String group) {
/*  63 */     if ((group == null) || (group.equals(""))) {
/*  64 */       Set<ReplicationConnectionGroup> s = new HashSet();
/*     */       
/*  66 */       s.addAll(GROUP_MAP.values());
/*  67 */       return s;
/*     */     }
/*  69 */     Set<ReplicationConnectionGroup> s = new HashSet();
/*  70 */     ReplicationConnectionGroup o = (ReplicationConnectionGroup)GROUP_MAP.get(group);
/*  71 */     if (o != null) {
/*  72 */       s.add(o);
/*     */     }
/*  74 */     return s;
/*     */   }
/*     */   
/*     */   public static void addSlaveHost(String group, String hostPortPair) throws SQLException {
/*  78 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/*  79 */     for (ReplicationConnectionGroup cg : s) {
/*  80 */       cg.addSlaveHost(hostPortPair);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void removeSlaveHost(String group, String hostPortPair) throws SQLException {
/*  85 */     removeSlaveHost(group, hostPortPair, true);
/*     */   }
/*     */   
/*     */   public static void removeSlaveHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/*  89 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/*  90 */     for (ReplicationConnectionGroup cg : s) {
/*  91 */       cg.removeSlaveHost(hostPortPair, closeGently);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void promoteSlaveToMaster(String group, String hostPortPair) throws SQLException {
/*  96 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/*  97 */     for (ReplicationConnectionGroup cg : s) {
/*  98 */       cg.promoteSlaveToMaster(hostPortPair);
/*     */     }
/*     */   }
/*     */   
/*     */   public static long getSlavePromotionCount(String group) throws SQLException {
/* 103 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 104 */     long promoted = 0L;
/* 105 */     for (ReplicationConnectionGroup cg : s) {
/* 106 */       long tmp = cg.getNumberOfSlavePromotions();
/* 107 */       if (tmp > promoted) {
/* 108 */         promoted = tmp;
/*     */       }
/*     */     }
/* 111 */     return promoted;
/*     */   }
/*     */   
/*     */   public static void removeMasterHost(String group, String hostPortPair) throws SQLException {
/* 115 */     removeMasterHost(group, hostPortPair, true);
/*     */   }
/*     */   
/*     */   public static void removeMasterHost(String group, String hostPortPair, boolean closeGently) throws SQLException {
/* 119 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 120 */     for (ReplicationConnectionGroup cg : s) {
/* 121 */       cg.removeMasterHost(hostPortPair, closeGently);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getRegisteredReplicationConnectionGroups() {
/* 126 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(null);
/* 127 */     StringBuilder sb = new StringBuilder();
/* 128 */     String sep = "";
/* 129 */     for (ReplicationConnectionGroup cg : s) {
/* 130 */       String group = cg.getGroupName();
/* 131 */       sb.append(sep);
/* 132 */       sb.append(group);
/* 133 */       sep = ",";
/*     */     }
/* 135 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static int getNumberOfMasterPromotion(String groupFilter) {
/* 139 */     int total = 0;
/* 140 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 141 */     for (ReplicationConnectionGroup cg : s) {
/* 142 */       total = (int)(total + cg.getNumberOfSlavePromotions());
/*     */     }
/* 144 */     return total;
/*     */   }
/*     */   
/*     */   public static int getConnectionCountWithHostAsSlave(String groupFilter, String hostPortPair) {
/* 148 */     int total = 0;
/* 149 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 150 */     for (ReplicationConnectionGroup cg : s) {
/* 151 */       total += cg.getConnectionCountWithHostAsSlave(hostPortPair);
/*     */     }
/* 153 */     return total;
/*     */   }
/*     */   
/*     */   public static int getConnectionCountWithHostAsMaster(String groupFilter, String hostPortPair) {
/* 157 */     int total = 0;
/* 158 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 159 */     for (ReplicationConnectionGroup cg : s) {
/* 160 */       total += cg.getConnectionCountWithHostAsMaster(hostPortPair);
/*     */     }
/* 162 */     return total;
/*     */   }
/*     */   
/*     */   public static Collection<String> getSlaveHosts(String groupFilter) {
/* 166 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 167 */     Collection<String> hosts = new ArrayList();
/* 168 */     for (ReplicationConnectionGroup cg : s) {
/* 169 */       hosts.addAll(cg.getSlaveHosts());
/*     */     }
/* 171 */     return hosts;
/*     */   }
/*     */   
/*     */   public static Collection<String> getMasterHosts(String groupFilter) {
/* 175 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(groupFilter);
/* 176 */     Collection<String> hosts = new ArrayList();
/* 177 */     for (ReplicationConnectionGroup cg : s) {
/* 178 */       hosts.addAll(cg.getMasterHosts());
/*     */     }
/* 180 */     return hosts;
/*     */   }
/*     */   
/*     */   public static long getTotalConnectionCount(String group) {
/* 184 */     long connections = 0L;
/* 185 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 186 */     for (ReplicationConnectionGroup cg : s) {
/* 187 */       connections += cg.getTotalConnectionCount();
/*     */     }
/* 189 */     return connections;
/*     */   }
/*     */   
/*     */   public static long getActiveConnectionCount(String group) {
/* 193 */     long connections = 0L;
/* 194 */     Collection<ReplicationConnectionGroup> s = getGroupsMatching(group);
/* 195 */     for (ReplicationConnectionGroup cg : s) {
/* 196 */       connections += cg.getActiveConnectionCount();
/*     */     }
/* 198 */     return connections;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ReplicationConnectionGroupManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */