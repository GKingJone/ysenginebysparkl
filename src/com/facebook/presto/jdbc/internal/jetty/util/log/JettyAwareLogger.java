/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.helpers.FormattingTuple;
/*     */ import org.slf4j.helpers.MessageFormatter;
/*     */ import org.slf4j.spi.LocationAwareLogger;
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
/*     */ class JettyAwareLogger
/*     */   implements Logger
/*     */ {
/*     */   private static final int DEBUG = 10;
/*     */   private static final int ERROR = 40;
/*     */   private static final int INFO = 20;
/*     */   private static final int TRACE = 0;
/*     */   private static final int WARN = 30;
/*  39 */   private static final String FQCN = Slf4jLog.class.getName();
/*     */   private final LocationAwareLogger _logger;
/*     */   
/*     */   public JettyAwareLogger(LocationAwareLogger logger)
/*     */   {
/*  44 */     this._logger = logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  53 */     return this._logger.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/*  62 */     return this._logger.isTraceEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(String msg)
/*     */   {
/*  71 */     log(null, 0, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(String format, Object arg)
/*     */   {
/*  80 */     log(null, 0, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(String format, Object arg1, Object arg2)
/*     */   {
/*  89 */     log(null, 0, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(String format, Object[] argArray)
/*     */   {
/*  98 */     log(null, 0, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(String msg, Throwable t)
/*     */   {
/* 107 */     log(null, 0, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isTraceEnabled(Marker marker)
/*     */   {
/* 116 */     return this._logger.isTraceEnabled(marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(Marker marker, String msg)
/*     */   {
/* 125 */     log(marker, 0, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(Marker marker, String format, Object arg)
/*     */   {
/* 134 */     log(marker, 0, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2)
/*     */   {
/* 143 */     log(marker, 0, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(Marker marker, String format, Object[] argArray)
/*     */   {
/* 152 */     log(marker, 0, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trace(Marker marker, String msg, Throwable t)
/*     */   {
/* 161 */     log(marker, 0, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 170 */     return this._logger.isDebugEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String msg)
/*     */   {
/* 179 */     log(null, 10, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String format, Object arg)
/*     */   {
/* 188 */     log(null, 10, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String format, Object arg1, Object arg2)
/*     */   {
/* 197 */     log(null, 10, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String format, Object[] argArray)
/*     */   {
/* 206 */     log(null, 10, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String msg, Throwable t)
/*     */   {
/* 215 */     log(null, 10, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDebugEnabled(Marker marker)
/*     */   {
/* 224 */     return this._logger.isDebugEnabled(marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Marker marker, String msg)
/*     */   {
/* 233 */     log(marker, 10, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Marker marker, String format, Object arg)
/*     */   {
/* 242 */     log(marker, 10, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2)
/*     */   {
/* 251 */     log(marker, 10, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Marker marker, String format, Object[] argArray)
/*     */   {
/* 260 */     log(marker, 10, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Marker marker, String msg, Throwable t)
/*     */   {
/* 269 */     log(marker, 10, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 278 */     return this._logger.isInfoEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String msg)
/*     */   {
/* 287 */     log(null, 20, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String format, Object arg)
/*     */   {
/* 296 */     log(null, 20, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String format, Object arg1, Object arg2)
/*     */   {
/* 305 */     log(null, 20, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String format, Object[] argArray)
/*     */   {
/* 314 */     log(null, 20, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String msg, Throwable t)
/*     */   {
/* 323 */     log(null, 20, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInfoEnabled(Marker marker)
/*     */   {
/* 332 */     return this._logger.isInfoEnabled(marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(Marker marker, String msg)
/*     */   {
/* 341 */     log(marker, 20, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(Marker marker, String format, Object arg)
/*     */   {
/* 350 */     log(marker, 20, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2)
/*     */   {
/* 359 */     log(marker, 20, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(Marker marker, String format, Object[] argArray)
/*     */   {
/* 368 */     log(marker, 20, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(Marker marker, String msg, Throwable t)
/*     */   {
/* 377 */     log(marker, 20, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 386 */     return this._logger.isWarnEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String msg)
/*     */   {
/* 395 */     log(null, 30, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String format, Object arg)
/*     */   {
/* 404 */     log(null, 30, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String format, Object[] argArray)
/*     */   {
/* 413 */     log(null, 30, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String format, Object arg1, Object arg2)
/*     */   {
/* 422 */     log(null, 30, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String msg, Throwable t)
/*     */   {
/* 431 */     log(null, 30, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWarnEnabled(Marker marker)
/*     */   {
/* 440 */     return this._logger.isWarnEnabled(marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Marker marker, String msg)
/*     */   {
/* 449 */     log(marker, 30, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Marker marker, String format, Object arg)
/*     */   {
/* 458 */     log(marker, 30, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2)
/*     */   {
/* 467 */     log(marker, 30, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Marker marker, String format, Object[] argArray)
/*     */   {
/* 476 */     log(marker, 30, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Marker marker, String msg, Throwable t)
/*     */   {
/* 485 */     log(marker, 30, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 494 */     return this._logger.isErrorEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String msg)
/*     */   {
/* 503 */     log(null, 40, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String format, Object arg)
/*     */   {
/* 512 */     log(null, 40, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String format, Object arg1, Object arg2)
/*     */   {
/* 521 */     log(null, 40, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String format, Object[] argArray)
/*     */   {
/* 530 */     log(null, 40, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String msg, Throwable t)
/*     */   {
/* 539 */     log(null, 40, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isErrorEnabled(Marker marker)
/*     */   {
/* 548 */     return this._logger.isErrorEnabled(marker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Marker marker, String msg)
/*     */   {
/* 557 */     log(marker, 40, msg, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Marker marker, String format, Object arg)
/*     */   {
/* 566 */     log(marker, 40, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2)
/*     */   {
/* 575 */     log(marker, 40, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Marker marker, String format, Object[] argArray)
/*     */   {
/* 584 */     log(marker, 40, format, argArray, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Marker marker, String msg, Throwable t)
/*     */   {
/* 593 */     log(marker, 40, msg, null, t);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 599 */     return this._logger.toString();
/*     */   }
/*     */   
/*     */   private void log(Marker marker, int level, String msg, Object[] argArray, Throwable t)
/*     */   {
/* 604 */     if (argArray == null)
/*     */     {
/*     */ 
/* 607 */       this._logger.log(marker, FQCN, level, msg, null, t);
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 614 */       int loggerLevel = this._logger.isWarnEnabled() ? 30 : this._logger.isInfoEnabled() ? 20 : this._logger.isDebugEnabled() ? 10 : this._logger.isTraceEnabled() ? 0 : 40;
/* 615 */       if (loggerLevel <= level)
/*     */       {
/*     */ 
/*     */ 
/* 619 */         FormattingTuple ft = MessageFormatter.arrayFormat(msg, argArray);
/* 620 */         this._logger.log(marker, FQCN, level, ft.getMessage(), null, t);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\JettyAwareLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */