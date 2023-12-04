/*     */ package com.mchange.v2.sql;
/*     */ 
/*     */ import com.mchange.lang.ThrowableUtils;
/*     */ import com.mchange.v2.lang.VersionUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.sql.SQLException;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SqlUtils
/*     */ {
/*  37 */   static final MLogger logger = MLog.getLogger(SqlUtils.class);
/*     */   
/*     */ 
/*  40 */   static final DateFormat tsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
/*     */   
/*     */   public static final String DRIVER_MANAGER_USER_PROPERTY = "user";
/*     */   public static final String DRIVER_MANAGER_PASSWORD_PROPERTY = "password";
/*     */   
/*     */   public static String escapeBadSqlPatternChars(String s)
/*     */   {
/*  47 */     StringBuffer sb = new StringBuffer(s);
/*  48 */     int i = 0; for (int len = sb.length(); i < len; i++)
/*  49 */       if (sb.charAt(i) == '\'')
/*     */       {
/*  51 */         sb.insert(i, '\'');
/*  52 */         len++;
/*  53 */         i += 2;
/*     */       }
/*  55 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static synchronized String escapeAsTimestamp(Date date) {
/*  59 */     return "{ts '" + tsdf.format(date) + "'}";
/*     */   }
/*     */   
/*  62 */   public static SQLException toSQLException(Throwable t) { return toSQLException(null, t); }
/*     */   
/*     */   public static SQLException toSQLException(String msg, Throwable t) {
/*  65 */     return toSQLException(msg, null, t);
/*     */   }
/*     */   
/*     */   public static SQLException toSQLException(String msg, String sqlState, Throwable t) {
/*  69 */     if ((t instanceof SQLException))
/*     */     {
/*  71 */       if (logger.isLoggable(MLevel.FINER))
/*     */       {
/*     */ 
/*     */ 
/*  75 */         SQLException s = (SQLException)t;
/*  76 */         StringBuffer tmp = new StringBuffer(255);
/*  77 */         tmp.append("Attempted to convert SQLException to SQLException. Leaving it alone.");
/*  78 */         tmp.append(" [SQLState: ");
/*  79 */         tmp.append(s.getSQLState());
/*  80 */         tmp.append("; errorCode: ");
/*  81 */         tmp.append(s.getErrorCode());
/*  82 */         tmp.append(']');
/*  83 */         if (msg != null)
/*  84 */           tmp.append(" Ignoring suggested message: '" + msg + "'.");
/*  85 */         logger.log(MLevel.FINER, tmp.toString(), t);
/*     */         
/*  87 */         SQLException s2 = s;
/*  88 */         while ((s2 = s2.getNextException()) != null)
/*  89 */           logger.log(MLevel.FINER, "Nested SQLException or SQLWarning: ", s2);
/*     */       }
/*  91 */       return (SQLException)t;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */     if (logger.isLoggable(MLevel.FINE)) {
/*  99 */       logger.log(MLevel.FINE, "Converting Throwable to SQLException...", t);
/*     */     }
/*     */     
/* 102 */     if (msg == null)
/* 103 */       msg = "An SQLException was provoked by the following failure: " + t.toString();
/* 104 */     if (VersionUtils.isAtLeastJavaVersion14())
/*     */     {
/* 106 */       SQLException out = new SQLException(msg);
/* 107 */       out.initCause(t);
/* 108 */       return out;
/*     */     }
/*     */     
/* 111 */     return new SQLException(msg + System.getProperty("line.separator") + "[Cause: " + ThrowableUtils.extractStackTrace(t) + ']', sqlState);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\SqlUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */