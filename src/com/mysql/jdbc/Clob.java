/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
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
/*     */ public class Clob
/*     */   implements java.sql.Clob, OutputStreamWatcher, WriterWatcher
/*     */ {
/*     */   private String charData;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   Clob(ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  42 */     this.charData = "";
/*  43 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/*  47 */     this.charData = charDataInit;
/*  48 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */ 
/*     */   public InputStream getAsciiStream()
/*     */     throws SQLException
/*     */   {
/*  55 */     if (this.charData != null) {
/*  56 */       return new ByteArrayInputStream(StringUtils.getBytes(this.charData));
/*     */     }
/*     */     
/*  59 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/*  66 */     if (this.charData != null) {
/*  67 */       return new StringReader(this.charData);
/*     */     }
/*     */     
/*  70 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSubString(long startPos, int length)
/*     */     throws SQLException
/*     */   {
/*  77 */     if (startPos < 1L) {
/*  78 */       throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*  81 */     int adjustedStartPos = (int)startPos - 1;
/*  82 */     int adjustedEndIndex = adjustedStartPos + length;
/*     */     
/*  84 */     if (this.charData != null) {
/*  85 */       if (adjustedEndIndex > this.charData.length()) {
/*  86 */         throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/*  89 */       return this.charData.substring(adjustedStartPos, adjustedEndIndex);
/*     */     }
/*     */     
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/*  99 */     if (this.charData != null) {
/* 100 */       return this.charData.length();
/*     */     }
/*     */     
/* 103 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long position(java.sql.Clob arg0, long arg1)
/*     */     throws SQLException
/*     */   {
/* 110 */     return position(arg0.getSubString(1L, (int)arg0.length()), arg1);
/*     */   }
/*     */   
/*     */ 
/*     */   public long position(String stringToFind, long startPos)
/*     */     throws SQLException
/*     */   {
/* 117 */     if (startPos < 1L) {
/* 118 */       throw SQLError.createSQLException(Messages.getString("Clob.8") + startPos + Messages.getString("Clob.9"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 122 */     if (this.charData != null) {
/* 123 */       if (startPos - 1L > this.charData.length()) {
/* 124 */         throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 127 */       int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
/*     */       
/* 129 */       return pos == -1 ? -1L : pos + 1;
/*     */     }
/*     */     
/* 132 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public OutputStream setAsciiStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 139 */     if (indexToWriteAt < 1L) {
/* 140 */       throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 143 */     WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 144 */     bytesOut.setWatcher(this);
/*     */     
/* 146 */     if (indexToWriteAt > 0L) {
/* 147 */       bytesOut.write(StringUtils.getBytes(this.charData), 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */     
/* 150 */     return bytesOut;
/*     */   }
/*     */   
/*     */ 
/*     */   public Writer setCharacterStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 157 */     if (indexToWriteAt < 1L) {
/* 158 */       throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 161 */     WatchableWriter writer = new WatchableWriter();
/* 162 */     writer.setWatcher(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 167 */     if (indexToWriteAt > 1L) {
/* 168 */       writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
/*     */     }
/*     */     
/* 171 */     return writer;
/*     */   }
/*     */   
/*     */ 
/*     */   public int setString(long pos, String str)
/*     */     throws SQLException
/*     */   {
/* 178 */     if (pos < 1L) {
/* 179 */       throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 182 */     if (str == null) {
/* 183 */       throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 186 */     StringBuilder charBuf = new StringBuilder(this.charData);
/*     */     
/* 188 */     pos -= 1L;
/*     */     
/* 190 */     int strLength = str.length();
/*     */     
/* 192 */     charBuf.replace((int)pos, (int)(pos + strLength), str);
/*     */     
/* 194 */     this.charData = charBuf.toString();
/*     */     
/* 196 */     return strLength;
/*     */   }
/*     */   
/*     */ 
/*     */   public int setString(long pos, String str, int offset, int len)
/*     */     throws SQLException
/*     */   {
/* 203 */     if (pos < 1L) {
/* 204 */       throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 207 */     if (str == null) {
/* 208 */       throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 211 */     StringBuilder charBuf = new StringBuilder(this.charData);
/*     */     
/* 213 */     pos -= 1L;
/*     */     try
/*     */     {
/* 216 */       String replaceString = str.substring(offset, offset + len);
/*     */       
/* 218 */       charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
/*     */     } catch (StringIndexOutOfBoundsException e) {
/* 220 */       throw SQLError.createSQLException(e.getMessage(), "S1009", e, this.exceptionInterceptor);
/*     */     }
/*     */     
/* 223 */     this.charData = charBuf.toString();
/*     */     
/* 225 */     return len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void streamClosed(WatchableOutputStream out)
/*     */   {
/* 232 */     int streamSize = out.size();
/*     */     
/* 234 */     if (streamSize < this.charData.length()) {
/*     */       try {
/* 236 */         out.write(StringUtils.getBytes(this.charData, null, null, false, null, this.exceptionInterceptor), streamSize, this.charData.length() - streamSize);
/*     */       }
/*     */       catch (SQLException ex) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 243 */     this.charData = StringUtils.toAsciiString(out.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 250 */     if (length > this.charData.length()) {
/* 251 */       throw SQLError.createSQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 256 */     this.charData = this.charData.substring(0, (int)length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writerClosed(char[] charDataBeingWritten)
/*     */   {
/* 263 */     this.charData = new String(charDataBeingWritten);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writerClosed(WatchableWriter out)
/*     */   {
/* 270 */     int dataLength = out.size();
/*     */     
/* 272 */     if (dataLength < this.charData.length()) {
/* 273 */       out.write(this.charData, dataLength, this.charData.length() - dataLength);
/*     */     }
/*     */     
/* 276 */     this.charData = out.toString();
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/* 280 */     this.charData = null;
/*     */   }
/*     */   
/*     */   public Reader getCharacterStream(long pos, long length) throws SQLException {
/* 284 */     return new StringReader(getSubString(pos, (int)length));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Clob.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */