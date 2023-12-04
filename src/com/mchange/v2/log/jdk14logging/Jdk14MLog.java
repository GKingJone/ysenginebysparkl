/*     */ package com.mchange.v2.log.jdk14logging;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.util.DoubleWeakHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.logging.Filter;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.Logger;
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
/*     */ public final class Jdk14MLog
/*     */   extends MLog
/*     */ {
/*  33 */   private static String[] UNKNOWN_ARRAY = { "UNKNOWN_CLASS", "UNKNOWN_METHOD" };
/*     */   
/*     */   private static final String CHECK_CLASS = "java.util.logging.Logger";
/*     */   
/*  37 */   private final Map namedLoggerMap = new DoubleWeakHashMap();
/*     */   
/*  39 */   MLogger global = null;
/*     */   
/*     */   public Jdk14MLog() throws ClassNotFoundException {
/*  42 */     Class.forName("java.util.logging.Logger");
/*     */   }
/*     */   
/*     */   public synchronized MLogger getMLogger(String name) {
/*  46 */     name = name.intern();
/*     */     
/*  48 */     MLogger out = (MLogger)this.namedLoggerMap.get(name);
/*  49 */     if (out == null)
/*     */     {
/*  51 */       Logger lg = Logger.getLogger(name);
/*  52 */       out = new Jdk14MLogger(lg);
/*  53 */       this.namedLoggerMap.put(name, out);
/*     */     }
/*  55 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized MLogger getMLogger(Class cl) {
/*  59 */     return getLogger(cl.getName());
/*     */   }
/*     */   
/*     */   public synchronized MLogger getMLogger()
/*     */   {
/*  64 */     if (this.global == null)
/*  65 */       this.global = new Jdk14MLogger(LogManager.getLogManager().getLogger("global"));
/*  66 */     return this.global;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String[] findCallingClassAndMethod()
/*     */   {
/*  76 */     StackTraceElement[] ste = new Throwable().getStackTrace();
/*  77 */     int i = 0; for (int len = ste.length; i < len; i++)
/*     */     {
/*  79 */       StackTraceElement check = ste[i];
/*  80 */       String cn = check.getClassName();
/*  81 */       if ((cn != null) && (!cn.startsWith("com.mchange.v2.log.jdk14logging")))
/*  82 */         return new String[] { check.getClassName(), check.getMethodName() };
/*     */     }
/*  84 */     return UNKNOWN_ARRAY;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class Jdk14MLogger
/*     */     implements MLogger
/*     */   {
/*     */     volatile Logger logger;
/*     */     
/*     */     Jdk14MLogger(Logger logger)
/*     */     {
/*  95 */       this.logger = logger;
/*     */     }
/*     */     
/*     */     private static Level level(MLevel lvl)
/*     */     {
/* 100 */       return (Level)lvl.asJdk14Level();
/*     */     }
/*     */     
/* 103 */     public ResourceBundle getResourceBundle() { return this.logger.getResourceBundle(); }
/*     */     
/*     */     public String getResourceBundleName() {
/* 106 */       return this.logger.getResourceBundleName();
/*     */     }
/*     */     
/*     */     public void setFilter(Object java14Filter) throws SecurityException {
/* 110 */       if (!(java14Filter instanceof Filter)) {
/* 111 */         throw new IllegalArgumentException("MLogger.setFilter( ... ) requires a java.util.logging.Filter. This is not enforced by the compiler only to permit building under jdk 1.3");
/*     */       }
/* 113 */       this.logger.setFilter((Filter)java14Filter);
/*     */     }
/*     */     
/*     */     public Object getFilter() {
/* 117 */       return this.logger.getFilter();
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg) {
/* 121 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 123 */       String[] sa = Jdk14MLog.access$000();
/* 124 */       this.logger.logp(level(l), sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Object param)
/*     */     {
/* 129 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 131 */       String[] sa = Jdk14MLog.access$000();
/* 132 */       this.logger.logp(level(l), sa[0], sa[1], msg, param);
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Object[] params)
/*     */     {
/* 137 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 139 */       String[] sa = Jdk14MLog.access$000();
/* 140 */       this.logger.logp(level(l), sa[0], sa[1], msg, params);
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Throwable t)
/*     */     {
/* 145 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 147 */       String[] sa = Jdk14MLog.access$000();
/* 148 */       this.logger.logp(level(l), sa[0], sa[1], msg, t);
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg)
/*     */     {
/* 153 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 155 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 157 */         String[] sa = Jdk14MLog.access$000();
/* 158 */         srcClass = sa[0];
/* 159 */         srcMeth = sa[1];
/*     */       }
/* 161 */       this.logger.logp(level(l), srcClass, srcMeth, msg);
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object param)
/*     */     {
/* 166 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 168 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 170 */         String[] sa = Jdk14MLog.access$000();
/* 171 */         srcClass = sa[0];
/* 172 */         srcMeth = sa[1];
/*     */       }
/* 174 */       this.logger.logp(level(l), srcClass, srcMeth, msg, param);
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object[] params)
/*     */     {
/* 179 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 181 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 183 */         String[] sa = Jdk14MLog.access$000();
/* 184 */         srcClass = sa[0];
/* 185 */         srcMeth = sa[1];
/*     */       }
/* 187 */       this.logger.logp(level(l), srcClass, srcMeth, msg, params);
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Throwable t)
/*     */     {
/* 192 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 194 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 196 */         String[] sa = Jdk14MLog.access$000();
/* 197 */         srcClass = sa[0];
/* 198 */         srcMeth = sa[1];
/*     */       }
/* 200 */       this.logger.logp(level(l), srcClass, srcMeth, msg, t);
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg)
/*     */     {
/* 205 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 207 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 209 */         String[] sa = Jdk14MLog.access$000();
/* 210 */         srcClass = sa[0];
/* 211 */         srcMeth = sa[1];
/*     */       }
/* 213 */       this.logger.logrb(level(l), srcClass, srcMeth, rb, msg);
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object param)
/*     */     {
/* 218 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 220 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 222 */         String[] sa = Jdk14MLog.access$000();
/* 223 */         srcClass = sa[0];
/* 224 */         srcMeth = sa[1];
/*     */       }
/* 226 */       this.logger.logrb(level(l), srcClass, srcMeth, rb, msg, param);
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object[] params)
/*     */     {
/* 231 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 233 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 235 */         String[] sa = Jdk14MLog.access$000();
/* 236 */         srcClass = sa[0];
/* 237 */         srcMeth = sa[1];
/*     */       }
/* 239 */       this.logger.logrb(level(l), srcClass, srcMeth, rb, msg, params);
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Throwable t)
/*     */     {
/* 244 */       if (!this.logger.isLoggable(level(l))) { return;
/*     */       }
/* 246 */       if ((srcClass == null) && (srcMeth == null))
/*     */       {
/* 248 */         String[] sa = Jdk14MLog.access$000();
/* 249 */         srcClass = sa[0];
/* 250 */         srcMeth = sa[1];
/*     */       }
/* 252 */       this.logger.logrb(level(l), srcClass, srcMeth, rb, msg, t);
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth)
/*     */     {
/* 257 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 259 */       this.logger.entering(srcClass, srcMeth);
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth, Object param)
/*     */     {
/* 264 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 266 */       this.logger.entering(srcClass, srcMeth, param);
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth, Object[] params)
/*     */     {
/* 271 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 273 */       this.logger.entering(srcClass, srcMeth, params);
/*     */     }
/*     */     
/*     */     public void exiting(String srcClass, String srcMeth)
/*     */     {
/* 278 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 280 */       this.logger.exiting(srcClass, srcMeth);
/*     */     }
/*     */     
/*     */     public void exiting(String srcClass, String srcMeth, Object result)
/*     */     {
/* 285 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 287 */       this.logger.exiting(srcClass, srcMeth, result);
/*     */     }
/*     */     
/*     */     public void throwing(String srcClass, String srcMeth, Throwable t)
/*     */     {
/* 292 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 294 */       this.logger.throwing(srcClass, srcMeth, t);
/*     */     }
/*     */     
/*     */     public void severe(String msg)
/*     */     {
/* 299 */       if (!this.logger.isLoggable(Level.SEVERE)) { return;
/*     */       }
/* 301 */       String[] sa = Jdk14MLog.access$000();
/* 302 */       this.logger.logp(Level.SEVERE, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void warning(String msg)
/*     */     {
/* 307 */       if (!this.logger.isLoggable(Level.WARNING)) { return;
/*     */       }
/* 309 */       String[] sa = Jdk14MLog.access$000();
/* 310 */       this.logger.logp(Level.WARNING, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void info(String msg)
/*     */     {
/* 315 */       if (!this.logger.isLoggable(Level.INFO)) { return;
/*     */       }
/* 317 */       String[] sa = Jdk14MLog.access$000();
/* 318 */       this.logger.logp(Level.INFO, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void config(String msg)
/*     */     {
/* 323 */       if (!this.logger.isLoggable(Level.CONFIG)) { return;
/*     */       }
/* 325 */       String[] sa = Jdk14MLog.access$000();
/* 326 */       this.logger.logp(Level.CONFIG, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void fine(String msg)
/*     */     {
/* 331 */       if (!this.logger.isLoggable(Level.FINE)) { return;
/*     */       }
/* 333 */       String[] sa = Jdk14MLog.access$000();
/* 334 */       this.logger.logp(Level.FINE, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void finer(String msg)
/*     */     {
/* 339 */       if (!this.logger.isLoggable(Level.FINER)) { return;
/*     */       }
/* 341 */       String[] sa = Jdk14MLog.access$000();
/* 342 */       this.logger.logp(Level.FINER, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void finest(String msg)
/*     */     {
/* 347 */       if (!this.logger.isLoggable(Level.FINEST)) { return;
/*     */       }
/* 349 */       String[] sa = Jdk14MLog.access$000();
/* 350 */       this.logger.logp(Level.FINEST, sa[0], sa[1], msg);
/*     */     }
/*     */     
/*     */     public void setLevel(MLevel l) throws SecurityException {
/* 354 */       this.logger.setLevel(level(l));
/*     */     }
/*     */     
/* 357 */     public MLevel getLevel() { return MLevel.fromIntValue(this.logger.getLevel().intValue()); }
/*     */     
/*     */     public boolean isLoggable(MLevel l) {
/* 360 */       return this.logger.isLoggable(level(l));
/*     */     }
/*     */     
/* 363 */     public String getName() { return this.logger.getName(); }
/*     */     
/*     */     public void addHandler(Object h) throws SecurityException
/*     */     {
/* 367 */       if (!(h instanceof Handler)) {
/* 368 */         throw new IllegalArgumentException("MLogger.addHandler( ... ) requires a java.util.logging.Handler. This is not enforced by the compiler only to permit building under jdk 1.3");
/*     */       }
/* 370 */       this.logger.addHandler((Handler)h);
/*     */     }
/*     */     
/*     */     public void removeHandler(Object h) throws SecurityException
/*     */     {
/* 375 */       if (!(h instanceof Handler)) {
/* 376 */         throw new IllegalArgumentException("MLogger.removeHandler( ... ) requires a java.util.logging.Handler. This is not enforced by the compiler only to permit building under jdk 1.3");
/*     */       }
/* 378 */       this.logger.removeHandler((Handler)h);
/*     */     }
/*     */     
/*     */     public Object[] getHandlers() {
/* 382 */       return this.logger.getHandlers();
/*     */     }
/*     */     
/* 385 */     public void setUseParentHandlers(boolean uph) { this.logger.setUseParentHandlers(uph); }
/*     */     
/*     */     public boolean getUseParentHandlers() {
/* 388 */       return this.logger.getUseParentHandlers();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\jdk14logging\Jdk14MLog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */