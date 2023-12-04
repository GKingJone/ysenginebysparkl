/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryIteratorException;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.InvalidPathException;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileTime;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class PathResource
/*     */   extends Resource
/*     */ {
/*  52 */   private static final Logger LOG = Log.getLogger(PathResource.class);
/*  53 */   private static final LinkOption[] NO_FOLLOW_LINKS = { LinkOption.NOFOLLOW_LINKS };
/*  54 */   private static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
/*     */   
/*     */   private final Path path;
/*     */   private final Path alias;
/*     */   private final URI uri;
/*     */   
/*     */   private static final Path checkAliasPath(Path path)
/*     */   {
/*  62 */     Path abs = path;
/*  63 */     if (!abs.isAbsolute())
/*     */     {
/*  65 */       abs = path.toAbsolutePath();
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  70 */       if (Files.isSymbolicLink(path))
/*  71 */         return Files.readSymbolicLink(path);
/*  72 */       if (Files.exists(path, new LinkOption[0]))
/*     */       {
/*  74 */         Path real = abs.toRealPath(FOLLOW_LINKS);
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
/* 110 */         int absCount = abs.getNameCount();
/* 111 */         int realCount = real.getNameCount();
/* 112 */         if (absCount != realCount)
/*     */         {
/*     */ 
/* 115 */           return real;
/*     */         }
/*     */         
/*     */ 
/* 119 */         for (int i = realCount - 1; i >= 0; i--)
/*     */         {
/* 121 */           if (!abs.getName(i).toString().equals(real.getName(i).toString()))
/*     */           {
/* 123 */             return real;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 130 */       LOG.ignore(e);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 134 */       LOG.warn("bad alias ({} {}) for {}", new Object[] { e.getClass().getName(), e.getMessage(), path });
/*     */     }
/* 136 */     return null;
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
/*     */   public PathResource(File file)
/*     */   {
/* 158 */     this(file.toPath());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PathResource(Path path)
/*     */   {
/* 168 */     this.path = path.toAbsolutePath();
/* 169 */     assertValidPath(path);
/* 170 */     this.uri = this.path.toUri();
/* 171 */     this.alias = checkAliasPath(path);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PathResource(URI uri)
/*     */     throws IOException
/*     */   {
/* 184 */     if (!uri.isAbsolute())
/*     */     {
/* 186 */       throw new IllegalArgumentException("not an absolute uri");
/*     */     }
/*     */     
/* 189 */     if (!uri.getScheme().equalsIgnoreCase("file"))
/*     */     {
/* 191 */       throw new IllegalArgumentException("not file: scheme");
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 197 */       path = new File(uri).toPath();
/*     */     }
/*     */     catch (InvalidPathException e) {
/*     */       Path path;
/* 201 */       throw e;
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 205 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 209 */       LOG.ignore(e);
/* 210 */       throw new IOException("Unable to build Path from: " + uri, e);
/*     */     }
/*     */     Path path;
/* 213 */     this.path = path.toAbsolutePath();
/* 214 */     this.uri = path.toUri();
/* 215 */     this.alias = checkAliasPath(path);
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
/*     */   public PathResource(URL url)
/*     */     throws IOException, URISyntaxException
/*     */   {
/* 239 */     this(url.toURI());
/*     */   }
/*     */   
/*     */   public Resource addPath(String subpath)
/*     */     throws IOException, MalformedURLException
/*     */   {
/* 245 */     String cpath = URIUtil.canonicalPath(subpath);
/*     */     
/* 247 */     if ((cpath == null) || (cpath.length() == 0)) {
/* 248 */       throw new MalformedURLException();
/*     */     }
/* 250 */     if ("/".equals(cpath)) {
/* 251 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 257 */     return new PathResource(this.path.getFileSystem().getPath(this.path.toString(), new String[] { subpath }));
/*     */   }
/*     */   
/*     */ 
/*     */   private void assertValidPath(Path path)
/*     */   {
/* 263 */     String str = path.toString();
/* 264 */     int idx = StringUtil.indexOfControlChars(str);
/* 265 */     if (idx >= 0)
/*     */     {
/* 267 */       throw new InvalidPathException(str, "Invalid Character at index " + idx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean delete()
/*     */     throws SecurityException
/*     */   {
/*     */     try
/*     */     {
/* 282 */       return Files.deleteIfExists(this.path);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 286 */       LOG.ignore(e); }
/* 287 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 294 */     if (this == obj)
/*     */     {
/* 296 */       return true;
/*     */     }
/* 298 */     if (obj == null)
/*     */     {
/* 300 */       return false;
/*     */     }
/* 302 */     if (getClass() != obj.getClass())
/*     */     {
/* 304 */       return false;
/*     */     }
/* 306 */     PathResource other = (PathResource)obj;
/* 307 */     if (this.path == null)
/*     */     {
/* 309 */       if (other.path != null)
/*     */       {
/* 311 */         return false;
/*     */       }
/*     */     }
/* 314 */     else if (!this.path.equals(other.path))
/*     */     {
/* 316 */       return false;
/*     */     }
/* 318 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 324 */     return Files.exists(this.path, NO_FOLLOW_LINKS);
/*     */   }
/*     */   
/*     */   public File getFile()
/*     */     throws IOException
/*     */   {
/* 330 */     return this.path.toFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Path getPath()
/*     */   {
/* 338 */     return this.path;
/*     */   }
/*     */   
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 344 */     return Files.newInputStream(this.path, new OpenOption[] { StandardOpenOption.READ });
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 350 */     return this.path.toAbsolutePath().toString();
/*     */   }
/*     */   
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 356 */     return FileChannel.open(this.path, new OpenOption[] { StandardOpenOption.READ });
/*     */   }
/*     */   
/*     */ 
/*     */   public URI getURI()
/*     */   {
/* 362 */     return this.uri;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/*     */     try
/*     */     {
/* 370 */       return this.path.toUri().toURL();
/*     */     }
/*     */     catch (MalformedURLException e) {}
/*     */     
/* 374 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 381 */     int prime = 31;
/* 382 */     int result = 1;
/* 383 */     result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
/* 384 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isContainedIn(Resource r)
/*     */     throws MalformedURLException
/*     */   {
/* 391 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 397 */     return Files.isDirectory(this.path, FOLLOW_LINKS);
/*     */   }
/*     */   
/*     */ 
/*     */   public long lastModified()
/*     */   {
/*     */     try
/*     */     {
/* 405 */       FileTime ft = Files.getLastModifiedTime(this.path, FOLLOW_LINKS);
/* 406 */       return ft.toMillis();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 410 */       LOG.ignore(e); }
/* 411 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long length()
/*     */   {
/*     */     try
/*     */     {
/* 420 */       return Files.size(this.path);
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/*     */ 
/* 425 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAlias()
/*     */   {
/* 432 */     return this.alias != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Path getAliasPath()
/*     */   {
/* 442 */     return this.alias;
/*     */   }
/*     */   
/*     */ 
/*     */   public URI getAlias()
/*     */   {
/* 448 */     return this.alias == null ? null : this.alias.toUri();
/*     */   }
/*     */   
/*     */   public String[] list()
/*     */   {
/*     */     try {
/* 454 */       DirectoryStream<Path> dir = Files.newDirectoryStream(this.path);Throwable localThrowable3 = null;
/*     */       try {
/* 456 */         List<String> entries = new ArrayList();
/* 457 */         for (Iterator localIterator = dir.iterator(); localIterator.hasNext();) { entry = (Path)localIterator.next();
/*     */           
/* 459 */           String name = entry.getFileName().toString();
/*     */           
/* 461 */           if (Files.isDirectory(entry, new LinkOption[0]))
/*     */           {
/* 463 */             name = name + "/";
/*     */           }
/*     */           
/* 466 */           entries.add(name); }
/*     */         Path entry;
/* 468 */         int size = entries.size();
/* 469 */         return (String[])entries.toArray(new String[size]);
/*     */       }
/*     */       catch (Throwable localThrowable1)
/*     */       {
/* 454 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 470 */         if (dir != null) { if (localThrowable3 != null) try { dir.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { dir.close();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 479 */       return null;
/*     */     }
/*     */     catch (DirectoryIteratorException e)
/*     */     {
/* 473 */       LOG.debug(e);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 477 */       LOG.debug(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean renameTo(Resource dest)
/*     */     throws SecurityException
/*     */   {
/* 485 */     if ((dest instanceof PathResource))
/*     */     {
/* 487 */       PathResource destRes = (PathResource)dest;
/*     */       try
/*     */       {
/* 490 */         Path result = Files.move(this.path, destRes.path, new CopyOption[0]);
/* 491 */         return Files.exists(result, NO_FOLLOW_LINKS);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 495 */         LOG.ignore(e);
/* 496 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 501 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void copyTo(File destination)
/*     */     throws IOException
/*     */   {
/* 508 */     if (isDirectory())
/*     */     {
/* 510 */       IO.copyDir(this.path.toFile(), destination);
/*     */     }
/*     */     else
/*     */     {
/* 514 */       Files.copy(this.path, destination.toPath(), new CopyOption[0]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 521 */     return this.uri.toASCIIString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\PathResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */