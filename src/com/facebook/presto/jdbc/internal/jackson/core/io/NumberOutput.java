/*     */ package com.facebook.presto.jdbc.internal.jackson.core.io;
/*     */ 
/*     */ 
/*     */ public final class NumberOutput
/*     */ {
/*     */   private static final char NC = '\000';
/*   7 */   private static int MILLION = 1000000;
/*   8 */   private static int BILLION = 1000000000;
/*   9 */   private static long TEN_BILLION_L = 10000000000L;
/*  10 */   private static long THOUSAND_L = 1000L;
/*     */   
/*  12 */   private static long MIN_INT_AS_LONG = -2147483648L;
/*  13 */   private static long MAX_INT_AS_LONG = 2147483647L;
/*     */   
/*  15 */   static final String SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
/*     */   
/*  17 */   private static final char[] LEAD_3 = new char['ྠ'];
/*  18 */   private static final char[] FULL_3 = new char['ྠ'];
/*     */   private static final byte[] FULL_TRIPLETS_B;
/*     */   
/*     */   static
/*     */   {
/*  23 */     int ix = 0;
/*  24 */     for (int i1 = 0; i1 < 10; i1++) {
/*  25 */       char f1 = (char)(48 + i1);
/*  26 */       char l1 = i1 == 0 ? '\000' : f1;
/*  27 */       for (int i2 = 0; i2 < 10; i2++) {
/*  28 */         char f2 = (char)(48 + i2);
/*  29 */         char l2 = (i1 == 0) && (i2 == 0) ? '\000' : f2;
/*  30 */         for (int i3 = 0; i3 < 10; i3++)
/*     */         {
/*  32 */           char f3 = (char)(48 + i3);
/*  33 */           LEAD_3[ix] = l1;
/*  34 */           LEAD_3[(ix + 1)] = l2;
/*  35 */           LEAD_3[(ix + 2)] = f3;
/*  36 */           FULL_3[ix] = f1;
/*  37 */           FULL_3[(ix + 1)] = f2;
/*  38 */           FULL_3[(ix + 2)] = f3;
/*  39 */           ix += 4;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  45 */     FULL_TRIPLETS_B = new byte['ྠ'];
/*     */     
/*  47 */     for (int i = 0; i < 4000; i++) {
/*  48 */       FULL_TRIPLETS_B[i] = ((byte)FULL_3[i]);
/*     */     }
/*     */   }
/*     */   
/*  52 */   private static final String[] sSmallIntStrs = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
/*     */   
/*     */ 
/*  55 */   private static final String[] sSmallIntStrs2 = { "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10" };
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
/*     */   public static int outputInt(int v, char[] b, int off)
/*     */   {
/*  70 */     if (v < 0) {
/*  71 */       if (v == Integer.MIN_VALUE)
/*     */       {
/*     */ 
/*     */ 
/*  75 */         return outputLong(v, b, off);
/*     */       }
/*  77 */       b[(off++)] = '-';
/*  78 */       v = -v;
/*     */     }
/*     */     
/*  81 */     if (v < MILLION) {
/*  82 */       if (v < 1000) {
/*  83 */         if (v < 10) {
/*  84 */           b[(off++)] = ((char)(48 + v));
/*     */         } else {
/*  86 */           off = leading3(v, b, off);
/*     */         }
/*     */       } else {
/*  89 */         int thousands = v / 1000;
/*  90 */         v -= thousands * 1000;
/*  91 */         off = leading3(thousands, b, off);
/*  92 */         off = full3(v, b, off);
/*     */       }
/*  94 */       return off;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */     boolean hasBillions = v >= BILLION;
/* 103 */     if (hasBillions) {
/* 104 */       v -= BILLION;
/* 105 */       if (v >= BILLION) {
/* 106 */         v -= BILLION;
/* 107 */         b[(off++)] = '2';
/*     */       } else {
/* 109 */         b[(off++)] = '1';
/*     */       }
/*     */     }
/* 112 */     int newValue = v / 1000;
/* 113 */     int ones = v - newValue * 1000;
/* 114 */     v = newValue;
/* 115 */     newValue /= 1000;
/* 116 */     int thousands = v - newValue * 1000;
/*     */     
/*     */ 
/* 119 */     if (hasBillions) {
/* 120 */       off = full3(newValue, b, off);
/*     */     } else {
/* 122 */       off = leading3(newValue, b, off);
/*     */     }
/* 124 */     off = full3(thousands, b, off);
/* 125 */     off = full3(ones, b, off);
/* 126 */     return off;
/*     */   }
/*     */   
/*     */   public static int outputInt(int v, byte[] b, int off)
/*     */   {
/* 131 */     if (v < 0) {
/* 132 */       if (v == Integer.MIN_VALUE) {
/* 133 */         return outputLong(v, b, off);
/*     */       }
/* 135 */       b[(off++)] = 45;
/* 136 */       v = -v;
/*     */     }
/*     */     
/* 139 */     if (v < MILLION) {
/* 140 */       if (v < 1000) {
/* 141 */         if (v < 10) {
/* 142 */           b[(off++)] = ((byte)(48 + v));
/*     */         } else {
/* 144 */           off = leading3(v, b, off);
/*     */         }
/*     */       } else {
/* 147 */         int thousands = v / 1000;
/* 148 */         v -= thousands * 1000;
/* 149 */         off = leading3(thousands, b, off);
/* 150 */         off = full3(v, b, off);
/*     */       }
/* 152 */       return off;
/*     */     }
/* 154 */     boolean hasB = v >= BILLION;
/* 155 */     if (hasB) {
/* 156 */       v -= BILLION;
/* 157 */       if (v >= BILLION) {
/* 158 */         v -= BILLION;
/* 159 */         b[(off++)] = 50;
/*     */       } else {
/* 161 */         b[(off++)] = 49;
/*     */       }
/*     */     }
/* 164 */     int newValue = v / 1000;
/* 165 */     int ones = v - newValue * 1000;
/* 166 */     v = newValue;
/* 167 */     newValue /= 1000;
/* 168 */     int thousands = v - newValue * 1000;
/*     */     
/* 170 */     if (hasB) {
/* 171 */       off = full3(newValue, b, off);
/*     */     } else {
/* 173 */       off = leading3(newValue, b, off);
/*     */     }
/* 175 */     off = full3(thousands, b, off);
/* 176 */     off = full3(ones, b, off);
/* 177 */     return off;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int outputLong(long v, char[] b, int off)
/*     */   {
/* 186 */     if (v < 0L)
/*     */     {
/*     */ 
/*     */ 
/* 190 */       if (v > MIN_INT_AS_LONG) {
/* 191 */         return outputInt((int)v, b, off);
/*     */       }
/* 193 */       if (v == Long.MIN_VALUE)
/*     */       {
/* 195 */         int len = SMALLEST_LONG.length();
/* 196 */         SMALLEST_LONG.getChars(0, len, b, off);
/* 197 */         return off + len;
/*     */       }
/* 199 */       b[(off++)] = '-';
/* 200 */       v = -v;
/*     */     }
/* 202 */     else if (v <= MAX_INT_AS_LONG) {
/* 203 */       return outputInt((int)v, b, off);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 210 */     int origOffset = off;
/* 211 */     off += calcLongStrLength(v);
/* 212 */     int ptr = off;
/*     */     
/*     */ 
/* 215 */     while (v > MAX_INT_AS_LONG) {
/* 216 */       ptr -= 3;
/* 217 */       long newValue = v / THOUSAND_L;
/* 218 */       int triplet = (int)(v - newValue * THOUSAND_L);
/* 219 */       full3(triplet, b, ptr);
/* 220 */       v = newValue;
/*     */     }
/*     */     
/* 223 */     int ivalue = (int)v;
/* 224 */     while (ivalue >= 1000) {
/* 225 */       ptr -= 3;
/* 226 */       int newValue = ivalue / 1000;
/* 227 */       int triplet = ivalue - newValue * 1000;
/* 228 */       full3(triplet, b, ptr);
/* 229 */       ivalue = newValue;
/*     */     }
/*     */     
/* 232 */     leading3(ivalue, b, origOffset);
/*     */     
/* 234 */     return off;
/*     */   }
/*     */   
/*     */   public static int outputLong(long v, byte[] b, int off)
/*     */   {
/* 239 */     if (v < 0L) {
/* 240 */       if (v > MIN_INT_AS_LONG) {
/* 241 */         return outputInt((int)v, b, off);
/*     */       }
/* 243 */       if (v == Long.MIN_VALUE)
/*     */       {
/* 245 */         int len = SMALLEST_LONG.length();
/* 246 */         for (int i = 0; i < len; i++) {
/* 247 */           b[(off++)] = ((byte)SMALLEST_LONG.charAt(i));
/*     */         }
/* 249 */         return off;
/*     */       }
/* 251 */       b[(off++)] = 45;
/* 252 */       v = -v;
/*     */     }
/* 254 */     else if (v <= MAX_INT_AS_LONG) {
/* 255 */       return outputInt((int)v, b, off);
/*     */     }
/*     */     
/* 258 */     int origOff = off;
/* 259 */     off += calcLongStrLength(v);
/* 260 */     int ptr = off;
/*     */     
/*     */ 
/* 263 */     while (v > MAX_INT_AS_LONG) {
/* 264 */       ptr -= 3;
/* 265 */       long newV = v / THOUSAND_L;
/* 266 */       int t = (int)(v - newV * THOUSAND_L);
/* 267 */       full3(t, b, ptr);
/* 268 */       v = newV;
/*     */     }
/*     */     
/* 271 */     int ivalue = (int)v;
/* 272 */     while (ivalue >= 1000) {
/* 273 */       ptr -= 3;
/* 274 */       int newV = ivalue / 1000;
/* 275 */       int t = ivalue - newV * 1000;
/* 276 */       full3(t, b, ptr);
/* 277 */       ivalue = newV;
/*     */     }
/* 279 */     leading3(ivalue, b, origOff);
/* 280 */     return off;
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
/*     */   public static String toString(int v)
/*     */   {
/* 296 */     if (v < sSmallIntStrs.length) {
/* 297 */       if (v >= 0) {
/* 298 */         return sSmallIntStrs[v];
/*     */       }
/* 300 */       int v2 = -v - 1;
/* 301 */       if (v2 < sSmallIntStrs2.length) {
/* 302 */         return sSmallIntStrs2[v2];
/*     */       }
/*     */     }
/* 305 */     return Integer.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(long v) {
/* 309 */     if ((v <= 2147483647L) && (v >= -2147483648L)) {
/* 310 */       return toString((int)v);
/*     */     }
/* 312 */     return Long.toString(v);
/*     */   }
/*     */   
/*     */   public static String toString(double v) {
/* 316 */     return Double.toString(v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String toString(float v)
/*     */   {
/* 323 */     return Float.toString(v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int leading3(int t, char[] b, int off)
/*     */   {
/* 334 */     int digitOffset = t << 2;
/* 335 */     char c = LEAD_3[(digitOffset++)];
/* 336 */     if (c != 0) {
/* 337 */       b[(off++)] = c;
/*     */     }
/* 339 */     c = LEAD_3[(digitOffset++)];
/* 340 */     if (c != 0) {
/* 341 */       b[(off++)] = c;
/*     */     }
/*     */     
/* 344 */     b[(off++)] = LEAD_3[digitOffset];
/* 345 */     return off;
/*     */   }
/*     */   
/*     */   private static int leading3(int t, byte[] b, int off)
/*     */   {
/* 350 */     int digitOffset = t << 2;
/* 351 */     char c = LEAD_3[(digitOffset++)];
/* 352 */     if (c != 0) {
/* 353 */       b[(off++)] = ((byte)c);
/*     */     }
/* 355 */     c = LEAD_3[(digitOffset++)];
/* 356 */     if (c != 0) {
/* 357 */       b[(off++)] = ((byte)c);
/*     */     }
/*     */     
/* 360 */     b[(off++)] = ((byte)LEAD_3[digitOffset]);
/* 361 */     return off;
/*     */   }
/*     */   
/*     */   private static int full3(int t, char[] b, int off)
/*     */   {
/* 366 */     int digitOffset = t << 2;
/* 367 */     b[(off++)] = FULL_3[(digitOffset++)];
/* 368 */     b[(off++)] = FULL_3[(digitOffset++)];
/* 369 */     b[(off++)] = FULL_3[digitOffset];
/* 370 */     return off;
/*     */   }
/*     */   
/*     */   private static int full3(int t, byte[] b, int off)
/*     */   {
/* 375 */     int digitOffset = t << 2;
/* 376 */     b[(off++)] = FULL_TRIPLETS_B[(digitOffset++)];
/* 377 */     b[(off++)] = FULL_TRIPLETS_B[(digitOffset++)];
/* 378 */     b[(off++)] = FULL_TRIPLETS_B[digitOffset];
/* 379 */     return off;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int calcLongStrLength(long v)
/*     */   {
/* 389 */     int len = 10;
/* 390 */     long cmp = TEN_BILLION_L;
/*     */     
/*     */ 
/* 393 */     while ((v >= cmp) && 
/* 394 */       (len != 19))
/*     */     {
/*     */ 
/* 397 */       len++;
/* 398 */       cmp = (cmp << 3) + (cmp << 1);
/*     */     }
/* 400 */     return len;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\NumberOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */