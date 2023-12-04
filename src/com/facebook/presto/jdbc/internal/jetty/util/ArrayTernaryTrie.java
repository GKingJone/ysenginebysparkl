/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.PrintStream;
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
/*     */ public class ArrayTernaryTrie<V>
/*     */   extends AbstractTrie<V>
/*     */ {
/*  61 */   private static int LO = 1;
/*  62 */   private static int EQ = 2;
/*  63 */   private static int HI = 3;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int ROW_SIZE = 4;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final char[] _tree;
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
/*     */   private final V[] _value;
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
/*     */   public ArrayTernaryTrie()
/*     */   {
/* 100 */     this(128);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayTernaryTrie(boolean insensitive)
/*     */   {
/* 109 */     this(insensitive, 128);
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
/*     */   public ArrayTernaryTrie(int capacity)
/*     */   {
/* 123 */     this(true, capacity);
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
/*     */   public ArrayTernaryTrie(boolean insensitive, int capacity)
/*     */   {
/* 138 */     super(insensitive);
/* 139 */     this._value = ((Object[])new Object[capacity]);
/* 140 */     this._tree = new char[capacity * 4];
/* 141 */     this._key = new String[capacity];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayTernaryTrie(ArrayTernaryTrie<V> trie, double factor)
/*     */   {
/* 151 */     super(trie.isCaseInsensitive());
/* 152 */     int capacity = (int)(trie._value.length * factor);
/* 153 */     this._rows = trie._rows;
/* 154 */     this._value = Arrays.copyOf(trie._value, capacity);
/* 155 */     this._tree = Arrays.copyOf(trie._tree, capacity * 4);
/* 156 */     this._key = ((String[])Arrays.copyOf(trie._key, capacity));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 163 */     this._rows = '\000';
/* 164 */     Arrays.fill(this._value, null);
/* 165 */     Arrays.fill(this._tree, '\000');
/* 166 */     Arrays.fill(this._key, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean put(String s, V v)
/*     */   {
/* 173 */     int t = 0;
/* 174 */     int limit = s.length();
/* 175 */     int last = 0;
/* 176 */     for (int k = 0; k < limit; k++)
/*     */     {
/* 178 */       char c = s.charAt(k);
/* 179 */       if ((isCaseInsensitive()) && (c < '')) {
/* 180 */         c = StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 184 */         int row = 4 * t;
/*     */         
/*     */ 
/* 187 */         if (t == this._rows)
/*     */         {
/* 189 */           this._rows = ((char)(this._rows + '\001'));
/* 190 */           if (this._rows >= this._key.length)
/*     */           {
/* 192 */             this._rows = ((char)(this._rows - '\001'));
/* 193 */             return false;
/*     */           }
/* 195 */           this._tree[row] = c;
/*     */         }
/*     */         
/* 198 */         char n = this._tree[row];
/* 199 */         int diff = n - c;
/* 200 */         if (diff == 0) {
/* 201 */           t = this._tree[(last = row + EQ)];
/* 202 */         } else if (diff < 0) {
/* 203 */           t = this._tree[(last = row + LO)];
/*     */         } else {
/* 205 */           t = this._tree[(last = row + HI)];
/*     */         }
/*     */         
/* 208 */         if (t == 0)
/*     */         {
/* 210 */           t = this._rows;
/* 211 */           this._tree[last] = ((char)t);
/*     */         }
/*     */         
/* 214 */         if (diff == 0) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 220 */     if (t == this._rows)
/*     */     {
/* 222 */       this._rows = ((char)(this._rows + '\001'));
/* 223 */       if (this._rows >= this._key.length)
/*     */       {
/* 225 */         this._rows = ((char)(this._rows - '\001'));
/* 226 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 231 */     this._key[t] = (v == null ? null : s);
/* 232 */     this._value[t] = v;
/*     */     
/* 234 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public V get(String s, int offset, int len)
/*     */   {
/* 242 */     int t = 0;
/* 243 */     for (int i = 0; i < len;)
/*     */     {
/* 245 */       char c = s.charAt(offset + i++);
/* 246 */       if ((isCaseInsensitive()) && (c < '')) {
/* 247 */         c = StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 251 */         int row = 4 * t;
/* 252 */         char n = this._tree[row];
/* 253 */         int diff = n - c;
/*     */         
/* 255 */         if (diff == 0)
/*     */         {
/* 257 */           t = this._tree[(row + EQ)];
/* 258 */           if (t != 0) break;
/* 259 */           return null;
/*     */         }
/*     */         
/*     */ 
/* 263 */         t = this._tree[(row + hilo(diff))];
/* 264 */         if (t == 0) {
/* 265 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 269 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V get(ByteBuffer b, int offset, int len)
/*     */   {
/* 276 */     int t = 0;
/* 277 */     offset += b.position();
/*     */     
/* 279 */     for (int i = 0; i < len;)
/*     */     {
/* 281 */       byte c = (byte)(b.get(offset + i++) & 0x7F);
/* 282 */       if (isCaseInsensitive()) {
/* 283 */         c = (byte)StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 287 */         int row = 4 * t;
/* 288 */         char n = this._tree[row];
/* 289 */         int diff = n - c;
/*     */         
/* 291 */         if (diff == 0)
/*     */         {
/* 293 */           t = this._tree[(row + EQ)];
/* 294 */           if (t != 0) break;
/* 295 */           return null;
/*     */         }
/*     */         
/*     */ 
/* 299 */         t = this._tree[(row + hilo(diff))];
/* 300 */         if (t == 0) {
/* 301 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 305 */     return (V)this._value[t];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V getBest(String s)
/*     */   {
/* 312 */     return (V)getBest(0, s, 0, s.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public V getBest(String s, int offset, int length)
/*     */   {
/* 319 */     return (V)getBest(0, s, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   private V getBest(int t, String s, int offset, int len)
/*     */   {
/* 325 */     int node = t;
/* 326 */     int end = offset + len;
/* 327 */     while (offset < end)
/*     */     {
/* 329 */       char c = s.charAt(offset++);
/* 330 */       len--;
/* 331 */       if ((isCaseInsensitive()) && (c < '')) {
/* 332 */         c = StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 336 */         int row = 4 * t;
/* 337 */         char n = this._tree[row];
/* 338 */         int diff = n - c;
/*     */         
/* 340 */         if (diff == 0)
/*     */         {
/* 342 */           t = this._tree[(row + EQ)];
/* 343 */           if (t == 0) {
/*     */             break label157;
/*     */           }
/*     */           
/* 347 */           if (this._key[t] == null)
/*     */             break;
/* 349 */           node = t;
/* 350 */           V better = getBest(t, s, offset, len);
/* 351 */           if (better != null)
/* 352 */             return better;
/* 353 */           break;
/*     */         }
/*     */         
/*     */ 
/* 357 */         t = this._tree[(row + hilo(diff))];
/* 358 */         if (t == 0) break label157;
/*     */       }
/*     */     }
/*     */     label157:
/* 362 */     return (V)this._value[node];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public V getBest(ByteBuffer b, int offset, int len)
/*     */   {
/* 370 */     if (b.hasArray())
/* 371 */       return (V)getBest(0, b.array(), b.arrayOffset() + b.position() + offset, len);
/* 372 */     return (V)getBest(0, b, offset, len);
/*     */   }
/*     */   
/*     */ 
/*     */   private V getBest(int t, byte[] b, int offset, int len)
/*     */   {
/* 378 */     int node = t;
/* 379 */     int end = offset + len;
/* 380 */     while (offset < end)
/*     */     {
/* 382 */       byte c = (byte)(b[(offset++)] & 0x7F);
/* 383 */       len--;
/* 384 */       if (isCaseInsensitive()) {
/* 385 */         c = (byte)StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 389 */         int row = 4 * t;
/* 390 */         char n = this._tree[row];
/* 391 */         int diff = n - c;
/*     */         
/* 393 */         if (diff == 0)
/*     */         {
/* 395 */           t = this._tree[(row + EQ)];
/* 396 */           if (t == 0) {
/*     */             break label152;
/*     */           }
/*     */           
/* 400 */           if (this._key[t] == null)
/*     */             break;
/* 402 */           node = t;
/* 403 */           V better = getBest(t, b, offset, len);
/* 404 */           if (better != null)
/* 405 */             return better;
/* 406 */           break;
/*     */         }
/*     */         
/*     */ 
/* 410 */         t = this._tree[(row + hilo(diff))];
/* 411 */         if (t == 0) break label152;
/*     */       }
/*     */     }
/*     */     label152:
/* 415 */     return (V)this._value[node];
/*     */   }
/*     */   
/*     */ 
/*     */   private V getBest(int t, ByteBuffer b, int offset, int len)
/*     */   {
/* 421 */     int node = t;
/* 422 */     int o = offset + b.position();
/*     */     
/* 424 */     for (int i = 0; i < len; i++)
/*     */     {
/* 426 */       byte c = (byte)(b.get(o + i) & 0x7F);
/* 427 */       if (isCaseInsensitive()) {
/* 428 */         c = (byte)StringUtil.lowercases[c];
/*     */       }
/*     */       for (;;)
/*     */       {
/* 432 */         int row = 4 * t;
/* 433 */         char n = this._tree[row];
/* 434 */         int diff = n - c;
/*     */         
/* 436 */         if (diff == 0)
/*     */         {
/* 438 */           t = this._tree[(row + EQ)];
/* 439 */           if (t == 0) {
/*     */             break label171;
/*     */           }
/*     */           
/* 443 */           if (this._key[t] == null)
/*     */             break;
/* 445 */           node = t;
/* 446 */           V best = getBest(t, b, offset + i + 1, len - i - 1);
/* 447 */           if (best != null)
/* 448 */             return best;
/* 449 */           break;
/*     */         }
/*     */         
/*     */ 
/* 453 */         t = this._tree[(row + hilo(diff))];
/* 454 */         if (t == 0) break label171;
/*     */       }
/*     */     }
/*     */     label171:
/* 458 */     return (V)this._value[node];
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 464 */     StringBuilder buf = new StringBuilder();
/* 465 */     for (int r = 0; r <= this._rows; r++)
/*     */     {
/* 467 */       if ((this._key[r] != null) && (this._value[r] != null))
/*     */       {
/* 469 */         buf.append(',');
/* 470 */         buf.append(this._key[r]);
/* 471 */         buf.append('=');
/* 472 */         buf.append(this._value[r].toString());
/*     */       }
/*     */     }
/* 475 */     if (buf.length() == 0) {
/* 476 */       return "{}";
/*     */     }
/* 478 */     buf.setCharAt(0, '{');
/* 479 */     buf.append('}');
/* 480 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> keySet()
/*     */   {
/* 488 */     Set<String> keys = new HashSet();
/*     */     
/* 490 */     for (int r = 0; r <= this._rows; r++)
/*     */     {
/* 492 */       if ((this._key[r] != null) && (this._value[r] != null))
/* 493 */         keys.add(this._key[r]);
/*     */     }
/* 495 */     return keys;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/* 501 */     return this._rows + '\001' == this._key.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int hilo(int diff)
/*     */   {
/* 508 */     return 1 + (diff | 0x7FFFFFFF) / 1073741823;
/*     */   }
/*     */   
/*     */   public void dump()
/*     */   {
/* 513 */     for (int r = 0; r < this._rows; r++)
/*     */     {
/* 515 */       char c = this._tree[(r * 4 + 0)];
/* 516 */       System.err.printf("%4d [%s,%d,%d,%d] '%s':%s%n", new Object[] {
/* 517 */         Integer.valueOf(r), "'" + c + "'", 
/*     */         
/* 519 */         Integer.valueOf(this._tree[(r * 4 + LO)]), 
/* 520 */         Integer.valueOf(this._tree[(r * 4 + EQ)]), 
/* 521 */         Integer.valueOf(this._tree[(r * 4 + HI)]), this._key[r], this._value[r] });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ArrayTernaryTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */