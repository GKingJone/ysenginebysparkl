/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CallableStatement
/*      */   extends PreparedStatement
/*      */   implements java.sql.CallableStatement
/*      */ {
/*      */   protected static final Constructor<?> JDBC_4_CSTMT_2_ARGS_CTOR;
/*      */   protected static final Constructor<?> JDBC_4_CSTMT_4_ARGS_CTOR;
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = Integer.MIN_VALUE;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*      */   
/*      */   static
/*      */   {
/*   59 */     if (Util.isJdbc4()) {
/*      */       try {
/*   61 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42CallableStatement" : "com.mysql.jdbc.JDBC4CallableStatement";
/*   62 */         JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, CallableStatementParamInfo.class });
/*      */         
/*   64 */         JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, Boolean.TYPE });
/*      */       }
/*      */       catch (SecurityException e) {
/*   67 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   69 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   71 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   74 */       JDBC_4_CSTMT_4_ARGS_CTOR = null;
/*   75 */       JDBC_4_CSTMT_2_ARGS_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class CallableStatementParam
/*      */   {
/*      */     int desiredJdbcType;
/*      */     
/*      */     int index;
/*      */     
/*      */     int inOutModifier;
/*      */     
/*      */     boolean isIn;
/*      */     
/*      */     boolean isOut;
/*      */     
/*      */     int jdbcType;
/*      */     
/*      */     short nullability;
/*      */     
/*      */     String paramName;
/*      */     
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*      */     
/*      */     CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier)
/*      */     {
/*  104 */       this.paramName = name;
/*  105 */       this.isIn = in;
/*  106 */       this.isOut = out;
/*  107 */       this.index = idx;
/*      */       
/*  109 */       this.jdbcType = jdbcType;
/*  110 */       this.typeName = typeName;
/*  111 */       this.precision = precision;
/*  112 */       this.scale = scale;
/*  113 */       this.nullability = nullability;
/*  114 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  124 */       return super.clone();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     String catalogInUse;
/*      */     
/*      */     boolean isFunctionCall;
/*      */     
/*      */     String nativeSql;
/*      */     
/*      */     int numParameters;
/*      */     
/*      */     List<CallableStatementParam> parameterList;
/*      */     
/*      */     Map<String, CallableStatementParam> parameterMap;
/*      */     
/*  144 */     boolean isReadOnlySafeProcedure = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  149 */     boolean isReadOnlySafeChecked = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo)
/*      */     {
/*  160 */       this.nativeSql = CallableStatement.this.originalSql;
/*  161 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  162 */       this.isFunctionCall = fullParamInfo.isFunctionCall;
/*      */       
/*  164 */       int[] localParameterMap = CallableStatement.this.placeholderToParameterIndexMap;
/*  165 */       int parameterMapLength = localParameterMap.length;
/*      */       
/*  167 */       this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
/*  168 */       this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
/*  169 */       this.parameterList = new ArrayList(fullParamInfo.numParameters);
/*  170 */       this.parameterMap = new HashMap(fullParamInfo.numParameters);
/*      */       
/*  172 */       if (this.isFunctionCall)
/*      */       {
/*  174 */         this.parameterList.add(fullParamInfo.parameterList.get(0));
/*      */       }
/*      */       
/*  177 */       int offset = this.isFunctionCall ? 1 : 0;
/*      */       
/*  179 */       for (int i = 0; i < parameterMapLength; i++) {
/*  180 */         if (localParameterMap[i] != 0) {
/*  181 */           CallableStatementParam param = (CallableStatementParam)fullParamInfo.parameterList.get(localParameterMap[i] + offset);
/*      */           
/*  183 */           this.parameterList.add(param);
/*  184 */           this.parameterMap.put(param.paramName, param);
/*      */         }
/*      */       }
/*      */       
/*  188 */       this.numParameters = this.parameterList.size();
/*      */     }
/*      */     
/*      */     CallableStatementParamInfo(ResultSet paramTypesRs) throws SQLException
/*      */     {
/*  193 */       boolean hadRows = paramTypesRs.last();
/*      */       
/*  195 */       this.nativeSql = CallableStatement.this.originalSql;
/*  196 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  197 */       this.isFunctionCall = CallableStatement.this.callingStoredFunction;
/*      */       
/*  199 */       if (hadRows) {
/*  200 */         this.numParameters = paramTypesRs.getRow();
/*      */         
/*  202 */         this.parameterList = new ArrayList(this.numParameters);
/*  203 */         this.parameterMap = new HashMap(this.numParameters);
/*      */         
/*  205 */         paramTypesRs.beforeFirst();
/*      */         
/*  207 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  209 */         this.numParameters = 0;
/*      */       }
/*      */       
/*  212 */       if (this.isFunctionCall) {
/*  213 */         this.numParameters += 1;
/*      */       }
/*      */     }
/*      */     
/*      */     private void addParametersFromDBMD(ResultSet paramTypesRs) throws SQLException {
/*  218 */       int i = 0;
/*      */       
/*  220 */       while (paramTypesRs.next()) {
/*  221 */         String paramName = paramTypesRs.getString(4);
/*  222 */         int inOutModifier = paramTypesRs.getInt(5);
/*      */         
/*  224 */         boolean isOutParameter = false;
/*  225 */         boolean isInParameter = false;
/*      */         
/*  227 */         if ((i == 0) && (this.isFunctionCall)) {
/*  228 */           isOutParameter = true;
/*  229 */           isInParameter = false;
/*  230 */         } else if (inOutModifier == 2) {
/*  231 */           isOutParameter = true;
/*  232 */           isInParameter = true;
/*  233 */         } else if (inOutModifier == 1) {
/*  234 */           isOutParameter = false;
/*  235 */           isInParameter = true;
/*  236 */         } else if (inOutModifier == 4) {
/*  237 */           isOutParameter = true;
/*  238 */           isInParameter = false;
/*      */         }
/*      */         
/*  241 */         int jdbcType = paramTypesRs.getInt(6);
/*  242 */         String typeName = paramTypesRs.getString(7);
/*  243 */         int precision = paramTypesRs.getInt(8);
/*  244 */         int scale = paramTypesRs.getInt(10);
/*  245 */         short nullability = paramTypesRs.getShort(12);
/*      */         
/*  247 */         CallableStatementParam paramInfoToAdd = new CallableStatementParam(paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */         
/*      */ 
/*  250 */         this.parameterList.add(paramInfoToAdd);
/*  251 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       }
/*      */     }
/*      */     
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  256 */       int localParamIndex = paramIndex - 1;
/*      */       
/*  258 */       if ((paramIndex < 0) || (localParamIndex >= this.numParameters)) {
/*  259 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + this.numParameters + Messages.getString("CallableStatement.13"), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  271 */       return super.clone();
/*      */     }
/*      */     
/*      */     CallableStatementParam getParameter(int index) {
/*  275 */       return (CallableStatementParam)this.parameterList.get(index);
/*      */     }
/*      */     
/*      */     CallableStatementParam getParameter(String name) {
/*  279 */       return (CallableStatementParam)this.parameterMap.get(name);
/*      */     }
/*      */     
/*      */     public String getParameterClassName(int arg0) throws SQLException {
/*  283 */       String mysqlTypeName = getParameterTypeName(arg0);
/*      */       
/*  285 */       boolean isBinaryOrBlob = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BINARY") != -1);
/*      */       
/*  287 */       boolean isUnsigned = StringUtils.indexOfIgnoreCase(mysqlTypeName, "UNSIGNED") != -1;
/*      */       
/*  289 */       int mysqlTypeIfKnown = 0;
/*      */       
/*  291 */       if (StringUtils.startsWithIgnoreCase(mysqlTypeName, "MEDIUMINT")) {
/*  292 */         mysqlTypeIfKnown = 9;
/*      */       }
/*      */       
/*  295 */       return ResultSetMetaData.getClassNameForJavaType(getParameterType(arg0), isUnsigned, mysqlTypeIfKnown, isBinaryOrBlob, false, CallableStatement.this.connection.getYearIsDateType());
/*      */     }
/*      */     
/*      */     public int getParameterCount() throws SQLException
/*      */     {
/*  300 */       if (this.parameterList == null) {
/*  301 */         return 0;
/*      */       }
/*      */       
/*  304 */       return this.parameterList.size();
/*      */     }
/*      */     
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*  308 */       checkBounds(arg0);
/*      */       
/*  310 */       return getParameter(arg0 - 1).inOutModifier;
/*      */     }
/*      */     
/*      */     public int getParameterType(int arg0) throws SQLException {
/*  314 */       checkBounds(arg0);
/*      */       
/*  316 */       return getParameter(arg0 - 1).jdbcType;
/*      */     }
/*      */     
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*  320 */       checkBounds(arg0);
/*      */       
/*  322 */       return getParameter(arg0 - 1).typeName;
/*      */     }
/*      */     
/*      */     public int getPrecision(int arg0) throws SQLException {
/*  326 */       checkBounds(arg0);
/*      */       
/*  328 */       return getParameter(arg0 - 1).precision;
/*      */     }
/*      */     
/*      */     public int getScale(int arg0) throws SQLException {
/*  332 */       checkBounds(arg0);
/*      */       
/*  334 */       return getParameter(arg0 - 1).scale;
/*      */     }
/*      */     
/*      */     public int isNullable(int arg0) throws SQLException {
/*  338 */       checkBounds(arg0);
/*      */       
/*  340 */       return getParameter(arg0 - 1).nullability;
/*      */     }
/*      */     
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*  344 */       checkBounds(arg0);
/*      */       
/*  346 */       return false;
/*      */     }
/*      */     
/*      */     Iterator<CallableStatementParam> iterator() {
/*  350 */       return this.parameterList.iterator();
/*      */     }
/*      */     
/*      */     int numberOfParameters() {
/*  354 */       return this.numParameters;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isWrapperFor(Class<?> iface)
/*      */       throws SQLException
/*      */     {
/*  361 */       CallableStatement.this.checkClosed();
/*      */       
/*      */ 
/*  364 */       return iface.isInstance(this);
/*      */     }
/*      */     
/*      */ 
/*      */     public <T> T unwrap(Class<T> iface)
/*      */       throws SQLException
/*      */     {
/*      */       try
/*      */       {
/*  373 */         return (T)iface.cast(this);
/*      */       } catch (ClassCastException cce) {
/*  375 */         throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", CallableStatement.this.getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String mangleParameterName(String origParameterName)
/*      */   {
/*  386 */     if (origParameterName == null) {
/*  387 */       return null;
/*      */     }
/*      */     
/*  390 */     int offset = 0;
/*      */     
/*  392 */     if ((origParameterName.length() > 0) && (origParameterName.charAt(0) == '@')) {
/*  393 */       offset = 1;
/*      */     }
/*      */     
/*  396 */     StringBuilder paramNameBuf = new StringBuilder("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*  397 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  398 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */     
/*  400 */     return paramNameBuf.toString();
/*      */   }
/*      */   
/*  403 */   private boolean callingStoredFunction = false;
/*      */   
/*      */   private ResultSetInternalMethods functionReturnValueResults;
/*      */   
/*  407 */   private boolean hasOutputParams = false;
/*      */   
/*      */ 
/*      */   private ResultSetInternalMethods outputParameterResults;
/*      */   
/*      */ 
/*  413 */   protected boolean outputParamWasNull = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private int[] parameterIndexToRsIndex;
/*      */   
/*      */ 
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */   
/*      */ 
/*      */   private CallableStatementParam returnValueParam;
/*      */   
/*      */ 
/*      */   private int[] placeholderToParameterIndexMap;
/*      */   
/*      */ 
/*      */ 
/*      */   public CallableStatement(MySQLConnection conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  433 */     super(conn, paramInfo.nativeSql, paramInfo.catalogInUse);
/*      */     
/*  435 */     this.paramInfo = paramInfo;
/*  436 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */     
/*  438 */     if (this.callingStoredFunction) {
/*  439 */       this.parameterCount += 1;
/*      */     }
/*      */     
/*  442 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static CallableStatement getInstance(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  453 */     if (!Util.isJdbc4()) {
/*  454 */       return new CallableStatement(conn, sql, catalog, isFunctionCall);
/*      */     }
/*      */     
/*  457 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, new Object[] { conn, sql, catalog, Boolean.valueOf(isFunctionCall) }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static CallableStatement getInstance(MySQLConnection conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  469 */     if (!Util.isJdbc4()) {
/*  470 */       return new CallableStatement(conn, paramInfo);
/*      */     }
/*      */     
/*  473 */     return (CallableStatement)Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, new Object[] { conn, paramInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */   private void generateParameterMap()
/*      */     throws SQLException
/*      */   {
/*  480 */     synchronized (checkClosed().getConnectionMutex()) {
/*  481 */       if (this.paramInfo == null) {
/*  482 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  487 */       int parameterCountFromMetaData = this.paramInfo.getParameterCount();
/*      */       
/*      */ 
/*      */ 
/*  491 */       if (this.callingStoredFunction) {
/*  492 */         parameterCountFromMetaData--;
/*      */       }
/*      */       
/*  495 */       if ((this.paramInfo != null) && (this.parameterCount != parameterCountFromMetaData)) {
/*  496 */         this.placeholderToParameterIndexMap = new int[this.parameterCount];
/*      */         
/*  498 */         int startPos = this.callingStoredFunction ? StringUtils.indexOfIgnoreCase(this.originalSql, "SELECT") : StringUtils.indexOfIgnoreCase(this.originalSql, "CALL");
/*      */         
/*      */ 
/*  501 */         if (startPos != -1) {
/*  502 */           int parenOpenPos = this.originalSql.indexOf('(', startPos + 4);
/*      */           
/*  504 */           if (parenOpenPos != -1) {
/*  505 */             int parenClosePos = StringUtils.indexOfIgnoreCase(parenOpenPos, this.originalSql, ")", "'", "'", StringUtils.SEARCH_MODE__ALL);
/*      */             
/*  507 */             if (parenClosePos != -1) {
/*  508 */               List<?> parsedParameters = StringUtils.split(this.originalSql.substring(parenOpenPos + 1, parenClosePos), ",", "'\"", "'\"", true);
/*      */               
/*  510 */               int numParsedParameters = parsedParameters.size();
/*      */               
/*      */ 
/*      */ 
/*  514 */               if (numParsedParameters != this.parameterCount) {}
/*      */               
/*      */ 
/*      */ 
/*  518 */               int placeholderCount = 0;
/*      */               
/*  520 */               for (int i = 0; i < numParsedParameters; i++) {
/*  521 */                 if (((String)parsedParameters.get(i)).equals("?")) {
/*  522 */                   this.placeholderToParameterIndexMap[(placeholderCount++)] = i;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
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
/*      */   public CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  546 */     super(conn, sql, catalog);
/*      */     
/*  548 */     this.callingStoredFunction = isFunctionCall;
/*      */     
/*  550 */     if (!this.callingStoredFunction) {
/*  551 */       if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL"))
/*      */       {
/*  553 */         fakeParameterTypes(false);
/*      */       } else {
/*  555 */         determineParameterTypes();
/*      */       }
/*      */       
/*  558 */       generateParameterMap();
/*      */     } else {
/*  560 */       determineParameterTypes();
/*  561 */       generateParameterMap();
/*      */       
/*  563 */       this.parameterCount += 1;
/*      */     }
/*      */     
/*  566 */     this.retrieveGeneratedKeys = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  576 */     setOutParams();
/*      */     
/*  578 */     super.addBatch();
/*      */   }
/*      */   
/*      */   private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException
/*      */   {
/*  583 */     synchronized (checkClosed().getConnectionMutex()) {
/*  584 */       if (this.callingStoredFunction) {
/*  585 */         if (paramIndex == 1)
/*      */         {
/*  587 */           if (this.returnValueParam == null) {
/*  588 */             this.returnValueParam = new CallableStatementParam("", 0, false, true, 12, "VARCHAR", 0, 0, (short)2, 5);
/*      */           }
/*      */           
/*      */ 
/*  592 */           return this.returnValueParam;
/*      */         }
/*      */         
/*      */ 
/*  596 */         paramIndex--;
/*      */       }
/*      */       
/*  599 */       checkParameterIndexBounds(paramIndex);
/*      */       
/*  601 */       int localParamIndex = paramIndex - 1;
/*      */       
/*  603 */       if (this.placeholderToParameterIndexMap != null) {
/*  604 */         localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */       }
/*      */       
/*  607 */       CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */       
/*      */ 
/*      */ 
/*  611 */       if (this.connection.getNoAccessToProcedureBodies()) {
/*  612 */         paramDescriptor.isOut = true;
/*  613 */         paramDescriptor.isIn = true;
/*  614 */         paramDescriptor.inOutModifier = 2;
/*  615 */       } else if (!paramDescriptor.isOut) {
/*  616 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  620 */       this.hasOutputParams = true;
/*      */       
/*  622 */       return paramDescriptor;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkParameterIndexBounds(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  632 */     synchronized (checkClosed().getConnectionMutex()) {
/*  633 */       this.paramInfo.checkBounds(paramIndex);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkStreamability()
/*      */     throws SQLException
/*      */   {
/*  645 */     if ((this.hasOutputParams) && (createStreamingResultSet())) {
/*  646 */       throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), "S1C00", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   public void clearParameters() throws SQLException
/*      */   {
/*  652 */     synchronized (checkClosed().getConnectionMutex()) {
/*  653 */       super.clearParameters();
/*      */       try
/*      */       {
/*  656 */         if (this.outputParameterResults != null) {
/*  657 */           this.outputParameterResults.close();
/*      */         }
/*      */       } finally {
/*  660 */         this.outputParameterResults = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void fakeParameterTypes(boolean isReallyProcedure)
/*      */     throws SQLException
/*      */   {
/*  673 */     synchronized (checkClosed().getConnectionMutex()) {
/*  674 */       Field[] fields = new Field[13];
/*      */       
/*  676 */       fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/*  677 */       fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/*  678 */       fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/*  679 */       fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/*  680 */       fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/*  681 */       fields[5] = new Field("", "DATA_TYPE", 5, 0);
/*  682 */       fields[6] = new Field("", "TYPE_NAME", 1, 0);
/*  683 */       fields[7] = new Field("", "PRECISION", 4, 0);
/*  684 */       fields[8] = new Field("", "LENGTH", 4, 0);
/*  685 */       fields[9] = new Field("", "SCALE", 5, 0);
/*  686 */       fields[10] = new Field("", "RADIX", 5, 0);
/*  687 */       fields[11] = new Field("", "NULLABLE", 5, 0);
/*  688 */       fields[12] = new Field("", "REMARKS", 1, 0);
/*      */       
/*  690 */       String procName = isReallyProcedure ? extractProcedureName() : null;
/*      */       
/*  692 */       byte[] procNameAsBytes = null;
/*      */       try
/*      */       {
/*  695 */         procNameAsBytes = procName == null ? null : StringUtils.getBytes(procName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/*  697 */         procNameAsBytes = StringUtils.s2b(procName, this.connection);
/*      */       }
/*      */       
/*  700 */       ArrayList<ResultSetRow> resultRows = new ArrayList();
/*      */       
/*  702 */       for (int i = 0; i < this.parameterCount; i++) {
/*  703 */         byte[][] row = new byte[13][];
/*  704 */         row[0] = null;
/*  705 */         row[1] = null;
/*  706 */         row[2] = procNameAsBytes;
/*  707 */         row[3] = StringUtils.s2b(String.valueOf(i), this.connection);
/*      */         
/*  709 */         row[4] = StringUtils.s2b(String.valueOf(1), this.connection);
/*      */         
/*  711 */         row[5] = StringUtils.s2b(String.valueOf(12), this.connection);
/*  712 */         row[6] = StringUtils.s2b("VARCHAR", this.connection);
/*  713 */         row[7] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  714 */         row[8] = StringUtils.s2b(Integer.toString(65535), this.connection);
/*  715 */         row[9] = StringUtils.s2b(Integer.toString(0), this.connection);
/*  716 */         row[10] = StringUtils.s2b(Integer.toString(10), this.connection);
/*      */         
/*  718 */         row[11] = StringUtils.s2b(Integer.toString(2), this.connection);
/*      */         
/*  720 */         row[12] = null;
/*      */         
/*  722 */         resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */       }
/*      */       
/*  725 */       ResultSet paramTypesRs = DatabaseMetaData.buildResultSet(fields, resultRows, this.connection);
/*      */       
/*  727 */       convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */     }
/*      */   }
/*      */   
/*      */   private void determineParameterTypes() throws SQLException {
/*  732 */     synchronized (checkClosed().getConnectionMutex()) {
/*  733 */       ResultSet paramTypesRs = null;
/*      */       
/*      */       try
/*      */       {
/*  737 */         String procName = extractProcedureName();
/*  738 */         String quotedId = "";
/*      */         try {
/*  740 */           quotedId = this.connection.supportsQuotedIdentifiers() ? this.connection.getMetaData().getIdentifierQuoteString() : "";
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*  744 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */         
/*  747 */         List<?> parseList = StringUtils.splitDBdotName(procName, "", quotedId, this.connection.isNoBackslashEscapesSet());
/*  748 */         String tmpCatalog = "";
/*      */         
/*  750 */         if (parseList.size() == 2) {
/*  751 */           tmpCatalog = (String)parseList.get(0);
/*  752 */           procName = (String)parseList.get(1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  757 */         java.sql.DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */         
/*  759 */         boolean useCatalog = false;
/*      */         
/*  761 */         if (tmpCatalog.length() <= 0) {
/*  762 */           useCatalog = true;
/*      */         }
/*      */         
/*  765 */         paramTypesRs = dbmd.getProcedureColumns((this.connection.versionMeetsMinimum(5, 0, 2)) && (useCatalog) ? this.currentCatalog : tmpCatalog, null, procName, "%");
/*      */         
/*      */ 
/*  768 */         boolean hasResults = false;
/*      */         try {
/*  770 */           if (paramTypesRs.next()) {
/*  771 */             paramTypesRs.previous();
/*  772 */             hasResults = true;
/*      */           }
/*      */         }
/*      */         catch (Exception e) {}
/*      */         
/*  777 */         if (hasResults) {
/*  778 */           convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
/*      */         } else {
/*  780 */           fakeParameterTypes(true);
/*      */         }
/*      */       } finally {
/*  783 */         SQLException sqlExRethrow = null;
/*      */         
/*  785 */         if (paramTypesRs != null) {
/*      */           try {
/*  787 */             paramTypesRs.close();
/*      */           } catch (SQLException sqlEx) {
/*  789 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */           
/*  792 */           paramTypesRs = null;
/*      */         }
/*      */         
/*  795 */         if (sqlExRethrow != null) {
/*  796 */           throw sqlExRethrow;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
/*  803 */     synchronized (checkClosed().getConnectionMutex()) {
/*  804 */       this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  815 */     synchronized (checkClosed().getConnectionMutex()) {
/*  816 */       boolean returnVal = false;
/*      */       
/*  818 */       checkStreamability();
/*      */       
/*  820 */       setInOutParamsOnServer();
/*  821 */       setOutParams();
/*      */       
/*  823 */       returnVal = super.execute();
/*      */       
/*  825 */       if (this.callingStoredFunction) {
/*  826 */         this.functionReturnValueResults = this.results;
/*  827 */         this.functionReturnValueResults.next();
/*  828 */         this.results = null;
/*      */       }
/*      */       
/*  831 */       retrieveOutParams();
/*      */       
/*  833 */       if (!this.callingStoredFunction) {
/*  834 */         return returnVal;
/*      */       }
/*      */       
/*      */ 
/*  838 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  849 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  851 */       checkStreamability();
/*      */       
/*  853 */       ResultSet execResults = null;
/*      */       
/*  855 */       setInOutParamsOnServer();
/*  856 */       setOutParams();
/*      */       
/*  858 */       execResults = super.executeQuery();
/*      */       
/*  860 */       retrieveOutParams();
/*      */       
/*  862 */       return execResults;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  873 */     return Util.truncateAndConvertToInt(executeLargeUpdate());
/*      */   }
/*      */   
/*      */   private String extractProcedureName() throws SQLException {
/*  877 */     String sanitizedSql = StringUtils.stripComments(this.originalSql, "`\"'", "`\"'", true, false, true, true);
/*      */     
/*      */ 
/*  880 */     int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
/*  881 */     int offset = 5;
/*      */     
/*  883 */     if (endCallIndex == -1) {
/*  884 */       endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
/*  885 */       offset = 7;
/*      */     }
/*      */     
/*  888 */     if (endCallIndex != -1) {
/*  889 */       StringBuilder nameBuf = new StringBuilder();
/*      */       
/*  891 */       String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
/*      */       
/*  893 */       int statementLength = trimmedStatement.length();
/*      */       
/*  895 */       for (int i = 0; i < statementLength; i++) {
/*  896 */         char c = trimmedStatement.charAt(i);
/*      */         
/*  898 */         if ((Character.isWhitespace(c)) || (c == '(') || (c == '?')) {
/*      */           break;
/*      */         }
/*  901 */         nameBuf.append(c);
/*      */       }
/*      */       
/*      */ 
/*  905 */       return nameBuf.toString();
/*      */     }
/*      */     
/*  908 */     throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), "S1000", getExceptionInterceptor());
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
/*      */   protected String fixParameterName(String paramNameIn)
/*      */     throws SQLException
/*      */   {
/*  923 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  925 */       if (((paramNameIn == null) || (paramNameIn.length() == 0)) && (!hasParametersView())) {
/*  926 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.0") + paramNameIn == null ? Messages.getString("CallableStatement.15") : Messages.getString("CallableStatement.16"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  931 */       if ((paramNameIn == null) && (hasParametersView())) {
/*  932 */         paramNameIn = "nullpn";
/*      */       }
/*      */       
/*  935 */       if (this.connection.getNoAccessToProcedureBodies()) {
/*  936 */         throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*  940 */       return mangleParameterName(paramNameIn);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/*  948 */     synchronized (checkClosed().getConnectionMutex()) {
/*  949 */       ResultSetInternalMethods rs = getOutputParameters(i);
/*      */       
/*  951 */       Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */       
/*  953 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/*  955 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  963 */     synchronized (checkClosed().getConnectionMutex()) {
/*  964 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/*  966 */       Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */       
/*  968 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/*  970 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  978 */     synchronized (checkClosed().getConnectionMutex()) {
/*  979 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/*  981 */       BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/*  983 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/*  985 */       return retValue;
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
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1000 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1001 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1003 */       BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */       
/* 1005 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1007 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1015 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1016 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1018 */       BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */       
/* 1020 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1022 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1030 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1031 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1033 */       Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1035 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1037 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1045 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1046 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1048 */       Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */       
/* 1050 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1052 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1060 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1061 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1063 */       boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1065 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1067 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1075 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1076 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1078 */       boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */       
/* 1080 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1082 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1090 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1091 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1093 */       byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1095 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1097 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1105 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1106 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1108 */       byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */       
/* 1110 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1112 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1120 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1121 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1123 */       byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1125 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1127 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1135 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1136 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1138 */       byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */       
/* 1140 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1142 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1150 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1151 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1153 */       Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1155 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1157 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1165 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1166 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1168 */       Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */       
/* 1170 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1172 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Date getDate(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1180 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1181 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1183 */       Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1185 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1187 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1195 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1196 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1198 */       Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */       
/* 1200 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1202 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1210 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1211 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1213 */       Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */       
/* 1215 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1217 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1225 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1226 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1228 */       Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */       
/* 1230 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1232 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public double getDouble(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1240 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1241 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1243 */       double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1245 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1247 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1255 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1256 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1258 */       double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */       
/* 1260 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1262 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1270 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1271 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1273 */       float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1275 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1277 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1285 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1286 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1288 */       float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */       
/* 1290 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1292 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1300 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1301 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1303 */       int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1305 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1307 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1315 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1316 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1318 */       int retValue = rs.getInt(fixParameterName(parameterName));
/*      */       
/* 1320 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1322 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1330 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1331 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1333 */       long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1335 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1337 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1345 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1346 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1348 */       long retValue = rs.getLong(fixParameterName(parameterName));
/*      */       
/* 1350 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1352 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */   protected int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
/* 1357 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1358 */       if (this.connection.getNoAccessToProcedureBodies()) {
/* 1359 */         throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1364 */       if ((paramName == null) || (paramName.length() == 0)) {
/* 1365 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/* 1368 */       if (this.paramInfo == null) {
/* 1369 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1373 */       CallableStatementParam namedParamInfo = this.paramInfo.getParameter(paramName);
/*      */       
/* 1375 */       if ((forOut) && (!namedParamInfo.isOut)) {
/* 1376 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1380 */       if (this.placeholderToParameterIndexMap == null) {
/* 1381 */         return namedParamInfo.index + 1;
/*      */       }
/*      */       
/* 1384 */       for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 1385 */         if (this.placeholderToParameterIndexMap[i] == namedParamInfo.index) {
/* 1386 */           return i + 1;
/*      */         }
/*      */       }
/*      */       
/* 1390 */       throw SQLError.createSQLException("Can't find local placeholder mapping for parameter named \"" + paramName + "\".", "S1009", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1399 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1400 */       CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */       
/* 1402 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1404 */       Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
/*      */       
/* 1406 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1408 */       return retVal;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getObject(int parameterIndex, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 1416 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1417 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1419 */       Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */       
/* 1421 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1423 */       return retVal;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1431 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1432 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1434 */       Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */       
/* 1436 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1438 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getObject(String parameterName, Map<String, Class<?>> map)
/*      */     throws SQLException
/*      */   {
/* 1446 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1447 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1449 */       Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */       
/* 1451 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1453 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */   public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException
/*      */   {
/* 1459 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1460 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/*      */ 
/* 1463 */       T retVal = ((ResultSetImpl)rs).getObject(mapOutputParameterIndexToRsIndex(parameterIndex), type);
/*      */       
/* 1465 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1467 */       return retVal;
/*      */     }
/*      */   }
/*      */   
/*      */   public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
/* 1472 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1473 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1475 */       T retValue = ((ResultSetImpl)rs).getObject(fixParameterName(parameterName), type);
/*      */       
/* 1477 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1479 */       return retValue;
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
/*      */   protected ResultSetInternalMethods getOutputParameters(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1494 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1495 */       this.outputParamWasNull = false;
/*      */       
/* 1497 */       if ((paramIndex == 1) && (this.callingStoredFunction) && (this.returnValueParam != null)) {
/* 1498 */         return this.functionReturnValueResults;
/*      */       }
/*      */       
/* 1501 */       if (this.outputParameterResults == null) {
/* 1502 */         if (this.paramInfo.numberOfParameters() == 0) {
/* 1503 */           throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/* 1506 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       
/* 1509 */       return this.outputParameterResults;
/*      */     }
/*      */   }
/*      */   
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException
/*      */   {
/* 1515 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1516 */       if (this.placeholderToParameterIndexMap == null) {
/* 1517 */         return this.paramInfo;
/*      */       }
/*      */       
/* 1520 */       return new CallableStatementParamInfo(this.paramInfo);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1528 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1529 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1531 */       Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1533 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1535 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1543 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1544 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1546 */       Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */       
/* 1548 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1550 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1558 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1559 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1561 */       short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1563 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1565 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1573 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1574 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1576 */       short retValue = rs.getShort(fixParameterName(parameterName));
/*      */       
/* 1578 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1580 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1588 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1589 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1591 */       String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1593 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1595 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1603 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1604 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1606 */       String retValue = rs.getString(fixParameterName(parameterName));
/*      */       
/* 1608 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1610 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1618 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1619 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1621 */       Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1623 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1625 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1633 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1634 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1636 */       Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */       
/* 1638 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1640 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1648 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1649 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1651 */       Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */       
/* 1653 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1655 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1663 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1664 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1666 */       Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */       
/* 1668 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1670 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1678 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1679 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1681 */       Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1683 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1685 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1693 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1694 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1696 */       Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */       
/* 1698 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1700 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1708 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1709 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1711 */       Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */       
/* 1713 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1715 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1723 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1724 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1726 */       Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */       
/* 1728 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1730 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1738 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1739 */       ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*      */       
/* 1741 */       URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */       
/* 1743 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1745 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1753 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1754 */       ResultSetInternalMethods rs = getOutputParameters(0);
/*      */       
/* 1756 */       URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */       
/* 1758 */       this.outputParamWasNull = rs.wasNull();
/*      */       
/* 1760 */       return retValue;
/*      */     }
/*      */   }
/*      */   
/*      */   protected int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException
/*      */   {
/* 1766 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1767 */       if ((this.returnValueParam != null) && (paramIndex == 1)) {
/* 1768 */         return 1;
/*      */       }
/*      */       
/* 1771 */       checkParameterIndexBounds(paramIndex);
/*      */       
/* 1773 */       int localParamIndex = paramIndex - 1;
/*      */       
/* 1775 */       if (this.placeholderToParameterIndexMap != null) {
/* 1776 */         localParamIndex = this.placeholderToParameterIndexMap[localParamIndex];
/*      */       }
/*      */       
/* 1779 */       int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */       
/* 1781 */       if (rsIndex == Integer.MIN_VALUE) {
/* 1782 */         throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1786 */       return rsIndex + 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1794 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 1795 */     paramDescriptor.desiredJdbcType = sqlType;
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1802 */     registerOutParameter(parameterIndex, sqlType);
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1809 */     checkIsOutputParam(parameterIndex);
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1816 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1817 */       registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1825 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */   
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1832 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void retrieveOutParams()
/*      */     throws SQLException
/*      */   {
/* 1842 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1843 */       int numParameters = this.paramInfo.numberOfParameters();
/*      */       
/* 1845 */       this.parameterIndexToRsIndex = new int[numParameters];
/*      */       
/* 1847 */       for (int i = 0; i < numParameters; i++) {
/* 1848 */         this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE;
/*      */       }
/*      */       
/* 1851 */       int localParamIndex = 0;
/*      */       
/* 1853 */       if (numParameters > 0) {
/* 1854 */         StringBuilder outParameterQuery = new StringBuilder("SELECT ");
/*      */         
/* 1856 */         boolean firstParam = true;
/* 1857 */         boolean hadOutputParams = false;
/*      */         
/* 1859 */         for (Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator(); paramIter.hasNext();) {
/* 1860 */           CallableStatementParam retrParamInfo = (CallableStatementParam)paramIter.next();
/*      */           
/* 1862 */           if (retrParamInfo.isOut) {
/* 1863 */             hadOutputParams = true;
/*      */             
/* 1865 */             this.parameterIndexToRsIndex[retrParamInfo.index] = (localParamIndex++);
/*      */             
/* 1867 */             if ((retrParamInfo.paramName == null) && (hasParametersView())) {
/* 1868 */               retrParamInfo.paramName = ("nullnp" + retrParamInfo.index);
/*      */             }
/*      */             
/* 1871 */             String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */             
/* 1873 */             if (!firstParam) {
/* 1874 */               outParameterQuery.append(",");
/*      */             } else {
/* 1876 */               firstParam = false;
/*      */             }
/*      */             
/* 1879 */             if (!outParameterName.startsWith("@")) {
/* 1880 */               outParameterQuery.append('@');
/*      */             }
/*      */             
/* 1883 */             outParameterQuery.append(outParameterName);
/*      */           }
/*      */         }
/*      */         
/* 1887 */         if (hadOutputParams)
/*      */         {
/* 1889 */           Statement outParameterStmt = null;
/* 1890 */           ResultSet outParamRs = null;
/*      */           try
/*      */           {
/* 1893 */             outParameterStmt = this.connection.createStatement();
/* 1894 */             outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/* 1895 */             this.outputParameterResults = ((ResultSetInternalMethods)outParamRs).copy();
/*      */             
/* 1897 */             if (!this.outputParameterResults.next()) {
/* 1898 */               this.outputParameterResults.close();
/* 1899 */               this.outputParameterResults = null;
/*      */             }
/*      */           } finally {
/* 1902 */             if (outParameterStmt != null) {
/* 1903 */               outParameterStmt.close();
/*      */             }
/*      */           }
/*      */         } else {
/* 1907 */           this.outputParameterResults = null;
/*      */         }
/*      */       } else {
/* 1910 */         this.outputParameterResults = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1919 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1926 */     setBigDecimal(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1933 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1940 */     setBoolean(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1947 */     setByte(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1954 */     setBytes(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1961 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/* 1968 */     setDate(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1975 */     setDate(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/* 1982 */     setDouble(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1989 */   public void setFloat(String parameterName, float x)
/* 1989 */     throws SQLException { setFloat(getNamedParamIndex(parameterName, false), x); }
/*      */   
/*      */   private void setInOutParamsOnServer() throws SQLException {
/*      */     Iterator<CallableStatementParam> paramIter;
/* 1993 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1994 */       if (this.paramInfo.numParameters > 0) {
/* 1995 */         for (paramIter = this.paramInfo.iterator(); paramIter.hasNext();)
/*      */         {
/* 1997 */           CallableStatementParam inParamInfo = (CallableStatementParam)paramIter.next();
/*      */           
/*      */ 
/* 2000 */           if ((inParamInfo.isOut) && (inParamInfo.isIn)) {
/* 2001 */             if ((inParamInfo.paramName == null) && (hasParametersView())) {
/* 2002 */               inParamInfo.paramName = ("nullnp" + inParamInfo.index);
/*      */             }
/*      */             
/* 2005 */             String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 2006 */             StringBuilder queryBuf = new StringBuilder(4 + inOutParameterName.length() + 1 + 1);
/* 2007 */             queryBuf.append("SET ");
/* 2008 */             queryBuf.append(inOutParameterName);
/* 2009 */             queryBuf.append("=?");
/*      */             
/* 2011 */             PreparedStatement setPstmt = null;
/*      */             try
/*      */             {
/* 2014 */               setPstmt = (PreparedStatement)((Wrapper)this.connection.clientPrepareStatement(queryBuf.toString())).unwrap(PreparedStatement.class);
/*      */               
/* 2016 */               if (this.isNull[inParamInfo.index] != 0) {
/* 2017 */                 setPstmt.setBytesNoEscapeNoQuotes(1, "NULL".getBytes());
/*      */               }
/*      */               else {
/* 2020 */                 byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index);
/*      */                 
/* 2022 */                 if (parameterAsBytes != null) {
/* 2023 */                   if ((parameterAsBytes.length > 8) && (parameterAsBytes[0] == 95) && (parameterAsBytes[1] == 98) && (parameterAsBytes[2] == 105) && (parameterAsBytes[3] == 110) && (parameterAsBytes[4] == 97) && (parameterAsBytes[5] == 114) && (parameterAsBytes[6] == 121) && (parameterAsBytes[7] == 39))
/*      */                   {
/*      */ 
/* 2026 */                     setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */                   } else {
/* 2028 */                     int sqlType = inParamInfo.desiredJdbcType;
/*      */                     
/* 2030 */                     switch (sqlType) {
/*      */                     case -7: 
/*      */                     case -4: 
/*      */                     case -3: 
/*      */                     case -2: 
/*      */                     case 2000: 
/*      */                     case 2004: 
/* 2037 */                       setPstmt.setBytes(1, parameterAsBytes);
/* 2038 */                       break;
/*      */                     
/*      */                     default: 
/* 2041 */                       setPstmt.setBytesNoEscape(1, parameterAsBytes);
/*      */                     }
/*      */                   }
/*      */                 } else {
/* 2045 */                   setPstmt.setNull(1, 0);
/*      */                 }
/*      */               }
/*      */               
/* 2049 */               setPstmt.executeUpdate();
/*      */             } finally {
/* 2051 */               if (setPstmt != null) {
/* 2052 */                 setPstmt.close();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/* 2065 */     setInt(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/* 2072 */     setLong(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2079 */     setNull(getNamedParamIndex(parameterName, false), sqlType);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 2086 */     setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2093 */     setObject(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 2100 */     setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setOutParams()
/*      */     throws SQLException
/*      */   {
/*      */     Iterator<CallableStatementParam> paramIter;
/*      */     
/* 2110 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2111 */       if (this.paramInfo.numParameters > 0) {
/* 2112 */         for (paramIter = this.paramInfo.iterator(); paramIter.hasNext();) {
/* 2113 */           CallableStatementParam outParamInfo = (CallableStatementParam)paramIter.next();
/*      */           
/* 2115 */           if ((!this.callingStoredFunction) && (outParamInfo.isOut))
/*      */           {
/* 2117 */             if ((outParamInfo.paramName == null) && (hasParametersView())) {
/* 2118 */               outParamInfo.paramName = ("nullnp" + outParamInfo.index);
/*      */             }
/*      */             
/* 2121 */             String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */             
/* 2123 */             int outParamIndex = 0;
/*      */             
/* 2125 */             if (this.placeholderToParameterIndexMap == null) {
/* 2126 */               outParamIndex = outParamInfo.index + 1;
/*      */             }
/*      */             else {
/* 2129 */               boolean found = false;
/*      */               
/* 2131 */               for (int i = 0; i < this.placeholderToParameterIndexMap.length; i++) {
/* 2132 */                 if (this.placeholderToParameterIndexMap[i] == outParamInfo.index) {
/* 2133 */                   outParamIndex = i + 1;
/* 2134 */                   found = true;
/* 2135 */                   break;
/*      */                 }
/*      */               }
/*      */               
/* 2139 */               if (!found) {
/* 2140 */                 throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + outParamInfo.paramName + Messages.getString("CallableStatement.22"), "S1009", getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2146 */             setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/* 2158 */     setShort(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/* 2165 */     setString(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2172 */     setTime(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2179 */     setTime(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2186 */     setTimestamp(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2193 */     setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/* 2200 */     setURL(getNamedParamIndex(parameterName, false), val);
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
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/* 2214 */     return Util.truncateAndConvertToInt(executeLargeBatch());
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getParameterIndexOffset()
/*      */   {
/* 2220 */     if (this.callingStoredFunction) {
/* 2221 */       return -1;
/*      */     }
/*      */     
/* 2224 */     return super.getParameterIndexOffset();
/*      */   }
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/* 2228 */     setAsciiStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2233 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException
/*      */   {
/* 2238 */     setBinaryStream(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException
/*      */   {
/* 2243 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, Blob x) throws SQLException
/*      */   {
/* 2248 */     setBlob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, InputStream inputStream) throws SQLException
/*      */   {
/* 2253 */     setBlob(getNamedParamIndex(parameterName, false), inputStream);
/*      */   }
/*      */   
/*      */   public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException
/*      */   {
/* 2258 */     setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2263 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2268 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Clob x) throws SQLException
/*      */   {
/* 2273 */     setClob(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Reader reader) throws SQLException
/*      */   {
/* 2278 */     setClob(getNamedParamIndex(parameterName, false), reader);
/*      */   }
/*      */   
/*      */   public void setClob(String parameterName, Reader reader, long length) throws SQLException
/*      */   {
/* 2283 */     setClob(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader value) throws SQLException
/*      */   {
/* 2288 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value);
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException
/*      */   {
/* 2293 */     setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean checkReadOnlyProcedure()
/*      */     throws SQLException
/*      */   {
/* 2304 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2305 */       if (this.connection.getNoAccessToProcedureBodies()) {
/* 2306 */         return false;
/*      */       }
/*      */       
/* 2309 */       if (this.paramInfo.isReadOnlySafeChecked) {
/* 2310 */         return this.paramInfo.isReadOnlySafeProcedure;
/*      */       }
/*      */       
/* 2313 */       ResultSet rs = null;
/* 2314 */       java.sql.PreparedStatement ps = null;
/*      */       try
/*      */       {
/* 2317 */         String procName = extractProcedureName();
/*      */         
/* 2319 */         String catalog = this.currentCatalog;
/*      */         
/* 2321 */         if (procName.indexOf(".") != -1) {
/* 2322 */           catalog = procName.substring(0, procName.indexOf("."));
/*      */           
/* 2324 */           if ((StringUtils.startsWithIgnoreCaseAndWs(catalog, "`")) && (catalog.trim().endsWith("`"))) {
/* 2325 */             catalog = catalog.substring(1, catalog.length() - 1);
/*      */           }
/*      */           
/* 2328 */           procName = procName.substring(procName.indexOf(".") + 1);
/* 2329 */           procName = StringUtils.toString(StringUtils.stripEnclosure(StringUtils.getBytes(procName), "`", "`"));
/*      */         }
/* 2331 */         ps = this.connection.prepareStatement("SELECT SQL_DATA_ACCESS FROM information_schema.routines WHERE routine_schema = ? AND routine_name = ?");
/* 2332 */         ps.setMaxRows(0);
/* 2333 */         ps.setFetchSize(0);
/*      */         
/* 2335 */         ps.setString(1, catalog);
/* 2336 */         ps.setString(2, procName);
/* 2337 */         rs = ps.executeQuery();
/* 2338 */         if (rs.next()) {
/* 2339 */           String sqlDataAccess = rs.getString(1);
/* 2340 */           if (("READS SQL DATA".equalsIgnoreCase(sqlDataAccess)) || ("NO SQL".equalsIgnoreCase(sqlDataAccess))) {
/* 2341 */             synchronized (this.paramInfo) {
/* 2342 */               this.paramInfo.isReadOnlySafeChecked = true;
/* 2343 */               this.paramInfo.isReadOnlySafeProcedure = true;
/*      */             }
/* 2345 */             ??? = 1;jsr 30;return ???;
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (SQLException e) {}finally
/*      */       {
/* 2351 */         jsr 6; } localObject3 = returnAddress; if (rs != null) {
/* 2352 */         rs.close();
/*      */       }
/* 2354 */       if (ps != null)
/* 2355 */         ps.close(); ret;
/*      */       
/*      */ 
/*      */ 
/* 2359 */       this.paramInfo.isReadOnlySafeChecked = false;
/* 2360 */       this.paramInfo.isReadOnlySafeProcedure = false;
/*      */     }
/* 2362 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean checkReadOnlySafeStatement()
/*      */     throws SQLException
/*      */   {
/* 2368 */     return (super.checkReadOnlySafeStatement()) || (checkReadOnlyProcedure());
/*      */   }
/*      */   
/*      */   private boolean hasParametersView() throws SQLException {
/* 2372 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 2374 */         if (this.connection.versionMeetsMinimum(5, 5, 0)) {
/* 2375 */           java.sql.DatabaseMetaData dbmd1 = new DatabaseMetaDataUsingInfoSchema(this.connection, this.connection.getCatalog());
/* 2376 */           return ((DatabaseMetaDataUsingInfoSchema)dbmd1).gethasParametersView();
/*      */         }
/*      */         
/* 2379 */         return false;
/*      */       } catch (SQLException e) {
/* 2381 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate()
/*      */     throws SQLException
/*      */   {
/* 2391 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2392 */       long returnVal = -1L;
/*      */       
/* 2394 */       checkStreamability();
/*      */       
/* 2396 */       if (this.callingStoredFunction) {
/* 2397 */         execute();
/*      */         
/* 2399 */         return -1L;
/*      */       }
/*      */       
/* 2402 */       setInOutParamsOnServer();
/* 2403 */       setOutParams();
/*      */       
/* 2405 */       returnVal = super.executeLargeUpdate();
/*      */       
/* 2407 */       retrieveOutParams();
/*      */       
/* 2409 */       return returnVal;
/*      */     }
/*      */   }
/*      */   
/*      */   public long[] executeLargeBatch() throws SQLException
/*      */   {
/* 2415 */     if (this.hasOutputParams) {
/* 2416 */       throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 2420 */     return super.executeLargeBatch();
/*      */   }
/*      */   
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {}
/*      */   
/*      */   /* Error */
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 30	com/mysql/jdbc/CallableStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 31 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 15	com/mysql/jdbc/CallableStatement:outputParamWasNull	Z
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2207	-> byte code offset #0
/*      */     //   Java source line #2208	-> byte code offset #12
/*      */     //   Java source line #2209	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	CallableStatement
/*      */     //   10	11	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CallableStatement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */