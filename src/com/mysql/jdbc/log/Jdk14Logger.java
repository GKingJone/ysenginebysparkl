/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.util.logging.Level;
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
/*     */ public class Jdk14Logger
/*     */   implements Log
/*     */ {
/*  35 */   private static final Level DEBUG = Level.FINE;
/*     */   
/*  37 */   private static final Level ERROR = Level.SEVERE;
/*     */   
/*  39 */   private static final Level FATAL = Level.SEVERE;
/*     */   
/*  41 */   private static final Level INFO = Level.INFO;
/*     */   
/*  43 */   private static final Level TRACE = Level.FINEST;
/*     */   
/*  45 */   private static final Level WARN = Level.WARNING;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  50 */   protected Logger jdkLogger = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Jdk14Logger(String name)
/*     */   {
/*  58 */     this.jdkLogger = Logger.getLogger(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  65 */     return this.jdkLogger.isLoggable(Level.FINE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  72 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  79 */     return this.jdkLogger.isLoggable(Level.SEVERE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/*  86 */     return this.jdkLogger.isLoggable(Level.INFO);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/*  93 */     return this.jdkLogger.isLoggable(Level.FINEST);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 100 */     return this.jdkLogger.isLoggable(Level.WARNING);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 110 */     logInternal(DEBUG, message, null);
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
/* 122 */     logInternal(DEBUG, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 132 */     logInternal(ERROR, message, null);
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
/* 144 */     logInternal(ERROR, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 154 */     logInternal(FATAL, message, null);
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
/* 166 */     logInternal(FATAL, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 176 */     logInternal(INFO, message, null);
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
/* 188 */     logInternal(INFO, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 198 */     logInternal(TRACE, message, null);
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
/* 210 */     logInternal(TRACE, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 220 */     logInternal(WARN, message, null);
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
/* 232 */     logInternal(WARN, message, exception);
/*     */   }
/*     */   
/*     */   private static final int findCallerStackDepth(StackTraceElement[] stackTrace) {
/* 236 */     int numFrames = stackTrace.length;
/*     */     
/* 238 */     for (int i = 0; i < numFrames; i++) {
/* 239 */       String callerClassName = stackTrace[i].getClassName();
/*     */       
/* 241 */       if ((!callerClassName.startsWith("com.mysql.jdbc")) || (callerClassName.startsWith("com.mysql.jdbc.compliance"))) {
/* 242 */         return i;
/*     */       }
/*     */     }
/*     */     
/* 246 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void logInternal(Level level, Object msg, Throwable exception)
/*     */   {
/* 254 */     if (this.jdkLogger.isLoggable(level)) {
/* 255 */       String messageAsString = null;
/* 256 */       String callerMethodName = "N/A";
/* 257 */       String callerClassName = "N/A";
/*     */       
/*     */ 
/*     */ 
/* 261 */       if ((msg instanceof ProfilerEvent)) {
/* 262 */         messageAsString = LogUtils.expandProfilerEventIfNecessary(msg).toString();
/*     */       } else {
/* 264 */         Throwable locationException = new Throwable();
/* 265 */         StackTraceElement[] locations = locationException.getStackTrace();
/*     */         
/* 267 */         int frameIdx = findCallerStackDepth(locations);
/*     */         
/* 269 */         if (frameIdx != 0) {
/* 270 */           callerClassName = locations[frameIdx].getClassName();
/* 271 */           callerMethodName = locations[frameIdx].getMethodName();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 276 */         messageAsString = String.valueOf(msg);
/*     */       }
/*     */       
/* 279 */       if (exception == null) {
/* 280 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString);
/*     */       } else {
/* 282 */         this.jdkLogger.logp(level, callerClassName, callerMethodName, messageAsString, exception);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\log\Jdk14Logger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */