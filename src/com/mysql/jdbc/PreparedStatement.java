/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParsePosition;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PreparedStatement
/*      */   extends StatementImpl
/*      */   implements java.sql.PreparedStatement
/*      */ {
/*      */   private static final Constructor<?> JDBC_4_PSTMT_2_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_PSTMT_3_ARG_CTOR;
/*      */   private static final Constructor<?> JDBC_4_PSTMT_4_ARG_CTOR;
/*      */   
/*      */   static
/*      */   {
/*   84 */     if (Util.isJdbc4()) {
/*      */       try {
/*   86 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42PreparedStatement" : "com.mysql.jdbc.JDBC4PreparedStatement";
/*   87 */         JDBC_4_PSTMT_2_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, String.class });
/*   88 */         JDBC_4_PSTMT_3_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, String.class, String.class });
/*   89 */         JDBC_4_PSTMT_4_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { MySQLConnection.class, String.class, String.class, ParseInfo.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*   92 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*   94 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*   96 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*   99 */       JDBC_4_PSTMT_2_ARG_CTOR = null;
/*  100 */       JDBC_4_PSTMT_3_ARG_CTOR = null;
/*  101 */       JDBC_4_PSTMT_4_ARG_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */   public class BatchParams {
/*  106 */     public boolean[] isNull = null;
/*      */     
/*  108 */     public boolean[] isStream = null;
/*      */     
/*  110 */     public InputStream[] parameterStreams = null;
/*      */     
/*  112 */     public byte[][] parameterStrings = (byte[][])null;
/*      */     
/*  114 */     public int[] streamLengths = null;
/*      */     
/*      */ 
/*      */ 
/*      */     BatchParams(byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags)
/*      */     {
/*  120 */       this.parameterStrings = new byte[strings.length][];
/*  121 */       this.parameterStreams = new InputStream[streams.length];
/*  122 */       this.isStream = new boolean[isStreamFlags.length];
/*  123 */       this.streamLengths = new int[lengths.length];
/*  124 */       this.isNull = new boolean[isNullFlags.length];
/*  125 */       System.arraycopy(strings, 0, this.parameterStrings, 0, strings.length);
/*  126 */       System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
/*  127 */       System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
/*  128 */       System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
/*  129 */       System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
/*      */     }
/*      */   }
/*      */   
/*      */   class EndPoint
/*      */   {
/*      */     int begin;
/*      */     int end;
/*      */     
/*      */     EndPoint(int b, int e) {
/*  139 */       this.begin = b;
/*  140 */       this.end = e;
/*      */     }
/*      */   }
/*      */   
/*      */   public static final class ParseInfo {
/*  145 */     char firstStmtChar = '\000';
/*      */     
/*  147 */     boolean foundLoadData = false;
/*      */     
/*  149 */     long lastUsed = 0L;
/*      */     
/*  151 */     int statementLength = 0;
/*      */     
/*  153 */     int statementStartPos = 0;
/*      */     
/*  155 */     boolean canRewriteAsMultiValueInsert = false;
/*      */     
/*  157 */     byte[][] staticSql = (byte[][])null;
/*      */     
/*  159 */     boolean isOnDuplicateKeyUpdate = false;
/*      */     
/*  161 */     int locationOfOnDuplicateKeyUpdate = -1;
/*      */     
/*      */     String valuesClause;
/*      */     
/*  165 */     boolean parametersInDuplicateKeyClause = false;
/*      */     String charEncoding;
/*      */     private ParseInfo batchHead;
/*      */     private ParseInfo batchValues;
/*      */     private ParseInfo batchODKUClause;
/*      */     
/*      */     ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  174 */       this(sql, conn, dbmd, encoding, converter, true);
/*      */     }
/*      */     
/*      */     public ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter, boolean buildRewriteInfo) throws SQLException
/*      */     {
/*      */       try {
/*  180 */         if (sql == null) {
/*  181 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.61"), "S1009", conn.getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*  185 */         this.charEncoding = encoding;
/*  186 */         this.lastUsed = System.currentTimeMillis();
/*      */         
/*  188 */         String quotedIdentifierString = dbmd.getIdentifierQuoteString();
/*      */         
/*  190 */         char quotedIdentifierChar = '\000';
/*      */         
/*  192 */         if ((quotedIdentifierString != null) && (!quotedIdentifierString.equals(" ")) && (quotedIdentifierString.length() > 0)) {
/*  193 */           quotedIdentifierChar = quotedIdentifierString.charAt(0);
/*      */         }
/*      */         
/*  196 */         this.statementLength = sql.length();
/*      */         
/*  198 */         ArrayList<int[]> endpointList = new ArrayList();
/*  199 */         boolean inQuotes = false;
/*  200 */         char quoteChar = '\000';
/*  201 */         boolean inQuotedId = false;
/*  202 */         int lastParmEnd = 0;
/*      */         
/*      */ 
/*  205 */         boolean noBackslashEscapes = conn.isNoBackslashEscapesSet();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  210 */         this.statementStartPos = StatementImpl.findStartOfStatement(sql);
/*      */         
/*  212 */         for (int i = this.statementStartPos; i < this.statementLength; i++) {
/*  213 */           char c = sql.charAt(i);
/*      */           
/*  215 */           if ((this.firstStmtChar == 0) && (Character.isLetter(c)))
/*      */           {
/*  217 */             this.firstStmtChar = Character.toUpperCase(c);
/*      */             
/*      */ 
/*  220 */             if (this.firstStmtChar == 'I') {
/*  221 */               this.locationOfOnDuplicateKeyUpdate = StatementImpl.getOnDuplicateKeyLocation(sql, conn.getDontCheckOnDuplicateKeyUpdateInSQL(), conn.getRewriteBatchedStatements(), conn.isNoBackslashEscapesSet());
/*      */               
/*  223 */               this.isOnDuplicateKeyUpdate = (this.locationOfOnDuplicateKeyUpdate != -1);
/*      */             }
/*      */           }
/*      */           
/*  227 */           if ((!noBackslashEscapes) && (c == '\\') && (i < this.statementLength - 1)) {
/*  228 */             i++;
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  233 */             if ((!inQuotes) && (quotedIdentifierChar != 0) && (c == quotedIdentifierChar)) {
/*  234 */               inQuotedId = !inQuotedId;
/*  235 */             } else if (!inQuotedId)
/*      */             {
/*      */ 
/*  238 */               if (inQuotes) {
/*  239 */                 if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  240 */                   if ((i < this.statementLength - 1) && (sql.charAt(i + 1) == quoteChar)) {
/*  241 */                     i++;
/*  242 */                     continue;
/*      */                   }
/*      */                   
/*  245 */                   inQuotes = !inQuotes;
/*  246 */                   quoteChar = '\000';
/*  247 */                 } else if (((c == '\'') || (c == '"')) && (c == quoteChar)) {
/*  248 */                   inQuotes = !inQuotes;
/*  249 */                   quoteChar = '\000';
/*      */                 }
/*      */               } else {
/*  252 */                 if ((c == '#') || ((c == '-') && (i + 1 < this.statementLength) && (sql.charAt(i + 1) == '-')))
/*      */                 {
/*  254 */                   int endOfStmt = this.statementLength - 1;
/*  256 */                   for (; 
/*  256 */                       i < endOfStmt; i++) {
/*  257 */                     c = sql.charAt(i);
/*      */                     
/*  259 */                     if ((c == '\r') || (c == '\n')) {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*  265 */                 if ((c == '/') && (i + 1 < this.statementLength))
/*      */                 {
/*  267 */                   char cNext = sql.charAt(i + 1);
/*      */                   
/*  269 */                   if (cNext == '*') {
/*  270 */                     i += 2;
/*      */                     
/*  272 */                     for (int j = i; j < this.statementLength; j++) {
/*  273 */                       i++;
/*  274 */                       cNext = sql.charAt(j);
/*      */                       
/*  276 */                       if ((cNext == '*') && (j + 1 < this.statementLength) && 
/*  277 */                         (sql.charAt(j + 1) == '/')) {
/*  278 */                         i++;
/*      */                         
/*  280 */                         if (i >= this.statementLength) break;
/*  281 */                         c = sql.charAt(i); break;
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */                 }
/*  289 */                 else if ((c == '\'') || (c == '"')) {
/*  290 */                   inQuotes = true;
/*  291 */                   quoteChar = c;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  296 */             if ((c == '?') && (!inQuotes) && (!inQuotedId)) {
/*  297 */               endpointList.add(new int[] { lastParmEnd, i });
/*  298 */               lastParmEnd = i + 1;
/*      */               
/*  300 */               if ((this.isOnDuplicateKeyUpdate) && (i > this.locationOfOnDuplicateKeyUpdate)) {
/*  301 */                 this.parametersInDuplicateKeyClause = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  306 */         if (this.firstStmtChar == 'L') {
/*  307 */           if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA")) {
/*  308 */             this.foundLoadData = true;
/*      */           } else {
/*  310 */             this.foundLoadData = false;
/*      */           }
/*      */         } else {
/*  313 */           this.foundLoadData = false;
/*      */         }
/*      */         
/*  316 */         endpointList.add(new int[] { lastParmEnd, this.statementLength });
/*  317 */         this.staticSql = new byte[endpointList.size()][];
/*      */         
/*  319 */         for (i = 0; i < this.staticSql.length; i++) {
/*  320 */           int[] ep = (int[])endpointList.get(i);
/*  321 */           int end = ep[1];
/*  322 */           int begin = ep[0];
/*  323 */           int len = end - begin;
/*      */           
/*  325 */           if (this.foundLoadData) {
/*  326 */             this.staticSql[i] = StringUtils.getBytes(sql, begin, len);
/*  327 */           } else if (encoding == null) {
/*  328 */             byte[] buf = new byte[len];
/*      */             
/*  330 */             for (int j = 0; j < len; j++) {
/*  331 */               buf[j] = ((byte)sql.charAt(begin + j));
/*      */             }
/*      */             
/*  334 */             this.staticSql[i] = buf;
/*      */           }
/*  336 */           else if (converter != null) {
/*  337 */             this.staticSql[i] = StringUtils.getBytes(sql, converter, encoding, conn.getServerCharset(), begin, len, conn.parserKnowsUnicode(), conn.getExceptionInterceptor());
/*      */           }
/*      */           else {
/*  340 */             this.staticSql[i] = StringUtils.getBytes(sql, encoding, conn.getServerCharset(), begin, len, conn.parserKnowsUnicode(), conn, conn.getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (StringIndexOutOfBoundsException oobEx)
/*      */       {
/*  346 */         SQLException sqlEx = new SQLException("Parse error for " + sql);
/*  347 */         sqlEx.initCause(oobEx);
/*      */         
/*  349 */         throw sqlEx;
/*      */       }
/*      */       
/*  352 */       if (buildRewriteInfo) {
/*  353 */         this.canRewriteAsMultiValueInsert = ((PreparedStatement.canRewrite(sql, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos)) && (!this.parametersInDuplicateKeyClause));
/*      */         
/*      */ 
/*  356 */         if ((this.canRewriteAsMultiValueInsert) && (conn.getRewriteBatchedStatements())) {
/*  357 */           buildRewriteBatchedParams(sql, conn, dbmd, encoding, converter);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void buildRewriteBatchedParams(String sql, MySQLConnection conn, DatabaseMetaData metadata, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  370 */       this.valuesClause = extractValuesClause(sql, conn.getMetaData().getIdentifierQuoteString());
/*  371 */       String odkuClause = this.isOnDuplicateKeyUpdate ? sql.substring(this.locationOfOnDuplicateKeyUpdate) : null;
/*      */       
/*  373 */       String headSql = null;
/*      */       
/*  375 */       if (this.isOnDuplicateKeyUpdate) {
/*  376 */         headSql = sql.substring(0, this.locationOfOnDuplicateKeyUpdate);
/*      */       } else {
/*  378 */         headSql = sql;
/*      */       }
/*      */       
/*  381 */       this.batchHead = new ParseInfo(headSql, conn, metadata, encoding, converter, false);
/*  382 */       this.batchValues = new ParseInfo("," + this.valuesClause, conn, metadata, encoding, converter, false);
/*  383 */       this.batchODKUClause = null;
/*      */       
/*  385 */       if ((odkuClause != null) && (odkuClause.length() > 0)) {
/*  386 */         this.batchODKUClause = new ParseInfo("," + this.valuesClause + " " + odkuClause, conn, metadata, encoding, converter, false);
/*      */       }
/*      */     }
/*      */     
/*      */     private String extractValuesClause(String sql, String quoteCharStr) throws SQLException {
/*  391 */       int indexOfValues = -1;
/*  392 */       int valuesSearchStart = this.statementStartPos;
/*      */       
/*  394 */       while (indexOfValues == -1) {
/*  395 */         if (quoteCharStr.length() > 0) {
/*  396 */           indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUES", quoteCharStr, quoteCharStr, StringUtils.SEARCH_MODE__MRK_COM_WS);
/*      */         }
/*      */         else {
/*  399 */           indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUES");
/*      */         }
/*      */         
/*  402 */         if (indexOfValues <= 0)
/*      */           break;
/*  404 */         char c = sql.charAt(indexOfValues - 1);
/*  405 */         if ((!Character.isWhitespace(c)) && (c != ')') && (c != '`')) {
/*  406 */           valuesSearchStart = indexOfValues + 6;
/*  407 */           indexOfValues = -1;
/*      */         }
/*      */         else {
/*  410 */           c = sql.charAt(indexOfValues + 6);
/*  411 */           if ((!Character.isWhitespace(c)) && (c != '(')) {
/*  412 */             valuesSearchStart = indexOfValues + 6;
/*  413 */             indexOfValues = -1;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  421 */       if (indexOfValues == -1) {
/*  422 */         return null;
/*      */       }
/*      */       
/*  425 */       int indexOfFirstParen = sql.indexOf('(', indexOfValues + 6);
/*      */       
/*  427 */       if (indexOfFirstParen == -1) {
/*  428 */         return null;
/*      */       }
/*      */       
/*  431 */       int endOfValuesClause = sql.lastIndexOf(')');
/*      */       
/*  433 */       if (endOfValuesClause == -1) {
/*  434 */         return null;
/*      */       }
/*      */       
/*  437 */       if (this.isOnDuplicateKeyUpdate) {
/*  438 */         endOfValuesClause = this.locationOfOnDuplicateKeyUpdate - 1;
/*      */       }
/*      */       
/*  441 */       return sql.substring(indexOfFirstParen, endOfValuesClause + 1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     synchronized ParseInfo getParseInfoForBatch(int numBatch)
/*      */     {
/*  448 */       AppendingBatchVisitor apv = new AppendingBatchVisitor();
/*  449 */       buildInfoForBatch(numBatch, apv);
/*      */       
/*  451 */       ParseInfo batchParseInfo = new ParseInfo(apv.getStaticSqlStrings(), this.firstStmtChar, this.foundLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
/*      */       
/*      */ 
/*  454 */       return batchParseInfo;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     String getSqlForBatch(int numBatch)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  463 */       ParseInfo batchInfo = getParseInfoForBatch(numBatch);
/*      */       
/*  465 */       return getSqlForBatch(batchInfo);
/*      */     }
/*      */     
/*      */ 
/*      */     String getSqlForBatch(ParseInfo batchInfo)
/*      */       throws UnsupportedEncodingException
/*      */     {
/*  472 */       int size = 0;
/*  473 */       byte[][] sqlStrings = batchInfo.staticSql;
/*  474 */       int sqlStringsLength = sqlStrings.length;
/*      */       
/*  476 */       for (int i = 0; i < sqlStringsLength; i++) {
/*  477 */         size += sqlStrings[i].length;
/*  478 */         size++;
/*      */       }
/*      */       
/*  481 */       StringBuilder buf = new StringBuilder(size);
/*      */       
/*  483 */       for (int i = 0; i < sqlStringsLength - 1; i++) {
/*  484 */         buf.append(StringUtils.toString(sqlStrings[i], this.charEncoding));
/*  485 */         buf.append("?");
/*      */       }
/*      */       
/*  488 */       buf.append(StringUtils.toString(sqlStrings[(sqlStringsLength - 1)]));
/*      */       
/*  490 */       return buf.toString();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void buildInfoForBatch(int numBatch, BatchVisitor visitor)
/*      */     {
/*  502 */       byte[][] headStaticSql = this.batchHead.staticSql;
/*  503 */       int headStaticSqlLength = headStaticSql.length;
/*      */       
/*  505 */       if (headStaticSqlLength > 1) {
/*  506 */         for (int i = 0; i < headStaticSqlLength - 1; i++) {
/*  507 */           visitor.append(headStaticSql[i]).increment();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  512 */       byte[] endOfHead = headStaticSql[(headStaticSqlLength - 1)];
/*  513 */       byte[][] valuesStaticSql = this.batchValues.staticSql;
/*  514 */       byte[] beginOfValues = valuesStaticSql[0];
/*      */       
/*  516 */       visitor.merge(endOfHead, beginOfValues).increment();
/*      */       
/*  518 */       int numValueRepeats = numBatch - 1;
/*      */       
/*  520 */       if (this.batchODKUClause != null) {
/*  521 */         numValueRepeats--;
/*      */       }
/*      */       
/*  524 */       int valuesStaticSqlLength = valuesStaticSql.length;
/*  525 */       byte[] endOfValues = valuesStaticSql[(valuesStaticSqlLength - 1)];
/*      */       
/*  527 */       for (int i = 0; i < numValueRepeats; i++) {
/*  528 */         for (int j = 1; j < valuesStaticSqlLength - 1; j++) {
/*  529 */           visitor.append(valuesStaticSql[j]).increment();
/*      */         }
/*  531 */         visitor.merge(endOfValues, beginOfValues).increment();
/*      */       }
/*      */       
/*  534 */       if (this.batchODKUClause != null) {
/*  535 */         byte[][] batchOdkuStaticSql = this.batchODKUClause.staticSql;
/*  536 */         byte[] beginOfOdku = batchOdkuStaticSql[0];
/*  537 */         visitor.decrement().merge(endOfValues, beginOfOdku).increment();
/*      */         
/*  539 */         int batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
/*      */         
/*  541 */         if (numBatch > 1) {
/*  542 */           for (int i = 1; i < batchOdkuStaticSqlLength; i++) {
/*  543 */             visitor.append(batchOdkuStaticSql[i]).increment();
/*      */           }
/*      */         } else {
/*  546 */           visitor.decrement().append(batchOdkuStaticSql[(batchOdkuStaticSqlLength - 1)]);
/*      */         }
/*      */       }
/*      */       else {
/*  550 */         visitor.decrement().append(this.staticSql[(this.staticSql.length - 1)]);
/*      */       }
/*      */     }
/*      */     
/*      */     private ParseInfo(byte[][] staticSql, char firstStmtChar, boolean foundLoadData, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementLength, int statementStartPos)
/*      */     {
/*  556 */       this.firstStmtChar = firstStmtChar;
/*  557 */       this.foundLoadData = foundLoadData;
/*  558 */       this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate;
/*  559 */       this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate;
/*  560 */       this.statementLength = statementLength;
/*  561 */       this.statementStartPos = statementStartPos;
/*  562 */       this.staticSql = staticSql;
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract interface BatchVisitor {
/*      */     public abstract BatchVisitor increment();
/*      */     
/*      */     public abstract BatchVisitor decrement();
/*      */     
/*      */     public abstract BatchVisitor append(byte[] paramArrayOfByte);
/*      */     
/*      */     public abstract BatchVisitor merge(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
/*      */   }
/*      */   
/*      */   static class AppendingBatchVisitor implements BatchVisitor {
/*  577 */     LinkedList<byte[]> statementComponents = new LinkedList();
/*      */     
/*      */     public BatchVisitor append(byte[] values) {
/*  580 */       this.statementComponents.addLast(values);
/*      */       
/*  582 */       return this;
/*      */     }
/*      */     
/*      */     public BatchVisitor increment()
/*      */     {
/*  587 */       return this;
/*      */     }
/*      */     
/*      */     public BatchVisitor decrement() {
/*  591 */       this.statementComponents.removeLast();
/*      */       
/*  593 */       return this;
/*      */     }
/*      */     
/*      */     public BatchVisitor merge(byte[] front, byte[] back) {
/*  597 */       int mergedLength = front.length + back.length;
/*  598 */       byte[] merged = new byte[mergedLength];
/*  599 */       System.arraycopy(front, 0, merged, 0, front.length);
/*  600 */       System.arraycopy(back, 0, merged, front.length, back.length);
/*  601 */       this.statementComponents.addLast(merged);
/*  602 */       return this;
/*      */     }
/*      */     
/*      */     public byte[][] getStaticSqlStrings() {
/*  606 */       byte[][] asBytes = new byte[this.statementComponents.size()][];
/*  607 */       this.statementComponents.toArray(asBytes);
/*      */       
/*  609 */       return asBytes;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*  614 */       StringBuilder buf = new StringBuilder();
/*  615 */       Iterator<byte[]> iter = this.statementComponents.iterator();
/*  616 */       while (iter.hasNext()) {
/*  617 */         buf.append(StringUtils.toString((byte[])iter.next()));
/*      */       }
/*      */       
/*  620 */       return buf.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  625 */   private static final byte[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static int readFully(Reader reader, char[] buf, int length)
/*      */     throws IOException
/*      */   {
/*  639 */     int numCharsRead = 0;
/*      */     
/*  641 */     while (numCharsRead < length) {
/*  642 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*      */       
/*  644 */       if (count < 0) {
/*      */         break;
/*      */       }
/*      */       
/*  648 */       numCharsRead += count;
/*      */     }
/*      */     
/*  651 */     return numCharsRead;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  660 */   protected boolean batchHasPlainStatements = false;
/*      */   
/*  662 */   private DatabaseMetaData dbmd = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  668 */   protected char firstCharOfStmt = '\000';
/*      */   
/*      */ 
/*  671 */   protected boolean isLoadDataQuery = false;
/*      */   
/*  673 */   protected boolean[] isNull = null;
/*      */   
/*  675 */   private boolean[] isStream = null;
/*      */   
/*  677 */   protected int numberOfExecutions = 0;
/*      */   
/*      */ 
/*  680 */   protected String originalSql = null;
/*      */   
/*      */ 
/*      */   protected int parameterCount;
/*      */   
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*      */   
/*  687 */   private InputStream[] parameterStreams = null;
/*      */   
/*  689 */   private byte[][] parameterValues = (byte[][])null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */   protected int[] parameterTypes = null;
/*      */   
/*      */   protected ParseInfo parseInfo;
/*      */   
/*      */   private java.sql.ResultSetMetaData pstmtResultMetaData;
/*      */   
/*  701 */   private byte[][] staticSqlStrings = (byte[][])null;
/*      */   
/*  703 */   private byte[] streamConvertBuf = null;
/*      */   
/*  705 */   private int[] streamLengths = null;
/*      */   
/*  707 */   private SimpleDateFormat tsdf = null;
/*      */   
/*      */ 
/*      */   private SimpleDateFormat ddf;
/*      */   
/*      */ 
/*      */   private SimpleDateFormat tdf;
/*      */   
/*      */ 
/*  716 */   protected boolean useTrueBoolean = false;
/*      */   
/*      */   protected boolean usingAnsiMode;
/*      */   
/*      */   protected String batchedValuesClause;
/*      */   
/*      */   private boolean doPingInstead;
/*      */   
/*  724 */   private boolean compensateForOnDuplicateKeyUpdate = false;
/*      */   
/*      */ 
/*      */   private CharsetEncoder charsetEncoder;
/*      */   
/*      */ 
/*  730 */   protected int batchCommandIndex = -1;
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean serverSupportsFracSecs;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  742 */     if (!Util.isJdbc4()) {
/*  743 */       return new PreparedStatement(conn, catalog);
/*      */     }
/*      */     
/*  746 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, new Object[] { conn, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  757 */     if (!Util.isJdbc4()) {
/*  758 */       return new PreparedStatement(conn, sql, catalog);
/*      */     }
/*      */     
/*  761 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, new Object[] { conn, sql, catalog }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  772 */     if (!Util.isJdbc4()) {
/*  773 */       return new PreparedStatement(conn, sql, catalog, cachedParseInfo);
/*      */     }
/*      */     
/*  776 */     return (PreparedStatement)Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, new Object[] { conn, sql, catalog, cachedParseInfo }, conn.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  792 */     super(conn, catalog);
/*      */     
/*  794 */     detectFractionalSecondsSupport();
/*  795 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */   }
/*      */   
/*      */   protected void detectFractionalSecondsSupport() throws SQLException {
/*  799 */     this.serverSupportsFracSecs = ((this.connection != null) && (this.connection.versionMeetsMinimum(5, 6, 4)));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  816 */     super(conn, catalog);
/*      */     
/*  818 */     if (sql == null) {
/*  819 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.0"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  822 */     detectFractionalSecondsSupport();
/*  823 */     this.originalSql = sql;
/*      */     
/*  825 */     if (this.originalSql.startsWith("/* ping */")) {
/*  826 */       this.doPingInstead = true;
/*      */     } else {
/*  828 */       this.doPingInstead = false;
/*      */     }
/*      */     
/*  831 */     this.dbmd = this.connection.getMetaData();
/*      */     
/*  833 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */     
/*  835 */     this.parseInfo = new ParseInfo(sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
/*      */     
/*  837 */     initializeFromParseInfo();
/*      */     
/*  839 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */     
/*  841 */     if (conn.getRequiresEscapingEncoder()) {
/*  842 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  861 */     super(conn, catalog);
/*      */     
/*  863 */     if (sql == null) {
/*  864 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.1"), "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  867 */     detectFractionalSecondsSupport();
/*  868 */     this.originalSql = sql;
/*      */     
/*  870 */     this.dbmd = this.connection.getMetaData();
/*      */     
/*  872 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */     
/*  874 */     this.parseInfo = cachedParseInfo;
/*      */     
/*  876 */     this.usingAnsiMode = (!this.connection.useAnsiQuotedIdentifiers());
/*      */     
/*  878 */     initializeFromParseInfo();
/*      */     
/*  880 */     this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
/*      */     
/*  882 */     if (conn.getRequiresEscapingEncoder()) {
/*  883 */       this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  896 */     synchronized (checkClosed().getConnectionMutex()) {
/*  897 */       if (this.batchedArgs == null) {
/*  898 */         this.batchedArgs = new ArrayList();
/*      */       }
/*      */       
/*  901 */       for (int i = 0; i < this.parameterValues.length; i++) {
/*  902 */         checkAllParametersSet(this.parameterValues[i], this.parameterStreams[i], i);
/*      */       }
/*      */       
/*  905 */       this.batchedArgs.add(new BatchParams(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
/*      */     }
/*      */   }
/*      */   
/*      */   public void addBatch(String sql) throws SQLException
/*      */   {
/*  911 */     synchronized (checkClosed().getConnectionMutex()) {
/*  912 */       this.batchHasPlainStatements = true;
/*      */       
/*  914 */       super.addBatch(sql);
/*      */     }
/*      */   }
/*      */   
/*      */   public String asSql() throws SQLException {
/*  919 */     return asSql(false);
/*      */   }
/*      */   
/*      */   public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/*  923 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*  925 */       StringBuilder buf = new StringBuilder();
/*      */       try
/*      */       {
/*  928 */         int realParameterCount = this.parameterCount + getParameterIndexOffset();
/*  929 */         Object batchArg = null;
/*  930 */         if (this.batchCommandIndex != -1) {
/*  931 */           batchArg = this.batchedArgs.get(this.batchCommandIndex);
/*      */         }
/*      */         
/*  934 */         for (int i = 0; i < realParameterCount; i++) {
/*  935 */           if (this.charEncoding != null) {
/*  936 */             buf.append(StringUtils.toString(this.staticSqlStrings[i], this.charEncoding));
/*      */           } else {
/*  938 */             buf.append(StringUtils.toString(this.staticSqlStrings[i]));
/*      */           }
/*      */           
/*  941 */           byte[] val = null;
/*  942 */           if ((batchArg != null) && ((batchArg instanceof String))) {
/*  943 */             buf.append((String)batchArg);
/*      */           }
/*      */           else {
/*  946 */             if (this.batchCommandIndex == -1) {
/*  947 */               val = this.parameterValues[i];
/*      */             } else {
/*  949 */               val = ((BatchParams)batchArg).parameterStrings[i];
/*      */             }
/*      */             
/*  952 */             boolean isStreamParam = false;
/*  953 */             if (this.batchCommandIndex == -1) {
/*  954 */               isStreamParam = this.isStream[i];
/*      */             } else {
/*  956 */               isStreamParam = ((BatchParams)batchArg).isStream[i];
/*      */             }
/*      */             
/*  959 */             if ((val == null) && (!isStreamParam)) {
/*  960 */               if (quoteStreamsAndUnknowns) {
/*  961 */                 buf.append("'");
/*      */               }
/*      */               
/*  964 */               buf.append("** NOT SPECIFIED **");
/*      */               
/*  966 */               if (quoteStreamsAndUnknowns) {
/*  967 */                 buf.append("'");
/*      */               }
/*  969 */             } else if (isStreamParam) {
/*  970 */               if (quoteStreamsAndUnknowns) {
/*  971 */                 buf.append("'");
/*      */               }
/*      */               
/*  974 */               buf.append("** STREAM DATA **");
/*      */               
/*  976 */               if (quoteStreamsAndUnknowns) {
/*  977 */                 buf.append("'");
/*      */               }
/*      */             }
/*  980 */             else if (this.charConverter != null) {
/*  981 */               buf.append(this.charConverter.toString(val));
/*      */             }
/*  983 */             else if (this.charEncoding != null) {
/*  984 */               buf.append(new String(val, this.charEncoding));
/*      */             } else {
/*  986 */               buf.append(StringUtils.toAsciiString(val));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  992 */         if (this.charEncoding != null) {
/*  993 */           buf.append(StringUtils.toString(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())], this.charEncoding));
/*      */         } else {
/*  995 */           buf.append(StringUtils.toAsciiString(this.staticSqlStrings[(this.parameterCount + getParameterIndexOffset())]));
/*      */         }
/*      */       } catch (UnsupportedEncodingException uue) {
/*  998 */         throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */       }
/*      */       
/* 1001 */       return buf.toString();
/*      */     }
/*      */   }
/*      */   
/*      */   public void clearBatch() throws SQLException
/*      */   {
/* 1007 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1008 */       this.batchHasPlainStatements = false;
/*      */       
/* 1010 */       super.clearBatch();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/* 1025 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1027 */       for (int i = 0; i < this.parameterValues.length; i++) {
/* 1028 */         this.parameterValues[i] = null;
/* 1029 */         this.parameterStreams[i] = null;
/* 1030 */         this.isStream[i] = false;
/* 1031 */         this.isNull[i] = false;
/* 1032 */         this.parameterTypes[i] = 0;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException {
/* 1038 */     int lastwritten = 0;
/*      */     
/* 1040 */     for (int i = 0; i < size; i++) {
/* 1041 */       byte b = buf[i];
/*      */       
/* 1043 */       if (b == 0)
/*      */       {
/* 1045 */         if (i > lastwritten) {
/* 1046 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */         
/*      */ 
/* 1050 */         packet.writeByte((byte)92);
/* 1051 */         packet.writeByte((byte)48);
/* 1052 */         lastwritten = i + 1;
/*      */       }
/* 1054 */       else if ((b == 92) || (b == 39) || ((!this.usingAnsiMode) && (b == 34)))
/*      */       {
/* 1056 */         if (i > lastwritten) {
/* 1057 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */         
/*      */ 
/* 1061 */         packet.writeByte((byte)92);
/* 1062 */         lastwritten = i;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1068 */     if (lastwritten < size) {
/* 1069 */       packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size) {
/* 1074 */     int lastwritten = 0;
/*      */     
/* 1076 */     for (int i = 0; i < size; i++) {
/* 1077 */       byte b = buf[i];
/*      */       
/* 1079 */       if (b == 0)
/*      */       {
/* 1081 */         if (i > lastwritten) {
/* 1082 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */         
/*      */ 
/* 1086 */         bytesOut.write(92);
/* 1087 */         bytesOut.write(48);
/* 1088 */         lastwritten = i + 1;
/*      */       }
/* 1090 */       else if ((b == 92) || (b == 39) || ((!this.usingAnsiMode) && (b == 34)))
/*      */       {
/* 1092 */         if (i > lastwritten) {
/* 1093 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */         
/*      */ 
/* 1097 */         bytesOut.write(92);
/* 1098 */         lastwritten = i;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1104 */     if (lastwritten < size) {
/* 1105 */       bytesOut.write(buf, lastwritten, size - lastwritten);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean checkReadOnlySafeStatement()
/*      */     throws SQLException
/*      */   {
/* 1116 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1117 */       return (this.firstCharOfStmt == 'S') || (!this.connection.isReadOnly());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/* 1133 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1135 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 1137 */       if (!checkReadOnlySafeStatement()) {
/* 1138 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1142 */       ResultSetInternalMethods rs = null;
/*      */       
/* 1144 */       CachedResultSetMetaData cachedMetadata = null;
/*      */       
/* 1146 */       this.lastQueryIsOnDupKeyUpdate = false;
/*      */       
/* 1148 */       if (this.retrieveGeneratedKeys) {
/* 1149 */         this.lastQueryIsOnDupKeyUpdate = containsOnDuplicateKeyUpdateInSQL();
/*      */       }
/*      */       
/* 1152 */       clearWarnings();
/*      */       
/* 1154 */       setupStreamingTimeout(locallyScopedConn);
/*      */       
/* 1156 */       this.batchedGeneratedKeys = null;
/*      */       
/* 1158 */       Buffer sendPacket = fillSendPacket();
/*      */       
/* 1160 */       String oldCatalog = null;
/*      */       
/* 1162 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1163 */         oldCatalog = locallyScopedConn.getCatalog();
/* 1164 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1170 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1171 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */       
/* 1174 */       Field[] metadataFromCache = null;
/*      */       
/* 1176 */       if (cachedMetadata != null) {
/* 1177 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */       
/* 1180 */       boolean oldInfoMsgState = false;
/*      */       
/* 1182 */       if (this.retrieveGeneratedKeys) {
/* 1183 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 1184 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1190 */       locallyScopedConn.setSessionMaxRows(this.firstCharOfStmt == 'S' ? this.maxRows : -1);
/*      */       
/* 1192 */       rs = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), this.firstCharOfStmt == 'S', metadataFromCache, false);
/*      */       
/* 1194 */       if (cachedMetadata != null) {
/* 1195 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, rs);
/*      */       }
/* 1197 */       else if ((rs.reallyResult()) && (locallyScopedConn.getCacheResultSetMetadata())) {
/* 1198 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, rs);
/*      */       }
/*      */       
/*      */ 
/* 1202 */       if (this.retrieveGeneratedKeys) {
/* 1203 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 1204 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */       
/* 1207 */       if (oldCatalog != null) {
/* 1208 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */       
/* 1211 */       if (rs != null) {
/* 1212 */         this.lastInsertId = rs.getUpdateID();
/*      */         
/* 1214 */         this.results = rs;
/*      */       }
/*      */       
/* 1217 */       return (rs != null) && (rs.reallyResult());
/*      */     }
/*      */   }
/*      */   
/*      */   protected long[] executeBatchInternal() throws SQLException
/*      */   {
/* 1223 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1225 */       if (this.connection.isReadOnly()) {
/* 1226 */         throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */       }
/*      */       
/*      */ 
/* 1230 */       if ((this.batchedArgs == null) || (this.batchedArgs.size() == 0)) {
/* 1231 */         return new long[0];
/*      */       }
/*      */       
/*      */ 
/* 1235 */       int batchTimeout = this.timeoutInMillis;
/* 1236 */       this.timeoutInMillis = 0;
/*      */       
/* 1238 */       resetCancelledState();
/*      */       try
/*      */       {
/* 1241 */         statementBegins();
/*      */         
/* 1243 */         clearWarnings();
/*      */         
/* 1245 */         if ((!this.batchHasPlainStatements) && (this.connection.getRewriteBatchedStatements()))
/*      */         {
/* 1247 */           if (canRewriteAsMultiValueInsertAtSqlLevel()) {
/* 1248 */             arrayOfLong = executeBatchedInserts(batchTimeout);jsr 83;return arrayOfLong;
/*      */           }
/*      */           
/* 1251 */           if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (!this.batchHasPlainStatements) && (this.batchedArgs != null) && (this.batchedArgs.size() > 3))
/*      */           {
/* 1253 */             arrayOfLong = executePreparedBatchAsMultiStatement(batchTimeout);jsr 28;return arrayOfLong;
/*      */           }
/*      */         }
/*      */         
/* 1257 */         long[] arrayOfLong = executeBatchSerially(batchTimeout);jsr 15;return arrayOfLong;
/*      */       } finally {
/* 1259 */         jsr 6; } localObject2 = returnAddress;this.statementExecuting.set(false);
/*      */       
/* 1261 */       clearBatch();ret;
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException
/*      */   {
/* 1267 */     return this.parseInfo.canRewriteAsMultiValueInsert;
/*      */   }
/*      */   
/*      */   protected int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
/* 1271 */     return this.parseInfo.locationOfOnDuplicateKeyUpdate;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String generateMultiStatementForBatch(int numBatches)
/*      */     throws SQLException
/*      */   {
/* 1451 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1452 */       StringBuilder newStatementSql = new StringBuilder((this.originalSql.length() + 1) * numBatches);
/*      */       
/* 1454 */       newStatementSql.append(this.originalSql);
/*      */       
/* 1456 */       for (int i = 0; i < numBatches - 1; i++) {
/* 1457 */         newStatementSql.append(';');
/* 1458 */         newStatementSql.append(this.originalSql);
/*      */       }
/*      */       
/* 1461 */       return newStatementSql.toString();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getValuesClause()
/*      */     throws SQLException
/*      */   {
/* 1611 */     return this.parseInfo.valuesClause;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int computeBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1622 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1623 */       long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
/*      */       
/* 1625 */       long maxSizeOfParameterSet = combinedValues[0];
/* 1626 */       long sizeOfEntireBatch = combinedValues[1];
/*      */       
/* 1628 */       int maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */       
/* 1630 */       if (sizeOfEntireBatch < maxAllowedPacket - this.originalSql.length()) {
/* 1631 */         return numBatchedArgs;
/*      */       }
/*      */       
/* 1634 */       return (int)Math.max(1L, (maxAllowedPacket - this.originalSql.length()) / maxSizeOfParameterSet);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs)
/*      */     throws SQLException
/*      */   {
/* 1645 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1646 */       long sizeOfEntireBatch = 0L;
/* 1647 */       long maxSizeOfParameterSet = 0L;
/*      */       
/* 1649 */       for (int i = 0; i < numBatchedArgs; i++) {
/* 1650 */         BatchParams paramArg = (BatchParams)this.batchedArgs.get(i);
/*      */         
/* 1652 */         boolean[] isNullBatch = paramArg.isNull;
/* 1653 */         boolean[] isStreamBatch = paramArg.isStream;
/*      */         
/* 1655 */         long sizeOfParameterSet = 0L;
/*      */         
/* 1657 */         for (int j = 0; j < isNullBatch.length; j++) {
/* 1658 */           if (isNullBatch[j] == 0)
/*      */           {
/* 1660 */             if (isStreamBatch[j] != 0) {
/* 1661 */               int streamLength = paramArg.streamLengths[j];
/*      */               
/* 1663 */               if (streamLength != -1) {
/* 1664 */                 sizeOfParameterSet += streamLength * 2;
/*      */               } else {
/* 1666 */                 int paramLength = paramArg.parameterStrings[j].length;
/* 1667 */                 sizeOfParameterSet += paramLength;
/*      */               }
/*      */             } else {
/* 1670 */               sizeOfParameterSet += paramArg.parameterStrings[j].length;
/*      */             }
/*      */           } else {
/* 1673 */             sizeOfParameterSet += 4L;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1683 */         if (getValuesClause() != null) {
/* 1684 */           sizeOfParameterSet += getValuesClause().length() + 1;
/*      */         } else {
/* 1686 */           sizeOfParameterSet += this.originalSql.length() + 1;
/*      */         }
/*      */         
/* 1689 */         sizeOfEntireBatch += sizeOfParameterSet;
/*      */         
/* 1691 */         if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 1692 */           maxSizeOfParameterSet = sizeOfParameterSet;
/*      */         }
/*      */       }
/*      */       
/* 1696 */       return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long[] executeBatchSerially(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/* 1709 */     synchronized (checkClosed().getConnectionMutex()) {
/* 1710 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 1712 */       if (locallyScopedConn == null) {
/* 1713 */         checkClosed();
/*      */       }
/*      */       
/* 1716 */       long[] updateCounts = null;
/*      */       
/* 1718 */       if (this.batchedArgs != null) {
/* 1719 */         int nbrCommands = this.batchedArgs.size();
/* 1720 */         updateCounts = new long[nbrCommands];
/*      */         
/* 1722 */         for (int i = 0; i < nbrCommands; i++) {
/* 1723 */           updateCounts[i] = -3L;
/*      */         }
/*      */         
/* 1726 */         SQLException sqlEx = null;
/*      */         
/* 1728 */         CancelTask timeoutTask = null;
/*      */         try
/*      */         {
/* 1731 */           if ((locallyScopedConn.getEnableQueryTimeouts()) && (batchTimeout != 0) && (locallyScopedConn.versionMeetsMinimum(5, 0, 0))) {
/* 1732 */             timeoutTask = new CancelTask(this, this);
/* 1733 */             locallyScopedConn.getCancelTimer().schedule(timeoutTask, batchTimeout);
/*      */           }
/*      */           
/* 1736 */           if (this.retrieveGeneratedKeys) {
/* 1737 */             this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */           }
/*      */           
/* 1740 */           for (this.batchCommandIndex = 0; this.batchCommandIndex < nbrCommands; this.batchCommandIndex += 1) {
/* 1741 */             Object arg = this.batchedArgs.get(this.batchCommandIndex);
/*      */             try
/*      */             {
/* 1744 */               if ((arg instanceof String)) {
/* 1745 */                 updateCounts[this.batchCommandIndex] = executeUpdateInternal((String)arg, true, this.retrieveGeneratedKeys);
/*      */                 
/*      */ 
/* 1748 */                 getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() == 'I') && (containsOnDuplicateKeyInString((String)arg)) ? 1 : 0);
/*      */               } else {
/* 1750 */                 BatchParams paramArg = (BatchParams)arg;
/* 1751 */                 updateCounts[this.batchCommandIndex] = executeUpdateInternal(paramArg.parameterStrings, paramArg.parameterStreams, paramArg.isStream, paramArg.streamLengths, paramArg.isNull, true);
/*      */                 
/*      */ 
/*      */ 
/* 1755 */                 getBatchedGeneratedKeys(containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
/*      */               }
/*      */             } catch (SQLException ex) {
/* 1758 */               updateCounts[this.batchCommandIndex] = -3L;
/*      */               
/* 1760 */               if ((this.continueBatchOnError) && (!(ex instanceof MySQLTimeoutException)) && (!(ex instanceof MySQLStatementCancelledException)) && (!hasDeadlockOrTimeoutRolledBackTx(ex)))
/*      */               {
/* 1762 */                 sqlEx = ex;
/*      */               } else {
/* 1764 */                 long[] newUpdateCounts = new long[this.batchCommandIndex];
/* 1765 */                 System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */                 
/* 1767 */                 throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1772 */           if (sqlEx != null) {
/* 1773 */             throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());
/*      */           }
/*      */         } catch (NullPointerException npe) {
/*      */           try {
/* 1777 */             checkClosed();
/*      */           } catch (SQLException connectionClosedEx) {
/* 1779 */             updateCounts[this.batchCommandIndex] = -3L;
/*      */             
/* 1781 */             long[] newUpdateCounts = new long[this.batchCommandIndex];
/*      */             
/* 1783 */             System.arraycopy(updateCounts, 0, newUpdateCounts, 0, this.batchCommandIndex);
/*      */             
/* 1785 */             throw SQLError.createBatchUpdateException(connectionClosedEx, newUpdateCounts, getExceptionInterceptor());
/*      */           }
/*      */           
/* 1788 */           throw npe;
/*      */         } finally {
/* 1790 */           this.batchCommandIndex = -1;
/*      */           
/* 1792 */           if (timeoutTask != null) {
/* 1793 */             timeoutTask.cancel();
/* 1794 */             locallyScopedConn.getCancelTimer().purge();
/*      */           }
/*      */           
/* 1797 */           resetCancelledState();
/*      */         }
/*      */       }
/*      */       
/* 1801 */       return updateCounts != null ? updateCounts : new long[0];
/*      */     }
/*      */   }
/*      */   
/*      */   public String getDateTime(String pattern)
/*      */   {
/* 1807 */     SimpleDateFormat sdf = new SimpleDateFormat(pattern);
/* 1808 */     return sdf.format(new java.util.Date());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 1832 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*      */       try {
/* 1835 */         resetCancelledState();
/*      */         
/* 1837 */         MySQLConnection locallyScopedConnection = this.connection;
/*      */         
/* 1839 */         this.numberOfExecutions += 1;
/*      */         
/* 1841 */         if (this.doPingInstead) {
/* 1842 */           doPingInstead();
/*      */           
/* 1844 */           return this.results;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1849 */         CancelTask timeoutTask = null;
/*      */         ResultSetInternalMethods rs;
/*      */         try {
/* 1852 */           if ((locallyScopedConnection.getEnableQueryTimeouts()) && (this.timeoutInMillis != 0) && (locallyScopedConnection.versionMeetsMinimum(5, 0, 0))) {
/* 1853 */             timeoutTask = new CancelTask(this, this);
/* 1854 */             locallyScopedConnection.getCancelTimer().schedule(timeoutTask, this.timeoutInMillis);
/*      */           }
/*      */           
/* 1857 */           if (!isBatch) {
/* 1858 */             statementBegins();
/*      */           }
/*      */           
/* 1861 */           rs = locallyScopedConnection.execSQL(this, null, maxRowsToRetrieve, sendPacket, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, metadataFromCache, isBatch);
/*      */           
/*      */ 
/* 1864 */           if (timeoutTask != null) {
/* 1865 */             timeoutTask.cancel();
/*      */             
/* 1867 */             locallyScopedConnection.getCancelTimer().purge();
/*      */             
/* 1869 */             if (timeoutTask.caughtWhileCancelling != null) {
/* 1870 */               throw timeoutTask.caughtWhileCancelling;
/*      */             }
/*      */             
/* 1873 */             timeoutTask = null;
/*      */           }
/*      */           
/* 1876 */           synchronized (this.cancelTimeoutMutex) {
/* 1877 */             if (this.wasCancelled) {
/* 1878 */               SQLException cause = null;
/*      */               
/* 1880 */               if (this.wasCancelledByTimeout) {
/* 1881 */                 cause = new MySQLTimeoutException();
/*      */               } else {
/* 1883 */                 cause = new MySQLStatementCancelledException();
/*      */               }
/*      */               
/* 1886 */               resetCancelledState();
/*      */               
/* 1888 */               throw cause;
/*      */             }
/*      */           }
/*      */         } finally {
/* 1892 */           if (!isBatch) {
/* 1893 */             this.statementExecuting.set(false);
/*      */           }
/*      */           
/* 1896 */           if (timeoutTask != null) {
/* 1897 */             timeoutTask.cancel();
/* 1898 */             locallyScopedConnection.getCancelTimer().purge();
/*      */           }
/*      */         }
/*      */         
/* 1902 */         return rs;
/*      */       } catch (NullPointerException npe) {
/* 1904 */         checkClosed();
/*      */         
/*      */ 
/* 1907 */         throw npe;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/* 1922 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 1924 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 1926 */       checkForDml(this.originalSql, this.firstCharOfStmt);
/*      */       
/* 1928 */       CachedResultSetMetaData cachedMetadata = null;
/*      */       
/* 1930 */       clearWarnings();
/*      */       
/* 1932 */       this.batchedGeneratedKeys = null;
/*      */       
/* 1934 */       setupStreamingTimeout(locallyScopedConn);
/*      */       
/* 1936 */       Buffer sendPacket = fillSendPacket();
/*      */       
/* 1938 */       implicitlyCloseAllOpenResults();
/*      */       
/* 1940 */       String oldCatalog = null;
/*      */       
/* 1942 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 1943 */         oldCatalog = locallyScopedConn.getCatalog();
/* 1944 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1950 */       if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1951 */         cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
/*      */       }
/*      */       
/* 1954 */       Field[] metadataFromCache = null;
/*      */       
/* 1956 */       if (cachedMetadata != null) {
/* 1957 */         metadataFromCache = cachedMetadata.fields;
/*      */       }
/*      */       
/* 1960 */       locallyScopedConn.setSessionMaxRows(this.maxRows);
/*      */       
/* 1962 */       this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, metadataFromCache, false);
/*      */       
/* 1964 */       if (oldCatalog != null) {
/* 1965 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */       
/* 1968 */       if (cachedMetadata != null) {
/* 1969 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */       }
/* 1971 */       else if (locallyScopedConn.getCacheResultSetMetadata()) {
/* 1972 */         locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, null, this.results);
/*      */       }
/*      */       
/*      */ 
/* 1976 */       this.lastInsertId = this.results.getUpdateID();
/*      */       
/* 1978 */       return this.results;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int executeUpdate()
/*      */     throws SQLException
/*      */   {
/* 1994 */     return Util.truncateAndConvertToInt(executeLargeUpdate());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long executeUpdateInternal(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch)
/*      */     throws SQLException
/*      */   {
/* 2003 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2004 */       if (clearBatchedGeneratedKeysAndWarnings) {
/* 2005 */         clearWarnings();
/* 2006 */         this.batchedGeneratedKeys = null;
/*      */       }
/*      */       
/* 2009 */       return executeUpdateInternal(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull, isBatch);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long executeUpdateInternal(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch)
/*      */     throws SQLException
/*      */   {
/* 2035 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 2037 */       MySQLConnection locallyScopedConn = this.connection;
/*      */       
/* 2039 */       if (locallyScopedConn.isReadOnly(false)) {
/* 2040 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2044 */       if ((this.firstCharOfStmt == 'S') && (isSelectQuery())) {
/* 2045 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), "01S03", getExceptionInterceptor());
/*      */       }
/*      */       
/* 2048 */       implicitlyCloseAllOpenResults();
/*      */       
/* 2050 */       ResultSetInternalMethods rs = null;
/*      */       
/* 2052 */       Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
/*      */       
/* 2054 */       String oldCatalog = null;
/*      */       
/* 2056 */       if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
/* 2057 */         oldCatalog = locallyScopedConn.getCatalog();
/* 2058 */         locallyScopedConn.setCatalog(this.currentCatalog);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2064 */       locallyScopedConn.setSessionMaxRows(-1);
/*      */       
/* 2066 */       boolean oldInfoMsgState = false;
/*      */       
/* 2068 */       if (this.retrieveGeneratedKeys) {
/* 2069 */         oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
/* 2070 */         locallyScopedConn.setReadInfoMsgEnabled(true);
/*      */       }
/*      */       
/* 2073 */       rs = executeInternal(-1, sendPacket, false, false, null, isReallyBatch);
/*      */       
/* 2075 */       if (this.retrieveGeneratedKeys) {
/* 2076 */         locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
/* 2077 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */       
/* 2080 */       if (oldCatalog != null) {
/* 2081 */         locallyScopedConn.setCatalog(oldCatalog);
/*      */       }
/*      */       
/* 2084 */       this.results = rs;
/*      */       
/* 2086 */       this.updateCount = rs.getUpdateCount();
/*      */       
/* 2088 */       if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate) && (
/* 2089 */         (this.updateCount == 2L) || (this.updateCount == 0L))) {
/* 2090 */         this.updateCount = 1L;
/*      */       }
/*      */       
/*      */ 
/* 2094 */       this.lastInsertId = rs.getUpdateID();
/*      */       
/* 2096 */       return this.updateCount;
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean containsOnDuplicateKeyUpdateInSQL() {
/* 2101 */     return this.parseInfo.isOnDuplicateKeyUpdate;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/* 2138 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2139 */       Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
/*      */       
/* 2141 */       sendPacket.clear();
/*      */       
/* 2143 */       sendPacket.writeByte((byte)3);
/*      */       
/* 2145 */       boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2150 */       int ensurePacketSize = 0;
/*      */       
/* 2152 */       String statementComment = this.connection.getStatementComment();
/*      */       
/* 2154 */       byte[] commentAsBytes = null;
/*      */       
/* 2156 */       if (statementComment != null) {
/* 2157 */         if (this.charConverter != null) {
/* 2158 */           commentAsBytes = this.charConverter.toBytes(statementComment);
/*      */         } else {
/* 2160 */           commentAsBytes = StringUtils.getBytes(statementComment, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/* 2164 */         ensurePacketSize += commentAsBytes.length;
/* 2165 */         ensurePacketSize += 6;
/*      */       }
/*      */       
/* 2168 */       for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2169 */         if ((batchedIsStream[i] != 0) && (useStreamLengths)) {
/* 2170 */           ensurePacketSize += batchedStreamLengths[i];
/*      */         }
/*      */       }
/*      */       
/* 2174 */       if (ensurePacketSize != 0) {
/* 2175 */         sendPacket.ensureCapacity(ensurePacketSize);
/*      */       }
/*      */       
/* 2178 */       if (commentAsBytes != null) {
/* 2179 */         sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2180 */         sendPacket.writeBytesNoNull(commentAsBytes);
/* 2181 */         sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */       }
/*      */       
/* 2184 */       for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 2185 */         checkAllParametersSet(batchedParameterStrings[i], batchedParameterStreams[i], i);
/*      */         
/* 2187 */         sendPacket.writeBytesNoNull(this.staticSqlStrings[i]);
/*      */         
/* 2189 */         if (batchedIsStream[i] != 0) {
/* 2190 */           streamToBytes(sendPacket, batchedParameterStreams[i], true, batchedStreamLengths[i], useStreamLengths);
/*      */         } else {
/* 2192 */           sendPacket.writeBytesNoNull(batchedParameterStrings[i]);
/*      */         }
/*      */       }
/*      */       
/* 2196 */       sendPacket.writeBytesNoNull(this.staticSqlStrings[batchedParameterStrings.length]);
/*      */       
/* 2198 */       return sendPacket;
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkAllParametersSet(byte[] parameterString, InputStream parameterStream, int columnIndex) throws SQLException {
/* 2203 */     if ((parameterString == null) && (parameterStream == null))
/*      */     {
/* 2205 */       throw SQLError.createSQLException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches)
/*      */     throws SQLException
/*      */   {
/* 2214 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2215 */       PreparedStatement pstmt = new PreparedStatement(localConn, "Rewritten batch of: " + this.originalSql, this.currentCatalog, this.parseInfo.getParseInfoForBatch(numBatches));
/*      */       
/* 2217 */       pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
/* 2218 */       pstmt.rewrittenBatchSize = numBatches;
/*      */       
/* 2220 */       return pstmt;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
/* 2225 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2226 */       this.retrieveGeneratedKeys = flag;
/*      */     }
/*      */   }
/*      */   
/* 2230 */   protected int rewrittenBatchSize = 0;
/*      */   
/*      */   public int getRewrittenBatchSize() {
/* 2233 */     return this.rewrittenBatchSize;
/*      */   }
/*      */   
/*      */   public String getNonRewrittenSql() throws SQLException {
/* 2237 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2238 */       int indexOfBatch = this.originalSql.indexOf(" of: ");
/*      */       
/* 2240 */       if (indexOfBatch != -1) {
/* 2241 */         return this.originalSql.substring(indexOfBatch + 5);
/*      */       }
/*      */       
/* 2244 */       return this.originalSql;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getBytesRepresentation(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 2254 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2255 */       if (this.isStream[parameterIndex] != 0) {
/* 2256 */         return streamToBytes(this.parameterStreams[parameterIndex], false, this.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */       }
/*      */       
/*      */ 
/* 2260 */       byte[] parameterVal = this.parameterValues[parameterIndex];
/*      */       
/* 2262 */       if (parameterVal == null) {
/* 2263 */         return null;
/*      */       }
/*      */       
/* 2266 */       if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39)) {
/* 2267 */         byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2268 */         System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */         
/* 2270 */         return valNoQuotes;
/*      */       }
/*      */       
/* 2273 */       return parameterVal;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] getBytesRepresentationForBatch(int parameterIndex, int commandIndex)
/*      */     throws SQLException
/*      */   {
/* 2285 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2286 */       Object batchedArg = this.batchedArgs.get(commandIndex);
/* 2287 */       if ((batchedArg instanceof String)) {
/*      */         try {
/* 2289 */           return StringUtils.getBytes((String)batchedArg, this.charEncoding);
/*      */         }
/*      */         catch (UnsupportedEncodingException uue) {
/* 2292 */           throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */         }
/*      */       }
/*      */       
/* 2296 */       BatchParams params = (BatchParams)batchedArg;
/* 2297 */       if (params.isStream[parameterIndex] != 0) {
/* 2298 */         return streamToBytes(params.parameterStreams[parameterIndex], false, params.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */       }
/*      */       
/* 2301 */       byte[] parameterVal = params.parameterStrings[parameterIndex];
/* 2302 */       if (parameterVal == null) {
/* 2303 */         return null;
/*      */       }
/*      */       
/* 2306 */       if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39)) {
/* 2307 */         byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 2308 */         System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */         
/* 2310 */         return valNoQuotes;
/*      */       }
/*      */       
/* 2313 */       return parameterVal;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final String getDateTimePattern(String dt, boolean toTime)
/*      */     throws Exception
/*      */   {
/* 2323 */     int dtLength = dt != null ? dt.length() : 0;
/*      */     
/* 2325 */     if ((dtLength >= 8) && (dtLength <= 10)) {
/* 2326 */       int dashCount = 0;
/* 2327 */       boolean isDateOnly = true;
/*      */       
/* 2329 */       for (int i = 0; i < dtLength; i++) {
/* 2330 */         char c = dt.charAt(i);
/*      */         
/* 2332 */         if ((!Character.isDigit(c)) && (c != '-')) {
/* 2333 */           isDateOnly = false;
/*      */           
/* 2335 */           break;
/*      */         }
/*      */         
/* 2338 */         if (c == '-') {
/* 2339 */           dashCount++;
/*      */         }
/*      */       }
/*      */       
/* 2343 */       if ((isDateOnly) && (dashCount == 2)) {
/* 2344 */         return "yyyy-MM-dd";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2351 */     boolean colonsOnly = true;
/*      */     
/* 2353 */     for (int i = 0; i < dtLength; i++) {
/* 2354 */       char c = dt.charAt(i);
/*      */       
/* 2356 */       if ((!Character.isDigit(c)) && (c != ':')) {
/* 2357 */         colonsOnly = false;
/*      */         
/* 2359 */         break;
/*      */       }
/*      */     }
/*      */     
/* 2363 */     if (colonsOnly) {
/* 2364 */       return "HH:mm:ss";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2373 */     StringReader reader = new StringReader(dt + " ");
/* 2374 */     ArrayList<Object[]> vec = new ArrayList();
/* 2375 */     ArrayList<Object[]> vecRemovelist = new ArrayList();
/* 2376 */     Object[] nv = new Object[3];
/*      */     
/* 2378 */     nv[0] = Character.valueOf('y');
/* 2379 */     nv[1] = new StringBuilder();
/* 2380 */     nv[2] = Integer.valueOf(0);
/* 2381 */     vec.add(nv);
/*      */     
/* 2383 */     if (toTime) {
/* 2384 */       nv = new Object[3];
/* 2385 */       nv[0] = Character.valueOf('h');
/* 2386 */       nv[1] = new StringBuilder();
/* 2387 */       nv[2] = Integer.valueOf(0);
/* 2388 */       vec.add(nv);
/*      */     }
/*      */     int z;
/* 2391 */     while ((z = reader.read()) != -1) {
/* 2392 */       char separator = (char)z;
/* 2393 */       int maxvecs = vec.size();
/*      */       
/* 2395 */       for (int count = 0; count < maxvecs; count++) {
/* 2396 */         Object[] v = (Object[])vec.get(count);
/* 2397 */         int n = ((Integer)v[2]).intValue();
/* 2398 */         char c = getSuccessor(((Character)v[0]).charValue(), n);
/*      */         
/* 2400 */         if (!Character.isLetterOrDigit(separator)) {
/* 2401 */           if ((c == ((Character)v[0]).charValue()) && (c != 'S')) {
/* 2402 */             vecRemovelist.add(v);
/*      */           } else {
/* 2404 */             ((StringBuilder)v[1]).append(separator);
/*      */             
/* 2406 */             if ((c == 'X') || (c == 'Y')) {
/* 2407 */               v[2] = Integer.valueOf(4);
/*      */             }
/*      */           }
/*      */         } else {
/* 2411 */           if (c == 'X') {
/* 2412 */             c = 'y';
/* 2413 */             nv = new Object[3];
/* 2414 */             nv[1] = new StringBuilder(((StringBuilder)v[1]).toString()).append('M');
/* 2415 */             nv[0] = Character.valueOf('M');
/* 2416 */             nv[2] = Integer.valueOf(1);
/* 2417 */             vec.add(nv);
/* 2418 */           } else if (c == 'Y') {
/* 2419 */             c = 'M';
/* 2420 */             nv = new Object[3];
/* 2421 */             nv[1] = new StringBuilder(((StringBuilder)v[1]).toString()).append('d');
/* 2422 */             nv[0] = Character.valueOf('d');
/* 2423 */             nv[2] = Integer.valueOf(1);
/* 2424 */             vec.add(nv);
/*      */           }
/*      */           
/* 2427 */           ((StringBuilder)v[1]).append(c);
/*      */           
/* 2429 */           if (c == ((Character)v[0]).charValue()) {
/* 2430 */             v[2] = Integer.valueOf(n + 1);
/*      */           } else {
/* 2432 */             v[0] = Character.valueOf(c);
/* 2433 */             v[2] = Integer.valueOf(1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2438 */       int size = vecRemovelist.size();
/*      */       
/* 2440 */       for (int i = 0; i < size; i++) {
/* 2441 */         Object[] v = (Object[])vecRemovelist.get(i);
/* 2442 */         vec.remove(v);
/*      */       }
/*      */       
/* 2445 */       vecRemovelist.clear();
/*      */     }
/*      */     
/* 2448 */     int size = vec.size();
/*      */     
/* 2450 */     for (int i = 0; i < size; i++) {
/* 2451 */       Object[] v = (Object[])vec.get(i);
/* 2452 */       char c = ((Character)v[0]).charValue();
/* 2453 */       int n = ((Integer)v[2]).intValue();
/*      */       
/* 2455 */       boolean bk = getSuccessor(c, n) != c;
/* 2456 */       boolean atEnd = ((c == 's') || (c == 'm') || ((c == 'h') && (toTime))) && (bk);
/* 2457 */       boolean finishesAtDate = (bk) && (c == 'd') && (!toTime);
/* 2458 */       boolean containsEnd = ((StringBuilder)v[1]).toString().indexOf('W') != -1;
/*      */       
/* 2460 */       if (((!atEnd) && (!finishesAtDate)) || (containsEnd)) {
/* 2461 */         vecRemovelist.add(v);
/*      */       }
/*      */     }
/*      */     
/* 2465 */     size = vecRemovelist.size();
/*      */     
/* 2467 */     for (int i = 0; i < size; i++) {
/* 2468 */       vec.remove(vecRemovelist.get(i));
/*      */     }
/*      */     
/* 2471 */     vecRemovelist.clear();
/* 2472 */     Object[] v = (Object[])vec.get(0);
/*      */     
/* 2474 */     StringBuilder format = (StringBuilder)v[1];
/* 2475 */     format.setLength(format.length() - 1);
/*      */     
/* 2477 */     return format.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2491 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2499 */       if (!isSelectQuery()) {
/* 2500 */         return null;
/*      */       }
/*      */       
/* 2503 */       PreparedStatement mdStmt = null;
/* 2504 */       ResultSet mdRs = null;
/*      */       
/* 2506 */       if (this.pstmtResultMetaData == null) {
/*      */         try {
/* 2508 */           mdStmt = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
/*      */           
/* 2510 */           mdStmt.setMaxRows(1);
/*      */           
/* 2512 */           int paramCount = this.parameterValues.length;
/*      */           
/* 2514 */           for (int i = 1; i <= paramCount; i++) {
/* 2515 */             mdStmt.setString(i, "");
/*      */           }
/*      */           
/* 2518 */           boolean hadResults = mdStmt.execute();
/*      */           
/* 2520 */           if (hadResults) {
/* 2521 */             mdRs = mdStmt.getResultSet();
/*      */             
/* 2523 */             this.pstmtResultMetaData = mdRs.getMetaData();
/*      */           } else {
/* 2525 */             this.pstmtResultMetaData = new ResultSetMetaData(new Field[0], this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         finally {
/* 2529 */           SQLException sqlExRethrow = null;
/*      */           
/* 2531 */           if (mdRs != null) {
/*      */             try {
/* 2533 */               mdRs.close();
/*      */             } catch (SQLException sqlEx) {
/* 2535 */               sqlExRethrow = sqlEx;
/*      */             }
/*      */             
/* 2538 */             mdRs = null;
/*      */           }
/*      */           
/* 2541 */           if (mdStmt != null) {
/*      */             try {
/* 2543 */               mdStmt.close();
/*      */             } catch (SQLException sqlEx) {
/* 2545 */               sqlExRethrow = sqlEx;
/*      */             }
/*      */             
/* 2548 */             mdStmt = null;
/*      */           }
/*      */           
/* 2551 */           if (sqlExRethrow != null) {
/* 2552 */             throw sqlExRethrow;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2557 */       return this.pstmtResultMetaData;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 2571 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2572 */       if (this.parameterMetaData == null) {
/* 2573 */         if (this.connection.getGenerateSimpleParameterMetadata()) {
/* 2574 */           this.parameterMetaData = new MysqlParameterMetadata(this.parameterCount);
/*      */         } else {
/* 2576 */           this.parameterMetaData = new MysqlParameterMetadata(null, this.parameterCount, getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/* 2580 */       return this.parameterMetaData;
/*      */     }
/*      */   }
/*      */   
/*      */   ParseInfo getParseInfo() {
/* 2585 */     return this.parseInfo;
/*      */   }
/*      */   
/*      */   private final char getSuccessor(char c, int n) {
/* 2589 */     return (c == 's') && (n < 2) ? 's' : c == 'm' ? 's' : (c == 'm') && (n < 2) ? 'm' : c == 'H' ? 'm' : (c == 'H') && (n < 2) ? 'H' : c == 'd' ? 'H' : (c == 'd') && (n < 2) ? 'd' : c == 'M' ? 'd' : (c == 'M') && (n < 3) ? 'M' : (c == 'M') && (n == 2) ? 'Y' : c == 'y' ? 'M' : (c == 'y') && (n < 4) ? 'y' : (c == 'y') && (n == 2) ? 'X' : 'W';
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void hexEscapeBlock(byte[] buf, Buffer packet, int size)
/*      */     throws SQLException
/*      */   {
/* 2605 */     for (int i = 0; i < size; i++) {
/* 2606 */       byte b = buf[i];
/* 2607 */       int lowBits = (b & 0xFF) / 16;
/* 2608 */       int highBits = (b & 0xFF) % 16;
/*      */       
/* 2610 */       packet.writeByte(HEX_DIGITS[lowBits]);
/* 2611 */       packet.writeByte(HEX_DIGITS[highBits]);
/*      */     }
/*      */   }
/*      */   
/*      */   private void initializeFromParseInfo() throws SQLException {
/* 2616 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2617 */       this.staticSqlStrings = this.parseInfo.staticSql;
/* 2618 */       this.isLoadDataQuery = this.parseInfo.foundLoadData;
/* 2619 */       this.firstCharOfStmt = this.parseInfo.firstStmtChar;
/*      */       
/* 2621 */       this.parameterCount = (this.staticSqlStrings.length - 1);
/*      */       
/* 2623 */       this.parameterValues = new byte[this.parameterCount][];
/* 2624 */       this.parameterStreams = new InputStream[this.parameterCount];
/* 2625 */       this.isStream = new boolean[this.parameterCount];
/* 2626 */       this.streamLengths = new int[this.parameterCount];
/* 2627 */       this.isNull = new boolean[this.parameterCount];
/* 2628 */       this.parameterTypes = new int[this.parameterCount];
/*      */       
/* 2630 */       clearParameters();
/*      */       
/* 2632 */       for (int j = 0; j < this.parameterCount; j++) {
/* 2633 */         this.isStream[j] = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final int readblock(InputStream i, byte[] b)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2646 */       return i.read(b);
/*      */     } catch (Throwable ex) {
/* 2648 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */       
/* 2650 */       sqlEx.initCause(ex);
/*      */       
/* 2652 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
/*      */     try {
/* 2658 */       int lengthToRead = length;
/*      */       
/* 2660 */       if (lengthToRead > b.length) {
/* 2661 */         lengthToRead = b.length;
/*      */       }
/*      */       
/* 2664 */       return i.read(b, 0, lengthToRead);
/*      */     } catch (Throwable ex) {
/* 2666 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), "S1000", getExceptionInterceptor());
/*      */       
/* 2668 */       sqlEx.initCause(ex);
/*      */       
/* 2670 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean closeOpenResults)
/*      */     throws SQLException
/*      */   {
/* 2685 */     MySQLConnection locallyScopedConn = this.connection;
/*      */     
/* 2687 */     if (locallyScopedConn == null) {
/* 2688 */       return;
/*      */     }
/*      */     
/* 2691 */     synchronized (locallyScopedConn.getConnectionMutex())
/*      */     {
/*      */ 
/*      */ 
/* 2695 */       if (this.isClosed) {
/* 2696 */         return;
/*      */       }
/*      */       
/* 2699 */       if ((this.useUsageAdvisor) && 
/* 2700 */         (this.numberOfExecutions <= 1)) {
/* 2701 */         String message = Messages.getString("PreparedStatement.43");
/*      */         
/* 2703 */         this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.currentCatalog, this.connectionId, getId(), -1, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2708 */       super.realClose(calledExplicitly, closeOpenResults);
/*      */       
/* 2710 */       this.dbmd = null;
/* 2711 */       this.originalSql = null;
/* 2712 */       this.staticSqlStrings = ((byte[][])null);
/* 2713 */       this.parameterValues = ((byte[][])null);
/* 2714 */       this.parameterStreams = null;
/* 2715 */       this.isStream = null;
/* 2716 */       this.streamLengths = null;
/* 2717 */       this.isNull = null;
/* 2718 */       this.streamConvertBuf = null;
/* 2719 */       this.parameterTypes = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 2736 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2761 */     if (x == null) {
/* 2762 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 2764 */       setBinaryStream(parameterIndex, x, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 2781 */     if (x == null) {
/* 2782 */       setNull(parameterIndex, 3);
/*      */     } else {
/* 2784 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
/*      */       
/* 2786 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 3;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 2810 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2811 */       if (x == null) {
/* 2812 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 2814 */         int parameterIndexOffset = getParameterIndexOffset();
/*      */         
/* 2816 */         if ((parameterIndex < 1) || (parameterIndex > this.staticSqlStrings.length)) {
/* 2817 */           throw SQLError.createSQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/* 2821 */         if ((parameterIndexOffset == -1) && (parameterIndex == 1)) {
/* 2822 */           throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/* 2826 */         this.parameterStreams[(parameterIndex - 1 + parameterIndexOffset)] = x;
/* 2827 */         this.isStream[(parameterIndex - 1 + parameterIndexOffset)] = true;
/* 2828 */         this.streamLengths[(parameterIndex - 1 + parameterIndexOffset)] = length;
/* 2829 */         this.isNull[(parameterIndex - 1 + parameterIndexOffset)] = false;
/* 2830 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2004;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/* 2836 */     setBinaryStream(parameterIndex, inputStream, (int)length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBlob(int i, Blob x)
/*      */     throws SQLException
/*      */   {
/* 2851 */     if (x == null) {
/* 2852 */       setNull(i, 2004);
/*      */     } else {
/* 2854 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */       
/* 2856 */       bytesOut.write(39);
/* 2857 */       escapeblockFast(x.getBytes(1L, (int)x.length()), bytesOut, (int)x.length());
/* 2858 */       bytesOut.write(39);
/*      */       
/* 2860 */       setInternal(i, bytesOut.toByteArray());
/*      */       
/* 2862 */       this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2004;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 2879 */     if (this.useTrueBoolean) {
/* 2880 */       setInternal(parameterIndex, x ? "1" : "0");
/*      */     } else {
/* 2882 */       setInternal(parameterIndex, x ? "'t'" : "'f'");
/*      */       
/* 2884 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 16;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 2901 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 2903 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -6;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 2920 */     setBytes(parameterIndex, x, true, true);
/*      */     
/* 2922 */     if (x != null) {
/* 2923 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) throws SQLException {
/* 2928 */     synchronized (checkClosed().getConnectionMutex()) {
/* 2929 */       if (x == null) {
/* 2930 */         setNull(parameterIndex, -2);
/*      */       } else {
/* 2932 */         String connectionEncoding = this.connection.getEncoding();
/*      */         try
/*      */         {
/* 2935 */           if ((this.connection.isNoBackslashEscapesSet()) || ((escapeForMBChars) && (this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2940 */             ByteArrayOutputStream bOut = new ByteArrayOutputStream(x.length * 2 + 3);
/* 2941 */             bOut.write(120);
/* 2942 */             bOut.write(39);
/*      */             
/* 2944 */             for (int i = 0; i < x.length; i++) {
/* 2945 */               int lowBits = (x[i] & 0xFF) / 16;
/* 2946 */               int highBits = (x[i] & 0xFF) % 16;
/*      */               
/* 2948 */               bOut.write(HEX_DIGITS[lowBits]);
/* 2949 */               bOut.write(HEX_DIGITS[highBits]);
/*      */             }
/*      */             
/* 2952 */             bOut.write(39);
/*      */             
/* 2954 */             setInternal(parameterIndex, bOut.toByteArray());
/*      */             
/* 2956 */             return;
/*      */           }
/*      */         } catch (SQLException ex) {
/* 2959 */           throw ex;
/*      */         } catch (RuntimeException ex) {
/* 2961 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 2962 */           sqlEx.initCause(ex);
/* 2963 */           throw sqlEx;
/*      */         }
/*      */         
/*      */ 
/* 2967 */         int numBytes = x.length;
/*      */         
/* 2969 */         int pad = 2;
/*      */         
/* 2971 */         boolean needsIntroducer = (checkForIntroducer) && (this.connection.versionMeetsMinimum(4, 1, 0));
/*      */         
/* 2973 */         if (needsIntroducer) {
/* 2974 */           pad += 7;
/*      */         }
/*      */         
/* 2977 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*      */         
/* 2979 */         if (needsIntroducer) {
/* 2980 */           bOut.write(95);
/* 2981 */           bOut.write(98);
/* 2982 */           bOut.write(105);
/* 2983 */           bOut.write(110);
/* 2984 */           bOut.write(97);
/* 2985 */           bOut.write(114);
/* 2986 */           bOut.write(121);
/*      */         }
/* 2988 */         bOut.write(39);
/*      */         
/* 2990 */         for (int i = 0; i < numBytes; i++) {
/* 2991 */           byte b = x[i];
/*      */           
/* 2993 */           switch (b) {
/*      */           case 0: 
/* 2995 */             bOut.write(92);
/* 2996 */             bOut.write(48);
/*      */             
/* 2998 */             break;
/*      */           
/*      */           case 10: 
/* 3001 */             bOut.write(92);
/* 3002 */             bOut.write(110);
/*      */             
/* 3004 */             break;
/*      */           
/*      */           case 13: 
/* 3007 */             bOut.write(92);
/* 3008 */             bOut.write(114);
/*      */             
/* 3010 */             break;
/*      */           
/*      */           case 92: 
/* 3013 */             bOut.write(92);
/* 3014 */             bOut.write(92);
/*      */             
/* 3016 */             break;
/*      */           
/*      */           case 39: 
/* 3019 */             bOut.write(92);
/* 3020 */             bOut.write(39);
/*      */             
/* 3022 */             break;
/*      */           
/*      */           case 34: 
/* 3025 */             bOut.write(92);
/* 3026 */             bOut.write(34);
/*      */             
/* 3028 */             break;
/*      */           
/*      */           case 26: 
/* 3031 */             bOut.write(92);
/* 3032 */             bOut.write(90);
/*      */             
/* 3034 */             break;
/*      */           
/*      */           default: 
/* 3037 */             bOut.write(b);
/*      */           }
/*      */           
/*      */         }
/* 3041 */         bOut.write(39);
/*      */         
/* 3043 */         setInternal(parameterIndex, bOut.toByteArray());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes)
/*      */     throws SQLException
/*      */   {
/* 3061 */     byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
/* 3062 */     parameterWithQuotes[0] = 39;
/* 3063 */     System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
/* 3064 */     parameterWithQuotes[(parameterAsBytes.length + 1)] = 39;
/*      */     
/* 3066 */     setInternal(parameterIndex, parameterWithQuotes);
/*      */   }
/*      */   
/*      */   protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
/* 3070 */     setInternal(parameterIndex, parameterAsBytes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 3095 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 3097 */         if (reader == null) {
/* 3098 */           setNull(parameterIndex, -1);
/*      */         } else {
/* 3100 */           char[] c = null;
/* 3101 */           int len = 0;
/*      */           
/* 3103 */           boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */           
/* 3105 */           String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */           
/* 3107 */           if ((useLength) && (length != -1)) {
/* 3108 */             c = new char[length];
/*      */             
/* 3110 */             int numCharsRead = readFully(reader, c, length);
/*      */             
/* 3112 */             if (forcedEncoding == null) {
/* 3113 */               setString(parameterIndex, new String(c, 0, numCharsRead));
/*      */             } else {
/*      */               try {
/* 3116 */                 setBytes(parameterIndex, StringUtils.getBytes(new String(c, 0, numCharsRead), forcedEncoding));
/*      */               } catch (UnsupportedEncodingException uee) {
/* 3118 */                 throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/* 3123 */             c = new char['က'];
/*      */             
/* 3125 */             StringBuilder buf = new StringBuilder();
/*      */             
/* 3127 */             while ((len = reader.read(c)) != -1) {
/* 3128 */               buf.append(c, 0, len);
/*      */             }
/*      */             
/* 3131 */             if (forcedEncoding == null) {
/* 3132 */               setString(parameterIndex, buf.toString());
/*      */             } else {
/*      */               try {
/* 3135 */                 setBytes(parameterIndex, StringUtils.getBytes(buf.toString(), forcedEncoding));
/*      */               } catch (UnsupportedEncodingException uee) {
/* 3137 */                 throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 3143 */           this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 3146 */         throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setClob(int i, Clob x)
/*      */     throws SQLException
/*      */   {
/* 3163 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3164 */       if (x == null) {
/* 3165 */         setNull(i, 2005);
/*      */       }
/*      */       else {
/* 3168 */         String forcedEncoding = this.connection.getClobCharacterEncoding();
/*      */         
/* 3170 */         if (forcedEncoding == null) {
/* 3171 */           setString(i, x.getSubString(1L, (int)x.length()));
/*      */         } else {
/*      */           try {
/* 3174 */             setBytes(i, StringUtils.getBytes(x.getSubString(1L, (int)x.length()), forcedEncoding));
/*      */           } catch (UnsupportedEncodingException uee) {
/* 3176 */             throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, "S1009", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 3181 */         this.parameterTypes[(i - 1 + getParameterIndexOffset())] = 2005;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 3199 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3217 */     if (x == null) {
/* 3218 */       setNull(parameterIndex, 91);
/*      */     }
/* 3220 */     else if (!this.useLegacyDatetimeCode) {
/* 3221 */       newSetDateInternal(parameterIndex, x, cal);
/*      */     } else {
/* 3223 */       synchronized (checkClosed().getConnectionMutex()) {
/* 3224 */         if (this.ddf == null) {
/* 3225 */           this.ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */         }
/* 3227 */         if (cal != null) {
/* 3228 */           this.ddf.setTimeZone(cal.getTimeZone());
/*      */         }
/*      */         
/* 3231 */         setInternal(parameterIndex, this.ddf.format(x));
/*      */         
/* 3233 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 91;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 3252 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3253 */       if ((!this.connection.getAllowNanAndInf()) && ((x == Double.POSITIVE_INFINITY) || (x == Double.NEGATIVE_INFINITY) || (Double.isNaN(x)))) {
/* 3254 */         throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3259 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */       
/* 3261 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 8;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 3278 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */     
/* 3280 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 6;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 3296 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3298 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 4;
/*      */   }
/*      */   
/*      */   protected final void setInternal(int paramIndex, byte[] val) throws SQLException {
/* 3302 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 3304 */       int parameterIndexOffset = getParameterIndexOffset();
/*      */       
/* 3306 */       checkBounds(paramIndex, parameterIndexOffset);
/*      */       
/* 3308 */       this.isStream[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3309 */       this.isNull[(paramIndex - 1 + parameterIndexOffset)] = false;
/* 3310 */       this.parameterStreams[(paramIndex - 1 + parameterIndexOffset)] = null;
/* 3311 */       this.parameterValues[(paramIndex - 1 + parameterIndexOffset)] = val;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
/* 3316 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3317 */       if (paramIndex < 1) {
/* 3318 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009", getExceptionInterceptor());
/*      */       }
/* 3320 */       if (paramIndex > this.parameterCount) {
/* 3321 */         throw SQLError.createSQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), "S1009", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3326 */       if ((parameterIndexOffset == -1) && (paramIndex == 1)) {
/* 3327 */         throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void setInternal(int paramIndex, String val) throws SQLException
/*      */   {
/* 3334 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 3336 */       byte[] parameterAsBytes = null;
/*      */       
/* 3338 */       if (this.charConverter != null) {
/* 3339 */         parameterAsBytes = this.charConverter.toBytes(val);
/*      */       } else {
/* 3341 */         parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 3345 */       setInternal(paramIndex, parameterAsBytes);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 3362 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3364 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -5;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 3383 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3384 */       setInternal(parameterIndex, "null");
/* 3385 */       this.isNull[(parameterIndex - 1 + getParameterIndexOffset())] = true;
/*      */       
/* 3387 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String arg)
/*      */     throws SQLException
/*      */   {
/* 3409 */     setNull(parameterIndex, sqlType);
/*      */     
/* 3411 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 0;
/*      */   }
/*      */   
/*      */   private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
/*      */     Number parameterAsNum;
/*      */     Number parameterAsNum;
/* 3417 */     if ((parameterObj instanceof Boolean)) {
/* 3418 */       parameterAsNum = ((Boolean)parameterObj).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
/* 3419 */     } else if ((parameterObj instanceof String)) { Number parameterAsNum;
/* 3420 */       switch (targetSqlType) {
/*      */       case -7:  Number parameterAsNum;
/* 3422 */         if (("1".equals(parameterObj)) || ("0".equals(parameterObj))) {
/* 3423 */           parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */         } else {
/* 3425 */           boolean parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*      */           
/* 3427 */           parameterAsNum = parameterAsBoolean ? Integer.valueOf(1) : Integer.valueOf(0);
/*      */         }
/*      */         
/* 3430 */         break;
/*      */       
/*      */       case -6: 
/*      */       case 4: 
/*      */       case 5: 
/* 3435 */         parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */         
/* 3437 */         break;
/*      */       
/*      */       case -5: 
/* 3440 */         parameterAsNum = Long.valueOf((String)parameterObj);
/*      */         
/* 3442 */         break;
/*      */       
/*      */       case 7: 
/* 3445 */         parameterAsNum = Float.valueOf((String)parameterObj);
/*      */         
/* 3447 */         break;
/*      */       
/*      */       case 6: 
/*      */       case 8: 
/* 3451 */         parameterAsNum = Double.valueOf((String)parameterObj);
/*      */         
/* 3453 */         break;
/*      */       case -4: case -3: case -2: 
/*      */       case -1: case 0: 
/*      */       case 1: case 2: 
/*      */       case 3: default: 
/* 3458 */         parameterAsNum = new BigDecimal((String)parameterObj);break;
/*      */       }
/*      */     } else {
/* 3461 */       parameterAsNum = (Number)parameterObj;
/*      */     }
/*      */     
/* 3464 */     switch (targetSqlType) {
/*      */     case -7: 
/*      */     case -6: 
/*      */     case 4: 
/*      */     case 5: 
/* 3469 */       setInt(parameterIndex, parameterAsNum.intValue());
/*      */       
/* 3471 */       break;
/*      */     
/*      */     case -5: 
/* 3474 */       setLong(parameterIndex, parameterAsNum.longValue());
/*      */       
/* 3476 */       break;
/*      */     
/*      */     case 7: 
/* 3479 */       setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */       
/* 3481 */       break;
/*      */     
/*      */     case 6: 
/*      */     case 8: 
/* 3485 */       setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */       
/* 3487 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/*      */     case 3: 
/* 3492 */       if ((parameterAsNum instanceof BigDecimal)) {
/* 3493 */         BigDecimal scaledBigDecimal = null;
/*      */         try
/*      */         {
/* 3496 */           scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try {
/* 3499 */             scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale, 4);
/*      */           } catch (ArithmeticException arEx) {
/* 3501 */             throw SQLError.createSQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", "S1009", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 3506 */         setBigDecimal(parameterIndex, scaledBigDecimal);
/* 3507 */       } else if ((parameterAsNum instanceof BigInteger)) {
/* 3508 */         setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scale));
/*      */       } else {
/* 3510 */         setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */       }
/*      */       break;
/*      */     }
/*      */   }
/*      */   
/*      */   public void setObject(int parameterIndex, Object parameterObj) throws SQLException
/*      */   {
/* 3518 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3519 */       if (parameterObj == null) {
/* 3520 */         setNull(parameterIndex, 1111);
/*      */       }
/* 3522 */       else if ((parameterObj instanceof Byte)) {
/* 3523 */         setInt(parameterIndex, ((Byte)parameterObj).intValue());
/* 3524 */       } else if ((parameterObj instanceof String)) {
/* 3525 */         setString(parameterIndex, (String)parameterObj);
/* 3526 */       } else if ((parameterObj instanceof BigDecimal)) {
/* 3527 */         setBigDecimal(parameterIndex, (BigDecimal)parameterObj);
/* 3528 */       } else if ((parameterObj instanceof Short)) {
/* 3529 */         setShort(parameterIndex, ((Short)parameterObj).shortValue());
/* 3530 */       } else if ((parameterObj instanceof Integer)) {
/* 3531 */         setInt(parameterIndex, ((Integer)parameterObj).intValue());
/* 3532 */       } else if ((parameterObj instanceof Long)) {
/* 3533 */         setLong(parameterIndex, ((Long)parameterObj).longValue());
/* 3534 */       } else if ((parameterObj instanceof Float)) {
/* 3535 */         setFloat(parameterIndex, ((Float)parameterObj).floatValue());
/* 3536 */       } else if ((parameterObj instanceof Double)) {
/* 3537 */         setDouble(parameterIndex, ((Double)parameterObj).doubleValue());
/* 3538 */       } else if ((parameterObj instanceof byte[])) {
/* 3539 */         setBytes(parameterIndex, (byte[])parameterObj);
/* 3540 */       } else if ((parameterObj instanceof java.sql.Date)) {
/* 3541 */         setDate(parameterIndex, (java.sql.Date)parameterObj);
/* 3542 */       } else if ((parameterObj instanceof Time)) {
/* 3543 */         setTime(parameterIndex, (Time)parameterObj);
/* 3544 */       } else if ((parameterObj instanceof Timestamp)) {
/* 3545 */         setTimestamp(parameterIndex, (Timestamp)parameterObj);
/* 3546 */       } else if ((parameterObj instanceof Boolean)) {
/* 3547 */         setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/* 3548 */       } else if ((parameterObj instanceof InputStream)) {
/* 3549 */         setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/* 3550 */       } else if ((parameterObj instanceof Blob)) {
/* 3551 */         setBlob(parameterIndex, (Blob)parameterObj);
/* 3552 */       } else if ((parameterObj instanceof Clob)) {
/* 3553 */         setClob(parameterIndex, (Clob)parameterObj);
/* 3554 */       } else if ((this.connection.getTreatUtilDateAsTimestamp()) && ((parameterObj instanceof java.util.Date))) {
/* 3555 */         setTimestamp(parameterIndex, new Timestamp(((java.util.Date)parameterObj).getTime()));
/* 3556 */       } else if ((parameterObj instanceof BigInteger)) {
/* 3557 */         setString(parameterIndex, parameterObj.toString());
/*      */       } else {
/* 3559 */         setSerializableObject(parameterIndex, parameterObj);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 3573 */     if (!(parameterObj instanceof BigDecimal)) {
/* 3574 */       setObject(parameterIndex, parameterObj, targetSqlType, 0);
/*      */     } else {
/* 3576 */       setObject(parameterIndex, parameterObj, targetSqlType, ((BigDecimal)parameterObj).scale());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 3608 */     synchronized (checkClosed().getConnectionMutex()) {
/* 3609 */       if (parameterObj == null) {
/* 3610 */         setNull(parameterIndex, 1111);
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/* 3616 */           switch (targetSqlType)
/*      */           {
/*      */           case 16: 
/* 3619 */             if ((parameterObj instanceof Boolean)) {
/* 3620 */               setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */ 
/*      */             }
/* 3623 */             else if ((parameterObj instanceof String)) {
/* 3624 */               setBoolean(parameterIndex, ("true".equalsIgnoreCase((String)parameterObj)) || (!"0".equalsIgnoreCase((String)parameterObj)));
/*      */ 
/*      */             }
/* 3627 */             else if ((parameterObj instanceof Number)) {
/* 3628 */               int intValue = ((Number)parameterObj).intValue();
/*      */               
/* 3630 */               setBoolean(parameterIndex, intValue != 0);
/*      */             }
/*      */             else
/*      */             {
/* 3634 */               throw SQLError.createSQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", "S1009", getExceptionInterceptor());
/*      */             }
/*      */             
/*      */ 
/*      */             break;
/*      */           case -7: 
/*      */           case -6: 
/*      */           case -5: 
/*      */           case 2: 
/*      */           case 3: 
/*      */           case 4: 
/*      */           case 5: 
/*      */           case 6: 
/*      */           case 7: 
/*      */           case 8: 
/* 3649 */             setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
/*      */             
/* 3651 */             break;
/*      */           
/*      */           case -1: 
/*      */           case 1: 
/*      */           case 12: 
/* 3656 */             if ((parameterObj instanceof BigDecimal)) {
/* 3657 */               setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal)parameterObj)));
/*      */             } else {
/* 3659 */               setString(parameterIndex, parameterObj.toString());
/*      */             }
/*      */             
/* 3662 */             break;
/*      */           
/*      */ 
/*      */           case 2005: 
/* 3666 */             if ((parameterObj instanceof Clob)) {
/* 3667 */               setClob(parameterIndex, (Clob)parameterObj);
/*      */             } else {
/* 3669 */               setString(parameterIndex, parameterObj.toString());
/*      */             }
/*      */             
/* 3672 */             break;
/*      */           
/*      */ 
/*      */           case -4: 
/*      */           case -3: 
/*      */           case -2: 
/*      */           case 2004: 
/* 3679 */             if ((parameterObj instanceof byte[])) {
/* 3680 */               setBytes(parameterIndex, (byte[])parameterObj);
/* 3681 */             } else if ((parameterObj instanceof Blob)) {
/* 3682 */               setBlob(parameterIndex, (Blob)parameterObj);
/*      */             } else {
/* 3684 */               setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */             }
/*      */             
/*      */ 
/* 3688 */             break;
/*      */           case 91: 
/*      */           case 93: 
/*      */             java.util.Date parameterAsDate;
/*      */             
/*      */             java.util.Date parameterAsDate;
/*      */             
/* 3695 */             if ((parameterObj instanceof String)) {
/* 3696 */               ParsePosition pp = new ParsePosition(0);
/* 3697 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, false), Locale.US);
/* 3698 */               parameterAsDate = sdf.parse((String)parameterObj, pp);
/*      */             } else {
/* 3700 */               parameterAsDate = (java.util.Date)parameterObj;
/*      */             }
/*      */             
/* 3703 */             switch (targetSqlType)
/*      */             {
/*      */             case 91: 
/* 3706 */               if ((parameterAsDate instanceof java.sql.Date)) {
/* 3707 */                 setDate(parameterIndex, (java.sql.Date)parameterAsDate);
/*      */               } else {
/* 3709 */                 setDate(parameterIndex, new java.sql.Date(parameterAsDate.getTime()));
/*      */               }
/*      */               
/* 3712 */               break;
/*      */             
/*      */ 
/*      */             case 93: 
/* 3716 */               if ((parameterAsDate instanceof Timestamp)) {
/* 3717 */                 setTimestamp(parameterIndex, (Timestamp)parameterAsDate);
/*      */               } else {
/* 3719 */                 setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
/*      */               }
/*      */               
/*      */               break;
/*      */             }
/*      */             
/* 3725 */             break;
/*      */           
/*      */ 
/*      */           case 92: 
/* 3729 */             if ((parameterObj instanceof String)) {
/* 3730 */               DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, true), Locale.US);
/* 3731 */               setTime(parameterIndex, new Time(sdf.parse((String)parameterObj).getTime()));
/* 3732 */             } else if ((parameterObj instanceof Timestamp)) {
/* 3733 */               Timestamp xT = (Timestamp)parameterObj;
/* 3734 */               setTime(parameterIndex, new Time(xT.getTime()));
/*      */             } else {
/* 3736 */               setTime(parameterIndex, (Time)parameterObj);
/*      */             }
/*      */             
/* 3739 */             break;
/*      */           
/*      */           case 1111: 
/* 3742 */             setSerializableObject(parameterIndex, parameterObj);
/*      */             
/* 3744 */             break;
/*      */           
/*      */           default: 
/* 3747 */             throw SQLError.createSQLException(Messages.getString("PreparedStatement.16"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */         catch (Exception ex) {
/* 3751 */           if ((ex instanceof SQLException)) {
/* 3752 */             throw ((SQLException)ex);
/*      */           }
/*      */           
/* 3755 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), "S1000", getExceptionInterceptor());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 3760 */           sqlEx.initCause(ex);
/*      */           
/* 3762 */           throw sqlEx;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
/* 3769 */     BatchParams paramArg = (BatchParams)paramSet;
/*      */     
/* 3771 */     boolean[] isNullBatch = paramArg.isNull;
/* 3772 */     boolean[] isStreamBatch = paramArg.isStream;
/*      */     
/* 3774 */     for (int j = 0; j < isNullBatch.length; j++) {
/* 3775 */       if (isNullBatch[j] != 0) {
/* 3776 */         batchedStatement.setNull(batchedParamIndex++, 0);
/*      */       }
/* 3778 */       else if (isStreamBatch[j] != 0) {
/* 3779 */         batchedStatement.setBinaryStream(batchedParamIndex++, paramArg.parameterStreams[j], paramArg.streamLengths[j]);
/*      */       } else {
/* 3781 */         ((PreparedStatement)batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex++, paramArg.parameterStrings[j]);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3786 */     return batchedParamIndex;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 3802 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void setSerializableObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3816 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 3817 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 3818 */       objectOut.writeObject(parameterObj);
/* 3819 */       objectOut.flush();
/* 3820 */       objectOut.close();
/* 3821 */       bytesOut.flush();
/* 3822 */       bytesOut.close();
/*      */       
/* 3824 */       byte[] buf = bytesOut.toByteArray();
/* 3825 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 3826 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/* 3827 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -2;
/*      */     } catch (Exception ex) {
/* 3829 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), "S1009", getExceptionInterceptor());
/*      */       
/* 3831 */       sqlEx.initCause(ex);
/*      */       
/* 3833 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 3850 */     setInternal(parameterIndex, String.valueOf(x));
/*      */     
/* 3852 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 5;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 3869 */     synchronized (checkClosed().getConnectionMutex())
/*      */     {
/* 3871 */       if (x == null) {
/* 3872 */         setNull(parameterIndex, 1);
/*      */       } else {
/* 3874 */         checkClosed();
/*      */         
/* 3876 */         int stringLength = x.length();
/*      */         
/* 3878 */         if (this.connection.isNoBackslashEscapesSet())
/*      */         {
/*      */ 
/* 3881 */           boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
/*      */           
/* 3883 */           if (!needsHexEscape) {
/* 3884 */             byte[] parameterAsBytes = null;
/*      */             
/* 3886 */             StringBuilder quotedString = new StringBuilder(x.length() + 2);
/* 3887 */             quotedString.append('\'');
/* 3888 */             quotedString.append(x);
/* 3889 */             quotedString.append('\'');
/*      */             
/* 3891 */             if (!this.isLoadDataQuery) {
/* 3892 */               parameterAsBytes = StringUtils.getBytes(quotedString.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */             }
/*      */             else
/*      */             {
/* 3896 */               parameterAsBytes = StringUtils.getBytes(quotedString.toString());
/*      */             }
/*      */             
/* 3899 */             setInternal(parameterIndex, parameterAsBytes);
/*      */           } else {
/* 3901 */             byte[] parameterAsBytes = null;
/*      */             
/* 3903 */             if (!this.isLoadDataQuery) {
/* 3904 */               parameterAsBytes = StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */             }
/*      */             else
/*      */             {
/* 3908 */               parameterAsBytes = StringUtils.getBytes(x);
/*      */             }
/*      */             
/* 3911 */             setBytes(parameterIndex, parameterAsBytes);
/*      */           }
/*      */           
/* 3914 */           return;
/*      */         }
/*      */         
/* 3917 */         String parameterAsString = x;
/* 3918 */         boolean needsQuoted = true;
/*      */         
/* 3920 */         if ((this.isLoadDataQuery) || (isEscapeNeededForString(x, stringLength))) {
/* 3921 */           needsQuoted = false;
/*      */           
/* 3923 */           StringBuilder buf = new StringBuilder((int)(x.length() * 1.1D));
/*      */           
/* 3925 */           buf.append('\'');
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3931 */           for (int i = 0; i < stringLength; i++) {
/* 3932 */             char c = x.charAt(i);
/*      */             
/* 3934 */             switch (c) {
/*      */             case '\000': 
/* 3936 */               buf.append('\\');
/* 3937 */               buf.append('0');
/*      */               
/* 3939 */               break;
/*      */             
/*      */             case '\n': 
/* 3942 */               buf.append('\\');
/* 3943 */               buf.append('n');
/*      */               
/* 3945 */               break;
/*      */             
/*      */             case '\r': 
/* 3948 */               buf.append('\\');
/* 3949 */               buf.append('r');
/*      */               
/* 3951 */               break;
/*      */             
/*      */             case '\\': 
/* 3954 */               buf.append('\\');
/* 3955 */               buf.append('\\');
/*      */               
/* 3957 */               break;
/*      */             
/*      */             case '\'': 
/* 3960 */               buf.append('\\');
/* 3961 */               buf.append('\'');
/*      */               
/* 3963 */               break;
/*      */             
/*      */             case '"': 
/* 3966 */               if (this.usingAnsiMode) {
/* 3967 */                 buf.append('\\');
/*      */               }
/*      */               
/* 3970 */               buf.append('"');
/*      */               
/* 3972 */               break;
/*      */             
/*      */             case '\032': 
/* 3975 */               buf.append('\\');
/* 3976 */               buf.append('Z');
/*      */               
/* 3978 */               break;
/*      */             
/*      */ 
/*      */             case '¥': 
/*      */             case '₩': 
/* 3983 */               if (this.charsetEncoder != null) {
/* 3984 */                 CharBuffer cbuf = CharBuffer.allocate(1);
/* 3985 */                 ByteBuffer bbuf = ByteBuffer.allocate(1);
/* 3986 */                 cbuf.put(c);
/* 3987 */                 cbuf.position(0);
/* 3988 */                 this.charsetEncoder.encode(cbuf, bbuf, true);
/* 3989 */                 if (bbuf.get(0) == 92) {
/* 3990 */                   buf.append('\\');
/*      */                 }
/*      */               }
/* 3993 */               buf.append(c);
/* 3994 */               break;
/*      */             
/*      */             default: 
/* 3997 */               buf.append(c);
/*      */             }
/*      */             
/*      */           }
/* 4001 */           buf.append('\'');
/*      */           
/* 4003 */           parameterAsString = buf.toString();
/*      */         }
/*      */         
/* 4006 */         byte[] parameterAsBytes = null;
/*      */         
/* 4008 */         if (!this.isLoadDataQuery) {
/* 4009 */           if (needsQuoted) {
/* 4010 */             parameterAsBytes = StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */           else {
/* 4013 */             parameterAsBytes = StringUtils.getBytes(parameterAsString, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/* 4018 */           parameterAsBytes = StringUtils.getBytes(parameterAsString);
/*      */         }
/*      */         
/* 4021 */         setInternal(parameterIndex, parameterAsBytes);
/*      */         
/* 4023 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 12;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isEscapeNeededForString(String x, int stringLength) {
/* 4029 */     boolean needsHexEscape = false;
/*      */     
/* 4031 */     for (int i = 0; i < stringLength; i++) {
/* 4032 */       char c = x.charAt(i);
/*      */       
/* 4034 */       switch (c)
/*      */       {
/*      */       case '\000': 
/* 4037 */         needsHexEscape = true;
/* 4038 */         break;
/*      */       
/*      */       case '\n': 
/* 4041 */         needsHexEscape = true;
/*      */         
/* 4043 */         break;
/*      */       
/*      */       case '\r': 
/* 4046 */         needsHexEscape = true;
/* 4047 */         break;
/*      */       
/*      */       case '\\': 
/* 4050 */         needsHexEscape = true;
/*      */         
/* 4052 */         break;
/*      */       
/*      */       case '\'': 
/* 4055 */         needsHexEscape = true;
/*      */         
/* 4057 */         break;
/*      */       
/*      */       case '"': 
/* 4060 */         needsHexEscape = true;
/*      */         
/* 4062 */         break;
/*      */       
/*      */       case '\032': 
/* 4065 */         needsHexEscape = true;
/*      */       }
/*      */       
/*      */       
/* 4069 */       if (needsHexEscape) {
/*      */         break;
/*      */       }
/*      */     }
/* 4073 */     return needsHexEscape;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4091 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4092 */       setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 4109 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4110 */       setTimeInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4130 */     if (x == null) {
/* 4131 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 4133 */       checkClosed();
/*      */       
/* 4135 */       if (!this.useLegacyDatetimeCode) {
/* 4136 */         newSetTimeInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4138 */         Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
/*      */         
/* 4140 */         x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         
/* 4142 */         setInternal(parameterIndex, "'" + x.toString() + "'");
/*      */       }
/*      */       
/* 4145 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 92;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 4164 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4165 */       setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 4182 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4183 */       setTimestampInternal(parameterIndex, x, null, this.connection.getDefaultTimeZone(), false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4202 */     if (x == null) {
/* 4203 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 4205 */       checkClosed();
/*      */       
/* 4207 */       if (!this.sendFractionalSeconds) {
/* 4208 */         x = TimeUtil.truncateFractionalSeconds(x);
/*      */       }
/*      */       
/* 4211 */       if (!this.useLegacyDatetimeCode) {
/* 4212 */         newSetTimestampInternal(parameterIndex, x, targetCalendar);
/*      */       } else {
/* 4214 */         Calendar sessionCalendar = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */         
/*      */ 
/* 4217 */         x = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */         
/* 4219 */         if (this.connection.getUseSSPSCompatibleTimezoneShift()) {
/* 4220 */           doSSPSCompatibleTimezoneShift(parameterIndex, x);
/*      */         } else {
/* 4222 */           synchronized (this) {
/* 4223 */             if (this.tsdf == null) {
/* 4224 */               this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*      */             }
/*      */             
/* 4227 */             StringBuffer buf = new StringBuffer();
/* 4228 */             buf.append(this.tsdf.format(x));
/*      */             
/* 4230 */             if (this.serverSupportsFracSecs) {
/* 4231 */               int nanos = x.getNanos();
/*      */               
/* 4233 */               if (nanos != 0) {
/* 4234 */                 buf.append('.');
/* 4235 */                 buf.append(TimeUtil.formatNanos(nanos, this.serverSupportsFracSecs, true));
/*      */               }
/*      */             }
/*      */             
/* 4239 */             buf.append('\'');
/*      */             
/* 4241 */             setInternal(parameterIndex, buf.toString());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 4247 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 93;
/*      */     }
/*      */   }
/*      */   
/*      */   private void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException {
/* 4252 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4253 */       if (this.tsdf == null) {
/* 4254 */         this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*      */       }
/*      */       
/* 4257 */       if (targetCalendar != null) {
/* 4258 */         this.tsdf.setTimeZone(targetCalendar.getTimeZone());
/*      */       } else {
/* 4260 */         this.tsdf.setTimeZone(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */       
/* 4263 */       StringBuffer buf = new StringBuffer();
/* 4264 */       buf.append(this.tsdf.format(x));
/* 4265 */       buf.append('.');
/* 4266 */       buf.append(TimeUtil.formatNanos(x.getNanos(), this.serverSupportsFracSecs, true));
/* 4267 */       buf.append('\'');
/*      */       
/* 4269 */       setInternal(parameterIndex, buf.toString());
/*      */     }
/*      */   }
/*      */   
/*      */   private void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar) throws SQLException {
/* 4274 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4275 */       if (this.tdf == null) {
/* 4276 */         this.tdf = new SimpleDateFormat("''HH:mm:ss''", Locale.US);
/*      */       }
/*      */       
/* 4279 */       if (targetCalendar != null) {
/* 4280 */         this.tdf.setTimeZone(targetCalendar.getTimeZone());
/*      */       } else {
/* 4282 */         this.tdf.setTimeZone(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */       
/* 4285 */       setInternal(parameterIndex, this.tdf.format(x));
/*      */     }
/*      */   }
/*      */   
/*      */   private void newSetDateInternal(int parameterIndex, java.sql.Date x, Calendar targetCalendar) throws SQLException {
/* 4290 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4291 */       if (this.ddf == null) {
/* 4292 */         this.ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */       }
/*      */       
/* 4295 */       if (targetCalendar != null) {
/* 4296 */         this.ddf.setTimeZone(targetCalendar.getTimeZone());
/* 4297 */       } else if (this.connection.getNoTimezoneConversionForDateType()) {
/* 4298 */         this.ddf.setTimeZone(this.connection.getDefaultTimeZone());
/*      */       } else {
/* 4300 */         this.ddf.setTimeZone(this.connection.getServerTimezoneTZ());
/*      */       }
/*      */       
/* 4303 */       setInternal(parameterIndex, this.ddf.format(x));
/*      */     }
/*      */   }
/*      */   
/*      */   private void doSSPSCompatibleTimezoneShift(int parameterIndex, Timestamp x) throws SQLException {
/* 4308 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4309 */       Calendar sessionCalendar2 = this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew();
/*      */       
/*      */ 
/* 4312 */       synchronized (sessionCalendar2) {
/* 4313 */         java.util.Date oldTime = sessionCalendar2.getTime();
/*      */         try
/*      */         {
/* 4316 */           sessionCalendar2.setTime(x);
/*      */           
/* 4318 */           int year = sessionCalendar2.get(1);
/* 4319 */           int month = sessionCalendar2.get(2) + 1;
/* 4320 */           int date = sessionCalendar2.get(5);
/*      */           
/* 4322 */           int hour = sessionCalendar2.get(11);
/* 4323 */           int minute = sessionCalendar2.get(12);
/* 4324 */           int seconds = sessionCalendar2.get(13);
/*      */           
/* 4326 */           StringBuilder tsBuf = new StringBuilder();
/*      */           
/* 4328 */           tsBuf.append('\'');
/* 4329 */           tsBuf.append(year);
/*      */           
/* 4331 */           tsBuf.append("-");
/*      */           
/* 4333 */           if (month < 10) {
/* 4334 */             tsBuf.append('0');
/*      */           }
/*      */           
/* 4337 */           tsBuf.append(month);
/*      */           
/* 4339 */           tsBuf.append('-');
/*      */           
/* 4341 */           if (date < 10) {
/* 4342 */             tsBuf.append('0');
/*      */           }
/*      */           
/* 4345 */           tsBuf.append(date);
/*      */           
/* 4347 */           tsBuf.append(' ');
/*      */           
/* 4349 */           if (hour < 10) {
/* 4350 */             tsBuf.append('0');
/*      */           }
/*      */           
/* 4353 */           tsBuf.append(hour);
/*      */           
/* 4355 */           tsBuf.append(':');
/*      */           
/* 4357 */           if (minute < 10) {
/* 4358 */             tsBuf.append('0');
/*      */           }
/*      */           
/* 4361 */           tsBuf.append(minute);
/*      */           
/* 4363 */           tsBuf.append(':');
/*      */           
/* 4365 */           if (seconds < 10) {
/* 4366 */             tsBuf.append('0');
/*      */           }
/*      */           
/* 4369 */           tsBuf.append(seconds);
/*      */           
/* 4371 */           tsBuf.append('.');
/* 4372 */           tsBuf.append(TimeUtil.formatNanos(x.getNanos(), this.serverSupportsFracSecs, true));
/* 4373 */           tsBuf.append('\'');
/*      */           
/* 4375 */           setInternal(parameterIndex, tsBuf.toString());
/*      */         }
/*      */         finally {
/* 4378 */           sessionCalendar2.setTime(oldTime);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 4409 */     if (x == null) {
/* 4410 */       setNull(parameterIndex, 12);
/*      */     } else {
/* 4412 */       setBinaryStream(parameterIndex, x, length);
/*      */       
/* 4414 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setURL(int parameterIndex, URL arg)
/*      */     throws SQLException
/*      */   {
/* 4422 */     if (arg != null) {
/* 4423 */       setString(parameterIndex, arg.toString());
/*      */       
/* 4425 */       this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 70;
/*      */     } else {
/* 4427 */       setNull(parameterIndex, 1);
/*      */     }
/*      */   }
/*      */   
/*      */   private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
/* 4432 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 4434 */         if (this.streamConvertBuf == null) {
/* 4435 */           this.streamConvertBuf = new byte['က'];
/*      */         }
/*      */         
/* 4438 */         String connectionEncoding = this.connection.getEncoding();
/*      */         
/* 4440 */         boolean hexEscape = false;
/*      */         try
/*      */         {
/* 4443 */           if ((this.connection.isNoBackslashEscapesSet()) || ((this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding)) && (!this.connection.parserKnowsUnicode())))
/*      */           {
/* 4445 */             hexEscape = true;
/*      */           }
/*      */         } catch (RuntimeException ex) {
/* 4448 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 4449 */           sqlEx.initCause(ex);
/* 4450 */           throw sqlEx;
/*      */         }
/*      */         
/* 4453 */         if (streamLength == -1) {
/* 4454 */           useLength = false;
/*      */         }
/*      */         
/* 4457 */         int bc = -1;
/*      */         
/* 4459 */         if (useLength) {
/* 4460 */           bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */         } else {
/* 4462 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */         
/* 4465 */         int lengthLeftToRead = streamLength - bc;
/*      */         
/* 4467 */         if (hexEscape) {
/* 4468 */           packet.writeStringNoNull("x");
/* 4469 */         } else if (this.connection.getIO().versionMeetsMinimum(4, 1, 0)) {
/* 4470 */           packet.writeStringNoNull("_binary");
/*      */         }
/*      */         
/* 4473 */         if (escape) {
/* 4474 */           packet.writeByte((byte)39);
/*      */         }
/*      */         
/* 4477 */         while (bc > 0) {
/* 4478 */           if (hexEscape) {
/* 4479 */             hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 4480 */           } else if (escape) {
/* 4481 */             escapeblockFast(this.streamConvertBuf, packet, bc);
/*      */           } else {
/* 4483 */             packet.writeBytesNoNull(this.streamConvertBuf, 0, bc);
/*      */           }
/*      */           
/* 4486 */           if (useLength) {
/* 4487 */             bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */             
/* 4489 */             if (bc > 0) {
/* 4490 */               lengthLeftToRead -= bc;
/*      */             }
/*      */           } else {
/* 4493 */             bc = readblock(in, this.streamConvertBuf);
/*      */           }
/*      */         }
/*      */         
/* 4497 */         if (escape) {
/* 4498 */           packet.writeByte((byte)39);
/*      */         }
/*      */       } finally {
/* 4501 */         if (this.connection.getAutoClosePStmtStreams()) {
/*      */           try {
/* 4503 */             in.close();
/*      */           }
/*      */           catch (IOException ioEx) {}
/*      */           
/* 4507 */           in = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException {
/* 4514 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4515 */       in.mark(Integer.MAX_VALUE);
/*      */       try {
/* 4517 */         if (this.streamConvertBuf == null) {
/* 4518 */           this.streamConvertBuf = new byte['က'];
/*      */         }
/* 4520 */         if (streamLength == -1) {
/* 4521 */           useLength = false;
/*      */         }
/*      */         
/* 4524 */         ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */         
/* 4526 */         int bc = -1;
/*      */         
/* 4528 */         if (useLength) {
/* 4529 */           bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */         } else {
/* 4531 */           bc = readblock(in, this.streamConvertBuf);
/*      */         }
/*      */         
/* 4534 */         int lengthLeftToRead = streamLength - bc;
/*      */         
/* 4536 */         if (escape) {
/* 4537 */           if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 4538 */             bytesOut.write(95);
/* 4539 */             bytesOut.write(98);
/* 4540 */             bytesOut.write(105);
/* 4541 */             bytesOut.write(110);
/* 4542 */             bytesOut.write(97);
/* 4543 */             bytesOut.write(114);
/* 4544 */             bytesOut.write(121);
/*      */           }
/*      */           
/* 4547 */           bytesOut.write(39);
/*      */         }
/*      */         
/* 4550 */         while (bc > 0) {
/* 4551 */           if (escape) {
/* 4552 */             escapeblockFast(this.streamConvertBuf, bytesOut, bc);
/*      */           } else {
/* 4554 */             bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */           }
/*      */           
/* 4557 */           if (useLength) {
/* 4558 */             bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */             
/* 4560 */             if (bc > 0) {
/* 4561 */               lengthLeftToRead -= bc;
/*      */             }
/*      */           } else {
/* 4564 */             bc = readblock(in, this.streamConvertBuf);
/*      */           }
/*      */         }
/*      */         
/* 4568 */         if (escape) {
/* 4569 */           bytesOut.write(39);
/*      */         }
/*      */         
/* 4572 */         byte[] arrayOfByte = bytesOut.toByteArray();jsr 17;return arrayOfByte;
/*      */       } finally {
/* 4574 */         jsr 6; } localObject2 = returnAddress;
/* 4575 */       try { in.reset();
/*      */       }
/*      */       catch (IOException e) {}
/* 4578 */       if (this.connection.getAutoClosePStmtStreams()) {
/*      */         try {
/* 4580 */           in.close();
/*      */         }
/*      */         catch (IOException ioEx) {}
/*      */         
/* 4584 */         in = null; } ret;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 4597 */     StringBuilder buf = new StringBuilder();
/* 4598 */     buf.append(super.toString());
/* 4599 */     buf.append(": ");
/*      */     try
/*      */     {
/* 4602 */       buf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 4604 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     }
/*      */     
/* 4607 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getParameterIndexOffset()
/*      */   {
/* 4617 */     return 0;
/*      */   }
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/* 4621 */     setAsciiStream(parameterIndex, x, -1);
/*      */   }
/*      */   
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 4625 */     setAsciiStream(parameterIndex, x, (int)length);
/* 4626 */     this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2005;
/*      */   }
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/* 4630 */     setBinaryStream(parameterIndex, x, -1);
/*      */   }
/*      */   
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/* 4634 */     setBinaryStream(parameterIndex, x, (int)length);
/*      */   }
/*      */   
/*      */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
/* 4638 */     setBinaryStream(parameterIndex, inputStream);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/* 4642 */     setCharacterStream(parameterIndex, reader, -1);
/*      */   }
/*      */   
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/* 4646 */     setCharacterStream(parameterIndex, reader, (int)length);
/*      */   }
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader) throws SQLException
/*      */   {
/* 4651 */     setCharacterStream(parameterIndex, reader);
/*      */   }
/*      */   
/*      */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
/*      */   {
/* 4656 */     setCharacterStream(parameterIndex, reader, length);
/*      */   }
/*      */   
/*      */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
/* 4660 */     setNCharacterStream(parameterIndex, value, -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 4678 */     synchronized (checkClosed().getConnectionMutex()) {
/* 4679 */       if ((this.charEncoding.equalsIgnoreCase("UTF-8")) || (this.charEncoding.equalsIgnoreCase("utf8"))) {
/* 4680 */         setString(parameterIndex, x);
/* 4681 */         return;
/*      */       }
/*      */       
/*      */ 
/* 4685 */       if (x == null) {
/* 4686 */         setNull(parameterIndex, 1);
/*      */       } else {
/* 4688 */         int stringLength = x.length();
/*      */         
/*      */ 
/*      */ 
/* 4692 */         StringBuilder buf = new StringBuilder((int)(x.length() * 1.1D + 4.0D));
/* 4693 */         buf.append("_utf8");
/* 4694 */         buf.append('\'');
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4700 */         for (int i = 0; i < stringLength; i++) {
/* 4701 */           char c = x.charAt(i);
/*      */           
/* 4703 */           switch (c) {
/*      */           case '\000': 
/* 4705 */             buf.append('\\');
/* 4706 */             buf.append('0');
/*      */             
/* 4708 */             break;
/*      */           
/*      */           case '\n': 
/* 4711 */             buf.append('\\');
/* 4712 */             buf.append('n');
/*      */             
/* 4714 */             break;
/*      */           
/*      */           case '\r': 
/* 4717 */             buf.append('\\');
/* 4718 */             buf.append('r');
/*      */             
/* 4720 */             break;
/*      */           
/*      */           case '\\': 
/* 4723 */             buf.append('\\');
/* 4724 */             buf.append('\\');
/*      */             
/* 4726 */             break;
/*      */           
/*      */           case '\'': 
/* 4729 */             buf.append('\\');
/* 4730 */             buf.append('\'');
/*      */             
/* 4732 */             break;
/*      */           
/*      */           case '"': 
/* 4735 */             if (this.usingAnsiMode) {
/* 4736 */               buf.append('\\');
/*      */             }
/*      */             
/* 4739 */             buf.append('"');
/*      */             
/* 4741 */             break;
/*      */           
/*      */           case '\032': 
/* 4744 */             buf.append('\\');
/* 4745 */             buf.append('Z');
/*      */             
/* 4747 */             break;
/*      */           
/*      */           default: 
/* 4750 */             buf.append(c);
/*      */           }
/*      */           
/*      */         }
/* 4754 */         buf.append('\'');
/*      */         
/* 4756 */         String parameterAsString = buf.toString();
/*      */         
/* 4758 */         byte[] parameterAsBytes = null;
/*      */         
/* 4760 */         if (!this.isLoadDataQuery) {
/* 4761 */           parameterAsBytes = StringUtils.getBytes(parameterAsString, this.connection.getCharsetConverter("UTF-8"), "UTF-8", this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */         }
/*      */         else
/*      */         {
/* 4765 */           parameterAsBytes = StringUtils.getBytes(parameterAsString);
/*      */         }
/*      */         
/* 4768 */         setInternal(parameterIndex, parameterAsBytes);
/*      */         
/* 4770 */         this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = -9;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNCharacterStream(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 4797 */     synchronized (checkClosed().getConnectionMutex()) {
/*      */       try {
/* 4799 */         if (reader == null) {
/* 4800 */           setNull(parameterIndex, -1);
/*      */         }
/*      */         else {
/* 4803 */           char[] c = null;
/* 4804 */           int len = 0;
/*      */           
/* 4806 */           boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */           
/*      */ 
/*      */ 
/* 4810 */           if ((useLength) && (length != -1L)) {
/* 4811 */             c = new char[(int)length];
/*      */             
/* 4813 */             int numCharsRead = readFully(reader, c, (int)length);
/* 4814 */             setNString(parameterIndex, new String(c, 0, numCharsRead));
/*      */           }
/*      */           else {
/* 4817 */             c = new char['က'];
/*      */             
/* 4819 */             StringBuilder buf = new StringBuilder();
/*      */             
/* 4821 */             while ((len = reader.read(c)) != -1) {
/* 4822 */               buf.append(c, 0, len);
/*      */             }
/*      */             
/* 4825 */             setNString(parameterIndex, buf.toString());
/*      */           }
/*      */           
/* 4828 */           this.parameterTypes[(parameterIndex - 1 + getParameterIndexOffset())] = 2011;
/*      */         }
/*      */       } catch (IOException ioEx) {
/* 4831 */         throw SQLError.createSQLException(ioEx.toString(), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/* 4837 */     setNCharacterStream(parameterIndex, reader);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNClob(int parameterIndex, Reader reader, long length)
/*      */     throws SQLException
/*      */   {
/* 4854 */     if (reader == null) {
/* 4855 */       setNull(parameterIndex, -1);
/*      */     } else {
/* 4857 */       setNCharacterStream(parameterIndex, reader, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class EmulatedPreparedStatementBindings
/*      */     implements ParameterBindings
/*      */   {
/*      */     private ResultSetImpl bindingsAsRs;
/*      */     
/*      */     private boolean[] parameterIsNull;
/*      */     
/*      */ 
/*      */     EmulatedPreparedStatementBindings()
/*      */       throws SQLException
/*      */     {
/* 4873 */       List<ResultSetRow> rows = new ArrayList();
/* 4874 */       this.parameterIsNull = new boolean[PreparedStatement.this.parameterCount];
/* 4875 */       System.arraycopy(PreparedStatement.this.isNull, 0, this.parameterIsNull, 0, PreparedStatement.this.parameterCount);
/* 4876 */       byte[][] rowData = new byte[PreparedStatement.this.parameterCount][];
/* 4877 */       Field[] typeMetadata = new Field[PreparedStatement.this.parameterCount];
/*      */       
/* 4879 */       for (int i = 0; i < PreparedStatement.this.parameterCount; i++) {
/* 4880 */         if (PreparedStatement.this.batchCommandIndex == -1) {
/* 4881 */           rowData[i] = PreparedStatement.this.getBytesRepresentation(i);
/*      */         } else {
/* 4883 */           rowData[i] = PreparedStatement.this.getBytesRepresentationForBatch(i, PreparedStatement.this.batchCommandIndex);
/*      */         }
/*      */         
/* 4886 */         int charsetIndex = 0;
/*      */         
/* 4888 */         if ((PreparedStatement.this.parameterTypes[i] == -2) || (PreparedStatement.this.parameterTypes[i] == 2004)) {
/* 4889 */           charsetIndex = 63;
/*      */         } else {
/*      */           try {
/* 4892 */             charsetIndex = CharsetMapping.getCollationIndexForJavaEncoding(PreparedStatement.this.connection.getEncoding(), PreparedStatement.this.connection);
/*      */           }
/*      */           catch (SQLException ex) {
/* 4895 */             throw ex;
/*      */           } catch (RuntimeException ex) {
/* 4897 */             SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 4898 */             sqlEx.initCause(ex);
/* 4899 */             throw sqlEx;
/*      */           }
/*      */         }
/*      */         
/* 4903 */         Field parameterMetadata = new Field(null, "parameter_" + (i + 1), charsetIndex, PreparedStatement.this.parameterTypes[i], rowData[i].length);
/* 4904 */         parameterMetadata.setConnection(PreparedStatement.this.connection);
/* 4905 */         typeMetadata[i] = parameterMetadata;
/*      */       }
/*      */       
/* 4908 */       rows.add(new ByteArrayRow(rowData, PreparedStatement.this.getExceptionInterceptor()));
/*      */       
/* 4910 */       this.bindingsAsRs = new ResultSetImpl(PreparedStatement.this.connection.getCatalog(), typeMetadata, new RowDataStatic(rows), PreparedStatement.this.connection, null);
/*      */       
/* 4912 */       this.bindingsAsRs.next();
/*      */     }
/*      */     
/*      */     public Array getArray(int parameterIndex) throws SQLException {
/* 4916 */       return this.bindingsAsRs.getArray(parameterIndex);
/*      */     }
/*      */     
/*      */     public InputStream getAsciiStream(int parameterIndex) throws SQLException {
/* 4920 */       return this.bindingsAsRs.getAsciiStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
/* 4924 */       return this.bindingsAsRs.getBigDecimal(parameterIndex);
/*      */     }
/*      */     
/*      */     public InputStream getBinaryStream(int parameterIndex) throws SQLException {
/* 4928 */       return this.bindingsAsRs.getBinaryStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Blob getBlob(int parameterIndex) throws SQLException {
/* 4932 */       return this.bindingsAsRs.getBlob(parameterIndex);
/*      */     }
/*      */     
/*      */     public boolean getBoolean(int parameterIndex) throws SQLException {
/* 4936 */       return this.bindingsAsRs.getBoolean(parameterIndex);
/*      */     }
/*      */     
/*      */     public byte getByte(int parameterIndex) throws SQLException {
/* 4940 */       return this.bindingsAsRs.getByte(parameterIndex);
/*      */     }
/*      */     
/*      */     public byte[] getBytes(int parameterIndex) throws SQLException {
/* 4944 */       return this.bindingsAsRs.getBytes(parameterIndex);
/*      */     }
/*      */     
/*      */     public Reader getCharacterStream(int parameterIndex) throws SQLException {
/* 4948 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Clob getClob(int parameterIndex) throws SQLException {
/* 4952 */       return this.bindingsAsRs.getClob(parameterIndex);
/*      */     }
/*      */     
/*      */     public java.sql.Date getDate(int parameterIndex) throws SQLException {
/* 4956 */       return this.bindingsAsRs.getDate(parameterIndex);
/*      */     }
/*      */     
/*      */     public double getDouble(int parameterIndex) throws SQLException {
/* 4960 */       return this.bindingsAsRs.getDouble(parameterIndex);
/*      */     }
/*      */     
/*      */     public float getFloat(int parameterIndex) throws SQLException {
/* 4964 */       return this.bindingsAsRs.getFloat(parameterIndex);
/*      */     }
/*      */     
/*      */     public int getInt(int parameterIndex) throws SQLException {
/* 4968 */       return this.bindingsAsRs.getInt(parameterIndex);
/*      */     }
/*      */     
/*      */     public long getLong(int parameterIndex) throws SQLException {
/* 4972 */       return this.bindingsAsRs.getLong(parameterIndex);
/*      */     }
/*      */     
/*      */     public Reader getNCharacterStream(int parameterIndex) throws SQLException {
/* 4976 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Reader getNClob(int parameterIndex) throws SQLException {
/* 4980 */       return this.bindingsAsRs.getCharacterStream(parameterIndex);
/*      */     }
/*      */     
/*      */     public Object getObject(int parameterIndex) throws SQLException {
/* 4984 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */       
/* 4986 */       if (this.parameterIsNull[(parameterIndex - 1)] != 0) {
/* 4987 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4992 */       switch (PreparedStatement.this.parameterTypes[(parameterIndex - 1)]) {
/*      */       case -6: 
/* 4994 */         return Byte.valueOf(getByte(parameterIndex));
/*      */       case 5: 
/* 4996 */         return Short.valueOf(getShort(parameterIndex));
/*      */       case 4: 
/* 4998 */         return Integer.valueOf(getInt(parameterIndex));
/*      */       case -5: 
/* 5000 */         return Long.valueOf(getLong(parameterIndex));
/*      */       case 6: 
/* 5002 */         return Float.valueOf(getFloat(parameterIndex));
/*      */       case 8: 
/* 5004 */         return Double.valueOf(getDouble(parameterIndex));
/*      */       }
/* 5006 */       return this.bindingsAsRs.getObject(parameterIndex);
/*      */     }
/*      */     
/*      */     public Ref getRef(int parameterIndex) throws SQLException
/*      */     {
/* 5011 */       return this.bindingsAsRs.getRef(parameterIndex);
/*      */     }
/*      */     
/*      */     public short getShort(int parameterIndex) throws SQLException {
/* 5015 */       return this.bindingsAsRs.getShort(parameterIndex);
/*      */     }
/*      */     
/*      */     public String getString(int parameterIndex) throws SQLException {
/* 5019 */       return this.bindingsAsRs.getString(parameterIndex);
/*      */     }
/*      */     
/*      */     public Time getTime(int parameterIndex) throws SQLException {
/* 5023 */       return this.bindingsAsRs.getTime(parameterIndex);
/*      */     }
/*      */     
/*      */     public Timestamp getTimestamp(int parameterIndex) throws SQLException {
/* 5027 */       return this.bindingsAsRs.getTimestamp(parameterIndex);
/*      */     }
/*      */     
/*      */     public URL getURL(int parameterIndex) throws SQLException {
/* 5031 */       return this.bindingsAsRs.getURL(parameterIndex);
/*      */     }
/*      */     
/*      */     public boolean isNull(int parameterIndex) throws SQLException {
/* 5035 */       PreparedStatement.this.checkBounds(parameterIndex, 0);
/*      */       
/* 5037 */       return this.parameterIsNull[(parameterIndex - 1)];
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 5061 */     int count = super.getUpdateCount();
/*      */     
/* 5063 */     if ((containsOnDuplicateKeyUpdateInSQL()) && (this.compensateForOnDuplicateKeyUpdate) && (
/* 5064 */       (count == 2) || (count == 0))) {
/* 5065 */       count = 1;
/*      */     }
/*      */     
/*      */ 
/* 5069 */     return count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos)
/*      */   {
/* 5076 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) {
/* 5077 */       if (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) {
/* 5078 */         return false;
/*      */       }
/* 5080 */       if (isOnDuplicateKeyUpdate) {
/* 5081 */         int updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ");
/* 5082 */         if (updateClausePos != -1) {
/* 5083 */           return StringUtils.indexOfIgnoreCase(updateClausePos, sql, "LAST_INSERT_ID", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) == -1;
/*      */         }
/*      */       }
/* 5086 */       return true;
/*      */     }
/*      */     
/* 5089 */     return (StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementStartPos)) && (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) == -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long executeLargeUpdate()
/*      */     throws SQLException
/*      */   {
/* 5098 */     return executeUpdateInternal(true, false);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected long[] executePreparedBatchAsMultiStatement(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_2
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 149	com/mysql/jdbc/PreparedStatement:batchedValuesClause	Ljava/lang/String;
/*      */     //   16: ifnonnull +29 -> 45
/*      */     //   19: aload_0
/*      */     //   20: new 73	java/lang/StringBuilder
/*      */     //   23: dup
/*      */     //   24: invokespecial 74	java/lang/StringBuilder:<init>	()V
/*      */     //   27: aload_0
/*      */     //   28: getfield 21	com/mysql/jdbc/PreparedStatement:originalSql	Ljava/lang/String;
/*      */     //   31: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   34: ldc -106
/*      */     //   36: invokevirtual 79	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   39: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   42: putfield 149	com/mysql/jdbc/PreparedStatement:batchedValuesClause	Ljava/lang/String;
/*      */     //   45: aload_0
/*      */     //   46: getfield 35	com/mysql/jdbc/PreparedStatement:connection	Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   49: astore_3
/*      */     //   50: aload_3
/*      */     //   51: invokeinterface 151 1 0
/*      */     //   56: istore 4
/*      */     //   58: aconst_null
/*      */     //   59: astore 5
/*      */     //   61: aload_0
/*      */     //   62: invokevirtual 108	com/mysql/jdbc/PreparedStatement:clearWarnings	()V
/*      */     //   65: aload_0
/*      */     //   66: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   69: invokeinterface 135 1 0
/*      */     //   74: istore 6
/*      */     //   76: aload_0
/*      */     //   77: getfield 106	com/mysql/jdbc/PreparedStatement:retrieveGeneratedKeys	Z
/*      */     //   80: ifeq +16 -> 96
/*      */     //   83: aload_0
/*      */     //   84: new 65	java/util/ArrayList
/*      */     //   87: dup
/*      */     //   88: iload 6
/*      */     //   90: invokespecial 152	java/util/ArrayList:<init>	(I)V
/*      */     //   93: putfield 110	com/mysql/jdbc/PreparedStatement:batchedGeneratedKeys	Ljava/util/ArrayList;
/*      */     //   96: aload_0
/*      */     //   97: iload 6
/*      */     //   99: invokevirtual 153	com/mysql/jdbc/PreparedStatement:computeBatchSize	(I)I
/*      */     //   102: istore 7
/*      */     //   104: iload 6
/*      */     //   106: iload 7
/*      */     //   108: if_icmpge +7 -> 115
/*      */     //   111: iload 6
/*      */     //   113: istore 7
/*      */     //   115: aconst_null
/*      */     //   116: astore 8
/*      */     //   118: iconst_1
/*      */     //   119: istore 9
/*      */     //   121: iconst_0
/*      */     //   122: istore 10
/*      */     //   124: iconst_0
/*      */     //   125: istore 11
/*      */     //   127: iconst_0
/*      */     //   128: istore 12
/*      */     //   130: iload 6
/*      */     //   132: newarray <illegal type>
/*      */     //   134: astore 13
/*      */     //   136: aconst_null
/*      */     //   137: astore 14
/*      */     //   139: iload 4
/*      */     //   141: ifne +12 -> 153
/*      */     //   144: aload_3
/*      */     //   145: invokeinterface 154 1 0
/*      */     //   150: invokevirtual 155	com/mysql/jdbc/MysqlIO:enableMultiQueries	()V
/*      */     //   153: aload_0
/*      */     //   154: getfield 106	com/mysql/jdbc/PreparedStatement:retrieveGeneratedKeys	Z
/*      */     //   157: ifeq +35 -> 192
/*      */     //   160: aload_3
/*      */     //   161: aload_0
/*      */     //   162: iload 7
/*      */     //   164: invokespecial 156	com/mysql/jdbc/PreparedStatement:generateMultiStatementForBatch	(I)Ljava/lang/String;
/*      */     //   167: iconst_1
/*      */     //   168: invokeinterface 157 3 0
/*      */     //   173: checkcast 158	com/mysql/jdbc/Wrapper
/*      */     //   176: ldc_w 159
/*      */     //   179: invokeinterface 160 2 0
/*      */     //   184: checkcast 159	java/sql/PreparedStatement
/*      */     //   187: astore 8
/*      */     //   189: goto +31 -> 220
/*      */     //   192: aload_3
/*      */     //   193: aload_0
/*      */     //   194: iload 7
/*      */     //   196: invokespecial 156	com/mysql/jdbc/PreparedStatement:generateMultiStatementForBatch	(I)Ljava/lang/String;
/*      */     //   199: invokeinterface 161 2 0
/*      */     //   204: checkcast 158	com/mysql/jdbc/Wrapper
/*      */     //   207: ldc_w 159
/*      */     //   210: invokeinterface 160 2 0
/*      */     //   215: checkcast 159	java/sql/PreparedStatement
/*      */     //   218: astore 8
/*      */     //   220: aload_3
/*      */     //   221: invokeinterface 162 1 0
/*      */     //   226: ifeq +47 -> 273
/*      */     //   229: iload_1
/*      */     //   230: ifeq +43 -> 273
/*      */     //   233: aload_3
/*      */     //   234: iconst_5
/*      */     //   235: iconst_0
/*      */     //   236: iconst_0
/*      */     //   237: invokeinterface 37 4 0
/*      */     //   242: ifeq +31 -> 273
/*      */     //   245: new 163	com/mysql/jdbc/StatementImpl$CancelTask
/*      */     //   248: dup
/*      */     //   249: aload_0
/*      */     //   250: aload 8
/*      */     //   252: checkcast 164	com/mysql/jdbc/StatementImpl
/*      */     //   255: invokespecial 165	com/mysql/jdbc/StatementImpl$CancelTask:<init>	(Lcom/mysql/jdbc/StatementImpl;Lcom/mysql/jdbc/StatementImpl;)V
/*      */     //   258: astore 5
/*      */     //   260: aload_3
/*      */     //   261: invokeinterface 166 1 0
/*      */     //   266: aload 5
/*      */     //   268: iload_1
/*      */     //   269: i2l
/*      */     //   270: invokevirtual 167	java/util/Timer:schedule	(Ljava/util/TimerTask;J)V
/*      */     //   273: iload 6
/*      */     //   275: iload 7
/*      */     //   277: if_icmpge +10 -> 287
/*      */     //   280: iload 6
/*      */     //   282: istore 10
/*      */     //   284: goto +10 -> 294
/*      */     //   287: iload 6
/*      */     //   289: iload 7
/*      */     //   291: idiv
/*      */     //   292: istore 10
/*      */     //   294: iload 10
/*      */     //   296: iload 7
/*      */     //   298: imul
/*      */     //   299: istore 15
/*      */     //   301: iconst_0
/*      */     //   302: istore 16
/*      */     //   304: iload 16
/*      */     //   306: iload 15
/*      */     //   308: if_icmpge +98 -> 406
/*      */     //   311: iload 16
/*      */     //   313: ifeq +63 -> 376
/*      */     //   316: iload 16
/*      */     //   318: iload 7
/*      */     //   320: irem
/*      */     //   321: ifne +55 -> 376
/*      */     //   324: aload 8
/*      */     //   326: invokeinterface 168 1 0
/*      */     //   331: pop
/*      */     //   332: goto +19 -> 351
/*      */     //   335: astore 17
/*      */     //   337: aload_0
/*      */     //   338: iload 11
/*      */     //   340: iload 7
/*      */     //   342: aload 13
/*      */     //   344: aload 17
/*      */     //   346: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   349: astore 14
/*      */     //   351: aload_0
/*      */     //   352: aload 8
/*      */     //   354: checkcast 164	com/mysql/jdbc/StatementImpl
/*      */     //   357: iload 12
/*      */     //   359: aload 13
/*      */     //   361: invokevirtual 170	com/mysql/jdbc/PreparedStatement:processMultiCountsAndKeys	(Lcom/mysql/jdbc/StatementImpl;I[J)I
/*      */     //   364: istore 12
/*      */     //   366: aload 8
/*      */     //   368: invokeinterface 171 1 0
/*      */     //   373: iconst_1
/*      */     //   374: istore 9
/*      */     //   376: aload_0
/*      */     //   377: aload 8
/*      */     //   379: iload 9
/*      */     //   381: aload_0
/*      */     //   382: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   385: iload 11
/*      */     //   387: iinc 11 1
/*      */     //   390: invokeinterface 77 2 0
/*      */     //   395: invokevirtual 172	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   398: istore 9
/*      */     //   400: iinc 16 1
/*      */     //   403: goto -99 -> 304
/*      */     //   406: aload 8
/*      */     //   408: invokeinterface 168 1 0
/*      */     //   413: pop
/*      */     //   414: goto +21 -> 435
/*      */     //   417: astore 16
/*      */     //   419: aload_0
/*      */     //   420: iload 11
/*      */     //   422: iconst_1
/*      */     //   423: isub
/*      */     //   424: iload 7
/*      */     //   426: aload 13
/*      */     //   428: aload 16
/*      */     //   430: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   433: astore 14
/*      */     //   435: aload_0
/*      */     //   436: aload 8
/*      */     //   438: checkcast 164	com/mysql/jdbc/StatementImpl
/*      */     //   441: iload 12
/*      */     //   443: aload 13
/*      */     //   445: invokevirtual 170	com/mysql/jdbc/PreparedStatement:processMultiCountsAndKeys	(Lcom/mysql/jdbc/StatementImpl;I[J)I
/*      */     //   448: istore 12
/*      */     //   450: aload 8
/*      */     //   452: invokeinterface 171 1 0
/*      */     //   457: iload 6
/*      */     //   459: iload 11
/*      */     //   461: isub
/*      */     //   462: istore 7
/*      */     //   464: jsr +14 -> 478
/*      */     //   467: goto +30 -> 497
/*      */     //   470: astore 18
/*      */     //   472: jsr +6 -> 478
/*      */     //   475: aload 18
/*      */     //   477: athrow
/*      */     //   478: astore 19
/*      */     //   480: aload 8
/*      */     //   482: ifnull +13 -> 495
/*      */     //   485: aload 8
/*      */     //   487: invokeinterface 173 1 0
/*      */     //   492: aconst_null
/*      */     //   493: astore 8
/*      */     //   495: ret 19
/*      */     //   497: iload 7
/*      */     //   499: ifle +145 -> 644
/*      */     //   502: aload_0
/*      */     //   503: getfield 106	com/mysql/jdbc/PreparedStatement:retrieveGeneratedKeys	Z
/*      */     //   506: ifeq +21 -> 527
/*      */     //   509: aload_3
/*      */     //   510: aload_0
/*      */     //   511: iload 7
/*      */     //   513: invokespecial 156	com/mysql/jdbc/PreparedStatement:generateMultiStatementForBatch	(I)Ljava/lang/String;
/*      */     //   516: iconst_1
/*      */     //   517: invokeinterface 157 3 0
/*      */     //   522: astore 8
/*      */     //   524: goto +17 -> 541
/*      */     //   527: aload_3
/*      */     //   528: aload_0
/*      */     //   529: iload 7
/*      */     //   531: invokespecial 156	com/mysql/jdbc/PreparedStatement:generateMultiStatementForBatch	(I)Ljava/lang/String;
/*      */     //   534: invokeinterface 161 2 0
/*      */     //   539: astore 8
/*      */     //   541: aload 5
/*      */     //   543: ifnull +13 -> 556
/*      */     //   546: aload 5
/*      */     //   548: aload 8
/*      */     //   550: checkcast 164	com/mysql/jdbc/StatementImpl
/*      */     //   553: putfield 174	com/mysql/jdbc/StatementImpl$CancelTask:toCancel	Lcom/mysql/jdbc/StatementImpl;
/*      */     //   556: iconst_1
/*      */     //   557: istore 9
/*      */     //   559: iload 11
/*      */     //   561: iload 6
/*      */     //   563: if_icmpge +30 -> 593
/*      */     //   566: aload_0
/*      */     //   567: aload 8
/*      */     //   569: iload 9
/*      */     //   571: aload_0
/*      */     //   572: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   575: iload 11
/*      */     //   577: iinc 11 1
/*      */     //   580: invokeinterface 77 2 0
/*      */     //   585: invokevirtual 172	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   588: istore 9
/*      */     //   590: goto -31 -> 559
/*      */     //   593: aload 8
/*      */     //   595: invokeinterface 168 1 0
/*      */     //   600: pop
/*      */     //   601: goto +21 -> 622
/*      */     //   604: astore 15
/*      */     //   606: aload_0
/*      */     //   607: iload 11
/*      */     //   609: iconst_1
/*      */     //   610: isub
/*      */     //   611: iload 7
/*      */     //   613: aload 13
/*      */     //   615: aload 15
/*      */     //   617: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   620: astore 14
/*      */     //   622: aload_0
/*      */     //   623: aload 8
/*      */     //   625: checkcast 164	com/mysql/jdbc/StatementImpl
/*      */     //   628: iload 12
/*      */     //   630: aload 13
/*      */     //   632: invokevirtual 170	com/mysql/jdbc/PreparedStatement:processMultiCountsAndKeys	(Lcom/mysql/jdbc/StatementImpl;I[J)I
/*      */     //   635: istore 12
/*      */     //   637: aload 8
/*      */     //   639: invokeinterface 171 1 0
/*      */     //   644: aload 5
/*      */     //   646: ifnull +36 -> 682
/*      */     //   649: aload 5
/*      */     //   651: getfield 175	com/mysql/jdbc/StatementImpl$CancelTask:caughtWhileCancelling	Ljava/sql/SQLException;
/*      */     //   654: ifnull +9 -> 663
/*      */     //   657: aload 5
/*      */     //   659: getfield 175	com/mysql/jdbc/StatementImpl$CancelTask:caughtWhileCancelling	Ljava/sql/SQLException;
/*      */     //   662: athrow
/*      */     //   663: aload 5
/*      */     //   665: invokevirtual 176	com/mysql/jdbc/StatementImpl$CancelTask:cancel	()Z
/*      */     //   668: pop
/*      */     //   669: aload_3
/*      */     //   670: invokeinterface 166 1 0
/*      */     //   675: invokevirtual 177	java/util/Timer:purge	()I
/*      */     //   678: pop
/*      */     //   679: aconst_null
/*      */     //   680: astore 5
/*      */     //   682: aload 14
/*      */     //   684: ifnull +15 -> 699
/*      */     //   687: aload 14
/*      */     //   689: aload 13
/*      */     //   691: aload_0
/*      */     //   692: invokevirtual 42	com/mysql/jdbc/PreparedStatement:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   695: invokestatic 178	com/mysql/jdbc/SQLError:createBatchUpdateException	(Ljava/sql/SQLException;[JLcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   698: athrow
/*      */     //   699: aload 13
/*      */     //   701: astore 15
/*      */     //   703: jsr +19 -> 722
/*      */     //   706: jsr +40 -> 746
/*      */     //   709: aload_2
/*      */     //   710: monitorexit
/*      */     //   711: aload 15
/*      */     //   713: areturn
/*      */     //   714: astore 20
/*      */     //   716: jsr +6 -> 722
/*      */     //   719: aload 20
/*      */     //   721: athrow
/*      */     //   722: astore 21
/*      */     //   724: aload 8
/*      */     //   726: ifnull +10 -> 736
/*      */     //   729: aload 8
/*      */     //   731: invokeinterface 173 1 0
/*      */     //   736: ret 21
/*      */     //   738: astore 22
/*      */     //   740: jsr +6 -> 746
/*      */     //   743: aload 22
/*      */     //   745: athrow
/*      */     //   746: astore 23
/*      */     //   748: aload 5
/*      */     //   750: ifnull +19 -> 769
/*      */     //   753: aload 5
/*      */     //   755: invokevirtual 176	com/mysql/jdbc/StatementImpl$CancelTask:cancel	()Z
/*      */     //   758: pop
/*      */     //   759: aload_3
/*      */     //   760: invokeinterface 166 1 0
/*      */     //   765: invokevirtual 177	java/util/Timer:purge	()I
/*      */     //   768: pop
/*      */     //   769: aload_0
/*      */     //   770: invokevirtual 137	com/mysql/jdbc/PreparedStatement:resetCancelledState	()V
/*      */     //   773: iload 4
/*      */     //   775: ifne +12 -> 787
/*      */     //   778: aload_3
/*      */     //   779: invokeinterface 154 1 0
/*      */     //   784: invokevirtual 179	com/mysql/jdbc/MysqlIO:disableMultiQueries	()V
/*      */     //   787: aload_0
/*      */     //   788: invokevirtual 146	com/mysql/jdbc/PreparedStatement:clearBatch	()V
/*      */     //   791: ret 23
/*      */     //   793: astore 24
/*      */     //   795: aload_2
/*      */     //   796: monitorexit
/*      */     //   797: aload 24
/*      */     //   799: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1285	-> byte code offset #0
/*      */     //   Java source line #1287	-> byte code offset #12
/*      */     //   Java source line #1288	-> byte code offset #19
/*      */     //   Java source line #1291	-> byte code offset #45
/*      */     //   Java source line #1293	-> byte code offset #50
/*      */     //   Java source line #1294	-> byte code offset #58
/*      */     //   Java source line #1297	-> byte code offset #61
/*      */     //   Java source line #1299	-> byte code offset #65
/*      */     //   Java source line #1301	-> byte code offset #76
/*      */     //   Java source line #1302	-> byte code offset #83
/*      */     //   Java source line #1305	-> byte code offset #96
/*      */     //   Java source line #1307	-> byte code offset #104
/*      */     //   Java source line #1308	-> byte code offset #111
/*      */     //   Java source line #1311	-> byte code offset #115
/*      */     //   Java source line #1313	-> byte code offset #118
/*      */     //   Java source line #1314	-> byte code offset #121
/*      */     //   Java source line #1315	-> byte code offset #124
/*      */     //   Java source line #1316	-> byte code offset #127
/*      */     //   Java source line #1317	-> byte code offset #130
/*      */     //   Java source line #1318	-> byte code offset #136
/*      */     //   Java source line #1321	-> byte code offset #139
/*      */     //   Java source line #1322	-> byte code offset #144
/*      */     //   Java source line #1325	-> byte code offset #153
/*      */     //   Java source line #1326	-> byte code offset #160
/*      */     //   Java source line #1329	-> byte code offset #192
/*      */     //   Java source line #1333	-> byte code offset #220
/*      */     //   Java source line #1334	-> byte code offset #245
/*      */     //   Java source line #1335	-> byte code offset #260
/*      */     //   Java source line #1338	-> byte code offset #273
/*      */     //   Java source line #1339	-> byte code offset #280
/*      */     //   Java source line #1341	-> byte code offset #287
/*      */     //   Java source line #1344	-> byte code offset #294
/*      */     //   Java source line #1346	-> byte code offset #301
/*      */     //   Java source line #1347	-> byte code offset #311
/*      */     //   Java source line #1349	-> byte code offset #324
/*      */     //   Java source line #1352	-> byte code offset #332
/*      */     //   Java source line #1350	-> byte code offset #335
/*      */     //   Java source line #1351	-> byte code offset #337
/*      */     //   Java source line #1354	-> byte code offset #351
/*      */     //   Java source line #1356	-> byte code offset #366
/*      */     //   Java source line #1357	-> byte code offset #373
/*      */     //   Java source line #1360	-> byte code offset #376
/*      */     //   Java source line #1346	-> byte code offset #400
/*      */     //   Java source line #1364	-> byte code offset #406
/*      */     //   Java source line #1367	-> byte code offset #414
/*      */     //   Java source line #1365	-> byte code offset #417
/*      */     //   Java source line #1366	-> byte code offset #419
/*      */     //   Java source line #1369	-> byte code offset #435
/*      */     //   Java source line #1371	-> byte code offset #450
/*      */     //   Java source line #1373	-> byte code offset #457
/*      */     //   Java source line #1374	-> byte code offset #464
/*      */     //   Java source line #1379	-> byte code offset #467
/*      */     //   Java source line #1375	-> byte code offset #470
/*      */     //   Java source line #1376	-> byte code offset #485
/*      */     //   Java source line #1377	-> byte code offset #492
/*      */     //   Java source line #1382	-> byte code offset #497
/*      */     //   Java source line #1384	-> byte code offset #502
/*      */     //   Java source line #1385	-> byte code offset #509
/*      */     //   Java source line #1387	-> byte code offset #527
/*      */     //   Java source line #1390	-> byte code offset #541
/*      */     //   Java source line #1391	-> byte code offset #546
/*      */     //   Java source line #1394	-> byte code offset #556
/*      */     //   Java source line #1396	-> byte code offset #559
/*      */     //   Java source line #1397	-> byte code offset #566
/*      */     //   Java source line #1401	-> byte code offset #593
/*      */     //   Java source line #1404	-> byte code offset #601
/*      */     //   Java source line #1402	-> byte code offset #604
/*      */     //   Java source line #1403	-> byte code offset #606
/*      */     //   Java source line #1406	-> byte code offset #622
/*      */     //   Java source line #1408	-> byte code offset #637
/*      */     //   Java source line #1411	-> byte code offset #644
/*      */     //   Java source line #1412	-> byte code offset #649
/*      */     //   Java source line #1413	-> byte code offset #657
/*      */     //   Java source line #1416	-> byte code offset #663
/*      */     //   Java source line #1418	-> byte code offset #669
/*      */     //   Java source line #1420	-> byte code offset #679
/*      */     //   Java source line #1423	-> byte code offset #682
/*      */     //   Java source line #1424	-> byte code offset #687
/*      */     //   Java source line #1427	-> byte code offset #699
/*      */     //   Java source line #1429	-> byte code offset #714
/*      */     //   Java source line #1430	-> byte code offset #729
/*      */     //   Java source line #1434	-> byte code offset #738
/*      */     //   Java source line #1435	-> byte code offset #753
/*      */     //   Java source line #1436	-> byte code offset #759
/*      */     //   Java source line #1439	-> byte code offset #769
/*      */     //   Java source line #1441	-> byte code offset #773
/*      */     //   Java source line #1442	-> byte code offset #778
/*      */     //   Java source line #1445	-> byte code offset #787
/*      */     //   Java source line #1447	-> byte code offset #793
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	800	0	this	PreparedStatement
/*      */     //   0	800	1	batchTimeout	int
/*      */     //   10	786	2	Ljava/lang/Object;	Object
/*      */     //   49	730	3	locallyScopedConn	MySQLConnection
/*      */     //   56	718	4	multiQueriesEnabled	boolean
/*      */     //   59	695	5	timeoutTask	StatementImpl.CancelTask
/*      */     //   74	488	6	numBatchedArgs	int
/*      */     //   102	510	7	numValuesPerBatch	int
/*      */     //   116	614	8	batchedStatement	java.sql.PreparedStatement
/*      */     //   119	470	9	batchedParamIndex	int
/*      */     //   122	173	10	numberToExecuteAsMultiValue	int
/*      */     //   125	483	11	batchCounter	int
/*      */     //   128	508	12	updateCountCounter	int
/*      */     //   134	566	13	updateCounts	long[]
/*      */     //   137	551	14	sqlEx	SQLException
/*      */     //   299	8	15	numberArgsToExecute	int
/*      */     //   604	108	15	ex	SQLException
/*      */     //   302	99	16	i	int
/*      */     //   417	12	16	ex	SQLException
/*      */     //   335	10	17	ex	SQLException
/*      */     //   470	6	18	localObject1	Object
/*      */     //   478	1	19	localObject2	Object
/*      */     //   714	6	20	localObject3	Object
/*      */     //   722	1	21	localObject4	Object
/*      */     //   738	6	22	localObject5	Object
/*      */     //   746	1	23	localObject6	Object
/*      */     //   793	5	24	localObject7	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   324	332	335	java/sql/SQLException
/*      */     //   406	414	417	java/sql/SQLException
/*      */     //   139	467	470	finally
/*      */     //   470	475	470	finally
/*      */     //   593	601	604	java/sql/SQLException
/*      */     //   497	706	714	finally
/*      */     //   714	719	714	finally
/*      */     //   61	709	738	finally
/*      */     //   714	743	738	finally
/*      */     //   12	711	793	finally
/*      */     //   714	797	793	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected long[] executeBatchedInserts(int batchTimeout)
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_2
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: invokevirtual 183	com/mysql/jdbc/PreparedStatement:getValuesClause	()Ljava/lang/String;
/*      */     //   16: astore_3
/*      */     //   17: aload_0
/*      */     //   18: getfield 35	com/mysql/jdbc/PreparedStatement:connection	Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   21: astore 4
/*      */     //   23: aload_3
/*      */     //   24: ifnonnull +11 -> 35
/*      */     //   27: aload_0
/*      */     //   28: iload_1
/*      */     //   29: invokevirtual 143	com/mysql/jdbc/PreparedStatement:executeBatchSerially	(I)[J
/*      */     //   32: aload_2
/*      */     //   33: monitorexit
/*      */     //   34: areturn
/*      */     //   35: aload_0
/*      */     //   36: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   39: invokeinterface 135 1 0
/*      */     //   44: istore 5
/*      */     //   46: aload_0
/*      */     //   47: getfield 106	com/mysql/jdbc/PreparedStatement:retrieveGeneratedKeys	Z
/*      */     //   50: ifeq +16 -> 66
/*      */     //   53: aload_0
/*      */     //   54: new 65	java/util/ArrayList
/*      */     //   57: dup
/*      */     //   58: iload 5
/*      */     //   60: invokespecial 152	java/util/ArrayList:<init>	(I)V
/*      */     //   63: putfield 110	com/mysql/jdbc/PreparedStatement:batchedGeneratedKeys	Ljava/util/ArrayList;
/*      */     //   66: aload_0
/*      */     //   67: iload 5
/*      */     //   69: invokevirtual 153	com/mysql/jdbc/PreparedStatement:computeBatchSize	(I)I
/*      */     //   72: istore 6
/*      */     //   74: iload 5
/*      */     //   76: iload 6
/*      */     //   78: if_icmpge +7 -> 85
/*      */     //   81: iload 5
/*      */     //   83: istore 6
/*      */     //   85: aconst_null
/*      */     //   86: astore 7
/*      */     //   88: iconst_1
/*      */     //   89: istore 8
/*      */     //   91: lconst_0
/*      */     //   92: lstore 9
/*      */     //   94: iconst_0
/*      */     //   95: istore 11
/*      */     //   97: iconst_0
/*      */     //   98: istore 12
/*      */     //   100: aconst_null
/*      */     //   101: astore 13
/*      */     //   103: aconst_null
/*      */     //   104: astore 14
/*      */     //   106: iload 5
/*      */     //   108: newarray <illegal type>
/*      */     //   110: astore 15
/*      */     //   112: aload_0
/*      */     //   113: aload 4
/*      */     //   115: iload 6
/*      */     //   117: invokevirtual 184	com/mysql/jdbc/PreparedStatement:prepareBatchedInsertSQL	(Lcom/mysql/jdbc/MySQLConnection;I)Lcom/mysql/jdbc/PreparedStatement;
/*      */     //   120: astore 7
/*      */     //   122: aload 4
/*      */     //   124: invokeinterface 162 1 0
/*      */     //   129: ifeq +46 -> 175
/*      */     //   132: iload_1
/*      */     //   133: ifeq +42 -> 175
/*      */     //   136: aload 4
/*      */     //   138: iconst_5
/*      */     //   139: iconst_0
/*      */     //   140: iconst_0
/*      */     //   141: invokeinterface 37 4 0
/*      */     //   146: ifeq +29 -> 175
/*      */     //   149: new 163	com/mysql/jdbc/StatementImpl$CancelTask
/*      */     //   152: dup
/*      */     //   153: aload_0
/*      */     //   154: aload 7
/*      */     //   156: invokespecial 165	com/mysql/jdbc/StatementImpl$CancelTask:<init>	(Lcom/mysql/jdbc/StatementImpl;Lcom/mysql/jdbc/StatementImpl;)V
/*      */     //   159: astore 13
/*      */     //   161: aload 4
/*      */     //   163: invokeinterface 166 1 0
/*      */     //   168: aload 13
/*      */     //   170: iload_1
/*      */     //   171: i2l
/*      */     //   172: invokevirtual 167	java/util/Timer:schedule	(Ljava/util/TimerTask;J)V
/*      */     //   175: iload 5
/*      */     //   177: iload 6
/*      */     //   179: if_icmpge +10 -> 189
/*      */     //   182: iload 5
/*      */     //   184: istore 11
/*      */     //   186: goto +10 -> 196
/*      */     //   189: iload 5
/*      */     //   191: iload 6
/*      */     //   193: idiv
/*      */     //   194: istore 11
/*      */     //   196: iload 11
/*      */     //   198: iload 6
/*      */     //   200: imul
/*      */     //   201: istore 16
/*      */     //   203: iconst_0
/*      */     //   204: istore 17
/*      */     //   206: iload 17
/*      */     //   208: iload 16
/*      */     //   210: if_icmpge +91 -> 301
/*      */     //   213: iload 17
/*      */     //   215: ifeq +56 -> 271
/*      */     //   218: iload 17
/*      */     //   220: iload 6
/*      */     //   222: irem
/*      */     //   223: ifne +48 -> 271
/*      */     //   226: lload 9
/*      */     //   228: aload 7
/*      */     //   230: invokevirtual 185	com/mysql/jdbc/PreparedStatement:executeLargeUpdate	()J
/*      */     //   233: ladd
/*      */     //   234: lstore 9
/*      */     //   236: goto +21 -> 257
/*      */     //   239: astore 18
/*      */     //   241: aload_0
/*      */     //   242: iload 12
/*      */     //   244: iconst_1
/*      */     //   245: isub
/*      */     //   246: iload 6
/*      */     //   248: aload 15
/*      */     //   250: aload 18
/*      */     //   252: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   255: astore 14
/*      */     //   257: aload_0
/*      */     //   258: aload 7
/*      */     //   260: invokevirtual 186	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   263: aload 7
/*      */     //   265: invokevirtual 187	com/mysql/jdbc/PreparedStatement:clearParameters	()V
/*      */     //   268: iconst_1
/*      */     //   269: istore 8
/*      */     //   271: aload_0
/*      */     //   272: aload 7
/*      */     //   274: iload 8
/*      */     //   276: aload_0
/*      */     //   277: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   280: iload 12
/*      */     //   282: iinc 12 1
/*      */     //   285: invokeinterface 77 2 0
/*      */     //   290: invokevirtual 172	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   293: istore 8
/*      */     //   295: iinc 17 1
/*      */     //   298: goto -92 -> 206
/*      */     //   301: lload 9
/*      */     //   303: aload 7
/*      */     //   305: invokevirtual 185	com/mysql/jdbc/PreparedStatement:executeLargeUpdate	()J
/*      */     //   308: ladd
/*      */     //   309: lstore 9
/*      */     //   311: goto +21 -> 332
/*      */     //   314: astore 17
/*      */     //   316: aload_0
/*      */     //   317: iload 12
/*      */     //   319: iconst_1
/*      */     //   320: isub
/*      */     //   321: iload 6
/*      */     //   323: aload 15
/*      */     //   325: aload 17
/*      */     //   327: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   330: astore 14
/*      */     //   332: aload_0
/*      */     //   333: aload 7
/*      */     //   335: invokevirtual 186	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   338: iload 5
/*      */     //   340: iload 12
/*      */     //   342: isub
/*      */     //   343: istore 6
/*      */     //   345: jsr +14 -> 359
/*      */     //   348: goto +28 -> 376
/*      */     //   351: astore 19
/*      */     //   353: jsr +6 -> 359
/*      */     //   356: aload 19
/*      */     //   358: athrow
/*      */     //   359: astore 20
/*      */     //   361: aload 7
/*      */     //   363: ifnull +11 -> 374
/*      */     //   366: aload 7
/*      */     //   368: invokevirtual 188	com/mysql/jdbc/PreparedStatement:close	()V
/*      */     //   371: aconst_null
/*      */     //   372: astore 7
/*      */     //   374: ret 20
/*      */     //   376: iload 6
/*      */     //   378: ifle +99 -> 477
/*      */     //   381: aload_0
/*      */     //   382: aload 4
/*      */     //   384: iload 6
/*      */     //   386: invokevirtual 184	com/mysql/jdbc/PreparedStatement:prepareBatchedInsertSQL	(Lcom/mysql/jdbc/MySQLConnection;I)Lcom/mysql/jdbc/PreparedStatement;
/*      */     //   389: astore 7
/*      */     //   391: aload 13
/*      */     //   393: ifnull +10 -> 403
/*      */     //   396: aload 13
/*      */     //   398: aload 7
/*      */     //   400: putfield 174	com/mysql/jdbc/StatementImpl$CancelTask:toCancel	Lcom/mysql/jdbc/StatementImpl;
/*      */     //   403: iconst_1
/*      */     //   404: istore 8
/*      */     //   406: iload 12
/*      */     //   408: iload 5
/*      */     //   410: if_icmpge +30 -> 440
/*      */     //   413: aload_0
/*      */     //   414: aload 7
/*      */     //   416: iload 8
/*      */     //   418: aload_0
/*      */     //   419: getfield 64	com/mysql/jdbc/PreparedStatement:batchedArgs	Ljava/util/List;
/*      */     //   422: iload 12
/*      */     //   424: iinc 12 1
/*      */     //   427: invokeinterface 77 2 0
/*      */     //   432: invokevirtual 172	com/mysql/jdbc/PreparedStatement:setOneBatchedParameterSet	(Ljava/sql/PreparedStatement;ILjava/lang/Object;)I
/*      */     //   435: istore 8
/*      */     //   437: goto -31 -> 406
/*      */     //   440: lload 9
/*      */     //   442: aload 7
/*      */     //   444: invokevirtual 185	com/mysql/jdbc/PreparedStatement:executeLargeUpdate	()J
/*      */     //   447: ladd
/*      */     //   448: lstore 9
/*      */     //   450: goto +21 -> 471
/*      */     //   453: astore 16
/*      */     //   455: aload_0
/*      */     //   456: iload 12
/*      */     //   458: iconst_1
/*      */     //   459: isub
/*      */     //   460: iload 6
/*      */     //   462: aload 15
/*      */     //   464: aload 16
/*      */     //   466: invokevirtual 169	com/mysql/jdbc/PreparedStatement:handleExceptionForBatch	(II[JLjava/sql/SQLException;)Ljava/sql/SQLException;
/*      */     //   469: astore 14
/*      */     //   471: aload_0
/*      */     //   472: aload 7
/*      */     //   474: invokevirtual 186	com/mysql/jdbc/PreparedStatement:getBatchedGeneratedKeys	(Ljava/sql/Statement;)V
/*      */     //   477: aload 14
/*      */     //   479: ifnull +15 -> 494
/*      */     //   482: aload 14
/*      */     //   484: aload 15
/*      */     //   486: aload_0
/*      */     //   487: invokevirtual 42	com/mysql/jdbc/PreparedStatement:getExceptionInterceptor	()Lcom/mysql/jdbc/ExceptionInterceptor;
/*      */     //   490: invokestatic 178	com/mysql/jdbc/SQLError:createBatchUpdateException	(Ljava/sql/SQLException;[JLcom/mysql/jdbc/ExceptionInterceptor;)Ljava/sql/SQLException;
/*      */     //   493: athrow
/*      */     //   494: iload 5
/*      */     //   496: iconst_1
/*      */     //   497: if_icmple +45 -> 542
/*      */     //   500: lload 9
/*      */     //   502: lconst_0
/*      */     //   503: lcmp
/*      */     //   504: ifle +9 -> 513
/*      */     //   507: ldc2_w 189
/*      */     //   510: goto +4 -> 514
/*      */     //   513: lconst_0
/*      */     //   514: lstore 16
/*      */     //   516: iconst_0
/*      */     //   517: istore 18
/*      */     //   519: iload 18
/*      */     //   521: iload 5
/*      */     //   523: if_icmpge +16 -> 539
/*      */     //   526: aload 15
/*      */     //   528: iload 18
/*      */     //   530: lload 16
/*      */     //   532: lastore
/*      */     //   533: iinc 18 1
/*      */     //   536: goto -17 -> 519
/*      */     //   539: goto +9 -> 548
/*      */     //   542: aload 15
/*      */     //   544: iconst_0
/*      */     //   545: lload 9
/*      */     //   547: lastore
/*      */     //   548: aload 15
/*      */     //   550: astore 16
/*      */     //   552: jsr +19 -> 571
/*      */     //   555: jsr +38 -> 593
/*      */     //   558: aload_2
/*      */     //   559: monitorexit
/*      */     //   560: aload 16
/*      */     //   562: areturn
/*      */     //   563: astore 21
/*      */     //   565: jsr +6 -> 571
/*      */     //   568: aload 21
/*      */     //   570: athrow
/*      */     //   571: astore 22
/*      */     //   573: aload 7
/*      */     //   575: ifnull +8 -> 583
/*      */     //   578: aload 7
/*      */     //   580: invokevirtual 188	com/mysql/jdbc/PreparedStatement:close	()V
/*      */     //   583: ret 22
/*      */     //   585: astore 23
/*      */     //   587: jsr +6 -> 593
/*      */     //   590: aload 23
/*      */     //   592: athrow
/*      */     //   593: astore 24
/*      */     //   595: aload 13
/*      */     //   597: ifnull +20 -> 617
/*      */     //   600: aload 13
/*      */     //   602: invokevirtual 176	com/mysql/jdbc/StatementImpl$CancelTask:cancel	()Z
/*      */     //   605: pop
/*      */     //   606: aload 4
/*      */     //   608: invokeinterface 166 1 0
/*      */     //   613: invokevirtual 177	java/util/Timer:purge	()I
/*      */     //   616: pop
/*      */     //   617: aload_0
/*      */     //   618: invokevirtual 137	com/mysql/jdbc/PreparedStatement:resetCancelledState	()V
/*      */     //   621: ret 24
/*      */     //   623: astore 25
/*      */     //   625: aload_2
/*      */     //   626: monitorexit
/*      */     //   627: aload 25
/*      */     //   629: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1475	-> byte code offset #0
/*      */     //   Java source line #1476	-> byte code offset #12
/*      */     //   Java source line #1478	-> byte code offset #17
/*      */     //   Java source line #1480	-> byte code offset #23
/*      */     //   Java source line #1481	-> byte code offset #27
/*      */     //   Java source line #1484	-> byte code offset #35
/*      */     //   Java source line #1486	-> byte code offset #46
/*      */     //   Java source line #1487	-> byte code offset #53
/*      */     //   Java source line #1490	-> byte code offset #66
/*      */     //   Java source line #1492	-> byte code offset #74
/*      */     //   Java source line #1493	-> byte code offset #81
/*      */     //   Java source line #1496	-> byte code offset #85
/*      */     //   Java source line #1498	-> byte code offset #88
/*      */     //   Java source line #1499	-> byte code offset #91
/*      */     //   Java source line #1500	-> byte code offset #94
/*      */     //   Java source line #1501	-> byte code offset #97
/*      */     //   Java source line #1502	-> byte code offset #100
/*      */     //   Java source line #1503	-> byte code offset #103
/*      */     //   Java source line #1505	-> byte code offset #106
/*      */     //   Java source line #1509	-> byte code offset #112
/*      */     //   Java source line #1512	-> byte code offset #122
/*      */     //   Java source line #1513	-> byte code offset #149
/*      */     //   Java source line #1514	-> byte code offset #161
/*      */     //   Java source line #1517	-> byte code offset #175
/*      */     //   Java source line #1518	-> byte code offset #182
/*      */     //   Java source line #1520	-> byte code offset #189
/*      */     //   Java source line #1523	-> byte code offset #196
/*      */     //   Java source line #1525	-> byte code offset #203
/*      */     //   Java source line #1526	-> byte code offset #213
/*      */     //   Java source line #1528	-> byte code offset #226
/*      */     //   Java source line #1531	-> byte code offset #236
/*      */     //   Java source line #1529	-> byte code offset #239
/*      */     //   Java source line #1530	-> byte code offset #241
/*      */     //   Java source line #1533	-> byte code offset #257
/*      */     //   Java source line #1534	-> byte code offset #263
/*      */     //   Java source line #1535	-> byte code offset #268
/*      */     //   Java source line #1539	-> byte code offset #271
/*      */     //   Java source line #1525	-> byte code offset #295
/*      */     //   Java source line #1543	-> byte code offset #301
/*      */     //   Java source line #1546	-> byte code offset #311
/*      */     //   Java source line #1544	-> byte code offset #314
/*      */     //   Java source line #1545	-> byte code offset #316
/*      */     //   Java source line #1548	-> byte code offset #332
/*      */     //   Java source line #1550	-> byte code offset #338
/*      */     //   Java source line #1551	-> byte code offset #345
/*      */     //   Java source line #1556	-> byte code offset #348
/*      */     //   Java source line #1552	-> byte code offset #351
/*      */     //   Java source line #1553	-> byte code offset #366
/*      */     //   Java source line #1554	-> byte code offset #371
/*      */     //   Java source line #1559	-> byte code offset #376
/*      */     //   Java source line #1560	-> byte code offset #381
/*      */     //   Java source line #1562	-> byte code offset #391
/*      */     //   Java source line #1563	-> byte code offset #396
/*      */     //   Java source line #1566	-> byte code offset #403
/*      */     //   Java source line #1568	-> byte code offset #406
/*      */     //   Java source line #1569	-> byte code offset #413
/*      */     //   Java source line #1573	-> byte code offset #440
/*      */     //   Java source line #1576	-> byte code offset #450
/*      */     //   Java source line #1574	-> byte code offset #453
/*      */     //   Java source line #1575	-> byte code offset #455
/*      */     //   Java source line #1578	-> byte code offset #471
/*      */     //   Java source line #1581	-> byte code offset #477
/*      */     //   Java source line #1582	-> byte code offset #482
/*      */     //   Java source line #1585	-> byte code offset #494
/*      */     //   Java source line #1586	-> byte code offset #500
/*      */     //   Java source line #1587	-> byte code offset #516
/*      */     //   Java source line #1588	-> byte code offset #526
/*      */     //   Java source line #1587	-> byte code offset #533
/*      */     //   Java source line #1590	-> byte code offset #539
/*      */     //   Java source line #1591	-> byte code offset #542
/*      */     //   Java source line #1593	-> byte code offset #548
/*      */     //   Java source line #1595	-> byte code offset #563
/*      */     //   Java source line #1596	-> byte code offset #578
/*      */     //   Java source line #1600	-> byte code offset #585
/*      */     //   Java source line #1601	-> byte code offset #600
/*      */     //   Java source line #1602	-> byte code offset #606
/*      */     //   Java source line #1605	-> byte code offset #617
/*      */     //   Java source line #1607	-> byte code offset #623
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	630	0	this	PreparedStatement
/*      */     //   0	630	1	batchTimeout	int
/*      */     //   10	616	2	Ljava/lang/Object;	Object
/*      */     //   16	8	3	valuesClause	String
/*      */     //   21	586	4	locallyScopedConn	MySQLConnection
/*      */     //   44	478	5	numBatchedArgs	int
/*      */     //   72	389	6	numValuesPerBatch	int
/*      */     //   86	493	7	batchedStatement	PreparedStatement
/*      */     //   89	347	8	batchedParamIndex	int
/*      */     //   92	454	9	updateCountRunningTotal	long
/*      */     //   95	102	11	numberToExecuteAsMultiValue	int
/*      */     //   98	359	12	batchCounter	int
/*      */     //   101	500	13	timeoutTask	StatementImpl.CancelTask
/*      */     //   104	379	14	sqlEx	SQLException
/*      */     //   110	439	15	updateCounts	long[]
/*      */     //   201	8	16	numberArgsToExecute	int
/*      */     //   453	12	16	ex	SQLException
/*      */     //   514	47	16	updCount	long
/*      */     //   204	92	17	i	int
/*      */     //   314	12	17	ex	SQLException
/*      */     //   239	12	18	ex	SQLException
/*      */     //   517	17	18	j	int
/*      */     //   351	6	19	localObject1	Object
/*      */     //   359	1	20	localObject2	Object
/*      */     //   563	6	21	localObject3	Object
/*      */     //   571	1	22	localObject4	Object
/*      */     //   585	6	23	localObject5	Object
/*      */     //   593	1	24	localObject6	Object
/*      */     //   623	5	25	localObject7	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   226	236	239	java/sql/SQLException
/*      */     //   301	311	314	java/sql/SQLException
/*      */     //   112	348	351	finally
/*      */     //   351	356	351	finally
/*      */     //   440	450	453	java/sql/SQLException
/*      */     //   376	555	563	finally
/*      */     //   563	568	563	finally
/*      */     //   112	558	585	finally
/*      */     //   563	590	585	finally
/*      */     //   12	34	623	finally
/*      */     //   35	560	623	finally
/*      */     //   563	627	623	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: aload_0
/*      */     //   14: getfield 24	com/mysql/jdbc/PreparedStatement:parameterValues	[[B
/*      */     //   17: aload_0
/*      */     //   18: getfield 22	com/mysql/jdbc/PreparedStatement:parameterStreams	[Ljava/io/InputStream;
/*      */     //   21: aload_0
/*      */     //   22: getfield 19	com/mysql/jdbc/PreparedStatement:isStream	[Z
/*      */     //   25: aload_0
/*      */     //   26: getfield 28	com/mysql/jdbc/PreparedStatement:streamLengths	[I
/*      */     //   29: invokevirtual 236	com/mysql/jdbc/PreparedStatement:fillSendPacket	([[B[Ljava/io/InputStream;[Z[I)Lcom/mysql/jdbc/Buffer;
/*      */     //   32: aload_1
/*      */     //   33: monitorexit
/*      */     //   34: areturn
/*      */     //   35: astore_2
/*      */     //   36: aload_1
/*      */     //   37: monitorexit
/*      */     //   38: aload_2
/*      */     //   39: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2114	-> byte code offset #0
/*      */     //   Java source line #2115	-> byte code offset #12
/*      */     //   Java source line #2116	-> byte code offset #35
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	40	0	this	PreparedStatement
/*      */     //   10	27	1	Ljava/lang/Object;	Object
/*      */     //   35	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	34	35	finally
/*      */     //   35	38	35	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected boolean isSelectQuery()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 21	com/mysql/jdbc/PreparedStatement:originalSql	Ljava/lang/String;
/*      */     //   16: ldc_w 305
/*      */     //   19: ldc_w 305
/*      */     //   22: iconst_1
/*      */     //   23: iconst_0
/*      */     //   24: iconst_1
/*      */     //   25: iconst_1
/*      */     //   26: invokestatic 306	com/mysql/jdbc/StringUtils:stripComments	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZZ)Ljava/lang/String;
/*      */     //   29: ldc_w 307
/*      */     //   32: invokestatic 308	com/mysql/jdbc/StringUtils:startsWithIgnoreCaseAndWs	(Ljava/lang/String;Ljava/lang/String;)Z
/*      */     //   35: aload_1
/*      */     //   36: monitorexit
/*      */     //   37: ireturn
/*      */     //   38: astore_2
/*      */     //   39: aload_1
/*      */     //   40: monitorexit
/*      */     //   41: aload_2
/*      */     //   42: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2562	-> byte code offset #0
/*      */     //   Java source line #2563	-> byte code offset #12
/*      */     //   Java source line #2564	-> byte code offset #38
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	43	0	this	PreparedStatement
/*      */     //   10	30	1	Ljava/lang/Object;	Object
/*      */     //   38	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	37	38	finally
/*      */     //   38	41	38	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   boolean isNull(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_2
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 18	com/mysql/jdbc/PreparedStatement:isNull	[Z
/*      */     //   16: iload_1
/*      */     //   17: baload
/*      */     //   18: aload_2
/*      */     //   19: monitorexit
/*      */     //   20: ireturn
/*      */     //   21: astore_3
/*      */     //   22: aload_2
/*      */     //   23: monitorexit
/*      */     //   24: aload_3
/*      */     //   25: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2639	-> byte code offset #0
/*      */     //   Java source line #2640	-> byte code offset #12
/*      */     //   Java source line #2641	-> byte code offset #21
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	26	0	this	PreparedStatement
/*      */     //   0	26	1	paramIndex	int
/*      */     //   10	13	2	Ljava/lang/Object;	Object
/*      */     //   21	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	20	21	finally
/*      */     //   21	24	21	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public ParameterBindings getParameterBindings()
/*      */     throws SQLException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: new 572	com/mysql/jdbc/PreparedStatement$EmulatedPreparedStatementBindings
/*      */     //   15: dup
/*      */     //   16: aload_0
/*      */     //   17: invokespecial 573	com/mysql/jdbc/PreparedStatement$EmulatedPreparedStatementBindings:<init>	(Lcom/mysql/jdbc/PreparedStatement;)V
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: areturn
/*      */     //   23: astore_2
/*      */     //   24: aload_1
/*      */     //   25: monitorexit
/*      */     //   26: aload_2
/*      */     //   27: athrow
/*      */     // Line number table:
/*      */     //   Java source line #4862	-> byte code offset #0
/*      */     //   Java source line #4863	-> byte code offset #12
/*      */     //   Java source line #4864	-> byte code offset #23
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	28	0	this	PreparedStatement
/*      */     //   10	15	1	Ljava/lang/Object;	Object
/*      */     //   23	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   12	22	23	finally
/*      */     //   23	26	23	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public String getPreparedSql()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokevirtual 62	com/mysql/jdbc/PreparedStatement:checkClosed	()Lcom/mysql/jdbc/MySQLConnection;
/*      */     //   4: invokeinterface 63 1 0
/*      */     //   9: dup
/*      */     //   10: astore_1
/*      */     //   11: monitorenter
/*      */     //   12: aload_0
/*      */     //   13: getfield 33	com/mysql/jdbc/PreparedStatement:rewrittenBatchSize	I
/*      */     //   16: ifne +10 -> 26
/*      */     //   19: aload_0
/*      */     //   20: getfield 21	com/mysql/jdbc/PreparedStatement:originalSql	Ljava/lang/String;
/*      */     //   23: aload_1
/*      */     //   24: monitorexit
/*      */     //   25: areturn
/*      */     //   26: aload_0
/*      */     //   27: getfield 52	com/mysql/jdbc/PreparedStatement:parseInfo	Lcom/mysql/jdbc/PreparedStatement$ParseInfo;
/*      */     //   30: aload_0
/*      */     //   31: getfield 52	com/mysql/jdbc/PreparedStatement:parseInfo	Lcom/mysql/jdbc/PreparedStatement$ParseInfo;
/*      */     //   34: invokevirtual 574	com/mysql/jdbc/PreparedStatement$ParseInfo:getSqlForBatch	(Lcom/mysql/jdbc/PreparedStatement$ParseInfo;)Ljava/lang/String;
/*      */     //   37: aload_1
/*      */     //   38: monitorexit
/*      */     //   39: areturn
/*      */     //   40: astore_2
/*      */     //   41: new 91	java/lang/RuntimeException
/*      */     //   44: dup
/*      */     //   45: aload_2
/*      */     //   46: invokespecial 575	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   49: athrow
/*      */     //   50: astore_3
/*      */     //   51: aload_1
/*      */     //   52: monitorexit
/*      */     //   53: aload_3
/*      */     //   54: athrow
/*      */     //   55: astore_1
/*      */     //   56: new 91	java/lang/RuntimeException
/*      */     //   59: dup
/*      */     //   60: aload_1
/*      */     //   61: invokespecial 575	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*      */     //   64: athrow
/*      */     // Line number table:
/*      */     //   Java source line #5043	-> byte code offset #0
/*      */     //   Java source line #5044	-> byte code offset #12
/*      */     //   Java source line #5045	-> byte code offset #19
/*      */     //   Java source line #5049	-> byte code offset #26
/*      */     //   Java source line #5050	-> byte code offset #40
/*      */     //   Java source line #5051	-> byte code offset #41
/*      */     //   Java source line #5053	-> byte code offset #50
/*      */     //   Java source line #5054	-> byte code offset #55
/*      */     //   Java source line #5055	-> byte code offset #56
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	65	0	this	PreparedStatement
/*      */     //   55	6	1	e	SQLException
/*      */     //   40	6	2	e	UnsupportedEncodingException
/*      */     //   50	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   26	37	40	java/io/UnsupportedEncodingException
/*      */     //   12	25	50	finally
/*      */     //   26	39	50	finally
/*      */     //   40	53	50	finally
/*      */     //   0	25	55	java/sql/SQLException
/*      */     //   26	39	55	java/sql/SQLException
/*      */     //   40	55	55	java/sql/SQLException
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\PreparedStatement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */