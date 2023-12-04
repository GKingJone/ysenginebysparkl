/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
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
/*     */ public class JDBC4ResultSet
/*     */   extends ResultSetImpl
/*     */ {
/*     */   public JDBC4ResultSet(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
/*     */   {
/*  47 */     super(updateCount, updateID, conn, creatorStmt);
/*     */   }
/*     */   
/*     */   public JDBC4ResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
/*  51 */     super(catalog, fields, tuples, conn, creatorStmt);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Reader getNCharacterStream(int columnIndex)
/*     */     throws SQLException
/*     */   {
/*  70 */     checkColumnBounds(columnIndex);
/*     */     
/*  72 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/*  73 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/*  74 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*  76 */     return getCharacterStream(columnIndex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Reader getNCharacterStream(String columnName)
/*     */     throws SQLException
/*     */   {
/*  95 */     return getNCharacterStream(findColumn(columnName));
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
/*     */   public NClob getNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 110 */     checkColumnBounds(columnIndex);
/*     */     
/* 112 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 113 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 114 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/* 116 */     if (!this.isBinaryEncoded) {
/* 117 */       String asString = getStringForNClob(columnIndex);
/*     */       
/* 119 */       if (asString == null) {
/* 120 */         return null;
/*     */       }
/*     */       
/* 123 */       return new JDBC4NClob(asString, getExceptionInterceptor());
/*     */     }
/*     */     
/* 126 */     return getNativeNClob(columnIndex);
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
/*     */   public NClob getNClob(String columnName)
/*     */     throws SQLException
/*     */   {
/* 141 */     return getNClob(findColumn(columnName));
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
/*     */   protected NClob getNativeNClob(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 156 */     String stringVal = getStringForNClob(columnIndex);
/*     */     
/* 158 */     if (stringVal == null) {
/* 159 */       return null;
/*     */     }
/*     */     
/* 162 */     return getNClobFromString(stringVal, columnIndex);
/*     */   }
/*     */   
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 166 */     String asString = null;
/*     */     
/* 168 */     String forcedEncoding = "UTF-8";
/*     */     try
/*     */     {
/* 171 */       byte[] asBytes = null;
/*     */       
/* 173 */       if (!this.isBinaryEncoded) {
/* 174 */         asBytes = getBytes(columnIndex);
/*     */       } else {
/* 176 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       }
/*     */       
/* 179 */       if (asBytes != null) {
/* 180 */         asString = new String(asBytes, forcedEncoding);
/*     */       }
/*     */     } catch (UnsupportedEncodingException uee) {
/* 183 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", 
/* 184 */         getExceptionInterceptor());
/*     */     }
/*     */     
/* 187 */     return asString;
/*     */   }
/*     */   
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
/* 191 */     return new JDBC4NClob(stringVal, getExceptionInterceptor());
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
/*     */ 
/*     */ 
/*     */   public String getNString(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 208 */     checkColumnBounds(columnIndex);
/*     */     
/* 210 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 211 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 212 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/* 214 */     return getString(columnIndex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getNString(String columnName)
/*     */     throws SQLException
/*     */   {
/* 232 */     return getNString(findColumn(columnName));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, int length)
/*     */     throws SQLException
/*     */   {
/* 254 */     throw new NotUpdatable();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateNCharacterStream(String columnName, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/* 275 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateNClob(String columnName, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 282 */     updateNClob(findColumn(columnName), nClob);
/*     */   }
/*     */   
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException {
/* 286 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateRowId(String columnName, RowId x) throws SQLException {
/* 290 */     updateRowId(findColumn(columnName), x);
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 294 */     throw SQLError.createSQLFeatureNotSupportedException();
/*     */   }
/*     */   
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 298 */     throw SQLError.createSQLFeatureNotSupportedException();
/*     */   }
/*     */   
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 302 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 306 */     checkColumnBounds(columnIndex);
/*     */     
/* 308 */     return new JDBC4MysqlSQLXML(this, columnIndex, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 312 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/* 316 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
/*     */   {
/* 321 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/* 326 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
/*     */   {
/* 331 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
/* 335 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
/*     */   {
/* 340 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
/* 344 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
/*     */   {
/* 349 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
/* 353 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 357 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/* 361 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
/*     */   {
/* 366 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 370 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 375 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
/* 379 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 384 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException {
/* 388 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 393 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
/* 397 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 402 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
/* 406 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 411 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
/*     */   {
/* 416 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 421 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
/* 425 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader) throws SQLException
/*     */   {
/* 430 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException
/*     */   {
/* 435 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/* 440 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 444 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNString(int columnIndex, String nString) throws SQLException {
/* 448 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNString(String columnLabel, String nString) throws SQLException
/*     */   {
/* 453 */     updateNString(findColumn(columnLabel), nString);
/*     */   }
/*     */   
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 457 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
/*     */   {
/* 462 */     updateSQLXML(findColumn(columnLabel), xmlObject);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 484 */     checkClosed();
/*     */     
/*     */ 
/* 487 */     return iface.isInstance(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 509 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/* 511 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public <T> T getObject(int columnIndex, Class<T> type) throws SQLException
/*     */   {
/* 517 */     if (type == null) {
/* 518 */       throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*     */     }
/*     */     
/* 521 */     if (type.equals(Struct.class))
/* 522 */       throw new SQLFeatureNotSupportedException();
/* 523 */     if (type.equals(RowId.class))
/* 524 */       return getRowId(columnIndex);
/* 525 */     if (type.equals(NClob.class))
/* 526 */       return getNClob(columnIndex);
/* 527 */     if (type.equals(SQLXML.class)) {
/* 528 */       return getSQLXML(columnIndex);
/*     */     }
/*     */     
/* 531 */     return (T)super.getObject(columnIndex, type);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4ResultSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */