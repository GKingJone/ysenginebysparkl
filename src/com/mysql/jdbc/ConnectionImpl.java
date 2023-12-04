/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.log.NullLogger;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLPermission;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.Executor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ConnectionImpl
/*      */   extends ConnectionPropertiesImpl
/*      */   implements MySQLConnection
/*      */ {
/*      */   private static final long serialVersionUID = 2877471301981509474L;
/*   84 */   private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
/*      */   
/*   86 */   private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
/*      */   public static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
/*      */   
/*      */   public String getHost()
/*      */   {
/*   91 */     return this.host;
/*      */   }
/*      */   
/*      */   public String getHostPortPair() {
/*   95 */     return this.host + ":" + this.port;
/*      */   }
/*      */   
/*   98 */   private MySQLConnection proxy = null;
/*   99 */   private InvocationHandler realProxy = null;
/*      */   
/*      */   public boolean isProxySet() {
/*  102 */     return this.proxy != null;
/*      */   }
/*      */   
/*      */   public void setProxy(MySQLConnection proxy) {
/*  106 */     this.proxy = proxy;
/*  107 */     this.realProxy = ((this.proxy instanceof MultiHostMySQLConnection) ? ((MultiHostMySQLConnection)proxy).getThisAsProxy() : null);
/*      */   }
/*      */   
/*      */ 
/*      */   private MySQLConnection getProxy()
/*      */   {
/*  113 */     return this.proxy != null ? this.proxy : this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public MySQLConnection getLoadBalanceSafeProxy()
/*      */   {
/*  121 */     return getMultiHostSafeProxy();
/*      */   }
/*      */   
/*      */   public MySQLConnection getMultiHostSafeProxy() {
/*  125 */     return getProxy();
/*      */   }
/*      */   
/*      */   public Object getConnectionMutex() {
/*  129 */     return this.realProxy != null ? this.realProxy : getProxy();
/*      */   }
/*      */   
/*      */   public class ExceptionInterceptorChain implements ExceptionInterceptor {
/*      */     private List<Extension> interceptors;
/*      */     
/*      */     ExceptionInterceptorChain(String interceptorClasses) throws SQLException {
/*  136 */       this.interceptors = Util.loadExtensions(ConnectionImpl.this, ConnectionImpl.this.props, interceptorClasses, "Connection.BadExceptionInterceptor", this);
/*      */     }
/*      */     
/*      */     void addRingZero(ExceptionInterceptor interceptor) throws SQLException
/*      */     {
/*  141 */       this.interceptors.add(0, interceptor);
/*      */     }
/*      */     
/*      */     public SQLException interceptException(SQLException sqlEx, Connection conn) {
/*  145 */       if (this.interceptors != null) {
/*  146 */         Iterator<Extension> iter = this.interceptors.iterator();
/*      */         
/*  148 */         while (iter.hasNext()) {
/*  149 */           sqlEx = ((ExceptionInterceptor)iter.next()).interceptException(sqlEx, ConnectionImpl.this);
/*      */         }
/*      */       }
/*      */       
/*  153 */       return sqlEx;
/*      */     }
/*      */     
/*      */     public void destroy() {
/*  157 */       if (this.interceptors != null) {
/*  158 */         Iterator<Extension> iter = this.interceptors.iterator();
/*      */         
/*  160 */         while (iter.hasNext()) {
/*  161 */           ((ExceptionInterceptor)iter.next()).destroy();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public void init(Connection conn, Properties properties) throws SQLException
/*      */     {
/*  168 */       if (this.interceptors != null) {
/*  169 */         Iterator<Extension> iter = this.interceptors.iterator();
/*      */         
/*  171 */         while (iter.hasNext()) {
/*  172 */           ((ExceptionInterceptor)iter.next()).init(conn, properties);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     public List<Extension> getInterceptors() {
/*  178 */       return this.interceptors;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   static class CompoundCacheKey
/*      */   {
/*      */     String componentOne;
/*      */     
/*      */ 
/*      */     String componentTwo;
/*      */     
/*      */     int hashCode;
/*      */     
/*      */ 
/*      */     CompoundCacheKey(String partOne, String partTwo)
/*      */     {
/*  196 */       this.componentOne = partOne;
/*  197 */       this.componentTwo = partTwo;
/*      */       
/*      */ 
/*  200 */       this.hashCode = ((this.componentOne != null ? this.componentOne : "") + this.componentTwo).hashCode();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  210 */       if ((obj instanceof CompoundCacheKey)) {
/*  211 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*      */         
/*  213 */         boolean firstPartEqual = false;
/*      */         
/*  215 */         if (this.componentOne == null) {
/*  216 */           firstPartEqual = another.componentOne == null;
/*      */         } else {
/*  218 */           firstPartEqual = this.componentOne.equals(another.componentOne);
/*      */         }
/*      */         
/*  221 */         return (firstPartEqual) && (this.componentTwo.equals(another.componentTwo));
/*      */       }
/*      */       
/*  224 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  234 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  242 */   private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */   public static Map<?, ?> charsetMap;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int HISTOGRAM_BUCKETS = 20;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String LOGGER_INSTANCE_NAME = "MySQL";
/*      */   
/*      */ 
/*      */ 
/*  262 */   private static Map<String, Integer> mapTransIsolationNameToValue = null;
/*      */   
/*      */ 
/*  265 */   private static final Log NULL_LOGGER = new NullLogger("MySQL");
/*      */   
/*      */ 
/*      */ 
/*      */   protected static Map<?, ?> roundRobinStatsMap;
/*      */   
/*      */ 
/*  272 */   private static final Map<String, Map<Long, String>> dynamicIndexToCollationMapByUrl = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  277 */   private static final Map<String, Map<Integer, String>> dynamicIndexToCharsetMapByUrl = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  282 */   private static final Map<String, Map<Integer, String>> customIndexToCharsetMapByUrl = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  287 */   private static final Map<String, Map<String, Integer>> customCharsetToMblenMapByUrl = new HashMap();
/*      */   
/*      */   private CacheAdapter<String, Map<String, String>> serverConfigCache;
/*      */   
/*      */   private long queryTimeCount;
/*      */   
/*      */   private double queryTimeSum;
/*      */   
/*      */   private double queryTimeSumSquares;
/*      */   
/*      */   private double queryTimeMean;
/*      */   
/*      */   private transient Timer cancelTimer;
/*      */   private List<Extension> connectionLifecycleInterceptors;
/*      */   private static final Constructor<?> JDBC_4_CONNECTION_CTOR;
/*      */   private static final int DEFAULT_RESULT_SET_TYPE = 1003;
/*      */   private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
/*      */   
/*      */   static
/*      */   {
/*  307 */     mapTransIsolationNameToValue = new HashMap(8);
/*  308 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", Integer.valueOf(1));
/*  309 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", Integer.valueOf(1));
/*  310 */     mapTransIsolationNameToValue.put("READ-COMMITTED", Integer.valueOf(2));
/*  311 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", Integer.valueOf(4));
/*  312 */     mapTransIsolationNameToValue.put("SERIALIZABLE", Integer.valueOf(8));
/*      */     
/*  314 */     if (Util.isJdbc4()) {
/*      */       try {
/*  316 */         JDBC_4_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4Connection").getConstructor(new Class[] { String.class, Integer.TYPE, Properties.class, String.class, String.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*  319 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  321 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  323 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  326 */       JDBC_4_CONNECTION_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor) {
/*  331 */     String origMessage = sqlEx.getMessage();
/*  332 */     String sqlState = sqlEx.getSQLState();
/*  333 */     int vendorErrorCode = sqlEx.getErrorCode();
/*      */     
/*  335 */     StringBuilder messageBuf = new StringBuilder(origMessage.length() + messageToAppend.length());
/*  336 */     messageBuf.append(origMessage);
/*  337 */     messageBuf.append(messageToAppend);
/*      */     
/*  339 */     SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(messageBuf.toString(), sqlState, vendorErrorCode, interceptor);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  347 */       Method getStackTraceMethod = null;
/*  348 */       Method setStackTraceMethod = null;
/*  349 */       Object theStackTraceAsObject = null;
/*      */       
/*  351 */       Class<?> stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/*  352 */       Class<?> stackTraceElementArrayClass = Array.newInstance(stackTraceElementClass, new int[] { 0 }).getClass();
/*      */       
/*  354 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
/*      */       
/*  356 */       setStackTraceMethod = Throwable.class.getMethod("setStackTrace", new Class[] { stackTraceElementArrayClass });
/*      */       
/*  358 */       if ((getStackTraceMethod != null) && (setStackTraceMethod != null)) {
/*  359 */         theStackTraceAsObject = getStackTraceMethod.invoke(sqlEx, new Object[0]);
/*  360 */         setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[] { theStackTraceAsObject });
/*      */       }
/*      */     }
/*      */     catch (NoClassDefFoundError noClassDefFound) {}catch (NoSuchMethodException noSuchMethodEx) {}catch (Throwable catchAll) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  370 */     return sqlExceptionWithNewMessage;
/*      */   }
/*      */   
/*      */   public Timer getCancelTimer() {
/*  374 */     synchronized (getConnectionMutex()) {
/*  375 */       if (this.cancelTimer == null) {
/*  376 */         boolean createdNamedTimer = false;
/*      */         
/*      */         try
/*      */         {
/*  380 */           Constructor<Timer> ctr = Timer.class.getConstructor(new Class[] { String.class, Boolean.TYPE });
/*      */           
/*  382 */           this.cancelTimer = ((Timer)ctr.newInstance(new Object[] { "MySQL Statement Cancellation Timer", Boolean.TRUE }));
/*  383 */           createdNamedTimer = true;
/*      */         } catch (Throwable t) {
/*  385 */           createdNamedTimer = false;
/*      */         }
/*      */         
/*  388 */         if (!createdNamedTimer) {
/*  389 */           this.cancelTimer = new Timer(true);
/*      */         }
/*      */       }
/*      */       
/*  393 */       return this.cancelTimer;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  406 */     if (!Util.isJdbc4()) {
/*  407 */       return new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
/*      */     }
/*      */     
/*  410 */     return (Connection)Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, new Object[] { hostToConnectTo, Integer.valueOf(portToConnectTo), info, databaseToConnectTo, url }, null);
/*      */   }
/*      */   
/*      */ 
/*  414 */   private static final Random random = new Random();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList)
/*      */   {
/*  423 */     int indexRange = hostList.size();
/*      */     
/*  425 */     int index = random.nextInt(indexRange);
/*      */     
/*  427 */     return index;
/*      */   }
/*      */   
/*      */   private static boolean nullSafeCompare(String s1, String s2) {
/*  431 */     if ((s1 == null) && (s2 == null)) {
/*  432 */       return true;
/*      */     }
/*      */     
/*  435 */     if ((s1 == null) && (s2 != null)) {
/*  436 */       return false;
/*      */     }
/*      */     
/*  439 */     return (s1 != null) && (s1.equals(s2));
/*      */   }
/*      */   
/*      */ 
/*  443 */   private boolean autoCommit = true;
/*      */   
/*      */ 
/*      */ 
/*      */   private CacheAdapter<String, PreparedStatement.ParseInfo> cachedPreparedStatementParams;
/*      */   
/*      */ 
/*      */ 
/*  451 */   private String characterSetMetadata = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  457 */   private String characterSetResultsOnServer = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  464 */   private Map<String, Object> charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
/*      */   
/*      */ 
/*  467 */   private long connectionCreationTimeMillis = 0L;
/*      */   
/*      */ 
/*      */   private long connectionId;
/*      */   
/*      */ 
/*  473 */   private String database = null;
/*      */   
/*      */ 
/*  476 */   private java.sql.DatabaseMetaData dbmd = null;
/*      */   
/*      */ 
/*      */   private TimeZone defaultTimeZone;
/*      */   
/*      */ 
/*      */   private ProfilerEventHandler eventSink;
/*      */   
/*      */ 
/*      */   private Throwable forceClosedReason;
/*      */   
/*  487 */   private boolean hasIsolationLevels = false;
/*      */   
/*      */ 
/*  490 */   private boolean hasQuotedIdentifiers = false;
/*      */   
/*      */ 
/*  493 */   private String host = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  499 */   public Map<Integer, String> indexToMysqlCharset = new HashMap();
/*      */   
/*  501 */   public Map<Integer, String> indexToCustomMysqlCharset = null;
/*      */   
/*  503 */   private Map<String, Integer> mysqlCharsetToCustomMblen = null;
/*      */   
/*      */ 
/*  506 */   private transient MysqlIO io = null;
/*      */   
/*  508 */   private boolean isClientTzUTC = false;
/*      */   
/*      */ 
/*  511 */   private boolean isClosed = true;
/*      */   
/*      */ 
/*  514 */   private boolean isInGlobalTx = false;
/*      */   
/*      */ 
/*  517 */   private boolean isRunningOnJDK13 = false;
/*      */   
/*      */ 
/*  520 */   private int isolationLevel = 2;
/*      */   
/*  522 */   private boolean isServerTzUTC = false;
/*      */   
/*      */ 
/*  525 */   private long lastQueryFinishedTime = 0L;
/*      */   
/*      */ 
/*  528 */   private transient Log log = NULL_LOGGER;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  534 */   private long longestQueryTimeMs = 0L;
/*      */   
/*      */ 
/*  537 */   private boolean lowerCaseTableNames = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  542 */   private long maximumNumberTablesAccessed = 0L;
/*      */   
/*      */ 
/*  545 */   private int sessionMaxRows = -1;
/*      */   
/*      */ 
/*      */   private long metricsLastReportedMs;
/*      */   
/*  550 */   private long minimumNumberTablesAccessed = Long.MAX_VALUE;
/*      */   
/*      */ 
/*  553 */   private String myURL = null;
/*      */   
/*      */ 
/*  556 */   private boolean needsPing = false;
/*      */   
/*  558 */   private int netBufferLength = 16384;
/*      */   
/*  560 */   private boolean noBackslashEscapes = false;
/*      */   
/*  562 */   private long numberOfPreparedExecutes = 0L;
/*      */   
/*  564 */   private long numberOfPrepares = 0L;
/*      */   
/*  566 */   private long numberOfQueriesIssued = 0L;
/*      */   
/*  568 */   private long numberOfResultSetsCreated = 0L;
/*      */   
/*      */   private long[] numTablesMetricsHistBreakpoints;
/*      */   
/*      */   private int[] numTablesMetricsHistCounts;
/*      */   
/*  574 */   private long[] oldHistBreakpoints = null;
/*      */   
/*  576 */   private int[] oldHistCounts = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  582 */   private final CopyOnWriteArrayList<Statement> openStatements = new CopyOnWriteArrayList();
/*      */   
/*      */   private LRUCache parsedCallableStatementCache;
/*      */   
/*  586 */   private boolean parserKnowsUnicode = false;
/*      */   
/*      */ 
/*  589 */   private String password = null;
/*      */   
/*      */ 
/*      */   private long[] perfMetricsHistBreakpoints;
/*      */   
/*      */ 
/*      */   private int[] perfMetricsHistCounts;
/*      */   
/*      */   private String pointOfOrigin;
/*      */   
/*  599 */   private int port = 3306;
/*      */   
/*      */ 
/*  602 */   protected Properties props = null;
/*      */   
/*      */ 
/*  605 */   private boolean readInfoMsg = false;
/*      */   
/*      */ 
/*  608 */   private boolean readOnly = false;
/*      */   
/*      */ 
/*      */   protected LRUCache resultSetMetadataCache;
/*      */   
/*      */ 
/*  614 */   private TimeZone serverTimezoneTZ = null;
/*      */   
/*      */ 
/*  617 */   private Map<String, String> serverVariables = null;
/*      */   
/*  619 */   private long shortestQueryTimeMs = Long.MAX_VALUE;
/*      */   
/*  621 */   private double totalQueryTimeMs = 0.0D;
/*      */   
/*      */ 
/*  624 */   private boolean transactionsSupported = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<String, Class<?>> typeMap;
/*      */   
/*      */ 
/*      */ 
/*  633 */   private boolean useAnsiQuotes = false;
/*      */   
/*      */ 
/*  636 */   private String user = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  642 */   private boolean useServerPreparedStmts = false;
/*      */   
/*      */   private LRUCache serverSideStatementCheckCache;
/*      */   
/*      */   private LRUCache serverSideStatementCache;
/*      */   
/*      */   private Calendar sessionCalendar;
/*      */   
/*      */   private Calendar utcCalendar;
/*      */   
/*      */   private String origHostToConnectTo;
/*      */   
/*      */   private int origPortToConnectTo;
/*      */   
/*      */   private String origDatabaseToConnectTo;
/*      */   
/*  658 */   private String errorMessageEncoding = "Cp1252";
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean usePlatformCharsetConverters;
/*      */   
/*      */ 
/*  665 */   private boolean hasTriedMasterFlag = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  671 */   private String statementComment = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean storesLowerCaseTableName;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private List<StatementInterceptorV2> statementInterceptors;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean requiresEscapingEncoder;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String hostPortPair;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url)
/*      */     throws SQLException
/*      */   {
/*  712 */     this.connectionCreationTimeMillis = System.currentTimeMillis();
/*      */     
/*  714 */     if (databaseToConnectTo == null) {
/*  715 */       databaseToConnectTo = "";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  721 */     this.origHostToConnectTo = hostToConnectTo;
/*  722 */     this.origPortToConnectTo = portToConnectTo;
/*  723 */     this.origDatabaseToConnectTo = databaseToConnectTo;
/*      */     try
/*      */     {
/*  726 */       Blob.class.getMethod("truncate", new Class[] { Long.TYPE });
/*      */       
/*  728 */       this.isRunningOnJDK13 = false;
/*      */     } catch (NoSuchMethodException nsme) {
/*  730 */       this.isRunningOnJDK13 = true;
/*      */     }
/*      */     
/*  733 */     this.sessionCalendar = new GregorianCalendar();
/*  734 */     this.utcCalendar = new GregorianCalendar();
/*  735 */     this.utcCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  744 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */     
/*  746 */     if (NonRegisteringDriver.isHostPropertiesList(hostToConnectTo)) {
/*  747 */       Properties hostSpecificProps = NonRegisteringDriver.expandHostKeyValues(hostToConnectTo);
/*      */       
/*  749 */       Enumeration<?> propertyNames = hostSpecificProps.propertyNames();
/*      */       
/*  751 */       while (propertyNames.hasMoreElements()) {
/*  752 */         String propertyName = propertyNames.nextElement().toString();
/*  753 */         String propertyValue = hostSpecificProps.getProperty(propertyName);
/*      */         
/*  755 */         info.setProperty(propertyName, propertyValue);
/*      */       }
/*      */       
/*      */     }
/*  759 */     else if (hostToConnectTo == null) {
/*  760 */       this.host = "localhost";
/*  761 */       this.hostPortPair = (this.host + ":" + portToConnectTo);
/*      */     } else {
/*  763 */       this.host = hostToConnectTo;
/*      */       
/*  765 */       if (hostToConnectTo.indexOf(":") == -1) {
/*  766 */         this.hostPortPair = (this.host + ":" + portToConnectTo);
/*      */       } else {
/*  768 */         this.hostPortPair = this.host;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  773 */     this.port = portToConnectTo;
/*      */     
/*  775 */     this.database = databaseToConnectTo;
/*  776 */     this.myURL = url;
/*  777 */     this.user = info.getProperty("user");
/*  778 */     this.password = info.getProperty("password");
/*      */     
/*  780 */     if ((this.user == null) || (this.user.equals(""))) {
/*  781 */       this.user = "";
/*      */     }
/*      */     
/*  784 */     if (this.password == null) {
/*  785 */       this.password = "";
/*      */     }
/*      */     
/*  788 */     this.props = info;
/*      */     
/*  790 */     initializeDriverProperties(info);
/*      */     
/*      */ 
/*  793 */     this.defaultTimeZone = TimeUtil.getDefaultTimeZone(getCacheDefaultTimezone());
/*      */     
/*  795 */     this.isClientTzUTC = ((!this.defaultTimeZone.useDaylightTime()) && (this.defaultTimeZone.getRawOffset() == 0));
/*      */     
/*  797 */     if (getUseUsageAdvisor()) {
/*  798 */       this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*      */     } else {
/*  800 */       this.pointOfOrigin = "";
/*      */     }
/*      */     try
/*      */     {
/*  804 */       this.dbmd = getMetaData(false, false);
/*  805 */       initializeSafeStatementInterceptors();
/*  806 */       createNewIO(false);
/*  807 */       unSafeStatementInterceptors();
/*      */     } catch (SQLException ex) {
/*  809 */       cleanup(ex);
/*      */       
/*      */ 
/*  812 */       throw ex;
/*      */     } catch (Exception ex) {
/*  814 */       cleanup(ex);
/*      */       
/*  816 */       StringBuilder mesg = new StringBuilder(128);
/*      */       
/*  818 */       if (!getParanoid()) {
/*  819 */         mesg.append("Cannot connect to MySQL server on ");
/*  820 */         mesg.append(this.host);
/*  821 */         mesg.append(":");
/*  822 */         mesg.append(this.port);
/*  823 */         mesg.append(".\n\n");
/*  824 */         mesg.append("Make sure that there is a MySQL server ");
/*  825 */         mesg.append("running on the machine/port you are trying ");
/*  826 */         mesg.append("to connect to and that the machine this software is running on ");
/*  827 */         mesg.append("is able to connect to this host/port (i.e. not firewalled). ");
/*  828 */         mesg.append("Also make sure that the server has not been started with the --skip-networking ");
/*  829 */         mesg.append("flag.\n\n");
/*      */       } else {
/*  831 */         mesg.append("Unable to connect to database.");
/*      */       }
/*      */       
/*  834 */       SQLException sqlEx = SQLError.createSQLException(mesg.toString(), "08S01", getExceptionInterceptor());
/*      */       
/*  836 */       sqlEx.initCause(ex);
/*      */       
/*  838 */       throw sqlEx;
/*      */     }
/*      */     
/*  841 */     NonRegisteringDriver.trackConnection(this);
/*      */   }
/*      */   
/*      */   public void unSafeStatementInterceptors() throws SQLException
/*      */   {
/*  846 */     ArrayList<StatementInterceptorV2> unSafedStatementInterceptors = new ArrayList(this.statementInterceptors.size());
/*      */     
/*  848 */     for (int i = 0; i < this.statementInterceptors.size(); i++) {
/*  849 */       NoSubInterceptorWrapper wrappedInterceptor = (NoSubInterceptorWrapper)this.statementInterceptors.get(i);
/*      */       
/*  851 */       unSafedStatementInterceptors.add(wrappedInterceptor.getUnderlyingInterceptor());
/*      */     }
/*      */     
/*  854 */     this.statementInterceptors = unSafedStatementInterceptors;
/*      */     
/*  856 */     if (this.io != null) {
/*  857 */       this.io.setStatementInterceptors(this.statementInterceptors);
/*      */     }
/*      */   }
/*      */   
/*      */   public void initializeSafeStatementInterceptors() throws SQLException {
/*  862 */     this.isClosed = false;
/*      */     
/*  864 */     List<Extension> unwrappedInterceptors = Util.loadExtensions(this, this.props, getStatementInterceptors(), "MysqlIo.BadStatementInterceptor", getExceptionInterceptor());
/*      */     
/*      */ 
/*  867 */     this.statementInterceptors = new ArrayList(unwrappedInterceptors.size());
/*      */     
/*  869 */     for (int i = 0; i < unwrappedInterceptors.size(); i++) {
/*  870 */       Extension interceptor = (Extension)unwrappedInterceptors.get(i);
/*      */       
/*      */ 
/*  873 */       if ((interceptor instanceof StatementInterceptor)) {
/*  874 */         if (ReflectiveStatementInterceptorAdapter.getV2PostProcessMethod(interceptor.getClass()) != null) {
/*  875 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new ReflectiveStatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */         } else {
/*  877 */           this.statementInterceptors.add(new NoSubInterceptorWrapper(new V1toV2StatementInterceptorAdapter((StatementInterceptor)interceptor)));
/*      */         }
/*      */       } else {
/*  880 */         this.statementInterceptors.add(new NoSubInterceptorWrapper((StatementInterceptorV2)interceptor));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public List<StatementInterceptorV2> getStatementInterceptorsInstances()
/*      */   {
/*  887 */     return this.statementInterceptors;
/*      */   }
/*      */   
/*      */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound)
/*      */   {
/*  892 */     if (histogramCounts == null) {
/*  893 */       createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
/*      */     } else {
/*  895 */       for (int i = 0; i < 20; i++) {
/*  896 */         if (histogramBreakpoints[i] >= value) {
/*  897 */           histogramCounts[i] += numberOfTimes;
/*      */           
/*  899 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void addToPerformanceHistogram(long value, int numberOfTimes) {
/*  906 */     checkAndCreatePerformanceHistogram();
/*      */     
/*  908 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, this.shortestQueryTimeMs == Long.MAX_VALUE ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */   
/*      */   private void addToTablesAccessedHistogram(long value, int numberOfTimes)
/*      */   {
/*  913 */     checkAndCreateTablesAccessedHistogram();
/*      */     
/*  915 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, this.minimumNumberTablesAccessed == Long.MAX_VALUE ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void buildCollationMapping()
/*      */     throws SQLException
/*      */   {
/*  927 */     Map<Integer, String> indexToCharset = null;
/*  928 */     Map<Long, String> sortedCollationMap = null;
/*  929 */     Map<Integer, String> customCharset = null;
/*  930 */     Map<String, Integer> customMblen = null;
/*      */     
/*  932 */     if (getCacheServerConfiguration()) {
/*  933 */       synchronized (dynamicIndexToCharsetMapByUrl) {
/*  934 */         indexToCharset = (Map)dynamicIndexToCharsetMapByUrl.get(getURL());
/*  935 */         sortedCollationMap = (Map)dynamicIndexToCollationMapByUrl.get(getURL());
/*  936 */         customCharset = (Map)customIndexToCharsetMapByUrl.get(getURL());
/*  937 */         customMblen = (Map)customCharsetToMblenMapByUrl.get(getURL());
/*      */       }
/*      */     }
/*      */     
/*  941 */     if (indexToCharset == null) {
/*  942 */       indexToCharset = new HashMap();
/*      */       
/*  944 */       if ((versionMeetsMinimum(4, 1, 0)) && (getDetectCustomCollations()))
/*      */       {
/*  946 */         java.sql.Statement stmt = null;
/*  947 */         ResultSet results = null;
/*      */         try
/*      */         {
/*  950 */           sortedCollationMap = new TreeMap();
/*  951 */           customCharset = new HashMap();
/*  952 */           customMblen = new HashMap();
/*      */           
/*  954 */           stmt = getMetadataSafeStatement();
/*      */           try
/*      */           {
/*  957 */             results = stmt.executeQuery("SHOW COLLATION");
/*  958 */             if (versionMeetsMinimum(5, 0, 0)) {
/*  959 */               Util.resultSetToMap(sortedCollationMap, results, 3, 2);
/*      */             } else {
/*  961 */               while (results.next()) {
/*  962 */                 sortedCollationMap.put(Long.valueOf(results.getLong(3)), results.getString(2));
/*      */               }
/*      */             }
/*      */           } catch (SQLException ex) {
/*  966 */             if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/*  967 */               throw ex;
/*      */             }
/*      */           }
/*      */           
/*  971 */           for (Iterator<Entry<Long, String>> indexIter = sortedCollationMap.entrySet().iterator(); indexIter.hasNext();) {
/*  972 */             Entry<Long, String> indexEntry = (Entry)indexIter.next();
/*      */             
/*  974 */             int collationIndex = ((Long)indexEntry.getKey()).intValue();
/*  975 */             String charsetName = (String)indexEntry.getValue();
/*      */             
/*  977 */             indexToCharset.put(Integer.valueOf(collationIndex), charsetName);
/*      */             
/*      */ 
/*  980 */             if ((collationIndex >= 255) || (!charsetName.equals(CharsetMapping.getMysqlCharsetNameForCollationIndex(Integer.valueOf(collationIndex)))))
/*      */             {
/*  982 */               customCharset.put(Integer.valueOf(collationIndex), charsetName);
/*      */             }
/*      */             
/*      */ 
/*  986 */             if (!CharsetMapping.CHARSET_NAME_TO_CHARSET.containsKey(charsetName)) {
/*  987 */               customMblen.put(charsetName, null);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  992 */           if (customMblen.size() > 0) {
/*      */             try {
/*  994 */               results = stmt.executeQuery("SHOW CHARACTER SET");
/*  995 */               while (results.next()) {
/*  996 */                 String charsetName = results.getString("Charset");
/*  997 */                 if (customMblen.containsKey(charsetName)) {
/*  998 */                   customMblen.put(charsetName, Integer.valueOf(results.getInt("Maxlen")));
/*      */                 }
/*      */               }
/*      */             } catch (SQLException ex) {
/* 1002 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1003 */                 throw ex;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1008 */           if (getCacheServerConfiguration()) {
/* 1009 */             synchronized (dynamicIndexToCharsetMapByUrl) {
/* 1010 */               dynamicIndexToCharsetMapByUrl.put(getURL(), indexToCharset);
/* 1011 */               dynamicIndexToCollationMapByUrl.put(getURL(), sortedCollationMap);
/* 1012 */               customIndexToCharsetMapByUrl.put(getURL(), customCharset);
/* 1013 */               customCharsetToMblenMapByUrl.put(getURL(), customMblen);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (SQLException ex) {
/* 1018 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 1020 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1021 */           sqlEx.initCause(ex);
/* 1022 */           throw sqlEx;
/*      */         } finally {
/* 1024 */           if (results != null) {
/*      */             try {
/* 1026 */               results.close();
/*      */             }
/*      */             catch (SQLException sqlE) {}
/*      */           }
/*      */           
/*      */ 
/* 1032 */           if (stmt != null) {
/*      */             try {
/* 1034 */               stmt.close();
/*      */             }
/*      */             catch (SQLException sqlE) {}
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1041 */         for (int i = 1; i < 255; i++) {
/* 1042 */           indexToCharset.put(Integer.valueOf(i), CharsetMapping.getMysqlCharsetNameForCollationIndex(Integer.valueOf(i)));
/*      */         }
/* 1044 */         if (getCacheServerConfiguration()) {
/* 1045 */           synchronized (dynamicIndexToCharsetMapByUrl) {
/* 1046 */             dynamicIndexToCharsetMapByUrl.put(getURL(), indexToCharset);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1053 */     this.indexToMysqlCharset = Collections.unmodifiableMap(indexToCharset);
/* 1054 */     if (customCharset != null) {
/* 1055 */       this.indexToCustomMysqlCharset = Collections.unmodifiableMap(customCharset);
/*      */     }
/* 1057 */     if (customMblen != null) {
/* 1058 */       this.mysqlCharsetToCustomMblen = Collections.unmodifiableMap(customMblen);
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean canHandleAsServerPreparedStatement(String sql) throws SQLException {
/* 1063 */     if ((sql == null) || (sql.length() == 0)) {
/* 1064 */       return true;
/*      */     }
/*      */     
/* 1067 */     if (!this.useServerPreparedStmts) {
/* 1068 */       return false;
/*      */     }
/*      */     
/* 1071 */     if (getCachePreparedStatements()) {
/* 1072 */       synchronized (this.serverSideStatementCheckCache) {
/* 1073 */         Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */         
/* 1075 */         if (flag != null) {
/* 1076 */           return flag.booleanValue();
/*      */         }
/*      */         
/* 1079 */         boolean canHandle = canHandleAsServerPreparedStatementNoCache(sql);
/*      */         
/* 1081 */         if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 1082 */           this.serverSideStatementCheckCache.put(sql, canHandle ? Boolean.TRUE : Boolean.FALSE);
/*      */         }
/*      */         
/* 1085 */         return canHandle;
/*      */       }
/*      */     }
/*      */     
/* 1089 */     return canHandleAsServerPreparedStatementNoCache(sql);
/*      */   }
/*      */   
/*      */   private boolean canHandleAsServerPreparedStatementNoCache(String sql)
/*      */     throws SQLException
/*      */   {
/* 1095 */     if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
/* 1096 */       return false;
/*      */     }
/*      */     
/* 1099 */     boolean canHandleAsStatement = true;
/*      */     
/* 1101 */     if ((!versionMeetsMinimum(5, 0, 7)) && ((StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "SELECT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "REPLACE"))))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1111 */       int currentPos = 0;
/* 1112 */       int statementLength = sql.length();
/* 1113 */       int lastPosToLook = statementLength - 7;
/* 1114 */       boolean allowBackslashEscapes = !this.noBackslashEscapes;
/* 1115 */       String quoteChar = this.useAnsiQuotes ? "\"" : "'";
/* 1116 */       boolean foundLimitWithPlaceholder = false;
/*      */       
/* 1118 */       while (currentPos < lastPosToLook) {
/* 1119 */         int limitStart = StringUtils.indexOfIgnoreCase(currentPos, sql, "LIMIT ", quoteChar, quoteChar, allowBackslashEscapes ? StringUtils.SEARCH_MODE__ALL : StringUtils.SEARCH_MODE__MRK_COM_WS);
/*      */         
/*      */ 
/* 1122 */         if (limitStart == -1) {
/*      */           break;
/*      */         }
/*      */         
/* 1126 */         currentPos = limitStart + 7;
/*      */         
/* 1128 */         while (currentPos < statementLength) {
/* 1129 */           char c = sql.charAt(currentPos);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1135 */           if ((!Character.isDigit(c)) && (!Character.isWhitespace(c)) && (c != ',') && (c != '?')) {
/*      */             break;
/*      */           }
/*      */           
/* 1139 */           if (c == '?') {
/* 1140 */             foundLimitWithPlaceholder = true;
/* 1141 */             break;
/*      */           }
/*      */           
/* 1144 */           currentPos++;
/*      */         }
/*      */       }
/*      */       
/* 1148 */       canHandleAsStatement = !foundLimitWithPlaceholder;
/* 1149 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "XA ")) {
/* 1150 */       canHandleAsStatement = false;
/* 1151 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/* 1152 */       canHandleAsStatement = false;
/* 1153 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "DO")) {
/* 1154 */       canHandleAsStatement = false;
/* 1155 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "SET")) {
/* 1156 */       canHandleAsStatement = false;
/* 1157 */     } else if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "SHOW WARNINGS")) && (versionMeetsMinimum(5, 7, 2))) {
/* 1158 */       canHandleAsStatement = false;
/*      */     }
/*      */     
/* 1161 */     return canHandleAsStatement;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/* 1178 */     synchronized (getConnectionMutex()) {
/* 1179 */       checkClosed();
/*      */       
/* 1181 */       if ((userName == null) || (userName.equals(""))) {
/* 1182 */         userName = "";
/*      */       }
/*      */       
/* 1185 */       if (newPassword == null) {
/* 1186 */         newPassword = "";
/*      */       }
/*      */       
/*      */ 
/* 1190 */       this.sessionMaxRows = -1;
/*      */       try
/*      */       {
/* 1193 */         this.io.changeUser(userName, newPassword, this.database);
/*      */       } catch (SQLException ex) {
/* 1195 */         if ((versionMeetsMinimum(5, 6, 13)) && ("28000".equals(ex.getSQLState()))) {
/* 1196 */           cleanup(ex);
/*      */         }
/* 1198 */         throw ex;
/*      */       }
/* 1200 */       this.user = userName;
/* 1201 */       this.password = newPassword;
/*      */       
/* 1203 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 1204 */         configureClientCharacterSet(true);
/*      */       }
/*      */       
/* 1207 */       setSessionVariables();
/*      */       
/* 1209 */       setupServerForTruncationChecks();
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean characterSetNamesMatches(String mysqlEncodingName)
/*      */   {
/* 1215 */     return (mysqlEncodingName != null) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_client"))) && (mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_connection")));
/*      */   }
/*      */   
/*      */   private void checkAndCreatePerformanceHistogram()
/*      */   {
/* 1220 */     if (this.perfMetricsHistCounts == null) {
/* 1221 */       this.perfMetricsHistCounts = new int[20];
/*      */     }
/*      */     
/* 1224 */     if (this.perfMetricsHistBreakpoints == null) {
/* 1225 */       this.perfMetricsHistBreakpoints = new long[20];
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkAndCreateTablesAccessedHistogram() {
/* 1230 */     if (this.numTablesMetricsHistCounts == null) {
/* 1231 */       this.numTablesMetricsHistCounts = new int[20];
/*      */     }
/*      */     
/* 1234 */     if (this.numTablesMetricsHistBreakpoints == null) {
/* 1235 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*      */     }
/*      */   }
/*      */   
/*      */   public void checkClosed() throws SQLException {
/* 1240 */     if (this.isClosed) {
/* 1241 */       throwConnectionClosedException();
/*      */     }
/*      */   }
/*      */   
/*      */   public void throwConnectionClosedException() throws SQLException {
/* 1246 */     SQLException ex = SQLError.createSQLException("No operations allowed after connection closed.", "08003", getExceptionInterceptor());
/*      */     
/*      */ 
/* 1249 */     if (this.forceClosedReason != null) {
/* 1250 */       ex.initCause(this.forceClosedReason);
/*      */     }
/*      */     
/* 1253 */     throw ex;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkServerEncoding()
/*      */     throws SQLException
/*      */   {
/* 1263 */     if ((getUseUnicode()) && (getEncoding() != null))
/*      */     {
/* 1265 */       return;
/*      */     }
/*      */     
/* 1268 */     String serverCharset = (String)this.serverVariables.get("character_set");
/*      */     
/* 1270 */     if (serverCharset == null)
/*      */     {
/* 1272 */       serverCharset = (String)this.serverVariables.get("character_set_server");
/*      */     }
/*      */     
/* 1275 */     String mappedServerEncoding = null;
/*      */     
/* 1277 */     if (serverCharset != null) {
/*      */       try {
/* 1279 */         mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlCharset(serverCharset);
/*      */       } catch (RuntimeException ex) {
/* 1281 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1282 */         sqlEx.initCause(ex);
/* 1283 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1290 */     if ((!getUseUnicode()) && (mappedServerEncoding != null)) {
/* 1291 */       SingleByteCharsetConverter converter = getCharsetConverter(mappedServerEncoding);
/*      */       
/* 1293 */       if (converter != null) {
/* 1294 */         setUseUnicode(true);
/* 1295 */         setEncoding(mappedServerEncoding);
/*      */         
/* 1297 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1304 */     if (serverCharset != null) {
/* 1305 */       if (mappedServerEncoding == null)
/*      */       {
/* 1307 */         if (Character.isLowerCase(serverCharset.charAt(0))) {
/* 1308 */           char[] ach = serverCharset.toCharArray();
/* 1309 */           ach[0] = Character.toUpperCase(serverCharset.charAt(0));
/* 1310 */           setEncoding(new String(ach));
/*      */         }
/*      */       }
/*      */       
/* 1314 */       if (mappedServerEncoding == null) {
/* 1315 */         throw SQLError.createSQLException("Unknown character encoding on server '" + serverCharset + "', use 'characterEncoding=' property " + " to provide correct mapping", "01S00", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1324 */         StringUtils.getBytes("abc", mappedServerEncoding);
/* 1325 */         setEncoding(mappedServerEncoding);
/* 1326 */         setUseUnicode(true);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 1328 */         throw SQLError.createSQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" to your JDBC URL.", "0S100", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkTransactionIsolationLevel()
/*      */     throws SQLException
/*      */   {
/* 1342 */     String txIsolationName = null;
/*      */     
/* 1344 */     if (versionMeetsMinimum(4, 0, 3)) {
/* 1345 */       txIsolationName = "tx_isolation";
/*      */     } else {
/* 1347 */       txIsolationName = "transaction_isolation";
/*      */     }
/*      */     
/* 1350 */     String s = (String)this.serverVariables.get(txIsolationName);
/*      */     
/* 1352 */     if (s != null) {
/* 1353 */       Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */       
/* 1355 */       if (intTI != null) {
/* 1356 */         this.isolationLevel = intTI.intValue();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void abortInternal()
/*      */     throws SQLException
/*      */   {
/* 1368 */     if (this.io != null)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1374 */         this.io.forceClose();
/* 1375 */         this.io.releaseResources();
/*      */       }
/*      */       catch (Throwable t) {}
/*      */       
/* 1379 */       this.io = null;
/*      */     }
/*      */     
/* 1382 */     this.isClosed = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void cleanup(Throwable whyCleanedUp)
/*      */   {
/*      */     try
/*      */     {
/* 1393 */       if (this.io != null) {
/* 1394 */         if (isClosed()) {
/* 1395 */           this.io.forceClose();
/*      */         } else {
/* 1397 */           realClose(false, false, false, whyCleanedUp);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx) {}
/*      */     
/*      */ 
/* 1404 */     this.isClosed = true;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void clearHasTriedMaster() {
/* 1409 */     this.hasTriedMasterFlag = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 1428 */     return clientPrepareStatement(sql, 1003, 1007);
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 1435 */     java.sql.PreparedStatement pStmt = clientPrepareStatement(sql);
/*      */     
/* 1437 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */     
/* 1439 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 1449 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */   
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException
/*      */   {
/* 1454 */     checkClosed();
/*      */     
/* 1456 */     String nativeSql = (processEscapeCodesIfNeeded) && (getProcessEscapeCodesForPrepStmts()) ? nativeSQL(sql) : sql;
/*      */     
/* 1458 */     PreparedStatement pStmt = null;
/*      */     
/* 1460 */     if (getCachePreparedStatements()) {
/* 1461 */       PreparedStatement.ParseInfo pStmtInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(nativeSql);
/*      */       
/* 1463 */       if (pStmtInfo == null) {
/* 1464 */         pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
/*      */         
/* 1466 */         this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
/*      */       } else {
/* 1468 */         pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, pStmtInfo);
/*      */       }
/*      */     } else {
/* 1471 */       pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
/*      */     }
/*      */     
/* 1474 */     pStmt.setResultSetType(resultSetType);
/* 1475 */     pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */     
/* 1477 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 1485 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */     
/* 1487 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */     
/* 1489 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 1496 */     PreparedStatement pStmt = (PreparedStatement)clientPrepareStatement(sql);
/*      */     
/* 1498 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */     
/* 1500 */     return pStmt;
/*      */   }
/*      */   
/*      */   public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
/*      */   {
/* 1505 */     return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/* 1521 */     synchronized (getConnectionMutex()) {
/* 1522 */       if (this.connectionLifecycleInterceptors != null) {
/* 1523 */         new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException {
/* 1526 */             ((ConnectionLifecycleInterceptor)each).close();
/*      */           }
/*      */         }.doForAll();
/*      */       }
/*      */       
/* 1531 */       realClose(true, true, false, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void closeAllOpenStatements()
/*      */     throws SQLException
/*      */   {
/* 1541 */     SQLException postponedException = null;
/*      */     
/* 1543 */     for (Statement stmt : this.openStatements) {
/*      */       try {
/* 1545 */         ((StatementImpl)stmt).realClose(false, true);
/*      */       } catch (SQLException sqlEx) {
/* 1547 */         postponedException = sqlEx;
/*      */       }
/*      */     }
/*      */     
/* 1551 */     if (postponedException != null) {
/* 1552 */       throw postponedException;
/*      */     }
/*      */   }
/*      */   
/*      */   private void closeStatement(java.sql.Statement stmt) {
/* 1557 */     if (stmt != null) {
/*      */       try {
/* 1559 */         stmt.close();
/*      */       }
/*      */       catch (SQLException sqlEx) {}
/*      */       
/*      */ 
/* 1564 */       stmt = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/* 1582 */     synchronized (getConnectionMutex()) {
/* 1583 */       checkClosed();
/*      */       try
/*      */       {
/* 1586 */         if (this.connectionLifecycleInterceptors != null) {
/* 1587 */           IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */           {
/*      */             void forEach(Extension each) throws SQLException
/*      */             {
/* 1591 */               if (!((ConnectionLifecycleInterceptor)each).commit()) {
/* 1592 */                 this.stopIterating = true;
/*      */               }
/*      */               
/*      */             }
/* 1596 */           };
/* 1597 */           iter.doForAll();
/*      */           
/* 1599 */           if (!iter.fullIteration()) {
/* 1600 */             jsr 136;return;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1605 */         if ((this.autoCommit) && (!getRelaxAutoCommit()))
/* 1606 */           throw SQLError.createSQLException("Can't call commit when autocommit=true", getExceptionInterceptor());
/* 1607 */         if (this.transactionsSupported) {
/* 1608 */           if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 1609 */             (!this.io.inTransactionOnServer())) {
/* 1610 */             jsr 71;return;
/*      */           }
/*      */           
/*      */ 
/* 1614 */           execSQL(null, "commit", -1, null, 1003, 1007, false, this.database, null, false);
/*      */         }
/*      */       } catch (SQLException sqlException) {
/* 1617 */         if ("08S01".equals(sqlException.getSQLState())) {
/* 1618 */           throw SQLError.createSQLException("Communications link failure during commit(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/* 1622 */         throw sqlException;
/*      */       } finally {
/* 1624 */         jsr 5; } localObject2 = returnAddress;this.needsPing = getReconnectAtTxEnd();ret;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void configureCharsetProperties()
/*      */     throws SQLException
/*      */   {
/* 1637 */     if (getEncoding() != null) {
/*      */       try
/*      */       {
/* 1640 */         String testString = "abc";
/* 1641 */         StringUtils.getBytes(testString, getEncoding());
/*      */       }
/*      */       catch (UnsupportedEncodingException UE) {
/* 1644 */         String oldEncoding = getEncoding();
/*      */         try
/*      */         {
/* 1647 */           setEncoding(CharsetMapping.getJavaEncodingForMysqlCharset(oldEncoding));
/*      */         } catch (RuntimeException ex) {
/* 1649 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1650 */           sqlEx.initCause(ex);
/* 1651 */           throw sqlEx;
/*      */         }
/*      */         
/* 1654 */         if (getEncoding() == null) {
/* 1655 */           throw SQLError.createSQLException("Java does not support the MySQL character encoding '" + oldEncoding + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */         try
/*      */         {
/* 1660 */           String testString = "abc";
/* 1661 */           StringUtils.getBytes(testString, getEncoding());
/*      */         } catch (UnsupportedEncodingException encodingEx) {
/* 1663 */           throw SQLError.createSQLException("Unsupported character encoding '" + getEncoding() + "'.", "01S00", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean configureClientCharacterSet(boolean dontCheckServerMatch)
/*      */     throws SQLException
/*      */   {
/* 1684 */     String realJavaEncoding = getEncoding();
/* 1685 */     boolean characterSetAlreadyConfigured = false;
/*      */     try
/*      */     {
/* 1688 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 1689 */         characterSetAlreadyConfigured = true;
/*      */         
/* 1691 */         setUseUnicode(true);
/*      */         
/* 1693 */         configureCharsetProperties();
/* 1694 */         realJavaEncoding = getEncoding();
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 1700 */           if ((this.props != null) && (this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex") != null)) {
/* 1701 */             this.io.serverCharsetIndex = Integer.parseInt(this.props.getProperty("com.mysql.jdbc.faultInjection.serverCharsetIndex"));
/*      */           }
/*      */           
/* 1704 */           String serverEncodingToSet = CharsetMapping.getJavaEncodingForCollationIndex(Integer.valueOf(this.io.serverCharsetIndex));
/*      */           
/* 1706 */           if ((serverEncodingToSet == null) || (serverEncodingToSet.length() == 0)) {
/* 1707 */             if (realJavaEncoding != null)
/*      */             {
/* 1709 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1711 */               throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1719 */           if ((versionMeetsMinimum(4, 1, 0)) && ("ISO8859_1".equalsIgnoreCase(serverEncodingToSet))) {
/* 1720 */             serverEncodingToSet = "Cp1252";
/*      */           }
/* 1722 */           if (("UnicodeBig".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-16".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-16LE".equalsIgnoreCase(serverEncodingToSet)) || ("UTF-32".equalsIgnoreCase(serverEncodingToSet)))
/*      */           {
/* 1724 */             serverEncodingToSet = "UTF-8";
/*      */           }
/*      */           
/* 1727 */           setEncoding(serverEncodingToSet);
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 1730 */           if (realJavaEncoding != null)
/*      */           {
/* 1732 */             setEncoding(realJavaEncoding);
/*      */           } else {
/* 1734 */             throw SQLError.createSQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/* 1740 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 1742 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 1743 */           sqlEx.initCause(ex);
/* 1744 */           throw sqlEx;
/*      */         }
/*      */         
/* 1747 */         if (getEncoding() == null)
/*      */         {
/* 1749 */           setEncoding("ISO8859_1");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1755 */         if (getUseUnicode()) {
/* 1756 */           if (realJavaEncoding != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1761 */             if ((realJavaEncoding.equalsIgnoreCase("UTF-8")) || (realJavaEncoding.equalsIgnoreCase("UTF8")))
/*      */             {
/*      */ 
/* 1764 */               boolean utf8mb4Supported = versionMeetsMinimum(5, 5, 2);
/* 1765 */               boolean useutf8mb4 = (utf8mb4Supported) && (CharsetMapping.UTF8MB4_INDEXES.contains(Integer.valueOf(this.io.serverCharsetIndex)));
/*      */               
/* 1767 */               if (!getUseOldUTF8Behavior()) {
/* 1768 */                 if ((dontCheckServerMatch) || (!characterSetNamesMatches("utf8")) || ((utf8mb4Supported) && (!characterSetNamesMatches("utf8mb4")))) {
/* 1769 */                   execSQL(null, "SET NAMES " + (useutf8mb4 ? "utf8mb4" : "utf8"), -1, null, 1003, 1007, false, this.database, null, false);
/*      */                   
/* 1771 */                   this.serverVariables.put("character_set_client", useutf8mb4 ? "utf8mb4" : "utf8");
/* 1772 */                   this.serverVariables.put("character_set_connection", useutf8mb4 ? "utf8mb4" : "utf8");
/*      */                 }
/*      */               } else {
/* 1775 */                 execSQL(null, "SET NAMES latin1", -1, null, 1003, 1007, false, this.database, null, false);
/*      */                 
/* 1777 */                 this.serverVariables.put("character_set_client", "latin1");
/* 1778 */                 this.serverVariables.put("character_set_connection", "latin1");
/*      */               }
/*      */               
/* 1781 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 1783 */               String mysqlCharsetName = CharsetMapping.getMysqlCharsetForJavaEncoding(realJavaEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1794 */               if (mysqlCharsetName != null)
/*      */               {
/* 1796 */                 if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlCharsetName))) {
/* 1797 */                   execSQL(null, "SET NAMES " + mysqlCharsetName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */                   
/* 1799 */                   this.serverVariables.put("character_set_client", mysqlCharsetName);
/* 1800 */                   this.serverVariables.put("character_set_connection", mysqlCharsetName);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1806 */               setEncoding(realJavaEncoding);
/*      */             }
/* 1808 */           } else if (getEncoding() != null)
/*      */           {
/* 1810 */             String mysqlCharsetName = getServerCharset();
/*      */             
/* 1812 */             if (getUseOldUTF8Behavior()) {
/* 1813 */               mysqlCharsetName = "latin1";
/*      */             }
/*      */             
/* 1816 */             boolean ucs2 = false;
/* 1817 */             if (("ucs2".equalsIgnoreCase(mysqlCharsetName)) || ("utf16".equalsIgnoreCase(mysqlCharsetName)) || ("utf16le".equalsIgnoreCase(mysqlCharsetName)) || ("utf32".equalsIgnoreCase(mysqlCharsetName)))
/*      */             {
/* 1819 */               mysqlCharsetName = "utf8";
/* 1820 */               ucs2 = true;
/* 1821 */               if (getCharacterSetResults() == null) {
/* 1822 */                 setCharacterSetResults("UTF-8");
/*      */               }
/*      */             }
/*      */             
/* 1826 */             if ((dontCheckServerMatch) || (!characterSetNamesMatches(mysqlCharsetName)) || (ucs2)) {
/*      */               try {
/* 1828 */                 execSQL(null, "SET NAMES " + mysqlCharsetName, -1, null, 1003, 1007, false, this.database, null, false);
/*      */                 
/* 1830 */                 this.serverVariables.put("character_set_client", mysqlCharsetName);
/* 1831 */                 this.serverVariables.put("character_set_connection", mysqlCharsetName);
/*      */               } catch (SQLException ex) {
/* 1833 */                 if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1834 */                   throw ex;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1839 */             realJavaEncoding = getEncoding();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1849 */         String onServer = null;
/* 1850 */         boolean isNullOnServer = false;
/*      */         
/* 1852 */         if (this.serverVariables != null) {
/* 1853 */           onServer = (String)this.serverVariables.get("character_set_results");
/*      */           
/* 1855 */           isNullOnServer = (onServer == null) || ("NULL".equalsIgnoreCase(onServer)) || (onServer.length() == 0);
/*      */         }
/*      */         
/* 1858 */         if (getCharacterSetResults() == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1863 */           if (!isNullOnServer) {
/*      */             try {
/* 1865 */               execSQL(null, "SET character_set_results = NULL", -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */             catch (SQLException ex) {
/* 1868 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1869 */                 throw ex;
/*      */               }
/*      */             }
/* 1872 */             this.serverVariables.put("jdbc.local.character_set_results", null);
/*      */           } else {
/* 1874 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */         }
/*      */         else {
/* 1878 */           if (getUseOldUTF8Behavior()) {
/*      */             try {
/* 1880 */               execSQL(null, "SET NAMES latin1", -1, null, 1003, 1007, false, this.database, null, false);
/*      */               
/* 1882 */               this.serverVariables.put("character_set_client", "latin1");
/* 1883 */               this.serverVariables.put("character_set_connection", "latin1");
/*      */             } catch (SQLException ex) {
/* 1885 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1886 */                 throw ex;
/*      */               }
/*      */             }
/*      */           }
/* 1890 */           String charsetResults = getCharacterSetResults();
/* 1891 */           String mysqlEncodingName = null;
/*      */           
/* 1893 */           if (("UTF-8".equalsIgnoreCase(charsetResults)) || ("UTF8".equalsIgnoreCase(charsetResults))) {
/* 1894 */             mysqlEncodingName = "utf8";
/* 1895 */           } else if ("null".equalsIgnoreCase(charsetResults)) {
/* 1896 */             mysqlEncodingName = "NULL";
/*      */           } else {
/* 1898 */             mysqlEncodingName = CharsetMapping.getMysqlCharsetForJavaEncoding(charsetResults.toUpperCase(Locale.ENGLISH), this);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1905 */           if (mysqlEncodingName == null) {
/* 1906 */             throw SQLError.createSQLException("Can't map " + charsetResults + " given for characterSetResults to a supported MySQL encoding.", "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 1910 */           if (!mysqlEncodingName.equalsIgnoreCase((String)this.serverVariables.get("character_set_results"))) {
/* 1911 */             StringBuilder setBuf = new StringBuilder("SET character_set_results = ".length() + mysqlEncodingName.length());
/* 1912 */             setBuf.append("SET character_set_results = ").append(mysqlEncodingName);
/*      */             try
/*      */             {
/* 1915 */               execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */             }
/*      */             catch (SQLException ex) {
/* 1918 */               if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1919 */                 throw ex;
/*      */               }
/*      */             }
/*      */             
/* 1923 */             this.serverVariables.put("jdbc.local.character_set_results", mysqlEncodingName);
/*      */             
/*      */ 
/* 1926 */             if (versionMeetsMinimum(5, 5, 0)) {
/* 1927 */               this.errorMessageEncoding = charsetResults;
/*      */             }
/*      */           }
/*      */           else {
/* 1931 */             this.serverVariables.put("jdbc.local.character_set_results", onServer);
/*      */           }
/*      */         }
/*      */         
/* 1935 */         if (getConnectionCollation() != null) {
/* 1936 */           StringBuilder setBuf = new StringBuilder("SET collation_connection = ".length() + getConnectionCollation().length());
/* 1937 */           setBuf.append("SET collation_connection = ").append(getConnectionCollation());
/*      */           try
/*      */           {
/* 1940 */             execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */           } catch (SQLException ex) {
/* 1942 */             if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 1943 */               throw ex;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1949 */         realJavaEncoding = getEncoding();
/*      */       }
/*      */       
/*      */     }
/*      */     finally
/*      */     {
/* 1955 */       setEncoding(realJavaEncoding);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1963 */       CharsetEncoder enc = Charset.forName(getEncoding()).newEncoder();
/* 1964 */       CharBuffer cbuf = CharBuffer.allocate(1);
/* 1965 */       ByteBuffer bbuf = ByteBuffer.allocate(1);
/*      */       
/* 1967 */       cbuf.put("¥");
/* 1968 */       cbuf.position(0);
/* 1969 */       enc.encode(cbuf, bbuf, true);
/* 1970 */       if (bbuf.get(0) == 92) {
/* 1971 */         this.requiresEscapingEncoder = true;
/*      */       } else {
/* 1973 */         cbuf.clear();
/* 1974 */         bbuf.clear();
/*      */         
/* 1976 */         cbuf.put("₩");
/* 1977 */         cbuf.position(0);
/* 1978 */         enc.encode(cbuf, bbuf, true);
/* 1979 */         if (bbuf.get(0) == 92) {
/* 1980 */           this.requiresEscapingEncoder = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (UnsupportedCharsetException ucex) {
/*      */       try {
/* 1986 */         byte[] bbuf = StringUtils.getBytes("¥", getEncoding());
/* 1987 */         if (bbuf[0] == 92) {
/* 1988 */           this.requiresEscapingEncoder = true;
/*      */         } else {
/* 1990 */           bbuf = StringUtils.getBytes("₩", getEncoding());
/* 1991 */           if (bbuf[0] == 92) {
/* 1992 */             this.requiresEscapingEncoder = true;
/*      */           }
/*      */         }
/*      */       } catch (UnsupportedEncodingException ueex) {
/* 1996 */         throw SQLError.createSQLException("Unable to use encoding: " + getEncoding(), "S1000", ueex, getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2001 */     return characterSetAlreadyConfigured;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void configureTimezone()
/*      */     throws SQLException
/*      */   {
/* 2012 */     String configuredTimeZoneOnServer = (String)this.serverVariables.get("timezone");
/*      */     
/* 2014 */     if (configuredTimeZoneOnServer == null) {
/* 2015 */       configuredTimeZoneOnServer = (String)this.serverVariables.get("time_zone");
/*      */       
/* 2017 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 2018 */         configuredTimeZoneOnServer = (String)this.serverVariables.get("system_time_zone");
/*      */       }
/*      */     }
/*      */     
/* 2022 */     String canonicalTimezone = getServerTimezone();
/*      */     
/* 2024 */     if (((getUseTimezone()) || (!getUseLegacyDatetimeCode())) && (configuredTimeZoneOnServer != null))
/*      */     {
/* 2026 */       if ((canonicalTimezone == null) || (StringUtils.isEmptyOrWhitespaceOnly(canonicalTimezone))) {
/*      */         try {
/* 2028 */           canonicalTimezone = TimeUtil.getCanonicalTimezone(configuredTimeZoneOnServer, getExceptionInterceptor());
/*      */         } catch (IllegalArgumentException iae) {
/* 2030 */           throw SQLError.createSQLException(iae.getMessage(), "S1000", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2035 */     if ((canonicalTimezone != null) && (canonicalTimezone.length() > 0)) {
/* 2036 */       this.serverTimezoneTZ = TimeZone.getTimeZone(canonicalTimezone);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2041 */       if ((!canonicalTimezone.equalsIgnoreCase("GMT")) && (this.serverTimezoneTZ.getID().equals("GMT"))) {
/* 2042 */         throw SQLError.createSQLException("No timezone mapping entry for '" + canonicalTimezone + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2046 */       this.isServerTzUTC = ((!this.serverTimezoneTZ.useDaylightTime()) && (this.serverTimezoneTZ.getRawOffset() == 0));
/*      */     }
/*      */   }
/*      */   
/*      */   private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound)
/*      */   {
/* 2052 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*      */     
/* 2054 */     if (bucketSize < 1.0D) {
/* 2055 */       bucketSize = 1.0D;
/*      */     }
/*      */     
/* 2058 */     for (int i = 0; i < 20; i++) {
/* 2059 */       breakpoints[i] = lowerBound;
/* 2060 */       lowerBound = (lowerBound + bucketSize);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createNewIO(boolean isForReconnect)
/*      */     throws SQLException
/*      */   {
/* 2075 */     synchronized (getConnectionMutex())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2080 */       Properties mergedProps = exposeAsProperties(this.props);
/*      */       
/* 2082 */       if (!getHighAvailability()) {
/* 2083 */         connectOneTryOnly(isForReconnect, mergedProps);
/*      */         
/* 2085 */         return;
/*      */       }
/*      */       
/* 2088 */       connectWithRetries(isForReconnect, mergedProps);
/*      */     }
/*      */   }
/*      */   
/*      */   private void connectWithRetries(boolean isForReconnect, Properties mergedProps) throws SQLException {
/* 2093 */     double timeout = getInitialTimeout();
/* 2094 */     boolean connectionGood = false;
/*      */     
/* 2096 */     Exception connectionException = null;
/*      */     
/* 2098 */     for (int attemptCount = 0; (attemptCount < getMaxReconnects()) && (!connectionGood); attemptCount++) {
/*      */       try {
/* 2100 */         if (this.io != null) {
/* 2101 */           this.io.forceClose();
/*      */         }
/*      */         
/* 2104 */         coreConnect(mergedProps);
/* 2105 */         pingInternal(false, 0);
/*      */         
/*      */         boolean oldAutoCommit;
/*      */         
/*      */         int oldIsolationLevel;
/*      */         boolean oldReadOnly;
/*      */         String oldCatalog;
/* 2112 */         synchronized (getConnectionMutex()) {
/* 2113 */           this.connectionId = this.io.getThreadId();
/* 2114 */           this.isClosed = false;
/*      */           
/*      */ 
/* 2117 */           oldAutoCommit = getAutoCommit();
/* 2118 */           oldIsolationLevel = this.isolationLevel;
/* 2119 */           oldReadOnly = isReadOnly(false);
/* 2120 */           oldCatalog = getCatalog();
/*      */           
/* 2122 */           this.io.setStatementInterceptors(this.statementInterceptors);
/*      */         }
/*      */         
/*      */ 
/* 2126 */         initializePropsFromServer();
/*      */         
/* 2128 */         if (isForReconnect)
/*      */         {
/* 2130 */           setAutoCommit(oldAutoCommit);
/*      */           
/* 2132 */           if (this.hasIsolationLevels) {
/* 2133 */             setTransactionIsolation(oldIsolationLevel);
/*      */           }
/*      */           
/* 2136 */           setCatalog(oldCatalog);
/* 2137 */           setReadOnly(oldReadOnly);
/*      */         }
/*      */         
/* 2140 */         connectionGood = true;
/*      */       }
/*      */       catch (Exception EEE)
/*      */       {
/* 2144 */         connectionException = EEE;
/* 2145 */         connectionGood = false;
/*      */         
/*      */ 
/* 2148 */         if (!connectionGood) break label190; }
/* 2149 */       break;
/*      */       
/*      */       label190:
/* 2152 */       if (attemptCount > 0) {
/*      */         try {
/* 2154 */           Thread.sleep(timeout * 1000L);
/*      */         }
/*      */         catch (InterruptedException IE) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2161 */     if (!connectionGood)
/*      */     {
/* 2163 */       SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[] { Integer.valueOf(getMaxReconnects()) }), "08001", getExceptionInterceptor());
/*      */       
/*      */ 
/* 2166 */       chainedEx.initCause(connectionException);
/*      */       
/* 2168 */       throw chainedEx;
/*      */     }
/*      */     
/* 2171 */     if ((getParanoid()) && (!getHighAvailability())) {
/* 2172 */       this.password = null;
/* 2173 */       this.user = null;
/*      */     }
/*      */     
/* 2176 */     if (isForReconnect)
/*      */     {
/*      */ 
/*      */ 
/* 2180 */       Iterator<Statement> statementIter = this.openStatements.iterator();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2186 */       Stack<Statement> serverPreparedStatements = null;
/*      */       
/* 2188 */       while (statementIter.hasNext()) {
/* 2189 */         Statement statementObj = (Statement)statementIter.next();
/*      */         
/* 2191 */         if ((statementObj instanceof ServerPreparedStatement)) {
/* 2192 */           if (serverPreparedStatements == null) {
/* 2193 */             serverPreparedStatements = new Stack();
/*      */           }
/*      */           
/* 2196 */           serverPreparedStatements.add(statementObj);
/*      */         }
/*      */       }
/*      */       
/* 2200 */       if (serverPreparedStatements != null) {
/* 2201 */         while (!serverPreparedStatements.isEmpty()) {
/* 2202 */           ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void coreConnect(Properties mergedProps) throws SQLException, IOException {
/* 2209 */     int newPort = 3306;
/* 2210 */     String newHost = "localhost";
/*      */     
/* 2212 */     String protocol = mergedProps.getProperty("PROTOCOL");
/*      */     
/* 2214 */     if (protocol != null)
/*      */     {
/*      */ 
/* 2217 */       if ("tcp".equalsIgnoreCase(protocol)) {
/* 2218 */         newHost = normalizeHost(mergedProps.getProperty("HOST"));
/* 2219 */         newPort = parsePortNumber(mergedProps.getProperty("PORT", "3306"));
/* 2220 */       } else if ("pipe".equalsIgnoreCase(protocol)) {
/* 2221 */         setSocketFactoryClassName(NamedPipeSocketFactory.class.getName());
/*      */         
/* 2223 */         String path = mergedProps.getProperty("PATH");
/*      */         
/* 2225 */         if (path != null) {
/* 2226 */           mergedProps.setProperty("namedPipePath", path);
/*      */         }
/*      */       }
/*      */       else {
/* 2230 */         newHost = normalizeHost(mergedProps.getProperty("HOST"));
/* 2231 */         newPort = parsePortNumber(mergedProps.getProperty("PORT", "3306"));
/*      */       }
/*      */     }
/*      */     else {
/* 2235 */       String[] parsedHostPortPair = NonRegisteringDriver.parseHostPortPair(this.hostPortPair);
/* 2236 */       newHost = parsedHostPortPair[0];
/*      */       
/* 2238 */       newHost = normalizeHost(newHost);
/*      */       
/* 2240 */       if (parsedHostPortPair[1] != null) {
/* 2241 */         newPort = parsePortNumber(parsedHostPortPair[1]);
/*      */       }
/*      */     }
/*      */     
/* 2245 */     this.port = newPort;
/* 2246 */     this.host = newHost;
/*      */     
/*      */ 
/* 2249 */     this.sessionMaxRows = -1;
/*      */     
/* 2251 */     this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), getProxy(), getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
/*      */     
/* 2253 */     this.io.doHandshake(this.user, this.password, this.database);
/* 2254 */     if (versionMeetsMinimum(5, 5, 0))
/*      */     {
/* 2256 */       this.errorMessageEncoding = this.io.getEncodingForHandshake();
/*      */     }
/*      */   }
/*      */   
/*      */   private String normalizeHost(String hostname) {
/* 2261 */     if ((hostname == null) || (StringUtils.isEmptyOrWhitespaceOnly(hostname))) {
/* 2262 */       return "localhost";
/*      */     }
/*      */     
/* 2265 */     return hostname;
/*      */   }
/*      */   
/*      */   private int parsePortNumber(String portAsString) throws SQLException {
/* 2269 */     int portNumber = 3306;
/*      */     try {
/* 2271 */       portNumber = Integer.parseInt(portAsString);
/*      */     } catch (NumberFormatException nfe) {
/* 2273 */       throw SQLError.createSQLException("Illegal connection port value '" + portAsString + "'", "01S00", getExceptionInterceptor());
/*      */     }
/*      */     
/* 2276 */     return portNumber;
/*      */   }
/*      */   
/*      */   private void connectOneTryOnly(boolean isForReconnect, Properties mergedProps) throws SQLException {
/* 2280 */     Exception connectionNotEstablishedBecause = null;
/*      */     
/*      */     try
/*      */     {
/* 2284 */       coreConnect(mergedProps);
/* 2285 */       this.connectionId = this.io.getThreadId();
/* 2286 */       this.isClosed = false;
/*      */       
/*      */ 
/* 2289 */       boolean oldAutoCommit = getAutoCommit();
/* 2290 */       int oldIsolationLevel = this.isolationLevel;
/* 2291 */       boolean oldReadOnly = isReadOnly(false);
/* 2292 */       String oldCatalog = getCatalog();
/*      */       
/* 2294 */       this.io.setStatementInterceptors(this.statementInterceptors);
/*      */       
/*      */ 
/* 2297 */       initializePropsFromServer();
/*      */       
/* 2299 */       if (isForReconnect)
/*      */       {
/* 2301 */         setAutoCommit(oldAutoCommit);
/*      */         
/* 2303 */         if (this.hasIsolationLevels) {
/* 2304 */           setTransactionIsolation(oldIsolationLevel);
/*      */         }
/*      */         
/* 2307 */         setCatalog(oldCatalog);
/*      */         
/* 2309 */         setReadOnly(oldReadOnly);
/*      */       }
/* 2311 */       return;
/*      */     }
/*      */     catch (Exception EEE)
/*      */     {
/* 2315 */       if (((EEE instanceof SQLException)) && (((SQLException)EEE).getErrorCode() == 1820) && (!getDisconnectOnExpiredPasswords()))
/*      */       {
/* 2317 */         return;
/*      */       }
/*      */       
/* 2320 */       if (this.io != null) {
/* 2321 */         this.io.forceClose();
/*      */       }
/*      */       
/* 2324 */       connectionNotEstablishedBecause = EEE;
/*      */       
/* 2326 */       if ((EEE instanceof SQLException)) {
/* 2327 */         throw ((SQLException)EEE);
/*      */       }
/*      */       
/* 2330 */       SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), "08001", getExceptionInterceptor());
/*      */       
/* 2332 */       chainedEx.initCause(connectionNotEstablishedBecause);
/*      */       
/* 2334 */       throw chainedEx;
/*      */     }
/*      */   }
/*      */   
/*      */   private void createPreparedStatementCaches() throws SQLException {
/* 2339 */     synchronized (getConnectionMutex()) {
/* 2340 */       int cacheSize = getPreparedStatementCacheSize();
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 2345 */         Class<?> factoryClass = Class.forName(getParseInfoCacheFactory());
/*      */         
/*      */ 
/* 2348 */         CacheAdapterFactory<String, PreparedStatement.ParseInfo> cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
/*      */         
/* 2350 */         this.cachedPreparedStatementParams = cacheFactory.getInstance(this, this.myURL, getPreparedStatementCacheSize(), getPreparedStatementCacheSqlLimit(), this.props);
/*      */       }
/*      */       catch (ClassNotFoundException e)
/*      */       {
/* 2354 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 2357 */         sqlEx.initCause(e);
/*      */         
/* 2359 */         throw sqlEx;
/*      */       } catch (InstantiationException e) {
/* 2361 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 2364 */         sqlEx.initCause(e);
/*      */         
/* 2366 */         throw sqlEx;
/*      */       } catch (IllegalAccessException e) {
/* 2368 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 2371 */         sqlEx.initCause(e);
/*      */         
/* 2373 */         throw sqlEx;
/*      */       }
/*      */       
/* 2376 */       if (getUseServerPreparedStmts()) {
/* 2377 */         this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */         
/* 2379 */         this.serverSideStatementCache = new LRUCache(cacheSize)
/*      */         {
/*      */           private static final long serialVersionUID = 7692318650375988114L;
/*      */           
/*      */           protected boolean removeEldestEntry(Entry<Object, Object> eldest)
/*      */           {
/* 2385 */             if (this.maxElements <= 1) {
/* 2386 */               return false;
/*      */             }
/*      */             
/* 2389 */             boolean removeIt = super.removeEldestEntry(eldest);
/*      */             
/* 2391 */             if (removeIt) {
/* 2392 */               ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
/* 2393 */               ps.isCached = false;
/* 2394 */               ps.setClosed(false);
/*      */               try
/*      */               {
/* 2397 */                 ps.close();
/*      */               }
/*      */               catch (SQLException sqlEx) {}
/*      */             }
/*      */             
/*      */ 
/* 2403 */             return removeIt;
/*      */           }
/*      */         };
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/* 2420 */     return createStatement(1003, 1007);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2436 */     checkClosed();
/*      */     
/* 2438 */     StatementImpl stmt = new StatementImpl(getMultiHostSafeProxy(), this.database);
/* 2439 */     stmt.setResultSetType(resultSetType);
/* 2440 */     stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */     
/* 2442 */     return stmt;
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2449 */     if ((getPedantic()) && 
/* 2450 */       (resultSetHoldability != 1)) {
/* 2451 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2456 */     return createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */   public void dumpTestcaseQuery(String query) {
/* 2460 */     System.err.println(query);
/*      */   }
/*      */   
/*      */   public Connection duplicate() throws SQLException {
/* 2464 */     return new ConnectionImpl(this.origHostToConnectTo, this.origPortToConnectTo, this.props, this.origDatabaseToConnectTo, this.myURL);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws SQLException
/*      */   {
/* 2503 */     return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, false);
/*      */   }
/*      */   
/*      */   public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata, boolean isBatch) throws SQLException
/*      */   {
/* 2508 */     synchronized (getConnectionMutex())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2513 */       long queryStartTime = 0L;
/*      */       
/* 2515 */       int endOfQueryPacketPosition = 0;
/*      */       
/* 2517 */       if (packet != null) {
/* 2518 */         endOfQueryPacketPosition = packet.getPosition();
/*      */       }
/*      */       
/* 2521 */       if (getGatherPerformanceMetrics()) {
/* 2522 */         queryStartTime = System.currentTimeMillis();
/*      */       }
/*      */       
/* 2525 */       this.lastQueryFinishedTime = 0L;
/*      */       
/* 2527 */       if ((getHighAvailability()) && ((this.autoCommit) || (getAutoReconnectForPools())) && (this.needsPing) && (!isBatch)) {
/*      */         try {
/* 2529 */           pingInternal(false, 0);
/*      */           
/* 2531 */           this.needsPing = false;
/*      */         } catch (Exception Ex) {
/* 2533 */           createNewIO(true);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2538 */         if (packet == null) {
/* 2539 */           encoding = null;
/*      */           
/* 2541 */           if (getUseUnicode()) {
/* 2542 */             encoding = getEncoding();
/*      */           }
/*      */           
/* 2545 */           ResultSetInternalMethods localResultSetInternalMethods = this.io.sqlQueryDirect(callingStatement, sql, encoding, null, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);jsr 236;return localResultSetInternalMethods;
/*      */         }
/*      */         
/*      */ 
/* 2549 */         String encoding = this.io.sqlQueryDirect(callingStatement, null, null, packet, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata);jsr 202;return encoding;
/*      */ 
/*      */       }
/*      */       catch (SQLException sqlE)
/*      */       {
/* 2554 */         if (getDumpQueriesOnException()) {
/* 2555 */           String extractedSql = extractSqlFromPacket(sql, packet, endOfQueryPacketPosition);
/* 2556 */           StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/* 2557 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/* 2558 */           messageBuf.append(extractedSql);
/* 2559 */           messageBuf.append("\n\n");
/*      */           
/* 2561 */           sqlE = appendMessageToException(sqlE, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */         
/* 2564 */         if (getHighAvailability()) {
/* 2565 */           this.needsPing = true;
/*      */         } else {
/* 2567 */           String sqlState = sqlE.getSQLState();
/*      */           
/* 2569 */           if ((sqlState != null) && (sqlState.equals("08S01"))) {
/* 2570 */             cleanup(sqlE);
/*      */           }
/*      */         }
/*      */         
/* 2574 */         throw sqlE;
/*      */       } catch (Exception ex) {
/* 2576 */         if (getHighAvailability()) {
/* 2577 */           this.needsPing = true;
/* 2578 */         } else if ((ex instanceof IOException)) {
/* 2579 */           cleanup(ex);
/*      */         }
/*      */         
/* 2582 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnexpectedException"), "S1000", getExceptionInterceptor());
/*      */         
/* 2584 */         sqlEx.initCause(ex);
/*      */         
/* 2586 */         throw sqlEx;
/*      */       } finally {
/* 2588 */         jsr 6; } localObject2 = returnAddress; if (getMaintainTimeStats()) {
/* 2589 */         this.lastQueryFinishedTime = System.currentTimeMillis();
/*      */       }
/*      */       
/* 2592 */       if (getGatherPerformanceMetrics()) {
/* 2593 */         long queryTime = System.currentTimeMillis() - queryStartTime;
/*      */         
/* 2595 */         registerQueryExecutionTime(queryTime); }
/* 2596 */       ret;
/*      */     }
/*      */   }
/*      */   
/*      */   public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition)
/*      */     throws SQLException
/*      */   {
/* 2603 */     String extractedSql = null;
/*      */     
/* 2605 */     if (possibleSqlQuery != null) {
/* 2606 */       if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
/* 2607 */         StringBuilder truncatedQueryBuf = new StringBuilder(possibleSqlQuery.substring(0, getMaxQuerySizeToLog()));
/* 2608 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 2609 */         extractedSql = truncatedQueryBuf.toString();
/*      */       } else {
/* 2611 */         extractedSql = possibleSqlQuery;
/*      */       }
/*      */     }
/*      */     
/* 2615 */     if (extractedSql == null)
/*      */     {
/*      */ 
/* 2618 */       int extractPosition = endOfQueryPacketPosition;
/*      */       
/* 2620 */       boolean truncated = false;
/*      */       
/* 2622 */       if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
/* 2623 */         extractPosition = getMaxQuerySizeToLog();
/* 2624 */         truncated = true;
/*      */       }
/*      */       
/* 2627 */       extractedSql = StringUtils.toString(queryPacket.getByteBuffer(), 5, extractPosition - 5);
/*      */       
/* 2629 */       if (truncated) {
/* 2630 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*      */       }
/*      */     }
/*      */     
/* 2634 */     return extractedSql;
/*      */   }
/*      */   
/*      */   public StringBuilder generateConnectionCommentBlock(StringBuilder buf)
/*      */   {
/* 2639 */     buf.append("/* conn id ");
/* 2640 */     buf.append(getId());
/* 2641 */     buf.append(" clock: ");
/* 2642 */     buf.append(System.currentTimeMillis());
/* 2643 */     buf.append(" */ ");
/*      */     
/* 2645 */     return buf;
/*      */   }
/*      */   
/*      */   public int getActiveStatementCount() {
/* 2649 */     return this.openStatements.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 2671 */     if (getDynamicCalendars()) {
/* 2672 */       return Calendar.getInstance();
/*      */     }
/*      */     
/* 2675 */     return getSessionLockedCalendar();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName)
/*      */     throws SQLException
/*      */   {
/* 2713 */     if (javaEncodingName == null) {
/* 2714 */       return null;
/*      */     }
/*      */     
/* 2717 */     if (this.usePlatformCharsetConverters) {
/* 2718 */       return null;
/*      */     }
/*      */     
/* 2721 */     SingleByteCharsetConverter converter = null;
/*      */     
/* 2723 */     synchronized (this.charsetConverterMap) {
/* 2724 */       Object asObject = this.charsetConverterMap.get(javaEncodingName);
/*      */       
/* 2726 */       if (asObject == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
/* 2727 */         return null;
/*      */       }
/*      */       
/* 2730 */       converter = (SingleByteCharsetConverter)asObject;
/*      */       
/* 2732 */       if (converter == null) {
/*      */         try {
/* 2734 */           converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
/*      */           
/* 2736 */           if (converter == null) {
/* 2737 */             this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */           } else {
/* 2739 */             this.charsetConverterMap.put(javaEncodingName, converter);
/*      */           }
/*      */         } catch (UnsupportedEncodingException unsupEncEx) {
/* 2742 */           this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */           
/* 2744 */           converter = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2749 */     return converter;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public String getCharsetNameForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 2757 */     return getEncodingForIndex(charsetIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getEncodingForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 2771 */     String javaEncoding = null;
/*      */     
/* 2773 */     if (getUseOldUTF8Behavior()) {
/* 2774 */       return getEncoding();
/*      */     }
/*      */     
/* 2777 */     if (charsetIndex != -1) {
/*      */       try {
/* 2779 */         if (this.indexToMysqlCharset.size() > 0) {
/* 2780 */           javaEncoding = CharsetMapping.getJavaEncodingForMysqlCharset((String)this.indexToMysqlCharset.get(Integer.valueOf(charsetIndex)), getEncoding());
/*      */         }
/*      */         
/* 2783 */         if (javaEncoding == null) {
/* 2784 */           javaEncoding = CharsetMapping.getJavaEncodingForCollationIndex(Integer.valueOf(charsetIndex), getEncoding());
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 2788 */         throw SQLError.createSQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", "S1000", getExceptionInterceptor());
/*      */       }
/*      */       catch (RuntimeException ex) {
/* 2791 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 2792 */         sqlEx.initCause(ex);
/* 2793 */         throw sqlEx;
/*      */       }
/*      */       
/*      */ 
/* 2797 */       if (javaEncoding == null) {
/* 2798 */         javaEncoding = getEncoding();
/*      */       }
/*      */     } else {
/* 2801 */       javaEncoding = getEncoding();
/*      */     }
/*      */     
/* 2804 */     return javaEncoding;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TimeZone getDefaultTimeZone()
/*      */   {
/* 2812 */     return getCacheDefaultTimezone() ? this.defaultTimeZone : TimeUtil.getDefaultTimeZone(false);
/*      */   }
/*      */   
/*      */   public String getErrorMessageEncoding() {
/* 2816 */     return this.errorMessageEncoding;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 2823 */     return 2;
/*      */   }
/*      */   
/*      */   public long getId() {
/* 2827 */     return this.connectionId;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/* 2839 */     synchronized (getConnectionMutex()) {
/* 2840 */       if (this.lastQueryFinishedTime == 0L) {
/* 2841 */         return 0L;
/*      */       }
/*      */       
/* 2844 */       long now = System.currentTimeMillis();
/* 2845 */       long idleTime = now - this.lastQueryFinishedTime;
/*      */       
/* 2847 */       return idleTime;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MysqlIO getIO()
/*      */     throws SQLException
/*      */   {
/* 2859 */     if ((this.io == null) || (this.isClosed)) {
/* 2860 */       throw SQLError.createSQLException("Operation not allowed on closed connection", "08003", getExceptionInterceptor());
/*      */     }
/*      */     
/* 2863 */     return this.io;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Log getLog()
/*      */     throws SQLException
/*      */   {
/* 2875 */     return this.log;
/*      */   }
/*      */   
/*      */   public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
/* 2879 */     return getMaxBytesPerChar(null, javaCharsetName);
/*      */   }
/*      */   
/*      */   public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException
/*      */   {
/* 2884 */     String charset = null;
/* 2885 */     int res = 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2892 */       if (this.indexToCustomMysqlCharset != null) {
/* 2893 */         charset = (String)this.indexToCustomMysqlCharset.get(charsetIndex);
/*      */       }
/*      */       
/* 2896 */       if (charset == null) {
/* 2897 */         charset = CharsetMapping.getMysqlCharsetNameForCollationIndex(charsetIndex);
/*      */       }
/*      */       
/*      */ 
/* 2901 */       if (charset == null) {
/* 2902 */         charset = CharsetMapping.getMysqlCharsetForJavaEncoding(javaCharsetName, this);
/*      */       }
/*      */       
/*      */ 
/* 2906 */       Integer mblen = null;
/* 2907 */       if (this.mysqlCharsetToCustomMblen != null) {
/* 2908 */         mblen = (Integer)this.mysqlCharsetToCustomMblen.get(charset);
/*      */       }
/*      */       
/*      */ 
/* 2912 */       if (mblen == null) {
/* 2913 */         mblen = Integer.valueOf(CharsetMapping.getMblen(charset));
/*      */       }
/*      */       
/* 2916 */       if (mblen != null) {
/* 2917 */         res = mblen.intValue();
/*      */       }
/*      */     } catch (SQLException ex) {
/* 2920 */       throw ex;
/*      */     } catch (RuntimeException ex) {
/* 2922 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 2923 */       sqlEx.initCause(ex);
/* 2924 */       throw sqlEx;
/*      */     }
/*      */     
/* 2927 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2941 */     return getMetaData(true, true);
/*      */   }
/*      */   
/*      */   private java.sql.DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
/* 2945 */     if (checkClosed) {
/* 2946 */       checkClosed();
/*      */     }
/*      */     
/* 2949 */     return DatabaseMetaData.getInstance(getMultiHostSafeProxy(), this.database, checkForInfoSchema);
/*      */   }
/*      */   
/*      */   public java.sql.Statement getMetadataSafeStatement() throws SQLException {
/* 2953 */     java.sql.Statement stmt = createStatement();
/*      */     
/* 2955 */     if (stmt.getMaxRows() != 0) {
/* 2956 */       stmt.setMaxRows(0);
/*      */     }
/*      */     
/* 2959 */     stmt.setEscapeProcessing(false);
/*      */     
/* 2961 */     if (stmt.getFetchSize() != 0) {
/* 2962 */       stmt.setFetchSize(0);
/*      */     }
/*      */     
/* 2965 */     return stmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getNetBufferLength()
/*      */   {
/* 2972 */     return this.netBufferLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public String getServerCharacterEncoding()
/*      */   {
/* 2980 */     return getServerCharset();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getServerCharset()
/*      */   {
/* 2989 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 2990 */       String charset = null;
/* 2991 */       if (this.indexToCustomMysqlCharset != null) {
/* 2992 */         charset = (String)this.indexToCustomMysqlCharset.get(Integer.valueOf(this.io.serverCharsetIndex));
/*      */       }
/* 2994 */       if (charset == null) {
/* 2995 */         charset = CharsetMapping.getMysqlCharsetNameForCollationIndex(Integer.valueOf(this.io.serverCharsetIndex));
/*      */       }
/* 2997 */       return charset != null ? charset : (String)this.serverVariables.get("character_set_server");
/*      */     }
/* 2999 */     return (String)this.serverVariables.get("character_set");
/*      */   }
/*      */   
/*      */   public int getServerMajorVersion() {
/* 3003 */     return this.io.getServerMajorVersion();
/*      */   }
/*      */   
/*      */   public int getServerMinorVersion() {
/* 3007 */     return this.io.getServerMinorVersion();
/*      */   }
/*      */   
/*      */   public int getServerSubMinorVersion() {
/* 3011 */     return this.io.getServerSubMinorVersion();
/*      */   }
/*      */   
/*      */   public TimeZone getServerTimezoneTZ() {
/* 3015 */     return this.serverTimezoneTZ;
/*      */   }
/*      */   
/*      */   public String getServerVariable(String variableName) {
/* 3019 */     if (this.serverVariables != null) {
/* 3020 */       return (String)this.serverVariables.get(variableName);
/*      */     }
/*      */     
/* 3023 */     return null;
/*      */   }
/*      */   
/*      */   public String getServerVersion() {
/* 3027 */     return this.io.getServerVersion();
/*      */   }
/*      */   
/*      */   public Calendar getSessionLockedCalendar()
/*      */   {
/* 3032 */     return this.sessionCalendar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 3044 */     synchronized (getConnectionMutex()) {
/* 3045 */       if ((this.hasIsolationLevels) && (!getUseLocalSessionState())) {
/* 3046 */         java.sql.Statement stmt = null;
/* 3047 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 3050 */           stmt = getMetadataSafeStatement();
/*      */           
/* 3052 */           String query = null;
/*      */           
/* 3054 */           int offset = 0;
/*      */           
/* 3056 */           if (versionMeetsMinimum(4, 0, 3)) {
/* 3057 */             query = "SELECT @@session.tx_isolation";
/* 3058 */             offset = 1;
/*      */           } else {
/* 3060 */             query = "SHOW VARIABLES LIKE 'transaction_isolation'";
/* 3061 */             offset = 2;
/*      */           }
/*      */           
/* 3064 */           rs = stmt.executeQuery(query);
/*      */           
/* 3066 */           if (rs.next()) {
/* 3067 */             String s = rs.getString(offset);
/*      */             
/* 3069 */             if (s != null) {
/* 3070 */               Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */               
/* 3072 */               if (intTI != null) {
/* 3073 */                 int i = intTI.intValue();jsr 68;return i;
/*      */               }
/*      */             }
/*      */             
/* 3077 */             throw SQLError.createSQLException("Could not map transaction isolation '" + s + " to a valid JDBC level.", "S1000", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 3081 */           throw SQLError.createSQLException("Could not retrieve transaction isolation level from server", "S1000", getExceptionInterceptor());
/*      */         }
/*      */         finally
/*      */         {
/* 3085 */           jsr 6; } localObject2 = returnAddress; if (rs != null) {
/*      */           try {
/* 3087 */             rs.close();
/*      */           }
/*      */           catch (Exception ex) {}
/*      */           
/*      */ 
/* 3092 */           rs = null;
/*      */         }
/*      */         
/* 3095 */         if (stmt != null) {
/*      */           try {
/* 3097 */             stmt.close();
/*      */           }
/*      */           catch (Exception ex) {}
/*      */           
/*      */ 
/* 3102 */           stmt = null; } ret;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3107 */       return this.isolationLevel;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map<String, Class<?>> getTypeMap()
/*      */     throws SQLException
/*      */   {
/* 3120 */     synchronized (getConnectionMutex()) {
/* 3121 */       if (this.typeMap == null) {
/* 3122 */         this.typeMap = new HashMap();
/*      */       }
/*      */       
/* 3125 */       return this.typeMap;
/*      */     }
/*      */   }
/*      */   
/*      */   public String getURL() {
/* 3130 */     return this.myURL;
/*      */   }
/*      */   
/*      */   public String getUser() {
/* 3134 */     return this.user;
/*      */   }
/*      */   
/*      */   public Calendar getUtcCalendar() {
/* 3138 */     return this.utcCalendar;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 3151 */     return null;
/*      */   }
/*      */   
/*      */   public boolean hasSameProperties(Connection c) {
/* 3155 */     return this.props.equals(c.getProperties());
/*      */   }
/*      */   
/*      */   public Properties getProperties() {
/* 3159 */     return this.props;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public boolean hasTriedMaster() {
/* 3164 */     return this.hasTriedMasterFlag;
/*      */   }
/*      */   
/*      */   public void incrementNumberOfPreparedExecutes() {
/* 3168 */     if (getGatherPerformanceMetrics()) {
/* 3169 */       this.numberOfPreparedExecutes += 1L;
/*      */       
/*      */ 
/* 3172 */       this.numberOfQueriesIssued += 1L;
/*      */     }
/*      */   }
/*      */   
/*      */   public void incrementNumberOfPrepares() {
/* 3177 */     if (getGatherPerformanceMetrics()) {
/* 3178 */       this.numberOfPrepares += 1L;
/*      */     }
/*      */   }
/*      */   
/*      */   public void incrementNumberOfResultSetsCreated() {
/* 3183 */     if (getGatherPerformanceMetrics()) {
/* 3184 */       this.numberOfResultSetsCreated += 1L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initializeDriverProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 3196 */     initializeProperties(info);
/*      */     
/* 3198 */     String exceptionInterceptorClasses = getExceptionInterceptors();
/*      */     
/* 3200 */     if ((exceptionInterceptorClasses != null) && (!"".equals(exceptionInterceptorClasses))) {
/* 3201 */       this.exceptionInterceptor = new ExceptionInterceptorChain(exceptionInterceptorClasses);
/*      */     }
/*      */     
/* 3204 */     this.usePlatformCharsetConverters = getUseJvmCharsetConverters();
/*      */     
/* 3206 */     this.log = LogFactory.getLogger(getLogger(), "MySQL", getExceptionInterceptor());
/*      */     
/* 3208 */     if ((getProfileSql()) || (getUseUsageAdvisor())) {
/* 3209 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(getMultiHostSafeProxy());
/*      */     }
/*      */     
/* 3212 */     if (getCachePreparedStatements()) {
/* 3213 */       createPreparedStatementCaches();
/*      */     }
/*      */     
/* 3216 */     if ((getNoDatetimeStringSync()) && (getUseTimezone())) {
/* 3217 */       throw SQLError.createSQLException("Can't enable noDatetimeStringSync and useTimezone configuration properties at the same time", "01S00", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3221 */     if (getCacheCallableStatements()) {
/* 3222 */       this.parsedCallableStatementCache = new LRUCache(getCallableStatementCacheSize());
/*      */     }
/*      */     
/* 3225 */     if (getAllowMultiQueries()) {
/* 3226 */       setCacheResultSetMetadata(false);
/*      */     }
/*      */     
/* 3229 */     if (getCacheResultSetMetadata()) {
/* 3230 */       this.resultSetMetadataCache = new LRUCache(getMetadataCacheSize());
/*      */     }
/*      */     
/* 3233 */     if (getSocksProxyHost() != null) {
/* 3234 */       setSocketFactoryClassName("com.mysql.jdbc.SocksProxySocketFactory");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initializePropsFromServer()
/*      */     throws SQLException
/*      */   {
/* 3246 */     String connectionInterceptorClasses = getConnectionLifecycleInterceptors();
/*      */     
/* 3248 */     this.connectionLifecycleInterceptors = null;
/*      */     
/* 3250 */     if (connectionInterceptorClasses != null) {
/* 3251 */       this.connectionLifecycleInterceptors = Util.loadExtensions(this, this.props, connectionInterceptorClasses, "Connection.badLifecycleInterceptor", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3255 */     setSessionVariables();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3261 */     if (!versionMeetsMinimum(4, 1, 0)) {
/* 3262 */       setTransformedBitIsBoolean(false);
/*      */     }
/*      */     
/* 3265 */     this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3270 */     if ((getUseServerPreparedStmts()) && (versionMeetsMinimum(4, 1, 0))) {
/* 3271 */       this.useServerPreparedStmts = true;
/*      */       
/* 3273 */       if ((versionMeetsMinimum(5, 0, 0)) && (!versionMeetsMinimum(5, 0, 3))) {
/* 3274 */         this.useServerPreparedStmts = false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3282 */     if (versionMeetsMinimum(3, 21, 22)) {
/* 3283 */       loadServerVariables();
/*      */       
/* 3285 */       if (versionMeetsMinimum(5, 0, 2)) {
/* 3286 */         this.autoIncrementIncrement = getServerVariableAsInt("auto_increment_increment", 1);
/*      */       } else {
/* 3288 */         this.autoIncrementIncrement = 1;
/*      */       }
/*      */       
/* 3291 */       buildCollationMapping();
/*      */       
/* 3293 */       LicenseConfiguration.checkLicenseType(this.serverVariables);
/*      */       
/* 3295 */       String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
/*      */       
/* 3297 */       this.lowerCaseTableNames = (("on".equalsIgnoreCase(lowerCaseTables)) || ("1".equalsIgnoreCase(lowerCaseTables)) || ("2".equalsIgnoreCase(lowerCaseTables)));
/*      */       
/* 3299 */       this.storesLowerCaseTableName = (("1".equalsIgnoreCase(lowerCaseTables)) || ("on".equalsIgnoreCase(lowerCaseTables)));
/*      */       
/* 3301 */       configureTimezone();
/*      */       
/* 3303 */       if (this.serverVariables.containsKey("max_allowed_packet")) {
/* 3304 */         int serverMaxAllowedPacket = getServerVariableAsInt("max_allowed_packet", -1);
/*      */         
/* 3306 */         if ((serverMaxAllowedPacket != -1) && ((serverMaxAllowedPacket < getMaxAllowedPacket()) || (getMaxAllowedPacket() <= 0))) {
/* 3307 */           setMaxAllowedPacket(serverMaxAllowedPacket);
/* 3308 */         } else if ((serverMaxAllowedPacket == -1) && (getMaxAllowedPacket() == -1)) {
/* 3309 */           setMaxAllowedPacket(65535);
/*      */         }
/*      */         
/* 3312 */         if (getUseServerPrepStmts()) {
/* 3313 */           int preferredBlobSendChunkSize = getBlobSendChunkSize();
/*      */           
/*      */ 
/* 3316 */           int packetHeaderSize = 8203;
/* 3317 */           int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, getMaxAllowedPacket()) - packetHeaderSize;
/*      */           
/* 3319 */           if (allowedBlobSendChunkSize <= 0) {
/* 3320 */             throw SQLError.createSQLException("Connection setting too low for 'maxAllowedPacket'. When 'useServerPrepStmts=true', 'maxAllowedPacket' must be higher than " + packetHeaderSize + ". Check also 'max_allowed_packet' in MySQL configuration files.", "01S00", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3327 */           setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
/*      */         }
/*      */       }
/*      */       
/* 3331 */       if (this.serverVariables.containsKey("net_buffer_length")) {
/* 3332 */         this.netBufferLength = getServerVariableAsInt("net_buffer_length", 16384);
/*      */       }
/*      */       
/* 3335 */       checkTransactionIsolationLevel();
/*      */       
/* 3337 */       if (!versionMeetsMinimum(4, 1, 0)) {
/* 3338 */         checkServerEncoding();
/*      */       }
/*      */       
/* 3341 */       this.io.checkForCharsetMismatch();
/*      */       
/* 3343 */       if (this.serverVariables.containsKey("sql_mode")) {
/* 3344 */         String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
/* 3345 */         if (StringUtils.isStrictlyNumeric(sqlModeAsString))
/*      */         {
/* 3347 */           this.useAnsiQuotes = ((Integer.parseInt(sqlModeAsString) & 0x4) > 0);
/* 3348 */         } else if (sqlModeAsString != null) {
/* 3349 */           this.useAnsiQuotes = (sqlModeAsString.indexOf("ANSI_QUOTES") != -1);
/* 3350 */           this.noBackslashEscapes = (sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3355 */     boolean overrideDefaultAutocommit = isAutoCommitNonDefaultOnServer();
/*      */     
/* 3357 */     configureClientCharacterSet(false);
/*      */     try
/*      */     {
/* 3360 */       this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages(this);
/*      */     } catch (SQLException ex) {
/* 3362 */       throw ex;
/*      */     } catch (RuntimeException ex) {
/* 3364 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 3365 */       sqlEx.initCause(ex);
/* 3366 */       throw sqlEx;
/*      */     }
/*      */     
/* 3369 */     if (versionMeetsMinimum(3, 23, 15)) {
/* 3370 */       this.transactionsSupported = true;
/*      */       
/* 3372 */       if (!overrideDefaultAutocommit) {
/*      */         try {
/* 3374 */           setAutoCommit(true);
/*      */         } catch (SQLException ex) {
/* 3376 */           if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 3377 */             throw ex;
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/* 3382 */       this.transactionsSupported = false;
/*      */     }
/*      */     
/* 3385 */     if (versionMeetsMinimum(3, 23, 36)) {
/* 3386 */       this.hasIsolationLevels = true;
/*      */     } else {
/* 3388 */       this.hasIsolationLevels = false;
/*      */     }
/*      */     
/* 3391 */     this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
/*      */     
/* 3393 */     this.io.resetMaxBuf();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3399 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3400 */       String characterSetResultsOnServerMysql = (String)this.serverVariables.get("jdbc.local.character_set_results");
/*      */       
/* 3402 */       if ((characterSetResultsOnServerMysql == null) || (StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL")) || (characterSetResultsOnServerMysql.length() == 0))
/*      */       {
/* 3404 */         String defaultMetadataCharsetMysql = (String)this.serverVariables.get("character_set_system");
/* 3405 */         String defaultMetadataCharset = null;
/*      */         
/* 3407 */         if (defaultMetadataCharsetMysql != null) {
/* 3408 */           defaultMetadataCharset = CharsetMapping.getJavaEncodingForMysqlCharset(defaultMetadataCharsetMysql);
/*      */         } else {
/* 3410 */           defaultMetadataCharset = "UTF-8";
/*      */         }
/*      */         
/* 3413 */         this.characterSetMetadata = defaultMetadataCharset;
/*      */       } else {
/* 3415 */         this.characterSetResultsOnServer = CharsetMapping.getJavaEncodingForMysqlCharset(characterSetResultsOnServerMysql);
/* 3416 */         this.characterSetMetadata = this.characterSetResultsOnServer;
/*      */       }
/*      */     } else {
/* 3419 */       this.characterSetMetadata = getEncoding();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3426 */     if ((versionMeetsMinimum(4, 1, 0)) && (!versionMeetsMinimum(4, 1, 10)) && (getAllowMultiQueries()) && 
/* 3427 */       (isQueryCacheEnabled())) {
/* 3428 */       setAllowMultiQueries(false);
/*      */     }
/*      */     
/*      */ 
/* 3432 */     if ((versionMeetsMinimum(5, 0, 0)) && ((getUseLocalTransactionState()) || (getElideSetAutoCommits())) && (isQueryCacheEnabled()) && (!versionMeetsMinimum(6, 0, 10)))
/*      */     {
/*      */ 
/* 3435 */       setUseLocalTransactionState(false);
/* 3436 */       setElideSetAutoCommits(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3443 */     setupServerForTruncationChecks();
/*      */   }
/*      */   
/*      */   private boolean isQueryCacheEnabled() {
/* 3447 */     return ("ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type"))) && (!"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size")));
/*      */   }
/*      */   
/*      */   private int getServerVariableAsInt(String variableName, int fallbackValue) throws SQLException {
/*      */     try {
/* 3452 */       return Integer.parseInt((String)this.serverVariables.get(variableName));
/*      */     } catch (NumberFormatException nfe) {
/* 3454 */       getLog().logWarn(Messages.getString("Connection.BadValueInServerVariables", new Object[] { variableName, this.serverVariables.get(variableName), Integer.valueOf(fallbackValue) }));
/*      */     }
/*      */     
/* 3457 */     return fallbackValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isAutoCommitNonDefaultOnServer()
/*      */     throws SQLException
/*      */   {
/* 3470 */     boolean overrideDefaultAutocommit = false;
/*      */     
/* 3472 */     String initConnectValue = (String)this.serverVariables.get("init_connect");
/*      */     
/* 3474 */     if ((versionMeetsMinimum(4, 1, 2)) && (initConnectValue != null) && (initConnectValue.length() > 0)) {
/* 3475 */       if (!getElideSetAutoCommits())
/*      */       {
/* 3477 */         ResultSet rs = null;
/* 3478 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 3481 */           stmt = getMetadataSafeStatement();
/*      */           
/* 3483 */           rs = stmt.executeQuery("SELECT @@session.autocommit");
/*      */           
/* 3485 */           if (rs.next()) {
/* 3486 */             this.autoCommit = rs.getBoolean(1);
/* 3487 */             if (this.autoCommit != true) {
/* 3488 */               overrideDefaultAutocommit = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/* 3493 */           if (rs != null) {
/*      */             try {
/* 3495 */               rs.close();
/*      */             }
/*      */             catch (SQLException sqlEx) {}
/*      */           }
/*      */           
/*      */ 
/* 3501 */           if (stmt != null) {
/*      */             try {
/* 3503 */               stmt.close();
/*      */ 
/*      */             }
/*      */             catch (SQLException sqlEx) {}
/*      */           }
/*      */         }
/*      */       }
/* 3510 */       else if (getIO().isSetNeededForAutoCommitMode(true))
/*      */       {
/* 3512 */         this.autoCommit = false;
/* 3513 */         overrideDefaultAutocommit = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3518 */     return overrideDefaultAutocommit;
/*      */   }
/*      */   
/*      */   public boolean isClientTzUTC() {
/* 3522 */     return this.isClientTzUTC;
/*      */   }
/*      */   
/*      */   public boolean isClosed() {
/* 3526 */     return this.isClosed;
/*      */   }
/*      */   
/*      */   public boolean isCursorFetchEnabled() throws SQLException {
/* 3530 */     return (versionMeetsMinimum(5, 0, 2)) && (getUseCursorFetch());
/*      */   }
/*      */   
/*      */   public boolean isInGlobalTx() {
/* 3534 */     return this.isInGlobalTx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isMasterConnection()
/*      */   {
/* 3545 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet()
/*      */   {
/* 3555 */     return this.noBackslashEscapes;
/*      */   }
/*      */   
/*      */   public boolean isReadInfoMsgEnabled() {
/* 3559 */     return this.readInfoMsg;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 3572 */     return isReadOnly(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isRunningOnJDK13()
/*      */   {
/* 3635 */     return this.isRunningOnJDK13;
/*      */   }
/*      */   
/*      */   public boolean isSameResource(Connection otherConnection) {
/* 3639 */     synchronized (getConnectionMutex()) {
/* 3640 */       if (otherConnection == null) {
/* 3641 */         return false;
/*      */       }
/*      */       
/* 3644 */       boolean directCompare = true;
/*      */       
/* 3646 */       String otherHost = ((ConnectionImpl)otherConnection).origHostToConnectTo;
/* 3647 */       String otherOrigDatabase = ((ConnectionImpl)otherConnection).origDatabaseToConnectTo;
/* 3648 */       String otherCurrentCatalog = ((ConnectionImpl)otherConnection).database;
/*      */       
/* 3650 */       if (!nullSafeCompare(otherHost, this.origHostToConnectTo)) {
/* 3651 */         directCompare = false;
/* 3652 */       } else if ((otherHost != null) && (otherHost.indexOf(',') == -1) && (otherHost.indexOf(':') == -1))
/*      */       {
/* 3654 */         directCompare = ((ConnectionImpl)otherConnection).origPortToConnectTo == this.origPortToConnectTo;
/*      */       }
/*      */       
/* 3657 */       if ((directCompare) && (
/* 3658 */         (!nullSafeCompare(otherOrigDatabase, this.origDatabaseToConnectTo)) || (!nullSafeCompare(otherCurrentCatalog, this.database)))) {
/* 3659 */         directCompare = false;
/*      */       }
/*      */       
/*      */ 
/* 3663 */       if (directCompare) {
/* 3664 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 3668 */       String otherResourceId = ((ConnectionImpl)otherConnection).getResourceId();
/* 3669 */       String myResourceId = getResourceId();
/*      */       
/* 3671 */       if ((otherResourceId != null) || (myResourceId != null)) {
/* 3672 */         directCompare = nullSafeCompare(otherResourceId, myResourceId);
/*      */         
/* 3674 */         if (directCompare) {
/* 3675 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 3679 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isServerTzUTC() {
/* 3684 */     return this.isServerTzUTC;
/*      */   }
/*      */   
/*      */   private void createConfigCacheIfNeeded() throws SQLException {
/* 3688 */     synchronized (getConnectionMutex()) {
/* 3689 */       if (this.serverConfigCache != null) {
/* 3690 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 3696 */         Class<?> factoryClass = Class.forName(getServerConfigCacheFactory());
/*      */         
/*      */ 
/* 3699 */         CacheAdapterFactory<String, Map<String, String>> cacheFactory = (CacheAdapterFactory)factoryClass.newInstance();
/*      */         
/* 3701 */         this.serverConfigCache = cacheFactory.getInstance(this, this.myURL, Integer.MAX_VALUE, Integer.MAX_VALUE, this.props);
/*      */         
/* 3703 */         ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor()
/*      */         {
/*      */           public void init(Connection conn, Properties config)
/*      */             throws SQLException
/*      */           {}
/*      */           
/*      */           public void destroy() {}
/*      */           
/*      */           public SQLException interceptException(SQLException sqlEx, Connection conn)
/*      */           {
/* 3713 */             if ((sqlEx.getSQLState() != null) && (sqlEx.getSQLState().startsWith("08"))) {
/* 3714 */               ConnectionImpl.this.serverConfigCache.invalidate(ConnectionImpl.this.getURL());
/*      */             }
/* 3716 */             return null;
/*      */           }
/*      */         };
/*      */         
/* 3720 */         if (this.exceptionInterceptor == null) {
/* 3721 */           this.exceptionInterceptor = evictOnCommsError;
/*      */         } else {
/* 3723 */           ((ExceptionInterceptorChain)this.exceptionInterceptor).addRingZero(evictOnCommsError);
/*      */         }
/*      */       } catch (ClassNotFoundException e) {
/* 3726 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 3729 */         sqlEx.initCause(e);
/*      */         
/* 3731 */         throw sqlEx;
/*      */       } catch (InstantiationException e) {
/* 3733 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 3736 */         sqlEx.initCause(e);
/*      */         
/* 3738 */         throw sqlEx;
/*      */       } catch (IllegalAccessException e) {
/* 3740 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[] { getParseInfoCacheFactory(), "parseInfoCacheFactory" }), getExceptionInterceptor());
/*      */         
/*      */ 
/* 3743 */         sqlEx.initCause(e);
/*      */         
/* 3745 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void loadServerVariables()
/*      */     throws SQLException
/*      */   {
/* 3761 */     if (getCacheServerConfiguration()) {
/* 3762 */       createConfigCacheIfNeeded();
/*      */       
/* 3764 */       Map<String, String> cachedVariableMap = (Map)this.serverConfigCache.get(getURL());
/*      */       
/* 3766 */       if (cachedVariableMap != null) {
/* 3767 */         String cachedServerVersion = (String)cachedVariableMap.get("server_version_string");
/*      */         
/* 3769 */         if ((cachedServerVersion != null) && (this.io.getServerVersion() != null) && (cachedServerVersion.equals(this.io.getServerVersion()))) {
/* 3770 */           this.serverVariables = cachedVariableMap;
/*      */           
/* 3772 */           return;
/*      */         }
/*      */         
/* 3775 */         this.serverConfigCache.invalidate(getURL());
/*      */       }
/*      */     }
/*      */     
/* 3779 */     java.sql.Statement stmt = null;
/* 3780 */     ResultSet results = null;
/*      */     try
/*      */     {
/* 3783 */       stmt = getMetadataSafeStatement();
/*      */       
/* 3785 */       String version = this.dbmd.getDriverVersion();
/*      */       
/* 3787 */       if ((version != null) && (version.indexOf('*') != -1)) {
/* 3788 */         StringBuilder buf = new StringBuilder(version.length() + 10);
/*      */         
/* 3790 */         for (int i = 0; i < version.length(); i++) {
/* 3791 */           char c = version.charAt(i);
/*      */           
/* 3793 */           if (c == '*') {
/* 3794 */             buf.append("[star]");
/*      */           } else {
/* 3796 */             buf.append(c);
/*      */           }
/*      */         }
/*      */         
/* 3800 */         version = buf.toString();
/*      */       }
/*      */       
/* 3803 */       String versionComment = "/* " + version + " */";
/*      */       
/* 3805 */       this.serverVariables = new HashMap();
/*      */       try
/*      */       {
/* 3808 */         if (versionMeetsMinimum(5, 1, 0)) {
/* 3809 */           StringBuilder queryBuf = new StringBuilder(versionComment).append("SELECT");
/* 3810 */           queryBuf.append("  @@session.auto_increment_increment AS auto_increment_increment");
/* 3811 */           queryBuf.append(", @@character_set_client AS character_set_client");
/* 3812 */           queryBuf.append(", @@character_set_connection AS character_set_connection");
/* 3813 */           queryBuf.append(", @@character_set_results AS character_set_results");
/* 3814 */           queryBuf.append(", @@character_set_server AS character_set_server");
/* 3815 */           queryBuf.append(", @@init_connect AS init_connect");
/* 3816 */           queryBuf.append(", @@interactive_timeout AS interactive_timeout");
/* 3817 */           if (!versionMeetsMinimum(5, 5, 0)) {
/* 3818 */             queryBuf.append(", @@language AS language");
/*      */           }
/* 3820 */           queryBuf.append(", @@license AS license");
/* 3821 */           queryBuf.append(", @@lower_case_table_names AS lower_case_table_names");
/* 3822 */           queryBuf.append(", @@max_allowed_packet AS max_allowed_packet");
/* 3823 */           queryBuf.append(", @@net_buffer_length AS net_buffer_length");
/* 3824 */           queryBuf.append(", @@net_write_timeout AS net_write_timeout");
/* 3825 */           queryBuf.append(", @@query_cache_size AS query_cache_size");
/* 3826 */           queryBuf.append(", @@query_cache_type AS query_cache_type");
/* 3827 */           queryBuf.append(", @@sql_mode AS sql_mode");
/* 3828 */           queryBuf.append(", @@system_time_zone AS system_time_zone");
/* 3829 */           queryBuf.append(", @@time_zone AS time_zone");
/* 3830 */           queryBuf.append(", @@tx_isolation AS tx_isolation");
/* 3831 */           queryBuf.append(", @@wait_timeout AS wait_timeout");
/*      */           
/* 3833 */           results = stmt.executeQuery(queryBuf.toString());
/* 3834 */           if (results.next()) {
/* 3835 */             ResultSetMetaData rsmd = results.getMetaData();
/* 3836 */             for (int i = 1; i <= rsmd.getColumnCount(); i++) {
/* 3837 */               this.serverVariables.put(rsmd.getColumnLabel(i), results.getString(i));
/*      */             }
/*      */           }
/*      */         } else {
/* 3841 */           results = stmt.executeQuery(versionComment + "SHOW VARIABLES");
/* 3842 */           while (results.next()) {
/* 3843 */             this.serverVariables.put(results.getString(1), results.getString(2));
/*      */           }
/*      */         }
/*      */         
/* 3847 */         results.close();
/* 3848 */         results = null;
/*      */       } catch (SQLException ex) {
/* 3850 */         if ((ex.getErrorCode() != 1820) || (getDisconnectOnExpiredPasswords())) {
/* 3851 */           throw ex;
/*      */         }
/*      */       }
/*      */       
/* 3855 */       if (getCacheServerConfiguration()) {
/* 3856 */         this.serverVariables.put("server_version_string", this.io.getServerVersion());
/*      */         
/* 3858 */         this.serverConfigCache.put(getURL(), this.serverVariables);
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {
/* 3862 */       throw e;
/*      */     } finally {
/* 3864 */       if (results != null) {
/*      */         try {
/* 3866 */           results.close();
/*      */         }
/*      */         catch (SQLException sqlE) {}
/*      */       }
/*      */       
/* 3871 */       if (stmt != null) {
/*      */         try {
/* 3873 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlE) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/* 3880 */   private int autoIncrementIncrement = 0;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   
/* 3883 */   public int getAutoIncrementIncrement() { return this.autoIncrementIncrement; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean lowerCaseTableNames()
/*      */   {
/* 3892 */     return this.lowerCaseTableNames;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/* 3908 */     if (sql == null) {
/* 3909 */       return null;
/*      */     }
/*      */     
/* 3912 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getMultiHostSafeProxy());
/*      */     
/* 3914 */     if ((escapedSqlResult instanceof String)) {
/* 3915 */       return (String)escapedSqlResult;
/*      */     }
/*      */     
/* 3918 */     return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */   }
/*      */   
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException {
/* 3922 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getMultiHostSafeProxy());
/*      */     
/* 3924 */     boolean isFunctionCall = false;
/* 3925 */     String parsedSql = null;
/*      */     
/* 3927 */     if ((escapedSqlResult instanceof EscapeProcessorResult)) {
/* 3928 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 3929 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 3931 */       parsedSql = (String)escapedSqlResult;
/* 3932 */       isFunctionCall = false;
/*      */     }
/*      */     
/* 3935 */     return CallableStatement.getInstance(getMultiHostSafeProxy(), parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */   
/*      */   public boolean parserKnowsUnicode() {
/* 3939 */     return this.parserKnowsUnicode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void ping()
/*      */     throws SQLException
/*      */   {
/* 3949 */     pingInternal(true, 0);
/*      */   }
/*      */   
/*      */   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
/* 3953 */     if (checkForClosedConnection) {
/* 3954 */       checkClosed();
/*      */     }
/*      */     
/* 3957 */     long pingMillisLifetime = getSelfDestructOnPingSecondsLifetime();
/* 3958 */     int pingMaxOperations = getSelfDestructOnPingMaxOperations();
/*      */     
/* 3960 */     if (((pingMillisLifetime > 0L) && (System.currentTimeMillis() - this.connectionCreationTimeMillis > pingMillisLifetime)) || ((pingMaxOperations > 0) && (pingMaxOperations <= this.io.getCommandCount())))
/*      */     {
/*      */ 
/* 3963 */       close();
/*      */       
/* 3965 */       throw SQLError.createSQLException(Messages.getString("Connection.exceededConnectionLifetime"), "08S01", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3969 */     this.io.sendCommand(14, null, null, false, null, timeoutMillis);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/* 3978 */     return prepareCall(sql, 1003, 1007);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 3997 */     if (versionMeetsMinimum(5, 0, 0)) {
/* 3998 */       CallableStatement cStmt = null;
/*      */       
/* 4000 */       if (!getCacheCallableStatements())
/*      */       {
/* 4002 */         cStmt = parseCallableStatement(sql);
/*      */       } else {
/* 4004 */         synchronized (this.parsedCallableStatementCache) {
/* 4005 */           CompoundCacheKey key = new CompoundCacheKey(getCatalog(), sql);
/*      */           
/* 4007 */           CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */           
/*      */ 
/* 4010 */           if (cachedParamInfo != null) {
/* 4011 */             cStmt = CallableStatement.getInstance(getMultiHostSafeProxy(), cachedParamInfo);
/*      */           } else {
/* 4013 */             cStmt = parseCallableStatement(sql);
/*      */             
/* 4015 */             synchronized (cStmt) {
/* 4016 */               cachedParamInfo = cStmt.paramInfo;
/*      */             }
/*      */             
/* 4019 */             this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4024 */       cStmt.setResultSetType(resultSetType);
/* 4025 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */       
/* 4027 */       return cStmt;
/*      */     }
/*      */     
/* 4030 */     throw SQLError.createSQLException("Callable statements not supported.", "S1C00", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4037 */     if ((getPedantic()) && 
/* 4038 */       (resultSetHoldability != 1)) {
/* 4039 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4044 */     CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */     
/* 4046 */     return cStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4071 */     return prepareStatement(sql, 1003, 1007);
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4078 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4080 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */     
/* 4082 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4101 */     synchronized (getConnectionMutex()) {
/* 4102 */       checkClosed();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 4107 */       PreparedStatement pStmt = null;
/*      */       
/* 4109 */       boolean canServerPrepare = true;
/*      */       
/* 4111 */       String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */       
/* 4113 */       if ((this.useServerPreparedStmts) && (getEmulateUnsupportedPstmts())) {
/* 4114 */         canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
/*      */       }
/*      */       
/* 4117 */       if ((this.useServerPreparedStmts) && (canServerPrepare)) {
/* 4118 */         if (getCachePreparedStatements()) {
/* 4119 */           synchronized (this.serverSideStatementCache) {
/* 4120 */             pStmt = (ServerPreparedStatement)this.serverSideStatementCache.remove(sql);
/*      */             
/* 4122 */             if (pStmt != null) {
/* 4123 */               ((ServerPreparedStatement)pStmt).setClosed(false);
/* 4124 */               pStmt.clearParameters();
/*      */             }
/*      */             
/* 4127 */             if (pStmt == null) {
/*      */               try {
/* 4129 */                 pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */                 
/* 4131 */                 if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4132 */                   ((ServerPreparedStatement)pStmt).isCached = true;
/*      */                 }
/*      */                 
/* 4135 */                 pStmt.setResultSetType(resultSetType);
/* 4136 */                 pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */               }
/*      */               catch (SQLException sqlEx) {
/* 4139 */                 if (getEmulateUnsupportedPstmts()) {
/* 4140 */                   pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */                   
/* 4142 */                   if (sql.length() < getPreparedStatementCacheSqlLimit()) {
/* 4143 */                     this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */                   }
/*      */                 } else {
/* 4146 */                   throw sqlEx;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } else {
/*      */           try {
/* 4153 */             pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
/*      */             
/* 4155 */             pStmt.setResultSetType(resultSetType);
/* 4156 */             pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 4159 */             if (getEmulateUnsupportedPstmts()) {
/* 4160 */               pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */             } else {
/* 4162 */               throw sqlEx;
/*      */             }
/*      */           }
/*      */         }
/*      */       } else {
/* 4167 */         pStmt = (PreparedStatement)clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
/*      */       }
/*      */       
/* 4170 */       return pStmt;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4178 */     if ((getPedantic()) && 
/* 4179 */       (resultSetHoldability != 1)) {
/* 4180 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4185 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4192 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4194 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */     
/* 4196 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 4203 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */     
/* 4205 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */     
/* 4207 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {
/* 4221 */     SQLException sqlEx = null;
/*      */     
/* 4223 */     if (isClosed()) {
/* 4224 */       return;
/*      */     }
/*      */     
/* 4227 */     this.forceClosedReason = reason;
/*      */     try
/*      */     {
/* 4230 */       if (!skipLocalTeardown) {
/* 4231 */         if ((!getAutoCommit()) && (issueRollback)) {
/*      */           try {
/* 4233 */             rollback();
/*      */           } catch (SQLException ex) {
/* 4235 */             sqlEx = ex;
/*      */           }
/*      */         }
/*      */         
/* 4239 */         reportMetrics();
/*      */         
/* 4241 */         if (getUseUsageAdvisor()) {
/* 4242 */           if (!calledExplicitly) {
/* 4243 */             String message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
/*      */             
/* 4245 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */           
/*      */ 
/* 4249 */           long connectionLifeTime = System.currentTimeMillis() - this.connectionCreationTimeMillis;
/*      */           
/* 4251 */           if (connectionLifeTime < 500L) {
/* 4252 */             String message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
/*      */             
/* 4254 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */           }
/*      */         }
/*      */         
/*      */         try
/*      */         {
/* 4260 */           closeAllOpenStatements();
/*      */         } catch (SQLException ex) {
/* 4262 */           sqlEx = ex;
/*      */         }
/*      */         
/* 4265 */         if (this.io != null) {
/*      */           try {
/* 4267 */             this.io.quit();
/*      */           }
/*      */           catch (Exception e) {}
/*      */         }
/*      */       }
/*      */       else {
/* 4273 */         this.io.forceClose();
/*      */       }
/*      */       
/* 4276 */       if (this.statementInterceptors != null) {
/* 4277 */         for (int i = 0; i < this.statementInterceptors.size(); i++) {
/* 4278 */           ((StatementInterceptorV2)this.statementInterceptors.get(i)).destroy();
/*      */         }
/*      */       }
/*      */       
/* 4282 */       if (this.exceptionInterceptor != null) {
/* 4283 */         this.exceptionInterceptor.destroy();
/*      */       }
/*      */     } finally {
/* 4286 */       this.openStatements.clear();
/* 4287 */       if (this.io != null) {
/* 4288 */         this.io.releaseResources();
/* 4289 */         this.io = null;
/*      */       }
/* 4291 */       this.statementInterceptors = null;
/* 4292 */       this.exceptionInterceptor = null;
/* 4293 */       ProfilerEventHandlerFactory.removeInstance(this);
/*      */       
/* 4295 */       synchronized (getConnectionMutex()) {
/* 4296 */         if (this.cancelTimer != null) {
/* 4297 */           this.cancelTimer.cancel();
/*      */         }
/*      */       }
/*      */       
/* 4301 */       this.isClosed = true;
/*      */     }
/*      */     
/* 4304 */     if (sqlEx != null) {
/* 4305 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   public void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException
/*      */   {
/* 4311 */     synchronized (getConnectionMutex()) {
/* 4312 */       if (pstmt.isPoolable()) {
/* 4313 */         synchronized (this.serverSideStatementCache) {
/* 4314 */           this.serverSideStatementCache.put(pstmt.originalSql, pstmt);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void decachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
/* 4321 */     synchronized (getConnectionMutex()) {
/* 4322 */       if (pstmt.isPoolable()) {
/* 4323 */         synchronized (this.serverSideStatementCache) {
/* 4324 */           this.serverSideStatementCache.remove(pstmt.originalSql);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerQueryExecutionTime(long queryTimeMs)
/*      */   {
/* 4334 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 4335 */       this.longestQueryTimeMs = queryTimeMs;
/*      */       
/* 4337 */       repartitionPerformanceHistogram();
/*      */     }
/*      */     
/* 4340 */     addToPerformanceHistogram(queryTimeMs, 1);
/*      */     
/* 4342 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 4343 */       this.shortestQueryTimeMs = (queryTimeMs == 0L ? 1L : queryTimeMs);
/*      */     }
/*      */     
/* 4346 */     this.numberOfQueriesIssued += 1L;
/*      */     
/* 4348 */     this.totalQueryTimeMs += queryTimeMs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerStatement(Statement stmt)
/*      */   {
/* 4358 */     this.openStatements.addIfAbsent(stmt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound)
/*      */   {
/* 4370 */     if (this.oldHistCounts == null) {
/* 4371 */       this.oldHistCounts = new int[histCounts.length];
/* 4372 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*      */     }
/*      */     
/* 4375 */     System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
/*      */     
/* 4377 */     System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
/*      */     
/* 4379 */     createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
/*      */     
/* 4381 */     for (int i = 0; i < 20; i++) {
/* 4382 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*      */     }
/*      */   }
/*      */   
/*      */   private void repartitionPerformanceHistogram() {
/* 4387 */     checkAndCreatePerformanceHistogram();
/*      */     
/* 4389 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, this.shortestQueryTimeMs == Long.MAX_VALUE ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */   
/*      */   private void repartitionTablesAccessedHistogram()
/*      */   {
/* 4394 */     checkAndCreateTablesAccessedHistogram();
/*      */     
/* 4396 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, this.minimumNumberTablesAccessed == Long.MAX_VALUE ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */   
/*      */   private void reportMetrics()
/*      */   {
/* 4401 */     if (getGatherPerformanceMetrics()) {
/* 4402 */       StringBuilder logMessage = new StringBuilder(256);
/*      */       
/* 4404 */       logMessage.append("** Performance Metrics Report **\n");
/* 4405 */       logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/* 4406 */       logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/* 4407 */       logMessage.append("\nAverage query execution time: " + this.totalQueryTimeMs / this.numberOfQueriesIssued + " ms");
/* 4408 */       logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/* 4409 */       logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/* 4410 */       logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/* 4411 */       logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*      */       
/* 4413 */       if (this.perfMetricsHistBreakpoints != null) {
/* 4414 */         logMessage.append("\n\n\tTiming Histogram:\n");
/* 4415 */         int maxNumPoints = 20;
/* 4416 */         int highestCount = Integer.MIN_VALUE;
/*      */         
/* 4418 */         for (int i = 0; i < 20; i++) {
/* 4419 */           if (this.perfMetricsHistCounts[i] > highestCount) {
/* 4420 */             highestCount = this.perfMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */         
/* 4424 */         if (highestCount == 0) {
/* 4425 */           highestCount = 1;
/*      */         }
/*      */         
/* 4428 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4430 */           if (i == 0) {
/* 4431 */             logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           } else {
/* 4433 */             logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */           
/*      */ 
/* 4437 */           logMessage.append("\t");
/*      */           
/* 4439 */           int numPointsToGraph = (int)(maxNumPoints * (this.perfMetricsHistCounts[i] / highestCount));
/*      */           
/* 4441 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4442 */             logMessage.append("*");
/*      */           }
/*      */           
/* 4445 */           if (this.longestQueryTimeMs < this.perfMetricsHistCounts[(i + 1)]) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/* 4450 */         if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 4451 */           logMessage.append("\n\tbetween ");
/* 4452 */           logMessage.append(this.perfMetricsHistBreakpoints[18]);
/* 4453 */           logMessage.append(" and ");
/* 4454 */           logMessage.append(this.perfMetricsHistBreakpoints[19]);
/* 4455 */           logMessage.append(" ms: \t");
/* 4456 */           logMessage.append(this.perfMetricsHistCounts[19]);
/*      */         }
/*      */       }
/*      */       
/* 4460 */       if (this.numTablesMetricsHistBreakpoints != null) {
/* 4461 */         logMessage.append("\n\n\tTable Join Histogram:\n");
/* 4462 */         int maxNumPoints = 20;
/* 4463 */         int highestCount = Integer.MIN_VALUE;
/*      */         
/* 4465 */         for (int i = 0; i < 20; i++) {
/* 4466 */           if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 4467 */             highestCount = this.numTablesMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */         
/* 4471 */         if (highestCount == 0) {
/* 4472 */           highestCount = 1;
/*      */         }
/*      */         
/* 4475 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4477 */           if (i == 0) {
/* 4478 */             logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*      */           } else {
/* 4480 */             logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */           
/*      */ 
/* 4484 */           logMessage.append("\t");
/*      */           
/* 4486 */           int numPointsToGraph = (int)(maxNumPoints * (this.numTablesMetricsHistCounts[i] / highestCount));
/*      */           
/* 4488 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4489 */             logMessage.append("*");
/*      */           }
/*      */           
/* 4492 */           if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[(i + 1)]) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/* 4497 */         if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 4498 */           logMessage.append("\n\tbetween ");
/* 4499 */           logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/* 4500 */           logMessage.append(" and ");
/* 4501 */           logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/* 4502 */           logMessage.append(" tables: ");
/* 4503 */           logMessage.append(this.numTablesMetricsHistCounts[19]);
/*      */         }
/*      */       }
/*      */       
/* 4507 */       this.log.logInfo(logMessage);
/*      */       
/* 4509 */       this.metricsLastReportedMs = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportMetricsIfNeeded()
/*      */   {
/* 4518 */     if ((getGatherPerformanceMetrics()) && 
/* 4519 */       (System.currentTimeMillis() - this.metricsLastReportedMs > getReportMetricsIntervalMillis())) {
/* 4520 */       reportMetrics();
/*      */     }
/*      */   }
/*      */   
/*      */   public void reportNumberOfTablesAccessed(int numTablesAccessed)
/*      */   {
/* 4526 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 4527 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*      */     }
/*      */     
/* 4530 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 4531 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*      */       
/* 4533 */       repartitionTablesAccessedHistogram();
/*      */     }
/*      */     
/* 4536 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void resetServerState()
/*      */     throws SQLException
/*      */   {
/* 4548 */     if ((!getParanoid()) && (this.io != null) && (versionMeetsMinimum(4, 0, 6))) {
/* 4549 */       changeUser(this.user, this.password);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/* 4563 */     synchronized (getConnectionMutex()) {
/* 4564 */       checkClosed();
/*      */       try
/*      */       {
/* 4567 */         if (this.connectionLifecycleInterceptors != null) {
/* 4568 */           IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */           {
/*      */             void forEach(Extension each) throws SQLException
/*      */             {
/* 4572 */               if (!((ConnectionLifecycleInterceptor)each).rollback()) {
/* 4573 */                 this.stopIterating = true;
/*      */               }
/*      */               
/*      */             }
/* 4577 */           };
/* 4578 */           iter.doForAll();
/*      */           
/* 4580 */           if (!iter.fullIteration()) {
/* 4581 */             jsr 115;return;
/*      */           }
/*      */         }
/*      */         
/* 4585 */         if ((this.autoCommit) && (!getRelaxAutoCommit())) {
/* 4586 */           throw SQLError.createSQLException("Can't call rollback when autocommit=true", "08003", getExceptionInterceptor());
/*      */         }
/* 4588 */         if (this.transactionsSupported) {
/*      */           try {
/* 4590 */             rollbackNoChecks();
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 4593 */             if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() == 1196)) {
/* 4594 */               return;
/*      */             }
/* 4596 */             throw sqlEx;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (SQLException sqlException) {
/* 4601 */         if ("08S01".equals(sqlException.getSQLState())) {
/* 4602 */           throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/* 4606 */         throw sqlException;
/*      */       } finally {
/* 4608 */         jsr 5; } localObject2 = returnAddress;this.needsPing = getReconnectAtTxEnd();ret;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void rollback(final Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/* 4618 */     synchronized (getConnectionMutex()) {
/* 4619 */       if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 4620 */         checkClosed();
/*      */         try
/*      */         {
/* 4623 */           if (this.connectionLifecycleInterceptors != null) {
/* 4624 */             IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */             {
/*      */               void forEach(Extension each) throws SQLException
/*      */               {
/* 4628 */                 if (!((ConnectionLifecycleInterceptor)each).rollback(savepoint)) {
/* 4629 */                   this.stopIterating = true;
/*      */                 }
/*      */                 
/*      */               }
/* 4633 */             };
/* 4634 */             iter.doForAll();
/*      */             
/* 4636 */             if (!iter.fullIteration()) {
/* 4637 */               jsr 240;return;
/*      */             }
/*      */           }
/*      */           
/* 4641 */           StringBuilder rollbackQuery = new StringBuilder("ROLLBACK TO SAVEPOINT ");
/* 4642 */           rollbackQuery.append('`');
/* 4643 */           rollbackQuery.append(savepoint.getSavepointName());
/* 4644 */           rollbackQuery.append('`');
/*      */           
/* 4646 */           java.sql.Statement stmt = null;
/*      */           try
/*      */           {
/* 4649 */             stmt = getMetadataSafeStatement();
/*      */             
/* 4651 */             stmt.executeUpdate(rollbackQuery.toString());
/*      */           } catch (SQLException sqlEx) {
/* 4653 */             int errno = sqlEx.getErrorCode();
/*      */             
/* 4655 */             if (errno == 1181) {
/* 4656 */               String msg = sqlEx.getMessage();
/*      */               
/* 4658 */               if (msg != null) {
/* 4659 */                 int indexOfError153 = msg.indexOf("153");
/*      */                 
/* 4661 */                 if (indexOfError153 != -1) {
/* 4662 */                   throw SQLError.createSQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", "S1009", errno, getExceptionInterceptor());
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 4669 */             if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196)) {
/* 4670 */               throw sqlEx;
/*      */             }
/*      */             
/* 4673 */             if ("08S01".equals(sqlEx.getSQLState())) {
/* 4674 */               throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007", getExceptionInterceptor());
/*      */             }
/*      */             
/*      */ 
/* 4678 */             throw sqlEx;
/*      */           } finally {
/* 4680 */             closeStatement(stmt);
/*      */           }
/*      */         } finally {
/* 4683 */           jsr 6; } localObject4 = returnAddress;this.needsPing = getReconnectAtTxEnd();ret;
/*      */       }
/*      */       else {
/* 4686 */         throw SQLError.createSQLFeatureNotSupportedException();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void rollbackNoChecks() throws SQLException {
/* 4692 */     if ((getUseLocalTransactionState()) && (versionMeetsMinimum(5, 0, 0)) && 
/* 4693 */       (!this.io.inTransactionOnServer())) {
/* 4694 */       return;
/*      */     }
/*      */     
/*      */ 
/* 4698 */     execSQL(null, "rollback", -1, null, 1003, 1007, false, this.database, null, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4706 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4708 */     return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getCatalog(), 1003, 1007);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4716 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4718 */     PreparedStatement pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getCatalog(), 1003, 1007);
/*      */     
/*      */ 
/* 4721 */     pStmt.setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */     
/* 4723 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4730 */     String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
/*      */     
/* 4732 */     return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, getCatalog(), resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4740 */     if ((getPedantic()) && 
/* 4741 */       (resultSetHoldability != 1)) {
/* 4742 */       throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4747 */     return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4755 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */     
/* 4757 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */     
/* 4759 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */   public java.sql.PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 4766 */     PreparedStatement pStmt = (PreparedStatement)serverPrepareStatement(sql);
/*      */     
/* 4768 */     pStmt.setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */     
/* 4770 */     return pStmt;
/*      */   }
/*      */   
/*      */   public boolean serverSupportsConvertFn() throws SQLException {
/* 4774 */     return versionMeetsMinimum(4, 0, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAutoCommit(final boolean autoCommitFlag)
/*      */     throws SQLException
/*      */   {
/* 4800 */     synchronized (getConnectionMutex()) {
/* 4801 */       checkClosed();
/*      */       
/* 4803 */       if (this.connectionLifecycleInterceptors != null) {
/* 4804 */         IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException
/*      */           {
/* 4808 */             if (!((ConnectionLifecycleInterceptor)each).setAutoCommit(autoCommitFlag)) {
/* 4809 */               this.stopIterating = true;
/*      */             }
/*      */             
/*      */           }
/* 4813 */         };
/* 4814 */         iter.doForAll();
/*      */         
/* 4816 */         if (!iter.fullIteration()) {
/* 4817 */           return;
/*      */         }
/*      */       }
/*      */       
/* 4821 */       if (getAutoReconnectForPools()) {
/* 4822 */         setHighAvailability(true);
/*      */       }
/*      */       try
/*      */       {
/* 4826 */         if (this.transactionsSupported)
/*      */         {
/* 4828 */           boolean needsSetOnServer = true;
/*      */           
/* 4830 */           if ((getUseLocalSessionState()) && (this.autoCommit == autoCommitFlag)) {
/* 4831 */             needsSetOnServer = false;
/* 4832 */           } else if (!getHighAvailability()) {
/* 4833 */             needsSetOnServer = getIO().isSetNeededForAutoCommitMode(autoCommitFlag);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 4839 */           this.autoCommit = autoCommitFlag;
/*      */           
/* 4841 */           if (needsSetOnServer) {
/* 4842 */             execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, 1003, 1007, false, this.database, null, false);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 4847 */           if ((!autoCommitFlag) && (!getRelaxAutoCommit())) {
/* 4848 */             throw SQLError.createSQLException("MySQL Versions Older than 3.23.15 do not support transactions", "08003", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 4852 */           this.autoCommit = autoCommitFlag;
/*      */         }
/*      */       } finally {
/* 4855 */         if (getAutoReconnectForPools()) {
/* 4856 */           setHighAvailability(false);
/*      */         }
/*      */       }
/*      */       
/* 4860 */       return;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCatalog(final String catalog)
/*      */     throws SQLException
/*      */   {
/* 4878 */     synchronized (getConnectionMutex()) {
/* 4879 */       checkClosed();
/*      */       
/* 4881 */       if (catalog == null) {
/* 4882 */         throw SQLError.createSQLException("Catalog can not be null", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 4885 */       if (this.connectionLifecycleInterceptors != null) {
/* 4886 */         IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException
/*      */           {
/* 4890 */             if (!((ConnectionLifecycleInterceptor)each).setCatalog(catalog)) {
/* 4891 */               this.stopIterating = true;
/*      */             }
/*      */             
/*      */           }
/* 4895 */         };
/* 4896 */         iter.doForAll();
/*      */         
/* 4898 */         if (!iter.fullIteration()) {
/* 4899 */           return;
/*      */         }
/*      */       }
/*      */       
/* 4903 */       if (getUseLocalSessionState()) {
/* 4904 */         if (this.lowerCaseTableNames) {
/* 4905 */           if (!this.database.equalsIgnoreCase(catalog)) {}
/*      */ 
/*      */ 
/*      */         }
/* 4909 */         else if (this.database.equals(catalog)) {
/* 4910 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 4915 */       String quotedId = this.dbmd.getIdentifierQuoteString();
/*      */       
/* 4917 */       if ((quotedId == null) || (quotedId.equals(" "))) {
/* 4918 */         quotedId = "";
/*      */       }
/*      */       
/* 4921 */       StringBuilder query = new StringBuilder("USE ");
/* 4922 */       query.append(StringUtils.quoteIdentifier(catalog, quotedId, getPedantic()));
/*      */       
/* 4924 */       execSQL(null, query.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */       
/* 4926 */       this.database = catalog;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setInGlobalTx(boolean flag)
/*      */   {
/* 4946 */     this.isInGlobalTx = flag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setReadInfoMsgEnabled(boolean flag)
/*      */   {
/* 4960 */     this.readInfoMsg = flag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setReadOnly(boolean readOnlyFlag)
/*      */     throws SQLException
/*      */   {
/* 4975 */     checkClosed();
/*      */     
/* 4977 */     setReadOnlyInternal(readOnlyFlag);
/*      */   }
/*      */   
/*      */   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException
/*      */   {
/* 4982 */     if ((getReadOnlyPropagatesToServer()) && (versionMeetsMinimum(5, 6, 5)) && (
/* 4983 */       (!getUseLocalSessionState()) || (readOnlyFlag != this.readOnly))) {
/* 4984 */       execSQL(null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, null, 1003, 1007, false, this.database, null, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4989 */     this.readOnly = readOnlyFlag;
/*      */   }
/*      */   
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/* 4996 */     MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
/*      */     
/* 4998 */     setSavepoint(savepoint);
/*      */     
/* 5000 */     return savepoint;
/*      */   }
/*      */   
/*      */   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException
/*      */   {
/* 5005 */     synchronized (getConnectionMutex()) {
/* 5006 */       if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 5007 */         checkClosed();
/*      */         
/* 5009 */         StringBuilder savePointQuery = new StringBuilder("SAVEPOINT ");
/* 5010 */         savePointQuery.append('`');
/* 5011 */         savePointQuery.append(savepoint.getSavepointName());
/* 5012 */         savePointQuery.append('`');
/*      */         
/* 5014 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 5017 */           stmt = getMetadataSafeStatement();
/*      */           
/* 5019 */           stmt.executeUpdate(savePointQuery.toString());
/*      */         } finally {
/* 5021 */           closeStatement(stmt);
/*      */         }
/*      */       } else {
/* 5024 */         throw SQLError.createSQLFeatureNotSupportedException();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/* 5033 */     synchronized (getConnectionMutex()) {
/* 5034 */       MysqlSavepoint savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
/*      */       
/* 5036 */       setSavepoint(savepoint);
/*      */       
/* 5038 */       return savepoint;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setSessionVariables() throws SQLException {
/* 5043 */     if ((versionMeetsMinimum(4, 0, 0)) && (getSessionVariables() != null)) {
/* 5044 */       List<String> variablesToSet = StringUtils.split(getSessionVariables(), ",", "\"'", "\"'", false);
/*      */       
/* 5046 */       int numVariablesToSet = variablesToSet.size();
/*      */       
/* 5048 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5051 */         stmt = getMetadataSafeStatement();
/*      */         
/* 5053 */         for (int i = 0; i < numVariablesToSet; i++) {
/* 5054 */           String variableValuePair = (String)variablesToSet.get(i);
/*      */           
/* 5056 */           if (variableValuePair.startsWith("@")) {
/* 5057 */             stmt.executeUpdate("SET " + variableValuePair);
/*      */           } else {
/* 5059 */             stmt.executeUpdate("SET SESSION " + variableValuePair);
/*      */           }
/*      */         }
/*      */       } finally {
/* 5063 */         if (stmt != null) {
/* 5064 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/* 5076 */     synchronized (getConnectionMutex()) {
/* 5077 */       checkClosed();
/*      */       
/* 5079 */       if (this.hasIsolationLevels) {
/* 5080 */         String sql = null;
/*      */         
/* 5082 */         boolean shouldSendSet = false;
/*      */         
/* 5084 */         if (getAlwaysSendSetIsolation()) {
/* 5085 */           shouldSendSet = true;
/*      */         }
/* 5087 */         else if (level != this.isolationLevel) {
/* 5088 */           shouldSendSet = true;
/*      */         }
/*      */         
/*      */ 
/* 5092 */         if (getUseLocalSessionState()) {
/* 5093 */           shouldSendSet = this.isolationLevel != level;
/*      */         }
/*      */         
/* 5096 */         if (shouldSendSet) {
/* 5097 */           switch (level) {
/*      */           case 0: 
/* 5099 */             throw SQLError.createSQLException("Transaction isolation level NONE not supported by MySQL", getExceptionInterceptor());
/*      */           
/*      */           case 2: 
/* 5102 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */             
/* 5104 */             break;
/*      */           
/*      */           case 1: 
/* 5107 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */             
/* 5109 */             break;
/*      */           
/*      */           case 4: 
/* 5112 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */             
/* 5114 */             break;
/*      */           
/*      */           case 8: 
/* 5117 */             sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */             
/* 5119 */             break;
/*      */           case 3: case 5: case 6: 
/*      */           case 7: default: 
/* 5122 */             throw SQLError.createSQLException("Unsupported transaction isolation level '" + level + "'", "S1C00", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */           
/* 5126 */           execSQL(null, sql, -1, null, 1003, 1007, false, this.database, null, false);
/*      */           
/* 5128 */           this.isolationLevel = level;
/*      */         }
/*      */       } else {
/* 5131 */         throw SQLError.createSQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", "S1C00", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTypeMap(Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 5147 */     synchronized (getConnectionMutex()) {
/* 5148 */       this.typeMap = map;
/*      */     }
/*      */   }
/*      */   
/*      */   private void setupServerForTruncationChecks() throws SQLException {
/* 5153 */     if ((getJdbcCompliantTruncation()) && 
/* 5154 */       (versionMeetsMinimum(5, 0, 2))) {
/* 5155 */       String currentSqlMode = (String)this.serverVariables.get("sql_mode");
/*      */       
/* 5157 */       boolean strictTransTablesIsSet = StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1;
/*      */       
/* 5159 */       if ((currentSqlMode == null) || (currentSqlMode.length() == 0) || (!strictTransTablesIsSet)) {
/* 5160 */         StringBuilder commandBuf = new StringBuilder("SET sql_mode='");
/*      */         
/* 5162 */         if ((currentSqlMode != null) && (currentSqlMode.length() > 0)) {
/* 5163 */           commandBuf.append(currentSqlMode);
/* 5164 */           commandBuf.append(",");
/*      */         }
/*      */         
/* 5167 */         commandBuf.append("STRICT_TRANS_TABLES'");
/*      */         
/* 5169 */         execSQL(null, commandBuf.toString(), -1, null, 1003, 1007, false, this.database, null, false);
/*      */         
/* 5171 */         setJdbcCompliantTruncation(false);
/* 5172 */       } else if (strictTransTablesIsSet)
/*      */       {
/* 5174 */         setJdbcCompliantTruncation(false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void shutdownServer()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5189 */       this.io.sendCommand(8, null, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 5191 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), "S1000", getExceptionInterceptor());
/*      */       
/*      */ 
/* 5194 */       sqlEx.initCause(ex);
/*      */       
/* 5196 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean supportsIsolationLevel() {
/* 5201 */     return this.hasIsolationLevels;
/*      */   }
/*      */   
/*      */   public boolean supportsQuotedIdentifiers() {
/* 5205 */     return this.hasQuotedIdentifiers;
/*      */   }
/*      */   
/*      */   public boolean supportsTransactions() {
/* 5209 */     return this.transactionsSupported;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unregisterStatement(Statement stmt)
/*      */   {
/* 5219 */     this.openStatements.remove(stmt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */     throws SQLException
/*      */   {
/* 5229 */     checkClosed();
/*      */     
/* 5231 */     return this.io.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet)
/*      */     throws SQLException
/*      */   {
/* 5277 */     if (cachedMetaData == null)
/*      */     {
/*      */ 
/* 5280 */       cachedMetaData = new CachedResultSetMetaData();
/*      */       
/*      */ 
/* 5283 */       resultSet.buildIndexMapping();
/* 5284 */       resultSet.initializeWithMetadata();
/*      */       
/* 5286 */       if ((resultSet instanceof UpdatableResultSet)) {
/* 5287 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */       }
/*      */       
/* 5290 */       resultSet.populateCachedMetaData(cachedMetaData);
/*      */       
/* 5292 */       this.resultSetMetadataCache.put(sql, cachedMetaData);
/*      */     } else {
/* 5294 */       resultSet.initializeFromCachedMetaData(cachedMetaData);
/* 5295 */       resultSet.initializeWithMetadata();
/*      */       
/* 5297 */       if ((resultSet instanceof UpdatableResultSet)) {
/* 5298 */         ((UpdatableResultSet)resultSet).checkUpdatability();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getStatementComment()
/*      */   {
/* 5311 */     return this.statementComment;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStatementComment(String comment)
/*      */   {
/* 5324 */     this.statementComment = comment;
/*      */   }
/*      */   
/*      */   public void reportQueryTime(long millisOrNanos) {
/* 5328 */     synchronized (getConnectionMutex()) {
/* 5329 */       this.queryTimeCount += 1L;
/* 5330 */       this.queryTimeSum += millisOrNanos;
/* 5331 */       this.queryTimeSumSquares += millisOrNanos * millisOrNanos;
/* 5332 */       this.queryTimeMean = ((this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isAbonormallyLongQuery(long millisOrNanos) {
/* 5337 */     synchronized (getConnectionMutex()) {
/* 5338 */       if (this.queryTimeCount < 15L) {
/* 5339 */         return false;
/*      */       }
/*      */       
/* 5342 */       double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
/*      */       
/* 5344 */       return millisOrNanos > this.queryTimeMean + 5.0D * stddev;
/*      */     }
/*      */   }
/*      */   
/*      */   public void initializeExtension(Extension ex) throws SQLException {
/* 5349 */     ex.init(this, this.props);
/*      */   }
/*      */   
/*      */   public void transactionBegun() throws SQLException {
/* 5353 */     synchronized (getConnectionMutex()) {
/* 5354 */       if (this.connectionLifecycleInterceptors != null) {
/* 5355 */         IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException
/*      */           {
/* 5359 */             ((ConnectionLifecycleInterceptor)each).transactionBegun();
/*      */           }
/*      */           
/* 5362 */         };
/* 5363 */         iter.doForAll();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void transactionCompleted() throws SQLException {
/* 5369 */     synchronized (getConnectionMutex()) {
/* 5370 */       if (this.connectionLifecycleInterceptors != null) {
/* 5371 */         IterateBlock<Extension> iter = new IterateBlock(this.connectionLifecycleInterceptors.iterator())
/*      */         {
/*      */           void forEach(Extension each) throws SQLException
/*      */           {
/* 5375 */             ((ConnectionLifecycleInterceptor)each).transactionCompleted();
/*      */           }
/*      */           
/* 5378 */         };
/* 5379 */         iter.doForAll();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean storesLowerCaseTableName() {
/* 5385 */     return this.storesLowerCaseTableName;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 5392 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   public boolean getRequiresEscapingEncoder() {
/* 5396 */     return this.requiresEscapingEncoder;
/*      */   }
/*      */   
/*      */   public boolean isServerLocal() throws SQLException {
/* 5400 */     synchronized (getConnectionMutex()) {
/* 5401 */       SocketFactory factory = getIO().socketFactory;
/*      */       
/* 5403 */       if ((factory instanceof SocketMetadata)) {
/* 5404 */         return ((SocketMetadata)factory).isLocallyConnected(this);
/*      */       }
/* 5406 */       getLog().logWarn(Messages.getString("Connection.NoMetadataOnSocketFactory"));
/* 5407 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSessionMaxRows(int max)
/*      */     throws SQLException
/*      */   {
/* 5429 */     synchronized (getConnectionMutex()) {
/* 5430 */       if (this.sessionMaxRows != max) {
/* 5431 */         this.sessionMaxRows = max;
/* 5432 */         execSQL(null, "SET SQL_SELECT_LIMIT=" + (this.sessionMaxRows == -1 ? "DEFAULT" : Integer.valueOf(this.sessionMaxRows)), -1, null, 1003, 1007, false, this.database, null, false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setSchema(String schema)
/*      */     throws SQLException
/*      */   {
/* 5441 */     synchronized (getConnectionMutex()) {
/* 5442 */       checkClosed();
/*      */     }
/*      */   }
/*      */   
/*      */   public String getSchema() throws SQLException
/*      */   {
/* 5448 */     synchronized (getConnectionMutex()) {
/* 5449 */       checkClosed();
/*      */       
/* 5451 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void abort(Executor executor)
/*      */     throws SQLException
/*      */   {
/* 5486 */     SecurityManager sec = System.getSecurityManager();
/*      */     
/* 5488 */     if (sec != null) {
/* 5489 */       sec.checkPermission(ABORT_PERM);
/*      */     }
/*      */     
/* 5492 */     if (executor == null) {
/* 5493 */       throw SQLError.createSQLException("Executor can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 5496 */     executor.execute(new Runnable()
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 5500 */           ConnectionImpl.this.abortInternal();
/*      */         } catch (SQLException e) {
/* 5502 */           throw new RuntimeException(e);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public void setNetworkTimeout(Executor executor, final int milliseconds) throws SQLException
/*      */   {
/* 5510 */     synchronized (getConnectionMutex()) {
/* 5511 */       SecurityManager sec = System.getSecurityManager();
/*      */       
/* 5513 */       if (sec != null) {
/* 5514 */         sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
/*      */       }
/*      */       
/* 5517 */       if (executor == null) {
/* 5518 */         throw SQLError.createSQLException("Executor can not be null", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 5521 */       checkClosed();
/* 5522 */       final MysqlIO mysqlIo = this.io;
/*      */       
/* 5524 */       executor.execute(new Runnable()
/*      */       {
/*      */         public void run() {
/*      */           try {
/* 5528 */             ConnectionImpl.this.setSocketTimeout(milliseconds);
/* 5529 */             mysqlIo.setSocketTimeout(milliseconds);
/*      */           } catch (SQLException e) {
/* 5531 */             throw new RuntimeException(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public int getNetworkTimeout() throws SQLException
/*      */   {
/* 5540 */     synchronized (getConnectionMutex()) {
/* 5541 */       checkClosed();
/* 5542 */       return getSocketTimeout();
/*      */     }
/*      */   }
/*      */   
/*      */   public ProfilerEventHandler getProfilerEventHandlerInstance() {
/* 5547 */     return this.eventSink;
/*      */   }
/*      */   
/*      */   public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {
/* 5551 */     this.eventSink = h;
/*      */   }
/*      */   
/*      */   protected ConnectionImpl() {}
/*      */   
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   /* Error */
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 36	com/mysql/jdbc/ConnectionImpl:getConnectionMutex	()Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 58	com/mysql/jdbc/ConnectionImpl:autoCommit	Z
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2661	-> byte code offset #0
/*      */     //   Java source line #2662	-> byte code offset #7
/*      */     //   Java source line #2663	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	ConnectionImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 36	com/mysql/jdbc/ConnectionImpl:getConnectionMutex	()Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 66	com/mysql/jdbc/ConnectionImpl:database	Ljava/lang/String;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2690	-> byte code offset #0
/*      */     //   Java source line #2691	-> byte code offset #7
/*      */     //   Java source line #2692	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	ConnectionImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public String getCharacterSetMetadata()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 36	com/mysql/jdbc/ConnectionImpl:getConnectionMutex	()Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 59	com/mysql/jdbc/ConnectionImpl:characterSetMetadata	Ljava/lang/String;
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: areturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2699	-> byte code offset #0
/*      */     //   Java source line #2700	-> byte code offset #7
/*      */     //   Java source line #2701	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	ConnectionImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isReadOnly(boolean useSessionStatus)
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: iload_1
/*      */     //   1: ifeq +177 -> 178
/*      */     //   4: aload_0
/*      */     //   5: getfield 76	com/mysql/jdbc/ConnectionImpl:isClosed	Z
/*      */     //   8: ifne +170 -> 178
/*      */     //   11: aload_0
/*      */     //   12: iconst_5
/*      */     //   13: bipush 6
/*      */     //   15: iconst_5
/*      */     //   16: invokevirtual 221	com/mysql/jdbc/ConnectionImpl:versionMeetsMinimum	(III)Z
/*      */     //   19: ifeq +159 -> 178
/*      */     //   22: aload_0
/*      */     //   23: invokevirtual 593	com/mysql/jdbc/ConnectionImpl:getUseLocalSessionState	()Z
/*      */     //   26: ifne +152 -> 178
/*      */     //   29: aload_0
/*      */     //   30: invokevirtual 679	com/mysql/jdbc/ConnectionImpl:getReadOnlyPropagatesToServer	()Z
/*      */     //   33: ifeq +145 -> 178
/*      */     //   36: aconst_null
/*      */     //   37: astore_2
/*      */     //   38: aconst_null
/*      */     //   39: astore_3
/*      */     //   40: aload_0
/*      */     //   41: invokevirtual 225	com/mysql/jdbc/ConnectionImpl:getMetadataSafeStatement	()Ljava/sql/Statement;
/*      */     //   44: astore_2
/*      */     //   45: aload_2
/*      */     //   46: ldc_w 680
/*      */     //   49: invokeinterface 227 2 0
/*      */     //   54: astore_3
/*      */     //   55: aload_3
/*      */     //   56: invokeinterface 229 1 0
/*      */     //   61: ifeq +26 -> 87
/*      */     //   64: aload_3
/*      */     //   65: iconst_1
/*      */     //   66: invokeinterface 681 2 0
/*      */     //   71: ifeq +7 -> 78
/*      */     //   74: iconst_1
/*      */     //   75: goto +4 -> 79
/*      */     //   78: iconst_0
/*      */     //   79: istore 4
/*      */     //   81: jsr +59 -> 140
/*      */     //   84: iload 4
/*      */     //   86: ireturn
/*      */     //   87: goto +39 -> 126
/*      */     //   90: astore 4
/*      */     //   92: aload 4
/*      */     //   94: invokevirtual 19	java/sql/SQLException:getErrorCode	()I
/*      */     //   97: sipush 1820
/*      */     //   100: if_icmpne +10 -> 110
/*      */     //   103: aload_0
/*      */     //   104: invokevirtual 234	com/mysql/jdbc/ConnectionImpl:getDisconnectOnExpiredPasswords	()Z
/*      */     //   107: ifeq +19 -> 126
/*      */     //   110: ldc_w 682
/*      */     //   113: ldc_w 385
/*      */     //   116: aload 4
/*      */     //   118: aload_0
/*      */     //   119: invokevirtual 139	com/mysql/jdbc/ConnectionImpl:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   122: invokestatic 438	com/mysql/jdbc/SQLError:createSQLException	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Lcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   125: athrow
/*      */     //   126: jsr +14 -> 140
/*      */     //   129: goto +49 -> 178
/*      */     //   132: astore 5
/*      */     //   134: jsr +6 -> 140
/*      */     //   137: aload 5
/*      */     //   139: athrow
/*      */     //   140: astore 6
/*      */     //   142: aload_3
/*      */     //   143: ifnull +16 -> 159
/*      */     //   146: aload_3
/*      */     //   147: invokeinterface 256 1 0
/*      */     //   152: goto +5 -> 157
/*      */     //   155: astore 7
/*      */     //   157: aconst_null
/*      */     //   158: astore_3
/*      */     //   159: aload_2
/*      */     //   160: ifnull +16 -> 176
/*      */     //   163: aload_2
/*      */     //   164: invokeinterface 257 1 0
/*      */     //   169: goto +5 -> 174
/*      */     //   172: astore 7
/*      */     //   174: aconst_null
/*      */     //   175: astore_2
/*      */     //   176: ret 6
/*      */     //   178: aload_0
/*      */     //   179: getfield 108	com/mysql/jdbc/ConnectionImpl:readOnly	Z
/*      */     //   182: ireturn
/*      */     // Line number table:
/*      */     //   Java source line #3589	-> byte code offset #0
/*      */     //   Java source line #3590	-> byte code offset #36
/*      */     //   Java source line #3591	-> byte code offset #38
/*      */     //   Java source line #3595	-> byte code offset #40
/*      */     //   Java source line #3597	-> byte code offset #45
/*      */     //   Java source line #3598	-> byte code offset #55
/*      */     //   Java source line #3599	-> byte code offset #64
/*      */     //   Java source line #3606	-> byte code offset #87
/*      */     //   Java source line #3601	-> byte code offset #90
/*      */     //   Java source line #3602	-> byte code offset #92
/*      */     //   Java source line #3603	-> byte code offset #110
/*      */     //   Java source line #3608	-> byte code offset #126
/*      */     //   Java source line #3628	-> byte code offset #129
/*      */     //   Java source line #3609	-> byte code offset #132
/*      */     //   Java source line #3611	-> byte code offset #146
/*      */     //   Java source line #3614	-> byte code offset #152
/*      */     //   Java source line #3612	-> byte code offset #155
/*      */     //   Java source line #3616	-> byte code offset #157
/*      */     //   Java source line #3619	-> byte code offset #159
/*      */     //   Java source line #3621	-> byte code offset #163
/*      */     //   Java source line #3624	-> byte code offset #169
/*      */     //   Java source line #3622	-> byte code offset #172
/*      */     //   Java source line #3626	-> byte code offset #174
/*      */     //   Java source line #3631	-> byte code offset #178
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	183	0	this	ConnectionImpl
/*      */     //   0	183	1	useSessionStatus	boolean
/*      */     //   37	139	2	stmt	java.sql.Statement
/*      */     //   39	120	3	rs	ResultSet
/*      */     //   79	6	4	bool	boolean
/*      */     //   90	27	4	ex1	SQLException
/*      */     //   132	6	5	localObject1	Object
/*      */     //   140	1	6	localObject2	Object
/*      */     //   155	3	7	ex	Exception
/*      */     //   172	3	7	ex	Exception
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   40	81	90	java/sql/SQLException
/*      */     //   40	84	132	finally
/*      */     //   87	129	132	finally
/*      */     //   132	137	132	finally
/*      */     //   146	152	155	java/lang/Exception
/*      */     //   163	169	172	java/lang/Exception
/*      */   }
/*      */   
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   public void setFailedOver(boolean flag) {}
/*      */   
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   @Deprecated
/*      */   public void setPreferSlaveDuringFailover(boolean flag) {}
/*      */   
/*      */   /* Error */
/*      */   public boolean useAnsiQuotedIdentifiers()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 36	com/mysql/jdbc/ConnectionImpl:getConnectionMutex	()Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 114	com/mysql/jdbc/ConnectionImpl:useAnsiQuotes	Z
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #5223	-> byte code offset #0
/*      */     //   Java source line #5224	-> byte code offset #7
/*      */     //   Java source line #5225	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	ConnectionImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public CachedResultSetMetaData getCachedMetaData(String sql)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 621	com/mysql/jdbc/ConnectionImpl:resultSetMetadataCache	Lcom/mysql/jdbc/util/LRUCache;
/*      */     //   4: ifnull +29 -> 33
/*      */     //   7: aload_0
/*      */     //   8: getfield 621	com/mysql/jdbc/ConnectionImpl:resultSetMetadataCache	Lcom/mysql/jdbc/util/LRUCache;
/*      */     //   11: dup
/*      */     //   12: astore_2
/*      */     //   13: monitorenter
/*      */     //   14: aload_0
/*      */     //   15: getfield 621	com/mysql/jdbc/ConnectionImpl:resultSetMetadataCache	Lcom/mysql/jdbc/util/LRUCache;
/*      */     //   18: aload_1
/*      */     //   19: invokevirtual 261	com/mysql/jdbc/util/LRUCache:get	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   22: checkcast 875	com/mysql/jdbc/CachedResultSetMetaData
/*      */     //   25: aload_2
/*      */     //   26: monitorexit
/*      */     //   27: areturn
/*      */     //   28: astore_3
/*      */     //   29: aload_2
/*      */     //   30: monitorexit
/*      */     //   31: aload_3
/*      */     //   32: athrow
/*      */     //   33: aconst_null
/*      */     //   34: areturn
/*      */     // Line number table:
/*      */     //   Java source line #5249	-> byte code offset #0
/*      */     //   Java source line #5250	-> byte code offset #7
/*      */     //   Java source line #5251	-> byte code offset #14
/*      */     //   Java source line #5252	-> byte code offset #28
/*      */     //   Java source line #5255	-> byte code offset #33
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	35	0	this	ConnectionImpl
/*      */     //   0	35	1	sql	String
/*      */     //   12	18	2	Ljava/lang/Object;	Object
/*      */     //   28	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   14	27	28	finally
/*      */     //   28	31	28	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getSessionMaxRows()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 36	com/mysql/jdbc/ConnectionImpl:getConnectionMutex	()Ljava/lang/Object;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 87	com/mysql/jdbc/ConnectionImpl:sessionMaxRows	I
/*      */     //   11: aload_1
/*      */     //   12: monitorexit
/*      */     //   13: ireturn
/*      */     //   14: astore_2
/*      */     //   15: aload_1
/*      */     //   16: monitorexit
/*      */     //   17: aload_2
/*      */     //   18: athrow
/*      */     // Line number table:
/*      */     //   Java source line #5415	-> byte code offset #0
/*      */     //   Java source line #5416	-> byte code offset #7
/*      */     //   Java source line #5417	-> byte code offset #14
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	19	0	this	ConnectionImpl
/*      */     //   5	11	1	Ljava/lang/Object;	Object
/*      */     //   14	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	13	14	finally
/*      */     //   14	17	14	finally
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */