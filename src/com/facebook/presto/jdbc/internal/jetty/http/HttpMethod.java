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
/*     */ public enum HttpMethod
/*     */ {
/*  33 */   GET, 
/*  34 */   POST, 
/*  35 */   HEAD, 
/*  36 */   PUT, 
/*  37 */   OPTIONS, 
/*  38 */   DELETE, 
/*  39 */   TRACE, 
/*  40 */   CONNECT, 
/*  41 */   MOVE, 
/*  42 */   PROXY, 
/*  43 */   PRI;
/*     */   
/*     */ 
/*     */   public static final Trie<HttpMethod> CACHE;
/*     */   
/*     */   private final ByteBuffer _buffer;
/*     */   
/*     */   private final byte[] _bytes;
/*     */   
/*     */ 
/*     */   public static HttpMethod lookAheadGet(byte[] bytes, int position, int limit)
/*     */   {
/*  55 */     int length = limit - position;
/*  56 */     if (length < 4)
/*  57 */       return null;
/*  58 */     switch (bytes[position])
/*     */     {
/*     */     case 71: 
/*  61 */       if ((bytes[(position + 1)] == 69) && (bytes[(position + 2)] == 84) && (bytes[(position + 3)] == 32))
/*  62 */         return GET;
/*     */       break;
/*     */     case 80: 
/*  65 */       if ((bytes[(position + 1)] == 79) && (bytes[(position + 2)] == 83) && (bytes[(position + 3)] == 84) && (length >= 5) && (bytes[(position + 4)] == 32))
/*  66 */         return POST;
/*  67 */       if ((bytes[(position + 1)] == 82) && (bytes[(position + 2)] == 79) && (bytes[(position + 3)] == 88) && (length >= 6) && (bytes[(position + 4)] == 89) && (bytes[(position + 5)] == 32))
/*  68 */         return PROXY;
/*  69 */       if ((bytes[(position + 1)] == 85) && (bytes[(position + 2)] == 84) && (bytes[(position + 3)] == 32))
/*  70 */         return PUT;
/*  71 */       if ((bytes[(position + 1)] == 82) && (bytes[(position + 2)] == 73) && (bytes[(position + 3)] == 32))
/*  72 */         return PRI;
/*     */       break;
/*     */     case 72: 
/*  75 */       if ((bytes[(position + 1)] == 69) && (bytes[(position + 2)] == 65) && (bytes[(position + 3)] == 68) && (length >= 5) && (bytes[(position + 4)] == 32))
/*  76 */         return HEAD;
/*     */       break;
/*     */     case 79: 
/*  79 */       if ((bytes[(position + 1)] == 80) && (bytes[(position + 2)] == 84) && (bytes[(position + 3)] == 73) && (length >= 8) && (bytes[(position + 4)] == 79) && (bytes[(position + 5)] == 78) && (bytes[(position + 6)] == 83) && (bytes[(position + 7)] == 32))
/*     */       {
/*  81 */         return OPTIONS; }
/*     */       break;
/*     */     case 68: 
/*  84 */       if ((bytes[(position + 1)] == 69) && (bytes[(position + 2)] == 76) && (bytes[(position + 3)] == 69) && (length >= 7) && (bytes[(position + 4)] == 84) && (bytes[(position + 5)] == 69) && (bytes[(position + 6)] == 32))
/*     */       {
/*  86 */         return DELETE; }
/*     */       break;
/*     */     case 84: 
/*  89 */       if ((bytes[(position + 1)] == 82) && (bytes[(position + 2)] == 65) && (bytes[(position + 3)] == 67) && (length >= 6) && (bytes[(position + 4)] == 69) && (bytes[(position + 5)] == 32))
/*     */       {
/*  91 */         return TRACE; }
/*     */       break;
/*     */     case 67: 
/*  94 */       if ((bytes[(position + 1)] == 79) && (bytes[(position + 2)] == 78) && (bytes[(position + 3)] == 78) && (length >= 8) && (bytes[(position + 4)] == 69) && (bytes[(position + 5)] == 67) && (bytes[(position + 6)] == 84) && (bytes[(position + 7)] == 32))
/*     */       {
/*  96 */         return CONNECT; }
/*     */       break;
/*     */     case 77: 
/*  99 */       if ((bytes[(position + 1)] == 79) && (bytes[(position + 2)] == 86) && (bytes[(position + 3)] == 69) && (length >= 5) && (bytes[(position + 4)] == 32)) {
/* 100 */         return MOVE;
/*     */       }
/*     */       
/*     */       break;
/*     */     }
/*     */     
/* 106 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static HttpMethod lookAheadGet(ByteBuffer buffer)
/*     */   {
/* 117 */     if (buffer.hasArray()) {
/* 118 */       return lookAheadGet(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.arrayOffset() + buffer.limit());
/*     */     }
/* 120 */     int l = buffer.remaining();
/* 121 */     if (l >= 4)
/*     */     {
/* 123 */       HttpMethod m = (HttpMethod)CACHE.getBest(buffer, 0, l);
/* 124 */       if (m != null)
/*     */       {
/* 126 */         int ml = m.asString().length();
/* 127 */         if ((l > ml) && (buffer.get(buffer.position() + ml) == 32))
/* 128 */           return m;
/*     */       }
/*     */     }
/* 131 */     return null;
/*     */   }
/*     */   
/*     */   static {
/* 135 */     CACHE = new ArrayTrie();
/*     */     
/*     */ 
/* 138 */     for (HttpMethod method : values()) {
/* 139 */       CACHE.put(method.toString(), method);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private HttpMethod()
/*     */   {
/* 149 */     this._bytes = StringUtil.getBytes(toString());
/* 150 */     this._buffer = ByteBuffer.wrap(this._bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/* 156 */     return this._bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean is(String s)
/*     */   {
/* 162 */     return toString().equalsIgnoreCase(s);
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer asBuffer()
/*     */   {
/* 168 */     return this._buffer.asReadOnlyBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public String asString()
/*     */   {
/* 174 */     return toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static HttpMethod fromString(String method)
/*     */   {
/* 185 */     return (HttpMethod)CACHE.get(method);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */