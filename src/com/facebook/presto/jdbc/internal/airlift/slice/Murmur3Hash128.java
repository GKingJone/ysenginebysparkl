/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Murmur3Hash128
/*     */ {
/*     */   private static final long C1 = -8663945395140668459L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long C2 = 5545529020109919103L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long DEFAULT_SEED = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Slice hash(Slice data)
/*     */   {
/*  25 */     return hash(data, 0, data.length());
/*     */   }
/*     */   
/*     */   public static Slice hash(Slice data, int offset, int length)
/*     */   {
/*  30 */     return hash(0L, data, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice hash(long seed, Slice data, int offset, int length)
/*     */   {
/*  35 */     int fastLimit = offset + length - 16 + 1;
/*     */     
/*  37 */     long h1 = seed;
/*  38 */     long h2 = seed;
/*     */     
/*  40 */     int current = offset;
/*  41 */     while (current < fastLimit) {
/*  42 */       long k1 = data.getLong(current);
/*  43 */       current += 8;
/*     */       
/*  45 */       long k2 = data.getLong(current);
/*  46 */       current += 8;
/*     */       
/*  48 */       k1 *= -8663945395140668459L;
/*  49 */       k1 = Long.rotateLeft(k1, 31);
/*  50 */       k1 *= 5545529020109919103L;
/*  51 */       h1 ^= k1;
/*     */       
/*  53 */       h1 = Long.rotateLeft(h1, 27);
/*  54 */       h1 += h2;
/*  55 */       h1 = h1 * 5L + 1390208809L;
/*     */       
/*  57 */       k2 *= 5545529020109919103L;
/*  58 */       k2 = Long.rotateLeft(k2, 33);
/*  59 */       k2 *= -8663945395140668459L;
/*  60 */       h2 ^= k2;
/*     */       
/*  62 */       h2 = Long.rotateLeft(h2, 31);
/*  63 */       h2 += h1;
/*  64 */       h2 = h2 * 5L + 944331445L;
/*     */     }
/*     */     
/*  67 */     long k1 = 0L;
/*  68 */     long k2 = 0L;
/*     */     
/*  70 */     switch (length & 0xF) {
/*     */     case 15: 
/*  72 */       k2 ^= data.getUnsignedByte(current + 14) << 48;
/*     */     case 14: 
/*  74 */       k2 ^= data.getUnsignedByte(current + 13) << 40;
/*     */     case 13: 
/*  76 */       k2 ^= data.getUnsignedByte(current + 12) << 32;
/*     */     case 12: 
/*  78 */       k2 ^= data.getUnsignedByte(current + 11) << 24;
/*     */     case 11: 
/*  80 */       k2 ^= data.getUnsignedByte(current + 10) << 16;
/*     */     case 10: 
/*  82 */       k2 ^= data.getUnsignedByte(current + 9) << 8;
/*     */     case 9: 
/*  84 */       k2 ^= data.getUnsignedByte(current + 8) << 0;
/*     */       
/*  86 */       k2 *= 5545529020109919103L;
/*  87 */       k2 = Long.rotateLeft(k2, 33);
/*  88 */       k2 *= -8663945395140668459L;
/*  89 */       h2 ^= k2;
/*     */     
/*     */     case 8: 
/*  92 */       k1 ^= data.getUnsignedByte(current + 7) << 56;
/*     */     case 7: 
/*  94 */       k1 ^= data.getUnsignedByte(current + 6) << 48;
/*     */     case 6: 
/*  96 */       k1 ^= data.getUnsignedByte(current + 5) << 40;
/*     */     case 5: 
/*  98 */       k1 ^= data.getUnsignedByte(current + 4) << 32;
/*     */     case 4: 
/* 100 */       k1 ^= data.getUnsignedByte(current + 3) << 24;
/*     */     case 3: 
/* 102 */       k1 ^= data.getUnsignedByte(current + 2) << 16;
/*     */     case 2: 
/* 104 */       k1 ^= data.getUnsignedByte(current + 1) << 8;
/*     */     case 1: 
/* 106 */       k1 ^= data.getUnsignedByte(current + 0) << 0;
/*     */       
/* 108 */       k1 *= -8663945395140668459L;
/* 109 */       k1 = Long.rotateLeft(k1, 31);
/* 110 */       k1 *= 5545529020109919103L;
/* 111 */       h1 ^= k1;
/*     */     }
/*     */     
/* 114 */     h1 ^= length;
/* 115 */     h2 ^= length;
/*     */     
/* 117 */     h1 += h2;
/* 118 */     h2 += h1;
/*     */     
/* 120 */     h1 = mix64(h1);
/* 121 */     h2 = mix64(h2);
/*     */     
/* 123 */     h1 += h2;
/* 124 */     h2 += h1;
/*     */     
/* 126 */     Slice result = Slices.allocate(16);
/* 127 */     result.setLong(0, h1);
/* 128 */     result.setLong(8, h2);
/*     */     
/* 130 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long hash64(Slice data)
/*     */   {
/* 138 */     return hash64(data, 0, data.length());
/*     */   }
/*     */   
/*     */   public static long hash64(Slice data, int offset, int length)
/*     */   {
/* 143 */     return hash64(0L, data, offset, length);
/*     */   }
/*     */   
/*     */   public static long hash64(long seed, Slice data, int offset, int length)
/*     */   {
/* 148 */     int fastLimit = offset + length - 16 + 1;
/*     */     
/* 150 */     long h1 = seed;
/* 151 */     long h2 = seed;
/*     */     
/* 153 */     int current = offset;
/* 154 */     while (current < fastLimit) {
/* 155 */       long k1 = data.getLong(current);
/* 156 */       current += 8;
/*     */       
/* 158 */       long k2 = data.getLong(current);
/* 159 */       current += 8;
/*     */       
/* 161 */       k1 *= -8663945395140668459L;
/* 162 */       k1 = Long.rotateLeft(k1, 31);
/* 163 */       k1 *= 5545529020109919103L;
/* 164 */       h1 ^= k1;
/*     */       
/* 166 */       h1 = Long.rotateLeft(h1, 27);
/* 167 */       h1 += h2;
/* 168 */       h1 = h1 * 5L + 1390208809L;
/*     */       
/* 170 */       k2 *= 5545529020109919103L;
/* 171 */       k2 = Long.rotateLeft(k2, 33);
/* 172 */       k2 *= -8663945395140668459L;
/* 173 */       h2 ^= k2;
/*     */       
/* 175 */       h2 = Long.rotateLeft(h2, 31);
/* 176 */       h2 += h1;
/* 177 */       h2 = h2 * 5L + 944331445L;
/*     */     }
/*     */     
/* 180 */     long k1 = 0L;
/* 181 */     long k2 = 0L;
/*     */     
/* 183 */     switch (length & 0xF) {
/*     */     case 15: 
/* 185 */       k2 ^= data.getUnsignedByte(current + 14) << 48;
/*     */     case 14: 
/* 187 */       k2 ^= data.getUnsignedByte(current + 13) << 40;
/*     */     case 13: 
/* 189 */       k2 ^= data.getUnsignedByte(current + 12) << 32;
/*     */     case 12: 
/* 191 */       k2 ^= data.getUnsignedByte(current + 11) << 24;
/*     */     case 11: 
/* 193 */       k2 ^= data.getUnsignedByte(current + 10) << 16;
/*     */     case 10: 
/* 195 */       k2 ^= data.getUnsignedByte(current + 9) << 8;
/*     */     case 9: 
/* 197 */       k2 ^= data.getUnsignedByte(current + 8) << 0;
/*     */       
/* 199 */       k2 *= 5545529020109919103L;
/* 200 */       k2 = Long.rotateLeft(k2, 33);
/* 201 */       k2 *= -8663945395140668459L;
/* 202 */       h2 ^= k2;
/*     */     
/*     */     case 8: 
/* 205 */       k1 ^= data.getUnsignedByte(current + 7) << 56;
/*     */     case 7: 
/* 207 */       k1 ^= data.getUnsignedByte(current + 6) << 48;
/*     */     case 6: 
/* 209 */       k1 ^= data.getUnsignedByte(current + 5) << 40;
/*     */     case 5: 
/* 211 */       k1 ^= data.getUnsignedByte(current + 4) << 32;
/*     */     case 4: 
/* 213 */       k1 ^= data.getUnsignedByte(current + 3) << 24;
/*     */     case 3: 
/* 215 */       k1 ^= data.getUnsignedByte(current + 2) << 16;
/*     */     case 2: 
/* 217 */       k1 ^= data.getUnsignedByte(current + 1) << 8;
/*     */     case 1: 
/* 219 */       k1 ^= data.getUnsignedByte(current + 0) << 0;
/*     */       
/* 221 */       k1 *= -8663945395140668459L;
/* 222 */       k1 = Long.rotateLeft(k1, 31);
/* 223 */       k1 *= 5545529020109919103L;
/* 224 */       h1 ^= k1;
/*     */     }
/*     */     
/* 227 */     h1 ^= length;
/* 228 */     h2 ^= length;
/*     */     
/* 230 */     h1 += h2;
/* 231 */     h2 += h1;
/*     */     
/* 233 */     h1 = mix64(h1);
/* 234 */     h2 = mix64(h2);
/*     */     
/* 236 */     return h1 + h2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long hash64(long value)
/*     */   {
/* 244 */     long h2 = 8L;
/* 245 */     long h1 = h2 + (h2 ^ Long.rotateLeft(value * -8663945395140668459L, 31) * 5545529020109919103L);
/*     */     
/* 247 */     return mix64(h1) + mix64(h1 + h2);
/*     */   }
/*     */   
/*     */   private static long mix64(long k)
/*     */   {
/* 252 */     k ^= k >>> 33;
/* 253 */     k *= -49064778989728563L;
/* 254 */     k ^= k >>> 33;
/* 255 */     k *= -4265267296055464877L;
/* 256 */     k ^= k >>> 33;
/*     */     
/* 258 */     return k;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Murmur3Hash128.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */