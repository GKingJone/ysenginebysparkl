/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import java.io.Closeable;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FinalizableReferenceQueue
/*     */   implements Closeable
/*     */ {
/* 131 */   private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
/*     */   private static final String FINALIZER_CLASS_NAME = "com.facebook.presto.jdbc.internal.guava.base.internal.Finalizer";
/*     */   private static final Method startFinalizer;
/*     */   final ReferenceQueue<Object> queue;
/*     */   final PhantomReference<Object> frqRef;
/*     */   final boolean threadStarted;
/*     */   
/* 138 */   static { Class<?> finalizer = loadFinalizer(new FinalizerLoader[] { new SystemLoader(), new DecoupledLoader(), new DirectLoader() });
/*     */     
/* 140 */     startFinalizer = getStartFinalizer(finalizer);
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
/*     */   public FinalizableReferenceQueue()
/*     */   {
/* 160 */     this.queue = new ReferenceQueue();
/* 161 */     this.frqRef = new PhantomReference(this, this.queue);
/* 162 */     boolean threadStarted = false;
/*     */     try {
/* 164 */       startFinalizer.invoke(null, new Object[] { FinalizableReference.class, this.queue, this.frqRef });
/* 165 */       threadStarted = true;
/*     */     } catch (IllegalAccessException impossible) {
/* 167 */       throw new AssertionError(impossible);
/*     */     } catch (Throwable t) {
/* 169 */       logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", t);
/*     */     }
/*     */     
/*     */ 
/* 173 */     this.threadStarted = threadStarted;
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/* 178 */     this.frqRef.enqueue();
/* 179 */     cleanUp();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void cleanUp()
/*     */   {
/* 188 */     if (this.threadStarted) {
/*     */       return;
/*     */     }
/*     */     
/*     */     Reference<?> reference;
/* 193 */     while ((reference = this.queue.poll()) != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 198 */       reference.clear();
/*     */       try {
/* 200 */         ((FinalizableReference)reference).finalizeReferent();
/*     */       } catch (Throwable t) {
/* 202 */         logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Class<?> loadFinalizer(FinalizerLoader... loaders)
/*     */   {
/* 213 */     for (FinalizerLoader loader : loaders) {
/* 214 */       Class<?> finalizer = loader.loadFinalizer();
/* 215 */       if (finalizer != null) {
/* 216 */         return finalizer;
/*     */       }
/*     */     }
/*     */     
/* 220 */     throw new AssertionError();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static abstract interface FinalizerLoader
/*     */   {
/*     */     @Nullable
/*     */     public abstract Class<?> loadFinalizer();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static class SystemLoader
/*     */     implements FinalizerLoader
/*     */   {
/*     */     @VisibleForTesting
/*     */     static boolean disabled;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Class<?> loadFinalizer()
/*     */     {
/* 249 */       if (disabled) {
/* 250 */         return null;
/*     */       }
/*     */       ClassLoader systemLoader;
/*     */       try {
/* 254 */         systemLoader = ClassLoader.getSystemClassLoader();
/*     */       } catch (SecurityException e) {
/* 256 */         FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
/* 257 */         return null;
/*     */       }
/* 259 */       if (systemLoader != null) {
/*     */         try {
/* 261 */           return systemLoader.loadClass("com.facebook.presto.jdbc.internal.guava.base.internal.Finalizer");
/*     */         }
/*     */         catch (ClassNotFoundException e) {
/* 264 */           return null;
/*     */         }
/*     */       }
/* 267 */       return null;
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
/*     */   static class DecoupledLoader
/*     */     implements FinalizerLoader
/*     */   {
/*     */     private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Class<?> loadFinalizer()
/*     */     {
/*     */       try
/*     */       {
/* 295 */         ClassLoader finalizerLoader = newLoader(getBaseUrl());
/* 296 */         return finalizerLoader.loadClass("com.facebook.presto.jdbc.internal.guava.base.internal.Finalizer");
/*     */       } catch (Exception e) {
/* 298 */         FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.", e); }
/* 299 */       return null;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     URL getBaseUrl()
/*     */       throws java.io.IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: ldc 35
/*     */       //   2: bipush 46
/*     */       //   4: bipush 47
/*     */       //   6: invokevirtual 69	java/lang/String:replace	(CC)Ljava/lang/String;
/*     */       //   9: invokestatic 73	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   12: ldc 75
/*     */       //   14: invokevirtual 79	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   17: astore_1
/*     */       //   18: aload_0
/*     */       //   19: invokevirtual 82	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */       //   22: invokevirtual 88	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
/*     */       //   25: aload_1
/*     */       //   26: invokevirtual 92	java/lang/ClassLoader:getResource	(Ljava/lang/String;)Ljava/net/URL;
/*     */       //   29: astore_2
/*     */       //   30: aload_2
/*     */       //   31: ifnonnull +12 -> 43
/*     */       //   34: new 94	java/io/FileNotFoundException
/*     */       //   37: dup
/*     */       //   38: aload_1
/*     */       //   39: invokespecial 97	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
/*     */       //   42: athrow
/*     */       //   43: aload_2
/*     */       //   44: invokevirtual 103	java/net/URL:toString	()Ljava/lang/String;
/*     */       //   47: astore_3
/*     */       //   48: aload_3
/*     */       //   49: aload_1
/*     */       //   50: invokevirtual 107	java/lang/String:endsWith	(Ljava/lang/String;)Z
/*     */       //   53: ifne +39 -> 92
/*     */       //   56: new 63	java/io/IOException
/*     */       //   59: dup
/*     */       //   60: ldc 109
/*     */       //   62: aload_3
/*     */       //   63: invokestatic 73	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   66: dup
/*     */       //   67: invokevirtual 113	java/lang/String:length	()I
/*     */       //   70: ifeq +9 -> 79
/*     */       //   73: invokevirtual 79	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   76: goto +12 -> 88
/*     */       //   79: pop
/*     */       //   80: new 65	java/lang/String
/*     */       //   83: dup_x1
/*     */       //   84: swap
/*     */       //   85: invokespecial 114	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */       //   88: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*     */       //   91: athrow
/*     */       //   92: aload_3
/*     */       //   93: iconst_0
/*     */       //   94: aload_3
/*     */       //   95: invokevirtual 113	java/lang/String:length	()I
/*     */       //   98: aload_1
/*     */       //   99: invokevirtual 113	java/lang/String:length	()I
/*     */       //   102: isub
/*     */       //   103: invokevirtual 119	java/lang/String:substring	(II)Ljava/lang/String;
/*     */       //   106: astore_3
/*     */       //   107: new 99	java/net/URL
/*     */       //   110: dup
/*     */       //   111: aload_2
/*     */       //   112: aload_3
/*     */       //   113: invokespecial 122	java/net/URL:<init>	(Ljava/net/URL;Ljava/lang/String;)V
/*     */       //   116: areturn
/*     */       // Line number table:
/*     */       //   Java source line #308	-> byte code offset #0
/*     */       //   Java source line #309	-> byte code offset #18
/*     */       //   Java source line #310	-> byte code offset #30
/*     */       //   Java source line #311	-> byte code offset #34
/*     */       //   Java source line #315	-> byte code offset #43
/*     */       //   Java source line #316	-> byte code offset #48
/*     */       //   Java source line #317	-> byte code offset #56
/*     */       //   Java source line #319	-> byte code offset #92
/*     */       //   Java source line #320	-> byte code offset #107
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	117	0	this	DecoupledLoader
/*     */       //   18	99	1	finalizerPath	String
/*     */       //   30	87	2	finalizerUrl	URL
/*     */       //   48	69	3	urlString	String
/*     */     }
/*     */     
/*     */     URLClassLoader newLoader(URL base)
/*     */     {
/* 328 */       return new URLClassLoader(new URL[] { base }, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static class DirectLoader
/*     */     implements FinalizerLoader
/*     */   {
/*     */     public Class<?> loadFinalizer()
/*     */     {
/*     */       try
/*     */       {
/* 340 */         return Class.forName("com.facebook.presto.jdbc.internal.guava.base.internal.Finalizer");
/*     */       } catch (ClassNotFoundException e) {
/* 342 */         throw new AssertionError(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static Method getStartFinalizer(Class<?> finalizer)
/*     */   {
/*     */     try
/*     */     {
/* 352 */       return finalizer.getMethod("startFinalizer", new Class[] { Class.class, ReferenceQueue.class, PhantomReference.class });
/*     */ 
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */ 
/* 358 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\FinalizableReferenceQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */