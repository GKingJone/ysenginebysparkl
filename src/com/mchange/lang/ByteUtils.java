/*     */ package com.mchange.lang;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ByteUtils
/*     */ {
/*     */   public static final short UNSIGNED_MAX_VALUE = 255;
/*     */   
/*     */   public static short toUnsigned(byte b)
/*     */   {
/*  35 */     return (short)(b < 0 ? 256 + b : b);
/*     */   }
/*     */   
/*     */   public static String toHexAscii(byte b) {
/*  39 */     StringWriter sw = new StringWriter(2);
/*  40 */     addHexAscii(b, sw);
/*  41 */     return sw.toString();
/*     */   }
/*     */   
/*     */   public static String toHexAscii(byte[] bytes)
/*     */   {
/*  46 */     int len = bytes.length;
/*  47 */     StringWriter sw = new StringWriter(len * 2);
/*  48 */     for (int i = 0; i < len; i++)
/*  49 */       addHexAscii(bytes[i], sw);
/*  50 */     return sw.toString();
/*     */   }
/*     */   
/*     */   public static byte[] fromHexAscii(String s) throws NumberFormatException
/*     */   {
/*     */     try
/*     */     {
/*  57 */       int len = s.length();
/*  58 */       if (len % 2 != 0) {
/*  59 */         throw new NumberFormatException("Hex ascii must be exactly two digits per byte.");
/*     */       }
/*  61 */       int out_len = len / 2;
/*  62 */       byte[] out = new byte[out_len];
/*  63 */       int i = 0;
/*  64 */       StringReader sr = new StringReader(s);
/*  65 */       while (i < out_len)
/*     */       {
/*  67 */         int val = 16 * fromHexDigit(sr.read()) + fromHexDigit(sr.read());
/*  68 */         out[(i++)] = ((byte)val);
/*     */       }
/*  70 */       return out;
/*     */     }
/*     */     catch (IOException e) {
/*  73 */       throw new InternalError("IOException reading from StringReader?!?!");
/*     */     }
/*     */   }
/*     */   
/*     */   static void addHexAscii(byte b, StringWriter sw) {
/*  78 */     short ub = toUnsigned(b);
/*  79 */     int h1 = ub / 16;
/*  80 */     int h2 = ub % 16;
/*  81 */     sw.write(toHexDigit(h1));
/*  82 */     sw.write(toHexDigit(h2));
/*     */   }
/*     */   
/*     */   private static int fromHexDigit(int c) throws NumberFormatException
/*     */   {
/*  87 */     if ((c >= 48) && (c < 58))
/*  88 */       return c - 48;
/*  89 */     if ((c >= 65) && (c < 71))
/*  90 */       return c - 55;
/*  91 */     if ((c >= 97) && (c < 103)) {
/*  92 */       return c - 87;
/*     */     }
/*  94 */     throw new NumberFormatException(39 + c + "' is not a valid hexadecimal digit.");
/*     */   }
/*     */   
/*     */ 
/*     */   private static char toHexDigit(int h)
/*     */   {
/*     */     char out;
/*     */     
/*     */     char out;
/* 103 */     if (h <= 9) out = (char)(h + 48); else {
/* 104 */       out = (char)(h + 55);
/*     */     }
/* 106 */     return out;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\lang\ByteUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */