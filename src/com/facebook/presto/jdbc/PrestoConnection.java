/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.client.ClientSession;
/*     */ import com.facebook.presto.jdbc.internal.client.ServerInfo;
/*     */ import com.facebook.presto.jdbc.internal.client.StatementClient;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Struct;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Objects;
/*     */ import java.util.Properties;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrestoConnection
/*     */   implements Connection
/*     */ {
/*  60 */   private final AtomicBoolean closed = new AtomicBoolean();
/*  61 */   private final AtomicReference<String> catalog = new AtomicReference();
/*  62 */   private final AtomicReference<String> schema = new AtomicReference();
/*  63 */   private final AtomicReference<String> timeZoneId = new AtomicReference();
/*  64 */   private final AtomicReference<Locale> locale = new AtomicReference();
/*     */   
/*     */   private final URI jdbcUri;
/*     */   private final URI httpUri;
/*     */   private final String user;
/*  69 */   private final Map<String, String> clientInfo = new ConcurrentHashMap();
/*  70 */   private final Map<String, String> sessionProperties = new ConcurrentHashMap();
/*  71 */   private final AtomicReference<String> transactionId = new AtomicReference();
/*     */   private final QueryExecutor queryExecutor;
/*     */   
/*     */   PrestoConnection(PrestoDriverUri uri, String user, QueryExecutor queryExecutor)
/*     */     throws SQLException
/*     */   {
/*  77 */     Objects.requireNonNull(uri, "uri is null");
/*  78 */     this.jdbcUri = uri.getJdbcUri();
/*  79 */     this.httpUri = uri.getHttpUri();
/*  80 */     this.schema.set(uri.getSchema());
/*  81 */     this.catalog.set(uri.getCatalog());
/*     */     
/*  83 */     this.user = ((String)Objects.requireNonNull(user, "user is null"));
/*  84 */     this.queryExecutor = ((QueryExecutor)Objects.requireNonNull(queryExecutor, "queryExecutor is null"));
/*  85 */     this.timeZoneId.set(TimeZone.getDefault().getID());
/*  86 */     this.locale.set(Locale.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */   public Statement createStatement()
/*     */     throws SQLException
/*     */   {
/*  93 */     checkOpen();
/*  94 */     return new PrestoStatement(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql)
/*     */     throws SQLException
/*     */   {
/* 101 */     checkOpen();
/* 102 */     throw new NotImplementedException("Connection", "prepareStatement");
/*     */   }
/*     */   
/*     */ 
/*     */   public CallableStatement prepareCall(String sql)
/*     */     throws SQLException
/*     */   {
/* 109 */     throw new NotImplementedException("Connection", "prepareCall");
/*     */   }
/*     */   
/*     */ 
/*     */   public String nativeSQL(String sql)
/*     */     throws SQLException
/*     */   {
/* 116 */     checkOpen();
/* 117 */     return sql;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAutoCommit(boolean autoCommit)
/*     */     throws SQLException
/*     */   {
/* 124 */     checkOpen();
/* 125 */     if (!autoCommit) {
/* 126 */       throw new SQLFeatureNotSupportedException("Disabling auto-commit mode not supported");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAutoCommit()
/*     */     throws SQLException
/*     */   {
/* 134 */     checkOpen();
/* 135 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void commit()
/*     */     throws SQLException
/*     */   {
/* 142 */     checkOpen();
/* 143 */     if (getAutoCommit()) {
/* 144 */       throw new SQLException("Connection is in auto-commit mode");
/*     */     }
/* 146 */     throw new NotImplementedException("Connection", "commit");
/*     */   }
/*     */   
/*     */ 
/*     */   public void rollback()
/*     */     throws SQLException
/*     */   {
/* 153 */     checkOpen();
/* 154 */     if (getAutoCommit()) {
/* 155 */       throw new SQLException("Connection is in auto-commit mode");
/*     */     }
/* 157 */     throw new NotImplementedException("Connection", "rollback");
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 164 */     this.closed.set(true);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */     throws SQLException
/*     */   {
/* 171 */     return this.closed.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public DatabaseMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/* 178 */     return new PrestoDatabaseMetaData(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */     throws SQLException
/*     */   {
/* 185 */     checkOpen();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isReadOnly()
/*     */     throws SQLException
/*     */   {
/* 193 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCatalog(String catalog)
/*     */     throws SQLException
/*     */   {
/* 200 */     checkOpen();
/* 201 */     this.catalog.set(catalog);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCatalog()
/*     */     throws SQLException
/*     */   {
/* 208 */     checkOpen();
/* 209 */     return (String)this.catalog.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTransactionIsolation(int level)
/*     */     throws SQLException
/*     */   {
/* 216 */     checkOpen();
/* 217 */     throw new SQLFeatureNotSupportedException("Transactions are not yet supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransactionIsolation()
/*     */     throws SQLException
/*     */   {
/* 224 */     checkOpen();
/* 225 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public SQLWarning getWarnings()
/*     */     throws SQLException
/*     */   {
/* 232 */     checkOpen();
/* 233 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearWarnings()
/*     */     throws SQLException
/*     */   {
/* 240 */     checkOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 247 */     checkResultSet(resultSetType, resultSetConcurrency);
/* 248 */     return createStatement();
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 255 */     checkResultSet(resultSetType, resultSetConcurrency);
/* 256 */     return prepareStatement(sql);
/*     */   }
/*     */   
/*     */ 
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 263 */     checkResultSet(resultSetType, resultSetConcurrency);
/* 264 */     throw new SQLFeatureNotSupportedException("prepareCall");
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Class<?>> getTypeMap()
/*     */     throws SQLException
/*     */   {
/* 271 */     throw new SQLFeatureNotSupportedException("getTypeMap");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTypeMap(Map<String, Class<?>> map)
/*     */     throws SQLException
/*     */   {
/* 278 */     throw new SQLFeatureNotSupportedException("setTypeMap");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setHoldability(int holdability)
/*     */     throws SQLException
/*     */   {
/* 285 */     checkOpen();
/* 286 */     if (holdability != 1) {
/* 287 */       throw new SQLFeatureNotSupportedException("Changing holdability not supported");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHoldability()
/*     */     throws SQLException
/*     */   {
/* 295 */     checkOpen();
/* 296 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public Savepoint setSavepoint()
/*     */     throws SQLException
/*     */   {
/* 303 */     throw new SQLFeatureNotSupportedException("setSavepoint");
/*     */   }
/*     */   
/*     */ 
/*     */   public Savepoint setSavepoint(String name)
/*     */     throws SQLException
/*     */   {
/* 310 */     throw new SQLFeatureNotSupportedException("setSavepoint");
/*     */   }
/*     */   
/*     */ 
/*     */   public void rollback(Savepoint savepoint)
/*     */     throws SQLException
/*     */   {
/* 317 */     throw new SQLFeatureNotSupportedException("rollback");
/*     */   }
/*     */   
/*     */ 
/*     */   public void releaseSavepoint(Savepoint savepoint)
/*     */     throws SQLException
/*     */   {
/* 324 */     throw new SQLFeatureNotSupportedException("releaseSavepoint");
/*     */   }
/*     */   
/*     */ 
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 331 */     checkHoldability(resultSetHoldability);
/* 332 */     return createStatement(resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 339 */     checkHoldability(resultSetHoldability);
/* 340 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */ 
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 347 */     checkHoldability(resultSetHoldability);
/* 348 */     return prepareCall(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
/*     */     throws SQLException
/*     */   {
/* 355 */     if (autoGeneratedKeys != 1) {
/* 356 */       throw new SQLFeatureNotSupportedException("Auto generated keys must be NO_GENERATED_KEYS");
/*     */     }
/* 358 */     return prepareStatement(sql);
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
/*     */     throws SQLException
/*     */   {
/* 365 */     throw new SQLFeatureNotSupportedException("prepareStatement");
/*     */   }
/*     */   
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, String[] columnNames)
/*     */     throws SQLException
/*     */   {
/* 372 */     throw new SQLFeatureNotSupportedException("prepareStatement");
/*     */   }
/*     */   
/*     */ 
/*     */   public Clob createClob()
/*     */     throws SQLException
/*     */   {
/* 379 */     throw new SQLFeatureNotSupportedException("createClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public Blob createBlob()
/*     */     throws SQLException
/*     */   {
/* 386 */     throw new SQLFeatureNotSupportedException("createBlob");
/*     */   }
/*     */   
/*     */ 
/*     */   public NClob createNClob()
/*     */     throws SQLException
/*     */   {
/* 393 */     throw new SQLFeatureNotSupportedException("createNClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public SQLXML createSQLXML()
/*     */     throws SQLException
/*     */   {
/* 400 */     throw new SQLFeatureNotSupportedException("createSQLXML");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/* 407 */     if (timeout < 0) {
/* 408 */       throw new SQLException("Timeout is negative");
/*     */     }
/* 410 */     return !isClosed();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClientInfo(String name, String value)
/*     */     throws SQLClientInfoException
/*     */   {
/* 417 */     Objects.requireNonNull(name, "name is null");
/* 418 */     if (value != null) {
/* 419 */       this.clientInfo.put(name, value);
/*     */     }
/*     */     else {
/* 422 */       this.clientInfo.remove(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClientInfo(Properties properties)
/*     */     throws SQLClientInfoException
/*     */   {
/* 430 */     this.clientInfo.putAll(Maps.fromProperties(properties));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getClientInfo(String name)
/*     */     throws SQLException
/*     */   {
/* 437 */     return (String)this.clientInfo.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public Properties getClientInfo()
/*     */     throws SQLException
/*     */   {
/* 444 */     Properties properties = new Properties();
/* 445 */     for (Entry<String, String> entry : this.clientInfo.entrySet()) {
/* 446 */       properties.setProperty((String)entry.getKey(), (String)entry.getValue());
/*     */     }
/* 448 */     return properties;
/*     */   }
/*     */   
/*     */ 
/*     */   public Array createArrayOf(String typeName, Object[] elements)
/*     */     throws SQLException
/*     */   {
/* 455 */     throw new SQLFeatureNotSupportedException("createArrayOf");
/*     */   }
/*     */   
/*     */ 
/*     */   public Struct createStruct(String typeName, Object[] attributes)
/*     */     throws SQLException
/*     */   {
/* 462 */     throw new SQLFeatureNotSupportedException("createStruct");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSchema(String schema)
/*     */     throws SQLException
/*     */   {
/* 469 */     checkOpen();
/* 470 */     this.schema.set(schema);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSchema()
/*     */     throws SQLException
/*     */   {
/* 477 */     checkOpen();
/* 478 */     return (String)this.schema.get();
/*     */   }
/*     */   
/*     */   public String getTimeZoneId()
/*     */   {
/* 483 */     return (String)this.timeZoneId.get();
/*     */   }
/*     */   
/*     */   public void setTimeZoneId(String timeZoneId)
/*     */   {
/* 488 */     Objects.requireNonNull(timeZoneId, "timeZoneId is null");
/* 489 */     this.timeZoneId.set(timeZoneId);
/*     */   }
/*     */   
/*     */   public Locale getLocale()
/*     */   {
/* 494 */     return (Locale)this.locale.get();
/*     */   }
/*     */   
/*     */   public void setLocale(Locale locale)
/*     */   {
/* 499 */     this.locale.set(locale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSessionProperty(String name, String value)
/*     */   {
/* 507 */     Objects.requireNonNull(name, "name is null");
/* 508 */     Objects.requireNonNull(value, "value is null");
/* 509 */     Preconditions.checkArgument(!name.isEmpty(), "name is empty");
/*     */     
/* 511 */     CharsetEncoder charsetEncoder = StandardCharsets.US_ASCII.newEncoder();
/* 512 */     Preconditions.checkArgument(name.indexOf('=') < 0, "Session property name must not contain '=': %s", new Object[] { name });
/* 513 */     Preconditions.checkArgument(charsetEncoder.canEncode(name), "Session property name is not US_ASCII: %s", new Object[] { name });
/* 514 */     Preconditions.checkArgument(charsetEncoder.canEncode(value), "Session property value is not US_ASCII: %s", new Object[] { value });
/*     */     
/* 516 */     this.sessionProperties.put(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public void abort(Executor executor)
/*     */     throws SQLException
/*     */   {
/* 523 */     close();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNetworkTimeout(Executor executor, int milliseconds)
/*     */     throws SQLException
/*     */   {
/* 530 */     throw new SQLFeatureNotSupportedException("setNetworkTimeout");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetworkTimeout()
/*     */     throws SQLException
/*     */   {
/* 537 */     throw new SQLFeatureNotSupportedException("getNetworkTimeout");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/* 545 */     if (isWrapperFor(iface)) {
/* 546 */       return this;
/*     */     }
/* 548 */     throw new SQLException("No wrapper for " + iface);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 555 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */   URI getURI()
/*     */   {
/* 560 */     return this.jdbcUri;
/*     */   }
/*     */   
/*     */   String getUser()
/*     */   {
/* 565 */     return this.user;
/*     */   }
/*     */   
/*     */   ServerInfo getServerInfo()
/*     */   {
/* 570 */     return this.queryExecutor.getServerInfo(this.httpUri);
/*     */   }
/*     */   
/*     */   StatementClient startQuery(String sql)
/*     */   {
/* 575 */     String source = (String)MoreObjects.firstNonNull(this.clientInfo.get("ApplicationName"), "presto-jdbc");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 586 */     ClientSession session = new ClientSession(this.httpUri, this.user, source, (String)this.catalog.get(), (String)this.schema.get(), (String)this.timeZoneId.get(), (Locale)this.locale.get(), ImmutableMap.copyOf(this.sessionProperties), (String)this.transactionId.get(), false, new Duration(2.0D, TimeUnit.MINUTES));
/*     */     
/*     */ 
/*     */ 
/* 590 */     return this.queryExecutor.startQuery(session, sql);
/*     */   }
/*     */   
/*     */   private void checkOpen()
/*     */     throws SQLException
/*     */   {
/* 596 */     if (isClosed()) {
/* 597 */       throw new SQLException("Connection is closed");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkResultSet(int resultSetType, int resultSetConcurrency)
/*     */     throws SQLFeatureNotSupportedException
/*     */   {
/* 604 */     if (resultSetType != 1003) {
/* 605 */       throw new SQLFeatureNotSupportedException("Result set type must be TYPE_FORWARD_ONLY");
/*     */     }
/* 607 */     if (resultSetConcurrency != 1007) {
/* 608 */       throw new SQLFeatureNotSupportedException("Result set concurrency must be CONCUR_READ_ONLY");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkHoldability(int resultSetHoldability)
/*     */     throws SQLFeatureNotSupportedException
/*     */   {
/* 615 */     if (resultSetHoldability != 1) {
/* 616 */       throw new SQLFeatureNotSupportedException("Result set holdability must be HOLD_CURSORS_OVER_COMMIT");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */