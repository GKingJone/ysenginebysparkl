/*     */ package com.mchange.v2.c3p0.jboss;
/*     */ 
/*     */ import com.mchange.v2.c3p0.ComboPooledDataSource;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.sql.SQLException;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NameAlreadyBoundException;
/*     */ import javax.naming.NameParser;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class C3P0PooledDataSource
/*     */   implements C3P0PooledDataSourceMBean
/*     */ {
/*  42 */   private static final MLogger logger = MLog.getLogger(C3P0PooledDataSource.class);
/*     */   
/*     */   String jndiName;
/*     */   
/*  46 */   ComboPooledDataSource combods = new ComboPooledDataSource();
/*     */   
/*     */   private void rebind() throws NamingException {
/*  49 */     rebind(null);
/*     */   }
/*     */   
/*     */   private void rebind(String unbindName) throws NamingException {
/*  53 */     InitialContext ictx = new InitialContext();
/*  54 */     if (unbindName != null) {
/*  55 */       ictx.unbind(unbindName);
/*     */     }
/*  57 */     if (this.jndiName != null)
/*     */     {
/*     */ 
/*     */ 
/*  61 */       Name name = ictx.getNameParser(this.jndiName).parse(this.jndiName);
/*  62 */       Context ctx = ictx;
/*  63 */       int i = 0; for (int max = name.size() - 1; i < max; i++) {
/*     */         try
/*     */         {
/*  66 */           ctx = ctx.createSubcontext(name.get(i));
/*     */         } catch (NameAlreadyBoundException ignore) {
/*  68 */           ctx = (Context)ctx.lookup(name.get(i));
/*     */         }
/*     */       }
/*  71 */       ictx.rebind(this.jndiName, this.combods);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setJndiName(String jndiName)
/*     */     throws NamingException
/*     */   {
/*  80 */     String unbindName = this.jndiName;
/*  81 */     this.jndiName = jndiName;
/*  82 */     rebind(unbindName);
/*     */   }
/*     */   
/*     */   public String getJndiName() {
/*  86 */     return this.jndiName;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/*  90 */     return this.combods.getDescription();
/*     */   }
/*     */   
/*     */   public void setDescription(String description) throws NamingException {
/*  94 */     this.combods.setDescription(description);
/*  95 */     rebind();
/*     */   }
/*     */   
/*     */   public String getDriverClass() {
/*  99 */     return this.combods.getDriverClass();
/*     */   }
/*     */   
/*     */   public void setDriverClass(String driverClass) throws PropertyVetoException, NamingException {
/* 103 */     this.combods.setDriverClass(driverClass);
/* 104 */     rebind();
/*     */   }
/*     */   
/*     */   public String getJdbcUrl() {
/* 108 */     return this.combods.getJdbcUrl();
/*     */   }
/*     */   
/*     */   public void setJdbcUrl(String jdbcUrl) throws NamingException {
/* 112 */     this.combods.setJdbcUrl(jdbcUrl);
/* 113 */     rebind();
/*     */   }
/*     */   
/*     */   public String getUser()
/*     */   {
/* 118 */     return this.combods.getUser();
/*     */   }
/*     */   
/*     */   public void setUser(String user) throws NamingException {
/* 122 */     this.combods.setUser(user);
/* 123 */     rebind();
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 127 */     return this.combods.getPassword();
/*     */   }
/*     */   
/*     */   public void setPassword(String password) throws NamingException {
/* 131 */     this.combods.setPassword(password);
/* 132 */     rebind();
/*     */   }
/*     */   
/*     */   public int getCheckoutTimeout()
/*     */   {
/* 137 */     return this.combods.getCheckoutTimeout();
/*     */   }
/*     */   
/*     */   public void setCheckoutTimeout(int checkoutTimeout) throws NamingException {
/* 141 */     this.combods.setCheckoutTimeout(checkoutTimeout);
/* 142 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireIncrement() {
/* 146 */     return this.combods.getAcquireIncrement();
/*     */   }
/*     */   
/*     */   public void setAcquireIncrement(int acquireIncrement) throws NamingException {
/* 150 */     this.combods.setAcquireIncrement(acquireIncrement);
/* 151 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireRetryAttempts() {
/* 155 */     return this.combods.getAcquireRetryAttempts();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryAttempts(int acquireRetryAttempts) throws NamingException {
/* 159 */     this.combods.setAcquireRetryAttempts(acquireRetryAttempts);
/* 160 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireRetryDelay() {
/* 164 */     return this.combods.getAcquireRetryDelay();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryDelay(int acquireRetryDelay) throws NamingException {
/* 168 */     this.combods.setAcquireRetryDelay(acquireRetryDelay);
/* 169 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isAutoCommitOnClose() {
/* 173 */     return this.combods.isAutoCommitOnClose();
/*     */   }
/*     */   
/*     */   public void setAutoCommitOnClose(boolean autoCommitOnClose) throws NamingException {
/* 177 */     this.combods.setAutoCommitOnClose(autoCommitOnClose);
/* 178 */     rebind();
/*     */   }
/*     */   
/*     */   public String getConnectionTesterClassName() {
/* 182 */     return this.combods.getConnectionTesterClassName();
/*     */   }
/*     */   
/*     */   public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException, NamingException {
/* 186 */     this.combods.setConnectionTesterClassName(connectionTesterClassName);
/* 187 */     rebind();
/*     */   }
/*     */   
/*     */   public String getAutomaticTestTable() {
/* 191 */     return this.combods.getAutomaticTestTable();
/*     */   }
/*     */   
/*     */   public void setAutomaticTestTable(String automaticTestTable) throws NamingException {
/* 195 */     this.combods.setAutomaticTestTable(automaticTestTable);
/* 196 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isForceIgnoreUnresolvedTransactions() {
/* 200 */     return this.combods.isForceIgnoreUnresolvedTransactions();
/*     */   }
/*     */   
/*     */   public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) throws NamingException {
/* 204 */     this.combods.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
/* 205 */     rebind();
/*     */   }
/*     */   
/*     */   public int getIdleConnectionTestPeriod() {
/* 209 */     return this.combods.getIdleConnectionTestPeriod();
/*     */   }
/*     */   
/*     */   public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) throws NamingException {
/* 213 */     this.combods.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
/* 214 */     rebind();
/*     */   }
/*     */   
/*     */   public int getInitialPoolSize() {
/* 218 */     return this.combods.getInitialPoolSize();
/*     */   }
/*     */   
/*     */   public void setInitialPoolSize(int initialPoolSize) throws NamingException {
/* 222 */     this.combods.setInitialPoolSize(initialPoolSize);
/* 223 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxIdleTime() {
/* 227 */     return this.combods.getMaxIdleTime();
/*     */   }
/*     */   
/*     */   public void setMaxIdleTime(int maxIdleTime) throws NamingException {
/* 231 */     this.combods.setMaxIdleTime(maxIdleTime);
/* 232 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxPoolSize() {
/* 236 */     return this.combods.getMaxPoolSize();
/*     */   }
/*     */   
/*     */   public void setMaxPoolSize(int maxPoolSize) throws NamingException {
/* 240 */     this.combods.setMaxPoolSize(maxPoolSize);
/* 241 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxStatements() {
/* 245 */     return this.combods.getMaxStatements();
/*     */   }
/*     */   
/*     */   public void setMaxStatements(int maxStatements) throws NamingException {
/* 249 */     this.combods.setMaxStatements(maxStatements);
/* 250 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxStatementsPerConnection() {
/* 254 */     return this.combods.getMaxStatementsPerConnection();
/*     */   }
/*     */   
/*     */   public void setMaxStatementsPerConnection(int maxStatementsPerConnection) throws NamingException {
/* 258 */     this.combods.setMaxStatementsPerConnection(maxStatementsPerConnection);
/* 259 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMinPoolSize() {
/* 263 */     return this.combods.getMinPoolSize();
/*     */   }
/*     */   
/*     */   public void setMinPoolSize(int minPoolSize) throws NamingException {
/* 267 */     this.combods.setMinPoolSize(minPoolSize);
/* 268 */     rebind();
/*     */   }
/*     */   
/*     */   public int getPropertyCycle() {
/* 272 */     return this.combods.getPropertyCycle();
/*     */   }
/*     */   
/*     */   public void setPropertyCycle(int propertyCycle) throws NamingException {
/* 276 */     this.combods.setPropertyCycle(propertyCycle);
/* 277 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isBreakAfterAcquireFailure() {
/* 281 */     return this.combods.isBreakAfterAcquireFailure();
/*     */   }
/*     */   
/*     */   public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) throws NamingException {
/* 285 */     this.combods.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
/* 286 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckout() {
/* 290 */     return this.combods.isTestConnectionOnCheckout();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) throws NamingException {
/* 294 */     this.combods.setTestConnectionOnCheckout(testConnectionOnCheckout);
/* 295 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckin() {
/* 299 */     return this.combods.isTestConnectionOnCheckin();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) throws NamingException {
/* 303 */     this.combods.setTestConnectionOnCheckin(testConnectionOnCheckin);
/* 304 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isUsesTraditionalReflectiveProxies() {
/* 308 */     return this.combods.isUsesTraditionalReflectiveProxies();
/*     */   }
/*     */   
/*     */   public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) throws NamingException {
/* 312 */     this.combods.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
/* 313 */     rebind();
/*     */   }
/*     */   
/*     */   public String getPreferredTestQuery() {
/* 317 */     return this.combods.getPreferredTestQuery();
/*     */   }
/*     */   
/*     */   public void setPreferredTestQuery(String preferredTestQuery) throws NamingException {
/* 321 */     this.combods.setPreferredTestQuery(preferredTestQuery);
/* 322 */     rebind();
/*     */   }
/*     */   
/*     */   public String getDataSourceName()
/*     */   {
/* 327 */     return this.combods.getDataSourceName();
/*     */   }
/*     */   
/*     */   public void setDataSourceName(String name) throws NamingException {
/* 331 */     this.combods.setDataSourceName(name);
/* 332 */     rebind();
/*     */   }
/*     */   
/*     */   public int getNumHelperThreads() {
/* 336 */     return this.combods.getNumHelperThreads();
/*     */   }
/*     */   
/*     */   public void setNumHelperThreads(int numHelperThreads) throws NamingException {
/* 340 */     this.combods.setNumHelperThreads(numHelperThreads);
/* 341 */     rebind();
/*     */   }
/*     */   
/*     */   public String getFactoryClassLocation()
/*     */   {
/* 346 */     return this.combods.getFactoryClassLocation();
/*     */   }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) throws NamingException {
/* 350 */     this.combods.setFactoryClassLocation(factoryClassLocation);
/* 351 */     rebind();
/*     */   }
/*     */   
/*     */   public int getNumUserPools()
/*     */     throws SQLException
/*     */   {
/* 357 */     return this.combods.getNumUserPools();
/*     */   }
/*     */   
/* 360 */   public int getNumConnectionsDefaultUser() throws SQLException { return this.combods.getNumConnectionsDefaultUser(); }
/*     */   
/*     */   public int getNumIdleConnectionsDefaultUser() throws SQLException {
/* 363 */     return this.combods.getNumIdleConnectionsDefaultUser();
/*     */   }
/*     */   
/* 366 */   public int getNumBusyConnectionsDefaultUser() throws SQLException { return this.combods.getNumBusyConnectionsDefaultUser(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
/* 369 */     return this.combods.getNumUnclosedOrphanedConnectionsDefaultUser();
/*     */   }
/*     */   
/* 372 */   public int getNumConnections(String username, String password) throws SQLException { return this.combods.getNumConnections(username, password); }
/*     */   
/*     */   public int getNumIdleConnections(String username, String password) throws SQLException {
/* 375 */     return this.combods.getNumIdleConnections(username, password);
/*     */   }
/*     */   
/* 378 */   public int getNumBusyConnections(String username, String password) throws SQLException { return this.combods.getNumBusyConnections(username, password); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
/* 381 */     return this.combods.getNumUnclosedOrphanedConnections(username, password);
/*     */   }
/*     */   
/* 384 */   public int getNumConnectionsAllUsers() throws SQLException { return this.combods.getNumConnectionsAllUsers(); }
/*     */   
/*     */   public int getNumIdleConnectionsAllUsers() throws SQLException {
/* 387 */     return this.combods.getNumIdleConnectionsAllUsers();
/*     */   }
/*     */   
/* 390 */   public int getNumBusyConnectionsAllUsers() throws SQLException { return this.combods.getNumBusyConnectionsAllUsers(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
/* 393 */     return this.combods.getNumUnclosedOrphanedConnectionsAllUsers();
/*     */   }
/*     */   
/*     */   public void softResetDefaultUser() throws SQLException {
/* 397 */     this.combods.softResetDefaultUser();
/*     */   }
/*     */   
/* 400 */   public void softReset(String username, String password) throws SQLException { this.combods.softReset(username, password); }
/*     */   
/*     */   public void softResetAllUsers() throws SQLException {
/* 403 */     this.combods.softResetAllUsers();
/*     */   }
/*     */   
/* 406 */   public void hardReset() throws SQLException { this.combods.hardReset(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 409 */     this.combods.close();
/*     */   }
/*     */   
/*     */   public void create()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   public void start()
/*     */     throws Exception
/*     */   {
/* 419 */     logger.log(MLevel.INFO, "Bound C3P0 PooledDataSource to name ''{0}''. Starting...", this.jndiName);
/* 420 */     this.combods.getNumBusyConnectionsDefaultUser();
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop() {}
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 431 */       this.combods.close();
/* 432 */       logger.log(MLevel.INFO, "Destroyed C3P0 PooledDataSource with name ''{0}''.", this.jndiName);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 436 */       logger.log(MLevel.INFO, "Failed to destroy C3P0 PooledDataSource.", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getConnectionCustomizerClassName() {
/* 441 */     return this.combods.getConnectionCustomizerClassName();
/*     */   }
/*     */   
/* 444 */   public float getEffectivePropertyCycle(String username, String password) throws SQLException { return this.combods.getEffectivePropertyCycle(username, password); }
/*     */   
/*     */   public float getEffectivePropertyCycleDefaultUser() throws SQLException {
/* 447 */     return this.combods.getEffectivePropertyCycleDefaultUser();
/*     */   }
/*     */   
/* 450 */   public int getMaxAdministrativeTaskTime() { return this.combods.getMaxAdministrativeTaskTime(); }
/*     */   
/*     */   public int getMaxConnectionAge() {
/* 453 */     return this.combods.getMaxConnectionAge();
/*     */   }
/*     */   
/* 456 */   public int getMaxIdleTimeExcessConnections() { return this.combods.getMaxIdleTimeExcessConnections(); }
/*     */   
/*     */   public int getUnreturnedConnectionTimeout() {
/* 459 */     return this.combods.getUnreturnedConnectionTimeout();
/*     */   }
/*     */   
/* 462 */   public boolean isDebugUnreturnedConnectionStackTraces() { return this.combods.isDebugUnreturnedConnectionStackTraces(); }
/*     */   
/*     */   public void setConnectionCustomizerClassName(String connectionCustomizerClassName) throws NamingException
/*     */   {
/* 466 */     this.combods.setConnectionCustomizerClassName(connectionCustomizerClassName);
/* 467 */     rebind();
/*     */   }
/*     */   
/*     */   public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) throws NamingException
/*     */   {
/* 472 */     this.combods.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
/* 473 */     rebind();
/*     */   }
/*     */   
/*     */   public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) throws NamingException
/*     */   {
/* 478 */     this.combods.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
/* 479 */     rebind();
/*     */   }
/*     */   
/*     */   public void setMaxConnectionAge(int maxConnectionAge) throws NamingException
/*     */   {
/* 484 */     this.combods.setMaxConnectionAge(maxConnectionAge);
/* 485 */     rebind();
/*     */   }
/*     */   
/*     */   public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) throws NamingException
/*     */   {
/* 490 */     this.combods.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
/* 491 */     rebind();
/*     */   }
/*     */   
/*     */   public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) throws NamingException
/*     */   {
/* 496 */     this.combods.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
/* 497 */     rebind();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\jboss\C3P0PooledDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */