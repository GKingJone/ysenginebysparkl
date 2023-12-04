/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v1.io.InputStreamUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0Defaults;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class PoolConfig
/*     */ {
/*  94 */   static final MLogger logger = MLog.getLogger(PoolConfig.class);
/*     */   public static final String INITIAL_POOL_SIZE = "c3p0.initialPoolSize"; public static final String MIN_POOL_SIZE = "c3p0.minPoolSize"; public static final String MAX_POOL_SIZE = "c3p0.maxPoolSize"; public static final String IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod"; public static final String MAX_IDLE_TIME = "c3p0.maxIdleTime"; public static final String PROPERTY_CYCLE = "c3p0.propertyCycle";
/*  96 */   static { Properties rsrcProps = findResourceProperties();
/*  97 */     PoolConfig rsrcDefaults = extractConfig(rsrcProps, null);
/*     */     Properties sysProps;
/*     */     try
/*     */     {
/* 101 */       sysProps = System.getProperties();
/*     */     }
/*     */     catch (SecurityException e) {
/* 104 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 105 */         logger.log(MLevel.WARNING, "Read of system Properties blocked -- ignoring any c3p0 configuration via System properties! (But any configuration via a c3p0.properties file is still okay!)", e);
/*     */       }
/*     */       
/*     */ 
/* 109 */       sysProps = new Properties();
/*     */     }
/* 111 */     DEFAULTS = extractConfig(sysProps, rsrcDefaults); }
/*     */   
/*     */   public static final String MAX_STATEMENTS = "c3p0.maxStatements";
/*     */   public static final String MAX_STATEMENTS_PER_CONNECTION = "c3p0.maxStatementsPerConnection"; public static final String CHECKOUT_TIMEOUT = "c3p0.checkoutTimeout"; public static final String ACQUIRE_INCREMENT = "c3p0.acquireIncrement"; public static final String ACQUIRE_RETRY_ATTEMPTS = "c3p0.acquireRetryAttempts"; public static final String ACQUIRE_RETRY_DELAY = "c3p0.acquireRetryDelay";
/* 115 */   public static int defaultNumHelperThreads() { return DEFAULTS.getNumHelperThreads(); }
/*     */   
/*     */   public static final String BREAK_AFTER_ACQUIRE_FAILURE = "c3p0.breakAfterAcquireFailure"; public static final String USES_TRADITIONAL_REFLECTIVE_PROXIES = "c3p0.usesTraditionalReflectiveProxies"; public static final String TEST_CONNECTION_ON_CHECKOUT = "c3p0.testConnectionOnCheckout"; public static final String TEST_CONNECTION_ON_CHECKIN = "c3p0.testConnectionOnCheckin";
/* 118 */   public static String defaultPreferredTestQuery() { return DEFAULTS.getPreferredTestQuery(); }
/*     */   
/*     */   public static final String CONNECTION_TESTER_CLASS_NAME = "c3p0.connectionTesterClassName"; public static final String AUTOMATIC_TEST_TABLE = "c3p0.automaticTestTable";
/* 121 */   public static String defaultFactoryClassLocation() { return DEFAULTS.getFactoryClassLocation(); }
/*     */   
/*     */   public static final String AUTO_COMMIT_ON_CLOSE = "c3p0.autoCommitOnClose"; public static final String FORCE_IGNORE_UNRESOLVED_TRANSACTIONS = "c3p0.forceIgnoreUnresolvedTransactions";
/* 124 */   public static int defaultMaxStatements() { return DEFAULTS.getMaxStatements(); }
/*     */   
/*     */   public static final String NUM_HELPER_THREADS = "c3p0.numHelperThreads"; public static final String PREFERRED_TEST_QUERY = "c3p0.preferredTestQuery";
/* 127 */   public static int defaultMaxStatementsPerConnection() { return DEFAULTS.getMaxStatementsPerConnection(); }
/*     */   
/*     */   public static final String FACTORY_CLASS_LOCATION = "c3p0.factoryClassLocation";
/* 130 */   public static int defaultInitialPoolSize() { return DEFAULTS.getInitialPoolSize(); }
/*     */   
/*     */   public static final String DEFAULT_CONFIG_RSRC_PATH = "/c3p0.properties";
/* 133 */   public static int defaultMinPoolSize() { return DEFAULTS.getMinPoolSize(); }
/*     */   
/*     */ 
/* 136 */   public static int defaultMaxPoolSize() { return DEFAULTS.getMaxPoolSize(); }
/*     */   
/*     */   public static int defaultIdleConnectionTestPeriod() {
/* 139 */     return DEFAULTS.getIdleConnectionTestPeriod();
/*     */   }
/*     */   
/* 142 */   public static int defaultMaxIdleTime() { return DEFAULTS.getMaxIdleTime(); }
/*     */   
/*     */   public static int defaultPropertyCycle() {
/* 145 */     return DEFAULTS.getPropertyCycle();
/*     */   }
/*     */   
/* 148 */   public static int defaultCheckoutTimeout() { return DEFAULTS.getCheckoutTimeout(); }
/*     */   
/*     */   public static int defaultAcquireIncrement() {
/* 151 */     return DEFAULTS.getAcquireIncrement();
/*     */   }
/*     */   
/* 154 */   public static int defaultAcquireRetryAttempts() { return DEFAULTS.getAcquireRetryAttempts(); }
/*     */   
/*     */   public static int defaultAcquireRetryDelay() {
/* 157 */     return DEFAULTS.getAcquireRetryDelay();
/*     */   }
/*     */   
/* 160 */   public static boolean defaultBreakAfterAcquireFailure() { return DEFAULTS.isBreakAfterAcquireFailure(); }
/*     */   
/*     */   public static String defaultConnectionTesterClassName() {
/* 163 */     return DEFAULTS.getConnectionTesterClassName();
/*     */   }
/*     */   
/* 166 */   public static String defaultAutomaticTestTable() { return DEFAULTS.getAutomaticTestTable(); }
/*     */   
/*     */   public static boolean defaultTestConnectionOnCheckout() {
/* 169 */     return DEFAULTS.isTestConnectionOnCheckout();
/*     */   }
/*     */   
/* 172 */   public static boolean defaultTestConnectionOnCheckin() { return DEFAULTS.isTestConnectionOnCheckin(); }
/*     */   
/*     */   public static boolean defaultAutoCommitOnClose() {
/* 175 */     return DEFAULTS.isAutoCommitOnClose();
/*     */   }
/*     */   
/* 178 */   public static boolean defaultForceIgnoreUnresolvedTransactions() { return DEFAULTS.isAutoCommitOnClose(); }
/*     */   
/*     */   public static boolean defaultUsesTraditionalReflectiveProxies() {
/* 181 */     return DEFAULTS.isUsesTraditionalReflectiveProxies();
/*     */   }
/*     */   
/*     */   static final PoolConfig DEFAULTS;
/*     */   int maxStatements;
/*     */   int maxStatementsPerConnection;
/*     */   int initialPoolSize;
/*     */   int minPoolSize;
/*     */   int maxPoolSize;
/*     */   int idleConnectionTestPeriod;
/*     */   int maxIdleTime;
/*     */   int propertyCycle;
/*     */   int checkoutTimeout;
/*     */   int acquireIncrement;
/*     */   int acquireRetryAttempts;
/*     */   int acquireRetryDelay;
/*     */   boolean breakAfterAcquireFailure;
/*     */   boolean testConnectionOnCheckout;
/*     */   boolean testConnectionOnCheckin;
/*     */   boolean autoCommitOnClose;
/*     */   boolean forceIgnoreUnresolvedTransactions;
/*     */   boolean usesTraditionalReflectiveProxies;
/*     */   String connectionTesterClassName;
/*     */   String automaticTestTable;
/*     */   int numHelperThreads;
/*     */   String preferredTestQuery;
/*     */   String factoryClassLocation;
/*     */   private PoolConfig(Properties props, boolean init) throws NumberFormatException
/*     */   {
/* 210 */     if (init)
/* 211 */       extractConfig(this, props, DEFAULTS);
/*     */   }
/*     */   
/*     */   public PoolConfig(Properties props) throws NumberFormatException {
/* 215 */     this(props, true);
/*     */   }
/*     */   
/* 218 */   public PoolConfig() throws NumberFormatException { this(null, true); }
/*     */   
/*     */   public int getNumHelperThreads() {
/* 221 */     return this.numHelperThreads;
/*     */   }
/*     */   
/* 224 */   public String getPreferredTestQuery() { return this.preferredTestQuery; }
/*     */   
/*     */   public String getFactoryClassLocation() {
/* 227 */     return this.factoryClassLocation;
/*     */   }
/*     */   
/* 230 */   public int getMaxStatements() { return this.maxStatements; }
/*     */   
/*     */   public int getMaxStatementsPerConnection() {
/* 233 */     return this.maxStatementsPerConnection;
/*     */   }
/*     */   
/* 236 */   public int getInitialPoolSize() { return this.initialPoolSize; }
/*     */   
/*     */   public int getMinPoolSize() {
/* 239 */     return this.minPoolSize;
/*     */   }
/*     */   
/* 242 */   public int getMaxPoolSize() { return this.maxPoolSize; }
/*     */   
/*     */   public int getIdleConnectionTestPeriod() {
/* 245 */     return this.idleConnectionTestPeriod;
/*     */   }
/*     */   
/* 248 */   public int getMaxIdleTime() { return this.maxIdleTime; }
/*     */   
/*     */   public int getPropertyCycle() {
/* 251 */     return this.propertyCycle;
/*     */   }
/*     */   
/* 254 */   public int getAcquireIncrement() { return this.acquireIncrement; }
/*     */   
/*     */   public int getCheckoutTimeout() {
/* 257 */     return this.checkoutTimeout;
/*     */   }
/*     */   
/* 260 */   public int getAcquireRetryAttempts() { return this.acquireRetryAttempts; }
/*     */   
/*     */   public int getAcquireRetryDelay() {
/* 263 */     return this.acquireRetryDelay;
/*     */   }
/*     */   
/* 266 */   public boolean isBreakAfterAcquireFailure() { return this.breakAfterAcquireFailure; }
/*     */   
/*     */   public boolean isUsesTraditionalReflectiveProxies() {
/* 269 */     return this.usesTraditionalReflectiveProxies;
/*     */   }
/*     */   
/* 272 */   public String getConnectionTesterClassName() { return this.connectionTesterClassName; }
/*     */   
/*     */   public String getAutomaticTestTable() {
/* 275 */     return this.automaticTestTable;
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/* 281 */   public boolean getTestConnectionOnCheckout() { return this.testConnectionOnCheckout; }
/*     */   
/*     */   public boolean isTestConnectionOnCheckout() {
/* 284 */     return getTestConnectionOnCheckout();
/*     */   }
/*     */   
/* 287 */   public boolean isTestConnectionOnCheckin() { return this.testConnectionOnCheckin; }
/*     */   
/*     */   public boolean isAutoCommitOnClose() {
/* 290 */     return this.autoCommitOnClose;
/*     */   }
/*     */   
/* 293 */   public boolean isForceIgnoreUnresolvedTransactions() { return this.forceIgnoreUnresolvedTransactions; }
/*     */   
/*     */   public void setNumHelperThreads(int numHelperThreads) {
/* 296 */     this.numHelperThreads = numHelperThreads;
/*     */   }
/*     */   
/* 299 */   public void setPreferredTestQuery(String preferredTestQuery) { this.preferredTestQuery = preferredTestQuery; }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) {
/* 302 */     this.factoryClassLocation = factoryClassLocation;
/*     */   }
/*     */   
/* 305 */   public void setMaxStatements(int maxStatements) { this.maxStatements = maxStatements; }
/*     */   
/*     */   public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
/* 308 */     this.maxStatementsPerConnection = maxStatementsPerConnection;
/*     */   }
/*     */   
/* 311 */   public void setInitialPoolSize(int initialPoolSize) { this.initialPoolSize = initialPoolSize; }
/*     */   
/*     */   public void setMinPoolSize(int minPoolSize) {
/* 314 */     this.minPoolSize = minPoolSize;
/*     */   }
/*     */   
/* 317 */   public void setMaxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; }
/*     */   
/*     */   public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
/* 320 */     this.idleConnectionTestPeriod = idleConnectionTestPeriod;
/*     */   }
/*     */   
/* 323 */   public void setMaxIdleTime(int maxIdleTime) { this.maxIdleTime = maxIdleTime; }
/*     */   
/*     */   public void setPropertyCycle(int propertyCycle) {
/* 326 */     this.propertyCycle = propertyCycle;
/*     */   }
/*     */   
/* 329 */   public void setCheckoutTimeout(int checkoutTimeout) { this.checkoutTimeout = checkoutTimeout; }
/*     */   
/*     */   public void setAcquireIncrement(int acquireIncrement) {
/* 332 */     this.acquireIncrement = acquireIncrement;
/*     */   }
/*     */   
/* 335 */   public void setAcquireRetryAttempts(int acquireRetryAttempts) { this.acquireRetryAttempts = acquireRetryAttempts; }
/*     */   
/*     */   public void setAcquireRetryDelay(int acquireRetryDelay) {
/* 338 */     this.acquireRetryDelay = acquireRetryDelay;
/*     */   }
/*     */   
/* 341 */   public void setConnectionTesterClassName(String connectionTesterClassName) { this.connectionTesterClassName = connectionTesterClassName; }
/*     */   
/*     */   public void setAutomaticTestTable(String automaticTestTable) {
/* 344 */     this.automaticTestTable = automaticTestTable;
/*     */   }
/*     */   
/* 347 */   public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) { this.breakAfterAcquireFailure = breakAfterAcquireFailure; }
/*     */   
/*     */   public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
/* 350 */     this.usesTraditionalReflectiveProxies = usesTraditionalReflectiveProxies;
/*     */   }
/*     */   
/* 353 */   public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) { this.testConnectionOnCheckout = testConnectionOnCheckout; }
/*     */   
/*     */   public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
/* 356 */     this.testConnectionOnCheckin = testConnectionOnCheckin;
/*     */   }
/*     */   
/* 359 */   public void setAutoCommitOnClose(boolean autoCommitOnClose) { this.autoCommitOnClose = autoCommitOnClose; }
/*     */   
/*     */   public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
/* 362 */     this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
/*     */   }
/*     */   
/*     */   private static PoolConfig extractConfig(Properties props, PoolConfig defaults) throws NumberFormatException {
/* 366 */     PoolConfig pcfg = new PoolConfig(null, false);
/* 367 */     extractConfig(pcfg, props, defaults);
/* 368 */     return pcfg;
/*     */   }
/*     */   
/*     */   private static void extractConfig(PoolConfig pcfg, Properties props, PoolConfig defaults) throws NumberFormatException
/*     */   {
/* 373 */     String maxStatementsStr = null;
/* 374 */     String maxStatementsPerConnectionStr = null;
/* 375 */     String initialPoolSizeStr = null;
/* 376 */     String minPoolSizeStr = null;
/* 377 */     String maxPoolSizeStr = null;
/* 378 */     String idleConnectionTestPeriodStr = null;
/* 379 */     String maxIdleTimeStr = null;
/* 380 */     String propertyCycleStr = null;
/* 381 */     String checkoutTimeoutStr = null;
/* 382 */     String acquireIncrementStr = null;
/* 383 */     String acquireRetryAttemptsStr = null;
/* 384 */     String acquireRetryDelayStr = null;
/* 385 */     String breakAfterAcquireFailureStr = null;
/* 386 */     String usesTraditionalReflectiveProxiesStr = null;
/* 387 */     String testConnectionOnCheckoutStr = null;
/* 388 */     String testConnectionOnCheckinStr = null;
/* 389 */     String autoCommitOnCloseStr = null;
/* 390 */     String forceIgnoreUnresolvedTransactionsStr = null;
/* 391 */     String connectionTesterClassName = null;
/* 392 */     String automaticTestTable = null;
/* 393 */     String numHelperThreadsStr = null;
/* 394 */     String preferredTestQuery = null;
/* 395 */     String factoryClassLocation = null;
/*     */     
/* 397 */     if (props != null)
/*     */     {
/* 399 */       maxStatementsStr = props.getProperty("c3p0.maxStatements");
/* 400 */       maxStatementsPerConnectionStr = props.getProperty("c3p0.maxStatementsPerConnection");
/* 401 */       initialPoolSizeStr = props.getProperty("c3p0.initialPoolSize");
/* 402 */       minPoolSizeStr = props.getProperty("c3p0.minPoolSize");
/* 403 */       maxPoolSizeStr = props.getProperty("c3p0.maxPoolSize");
/* 404 */       idleConnectionTestPeriodStr = props.getProperty("c3p0.idleConnectionTestPeriod");
/* 405 */       maxIdleTimeStr = props.getProperty("c3p0.maxIdleTime");
/* 406 */       propertyCycleStr = props.getProperty("c3p0.propertyCycle");
/* 407 */       checkoutTimeoutStr = props.getProperty("c3p0.checkoutTimeout");
/* 408 */       acquireIncrementStr = props.getProperty("c3p0.acquireIncrement");
/* 409 */       acquireRetryAttemptsStr = props.getProperty("c3p0.acquireRetryAttempts");
/* 410 */       acquireRetryDelayStr = props.getProperty("c3p0.acquireRetryDelay");
/* 411 */       breakAfterAcquireFailureStr = props.getProperty("c3p0.breakAfterAcquireFailure");
/* 412 */       usesTraditionalReflectiveProxiesStr = props.getProperty("c3p0.usesTraditionalReflectiveProxies");
/* 413 */       testConnectionOnCheckoutStr = props.getProperty("c3p0.testConnectionOnCheckout");
/* 414 */       testConnectionOnCheckinStr = props.getProperty("c3p0.testConnectionOnCheckin");
/* 415 */       autoCommitOnCloseStr = props.getProperty("c3p0.autoCommitOnClose");
/* 416 */       forceIgnoreUnresolvedTransactionsStr = props.getProperty("c3p0.forceIgnoreUnresolvedTransactions");
/* 417 */       connectionTesterClassName = props.getProperty("c3p0.connectionTesterClassName");
/* 418 */       automaticTestTable = props.getProperty("c3p0.automaticTestTable");
/* 419 */       numHelperThreadsStr = props.getProperty("c3p0.numHelperThreads");
/* 420 */       preferredTestQuery = props.getProperty("c3p0.preferredTestQuery");
/* 421 */       factoryClassLocation = props.getProperty("c3p0.factoryClassLocation");
/*     */     }
/*     */     
/*     */ 
/* 425 */     if (maxStatementsStr != null) {
/* 426 */       pcfg.setMaxStatements(Integer.parseInt(maxStatementsStr.trim()));
/* 427 */     } else if (defaults != null) {
/* 428 */       pcfg.setMaxStatements(defaults.getMaxStatements());
/*     */     } else {
/* 430 */       pcfg.setMaxStatements(C3P0Defaults.maxStatements());
/*     */     }
/*     */     
/* 433 */     if (maxStatementsPerConnectionStr != null) {
/* 434 */       pcfg.setMaxStatementsPerConnection(Integer.parseInt(maxStatementsPerConnectionStr.trim()));
/* 435 */     } else if (defaults != null) {
/* 436 */       pcfg.setMaxStatementsPerConnection(defaults.getMaxStatementsPerConnection());
/*     */     } else {
/* 438 */       pcfg.setMaxStatementsPerConnection(C3P0Defaults.maxStatementsPerConnection());
/*     */     }
/*     */     
/* 441 */     if (initialPoolSizeStr != null) {
/* 442 */       pcfg.setInitialPoolSize(Integer.parseInt(initialPoolSizeStr.trim()));
/* 443 */     } else if (defaults != null) {
/* 444 */       pcfg.setInitialPoolSize(defaults.getInitialPoolSize());
/*     */     } else {
/* 446 */       pcfg.setInitialPoolSize(C3P0Defaults.initialPoolSize());
/*     */     }
/*     */     
/* 449 */     if (minPoolSizeStr != null) {
/* 450 */       pcfg.setMinPoolSize(Integer.parseInt(minPoolSizeStr.trim()));
/* 451 */     } else if (defaults != null) {
/* 452 */       pcfg.setMinPoolSize(defaults.getMinPoolSize());
/*     */     } else {
/* 454 */       pcfg.setMinPoolSize(C3P0Defaults.minPoolSize());
/*     */     }
/*     */     
/* 457 */     if (maxPoolSizeStr != null) {
/* 458 */       pcfg.setMaxPoolSize(Integer.parseInt(maxPoolSizeStr.trim()));
/* 459 */     } else if (defaults != null) {
/* 460 */       pcfg.setMaxPoolSize(defaults.getMaxPoolSize());
/*     */     } else {
/* 462 */       pcfg.setMaxPoolSize(C3P0Defaults.maxPoolSize());
/*     */     }
/*     */     
/* 465 */     if (idleConnectionTestPeriodStr != null) {
/* 466 */       pcfg.setIdleConnectionTestPeriod(Integer.parseInt(idleConnectionTestPeriodStr.trim()));
/* 467 */     } else if (defaults != null) {
/* 468 */       pcfg.setIdleConnectionTestPeriod(defaults.getIdleConnectionTestPeriod());
/*     */     } else {
/* 470 */       pcfg.setIdleConnectionTestPeriod(C3P0Defaults.idleConnectionTestPeriod());
/*     */     }
/*     */     
/* 473 */     if (maxIdleTimeStr != null) {
/* 474 */       pcfg.setMaxIdleTime(Integer.parseInt(maxIdleTimeStr.trim()));
/* 475 */     } else if (defaults != null) {
/* 476 */       pcfg.setMaxIdleTime(defaults.getMaxIdleTime());
/*     */     } else {
/* 478 */       pcfg.setMaxIdleTime(C3P0Defaults.maxIdleTime());
/*     */     }
/*     */     
/* 481 */     if (propertyCycleStr != null) {
/* 482 */       pcfg.setPropertyCycle(Integer.parseInt(propertyCycleStr.trim()));
/* 483 */     } else if (defaults != null) {
/* 484 */       pcfg.setPropertyCycle(defaults.getPropertyCycle());
/*     */     } else {
/* 486 */       pcfg.setPropertyCycle(C3P0Defaults.propertyCycle());
/*     */     }
/*     */     
/* 489 */     if (checkoutTimeoutStr != null) {
/* 490 */       pcfg.setCheckoutTimeout(Integer.parseInt(checkoutTimeoutStr.trim()));
/* 491 */     } else if (defaults != null) {
/* 492 */       pcfg.setCheckoutTimeout(defaults.getCheckoutTimeout());
/*     */     } else {
/* 494 */       pcfg.setCheckoutTimeout(C3P0Defaults.checkoutTimeout());
/*     */     }
/*     */     
/* 497 */     if (acquireIncrementStr != null) {
/* 498 */       pcfg.setAcquireIncrement(Integer.parseInt(acquireIncrementStr.trim()));
/* 499 */     } else if (defaults != null) {
/* 500 */       pcfg.setAcquireIncrement(defaults.getAcquireIncrement());
/*     */     } else {
/* 502 */       pcfg.setAcquireIncrement(C3P0Defaults.acquireIncrement());
/*     */     }
/*     */     
/* 505 */     if (acquireRetryAttemptsStr != null) {
/* 506 */       pcfg.setAcquireRetryAttempts(Integer.parseInt(acquireRetryAttemptsStr.trim()));
/* 507 */     } else if (defaults != null) {
/* 508 */       pcfg.setAcquireRetryAttempts(defaults.getAcquireRetryAttempts());
/*     */     } else {
/* 510 */       pcfg.setAcquireRetryAttempts(C3P0Defaults.acquireRetryAttempts());
/*     */     }
/*     */     
/* 513 */     if (acquireRetryDelayStr != null) {
/* 514 */       pcfg.setAcquireRetryDelay(Integer.parseInt(acquireRetryDelayStr.trim()));
/* 515 */     } else if (defaults != null) {
/* 516 */       pcfg.setAcquireRetryDelay(defaults.getAcquireRetryDelay());
/*     */     } else {
/* 518 */       pcfg.setAcquireRetryDelay(C3P0Defaults.acquireRetryDelay());
/*     */     }
/*     */     
/* 521 */     if (breakAfterAcquireFailureStr != null) {
/* 522 */       pcfg.setBreakAfterAcquireFailure(Boolean.valueOf(breakAfterAcquireFailureStr.trim()).booleanValue());
/* 523 */     } else if (defaults != null) {
/* 524 */       pcfg.setBreakAfterAcquireFailure(defaults.isBreakAfterAcquireFailure());
/*     */     } else {
/* 526 */       pcfg.setBreakAfterAcquireFailure(C3P0Defaults.breakAfterAcquireFailure());
/*     */     }
/*     */     
/* 529 */     if (usesTraditionalReflectiveProxiesStr != null) {
/* 530 */       pcfg.setUsesTraditionalReflectiveProxies(Boolean.valueOf(usesTraditionalReflectiveProxiesStr.trim()).booleanValue());
/* 531 */     } else if (defaults != null) {
/* 532 */       pcfg.setUsesTraditionalReflectiveProxies(defaults.isUsesTraditionalReflectiveProxies());
/*     */     } else {
/* 534 */       pcfg.setUsesTraditionalReflectiveProxies(C3P0Defaults.usesTraditionalReflectiveProxies());
/*     */     }
/*     */     
/* 537 */     if (testConnectionOnCheckoutStr != null) {
/* 538 */       pcfg.setTestConnectionOnCheckout(Boolean.valueOf(testConnectionOnCheckoutStr.trim()).booleanValue());
/* 539 */     } else if (defaults != null) {
/* 540 */       pcfg.setTestConnectionOnCheckout(defaults.isTestConnectionOnCheckout());
/*     */     } else {
/* 542 */       pcfg.setTestConnectionOnCheckout(C3P0Defaults.testConnectionOnCheckout());
/*     */     }
/*     */     
/* 545 */     if (testConnectionOnCheckinStr != null) {
/* 546 */       pcfg.setTestConnectionOnCheckin(Boolean.valueOf(testConnectionOnCheckinStr.trim()).booleanValue());
/* 547 */     } else if (defaults != null) {
/* 548 */       pcfg.setTestConnectionOnCheckin(defaults.isTestConnectionOnCheckin());
/*     */     } else {
/* 550 */       pcfg.setTestConnectionOnCheckin(C3P0Defaults.testConnectionOnCheckin());
/*     */     }
/*     */     
/* 553 */     if (autoCommitOnCloseStr != null) {
/* 554 */       pcfg.setAutoCommitOnClose(Boolean.valueOf(autoCommitOnCloseStr.trim()).booleanValue());
/* 555 */     } else if (defaults != null) {
/* 556 */       pcfg.setAutoCommitOnClose(defaults.isAutoCommitOnClose());
/*     */     } else {
/* 558 */       pcfg.setAutoCommitOnClose(C3P0Defaults.autoCommitOnClose());
/*     */     }
/*     */     
/* 561 */     if (forceIgnoreUnresolvedTransactionsStr != null) {
/* 562 */       pcfg.setForceIgnoreUnresolvedTransactions(Boolean.valueOf(forceIgnoreUnresolvedTransactionsStr.trim()).booleanValue());
/* 563 */     } else if (defaults != null) {
/* 564 */       pcfg.setForceIgnoreUnresolvedTransactions(defaults.isForceIgnoreUnresolvedTransactions());
/*     */     } else {
/* 566 */       pcfg.setForceIgnoreUnresolvedTransactions(C3P0Defaults.forceIgnoreUnresolvedTransactions());
/*     */     }
/*     */     
/* 569 */     if (connectionTesterClassName != null) {
/* 570 */       pcfg.setConnectionTesterClassName(connectionTesterClassName.trim());
/* 571 */     } else if (defaults != null) {
/* 572 */       pcfg.setConnectionTesterClassName(defaults.getConnectionTesterClassName());
/*     */     } else {
/* 574 */       pcfg.setConnectionTesterClassName(C3P0Defaults.connectionTesterClassName());
/*     */     }
/*     */     
/* 577 */     if (automaticTestTable != null) {
/* 578 */       pcfg.setAutomaticTestTable(automaticTestTable.trim());
/* 579 */     } else if (defaults != null) {
/* 580 */       pcfg.setAutomaticTestTable(defaults.getAutomaticTestTable());
/*     */     } else {
/* 582 */       pcfg.setAutomaticTestTable(C3P0Defaults.automaticTestTable());
/*     */     }
/*     */     
/* 585 */     if (numHelperThreadsStr != null) {
/* 586 */       pcfg.setNumHelperThreads(Integer.parseInt(numHelperThreadsStr.trim()));
/* 587 */     } else if (defaults != null) {
/* 588 */       pcfg.setNumHelperThreads(defaults.getNumHelperThreads());
/*     */     } else {
/* 590 */       pcfg.setNumHelperThreads(C3P0Defaults.numHelperThreads());
/*     */     }
/*     */     
/* 593 */     if (preferredTestQuery != null) {
/* 594 */       pcfg.setPreferredTestQuery(preferredTestQuery.trim());
/* 595 */     } else if (defaults != null) {
/* 596 */       pcfg.setPreferredTestQuery(defaults.getPreferredTestQuery());
/*     */     } else {
/* 598 */       pcfg.setPreferredTestQuery(C3P0Defaults.preferredTestQuery());
/*     */     }
/*     */     
/* 601 */     if (factoryClassLocation != null) {
/* 602 */       pcfg.setFactoryClassLocation(factoryClassLocation.trim());
/* 603 */     } else if (defaults != null) {
/* 604 */       pcfg.setFactoryClassLocation(defaults.getFactoryClassLocation());
/*     */     } else
/* 606 */       pcfg.setFactoryClassLocation(C3P0Defaults.factoryClassLocation());
/*     */   }
/*     */   
/*     */   private static Properties findResourceProperties() {
/* 610 */     return MultiPropertiesConfig.readVmConfig().getPropertiesByResourcePath("/c3p0.properties");
/*     */   }
/*     */   
/*     */   private static Properties origFindResourceProperties() {
/* 614 */     Properties props = new Properties();
/*     */     
/* 616 */     InputStream is = null;
/*     */     try
/*     */     {
/* 619 */       is = PoolConfig.class.getResourceAsStream("/c3p0.properties");
/* 620 */       if (is != null) {
/* 621 */         props.load(is);
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 626 */       if (logger.isLoggable(MLevel.WARNING))
/* 627 */         logger.log(MLevel.WARNING, "An IOException occurred while trying to read Pool properties!", e);
/* 628 */       props = new Properties();
/*     */     }
/*     */     finally {
/* 631 */       InputStreamUtils.attemptClose(is);
/*     */     }
/* 633 */     return props;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\PoolConfig.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */