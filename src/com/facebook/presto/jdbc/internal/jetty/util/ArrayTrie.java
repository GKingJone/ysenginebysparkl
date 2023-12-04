/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
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
/*     */ public class ArrayTrie<V>
/*     */   extends AbstractTrie<V>
/*     */ {
/*     */   private static final int ROW_SIZE = 32;
/*  64 */   private static final int[] __lookup = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final char[] _rowIndex;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final String[] _key;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final V[] _value;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char[][] _bigIndex;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private char _rows;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayTrie()
/*     */   {
/* 116 */     this(128);
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
/*     */ 
/*     */   public ArrayTrie(int capacity)
/*     */   {
/* 131 */     super(true);
/* 132 */     this._value = ((Object[])new Object[capacity]);
/* 133 */     this._rowIndex = new char[capacity * 32];
/* 134 */     this._key = new String[capacity];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 141 */     this._rows = '\000';
/* 142 */     Arrays.fill(this._value, null);
/* 143 */     Arrays.fill(this._rowIndex, '\000');
/* 144 */     Arrays.fill(this._key, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean put(String s, V v)
/*     */   {
/* 151 */     int t = 0;
/*     */     
/* 153 */     int limit = s.length();
/* 154 */     for (int k = 0; k < limit; k++)
/*     */     {
/* 156 */       char c = s.charAt(k);
/*     */       
/* 158 */       int index = __lookup[(c & 0x7F)];
/* 159 */       if (index >= 0)
/*     */       {
/* 161 */         int idx = t * 32 + index;
/* 162 */         t = this._rowIndex[idx];
/* 163 */         if (t == 0)
/*     */         {
/* 165 */           if ((this._rows = (char)(this._rows + '\001')) >= this._value.length)
/* 166 */             return false;
/* 167 */           t = this._rowIndex[idx] = this._rows;
/*     */         }
/*     */       } else {
/* 170 */         if (c > '') {
/* 171 */           throw new IllegalArgumentException("non ascii character");
/*     */         }
/*     */         
/* 174 */         if (this._bigIndex == null)
/* 175 */           this._bigIndex = new char[this._value.length][];
/* 176 */         if (t >= this._bigIndex.length)
/* 177 */           return false;
/* 178 */         char[] big = this._bigIndex[t];
/* 179 */         if (big == null)
/* 180 */           big = this._bigIndex[t] = new char[''];
/* 181 */         t = big[c];
/* 182 */         if (t == 0)
/*     */         {
/* 184 */           if (this._rows == this._value.length)
/* 185 */             return false;
/* 186 */           t = big[c] = this._rows = (char)(this._rows + '\001');
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 191 */     if (t >= this._key.length)
/*     */     {
/* 193 */       this._rows = ((char)this._key.length);
/* 194 */       return false;
/*     */     }
/*     */     
/* 197 */     this._key[t] = (v == null ? null : s);
/* 198 */     this._value[t] = v;
/* 199 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V get(String s, int offset, int len)
/*     */   {
/* 206 */     int t = 0;
/* 207 */     for (int i = 0; i < len; i++)
/*     */     {
/* 209 */       char c = s.charAt(offset + i);
/* 210 */       int index = __lookup[(c & 0x7F)];
/* 211 */       if (index >= 0)
/*     */       {
/* 213 */         int idx = t * 32 + index;
/* 214 */         t = this._rowIndex[idx];
/* 215 */         if (t == 0) {
/* 216 */           return null;
/*     */         }
/*     */       }
/*     */       else {
/* 220 */         char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 221 */         if (big == null)
/* 222 */           return null;
/* 223 */         t = big[c];
/* 224 */         if (t == 0)
/* 225 */           return null;
/*     */       }
/*     */     }
/* 228 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V get(ByteBuffer b, int offset, int len)
/*     */   {
/* 235 */     int t = 0;
/* 236 */     for (int i = 0; i < len; i++)
/*     */     {
/* 238 */       byte c = b.get(offset + i);
/* 239 */       int index = __lookup[(c & 0x7F)];
/* 240 */       if (index >= 0)
/*     */       {
/* 242 */         int idx = t * 32 + index;
/* 243 */         t = this._rowIndex[idx];
/* 244 */         if (t == 0) {
/* 245 */           return null;
/*     */         }
/*     */       }
/*     */       else {
/* 249 */         char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 250 */         if (big == null)
/* 251 */           return null;
/* 252 */         t = big[c];
/* 253 */         if (t == 0)
/* 254 */           return null;
/*     */       }
/*     */     }
/* 257 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V getBest(byte[] b, int offset, int len)
/*     */   {
/* 264 */     return (V)getBest(0, b, offset, len);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V getBest(ByteBuffer b, int offset, int len)
/*     */   {
/* 271 */     if (b.hasArray())
/* 272 */       return (V)getBest(0, b.array(), b.arrayOffset() + b.position() + offset, len);
/* 273 */     return (V)getBest(0, b, offset, len);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V getBest(String s, int offset, int len)
/*     */   {
/* 280 */     return (V)getBest(0, s, offset, len);
/*     */   }
/*     */   
/*     */ 
/*     */   private V getBest(int t, String s, int offset, int len)
/*     */   {
/* 286 */     int pos = offset;
/* 287 */     for (int i = 0; i < len; i++)
/*     */     {
/* 289 */       char c = s.charAt(pos++);
/* 290 */       int index = __lookup[(c & 0x7F)];
/* 291 */       if (index >= 0)
/*     */       {
/* 293 */         int idx = t * 32 + index;
/* 294 */         int nt = this._rowIndex[idx];
/* 295 */         if (nt == 0)
/*     */           break;
/* 297 */         t = nt;
/*     */       }
/*     */       else
/*     */       {
/* 301 */         char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 302 */         if (big == null)
/* 303 */           return null;
/* 304 */         int nt = big[c];
/* 305 */         if (nt == 0)
/*     */           break;
/* 307 */         t = nt;
/*     */       }
/*     */       
/*     */ 
/* 311 */       if (this._key[t] != null)
/*     */       {
/*     */ 
/* 314 */         V best = getBest(t, s, offset + i + 1, len - i - 1);
/* 315 */         if (best != null)
/* 316 */           return best;
/* 317 */         return (V)this._value[t];
/*     */       }
/*     */     }
/* 320 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */   private V getBest(int t, byte[] b, int offset, int len)
/*     */   {
/* 326 */     for (int i = 0; i < len; i++)
/*     */     {
/* 328 */       byte c = b[(offset + i)];
/* 329 */       int index = __lookup[(c & 0x7F)];
/* 330 */       if (index >= 0)
/*     */       {
/* 332 */         int idx = t * 32 + index;
/* 333 */         int nt = this._rowIndex[idx];
/* 334 */         if (nt == 0)
/*     */           break;
/* 336 */         t = nt;
/*     */       }
/*     */       else
/*     */       {
/* 340 */         char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 341 */         if (big == null)
/* 342 */           return null;
/* 343 */         int nt = big[c];
/* 344 */         if (nt == 0)
/*     */           break;
/* 346 */         t = nt;
/*     */       }
/*     */       
/*     */ 
/* 350 */       if (this._key[t] != null)
/*     */       {
/*     */ 
/* 353 */         V best = getBest(t, b, offset + i + 1, len - i - 1);
/* 354 */         if (best == null) break;
/* 355 */         return best;
/*     */       }
/*     */     }
/*     */     
/* 359 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */   private V getBest(int t, ByteBuffer b, int offset, int len)
/*     */   {
/* 364 */     int pos = b.position() + offset;
/* 365 */     for (int i = 0; i < len; i++)
/*     */     {
/* 367 */       byte c = b.get(pos++);
/* 368 */       int index = __lookup[(c & 0x7F)];
/* 369 */       if (index >= 0)
/*     */       {
/* 371 */         int idx = t * 32 + index;
/* 372 */         int nt = this._rowIndex[idx];
/* 373 */         if (nt == 0)
/*     */           break;
/* 375 */         t = nt;
/*     */       }
/*     */       else
/*     */       {
/* 379 */         char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 380 */         if (big == null)
/* 381 */           return null;
/* 382 */         int nt = big[c];
/* 383 */         if (nt == 0)
/*     */           break;
/* 385 */         t = nt;
/*     */       }
/*     */       
/*     */ 
/* 389 */       if (this._key[t] != null)
/*     */       {
/*     */ 
/* 392 */         V best = getBest(t, b, offset + i + 1, len - i - 1);
/* 393 */         if (best == null) break;
/* 394 */         return best;
/*     */       }
/*     */     }
/*     */     
/* 398 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 407 */     StringBuilder buf = new StringBuilder();
/* 408 */     toString(buf, 0);
/*     */     
/* 410 */     if (buf.length() == 0) {
/* 411 */       return "{}";
/*     */     }
/* 413 */     buf.setCharAt(0, '{');
/* 414 */     buf.append('}');
/* 415 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   private void toString(Appendable out, int t)
/*     */   {
/* 421 */     if (this._value[t] != null)
/*     */     {
/*     */       try
/*     */       {
/* 425 */         out.append(',');
/* 426 */         out.append(this._key[t]);
/* 427 */         out.append('=');
/* 428 */         out.append(this._value[t].toString());
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 432 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */     int idx;
/* 436 */     for (int i = 0; i < 32; i++)
/*     */     {
/* 438 */       idx = t * 32 + i;
/* 439 */       if (this._rowIndex[idx] != 0) {
/* 440 */         toString(out, this._rowIndex[idx]);
/*     */       }
/*     */     }
/* 443 */     char[] big = this._bigIndex == null ? null : this._bigIndex[t];
/* 444 */     if (big != null)
/*     */     {
/* 446 */       for (int i : big) {
/* 447 */         if (i != 0) {
/* 448 */           toString(out, i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Set<String> keySet()
/*     */   {
/* 456 */     Set<String> keys = new HashSet();
/* 457 */     keySet(keys, 0);
/* 458 */     return keys;
/*     */   }
/*     */   
/*     */   private void keySet(Set<String> set, int t)
/*     */   {
/* 463 */     if ((t < this._value.length) && (this._value[t] != null))
/* 464 */       set.add(this._key[t]);
/*     */     int idx;
/* 466 */     for (int i = 0; i < 32; i++)
/*     */     {
/* 468 */       idx = t * 32 + i;
/* 469 */       if ((idx < this._rowIndex.length) && (this._rowIndex[idx] != 0)) {
/* 470 */         keySet(set, this._rowIndex[idx]);
/*     */       }
/*     */     }
/* 473 */     char[] big = (this._bigIndex == null) || (t >= this._bigIndex.length) ? null : this._bigIndex[t];
/* 474 */     if (big != null)
/*     */     {
/* 476 */       for (int i : big) {
/* 477 */         if (i != 0) {
/* 478 */           keySet(set, i);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isFull() {
/* 485 */     return this._rows + '\001' >= this._key.length;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ArrayTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */