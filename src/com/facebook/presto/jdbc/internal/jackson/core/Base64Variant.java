/*     */ package com.facebook.presto.jdbc.internal.jackson.core;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.util.ByteArrayBuilder;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Base64Variant
/*     */   implements Serializable
/*     */ {
/*     */   private static final int INT_SPACE = 32;
/*     */   private static final long serialVersionUID = 1L;
/*     */   static final char PADDING_CHAR_NONE = '\000';
/*     */   public static final int BASE64_VALUE_INVALID = -1;
/*     */   public static final int BASE64_VALUE_PADDING = -2;
/*  55 */   private final transient int[] _asciiToBase64 = new int[''];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  61 */   private final transient char[] _base64ToAsciiC = new char[64];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  67 */   private final transient byte[] _base64ToAsciiB = new byte[64];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final String _name;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final transient boolean _usesPadding;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final transient char _paddingChar;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final transient int _maxLineLength;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Base64Variant(String name, String base64Alphabet, boolean usesPadding, char paddingChar, int maxLineLength)
/*     */   {
/* 111 */     this._name = name;
/* 112 */     this._usesPadding = usesPadding;
/* 113 */     this._paddingChar = paddingChar;
/* 114 */     this._maxLineLength = maxLineLength;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 119 */     int alphaLen = base64Alphabet.length();
/* 120 */     if (alphaLen != 64) {
/* 121 */       throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
/*     */     }
/*     */     
/*     */ 
/* 125 */     base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
/* 126 */     Arrays.fill(this._asciiToBase64, -1);
/* 127 */     for (int i = 0; i < alphaLen; i++) {
/* 128 */       char alpha = this._base64ToAsciiC[i];
/* 129 */       this._base64ToAsciiB[i] = ((byte)alpha);
/* 130 */       this._asciiToBase64[alpha] = i;
/*     */     }
/*     */     
/*     */ 
/* 134 */     if (usesPadding) {
/* 135 */       this._asciiToBase64[paddingChar] = -2;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Base64Variant(Base64Variant base, String name, int maxLineLength)
/*     */   {
/* 146 */     this(base, name, base._usesPadding, base._paddingChar, maxLineLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Base64Variant(Base64Variant base, String name, boolean usesPadding, char paddingChar, int maxLineLength)
/*     */   {
/* 156 */     this._name = name;
/* 157 */     byte[] srcB = base._base64ToAsciiB;
/* 158 */     System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
/* 159 */     char[] srcC = base._base64ToAsciiC;
/* 160 */     System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
/* 161 */     int[] srcV = base._asciiToBase64;
/* 162 */     System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
/*     */     
/* 164 */     this._usesPadding = usesPadding;
/* 165 */     this._paddingChar = paddingChar;
/* 166 */     this._maxLineLength = maxLineLength;
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
/*     */   protected Object readResolve()
/*     */   {
/* 180 */     return Base64Variants.valueOf(this._name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */   public String getName() { return this._name; }
/*     */   
/* 191 */   public boolean usesPadding() { return this._usesPadding; }
/* 192 */   public boolean usesPaddingChar(char c) { return c == this._paddingChar; }
/* 193 */   public boolean usesPaddingChar(int ch) { return ch == this._paddingChar; }
/* 194 */   public char getPaddingChar() { return this._paddingChar; }
/* 195 */   public byte getPaddingByte() { return (byte)this._paddingChar; }
/*     */   
/* 197 */   public int getMaxLineLength() { return this._maxLineLength; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int decodeBase64Char(char c)
/*     */   {
/* 210 */     int ch = c;
/* 211 */     return ch <= 127 ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */   
/*     */   public int decodeBase64Char(int ch)
/*     */   {
/* 216 */     return ch <= 127 ? this._asciiToBase64[ch] : -1;
/*     */   }
/*     */   
/*     */   public int decodeBase64Byte(byte b)
/*     */   {
/* 221 */     int ch = b;
/* 222 */     return ch <= 127 ? this._asciiToBase64[ch] : -1;
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
/*     */   public char encodeBase64BitsAsChar(int value)
/*     */   {
/* 236 */     return this._base64ToAsciiC[value];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int encodeBase64Chunk(int b24, char[] buffer, int ptr)
/*     */   {
/* 245 */     buffer[(ptr++)] = this._base64ToAsciiC[(b24 >> 18 & 0x3F)];
/* 246 */     buffer[(ptr++)] = this._base64ToAsciiC[(b24 >> 12 & 0x3F)];
/* 247 */     buffer[(ptr++)] = this._base64ToAsciiC[(b24 >> 6 & 0x3F)];
/* 248 */     buffer[(ptr++)] = this._base64ToAsciiC[(b24 & 0x3F)];
/* 249 */     return ptr;
/*     */   }
/*     */   
/*     */   public void encodeBase64Chunk(StringBuilder sb, int b24)
/*     */   {
/* 254 */     sb.append(this._base64ToAsciiC[(b24 >> 18 & 0x3F)]);
/* 255 */     sb.append(this._base64ToAsciiC[(b24 >> 12 & 0x3F)]);
/* 256 */     sb.append(this._base64ToAsciiC[(b24 >> 6 & 0x3F)]);
/* 257 */     sb.append(this._base64ToAsciiC[(b24 & 0x3F)]);
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr)
/*     */   {
/* 270 */     buffer[(outPtr++)] = this._base64ToAsciiC[(bits >> 18 & 0x3F)];
/* 271 */     buffer[(outPtr++)] = this._base64ToAsciiC[(bits >> 12 & 0x3F)];
/* 272 */     if (this._usesPadding) {
/* 273 */       buffer[(outPtr++)] = (outputBytes == 2 ? this._base64ToAsciiC[(bits >> 6 & 0x3F)] : this._paddingChar);
/*     */       
/* 275 */       buffer[(outPtr++)] = this._paddingChar;
/*     */     }
/* 277 */     else if (outputBytes == 2) {
/* 278 */       buffer[(outPtr++)] = this._base64ToAsciiC[(bits >> 6 & 0x3F)];
/*     */     }
/*     */     
/* 281 */     return outPtr;
/*     */   }
/*     */   
/*     */   public void encodeBase64Partial(StringBuilder sb, int bits, int outputBytes)
/*     */   {
/* 286 */     sb.append(this._base64ToAsciiC[(bits >> 18 & 0x3F)]);
/* 287 */     sb.append(this._base64ToAsciiC[(bits >> 12 & 0x3F)]);
/* 288 */     if (this._usesPadding) {
/* 289 */       sb.append(outputBytes == 2 ? this._base64ToAsciiC[(bits >> 6 & 0x3F)] : this._paddingChar);
/*     */       
/* 291 */       sb.append(this._paddingChar);
/*     */     }
/* 293 */     else if (outputBytes == 2) {
/* 294 */       sb.append(this._base64ToAsciiC[(bits >> 6 & 0x3F)]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte encodeBase64BitsAsByte(int value)
/*     */   {
/* 302 */     return this._base64ToAsciiB[value];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int encodeBase64Chunk(int b24, byte[] buffer, int ptr)
/*     */   {
/* 311 */     buffer[(ptr++)] = this._base64ToAsciiB[(b24 >> 18 & 0x3F)];
/* 312 */     buffer[(ptr++)] = this._base64ToAsciiB[(b24 >> 12 & 0x3F)];
/* 313 */     buffer[(ptr++)] = this._base64ToAsciiB[(b24 >> 6 & 0x3F)];
/* 314 */     buffer[(ptr++)] = this._base64ToAsciiB[(b24 & 0x3F)];
/* 315 */     return ptr;
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
/*     */   public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr)
/*     */   {
/* 328 */     buffer[(outPtr++)] = this._base64ToAsciiB[(bits >> 18 & 0x3F)];
/* 329 */     buffer[(outPtr++)] = this._base64ToAsciiB[(bits >> 12 & 0x3F)];
/* 330 */     if (this._usesPadding) {
/* 331 */       byte pb = (byte)this._paddingChar;
/* 332 */       buffer[(outPtr++)] = (outputBytes == 2 ? this._base64ToAsciiB[(bits >> 6 & 0x3F)] : pb);
/*     */       
/* 334 */       buffer[(outPtr++)] = pb;
/*     */     }
/* 336 */     else if (outputBytes == 2) {
/* 337 */       buffer[(outPtr++)] = this._base64ToAsciiB[(bits >> 6 & 0x3F)];
/*     */     }
/*     */     
/* 340 */     return outPtr;
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
/*     */   public String encode(byte[] input)
/*     */   {
/* 359 */     return encode(input, false);
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
/*     */   public String encode(byte[] input, boolean addQuotes)
/*     */   {
/* 372 */     int inputEnd = input.length;
/*     */     
/*     */ 
/*     */ 
/* 376 */     int outputLen = inputEnd + (inputEnd >> 2) + (inputEnd >> 3);
/* 377 */     StringBuilder sb = new StringBuilder(outputLen);
/*     */     
/* 379 */     if (addQuotes) {
/* 380 */       sb.append('"');
/*     */     }
/*     */     
/* 383 */     int chunksBeforeLF = getMaxLineLength() >> 2;
/*     */     
/*     */ 
/* 386 */     int inputPtr = 0;
/* 387 */     int safeInputEnd = inputEnd - 3;
/*     */     
/* 389 */     while (inputPtr <= safeInputEnd)
/*     */     {
/* 391 */       int b24 = input[(inputPtr++)] << 8;
/* 392 */       b24 |= input[(inputPtr++)] & 0xFF;
/* 393 */       b24 = b24 << 8 | input[(inputPtr++)] & 0xFF;
/* 394 */       encodeBase64Chunk(sb, b24);
/* 395 */       chunksBeforeLF--; if (chunksBeforeLF <= 0)
/*     */       {
/* 397 */         sb.append('\\');
/* 398 */         sb.append('n');
/* 399 */         chunksBeforeLF = getMaxLineLength() >> 2;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 404 */     int inputLeft = inputEnd - inputPtr;
/* 405 */     if (inputLeft > 0) {
/* 406 */       int b24 = input[(inputPtr++)] << 16;
/* 407 */       if (inputLeft == 2) {
/* 408 */         b24 |= (input[(inputPtr++)] & 0xFF) << 8;
/*     */       }
/* 410 */       encodeBase64Partial(sb, b24, inputLeft);
/*     */     }
/*     */     
/* 413 */     if (addQuotes) {
/* 414 */       sb.append('"');
/*     */     }
/* 416 */     return sb.toString();
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
/*     */   public byte[] decode(String input)
/*     */     throws IllegalArgumentException
/*     */   {
/* 432 */     ByteArrayBuilder b = new ByteArrayBuilder();
/* 433 */     decode(input, b);
/* 434 */     return b.toByteArray();
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
/*     */   public void decode(String str, ByteArrayBuilder builder)
/*     */     throws IllegalArgumentException
/*     */   {
/* 452 */     int ptr = 0;
/* 453 */     int len = str.length();
/*     */     
/*     */ 
/* 456 */     while (ptr < len)
/*     */     {
/*     */       do
/*     */       {
/* 460 */         ch = str.charAt(ptr++);
/* 461 */         if (ptr >= len) {
/*     */           break;
/*     */         }
/* 464 */       } while (ch <= ' ');
/* 465 */       int bits = decodeBase64Char(ch);
/* 466 */       if (bits < 0) {
/* 467 */         _reportInvalidBase64(ch, 0, null);
/*     */       }
/* 469 */       int decodedData = bits;
/*     */       
/* 471 */       if (ptr >= len) {
/* 472 */         _reportBase64EOF();
/*     */       }
/* 474 */       char ch = str.charAt(ptr++);
/* 475 */       bits = decodeBase64Char(ch);
/* 476 */       if (bits < 0) {
/* 477 */         _reportInvalidBase64(ch, 1, null);
/*     */       }
/* 479 */       decodedData = decodedData << 6 | bits;
/*     */       
/* 481 */       if (ptr >= len)
/*     */       {
/* 483 */         if (!usesPadding()) {
/* 484 */           decodedData >>= 4;
/* 485 */           builder.append(decodedData);
/* 486 */           break;
/*     */         }
/* 488 */         _reportBase64EOF();
/*     */       }
/* 490 */       ch = str.charAt(ptr++);
/* 491 */       bits = decodeBase64Char(ch);
/*     */       
/*     */ 
/* 494 */       if (bits < 0) {
/* 495 */         if (bits != -2) {
/* 496 */           _reportInvalidBase64(ch, 2, null);
/*     */         }
/*     */         
/* 499 */         if (ptr >= len) {
/* 500 */           _reportBase64EOF();
/*     */         }
/* 502 */         ch = str.charAt(ptr++);
/* 503 */         if (!usesPaddingChar(ch)) {
/* 504 */           _reportInvalidBase64(ch, 3, "expected padding character '" + getPaddingChar() + "'");
/*     */         }
/*     */         
/* 507 */         decodedData >>= 4;
/* 508 */         builder.append(decodedData);
/*     */       }
/*     */       else
/*     */       {
/* 512 */         decodedData = decodedData << 6 | bits;
/*     */         
/* 514 */         if (ptr >= len)
/*     */         {
/* 516 */           if (!usesPadding()) {
/* 517 */             decodedData >>= 2;
/* 518 */             builder.appendTwoBytes(decodedData);
/* 519 */             break;
/*     */           }
/* 521 */           _reportBase64EOF();
/*     */         }
/* 523 */         ch = str.charAt(ptr++);
/* 524 */         bits = decodeBase64Char(ch);
/* 525 */         if (bits < 0) {
/* 526 */           if (bits != -2) {
/* 527 */             _reportInvalidBase64(ch, 3, null);
/*     */           }
/* 529 */           decodedData >>= 2;
/* 530 */           builder.appendTwoBytes(decodedData);
/*     */         }
/*     */         else {
/* 533 */           decodedData = decodedData << 6 | bits;
/* 534 */           builder.appendThreeBytes(decodedData);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 546 */     return this._name;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 551 */     return o == this;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 556 */     return this._name.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void _reportInvalidBase64(char ch, int bindex, String msg)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     String base;
/*     */     
/*     */ 
/*     */ 
/*     */     String base;
/*     */     
/*     */ 
/*     */ 
/* 573 */     if (ch <= ' ') {
/* 574 */       base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units"; } else { String base;
/* 575 */       if (usesPaddingChar(ch)) {
/* 576 */         base = "Unexpected padding character ('" + getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character"; } else { String base;
/* 577 */         if ((!Character.isDefined(ch)) || (Character.isISOControl(ch)))
/*     */         {
/* 579 */           base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */         } else
/* 581 */           base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
/*     */       } }
/* 583 */     if (msg != null) {
/* 584 */       base = base + ": " + msg;
/*     */     }
/* 586 */     throw new IllegalArgumentException(base);
/*     */   }
/*     */   
/*     */   protected void _reportBase64EOF() throws IllegalArgumentException {
/* 590 */     throw new IllegalArgumentException("Unexpected end-of-String in base64 content");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\Base64Variant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */