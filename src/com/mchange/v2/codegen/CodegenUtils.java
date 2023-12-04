/*     */ package com.mchange.v2.codegen;
/*     */ 
/*     */ import com.mchange.v1.lang.ClassUtils;
/*     */ import java.io.File;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CodegenUtils
/*     */ {
/*     */   public static String getModifierString(int modifiers)
/*     */   {
/*  35 */     StringBuffer sb = new StringBuffer(32);
/*  36 */     if (Modifier.isPublic(modifiers))
/*  37 */       sb.append("public ");
/*  38 */     if (Modifier.isProtected(modifiers))
/*  39 */       sb.append("protected ");
/*  40 */     if (Modifier.isPrivate(modifiers))
/*  41 */       sb.append("private ");
/*  42 */     if (Modifier.isAbstract(modifiers))
/*  43 */       sb.append("abstract ");
/*  44 */     if (Modifier.isStatic(modifiers))
/*  45 */       sb.append("static ");
/*  46 */     if (Modifier.isFinal(modifiers))
/*  47 */       sb.append("final ");
/*  48 */     if (Modifier.isSynchronized(modifiers))
/*  49 */       sb.append("synchronized ");
/*  50 */     if (Modifier.isTransient(modifiers))
/*  51 */       sb.append("transient ");
/*  52 */     if (Modifier.isVolatile(modifiers))
/*  53 */       sb.append("volatile ");
/*  54 */     if (Modifier.isStrict(modifiers))
/*  55 */       sb.append("strictfp ");
/*  56 */     if (Modifier.isNative(modifiers))
/*  57 */       sb.append("native ");
/*  58 */     if (Modifier.isInterface(modifiers))
/*  59 */       sb.append("interface ");
/*  60 */     return sb.toString().trim();
/*     */   }
/*     */   
/*     */   public static Class unarrayClass(Class cl)
/*     */   {
/*  65 */     Class out = cl;
/*  66 */     while (out.isArray())
/*  67 */       out = out.getComponentType();
/*  68 */     return out;
/*     */   }
/*     */   
/*     */   public static boolean inSamePackage(String cn1, String cn2)
/*     */   {
/*  73 */     int pkgdot = cn1.lastIndexOf('.');
/*  74 */     int pkgdot2 = cn2.lastIndexOf('.');
/*     */     
/*     */ 
/*  77 */     if ((pkgdot < 0) || (pkgdot2 < 0))
/*  78 */       return true;
/*  79 */     if (cn1.substring(0, pkgdot).equals(cn1.substring(0, pkgdot)))
/*     */     {
/*  81 */       if (cn2.indexOf('.') >= 0) {
/*  82 */         return false;
/*     */       }
/*  84 */       return true;
/*     */     }
/*     */     
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String fqcnLastElement(String fqcn)
/*     */   {
/*  94 */     return ClassUtils.fqcnLastElement(fqcn);
/*     */   }
/*     */   
/*  97 */   public static String methodSignature(Method m) { return methodSignature(m, null); }
/*     */   
/*     */   public static String methodSignature(Method m, String[] argNames) {
/* 100 */     return methodSignature(1, m, argNames);
/*     */   }
/*     */   
/*     */   public static String methodSignature(int modifiers, Method m, String[] argNames) {
/* 104 */     StringBuffer sb = new StringBuffer(256);
/* 105 */     sb.append(getModifierString(modifiers));
/* 106 */     sb.append(' ');
/* 107 */     sb.append(ClassUtils.simpleClassName(m.getReturnType()));
/* 108 */     sb.append(' ');
/* 109 */     sb.append(m.getName());
/* 110 */     sb.append('(');
/* 111 */     Class[] cls = m.getParameterTypes();
/* 112 */     int i = 0; for (int len = cls.length; i < len; i++)
/*     */     {
/* 114 */       if (i != 0)
/* 115 */         sb.append(", ");
/* 116 */       sb.append(ClassUtils.simpleClassName(cls[i]));
/* 117 */       sb.append(' ');
/* 118 */       sb.append(argNames == null ? String.valueOf((char)(97 + i)) : argNames[i]);
/*     */     }
/* 120 */     sb.append(')');
/* 121 */     Class[] excClasses = m.getExceptionTypes();
/* 122 */     if (excClasses.length > 0)
/*     */     {
/* 124 */       sb.append(" throws ");
/* 125 */       int i = 0; for (int len = excClasses.length; i < len; i++)
/*     */       {
/* 127 */         if (i != 0)
/* 128 */           sb.append(", ");
/* 129 */         sb.append(ClassUtils.simpleClassName(excClasses[i]));
/*     */       }
/*     */     }
/* 132 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String methodCall(Method m) {
/* 136 */     return methodCall(m, null);
/*     */   }
/*     */   
/*     */   public static String methodCall(Method m, String[] argNames) {
/* 140 */     StringBuffer sb = new StringBuffer(256);
/* 141 */     sb.append(m.getName());
/* 142 */     sb.append('(');
/* 143 */     Class[] cls = m.getParameterTypes();
/* 144 */     int i = 0; for (int len = cls.length; i < len; i++)
/*     */     {
/* 146 */       if (i != 0)
/* 147 */         sb.append(", ");
/* 148 */       sb.append(argNames == null ? generatedArgumentName(i) : argNames[i]);
/*     */     }
/* 150 */     sb.append(')');
/* 151 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static String generatedArgumentName(int index) {
/* 155 */     return String.valueOf((char)(97 + index));
/*     */   }
/*     */   
/* 158 */   public static String simpleClassName(Class cl) { return ClassUtils.simpleClassName(cl); }
/*     */   
/*     */   public static IndentedWriter toIndentedWriter(Writer w) {
/* 161 */     return (w instanceof IndentedWriter) ? (IndentedWriter)w : new IndentedWriter(w);
/*     */   }
/*     */   
/*     */   public static String packageNameToFileSystemDirPath(String packageName) {
/* 165 */     StringBuffer sb = new StringBuffer(packageName);
/* 166 */     int i = 0; for (int len = sb.length(); i < len; i++)
/* 167 */       if (sb.charAt(i) == '.')
/* 168 */         sb.setCharAt(i, File.separatorChar);
/* 169 */     sb.append(File.separatorChar);
/* 170 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\CodegenUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */