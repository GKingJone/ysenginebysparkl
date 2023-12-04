/*    */ package com.mchange.v2.cfg;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class CombinedMultiPropertiesConfig
/*    */   extends MultiPropertiesConfig
/*    */ {
/*    */   MultiPropertiesConfig[] configs;
/*    */   String[] resourcePaths;
/*    */   
/*    */   CombinedMultiPropertiesConfig(MultiPropertiesConfig[] configs)
/*    */   {
/* 35 */     this.configs = configs;
/*    */     
/* 37 */     List allPaths = new LinkedList();
/* 38 */     for (int i = configs.length - 1; i >= 0; i--)
/*    */     {
/* 40 */       String[] rps = configs[i].getPropertiesResourcePaths();
/* 41 */       for (int j = rps.length - 1; j >= 0; j--)
/*    */       {
/* 43 */         String rp = rps[j];
/* 44 */         if (!allPaths.contains(rp))
/* 45 */           allPaths.add(0, rp);
/*    */       }
/*    */     }
/* 48 */     this.resourcePaths = ((String[])allPaths.toArray(new String[allPaths.size()]));
/*    */   }
/*    */   
/*    */   public String[] getPropertiesResourcePaths() {
/* 52 */     return (String[])this.resourcePaths.clone();
/*    */   }
/*    */   
/*    */   public Properties getPropertiesByResourcePath(String path) {
/* 56 */     for (int i = this.configs.length - 1; i >= 0; i--)
/*    */     {
/* 58 */       MultiPropertiesConfig config = this.configs[i];
/* 59 */       Properties check = config.getPropertiesByResourcePath(path);
/* 60 */       if (check != null)
/* 61 */         return check;
/*    */     }
/* 63 */     return null;
/*    */   }
/*    */   
/*    */   public Properties getPropertiesByPrefix(String pfx)
/*    */   {
/* 68 */     List entries = new LinkedList();
/* 69 */     for (int i = this.configs.length - 1; i >= 0; i--)
/*    */     {
/* 71 */       MultiPropertiesConfig config = this.configs[i];
/* 72 */       Properties check = config.getPropertiesByPrefix(pfx);
/* 73 */       if (check != null)
/* 74 */         entries.addAll(0, check.entrySet());
/*    */     }
/* 76 */     if (entries.size() == 0) {
/* 77 */       return null;
/*    */     }
/*    */     
/* 80 */     Properties out = new Properties();
/* 81 */     for (Iterator ii = entries.iterator(); ii.hasNext();)
/*    */     {
/* 83 */       Map.Entry entry = (Map.Entry)ii.next();
/* 84 */       out.put(entry.getKey(), entry.getValue());
/*    */     }
/* 86 */     return out;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getProperty(String key)
/*    */   {
/* 92 */     for (int i = this.configs.length - 1; i >= 0; i--)
/*    */     {
/* 94 */       MultiPropertiesConfig config = this.configs[i];
/* 95 */       String check = config.getProperty(key);
/* 96 */       if (check != null)
/* 97 */         return check;
/*    */     }
/* 99 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\cfg\CombinedMultiPropertiesConfig.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */