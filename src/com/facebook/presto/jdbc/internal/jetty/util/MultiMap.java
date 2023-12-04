/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
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
/*     */ public class MultiMap<V>
/*     */   extends HashMap<String, List<V>>
/*     */ {
/*     */   public MultiMap() {}
/*     */   
/*     */   public MultiMap(Map<String, List<V>> map)
/*     */   {
/*  43 */     super(map);
/*     */   }
/*     */   
/*     */   public MultiMap(MultiMap<V> map)
/*     */   {
/*  48 */     super(map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<V> getValues(String name)
/*     */   {
/*  60 */     List<V> vals = (List)super.get(name);
/*  61 */     if ((vals == null) || (vals.isEmpty())) {
/*  62 */       return null;
/*     */     }
/*  64 */     return vals;
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
/*     */   public V getValue(String name, int i)
/*     */   {
/*  77 */     List<V> vals = getValues(name);
/*  78 */     if (vals == null) {
/*  79 */       return null;
/*     */     }
/*  81 */     if ((i == 0) && (vals.isEmpty())) {
/*  82 */       return null;
/*     */     }
/*  84 */     return (V)vals.get(i);
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
/*     */   public String getString(String name)
/*     */   {
/*  98 */     List<V> vals = (List)get(name);
/*  99 */     if ((vals == null) || (vals.isEmpty()))
/*     */     {
/* 101 */       return null;
/*     */     }
/*     */     
/* 104 */     if (vals.size() == 1)
/*     */     {
/*     */ 
/* 107 */       return vals.get(0).toString();
/*     */     }
/*     */     
/*     */ 
/* 111 */     StringBuilder values = new StringBuilder(128);
/* 112 */     for (V e : vals)
/*     */     {
/* 114 */       if (e != null)
/*     */       {
/* 116 */         if (values.length() > 0)
/* 117 */           values.append(',');
/* 118 */         values.append(e.toString());
/*     */       }
/*     */     }
/* 121 */     return values.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<V> put(String name, V value)
/*     */   {
/* 132 */     if (value == null) {
/* 133 */       return (List)super.put(name, null);
/*     */     }
/* 135 */     List<V> vals = new ArrayList();
/* 136 */     vals.add(value);
/* 137 */     return (List)put(name, vals);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putAllValues(Map<String, V> input)
/*     */   {
/* 146 */     for (Entry<String, V> entry : input.entrySet())
/*     */     {
/* 148 */       put((String)entry.getKey(), entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<V> putValues(String name, List<V> values)
/*     */   {
/* 160 */     return (List)super.put(name, values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @SafeVarargs
/*     */   public final List<V> putValues(String name, V... values)
/*     */   {
/* 172 */     List<V> list = new ArrayList();
/* 173 */     list.addAll(Arrays.asList(values));
/* 174 */     return (List)super.put(name, list);
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
/*     */   public void add(String name, V value)
/*     */   {
/* 187 */     List<V> lo = (List)get(name);
/* 188 */     if (lo == null) {
/* 189 */       lo = new ArrayList();
/*     */     }
/* 191 */     lo.add(value);
/* 192 */     super.put(name, lo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addValues(String name, List<V> values)
/*     */   {
/* 204 */     List<V> lo = (List)get(name);
/* 205 */     if (lo == null) {
/* 206 */       lo = new ArrayList();
/*     */     }
/* 208 */     lo.addAll(values);
/* 209 */     put(name, lo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addValues(String name, V[] values)
/*     */   {
/* 221 */     List<V> lo = (List)get(name);
/* 222 */     if (lo == null) {
/* 223 */       lo = new ArrayList();
/*     */     }
/* 225 */     lo.addAll(Arrays.asList(values));
/* 226 */     put(name, lo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean addAllValues(MultiMap<V> map)
/*     */   {
/* 238 */     boolean merged = false;
/*     */     
/* 240 */     if ((map == null) || (map.isEmpty()))
/*     */     {
/*     */ 
/* 243 */       return merged;
/*     */     }
/*     */     
/* 246 */     for (Entry<String, List<V>> entry : map.entrySet())
/*     */     {
/* 248 */       String name = (String)entry.getKey();
/* 249 */       List<V> values = (List)entry.getValue();
/*     */       
/* 251 */       if (containsKey(name))
/*     */       {
/* 253 */         merged = true;
/*     */       }
/*     */       
/* 256 */       addValues(name, values);
/*     */     }
/*     */     
/* 259 */     return merged;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean removeValue(String name, V value)
/*     */   {
/* 270 */     List<V> lo = (List)get(name);
/* 271 */     if ((lo == null) || (lo.isEmpty())) {
/* 272 */       return false;
/*     */     }
/* 274 */     boolean ret = lo.remove(value);
/* 275 */     if (lo.isEmpty()) {
/* 276 */       remove(name);
/*     */     } else {
/* 278 */       put(name, lo);
/*     */     }
/* 280 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsSimpleValue(V value)
/*     */   {
/* 292 */     for (List<V> vals : values())
/*     */     {
/* 294 */       if ((vals.size() == 1) && (vals.contains(value)))
/*     */       {
/* 296 */         return true;
/*     */       }
/*     */     }
/* 299 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 305 */     Iterator<Entry<String, List<V>>> iter = entrySet().iterator();
/* 306 */     StringBuilder sb = new StringBuilder();
/* 307 */     sb.append('{');
/* 308 */     boolean delim = false;
/* 309 */     while (iter.hasNext())
/*     */     {
/* 311 */       Entry<String, List<V>> e = (Entry)iter.next();
/* 312 */       if (delim)
/*     */       {
/* 314 */         sb.append(", ");
/*     */       }
/* 316 */       String key = (String)e.getKey();
/* 317 */       List<V> vals = (List)e.getValue();
/* 318 */       sb.append(key);
/* 319 */       sb.append('=');
/* 320 */       if (vals.size() == 1)
/*     */       {
/* 322 */         sb.append(vals.get(0));
/*     */       }
/*     */       else
/*     */       {
/* 326 */         sb.append(vals);
/*     */       }
/* 328 */       delim = true;
/*     */     }
/* 330 */     sb.append('}');
/* 331 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, String[]> toStringArrayMap()
/*     */   {
/* 340 */     HashMap<String, String[]> map = new HashMap(size() * 3 / 2)
/*     */     {
/*     */ 
/*     */       public String toString()
/*     */       {
/* 345 */         StringBuilder b = new StringBuilder();
/* 346 */         b.append('{');
/* 347 */         for (String k : super.keySet())
/*     */         {
/* 349 */           if (b.length() > 1)
/* 350 */             b.append(',');
/* 351 */           b.append(k);
/* 352 */           b.append('=');
/* 353 */           b.append(Arrays.asList((Object[])super.get(k)));
/*     */         }
/*     */         
/* 356 */         b.append('}');
/* 357 */         return b.toString();
/*     */       }
/*     */     };
/*     */     
/* 361 */     for (Entry<String, List<V>> entry : entrySet())
/*     */     {
/* 363 */       String[] a = null;
/* 364 */       if (entry.getValue() != null)
/*     */       {
/* 366 */         a = new String[((List)entry.getValue()).size()];
/* 367 */         a = (String[])((List)entry.getValue()).toArray(a);
/*     */       }
/* 369 */       map.put(entry.getKey(), a);
/*     */     }
/* 371 */     return map;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MultiMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */