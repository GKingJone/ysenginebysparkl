/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.B64Code;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IO;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Loader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Resource
/*     */   implements ResourceFactory, Closeable
/*     */ {
/*  57 */   private static final Logger LOG = Log.getLogger(Resource.class);
/*  58 */   public static boolean __defaultUseCaches = true;
/*     */   
/*     */ 
/*     */ 
/*     */   volatile Object _associate;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setDefaultUseCaches(boolean useCaches)
/*     */   {
/*  69 */     __defaultUseCaches = useCaches;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean getDefaultUseCaches()
/*     */   {
/*  75 */     return __defaultUseCaches;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Resource newResource(URI uri)
/*     */     throws MalformedURLException
/*     */   {
/*  87 */     return newResource(uri.toURL());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Resource newResource(URL url)
/*     */   {
/*  97 */     return newResource(url, __defaultUseCaches);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Resource newResource(URL url, boolean useCaches)
/*     */   {
/* 109 */     if (url == null) {
/* 110 */       return null;
/*     */     }
/* 112 */     String url_string = url.toExternalForm();
/* 113 */     if (url_string.startsWith("file:"))
/*     */     {
/*     */       try
/*     */       {
/* 117 */         return new PathResource(url);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 121 */         LOG.warn(e.toString(), new Object[0]);
/* 122 */         LOG.debug("EXCEPTION ", e);
/* 123 */         return new BadResource(url, e.toString());
/*     */       }
/*     */     }
/* 126 */     if (url_string.startsWith("jar:file:"))
/*     */     {
/* 128 */       return new JarFileResource(url, useCaches);
/*     */     }
/* 130 */     if (url_string.startsWith("jar:"))
/*     */     {
/* 132 */       return new JarResource(url, useCaches);
/*     */     }
/*     */     
/* 135 */     return new URLResource(url, null, useCaches);
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
/*     */   public static Resource newResource(String resource)
/*     */     throws MalformedURLException
/*     */   {
/* 149 */     return newResource(resource, __defaultUseCaches);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Resource newResource(String resource, boolean useCaches)
/*     */     throws MalformedURLException
/*     */   {
/* 162 */     URL url = null;
/*     */     
/*     */     try
/*     */     {
/* 166 */       url = new URL(resource);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 170 */       if ((!resource.startsWith("ftp:")) && 
/* 171 */         (!resource.startsWith("file:")) && 
/* 172 */         (!resource.startsWith("jar:")))
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 177 */           if (resource.startsWith("./")) {
/* 178 */             resource = resource.substring(2);
/*     */           }
/* 180 */           File file = new File(resource).getCanonicalFile();
/* 181 */           return new PathResource(file.toPath());
/*     */         }
/*     */         catch (Exception e2)
/*     */         {
/* 185 */           LOG.debug("EXCEPTION ", e2);
/* 186 */           throw e;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 191 */       LOG.warn("Bad Resource: " + resource, new Object[0]);
/* 192 */       throw e;
/*     */     }
/*     */     
/*     */ 
/* 196 */     return newResource(url, useCaches);
/*     */   }
/*     */   
/*     */ 
/*     */   public static Resource newResource(File file)
/*     */   {
/* 202 */     return new PathResource(file.toPath());
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
/*     */   public static Resource newSystemResource(String resource)
/*     */     throws IOException
/*     */   {
/* 216 */     URL url = null;
/*     */     
/* 218 */     ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 219 */     if (loader != null)
/*     */     {
/*     */       try
/*     */       {
/* 223 */         url = loader.getResource(resource);
/* 224 */         if ((url == null) && (resource.startsWith("/"))) {
/* 225 */           url = loader.getResource(resource.substring(1));
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/* 232 */         url = null;
/*     */       }
/*     */     }
/* 235 */     if (url == null)
/*     */     {
/* 237 */       loader = Resource.class.getClassLoader();
/* 238 */       if (loader != null)
/*     */       {
/* 240 */         url = loader.getResource(resource);
/* 241 */         if ((url == null) && (resource.startsWith("/"))) {
/* 242 */           url = loader.getResource(resource.substring(1));
/*     */         }
/*     */       }
/*     */     }
/* 246 */     if (url == null)
/*     */     {
/* 248 */       url = ClassLoader.getSystemResource(resource);
/* 249 */       if ((url == null) && (resource.startsWith("/"))) {
/* 250 */         url = ClassLoader.getSystemResource(resource.substring(1));
/*     */       }
/*     */     }
/* 253 */     if (url == null) {
/* 254 */       return null;
/*     */     }
/* 256 */     return newResource(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Resource newClassPathResource(String resource)
/*     */   {
/* 266 */     return newClassPathResource(resource, true, false);
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
/*     */   public static Resource newClassPathResource(String name, boolean useCaches, boolean checkParents)
/*     */   {
/* 283 */     URL url = Resource.class.getResource(name);
/*     */     
/* 285 */     if (url == null)
/* 286 */       url = Loader.getResource(Resource.class, name);
/* 287 */     if (url == null)
/* 288 */       return null;
/* 289 */     return newResource(url, useCaches);
/*     */   }
/*     */   
/*     */   public static boolean isContainedIn(Resource r, Resource containingResource)
/*     */     throws MalformedURLException
/*     */   {
/* 295 */     return r.isContainedIn(containingResource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 302 */     close();
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract boolean isContainedIn(Resource paramResource)
/*     */     throws MalformedURLException;
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public final void release()
/*     */   {
/* 315 */     close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void close();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean exists();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean isDirectory();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long lastModified();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long length();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public abstract URL getURL();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URI getURI()
/*     */   {
/*     */     try
/*     */     {
/* 377 */       return getURL().toURI();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 381 */       throw new RuntimeException(e);
/*     */     }
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
/*     */   public abstract File getFile()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract InputStream getInputStream()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean delete()
/*     */     throws SecurityException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean renameTo(Resource paramResource)
/*     */     throws SecurityException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String[] list();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Resource addPath(String paramString)
/*     */     throws IOException, MalformedURLException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Resource getResource(String path)
/*     */   {
/*     */     try
/*     */     {
/* 480 */       return addPath(path);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 484 */       LOG.debug(e); }
/* 485 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public String encode(String uri)
/*     */   {
/* 498 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getAssociate()
/*     */   {
/* 506 */     return this._associate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAssociate(Object o)
/*     */   {
/* 514 */     this._associate = o;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAlias()
/*     */   {
/* 523 */     return getAlias() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URI getAlias()
/*     */   {
/* 532 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getListHTML(String base, boolean parent)
/*     */     throws IOException
/*     */   {
/* 545 */     base = URIUtil.canonicalPath(base);
/* 546 */     if ((base == null) || (!isDirectory())) {
/* 547 */       return null;
/*     */     }
/* 549 */     String[] ls = list();
/* 550 */     if (ls == null)
/* 551 */       return null;
/* 552 */     Arrays.sort(ls);
/*     */     
/* 554 */     String decodedBase = URIUtil.decodePath(base);
/* 555 */     String title = "Directory: " + deTag(decodedBase);
/*     */     
/* 557 */     StringBuilder buf = new StringBuilder(4096);
/* 558 */     buf.append("<HTML><HEAD>");
/* 559 */     buf.append("<LINK HREF=\"").append("jetty-dir.css").append("\" REL=\"stylesheet\" TYPE=\"text/css\"/><TITLE>");
/* 560 */     buf.append(title);
/* 561 */     buf.append("</TITLE></HEAD><BODY>\n<H1>");
/* 562 */     buf.append(title);
/* 563 */     buf.append("</H1>\n<TABLE BORDER=0>\n");
/*     */     
/* 565 */     if (parent)
/*     */     {
/* 567 */       buf.append("<TR><TD><A HREF=\"");
/* 568 */       buf.append(URIUtil.addPaths(base, "../"));
/* 569 */       buf.append("\">Parent Directory</A></TD><TD></TD><TD></TD></TR>\n");
/*     */     }
/*     */     
/* 572 */     String encodedBase = hrefEncodeURI(base);
/*     */     
/* 574 */     DateFormat dfmt = DateFormat.getDateTimeInstance(2, 2);
/*     */     
/* 576 */     for (int i = 0; i < ls.length; i++)
/*     */     {
/* 578 */       Resource item = addPath(ls[i]);
/*     */       
/* 580 */       buf.append("\n<TR><TD><A HREF=\"");
/* 581 */       String path = URIUtil.addPaths(encodedBase, URIUtil.encodePath(ls[i]));
/*     */       
/* 583 */       buf.append(path);
/*     */       
/* 585 */       if ((item.isDirectory()) && (!path.endsWith("/"))) {
/* 586 */         buf.append("/");
/*     */       }
/*     */       
/* 589 */       buf.append("\">");
/* 590 */       buf.append(deTag(ls[i]));
/* 591 */       buf.append("&nbsp;");
/* 592 */       buf.append("</A></TD><TD ALIGN=right>");
/* 593 */       buf.append(item.length());
/* 594 */       buf.append(" bytes&nbsp;</TD><TD>");
/* 595 */       buf.append(dfmt.format(new Date(item.lastModified())));
/* 596 */       buf.append("</TD></TR>");
/*     */     }
/* 598 */     buf.append("</TABLE>\n");
/* 599 */     buf.append("</BODY></HTML>\n");
/*     */     
/* 601 */     return buf.toString();
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
/*     */   private static String hrefEncodeURI(String raw)
/*     */   {
/* 616 */     StringBuffer buf = null;
/*     */     
/*     */ 
/* 619 */     for (int i = 0; i < raw.length(); i++)
/*     */     {
/* 621 */       char c = raw.charAt(i);
/* 622 */       switch (c)
/*     */       {
/*     */       case '"': 
/*     */       case '\'': 
/*     */       case '<': 
/*     */       case '>': 
/* 628 */         buf = new StringBuffer(raw.length() << 1);
/*     */         break label83; }
/*     */     }
/*     */     label83:
/* 632 */     if (buf == null) {
/* 633 */       return raw;
/*     */     }
/* 635 */     for (int i = 0; i < raw.length(); i++)
/*     */     {
/* 637 */       char c = raw.charAt(i);
/* 638 */       switch (c)
/*     */       {
/*     */       case '"': 
/* 641 */         buf.append("%22");
/* 642 */         break;
/*     */       case '\'': 
/* 644 */         buf.append("%27");
/* 645 */         break;
/*     */       case '<': 
/* 647 */         buf.append("%3C");
/* 648 */         break;
/*     */       case '>': 
/* 650 */         buf.append("%3E");
/* 651 */         break;
/*     */       default: 
/* 653 */         buf.append(c);
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 658 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static String deTag(String raw)
/*     */   {
/* 663 */     return StringUtil.sanitizeXmlString(raw);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTo(OutputStream out, long start, long count)
/*     */     throws IOException
/*     */   {
/* 676 */     InputStream in = getInputStream();Throwable localThrowable3 = null;
/*     */     try {
/* 678 */       in.skip(start);
/* 679 */       if (count < 0L) {
/* 680 */         IO.copy(in, out);
/*     */       } else {
/* 682 */         IO.copy(in, out, count);
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 676 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 683 */       if (in != null) { if (localThrowable3 != null) try { in.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { in.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyTo(File destination)
/*     */     throws IOException
/*     */   {
/* 698 */     if (destination.exists()) {
/* 699 */       throw new IllegalArgumentException(destination + " exists");
/*     */     }
/* 701 */     OutputStream out = new FileOutputStream(destination);Throwable localThrowable3 = null;
/*     */     try {
/* 703 */       writeTo(out, 0L, -1L);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 701 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     }
/*     */     finally {
/* 704 */       if (out != null) { if (localThrowable3 != null) try { out.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { out.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getWeakETag()
/*     */   {
/* 715 */     return getWeakETag("");
/*     */   }
/*     */   
/*     */   public String getWeakETag(String suffix)
/*     */   {
/*     */     try
/*     */     {
/* 722 */       StringBuilder b = new StringBuilder(32);
/* 723 */       b.append("W/\"");
/*     */       
/* 725 */       String name = getName();
/* 726 */       int length = name.length();
/* 727 */       long lhash = 0L;
/* 728 */       for (int i = 0; i < length; i++) {
/* 729 */         lhash = 31L * lhash + name.charAt(i);
/*     */       }
/* 731 */       B64Code.encode(lastModified() ^ lhash, b);
/* 732 */       B64Code.encode(length() ^ lhash, b);
/* 733 */       b.append(suffix);
/* 734 */       b.append('"');
/* 735 */       return b.toString();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 739 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Collection<Resource> getAllResources()
/*     */   {
/*     */     try
/*     */     {
/* 748 */       ArrayList<Resource> deep = new ArrayList();
/*     */       
/* 750 */       String[] list = list();
/* 751 */       if (list != null)
/*     */       {
/* 753 */         for (String i : list)
/*     */         {
/* 755 */           Resource r = addPath(i);
/* 756 */           if (r.isDirectory()) {
/* 757 */             deep.addAll(r.getAllResources());
/*     */           } else {
/* 759 */             deep.add(r);
/*     */           }
/*     */         }
/*     */       }
/* 763 */       return deep;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 767 */       throw new IllegalStateException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static URL toURL(File file)
/*     */     throws MalformedURLException
/*     */   {
/* 779 */     return file.toURI().toURL();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */