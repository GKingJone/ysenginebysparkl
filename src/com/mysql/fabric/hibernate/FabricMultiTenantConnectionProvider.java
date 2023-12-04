/*     */ package com.mysql.fabric.hibernate;
/*     */ 
/*     */ import com.mysql.fabric.FabricCommunicationException;
/*     */ import com.mysql.fabric.FabricConnection;
/*     */ import com.mysql.fabric.Server;
/*     */ import com.mysql.fabric.ServerGroup;
/*     */ import com.mysql.fabric.ServerMode;
/*     */ import com.mysql.fabric.ShardMapping;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
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
/*     */ 
/*     */ public class FabricMultiTenantConnectionProvider
/*     */   implements MultiTenantConnectionProvider
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private FabricConnection fabricConnection;
/*     */   private String database;
/*     */   private String table;
/*     */   private String user;
/*     */   private String password;
/*     */   private ShardMapping shardMapping;
/*     */   private ServerGroup globalGroup;
/*     */   
/*     */   public FabricMultiTenantConnectionProvider(String fabricUrl, String database, String table, String user, String password, String fabricUser, String fabricPassword)
/*     */   {
/*     */     try
/*     */     {
/*  59 */       this.fabricConnection = new FabricConnection(fabricUrl, fabricUser, fabricPassword);
/*  60 */       this.database = database;
/*  61 */       this.table = table;
/*  62 */       this.user = user;
/*  63 */       this.password = password;
/*  64 */       this.shardMapping = this.fabricConnection.getShardMapping(this.database, this.table);
/*  65 */       this.globalGroup = this.fabricConnection.getServerGroup(this.shardMapping.getGlobalGroupName());
/*     */     } catch (FabricCommunicationException ex) {
/*  67 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Connection getReadWriteConnectionFromServerGroup(ServerGroup serverGroup)
/*     */     throws SQLException
/*     */   {
/*  79 */     for (Server s : serverGroup.getServers()) {
/*  80 */       if (ServerMode.READ_WRITE.equals(s.getMode())) {
/*  81 */         String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", new Object[] { s.getHostname(), Integer.valueOf(s.getPort()), this.database });
/*  82 */         return DriverManager.getConnection(jdbcUrl, this.user, this.password);
/*     */       }
/*     */     }
/*  85 */     throw new SQLException("Unable to find r/w server for chosen shard mapping in group " + serverGroup.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Connection getAnyConnection()
/*     */     throws SQLException
/*     */   {
/*  94 */     return getReadWriteConnectionFromServerGroup(this.globalGroup);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Connection getConnection(String tenantIdentifier)
/*     */     throws SQLException
/*     */   {
/* 102 */     String serverGroupName = this.shardMapping.getGroupNameForKey(tenantIdentifier);
/*     */     try {
/* 104 */       ServerGroup serverGroup = this.fabricConnection.getServerGroup(serverGroupName);
/* 105 */       return getReadWriteConnectionFromServerGroup(serverGroup);
/*     */     } catch (FabricCommunicationException ex) {
/* 107 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public void releaseAnyConnection(Connection connection)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 116 */       connection.close();
/*     */     } catch (Exception ex) {
/* 118 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void releaseConnection(String tenantIdentifier, Connection connection)
/*     */     throws SQLException
/*     */   {
/* 126 */     releaseAnyConnection(connection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean supportsAggressiveRelease()
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isUnwrappableAs(Class unwrapType)
/*     */   {
/* 140 */     return false;
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> unwrapType) {
/* 144 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\hibernate\FabricMultiTenantConnectionProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */