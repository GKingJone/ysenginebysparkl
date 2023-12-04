/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Loader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.LogRecord;
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
/*     */ public class JavaUtilLog
/*     */   extends AbstractLogger
/*     */ {
/*  66 */   private static final String THIS_CLASS = JavaUtilLog.class.getName();
/*     */   
/*  68 */   private static final boolean __source = Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.SOURCE", Log.__props
/*  69 */     .getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.javautil.SOURCE", "true")));
/*     */   
/*  71 */   private static boolean _initialized = false;
/*     */   
/*     */   private Level configuredLevel;
/*     */   private java.util.logging.Logger _logger;
/*     */   
/*     */   public JavaUtilLog()
/*     */   {
/*  78 */     this("com.facebook.presto.jdbc.internal.jetty.util.log.javautil");
/*     */   }
/*     */   
/*     */   public JavaUtilLog(String name)
/*     */   {
/*  83 */     synchronized (JavaUtilLog.class)
/*     */     {
/*  85 */       if (!_initialized)
/*     */       {
/*  87 */         _initialized = true;
/*     */         
/*  89 */         final String properties = Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.javautil.PROPERTIES", null);
/*  90 */         if (properties != null)
/*     */         {
/*  92 */           AccessController.doPrivileged(new PrivilegedAction()
/*     */           {
/*     */             public Object run()
/*     */             {
/*     */               try
/*     */               {
/*  98 */                 URL props = Loader.getResource(JavaUtilLog.class, properties);
/*  99 */                 if (props != null) {
/* 100 */                   LogManager.getLogManager().readConfiguration(props.openStream());
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 104 */                 System.err.println("[WARN] Error loading logging config: " + properties);
/* 105 */                 e.printStackTrace(System.err);
/*     */               }
/*     */               
/* 108 */               return null;
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 115 */     this._logger = java.util.logging.Logger.getLogger(name);
/*     */     
/* 117 */     switch (lookupLoggingLevel(Log.__props, name))
/*     */     {
/*     */     case 0: 
/* 120 */       this._logger.setLevel(Level.ALL);
/* 121 */       break;
/*     */     case 1: 
/* 123 */       this._logger.setLevel(Level.FINE);
/* 124 */       break;
/*     */     case 2: 
/* 126 */       this._logger.setLevel(Level.INFO);
/* 127 */       break;
/*     */     case 3: 
/* 129 */       this._logger.setLevel(Level.WARNING);
/* 130 */       break;
/*     */     case 10: 
/* 132 */       this._logger.setLevel(Level.OFF);
/* 133 */       break;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 139 */     this.configuredLevel = this._logger.getLevel();
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 144 */     return this._logger.getName();
/*     */   }
/*     */   
/*     */   protected void log(Level level, String msg, Throwable thrown)
/*     */   {
/* 149 */     LogRecord record = new LogRecord(level, msg);
/* 150 */     if (thrown != null)
/* 151 */       record.setThrown(thrown);
/* 152 */     record.setLoggerName(this._logger.getName());
/* 153 */     if (__source)
/*     */     {
/* 155 */       StackTraceElement[] stack = new Throwable().getStackTrace();
/* 156 */       for (int i = 0; i < stack.length; i++)
/*     */       {
/* 158 */         StackTraceElement e = stack[i];
/* 159 */         if (!e.getClassName().equals(THIS_CLASS))
/*     */         {
/* 161 */           record.setSourceClassName(e.getClassName());
/* 162 */           record.setSourceMethodName(e.getMethodName());
/* 163 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 167 */     this._logger.log(record);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Object... args)
/*     */   {
/* 172 */     if (this._logger.isLoggable(Level.WARNING)) {
/* 173 */       log(Level.WARNING, format(msg, args), null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void warn(Throwable thrown) {
/* 178 */     if (this._logger.isLoggable(Level.WARNING)) {
/* 179 */       log(Level.WARNING, "", thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable thrown) {
/* 184 */     if (this._logger.isLoggable(Level.WARNING)) {
/* 185 */       log(Level.WARNING, msg, thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(String msg, Object... args) {
/* 190 */     if (this._logger.isLoggable(Level.INFO)) {
/* 191 */       log(Level.INFO, format(msg, args), null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(Throwable thrown) {
/* 196 */     if (this._logger.isLoggable(Level.INFO)) {
/* 197 */       log(Level.INFO, "", thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable thrown) {
/* 202 */     if (this._logger.isLoggable(Level.INFO)) {
/* 203 */       log(Level.INFO, msg, thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 208 */     return this._logger.isLoggable(Level.FINE);
/*     */   }
/*     */   
/*     */   public void setDebugEnabled(boolean enabled)
/*     */   {
/* 213 */     if (enabled)
/*     */     {
/* 215 */       this.configuredLevel = this._logger.getLevel();
/* 216 */       this._logger.setLevel(Level.FINE);
/*     */     }
/*     */     else
/*     */     {
/* 220 */       this._logger.setLevel(this.configuredLevel);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(String msg, Object... args)
/*     */   {
/* 226 */     if (this._logger.isLoggable(Level.FINE)) {
/* 227 */       log(Level.FINE, format(msg, args), null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(String msg, long arg) {
/* 232 */     if (this._logger.isLoggable(Level.FINE)) {
/* 233 */       log(Level.FINE, format(msg, new Object[] { Long.valueOf(arg) }), null);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(Throwable thrown) {
/* 238 */     if (this._logger.isLoggable(Level.FINE)) {
/* 239 */       log(Level.FINE, "", thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable thrown) {
/* 244 */     if (this._logger.isLoggable(Level.FINE)) {
/* 245 */       log(Level.FINE, msg, thrown);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Logger newLogger(String fullname)
/*     */   {
/* 253 */     return new JavaUtilLog(fullname);
/*     */   }
/*     */   
/*     */   public void ignore(Throwable ignored)
/*     */   {
/* 258 */     if (this._logger.isLoggable(Level.ALL)) {
/* 259 */       log(Level.WARNING, "IGNORED ", ignored);
/*     */     }
/*     */   }
/*     */   
/*     */   private String format(String msg, Object... args) {
/* 264 */     msg = String.valueOf(msg);
/* 265 */     String braces = "{}";
/* 266 */     StringBuilder builder = new StringBuilder();
/* 267 */     int start = 0;
/* 268 */     for (Object arg : args)
/*     */     {
/* 270 */       int bracesIndex = msg.indexOf(braces, start);
/* 271 */       if (bracesIndex < 0)
/*     */       {
/* 273 */         builder.append(msg.substring(start));
/* 274 */         builder.append(" ");
/* 275 */         builder.append(arg);
/* 276 */         start = msg.length();
/*     */       }
/*     */       else
/*     */       {
/* 280 */         builder.append(msg.substring(start, bracesIndex));
/* 281 */         builder.append(String.valueOf(arg));
/* 282 */         start = bracesIndex + braces.length();
/*     */       }
/*     */     }
/* 285 */     builder.append(msg.substring(start));
/* 286 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\JavaUtilLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */