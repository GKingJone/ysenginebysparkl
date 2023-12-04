/*      */ package com.mysql.fabric.jdbc;
/*      */ 
/*      */ import com.mysql.fabric.FabricCommunicationException;
/*      */ import com.mysql.fabric.FabricConnection;
/*      */ import com.mysql.fabric.Server;
/*      */ import com.mysql.fabric.ServerGroup;
/*      */ import com.mysql.fabric.ShardMapping;
/*      */ import com.mysql.fabric.proto.xmlrpc.XmlRpcClient;
/*      */ import com.mysql.jdbc.Buffer;
/*      */ import com.mysql.jdbc.CachedResultSetMetaData;
/*      */ import com.mysql.jdbc.Connection;
/*      */ import com.mysql.jdbc.ConnectionProperties;
/*      */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*      */ import com.mysql.jdbc.ExceptionInterceptor;
/*      */ import com.mysql.jdbc.Extension;
/*      */ import com.mysql.jdbc.Field;
/*      */ import com.mysql.jdbc.MySQLConnection;
/*      */ import com.mysql.jdbc.MysqlIO;
/*      */ import com.mysql.jdbc.ReplicationConnection;
/*      */ import com.mysql.jdbc.ReplicationConnectionGroup;
/*      */ import com.mysql.jdbc.ReplicationConnectionGroupManager;
/*      */ import com.mysql.jdbc.ReplicationConnectionProxy;
/*      */ import com.mysql.jdbc.ResultSetInternalMethods;
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.ServerPreparedStatement;
/*      */ import com.mysql.jdbc.SingleByteCharsetConverter;
/*      */ import com.mysql.jdbc.StatementImpl;
/*      */ import com.mysql.jdbc.StatementInterceptorV2;
/*      */ import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FabricMySQLConnectionProxy
/*      */   extends ConnectionPropertiesImpl
/*      */   implements FabricMySQLConnection, FabricMySQLConnectionProperties
/*      */ {
/*      */   private static final long serialVersionUID = 5845485979107347258L;
/*      */   private Log log;
/*      */   protected FabricConnection fabricConnection;
/*   93 */   protected boolean closed = false;
/*      */   
/*   95 */   protected boolean transactionInProgress = false;
/*      */   
/*      */ 
/*   98 */   protected Map<ServerGroup, ReplicationConnection> serverConnections = new HashMap();
/*      */   
/*      */ 
/*      */   protected ReplicationConnection currentConnection;
/*      */   
/*      */ 
/*      */   protected String shardKey;
/*      */   
/*      */   protected String shardTable;
/*      */   
/*      */   protected String serverGroupName;
/*      */   
/*  110 */   protected Set<String> queryTables = new HashSet();
/*      */   
/*      */   protected ServerGroup serverGroup;
/*      */   
/*      */   protected String host;
/*      */   
/*      */   protected String port;
/*      */   
/*      */   protected String username;
/*      */   protected String password;
/*      */   protected String database;
/*      */   protected ShardMapping shardMapping;
/*  122 */   protected boolean readOnly = false;
/*  123 */   protected boolean autoCommit = true;
/*  124 */   protected int transactionIsolation = 4;
/*      */   
/*      */   private String fabricShardKey;
/*      */   private String fabricShardTable;
/*      */   private String fabricServerGroup;
/*      */   private String fabricProtocol;
/*      */   private String fabricUsername;
/*      */   private String fabricPassword;
/*  132 */   private boolean reportErrors = false;
/*      */   
/*      */ 
/*      */ 
/*  136 */   private static final Set<String> replConnGroupLocks = Collections.synchronizedSet(new HashSet());
/*      */   
/*      */   public FabricMySQLConnectionProxy(Properties props) throws SQLException
/*      */   {
/*  140 */     this.fabricShardKey = props.getProperty("fabricShardKey");
/*  141 */     this.fabricShardTable = props.getProperty("fabricShardTable");
/*  142 */     this.fabricServerGroup = props.getProperty("fabricServerGroup");
/*  143 */     this.fabricProtocol = props.getProperty("fabricProtocol");
/*  144 */     this.fabricUsername = props.getProperty("fabricUsername");
/*  145 */     this.fabricPassword = props.getProperty("fabricPassword");
/*  146 */     this.reportErrors = Boolean.valueOf(props.getProperty("fabricReportErrors")).booleanValue();
/*  147 */     props.remove("fabricShardKey");
/*  148 */     props.remove("fabricShardTable");
/*  149 */     props.remove("fabricServerGroup");
/*  150 */     props.remove("fabricProtocol");
/*  151 */     props.remove("fabricUsername");
/*  152 */     props.remove("fabricPassword");
/*  153 */     props.remove("fabricReportErrors");
/*      */     
/*  155 */     this.host = props.getProperty("HOST");
/*  156 */     this.port = props.getProperty("PORT");
/*  157 */     this.username = props.getProperty("user");
/*  158 */     this.password = props.getProperty("password");
/*  159 */     this.database = props.getProperty("DBNAME");
/*  160 */     if (this.username == null) {
/*  161 */       this.username = "";
/*      */     }
/*  163 */     if (this.password == null) {
/*  164 */       this.password = "";
/*      */     }
/*      */     
/*      */ 
/*  168 */     String exceptionInterceptors = props.getProperty("exceptionInterceptors");
/*  169 */     if ((exceptionInterceptors == null) || ("null".equals("exceptionInterceptors"))) {
/*  170 */       exceptionInterceptors = "";
/*      */     } else {
/*  172 */       exceptionInterceptors = exceptionInterceptors + ",";
/*      */     }
/*  174 */     exceptionInterceptors = exceptionInterceptors + "com.mysql.fabric.jdbc.ErrorReportingExceptionInterceptor";
/*  175 */     props.setProperty("exceptionInterceptors", exceptionInterceptors);
/*      */     
/*  177 */     initializeProperties(props);
/*      */     
/*      */ 
/*  180 */     if ((this.fabricServerGroup != null) && (this.fabricShardTable != null)) {
/*  181 */       throw SQLError.createSQLException("Server group and shard table are mutually exclusive. Only one may be provided.", "08004", null, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  186 */       String url = this.fabricProtocol + "://" + this.host + ":" + this.port;
/*  187 */       this.fabricConnection = new FabricConnection(url, this.fabricUsername, this.fabricPassword);
/*      */     } catch (FabricCommunicationException ex) {
/*  189 */       throw SQLError.createSQLException("Unable to establish connection to the Fabric server", "08004", ex, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  194 */     this.log = LogFactory.getLogger(getLogger(), "FabricMySQLConnectionProxy", null);
/*      */     
/*  196 */     setShardTable(this.fabricShardTable);
/*  197 */     setShardKey(this.fabricShardKey);
/*      */     
/*  199 */     setServerGroupName(this.fabricServerGroup);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   synchronized SQLException interceptException(SQLException sqlEx, Connection conn, String groupName, String hostname, String portNumber)
/*      */     throws FabricCommunicationException
/*      */   {
/*  216 */     if ((sqlEx.getSQLState() != null) && (!sqlEx.getSQLState().startsWith("08")) && (!MySQLNonTransientConnectionException.class.isAssignableFrom(sqlEx.getClass())))
/*      */     {
/*  218 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  222 */     Server currentServer = this.serverGroup.getServer(hostname + ":" + portNumber);
/*      */     
/*      */ 
/*  225 */     if (currentServer == null) {
/*  226 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  230 */     if (this.reportErrors) {
/*  231 */       this.fabricConnection.getClient().reportServerError(currentServer, sqlEx.toString(), true);
/*      */     }
/*      */     
/*      */ 
/*  235 */     if (replConnGroupLocks.add(this.serverGroup.getName())) {
/*      */       try {
/*      */         SQLException localSQLException1;
/*      */         try {
/*  239 */           this.fabricConnection.refreshState();
/*  240 */           setCurrentServerGroup(this.serverGroup.getName());
/*      */         } catch (SQLException ex) {
/*  242 */           return SQLError.createSQLException("Unable to refresh Fabric state. Failover impossible", "08006", ex, null);
/*      */         }
/*      */         
/*      */         try
/*      */         {
/*  247 */           syncGroupServersToReplicationConnectionGroup(ReplicationConnectionGroupManager.getConnectionGroup(groupName));
/*      */         } catch (SQLException ex) {
/*  249 */           return ex;
/*      */         }
/*      */       } finally {
/*  252 */         replConnGroupLocks.remove(this.serverGroup.getName());
/*      */       }
/*      */     } else {
/*  255 */       return SQLError.createSQLException("Fabric state syncing already in progress in another thread.", "08006", sqlEx, null);
/*      */     }
/*      */     
/*  258 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private void refreshStateIfNecessary()
/*      */     throws SQLException
/*      */   {
/*  265 */     if (this.fabricConnection.isStateExpired()) {
/*      */       try {
/*  267 */         this.fabricConnection.refreshState();
/*      */       } catch (FabricCommunicationException ex) {
/*  269 */         throw SQLError.createSQLException("Unable to establish connection to the Fabric server", "08004", ex, getExceptionInterceptor(), this);
/*      */       }
/*      */       
/*  272 */       if (this.serverGroup != null) {
/*  273 */         setCurrentServerGroup(this.serverGroup.getName());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setShardKey(String shardKey)
/*      */     throws SQLException
/*      */   {
/*  282 */     ensureNoTransactionInProgress();
/*      */     
/*  284 */     this.currentConnection = null;
/*      */     
/*  286 */     if (shardKey != null) {
/*  287 */       if (this.serverGroupName != null) {
/*  288 */         throw SQLError.createSQLException("Shard key cannot be provided when server group is chosen directly.", "S1009", null, getExceptionInterceptor(), this);
/*      */       }
/*  290 */       if (this.shardTable == null) {
/*  291 */         throw SQLError.createSQLException("Shard key cannot be provided without a shard table.", "S1009", null, getExceptionInterceptor(), this);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  296 */       setCurrentServerGroup(this.shardMapping.getGroupNameForKey(shardKey));
/*  297 */     } else if (this.shardTable != null) {
/*  298 */       setCurrentServerGroup(this.shardMapping.getGlobalGroupName());
/*      */     }
/*  300 */     this.shardKey = shardKey;
/*      */   }
/*      */   
/*      */   public String getShardKey() {
/*  304 */     return this.shardKey;
/*      */   }
/*      */   
/*      */   public void setShardTable(String shardTable) throws SQLException {
/*  308 */     ensureNoTransactionInProgress();
/*      */     
/*  310 */     this.currentConnection = null;
/*      */     
/*  312 */     if (this.serverGroupName != null) {
/*  313 */       throw SQLError.createSQLException("Server group and shard table are mutually exclusive. Only one may be provided.", "S1009", null, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */ 
/*  317 */     this.shardKey = null;
/*  318 */     this.serverGroup = null;
/*  319 */     this.shardTable = shardTable;
/*  320 */     if (shardTable == null) {
/*  321 */       this.shardMapping = null;
/*      */     }
/*      */     else {
/*  324 */       String table = shardTable;
/*  325 */       String db = this.database;
/*  326 */       if (shardTable.contains(".")) {
/*  327 */         String[] pair = shardTable.split("\\.");
/*  328 */         table = pair[0];
/*  329 */         db = pair[1];
/*      */       }
/*      */       try {
/*  332 */         this.shardMapping = this.fabricConnection.getShardMapping(db, table);
/*  333 */         if (this.shardMapping == null) {
/*  334 */           throw SQLError.createSQLException("Shard mapping not found for table `" + shardTable + "'", "S1009", null, getExceptionInterceptor(), this);
/*      */         }
/*      */         
/*      */ 
/*  338 */         setCurrentServerGroup(this.shardMapping.getGlobalGroupName());
/*      */       }
/*      */       catch (FabricCommunicationException ex) {
/*  341 */         throw SQLError.createSQLException("Fabric communication failure.", "08S01", ex, getExceptionInterceptor(), this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public String getShardTable()
/*      */   {
/*  348 */     return this.shardTable;
/*      */   }
/*      */   
/*      */   public void setServerGroupName(String serverGroupName) throws SQLException {
/*  352 */     ensureNoTransactionInProgress();
/*      */     
/*  354 */     this.currentConnection = null;
/*      */     
/*      */ 
/*  357 */     if (serverGroupName != null) {
/*  358 */       setCurrentServerGroup(serverGroupName);
/*      */     }
/*      */     
/*  361 */     this.serverGroupName = serverGroupName;
/*      */   }
/*      */   
/*      */   public String getServerGroupName() {
/*  365 */     return this.serverGroupName;
/*      */   }
/*      */   
/*      */   public void clearServerSelectionCriteria() throws SQLException {
/*  369 */     ensureNoTransactionInProgress();
/*  370 */     this.shardTable = null;
/*  371 */     this.shardKey = null;
/*  372 */     this.serverGroupName = null;
/*  373 */     this.serverGroup = null;
/*  374 */     this.queryTables.clear();
/*  375 */     this.currentConnection = null;
/*      */   }
/*      */   
/*      */   public ServerGroup getCurrentServerGroup() {
/*  379 */     return this.serverGroup;
/*      */   }
/*      */   
/*      */   public void clearQueryTables() throws SQLException {
/*  383 */     ensureNoTransactionInProgress();
/*      */     
/*  385 */     this.currentConnection = null;
/*      */     
/*  387 */     this.queryTables.clear();
/*  388 */     setShardTable(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addQueryTable(String tableName)
/*      */     throws SQLException
/*      */   {
/*  400 */     ensureNoTransactionInProgress();
/*      */     
/*  402 */     this.currentConnection = null;
/*      */     
/*      */     try
/*      */     {
/*  406 */       if (this.shardMapping == null) {
/*  407 */         if (this.fabricConnection.getShardMapping(this.database, tableName) != null) {
/*  408 */           setShardTable(tableName);
/*      */         }
/*      */       } else {
/*  411 */         ShardMapping mappingForTableName = this.fabricConnection.getShardMapping(this.database, tableName);
/*  412 */         if ((mappingForTableName != null) && (!mappingForTableName.equals(this.shardMapping))) {
/*  413 */           throw SQLError.createSQLException("Cross-shard query not allowed", "S1009", null, getExceptionInterceptor(), this);
/*      */         }
/*      */       }
/*      */       
/*  417 */       this.queryTables.add(tableName);
/*      */     } catch (FabricCommunicationException ex) {
/*  419 */       throw SQLError.createSQLException("Fabric communication failure.", "08S01", ex, getExceptionInterceptor(), this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<String> getQueryTables()
/*      */   {
/*  428 */     return this.queryTables;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setCurrentServerGroup(String serverGroupName)
/*      */     throws SQLException
/*      */   {
/*  435 */     this.serverGroup = null;
/*      */     try
/*      */     {
/*  438 */       this.serverGroup = this.fabricConnection.getServerGroup(serverGroupName);
/*      */     } catch (FabricCommunicationException ex) {
/*  440 */       throw SQLError.createSQLException("Fabric communication failure.", "08S01", ex, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */ 
/*  444 */     if (this.serverGroup == null) {
/*  445 */       throw SQLError.createSQLException("Cannot find server group: `" + serverGroupName + "'", "S1009", null, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  450 */     ReplicationConnectionGroup replConnGroup = ReplicationConnectionGroupManager.getConnectionGroup(serverGroupName);
/*  451 */     if ((replConnGroup != null) && 
/*  452 */       (replConnGroupLocks.add(this.serverGroup.getName()))) {
/*      */       try {
/*  454 */         syncGroupServersToReplicationConnectionGroup(replConnGroup);
/*      */       } finally {
/*  456 */         replConnGroupLocks.remove(this.serverGroup.getName());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected MySQLConnection getActiveMySQLConnection()
/*      */     throws SQLException
/*      */   {
/*  476 */     ReplicationConnection c = (ReplicationConnection)getActiveConnection();
/*  477 */     MySQLConnection mc = (MySQLConnection)c.getCurrentConnection();
/*  478 */     return mc;
/*      */   }
/*      */   
/*      */   protected MySQLConnection getActiveMySQLConnectionPassive() {
/*      */     try {
/*  483 */       return getActiveMySQLConnection();
/*      */     } catch (SQLException ex) {
/*  485 */       throw new IllegalStateException("Unable to determine active connection", ex);
/*      */     }
/*      */   }
/*      */   
/*      */   protected Connection getActiveConnectionPassive() {
/*      */     try {
/*  491 */       return getActiveConnection();
/*      */     } catch (SQLException ex) {
/*  493 */       throw new IllegalStateException("Unable to determine active connection", ex);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void syncGroupServersToReplicationConnectionGroup(ReplicationConnectionGroup replConnGroup)
/*      */     throws SQLException
/*      */   {
/*  505 */     String currentMasterString = null;
/*  506 */     if (replConnGroup.getMasterHosts().size() == 1) {
/*  507 */       currentMasterString = (String)replConnGroup.getMasterHosts().iterator().next();
/*      */     }
/*      */     
/*  510 */     if ((currentMasterString != null) && ((this.serverGroup.getMaster() == null) || (!currentMasterString.equals(this.serverGroup.getMaster().getHostPortString()))))
/*      */     {
/*      */       try
/*      */       {
/*  514 */         replConnGroup.removeMasterHost(currentMasterString, false);
/*      */       }
/*      */       catch (SQLException ex) {
/*  517 */         getLog().logWarn("Unable to remove master: " + currentMasterString, ex);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  522 */     Server newMaster = this.serverGroup.getMaster();
/*  523 */     if ((newMaster != null) && (replConnGroup.getMasterHosts().size() == 0)) {
/*  524 */       getLog().logInfo("Changing master for group '" + replConnGroup.getGroupName() + "' to: " + newMaster);
/*      */       try {
/*  526 */         if (!replConnGroup.getSlaveHosts().contains(newMaster.getHostPortString())) {
/*  527 */           replConnGroup.addSlaveHost(newMaster.getHostPortString());
/*      */         }
/*  529 */         replConnGroup.promoteSlaveToMaster(newMaster.getHostPortString());
/*      */       } catch (SQLException ex) {
/*  531 */         throw SQLError.createSQLException("Unable to promote new master '" + newMaster.toString() + "'", ex.getSQLState(), ex, null);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  537 */     for (Server s : this.serverGroup.getServers()) {
/*  538 */       if (s.isSlave()) {
/*      */         try
/*      */         {
/*  541 */           replConnGroup.addSlaveHost(s.getHostPortString());
/*      */         }
/*      */         catch (SQLException ex) {
/*  544 */           getLog().logWarn("Unable to add slave: " + s.toString(), ex);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  549 */     for (String hostPortString : replConnGroup.getSlaveHosts()) {
/*  550 */       Server fabServer = this.serverGroup.getServer(hostPortString);
/*  551 */       if ((fabServer == null) || (!fabServer.isSlave())) {
/*      */         try {
/*  553 */           replConnGroup.removeSlaveHost(hostPortString, true);
/*      */         }
/*      */         catch (SQLException ex) {
/*  556 */           getLog().logWarn("Unable to remove slave: " + hostPortString, ex);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected Connection getActiveConnection() throws SQLException {
/*  563 */     if (!this.transactionInProgress) {
/*  564 */       refreshStateIfNecessary();
/*      */     }
/*      */     
/*  567 */     if (this.currentConnection != null) {
/*  568 */       return this.currentConnection;
/*      */     }
/*      */     
/*  571 */     if (getCurrentServerGroup() == null) {
/*  572 */       throw SQLError.createSQLException("No server group selected.", "08004", null, getExceptionInterceptor(), this);
/*      */     }
/*      */     
/*      */ 
/*  576 */     this.currentConnection = ((ReplicationConnection)this.serverConnections.get(this.serverGroup));
/*  577 */     if (this.currentConnection != null) {
/*  578 */       return this.currentConnection;
/*      */     }
/*      */     
/*      */ 
/*  582 */     List<String> masterHost = new ArrayList();
/*  583 */     List<String> slaveHosts = new ArrayList();
/*  584 */     for (Server s : this.serverGroup.getServers()) {
/*  585 */       if (s.isMaster()) {
/*  586 */         masterHost.add(s.getHostPortString());
/*  587 */       } else if (s.isSlave()) {
/*  588 */         slaveHosts.add(s.getHostPortString());
/*      */       }
/*      */     }
/*  591 */     Properties info = exposeAsProperties(null);
/*  592 */     ReplicationConnectionGroup replConnGroup = ReplicationConnectionGroupManager.getConnectionGroup(this.serverGroup.getName());
/*  593 */     if ((replConnGroup != null) && 
/*  594 */       (replConnGroupLocks.add(this.serverGroup.getName()))) {
/*      */       try {
/*  596 */         syncGroupServersToReplicationConnectionGroup(replConnGroup);
/*      */       } finally {
/*  598 */         replConnGroupLocks.remove(this.serverGroup.getName());
/*      */       }
/*      */     }
/*      */     
/*  602 */     info.put("replicationConnectionGroup", this.serverGroup.getName());
/*  603 */     info.setProperty("user", this.username);
/*  604 */     info.setProperty("password", this.password);
/*  605 */     info.setProperty("DBNAME", getCatalog());
/*  606 */     info.setProperty("connectionAttributes", "fabricHaGroup:" + this.serverGroup.getName());
/*  607 */     info.setProperty("retriesAllDown", "1");
/*  608 */     info.setProperty("allowMasterDownConnections", "true");
/*  609 */     info.setProperty("allowSlaveDownConnections", "true");
/*  610 */     info.setProperty("readFromMasterWhenNoSlaves", "true");
/*  611 */     this.currentConnection = ReplicationConnectionProxy.createProxyInstance(masterHost, info, slaveHosts, info);
/*  612 */     this.serverConnections.put(this.serverGroup, this.currentConnection);
/*      */     
/*  614 */     this.currentConnection.setProxy(this);
/*  615 */     this.currentConnection.setAutoCommit(this.autoCommit);
/*  616 */     this.currentConnection.setReadOnly(this.readOnly);
/*  617 */     this.currentConnection.setTransactionIsolation(this.transactionIsolation);
/*  618 */     return this.currentConnection;
/*      */   }
/*      */   
/*      */   private void ensureOpen() throws SQLException {
/*  622 */     if (this.closed) {
/*  623 */       throw SQLError.createSQLException("No operations allowed after connection closed.", "08003", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   private void ensureNoTransactionInProgress() throws SQLException
/*      */   {
/*  629 */     ensureOpen();
/*  630 */     if ((this.transactionInProgress) && (!this.autoCommit)) {
/*  631 */       throw SQLError.createSQLException("Not allow while a transaction is active.", "25000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  640 */     this.closed = true;
/*  641 */     for (Connection c : this.serverConnections.values()) {
/*      */       try {
/*  643 */         c.close();
/*      */       }
/*      */       catch (SQLException ex) {}
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/*  650 */     return this.closed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isValid(int timeout)
/*      */     throws SQLException
/*      */   {
/*  658 */     return !this.closed;
/*      */   }
/*      */   
/*      */   public void setReadOnly(boolean readOnly) throws SQLException {
/*  662 */     this.readOnly = readOnly;
/*  663 */     for (ReplicationConnection conn : this.serverConnections.values()) {
/*  664 */       conn.setReadOnly(readOnly);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isReadOnly() throws SQLException {
/*  669 */     return this.readOnly;
/*      */   }
/*      */   
/*      */   public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
/*  673 */     return this.readOnly;
/*      */   }
/*      */   
/*      */   public void setCatalog(String catalog) throws SQLException {
/*  677 */     this.database = catalog;
/*  678 */     for (Connection c : this.serverConnections.values()) {
/*  679 */       c.setCatalog(catalog);
/*      */     }
/*      */   }
/*      */   
/*      */   public String getCatalog() {
/*  684 */     return this.database;
/*      */   }
/*      */   
/*      */   public void rollback() throws SQLException {
/*  688 */     getActiveConnection().rollback();
/*  689 */     transactionCompleted();
/*      */   }
/*      */   
/*      */   public void rollback(Savepoint savepoint) throws SQLException {
/*  693 */     getActiveConnection().rollback();
/*  694 */     transactionCompleted();
/*      */   }
/*      */   
/*      */   public void commit() throws SQLException {
/*  698 */     getActiveConnection().commit();
/*  699 */     transactionCompleted();
/*      */   }
/*      */   
/*      */   public void setAutoCommit(boolean autoCommit) throws SQLException {
/*  703 */     this.autoCommit = autoCommit;
/*  704 */     for (Connection c : this.serverConnections.values()) {
/*  705 */       c.setAutoCommit(this.autoCommit);
/*      */     }
/*      */   }
/*      */   
/*      */   public void transactionBegun() throws SQLException {
/*  710 */     if (!this.autoCommit) {
/*  711 */       this.transactionInProgress = true;
/*      */     }
/*      */   }
/*      */   
/*      */   public void transactionCompleted() throws SQLException {
/*  716 */     this.transactionInProgress = false;
/*  717 */     refreshStateIfNecessary();
/*      */   }
/*      */   
/*      */   public boolean getAutoCommit() {
/*  721 */     return this.autoCommit;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public MySQLConnection getLoadBalanceSafeProxy()
/*      */   {
/*  729 */     return getMultiHostSafeProxy();
/*      */   }
/*      */   
/*      */   public MySQLConnection getMultiHostSafeProxy() {
/*  733 */     return getActiveMySQLConnectionPassive();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/*  740 */     this.transactionIsolation = level;
/*  741 */     for (Connection c : this.serverConnections.values()) {
/*  742 */       c.setTransactionIsolation(level);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
/*  747 */     for (Connection c : this.serverConnections.values()) {
/*  748 */       c.setTypeMap(map);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setHoldability(int holdability) throws SQLException {
/*  753 */     for (Connection c : this.serverConnections.values()) {
/*  754 */       c.setHoldability(holdability);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setProxy(MySQLConnection proxy) {}
/*      */   
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/*  765 */     return getActiveConnection().setSavepoint();
/*      */   }
/*      */   
/*      */   public Savepoint setSavepoint(String name) throws SQLException {
/*  769 */     this.transactionInProgress = true;
/*  770 */     return getActiveConnection().setSavepoint(name);
/*      */   }
/*      */   
/*      */   public void releaseSavepoint(Savepoint savepoint) {}
/*      */   
/*      */   public CallableStatement prepareCall(String sql) throws SQLException
/*      */   {
/*  777 */     transactionBegun();
/*  778 */     return getActiveConnection().prepareCall(sql);
/*      */   }
/*      */   
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  782 */     transactionBegun();
/*  783 */     return getActiveConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  787 */     transactionBegun();
/*  788 */     return getActiveConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql) throws SQLException {
/*  792 */     transactionBegun();
/*  793 */     return getActiveConnection().prepareStatement(sql);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
/*  797 */     transactionBegun();
/*  798 */     return getActiveConnection().prepareStatement(sql, autoGeneratedKeys);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
/*  802 */     transactionBegun();
/*  803 */     return getActiveConnection().prepareStatement(sql, columnIndexes);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  807 */     transactionBegun();
/*  808 */     return getActiveConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  812 */     transactionBegun();
/*  813 */     return getActiveConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */   
/*      */   public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
/*  817 */     transactionBegun();
/*  818 */     return getActiveConnection().prepareStatement(sql, columnNames);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
/*  822 */     transactionBegun();
/*  823 */     return getActiveConnection().clientPrepareStatement(sql);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*  827 */     transactionBegun();
/*  828 */     return getActiveConnection().clientPrepareStatement(sql, autoGenKeyIndex);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  832 */     transactionBegun();
/*  833 */     return getActiveConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*  837 */     transactionBegun();
/*  838 */     return getActiveConnection().clientPrepareStatement(sql, autoGenKeyIndexes);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*  843 */     transactionBegun();
/*  844 */     return getActiveConnection().clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */   
/*      */   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*  848 */     transactionBegun();
/*  849 */     return getActiveConnection().clientPrepareStatement(sql, autoGenKeyColNames);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
/*  853 */     transactionBegun();
/*  854 */     return getActiveConnection().serverPrepareStatement(sql);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
/*  858 */     transactionBegun();
/*  859 */     return getActiveConnection().serverPrepareStatement(sql, autoGenKeyIndex);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
/*  863 */     transactionBegun();
/*  864 */     return getActiveConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/*  869 */     transactionBegun();
/*  870 */     return getActiveConnection().serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
/*  874 */     transactionBegun();
/*  875 */     return getActiveConnection().serverPrepareStatement(sql, autoGenKeyIndexes);
/*      */   }
/*      */   
/*      */   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
/*  879 */     transactionBegun();
/*  880 */     return getActiveConnection().serverPrepareStatement(sql, autoGenKeyColNames);
/*      */   }
/*      */   
/*      */   public java.sql.Statement createStatement() throws SQLException {
/*  884 */     transactionBegun();
/*  885 */     return getActiveConnection().createStatement();
/*      */   }
/*      */   
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
/*  889 */     transactionBegun();
/*  890 */     return getActiveConnection().createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
/*  894 */     transactionBegun();
/*  895 */     return getActiveConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*      */   }
/*      */   
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException
/*      */   {
/*  900 */     return getActiveMySQLConnection().execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);
/*      */   }
/*      */   
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/*  906 */     return getActiveMySQLConnection().execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, isBatch);
/*      */   }
/*      */   
/*      */   public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException
/*      */   {
/*  911 */     return getActiveMySQLConnection().extractSqlFromPacket(possibleSqlQuery, queryPacket, endOfQueryPacketPosition);
/*      */   }
/*      */   
/*      */   public StringBuilder generateConnectionCommentBlock(StringBuilder buf) {
/*  915 */     return getActiveMySQLConnectionPassive().generateConnectionCommentBlock(buf);
/*      */   }
/*      */   
/*      */   public MysqlIO getIO() throws SQLException {
/*  919 */     return getActiveMySQLConnection().getIO();
/*      */   }
/*      */   
/*      */   public Calendar getCalendarInstanceForSessionOrNew() {
/*  923 */     return getActiveMySQLConnectionPassive().getCalendarInstanceForSessionOrNew();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public String getServerCharacterEncoding()
/*      */   {
/*  931 */     return getServerCharset();
/*      */   }
/*      */   
/*      */   public String getServerCharset() {
/*  935 */     return getActiveMySQLConnectionPassive().getServerCharset();
/*      */   }
/*      */   
/*      */   public TimeZone getServerTimezoneTZ() {
/*  939 */     return getActiveMySQLConnectionPassive().getServerTimezoneTZ();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */     throws SQLException
/*      */   {
/*  947 */     return getActiveConnection().versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/*  954 */     return getActiveConnectionPassive().supportsIsolationLevel();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers()
/*      */   {
/*  961 */     return getActiveConnectionPassive().supportsQuotedIdentifiers();
/*      */   }
/*      */   
/*      */   public DatabaseMetaData getMetaData() throws SQLException {
/*  965 */     return getActiveConnection().getMetaData();
/*      */   }
/*      */   
/*      */   public String getCharacterSetMetadata() {
/*  969 */     return getActiveMySQLConnectionPassive().getCharacterSetMetadata();
/*      */   }
/*      */   
/*      */   public java.sql.Statement getMetadataSafeStatement() throws SQLException {
/*  973 */     return getActiveMySQLConnection().getMetadataSafeStatement();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isWrapperFor(Class<?> iface)
/*      */   {
/*  982 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T unwrap(Class<T> iface)
/*      */   {
/*  989 */     return null;
/*      */   }
/*      */   
/*      */   public void unSafeStatementInterceptors() throws SQLException
/*      */   {}
/*      */   
/*      */   public boolean supportsTransactions()
/*      */   {
/*  997 */     return true;
/*      */   }
/*      */   
/*      */   public boolean isRunningOnJDK13() {
/* 1001 */     return false;
/*      */   }
/*      */   
/*      */   public void createNewIO(boolean isForReconnect) throws SQLException {
/* 1005 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */   public void dumpTestcaseQuery(String query) {}
/*      */   
/*      */   public void abortInternal()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public boolean isServerLocal()
/*      */     throws SQLException
/*      */   {
/* 1018 */     return false;
/*      */   }
/*      */   
/*      */   public void shutdownServer() throws SQLException {
/* 1022 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public void clearHasTriedMaster() {}
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasTriedMaster()
/*      */   {
/* 1032 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isInGlobalTx()
/*      */   {
/* 1037 */     return false;
/*      */   }
/*      */   
/*      */   public void setInGlobalTx(boolean flag)
/*      */   {
/* 1042 */     throw new RuntimeException("Global transactions not supported.");
/*      */   }
/*      */   
/*      */   public void changeUser(String userName, String newPassword) throws SQLException {
/* 1046 */     throw SQLError.createSQLException("User change not allowed.", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFabricShardKey(String value)
/*      */   {
/* 1053 */     this.fabricShardKey = value;
/*      */   }
/*      */   
/*      */   public String getFabricShardKey() {
/* 1057 */     return this.fabricShardKey;
/*      */   }
/*      */   
/*      */   public void setFabricShardTable(String value) {
/* 1061 */     this.fabricShardTable = value;
/*      */   }
/*      */   
/*      */   public String getFabricShardTable() {
/* 1065 */     return this.fabricShardTable;
/*      */   }
/*      */   
/*      */   public void setFabricServerGroup(String value) {
/* 1069 */     this.fabricServerGroup = value;
/*      */   }
/*      */   
/*      */   public String getFabricServerGroup() {
/* 1073 */     return this.fabricServerGroup;
/*      */   }
/*      */   
/*      */   public void setFabricProtocol(String value) {
/* 1077 */     this.fabricProtocol = value;
/*      */   }
/*      */   
/*      */   public String getFabricProtocol() {
/* 1081 */     return this.fabricProtocol;
/*      */   }
/*      */   
/*      */   public void setFabricUsername(String value) {
/* 1085 */     this.fabricUsername = value;
/*      */   }
/*      */   
/*      */   public String getFabricUsername() {
/* 1089 */     return this.fabricUsername;
/*      */   }
/*      */   
/*      */   public void setFabricPassword(String value) {
/* 1093 */     this.fabricPassword = value;
/*      */   }
/*      */   
/*      */   public String getFabricPassword() {
/* 1097 */     return this.fabricPassword;
/*      */   }
/*      */   
/*      */   public void setFabricReportErrors(boolean value) {
/* 1101 */     this.reportErrors = value;
/*      */   }
/*      */   
/*      */   public boolean getFabricReportErrors() {
/* 1105 */     return this.reportErrors;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property)
/*      */   {
/* 1113 */     super.setAllowLoadLocalInfile(property);
/* 1114 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1115 */       cp.setAllowLoadLocalInfile(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAllowMultiQueries(boolean property)
/*      */   {
/* 1121 */     super.setAllowMultiQueries(property);
/* 1122 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1123 */       cp.setAllowMultiQueries(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAllowNanAndInf(boolean flag)
/*      */   {
/* 1129 */     super.setAllowNanAndInf(flag);
/* 1130 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1131 */       cp.setAllowNanAndInf(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/* 1137 */     super.setAllowUrlInLocalInfile(flag);
/* 1138 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1139 */       cp.setAllowUrlInLocalInfile(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/* 1145 */     super.setAlwaysSendSetIsolation(flag);
/* 1146 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1147 */       cp.setAlwaysSendSetIsolation(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoDeserialize(boolean flag)
/*      */   {
/* 1153 */     super.setAutoDeserialize(flag);
/* 1154 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1155 */       cp.setAutoDeserialize(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoGenerateTestcaseScript(boolean flag)
/*      */   {
/* 1161 */     super.setAutoGenerateTestcaseScript(flag);
/* 1162 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1163 */       cp.setAutoGenerateTestcaseScript(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoReconnect(boolean flag)
/*      */   {
/* 1169 */     super.setAutoReconnect(flag);
/* 1170 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1171 */       cp.setAutoReconnect(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/* 1177 */     super.setAutoReconnectForConnectionPools(property);
/* 1178 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1179 */       cp.setAutoReconnectForConnectionPools(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoReconnectForPools(boolean flag)
/*      */   {
/* 1185 */     super.setAutoReconnectForPools(flag);
/* 1186 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1187 */       cp.setAutoReconnectForPools(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setBlobSendChunkSize(String value) throws SQLException
/*      */   {
/* 1193 */     super.setBlobSendChunkSize(value);
/* 1194 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1195 */       cp.setBlobSendChunkSize(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCacheCallableStatements(boolean flag)
/*      */   {
/* 1201 */     super.setCacheCallableStatements(flag);
/* 1202 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1203 */       cp.setCacheCallableStatements(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCachePreparedStatements(boolean flag)
/*      */   {
/* 1209 */     super.setCachePreparedStatements(flag);
/* 1210 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1211 */       cp.setCachePreparedStatements(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCacheResultSetMetadata(boolean property)
/*      */   {
/* 1217 */     super.setCacheResultSetMetadata(property);
/* 1218 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1219 */       cp.setCacheResultSetMetadata(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCacheServerConfiguration(boolean flag)
/*      */   {
/* 1225 */     super.setCacheServerConfiguration(flag);
/* 1226 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1227 */       cp.setCacheServerConfiguration(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCallableStatementCacheSize(int size) throws SQLException
/*      */   {
/* 1233 */     super.setCallableStatementCacheSize(size);
/* 1234 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1235 */       cp.setCallableStatementCacheSize(size);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/* 1241 */     super.setCapitalizeDBMDTypes(property);
/* 1242 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1243 */       cp.setCapitalizeDBMDTypes(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCapitalizeTypeNames(boolean flag)
/*      */   {
/* 1249 */     super.setCapitalizeTypeNames(flag);
/* 1250 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1251 */       cp.setCapitalizeTypeNames(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCharacterEncoding(String encoding)
/*      */   {
/* 1257 */     super.setCharacterEncoding(encoding);
/* 1258 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1259 */       cp.setCharacterEncoding(encoding);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCharacterSetResults(String characterSet)
/*      */   {
/* 1265 */     super.setCharacterSetResults(characterSet);
/* 1266 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1267 */       cp.setCharacterSetResults(characterSet);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClobberStreamingResults(boolean flag)
/*      */   {
/* 1273 */     super.setClobberStreamingResults(flag);
/* 1274 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1275 */       cp.setClobberStreamingResults(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClobCharacterEncoding(String encoding)
/*      */   {
/* 1281 */     super.setClobCharacterEncoding(encoding);
/* 1282 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1283 */       cp.setClobCharacterEncoding(encoding);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setConnectionCollation(String collation)
/*      */   {
/* 1289 */     super.setConnectionCollation(collation);
/* 1290 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1291 */       cp.setConnectionCollation(collation);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setConnectTimeout(int timeoutMs) throws SQLException
/*      */   {
/* 1297 */     super.setConnectTimeout(timeoutMs);
/* 1298 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1299 */       cp.setConnectTimeout(timeoutMs);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 1305 */     super.setContinueBatchOnError(property);
/* 1306 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1307 */       cp.setContinueBatchOnError(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCreateDatabaseIfNotExist(boolean flag)
/*      */   {
/* 1313 */     super.setCreateDatabaseIfNotExist(flag);
/* 1314 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1315 */       cp.setCreateDatabaseIfNotExist(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDefaultFetchSize(int n) throws SQLException
/*      */   {
/* 1321 */     super.setDefaultFetchSize(n);
/* 1322 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1323 */       cp.setDefaultFetchSize(n);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDetectServerPreparedStmts(boolean property)
/*      */   {
/* 1329 */     super.setDetectServerPreparedStmts(property);
/* 1330 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1331 */       cp.setDetectServerPreparedStmts(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDontTrackOpenResources(boolean flag)
/*      */   {
/* 1337 */     super.setDontTrackOpenResources(flag);
/* 1338 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1339 */       cp.setDontTrackOpenResources(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDumpQueriesOnException(boolean flag)
/*      */   {
/* 1345 */     super.setDumpQueriesOnException(flag);
/* 1346 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1347 */       cp.setDumpQueriesOnException(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDynamicCalendars(boolean flag)
/*      */   {
/* 1353 */     super.setDynamicCalendars(flag);
/* 1354 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1355 */       cp.setDynamicCalendars(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setElideSetAutoCommits(boolean flag)
/*      */   {
/* 1361 */     super.setElideSetAutoCommits(flag);
/* 1362 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1363 */       cp.setElideSetAutoCommits(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEmptyStringsConvertToZero(boolean flag)
/*      */   {
/* 1369 */     super.setEmptyStringsConvertToZero(flag);
/* 1370 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1371 */       cp.setEmptyStringsConvertToZero(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEmulateLocators(boolean property)
/*      */   {
/* 1377 */     super.setEmulateLocators(property);
/* 1378 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1379 */       cp.setEmulateLocators(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/* 1385 */     super.setEmulateUnsupportedPstmts(flag);
/* 1386 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1387 */       cp.setEmulateUnsupportedPstmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEnablePacketDebug(boolean flag)
/*      */   {
/* 1393 */     super.setEnablePacketDebug(flag);
/* 1394 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1395 */       cp.setEnablePacketDebug(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEncoding(String property)
/*      */   {
/* 1401 */     super.setEncoding(property);
/* 1402 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1403 */       cp.setEncoding(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 1409 */     super.setExplainSlowQueries(flag);
/* 1410 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1411 */       cp.setExplainSlowQueries(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setFailOverReadOnly(boolean flag)
/*      */   {
/* 1417 */     super.setFailOverReadOnly(flag);
/* 1418 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1419 */       cp.setFailOverReadOnly(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/* 1425 */     super.setGatherPerformanceMetrics(flag);
/* 1426 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1427 */       cp.setGatherPerformanceMetrics(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/* 1433 */     super.setHoldResultsOpenOverStatementClose(flag);
/* 1434 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1435 */       cp.setHoldResultsOpenOverStatementClose(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setIgnoreNonTxTables(boolean property)
/*      */   {
/* 1441 */     super.setIgnoreNonTxTables(property);
/* 1442 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1443 */       cp.setIgnoreNonTxTables(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setInitialTimeout(int property) throws SQLException
/*      */   {
/* 1449 */     super.setInitialTimeout(property);
/* 1450 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1451 */       cp.setInitialTimeout(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setIsInteractiveClient(boolean property)
/*      */   {
/* 1457 */     super.setIsInteractiveClient(property);
/* 1458 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1459 */       cp.setIsInteractiveClient(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/* 1465 */     super.setJdbcCompliantTruncation(flag);
/* 1466 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1467 */       cp.setJdbcCompliantTruncation(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLocatorFetchBufferSize(String value) throws SQLException
/*      */   {
/* 1473 */     super.setLocatorFetchBufferSize(value);
/* 1474 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1475 */       cp.setLocatorFetchBufferSize(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLogger(String property)
/*      */   {
/* 1481 */     super.setLogger(property);
/* 1482 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1483 */       cp.setLogger(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 1489 */     super.setLoggerClassName(className);
/* 1490 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1491 */       cp.setLoggerClassName(className);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 1497 */     super.setLogSlowQueries(flag);
/* 1498 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1499 */       cp.setLogSlowQueries(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMaintainTimeStats(boolean flag)
/*      */   {
/* 1505 */     super.setMaintainTimeStats(flag);
/* 1506 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1507 */       cp.setMaintainTimeStats(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException
/*      */   {
/* 1513 */     super.setMaxQuerySizeToLog(sizeInBytes);
/* 1514 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1515 */       cp.setMaxQuerySizeToLog(sizeInBytes);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMaxReconnects(int property) throws SQLException
/*      */   {
/* 1521 */     super.setMaxReconnects(property);
/* 1522 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1523 */       cp.setMaxReconnects(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMaxRows(int property) throws SQLException
/*      */   {
/* 1529 */     super.setMaxRows(property);
/* 1530 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1531 */       cp.setMaxRows(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMetadataCacheSize(int value) throws SQLException
/*      */   {
/* 1537 */     super.setMetadataCacheSize(value);
/* 1538 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1539 */       cp.setMetadataCacheSize(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 1545 */     super.setNoDatetimeStringSync(flag);
/* 1546 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1547 */       cp.setNoDatetimeStringSync(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNullCatalogMeansCurrent(boolean value)
/*      */   {
/* 1553 */     super.setNullCatalogMeansCurrent(value);
/* 1554 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1555 */       cp.setNullCatalogMeansCurrent(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNullNamePatternMatchesAll(boolean value)
/*      */   {
/* 1561 */     super.setNullNamePatternMatchesAll(value);
/* 1562 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1563 */       cp.setNullNamePatternMatchesAll(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPacketDebugBufferSize(int size) throws SQLException
/*      */   {
/* 1569 */     super.setPacketDebugBufferSize(size);
/* 1570 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1571 */       cp.setPacketDebugBufferSize(size);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 1577 */     super.setParanoid(property);
/* 1578 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1579 */       cp.setParanoid(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 1585 */     super.setPedantic(property);
/* 1586 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1587 */       cp.setPedantic(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSize(int cacheSize) throws SQLException
/*      */   {
/* 1593 */     super.setPreparedStatementCacheSize(cacheSize);
/* 1594 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1595 */       cp.setPreparedStatementCacheSize(cacheSize);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException
/*      */   {
/* 1601 */     super.setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/* 1602 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1603 */       cp.setPreparedStatementCacheSqlLimit(cacheSqlLimit);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 1609 */     super.setProfileSql(property);
/* 1610 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1611 */       cp.setProfileSql(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 1617 */     super.setProfileSQL(flag);
/* 1618 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1619 */       cp.setProfileSQL(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 1625 */     super.setPropertiesTransform(value);
/* 1626 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1627 */       cp.setPropertiesTransform(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setQueriesBeforeRetryMaster(int property) throws SQLException
/*      */   {
/* 1633 */     super.setQueriesBeforeRetryMaster(property);
/* 1634 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1635 */       cp.setQueriesBeforeRetryMaster(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 1641 */     super.setReconnectAtTxEnd(property);
/* 1642 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1643 */       cp.setReconnectAtTxEnd(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 1649 */     super.setRelaxAutoCommit(property);
/* 1650 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1651 */       cp.setRelaxAutoCommit(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setReportMetricsIntervalMillis(int millis) throws SQLException
/*      */   {
/* 1657 */     super.setReportMetricsIntervalMillis(millis);
/* 1658 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1659 */       cp.setReportMetricsIntervalMillis(millis);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 1665 */     super.setRequireSSL(property);
/* 1666 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1667 */       cp.setRequireSSL(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRetainStatementAfterResultSetClose(boolean flag)
/*      */   {
/* 1673 */     super.setRetainStatementAfterResultSetClose(flag);
/* 1674 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1675 */       cp.setRetainStatementAfterResultSetClose(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 1681 */     super.setRollbackOnPooledClose(flag);
/* 1682 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1683 */       cp.setRollbackOnPooledClose(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 1689 */     super.setRoundRobinLoadBalance(flag);
/* 1690 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1691 */       cp.setRoundRobinLoadBalance(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 1697 */     super.setRunningCTS13(flag);
/* 1698 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1699 */       cp.setRunningCTS13(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSecondsBeforeRetryMaster(int property) throws SQLException
/*      */   {
/* 1705 */     super.setSecondsBeforeRetryMaster(property);
/* 1706 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1707 */       cp.setSecondsBeforeRetryMaster(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 1713 */     super.setServerTimezone(property);
/* 1714 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1715 */       cp.setServerTimezone(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 1721 */     super.setSessionVariables(variables);
/* 1722 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1723 */       cp.setSessionVariables(variables);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdMillis(int millis) throws SQLException
/*      */   {
/* 1729 */     super.setSlowQueryThresholdMillis(millis);
/* 1730 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1731 */       cp.setSlowQueryThresholdMillis(millis);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 1737 */     super.setSocketFactoryClassName(property);
/* 1738 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1739 */       cp.setSocketFactoryClassName(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSocketTimeout(int property) throws SQLException
/*      */   {
/* 1745 */     super.setSocketTimeout(property);
/* 1746 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1747 */       cp.setSocketTimeout(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 1753 */     super.setStrictFloatingPoint(property);
/* 1754 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1755 */       cp.setStrictFloatingPoint(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 1761 */     super.setStrictUpdates(property);
/* 1762 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1763 */       cp.setStrictUpdates(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 1769 */     super.setTinyInt1isBit(flag);
/* 1770 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1771 */       cp.setTinyInt1isBit(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 1777 */     super.setTraceProtocol(flag);
/* 1778 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1779 */       cp.setTraceProtocol(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTransformedBitIsBoolean(boolean flag)
/*      */   {
/* 1785 */     super.setTransformedBitIsBoolean(flag);
/* 1786 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1787 */       cp.setTransformedBitIsBoolean(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 1793 */     super.setUseCompression(property);
/* 1794 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1795 */       cp.setUseCompression(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 1801 */     super.setUseFastIntParsing(flag);
/* 1802 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1803 */       cp.setUseFastIntParsing(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 1809 */     super.setUseHostsInPrivileges(property);
/* 1810 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1811 */       cp.setUseHostsInPrivileges(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseInformationSchema(boolean flag)
/*      */   {
/* 1817 */     super.setUseInformationSchema(flag);
/* 1818 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1819 */       cp.setUseInformationSchema(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 1825 */     super.setUseLocalSessionState(flag);
/* 1826 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1827 */       cp.setUseLocalSessionState(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 1833 */     super.setUseOldUTF8Behavior(flag);
/* 1834 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1835 */       cp.setUseOldUTF8Behavior(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 1841 */     super.setUseOnlyServerErrorMessages(flag);
/* 1842 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1843 */       cp.setUseOnlyServerErrorMessages(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 1849 */     super.setUseReadAheadInput(flag);
/* 1850 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1851 */       cp.setUseReadAheadInput(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 1857 */     super.setUseServerPreparedStmts(flag);
/* 1858 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1859 */       cp.setUseServerPreparedStmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 1865 */     super.setUseSqlStateCodes(flag);
/* 1866 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1867 */       cp.setUseSqlStateCodes(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 1873 */     super.setUseSSL(property);
/* 1874 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1875 */       cp.setUseSSL(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 1881 */     super.setUseStreamLengthsInPrepStmts(property);
/* 1882 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1883 */       cp.setUseStreamLengthsInPrepStmts(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 1889 */     super.setUseTimezone(property);
/* 1890 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1891 */       cp.setUseTimezone(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 1897 */     super.setUseUltraDevWorkAround(property);
/* 1898 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1899 */       cp.setUseUltraDevWorkAround(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 1905 */     super.setUseUnbufferedInput(flag);
/* 1906 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1907 */       cp.setUseUnbufferedInput(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 1913 */     super.setUseUnicode(flag);
/* 1914 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1915 */       cp.setUseUnicode(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 1921 */     super.setUseUsageAdvisor(useUsageAdvisorFlag);
/* 1922 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1923 */       cp.setUseUsageAdvisor(useUsageAdvisorFlag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 1929 */     super.setYearIsDateType(flag);
/* 1930 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1931 */       cp.setYearIsDateType(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 1937 */     super.setZeroDateTimeBehavior(behavior);
/* 1938 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1939 */       cp.setZeroDateTimeBehavior(behavior);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseCursorFetch(boolean flag)
/*      */   {
/* 1945 */     super.setUseCursorFetch(flag);
/* 1946 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1947 */       cp.setUseCursorFetch(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag)
/*      */   {
/* 1953 */     super.setOverrideSupportsIntegrityEnhancementFacility(flag);
/* 1954 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1955 */       cp.setOverrideSupportsIntegrityEnhancementFacility(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNoTimezoneConversionForTimeType(boolean flag)
/*      */   {
/* 1961 */     super.setNoTimezoneConversionForTimeType(flag);
/* 1962 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1963 */       cp.setNoTimezoneConversionForTimeType(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseJDBCCompliantTimezoneShift(boolean flag)
/*      */   {
/* 1969 */     super.setUseJDBCCompliantTimezoneShift(flag);
/* 1970 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1971 */       cp.setUseJDBCCompliantTimezoneShift(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoClosePStmtStreams(boolean flag)
/*      */   {
/* 1977 */     super.setAutoClosePStmtStreams(flag);
/* 1978 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1979 */       cp.setAutoClosePStmtStreams(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setProcessEscapeCodesForPrepStmts(boolean flag)
/*      */   {
/* 1985 */     super.setProcessEscapeCodesForPrepStmts(flag);
/* 1986 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1987 */       cp.setProcessEscapeCodesForPrepStmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseGmtMillisForDatetimes(boolean flag)
/*      */   {
/* 1993 */     super.setUseGmtMillisForDatetimes(flag);
/* 1994 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 1995 */       cp.setUseGmtMillisForDatetimes(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDumpMetadataOnColumnNotFound(boolean flag)
/*      */   {
/* 2001 */     super.setDumpMetadataOnColumnNotFound(flag);
/* 2002 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2003 */       cp.setDumpMetadataOnColumnNotFound(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setResourceId(String resourceId)
/*      */   {
/* 2009 */     super.setResourceId(resourceId);
/* 2010 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2011 */       cp.setResourceId(resourceId);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRewriteBatchedStatements(boolean flag)
/*      */   {
/* 2017 */     super.setRewriteBatchedStatements(flag);
/* 2018 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2019 */       cp.setRewriteBatchedStatements(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads)
/*      */   {
/* 2025 */     super.setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/* 2026 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2027 */       cp.setJdbcCompliantTruncationForReads(jdbcCompliantTruncationForReads);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseJvmCharsetConverters(boolean flag)
/*      */   {
/* 2033 */     super.setUseJvmCharsetConverters(flag);
/* 2034 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2035 */       cp.setUseJvmCharsetConverters(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPinGlobalTxToPhysicalConnection(boolean flag)
/*      */   {
/* 2041 */     super.setPinGlobalTxToPhysicalConnection(flag);
/* 2042 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2043 */       cp.setPinGlobalTxToPhysicalConnection(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setGatherPerfMetrics(boolean flag)
/*      */   {
/* 2049 */     super.setGatherPerfMetrics(flag);
/* 2050 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2051 */       cp.setGatherPerfMetrics(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUltraDevHack(boolean flag)
/*      */   {
/* 2057 */     super.setUltraDevHack(flag);
/* 2058 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2059 */       cp.setUltraDevHack(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setInteractiveClient(boolean property)
/*      */   {
/* 2065 */     super.setInteractiveClient(property);
/* 2066 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2067 */       cp.setInteractiveClient(property);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSocketFactory(String name)
/*      */   {
/* 2073 */     super.setSocketFactory(name);
/* 2074 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2075 */       cp.setSocketFactory(name);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseServerPrepStmts(boolean flag)
/*      */   {
/* 2081 */     super.setUseServerPrepStmts(flag);
/* 2082 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2083 */       cp.setUseServerPrepStmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCacheCallableStmts(boolean flag)
/*      */   {
/* 2089 */     super.setCacheCallableStmts(flag);
/* 2090 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2091 */       cp.setCacheCallableStmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCachePrepStmts(boolean flag)
/*      */   {
/* 2097 */     super.setCachePrepStmts(flag);
/* 2098 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2099 */       cp.setCachePrepStmts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCallableStmtCacheSize(int cacheSize) throws SQLException
/*      */   {
/* 2105 */     super.setCallableStmtCacheSize(cacheSize);
/* 2106 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2107 */       cp.setCallableStmtCacheSize(cacheSize);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPrepStmtCacheSize(int cacheSize) throws SQLException
/*      */   {
/* 2113 */     super.setPrepStmtCacheSize(cacheSize);
/* 2114 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2115 */       cp.setPrepStmtCacheSize(cacheSize);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException
/*      */   {
/* 2121 */     super.setPrepStmtCacheSqlLimit(sqlLimit);
/* 2122 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2123 */       cp.setPrepStmtCacheSqlLimit(sqlLimit);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNoAccessToProcedureBodies(boolean flag)
/*      */   {
/* 2129 */     super.setNoAccessToProcedureBodies(flag);
/* 2130 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2131 */       cp.setNoAccessToProcedureBodies(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseOldAliasMetadataBehavior(boolean flag)
/*      */   {
/* 2137 */     super.setUseOldAliasMetadataBehavior(flag);
/* 2138 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2139 */       cp.setUseOldAliasMetadataBehavior(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStorePassword(String value)
/*      */   {
/* 2145 */     super.setClientCertificateKeyStorePassword(value);
/* 2146 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2147 */       cp.setClientCertificateKeyStorePassword(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStoreType(String value)
/*      */   {
/* 2153 */     super.setClientCertificateKeyStoreType(value);
/* 2154 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2155 */       cp.setClientCertificateKeyStoreType(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClientCertificateKeyStoreUrl(String value)
/*      */   {
/* 2161 */     super.setClientCertificateKeyStoreUrl(value);
/* 2162 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2163 */       cp.setClientCertificateKeyStoreUrl(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStorePassword(String value)
/*      */   {
/* 2169 */     super.setTrustCertificateKeyStorePassword(value);
/* 2170 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2171 */       cp.setTrustCertificateKeyStorePassword(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStoreType(String value)
/*      */   {
/* 2177 */     super.setTrustCertificateKeyStoreType(value);
/* 2178 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2179 */       cp.setTrustCertificateKeyStoreType(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTrustCertificateKeyStoreUrl(String value)
/*      */   {
/* 2185 */     super.setTrustCertificateKeyStoreUrl(value);
/* 2186 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2187 */       cp.setTrustCertificateKeyStoreUrl(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseSSPSCompatibleTimezoneShift(boolean flag)
/*      */   {
/* 2193 */     super.setUseSSPSCompatibleTimezoneShift(flag);
/* 2194 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2195 */       cp.setUseSSPSCompatibleTimezoneShift(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTreatUtilDateAsTimestamp(boolean flag)
/*      */   {
/* 2201 */     super.setTreatUtilDateAsTimestamp(flag);
/* 2202 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2203 */       cp.setTreatUtilDateAsTimestamp(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseFastDateParsing(boolean flag)
/*      */   {
/* 2209 */     super.setUseFastDateParsing(flag);
/* 2210 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2211 */       cp.setUseFastDateParsing(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLocalSocketAddress(String address)
/*      */   {
/* 2217 */     super.setLocalSocketAddress(address);
/* 2218 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2219 */       cp.setLocalSocketAddress(address);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseConfigs(String configs)
/*      */   {
/* 2225 */     super.setUseConfigs(configs);
/* 2226 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2227 */       cp.setUseConfigs(configs);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setGenerateSimpleParameterMetadata(boolean flag)
/*      */   {
/* 2233 */     super.setGenerateSimpleParameterMetadata(flag);
/* 2234 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2235 */       cp.setGenerateSimpleParameterMetadata(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLogXaCommands(boolean flag)
/*      */   {
/* 2241 */     super.setLogXaCommands(flag);
/* 2242 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2243 */       cp.setLogXaCommands(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setResultSetSizeThreshold(int threshold) throws SQLException
/*      */   {
/* 2249 */     super.setResultSetSizeThreshold(threshold);
/* 2250 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2251 */       cp.setResultSetSizeThreshold(threshold);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNetTimeoutForStreamingResults(int value) throws SQLException
/*      */   {
/* 2257 */     super.setNetTimeoutForStreamingResults(value);
/* 2258 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2259 */       cp.setNetTimeoutForStreamingResults(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEnableQueryTimeouts(boolean flag)
/*      */   {
/* 2265 */     super.setEnableQueryTimeouts(flag);
/* 2266 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2267 */       cp.setEnableQueryTimeouts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPadCharsWithSpace(boolean flag)
/*      */   {
/* 2273 */     super.setPadCharsWithSpace(flag);
/* 2274 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2275 */       cp.setPadCharsWithSpace(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseDynamicCharsetInfo(boolean flag)
/*      */   {
/* 2281 */     super.setUseDynamicCharsetInfo(flag);
/* 2282 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2283 */       cp.setUseDynamicCharsetInfo(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setClientInfoProvider(String classname)
/*      */   {
/* 2289 */     super.setClientInfoProvider(classname);
/* 2290 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2291 */       cp.setClientInfoProvider(classname);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPopulateInsertRowWithDefaultValues(boolean flag)
/*      */   {
/* 2297 */     super.setPopulateInsertRowWithDefaultValues(flag);
/* 2298 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2299 */       cp.setPopulateInsertRowWithDefaultValues(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceStrategy(String strategy)
/*      */   {
/* 2305 */     super.setLoadBalanceStrategy(strategy);
/* 2306 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2307 */       cp.setLoadBalanceStrategy(strategy);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTcpNoDelay(boolean flag)
/*      */   {
/* 2313 */     super.setTcpNoDelay(flag);
/* 2314 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2315 */       cp.setTcpNoDelay(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTcpKeepAlive(boolean flag)
/*      */   {
/* 2321 */     super.setTcpKeepAlive(flag);
/* 2322 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2323 */       cp.setTcpKeepAlive(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTcpRcvBuf(int bufSize) throws SQLException
/*      */   {
/* 2329 */     super.setTcpRcvBuf(bufSize);
/* 2330 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2331 */       cp.setTcpRcvBuf(bufSize);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTcpSndBuf(int bufSize) throws SQLException
/*      */   {
/* 2337 */     super.setTcpSndBuf(bufSize);
/* 2338 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2339 */       cp.setTcpSndBuf(bufSize);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setTcpTrafficClass(int classFlags) throws SQLException
/*      */   {
/* 2345 */     super.setTcpTrafficClass(classFlags);
/* 2346 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2347 */       cp.setTcpTrafficClass(classFlags);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseNanosForElapsedTime(boolean flag)
/*      */   {
/* 2353 */     super.setUseNanosForElapsedTime(flag);
/* 2354 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2355 */       cp.setUseNanosForElapsedTime(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSlowQueryThresholdNanos(long nanos) throws SQLException
/*      */   {
/* 2361 */     super.setSlowQueryThresholdNanos(nanos);
/* 2362 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2363 */       cp.setSlowQueryThresholdNanos(nanos);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setStatementInterceptors(String value)
/*      */   {
/* 2369 */     super.setStatementInterceptors(value);
/* 2370 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2371 */       cp.setStatementInterceptors(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseDirectRowUnpack(boolean flag)
/*      */   {
/* 2377 */     super.setUseDirectRowUnpack(flag);
/* 2378 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2379 */       cp.setUseDirectRowUnpack(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLargeRowSizeThreshold(String value) throws SQLException
/*      */   {
/* 2385 */     super.setLargeRowSizeThreshold(value);
/* 2386 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2387 */       cp.setLargeRowSizeThreshold(value);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseBlobToStoreUTF8OutsideBMP(boolean flag)
/*      */   {
/* 2393 */     super.setUseBlobToStoreUTF8OutsideBMP(flag);
/* 2394 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2395 */       cp.setUseBlobToStoreUTF8OutsideBMP(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern)
/*      */   {
/* 2401 */     super.setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/* 2402 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2403 */       cp.setUtf8OutsideBmpExcludedColumnNamePattern(regexPattern);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern)
/*      */   {
/* 2409 */     super.setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/* 2410 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2411 */       cp.setUtf8OutsideBmpIncludedColumnNamePattern(regexPattern);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag)
/*      */   {
/* 2417 */     super.setIncludeInnodbStatusInDeadlockExceptions(flag);
/* 2418 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2419 */       cp.setIncludeInnodbStatusInDeadlockExceptions(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setIncludeThreadDumpInDeadlockExceptions(boolean flag)
/*      */   {
/* 2425 */     super.setIncludeThreadDumpInDeadlockExceptions(flag);
/* 2426 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2427 */       cp.setIncludeThreadDumpInDeadlockExceptions(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setIncludeThreadNamesAsStatementComment(boolean flag)
/*      */   {
/* 2433 */     super.setIncludeThreadNamesAsStatementComment(flag);
/* 2434 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2435 */       cp.setIncludeThreadNamesAsStatementComment(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setBlobsAreStrings(boolean flag)
/*      */   {
/* 2441 */     super.setBlobsAreStrings(flag);
/* 2442 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2443 */       cp.setBlobsAreStrings(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setFunctionsNeverReturnBlobs(boolean flag)
/*      */   {
/* 2449 */     super.setFunctionsNeverReturnBlobs(flag);
/* 2450 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2451 */       cp.setFunctionsNeverReturnBlobs(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAutoSlowLog(boolean flag)
/*      */   {
/* 2457 */     super.setAutoSlowLog(flag);
/* 2458 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2459 */       cp.setAutoSlowLog(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setConnectionLifecycleInterceptors(String interceptors)
/*      */   {
/* 2465 */     super.setConnectionLifecycleInterceptors(interceptors);
/* 2466 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2467 */       cp.setConnectionLifecycleInterceptors(interceptors);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandler(String handler)
/*      */   {
/* 2473 */     super.setProfilerEventHandler(handler);
/* 2474 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2475 */       cp.setProfilerEventHandler(handler);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setVerifyServerCertificate(boolean flag)
/*      */   {
/* 2481 */     super.setVerifyServerCertificate(flag);
/* 2482 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2483 */       cp.setVerifyServerCertificate(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseLegacyDatetimeCode(boolean flag)
/*      */   {
/* 2489 */     super.setUseLegacyDatetimeCode(flag);
/* 2490 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2491 */       cp.setUseLegacyDatetimeCode(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException
/*      */   {
/* 2497 */     super.setSelfDestructOnPingSecondsLifetime(seconds);
/* 2498 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2499 */       cp.setSelfDestructOnPingSecondsLifetime(seconds);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException
/*      */   {
/* 2505 */     super.setSelfDestructOnPingMaxOperations(maxOperations);
/* 2506 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2507 */       cp.setSelfDestructOnPingMaxOperations(maxOperations);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseColumnNamesInFindColumn(boolean flag)
/*      */   {
/* 2513 */     super.setUseColumnNamesInFindColumn(flag);
/* 2514 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2515 */       cp.setUseColumnNamesInFindColumn(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseLocalTransactionState(boolean flag)
/*      */   {
/* 2521 */     super.setUseLocalTransactionState(flag);
/* 2522 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2523 */       cp.setUseLocalTransactionState(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag)
/*      */   {
/* 2529 */     super.setCompensateOnDuplicateKeyUpdateCounts(flag);
/* 2530 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2531 */       cp.setCompensateOnDuplicateKeyUpdateCounts(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUseAffectedRows(boolean flag)
/*      */   {
/* 2537 */     super.setUseAffectedRows(flag);
/* 2538 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2539 */       cp.setUseAffectedRows(flag);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setPasswordCharacterEncoding(String characterSet)
/*      */   {
/* 2545 */     super.setPasswordCharacterEncoding(characterSet);
/* 2546 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2547 */       cp.setPasswordCharacterEncoding(characterSet);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout) throws SQLException
/*      */   {
/* 2553 */     super.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/* 2554 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2555 */       cp.setLoadBalanceBlacklistTimeout(loadBalanceBlacklistTimeout);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setRetriesAllDown(int retriesAllDown) throws SQLException
/*      */   {
/* 2561 */     super.setRetriesAllDown(retriesAllDown);
/* 2562 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2563 */       cp.setRetriesAllDown(retriesAllDown);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setExceptionInterceptors(String exceptionInterceptors)
/*      */   {
/* 2569 */     super.setExceptionInterceptors(exceptionInterceptors);
/* 2570 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2571 */       cp.setExceptionInterceptors(exceptionInterceptors);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection)
/*      */   {
/* 2577 */     super.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/* 2578 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2579 */       cp.setQueryTimeoutKillsConnection(queryTimeoutKillsConnection);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalancePingTimeout(int loadBalancePingTimeout) throws SQLException
/*      */   {
/* 2585 */     super.setLoadBalancePingTimeout(loadBalancePingTimeout);
/* 2586 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2587 */       cp.setLoadBalancePingTimeout(loadBalancePingTimeout);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer)
/*      */   {
/* 2593 */     super.setLoadBalanceValidateConnectionOnSwapServer(loadBalanceValidateConnectionOnSwapServer);
/* 2594 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2595 */       cp.setLoadBalanceValidateConnectionOnSwapServer(loadBalanceValidateConnectionOnSwapServer);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup)
/*      */   {
/* 2601 */     super.setLoadBalanceConnectionGroup(loadBalanceConnectionGroup);
/* 2602 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2603 */       cp.setLoadBalanceConnectionGroup(loadBalanceConnectionGroup);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker)
/*      */   {
/* 2609 */     super.setLoadBalanceExceptionChecker(loadBalanceExceptionChecker);
/* 2610 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2611 */       cp.setLoadBalanceExceptionChecker(loadBalanceExceptionChecker);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover)
/*      */   {
/* 2617 */     super.setLoadBalanceSQLStateFailover(loadBalanceSQLStateFailover);
/* 2618 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2619 */       cp.setLoadBalanceSQLStateFailover(loadBalanceSQLStateFailover);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover)
/*      */   {
/* 2625 */     super.setLoadBalanceSQLExceptionSubclassFailover(loadBalanceSQLExceptionSubclassFailover);
/* 2626 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2627 */       cp.setLoadBalanceSQLExceptionSubclassFailover(loadBalanceSQLExceptionSubclassFailover);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX)
/*      */   {
/* 2633 */     super.setLoadBalanceEnableJMX(loadBalanceEnableJMX);
/* 2634 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2635 */       cp.setLoadBalanceEnableJMX(loadBalanceEnableJMX);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold) throws SQLException
/*      */   {
/* 2641 */     super.setLoadBalanceAutoCommitStatementThreshold(loadBalanceAutoCommitStatementThreshold);
/* 2642 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2643 */       cp.setLoadBalanceAutoCommitStatementThreshold(loadBalanceAutoCommitStatementThreshold);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex)
/*      */   {
/* 2649 */     super.setLoadBalanceAutoCommitStatementRegex(loadBalanceAutoCommitStatementRegex);
/* 2650 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2651 */       cp.setLoadBalanceAutoCommitStatementRegex(loadBalanceAutoCommitStatementRegex);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setAuthenticationPlugins(String authenticationPlugins)
/*      */   {
/* 2657 */     super.setAuthenticationPlugins(authenticationPlugins);
/* 2658 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2659 */       cp.setAuthenticationPlugins(authenticationPlugins);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins)
/*      */   {
/* 2665 */     super.setDisabledAuthenticationPlugins(disabledAuthenticationPlugins);
/* 2666 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2667 */       cp.setDisabledAuthenticationPlugins(disabledAuthenticationPlugins);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin)
/*      */   {
/* 2673 */     super.setDefaultAuthenticationPlugin(defaultAuthenticationPlugin);
/* 2674 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2675 */       cp.setDefaultAuthenticationPlugin(defaultAuthenticationPlugin);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setParseInfoCacheFactory(String factoryClassname)
/*      */   {
/* 2681 */     super.setParseInfoCacheFactory(factoryClassname);
/* 2682 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2683 */       cp.setParseInfoCacheFactory(factoryClassname);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setServerConfigCacheFactory(String factoryClassname)
/*      */   {
/* 2689 */     super.setServerConfigCacheFactory(factoryClassname);
/* 2690 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2691 */       cp.setServerConfigCacheFactory(factoryClassname);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords)
/*      */   {
/* 2697 */     super.setDisconnectOnExpiredPasswords(disconnectOnExpiredPasswords);
/* 2698 */     for (ConnectionProperties cp : this.serverConnections.values()) {
/* 2699 */       cp.setDisconnectOnExpiredPasswords(disconnectOnExpiredPasswords);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions)
/*      */   {
/* 2705 */     super.setGetProceduresReturnsFunctions(getProcedureReturnsFunctions);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getActiveStatementCount()
/*      */   {
/* 2711 */     return -1;
/*      */   }
/*      */   
/*      */   public long getIdleFor() {
/* 2715 */     return -1L;
/*      */   }
/*      */   
/*      */   public Log getLog() {
/* 2719 */     return this.log;
/*      */   }
/*      */   
/*      */   public boolean isMasterConnection() {
/* 2723 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isNoBackslashEscapesSet() {
/* 2727 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isSameResource(Connection c) {
/* 2731 */     return false;
/*      */   }
/*      */   
/*      */   public boolean parserKnowsUnicode() {
/* 2735 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void ping()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void resetServerState()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void setFailedOver(boolean flag) {}
/*      */   
/*      */   @Deprecated
/*      */   public void setPreferSlaveDuringFailover(boolean flag) {}
/*      */   
/*      */   public void setStatementComment(String comment) {}
/*      */   
/*      */   public void reportQueryTime(long millisOrNanos) {}
/*      */   
/*      */   public boolean isAbonormallyLongQuery(long millisOrNanos)
/*      */   {
/* 2758 */     return false;
/*      */   }
/*      */   
/*      */   public void initializeExtension(Extension ex) throws SQLException
/*      */   {}
/*      */   
/*      */   public int getAutoIncrementIncrement() {
/* 2765 */     return -1;
/*      */   }
/*      */   
/*      */   public boolean hasSameProperties(Connection c) {
/* 2769 */     return false;
/*      */   }
/*      */   
/*      */   public Properties getProperties() {
/* 2773 */     return null;
/*      */   }
/*      */   
/*      */   public void setSchema(String schema) throws SQLException
/*      */   {}
/*      */   
/*      */   public String getSchema() throws SQLException {
/* 2780 */     return null;
/*      */   }
/*      */   
/*      */   public void abort(Executor executor) throws SQLException
/*      */   {}
/*      */   
/*      */   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
/*      */   {}
/*      */   
/*      */   public int getNetworkTimeout() throws SQLException {
/* 2790 */     return -1;
/*      */   }
/*      */   
/*      */   public void checkClosed() throws SQLException
/*      */   {}
/*      */   
/*      */   public Object getConnectionMutex() {
/* 2797 */     return this;
/*      */   }
/*      */   
/*      */   public void setSessionMaxRows(int max) throws SQLException {
/* 2801 */     for (Connection c : this.serverConnections.values()) {
/* 2802 */       c.setSessionMaxRows(max);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getSessionMaxRows() {
/* 2807 */     return getActiveConnectionPassive().getSessionMaxRows();
/*      */   }
/*      */   
/*      */   public boolean isProxySet()
/*      */   {
/* 2812 */     return false;
/*      */   }
/*      */   
/*      */   public Connection duplicate() throws SQLException {
/* 2816 */     return null;
/*      */   }
/*      */   
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql) {
/* 2820 */     return null;
/*      */   }
/*      */   
/*      */   public Timer getCancelTimer() {
/* 2824 */     return null;
/*      */   }
/*      */   
/*      */   public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
/* 2828 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public String getCharsetNameForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 2836 */     return getEncodingForIndex(charsetIndex);
/*      */   }
/*      */   
/*      */   public String getEncodingForIndex(int charsetIndex) throws SQLException {
/* 2840 */     return null;
/*      */   }
/*      */   
/*      */   public TimeZone getDefaultTimeZone() {
/* 2844 */     return null;
/*      */   }
/*      */   
/*      */   public String getErrorMessageEncoding() {
/* 2848 */     return null;
/*      */   }
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 2853 */     if (this.currentConnection == null) {
/* 2854 */       return null;
/*      */     }
/*      */     
/* 2857 */     return getActiveConnectionPassive().getExceptionInterceptor();
/*      */   }
/*      */   
/*      */   public String getHost() {
/* 2861 */     return null;
/*      */   }
/*      */   
/*      */   public String getHostPortPair() {
/* 2865 */     return null;
/*      */   }
/*      */   
/*      */   public long getId() {
/* 2869 */     return -1L;
/*      */   }
/*      */   
/*      */   public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
/* 2873 */     return -1;
/*      */   }
/*      */   
/*      */   public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException {
/* 2877 */     return -1;
/*      */   }
/*      */   
/*      */   public int getNetBufferLength() {
/* 2881 */     return -1;
/*      */   }
/*      */   
/*      */   public boolean getRequiresEscapingEncoder() {
/* 2885 */     return false;
/*      */   }
/*      */   
/*      */   public int getServerMajorVersion() {
/* 2889 */     return -1;
/*      */   }
/*      */   
/*      */   public int getServerMinorVersion() {
/* 2893 */     return -1;
/*      */   }
/*      */   
/*      */   public int getServerSubMinorVersion() {
/* 2897 */     return -1;
/*      */   }
/*      */   
/*      */   public String getServerVariable(String variableName) {
/* 2901 */     return null;
/*      */   }
/*      */   
/*      */   public String getServerVersion() {
/* 2905 */     return null;
/*      */   }
/*      */   
/*      */   public Calendar getSessionLockedCalendar() {
/* 2909 */     return null;
/*      */   }
/*      */   
/*      */   public String getStatementComment() {
/* 2913 */     return null;
/*      */   }
/*      */   
/*      */   public List<StatementInterceptorV2> getStatementInterceptorsInstances() {
/* 2917 */     return null;
/*      */   }
/*      */   
/*      */   public String getURL() {
/* 2921 */     return null;
/*      */   }
/*      */   
/*      */   public String getUser() {
/* 2925 */     return null;
/*      */   }
/*      */   
/*      */   public Calendar getUtcCalendar() {
/* 2929 */     return null;
/*      */   }
/*      */   
/*      */   public void incrementNumberOfPreparedExecutes() {}
/*      */   
/*      */   public void incrementNumberOfPrepares() {}
/*      */   
/*      */   public void incrementNumberOfResultSetsCreated() {}
/*      */   
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void initializeSafeStatementInterceptors()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public boolean isClientTzUTC()
/*      */   {
/* 2948 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isCursorFetchEnabled() throws SQLException {
/* 2952 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isReadInfoMsgEnabled() {
/* 2956 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isServerTzUTC() {
/* 2960 */     return false;
/*      */   }
/*      */   
/*      */   public boolean lowerCaseTableNames() {
/* 2964 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void maxRowsChanged(com.mysql.jdbc.Statement stmt) {}
/*      */   
/*      */ 
/*      */   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void recachePreparedStatement(ServerPreparedStatement pstmt)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void registerQueryExecutionTime(long queryTimeMs) {}
/*      */   
/*      */   public void registerStatement(com.mysql.jdbc.Statement stmt) {}
/*      */   
/*      */   public void reportNumberOfTablesAccessed(int numTablesAccessed) {}
/*      */   
/*      */   public boolean serverSupportsConvertFn()
/*      */     throws SQLException
/*      */   {
/* 2992 */     return false;
/*      */   }
/*      */   
/*      */   public void setReadInfoMsgEnabled(boolean flag) {}
/*      */   
/*      */   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException
/*      */   {}
/*      */   
/*      */   public boolean storesLowerCaseTableName()
/*      */   {
/* 3002 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void throwConnectionClosedException()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */ 
/*      */   public void unregisterStatement(com.mysql.jdbc.Statement stmt) {}
/*      */   
/*      */   public void unsetMaxRows(com.mysql.jdbc.Statement stmt)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public boolean useAnsiQuotedIdentifiers()
/*      */   {
/* 3019 */     return false;
/*      */   }
/*      */   
/*      */   public boolean useMaxRows() {
/* 3023 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearWarnings() {}
/*      */   
/*      */   public Properties getClientInfo()
/*      */   {
/* 3031 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getClientInfo(String name)
/*      */   {
/* 3040 */     return null;
/*      */   }
/*      */   
/*      */   public int getHoldability() {
/* 3044 */     return -1;
/*      */   }
/*      */   
/*      */   public int getTransactionIsolation() {
/* 3048 */     return -1;
/*      */   }
/*      */   
/*      */   public Map<String, Class<?>> getTypeMap() {
/* 3052 */     return null;
/*      */   }
/*      */   
/*      */   public SQLWarning getWarnings() {
/* 3056 */     return null;
/*      */   }
/*      */   
/*      */   public String nativeSQL(String sql) {
/* 3060 */     return null;
/*      */   }
/*      */   
/*      */   public ProfilerEventHandler getProfilerEventHandlerInstance() {
/* 3064 */     return null;
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {}
/*      */   
/*      */   public void decachePreparedStatement(ServerPreparedStatement pstmt)
/*      */     throws SQLException
/*      */   {}
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\FabricMySQLConnectionProxy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */