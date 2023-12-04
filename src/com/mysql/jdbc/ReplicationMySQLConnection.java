/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public class ReplicationMySQLConnection
/*     */   extends MultiHostMySQLConnection
/*     */   implements ReplicationConnection
/*     */ {
/*     */   public ReplicationMySQLConnection(MultiHostConnectionProxy proxy)
/*     */   {
/*  32 */     super(proxy);
/*     */   }
/*     */   
/*     */   protected ReplicationConnectionProxy getThisAsProxy()
/*     */   {
/*  37 */     return (ReplicationConnectionProxy)super.getThisAsProxy();
/*     */   }
/*     */   
/*     */   protected MySQLConnection getActiveMySQLConnection()
/*     */   {
/*  42 */     return (MySQLConnection)getCurrentConnection();
/*     */   }
/*     */   
/*     */   public synchronized Connection getCurrentConnection() {
/*  46 */     return getThisAsProxy().getCurrentConnection();
/*     */   }
/*     */   
/*     */   public long getConnectionGroupId() {
/*  50 */     return getThisAsProxy().getConnectionGroupId();
/*     */   }
/*     */   
/*     */   public synchronized Connection getMasterConnection() {
/*  54 */     return getThisAsProxy().getMasterConnection();
/*     */   }
/*     */   
/*     */   private Connection getValidatedMasterConnection() {
/*  58 */     Connection conn = getThisAsProxy().masterConnection;
/*     */     try {
/*  60 */       return (conn == null) || (conn.isClosed()) ? null : conn;
/*     */     } catch (SQLException e) {}
/*  62 */     return null;
/*     */   }
/*     */   
/*     */   public void promoteSlaveToMaster(String host) throws SQLException
/*     */   {
/*  67 */     getThisAsProxy().promoteSlaveToMaster(host);
/*     */   }
/*     */   
/*     */   public void removeMasterHost(String host) throws SQLException {
/*  71 */     getThisAsProxy().removeMasterHost(host);
/*     */   }
/*     */   
/*     */   public void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
/*  75 */     getThisAsProxy().removeMasterHost(host, waitUntilNotInUse);
/*     */   }
/*     */   
/*     */   public boolean isHostMaster(String host) {
/*  79 */     return getThisAsProxy().isHostMaster(host);
/*     */   }
/*     */   
/*     */   public synchronized Connection getSlavesConnection() {
/*  83 */     return getThisAsProxy().getSlavesConnection();
/*     */   }
/*     */   
/*     */   private Connection getValidatedSlavesConnection() {
/*  87 */     Connection conn = getThisAsProxy().slavesConnection;
/*     */     try {
/*  89 */       return (conn == null) || (conn.isClosed()) ? null : conn;
/*     */     } catch (SQLException e) {}
/*  91 */     return null;
/*     */   }
/*     */   
/*     */   public void addSlaveHost(String host) throws SQLException
/*     */   {
/*  96 */     getThisAsProxy().addSlaveHost(host);
/*     */   }
/*     */   
/*     */   public void removeSlave(String host) throws SQLException {
/* 100 */     getThisAsProxy().removeSlave(host);
/*     */   }
/*     */   
/*     */   public void removeSlave(String host, boolean closeGently) throws SQLException {
/* 104 */     getThisAsProxy().removeSlave(host, closeGently);
/*     */   }
/*     */   
/*     */   public boolean isHostSlave(String host) {
/* 108 */     return getThisAsProxy().isHostSlave(host);
/*     */   }
/*     */   
/*     */   public void setReadOnly(boolean readOnlyFlag) throws SQLException
/*     */   {
/* 113 */     getThisAsProxy().setReadOnly(readOnlyFlag);
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException
/*     */   {
/* 118 */     return getThisAsProxy().isReadOnly();
/*     */   }
/*     */   
/*     */   public synchronized void ping() throws SQLException
/*     */   {
/*     */     Connection conn;
/*     */     try {
/* 125 */       if ((conn = getValidatedMasterConnection()) != null) {
/* 126 */         conn.ping();
/*     */       }
/*     */     } catch (SQLException e) {
/* 129 */       if (isMasterConnection()) {
/* 130 */         throw e;
/*     */       }
/*     */     }
/*     */     try {
/* 134 */       if ((conn = getValidatedSlavesConnection()) != null) {
/* 135 */         conn.ping();
/*     */       }
/*     */     } catch (SQLException e) {
/* 138 */       if (!isMasterConnection()) {
/* 139 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void changeUser(String userName, String newPassword) throws SQLException
/*     */   {
/*     */     Connection conn;
/* 147 */     if ((conn = getValidatedMasterConnection()) != null) {
/* 148 */       conn.changeUser(userName, newPassword);
/*     */     }
/* 150 */     if ((conn = getValidatedSlavesConnection()) != null) {
/* 151 */       conn.changeUser(userName, newPassword);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setStatementComment(String comment)
/*     */   {
/*     */     Connection conn;
/* 158 */     if ((conn = getValidatedMasterConnection()) != null) {
/* 159 */       conn.setStatementComment(comment);
/*     */     }
/* 161 */     if ((conn = getValidatedSlavesConnection()) != null) {
/* 162 */       conn.setStatementComment(comment);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasSameProperties(Connection c)
/*     */   {
/* 168 */     Connection connM = getValidatedMasterConnection();
/* 169 */     Connection connS = getValidatedSlavesConnection();
/* 170 */     if ((connM == null) && (connS == null)) {
/* 171 */       return false;
/*     */     }
/* 173 */     return ((connM == null) || (connM.hasSameProperties(c))) && ((connS == null) || (connS.hasSameProperties(c)));
/*     */   }
/*     */   
/*     */   public Properties getProperties()
/*     */   {
/* 178 */     Properties props = new Properties();
/*     */     Connection conn;
/* 180 */     if ((conn = getValidatedMasterConnection()) != null) {
/* 181 */       props.putAll(conn.getProperties());
/*     */     }
/* 183 */     if ((conn = getValidatedSlavesConnection()) != null) {
/* 184 */       props.putAll(conn.getProperties());
/*     */     }
/*     */     
/* 187 */     return props;
/*     */   }
/*     */   
/*     */   public void abort(Executor executor) throws SQLException
/*     */   {
/* 192 */     getThisAsProxy().doAbort(executor);
/*     */   }
/*     */   
/*     */   public void abortInternal() throws SQLException
/*     */   {
/* 197 */     getThisAsProxy().doAbortInternal();
/*     */   }
/*     */   
/*     */   public boolean getAllowMasterDownConnections()
/*     */   {
/* 202 */     return getThisAsProxy().allowMasterDownConnections;
/*     */   }
/*     */   
/*     */   public void setAllowMasterDownConnections(boolean connectIfMasterDown)
/*     */   {
/* 207 */     getThisAsProxy().allowMasterDownConnections = connectIfMasterDown;
/*     */   }
/*     */   
/*     */   public boolean getReplicationEnableJMX()
/*     */   {
/* 212 */     return getThisAsProxy().enableJMX;
/*     */   }
/*     */   
/*     */   public void setReplicationEnableJMX(boolean replicationEnableJMX)
/*     */   {
/* 217 */     getThisAsProxy().enableJMX = replicationEnableJMX;
/*     */   }
/*     */   
/*     */   public void setProxy(MySQLConnection proxy)
/*     */   {
/* 222 */     getThisAsProxy().setProxy(proxy);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ReplicationMySQLConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */