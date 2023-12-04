/*     */ package com.facebook.presto.jdbc.internal.guava.primitives;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.Comparator;
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
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class UnsignedInts
/*     */ {
/*     */   static final long INT_MASK = 4294967295L;
/*     */   
/*     */   static int flip(int value)
/*     */   {
/*  55 */     return value ^ 0x80000000;
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
/*     */   public static int compare(int a, int b)
/*     */   {
/*  68 */     return Ints.compare(flip(a), flip(b));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static long toLong(int value)
/*     */   {
/*  75 */     return value & 0xFFFFFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int min(int... array)
/*     */   {
/*  87 */     Preconditions.checkArgument(array.length > 0);
/*  88 */     int min = flip(array[0]);
/*  89 */     for (int i = 1; i < array.length; i++) {
/*  90 */       int next = flip(array[i]);
/*  91 */       if (next < min) {
/*  92 */         min = next;
/*     */       }
/*     */     }
/*  95 */     return flip(min);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int max(int... array)
/*     */   {
/* 107 */     Preconditions.checkArgument(array.length > 0);
/* 108 */     int max = flip(array[0]);
/* 109 */     for (int i = 1; i < array.length; i++) {
/* 110 */       int next = flip(array[i]);
/* 111 */       if (next > max) {
/* 112 */         max = next;
/*     */       }
/*     */     }
/* 115 */     return flip(max);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String join(String separator, int... array)
/*     */   {
/* 127 */     Preconditions.checkNotNull(separator);
/* 128 */     if (array.length == 0) {
/* 129 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 133 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 134 */     builder.append(toString(array[0]));
/* 135 */     for (int i = 1; i < array.length; i++) {
/* 136 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 138 */     return builder.toString();
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
/*     */   public static Comparator<int[]> lexicographicalComparator()
/*     */   {
/* 154 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */   
/*     */   static enum LexicographicalComparator implements Comparator<int[]> {
/* 158 */     INSTANCE;
/*     */     
/*     */     private LexicographicalComparator() {}
/*     */     
/* 162 */     public int compare(int[] left, int[] right) { int minLength = Math.min(left.length, right.length);
/* 163 */       for (int i = 0; i < minLength; i++) {
/* 164 */         if (left[i] != right[i]) {
/* 165 */           return UnsignedInts.compare(left[i], right[i]);
/*     */         }
/*     */       }
/* 168 */       return left.length - right.length;
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
/*     */   public static int divide(int dividend, int divisor)
/*     */   {
/* 181 */     return (int)(toLong(dividend) / toLong(divisor));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int remainder(int dividend, int divisor)
/*     */   {
/* 193 */     return (int)(toLong(dividend) % toLong(divisor));
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static int decode(String stringValue)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 100	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:fromString	(Ljava/lang/String;)Lcom/facebook/presto/jdbc/internal/guava/primitives/ParseRequest;
/*     */     //   4: astore_1
/*     */     //   5: aload_1
/*     */     //   6: getfield 103	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:rawValue	Ljava/lang/String;
/*     */     //   9: aload_1
/*     */     //   10: getfield 106	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:radix	I
/*     */     //   13: invokestatic 110	com/facebook/presto/jdbc/internal/guava/primitives/UnsignedInts:parseUnsignedInt	(Ljava/lang/String;I)I
/*     */     //   16: ireturn
/*     */     //   17: astore_2
/*     */     //   18: new 94	java/lang/NumberFormatException
/*     */     //   21: dup
/*     */     //   22: ldc 114
/*     */     //   24: aload_0
/*     */     //   25: invokestatic 118	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   28: dup
/*     */     //   29: invokevirtual 122	java/lang/String:length	()I
/*     */     //   32: ifeq +9 -> 41
/*     */     //   35: invokevirtual 126	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   38: goto +12 -> 50
/*     */     //   41: pop
/*     */     //   42: new 112	java/lang/String
/*     */     //   45: dup_x1
/*     */     //   46: swap
/*     */     //   47: invokespecial 129	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   50: invokespecial 130	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   53: astore_3
/*     */     //   54: aload_3
/*     */     //   55: aload_2
/*     */     //   56: invokevirtual 134	java/lang/NumberFormatException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*     */     //   59: pop
/*     */     //   60: aload_3
/*     */     //   61: athrow
/*     */     // Line number table:
/*     */     //   Java source line #212	-> byte code offset #0
/*     */     //   Java source line #215	-> byte code offset #5
/*     */     //   Java source line #216	-> byte code offset #17
/*     */     //   Java source line #217	-> byte code offset #18
/*     */     //   Java source line #219	-> byte code offset #54
/*     */     //   Java source line #220	-> byte code offset #60
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	62	0	stringValue	String
/*     */     //   5	57	1	request	ParseRequest
/*     */     //   18	44	2	e	NumberFormatException
/*     */     //   54	8	3	decodeException	NumberFormatException
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   5	16	17	java/lang/NumberFormatException
/*     */   }
/*     */   
/*     */   public static int parseUnsignedInt(String s)
/*     */   {
/* 232 */     return parseUnsignedInt(s, 10);
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
/*     */   public static int parseUnsignedInt(String string, int radix)
/*     */   {
/* 247 */     Preconditions.checkNotNull(string);
/* 248 */     long result = Long.parseLong(string, radix);
/* 249 */     if ((result & 0xFFFFFFFF) != result) {
/* 250 */       String str = String.valueOf(String.valueOf(string));int i = radix;throw new NumberFormatException(69 + str.length() + "Input " + str + " in base " + i + " is not in the range of an unsigned integer");
/*     */     }
/*     */     
/* 253 */     return (int)result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String toString(int x)
/*     */   {
/* 260 */     return toString(x, 10);
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
/*     */   public static String toString(int x, int radix)
/*     */   {
/* 273 */     long asLong = x & 0xFFFFFFFF;
/* 274 */     return Long.toString(asLong, radix);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\primitives\UnsignedInts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */