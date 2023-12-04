/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField.IntValueHttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpStatus.Code;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpURI;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.PreEncodedHttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ public class HpackEncoder
/*     */ {
/*  40 */   public static final Logger LOG = Log.getLogger(HpackEncoder.class);
/*     */   
/*  42 */   private static final HttpField[] __status = new HttpField['ɗ'];
/*     */   
/*     */ 
/*     */ 
/*  46 */   static final EnumSet<HttpHeader> __DO_NOT_HUFFMAN = EnumSet.of(HttpHeader.AUTHORIZATION, HttpHeader.CONTENT_MD5, HttpHeader.PROXY_AUTHENTICATE, HttpHeader.PROXY_AUTHORIZATION);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */   static final EnumSet<HttpHeader> __DO_NOT_INDEX = EnumSet.of(HttpHeader.AUTHORIZATION, new HttpHeader[] { HttpHeader.CONTENT_MD5, HttpHeader.CONTENT_RANGE, HttpHeader.ETAG, HttpHeader.IF_MODIFIED_SINCE, HttpHeader.IF_UNMODIFIED_SINCE, HttpHeader.IF_NONE_MATCH, HttpHeader.IF_RANGE, HttpHeader.IF_MATCH, HttpHeader.LOCATION, HttpHeader.RANGE, HttpHeader.RETRY_AFTER, HttpHeader.LAST_MODIFIED, HttpHeader.SET_COOKIE, HttpHeader.SET_COOKIE2 });
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
/*  75 */   static final EnumSet<HttpHeader> __NEVER_INDEX = EnumSet.of(HttpHeader.AUTHORIZATION, HttpHeader.SET_COOKIE, HttpHeader.SET_COOKIE2);
/*     */   private final HpackContext _context;
/*     */   private final boolean _debug;
/*     */   private int _remoteMaxDynamicTableSize;
/*     */   private int _localMaxDynamicTableSize;
/*     */   
/*     */   static {
/*  82 */     for (HttpStatus.Code code : HttpStatus.Code.values()) {
/*  83 */       __status[code.getCode()] = new PreEncodedHttpField(HttpHeader.C_STATUS, Integer.toString(code.getCode()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HpackEncoder()
/*     */   {
/*  93 */     this(4096, 4096);
/*     */   }
/*     */   
/*     */   public HpackEncoder(int localMaxDynamicTableSize)
/*     */   {
/*  98 */     this(localMaxDynamicTableSize, 4096);
/*     */   }
/*     */   
/*     */   public HpackEncoder(int localMaxDynamicTableSize, int remoteMaxDynamicTableSize)
/*     */   {
/* 103 */     this._context = new HpackContext(remoteMaxDynamicTableSize);
/* 104 */     this._remoteMaxDynamicTableSize = remoteMaxDynamicTableSize;
/* 105 */     this._localMaxDynamicTableSize = localMaxDynamicTableSize;
/* 106 */     this._debug = LOG.isDebugEnabled();
/*     */   }
/*     */   
/*     */   public HpackContext getHpackContext()
/*     */   {
/* 111 */     return this._context;
/*     */   }
/*     */   
/*     */   public void setRemoteMaxDynamicTableSize(int remoteMaxDynamicTableSize)
/*     */   {
/* 116 */     this._remoteMaxDynamicTableSize = remoteMaxDynamicTableSize;
/*     */   }
/*     */   
/*     */   public void setLocalMaxDynamicTableSize(int localMaxDynamicTableSize)
/*     */   {
/* 121 */     this._localMaxDynamicTableSize = localMaxDynamicTableSize;
/*     */   }
/*     */   
/*     */   public void encode(ByteBuffer buffer, MetaData metadata)
/*     */   {
/* 126 */     if (LOG.isDebugEnabled()) {
/* 127 */       LOG.debug(String.format("CtxTbl[%x] encoding", new Object[] { Integer.valueOf(this._context.hashCode()) }), new Object[0]);
/*     */     }
/* 129 */     int pos = buffer.position();
/*     */     
/*     */ 
/* 132 */     int maxDynamicTableSize = Math.min(this._remoteMaxDynamicTableSize, this._localMaxDynamicTableSize);
/* 133 */     if (maxDynamicTableSize != this._context.getMaxDynamicTableSize()) {
/* 134 */       encodeMaxDynamicTableSize(buffer, maxDynamicTableSize);
/*     */     }
/*     */     Response response;
/* 137 */     if (metadata.isRequest())
/*     */     {
/* 139 */       Request request = (Request)metadata;
/*     */       
/*     */ 
/* 142 */       String scheme = request.getURI().getScheme();
/* 143 */       encode(buffer, new HttpField(HttpHeader.C_SCHEME, scheme == null ? HttpScheme.HTTP.asString() : scheme));
/* 144 */       encode(buffer, new HttpField(HttpHeader.C_METHOD, request.getMethod()));
/* 145 */       encode(buffer, new HttpField(HttpHeader.C_AUTHORITY, request.getURI().getAuthority()));
/* 146 */       encode(buffer, new HttpField(HttpHeader.C_PATH, request.getURI().getPathQuery()));
/*     */ 
/*     */     }
/* 149 */     else if (metadata.isResponse())
/*     */     {
/* 151 */       response = (Response)metadata;
/* 152 */       int code = response.getStatus();
/* 153 */       HttpField status = code < __status.length ? __status[code] : null;
/* 154 */       if (status == null)
/* 155 */         status = new IntValueHttpField(HttpHeader.C_STATUS, code);
/* 156 */       encode(buffer, status);
/*     */     }
/*     */     
/*     */ 
/* 160 */     for (HttpField field : metadata) {
/* 161 */       encode(buffer, field);
/*     */     }
/* 163 */     if (LOG.isDebugEnabled()) {
/* 164 */       LOG.debug(String.format("CtxTbl[%x] encoded %d octets", new Object[] { Integer.valueOf(this._context.hashCode()), Integer.valueOf(buffer.position() - pos) }), new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void encodeMaxDynamicTableSize(ByteBuffer buffer, int maxDynamicTableSize) {
/* 169 */     if (maxDynamicTableSize > this._remoteMaxDynamicTableSize)
/* 170 */       throw new IllegalArgumentException();
/* 171 */     buffer.put((byte)32);
/* 172 */     NBitInteger.encode(buffer, 5, maxDynamicTableSize);
/* 173 */     this._context.resize(maxDynamicTableSize);
/*     */   }
/*     */   
/*     */   public void encode(ByteBuffer buffer, HttpField field)
/*     */   {
/* 178 */     int p = this._debug ? buffer.position() : -1;
/*     */     
/* 180 */     String encoding = null;
/*     */     
/*     */ 
/* 183 */     HpackContext.Entry entry = this._context.get(field);
/* 184 */     if (entry != null)
/*     */     {
/*     */ 
/* 187 */       if (entry.isStatic())
/*     */       {
/* 189 */         buffer.put(((HpackContext.StaticEntry)entry).getEncodedField());
/* 190 */         if (this._debug) {
/* 191 */           encoding = "IdxFieldS1";
/*     */         }
/*     */       }
/*     */       else {
/* 195 */         int index = this._context.index(entry);
/* 196 */         buffer.put((byte)Byte.MIN_VALUE);
/* 197 */         NBitInteger.encode(buffer, 7, index);
/* 198 */         if (this._debug) {
/* 199 */           encoding = "IdxField" + (entry.isStatic() ? "S" : "") + (1 + NBitInteger.octectsNeeded(7, index));
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 208 */       HttpHeader header = field.getHeader();
/*     */       
/*     */       boolean indexed;
/* 211 */       if (header == null)
/*     */       {
/*     */ 
/* 214 */         HpackContext.Entry name = this._context.get(field.getName());
/*     */         
/* 216 */         if ((field instanceof PreEncodedHttpField))
/*     */         {
/* 218 */           int i = buffer.position();
/* 219 */           ((PreEncodedHttpField)field).putTo(buffer, HttpVersion.HTTP_2);
/* 220 */           byte b = buffer.get(i);
/* 221 */           boolean indexed = (b < 0) || (b >= 64);
/* 222 */           if (this._debug) {
/* 223 */             encoding = indexed ? "PreEncodedIdx" : "PreEncoded";
/*     */           }
/*     */         }
/* 226 */         else if (name == null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 231 */           boolean indexed = true;
/* 232 */           encodeName(buffer, (byte)64, 6, field.getName(), null);
/* 233 */           encodeValue(buffer, true, field.getValue());
/* 234 */           if (this._debug) {
/* 235 */             encoding = "LitHuffNHuffVIdx";
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 241 */           boolean indexed = false;
/* 242 */           encodeName(buffer, (byte)0, 4, field.getName(), null);
/* 243 */           encodeValue(buffer, true, field.getValue());
/* 244 */           if (this._debug) {
/* 245 */             encoding = "LitHuffNHuffV!Idx";
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 251 */         HpackContext.Entry name = this._context.get(header);
/*     */         
/* 253 */         if ((field instanceof PreEncodedHttpField))
/*     */         {
/*     */ 
/* 256 */           int i = buffer.position();
/* 257 */           ((PreEncodedHttpField)field).putTo(buffer, HttpVersion.HTTP_2);
/* 258 */           byte b = buffer.get(i);
/* 259 */           boolean indexed = (b < 0) || (b >= 64);
/* 260 */           if (this._debug) {
/* 261 */             encoding = indexed ? "PreEncodedIdx" : "PreEncoded";
/*     */           }
/* 263 */         } else if (__DO_NOT_INDEX.contains(header))
/*     */         {
/*     */ 
/* 266 */           boolean indexed = false;
/* 267 */           boolean never_index = __NEVER_INDEX.contains(header);
/* 268 */           boolean huffman = !__DO_NOT_HUFFMAN.contains(header);
/* 269 */           encodeName(buffer, (byte)(never_index ? 16 : 0), 4, header.asString(), name);
/* 270 */           encodeValue(buffer, huffman, field.getValue());
/*     */           
/* 272 */           if (this._debug)
/*     */           {
/* 274 */             encoding = "Lit" + (name == null ? "HuffN" : new StringBuilder().append("IdxN").append(name.isStatic() ? "S" : "").append(1 + NBitInteger.octectsNeeded(4, this._context.index(name))).toString()) + (huffman ? "HuffV" : "LitV") + (never_index ? "!!Idx" : indexed ? "Idx" : "!Idx");
/*     */           }
/*     */           
/*     */         }
/* 278 */         else if ((header == HttpHeader.CONTENT_LENGTH) && (field.getValue().length() > 1))
/*     */         {
/*     */ 
/* 281 */           boolean indexed = false;
/* 282 */           encodeName(buffer, (byte)0, 4, header.asString(), name);
/* 283 */           encodeValue(buffer, true, field.getValue());
/* 284 */           if (this._debug) {
/* 285 */             encoding = "LitIdxNS" + (1 + NBitInteger.octectsNeeded(4, this._context.index(name))) + "HuffV!Idx";
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 290 */           indexed = true;
/* 291 */           boolean huffman = !__DO_NOT_HUFFMAN.contains(header);
/* 292 */           encodeName(buffer, (byte)64, 6, header.asString(), name);
/* 293 */           encodeValue(buffer, huffman, field.getValue());
/* 294 */           if (this._debug) {
/* 295 */             encoding = (name == null ? "LitHuffN" : new StringBuilder().append("LitIdxN").append(name.isStatic() ? "S" : "").append(1 + NBitInteger.octectsNeeded(6, this._context.index(name))).toString()) + (huffman ? "HuffVIdx" : "LitVIdx");
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 302 */       if (indexed) {
/* 303 */         this._context.add(field);
/*     */       }
/*     */     }
/* 306 */     if (this._debug)
/*     */     {
/* 308 */       int e = buffer.position();
/* 309 */       if (LOG.isDebugEnabled()) {
/* 310 */         LOG.debug("encode {}:'{}' to '{}'", new Object[] { encoding, field, TypeUtil.toHexString(buffer.array(), buffer.arrayOffset() + p, e - p) });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void encodeName(ByteBuffer buffer, byte mask, int bits, String name, HpackContext.Entry entry) {
/* 316 */     buffer.put(mask);
/* 317 */     if (entry == null)
/*     */     {
/*     */ 
/*     */ 
/* 321 */       buffer.put((byte)Byte.MIN_VALUE);
/* 322 */       NBitInteger.encode(buffer, 7, Huffman.octetsNeededLC(name));
/* 323 */       Huffman.encodeLC(buffer, name);
/*     */     }
/*     */     else
/*     */     {
/* 327 */       NBitInteger.encode(buffer, bits, this._context.index(entry));
/*     */     }
/*     */   }
/*     */   
/*     */   static void encodeValue(ByteBuffer buffer, boolean huffman, String value)
/*     */   {
/* 333 */     if (huffman)
/*     */     {
/*     */ 
/* 336 */       buffer.put((byte)Byte.MIN_VALUE);
/* 337 */       NBitInteger.encode(buffer, 7, Huffman.octetsNeeded(value));
/* 338 */       Huffman.encode(buffer, value);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 343 */       buffer.put((byte)0);
/* 344 */       NBitInteger.encode(buffer, 7, value.length());
/* 345 */       for (int i = 0; i < value.length(); i++)
/*     */       {
/* 347 */         char c = value.charAt(i);
/* 348 */         if ((c < ' ') || (c > ''))
/* 349 */           throw new IllegalArgumentException();
/* 350 */         buffer.put((byte)c);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\HpackEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */