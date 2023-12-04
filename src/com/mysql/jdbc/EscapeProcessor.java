/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
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
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   private static Map<String, String> JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;
/*     */   
/*     */   static
/*     */   {
/*  47 */     Map<String, String> tempMap = new HashMap();
/*     */     
/*  49 */     tempMap.put("BIGINT", "0 + ?");
/*  50 */     tempMap.put("BINARY", "BINARY");
/*  51 */     tempMap.put("BIT", "0 + ?");
/*  52 */     tempMap.put("CHAR", "CHAR");
/*  53 */     tempMap.put("DATE", "DATE");
/*  54 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  55 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  56 */     tempMap.put("FLOAT", "0.0 + ?");
/*  57 */     tempMap.put("INTEGER", "0 + ?");
/*  58 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  59 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  60 */     tempMap.put("REAL", "0.0 + ?");
/*  61 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  62 */     tempMap.put("TIME", "TIME");
/*  63 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  64 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  65 */     tempMap.put("VARBINARY", "BINARY");
/*  66 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */     
/*  68 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
/*     */     
/*  70 */     tempMap = new HashMap(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
/*     */     
/*  72 */     tempMap.put("BINARY", "CONCAT(?)");
/*  73 */     tempMap.put("CHAR", "CONCAT(?)");
/*  74 */     tempMap.remove("DATE");
/*  75 */     tempMap.put("LONGVARBINARY", "CONCAT(?)");
/*  76 */     tempMap.remove("TIME");
/*  77 */     tempMap.remove("TIMESTAMP");
/*  78 */     tempMap.put("VARBINARY", "CONCAT(?)");
/*     */     
/*  80 */     JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
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
/*     */   public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn, MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/*  96 */     boolean replaceEscapeSequence = false;
/*  97 */     String escapeSequence = null;
/*     */     
/*  99 */     if (sql == null) {
/* 100 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 106 */     int beginBrace = sql.indexOf('{');
/* 107 */     int nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf('}', beginBrace);
/*     */     
/* 109 */     if (nextEndBrace == -1) {
/* 110 */       return sql;
/*     */     }
/*     */     
/* 113 */     StringBuilder newSql = new StringBuilder();
/*     */     
/* 115 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */     
/* 117 */     byte usesVariables = 0;
/* 118 */     boolean callingStoredFunction = false;
/*     */     
/* 120 */     while (escapeTokenizer.hasMoreTokens()) {
/* 121 */       String token = escapeTokenizer.nextToken();
/*     */       
/* 123 */       if (token.length() != 0) {
/* 124 */         if (token.charAt(0) == '{')
/*     */         {
/* 126 */           if (!token.endsWith("}")) {
/* 127 */             throw SQLError.createSQLException("Not a valid escape sequence: " + token, conn.getExceptionInterceptor());
/*     */           }
/*     */           
/* 130 */           if (token.length() > 2) {
/* 131 */             int nestedBrace = token.indexOf('{', 2);
/*     */             
/* 133 */             if (nestedBrace != -1) {
/* 134 */               StringBuilder buf = new StringBuilder(token.substring(0, 1));
/*     */               
/* 136 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), serverSupportsConvertFn, conn);
/*     */               
/* 138 */               String remaining = null;
/*     */               
/* 140 */               if ((remainingResults instanceof String)) {
/* 141 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 143 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */                 
/* 145 */                 if (usesVariables != 1) {
/* 146 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               }
/*     */               
/* 150 */               buf.append(remaining);
/*     */               
/* 152 */               buf.append('}');
/*     */               
/* 154 */               token = buf.toString();
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 160 */           String collapsedToken = removeWhitespace(token);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 165 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape")) {
/*     */             try {
/* 167 */               StringTokenizer st = new StringTokenizer(token, " '");
/* 168 */               st.nextToken();
/* 169 */               escapeSequence = st.nextToken();
/*     */               
/* 171 */               if (escapeSequence.length() < 3) {
/* 172 */                 newSql.append(token);
/*     */               }
/*     */               else {
/* 175 */                 escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/* 176 */                 replaceEscapeSequence = true;
/*     */               }
/*     */             } catch (NoSuchElementException e) {
/* 179 */               newSql.append(token);
/*     */             }
/* 181 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn")) {
/* 182 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 183 */             int endPos = token.length() - 1;
/*     */             
/* 185 */             String fnToken = token.substring(startPos, endPos);
/*     */             
/*     */ 
/*     */ 
/* 189 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert")) {
/* 190 */               newSql.append(processConvertToken(fnToken, serverSupportsConvertFn, conn));
/*     */             }
/*     */             else {
/* 193 */               newSql.append(fnToken);
/*     */             }
/* 195 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d")) {
/* 196 */             int startPos = token.indexOf('\'') + 1;
/* 197 */             int endPos = token.lastIndexOf('\'');
/*     */             
/* 199 */             if ((startPos == -1) || (endPos == -1)) {
/* 200 */               newSql.append(token);
/*     */             }
/*     */             else {
/* 203 */               String argument = token.substring(startPos, endPos);
/*     */               try
/*     */               {
/* 206 */                 StringTokenizer st = new StringTokenizer(argument, " -");
/* 207 */                 String year4 = st.nextToken();
/* 208 */                 String month2 = st.nextToken();
/* 209 */                 String day2 = st.nextToken();
/* 210 */                 String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/* 211 */                 newSql.append(dateString);
/*     */               } catch (NoSuchElementException e) {
/* 213 */                 throw SQLError.createSQLException("Syntax error for DATE escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */               }
/*     */             }
/*     */           }
/* 217 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts")) {
/* 218 */             processTimestampToken(conn, newSql, token);
/* 219 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t")) {
/* 220 */             processTimeToken(conn, newSql, token);
/* 221 */           } else if ((StringUtils.startsWithIgnoreCase(collapsedToken, "{call")) || (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")))
/*     */           {
/* 223 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/* 224 */             int endPos = token.length() - 1;
/*     */             
/* 226 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
/* 227 */               callingStoredFunction = true;
/* 228 */               newSql.append("SELECT ");
/* 229 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 231 */               callingStoredFunction = false;
/* 232 */               newSql.append("CALL ");
/* 233 */               newSql.append(token.substring(startPos, endPos));
/*     */             }
/*     */             
/* 236 */             for (int i = endPos - 1; i >= startPos; i--) {
/* 237 */               char c = token.charAt(i);
/*     */               
/* 239 */               if (!Character.isWhitespace(c))
/*     */               {
/*     */ 
/*     */ 
/* 243 */                 if (c == ')') break;
/* 244 */                 newSql.append("()"); break;
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 249 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj"))
/*     */           {
/* 251 */             newSql.append(token);
/*     */           }
/*     */           else {
/* 254 */             newSql.append(token);
/*     */           }
/*     */         } else {
/* 257 */           newSql.append(token);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 262 */     String escapedSql = newSql.toString();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 267 */     if (replaceEscapeSequence) {
/* 268 */       String currentSql = escapedSql;
/*     */       
/* 270 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 271 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 272 */         String lhs = currentSql.substring(0, escapePos);
/* 273 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/* 274 */         currentSql = lhs + "\\" + rhs;
/*     */       }
/*     */       
/* 277 */       escapedSql = currentSql;
/*     */     }
/*     */     
/* 280 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 281 */     epr.escapedSql = escapedSql;
/* 282 */     epr.callingStoredFunction = callingStoredFunction;
/*     */     
/* 284 */     if (usesVariables != 1) {
/* 285 */       if (escapeTokenizer.sawVariableUse()) {
/* 286 */         epr.usesVariables = 1;
/*     */       } else {
/* 288 */         epr.usesVariables = 0;
/*     */       }
/*     */     }
/*     */     
/* 292 */     return epr;
/*     */   }
/*     */   
/*     */   private static void processTimeToken(MySQLConnection conn, StringBuilder newSql, String token) throws SQLException {
/* 296 */     int startPos = token.indexOf('\'') + 1;
/* 297 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 299 */     if ((startPos == -1) || (endPos == -1)) {
/* 300 */       newSql.append(token);
/*     */     }
/*     */     else {
/* 303 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 306 */         StringTokenizer st = new StringTokenizer(argument, " :.");
/* 307 */         String hour = st.nextToken();
/* 308 */         String minute = st.nextToken();
/* 309 */         String second = st.nextToken();
/*     */         
/* 311 */         boolean serverSupportsFractionalSecond = false;
/* 312 */         String fractionalSecond = "";
/*     */         
/* 314 */         if ((st.hasMoreTokens()) && 
/* 315 */           (conn.versionMeetsMinimum(5, 6, 4))) {
/* 316 */           serverSupportsFractionalSecond = true;
/* 317 */           fractionalSecond = "." + st.nextToken();
/*     */         }
/*     */         
/*     */ 
/* 321 */         if ((!conn.getUseTimezone()) || (!conn.getUseLegacyDatetimeCode())) {
/* 322 */           newSql.append("'");
/* 323 */           newSql.append(hour);
/* 324 */           newSql.append(":");
/* 325 */           newSql.append(minute);
/* 326 */           newSql.append(":");
/* 327 */           newSql.append(second);
/* 328 */           newSql.append(fractionalSecond);
/* 329 */           newSql.append("'");
/*     */         } else {
/* 331 */           Calendar sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */           try
/*     */           {
/* 334 */             int hourInt = Integer.parseInt(hour);
/* 335 */             int minuteInt = Integer.parseInt(minute);
/* 336 */             int secondInt = Integer.parseInt(second);
/*     */             
/* 338 */             Time toBeAdjusted = TimeUtil.fastTimeCreate(sessionCalendar, hourInt, minuteInt, secondInt, conn.getExceptionInterceptor());
/*     */             
/* 340 */             Time inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */             
/*     */ 
/* 343 */             newSql.append("'");
/* 344 */             newSql.append(inServerTimezone.toString());
/*     */             
/* 346 */             if (serverSupportsFractionalSecond) {
/* 347 */               newSql.append(fractionalSecond);
/*     */             }
/*     */             
/* 350 */             newSql.append("'");
/*     */           }
/*     */           catch (NumberFormatException nfe) {
/* 353 */             throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NoSuchElementException e) {
/* 358 */         throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void processTimestampToken(MySQLConnection conn, StringBuilder newSql, String token) throws SQLException {
/* 364 */     int startPos = token.indexOf('\'') + 1;
/* 365 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 367 */     if ((startPos == -1) || (endPos == -1)) {
/* 368 */       newSql.append(token);
/*     */     }
/*     */     else {
/* 371 */       String argument = token.substring(startPos, endPos);
/*     */       try
/*     */       {
/* 374 */         if (!conn.getUseLegacyDatetimeCode()) {
/* 375 */           Timestamp ts = Timestamp.valueOf(argument);
/* 376 */           SimpleDateFormat tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss", Locale.US);
/*     */           
/* 378 */           tsdf.setTimeZone(conn.getServerTimezoneTZ());
/*     */           
/* 380 */           newSql.append(tsdf.format(ts));
/*     */           
/* 382 */           if ((ts.getNanos() > 0) && (conn.versionMeetsMinimum(5, 6, 4))) {
/* 383 */             newSql.append('.');
/* 384 */             newSql.append(TimeUtil.formatNanos(ts.getNanos(), true, true));
/*     */           }
/*     */           
/* 387 */           newSql.append('\'');
/*     */         }
/*     */         else {
/* 390 */           StringTokenizer st = new StringTokenizer(argument, " .-:");
/*     */           try {
/* 392 */             String year4 = st.nextToken();
/* 393 */             String month2 = st.nextToken();
/* 394 */             String day2 = st.nextToken();
/* 395 */             String hour = st.nextToken();
/* 396 */             String minute = st.nextToken();
/* 397 */             String second = st.nextToken();
/*     */             
/* 399 */             boolean serverSupportsFractionalSecond = false;
/* 400 */             String fractionalSecond = "";
/* 401 */             if ((st.hasMoreTokens()) && 
/* 402 */               (conn.versionMeetsMinimum(5, 6, 4))) {
/* 403 */               serverSupportsFractionalSecond = true;
/* 404 */               fractionalSecond = "." + st.nextToken();
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 420 */             if ((!conn.getUseTimezone()) && (!conn.getUseJDBCCompliantTimezoneShift())) {
/* 421 */               newSql.append("'").append(year4).append("-").append(month2).append("-").append(day2).append(" ").append(hour).append(":").append(minute).append(":").append(second).append(fractionalSecond).append("'");
/*     */             }
/*     */             else {
/* 424 */               Calendar sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
/*     */               try
/*     */               {
/* 427 */                 int year4Int = Integer.parseInt(year4);
/* 428 */                 int month2Int = Integer.parseInt(month2);
/* 429 */                 int day2Int = Integer.parseInt(day2);
/* 430 */                 int hourInt = Integer.parseInt(hour);
/* 431 */                 int minuteInt = Integer.parseInt(minute);
/* 432 */                 int secondInt = Integer.parseInt(second);
/*     */                 
/* 434 */                 boolean useGmtMillis = conn.getUseGmtMillisForDatetimes();
/*     */                 
/* 436 */                 Timestamp toBeAdjusted = TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? Calendar.getInstance(TimeZone.getTimeZone("GMT")) : null, sessionCalendar, year4Int, month2Int, day2Int, hourInt, minuteInt, secondInt, 0);
/*     */                 
/*     */ 
/*     */ 
/* 440 */                 Timestamp inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, null, toBeAdjusted, sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
/*     */                 
/*     */ 
/* 443 */                 newSql.append("'");
/*     */                 
/* 445 */                 String timezoneLiteral = inServerTimezone.toString();
/*     */                 
/* 447 */                 int indexOfDot = timezoneLiteral.indexOf(".");
/*     */                 
/* 449 */                 if (indexOfDot != -1) {
/* 450 */                   timezoneLiteral = timezoneLiteral.substring(0, indexOfDot);
/*     */                 }
/*     */                 
/* 453 */                 newSql.append(timezoneLiteral);
/*     */                 
/* 455 */                 if (serverSupportsFractionalSecond) {
/* 456 */                   newSql.append(fractionalSecond);
/*     */                 }
/* 458 */                 newSql.append("'");
/*     */               }
/*     */               catch (NumberFormatException nfe) {
/* 461 */                 throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + token + "'.", "S1009", conn.getExceptionInterceptor());
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (NoSuchElementException e) {
/* 466 */             throw SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IllegalArgumentException illegalArgumentException) {
/* 471 */         SQLException sqlEx = SQLError.createSQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000", conn.getExceptionInterceptor());
/*     */         
/* 473 */         sqlEx.initCause(illegalArgumentException);
/*     */         
/* 475 */         throw sqlEx;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn, MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/* 518 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */     
/* 520 */     if (firstIndexOfParen == -1) {
/* 521 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 526 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */     
/* 528 */     if (indexOfComma == -1) {
/* 529 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/* 533 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */     
/* 535 */     if (indexOfCloseParen == -1) {
/* 536 */       throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", "42000", conn.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 542 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/* 543 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */     
/* 545 */     String newType = null;
/*     */     
/* 547 */     String trimmedType = type.trim();
/*     */     
/* 549 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 550 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */     
/* 553 */     if (serverSupportsConvertFn) {
/* 554 */       newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     } else {
/* 556 */       newType = (String)JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 561 */       if (newType == null) {
/* 562 */         throw SQLError.createSQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", "S1000", conn.getExceptionInterceptor());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 568 */     if (newType == null) {
/* 569 */       throw SQLError.createSQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", "S1000", conn.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/* 573 */     int replaceIndex = newType.indexOf("?");
/*     */     
/* 575 */     if (replaceIndex != -1) {
/* 576 */       StringBuilder convertRewrite = new StringBuilder(newType.substring(0, replaceIndex));
/* 577 */       convertRewrite.append(expression);
/* 578 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */       
/* 580 */       return convertRewrite.toString();
/*     */     }
/*     */     
/* 583 */     StringBuilder castRewrite = new StringBuilder("CAST(");
/* 584 */     castRewrite.append(expression);
/* 585 */     castRewrite.append(" AS ");
/* 586 */     castRewrite.append(newType);
/* 587 */     castRewrite.append(")");
/*     */     
/* 589 */     return castRewrite.toString();
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
/*     */   private static String removeWhitespace(String toCollapse)
/*     */   {
/* 603 */     if (toCollapse == null) {
/* 604 */       return null;
/*     */     }
/*     */     
/* 607 */     int length = toCollapse.length();
/*     */     
/* 609 */     StringBuilder collapsed = new StringBuilder(length);
/*     */     
/* 611 */     for (int i = 0; i < length; i++) {
/* 612 */       char c = toCollapse.charAt(i);
/*     */       
/* 614 */       if (!Character.isWhitespace(c)) {
/* 615 */         collapsed.append(c);
/*     */       }
/*     */     }
/*     */     
/* 619 */     return collapsed.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\EscapeProcessor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */