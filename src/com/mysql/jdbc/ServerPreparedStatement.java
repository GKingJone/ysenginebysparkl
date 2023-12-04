/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
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
/*      */ public class ServerPreparedStatement
/*      */   extends PreparedStatement
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_SPS_CTOR;
/*      */   protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*      */   
/*      */   static
/*      */   {
/*   62 */     if (Util.isJdbc4()) {
/*      */       try {
/*   64 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ServerPreparedStatement" : "com.mysql.jdbc.JDBC4ServerPreparedStatement";
/*   65 */         JDBC_4_SPS_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, Integer.TYPE, Integer.TYPE });
/*      */       }
/*      */       catch (SecurityException e) {
/*   68 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   70 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   72 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   75 */       JDBC_4_SPS_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public static class BatchedBindValues
/*      */   {
/*      */     public BindValue[] batchedParameterValues;
/*      */     
/*      */     BatchedBindValues(BindValue[] paramVals)
/*      */     {
/*   85 */       int numParams = paramVals.length;
/*      */       
/*   87 */       this.batchedParameterValues = new BindValue[numParams];
/*      */       
/*   89 */       for (int i = 0; i < numParams; i++) {
/*   90 */         this.batchedParameterValues[i] = new BindValue(paramVals[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static class BindValue
/*      */   {
/*   97 */     public long boundBeforeExecutionNum = 0L;
/*      */     
/*      */     public long bindLength;
/*      */     
/*      */     public int bufferType;
/*      */     
/*      */     public double doubleBinding;
/*      */     
/*      */     public float floatBinding;
/*      */     
/*      */     public boolean isLongData;
/*      */     
/*      */     public boolean isNull;
/*      */     
/*  111 */     public boolean isSet = false;
/*      */     
/*      */     public long longBinding;
/*      */     
/*      */     public Object value;
/*      */     
/*      */     BindValue() {}
/*      */     
/*      */     BindValue(BindValue copyMe)
/*      */     {
/*  121 */       this.value = copyMe.value;
/*  122 */       this.isSet = copyMe.isSet;
/*  123 */       this.isLongData = copyMe.isLongData;
/*  124 */       this.isNull = copyMe.isNull;
/*  125 */       this.bufferType = copyMe.bufferType;
/*  126 */       this.bindLength = copyMe.bindLength;
/*  127 */       this.longBinding = copyMe.longBinding;
/*  128 */       this.floatBinding = copyMe.floatBinding;
/*  129 */       this.doubleBinding = copyMe.doubleBinding;
/*      */     }
/*      */     
/*      */     void reset() {
/*  133 */       this.isNull = false;
/*  134 */       this.isSet = false;
/*  135 */       this.value = null;
/*  136 */       this.isLongData = false;
/*      */       
/*  138 */       this.longBinding = 0L;
/*  139 */       this.floatBinding = 0.0F;
/*  140 */       this.doubleBinding = 0.0D;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*  145 */       return toString(false);
/*      */     }
/*      */     
/*      */     public String toString(boolean quoteIfNeeded) {
/*  149 */       if (this.isLongData) {
/*  150 */         return "' STREAM DATA '";
/*      */       }
/*      */       
/*  153 */       if (this.isNull) {
/*  154 */         return "NULL";
/*      */       }
/*      */       
/*  157 */       switch (this.bufferType) {
/*      */       case 1: 
/*      */       case 2: 
/*      */       case 3: 
/*      */       case 8: 
/*  162 */         return String.valueOf(this.longBinding);
/*      */       case 4: 
/*  164 */         return String.valueOf(this.floatBinding);
/*      */       case 5: 
/*  166 */         return String.valueOf(this.doubleBinding);
/*      */       case 7: 
/*      */       case 10: 
/*      */       case 11: 
/*      */       case 12: 
/*      */       case 15: 
/*      */       case 253: 
/*      */       case 254: 
/*  174 */         if (quoteIfNeeded) {
/*  175 */           return "'" + String.valueOf(this.value) + "'";
/*      */         }
/*  177 */         return String.valueOf(this.value);
/*      */       }
/*      */       
/*  180 */       if ((this.value instanceof byte[])) {
/*  181 */         return "byte data";
/*      */       }
/*  183 */       if (quoteIfNeeded) {
/*  184 */         return "'" + String.valueOf(this.value) + "'";
/*      */       }
/*  186 */       return String.valueOf(this.value);
/*      */     }
/*      */     
/*      */     long getBoundLength()
/*      */     {
/*  191 */       if (this.isNull) {
/*  192 */         return 0L;
/*      */       }
/*      */       
/*  195 */       if (this.isLongData) {
/*  196 */         return this.bindLength;
/*      */       }
/*      */       
/*  199 */       switch (this.bufferType)
/*      */       {
/*      */       case 1: 
/*  202 */         return 1L;
/*      */       case 2: 
/*  204 */         return 2L;
/*      */       case 3: 
/*  206 */         return 4L;
/*      */       case 8: 
/*  208 */         return 8L;
/*      */       case 4: 
/*  210 */         return 4L;
/*      */       case 5: 
/*  212 */         return 8L;
/*      */       case 11: 
/*  214 */         return 9L;
/*      */       case 10: 
/*  216 */         return 7L;
/*      */       case 7: 
/*      */       case 12: 
/*  219 */         return 11L;
/*      */       case 0: 
/*      */       case 15: 
/*      */       case 246: 
/*      */       case 253: 
/*      */       case 254: 
/*  225 */         if ((this.value instanceof byte[])) {
/*  226 */           return ((byte[])this.value).length;
/*      */         }
/*  228 */         return ((String)this.value).length();
/*      */       }
/*      */       
/*  231 */       return 0L;
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
/*  251 */   private boolean hasOnDuplicateKeyUpdate = false;
/*      */   
/*      */   private void storeTime(Buffer intoBuf, Time tm) throws SQLException
/*      */   {
/*  255 */     intoBuf.ensureCapacity(9);
/*  256 */     intoBuf.writeByte((byte)8);
/*  257 */     intoBuf.writeByte((byte)0);
/*  258 */     intoBuf.writeLong(0L);
/*      */     
/*  260 */     Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */     
/*  262 */     synchronized (sessionCalendar) {
/*  263 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try {
/*  265 */         sessionCalendar.setTime(tm);
/*  266 */         intoBuf.writeByte((byte)sessionCalendar.get(11));
/*  267 */         intoBuf.writeByte((byte)sessionCalendar.get(12));
/*  268 */         intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */       }
/*      */       finally
/*      */       {
/*  272 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  282 */   private boolean detectedLongParameterSwitch = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private int fieldCount;
/*      */   
/*      */ 
/*      */ 
/*  291 */   private boolean invalid = false;
/*      */   
/*      */ 
/*      */   private SQLException invalidationException;
/*      */   
/*      */ 
/*      */   private Buffer outByteBuffer;
/*      */   
/*      */ 
/*      */   private BindValue[] parameterBindings;
/*      */   
/*      */ 
/*      */   private Field[] parameterFields;
/*      */   
/*      */ 
/*      */   private Field[] resultFields;
/*      */   
/*  308 */   private boolean sendTypesToServer = false;
/*      */   
/*      */ 
/*      */   private long serverStatementId;
/*      */   
/*      */ 
/*  314 */   private int stringTypeCode = 254;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean serverNeedsResetBeforeEachExecution;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static ServerPreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  327 */     if (!Util.isJdbc4()) {
/*  328 */       return new ServerPreparedStatement(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*      */     }
/*      */     try
/*      */     {
/*  332 */       return (ServerPreparedStatement)JDBC_4_SPS_CTOR.newInstance(new Object[] { conn, sql, catalog, Integer.valueOf(resultSetType), Integer.valueOf(resultSetConcurrency) });
/*      */     }
/*      */     catch (IllegalArgumentException e) {
/*  335 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InstantiationException e) {
/*  337 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (IllegalAccessException e) {
/*  339 */       throw new SQLException(e.toString(), "S1000");
/*      */     } catch (InvocationTargetException e) {
/*  341 */       Throwable target = e.getTargetException();
/*      */       
/*  343 */       if ((target instanceof SQLException)) {
/*  344 */         throw ((SQLException)target);
/*      */       }
/*      */       
/*  347 */       throw new SQLException(target.toString(), "S1000");
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
/*      */   protected ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/*  365 */     super(conn, catalog);
/*      */     
/*  367 */     checkNullOrEmptyQuery(sql);
/*      */     
/*  369 */     int startOfStatement = findStartOfStatement(sql);
/*      */     
/*  371 */     this.firstCharOfStmt = StringUtils.firstAlphaCharUc(sql, startOfStatement);
/*      */     
/*  373 */     this.hasOnDuplicateKeyUpdate = ((this.firstCharOfStmt == 'I') && (containsOnDuplicateKeyInString(sql)));
/*      */     
/*  375 */     if (this.connection.versionMeetsMinimum(5, 0, 0)) {
/*  376 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(5, 0, 3));
/*      */     } else {
/*  378 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(4, 1, 10));
/*      */     }
/*      */     
/*  381 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*  382 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */     
/*  384 */     String statementComment = this.connection.getStatementComment();
/*      */     
/*  386 */     this.originalSql = ("/* " + statementComment + " */ " + sql);
/*      */     
/*  388 */     if (this.connection.versionMeetsMinimum(4, 1, 2)) {
/*  389 */       this.stringTypeCode = 253;
/*      */     } else {
/*  391 */       this.stringTypeCode = 254;
/*      */     }
/*      */     try
/*      */     {
/*  395 */       serverPrepare(sql);
/*      */     } catch (SQLException sqlEx) {
/*  397 */       realClose(false, true);
/*      */       
/*  399 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  401 */       realClose(false, true);
/*      */       
/*  403 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*  404 */       sqlEx.initCause(ex);
/*      */       
/*  406 */       throw sqlEx;
/*      */     }
/*      */     
/*  409 */     setResultSetType(resultSetType);
/*  410 */     setResultSetConcurrency(resultSetConcurrency);
/*      */     
/*  412 */     this.parameterTypes = new int[this.parameterCount];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  425 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  427 */       if (this.batchedArgs == null) {
/*  428 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */       
/*  431 */       this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
/*      */     }
/*      */   }
/*      */   
/*      */   public String asSql(boolean quoteStreamsAndUnknowns)
/*      */     throws SQLException
/*      */   {
/*  438 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  440 */       PreparedStatement pStmtForSub = null;
/*      */       try
/*      */       {
/*  443 */         pStmtForSub = PreparedStatement.getInstance(this.connection, this.originalSql, this.currentCatalog);
/*      */         
/*  445 */         int numParameters = pStmtForSub.parameterCount;
/*  446 */         int ourNumParameters = this.parameterCount;
/*      */         
/*  448 */         for (int i = 0; (i < numParameters) && (i < ourNumParameters); i++) {
/*  449 */           if (this.parameterBindings[i] != null) {
/*  450 */             if (this.parameterBindings[i].isNull) {
/*  451 */               pStmtForSub.setNull(i + 1, 0);
/*      */             } else {
/*  453 */               BindValue bindValue = this.parameterBindings[i];
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  458 */               switch (bindValue.bufferType)
/*      */               {
/*      */               case 1: 
/*  461 */                 pStmtForSub.setByte(i + 1, (byte)(int)bindValue.longBinding);
/*  462 */                 break;
/*      */               case 2: 
/*  464 */                 pStmtForSub.setShort(i + 1, (short)(int)bindValue.longBinding);
/*  465 */                 break;
/*      */               case 3: 
/*  467 */                 pStmtForSub.setInt(i + 1, (int)bindValue.longBinding);
/*  468 */                 break;
/*      */               case 8: 
/*  470 */                 pStmtForSub.setLong(i + 1, bindValue.longBinding);
/*  471 */                 break;
/*      */               case 4: 
/*  473 */                 pStmtForSub.setFloat(i + 1, bindValue.floatBinding);
/*  474 */                 break;
/*      */               case 5: 
/*  476 */                 pStmtForSub.setDouble(i + 1, bindValue.doubleBinding);
/*  477 */                 break;
/*      */               case 6: case 7: default: 
/*  479 */                 pStmtForSub.setObject(i + 1, this.parameterBindings[i].value);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  486 */         i = pStmtForSub.asSql(quoteStreamsAndUnknowns);jsr 16;return i;
/*      */       } finally {
/*  488 */         jsr 6; } localObject2 = returnAddress; if (pStmtForSub != null) {
/*      */         try {
/*  490 */           pStmtForSub.close();
/*      */         } catch (SQLException sqlEx) {}
/*      */       }
/*  493 */       ret;
/*      */     }
/*      */   }
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
/*  506 */     if (this.invalid) {
/*  507 */       throw this.invalidationException;
/*      */     }
/*      */     
/*  510 */     return super.checkClosed();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  518 */     synchronized (checkClosed().getConnectionMutex()) {
/*  519 */       clearParametersInternal(true);
/*      */     }
/*      */   }
/*      */   
/*      */   private void clearParametersInternal(boolean clearServerParameters) throws SQLException {
/*  524 */     boolean hadLongData = false;
/*      */     
/*  526 */     if (this.parameterBindings != null) {
/*  527 */       for (int i = 0; i < this.parameterCount; i++) {
/*  528 */         if ((this.parameterBindings[i] != null) && (this.parameterBindings[i].isLongData)) {
/*  529 */           hadLongData = true;
/*      */         }
/*      */         
/*  532 */         this.parameterBindings[i].reset();
/*      */       }
/*      */     }
/*      */     
/*  536 */     if ((clearServerParameters) && (hadLongData)) {
/*  537 */       serverResetStatement();
/*      */       
/*  539 */       this.detectedLongParameterSwitch = false;
/*      */     }
/*      */   }
/*      */   
/*  543 */   protected boolean isCached = false;
/*      */   
/*      */   private boolean useAutoSlowLog;
/*      */   
/*      */   private Calendar serverTzCalendar;
/*      */   private Calendar defaultTzCalendar;
/*      */   
/*      */   protected void setClosed(boolean flag)
/*      */   {
/*  552 */     this.isClosed = flag;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  560 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/*  562 */     if (locallyScopedConn == null) {
/*  563 */       return;
/*      */     }
/*      */     
/*  566 */     synchronized (locallyScopedConn.getConnectionMutex())
/*      */     {
/*  568 */       if ((this.isCached) && (!this.isClosed)) {
/*  569 */         clearParameters();
/*      */         
/*  571 */         this.isClosed = true;
/*      */         
/*  573 */         this.connection.recachePreparedStatement(this);
/*  574 */         return;
/*      */       }
/*      */       
/*  577 */       realClose(true, true);
/*      */     }
/*      */   }
/*      */   
/*      */   private void dumpCloseForTestcase() throws SQLException {
/*  582 */     synchronized (checkClosed().getConnectionMutex()) {
/*  583 */       StringBuilder buf = new StringBuilder();
/*  584 */       this.connection.generateConnectionCommentBlock(buf);
/*  585 */       buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  586 */       buf.append(this.statementId);
/*  587 */       buf.append(";\n");
/*      */       
/*  589 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   private void dumpExecuteForTestcase() throws SQLException {
/*  594 */     synchronized (checkClosed().getConnectionMutex()) {
/*  595 */       StringBuilder buf = new StringBuilder();
/*      */       
/*  597 */       for (int i = 0; i < this.parameterCount; i++) {
/*  598 */         this.connection.generateConnectionCommentBlock(buf);
/*      */         
/*  600 */         buf.append("SET @debug_stmt_param");
/*  601 */         buf.append(this.statementId);
/*  602 */         buf.append("_");
/*  603 */         buf.append(i);
/*  604 */         buf.append("=");
/*      */         
/*  606 */         if (this.parameterBindings[i].isNull) {
/*  607 */           buf.append("NULL");
/*      */         } else {
/*  609 */           buf.append(this.parameterBindings[i].toString(true));
/*      */         }
/*      */         
/*  612 */         buf.append(";\n");
/*      */       }
/*      */       
/*  615 */       this.connection.generateConnectionCommentBlock(buf);
/*      */       
/*  617 */       buf.append("EXECUTE debug_stmt_");
/*  618 */       buf.append(this.statementId);
/*      */       
/*  620 */       if (this.parameterCount > 0) {
/*  621 */         buf.append(" USING ");
/*  622 */         for (int i = 0; i < this.parameterCount; i++) {
/*  623 */           if (i > 0) {
/*  624 */             buf.append(", ");
/*      */           }
/*      */           
/*  627 */           buf.append("@debug_stmt_param");
/*  628 */           buf.append(this.statementId);
/*  629 */           buf.append("_");
/*  630 */           buf.append(i);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  635 */       buf.append(";\n");
/*      */       
/*  637 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   private void dumpPrepareForTestcase() throws SQLException {
/*  642 */     synchronized (checkClosed().getConnectionMutex()) {
/*  643 */       StringBuilder buf = new StringBuilder(this.originalSql.length() + 64);
/*      */       
/*  645 */       this.connection.generateConnectionCommentBlock(buf);
/*      */       
/*  647 */       buf.append("PREPARE debug_stmt_");
/*  648 */       buf.append(this.statementId);
/*  649 */       buf.append(" FROM \"");
/*  650 */       buf.append(this.originalSql);
/*  651 */       buf.append("\";\n");
/*      */       
/*  653 */       this.connection.dumpTestcaseQuery(buf.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   protected long[] executeBatchSerially(int batchTimeout) throws SQLException { MySQLConnection locallyScopedConn;
/*      */     BindValue[] oldBindValues;
/*  659 */     synchronized (checkClosed().getConnectionMutex()) {
/*  660 */       locallyScopedConn = this.connection;
/*      */       
/*  662 */       if (locallyScopedConn.isReadOnly()) {
/*  663 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  667 */       clearWarnings();
/*      */       
/*      */ 
/*      */ 
/*  671 */       oldBindValues = this.parameterBindings;
/*      */     }
/*      */     try {
/*  674 */       long[] updateCounts = null;
/*      */       
/*  676 */       if (this.batchedArgs != null) {
/*  677 */         nbrCommands = this.batchedArgs.size();
/*  678 */         updateCounts = new long[nbrCommands];
/*      */         
/*  680 */         if (this.retrieveGeneratedKeys) {
/*  681 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */         
/*  684 */         for (int i = 0; i < nbrCommands; i++) {
/*  685 */           updateCounts[i] = -3L;
/*      */         }
/*      */         
/*  688 */         SQLException sqlEx = null;
/*      */         
/*  690 */         int commandIndex = 0;
/*      */         
/*  692 */         BindValue[] previousBindValuesForBatch = null;
/*      */         
/*  694 */         CancelTask timeoutTask = null;
/*      */         try
/*      */         {
/*  697 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/*  698 */             timeoutTask = new CancelTask(this, this);
/*  699 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */           
/*  702 */           for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  703 */             Object arg = this.batchedArgs.get(commandIndex);
/*      */             try
/*      */             {
/*  706 */               if ((arg instanceof String)) {
/*  707 */                 updateCounts[commandIndex] = executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
/*      */                 
/*      */ 
/*  710 */                 getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I') && (containsOnDuplicateKeyInString((String)arg)) ? 1 : 0);
/*      */               } else {
/*  712 */                 this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
/*      */                 
/*      */ 
/*      */ 
/*  716 */                 if (previousBindValuesForBatch != null) {
/*  717 */                   for (int j = 0; j < this.parameterBindings.length; j++) {
/*  718 */                     if (this.parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
/*  719 */                       this.sendTypesToServer = true;
/*      */                       
/*  721 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/*  727 */                   updateCounts[commandIndex] = executeUpdateInternal(false, true);
/*      */                 } finally {
/*  729 */                   previousBindValuesForBatch = this.parameterBindings;
/*      */                 }
/*      */                 
/*      */ 
/*  733 */                 getBatchedGeneratedKeys(containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
/*      */               }
/*      */             } catch (SQLException ex) {
/*  736 */               updateCounts[commandIndex] = -3L;
/*      */               
/*  738 */               if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */               {
/*  740 */                 sqlEx = ex;
/*      */               } else {
/*  742 */                 long[] newUpdateCounts = new long[commandIndex];
/*  743 */                 System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */                 
/*  745 */                 throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */           }
/*      */         } finally {
/*  750 */           if (timeoutTask != null) {
/*  751 */             timeoutTask.cancel();
/*      */             
/*  753 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */           
/*  756 */           resetCancelledState();
/*      */         }
/*      */         
/*  759 */         if (sqlEx != null) {
/*  760 */           throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/*  764 */       int nbrCommands = updateCounts != null ? updateCounts : new long[0];jsr 16;return nbrCommands;
/*      */     } finally {
/*  766 */       jsr 6; } localObject6 = returnAddress;this.parameterBindings = oldBindValues;
/*  767 */     this.sendTypesToServer = true;
/*      */     
/*  769 */     clearBatch();ret;
/*      */     
/*  771 */     localObject7 = finally;throw ((Throwable)localObject7);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/*  780 */     synchronized (checkClosed().getConnectionMutex()) {
/*  781 */       this.numberOfExecutions += 1;
/*      */       
/*      */       try
/*      */       {
/*  785 */         return serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadataFromCache);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/*  788 */         if (this.connection.getEnablePacketDebug()) {
/*  789 */           this.connection.getIO().dumpPacketRingBuffer();
/*      */         }
/*      */         
/*  792 */         if (this.connection.getDumpQueriesOnException()) {
/*  793 */           String extractedSql = toString();
/*  794 */           StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/*  795 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/*  796 */           messageBuf.append(extractedSql);
/*  797 */           messageBuf.append("\n\n");
/*      */           
/*  799 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */         
/*  802 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/*  804 */         if (this.connection.getEnablePacketDebug()) {
/*  805 */           this.connection.getIO().dumpPacketRingBuffer();
/*      */         }
/*      */         
/*  808 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/*      */         
/*  810 */         if (this.connection.getDumpQueriesOnException()) {
/*  811 */           String extractedSql = toString();
/*  812 */           StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
/*  813 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
/*  814 */           messageBuf.append(extractedSql);
/*  815 */           messageBuf.append("\n\n");
/*      */           
/*  817 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */         
/*  820 */         sqlEx.initCause(ex);
/*      */         
/*  822 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/*  832 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/*  841 */     return null;
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
/*      */   protected BindValue getBinding(int parameterIndex, boolean forLongData)
/*      */     throws SQLException
/*      */   {
/*  855 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  857 */       if (this.parameterBindings.length == 0) {
/*  858 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.8"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  862 */       parameterIndex--;
/*      */       
/*  864 */       if ((parameterIndex < 0) || (parameterIndex >= this.parameterBindings.length)) {
/*  865 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  870 */       if (this.parameterBindings[parameterIndex] == null) {
/*  871 */         this.parameterBindings[parameterIndex] = new BindValue();
/*      */       }
/*  873 */       else if ((this.parameterBindings[parameterIndex].isLongData) && (!forLongData)) {
/*  874 */         this.detectedLongParameterSwitch = true;
/*      */       }
/*      */       
/*      */ 
/*  878 */       return this.parameterBindings[parameterIndex];
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BindValue[] getParameterBindValues()
/*      */   {
/*  890 */     return this.parameterBindings;
/*      */   }
/*      */   
/*      */ 
/*      */   byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  897 */     synchronized (checkClosed().getConnectionMutex()) {
/*  898 */       BindValue bindValue = getBinding(parameterIndex, false);
/*      */       
/*  900 */       if (bindValue.isNull)
/*  901 */         return null;
/*  902 */       if (bindValue.isLongData) {
/*  903 */         throw SQLError.createSQLFeatureNotSupportedException();
/*      */       }
/*  905 */       if (this.outByteBuffer == null) {
/*  906 */         this.outByteBuffer = new Buffer(this.connection.getNetBufferLength());
/*      */       }
/*      */       
/*  909 */       this.outByteBuffer.clear();
/*      */       
/*  911 */       int originalPosition = this.outByteBuffer.getPosition();
/*      */       
/*  913 */       storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
/*      */       
/*  915 */       int newPosition = this.outByteBuffer.getPosition();
/*      */       
/*  917 */       int length = newPosition - originalPosition;
/*      */       
/*  919 */       byte[] valueAsBytes = new byte[length];
/*      */       
/*  921 */       System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
/*      */       
/*  923 */       return valueAsBytes;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  933 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  935 */       if (this.resultFields == null) {
/*  936 */         return null;
/*      */       }
/*      */       
/*  939 */       return new ResultSetMetaData(this.resultFields, this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/*  949 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  951 */       if (this.parameterMetaData == null) {
/*  952 */         this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount, getExceptionInterceptor());
/*      */       }
/*      */       
/*  955 */       return this.parameterMetaData;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/*  964 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
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
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/*  978 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/*  980 */     if (locallyScopedConn == null) {
/*  981 */       return;
/*      */     }
/*      */     
/*  984 */     synchronized (locallyScopedConn.getConnectionMutex())
/*      */     {
/*  986 */       if (this.connection != null) {
/*  987 */         if (this.connection.getAutoGenerateTestcaseScript()) {
/*  988 */           dumpCloseForTestcase();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  998 */         SQLException exceptionDuringClose = null;
/*      */         
/* 1000 */         if ((calledExplicitly) && (!this.connection.isClosed())) {
/* 1001 */           synchronized (this.connection.getConnectionMutex())
/*      */           {
/*      */             try {
/* 1004 */               MysqlIO mysql = this.connection.getIO();
/*      */               
/* 1006 */               Buffer packet = mysql.getSharedSendPacket();
/*      */               
/* 1008 */               packet.writeByte((byte)25);
/* 1009 */               packet.writeLong(this.serverStatementId);
/*      */               
/* 1011 */               mysql.sendCommand(25, null, packet, true, null, 0);
/*      */             } catch (SQLException sqlEx) {
/* 1013 */               exceptionDuringClose = sqlEx;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1018 */         if (this.isCached) {
/* 1019 */           this.connection.decachePreparedStatement(this);
/*      */         }
/* 1021 */         super.realClose(calledExplicitly, closeOpenResults);
/*      */         
/* 1023 */         clearParametersInternal(false);
/* 1024 */         this.parameterBindings = null;
/*      */         
/* 1026 */         this.parameterFields = null;
/* 1027 */         this.resultFields = null;
/*      */         
/* 1029 */         if (exceptionDuringClose != null) {
/* 1030 */           throw exceptionDuringClose;
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
/*      */   protected void rePrepare()
/*      */     throws SQLException
/*      */   {
/* 1044 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1045 */       this.invalidationException = null;
/*      */       try
/*      */       {
/* 1048 */         serverPrepare(this.originalSql);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1051 */         this.invalidationException = sqlEx;
/*      */       } catch (Exception ex) {
/* 1053 */         this.invalidationException = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/* 1054 */         this.invalidationException.initCause(ex);
/*      */       }
/*      */       
/* 1057 */       if (this.invalidationException != null) {
/* 1058 */         this.invalid = true;
/*      */         
/* 1060 */         this.parameterBindings = null;
/*      */         
/* 1062 */         this.parameterFields = null;
/* 1063 */         this.resultFields = null;
/*      */         
/* 1065 */         if (this.results != null) {
/*      */           try {
/* 1067 */             this.results.close();
/*      */           }
/*      */           catch (Exception ex) {}
/*      */         }
/*      */         
/* 1072 */         if (this.generatedKeysResults != null) {
/*      */           try {
/* 1074 */             this.generatedKeysResults.close();
/*      */           }
/*      */           catch (Exception ex) {}
/*      */         }
/*      */         try
/*      */         {
/* 1080 */           closeAllOpenResults();
/*      */         }
/*      */         catch (Exception e) {}
/*      */         
/* 1084 */         if ((this.connection != null) && 
/* 1085 */           (!this.connection.getDontTrackOpenResources())) {
/* 1086 */           this.connection.unregisterStatement(this);
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
/*      */   boolean isCursorRequired()
/*      */     throws SQLException
/*      */   {
/* 1100 */     return (this.resultFields != null) && (this.connection.isCursorFetchEnabled()) && (getResultSetType() == 1003) && (getResultSetConcurrency() == 1007) && (getFetchSize() > 0);
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
/*      */   private ResultSetInternalMethods serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 1135 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1136 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1138 */       if (mysql.shouldIntercept()) {
/* 1139 */         ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPre(this.originalSql, this, true);
/*      */         
/* 1141 */         if (interceptedResults != null) {
/* 1142 */           return interceptedResults;
/*      */         }
/*      */       }
/*      */       
/* 1146 */       if (this.detectedLongParameterSwitch)
/*      */       {
/* 1148 */         boolean firstFound = false;
/* 1149 */         long boundTimeToCheck = 0L;
/*      */         
/* 1151 */         for (int i = 0; i < this.parameterCount - 1; i++) {
/* 1152 */           if (this.parameterBindings[i].isLongData) {
/* 1153 */             if ((firstFound) && (boundTimeToCheck != this.parameterBindings[i].boundBeforeExecutionNum)) {
/* 1154 */               throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00", getExceptionInterceptor());
/*      */             }
/*      */             
/*      */ 
/* 1158 */             firstFound = true;
/* 1159 */             boundTimeToCheck = this.parameterBindings[i].boundBeforeExecutionNum;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1165 */         serverResetStatement();
/*      */       }
/*      */       
/*      */ 
/* 1169 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1170 */         if (!this.parameterBindings[i].isSet) {
/* 1171 */           throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.13") + (i + 1) + Messages.getString("ServerPreparedStatement.14"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1180 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1181 */         if (this.parameterBindings[i].isLongData) {
/* 1182 */           serverLongData(i, this.parameterBindings[i]);
/*      */         }
/*      */       }
/*      */       
/* 1186 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1187 */         dumpExecuteForTestcase();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1194 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1196 */       packet.clear();
/* 1197 */       packet.writeByte((byte)23);
/* 1198 */       packet.writeLong(this.serverStatementId);
/*      */       
/* 1200 */       if (this.connection.versionMeetsMinimum(4, 1, 2)) {
/* 1201 */         if (isCursorRequired()) {
/* 1202 */           packet.writeByte((byte)1);
/*      */         } else {
/* 1204 */           packet.writeByte((byte)0);
/*      */         }
/*      */         
/* 1207 */         packet.writeLong(1L);
/*      */       }
/*      */       
/*      */ 
/* 1211 */       int nullCount = (this.parameterCount + 7) / 8;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1216 */       int nullBitsPosition = packet.getPosition();
/*      */       
/* 1218 */       for (int i = 0; i < nullCount; i++) {
/* 1219 */         packet.writeByte((byte)0);
/*      */       }
/*      */       
/* 1222 */       byte[] nullBitsBuffer = new byte[nullCount];
/*      */       
/*      */ 
/* 1225 */       packet.writeByte((byte)(this.sendTypesToServer ? 1 : 0));
/*      */       
/* 1227 */       if (this.sendTypesToServer)
/*      */       {
/*      */ 
/*      */ 
/* 1231 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1232 */           packet.writeInt(this.parameterBindings[i].bufferType);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1239 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1240 */         if (!this.parameterBindings[i].isLongData) {
/* 1241 */           if (!this.parameterBindings[i].isNull) {
/* 1242 */             storeBinding(packet, this.parameterBindings[i], mysql);
/*      */           } else {
/* 1244 */             int tmp550_549 = (i / 8); byte[] tmp550_543 = nullBitsBuffer;tmp550_543[tmp550_549] = ((byte)(tmp550_543[tmp550_549] | 1 << (i & 0x7)));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1252 */       int endPosition = packet.getPosition();
/* 1253 */       packet.setPosition(nullBitsPosition);
/* 1254 */       packet.writeBytesNoNull(nullBitsBuffer);
/* 1255 */       packet.setPosition(endPosition);
/*      */       
/* 1257 */       long begin = 0L;
/*      */       
/* 1259 */       boolean logSlowQueries = this.connection.getLogSlowQueries();
/* 1260 */       boolean gatherPerformanceMetrics = this.connection.getGatherPerformanceMetrics();
/*      */       
/* 1262 */       if ((this.profileSQL) || (logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1263 */         begin = mysql.getCurrentTimeNanosOrMillis();
/*      */       }
/*      */       
/* 1266 */       resetCancelledState();
/*      */       
/* 1268 */       CancelTask timeoutTask = null;
/*      */       
/*      */       try
/*      */       {
/* 1272 */         String queryAsString = "";
/* 1273 */         if ((this.profileSQL) || (logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1274 */           queryAsString = asSql(true);
/*      */         }
/*      */         
/* 1277 */         if ((this.connection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (this.connection.versionMeetsMinimum(5, 0, 0))) {
/* 1278 */           timeoutTask = new CancelTask(this, this);
/* 1279 */           this.connection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */         }
/*      */         
/* 1282 */         statementBegins();
/*      */         
/* 1284 */         Buffer resultPacket = mysql.sendCommand(23, null, packet, false, null, 0);
/*      */         
/* 1286 */         long queryEndTime = 0L;
/*      */         
/* 1288 */         if ((logSlowQueries) || (gatherPerformanceMetrics) || (this.profileSQL)) {
/* 1289 */           queryEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */         }
/*      */         
/* 1292 */         if (timeoutTask != null) {
/* 1293 */           timeoutTask.cancel();
/*      */           
/* 1295 */           this.connection.getCancelTimer().purge();
/*      */           
/* 1297 */           if (timeoutTask.caughtWhileCancelling != null) {
/* 1298 */             throw timeoutTask.caughtWhileCancelling;
/*      */           }
/*      */           
/* 1301 */           timeoutTask = null;
/*      */         }
/*      */         
/* 1304 */         synchronized (this.cancelTimeoutMutex) {
/* 1305 */           if (this.wasCancelled) {
/* 1306 */             SQLException cause = null;
/*      */             
/* 1308 */             if (this.wasCancelledByTimeout) {
/* 1309 */               cause = new MySQLTimeoutException();
/*      */             } else {
/* 1311 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */             
/* 1314 */             resetCancelledState();
/*      */             
/* 1316 */             throw cause;
/*      */           }
/*      */         }
/*      */         
/* 1320 */         boolean queryWasSlow = false;
/*      */         
/* 1322 */         if ((logSlowQueries) || (gatherPerformanceMetrics)) {
/* 1323 */           long elapsedTime = queryEndTime - begin;
/*      */           
/* 1325 */           if (logSlowQueries) {
/* 1326 */             if (this.useAutoSlowLog) {
/* 1327 */               queryWasSlow = elapsedTime > this.connection.getSlowQueryThresholdMillis();
/*      */             } else {
/* 1329 */               queryWasSlow = this.connection.isAbonormallyLongQuery(elapsedTime);
/*      */               
/* 1331 */               this.connection.reportQueryTime(elapsedTime);
/*      */             }
/*      */           }
/*      */           
/* 1335 */           if (queryWasSlow)
/*      */           {
/* 1337 */             StringBuilder mesgBuf = new StringBuilder(48 + this.originalSql.length());
/* 1338 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15"));
/* 1339 */             mesgBuf.append(mysql.getSlowQueryThreshold());
/* 1340 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.15a"));
/* 1341 */             mesgBuf.append(elapsedTime);
/* 1342 */             mesgBuf.append(Messages.getString("ServerPreparedStatement.16"));
/*      */             
/* 1344 */             mesgBuf.append("as prepared: ");
/* 1345 */             mesgBuf.append(this.originalSql);
/* 1346 */             mesgBuf.append("\n\n with parameters bound:\n\n");
/* 1347 */             mesgBuf.append(queryAsString);
/*      */             
/* 1349 */             this.eventSink.consumeEvent(new ProfilerEvent((byte)6, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), elapsedTime, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), mesgBuf.toString()));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1354 */           if (gatherPerformanceMetrics) {
/* 1355 */             this.connection.registerQueryExecutionTime(elapsedTime);
/*      */           }
/*      */         }
/*      */         
/* 1359 */         this.connection.incrementNumberOfPreparedExecutes();
/*      */         
/* 1361 */         if (this.profileSQL) {
/* 1362 */           this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */           
/* 1364 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)4, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), truncateQueryToLog(queryAsString)));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1369 */         ResultSetInternalMethods rs = mysql.readAllResults(this, maxRowsToRetrieve, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, resultPacket, true, this.fieldCount, metadataFromCache);
/*      */         
/*      */ 
/* 1372 */         if (mysql.shouldIntercept()) {
/* 1373 */           ResultSetInternalMethods interceptedResults = mysql.invokeStatementInterceptorsPost(this.originalSql, this, rs, true, null);
/*      */           
/* 1375 */           if (interceptedResults != null) {
/* 1376 */             rs = interceptedResults;
/*      */           }
/*      */         }
/*      */         long fetchEndTime;
/* 1380 */         if (this.profileSQL) {
/* 1381 */           fetchEndTime = mysql.getCurrentTimeNanosOrMillis();
/*      */           
/* 1383 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)5, "", this.currentCatalog, this.connection.getId(), getId(), 0, System.currentTimeMillis(), fetchEndTime - queryEndTime, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), null));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1392 */         if ((queryWasSlow) && (this.connection.getExplainSlowQueries())) {
/* 1393 */           mysql.explainSlowQuery(StringUtils.getBytes(queryAsString), queryAsString);
/*      */         }
/*      */         
/* 1396 */         if ((!createStreamingResultSet) && (this.serverNeedsResetBeforeEachExecution)) {
/* 1397 */           serverResetStatement();
/*      */         }
/*      */         
/* 1400 */         this.sendTypesToServer = false;
/* 1401 */         this.results = rs;
/*      */         
/* 1403 */         if (mysql.hadWarnings()) {
/* 1404 */           mysql.scanForAndThrowDataTruncation();
/*      */         }
/*      */         
/* 1407 */         ResultSetInternalMethods localResultSetInternalMethods1 = rs;jsr 45;return localResultSetInternalMethods1;
/*      */       } catch (SQLException sqlEx) {
/* 1409 */         if (mysql.shouldIntercept()) {
/* 1410 */           mysql.invokeStatementInterceptorsPost(this.originalSql, this, null, true, sqlEx);
/*      */         }
/*      */         
/* 1413 */         throw sqlEx;
/*      */       } finally {
/* 1415 */         jsr 6; } localObject3 = returnAddress;this.statementExecuting.set(false);
/*      */       
/* 1417 */       if (timeoutTask != null) {
/* 1418 */         timeoutTask.cancel();
/* 1419 */         this.connection.getCancelTimer().purge(); } ret;
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
/*      */   private void serverLongData(int parameterIndex, BindValue longData)
/*      */     throws SQLException
/*      */   {
/* 1451 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1452 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1454 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1456 */       Object value = longData.value;
/*      */       
/* 1458 */       if ((value instanceof byte[])) {
/* 1459 */         packet.clear();
/* 1460 */         packet.writeByte((byte)24);
/* 1461 */         packet.writeLong(this.serverStatementId);
/* 1462 */         packet.writeInt(parameterIndex);
/*      */         
/* 1464 */         packet.writeBytesNoNull((byte[])longData.value);
/*      */         
/* 1466 */         mysql.sendCommand(24, null, packet, true, null, 0);
/* 1467 */       } else if ((value instanceof InputStream)) {
/* 1468 */         storeStream(mysql, parameterIndex, packet, (InputStream)value);
/* 1469 */       } else if ((value instanceof Blob)) {
/* 1470 */         storeStream(mysql, parameterIndex, packet, ((Blob)value).getBinaryStream());
/* 1471 */       } else if ((value instanceof Reader)) {
/* 1472 */         storeReader(mysql, parameterIndex, packet, (Reader)value);
/*      */       } else {
/* 1474 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void serverPrepare(String sql) throws SQLException
/*      */   {
/* 1481 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1482 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1484 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1485 */         dumpPrepareForTestcase();
/*      */       }
/*      */       try
/*      */       {
/* 1489 */         long begin = 0L;
/*      */         
/* 1491 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA")) {
/* 1492 */           this.isLoadDataQuery = true;
/*      */         } else {
/* 1494 */           this.isLoadDataQuery = false;
/*      */         }
/*      */         
/* 1497 */         if (this.connection.getProfileSql()) {
/* 1498 */           begin = System.currentTimeMillis();
/*      */         }
/*      */         
/* 1501 */         String characterEncoding = null;
/* 1502 */         String connectionEncoding = this.connection.getEncoding();
/*      */         
/* 1504 */         if ((!this.isLoadDataQuery) && (this.connection.getUseUnicode()) && (connectionEncoding != null)) {
/* 1505 */           characterEncoding = connectionEncoding;
/*      */         }
/*      */         
/* 1508 */         Buffer prepareResultPacket = mysql.sendCommand(22, sql, null, false, characterEncoding, 0);
/*      */         
/* 1510 */         if (this.connection.versionMeetsMinimum(4, 1, 1))
/*      */         {
/* 1512 */           prepareResultPacket.setPosition(1);
/*      */         }
/*      */         else {
/* 1515 */           prepareResultPacket.setPosition(0);
/*      */         }
/*      */         
/* 1518 */         this.serverStatementId = prepareResultPacket.readLong();
/* 1519 */         this.fieldCount = prepareResultPacket.readInt();
/* 1520 */         this.parameterCount = prepareResultPacket.readInt();
/* 1521 */         this.parameterBindings = new BindValue[this.parameterCount];
/*      */         
/* 1523 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1524 */           this.parameterBindings[i] = new BindValue();
/*      */         }
/*      */         
/* 1527 */         this.connection.incrementNumberOfPrepares();
/*      */         
/* 1529 */         if (this.profileSQL) {
/* 1530 */           this.eventSink.consumeEvent(new ProfilerEvent((byte)2, "", this.currentCatalog, this.connectionId, this.statementId, -1, System.currentTimeMillis(), mysql.getCurrentTimeNanosOrMillis() - begin, mysql.getQueryTimingUnits(), null, LogUtils.findCallingClassAndMethod(new Throwable()), truncateQueryToLog(sql)));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1535 */         boolean checkEOF = !mysql.isEOFDeprecated();
/*      */         
/* 1537 */         if ((this.parameterCount > 0) && (this.connection.versionMeetsMinimum(4, 1, 2)) && (!mysql.isVersion(5, 0, 0))) {
/* 1538 */           this.parameterFields = new Field[this.parameterCount];
/*      */           
/*      */ 
/* 1541 */           for (int i = 0; i < this.parameterCount; i++) {
/* 1542 */             Buffer metaDataPacket = mysql.readPacket();
/* 1543 */             this.parameterFields[i] = mysql.unpackField(metaDataPacket, false);
/*      */           }
/* 1545 */           if (checkEOF) {
/* 1546 */             mysql.readPacket();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1551 */         if (this.fieldCount > 0) {
/* 1552 */           this.resultFields = new Field[this.fieldCount];
/*      */           
/*      */ 
/* 1555 */           for (int i = 0; i < this.fieldCount; i++) {
/* 1556 */             Buffer fieldPacket = mysql.readPacket();
/* 1557 */             this.resultFields[i] = mysql.unpackField(fieldPacket, false);
/*      */           }
/* 1559 */           if (checkEOF) {
/* 1560 */             mysql.readPacket();
/*      */           }
/*      */         }
/*      */       } catch (SQLException sqlEx) {
/* 1564 */         if (this.connection.getDumpQueriesOnException()) {
/* 1565 */           StringBuilder messageBuf = new StringBuilder(this.originalSql.length() + 32);
/* 1566 */           messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/* 1567 */           messageBuf.append(this.originalSql);
/*      */           
/* 1569 */           sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
/*      */         }
/*      */         
/* 1572 */         throw sqlEx;
/*      */       }
/*      */       finally {
/* 1575 */         this.connection.getIO().clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private String truncateQueryToLog(String sql) throws SQLException {
/* 1581 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1582 */       String query = null;
/*      */       
/* 1584 */       if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
/* 1585 */         StringBuilder queryBuf = new StringBuilder(this.connection.getMaxQuerySizeToLog() + 12);
/* 1586 */         queryBuf.append(sql.substring(0, this.connection.getMaxQuerySizeToLog()));
/* 1587 */         queryBuf.append(Messages.getString("MysqlIO.25"));
/*      */         
/* 1589 */         query = queryBuf.toString();
/*      */       } else {
/* 1591 */         query = sql;
/*      */       }
/*      */       
/* 1594 */       return query;
/*      */     }
/*      */   }
/*      */   
/*      */   private void serverResetStatement() throws SQLException {
/* 1599 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1601 */       MysqlIO mysql = this.connection.getIO();
/*      */       
/* 1603 */       Buffer packet = mysql.getSharedSendPacket();
/*      */       
/* 1605 */       packet.clear();
/* 1606 */       packet.writeByte((byte)26);
/* 1607 */       packet.writeLong(this.serverStatementId);
/*      */       try
/*      */       {
/* 1610 */         mysql.sendCommand(26, null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), null, 0);
/*      */       } catch (SQLException sqlEx) {
/* 1612 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1614 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1000", getExceptionInterceptor());
/* 1615 */         sqlEx.initCause(ex);
/*      */         
/* 1617 */         throw sqlEx;
/*      */       } finally {
/* 1619 */         mysql.clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 1629 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1637 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1638 */       if (x == null) {
/* 1639 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1641 */         BindValue binding = getBinding(parameterIndex, true);
/* 1642 */         resetToType(binding, 252);
/*      */         
/* 1644 */         binding.value = x;
/* 1645 */         binding.isLongData = true;
/*      */         
/* 1647 */         if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1648 */           binding.bindLength = length;
/*      */         } else {
/* 1650 */           binding.bindLength = -1L;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1661 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1663 */       if (x == null) {
/* 1664 */         setNull(parameterIndex, 3);
/*      */       }
/*      */       else {
/* 1667 */         BindValue binding = getBinding(parameterIndex, false);
/*      */         
/* 1669 */         if (this.connection.versionMeetsMinimum(5, 0, 3)) {
/* 1670 */           resetToType(binding, 246);
/*      */         } else {
/* 1672 */           resetToType(binding, this.stringTypeCode);
/*      */         }
/*      */         
/* 1675 */         binding.value = StringUtils.fixDecimalExponent(StringUtils.consistentToString(x));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1685 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1687 */       if (x == null) {
/* 1688 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1690 */         BindValue binding = getBinding(parameterIndex, true);
/* 1691 */         resetToType(binding, 252);
/*      */         
/* 1693 */         binding.value = x;
/* 1694 */         binding.isLongData = true;
/*      */         
/* 1696 */         if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1697 */           binding.bindLength = length;
/*      */         } else {
/* 1699 */           binding.bindLength = -1L;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBlob(int parameterIndex, Blob x)
/*      */     throws SQLException
/*      */   {
/* 1710 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1712 */       if (x == null) {
/* 1713 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1715 */         BindValue binding = getBinding(parameterIndex, true);
/* 1716 */         resetToType(binding, 252);
/*      */         
/* 1718 */         binding.value = x;
/* 1719 */         binding.isLongData = true;
/*      */         
/* 1721 */         if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1722 */           binding.bindLength = x.length();
/*      */         } else {
/* 1724 */           binding.bindLength = -1L;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1735 */     setByte(parameterIndex, (byte)(x ? 1 : 0));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1743 */     checkClosed();
/*      */     
/* 1745 */     BindValue binding = getBinding(parameterIndex, false);
/* 1746 */     resetToType(binding, 1);
/*      */     
/* 1748 */     binding.longBinding = x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1756 */     checkClosed();
/*      */     
/* 1758 */     if (x == null) {
/* 1759 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1761 */       BindValue binding = getBinding(parameterIndex, false);
/* 1762 */       resetToType(binding, 253);
/*      */       
/* 1764 */       binding.value = x;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1773 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1775 */       if (reader == null) {
/* 1776 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1778 */         BindValue binding = getBinding(parameterIndex, true);
/* 1779 */         resetToType(binding, 252);
/*      */         
/* 1781 */         binding.value = reader;
/* 1782 */         binding.isLongData = true;
/*      */         
/* 1784 */         if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1785 */           binding.bindLength = length;
/*      */         } else {
/* 1787 */           binding.bindLength = -1L;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setClob(int parameterIndex, Clob x)
/*      */     throws SQLException
/*      */   {
/* 1798 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1800 */       if (x == null) {
/* 1801 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 1803 */         BindValue binding = getBinding(parameterIndex, true);
/* 1804 */         resetToType(binding, 252);
/*      */         
/* 1806 */         binding.value = x.getCharacterStream();
/* 1807 */         binding.isLongData = true;
/*      */         
/* 1809 */         if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 1810 */           binding.bindLength = x.length();
/*      */         } else {
/* 1812 */           binding.bindLength = -1L;
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
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 1832 */     setDate(parameterIndex, x, null);
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
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1851 */     if (x == null) {
/* 1852 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 1854 */       BindValue binding = getBinding(parameterIndex, false);
/* 1855 */       resetToType(binding, 10);
/*      */       
/* 1857 */       binding.value = x;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 1866 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1868 */       if ((!this.connection.getAllowNanAndInf()) && ((x == Double.POSITIVE_INFINITY) || (x == Double.NEGATIVE_INFINITY) || (Double.isNaN(x)))) {
/* 1869 */         throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1874 */       BindValue binding = getBinding(parameterIndex, false);
/* 1875 */       resetToType(binding, 5);
/*      */       
/* 1877 */       binding.doubleBinding = x;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 1886 */     checkClosed();
/*      */     
/* 1888 */     BindValue binding = getBinding(parameterIndex, false);
/* 1889 */     resetToType(binding, 4);
/*      */     
/* 1891 */     binding.floatBinding = x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 1899 */     checkClosed();
/*      */     
/* 1901 */     BindValue binding = getBinding(parameterIndex, false);
/* 1902 */     resetToType(binding, 3);
/*      */     
/* 1904 */     binding.longBinding = x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 1912 */     checkClosed();
/*      */     
/* 1914 */     BindValue binding = getBinding(parameterIndex, false);
/* 1915 */     resetToType(binding, 8);
/*      */     
/* 1917 */     binding.longBinding = x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1925 */     checkClosed();
/*      */     
/* 1927 */     BindValue binding = getBinding(parameterIndex, false);
/* 1928 */     resetToType(binding, 6);
/*      */     
/* 1930 */     binding.isNull = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1938 */     checkClosed();
/*      */     
/* 1940 */     BindValue binding = getBinding(parameterIndex, false);
/* 1941 */     resetToType(binding, 6);
/*      */     
/* 1943 */     binding.isNull = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 1951 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 1959 */     checkClosed();
/*      */     
/* 1961 */     BindValue binding = getBinding(parameterIndex, false);
/* 1962 */     resetToType(binding, 2);
/*      */     
/* 1964 */     binding.longBinding = x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 1972 */     checkClosed();
/*      */     
/* 1974 */     if (x == null) {
/* 1975 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 1977 */       BindValue binding = getBinding(parameterIndex, false);
/* 1978 */       resetToType(binding, this.stringTypeCode);
/*      */       
/* 1980 */       binding.value = x;
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
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 1997 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1998 */       setTimeInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
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
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2019 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2020 */       setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
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
/*      */   private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 2040 */     if (x == null) {
/* 2041 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 2043 */       BindValue binding = getBinding(parameterIndex, false);
/* 2044 */       resetToType(binding, 11);
/*      */       
/* 2046 */       if (!this.useLegacyDatetimeCode) {
/* 2047 */         binding.value = x;
/*      */       } else {
/* 2049 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */         
/* 2051 */         binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
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
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2071 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2072 */       setTimestampInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
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
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2092 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2093 */       setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */     }
/*      */   }
/*      */   
/*      */   private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
/* 2098 */     if (x == null) {
/* 2099 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 2101 */       BindValue binding = getBinding(parameterIndex, false);
/* 2102 */       resetToType(binding, 12);
/*      */       
/* 2104 */       if (!this.sendFractionalSeconds) {
/* 2105 */         x = TimeUtil.truncateFractionalSeconds(x);
/*      */       }
/*      */       
/* 2108 */       if (!this.useLegacyDatetimeCode) {
/* 2109 */         binding.value = x;
/*      */       } else {
/* 2111 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */         
/*      */ 
/* 2114 */         binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void resetToType(BindValue oldValue, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2124 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 2126 */       oldValue.reset();
/*      */       
/* 2128 */       if ((bufferType != 6) || (oldValue.bufferType == 0))
/*      */       {
/* 2130 */         if (oldValue.bufferType != bufferType) {
/* 2131 */           this.sendTypesToServer = true;
/* 2132 */           oldValue.bufferType = bufferType;
/*      */         }
/*      */       }
/*      */       
/* 2136 */       oldValue.isSet = true;
/* 2137 */       oldValue.boundBeforeExecutionNum = this.numberOfExecutions;
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
/*      */   @Deprecated
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2155 */     checkClosed();
/*      */     
/* 2157 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setURL(int parameterIndex, URL x)
/*      */     throws SQLException
/*      */   {
/* 2165 */     checkClosed();
/*      */     
/* 2167 */     setString(parameterIndex, x.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql)
/*      */     throws SQLException
/*      */   {
/* 2180 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 2182 */         Object value = bindValue.value;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2187 */         switch (bindValue.bufferType)
/*      */         {
/*      */         case 1: 
/* 2190 */           packet.writeByte((byte)(int)bindValue.longBinding);
/* 2191 */           return;
/*      */         case 2: 
/* 2193 */           packet.ensureCapacity(2);
/* 2194 */           packet.writeInt((int)bindValue.longBinding);
/* 2195 */           return;
/*      */         case 3: 
/* 2197 */           packet.ensureCapacity(4);
/* 2198 */           packet.writeLong((int)bindValue.longBinding);
/* 2199 */           return;
/*      */         case 8: 
/* 2201 */           packet.ensureCapacity(8);
/* 2202 */           packet.writeLongLong(bindValue.longBinding);
/* 2203 */           return;
/*      */         case 4: 
/* 2205 */           packet.ensureCapacity(4);
/* 2206 */           packet.writeFloat(bindValue.floatBinding);
/* 2207 */           return;
/*      */         case 5: 
/* 2209 */           packet.ensureCapacity(8);
/* 2210 */           packet.writeDouble(bindValue.doubleBinding);
/* 2211 */           return;
/*      */         case 11: 
/* 2213 */           storeTime(packet, (Time)value);
/* 2214 */           return;
/*      */         case 7: 
/*      */         case 10: 
/*      */         case 12: 
/* 2218 */           storeDateTime(packet, (java.util.Date)value, mysql, bindValue.bufferType);
/* 2219 */           return;
/*      */         case 0: 
/*      */         case 15: 
/*      */         case 246: 
/*      */         case 253: 
/*      */         case 254: 
/* 2225 */           if ((value instanceof byte[])) {
/* 2226 */             packet.writeLenBytes((byte[])value);
/* 2227 */           } else if (!this.isLoadDataQuery) {
/* 2228 */             packet.writeLenString((String)value, this.charEncoding, this.connection.getServerCharset(), this.charConverter, this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/*      */           else {
/* 2231 */             packet.writeLenBytes(StringUtils.getBytes((String)value));
/*      */           }
/*      */           
/* 2234 */           return;
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException uEE) {
/* 2238 */         throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void storeDateTime412AndOlder(Buffer intoBuf, java.util.Date dt, int bufferType) throws SQLException
/*      */   {
/* 2245 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2246 */       Calendar sessionCalendar = null;
/*      */       
/* 2248 */       if (!this.useLegacyDatetimeCode) {
/* 2249 */         if (bufferType == 10) {
/* 2250 */           sessionCalendar = getDefaultTzCalendar();
/*      */         } else {
/* 2252 */           sessionCalendar = getServerTzCalendar();
/*      */         }
/*      */       } else {
/* 2255 */         sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */       
/*      */ 
/* 2259 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2262 */         intoBuf.ensureCapacity(8);
/* 2263 */         intoBuf.writeByte((byte)7);
/*      */         
/* 2265 */         sessionCalendar.setTime(dt);
/*      */         
/* 2267 */         int year = sessionCalendar.get(1);
/* 2268 */         int month = sessionCalendar.get(2) + 1;
/* 2269 */         int date = sessionCalendar.get(5);
/*      */         
/* 2271 */         intoBuf.writeInt(year);
/* 2272 */         intoBuf.writeByte((byte)month);
/* 2273 */         intoBuf.writeByte((byte)date);
/*      */         
/* 2275 */         if ((dt instanceof java.sql.Date)) {
/* 2276 */           intoBuf.writeByte((byte)0);
/* 2277 */           intoBuf.writeByte((byte)0);
/* 2278 */           intoBuf.writeByte((byte)0);
/*      */         } else {
/* 2280 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/* 2281 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/* 2282 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */       } finally {
/* 2285 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql, int bufferType)
/*      */     throws SQLException
/*      */   {
/* 2298 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2299 */       if (this.connection.versionMeetsMinimum(4, 1, 3)) {
/* 2300 */         storeDateTime413AndNewer(intoBuf, dt, bufferType);
/*      */       } else {
/* 2302 */         storeDateTime412AndOlder(intoBuf, dt, bufferType);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt, int bufferType) throws SQLException {
/* 2308 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2309 */       Calendar sessionCalendar = null;
/*      */       
/* 2311 */       if (!this.useLegacyDatetimeCode) {
/* 2312 */         if (bufferType == 10) {
/* 2313 */           sessionCalendar = getDefaultTzCalendar();
/*      */         } else {
/* 2315 */           sessionCalendar = getServerTzCalendar();
/*      */         }
/*      */       } else {
/* 2318 */         sessionCalendar = ((dt instanceof Timestamp)) && (this.connection.getUseJDBCCompliantTimezoneShift()) ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       }
/*      */       
/*      */ 
/* 2322 */       java.util.Date oldTime = sessionCalendar.getTime();
/*      */       try
/*      */       {
/* 2325 */         sessionCalendar.setTime(dt);
/*      */         
/* 2327 */         if ((dt instanceof java.sql.Date)) {
/* 2328 */           sessionCalendar.set(11, 0);
/* 2329 */           sessionCalendar.set(12, 0);
/* 2330 */           sessionCalendar.set(13, 0);
/*      */         }
/*      */         
/* 2333 */         byte length = 7;
/*      */         
/* 2335 */         if ((dt instanceof Timestamp)) {
/* 2336 */           length = 11;
/*      */         }
/*      */         
/* 2339 */         intoBuf.ensureCapacity(length);
/*      */         
/* 2341 */         intoBuf.writeByte(length);
/*      */         
/* 2343 */         int year = sessionCalendar.get(1);
/* 2344 */         int month = sessionCalendar.get(2) + 1;
/* 2345 */         int date = sessionCalendar.get(5);
/*      */         
/* 2347 */         intoBuf.writeInt(year);
/* 2348 */         intoBuf.writeByte((byte)month);
/* 2349 */         intoBuf.writeByte((byte)date);
/*      */         
/* 2351 */         if ((dt instanceof java.sql.Date)) {
/* 2352 */           intoBuf.writeByte((byte)0);
/* 2353 */           intoBuf.writeByte((byte)0);
/* 2354 */           intoBuf.writeByte((byte)0);
/*      */         } else {
/* 2356 */           intoBuf.writeByte((byte)sessionCalendar.get(11));
/* 2357 */           intoBuf.writeByte((byte)sessionCalendar.get(12));
/* 2358 */           intoBuf.writeByte((byte)sessionCalendar.get(13));
/*      */         }
/*      */         
/* 2361 */         if (length == 11)
/*      */         {
/* 2363 */           intoBuf.writeLong(((Timestamp)dt).getNanos() / 1000);
/*      */         }
/*      */       }
/*      */       finally {
/* 2367 */         sessionCalendar.setTime(oldTime);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private Calendar getServerTzCalendar() throws SQLException {
/* 2373 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2374 */       if (this.serverTzCalendar == null) {
/* 2375 */         this.serverTzCalendar = new GregorianCalendar(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */       
/* 2378 */       return this.serverTzCalendar;
/*      */     }
/*      */   }
/*      */   
/*      */   private Calendar getDefaultTzCalendar() throws SQLException {
/* 2383 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2384 */       if (this.defaultTzCalendar == null) {
/* 2385 */         this.defaultTzCalendar = new GregorianCalendar(TimeZone.getDefault());
/*      */       }
/*      */       
/* 2388 */       return this.defaultTzCalendar;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream)
/*      */     throws SQLException
/*      */   {
/* 2396 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2397 */       String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */       
/* 2399 */       String clobEncoding = forcedEncoding == null ? this.connection.getEncoding() : forcedEncoding;
/*      */       
/* 2401 */       int maxBytesChar = 2;
/*      */       
/* 2403 */       if (clobEncoding != null) {
/* 2404 */         if (!clobEncoding.equals("UTF-16")) {
/* 2405 */           maxBytesChar = this.connection.getMaxBytesPerChar(clobEncoding);
/*      */           
/* 2407 */           if (maxBytesChar == 1) {
/* 2408 */             maxBytesChar = 2;
/*      */           }
/*      */         } else {
/* 2411 */           maxBytesChar = 4;
/*      */         }
/*      */       }
/*      */       
/* 2415 */       char[] buf = new char[8192 / maxBytesChar];
/*      */       
/* 2417 */       int numRead = 0;
/*      */       
/* 2419 */       int bytesInPacket = 0;
/* 2420 */       int totalBytesRead = 0;
/* 2421 */       int bytesReadAtLastSend = 0;
/* 2422 */       int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */       try
/*      */       {
/* 2425 */         packet.clear();
/* 2426 */         packet.writeByte((byte)24);
/* 2427 */         packet.writeLong(this.serverStatementId);
/* 2428 */         packet.writeInt(parameterIndex);
/*      */         
/* 2430 */         boolean readAny = false;
/*      */         
/* 2432 */         while ((numRead = inStream.read(buf)) != -1) {
/* 2433 */           readAny = true;
/*      */           
/* 2435 */           byte[] valueAsBytes = StringUtils.getBytes(buf, null, clobEncoding, this.connection.getServerCharset(), 0, numRead, this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           
/*      */ 
/* 2438 */           packet.writeBytesNoNull(valueAsBytes, 0, valueAsBytes.length);
/*      */           
/* 2440 */           bytesInPacket += valueAsBytes.length;
/* 2441 */           totalBytesRead += valueAsBytes.length;
/*      */           
/* 2443 */           if (bytesInPacket >= packetIsFullAt) {
/* 2444 */             bytesReadAtLastSend = totalBytesRead;
/*      */             
/* 2446 */             mysql.sendCommand(24, null, packet, true, null, 0);
/*      */             
/* 2448 */             bytesInPacket = 0;
/* 2449 */             packet.clear();
/* 2450 */             packet.writeByte((byte)24);
/* 2451 */             packet.writeLong(this.serverStatementId);
/* 2452 */             packet.writeInt(parameterIndex);
/*      */           }
/*      */         }
/*      */         
/* 2456 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 2457 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */         
/* 2460 */         if (!readAny) {
/* 2461 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 2464 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */         
/* 2466 */         sqlEx.initCause(ioEx);
/*      */         
/* 2468 */         throw sqlEx;
/*      */       } finally {
/* 2470 */         if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2471 */           (inStream != null)) {
/*      */           try {
/* 2473 */             inStream.close();
/*      */           }
/*      */           catch (IOException ioEx) {}
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream)
/*      */     throws SQLException
/*      */   {
/* 2484 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2485 */       byte[] buf = new byte[' '];
/*      */       
/* 2487 */       int numRead = 0;
/*      */       try
/*      */       {
/* 2490 */         int bytesInPacket = 0;
/* 2491 */         int totalBytesRead = 0;
/* 2492 */         int bytesReadAtLastSend = 0;
/* 2493 */         int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */         
/* 2495 */         packet.clear();
/* 2496 */         packet.writeByte((byte)24);
/* 2497 */         packet.writeLong(this.serverStatementId);
/* 2498 */         packet.writeInt(parameterIndex);
/*      */         
/* 2500 */         boolean readAny = false;
/*      */         
/* 2502 */         while ((numRead = inStream.read(buf)) != -1)
/*      */         {
/* 2504 */           readAny = true;
/*      */           
/* 2506 */           packet.writeBytesNoNull(buf, 0, numRead);
/* 2507 */           bytesInPacket += numRead;
/* 2508 */           totalBytesRead += numRead;
/*      */           
/* 2510 */           if (bytesInPacket >= packetIsFullAt) {
/* 2511 */             bytesReadAtLastSend = totalBytesRead;
/*      */             
/* 2513 */             mysql.sendCommand(24, null, packet, true, null, 0);
/*      */             
/* 2515 */             bytesInPacket = 0;
/* 2516 */             packet.clear();
/* 2517 */             packet.writeByte((byte)24);
/* 2518 */             packet.writeLong(this.serverStatementId);
/* 2519 */             packet.writeInt(parameterIndex);
/*      */           }
/*      */         }
/*      */         
/* 2523 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 2524 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */         
/* 2527 */         if (!readAny) {
/* 2528 */           mysql.sendCommand(24, null, packet, true, null, 0);
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 2531 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */         
/* 2533 */         sqlEx.initCause(ioEx);
/*      */         
/* 2535 */         throw sqlEx;
/*      */       } finally {
/* 2537 */         if ((this.connection.getAutoClosePStmtStreams()) && 
/* 2538 */           (inStream != null)) {
/*      */           try {
/* 2540 */             inStream.close();
/*      */           }
/*      */           catch (IOException ioEx) {}
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
/*      */   public String toString()
/*      */   {
/* 2555 */     StringBuilder toStringBuf = new StringBuilder();
/*      */     
/* 2557 */     toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
/* 2558 */     toStringBuf.append(this.serverStatementId);
/* 2559 */     toStringBuf.append("] - ");
/*      */     try
/*      */     {
/* 2562 */       toStringBuf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 2564 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 2565 */       toStringBuf.append(sqlEx);
/*      */     }
/*      */     
/* 2568 */     return toStringBuf.toString();
/*      */   }
/*      */   
/*      */   protected long getServerStatementId() {
/* 2572 */     return this.serverStatementId;
/*      */   }
/*      */   
/* 2575 */   private boolean hasCheckedRewrite = false;
/* 2576 */   private boolean canRewrite = false;
/*      */   
/*      */   public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException
/*      */   {
/* 2580 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2581 */       if (!this.hasCheckedRewrite) {
/* 2582 */         this.hasCheckedRewrite = true;
/* 2583 */         this.canRewrite = canRewrite(this.originalSql, isOnDuplicateKeyUpdate(), getLocationOfOnDuplicateKeyUpdate(), 0);
/*      */         
/* 2585 */         this.parseInfo = new ParseInfo(this.originalSql, this.connection, this.connection.getMetaData(), this.charEncoding, this.charConverter);
/*      */       }
/*      */       
/* 2588 */       return this.canRewrite;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean canRewriteAsMultivalueInsertStatement() throws SQLException {
/* 2593 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2594 */       if (!canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 2595 */         return false;
/*      */       }
/*      */       
/* 2598 */       BindValue[] currentBindValues = null;
/* 2599 */       BindValue[] previousBindValues = null;
/*      */       
/* 2601 */       int nbrCommands = this.batchedArgs.size();
/*      */       
/*      */ 
/*      */ 
/* 2605 */       for (int commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/* 2606 */         Object arg = this.batchedArgs.get(commandIndex);
/*      */         
/* 2608 */         if (!(arg instanceof String))
/*      */         {
/* 2610 */           currentBindValues = ((BatchedBindValues)arg).batchedParameterValues;
/*      */           
/*      */ 
/*      */ 
/* 2614 */           if (previousBindValues != null) {
/* 2615 */             for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2616 */               if (currentBindValues[j].bufferType != previousBindValues[j].bufferType) {
/* 2617 */                 return false;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2624 */       return true;
/*      */     }
/*      */   }
/*      */   
/* 2628 */   private int locationOfOnDuplicateKeyUpdate = -2;
/*      */   
/*      */   protected int getLocationOfOnDuplicateKeyUpdate() throws SQLException
/*      */   {
/* 2632 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2633 */       if (this.locationOfOnDuplicateKeyUpdate == -2) {
/* 2634 */         this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(this.originalSql, this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), this.connection.getRewriteBatchedStatements(), this.connection.isNoBackslashEscapesSet());
/*      */       }
/*      */       
/*      */ 
/* 2638 */       return this.locationOfOnDuplicateKeyUpdate;
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean isOnDuplicateKeyUpdate() throws SQLException {
/* 2643 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2644 */       return getLocationOfOnDuplicateKeyUpdate() != -1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 2656 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2657 */       long sizeOfEntireBatch = 10L;
/* 2658 */       long maxSizeOfParameterSet = 0L;
/*      */       
/* 2660 */       for (int i = 0; i < numBatchedArgs; i++) {
/* 2661 */         BindValue[] paramArg = ((BatchedBindValues)this.batchedArgs.get(i)).batchedParameterValues;
/*      */         
/* 2663 */         long sizeOfParameterSet = 0L;
/*      */         
/* 2665 */         sizeOfParameterSet += (this.parameterCount + 7) / 8;
/*      */         
/* 2667 */         sizeOfParameterSet += this.parameterCount * 2;
/*      */         
/* 2669 */         for (int j = 0; j < this.parameterBindings.length; j++) {
/* 2670 */           if (!paramArg[j].isNull)
/*      */           {
/* 2672 */             long size = paramArg[j].getBoundLength();
/*      */             
/* 2674 */             if (paramArg[j].isLongData) {
/* 2675 */               if (size != -1L) {
/* 2676 */                 sizeOfParameterSet += size;
/*      */               }
/*      */             } else {
/* 2679 */               sizeOfParameterSet += size;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2684 */         sizeOfEntireBatch += sizeOfParameterSet;
/*      */         
/* 2686 */         if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 2687 */           maxSizeOfParameterSet = sizeOfParameterSet;
/*      */         }
/*      */       }
/*      */       
/* 2691 */       return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */     }
/*      */   }
/*      */   
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException
/*      */   {
/* 2697 */     BindValue[] paramArg = ((BatchedBindValues)paramSet).batchedParameterValues;
/*      */     
/* 2699 */     for (int j = 0; j < paramArg.length; j++) {
/* 2700 */       if (paramArg[j].isNull) {
/* 2701 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 2703 */       else if (paramArg[j].isLongData) {
/* 2704 */         Object value = paramArg[j].value;
/*      */         
/* 2706 */         if ((value instanceof InputStream)) {
/* 2707 */           batchedStatement.setBinaryStream(batchedParamIndex++, (InputStream)value, (int)paramArg[j].bindLength);
/*      */         } else {
/* 2709 */           batchedStatement.setCharacterStream(batchedParamIndex++, (Reader)value, (int)paramArg[j].bindLength);
/*      */         }
/*      */       }
/*      */       else {
/* 2713 */         switch (paramArg[j].bufferType)
/*      */         {
/*      */         case 1: 
/* 2716 */           batchedStatement.setByte(batchedParamIndex++, (byte)(int)paramArg[j].longBinding);
/* 2717 */           break;
/*      */         case 2: 
/* 2719 */           batchedStatement.setShort(batchedParamIndex++, (short)(int)paramArg[j].longBinding);
/* 2720 */           break;
/*      */         case 3: 
/* 2722 */           batchedStatement.setInt(batchedParamIndex++, (int)paramArg[j].longBinding);
/* 2723 */           break;
/*      */         case 8: 
/* 2725 */           batchedStatement.setLong(batchedParamIndex++, paramArg[j].longBinding);
/* 2726 */           break;
/*      */         case 4: 
/* 2728 */           batchedStatement.setFloat(batchedParamIndex++, paramArg[j].floatBinding);
/* 2729 */           break;
/*      */         case 5: 
/* 2731 */           batchedStatement.setDouble(batchedParamIndex++, paramArg[j].doubleBinding);
/* 2732 */           break;
/*      */         case 11: 
/* 2734 */           batchedStatement.setTime(batchedParamIndex++, (Time)paramArg[j].value);
/* 2735 */           break;
/*      */         case 10: 
/* 2737 */           batchedStatement.setDate(batchedParamIndex++, (java.sql.Date)paramArg[j].value);
/* 2738 */           break;
/*      */         case 7: 
/*      */         case 12: 
/* 2741 */           batchedStatement.setTimestamp(batchedParamIndex++, (Timestamp)paramArg[j].value);
/* 2742 */           break;
/*      */         case 0: 
/*      */         case 15: 
/*      */         case 246: 
/*      */         case 253: 
/*      */         case 254: 
/* 2748 */           Object value = paramArg[j].value;
/*      */           
/* 2750 */           if ((value instanceof byte[])) {
/* 2751 */             batchedStatement.setBytes(batchedParamIndex, (byte[])value);
/*      */           } else {
/* 2753 */             batchedStatement.setString(batchedParamIndex, (String)value);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2758 */           if ((batchedStatement instanceof ServerPreparedStatement)) {
/* 2759 */             BindValue asBound = ((ServerPreparedStatement)batchedStatement).getBinding(batchedParamIndex, false);
/* 2760 */             asBound.bufferType = paramArg[j].bufferType;
/*      */           }
/*      */           
/* 2763 */           batchedParamIndex++;
/*      */           
/* 2765 */           break;
/*      */         default: 
/* 2767 */           throw new IllegalArgumentException("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2774 */     return batchedParamIndex;
/*      */   }
/*      */   
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL()
/*      */   {
/* 2779 */     return this.hasOnDuplicateKeyUpdate;
/*      */   }
/*      */   
/*      */   protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException
/*      */   {
/* 2784 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 2786 */         PreparedStatement pstmt = new ServerPreparedStatement(localConn, this.parseInfo.getSqlForBatch(numBatches), this.currentCatalog, this.resultSetConcurrency, this.resultSetType);
/*      */         
/* 2788 */         pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/*      */         
/* 2790 */         return pstmt;
/*      */       } catch (UnsupportedEncodingException e) {
/* 2792 */         SQLException sqlEx = SQLError.createSQLException("Unable to prepare batch statement", "S1000", getExceptionInterceptor());
/*      */         
/* 2794 */         sqlEx.initCause(e);
/*      */         
/* 2796 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ServerPreparedStatement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */