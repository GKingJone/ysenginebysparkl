/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Loader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Uptime;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ public class Log
/*     */ {
/*     */   public static final String EXCEPTION = "EXCEPTION ";
/*     */   public static final String IGNORED = "IGNORED ";
/*     */   protected static final Properties __props;
/*     */   public static String __logClass;
/*     */   public static boolean __ignored;
/*  76 */   private static final ConcurrentMap<String, Logger> __loggers = new ConcurrentHashMap();
/*     */   
/*     */   private static Logger LOG;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  83 */     __props = new Properties();
/*     */     
/*  85 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public Object run()
/*     */       {
/*     */ 
/*     */ 
/*  94 */         Log.loadProperties("jetty-logging.properties", Log.__props);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */         String osName = System.getProperty("os.name");
/*     */         
/* 102 */         if ((osName != null) && (osName.length() > 0))
/*     */         {
/* 104 */           osName = osName.toLowerCase(Locale.ENGLISH).replace(' ', '-');
/* 105 */           Log.loadProperties("jetty-logging-" + osName + ".properties", Log.__props);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 112 */         Enumeration<String> systemKeyEnum = System.getProperties().propertyNames();
/* 113 */         while (systemKeyEnum.hasMoreElements())
/*     */         {
/* 115 */           String key = (String)systemKeyEnum.nextElement();
/* 116 */           String val = System.getProperty(key);
/*     */           
/* 118 */           if (val != null)
/*     */           {
/* 120 */             Log.__props.setProperty(key, val);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 126 */         Log.__logClass = Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.class", "com.facebook.presto.jdbc.internal.jetty.util.log.Slf4jLog");
/* 127 */         Log.__ignored = Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.IGNORED", "false"));
/* 128 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   static void loadProperties(String resourceName, Properties props)
/*     */   {
/* 135 */     URL testProps = Loader.getResource(Log.class, resourceName);
/* 136 */     if (testProps != null) {
/*     */       try {
/* 138 */         InputStream in = testProps.openStream();Throwable localThrowable3 = null;
/*     */         try {
/* 140 */           p = new Properties();
/* 141 */           p.load(in);
/* 142 */           for (Object key : p.keySet())
/*     */           {
/* 144 */             Object value = p.get(key);
/* 145 */             if (value != null)
/*     */             {
/* 147 */               props.put(key, value);
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable localThrowable1)
/*     */         {
/*     */           Properties p;
/* 138 */           localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */           if (in != null) if (localThrowable3 != null) try { in.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else in.close();
/*     */         }
/*     */       } catch (IOException e) {
/* 153 */         System.err.println("[WARN] Error loading logging config: " + testProps);
/* 154 */         e.printStackTrace(System.err);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 160 */   private static boolean __initialized = false;
/*     */   
/*     */   public static void initialized()
/*     */   {
/* 164 */     synchronized (Log.class)
/*     */     {
/* 166 */       if (__initialized)
/* 167 */         return;
/* 168 */       __initialized = true;
/*     */       
/*     */       try
/*     */       {
/* 172 */         Class<?> log_class = __logClass == null ? null : Loader.loadClass(Log.class, __logClass);
/* 173 */         if ((LOG == null) || ((log_class != null) && (!LOG.getClass().equals(log_class))))
/*     */         {
/* 175 */           LOG = (Logger)log_class.newInstance();
/* 176 */           LOG.debug("Logging to {} via {}", new Object[] { LOG, log_class.getName() });
/*     */         }
/*     */         
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 182 */         initStandardLogging(e);
/*     */       }
/*     */       
/* 185 */       if (LOG != null) {
/* 186 */         LOG.info(String.format("Logging initialized @%dms", new Object[] { Long.valueOf(Uptime.getUptime()) }), new Object[0]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void initStandardLogging(Throwable e)
/*     */   {
/* 193 */     if ((e != null) && (__ignored))
/*     */     {
/* 195 */       e.printStackTrace(System.err);
/*     */     }
/*     */     
/* 198 */     if (LOG == null)
/*     */     {
/* 200 */       Class<?> log_class = StdErrLog.class;
/* 201 */       LOG = new StdErrLog();
/* 202 */       LOG.debug("Logging to {} via {}", new Object[] { LOG, log_class.getName() });
/*     */     }
/*     */   }
/*     */   
/*     */   public static Logger getLog()
/*     */   {
/* 208 */     initialized();
/* 209 */     return LOG;
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
/*     */   public static void setLog(Logger log)
/*     */   {
/* 223 */     LOG = log;
/* 224 */     __logClass = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 232 */     initialized();
/* 233 */     return LOG;
/*     */   }
/*     */   
/*     */   static boolean isIgnored()
/*     */   {
/* 238 */     return __ignored;
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
/*     */ 
/*     */   public static void setLogToParent(String name)
/*     */   {
/* 258 */     ClassLoader loader = Log.class.getClassLoader();
/* 259 */     if ((loader != null) && (loader.getParent() != null))
/*     */     {
/*     */       try
/*     */       {
/* 263 */         Class<?> uberlog = loader.getParent().loadClass("com.facebook.presto.jdbc.internal.jetty.util.log.Log");
/* 264 */         Method getLogger = uberlog.getMethod("getLogger", new Class[] { String.class });
/* 265 */         Object logger = getLogger.invoke(null, new Object[] { name });
/* 266 */         setLog(new LoggerLog(logger));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 270 */         e.printStackTrace();
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/* 275 */       setLog(getLogger(name));
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
/*     */   public static Logger getLogger(Class<?> clazz)
/*     */   {
/* 288 */     return getLogger(clazz.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 300 */     if (name == null) {
/* 301 */       return LOG;
/*     */     }
/* 303 */     Logger logger = (Logger)__loggers.get(name);
/* 304 */     if (logger == null) {
/* 305 */       logger = LOG.getLogger(name);
/*     */     }
/* 307 */     return logger;
/*     */   }
/*     */   
/*     */   static ConcurrentMap<String, Logger> getMutableLoggers()
/*     */   {
/* 312 */     return __loggers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("list of all instantiated loggers")
/*     */   public static Map<String, Logger> getLoggers()
/*     */   {
/* 323 */     return Collections.unmodifiableMap(__loggers);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */