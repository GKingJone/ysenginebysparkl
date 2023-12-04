/*     */ package com.mchange.v2.log;
/*     */ 
/*     */ import com.mchange.v1.util.StringTokenizerUtils;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public abstract class MLog
/*     */ {
/*     */   static final NameTransformer transformer;
/*     */   static final MLog mlog;
/*     */   static final MultiPropertiesConfig CONFIG;
/*     */   static final MLogger logger;
/*     */   
/*     */   static
/*     */   {
/*  42 */     String[] defaults = { "/com/mchange/v2/log/default-mchange-log.properties", "/mchange-log.properties", "/" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  48 */     CONFIG = MultiPropertiesConfig.readVmConfig(defaults, null);
/*     */     
/*  50 */     String classnamesStr = CONFIG.getProperty("com.mchange.v2.log.MLog");
/*  51 */     String[] classnames = null;
/*  52 */     if (classnamesStr == null)
/*  53 */       classnamesStr = CONFIG.getProperty("com.mchange.v2.log.mlog");
/*  54 */     if (classnamesStr != null) {
/*  55 */       classnames = StringTokenizerUtils.tokenizeToArray(classnamesStr, ", \t\r\n");
/*     */     }
/*  57 */     boolean warn = false;
/*  58 */     MLog tmpml = null;
/*  59 */     if (classnames != null)
/*  60 */       tmpml = findByClassnames(classnames);
/*  61 */     if (tmpml == null)
/*  62 */       tmpml = findByClassnames(MLogClasses.CLASSNAMES);
/*  63 */     if (tmpml == null)
/*     */     {
/*  65 */       warn = true;
/*  66 */       tmpml = new FallbackMLog();
/*     */     }
/*  68 */     mlog = tmpml;
/*  69 */     if (warn) {
/*  70 */       info("Using " + mlog.getClass().getName() + " -- Named logger's not supported, everything goes to System.err.");
/*     */     }
/*  72 */     logger = getLogger(MLog.class);
/*  73 */     String loggerDesc = mlog.getClass().getName();
/*  74 */     if ("com.mchange.v2.log.jdk14logging.Jdk14MLog".equals(loggerDesc)) {
/*  75 */       loggerDesc = "java 1.4+ standard";
/*  76 */     } else if ("com.mchange.v2.log.log4j.Log4jMLog".equals(loggerDesc)) {
/*  77 */       loggerDesc = "log4j";
/*     */     }
/*  79 */     if (logger.isLoggable(MLevel.INFO)) {
/*  80 */       logger.log(MLevel.INFO, "MLog clients using " + loggerDesc + " logging.");
/*     */     }
/*  82 */     NameTransformer tmpt = null;
/*  83 */     String tClassName = CONFIG.getProperty("com.mchange.v2.log.NameTransformer");
/*  84 */     if (tClassName == null) {
/*  85 */       tClassName = CONFIG.getProperty("com.mchange.v2.log.nametransformer");
/*     */     }
/*     */     try {
/*  88 */       if (tClassName != null) {
/*  89 */         tmpt = (NameTransformer)Class.forName(tClassName).newInstance();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  93 */       System.err.println("Failed to instantiate com.mchange.v2.log.NameTransformer '" + tClassName + "'!");
/*  94 */       e.printStackTrace();
/*     */     }
/*  96 */     transformer = tmpt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static MLog findByClassnames(String[] classnames)
/*     */   {
/* 103 */     List attempts = null;
/* 104 */     int i = 0; for (int len = classnames.length; i < len; i++) {
/*     */       try {
/* 106 */         return (MLog)Class.forName(classnames[i]).newInstance();
/*     */       }
/*     */       catch (Exception e) {
/* 109 */         if (attempts == null)
/* 110 */           attempts = new ArrayList();
/* 111 */         attempts.add(classnames[i]);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 116 */     System.err.println("Tried without success to load the following MLog classes:");
/* 117 */     int i = 0; for (int len = attempts.size(); i < len; i++)
/* 118 */       System.err.println("\t" + attempts.get(i));
/* 119 */     return null;
/*     */   }
/*     */   
/*     */ 
/* 123 */   public static MLog instance() { return mlog; }
/*     */   
/*     */   public static MLogger getLogger(String name) {
/*     */     MLogger out;
/*     */     MLogger out;
/* 128 */     if (transformer == null) {
/* 129 */       out = instance().getMLogger(name);
/*     */     }
/*     */     else {
/* 132 */       String xname = transformer.transformName(name);
/* 133 */       MLogger out; if (xname != null) {
/* 134 */         out = instance().getMLogger(xname);
/*     */       } else
/* 136 */         out = instance().getMLogger(name);
/*     */     }
/* 138 */     return out;
/*     */   }
/*     */   
/*     */   public static MLogger getLogger(Class cl) {
/*     */     MLogger out;
/*     */     MLogger out;
/* 144 */     if (transformer == null) {
/* 145 */       out = instance().getMLogger(cl);
/*     */     }
/*     */     else {
/* 148 */       String xname = transformer.transformName(cl);
/* 149 */       MLogger out; if (xname != null) {
/* 150 */         out = instance().getMLogger(xname);
/*     */       } else
/* 152 */         out = instance().getMLogger(cl);
/*     */     }
/* 154 */     return out;
/*     */   }
/*     */   
/*     */   public static MLogger getLogger() {
/*     */     MLogger out;
/*     */     MLogger out;
/* 160 */     if (transformer == null) {
/* 161 */       out = instance().getMLogger();
/*     */     }
/*     */     else {
/* 164 */       String xname = transformer.transformName();
/* 165 */       MLogger out; if (xname != null) {
/* 166 */         out = instance().getMLogger(xname);
/*     */       } else
/* 168 */         out = instance().getMLogger();
/*     */     }
/* 170 */     return out;
/*     */   }
/*     */   
/*     */   public static void log(MLevel l, String msg) {
/* 174 */     instance();getLogger().log(l, msg);
/*     */   }
/*     */   
/* 177 */   public static void log(MLevel l, String msg, Object param) { instance();getLogger().log(l, msg, param);
/*     */   }
/*     */   
/* 180 */   public static void log(MLevel l, String msg, Object[] params) { instance();getLogger().log(l, msg, params);
/*     */   }
/*     */   
/* 183 */   public static void log(MLevel l, String msg, Throwable t) { instance();getLogger().log(l, msg, t);
/*     */   }
/*     */   
/* 186 */   public static void logp(MLevel l, String srcClass, String srcMeth, String msg) { instance();getLogger().logp(l, srcClass, srcMeth, msg);
/*     */   }
/*     */   
/* 189 */   public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Object param) { instance();getLogger().logp(l, srcClass, srcMeth, msg, param);
/*     */   }
/*     */   
/* 192 */   public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Object[] params) { instance();getLogger().logp(l, srcClass, srcMeth, msg, params);
/*     */   }
/*     */   
/* 195 */   public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Throwable t) { instance();getLogger().logp(l, srcClass, srcMeth, msg, t);
/*     */   }
/*     */   
/* 198 */   public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg) { instance();getLogger().logp(l, srcClass, srcMeth, rb, msg);
/*     */   }
/*     */   
/* 201 */   public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object param) { instance();getLogger().logrb(l, srcClass, srcMeth, rb, msg, param);
/*     */   }
/*     */   
/* 204 */   public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object[] params) { instance();getLogger().logrb(l, srcClass, srcMeth, rb, msg, params);
/*     */   }
/*     */   
/* 207 */   public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Throwable t) { instance();getLogger().logrb(l, srcClass, srcMeth, rb, msg, t);
/*     */   }
/*     */   
/* 210 */   public static void entering(String srcClass, String srcMeth) { instance();getLogger().entering(srcClass, srcMeth);
/*     */   }
/*     */   
/* 213 */   public static void entering(String srcClass, String srcMeth, Object param) { instance();getLogger().entering(srcClass, srcMeth, param);
/*     */   }
/*     */   
/* 216 */   public static void entering(String srcClass, String srcMeth, Object[] params) { instance();getLogger().entering(srcClass, srcMeth, params);
/*     */   }
/*     */   
/* 219 */   public static void exiting(String srcClass, String srcMeth) { instance();getLogger().exiting(srcClass, srcMeth);
/*     */   }
/*     */   
/* 222 */   public static void exiting(String srcClass, String srcMeth, Object result) { instance();getLogger().exiting(srcClass, srcMeth, result);
/*     */   }
/*     */   
/* 225 */   public static void throwing(String srcClass, String srcMeth, Throwable t) { instance();getLogger().throwing(srcClass, srcMeth, t);
/*     */   }
/*     */   
/* 228 */   public static void severe(String msg) { instance();getLogger().severe(msg);
/*     */   }
/*     */   
/* 231 */   public static void warning(String msg) { instance();getLogger().warning(msg);
/*     */   }
/*     */   
/* 234 */   public static void info(String msg) { instance();getLogger().info(msg);
/*     */   }
/*     */   
/* 237 */   public static void config(String msg) { instance();getLogger().config(msg);
/*     */   }
/*     */   
/* 240 */   public static void fine(String msg) { instance();getLogger().fine(msg);
/*     */   }
/*     */   
/* 243 */   public static void finer(String msg) { instance();getLogger().finer(msg);
/*     */   }
/*     */   
/* 246 */   public static void finest(String msg) { instance();getLogger().finest(msg);
/*     */   }
/*     */   
/*     */   public abstract MLogger getMLogger(String paramString);
/*     */   
/*     */   public abstract MLogger getMLogger(Class paramClass);
/*     */   
/*     */   public abstract MLogger getMLogger();
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\MLog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */