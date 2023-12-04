/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Uptime
/*     */ {
/*     */   public static final int NOIMPL = -1;
/*     */   
/*     */   public static class DefaultImpl
/*     */     implements Impl
/*     */   {
/*     */     public Object mxBean;
/*     */     public Method uptimeMethod;
/*     */     
/*     */     public DefaultImpl()
/*     */     {
/*  43 */       ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*     */       try
/*     */       {
/*  46 */         Class<?> mgmtFactory = Class.forName("java.lang.management.ManagementFactory", true, cl);
/*  47 */         Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, cl);
/*  48 */         Class<?>[] noparams = new Class[0];
/*  49 */         Method mxBeanMethod = mgmtFactory.getMethod("getRuntimeMXBean", noparams);
/*  50 */         if (mxBeanMethod == null)
/*     */         {
/*  52 */           throw new UnsupportedOperationException("method getRuntimeMXBean() not found");
/*     */         }
/*  54 */         this.mxBean = mxBeanMethod.invoke(mgmtFactory, new Object[0]);
/*  55 */         if (this.mxBean == null)
/*     */         {
/*  57 */           throw new UnsupportedOperationException("getRuntimeMXBean() method returned null");
/*     */         }
/*  59 */         this.uptimeMethod = runtimeClass.getMethod("getUptime", noparams);
/*  60 */         if (this.mxBean == null)
/*     */         {
/*  62 */           throw new UnsupportedOperationException("method getUptime() not found");
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/*     */       catch (ClassNotFoundException|NoClassDefFoundError|NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e)
/*     */       {
/*     */ 
/*     */ 
/*  73 */         throw new UnsupportedOperationException("Implementation not available in this environment", e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public long getUptime()
/*     */     {
/*     */       try
/*     */       {
/*  82 */         return ((Long)this.uptimeMethod.invoke(this.mxBean, new Object[0])).longValue();
/*     */       }
/*     */       catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {}
/*     */       
/*  86 */       return -1L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  91 */   private static final Uptime INSTANCE = new Uptime();
/*     */   private Impl impl;
/*     */   
/*     */   public static Uptime getInstance() {
/*  95 */     return INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Uptime()
/*     */   {
/*     */     try
/*     */     {
/* 104 */       this.impl = new DefaultImpl();
/*     */     }
/*     */     catch (UnsupportedOperationException e)
/*     */     {
/* 108 */       System.err.printf("Defaulting Uptime to NOIMPL due to (%s) %s%n", new Object[] { e.getClass().getName(), e.getMessage() });
/* 109 */       this.impl = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public Impl getImpl()
/*     */   {
/* 115 */     return this.impl;
/*     */   }
/*     */   
/*     */   public void setImpl(Impl impl)
/*     */   {
/* 120 */     this.impl = impl;
/*     */   }
/*     */   
/*     */   public static long getUptime()
/*     */   {
/* 125 */     Uptime u = getInstance();
/* 126 */     if ((u == null) || (u.impl == null))
/*     */     {
/* 128 */       return -1L;
/*     */     }
/* 130 */     return u.impl.getUptime();
/*     */   }
/*     */   
/*     */   public static abstract interface Impl
/*     */   {
/*     */     public abstract long getUptime();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Uptime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */