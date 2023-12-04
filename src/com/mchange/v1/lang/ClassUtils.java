/*     */ package com.mchange.v1.lang;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ClassUtils
/*     */ {
/*  31 */   static final String[] EMPTY_SA = new String[0];
/*     */   
/*     */   static Map primitivesToClasses;
/*     */   
/*     */   static
/*     */   {
/*  37 */     HashMap tmp = new HashMap();
/*  38 */     tmp.put("boolean", Boolean.TYPE);
/*  39 */     tmp.put("int", Integer.TYPE);
/*  40 */     tmp.put("char", Character.TYPE);
/*  41 */     tmp.put("short", Short.TYPE);
/*  42 */     tmp.put("int", Integer.TYPE);
/*  43 */     tmp.put("long", Long.TYPE);
/*  44 */     tmp.put("float", Float.TYPE);
/*  45 */     tmp.put("double", Double.TYPE);
/*  46 */     tmp.put("void", Void.TYPE);
/*     */     
/*  48 */     primitivesToClasses = Collections.unmodifiableMap(tmp);
/*     */   }
/*     */   
/*     */   public static Set allAssignableFrom(Class type)
/*     */   {
/*  53 */     Set out = new HashSet();
/*     */     
/*     */ 
/*  56 */     for (Class cl = type; cl != null; cl = cl.getSuperclass()) {
/*  57 */       out.add(cl);
/*     */     }
/*     */     
/*  60 */     addSuperInterfacesToSet(type, out);
/*  61 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String simpleClassName(Class cl)
/*     */   {
/*  67 */     int array_level = 0;
/*  68 */     while (cl.isArray())
/*     */     {
/*  70 */       array_level++;
/*  71 */       cl = cl.getComponentType();
/*     */     }
/*  73 */     String scn = simpleClassName(cl.getName());
/*  74 */     if (array_level > 0)
/*     */     {
/*  76 */       StringBuffer sb = new StringBuffer(16);
/*  77 */       sb.append(scn);
/*  78 */       for (int i = 0; i < array_level; i++)
/*  79 */         sb.append("[]");
/*  80 */       return sb.toString();
/*     */     }
/*     */     
/*  83 */     return scn;
/*     */   }
/*     */   
/*     */   private static String simpleClassName(String fqcn)
/*     */   {
/*  88 */     int pkgdot = fqcn.lastIndexOf('.');
/*  89 */     if (pkgdot < 0)
/*  90 */       return fqcn;
/*  91 */     String scn = fqcn.substring(pkgdot + 1);
/*  92 */     if (scn.indexOf('$') >= 0)
/*     */     {
/*  94 */       StringBuffer sb = new StringBuffer(scn);
/*  95 */       int i = 0; for (int len = sb.length(); i < len; i++)
/*     */       {
/*  97 */         if (sb.charAt(i) == '$')
/*  98 */           sb.setCharAt(i, '.');
/*     */       }
/* 100 */       return sb.toString();
/*     */     }
/*     */     
/* 103 */     return scn;
/*     */   }
/*     */   
/*     */   public static boolean isPrimitive(String typeStr) {
/* 107 */     return primitivesToClasses.get(typeStr) != null;
/*     */   }
/*     */   
/* 110 */   public static Class classForPrimitive(String typeStr) { return (Class)primitivesToClasses.get(typeStr); }
/*     */   
/*     */   public static Class forName(String fqcnOrPrimitive) throws ClassNotFoundException
/*     */   {
/* 114 */     Class out = classForPrimitive(fqcnOrPrimitive);
/* 115 */     if (out == null)
/* 116 */       out = Class.forName(fqcnOrPrimitive);
/* 117 */     return out;
/*     */   }
/*     */   
/*     */   public static Class forName(String fqOrSimple, String[] importPkgs, String[] importClasses) throws AmbiguousClassNameException, ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 124 */       return Class.forName(fqOrSimple);
/*     */     } catch (ClassNotFoundException e) {}
/* 126 */     return classForSimpleName(fqOrSimple, importPkgs, importClasses);
/*     */   }
/*     */   
/*     */   public static Class classForSimpleName(String simpleName, String[] importPkgs, String[] importClasses)
/*     */     throws AmbiguousClassNameException, ClassNotFoundException
/*     */   {
/* 132 */     Set checkSet = new HashSet();
/* 133 */     Class out = classForPrimitive(simpleName);
/*     */     
/* 135 */     if (out == null)
/*     */     {
/* 137 */       if (importPkgs == null) {
/* 138 */         importPkgs = EMPTY_SA;
/*     */       }
/* 140 */       if (importClasses == null) {
/* 141 */         importClasses = EMPTY_SA;
/*     */       }
/* 143 */       int i = 0; for (int len = importClasses.length; i < len; i++)
/*     */       {
/* 145 */         String importSimpleName = fqcnLastElement(importClasses[i]);
/* 146 */         if (!checkSet.add(importSimpleName)) {
/* 147 */           throw new IllegalArgumentException("Duplicate imported classes: " + importSimpleName);
/*     */         }
/* 149 */         if (simpleName.equals(importSimpleName))
/*     */         {
/* 151 */           out = Class.forName(importClasses[i]); }
/*     */       }
/* 153 */       if (out == null) {
/*     */         try {
/* 155 */           out = Class.forName("java.lang." + simpleName);
/*     */         }
/*     */         catch (ClassNotFoundException e) {}
/*     */         
/* 159 */         int i = 0; for (int len = importPkgs.length; i < len; i++)
/*     */         {
/*     */           try
/*     */           {
/* 163 */             String tryClass = importPkgs[i] + '.' + simpleName;
/* 164 */             Class test = Class.forName(tryClass);
/* 165 */             if (out == null) {
/* 166 */               out = test;
/*     */             } else {
/* 168 */               throw new AmbiguousClassNameException(simpleName, out, test);
/*     */             }
/*     */           }
/*     */           catch (ClassNotFoundException e) {}
/*     */         }
/*     */       }
/*     */     }
/* 175 */     if (out == null) {
/* 176 */       throw new ClassNotFoundException("Could not find a class whose unqualified name is \"" + simpleName + "\" with the imports supplied. Import packages are " + Arrays.asList(importPkgs) + "; class imports are " + Arrays.asList(importClasses));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 181 */     return out;
/*     */   }
/*     */   
/*     */   public static String resolvableTypeName(Class type, String[] importPkgs, String[] importClasses)
/*     */     throws ClassNotFoundException
/*     */   {
/* 187 */     String simpleName = simpleClassName(type);
/*     */     try {
/* 189 */       classForSimpleName(simpleName, importPkgs, importClasses);
/*     */     } catch (AmbiguousClassNameException e) {
/* 191 */       return type.getName(); }
/* 192 */     return simpleName;
/*     */   }
/*     */   
/*     */   public static String fqcnLastElement(String fqcn)
/*     */   {
/* 197 */     int pkgdot = fqcn.lastIndexOf('.');
/* 198 */     if (pkgdot < 0)
/* 199 */       return fqcn;
/* 200 */     return fqcn.substring(pkgdot + 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void addSuperInterfacesToSet(Class type, Set set)
/*     */   {
/* 207 */     Class[] ifaces = type.getInterfaces();
/* 208 */     int i = 0; for (int len = ifaces.length; i < len; i++)
/*     */     {
/* 210 */       set.add(ifaces[i]);
/* 211 */       addSuperInterfacesToSet(ifaces[i], set);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\lang\ClassUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */