/*     */ package com.mchange.v2.c3p0.cfg;
/*     */ 
/*     */ import com.mchange.v2.c3p0.impl.C3P0Defaults;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public final class C3P0ConfigUtils
/*     */ {
/*     */   public static final String PROPS_FILE_RSRC_PATH = "/c3p0.properties";
/*     */   public static final String PROPS_FILE_PROP_PFX = "c3p0.";
/*     */   public static final int PROPS_FILE_PROP_PFX_LEN = 5;
/*  39 */   private static final String[] MISSPELL_PFXS = { "/c3pO", "/c3po", "/C3P0", "/C3PO" };
/*     */   
/*  41 */   static final MLogger logger = MLog.getLogger(C3P0ConfigUtils.class);
/*     */   
/*     */   static
/*     */   {
/*  45 */     if ((logger.isLoggable(MLevel.WARNING)) && (C3P0ConfigUtils.class.getResource("/c3p0.properties") == null))
/*     */     {
/*     */ 
/*  48 */       for (int i = 0; i < MISSPELL_PFXS.length; i++)
/*     */       {
/*  50 */         String test = MISSPELL_PFXS[i] + ".properties";
/*  51 */         if (C3P0ConfigUtils.class.getResource(MISSPELL_PFXS[i] + ".properties") != null)
/*     */         {
/*  53 */           logger.warning("POSSIBLY MISSPELLED c3p0.properties CONFIG RESOURCE FOUND. Please ensure the file name is c3p0.properties, all lower case, with the digit 0 (NOT the letter O) in c3p0. It should be placed  in the top level of c3p0's effective classpath.");
/*     */           
/*     */ 
/*     */ 
/*  57 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static HashMap extractHardcodedC3P0Defaults(boolean stringify)
/*     */   {
/*  65 */     HashMap out = new HashMap();
/*     */     
/*     */     try
/*     */     {
/*  69 */       Method[] methods = C3P0Defaults.class.getMethods();
/*  70 */       int i = 0; for (int len = methods.length; i < len; i++)
/*     */       {
/*  72 */         Method m = methods[i];
/*  73 */         int mods = m.getModifiers();
/*  74 */         if (((mods & 0x1) != 0) && ((mods & 0x8) != 0) && (m.getParameterTypes().length == 0))
/*     */         {
/*  76 */           if (stringify)
/*     */           {
/*  78 */             Object val = m.invoke(null, null);
/*  79 */             if (val != null) {
/*  80 */               out.put(m.getName(), String.valueOf(val));
/*     */             }
/*     */           } else {
/*  83 */             out.put(m.getName(), m.invoke(null, null));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  89 */       logger.log(MLevel.WARNING, "Failed to extract hardcoded default config!?", e);
/*     */     }
/*     */     
/*  92 */     return out;
/*     */   }
/*     */   
/*     */   public static HashMap extractHardcodedC3P0Defaults() {
/*  96 */     return extractHardcodedC3P0Defaults(true);
/*     */   }
/*     */   
/*     */   public static HashMap extractC3P0PropertiesResources() {
/* 100 */     HashMap out = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 105 */     Properties props = findAllC3P0Properties();
/* 106 */     for (Iterator ii = props.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 108 */       String key = (String)ii.next();
/* 109 */       String val = (String)props.get(key);
/* 110 */       if (key.startsWith("c3p0.")) {
/* 111 */         out.put(key.substring(5).trim(), val.trim());
/*     */       }
/*     */     }
/* 114 */     return out;
/*     */   }
/*     */   
/*     */   public static C3P0Config configFromFlatDefaults(HashMap flatDefaults)
/*     */   {
/* 119 */     NamedScope defaults = new NamedScope();
/* 120 */     defaults.props.putAll(flatDefaults);
/*     */     
/* 122 */     HashMap configNamesToNamedScopes = new HashMap();
/*     */     
/* 124 */     return new C3P0Config(defaults, configNamesToNamedScopes);
/*     */   }
/*     */   
/*     */   public static String getPropFileConfigProperty(String prop) {
/* 128 */     return MultiPropertiesConfig.readVmConfig().getProperty(prop);
/*     */   }
/*     */   
/* 131 */   private static Properties findResourceProperties() { return MultiPropertiesConfig.readVmConfig().getPropertiesByResourcePath("/c3p0.properties"); }
/*     */   
/*     */   private static Properties findAllC3P0Properties() {
/* 134 */     return MultiPropertiesConfig.readVmConfig().getPropertiesByPrefix("c3p0");
/*     */   }
/*     */   
/*     */   static Properties findAllC3P0SystemProperties() {
/* 138 */     Properties out = new Properties();
/*     */     
/* 140 */     SecurityException sampleExc = null;
/*     */     try
/*     */     {
/* 143 */       for (ii = C3P0Defaults.getKnownProperties().iterator(); ii.hasNext();)
/*     */       {
/* 145 */         String key = (String)ii.next();
/* 146 */         String prefixedKey = "c3p0." + key;
/* 147 */         String value = System.getProperty(prefixedKey);
/* 148 */         if ((value != null) && (value.trim().length() > 0))
/* 149 */           out.put(key, value);
/*     */       }
/*     */     } catch (SecurityException e) {
/*     */       Iterator ii;
/* 153 */       sampleExc = e;
/*     */     }
/* 155 */     return out;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\cfg\C3P0ConfigUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */