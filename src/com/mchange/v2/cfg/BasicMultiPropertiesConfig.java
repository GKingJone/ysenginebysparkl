/*     */ package com.mchange.v2.cfg;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class BasicMultiPropertiesConfig
/*     */   extends MultiPropertiesConfig
/*     */ {
/*     */   String[] rps;
/*  33 */   Map propsByResourcePaths = new HashMap();
/*     */   Map propsByPrefixes;
/*     */   Properties propsByKey;
/*     */   
/*     */   public BasicMultiPropertiesConfig(String[] resourcePaths)
/*     */   {
/*  39 */     this(resourcePaths, null);
/*     */   }
/*     */   
/*     */   public BasicMultiPropertiesConfig(String[] resourcePaths, MLogger logger) {
/*  43 */     List goodPaths = new ArrayList();
/*  44 */     int i = 0; for (int len = resourcePaths.length; i < len; i++)
/*     */     {
/*  46 */       String rp = resourcePaths[i];
/*  47 */       if ("/".equals(rp))
/*     */       {
/*     */         try
/*     */         {
/*  51 */           this.propsByResourcePaths.put(rp, System.getProperties());
/*  52 */           goodPaths.add(rp);
/*     */         }
/*     */         catch (SecurityException e)
/*     */         {
/*  56 */           if (logger != null)
/*     */           {
/*  58 */             if (logger.isLoggable(MLevel.WARNING)) {
/*  59 */               logger.log(MLevel.WARNING, "Read of system Properties blocked -- ignoring any configuration via System properties, and using Empty Properties! (But any configuration via a resource properties files is still okay!)", e);
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*  67 */             System.err.println("Read of system Properties blocked -- ignoring any configuration via System properties, and using Empty Properties! (But any configuration via a resource properties files is still okay!)");
/*     */             
/*     */ 
/*  70 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  76 */         Properties p = new Properties();
/*  77 */         InputStream pis = MultiPropertiesConfig.class.getResourceAsStream(rp);
/*  78 */         if (pis != null)
/*     */         {
/*     */           try
/*     */           {
/*  82 */             p.load(pis);
/*  83 */             this.propsByResourcePaths.put(rp, p);
/*  84 */             goodPaths.add(rp);
/*     */           }
/*     */           catch (IOException e)
/*     */           {
/*  88 */             if (logger != null)
/*     */             {
/*  90 */               if (logger.isLoggable(MLevel.WARNING)) {
/*  91 */                 logger.log(MLevel.WARNING, "An IOException occurred while loading configuration properties from resource path '" + rp + "'.", e);
/*     */               }
/*     */               
/*     */             }
/*     */             else {
/*  96 */               e.printStackTrace();
/*     */             }
/*     */           } finally {
/*     */             try {
/* 100 */               if (pis != null) pis.close();
/*     */             }
/*     */             catch (IOException e) {
/* 103 */               if (logger != null)
/*     */               {
/* 105 */                 if (logger.isLoggable(MLevel.WARNING)) {
/* 106 */                   logger.log(MLevel.WARNING, "An IOException occurred while closing InputStream from resource path '" + rp + "'.", e);
/*     */                 }
/*     */                 
/*     */               }
/*     */               else {
/* 111 */                 e.printStackTrace();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 117 */         if (logger != null)
/*     */         {
/* 119 */           if (logger.isLoggable(MLevel.FINE)) {
/* 120 */             logger.fine("Configuration properties not found at ResourcePath '" + rp + "'. [logger name: " + logger.getName() + ']');
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 128 */     this.rps = ((String[])goodPaths.toArray(new String[goodPaths.size()]));
/* 129 */     this.propsByPrefixes = Collections.unmodifiableMap(extractPrefixMapFromRsrcPathMap(this.rps, this.propsByResourcePaths));
/* 130 */     this.propsByResourcePaths = Collections.unmodifiableMap(this.propsByResourcePaths);
/* 131 */     this.propsByKey = extractPropsByKey(this.rps, this.propsByResourcePaths);
/*     */   }
/*     */   
/*     */ 
/*     */   private static String extractPrefix(String s)
/*     */   {
/* 137 */     int lastdot = s.lastIndexOf('.');
/* 138 */     if (lastdot < 0) {
/* 139 */       return null;
/*     */     }
/* 141 */     return s.substring(0, lastdot);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Properties findProps(String rp, Map pbrp)
/*     */   {
/* 165 */     Properties p = (Properties)pbrp.get(rp);
/*     */     
/*     */ 
/*     */ 
/* 169 */     return p;
/*     */   }
/*     */   
/*     */   private static Properties extractPropsByKey(String[] resourcePaths, Map pbrp)
/*     */   {
/* 174 */     Properties out = new Properties();
/* 175 */     int i = 0; String rp; Properties p; Iterator ii; for (int len = resourcePaths.length; i < len; i++)
/*     */     {
/* 177 */       rp = resourcePaths[i];
/* 178 */       p = findProps(rp, pbrp);
/* 179 */       if (p == null)
/*     */       {
/* 181 */         System.err.println("Could not find loaded properties for resource path: " + rp);
/*     */       }
/*     */       else
/* 184 */         for (ii = p.keySet().iterator(); ii.hasNext();)
/*     */         {
/* 186 */           Object kObj = ii.next();
/* 187 */           if (!(kObj instanceof String))
/*     */           {
/*     */ 
/*     */ 
/* 191 */             System.err.println(class$com$mchange$v2$cfg$BasicMultiPropertiesConfig.getName() + ": " + "Properties object found at resource path " + ("/".equals(rp) ? "[system properties]" : new StringBuffer().append("'").append(rp).append("'").toString()) + "' contains a key that is not a String: " + kObj);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 196 */             System.err.println("Skipping...");
/*     */           }
/*     */           else {
/* 199 */             Object vObj = p.get(kObj);
/* 200 */             if ((vObj != null) && (!(vObj instanceof String)))
/*     */             {
/*     */ 
/*     */ 
/* 204 */               System.err.println(class$com$mchange$v2$cfg$BasicMultiPropertiesConfig.getName() + ": " + "Properties object found at resource path " + ("/".equals(rp) ? "[system properties]" : new StringBuffer().append("'").append(rp).append("'").toString()) + " contains a value that is not a String: " + vObj);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 209 */               System.err.println("Skipping...");
/*     */             }
/*     */             else
/*     */             {
/* 213 */               String key = (String)kObj;
/* 214 */               String val = (String)vObj;
/* 215 */               out.put(key, val);
/*     */             }
/*     */           } } }
/* 218 */     return out;
/*     */   }
/*     */   
/*     */   private static Map extractPrefixMapFromRsrcPathMap(String[] resourcePaths, Map pbrp)
/*     */   {
/* 223 */     Map out = new HashMap();
/*     */     
/* 225 */     int i = 0; String rp; Properties p; Iterator jj; for (int len = resourcePaths.length; i < len; i++)
/*     */     {
/* 227 */       rp = resourcePaths[i];
/* 228 */       p = findProps(rp, pbrp);
/* 229 */       if (p == null)
/*     */       {
/* 231 */         System.err.println(BasicMultiPropertiesConfig.class.getName() + " -- Could not find loaded properties for resource path: " + rp);
/*     */       }
/*     */       else
/* 234 */         for (jj = p.keySet().iterator(); jj.hasNext();)
/*     */         {
/* 236 */           Object kObj = jj.next();
/* 237 */           if (!(kObj instanceof String))
/*     */           {
/*     */ 
/*     */ 
/* 241 */             System.err.println(class$com$mchange$v2$cfg$BasicMultiPropertiesConfig.getName() + ": " + "Properties object found at resource path " + ("/".equals(rp) ? "[system properties]" : new StringBuffer().append("'").append(rp).append("'").toString()) + "' contains a key that is not a String: " + kObj);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 246 */             System.err.println("Skipping...");
/*     */           }
/*     */           else
/*     */           {
/* 250 */             String key = (String)kObj;
/* 251 */             String prefix = extractPrefix(key);
/* 252 */             while (prefix != null)
/*     */             {
/* 254 */               Properties byPfx = (Properties)out.get(prefix);
/* 255 */               if (byPfx == null)
/*     */               {
/* 257 */                 byPfx = new Properties();
/* 258 */                 out.put(prefix, byPfx);
/*     */               }
/* 260 */               byPfx.put(key, p.get(key));
/*     */               
/* 262 */               prefix = extractPrefix(prefix);
/*     */             }
/*     */           }
/*     */         } }
/* 266 */     return out;
/*     */   }
/*     */   
/*     */   public String[] getPropertiesResourcePaths() {
/* 270 */     return (String[])this.rps.clone();
/*     */   }
/*     */   
/*     */   public Properties getPropertiesByResourcePath(String path) {
/* 274 */     Properties out = (Properties)this.propsByResourcePaths.get(path);
/* 275 */     return out == null ? new Properties() : out;
/*     */   }
/*     */   
/*     */   public Properties getPropertiesByPrefix(String pfx)
/*     */   {
/* 280 */     Properties out = (Properties)this.propsByPrefixes.get(pfx);
/* 281 */     return out == null ? new Properties() : out;
/*     */   }
/*     */   
/*     */   public String getProperty(String key) {
/* 285 */     return this.propsByKey.getProperty(key);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\cfg\BasicMultiPropertiesConfig.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */