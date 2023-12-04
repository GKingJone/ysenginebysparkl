/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  43 */   private List<String> primaryKeyColumns = null;
/*     */   
/*  45 */   private List<String> primaryKeyValues = null;
/*     */   
/*     */ 
/*     */   private ResultSetImpl creatorResultSet;
/*     */   
/*  50 */   private String blobColumnName = null;
/*     */   
/*  52 */   private String tableName = null;
/*     */   
/*  54 */   private int numColsInResultSet = 0;
/*     */   
/*  56 */   private int numPrimaryKeys = 0;
/*     */   
/*     */   private String quotedId;
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */ 
/*     */   BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  66 */     this.exceptionInterceptor = exceptionInterceptor;
/*  67 */     this.creatorResultSet = creatorResultSetToSet;
/*     */     
/*  69 */     this.numColsInResultSet = this.creatorResultSet.fields.length;
/*  70 */     this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
/*     */     
/*  72 */     if (this.numColsInResultSet > 1) {
/*  73 */       this.primaryKeyColumns = new ArrayList();
/*  74 */       this.primaryKeyValues = new ArrayList();
/*     */       
/*  76 */       for (int i = 0; i < this.numColsInResultSet; i++) {
/*  77 */         if (this.creatorResultSet.fields[i].isPrimaryKey()) {
/*  78 */           StringBuilder keyName = new StringBuilder();
/*  79 */           keyName.append(this.quotedId);
/*     */           
/*  81 */           String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
/*     */           
/*  83 */           if ((originalColumnName != null) && (originalColumnName.length() > 0)) {
/*  84 */             keyName.append(originalColumnName);
/*     */           } else {
/*  86 */             keyName.append(this.creatorResultSet.fields[i].getName());
/*     */           }
/*     */           
/*  89 */           keyName.append(this.quotedId);
/*     */           
/*  91 */           this.primaryKeyColumns.add(keyName.toString());
/*  92 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         }
/*     */       }
/*     */     } else {
/*  96 */       notEnoughInformationInQuery();
/*     */     }
/*     */     
/*  99 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */     
/* 101 */     if (this.numPrimaryKeys == 0) {
/* 102 */       notEnoughInformationInQuery();
/*     */     }
/*     */     
/* 105 */     if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
/* 106 */       StringBuilder tableNameBuffer = new StringBuilder();
/*     */       
/* 108 */       String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
/*     */       
/* 110 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/* 111 */         tableNameBuffer.append(this.quotedId);
/* 112 */         tableNameBuffer.append(databaseName);
/* 113 */         tableNameBuffer.append(this.quotedId);
/* 114 */         tableNameBuffer.append('.');
/*     */       }
/*     */       
/* 117 */       tableNameBuffer.append(this.quotedId);
/* 118 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getOriginalTableName());
/* 119 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 121 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 123 */       StringBuilder tableNameBuffer = new StringBuilder();
/*     */       
/* 125 */       tableNameBuffer.append(this.quotedId);
/* 126 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getTableName());
/* 127 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 129 */       this.tableName = tableNameBuffer.toString();
/*     */     }
/*     */     
/* 132 */     this.blobColumnName = (this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId);
/*     */   }
/*     */   
/*     */   private void notEnoughInformationInQuery() throws SQLException {
/* 136 */     throw SQLError.createSQLException("Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", "S1000", this.exceptionInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 144 */     throw SQLError.createSQLFeatureNotSupportedException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 157 */     return new BufferedInputStream(new LocatorInputStream(), this.creatorResultSet.connection.getLocatorFetchBufferSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 164 */     PreparedStatement pStmt = null;
/*     */     
/* 166 */     if (offset + length > bytes.length) {
/* 167 */       length = bytes.length - offset;
/*     */     }
/*     */     
/* 170 */     byte[] bytesToWrite = new byte[length];
/* 171 */     System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */     
/*     */ 
/* 174 */     StringBuilder query = new StringBuilder("UPDATE ");
/* 175 */     query.append(this.tableName);
/* 176 */     query.append(" SET ");
/* 177 */     query.append(this.blobColumnName);
/* 178 */     query.append(" = INSERT(");
/* 179 */     query.append(this.blobColumnName);
/* 180 */     query.append(", ");
/* 181 */     query.append(writeAt);
/* 182 */     query.append(", ");
/* 183 */     query.append(length);
/* 184 */     query.append(", ?) WHERE ");
/*     */     
/* 186 */     query.append((String)this.primaryKeyColumns.get(0));
/* 187 */     query.append(" = ?");
/*     */     
/* 189 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 190 */       query.append(" AND ");
/* 191 */       query.append((String)this.primaryKeyColumns.get(i));
/* 192 */       query.append(" = ?");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 197 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */       
/* 199 */       pStmt.setBytes(1, bytesToWrite);
/*     */       
/* 201 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 202 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 205 */       int rowsUpdated = pStmt.executeUpdate();
/*     */       
/* 207 */       if (rowsUpdated != 1) {
/* 208 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } finally {
/* 211 */       if (pStmt != null) {
/*     */         try {
/* 213 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 218 */         pStmt = null;
/*     */       }
/*     */     }
/*     */     
/* 222 */     return (int)length();
/*     */   }
/*     */   
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 229 */     return setBytes(writeAt, bytes, 0, bytes.length);
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
/*     */   public byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 247 */     PreparedStatement pStmt = null;
/*     */     
/*     */     try
/*     */     {
/* 251 */       pStmt = createGetBytesStatement();
/*     */       
/* 253 */       return getBytesInternal(pStmt, pos, length);
/*     */     } finally {
/* 255 */       if (pStmt != null) {
/*     */         try {
/* 257 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 262 */         pStmt = null;
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
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 277 */     ResultSet blobRs = null;
/* 278 */     PreparedStatement pStmt = null;
/*     */     
/*     */ 
/* 281 */     StringBuilder query = new StringBuilder("SELECT LENGTH(");
/* 282 */     query.append(this.blobColumnName);
/* 283 */     query.append(") FROM ");
/* 284 */     query.append(this.tableName);
/* 285 */     query.append(" WHERE ");
/*     */     
/* 287 */     query.append((String)this.primaryKeyColumns.get(0));
/* 288 */     query.append(" = ?");
/*     */     
/* 290 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 291 */       query.append(" AND ");
/* 292 */       query.append((String)this.primaryKeyColumns.get(i));
/* 293 */       query.append(" = ?");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 298 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */       
/* 300 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 301 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 304 */       blobRs = pStmt.executeQuery();
/*     */       
/* 306 */       if (blobRs.next()) {
/* 307 */         return blobRs.getLong(1);
/*     */       }
/*     */       
/* 310 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     } finally {
/* 312 */       if (blobRs != null) {
/*     */         try {
/* 314 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 319 */         blobRs = null;
/*     */       }
/*     */       
/* 322 */       if (pStmt != null) {
/*     */         try {
/* 324 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 329 */         pStmt = null;
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
/*     */   public long position(Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 349 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */   
/*     */ 
/*     */   public long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 356 */     ResultSet blobRs = null;
/* 357 */     PreparedStatement pStmt = null;
/*     */     
/*     */ 
/* 360 */     StringBuilder query = new StringBuilder("SELECT LOCATE(");
/* 361 */     query.append("?, ");
/* 362 */     query.append(this.blobColumnName);
/* 363 */     query.append(", ");
/* 364 */     query.append(start);
/* 365 */     query.append(") FROM ");
/* 366 */     query.append(this.tableName);
/* 367 */     query.append(" WHERE ");
/*     */     
/* 369 */     query.append((String)this.primaryKeyColumns.get(0));
/* 370 */     query.append(" = ?");
/*     */     
/* 372 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 373 */       query.append(" AND ");
/* 374 */       query.append((String)this.primaryKeyColumns.get(i));
/* 375 */       query.append(" = ?");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 380 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/* 381 */       pStmt.setBytes(1, pattern);
/*     */       
/* 383 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 384 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 387 */       blobRs = pStmt.executeQuery();
/*     */       
/* 389 */       if (blobRs.next()) {
/* 390 */         return blobRs.getLong(1);
/*     */       }
/*     */       
/* 393 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     } finally {
/* 395 */       if (blobRs != null) {
/*     */         try {
/* 397 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 402 */         blobRs = null;
/*     */       }
/*     */       
/* 405 */       if (pStmt != null) {
/*     */         try {
/* 407 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 412 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 421 */     PreparedStatement pStmt = null;
/*     */     
/*     */ 
/* 424 */     StringBuilder query = new StringBuilder("UPDATE ");
/* 425 */     query.append(this.tableName);
/* 426 */     query.append(" SET ");
/* 427 */     query.append(this.blobColumnName);
/* 428 */     query.append(" = LEFT(");
/* 429 */     query.append(this.blobColumnName);
/* 430 */     query.append(", ");
/* 431 */     query.append(length);
/* 432 */     query.append(") WHERE ");
/*     */     
/* 434 */     query.append((String)this.primaryKeyColumns.get(0));
/* 435 */     query.append(" = ?");
/*     */     
/* 437 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 438 */       query.append(" AND ");
/* 439 */       query.append((String)this.primaryKeyColumns.get(i));
/* 440 */       query.append(" = ?");
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 445 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */       
/* 447 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 448 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 451 */       int rowsUpdated = pStmt.executeUpdate();
/*     */       
/* 453 */       if (rowsUpdated != 1) {
/* 454 */         throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } finally {
/* 457 */       if (pStmt != null) {
/*     */         try {
/* 459 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 464 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   PreparedStatement createGetBytesStatement() throws SQLException {
/* 470 */     StringBuilder query = new StringBuilder("SELECT SUBSTRING(");
/*     */     
/* 472 */     query.append(this.blobColumnName);
/* 473 */     query.append(", ");
/* 474 */     query.append("?");
/* 475 */     query.append(", ");
/* 476 */     query.append("?");
/* 477 */     query.append(") FROM ");
/* 478 */     query.append(this.tableName);
/* 479 */     query.append(" WHERE ");
/*     */     
/* 481 */     query.append((String)this.primaryKeyColumns.get(0));
/* 482 */     query.append(" = ?");
/*     */     
/* 484 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 485 */       query.append(" AND ");
/* 486 */       query.append((String)this.primaryKeyColumns.get(i));
/* 487 */       query.append(" = ?");
/*     */     }
/*     */     
/* 490 */     return this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */   }
/*     */   
/*     */   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length) throws SQLException
/*     */   {
/* 495 */     ResultSet blobRs = null;
/*     */     
/*     */     try
/*     */     {
/* 499 */       pStmt.setLong(1, pos);
/* 500 */       pStmt.setInt(2, length);
/*     */       
/* 502 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 503 */         pStmt.setString(i + 3, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 506 */       blobRs = pStmt.executeQuery();
/*     */       
/* 508 */       if (blobRs.next()) {
/* 509 */         return ((ResultSetImpl)blobRs).getBytes(1, true);
/*     */       }
/*     */       
/* 512 */       throw SQLError.createSQLException("BLOB data not found! Did primary keys change?", "S1000", this.exceptionInterceptor);
/*     */     } finally {
/* 514 */       if (blobRs != null) {
/*     */         try {
/* 516 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx) {}
/*     */         
/*     */ 
/* 521 */         blobRs = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class LocatorInputStream extends InputStream {
/* 527 */     long currentPositionInBlob = 0L;
/*     */     
/* 529 */     long length = 0L;
/*     */     
/* 531 */     PreparedStatement pStmt = null;
/*     */     
/*     */     LocatorInputStream() throws SQLException {
/* 534 */       this.length = BlobFromLocator.this.length();
/* 535 */       this.pStmt = BlobFromLocator.this.createGetBytesStatement();
/*     */     }
/*     */     
/*     */     LocatorInputStream(long pos, long len) throws SQLException
/*     */     {
/* 540 */       this.length = (pos + len);
/* 541 */       this.currentPositionInBlob = pos;
/* 542 */       long blobLength = BlobFromLocator.this.length();
/*     */       
/* 544 */       if (pos + len > blobLength) {
/* 545 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamLength", new Object[] { Long.valueOf(blobLength), Long.valueOf(pos), Long.valueOf(len) }), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 550 */       if (pos < 1L) {
/* 551 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */       
/*     */ 
/* 555 */       if (pos > blobLength) {
/* 556 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this.exceptionInterceptor);
/*     */       }
/*     */     }
/*     */     
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 563 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 564 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 568 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
/*     */         
/* 570 */         if (asBytes == null) {
/* 571 */           return -1;
/*     */         }
/*     */         
/* 574 */         return asBytes[0];
/*     */       } catch (SQLException sqlEx) {
/* 576 */         throw new IOException(sqlEx.toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 587 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 588 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 592 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
/*     */         
/* 594 */         if (asBytes == null) {
/* 595 */           return -1;
/*     */         }
/*     */         
/* 598 */         System.arraycopy(asBytes, 0, b, off, asBytes.length);
/*     */         
/* 600 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 602 */         return asBytes.length;
/*     */       } catch (SQLException sqlEx) {
/* 604 */         throw new IOException(sqlEx.toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 615 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 616 */         return -1;
/*     */       }
/*     */       try
/*     */       {
/* 620 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
/*     */         
/* 622 */         if (asBytes == null) {
/* 623 */           return -1;
/*     */         }
/*     */         
/* 626 */         System.arraycopy(asBytes, 0, b, 0, asBytes.length);
/*     */         
/* 628 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 630 */         return asBytes.length;
/*     */       } catch (SQLException sqlEx) {
/* 632 */         throw new IOException(sqlEx.toString());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 643 */       if (this.pStmt != null) {
/*     */         try {
/* 645 */           this.pStmt.close();
/*     */         } catch (SQLException sqlEx) {
/* 647 */           throw new IOException(sqlEx.toString());
/*     */         }
/*     */       }
/*     */       
/* 651 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/* 656 */     this.creatorResultSet = null;
/* 657 */     this.primaryKeyColumns = null;
/* 658 */     this.primaryKeyValues = null;
/*     */   }
/*     */   
/*     */   public InputStream getBinaryStream(long pos, long length) throws SQLException {
/* 662 */     return new LocatorInputStream(pos, length);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\BlobFromLocator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */