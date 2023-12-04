/*     */ package com.mchange.v2.c3p0.cfg;
/*     */ 
/*     */ import com.mchange.v1.lang.BooleanUtils;
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0Defaults;
/*     */ import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ public final class C3P0Config
/*     */ {
/*     */   public static final String CFG_FINDER_CLASSNAME_KEY = "com.mchange.v2.c3p0.cfg.finder";
/*     */   public static final String DEFAULT_CONFIG_NAME = "default";
/*     */   public static final C3P0Config MAIN;
/*  47 */   static final MLogger logger = MLog.getLogger(C3P0Config.class);
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
/*     */   static
/*     */   {
/*  69 */     String cname = MultiPropertiesConfig.readVmConfig().getProperty("com.mchange.v2.c3p0.cfg.finder");
/*     */     
/*  71 */     C3P0ConfigFinder cfgFinder = null;
/*     */     try
/*     */     {
/*  74 */       if (cname != null) {
/*  75 */         cfgFinder = (C3P0ConfigFinder)Class.forName(cname).newInstance();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  80 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  81 */         logger.log(MLevel.WARNING, "Could not load specified C3P0ConfigFinder class'" + cname + "'.", e);
/*     */       }
/*     */     }
/*     */     C3P0Config protoMain;
/*     */     try {
/*  86 */       if (cfgFinder == null)
/*     */       {
/*  88 */         Class.forName("org.w3c.dom.Node");
/*  89 */         Class.forName("com.mchange.v2.c3p0.cfg.C3P0ConfigXmlUtils");
/*  90 */         cfgFinder = new DefaultC3P0ConfigFinder();
/*     */       }
/*  92 */       protoMain = cfgFinder.findConfig();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  97 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  98 */         logger.log(MLevel.WARNING, "XML configuration disabled! Verify that standard XML libs are available.", e);
/*     */       }
/* 100 */       HashMap flatDefaults = C3P0ConfigUtils.extractHardcodedC3P0Defaults();
/* 101 */       flatDefaults.putAll(C3P0ConfigUtils.extractC3P0PropertiesResources());
/* 102 */       protoMain = C3P0ConfigUtils.configFromFlatDefaults(flatDefaults);
/*     */     }
/* 104 */     MAIN = protoMain;
/*     */     
/* 106 */     warnOnUnknownProperties(MAIN);
/*     */   }
/*     */   
/*     */   private static void warnOnUnknownProperties(C3P0Config cfg)
/*     */   {
/* 111 */     warnOnUnknownProperties(cfg.defaultConfig);
/* 112 */     for (Iterator ii = cfg.configNamesToNamedScopes.values().iterator(); ii.hasNext();) {
/* 113 */       warnOnUnknownProperties((NamedScope)ii.next());
/*     */     }
/*     */   }
/*     */   
/*     */   private static void warnOnUnknownProperties(NamedScope scope) {
/* 118 */     warnOnUnknownProperties(scope.props);
/* 119 */     for (Iterator ii = scope.userNamesToOverrides.values().iterator(); ii.hasNext();) {
/* 120 */       warnOnUnknownProperties((Map)ii.next());
/*     */     }
/*     */   }
/*     */   
/*     */   private static void warnOnUnknownProperties(Map propMap) {
/* 125 */     for (Iterator ii = propMap.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 127 */       String prop = (String)ii.next();
/* 128 */       if ((!C3P0Defaults.isKnownProperty(prop)) && (logger.isLoggable(MLevel.WARNING))) {
/* 129 */         logger.log(MLevel.WARNING, "Unknown c3p0-config property: " + prop);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getUnspecifiedUserProperty(String propKey, String configName) {
/* 135 */     String out = null;
/*     */     
/* 137 */     if (configName == null) {
/* 138 */       out = (String)MAIN.defaultConfig.props.get(propKey);
/*     */     }
/*     */     else {
/* 141 */       NamedScope named = (NamedScope)MAIN.configNamesToNamedScopes.get(configName);
/* 142 */       if (named != null) {
/* 143 */         out = (String)named.props.get(propKey);
/*     */       } else {
/* 145 */         logger.warning("named-config with name '" + configName + "' does not exist. Using default-config for property '" + propKey + "'.");
/*     */       }
/* 147 */       if (out == null) {
/* 148 */         out = (String)MAIN.defaultConfig.props.get(propKey);
/*     */       }
/*     */     }
/* 151 */     return out;
/*     */   }
/*     */   
/*     */   public static Map getUnspecifiedUserProperties(String configName)
/*     */   {
/* 156 */     Map out = new HashMap();
/*     */     
/* 158 */     out.putAll(MAIN.defaultConfig.props);
/*     */     
/* 160 */     if (configName != null)
/*     */     {
/* 162 */       NamedScope named = (NamedScope)MAIN.configNamesToNamedScopes.get(configName);
/* 163 */       if (named != null) {
/* 164 */         out.putAll(named.props);
/*     */       } else {
/* 166 */         logger.warning("named-config with name '" + configName + "' does not exist. Using default-config.");
/*     */       }
/*     */     }
/* 169 */     return out;
/*     */   }
/*     */   
/*     */   public static Map getUserOverrides(String configName)
/*     */   {
/* 174 */     Map out = new HashMap();
/*     */     
/* 176 */     NamedScope namedConfigScope = null;
/*     */     
/* 178 */     if (configName != null) {
/* 179 */       namedConfigScope = (NamedScope)MAIN.configNamesToNamedScopes.get(configName);
/*     */     }
/* 181 */     out.putAll(MAIN.defaultConfig.userNamesToOverrides);
/*     */     
/* 183 */     if (namedConfigScope != null) {
/* 184 */       out.putAll(namedConfigScope.userNamesToOverrides);
/*     */     }
/* 186 */     return out.isEmpty() ? null : out;
/*     */   }
/*     */   
/*     */   public static String getUserOverridesAsString(String configName) throws IOException
/*     */   {
/* 191 */     Map userOverrides = getUserOverrides(configName);
/* 192 */     if (userOverrides == null) {
/* 193 */       return null;
/*     */     }
/* 195 */     return C3P0ImplUtils.createUserOverridesAsString(userOverrides).intern();
/*     */   }
/*     */   
/* 198 */   static final Class[] SUOAS_ARGS = { String.class };
/*     */   
/* 200 */   static final Collection SKIP_BIND_PROPS = Arrays.asList(new String[] { "loginTimeout", "properties" });
/*     */   NamedScope defaultConfig;
/*     */   HashMap configNamesToNamedScopes;
/*     */   
/* 204 */   public static void bindNamedConfigToBean(Object bean, String configName) throws IntrospectionException { Map defaultUserProps = getUnspecifiedUserProperties(configName);
/* 205 */     BeansUtils.overwriteAccessiblePropertiesFromMap(defaultUserProps, bean, false, SKIP_BIND_PROPS, true, MLevel.FINEST, MLevel.WARNING, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 215 */       Method m = bean.getClass().getMethod("setUserOverridesAsString", SUOAS_ARGS);
/* 216 */       m.invoke(bean, new Object[] { getUserOverridesAsString(configName) });
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 220 */       e.printStackTrace();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 225 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 226 */         logger.log(MLevel.WARNING, "An exception occurred while trying to bind user overrides for named config '" + configName + "'. Only default user configs " + "will be used.", e);
/*     */       }
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
/*     */   public static String initializeUserOverridesAsString()
/*     */   {
/*     */     try
/*     */     {
/* 243 */       return getUserOverridesAsString(null);
/*     */     }
/*     */     catch (Exception e) {
/* 246 */       if (logger.isLoggable(MLevel.WARNING))
/* 247 */         logger.log(MLevel.WARNING, "Error initializing default user overrides. User overrides may be ignored.", e); }
/* 248 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String initializeStringPropertyVar(String propKey, String dflt)
/*     */   {
/* 254 */     String out = getUnspecifiedUserProperty(propKey, null);
/* 255 */     if (out == null) out = dflt;
/* 256 */     return out;
/*     */   }
/*     */   
/*     */   public static int initializeIntPropertyVar(String propKey, int dflt)
/*     */   {
/* 261 */     boolean set = false;
/* 262 */     int out = -1;
/*     */     
/* 264 */     String outStr = getUnspecifiedUserProperty(propKey, null);
/* 265 */     if (outStr != null)
/*     */     {
/*     */       try
/*     */       {
/* 269 */         out = Integer.parseInt(outStr.trim());
/* 270 */         set = true;
/*     */       }
/*     */       catch (NumberFormatException e)
/*     */       {
/* 274 */         logger.info("'" + outStr + "' is not a legal value for property '" + propKey + "'. Using default value: " + dflt);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 279 */     if (!set) {
/* 280 */       out = dflt;
/*     */     }
/*     */     
/* 283 */     return out;
/*     */   }
/*     */   
/*     */   public static boolean initializeBooleanPropertyVar(String propKey, boolean dflt)
/*     */   {
/* 288 */     boolean set = false;
/* 289 */     boolean out = false;
/*     */     
/* 291 */     String outStr = getUnspecifiedUserProperty(propKey, null);
/* 292 */     if (outStr != null)
/*     */     {
/*     */       try
/*     */       {
/* 296 */         out = BooleanUtils.parseBoolean(outStr.trim());
/* 297 */         set = true;
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/* 301 */         logger.info("'" + outStr + "' is not a legal value for property '" + propKey + "'. Using default value: " + dflt);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 306 */     if (!set) {
/* 307 */       out = dflt;
/*     */     }
/* 309 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   C3P0Config(NamedScope defaultConfig, HashMap configNamesToNamedScopes)
/*     */   {
/* 319 */     this.defaultConfig = defaultConfig;
/* 320 */     this.configNamesToNamedScopes = configNamesToNamedScopes;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\cfg\C3P0Config.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */