/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ResultSetImpl
/*      */   implements ResultSetInternalMethods
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_RS_4_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_RS_5_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_UPD_RS_5_ARG_CTOR;
/*      */   protected static final double MIN_DIFF_PREC;
/*      */   protected static final double MAX_DIFF_PREC;
/*      */   static int resultCounter;
/*      */   
/*      */   protected static BigInteger convertLongToUlong(long longVal)
/*      */   {
/*  148 */     byte[] asBytes = new byte[8];
/*  149 */     asBytes[7] = ((byte)(int)(longVal & 0xFF));
/*  150 */     asBytes[6] = ((byte)(int)(longVal >>> 8));
/*  151 */     asBytes[5] = ((byte)(int)(longVal >>> 16));
/*  152 */     asBytes[4] = ((byte)(int)(longVal >>> 24));
/*  153 */     asBytes[3] = ((byte)(int)(longVal >>> 32));
/*  154 */     asBytes[2] = ((byte)(int)(longVal >>> 40));
/*  155 */     asBytes[1] = ((byte)(int)(longVal >>> 48));
/*  156 */     asBytes[0] = ((byte)(int)(longVal >>> 56));
/*      */     
/*  158 */     return new BigInteger(1, asBytes);
/*      */   }
/*      */   
/*      */ 
/*  162 */   protected String catalog = null;
/*      */   
/*      */ 
/*  165 */   protected Map<String, Integer> columnLabelToIndex = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  171 */   protected Map<String, Integer> columnToIndexCache = null;
/*      */   
/*      */ 
/*  174 */   protected boolean[] columnUsed = null;
/*      */   
/*      */ 
/*      */   protected volatile MySQLConnection connection;
/*      */   
/*  179 */   protected long connectionId = 0L;
/*      */   
/*      */ 
/*  182 */   protected int currentRow = -1;
/*      */   
/*      */ 
/*  185 */   protected boolean doingUpdates = false;
/*      */   
/*  187 */   protected ProfilerEventHandler eventSink = null;
/*      */   
/*  189 */   Calendar fastDefaultCal = null;
/*  190 */   Calendar fastClientCal = null;
/*      */   
/*      */ 
/*  193 */   protected int fetchDirection = 1000;
/*      */   
/*      */ 
/*  196 */   protected int fetchSize = 0;
/*      */   
/*      */ 
/*      */ 
/*      */   protected Field[] fields;
/*      */   
/*      */ 
/*      */ 
/*      */   protected char firstCharOfQuery;
/*      */   
/*      */ 
/*      */ 
/*  208 */   protected Map<String, Integer> fullColumnNameToIndex = null;
/*      */   
/*  210 */   protected Map<String, Integer> columnNameToIndex = null;
/*      */   
/*  212 */   protected boolean hasBuiltIndexMapping = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  217 */   protected boolean isBinaryEncoded = false;
/*      */   
/*      */ 
/*  220 */   protected boolean isClosed = false;
/*      */   
/*  222 */   protected ResultSetInternalMethods nextResultSet = null;
/*      */   
/*      */ 
/*  225 */   protected boolean onInsertRow = false;
/*      */   
/*      */ 
/*      */ 
/*      */   protected StatementImpl owningStatement;
/*      */   
/*      */ 
/*      */ 
/*      */   protected String pointOfOrigin;
/*      */   
/*      */ 
/*  236 */   protected boolean profileSql = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  241 */   protected boolean reallyResult = false;
/*      */   
/*      */ 
/*      */   protected int resultId;
/*      */   
/*      */ 
/*  247 */   protected int resultSetConcurrency = 0;
/*      */   
/*      */ 
/*  250 */   protected int resultSetType = 0;
/*      */   
/*      */ 
/*      */ 
/*      */   protected RowData rowData;
/*      */   
/*      */ 
/*      */ 
/*  258 */   protected String serverInfo = null;
/*      */   
/*      */ 
/*      */   PreparedStatement statementUsedForFetchingRows;
/*      */   
/*  263 */   protected ResultSetRow thisRow = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long updateCount;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  277 */   protected long updateId = -1L;
/*      */   
/*  279 */   private boolean useStrictFloatingPoint = false;
/*      */   
/*  281 */   protected boolean useUsageAdvisor = false;
/*      */   
/*      */ 
/*  284 */   protected SQLWarning warningChain = null;
/*      */   
/*      */ 
/*  287 */   protected boolean wasNullFlag = false;
/*      */   
/*      */   protected Statement wrapperStatement;
/*      */   
/*      */   protected boolean retainOwningStatement;
/*      */   
/*  293 */   protected Calendar gmtCalendar = null;
/*      */   
/*  295 */   protected boolean useFastDateParsing = false;
/*      */   
/*  297 */   private boolean padCharsWithSpace = false;
/*      */   
/*      */   private boolean jdbcCompliantTruncationForReads;
/*      */   
/*  301 */   private boolean useFastIntParsing = true;
/*      */   private boolean useColumnNamesInFindColumn;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   static final char[] EMPTY_SPACE;
/*      */   
/*      */   static
/*      */   {
/*  106 */     if (Util.isJdbc4()) {
/*      */       try {
/*  108 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ResultSet" : "com.mysql.jdbc.JDBC4ResultSet";
/*  109 */         JDBC_4_RS_4_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { Long.TYPE, Long.TYPE, MySQLConnection.class, StatementImpl.class });
/*      */         
/*  111 */         JDBC_4_RS_5_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class });
/*      */         
/*      */ 
/*  114 */         jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42UpdatableResultSet" : "com.mysql.jdbc.JDBC4UpdatableResultSet";
/*  115 */         JDBC_4_UPD_RS_5_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*  118 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  120 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  122 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  125 */       JDBC_4_RS_4_ARG_CTOR = null;
/*  126 */       JDBC_4_RS_5_ARG_CTOR = null;
/*  127 */       JDBC_4_UPD_RS_5_ARG_CTOR = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  134 */     MIN_DIFF_PREC = Float.parseFloat(Float.toString(Float.MIN_VALUE)) - Double.parseDouble(Float.toString(Float.MIN_VALUE));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  139 */     MAX_DIFF_PREC = Float.parseFloat(Float.toString(Float.MAX_VALUE)) - Double.parseDouble(Float.toString(Float.MAX_VALUE));
/*      */     
/*      */ 
/*  142 */     resultCounter = 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  306 */     EMPTY_SPACE = new char['ÿ'];
/*      */     
/*      */ 
/*  309 */     for (int i = 0; i < EMPTY_SPACE.length; i++) {
/*  310 */       EMPTY_SPACE[i] = ' ';
/*      */     }
/*      */   }
/*      */   
/*      */   protected static ResultSetImpl getInstance(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
/*  315 */     if (!Util.isJdbc4()) {
/*  316 */       return new ResultSetImpl(updateCount, updateID, conn, creatorStmt);
/*      */     }
/*      */     
/*  319 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[] { Long.valueOf(updateCount), Long.valueOf(updateID), conn, creatorStmt }, conn.getExceptionInterceptor());
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
/*      */   protected static ResultSetImpl getInstance(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt, boolean isUpdatable)
/*      */     throws SQLException
/*      */   {
/*  333 */     if (!Util.isJdbc4()) {
/*  334 */       if (!isUpdatable) {
/*  335 */         return new ResultSetImpl(catalog, fields, tuples, conn, creatorStmt);
/*      */       }
/*      */       
/*  338 */       return new UpdatableResultSet(catalog, fields, tuples, conn, creatorStmt);
/*      */     }
/*      */     
/*  341 */     if (!isUpdatable) {
/*  342 */       return (ResultSetImpl)Util.handleNewInstance(JDBC_4_RS_5_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*  346 */     return (ResultSetImpl)Util.handleNewInstance(JDBC_4_UPD_RS_5_ARG_CTOR, new Object[] { catalog, fields, tuples, conn, creatorStmt }, conn.getExceptionInterceptor());
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
/*      */   public ResultSetImpl(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
/*      */   {
/*  361 */     this.updateCount = updateCount;
/*  362 */     this.updateId = updateID;
/*  363 */     this.reallyResult = false;
/*  364 */     this.fields = new Field[0];
/*      */     
/*  366 */     this.connection = conn;
/*  367 */     this.owningStatement = creatorStmt;
/*      */     
/*  369 */     this.retainOwningStatement = false;
/*      */     
/*  371 */     if (this.connection != null) {
/*  372 */       this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */       
/*  374 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */       
/*  376 */       this.connectionId = this.connection.getId();
/*  377 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  378 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */       
/*  380 */       this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
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
/*      */   public ResultSetImpl(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  401 */     this.connection = conn;
/*      */     
/*  403 */     this.retainOwningStatement = false;
/*      */     
/*  405 */     if (this.connection != null) {
/*  406 */       this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*  407 */       this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
/*  408 */       this.connectionId = this.connection.getId();
/*  409 */       this.useFastDateParsing = this.connection.getUseFastDateParsing();
/*  410 */       this.profileSql = this.connection.getProfileSql();
/*  411 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*  412 */       this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
/*  413 */       this.useFastIntParsing = this.connection.getUseFastIntParsing();
/*  414 */       this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
/*  415 */       this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
/*      */     }
/*      */     
/*  418 */     this.owningStatement = creatorStmt;
/*      */     
/*  420 */     this.catalog = catalog;
/*      */     
/*  422 */     this.fields = fields;
/*  423 */     this.rowData = tuples;
/*  424 */     this.updateCount = this.rowData.size();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  430 */     this.reallyResult = true;
/*      */     
/*      */ 
/*  433 */     if (this.rowData.size() > 0) {
/*  434 */       if ((this.updateCount == 1L) && 
/*  435 */         (this.thisRow == null)) {
/*  436 */         this.rowData.close();
/*  437 */         this.updateCount = -1L;
/*      */       }
/*      */     }
/*      */     else {
/*  441 */       this.thisRow = null;
/*      */     }
/*      */     
/*  444 */     this.rowData.setOwner(this);
/*      */     
/*  446 */     if (this.fields != null) {
/*  447 */       initializeWithMetadata();
/*      */     }
/*  449 */     this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
/*      */     
/*  451 */     this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
/*      */     
/*  453 */     setRowPositionValidity();
/*      */   }
/*      */   
/*      */   public void initializeWithMetadata() throws SQLException {
/*  457 */     synchronized (checkClosed().getConnectionMutex()) {
/*  458 */       this.rowData.setMetadata(this.fields);
/*      */       
/*  460 */       this.columnToIndexCache = new HashMap();
/*      */       
/*  462 */       if ((this.profileSql) || (this.connection.getUseUsageAdvisor())) {
/*  463 */         this.columnUsed = new boolean[this.fields.length];
/*  464 */         this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
/*  465 */         this.resultId = (resultCounter++);
/*  466 */         this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  467 */         this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */       }
/*      */       
/*  470 */       if (this.connection.getGatherPerformanceMetrics()) {
/*  471 */         this.connection.incrementNumberOfResultSetsCreated();
/*      */         
/*  473 */         Set<String> tableNamesSet = new HashSet();
/*      */         
/*  475 */         for (int i = 0; i < this.fields.length; i++) {
/*  476 */           Field f = this.fields[i];
/*      */           
/*  478 */           String tableName = f.getOriginalTableName();
/*      */           
/*  480 */           if (tableName == null) {
/*  481 */             tableName = f.getTableName();
/*      */           }
/*      */           
/*  484 */           if (tableName != null) {
/*  485 */             if (this.connection.lowerCaseTableNames()) {
/*  486 */               tableName = tableName.toLowerCase();
/*      */             }
/*      */             
/*      */ 
/*  490 */             tableNamesSet.add(tableName);
/*      */           }
/*      */         }
/*      */         
/*  494 */         this.connection.reportNumberOfTablesAccessed(tableNamesSet.size());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized Calendar getFastDefaultCalendar() {
/*  500 */     if (this.fastDefaultCal == null) {
/*  501 */       this.fastDefaultCal = new GregorianCalendar(Locale.US);
/*  502 */       this.fastDefaultCal.setTimeZone(getDefaultTimeZone());
/*      */     }
/*  504 */     return this.fastDefaultCal;
/*      */   }
/*      */   
/*      */   private synchronized Calendar getFastClientCalendar() {
/*  508 */     if (this.fastClientCal == null) {
/*  509 */       this.fastClientCal = new GregorianCalendar(Locale.US);
/*      */     }
/*  511 */     return this.fastClientCal;
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
/*      */   public boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  548 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*      */       boolean b;
/*      */       boolean b;
/*  552 */       if (this.rowData.size() == 0) {
/*  553 */         b = false;
/*      */       } else {
/*  555 */         if (this.onInsertRow) {
/*  556 */           this.onInsertRow = false;
/*      */         }
/*      */         
/*  559 */         if (this.doingUpdates) {
/*  560 */           this.doingUpdates = false;
/*      */         }
/*      */         
/*  563 */         if (this.thisRow != null) {
/*  564 */           this.thisRow.closeOpenStreams();
/*      */         }
/*      */         boolean b;
/*  567 */         if (row == 0) {
/*  568 */           beforeFirst();
/*  569 */           b = false; } else { boolean b;
/*  570 */           if (row == 1) {
/*  571 */             b = first(); } else { boolean b;
/*  572 */             if (row == -1) {
/*  573 */               b = last(); } else { boolean b;
/*  574 */               if (row > this.rowData.size()) {
/*  575 */                 afterLast();
/*  576 */                 b = false;
/*      */               } else { boolean b;
/*  578 */                 if (row < 0)
/*      */                 {
/*  580 */                   int newRowPosition = this.rowData.size() + row + 1;
/*      */                   boolean b;
/*  582 */                   if (newRowPosition <= 0) {
/*  583 */                     beforeFirst();
/*  584 */                     b = false;
/*      */                   } else {
/*  586 */                     b = absolute(newRowPosition);
/*      */                   }
/*      */                 } else {
/*  589 */                   row--;
/*  590 */                   this.rowData.setCurrentRow(row);
/*  591 */                   this.thisRow = this.rowData.getAt(row);
/*  592 */                   b = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           } } }
/*  597 */       setRowPositionValidity();
/*      */       
/*  599 */       return b;
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
/*      */   public void afterLast()
/*      */     throws SQLException
/*      */   {
/*  615 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  617 */       if (this.onInsertRow) {
/*  618 */         this.onInsertRow = false;
/*      */       }
/*      */       
/*  621 */       if (this.doingUpdates) {
/*  622 */         this.doingUpdates = false;
/*      */       }
/*      */       
/*  625 */       if (this.thisRow != null) {
/*  626 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/*  629 */       if (this.rowData.size() != 0) {
/*  630 */         this.rowData.afterLast();
/*  631 */         this.thisRow = null;
/*      */       }
/*      */       
/*  634 */       setRowPositionValidity();
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
/*      */   public void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  650 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  652 */       if (this.onInsertRow) {
/*  653 */         this.onInsertRow = false;
/*      */       }
/*      */       
/*  656 */       if (this.doingUpdates) {
/*  657 */         this.doingUpdates = false;
/*      */       }
/*      */       
/*  660 */       if (this.rowData.size() == 0) {
/*  661 */         return;
/*      */       }
/*      */       
/*  664 */       if (this.thisRow != null) {
/*  665 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/*  668 */       this.rowData.beforeFirst();
/*  669 */       this.thisRow = null;
/*      */       
/*  671 */       setRowPositionValidity();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void buildIndexMapping()
/*      */     throws SQLException
/*      */   {
/*  683 */     int numFields = this.fields.length;
/*  684 */     this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  685 */     this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  686 */     this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */     for (int i = numFields - 1; i >= 0; i--) {
/*  696 */       Integer index = Integer.valueOf(i);
/*  697 */       String columnName = this.fields[i].getOriginalName();
/*  698 */       String columnLabel = this.fields[i].getName();
/*  699 */       String fullColumnName = this.fields[i].getFullName();
/*      */       
/*  701 */       if (columnLabel != null) {
/*  702 */         this.columnLabelToIndex.put(columnLabel, index);
/*      */       }
/*      */       
/*  705 */       if (fullColumnName != null) {
/*  706 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*      */       }
/*      */       
/*  709 */       if (columnName != null) {
/*  710 */         this.columnNameToIndex.put(columnName, index);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  715 */     this.hasBuiltIndexMapping = true;
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
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  730 */     throw new NotUpdatable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final MySQLConnection checkClosed()
/*      */     throws SQLException
/*      */   {
/*  740 */     MySQLConnection c = this.connection;
/*      */     
/*  742 */     if (c == null) {
/*  743 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*  747 */     return c;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void checkColumnBounds(int columnIndex)
/*      */     throws SQLException
/*      */   {
/*  760 */     synchronized (checkClosed().getConnectionMutex()) {
/*  761 */       if (columnIndex < 1) {
/*  762 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  766 */       if (columnIndex > this.fields.length) {
/*  767 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[] { Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  773 */       if ((this.profileSql) || (this.useUsageAdvisor)) {
/*  774 */         this.columnUsed[(columnIndex - 1)] = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  787 */     checkClosed();
/*      */     
/*  789 */     if (!this.onValidRow) {
/*  790 */       throw SQLError.createSQLException(this.invalidRowReason, "S1000", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*  794 */   private boolean onValidRow = false;
/*  795 */   private String invalidRowReason = null;
/*      */   protected boolean useLegacyDatetimeCode;
/*      */   private TimeZone serverTimeZoneTz;
/*      */   
/*      */   private void setRowPositionValidity() throws SQLException {
/*  800 */     if ((!this.rowData.isDynamic()) && (this.rowData.size() == 0)) {
/*  801 */       this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
/*  802 */       this.onValidRow = false;
/*  803 */     } else if (this.rowData.isBeforeFirst()) {
/*  804 */       this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
/*  805 */       this.onValidRow = false;
/*  806 */     } else if (this.rowData.isAfterLast()) {
/*  807 */       this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
/*  808 */       this.onValidRow = false;
/*      */     } else {
/*  810 */       this.onValidRow = true;
/*  811 */       this.invalidRowReason = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void clearNextResult()
/*      */   {
/*  820 */     this.nextResultSet = null;
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
/*  831 */     synchronized (checkClosed().getConnectionMutex()) {
/*  832 */       this.warningChain = null;
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
/*  851 */     realClose(true);
/*      */   }
/*      */   
/*      */   private int convertToZeroWithEmptyCheck() throws SQLException {
/*  855 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  856 */       return 0;
/*      */     }
/*      */     
/*  859 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   private String convertToZeroLiteralStringWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  865 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  866 */       return "0";
/*      */     }
/*      */     
/*  869 */     throw SQLError.createSQLException("Can't convert empty string ('') to numeric", "22018", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ResultSetInternalMethods copy()
/*      */     throws SQLException
/*      */   {
/*  877 */     synchronized (checkClosed().getConnectionMutex()) {
/*  878 */       ResultSetInternalMethods rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
/*      */       
/*  880 */       return rs;
/*      */     }
/*      */   }
/*      */   
/*      */   public void redefineFieldsForDBMD(Field[] f) {
/*  885 */     this.fields = f;
/*      */     
/*  887 */     for (int i = 0; i < this.fields.length; i++) {
/*  888 */       this.fields[i].setUseOldNameMetadata(true);
/*  889 */       this.fields[i].setConnection(this.connection);
/*      */     }
/*      */   }
/*      */   
/*      */   public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
/*  894 */     cachedMetaData.fields = this.fields;
/*  895 */     cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
/*  896 */     cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
/*  897 */     cachedMetaData.metadata = getMetaData();
/*      */   }
/*      */   
/*      */   public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
/*  901 */     this.fields = cachedMetaData.fields;
/*  902 */     this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
/*  903 */     this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
/*  904 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  917 */     throw new NotUpdatable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String extractStringFromNativeColumn(int columnIndex, int mysqlType)
/*      */     throws SQLException
/*      */   {
/*  927 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/*  929 */     this.wasNullFlag = false;
/*      */     
/*  931 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/*  932 */       this.wasNullFlag = true;
/*      */       
/*  934 */       return null;
/*      */     }
/*      */     
/*  937 */     this.wasNullFlag = false;
/*      */     
/*  939 */     String encoding = this.fields[columnIndexMinusOne].getEncoding();
/*      */     
/*  941 */     return this.thisRow.getString(columnIndex - 1, encoding, this.connection);
/*      */   }
/*      */   
/*      */   protected Date fastDateCreate(Calendar cal, int year, int month, int day) throws SQLException {
/*  945 */     synchronized (checkClosed().getConnectionMutex()) {
/*  946 */       Calendar targetCalendar = cal;
/*      */       
/*  948 */       if (cal == null) {
/*  949 */         if (this.connection.getNoTimezoneConversionForDateType()) {
/*  950 */           targetCalendar = getFastClientCalendar();
/*      */         } else {
/*  952 */           targetCalendar = getFastDefaultCalendar();
/*      */         }
/*      */       }
/*      */       
/*  956 */       if (!this.useLegacyDatetimeCode) {
/*  957 */         return TimeUtil.fastDateCreate(year, month, day, targetCalendar);
/*      */       }
/*      */       
/*  960 */       boolean useGmtMillis = (cal == null) && (!this.connection.getNoTimezoneConversionForDateType()) && (this.connection.getUseGmtMillisForDatetimes());
/*      */       
/*  962 */       return TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : targetCalendar, targetCalendar, year, month, day);
/*      */     }
/*      */   }
/*      */   
/*      */   protected Time fastTimeCreate(Calendar cal, int hour, int minute, int second) throws SQLException {
/*  967 */     synchronized (checkClosed().getConnectionMutex()) {
/*  968 */       if (!this.useLegacyDatetimeCode) {
/*  969 */         return TimeUtil.fastTimeCreate(hour, minute, second, cal, getExceptionInterceptor());
/*      */       }
/*      */       
/*  972 */       if (cal == null) {
/*  973 */         cal = getFastDefaultCalendar();
/*      */       }
/*      */       
/*  976 */       return TimeUtil.fastTimeCreate(cal, hour, minute, second, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   protected Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) throws SQLException
/*      */   {
/*  982 */     synchronized (checkClosed().getConnectionMutex()) {
/*  983 */       if (!this.useLegacyDatetimeCode) {
/*  984 */         return TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
/*      */       }
/*      */       
/*  987 */       if (cal == null) {
/*  988 */         cal = getFastDefaultCalendar();
/*      */       }
/*      */       
/*  991 */       boolean useGmtMillis = this.connection.getUseGmtMillisForDatetimes();
/*      */       
/*  993 */       return TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal, year, month, day, hour, minute, seconds, secondsPart);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int findColumn(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1038 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*      */ 
/* 1041 */       if (!this.hasBuiltIndexMapping) {
/* 1042 */         buildIndexMapping();
/*      */       }
/*      */       
/* 1045 */       Integer index = (Integer)this.columnToIndexCache.get(columnName);
/*      */       
/* 1047 */       if (index != null) {
/* 1048 */         return index.intValue() + 1;
/*      */       }
/*      */       
/* 1051 */       index = (Integer)this.columnLabelToIndex.get(columnName);
/*      */       
/* 1053 */       if ((index == null) && (this.useColumnNamesInFindColumn)) {
/* 1054 */         index = (Integer)this.columnNameToIndex.get(columnName);
/*      */       }
/*      */       
/* 1057 */       if (index == null) {
/* 1058 */         index = (Integer)this.fullColumnNameToIndex.get(columnName);
/*      */       }
/*      */       
/* 1061 */       if (index != null) {
/* 1062 */         this.columnToIndexCache.put(columnName, index);
/*      */         
/* 1064 */         return index.intValue() + 1;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1069 */       for (int i = 0; i < this.fields.length; i++) {
/* 1070 */         if (this.fields[i].getName().equalsIgnoreCase(columnName))
/* 1071 */           return i + 1;
/* 1072 */         if (this.fields[i].getFullName().equalsIgnoreCase(columnName)) {
/* 1073 */           return i + 1;
/*      */         }
/*      */       }
/*      */       
/* 1077 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022", getExceptionInterceptor());
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
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/* 1096 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1098 */       boolean b = true;
/*      */       
/* 1100 */       if (this.rowData.isEmpty()) {
/* 1101 */         b = false;
/*      */       }
/*      */       else {
/* 1104 */         if (this.onInsertRow) {
/* 1105 */           this.onInsertRow = false;
/*      */         }
/*      */         
/* 1108 */         if (this.doingUpdates) {
/* 1109 */           this.doingUpdates = false;
/*      */         }
/*      */         
/* 1112 */         this.rowData.beforeFirst();
/* 1113 */         this.thisRow = this.rowData.next();
/*      */       }
/*      */       
/* 1116 */       setRowPositionValidity();
/*      */       
/* 1118 */       return b;
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
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/* 1135 */     checkColumnBounds(i);
/*      */     
/* 1137 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public Array getArray(String colName)
/*      */     throws SQLException
/*      */   {
/* 1153 */     return getArray(findColumn(colName));
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
/*      */   public InputStream getAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1180 */     checkRowPos();
/*      */     
/* 1182 */     if (!this.isBinaryEncoded) {
/* 1183 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */     
/* 1186 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream getAsciiStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1195 */     return getAsciiStream(findColumn(columnName));
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
/*      */   public BigDecimal getBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1212 */     if (!this.isBinaryEncoded) {
/* 1213 */       String stringVal = getString(columnIndex);
/*      */       
/*      */ 
/* 1216 */       if (stringVal != null) {
/* 1217 */         if (stringVal.length() == 0)
/*      */         {
/* 1219 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */           
/* 1221 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 1225 */           return new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/* 1229 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1235 */       return null;
/*      */     }
/*      */     
/* 1238 */     return getNativeBigDecimal(columnIndex);
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
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1259 */     if (!this.isBinaryEncoded) {
/* 1260 */       String stringVal = getString(columnIndex);
/*      */       
/*      */ 
/* 1263 */       if (stringVal != null) {
/* 1264 */         if (stringVal.length() == 0) {
/* 1265 */           BigDecimal val = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */           try
/*      */           {
/* 1268 */             return val.setScale(scale);
/*      */           } catch (ArithmeticException ex) {
/*      */             try {
/* 1271 */               return val.setScale(scale, 4);
/*      */             } catch (ArithmeticException arEx) {
/* 1273 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1281 */           val = new BigDecimal(stringVal);
/*      */         } catch (NumberFormatException ex) { BigDecimal val;
/* 1283 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1284 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 1286 */             val = new BigDecimal(valueAsLong);
/*      */           } else {
/* 1288 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1295 */           return val.setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try { BigDecimal val;
/* 1298 */             return val.setScale(scale, 4);
/*      */           } catch (ArithmeticException arithEx) {
/* 1300 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1307 */       return null;
/*      */     }
/*      */     
/* 1310 */     return getNativeBigDecimal(columnIndex, scale);
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
/*      */   public BigDecimal getBigDecimal(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1326 */     return getBigDecimal(findColumn(columnName));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(String columnName, int scale)
/*      */     throws SQLException
/*      */   {
/* 1339 */     return getBigDecimal(findColumn(columnName), scale);
/*      */   }
/*      */   
/*      */   private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1345 */     if (stringVal != null) {
/* 1346 */       if (stringVal.length() == 0) {
/* 1347 */         BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
/*      */         try
/*      */         {
/* 1350 */           return bdVal.setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try {
/* 1353 */             return bdVal.setScale(scale, 4);
/*      */           } catch (ArithmeticException arEx) {
/* 1355 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1364 */         return new BigDecimal(stringVal).setScale(scale);
/*      */       } catch (ArithmeticException ex) {
/*      */         try {
/* 1367 */           return new BigDecimal(stringVal).setScale(scale, 4);
/*      */         } catch (ArithmeticException arEx) {
/* 1369 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */         }
/*      */         
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/* 1375 */         if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 1376 */           long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */           try
/*      */           {
/* 1379 */             return new BigDecimal(valueAsLong).setScale(scale);
/*      */           } catch (ArithmeticException arEx1) {
/*      */             try {
/* 1382 */               return new BigDecimal(valueAsLong).setScale(scale, 4);
/*      */             } catch (ArithmeticException arEx2) {
/* 1384 */               throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1391 */         if ((this.fields[(columnIndex - 1)].getMysqlType() == 1) && (this.connection.getTinyInt1isBit()) && (this.fields[(columnIndex - 1)].getLength() == 1L))
/*      */         {
/* 1393 */           return new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1 : 0).setScale(scale);
/*      */         }
/*      */         
/* 1396 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1401 */     return null;
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
/*      */   public InputStream getBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1422 */     checkRowPos();
/*      */     
/* 1424 */     if (!this.isBinaryEncoded) {
/* 1425 */       checkColumnBounds(columnIndex);
/*      */       
/* 1427 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1429 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1430 */         this.wasNullFlag = true;
/*      */         
/* 1432 */         return null;
/*      */       }
/*      */       
/* 1435 */       this.wasNullFlag = false;
/*      */       
/* 1437 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1440 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public InputStream getBinaryStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1449 */     return getBinaryStream(findColumn(columnName));
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
/*      */   public java.sql.Blob getBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1464 */     if (!this.isBinaryEncoded) {
/* 1465 */       checkRowPos();
/*      */       
/* 1467 */       checkColumnBounds(columnIndex);
/*      */       
/* 1469 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1471 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1472 */         this.wasNullFlag = true;
/*      */       } else {
/* 1474 */         this.wasNullFlag = false;
/*      */       }
/*      */       
/* 1477 */       if (this.wasNullFlag) {
/* 1478 */         return null;
/*      */       }
/*      */       
/* 1481 */       if (!this.connection.getEmulateLocators()) {
/* 1482 */         return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne), getExceptionInterceptor());
/*      */       }
/*      */       
/* 1485 */       return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */     }
/*      */     
/* 1488 */     return getNativeBlob(columnIndex);
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
/*      */   public java.sql.Blob getBlob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1503 */     return getBlob(findColumn(colName));
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
/*      */   public boolean getBoolean(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1519 */     checkColumnBounds(columnIndex);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1525 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 1527 */     Field field = this.fields[columnIndexMinusOne];
/*      */     
/* 1529 */     if (field.getMysqlType() == 16) {
/* 1530 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1533 */     this.wasNullFlag = false;
/*      */     
/* 1535 */     int sqlType = field.getSQLType();
/*      */     long boolVal;
/* 1537 */     switch (sqlType) {
/*      */     case 16: 
/* 1539 */       if (field.getMysqlType() == -1) {
/* 1540 */         String stringVal = getString(columnIndex);
/*      */         
/* 1542 */         return getBooleanFromString(stringVal);
/*      */       }
/*      */       
/* 1545 */       boolVal = getLong(columnIndex, false);
/*      */       
/* 1547 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     case -7: 
/*      */     case -6: 
/*      */     case -5: 
/*      */     case 2: 
/*      */     case 3: 
/*      */     case 4: 
/*      */     case 5: 
/*      */     case 6: 
/*      */     case 7: 
/*      */     case 8: 
/* 1558 */       boolVal = getLong(columnIndex, false);
/*      */       
/* 1560 */       return (boolVal == -1L) || (boolVal > 0L);
/*      */     }
/* 1562 */     if (this.connection.getPedantic())
/*      */     {
/* 1564 */       switch (sqlType) {
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case 70: 
/*      */       case 91: 
/*      */       case 92: 
/*      */       case 93: 
/*      */       case 2000: 
/*      */       case 2002: 
/*      */       case 2003: 
/*      */       case 2004: 
/*      */       case 2005: 
/*      */       case 2006: 
/* 1578 */         throw SQLError.createSQLException("Required type conversion not allowed", "22018", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */     }
/*      */     
/* 1583 */     if ((sqlType == -2) || (sqlType == -3) || (sqlType == -4) || (sqlType == 2004)) {
/* 1584 */       return byteArrayToBoolean(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1587 */     if (this.useUsageAdvisor) {
/* 1588 */       issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1593 */     String stringVal = getString(columnIndex);
/*      */     
/* 1595 */     return getBooleanFromString(stringVal);
/*      */   }
/*      */   
/*      */   private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException
/*      */   {
/* 1600 */     Object value = this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     
/* 1602 */     if (value == null) {
/* 1603 */       this.wasNullFlag = true;
/*      */       
/* 1605 */       return false;
/*      */     }
/*      */     
/* 1608 */     this.wasNullFlag = false;
/*      */     
/* 1610 */     if (((byte[])value).length == 0) {
/* 1611 */       return false;
/*      */     }
/*      */     
/* 1614 */     byte boolVal = ((byte[])(byte[])value)[0];
/*      */     
/* 1616 */     if (boolVal == 49)
/* 1617 */       return true;
/* 1618 */     if (boolVal == 48) {
/* 1619 */       return false;
/*      */     }
/*      */     
/* 1622 */     return (boolVal == -1) || (boolVal > 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getBoolean(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1631 */     return getBoolean(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final boolean getBooleanFromString(String stringVal) throws SQLException {
/* 1635 */     if ((stringVal != null) && (stringVal.length() > 0)) {
/* 1636 */       int c = Character.toLowerCase(stringVal.charAt(0));
/*      */       
/* 1638 */       return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
/*      */     }
/*      */     
/* 1641 */     return false;
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
/*      */   public byte getByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1656 */     if (!this.isBinaryEncoded) {
/* 1657 */       String stringVal = getString(columnIndex);
/*      */       
/* 1659 */       if ((this.wasNullFlag) || (stringVal == null)) {
/* 1660 */         return 0;
/*      */       }
/*      */       
/* 1663 */       return getByteFromString(stringVal, columnIndex);
/*      */     }
/*      */     
/* 1666 */     return getNativeByte(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte getByte(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1675 */     return getByte(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final byte getByteFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1680 */     if ((stringVal != null) && (stringVal.length() == 0)) {
/* 1681 */       return (byte)convertToZeroWithEmptyCheck();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1691 */     if (stringVal == null) {
/* 1692 */       return 0;
/*      */     }
/*      */     
/* 1695 */     stringVal = stringVal.trim();
/*      */     try
/*      */     {
/* 1698 */       int decimalIndex = stringVal.indexOf(".");
/*      */       
/* 1700 */       if (decimalIndex != -1) {
/* 1701 */         double valueAsDouble = Double.parseDouble(stringVal);
/*      */         
/* 1703 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 1704 */           (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D))) {
/* 1705 */           throwRangeException(stringVal, columnIndex, -6);
/*      */         }
/*      */         
/*      */ 
/* 1709 */         return (byte)(int)valueAsDouble;
/*      */       }
/*      */       
/* 1712 */       long valueAsLong = Long.parseLong(stringVal);
/*      */       
/* 1714 */       if ((this.jdbcCompliantTruncationForReads) && (
/* 1715 */         (valueAsLong < -128L) || (valueAsLong > 127L))) {
/* 1716 */         throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
/*      */       }
/*      */       
/*      */ 
/* 1720 */       return (byte)(int)valueAsLong;
/*      */     } catch (NumberFormatException NFE) {
/* 1722 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009", getExceptionInterceptor());
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
/*      */   public byte[] getBytes(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1744 */     return getBytes(columnIndex, false);
/*      */   }
/*      */   
/*      */   protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException {
/* 1748 */     if (!this.isBinaryEncoded) {
/* 1749 */       checkRowPos();
/*      */       
/* 1751 */       checkColumnBounds(columnIndex);
/*      */       
/* 1753 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1755 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1756 */         this.wasNullFlag = true;
/*      */       } else {
/* 1758 */         this.wasNullFlag = false;
/*      */       }
/*      */       
/* 1761 */       if (this.wasNullFlag) {
/* 1762 */         return null;
/*      */       }
/*      */       
/* 1765 */       return this.thisRow.getColumnValue(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1768 */     return getNativeBytes(columnIndex, noConversion);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getBytes(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1777 */     return getBytes(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final byte[] getBytesFromString(String stringVal) throws SQLException {
/* 1781 */     if (stringVal != null) {
/* 1782 */       return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), this.connection, getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 1786 */     return null;
/*      */   }
/*      */   
/*      */   public int getBytesSize() throws SQLException {
/* 1790 */     RowData localRowData = this.rowData;
/*      */     
/* 1792 */     checkClosed();
/*      */     
/* 1794 */     if ((localRowData instanceof RowDataStatic)) {
/* 1795 */       int bytesSize = 0;
/*      */       
/* 1797 */       int numRows = localRowData.size();
/*      */       
/* 1799 */       for (int i = 0; i < numRows; i++) {
/* 1800 */         bytesSize += localRowData.getAt(i).getBytesSize();
/*      */       }
/*      */       
/* 1803 */       return bytesSize;
/*      */     }
/*      */     
/* 1806 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Calendar getCalendarInstanceForSessionOrNew()
/*      */     throws SQLException
/*      */   {
/* 1814 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1815 */       if (this.connection != null) {
/* 1816 */         return this.connection.getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */       
/*      */ 
/* 1820 */       return new GregorianCalendar();
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
/*      */   public Reader getCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1840 */     if (!this.isBinaryEncoded) {
/* 1841 */       checkColumnBounds(columnIndex);
/*      */       
/* 1843 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 1845 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 1846 */         this.wasNullFlag = true;
/*      */         
/* 1848 */         return null;
/*      */       }
/*      */       
/* 1851 */       this.wasNullFlag = false;
/*      */       
/* 1853 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */     
/* 1856 */     return getNativeCharacterStream(columnIndex);
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
/*      */   public Reader getCharacterStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1875 */     return getCharacterStream(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final Reader getCharacterStreamFromString(String stringVal) throws SQLException {
/* 1879 */     if (stringVal != null) {
/* 1880 */       return new StringReader(stringVal);
/*      */     }
/*      */     
/* 1883 */     return null;
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
/*      */   public java.sql.Clob getClob(int i)
/*      */     throws SQLException
/*      */   {
/* 1898 */     if (!this.isBinaryEncoded) {
/* 1899 */       String asString = getStringForClob(i);
/*      */       
/* 1901 */       if (asString == null) {
/* 1902 */         return null;
/*      */       }
/*      */       
/* 1905 */       return new Clob(asString, getExceptionInterceptor());
/*      */     }
/*      */     
/* 1908 */     return getNativeClob(i);
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
/*      */   public java.sql.Clob getClob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1923 */     return getClob(findColumn(colName));
/*      */   }
/*      */   
/*      */   private final java.sql.Clob getClobFromString(String stringVal) throws SQLException {
/* 1927 */     return new Clob(stringVal, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/* 1940 */     return 1007;
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
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/* 1966 */     throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00", getExceptionInterceptor());
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
/*      */   public Date getDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1982 */     return getDate(columnIndex, null);
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
/*      */   public Date getDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2002 */     if (this.isBinaryEncoded) {
/* 2003 */       return getNativeDate(columnIndex, cal);
/*      */     }
/*      */     
/* 2006 */     if (!this.useFastDateParsing) {
/* 2007 */       String stringVal = getStringInternal(columnIndex, false);
/*      */       
/* 2009 */       if (stringVal == null) {
/* 2010 */         return null;
/*      */       }
/*      */       
/* 2013 */       return getDateFromString(stringVal, columnIndex, cal);
/*      */     }
/*      */     
/* 2016 */     checkColumnBounds(columnIndex);
/*      */     
/* 2018 */     int columnIndexMinusOne = columnIndex - 1;
/* 2019 */     Date tmpDate = this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
/* 2020 */     if ((this.thisRow.isNull(columnIndexMinusOne)) || (tmpDate == null))
/*      */     {
/* 2022 */       this.wasNullFlag = true;
/*      */       
/* 2024 */       return null;
/*      */     }
/*      */     
/* 2027 */     this.wasNullFlag = false;
/*      */     
/* 2029 */     return tmpDate;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Date getDate(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2038 */     return getDate(findColumn(columnName));
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
/*      */   public Date getDate(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2057 */     return getDate(findColumn(columnName), cal);
/*      */   }
/*      */   
/*      */   private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException {
/* 2061 */     int year = 0;
/* 2062 */     int month = 0;
/* 2063 */     int day = 0;
/*      */     try
/*      */     {
/* 2066 */       this.wasNullFlag = false;
/*      */       
/* 2068 */       if (stringVal == null) {
/* 2069 */         this.wasNullFlag = true;
/*      */         
/* 2071 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2081 */       stringVal = stringVal.trim();
/*      */       
/*      */ 
/* 2084 */       int dec = stringVal.indexOf(".");
/* 2085 */       if (dec > -1) {
/* 2086 */         stringVal = stringVal.substring(0, dec);
/*      */       }
/*      */       
/* 2089 */       if ((stringVal.equals("0")) || (stringVal.equals("0000-00-00")) || (stringVal.equals("0000-00-00 00:00:00")) || (stringVal.equals("00000000000000")) || (stringVal.equals("0")))
/*      */       {
/*      */ 
/* 2092 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/* 2093 */           this.wasNullFlag = true;
/*      */           
/* 2095 */           return null; }
/* 2096 */         if ("exception".equals(this.connection.getZeroDateTimeBehavior())) {
/* 2097 */           throw SQLError.createSQLException("Value '" + stringVal + "' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2102 */         return fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/* 2104 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 7)
/*      */       {
/* 2106 */         switch (stringVal.length()) {
/*      */         case 19: 
/*      */         case 21: 
/* 2109 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2110 */           month = Integer.parseInt(stringVal.substring(5, 7));
/* 2111 */           day = Integer.parseInt(stringVal.substring(8, 10));
/*      */           
/* 2113 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         
/*      */ 
/*      */         case 8: 
/*      */         case 14: 
/* 2118 */           year = Integer.parseInt(stringVal.substring(0, 4));
/* 2119 */           month = Integer.parseInt(stringVal.substring(4, 6));
/* 2120 */           day = Integer.parseInt(stringVal.substring(6, 8));
/*      */           
/* 2122 */           return fastDateCreate(targetCalendar, year, month, day);
/*      */         
/*      */ 
/*      */         case 6: 
/*      */         case 10: 
/*      */         case 12: 
/* 2128 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */           
/* 2130 */           if (year <= 69) {
/* 2131 */             year += 100;
/*      */           }
/*      */           
/* 2134 */           month = Integer.parseInt(stringVal.substring(2, 4));
/* 2135 */           day = Integer.parseInt(stringVal.substring(4, 6));
/*      */           
/* 2137 */           return fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         
/*      */ 
/*      */         case 4: 
/* 2141 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */           
/* 2143 */           if (year <= 69) {
/* 2144 */             year += 100;
/*      */           }
/*      */           
/* 2147 */           month = Integer.parseInt(stringVal.substring(2, 4));
/*      */           
/* 2149 */           return fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         
/*      */ 
/*      */         case 2: 
/* 2153 */           year = Integer.parseInt(stringVal.substring(0, 2));
/*      */           
/* 2155 */           if (year <= 69) {
/* 2156 */             year += 100;
/*      */           }
/*      */           
/* 2159 */           return fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         }
/*      */         
/*      */         
/* 2163 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2167 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */       {
/* 2169 */         if ((stringVal.length() == 2) || (stringVal.length() == 1)) {
/* 2170 */           year = Integer.parseInt(stringVal);
/*      */           
/* 2172 */           if (year <= 69) {
/* 2173 */             year += 100;
/*      */           }
/*      */           
/* 2176 */           year += 1900;
/*      */         } else {
/* 2178 */           year = Integer.parseInt(stringVal.substring(0, 4));
/*      */         }
/*      */         
/* 2181 */         return fastDateCreate(targetCalendar, year, 1, 1); }
/* 2182 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 11) {
/* 2183 */         return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/* 2185 */       if (stringVal.length() < 10) {
/* 2186 */         if (stringVal.length() == 8) {
/* 2187 */           return fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */         
/* 2190 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2195 */       if (stringVal.length() != 18) {
/* 2196 */         year = Integer.parseInt(stringVal.substring(0, 4));
/* 2197 */         month = Integer.parseInt(stringVal.substring(5, 7));
/* 2198 */         day = Integer.parseInt(stringVal.substring(8, 10));
/*      */       }
/*      */       else {
/* 2201 */         StringTokenizer st = new StringTokenizer(stringVal, "- ");
/*      */         
/* 2203 */         year = Integer.parseInt(st.nextToken());
/* 2204 */         month = Integer.parseInt(st.nextToken());
/* 2205 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */       
/*      */ 
/* 2209 */       return fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/* 2211 */       throw sqlEx;
/*      */     } catch (Exception e) {
/* 2213 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */       
/*      */ 
/*      */ 
/* 2217 */       sqlEx.initCause(e);
/*      */       
/* 2219 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   private TimeZone getDefaultTimeZone() {
/* 2224 */     return this.useLegacyDatetimeCode ? this.connection.getDefaultTimeZone() : this.serverTimeZoneTz;
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
/*      */   public double getDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2239 */     if (!this.isBinaryEncoded) {
/* 2240 */       return getDoubleInternal(columnIndex);
/*      */     }
/*      */     
/* 2243 */     return getNativeDouble(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getDouble(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2252 */     return getDouble(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException {
/* 2256 */     return getDoubleInternal(stringVal, columnIndex);
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
/*      */   protected double getDoubleInternal(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 2272 */     return getDoubleInternal(getString(colIndex), colIndex);
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
/*      */   protected double getDoubleInternal(String stringVal, int colIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2291 */       if (stringVal == null) {
/* 2292 */         return 0.0D;
/*      */       }
/*      */       
/* 2295 */       if (stringVal.length() == 0) {
/* 2296 */         return convertToZeroWithEmptyCheck();
/*      */       }
/*      */       
/* 2299 */       double d = Double.parseDouble(stringVal);
/*      */       
/* 2301 */       if (this.useStrictFloatingPoint)
/*      */       {
/* 2303 */         if (d == 2.147483648E9D)
/*      */         {
/* 2305 */           d = 2.147483647E9D;
/* 2306 */         } else if (d == 1.0000000036275E-15D)
/*      */         {
/* 2308 */           d = 1.0E-15D;
/* 2309 */         } else if (d == 9.999999869911E14D) {
/* 2310 */           d = 9.99999999999999E14D;
/* 2311 */         } else if (d == 1.4012984643248E-45D) {
/* 2312 */           d = 1.4E-45D;
/* 2313 */         } else if (d == 1.4013E-45D) {
/* 2314 */           d = 1.4E-45D;
/* 2315 */         } else if (d == 3.4028234663853E37D) {
/* 2316 */           d = 3.4028235E37D;
/* 2317 */         } else if (d == -2.14748E9D) {
/* 2318 */           d = -2.147483648E9D;
/* 2319 */         } else if (d != 3.40282E37D) {} }
/* 2320 */       return 3.4028235E37D;
/*      */ 
/*      */     }
/*      */     catch (NumberFormatException e)
/*      */     {
/*      */ 
/* 2326 */       if (this.fields[(colIndex - 1)].getMysqlType() == 16) {
/* 2327 */         long valueAsLong = getNumericRepresentationOfSQLBitType(colIndex);
/*      */         
/* 2329 */         return valueAsLong;
/*      */       }
/*      */       
/* 2332 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_number", new Object[] { stringVal, Integer.valueOf(colIndex) }), "S1009", getExceptionInterceptor());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public float getFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2393 */     if (!this.isBinaryEncoded) {
/* 2394 */       String val = null;
/*      */       
/* 2396 */       val = getString(columnIndex);
/*      */       
/* 2398 */       return getFloatFromString(val, columnIndex);
/*      */     }
/*      */     
/* 2401 */     return getNativeFloat(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public float getFloat(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2410 */     return getFloat(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final float getFloatFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 2415 */       if (val != null) {
/* 2416 */         if (val.length() == 0) {
/* 2417 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2420 */         float f = Float.parseFloat(val);
/*      */         
/* 2422 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2423 */           (f == Float.MIN_VALUE) || (f == Float.MAX_VALUE))) {
/* 2424 */           double valAsDouble = Double.parseDouble(val);
/*      */           
/*      */ 
/*      */ 
/* 2428 */           if ((valAsDouble < 1.401298464324817E-45D - MIN_DIFF_PREC) || (valAsDouble > 3.4028234663852886E38D - MAX_DIFF_PREC)) {
/* 2429 */             throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2434 */         return f;
/*      */       }
/*      */       
/* 2437 */       return 0.0F;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2440 */         Double valueAsDouble = new Double(val);
/* 2441 */         float valueAsFloat = valueAsDouble.floatValue();
/*      */         
/* 2443 */         if (this.jdbcCompliantTruncationForReads)
/*      */         {
/* 2445 */           if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == Float.NEGATIVE_INFINITY)) || (valueAsFloat == Float.POSITIVE_INFINITY)) {
/* 2446 */             throwRangeException(valueAsDouble.toString(), columnIndex, 6);
/*      */           }
/*      */         }
/*      */         
/* 2450 */         return valueAsFloat;
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException newNfe)
/*      */       {
/* 2455 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009", getExceptionInterceptor());
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
/*      */   public int getInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2472 */     checkRowPos();
/*      */     
/* 2474 */     if (!this.isBinaryEncoded) {
/* 2475 */       int columnIndexMinusOne = columnIndex - 1;
/* 2476 */       if (this.useFastIntParsing) {
/* 2477 */         checkColumnBounds(columnIndex);
/*      */         
/* 2479 */         if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2480 */           this.wasNullFlag = true;
/*      */         } else {
/* 2482 */           this.wasNullFlag = false;
/*      */         }
/*      */         
/* 2485 */         if (this.wasNullFlag) {
/* 2486 */           return 0;
/*      */         }
/*      */         
/* 2489 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2490 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2493 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */         
/* 2495 */         if (!needsFullParse) {
/*      */           try {
/* 2497 */             return getIntWithOverflowCheck(columnIndexMinusOne);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2501 */               return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection));
/*      */ 
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/*      */ 
/* 2507 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2508 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */                 
/* 2510 */                 if ((this.connection.getJdbcCompliantTruncationForReads()) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))) {
/* 2511 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */                 }
/*      */                 
/* 2514 */                 return (int)valueAsLong;
/*      */               }
/*      */               
/* 2517 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2525 */       String val = null;
/*      */       try
/*      */       {
/* 2528 */         val = getString(columnIndex);
/*      */         
/* 2530 */         if (val != null) {
/* 2531 */           if (val.length() == 0) {
/* 2532 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 2535 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1)) {
/* 2536 */             int intVal = Integer.parseInt(val);
/*      */             
/* 2538 */             checkForIntegerTruncation(columnIndexMinusOne, null, intVal);
/*      */             
/* 2540 */             return intVal;
/*      */           }
/*      */           
/*      */ 
/* 2544 */           int intVal = parseIntAsDouble(columnIndex, val);
/*      */           
/* 2546 */           checkForIntegerTruncation(columnIndex, null, intVal);
/*      */           
/* 2548 */           return intVal;
/*      */         }
/*      */         
/* 2551 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2554 */           return parseIntAsDouble(columnIndex, val);
/*      */ 
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 2559 */           if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2560 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 2562 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))) {
/* 2563 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */             
/* 2566 */             return (int)valueAsLong;
/*      */           }
/*      */           
/* 2569 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2574 */     return getNativeInt(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getInt(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2583 */     return getInt(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final int getIntFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 2588 */       if (val != null)
/*      */       {
/* 2590 */         if (val.length() == 0) {
/* 2591 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2594 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2602 */           val = val.trim();
/*      */           
/* 2604 */           int valueAsInt = Integer.parseInt(val);
/*      */           
/* 2606 */           if ((this.jdbcCompliantTruncationForReads) && (
/* 2607 */             (valueAsInt == Integer.MIN_VALUE) || (valueAsInt == Integer.MAX_VALUE))) {
/* 2608 */             long valueAsLong = Long.parseLong(val);
/*      */             
/* 2610 */             if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)) {
/* 2611 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2616 */           return valueAsInt;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2621 */         double valueAsDouble = Double.parseDouble(val);
/*      */         
/* 2623 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2624 */           (valueAsDouble < -2.147483648E9D) || (valueAsDouble > 2.147483647E9D))) {
/* 2625 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */         
/*      */ 
/* 2629 */         return (int)valueAsDouble;
/*      */       }
/*      */       
/* 2632 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2635 */         double valueAsDouble = Double.parseDouble(val);
/*      */         
/* 2637 */         if ((this.jdbcCompliantTruncationForReads) && (
/* 2638 */           (valueAsDouble < -2.147483648E9D) || (valueAsDouble > 2.147483647E9D))) {
/* 2639 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */         
/*      */ 
/* 2643 */         return (int)valueAsDouble;
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException newNfe)
/*      */       {
/* 2648 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009", getExceptionInterceptor());
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
/*      */   public long getLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2666 */     return getLong(columnIndex, true);
/*      */   }
/*      */   
/*      */   private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
/* 2670 */     if (!this.isBinaryEncoded) {
/* 2671 */       checkRowPos();
/*      */       
/* 2673 */       int columnIndexMinusOne = columnIndex - 1;
/*      */       
/* 2675 */       if (this.useFastIntParsing)
/*      */       {
/* 2677 */         checkColumnBounds(columnIndex);
/*      */         
/* 2679 */         if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2680 */           this.wasNullFlag = true;
/*      */         } else {
/* 2682 */           this.wasNullFlag = false;
/*      */         }
/*      */         
/* 2685 */         if (this.wasNullFlag) {
/* 2686 */           return 0L;
/*      */         }
/*      */         
/* 2689 */         if (this.thisRow.length(columnIndexMinusOne) == 0L) {
/* 2690 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2693 */         boolean needsFullParse = this.thisRow.isFloatingPointNumber(columnIndexMinusOne);
/*      */         
/* 2695 */         if (!needsFullParse) {
/*      */           try {
/* 2697 */             return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 2701 */               return parseLongAsDouble(columnIndexMinusOne, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection));
/*      */ 
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/*      */ 
/* 2707 */               if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
/* 2708 */                 return getNumericRepresentationOfSQLBitType(columnIndex);
/*      */               }
/*      */               
/* 2711 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2719 */       String val = null;
/*      */       try
/*      */       {
/* 2722 */         val = getString(columnIndex);
/*      */         
/* 2724 */         if (val != null) {
/* 2725 */           if (val.length() == 0) {
/* 2726 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 2729 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 2730 */             return parseLongWithOverflowCheck(columnIndexMinusOne, null, val, overflowCheck);
/*      */           }
/*      */           
/*      */ 
/* 2734 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */         }
/*      */         
/* 2737 */         return 0L;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2740 */           return parseLongAsDouble(columnIndexMinusOne, val);
/*      */ 
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 2745 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2750 */     return getNativeLong(columnIndex, overflowCheck, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLong(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2759 */     return getLong(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException {
/*      */     try {
/* 2764 */       if (val != null)
/*      */       {
/* 2766 */         if (val.length() == 0) {
/* 2767 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 2770 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 2771 */           return parseLongWithOverflowCheck(columnIndexZeroBased, null, val, true);
/*      */         }
/*      */         
/*      */ 
/* 2775 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */       }
/*      */       
/* 2778 */       return 0L;
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/*      */       try {
/* 2782 */         return parseLongAsDouble(columnIndexZeroBased, val);
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException newNfe)
/*      */       {
/* 2787 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + (columnIndexZeroBased + 1), "S1009", getExceptionInterceptor());
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
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2803 */     checkClosed();
/*      */     
/* 2805 */     return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
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
/*      */   protected Array getNativeArray(int i)
/*      */     throws SQLException
/*      */   {
/* 2822 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   protected InputStream getNativeAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2849 */     checkRowPos();
/*      */     
/* 2851 */     return getNativeBinaryStream(columnIndex);
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
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2869 */     checkColumnBounds(columnIndex);
/*      */     
/* 2871 */     int scale = this.fields[(columnIndex - 1)].getDecimals();
/*      */     
/* 2873 */     return getNativeBigDecimal(columnIndex, scale);
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
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 2891 */     checkColumnBounds(columnIndex);
/*      */     
/* 2893 */     String stringVal = null;
/*      */     
/* 2895 */     Field f = this.fields[(columnIndex - 1)];
/*      */     
/* 2897 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 2899 */     if (value == null) {
/* 2900 */       this.wasNullFlag = true;
/*      */       
/* 2902 */       return null;
/*      */     }
/*      */     
/* 2905 */     this.wasNullFlag = false;
/*      */     
/* 2907 */     switch (f.getSQLType()) {
/*      */     case 2: 
/*      */     case 3: 
/* 2910 */       stringVal = StringUtils.toAsciiString((byte[])value);
/* 2911 */       break;
/*      */     default: 
/* 2913 */       stringVal = getNativeString(columnIndex);
/*      */     }
/*      */     
/* 2916 */     return getBigDecimalFromString(stringVal, columnIndex, scale);
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
/*      */   protected InputStream getNativeBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2937 */     checkRowPos();
/*      */     
/* 2939 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 2941 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 2942 */       this.wasNullFlag = true;
/*      */       
/* 2944 */       return null;
/*      */     }
/*      */     
/* 2947 */     this.wasNullFlag = false;
/*      */     
/* 2949 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -7: 
/*      */     case -4: 
/*      */     case -3: 
/*      */     case -2: 
/*      */     case 2004: 
/* 2955 */       return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
/*      */     }
/*      */     
/* 2958 */     byte[] b = getNativeBytes(columnIndex, false);
/*      */     
/* 2960 */     if (b != null) {
/* 2961 */       return new ByteArrayInputStream(b);
/*      */     }
/*      */     
/* 2964 */     return null;
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
/*      */   protected java.sql.Blob getNativeBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2979 */     checkRowPos();
/*      */     
/* 2981 */     checkColumnBounds(columnIndex);
/*      */     
/* 2983 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 2985 */     if (value == null) {
/* 2986 */       this.wasNullFlag = true;
/*      */     } else {
/* 2988 */       this.wasNullFlag = false;
/*      */     }
/*      */     
/* 2991 */     if (this.wasNullFlag) {
/* 2992 */       return null;
/*      */     }
/*      */     
/* 2995 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */     
/* 2997 */     byte[] dataAsBytes = null;
/*      */     
/* 2999 */     switch (mysqlType) {
/*      */     case 249: 
/*      */     case 250: 
/*      */     case 251: 
/*      */     case 252: 
/* 3004 */       dataAsBytes = (byte[])value;
/* 3005 */       break;
/*      */     
/*      */     default: 
/* 3008 */       dataAsBytes = getNativeBytes(columnIndex, false);
/*      */     }
/*      */     
/* 3011 */     if (!this.connection.getEmulateLocators()) {
/* 3012 */       return new Blob(dataAsBytes, getExceptionInterceptor());
/*      */     }
/*      */     
/* 3015 */     return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   public static boolean arraysEqual(byte[] left, byte[] right) {
/* 3019 */     if (left == null) {
/* 3020 */       return right == null;
/*      */     }
/* 3022 */     if (right == null) {
/* 3023 */       return false;
/*      */     }
/* 3025 */     if (left.length != right.length) {
/* 3026 */       return false;
/*      */     }
/* 3028 */     for (int i = 0; i < left.length; i++) {
/* 3029 */       if (left[i] != right[i]) {
/* 3030 */         return false;
/*      */       }
/*      */     }
/* 3033 */     return true;
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
/*      */   protected byte getNativeByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3048 */     return getNativeByte(columnIndex, true);
/*      */   }
/*      */   
/*      */   protected byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
/* 3052 */     checkRowPos();
/*      */     
/* 3054 */     checkColumnBounds(columnIndex);
/*      */     
/* 3056 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3058 */     if (value == null) {
/* 3059 */       this.wasNullFlag = true;
/*      */       
/* 3061 */       return 0;
/*      */     }
/*      */     
/* 3064 */     this.wasNullFlag = false;
/*      */     
/* 3066 */     columnIndex--;
/*      */     
/* 3068 */     Field field = this.fields[columnIndex];
/*      */     long valueAsLong;
/* 3070 */     short valueAsShort; switch (field.getMysqlType()) {
/*      */     case 16: 
/* 3072 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */       
/* 3074 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -128L) || (valueAsLong > 127L))) {
/* 3075 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */       
/* 3078 */       return (byte)(int)valueAsLong;
/*      */     case 1: 
/* 3080 */       byte valueAsByte = ((byte[])(byte[])value)[0];
/*      */       
/* 3082 */       if (!field.isUnsigned()) {
/* 3083 */         return valueAsByte;
/*      */       }
/*      */       
/* 3086 */       valueAsShort = valueAsByte >= 0 ? (short)valueAsByte : (short)(valueAsByte + 256);
/*      */       
/* 3088 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && 
/* 3089 */         (valueAsShort > 127)) {
/* 3090 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3094 */       return (byte)valueAsShort;
/*      */     
/*      */     case 2: 
/*      */     case 13: 
/* 3098 */       valueAsShort = getNativeShort(columnIndex + 1);
/*      */       
/* 3100 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3101 */         (valueAsShort < -128) || (valueAsShort > 127))) {
/* 3102 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3106 */       return (byte)valueAsShort;
/*      */     case 3: 
/*      */     case 9: 
/* 3109 */       int valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */       
/* 3111 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3112 */         (valueAsInt < -128) || (valueAsInt > 127))) {
/* 3113 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3117 */       return (byte)valueAsInt;
/*      */     
/*      */     case 4: 
/* 3120 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */       
/* 3122 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3123 */         (valueAsFloat < -128.0F) || (valueAsFloat > 127.0F)))
/*      */       {
/* 3125 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3129 */       return (byte)(int)valueAsFloat;
/*      */     
/*      */     case 5: 
/* 3132 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */       
/* 3134 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3135 */         (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D))) {
/* 3136 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3140 */       return (byte)(int)valueAsDouble;
/*      */     
/*      */     case 8: 
/* 3143 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */       
/* 3145 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3146 */         (valueAsLong < -128L) || (valueAsLong > 127L))) {
/* 3147 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */       
/*      */ 
/* 3151 */       return (byte)(int)valueAsLong;
/*      */     }
/*      */     
/* 3154 */     if (this.useUsageAdvisor) {
/* 3155 */       issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3160 */     return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1);
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
/*      */   protected byte[] getNativeBytes(int columnIndex, boolean noConversion)
/*      */     throws SQLException
/*      */   {
/* 3180 */     checkRowPos();
/*      */     
/* 3182 */     checkColumnBounds(columnIndex);
/*      */     
/* 3184 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 3186 */     if (value == null) {
/* 3187 */       this.wasNullFlag = true;
/*      */     } else {
/* 3189 */       this.wasNullFlag = false;
/*      */     }
/*      */     
/* 3192 */     if (this.wasNullFlag) {
/* 3193 */       return null;
/*      */     }
/*      */     
/* 3196 */     Field field = this.fields[(columnIndex - 1)];
/*      */     
/* 3198 */     int mysqlType = field.getMysqlType();
/*      */     
/*      */ 
/* 3201 */     if (noConversion) {
/* 3202 */       mysqlType = 252;
/*      */     }
/*      */     
/* 3205 */     switch (mysqlType) {
/*      */     case 16: 
/*      */     case 249: 
/*      */     case 250: 
/*      */     case 251: 
/*      */     case 252: 
/* 3211 */       return (byte[])value;
/*      */     
/*      */     case 15: 
/*      */     case 253: 
/*      */     case 254: 
/* 3216 */       if ((value instanceof byte[])) {
/* 3217 */         return (byte[])value;
/*      */       }
/*      */       
/*      */       break;
/*      */     }
/*      */     
/* 3223 */     int sqlType = field.getSQLType();
/*      */     
/* 3225 */     if ((sqlType == -3) || (sqlType == -2)) {
/* 3226 */       return (byte[])value;
/*      */     }
/*      */     
/* 3229 */     return getBytesFromString(getNativeString(columnIndex));
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
/*      */   protected Reader getNativeCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3248 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 3250 */     switch (this.fields[columnIndexMinusOne].getSQLType()) {
/*      */     case -1: 
/*      */     case 1: 
/*      */     case 12: 
/*      */     case 2005: 
/* 3255 */       if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 3256 */         this.wasNullFlag = true;
/*      */         
/* 3258 */         return null;
/*      */       }
/*      */       
/* 3261 */       this.wasNullFlag = false;
/*      */       
/* 3263 */       return this.thisRow.getReader(columnIndexMinusOne);
/*      */     }
/*      */     
/* 3266 */     String asString = getStringForClob(columnIndex);
/*      */     
/* 3268 */     if (asString == null) {
/* 3269 */       return null;
/*      */     }
/*      */     
/* 3272 */     return getCharacterStreamFromString(asString);
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
/*      */   protected java.sql.Clob getNativeClob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3287 */     String stringVal = getStringForClob(columnIndex);
/*      */     
/* 3289 */     if (stringVal == null) {
/* 3290 */       return null;
/*      */     }
/*      */     
/* 3293 */     return getClobFromString(stringVal);
/*      */   }
/*      */   
/*      */   private String getNativeConvertToString(int columnIndex, Field field) throws SQLException {
/* 3297 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 3299 */       int sqlType = field.getSQLType();
/* 3300 */       int mysqlType = field.getMysqlType();
/*      */       int intVal;
/* 3302 */       long longVal; switch (sqlType) {
/*      */       case -7: 
/* 3304 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       case 16: 
/* 3306 */         boolean booleanVal = getBoolean(columnIndex);
/*      */         
/* 3308 */         if (this.wasNullFlag) {
/* 3309 */           return null;
/*      */         }
/*      */         
/* 3312 */         return String.valueOf(booleanVal);
/*      */       
/*      */       case -6: 
/* 3315 */         byte tinyintVal = getNativeByte(columnIndex, false);
/*      */         
/* 3317 */         if (this.wasNullFlag) {
/* 3318 */           return null;
/*      */         }
/*      */         
/* 3321 */         if ((!field.isUnsigned()) || (tinyintVal >= 0)) {
/* 3322 */           return String.valueOf(tinyintVal);
/*      */         }
/*      */         
/* 3325 */         short unsignedTinyVal = (short)(tinyintVal & 0xFF);
/*      */         
/* 3327 */         return String.valueOf(unsignedTinyVal);
/*      */       
/*      */ 
/*      */       case 5: 
/* 3331 */         intVal = getNativeInt(columnIndex, false);
/*      */         
/* 3333 */         if (this.wasNullFlag) {
/* 3334 */           return null;
/*      */         }
/*      */         
/* 3337 */         if ((!field.isUnsigned()) || (intVal >= 0)) {
/* 3338 */           return String.valueOf(intVal);
/*      */         }
/*      */         
/* 3341 */         intVal &= 0xFFFF;
/*      */         
/* 3343 */         return String.valueOf(intVal);
/*      */       
/*      */       case 4: 
/* 3346 */         intVal = getNativeInt(columnIndex, false);
/*      */         
/* 3348 */         if (this.wasNullFlag) {
/* 3349 */           return null;
/*      */         }
/*      */         
/* 3352 */         if ((!field.isUnsigned()) || (intVal >= 0) || (field.getMysqlType() == 9))
/*      */         {
/* 3354 */           return String.valueOf(intVal);
/*      */         }
/*      */         
/* 3357 */         longVal = intVal & 0xFFFFFFFF;
/*      */         
/* 3359 */         return String.valueOf(longVal);
/*      */       
/*      */ 
/*      */       case -5: 
/* 3363 */         if (!field.isUnsigned()) {
/* 3364 */           longVal = getNativeLong(columnIndex, false, true);
/*      */           
/* 3366 */           if (this.wasNullFlag) {
/* 3367 */             return null;
/*      */           }
/*      */           
/* 3370 */           return String.valueOf(longVal);
/*      */         }
/*      */         
/* 3373 */         long longVal = getNativeLong(columnIndex, false, false);
/*      */         
/* 3375 */         if (this.wasNullFlag) {
/* 3376 */           return null;
/*      */         }
/*      */         
/* 3379 */         return String.valueOf(convertLongToUlong(longVal));
/*      */       case 7: 
/* 3381 */         float floatVal = getNativeFloat(columnIndex);
/*      */         
/* 3383 */         if (this.wasNullFlag) {
/* 3384 */           return null;
/*      */         }
/*      */         
/* 3387 */         return String.valueOf(floatVal);
/*      */       
/*      */       case 6: 
/*      */       case 8: 
/* 3391 */         double doubleVal = getNativeDouble(columnIndex);
/*      */         
/* 3393 */         if (this.wasNullFlag) {
/* 3394 */           return null;
/*      */         }
/*      */         
/* 3397 */         return String.valueOf(doubleVal);
/*      */       
/*      */       case 2: 
/*      */       case 3: 
/* 3401 */         String stringVal = StringUtils.toAsciiString(this.thisRow.getColumnValue(columnIndex - 1));
/*      */         
/*      */ 
/*      */ 
/* 3405 */         if (stringVal != null) {
/* 3406 */           this.wasNullFlag = false;
/*      */           
/* 3408 */           if (stringVal.length() == 0) {
/* 3409 */             BigDecimal val = new BigDecimal(0);
/*      */             
/* 3411 */             return val.toString();
/*      */           }
/*      */           BigDecimal val;
/*      */           try {
/* 3415 */             val = new BigDecimal(stringVal);
/*      */           } catch (NumberFormatException ex) {
/* 3417 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 3422 */           return val.toString();
/*      */         }
/*      */         
/* 3425 */         this.wasNullFlag = true;
/*      */         
/* 3427 */         return null;
/*      */       
/*      */ 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/* 3433 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/* 3438 */         if (!field.isBlob())
/* 3439 */           return extractStringFromNativeColumn(columnIndex, mysqlType);
/* 3440 */         if (!field.isBinary()) {
/* 3441 */           return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */         }
/* 3443 */         byte[] data = getBytes(columnIndex);
/* 3444 */         Object obj = data;
/*      */         
/* 3446 */         if ((data != null) && (data.length >= 2)) {
/* 3447 */           if ((data[0] == -84) && (data[1] == -19)) {
/*      */             try
/*      */             {
/* 3450 */               ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/* 3451 */               ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/* 3452 */               obj = objIn.readObject();
/* 3453 */               objIn.close();
/* 3454 */               bytesIn.close();
/*      */             } catch (ClassNotFoundException cnfe) {
/* 3456 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */             }
/*      */             catch (IOException ex) {
/* 3459 */               obj = data;
/*      */             }
/*      */           }
/*      */           
/* 3463 */           return obj.toString();
/*      */         }
/*      */         
/* 3466 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       case 91: 
/* 3472 */         if (mysqlType == 13) {
/* 3473 */           short shortVal = getNativeShort(columnIndex);
/*      */           
/* 3475 */           if (!this.connection.getYearIsDateType())
/*      */           {
/* 3477 */             if (this.wasNullFlag) {
/* 3478 */               return null;
/*      */             }
/*      */             
/* 3481 */             return String.valueOf(shortVal);
/*      */           }
/*      */           
/* 3484 */           if (field.getLength() == 2L)
/*      */           {
/* 3486 */             if (shortVal <= 69) {
/* 3487 */               shortVal = (short)(shortVal + 100);
/*      */             }
/*      */             
/* 3490 */             shortVal = (short)(shortVal + 1900);
/*      */           }
/*      */           
/* 3493 */           return fastDateCreate(null, shortVal, 1, 1).toString();
/*      */         }
/*      */         
/*      */ 
/* 3497 */         if (this.connection.getNoDatetimeStringSync()) {
/* 3498 */           byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */           
/* 3500 */           if (asBytes == null) {
/* 3501 */             return null;
/*      */           }
/*      */           
/* 3504 */           if (asBytes.length == 0)
/*      */           {
/*      */ 
/*      */ 
/* 3508 */             return "0000-00-00";
/*      */           }
/*      */           
/* 3511 */           int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/* 3512 */           int month = asBytes[2];
/* 3513 */           int day = asBytes[3];
/*      */           
/* 3515 */           if ((year == 0) && (month == 0) && (day == 0)) {
/* 3516 */             return "0000-00-00";
/*      */           }
/*      */         }
/*      */         
/* 3520 */         Date dt = getNativeDate(columnIndex);
/*      */         
/* 3522 */         if (dt == null) {
/* 3523 */           return null;
/*      */         }
/*      */         
/* 3526 */         return String.valueOf(dt);
/*      */       
/*      */       case 92: 
/* 3529 */         Time tm = getNativeTime(columnIndex, null, this.connection.getDefaultTimeZone(), false);
/*      */         
/* 3531 */         if (tm == null) {
/* 3532 */           return null;
/*      */         }
/*      */         
/* 3535 */         return String.valueOf(tm);
/*      */       
/*      */       case 93: 
/* 3538 */         if (this.connection.getNoDatetimeStringSync()) {
/* 3539 */           byte[] asBytes = getNativeBytes(columnIndex, true);
/*      */           
/* 3541 */           if (asBytes == null) {
/* 3542 */             return null;
/*      */           }
/*      */           
/* 3545 */           if (asBytes.length == 0)
/*      */           {
/*      */ 
/*      */ 
/* 3549 */             return "0000-00-00 00:00:00";
/*      */           }
/*      */           
/* 3552 */           int year = asBytes[0] & 0xFF | (asBytes[1] & 0xFF) << 8;
/* 3553 */           int month = asBytes[2];
/* 3554 */           int day = asBytes[3];
/*      */           
/* 3556 */           if ((year == 0) && (month == 0) && (day == 0)) {
/* 3557 */             return "0000-00-00 00:00:00";
/*      */           }
/*      */         }
/*      */         
/* 3561 */         Timestamp tstamp = getNativeTimestamp(columnIndex, null, this.connection.getDefaultTimeZone(), false);
/*      */         
/* 3563 */         if (tstamp == null) {
/* 3564 */           return null;
/*      */         }
/*      */         
/* 3567 */         String result = String.valueOf(tstamp);
/*      */         
/* 3569 */         if (!this.connection.getNoDatetimeStringSync()) {
/* 3570 */           return result;
/*      */         }
/*      */         
/* 3573 */         if (result.endsWith(".0")) {
/* 3574 */           return result.substring(0, result.length() - 2);
/*      */         }
/* 3576 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       }
/*      */       
/* 3579 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
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
/*      */   protected Date getNativeDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3596 */     return getNativeDate(columnIndex, null);
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
/*      */   protected Date getNativeDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3616 */     checkRowPos();
/* 3617 */     checkColumnBounds(columnIndex);
/*      */     
/* 3619 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 3621 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 3623 */     Date dateToReturn = null;
/*      */     
/* 3625 */     if (mysqlType == 10)
/*      */     {
/* 3627 */       dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this, cal);
/*      */     } else {
/* 3629 */       TimeZone tz = cal != null ? cal.getTimeZone() : getDefaultTimeZone();
/*      */       
/* 3631 */       boolean rollForward = (tz != null) && (!tz.equals(getDefaultTimeZone()));
/*      */       
/* 3633 */       dateToReturn = (Date)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 91, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3641 */     if (dateToReturn == null)
/*      */     {
/* 3643 */       this.wasNullFlag = true;
/*      */       
/* 3645 */       return null;
/*      */     }
/*      */     
/* 3648 */     this.wasNullFlag = false;
/*      */     
/* 3650 */     return dateToReturn;
/*      */   }
/*      */   
/*      */   Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
/* 3654 */     if (this.useUsageAdvisor) {
/* 3655 */       issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 10 });
/*      */     }
/*      */     
/*      */ 
/* 3659 */     String stringVal = getNativeString(columnIndex);
/*      */     
/* 3661 */     return getDateFromString(stringVal, columnIndex, null);
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
/*      */   protected double getNativeDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3676 */     checkRowPos();
/* 3677 */     checkColumnBounds(columnIndex);
/*      */     
/* 3679 */     columnIndex--;
/*      */     
/* 3681 */     if (this.thisRow.isNull(columnIndex)) {
/* 3682 */       this.wasNullFlag = true;
/*      */       
/* 3684 */       return 0.0D;
/*      */     }
/*      */     
/* 3687 */     this.wasNullFlag = false;
/*      */     
/* 3689 */     Field f = this.fields[columnIndex];
/*      */     
/* 3691 */     switch (f.getMysqlType()) {
/*      */     case 5: 
/* 3693 */       return this.thisRow.getNativeDouble(columnIndex);
/*      */     case 1: 
/* 3695 */       if (!f.isUnsigned()) {
/* 3696 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */       
/* 3699 */       return getNativeShort(columnIndex + 1);
/*      */     case 2: 
/*      */     case 13: 
/* 3702 */       if (!f.isUnsigned()) {
/* 3703 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */       
/* 3706 */       return getNativeInt(columnIndex + 1);
/*      */     case 3: 
/*      */     case 9: 
/* 3709 */       if (!f.isUnsigned()) {
/* 3710 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */       
/* 3713 */       return getNativeLong(columnIndex + 1);
/*      */     case 8: 
/* 3715 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */       
/* 3717 */       if (!f.isUnsigned()) {
/* 3718 */         return valueAsLong;
/*      */       }
/*      */       
/* 3721 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */       
/*      */ 
/*      */ 
/* 3725 */       return asBigInt.doubleValue();
/*      */     case 4: 
/* 3727 */       return getNativeFloat(columnIndex + 1);
/*      */     case 16: 
/* 3729 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     }
/* 3731 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 3733 */     if (this.useUsageAdvisor) {
/* 3734 */       issueConversionViaParsingWarning("getDouble()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3739 */     return getDoubleFromString(stringVal, columnIndex + 1);
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
/*      */   protected float getNativeFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3755 */     checkRowPos();
/* 3756 */     checkColumnBounds(columnIndex);
/*      */     
/* 3758 */     columnIndex--;
/*      */     
/* 3760 */     if (this.thisRow.isNull(columnIndex)) {
/* 3761 */       this.wasNullFlag = true;
/*      */       
/* 3763 */       return 0.0F;
/*      */     }
/*      */     
/* 3766 */     this.wasNullFlag = false;
/*      */     
/* 3768 */     Field f = this.fields[columnIndex];
/*      */     long valueAsLong;
/* 3770 */     switch (f.getMysqlType()) {
/*      */     case 16: 
/* 3772 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */       
/* 3774 */       return (float)valueAsLong;
/*      */     
/*      */ 
/*      */ 
/*      */     case 5: 
/* 3779 */       Double valueAsDouble = new Double(getNativeDouble(columnIndex + 1));
/*      */       
/* 3781 */       float valueAsFloat = valueAsDouble.floatValue();
/*      */       
/* 3783 */       if (((this.jdbcCompliantTruncationForReads) && (valueAsFloat == Float.NEGATIVE_INFINITY)) || (valueAsFloat == Float.POSITIVE_INFINITY)) {
/* 3784 */         throwRangeException(valueAsDouble.toString(), columnIndex + 1, 6);
/*      */       }
/*      */       
/* 3787 */       return (float)getNativeDouble(columnIndex + 1);
/*      */     case 1: 
/* 3789 */       if (!f.isUnsigned()) {
/* 3790 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */       
/* 3793 */       return getNativeShort(columnIndex + 1);
/*      */     case 2: 
/*      */     case 13: 
/* 3796 */       if (!f.isUnsigned()) {
/* 3797 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */       
/* 3800 */       return getNativeInt(columnIndex + 1);
/*      */     case 3: 
/*      */     case 9: 
/* 3803 */       if (!f.isUnsigned()) {
/* 3804 */         return getNativeInt(columnIndex + 1);
/*      */       }
/*      */       
/* 3807 */       return (float)getNativeLong(columnIndex + 1);
/*      */     case 8: 
/* 3809 */       valueAsLong = getNativeLong(columnIndex + 1);
/*      */       
/* 3811 */       if (!f.isUnsigned()) {
/* 3812 */         return (float)valueAsLong;
/*      */       }
/*      */       
/* 3815 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */       
/*      */ 
/*      */ 
/* 3819 */       return asBigInt.floatValue();
/*      */     
/*      */     case 4: 
/* 3822 */       return this.thisRow.getNativeFloat(columnIndex);
/*      */     }
/*      */     
/* 3825 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 3827 */     if (this.useUsageAdvisor) {
/* 3828 */       issueConversionViaParsingWarning("getFloat()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3833 */     return getFloatFromString(stringVal, columnIndex + 1);
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
/*      */   protected int getNativeInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3849 */     return getNativeInt(columnIndex, true);
/*      */   }
/*      */   
/*      */   protected int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
/* 3853 */     checkRowPos();
/* 3854 */     checkColumnBounds(columnIndex);
/*      */     
/* 3856 */     columnIndex--;
/*      */     
/* 3858 */     if (this.thisRow.isNull(columnIndex)) {
/* 3859 */       this.wasNullFlag = true;
/*      */       
/* 3861 */       return 0;
/*      */     }
/*      */     
/* 3864 */     this.wasNullFlag = false;
/*      */     
/* 3866 */     Field f = this.fields[columnIndex];
/*      */     long valueAsLong;
/* 3868 */     double valueAsDouble; switch (f.getMysqlType()) {
/*      */     case 16: 
/* 3870 */       valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */       
/* 3872 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))) {
/* 3873 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */       
/* 3876 */       return (short)(int)valueAsLong;
/*      */     case 1: 
/* 3878 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */       
/* 3880 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 3881 */         return tinyintVal;
/*      */       }
/*      */       
/* 3884 */       return tinyintVal + 256;
/*      */     case 2: 
/*      */     case 13: 
/* 3887 */       short asShort = getNativeShort(columnIndex + 1, false);
/*      */       
/* 3889 */       if ((!f.isUnsigned()) || (asShort >= 0)) {
/* 3890 */         return asShort;
/*      */       }
/*      */       
/* 3893 */       return asShort + 65536;
/*      */     
/*      */     case 3: 
/*      */     case 9: 
/* 3897 */       int valueAsInt = this.thisRow.getNativeInt(columnIndex);
/*      */       
/* 3899 */       if (!f.isUnsigned()) {
/* 3900 */         return valueAsInt;
/*      */       }
/*      */       
/* 3903 */       valueAsLong = valueAsInt >= 0 ? valueAsInt : valueAsInt + 4294967296L;
/*      */       
/* 3905 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 2147483647L)) {
/* 3906 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */       
/* 3909 */       return (int)valueAsLong;
/*      */     case 8: 
/* 3911 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */       
/* 3913 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3914 */         (valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))) {
/* 3915 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */       
/*      */ 
/* 3919 */       return (int)valueAsLong;
/*      */     case 5: 
/* 3921 */       valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */       
/* 3923 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3924 */         (valueAsDouble < -2.147483648E9D) || (valueAsDouble > 2.147483647E9D))) {
/* 3925 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */       
/*      */ 
/* 3929 */       return (int)valueAsDouble;
/*      */     case 4: 
/* 3931 */       valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */       
/* 3933 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 3934 */         (valueAsDouble < -2.147483648E9D) || (valueAsDouble > 2.147483647E9D))) {
/* 3935 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */       
/*      */ 
/* 3939 */       return (int)valueAsDouble;
/*      */     }
/*      */     
/* 3942 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 3944 */     if (this.useUsageAdvisor) {
/* 3945 */       issueConversionViaParsingWarning("getInt()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3950 */     return getIntFromString(stringVal, columnIndex + 1);
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
/*      */   protected long getNativeLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3966 */     return getNativeLong(columnIndex, true, true);
/*      */   }
/*      */   
/*      */   protected long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException {
/* 3970 */     checkRowPos();
/* 3971 */     checkColumnBounds(columnIndex);
/*      */     
/* 3973 */     columnIndex--;
/*      */     
/* 3975 */     if (this.thisRow.isNull(columnIndex)) {
/* 3976 */       this.wasNullFlag = true;
/*      */       
/* 3978 */       return 0L;
/*      */     }
/*      */     
/* 3981 */     this.wasNullFlag = false;
/*      */     
/* 3983 */     Field f = this.fields[columnIndex];
/*      */     double valueAsDouble;
/* 3985 */     switch (f.getMysqlType()) {
/*      */     case 16: 
/* 3987 */       return getNumericRepresentationOfSQLBitType(columnIndex + 1);
/*      */     case 1: 
/* 3989 */       if (!f.isUnsigned()) {
/* 3990 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */       
/* 3993 */       return getNativeInt(columnIndex + 1);
/*      */     case 2: 
/* 3995 */       if (!f.isUnsigned()) {
/* 3996 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */       
/* 3999 */       return getNativeInt(columnIndex + 1, false);
/*      */     
/*      */     case 13: 
/* 4002 */       return getNativeShort(columnIndex + 1);
/*      */     case 3: 
/*      */     case 9: 
/* 4005 */       int asInt = getNativeInt(columnIndex + 1, false);
/*      */       
/* 4007 */       if ((!f.isUnsigned()) || (asInt >= 0)) {
/* 4008 */         return asInt;
/*      */       }
/*      */       
/* 4011 */       return asInt + 4294967296L;
/*      */     case 8: 
/* 4013 */       long valueAsLong = this.thisRow.getNativeLong(columnIndex);
/*      */       
/* 4015 */       if ((!f.isUnsigned()) || (!expandUnsignedLong)) {
/* 4016 */         return valueAsLong;
/*      */       }
/*      */       
/* 4019 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */       
/* 4021 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) < 0)))
/*      */       {
/* 4023 */         throwRangeException(asBigInt.toString(), columnIndex + 1, -5);
/*      */       }
/*      */       
/* 4026 */       return getLongFromString(asBigInt.toString(), columnIndex);
/*      */     
/*      */     case 5: 
/* 4029 */       valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */       
/* 4031 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4032 */         (valueAsDouble < -9.223372036854776E18D) || (valueAsDouble > 9.223372036854776E18D))) {
/* 4033 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */       
/*      */ 
/* 4037 */       return valueAsDouble;
/*      */     case 4: 
/* 4039 */       valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */       
/* 4041 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4042 */         (valueAsDouble < -9.223372036854776E18D) || (valueAsDouble > 9.223372036854776E18D))) {
/* 4043 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */       
/*      */ 
/* 4047 */       return valueAsDouble;
/*      */     }
/* 4049 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4051 */     if (this.useUsageAdvisor) {
/* 4052 */       issueConversionViaParsingWarning("getLong()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4057 */     return getLongFromString(stringVal, columnIndex + 1);
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
/*      */   protected Ref getNativeRef(int i)
/*      */     throws SQLException
/*      */   {
/* 4074 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   protected short getNativeShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4089 */     return getNativeShort(columnIndex, true);
/*      */   }
/*      */   
/*      */   protected short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
/* 4093 */     checkRowPos();
/* 4094 */     checkColumnBounds(columnIndex);
/*      */     
/* 4096 */     columnIndex--;
/*      */     
/* 4098 */     if (this.thisRow.isNull(columnIndex)) {
/* 4099 */       this.wasNullFlag = true;
/*      */       
/* 4101 */       return 0;
/*      */     }
/*      */     
/* 4104 */     this.wasNullFlag = false;
/*      */     
/* 4106 */     Field f = this.fields[columnIndex];
/*      */     int valueAsInt;
/* 4108 */     long valueAsLong; switch (f.getMysqlType())
/*      */     {
/*      */     case 1: 
/* 4111 */       byte tinyintVal = getNativeByte(columnIndex + 1, false);
/*      */       
/* 4113 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 4114 */         return (short)tinyintVal;
/*      */       }
/*      */       
/* 4117 */       return (short)(tinyintVal + 256);
/*      */     
/*      */     case 2: 
/*      */     case 13: 
/* 4121 */       short asShort = this.thisRow.getNativeShort(columnIndex);
/*      */       
/* 4123 */       if (!f.isUnsigned()) {
/* 4124 */         return asShort;
/*      */       }
/*      */       
/* 4127 */       valueAsInt = asShort & 0xFFFF;
/*      */       
/* 4129 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767)) {
/* 4130 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */       }
/*      */       
/* 4133 */       return (short)valueAsInt;
/*      */     case 3: 
/*      */     case 9: 
/* 4136 */       if (!f.isUnsigned()) {
/* 4137 */         valueAsInt = getNativeInt(columnIndex + 1, false);
/*      */         
/* 4139 */         if (((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsInt > 32767)) || (valueAsInt < 32768)) {
/* 4140 */           throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, 5);
/*      */         }
/*      */         
/* 4143 */         return (short)valueAsInt;
/*      */       }
/*      */       
/* 4146 */       valueAsLong = getNativeLong(columnIndex + 1, false, true);
/*      */       
/* 4148 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (valueAsLong > 32767L)) {
/* 4149 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */       }
/*      */       
/* 4152 */       return (short)(int)valueAsLong;
/*      */     
/*      */     case 8: 
/* 4155 */       valueAsLong = getNativeLong(columnIndex + 1, false, false);
/*      */       
/* 4157 */       if (!f.isUnsigned()) {
/* 4158 */         if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4159 */           (valueAsLong < -32768L) || (valueAsLong > 32767L))) {
/* 4160 */           throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 5);
/*      */         }
/*      */         
/*      */ 
/* 4164 */         return (short)(int)valueAsLong;
/*      */       }
/*      */       
/* 4167 */       BigInteger asBigInt = convertLongToUlong(valueAsLong);
/*      */       
/* 4169 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && ((asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0) || (asBigInt.compareTo(new BigInteger(String.valueOf(32768))) < 0)))
/*      */       {
/* 4171 */         throwRangeException(asBigInt.toString(), columnIndex + 1, 5);
/*      */       }
/*      */       
/* 4174 */       return (short)getIntFromString(asBigInt.toString(), columnIndex + 1);
/*      */     
/*      */     case 5: 
/* 4177 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */       
/* 4179 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4180 */         (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D))) {
/* 4181 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 5);
/*      */       }
/*      */       
/*      */ 
/* 4185 */       return (short)(int)valueAsDouble;
/*      */     case 4: 
/* 4187 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */       
/* 4189 */       if ((overflowCheck) && (this.jdbcCompliantTruncationForReads) && (
/* 4190 */         (valueAsFloat < -32768.0F) || (valueAsFloat > 32767.0F))) {
/* 4191 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, 5);
/*      */       }
/*      */       
/*      */ 
/* 4195 */       return (short)(int)valueAsFloat;
/*      */     }
/* 4197 */     String stringVal = getNativeString(columnIndex + 1);
/*      */     
/* 4199 */     if (this.useUsageAdvisor) {
/* 4200 */       issueConversionViaParsingWarning("getShort()", columnIndex, stringVal, this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4205 */     return getShortFromString(stringVal, columnIndex + 1);
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
/*      */   protected String getNativeString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4221 */     checkRowPos();
/* 4222 */     checkColumnBounds(columnIndex);
/*      */     
/* 4224 */     if (this.fields == null) {
/* 4225 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 4229 */     if (this.thisRow.isNull(columnIndex - 1)) {
/* 4230 */       this.wasNullFlag = true;
/*      */       
/* 4232 */       return null;
/*      */     }
/*      */     
/* 4235 */     this.wasNullFlag = false;
/*      */     
/* 4237 */     String stringVal = null;
/*      */     
/* 4239 */     Field field = this.fields[(columnIndex - 1)];
/*      */     
/*      */ 
/* 4242 */     stringVal = getNativeConvertToString(columnIndex, field);
/* 4243 */     int mysqlType = field.getMysqlType();
/*      */     
/* 4245 */     if ((mysqlType != 7) && (mysqlType != 10) && (field.isZeroFill()) && (stringVal != null)) {
/* 4246 */       int origLength = stringVal.length();
/*      */       
/* 4248 */       StringBuilder zeroFillBuf = new StringBuilder(origLength);
/*      */       
/* 4250 */       long numZeros = field.getLength() - origLength;
/*      */       
/* 4252 */       for (long i = 0L; i < numZeros; i += 1L) {
/* 4253 */         zeroFillBuf.append('0');
/*      */       }
/*      */       
/* 4256 */       zeroFillBuf.append(stringVal);
/*      */       
/* 4258 */       stringVal = zeroFillBuf.toString();
/*      */     }
/*      */     
/* 4261 */     return stringVal;
/*      */   }
/*      */   
/*      */   private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4265 */     checkRowPos();
/* 4266 */     checkColumnBounds(columnIndex);
/*      */     
/* 4268 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4270 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 4272 */     Time timeVal = null;
/*      */     
/* 4274 */     if (mysqlType == 11) {
/* 4275 */       timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */     else {
/* 4278 */       timeVal = (Time)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 92, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4286 */     if (timeVal == null)
/*      */     {
/* 4288 */       this.wasNullFlag = true;
/*      */       
/* 4290 */       return null;
/*      */     }
/*      */     
/* 4293 */     this.wasNullFlag = false;
/*      */     
/* 4295 */     return timeVal;
/*      */   }
/*      */   
/*      */   Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4299 */     if (this.useUsageAdvisor) {
/* 4300 */       issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 11 });
/*      */     }
/*      */     
/*      */ 
/* 4304 */     String strTime = getNativeString(columnIndex);
/*      */     
/* 4306 */     return getTimeFromString(strTime, targetCalendar, columnIndex, tz, rollForward);
/*      */   }
/*      */   
/*      */   private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4310 */     checkRowPos();
/* 4311 */     checkColumnBounds(columnIndex);
/*      */     
/* 4313 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4315 */     Timestamp tsVal = null;
/*      */     
/* 4317 */     int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
/*      */     
/* 4319 */     switch (mysqlType) {
/*      */     case 7: 
/*      */     case 12: 
/* 4322 */       tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
/* 4323 */       break;
/*      */     
/*      */ 
/*      */     default: 
/* 4327 */       tsVal = (Timestamp)this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, null, 93, mysqlType, tz, rollForward, this.connection, this);
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4336 */     if (tsVal == null)
/*      */     {
/* 4338 */       this.wasNullFlag = true;
/*      */       
/* 4340 */       return null;
/*      */     }
/*      */     
/* 4343 */     this.wasNullFlag = false;
/*      */     
/* 4345 */     return tsVal;
/*      */   }
/*      */   
/*      */   Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 4349 */     if (this.useUsageAdvisor) {
/* 4350 */       issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[(columnIndex - 1)], new int[] { 7, 12 });
/*      */     }
/*      */     
/*      */ 
/* 4354 */     String strTimestamp = getNativeString(columnIndex);
/*      */     
/* 4356 */     return getTimestampFromString(columnIndex, targetCalendar, strTimestamp, tz, rollForward);
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
/*      */   protected InputStream getNativeUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4381 */     checkRowPos();
/*      */     
/* 4383 */     return getBinaryStream(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */   protected URL getNativeURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 4390 */     String val = getString(colIndex);
/*      */     
/* 4392 */     if (val == null) {
/* 4393 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 4397 */       return new URL(val);
/*      */     } catch (MalformedURLException mfe) {
/* 4399 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized ResultSetInternalMethods getNextResultSet()
/*      */   {
/* 4408 */     return this.nextResultSet;
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
/*      */   public Object getObject(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4432 */     checkRowPos();
/* 4433 */     checkColumnBounds(columnIndex);
/*      */     
/* 4435 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 4437 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 4438 */       this.wasNullFlag = true;
/*      */       
/* 4440 */       return null;
/*      */     }
/*      */     
/* 4443 */     this.wasNullFlag = false;
/*      */     
/*      */ 
/* 4446 */     Field field = this.fields[columnIndexMinusOne];
/*      */     String stringVal;
/* 4448 */     switch (field.getSQLType()) {
/*      */     case -7: 
/*      */     case 16: 
/* 4451 */       if ((field.getMysqlType() == 16) && (!field.isSingleBit())) {
/* 4452 */         return getBytes(columnIndex);
/*      */       }
/*      */       
/*      */ 
/* 4456 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     
/*      */     case -6: 
/* 4459 */       if (!field.isUnsigned()) {
/* 4460 */         return Integer.valueOf(getByte(columnIndex));
/*      */       }
/*      */       
/* 4463 */       return Integer.valueOf(getInt(columnIndex));
/*      */     
/*      */ 
/*      */     case 5: 
/* 4467 */       return Integer.valueOf(getInt(columnIndex));
/*      */     
/*      */ 
/*      */     case 4: 
/* 4471 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9)) {
/* 4472 */         return Integer.valueOf(getInt(columnIndex));
/*      */       }
/*      */       
/* 4475 */       return Long.valueOf(getLong(columnIndex));
/*      */     
/*      */ 
/*      */     case -5: 
/* 4479 */       if (!field.isUnsigned()) {
/* 4480 */         return Long.valueOf(getLong(columnIndex));
/*      */       }
/*      */       
/* 4483 */       stringVal = getString(columnIndex);
/*      */       
/* 4485 */       if (stringVal == null) {
/* 4486 */         return null;
/*      */       }
/*      */       try
/*      */       {
/* 4490 */         return new BigInteger(stringVal);
/*      */       } catch (NumberFormatException nfe) {
/* 4492 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { Integer.valueOf(columnIndex), stringVal }), "S1009", getExceptionInterceptor());
/*      */       }
/*      */     
/*      */ 
/*      */ 
/*      */     case 2: 
/*      */     case 3: 
/* 4499 */       stringVal = getString(columnIndex);
/*      */       
/*      */ 
/*      */ 
/* 4503 */       if (stringVal != null) {
/* 4504 */         if (stringVal.length() == 0) {
/* 4505 */           BigDecimal val = new BigDecimal(0);
/*      */           
/* 4507 */           return val;
/*      */         }
/*      */         BigDecimal val;
/*      */         try {
/* 4511 */           val = new BigDecimal(stringVal);
/*      */         } catch (NumberFormatException ex) {
/* 4513 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4518 */         return val;
/*      */       }
/*      */       
/* 4521 */       return null;
/*      */     
/*      */     case 7: 
/* 4524 */       return new Float(getFloat(columnIndex));
/*      */     
/*      */     case 6: 
/*      */     case 8: 
/* 4528 */       return new Double(getDouble(columnIndex));
/*      */     
/*      */     case 1: 
/*      */     case 12: 
/* 4532 */       if (!field.isOpaqueBinary()) {
/* 4533 */         return getString(columnIndex);
/*      */       }
/*      */       
/* 4536 */       return getBytes(columnIndex);
/*      */     case -1: 
/* 4538 */       if (!field.isOpaqueBinary()) {
/* 4539 */         return getStringForClob(columnIndex);
/*      */       }
/*      */       
/* 4542 */       return getBytes(columnIndex);
/*      */     
/*      */     case -4: 
/*      */     case -3: 
/*      */     case -2: 
/* 4547 */       if (field.getMysqlType() == 255)
/* 4548 */         return getBytes(columnIndex);
/* 4549 */       if ((field.isBinary()) || (field.isBlob())) {
/* 4550 */         byte[] data = getBytes(columnIndex);
/*      */         
/* 4552 */         if (this.connection.getAutoDeserialize()) {
/* 4553 */           Object obj = data;
/*      */           
/* 4555 */           if ((data != null) && (data.length >= 2)) {
/* 4556 */             if ((data[0] == -84) && (data[1] == -19)) {
/*      */               try
/*      */               {
/* 4559 */                 ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/* 4560 */                 ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/* 4561 */                 obj = objIn.readObject();
/* 4562 */                 objIn.close();
/* 4563 */                 bytesIn.close();
/*      */               } catch (ClassNotFoundException cnfe) {
/* 4565 */                 throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
/*      */               }
/*      */               catch (IOException ex) {
/* 4568 */                 obj = data;
/*      */               }
/*      */             } else {
/* 4571 */               return getString(columnIndex);
/*      */             }
/*      */           }
/*      */           
/* 4575 */           return obj;
/*      */         }
/*      */         
/* 4578 */         return data;
/*      */       }
/*      */       
/* 4581 */       return getBytes(columnIndex);
/*      */     
/*      */     case 91: 
/* 4584 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType())) {
/* 4585 */         return Short.valueOf(getShort(columnIndex));
/*      */       }
/*      */       
/* 4588 */       return getDate(columnIndex);
/*      */     
/*      */     case 92: 
/* 4591 */       return getTime(columnIndex);
/*      */     
/*      */     case 93: 
/* 4594 */       return getTimestamp(columnIndex);
/*      */     }
/*      */     
/* 4597 */     return getString(columnIndex);
/*      */   }
/*      */   
/*      */   public <T> T getObject(int columnIndex, Class<T> type)
/*      */     throws SQLException
/*      */   {
/* 4603 */     if (type == null) {
/* 4604 */       throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 4607 */     if (type.equals(String.class))
/* 4608 */       return getString(columnIndex);
/* 4609 */     if (type.equals(BigDecimal.class))
/* 4610 */       return getBigDecimal(columnIndex);
/* 4611 */     if ((type.equals(Boolean.class)) || (type.equals(Boolean.TYPE)))
/* 4612 */       return Boolean.valueOf(getBoolean(columnIndex));
/* 4613 */     if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE)))
/* 4614 */       return Integer.valueOf(getInt(columnIndex));
/* 4615 */     if ((type.equals(Long.class)) || (type.equals(Long.TYPE)))
/* 4616 */       return Long.valueOf(getLong(columnIndex));
/* 4617 */     if ((type.equals(Float.class)) || (type.equals(Float.TYPE)))
/* 4618 */       return Float.valueOf(getFloat(columnIndex));
/* 4619 */     if ((type.equals(Double.class)) || (type.equals(Double.TYPE)))
/* 4620 */       return Double.valueOf(getDouble(columnIndex));
/* 4621 */     if (type.equals(byte[].class))
/* 4622 */       return getBytes(columnIndex);
/* 4623 */     if (type.equals(Date.class))
/* 4624 */       return getDate(columnIndex);
/* 4625 */     if (type.equals(Time.class))
/* 4626 */       return getTime(columnIndex);
/* 4627 */     if (type.equals(Timestamp.class))
/* 4628 */       return getTimestamp(columnIndex);
/* 4629 */     if (type.equals(Clob.class))
/* 4630 */       return getClob(columnIndex);
/* 4631 */     if (type.equals(Blob.class))
/* 4632 */       return getBlob(columnIndex);
/* 4633 */     if (type.equals(Array.class))
/* 4634 */       return getArray(columnIndex);
/* 4635 */     if (type.equals(Ref.class))
/* 4636 */       return getRef(columnIndex);
/* 4637 */     if (type.equals(URL.class)) {
/* 4638 */       return getURL(columnIndex);
/*      */     }
/* 4640 */     if (this.connection.getAutoDeserialize()) {
/*      */       try {
/* 4642 */         return (T)type.cast(getObject(columnIndex));
/*      */       } catch (ClassCastException cce) {
/* 4644 */         SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
/*      */         
/* 4646 */         sqlEx.initCause(cce);
/*      */         
/* 4648 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */     
/* 4652 */     throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), "S1009", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */   public <T> T getObject(String columnLabel, Class<T> type)
/*      */     throws SQLException
/*      */   {
/* 4659 */     return (T)getObject(findColumn(columnLabel), type);
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
/*      */   public Object getObject(int i, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 4678 */     return getObject(i);
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
/*      */   public Object getObject(String columnName)
/*      */     throws SQLException
/*      */   {
/* 4702 */     return getObject(findColumn(columnName));
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
/*      */   public Object getObject(String colName, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 4721 */     return getObject(findColumn(colName), map);
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
/* 4725 */     checkRowPos();
/* 4726 */     checkColumnBounds(columnIndex);
/*      */     
/* 4728 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 4730 */     if (value == null) {
/* 4731 */       this.wasNullFlag = true;
/*      */       
/* 4733 */       return null;
/*      */     }
/*      */     
/* 4736 */     this.wasNullFlag = false;
/*      */     
/*      */ 
/* 4739 */     Field field = this.fields[(columnIndex - 1)];
/*      */     
/* 4741 */     switch (desiredSqlType)
/*      */     {
/*      */     case -7: 
/*      */     case 16: 
/* 4745 */       return Boolean.valueOf(getBoolean(columnIndex));
/*      */     
/*      */     case -6: 
/* 4748 */       return Integer.valueOf(getInt(columnIndex));
/*      */     
/*      */     case 5: 
/* 4751 */       return Integer.valueOf(getInt(columnIndex));
/*      */     
/*      */ 
/*      */     case 4: 
/* 4755 */       if ((!field.isUnsigned()) || (field.getMysqlType() == 9)) {
/* 4756 */         return Integer.valueOf(getInt(columnIndex));
/*      */       }
/*      */       
/* 4759 */       return Long.valueOf(getLong(columnIndex));
/*      */     
/*      */ 
/*      */     case -5: 
/* 4763 */       if (field.isUnsigned()) {
/* 4764 */         return getBigDecimal(columnIndex);
/*      */       }
/*      */       
/* 4767 */       return Long.valueOf(getLong(columnIndex));
/*      */     
/*      */ 
/*      */     case 2: 
/*      */     case 3: 
/* 4772 */       String stringVal = getString(columnIndex);
/*      */       
/*      */ 
/* 4775 */       if (stringVal != null) {
/* 4776 */         if (stringVal.length() == 0) {
/* 4777 */           BigDecimal val = new BigDecimal(0);
/*      */           
/* 4779 */           return val;
/*      */         }
/*      */         BigDecimal val;
/*      */         try {
/* 4783 */           val = new BigDecimal(stringVal);
/*      */         } catch (NumberFormatException ex) {
/* 4785 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, Integer.valueOf(columnIndex) }), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4790 */         return val;
/*      */       }
/*      */       
/* 4793 */       return null;
/*      */     
/*      */     case 7: 
/* 4796 */       return new Float(getFloat(columnIndex));
/*      */     
/*      */ 
/*      */     case 6: 
/* 4800 */       if (!this.connection.getRunningCTS13()) {
/* 4801 */         return new Double(getFloat(columnIndex));
/*      */       }
/* 4803 */       return new Float(getFloat(columnIndex));
/*      */     
/*      */ 
/*      */     case 8: 
/* 4807 */       return new Double(getDouble(columnIndex));
/*      */     
/*      */     case 1: 
/*      */     case 12: 
/* 4811 */       return getString(columnIndex);
/*      */     case -1: 
/* 4813 */       return getStringForClob(columnIndex);
/*      */     case -4: 
/*      */     case -3: 
/*      */     case -2: 
/* 4817 */       return getBytes(columnIndex);
/*      */     
/*      */     case 91: 
/* 4820 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType())) {
/* 4821 */         return Short.valueOf(getShort(columnIndex));
/*      */       }
/*      */       
/* 4824 */       return getDate(columnIndex);
/*      */     
/*      */     case 92: 
/* 4827 */       return getTime(columnIndex);
/*      */     
/*      */     case 93: 
/* 4830 */       return getTimestamp(columnIndex);
/*      */     }
/*      */     
/* 4833 */     return getString(columnIndex);
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException
/*      */   {
/* 4838 */     return getObjectStoredProc(i, desiredSqlType);
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
/* 4842 */     return getObjectStoredProc(findColumn(columnName), desiredSqlType);
/*      */   }
/*      */   
/*      */   public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
/* 4846 */     return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
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
/*      */   public Ref getRef(int i)
/*      */     throws SQLException
/*      */   {
/* 4862 */     checkColumnBounds(i);
/* 4863 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public Ref getRef(String colName)
/*      */     throws SQLException
/*      */   {
/* 4879 */     return getRef(findColumn(colName));
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
/*      */   public int getRow()
/*      */     throws SQLException
/*      */   {
/* 4895 */     checkClosed();
/*      */     
/* 4897 */     int currentRowNumber = this.rowData.getCurrentRowNumber();
/* 4898 */     int row = 0;
/*      */     
/*      */ 
/* 4901 */     if (!this.rowData.isDynamic()) {
/* 4902 */       if ((currentRowNumber < 0) || (this.rowData.isAfterLast()) || (this.rowData.isEmpty())) {
/* 4903 */         row = 0;
/*      */       } else {
/* 4905 */         row = currentRowNumber + 1;
/*      */       }
/*      */     }
/*      */     else {
/* 4909 */       row = currentRowNumber + 1;
/*      */     }
/*      */     
/* 4912 */     return row;
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
/*      */   private long getNumericRepresentationOfSQLBitType(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4932 */     Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */     
/* 4934 */     if ((this.fields[(columnIndex - 1)].isSingleBit()) || (((byte[])value).length == 1)) {
/* 4935 */       return ((byte[])(byte[])value)[0];
/*      */     }
/*      */     
/* 4938 */     byte[] asBytes = (byte[])value;
/*      */     
/* 4940 */     int shift = 0;
/*      */     
/* 4942 */     long[] steps = new long[asBytes.length];
/*      */     
/* 4944 */     for (int i = asBytes.length - 1; i >= 0; i--) {
/* 4945 */       steps[i] = ((asBytes[i] & 0xFF) << shift);
/* 4946 */       shift += 8;
/*      */     }
/*      */     
/* 4949 */     long valueAsLong = 0L;
/*      */     
/* 4951 */     for (int i = 0; i < asBytes.length; i++) {
/* 4952 */       valueAsLong |= steps[i];
/*      */     }
/*      */     
/* 4955 */     return valueAsLong;
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
/*      */   public short getShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4970 */     if (!this.isBinaryEncoded) {
/* 4971 */       checkRowPos();
/*      */       
/* 4973 */       if (this.useFastIntParsing)
/*      */       {
/* 4975 */         checkColumnBounds(columnIndex);
/*      */         
/* 4977 */         Object value = this.thisRow.getColumnValue(columnIndex - 1);
/*      */         
/* 4979 */         if (value == null) {
/* 4980 */           this.wasNullFlag = true;
/*      */         } else {
/* 4982 */           this.wasNullFlag = false;
/*      */         }
/*      */         
/* 4985 */         if (this.wasNullFlag) {
/* 4986 */           return 0;
/*      */         }
/*      */         
/* 4989 */         byte[] shortAsBytes = (byte[])value;
/*      */         
/* 4991 */         if (shortAsBytes.length == 0) {
/* 4992 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 4995 */         boolean needsFullParse = false;
/*      */         
/* 4997 */         for (int i = 0; i < shortAsBytes.length; i++) {
/* 4998 */           if (((char)shortAsBytes[i] == 'e') || ((char)shortAsBytes[i] == 'E')) {
/* 4999 */             needsFullParse = true;
/*      */             
/* 5001 */             break;
/*      */           }
/*      */         }
/*      */         
/* 5005 */         if (!needsFullParse) {
/*      */           try {
/* 5007 */             return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe) {
/*      */             try {
/* 5011 */               return parseShortAsDouble(columnIndex, StringUtils.toString(shortAsBytes));
/*      */ 
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 5016 */               if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5017 */                 long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */                 
/* 5019 */                 if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L))) {
/* 5020 */                   throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */                 }
/*      */                 
/* 5023 */                 return (short)(int)valueAsLong;
/*      */               }
/*      */               
/* 5026 */               throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + StringUtils.toString(shortAsBytes) + "'", "S1009", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 5033 */       String val = null;
/*      */       try
/*      */       {
/* 5036 */         val = getString(columnIndex);
/*      */         
/* 5038 */         if (val != null)
/*      */         {
/* 5040 */           if (val.length() == 0) {
/* 5041 */             return (short)convertToZeroWithEmptyCheck();
/*      */           }
/*      */           
/* 5044 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1)) {
/* 5045 */             return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */           
/*      */ 
/* 5049 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */         
/* 5052 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 5055 */           return parseShortAsDouble(columnIndex, val);
/*      */ 
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 5060 */           if (this.fields[(columnIndex - 1)].getMysqlType() == 16) {
/* 5061 */             long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
/*      */             
/* 5063 */             if ((this.jdbcCompliantTruncationForReads) && ((valueAsLong < -32768L) || (valueAsLong > 32767L))) {
/* 5064 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
/*      */             }
/*      */             
/* 5067 */             return (short)(int)valueAsLong;
/*      */           }
/*      */           
/* 5070 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 5075 */     return getNativeShort(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public short getShort(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5084 */     return getShort(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private final short getShortFromString(String val, int columnIndex) throws SQLException {
/*      */     try {
/* 5089 */       if (val != null)
/*      */       {
/* 5091 */         if (val.length() == 0) {
/* 5092 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */         
/* 5095 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1)) {
/* 5096 */           return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */         
/*      */ 
/* 5100 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */       
/* 5103 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 5106 */         return parseShortAsDouble(columnIndex, val);
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException newNfe)
/*      */       {
/* 5111 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009", getExceptionInterceptor());
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
/*      */   public Statement getStatement()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5127 */       synchronized (checkClosed().getConnectionMutex()) {
/* 5128 */         if (this.wrapperStatement != null) {
/* 5129 */           return this.wrapperStatement;
/*      */         }
/*      */         
/* 5132 */         return this.owningStatement;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5146 */       return this.owningStatement;
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/* 5136 */       if (!this.retainOwningStatement) {
/* 5137 */         throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 5142 */       if (this.wrapperStatement != null) {
/* 5143 */         return this.wrapperStatement;
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
/*      */   public String getString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5163 */     String stringVal = getStringInternal(columnIndex, true);
/*      */     
/* 5165 */     if ((this.padCharsWithSpace) && (stringVal != null)) {
/* 5166 */       Field f = this.fields[(columnIndex - 1)];
/*      */       
/* 5168 */       if (f.getMysqlType() == 254) {
/* 5169 */         int fieldLength = (int)f.getLength() / f.getMaxBytesPerCharacter();
/*      */         
/* 5171 */         int currentLength = stringVal.length();
/*      */         
/* 5173 */         if (currentLength < fieldLength) {
/* 5174 */           StringBuilder paddedBuf = new StringBuilder(fieldLength);
/* 5175 */           paddedBuf.append(stringVal);
/*      */           
/* 5177 */           int difference = fieldLength - currentLength;
/*      */           
/* 5179 */           paddedBuf.append(EMPTY_SPACE, 0, difference);
/*      */           
/* 5181 */           stringVal = paddedBuf.toString();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 5186 */     return stringVal;
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
/*      */   public String getString(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5202 */     return getString(findColumn(columnName));
/*      */   }
/*      */   
/*      */   private String getStringForClob(int columnIndex) throws SQLException {
/* 5206 */     String asString = null;
/*      */     
/* 5208 */     String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */     
/* 5210 */     if (forcedEncoding == null) {
/* 5211 */       if (!this.isBinaryEncoded) {
/* 5212 */         asString = getString(columnIndex);
/*      */       } else {
/* 5214 */         asString = getNativeString(columnIndex);
/*      */       }
/*      */     } else {
/*      */       try {
/* 5218 */         byte[] asBytes = null;
/*      */         
/* 5220 */         if (!this.isBinaryEncoded) {
/* 5221 */           asBytes = getBytes(columnIndex);
/*      */         } else {
/* 5223 */           asBytes = getNativeBytes(columnIndex, true);
/*      */         }
/*      */         
/* 5226 */         if (asBytes != null) {
/* 5227 */           asString = StringUtils.toString(asBytes, forcedEncoding);
/*      */         }
/*      */       } catch (UnsupportedEncodingException uee) {
/* 5230 */         throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 5235 */     return asString;
/*      */   }
/*      */   
/*      */   protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException {
/* 5239 */     if (!this.isBinaryEncoded) {
/* 5240 */       checkRowPos();
/* 5241 */       checkColumnBounds(columnIndex);
/*      */       
/* 5243 */       if (this.fields == null) {
/* 5244 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 5250 */       int internalColumnIndex = columnIndex - 1;
/*      */       
/* 5252 */       if (this.thisRow.isNull(internalColumnIndex)) {
/* 5253 */         this.wasNullFlag = true;
/*      */         
/* 5255 */         return null;
/*      */       }
/*      */       
/* 5258 */       this.wasNullFlag = false;
/*      */       
/* 5260 */       Field metadata = this.fields[internalColumnIndex];
/*      */       
/* 5262 */       String stringVal = null;
/*      */       
/* 5264 */       if (metadata.getMysqlType() == 16) {
/* 5265 */         if (metadata.isSingleBit()) {
/* 5266 */           byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
/*      */           
/* 5268 */           if (value.length == 0) {
/* 5269 */             return String.valueOf(convertToZeroWithEmptyCheck());
/*      */           }
/*      */           
/* 5272 */           return String.valueOf(value[0]);
/*      */         }
/*      */         
/* 5275 */         return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
/*      */       }
/*      */       
/* 5278 */       String encoding = metadata.getEncoding();
/*      */       
/* 5280 */       stringVal = this.thisRow.getString(internalColumnIndex, encoding, this.connection);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5286 */       if (metadata.getMysqlType() == 13) {
/* 5287 */         if (!this.connection.getYearIsDateType()) {
/* 5288 */           return stringVal;
/*      */         }
/*      */         
/* 5291 */         Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */         
/* 5293 */         if (dt == null) {
/* 5294 */           this.wasNullFlag = true;
/*      */           
/* 5296 */           return null;
/*      */         }
/*      */         
/* 5299 */         this.wasNullFlag = false;
/*      */         
/* 5301 */         return dt.toString();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 5306 */       if ((checkDateTypes) && (!this.connection.getNoDatetimeStringSync())) {
/* 5307 */         switch (metadata.getSQLType()) {
/*      */         case 92: 
/* 5309 */           Time tm = getTimeFromString(stringVal, null, columnIndex, getDefaultTimeZone(), false);
/*      */           
/* 5311 */           if (tm == null) {
/* 5312 */             this.wasNullFlag = true;
/*      */             
/* 5314 */             return null;
/*      */           }
/*      */           
/* 5317 */           this.wasNullFlag = false;
/*      */           
/* 5319 */           return tm.toString();
/*      */         
/*      */         case 91: 
/* 5322 */           Date dt = getDateFromString(stringVal, columnIndex, null);
/*      */           
/* 5324 */           if (dt == null) {
/* 5325 */             this.wasNullFlag = true;
/*      */             
/* 5327 */             return null;
/*      */           }
/*      */           
/* 5330 */           this.wasNullFlag = false;
/*      */           
/* 5332 */           return dt.toString();
/*      */         case 93: 
/* 5334 */           Timestamp ts = getTimestampFromString(columnIndex, null, stringVal, getDefaultTimeZone(), false);
/*      */           
/* 5336 */           if (ts == null) {
/* 5337 */             this.wasNullFlag = true;
/*      */             
/* 5339 */             return null;
/*      */           }
/*      */           
/* 5342 */           this.wasNullFlag = false;
/*      */           
/* 5344 */           return ts.toString();
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 5350 */       return stringVal;
/*      */     }
/*      */     
/* 5353 */     return getNativeString(columnIndex);
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
/*      */   public Time getTime(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5368 */     return getTimeInternal(columnIndex, null, getDefaultTimeZone(), false);
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
/*      */   public Time getTime(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5387 */     return getTimeInternal(columnIndex, cal, cal.getTimeZone(), true);
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
/*      */   public Time getTime(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5402 */     return getTime(findColumn(columnName));
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
/*      */   public Time getTime(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5421 */     return getTime(findColumn(columnName), cal);
/*      */   }
/*      */   
/*      */   private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException {
/* 5425 */     synchronized (checkClosed().getConnectionMutex()) {
/* 5426 */       int hr = 0;
/* 5427 */       int min = 0;
/* 5428 */       int sec = 0;
/*      */       
/*      */       try
/*      */       {
/* 5432 */         if (timeAsString == null) {
/* 5433 */           this.wasNullFlag = true;
/*      */           
/* 5435 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5445 */         timeAsString = timeAsString.trim();
/*      */         
/*      */ 
/* 5448 */         int dec = timeAsString.indexOf(".");
/* 5449 */         if (dec > -1) {
/* 5450 */           timeAsString = timeAsString.substring(0, dec);
/*      */         }
/*      */         
/* 5453 */         if ((timeAsString.equals("0")) || (timeAsString.equals("0000-00-00")) || (timeAsString.equals("0000-00-00 00:00:00")) || (timeAsString.equals("00000000000000")))
/*      */         {
/* 5455 */           if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/* 5456 */             this.wasNullFlag = true;
/*      */             
/* 5458 */             return null; }
/* 5459 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior())) {
/* 5460 */             throw SQLError.createSQLException("Value '" + timeAsString + "' can not be represented as java.sql.Time", "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 5465 */           return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */         }
/*      */         
/* 5468 */         this.wasNullFlag = false;
/*      */         
/* 5470 */         Field timeColField = this.fields[(columnIndex - 1)];
/*      */         
/* 5472 */         if (timeColField.getMysqlType() == 7)
/*      */         {
/* 5474 */           int length = timeAsString.length();
/*      */           
/* 5476 */           switch (length)
/*      */           {
/*      */           case 19: 
/* 5479 */             hr = Integer.parseInt(timeAsString.substring(length - 8, length - 6));
/* 5480 */             min = Integer.parseInt(timeAsString.substring(length - 5, length - 3));
/* 5481 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */             
/*      */ 
/* 5484 */             break;
/*      */           case 12: 
/*      */           case 14: 
/* 5487 */             hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
/* 5488 */             min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
/* 5489 */             sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */             
/*      */ 
/* 5492 */             break;
/*      */           
/*      */           case 10: 
/* 5495 */             hr = Integer.parseInt(timeAsString.substring(6, 8));
/* 5496 */             min = Integer.parseInt(timeAsString.substring(8, 10));
/* 5497 */             sec = 0;
/*      */             
/*      */ 
/* 5500 */             break;
/*      */           case 11: case 13: case 15: case 16: 
/*      */           case 17: case 18: default: 
/* 5503 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */           
/* 5507 */           SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */           
/*      */ 
/*      */ 
/* 5511 */           if (this.warningChain == null) {
/* 5512 */             this.warningChain = precisionLost;
/*      */           } else {
/* 5514 */             this.warningChain.setNextWarning(precisionLost);
/*      */           }
/* 5516 */         } else if (timeColField.getMysqlType() == 12) {
/* 5517 */           hr = Integer.parseInt(timeAsString.substring(11, 13));
/* 5518 */           min = Integer.parseInt(timeAsString.substring(14, 16));
/* 5519 */           sec = Integer.parseInt(timeAsString.substring(17, 19));
/*      */           
/* 5521 */           SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */           
/*      */ 
/*      */ 
/* 5525 */           if (this.warningChain == null) {
/* 5526 */             this.warningChain = precisionLost;
/*      */           } else
/* 5528 */             this.warningChain.setNextWarning(precisionLost);
/*      */         } else {
/* 5530 */           if (timeColField.getMysqlType() == 10) {
/* 5531 */             return fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */           }
/*      */           
/*      */ 
/* 5535 */           if ((timeAsString.length() != 5) && (timeAsString.length() != 8)) {
/* 5536 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5543 */           hr = Integer.parseInt(timeAsString.substring(0, 2));
/* 5544 */           min = Integer.parseInt(timeAsString.substring(3, 5));
/* 5545 */           sec = timeAsString.length() == 5 ? 0 : Integer.parseInt(timeAsString.substring(6));
/*      */         }
/*      */         
/* 5548 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */         
/* 5550 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimeCreate(sessionCalendar, hr, min, sec), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */       catch (RuntimeException ex) {
/* 5553 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", getExceptionInterceptor());
/* 5554 */         sqlEx.initCause(ex);
/*      */         
/* 5556 */         throw sqlEx;
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
/*      */   private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 5576 */     checkRowPos();
/*      */     
/* 5578 */     if (this.isBinaryEncoded) {
/* 5579 */       return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */     
/* 5582 */     if (!this.useFastDateParsing) {
/* 5583 */       String timeAsString = getStringInternal(columnIndex, false);
/*      */       
/* 5585 */       return getTimeFromString(timeAsString, targetCalendar, columnIndex, tz, rollForward);
/*      */     }
/*      */     
/* 5588 */     checkColumnBounds(columnIndex);
/*      */     
/* 5590 */     int columnIndexMinusOne = columnIndex - 1;
/*      */     
/* 5592 */     if (this.thisRow.isNull(columnIndexMinusOne)) {
/* 5593 */       this.wasNullFlag = true;
/*      */       
/* 5595 */       return null;
/*      */     }
/*      */     
/* 5598 */     this.wasNullFlag = false;
/*      */     
/* 5600 */     return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
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
/*      */   public Timestamp getTimestamp(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5616 */     return getTimestampInternal(columnIndex, null, getDefaultTimeZone(), false);
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
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5636 */     return getTimestampInternal(columnIndex, cal, cal.getTimeZone(), true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5645 */     return getTimestamp(findColumn(columnName));
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
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5665 */     return getTimestamp(findColumn(columnName), cal);
/*      */   }
/*      */   
/*      */   private Timestamp getTimestampFromString(int columnIndex, Calendar targetCalendar, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/*      */     try {
/* 5671 */       this.wasNullFlag = false;
/*      */       
/* 5673 */       if (timestampValue == null) {
/* 5674 */         this.wasNullFlag = true;
/*      */         
/* 5676 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5686 */       timestampValue = timestampValue.trim();
/*      */       
/* 5688 */       int length = timestampValue.length();
/*      */       
/* 5690 */       Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       
/*      */ 
/* 5693 */       if ((length > 0) && (timestampValue.charAt(0) == '0') && ((timestampValue.equals("0000-00-00")) || (timestampValue.equals("0000-00-00 00:00:00")) || (timestampValue.equals("00000000000000")) || (timestampValue.equals("0"))))
/*      */       {
/*      */ 
/* 5696 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/* 5697 */           this.wasNullFlag = true;
/*      */           
/* 5699 */           return null; }
/* 5700 */         if ("exception".equals(this.connection.getZeroDateTimeBehavior())) {
/* 5701 */           throw SQLError.createSQLException("Value '" + timestampValue + "' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 5706 */         return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */       }
/* 5708 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 13)
/*      */       {
/* 5710 */         if (!this.useLegacyDatetimeCode) {
/* 5711 */           return TimeUtil.fastTimestampCreate(tz, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0);
/*      */         }
/*      */         
/* 5714 */         return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5721 */       int year = 0;
/* 5722 */       int month = 0;
/* 5723 */       int day = 0;
/* 5724 */       int hour = 0;
/* 5725 */       int minutes = 0;
/* 5726 */       int seconds = 0;
/* 5727 */       int nanos = 0;
/*      */       
/*      */ 
/* 5730 */       int decimalIndex = timestampValue.indexOf(".");
/*      */       
/* 5732 */       if (decimalIndex == length - 1)
/*      */       {
/* 5734 */         length--;
/*      */       }
/* 5736 */       else if (decimalIndex != -1)
/*      */       {
/* 5738 */         if (decimalIndex + 2 <= length) {
/* 5739 */           nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
/*      */           
/* 5741 */           int numDigits = length - (decimalIndex + 1);
/*      */           
/* 5743 */           if (numDigits < 9) {
/* 5744 */             int factor = (int)Math.pow(10.0D, 9 - numDigits);
/* 5745 */             nanos *= factor;
/*      */           }
/*      */           
/* 5748 */           length = decimalIndex;
/*      */         } else {
/* 5750 */           throw new IllegalArgumentException();
/*      */         }
/*      */       }
/*      */       
/* 5754 */       switch (length) {
/*      */       case 19: 
/*      */       case 20: 
/*      */       case 21: 
/*      */       case 22: 
/*      */       case 23: 
/*      */       case 24: 
/*      */       case 25: 
/*      */       case 26: 
/* 5763 */         year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5764 */         month = Integer.parseInt(timestampValue.substring(5, 7));
/* 5765 */         day = Integer.parseInt(timestampValue.substring(8, 10));
/* 5766 */         hour = Integer.parseInt(timestampValue.substring(11, 13));
/* 5767 */         minutes = Integer.parseInt(timestampValue.substring(14, 16));
/* 5768 */         seconds = Integer.parseInt(timestampValue.substring(17, 19));
/*      */         
/* 5770 */         break;
/*      */       
/*      */ 
/*      */       case 14: 
/* 5774 */         year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5775 */         month = Integer.parseInt(timestampValue.substring(4, 6));
/* 5776 */         day = Integer.parseInt(timestampValue.substring(6, 8));
/* 5777 */         hour = Integer.parseInt(timestampValue.substring(8, 10));
/* 5778 */         minutes = Integer.parseInt(timestampValue.substring(10, 12));
/* 5779 */         seconds = Integer.parseInt(timestampValue.substring(12, 14));
/*      */         
/* 5781 */         break;
/*      */       
/*      */ 
/*      */       case 12: 
/* 5785 */         year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */         
/* 5787 */         if (year <= 69) {
/* 5788 */           year += 100;
/*      */         }
/*      */         
/* 5791 */         year += 1900;
/*      */         
/* 5793 */         month = Integer.parseInt(timestampValue.substring(2, 4));
/* 5794 */         day = Integer.parseInt(timestampValue.substring(4, 6));
/* 5795 */         hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 5796 */         minutes = Integer.parseInt(timestampValue.substring(8, 10));
/* 5797 */         seconds = Integer.parseInt(timestampValue.substring(10, 12));
/*      */         
/* 5799 */         break;
/*      */       
/*      */ 
/*      */       case 10: 
/* 5803 */         if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (timestampValue.indexOf("-") != -1)) {
/* 5804 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5805 */           month = Integer.parseInt(timestampValue.substring(5, 7));
/* 5806 */           day = Integer.parseInt(timestampValue.substring(8, 10));
/* 5807 */           hour = 0;
/* 5808 */           minutes = 0;
/*      */         } else {
/* 5810 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */           
/* 5812 */           if (year <= 69) {
/* 5813 */             year += 100;
/*      */           }
/*      */           
/* 5816 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/* 5817 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/* 5818 */           hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 5819 */           minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */           
/* 5821 */           year += 1900;
/*      */         }
/*      */         
/* 5824 */         break;
/*      */       
/*      */ 
/*      */       case 8: 
/* 5828 */         if (timestampValue.indexOf(":") != -1) {
/* 5829 */           hour = Integer.parseInt(timestampValue.substring(0, 2));
/* 5830 */           minutes = Integer.parseInt(timestampValue.substring(3, 5));
/* 5831 */           seconds = Integer.parseInt(timestampValue.substring(6, 8));
/* 5832 */           year = 1970;
/* 5833 */           month = 1;
/* 5834 */           day = 1;
/*      */         }
/*      */         else
/*      */         {
/* 5838 */           year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5839 */           month = Integer.parseInt(timestampValue.substring(4, 6));
/* 5840 */           day = Integer.parseInt(timestampValue.substring(6, 8));
/*      */           
/* 5842 */           year -= 1900;
/* 5843 */           month--;
/*      */         }
/* 5845 */         break;
/*      */       
/*      */ 
/*      */       case 6: 
/* 5849 */         year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */         
/* 5851 */         if (year <= 69) {
/* 5852 */           year += 100;
/*      */         }
/*      */         
/* 5855 */         year += 1900;
/*      */         
/* 5857 */         month = Integer.parseInt(timestampValue.substring(2, 4));
/* 5858 */         day = Integer.parseInt(timestampValue.substring(4, 6));
/*      */         
/* 5860 */         break;
/*      */       
/*      */ 
/*      */       case 4: 
/* 5864 */         year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */         
/* 5866 */         if (year <= 69) {
/* 5867 */           year += 100;
/*      */         }
/*      */         
/* 5870 */         year += 1900;
/*      */         
/* 5872 */         month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */         
/* 5874 */         day = 1;
/*      */         
/* 5876 */         break;
/*      */       
/*      */ 
/*      */       case 2: 
/* 5880 */         year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */         
/* 5882 */         if (year <= 69) {
/* 5883 */           year += 100;
/*      */         }
/*      */         
/* 5886 */         year += 1900;
/* 5887 */         month = 1;
/* 5888 */         day = 1;
/*      */         
/* 5890 */         break;
/*      */       case 3: case 5: case 7: case 9: 
/*      */       case 11: case 13: case 15: case 16: 
/*      */       case 17: case 18: default: 
/* 5894 */         throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
/*      */       }
/*      */       
/*      */       
/* 5898 */       if (!this.useLegacyDatetimeCode) {
/* 5899 */         return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */       }
/*      */       
/* 5902 */       return TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/* 5907 */       SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009", getExceptionInterceptor());
/*      */       
/* 5909 */       sqlEx.initCause(e);
/*      */       
/* 5911 */       throw sqlEx;
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
/*      */   private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 5931 */     if (this.isBinaryEncoded) {
/* 5932 */       return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
/*      */     }
/*      */     
/* 5935 */     Timestamp tsVal = null;
/*      */     
/* 5937 */     if (!this.useFastDateParsing) {
/* 5938 */       String timestampValue = getStringInternal(columnIndex, false);
/*      */       
/* 5940 */       tsVal = getTimestampFromString(columnIndex, targetCalendar, timestampValue, tz, rollForward);
/*      */     } else {
/* 5942 */       checkClosed();
/* 5943 */       checkRowPos();
/* 5944 */       checkColumnBounds(columnIndex);
/*      */       
/* 5946 */       tsVal = this.thisRow.getTimestampFast(columnIndex - 1, targetCalendar, tz, rollForward, this.connection, this);
/*      */     }
/*      */     
/* 5949 */     if (tsVal == null) {
/* 5950 */       this.wasNullFlag = true;
/*      */     } else {
/* 5952 */       this.wasNullFlag = false;
/*      */     }
/*      */     
/* 5955 */     return tsVal;
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
/*      */   public int getType()
/*      */     throws SQLException
/*      */   {
/* 5969 */     return this.resultSetType;
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
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5992 */     if (!this.isBinaryEncoded) {
/* 5993 */       checkRowPos();
/*      */       
/* 5995 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */     
/* 5998 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 6010 */     return getUnicodeStream(findColumn(columnName));
/*      */   }
/*      */   
/*      */   public long getUpdateCount() {
/* 6014 */     return this.updateCount;
/*      */   }
/*      */   
/*      */   public long getUpdateID() {
/* 6018 */     return this.updateId;
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 6025 */     String val = getString(colIndex);
/*      */     
/* 6027 */     if (val == null) {
/* 6028 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6032 */       return new URL(val);
/*      */     } catch (MalformedURLException mfe) {
/* 6034 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URL getURL(String colName)
/*      */     throws SQLException
/*      */   {
/* 6043 */     String val = getString(colName);
/*      */     
/* 6045 */     if (val == null) {
/* 6046 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 6050 */       return new URL(val);
/*      */     } catch (MalformedURLException mfe) {
/* 6052 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009", getExceptionInterceptor());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void insertRow()
/*      */     throws SQLException
/*      */   {
/* 6093 */     throw new NotUpdatable();
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
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/* 6110 */     synchronized (checkClosed().getConnectionMutex()) {
/* 6111 */       boolean b = this.rowData.isAfterLast();
/*      */       
/* 6113 */       return b;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion)
/*      */     throws SQLException
/*      */   {
/* 6180 */     synchronized (checkClosed().getConnectionMutex()) {
/* 6181 */       StringBuilder originalQueryBuf = new StringBuilder();
/*      */       
/* 6183 */       if ((this.owningStatement != null) && ((this.owningStatement instanceof PreparedStatement))) {
/* 6184 */         originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
/* 6185 */         originalQueryBuf.append(((PreparedStatement)this.owningStatement).originalSql);
/* 6186 */         originalQueryBuf.append("\n\n");
/*      */       } else {
/* 6188 */         originalQueryBuf.append(".");
/*      */       }
/*      */       
/* 6191 */       StringBuilder convertibleTypesBuf = new StringBuilder();
/*      */       
/* 6193 */       for (int i = 0; i < typesWithNoParseConversion.length; i++) {
/* 6194 */         convertibleTypesBuf.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
/* 6195 */         convertibleTypesBuf.append("\n");
/*      */       }
/*      */       
/* 6198 */       String message = Messages.getString("ResultSet.CostlyConversion", new Object[] { methodName, Integer.valueOf(columnIndex + 1), fieldInfo.getOriginalName(), fieldInfo.getOriginalTableName(), originalQueryBuf.toString(), value != null ? value.getClass().getName() : ResultSetMetaData.getClassNameForJavaType(fieldInfo.getSQLType(), fieldInfo.isUnsigned(), fieldInfo.getMysqlType(), (fieldInfo.isBinary()) || (fieldInfo.isBlob()) ? 1 : false, fieldInfo.isOpaqueBinary(), this.connection.getYearIsDateType()), MysqlDefs.typeToName(fieldInfo.getMysqlType()), convertibleTypesBuf.toString() });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6206 */       this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
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
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/* 6227 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 6229 */       boolean b = true;
/*      */       
/* 6231 */       if (this.rowData.size() == 0) {
/* 6232 */         b = false;
/*      */       }
/*      */       else {
/* 6235 */         if (this.onInsertRow) {
/* 6236 */           this.onInsertRow = false;
/*      */         }
/*      */         
/* 6239 */         if (this.doingUpdates) {
/* 6240 */           this.doingUpdates = false;
/*      */         }
/*      */         
/* 6243 */         if (this.thisRow != null) {
/* 6244 */           this.thisRow.closeOpenStreams();
/*      */         }
/*      */         
/* 6247 */         this.rowData.beforeLast();
/* 6248 */         this.thisRow = this.rowData.next();
/*      */       }
/*      */       
/* 6251 */       setRowPositionValidity();
/*      */       
/* 6253 */       return b;
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
/*      */   public void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 6275 */     throw new NotUpdatable();
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
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 6295 */     throw new NotUpdatable();
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
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/* 6313 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 6315 */       if (this.onInsertRow) {
/* 6316 */         this.onInsertRow = false;
/*      */       }
/*      */       
/* 6319 */       if (this.doingUpdates) {
/* 6320 */         this.doingUpdates = false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 6325 */       if (!reallyResult()) {
/* 6326 */         throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 6330 */       if (this.thisRow != null)
/* 6331 */         this.thisRow.closeOpenStreams();
/*      */       boolean b;
/*      */       boolean b;
/* 6334 */       if (this.rowData.size() == 0) {
/* 6335 */         b = false;
/*      */       } else {
/* 6337 */         this.thisRow = this.rowData.next();
/*      */         boolean b;
/* 6339 */         if (this.thisRow == null) {
/* 6340 */           b = false;
/*      */         } else {
/* 6342 */           clearWarnings();
/*      */           
/* 6344 */           b = true;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 6349 */       setRowPositionValidity();
/*      */       
/* 6351 */       return b;
/*      */     }
/*      */   }
/*      */   
/*      */   private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
/* 6356 */     if (val == null) {
/* 6357 */       return 0;
/*      */     }
/*      */     
/* 6360 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 6362 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 6363 */       (valueAsDouble < -2.147483648E9D) || (valueAsDouble > 2.147483647E9D))) {
/* 6364 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */     }
/*      */     
/*      */ 
/* 6368 */     return (int)valueAsDouble;
/*      */   }
/*      */   
/*      */   private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
/* 6372 */     int intValue = this.thisRow.getInt(columnIndex);
/*      */     
/* 6374 */     checkForIntegerTruncation(columnIndex, null, intValue);
/*      */     
/* 6376 */     return intValue;
/*      */   }
/*      */   
/*      */   private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue) throws SQLException {
/* 6380 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 6381 */       (intValue == Integer.MIN_VALUE) || (intValue == Integer.MAX_VALUE))) {
/* 6382 */       String valueAsString = null;
/*      */       
/* 6384 */       if (valueAsBytes == null) {
/* 6385 */         valueAsString = this.thisRow.getString(columnIndex, this.fields[columnIndex].getEncoding(), this.connection);
/*      */       }
/*      */       
/* 6388 */       long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */       
/* 6390 */       if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)) {
/* 6391 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex + 1, 4);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private long parseLongAsDouble(int columnIndexZeroBased, String val) throws NumberFormatException, SQLException
/*      */   {
/* 6398 */     if (val == null) {
/* 6399 */       return 0L;
/*      */     }
/*      */     
/* 6402 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 6404 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 6405 */       (valueAsDouble < -9.223372036854776E18D) || (valueAsDouble > 9.223372036854776E18D))) {
/* 6406 */       throwRangeException(val, columnIndexZeroBased + 1, -5);
/*      */     }
/*      */     
/*      */ 
/* 6410 */     return valueAsDouble;
/*      */   }
/*      */   
/*      */   private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
/* 6414 */     long longValue = this.thisRow.getLong(columnIndexZeroBased);
/*      */     
/* 6416 */     if (doOverflowCheck) {
/* 6417 */       checkForLongTruncation(columnIndexZeroBased, null, longValue);
/*      */     }
/*      */     
/* 6420 */     return longValue;
/*      */   }
/*      */   
/*      */   private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 6426 */     long longValue = 0L;
/*      */     
/* 6428 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 6429 */       return 0L;
/*      */     }
/*      */     
/* 6432 */     if (valueAsBytes != null) {
/* 6433 */       longValue = StringUtils.getLong(valueAsBytes);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/* 6442 */       valueAsString = valueAsString.trim();
/*      */       
/* 6444 */       longValue = Long.parseLong(valueAsString);
/*      */     }
/*      */     
/* 6447 */     if ((doCheck) && (this.jdbcCompliantTruncationForReads)) {
/* 6448 */       checkForLongTruncation(columnIndexZeroBased, valueAsBytes, longValue);
/*      */     }
/*      */     
/* 6451 */     return longValue;
/*      */   }
/*      */   
/*      */   private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
/* 6455 */     if ((longValue == Long.MIN_VALUE) || (longValue == Long.MAX_VALUE)) {
/* 6456 */       String valueAsString = null;
/*      */       
/* 6458 */       if (valueAsBytes == null) {
/* 6459 */         valueAsString = this.thisRow.getString(columnIndexZeroBased, this.fields[columnIndexZeroBased].getEncoding(), this.connection);
/*      */       }
/*      */       
/* 6462 */       double valueAsDouble = Double.parseDouble(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */       
/* 6464 */       if ((valueAsDouble < -9.223372036854776E18D) || (valueAsDouble > 9.223372036854776E18D)) {
/* 6465 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndexZeroBased + 1, -5);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private short parseShortAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
/* 6471 */     if (val == null) {
/* 6472 */       return 0;
/*      */     }
/*      */     
/* 6475 */     double valueAsDouble = Double.parseDouble(val);
/*      */     
/* 6477 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 6478 */       (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D))) {
/* 6479 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
/*      */     }
/*      */     
/*      */ 
/* 6483 */     return (short)(int)valueAsDouble;
/*      */   }
/*      */   
/*      */   private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString) throws NumberFormatException, SQLException
/*      */   {
/* 6488 */     short shortValue = 0;
/*      */     
/* 6490 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 6491 */       return 0;
/*      */     }
/*      */     
/* 6494 */     if (valueAsBytes != null) {
/* 6495 */       shortValue = StringUtils.getShort(valueAsBytes);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/* 6504 */       valueAsString = valueAsString.trim();
/*      */       
/* 6506 */       shortValue = Short.parseShort(valueAsString);
/*      */     }
/*      */     
/* 6509 */     if ((this.jdbcCompliantTruncationForReads) && (
/* 6510 */       (shortValue == Short.MIN_VALUE) || (shortValue == Short.MAX_VALUE))) {
/* 6511 */       long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
/*      */       
/* 6513 */       if ((valueAsLong < -32768L) || (valueAsLong > 32767L)) {
/* 6514 */         throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex, 5);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 6519 */     return shortValue;
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
/*      */   public boolean prev()
/*      */     throws SQLException
/*      */   {
/* 6542 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 6544 */       int rowIndex = this.rowData.getCurrentRowNumber();
/*      */       
/* 6546 */       if (this.thisRow != null) {
/* 6547 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/* 6550 */       boolean b = true;
/*      */       
/* 6552 */       if (rowIndex - 1 >= 0) {
/* 6553 */         rowIndex--;
/* 6554 */         this.rowData.setCurrentRow(rowIndex);
/* 6555 */         this.thisRow = this.rowData.getAt(rowIndex);
/*      */         
/* 6557 */         b = true;
/* 6558 */       } else if (rowIndex - 1 == -1) {
/* 6559 */         rowIndex--;
/* 6560 */         this.rowData.setCurrentRow(rowIndex);
/* 6561 */         this.thisRow = null;
/*      */         
/* 6563 */         b = false;
/*      */       } else {
/* 6565 */         b = false;
/*      */       }
/*      */       
/* 6568 */       setRowPositionValidity();
/*      */       
/* 6570 */       return b;
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
/*      */   public boolean previous()
/*      */     throws SQLException
/*      */   {
/* 6592 */     synchronized (checkClosed().getConnectionMutex()) {
/* 6593 */       if (this.onInsertRow) {
/* 6594 */         this.onInsertRow = false;
/*      */       }
/*      */       
/* 6597 */       if (this.doingUpdates) {
/* 6598 */         this.doingUpdates = false;
/*      */       }
/*      */       
/* 6601 */       return prev();
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
/*      */   public void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 6616 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/* 6618 */     if (locallyScopedConn == null) {
/* 6619 */       return;
/*      */     }
/*      */     
/* 6622 */     synchronized (locallyScopedConn.getConnectionMutex())
/*      */     {
/*      */ 
/*      */ 
/* 6626 */       if (this.isClosed) {
/* 6627 */         return;
/*      */       }
/*      */       try
/*      */       {
/* 6631 */         if (this.useUsageAdvisor)
/*      */         {
/*      */ 
/*      */ 
/* 6635 */           if (!calledExplicitly) {
/* 6636 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver")));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 6642 */           if ((this.rowData instanceof RowDataStatic))
/*      */           {
/*      */ 
/*      */ 
/* 6646 */             if (this.rowData.size() > this.connection.getResultSetSizeThreshold()) {
/* 6647 */               this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Too_Large_Result_Set", new Object[] { Integer.valueOf(this.rowData.size()), Integer.valueOf(this.connection.getResultSetSizeThreshold()) })));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6655 */             if ((!isLast()) && (!isAfterLast()) && (this.rowData.size() != 0))
/*      */             {
/* 6657 */               this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set", new Object[] { Integer.valueOf(getRow()), Integer.valueOf(this.rowData.size()) })));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 6670 */           if ((this.columnUsed.length > 0) && (!this.rowData.wasEmpty())) {
/* 6671 */             StringBuilder buf = new StringBuilder(Messages.getString("ResultSet.The_following_columns_were_never_referenced"));
/*      */             
/* 6673 */             boolean issueWarn = false;
/*      */             
/* 6675 */             for (int i = 0; i < this.columnUsed.length; i++) {
/* 6676 */               if (this.columnUsed[i] == 0) {
/* 6677 */                 if (!issueWarn) {
/* 6678 */                   issueWarn = true;
/*      */                 } else {
/* 6680 */                   buf.append(", ");
/*      */                 }
/*      */                 
/* 6683 */                 buf.append(this.fields[i].getFullName());
/*      */               }
/*      */             }
/*      */             
/* 6687 */             if (issueWarn) {
/* 6688 */               this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, buf.toString()));
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 6696 */         if ((this.owningStatement != null) && (calledExplicitly)) {
/* 6697 */           this.owningStatement.removeOpenResultSet(this);
/*      */         }
/*      */         
/* 6700 */         SQLException exceptionDuringClose = null;
/*      */         
/* 6702 */         if (this.rowData != null) {
/*      */           try {
/* 6704 */             this.rowData.close();
/*      */           } catch (SQLException sqlEx) {
/* 6706 */             exceptionDuringClose = sqlEx;
/*      */           }
/*      */         }
/*      */         
/* 6710 */         if (this.statementUsedForFetchingRows != null) {
/*      */           try {
/* 6712 */             this.statementUsedForFetchingRows.realClose(true, false);
/*      */           } catch (SQLException sqlEx) {
/* 6714 */             if (exceptionDuringClose != null) {
/* 6715 */               exceptionDuringClose.setNextException(sqlEx);
/*      */             } else {
/* 6717 */               exceptionDuringClose = sqlEx;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 6722 */         this.rowData = null;
/* 6723 */         this.fields = null;
/* 6724 */         this.columnLabelToIndex = null;
/* 6725 */         this.fullColumnNameToIndex = null;
/* 6726 */         this.columnToIndexCache = null;
/* 6727 */         this.eventSink = null;
/* 6728 */         this.warningChain = null;
/*      */         
/* 6730 */         if (!this.retainOwningStatement) {
/* 6731 */           this.owningStatement = null;
/*      */         }
/*      */         
/* 6734 */         this.catalog = null;
/* 6735 */         this.serverInfo = null;
/* 6736 */         this.thisRow = null;
/* 6737 */         this.fastDefaultCal = null;
/* 6738 */         this.fastClientCal = null;
/* 6739 */         this.connection = null;
/*      */         
/* 6741 */         this.isClosed = true;
/*      */         
/* 6743 */         if (exceptionDuringClose != null) {
/* 6744 */           throw exceptionDuringClose;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClosed()
/*      */     throws SQLException
/*      */   {
/* 6754 */     return this.isClosed;
/*      */   }
/*      */   
/*      */   public boolean reallyResult() {
/* 6758 */     if (this.rowData != null) {
/* 6759 */       return true;
/*      */     }
/*      */     
/* 6762 */     return this.reallyResult;
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
/*      */   public void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 6785 */     throw new NotUpdatable();
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
/*      */   public boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 6811 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 6813 */       if (this.rowData.size() == 0) {
/* 6814 */         setRowPositionValidity();
/*      */         
/* 6816 */         return false;
/*      */       }
/*      */       
/* 6819 */       if (this.thisRow != null) {
/* 6820 */         this.thisRow.closeOpenStreams();
/*      */       }
/*      */       
/* 6823 */       this.rowData.moveRowRelative(rows);
/* 6824 */       this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
/*      */       
/* 6826 */       setRowPositionValidity();
/*      */       
/* 6828 */       return (!this.rowData.isAfterLast()) && (!this.rowData.isBeforeFirst());
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
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 6847 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 6864 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 6881 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setBinaryEncoded()
/*      */   {
/* 6889 */     this.isBinaryEncoded = true;
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
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 6908 */     synchronized (checkClosed().getConnectionMutex()) {
/* 6909 */       if ((direction != 1000) && (direction != 1001) && (direction != 1002)) {
/* 6910 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 6914 */       this.fetchDirection = direction;
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
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 6935 */     synchronized (checkClosed().getConnectionMutex()) {
/* 6936 */       if (rows < 0) {
/* 6937 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 6941 */       this.fetchSize = rows;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFirstCharOfQuery(char c)
/*      */   {
/*      */     try
/*      */     {
/* 6954 */       synchronized (checkClosed().getConnectionMutex()) {
/* 6955 */         this.firstCharOfQuery = c;
/*      */       }
/*      */     } catch (SQLException e) {
/* 6958 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void setNextResultSet(ResultSetInternalMethods nextResultSet)
/*      */   {
/* 6968 */     this.nextResultSet = nextResultSet;
/*      */   }
/*      */   
/*      */   public void setOwningStatement(StatementImpl owningStatement) {
/*      */     try {
/* 6973 */       synchronized (checkClosed().getConnectionMutex()) {
/* 6974 */         this.owningStatement = owningStatement;
/*      */       }
/*      */     } catch (SQLException e) {
/* 6977 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/*      */     try
/*      */     {
/* 6989 */       synchronized (checkClosed().getConnectionMutex()) {
/* 6990 */         this.resultSetConcurrency = concurrencyFlag;
/*      */       }
/*      */     } catch (SQLException e) {
/* 6993 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void setResultSetType(int typeFlag)
/*      */   {
/*      */     try
/*      */     {
/* 7006 */       synchronized (checkClosed().getConnectionMutex()) {
/* 7007 */         this.resultSetType = typeFlag;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7010 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setServerInfo(String info)
/*      */   {
/*      */     try
/*      */     {
/* 7022 */       synchronized (checkClosed().getConnectionMutex()) {
/* 7023 */         this.serverInfo = info;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7026 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setStatementUsedForFetchingRows(PreparedStatement stmt) {
/*      */     try {
/* 7032 */       synchronized (checkClosed().getConnectionMutex()) {
/* 7033 */         this.statementUsedForFetchingRows = stmt;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7036 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void setWrapperStatement(Statement wrapperStatement)
/*      */   {
/*      */     try
/*      */     {
/* 7046 */       synchronized (checkClosed().getConnectionMutex()) {
/* 7047 */         this.wrapperStatement = wrapperStatement;
/*      */       }
/*      */     } catch (SQLException e) {
/* 7050 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException {
/* 7055 */     String datatype = null;
/*      */     
/* 7057 */     switch (jdbcType) {
/*      */     case -6: 
/* 7059 */       datatype = "TINYINT";
/* 7060 */       break;
/*      */     case 5: 
/* 7062 */       datatype = "SMALLINT";
/* 7063 */       break;
/*      */     case 4: 
/* 7065 */       datatype = "INTEGER";
/* 7066 */       break;
/*      */     case -5: 
/* 7068 */       datatype = "BIGINT";
/* 7069 */       break;
/*      */     case 7: 
/* 7071 */       datatype = "REAL";
/* 7072 */       break;
/*      */     case 6: 
/* 7074 */       datatype = "FLOAT";
/* 7075 */       break;
/*      */     case 8: 
/* 7077 */       datatype = "DOUBLE";
/* 7078 */       break;
/*      */     case 3: 
/* 7080 */       datatype = "DECIMAL";
/* 7081 */       break;
/*      */     case -4: case -3: case -2: case -1: case 0: case 1: case 2: default: 
/* 7083 */       datatype = " (JDBC type '" + jdbcType + "')";
/*      */     }
/*      */     
/* 7086 */     throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/* 7092 */     if (this.reallyResult) {
/* 7093 */       return super.toString();
/*      */     }
/*      */     
/* 7096 */     return "Result set representing update count of " + this.updateCount;
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateArray(int arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 7103 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateArray(String arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 7110 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7132 */     throw new NotUpdatable();
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
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7153 */     updateAsciiStream(findColumn(columnName), x, length);
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
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 7172 */     throw new NotUpdatable();
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
/*      */   public void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 7190 */     updateBigDecimal(findColumn(columnName), x);
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
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7212 */     throw new NotUpdatable();
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
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 7233 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateBlob(int arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 7240 */     throw new NotUpdatable();
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateBlob(String arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 7247 */     throw new NotUpdatable();
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
/*      */   public void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 7266 */     throw new NotUpdatable();
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
/*      */   public void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 7284 */     updateBoolean(findColumn(columnName), x);
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
/*      */   public void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 7303 */     throw new NotUpdatable();
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
/*      */   public void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 7321 */     updateByte(findColumn(columnName), x);
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
/*      */   public void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 7340 */     throw new NotUpdatable();
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
/*      */   public void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 7358 */     updateBytes(findColumn(columnName), x);
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
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 7380 */     throw new NotUpdatable();
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
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 7401 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateClob(int arg0, java.sql.Clob arg1)
/*      */     throws SQLException
/*      */   {
/* 7408 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateClob(String columnName, java.sql.Clob clob)
/*      */     throws SQLException
/*      */   {
/* 7415 */     updateClob(findColumn(columnName), clob);
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
/*      */   public void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 7434 */     throw new NotUpdatable();
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
/*      */   public void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 7452 */     updateDate(findColumn(columnName), x);
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
/*      */   public void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 7471 */     throw new NotUpdatable();
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
/*      */   public void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 7489 */     updateDouble(findColumn(columnName), x);
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
/*      */   public void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 7508 */     throw new NotUpdatable();
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
/*      */   public void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 7526 */     updateFloat(findColumn(columnName), x);
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
/*      */   public void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 7545 */     throw new NotUpdatable();
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
/*      */   public void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 7563 */     updateInt(findColumn(columnName), x);
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
/*      */   public void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 7582 */     throw new NotUpdatable();
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
/*      */   public void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 7600 */     updateLong(findColumn(columnName), x);
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
/*      */   public void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 7617 */     throw new NotUpdatable();
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
/*      */   public void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 7633 */     updateNull(findColumn(columnName));
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
/*      */   public void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 7652 */     throw new NotUpdatable();
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
/*      */   public void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 7675 */     throw new NotUpdatable();
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
/*      */   public void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 7693 */     updateObject(findColumn(columnName), x);
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
/*      */   public void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 7715 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateRef(int arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 7722 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */   public void updateRef(String arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 7729 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 7742 */     throw new NotUpdatable();
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
/*      */   public void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 7761 */     throw new NotUpdatable();
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
/*      */   public void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 7779 */     updateShort(findColumn(columnName), x);
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
/*      */   public void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 7798 */     throw new NotUpdatable();
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
/*      */   public void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 7816 */     updateString(findColumn(columnName), x);
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
/*      */   public void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 7835 */     throw new NotUpdatable();
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
/*      */   public void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 7853 */     updateTime(findColumn(columnName), x);
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
/*      */   public void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 7872 */     throw new NotUpdatable();
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
/*      */   public void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 7890 */     updateTimestamp(findColumn(columnName), x);
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
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 7905 */     return this.wasNullFlag;
/*      */   }
/*      */   
/*      */ 
/*      */   protected Calendar getGmtCalendar()
/*      */   {
/* 7911 */     if (this.gmtCalendar == null) {
/* 7912 */       this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */     }
/*      */     
/* 7915 */     return this.gmtCalendar;
/*      */   }
/*      */   
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 7919 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 29	com/mysql/jdbc/ResultSetImpl:fetchDirection	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2346	-> byte code offset #0
/*      */     //   Java source line #2347	-> byte code offset #12
/*      */     //   Java source line #2348	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	ResultSetImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 30	com/mysql/jdbc/ResultSetImpl:fetchSize	I
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2360	-> byte code offset #0
/*      */     //   Java source line #2361	-> byte code offset #12
/*      */     //   Java source line #2362	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	ResultSetImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public char getFirstCharOfQuery()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 347	com/mysql/jdbc/ResultSetImpl:firstCharOfQuery	C
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     //   24: astore_1
/*      */     //   25: new 348	java/lang/RuntimeException
/*      */     //   28: dup
/*      */     //   29: aload_1
/*      */     //   30: invokespecial 349	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   33: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2373	-> byte code offset #0
/*      */     //   Java source line #2374	-> byte code offset #12
/*      */     //   Java source line #2375	-> byte code offset #19
/*      */     //   Java source line #2376	-> byte code offset #24
/*      */     //   Java source line #2377	-> byte code offset #25
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	34	0	this	ResultSetImpl
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
/*      */   public String getServerInfo()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 42	com/mysql/jdbc/ResultSetImpl:serverInfo	Ljava/lang/String;
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: areturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     //   24: astore_1
/*      */     //   25: new 348	java/lang/RuntimeException
/*      */     //   28: dup
/*      */     //   29: aload_1
/*      */     //   30: invokespecial 349	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   33: athrow
/*      */     // Line number table:
/*      */     //   Java source line #4922	-> byte code offset #0
/*      */     //   Java source line #4923	-> byte code offset #12
/*      */     //   Java source line #4924	-> byte code offset #19
/*      */     //   Java source line #4925	-> byte code offset #24
/*      */     //   Java source line #4926	-> byte code offset #25
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	34	0	this	ResultSetImpl
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
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 49	com/mysql/jdbc/ResultSetImpl:warningChain	Ljava/sql/SQLWarning;
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: areturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #6077	-> byte code offset #0
/*      */     //   Java source line #6078	-> byte code offset #12
/*      */     //   Java source line #6079	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	ResultSetImpl
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 77	com/mysql/jdbc/ResultSetImpl:rowData	Lcom/mysql/jdbc/RowData;
/*      */     //   16: invokeinterface 145 1 0
/*      */     //   21: aload_1
/*      */     //   22: monitorexit
/*      */     //   23: ireturn
/*      */     //   24: astore_2
/*      */     //   25: aload_1
/*      */     //   26: monitorexit
/*      */     //   27: aload_2
/*      */     //   28: athrow
/*      */     // Line number table:
/*      */     //   Java source line #6131	-> byte code offset #0
/*      */     //   Java source line #6132	-> byte code offset #12
/*      */     //   Java source line #6133	-> byte code offset #24
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	29	0	this	ResultSetImpl
/*      */     //   10	16	1	Ljava/lang/Object;	Object
/*      */     //   24	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	23	24	finally
/*      */     //   24	27	24	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 77	com/mysql/jdbc/ResultSetImpl:rowData	Lcom/mysql/jdbc/RowData;
/*      */     //   16: invokeinterface 617 1 0
/*      */     //   21: aload_1
/*      */     //   22: monitorexit
/*      */     //   23: ireturn
/*      */     //   24: astore_2
/*      */     //   25: aload_1
/*      */     //   26: monitorexit
/*      */     //   27: aload_2
/*      */     //   28: athrow
/*      */     // Line number table:
/*      */     //   Java source line #6149	-> byte code offset #0
/*      */     //   Java source line #6150	-> byte code offset #12
/*      */     //   Java source line #6151	-> byte code offset #24
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	29	0	this	ResultSetImpl
/*      */     //   10	16	1	Ljava/lang/Object;	Object
/*      */     //   24	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	23	24	finally
/*      */     //   24	27	24	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isLast()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 85	com/mysql/jdbc/ResultSetImpl:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 86 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 77	com/mysql/jdbc/ResultSetImpl:rowData	Lcom/mysql/jdbc/RowData;
/*      */     //   16: invokeinterface 618 1 0
/*      */     //   21: aload_1
/*      */     //   22: monitorexit
/*      */     //   23: ireturn
/*      */     //   24: astore_2
/*      */     //   25: aload_1
/*      */     //   26: monitorexit
/*      */     //   27: aload_2
/*      */     //   28: athrow
/*      */     // Line number table:
/*      */     //   Java source line #6168	-> byte code offset #0
/*      */     //   Java source line #6169	-> byte code offset #12
/*      */     //   Java source line #6170	-> byte code offset #24
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	29	0	this	ResultSetImpl
/*      */     //   10	16	1	Ljava/lang/Object;	Object
/*      */     //   24	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	23	24	finally
/*      */     //   24	27	24	finally
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ResultSetImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */