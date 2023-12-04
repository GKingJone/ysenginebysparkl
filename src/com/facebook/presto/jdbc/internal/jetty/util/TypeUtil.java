/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.Name;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TypeUtil
/*     */ {
/*  49 */   private static final Logger LOG = Log.getLogger(TypeUtil.class);
/*  50 */   public static final Class<?>[] NO_ARGS = new Class[0];
/*     */   
/*     */   public static final int CR = 13;
/*     */   
/*     */   public static final int LF = 10;
/*  55 */   private static final HashMap<String, Class<?>> name2Class = new HashMap();
/*     */   private static final HashMap<Class<?>, String> class2Name;
/*     */   
/*  58 */   static { name2Class.put("boolean", Boolean.TYPE);
/*  59 */     name2Class.put("byte", Byte.TYPE);
/*  60 */     name2Class.put("char", Character.TYPE);
/*  61 */     name2Class.put("double", Double.TYPE);
/*  62 */     name2Class.put("float", Float.TYPE);
/*  63 */     name2Class.put("int", Integer.TYPE);
/*  64 */     name2Class.put("long", Long.TYPE);
/*  65 */     name2Class.put("short", Short.TYPE);
/*  66 */     name2Class.put("void", Void.TYPE);
/*     */     
/*  68 */     name2Class.put("java.lang.Boolean.TYPE", Boolean.TYPE);
/*  69 */     name2Class.put("java.lang.Byte.TYPE", Byte.TYPE);
/*  70 */     name2Class.put("java.lang.Character.TYPE", Character.TYPE);
/*  71 */     name2Class.put("java.lang.Double.TYPE", Double.TYPE);
/*  72 */     name2Class.put("java.lang.Float.TYPE", Float.TYPE);
/*  73 */     name2Class.put("java.lang.Integer.TYPE", Integer.TYPE);
/*  74 */     name2Class.put("java.lang.Long.TYPE", Long.TYPE);
/*  75 */     name2Class.put("java.lang.Short.TYPE", Short.TYPE);
/*  76 */     name2Class.put("java.lang.Void.TYPE", Void.TYPE);
/*     */     
/*  78 */     name2Class.put("java.lang.Boolean", Boolean.class);
/*  79 */     name2Class.put("java.lang.Byte", Byte.class);
/*  80 */     name2Class.put("java.lang.Character", Character.class);
/*  81 */     name2Class.put("java.lang.Double", Double.class);
/*  82 */     name2Class.put("java.lang.Float", Float.class);
/*  83 */     name2Class.put("java.lang.Integer", Integer.class);
/*  84 */     name2Class.put("java.lang.Long", Long.class);
/*  85 */     name2Class.put("java.lang.Short", Short.class);
/*     */     
/*  87 */     name2Class.put("Boolean", Boolean.class);
/*  88 */     name2Class.put("Byte", Byte.class);
/*  89 */     name2Class.put("Character", Character.class);
/*  90 */     name2Class.put("Double", Double.class);
/*  91 */     name2Class.put("Float", Float.class);
/*  92 */     name2Class.put("Integer", Integer.class);
/*  93 */     name2Class.put("Long", Long.class);
/*  94 */     name2Class.put("Short", Short.class);
/*     */     
/*  96 */     name2Class.put(null, Void.TYPE);
/*  97 */     name2Class.put("string", String.class);
/*  98 */     name2Class.put("String", String.class);
/*  99 */     name2Class.put("java.lang.String", String.class);
/*     */     
/*     */ 
/*     */ 
/* 103 */     class2Name = new HashMap();
/*     */     
/*     */ 
/* 106 */     class2Name.put(Boolean.TYPE, "boolean");
/* 107 */     class2Name.put(Byte.TYPE, "byte");
/* 108 */     class2Name.put(Character.TYPE, "char");
/* 109 */     class2Name.put(Double.TYPE, "double");
/* 110 */     class2Name.put(Float.TYPE, "float");
/* 111 */     class2Name.put(Integer.TYPE, "int");
/* 112 */     class2Name.put(Long.TYPE, "long");
/* 113 */     class2Name.put(Short.TYPE, "short");
/* 114 */     class2Name.put(Void.TYPE, "void");
/*     */     
/* 116 */     class2Name.put(Boolean.class, "java.lang.Boolean");
/* 117 */     class2Name.put(Byte.class, "java.lang.Byte");
/* 118 */     class2Name.put(Character.class, "java.lang.Character");
/* 119 */     class2Name.put(Double.class, "java.lang.Double");
/* 120 */     class2Name.put(Float.class, "java.lang.Float");
/* 121 */     class2Name.put(Integer.class, "java.lang.Integer");
/* 122 */     class2Name.put(Long.class, "java.lang.Long");
/* 123 */     class2Name.put(Short.class, "java.lang.Short");
/*     */     
/* 125 */     class2Name.put(null, "void");
/* 126 */     class2Name.put(String.class, "java.lang.String");
/*     */     
/*     */ 
/*     */ 
/* 130 */     class2Value = new HashMap();
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 135 */       Class<?>[] s = { String.class };
/*     */       
/* 137 */       class2Value.put(Boolean.TYPE, Boolean.class
/* 138 */         .getMethod("valueOf", s));
/* 139 */       class2Value.put(Byte.TYPE, Byte.class
/* 140 */         .getMethod("valueOf", s));
/* 141 */       class2Value.put(Double.TYPE, Double.class
/* 142 */         .getMethod("valueOf", s));
/* 143 */       class2Value.put(Float.TYPE, Float.class
/* 144 */         .getMethod("valueOf", s));
/* 145 */       class2Value.put(Integer.TYPE, Integer.class
/* 146 */         .getMethod("valueOf", s));
/* 147 */       class2Value.put(Long.TYPE, Long.class
/* 148 */         .getMethod("valueOf", s));
/* 149 */       class2Value.put(Short.TYPE, Short.class
/* 150 */         .getMethod("valueOf", s));
/*     */       
/* 152 */       class2Value.put(Boolean.class, Boolean.class
/* 153 */         .getMethod("valueOf", s));
/* 154 */       class2Value.put(Byte.class, Byte.class
/* 155 */         .getMethod("valueOf", s));
/* 156 */       class2Value.put(Double.class, Double.class
/* 157 */         .getMethod("valueOf", s));
/* 158 */       class2Value.put(Float.class, Float.class
/* 159 */         .getMethod("valueOf", s));
/* 160 */       class2Value.put(Integer.class, Integer.class
/* 161 */         .getMethod("valueOf", s));
/* 162 */       class2Value.put(Long.class, Long.class
/* 163 */         .getMethod("valueOf", s));
/* 164 */       class2Value.put(Short.class, Short.class
/* 165 */         .getMethod("valueOf", s));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 169 */       throw new Error(e);
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
/*     */   public static <T> List<T> asList(T[] a)
/*     */   {
/* 183 */     if (a == null)
/* 184 */       return Collections.emptyList();
/* 185 */     return Arrays.asList(a);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Class<?> fromName(String name)
/*     */   {
/* 195 */     return (Class)name2Class.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toName(Class<?> type)
/*     */   {
/* 205 */     return (String)class2Name.get(type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object valueOf(Class<?> type, String value)
/*     */   {
/*     */     try
/*     */     {
/* 218 */       if (type.equals(String.class)) {
/* 219 */         return value;
/*     */       }
/* 221 */       Method m = (Method)class2Value.get(type);
/* 222 */       if (m != null) {
/* 223 */         return m.invoke(null, new Object[] { value });
/*     */       }
/* 225 */       if ((type.equals(Character.TYPE)) || 
/* 226 */         (type.equals(Character.class))) {
/* 227 */         return Character.valueOf(value.charAt(0));
/*     */       }
/* 229 */       Constructor<?> c = type.getConstructor(new Class[] { String.class });
/* 230 */       return c.newInstance(new Object[] { value });
/*     */     }
/*     */     catch (NoSuchMethodException|IllegalAccessException|InstantiationException x)
/*     */     {
/* 234 */       LOG.ignore(x);
/*     */     }
/*     */     catch (InvocationTargetException x)
/*     */     {
/* 238 */       if ((x.getTargetException() instanceof Error))
/* 239 */         throw ((Error)x.getTargetException());
/* 240 */       LOG.ignore(x);
/*     */     }
/* 242 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object valueOf(String type, String value)
/*     */   {
/* 253 */     return valueOf(fromName(type), value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final HashMap<Class<?>, Method> class2Value;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int parseInt(String s, int offset, int length, int base)
/*     */     throws NumberFormatException
/*     */   {
/* 269 */     int value = 0;
/*     */     
/* 271 */     if (length < 0) {
/* 272 */       length = s.length() - offset;
/*     */     }
/* 274 */     for (int i = 0; i < length; i++)
/*     */     {
/* 276 */       char c = s.charAt(offset + i);
/*     */       
/* 278 */       int digit = convertHexDigit(c);
/* 279 */       if ((digit < 0) || (digit >= base))
/* 280 */         throw new NumberFormatException(s.substring(offset, offset + length));
/* 281 */       value = value * base + digit;
/*     */     }
/* 283 */     return value;
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
/*     */   public static int parseInt(byte[] b, int offset, int length, int base)
/*     */     throws NumberFormatException
/*     */   {
/* 299 */     int value = 0;
/*     */     
/* 301 */     if (length < 0) {
/* 302 */       length = b.length - offset;
/*     */     }
/* 304 */     for (int i = 0; i < length; i++)
/*     */     {
/* 306 */       char c = (char)(0xFF & b[(offset + i)]);
/*     */       
/* 308 */       int digit = c - '0';
/* 309 */       if ((digit < 0) || (digit >= base) || (digit >= 10))
/*     */       {
/* 311 */         digit = '\n' + c - 65;
/* 312 */         if ((digit < 10) || (digit >= base))
/* 313 */           digit = '\n' + c - 97;
/*     */       }
/* 315 */       if ((digit < 0) || (digit >= base))
/* 316 */         throw new NumberFormatException(new String(b, offset, length));
/* 317 */       value = value * base + digit;
/*     */     }
/* 319 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte[] parseBytes(String s, int base)
/*     */   {
/* 325 */     byte[] bytes = new byte[s.length() / 2];
/* 326 */     for (int i = 0; i < s.length(); i += 2)
/* 327 */       bytes[(i / 2)] = ((byte)parseInt(s, i, 2, base));
/* 328 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toString(byte[] bytes, int base)
/*     */   {
/* 334 */     StringBuilder buf = new StringBuilder();
/* 335 */     for (byte b : bytes)
/*     */     {
/* 337 */       int bi = 0xFF & b;
/* 338 */       int c = 48 + bi / base % base;
/* 339 */       if (c > 57)
/* 340 */         c = 97 + (c - 48 - 10);
/* 341 */       buf.append((char)c);
/* 342 */       c = 48 + bi % base;
/* 343 */       if (c > 57)
/* 344 */         c = 97 + (c - 48 - 10);
/* 345 */       buf.append((char)c);
/*     */     }
/* 347 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte convertHexDigit(byte c)
/*     */   {
/* 357 */     byte b = (byte)((c & 0x1F) + (c >> 6) * 25 - 16);
/* 358 */     if ((b < 0) || (b > 15))
/* 359 */       throw new NumberFormatException("!hex " + c);
/* 360 */     return b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int convertHexDigit(char c)
/*     */   {
/* 370 */     int d = (c & 0x1F) + (c >> '\006') * 25 - 16;
/* 371 */     if ((d < 0) || (d > 15))
/* 372 */       throw new NumberFormatException("!hex " + c);
/* 373 */     return d;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int convertHexDigit(int c)
/*     */   {
/* 383 */     int d = (c & 0x1F) + (c >> 6) * 25 - 16;
/* 384 */     if ((d < 0) || (d > 15))
/* 385 */       throw new NumberFormatException("!hex " + c);
/* 386 */     return d;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void toHex(byte b, Appendable buf)
/*     */   {
/*     */     try
/*     */     {
/* 394 */       int d = 0xF & (0xF0 & b) >> 4;
/* 395 */       buf.append((char)((d > 9 ? 55 : 48) + d));
/* 396 */       d = 0xF & b;
/* 397 */       buf.append((char)((d > 9 ? 55 : 48) + d));
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 401 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void toHex(int value, Appendable buf)
/*     */     throws IOException
/*     */   {
/* 408 */     int d = 0xF & (0xF0000000 & value) >> 28;
/* 409 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 410 */     d = 0xF & (0xF000000 & value) >> 24;
/* 411 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 412 */     d = 0xF & (0xF00000 & value) >> 20;
/* 413 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 414 */     d = 0xF & (0xF0000 & value) >> 16;
/* 415 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 416 */     d = 0xF & (0xF000 & value) >> 12;
/* 417 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 418 */     d = 0xF & (0xF00 & value) >> 8;
/* 419 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 420 */     d = 0xF & (0xF0 & value) >> 4;
/* 421 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/* 422 */     d = 0xF & value;
/* 423 */     buf.append((char)((d > 9 ? 55 : 48) + d));
/*     */     
/* 425 */     Integer.toString(0, 36);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void toHex(long value, Appendable buf)
/*     */     throws IOException
/*     */   {
/* 432 */     toHex((int)(value >> 32), buf);
/* 433 */     toHex((int)value, buf);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toHexString(byte b)
/*     */   {
/* 439 */     return toHexString(new byte[] { b }, 0, 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toHexString(byte[] b)
/*     */   {
/* 445 */     return toHexString(b, 0, b.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toHexString(byte[] b, int offset, int length)
/*     */   {
/* 451 */     StringBuilder buf = new StringBuilder();
/* 452 */     for (int i = offset; i < offset + length; i++)
/*     */     {
/* 454 */       int bi = 0xFF & b[i];
/* 455 */       int c = 48 + bi / 16 % 16;
/* 456 */       if (c > 57)
/* 457 */         c = 65 + (c - 48 - 10);
/* 458 */       buf.append((char)c);
/* 459 */       c = 48 + bi % 16;
/* 460 */       if (c > 57)
/* 461 */         c = 97 + (c - 48 - 10);
/* 462 */       buf.append((char)c);
/*     */     }
/* 464 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte[] fromHexString(String s)
/*     */   {
/* 470 */     if (s.length() % 2 != 0)
/* 471 */       throw new IllegalArgumentException(s);
/* 472 */     byte[] array = new byte[s.length() / 2];
/* 473 */     for (int i = 0; i < array.length; i++)
/*     */     {
/* 475 */       int b = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
/* 476 */       array[i] = ((byte)(0xFF & b));
/*     */     }
/* 478 */     return array;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void dump(Class<?> c)
/*     */   {
/* 484 */     System.err.println("Dump: " + c);
/* 485 */     dump(c.getClassLoader());
/*     */   }
/*     */   
/*     */   public static void dump(ClassLoader cl)
/*     */   {
/* 490 */     System.err.println("Dump Loaders:");
/* 491 */     while (cl != null)
/*     */     {
/* 493 */       System.err.println("  loader " + cl);
/* 494 */       cl = cl.getParent();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static Object call(Class<?> oClass, String methodName, Object obj, Object[] arg)
/*     */     throws InvocationTargetException, NoSuchMethodException
/*     */   {
/* 502 */     Objects.requireNonNull(oClass, "Class cannot be null");
/* 503 */     Objects.requireNonNull(methodName, "Method name cannot be null");
/* 504 */     if (StringUtil.isBlank(methodName))
/*     */     {
/* 506 */       throw new IllegalArgumentException("Method name cannot be blank");
/*     */     }
/*     */     
/*     */ 
/* 510 */     Method[] arrayOfMethod1 = oClass.getMethods();int i = arrayOfMethod1.length; for (Method localMethod1 = 0; localMethod1 < i; localMethod1++) { method = arrayOfMethod1[localMethod1];
/*     */       
/* 512 */       if (method.getName().equals(methodName))
/*     */       {
/* 514 */         if (method.getParameterTypes().length == arg.length)
/*     */         {
/* 516 */           if (Modifier.isStatic(method.getModifiers()) == (obj == null))
/*     */           {
/* 518 */             if ((obj != null) || (method.getDeclaringClass() == oClass))
/*     */             {
/*     */               try
/*     */               {
/*     */ 
/* 523 */                 return method.invoke(obj, arg);
/*     */               }
/*     */               catch (IllegalAccessException|IllegalArgumentException e)
/*     */               {
/* 527 */                 LOG.ignore(e);
/*     */               } } }
/*     */         }
/*     */       }
/*     */     }
/* 532 */     Object[] args_with_opts = null;
/*     */     
/* 534 */     Method[] arrayOfMethod2 = oClass.getMethods();localMethod1 = arrayOfMethod2.length; for (Method method = 0; method < localMethod1; method++) { Method method = arrayOfMethod2[method];
/*     */       
/* 536 */       if (method.getName().equals(methodName))
/*     */       {
/* 538 */         if (method.getParameterTypes().length == arg.length + 1)
/*     */         {
/* 540 */           if (method.getParameterTypes()[arg.length].isArray())
/*     */           {
/* 542 */             if (Modifier.isStatic(method.getModifiers()) == (obj == null))
/*     */             {
/* 544 */               if ((obj != null) || (method.getDeclaringClass() == oClass))
/*     */               {
/*     */ 
/* 547 */                 if (args_with_opts == null) {
/* 548 */                   args_with_opts = ArrayUtil.addToArray(arg, new Object[0], Object.class);
/*     */                 }
/*     */                 try {
/* 551 */                   return method.invoke(obj, args_with_opts);
/*     */                 }
/*     */                 catch (IllegalAccessException|IllegalArgumentException e)
/*     */                 {
/* 555 */                   LOG.ignore(e);
/*     */                 }
/*     */               } } } }
/*     */       }
/*     */     }
/* 560 */     throw new NoSuchMethodException(methodName);
/*     */   }
/*     */   
/*     */   public static Object construct(Class<?> klass, Object[] arguments) throws InvocationTargetException, NoSuchMethodException
/*     */   {
/* 565 */     Objects.requireNonNull(klass, "Class cannot be null");
/*     */     
/* 567 */     for (Constructor<?> constructor : klass.getConstructors())
/*     */     {
/* 569 */       if (arguments == null ? 
/*     */       
/*     */ 
/* 572 */         constructor.getParameterTypes().length == 0 : 
/*     */         
/*     */ 
/* 575 */         constructor.getParameterTypes().length == arguments.length)
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 580 */           return constructor.newInstance(arguments);
/*     */         }
/*     */         catch (InstantiationException|IllegalAccessException|IllegalArgumentException e)
/*     */         {
/* 584 */           LOG.ignore(e);
/*     */         } }
/*     */     }
/* 587 */     throw new NoSuchMethodException("<init>");
/*     */   }
/*     */   
/*     */   public static Object construct(Class<?> klass, Object[] arguments, Map<String, Object> namedArgMap) throws InvocationTargetException, NoSuchMethodException
/*     */   {
/* 592 */     Objects.requireNonNull(klass, "Class cannot be null");
/* 593 */     Objects.requireNonNull(namedArgMap, "Named Argument Map cannot be null");
/*     */     
/* 595 */     for (Constructor<?> constructor : klass.getConstructors())
/*     */     {
/* 597 */       if (arguments == null ? 
/*     */       
/*     */ 
/* 600 */         constructor.getParameterTypes().length == 0 : 
/*     */         
/*     */ 
/* 603 */         constructor.getParameterTypes().length == arguments.length)
/*     */       {
/*     */         try
/*     */         {
/*     */ 
/* 608 */           Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
/*     */           
/* 610 */           if ((arguments == null) || (arguments.length == 0))
/*     */           {
/* 612 */             if (LOG.isDebugEnabled())
/* 613 */               LOG.debug("Constructor has no arguments", new Object[0]);
/* 614 */             return constructor.newInstance(arguments);
/*     */           }
/* 616 */           if ((parameterAnnotations == null) || (parameterAnnotations.length == 0))
/*     */           {
/* 618 */             if (LOG.isDebugEnabled())
/* 619 */               LOG.debug("Constructor has no parameter annotations", new Object[0]);
/* 620 */             return constructor.newInstance(arguments);
/*     */           }
/*     */           
/*     */ 
/* 624 */           Object[] swizzled = new Object[arguments.length];
/*     */           
/* 626 */           int count = 0;
/* 627 */           for (Annotation[] annotations : parameterAnnotations)
/*     */           {
/* 629 */             for (Annotation annotation : annotations)
/*     */             {
/* 631 */               if ((annotation instanceof Name))
/*     */               {
/* 633 */                 Name param = (Name)annotation;
/*     */                 
/* 635 */                 if (namedArgMap.containsKey(param.value()))
/*     */                 {
/* 637 */                   if (LOG.isDebugEnabled())
/* 638 */                     LOG.debug("placing named {} in position {}", new Object[] { param.value(), Integer.valueOf(count) });
/* 639 */                   swizzled[count] = namedArgMap.get(param.value());
/*     */                 }
/*     */                 else
/*     */                 {
/* 643 */                   if (LOG.isDebugEnabled())
/* 644 */                     LOG.debug("placing {} in position {}", new Object[] { arguments[count], Integer.valueOf(count) });
/* 645 */                   swizzled[count] = arguments[count];
/*     */                 }
/* 647 */                 count++;
/*     */ 
/*     */ 
/*     */               }
/* 651 */               else if (LOG.isDebugEnabled()) {
/* 652 */                 LOG.debug("passing on annotation {}", new Object[] { annotation });
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 657 */           return constructor.newInstance(swizzled);
/*     */ 
/*     */         }
/*     */         catch (InstantiationException|IllegalAccessException|IllegalArgumentException e)
/*     */         {
/*     */ 
/* 663 */           LOG.ignore(e);
/*     */         } }
/*     */     }
/* 666 */     throw new NoSuchMethodException("<init>");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isTrue(Object o)
/*     */   {
/* 676 */     if (o == null)
/* 677 */       return false;
/* 678 */     if ((o instanceof Boolean))
/* 679 */       return ((Boolean)o).booleanValue();
/* 680 */     return Boolean.parseBoolean(o.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isFalse(Object o)
/*     */   {
/* 690 */     if (o == null)
/* 691 */       return false;
/* 692 */     if ((o instanceof Boolean))
/* 693 */       return !((Boolean)o).booleanValue();
/* 694 */     return "false".equalsIgnoreCase(o.toString());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\TypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */