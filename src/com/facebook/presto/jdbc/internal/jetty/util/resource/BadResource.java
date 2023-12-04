/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
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
/*     */ class BadResource
/*     */   extends URLResource
/*     */ {
/*  39 */   private String _message = null;
/*     */   
/*     */ 
/*     */   BadResource(URL url, String message)
/*     */   {
/*  44 */     super(url, null);
/*  45 */     this._message = message;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/*  53 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long lastModified()
/*     */   {
/*  60 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/*  67 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long length()
/*     */   {
/*  74 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/*  82 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/*  89 */     throw new FileNotFoundException(this._message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean delete()
/*     */     throws SecurityException
/*     */   {
/*  97 */     throw new SecurityException(this._message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean renameTo(Resource dest)
/*     */     throws SecurityException
/*     */   {
/* 105 */     throw new SecurityException(this._message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] list()
/*     */   {
/* 112 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void copyTo(File destination)
/*     */     throws IOException
/*     */   {
/* 120 */     throw new SecurityException(this._message);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 127 */     return super.toString() + "; BadResource=" + this._message;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\BadResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */