/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.Date;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class ResultSetRow
/*      */ {
/*      */   protected ExceptionInterceptor exceptionInterceptor;
/*      */   protected Field[] metadata;
/*      */   
/*      */   protected ResultSetRow(ExceptionInterceptor exceptionInterceptor)
/*      */   {
/*   48 */     this.exceptionInterceptor = exceptionInterceptor;
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
/*      */   public abstract void closeOpenStreams();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract InputStream getBinaryInputStream(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract byte[] getColumnValue(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar)
/*      */     throws SQLException
/*      */   {
/*   91 */     int year = 0;
/*   92 */     int month = 0;
/*   93 */     int day = 0;
/*      */     try
/*      */     {
/*   96 */       if (dateAsBytes == null) {
/*   97 */         return null;
/*      */       }
/*      */       
/*  100 */       boolean allZeroDate = true;
/*      */       
/*  102 */       boolean onlyTimePresent = false;
/*      */       
/*  104 */       for (int i = 0; i < length; i++) {
/*  105 */         if (dateAsBytes[(offset + i)] == 58) {
/*  106 */           onlyTimePresent = true;
/*  107 */           break;
/*      */         }
/*      */       }
/*      */       
/*  111 */       for (int i = 0; i < length; i++) {
/*  112 */         byte b = dateAsBytes[(offset + i)];
/*      */         
/*  114 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  115 */           onlyTimePresent = false;
/*      */         }
/*      */         
/*  118 */         if ((b != 48) && (b != 32) && (b != 58) && (b != 45) && (b != 47) && (b != 46)) {
/*  119 */           allZeroDate = false;
/*      */           
/*  121 */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  126 */       int decimalIndex = -1;
/*  127 */       for (int i = 0; i < length; i++) {
/*  128 */         if (dateAsBytes[(offset + i)] == 46) {
/*  129 */           decimalIndex = i;
/*  130 */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  135 */       if (decimalIndex > -1) {
/*  136 */         length = decimalIndex;
/*      */       }
/*      */       
/*  139 */       if ((!onlyTimePresent) && (allZeroDate))
/*      */       {
/*  141 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  143 */           return null; }
/*  144 */         if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  145 */           throw SQLError.createSQLException("Value '" + StringUtils.toString(dateAsBytes) + "' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  150 */         return rs.fastDateCreate(targetCalendar, 1, 1, 1);
/*      */       }
/*  152 */       if (this.metadata[columnIndex].getMysqlType() == 7)
/*      */       {
/*  154 */         switch (length) {
/*      */         case 19: 
/*      */         case 21: 
/*      */         case 29: 
/*  158 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*  159 */           month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*  160 */           day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */           
/*  162 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         
/*      */ 
/*      */         case 8: 
/*      */         case 14: 
/*  167 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*  168 */           month = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*  169 */           day = StringUtils.getInt(dateAsBytes, offset + 6, offset + 8);
/*      */           
/*  171 */           return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */         
/*      */ 
/*      */         case 6: 
/*      */         case 10: 
/*      */         case 12: 
/*  177 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */           
/*  179 */           if (year <= 69) {
/*  180 */             year += 100;
/*      */           }
/*      */           
/*  183 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*  184 */           day = StringUtils.getInt(dateAsBytes, offset + 4, offset + 6);
/*      */           
/*  186 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, day);
/*      */         
/*      */ 
/*      */         case 4: 
/*  190 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */           
/*  192 */           if (year <= 69) {
/*  193 */             year += 100;
/*      */           }
/*      */           
/*  196 */           month = StringUtils.getInt(dateAsBytes, offset + 2, offset + 4);
/*      */           
/*  198 */           return rs.fastDateCreate(targetCalendar, year + 1900, month, 1);
/*      */         
/*      */ 
/*      */         case 2: 
/*  202 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 2);
/*      */           
/*  204 */           if (year <= 69) {
/*  205 */             year += 100;
/*      */           }
/*      */           
/*  208 */           return rs.fastDateCreate(targetCalendar, year + 1900, 1, 1);
/*      */         }
/*      */         
/*      */         
/*  212 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  217 */       if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */       {
/*  219 */         if ((length == 2) || (length == 1)) {
/*  220 */           year = StringUtils.getInt(dateAsBytes, offset, offset + length);
/*      */           
/*  222 */           if (year <= 69) {
/*  223 */             year += 100;
/*      */           }
/*      */           
/*  226 */           year += 1900;
/*      */         } else {
/*  228 */           year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*      */         }
/*      */         
/*  231 */         return rs.fastDateCreate(targetCalendar, year, 1, 1); }
/*  232 */       if (this.metadata[columnIndex].getMysqlType() == 11) {
/*  233 */         return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */       }
/*  235 */       if (length < 10) {
/*  236 */         if (length == 8) {
/*  237 */           return rs.fastDateCreate(targetCalendar, 1970, 1, 1);
/*      */         }
/*      */         
/*      */ 
/*  241 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  247 */       if (length != 18) {
/*  248 */         year = StringUtils.getInt(dateAsBytes, offset + 0, offset + 4);
/*  249 */         month = StringUtils.getInt(dateAsBytes, offset + 5, offset + 7);
/*  250 */         day = StringUtils.getInt(dateAsBytes, offset + 8, offset + 10);
/*      */       }
/*      */       else {
/*  253 */         StringTokenizer st = new StringTokenizer(StringUtils.toString(dateAsBytes, offset, length, "ISO8859_1"), "- ");
/*      */         
/*  255 */         year = Integer.parseInt(st.nextToken());
/*  256 */         month = Integer.parseInt(st.nextToken());
/*  257 */         day = Integer.parseInt(st.nextToken());
/*      */       }
/*      */       
/*      */ 
/*  261 */       return rs.fastDateCreate(targetCalendar, year, month, day);
/*      */     } catch (SQLException sqlEx) {
/*  263 */       throw sqlEx;
/*      */     } catch (Exception e) {
/*  265 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[] { StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1) }), "S1009", this.exceptionInterceptor);
/*      */       
/*      */ 
/*  268 */       sqlEx.initCause(e);
/*      */       
/*  270 */       throw sqlEx;
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
/*      */   public abstract Date getDateFast(int paramInt, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract int getInt(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long getLong(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, byte[] bits, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*  313 */     int year = 0;
/*  314 */     int month = 0;
/*  315 */     int day = 0;
/*      */     
/*  317 */     if (length != 0) {
/*  318 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*      */       
/*  320 */       month = bits[(offset + 2)];
/*  321 */       day = bits[(offset + 3)];
/*      */     }
/*      */     
/*  324 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  325 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*  326 */         return null;
/*  327 */       if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  328 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*  332 */       year = 1;
/*  333 */       month = 1;
/*  334 */       day = 1;
/*      */     }
/*      */     
/*  337 */     if (!rs.useLegacyDatetimeCode) {
/*  338 */       return TimeUtil.fastDateCreate(year, month, day, cal);
/*      */     }
/*      */     
/*  341 */     return rs.fastDateCreate(cal == null ? rs.getCalendarInstanceForSessionOrNew() : cal, year, month, day);
/*      */   }
/*      */   
/*      */   public abstract Date getNativeDate(int paramInt, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl, Calendar paramCalendar)
/*      */     throws SQLException;
/*      */   
/*      */   protected Object getNativeDateTimeValue(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException
/*      */   {
/*  349 */     int year = 0;
/*  350 */     int month = 0;
/*  351 */     int day = 0;
/*      */     
/*  353 */     int hour = 0;
/*  354 */     int minute = 0;
/*  355 */     int seconds = 0;
/*      */     
/*  357 */     int nanos = 0;
/*      */     
/*  359 */     if (bits == null)
/*      */     {
/*  361 */       return null;
/*      */     }
/*      */     
/*  364 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */     
/*  366 */     boolean populatedFromDateTimeValue = false;
/*      */     
/*  368 */     switch (mysqlType) {
/*      */     case 7: 
/*      */     case 12: 
/*  371 */       populatedFromDateTimeValue = true;
/*      */       
/*  373 */       if (length != 0) {
/*  374 */         year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*  375 */         month = bits[(offset + 2)];
/*  376 */         day = bits[(offset + 3)];
/*      */         
/*  378 */         if (length > 4) {
/*  379 */           hour = bits[(offset + 4)];
/*  380 */           minute = bits[(offset + 5)];
/*  381 */           seconds = bits[(offset + 6)];
/*      */         }
/*      */         
/*  384 */         if (length > 7)
/*      */         {
/*  386 */           nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 10: 
/*  393 */       populatedFromDateTimeValue = true;
/*      */       
/*  395 */       if (length != 0) {
/*  396 */         year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*  397 */         month = bits[(offset + 2)];
/*  398 */         day = bits[(offset + 3)];
/*      */       }
/*      */       
/*      */       break;
/*      */     case 11: 
/*  403 */       populatedFromDateTimeValue = true;
/*      */       
/*  405 */       if (length != 0)
/*      */       {
/*      */ 
/*  408 */         hour = bits[(offset + 5)];
/*  409 */         minute = bits[(offset + 6)];
/*  410 */         seconds = bits[(offset + 7)];
/*      */       }
/*      */       
/*  413 */       year = 1970;
/*  414 */       month = 1;
/*  415 */       day = 1;
/*      */       
/*  417 */       break;
/*      */     case 8: case 9: default: 
/*  419 */       populatedFromDateTimeValue = false;
/*      */     }
/*      */     
/*  422 */     switch (jdbcType) {
/*      */     case 92: 
/*  424 */       if (populatedFromDateTimeValue) {
/*  425 */         if (!rs.useLegacyDatetimeCode) {
/*  426 */           return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */         }
/*      */         
/*  429 */         Time time = TimeUtil.fastTimeCreate(rs.getCalendarInstanceForSessionOrNew(), hour, minute, seconds, this.exceptionInterceptor);
/*      */         
/*  431 */         Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */         
/*  433 */         return adjustedTime;
/*      */       }
/*      */       
/*  436 */       return rs.getNativeTimeViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     
/*      */     case 91: 
/*  439 */       if (populatedFromDateTimeValue) {
/*  440 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  441 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  443 */             return null; }
/*  444 */           if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  445 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */           }
/*      */           
/*  448 */           year = 1;
/*  449 */           month = 1;
/*  450 */           day = 1;
/*      */         }
/*      */         
/*  453 */         if (!rs.useLegacyDatetimeCode) {
/*  454 */           return TimeUtil.fastDateCreate(year, month, day, targetCalendar);
/*      */         }
/*      */         
/*  457 */         return rs.fastDateCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */       }
/*      */       
/*  460 */       return rs.getNativeDateViaParseConversion(columnIndex + 1);
/*      */     case 93: 
/*  462 */       if (populatedFromDateTimeValue) {
/*  463 */         if ((year == 0) && (month == 0) && (day == 0)) {
/*  464 */           if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */           {
/*  466 */             return null; }
/*  467 */           if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  468 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */           
/*  471 */           year = 1;
/*  472 */           month = 1;
/*  473 */           day = 1;
/*      */         }
/*      */         
/*  476 */         if (!rs.useLegacyDatetimeCode) {
/*  477 */           return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */         }
/*      */         
/*  480 */         Timestamp ts = rs.fastTimestampCreate(rs.getCalendarInstanceForSessionOrNew(), year, month, day, hour, minute, seconds, nanos);
/*      */         
/*  482 */         Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */         
/*  484 */         return adjustedTs;
/*      */       }
/*      */       
/*  487 */       return rs.getNativeTimestampViaParseConversion(columnIndex + 1, targetCalendar, tz, rollForward);
/*      */     }
/*      */     
/*  490 */     throw new SQLException("Internal error - conversion method doesn't support this type", "S1000");
/*      */   }
/*      */   
/*      */   public abstract Object getNativeDateTimeValue(int paramInt1, Calendar paramCalendar, int paramInt2, int paramInt3, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */   
/*      */   protected double getNativeDouble(byte[] bits, int offset)
/*      */   {
/*  498 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */     
/*      */ 
/*      */ 
/*  502 */     return Double.longBitsToDouble(valueAsLong);
/*      */   }
/*      */   
/*      */   public abstract double getNativeDouble(int paramInt) throws SQLException;
/*      */   
/*      */   protected float getNativeFloat(byte[] bits, int offset) {
/*  508 */     int asInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */     
/*  510 */     return Float.intBitsToFloat(asInt);
/*      */   }
/*      */   
/*      */   public abstract float getNativeFloat(int paramInt) throws SQLException;
/*      */   
/*      */   protected int getNativeInt(byte[] bits, int offset)
/*      */   {
/*  517 */     int valueAsInt = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24;
/*      */     
/*  519 */     return valueAsInt;
/*      */   }
/*      */   
/*      */   public abstract int getNativeInt(int paramInt) throws SQLException;
/*      */   
/*      */   protected long getNativeLong(byte[] bits, int offset) {
/*  525 */     long valueAsLong = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8 | (bits[(offset + 2)] & 0xFF) << 16 | (bits[(offset + 3)] & 0xFF) << 24 | (bits[(offset + 4)] & 0xFF) << 32 | (bits[(offset + 5)] & 0xFF) << 40 | (bits[(offset + 6)] & 0xFF) << 48 | (bits[(offset + 7)] & 0xFF) << 56;
/*      */     
/*      */ 
/*      */ 
/*  529 */     return valueAsLong;
/*      */   }
/*      */   
/*      */   public abstract long getNativeLong(int paramInt) throws SQLException;
/*      */   
/*      */   protected short getNativeShort(byte[] bits, int offset) {
/*  535 */     short asShort = (short)(bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8);
/*      */     
/*  537 */     return asShort;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract short getNativeShort(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Time getNativeTime(int columnIndex, byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  557 */     int hour = 0;
/*  558 */     int minute = 0;
/*  559 */     int seconds = 0;
/*      */     
/*  561 */     if (length != 0)
/*      */     {
/*      */ 
/*  564 */       hour = bits[(offset + 5)];
/*  565 */       minute = bits[(offset + 6)];
/*  566 */       seconds = bits[(offset + 7)];
/*      */     }
/*      */     
/*  569 */     if (!rs.useLegacyDatetimeCode) {
/*  570 */       return TimeUtil.fastTimeCreate(hour, minute, seconds, targetCalendar, this.exceptionInterceptor);
/*      */     }
/*      */     
/*  573 */     Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */     
/*  575 */     Time time = TimeUtil.fastTimeCreate(sessionCalendar, hour, minute, seconds, this.exceptionInterceptor);
/*      */     
/*  577 */     Time adjustedTime = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, time, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */     
/*  579 */     return adjustedTime;
/*      */   }
/*      */   
/*      */   public abstract Time getNativeTime(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */   
/*      */   protected Timestamp getNativeTimestamp(byte[] bits, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException
/*      */   {
/*  587 */     int year = 0;
/*  588 */     int month = 0;
/*  589 */     int day = 0;
/*      */     
/*  591 */     int hour = 0;
/*  592 */     int minute = 0;
/*  593 */     int seconds = 0;
/*      */     
/*  595 */     int nanos = 0;
/*      */     
/*  597 */     if (length != 0) {
/*  598 */       year = bits[(offset + 0)] & 0xFF | (bits[(offset + 1)] & 0xFF) << 8;
/*  599 */       month = bits[(offset + 2)];
/*  600 */       day = bits[(offset + 3)];
/*      */       
/*  602 */       if (length > 4) {
/*  603 */         hour = bits[(offset + 4)];
/*  604 */         minute = bits[(offset + 5)];
/*  605 */         seconds = bits[(offset + 6)];
/*      */       }
/*      */       
/*  608 */       if (length > 7)
/*      */       {
/*  610 */         nanos = (bits[(offset + 7)] & 0xFF | (bits[(offset + 8)] & 0xFF) << 8 | (bits[(offset + 9)] & 0xFF) << 16 | (bits[(offset + 10)] & 0xFF) << 24) * 1000;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  615 */     if ((length == 0) || ((year == 0) && (month == 0) && (day == 0))) {
/*  616 */       if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */       {
/*  618 */         return null; }
/*  619 */       if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  620 */         throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */       }
/*      */       
/*      */ 
/*  624 */       year = 1;
/*  625 */       month = 1;
/*  626 */       day = 1;
/*      */     }
/*      */     
/*  629 */     if (!rs.useLegacyDatetimeCode) {
/*  630 */       return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minute, seconds, nanos);
/*      */     }
/*      */     
/*  633 */     Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */     
/*  635 */     Timestamp ts = rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minute, seconds, nanos);
/*      */     
/*  637 */     Timestamp adjustedTs = TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, ts, conn.getServerTimezoneTZ(), tz, rollForward);
/*      */     
/*  639 */     return adjustedTs;
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
/*      */   public abstract Timestamp getNativeTimestamp(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract Reader getReader(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract String getString(int paramInt, String paramString, MySQLConnection paramMySQLConnection)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getString(String encoding, MySQLConnection conn, byte[] value, int offset, int length)
/*      */     throws SQLException
/*      */   {
/*  689 */     String stringVal = null;
/*      */     
/*  691 */     if ((conn != null) && (conn.getUseUnicode())) {
/*      */       try {
/*  693 */         if (encoding == null) {
/*  694 */           stringVal = StringUtils.toString(value);
/*      */         } else {
/*  696 */           SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
/*      */           
/*  698 */           if (converter != null) {
/*  699 */             stringVal = converter.toString(value, offset, length);
/*      */           } else {
/*  701 */             stringVal = StringUtils.toString(value, offset, length, encoding);
/*      */           }
/*      */         }
/*      */       } catch (UnsupportedEncodingException E) {
/*  705 */         throw SQLError.createSQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100", this.exceptionInterceptor);
/*      */       }
/*      */       
/*      */     } else {
/*  709 */       stringVal = StringUtils.toAsciiString(value, offset, length);
/*      */     }
/*      */     
/*  712 */     return stringVal;
/*      */   }
/*      */   
/*      */   protected Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int fullLength, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs)
/*      */     throws SQLException
/*      */   {
/*  718 */     int hr = 0;
/*  719 */     int min = 0;
/*  720 */     int sec = 0;
/*  721 */     int nanos = 0;
/*      */     
/*  723 */     int decimalIndex = -1;
/*      */     
/*      */     try
/*      */     {
/*  727 */       if (timeAsBytes == null) {
/*  728 */         return null;
/*      */       }
/*      */       
/*  731 */       boolean allZeroTime = true;
/*  732 */       boolean onlyTimePresent = false;
/*      */       
/*  734 */       for (int i = 0; i < fullLength; i++) {
/*  735 */         if (timeAsBytes[(offset + i)] == 58) {
/*  736 */           onlyTimePresent = true;
/*  737 */           break;
/*      */         }
/*      */       }
/*      */       
/*  741 */       for (int i = 0; i < fullLength; i++) {
/*  742 */         if (timeAsBytes[(offset + i)] == 46) {
/*  743 */           decimalIndex = i;
/*  744 */           break;
/*      */         }
/*      */       }
/*      */       
/*  748 */       for (int i = 0; i < fullLength; i++) {
/*  749 */         byte b = timeAsBytes[(offset + i)];
/*      */         
/*  751 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  752 */           onlyTimePresent = false;
/*      */         }
/*      */         
/*  755 */         if ((b != 48) && (b != 32) && (b != 58) && (b != 45) && (b != 47) && (b != 46)) {
/*  756 */           allZeroTime = false;
/*      */           
/*  758 */           break;
/*      */         }
/*      */       }
/*      */       
/*  762 */       if ((!onlyTimePresent) && (allZeroTime)) {
/*  763 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*  764 */           return null;
/*  765 */         if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  766 */           throw SQLError.createSQLException("Value '" + StringUtils.toString(timeAsBytes) + "' can not be represented as java.sql.Time", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  771 */         return rs.fastTimeCreate(targetCalendar, 0, 0, 0);
/*      */       }
/*      */       
/*  774 */       Field timeColField = this.metadata[columnIndex];
/*      */       
/*  776 */       int length = fullLength;
/*      */       
/*  778 */       if (decimalIndex != -1)
/*      */       {
/*  780 */         length = decimalIndex;
/*      */         
/*  782 */         if (decimalIndex + 2 <= fullLength) {
/*  783 */           nanos = StringUtils.getInt(timeAsBytes, offset + decimalIndex + 1, offset + fullLength);
/*      */           
/*  785 */           int numDigits = fullLength - (decimalIndex + 1);
/*      */           
/*  787 */           if (numDigits < 9) {
/*  788 */             int factor = (int)Math.pow(10.0D, 9 - numDigits);
/*  789 */             nanos *= factor;
/*      */           }
/*      */         } else {
/*  792 */           throw new IllegalArgumentException();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       SQLWarning precisionLost;
/*      */       
/*      */ 
/*  801 */       if (timeColField.getMysqlType() == 7)
/*      */       {
/*  803 */         switch (length)
/*      */         {
/*      */         case 19: 
/*  806 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 8, offset + length - 6);
/*  807 */           min = StringUtils.getInt(timeAsBytes, offset + length - 5, offset + length - 3);
/*  808 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */           
/*      */ 
/*  811 */           break;
/*      */         case 12: 
/*      */         case 14: 
/*  814 */           hr = StringUtils.getInt(timeAsBytes, offset + length - 6, offset + length - 4);
/*  815 */           min = StringUtils.getInt(timeAsBytes, offset + length - 4, offset + length - 2);
/*  816 */           sec = StringUtils.getInt(timeAsBytes, offset + length - 2, offset + length);
/*      */           
/*      */ 
/*  819 */           break;
/*      */         
/*      */         case 10: 
/*  822 */           hr = StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*  823 */           min = StringUtils.getInt(timeAsBytes, offset + 8, offset + 10);
/*  824 */           sec = 0;
/*      */           
/*      */ 
/*  827 */           break;
/*      */         case 11: case 13: case 15: case 16: 
/*      */         case 17: case 18: default: 
/*  830 */           throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (columnIndex + 1) + "(" + timeColField + ").", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */         
/*      */         
/*      */ 
/*  835 */         precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + timeColField + ").");
/*      */       }
/*      */       else
/*      */       {
/*      */         SQLWarning precisionLost;
/*      */         
/*      */ 
/*      */ 
/*  843 */         if (timeColField.getMysqlType() == 12) {
/*  844 */           hr = StringUtils.getInt(timeAsBytes, offset + 11, offset + 13);
/*  845 */           min = StringUtils.getInt(timeAsBytes, offset + 14, offset + 16);
/*  846 */           sec = StringUtils.getInt(timeAsBytes, offset + 17, offset + 19);
/*      */           
/*      */ 
/*  849 */           precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (columnIndex + 1) + "(" + timeColField + ").");
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*  858 */           if (timeColField.getMysqlType() == 10) {
/*  859 */             return rs.fastTimeCreate(null, 0, 0, 0);
/*      */           }
/*      */           
/*      */ 
/*  863 */           if ((length != 5) && (length != 8)) {
/*  864 */             throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + StringUtils.toString(timeAsBytes) + Messages.getString("ResultSet.___in_column__268") + (columnIndex + 1), "S1009", this.exceptionInterceptor);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  870 */           hr = StringUtils.getInt(timeAsBytes, offset + 0, offset + 2);
/*  871 */           min = StringUtils.getInt(timeAsBytes, offset + 3, offset + 5);
/*  872 */           sec = length == 5 ? 0 : StringUtils.getInt(timeAsBytes, offset + 6, offset + 8);
/*      */         }
/*      */       }
/*  875 */       Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
/*      */       
/*  877 */       if (!rs.useLegacyDatetimeCode)
/*      */       {
/*      */ 
/*      */ 
/*  881 */         return rs.fastTimeCreate(targetCalendar, hr, min, sec);
/*      */       }
/*      */       
/*  884 */       return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimeCreate(sessionCalendar, hr, min, sec), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/*      */     }
/*      */     catch (RuntimeException ex)
/*      */     {
/*      */ 
/*  890 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", this.exceptionInterceptor);
/*  891 */       sqlEx.initCause(ex);
/*      */       
/*  893 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   public abstract Time getTimeFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */   
/*      */   protected Timestamp getTimestampFast(int columnIndex, byte[] timestampAsBytes, int offset, int length, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  904 */       Calendar sessionCalendar = conn.getUseJDBCCompliantTimezoneShift() ? conn.getUtcCalendar() : rs.getCalendarInstanceForSessionOrNew();
/*      */       
/*  906 */       boolean allZeroTimestamp = true;
/*      */       
/*  908 */       boolean onlyTimePresent = false;
/*      */       
/*  910 */       for (int i = 0; i < length; i++) {
/*  911 */         if (timestampAsBytes[(offset + i)] == 58) {
/*  912 */           onlyTimePresent = true;
/*  913 */           break;
/*      */         }
/*      */       }
/*      */       
/*  917 */       for (int i = 0; i < length; i++) {
/*  918 */         byte b = timestampAsBytes[(offset + i)];
/*      */         
/*  920 */         if ((b == 32) || (b == 45) || (b == 47)) {
/*  921 */           onlyTimePresent = false;
/*      */         }
/*      */         
/*  924 */         if ((b != 48) && (b != 32) && (b != 58) && (b != 45) && (b != 47) && (b != 46)) {
/*  925 */           allZeroTimestamp = false;
/*      */           
/*  927 */           break;
/*      */         }
/*      */       }
/*      */       
/*  931 */       if ((!onlyTimePresent) && (allZeroTimestamp))
/*      */       {
/*  933 */         if ("convertToNull".equals(conn.getZeroDateTimeBehavior()))
/*      */         {
/*  935 */           return null; }
/*  936 */         if ("exception".equals(conn.getZeroDateTimeBehavior())) {
/*  937 */           throw SQLError.createSQLException("Value '" + StringUtils.toString(timestampAsBytes) + "' can not be represented as java.sql.Timestamp", "S1009", this.exceptionInterceptor);
/*      */         }
/*      */         
/*      */ 
/*  941 */         if (!rs.useLegacyDatetimeCode) {
/*  942 */           return TimeUtil.fastTimestampCreate(tz, 1, 1, 1, 0, 0, 0, 0);
/*      */         }
/*      */         
/*  945 */         return rs.fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */       }
/*  947 */       if (this.metadata[columnIndex].getMysqlType() == 13)
/*      */       {
/*  949 */         if (!rs.useLegacyDatetimeCode) {
/*  950 */           return TimeUtil.fastTimestampCreate(tz, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0);
/*      */         }
/*      */         
/*  953 */         return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, StringUtils.getInt(timestampAsBytes, offset, 4), 1, 1, 0, 0, 0, 0), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  959 */       int year = 0;
/*  960 */       int month = 0;
/*  961 */       int day = 0;
/*  962 */       int hour = 0;
/*  963 */       int minutes = 0;
/*  964 */       int seconds = 0;
/*  965 */       int nanos = 0;
/*      */       
/*      */ 
/*  968 */       int decimalIndex = -1;
/*  969 */       for (int i = 0; i < length; i++) {
/*  970 */         if (timestampAsBytes[(offset + i)] == 46) {
/*  971 */           decimalIndex = i;
/*  972 */           break;
/*      */         }
/*      */       }
/*      */       
/*  976 */       if (decimalIndex == offset + length - 1)
/*      */       {
/*  978 */         length--;
/*      */       }
/*  980 */       else if (decimalIndex != -1) {
/*  981 */         if (decimalIndex + 2 <= length) {
/*  982 */           nanos = StringUtils.getInt(timestampAsBytes, offset + decimalIndex + 1, offset + length);
/*      */           
/*  984 */           int numDigits = length - (decimalIndex + 1);
/*      */           
/*  986 */           if (numDigits < 9) {
/*  987 */             int factor = (int)Math.pow(10.0D, 9 - numDigits);
/*  988 */             nanos *= factor;
/*      */           }
/*      */         } else {
/*  991 */           throw new IllegalArgumentException();
/*      */         }
/*      */         
/*      */ 
/*  995 */         length = decimalIndex;
/*      */       }
/*      */       
/*  998 */       switch (length) {
/*      */       case 19: 
/*      */       case 20: 
/*      */       case 21: 
/*      */       case 22: 
/*      */       case 23: 
/*      */       case 24: 
/*      */       case 25: 
/*      */       case 26: 
/*      */       case 29: 
/* 1008 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/* 1009 */         month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/* 1010 */         day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/* 1011 */         hour = StringUtils.getInt(timestampAsBytes, offset + 11, offset + 13);
/* 1012 */         minutes = StringUtils.getInt(timestampAsBytes, offset + 14, offset + 16);
/* 1013 */         seconds = StringUtils.getInt(timestampAsBytes, offset + 17, offset + 19);
/*      */         
/* 1015 */         break;
/*      */       
/*      */ 
/*      */       case 14: 
/* 1019 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/* 1020 */         month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/* 1021 */         day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/* 1022 */         hour = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/* 1023 */         minutes = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/* 1024 */         seconds = StringUtils.getInt(timestampAsBytes, offset + 12, offset + 14);
/*      */         
/* 1026 */         break;
/*      */       
/*      */ 
/*      */       case 12: 
/* 1030 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */         
/* 1032 */         if (year <= 69) {
/* 1033 */           year += 100;
/*      */         }
/*      */         
/* 1036 */         year += 1900;
/*      */         
/* 1038 */         month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/* 1039 */         day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/* 1040 */         hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/* 1041 */         minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/* 1042 */         seconds = StringUtils.getInt(timestampAsBytes, offset + 10, offset + 12);
/*      */         
/* 1044 */         break;
/*      */       
/*      */ 
/*      */       case 10: 
/* 1048 */         boolean hasDash = false;
/*      */         
/* 1050 */         for (int i = 0; i < length; i++) {
/* 1051 */           if (timestampAsBytes[(offset + i)] == 45) {
/* 1052 */             hasDash = true;
/* 1053 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1057 */         if ((this.metadata[columnIndex].getMysqlType() == 10) || (hasDash)) {
/* 1058 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/* 1059 */           month = StringUtils.getInt(timestampAsBytes, offset + 5, offset + 7);
/* 1060 */           day = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/* 1061 */           hour = 0;
/* 1062 */           minutes = 0;
/*      */         } else {
/* 1064 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */           
/* 1066 */           if (year <= 69) {
/* 1067 */             year += 100;
/*      */           }
/*      */           
/* 1070 */           month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/* 1071 */           day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/* 1072 */           hour = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/* 1073 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 8, offset + 10);
/*      */           
/* 1075 */           year += 1900;
/*      */         }
/*      */         
/* 1078 */         break;
/*      */       
/*      */ 
/*      */       case 8: 
/* 1082 */         boolean hasColon = false;
/*      */         
/* 1084 */         for (int i = 0; i < length; i++) {
/* 1085 */           if (timestampAsBytes[(offset + i)] == 58) {
/* 1086 */             hasColon = true;
/* 1087 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1091 */         if (hasColon) {
/* 1092 */           hour = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/* 1093 */           minutes = StringUtils.getInt(timestampAsBytes, offset + 3, offset + 5);
/* 1094 */           seconds = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */           
/* 1096 */           year = 1970;
/* 1097 */           month = 1;
/* 1098 */           day = 1;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1103 */           year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 4);
/* 1104 */           month = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/* 1105 */           day = StringUtils.getInt(timestampAsBytes, offset + 6, offset + 8);
/*      */           
/* 1107 */           year -= 1900;
/* 1108 */           month--;
/*      */         }
/* 1110 */         break;
/*      */       
/*      */ 
/*      */       case 6: 
/* 1114 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */         
/* 1116 */         if (year <= 69) {
/* 1117 */           year += 100;
/*      */         }
/*      */         
/* 1120 */         year += 1900;
/*      */         
/* 1122 */         month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/* 1123 */         day = StringUtils.getInt(timestampAsBytes, offset + 4, offset + 6);
/*      */         
/* 1125 */         break;
/*      */       
/*      */ 
/*      */       case 4: 
/* 1129 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */         
/* 1131 */         if (year <= 69) {
/* 1132 */           year += 100;
/*      */         }
/*      */         
/* 1135 */         month = StringUtils.getInt(timestampAsBytes, offset + 2, offset + 4);
/*      */         
/* 1137 */         day = 1;
/*      */         
/* 1139 */         break;
/*      */       
/*      */ 
/*      */       case 2: 
/* 1143 */         year = StringUtils.getInt(timestampAsBytes, offset + 0, offset + 2);
/*      */         
/* 1145 */         if (year <= 69) {
/* 1146 */           year += 100;
/*      */         }
/*      */         
/* 1149 */         year += 1900;
/* 1150 */         month = 1;
/* 1151 */         day = 1;
/*      */         
/* 1153 */         break;
/*      */       case 3: case 5: case 7: case 9: case 11: 
/*      */       case 13: case 15: case 16: case 17: 
/*      */       case 18: case 27: case 28: default: 
/* 1157 */         throw new SQLException("Bad format for Timestamp '" + StringUtils.toString(timestampAsBytes) + "' in column " + (columnIndex + 1) + ".", "S1009");
/*      */       }
/*      */       
/*      */       
/*      */ 
/* 1162 */       if (!rs.useLegacyDatetimeCode) {
/* 1163 */         return TimeUtil.fastTimestampCreate(tz, year, month, day, hour, minutes, seconds, nanos);
/*      */       }
/*      */       
/* 1166 */       return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, rs.fastTimestampCreate(sessionCalendar, year, month, day, hour, minutes, seconds, nanos), conn.getServerTimezoneTZ(), tz, rollForward);
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/* 1170 */       SQLException sqlEx = SQLError.createSQLException("Cannot convert value '" + getString(columnIndex, "ISO8859_1", conn) + "' from column " + (columnIndex + 1) + " to TIMESTAMP.", "S1009", this.exceptionInterceptor);
/*      */       
/*      */ 
/* 1173 */       sqlEx.initCause(e);
/*      */       
/* 1175 */       throw sqlEx;
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
/*      */   public abstract Timestamp getTimestampFast(int paramInt, Calendar paramCalendar, TimeZone paramTimeZone, boolean paramBoolean, MySQLConnection paramMySQLConnection, ResultSetImpl paramResultSetImpl)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract boolean isFloatingPointNumber(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract boolean isNull(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract long length(int paramInt)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract void setColumnValue(int paramInt, byte[] paramArrayOfByte)
/*      */     throws SQLException;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSetRow setMetadata(Field[] f)
/*      */     throws SQLException
/*      */   {
/* 1240 */     this.metadata = f;
/*      */     
/* 1242 */     return this;
/*      */   }
/*      */   
/*      */   public abstract int getBytesSize();
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ResultSetRow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */