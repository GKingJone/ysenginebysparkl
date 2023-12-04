/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import java.lang.reflect.Method;
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
/*     */ public class LoggerLog
/*     */   extends AbstractLogger
/*     */ {
/*     */   private final Object _logger;
/*     */   private final Method _debugMT;
/*     */   private final Method _debugMAA;
/*     */   private final Method _infoMT;
/*     */   private final Method _infoMAA;
/*     */   private final Method _warnMT;
/*     */   private final Method _warnMAA;
/*     */   private final Method _setDebugEnabledE;
/*     */   private final Method _getLoggerN;
/*     */   private final Method _getName;
/*     */   private volatile boolean _debug;
/*     */   
/*     */   public LoggerLog(Object logger)
/*     */   {
/*     */     try
/*     */     {
/*  44 */       this._logger = logger;
/*  45 */       Class<?> lc = logger.getClass();
/*  46 */       this._debugMT = lc.getMethod("debug", new Class[] { String.class, Throwable.class });
/*  47 */       this._debugMAA = lc.getMethod("debug", new Class[] { String.class, Object[].class });
/*  48 */       this._infoMT = lc.getMethod("info", new Class[] { String.class, Throwable.class });
/*  49 */       this._infoMAA = lc.getMethod("info", new Class[] { String.class, Object[].class });
/*  50 */       this._warnMT = lc.getMethod("warn", new Class[] { String.class, Throwable.class });
/*  51 */       this._warnMAA = lc.getMethod("warn", new Class[] { String.class, Object[].class });
/*  52 */       Method _isDebugEnabled = lc.getMethod("isDebugEnabled", new Class[0]);
/*  53 */       this._setDebugEnabledE = lc.getMethod("setDebugEnabled", new Class[] { Boolean.TYPE });
/*  54 */       this._getLoggerN = lc.getMethod("getLogger", new Class[] { String.class });
/*  55 */       this._getName = lc.getMethod("getName", new Class[0]);
/*     */       
/*  57 */       this._debug = ((Boolean)_isDebugEnabled.invoke(this._logger, new Object[0])).booleanValue();
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/*  61 */       throw new IllegalStateException(x);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*     */     try
/*     */     {
/*  69 */       return (String)this._getName.invoke(this._logger, new Object[0]);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  73 */       e.printStackTrace(); }
/*  74 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void warn(String msg, Object... args)
/*     */   {
/*     */     try
/*     */     {
/*  82 */       this._warnMAA.invoke(this._logger, args);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  86 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void warn(Throwable thrown)
/*     */   {
/*  92 */     warn("", thrown);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable thrown)
/*     */   {
/*     */     try
/*     */     {
/*  99 */       this._warnMT.invoke(this._logger, new Object[] { msg, thrown });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 103 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(String msg, Object... args)
/*     */   {
/*     */     try
/*     */     {
/* 111 */       this._infoMAA.invoke(this._logger, args);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 115 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(Throwable thrown)
/*     */   {
/* 121 */     info("", thrown);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable thrown)
/*     */   {
/*     */     try
/*     */     {
/* 128 */       this._infoMT.invoke(this._logger, new Object[] { msg, thrown });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 132 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 138 */     return this._debug;
/*     */   }
/*     */   
/*     */   public void setDebugEnabled(boolean enabled)
/*     */   {
/*     */     try
/*     */     {
/* 145 */       this._setDebugEnabledE.invoke(this._logger, new Object[] { Boolean.valueOf(enabled) });
/* 146 */       this._debug = enabled;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 150 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void debug(String msg, Object... args)
/*     */   {
/* 157 */     if (!this._debug) {
/* 158 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 162 */       this._debugMAA.invoke(this._logger, args);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 166 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(Throwable thrown)
/*     */   {
/* 172 */     debug("", thrown);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable th)
/*     */   {
/* 177 */     if (!this._debug) {
/* 178 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 182 */       this._debugMT.invoke(this._logger, new Object[] { msg, th });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 186 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(String msg, long value)
/*     */   {
/* 192 */     if (!this._debug) {
/* 193 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 197 */       this._debugMAA.invoke(this._logger, new Object[] { new Long(value) });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 201 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void ignore(Throwable ignored)
/*     */   {
/* 207 */     if (Log.isIgnored())
/*     */     {
/* 209 */       warn("IGNORED ", ignored);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Logger newLogger(String fullname)
/*     */   {
/*     */     try
/*     */     {
/* 220 */       Object logger = this._getLoggerN.invoke(this._logger, new Object[] { fullname });
/* 221 */       return new LoggerLog(logger);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 225 */       e.printStackTrace(); }
/* 226 */     return this;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\LoggerLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */