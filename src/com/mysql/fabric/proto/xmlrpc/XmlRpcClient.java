/*     */ package com.mysql.fabric.proto.xmlrpc;
/*     */ 
/*     */ import com.mysql.fabric.FabricCommunicationException;
/*     */ import com.mysql.fabric.FabricStateResponse;
/*     */ import com.mysql.fabric.Response;
/*     */ import com.mysql.fabric.Server;
/*     */ import com.mysql.fabric.ServerGroup;
/*     */ import com.mysql.fabric.ServerMode;
/*     */ import com.mysql.fabric.ServerRole;
/*     */ import com.mysql.fabric.ShardIndex;
/*     */ import com.mysql.fabric.ShardMapping;
/*     */ import com.mysql.fabric.ShardMappingFactory;
/*     */ import com.mysql.fabric.ShardTable;
/*     */ import com.mysql.fabric.ShardingType;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ 
/*     */ 
/*     */ public class XmlRpcClient
/*     */ {
/*     */   private static final String THREAT_REPORTER_NAME = "MySQL Connector/J";
/*     */   private static final String METHOD_DUMP_FABRIC_NODES = "dump.fabric_nodes";
/*     */   private static final String METHOD_DUMP_SERVERS = "dump.servers";
/*     */   private static final String METHOD_DUMP_SHARD_TABLES = "dump.shard_tables";
/*     */   private static final String METHOD_DUMP_SHARD_INDEX = "dump.shard_index";
/*     */   private static final String METHOD_DUMP_SHARD_MAPS = "dump.shard_maps";
/*     */   private static final String METHOD_SHARDING_LOOKUP_SERVERS = "sharding.lookup_servers";
/*     */   private static final String METHOD_SHARDING_CREATE_DEFINITION = "sharding.create_definition";
/*     */   private static final String METHOD_SHARDING_ADD_TABLE = "sharding.add_table";
/*     */   private static final String METHOD_SHARDING_ADD_SHARD = "sharding.add_shard";
/*     */   private static final String METHOD_GROUP_LOOKUP_GROUPS = "group.lookup_groups";
/*     */   private static final String METHOD_GROUP_CREATE = "group.create";
/*     */   private static final String METHOD_GROUP_ADD = "group.add";
/*     */   private static final String METHOD_GROUP_PROMOTE = "group.promote";
/*     */   private static final String METHOD_GROUP_DESTROY = "group.destroy";
/*     */   private static final String METHOD_THREAT_REPORT_ERROR = "threat.report_error";
/*     */   private static final String METHOD_THREAT_REPORT_FAILURE = "threat.report_failure";
/*     */   private static final String FIELD_MODE = "mode";
/*     */   private static final String FIELD_STATUS = "status";
/*     */   private static final String FIELD_HOST = "host";
/*     */   private static final String FIELD_PORT = "port";
/*     */   private static final String FIELD_ADDRESS = "address";
/*     */   private static final String FIELD_GROUP_ID = "group_id";
/*     */   private static final String FIELD_SERVER_UUID = "server_uuid";
/*     */   private static final String FIELD_WEIGHT = "weight";
/*     */   private static final String FIELD_SCHEMA_NAME = "schema_name";
/*     */   private static final String FIELD_TABLE_NAME = "table_name";
/*     */   private static final String FIELD_COLUMN_NAME = "column_name";
/*     */   private static final String FIELD_LOWER_BOUND = "lower_bound";
/*     */   private static final String FIELD_SHARD_ID = "shard_id";
/*     */   private static final String FIELD_MAPPING_ID = "mapping_id";
/*     */   private static final String FIELD_GLOBAL_GROUP_ID = "global_group_id";
/*     */   private static final String FIELD_TYPE_NAME = "type_name";
/*     */   private static final String FIELD_RESULT = "result";
/*     */   private XmlRpcMethodCaller methodCaller;
/*     */   
/*     */   public XmlRpcClient(String url, String username, String password)
/*     */     throws FabricCommunicationException
/*     */   {
/*  92 */     this.methodCaller = new InternalXmlRpcMethodCaller(url);
/*  93 */     if ((username != null) && (!"".equals(username)) && (password != null)) {
/*  94 */       this.methodCaller = new AuthenticatedXmlRpcMethodCaller(this.methodCaller, url, username, password);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Server unmarshallServer(Map<String, ?> serverData)
/*     */     throws FabricCommunicationException
/*     */   {
/*     */     try
/*     */     {
/*     */       int port;
/*     */       
/*     */       ServerMode mode;
/*     */       ServerRole role;
/*     */       String host;
/*     */       int port;
/* 110 */       if (Integer.class.equals(serverData.get("mode").getClass())) {
/* 111 */         ServerMode mode = ServerMode.getFromConstant((Integer)serverData.get("mode"));
/* 112 */         ServerRole role = ServerRole.getFromConstant((Integer)serverData.get("status"));
/* 113 */         String host = (String)serverData.get("host");
/* 114 */         port = ((Integer)serverData.get("port")).intValue();
/*     */       }
/*     */       else {
/* 117 */         mode = ServerMode.valueOf((String)serverData.get("mode"));
/* 118 */         role = ServerRole.valueOf((String)serverData.get("status"));
/* 119 */         String[] hostnameAndPort = ((String)serverData.get("address")).split(":");
/* 120 */         host = hostnameAndPort[0];
/* 121 */         port = Integer.valueOf(hostnameAndPort[1]).intValue();
/*     */       }
/* 123 */       return new Server((String)serverData.get("group_id"), (String)serverData.get("server_uuid"), host, port, mode, role, ((Double)serverData.get("weight")).doubleValue());
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 127 */       throw new FabricCommunicationException("Unable to parse server definition", ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static Set<Server> toServerSet(List<Map<String, ?>> l)
/*     */     throws FabricCommunicationException
/*     */   {
/* 135 */     Set<Server> servers = new HashSet();
/* 136 */     for (Map<String, ?> serverData : l) {
/* 137 */       servers.add(unmarshallServer(serverData));
/*     */     }
/* 139 */     return servers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Response errorSafeCallMethod(String methodName, Object[] args)
/*     */     throws FabricCommunicationException
/*     */   {
/* 149 */     List<?> responseData = this.methodCaller.call(methodName, args);
/* 150 */     Response response = new Response(responseData);
/* 151 */     if (response.getErrorMessage() != null) {
/* 152 */       throw new FabricCommunicationException("Call failed to method `" + methodName + "':\n" + response.getErrorMessage());
/*     */     }
/* 154 */     return response;
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<String> getFabricNames()
/*     */     throws FabricCommunicationException
/*     */   {
/* 161 */     Response resp = errorSafeCallMethod("dump.fabric_nodes", new Object[0]);
/* 162 */     Set<String> names = new HashSet();
/* 163 */     for (Map<String, ?> node : resp.getResultSet()) {
/* 164 */       names.add(node.get("host") + ":" + node.get("port"));
/*     */     }
/* 166 */     return names;
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<String> getGroupNames()
/*     */     throws FabricCommunicationException
/*     */   {
/* 173 */     Set<String> groupNames = new HashSet();
/* 174 */     for (Map<String, ?> row : errorSafeCallMethod("group.lookup_groups", null).getResultSet()) {
/* 175 */       groupNames.add((String)row.get("group_id"));
/*     */     }
/* 177 */     return groupNames;
/*     */   }
/*     */   
/*     */   public ServerGroup getServerGroup(String groupName) throws FabricCommunicationException {
/* 181 */     Set<ServerGroup> groups = (Set)getServerGroups(groupName).getData();
/* 182 */     if (groups.size() == 1) {
/* 183 */       return (ServerGroup)groups.iterator().next();
/*     */     }
/* 185 */     return null;
/*     */   }
/*     */   
/*     */   public Set<Server> getServersForKey(String tableName, int key) throws FabricCommunicationException {
/* 189 */     Response r = errorSafeCallMethod("sharding.lookup_servers", new Object[] { tableName, Integer.valueOf(key) });
/* 190 */     return toServerSet(r.getResultSet());
/*     */   }
/*     */   
/*     */ 
/*     */   public FabricStateResponse<Set<ServerGroup>> getServerGroups(String groupPattern)
/*     */     throws FabricCommunicationException
/*     */   {
/* 197 */     int version = 0;
/* 198 */     Response response = errorSafeCallMethod("dump.servers", new Object[] { Integer.valueOf(version), groupPattern });
/*     */     
/* 200 */     Map<String, Set<Server>> serversByGroupName = new HashMap();
/* 201 */     for (Map<String, ?> server : response.getResultSet()) {
/* 202 */       Server s = unmarshallServer(server);
/* 203 */       if (serversByGroupName.get(s.getGroupName()) == null) {
/* 204 */         serversByGroupName.put(s.getGroupName(), new HashSet());
/*     */       }
/* 206 */       ((Set)serversByGroupName.get(s.getGroupName())).add(s);
/*     */     }
/*     */     
/* 209 */     Set<ServerGroup> serverGroups = new HashSet();
/* 210 */     for (Entry<String, Set<Server>> entry : serversByGroupName.entrySet()) {
/* 211 */       ServerGroup g = new ServerGroup((String)entry.getKey(), (Set)entry.getValue());
/* 212 */       serverGroups.add(g);
/*     */     }
/* 214 */     return new FabricStateResponse(serverGroups, response.getTtl());
/*     */   }
/*     */   
/*     */   public FabricStateResponse<Set<ServerGroup>> getServerGroups() throws FabricCommunicationException {
/* 218 */     return getServerGroups("");
/*     */   }
/*     */   
/*     */   private FabricStateResponse<Set<ShardTable>> getShardTables(int shardMappingId) throws FabricCommunicationException {
/* 222 */     int version = 0;
/* 223 */     Object[] args = { Integer.valueOf(version), String.valueOf(shardMappingId) };
/* 224 */     Response tablesResponse = errorSafeCallMethod("dump.shard_tables", args);
/* 225 */     Set<ShardTable> tables = new HashSet();
/*     */     
/* 227 */     for (Map<String, ?> rawTable : tablesResponse.getResultSet()) {
/* 228 */       String database = (String)rawTable.get("schema_name");
/* 229 */       String table = (String)rawTable.get("table_name");
/* 230 */       String column = (String)rawTable.get("column_name");
/* 231 */       ShardTable st = new ShardTable(database, table, column);
/* 232 */       tables.add(st);
/*     */     }
/* 234 */     return new FabricStateResponse(tables, tablesResponse.getTtl());
/*     */   }
/*     */   
/*     */   private FabricStateResponse<Set<ShardIndex>> getShardIndices(int shardMappingId) throws FabricCommunicationException {
/* 238 */     int version = 0;
/* 239 */     Object[] args = { Integer.valueOf(version), String.valueOf(shardMappingId) };
/* 240 */     Response indexResponse = errorSafeCallMethod("dump.shard_index", args);
/* 241 */     Set<ShardIndex> indices = new HashSet();
/*     */     
/*     */ 
/* 244 */     for (Map<String, ?> rawIndexEntry : indexResponse.getResultSet()) {
/* 245 */       String bound = (String)rawIndexEntry.get("lower_bound");
/* 246 */       int shardId = ((Integer)rawIndexEntry.get("shard_id")).intValue();
/* 247 */       String groupName = (String)rawIndexEntry.get("group_id");
/* 248 */       ShardIndex si = new ShardIndex(bound, Integer.valueOf(shardId), groupName);
/* 249 */       indices.add(si);
/*     */     }
/* 251 */     return new FabricStateResponse(indices, indexResponse.getTtl());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FabricStateResponse<Set<ShardMapping>> getShardMappings(String shardMappingIdPattern)
/*     */     throws FabricCommunicationException
/*     */   {
/* 262 */     int version = 0;
/* 263 */     Object[] args = { Integer.valueOf(version), shardMappingIdPattern };
/* 264 */     Response mapsResponse = errorSafeCallMethod("dump.shard_maps", args);
/*     */     
/* 266 */     long minExpireTimeMillis = System.currentTimeMillis() + 1000 * mapsResponse.getTtl();
/*     */     
/*     */ 
/* 269 */     Set<ShardMapping> mappings = new HashSet();
/* 270 */     for (Map<String, ?> rawMapping : mapsResponse.getResultSet()) {
/* 271 */       int mappingId = ((Integer)rawMapping.get("mapping_id")).intValue();
/* 272 */       ShardingType shardingType = ShardingType.valueOf((String)rawMapping.get("type_name"));
/* 273 */       String globalGroupName = (String)rawMapping.get("global_group_id");
/*     */       
/* 275 */       FabricStateResponse<Set<ShardTable>> tables = getShardTables(mappingId);
/* 276 */       FabricStateResponse<Set<ShardIndex>> indices = getShardIndices(mappingId);
/*     */       
/* 278 */       if (tables.getExpireTimeMillis() < minExpireTimeMillis) {
/* 279 */         minExpireTimeMillis = tables.getExpireTimeMillis();
/*     */       }
/* 281 */       if (indices.getExpireTimeMillis() < minExpireTimeMillis) {
/* 282 */         minExpireTimeMillis = indices.getExpireTimeMillis();
/*     */       }
/*     */       
/* 285 */       ShardMapping m = new ShardMappingFactory().createShardMapping(mappingId, shardingType, globalGroupName, (Set)tables.getData(), (Set)indices.getData());
/* 286 */       mappings.add(m);
/*     */     }
/*     */     
/* 289 */     return new FabricStateResponse(mappings, minExpireTimeMillis);
/*     */   }
/*     */   
/*     */   public FabricStateResponse<Set<ShardMapping>> getShardMappings() throws FabricCommunicationException {
/* 293 */     return getShardMappings("");
/*     */   }
/*     */   
/*     */ 
/*     */   public void createGroup(String groupName)
/*     */     throws FabricCommunicationException
/*     */   {
/* 300 */     errorSafeCallMethod("group.create", new Object[] { groupName });
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroyGroup(String groupName)
/*     */     throws FabricCommunicationException
/*     */   {
/* 307 */     errorSafeCallMethod("group.destroy", new Object[] { groupName });
/*     */   }
/*     */   
/*     */ 
/*     */   public void createServerInGroup(String groupName, String hostname, int port)
/*     */     throws FabricCommunicationException
/*     */   {
/* 314 */     errorSafeCallMethod("group.add", new Object[] { groupName, hostname + ":" + port });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int createShardMapping(ShardingType type, String globalGroupName)
/*     */     throws FabricCommunicationException
/*     */   {
/* 327 */     Response r = errorSafeCallMethod("sharding.create_definition", new Object[] { type.toString(), globalGroupName });
/* 328 */     return ((Integer)((Map)r.getResultSet().get(0)).get("result")).intValue();
/*     */   }
/*     */   
/*     */   public void createShardTable(int shardMappingId, String database, String table, String column) throws FabricCommunicationException {
/* 332 */     errorSafeCallMethod("sharding.add_table", new Object[] { Integer.valueOf(shardMappingId), database + "." + table, column });
/*     */   }
/*     */   
/*     */   public void createShardIndex(int shardMappingId, String groupNameLowerBoundList) throws FabricCommunicationException {
/* 336 */     String status = "ENABLED";
/* 337 */     errorSafeCallMethod("sharding.add_shard", new Object[] { Integer.valueOf(shardMappingId), groupNameLowerBoundList, status });
/*     */   }
/*     */   
/*     */   public void addServerToGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
/* 341 */     errorSafeCallMethod("group.add", new Object[] { groupName, hostname + ":" + port });
/*     */   }
/*     */   
/*     */   public void promoteServerInGroup(String groupName, String hostname, int port) throws FabricCommunicationException {
/* 345 */     ServerGroup serverGroup = getServerGroup(groupName);
/* 346 */     for (Server s : serverGroup.getServers()) {
/* 347 */       if ((s.getHostname().equals(hostname)) && (s.getPort() == port)) {
/* 348 */         errorSafeCallMethod("group.promote", new Object[] { groupName, s.getUuid() });
/* 349 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportServerError(Server server, String errorDescription, boolean forceFaulty) throws FabricCommunicationException {
/* 355 */     String reporter = "MySQL Connector/J";
/* 356 */     String command = "threat.report_error";
/* 357 */     if (forceFaulty) {
/* 358 */       command = "threat.report_failure";
/*     */     }
/* 360 */     errorSafeCallMethod(command, new Object[] { server.getUuid(), reporter, errorDescription });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\XmlRpcClient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */