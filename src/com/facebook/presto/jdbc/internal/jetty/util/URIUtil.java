/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class URIUtil
/*     */   implements Cloneable
/*     */ {
/*  46 */   private static final Logger LOG = Log.getLogger(URIUtil.class);
/*     */   
/*     */   public static final String SLASH = "/";
/*     */   
/*     */   public static final String HTTP = "http";
/*     */   public static final String HTTP_COLON = "http:";
/*     */   public static final String HTTPS = "https";
/*     */   public static final String HTTPS_COLON = "https:";
/*  54 */   public static final Charset __CHARSET = StandardCharsets.UTF_8;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String encodePath(String path)
/*     */   {
/*  68 */     if ((path == null) || (path.length() == 0)) {
/*  69 */       return path;
/*     */     }
/*  71 */     StringBuilder buf = encodePath(null, path);
/*  72 */     return buf == null ? path : buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static StringBuilder encodePath(StringBuilder buf, String path)
/*     */   {
/*  83 */     byte[] bytes = null;
/*  84 */     if (buf == null)
/*     */     {
/*  86 */       for (int i = 0; i < path.length(); i++)
/*     */       {
/*  88 */         char c = path.charAt(i);
/*  89 */         switch (c)
/*     */         {
/*     */         case ' ': 
/*     */         case '"': 
/*     */         case '#': 
/*     */         case '%': 
/*     */         case '\'': 
/*     */         case ';': 
/*     */         case '<': 
/*     */         case '>': 
/*     */         case '?': 
/*     */         case '[': 
/*     */         case ']': 
/* 102 */           buf = new StringBuilder(path.length() * 2);
/* 103 */           break;
/*     */         default: 
/* 105 */           if (c > '')
/*     */           {
/* 107 */             bytes = path.getBytes(__CHARSET);
/* 108 */             buf = new StringBuilder(path.length() * 2);
/* 109 */             break;
/*     */           }
/*     */           break; }
/*     */       }
/* 113 */       if (buf == null) {
/* 114 */         return null;
/*     */       }
/*     */     }
/* 117 */     if (bytes != null)
/*     */     {
/* 119 */       for (int i = 0; i < bytes.length; i++)
/*     */       {
/* 121 */         byte c = bytes[i];
/* 122 */         switch (c)
/*     */         {
/*     */         case 37: 
/* 125 */           buf.append("%25");
/* 126 */           break;
/*     */         case 63: 
/* 128 */           buf.append("%3F");
/* 129 */           break;
/*     */         case 59: 
/* 131 */           buf.append("%3B");
/* 132 */           break;
/*     */         case 35: 
/* 134 */           buf.append("%23");
/* 135 */           break;
/*     */         case 34: 
/* 137 */           buf.append("%22");
/* 138 */           break;
/*     */         case 39: 
/* 140 */           buf.append("%27");
/* 141 */           break;
/*     */         case 60: 
/* 143 */           buf.append("%3C");
/* 144 */           break;
/*     */         case 62: 
/* 146 */           buf.append("%3E");
/* 147 */           break;
/*     */         case 32: 
/* 149 */           buf.append("%20");
/* 150 */           break;
/*     */         case 91: 
/* 152 */           buf.append("%5B");
/* 153 */           break;
/*     */         case 93: 
/* 155 */           buf.append("%5D");
/* 156 */           break;
/*     */         default: 
/* 158 */           if (c < 0)
/*     */           {
/* 160 */             buf.append('%');
/* 161 */             TypeUtil.toHex(c, buf);
/*     */           }
/*     */           else {
/* 164 */             buf.append((char)c);
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     } else {
/* 171 */       for (int i = 0; i < path.length(); i++)
/*     */       {
/* 173 */         char c = path.charAt(i);
/* 174 */         switch (c)
/*     */         {
/*     */         case '%': 
/* 177 */           buf.append("%25");
/* 178 */           break;
/*     */         case '?': 
/* 180 */           buf.append("%3F");
/* 181 */           break;
/*     */         case ';': 
/* 183 */           buf.append("%3B");
/* 184 */           break;
/*     */         case '#': 
/* 186 */           buf.append("%23");
/* 187 */           break;
/*     */         case '"': 
/* 189 */           buf.append("%22");
/* 190 */           break;
/*     */         case '\'': 
/* 192 */           buf.append("%27");
/* 193 */           break;
/*     */         case '<': 
/* 195 */           buf.append("%3C");
/* 196 */           break;
/*     */         case '>': 
/* 198 */           buf.append("%3E");
/* 199 */           break;
/*     */         case ' ': 
/* 201 */           buf.append("%20");
/* 202 */           break;
/*     */         case '[': 
/* 204 */           buf.append("%5B");
/* 205 */           break;
/*     */         case ']': 
/* 207 */           buf.append("%5D");
/* 208 */           break;
/*     */         default: 
/* 210 */           buf.append(c);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 217 */     return buf;
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
/*     */   public static StringBuilder encodeString(StringBuilder buf, String path, String encode)
/*     */   {
/* 231 */     if (buf == null)
/*     */     {
/*     */ 
/* 234 */       for (int i = 0; i < path.length(); i++)
/*     */       {
/* 236 */         char c = path.charAt(i);
/* 237 */         if ((c == '%') || (encode.indexOf(c) >= 0))
/*     */         {
/* 239 */           buf = new StringBuilder(path.length() << 1);
/* 240 */           break;
/*     */         }
/*     */       }
/* 243 */       if (buf == null) {
/* 244 */         return null;
/*     */       }
/*     */     }
/* 247 */     synchronized (buf)
/*     */     {
/* 249 */       for (int i = 0; i < path.length(); i++)
/*     */       {
/* 251 */         char c = path.charAt(i);
/* 252 */         if ((c == '%') || (encode.indexOf(c) >= 0))
/*     */         {
/* 254 */           buf.append('%');
/* 255 */           StringUtil.append(buf, (byte)(0xFF & c), 16);
/*     */         }
/*     */         else {
/* 258 */           buf.append(c);
/*     */         }
/*     */       }
/*     */     }
/* 262 */     return buf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String decodePath(String path)
/*     */   {
/* 270 */     return decodePath(path, 0, path.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String decodePath(String path, int offset, int length)
/*     */   {
/*     */     try
/*     */     {
/* 280 */       Utf8StringBuilder builder = null;
/* 281 */       int end = offset + length;
/* 282 */       for (int i = offset; i < end; i++)
/*     */       {
/* 284 */         char c = path.charAt(i);
/* 285 */         switch (c)
/*     */         {
/*     */         case '%': 
/* 288 */           if (builder == null)
/*     */           {
/* 290 */             builder = new Utf8StringBuilder(path.length());
/* 291 */             builder.append(path, offset, i - offset);
/*     */           }
/* 293 */           if (i + 2 < end)
/*     */           {
/* 295 */             char u = path.charAt(i + 1);
/* 296 */             if (u == 'u')
/*     */             {
/*     */ 
/* 299 */               builder.append((char)(0xFFFF & TypeUtil.parseInt(path, i + 2, 4, 16)));
/* 300 */               i += 5;
/*     */             }
/*     */             else
/*     */             {
/* 304 */               builder.append((byte)(0xFF & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2))));
/* 305 */               i += 2;
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 310 */             throw new IllegalArgumentException("Bad URI % encoding");
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case ';': 
/* 316 */           if (builder == null)
/*     */           {
/* 318 */             builder = new Utf8StringBuilder(path.length());
/* 319 */             builder.append(path, offset, i - offset);
/*     */           }
/*     */           do {
/* 322 */             i++; if (i >= end)
/*     */               break;
/* 324 */           } while (path.charAt(i) != '/');
/*     */           
/* 326 */           builder.append('/');
/* 327 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         default: 
/* 334 */           if (builder != null) {
/* 335 */             builder.append(c);
/*     */           }
/*     */           break;
/*     */         }
/*     */       }
/* 340 */       if (builder != null)
/* 341 */         return builder.toString();
/* 342 */       if ((offset == 0) && (length == path.length()))
/* 343 */         return path;
/* 344 */       return path.substring(offset, end);
/*     */     }
/*     */     catch (Utf8Appendable.NotUtf8Exception e)
/*     */     {
/* 348 */       LOG.warn(path.substring(offset, offset + length) + " " + e, new Object[0]);
/* 349 */       LOG.debug(e); }
/* 350 */     return decodeISO88591Path(path, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String decodeISO88591Path(String path, int offset, int length)
/*     */   {
/* 360 */     StringBuilder builder = null;
/* 361 */     int end = offset + length;
/* 362 */     for (int i = offset; i < end; i++)
/*     */     {
/* 364 */       char c = path.charAt(i);
/* 365 */       switch (c)
/*     */       {
/*     */       case '%': 
/* 368 */         if (builder == null)
/*     */         {
/* 370 */           builder = new StringBuilder(path.length());
/* 371 */           builder.append(path, offset, i - offset);
/*     */         }
/* 373 */         if (i + 2 < end)
/*     */         {
/* 375 */           char u = path.charAt(i + 1);
/* 376 */           if (u == 'u')
/*     */           {
/*     */ 
/* 379 */             builder.append((char)(0xFFFF & TypeUtil.parseInt(path, i + 2, 4, 16)));
/* 380 */             i += 5;
/*     */           }
/*     */           else
/*     */           {
/* 384 */             builder.append((byte)(0xFF & TypeUtil.convertHexDigit(u) * 16 + TypeUtil.convertHexDigit(path.charAt(i + 2))));
/* 385 */             i += 2;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 390 */           throw new IllegalArgumentException();
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case ';': 
/* 396 */         if (builder == null)
/*     */         {
/* 398 */           builder = new StringBuilder(path.length());
/* 399 */           builder.append(path, offset, i - offset);
/*     */         }
/* 401 */         do { i++; if (i >= end)
/*     */             break;
/* 403 */         } while (path.charAt(i) != '/');
/*     */         
/* 405 */         builder.append('/');
/* 406 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       default: 
/* 413 */         if (builder != null) {
/* 414 */           builder.append(c);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 419 */     if (builder != null)
/* 420 */       return builder.toString();
/* 421 */     if ((offset == 0) && (length == path.length()))
/* 422 */       return path;
/* 423 */     return path.substring(offset, end);
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
/*     */   public static String addPaths(String p1, String p2)
/*     */   {
/* 437 */     if ((p1 == null) || (p1.length() == 0))
/*     */     {
/* 439 */       if ((p1 != null) && (p2 == null))
/* 440 */         return p1;
/* 441 */       return p2;
/*     */     }
/* 443 */     if ((p2 == null) || (p2.length() == 0)) {
/* 444 */       return p1;
/*     */     }
/* 446 */     int split = p1.indexOf(';');
/* 447 */     if (split < 0)
/* 448 */       split = p1.indexOf('?');
/* 449 */     if (split == 0)
/* 450 */       return p2 + p1;
/* 451 */     if (split < 0) {
/* 452 */       split = p1.length();
/*     */     }
/* 454 */     StringBuilder buf = new StringBuilder(p1.length() + p2.length() + 2);
/* 455 */     buf.append(p1);
/*     */     
/* 457 */     if (buf.charAt(split - 1) == '/')
/*     */     {
/* 459 */       if (p2.startsWith("/"))
/*     */       {
/* 461 */         buf.deleteCharAt(split - 1);
/* 462 */         buf.insert(split - 1, p2);
/*     */       }
/*     */       else {
/* 465 */         buf.insert(split, p2);
/*     */       }
/*     */       
/*     */     }
/* 469 */     else if (p2.startsWith("/")) {
/* 470 */       buf.insert(split, p2);
/*     */     }
/*     */     else {
/* 473 */       buf.insert(split, '/');
/* 474 */       buf.insert(split + 1, p2);
/*     */     }
/*     */     
/*     */ 
/* 478 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String parentPath(String p)
/*     */   {
/* 489 */     if ((p == null) || ("/".equals(p)))
/* 490 */       return null;
/* 491 */     int slash = p.lastIndexOf('/', p.length() - 2);
/* 492 */     if (slash >= 0)
/* 493 */       return p.substring(0, slash + 1);
/* 494 */     return null;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static String canonicalPath(String path)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: ifnull +10 -> 11
/*     */     //   4: aload_0
/*     */     //   5: invokevirtual 46	java/lang/String:length	()I
/*     */     //   8: ifne +5 -> 13
/*     */     //   11: aload_0
/*     */     //   12: areturn
/*     */     //   13: aload_0
/*     */     //   14: invokevirtual 46	java/lang/String:length	()I
/*     */     //   17: istore_1
/*     */     //   18: aload_0
/*     */     //   19: bipush 47
/*     */     //   21: iload_1
/*     */     //   22: invokevirtual 229	java/lang/String:lastIndexOf	(II)I
/*     */     //   25: istore_2
/*     */     //   26: iload_1
/*     */     //   27: ifle +87 -> 114
/*     */     //   30: iload_1
/*     */     //   31: iload_2
/*     */     //   32: isub
/*     */     //   33: lookupswitch	default:+66->99, 2:+27->60, 3:+42->75
/*     */     //   60: aload_0
/*     */     //   61: iload_2
/*     */     //   62: iconst_1
/*     */     //   63: iadd
/*     */     //   64: invokevirtual 64	java/lang/String:charAt	(I)C
/*     */     //   67: bipush 46
/*     */     //   69: if_icmpeq +45 -> 114
/*     */     //   72: goto +27 -> 99
/*     */     //   75: aload_0
/*     */     //   76: iload_2
/*     */     //   77: iconst_1
/*     */     //   78: iadd
/*     */     //   79: invokevirtual 64	java/lang/String:charAt	(I)C
/*     */     //   82: bipush 46
/*     */     //   84: if_icmpne +15 -> 99
/*     */     //   87: aload_0
/*     */     //   88: iload_2
/*     */     //   89: iconst_2
/*     */     //   90: iadd
/*     */     //   91: invokevirtual 64	java/lang/String:charAt	(I)C
/*     */     //   94: bipush 46
/*     */     //   96: if_icmpeq +18 -> 114
/*     */     //   99: iload_2
/*     */     //   100: istore_1
/*     */     //   101: aload_0
/*     */     //   102: bipush 47
/*     */     //   104: iload_1
/*     */     //   105: iconst_1
/*     */     //   106: isub
/*     */     //   107: invokevirtual 229	java/lang/String:lastIndexOf	(II)I
/*     */     //   110: istore_2
/*     */     //   111: goto -85 -> 26
/*     */     //   114: iload_2
/*     */     //   115: iload_1
/*     */     //   116: if_icmplt +5 -> 121
/*     */     //   119: aload_0
/*     */     //   120: areturn
/*     */     //   121: new 51	java/lang/StringBuilder
/*     */     //   124: dup
/*     */     //   125: aload_0
/*     */     //   126: invokespecial 233	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*     */     //   129: astore_3
/*     */     //   130: iconst_m1
/*     */     //   131: istore 4
/*     */     //   133: iconst_m1
/*     */     //   134: istore 5
/*     */     //   136: iconst_0
/*     */     //   137: istore 6
/*     */     //   139: iload_1
/*     */     //   140: ifle +467 -> 607
/*     */     //   143: iload_1
/*     */     //   144: iload_2
/*     */     //   145: isub
/*     */     //   146: lookupswitch	default:+347->493, 2:+26->172, 3:+227->373
/*     */     //   172: aload_3
/*     */     //   173: iload_2
/*     */     //   174: iconst_1
/*     */     //   175: iadd
/*     */     //   176: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   179: bipush 46
/*     */     //   181: if_icmpeq +60 -> 241
/*     */     //   184: iload 6
/*     */     //   186: ifle +356 -> 542
/*     */     //   189: iinc 6 -1
/*     */     //   192: iload 6
/*     */     //   194: ifne +348 -> 542
/*     */     //   197: iload_2
/*     */     //   198: iflt +7 -> 205
/*     */     //   201: iload_2
/*     */     //   202: goto +4 -> 206
/*     */     //   205: iconst_0
/*     */     //   206: istore 4
/*     */     //   208: iload 4
/*     */     //   210: ifle +332 -> 542
/*     */     //   213: iload 5
/*     */     //   215: aload_3
/*     */     //   216: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   219: if_icmpne +323 -> 542
/*     */     //   222: aload_3
/*     */     //   223: iload 5
/*     */     //   225: iconst_1
/*     */     //   226: isub
/*     */     //   227: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   230: bipush 46
/*     */     //   232: if_icmpne +310 -> 542
/*     */     //   235: iinc 4 1
/*     */     //   238: goto +304 -> 542
/*     */     //   241: iload_2
/*     */     //   242: ifge +34 -> 276
/*     */     //   245: aload_3
/*     */     //   246: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   249: iconst_2
/*     */     //   250: if_icmple +26 -> 276
/*     */     //   253: aload_3
/*     */     //   254: iconst_1
/*     */     //   255: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   258: bipush 47
/*     */     //   260: if_icmpne +16 -> 276
/*     */     //   263: aload_3
/*     */     //   264: iconst_2
/*     */     //   265: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   268: bipush 47
/*     */     //   270: if_icmpne +6 -> 276
/*     */     //   273: goto +269 -> 542
/*     */     //   276: iload 5
/*     */     //   278: ifge +6 -> 284
/*     */     //   281: iload_1
/*     */     //   282: istore 5
/*     */     //   284: iload_2
/*     */     //   285: istore 4
/*     */     //   287: iload 4
/*     */     //   289: iflt +19 -> 308
/*     */     //   292: iload 4
/*     */     //   294: ifne +43 -> 337
/*     */     //   297: aload_3
/*     */     //   298: iload 4
/*     */     //   300: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   303: bipush 47
/*     */     //   305: if_icmpne +32 -> 337
/*     */     //   308: iinc 4 1
/*     */     //   311: iload 5
/*     */     //   313: aload_3
/*     */     //   314: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   317: if_icmpge +225 -> 542
/*     */     //   320: aload_3
/*     */     //   321: iload 5
/*     */     //   323: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   326: bipush 47
/*     */     //   328: if_icmpne +214 -> 542
/*     */     //   331: iinc 5 1
/*     */     //   334: goto +208 -> 542
/*     */     //   337: iload_1
/*     */     //   338: aload_3
/*     */     //   339: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   342: if_icmpne +6 -> 348
/*     */     //   345: iinc 4 1
/*     */     //   348: iload_2
/*     */     //   349: iinc 2 -1
/*     */     //   352: istore_1
/*     */     //   353: iload_2
/*     */     //   354: iflt -215 -> 139
/*     */     //   357: aload_3
/*     */     //   358: iload_2
/*     */     //   359: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   362: bipush 47
/*     */     //   364: if_icmpeq -225 -> 139
/*     */     //   367: iinc 2 -1
/*     */     //   370: goto -17 -> 353
/*     */     //   373: aload_3
/*     */     //   374: iload_2
/*     */     //   375: iconst_1
/*     */     //   376: iadd
/*     */     //   377: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   380: bipush 46
/*     */     //   382: if_icmpne +15 -> 397
/*     */     //   385: aload_3
/*     */     //   386: iload_2
/*     */     //   387: iconst_2
/*     */     //   388: iadd
/*     */     //   389: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   392: bipush 46
/*     */     //   394: if_icmpeq +60 -> 454
/*     */     //   397: iload 6
/*     */     //   399: ifle +143 -> 542
/*     */     //   402: iinc 6 -1
/*     */     //   405: iload 6
/*     */     //   407: ifne +135 -> 542
/*     */     //   410: iload_2
/*     */     //   411: iflt +7 -> 418
/*     */     //   414: iload_2
/*     */     //   415: goto +4 -> 419
/*     */     //   418: iconst_0
/*     */     //   419: istore 4
/*     */     //   421: iload 4
/*     */     //   423: ifle +119 -> 542
/*     */     //   426: iload 5
/*     */     //   428: aload_3
/*     */     //   429: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   432: if_icmpne +110 -> 542
/*     */     //   435: aload_3
/*     */     //   436: iload 5
/*     */     //   438: iconst_1
/*     */     //   439: isub
/*     */     //   440: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   443: bipush 46
/*     */     //   445: if_icmpne +97 -> 542
/*     */     //   448: iinc 4 1
/*     */     //   451: goto +91 -> 542
/*     */     //   454: iload_2
/*     */     //   455: istore 4
/*     */     //   457: iload 5
/*     */     //   459: ifge +6 -> 465
/*     */     //   462: iload_1
/*     */     //   463: istore 5
/*     */     //   465: iinc 6 1
/*     */     //   468: iload_2
/*     */     //   469: iinc 2 -1
/*     */     //   472: istore_1
/*     */     //   473: iload_2
/*     */     //   474: iflt -335 -> 139
/*     */     //   477: aload_3
/*     */     //   478: iload_2
/*     */     //   479: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   482: bipush 47
/*     */     //   484: if_icmpeq -345 -> 139
/*     */     //   487: iinc 2 -1
/*     */     //   490: goto -17 -> 473
/*     */     //   493: iload 6
/*     */     //   495: ifle +47 -> 542
/*     */     //   498: iinc 6 -1
/*     */     //   501: iload 6
/*     */     //   503: ifne +39 -> 542
/*     */     //   506: iload_2
/*     */     //   507: iflt +7 -> 514
/*     */     //   510: iload_2
/*     */     //   511: goto +4 -> 515
/*     */     //   514: iconst_0
/*     */     //   515: istore 4
/*     */     //   517: iload 5
/*     */     //   519: aload_3
/*     */     //   520: invokevirtual 234	java/lang/StringBuilder:length	()I
/*     */     //   523: if_icmpne +19 -> 542
/*     */     //   526: aload_3
/*     */     //   527: iload 5
/*     */     //   529: iconst_1
/*     */     //   530: isub
/*     */     //   531: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   534: bipush 46
/*     */     //   536: if_icmpne +6 -> 542
/*     */     //   539: iinc 4 1
/*     */     //   542: iload 6
/*     */     //   544: ifgt +38 -> 582
/*     */     //   547: iload 4
/*     */     //   549: iflt +33 -> 582
/*     */     //   552: iload 5
/*     */     //   554: iload 4
/*     */     //   556: if_icmplt +26 -> 582
/*     */     //   559: aload_3
/*     */     //   560: iload 4
/*     */     //   562: iload 5
/*     */     //   564: invokevirtual 238	java/lang/StringBuilder:delete	(II)Ljava/lang/StringBuilder;
/*     */     //   567: pop
/*     */     //   568: iconst_m1
/*     */     //   569: dup
/*     */     //   570: istore 5
/*     */     //   572: istore 4
/*     */     //   574: iload 6
/*     */     //   576: ifle +6 -> 582
/*     */     //   579: iload_1
/*     */     //   580: istore 5
/*     */     //   582: iload_2
/*     */     //   583: iinc 2 -1
/*     */     //   586: istore_1
/*     */     //   587: iload_2
/*     */     //   588: iflt -449 -> 139
/*     */     //   591: aload_3
/*     */     //   592: iload_2
/*     */     //   593: invokevirtual 203	java/lang/StringBuilder:charAt	(I)C
/*     */     //   596: bipush 47
/*     */     //   598: if_icmpeq -459 -> 139
/*     */     //   601: iinc 2 -1
/*     */     //   604: goto -17 -> 587
/*     */     //   607: iload 6
/*     */     //   609: ifle +5 -> 614
/*     */     //   612: aconst_null
/*     */     //   613: areturn
/*     */     //   614: iload 5
/*     */     //   616: iflt +12 -> 628
/*     */     //   619: aload_3
/*     */     //   620: iload 4
/*     */     //   622: iload 5
/*     */     //   624: invokevirtual 238	java/lang/StringBuilder:delete	(II)Ljava/lang/StringBuilder;
/*     */     //   627: pop
/*     */     //   628: aload_3
/*     */     //   629: invokevirtual 55	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   632: areturn
/*     */     // Line number table:
/*     */     //   Java source line #506	-> byte code offset #0
/*     */     //   Java source line #507	-> byte code offset #11
/*     */     //   Java source line #509	-> byte code offset #13
/*     */     //   Java source line #510	-> byte code offset #18
/*     */     //   Java source line #513	-> byte code offset #26
/*     */     //   Java source line #515	-> byte code offset #30
/*     */     //   Java source line #518	-> byte code offset #60
/*     */     //   Java source line #519	-> byte code offset #72
/*     */     //   Java source line #522	-> byte code offset #75
/*     */     //   Java source line #527	-> byte code offset #99
/*     */     //   Java source line #528	-> byte code offset #101
/*     */     //   Java source line #532	-> byte code offset #114
/*     */     //   Java source line #533	-> byte code offset #119
/*     */     //   Java source line #535	-> byte code offset #121
/*     */     //   Java source line #536	-> byte code offset #130
/*     */     //   Java source line #537	-> byte code offset #133
/*     */     //   Java source line #538	-> byte code offset #136
/*     */     //   Java source line #540	-> byte code offset #139
/*     */     //   Java source line #542	-> byte code offset #143
/*     */     //   Java source line #545	-> byte code offset #172
/*     */     //   Java source line #547	-> byte code offset #184
/*     */     //   Java source line #549	-> byte code offset #197
/*     */     //   Java source line #550	-> byte code offset #208
/*     */     //   Java source line #551	-> byte code offset #235
/*     */     //   Java source line #556	-> byte code offset #241
/*     */     //   Java source line #557	-> byte code offset #273
/*     */     //   Java source line #559	-> byte code offset #276
/*     */     //   Java source line #560	-> byte code offset #281
/*     */     //   Java source line #561	-> byte code offset #284
/*     */     //   Java source line #562	-> byte code offset #287
/*     */     //   Java source line #564	-> byte code offset #308
/*     */     //   Java source line #565	-> byte code offset #311
/*     */     //   Java source line #566	-> byte code offset #331
/*     */     //   Java source line #569	-> byte code offset #337
/*     */     //   Java source line #570	-> byte code offset #345
/*     */     //   Java source line #572	-> byte code offset #348
/*     */     //   Java source line #573	-> byte code offset #353
/*     */     //   Java source line #574	-> byte code offset #367
/*     */     //   Java source line #578	-> byte code offset #373
/*     */     //   Java source line #580	-> byte code offset #397
/*     */     //   Java source line #581	-> byte code offset #410
/*     */     //   Java source line #582	-> byte code offset #421
/*     */     //   Java source line #583	-> byte code offset #448
/*     */     //   Java source line #588	-> byte code offset #454
/*     */     //   Java source line #589	-> byte code offset #457
/*     */     //   Java source line #590	-> byte code offset #462
/*     */     //   Java source line #592	-> byte code offset #465
/*     */     //   Java source line #593	-> byte code offset #468
/*     */     //   Java source line #594	-> byte code offset #473
/*     */     //   Java source line #595	-> byte code offset #487
/*     */     //   Java source line #599	-> byte code offset #493
/*     */     //   Java source line #601	-> byte code offset #506
/*     */     //   Java source line #602	-> byte code offset #517
/*     */     //   Java source line #603	-> byte code offset #539
/*     */     //   Java source line #608	-> byte code offset #542
/*     */     //   Java source line #610	-> byte code offset #559
/*     */     //   Java source line #611	-> byte code offset #568
/*     */     //   Java source line #612	-> byte code offset #574
/*     */     //   Java source line #613	-> byte code offset #579
/*     */     //   Java source line #616	-> byte code offset #582
/*     */     //   Java source line #617	-> byte code offset #587
/*     */     //   Java source line #618	-> byte code offset #601
/*     */     //   Java source line #622	-> byte code offset #607
/*     */     //   Java source line #623	-> byte code offset #612
/*     */     //   Java source line #626	-> byte code offset #614
/*     */     //   Java source line #627	-> byte code offset #619
/*     */     //   Java source line #629	-> byte code offset #628
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	633	0	path	String
/*     */     //   17	570	1	end	int
/*     */     //   25	577	2	start	int
/*     */     //   129	500	3	buf	StringBuilder
/*     */     //   131	490	4	delStart	int
/*     */     //   134	489	5	delEnd	int
/*     */     //   137	471	6	skip	int
/*     */   }
/*     */   
/*     */   public static String compactPath(String path)
/*     */   {
/* 640 */     if ((path == null) || (path.length() == 0)) {
/* 641 */       return path;
/*     */     }
/* 643 */     int state = 0;
/* 644 */     int end = path.length();
/* 645 */     int i = 0;
/*     */     
/*     */ 
/* 648 */     while (i < end)
/*     */     {
/* 650 */       char c = path.charAt(i);
/* 651 */       switch (c)
/*     */       {
/*     */       case '?': 
/* 654 */         return path;
/*     */       case '/': 
/* 656 */         state++;
/* 657 */         if (state != 2) break;
/* 658 */         break;
/*     */       
/*     */       default: 
/* 661 */         state = 0;
/*     */       }
/* 663 */       i++;
/*     */     }
/*     */     
/* 666 */     if (state < 2) {
/* 667 */       return path;
/*     */     }
/* 669 */     StringBuffer buf = new StringBuffer(path.length());
/* 670 */     buf.append(path, 0, i);
/*     */     
/*     */ 
/* 673 */     while (i < end)
/*     */     {
/* 675 */       char c = path.charAt(i);
/* 676 */       switch (c)
/*     */       {
/*     */       case '?': 
/* 679 */         buf.append(path, i, end);
/* 680 */         break;
/*     */       case '/': 
/* 682 */         if (state++ == 0)
/* 683 */           buf.append(c);
/*     */         break;
/*     */       default: 
/* 686 */         state = 0;
/* 687 */         buf.append(c);
/*     */       }
/* 689 */       i++;
/*     */     }
/*     */     
/* 692 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasScheme(String uri)
/*     */   {
/* 702 */     for (int i = 0; i < uri.length(); i++)
/*     */     {
/* 704 */       char c = uri.charAt(i);
/* 705 */       if (c == ':')
/* 706 */         return true;
/* 707 */       if (((c < 'a') || (c > 'z')) && ((c < 'A') || (c > 'Z')) && ((i <= 0) || (((c < '0') || (c > '9')) && (c != '.') && (c != '+') && (c != '-')))) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 716 */     return false;
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
/*     */   public static String newURI(String scheme, String server, int port, String path, String query)
/*     */   {
/* 731 */     StringBuilder builder = newURIBuilder(scheme, server, port);
/* 732 */     builder.append(path);
/* 733 */     if ((query != null) && (query.length() > 0))
/* 734 */       builder.append('?').append(query);
/* 735 */     return builder.toString();
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
/*     */   public static StringBuilder newURIBuilder(String scheme, String server, int port)
/*     */   {
/* 748 */     StringBuilder builder = new StringBuilder();
/* 749 */     appendSchemeHostPort(builder, scheme, server, port);
/* 750 */     return builder;
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
/*     */   public static void appendSchemeHostPort(StringBuilder url, String scheme, String server, int port)
/*     */   {
/* 763 */     if ((server.indexOf(':') >= 0) && (server.charAt(0) != '[')) {
/* 764 */       url.append(scheme).append("://").append('[').append(server).append(']');
/*     */     } else {
/* 766 */       url.append(scheme).append("://").append(server);
/*     */     }
/* 768 */     if (port > 0)
/*     */     {
/* 770 */       switch (scheme)
/*     */       {
/*     */       case "http": 
/* 773 */         if (port != 80) {
/* 774 */           url.append(':').append(port);
/*     */         }
/*     */         break;
/*     */       case "https": 
/* 778 */         if (port != 443) {
/* 779 */           url.append(':').append(port);
/*     */         }
/*     */         break;
/*     */       default: 
/* 783 */         url.append(':').append(port);
/*     */       }
/*     */       
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
/*     */   public static void appendSchemeHostPort(StringBuffer url, String scheme, String server, int port)
/*     */   {
/* 798 */     synchronized (url)
/*     */     {
/* 800 */       if ((server.indexOf(':') >= 0) && (server.charAt(0) != '[')) {
/* 801 */         url.append(scheme).append("://").append('[').append(server).append(']');
/*     */       } else {
/* 803 */         url.append(scheme).append("://").append(server);
/*     */       }
/* 805 */       if (port > 0)
/*     */       {
/* 807 */         switch (scheme)
/*     */         {
/*     */         case "http": 
/* 810 */           if (port != 80) {
/* 811 */             url.append(':').append(port);
/*     */           }
/*     */           break;
/*     */         case "https": 
/* 815 */           if (port != 443) {
/* 816 */             url.append(':').append(port);
/*     */           }
/*     */           break;
/*     */         default: 
/* 820 */           url.append(':').append(port);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean equalsIgnoreEncodings(String uriA, String uriB)
/*     */   {
/* 828 */     int lenA = uriA.length();
/* 829 */     int lenB = uriB.length();
/* 830 */     int a = 0;
/* 831 */     int b = 0;
/*     */     
/* 833 */     while ((a < lenA) && (b < lenB))
/*     */     {
/* 835 */       int oa = uriA.charAt(a++);
/* 836 */       int ca = oa;
/* 837 */       if (ca == 37) {
/* 838 */         ca = TypeUtil.convertHexDigit(uriA.charAt(a++)) * 16 + TypeUtil.convertHexDigit(uriA.charAt(a++));
/*     */       }
/* 840 */       int ob = uriB.charAt(b++);
/* 841 */       int cb = ob;
/* 842 */       if (cb == 37) {
/* 843 */         cb = TypeUtil.convertHexDigit(uriB.charAt(b++)) * 16 + TypeUtil.convertHexDigit(uriB.charAt(b++));
/*     */       }
/* 845 */       if ((ca == 47) && (oa != ob)) {
/* 846 */         return false;
/*     */       }
/* 848 */       if (ca != cb)
/* 849 */         return decodePath(uriA).equals(decodePath(uriB));
/*     */     }
/* 851 */     return (a == lenA) && (b == lenB);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\URIUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */