/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ZigZagEncoding
/*     */ {
/*     */   static void putLong(ByteBuffer buffer, long value)
/*     */   {
/*  36 */     value = value << 1 ^ value >> 63;
/*  37 */     if (value >>> 7 == 0L) {
/*  38 */       buffer.put((byte)(int)value);
/*     */     } else {
/*  40 */       buffer.put((byte)(int)(value & 0x7F | 0x80));
/*  41 */       if (value >>> 14 == 0L) {
/*  42 */         buffer.put((byte)(int)(value >>> 7));
/*     */       } else {
/*  44 */         buffer.put((byte)(int)(value >>> 7 | 0x80));
/*  45 */         if (value >>> 21 == 0L) {
/*  46 */           buffer.put((byte)(int)(value >>> 14));
/*     */         } else {
/*  48 */           buffer.put((byte)(int)(value >>> 14 | 0x80));
/*  49 */           if (value >>> 28 == 0L) {
/*  50 */             buffer.put((byte)(int)(value >>> 21));
/*     */           } else {
/*  52 */             buffer.put((byte)(int)(value >>> 21 | 0x80));
/*  53 */             if (value >>> 35 == 0L) {
/*  54 */               buffer.put((byte)(int)(value >>> 28));
/*     */             } else {
/*  56 */               buffer.put((byte)(int)(value >>> 28 | 0x80));
/*  57 */               if (value >>> 42 == 0L) {
/*  58 */                 buffer.put((byte)(int)(value >>> 35));
/*     */               } else {
/*  60 */                 buffer.put((byte)(int)(value >>> 35 | 0x80));
/*  61 */                 if (value >>> 49 == 0L) {
/*  62 */                   buffer.put((byte)(int)(value >>> 42));
/*     */                 } else {
/*  64 */                   buffer.put((byte)(int)(value >>> 42 | 0x80));
/*  65 */                   if (value >>> 56 == 0L) {
/*  66 */                     buffer.put((byte)(int)(value >>> 49));
/*     */                   } else {
/*  68 */                     buffer.put((byte)(int)(value >>> 49 | 0x80));
/*  69 */                     buffer.put((byte)(int)(value >>> 56));
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void putInt(ByteBuffer buffer, int value)
/*     */   {
/*  86 */     value = value << 1 ^ value >> 31;
/*  87 */     if (value >>> 7 == 0) {
/*  88 */       buffer.put((byte)value);
/*     */     } else {
/*  90 */       buffer.put((byte)(value & 0x7F | 0x80));
/*  91 */       if (value >>> 14 == 0) {
/*  92 */         buffer.put((byte)(value >>> 7));
/*     */       } else {
/*  94 */         buffer.put((byte)(value >>> 7 | 0x80));
/*  95 */         if (value >>> 21 == 0) {
/*  96 */           buffer.put((byte)(value >>> 14));
/*     */         } else {
/*  98 */           buffer.put((byte)(value >>> 14 | 0x80));
/*  99 */           if (value >>> 28 == 0) {
/* 100 */             buffer.put((byte)(value >>> 21));
/*     */           } else {
/* 102 */             buffer.put((byte)(value >>> 21 | 0x80));
/* 103 */             buffer.put((byte)(value >>> 28));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static long getLong(ByteBuffer buffer)
/*     */   {
/* 116 */     long v = buffer.get();
/* 117 */     long value = v & 0x7F;
/* 118 */     if ((v & 0x80) != 0L) {
/* 119 */       v = buffer.get();
/* 120 */       value |= (v & 0x7F) << 7;
/* 121 */       if ((v & 0x80) != 0L) {
/* 122 */         v = buffer.get();
/* 123 */         value |= (v & 0x7F) << 14;
/* 124 */         if ((v & 0x80) != 0L) {
/* 125 */           v = buffer.get();
/* 126 */           value |= (v & 0x7F) << 21;
/* 127 */           if ((v & 0x80) != 0L) {
/* 128 */             v = buffer.get();
/* 129 */             value |= (v & 0x7F) << 28;
/* 130 */             if ((v & 0x80) != 0L) {
/* 131 */               v = buffer.get();
/* 132 */               value |= (v & 0x7F) << 35;
/* 133 */               if ((v & 0x80) != 0L) {
/* 134 */                 v = buffer.get();
/* 135 */                 value |= (v & 0x7F) << 42;
/* 136 */                 if ((v & 0x80) != 0L) {
/* 137 */                   v = buffer.get();
/* 138 */                   value |= (v & 0x7F) << 49;
/* 139 */                   if ((v & 0x80) != 0L) {
/* 140 */                     v = buffer.get();
/* 141 */                     value |= v << 56;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 150 */     value = value >>> 1 ^ -(value & 1L);
/* 151 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int getInt(ByteBuffer buffer)
/*     */   {
/* 160 */     int v = buffer.get();
/* 161 */     int value = v & 0x7F;
/* 162 */     if ((v & 0x80) != 0) {
/* 163 */       v = buffer.get();
/* 164 */       value |= (v & 0x7F) << 7;
/* 165 */       if ((v & 0x80) != 0) {
/* 166 */         v = buffer.get();
/* 167 */         value |= (v & 0x7F) << 14;
/* 168 */         if ((v & 0x80) != 0) {
/* 169 */           v = buffer.get();
/* 170 */           value |= (v & 0x7F) << 21;
/* 171 */           if ((v & 0x80) != 0) {
/* 172 */             v = buffer.get();
/* 173 */             value |= (v & 0x7F) << 28;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 178 */     value = value >>> 1 ^ -(value & 0x1);
/* 179 */     return value;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\ZigZagEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */