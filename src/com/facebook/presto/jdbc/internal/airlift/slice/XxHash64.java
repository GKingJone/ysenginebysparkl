/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import sun.misc.Unsafe;
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
/*     */ public final class XxHash64
/*     */ {
/*     */   private static final long PRIME64_1 = -7046029288634856825L;
/*     */   private static final long PRIME64_2 = -4417276706812531889L;
/*     */   private static final long PRIME64_3 = 1609587929392839161L;
/*     */   private static final long PRIME64_4 = -8796714831421723037L;
/*     */   private static final long PRIME64_5 = 2870177450012600261L;
/*     */   private static final long DEFAULT_SEED = 0L;
/*     */   private final long seed;
/*  37 */   private static final long BUFFER_ADDRESS = Unsafe.ARRAY_BYTE_BASE_OFFSET;
/*  38 */   private final byte[] buffer = new byte[32];
/*     */   
/*     */   private int bufferSize;
/*     */   
/*     */   private long bodyLength;
/*     */   private long v1;
/*     */   private long v2;
/*     */   private long v3;
/*     */   private long v4;
/*     */   
/*     */   public XxHash64()
/*     */   {
/*  50 */     this(0L);
/*     */   }
/*     */   
/*     */   public XxHash64(long seed)
/*     */   {
/*  55 */     this.seed = seed;
/*  56 */     this.v1 = (seed + -7046029288634856825L + -4417276706812531889L);
/*  57 */     this.v2 = (seed + -4417276706812531889L);
/*  58 */     this.v3 = seed;
/*  59 */     this.v4 = (seed - -7046029288634856825L);
/*     */   }
/*     */   
/*     */   public XxHash64 update(byte[] data)
/*     */   {
/*  64 */     return update(data, 0, data.length);
/*     */   }
/*     */   
/*     */   public XxHash64 update(byte[] data, int offset, int length)
/*     */   {
/*  69 */     Preconditions.checkPositionIndexes(offset, offset + length, data.length);
/*  70 */     updateHash(data, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, length);
/*  71 */     return this;
/*     */   }
/*     */   
/*     */   public XxHash64 update(Slice data)
/*     */   {
/*  76 */     return update(data, 0, data.length());
/*     */   }
/*     */   
/*     */   public XxHash64 update(Slice data, int offset, int length)
/*     */   {
/*  81 */     Preconditions.checkPositionIndexes(0, offset + length, data.length());
/*  82 */     updateHash(data.getBase(), data.getAddress() + offset, length);
/*  83 */     return this;
/*     */   }
/*     */   
/*     */   public long hash() {
/*     */     long hash;
/*     */     long hash;
/*  89 */     if (this.bodyLength > 0L) {
/*  90 */       hash = computeBody();
/*     */     }
/*     */     else {
/*  93 */       hash = this.seed + 2870177450012600261L;
/*     */     }
/*     */     
/*  96 */     hash += this.bodyLength + this.bufferSize;
/*     */     
/*  98 */     return updateTail(hash, this.buffer, BUFFER_ADDRESS, 0, this.bufferSize);
/*     */   }
/*     */   
/*     */   private long computeBody()
/*     */   {
/* 103 */     long hash = Long.rotateLeft(this.v1, 1) + Long.rotateLeft(this.v2, 7) + Long.rotateLeft(this.v3, 12) + Long.rotateLeft(this.v4, 18);
/*     */     
/* 105 */     hash = update(hash, this.v1);
/* 106 */     hash = update(hash, this.v2);
/* 107 */     hash = update(hash, this.v3);
/* 108 */     hash = update(hash, this.v4);
/*     */     
/* 110 */     return hash;
/*     */   }
/*     */   
/*     */   private void updateHash(Object base, long address, int length)
/*     */   {
/* 115 */     if (this.bufferSize > 0) {
/* 116 */       int available = Math.min(32 - this.bufferSize, length);
/*     */       
/* 118 */       JvmUtils.unsafe.copyMemory(base, address, this.buffer, BUFFER_ADDRESS + this.bufferSize, available);
/*     */       
/* 120 */       this.bufferSize += available;
/* 121 */       address += available;
/* 122 */       length -= available;
/*     */       
/* 124 */       if (this.bufferSize == 32) {
/* 125 */         updateBody(this.buffer, BUFFER_ADDRESS, this.bufferSize);
/* 126 */         this.bufferSize = 0;
/*     */       }
/*     */     }
/*     */     
/* 130 */     if (length >= 32) {
/* 131 */       int index = updateBody(base, address, length);
/* 132 */       address += index;
/* 133 */       length -= index;
/*     */     }
/*     */     
/* 136 */     if (length > 0) {
/* 137 */       JvmUtils.unsafe.copyMemory(base, address, this.buffer, BUFFER_ADDRESS, length);
/* 138 */       this.bufferSize = length;
/*     */     }
/*     */   }
/*     */   
/*     */   private int updateBody(Object base, long address, int length)
/*     */   {
/* 144 */     int remaining = length;
/* 145 */     while (remaining >= 32) {
/* 146 */       this.v1 = mix(this.v1, JvmUtils.unsafe.getLong(base, address));
/* 147 */       this.v2 = mix(this.v2, JvmUtils.unsafe.getLong(base, address + 8L));
/* 148 */       this.v3 = mix(this.v3, JvmUtils.unsafe.getLong(base, address + 16L));
/* 149 */       this.v4 = mix(this.v4, JvmUtils.unsafe.getLong(base, address + 24L));
/*     */       
/* 151 */       address += 32L;
/* 152 */       remaining -= 32;
/*     */     }
/*     */     
/* 155 */     int index = length - remaining;
/* 156 */     this.bodyLength += index;
/* 157 */     return index;
/*     */   }
/*     */   
/*     */   public static long hash(long value)
/*     */   {
/* 162 */     long hash = 2870177450012600269L;
/* 163 */     hash = updateTail(hash, value);
/* 164 */     hash = finalShuffle(hash);
/*     */     
/* 166 */     return hash;
/*     */   }
/*     */   
/*     */   public static long hash(InputStream in)
/*     */     throws IOException
/*     */   {
/* 172 */     return hash(0L, in);
/*     */   }
/*     */   
/*     */   public static long hash(long seed, InputStream in)
/*     */     throws IOException
/*     */   {
/* 178 */     XxHash64 hash = new XxHash64(seed);
/* 179 */     byte[] buffer = new byte[' '];
/*     */     for (;;) {
/* 181 */       int length = in.read(buffer);
/* 182 */       if (length == -1) {
/*     */         break;
/*     */       }
/* 185 */       hash.update(buffer, 0, length);
/*     */     }
/* 187 */     return hash.hash();
/*     */   }
/*     */   
/*     */   public static long hash(Slice data)
/*     */   {
/* 192 */     return hash(data, 0, data.length());
/*     */   }
/*     */   
/*     */   public static long hash(long seed, Slice data)
/*     */   {
/* 197 */     return hash(seed, data, 0, data.length());
/*     */   }
/*     */   
/*     */   public static long hash(Slice data, int offset, int length)
/*     */   {
/* 202 */     return hash(0L, data, offset, length);
/*     */   }
/*     */   
/*     */   public static long hash(long seed, Slice data, int offset, int length)
/*     */   {
/* 207 */     Preconditions.checkPositionIndexes(0, offset + length, data.length());
/*     */     
/* 209 */     Object base = data.getBase();
/* 210 */     long address = data.getAddress() + offset;
/*     */     long hash;
/*     */     long hash;
/* 213 */     if (length >= 32) {
/* 214 */       hash = updateBody(seed, base, address, length);
/*     */     }
/*     */     else {
/* 217 */       hash = seed + 2870177450012600261L;
/*     */     }
/*     */     
/* 220 */     hash += length;
/*     */     
/*     */ 
/*     */ 
/* 224 */     int index = length & 0xFFFFFFE0;
/*     */     
/* 226 */     return updateTail(hash, base, address, index, length);
/*     */   }
/*     */   
/*     */   private static long updateTail(long hash, Object base, long address, int index, int length)
/*     */   {
/* 231 */     while (index <= length - 8) {
/* 232 */       hash = updateTail(hash, JvmUtils.unsafe.getLong(base, address + index));
/* 233 */       index += 8;
/*     */     }
/*     */     
/* 236 */     if (index <= length - 4) {
/* 237 */       hash = updateTail(hash, JvmUtils.unsafe.getInt(base, address + index));
/* 238 */       index += 4;
/*     */     }
/*     */     
/* 241 */     while (index < length) {
/* 242 */       hash = updateTail(hash, JvmUtils.unsafe.getByte(base, address + index));
/* 243 */       index++;
/*     */     }
/*     */     
/* 246 */     hash = finalShuffle(hash);
/*     */     
/* 248 */     return hash;
/*     */   }
/*     */   
/*     */   private static long updateBody(long seed, Object base, long address, int length)
/*     */   {
/* 253 */     long v1 = seed + -7046029288634856825L + -4417276706812531889L;
/* 254 */     long v2 = seed + -4417276706812531889L;
/* 255 */     long v3 = seed;
/* 256 */     long v4 = seed - -7046029288634856825L;
/*     */     
/* 258 */     int remaining = length;
/* 259 */     while (remaining >= 32) {
/* 260 */       v1 = mix(v1, JvmUtils.unsafe.getLong(base, address));
/* 261 */       v2 = mix(v2, JvmUtils.unsafe.getLong(base, address + 8L));
/* 262 */       v3 = mix(v3, JvmUtils.unsafe.getLong(base, address + 16L));
/* 263 */       v4 = mix(v4, JvmUtils.unsafe.getLong(base, address + 24L));
/*     */       
/* 265 */       address += 32L;
/* 266 */       remaining -= 32;
/*     */     }
/*     */     
/* 269 */     long hash = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);
/*     */     
/* 271 */     hash = update(hash, v1);
/* 272 */     hash = update(hash, v2);
/* 273 */     hash = update(hash, v3);
/* 274 */     hash = update(hash, v4);
/*     */     
/* 276 */     return hash;
/*     */   }
/*     */   
/*     */   private static long mix(long current, long value)
/*     */   {
/* 281 */     return Long.rotateLeft(current + value * -4417276706812531889L, 31) * -7046029288634856825L;
/*     */   }
/*     */   
/*     */   private static long update(long hash, long value)
/*     */   {
/* 286 */     long temp = hash ^ mix(0L, value);
/* 287 */     return temp * -7046029288634856825L + -8796714831421723037L;
/*     */   }
/*     */   
/*     */   private static long updateTail(long hash, long value)
/*     */   {
/* 292 */     long temp = hash ^ mix(0L, value);
/* 293 */     return Long.rotateLeft(temp, 27) * -7046029288634856825L + -8796714831421723037L;
/*     */   }
/*     */   
/*     */   private static long updateTail(long hash, int value)
/*     */   {
/* 298 */     long unsigned = value & 0xFFFFFFFF;
/* 299 */     long temp = hash ^ unsigned * -7046029288634856825L;
/* 300 */     return Long.rotateLeft(temp, 23) * -4417276706812531889L + 1609587929392839161L;
/*     */   }
/*     */   
/*     */   private static long updateTail(long hash, byte value)
/*     */   {
/* 305 */     int unsigned = value & 0xFF;
/* 306 */     long temp = hash ^ unsigned * 2870177450012600261L;
/* 307 */     return Long.rotateLeft(temp, 11) * -7046029288634856825L;
/*     */   }
/*     */   
/*     */   private static long finalShuffle(long hash)
/*     */   {
/* 312 */     hash ^= hash >>> 33;
/* 313 */     hash *= -4417276706812531889L;
/* 314 */     hash ^= hash >>> 29;
/* 315 */     hash *= 1609587929392839161L;
/* 316 */     hash ^= hash >>> 32;
/* 317 */     return hash;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\XxHash64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */