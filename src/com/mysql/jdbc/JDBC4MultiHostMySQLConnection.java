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
/*     */ public class JDBC4MultiHostMySQLConnection
/*     */   extends MultiHostMySQLConnection
/*     */   implements JDBC4MySQLConnection
/*     */ {
/*     */   public JDBC4MultiHostMySQLConnection(MultiHostConnectionProxy proxy)
/*     */     throws SQLException
/*     */   {
/*  45 */     super(proxy);
/*     */   }
/*     */   
/*     */   private JDBC4Connection getJDBC4Connection() {
/*  49 */     return (JDBC4Connection)getThisAsProxy().currentConnection;
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
/*     */   /* Error */
/*     */   public boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 2	com/mysql/jdbc/JDBC4MultiHostMySQLConnection:getThisAsProxy	()Lcom/mysql/jdbc/MultiHostConnectionProxy;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: invokespecial 5	com/mysql/jdbc/JDBC4MultiHostMySQLConnection:getJDBC4Connection	()Lcom/mysql/jdbc/JDBC4Connection;
/*     */     //   11: iload_1
/*     */     //   12: invokevirtual 11	com/mysql/jdbc/JDBC4Connection:isValid	(I)Z
/*     */     //   15: aload_2
/*     */     //   16: monitorexit
/*     */     //   17: ireturn
/*     */     //   18: astore_3
/*     */     //   19: aload_2
/*     */     //   20: monitorexit
/*     */     //   21: aload_3
/*     */     //   22: athrow
/*     */     // Line number table:
/*     */     //   Java source line #73	-> byte code offset #0
/*     */     //   Java source line #74	-> byte code offset #7
/*     */     //   Java source line #75	-> byte code offset #18
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	23	0	this	JDBC4MultiHostMySQLConnection
/*     */     //   0	23	1	timeout	int
/*     */     //   5	15	2	Ljava/lang/Object;	Object
/*     */     //   18	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	17	18	finally
/*     */     //   18	21	18	finally
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties)
/*     */     throws SQLClientInfoException
/*     */   {
/*  79 */     getJDBC4Connection().setClientInfo(properties);
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*  83 */     getJDBC4Connection().setClientInfo(name, value);
/*     */   }
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/*  87 */     checkClosed();
/*     */     
/*     */ 
/*  90 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException
/*     */   {
/*     */     try {
/*  96 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/*  98 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Blob createBlob()
/*     */   {
/* 106 */     return getJDBC4Connection().createBlob();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Clob createClob()
/*     */   {
/* 113 */     return getJDBC4Connection().createClob();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public NClob createNClob()
/*     */   {
/* 120 */     return getJDBC4Connection().createNClob();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public JDBC4ClientInfoProvider getClientInfoProviderImpl()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual 2	com/mysql/jdbc/JDBC4MultiHostMySQLConnection:getThisAsProxy	()Lcom/mysql/jdbc/MultiHostConnectionProxy;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: invokespecial 5	com/mysql/jdbc/JDBC4MultiHostMySQLConnection:getJDBC4Connection	()Lcom/mysql/jdbc/JDBC4Connection;
/*     */     //   11: invokevirtual 31	com/mysql/jdbc/JDBC4Connection:getClientInfoProviderImpl	()Lcom/mysql/jdbc/JDBC4ClientInfoProvider;
/*     */     //   14: aload_1
/*     */     //   15: monitorexit
/*     */     //   16: areturn
/*     */     //   17: astore_2
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: aload_2
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #124	-> byte code offset #0
/*     */     //   Java source line #125	-> byte code offset #7
/*     */     //   Java source line #126	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	JDBC4MultiHostMySQLConnection
/*     */     //   5	14	1	Ljava/lang/Object;	Object
/*     */     //   17	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4MultiHostMySQLConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */