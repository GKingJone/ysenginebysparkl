/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
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
/*     */ public class IntrospectionUtil
/*     */ {
/*     */   public static boolean isJavaBeanCompliantSetter(Method method)
/*     */   {
/*  38 */     if (method == null) {
/*  39 */       return false;
/*     */     }
/*  41 */     if (method.getReturnType() != Void.TYPE) {
/*  42 */       return false;
/*     */     }
/*  44 */     if (!method.getName().startsWith("set")) {
/*  45 */       return false;
/*     */     }
/*  47 */     if (method.getParameterTypes().length != 1) {
/*  48 */       return false;
/*     */     }
/*  50 */     return true;
/*     */   }
/*     */   
/*     */   public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] args, boolean checkInheritance, boolean strictArgs)
/*     */     throws NoSuchMethodException
/*     */   {
/*  56 */     if (clazz == null)
/*  57 */       throw new NoSuchMethodException("No class");
/*  58 */     if ((methodName == null) || (methodName.trim().equals(""))) {
/*  59 */       throw new NoSuchMethodException("No method name");
/*     */     }
/*  61 */     Method method = null;
/*  62 */     Method[] methods = clazz.getDeclaredMethods();
/*  63 */     for (int i = 0; (i < methods.length) && (method == null); i++)
/*     */     {
/*  65 */       if (methods[i].getName().equals(methodName)) { if (checkParams(methods[i].getParameterTypes(), args == null ? new Class[0] : args, strictArgs))
/*     */         {
/*  67 */           method = methods[i];
/*     */         }
/*     */       }
/*     */     }
/*  71 */     if (method != null)
/*     */     {
/*  73 */       return method;
/*     */     }
/*  75 */     if (checkInheritance) {
/*  76 */       return findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
/*     */     }
/*  78 */     throw new NoSuchMethodException("No such method " + methodName + " on class " + clazz.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Field findField(Class<?> clazz, String targetName, Class<?> targetType, boolean checkInheritance, boolean strictType)
/*     */     throws NoSuchFieldException
/*     */   {
/*  89 */     if (clazz == null)
/*  90 */       throw new NoSuchFieldException("No class");
/*  91 */     if (targetName == null) {
/*  92 */       throw new NoSuchFieldException("No field name");
/*     */     }
/*     */     try
/*     */     {
/*  96 */       Field field = clazz.getDeclaredField(targetName);
/*  97 */       if (strictType)
/*     */       {
/*  99 */         if (field.getType().equals(targetType)) {
/* 100 */           return field;
/*     */         }
/*     */         
/*     */       }
/* 104 */       else if (field.getType().isAssignableFrom(targetType)) {
/* 105 */         return field;
/*     */       }
/* 107 */       if (checkInheritance)
/*     */       {
/* 109 */         return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
/*     */       }
/*     */       
/* 112 */       throw new NoSuchFieldException("No field with name " + targetName + " in class " + clazz.getName() + " of type " + targetType);
/*     */     }
/*     */     catch (NoSuchFieldException e) {}
/*     */     
/* 116 */     return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), targetName, targetType, strictType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isInheritable(Package pack, Member member)
/*     */   {
/* 126 */     if (pack == null)
/* 127 */       return false;
/* 128 */     if (member == null) {
/* 129 */       return false;
/*     */     }
/* 131 */     int modifiers = member.getModifiers();
/* 132 */     if (Modifier.isPublic(modifiers))
/* 133 */       return true;
/* 134 */     if (Modifier.isProtected(modifiers))
/* 135 */       return true;
/* 136 */     if ((!Modifier.isPrivate(modifiers)) && (pack.equals(member.getDeclaringClass().getPackage()))) {
/* 137 */       return true;
/*     */     }
/* 139 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean checkParams(Class<?>[] formalParams, Class<?>[] actualParams, boolean strict)
/*     */   {
/* 147 */     if (formalParams == null)
/* 148 */       return actualParams == null;
/* 149 */     if (actualParams == null) {
/* 150 */       return false;
/*     */     }
/* 152 */     if (formalParams.length != actualParams.length) {
/* 153 */       return false;
/*     */     }
/* 155 */     if (formalParams.length == 0) {
/* 156 */       return true;
/*     */     }
/* 158 */     int j = 0;
/* 159 */     if (strict)
/*     */     {
/* 161 */       while ((j < formalParams.length) && (formalParams[j].equals(actualParams[j]))) {
/* 162 */         j++;
/*     */       }
/*     */     }
/*     */     
/* 166 */     while ((j < formalParams.length) && (formalParams[j].isAssignableFrom(actualParams[j])))
/*     */     {
/* 168 */       j++;
/*     */     }
/*     */     
/*     */ 
/* 172 */     if (j != formalParams.length)
/*     */     {
/* 174 */       return false;
/*     */     }
/*     */     
/* 177 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isSameSignature(Method methodA, Method methodB)
/*     */   {
/* 183 */     if (methodA == null)
/* 184 */       return false;
/* 185 */     if (methodB == null) {
/* 186 */       return false;
/*     */     }
/* 188 */     List<Class<?>> parameterTypesA = Arrays.asList(methodA.getParameterTypes());
/* 189 */     List<Class<?>> parameterTypesB = Arrays.asList(methodB.getParameterTypes());
/*     */     
/* 191 */     if (methodA.getName().equals(methodB.getName()))
/*     */     {
/* 193 */       if (parameterTypesA.containsAll(parameterTypesB))
/* 194 */         return true;
/*     */     }
/* 196 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isTypeCompatible(Class<?> formalType, Class<?> actualType, boolean strict)
/*     */   {
/* 201 */     if (formalType == null)
/* 202 */       return actualType == null;
/* 203 */     if (actualType == null) {
/* 204 */       return false;
/*     */     }
/* 206 */     if (strict) {
/* 207 */       return formalType.equals(actualType);
/*     */     }
/* 209 */     return formalType.isAssignableFrom(actualType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean containsSameMethodSignature(Method method, Class<?> c, boolean checkPackage)
/*     */   {
/* 217 */     if (checkPackage)
/*     */     {
/* 219 */       if (!c.getPackage().equals(method.getDeclaringClass().getPackage())) {
/* 220 */         return false;
/*     */       }
/*     */     }
/* 223 */     boolean samesig = false;
/* 224 */     Method[] methods = c.getDeclaredMethods();
/* 225 */     for (int i = 0; (i < methods.length) && (!samesig); i++)
/*     */     {
/* 227 */       if (isSameSignature(method, methods[i]))
/* 228 */         samesig = true;
/*     */     }
/* 230 */     return samesig;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean containsSameFieldName(Field field, Class<?> c, boolean checkPackage)
/*     */   {
/* 236 */     if (checkPackage)
/*     */     {
/* 238 */       if (!c.getPackage().equals(field.getDeclaringClass().getPackage())) {
/* 239 */         return false;
/*     */       }
/*     */     }
/* 242 */     boolean sameName = false;
/* 243 */     Field[] fields = c.getDeclaredFields();
/* 244 */     for (int i = 0; (i < fields.length) && (!sameName); i++)
/*     */     {
/* 246 */       if (fields[i].getName().equals(field.getName()))
/* 247 */         sameName = true;
/*     */     }
/* 249 */     return sameName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static Method findInheritedMethod(Package pack, Class<?> clazz, String methodName, Class<?>[] args, boolean strictArgs)
/*     */     throws NoSuchMethodException
/*     */   {
/* 257 */     if (clazz == null)
/* 258 */       throw new NoSuchMethodException("No class");
/* 259 */     if (methodName == null) {
/* 260 */       throw new NoSuchMethodException("No method name");
/*     */     }
/* 262 */     Method method = null;
/* 263 */     Method[] methods = clazz.getDeclaredMethods();
/* 264 */     for (int i = 0; (i < methods.length) && (method == null); i++)
/*     */     {
/* 266 */       if ((methods[i].getName().equals(methodName)) && 
/* 267 */         (isInheritable(pack, methods[i])) && 
/* 268 */         (checkParams(methods[i].getParameterTypes(), args, strictArgs)))
/* 269 */         method = methods[i];
/*     */     }
/* 271 */     if (method != null)
/*     */     {
/* 273 */       return method;
/*     */     }
/*     */     
/* 276 */     return findInheritedMethod(clazz.getPackage(), clazz.getSuperclass(), methodName, args, strictArgs);
/*     */   }
/*     */   
/*     */   protected static Field findInheritedField(Package pack, Class<?> clazz, String fieldName, Class<?> fieldType, boolean strictType)
/*     */     throws NoSuchFieldException
/*     */   {
/* 282 */     if (clazz == null)
/* 283 */       throw new NoSuchFieldException("No class");
/* 284 */     if (fieldName == null) {
/* 285 */       throw new NoSuchFieldException("No field name");
/*     */     }
/*     */     try {
/* 288 */       Field field = clazz.getDeclaredField(fieldName);
/* 289 */       if ((isInheritable(pack, field)) && (isTypeCompatible(fieldType, field.getType(), strictType))) {
/* 290 */         return field;
/*     */       }
/* 292 */       return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
/*     */     }
/*     */     catch (NoSuchFieldException e) {}
/*     */     
/* 296 */     return findInheritedField(clazz.getPackage(), clazz.getSuperclass(), fieldName, fieldType, strictType);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IntrospectionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */