/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import sun.misc.Unsafe;
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
/*     */ public final class SizeOf
/*     */ {
/*     */   public static final byte SIZE_OF_BYTE = 1;
/*     */   public static final byte SIZE_OF_SHORT = 2;
/*     */   public static final byte SIZE_OF_INT = 4;
/*     */   public static final byte SIZE_OF_LONG = 8;
/*     */   public static final byte SIZE_OF_FLOAT = 4;
/*     */   public static final byte SIZE_OF_DOUBLE = 8;
/*     */   
/*     */   public static long sizeOf(boolean[] array)
/*     */   {
/*  46 */     if (array == null) {
/*  47 */       return 0L;
/*     */     }
/*  49 */     return Unsafe.ARRAY_BOOLEAN_BASE_OFFSET + Unsafe.ARRAY_BOOLEAN_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(byte[] array)
/*     */   {
/*  54 */     if (array == null) {
/*  55 */       return 0L;
/*     */     }
/*  57 */     return Unsafe.ARRAY_BYTE_BASE_OFFSET + Unsafe.ARRAY_BYTE_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(short[] array)
/*     */   {
/*  62 */     if (array == null) {
/*  63 */       return 0L;
/*     */     }
/*  65 */     return Unsafe.ARRAY_SHORT_BASE_OFFSET + Unsafe.ARRAY_SHORT_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(char[] array)
/*     */   {
/*  70 */     if (array == null) {
/*  71 */       return 0L;
/*     */     }
/*  73 */     return Unsafe.ARRAY_CHAR_BASE_OFFSET + Unsafe.ARRAY_CHAR_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(int[] array)
/*     */   {
/*  78 */     if (array == null) {
/*  79 */       return 0L;
/*     */     }
/*  81 */     return Unsafe.ARRAY_INT_BASE_OFFSET + Unsafe.ARRAY_INT_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(long[] array)
/*     */   {
/*  86 */     if (array == null) {
/*  87 */       return 0L;
/*     */     }
/*  89 */     return Unsafe.ARRAY_LONG_BASE_OFFSET + Unsafe.ARRAY_LONG_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(float[] array)
/*     */   {
/*  94 */     if (array == null) {
/*  95 */       return 0L;
/*     */     }
/*  97 */     return Unsafe.ARRAY_FLOAT_BASE_OFFSET + Unsafe.ARRAY_FLOAT_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(double[] array)
/*     */   {
/* 102 */     if (array == null) {
/* 103 */       return 0L;
/*     */     }
/* 105 */     return Unsafe.ARRAY_DOUBLE_BASE_OFFSET + Unsafe.ARRAY_DOUBLE_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */   public static long sizeOf(Object[] array)
/*     */   {
/* 110 */     if (array == null) {
/* 111 */       return 0L;
/*     */     }
/* 113 */     return Unsafe.ARRAY_OBJECT_BASE_OFFSET + Unsafe.ARRAY_OBJECT_INDEX_SCALE * array.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public static long sizeOfBooleanArray(int length)
/*     */   {
/* 119 */     return Unsafe.ARRAY_BOOLEAN_BASE_OFFSET + Unsafe.ARRAY_BOOLEAN_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfByteArray(int length)
/*     */   {
/* 124 */     return Unsafe.ARRAY_BYTE_BASE_OFFSET + Unsafe.ARRAY_BYTE_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfShortArray(int length)
/*     */   {
/* 129 */     return Unsafe.ARRAY_SHORT_BASE_OFFSET + Unsafe.ARRAY_SHORT_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfCharArray(int length)
/*     */   {
/* 134 */     return Unsafe.ARRAY_CHAR_BASE_OFFSET + Unsafe.ARRAY_CHAR_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfIntArray(int length)
/*     */   {
/* 139 */     return Unsafe.ARRAY_INT_BASE_OFFSET + Unsafe.ARRAY_INT_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfLongArray(int length)
/*     */   {
/* 144 */     return Unsafe.ARRAY_LONG_BASE_OFFSET + Unsafe.ARRAY_LONG_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfFloatArray(int length)
/*     */   {
/* 149 */     return Unsafe.ARRAY_FLOAT_BASE_OFFSET + Unsafe.ARRAY_FLOAT_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfDoubleArray(int length)
/*     */   {
/* 154 */     return Unsafe.ARRAY_DOUBLE_BASE_OFFSET + Unsafe.ARRAY_DOUBLE_INDEX_SCALE * length;
/*     */   }
/*     */   
/*     */   public static long sizeOfObjectArray(int length)
/*     */   {
/* 159 */     return Unsafe.ARRAY_OBJECT_BASE_OFFSET + Unsafe.ARRAY_OBJECT_INDEX_SCALE * length;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\SizeOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */