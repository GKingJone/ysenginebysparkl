/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum HttpHeader
/*     */ {
/*  33 */   CONNECTION("Connection"), 
/*  34 */   CACHE_CONTROL("Cache-Control"), 
/*  35 */   DATE("Date"), 
/*  36 */   PRAGMA("Pragma"), 
/*  37 */   PROXY_CONNECTION("Proxy-Connection"), 
/*  38 */   TRAILER("Trailer"), 
/*  39 */   TRANSFER_ENCODING("Transfer-Encoding"), 
/*  40 */   UPGRADE("Upgrade"), 
/*  41 */   VIA("Via"), 
/*  42 */   WARNING("Warning"), 
/*  43 */   NEGOTIATE("Negotiate"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  48 */   ALLOW("Allow"), 
/*  49 */   CONTENT_ENCODING("Content-Encoding"), 
/*  50 */   CONTENT_LANGUAGE("Content-Language"), 
/*  51 */   CONTENT_LENGTH("Content-Length"), 
/*  52 */   CONTENT_LOCATION("Content-Location"), 
/*  53 */   CONTENT_MD5("Content-MD5"), 
/*  54 */   CONTENT_RANGE("Content-Range"), 
/*  55 */   CONTENT_TYPE("Content-Type"), 
/*  56 */   EXPIRES("Expires"), 
/*  57 */   LAST_MODIFIED("Last-Modified"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  62 */   ACCEPT("Accept"), 
/*  63 */   ACCEPT_CHARSET("Accept-Charset"), 
/*  64 */   ACCEPT_ENCODING("Accept-Encoding"), 
/*  65 */   ACCEPT_LANGUAGE("Accept-Language"), 
/*  66 */   AUTHORIZATION("Authorization"), 
/*  67 */   EXPECT("Expect"), 
/*  68 */   FORWARDED("Forwarded"), 
/*  69 */   FROM("From"), 
/*  70 */   HOST("Host"), 
/*  71 */   IF_MATCH("If-Match"), 
/*  72 */   IF_MODIFIED_SINCE("If-Modified-Since"), 
/*  73 */   IF_NONE_MATCH("If-None-Match"), 
/*  74 */   IF_RANGE("If-Range"), 
/*  75 */   IF_UNMODIFIED_SINCE("If-Unmodified-Since"), 
/*  76 */   KEEP_ALIVE("Keep-Alive"), 
/*  77 */   MAX_FORWARDS("Max-Forwards"), 
/*  78 */   PROXY_AUTHORIZATION("Proxy-Authorization"), 
/*  79 */   RANGE("Range"), 
/*  80 */   REQUEST_RANGE("Request-Range"), 
/*  81 */   REFERER("Referer"), 
/*  82 */   TE("TE"), 
/*  83 */   USER_AGENT("User-Agent"), 
/*  84 */   X_FORWARDED_FOR("X-Forwarded-For"), 
/*  85 */   X_FORWARDED_PROTO("X-Forwarded-Proto"), 
/*  86 */   X_FORWARDED_SERVER("X-Forwarded-Server"), 
/*  87 */   X_FORWARDED_HOST("X-Forwarded-Host"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  92 */   ACCEPT_RANGES("Accept-Ranges"), 
/*  93 */   AGE("Age"), 
/*  94 */   ETAG("ETag"), 
/*  95 */   LOCATION("Location"), 
/*  96 */   PROXY_AUTHENTICATE("Proxy-Authenticate"), 
/*  97 */   RETRY_AFTER("Retry-After"), 
/*  98 */   SERVER("Server"), 
/*  99 */   SERVLET_ENGINE("Servlet-Engine"), 
/* 100 */   VARY("Vary"), 
/* 101 */   WWW_AUTHENTICATE("WWW-Authenticate"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 106 */   COOKIE("Cookie"), 
/* 107 */   SET_COOKIE("Set-Cookie"), 
/* 108 */   SET_COOKIE2("Set-Cookie2"), 
/* 109 */   MIME_VERSION("MIME-Version"), 
/* 110 */   IDENTITY("identity"), 
/*     */   
/* 112 */   X_POWERED_BY("X-Powered-By"), 
/* 113 */   HTTP2_SETTINGS("HTTP2-Settings"), 
/*     */   
/* 115 */   STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"), 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 120 */   C_METHOD(":method"), 
/* 121 */   C_SCHEME(":scheme"), 
/* 122 */   C_AUTHORITY(":authority"), 
/* 123 */   C_PATH(":path"), 
/* 124 */   C_STATUS(":status"), 
/*     */   
/* 126 */   UNKNOWN("::UNKNOWN::");
/*     */   
/*     */   public static final Trie<HttpHeader> CACHE;
/*     */   
/* 130 */   static { CACHE = new ArrayTrie(560);
/*     */     
/*     */ 
/* 133 */     for (HttpHeader header : values()) {
/* 134 */       if ((header != UNKNOWN) && 
/* 135 */         (!CACHE.put(header.toString(), header))) {
/* 136 */         throw new IllegalStateException();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final String _string;
/*     */   private final byte[] _bytes;
/*     */   private final byte[] _bytesColonSpace;
/*     */   private final ByteBuffer _buffer;
/*     */   private HttpHeader(String s)
/*     */   {
/* 147 */     this._string = s;
/* 148 */     this._bytes = StringUtil.getBytes(s);
/* 149 */     this._bytesColonSpace = StringUtil.getBytes(s + ": ");
/* 150 */     this._buffer = ByteBuffer.wrap(this._bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer toBuffer()
/*     */   {
/* 156 */     return this._buffer.asReadOnlyBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/* 162 */     return this._bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBytesColonSpace()
/*     */   {
/* 168 */     return this._bytesColonSpace;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean is(String s)
/*     */   {
/* 174 */     return this._string.equalsIgnoreCase(s);
/*     */   }
/*     */   
/*     */ 
/*     */   public String asString()
/*     */   {
/* 180 */     return this._string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 187 */     return this._string;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */