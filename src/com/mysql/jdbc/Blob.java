/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Blob
/*     */   implements java.sql.Blob, OutputStreamWatcher
/*     */ {
/*  48 */   private byte[] binaryData = null;
/*  49 */   private boolean isClosed = false;
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */ 
/*     */   Blob(ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  56 */     setBinaryData(Constants.EMPTY_BYTE_ARRAY);
/*  57 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Blob(byte[] data, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  66 */     setBinaryData(data);
/*  67 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet)
/*     */   {
/*  78 */     setBinaryData(data);
/*     */   }
/*     */   
/*     */   private synchronized byte[] getBinaryData() {
/*  82 */     return this.binaryData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/*  94 */     checkClosed();
/*     */     
/*  96 */     return new ByteArrayInputStream(getBinaryData());
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
/*     */   public synchronized byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 114 */     checkClosed();
/*     */     
/* 116 */     if (pos < 1L) {
/* 117 */       throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 120 */     pos -= 1L;
/*     */     
/* 122 */     if (pos > this.binaryData.length) {
/* 123 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 127 */     if (pos + length > this.binaryData.length) {
/* 128 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 132 */     byte[] newData = new byte[length];
/* 133 */     System.arraycopy(getBinaryData(), (int)pos, newData, 0, length);
/*     */     
/* 135 */     return newData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized long length()
/*     */     throws SQLException
/*     */   {
/* 148 */     checkClosed();
/*     */     
/* 150 */     return getBinaryData().length;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 157 */     throw SQLError.createSQLException("Not implemented", this.exceptionInterceptor);
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
/*     */   public synchronized long position(java.sql.Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 175 */     checkClosed();
/*     */     
/* 177 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */   
/*     */   private synchronized void setBinaryData(byte[] newBinaryData) {
/* 181 */     this.binaryData = newBinaryData;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 188 */     checkClosed();
/*     */     
/* 190 */     if (indexToWriteAt < 1L) {
/* 191 */       throw SQLError.createSQLException(Messages.getString("Blob.0"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 194 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 195 */     bytesOut.setWatcher(this);
/*     */     
/* 197 */     if (indexToWriteAt > 0L) {
/* 198 */       bytesOut.write(this.binaryData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */     
/* 201 */     return bytesOut;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 208 */     checkClosed();
/*     */     
/* 210 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 217 */     checkClosed();
/*     */     
/* 219 */     OutputStream bytesOut = setBinaryStream(writeAt);
/*     */     try
/*     */     {
/* 222 */       bytesOut.write(bytes, offset, length);
/*     */     } catch (IOException ioEx) {
/* 224 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("Blob.1"), "S1000", this.exceptionInterceptor);
/* 225 */       sqlEx.initCause(ioEx);
/*     */       
/* 227 */       throw sqlEx;
/*     */     } finally {
/*     */       try {
/* 230 */         bytesOut.close();
/*     */       }
/*     */       catch (IOException doNothing) {}
/*     */     }
/*     */     
/*     */ 
/* 236 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void streamClosed(byte[] byteData)
/*     */   {
/* 243 */     this.binaryData = byteData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void streamClosed(WatchableOutputStream out)
/*     */   {
/* 250 */     int streamSize = out.size();
/*     */     
/* 252 */     if (streamSize < this.binaryData.length) {
/* 253 */       out.write(this.binaryData, streamSize, this.binaryData.length - streamSize);
/*     */     }
/*     */     
/* 256 */     this.binaryData = out.toByteArray();
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
/*     */   public synchronized void truncate(long len)
/*     */     throws SQLException
/*     */   {
/* 276 */     checkClosed();
/*     */     
/* 278 */     if (len < 0L) {
/* 279 */       throw SQLError.createSQLException("\"len\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 282 */     if (len > this.binaryData.length) {
/* 283 */       throw SQLError.createSQLException("\"len\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 289 */     byte[] newData = new byte[(int)len];
/* 290 */     System.arraycopy(getBinaryData(), 0, newData, 0, (int)len);
/* 291 */     this.binaryData = newData;
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
/*     */   public synchronized void free()
/*     */     throws SQLException
/*     */   {
/* 312 */     this.binaryData = null;
/* 313 */     this.isClosed = true;
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
/*     */   public synchronized InputStream getBinaryStream(long pos, long length)
/*     */     throws SQLException
/*     */   {
/* 337 */     checkClosed();
/*     */     
/* 339 */     if (pos < 1L) {
/* 340 */       throw SQLError.createSQLException("\"pos\" argument can not be < 1.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 343 */     pos -= 1L;
/*     */     
/* 345 */     if (pos > this.binaryData.length) {
/* 346 */       throw SQLError.createSQLException("\"pos\" argument can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 350 */     if (pos + length > this.binaryData.length) {
/* 351 */       throw SQLError.createSQLException("\"pos\" + \"length\" arguments can not be larger than the BLOB's length.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 355 */     return new ByteArrayInputStream(getBinaryData(), (int)pos, (int)length);
/*     */   }
/*     */   
/*     */   private synchronized void checkClosed() throws SQLException {
/* 359 */     if (this.isClosed) {
/* 360 */       throw SQLError.createSQLException("Invalid operation on closed BLOB", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Blob.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */