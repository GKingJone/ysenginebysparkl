/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RowDataDynamic
/*     */   implements RowData
/*     */ {
/*     */   private int columnCount;
/*     */   private Field[] metadata;
/*  40 */   private int index = -1;
/*     */   
/*     */   private MysqlIO io;
/*     */   
/*  44 */   private boolean isAfterEnd = false;
/*     */   
/*  46 */   private boolean noMoreRows = false;
/*     */   
/*  48 */   private boolean isBinaryEncoded = false;
/*     */   
/*     */   private ResultSetRow nextRow;
/*     */   
/*     */   private ResultSetImpl owner;
/*     */   
/*  54 */   private boolean streamerClosed = false;
/*     */   
/*  56 */   private boolean wasEmpty = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean useBufferRowExplicit;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean moreResultsExisted;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded)
/*     */     throws SQLException
/*     */   {
/*  79 */     this.io = io;
/*  80 */     this.columnCount = colCount;
/*  81 */     this.isBinaryEncoded = isBinaryEncoded;
/*  82 */     this.metadata = fields;
/*  83 */     this.exceptionInterceptor = this.io.getExceptionInterceptor();
/*  84 */     this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
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
/*  96 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 106 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 116 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 126 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 138 */     Object mutex = this;
/*     */     
/* 140 */     MySQLConnection conn = null;
/*     */     
/* 142 */     if (this.owner != null) {
/* 143 */       conn = this.owner.connection;
/*     */       
/* 145 */       if (conn != null) {
/* 146 */         mutex = conn.getConnectionMutex();
/*     */       }
/*     */     }
/*     */     
/* 150 */     boolean hadMore = false;
/* 151 */     int howMuchMore = 0;
/*     */     
/* 153 */     synchronized (mutex)
/*     */     {
/* 155 */       while (next() != null) {
/* 156 */         hadMore = true;
/* 157 */         howMuchMore++;
/*     */         
/* 159 */         if (howMuchMore % 100 == 0) {
/* 160 */           Thread.yield();
/*     */         }
/*     */       }
/*     */       
/* 164 */       if (conn != null) {
/* 165 */         if ((!conn.getClobberStreamingResults()) && (conn.getNetTimeoutForStreamingResults() > 0)) {
/* 166 */           String oldValue = conn.getServerVariable("net_write_timeout");
/*     */           
/* 168 */           if ((oldValue == null) || (oldValue.length() == 0)) {
/* 169 */             oldValue = "60";
/*     */           }
/*     */           
/* 172 */           this.io.clearInputStream();
/*     */           
/* 174 */           Statement stmt = null;
/*     */           try
/*     */           {
/* 177 */             stmt = conn.createStatement();
/* 178 */             ((StatementImpl)stmt).executeSimpleNonQuery(conn, "SET net_write_timeout=" + oldValue);
/*     */           } finally {
/* 180 */             if (stmt != null) {
/* 181 */               stmt.close();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 186 */         if ((conn.getUseUsageAdvisor()) && 
/* 187 */           (hadMore))
/*     */         {
/* 189 */           ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(conn);
/*     */           
/* 191 */           eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owner.owningStatement == null ? "N/A" : this.owner.owningStatement.currentCatalog, this.owner.connectionId, this.owner.owningStatement == null ? -1 : this.owner.owningStatement.getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, null, Messages.getString("RowDataDynamic.2") + howMuchMore + Messages.getString("RowDataDynamic.3") + Messages.getString("RowDataDynamic.4") + Messages.getString("RowDataDynamic.5") + Messages.getString("RowDataDynamic.6") + this.owner.pointOfOrigin));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */     this.metadata = null;
/* 204 */     this.owner = null;
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
/* 217 */     notSupported();
/*     */     
/* 219 */     return null;
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
/* 230 */     notSupported();
/*     */     
/* 232 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/* 239 */     return this.owner;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 250 */     boolean hasNext = this.nextRow != null;
/*     */     
/* 252 */     if ((!hasNext) && (!this.streamerClosed)) {
/* 253 */       this.io.closeStreamer(this);
/* 254 */       this.streamerClosed = true;
/*     */     }
/*     */     
/* 257 */     return hasNext;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAfterLast()
/*     */     throws SQLException
/*     */   {
/* 268 */     return this.isAfterEnd;
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
/* 279 */     return this.index < 0;
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
/* 291 */     return true;
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
/* 302 */     notSupported();
/*     */     
/* 304 */     return false;
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
/* 315 */     notSupported();
/*     */     
/* 317 */     return false;
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
/* 328 */     notSupported();
/*     */     
/* 330 */     return false;
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
/* 342 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSetRow next()
/*     */     throws SQLException
/*     */   {
/* 354 */     nextRecord();
/*     */     
/* 356 */     if ((this.nextRow == null) && (!this.streamerClosed) && (!this.moreResultsExisted)) {
/* 357 */       this.io.closeStreamer(this);
/* 358 */       this.streamerClosed = true;
/*     */     }
/*     */     
/* 361 */     if ((this.nextRow != null) && 
/* 362 */       (this.index != Integer.MAX_VALUE)) {
/* 363 */       this.index += 1;
/*     */     }
/*     */     
/*     */ 
/* 367 */     return this.nextRow;
/*     */   }
/*     */   
/*     */   private void nextRecord() throws SQLException
/*     */   {
/*     */     try {
/* 373 */       if (!this.noMoreRows) {
/* 374 */         this.nextRow = this.io.nextRow(this.metadata, this.columnCount, this.isBinaryEncoded, 1007, true, this.useBufferRowExplicit, true, null);
/*     */         
/*     */ 
/* 377 */         if (this.nextRow == null) {
/* 378 */           this.noMoreRows = true;
/* 379 */           this.isAfterEnd = true;
/* 380 */           this.moreResultsExisted = this.io.tackOnMoreStreamingResults(this.owner);
/*     */           
/* 382 */           if (this.index == -1) {
/* 383 */             this.wasEmpty = true;
/*     */           }
/*     */         }
/*     */       } else {
/* 387 */         this.nextRow = null;
/* 388 */         this.isAfterEnd = true;
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 391 */       if ((sqlEx instanceof StreamingNotifiable)) {
/* 392 */         ((StreamingNotifiable)sqlEx).setWasStreamingResults();
/*     */       }
/*     */       
/*     */ 
/* 396 */       this.noMoreRows = true;
/*     */       
/*     */ 
/* 399 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 401 */       String exceptionType = ex.getClass().getName();
/* 402 */       String exceptionMessage = ex.getMessage();
/*     */       
/* 404 */       exceptionMessage = exceptionMessage + Messages.getString("RowDataDynamic.7");
/* 405 */       exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*     */       
/* 407 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("RowDataDynamic.8") + exceptionType + Messages.getString("RowDataDynamic.9") + exceptionMessage, "S1000", this.exceptionInterceptor);
/*     */       
/*     */ 
/* 410 */       sqlEx.initCause(ex);
/*     */       
/* 412 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   private void notSupported() throws SQLException {
/* 417 */     throw new OperationNotSupportedException();
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
/* 429 */     notSupported();
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
/* 441 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 448 */     this.owner = rs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 457 */     return -1;
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 461 */     return this.wasEmpty;
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 465 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\RowDataDynamic.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */