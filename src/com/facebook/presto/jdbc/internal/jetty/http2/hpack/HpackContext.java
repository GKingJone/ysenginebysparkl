/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTernaryTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HpackContext
/*     */ {
/*  49 */   public static final Logger LOG = Log.getLogger(HpackContext.class);
/*     */   
/*  51 */   public static final String[][] STATIC_TABLE = { { null, null }, { ":authority", null }, { ":method", "GET" }, { ":method", "POST" }, { ":path", "/" }, { ":path", "/index.html" }, { ":scheme", "http" }, { ":scheme", "https" }, { ":status", "200" }, { ":status", "204" }, { ":status", "206" }, { ":status", "304" }, { ":status", "400" }, { ":status", "404" }, { ":status", "500" }, { "accept-charset", null }, { "accept-encoding", "gzip, deflate" }, { "accept-language", null }, { "accept-ranges", null }, { "accept", null }, { "access-control-allow-origin", null }, { "age", null }, { "allow", null }, { "authorization", null }, { "cache-control", null }, { "content-disposition", null }, { "content-encoding", null }, { "content-language", null }, { "content-length", null }, { "content-location", null }, { "content-range", null }, { "content-type", null }, { "cookie", null }, { "date", null }, { "etag", null }, { "expect", null }, { "expires", null }, { "from", null }, { "host", null }, { "if-match", null }, { "if-modified-since", null }, { "if-none-match", null }, { "if-range", null }, { "if-unmodified-since", null }, { "last-modified", null }, { "link", null }, { "location", null }, { "max-forwards", null }, { "proxy-authenticate", null }, { "proxy-authorization", null }, { "range", null }, { "referer", null }, { "refresh", null }, { "retry-after", null }, { "server", null }, { "set-cookie", null }, { "strict-transport-security", null }, { "transfer-encoding", null }, { "user-agent", null }, { "vary", null }, { "via", null }, { "www-authenticate", null } };
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
/* 117 */   private static final Map<HttpField, Entry> __staticFieldMap = new HashMap();
/* 118 */   private static final Trie<StaticEntry> __staticNameMap = new ArrayTernaryTrie(true, 512);
/* 119 */   private static final StaticEntry[] __staticTableByHeader = new StaticEntry[HttpHeader.UNKNOWN.ordinal()];
/* 120 */   private static final StaticEntry[] __staticTable = new StaticEntry[STATIC_TABLE.length];
/*     */   private int _maxDynamicTableSizeInBytes;
/*     */   
/* 123 */   static { Set<String> added = new HashSet();
/* 124 */     for (int i = 1; i < STATIC_TABLE.length; i++)
/*     */     {
/* 126 */       entry = null;
/*     */       
/* 128 */       name = STATIC_TABLE[i][0];
/* 129 */       String value = STATIC_TABLE[i][1];
/* 130 */       HttpHeader header = (HttpHeader)HttpHeader.CACHE.get(name);
/* 131 */       if ((header != null) && (value != null))
/*     */       {
/* 133 */         switch (header)
/*     */         {
/*     */ 
/*     */ 
/*     */         case C_METHOD: 
/* 138 */           HttpMethod method = (HttpMethod)HttpMethod.CACHE.get(value);
/* 139 */           if (method != null) {
/* 140 */             entry = new StaticEntry(i, new StaticTableHttpField(header, name, value, method));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case C_SCHEME: 
/* 147 */           HttpScheme scheme = (HttpScheme)HttpScheme.CACHE.get(value);
/* 148 */           if (scheme != null) {
/* 149 */             entry = new StaticEntry(i, new StaticTableHttpField(header, name, value, scheme));
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case C_STATUS: 
/* 155 */           entry = new StaticEntry(i, new StaticTableHttpField(header, name, value, Integer.valueOf(value)));
/* 156 */           break;
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 164 */       if (entry == null) {
/* 165 */         entry = new StaticEntry(i, header == null ? new HttpField(STATIC_TABLE[i][0], value) : new HttpField(header, name, value));
/*     */       }
/*     */       
/* 168 */       __staticTable[i] = entry;
/*     */       
/* 170 */       if (entry._field.getValue() != null) {
/* 171 */         __staticFieldMap.put(entry._field, entry);
/*     */       }
/* 173 */       if (!added.contains(entry._field.getName()))
/*     */       {
/* 175 */         added.add(entry._field.getName());
/* 176 */         __staticNameMap.put(entry._field.getName(), entry);
/* 177 */         if (__staticNameMap.get(entry._field.getName()) == null) {
/* 178 */           throw new IllegalStateException("name trie too small");
/*     */         }
/*     */       }
/*     */     }
/* 182 */     i = HttpHeader.values();StaticEntry entry = i.length; for (String name = 0; name < entry; name++) { HttpHeader h = i[name];
/*     */       
/* 184 */       StaticEntry entry = (StaticEntry)__staticNameMap.get(h.asString());
/* 185 */       if (entry != null) {
/* 186 */         __staticTableByHeader[h.ordinal()] = entry;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int _dynamicTableSizeInBytes;
/*     */   private final DynamicTable _dynamicTable;
/* 193 */   private final Map<HttpField, Entry> _fieldMap = new HashMap();
/* 194 */   private final Map<String, Entry> _nameMap = new HashMap();
/*     */   
/*     */   HpackContext(int maxDynamicTableSize)
/*     */   {
/* 198 */     this._maxDynamicTableSizeInBytes = maxDynamicTableSize;
/* 199 */     int guesstimateEntries = 10 + maxDynamicTableSize / 52;
/* 200 */     this._dynamicTable = new DynamicTable(guesstimateEntries, guesstimateEntries + 10, null);
/* 201 */     if (LOG.isDebugEnabled()) {
/* 202 */       LOG.debug(String.format("HdrTbl[%x] created max=%d", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(maxDynamicTableSize) }), new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void resize(int newMaxDynamicTableSize) {
/* 207 */     if (LOG.isDebugEnabled())
/* 208 */       LOG.debug(String.format("HdrTbl[%x] resized max=%d->%d", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(this._maxDynamicTableSizeInBytes), Integer.valueOf(newMaxDynamicTableSize) }), new Object[0]);
/* 209 */     this._maxDynamicTableSizeInBytes = newMaxDynamicTableSize;
/* 210 */     int guesstimateEntries = 10 + newMaxDynamicTableSize / 52;
/* 211 */     evict();
/* 212 */     this._dynamicTable.resizeUnsafe(guesstimateEntries);
/*     */   }
/*     */   
/*     */   public Entry get(HttpField field)
/*     */   {
/* 217 */     Entry entry = (Entry)this._fieldMap.get(field);
/* 218 */     if (entry == null)
/* 219 */       entry = (Entry)__staticFieldMap.get(field);
/* 220 */     return entry;
/*     */   }
/*     */   
/*     */   public Entry get(String name)
/*     */   {
/* 225 */     Entry entry = (Entry)__staticNameMap.get(name);
/* 226 */     if (entry != null)
/* 227 */       return entry;
/* 228 */     return (Entry)this._nameMap.get(StringUtil.asciiToLowerCase(name));
/*     */   }
/*     */   
/*     */   public Entry get(int index)
/*     */   {
/* 233 */     if (index < __staticTable.length) {
/* 234 */       return __staticTable[index];
/*     */     }
/* 236 */     int d = this._dynamicTable.size() - index + __staticTable.length - 1;
/*     */     
/* 238 */     if (d >= 0)
/* 239 */       return (Entry)this._dynamicTable.getUnsafe(d);
/* 240 */     return null;
/*     */   }
/*     */   
/*     */   public Entry get(HttpHeader header)
/*     */   {
/* 245 */     Entry e = __staticTableByHeader[header.ordinal()];
/* 246 */     if (e == null)
/* 247 */       return get(header.asString());
/* 248 */     return e;
/*     */   }
/*     */   
/*     */   public static Entry getStatic(HttpHeader header)
/*     */   {
/* 253 */     return __staticTableByHeader[header.ordinal()];
/*     */   }
/*     */   
/*     */   public Entry add(HttpField field)
/*     */   {
/* 258 */     int slot = this._dynamicTable.getNextSlotUnsafe();
/* 259 */     Entry entry = new Entry(slot, field);
/* 260 */     int size = entry.getSize();
/* 261 */     if (size > this._maxDynamicTableSizeInBytes)
/*     */     {
/* 263 */       if (LOG.isDebugEnabled())
/* 264 */         LOG.debug(String.format("HdrTbl[%x] !added size %d>%d", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(size), Integer.valueOf(this._maxDynamicTableSizeInBytes) }), new Object[0]);
/* 265 */       return null;
/*     */     }
/* 267 */     this._dynamicTableSizeInBytes += size;
/* 268 */     this._dynamicTable.addUnsafe(entry);
/* 269 */     this._fieldMap.put(field, entry);
/* 270 */     this._nameMap.put(StringUtil.asciiToLowerCase(field.getName()), entry);
/*     */     
/* 272 */     if (LOG.isDebugEnabled())
/* 273 */       LOG.debug(String.format("HdrTbl[%x] added %s", new Object[] { Integer.valueOf(hashCode()), entry }), new Object[0]);
/* 274 */     evict();
/* 275 */     return entry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 283 */     return this._dynamicTable.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDynamicTableSize()
/*     */   {
/* 291 */     return this._dynamicTableSizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxDynamicTableSize()
/*     */   {
/* 299 */     return this._maxDynamicTableSizeInBytes;
/*     */   }
/*     */   
/*     */   public int index(Entry entry)
/*     */   {
/* 304 */     if (entry._slot < 0)
/* 305 */       return 0;
/* 306 */     if (entry.isStatic()) {
/* 307 */       return entry._slot;
/*     */     }
/* 309 */     return this._dynamicTable.index(entry) + __staticTable.length - 1;
/*     */   }
/*     */   
/*     */   public static int staticIndex(HttpHeader header)
/*     */   {
/* 314 */     if (header == null)
/* 315 */       return 0;
/* 316 */     Entry entry = (Entry)__staticNameMap.get(header.asString());
/* 317 */     if (entry == null)
/* 318 */       return 0;
/* 319 */     return entry.getSlot();
/*     */   }
/*     */   
/*     */   private void evict()
/*     */   {
/* 324 */     while (this._dynamicTableSizeInBytes > this._maxDynamicTableSizeInBytes)
/*     */     {
/* 326 */       Entry entry = (Entry)this._dynamicTable.pollUnsafe();
/* 327 */       if (LOG.isDebugEnabled())
/* 328 */         LOG.debug(String.format("HdrTbl[%x] evict %s", new Object[] { Integer.valueOf(hashCode()), entry }), new Object[0]);
/* 329 */       this._dynamicTableSizeInBytes -= entry.getSize();
/* 330 */       entry._slot = -1;
/* 331 */       this._fieldMap.remove(entry.getHttpField());
/* 332 */       String lc = StringUtil.asciiToLowerCase(entry.getHttpField().getName());
/* 333 */       if (entry == this._nameMap.get(lc))
/* 334 */         this._nameMap.remove(lc);
/*     */     }
/* 336 */     if (LOG.isDebugEnabled()) {
/* 337 */       LOG.debug(String.format("HdrTbl[%x] entries=%d, size=%d, max=%d", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(this._dynamicTable.size()), Integer.valueOf(this._dynamicTableSizeInBytes), Integer.valueOf(this._maxDynamicTableSizeInBytes) }), new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 343 */     return String.format("HpackContext@%x{entries=%d,size=%d,max=%d}", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(this._dynamicTable.size()), Integer.valueOf(this._dynamicTableSizeInBytes), Integer.valueOf(this._maxDynamicTableSizeInBytes) });
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
/*     */   private class DynamicTable
/*     */     extends ArrayQueue<Entry>
/*     */   {
/*     */     private DynamicTable(int initCapacity, int growBy)
/*     */     {
/* 360 */       super(growBy);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void resizeUnsafe(int newCapacity)
/*     */     {
/* 371 */       super.resizeUnsafe(newCapacity);
/* 372 */       for (int s = 0; s < this._nextSlot; s++) {
/* 373 */         ((Entry)this._elements[s])._slot = s;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean enqueue(Entry e)
/*     */     {
/* 383 */       return super.enqueue(e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Entry dequeue()
/*     */     {
/* 393 */       return (Entry)super.dequeue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int index(Entry entry)
/*     */     {
/* 403 */       return entry._slot >= this._nextE ? this._size - entry._slot + this._nextE : this._nextSlot - entry._slot;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Entry
/*     */   {
/*     */     final HttpField _field;
/*     */     
/*     */ 
/*     */     int _slot;
/*     */     
/*     */ 
/*     */ 
/*     */     Entry()
/*     */     {
/* 420 */       this._slot = 0;
/* 421 */       this._field = null;
/*     */     }
/*     */     
/*     */     Entry(int index, String name, String value)
/*     */     {
/* 426 */       this._slot = index;
/* 427 */       this._field = new HttpField(name, value);
/*     */     }
/*     */     
/*     */     Entry(int slot, HttpField field)
/*     */     {
/* 432 */       this._slot = slot;
/* 433 */       this._field = field;
/*     */     }
/*     */     
/*     */     public int getSize()
/*     */     {
/* 438 */       return 32 + this._field.getName().length() + this._field.getValue().length();
/*     */     }
/*     */     
/*     */     public HttpField getHttpField()
/*     */     {
/* 443 */       return this._field;
/*     */     }
/*     */     
/*     */     public boolean isStatic()
/*     */     {
/* 448 */       return false;
/*     */     }
/*     */     
/*     */     public byte[] getStaticHuffmanValue()
/*     */     {
/* 453 */       return null;
/*     */     }
/*     */     
/*     */     public int getSlot()
/*     */     {
/* 458 */       return this._slot;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 463 */       return String.format("{%s,%d,%s,%x}", new Object[] { isStatic() ? "S" : "D", Integer.valueOf(this._slot), this._field, Integer.valueOf(hashCode()) });
/*     */     }
/*     */   }
/*     */   
/*     */   public static class StaticEntry extends Entry
/*     */   {
/*     */     private final byte[] _huffmanValue;
/*     */     private final byte _encodedField;
/*     */     
/*     */     StaticEntry(int index, HttpField field)
/*     */     {
/* 474 */       super(field);
/* 475 */       String value = field.getValue();
/* 476 */       if ((value != null) && (value.length() > 0))
/*     */       {
/* 478 */         int huffmanLen = Huffman.octetsNeeded(value);
/* 479 */         int lenLen = NBitInteger.octectsNeeded(7, huffmanLen);
/* 480 */         this._huffmanValue = new byte[1 + lenLen + huffmanLen];
/* 481 */         ByteBuffer buffer = ByteBuffer.wrap(this._huffmanValue);
/*     */         
/*     */ 
/* 484 */         buffer.put((byte)Byte.MIN_VALUE);
/*     */         
/* 486 */         NBitInteger.encode(buffer, 7, huffmanLen);
/*     */         
/* 488 */         Huffman.encode(buffer, value);
/*     */       }
/*     */       else {
/* 491 */         this._huffmanValue = null;
/*     */       }
/* 493 */       this._encodedField = ((byte)(0x80 | index));
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isStatic()
/*     */     {
/* 499 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     public byte[] getStaticHuffmanValue()
/*     */     {
/* 505 */       return this._huffmanValue;
/*     */     }
/*     */     
/*     */     public byte getEncodedField()
/*     */     {
/* 510 */       return this._encodedField;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\HpackContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */