/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteArrayRow
/*     */   extends ResultSetRow
/*     */ {
/*     */   byte[][] internalRowData;
/*     */   
/*     */   public ByteArrayRow(byte[][] internalRowData, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  46 */     super(exceptionInterceptor);
/*     */     
/*  48 */     this.internalRowData = internalRowData;
/*     */   }
/*     */   
/*     */   public byte[] getColumnValue(int index) throws SQLException
/*     */   {
/*  53 */     return this.internalRowData[index];
/*     */   }
/*     */   
/*     */   public void setColumnValue(int index, byte[] value) throws SQLException
/*     */   {
/*  58 */     this.internalRowData[index] = value;
/*     */   }
/*     */   
/*     */   public String getString(int index, String encoding, MySQLConnection conn) throws SQLException
/*     */   {
/*  63 */     byte[] columnData = this.internalRowData[index];
/*     */     
/*  65 */     if (columnData == null) {
/*  66 */       return null;
/*     */     }
/*     */     
/*  69 */     return getString(encoding, conn, columnData, 0, columnData.length);
/*     */   }
/*     */   
/*     */   public boolean isNull(int index) throws SQLException
/*     */   {
/*  74 */     return this.internalRowData[index] == null;
/*     */   }
/*     */   
/*     */   public boolean isFloatingPointNumber(int index) throws SQLException
/*     */   {
/*  79 */     byte[] numAsBytes = this.internalRowData[index];
/*     */     
/*  81 */     if ((this.internalRowData[index] == null) || (this.internalRowData[index].length == 0)) {
/*  82 */       return false;
/*     */     }
/*     */     
/*  85 */     for (int i = 0; i < numAsBytes.length; i++) {
/*  86 */       if (((char)numAsBytes[i] == 'e') || ((char)numAsBytes[i] == 'E')) {
/*  87 */         return true;
/*     */       }
/*     */     }
/*     */     
/*  91 */     return false;
/*     */   }
/*     */   
/*     */   public long length(int index) throws SQLException
/*     */   {
/*  96 */     if (this.internalRowData[index] == null) {
/*  97 */       return 0L;
/*     */     }
/*     */     
/* 100 */     return this.internalRowData[index].length;
/*     */   }
/*     */   
/*     */   public int getInt(int columnIndex)
/*     */   {
/* 105 */     if (this.internalRowData[columnIndex] == null) {
/* 106 */       return 0;
/*     */     }
/*     */     
/* 109 */     return StringUtils.getInt(this.internalRowData[columnIndex]);
/*     */   }
/*     */   
/*     */   public long getLong(int columnIndex)
/*     */   {
/* 114 */     if (this.internalRowData[columnIndex] == null) {
/* 115 */       return 0L;
/*     */     }
/*     */     
/* 118 */     return StringUtils.getLong(this.internalRowData[columnIndex]);
/*     */   }
/*     */   
/*     */   public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 124 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 126 */     if (columnValue == null) {
/* 127 */       return null;
/*     */     }
/*     */     
/* 130 */     return getTimestampFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */   
/*     */   public double getNativeDouble(int columnIndex) throws SQLException
/*     */   {
/* 135 */     if (this.internalRowData[columnIndex] == null) {
/* 136 */       return 0.0D;
/*     */     }
/*     */     
/* 139 */     return getNativeDouble(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public float getNativeFloat(int columnIndex) throws SQLException
/*     */   {
/* 144 */     if (this.internalRowData[columnIndex] == null) {
/* 145 */       return 0.0F;
/*     */     }
/*     */     
/* 148 */     return getNativeFloat(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public int getNativeInt(int columnIndex) throws SQLException
/*     */   {
/* 153 */     if (this.internalRowData[columnIndex] == null) {
/* 154 */       return 0;
/*     */     }
/*     */     
/* 157 */     return getNativeInt(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public long getNativeLong(int columnIndex) throws SQLException
/*     */   {
/* 162 */     if (this.internalRowData[columnIndex] == null) {
/* 163 */       return 0L;
/*     */     }
/*     */     
/* 166 */     return getNativeLong(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public short getNativeShort(int columnIndex) throws SQLException
/*     */   {
/* 171 */     if (this.internalRowData[columnIndex] == null) {
/* 172 */       return 0;
/*     */     }
/*     */     
/* 175 */     return getNativeShort(this.internalRowData[columnIndex], 0);
/*     */   }
/*     */   
/*     */   public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 181 */     byte[] bits = this.internalRowData[columnIndex];
/*     */     
/* 183 */     if (bits == null) {
/* 184 */       return null;
/*     */     }
/*     */     
/* 187 */     return getNativeTimestamp(bits, 0, bits.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */   
/*     */ 
/*     */   public void closeOpenStreams() {}
/*     */   
/*     */ 
/*     */   public InputStream getBinaryInputStream(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 197 */     if (this.internalRowData[columnIndex] == null) {
/* 198 */       return null;
/*     */     }
/*     */     
/* 201 */     return new ByteArrayInputStream(this.internalRowData[columnIndex]);
/*     */   }
/*     */   
/*     */   public Reader getReader(int columnIndex) throws SQLException
/*     */   {
/* 206 */     InputStream stream = getBinaryInputStream(columnIndex);
/*     */     
/* 208 */     if (stream == null) {
/* 209 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 213 */       return new InputStreamReader(stream, this.metadata[columnIndex].getEncoding());
/*     */     } catch (UnsupportedEncodingException e) {
/* 215 */       SQLException sqlEx = SQLError.createSQLException("", this.exceptionInterceptor);
/*     */       
/* 217 */       sqlEx.initCause(e);
/*     */       
/* 219 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 226 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 228 */     if (columnValue == null) {
/* 229 */       return null;
/*     */     }
/*     */     
/* 232 */     return getTimeFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */   
/*     */   public Date getDateFast(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException
/*     */   {
/* 237 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 239 */     if (columnValue == null) {
/* 240 */       return null;
/*     */     }
/*     */     
/* 243 */     return getDateFast(columnIndex, this.internalRowData[columnIndex], 0, columnValue.length, conn, rs, targetCalendar);
/*     */   }
/*     */   
/*     */   public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 249 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 251 */     if (columnValue == null) {
/* 252 */       return null;
/*     */     }
/*     */     
/* 255 */     return getNativeDateTimeValue(columnIndex, columnValue, 0, columnValue.length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
/*     */   }
/*     */   
/*     */   public Date getNativeDate(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar cal) throws SQLException
/*     */   {
/* 260 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 262 */     if (columnValue == null) {
/* 263 */       return null;
/*     */     }
/*     */     
/* 266 */     return getNativeDate(columnIndex, columnValue, 0, columnValue.length, conn, rs, cal);
/*     */   }
/*     */   
/*     */   public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*     */     throws SQLException
/*     */   {
/* 272 */     byte[] columnValue = this.internalRowData[columnIndex];
/*     */     
/* 274 */     if (columnValue == null) {
/* 275 */       return null;
/*     */     }
/*     */     
/* 278 */     return getNativeTime(columnIndex, columnValue, 0, columnValue.length, targetCalendar, tz, rollForward, conn, rs);
/*     */   }
/*     */   
/*     */   public int getBytesSize()
/*     */   {
/* 283 */     if (this.internalRowData == null) {
/* 284 */       return 0;
/*     */     }
/*     */     
/* 287 */     int bytesSize = 0;
/*     */     
/* 289 */     for (int i = 0; i < this.internalRowData.length; i++) {
/* 290 */       if (this.internalRowData[i] != null) {
/* 291 */         bytesSize += this.internalRowData[i].length;
/*     */       }
/*     */     }
/*     */     
/* 295 */     return bytesSize;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ByteArrayRow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */