/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.c3p0.ConnectionTester;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class C3P0Defaults
/*     */ {
/*     */   private static final int MAX_STATEMENTS = 0;
/*     */   private static final int MAX_STATEMENTS_PER_CONNECTION = 0;
/*     */   private static final int INITIAL_POOL_SIZE = 3;
/*     */   private static final int MIN_POOL_SIZE = 3;
/*     */   private static final int MAX_POOL_SIZE = 15;
/*     */   private static final int IDLE_CONNECTION_TEST_PERIOD = 0;
/*     */   private static final int MAX_IDLE_TIME = 0;
/*     */   private static final int PROPERTY_CYCLE = 0;
/*     */   private static final int ACQUIRE_INCREMENT = 3;
/*     */   private static final int ACQUIRE_RETRY_ATTEMPTS = 30;
/*     */   private static final int ACQUIRE_RETRY_DELAY = 1000;
/*     */   private static final int CHECKOUT_TIMEOUT = 0;
/*     */   private static final int MAX_ADMINISTRATIVE_TASK_TIME = 0;
/*     */   private static final int MAX_IDLE_TIME_EXCESS_CONNECTIONS = 0;
/*     */   private static final int MAX_CONNECTION_AGE = 0;
/*     */   private static final int UNRETURNED_CONNECTION_TIMEOUT = 0;
/*     */   private static final boolean BREAK_AFTER_ACQUIRE_FAILURE = false;
/*     */   private static final boolean TEST_CONNECTION_ON_CHECKOUT = false;
/*     */   private static final boolean TEST_CONNECTION_ON_CHECKIN = false;
/*     */   private static final boolean AUTO_COMMIT_ON_CLOSE = false;
/*     */   private static final boolean FORCE_IGNORE_UNRESOLVED_TXNS = false;
/*     */   private static final boolean USES_TRADITIONAL_REFLECTIVE_PROXIES = false;
/*     */   private static final boolean DEBUG_UNRETURNED_CONNECTION_STACK_TRACES = false;
/*  59 */   private static final ConnectionTester CONNECTION_TESTER = new DefaultConnectionTester();
/*     */   
/*     */   private static final int NUM_HELPER_THREADS = 3;
/*     */   
/*  63 */   private static final String AUTOMATIC_TEST_TABLE = null;
/*  64 */   private static final String CONNECTION_CUSTOMIZER_CLASS_NAME = null;
/*  65 */   private static final String DRIVER_CLASS = null;
/*  66 */   private static final String JDBC_URL = null;
/*  67 */   private static final String OVERRIDE_DEFAULT_USER = null;
/*  68 */   private static final String OVERRIDE_DEFAULT_PASSWORD = null;
/*  69 */   private static final String PASSWORD = null;
/*  70 */   private static final String PREFERRED_TEST_QUERY = null;
/*  71 */   private static final String FACTORY_CLASS_LOCATION = null;
/*  72 */   private static final String USER_OVERRIDES_AS_STRING = null;
/*  73 */   private static final String USER = null;
/*     */   
/*     */   private static Set KNOWN_PROPERTIES;
/*     */   
/*     */   static
/*     */   {
/*  79 */     Method[] methods = C3P0Defaults.class.getMethods();
/*  80 */     Set s = new HashSet();
/*  81 */     int i = 0; for (int len = methods.length; i < len; i++)
/*     */     {
/*  83 */       Method m = methods[i];
/*  84 */       if ((Modifier.isStatic(m.getModifiers())) && (m.getParameterTypes().length == 0))
/*  85 */         s.add(m.getName());
/*     */     }
/*  87 */     KNOWN_PROPERTIES = Collections.unmodifiableSet(s);
/*     */   }
/*     */   
/*     */   public static Set getKnownProperties() {
/*  91 */     return KNOWN_PROPERTIES;
/*     */   }
/*     */   
/*  94 */   public static boolean isKnownProperty(String s) { return KNOWN_PROPERTIES.contains(s); }
/*     */   
/*     */   public static int maxStatements() {
/*  97 */     return 0;
/*     */   }
/*     */   
/* 100 */   public static int maxStatementsPerConnection() { return 0; }
/*     */   
/*     */   public static int initialPoolSize() {
/* 103 */     return 3;
/*     */   }
/*     */   
/* 106 */   public static int minPoolSize() { return 3; }
/*     */   
/*     */   public static int maxPoolSize() {
/* 109 */     return 15;
/*     */   }
/*     */   
/* 112 */   public static int idleConnectionTestPeriod() { return 0; }
/*     */   
/*     */   public static int maxIdleTime() {
/* 115 */     return 0;
/*     */   }
/*     */   
/* 118 */   public static int unreturnedConnectionTimeout() { return 0; }
/*     */   
/*     */   public static int propertyCycle() {
/* 121 */     return 0;
/*     */   }
/*     */   
/* 124 */   public static int acquireIncrement() { return 3; }
/*     */   
/*     */   public static int acquireRetryAttempts() {
/* 127 */     return 30;
/*     */   }
/*     */   
/* 130 */   public static int acquireRetryDelay() { return 1000; }
/*     */   
/*     */   public static int checkoutTimeout() {
/* 133 */     return 0;
/*     */   }
/*     */   
/* 136 */   public static String connectionCustomizerClassName() { return CONNECTION_CUSTOMIZER_CLASS_NAME; }
/*     */   
/*     */   public static ConnectionTester connectionTester() {
/* 139 */     return CONNECTION_TESTER;
/*     */   }
/*     */   
/* 142 */   public static String connectionTesterClassName() { return CONNECTION_TESTER.getClass().getName(); }
/*     */   
/*     */   public static String automaticTestTable() {
/* 145 */     return AUTOMATIC_TEST_TABLE;
/*     */   }
/*     */   
/* 148 */   public static String driverClass() { return DRIVER_CLASS; }
/*     */   
/*     */   public static String jdbcUrl() {
/* 151 */     return JDBC_URL;
/*     */   }
/*     */   
/* 154 */   public static int numHelperThreads() { return 3; }
/*     */   
/*     */   public static boolean breakAfterAcquireFailure() {
/* 157 */     return false;
/*     */   }
/*     */   
/* 160 */   public static boolean testConnectionOnCheckout() { return false; }
/*     */   
/*     */   public static boolean testConnectionOnCheckin() {
/* 163 */     return false;
/*     */   }
/*     */   
/* 166 */   public static boolean autoCommitOnClose() { return false; }
/*     */   
/*     */   public static boolean forceIgnoreUnresolvedTransactions() {
/* 169 */     return false;
/*     */   }
/*     */   
/* 172 */   public static boolean debugUnreturnedConnectionStackTraces() { return false; }
/*     */   
/*     */   public static boolean usesTraditionalReflectiveProxies() {
/* 175 */     return false;
/*     */   }
/*     */   
/* 178 */   public static String preferredTestQuery() { return PREFERRED_TEST_QUERY; }
/*     */   
/*     */   public static String userOverridesAsString() {
/* 181 */     return USER_OVERRIDES_AS_STRING;
/*     */   }
/*     */   
/* 184 */   public static String factoryClassLocation() { return FACTORY_CLASS_LOCATION; }
/*     */   
/*     */   public static String overrideDefaultUser() {
/* 187 */     return OVERRIDE_DEFAULT_USER;
/*     */   }
/*     */   
/* 190 */   public static String overrideDefaultPassword() { return OVERRIDE_DEFAULT_PASSWORD; }
/*     */   
/*     */   public static String user() {
/* 193 */     return USER;
/*     */   }
/*     */   
/* 196 */   public static String password() { return PASSWORD; }
/*     */   
/*     */   public static int maxAdministrativeTaskTime() {
/* 199 */     return 0;
/*     */   }
/*     */   
/* 202 */   public static int maxIdleTimeExcessConnections() { return 0; }
/*     */   
/*     */   public static int maxConnectionAge() {
/* 205 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0Defaults.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */