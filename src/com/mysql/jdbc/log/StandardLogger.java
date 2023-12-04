/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.io.PrintStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardLogger
/*     */   implements Log
/*     */ {
/*     */   private static final int FATAL = 0;
/*     */   private static final int ERROR = 1;
/*     */   private static final int WARN = 2;
/*     */   private static final int INFO = 3;
/*     */   private static final int DEBUG = 4;
/*     */   private static final int TRACE = 5;
/*  47 */   private static StringBuffer bufferedLog = null;
/*     */   
/*  49 */   private boolean logLocationInfo = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StandardLogger(String name)
/*     */   {
/*  58 */     this(name, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StandardLogger(String name, boolean logLocationInfo)
/*     */   {
/*  66 */     this.logLocationInfo = logLocationInfo;
/*     */   }
/*     */   
/*     */   public static void startLoggingToBuffer() {
/*  70 */     bufferedLog = new StringBuffer();
/*     */   }
/*     */   
/*     */   public static void dropBuffer() {
/*  74 */     bufferedLog = null;
/*     */   }
/*     */   
/*     */   public static Appendable getBuffer() {
/*  78 */     return bufferedLog;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  85 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  92 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  99 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 106 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 113 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 120 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 130 */     logInternal(4, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logDebug(Object message, Throwable exception)
/*     */   {
/* 142 */     logInternal(4, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 152 */     logInternal(1, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logError(Object message, Throwable exception)
/*     */   {
/* 164 */     logInternal(1, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 174 */     logInternal(0, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logFatal(Object message, Throwable exception)
/*     */   {
/* 186 */     logInternal(0, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 196 */     logInternal(3, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logInfo(Object message, Throwable exception)
/*     */   {
/* 208 */     logInternal(3, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 218 */     logInternal(5, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logTrace(Object message, Throwable exception)
/*     */   {
/* 230 */     logInternal(5, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 240 */     logInternal(2, message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logWarn(Object message, Throwable exception)
/*     */   {
/* 252 */     logInternal(2, message, exception);
/*     */   }
/*     */   
/*     */   protected void logInternal(int level, Object msg, Throwable exception) {
/* 256 */     StringBuilder msgBuf = new StringBuilder();
/* 257 */     msgBuf.append(new Date().toString());
/* 258 */     msgBuf.append(" ");
/*     */     
/* 260 */     switch (level) {
/*     */     case 0: 
/* 262 */       msgBuf.append("FATAL: ");
/*     */       
/* 264 */       break;
/*     */     
/*     */     case 1: 
/* 267 */       msgBuf.append("ERROR: ");
/*     */       
/* 269 */       break;
/*     */     
/*     */     case 2: 
/* 272 */       msgBuf.append("WARN: ");
/*     */       
/* 274 */       break;
/*     */     
/*     */     case 3: 
/* 277 */       msgBuf.append("INFO: ");
/*     */       
/* 279 */       break;
/*     */     
/*     */     case 4: 
/* 282 */       msgBuf.append("DEBUG: ");
/*     */       
/* 284 */       break;
/*     */     
/*     */     case 5: 
/* 287 */       msgBuf.append("TRACE: ");
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 292 */     if ((msg instanceof ProfilerEvent)) {
/* 293 */       msgBuf.append(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */     }
/*     */     else {
/* 296 */       if ((this.logLocationInfo) && (level != 5)) {
/* 297 */         Throwable locationException = new Throwable();
/* 298 */         msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
/* 299 */         msgBuf.append(" ");
/*     */       }
/*     */       
/* 302 */       if (msg != null) {
/* 303 */         msgBuf.append(String.valueOf(msg));
/*     */       }
/*     */     }
/*     */     
/* 307 */     if (exception != null) {
/* 308 */       msgBuf.append("\n");
/* 309 */       msgBuf.append("\n");
/* 310 */       msgBuf.append("EXCEPTION STACK TRACE:");
/* 311 */       msgBuf.append("\n");
/* 312 */       msgBuf.append("\n");
/* 313 */       msgBuf.append(Util.stackTraceToString(exception));
/*     */     }
/*     */     
/* 316 */     String messageAsString = msgBuf.toString();
/*     */     
/* 318 */     System.err.println(messageAsString);
/*     */     
/* 320 */     if (bufferedLog != null) {
/* 321 */       bufferedLog.append(messageAsString);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\log\StandardLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */