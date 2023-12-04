/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.EnumSet;
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
/*     */ public enum HttpHeaderValue
/*     */ {
/*  34 */   CLOSE("close"), 
/*  35 */   CHUNKED("chunked"), 
/*  36 */   GZIP("gzip"), 
/*  37 */   IDENTITY("identity"), 
/*  38 */   KEEP_ALIVE("keep-alive"), 
/*  39 */   CONTINUE("100-continue"), 
/*  40 */   PROCESSING("102-processing"), 
/*  41 */   TE("TE"), 
/*  42 */   BYTES("bytes"), 
/*  43 */   NO_CACHE("no-cache"), 
/*  44 */   UPGRADE("Upgrade"), 
/*  45 */   UNKNOWN("::UNKNOWN::");
/*     */   
/*     */   static {
/*  48 */     CACHE = new ArrayTrie();
/*     */     
/*     */ 
/*  51 */     for (HttpHeaderValue value : values()) {
/*  52 */       if (value != UNKNOWN) {
/*  53 */         CACHE.put(value.toString(), value);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static final Trie<HttpHeaderValue> CACHE;
/*     */   private final String _string;
/*     */   private final ByteBuffer _buffer;
/*     */   private HttpHeaderValue(String s) {
/*  62 */     this._string = s;
/*  63 */     this._buffer = BufferUtil.toBuffer(s);
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer toBuffer()
/*     */   {
/*  69 */     return this._buffer.asReadOnlyBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean is(String s)
/*     */   {
/*  75 */     return this._string.equalsIgnoreCase(s);
/*     */   }
/*     */   
/*     */ 
/*     */   public String asString()
/*     */   {
/*  81 */     return this._string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  88 */     return this._string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  93 */   private static EnumSet<HttpHeader> __known = EnumSet.of(HttpHeader.CONNECTION, HttpHeader.TRANSFER_ENCODING, HttpHeader.CONTENT_ENCODING);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasKnownValues(HttpHeader header)
/*     */   {
/* 100 */     if (header == null)
/* 101 */       return false;
/* 102 */     return __known.contains(header);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpHeaderValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */