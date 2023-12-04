/*     */ package com.facebook.presto.jdbc.internal.guava.primitives;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.math.BigInteger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class UnsignedLongs
/*     */ {
/*     */   public static final long MAX_VALUE = -1L;
/*     */   
/*     */   private static long flip(long a)
/*     */   {
/*  63 */     return a ^ 0x8000000000000000;
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
/*     */   public static int compare(long a, long b)
/*     */   {
/*  76 */     return Longs.compare(flip(a), flip(b));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long min(long... array)
/*     */   {
/*  88 */     Preconditions.checkArgument(array.length > 0);
/*  89 */     long min = flip(array[0]);
/*  90 */     for (int i = 1; i < array.length; i++) {
/*  91 */       long next = flip(array[i]);
/*  92 */       if (next < min) {
/*  93 */         min = next;
/*     */       }
/*     */     }
/*  96 */     return flip(min);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long max(long... array)
/*     */   {
/* 108 */     Preconditions.checkArgument(array.length > 0);
/* 109 */     long max = flip(array[0]);
/* 110 */     for (int i = 1; i < array.length; i++) {
/* 111 */       long next = flip(array[i]);
/* 112 */       if (next > max) {
/* 113 */         max = next;
/*     */       }
/*     */     }
/* 116 */     return flip(max);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String join(String separator, long... array)
/*     */   {
/* 128 */     Preconditions.checkNotNull(separator);
/* 129 */     if (array.length == 0) {
/* 130 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 134 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 135 */     builder.append(toString(array[0]));
/* 136 */     for (int i = 1; i < array.length; i++) {
/* 137 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 139 */     return builder.toString();
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
/*     */   public static Comparator<long[]> lexicographicalComparator()
/*     */   {
/* 156 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */   
/*     */   static enum LexicographicalComparator implements Comparator<long[]> {
/* 160 */     INSTANCE;
/*     */     
/*     */     private LexicographicalComparator() {}
/*     */     
/* 164 */     public int compare(long[] left, long[] right) { int minLength = Math.min(left.length, right.length);
/* 165 */       for (int i = 0; i < minLength; i++) {
/* 166 */         if (left[i] != right[i]) {
/* 167 */           return UnsignedLongs.compare(left[i], right[i]);
/*     */         }
/*     */       }
/* 170 */       return left.length - right.length;
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
/*     */   public static long divide(long dividend, long divisor)
/*     */   {
/* 183 */     if (divisor < 0L) {
/* 184 */       if (compare(dividend, divisor) < 0) {
/* 185 */         return 0L;
/*     */       }
/* 187 */       return 1L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 192 */     if (dividend >= 0L) {
/* 193 */       return dividend / divisor;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */     long quotient = (dividend >>> 1) / divisor << 1;
/* 203 */     long rem = dividend - quotient * divisor;
/* 204 */     return quotient + (compare(rem, divisor) >= 0 ? 1 : 0);
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
/*     */   public static long remainder(long dividend, long divisor)
/*     */   {
/* 217 */     if (divisor < 0L) {
/* 218 */       if (compare(dividend, divisor) < 0) {
/* 219 */         return dividend;
/*     */       }
/* 221 */       return dividend - divisor;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 226 */     if (dividend >= 0L) {
/* 227 */       return dividend % divisor;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */     long quotient = (dividend >>> 1) / divisor << 1;
/* 237 */     long rem = dividend - quotient * divisor;
/* 238 */     return rem - (compare(rem, divisor) >= 0 ? divisor : 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long parseUnsignedLong(String s)
/*     */   {
/* 250 */     return parseUnsignedLong(s, 10);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static long decode(String stringValue)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 109	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:fromString	(Ljava/lang/String;)Lcom/facebook/presto/jdbc/internal/guava/primitives/ParseRequest;
/*     */     //   4: astore_1
/*     */     //   5: aload_1
/*     */     //   6: getfield 112	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:rawValue	Ljava/lang/String;
/*     */     //   9: aload_1
/*     */     //   10: getfield 115	com/facebook/presto/jdbc/internal/guava/primitives/ParseRequest:radix	I
/*     */     //   13: invokestatic 99	com/facebook/presto/jdbc/internal/guava/primitives/UnsignedLongs:parseUnsignedLong	(Ljava/lang/String;I)J
/*     */     //   16: lreturn
/*     */     //   17: astore_2
/*     */     //   18: new 103	java/lang/NumberFormatException
/*     */     //   21: dup
/*     */     //   22: ldc 119
/*     */     //   24: aload_0
/*     */     //   25: invokestatic 123	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   28: dup
/*     */     //   29: invokevirtual 127	java/lang/String:length	()I
/*     */     //   32: ifeq +9 -> 41
/*     */     //   35: invokevirtual 131	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   38: goto +12 -> 50
/*     */     //   41: pop
/*     */     //   42: new 117	java/lang/String
/*     */     //   45: dup_x1
/*     */     //   46: swap
/*     */     //   47: invokespecial 134	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   50: invokespecial 135	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   53: astore_3
/*     */     //   54: aload_3
/*     */     //   55: aload_2
/*     */     //   56: invokevirtual 139	java/lang/NumberFormatException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
/*     */     //   59: pop
/*     */     //   60: aload_3
/*     */     //   61: athrow
/*     */     // Line number table:
/*     */     //   Java source line #270	-> byte code offset #0
/*     */     //   Java source line #273	-> byte code offset #5
/*     */     //   Java source line #274	-> byte code offset #17
/*     */     //   Java source line #275	-> byte code offset #18
/*     */     //   Java source line #277	-> byte code offset #54
/*     */     //   Java source line #278	-> byte code offset #60
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
/*     */   /* Error */
/*     */   public static long parseUnsignedLong(String s, int radix)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 58	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: invokevirtual 127	java/lang/String:length	()I
/*     */     //   9: ifne +13 -> 22
/*     */     //   12: new 103	java/lang/NumberFormatException
/*     */     //   15: dup
/*     */     //   16: ldc -109
/*     */     //   18: invokespecial 135	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   21: athrow
/*     */     //   22: iload_1
/*     */     //   23: iconst_2
/*     */     //   24: if_icmplt +9 -> 33
/*     */     //   27: iload_1
/*     */     //   28: bipush 36
/*     */     //   30: if_icmple +34 -> 64
/*     */     //   33: new 103	java/lang/NumberFormatException
/*     */     //   36: dup
/*     */     //   37: iload_1
/*     */     //   38: istore_2
/*     */     //   39: new 62	java/lang/StringBuilder
/*     */     //   42: dup
/*     */     //   43: bipush 26
/*     */     //   45: invokespecial 65	java/lang/StringBuilder:<init>	(I)V
/*     */     //   48: ldc -107
/*     */     //   50: invokevirtual 73	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   53: iload_2
/*     */     //   54: invokevirtual 152	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   57: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   60: invokespecial 135	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   63: athrow
/*     */     //   64: getstatic 154	com/facebook/presto/jdbc/internal/guava/primitives/UnsignedLongs:maxSafeDigits	[I
/*     */     //   67: iload_1
/*     */     //   68: iaload
/*     */     //   69: iconst_1
/*     */     //   70: isub
/*     */     //   71: istore_2
/*     */     //   72: lconst_0
/*     */     //   73: lstore_3
/*     */     //   74: iconst_0
/*     */     //   75: istore 5
/*     */     //   77: iload 5
/*     */     //   79: aload_0
/*     */     //   80: invokevirtual 127	java/lang/String:length	()I
/*     */     //   83: if_icmpge +97 -> 180
/*     */     //   86: aload_0
/*     */     //   87: iload 5
/*     */     //   89: invokevirtual 158	java/lang/String:charAt	(I)C
/*     */     //   92: iload_1
/*     */     //   93: invokestatic 164	java/lang/Character:digit	(CI)I
/*     */     //   96: istore 6
/*     */     //   98: iload 6
/*     */     //   100: iconst_m1
/*     */     //   101: if_icmpne +12 -> 113
/*     */     //   104: new 103	java/lang/NumberFormatException
/*     */     //   107: dup
/*     */     //   108: aload_0
/*     */     //   109: invokespecial 135	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   112: athrow
/*     */     //   113: iload 5
/*     */     //   115: iload_2
/*     */     //   116: if_icmple +49 -> 165
/*     */     //   119: lload_3
/*     */     //   120: iload 6
/*     */     //   122: iload_1
/*     */     //   123: invokestatic 168	com/facebook/presto/jdbc/internal/guava/primitives/UnsignedLongs:overflowInParse	(JII)Z
/*     */     //   126: ifeq +39 -> 165
/*     */     //   129: new 103	java/lang/NumberFormatException
/*     */     //   132: dup
/*     */     //   133: ldc -86
/*     */     //   135: aload_0
/*     */     //   136: invokestatic 123	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   139: dup
/*     */     //   140: invokevirtual 127	java/lang/String:length	()I
/*     */     //   143: ifeq +9 -> 152
/*     */     //   146: invokevirtual 131	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   149: goto +12 -> 161
/*     */     //   152: pop
/*     */     //   153: new 117	java/lang/String
/*     */     //   156: dup_x1
/*     */     //   157: swap
/*     */     //   158: invokespecial 134	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   161: invokespecial 135	java/lang/NumberFormatException:<init>	(Ljava/lang/String;)V
/*     */     //   164: athrow
/*     */     //   165: lload_3
/*     */     //   166: iload_1
/*     */     //   167: i2l
/*     */     //   168: lmul
/*     */     //   169: iload 6
/*     */     //   171: i2l
/*     */     //   172: ladd
/*     */     //   173: lstore_3
/*     */     //   174: iinc 5 1
/*     */     //   177: goto -100 -> 77
/*     */     //   180: lload_3
/*     */     //   181: lreturn
/*     */     // Line number table:
/*     */     //   Java source line #294	-> byte code offset #0
/*     */     //   Java source line #295	-> byte code offset #5
/*     */     //   Java source line #296	-> byte code offset #12
/*     */     //   Java source line #298	-> byte code offset #22
/*     */     //   Java source line #299	-> byte code offset #33
/*     */     //   Java source line #302	-> byte code offset #64
/*     */     //   Java source line #303	-> byte code offset #72
/*     */     //   Java source line #304	-> byte code offset #74
/*     */     //   Java source line #305	-> byte code offset #86
/*     */     //   Java source line #306	-> byte code offset #98
/*     */     //   Java source line #307	-> byte code offset #104
/*     */     //   Java source line #309	-> byte code offset #113
/*     */     //   Java source line #310	-> byte code offset #129
/*     */     //   Java source line #312	-> byte code offset #165
/*     */     //   Java source line #304	-> byte code offset #174
/*     */     //   Java source line #315	-> byte code offset #180
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	182	0	s	String
/*     */     //   0	182	1	radix	int
/*     */     //   72	110	2	max_safe_pos	int
/*     */     //   74	108	3	value	long
/*     */     //   77	103	5	pos	int
/*     */     //   98	76	6	digit	int
/*     */   }
/*     */   
/*     */   private static boolean overflowInParse(long current, int digit, int radix)
/*     */   {
/* 325 */     if (current >= 0L) {
/* 326 */       if (current < maxValueDivs[radix]) {
/* 327 */         return false;
/*     */       }
/* 329 */       if (current > maxValueDivs[radix]) {
/* 330 */         return true;
/*     */       }
/*     */       
/* 333 */       return digit > maxValueMods[radix];
/*     */     }
/*     */     
/*     */ 
/* 337 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String toString(long x)
/*     */   {
/* 344 */     return toString(x, 10);
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
/*     */   public static String toString(long x, int radix)
/*     */   {
/* 357 */     Preconditions.checkArgument((radix >= 2) && (radix <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[] { Integer.valueOf(radix) });
/*     */     
/* 359 */     if (x == 0L)
/*     */     {
/* 361 */       return "0";
/*     */     }
/* 363 */     char[] buf = new char[64];
/* 364 */     int i = buf.length;
/* 365 */     if (x < 0L)
/*     */     {
/*     */ 
/* 368 */       long quotient = divide(x, radix);
/* 369 */       long rem = x - quotient * radix;
/* 370 */       buf[(--i)] = Character.forDigit((int)rem, radix);
/* 371 */       x = quotient;
/*     */     }
/*     */     
/* 374 */     while (x > 0L) {
/* 375 */       buf[(--i)] = Character.forDigit((int)(x % radix), radix);
/* 376 */       x /= radix;
/*     */     }
/*     */     
/* 379 */     return new String(buf, i, buf.length - i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 384 */   private static final long[] maxValueDivs = new long[37];
/* 385 */   private static final int[] maxValueMods = new int[37];
/* 386 */   private static final int[] maxSafeDigits = new int[37];
/*     */   
/* 388 */   static { BigInteger overflow = new BigInteger("10000000000000000", 16);
/* 389 */     for (int i = 2; i <= 36; i++) {
/* 390 */       maxValueDivs[i] = divide(-1L, i);
/* 391 */       maxValueMods[i] = ((int)remainder(-1L, i));
/* 392 */       maxSafeDigits[i] = (overflow.toString(i).length() - 1);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\primitives\UnsignedLongs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */