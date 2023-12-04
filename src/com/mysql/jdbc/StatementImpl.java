/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.InputStream;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*      */ public class StatementImpl
/*      */   implements Statement
/*      */ {
/*      */   protected static final String PING_MARKER = "/* ping */";
/*   62 */   protected static final String[] ON_DUPLICATE_KEY_UPDATE_CLAUSE = { "ON", "DUPLICATE", "KEY", "UPDATE" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   class CancelTask
/*      */     extends TimerTask
/*      */   {
/*   71 */     long connectionId = 0L;
/*   72 */     SQLException caughtWhileCancelling = null;
/*      */     StatementImpl toCancel;
/*   74 */     Properties origConnProps = null;
/*   75 */     String origConnURL = "";
/*      */     
/*      */     CancelTask(StatementImpl cancellee) throws SQLException {
/*   78 */       this.connectionId = cancellee.connectionId;
/*   79 */       this.toCancel = cancellee;
/*   80 */       this.origConnProps = new Properties();
/*      */       
/*   82 */       Properties props = StatementImpl.this.connection.getProperties();
/*      */       
/*   84 */       Enumeration<?> keys = props.propertyNames();
/*      */       
/*   86 */       while (keys.hasMoreElements()) {
/*   87 */         String key = keys.nextElement().toString();
/*   88 */         this.origConnProps.setProperty(key, props.getProperty(key));
/*      */       }
/*      */       
/*   91 */       this.origConnURL = StatementImpl.this.connection.getURL();
/*      */     }
/*      */     
/*      */ 
/*      */     public void run()
/*      */     {
/*   97 */       Thread cancelThread = new Thread()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*  102 */           Connection cancelConn = null;
/*  103 */           java.sql.Statement cancelStmt = null;
/*      */           try
/*      */           {
/*  106 */             if (StatementImpl.this.connection.getQueryTimeoutKillsConnection()) {
/*  107 */               CancelTask.this.toCancel.wasCancelled = true;
/*  108 */               CancelTask.this.toCancel.wasCancelledByTimeout = true;
/*  109 */               StatementImpl.this.connection.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
/*      */             }
/*      */             else {
/*  112 */               synchronized (StatementImpl.this.cancelTimeoutMutex) {
/*  113 */                 if (CancelTask.this.origConnURL.equals(StatementImpl.this.connection.getURL()))
/*      */                 {
/*  115 */                   cancelConn = StatementImpl.this.connection.duplicate();
/*  116 */                   cancelStmt = cancelConn.createStatement();
/*  117 */                   cancelStmt.execute("KILL QUERY " + CancelTask.this.connectionId);
/*      */                 } else {
/*      */                   try {
/*  120 */                     cancelConn = (Connection)DriverManager.getConnection(CancelTask.this.origConnURL, CancelTask.this.origConnProps);
/*  121 */                     cancelStmt = cancelConn.createStatement();
/*  122 */                     cancelStmt.execute("KILL QUERY " + CancelTask.this.connectionId);
/*      */                   }
/*      */                   catch (NullPointerException npe) {}
/*      */                 }
/*      */                 
/*  127 */                 CancelTask.this.toCancel.wasCancelled = true;
/*  128 */                 CancelTask.this.toCancel.wasCancelledByTimeout = true;
/*      */               }
/*      */             }
/*      */           } catch (SQLException sqlEx) {
/*  132 */             CancelTask.this.caughtWhileCancelling = sqlEx;
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (NullPointerException npe) {}finally
/*      */           {
/*      */ 
/*  139 */             if (cancelStmt != null) {
/*      */               try {
/*  141 */                 cancelStmt.close();
/*      */               } catch (SQLException sqlEx) {
/*  143 */                 throw new RuntimeException(sqlEx.toString());
/*      */               }
/*      */             }
/*      */             
/*  147 */             if (cancelConn != null) {
/*      */               try {
/*  149 */                 cancelConn.close();
/*      */               } catch (SQLException sqlEx) {
/*  151 */                 throw new RuntimeException(sqlEx.toString());
/*      */               }
/*      */             }
/*      */             
/*  155 */             CancelTask.this.toCancel = null;
/*  156 */             CancelTask.this.origConnProps = null;
/*  157 */             CancelTask.this.origConnURL = null;
/*      */           }
/*      */           
/*      */         }
/*  161 */       };
/*  162 */       cancelThread.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  171 */   protected Object cancelTimeoutMutex = new Object();
/*      */   
/*      */ 
/*  174 */   static int statementCounter = 1;
/*      */   
/*      */   public static final byte USES_VARIABLES_FALSE = 0;
/*      */   
/*      */   public static final byte USES_VARIABLES_TRUE = 1;
/*      */   
/*      */   public static final byte USES_VARIABLES_UNKNOWN = -1;
/*      */   
/*  182 */   protected boolean wasCancelled = false;
/*  183 */   protected boolean wasCancelledByTimeout = false;
/*      */   
/*      */ 
/*      */   protected List<Object> batchedArgs;
/*      */   
/*      */ 
/*  189 */   protected SingleByteCharsetConverter charConverter = null;
/*      */   
/*      */ 
/*  192 */   protected String charEncoding = null;
/*      */   
/*      */ 
/*  195 */   protected volatile MySQLConnection connection = null;
/*      */   
/*  197 */   protected long connectionId = 0L;
/*      */   
/*      */ 
/*  200 */   protected String currentCatalog = null;
/*      */   
/*      */ 
/*  203 */   protected boolean doEscapeProcessing = true;
/*      */   
/*      */ 
/*  206 */   protected ProfilerEventHandler eventSink = null;
/*      */   
/*      */ 
/*  209 */   private int fetchSize = 0;
/*      */   
/*      */ 
/*  212 */   protected boolean isClosed = false;
/*      */   
/*      */ 
/*  215 */   protected long lastInsertId = -1L;
/*      */   
/*      */ 
/*  218 */   protected int maxFieldSize = MysqlIO.getMaxBuf();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  224 */   protected int maxRows = -1;
/*      */   
/*      */ 
/*  227 */   protected Set<ResultSetInternalMethods> openResults = new HashSet();
/*      */   
/*      */ 
/*  230 */   protected boolean pedantic = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String pointOfOrigin;
/*      */   
/*      */ 
/*      */ 
/*  239 */   protected boolean profileSQL = false;
/*      */   
/*      */ 
/*  242 */   protected ResultSetInternalMethods results = null;
/*      */   
/*  244 */   protected ResultSetInternalMethods generatedKeysResults = null;
/*      */   
/*      */ 
/*  247 */   protected int resultSetConcurrency = 0;
/*      */   
/*      */ 
/*  250 */   protected int resultSetType = 0;
/*      */   
/*      */ 
/*      */   protected int statementId;
/*      */   
/*      */ 
/*  256 */   protected int timeoutInMillis = 0;
/*      */   
/*      */ 
/*  259 */   protected long updateCount = -1L;
/*      */   
/*      */ 
/*  262 */   protected boolean useUsageAdvisor = false;
/*      */   
/*      */ 
/*  265 */   protected SQLWarning warningChain = null;
/*      */   
/*      */ 
/*  268 */   protected boolean clearWarningsCalled = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  274 */   protected boolean holdResultsOpenOverClose = false;
/*      */   
/*  276 */   protected ArrayList<ResultSetRow> batchedGeneratedKeys = null;
/*      */   
/*  278 */   protected boolean retrieveGeneratedKeys = false;
/*      */   
/*  280 */   protected boolean continueBatchOnError = false;
/*      */   
/*  282 */   protected PingTarget pingTarget = null;
/*      */   
/*      */ 
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   
/*      */   protected boolean sendFractionalSeconds;
/*      */   
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   
/*  291 */   protected boolean lastQueryIsOnDupKeyUpdate = false;
/*      */   
/*      */ 
/*  294 */   protected final AtomicBoolean statementExecuting = new AtomicBoolean(false);
/*      */   
/*      */ 
/*  297 */   private boolean isImplicitlyClosingResults = false;
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
/*      */   public StatementImpl(MySQLConnection c, String catalog)
/*      */     throws SQLException
/*      */   {
/*  311 */     if ((c == null) || (c.isClosed())) {
/*  312 */       throw SQLError.createSQLException(Messages.getString("Statement.0"), "08003", null);
/*      */     }
/*      */     
/*  315 */     this.connection = c;
/*  316 */     this.connectionId = this.connection.getId();
/*  317 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */     
/*  319 */     this.currentCatalog = catalog;
/*  320 */     this.pedantic = this.connection.getPedantic();
/*  321 */     this.continueBatchOnError = this.connection.getContinueBatchOnError();
/*  322 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*  323 */     this.sendFractionalSeconds = this.connection.getSendFractionalSeconds();
/*  324 */     this.doEscapeProcessing = this.connection.getEnableEscapeProcessing();
/*      */     
/*  326 */     if (!this.connection.getDontTrackOpenResources()) {
/*  327 */       this.connection.registerStatement(this);
/*      */     }
/*      */     
/*  330 */     this.maxFieldSize = this.connection.getMaxAllowedPacket();
/*      */     
/*  332 */     int defaultFetchSize = this.connection.getDefaultFetchSize();
/*  333 */     if (defaultFetchSize != 0) {
/*  334 */       setFetchSize(defaultFetchSize);
/*      */     }
/*      */     
/*  337 */     if (this.connection.getUseUnicode()) {
/*  338 */       this.charEncoding = this.connection.getEncoding();
/*  339 */       this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */     }
/*      */     
/*  342 */     boolean profiling = (this.connection.getProfileSql()) || (this.connection.getUseUsageAdvisor()) || (this.connection.getLogSlowQueries());
/*  343 */     if ((this.connection.getAutoGenerateTestcaseScript()) || (profiling)) {
/*  344 */       this.statementId = (statementCounter++);
/*      */     }
/*  346 */     if (profiling) {
/*  347 */       this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*  348 */       this.profileSQL = this.connection.getProfileSql();
/*  349 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  350 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */     }
/*      */     
/*  353 */     int maxRowsConn = this.connection.getMaxRows();
/*  354 */     if (maxRowsConn != -1) {
/*  355 */       setMaxRows(maxRowsConn);
/*      */     }
/*      */     
/*  358 */     this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
/*      */     
/*  360 */     this.version5013OrNewer = this.connection.versionMeetsMinimum(5, 0, 13);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/*  369 */     synchronized (checkClosed().getConnectionMutex()) {
/*  370 */       if (this.batchedArgs == null) {
/*  371 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */       
/*  374 */       if (sql != null) {
/*  375 */         this.batchedArgs.add(sql);
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
/*      */   public List<Object> getBatchedArgs()
/*      */   {
/*  389 */     return this.batchedArgs == null ? null : Collections.unmodifiableList(this.batchedArgs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*  398 */     if (!this.statementExecuting.get()) {
/*  399 */       return;
/*      */     }
/*      */     
/*  402 */     if ((!this.isClosed) && (this.connection != null) && (this.connection.versionMeetsMinimum(5, 0, 0))) {
/*  403 */       Connection cancelConn = null;
/*  404 */       java.sql.Statement cancelStmt = null;
/*      */       try
/*      */       {
/*  407 */         cancelConn = this.connection.duplicate();
/*  408 */         cancelStmt = cancelConn.createStatement();
/*  409 */         cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
/*  410 */         this.wasCancelled = true;
/*      */       } finally {
/*  412 */         if (cancelStmt != null) {
/*  413 */           cancelStmt.close();
/*      */         }
/*      */         
/*  416 */         if (cancelConn != null) {
/*  417 */           cancelConn.close();
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
/*      */   protected MySQLConnection checkClosed()
/*      */     throws SQLException
/*      */   {
/*  433 */     MySQLConnection c = this.connection;
/*      */     
/*  435 */     if (c == null) {
/*  436 */       throw SQLError.createSQLException(Messages.getString("Statement.49"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  439 */     return c;
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
/*      */   protected void checkForDml(String sql, char firstStatementChar)
/*      */     throws SQLException
/*      */   {
/*  455 */     if ((firstStatementChar == 'I') || (firstStatementChar == 'U') || (firstStatementChar == 'D') || (firstStatementChar == 'A') || (firstStatementChar == 'C') || (firstStatementChar == 'T') || (firstStatementChar == 'R'))
/*      */     {
/*  457 */       String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
/*      */       
/*  459 */       if ((StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "TRUNCATE")) || (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "RENAME")))
/*      */       {
/*      */ 
/*      */ 
/*  463 */         throw SQLError.createSQLException(Messages.getString("Statement.57"), "S1009", getExceptionInterceptor());
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
/*      */   protected void checkNullOrEmptyQuery(String sql)
/*      */     throws SQLException
/*      */   {
/*  478 */     if (sql == null) {
/*  479 */       throw SQLError.createSQLException(Messages.getString("Statement.59"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  482 */     if (sql.length() == 0) {
/*  483 */       throw SQLError.createSQLException(Messages.getString("Statement.61"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearBatch()
/*      */     throws SQLException
/*      */   {
/*  496 */     synchronized (checkClosed().getConnectionMutex()) {
/*  497 */       if (this.batchedArgs != null) {
/*  498 */         this.batchedArgs.clear();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  511 */     synchronized (checkClosed().getConnectionMutex()) {
/*  512 */       this.clearWarningsCalled = true;
/*  513 */       this.warningChain = null;
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
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  532 */     realClose(true, true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closeAllOpenResults()
/*      */     throws SQLException
/*      */   {
/*  539 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/*  541 */     if (locallyScopedConn == null) {
/*  542 */       return;
/*      */     }
/*      */     
/*  545 */     synchronized (locallyScopedConn.getConnectionMutex()) {
/*  546 */       if (this.openResults != null) {
/*  547 */         for (ResultSetInternalMethods element : this.openResults) {
/*      */           try {
/*  549 */             element.realClose(false);
/*      */           } catch (SQLException sqlEx) {
/*  551 */             AssertionFailedException.shouldNotHappen(sqlEx);
/*      */           }
/*      */         }
/*      */         
/*  555 */         this.openResults.clear();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void implicitlyCloseAllOpenResults()
/*      */     throws SQLException
/*      */   {
/*  564 */     this.isImplicitlyClosingResults = true;
/*      */     try {
/*  566 */       if ((!this.connection.getHoldResultsOpenOverStatementClose()) && (!this.connection.getDontTrackOpenResources()) && (!this.holdResultsOpenOverClose)) {
/*  567 */         if (this.results != null) {
/*  568 */           this.results.realClose(false);
/*      */         }
/*  570 */         if (this.generatedKeysResults != null) {
/*  571 */           this.generatedKeysResults.realClose(false);
/*      */         }
/*  573 */         closeAllOpenResults();
/*      */       }
/*      */     } finally {
/*  576 */       this.isImplicitlyClosingResults = false;
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeOpenResultSet(ResultSetInternalMethods rs) {
/*      */     try {
/*  582 */       synchronized (checkClosed().getConnectionMutex()) {
/*  583 */         if (this.openResults != null) {
/*  584 */           this.openResults.remove(rs);
/*      */         }
/*      */         
/*  587 */         boolean hasMoreResults = rs.getNextResultSet() != null;
/*      */         
/*      */ 
/*  590 */         if ((this.results == rs) && (!hasMoreResults)) {
/*  591 */           this.results = null;
/*      */         }
/*  593 */         if (this.generatedKeysResults == rs) {
/*  594 */           this.generatedKeysResults = null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  600 */         if ((!this.isImplicitlyClosingResults) && (!hasMoreResults)) {
/*  601 */           checkAndPerformCloseOnCompletionAction();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {}
/*      */   }
/*      */   
/*      */   public int getOpenResultSetCount()
/*      */   {
/*      */     try {
/*  611 */       synchronized (checkClosed().getConnectionMutex()) {
/*  612 */         if (this.openResults != null) {
/*  613 */           return this.openResults.size();
/*      */         }
/*      */         
/*  616 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  621 */       return 0;
/*      */     }
/*      */     catch (SQLException e) {}
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkAndPerformCloseOnCompletionAction()
/*      */   {
/*      */     try
/*      */     {
/*  631 */       synchronized (checkClosed().getConnectionMutex()) {
/*  632 */         if ((isCloseOnCompletion()) && (!this.connection.getDontTrackOpenResources()) && (getOpenResultSetCount() == 0) && ((this.results == null) || (!this.results.reallyResult()) || (this.results.isClosed())) && ((this.generatedKeysResults == null) || (!this.generatedKeysResults.reallyResult()) || (this.generatedKeysResults.isClosed())))
/*      */         {
/*      */ 
/*  635 */           realClose(false, false);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {}
/*      */   }
/*      */   
/*      */ 
/*      */   private ResultSetInternalMethods createResultSetUsingServerFetch(String sql)
/*      */     throws SQLException
/*      */   {
/*  646 */     synchronized (checkClosed().getConnectionMutex()) {
/*  647 */       java.sql.PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
/*      */       
/*  649 */       pStmt.setFetchSize(this.fetchSize);
/*      */       
/*  651 */       if (this.maxRows > -1) {
/*  652 */         pStmt.setMaxRows(this.maxRows);
/*      */       }
/*      */       
/*  655 */       statementBegins();
/*      */       
/*  657 */       pStmt.execute();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  662 */       ResultSetInternalMethods rs = ((StatementImpl)pStmt).getResultSetInternal();
/*      */       
/*  664 */       rs.setStatementUsedForFetchingRows((PreparedStatement)pStmt);
/*      */       
/*  666 */       this.results = rs;
/*      */       
/*  668 */       return rs;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean createStreamingResultSet()
/*      */   {
/*  680 */     return (this.resultSetType == 1003) && (this.resultSetConcurrency == 1007) && (this.fetchSize == Integer.MIN_VALUE);
/*      */   }
/*      */   
/*      */ 
/*  684 */   private int originalResultSetType = 0;
/*  685 */   private int originalFetchSize = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enableStreamingResults()
/*      */     throws SQLException
/*      */   {
/*  693 */     synchronized (checkClosed().getConnectionMutex()) {
/*  694 */       this.originalResultSetType = this.resultSetType;
/*  695 */       this.originalFetchSize = this.fetchSize;
/*      */       
/*  697 */       setFetchSize(Integer.MIN_VALUE);
/*  698 */       setResultSetType(1003);
/*      */     }
/*      */   }
/*      */   
/*      */   public void disableStreamingResults() throws SQLException {
/*  703 */     synchronized (checkClosed().getConnectionMutex()) {
/*  704 */       if ((this.fetchSize == Integer.MIN_VALUE) && (this.resultSetType == 1003)) {
/*  705 */         setFetchSize(this.originalFetchSize);
/*  706 */         setResultSetType(this.originalResultSetType);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setupStreamingTimeout(MySQLConnection con)
/*      */     throws SQLException
/*      */   {
/*  719 */     if ((createStreamingResultSet()) && (con.getNetTimeoutForStreamingResults() > 0)) {
/*  720 */       executeSimpleNonQuery(con, "SET net_write_timeout=" + con.getNetTimeoutForStreamingResults());
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
/*      */   public boolean execute(String sql)
/*      */     throws SQLException
/*      */   {
/*  739 */     return executeInternal(sql, false);
/*      */   }
/*      */   
/*      */   private boolean executeInternal(String sql, boolean returnGeneratedKeys) throws SQLException {
/*  743 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */     char firstNonWsChar;
/*  745 */     boolean maybeSelect; boolean readInfoMsgState; synchronized (locallyScopedConn.getConnectionMutex()) {
/*  746 */       checkClosed();
/*      */       
/*  748 */       checkNullOrEmptyQuery(sql);
/*      */       
/*  750 */       resetCancelledState();
/*      */       
/*  752 */       firstNonWsChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*  753 */       maybeSelect = firstNonWsChar == 'S';
/*      */       
/*  755 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*      */       
/*  757 */       this.lastQueryIsOnDupKeyUpdate = ((returnGeneratedKeys) && (firstNonWsChar == 'I') && (containsOnDuplicateKeyInString(sql)));
/*      */       
/*  759 */       if ((!maybeSelect) && (locallyScopedConn.isReadOnly())) {
/*  760 */         throw SQLError.createSQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  764 */       readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/*  765 */       if ((returnGeneratedKeys) && (firstNonWsChar == 'R'))
/*      */       {
/*      */ 
/*  768 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */     }
/*      */     try {
/*  772 */       setupStreamingTimeout(locallyScopedConn);
/*      */       Object escapedSqlResult;
/*  774 */       if (this.doEscapeProcessing) {
/*  775 */         escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), locallyScopedConn);
/*      */         
/*  777 */         if ((escapedSqlResult instanceof String)) {
/*  778 */           sql = (String)escapedSqlResult;
/*      */         } else {
/*  780 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */       
/*  784 */       implicitlyCloseAllOpenResults();
/*      */       
/*  786 */       if ((sql.charAt(0) == '/') && 
/*  787 */         (sql.startsWith("/* ping */"))) {
/*  788 */         doPingInstead();
/*      */         
/*  790 */         escapedSqlResult = 1;jsr 493;return (boolean)escapedSqlResult;
/*      */       }
/*      */       
/*      */ 
/*  794 */       CachedResultSetMetaData cachedMetaData = null;
/*      */       
/*  796 */       ResultSetInternalMethods rs = null;
/*      */       
/*  798 */       this.batchedGeneratedKeys = null;
/*      */       
/*  800 */       if (useServerFetch()) {
/*  801 */         rs = createResultSetUsingServerFetch(sql);
/*      */       } else {
/*  803 */         timeoutTask = null;
/*      */         
/*  805 */         String oldCatalog = null;
/*      */         try
/*      */         {
/*  808 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/*  809 */             timeoutTask = new CancelTask(this);
/*  810 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */           }
/*      */           
/*  813 */           if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/*  814 */             oldCatalog = locallyScopedConn.getCatalog();
/*  815 */             locallyScopedConn.setCatalog(this.currentCatalog);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  822 */           Field[] cachedFields = null;
/*      */           
/*  824 */           if (locallyScopedConn.getCacheResultSetMetadata()) {
/*  825 */             cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */             
/*  827 */             if (cachedMetaData != null) {
/*  828 */               cachedFields = cachedMetaData.fields;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  835 */           locallyScopedConn.setSessionMaxRows(maybeSelect ? this.maxRows : -1);
/*      */           
/*  837 */           statementBegins();
/*      */           
/*  839 */           rs = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), this.currentCatalog, cachedFields);
/*      */           
/*      */ 
/*  842 */           if (timeoutTask != null) {
/*  843 */             if (timeoutTask.caughtWhileCancelling != null) {
/*  844 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */             
/*  847 */             timeoutTask.cancel();
/*  848 */             timeoutTask = null;
/*      */           }
/*      */           
/*  851 */           synchronized (this.cancelTimeoutMutex) {
/*  852 */             if (this.wasCancelled) {
/*  853 */               SQLException cause = null;
/*      */               
/*  855 */               if (this.wasCancelledByTimeout) {
/*  856 */                 cause = new MySQLTimeoutException();
/*      */               } else {
/*  858 */                 cause = new MySQLStatementCancelledException();
/*      */               }
/*      */               
/*  861 */               resetCancelledState();
/*      */               
/*  863 */               throw cause;
/*      */             }
/*      */           }
/*      */         } finally {
/*  867 */           if (timeoutTask != null) {
/*  868 */             timeoutTask.cancel();
/*  869 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */           
/*  872 */           if (oldCatalog != null) {
/*  873 */             locallyScopedConn.setCatalog(oldCatalog);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  878 */       if (rs != null) {
/*  879 */         this.lastInsertId = rs.getUpdateID();
/*      */         
/*  881 */         this.results = rs;
/*      */         
/*  883 */         rs.setFirstCharOfQuery(firstNonWsChar);
/*      */         
/*  885 */         if (rs.reallyResult()) {
/*  886 */           if (cachedMetaData != null) {
/*  887 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */           }
/*  889 */           else if (this.connection.getCacheResultSetMetadata()) {
/*  890 */             locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  896 */       CancelTask timeoutTask = (rs != null) && (rs.reallyResult()) ? 1 : 0;jsr 17;return timeoutTask;
/*      */     } finally {
/*  898 */       jsr 6; } localObject5 = returnAddress;locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */     
/*  900 */     this.statementExecuting.set(false);ret;
/*      */     
/*  902 */     localObject6 = finally;throw ((Throwable)localObject6);
/*      */   }
/*      */   
/*      */   protected void statementBegins() {
/*  906 */     this.clearWarningsCalled = false;
/*  907 */     this.statementExecuting.set(true);
/*      */   }
/*      */   
/*      */   protected void resetCancelledState() throws SQLException {
/*  911 */     synchronized (checkClosed().getConnectionMutex()) {
/*  912 */       if (this.cancelTimeoutMutex == null) {
/*  913 */         return;
/*      */       }
/*      */       
/*  916 */       synchronized (this.cancelTimeoutMutex) {
/*  917 */         this.wasCancelled = false;
/*  918 */         this.wasCancelledByTimeout = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean execute(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  927 */     return executeInternal(sql, returnGeneratedKeys == 1);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean execute(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/*  934 */     return executeInternal(sql, (generatedKeyIndices != null) && (generatedKeyIndices.length > 0));
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean execute(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/*  941 */     return executeInternal(sql, (generatedKeyNames != null) && (generatedKeyNames.length > 0));
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
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  958 */     return Util.truncateAndConvertToInt(executeBatchInternal());
/*      */   }
/*      */   
/*      */   protected long[] executeBatchInternal() throws SQLException {
/*  962 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */     
/*  964 */     synchronized (locallyScopedConn.getConnectionMutex()) {
/*  965 */       if (locallyScopedConn.isReadOnly()) {
/*  966 */         throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  970 */       implicitlyCloseAllOpenResults();
/*      */       
/*  972 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/*  973 */         return new long[0];
/*      */       }
/*      */       
/*      */ 
/*  977 */       int individualStatementTimeout = this.timeoutInMillis;
/*  978 */       this.timeoutInMillis = 0;
/*      */       
/*  980 */       CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/*  983 */         resetCancelledState();
/*      */         
/*  985 */         statementBegins();
/*      */         try
/*      */         {
/*  988 */           this.retrieveGeneratedKeys = true;
/*      */           
/*  990 */           long[] updateCounts = null;
/*      */           
/*  992 */           if (this.batchedArgs != null) {
/*  993 */             nbrCommands = this.batchedArgs.size();
/*      */             
/*  995 */             this.batchedGeneratedKeys = new ArrayList(this.batchedArgs.size());
/*      */             
/*  997 */             boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
/*      */             
/*  999 */             if ((locallyScopedConn.versionMeetsMinimum(4, 1, 1)) && ((multiQueriesEnabled) || ((locallyScopedConn.getRewriteBatchedStatements()) && (nbrCommands > 4))))
/*      */             {
/* 1001 */               long[] arrayOfLong1 = executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout);return arrayOfLong1;
/*      */             }
/*      */             
/* 1004 */             if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/* 1005 */               timeoutTask = new CancelTask(this);
/* 1006 */               locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */             }
/*      */             
/* 1009 */             updateCounts = new long[nbrCommands];
/*      */             
/* 1011 */             for (int i = 0; i < nbrCommands; i++) {
/* 1012 */               updateCounts[i] = -3L;
/*      */             }
/*      */             
/* 1015 */             SQLException sqlEx = null;
/*      */             
/* 1017 */             int commandIndex = 0;
/*      */             
/* 1019 */             for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*      */               try {
/* 1021 */                 String sql = (String)this.batchedArgs.get(commandIndex);
/* 1022 */                 updateCounts[commandIndex] = executeUpdateInternal(sql, true, true);
/*      */                 
/* 1024 */                 getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I') && (containsOnDuplicateKeyInString(sql)) ? 1 : 0);
/*      */               } catch (SQLException ex) {
/* 1026 */                 updateCounts[commandIndex] = -3L;
/*      */                 
/* 1028 */                 if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */                 {
/* 1030 */                   sqlEx = ex;
/*      */                 } else {
/* 1032 */                   long[] newUpdateCounts = new long[commandIndex];
/*      */                   
/* 1034 */                   if (hasDeadlockOrTimeoutRolledBackTx(ex)) {
/* 1035 */                     for (int i = 0; i < newUpdateCounts.length; i++) {
/* 1036 */                       newUpdateCounts[i] = -3L;
/*      */                     }
/*      */                   } else {
/* 1039 */                     System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */                   }
/*      */                   
/* 1042 */                   throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1047 */             if (sqlEx != null) {
/* 1048 */               throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           
/* 1052 */           if (timeoutTask != null) {
/* 1053 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1054 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */             
/* 1057 */             timeoutTask.cancel();
/*      */             
/* 1059 */             locallyScopedConn.getCancelTimer().purge();
/* 1060 */             timeoutTask = null;
/*      */           }
/*      */           
/* 1063 */           int nbrCommands = updateCounts != null ? updateCounts : new long[0];return nbrCommands;
/*      */         } finally {
/* 1065 */           this.statementExecuting.set(false);
/*      */         }
/*      */         
/*      */ 
/* 1069 */         localObject4 = returnAddress; } finally { jsr 6; } if (timeoutTask != null) {
/* 1070 */         timeoutTask.cancel();
/*      */         
/* 1072 */         locallyScopedConn.getCancelTimer().purge();
/*      */       }
/*      */       
/* 1075 */       resetCancelledState();
/*      */       
/* 1077 */       this.timeoutInMillis = individualStatementTimeout;
/*      */       
/* 1079 */       clearBatch();ret;
/*      */     }
/*      */   }
/*      */   
/*      */   protected final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex)
/*      */   {
/* 1085 */     int vendorCode = ex.getErrorCode();
/*      */     
/* 1087 */     switch (vendorCode) {
/*      */     case 1206: 
/*      */     case 1213: 
/* 1090 */       return true;
/*      */     case 1205: 
/* 1092 */       return !this.version5013OrNewer;
/*      */     }
/* 1094 */     return false;
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
/*      */   private long[] executeBatchUsingMultiQueries(boolean multiQueriesEnabled, int nbrCommands, int individualStatementTimeout)
/*      */     throws SQLException
/*      */   {
/* 1108 */     MySQLConnection locallyScopedConn = checkClosed();
/*      */     
/* 1110 */     synchronized (locallyScopedConn.getConnectionMutex()) {
/* 1111 */       if (!multiQueriesEnabled) {
/* 1112 */         locallyScopedConn.getIO().enableMultiQueries();
/*      */       }
/*      */       
/* 1115 */       java.sql.Statement batchStmt = null;
/*      */       
/* 1117 */       CancelTask timeoutTask = null;
/*      */       try
/*      */       {
/* 1120 */         long[] updateCounts = new long[nbrCommands];
/*      */         
/* 1122 */         for (int i = 0; i < nbrCommands; i++) {
/* 1123 */           updateCounts[i] = -3L;
/*      */         }
/*      */         
/* 1126 */         int commandIndex = 0;
/*      */         
/* 1128 */         StringBuilder queryBuf = new StringBuilder();
/*      */         
/* 1130 */         batchStmt = locallyScopedConn.createStatement();
/*      */         
/* 1132 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (individualStatementTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/* 1133 */           timeoutTask = new CancelTask((StatementImpl)batchStmt);
/* 1134 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, individualStatementTimeout);
/*      */         }
/*      */         
/* 1137 */         int counter = 0;
/*      */         
/* 1139 */         int numberOfBytesPerChar = 1;
/*      */         
/* 1141 */         String connectionEncoding = locallyScopedConn.getEncoding();
/*      */         
/* 1143 */         if (StringUtils.startsWithIgnoreCase(connectionEncoding, "utf")) {
/* 1144 */           numberOfBytesPerChar = 3;
/* 1145 */         } else if (CharsetMapping.isMultibyteCharset(connectionEncoding)) {
/* 1146 */           numberOfBytesPerChar = 2;
/*      */         }
/*      */         
/* 1149 */         int escapeAdjust = 1;
/*      */         
/* 1151 */         batchStmt.setEscapeProcessing(this.doEscapeProcessing);
/*      */         
/* 1153 */         if (this.doEscapeProcessing) {
/* 1154 */           escapeAdjust = 2;
/*      */         }
/*      */         
/* 1157 */         SQLException sqlEx = null;
/*      */         
/* 1159 */         int argumentSetsInBatchSoFar = 0;
/*      */         
/* 1161 */         for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 1162 */           String nextQuery = (String)this.batchedArgs.get(commandIndex);
/*      */           
/* 1164 */           if (((queryBuf.length() + nextQuery.length()) * numberOfBytesPerChar + 1 + 4) * escapeAdjust + 32 > this.connection.getMaxAllowedPacket())
/*      */           {
/*      */             try {
/* 1167 */               batchStmt.execute(queryBuf.toString(), 1);
/*      */             } catch (SQLException ex) {
/* 1169 */               sqlEx = handleExceptionForBatch(commandIndex, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */             }
/*      */             
/* 1172 */             counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */             
/* 1174 */             queryBuf = new StringBuilder();
/* 1175 */             argumentSetsInBatchSoFar = 0;
/*      */           }
/*      */           
/* 1178 */           queryBuf.append(nextQuery);
/* 1179 */           queryBuf.append(";");
/* 1180 */           argumentSetsInBatchSoFar++;
/*      */         }
/*      */         
/* 1183 */         if (queryBuf.length() > 0) {
/*      */           try {
/* 1185 */             batchStmt.execute(queryBuf.toString(), 1);
/*      */           } catch (SQLException ex) {
/* 1187 */             sqlEx = handleExceptionForBatch(commandIndex - 1, argumentSetsInBatchSoFar, updateCounts, ex);
/*      */           }
/*      */           
/* 1190 */           counter = processMultiCountsAndKeys((StatementImpl)batchStmt, counter, updateCounts);
/*      */         }
/*      */         
/* 1193 */         if (timeoutTask != null) {
/* 1194 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1195 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1198 */           timeoutTask.cancel();
/*      */           
/* 1200 */           locallyScopedConn.getCancelTimer().purge();
/*      */           
/* 1202 */           timeoutTask = null;
/*      */         }
/*      */         
/* 1205 */         if (sqlEx != null) {
/* 1206 */           throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());
/*      */         }
/*      */         
/* 1209 */         ex = updateCounts != null ? updateCounts : new long[0];jsr 17;return ex;
/*      */       } finally {
/* 1211 */         jsr 6; } localObject2 = returnAddress; if (timeoutTask != null) {
/* 1212 */         timeoutTask.cancel();
/*      */         
/* 1214 */         locallyScopedConn.getCancelTimer().purge();
/*      */       }
/*      */       
/* 1217 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1220 */         if (batchStmt != null) {
/* 1221 */           batchStmt.close();
/*      */         }
/*      */       } finally {
/* 1224 */         if (!multiQueriesEnabled)
/* 1225 */           locallyScopedConn.getIO().disableMultiQueries(); } } ret;
/*      */     
/*      */ 
/*      */ 
/* 1229 */     localObject5 = finally;throw ((Throwable)localObject5);
/*      */   }
/*      */   
/*      */   protected int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, long[] updateCounts) throws SQLException {
/* 1233 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1234 */       updateCounts[(updateCountCounter++)] = batchedStatement.getLargeUpdateCount();
/*      */       
/* 1236 */       boolean doGenKeys = this.batchedGeneratedKeys != null;
/*      */       
/* 1238 */       byte[][] row = (byte[][])null;
/*      */       
/* 1240 */       if (doGenKeys) {
/* 1241 */         long generatedKey = batchedStatement.getLastInsertID();
/*      */         
/* 1243 */         row = new byte[1][];
/* 1244 */         row[0] = StringUtils.getBytes(Long.toString(generatedKey));
/* 1245 */         this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */       }
/*      */       
/* 1248 */       while ((batchedStatement.getMoreResults()) || (batchedStatement.getLargeUpdateCount() != -1L)) {
/* 1249 */         updateCounts[(updateCountCounter++)] = batchedStatement.getLargeUpdateCount();
/*      */         
/* 1251 */         if (doGenKeys) {
/* 1252 */           long generatedKey = batchedStatement.getLastInsertID();
/*      */           
/* 1254 */           row = new byte[1][];
/* 1255 */           row[0] = StringUtils.getBytes(Long.toString(generatedKey));
/* 1256 */           this.batchedGeneratedKeys.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */         }
/*      */       }
/*      */       
/* 1260 */       return updateCountCounter;
/*      */     }
/*      */   }
/*      */   
/*      */   protected SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, long[] updateCounts, SQLException ex) throws BatchUpdateException, SQLException
/*      */   {
/* 1266 */     for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--) {
/* 1267 */       updateCounts[j] = -3L;
/*      */     }
/*      */     
/* 1270 */     if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */     {
/* 1272 */       return ex;
/*      */     }
/*      */     
/* 1275 */     long[] newUpdateCounts = new long[endOfBatchIndex];
/* 1276 */     System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
/*      */     
/* 1278 */     throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
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
/*      */   public ResultSet executeQuery(String sql)
/*      */     throws SQLException
/*      */   {
/* 1293 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1294 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 1296 */       this.retrieveGeneratedKeys = false;
/*      */       
/* 1298 */       resetCancelledState();
/*      */       
/* 1300 */       checkNullOrEmptyQuery(sql);
/*      */       
/* 1302 */       setupStreamingTimeout(locallyScopedConn);
/*      */       
/* 1304 */       if (this.doEscapeProcessing) {
/* 1305 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, locallyScopedConn.serverSupportsConvertFn(), this.connection);
/*      */         
/* 1307 */         if ((escapedSqlResult instanceof String)) {
/* 1308 */           sql = (String)escapedSqlResult;
/*      */         } else {
/* 1310 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */       
/* 1314 */       char firstStatementChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */       
/* 1316 */       if ((sql.charAt(0) == '/') && 
/* 1317 */         (sql.startsWith("/* ping */"))) {
/* 1318 */         doPingInstead();
/*      */         
/* 1320 */         return this.results;
/*      */       }
/*      */       
/*      */ 
/* 1324 */       checkForDml(sql, firstStatementChar);
/*      */       
/* 1326 */       implicitlyCloseAllOpenResults();
/*      */       
/* 1328 */       CachedResultSetMetaData cachedMetaData = null;
/*      */       
/* 1330 */       if (useServerFetch()) {
/* 1331 */         this.results = createResultSetUsingServerFetch(sql);
/*      */         
/* 1333 */         return this.results;
/*      */       }
/*      */       
/* 1336 */       CancelTask timeoutTask = null;
/*      */       
/* 1338 */       String oldCatalog = null;
/*      */       try
/*      */       {
/* 1341 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/* 1342 */           timeoutTask = new CancelTask(this);
/* 1343 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */         
/* 1346 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1347 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1348 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1355 */         Field[] cachedFields = null;
/*      */         
/* 1357 */         if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1358 */           cachedMetaData = locallyScopedConn.getCachedMetaData(sql);
/*      */           
/* 1360 */           if (cachedMetaData != null) {
/* 1361 */             cachedFields = cachedMetaData.fields;
/*      */           }
/*      */         }
/*      */         
/* 1365 */         locallyScopedConn.setSessionMaxRows(this.maxRows);
/*      */         
/* 1367 */         statementBegins();
/*      */         
/* 1369 */         this.results = locallyScopedConn.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), this.currentCatalog, cachedFields);
/*      */         
/*      */ 
/* 1372 */         if (timeoutTask != null) {
/* 1373 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1374 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1377 */           timeoutTask.cancel();
/*      */           
/* 1379 */           locallyScopedConn.getCancelTimer().purge();
/*      */           
/* 1381 */           timeoutTask = null;
/*      */         }
/*      */         
/* 1384 */         synchronized (this.cancelTimeoutMutex) {
/* 1385 */           if (this.wasCancelled) {
/* 1386 */             SQLException cause = null;
/*      */             
/* 1388 */             if (this.wasCancelledByTimeout) {
/* 1389 */               cause = new MySQLTimeoutException();
/*      */             } else {
/* 1391 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */             
/* 1394 */             resetCancelledState();
/*      */             
/* 1396 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1400 */         this.statementExecuting.set(false);
/*      */         
/* 1402 */         if (timeoutTask != null) {
/* 1403 */           timeoutTask.cancel();
/*      */           
/* 1405 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */         
/* 1408 */         if (oldCatalog != null) {
/* 1409 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */       
/* 1413 */       this.lastInsertId = this.results.getUpdateID();
/*      */       
/* 1415 */       if (cachedMetaData != null) {
/* 1416 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */       }
/* 1418 */       else if (this.connection.getCacheResultSetMetadata()) {
/* 1419 */         locallyScopedConn.initializeResultsMetadataFromCache(sql, null, this.results);
/*      */       }
/*      */       
/*      */ 
/* 1423 */       return this.results;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doPingInstead() throws SQLException {
/* 1428 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1429 */       if (this.pingTarget != null) {
/* 1430 */         this.pingTarget.doPing();
/*      */       } else {
/* 1432 */         this.connection.ping();
/*      */       }
/*      */       
/* 1435 */       ResultSetInternalMethods fakeSelectOneResultSet = generatePingResultSet();
/* 1436 */       this.results = fakeSelectOneResultSet;
/*      */     }
/*      */   }
/*      */   
/*      */   protected ResultSetInternalMethods generatePingResultSet() throws SQLException {
/* 1441 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1442 */       Field[] fields = { new Field(null, "1", -5, 1) };
/* 1443 */       ArrayList<ResultSetRow> rows = new ArrayList();
/* 1444 */       byte[] colVal = { 49 };
/*      */       
/* 1446 */       rows.add(new ByteArrayRow(new byte[][] { colVal }, getExceptionInterceptor()));
/*      */       
/* 1448 */       return (ResultSetInternalMethods)DatabaseMetaData.buildResultSet(fields, rows, this.connection);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void executeSimpleNonQuery(MySQLConnection c, String nonQuery) throws SQLException {
/* 1453 */     c.execSQL(this, nonQuery, -1, null, 1003, 1007, false, this.currentCatalog, null, false).close();
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
/*      */   public int executeUpdate(String sql)
/*      */     throws SQLException
/*      */   {
/* 1468 */     return Util.truncateAndConvertToInt(executeLargeUpdate(sql));
/*      */   }
/*      */   
/*      */   protected long executeUpdateInternal(String sql, boolean isBatch, boolean returnGeneratedKeys) throws SQLException {
/* 1472 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1473 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 1475 */       checkNullOrEmptyQuery(sql);
/*      */       
/* 1477 */       resetCancelledState();
/*      */       
/* 1479 */       char firstStatementChar = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
/*      */       
/* 1481 */       this.retrieveGeneratedKeys = returnGeneratedKeys;
/*      */       
/* 1483 */       this.lastQueryIsOnDupKeyUpdate = ((returnGeneratedKeys) && (firstStatementChar == 'I') && (containsOnDuplicateKeyInString(sql)));
/*      */       
/* 1485 */       ResultSetInternalMethods rs = null;
/*      */       
/* 1487 */       if (this.doEscapeProcessing) {
/* 1488 */         Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn(), this.connection);
/*      */         
/* 1490 */         if ((escapedSqlResult instanceof String)) {
/* 1491 */           sql = (String)escapedSqlResult;
/*      */         } else {
/* 1493 */           sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */         }
/*      */       }
/*      */       
/* 1497 */       if (locallyScopedConn.isReadOnly(false)) {
/* 1498 */         throw SQLError.createSQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1502 */       if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
/* 1503 */         throw SQLError.createSQLException(Messages.getString("Statement.46"), "01S03", getExceptionInterceptor());
/*      */       }
/*      */       
/* 1506 */       implicitlyCloseAllOpenResults();
/*      */       
/*      */ 
/*      */ 
/* 1510 */       CancelTask timeoutTask = null;
/*      */       
/* 1512 */       String oldCatalog = null;
/*      */       
/* 1514 */       boolean readInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 1515 */       if ((returnGeneratedKeys) && (firstStatementChar == 'R'))
/*      */       {
/*      */ 
/* 1518 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */       try
/*      */       {
/* 1522 */         if ((locallyScopedConn.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/* 1523 */           timeoutTask = new CancelTask(this);
/* 1524 */           locallyScopedConn.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */         
/* 1527 */         if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1528 */           oldCatalog = locallyScopedConn.getCatalog();
/* 1529 */           locallyScopedConn.setCatalog(this.currentCatalog);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1535 */         locallyScopedConn.setSessionMaxRows(-1);
/*      */         
/* 1537 */         statementBegins();
/*      */         
/*      */ 
/* 1540 */         rs = locallyScopedConn.execSQL(this, sql, -1, null, 1003, 1007, false, this.currentCatalog, null, isBatch);
/*      */         
/*      */ 
/* 1543 */         if (timeoutTask != null) {
/* 1544 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1545 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1548 */           timeoutTask.cancel();
/*      */           
/* 1550 */           locallyScopedConn.getCancelTimer().purge();
/*      */           
/* 1552 */           timeoutTask = null;
/*      */         }
/*      */         
/* 1555 */         synchronized (this.cancelTimeoutMutex) {
/* 1556 */           if (this.wasCancelled) {
/* 1557 */             SQLException cause = null;
/*      */             
/* 1559 */             if (this.wasCancelledByTimeout) {
/* 1560 */               cause = new MySQLTimeoutException();
/*      */             } else {
/* 1562 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */             
/* 1565 */             resetCancelledState();
/*      */             
/* 1567 */             throw cause;
/*      */           }
/*      */         }
/*      */       } finally {
/* 1571 */         locallyScopedConn.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         
/* 1573 */         if (timeoutTask != null) {
/* 1574 */           timeoutTask.cancel();
/*      */           
/* 1576 */           locallyScopedConn.getCancelTimer().purge();
/*      */         }
/*      */         
/* 1579 */         if (oldCatalog != null) {
/* 1580 */           locallyScopedConn.setCatalog(oldCatalog);
/*      */         }
/*      */         
/* 1583 */         if (!isBatch) {
/* 1584 */           this.statementExecuting.set(false);
/*      */         }
/*      */       }
/*      */       
/* 1588 */       this.results = rs;
/*      */       
/* 1590 */       rs.setFirstCharOfQuery(firstStatementChar);
/*      */       
/* 1592 */       this.updateCount = rs.getUpdateCount();
/*      */       
/* 1594 */       this.lastInsertId = rs.getUpdateID();
/*      */       
/* 1596 */       return this.updateCount;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int executeUpdate(String sql, int autoGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/* 1604 */     return Util.truncateAndConvertToInt(executeLargeUpdate(sql, autoGeneratedKeys));
/*      */   }
/*      */   
/*      */ 
/*      */   public int executeUpdate(String sql, int[] columnIndexes)
/*      */     throws SQLException
/*      */   {
/* 1611 */     return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnIndexes));
/*      */   }
/*      */   
/*      */ 
/*      */   public int executeUpdate(String sql, String[] columnNames)
/*      */     throws SQLException
/*      */   {
/* 1618 */     return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnNames));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */     throws SQLException
/*      */   {
/* 1626 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1627 */       if (this.connection != null) {
/* 1628 */         return this.connection.getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */       
/* 1631 */       return new GregorianCalendar();
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public java.sql.Connection getConnection()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 8	com/mysql/jdbc/StatementImpl:connection	Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: areturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1644	-> byte code offset #0
/*      */     //   Java source line #1645	-> byte code offset #12
/*      */     //   Java source line #1646	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 1658 */     return 1000;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 13	com/mysql/jdbc/StatementImpl:fetchSize	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1670	-> byte code offset #0
/*      */     //   Java source line #1671	-> byte code offset #12
/*      */     //   Java source line #1672	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1679 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1680 */       if (!this.retrieveGeneratedKeys) {
/* 1681 */         throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1685 */       if (this.batchedGeneratedKeys == null) {
/* 1686 */         if (this.lastQueryIsOnDupKeyUpdate) {
/* 1687 */           return this.generatedKeysResults = getGeneratedKeysInternal(1L);
/*      */         }
/* 1689 */         return this.generatedKeysResults = getGeneratedKeysInternal();
/*      */       }
/*      */       
/* 1692 */       Field[] fields = new Field[1];
/* 1693 */       fields[0] = new Field("", "GENERATED_KEY", -5, 20);
/* 1694 */       fields[0].setConnection(this.connection);
/*      */       
/* 1696 */       this.generatedKeysResults = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
/*      */       
/*      */ 
/* 1699 */       return this.generatedKeysResults;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ResultSetInternalMethods getGeneratedKeysInternal()
/*      */     throws SQLException
/*      */   {
/* 1709 */     long numKeys = getLargeUpdateCount();
/* 1710 */     return getGeneratedKeysInternal(numKeys);
/*      */   }
/*      */   
/*      */   protected ResultSetInternalMethods getGeneratedKeysInternal(long numKeys) throws SQLException {
/* 1714 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1715 */       Field[] fields = new Field[1];
/* 1716 */       fields[0] = new Field("", "GENERATED_KEY", -5, 20);
/* 1717 */       fields[0].setConnection(this.connection);
/* 1718 */       fields[0].setUseOldNameMetadata(true);
/*      */       
/* 1720 */       ArrayList<ResultSetRow> rowSet = new ArrayList();
/*      */       
/* 1722 */       long beginAt = getLastInsertID();
/*      */       
/* 1724 */       if (beginAt < 0L) {
/* 1725 */         fields[0].setUnsigned();
/*      */       }
/*      */       
/* 1728 */       if (this.results != null) {
/* 1729 */         String serverInfo = this.results.getServerInfo();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1734 */         if ((numKeys > 0L) && (this.results.getFirstCharOfQuery() == 'R') && (serverInfo != null) && (serverInfo.length() > 0)) {
/* 1735 */           numKeys = getRecordCountFromInfo(serverInfo);
/*      */         }
/*      */         
/* 1738 */         if ((beginAt != 0L) && (numKeys > 0L)) {
/* 1739 */           for (int i = 0; i < numKeys; i++) {
/* 1740 */             byte[][] row = new byte[1][];
/* 1741 */             if (beginAt > 0L) {
/* 1742 */               row[0] = StringUtils.getBytes(Long.toString(beginAt));
/*      */             } else {
/* 1744 */               byte[] asBytes = new byte[8];
/* 1745 */               asBytes[7] = ((byte)(int)(beginAt & 0xFF));
/* 1746 */               asBytes[6] = ((byte)(int)(beginAt >>> 8));
/* 1747 */               asBytes[5] = ((byte)(int)(beginAt >>> 16));
/* 1748 */               asBytes[4] = ((byte)(int)(beginAt >>> 24));
/* 1749 */               asBytes[3] = ((byte)(int)(beginAt >>> 32));
/* 1750 */               asBytes[2] = ((byte)(int)(beginAt >>> 40));
/* 1751 */               asBytes[1] = ((byte)(int)(beginAt >>> 48));
/* 1752 */               asBytes[0] = ((byte)(int)(beginAt >>> 56));
/*      */               
/* 1754 */               BigInteger val = new BigInteger(1, asBytes);
/*      */               
/* 1756 */               row[0] = val.toString().getBytes();
/*      */             }
/* 1758 */             rowSet.add(new ByteArrayRow(row, getExceptionInterceptor()));
/* 1759 */             beginAt += this.connection.getAutoIncrementIncrement();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1764 */       ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
/*      */       
/*      */ 
/* 1767 */       return gkRs;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getId()
/*      */   {
/* 1777 */     return this.statementId;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public long getLastInsertID()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 17	com/mysql/jdbc/StatementImpl:lastInsertId	J
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: lreturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     //   24: astore_1
/*      */     //   25: new 297	java/lang/RuntimeException
/*      */     //   28: dup
/*      */     //   29: aload_1
/*      */     //   30: invokespecial 298	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   33: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1793	-> byte code offset #0
/*      */     //   Java source line #1794	-> byte code offset #12
/*      */     //   Java source line #1795	-> byte code offset #19
/*      */     //   Java source line #1796	-> byte code offset #24
/*      */     //   Java source line #1797	-> byte code offset #25
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	34	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   24	6	1	e	SQLException
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */     //   0	18	24	java/sql/SQLException
/*      */     //   19	24	24	java/sql/SQLException
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public long getLongUpdateCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 26	com/mysql/jdbc/StatementImpl:results	Lcom/mysql/jdbc/ResultSetInternalMethods;
/*      */     //   16: ifnonnull +9 -> 25
/*      */     //   19: ldc2_w 15
/*      */     //   22: aload_1
/*      */     //   23: monitorexit
/*      */     //   24: lreturn
/*      */     //   25: aload_0
/*      */     //   26: getfield 26	com/mysql/jdbc/StatementImpl:results	Lcom/mysql/jdbc/ResultSetInternalMethods;
/*      */     //   29: invokeinterface 144 1 0
/*      */     //   34: ifeq +9 -> 43
/*      */     //   37: ldc2_w 15
/*      */     //   40: aload_1
/*      */     //   41: monitorexit
/*      */     //   42: lreturn
/*      */     //   43: aload_0
/*      */     //   44: getfield 31	com/mysql/jdbc/StatementImpl:updateCount	J
/*      */     //   47: aload_1
/*      */     //   48: monitorexit
/*      */     //   49: lreturn
/*      */     //   50: astore_2
/*      */     //   51: aload_1
/*      */     //   52: monitorexit
/*      */     //   53: aload_2
/*      */     //   54: athrow
/*      */     //   55: astore_1
/*      */     //   56: new 297	java/lang/RuntimeException
/*      */     //   59: dup
/*      */     //   60: aload_1
/*      */     //   61: invokespecial 298	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   64: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1814	-> byte code offset #0
/*      */     //   Java source line #1815	-> byte code offset #12
/*      */     //   Java source line #1816	-> byte code offset #19
/*      */     //   Java source line #1819	-> byte code offset #25
/*      */     //   Java source line #1820	-> byte code offset #37
/*      */     //   Java source line #1823	-> byte code offset #43
/*      */     //   Java source line #1824	-> byte code offset #50
/*      */     //   Java source line #1825	-> byte code offset #55
/*      */     //   Java source line #1826	-> byte code offset #56
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	65	0	this	StatementImpl
/*      */     //   55	6	1	e	SQLException
/*      */     //   50	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	24	50	finally
/*      */     //   25	42	50	finally
/*      */     //   43	49	50	finally
/*      */     //   50	53	50	finally
/*      */     //   0	24	55	java/sql/SQLException
/*      */     //   25	42	55	java/sql/SQLException
/*      */     //   43	49	55	java/sql/SQLException
/*      */     //   50	55	55	java/sql/SQLException
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 19	com/mysql/jdbc/StatementImpl:maxFieldSize	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1842	-> byte code offset #0
/*      */     //   Java source line #1843	-> byte code offset #12
/*      */     //   Java source line #1844	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public int getMaxRows()
/*      */     throws SQLException
/*      */   {
/* 1858 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1859 */       if (this.maxRows <= 0) {
/* 1860 */         return 0;
/*      */       }
/*      */       
/* 1863 */       return this.maxRows;
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
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 1877 */     return getMoreResults(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getMoreResults(int current)
/*      */     throws SQLException
/*      */   {
/* 1884 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1885 */       if (this.results == null) {
/* 1886 */         return false;
/*      */       }
/*      */       
/* 1889 */       boolean streamingMode = createStreamingResultSet();
/*      */       
/* 1891 */       while ((streamingMode) && 
/* 1892 */         (this.results.reallyResult()) && 
/* 1893 */         (this.results.next())) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1899 */       ResultSetInternalMethods nextResultSet = this.results.getNextResultSet();
/*      */       
/* 1901 */       switch (current)
/*      */       {
/*      */       case 1: 
/* 1904 */         if (this.results != null) {
/* 1905 */           if ((!streamingMode) && (!this.connection.getDontTrackOpenResources())) {
/* 1906 */             this.results.realClose(false);
/*      */           }
/*      */           
/* 1909 */           this.results.clearNextResult();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 3: 
/* 1916 */         if (this.results != null) {
/* 1917 */           if ((!streamingMode) && (!this.connection.getDontTrackOpenResources())) {
/* 1918 */             this.results.realClose(false);
/*      */           }
/*      */           
/* 1921 */           this.results.clearNextResult();
/*      */         }
/*      */         
/* 1924 */         closeAllOpenResults();
/*      */         
/* 1926 */         break;
/*      */       
/*      */       case 2: 
/* 1929 */         if (!this.connection.getDontTrackOpenResources()) {
/* 1930 */           this.openResults.add(this.results);
/*      */         }
/*      */         
/* 1933 */         this.results.clearNextResult();
/*      */         
/* 1935 */         break;
/*      */       
/*      */       default: 
/* 1938 */         throw SQLError.createSQLException(Messages.getString("Statement.19"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 1941 */       this.results = nextResultSet;
/*      */       
/* 1943 */       if (this.results == null) {
/* 1944 */         this.updateCount = -1L;
/* 1945 */         this.lastInsertId = -1L;
/* 1946 */       } else if (this.results.reallyResult()) {
/* 1947 */         this.updateCount = -1L;
/* 1948 */         this.lastInsertId = -1L;
/*      */       } else {
/* 1950 */         this.updateCount = this.results.getUpdateCount();
/* 1951 */         this.lastInsertId = this.results.getUpdateID();
/*      */       }
/*      */       
/* 1954 */       boolean moreResults = (this.results != null) && (this.results.reallyResult());
/* 1955 */       if (!moreResults) {
/* 1956 */         checkAndPerformCloseOnCompletionAction();
/*      */       }
/* 1958 */       return moreResults;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 30	com/mysql/jdbc/StatementImpl:timeoutInMillis	I
/*      */     //   16: sipush 1000
/*      */     //   19: idiv
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: ireturn
/*      */     //   23: astore_2
/*      */     //   24: aload_1
/*      */     //   25: monitorexit
/*      */     //   26: aload_2
/*      */     //   27: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1973	-> byte code offset #0
/*      */     //   Java source line #1974	-> byte code offset #12
/*      */     //   Java source line #1975	-> byte code offset #23
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	28	0	this	StatementImpl
/*      */     //   10	15	1	Ljava/lang/Object;	Object
/*      */     //   23	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	22	23	finally
/*      */     //   23	26	23	finally
/*      */   }
/*      */   
/*      */   private long getRecordCountFromInfo(String serverInfo)
/*      */   {
/* 1984 */     StringBuilder recordsBuf = new StringBuilder();
/* 1985 */     long recordsCount = 0L;
/* 1986 */     long duplicatesCount = 0L;
/*      */     
/* 1988 */     char c = '\000';
/*      */     
/* 1990 */     int length = serverInfo.length();
/* 1991 */     for (int i = 0; 
/*      */         
/* 1993 */         i < length; i++) {
/* 1994 */       c = serverInfo.charAt(i);
/*      */       
/* 1996 */       if (Character.isDigit(c)) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 2001 */     recordsBuf.append(c);
/* 2002 */     i++;
/* 2004 */     for (; 
/* 2004 */         i < length; i++) {
/* 2005 */       c = serverInfo.charAt(i);
/*      */       
/* 2007 */       if (!Character.isDigit(c)) {
/*      */         break;
/*      */       }
/*      */       
/* 2011 */       recordsBuf.append(c);
/*      */     }
/*      */     
/* 2014 */     recordsCount = Long.parseLong(recordsBuf.toString());
/*      */     
/* 2016 */     StringBuilder duplicatesBuf = new StringBuilder();
/* 2018 */     for (; 
/* 2018 */         i < length; i++) {
/* 2019 */       c = serverInfo.charAt(i);
/*      */       
/* 2021 */       if (Character.isDigit(c)) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/* 2026 */     duplicatesBuf.append(c);
/* 2027 */     i++;
/* 2029 */     for (; 
/* 2029 */         i < length; i++) {
/* 2030 */       c = serverInfo.charAt(i);
/*      */       
/* 2032 */       if (!Character.isDigit(c)) {
/*      */         break;
/*      */       }
/*      */       
/* 2036 */       duplicatesBuf.append(c);
/*      */     }
/*      */     
/* 2039 */     duplicatesCount = Long.parseLong(duplicatesBuf.toString());
/*      */     
/* 2041 */     return recordsCount - duplicatesCount;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 2054 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2055 */       return (this.results != null) && (this.results.reallyResult()) ? this.results : null;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 28	com/mysql/jdbc/StatementImpl:resultSetConcurrency	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2068	-> byte code offset #0
/*      */     //   Java source line #2069	-> byte code offset #12
/*      */     //   Java source line #2070	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 2077 */     return 1;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected ResultSetInternalMethods getResultSetInternal()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 26	com/mysql/jdbc/StatementImpl:results	Lcom/mysql/jdbc/ResultSetInternalMethods;
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: areturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     //   24: astore_1
/*      */     //   25: aload_0
/*      */     //   26: getfield 26	com/mysql/jdbc/StatementImpl:results	Lcom/mysql/jdbc/ResultSetInternalMethods;
/*      */     //   29: areturn
/*      */     // Line number table:
/*      */     //   Java source line #2082	-> byte code offset #0
/*      */     //   Java source line #2083	-> byte code offset #12
/*      */     //   Java source line #2084	-> byte code offset #19
/*      */     //   Java source line #2085	-> byte code offset #24
/*      */     //   Java source line #2086	-> byte code offset #25
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	30	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   24	2	1	e	SQLException
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */     //   0	18	24	java/sql/SQLException
/*      */     //   19	24	24	java/sql/SQLException
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getResultSetType()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 29	com/mysql/jdbc/StatementImpl:resultSetType	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2099	-> byte code offset #0
/*      */     //   Java source line #2100	-> byte code offset #12
/*      */     //   Java source line #2101	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 2115 */     return Util.truncateAndConvertToInt(getLargeUpdateCount());
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
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 2138 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 2140 */       if (this.clearWarningsCalled) {
/* 2141 */         return null;
/*      */       }
/*      */       
/* 2144 */       if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 2145 */         SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
/*      */         
/* 2147 */         if (this.warningChain != null) {
/* 2148 */           this.warningChain.setNextWarning(pendingWarningsFromServer);
/*      */         } else {
/* 2150 */           this.warningChain = pendingWarningsFromServer;
/*      */         }
/*      */         
/* 2153 */         return this.warningChain;
/*      */       }
/*      */       
/* 2156 */       return this.warningChain;
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
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/* 2170 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/* 2172 */     if ((locallyScopedConn == null) || (this.isClosed)) {
/* 2173 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2177 */     if (!locallyScopedConn.getDontTrackOpenResources()) {
/* 2178 */       locallyScopedConn.unregisterStatement(this);
/*      */     }
/*      */     
/* 2181 */     if ((this.useUsageAdvisor) && 
/* 2182 */       (!calledExplicitly)) {
/* 2183 */       String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
/*      */       
/* 2185 */       this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2190 */     if (closeOpenResults) {
/* 2191 */       closeOpenResults = (!this.holdResultsOpenOverClose) && (!this.connection.getDontTrackOpenResources());
/*      */     }
/*      */     
/* 2194 */     if (closeOpenResults) {
/* 2195 */       if (this.results != null) {
/*      */         try
/*      */         {
/* 2198 */           this.results.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */       }
/*      */       
/* 2203 */       if (this.generatedKeysResults != null) {
/*      */         try
/*      */         {
/* 2206 */           this.generatedKeysResults.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */       }
/*      */       
/* 2211 */       closeAllOpenResults();
/*      */     }
/*      */     
/* 2214 */     this.isClosed = true;
/*      */     
/* 2216 */     this.results = null;
/* 2217 */     this.generatedKeysResults = null;
/* 2218 */     this.connection = null;
/* 2219 */     this.warningChain = null;
/* 2220 */     this.openResults = null;
/* 2221 */     this.batchedGeneratedKeys = null;
/* 2222 */     this.localInfileInputStream = null;
/* 2223 */     this.pingTarget = null;
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
/*      */   public void setCursorName(String name)
/*      */     throws SQLException
/*      */   {}
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
/*      */   public void setEscapeProcessing(boolean enable)
/*      */     throws SQLException
/*      */   {
/* 2258 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2259 */       this.doEscapeProcessing = enable;
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
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 2277 */     switch (direction)
/*      */     {
/*      */     case 1000: 
/*      */     case 1001: 
/*      */     case 1002: 
/*      */       break;
/*      */     default: 
/* 2284 */       throw SQLError.createSQLException(Messages.getString("Statement.5"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
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
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 2303 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2304 */       if (((rows < 0) && (rows != Integer.MIN_VALUE)) || ((this.maxRows > 0) && (rows > getMaxRows()))) {
/* 2305 */         throw SQLError.createSQLException(Messages.getString("Statement.7"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 2308 */       this.fetchSize = rows;
/*      */     }
/*      */   }
/*      */   
/*      */   public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose) {
/*      */     try {
/* 2314 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2315 */         this.holdResultsOpenOverClose = holdResultsOpenOverClose;
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {}
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
/*      */   public void setMaxFieldSize(int max)
/*      */     throws SQLException
/*      */   {
/* 2332 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2333 */       if (max < 0) {
/* 2334 */         throw SQLError.createSQLException(Messages.getString("Statement.11"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 2337 */       int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
/*      */       
/* 2339 */       if (max > maxBuf) {
/* 2340 */         throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[] { Long.valueOf(maxBuf) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2344 */       this.maxFieldSize = max;
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
/*      */   public void setMaxRows(int max)
/*      */     throws SQLException
/*      */   {
/* 2360 */     setLargeMaxRows(max);
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
/*      */   public void setQueryTimeout(int seconds)
/*      */     throws SQLException
/*      */   {
/* 2374 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2375 */       if (seconds < 0) {
/* 2376 */         throw SQLError.createSQLException(Messages.getString("Statement.21"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 2379 */       this.timeoutInMillis = (seconds * 1000);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/*      */     try
/*      */     {
/* 2390 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2391 */         this.resultSetConcurrency = concurrencyFlag;
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   void setResultSetType(int typeFlag)
/*      */   {
/*      */     try
/*      */     {
/* 2405 */       synchronized (checkClosed().getConnectionMutex()) {
/* 2406 */         this.resultSetType = typeFlag;
/*      */       }
/*      */     }
/*      */     catch (SQLException e) {}
/*      */   }
/*      */   
/*      */   protected void getBatchedGeneratedKeys(java.sql.Statement batchedStatement) throws SQLException
/*      */   {
/* 2414 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2415 */       if (this.retrieveGeneratedKeys) {
/* 2416 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 2419 */           rs = batchedStatement.getGeneratedKeys();
/*      */           
/* 2421 */           while (rs.next()) {
/* 2422 */             this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */           }
/*      */         } finally {
/* 2425 */           if (rs != null) {
/* 2426 */             rs.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void getBatchedGeneratedKeys(int maxKeys) throws SQLException {
/* 2434 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2435 */       if (this.retrieveGeneratedKeys) {
/* 2436 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 2439 */           if (maxKeys == 0) {
/* 2440 */             rs = getGeneratedKeysInternal();
/*      */           } else {
/* 2442 */             rs = getGeneratedKeysInternal(maxKeys);
/*      */           }
/*      */           
/* 2445 */           while (rs.next()) {
/* 2446 */             this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][] { rs.getBytes(1) }, getExceptionInterceptor()));
/*      */           }
/*      */         } finally {
/* 2449 */           this.isImplicitlyClosingResults = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2455 */         ret;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean useServerFetch()
/*      */     throws SQLException
/*      */   {
/* 2463 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2464 */       return (this.connection.isCursorFetchEnabled()) && (this.fetchSize > 0) && (this.resultSetConcurrency == 1007) && (this.resultSetType == 1003);
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
/* 2479 */   private boolean isPoolable = true;
/*      */   private InputStream localInfileInputStream;
/*      */   protected final boolean version5013OrNewer;
/*      */   
/*      */   /* Error */
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 8	com/mysql/jdbc/StatementImpl:connection	Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: astore_1
/*      */     //   5: aload_1
/*      */     //   6: ifnonnull +5 -> 11
/*      */     //   9: iconst_1
/*      */     //   10: ireturn
/*      */     //   11: aload_1
/*      */     //   12: invokeinterface 89 1 0
/*      */     //   17: dup
/*      */     //   18: astore_2
/*      */     //   19: monitorenter
/*      */     //   20: aload_0
/*      */     //   21: getfield 14	com/mysql/jdbc/StatementImpl:isClosed	Z
/*      */     //   24: aload_2
/*      */     //   25: monitorexit
/*      */     //   26: ireturn
/*      */     //   27: astore_3
/*      */     //   28: aload_2
/*      */     //   29: monitorexit
/*      */     //   30: aload_3
/*      */     //   31: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2470	-> byte code offset #0
/*      */     //   Java source line #2471	-> byte code offset #5
/*      */     //   Java source line #2472	-> byte code offset #9
/*      */     //   Java source line #2474	-> byte code offset #11
/*      */     //   Java source line #2475	-> byte code offset #20
/*      */     //   Java source line #2476	-> byte code offset #27
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	32	0	this	StatementImpl
/*      */     //   4	8	1	locallyScopedConn	MySQLConnection
/*      */     //   18	11	2	Ljava/lang/Object;	Object
/*      */     //   27	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   20	26	27	finally
/*      */     //   27	30	27	finally
/*      */   }
/*      */   
/*      */   public boolean isPoolable()
/*      */     throws SQLException
/*      */   {
/* 2482 */     return this.isPoolable;
/*      */   }
/*      */   
/*      */   public void setPoolable(boolean poolable) throws SQLException {
/* 2486 */     this.isPoolable = poolable;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWrapperFor(Class<?> iface)
/*      */     throws SQLException
/*      */   {
/* 2493 */     checkClosed();
/*      */     
/*      */ 
/* 2496 */     return iface.isInstance(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T unwrap(Class<T> iface)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2505 */       return (T)iface.cast(this);
/*      */     } catch (ClassCastException cce) {
/* 2507 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   protected static int findStartOfStatement(String sql) {
/* 2512 */     int statementStartPos = 0;
/*      */     
/* 2514 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
/* 2515 */       statementStartPos = sql.indexOf("*/");
/*      */       
/* 2517 */       if (statementStartPos == -1) {
/* 2518 */         statementStartPos = 0;
/*      */       } else {
/* 2520 */         statementStartPos += 2;
/*      */       }
/* 2522 */     } else if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "--")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "#"))) {
/* 2523 */       statementStartPos = sql.indexOf('\n');
/*      */       
/* 2525 */       if (statementStartPos == -1) {
/* 2526 */         statementStartPos = sql.indexOf('\r');
/*      */         
/* 2528 */         if (statementStartPos == -1) {
/* 2529 */           statementStartPos = 0;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2534 */     return statementStartPos;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream getLocalInfileInputStream()
/*      */   {
/* 2542 */     return this.localInfileInputStream;
/*      */   }
/*      */   
/*      */   public void setLocalInfileInputStream(InputStream stream) {
/* 2546 */     this.localInfileInputStream = stream;
/*      */   }
/*      */   
/*      */   public void setPingTarget(PingTarget pingTarget) {
/* 2550 */     this.pingTarget = pingTarget;
/*      */   }
/*      */   
/*      */   public ExceptionInterceptor getExceptionInterceptor() {
/* 2554 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   protected boolean containsOnDuplicateKeyInString(String sql) {
/* 2558 */     return getOnDuplicateKeyLocation(sql, this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), this.connection.getRewriteBatchedStatements(), this.connection.isNoBackslashEscapesSet()) != -1;
/*      */   }
/*      */   
/*      */ 
/*      */   protected static int getOnDuplicateKeyLocation(String sql, boolean dontCheckOnDuplicateKeyUpdateInSQL, boolean rewriteBatchedStatements, boolean noBackslashEscapes)
/*      */   {
/* 2564 */     return (dontCheckOnDuplicateKeyUpdateInSQL) && (!rewriteBatchedStatements) ? -1 : StringUtils.indexOfIgnoreCase(0, sql, ON_DUPLICATE_KEY_UPDATE_CLAUSE, "\"'`", "\"'`", noBackslashEscapes ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */   }
/*      */   
/*      */ 
/* 2568 */   private boolean closeOnCompletion = false;
/*      */   
/*      */   public void closeOnCompletion() throws SQLException {
/* 2571 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2572 */       this.closeOnCompletion = true;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isCloseOnCompletion()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 88	com/mysql/jdbc/StatementImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 89 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 48	com/mysql/jdbc/StatementImpl:closeOnCompletion	Z
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2577	-> byte code offset #0
/*      */     //   Java source line #2578	-> byte code offset #12
/*      */     //   Java source line #2579	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	StatementImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   public long[] executeLargeBatch()
/*      */     throws SQLException
/*      */   {
/* 2587 */     return executeBatchInternal();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate(String sql)
/*      */     throws SQLException
/*      */   {
/* 2595 */     return executeUpdateInternal(sql, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate(String sql, int autoGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/* 2603 */     return executeUpdateInternal(sql, false, autoGeneratedKeys == 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate(String sql, int[] columnIndexes)
/*      */     throws SQLException
/*      */   {
/* 2611 */     return executeUpdateInternal(sql, false, (columnIndexes != null) && (columnIndexes.length > 0));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate(String sql, String[] columnNames)
/*      */     throws SQLException
/*      */   {
/* 2619 */     return executeUpdateInternal(sql, false, (columnNames != null) && (columnNames.length > 0));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLargeMaxRows()
/*      */     throws SQLException
/*      */   {
/* 2628 */     return getMaxRows();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getLargeUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 2636 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2637 */       if (this.results == null) {
/* 2638 */         return -1L;
/*      */       }
/*      */       
/* 2641 */       if (this.results.reallyResult()) {
/* 2642 */         return -1L;
/*      */       }
/*      */       
/* 2645 */       return this.results.getUpdateCount();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLargeMaxRows(long max)
/*      */     throws SQLException
/*      */   {
/* 2654 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2655 */       if ((max > 50000000L) || (max < 0L)) {
/* 2656 */         throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2660 */       if (max == 0L) {
/* 2661 */         max = -1L;
/*      */       }
/*      */       
/* 2664 */       this.maxRows = ((int)max);
/*      */     }
/*      */   }
/*      */   
/*      */   boolean isCursorRequired() throws SQLException {
/* 2669 */     return false;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\StatementImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */