/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class B64Code
/*     */ {
/*     */   private static final char __pad = '=';
/*  38 */   private static final char[] __rfc1421alphabet = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
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
/*  49 */   private static final byte[] __rfc1421nibbles = new byte['Ā'];
/*  50 */   static { for (int i = 0; i < 256; i++)
/*  51 */       __rfc1421nibbles[i] = -1;
/*  52 */     for (byte b = 0; b < 64; b = (byte)(b + 1))
/*  53 */       __rfc1421nibbles[((byte)__rfc1421alphabet[b])] = b;
/*  54 */     __rfc1421nibbles[61] = 0;
/*     */     
/*     */ 
/*  57 */     __rfc4648urlAlphabet = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
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
/*  68 */     __rfc4648urlNibbles = new byte['Ā'];
/*  69 */     for (int i = 0; i < 256; i++)
/*  70 */       __rfc4648urlNibbles[i] = -1;
/*  71 */     for (byte b = 0; b < 64; b = (byte)(b + 1))
/*  72 */       __rfc4648urlNibbles[((byte)__rfc4648urlAlphabet[b])] = b;
/*  73 */     __rfc4648urlNibbles[61] = 0;
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
/*     */   public static String encode(String s)
/*     */   {
/*  88 */     return encode(s, (Charset)null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String encode(String s, String charEncoding)
/*     */   {
/*     */     byte[] bytes;
/*     */     
/*     */ 
/*     */     byte[] bytes;
/*     */     
/*     */ 
/* 102 */     if (charEncoding == null) {
/* 103 */       bytes = s.getBytes(StandardCharsets.ISO_8859_1);
/*     */     } else
/* 105 */       bytes = s.getBytes(Charset.forName(charEncoding));
/* 106 */     return new String(encode(bytes));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String encode(String s, Charset charEncoding)
/*     */   {
/* 118 */     byte[] bytes = s.getBytes(charEncoding == null ? StandardCharsets.ISO_8859_1 : charEncoding);
/* 119 */     return new String(encode(bytes));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static char[] encode(byte[] b)
/*     */   {
/* 131 */     if (b == null) {
/* 132 */       return null;
/*     */     }
/* 134 */     int bLen = b.length;
/* 135 */     int cLen = (bLen + 2) / 3 * 4;
/* 136 */     char[] c = new char[cLen];
/* 137 */     int ci = 0;
/* 138 */     int bi = 0;
/*     */     
/* 140 */     int stop = bLen / 3 * 3;
/* 141 */     while (bi < stop)
/*     */     {
/* 143 */       byte b0 = b[(bi++)];
/* 144 */       byte b1 = b[(bi++)];
/* 145 */       byte b2 = b[(bi++)];
/* 146 */       c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 147 */       c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F | b1 >>> 4 & 0xF)];
/* 148 */       c[(ci++)] = __rfc1421alphabet[(b1 << 2 & 0x3F | b2 >>> 6 & 0x3)];
/* 149 */       c[(ci++)] = __rfc1421alphabet[(b2 & 0x3F)];
/*     */     }
/*     */     
/* 152 */     if (bLen != bi)
/*     */     {
/* 154 */       switch (bLen % 3)
/*     */       {
/*     */       case 2: 
/* 157 */         byte b0 = b[(bi++)];
/* 158 */         byte b1 = b[(bi++)];
/* 159 */         c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 160 */         c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F | b1 >>> 4 & 0xF)];
/* 161 */         c[(ci++)] = __rfc1421alphabet[(b1 << 2 & 0x3F)];
/* 162 */         c[(ci++)] = '=';
/* 163 */         break;
/*     */       
/*     */       case 1: 
/* 166 */         byte b0 = b[(bi++)];
/* 167 */         c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 168 */         c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F)];
/* 169 */         c[(ci++)] = '=';
/* 170 */         c[(ci++)] = '=';
/* 171 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 178 */     return c;
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
/*     */   public static char[] encode(byte[] b, boolean rfc2045)
/*     */   {
/* 191 */     if (b == null)
/* 192 */       return null;
/* 193 */     if (!rfc2045) {
/* 194 */       return encode(b);
/*     */     }
/* 196 */     int bLen = b.length;
/* 197 */     int cLen = (bLen + 2) / 3 * 4;
/* 198 */     cLen += 2 + 2 * (cLen / 76);
/* 199 */     char[] c = new char[cLen];
/* 200 */     int ci = 0;
/* 201 */     int bi = 0;
/*     */     
/* 203 */     int stop = bLen / 3 * 3;
/* 204 */     int l = 0;
/* 205 */     while (bi < stop)
/*     */     {
/* 207 */       byte b0 = b[(bi++)];
/* 208 */       byte b1 = b[(bi++)];
/* 209 */       byte b2 = b[(bi++)];
/* 210 */       c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 211 */       c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F | b1 >>> 4 & 0xF)];
/* 212 */       c[(ci++)] = __rfc1421alphabet[(b1 << 2 & 0x3F | b2 >>> 6 & 0x3)];
/* 213 */       c[(ci++)] = __rfc1421alphabet[(b2 & 0x3F)];
/* 214 */       l += 4;
/* 215 */       if (l % 76 == 0)
/*     */       {
/* 217 */         c[(ci++)] = '\r';
/* 218 */         c[(ci++)] = '\n';
/*     */       }
/*     */     }
/*     */     
/* 222 */     if (bLen != bi)
/*     */     {
/* 224 */       switch (bLen % 3)
/*     */       {
/*     */       case 2: 
/* 227 */         byte b0 = b[(bi++)];
/* 228 */         byte b1 = b[(bi++)];
/* 229 */         c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 230 */         c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F | b1 >>> 4 & 0xF)];
/* 231 */         c[(ci++)] = __rfc1421alphabet[(b1 << 2 & 0x3F)];
/* 232 */         c[(ci++)] = '=';
/* 233 */         break;
/*     */       
/*     */       case 1: 
/* 236 */         byte b0 = b[(bi++)];
/* 237 */         c[(ci++)] = __rfc1421alphabet[(b0 >>> 2 & 0x3F)];
/* 238 */         c[(ci++)] = __rfc1421alphabet[(b0 << 4 & 0x3F)];
/* 239 */         c[(ci++)] = '=';
/* 240 */         c[(ci++)] = '=';
/* 241 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 248 */     c[(ci++)] = '\r';
/* 249 */     c[(ci++)] = '\n';
/* 250 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final char[] __rfc4648urlAlphabet;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String decode(String encoded, String charEncoding)
/*     */   {
/* 266 */     byte[] decoded = decode(encoded);
/* 267 */     if (charEncoding == null)
/* 268 */       return new String(decoded);
/* 269 */     return new String(decoded, Charset.forName(charEncoding));
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
/*     */   public static String decode(String encoded, Charset charEncoding)
/*     */   {
/* 284 */     byte[] decoded = decode(encoded);
/* 285 */     if (charEncoding == null)
/* 286 */       return new String(decoded);
/* 287 */     return new String(decoded, charEncoding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final byte[] __rfc4648urlNibbles;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] decode(char[] b)
/*     */   {
/* 304 */     if (b == null) {
/* 305 */       return null;
/*     */     }
/* 307 */     int bLen = b.length;
/* 308 */     if (bLen % 4 != 0) {
/* 309 */       throw new IllegalArgumentException("Input block size is not 4");
/*     */     }
/* 311 */     int li = bLen - 1;
/* 312 */     while ((li >= 0) && (b[li] == '=')) {
/* 313 */       li--;
/*     */     }
/* 315 */     if (li < 0) {
/* 316 */       return new byte[0];
/*     */     }
/*     */     
/* 319 */     int rLen = (li + 1) * 3 / 4;
/* 320 */     byte[] r = new byte[rLen];
/* 321 */     int ri = 0;
/* 322 */     int bi = 0;
/* 323 */     int stop = rLen / 3 * 3;
/*     */     
/*     */     try
/*     */     {
/* 327 */       while (ri < stop)
/*     */       {
/* 329 */         byte b0 = __rfc1421nibbles[b[(bi++)]];
/* 330 */         byte b1 = __rfc1421nibbles[b[(bi++)]];
/* 331 */         byte b2 = __rfc1421nibbles[b[(bi++)]];
/* 332 */         byte b3 = __rfc1421nibbles[b[(bi++)]];
/* 333 */         if ((b0 < 0) || (b1 < 0) || (b2 < 0) || (b3 < 0)) {
/* 334 */           throw new IllegalArgumentException("Not B64 encoded");
/*     */         }
/* 336 */         r[(ri++)] = ((byte)(b0 << 2 | b1 >>> 4));
/* 337 */         r[(ri++)] = ((byte)(b1 << 4 | b2 >>> 2));
/* 338 */         r[(ri++)] = ((byte)(b2 << 6 | b3));
/*     */       }
/*     */       
/* 341 */       if (rLen != ri)
/*     */       {
/* 343 */         switch (rLen % 3)
/*     */         {
/*     */         case 2: 
/* 346 */           byte b0 = __rfc1421nibbles[b[(bi++)]];
/* 347 */           byte b1 = __rfc1421nibbles[b[(bi++)]];
/* 348 */           byte b2 = __rfc1421nibbles[b[(bi++)]];
/* 349 */           if ((b0 < 0) || (b1 < 0) || (b2 < 0))
/* 350 */             throw new IllegalArgumentException("Not B64 encoded");
/* 351 */           r[(ri++)] = ((byte)(b0 << 2 | b1 >>> 4));
/* 352 */           r[(ri++)] = ((byte)(b1 << 4 | b2 >>> 2));
/* 353 */           break;
/*     */         
/*     */         case 1: 
/* 356 */           byte b0 = __rfc1421nibbles[b[(bi++)]];
/* 357 */           byte b1 = __rfc1421nibbles[b[(bi++)]];
/* 358 */           if ((b0 < 0) || (b1 < 0))
/* 359 */             throw new IllegalArgumentException("Not B64 encoded");
/* 360 */           r[(ri++)] = ((byte)(b0 << 2 | b1 >>> 4));
/*     */         
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     catch (IndexOutOfBoundsException e)
/*     */     {
/* 370 */       throw new IllegalArgumentException("char " + bi + " was not B64 encoded");
/*     */     }
/*     */     
/*     */ 
/* 374 */     return r;
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
/*     */   public static byte[] decode(String encoded)
/*     */   {
/* 387 */     if (encoded == null) {
/* 388 */       return null;
/*     */     }
/* 390 */     ByteArrayOutputStream bout = new ByteArrayOutputStream(4 * encoded.length() / 3);
/* 391 */     decode(encoded, bout);
/* 392 */     return bout.toByteArray();
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
/*     */   public static void decode(String encoded, ByteArrayOutputStream bout)
/*     */   {
/* 406 */     if (encoded == null) {
/* 407 */       return;
/*     */     }
/* 409 */     if (bout == null) {
/* 410 */       throw new IllegalArgumentException("No outputstream for decoded bytes");
/*     */     }
/* 412 */     int ci = 0;
/* 413 */     byte[] nibbles = new byte[4];
/* 414 */     int s = 0;
/*     */     
/* 416 */     while (ci < encoded.length())
/*     */     {
/* 418 */       char c = encoded.charAt(ci++);
/*     */       
/* 420 */       if (c == '=') {
/*     */         break;
/*     */       }
/* 423 */       if (!Character.isWhitespace(c))
/*     */       {
/*     */ 
/* 426 */         byte nibble = __rfc1421nibbles[c];
/* 427 */         if (nibble < 0) {
/* 428 */           throw new IllegalArgumentException("Not B64 encoded");
/*     */         }
/* 430 */         nibbles[(s++)] = __rfc1421nibbles[c];
/*     */         
/* 432 */         switch (s)
/*     */         {
/*     */         case 1: 
/*     */           break;
/*     */         case 2: 
/* 437 */           bout.write(nibbles[0] << 2 | nibbles[1] >>> 4);
/* 438 */           break;
/*     */         case 3: 
/* 440 */           bout.write(nibbles[1] << 4 | nibbles[2] >>> 2);
/* 441 */           break;
/*     */         case 4: 
/* 443 */           bout.write(nibbles[2] << 6 | nibbles[3]);
/* 444 */           s = 0;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] decodeRFC4648URL(String encoded)
/*     */   {
/* 456 */     if (encoded == null) {
/* 457 */       return null;
/*     */     }
/* 459 */     ByteArrayOutputStream bout = new ByteArrayOutputStream(4 * encoded.length() / 3);
/* 460 */     decodeRFC4648URL(encoded, bout);
/* 461 */     return bout.toByteArray();
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
/*     */   public static void decodeRFC4648URL(String encoded, ByteArrayOutputStream bout)
/*     */   {
/* 475 */     if (encoded == null) {
/* 476 */       return;
/*     */     }
/* 478 */     if (bout == null) {
/* 479 */       throw new IllegalArgumentException("No outputstream for decoded bytes");
/*     */     }
/* 481 */     int ci = 0;
/* 482 */     byte[] nibbles = new byte[4];
/* 483 */     int s = 0;
/*     */     
/* 485 */     while (ci < encoded.length())
/*     */     {
/* 487 */       char c = encoded.charAt(ci++);
/*     */       
/* 489 */       if (c == '=') {
/*     */         break;
/*     */       }
/* 492 */       if (!Character.isWhitespace(c))
/*     */       {
/*     */ 
/* 495 */         byte nibble = __rfc4648urlNibbles[c];
/* 496 */         if (nibble < 0) {
/* 497 */           throw new IllegalArgumentException("Not B64 encoded");
/*     */         }
/* 499 */         nibbles[(s++)] = __rfc4648urlNibbles[c];
/*     */         
/* 501 */         switch (s)
/*     */         {
/*     */         case 1: 
/*     */           break;
/*     */         case 2: 
/* 506 */           bout.write(nibbles[0] << 2 | nibbles[1] >>> 4);
/* 507 */           break;
/*     */         case 3: 
/* 509 */           bout.write(nibbles[1] << 4 | nibbles[2] >>> 2);
/* 510 */           break;
/*     */         case 4: 
/* 512 */           bout.write(nibbles[2] << 6 | nibbles[3]);
/* 513 */           s = 0;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void encode(int value, Appendable buf)
/*     */     throws IOException
/*     */   {
/* 525 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC000000 & value) >> 26)]);
/* 526 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F00000 & value) >> 20)]);
/* 527 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC000 & value) >> 14)]);
/* 528 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F00 & value) >> 8)]);
/* 529 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC & value) >> 2)]);
/* 530 */     buf.append(__rfc1421alphabet[(0x3F & (0x3 & value) << 4)]);
/* 531 */     buf.append('=');
/*     */   }
/*     */   
/*     */   public static void encode(long lvalue, Appendable buf) throws IOException
/*     */   {
/* 536 */     int value = (int)(0xFFFFFFFFFFFFFFFC & lvalue >> 32);
/* 537 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC000000 & value) >> 26)]);
/* 538 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F00000 & value) >> 20)]);
/* 539 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC000 & value) >> 14)]);
/* 540 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F00 & value) >> 8)]);
/* 541 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC & value) >> 2)]);
/*     */     
/* 543 */     buf.append(__rfc1421alphabet[(0x3F & ((0x3 & value) << 4) + (0xF & (int)(lvalue >> 28)))]);
/*     */     
/* 545 */     value = 0xFFFFFFF & (int)lvalue;
/* 546 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC00000 & value) >> 22)]);
/* 547 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F0000 & value) >> 16)]);
/* 548 */     buf.append(__rfc1421alphabet[(0x3F & (0xFC00 & value) >> 10)]);
/* 549 */     buf.append(__rfc1421alphabet[(0x3F & (0x3F0 & value) >> 4)]);
/* 550 */     buf.append(__rfc1421alphabet[(0x3F & (0xF & value) << 2)]);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\B64Code.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */