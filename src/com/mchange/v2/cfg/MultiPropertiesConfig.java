/*     */ package com.mchange.v2.cfg;
/*     */ 
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
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
/*     */ public abstract class MultiPropertiesConfig
/*     */ {
/*  54 */   static final MultiPropertiesConfig EMPTY = new BasicMultiPropertiesConfig(new String[0]);
/*     */   
/*     */   static final String VM_CONFIG_RSRC_PATHS = "/com/mchange/v2/cfg/vmConfigResourcePaths.txt";
/*     */   
/*  58 */   static MultiPropertiesConfig vmConfig = null;
/*     */   
/*     */   public static MultiPropertiesConfig read(String[] resourcePath, MLogger logger) {
/*  61 */     return new BasicMultiPropertiesConfig(resourcePath, logger);
/*     */   }
/*     */   
/*  64 */   public static MultiPropertiesConfig read(String[] resourcePath) { return new BasicMultiPropertiesConfig(resourcePath); }
/*     */   
/*     */   public static MultiPropertiesConfig combine(MultiPropertiesConfig[] configs) {
/*  67 */     return new CombinedMultiPropertiesConfig(configs);
/*     */   }
/*     */   
/*     */   public static MultiPropertiesConfig readVmConfig(String[] defaultResources, String[] preemptingResources) {
/*  71 */     List l = new LinkedList();
/*  72 */     if (defaultResources != null)
/*  73 */       l.add(read(defaultResources));
/*  74 */     l.add(readVmConfig());
/*  75 */     if (preemptingResources != null)
/*  76 */       l.add(read(preemptingResources));
/*  77 */     return combine((MultiPropertiesConfig[])l.toArray(new MultiPropertiesConfig[l.size()]));
/*     */   }
/*     */   
/*     */   public static MultiPropertiesConfig readVmConfig()
/*     */   {
/*  82 */     if (vmConfig == null)
/*     */     {
/*  84 */       List rps = new ArrayList();
/*     */       
/*  86 */       BufferedReader br = null;
/*     */       try
/*     */       {
/*  89 */         InputStream is = MultiPropertiesConfig.class.getResourceAsStream("/com/mchange/v2/cfg/vmConfigResourcePaths.txt");
/*  90 */         if (is != null)
/*     */         {
/*  92 */           br = new BufferedReader(new InputStreamReader(is, "8859_1"));
/*     */           String rp;
/*  94 */           while ((rp = br.readLine()) != null)
/*     */           {
/*  96 */             rp = rp.trim();
/*  97 */             if ((!"".equals(rp)) && (!rp.startsWith("#")))
/*     */             {
/*     */ 
/* 100 */               rps.add(rp); }
/*     */           }
/* 102 */           vmConfig = new BasicMultiPropertiesConfig((String[])rps.toArray(new String[rps.size()]));
/*     */         }
/*     */         else
/*     */         {
/* 106 */           System.err.println("com.mchange.v2.cfg.MultiPropertiesConfig: Resource path list could not be found at resource path: /com/mchange/v2/cfg/vmConfigResourcePaths.txt");
/* 107 */           System.err.println("com.mchange.v2.cfg.MultiPropertiesConfig: Using empty vmconfig.");
/* 108 */           vmConfig = EMPTY;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 119 */         return vmConfig;
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 112 */         e.printStackTrace();
/*     */       } finally {
/*     */         try {
/* 115 */           if (br != null) br.close();
/* 116 */         } catch (IOException e) { e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean foundVmConfig() {
/* 123 */     return vmConfig != EMPTY;
/*     */   }
/*     */   
/*     */   public abstract String[] getPropertiesResourcePaths();
/*     */   
/*     */   public abstract Properties getPropertiesByResourcePath(String paramString);
/*     */   
/*     */   public abstract Properties getPropertiesByPrefix(String paramString);
/*     */   
/*     */   public abstract String getProperty(String paramString);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\cfg\MultiPropertiesConfig.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */