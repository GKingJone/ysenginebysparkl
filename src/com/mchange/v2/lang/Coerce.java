/*     */ package com.mchange.v2.lang;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ public final class Coerce
/*     */ {
/*     */   static final Set CAN_COERCE;
/*     */   
/*     */   static
/*     */   {
/*  34 */     Class[] classes = { Byte.TYPE, Boolean.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, String.class, Byte.class, Boolean.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  54 */     Set tmp = new HashSet();
/*  55 */     tmp.addAll(Arrays.asList(classes));
/*  56 */     CAN_COERCE = Collections.unmodifiableSet(tmp);
/*     */   }
/*     */   
/*     */   public static boolean canCoerce(Class cl) {
/*  60 */     return CAN_COERCE.contains(cl);
/*     */   }
/*     */   
/*  63 */   public static boolean canCoerce(Object o) { return canCoerce(o.getClass()); }
/*     */   
/*     */   public static int toInt(String s) {
/*     */     try {
/*  67 */       return Integer.parseInt(s);
/*     */     } catch (NumberFormatException e) {}
/*  69 */     return (int)Double.parseDouble(s);
/*     */   }
/*     */   
/*     */   public static long toLong(String s) {
/*     */     try {
/*  74 */       return Long.parseLong(s);
/*     */     } catch (NumberFormatException e) {}
/*  76 */     return Double.parseDouble(s);
/*     */   }
/*     */   
/*     */   public static float toFloat(String s) {
/*  80 */     return Float.parseFloat(s);
/*     */   }
/*     */   
/*  83 */   public static double toDouble(String s) { return Double.parseDouble(s); }
/*     */   
/*     */   public static byte toByte(String s) {
/*  86 */     return (byte)toInt(s);
/*     */   }
/*     */   
/*  89 */   public static short toShort(String s) { return (short)toInt(s); }
/*     */   
/*     */   public static boolean toBoolean(String s) {
/*  92 */     return Boolean.valueOf(s).booleanValue();
/*     */   }
/*     */   
/*     */   public static char toChar(String s) {
/*  96 */     s = s.trim();
/*  97 */     if (s.length() == 1) {
/*  98 */       return s.charAt(0);
/*     */     }
/* 100 */     return (char)toInt(s);
/*     */   }
/*     */   
/*     */   public static Object toObject(String s, Class type)
/*     */   {
/* 105 */     if (type == Byte.TYPE) { type = Byte.class;
/* 106 */     } else if (type == Boolean.TYPE) { type = Boolean.class;
/* 107 */     } else if (type == Character.TYPE) { type = Character.class;
/* 108 */     } else if (type == Short.TYPE) { type = Short.class;
/* 109 */     } else if (type == Integer.TYPE) { type = Integer.class;
/* 110 */     } else if (type == Long.TYPE) { type = Long.class;
/* 111 */     } else if (type == Float.TYPE) { type = Float.class;
/* 112 */     } else if (type == Double.TYPE) { type = Double.class;
/*     */     }
/* 114 */     if (type == String.class)
/* 115 */       return s;
/* 116 */     if (type == Byte.class)
/* 117 */       return new Byte(toByte(s));
/* 118 */     if (type == Boolean.class)
/* 119 */       return Boolean.valueOf(s);
/* 120 */     if (type == Character.class)
/* 121 */       return new Character(toChar(s));
/* 122 */     if (type == Short.class)
/* 123 */       return new Short(toShort(s));
/* 124 */     if (type == Integer.class)
/* 125 */       return new Integer(s);
/* 126 */     if (type == Long.class)
/* 127 */       return new Long(s);
/* 128 */     if (type == Float.class)
/* 129 */       return new Float(s);
/* 130 */     if (type == Double.class) {
/* 131 */       return new Double(s);
/*     */     }
/* 133 */     throw new IllegalArgumentException("Cannot coerce to type: " + type.getName());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\lang\Coerce.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */