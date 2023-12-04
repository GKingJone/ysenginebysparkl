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
/*     */ public enum HttpVersion
/*     */ {
/*  31 */   HTTP_0_9("HTTP/0.9", 9), 
/*  32 */   HTTP_1_0("HTTP/1.0", 10), 
/*  33 */   HTTP_1_1("HTTP/1.1", 11), 
/*  34 */   HTTP_2("HTTP/2.0", 20);
/*     */   
/*     */   static {
/*  37 */     CACHE = new ArrayTrie();
/*     */     
/*     */ 
/*  40 */     for (HttpVersion version : values()) {
/*  41 */       CACHE.put(version.toString(), version);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static final Trie<HttpVersion> CACHE;
/*     */   
/*     */   private final String _string;
/*     */   private final byte[] _bytes;
/*     */   private final ByteBuffer _buffer;
/*     */   private final int _version;
/*     */   public static HttpVersion lookAheadGet(byte[] bytes, int position, int limit)
/*     */   {
/*  54 */     int length = limit - position;
/*  55 */     if (length < 9) {
/*  56 */       return null;
/*     */     }
/*  58 */     if ((bytes[(position + 4)] == 47) && (bytes[(position + 6)] == 46) && (Character.isWhitespace((char)bytes[(position + 8)])) && (((bytes[position] == 72) && (bytes[(position + 1)] == 84) && (bytes[(position + 2)] == 84) && (bytes[(position + 3)] == 80)) || ((bytes[position] == 104) && (bytes[(position + 1)] == 116) && (bytes[(position + 2)] == 116) && (bytes[(position + 3)] == 112))))
/*     */     {
/*     */ 
/*     */ 
/*  62 */       switch (bytes[(position + 5)])
/*     */       {
/*     */       case 49: 
/*  65 */         switch (bytes[(position + 7)])
/*     */         {
/*     */         case 48: 
/*  68 */           return HTTP_1_0;
/*     */         case 49: 
/*  70 */           return HTTP_1_1;
/*     */         }
/*  72 */         break;
/*     */       case 50: 
/*  74 */         switch (bytes[(position + 7)])
/*     */         {
/*     */         case 48: 
/*  77 */           return HTTP_2;
/*     */         }
/*     */         
/*     */         break;
/*     */       }
/*     */     }
/*  83 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static HttpVersion lookAheadGet(ByteBuffer buffer)
/*     */   {
/*  94 */     if (buffer.hasArray())
/*  95 */       return lookAheadGet(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.arrayOffset() + buffer.limit());
/*  96 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private HttpVersion(String s, int version)
/*     */   {
/* 108 */     this._string = s;
/* 109 */     this._bytes = StringUtil.getBytes(s);
/* 110 */     this._buffer = ByteBuffer.wrap(this._bytes);
/* 111 */     this._version = version;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] toBytes()
/*     */   {
/* 117 */     return this._bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer toBuffer()
/*     */   {
/* 123 */     return this._buffer.asReadOnlyBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 129 */     return this._version;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean is(String s)
/*     */   {
/* 135 */     return this._string.equalsIgnoreCase(s);
/*     */   }
/*     */   
/*     */ 
/*     */   public String asString()
/*     */   {
/* 141 */     return this._string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 148 */     return this._string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static HttpVersion fromString(String version)
/*     */   {
/* 158 */     return (HttpVersion)CACHE.get(version);
/*     */   }
/*     */   
/*     */ 
/*     */   public static HttpVersion fromVersion(int version)
/*     */   {
/* 164 */     switch (version) {
/*     */     case 9: 
/* 166 */       return HTTP_0_9;
/* 167 */     case 10:  return HTTP_1_0;
/* 168 */     case 11:  return HTTP_1_1;
/* 169 */     case 20:  return HTTP_2; }
/* 170 */     throw new IllegalArgumentException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */