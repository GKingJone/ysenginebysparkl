/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.Manifest;
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
/*     */ public class JarResource
/*     */   extends URLResource
/*     */ {
/*  43 */   private static final Logger LOG = Log.getLogger(JarResource.class);
/*     */   
/*     */   protected JarURLConnection _jarConnection;
/*     */   
/*     */   protected JarResource(URL url)
/*     */   {
/*  49 */     super(url, null);
/*     */   }
/*     */   
/*     */ 
/*     */   protected JarResource(URL url, boolean useCaches)
/*     */   {
/*  55 */     super(url, null, useCaches);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/*  62 */     this._jarConnection = null;
/*  63 */     super.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized boolean checkConnection()
/*     */   {
/*  70 */     super.checkConnection();
/*     */     try
/*     */     {
/*  73 */       if (this._jarConnection != this._connection) {
/*  74 */         newConnection();
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*  78 */       LOG.ignore(e);
/*  79 */       this._jarConnection = null;
/*     */     }
/*     */     
/*  82 */     return this._jarConnection != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void newConnection()
/*     */     throws IOException
/*     */   {
/*  91 */     this._jarConnection = ((JarURLConnection)this._connection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 101 */     if (this._urlString.endsWith("!/")) {
/* 102 */       return checkConnection();
/*     */     }
/* 104 */     return super.exists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */     throws IOException
/*     */   {
/* 112 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 120 */     checkConnection();
/* 121 */     if (!this._urlString.endsWith("!/"))
/* 122 */       new FilterInputStream(getInputStream(false))
/*     */       {
/*     */         public void close() throws IOException {
/* 125 */           this.in = IO.getClosedStream();
/*     */         }
/*     */       };
/* 128 */     URL url = new URL(this._urlString.substring(4, this._urlString.length() - 2));
/* 129 */     InputStream is = url.openStream();
/* 130 */     return is;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyTo(File directory)
/*     */     throws IOException
/*     */   {
/* 141 */     if (!exists()) {
/* 142 */       return;
/*     */     }
/* 144 */     if (LOG.isDebugEnabled()) {
/* 145 */       LOG.debug("Extract " + this + " to " + directory, new Object[0]);
/*     */     }
/* 147 */     String urlString = getURL().toExternalForm().trim();
/* 148 */     int endOfJarUrl = urlString.indexOf("!/");
/* 149 */     int startOfJarUrl = endOfJarUrl >= 0 ? 4 : 0;
/*     */     
/* 151 */     if (endOfJarUrl < 0) {
/* 152 */       throw new IOException("Not a valid jar url: " + urlString);
/*     */     }
/* 154 */     URL jarFileURL = new URL(urlString.substring(startOfJarUrl, endOfJarUrl));
/* 155 */     String subEntryName = endOfJarUrl + 2 < urlString.length() ? urlString.substring(endOfJarUrl + 2) : null;
/* 156 */     boolean subEntryIsDir = (subEntryName != null) && (subEntryName.endsWith("/"));
/*     */     
/* 158 */     if (LOG.isDebugEnabled())
/* 159 */       LOG.debug("Extracting entry = " + subEntryName + " from jar " + jarFileURL, new Object[0]);
/* 160 */     URLConnection c = jarFileURL.openConnection();
/* 161 */     c.setUseCaches(false);
/* 162 */     InputStream is = c.getInputStream();Throwable localThrowable12 = null;
/* 163 */     try { JarInputStream jin = new JarInputStream(is);Throwable localThrowable13 = null;
/*     */       try {
/*     */         JarEntry entry;
/*     */         OutputStream fout;
/* 167 */         while ((entry = jin.getNextJarEntry()) != null)
/*     */         {
/* 169 */           String entryName = entry.getName();
/* 170 */           boolean shouldExtract; boolean shouldExtract; if ((subEntryName != null) && (entryName.startsWith(subEntryName)))
/*     */           {
/*     */ 
/* 173 */             if ((!subEntryIsDir) && (subEntryName.length() + 1 == entryName.length()) && (entryName.endsWith("/"))) {
/* 174 */               subEntryIsDir = true;
/*     */             }
/*     */             
/*     */             boolean shouldExtract;
/* 178 */             if (subEntryIsDir)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 184 */               entryName = entryName.substring(subEntryName.length());
/* 185 */               boolean shouldExtract; if (!entryName.equals(""))
/*     */               {
/*     */ 
/* 188 */                 shouldExtract = true;
/*     */               }
/*     */               else {
/* 191 */                 shouldExtract = false;
/*     */               }
/*     */             } else {
/* 194 */               shouldExtract = true;
/*     */             } } else { boolean shouldExtract;
/* 196 */             if ((subEntryName != null) && (!entryName.startsWith(subEntryName)))
/*     */             {
/*     */ 
/*     */ 
/* 200 */               shouldExtract = false;
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 205 */               shouldExtract = true;
/*     */             }
/*     */           }
/* 208 */           if (!shouldExtract)
/*     */           {
/* 210 */             if (LOG.isDebugEnabled()) {
/* 211 */               LOG.debug("Skipping entry: " + entryName, new Object[0]);
/*     */             }
/*     */           }
/*     */           else {
/* 215 */             String dotCheck = entryName.replace('\\', '/');
/* 216 */             dotCheck = URIUtil.canonicalPath(dotCheck);
/* 217 */             if (dotCheck == null)
/*     */             {
/* 219 */               if (LOG.isDebugEnabled()) {
/* 220 */                 LOG.debug("Invalid entry: " + entryName, new Object[0]);
/*     */               }
/*     */             }
/*     */             else {
/* 224 */               File file = new File(directory, entryName);
/*     */               
/* 226 */               if (entry.isDirectory())
/*     */               {
/*     */ 
/* 229 */                 if (!file.exists()) {
/* 230 */                   file.mkdirs();
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 235 */                 File dir = new File(file.getParent());
/* 236 */                 if (!dir.exists()) {
/* 237 */                   dir.mkdirs();
/*     */                 }
/*     */                 
/* 240 */                 fout = new FileOutputStream(file);Throwable localThrowable14 = null;
/*     */                 try {
/* 242 */                   IO.copy(jin, fout);
/*     */                 }
/*     */                 catch (Throwable localThrowable1)
/*     */                 {
/* 240 */                   localThrowable14 = localThrowable1;throw localThrowable1;
/*     */                 }
/*     */                 finally {}
/*     */                 
/*     */ 
/*     */ 
/* 246 */                 if (entry.getTime() >= 0L)
/* 247 */                   file.setLastModified(entry.getTime());
/*     */               }
/*     */             }
/*     */           } }
/* 251 */         if ((subEntryName == null) || ((subEntryName != null) && (subEntryName.equalsIgnoreCase("META-INF/MANIFEST.MF"))))
/*     */         {
/* 253 */           Manifest manifest = jin.getManifest();
/* 254 */           if (manifest != null)
/*     */           {
/* 256 */             File metaInf = new File(directory, "META-INF");
/* 257 */             metaInf.mkdir();
/* 258 */             File f = new File(metaInf, "MANIFEST.MF");
/* 259 */             OutputStream fout = new FileOutputStream(f);fout = null;
/*     */             try {
/* 261 */               manifest.write(fout);
/*     */             }
/*     */             catch (Throwable localThrowable16)
/*     */             {
/* 259 */               fout = localThrowable16;throw localThrowable16;
/*     */             }
/*     */             finally {}
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable7)
/*     */       {
/* 162 */         localThrowable13 = localThrowable7;throw localThrowable7; } finally {} } catch (Throwable localThrowable10) { localThrowable12 = localThrowable10;throw localThrowable10;
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
/*     */     }
/*     */     finally
/*     */     {
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
/* 265 */       if (is != null) if (localThrowable12 != null) try { is.close(); } catch (Throwable localThrowable11) { localThrowable12.addSuppressed(localThrowable11); } else is.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static Resource newJarResource(Resource resource) throws IOException {
/* 270 */     if ((resource instanceof JarResource))
/* 271 */       return resource;
/* 272 */     return Resource.newResource("jar:" + resource + "!/");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\JarResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */