/*     */ package com.mchange.v2.log.log4j;
/*     */ 
/*     */ import com.mchange.v2.log.LogUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.ResourceBundle;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
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
/*     */ public final class Log4jMLog
/*     */   extends MLog
/*     */ {
/*     */   static final String CHECK_CLASS = "org.apache.log4j.Logger";
/*  38 */   MLogger global = null;
/*     */   
/*     */   public Log4jMLog() throws ClassNotFoundException {
/*  41 */     Class.forName("org.apache.log4j.Logger");
/*     */   }
/*     */   
/*     */   public MLogger getMLogger(String name) {
/*  45 */     Logger lg = Logger.getLogger(name);
/*  46 */     return new Log4jMLogger(lg);
/*     */   }
/*     */   
/*     */   public MLogger getMLogger(Class cl)
/*     */   {
/*  51 */     Logger lg = Logger.getLogger(cl);
/*  52 */     return new Log4jMLogger(lg);
/*     */   }
/*     */   
/*     */ 
/*     */   public MLogger getMLogger()
/*     */   {
/*  58 */     Logger lg = Logger.getRootLogger();
/*  59 */     return new Log4jMLogger(lg);
/*     */   }
/*     */   
/*     */   private static final class Log4jMLogger implements MLogger
/*     */   {
/*  64 */     static final String FQCN = Log4jMLogger.class.getName();
/*     */     
/*     */ 
/*  67 */     MLevel myLevel = null;
/*     */     volatile Logger logger;
/*     */     
/*     */     Log4jMLogger(Logger logger)
/*     */     {
/*  72 */       this.logger = logger;
/*     */     }
/*     */     
/*     */     private static MLevel guessMLevel(Level lvl) {
/*  76 */       if (lvl == null)
/*  77 */         return null;
/*  78 */       if (lvl == Level.ALL)
/*  79 */         return MLevel.ALL;
/*  80 */       if (lvl == Level.DEBUG)
/*  81 */         return MLevel.FINEST;
/*  82 */       if (lvl == Level.ERROR)
/*  83 */         return MLevel.SEVERE;
/*  84 */       if (lvl == Level.FATAL)
/*  85 */         return MLevel.SEVERE;
/*  86 */       if (lvl == Level.INFO)
/*  87 */         return MLevel.INFO;
/*  88 */       if (lvl == Level.OFF)
/*  89 */         return MLevel.OFF;
/*  90 */       if (lvl == Level.WARN) {
/*  91 */         return MLevel.WARNING;
/*     */       }
/*  93 */       throw new IllegalArgumentException("Unknown level: " + lvl);
/*     */     }
/*     */     
/*     */     private static Level level(MLevel lvl)
/*     */     {
/*  98 */       if (lvl == null)
/*  99 */         return null;
/* 100 */       if (lvl == MLevel.ALL)
/* 101 */         return Level.ALL;
/* 102 */       if (lvl == MLevel.CONFIG)
/* 103 */         return Level.DEBUG;
/* 104 */       if (lvl == MLevel.FINE)
/* 105 */         return Level.DEBUG;
/* 106 */       if (lvl == MLevel.FINER)
/* 107 */         return Level.DEBUG;
/* 108 */       if (lvl == MLevel.FINEST)
/* 109 */         return Level.DEBUG;
/* 110 */       if (lvl == MLevel.INFO)
/* 111 */         return Level.INFO;
/* 112 */       if (lvl == MLevel.INFO)
/* 113 */         return Level.OFF;
/* 114 */       if (lvl == MLevel.SEVERE)
/* 115 */         return Level.ERROR;
/* 116 */       if (lvl == MLevel.WARNING) {
/* 117 */         return Level.WARN;
/*     */       }
/* 119 */       throw new IllegalArgumentException("Unknown MLevel: " + lvl);
/*     */     }
/*     */     
/*     */     private static String createMessage(String srcClass, String srcMeth, String msg)
/*     */     {
/* 124 */       StringBuffer sb = new StringBuffer(511);
/* 125 */       sb.append("[class: ");
/* 126 */       sb.append(srcClass);
/* 127 */       sb.append("; method: ");
/* 128 */       sb.append(srcMeth);
/* 129 */       if (!srcMeth.endsWith(")"))
/* 130 */         sb.append("()");
/* 131 */       sb.append("] ");
/* 132 */       sb.append(msg);
/* 133 */       return sb.toString();
/*     */     }
/*     */     
/*     */     private static String createMessage(String srcMeth, String msg)
/*     */     {
/* 138 */       StringBuffer sb = new StringBuffer(511);
/* 139 */       sb.append("[method: ");
/* 140 */       sb.append(srcMeth);
/* 141 */       if (!srcMeth.endsWith(")"))
/* 142 */         sb.append("()");
/* 143 */       sb.append("] ");
/* 144 */       sb.append(msg);
/* 145 */       return sb.toString();
/*     */     }
/*     */     
/*     */     public ResourceBundle getResourceBundle() {
/* 149 */       return null;
/*     */     }
/*     */     
/* 152 */     public String getResourceBundleName() { return null; }
/*     */     
/*     */     public void setFilter(Object java14Filter) throws SecurityException {
/* 155 */       warning("setFilter() not supported by MLogger " + getClass().getName());
/*     */     }
/*     */     
/* 158 */     public Object getFilter() { return null; }
/*     */     
/*     */     private void log(Level lvl, Object msg, Throwable t) {
/* 161 */       this.logger.log(FQCN, lvl, msg, t);
/*     */     }
/*     */     
/* 164 */     public void log(MLevel l, String msg) { log(level(l), msg, null); }
/*     */     
/*     */     public void log(MLevel l, String msg, Object param) {
/* 167 */       log(level(l), msg != null ? MessageFormat.format(msg, new Object[] { param }) : null, null);
/*     */     }
/*     */     
/* 170 */     public void log(MLevel l, String msg, Object[] params) { log(level(l), msg != null ? MessageFormat.format(msg, params) : null, null); }
/*     */     
/*     */     public void log(MLevel l, String msg, Throwable t) {
/* 173 */       log(level(l), msg, t);
/*     */     }
/*     */     
/* 176 */     public void logp(MLevel l, String srcClass, String srcMeth, String msg) { log(level(l), createMessage(srcClass, srcMeth, msg), null); }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object param) {
/* 179 */       log(level(l), createMessage(srcClass, srcMeth, msg != null ? MessageFormat.format(msg, new Object[] { param }) : null), null);
/*     */     }
/*     */     
/* 182 */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object[] params) { log(level(l), createMessage(srcClass, srcMeth, msg != null ? MessageFormat.format(msg, params) : null), null); }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Throwable t) {
/* 185 */       log(level(l), createMessage(srcClass, srcMeth, msg), t);
/*     */     }
/*     */     
/* 188 */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg) { log(level(l), createMessage(srcClass, srcMeth, Log4jMLog.formatMessage(rb, msg, null)), null); }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object param) {
/* 191 */       log(level(l), createMessage(srcClass, srcMeth, Log4jMLog.formatMessage(rb, msg, new Object[] { param })), null);
/*     */     }
/*     */     
/* 194 */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object[] params) { log(level(l), createMessage(srcClass, srcMeth, Log4jMLog.formatMessage(rb, msg, params)), null); }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Throwable t) {
/* 197 */       log(level(l), createMessage(srcClass, srcMeth, Log4jMLog.formatMessage(rb, msg, null)), t);
/*     */     }
/*     */     
/* 200 */     public void entering(String srcClass, String srcMeth) { log(Level.DEBUG, createMessage(srcClass, srcMeth, "entering method."), null); }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth, Object param) {
/* 203 */       log(Level.DEBUG, createMessage(srcClass, srcMeth, "entering method... param: " + param.toString()), null);
/*     */     }
/*     */     
/* 206 */     public void entering(String srcClass, String srcMeth, Object[] params) { log(Level.DEBUG, createMessage(srcClass, srcMeth, "entering method... " + LogUtils.createParamsList(params)), null); }
/*     */     
/*     */     public void exiting(String srcClass, String srcMeth) {
/* 209 */       log(Level.DEBUG, createMessage(srcClass, srcMeth, "exiting method."), null);
/*     */     }
/*     */     
/* 212 */     public void exiting(String srcClass, String srcMeth, Object result) { log(Level.DEBUG, createMessage(srcClass, srcMeth, "exiting method... result: " + result.toString()), null); }
/*     */     
/*     */     public void throwing(String srcClass, String srcMeth, Throwable t) {
/* 215 */       log(Level.DEBUG, createMessage(srcClass, srcMeth, "throwing exception... "), t);
/*     */     }
/*     */     
/* 218 */     public void severe(String msg) { log(Level.ERROR, msg, null); }
/*     */     
/*     */     public void warning(String msg) {
/* 221 */       log(Level.WARN, msg, null);
/*     */     }
/*     */     
/* 224 */     public void info(String msg) { log(Level.INFO, msg, null); }
/*     */     
/*     */     public void config(String msg) {
/* 227 */       log(Level.DEBUG, msg, null);
/*     */     }
/*     */     
/* 230 */     public void fine(String msg) { log(Level.DEBUG, msg, null); }
/*     */     
/*     */     public void finer(String msg) {
/* 233 */       log(Level.DEBUG, msg, null);
/*     */     }
/*     */     
/* 236 */     public void finest(String msg) { log(Level.DEBUG, msg, null); }
/*     */     
/*     */     public synchronized void setLevel(MLevel l) throws SecurityException
/*     */     {
/* 240 */       this.logger.setLevel(level(l));
/* 241 */       this.myLevel = l;
/*     */     }
/*     */     
/*     */ 
/*     */     public synchronized MLevel getLevel()
/*     */     {
/* 247 */       if (this.myLevel == null)
/* 248 */         this.myLevel = guessMLevel(this.logger.getLevel());
/* 249 */       return this.myLevel;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isLoggable(MLevel l)
/*     */     {
/* 257 */       return this.logger.isEnabledFor(level(l));
/*     */     }
/*     */     
/*     */     public String getName() {
/* 261 */       return this.logger.getName();
/*     */     }
/*     */     
/*     */     public void addHandler(Object h) throws SecurityException {
/* 265 */       if (!(h instanceof Appender))
/* 266 */         throw new IllegalArgumentException("The 'handler' " + h + " is not compatible with MLogger " + this);
/* 267 */       this.logger.addAppender((Appender)h);
/*     */     }
/*     */     
/*     */     public void removeHandler(Object h) throws SecurityException
/*     */     {
/* 272 */       if (!(h instanceof Appender))
/* 273 */         throw new IllegalArgumentException("The 'handler' " + h + " is not compatible with MLogger " + this);
/* 274 */       this.logger.removeAppender((Appender)h);
/*     */     }
/*     */     
/*     */     public Object[] getHandlers()
/*     */     {
/* 279 */       List tmp = new LinkedList();
/* 280 */       for (Enumeration e = this.logger.getAllAppenders(); e.hasMoreElements();)
/* 281 */         tmp.add(e.nextElement());
/* 282 */       return tmp.toArray();
/*     */     }
/*     */     
/*     */     public void setUseParentHandlers(boolean uph) {
/* 286 */       this.logger.setAdditivity(uph);
/*     */     }
/*     */     
/* 289 */     public boolean getUseParentHandlers() { return this.logger.getAdditivity(); }
/*     */   }
/*     */   
/*     */   private static String formatMessage(String rbname, String msg, Object[] params)
/*     */   {
/* 294 */     if (msg == null)
/*     */     {
/* 296 */       if (params == null) {
/* 297 */         return "";
/*     */       }
/* 299 */       return LogUtils.createParamsList(params);
/*     */     }
/*     */     
/*     */ 
/* 303 */     ResourceBundle rb = ResourceBundle.getBundle(rbname);
/* 304 */     if (rb != null)
/*     */     {
/* 306 */       String check = rb.getString(msg);
/* 307 */       if (check != null)
/* 308 */         msg = check;
/*     */     }
/* 310 */     return params == null ? msg : MessageFormat.format(msg, params);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\log4j\Log4jMLog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */