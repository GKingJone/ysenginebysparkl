/*    */ package com.mchange.v2.c3p0.cfg;
/*    */ 
/*    */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.util.HashMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultC3P0ConfigFinder
/*    */   implements C3P0ConfigFinder
/*    */ {
/*    */   static final String XML_CFG_FILE_KEY = "com.mchange.v2.c3p0.cfg.xml";
/*    */   
/*    */   public C3P0Config findConfig()
/*    */     throws Exception
/*    */   {
/* 39 */     HashMap flatDefaults = C3P0ConfigUtils.extractHardcodedC3P0Defaults();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 44 */     flatDefaults.putAll(C3P0ConfigUtils.extractC3P0PropertiesResources());
/*    */     
/* 46 */     String cfgFile = MultiPropertiesConfig.readVmConfig().getProperty("com.mchange.v2.c3p0.cfg.xml");
/* 47 */     C3P0Config out; C3P0Config out; if (cfgFile == null)
/*    */     {
/* 49 */       C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromDefaultResource();
/* 50 */       C3P0Config out; if (xmlConfig != null)
/*    */       {
/* 52 */         insertDefaultsUnderNascentConfig(flatDefaults, xmlConfig);
/* 53 */         out = xmlConfig;
/*    */       }
/*    */       else {
/* 56 */         out = C3P0ConfigUtils.configFromFlatDefaults(flatDefaults);
/*    */       }
/*    */     }
/*    */     else {
/* 60 */       InputStream is = new BufferedInputStream(new FileInputStream(cfgFile));
/*    */       try
/*    */       {
/* 63 */         C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromInputStream(is);
/* 64 */         insertDefaultsUnderNascentConfig(flatDefaults, xmlConfig);
/* 65 */         out = xmlConfig;
/*    */         
/*    */         try
/*    */         {
/* 69 */           is.close();
/*    */         } catch (Exception e) {
/* 71 */           e.printStackTrace();
/*    */         }
/*    */         
/*    */ 
/*    */ 
/*    */ 
/* 77 */         sysPropConfig = C3P0ConfigUtils.findAllC3P0SystemProperties();
/*    */       }
/*    */       finally
/*    */       {
/*    */         try
/*    */         {
/* 69 */           is.close();
/*    */         } catch (Exception e) {
/* 71 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */     Properties sysPropConfig;
/*    */     
/* 78 */     out.defaultConfig.props.putAll(sysPropConfig);
/*    */     
/* 80 */     return out;
/*    */   }
/*    */   
/*    */   private void insertDefaultsUnderNascentConfig(HashMap flatDefaults, C3P0Config config)
/*    */   {
/* 85 */     flatDefaults.putAll(config.defaultConfig.props);
/* 86 */     config.defaultConfig.props = flatDefaults;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\cfg\DefaultC3P0ConfigFinder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */