/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ public abstract class AbstractLogger
/*     */   implements Logger
/*     */ {
/*     */   public static final int LEVEL_DEFAULT = -1;
/*     */   public static final int LEVEL_ALL = 0;
/*     */   public static final int LEVEL_DEBUG = 1;
/*     */   public static final int LEVEL_INFO = 2;
/*     */   public static final int LEVEL_WARN = 3;
/*     */   public static final int LEVEL_OFF = 10;
/*     */   
/*     */   public final Logger getLogger(String name)
/*     */   {
/*  39 */     if (isBlank(name)) {
/*  40 */       return this;
/*     */     }
/*  42 */     String basename = getName();
/*  43 */     String fullname = basename + "." + name;
/*     */     
/*  45 */     Logger logger = (Logger)Log.getLoggers().get(fullname);
/*  46 */     if (logger == null)
/*     */     {
/*  48 */       Logger newlog = newLogger(fullname);
/*     */       
/*  50 */       logger = (Logger)Log.getMutableLoggers().putIfAbsent(fullname, newlog);
/*  51 */       if (logger == null) {
/*  52 */         logger = newlog;
/*     */       }
/*     */     }
/*  55 */     return logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Logger newLogger(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean isBlank(String name)
/*     */   {
/*  70 */     if (name == null)
/*     */     {
/*  72 */       return true;
/*     */     }
/*  74 */     int size = name.length();
/*     */     
/*  76 */     for (int i = 0; i < size; i++)
/*     */     {
/*  78 */       char c = name.charAt(i);
/*  79 */       if (!Character.isWhitespace(c))
/*     */       {
/*  81 */         return false;
/*     */       }
/*     */     }
/*  84 */     return true;
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
/*     */   public static int lookupLoggingLevel(Properties props, String name)
/*     */   {
/*  99 */     if ((props == null) || (props.isEmpty()) || (name == null)) {
/* 100 */       return -1;
/*     */     }
/*     */     
/*     */ 
/* 104 */     String nameSegment = name;
/*     */     
/* 106 */     while ((nameSegment != null) && (nameSegment.length() > 0))
/*     */     {
/* 108 */       String levelStr = props.getProperty(nameSegment + ".LEVEL");
/*     */       
/* 110 */       int level = getLevelId(nameSegment + ".LEVEL", levelStr);
/* 111 */       if (level != -1)
/*     */       {
/* 113 */         return level;
/*     */       }
/*     */       
/*     */ 
/* 117 */       int idx = nameSegment.lastIndexOf('.');
/* 118 */       if (idx >= 0)
/*     */       {
/* 120 */         nameSegment = nameSegment.substring(0, idx);
/*     */       }
/*     */       else
/*     */       {
/* 124 */         nameSegment = null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 129 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getLoggingProperty(Properties props, String name, String property)
/*     */   {
/* 137 */     String nameSegment = name;
/*     */     
/* 139 */     while ((nameSegment != null) && (nameSegment.length() > 0))
/*     */     {
/* 141 */       String s = props.getProperty(nameSegment + "." + property);
/* 142 */       if (s != null) {
/* 143 */         return s;
/*     */       }
/*     */       
/* 146 */       int idx = nameSegment.lastIndexOf('.');
/* 147 */       nameSegment = idx >= 0 ? nameSegment.substring(0, idx) : null;
/*     */     }
/*     */     
/* 150 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static int getLevelId(String levelSegment, String levelName)
/*     */   {
/* 156 */     if (levelName == null)
/*     */     {
/* 158 */       return -1;
/*     */     }
/* 160 */     String levelStr = levelName.trim();
/* 161 */     if ("ALL".equalsIgnoreCase(levelStr))
/*     */     {
/* 163 */       return 0;
/*     */     }
/* 165 */     if ("DEBUG".equalsIgnoreCase(levelStr))
/*     */     {
/* 167 */       return 1;
/*     */     }
/* 169 */     if ("INFO".equalsIgnoreCase(levelStr))
/*     */     {
/* 171 */       return 2;
/*     */     }
/* 173 */     if ("WARN".equalsIgnoreCase(levelStr))
/*     */     {
/* 175 */       return 3;
/*     */     }
/* 177 */     if ("OFF".equalsIgnoreCase(levelStr))
/*     */     {
/* 179 */       return 10;
/*     */     }
/*     */     
/* 182 */     System.err.println("Unknown StdErrLog level [" + levelSegment + "]=[" + levelStr + "], expecting only [ALL, DEBUG, INFO, WARN, OFF] as values.");
/* 183 */     return -1;
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
/*     */   protected static String condensePackageString(String classname)
/*     */   {
/* 203 */     String[] parts = classname.split("\\.");
/* 204 */     StringBuilder dense = new StringBuilder();
/* 205 */     for (int i = 0; i < parts.length - 1; i++)
/*     */     {
/* 207 */       dense.append(parts[i].charAt(0));
/*     */     }
/* 209 */     if (dense.length() > 0)
/*     */     {
/* 211 */       dense.append('.');
/*     */     }
/* 213 */     dense.append(parts[(parts.length - 1)]);
/* 214 */     return dense.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public void debug(String msg, long arg)
/*     */   {
/* 220 */     if (isDebugEnabled())
/*     */     {
/* 222 */       debug(msg, new Object[] { new Long(arg) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\AbstractLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */