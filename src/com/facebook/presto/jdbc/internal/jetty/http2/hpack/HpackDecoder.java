/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.BadMessageException;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField.IntValueHttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField.LongValueHttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ public class HpackDecoder
/*     */ {
/*  40 */   public static final Logger LOG = Log.getLogger(HpackDecoder.class);
/*  41 */   public static final LongValueHttpField CONTENT_LENGTH_0 = new LongValueHttpField(HttpHeader.CONTENT_LENGTH, 0L);
/*     */   
/*     */ 
/*     */   private final HpackContext _context;
/*     */   
/*     */ 
/*     */   private final MetaDataBuilder _builder;
/*     */   
/*     */   private int _localMaxDynamicTableSize;
/*     */   
/*     */ 
/*     */   public HpackDecoder(int localMaxDynamicTableSize, int maxHeaderSize)
/*     */   {
/*  54 */     this._context = new HpackContext(localMaxDynamicTableSize);
/*  55 */     this._localMaxDynamicTableSize = localMaxDynamicTableSize;
/*  56 */     this._builder = new MetaDataBuilder(maxHeaderSize);
/*     */   }
/*     */   
/*     */   public HpackContext getHpackContext()
/*     */   {
/*  61 */     return this._context;
/*     */   }
/*     */   
/*     */   public void setLocalMaxDynamicTableSize(int localMaxdynamciTableSize)
/*     */   {
/*  66 */     this._localMaxDynamicTableSize = localMaxdynamciTableSize;
/*     */   }
/*     */   
/*     */   public MetaData decode(ByteBuffer buffer)
/*     */   {
/*  71 */     if (LOG.isDebugEnabled()) {
/*  72 */       LOG.debug(String.format("CtxTbl[%x] decoding %d octets", new Object[] { Integer.valueOf(this._context.hashCode()), Integer.valueOf(buffer.remaining()) }), new Object[0]);
/*     */     }
/*     */     
/*  75 */     if (buffer.remaining() > this._builder.getMaxSize()) {
/*  76 */       throw new BadMessageException(413, "Header frame size " + buffer.remaining() + ">" + this._builder.getMaxSize());
/*     */     }
/*     */     
/*  79 */     while (buffer.hasRemaining())
/*     */     {
/*  81 */       if (LOG.isDebugEnabled())
/*     */       {
/*  83 */         int l = Math.min(buffer.remaining(), 16);
/*     */         
/*  85 */         LOG.debug("decode {}{}", new Object[] {
/*  86 */           TypeUtil.toHexString(buffer.array(), buffer.arrayOffset() + buffer.position(), l), l < buffer
/*  87 */           .remaining() ? "..." : "" });
/*     */       }
/*     */       
/*  90 */       byte b = buffer.get();
/*  91 */       if (b < 0)
/*     */       {
/*     */ 
/*  94 */         int index = NBitInteger.decode(buffer, 7);
/*  95 */         HpackContext.Entry entry = this._context.get(index);
/*  96 */         if (entry == null)
/*     */         {
/*  98 */           throw new BadMessageException("Unknown index " + index);
/*     */         }
/* 100 */         if (entry.isStatic())
/*     */         {
/* 102 */           if (LOG.isDebugEnabled()) {
/* 103 */             LOG.debug("decode IdxStatic {}", new Object[] { entry });
/*     */           }
/* 105 */           this._builder.emit(entry.getHttpField());
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 112 */           if (LOG.isDebugEnabled()) {
/* 113 */             LOG.debug("decode Idx {}", new Object[] { entry });
/*     */           }
/* 115 */           this._builder.emit(entry.getHttpField());
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 121 */         byte f = (byte)((b & 0xF0) >> 4);
/*     */         
/*     */ 
/*     */         int name_index;
/*     */         
/*     */ 
/*     */         int name_index;
/*     */         
/* 129 */         switch (f)
/*     */         {
/*     */ 
/*     */         case 2: 
/*     */         case 3: 
/* 134 */           int size = NBitInteger.decode(buffer, 5);
/* 135 */           if (LOG.isDebugEnabled())
/* 136 */             LOG.debug("decode resize=" + size, new Object[0]);
/* 137 */           if (size > this._localMaxDynamicTableSize)
/* 138 */             throw new IllegalArgumentException();
/* 139 */           this._context.resize(size);
/* 140 */           break;
/*     */         
/*     */         case 0: 
/*     */         case 1: 
/* 144 */           boolean indexed = false;
/* 145 */           name_index = NBitInteger.decode(buffer, 4);
/* 146 */           break;
/*     */         
/*     */ 
/*     */         case 4: 
/*     */         case 5: 
/*     */         case 6: 
/*     */         case 7: 
/* 153 */           boolean indexed = true;
/* 154 */           name_index = NBitInteger.decode(buffer, 6);
/* 155 */           break;
/*     */         
/*     */         default: 
/* 158 */           throw new IllegalStateException();
/*     */           
/*     */           int name_index;
/*     */           boolean indexed;
/* 162 */           boolean huffmanName = false;
/*     */           HttpHeader header;
/*     */           String name;
/* 165 */           HttpHeader header; if (name_index > 0)
/*     */           {
/* 167 */             HpackContext.Entry name_entry = this._context.get(name_index);
/* 168 */             String name = name_entry.getHttpField().getName();
/* 169 */             header = name_entry.getHttpField().getHeader();
/*     */           }
/*     */           else
/*     */           {
/* 173 */             huffmanName = (buffer.get() & 0x80) == 128;
/* 174 */             int length = NBitInteger.decode(buffer, 7);
/* 175 */             this._builder.checkSize(length, huffmanName);
/* 176 */             String name; if (huffmanName) {
/* 177 */               name = Huffman.decode(buffer, length);
/*     */             } else
/* 179 */               name = toASCIIString(buffer, length);
/* 180 */             for (int i = 0; i < name.length(); i++)
/*     */             {
/* 182 */               char c = name.charAt(i);
/* 183 */               if ((c >= 'A') && (c <= 'Z'))
/*     */               {
/* 185 */                 throw new BadMessageException(400, "Uppercase header name");
/*     */               }
/*     */             }
/* 188 */             header = (HttpHeader)HttpHeader.CACHE.get(name);
/*     */           }
/*     */           
/*     */ 
/* 192 */           boolean huffmanValue = (buffer.get() & 0x80) == 128;
/* 193 */           int length = NBitInteger.decode(buffer, 7);
/* 194 */           this._builder.checkSize(length, huffmanValue);
/* 195 */           String value; String value; if (huffmanValue) {
/* 196 */             value = Huffman.decode(buffer, length);
/*     */           } else {
/* 198 */             value = toASCIIString(buffer, length);
/*     */           }
/*     */           HttpField field;
/*     */           HttpField field;
/* 202 */           if (header == null)
/*     */           {
/*     */ 
/* 205 */             field = new HttpField(null, name, value);
/*     */           }
/*     */           else {
/*     */             HttpField field;
/*     */             HttpField field;
/*     */             HttpField field;
/* 211 */             switch (header) {
/*     */             case C_STATUS: 
/*     */               HttpField field;
/* 214 */               if (indexed) {
/* 215 */                 field = new IntValueHttpField(header, name, value);
/*     */               } else
/* 217 */                 field = new HttpField(header, name, value);
/* 218 */               break;
/*     */             
/*     */             case C_AUTHORITY: 
/* 221 */               field = new AuthorityHttpField(value);
/* 222 */               break;
/*     */             case CONTENT_LENGTH: 
/*     */               HttpField field;
/* 225 */               if ("0".equals(value)) {
/* 226 */                 field = CONTENT_LENGTH_0;
/*     */               } else
/* 228 */                 field = new LongValueHttpField(header, name, value);
/* 229 */               break;
/*     */             
/*     */             default: 
/* 232 */               field = new HttpField(header, name, value);
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 237 */           if (LOG.isDebugEnabled())
/*     */           {
/* 239 */             LOG.debug("decoded '{}' by {}/{}/{}", new Object[] { field, huffmanName ? "HuffName" : name_index > 0 ? "IdxName" : "LitName", huffmanValue ? "HuffVal" : "LitVal", indexed ? "Idx" : "" });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 247 */           this._builder.emit(field);
/*     */           
/*     */ 
/* 250 */           if (indexed)
/*     */           {
/*     */ 
/* 253 */             this._context.add(field); }
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 259 */     return this._builder.build();
/*     */   }
/*     */   
/*     */   public static String toASCIIString(ByteBuffer buffer, int length)
/*     */   {
/* 264 */     StringBuilder builder = new StringBuilder(length);
/* 265 */     int position = buffer.position();
/* 266 */     int start = buffer.arrayOffset() + position;
/* 267 */     int end = start + length;
/* 268 */     buffer.position(position + length);
/* 269 */     byte[] array = buffer.array();
/* 270 */     for (int i = start; i < end; i++)
/* 271 */       builder.append((char)(0x7F & array[i]));
/* 272 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 278 */     return String.format("HpackDecoder@%x{%s}", new Object[] { Integer.valueOf(hashCode()), this._context });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\HpackDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */