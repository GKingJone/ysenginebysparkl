/*     */ package com.facebook.presto.jdbc.internal.guava.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public final class Resources
/*     */ {
/*     */   public static ByteSource asByteSource(URL url)
/*     */   {
/*  56 */     return new UrlByteSource(url, null);
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class UrlByteSource
/*     */     extends ByteSource
/*     */   {
/*     */     private final URL url;
/*     */     
/*     */     private UrlByteSource(URL url)
/*     */     {
/*  67 */       this.url = ((URL)Preconditions.checkNotNull(url));
/*     */     }
/*     */     
/*     */     public InputStream openStream() throws IOException
/*     */     {
/*  72 */       return this.url.openStream();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/*  77 */       String str = String.valueOf(String.valueOf(this.url));return 24 + str.length() + "Resources.asByteSource(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CharSource asCharSource(URL url, Charset charset)
/*     */   {
/*  88 */     return asByteSource(url).asCharSource(charset);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] toByteArray(URL url)
/*     */     throws IOException
/*     */   {
/*  99 */     return asByteSource(url).read();
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
/*     */   public static String toString(URL url, Charset charset)
/*     */     throws IOException
/*     */   {
/* 113 */     return asCharSource(url, charset).read();
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
/*     */   public static <T> T readLines(URL url, Charset charset, LineProcessor<T> callback)
/*     */     throws IOException
/*     */   {
/* 129 */     return (T)asCharSource(url, charset).readLines(callback);
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
/*     */   public static List<String> readLines(URL url, Charset charset)
/*     */     throws IOException
/*     */   {
/* 151 */     (List)readLines(url, charset, new LineProcessor() {
/* 152 */       final List<String> result = Lists.newArrayList();
/*     */       
/*     */       public boolean processLine(String line)
/*     */       {
/* 156 */         this.result.add(line);
/* 157 */         return true;
/*     */       }
/*     */       
/*     */       public List<String> getResult()
/*     */       {
/* 162 */         return this.result;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void copy(URL from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 175 */     asByteSource(from).copyTo(to);
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
/*     */   public static URL getResource(String resourceName)
/*     */   {
/* 193 */     ClassLoader loader = (ClassLoader)MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(), Resources.class.getClassLoader());
/*     */     
/*     */ 
/* 196 */     URL url = loader.getResource(resourceName);
/* 197 */     Preconditions.checkArgument(url != null, "resource %s not found.", new Object[] { resourceName });
/* 198 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static URL getResource(Class<?> contextClass, String resourceName)
/*     */   {
/* 208 */     URL url = contextClass.getResource(resourceName);
/* 209 */     Preconditions.checkArgument(url != null, "resource %s relative to %s not found.", new Object[] { resourceName, contextClass.getName() });
/*     */     
/* 211 */     return url;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\Resources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */