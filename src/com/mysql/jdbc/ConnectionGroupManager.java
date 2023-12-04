/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
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
/*     */ public class ConnectionGroupManager
/*     */ {
/*  37 */   private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap();
/*     */   
/*  39 */   private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();
/*     */   
/*  41 */   private static boolean hasRegisteredJmx = false;
/*     */   
/*     */   public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName) {
/*  44 */     if (GROUP_MAP.containsKey(groupName)) {
/*  45 */       return (ConnectionGroup)GROUP_MAP.get(groupName);
/*     */     }
/*  47 */     ConnectionGroup group = new ConnectionGroup(groupName);
/*  48 */     GROUP_MAP.put(groupName, group);
/*  49 */     return group;
/*     */   }
/*     */   
/*     */   public static void registerJmx() throws SQLException {
/*  53 */     if (hasRegisteredJmx) {
/*  54 */       return;
/*     */     }
/*     */     
/*  57 */     mbean.registerJmx();
/*  58 */     hasRegisteredJmx = true;
/*     */   }
/*     */   
/*     */   public static ConnectionGroup getConnectionGroup(String groupName) {
/*  62 */     return (ConnectionGroup)GROUP_MAP.get(groupName);
/*     */   }
/*     */   
/*     */   private static Collection<ConnectionGroup> getGroupsMatching(String group) {
/*  66 */     if ((group == null) || (group.equals(""))) {
/*  67 */       Set<ConnectionGroup> s = new HashSet();
/*     */       
/*  69 */       s.addAll(GROUP_MAP.values());
/*  70 */       return s;
/*     */     }
/*  72 */     Set<ConnectionGroup> s = new HashSet();
/*  73 */     ConnectionGroup o = (ConnectionGroup)GROUP_MAP.get(group);
/*  74 */     if (o != null) {
/*  75 */       s.add(o);
/*     */     }
/*  77 */     return s;
/*     */   }
/*     */   
/*     */   public static void addHost(String group, String hostPortPair, boolean forExisting)
/*     */   {
/*  82 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/*  83 */     for (ConnectionGroup cg : s) {
/*  84 */       cg.addHost(hostPortPair, forExisting);
/*     */     }
/*     */   }
/*     */   
/*     */   public static int getActiveHostCount(String group) {
/*  89 */     Set<String> active = new HashSet();
/*  90 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/*  91 */     for (ConnectionGroup cg : s) {
/*  92 */       active.addAll(cg.getInitialHosts());
/*     */     }
/*  94 */     return active.size();
/*     */   }
/*     */   
/*     */   public static long getActiveLogicalConnectionCount(String group) {
/*  98 */     int count = 0;
/*  99 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 100 */     for (ConnectionGroup cg : s) {
/* 101 */       count = (int)(count + cg.getActiveLogicalConnectionCount());
/*     */     }
/* 103 */     return count;
/*     */   }
/*     */   
/*     */   public static long getActivePhysicalConnectionCount(String group) {
/* 107 */     int count = 0;
/* 108 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 109 */     for (ConnectionGroup cg : s) {
/* 110 */       count = (int)(count + cg.getActivePhysicalConnectionCount());
/*     */     }
/* 112 */     return count;
/*     */   }
/*     */   
/*     */   public static int getTotalHostCount(String group) {
/* 116 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 117 */     Set<String> hosts = new HashSet();
/* 118 */     for (ConnectionGroup cg : s) {
/* 119 */       hosts.addAll(cg.getInitialHosts());
/* 120 */       hosts.addAll(cg.getClosedHosts());
/*     */     }
/* 122 */     return hosts.size();
/*     */   }
/*     */   
/*     */   public static long getTotalLogicalConnectionCount(String group) {
/* 126 */     long count = 0L;
/* 127 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 128 */     for (ConnectionGroup cg : s) {
/* 129 */       count += cg.getTotalLogicalConnectionCount();
/*     */     }
/* 131 */     return count;
/*     */   }
/*     */   
/*     */   public static long getTotalPhysicalConnectionCount(String group) {
/* 135 */     long count = 0L;
/* 136 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 137 */     for (ConnectionGroup cg : s) {
/* 138 */       count += cg.getTotalPhysicalConnectionCount();
/*     */     }
/* 140 */     return count;
/*     */   }
/*     */   
/*     */   public static long getTotalTransactionCount(String group) {
/* 144 */     long count = 0L;
/* 145 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 146 */     for (ConnectionGroup cg : s) {
/* 147 */       count += cg.getTotalTransactionCount();
/*     */     }
/* 149 */     return count;
/*     */   }
/*     */   
/*     */   public static void removeHost(String group, String hostPortPair) throws SQLException {
/* 153 */     removeHost(group, hostPortPair, false);
/*     */   }
/*     */   
/*     */   public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
/* 157 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 158 */     for (ConnectionGroup cg : s) {
/* 159 */       cg.removeHost(host, removeExisting);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getActiveHostLists(String group) {
/* 164 */     Collection<ConnectionGroup> s = getGroupsMatching(group);
/* 165 */     Map<String, Integer> hosts = new HashMap();
/* 166 */     for (ConnectionGroup cg : s)
/*     */     {
/* 168 */       Collection<String> l = cg.getInitialHosts();
/* 169 */       for (String host : l) {
/* 170 */         Integer o = (Integer)hosts.get(host);
/* 171 */         if (o == null) {
/* 172 */           o = Integer.valueOf(1);
/*     */         } else {
/* 174 */           o = Integer.valueOf(o.intValue() + 1);
/*     */         }
/* 176 */         hosts.put(host, o);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 181 */     StringBuilder sb = new StringBuilder();
/* 182 */     String sep = "";
/* 183 */     for (String host : hosts.keySet()) {
/* 184 */       sb.append(sep);
/* 185 */       sb.append(host);
/* 186 */       sb.append('(');
/* 187 */       sb.append(hosts.get(host));
/* 188 */       sb.append(')');
/* 189 */       sep = ",";
/*     */     }
/* 191 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String getRegisteredConnectionGroups() {
/* 195 */     Collection<ConnectionGroup> s = getGroupsMatching(null);
/* 196 */     StringBuilder sb = new StringBuilder();
/* 197 */     String sep = "";
/* 198 */     for (ConnectionGroup cg : s) {
/* 199 */       String group = cg.getGroupName();
/* 200 */       sb.append(sep);
/* 201 */       sb.append(group);
/* 202 */       sep = ",";
/*     */     }
/* 204 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionGroupManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */