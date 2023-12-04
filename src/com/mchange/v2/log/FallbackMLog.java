/*     */ package com.mchange.v2.log;
/*     */ 
/*     */ import com.mchange.lang.ThrowableUtils;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import java.io.PrintStream;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ResourceBundle;
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
/*     */ public final class FallbackMLog
/*     */   extends MLog
/*     */ {
/*     */   static final MLevel DEFAULT_CUTOFF_LEVEL;
/*     */   MLogger logger;
/*     */   
/*     */   static
/*     */   {
/*  37 */     MLevel dflt = null;
/*  38 */     String dfltName = MLog.CONFIG.getProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL");
/*  39 */     if (dfltName != null)
/*  40 */       dflt = MLevel.fromSeverity(dfltName);
/*  41 */     if (dflt == null)
/*  42 */       dflt = MLevel.INFO;
/*  43 */     DEFAULT_CUTOFF_LEVEL = dflt;
/*     */   }
/*     */   
/*  46 */   public FallbackMLog() { this.logger = new FallbackMLogger(null); }
/*     */   
/*     */   public synchronized MLogger getMLogger(String name) {
/*  49 */     return this.logger;
/*     */   }
/*     */   
/*  52 */   public MLogger getMLogger(Class cl) { return getLogger(cl.getName()); }
/*     */   
/*     */ 
/*     */ 
/*  56 */   public MLogger getMLogger() { return this.logger; }
/*     */   
/*  58 */   private static final class FallbackMLogger implements MLogger { FallbackMLogger(FallbackMLog.1 x0) { this(); }
/*     */     
/*  60 */     MLevel cutoffLevel = FallbackMLog.DEFAULT_CUTOFF_LEVEL;
/*     */     
/*     */     private void formatrb(MLevel l, String srcClass, String srcMeth, String rbname, String msg, Object[] params, Throwable t)
/*     */     {
/*  64 */       ResourceBundle rb = ResourceBundle.getBundle(rbname);
/*  65 */       if ((msg != null) && (rb != null))
/*     */       {
/*  67 */         String check = rb.getString(msg);
/*  68 */         if (check != null)
/*  69 */           msg = check;
/*     */       }
/*  71 */       format(l, srcClass, srcMeth, msg, params, t);
/*     */     }
/*     */     
/*     */     private void format(MLevel l, String srcClass, String srcMeth, String msg, Object[] params, Throwable t) {
/*  75 */       System.err.println(formatString(l, srcClass, srcMeth, msg, params, t));
/*     */     }
/*     */     
/*     */     private String formatString(MLevel l, String srcClass, String srcMeth, String msg, Object[] params, Throwable t) {
/*  79 */       boolean add_parens = (srcMeth != null) && (!srcMeth.endsWith(")"));
/*     */       
/*  81 */       StringBuffer sb = new StringBuffer(256);
/*  82 */       sb.append(l.getLineHeader());
/*  83 */       sb.append(' ');
/*  84 */       if ((srcClass != null) && (srcMeth != null))
/*     */       {
/*  86 */         sb.append('[');
/*  87 */         sb.append(srcClass);
/*  88 */         sb.append('.');
/*  89 */         sb.append(srcMeth);
/*  90 */         if (add_parens)
/*  91 */           sb.append("()");
/*  92 */         sb.append(']');
/*     */       }
/*  94 */       else if (srcClass != null)
/*     */       {
/*  96 */         sb.append('[');
/*  97 */         sb.append(srcClass);
/*  98 */         sb.append(']');
/*     */       }
/* 100 */       else if (srcMeth != null)
/*     */       {
/* 102 */         sb.append('[');
/* 103 */         sb.append(srcMeth);
/* 104 */         if (add_parens)
/* 105 */           sb.append("()");
/* 106 */         sb.append(']');
/*     */       }
/* 108 */       if (msg == null)
/*     */       {
/* 110 */         if (params != null)
/*     */         {
/* 112 */           sb.append("params: ");
/* 113 */           int i = 0; for (int len = params.length; i < len; i++)
/*     */           {
/* 115 */             if (i != 0) sb.append(", ");
/* 116 */             sb.append(params[i]);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 122 */       else if (params == null) {
/* 123 */         sb.append(msg);
/*     */       }
/*     */       else {
/* 126 */         MessageFormat mfmt = new MessageFormat(msg);
/* 127 */         sb.append(mfmt.format(params));
/*     */       }
/*     */       
/*     */ 
/* 131 */       if (t != null) {
/* 132 */         sb.append(ThrowableUtils.extractStackTrace(t));
/*     */       }
/* 134 */       return sb.toString();
/*     */     }
/*     */     
/*     */ 
/*     */     public ResourceBundle getResourceBundle()
/*     */     {
/* 140 */       return null;
/*     */     }
/*     */     
/*     */     public String getResourceBundleName() {
/* 144 */       return null;
/*     */     }
/*     */     
/*     */     public void setFilter(Object java14Filter) throws SecurityException {
/* 148 */       warning("Using FallbackMLog -- Filters not supported!");
/*     */     }
/*     */     
/*     */     public Object getFilter()
/*     */     {
/* 153 */       return null;
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg)
/*     */     {
/* 158 */       if (isLoggable(l)) {
/* 159 */         format(l, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Object param) {
/* 164 */       if (isLoggable(l)) {
/* 165 */         format(l, null, null, msg, new Object[] { param }, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Object[] params) {
/* 170 */       if (isLoggable(l)) {
/* 171 */         format(l, null, null, msg, params, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void log(MLevel l, String msg, Throwable t) {
/* 176 */       if (isLoggable(l)) {
/* 177 */         format(l, null, null, msg, null, t);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg) {
/* 182 */       if (isLoggable(l)) {
/* 183 */         format(l, srcClass, srcMeth, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object param) {
/* 188 */       if (isLoggable(l)) {
/* 189 */         format(l, srcClass, srcMeth, msg, new Object[] { param }, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Object[] params) {
/* 194 */       if (isLoggable(l)) {
/* 195 */         format(l, srcClass, srcMeth, msg, params, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logp(MLevel l, String srcClass, String srcMeth, String msg, Throwable t) {
/* 200 */       if (isLoggable(l)) {
/* 201 */         format(l, srcClass, srcMeth, msg, null, t);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg) {
/* 206 */       if (isLoggable(l)) {
/* 207 */         formatrb(l, srcClass, srcMeth, rb, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object param) {
/* 212 */       if (isLoggable(l)) {
/* 213 */         formatrb(l, srcClass, srcMeth, rb, msg, new Object[] { param }, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object[] params) {
/* 218 */       if (isLoggable(l)) {
/* 219 */         formatrb(l, srcClass, srcMeth, rb, msg, params, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Throwable t) {
/* 224 */       if (isLoggable(l)) {
/* 225 */         formatrb(l, srcClass, srcMeth, rb, msg, null, t);
/*     */       }
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth) {
/* 230 */       if (isLoggable(MLevel.FINER)) {
/* 231 */         format(MLevel.FINER, srcClass, srcMeth, "Entering method.", null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth, Object param) {
/* 236 */       if (isLoggable(MLevel.FINER)) {
/* 237 */         format(MLevel.FINER, srcClass, srcMeth, "Entering method with argument " + param, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void entering(String srcClass, String srcMeth, Object[] params) {
/* 242 */       if (isLoggable(MLevel.FINER))
/*     */       {
/* 244 */         if (params == null) {
/* 245 */           entering(srcClass, srcMeth);
/*     */         }
/*     */         else {
/* 248 */           StringBuffer sb = new StringBuffer(128);
/* 249 */           sb.append("( ");
/* 250 */           int i = 0; for (int len = params.length; i < len; i++)
/*     */           {
/* 252 */             if (i != 0) sb.append(", ");
/* 253 */             sb.append(params[i]);
/*     */           }
/* 255 */           sb.append(" )");
/* 256 */           format(MLevel.FINER, srcClass, srcMeth, "Entering method with arguments " + sb.toString(), null, null);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void exiting(String srcClass, String srcMeth)
/*     */     {
/* 263 */       if (isLoggable(MLevel.FINER)) {
/* 264 */         format(MLevel.FINER, srcClass, srcMeth, "Exiting method.", null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void exiting(String srcClass, String srcMeth, Object result) {
/* 269 */       if (isLoggable(MLevel.FINER)) {
/* 270 */         format(MLevel.FINER, srcClass, srcMeth, "Exiting method with result " + result, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void throwing(String srcClass, String srcMeth, Throwable t) {
/* 275 */       if (isLoggable(MLevel.FINE)) {
/* 276 */         format(MLevel.FINE, srcClass, srcMeth, "Throwing exception.", null, t);
/*     */       }
/*     */     }
/*     */     
/*     */     public void severe(String msg) {
/* 281 */       if (isLoggable(MLevel.SEVERE)) {
/* 282 */         format(MLevel.SEVERE, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void warning(String msg) {
/* 287 */       if (isLoggable(MLevel.WARNING)) {
/* 288 */         format(MLevel.WARNING, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void info(String msg) {
/* 293 */       if (isLoggable(MLevel.INFO)) {
/* 294 */         format(MLevel.INFO, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void config(String msg) {
/* 299 */       if (isLoggable(MLevel.CONFIG)) {
/* 300 */         format(MLevel.CONFIG, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void fine(String msg) {
/* 305 */       if (isLoggable(MLevel.FINE)) {
/* 306 */         format(MLevel.FINE, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void finer(String msg) {
/* 311 */       if (isLoggable(MLevel.FINER)) {
/* 312 */         format(MLevel.FINER, null, null, msg, null, null);
/*     */       }
/*     */     }
/*     */     
/*     */     public void finest(String msg) {
/* 317 */       if (isLoggable(MLevel.FINEST))
/* 318 */         format(MLevel.FINEST, null, null, msg, null, null);
/*     */     }
/*     */     
/*     */     public void setLevel(MLevel l) throws SecurityException {
/* 322 */       this.cutoffLevel = l;
/*     */     }
/*     */     
/* 325 */     public synchronized MLevel getLevel() { return this.cutoffLevel; }
/*     */     
/*     */     public synchronized boolean isLoggable(MLevel l) {
/* 328 */       return l.intValue() >= this.cutoffLevel.intValue();
/*     */     }
/*     */     
/* 331 */     public String getName() { return "global"; }
/*     */     
/*     */     public void addHandler(Object h) throws SecurityException
/*     */     {
/* 335 */       warning("Using FallbackMLog -- Handlers not supported.");
/*     */     }
/*     */     
/*     */     public void removeHandler(Object h) throws SecurityException
/*     */     {
/* 340 */       warning("Using FallbackMLog -- Handlers not supported.");
/*     */     }
/*     */     
/*     */     public Object[] getHandlers()
/*     */     {
/* 345 */       warning("Using FallbackMLog -- Handlers not supported.");
/* 346 */       return new Object[0];
/*     */     }
/*     */     
/*     */     public void setUseParentHandlers(boolean uph)
/*     */     {
/* 351 */       warning("Using FallbackMLog -- Handlers not supported.");
/*     */     }
/*     */     
/*     */     public boolean getUseParentHandlers() {
/* 355 */       return false;
/*     */     }
/*     */     
/*     */     private FallbackMLogger() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\FallbackMLog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */