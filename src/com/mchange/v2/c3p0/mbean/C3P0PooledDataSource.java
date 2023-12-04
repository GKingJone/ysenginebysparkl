/*     */ package com.mchange.v2.c3p0.mbean;
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
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public class C3P0PooledDataSource
/*     */   implements C3P0PooledDataSourceMBean
/*     */ {
/*  45 */   private static final MLogger logger = MLog.getLogger(C3P0PooledDataSource.class);
/*     */   
/*     */   String jndiName;
/*     */   
/*  49 */   ComboPooledDataSource combods = new ComboPooledDataSource();
/*     */   
/*     */   private void rebind() throws NamingException {
/*  52 */     rebind(null);
/*     */   }
/*     */   
/*     */   private void rebind(String unbindName) throws NamingException {
/*  56 */     InitialContext ictx = new InitialContext();
/*  57 */     if (unbindName != null) {
/*  58 */       ictx.unbind(unbindName);
/*     */     }
/*  60 */     if (this.jndiName != null)
/*     */     {
/*     */ 
/*     */ 
/*  64 */       Name name = ictx.getNameParser(this.jndiName).parse(this.jndiName);
/*  65 */       Context ctx = ictx;
/*  66 */       int i = 0; for (int max = name.size() - 1; i < max; i++) {
/*     */         try
/*     */         {
/*  69 */           ctx = ctx.createSubcontext(name.get(i));
/*     */         } catch (NameAlreadyBoundException ignore) {
/*  71 */           ctx = (Context)ctx.lookup(name.get(i));
/*     */         }
/*     */       }
/*  74 */       ictx.rebind(this.jndiName, this.combods);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setJndiName(String jndiName)
/*     */     throws NamingException
/*     */   {
/*  83 */     String unbindName = this.jndiName;
/*  84 */     this.jndiName = jndiName;
/*  85 */     rebind(unbindName);
/*     */   }
/*     */   
/*     */   public String getJndiName() {
/*  89 */     return this.jndiName;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/*  93 */     return this.combods.getDescription();
/*     */   }
/*     */   
/*     */   public void setDescription(String description) throws NamingException {
/*  97 */     this.combods.setDescription(description);
/*  98 */     rebind();
/*     */   }
/*     */   
/*     */   public String getDriverClass() {
/* 102 */     return this.combods.getDriverClass();
/*     */   }
/*     */   
/*     */   public void setDriverClass(String driverClass) throws PropertyVetoException, NamingException {
/* 106 */     this.combods.setDriverClass(driverClass);
/* 107 */     rebind();
/*     */   }
/*     */   
/*     */   public String getJdbcUrl() {
/* 111 */     return this.combods.getJdbcUrl();
/*     */   }
/*     */   
/*     */   public void setJdbcUrl(String jdbcUrl) throws NamingException {
/* 115 */     this.combods.setJdbcUrl(jdbcUrl);
/* 116 */     rebind();
/*     */   }
/*     */   
/*     */   public String getUser()
/*     */   {
/* 121 */     return this.combods.getUser();
/*     */   }
/*     */   
/*     */   public void setUser(String user) throws NamingException {
/* 125 */     this.combods.setUser(user);
/* 126 */     rebind();
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 130 */     return this.combods.getPassword();
/*     */   }
/*     */   
/*     */   public void setPassword(String password) throws NamingException {
/* 134 */     this.combods.setPassword(password);
/* 135 */     rebind();
/*     */   }
/*     */   
/*     */   public int getCheckoutTimeout()
/*     */   {
/* 140 */     return this.combods.getCheckoutTimeout();
/*     */   }
/*     */   
/*     */   public void setCheckoutTimeout(int checkoutTimeout) throws NamingException {
/* 144 */     this.combods.setCheckoutTimeout(checkoutTimeout);
/* 145 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireIncrement() {
/* 149 */     return this.combods.getAcquireIncrement();
/*     */   }
/*     */   
/*     */   public void setAcquireIncrement(int acquireIncrement) throws NamingException {
/* 153 */     this.combods.setAcquireIncrement(acquireIncrement);
/* 154 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireRetryAttempts() {
/* 158 */     return this.combods.getAcquireRetryAttempts();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryAttempts(int acquireRetryAttempts) throws NamingException {
/* 162 */     this.combods.setAcquireRetryAttempts(acquireRetryAttempts);
/* 163 */     rebind();
/*     */   }
/*     */   
/*     */   public int getAcquireRetryDelay() {
/* 167 */     return this.combods.getAcquireRetryDelay();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryDelay(int acquireRetryDelay) throws NamingException {
/* 171 */     this.combods.setAcquireRetryDelay(acquireRetryDelay);
/* 172 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isAutoCommitOnClose() {
/* 176 */     return this.combods.isAutoCommitOnClose();
/*     */   }
/*     */   
/*     */   public void setAutoCommitOnClose(boolean autoCommitOnClose) throws NamingException {
/* 180 */     this.combods.setAutoCommitOnClose(autoCommitOnClose);
/* 181 */     rebind();
/*     */   }
/*     */   
/*     */   public String getConnectionTesterClassName() {
/* 185 */     return this.combods.getConnectionTesterClassName();
/*     */   }
/*     */   
/*     */   public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException, NamingException {
/* 189 */     this.combods.setConnectionTesterClassName(connectionTesterClassName);
/* 190 */     rebind();
/*     */   }
/*     */   
/*     */   public String getAutomaticTestTable() {
/* 194 */     return this.combods.getAutomaticTestTable();
/*     */   }
/*     */   
/*     */   public void setAutomaticTestTable(String automaticTestTable) throws NamingException {
/* 198 */     this.combods.setAutomaticTestTable(automaticTestTable);
/* 199 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isForceIgnoreUnresolvedTransactions() {
/* 203 */     return this.combods.isForceIgnoreUnresolvedTransactions();
/*     */   }
/*     */   
/*     */   public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) throws NamingException {
/* 207 */     this.combods.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
/* 208 */     rebind();
/*     */   }
/*     */   
/*     */   public int getIdleConnectionTestPeriod() {
/* 212 */     return this.combods.getIdleConnectionTestPeriod();
/*     */   }
/*     */   
/*     */   public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) throws NamingException {
/* 216 */     this.combods.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
/* 217 */     rebind();
/*     */   }
/*     */   
/*     */   public int getInitialPoolSize() {
/* 221 */     return this.combods.getInitialPoolSize();
/*     */   }
/*     */   
/*     */   public void setInitialPoolSize(int initialPoolSize) throws NamingException {
/* 225 */     this.combods.setInitialPoolSize(initialPoolSize);
/* 226 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxIdleTime() {
/* 230 */     return this.combods.getMaxIdleTime();
/*     */   }
/*     */   
/*     */   public void setMaxIdleTime(int maxIdleTime) throws NamingException {
/* 234 */     this.combods.setMaxIdleTime(maxIdleTime);
/* 235 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxPoolSize() {
/* 239 */     return this.combods.getMaxPoolSize();
/*     */   }
/*     */   
/*     */   public void setMaxPoolSize(int maxPoolSize) throws NamingException {
/* 243 */     this.combods.setMaxPoolSize(maxPoolSize);
/* 244 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxStatements() {
/* 248 */     return this.combods.getMaxStatements();
/*     */   }
/*     */   
/*     */   public void setMaxStatements(int maxStatements) throws NamingException {
/* 252 */     this.combods.setMaxStatements(maxStatements);
/* 253 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMaxStatementsPerConnection() {
/* 257 */     return this.combods.getMaxStatementsPerConnection();
/*     */   }
/*     */   
/*     */   public void setMaxStatementsPerConnection(int maxStatementsPerConnection) throws NamingException {
/* 261 */     this.combods.setMaxStatementsPerConnection(maxStatementsPerConnection);
/* 262 */     rebind();
/*     */   }
/*     */   
/*     */   public int getMinPoolSize() {
/* 266 */     return this.combods.getMinPoolSize();
/*     */   }
/*     */   
/*     */   public void setMinPoolSize(int minPoolSize) throws NamingException {
/* 270 */     this.combods.setMinPoolSize(minPoolSize);
/* 271 */     rebind();
/*     */   }
/*     */   
/*     */   public int getPropertyCycle() {
/* 275 */     return this.combods.getPropertyCycle();
/*     */   }
/*     */   
/*     */   public void setPropertyCycle(int propertyCycle) throws NamingException {
/* 279 */     this.combods.setPropertyCycle(propertyCycle);
/* 280 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isBreakAfterAcquireFailure() {
/* 284 */     return this.combods.isBreakAfterAcquireFailure();
/*     */   }
/*     */   
/*     */   public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) throws NamingException {
/* 288 */     this.combods.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
/* 289 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckout() {
/* 293 */     return this.combods.isTestConnectionOnCheckout();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) throws NamingException {
/* 297 */     this.combods.setTestConnectionOnCheckout(testConnectionOnCheckout);
/* 298 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckin() {
/* 302 */     return this.combods.isTestConnectionOnCheckin();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) throws NamingException {
/* 306 */     this.combods.setTestConnectionOnCheckin(testConnectionOnCheckin);
/* 307 */     rebind();
/*     */   }
/*     */   
/*     */   public boolean isUsesTraditionalReflectiveProxies() {
/* 311 */     return this.combods.isUsesTraditionalReflectiveProxies();
/*     */   }
/*     */   
/*     */   public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) throws NamingException {
/* 315 */     this.combods.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
/* 316 */     rebind();
/*     */   }
/*     */   
/*     */   public String getPreferredTestQuery() {
/* 320 */     return this.combods.getPreferredTestQuery();
/*     */   }
/*     */   
/*     */   public void setPreferredTestQuery(String preferredTestQuery) throws NamingException {
/* 324 */     this.combods.setPreferredTestQuery(preferredTestQuery);
/* 325 */     rebind();
/*     */   }
/*     */   
/*     */   public String getDataSourceName()
/*     */   {
/* 330 */     return this.combods.getDataSourceName();
/*     */   }
/*     */   
/*     */   public void setDataSourceName(String name) throws NamingException {
/* 334 */     this.combods.setDataSourceName(name);
/* 335 */     rebind();
/*     */   }
/*     */   
/*     */   public int getNumHelperThreads() {
/* 339 */     return this.combods.getNumHelperThreads();
/*     */   }
/*     */   
/*     */   public void setNumHelperThreads(int numHelperThreads) throws NamingException {
/* 343 */     this.combods.setNumHelperThreads(numHelperThreads);
/* 344 */     rebind();
/*     */   }
/*     */   
/*     */   public String getFactoryClassLocation()
/*     */   {
/* 349 */     return this.combods.getFactoryClassLocation();
/*     */   }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) throws NamingException {
/* 353 */     this.combods.setFactoryClassLocation(factoryClassLocation);
/* 354 */     rebind();
/*     */   }
/*     */   
/*     */   public int getNumUserPools()
/*     */     throws SQLException
/*     */   {
/* 360 */     return this.combods.getNumUserPools();
/*     */   }
/*     */   
/* 363 */   public int getNumConnectionsDefaultUser() throws SQLException { return this.combods.getNumConnectionsDefaultUser(); }
/*     */   
/*     */   public int getNumIdleConnectionsDefaultUser() throws SQLException {
/* 366 */     return this.combods.getNumIdleConnectionsDefaultUser();
/*     */   }
/*     */   
/* 369 */   public int getNumBusyConnectionsDefaultUser() throws SQLException { return this.combods.getNumBusyConnectionsDefaultUser(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
/* 372 */     return this.combods.getNumUnclosedOrphanedConnectionsDefaultUser();
/*     */   }
/*     */   
/* 375 */   public int getNumConnections(String username, String password) throws SQLException { return this.combods.getNumConnections(username, password); }
/*     */   
/*     */   public int getNumIdleConnections(String username, String password) throws SQLException {
/* 378 */     return this.combods.getNumIdleConnections(username, password);
/*     */   }
/*     */   
/* 381 */   public int getNumBusyConnections(String username, String password) throws SQLException { return this.combods.getNumBusyConnections(username, password); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
/* 384 */     return this.combods.getNumUnclosedOrphanedConnections(username, password);
/*     */   }
/*     */   
/* 387 */   public int getNumConnectionsAllUsers() throws SQLException { return this.combods.getNumConnectionsAllUsers(); }
/*     */   
/*     */   public int getNumIdleConnectionsAllUsers() throws SQLException {
/* 390 */     return this.combods.getNumIdleConnectionsAllUsers();
/*     */   }
/*     */   
/* 393 */   public int getNumBusyConnectionsAllUsers() throws SQLException { return this.combods.getNumBusyConnectionsAllUsers(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
/* 396 */     return this.combods.getNumUnclosedOrphanedConnectionsAllUsers();
/*     */   }
/*     */   
/*     */   public void softResetDefaultUser() throws SQLException {
/* 400 */     this.combods.softResetDefaultUser();
/*     */   }
/*     */   
/* 403 */   public void softReset(String username, String password) throws SQLException { this.combods.softReset(username, password); }
/*     */   
/*     */   public void softResetAllUsers() throws SQLException {
/* 406 */     this.combods.softResetAllUsers();
/*     */   }
/*     */   
/* 409 */   public void hardReset() throws SQLException { this.combods.hardReset(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 412 */     this.combods.close();
/*     */   }
/*     */   
/*     */   public void create()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   public void start()
/*     */     throws Exception
/*     */   {
/* 422 */     logger.log(MLevel.INFO, "Bound C3P0 PooledDataSource to name ''{0}''. Starting...", this.jndiName);
/* 423 */     this.combods.getNumBusyConnectionsDefaultUser();
/*     */   }
/*     */   
/*     */   public void stop() {}
/*     */   
/*     */   public void destroy() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\mbean\C3P0PooledDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */