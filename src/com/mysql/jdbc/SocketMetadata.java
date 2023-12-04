/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Properties;
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
/*     */ public abstract interface SocketMetadata
/*     */ {
/*     */   public abstract boolean isLocallyConnected(ConnectionImpl paramConnectionImpl)
/*     */     throws SQLException;
/*     */   
/*     */   public static class Helper
/*     */   {
/*     */     public static final String IS_LOCAL_HOSTNAME_REPLACEMENT_PROPERTY_NAME = "com.mysql.jdbc.test.isLocalHostnameReplacement";
/*     */     
/*     */     public static boolean isLocallyConnected(ConnectionImpl conn)
/*     */       throws SQLException
/*     */     {
/*  48 */       long threadId = conn.getId();
/*  49 */       Statement processListStmt = conn.getMetadataSafeStatement();
/*  50 */       ResultSet rs = null;
/*  51 */       String processHost = null;
/*     */       
/*     */ 
/*  54 */       if (System.getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement") != null) {
/*  55 */         processHost = System.getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement");
/*     */       }
/*  57 */       else if (conn.getProperties().getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement") != null) {
/*  58 */         processHost = conn.getProperties().getProperty("com.mysql.jdbc.test.isLocalHostnameReplacement");
/*     */       } else {
/*     */         try
/*     */         {
/*  62 */           processHost = findProcessHost(threadId, processListStmt);
/*     */           
/*  64 */           if (processHost == null)
/*     */           {
/*  66 */             conn.getLog().logWarn(String.format("Connection id %d not found in \"SHOW PROCESSLIST\", assuming 32-bit overflow, using SELECT CONNECTION_ID() instead", new Object[] { Long.valueOf(threadId) }));
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*  71 */             rs = processListStmt.executeQuery("SELECT CONNECTION_ID()");
/*     */             
/*  73 */             if (rs.next()) {
/*  74 */               threadId = rs.getLong(1);
/*     */               
/*  76 */               processHost = findProcessHost(threadId, processListStmt);
/*     */             } else {
/*  78 */               conn.getLog().logError("No rows returned for statement \"SELECT CONNECTION_ID()\", local connection check will most likely be incorrect");
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/*  83 */           processListStmt.close();
/*     */         }
/*     */       }
/*     */       
/*  87 */       if (processHost != null) {
/*  88 */         conn.getLog().logDebug(String.format("Using 'host' value of '%s' to determine locality of connection", new Object[] { processHost }));
/*     */         
/*  90 */         int endIndex = processHost.lastIndexOf(":");
/*  91 */         if (endIndex != -1) {
/*  92 */           processHost = processHost.substring(0, endIndex);
/*     */           try
/*     */           {
/*  95 */             boolean isLocal = false;
/*     */             
/*  97 */             InetAddress[] allHostAddr = InetAddress.getAllByName(processHost);
/*     */             
/*     */ 
/* 100 */             SocketAddress remoteSocketAddr = conn.getIO().mysqlConnection.getRemoteSocketAddress();
/*     */             
/* 102 */             if ((remoteSocketAddr instanceof InetSocketAddress)) {
/* 103 */               InetAddress whereIConnectedTo = ((InetSocketAddress)remoteSocketAddr).getAddress();
/*     */               
/* 105 */               for (InetAddress hostAddr : allHostAddr) {
/* 106 */                 if (hostAddr.equals(whereIConnectedTo)) {
/* 107 */                   conn.getLog().logDebug(String.format("Locally connected - HostAddress(%s).equals(whereIconnectedTo({%s})", new Object[] { hostAddr, whereIConnectedTo }));
/*     */                   
/* 109 */                   isLocal = true;
/* 110 */                   break;
/*     */                 }
/* 112 */                 conn.getLog().logDebug(String.format("Attempted locally connected check failed - ! HostAddress(%s).equals(whereIconnectedTo(%s)", new Object[] { hostAddr, whereIConnectedTo }));
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 117 */               String msg = String.format("Remote socket address %s is not an inet socket address", new Object[] { remoteSocketAddr });
/* 118 */               conn.getLog().logDebug(msg);
/*     */             }
/*     */             
/* 121 */             return isLocal;
/*     */           } catch (UnknownHostException e) {
/* 123 */             conn.getLog().logWarn(Messages.getString("Connection.CantDetectLocalConnect", new Object[] { processHost }), e);
/* 124 */             return false;
/*     */           }
/*     */         }
/* 127 */         conn.getLog().logWarn(String.format("No port number present in 'host' from SHOW PROCESSLIST '%s', unable to determine whether locally connected", new Object[] { processHost }));
/*     */         
/* 129 */         return false;
/*     */       }
/* 131 */       conn.getLog().logWarn(String.format("Cannot find process listing for connection %d in SHOW PROCESSLIST output, unable to determine if locally connected", new Object[] { Long.valueOf(threadId) }));
/*     */       
/* 133 */       return false;
/*     */     }
/*     */     
/*     */     private static String findProcessHost(long threadId, Statement processListStmt) throws SQLException {
/* 137 */       String processHost = null;
/* 138 */       ResultSet rs = processListStmt.executeQuery("SHOW PROCESSLIST");
/*     */       
/* 140 */       while (rs.next()) {
/* 141 */         long id = rs.getLong(1);
/*     */         
/* 143 */         if (threadId == id) {
/* 144 */           processHost = rs.getString(3);
/* 145 */           break;
/*     */         }
/*     */       }
/*     */       
/* 149 */       return processHost;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\SocketMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */