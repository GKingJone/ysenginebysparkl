/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MimeTypes
/*     */ {
/*     */   public static enum Type
/*     */   {
/*  50 */     FORM_ENCODED("application/x-www-form-urlencoded"), 
/*  51 */     MESSAGE_HTTP("message/http"), 
/*  52 */     MULTIPART_BYTERANGES("multipart/byteranges"), 
/*     */     
/*  54 */     TEXT_HTML("text/html"), 
/*  55 */     TEXT_PLAIN("text/plain"), 
/*  56 */     TEXT_XML("text/xml"), 
/*  57 */     TEXT_JSON("text/json", StandardCharsets.UTF_8), 
/*  58 */     APPLICATION_JSON("application/json", StandardCharsets.UTF_8), 
/*     */     
/*  60 */     TEXT_HTML_8859_1("text/html;charset=iso-8859-1", TEXT_HTML), 
/*  61 */     TEXT_HTML_UTF_8("text/html;charset=utf-8", TEXT_HTML), 
/*     */     
/*  63 */     TEXT_PLAIN_8859_1("text/plain;charset=iso-8859-1", TEXT_PLAIN), 
/*  64 */     TEXT_PLAIN_UTF_8("text/plain;charset=utf-8", TEXT_PLAIN), 
/*     */     
/*  66 */     TEXT_XML_8859_1("text/xml;charset=iso-8859-1", TEXT_XML), 
/*  67 */     TEXT_XML_UTF_8("text/xml;charset=utf-8", TEXT_XML), 
/*     */     
/*  69 */     TEXT_JSON_8859_1("text/json;charset=iso-8859-1", TEXT_JSON), 
/*  70 */     TEXT_JSON_UTF_8("text/json;charset=utf-8", TEXT_JSON), 
/*     */     
/*  72 */     APPLICATION_JSON_8859_1("text/json;charset=iso-8859-1", APPLICATION_JSON), 
/*  73 */     APPLICATION_JSON_UTF_8("text/json;charset=utf-8", APPLICATION_JSON);
/*     */     
/*     */ 
/*     */     private final String _string;
/*     */     
/*     */     private final Type _base;
/*     */     
/*     */     private final ByteBuffer _buffer;
/*     */     private final Charset _charset;
/*     */     private final String _charsetString;
/*     */     private final boolean _assumedCharset;
/*     */     private final HttpField _field;
/*     */     
/*     */     private Type(String s)
/*     */     {
/*  88 */       this._string = s;
/*  89 */       this._buffer = BufferUtil.toBuffer(s);
/*  90 */       this._base = this;
/*  91 */       this._charset = null;
/*  92 */       this._charsetString = null;
/*  93 */       this._assumedCharset = false;
/*  94 */       this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
/*     */     }
/*     */     
/*     */ 
/*     */     private Type(String s, Type base)
/*     */     {
/* 100 */       this._string = s;
/* 101 */       this._buffer = BufferUtil.toBuffer(s);
/* 102 */       this._base = base;
/* 103 */       int i = s.indexOf(";charset=");
/* 104 */       this._charset = Charset.forName(s.substring(i + 9));
/* 105 */       this._charsetString = this._charset.toString().toLowerCase(Locale.ENGLISH);
/* 106 */       this._assumedCharset = false;
/* 107 */       this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
/*     */     }
/*     */     
/*     */ 
/*     */     private Type(String s, Charset cs)
/*     */     {
/* 113 */       this._string = s;
/* 114 */       this._base = this;
/* 115 */       this._buffer = BufferUtil.toBuffer(s);
/* 116 */       this._charset = cs;
/* 117 */       this._charsetString = (this._charset == null ? null : this._charset.toString().toLowerCase(Locale.ENGLISH));
/* 118 */       this._assumedCharset = true;
/* 119 */       this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
/*     */     }
/*     */     
/*     */ 
/*     */     public ByteBuffer asBuffer()
/*     */     {
/* 125 */       return this._buffer.asReadOnlyBuffer();
/*     */     }
/*     */     
/*     */ 
/*     */     public Charset getCharset()
/*     */     {
/* 131 */       return this._charset;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getCharsetString()
/*     */     {
/* 137 */       return this._charsetString;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean is(String s)
/*     */     {
/* 143 */       return this._string.equalsIgnoreCase(s);
/*     */     }
/*     */     
/*     */ 
/*     */     public String asString()
/*     */     {
/* 149 */       return this._string;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 156 */       return this._string;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isCharsetAssumed()
/*     */     {
/* 162 */       return this._assumedCharset;
/*     */     }
/*     */     
/*     */ 
/*     */     public HttpField getContentTypeField()
/*     */     {
/* 168 */       return this._field;
/*     */     }
/*     */     
/*     */ 
/*     */     public Type getBaseType()
/*     */     {
/* 174 */       return this._base;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 179 */   private static final Logger LOG = Log.getLogger(MimeTypes.class);
/* 180 */   public static final Trie<Type> CACHE = new ArrayTrie(512);
/* 181 */   private static final Trie<ByteBuffer> TYPES = new ArrayTrie(512);
/* 182 */   private static final Map<String, String> __dftMimeMap = new HashMap();
/* 183 */   private static final Map<String, String> __encodings = new HashMap();
/*     */   
/*     */   static
/*     */   {
/* 187 */     for (Type type : Type.values())
/*     */     {
/* 189 */       CACHE.put(type.toString(), type);
/* 190 */       TYPES.put(type.toString(), type.asBuffer());
/*     */       
/* 192 */       int charset = type.toString().indexOf(";charset=");
/* 193 */       if (charset > 0)
/*     */       {
/* 195 */         String alt = type.toString().replace(";charset=", "; charset=");
/* 196 */         CACHE.put(alt, type);
/* 197 */         TYPES.put(alt, type.asBuffer());
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 203 */       ResourceBundle mime = ResourceBundle.getBundle("com/facebook/presto/jdbc/internal/jetty/http/mime");
/* 204 */       Object i = mime.getKeys();
/* 205 */       while (((Enumeration)i).hasMoreElements())
/*     */       {
/* 207 */         String ext = (String)((Enumeration)i).nextElement();
/* 208 */         String m = mime.getString(ext);
/* 209 */         __dftMimeMap.put(StringUtil.asciiToLowerCase(ext), normalizeMimeType(m));
/*     */       }
/*     */     }
/*     */     catch (MissingResourceException e)
/*     */     {
/* 214 */       LOG.warn(e.toString(), new Object[0]);
/* 215 */       LOG.debug(e);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 220 */       ResourceBundle encoding = ResourceBundle.getBundle("com/facebook/presto/jdbc/internal/jetty/http/encoding");
/* 221 */       Object i = encoding.getKeys();
/* 222 */       while (((Enumeration)i).hasMoreElements())
/*     */       {
/* 224 */         String type = (String)((Enumeration)i).nextElement();
/* 225 */         __encodings.put(type, encoding.getString(type));
/*     */       }
/*     */     }
/*     */     catch (MissingResourceException e)
/*     */     {
/* 230 */       LOG.warn(e.toString(), new Object[0]);
/* 231 */       LOG.debug(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 237 */   private final Map<String, String> _mimeMap = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Map<String, String> getMimeMap()
/*     */   {
/* 249 */     return this._mimeMap;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMimeMap(Map<String, String> mimeMap)
/*     */   {
/* 258 */     this._mimeMap.clear();
/* 259 */     if (mimeMap != null)
/*     */     {
/* 261 */       for (Entry<String, String> ext : mimeMap.entrySet()) {
/* 262 */         this._mimeMap.put(StringUtil.asciiToLowerCase((String)ext.getKey()), normalizeMimeType((String)ext.getValue()));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getMimeByExtension(String filename)
/*     */   {
/* 274 */     String type = null;
/*     */     
/* 276 */     if (filename != null)
/*     */     {
/* 278 */       int i = -1;
/* 279 */       while (type == null)
/*     */       {
/* 281 */         i = filename.indexOf(".", i + 1);
/*     */         
/* 283 */         if ((i < 0) || (i >= filename.length())) {
/*     */           break;
/*     */         }
/* 286 */         String ext = StringUtil.asciiToLowerCase(filename.substring(i + 1));
/* 287 */         if (this._mimeMap != null)
/* 288 */           type = (String)this._mimeMap.get(ext);
/* 289 */         if (type == null) {
/* 290 */           type = (String)__dftMimeMap.get(ext);
/*     */         }
/*     */       }
/*     */     }
/* 294 */     if (type == null)
/*     */     {
/* 296 */       if (this._mimeMap != null)
/* 297 */         type = (String)this._mimeMap.get("*");
/* 298 */       if (type == null) {
/* 299 */         type = (String)__dftMimeMap.get("*");
/*     */       }
/*     */     }
/* 302 */     return type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addMimeMapping(String extension, String type)
/*     */   {
/* 312 */     this._mimeMap.put(StringUtil.asciiToLowerCase(extension), normalizeMimeType(type));
/*     */   }
/*     */   
/*     */ 
/*     */   public static Set<String> getKnownMimeTypes()
/*     */   {
/* 318 */     return new HashSet(__dftMimeMap.values());
/*     */   }
/*     */   
/*     */ 
/*     */   private static String normalizeMimeType(String type)
/*     */   {
/* 324 */     Type t = (Type)CACHE.get(type);
/* 325 */     if (t != null) {
/* 326 */       return t.asString();
/*     */     }
/* 328 */     return StringUtil.asciiToLowerCase(type);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getCharsetFromContentType(String value)
/*     */   {
/* 334 */     if (value == null)
/* 335 */       return null;
/* 336 */     int end = value.length();
/* 337 */     int state = 0;
/* 338 */     int start = 0;
/* 339 */     boolean quote = false;
/* 340 */     for (int i = 0; 
/* 341 */         i < end; i++)
/*     */     {
/* 343 */       char b = value.charAt(i);
/*     */       
/* 345 */       if ((quote) && (state != 10))
/*     */       {
/* 347 */         if ('"' == b) {
/* 348 */           quote = false;
/*     */         }
/*     */       }
/*     */       else
/* 352 */         switch (state)
/*     */         {
/*     */         case 0: 
/* 355 */           if ('"' == b)
/*     */           {
/* 357 */             quote = true;
/*     */ 
/*     */           }
/* 360 */           else if (';' == b)
/* 361 */             state = 1;
/*     */           break;
/*     */         case 1: 
/* 364 */           if ('c' == b) state = 2; else if (' ' != b) state = 0;
/*     */           break; case 2:  if ('h' == b) state = 3; else state = 0;
/*     */           break; case 3:  if ('a' == b) state = 4; else state = 0;
/*     */           break; case 4:  if ('r' == b) state = 5; else state = 0;
/*     */           break; case 5:  if ('s' == b) state = 6; else state = 0;
/*     */           break; case 6:  if ('e' == b) state = 7; else state = 0;
/*     */           break; case 7:  if ('t' == b) state = 8; else state = 0;
/*     */           break;
/* 372 */         case 8:  if ('=' == b) state = 9; else if (' ' != b) state = 0;
/*     */           break;
/*     */         case 9: 
/* 375 */           if (' ' != b)
/*     */           {
/* 377 */             if ('"' == b)
/*     */             {
/* 379 */               quote = true;
/* 380 */               start = i + 1;
/* 381 */               state = 10;
/*     */             }
/*     */             else {
/* 384 */               start = i;
/* 385 */               state = 10; } }
/* 386 */           break;
/*     */         
/*     */         case 10: 
/* 389 */           if (((!quote) && ((';' == b) || (' ' == b))) || ((quote) && ('"' == b)))
/*     */           {
/* 391 */             return StringUtil.normalizeCharset(value, start, i - start); }
/*     */           break;
/*     */         }
/*     */     }
/* 395 */     if (state == 10) {
/* 396 */       return StringUtil.normalizeCharset(value, start, i - start);
/*     */     }
/* 398 */     return null;
/*     */   }
/*     */   
/*     */   public static String inferCharsetFromContentType(String value)
/*     */   {
/* 403 */     return (String)__encodings.get(value);
/*     */   }
/*     */   
/*     */   public static String getContentTypeWithoutCharset(String value)
/*     */   {
/* 408 */     int end = value.length();
/* 409 */     int state = 0;
/* 410 */     int start = 0;
/* 411 */     boolean quote = false;
/* 412 */     int i = 0;
/* 413 */     StringBuilder builder = null;
/* 414 */     for (; i < end; i++)
/*     */     {
/* 416 */       char b = value.charAt(i);
/*     */       
/* 418 */       if ('"' == b)
/*     */       {
/* 420 */         if (quote)
/*     */         {
/* 422 */           quote = false;
/*     */         }
/*     */         else
/*     */         {
/* 426 */           quote = true;
/*     */         }
/*     */         
/* 429 */         switch (state)
/*     */         {
/*     */         case 11: 
/* 432 */           builder.append(b); break;
/*     */         case 10: 
/*     */           break;
/*     */         case 9: 
/* 436 */           builder = new StringBuilder();
/* 437 */           builder.append(value, 0, start + 1);
/* 438 */           state = 10;
/* 439 */           break;
/*     */         default: 
/* 441 */           start = i;
/* 442 */           state = 0;
/*     */           
/* 444 */           break;
/*     */         }
/*     */       }
/* 447 */       else if (quote)
/*     */       {
/* 449 */         if ((builder != null) && (state != 10)) {
/* 450 */           builder.append(b);
/*     */         }
/*     */       }
/*     */       else {
/* 454 */         switch (state)
/*     */         {
/*     */         case 0: 
/* 457 */           if (';' == b) {
/* 458 */             state = 1;
/* 459 */           } else if (' ' != b)
/* 460 */             start = i;
/*     */           break;
/*     */         case 1: 
/* 463 */           if ('c' == b) state = 2; else if (' ' != b) state = 0;
/*     */           break; case 2:  if ('h' == b) state = 3; else state = 0;
/*     */           break; case 3:  if ('a' == b) state = 4; else state = 0;
/*     */           break; case 4:  if ('r' == b) state = 5; else state = 0;
/*     */           break; case 5:  if ('s' == b) state = 6; else state = 0;
/*     */           break; case 6:  if ('e' == b) state = 7; else state = 0;
/*     */           break; case 7:  if ('t' == b) state = 8; else state = 0;
/*     */           break; case 8:  if ('=' == b) state = 9; else if (' ' != b) state = 0;
/*     */           break;
/*     */         case 9: 
/* 473 */           if (' ' != b)
/*     */           {
/* 475 */             builder = new StringBuilder();
/* 476 */             builder.append(value, 0, start + 1);
/* 477 */             state = 10; }
/* 478 */           break;
/*     */         
/*     */         case 10: 
/* 481 */           if (';' == b)
/*     */           {
/* 483 */             builder.append(b);
/* 484 */             state = 11;
/*     */           }
/*     */           break;
/*     */         case 11: 
/* 488 */           if (' ' != b)
/* 489 */             builder.append(b);
/*     */           break; }
/*     */       } }
/* 492 */     if (builder == null)
/* 493 */       return value;
/* 494 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\MimeTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */