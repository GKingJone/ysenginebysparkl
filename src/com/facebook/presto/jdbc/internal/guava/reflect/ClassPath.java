/*     */ package com.facebook.presto.jdbc.internal.guava.reflect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.CharMatcher;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Splitter;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.FluentIterable;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet.Builder;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedSet.Builder;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Ordering;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Sets;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class ClassPath
/*     */ {
/*  60 */   private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
/*     */   
/*  62 */   private static final Predicate<ClassInfo> IS_TOP_LEVEL = new Predicate() {
/*     */     public boolean apply(ClassInfo info) {
/*  64 */       return info.className.indexOf('$') == -1;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*  69 */   private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
/*     */   
/*     */   private static final String CLASS_FILE_NAME_EXTENSION = ".class";
/*     */   
/*     */   private final ImmutableSet<ResourceInfo> resources;
/*     */   
/*     */   private ClassPath(ImmutableSet<ResourceInfo> resources)
/*     */   {
/*  77 */     this.resources = resources;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassPath from(ClassLoader classloader)
/*     */     throws IOException
/*     */   {
/*  90 */     Scanner scanner = new Scanner();
/*  91 */     for (Map.Entry<URI, ClassLoader> entry : getClassPathEntries(classloader).entrySet()) {
/*  92 */       scanner.scan((URI)entry.getKey(), (ClassLoader)entry.getValue());
/*     */     }
/*  94 */     return new ClassPath(scanner.getResources());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableSet<ResourceInfo> getResources()
/*     */   {
/* 102 */     return this.resources;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableSet<ClassInfo> getAllClasses()
/*     */   {
/* 111 */     return FluentIterable.from(this.resources).filter(ClassInfo.class).toSet();
/*     */   }
/*     */   
/*     */   public ImmutableSet<ClassInfo> getTopLevelClasses()
/*     */   {
/* 116 */     return FluentIterable.from(this.resources).filter(ClassInfo.class).filter(IS_TOP_LEVEL).toSet();
/*     */   }
/*     */   
/*     */   public ImmutableSet<ClassInfo> getTopLevelClasses(String packageName)
/*     */   {
/* 121 */     Preconditions.checkNotNull(packageName);
/* 122 */     ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
/* 123 */     for (ClassInfo classInfo : getTopLevelClasses()) {
/* 124 */       if (classInfo.getPackageName().equals(packageName)) {
/* 125 */         builder.add(classInfo);
/*     */       }
/*     */     }
/* 128 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableSet<ClassInfo> getTopLevelClassesRecursive(String packageName)
/*     */   {
/* 136 */     Preconditions.checkNotNull(packageName);
/* 137 */     String str1 = String.valueOf(String.valueOf(packageName));String packagePrefix = 1 + str1.length() + str1 + ".";
/* 138 */     ImmutableSet.Builder<ClassInfo> builder = ImmutableSet.builder();
/* 139 */     for (ClassInfo classInfo : getTopLevelClasses()) {
/* 140 */       if (classInfo.getName().startsWith(packagePrefix)) {
/* 141 */         builder.add(classInfo);
/*     */       }
/*     */     }
/* 144 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static class ResourceInfo
/*     */   {
/*     */     private final String resourceName;
/*     */     
/*     */     final ClassLoader loader;
/*     */     
/*     */ 
/*     */     static ResourceInfo of(String resourceName, ClassLoader loader)
/*     */     {
/* 159 */       if (resourceName.endsWith(".class")) {
/* 160 */         return new ClassInfo(resourceName, loader);
/*     */       }
/* 162 */       return new ResourceInfo(resourceName, loader);
/*     */     }
/*     */     
/*     */     ResourceInfo(String resourceName, ClassLoader loader)
/*     */     {
/* 167 */       this.resourceName = ((String)Preconditions.checkNotNull(resourceName));
/* 168 */       this.loader = ((ClassLoader)Preconditions.checkNotNull(loader));
/*     */     }
/*     */     
/*     */     public final URL url()
/*     */     {
/* 173 */       return (URL)Preconditions.checkNotNull(this.loader.getResource(this.resourceName), "Failed to load resource: %s", new Object[] { this.resourceName });
/*     */     }
/*     */     
/*     */ 
/*     */     public final String getResourceName()
/*     */     {
/* 179 */       return this.resourceName;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 183 */       return this.resourceName.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 187 */       if ((obj instanceof ResourceInfo)) {
/* 188 */         ResourceInfo that = (ResourceInfo)obj;
/* 189 */         return (this.resourceName.equals(that.resourceName)) && (this.loader == that.loader);
/*     */       }
/*     */       
/* 192 */       return false;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 197 */       return this.resourceName;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @Beta
/*     */   public static final class ClassInfo
/*     */     extends ResourceInfo
/*     */   {
/*     */     private final String className;
/*     */     
/*     */ 
/*     */     ClassInfo(String resourceName, ClassLoader loader)
/*     */     {
/* 211 */       super(loader);
/* 212 */       this.className = ClassPath.getClassName(resourceName);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getPackageName()
/*     */     {
/* 222 */       return Reflection.getPackageName(this.className);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getSimpleName()
/*     */     {
/* 232 */       int lastDollarSign = this.className.lastIndexOf('$');
/* 233 */       if (lastDollarSign != -1) {
/* 234 */         String innerClassName = this.className.substring(lastDollarSign + 1);
/*     */         
/*     */ 
/* 237 */         return CharMatcher.DIGIT.trimLeadingFrom(innerClassName);
/*     */       }
/* 239 */       String packageName = getPackageName();
/* 240 */       if (packageName.isEmpty()) {
/* 241 */         return this.className;
/*     */       }
/*     */       
/*     */ 
/* 245 */       return this.className.substring(packageName.length() + 1);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getName()
/*     */     {
/* 255 */       return this.className;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Class<?> load()
/*     */     {
/*     */       try
/*     */       {
/* 266 */         return this.loader.loadClass(this.className);
/*     */       }
/*     */       catch (ClassNotFoundException e) {
/* 269 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public String toString() {
/* 274 */       return this.className;
/*     */     }
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static ImmutableMap<URI, ClassLoader> getClassPathEntries(ClassLoader classloader) {
/* 280 */     LinkedHashMap<URI, ClassLoader> entries = Maps.newLinkedHashMap();
/*     */     
/* 282 */     ClassLoader parent = classloader.getParent();
/* 283 */     if (parent != null) {
/* 284 */       entries.putAll(getClassPathEntries(parent));
/*     */     }
/* 286 */     if ((classloader instanceof URLClassLoader)) {
/* 287 */       URLClassLoader urlClassLoader = (URLClassLoader)classloader;
/* 288 */       for (URL entry : urlClassLoader.getURLs()) {
/*     */         URI uri;
/*     */         try {
/* 291 */           uri = entry.toURI();
/*     */         } catch (URISyntaxException e) {
/* 293 */           throw new IllegalArgumentException(e);
/*     */         }
/* 295 */         if (!entries.containsKey(uri)) {
/* 296 */           entries.put(uri, classloader);
/*     */         }
/*     */       }
/*     */     }
/* 300 */     return ImmutableMap.copyOf(entries);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static final class Scanner {
/* 305 */     private final ImmutableSortedSet.Builder<ResourceInfo> resources = new ImmutableSortedSet.Builder(Ordering.usingToString());
/*     */     
/* 307 */     private final Set<URI> scannedUris = Sets.newHashSet();
/*     */     
/*     */     ImmutableSortedSet<ResourceInfo> getResources() {
/* 310 */       return this.resources.build();
/*     */     }
/*     */     
/*     */     void scan(URI uri, ClassLoader classloader) throws IOException {
/* 314 */       if ((uri.getScheme().equals("file")) && (this.scannedUris.add(uri))) {
/* 315 */         scanFrom(new File(uri), classloader);
/*     */       }
/*     */     }
/*     */     
/*     */     @VisibleForTesting
/*     */     void scanFrom(File file, ClassLoader classloader) throws IOException {
/* 321 */       if (!file.exists()) {
/* 322 */         return;
/*     */       }
/* 324 */       if (file.isDirectory()) {
/* 325 */         scanDirectory(file, classloader);
/*     */       } else {
/* 327 */         scanJar(file, classloader);
/*     */       }
/*     */     }
/*     */     
/*     */     private void scanDirectory(File directory, ClassLoader classloader) throws IOException {
/* 332 */       scanDirectory(directory, classloader, "", ImmutableSet.of());
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     private void scanDirectory(File directory, ClassLoader classloader, String packagePrefix, ImmutableSet<File> ancestors)
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_1
/*     */       //   1: invokevirtual 126	java/io/File:getCanonicalFile	()Ljava/io/File;
/*     */       //   4: astore 5
/*     */       //   6: aload 4
/*     */       //   8: aload 5
/*     */       //   10: invokevirtual 129	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet:contains	(Ljava/lang/Object;)Z
/*     */       //   13: ifeq +4 -> 17
/*     */       //   16: return
/*     */       //   17: aload_1
/*     */       //   18: invokevirtual 133	java/io/File:listFiles	()[Ljava/io/File;
/*     */       //   21: astore 6
/*     */       //   23: aload 6
/*     */       //   25: ifnonnull +47 -> 72
/*     */       //   28: invokestatic 137	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath:access$100	()Ljava/util/logging/Logger;
/*     */       //   31: aload_1
/*     */       //   32: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   35: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   38: astore 7
/*     */       //   40: new 143	java/lang/StringBuilder
/*     */       //   43: dup
/*     */       //   44: bipush 22
/*     */       //   46: aload 7
/*     */       //   48: invokevirtual 147	java/lang/String:length	()I
/*     */       //   51: iadd
/*     */       //   52: invokespecial 150	java/lang/StringBuilder:<init>	(I)V
/*     */       //   55: ldc -104
/*     */       //   57: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   60: aload 7
/*     */       //   62: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   65: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   68: invokevirtual 165	java/util/logging/Logger:warning	(Ljava/lang/String;)V
/*     */       //   71: return
/*     */       //   72: invokestatic 171	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet:builder	()Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder;
/*     */       //   75: aload 4
/*     */       //   77: invokevirtual 175	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder:addAll	(Ljava/lang/Iterable;)Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder;
/*     */       //   80: aload 5
/*     */       //   82: invokevirtual 178	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder:add	(Ljava/lang/Object;)Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder;
/*     */       //   85: invokevirtual 180	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder:build	()Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet;
/*     */       //   88: astore 7
/*     */       //   90: aload 6
/*     */       //   92: astore 8
/*     */       //   94: aload 8
/*     */       //   96: arraylength
/*     */       //   97: istore 9
/*     */       //   99: iconst_0
/*     */       //   100: istore 10
/*     */       //   102: iload 10
/*     */       //   104: iload 9
/*     */       //   106: if_icmpge +157 -> 263
/*     */       //   109: aload 8
/*     */       //   111: iload 10
/*     */       //   113: aaload
/*     */       //   114: astore 11
/*     */       //   116: aload 11
/*     */       //   118: invokevirtual 185	java/io/File:getName	()Ljava/lang/String;
/*     */       //   121: astore 12
/*     */       //   123: aload 11
/*     */       //   125: invokevirtual 105	java/io/File:isDirectory	()Z
/*     */       //   128: ifeq +72 -> 200
/*     */       //   131: aload_0
/*     */       //   132: aload 11
/*     */       //   134: aload_2
/*     */       //   135: aload_3
/*     */       //   136: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   139: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   142: astore 13
/*     */       //   144: aload 12
/*     */       //   146: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   149: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   152: astore 14
/*     */       //   154: new 143	java/lang/StringBuilder
/*     */       //   157: dup
/*     */       //   158: iconst_1
/*     */       //   159: aload 13
/*     */       //   161: invokevirtual 147	java/lang/String:length	()I
/*     */       //   164: iadd
/*     */       //   165: aload 14
/*     */       //   167: invokevirtual 147	java/lang/String:length	()I
/*     */       //   170: iadd
/*     */       //   171: invokespecial 150	java/lang/StringBuilder:<init>	(I)V
/*     */       //   174: aload 13
/*     */       //   176: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   179: aload 14
/*     */       //   181: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   184: ldc -69
/*     */       //   186: invokevirtual 156	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */       //   189: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */       //   192: aload 7
/*     */       //   194: invokespecial 121	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath$Scanner:scanDirectory	(Ljava/io/File;Ljava/lang/ClassLoader;Ljava/lang/String;Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet;)V
/*     */       //   197: goto +60 -> 257
/*     */       //   200: aload_3
/*     */       //   201: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   204: aload 12
/*     */       //   206: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   209: dup
/*     */       //   210: invokevirtual 147	java/lang/String:length	()I
/*     */       //   213: ifeq +9 -> 222
/*     */       //   216: invokevirtual 191	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   219: goto +12 -> 231
/*     */       //   222: pop
/*     */       //   223: new 76	java/lang/String
/*     */       //   226: dup_x1
/*     */       //   227: swap
/*     */       //   228: invokespecial 193	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */       //   231: astore 13
/*     */       //   233: aload 13
/*     */       //   235: ldc -61
/*     */       //   237: invokevirtual 80	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */       //   240: ifne +17 -> 257
/*     */       //   243: aload_0
/*     */       //   244: getfield 47	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath$Scanner:resources	Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSortedSet$Builder;
/*     */       //   247: aload 13
/*     */       //   249: aload_2
/*     */       //   250: invokestatic 198	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath$ResourceInfo:of	(Ljava/lang/String;Ljava/lang/ClassLoader;)Lcom/facebook/presto/jdbc/internal/guava/reflect/ClassPath$ResourceInfo;
/*     */       //   253: invokevirtual 201	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSortedSet$Builder:add	(Ljava/lang/Object;)Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSortedSet$Builder;
/*     */       //   256: pop
/*     */       //   257: iinc 10 1
/*     */       //   260: goto -158 -> 102
/*     */       //   263: return
/*     */       // Line number table:
/*     */       //   Java source line #338	-> byte code offset #0
/*     */       //   Java source line #339	-> byte code offset #6
/*     */       //   Java source line #341	-> byte code offset #16
/*     */       //   Java source line #343	-> byte code offset #17
/*     */       //   Java source line #344	-> byte code offset #23
/*     */       //   Java source line #345	-> byte code offset #28
/*     */       //   Java source line #347	-> byte code offset #71
/*     */       //   Java source line #349	-> byte code offset #72
/*     */       //   Java source line #353	-> byte code offset #90
/*     */       //   Java source line #354	-> byte code offset #116
/*     */       //   Java source line #355	-> byte code offset #123
/*     */       //   Java source line #356	-> byte code offset #131
/*     */       //   Java source line #357	-> byte code offset #197
/*     */       //   Java source line #358	-> byte code offset #200
/*     */       //   Java source line #359	-> byte code offset #233
/*     */       //   Java source line #360	-> byte code offset #243
/*     */       //   Java source line #353	-> byte code offset #257
/*     */       //   Java source line #364	-> byte code offset #263
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	264	0	this	Scanner
/*     */       //   0	264	1	directory	File
/*     */       //   0	264	2	classloader	ClassLoader
/*     */       //   0	264	3	packagePrefix	String
/*     */       //   0	264	4	ancestors	ImmutableSet<File>
/*     */       //   6	258	5	canonical	File
/*     */       //   23	241	6	files	File[]
/*     */       //   90	174	7	newAncestors	ImmutableSet<File>
/*     */       //   94	169	8	arr$	File[]
/*     */       //   99	164	9	len$	int
/*     */       //   102	161	10	i$	int
/*     */       //   116	141	11	f	File
/*     */       //   123	134	12	name	String
/*     */       //   233	24	13	resourceName	String
/*     */     }
/*     */     
/*     */     private void scanJar(File file, ClassLoader classloader)
/*     */       throws IOException
/*     */     {
/*     */       JarFile jarFile;
/*     */       try
/*     */       {
/* 369 */         jarFile = new JarFile(file);
/*     */       }
/*     */       catch (IOException e) {
/* 372 */         return;
/*     */       }
/*     */       try {
/* 375 */         for (URI uri : getClassPathFromManifest(file, jarFile.getManifest())) {
/* 376 */           scan(uri, classloader);
/*     */         }
/* 378 */         Enumeration<JarEntry> entries = jarFile.entries();
/* 379 */         while (entries.hasMoreElements()) {
/* 380 */           JarEntry entry = (JarEntry)entries.nextElement();
/* 381 */           if ((!entry.isDirectory()) && (!entry.getName().equals("META-INF/MANIFEST.MF")))
/*     */           {
/*     */ 
/* 384 */             this.resources.add(ResourceInfo.of(entry.getName(), classloader)); }
/*     */         }
/*     */         return;
/*     */       } finally {
/* 388 */         try { jarFile.close();
/*     */         }
/*     */         catch (IOException ignored) {}
/*     */       }
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     @VisibleForTesting
/*     */     static ImmutableSet<URI> getClassPathFromManifest(File jarFile, @javax.annotation.Nullable java.util.jar.Manifest manifest)
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_1
/*     */       //   1: ifnonnull +7 -> 8
/*     */       //   4: invokestatic 118	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet:of	()Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet;
/*     */       //   7: areturn
/*     */       //   8: invokestatic 171	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet:builder	()Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder;
/*     */       //   11: astore_2
/*     */       //   12: aload_1
/*     */       //   13: invokevirtual 284	java/util/jar/Manifest:getMainAttributes	()Ljava/util/jar/Attributes;
/*     */       //   16: getstatic 288	java/util/jar/Attributes$Name:CLASS_PATH	Ljava/util/jar/Attributes$Name;
/*     */       //   19: invokevirtual 289	java/util/jar/Attributes$Name:toString	()Ljava/lang/String;
/*     */       //   22: invokevirtual 292	java/util/jar/Attributes:getValue	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   25: astore_3
/*     */       //   26: aload_3
/*     */       //   27: ifnull +101 -> 128
/*     */       //   30: invokestatic 296	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath:access$200	()Lcom/facebook/presto/jdbc/internal/guava/base/Splitter;
/*     */       //   33: aload_3
/*     */       //   34: invokevirtual 302	com/facebook/presto/jdbc/internal/guava/base/Splitter:split	(Ljava/lang/CharSequence;)Ljava/lang/Iterable;
/*     */       //   37: invokeinterface 305 1 0
/*     */       //   42: astore 4
/*     */       //   44: aload 4
/*     */       //   46: invokeinterface 238 1 0
/*     */       //   51: ifeq +77 -> 128
/*     */       //   54: aload 4
/*     */       //   56: invokeinterface 242 1 0
/*     */       //   61: checkcast 76	java/lang/String
/*     */       //   64: astore 5
/*     */       //   66: aload_0
/*     */       //   67: aload 5
/*     */       //   69: invokestatic 309	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath$Scanner:getClassPathEntry	(Ljava/io/File;Ljava/lang/String;)Ljava/net/URI;
/*     */       //   72: astore 6
/*     */       //   74: goto +44 -> 118
/*     */       //   77: astore 7
/*     */       //   79: invokestatic 137	com/facebook/presto/jdbc/internal/guava/reflect/ClassPath:access$100	()Ljava/util/logging/Logger;
/*     */       //   82: ldc_w 311
/*     */       //   85: aload 5
/*     */       //   87: invokestatic 141	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   90: dup
/*     */       //   91: invokevirtual 147	java/lang/String:length	()I
/*     */       //   94: ifeq +9 -> 103
/*     */       //   97: invokevirtual 191	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   100: goto +12 -> 112
/*     */       //   103: pop
/*     */       //   104: new 76	java/lang/String
/*     */       //   107: dup_x1
/*     */       //   108: swap
/*     */       //   109: invokespecial 193	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */       //   112: invokevirtual 165	java/util/logging/Logger:warning	(Ljava/lang/String;)V
/*     */       //   115: goto -71 -> 44
/*     */       //   118: aload_2
/*     */       //   119: aload 6
/*     */       //   121: invokevirtual 178	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder:add	(Ljava/lang/Object;)Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder;
/*     */       //   124: pop
/*     */       //   125: goto -81 -> 44
/*     */       //   128: aload_2
/*     */       //   129: invokevirtual 180	com/facebook/presto/jdbc/internal/guava/collect/ImmutableSet$Builder:build	()Lcom/facebook/presto/jdbc/internal/guava/collect/ImmutableSet;
/*     */       //   132: areturn
/*     */       // Line number table:
/*     */       //   Java source line #401	-> byte code offset #0
/*     */       //   Java source line #402	-> byte code offset #4
/*     */       //   Java source line #404	-> byte code offset #8
/*     */       //   Java source line #405	-> byte code offset #12
/*     */       //   Java source line #407	-> byte code offset #26
/*     */       //   Java source line #408	-> byte code offset #30
/*     */       //   Java source line #411	-> byte code offset #66
/*     */       //   Java source line #416	-> byte code offset #74
/*     */       //   Java source line #412	-> byte code offset #77
/*     */       //   Java source line #414	-> byte code offset #79
/*     */       //   Java source line #415	-> byte code offset #115
/*     */       //   Java source line #417	-> byte code offset #118
/*     */       //   Java source line #418	-> byte code offset #125
/*     */       //   Java source line #420	-> byte code offset #128
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	133	0	jarFile	File
/*     */       //   0	133	1	manifest	java.util.jar.Manifest
/*     */       //   12	121	2	builder	ImmutableSet.Builder<URI>
/*     */       //   26	107	3	classpathAttribute	String
/*     */       //   44	84	4	i$	java.util.Iterator
/*     */       //   66	59	5	path	String
/*     */       //   74	51	6	uri	URI
/*     */       //   79	39	7	e	URISyntaxException
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   66	74	77	java/net/URISyntaxException
/*     */     }
/*     */     
/*     */     @VisibleForTesting
/*     */     static URI getClassPathEntry(File jarFile, String path)
/*     */       throws URISyntaxException
/*     */     {
/* 431 */       URI uri = new URI(path);
/* 432 */       if (uri.isAbsolute()) {
/* 433 */         return uri;
/*     */       }
/* 435 */       return new File(jarFile.getParentFile(), path.replace('/', File.separatorChar)).toURI();
/*     */     }
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static String getClassName(String filename) {
/* 441 */     int classNameEnd = filename.length() - ".class".length();
/* 442 */     return filename.substring(0, classNameEnd).replace('/', '.');
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\reflect\ClassPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */