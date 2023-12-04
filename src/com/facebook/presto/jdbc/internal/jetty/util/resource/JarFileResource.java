/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class JarFileResource
/*     */   extends JarResource
/*     */ {
/*  38 */   private static final Logger LOG = Log.getLogger(JarFileResource.class);
/*     */   
/*     */   private JarFile _jarFile;
/*     */   private File _file;
/*     */   private String[] _list;
/*     */   private JarEntry _entry;
/*     */   private boolean _directory;
/*     */   private String _jarUrl;
/*     */   private String _path;
/*     */   private boolean _exists;
/*     */   
/*     */   protected JarFileResource(URL url)
/*     */   {
/*  51 */     super(url);
/*     */   }
/*     */   
/*     */ 
/*     */   protected JarFileResource(URL url, boolean useCaches)
/*     */   {
/*  57 */     super(url, useCaches);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/*  64 */     this._exists = false;
/*  65 */     this._list = null;
/*  66 */     this._entry = null;
/*  67 */     this._file = null;
/*     */     
/*     */ 
/*  70 */     if (!getUseCaches())
/*     */     {
/*  72 */       if (this._jarFile != null)
/*     */       {
/*     */         try
/*     */         {
/*  76 */           if (LOG.isDebugEnabled())
/*  77 */             LOG.debug("Closing JarFile " + this._jarFile.getName(), new Object[0]);
/*  78 */           this._jarFile.close();
/*     */         }
/*     */         catch (IOException ioe)
/*     */         {
/*  82 */           LOG.ignore(ioe);
/*     */         }
/*     */       }
/*     */     }
/*  86 */     this._jarFile = null;
/*  87 */     super.close();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected synchronized boolean checkConnection()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 95	com/facebook/presto/jdbc/internal/jetty/util/resource/JarResource:checkConnection	()Z
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: getfield 99	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_jarConnection	Ljava/net/JarURLConnection;
/*     */     //   9: ifnonnull +56 -> 65
/*     */     //   12: aload_0
/*     */     //   13: aconst_null
/*     */     //   14: putfield 43	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_entry	Ljava/util/jar/JarEntry;
/*     */     //   17: aload_0
/*     */     //   18: aconst_null
/*     */     //   19: putfield 45	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_file	Ljava/io/File;
/*     */     //   22: aload_0
/*     */     //   23: aconst_null
/*     */     //   24: putfield 51	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_jarFile	Ljava/util/jar/JarFile;
/*     */     //   27: aload_0
/*     */     //   28: aconst_null
/*     */     //   29: putfield 41	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_list	[Ljava/lang/String;
/*     */     //   32: goto +33 -> 65
/*     */     //   35: astore_1
/*     */     //   36: aload_0
/*     */     //   37: getfield 99	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_jarConnection	Ljava/net/JarURLConnection;
/*     */     //   40: ifnonnull +23 -> 63
/*     */     //   43: aload_0
/*     */     //   44: aconst_null
/*     */     //   45: putfield 43	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_entry	Ljava/util/jar/JarEntry;
/*     */     //   48: aload_0
/*     */     //   49: aconst_null
/*     */     //   50: putfield 45	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_file	Ljava/io/File;
/*     */     //   53: aload_0
/*     */     //   54: aconst_null
/*     */     //   55: putfield 51	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_jarFile	Ljava/util/jar/JarFile;
/*     */     //   58: aload_0
/*     */     //   59: aconst_null
/*     */     //   60: putfield 41	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_list	[Ljava/lang/String;
/*     */     //   63: aload_1
/*     */     //   64: athrow
/*     */     //   65: aload_0
/*     */     //   66: getfield 51	com/facebook/presto/jdbc/internal/jetty/util/resource/JarFileResource:_jarFile	Ljava/util/jar/JarFile;
/*     */     //   69: ifnull +7 -> 76
/*     */     //   72: iconst_1
/*     */     //   73: goto +4 -> 77
/*     */     //   76: iconst_0
/*     */     //   77: ireturn
/*     */     // Line number table:
/*     */     //   Java source line #96	-> byte code offset #0
/*     */     //   Java source line #100	-> byte code offset #5
/*     */     //   Java source line #102	-> byte code offset #12
/*     */     //   Java source line #103	-> byte code offset #17
/*     */     //   Java source line #104	-> byte code offset #22
/*     */     //   Java source line #105	-> byte code offset #27
/*     */     //   Java source line #100	-> byte code offset #35
/*     */     //   Java source line #102	-> byte code offset #43
/*     */     //   Java source line #103	-> byte code offset #48
/*     */     //   Java source line #104	-> byte code offset #53
/*     */     //   Java source line #105	-> byte code offset #58
/*     */     //   Java source line #108	-> byte code offset #65
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	78	0	this	JarFileResource
/*     */     //   35	29	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	5	35	finally
/*     */   }
/*     */   
/*     */   protected synchronized void newConnection()
/*     */     throws IOException
/*     */   {
/* 117 */     super.newConnection();
/*     */     
/* 119 */     this._entry = null;
/* 120 */     this._file = null;
/* 121 */     this._jarFile = null;
/* 122 */     this._list = null;
/*     */     
/* 124 */     int sep = this._urlString.indexOf("!/");
/* 125 */     this._jarUrl = this._urlString.substring(0, sep + 2);
/* 126 */     this._path = this._urlString.substring(sep + 2);
/* 127 */     if (this._path.length() == 0)
/* 128 */       this._path = null;
/* 129 */     this._jarFile = this._jarConnection.getJarFile();
/* 130 */     this._file = new File(this._jarFile.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 142 */     if (this._exists) {
/* 143 */       return true;
/*     */     }
/* 145 */     if (this._urlString.endsWith("!/"))
/*     */     {
/* 147 */       String file_url = this._urlString.substring(4, this._urlString.length() - 2);
/* 148 */       try { return newResource(file_url).exists();
/* 149 */       } catch (Exception e) { LOG.ignore(e);return false;
/*     */       }
/*     */     }
/* 152 */     boolean check = checkConnection();
/*     */     
/*     */ 
/* 155 */     if ((this._jarUrl != null) && (this._path == null))
/*     */     {
/*     */ 
/* 158 */       this._directory = check;
/* 159 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 164 */     boolean close_jar_file = false;
/* 165 */     JarFile jar_file = null;
/* 166 */     if (check)
/*     */     {
/* 168 */       jar_file = this._jarFile;
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 174 */         JarURLConnection c = (JarURLConnection)new URL(this._jarUrl).openConnection();
/* 175 */         c.setUseCaches(getUseCaches());
/* 176 */         jar_file = c.getJarFile();
/* 177 */         close_jar_file = !getUseCaches();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 181 */         LOG.ignore(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 186 */     if ((jar_file != null) && (this._entry == null) && (!this._directory))
/*     */     {
/*     */ 
/* 189 */       JarEntry entry = jar_file.getJarEntry(this._path);
/* 190 */       if (entry == null)
/*     */       {
/*     */ 
/* 193 */         this._exists = false;
/*     */       }
/* 195 */       else if (entry.isDirectory())
/*     */       {
/* 197 */         this._directory = true;
/* 198 */         this._entry = entry;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 203 */         JarEntry directory = jar_file.getJarEntry(this._path + '/');
/* 204 */         if (directory != null)
/*     */         {
/* 206 */           this._directory = true;
/* 207 */           this._entry = directory;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 212 */           this._directory = false;
/* 213 */           this._entry = entry;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 218 */     if ((close_jar_file) && (jar_file != null))
/*     */     {
/*     */       try
/*     */       {
/* 222 */         jar_file.close();
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 226 */         LOG.ignore(ioe);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 231 */     this._exists = ((this._directory) || (this._entry != null));
/* 232 */     return this._exists;
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
/*     */   public boolean isDirectory()
/*     */   {
/* 245 */     return (this._urlString.endsWith("/")) || ((exists()) && (this._directory));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long lastModified()
/*     */   {
/* 255 */     if ((checkConnection()) && (this._file != null))
/*     */     {
/* 257 */       if ((exists()) && (this._entry != null))
/* 258 */         return this._entry.getTime();
/* 259 */       return this._file.lastModified();
/*     */     }
/* 261 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized String[] list()
/*     */   {
/* 268 */     if ((isDirectory()) && (this._list == null))
/*     */     {
/* 270 */       List<String> list = null;
/*     */       try
/*     */       {
/* 273 */         list = listEntries();
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 283 */         LOG.warn("Retrying list:" + e, new Object[0]);
/* 284 */         LOG.debug(e);
/* 285 */         close();
/* 286 */         list = listEntries();
/*     */       }
/*     */       
/* 289 */       if (list != null)
/*     */       {
/* 291 */         this._list = new String[list.size()];
/* 292 */         list.toArray(this._list);
/*     */       }
/*     */     }
/* 295 */     return this._list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private List<String> listEntries()
/*     */   {
/* 302 */     checkConnection();
/*     */     
/* 304 */     ArrayList<String> list = new ArrayList(32);
/* 305 */     JarFile jarFile = this._jarFile;
/* 306 */     if (jarFile == null)
/*     */     {
/*     */       try
/*     */       {
/* 310 */         JarURLConnection jc = (JarURLConnection)new URL(this._jarUrl).openConnection();
/* 311 */         jc.setUseCaches(getUseCaches());
/* 312 */         jarFile = jc.getJarFile();
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 317 */         e.printStackTrace();
/* 318 */         LOG.ignore(e);
/*     */       }
/* 320 */       if (jarFile == null) {
/* 321 */         throw new IllegalStateException();
/*     */       }
/*     */     }
/* 324 */     Enumeration<JarEntry> e = jarFile.entries();
/* 325 */     String dir = this._urlString.substring(this._urlString.indexOf("!/") + 2);
/* 326 */     while (e.hasMoreElements())
/*     */     {
/* 328 */       JarEntry entry = (JarEntry)e.nextElement();
/* 329 */       String name = entry.getName().replace('\\', '/');
/* 330 */       if ((name.startsWith(dir)) && (name.length() != dir.length()))
/*     */       {
/*     */ 
/*     */ 
/* 334 */         String listName = name.substring(dir.length());
/* 335 */         int dash = listName.indexOf('/');
/* 336 */         if (dash >= 0)
/*     */         {
/*     */ 
/*     */ 
/* 340 */           if ((dash != 0) || (listName.length() != 1))
/*     */           {
/*     */ 
/*     */ 
/* 344 */             if (dash == 0) {
/* 345 */               listName = listName.substring(dash + 1, listName.length());
/*     */             } else {
/* 347 */               listName = listName.substring(0, dash + 1);
/*     */             }
/* 349 */             if (list.contains(listName)) {}
/*     */           }
/*     */         }
/*     */         else
/* 353 */           list.add(listName);
/*     */       }
/*     */     }
/* 356 */     return list;
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
/*     */   public long length()
/*     */   {
/* 370 */     if (isDirectory()) {
/* 371 */       return -1L;
/*     */     }
/* 373 */     if (this._entry != null) {
/* 374 */       return this._entry.getSize();
/*     */     }
/* 376 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Resource getNonCachingResource(Resource resource)
/*     */   {
/* 388 */     if (!(resource instanceof JarFileResource)) {
/* 389 */       return resource;
/*     */     }
/* 391 */     JarFileResource oldResource = (JarFileResource)resource;
/*     */     
/* 393 */     JarFileResource newResource = new JarFileResource(oldResource.getURL(), false);
/* 394 */     return newResource;
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
/*     */   public boolean isContainedIn(Resource resource)
/*     */     throws MalformedURLException
/*     */   {
/* 409 */     String string = this._urlString;
/* 410 */     int index = string.indexOf("!/");
/* 411 */     if (index > 0)
/* 412 */       string = string.substring(0, index);
/* 413 */     if (string.startsWith("jar:"))
/* 414 */       string = string.substring(4);
/* 415 */     URL url = new URL(string);
/* 416 */     return url.sameFile(resource.getURL());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\JarFileResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */