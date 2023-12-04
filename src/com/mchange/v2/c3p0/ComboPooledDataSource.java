/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.beans.VetoableChangeListener;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ComboPooledDataSource
/*     */   extends AbstractPoolBackedDataSource
/*     */   implements PooledDataSource, Serializable, Referenceable
/*     */ {
/*  44 */   static final MLogger logger = MLog.getLogger(ComboPooledDataSource.class);
/*     */   
/*  46 */   static final Set TO_STRING_IGNORE_PROPS = new HashSet(Arrays.asList(new String[] { "connection", "lastAcquisitionFailureDefaultUser", "lastCheckinFailureDefaultUser", "lastCheckoutFailureDefaultUser", "lastConnectionTestFailureDefaultUser", "lastIdleTestFailureDefaultUser", "logWriter", "loginTimeout", "numBusyConnections", "numBusyConnectionsAllUsers", "numBusyConnectionsDefaultUser", "numConnections", "numConnectionsAllUsers", "numConnectionsDefaultUser", "numFailedCheckinsDefaultUser", "numFailedCheckoutsDefaultUser", "numFailedIdleTestsDefaultUser", "numIdleConnections", "numIdleConnectionsAllUsers", "numIdleConnectionsDefaultUser", "numUnclosedOrphanedConnections", "numUnclosedOrphanedConnectionsAllUsers", "numUnclosedOrphanedConnectionsDefaultUser", "numUserPools", "effectivePropertyCycleDefaultUser", "startTimeMillisDefaultUser", "statementCacheNumCheckedOutDefaultUser", "statementCacheNumCheckedOutStatementsAllUsers", "statementCacheNumConnectionsWithCachedStatementsAllUsers", "statementCacheNumConnectionsWithCachedStatementsDefaultUser", "statementCacheNumStatementsAllUsers", "statementCacheNumStatementsDefaultUser", "threadPoolSize", "threadPoolNumActiveThreads", "threadPoolNumIdleThreads", "threadPoolNumTasksPending", "threadPoolStackTraces", "threadPoolStatus", "overrideDefaultUser", "overrideDefaultPassword", "password", "reference", "upTimeMillisDefaultUser", "user", "userOverridesAsString", "allUsers", "connectionPoolDataSource" }));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   transient DriverManagerDataSource dmds;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   transient WrapperConnectionPoolDataSource wcpds;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final short VERSION = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ComboPooledDataSource()
/*     */   {
/* 105 */     this(true);
/*     */   }
/*     */   
/*     */   public ComboPooledDataSource(boolean autoregister) {
/* 109 */     super(autoregister);
/*     */     
/*     */ 
/*     */ 
/* 113 */     this.dmds = new DriverManagerDataSource();
/* 114 */     this.wcpds = new WrapperConnectionPoolDataSource();
/*     */     
/* 116 */     this.wcpds.setNestedDataSource(this.dmds);
/*     */     try
/*     */     {
/* 119 */       setConnectionPoolDataSource(this.wcpds);
/*     */     }
/*     */     catch (PropertyVetoException e) {
/* 122 */       logger.log(MLevel.WARNING, "Hunh??? This can't happen. We haven't set up any listeners to veto the property change yet!", e);
/* 123 */       throw new RuntimeException("Hunh??? This can't happen. We haven't set up any listeners to veto the property change yet! " + e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 128 */     setUpPropertyEvents();
/*     */   }
/*     */   
/*     */   private void setUpPropertyEvents()
/*     */   {
/* 133 */     VetoableChangeListener wcpdsConsistencyEnforcer = new VetoableChangeListener()
/*     */     {
/*     */       public void vetoableChange(PropertyChangeEvent evt)
/*     */         throws PropertyVetoException
/*     */       {
/* 138 */         String propName = evt.getPropertyName();
/* 139 */         Object val = evt.getNewValue();
/*     */         
/* 141 */         if ("connectionPoolDataSource".equals(propName))
/*     */         {
/* 143 */           if ((val instanceof WrapperConnectionPoolDataSource))
/*     */           {
/* 145 */             DataSource nested = ((WrapperConnectionPoolDataSource)val).getNestedDataSource();
/* 146 */             if (!(nested instanceof DriverManagerDataSource)) {
/* 147 */               throw new PropertyVetoException("ComboPooledDataSource requires that its unpooled DataSource  be set at all times, and that it be a com.mchange.v2.c3p0.DriverManagerDataSource. Bad: " + nested, evt);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 152 */             throw new PropertyVetoException("ComboPooledDataSource requires that its ConnectionPoolDataSource  be set at all times, and that it be a com.mchange.v2.c3p0.WrapperConnectionPoolDataSource. Bad: " + val, evt);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 157 */     };
/* 158 */     addVetoableChangeListener(wcpdsConsistencyEnforcer);
/*     */     
/* 160 */     PropertyChangeListener wcpdsStateUpdater = new PropertyChangeListener()
/*     */     {
/*     */ 
/* 163 */       public void propertyChange(PropertyChangeEvent evt) { ComboPooledDataSource.this.updateLocalVarsFromCpdsProp(); }
/* 164 */     };
/* 165 */     addPropertyChangeListener(wcpdsStateUpdater);
/*     */   }
/*     */   
/*     */   private void updateLocalVarsFromCpdsProp()
/*     */   {
/* 170 */     this.wcpds = ((WrapperConnectionPoolDataSource)getConnectionPoolDataSource());
/* 171 */     this.dmds = ((DriverManagerDataSource)this.wcpds.getNestedDataSource());
/*     */   }
/*     */   
/*     */   public ComboPooledDataSource(String configName)
/*     */   {
/* 176 */     this();
/* 177 */     initializeNamedConfig(configName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 189 */     return this.dmds.getDescription();
/*     */   }
/*     */   
/* 192 */   public void setDescription(String description) { this.dmds.setDescription(description); }
/*     */   
/*     */   public String getDriverClass() {
/* 195 */     return this.dmds.getDriverClass();
/*     */   }
/*     */   
/*     */   public void setDriverClass(String driverClass) throws PropertyVetoException {
/* 199 */     this.dmds.setDriverClass(driverClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getJdbcUrl()
/*     */   {
/* 206 */     return this.dmds.getJdbcUrl();
/*     */   }
/*     */   
/*     */   public void setJdbcUrl(String jdbcUrl)
/*     */   {
/* 211 */     this.dmds.setJdbcUrl(jdbcUrl);
/* 212 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Properties getProperties()
/*     */   {
/* 221 */     return this.dmds.getProperties();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setProperties(Properties properties)
/*     */   {
/* 227 */     this.dmds.setProperties(properties);
/* 228 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getUser()
/*     */   {
/* 233 */     return this.dmds.getUser();
/*     */   }
/*     */   
/*     */   public void setUser(String user) {
/* 237 */     this.dmds.setUser(user);
/* 238 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getPassword() {
/* 242 */     return this.dmds.getPassword();
/*     */   }
/*     */   
/*     */   public void setPassword(String password) {
/* 246 */     this.dmds.setPassword(password);
/* 247 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getCheckoutTimeout()
/*     */   {
/* 252 */     return this.wcpds.getCheckoutTimeout();
/*     */   }
/*     */   
/*     */   public void setCheckoutTimeout(int checkoutTimeout) {
/* 256 */     this.wcpds.setCheckoutTimeout(checkoutTimeout);
/* 257 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getAcquireIncrement() {
/* 261 */     return this.wcpds.getAcquireIncrement();
/*     */   }
/*     */   
/*     */   public void setAcquireIncrement(int acquireIncrement) {
/* 265 */     this.wcpds.setAcquireIncrement(acquireIncrement);
/* 266 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getAcquireRetryAttempts() {
/* 270 */     return this.wcpds.getAcquireRetryAttempts();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryAttempts(int acquireRetryAttempts) {
/* 274 */     this.wcpds.setAcquireRetryAttempts(acquireRetryAttempts);
/* 275 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getAcquireRetryDelay() {
/* 279 */     return this.wcpds.getAcquireRetryDelay();
/*     */   }
/*     */   
/*     */   public void setAcquireRetryDelay(int acquireRetryDelay) {
/* 283 */     this.wcpds.setAcquireRetryDelay(acquireRetryDelay);
/* 284 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isAutoCommitOnClose() {
/* 288 */     return this.wcpds.isAutoCommitOnClose();
/*     */   }
/*     */   
/*     */   public void setAutoCommitOnClose(boolean autoCommitOnClose) {
/* 292 */     this.wcpds.setAutoCommitOnClose(autoCommitOnClose);
/* 293 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getConnectionTesterClassName() {
/* 297 */     return this.wcpds.getConnectionTesterClassName();
/*     */   }
/*     */   
/*     */   public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException {
/* 301 */     this.wcpds.setConnectionTesterClassName(connectionTesterClassName);
/* 302 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getAutomaticTestTable() {
/* 306 */     return this.wcpds.getAutomaticTestTable();
/*     */   }
/*     */   
/*     */   public void setAutomaticTestTable(String automaticTestTable) {
/* 310 */     this.wcpds.setAutomaticTestTable(automaticTestTable);
/* 311 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isForceIgnoreUnresolvedTransactions() {
/* 315 */     return this.wcpds.isForceIgnoreUnresolvedTransactions();
/*     */   }
/*     */   
/*     */   public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
/* 319 */     this.wcpds.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
/* 320 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getIdleConnectionTestPeriod() {
/* 324 */     return this.wcpds.getIdleConnectionTestPeriod();
/*     */   }
/*     */   
/*     */   public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
/* 328 */     this.wcpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
/* 329 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getInitialPoolSize() {
/* 333 */     return this.wcpds.getInitialPoolSize();
/*     */   }
/*     */   
/*     */   public void setInitialPoolSize(int initialPoolSize) {
/* 337 */     this.wcpds.setInitialPoolSize(initialPoolSize);
/* 338 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxIdleTime() {
/* 342 */     return this.wcpds.getMaxIdleTime();
/*     */   }
/*     */   
/*     */   public void setMaxIdleTime(int maxIdleTime) {
/* 346 */     this.wcpds.setMaxIdleTime(maxIdleTime);
/* 347 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxPoolSize() {
/* 351 */     return this.wcpds.getMaxPoolSize();
/*     */   }
/*     */   
/*     */   public void setMaxPoolSize(int maxPoolSize) {
/* 355 */     this.wcpds.setMaxPoolSize(maxPoolSize);
/* 356 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxStatements() {
/* 360 */     return this.wcpds.getMaxStatements();
/*     */   }
/*     */   
/*     */   public void setMaxStatements(int maxStatements) {
/* 364 */     this.wcpds.setMaxStatements(maxStatements);
/* 365 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxStatementsPerConnection() {
/* 369 */     return this.wcpds.getMaxStatementsPerConnection();
/*     */   }
/*     */   
/*     */   public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
/* 373 */     this.wcpds.setMaxStatementsPerConnection(maxStatementsPerConnection);
/* 374 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMinPoolSize() {
/* 378 */     return this.wcpds.getMinPoolSize();
/*     */   }
/*     */   
/*     */   public void setMinPoolSize(int minPoolSize) {
/* 382 */     this.wcpds.setMinPoolSize(minPoolSize);
/* 383 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getOverrideDefaultUser() {
/* 387 */     return this.wcpds.getOverrideDefaultUser();
/*     */   }
/*     */   
/*     */   public void setOverrideDefaultUser(String overrideDefaultUser) {
/* 391 */     this.wcpds.setOverrideDefaultUser(overrideDefaultUser);
/* 392 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getOverrideDefaultPassword() {
/* 396 */     return this.wcpds.getOverrideDefaultPassword();
/*     */   }
/*     */   
/*     */   public void setOverrideDefaultPassword(String overrideDefaultPassword) {
/* 400 */     this.wcpds.setOverrideDefaultPassword(overrideDefaultPassword);
/* 401 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getPropertyCycle() {
/* 405 */     return this.wcpds.getPropertyCycle();
/*     */   }
/*     */   
/*     */   public void setPropertyCycle(int propertyCycle) {
/* 409 */     this.wcpds.setPropertyCycle(propertyCycle);
/* 410 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isBreakAfterAcquireFailure() {
/* 414 */     return this.wcpds.isBreakAfterAcquireFailure();
/*     */   }
/*     */   
/*     */   public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
/* 418 */     this.wcpds.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
/* 419 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckout() {
/* 423 */     return this.wcpds.isTestConnectionOnCheckout();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
/* 427 */     this.wcpds.setTestConnectionOnCheckout(testConnectionOnCheckout);
/* 428 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isTestConnectionOnCheckin() {
/* 432 */     return this.wcpds.isTestConnectionOnCheckin();
/*     */   }
/*     */   
/*     */   public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
/* 436 */     this.wcpds.setTestConnectionOnCheckin(testConnectionOnCheckin);
/* 437 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isUsesTraditionalReflectiveProxies() {
/* 441 */     return this.wcpds.isUsesTraditionalReflectiveProxies();
/*     */   }
/*     */   
/*     */   public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
/* 445 */     this.wcpds.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
/* 446 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getPreferredTestQuery() {
/* 450 */     return this.wcpds.getPreferredTestQuery();
/*     */   }
/*     */   
/*     */   public void setPreferredTestQuery(String preferredTestQuery) {
/* 454 */     this.wcpds.setPreferredTestQuery(preferredTestQuery);
/* 455 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getUserOverridesAsString() {
/* 459 */     return this.wcpds.getUserOverridesAsString();
/*     */   }
/*     */   
/*     */   public void setUserOverridesAsString(String userOverridesAsString) throws PropertyVetoException {
/* 463 */     this.wcpds.setUserOverridesAsString(userOverridesAsString);
/* 464 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxAdministrativeTaskTime() {
/* 468 */     return this.wcpds.getMaxAdministrativeTaskTime();
/*     */   }
/*     */   
/*     */   public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
/* 472 */     this.wcpds.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
/* 473 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxIdleTimeExcessConnections() {
/* 477 */     return this.wcpds.getMaxIdleTimeExcessConnections();
/*     */   }
/*     */   
/*     */   public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
/* 481 */     this.wcpds.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
/* 482 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getMaxConnectionAge() {
/* 486 */     return this.wcpds.getMaxConnectionAge();
/*     */   }
/*     */   
/*     */   public void setMaxConnectionAge(int maxConnectionAge) {
/* 490 */     this.wcpds.setMaxConnectionAge(maxConnectionAge);
/* 491 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getConnectionCustomizerClassName() {
/* 495 */     return this.wcpds.getConnectionCustomizerClassName();
/*     */   }
/*     */   
/*     */   public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
/* 499 */     this.wcpds.setConnectionCustomizerClassName(connectionCustomizerClassName);
/* 500 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public int getUnreturnedConnectionTimeout() {
/* 504 */     return this.wcpds.getUnreturnedConnectionTimeout();
/*     */   }
/*     */   
/*     */   public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
/* 508 */     this.wcpds.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
/* 509 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public boolean isDebugUnreturnedConnectionStackTraces() {
/* 513 */     return this.wcpds.isDebugUnreturnedConnectionStackTraces();
/*     */   }
/*     */   
/*     */   public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
/* 517 */     this.wcpds.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
/* 518 */     resetPoolManager(false);
/*     */   }
/*     */   
/*     */   public String getFactoryClassLocation()
/*     */   {
/* 523 */     return super.getFactoryClassLocation();
/*     */   }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) {
/* 527 */     this.dmds.setFactoryClassLocation(factoryClassLocation);
/* 528 */     this.wcpds.setFactoryClassLocation(factoryClassLocation);
/* 529 */     super.setFactoryClassLocation(factoryClassLocation);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 536 */     StringBuffer sb = new StringBuffer(512);
/* 537 */     sb.append(getClass().getName());
/* 538 */     sb.append(" [ ");
/* 539 */     try { BeansUtils.appendPropNamesAndValues(sb, this, TO_STRING_IGNORE_PROPS);
/*     */     }
/*     */     catch (Exception e) {
/* 542 */       sb.append(e.toString());
/*     */     }
/*     */     
/* 545 */     sb.append(" ]");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 551 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 560 */     oos.writeShort(1);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 565 */     short version = ois.readShort();
/* 566 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 569 */       updateLocalVarsFromCpdsProp();
/* 570 */       setUpPropertyEvents();
/* 571 */       break;
/*     */     default: 
/* 573 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\ComboPooledDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */