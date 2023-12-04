/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
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
/*     */ public class Loader
/*     */ {
/*     */   public static URL getResource(Class<?> loadClass, String name)
/*     */   {
/*  51 */     URL url = null;
/*  52 */     ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
/*  53 */     if (context_loader != null) {
/*  54 */       url = context_loader.getResource(name);
/*     */     }
/*  56 */     if ((url == null) && (loadClass != null))
/*     */     {
/*  58 */       ClassLoader load_loader = loadClass.getClassLoader();
/*  59 */       if ((load_loader != null) && (load_loader != context_loader)) {
/*  60 */         url = load_loader.getResource(name);
/*     */       }
/*     */     }
/*  63 */     if (url == null) {
/*  64 */       url = ClassLoader.getSystemResource(name);
/*     */     }
/*  66 */     return url;
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
/*     */   public static Class loadClass(Class loadClass, String name)
/*     */     throws ClassNotFoundException
/*     */   {
/*  81 */     ClassNotFoundException ex = null;
/*  82 */     Class<?> c = null;
/*  83 */     ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
/*  84 */     if (context_loader != null) {
/*     */       try {
/*  86 */         c = context_loader.loadClass(name);
/*  87 */       } catch (ClassNotFoundException e) { ex = e;
/*     */       }
/*     */     }
/*  90 */     if ((c == null) && (loadClass != null))
/*     */     {
/*  92 */       ClassLoader load_loader = loadClass.getClassLoader();
/*  93 */       if ((load_loader != null) && (load_loader != context_loader)) {
/*     */         try {
/*  95 */           c = load_loader.loadClass(name);
/*  96 */         } catch (ClassNotFoundException e) { if (ex == null) ex = e;
/*     */         }
/*     */       }
/*     */     }
/* 100 */     if (c == null) {
/*     */       try {
/* 102 */         c = Class.forName(name);
/*     */       }
/*     */       catch (ClassNotFoundException e) {
/* 105 */         if (ex != null)
/* 106 */           throw ex;
/* 107 */         throw e;
/*     */       }
/*     */     }
/*     */     
/* 111 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ResourceBundle getResourceBundle(Class<?> loadClass, String name, boolean checkParents, Locale locale)
/*     */     throws MissingResourceException
/*     */   {
/* 120 */     MissingResourceException ex = null;
/* 121 */     ResourceBundle bundle = null;
/* 122 */     ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 123 */     while ((bundle == null) && (loader != null)) {
/*     */       try {
/* 125 */         bundle = ResourceBundle.getBundle(name, locale, loader);
/* 126 */       } catch (MissingResourceException e) { if (ex == null) ex = e; }
/* 127 */       loader = (bundle == null) && (checkParents) ? loader.getParent() : null;
/*     */     }
/*     */     
/* 130 */     loader = loadClass == null ? null : loadClass.getClassLoader();
/* 131 */     while ((bundle == null) && (loader != null)) {
/*     */       try {
/* 133 */         bundle = ResourceBundle.getBundle(name, locale, loader);
/* 134 */       } catch (MissingResourceException e) { if (ex == null) ex = e; }
/* 135 */       loader = (bundle == null) && (checkParents) ? loader.getParent() : null;
/*     */     }
/*     */     
/* 138 */     if (bundle == null) {
/*     */       try {
/* 140 */         bundle = ResourceBundle.getBundle(name, locale);
/* 141 */       } catch (MissingResourceException e) { if (ex == null) ex = e;
/*     */       }
/*     */     }
/* 144 */     if (bundle != null)
/* 145 */       return bundle;
/* 146 */     throw ex;
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
/*     */   public static String getClassPath(ClassLoader loader)
/*     */     throws Exception
/*     */   {
/* 162 */     StringBuilder classpath = new StringBuilder();
/* 163 */     while ((loader != null) && ((loader instanceof URLClassLoader)))
/*     */     {
/* 165 */       URL[] urls = ((URLClassLoader)loader).getURLs();
/* 166 */       if (urls != null)
/*     */       {
/* 168 */         for (int i = 0; i < urls.length; i++)
/*     */         {
/* 170 */           Resource resource = Resource.newResource(urls[i]);
/* 171 */           File file = resource.getFile();
/* 172 */           if ((file != null) && (file.exists()))
/*     */           {
/* 174 */             if (classpath.length() > 0)
/* 175 */               classpath.append(File.pathSeparatorChar);
/* 176 */             classpath.append(file.getAbsolutePath());
/*     */           }
/*     */         }
/*     */       }
/* 180 */       loader = loader.getParent();
/*     */     }
/* 182 */     return classpath.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Loader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */