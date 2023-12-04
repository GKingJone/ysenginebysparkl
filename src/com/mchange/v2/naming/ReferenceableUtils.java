/*     */ package com.mchange.v2.naming;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ReferenceableUtils
/*     */ {
/*  36 */   static final MLogger logger = MLog.getLogger(ReferenceableUtils.class);
/*     */   
/*     */   static final String REFADDR_VERSION = "version";
/*     */   
/*     */   static final String REFADDR_CLASSNAME = "classname";
/*     */   
/*     */   static final String REFADDR_FACTORY = "factory";
/*     */   
/*     */   static final String REFADDR_FACTORY_CLASS_LOCATION = "factoryClassLocation";
/*     */   
/*     */   static final String REFADDR_SIZE = "size";
/*     */   
/*     */   static final int CURRENT_REF_VERSION = 1;
/*     */   
/*     */ 
/*     */   public static String literalNullToNull(String s)
/*     */   {
/*  53 */     if ((s == null) || ("null".equals(s))) {
/*  54 */       return null;
/*     */     }
/*  56 */     return s;
/*     */   }
/*     */   
/*     */   public static Object referenceToObject(Reference ref, Name name, Context nameCtx, Hashtable env)
/*     */     throws NamingException
/*     */   {
/*     */     try
/*     */     {
/*  64 */       String fClassName = ref.getFactoryClassName();
/*  65 */       String fClassLocation = ref.getFactoryClassLocation();
/*     */       ClassLoader cl;
/*     */       ClassLoader cl;
/*  68 */       if (fClassLocation == null) {
/*  69 */         cl = ClassLoader.getSystemClassLoader();
/*     */       }
/*     */       else {
/*  72 */         URL u = new URL(fClassLocation);
/*  73 */         cl = new URLClassLoader(new URL[] { u }, ClassLoader.getSystemClassLoader());
/*     */       }
/*     */       
/*  76 */       Class fClass = Class.forName(fClassName, true, cl);
/*  77 */       ObjectFactory of = (ObjectFactory)fClass.newInstance();
/*  78 */       return of.getObjectInstance(ref, name, nameCtx, env);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/*  85 */       if (logger.isLoggable(MLevel.FINE)) {
/*  86 */         logger.log(MLevel.FINE, "Could not resolve Reference to Object!", e);
/*     */       }
/*  88 */       NamingException ne = new NamingException("Could not resolve Reference to Object!");
/*  89 */       ne.setRootCause(e);
/*  90 */       throw ne;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static void appendToReference(Reference appendTo, Reference orig)
/*     */     throws NamingException
/*     */   {
/* 102 */     int len = orig.size();
/* 103 */     appendTo.add(new StringRefAddr("version", String.valueOf(1)));
/* 104 */     appendTo.add(new StringRefAddr("classname", orig.getClassName()));
/* 105 */     appendTo.add(new StringRefAddr("factory", orig.getFactoryClassName()));
/* 106 */     appendTo.add(new StringRefAddr("factoryClassLocation", orig.getFactoryClassLocation()));
/*     */     
/* 108 */     appendTo.add(new StringRefAddr("size", String.valueOf(len)));
/* 109 */     for (int i = 0; i < len; i++) {
/* 110 */       appendTo.add(orig.get(i));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static ExtractRec extractNestedReference(Reference extractFrom, int index)
/*     */     throws NamingException
/*     */   {
/*     */     try
/*     */     {
/* 123 */       int version = Integer.parseInt((String)extractFrom.get(index++).getContent());
/* 124 */       if (version == 1)
/*     */       {
/* 126 */         String className = (String)extractFrom.get(index++).getContent();
/* 127 */         String factoryClassName = (String)extractFrom.get(index++).getContent();
/* 128 */         String factoryClassLocation = (String)extractFrom.get(index++).getContent();
/*     */         
/* 130 */         Reference outRef = new Reference(className, factoryClassName, factoryClassLocation);
/*     */         
/*     */ 
/* 133 */         int size = Integer.parseInt((String)extractFrom.get(index++).getContent());
/* 134 */         for (int i = 0; i < size; i++)
/* 135 */           outRef.add(extractFrom.get(index++));
/* 136 */         return new ExtractRec(outRef, index, null);
/*     */       }
/*     */       
/* 139 */       throw new NamingException("Bad version of nested reference!!!");
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NumberFormatException e)
/*     */     {
/*     */ 
/* 146 */       if (logger.isLoggable(MLevel.FINE)) {
/* 147 */         logger.log(MLevel.FINE, "Version or size nested reference was not a number!!!", e);
/*     */       }
/* 149 */       throw new NamingException("Version or size nested reference was not a number!!!");
/*     */     } }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static class ExtractRec { public Reference ref;
/*     */     public int index;
/*     */     
/* 158 */     ExtractRec(Reference x0, int x1, ReferenceableUtils.1 x2) { this(x0, x1); }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private ExtractRec(Reference ref, int index)
/*     */     {
/* 170 */       this.ref = ref;
/* 171 */       this.index = index;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\naming\ReferenceableUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */