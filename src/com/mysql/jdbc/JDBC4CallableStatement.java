/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
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
/*     */ public class JDBC4CallableStatement
/*     */   extends CallableStatement
/*     */ {
/*     */   public JDBC4CallableStatement(MySQLConnection conn, CallableStatementParamInfo paramInfo)
/*     */     throws SQLException
/*     */   {
/*  38 */     super(conn, paramInfo);
/*     */   }
/*     */   
/*     */   public JDBC4CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
/*  42 */     super(conn, sql, catalog, isFunctionCall);
/*     */   }
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*  46 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */   
/*     */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*  50 */     JDBC4PreparedStatementHelper.setRowId(this, getNamedParamIndex(parameterName, false), x);
/*     */   }
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*  54 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */   
/*     */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*  58 */     JDBC4PreparedStatementHelper.setSQLXML(this, getNamedParamIndex(parameterName, false), xmlObject);
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int parameterIndex) throws SQLException
/*     */   {
/*  63 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/*  65 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/*  67 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/*  69 */     return retValue;
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String parameterName) throws SQLException
/*     */   {
/*  74 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/*  76 */     SQLXML retValue = ((JDBC4ResultSet)rs).getSQLXML(fixParameterName(parameterName));
/*     */     
/*  78 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/*  80 */     return retValue;
/*     */   }
/*     */   
/*     */   public RowId getRowId(int parameterIndex) throws SQLException {
/*  84 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/*  86 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/*  88 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/*  90 */     return retValue;
/*     */   }
/*     */   
/*     */   public RowId getRowId(String parameterName) throws SQLException {
/*  94 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/*  96 */     RowId retValue = ((JDBC4ResultSet)rs).getRowId(fixParameterName(parameterName));
/*     */     
/*  98 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 100 */     return retValue;
/*     */   }
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
/*     */   public void setNClob(int parameterIndex, NClob value)
/*     */     throws SQLException
/*     */   {
/* 115 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, NClob value) throws SQLException {
/* 119 */     JDBC4PreparedStatementHelper.setNClob(this, getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader) throws SQLException
/*     */   {
/* 124 */     setNClob(getNamedParamIndex(parameterName, false), reader);
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException
/*     */   {
/* 129 */     setNClob(getNamedParamIndex(parameterName, false), reader, length);
/*     */   }
/*     */   
/*     */   public void setNString(String parameterName, String value) throws SQLException
/*     */   {
/* 134 */     setNString(getNamedParamIndex(parameterName, false), value);
/*     */   }
/*     */   
/*     */ 
/*     */   public Reader getCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 141 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 143 */     Reader retValue = rs.getCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/* 145 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 147 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public Reader getCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 154 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/* 156 */     Reader retValue = rs.getCharacterStream(fixParameterName(parameterName));
/*     */     
/* 158 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 160 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public Reader getNCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 167 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 169 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/* 171 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 173 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public Reader getNCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 180 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/* 182 */     Reader retValue = ((JDBC4ResultSet)rs).getNCharacterStream(fixParameterName(parameterName));
/*     */     
/* 184 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 186 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public NClob getNClob(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 193 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 195 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/* 197 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 199 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public NClob getNClob(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 206 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/* 208 */     NClob retValue = ((JDBC4ResultSet)rs).getNClob(fixParameterName(parameterName));
/*     */     
/* 210 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 212 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNString(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/* 219 */     ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
/*     */     
/* 221 */     String retValue = ((JDBC4ResultSet)rs).getNString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*     */     
/* 223 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 225 */     return retValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getNString(String parameterName)
/*     */     throws SQLException
/*     */   {
/* 232 */     ResultSetInternalMethods rs = getOutputParameters(0);
/*     */     
/* 234 */     String retValue = ((JDBC4ResultSet)rs).getNString(fixParameterName(parameterName));
/*     */     
/* 236 */     this.outputParamWasNull = rs.wasNull();
/*     */     
/* 238 */     return retValue;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4CallableStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */