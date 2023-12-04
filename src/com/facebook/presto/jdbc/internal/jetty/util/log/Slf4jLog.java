/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class Slf4jLog
/*     */   extends AbstractLogger
/*     */ {
/*     */   private final org.slf4j.Logger _logger;
/*     */   
/*     */   public Slf4jLog()
/*     */     throws Exception
/*     */   {
/*  32 */     this("com.facebook.presto.jdbc.internal.jetty.util.log");
/*     */   }
/*     */   
/*     */ 
/*     */   public Slf4jLog(String name)
/*     */   {
/*  38 */     org.slf4j.Logger logger = LoggerFactory.getLogger(name);
/*     */     
/*     */ 
/*     */ 
/*  42 */     if ((logger instanceof LocationAwareLogger))
/*     */     {
/*  44 */       this._logger = new JettyAwareLogger((LocationAwareLogger)logger);
/*     */     }
/*     */     else
/*     */     {
/*  48 */       this._logger = logger;
/*     */     }
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*  54 */     return this._logger.getName();
/*     */   }
/*     */   
/*     */   public void warn(String msg, Object... args)
/*     */   {
/*  59 */     this._logger.warn(msg, args);
/*     */   }
/*     */   
/*     */   public void warn(Throwable thrown)
/*     */   {
/*  64 */     warn("", thrown);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable thrown)
/*     */   {
/*  69 */     this._logger.warn(msg, thrown);
/*     */   }
/*     */   
/*     */   public void info(String msg, Object... args)
/*     */   {
/*  74 */     this._logger.info(msg, args);
/*     */   }
/*     */   
/*     */   public void info(Throwable thrown)
/*     */   {
/*  79 */     info("", thrown);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable thrown)
/*     */   {
/*  84 */     this._logger.info(msg, thrown);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Object... args)
/*     */   {
/*  89 */     this._logger.debug(msg, args);
/*     */   }
/*     */   
/*     */   public void debug(String msg, long arg)
/*     */   {
/*  94 */     if (isDebugEnabled()) {
/*  95 */       this._logger.debug(msg, new Object[] { new Long(arg) });
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(Throwable thrown) {
/* 100 */     debug("", thrown);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable thrown)
/*     */   {
/* 105 */     this._logger.debug(msg, thrown);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 110 */     return this._logger.isDebugEnabled();
/*     */   }
/*     */   
/*     */   public void setDebugEnabled(boolean enabled)
/*     */   {
/* 115 */     warn("setDebugEnabled not implemented", new Object[] { null, null });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Logger newLogger(String fullname)
/*     */   {
/* 123 */     return new Slf4jLog(fullname);
/*     */   }
/*     */   
/*     */   public void ignore(Throwable ignored)
/*     */   {
/* 128 */     if (Log.isIgnored())
/*     */     {
/* 130 */       warn("IGNORED ", ignored);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 137 */     return this._logger.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\Slf4jLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */