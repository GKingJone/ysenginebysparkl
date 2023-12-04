/*      */ package com.mysql.jdbc.jdbc2.optional;
/*      */ 
/*      */ import com.mysql.jdbc.SQLError;
/*      */ import com.mysql.jdbc.Util;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CallableStatementWrapper
/*      */   extends PreparedStatementWrapper
/*      */   implements CallableStatement
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR;
/*      */   
/*      */   static
/*      */   {
/*   54 */     if (Util.isJdbc4()) {
/*      */       try {
/*   56 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.jdbc2.optional.JDBC42CallableStatementWrapper" : "com.mysql.jdbc.jdbc2.optional.JDBC4CallableStatementWrapper";
/*      */         
/*   58 */         JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, CallableStatement.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*   61 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   63 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   65 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   68 */       JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static CallableStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap) throws SQLException {
/*   73 */     if (!Util.isJdbc4()) {
/*   74 */       return new CallableStatementWrapper(c, conn, toWrap);
/*      */     }
/*      */     
/*   77 */     return (CallableStatementWrapper)Util.handleNewInstance(JDBC_4_CALLABLE_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*      */   {
/*   87 */     super(c, conn, toWrap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   97 */       if (this.wrappedStmt != null) {
/*   98 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*      */       } else {
/*  100 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  103 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  114 */       if (this.wrappedStmt != null) {
/*  115 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*      */       } else {
/*  117 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  120 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  131 */       if (this.wrappedStmt != null) {
/*  132 */         return ((CallableStatement)this.wrappedStmt).wasNull();
/*      */       }
/*  134 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  137 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  140 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  150 */       if (this.wrappedStmt != null) {
/*  151 */         return ((CallableStatement)this.wrappedStmt).getString(parameterIndex);
/*      */       }
/*  153 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  156 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  158 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  168 */       if (this.wrappedStmt != null) {
/*  169 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterIndex);
/*      */       }
/*  171 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  174 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  177 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  187 */       if (this.wrappedStmt != null) {
/*  188 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterIndex);
/*      */       }
/*  190 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  193 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  196 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  206 */       if (this.wrappedStmt != null) {
/*  207 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterIndex);
/*      */       }
/*  209 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  212 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  215 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  225 */       if (this.wrappedStmt != null) {
/*  226 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterIndex);
/*      */       }
/*  228 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  231 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  234 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  244 */       if (this.wrappedStmt != null) {
/*  245 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterIndex);
/*      */       }
/*  247 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  250 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  253 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  263 */       if (this.wrappedStmt != null) {
/*  264 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterIndex);
/*      */       }
/*  266 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  269 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  272 */     return 0.0F;
/*      */   }
/*      */   
/*      */   public double getDouble(int parameterIndex) throws SQLException {
/*      */     try {
/*  277 */       if (this.wrappedStmt != null) {
/*  278 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterIndex);
/*      */       }
/*  280 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  283 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  286 */     return 0.0D;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
/*      */     try {
/*  292 */       if (this.wrappedStmt != null) {
/*  293 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex, scale);
/*      */       }
/*  295 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  298 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  301 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  311 */       if (this.wrappedStmt != null) {
/*  312 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterIndex);
/*      */       }
/*  314 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  317 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  320 */     return null;
/*      */   }
/*      */   
/*      */   public Date getDate(int parameterIndex) throws SQLException {
/*      */     try {
/*  325 */       if (this.wrappedStmt != null) {
/*  326 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex);
/*      */       }
/*  328 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  331 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  334 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  344 */       if (this.wrappedStmt != null) {
/*  345 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex);
/*      */       }
/*  347 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  350 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  353 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  363 */       if (this.wrappedStmt != null) {
/*  364 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex);
/*      */       }
/*  366 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  369 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  372 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  382 */       if (this.wrappedStmt != null) {
/*  383 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex);
/*      */       }
/*  385 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  388 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  391 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  401 */       if (this.wrappedStmt != null) {
/*  402 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex);
/*      */       }
/*  404 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  407 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  410 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getObject(int parameterIndex, Map<String, Class<?>> typeMap)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  420 */       if (this.wrappedStmt != null) {
/*  421 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterIndex, typeMap);
/*      */       }
/*  423 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  426 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  428 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  438 */       if (this.wrappedStmt != null) {
/*  439 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterIndex);
/*      */       }
/*  441 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  444 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  447 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  457 */       if (this.wrappedStmt != null) {
/*  458 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterIndex);
/*      */       }
/*  460 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  463 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  466 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  476 */       if (this.wrappedStmt != null) {
/*  477 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterIndex);
/*      */       }
/*  479 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  482 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  484 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Array getArray(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  494 */       if (this.wrappedStmt != null) {
/*  495 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterIndex);
/*      */       }
/*  497 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  500 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  502 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  512 */       if (this.wrappedStmt != null) {
/*  513 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterIndex, cal);
/*      */       }
/*  515 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  518 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  520 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  530 */       if (this.wrappedStmt != null) {
/*  531 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterIndex, cal);
/*      */       }
/*  533 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  536 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  538 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  548 */       if (this.wrappedStmt != null) {
/*  549 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex, cal);
/*      */       }
/*  551 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  554 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*  556 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(int paramIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  566 */       if (this.wrappedStmt != null) {
/*  567 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(paramIndex, sqlType, typeName);
/*      */       } else {
/*  569 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  572 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  583 */       if (this.wrappedStmt != null) {
/*  584 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*      */       } else {
/*  586 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  589 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  600 */       if (this.wrappedStmt != null) {
/*  601 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*      */       } else {
/*  603 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  606 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  617 */       if (this.wrappedStmt != null) {
/*  618 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*      */       } else {
/*  620 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  623 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  634 */       if (this.wrappedStmt != null) {
/*  635 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterIndex);
/*      */       }
/*  637 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  640 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/*  643 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  653 */       if (this.wrappedStmt != null) {
/*  654 */         ((CallableStatement)this.wrappedStmt).setURL(parameterName, val);
/*      */       } else {
/*  656 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  659 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  670 */       if (this.wrappedStmt != null) {
/*  671 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType);
/*      */       } else {
/*  673 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  676 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  687 */       if (this.wrappedStmt != null) {
/*  688 */         ((CallableStatement)this.wrappedStmt).setBoolean(parameterName, x);
/*      */       } else {
/*  690 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  693 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  704 */       if (this.wrappedStmt != null) {
/*  705 */         ((CallableStatement)this.wrappedStmt).setByte(parameterName, x);
/*      */       } else {
/*  707 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  710 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  721 */       if (this.wrappedStmt != null) {
/*  722 */         ((CallableStatement)this.wrappedStmt).setShort(parameterName, x);
/*      */       } else {
/*  724 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  727 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  738 */       if (this.wrappedStmt != null) {
/*  739 */         ((CallableStatement)this.wrappedStmt).setInt(parameterName, x);
/*      */       } else {
/*  741 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  744 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  755 */       if (this.wrappedStmt != null) {
/*  756 */         ((CallableStatement)this.wrappedStmt).setLong(parameterName, x);
/*      */       } else {
/*  758 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  761 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setFloat(String parameterName, float x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  772 */       if (this.wrappedStmt != null) {
/*  773 */         ((CallableStatement)this.wrappedStmt).setFloat(parameterName, x);
/*      */       } else {
/*  775 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  778 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  789 */       if (this.wrappedStmt != null) {
/*  790 */         ((CallableStatement)this.wrappedStmt).setDouble(parameterName, x);
/*      */       } else {
/*  792 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  795 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  806 */       if (this.wrappedStmt != null) {
/*  807 */         ((CallableStatement)this.wrappedStmt).setBigDecimal(parameterName, x);
/*      */       } else {
/*  809 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  812 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  823 */       if (this.wrappedStmt != null) {
/*  824 */         ((CallableStatement)this.wrappedStmt).setString(parameterName, x);
/*      */       } else {
/*  826 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  829 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  840 */       if (this.wrappedStmt != null) {
/*  841 */         ((CallableStatement)this.wrappedStmt).setBytes(parameterName, x);
/*      */       } else {
/*  843 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  846 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  857 */       if (this.wrappedStmt != null) {
/*  858 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x);
/*      */       } else {
/*  860 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  863 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  874 */       if (this.wrappedStmt != null) {
/*  875 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x);
/*      */       } else {
/*  877 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  880 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  891 */       if (this.wrappedStmt != null) {
/*  892 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x);
/*      */       } else {
/*  894 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  897 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  908 */       if (this.wrappedStmt != null) {
/*  909 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*      */       } else {
/*  911 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  914 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  926 */       if (this.wrappedStmt != null) {
/*  927 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*      */       } else {
/*  929 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  932 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  943 */       if (this.wrappedStmt != null) {
/*  944 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scale);
/*      */       } else {
/*  946 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  949 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  960 */       if (this.wrappedStmt != null) {
/*  961 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*      */       } else {
/*  963 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  966 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  977 */       if (this.wrappedStmt != null) {
/*  978 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x);
/*      */       } else {
/*  980 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/*  983 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  994 */       if (this.wrappedStmt != null) {
/*  995 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*      */       } else {
/*  997 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 1000 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1011 */       if (this.wrappedStmt != null) {
/* 1012 */         ((CallableStatement)this.wrappedStmt).setDate(parameterName, x, cal);
/*      */       } else {
/* 1014 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 1017 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1028 */       if (this.wrappedStmt != null) {
/* 1029 */         ((CallableStatement)this.wrappedStmt).setTime(parameterName, x, cal);
/*      */       } else {
/* 1031 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 1034 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1045 */       if (this.wrappedStmt != null) {
/* 1046 */         ((CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x, cal);
/*      */       } else {
/* 1048 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 1051 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1062 */       if (this.wrappedStmt != null) {
/* 1063 */         ((CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType, typeName);
/*      */       } else {
/* 1065 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */       }
/*      */     } catch (SQLException sqlEx) {
/* 1068 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1079 */       if (this.wrappedStmt != null) {
/* 1080 */         return ((CallableStatement)this.wrappedStmt).getString(parameterName);
/*      */       }
/* 1082 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1085 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1087 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1097 */       if (this.wrappedStmt != null) {
/* 1098 */         return ((CallableStatement)this.wrappedStmt).getBoolean(parameterName);
/*      */       }
/* 1100 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1103 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1106 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1116 */       if (this.wrappedStmt != null) {
/* 1117 */         return ((CallableStatement)this.wrappedStmt).getByte(parameterName);
/*      */       }
/* 1119 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1122 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1125 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1135 */       if (this.wrappedStmt != null) {
/* 1136 */         return ((CallableStatement)this.wrappedStmt).getShort(parameterName);
/*      */       }
/* 1138 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1141 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1144 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1154 */       if (this.wrappedStmt != null) {
/* 1155 */         return ((CallableStatement)this.wrappedStmt).getInt(parameterName);
/*      */       }
/* 1157 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1160 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1163 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1173 */       if (this.wrappedStmt != null) {
/* 1174 */         return ((CallableStatement)this.wrappedStmt).getLong(parameterName);
/*      */       }
/* 1176 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1179 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1182 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1192 */       if (this.wrappedStmt != null) {
/* 1193 */         return ((CallableStatement)this.wrappedStmt).getFloat(parameterName);
/*      */       }
/* 1195 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1198 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1201 */     return 0.0F;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1211 */       if (this.wrappedStmt != null) {
/* 1212 */         return ((CallableStatement)this.wrappedStmt).getDouble(parameterName);
/*      */       }
/* 1214 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1217 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1220 */     return 0.0D;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1230 */       if (this.wrappedStmt != null) {
/* 1231 */         return ((CallableStatement)this.wrappedStmt).getBytes(parameterName);
/*      */       }
/* 1233 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1236 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1239 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1249 */       if (this.wrappedStmt != null) {
/* 1250 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName);
/*      */       }
/* 1252 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1255 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1258 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1268 */       if (this.wrappedStmt != null) {
/* 1269 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName);
/*      */       }
/* 1271 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1274 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1277 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1287 */       if (this.wrappedStmt != null) {
/* 1288 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName);
/*      */       }
/* 1290 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1293 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1296 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1306 */       if (this.wrappedStmt != null) {
/* 1307 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName);
/*      */       }
/* 1309 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1312 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1315 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1325 */       if (this.wrappedStmt != null) {
/* 1326 */         return ((CallableStatement)this.wrappedStmt).getBigDecimal(parameterName);
/*      */       }
/* 1328 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1331 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1334 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getObject(String parameterName, Map<String, Class<?>> typeMap)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1344 */       if (this.wrappedStmt != null) {
/* 1345 */         return ((CallableStatement)this.wrappedStmt).getObject(parameterName, typeMap);
/*      */       }
/* 1347 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1350 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1352 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1362 */       if (this.wrappedStmt != null) {
/* 1363 */         return ((CallableStatement)this.wrappedStmt).getRef(parameterName);
/*      */       }
/* 1365 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1368 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1371 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1381 */       if (this.wrappedStmt != null) {
/* 1382 */         return ((CallableStatement)this.wrappedStmt).getBlob(parameterName);
/*      */       }
/* 1384 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1387 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1390 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1400 */       if (this.wrappedStmt != null) {
/* 1401 */         return ((CallableStatement)this.wrappedStmt).getClob(parameterName);
/*      */       }
/* 1403 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1406 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1408 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1418 */       if (this.wrappedStmt != null) {
/* 1419 */         return ((CallableStatement)this.wrappedStmt).getArray(parameterName);
/*      */       }
/* 1421 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1424 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1426 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1436 */       if (this.wrappedStmt != null) {
/* 1437 */         return ((CallableStatement)this.wrappedStmt).getDate(parameterName, cal);
/*      */       }
/* 1439 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1442 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1444 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1454 */       if (this.wrappedStmt != null) {
/* 1455 */         return ((CallableStatement)this.wrappedStmt).getTime(parameterName, cal);
/*      */       }
/* 1457 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1460 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1462 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1472 */       if (this.wrappedStmt != null) {
/* 1473 */         return ((CallableStatement)this.wrappedStmt).getTimestamp(parameterName, cal);
/*      */       }
/* 1475 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1478 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/* 1480 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1490 */       if (this.wrappedStmt != null) {
/* 1491 */         return ((CallableStatement)this.wrappedStmt).getURL(parameterName);
/*      */       }
/* 1493 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 1496 */       checkAndFireConnectionError(sqlEx);
/*      */     }
/*      */     
/* 1499 */     return null;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\CallableStatementWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */