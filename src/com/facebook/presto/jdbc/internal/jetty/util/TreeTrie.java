/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
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
/*     */ public class TreeTrie<V>
/*     */   extends AbstractTrie<V>
/*     */ {
/*  47 */   private static final int[] __lookup = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, -1, 27, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 28, 29, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final int INDEX = 32;
/*     */   
/*     */ 
/*     */ 
/*     */   private final TreeTrie<V>[] _nextIndex;
/*     */   
/*     */ 
/*     */ 
/*  60 */   private final List<TreeTrie<V>> _nextOther = new ArrayList();
/*     */   private final char _c;
/*     */   private String _key;
/*     */   private V _value;
/*     */   
/*     */   public TreeTrie()
/*     */   {
/*  67 */     super(true);
/*  68 */     this._nextIndex = new TreeTrie[32];
/*  69 */     this._c = '\000';
/*     */   }
/*     */   
/*     */   private TreeTrie(char c)
/*     */   {
/*  74 */     super(true);
/*  75 */     this._nextIndex = new TreeTrie[32];
/*  76 */     this._c = c;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/*  82 */     Arrays.fill(this._nextIndex, null);
/*  83 */     this._nextOther.clear();
/*  84 */     this._key = null;
/*  85 */     this._value = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean put(String s, V v)
/*     */   {
/*  91 */     TreeTrie<V> t = this;
/*  92 */     int limit = s.length();
/*  93 */     for (int k = 0; k < limit; k++)
/*     */     {
/*  95 */       char c = s.charAt(k);
/*     */       
/*  97 */       int index = (c >= 0) && (c < '') ? __lookup[c] : -1;
/*  98 */       if (index >= 0)
/*     */       {
/* 100 */         if (t._nextIndex[index] == null)
/* 101 */           t._nextIndex[index] = new TreeTrie(c);
/* 102 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 106 */         TreeTrie<V> n = null;
/* 107 */         for (int i = t._nextOther.size(); i-- > 0;)
/*     */         {
/* 109 */           n = (TreeTrie)t._nextOther.get(i);
/* 110 */           if (n._c == c)
/*     */             break;
/* 112 */           n = null;
/*     */         }
/* 114 */         if (n == null)
/*     */         {
/* 116 */           n = new TreeTrie(c);
/* 117 */           t._nextOther.add(n);
/*     */         }
/* 119 */         t = n;
/*     */       }
/*     */     }
/* 122 */     t._key = (v == null ? null : s);
/* 123 */     t._value = v;
/* 124 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public V get(String s, int offset, int len)
/*     */   {
/* 130 */     TreeTrie<V> t = this;
/* 131 */     for (int i = 0; i < len; i++)
/*     */     {
/* 133 */       char c = s.charAt(offset + i);
/* 134 */       int index = (c >= 0) && (c < '') ? __lookup[c] : -1;
/* 135 */       if (index >= 0)
/*     */       {
/* 137 */         if (t._nextIndex[index] == null)
/* 138 */           return null;
/* 139 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 143 */         TreeTrie<V> n = null;
/* 144 */         for (int j = t._nextOther.size(); j-- > 0;)
/*     */         {
/* 146 */           n = (TreeTrie)t._nextOther.get(j);
/* 147 */           if (n._c == c)
/*     */             break;
/* 149 */           n = null;
/*     */         }
/* 151 */         if (n == null)
/* 152 */           return null;
/* 153 */         t = n;
/*     */       }
/*     */     }
/* 156 */     return (V)t._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public V get(ByteBuffer b, int offset, int len)
/*     */   {
/* 162 */     TreeTrie<V> t = this;
/* 163 */     for (int i = 0; i < len; i++)
/*     */     {
/* 165 */       byte c = b.get(offset + i);
/* 166 */       int index = (c >= 0) && (c < Byte.MAX_VALUE) ? __lookup[c] : -1;
/* 167 */       if (index >= 0)
/*     */       {
/* 169 */         if (t._nextIndex[index] == null)
/* 170 */           return null;
/* 171 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 175 */         TreeTrie<V> n = null;
/* 176 */         for (int j = t._nextOther.size(); j-- > 0;)
/*     */         {
/* 178 */           n = (TreeTrie)t._nextOther.get(j);
/* 179 */           if (n._c == c)
/*     */             break;
/* 181 */           n = null;
/*     */         }
/* 183 */         if (n == null)
/* 184 */           return null;
/* 185 */         t = n;
/*     */       }
/*     */     }
/* 188 */     return (V)t._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public V getBest(byte[] b, int offset, int len)
/*     */   {
/* 194 */     TreeTrie<V> t = this;
/* 195 */     for (int i = 0; i < len; i++)
/*     */     {
/* 197 */       byte c = b[(offset + i)];
/* 198 */       int index = (c >= 0) && (c < Byte.MAX_VALUE) ? __lookup[c] : -1;
/* 199 */       if (index >= 0)
/*     */       {
/* 201 */         if (t._nextIndex[index] == null)
/*     */           break;
/* 203 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 207 */         TreeTrie<V> n = null;
/* 208 */         for (int j = t._nextOther.size(); j-- > 0;)
/*     */         {
/* 210 */           n = (TreeTrie)t._nextOther.get(j);
/* 211 */           if (n._c == c)
/*     */             break;
/* 213 */           n = null;
/*     */         }
/* 215 */         if (n == null)
/*     */           break;
/* 217 */         t = n;
/*     */       }
/*     */       
/*     */ 
/* 221 */       if (t._key != null)
/*     */       {
/*     */ 
/* 224 */         V best = t.getBest(b, offset + i + 1, len - i - 1);
/* 225 */         if (best == null) break;
/* 226 */         return best;
/*     */       }
/*     */     }
/*     */     
/* 230 */     return (V)t._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public V getBest(String s, int offset, int len)
/*     */   {
/* 236 */     TreeTrie<V> t = this;
/* 237 */     for (int i = 0; i < len; i++)
/*     */     {
/* 239 */       byte c = (byte)(0xFF & s.charAt(offset + i));
/* 240 */       int index = (c >= 0) && (c < Byte.MAX_VALUE) ? __lookup[c] : -1;
/* 241 */       if (index >= 0)
/*     */       {
/* 243 */         if (t._nextIndex[index] == null)
/*     */           break;
/* 245 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 249 */         TreeTrie<V> n = null;
/* 250 */         for (int j = t._nextOther.size(); j-- > 0;)
/*     */         {
/* 252 */           n = (TreeTrie)t._nextOther.get(j);
/* 253 */           if (n._c == c)
/*     */             break;
/* 255 */           n = null;
/*     */         }
/* 257 */         if (n == null)
/*     */           break;
/* 259 */         t = n;
/*     */       }
/*     */       
/*     */ 
/* 263 */       if (t._key != null)
/*     */       {
/*     */ 
/* 266 */         V best = t.getBest(s, offset + i + 1, len - i - 1);
/* 267 */         if (best == null) break;
/* 268 */         return best;
/*     */       }
/*     */     }
/*     */     
/* 272 */     return (V)t._value;
/*     */   }
/*     */   
/*     */ 
/*     */   public V getBest(ByteBuffer b, int offset, int len)
/*     */   {
/* 278 */     if (b.hasArray())
/* 279 */       return (V)getBest(b.array(), b.arrayOffset() + b.position() + offset, len);
/* 280 */     return (V)getBestByteBuffer(b, offset, len);
/*     */   }
/*     */   
/*     */   private V getBestByteBuffer(ByteBuffer b, int offset, int len)
/*     */   {
/* 285 */     TreeTrie<V> t = this;
/* 286 */     int pos = b.position() + offset;
/* 287 */     for (int i = 0; i < len; i++)
/*     */     {
/* 289 */       byte c = b.get(pos++);
/* 290 */       int index = (c >= 0) && (c < Byte.MAX_VALUE) ? __lookup[c] : -1;
/* 291 */       if (index >= 0)
/*     */       {
/* 293 */         if (t._nextIndex[index] == null)
/*     */           break;
/* 295 */         t = t._nextIndex[index];
/*     */       }
/*     */       else
/*     */       {
/* 299 */         TreeTrie<V> n = null;
/* 300 */         for (int j = t._nextOther.size(); j-- > 0;)
/*     */         {
/* 302 */           n = (TreeTrie)t._nextOther.get(j);
/* 303 */           if (n._c == c)
/*     */             break;
/* 305 */           n = null;
/*     */         }
/* 307 */         if (n == null)
/*     */           break;
/* 309 */         t = n;
/*     */       }
/*     */       
/*     */ 
/* 313 */       if (t._key != null)
/*     */       {
/*     */ 
/* 316 */         V best = t.getBest(b, offset + i + 1, len - i - 1);
/* 317 */         if (best == null) break;
/* 318 */         return best;
/*     */       }
/*     */     }
/*     */     
/* 322 */     return (V)t._value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 329 */     StringBuilder buf = new StringBuilder();
/* 330 */     toString(buf, this);
/*     */     
/* 332 */     if (buf.length() == 0) {
/* 333 */       return "{}";
/*     */     }
/* 335 */     buf.setCharAt(0, '{');
/* 336 */     buf.append('}');
/* 337 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static <V> void toString(Appendable out, TreeTrie<V> t) {
/*     */     int i;
/* 342 */     if (t != null)
/*     */     {
/* 344 */       if (t._value != null)
/*     */       {
/*     */         try
/*     */         {
/* 348 */           out.append(',');
/* 349 */           out.append(t._key);
/* 350 */           out.append('=');
/* 351 */           out.append(t._value.toString());
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 355 */           throw new RuntimeException(e);
/*     */         }
/*     */       }
/*     */       
/* 359 */       for (int i = 0; i < 32; i++)
/*     */       {
/* 361 */         if (t._nextIndex[i] != null)
/* 362 */           toString(out, t._nextIndex[i]);
/*     */       }
/* 364 */       for (i = t._nextOther.size(); i-- > 0;) {
/* 365 */         toString(out, (TreeTrie)t._nextOther.get(i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Set<String> keySet()
/*     */   {
/* 372 */     Set<String> keys = new HashSet();
/* 373 */     keySet(keys, this);
/* 374 */     return keys;
/*     */   }
/*     */   
/*     */   private static <V> void keySet(Set<String> set, TreeTrie<V> t) {
/*     */     int i;
/* 379 */     if (t != null)
/*     */     {
/* 381 */       if (t._key != null) {
/* 382 */         set.add(t._key);
/*     */       }
/* 384 */       for (int i = 0; i < 32; i++)
/*     */       {
/* 386 */         if (t._nextIndex[i] != null)
/* 387 */           keySet(set, t._nextIndex[i]);
/*     */       }
/* 389 */       for (i = t._nextOther.size(); i-- > 0;) {
/* 390 */         keySet(set, (TreeTrie)t._nextOther.get(i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isFull()
/*     */   {
/* 397 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\TreeTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */