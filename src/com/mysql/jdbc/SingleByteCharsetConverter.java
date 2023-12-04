/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SingleByteCharsetConverter
/*     */ {
/*     */   private static final int BYTE_RANGE = 256;
/*  37 */   private static byte[] allBytes = new byte['Ā'];
/*  38 */   private static final Map<String, SingleByteCharsetConverter> CONVERTER_MAP = new HashMap();
/*     */   
/*  40 */   private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */   
/*     */ 
/*     */ 
/*  44 */   private static byte[] unknownCharsMap = new byte[65536];
/*     */   
/*     */   static {
/*  47 */     for (int i = -128; i <= 127; i++) {
/*  48 */       allBytes[(i - -128)] = ((byte)i);
/*     */     }
/*     */     
/*  51 */     for (int i = 0; i < unknownCharsMap.length; i++) {
/*  52 */       unknownCharsMap[i] = 63;
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
/*     */   public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/*  68 */     SingleByteCharsetConverter instance = (SingleByteCharsetConverter)CONVERTER_MAP.get(encodingName);
/*     */     
/*  70 */     if (instance == null) {
/*  71 */       instance = initCharset(encodingName);
/*     */     }
/*     */     
/*  74 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SingleByteCharsetConverter initCharset(String javaEncodingName)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/*     */     try
/*     */     {
/*  89 */       if (CharsetMapping.isMultibyteCharset(javaEncodingName)) {
/*  90 */         return null;
/*     */       }
/*     */     } catch (RuntimeException ex) {
/*  93 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/*  94 */       sqlEx.initCause(ex);
/*  95 */       throw sqlEx;
/*     */     }
/*     */     
/*  98 */     SingleByteCharsetConverter converter = new SingleByteCharsetConverter(javaEncodingName);
/*     */     
/* 100 */     CONVERTER_MAP.put(javaEncodingName, converter);
/*     */     
/* 102 */     return converter;
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
/*     */   public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length)
/*     */   {
/* 118 */     return new String(buffer, startPos, length);
/*     */   }
/*     */   
/* 121 */   private char[] byteToChars = new char['Ā'];
/*     */   
/* 123 */   private byte[] charToByteMap = new byte[65536];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SingleByteCharsetConverter(String encodingName)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 134 */     String allBytesString = new String(allBytes, 0, 256, encodingName);
/* 135 */     int allBytesLen = allBytesString.length();
/*     */     
/* 137 */     System.arraycopy(unknownCharsMap, 0, this.charToByteMap, 0, this.charToByteMap.length);
/*     */     
/* 139 */     for (int i = 0; (i < 256) && (i < allBytesLen); i++) {
/* 140 */       char c = allBytesString.charAt(i);
/* 141 */       this.byteToChars[i] = c;
/* 142 */       this.charToByteMap[c] = allBytes[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public final byte[] toBytes(char[] c) {
/* 147 */     if (c == null) {
/* 148 */       return null;
/*     */     }
/*     */     
/* 151 */     int length = c.length;
/* 152 */     byte[] bytes = new byte[length];
/*     */     
/* 154 */     for (int i = 0; i < length; i++) {
/* 155 */       bytes[i] = this.charToByteMap[c[i]];
/*     */     }
/*     */     
/* 158 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
/* 162 */     if (c == null) {
/* 163 */       return null;
/*     */     }
/*     */     
/* 166 */     int length = c.length + 2;
/* 167 */     int charLength = c.length;
/*     */     
/* 169 */     byte[] bytes = new byte[length];
/* 170 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */     
/* 172 */     for (int i = 0; i < charLength; i++) {
/* 173 */       bytes[(i + 1)] = this.charToByteMap[c[i]];
/*     */     }
/*     */     
/* 176 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */     
/* 178 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytes(char[] chars, int offset, int length) {
/* 182 */     if (chars == null) {
/* 183 */       return null;
/*     */     }
/*     */     
/* 186 */     if (length == 0) {
/* 187 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 190 */     byte[] bytes = new byte[length];
/*     */     
/* 192 */     for (int i = 0; i < length; i++) {
/* 193 */       bytes[i] = this.charToByteMap[chars[(i + offset)]];
/*     */     }
/*     */     
/* 196 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final byte[] toBytes(String s)
/*     */   {
/* 207 */     if (s == null) {
/* 208 */       return null;
/*     */     }
/*     */     
/* 211 */     int length = s.length();
/* 212 */     byte[] bytes = new byte[length];
/*     */     
/* 214 */     for (int i = 0; i < length; i++) {
/* 215 */       bytes[i] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */     
/* 218 */     return bytes;
/*     */   }
/*     */   
/*     */   public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
/* 222 */     if (s == null) {
/* 223 */       return null;
/*     */     }
/*     */     
/* 226 */     int stringLength = s.length();
/*     */     
/* 228 */     int length = stringLength + 2;
/*     */     
/* 230 */     byte[] bytes = new byte[length];
/*     */     
/* 232 */     bytes[0] = this.charToByteMap[beginWrap];
/*     */     
/* 234 */     for (int i = 0; i < stringLength; i++) {
/* 235 */       bytes[(i + 1)] = this.charToByteMap[s.charAt(i)];
/*     */     }
/*     */     
/* 238 */     bytes[(length - 1)] = this.charToByteMap[endWrap];
/*     */     
/* 240 */     return bytes;
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
/*     */   public final byte[] toBytes(String s, int offset, int length)
/*     */   {
/* 256 */     if (s == null) {
/* 257 */       return null;
/*     */     }
/*     */     
/* 260 */     if (length == 0) {
/* 261 */       return EMPTY_BYTE_ARRAY;
/*     */     }
/*     */     
/* 264 */     byte[] bytes = new byte[length];
/*     */     
/* 266 */     for (int i = 0; i < length; i++) {
/* 267 */       char c = s.charAt(i + offset);
/* 268 */       bytes[i] = this.charToByteMap[c];
/*     */     }
/*     */     
/* 271 */     return bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final String toString(byte[] buffer)
/*     */   {
/* 283 */     return toString(buffer, 0, buffer.length);
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
/*     */   public final String toString(byte[] buffer, int startPos, int length)
/*     */   {
/* 299 */     char[] charArray = new char[length];
/* 300 */     int readpoint = startPos;
/*     */     
/* 302 */     for (int i = 0; i < length; i++) {
/* 303 */       charArray[i] = this.byteToChars[(buffer[readpoint] - Byte.MIN_VALUE)];
/* 304 */       readpoint++;
/*     */     }
/*     */     
/* 307 */     return new String(charArray);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\SingleByteCharsetConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */