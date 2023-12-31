/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4UpdatableResultSet
/*     */   extends UpdatableResultSet
/*     */ {
/*     */   public JDBC4UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*     */     throws SQLException
/*     */   {
/*  44 */     super(catalog, fields, tuples, conn, creatorStmt);
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
/*  48 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/*  53 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
/*     */   {
/*  58 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
/*     */   {
/*  63 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
/*     */   {
/*  68 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
/*  72 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
/*     */   {
/*  77 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
/*     */   {
/*  82 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader) throws SQLException
/*     */   {
/*  87 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/*  92 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
/*     */   {
/*  97 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
/*     */   {
/* 102 */     updateNCharacterStream(columnIndex, x, (int)length);
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader) throws SQLException
/*     */   {
/* 107 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
/*     */   {
/* 112 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
/* 116 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateRowId(int columnIndex, RowId x) throws SQLException
/*     */   {
/* 121 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
/* 125 */     updateAsciiStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 129 */     updateAsciiStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
/* 133 */     updateBinaryStream(findColumn(columnLabel), x);
/*     */   }
/*     */   
/*     */   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
/* 137 */     updateBinaryStream(findColumn(columnLabel), x, length);
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
/* 141 */     updateBlob(findColumn(columnLabel), inputStream);
/*     */   }
/*     */   
/*     */   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
/* 145 */     updateBlob(findColumn(columnLabel), inputStream, length);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 149 */     updateCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
/* 153 */     updateCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader) throws SQLException {
/* 157 */     updateClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
/* 161 */     updateClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
/* 165 */     updateNCharacterStream(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 170 */     updateNCharacterStream(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
/* 174 */     updateNClob(findColumn(columnLabel), reader);
/*     */   }
/*     */   
/*     */   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
/*     */   {
/* 179 */     updateNClob(findColumn(columnLabel), reader, length);
/*     */   }
/*     */   
/*     */   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
/* 183 */     updateSQLXML(findColumn(columnLabel), xmlObject);
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
/*     */   public synchronized void updateNCharacterStream(int columnIndex, Reader x, int length)
/*     */     throws SQLException
/*     */   {
/* 205 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 206 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 207 */       throw new SQLException("Can not call updateNCharacterStream() when field's character set isn't UTF-8");
/*     */     }
/*     */     
/* 210 */     if (!this.onInsertRow) {
/* 211 */       if (!this.doingUpdates) {
/* 212 */         this.doingUpdates = true;
/* 213 */         syncUpdate();
/*     */       }
/*     */       
/* 216 */       ((JDBC4PreparedStatement)this.updater).setNCharacterStream(columnIndex, x, length);
/*     */     } else {
/* 218 */       ((JDBC4PreparedStatement)this.inserter).setNCharacterStream(columnIndex, x, length);
/*     */       
/* 220 */       if (x == null) {
/* 221 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*     */       } else {
/* 223 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*     */       }
/*     */     }
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
/*     */   public synchronized void updateNCharacterStream(String columnName, Reader reader, int length)
/*     */     throws SQLException
/*     */   {
/* 246 */     updateNCharacterStream(findColumn(columnName), reader, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateNClob(int columnIndex, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 253 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 254 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 255 */       throw new SQLException("Can not call updateNClob() when field's character set isn't UTF-8");
/*     */     }
/*     */     
/* 258 */     if (nClob == null) {
/* 259 */       updateNull(columnIndex);
/*     */     } else {
/* 261 */       updateNCharacterStream(columnIndex, nClob.getCharacterStream(), (int)nClob.length());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateNClob(String columnName, NClob nClob)
/*     */     throws SQLException
/*     */   {
/* 269 */     updateNClob(findColumn(columnName), nClob);
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
/*     */   public synchronized void updateNString(int columnIndex, String x)
/*     */     throws SQLException
/*     */   {
/* 287 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 288 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 289 */       throw new SQLException("Can not call updateNString() when field's character set isn't UTF-8");
/*     */     }
/*     */     
/* 292 */     if (!this.onInsertRow) {
/* 293 */       if (!this.doingUpdates) {
/* 294 */         this.doingUpdates = true;
/* 295 */         syncUpdate();
/*     */       }
/*     */       
/* 298 */       ((JDBC4PreparedStatement)this.updater).setNString(columnIndex, x);
/*     */     } else {
/* 300 */       ((JDBC4PreparedStatement)this.inserter).setNString(columnIndex, x);
/*     */       
/* 302 */       if (x == null) {
/* 303 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*     */       } else {
/* 305 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, fieldEncoding, this.connection.getServerCharset(), this.connection
/* 306 */           .parserKnowsUnicode(), getExceptionInterceptor()));
/*     */       }
/*     */     }
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
/*     */   public synchronized void updateNString(String columnName, String x)
/*     */     throws SQLException
/*     */   {
/* 326 */     updateNString(findColumn(columnName), x);
/*     */   }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 330 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/* 345 */     String stringVal = getStringForNClob(columnIndex);
/*     */     
/* 347 */     if (stringVal == null) {
/* 348 */       return null;
/*     */     }
/*     */     
/* 351 */     return getNClobFromString(stringVal, columnIndex);
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
/* 370 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/* 371 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 372 */       throw new SQLException("Can not call getNCharacterStream() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/* 375 */     return getCharacterStream(columnIndex);
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
/* 394 */     return getNCharacterStream(findColumn(columnName));
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
/* 409 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/*     */     
/* 411 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 412 */       throw new SQLException("Can not call getNClob() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/* 415 */     if (!this.isBinaryEncoded) {
/* 416 */       String asString = getStringForNClob(columnIndex);
/*     */       
/* 418 */       if (asString == null) {
/* 419 */         return null;
/*     */       }
/*     */       
/* 422 */       return new JDBC4NClob(asString, getExceptionInterceptor());
/*     */     }
/*     */     
/* 425 */     return getNativeNClob(columnIndex);
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
/* 440 */     return getNClob(findColumn(columnName));
/*     */   }
/*     */   
/*     */   private final NClob getNClobFromString(String stringVal, int columnIndex) throws SQLException {
/* 444 */     return new JDBC4NClob(stringVal, getExceptionInterceptor());
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
/* 461 */     String fieldEncoding = this.fields[(columnIndex - 1)].getEncoding();
/*     */     
/* 463 */     if ((fieldEncoding == null) || (!fieldEncoding.equals("UTF-8"))) {
/* 464 */       throw new SQLException("Can not call getNString() when field's charset isn't UTF-8");
/*     */     }
/*     */     
/* 467 */     return getString(columnIndex);
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
/* 485 */     return getNString(findColumn(columnName));
/*     */   }
/*     */   
/*     */   public RowId getRowId(int columnIndex) throws SQLException {
/* 489 */     throw SQLError.createSQLFeatureNotSupportedException();
/*     */   }
/*     */   
/*     */   public RowId getRowId(String columnLabel) throws SQLException {
/* 493 */     return getRowId(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int columnIndex) throws SQLException {
/* 497 */     return new JDBC4MysqlSQLXML(this, columnIndex, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String columnLabel) throws SQLException {
/* 501 */     return getSQLXML(findColumn(columnLabel));
/*     */   }
/*     */   
/*     */   private String getStringForNClob(int columnIndex) throws SQLException {
/* 505 */     String asString = null;
/*     */     
/* 507 */     String forcedEncoding = "UTF-8";
/*     */     try
/*     */     {
/* 510 */       byte[] asBytes = null;
/*     */       
/* 512 */       if (!this.isBinaryEncoded) {
/* 513 */         asBytes = getBytes(columnIndex);
/*     */       } else {
/* 515 */         asBytes = getNativeBytes(columnIndex, true);
/*     */       }
/*     */       
/* 518 */       if (asBytes != null) {
/* 519 */         asString = new String(asBytes, forcedEncoding);
/*     */       }
/*     */     } catch (UnsupportedEncodingException uee) {
/* 522 */       throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", 
/* 523 */         getExceptionInterceptor());
/*     */     }
/*     */     
/* 526 */     return asString;
/*     */   }
/*     */   
/*     */   public synchronized boolean isClosed() throws SQLException {
/* 530 */     return this.isClosed;
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
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 554 */     checkClosed();
/*     */     
/*     */ 
/* 557 */     return iface.isInstance(this);
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
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 582 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/* 584 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4UpdatableResultSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */