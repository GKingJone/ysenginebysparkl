/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.file.InvalidPathException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.security.Permission;
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
/*     */ @Deprecated
/*     */ public class FileResource
/*     */   extends Resource
/*     */ {
/*  56 */   private static final Logger LOG = Log.getLogger(FileResource.class);
/*     */   
/*     */   private final File _file;
/*     */   
/*     */   private final String _uri;
/*     */   
/*     */   private final URI _alias;
/*     */   
/*     */ 
/*     */   public FileResource(URL url)
/*     */     throws IOException, URISyntaxException
/*     */   {
/*     */     File file;
/*     */     try
/*     */     {
/*  71 */       File file = new File(url.toURI());
/*  72 */       assertValidPath(file.toString());
/*     */     }
/*     */     catch (URISyntaxException e)
/*     */     {
/*  76 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  80 */       if (!url.toString().startsWith("file:")) {
/*  81 */         throw new IllegalArgumentException("!file:");
/*     */       }
/*  83 */       LOG.ignore(e);
/*     */       
/*     */       try
/*     */       {
/*  87 */         String file_url = "file:" + URIUtil.encodePath(url.toString().substring(5));
/*  88 */         URI uri = new URI(file_url);
/*  89 */         File file; if (uri.getAuthority() == null) {
/*  90 */           file = new File(uri);
/*     */         } else {
/*  92 */           file = new File("//" + uri.getAuthority() + URIUtil.decodePath(url.getFile()));
/*     */         }
/*     */       } catch (Exception e2) {
/*     */         File file;
/*  96 */         LOG.ignore(e2);
/*     */         
/*  98 */         URLConnection connection = url.openConnection();
/*  99 */         Permission perm = connection.getPermission();
/* 100 */         file = new File(perm == null ? url.getFile() : perm.getName());
/*     */       }
/*     */     }
/*     */     
/* 104 */     this._file = file;
/* 105 */     this._uri = normalizeURI(this._file, url.toURI());
/* 106 */     this._alias = checkFileAlias(this._file);
/*     */   }
/*     */   
/*     */ 
/*     */   public FileResource(URI uri)
/*     */   {
/* 112 */     File file = new File(uri);
/* 113 */     this._file = file;
/* 114 */     URI file_uri = this._file.toURI();
/* 115 */     this._uri = normalizeURI(this._file, uri);
/* 116 */     assertValidPath(file.toString());
/*     */     
/*     */ 
/* 119 */     if (!URIUtil.equalsIgnoreEncodings(this._uri, file_uri.toString())) {
/* 120 */       this._alias = this._file.toURI();
/*     */     } else {
/* 122 */       this._alias = checkFileAlias(this._file);
/*     */     }
/*     */   }
/*     */   
/*     */   public FileResource(File file)
/*     */   {
/* 128 */     assertValidPath(file.toString());
/* 129 */     this._file = file;
/* 130 */     this._uri = normalizeURI(this._file, this._file.toURI());
/* 131 */     this._alias = checkFileAlias(this._file);
/*     */   }
/*     */   
/*     */ 
/*     */   private static String normalizeURI(File file, URI uri)
/*     */   {
/* 137 */     String u = uri.toASCIIString();
/* 138 */     if (file.isDirectory())
/*     */     {
/* 140 */       if (!u.endsWith("/")) {
/* 141 */         u = u + "/";
/*     */       }
/* 143 */     } else if ((file.exists()) && (u.endsWith("/")))
/* 144 */       u = u.substring(0, u.length() - 1);
/* 145 */     return u;
/*     */   }
/*     */   
/*     */ 
/*     */   private static URI checkFileAlias(File file)
/*     */   {
/*     */     try
/*     */     {
/* 153 */       String abs = file.getAbsolutePath();
/* 154 */       String can = file.getCanonicalPath();
/*     */       
/* 156 */       if (!abs.equals(can))
/*     */       {
/* 158 */         if (LOG.isDebugEnabled()) {
/* 159 */           LOG.debug("ALIAS abs={} can={}", new Object[] { abs, can });
/*     */         }
/* 161 */         URI alias = new File(can).toURI();
/*     */         
/* 163 */         String uri = "file://" + URIUtil.encodePath(alias.getPath());
/* 164 */         return new URI(uri);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 169 */       LOG.warn("bad alias for {}: {}", new Object[] { file, e.toString() });
/* 170 */       LOG.debug(e);
/*     */       try
/*     */       {
/* 173 */         return new URI("http://eclipse.org/bad/canonical/alias");
/*     */       }
/*     */       catch (Exception e2)
/*     */       {
/* 177 */         LOG.ignore(e2);
/* 178 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */     
/* 182 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Resource addPath(String path)
/*     */     throws IOException, MalformedURLException
/*     */   {
/* 190 */     assertValidPath(path);
/* 191 */     path = URIUtil.canonicalPath(path);
/*     */     
/* 193 */     if (path == null) {
/* 194 */       throw new MalformedURLException();
/*     */     }
/* 196 */     if ("/".equals(path)) {
/* 197 */       return this;
/*     */     }
/* 199 */     path = URIUtil.encodePath(path);
/*     */     
/*     */     try
/*     */     {
/*     */       URI uri;
/* 204 */       if (this._file.isDirectory())
/*     */       {
/*     */ 
/* 207 */         uri = new URI(URIUtil.addPaths(this._uri, path));
/*     */       }
/*     */       else
/*     */       {
/* 211 */         uri = new URI(this._uri + path);
/*     */       }
/*     */     }
/*     */     catch (URISyntaxException e) {
/*     */       URI uri;
/* 216 */       throw new InvalidPathException(path, e.getMessage()) {};
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     URI uri;
/*     */     
/*     */ 
/* 224 */     return new FileResource(uri);
/*     */   }
/*     */   
/*     */   private void assertValidPath(String path)
/*     */   {
/* 229 */     int idx = StringUtil.indexOfControlChars(path);
/* 230 */     if (idx >= 0)
/*     */     {
/* 232 */       throw new InvalidPathException(path, "Invalid Character at index " + idx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URI getAlias()
/*     */   {
/* 240 */     return this._alias;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 250 */     return this._file.exists();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long lastModified()
/*     */   {
/* 260 */     return this._file.lastModified();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 270 */     return ((this._file.exists()) && (this._file.isDirectory())) || (this._uri.endsWith("/"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long length()
/*     */   {
/* 280 */     return this._file.length();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 291 */     return this._file.getAbsolutePath();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 302 */     return this._file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 312 */     return new FileInputStream(this._file);
/*     */   }
/*     */   
/*     */ 
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 319 */     return FileChannel.open(this._file.toPath(), new OpenOption[] { StandardOpenOption.READ });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean delete()
/*     */     throws SecurityException
/*     */   {
/* 330 */     return this._file.delete();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean renameTo(Resource dest)
/*     */     throws SecurityException
/*     */   {
/* 341 */     if ((dest instanceof FileResource)) {
/* 342 */       return this._file.renameTo(((FileResource)dest)._file);
/*     */     }
/* 344 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] list()
/*     */   {
/* 354 */     String[] list = this._file.list();
/* 355 */     if (list == null)
/* 356 */       return null;
/* 357 */     for (int i = list.length; i-- > 0; 
/*     */         
/*     */ 
/*     */ 
/* 361 */         tmp64_62[tmp64_63] = (tmp64_62[tmp64_63] + "/")) {
/*     */       label17:
/* 359 */       if ((!new File(this._file, list[i]).isDirectory()) || 
/* 360 */         (list[i].endsWith("/")))
/*     */         break label17;
/*     */     }
/* 363 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 374 */     if (this == o) {
/* 375 */       return true;
/*     */     }
/* 377 */     if ((null == o) || (!(o instanceof FileResource))) {
/* 378 */       return false;
/*     */     }
/* 380 */     FileResource f = (FileResource)o;
/* 381 */     return (f._file == this._file) || ((null != this._file) && (this._file.equals(f._file)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 391 */     return null == this._file ? super.hashCode() : this._file.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void copyTo(File destination)
/*     */     throws IOException
/*     */   {
/* 399 */     if (isDirectory())
/*     */     {
/* 401 */       IO.copyDir(getFile(), destination);
/*     */     }
/*     */     else
/*     */     {
/* 405 */       if (destination.exists())
/* 406 */         throw new IllegalArgumentException(destination + " exists");
/* 407 */       IO.copy(getFile(), destination);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isContainedIn(Resource r)
/*     */     throws MalformedURLException
/*     */   {
/* 414 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getURL()
/*     */   {
/*     */     try
/*     */     {
/* 427 */       return new URL(this._uri);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 431 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public URI getURI()
/*     */   {
/* 438 */     return this._file.toURI();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 444 */     return this._uri;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\FileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */