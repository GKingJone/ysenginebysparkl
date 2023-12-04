/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FieldUtils
/*     */ {
/*     */   public static int safeNegate(int paramInt)
/*     */   {
/*  49 */     if (paramInt == Integer.MIN_VALUE) {
/*  50 */       throw new ArithmeticException("Integer.MIN_VALUE cannot be negated");
/*     */     }
/*  52 */     return -paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int safeAdd(int paramInt1, int paramInt2)
/*     */   {
/*  64 */     int i = paramInt1 + paramInt2;
/*     */     
/*  66 */     if (((paramInt1 ^ i) < 0) && ((paramInt1 ^ paramInt2) >= 0)) {
/*  67 */       throw new ArithmeticException("The calculation caused an overflow: " + paramInt1 + " + " + paramInt2);
/*     */     }
/*     */     
/*  70 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long safeAdd(long paramLong1, long paramLong2)
/*     */   {
/*  82 */     long l = paramLong1 + paramLong2;
/*     */     
/*  84 */     if (((paramLong1 ^ l) < 0L) && ((paramLong1 ^ paramLong2) >= 0L)) {
/*  85 */       throw new ArithmeticException("The calculation caused an overflow: " + paramLong1 + " + " + paramLong2);
/*     */     }
/*     */     
/*  88 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long safeSubtract(long paramLong1, long paramLong2)
/*     */   {
/* 100 */     long l = paramLong1 - paramLong2;
/*     */     
/* 102 */     if (((paramLong1 ^ l) < 0L) && ((paramLong1 ^ paramLong2) < 0L)) {
/* 103 */       throw new ArithmeticException("The calculation caused an overflow: " + paramLong1 + " - " + paramLong2);
/*     */     }
/*     */     
/* 106 */     return l;
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
/*     */   public static int safeMultiply(int paramInt1, int paramInt2)
/*     */   {
/* 119 */     long l = paramInt1 * paramInt2;
/* 120 */     if ((l < -2147483648L) || (l > 2147483647L)) {
/* 121 */       throw new ArithmeticException("Multiplication overflows an int: " + paramInt1 + " * " + paramInt2);
/*     */     }
/* 123 */     return (int)l;
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
/*     */   public static long safeMultiply(long paramLong, int paramInt)
/*     */   {
/* 136 */     switch (paramInt) {
/*     */     case -1: 
/* 138 */       if (paramLong == Long.MIN_VALUE) {
/* 139 */         throw new ArithmeticException("Multiplication overflows a long: " + paramLong + " * " + paramInt);
/*     */       }
/* 141 */       return -paramLong;
/*     */     case 0: 
/* 143 */       return 0L;
/*     */     case 1: 
/* 145 */       return paramLong;
/*     */     }
/* 147 */     long l = paramLong * paramInt;
/* 148 */     if (l / paramInt != paramLong) {
/* 149 */       throw new ArithmeticException("Multiplication overflows a long: " + paramLong + " * " + paramInt);
/*     */     }
/* 151 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long safeMultiply(long paramLong1, long paramLong2)
/*     */   {
/* 163 */     if (paramLong2 == 1L) {
/* 164 */       return paramLong1;
/*     */     }
/* 166 */     if (paramLong1 == 1L) {
/* 167 */       return paramLong2;
/*     */     }
/* 169 */     if ((paramLong1 == 0L) || (paramLong2 == 0L)) {
/* 170 */       return 0L;
/*     */     }
/* 172 */     long l = paramLong1 * paramLong2;
/* 173 */     if ((l / paramLong2 != paramLong1) || ((paramLong1 == Long.MIN_VALUE) && (paramLong2 == -1L)) || ((paramLong2 == Long.MIN_VALUE) && (paramLong1 == -1L))) {
/* 174 */       throw new ArithmeticException("Multiplication overflows a long: " + paramLong1 + " * " + paramLong2);
/*     */     }
/* 176 */     return l;
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
/*     */   public static long safeDivide(long paramLong1, long paramLong2)
/*     */   {
/* 189 */     if ((paramLong1 == Long.MIN_VALUE) && (paramLong2 == -1L)) {
/* 190 */       throw new ArithmeticException("Multiplication overflows a long: " + paramLong1 + " / " + paramLong2);
/*     */     }
/* 192 */     return paramLong1 / paramLong2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int safeToInt(long paramLong)
/*     */   {
/* 203 */     if ((-2147483648L <= paramLong) && (paramLong <= 2147483647L)) {
/* 204 */       return (int)paramLong;
/*     */     }
/* 206 */     throw new ArithmeticException("Value cannot fit in an int: " + paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int safeMultiplyToInt(long paramLong1, long paramLong2)
/*     */   {
/* 218 */     long l = safeMultiply(paramLong1, paramLong2);
/* 219 */     return safeToInt(l);
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
/*     */   public static void verifyValueBounds(DateTimeField paramDateTimeField, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 233 */     if ((paramInt1 < paramInt2) || (paramInt1 > paramInt3)) {
/* 234 */       throw new IllegalFieldValueException(paramDateTimeField.getType(), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void verifyValueBounds(DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 251 */     if ((paramInt1 < paramInt2) || (paramInt1 > paramInt3)) {
/* 252 */       throw new IllegalFieldValueException(paramDateTimeFieldType, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3));
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
/*     */ 
/*     */ 
/*     */   public static void verifyValueBounds(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 268 */     if ((paramInt1 < paramInt2) || (paramInt1 > paramInt3)) {
/* 269 */       throw new IllegalFieldValueException(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getWrappedValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */   {
/* 292 */     return getWrappedValue(paramInt1 + paramInt2, paramInt3, paramInt4);
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
/*     */   public static int getWrappedValue(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 308 */     if (paramInt2 >= paramInt3) {
/* 309 */       throw new IllegalArgumentException("MIN > MAX");
/*     */     }
/*     */     
/* 312 */     int i = paramInt3 - paramInt2 + 1;
/* 313 */     paramInt1 -= paramInt2;
/*     */     
/* 315 */     if (paramInt1 >= 0) {
/* 316 */       return paramInt1 % i + paramInt2;
/*     */     }
/*     */     
/* 319 */     int j = -paramInt1 % i;
/*     */     
/* 321 */     if (j == 0) {
/* 322 */       return 0 + paramInt2;
/*     */     }
/* 324 */     return i - j + paramInt2;
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
/*     */   public static boolean equals(Object paramObject1, Object paramObject2)
/*     */   {
/* 337 */     if (paramObject1 == paramObject2) {
/* 338 */       return true;
/*     */     }
/* 340 */     if ((paramObject1 == null) || (paramObject2 == null)) {
/* 341 */       return false;
/*     */     }
/* 343 */     return paramObject1.equals(paramObject2);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\FieldUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */