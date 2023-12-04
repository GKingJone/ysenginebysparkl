/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
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
/*     */ public class PrestoPreparedStatement
/*     */   extends PrestoStatement
/*     */   implements PreparedStatement
/*     */ {
/*     */   PrestoPreparedStatement(PrestoConnection connection, String sql)
/*     */     throws SQLException
/*     */   {
/*  45 */     super(connection);
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSet executeQuery()
/*     */     throws SQLException
/*     */   {
/*  52 */     throw new NotImplementedException("PreparedStatement", "executeQuery");
/*     */   }
/*     */   
/*     */ 
/*     */   public int executeUpdate()
/*     */     throws SQLException
/*     */   {
/*  59 */     throw new NotImplementedException("PreparedStatement", "executeUpdate");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNull(int parameterIndex, int sqlType)
/*     */     throws SQLException
/*     */   {
/*  66 */     throw new NotImplementedException("PreparedStatement", "setNull");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBoolean(int parameterIndex, boolean x)
/*     */     throws SQLException
/*     */   {
/*  73 */     throw new NotImplementedException("PreparedStatement", "setBoolean");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setByte(int parameterIndex, byte x)
/*     */     throws SQLException
/*     */   {
/*  80 */     throw new NotImplementedException("PreparedStatement", "setByte");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setShort(int parameterIndex, short x)
/*     */     throws SQLException
/*     */   {
/*  87 */     throw new NotImplementedException("PreparedStatement", "setShort");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setInt(int parameterIndex, int x)
/*     */     throws SQLException
/*     */   {
/*  94 */     throw new NotImplementedException("PreparedStatement", "setInt");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLong(int parameterIndex, long x)
/*     */     throws SQLException
/*     */   {
/* 101 */     throw new NotImplementedException("PreparedStatement", "setLong");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setFloat(int parameterIndex, float x)
/*     */     throws SQLException
/*     */   {
/* 108 */     throw new NotImplementedException("PreparedStatement", "setFloat");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDouble(int parameterIndex, double x)
/*     */     throws SQLException
/*     */   {
/* 115 */     throw new NotImplementedException("PreparedStatement", "setDouble");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*     */     throws SQLException
/*     */   {
/* 122 */     throw new NotImplementedException("PreparedStatement", "setBigDecimal");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setString(int parameterIndex, String x)
/*     */     throws SQLException
/*     */   {
/* 129 */     throw new NotImplementedException("PreparedStatement", "setString");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBytes(int parameterIndex, byte[] x)
/*     */     throws SQLException
/*     */   {
/* 136 */     throw new NotImplementedException("PreparedStatement", "setBytes");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDate(int parameterIndex, Date x)
/*     */     throws SQLException
/*     */   {
/* 143 */     throw new NotImplementedException("PreparedStatement", "setDate");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTime(int parameterIndex, Time x)
/*     */     throws SQLException
/*     */   {
/* 150 */     throw new NotImplementedException("PreparedStatement", "setTime");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTimestamp(int parameterIndex, Timestamp x)
/*     */     throws SQLException
/*     */   {
/* 157 */     throw new NotImplementedException("PreparedStatement", "setTimestamp");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/* 164 */     throw new NotImplementedException("PreparedStatement", "setAsciiStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/* 171 */     throw new SQLFeatureNotSupportedException("setUnicodeStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/* 178 */     throw new NotImplementedException("PreparedStatement", "setBinaryStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearParameters()
/*     */     throws SQLException
/*     */   {
/* 185 */     throw new NotImplementedException("PreparedStatement", "clearParameters");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType)
/*     */     throws SQLException
/*     */   {
/* 192 */     throw new NotImplementedException("PreparedStatement", "setObject");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x)
/*     */     throws SQLException
/*     */   {
/* 199 */     throw new NotImplementedException("PreparedStatement", "setObject");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean execute()
/*     */     throws SQLException
/*     */   {
/* 206 */     throw new NotImplementedException("PreparedStatement", "execute");
/*     */   }
/*     */   
/*     */ 
/*     */   public void addBatch()
/*     */     throws SQLException
/*     */   {
/* 213 */     throw new NotImplementedException("PreparedStatement", "addBatch");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/* 220 */     throw new NotImplementedException("PreparedStatement", "setCharacterStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRef(int parameterIndex, Ref x)
/*     */     throws SQLException
/*     */   {
/* 227 */     throw new SQLFeatureNotSupportedException("setRef");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBlob(int parameterIndex, Blob x)
/*     */     throws SQLException
/*     */   {
/* 234 */     throw new SQLFeatureNotSupportedException("setBlob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClob(int parameterIndex, Clob x)
/*     */     throws SQLException
/*     */   {
/* 241 */     throw new SQLFeatureNotSupportedException("setClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setArray(int parameterIndex, Array x)
/*     */     throws SQLException
/*     */   {
/* 248 */     throw new SQLFeatureNotSupportedException("setArray");
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSetMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/* 255 */     throw new SQLFeatureNotSupportedException("getMetaData");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/* 262 */     throw new NotImplementedException("PreparedStatement", "setDate");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/* 269 */     throw new NotImplementedException("PreparedStatement", "setTime");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*     */     throws SQLException
/*     */   {
/* 276 */     throw new NotImplementedException("PreparedStatement", "setTimestamp");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*     */     throws SQLException
/*     */   {
/* 283 */     throw new NotImplementedException("PreparedStatement", "setNull");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setURL(int parameterIndex, URL x)
/*     */     throws SQLException
/*     */   {
/* 290 */     throw new SQLFeatureNotSupportedException("setURL");
/*     */   }
/*     */   
/*     */ 
/*     */   public ParameterMetaData getParameterMetaData()
/*     */     throws SQLException
/*     */   {
/* 297 */     throw new NotImplementedException("PreparedStatement", "getParameterMetaData");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRowId(int parameterIndex, RowId x)
/*     */     throws SQLException
/*     */   {
/* 304 */     throw new SQLFeatureNotSupportedException("setRowId");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNString(int parameterIndex, String value)
/*     */     throws SQLException
/*     */   {
/* 311 */     throw new SQLFeatureNotSupportedException("setNString");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length)
/*     */     throws SQLException
/*     */   {
/* 318 */     throw new SQLFeatureNotSupportedException("setNCharacterStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNClob(int parameterIndex, NClob value)
/*     */     throws SQLException
/*     */   {
/* 325 */     throw new SQLFeatureNotSupportedException("setNClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/* 332 */     throw new SQLFeatureNotSupportedException("setClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length)
/*     */     throws SQLException
/*     */   {
/* 339 */     throw new SQLFeatureNotSupportedException("setBlob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/* 346 */     throw new SQLFeatureNotSupportedException("setNClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject)
/*     */     throws SQLException
/*     */   {
/* 353 */     throw new SQLFeatureNotSupportedException("setSQLXML");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 360 */     throw new SQLFeatureNotSupportedException("setObject");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length)
/*     */     throws SQLException
/*     */   {
/* 367 */     throw new NotImplementedException("PreparedStatement", "setAsciiStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length)
/*     */     throws SQLException
/*     */   {
/* 374 */     throw new NotImplementedException("PreparedStatement", "setBinaryStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/* 381 */     throw new NotImplementedException("PreparedStatement", "setCharacterStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAsciiStream(int parameterIndex, InputStream x)
/*     */     throws SQLException
/*     */   {
/* 388 */     throw new SQLFeatureNotSupportedException("setAsciiStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBinaryStream(int parameterIndex, InputStream x)
/*     */     throws SQLException
/*     */   {
/* 395 */     throw new SQLFeatureNotSupportedException("setBinaryStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setCharacterStream(int parameterIndex, Reader reader)
/*     */     throws SQLException
/*     */   {
/* 402 */     throw new SQLFeatureNotSupportedException("setCharacterStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader value)
/*     */     throws SQLException
/*     */   {
/* 409 */     throw new SQLFeatureNotSupportedException("setNCharacterStream");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClob(int parameterIndex, Reader reader)
/*     */     throws SQLException
/*     */   {
/* 416 */     throw new SQLFeatureNotSupportedException("setClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setBlob(int parameterIndex, InputStream inputStream)
/*     */     throws SQLException
/*     */   {
/* 423 */     throw new SQLFeatureNotSupportedException("setBlob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader)
/*     */     throws SQLException
/*     */   {
/* 430 */     throw new SQLFeatureNotSupportedException("setNClob");
/*     */   }
/*     */   
/*     */ 
/*     */   public void addBatch(String sql)
/*     */     throws SQLException
/*     */   {
/* 437 */     throw new SQLException("This method cannot be called on PreparedStatement");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */