/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.Messages;
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class MysqlXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private static final int MAX_COMMAND_LENGTH = 300;
/*     */   private com.mysql.jdbc.Connection underlyingConnection;
/*     */   private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
/*     */   private Log log;
/*     */   protected boolean logXaCommands;
/*     */   private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
/*     */   
/*     */   static
/*     */   {
/*  74 */     HashMap<Integer, Integer> temp = new HashMap();
/*     */     
/*  76 */     temp.put(Integer.valueOf(1397), Integer.valueOf(-4));
/*  77 */     temp.put(Integer.valueOf(1398), Integer.valueOf(-5));
/*  78 */     temp.put(Integer.valueOf(1399), Integer.valueOf(-7));
/*  79 */     temp.put(Integer.valueOf(1400), Integer.valueOf(-9));
/*  80 */     temp.put(Integer.valueOf(1401), Integer.valueOf(-3));
/*  81 */     temp.put(Integer.valueOf(1402), Integer.valueOf(100));
/*  82 */     temp.put(Integer.valueOf(1440), Integer.valueOf(-8));
/*     */     
/*  84 */     MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */     if (Util.isJdbc4()) {
/*     */       try {
/*  92 */         JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(new Class[] { com.mysql.jdbc.Connection.class, Boolean.TYPE });
/*     */       }
/*     */       catch (SecurityException e) {
/*  95 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  97 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  99 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/* 102 */       JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static MysqlXAConnection getInstance(com.mysql.jdbc.Connection mysqlConnection, boolean logXaCommands) throws SQLException {
/* 107 */     if (!Util.isJdbc4()) {
/* 108 */       return new MysqlXAConnection(mysqlConnection, logXaCommands);
/*     */     }
/*     */     
/* 111 */     return (MysqlXAConnection)Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[] { mysqlConnection, Boolean.valueOf(logXaCommands) }, mysqlConnection.getExceptionInterceptor());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MysqlXAConnection(com.mysql.jdbc.Connection connection, boolean logXaCommands)
/*     */     throws SQLException
/*     */   {
/* 119 */     super(connection);
/* 120 */     this.underlyingConnection = connection;
/* 121 */     this.log = connection.getLog();
/* 122 */     this.logXaCommands = logXaCommands;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public XAResource getXAResource()
/*     */     throws SQLException
/*     */   {
/* 135 */     return this;
/*     */   }
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
/*     */   public int getTransactionTimeout()
/*     */     throws XAException
/*     */   {
/* 152 */     return 0;
/*     */   }
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
/*     */   public boolean setTransactionTimeout(int arg0)
/*     */     throws XAException
/*     */   {
/* 177 */     return false;
/*     */   }
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
/*     */   public boolean isSameRM(XAResource xares)
/*     */     throws XAException
/*     */   {
/* 197 */     if ((xares instanceof MysqlXAConnection)) {
/* 198 */       return this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection);
/*     */     }
/*     */     
/* 201 */     return false;
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Xid[] recover(int flag)
/*     */     throws XAException
/*     */   {
/* 242 */     return recover(this.underlyingConnection, flag);
/*     */   }
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
/*     */   protected static Xid[] recover(java.sql.Connection c, int flag)
/*     */     throws XAException
/*     */   {
/* 267 */     boolean startRscan = (flag & 0x1000000) > 0;
/* 268 */     boolean endRscan = (flag & 0x800000) > 0;
/*     */     
/* 270 */     if ((!startRscan) && (!endRscan) && (flag != 0)) {
/* 271 */       throw new MysqlXAException(-5, Messages.getString("MysqlXAConnection.001"), null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 280 */     if (!startRscan) {
/* 281 */       return new Xid[0];
/*     */     }
/*     */     
/* 284 */     ResultSet rs = null;
/* 285 */     Statement stmt = null;
/*     */     
/* 287 */     List<MysqlXid> recoveredXidList = new ArrayList();
/*     */     
/*     */     try
/*     */     {
/* 291 */       stmt = c.createStatement();
/*     */       
/* 293 */       rs = stmt.executeQuery("XA RECOVER");
/*     */       
/* 295 */       while (rs.next()) {
/* 296 */         int formatId = rs.getInt(1);
/* 297 */         int gtridLength = rs.getInt(2);
/* 298 */         int bqualLength = rs.getInt(3);
/* 299 */         byte[] gtridAndBqual = rs.getBytes(4);
/*     */         
/* 301 */         byte[] gtrid = new byte[gtridLength];
/* 302 */         byte[] bqual = new byte[bqualLength];
/*     */         
/* 304 */         if (gtridAndBqual.length != gtridLength + bqualLength) {
/* 305 */           throw new MysqlXAException(105, Messages.getString("MysqlXAConnection.002"), null);
/*     */         }
/*     */         
/* 308 */         System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
/* 309 */         System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
/*     */         
/* 311 */         recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 314 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 316 */       if (rs != null) {
/*     */         try {
/* 318 */           rs.close();
/*     */         } catch (SQLException sqlEx) {
/* 320 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */       
/* 324 */       if (stmt != null) {
/*     */         try {
/* 326 */           stmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 328 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 333 */     int numXids = recoveredXidList.size();
/*     */     
/* 335 */     Xid[] asXids = new Xid[numXids];
/* 336 */     Object[] asObjects = recoveredXidList.toArray();
/*     */     
/* 338 */     for (int i = 0; i < numXids; i++) {
/* 339 */       asXids[i] = ((Xid)asObjects[i]);
/*     */     }
/*     */     
/* 342 */     return asXids;
/*     */   }
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
/*     */   public int prepare(Xid xid)
/*     */     throws XAException
/*     */   {
/* 364 */     StringBuilder commandBuf = new StringBuilder(300);
/* 365 */     commandBuf.append("XA PREPARE ");
/* 366 */     appendXid(commandBuf, xid);
/*     */     
/* 368 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 370 */     return 0;
/*     */   }
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
/*     */ 
/*     */   public void rollback(Xid xid)
/*     */     throws XAException
/*     */   {
/* 406 */     StringBuilder commandBuf = new StringBuilder(300);
/* 407 */     commandBuf.append("XA ROLLBACK ");
/* 408 */     appendXid(commandBuf, xid);
/*     */     try
/*     */     {
/* 411 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 413 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
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
/*     */   public void end(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 446 */     StringBuilder commandBuf = new StringBuilder(300);
/* 447 */     commandBuf.append("XA END ");
/* 448 */     appendXid(commandBuf, xid);
/*     */     
/* 450 */     switch (flags) {
/*     */     case 67108864: 
/*     */       break;
/*     */     case 33554432: 
/* 454 */       commandBuf.append(" SUSPEND");
/* 455 */       break;
/*     */     case 536870912: 
/*     */       break;
/*     */     default: 
/* 459 */       throw new XAException(-5);
/*     */     }
/*     */     
/* 462 */     dispatchCommand(commandBuf.toString());
/*     */   }
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
/*     */   public void start(Xid xid, int flags)
/*     */     throws XAException
/*     */   {
/* 489 */     StringBuilder commandBuf = new StringBuilder(300);
/* 490 */     commandBuf.append("XA START ");
/* 491 */     appendXid(commandBuf, xid);
/*     */     
/* 493 */     switch (flags) {
/*     */     case 2097152: 
/* 495 */       commandBuf.append(" JOIN");
/* 496 */       break;
/*     */     case 134217728: 
/* 498 */       commandBuf.append(" RESUME");
/* 499 */       break;
/*     */     case 0: 
/*     */       break;
/*     */     
/*     */     default: 
/* 504 */       throw new XAException(-5);
/*     */     }
/*     */     
/* 507 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 509 */     this.underlyingConnection.setInGlobalTx(true);
/*     */   }
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
/*     */   public void commit(Xid xid, boolean onePhase)
/*     */     throws XAException
/*     */   {
/* 534 */     StringBuilder commandBuf = new StringBuilder(300);
/* 535 */     commandBuf.append("XA COMMIT ");
/* 536 */     appendXid(commandBuf, xid);
/*     */     
/* 538 */     if (onePhase) {
/* 539 */       commandBuf.append(" ONE PHASE");
/*     */     }
/*     */     try
/*     */     {
/* 543 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 545 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     }
/*     */   }
/*     */   
/*     */   private ResultSet dispatchCommand(String command) throws XAException {
/* 550 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 553 */       if (this.logXaCommands) {
/* 554 */         this.log.logDebug("Executing XA statement: " + command);
/*     */       }
/*     */       
/*     */ 
/* 558 */       stmt = this.underlyingConnection.createStatement();
/*     */       
/* 560 */       stmt.execute(command);
/*     */       
/* 562 */       ResultSet rs = stmt.getResultSet();
/*     */       
/* 564 */       return rs;
/*     */     } catch (SQLException sqlEx) {
/* 566 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 568 */       if (stmt != null) {
/*     */         try {
/* 570 */           stmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
/* 578 */     Integer xaCode = (Integer)MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Integer.valueOf(sqlEx.getErrorCode()));
/*     */     
/* 580 */     if (xaCode != null) {
/* 581 */       return (XAException)new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null).initCause(sqlEx);
/*     */     }
/*     */     
/* 584 */     return (XAException)new MysqlXAException(-7, Messages.getString("MysqlXAConnection.003"), null).initCause(sqlEx);
/*     */   }
/*     */   
/*     */   private static void appendXid(StringBuilder builder, Xid xid) {
/* 588 */     byte[] gtrid = xid.getGlobalTransactionId();
/* 589 */     byte[] btrid = xid.getBranchQualifier();
/*     */     
/* 591 */     if (gtrid != null) {
/* 592 */       StringUtils.appendAsHex(builder, gtrid);
/*     */     }
/*     */     
/* 595 */     builder.append(',');
/* 596 */     if (btrid != null) {
/* 597 */       StringUtils.appendAsHex(builder, btrid);
/*     */     }
/*     */     
/* 600 */     builder.append(',');
/* 601 */     StringUtils.appendAsHex(builder, xid.getFormatId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized java.sql.Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 611 */     java.sql.Connection connToWrap = getConnection(false, true);
/*     */     
/* 613 */     return connToWrap;
/*     */   }
/*     */   
/*     */   public void forget(Xid xid)
/*     */     throws XAException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlXAConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */