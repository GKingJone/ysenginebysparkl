/*     */ package com.mysql.fabric;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Server
/*     */   implements Comparable<Server>
/*     */ {
/*     */   private String groupName;
/*     */   
/*     */ 
/*     */ 
/*     */   private String uuid;
/*     */   
/*     */ 
/*     */ 
/*     */   private String hostname;
/*     */   
/*     */ 
/*     */ 
/*     */   private int port;
/*     */   
/*     */ 
/*     */ 
/*     */   private ServerMode mode;
/*     */   
/*     */ 
/*     */ 
/*     */   private ServerRole role;
/*     */   
/*     */ 
/*     */ 
/*     */   private double weight;
/*     */   
/*     */ 
/*     */ 
/*     */   public Server(String groupName, String uuid, String hostname, int port, ServerMode mode, ServerRole role, double weight)
/*     */   {
/*  39 */     this.groupName = groupName;
/*  40 */     this.uuid = uuid;
/*  41 */     this.hostname = hostname;
/*  42 */     this.port = port;
/*  43 */     this.mode = mode;
/*  44 */     this.role = role;
/*  45 */     this.weight = weight;
/*  46 */     assert ((uuid != null) && (!"".equals(uuid)));
/*  47 */     assert ((hostname != null) && (!"".equals(hostname)));
/*  48 */     assert (port > 0);
/*  49 */     assert (mode != null);
/*  50 */     assert (role != null);
/*  51 */     assert (weight > 0.0D);
/*     */   }
/*     */   
/*     */   public String getGroupName() {
/*  55 */     return this.groupName;
/*     */   }
/*     */   
/*     */   public String getUuid() {
/*  59 */     return this.uuid;
/*     */   }
/*     */   
/*     */   public String getHostname() {
/*  63 */     return this.hostname;
/*     */   }
/*     */   
/*     */   public int getPort() {
/*  67 */     return this.port;
/*     */   }
/*     */   
/*     */   public ServerMode getMode() {
/*  71 */     return this.mode;
/*     */   }
/*     */   
/*     */   public ServerRole getRole() {
/*  75 */     return this.role;
/*     */   }
/*     */   
/*     */   public double getWeight() {
/*  79 */     return this.weight;
/*     */   }
/*     */   
/*     */   public String getHostPortString() {
/*  83 */     return this.hostname + ":" + this.port;
/*     */   }
/*     */   
/*     */   public boolean isMaster() {
/*  87 */     return this.role == ServerRole.PRIMARY;
/*     */   }
/*     */   
/*     */   public boolean isSlave() {
/*  91 */     return (this.role == ServerRole.SECONDARY) || (this.role == ServerRole.SPARE);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*  96 */     return String.format("Server[%s, %s:%d, %s, %s, weight=%s]", new Object[] { this.uuid, this.hostname, Integer.valueOf(this.port), this.mode, this.role, Double.valueOf(this.weight) });
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 101 */     if (!(o instanceof Server)) {
/* 102 */       return false;
/*     */     }
/* 104 */     Server s = (Server)o;
/* 105 */     return s.getUuid().equals(getUuid());
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 110 */     return getUuid().hashCode();
/*     */   }
/*     */   
/*     */   public int compareTo(Server other) {
/* 114 */     return getUuid().compareTo(other.getUuid());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\Server.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */