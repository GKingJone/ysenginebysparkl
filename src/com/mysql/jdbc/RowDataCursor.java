/*     */ package com.mysql.jdbc;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RowDataCursor
/*     */   implements RowData
/*     */ {
/*     */   private static final int BEFORE_START_OF_ROWS = -1;
/*     */   private List<ResultSetRow> fetchedRows;
/*  46 */   private int currentPositionInEntireResult = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  52 */   private int currentPositionInFetchedRows = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ResultSetImpl owner;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  62 */   private boolean lastRowFetched = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Field[] metadata;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private MysqlIO mysql;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long statementIdOnServer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ServerPreparedStatement prepStmt;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int SERVER_STATUS_LAST_ROW_SENT = 128;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  95 */   private boolean firstFetchCompleted = false;
/*     */   
/*  97 */   private boolean wasEmpty = false;
/*     */   
/*  99 */   private boolean useBufferRowExplicit = false;
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
/*     */   public RowDataCursor(MysqlIO ioChannel, ServerPreparedStatement creatingStatement, Field[] metadata)
/*     */   {
/* 112 */     this.currentPositionInEntireResult = -1;
/* 113 */     this.metadata = metadata;
/* 114 */     this.mysql = ioChannel;
/* 115 */     this.statementIdOnServer = creatingStatement.getServerStatementId();
/* 116 */     this.prepStmt = creatingStatement;
/* 117 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAfterLast()
/*     */   {
/* 125 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows > this.fetchedRows.size());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSetRow getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 138 */     notSupported();
/*     */     
/* 140 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 151 */     return this.currentPositionInEntireResult < 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 163 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 174 */     return this.currentPositionInEntireResult + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 186 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 197 */     return (isBeforeFirst()) && (isAfterLast());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 208 */     return this.currentPositionInEntireResult == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 219 */     return (this.lastRowFetched) && (this.currentPositionInFetchedRows == this.fetchedRows.size() - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRow(ResultSetRow row)
/*     */     throws SQLException
/*     */   {
/* 231 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 241 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 251 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 261 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 272 */     this.metadata = null;
/* 273 */     this.owner = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 285 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 286 */       return false;
/*     */     }
/*     */     
/* 289 */     if ((this.owner != null) && (this.owner.owningStatement != null)) {
/* 290 */       int maxRows = this.owner.owningStatement.maxRows;
/*     */       
/* 292 */       if ((maxRows != -1) && (this.currentPositionInEntireResult + 1 > maxRows)) {
/* 293 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 297 */     if (this.currentPositionInEntireResult != -1)
/*     */     {
/* 299 */       if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1)
/* 300 */         return true;
/* 301 */       if ((this.currentPositionInFetchedRows == this.fetchedRows.size()) && (this.lastRowFetched)) {
/* 302 */         return false;
/*     */       }
/*     */       
/* 305 */       fetchMoreRows();
/*     */       
/* 307 */       return this.fetchedRows.size() > 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 313 */     fetchMoreRows();
/*     */     
/* 315 */     return this.fetchedRows.size() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 327 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 338 */     if ((this.fetchedRows == null) && (this.currentPositionInEntireResult != -1)) {
/* 339 */       throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000", this.mysql.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/* 343 */     if (!hasNext()) {
/* 344 */       return null;
/*     */     }
/*     */     
/* 347 */     this.currentPositionInEntireResult += 1;
/* 348 */     this.currentPositionInFetchedRows += 1;
/*     */     
/*     */ 
/* 351 */     if ((this.fetchedRows != null) && (this.fetchedRows.size() == 0)) {
/* 352 */       return null;
/*     */     }
/*     */     
/* 355 */     if ((this.fetchedRows == null) || (this.currentPositionInFetchedRows > this.fetchedRows.size() - 1)) {
/* 356 */       fetchMoreRows();
/* 357 */       this.currentPositionInFetchedRows = 0;
/*     */     }
/*     */     
/* 360 */     ResultSetRow row = (ResultSetRow)this.fetchedRows.get(this.currentPositionInFetchedRows);
/*     */     
/* 362 */     row.setMetadata(this.metadata);
/*     */     
/* 364 */     return row;
/*     */   }
/*     */   
/*     */   private void fetchMoreRows()
/*     */     throws SQLException
/*     */   {
/* 370 */     if (this.lastRowFetched) {
/* 371 */       this.fetchedRows = new ArrayList(0);
/* 372 */       return;
/*     */     }
/*     */     
/* 375 */     synchronized (this.owner.connection.getConnectionMutex()) {
/* 376 */       boolean oldFirstFetchCompleted = this.firstFetchCompleted;
/*     */       
/* 378 */       if (!this.firstFetchCompleted) {
/* 379 */         this.firstFetchCompleted = true;
/*     */       }
/*     */       
/* 382 */       int numRowsToFetch = this.owner.getFetchSize();
/*     */       
/* 384 */       if (numRowsToFetch == 0) {
/* 385 */         numRowsToFetch = this.prepStmt.getFetchSize();
/*     */       }
/*     */       
/* 388 */       if (numRowsToFetch == Integer.MIN_VALUE)
/*     */       {
/*     */ 
/* 391 */         numRowsToFetch = 1;
/*     */       }
/*     */       
/* 394 */       this.fetchedRows = this.mysql.fetchRowsViaCursor(this.fetchedRows, this.statementIdOnServer, this.metadata, numRowsToFetch, this.useBufferRowExplicit);
/*     */       
/* 396 */       this.currentPositionInFetchedRows = -1;
/*     */       
/* 398 */       if ((this.mysql.getServerStatus() & 0x80) != 0) {
/* 399 */         this.lastRowFetched = true;
/*     */         
/* 401 */         if ((!oldFirstFetchCompleted) && (this.fetchedRows.size() == 0)) {
/* 402 */           this.wasEmpty = true;
/*     */         }
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
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 417 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 426 */     return -1;
/*     */   }
/*     */   
/*     */   protected void nextRecord() throws SQLException
/*     */   {}
/*     */   
/*     */   private void notSupported() throws SQLException
/*     */   {
/* 434 */     throw new OperationNotSupportedException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 443 */     this.owner = rs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 452 */     return this.owner;
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 456 */     return this.wasEmpty;
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 460 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\RowDataCursor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */