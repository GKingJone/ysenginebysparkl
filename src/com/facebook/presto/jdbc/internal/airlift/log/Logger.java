/*     */ package com.facebook.presto.jdbc.internal.airlift.log;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.inject.Inject;
/*     */ import java.util.Arrays;
/*     */ import java.util.IllegalFormatException;
/*     */ import java.util.logging.Level;
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
/*     */ public class Logger
/*     */ {
/*     */   private final java.util.logging.Logger logger;
/*     */   
/*     */   @Inject
/*     */   Logger(java.util.logging.Logger logger)
/*     */   {
/*  36 */     this.logger = logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Logger get(Class<?> clazz)
/*     */   {
/*  47 */     return get(clazz.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Logger get(String name)
/*     */   {
/*  58 */     java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
/*  59 */     return new Logger(logger);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(Throwable exception, String message)
/*     */   {
/*  70 */     this.logger.log(Level.FINE, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void debug(String message)
/*     */   {
/*  80 */     this.logger.fine(message);
/*     */   }
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
/*     */   public void debug(String format, Object... args)
/*     */   {
/*  98 */     if (this.logger.isLoggable(Level.FINE)) {
/*     */       String message;
/*     */       try {
/* 101 */         message = String.format(format, args);
/*     */       } catch (IllegalFormatException e) {
/*     */         String message;
/* 104 */         this.logger.log(Level.SEVERE, illegalFormatMessageFor("DEBUG", format, args), e);
/* 105 */         message = rawMessageFor(format, args);
/*     */       }
/* 107 */       this.logger.fine(message);
/*     */     }
/*     */   }
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
/*     */   public void debug(Throwable exception, String format, Object... args)
/*     */   {
/* 127 */     if (this.logger.isLoggable(Level.FINE)) {
/*     */       String message;
/*     */       try {
/* 130 */         message = String.format(format, args);
/*     */       } catch (IllegalFormatException e) {
/*     */         String message;
/* 133 */         this.logger.log(Level.SEVERE, illegalFormatMessageFor("DEBUG", format, args), e);
/* 134 */         message = rawMessageFor(format, args);
/*     */       }
/* 136 */       this.logger.log(Level.FINE, message, exception);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void info(String message)
/*     */   {
/* 147 */     this.logger.info(message);
/*     */   }
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
/*     */   public void info(String format, Object... args)
/*     */   {
/* 165 */     if (this.logger.isLoggable(Level.INFO)) {
/*     */       String message;
/*     */       try {
/* 168 */         message = String.format(format, args);
/*     */       } catch (IllegalFormatException e) {
/*     */         String message;
/* 171 */         this.logger.log(Level.SEVERE, illegalFormatMessageFor("INFO", format, args), e);
/* 172 */         message = rawMessageFor(format, args);
/*     */       }
/* 174 */       this.logger.info(message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(Throwable exception, String message)
/*     */   {
/* 186 */     this.logger.log(Level.WARNING, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void warn(String message)
/*     */   {
/* 196 */     this.logger.warning(message);
/*     */   }
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
/*     */   public void warn(Throwable exception, String format, Object... args)
/*     */   {
/* 215 */     if (this.logger.isLoggable(Level.WARNING)) {
/*     */       String message;
/*     */       try {
/* 218 */         message = String.format(format, args);
/*     */       } catch (IllegalFormatException e) {
/*     */         String message;
/* 221 */         this.logger.log(Level.SEVERE, illegalFormatMessageFor("WARN", format, args), e);
/* 222 */         message = rawMessageFor(format, args);
/*     */       }
/* 224 */       this.logger.log(Level.WARNING, message, exception);
/*     */     }
/*     */   }
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
/*     */   public void warn(String format, Object... args)
/*     */   {
/* 243 */     warn(null, format, args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(Throwable exception, String message)
/*     */   {
/* 254 */     this.logger.log(Level.SEVERE, message, exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String message)
/*     */   {
/* 264 */     this.logger.severe(message);
/*     */   }
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
/*     */   public void error(Throwable exception, String format, Object... args)
/*     */   {
/* 283 */     if (this.logger.isLoggable(Level.SEVERE)) {
/*     */       String message;
/*     */       try {
/* 286 */         message = String.format(format, args);
/*     */       } catch (IllegalFormatException e) {
/*     */         String message;
/* 289 */         this.logger.log(Level.SEVERE, illegalFormatMessageFor("ERROR", format, args), e);
/* 290 */         message = rawMessageFor(format, args);
/*     */       }
/* 292 */       this.logger.log(Level.SEVERE, message, exception);
/*     */     }
/*     */   }
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
/*     */   public void error(Throwable exception)
/*     */   {
/* 308 */     if (this.logger.isLoggable(Level.SEVERE)) {
/* 309 */       this.logger.log(Level.SEVERE, exception.getMessage(), exception);
/*     */     }
/*     */   }
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
/*     */   public void error(String format, Object... args)
/*     */   {
/* 328 */     error(null, format, args);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 333 */     return this.logger.isLoggable(Level.FINE);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 338 */     return this.logger.isLoggable(Level.INFO);
/*     */   }
/*     */   
/*     */   private String illegalFormatMessageFor(String level, String message, Object... args)
/*     */   {
/* 343 */     return String.format("Invalid format string while trying to log: %s '%s' %s", new Object[] { level, message, Arrays.asList(args) });
/*     */   }
/*     */   
/*     */   private String rawMessageFor(String format, Object... args)
/*     */   {
/* 348 */     return String.format("'%s' %s", new Object[] { format, Arrays.asList(args) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\log\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */