/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class Slf4JLogger
/*     */   implements Log
/*     */ {
/*     */   private Logger log;
/*     */   
/*     */   public Slf4JLogger(String name)
/*     */   {
/*  33 */     this.log = LoggerFactory.getLogger(name);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  37 */     return this.log.isDebugEnabled();
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/*  41 */     return this.log.isErrorEnabled();
/*     */   }
/*     */   
/*     */   public boolean isFatalEnabled() {
/*  45 */     return this.log.isErrorEnabled();
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/*  49 */     return this.log.isInfoEnabled();
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  53 */     return this.log.isTraceEnabled();
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled() {
/*  57 */     return this.log.isWarnEnabled();
/*     */   }
/*     */   
/*     */   public void logDebug(Object msg) {
/*  61 */     this.log.debug(msg.toString());
/*     */   }
/*     */   
/*     */   public void logDebug(Object msg, Throwable thrown) {
/*  65 */     this.log.debug(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logError(Object msg) {
/*  69 */     this.log.error(msg.toString());
/*     */   }
/*     */   
/*     */   public void logError(Object msg, Throwable thrown) {
/*  73 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logFatal(Object msg) {
/*  77 */     this.log.error(msg.toString());
/*     */   }
/*     */   
/*     */   public void logFatal(Object msg, Throwable thrown) {
/*  81 */     this.log.error(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logInfo(Object msg) {
/*  85 */     this.log.info(msg.toString());
/*     */   }
/*     */   
/*     */   public void logInfo(Object msg, Throwable thrown) {
/*  89 */     this.log.info(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logTrace(Object msg) {
/*  93 */     this.log.trace(msg.toString());
/*     */   }
/*     */   
/*     */   public void logTrace(Object msg, Throwable thrown) {
/*  97 */     this.log.trace(msg.toString(), thrown);
/*     */   }
/*     */   
/*     */   public void logWarn(Object msg) {
/* 101 */     this.log.warn(msg.toString());
/*     */   }
/*     */   
/*     */   public void logWarn(Object msg, Throwable thrown) {
/* 105 */     this.log.warn(msg.toString(), thrown);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\log\Slf4JLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */