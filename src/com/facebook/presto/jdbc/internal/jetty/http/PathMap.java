/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTernaryTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class PathMap<O>
/*     */   extends HashMap<String, O>
/*     */ {
/*  81 */   private static String __pathSpecSeparators = ":,";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setPathSpecSeparators(String s)
/*     */   {
/*  92 */     __pathSpecSeparators = s;
/*     */   }
/*     */   
/*     */ 
/*  96 */   Trie<MappedEntry<O>> _prefixMap = new ArrayTernaryTrie(false);
/*  97 */   Trie<MappedEntry<O>> _suffixMap = new ArrayTernaryTrie(false);
/*  98 */   final Map<String, MappedEntry<O>> _exactMap = new HashMap();
/*     */   
/* 100 */   List<MappedEntry<O>> _defaultSingletonList = null;
/* 101 */   MappedEntry<O> _prefixDefault = null;
/* 102 */   MappedEntry<O> _default = null;
/* 103 */   boolean _nodefault = false;
/*     */   
/*     */ 
/*     */   public PathMap()
/*     */   {
/* 108 */     this(11);
/*     */   }
/*     */   
/*     */ 
/*     */   public PathMap(boolean noDefault)
/*     */   {
/* 114 */     this(11, noDefault);
/*     */   }
/*     */   
/*     */ 
/*     */   public PathMap(int capacity)
/*     */   {
/* 120 */     this(capacity, false);
/*     */   }
/*     */   
/*     */ 
/*     */   private PathMap(int capacity, boolean noDefault)
/*     */   {
/* 126 */     super(capacity);
/* 127 */     this._nodefault = noDefault;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PathMap(Map<String, ? extends O> dictMap)
/*     */   {
/* 137 */     putAll(dictMap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public O put(String pathSpec, O object)
/*     */   {
/* 149 */     if ("".equals(pathSpec.trim()))
/*     */     {
/* 151 */       MappedEntry<O> entry = new MappedEntry("", object);
/* 152 */       entry.setMapped("");
/* 153 */       this._exactMap.put("", entry);
/* 154 */       return (O)super.put("", object);
/*     */     }
/*     */     
/* 157 */     StringTokenizer tok = new StringTokenizer(pathSpec, __pathSpecSeparators);
/* 158 */     O old = null;
/*     */     
/* 160 */     while (tok.hasMoreTokens())
/*     */     {
/* 162 */       String spec = tok.nextToken();
/*     */       
/* 164 */       if ((!spec.startsWith("/")) && (!spec.startsWith("*."))) {
/* 165 */         throw new IllegalArgumentException("PathSpec " + spec + ". must start with '/' or '*.'");
/*     */       }
/* 167 */       old = super.put(spec, object);
/*     */       
/*     */ 
/* 170 */       MappedEntry<O> entry = new MappedEntry(spec, object);
/*     */       
/* 172 */       if (entry.getKey().equals(spec))
/*     */       {
/* 174 */         if (spec.equals("/*")) {
/* 175 */           this._prefixDefault = entry;
/* 176 */         } else if (spec.endsWith("/*"))
/*     */         {
/* 178 */           String mapped = spec.substring(0, spec.length() - 2);
/* 179 */           entry.setMapped(mapped);
/* 180 */           while (!this._prefixMap.put(mapped, entry)) {
/* 181 */             this._prefixMap = new ArrayTernaryTrie((ArrayTernaryTrie)this._prefixMap, 1.5D);
/*     */           }
/* 183 */         } else if (spec.startsWith("*."))
/*     */         {
/* 185 */           String suffix = spec.substring(2);
/* 186 */           while (!this._suffixMap.put(suffix, entry)) {
/* 187 */             this._suffixMap = new ArrayTernaryTrie((ArrayTernaryTrie)this._suffixMap, 1.5D);
/*     */           }
/* 189 */         } else if (spec.equals("/"))
/*     */         {
/* 191 */           if (this._nodefault) {
/* 192 */             this._exactMap.put(spec, entry);
/*     */           }
/*     */           else {
/* 195 */             this._default = entry;
/* 196 */             this._defaultSingletonList = Collections.singletonList(this._default);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 201 */           entry.setMapped(spec);
/* 202 */           this._exactMap.put(spec, entry);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 207 */     return old;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public O match(String path)
/*     */   {
/* 217 */     MappedEntry<O> entry = getMatch(path);
/* 218 */     if (entry != null)
/* 219 */       return (O)entry.getValue();
/* 220 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MappedEntry<O> getMatch(String path)
/*     */   {
/* 231 */     if (path == null) {
/* 232 */       return null;
/*     */     }
/* 234 */     int l = path.length();
/*     */     
/* 236 */     MappedEntry<O> entry = null;
/*     */     
/*     */ 
/* 239 */     if ((l == 1) && (path.charAt(0) == '/'))
/*     */     {
/* 241 */       entry = (MappedEntry)this._exactMap.get("");
/* 242 */       if (entry != null) {
/* 243 */         return entry;
/*     */       }
/*     */     }
/*     */     
/* 247 */     entry = (MappedEntry)this._exactMap.get(path);
/* 248 */     if (entry != null) {
/* 249 */       return entry;
/*     */     }
/*     */     
/* 252 */     int i = l;
/* 253 */     Trie<MappedEntry<O>> prefix_map = this._prefixMap;
/* 254 */     while (i >= 0)
/*     */     {
/* 256 */       entry = (MappedEntry)prefix_map.getBest(path, 0, i);
/* 257 */       if (entry == null)
/*     */         break;
/* 259 */       String key = entry.getKey();
/* 260 */       if ((key.length() - 2 >= path.length()) || (path.charAt(key.length() - 2) == '/'))
/* 261 */         return entry;
/* 262 */       i = key.length() - 3;
/*     */     }
/*     */     
/*     */ 
/* 266 */     if (this._prefixDefault != null) {
/* 267 */       return this._prefixDefault;
/*     */     }
/*     */     
/* 270 */     i = 0;
/* 271 */     Trie<MappedEntry<O>> suffix_map = this._suffixMap;
/* 272 */     while ((i = path.indexOf('.', i + 1)) > 0)
/*     */     {
/* 274 */       entry = (MappedEntry)suffix_map.get(path, i + 1, l - i - 1);
/* 275 */       if (entry != null) {
/* 276 */         return entry;
/*     */       }
/*     */     }
/*     */     
/* 280 */     return this._default;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<? extends Entry<String, O>> getMatches(String path)
/*     */   {
/* 292 */     List<MappedEntry<O>> entries = new ArrayList();
/*     */     
/* 294 */     if (path == null)
/* 295 */       return entries;
/* 296 */     if (path.length() == 0) {
/* 297 */       return this._defaultSingletonList;
/*     */     }
/*     */     
/* 300 */     MappedEntry<O> entry = (MappedEntry)this._exactMap.get(path);
/* 301 */     if (entry != null) {
/* 302 */       entries.add(entry);
/*     */     }
/*     */     
/* 305 */     int l = path.length();
/* 306 */     int i = l;
/* 307 */     Trie<MappedEntry<O>> prefix_map = this._prefixMap;
/* 308 */     while (i >= 0)
/*     */     {
/* 310 */       entry = (MappedEntry)prefix_map.getBest(path, 0, i);
/* 311 */       if (entry == null)
/*     */         break;
/* 313 */       String key = entry.getKey();
/* 314 */       if ((key.length() - 2 >= path.length()) || (path.charAt(key.length() - 2) == '/')) {
/* 315 */         entries.add(entry);
/*     */       }
/* 317 */       i = key.length() - 3;
/*     */     }
/*     */     
/*     */ 
/* 321 */     if (this._prefixDefault != null) {
/* 322 */       entries.add(this._prefixDefault);
/*     */     }
/*     */     
/* 325 */     i = 0;
/* 326 */     Trie<MappedEntry<O>> suffix_map = this._suffixMap;
/* 327 */     while ((i = path.indexOf('.', i + 1)) > 0)
/*     */     {
/* 329 */       entry = (MappedEntry)suffix_map.get(path, i + 1, l - i - 1);
/* 330 */       if (entry != null) {
/* 331 */         entries.add(entry);
/*     */       }
/*     */     }
/*     */     
/* 335 */     if ("/".equals(path))
/*     */     {
/* 337 */       entry = (MappedEntry)this._exactMap.get("");
/* 338 */       if (entry != null) {
/* 339 */         entries.add(entry);
/*     */       }
/*     */     }
/*     */     
/* 343 */     if (this._default != null) {
/* 344 */       entries.add(this._default);
/*     */     }
/* 346 */     return entries;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsMatch(String path)
/*     */   {
/* 358 */     MappedEntry<?> match = getMatch(path);
/* 359 */     return (match != null) && (!match.equals(this._default));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public O remove(Object pathSpec)
/*     */   {
/* 366 */     if (pathSpec != null)
/*     */     {
/* 368 */       String spec = (String)pathSpec;
/* 369 */       if (spec.equals("/*")) {
/* 370 */         this._prefixDefault = null;
/* 371 */       } else if (spec.endsWith("/*")) {
/* 372 */         this._prefixMap.remove(spec.substring(0, spec.length() - 2));
/* 373 */       } else if (spec.startsWith("*.")) {
/* 374 */         this._suffixMap.remove(spec.substring(2));
/* 375 */       } else if (spec.equals("/"))
/*     */       {
/* 377 */         this._default = null;
/* 378 */         this._defaultSingletonList = null;
/*     */       }
/*     */       else {
/* 381 */         this._exactMap.remove(spec);
/*     */       } }
/* 383 */     return (O)super.remove(pathSpec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/* 390 */     this._exactMap.clear();
/* 391 */     this._prefixMap = new ArrayTernaryTrie(false);
/* 392 */     this._suffixMap = new ArrayTernaryTrie(false);
/* 393 */     this._default = null;
/* 394 */     this._defaultSingletonList = null;
/* 395 */     this._prefixDefault = null;
/* 396 */     super.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean match(String pathSpec, String path)
/*     */   {
/* 407 */     return match(pathSpec, path, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean match(String pathSpec, String path, boolean noDefault)
/*     */   {
/* 419 */     if (pathSpec.length() == 0) {
/* 420 */       return "/".equals(path);
/*     */     }
/* 422 */     char c = pathSpec.charAt(0);
/* 423 */     if (c == '/')
/*     */     {
/* 425 */       if (((!noDefault) && (pathSpec.length() == 1)) || (pathSpec.equals(path))) {
/* 426 */         return true;
/*     */       }
/* 428 */       if (isPathWildcardMatch(pathSpec, path)) {
/* 429 */         return true;
/*     */       }
/* 431 */     } else if (c == '*') {
/* 432 */       return path.regionMatches(path.length() - pathSpec.length() + 1, pathSpec, 1, pathSpec
/* 433 */         .length() - 1); }
/* 434 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean isPathWildcardMatch(String pathSpec, String path)
/*     */   {
/* 441 */     int cpl = pathSpec.length() - 2;
/* 442 */     if ((pathSpec.endsWith("/*")) && (path.regionMatches(0, pathSpec, 0, cpl)))
/*     */     {
/* 444 */       if ((path.length() == cpl) || ('/' == path.charAt(cpl)))
/* 445 */         return true;
/*     */     }
/* 447 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String pathMatch(String pathSpec, String path)
/*     */   {
/* 459 */     char c = pathSpec.charAt(0);
/*     */     
/* 461 */     if (c == '/')
/*     */     {
/* 463 */       if (pathSpec.length() == 1) {
/* 464 */         return path;
/*     */       }
/* 466 */       if (pathSpec.equals(path)) {
/* 467 */         return path;
/*     */       }
/* 469 */       if (isPathWildcardMatch(pathSpec, path)) {
/* 470 */         return path.substring(0, pathSpec.length() - 2);
/*     */       }
/* 472 */     } else if (c == '*')
/*     */     {
/* 474 */       if (path.regionMatches(path.length() - (pathSpec.length() - 1), pathSpec, 1, pathSpec
/* 475 */         .length() - 1))
/* 476 */         return path;
/*     */     }
/* 478 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String pathInfo(String pathSpec, String path)
/*     */   {
/* 489 */     if ("".equals(pathSpec)) {
/* 490 */       return path;
/*     */     }
/* 492 */     char c = pathSpec.charAt(0);
/*     */     
/* 494 */     if (c == '/')
/*     */     {
/* 496 */       if (pathSpec.length() == 1) {
/* 497 */         return null;
/*     */       }
/* 499 */       boolean wildcard = isPathWildcardMatch(pathSpec, path);
/*     */       
/*     */ 
/* 502 */       if ((pathSpec.equals(path)) && (!wildcard)) {
/* 503 */         return null;
/*     */       }
/* 505 */       if (wildcard)
/*     */       {
/* 507 */         if (path.length() == pathSpec.length() - 2)
/* 508 */           return null;
/* 509 */         return path.substring(pathSpec.length() - 2);
/*     */       }
/*     */     }
/* 512 */     return null;
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
/*     */   public static String relativePath(String base, String pathSpec, String path)
/*     */   {
/* 527 */     String info = pathInfo(pathSpec, path);
/* 528 */     if (info == null) {
/* 529 */       info = path;
/*     */     }
/* 531 */     if (info.startsWith("./"))
/* 532 */       info = info.substring(2);
/* 533 */     if (base.endsWith("/")) {
/* 534 */       if (info.startsWith("/")) {
/* 535 */         path = base + info.substring(1);
/*     */       } else {
/* 537 */         path = base + info;
/*     */       }
/* 539 */     } else if (info.startsWith("/")) {
/* 540 */       path = base + info;
/*     */     } else
/* 542 */       path = base + "/" + info;
/* 543 */     return path;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class MappedEntry<O>
/*     */     implements Entry<String, O>
/*     */   {
/*     */     private final String key;
/*     */     
/*     */     private final O value;
/*     */     private String mapped;
/*     */     
/*     */     MappedEntry(String key, O value)
/*     */     {
/* 557 */       this.key = key;
/* 558 */       this.value = value;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getKey()
/*     */     {
/* 564 */       return this.key;
/*     */     }
/*     */     
/*     */ 
/*     */     public O getValue()
/*     */     {
/* 570 */       return (O)this.value;
/*     */     }
/*     */     
/*     */ 
/*     */     public O setValue(O o)
/*     */     {
/* 576 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 582 */       return this.key + "=" + this.value;
/*     */     }
/*     */     
/*     */     public String getMapped()
/*     */     {
/* 587 */       return this.mapped;
/*     */     }
/*     */     
/*     */     void setMapped(String mapped)
/*     */     {
/* 592 */       this.mapped = mapped;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class PathSet extends AbstractSet<String> implements Predicate<String>
/*     */   {
/* 598 */     private final PathMap<Boolean> _map = new PathMap();
/*     */     
/*     */ 
/*     */     public Iterator<String> iterator()
/*     */     {
/* 603 */       return this._map.keySet().iterator();
/*     */     }
/*     */     
/*     */ 
/*     */     public int size()
/*     */     {
/* 609 */       return this._map.size();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean add(String item)
/*     */     {
/* 615 */       return this._map.put(item, Boolean.TRUE) == null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean remove(Object item)
/*     */     {
/* 621 */       return this._map.remove(item) != null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 627 */       return this._map.containsKey(o);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean test(String s)
/*     */     {
/* 633 */       return this._map.containsMatch(s);
/*     */     }
/*     */     
/*     */     public boolean containsMatch(String s)
/*     */     {
/* 638 */       return this._map.containsMatch(s);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\PathMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */