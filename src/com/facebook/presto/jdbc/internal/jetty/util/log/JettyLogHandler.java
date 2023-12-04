/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class JettyLogHandler
/*     */   extends Handler
/*     */ {
/*     */   public static void config()
/*     */   {
/*  38 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*  39 */     URL url = cl.getResource("logging.properties");
/*  40 */     if (url != null)
/*     */     {
/*  42 */       System.err.printf("Initializing java.util.logging from %s%n", new Object[] { url });
/*  43 */       try { InputStream in = url.openStream();Throwable localThrowable3 = null;
/*     */         try {
/*  45 */           LogManager.getLogManager().readConfiguration(in);
/*     */         }
/*     */         catch (Throwable localThrowable1)
/*     */         {
/*  43 */           localThrowable3 = localThrowable1;throw localThrowable1;
/*     */         }
/*     */         finally {
/*  46 */           if (in != null) if (localThrowable3 != null) try { in.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else in.close();
/*     */         }
/*     */       } catch (IOException e) {
/*  49 */         e.printStackTrace(System.err);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  54 */       System.err.printf("WARNING: java.util.logging failed to initialize: logging.properties not found%n", new Object[0]);
/*     */     }
/*     */     
/*  57 */     System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
/*     */   }
/*     */   
/*     */   public JettyLogHandler()
/*     */   {
/*  62 */     if (Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.DEBUG", "false")))
/*     */     {
/*  64 */       setLevel(Level.FINEST);
/*     */     }
/*     */     
/*  67 */     if (Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.IGNORED", "false")))
/*     */     {
/*  69 */       setLevel(Level.ALL);
/*     */     }
/*     */     
/*  72 */     System.err.printf("%s Initialized at level [%s]%n", new Object[] { getClass().getName(), getLevel().getName() });
/*     */   }
/*     */   
/*     */   private synchronized String formatMessage(LogRecord record)
/*     */   {
/*  77 */     String msg = getMessage(record);
/*     */     
/*     */     try
/*     */     {
/*  81 */       Object[] params = record.getParameters();
/*  82 */       if ((params == null) || (params.length == 0))
/*     */       {
/*  84 */         return msg;
/*     */       }
/*     */       
/*  87 */       if (Pattern.compile("\\{\\d+\\}").matcher(msg).find())
/*     */       {
/*  89 */         return MessageFormat.format(msg, params);
/*     */       }
/*     */       
/*  92 */       return msg;
/*     */     }
/*     */     catch (Exception ex) {}
/*     */     
/*  96 */     return msg;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getMessage(LogRecord record)
/*     */   {
/* 102 */     ResourceBundle bundle = record.getResourceBundle();
/* 103 */     if (bundle != null)
/*     */     {
/*     */       try
/*     */       {
/* 107 */         return bundle.getString(record.getMessage());
/*     */       }
/*     */       catch (MissingResourceException localMissingResourceException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 114 */     return record.getMessage();
/*     */   }
/*     */   
/*     */ 
/*     */   public void publish(LogRecord record)
/*     */   {
/* 120 */     Logger JLOG = getJettyLogger(record.getLoggerName());
/*     */     
/* 122 */     int level = record.getLevel().intValue();
/* 123 */     if (level >= Level.OFF.intValue())
/*     */     {
/*     */ 
/* 126 */       return;
/*     */     }
/*     */     
/* 129 */     Throwable cause = record.getThrown();
/* 130 */     String msg = formatMessage(record);
/*     */     
/* 132 */     if (level >= Level.WARNING.intValue())
/*     */     {
/*     */ 
/* 135 */       if (cause != null)
/*     */       {
/* 137 */         JLOG.warn(msg, cause);
/*     */       }
/*     */       else
/*     */       {
/* 141 */         JLOG.warn(msg, new Object[0]);
/*     */       }
/* 143 */       return;
/*     */     }
/*     */     
/* 146 */     if (level >= Level.INFO.intValue())
/*     */     {
/*     */ 
/* 149 */       if (cause != null)
/*     */       {
/* 151 */         JLOG.info(msg, cause);
/*     */       }
/*     */       else
/*     */       {
/* 155 */         JLOG.info(msg, new Object[0]);
/*     */       }
/* 157 */       return;
/*     */     }
/*     */     
/* 160 */     if (level >= Level.FINEST.intValue())
/*     */     {
/*     */ 
/* 163 */       if (cause != null)
/*     */       {
/* 165 */         JLOG.debug(msg, cause);
/*     */       }
/*     */       else
/*     */       {
/* 169 */         JLOG.debug(msg, new Object[0]);
/*     */       }
/* 171 */       return;
/*     */     }
/*     */     
/* 174 */     if (level >= Level.ALL.intValue())
/*     */     {
/*     */ 
/* 177 */       JLOG.ignore(cause);
/* 178 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   private Logger getJettyLogger(String loggerName)
/*     */   {
/* 184 */     return Log.getLogger(loggerName);
/*     */   }
/*     */   
/*     */   public void flush() {}
/*     */   
/*     */   public void close()
/*     */     throws SecurityException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\JettyLogHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */