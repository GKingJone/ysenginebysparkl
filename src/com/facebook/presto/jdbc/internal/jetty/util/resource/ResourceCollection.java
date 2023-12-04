/*     */ package com.facebook.presto.jdbc.internal.jetty.util.resource;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResourceCollection
/*     */   extends Resource
/*     */ {
/*  49 */   private static final Logger LOG = Log.getLogger(ResourceCollection.class);
/*     */   
/*     */ 
/*     */ 
/*     */   private Resource[] _resources;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceCollection()
/*     */   {
/*  60 */     this._resources = new Resource[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceCollection(Resource... resources)
/*     */   {
/*  71 */     List<Resource> list = new ArrayList();
/*  72 */     for (Resource r : resources)
/*     */     {
/*  74 */       if (r != null)
/*     */       {
/*  76 */         if ((r instanceof ResourceCollection))
/*     */         {
/*  78 */           for (Resource r2 : ((ResourceCollection)r).getResources()) {
/*  79 */             list.add(r2);
/*     */           }
/*     */         } else
/*  82 */           list.add(r); }
/*     */     }
/*  84 */     this._resources = ((Resource[])list.toArray(new Resource[list.size()]));
/*  85 */     for (Resource r : this._resources)
/*     */     {
/*  87 */       if ((!r.exists()) || (!r.isDirectory())) {
/*  88 */         throw new IllegalArgumentException(r + " is not an existing directory.");
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
/*     */   public ResourceCollection(String[] resources)
/*     */   {
/* 101 */     this._resources = new Resource[resources.length];
/*     */     try
/*     */     {
/* 104 */       for (int i = 0; i < resources.length; i++)
/*     */       {
/* 106 */         this._resources[i] = Resource.newResource(resources[i]);
/* 107 */         if ((!this._resources[i].exists()) || (!this._resources[i].isDirectory())) {
/* 108 */           throw new IllegalArgumentException(this._resources[i] + " is not an existing directory.");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 113 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 117 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceCollection(String csvResources)
/*     */   {
/* 129 */     setResourcesAsCSV(csvResources);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Resource[] getResources()
/*     */   {
/* 140 */     return this._resources;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setResources(Resource[] resources)
/*     */   {
/* 151 */     this._resources = (resources != null ? resources : new Resource[0]);
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
/*     */   public void setResourcesAsCSV(String csvResources)
/*     */   {
/* 164 */     StringTokenizer tokenizer = new StringTokenizer(csvResources, ",;");
/* 165 */     int len = tokenizer.countTokens();
/* 166 */     if (len == 0)
/*     */     {
/* 168 */       throw new IllegalArgumentException("ResourceCollection@setResourcesAsCSV(String)  argument must be a string containing one or more comma-separated resource strings.");
/*     */     }
/*     */     
/*     */ 
/* 172 */     List<Resource> resources = new ArrayList();
/*     */     
/*     */     try
/*     */     {
/* 176 */       while (tokenizer.hasMoreTokens())
/*     */       {
/* 178 */         Resource resource = Resource.newResource(tokenizer.nextToken().trim());
/* 179 */         if ((!resource.exists()) || (!resource.isDirectory())) {
/* 180 */           LOG.warn(" !exist " + resource, new Object[0]);
/*     */         } else {
/* 182 */           resources.add(resource);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 187 */       throw new RuntimeException(e);
/*     */     }
/*     */     
/* 190 */     this._resources = ((Resource[])resources.toArray(new Resource[resources.size()]));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Resource addPath(String path)
/*     */     throws IOException, MalformedURLException
/*     */   {
/* 201 */     if (this._resources == null) {
/* 202 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 204 */     if (path == null) {
/* 205 */       throw new MalformedURLException();
/*     */     }
/* 207 */     if ((path.length() == 0) || ("/".equals(path))) {
/* 208 */       return this;
/*     */     }
/* 210 */     Resource resource = null;
/* 211 */     ArrayList<Resource> resources = null;
/* 212 */     for (int i = 0; 
/* 213 */         i < this._resources.length; i++)
/*     */     {
/* 215 */       resource = this._resources[i].addPath(path);
/* 216 */       if (resource.exists())
/*     */       {
/* 218 */         if (resource.isDirectory())
/*     */           break;
/* 220 */         return resource;
/*     */       }
/*     */     }
/*     */     
/* 224 */     for (i++; i < this._resources.length; i++)
/*     */     {
/* 226 */       Resource r = this._resources[i].addPath(path);
/* 227 */       if ((r.exists()) && (r.isDirectory()))
/*     */       {
/* 229 */         if (resources == null) {
/* 230 */           resources = new ArrayList();
/*     */         }
/* 232 */         if (resource != null)
/*     */         {
/* 234 */           resources.add(resource);
/* 235 */           resource = null;
/*     */         }
/*     */         
/* 238 */         resources.add(r);
/*     */       }
/*     */     }
/*     */     
/* 242 */     if (resource != null)
/* 243 */       return resource;
/* 244 */     if (resources != null)
/* 245 */       return new ResourceCollection((Resource[])resources.toArray(new Resource[resources.size()]));
/* 246 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object findResource(String path)
/*     */     throws IOException, MalformedURLException
/*     */   {
/* 258 */     Resource resource = null;
/* 259 */     ArrayList<Resource> resources = null;
/* 260 */     for (int i = 0; 
/* 261 */         i < this._resources.length; i++)
/*     */     {
/* 263 */       resource = this._resources[i].addPath(path);
/* 264 */       if (resource.exists())
/*     */       {
/* 266 */         if (resource.isDirectory()) {
/*     */           break;
/*     */         }
/* 269 */         return resource;
/*     */       }
/*     */     }
/*     */     
/* 273 */     for (i++; i < this._resources.length; i++)
/*     */     {
/* 275 */       Resource r = this._resources[i].addPath(path);
/* 276 */       if ((r.exists()) && (r.isDirectory()))
/*     */       {
/* 278 */         if (resource != null)
/*     */         {
/* 280 */           resources = new ArrayList();
/* 281 */           resources.add(resource);
/*     */         }
/* 283 */         resources.add(r);
/*     */       }
/*     */     }
/*     */     
/* 287 */     if (resource != null)
/* 288 */       return resource;
/* 289 */     if (resources != null)
/* 290 */       return resources;
/* 291 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean delete()
/*     */     throws SecurityException
/*     */   {
/* 298 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean exists()
/*     */   {
/* 305 */     if (this._resources == null) {
/* 306 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 308 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */     throws IOException
/*     */   {
/* 315 */     if (this._resources == null) {
/* 316 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 318 */     for (Resource r : this._resources)
/*     */     {
/* 320 */       File f = r.getFile();
/* 321 */       if (f != null)
/* 322 */         return f;
/*     */     }
/* 324 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 331 */     if (this._resources == null) {
/* 332 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 334 */     for (Resource r : this._resources)
/*     */     {
/* 336 */       InputStream is = r.getInputStream();
/* 337 */       if (is != null)
/* 338 */         return is;
/*     */     }
/* 340 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public ReadableByteChannel getReadableByteChannel()
/*     */     throws IOException
/*     */   {
/* 347 */     if (this._resources == null) {
/* 348 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 350 */     for (Resource r : this._resources)
/*     */     {
/* 352 */       ReadableByteChannel channel = r.getReadableByteChannel();
/* 353 */       if (channel != null)
/* 354 */         return channel;
/*     */     }
/* 356 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 363 */     if (this._resources == null) {
/* 364 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 366 */     for (Resource r : this._resources)
/*     */     {
/* 368 */       String name = r.getName();
/* 369 */       if (name != null)
/* 370 */         return name;
/*     */     }
/* 372 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 379 */     if (this._resources == null) {
/* 380 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 382 */     for (Resource r : this._resources)
/*     */     {
/* 384 */       URL url = r.getURL();
/* 385 */       if (url != null)
/* 386 */         return url;
/*     */     }
/* 388 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDirectory()
/*     */   {
/* 395 */     if (this._resources == null) {
/* 396 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 398 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long lastModified()
/*     */   {
/* 405 */     if (this._resources == null) {
/* 406 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 408 */     for (Resource r : this._resources)
/*     */     {
/* 410 */       long lm = r.lastModified();
/* 411 */       if (lm != -1L)
/* 412 */         return lm;
/*     */     }
/* 414 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long length()
/*     */   {
/* 421 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] list()
/*     */   {
/* 431 */     if (this._resources == null) {
/* 432 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 434 */     HashSet<String> set = new HashSet();
/* 435 */     for (Resource r : this._resources)
/*     */     {
/* 437 */       for (String s : r.list())
/* 438 */         set.add(s);
/*     */     }
/* 440 */     String[] result = (String[])set.toArray(new String[set.size()]);
/* 441 */     Arrays.sort(result);
/* 442 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 449 */     if (this._resources == null) {
/* 450 */       throw new IllegalStateException("*resources* not set.");
/*     */     }
/* 452 */     for (Resource r : this._resources) {
/* 453 */       r.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean renameTo(Resource dest)
/*     */     throws SecurityException
/*     */   {
/* 460 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void copyTo(File destination)
/*     */     throws IOException
/*     */   {
/* 468 */     for (int r = this._resources.length; r-- > 0;) {
/* 469 */       this._resources[r].copyTo(destination);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 479 */     if (this._resources == null) {
/* 480 */       return "[]";
/*     */     }
/* 482 */     return String.valueOf(Arrays.asList(this._resources));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isContainedIn(Resource r)
/*     */     throws MalformedURLException
/*     */   {
/* 490 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\resource\ResourceCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */