/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public class FormatUtils
/*     */ {
/*  31 */   private static final double LOG_10 = Math.log(10.0D);
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
/*     */   public static void appendPaddedInteger(StringBuffer paramStringBuffer, int paramInt1, int paramInt2)
/*     */   {
/*     */     try
/*     */     {
/*  51 */       appendPaddedInteger(paramStringBuffer, paramInt1, paramInt2);
/*     */     }
/*     */     catch (IOException localIOException) {}
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
/*     */   public static void appendPaddedInteger(Appendable paramAppendable, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/*  69 */     if (paramInt1 < 0) {
/*  70 */       paramAppendable.append('-');
/*  71 */       if (paramInt1 != Integer.MIN_VALUE) {
/*  72 */         paramInt1 = -paramInt1;
/*     */       } else {
/*  74 */         for (; paramInt2 > 10; paramInt2--) {
/*  75 */           paramAppendable.append('0');
/*     */         }
/*  77 */         paramAppendable.append("2147483648");
/*  78 */         return;
/*     */       }
/*     */     }
/*  81 */     if (paramInt1 < 10) {
/*  82 */       for (; paramInt2 > 1; paramInt2--) {
/*  83 */         paramAppendable.append('0');
/*     */       }
/*  85 */       paramAppendable.append((char)(paramInt1 + 48)); } else { int i;
/*  86 */       if (paramInt1 < 100) {
/*  87 */         for (; paramInt2 > 2; paramInt2--) {
/*  88 */           paramAppendable.append('0');
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  93 */         i = (paramInt1 + 1) * 13421772 >> 27;
/*  94 */         paramAppendable.append((char)(i + 48));
/*     */         
/*  96 */         paramAppendable.append((char)(paramInt1 - (i << 3) - (i << 1) + 48));
/*     */       }
/*     */       else {
/*  99 */         if (paramInt1 < 1000) {
/* 100 */           i = 3;
/* 101 */         } else if (paramInt1 < 10000) {
/* 102 */           i = 4;
/*     */         } else {
/* 104 */           i = (int)(Math.log(paramInt1) / LOG_10) + 1;
/*     */         }
/* 106 */         for (; paramInt2 > i; paramInt2--) {
/* 107 */           paramAppendable.append('0');
/*     */         }
/* 109 */         paramAppendable.append(Integer.toString(paramInt1));
/*     */       }
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
/*     */   public static void appendPaddedInteger(StringBuffer paramStringBuffer, long paramLong, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 125 */       appendPaddedInteger(paramStringBuffer, paramLong, paramInt);
/*     */     }
/*     */     catch (IOException localIOException) {}
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
/*     */   public static void appendPaddedInteger(Appendable paramAppendable, long paramLong, int paramInt)
/*     */     throws IOException
/*     */   {
/* 143 */     int i = (int)paramLong;
/* 144 */     if (i == paramLong) {
/* 145 */       appendPaddedInteger(paramAppendable, i, paramInt);
/* 146 */     } else if (paramInt <= 19) {
/* 147 */       paramAppendable.append(Long.toString(paramLong));
/*     */     } else {
/* 149 */       if (paramLong < 0L) {
/* 150 */         paramAppendable.append('-');
/* 151 */         if (paramLong != Long.MIN_VALUE) {
/* 152 */           paramLong = -paramLong;
/*     */         } else {
/* 154 */           for (; paramInt > 19; paramInt--) {
/* 155 */             paramAppendable.append('0');
/*     */           }
/* 157 */           paramAppendable.append("9223372036854775808");
/* 158 */           return;
/*     */         }
/*     */       }
/* 161 */       int j = (int)(Math.log(paramLong) / LOG_10) + 1;
/* 162 */       for (; paramInt > j; paramInt--) {
/* 163 */         paramAppendable.append('0');
/*     */       }
/* 165 */       paramAppendable.append(Long.toString(paramLong));
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
/*     */   public static void writePaddedInteger(Writer paramWriter, int paramInt1, int paramInt2)
/*     */     throws IOException
/*     */   {
/* 182 */     if (paramInt1 < 0) {
/* 183 */       paramWriter.write(45);
/* 184 */       if (paramInt1 != Integer.MIN_VALUE) {
/* 185 */         paramInt1 = -paramInt1;
/*     */       } else {
/* 187 */         for (; paramInt2 > 10; paramInt2--) {
/* 188 */           paramWriter.write(48);
/*     */         }
/* 190 */         paramWriter.write("2147483648");
/* 191 */         return;
/*     */       }
/*     */     }
/* 194 */     if (paramInt1 < 10) {
/* 195 */       for (; paramInt2 > 1; paramInt2--) {
/* 196 */         paramWriter.write(48);
/*     */       }
/* 198 */       paramWriter.write(paramInt1 + 48); } else { int i;
/* 199 */       if (paramInt1 < 100) {
/* 200 */         for (; paramInt2 > 2; paramInt2--) {
/* 201 */           paramWriter.write(48);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 206 */         i = (paramInt1 + 1) * 13421772 >> 27;
/* 207 */         paramWriter.write(i + 48);
/*     */         
/* 209 */         paramWriter.write(paramInt1 - (i << 3) - (i << 1) + 48);
/*     */       }
/*     */       else {
/* 212 */         if (paramInt1 < 1000) {
/* 213 */           i = 3;
/* 214 */         } else if (paramInt1 < 10000) {
/* 215 */           i = 4;
/*     */         } else {
/* 217 */           i = (int)(Math.log(paramInt1) / LOG_10) + 1;
/*     */         }
/* 219 */         for (; paramInt2 > i; paramInt2--) {
/* 220 */           paramWriter.write(48);
/*     */         }
/* 222 */         paramWriter.write(Integer.toString(paramInt1));
/*     */       }
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
/*     */   public static void writePaddedInteger(Writer paramWriter, long paramLong, int paramInt)
/*     */     throws IOException
/*     */   {
/* 239 */     int i = (int)paramLong;
/* 240 */     if (i == paramLong) {
/* 241 */       writePaddedInteger(paramWriter, i, paramInt);
/* 242 */     } else if (paramInt <= 19) {
/* 243 */       paramWriter.write(Long.toString(paramLong));
/*     */     } else {
/* 245 */       if (paramLong < 0L) {
/* 246 */         paramWriter.write(45);
/* 247 */         if (paramLong != Long.MIN_VALUE) {
/* 248 */           paramLong = -paramLong;
/*     */         } else {
/* 250 */           for (; paramInt > 19; paramInt--) {
/* 251 */             paramWriter.write(48);
/*     */           }
/* 253 */           paramWriter.write("9223372036854775808");
/* 254 */           return;
/*     */         }
/*     */       }
/* 257 */       int j = (int)(Math.log(paramLong) / LOG_10) + 1;
/* 258 */       for (; paramInt > j; paramInt--) {
/* 259 */         paramWriter.write(48);
/*     */       }
/* 261 */       paramWriter.write(Long.toString(paramLong));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void appendUnpaddedInteger(StringBuffer paramStringBuffer, int paramInt)
/*     */   {
/*     */     try
/*     */     {
/* 275 */       appendUnpaddedInteger(paramStringBuffer, paramInt);
/*     */     }
/*     */     catch (IOException localIOException) {}
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
/*     */   public static void appendUnpaddedInteger(Appendable paramAppendable, int paramInt)
/*     */     throws IOException
/*     */   {
/* 291 */     if (paramInt < 0) {
/* 292 */       paramAppendable.append('-');
/* 293 */       if (paramInt != Integer.MIN_VALUE) {
/* 294 */         paramInt = -paramInt;
/*     */       } else {
/* 296 */         paramAppendable.append("2147483648");
/* 297 */         return;
/*     */       }
/*     */     }
/* 300 */     if (paramInt < 10) {
/* 301 */       paramAppendable.append((char)(paramInt + 48));
/* 302 */     } else if (paramInt < 100)
/*     */     {
/*     */ 
/*     */ 
/* 306 */       int i = (paramInt + 1) * 13421772 >> 27;
/* 307 */       paramAppendable.append((char)(i + 48));
/*     */       
/* 309 */       paramAppendable.append((char)(paramInt - (i << 3) - (i << 1) + 48));
/*     */     } else {
/* 311 */       paramAppendable.append(Integer.toString(paramInt));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void appendUnpaddedInteger(StringBuffer paramStringBuffer, long paramLong)
/*     */   {
/*     */     try
/*     */     {
/* 325 */       appendUnpaddedInteger(paramStringBuffer, paramLong);
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void appendUnpaddedInteger(Appendable paramAppendable, long paramLong)
/*     */     throws IOException
/*     */   {
/* 340 */     int i = (int)paramLong;
/* 341 */     if (i == paramLong) {
/* 342 */       appendUnpaddedInteger(paramAppendable, i);
/*     */     } else {
/* 344 */       paramAppendable.append(Long.toString(paramLong));
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
/*     */   public static void writeUnpaddedInteger(Writer paramWriter, int paramInt)
/*     */     throws IOException
/*     */   {
/* 359 */     if (paramInt < 0) {
/* 360 */       paramWriter.write(45);
/* 361 */       if (paramInt != Integer.MIN_VALUE) {
/* 362 */         paramInt = -paramInt;
/*     */       } else {
/* 364 */         paramWriter.write("2147483648");
/* 365 */         return;
/*     */       }
/*     */     }
/* 368 */     if (paramInt < 10) {
/* 369 */       paramWriter.write(paramInt + 48);
/* 370 */     } else if (paramInt < 100)
/*     */     {
/*     */ 
/*     */ 
/* 374 */       int i = (paramInt + 1) * 13421772 >> 27;
/* 375 */       paramWriter.write(i + 48);
/*     */       
/* 377 */       paramWriter.write(paramInt - (i << 3) - (i << 1) + 48);
/*     */     } else {
/* 379 */       paramWriter.write(Integer.toString(paramInt));
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
/*     */   public static void writeUnpaddedInteger(Writer paramWriter, long paramLong)
/*     */     throws IOException
/*     */   {
/* 394 */     int i = (int)paramLong;
/* 395 */     if (i == paramLong) {
/* 396 */       writeUnpaddedInteger(paramWriter, i);
/*     */     } else {
/* 398 */       paramWriter.write(Long.toString(paramLong));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int calculateDigitCount(long paramLong)
/*     */   {
/* 407 */     if (paramLong < 0L) {
/* 408 */       if (paramLong != Long.MIN_VALUE) {
/* 409 */         return calculateDigitCount(-paramLong) + 1;
/*     */       }
/* 411 */       return 20;
/*     */     }
/*     */     
/* 414 */     return paramLong < 10000L ? 4 : paramLong < 1000L ? 3 : paramLong < 100L ? 2 : paramLong < 10L ? 1 : (int)(Math.log(paramLong) / LOG_10) + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int parseTwoDigits(CharSequence paramCharSequence, int paramInt)
/*     */   {
/* 423 */     int i = paramCharSequence.charAt(paramInt) - '0';
/* 424 */     return (i << 3) + (i << 1) + paramCharSequence.charAt(paramInt + 1) - 48;
/*     */   }
/*     */   
/*     */   static String createErrorMessage(String paramString, int paramInt) {
/* 428 */     int i = paramInt + 32;
/*     */     String str;
/* 430 */     if (paramString.length() <= i + 3) {
/* 431 */       str = paramString;
/*     */     } else {
/* 433 */       str = paramString.substring(0, i).concat("...");
/*     */     }
/*     */     
/* 436 */     if (paramInt <= 0) {
/* 437 */       return "Invalid format: \"" + str + '"';
/*     */     }
/*     */     
/* 440 */     if (paramInt >= paramString.length()) {
/* 441 */       return "Invalid format: \"" + str + "\" is too short";
/*     */     }
/*     */     
/* 444 */     return "Invalid format: \"" + str + "\" is malformed at \"" + str.substring(paramInt) + '"';
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\FormatUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */