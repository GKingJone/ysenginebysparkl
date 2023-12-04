/*     */ package com.mysql.fabric;
/*     */ 
/*     */ import com.mysql.fabric.proto.xmlrpc.XmlRpcClient;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ public class FabricConnection
/*     */ {
/*     */   private XmlRpcClient client;
/*  36 */   private Map<String, ShardMapping> shardMappingsByTableName = new HashMap();
/*  37 */   private Map<String, ServerGroup> serverGroupsByName = new HashMap();
/*     */   private long shardMappingsExpiration;
/*     */   private long serverGroupsExpiration;
/*     */   
/*     */   public FabricConnection(String url, String username, String password) throws FabricCommunicationException {
/*  42 */     this.client = new XmlRpcClient(url, username, password);
/*  43 */     refreshState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FabricConnection(Set<String> urls, String username, String password)
/*     */     throws FabricCommunicationException
/*     */   {
/*  53 */     throw new UnsupportedOperationException("Multiple connections not supported.");
/*     */   }
/*     */   
/*     */   public String getInstanceUuid() {
/*  57 */     return null;
/*     */   }
/*     */   
/*     */   public int getVersion() {
/*  61 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int refreshState()
/*     */     throws FabricCommunicationException
/*     */   {
/*  68 */     FabricStateResponse<Set<ServerGroup>> serverGroups = this.client.getServerGroups();
/*  69 */     FabricStateResponse<Set<ShardMapping>> shardMappings = this.client.getShardMappings();
/*  70 */     this.serverGroupsExpiration = serverGroups.getExpireTimeMillis();
/*  71 */     this.shardMappingsExpiration = shardMappings.getExpireTimeMillis();
/*     */     
/*  73 */     for (ServerGroup g : (Set)serverGroups.getData()) {
/*  74 */       this.serverGroupsByName.put(g.getName(), g);
/*     */     }
/*     */     
/*  77 */     for (Iterator i$ = ((Set)shardMappings.getData()).iterator(); i$.hasNext();) { m = (ShardMapping)i$.next();
/*     */       
/*  79 */       for (ShardTable t : m.getShardTables()) {
/*  80 */         this.shardMappingsByTableName.put(t.getDatabase() + "." + t.getTable(), m);
/*     */       }
/*     */     }
/*     */     ShardMapping m;
/*  84 */     return 0;
/*     */   }
/*     */   
/*     */   public ServerGroup getServerGroup(String serverGroupName) throws FabricCommunicationException {
/*  88 */     if (isStateExpired()) {
/*  89 */       refreshState();
/*     */     }
/*  91 */     return (ServerGroup)this.serverGroupsByName.get(serverGroupName);
/*     */   }
/*     */   
/*     */   public ShardMapping getShardMapping(String database, String table) throws FabricCommunicationException {
/*  95 */     if (isStateExpired()) {
/*  96 */       refreshState();
/*     */     }
/*  98 */     return (ShardMapping)this.shardMappingsByTableName.get(database + "." + table);
/*     */   }
/*     */   
/*     */   public boolean isStateExpired() {
/* 102 */     return (System.currentTimeMillis() > this.shardMappingsExpiration) || (System.currentTimeMillis() > this.serverGroupsExpiration);
/*     */   }
/*     */   
/*     */   public Set<String> getFabricHosts() {
/* 106 */     return null;
/*     */   }
/*     */   
/*     */   public XmlRpcClient getClient() {
/* 110 */     return this.client;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\FabricConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */