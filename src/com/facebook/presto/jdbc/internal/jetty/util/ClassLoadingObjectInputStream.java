/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassLoadingObjectInputStream
/*     */   extends ObjectInputStream
/*     */ {
/*     */   public ClassLoadingObjectInputStream(InputStream in)
/*     */     throws IOException
/*     */   {
/*  40 */     super(in);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ClassLoadingObjectInputStream()
/*     */     throws IOException
/*     */   {}
/*     */   
/*     */ 
/*     */   public Class<?> resolveClass(ObjectStreamClass cl)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/*  55 */       return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
/*     */     }
/*     */     catch (ClassNotFoundException e) {}
/*     */     
/*  59 */     return super.resolveClass(cl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<?> resolveProxyClass(String[] interfaces)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  68 */     ClassLoader loader = Thread.currentThread().getContextClassLoader();
/*     */     
/*  70 */     ClassLoader nonPublicLoader = null;
/*  71 */     boolean hasNonPublicInterface = false;
/*     */     
/*     */ 
/*  74 */     Class<?>[] classObjs = new Class[interfaces.length];
/*  75 */     for (int i = 0; i < interfaces.length; i++)
/*     */     {
/*  77 */       Class<?> cl = Class.forName(interfaces[i], false, loader);
/*  78 */       if ((cl.getModifiers() & 0x1) == 0)
/*     */       {
/*  80 */         if (hasNonPublicInterface)
/*     */         {
/*  82 */           if (nonPublicLoader != cl.getClassLoader())
/*     */           {
/*  84 */             throw new IllegalAccessError("conflicting non-public interface class loaders");
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/*  90 */           nonPublicLoader = cl.getClassLoader();
/*  91 */           hasNonPublicInterface = true;
/*     */         }
/*     */       }
/*  94 */       classObjs[i] = cl;
/*     */     }
/*     */     try
/*     */     {
/*  98 */       return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : loader, classObjs);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 102 */       throw new ClassNotFoundException(null, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ClassLoadingObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */