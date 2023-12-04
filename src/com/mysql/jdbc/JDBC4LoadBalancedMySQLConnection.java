/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4LoadBalancedMySQLConnection
/*     */   extends LoadBalancedMySQLConnection
/*     */   implements JDBC4MySQLConnection
/*     */ {
/*     */   public JDBC4LoadBalancedMySQLConnection(LoadBalancedConnectionProxy proxy)
/*     */     throws SQLException
/*     */   {
/*  45 */     super(proxy);
/*     */   }
/*     */   
/*     */   private JDBC4MySQLConnection getJDBC4Connection() {
/*  49 */     return (JDBC4MySQLConnection)getActiveMySQLConnection();
/*     */   }
/*     */   
/*     */   public SQLXML createSQLXML() throws SQLException {
/*  53 */     return getJDBC4Connection().createSQLXML();
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  57 */     return getJDBC4Connection().createArrayOf(typeName, elements);
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/*  61 */     return getJDBC4Connection().createStruct(typeName, attributes);
/*     */   }
/*     */   
/*     */   public Properties getClientInfo() throws SQLException {
/*  65 */     return getJDBC4Connection().getClientInfo();
/*     */   }
/*     */   
/*     */   public String getClientInfo(String name) throws SQLException {
/*  69 */     return getJDBC4Connection().getClientInfo(name);
/*     */   }
/*     */   
/*     */   public boolean isValid(int timeout) throws SQLException {
/*  73 */     return getJDBC4Connection().isValid(timeout);
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*  77 */     getJDBC4Connection().setClientInfo(properties);
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*  81 */     getJDBC4Connection().setClientInfo(name, value);
/*     */   }
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  85 */     checkClosed();
/*     */     
/*     */ 
/*  88 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException
/*     */   {
/*     */     try {
/*  94 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/*  96 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Blob createBlob()
/*     */   {
/* 104 */     return getJDBC4Connection().createBlob();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Clob createClob()
/*     */   {
/* 111 */     return getJDBC4Connection().createClob();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public NClob createNClob()
/*     */   {
/* 118 */     return getJDBC4Connection().createNClob();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public JDBC4ClientInfoProvider getClientInfoProviderImpl()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 30	com/mysql/jdbc/JDBC4LoadBalancedMySQLConnection:getThisAsProxy	()Lcom/mysql/jdbc/LoadBalancedConnectionProxy;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: invokespecial 4	com/mysql/jdbc/JDBC4LoadBalancedMySQLConnection:getJDBC4Connection	()Lcom/mysql/jdbc/JDBC4MySQLConnection;
/*     */     //   11: invokeinterface 31 1 0
/*     */     //   16: aload_1
/*     */     //   17: monitorexit
/*     */     //   18: areturn
/*     */     //   19: astore_2
/*     */     //   20: aload_1
/*     */     //   21: monitorexit
/*     */     //   22: aload_2
/*     */     //   23: athrow
/*     */     // Line number table:
/*     */     //   Java source line #122	-> byte code offset #0
/*     */     //   Java source line #123	-> byte code offset #7
/*     */     //   Java source line #124	-> byte code offset #19
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	24	0	this	JDBC4LoadBalancedMySQLConnection
/*     */     //   5	16	1	Ljava/lang/Object;	Object
/*     */     //   19	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	18	19	finally
/*     */     //   19	22	19	finally
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4LoadBalancedMySQLConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */