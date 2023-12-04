/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.ReadableByteChannel;
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
/*     */ public class EmptyResource
/*     */   extends Resource
/*     */ {
/*  35 */   public static final Resource INSTANCE = new EmptyResource();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isContainedIn(Resource r)
/*     */     throws MalformedURLException
/*     */   {
/*  44 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/*  55 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/*  61 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public long lastModified()
/*     */   {
/*  67 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long length()
/*     */   {
/*  73 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/*  79 */     return null;
/*     */   }
/*     */   
/*     */   public File getFile()
/*     */     throws IOException
/*     */   {
/*  85 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  91 */     return null;
/*     */   }
/*     */   
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/*  97 */     return null;
/*     */   }
/*     */   
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 103 */     return null;
/*     */   }
/*     */   
/*     */   public boolean delete()
/*     */     throws SecurityException
/*     */   {
/* 109 */     return false;
/*     */   }
/*     */   
/*     */   public boolean renameTo(Resource dest)
/*     */     throws SecurityException
/*     */   {
/* 115 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] list()
/*     */   {
/* 121 */     return null;
/*     */   }
/*     */   
/*     */   public Resource addPath(String path)
/*     */     throws IOException, MalformedURLException
/*     */   {
/* 127 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\EmptyResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */