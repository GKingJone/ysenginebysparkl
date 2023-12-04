/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
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
/*     */ public class SuspendableXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */   
/*     */   static
/*     */   {
/*  44 */     if (Util.isJdbc4()) {
/*     */       try {
/*  46 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection").getConstructor(new Class[] { com.mysql.jdbc.Connection.class });
/*     */       }
/*     */       catch (SecurityException e) {
/*  49 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  51 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  53 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/*  56 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static SuspendableXAConnection getInstance(com.mysql.jdbc.Connection mysqlConnection) throws SQLException {
/*  61 */     if (!Util.isJdbc4()) {
/*  62 */       return new SuspendableXAConnection(mysqlConnection);
/*     */     }
/*     */     
/*  65 */     return (SuspendableXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection }, mysqlConnection.getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   public SuspendableXAConnection(com.mysql.jdbc.Connection connection)
/*     */   {
/*  70 */     super(connection);
/*  71 */     this.underlyingConnection = connection;
/*     */   }
/*     */   
/*  74 */   private static final Map<Xid, XAConnection> XIDS_TO_PHYSICAL_CONNECTIONS = new HashMap();
/*     */   
/*     */   private Xid currentXid;
/*     */   
/*     */   private XAConnection currentXAConnection;
/*     */   
/*     */   private XAResource currentXAResource;
/*     */   
/*     */   private com.mysql.jdbc.Connection underlyingConnection;
/*     */   
/*     */ 
/*     */   private static synchronized XAConnection findConnectionForXid(com.mysql.jdbc.Connection connectionToWrap, Xid xid)
/*     */     throws SQLException
/*     */   {
/*  88 */     XAConnection conn = (XAConnection)XIDS_TO_PHYSICAL_CONNECTIONS.get(xid);
/*     */     
/*  90 */     if (conn == null) {
/*  91 */       conn = new MysqlXAConnection(connectionToWrap, connectionToWrap.getLogXaCommands());
/*  92 */       XIDS_TO_PHYSICAL_CONNECTIONS.put(xid, conn);
/*     */     }
/*     */     
/*  95 */     return conn;
/*     */   }
/*     */   
/*     */   private static synchronized void removeXAConnectionMapping(Xid xid) {
/*  99 */     XIDS_TO_PHYSICAL_CONNECTIONS.remove(xid);
/*     */   }
/*     */   
/*     */   private synchronized void switchToXid(Xid xid) throws XAException {
/* 103 */     if (xid == null) {
/* 104 */       throw new XAException();
/*     */     }
/*     */     try
/*     */     {
/* 108 */       if (!xid.equals(this.currentXid)) {
/* 109 */         XAConnection toSwitchTo = findConnectionForXid(this.underlyingConnection, xid);
/* 110 */         this.currentXAConnection = toSwitchTo;
/* 111 */         this.currentXid = xid;
/* 112 */         this.currentXAResource = toSwitchTo.getXAResource();
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 115 */       throw new XAException();
/*     */     }
/*     */   }
/*     */   
/*     */   public XAResource getXAResource() throws SQLException {
/* 120 */     return this;
/*     */   }
/*     */   
/*     */   public void commit(Xid xid, boolean arg1) throws XAException {
/* 124 */     switchToXid(xid);
/* 125 */     this.currentXAResource.commit(xid, arg1);
/* 126 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */   
/*     */   public void end(Xid xid, int arg1) throws XAException {
/* 130 */     switchToXid(xid);
/* 131 */     this.currentXAResource.end(xid, arg1);
/*     */   }
/*     */   
/*     */   public void forget(Xid xid) throws XAException {
/* 135 */     switchToXid(xid);
/* 136 */     this.currentXAResource.forget(xid);
/*     */     
/* 138 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */   
/*     */   public int getTransactionTimeout() throws XAException {
/* 142 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isSameRM(XAResource xaRes) throws XAException {
/* 146 */     return xaRes == this;
/*     */   }
/*     */   
/*     */   public int prepare(Xid xid) throws XAException {
/* 150 */     switchToXid(xid);
/* 151 */     return this.currentXAResource.prepare(xid);
/*     */   }
/*     */   
/*     */   public Xid[] recover(int flag) throws XAException {
/* 155 */     return MysqlXAConnection.recover(this.underlyingConnection, flag);
/*     */   }
/*     */   
/*     */   public void rollback(Xid xid) throws XAException {
/* 159 */     switchToXid(xid);
/* 160 */     this.currentXAResource.rollback(xid);
/* 161 */     removeXAConnectionMapping(xid);
/*     */   }
/*     */   
/*     */   public boolean setTransactionTimeout(int arg0) throws XAException {
/* 165 */     return false;
/*     */   }
/*     */   
/*     */   public void start(Xid xid, int arg1) throws XAException {
/* 169 */     switchToXid(xid);
/*     */     
/* 171 */     if (arg1 != 2097152) {
/* 172 */       this.currentXAResource.start(xid, arg1);
/*     */       
/* 174 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */     this.currentXAResource.start(xid, 134217728);
/*     */   }
/*     */   
/*     */   public synchronized java.sql.Connection getConnection() throws SQLException
/*     */   {
/* 186 */     if (this.currentXAConnection == null) {
/* 187 */       return getConnection(false, true);
/*     */     }
/*     */     
/* 190 */     return this.currentXAConnection.getConnection();
/*     */   }
/*     */   
/*     */   public void close() throws SQLException
/*     */   {
/* 195 */     if (this.currentXAConnection == null) {
/* 196 */       super.close();
/*     */     } else {
/* 198 */       removeXAConnectionMapping(this.currentXid);
/* 199 */       this.currentXAConnection.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\SuspendableXAConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */