/*     */ package com.mchange.v2.c3p0.cfg;
/*     */ 
/*     */ import com.mchange.v1.xml.DomParseUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
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
/*     */ public final class C3P0ConfigXmlUtils
/*     */ {
/*     */   public static final String XML_CONFIG_RSRC_PATH = "/c3p0-config.xml";
/*  38 */   static final MLogger logger = MLog.getLogger(C3P0ConfigXmlUtils.class);
/*     */   
/*     */   public static final String LINESEP;
/*     */   
/*  42 */   private static final String[] MISSPELL_PFXS = { "/c3p0", "/c3pO", "/c3po", "/C3P0", "/C3PO" };
/*  43 */   private static final char[] MISSPELL_LINES = { '-', '_' };
/*  44 */   private static final String[] MISSPELL_CONFIG = { "config", "CONFIG" };
/*  45 */   private static final String[] MISSPELL_XML = { "xml", "XML" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final void warnCommonXmlConfigResourceMisspellings()
/*     */   {
/*  56 */     if (logger.isLoggable(MLevel.WARNING))
/*     */     {
/*  58 */       int a = 0; for (int lena = MISSPELL_PFXS.length; a < lena; a++)
/*     */       {
/*  60 */         StringBuffer sb = new StringBuffer(16);
/*  61 */         sb.append(MISSPELL_PFXS[a]);
/*  62 */         int b = 0; for (int lenb = MISSPELL_LINES.length; b < lenb; b++)
/*     */         {
/*  64 */           sb.append(MISSPELL_LINES[b]);
/*  65 */           int c = 0; for (int lenc = MISSPELL_CONFIG.length; c < lenc; c++)
/*     */           {
/*  67 */             sb.append(MISSPELL_CONFIG[c]);
/*  68 */             sb.append('.');
/*  69 */             int d = 0; for (int lend = MISSPELL_XML.length; d < lend; d++)
/*     */             {
/*  71 */               sb.append(MISSPELL_XML[d]);
/*  72 */               String test = sb.toString();
/*  73 */               if (!test.equals("/c3p0-config.xml"))
/*     */               {
/*  75 */                 Object hopefullyNull = C3P0ConfigXmlUtils.class.getResource(test);
/*  76 */                 if (hopefullyNull != null)
/*     */                 {
/*  78 */                   logger.warning("POSSIBLY MISSPELLED c3p0-conf.xml RESOURCE FOUND. Please ensure the file name is c3p0-config.xml, all lower case, with the digit 0 (NOT the letter O) in c3p0. It should be placed  in the top level of c3p0's effective classpath.");
/*     */                   
/*     */ 
/*     */ 
/*  82 */                   return;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*     */     String ls;
/*     */     try
/*     */     {
/*  98 */       ls = System.getProperty("line.separator", "\r\n");
/*     */     } catch (Exception e) {
/* 100 */       ls = "\r\n";
/*     */     }
/* 102 */     LINESEP = ls;
/*     */   }
/*     */   
/*     */   public static C3P0Config extractXmlConfigFromDefaultResource()
/*     */     throws Exception
/*     */   {
/* 108 */     InputStream is = null;
/*     */     
/*     */     try
/*     */     {
/* 112 */       is = C3P0ConfigUtils.class.getResourceAsStream("/c3p0-config.xml");
/* 113 */       C3P0Config localC3P0Config; if (is == null)
/*     */       {
/* 115 */         warnCommonXmlConfigResourceMisspellings();
/* 116 */         return null;
/*     */       }
/*     */       
/* 119 */       return extractXmlConfigFromInputStream(is);
/*     */     }
/*     */     finally {
/*     */       try {
/* 123 */         if (is != null) is.close();
/*     */       }
/*     */       catch (Exception e) {
/* 126 */         if (logger.isLoggable(MLevel.FINE)) {
/* 127 */           logger.log(MLevel.FINE, "Exception on resource InputStream close.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static C3P0Config extractXmlConfigFromInputStream(InputStream is) throws Exception {
/* 134 */     DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
/* 135 */     DocumentBuilder db = fact.newDocumentBuilder();
/* 136 */     Document doc = db.parse(is);
/*     */     
/* 138 */     return extractConfigFromXmlDoc(doc);
/*     */   }
/*     */   
/*     */   public static C3P0Config extractConfigFromXmlDoc(Document doc) throws Exception
/*     */   {
/* 143 */     Element docElem = doc.getDocumentElement();
/* 144 */     if (docElem.getTagName().equals("c3p0-config"))
/*     */     {
/*     */ 
/* 147 */       HashMap configNamesToNamedScopes = new HashMap();
/*     */       
/* 149 */       Element defaultConfigElem = DomParseUtils.uniqueChild(docElem, "default-config");
/* 150 */       NamedScope defaults; NamedScope defaults; if (defaultConfigElem != null) {
/* 151 */         defaults = extractNamedScopeFromLevel(defaultConfigElem);
/*     */       } else
/* 153 */         defaults = new NamedScope();
/* 154 */       NodeList nl = DomParseUtils.immediateChildElementsByTagName(docElem, "named-config");
/* 155 */       int i = 0; for (int len = nl.getLength(); i < len; i++)
/*     */       {
/* 157 */         Element namedConfigElem = (Element)nl.item(i);
/* 158 */         String configName = namedConfigElem.getAttribute("name");
/* 159 */         if ((configName != null) && (configName.length() > 0))
/*     */         {
/* 161 */           NamedScope namedConfig = extractNamedScopeFromLevel(namedConfigElem);
/* 162 */           configNamesToNamedScopes.put(configName, namedConfig);
/*     */         }
/*     */         else {
/* 165 */           logger.warning("Configuration XML contained named-config element without name attribute: " + namedConfigElem);
/*     */         } }
/* 167 */       return new C3P0Config(defaults, configNamesToNamedScopes);
/*     */     }
/*     */     
/* 170 */     throw new Exception("Root element of c3p0 config xml should be 'c3p0-config', not '" + docElem.getTagName() + "'.");
/*     */   }
/*     */   
/*     */   private static NamedScope extractNamedScopeFromLevel(Element elem)
/*     */   {
/* 175 */     HashMap props = extractPropertiesFromLevel(elem);
/* 176 */     HashMap userNamesToOverrides = new HashMap();
/*     */     
/* 178 */     NodeList nl = DomParseUtils.immediateChildElementsByTagName(elem, "user-overrides");
/* 179 */     int i = 0; for (int len = nl.getLength(); i < len; i++)
/*     */     {
/* 181 */       Element perUserConfigElem = (Element)nl.item(i);
/* 182 */       String userName = perUserConfigElem.getAttribute("user");
/* 183 */       if ((userName != null) && (userName.length() > 0))
/*     */       {
/* 185 */         HashMap userProps = extractPropertiesFromLevel(perUserConfigElem);
/* 186 */         userNamesToOverrides.put(userName, userProps);
/*     */       }
/*     */       else {
/* 189 */         logger.warning("Configuration XML contained user-overrides element without user attribute: " + LINESEP + perUserConfigElem);
/*     */       }
/*     */     }
/* 192 */     return new NamedScope(props, userNamesToOverrides);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static HashMap extractPropertiesFromLevel(Element elem)
/*     */   {
/* 199 */     HashMap out = new HashMap();
/*     */     
/*     */     try
/*     */     {
/* 203 */       NodeList nl = DomParseUtils.immediateChildElementsByTagName(elem, "property");
/* 204 */       int len = nl.getLength();
/* 205 */       for (int i = 0; i < len; i++)
/*     */       {
/* 207 */         Element propertyElem = (Element)nl.item(i);
/* 208 */         String propName = propertyElem.getAttribute("name");
/* 209 */         if ((propName != null) && (propName.length() > 0))
/*     */         {
/* 211 */           String propVal = DomParseUtils.allTextFromElement(propertyElem, true);
/* 212 */           out.put(propName, propVal);
/*     */         }
/*     */         else
/*     */         {
/* 216 */           logger.warning("Configuration XML contained property element without name attribute: " + LINESEP + propertyElem);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 221 */       logger.log(MLevel.WARNING, "An exception occurred while reading config XML. Some configuration information has probably been ignored.", e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 227 */     return out;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\cfg\C3P0ConfigXmlUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */