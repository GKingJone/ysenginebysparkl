/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LazyList
/*     */   implements Cloneable, Serializable
/*     */ {
/*  66 */   private static final String[] __EMTPY_STRING_ARRAY = new String[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object add(Object list, Object item)
/*     */   {
/*  81 */     if (list == null)
/*     */     {
/*  83 */       if (((item instanceof List)) || (item == null))
/*     */       {
/*  85 */         List<Object> l = new ArrayList();
/*  86 */         l.add(item);
/*  87 */         return l;
/*     */       }
/*     */       
/*  90 */       return item;
/*     */     }
/*     */     
/*  93 */     if ((list instanceof List))
/*     */     {
/*  95 */       ((List)list).add(item);
/*  96 */       return list;
/*     */     }
/*     */     
/*  99 */     List<Object> l = new ArrayList();
/* 100 */     l.add(list);
/* 101 */     l.add(item);
/* 102 */     return l;
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
/*     */   public static Object add(Object list, int index, Object item)
/*     */   {
/* 115 */     if (list == null)
/*     */     {
/* 117 */       if ((index > 0) || ((item instanceof List)) || (item == null))
/*     */       {
/* 119 */         List<Object> l = new ArrayList();
/* 120 */         l.add(index, item);
/* 121 */         return l;
/*     */       }
/* 123 */       return item;
/*     */     }
/*     */     
/* 126 */     if ((list instanceof List))
/*     */     {
/* 128 */       ((List)list).add(index, item);
/* 129 */       return list;
/*     */     }
/*     */     
/* 132 */     List<Object> l = new ArrayList();
/* 133 */     l.add(list);
/* 134 */     l.add(index, item);
/* 135 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object addCollection(Object list, Collection<?> collection)
/*     */   {
/* 146 */     Iterator<?> i = collection.iterator();
/* 147 */     while (i.hasNext())
/* 148 */       list = add(list, i.next());
/* 149 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object addArray(Object list, Object[] array)
/*     */   {
/* 160 */     for (int i = 0; (array != null) && (i < array.length); i++)
/* 161 */       list = add(list, array[i]);
/* 162 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object ensureSize(Object list, int initialSize)
/*     */   {
/* 173 */     if (list == null)
/* 174 */       return new ArrayList(initialSize);
/* 175 */     if ((list instanceof ArrayList))
/*     */     {
/* 177 */       ArrayList<?> ol = (ArrayList)list;
/* 178 */       if (ol.size() > initialSize)
/* 179 */         return ol;
/* 180 */       ArrayList<Object> nl = new ArrayList(initialSize);
/* 181 */       nl.addAll(ol);
/* 182 */       return nl;
/*     */     }
/* 184 */     List<Object> l = new ArrayList(initialSize);
/* 185 */     l.add(list);
/* 186 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Object remove(Object list, Object o)
/*     */   {
/* 192 */     if (list == null) {
/* 193 */       return null;
/*     */     }
/* 195 */     if ((list instanceof List))
/*     */     {
/* 197 */       List<?> l = (List)list;
/* 198 */       l.remove(o);
/* 199 */       if (l.size() == 0)
/* 200 */         return null;
/* 201 */       return list;
/*     */     }
/*     */     
/* 204 */     if (list.equals(o))
/* 205 */       return null;
/* 206 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */   public static Object remove(Object list, int i)
/*     */   {
/* 212 */     if (list == null) {
/* 213 */       return null;
/*     */     }
/* 215 */     if ((list instanceof List))
/*     */     {
/* 217 */       List<?> l = (List)list;
/* 218 */       l.remove(i);
/* 219 */       if (l.size() == 0)
/* 220 */         return null;
/* 221 */       return list;
/*     */     }
/*     */     
/* 224 */     if (i == 0)
/* 225 */       return null;
/* 226 */     return list;
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
/*     */   public static <E> List<E> getList(Object list)
/*     */   {
/* 241 */     return getList(list, false);
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
/*     */ 
/*     */ 
/*     */   public static <E> List<E> getList(Object list, boolean nullForEmpty)
/*     */   {
/* 258 */     if (list == null)
/*     */     {
/* 260 */       if (nullForEmpty)
/* 261 */         return null;
/* 262 */       return Collections.emptyList();
/*     */     }
/* 264 */     if ((list instanceof List)) {
/* 265 */       return (List)list;
/*     */     }
/* 267 */     return Collections.singletonList(list);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean hasEntry(Object list)
/*     */   {
/* 279 */     if (list == null)
/* 280 */       return false;
/* 281 */     if ((list instanceof List))
/* 282 */       return !((List)list).isEmpty();
/* 283 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isEmpty(Object list)
/*     */   {
/* 295 */     if (list == null)
/* 296 */       return true;
/* 297 */     if ((list instanceof List))
/* 298 */       return ((List)list).isEmpty();
/* 299 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String[] toStringArray(Object list)
/*     */   {
/* 306 */     if (list == null) {
/* 307 */       return __EMTPY_STRING_ARRAY;
/*     */     }
/* 309 */     if ((list instanceof List))
/*     */     {
/* 311 */       List<?> l = (List)list;
/* 312 */       String[] a = new String[l.size()];
/* 313 */       for (int i = l.size(); i-- > 0;)
/*     */       {
/* 315 */         Object o = l.get(i);
/* 316 */         if (o != null)
/* 317 */           a[i] = o.toString();
/*     */       }
/* 319 */       return a;
/*     */     }
/*     */     
/* 322 */     return new String[] { list.toString() };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object toArray(Object list, Class<?> clazz)
/*     */   {
/* 333 */     if (list == null) {
/* 334 */       return Array.newInstance(clazz, 0);
/*     */     }
/* 336 */     if ((list instanceof List))
/*     */     {
/* 338 */       List<?> l = (List)list;
/* 339 */       if (clazz.isPrimitive())
/*     */       {
/* 341 */         Object a = Array.newInstance(clazz, l.size());
/* 342 */         for (int i = 0; i < l.size(); i++)
/* 343 */           Array.set(a, i, l.get(i));
/* 344 */         return a;
/*     */       }
/* 346 */       return l.toArray((Object[])Array.newInstance(clazz, l.size()));
/*     */     }
/*     */     
/*     */ 
/* 350 */     Object a = Array.newInstance(clazz, 1);
/* 351 */     Array.set(a, 0, list);
/* 352 */     return a;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int size(Object list)
/*     */   {
/* 362 */     if (list == null)
/* 363 */       return 0;
/* 364 */     if ((list instanceof List))
/* 365 */       return ((List)list).size();
/* 366 */     return 1;
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
/*     */   public static <E> E get(Object list, int i)
/*     */   {
/* 379 */     if (list == null) {
/* 380 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 382 */     if ((list instanceof List)) {
/* 383 */       return (E)((List)list).get(i);
/*     */     }
/* 385 */     if (i == 0) {
/* 386 */       return (E)list;
/*     */     }
/* 388 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean contains(Object list, Object item)
/*     */   {
/* 394 */     if (list == null) {
/* 395 */       return false;
/*     */     }
/* 397 */     if ((list instanceof List)) {
/* 398 */       return ((List)list).contains(item);
/*     */     }
/* 400 */     return list.equals(item);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Object clone(Object list)
/*     */   {
/* 407 */     if (list == null)
/* 408 */       return null;
/* 409 */     if ((list instanceof List))
/* 410 */       return new ArrayList((List)list);
/* 411 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String toString(Object list)
/*     */   {
/* 417 */     if (list == null)
/* 418 */       return "[]";
/* 419 */     if ((list instanceof List))
/* 420 */       return list.toString();
/* 421 */     return "[" + list + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <E> Iterator<E> iterator(Object list)
/*     */   {
/* 428 */     if (list == null)
/*     */     {
/* 430 */       List<E> empty = Collections.emptyList();
/* 431 */       return empty.iterator();
/*     */     }
/* 433 */     if ((list instanceof List))
/*     */     {
/* 435 */       return ((List)list).iterator();
/*     */     }
/* 437 */     List<E> l = getList(list);
/* 438 */     return l.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <E> ListIterator<E> listIterator(Object list)
/*     */   {
/* 445 */     if (list == null)
/*     */     {
/* 447 */       List<E> empty = Collections.emptyList();
/* 448 */       return empty.listIterator();
/*     */     }
/* 450 */     if ((list instanceof List)) {
/* 451 */       return ((List)list).listIterator();
/*     */     }
/* 453 */     List<E> l = getList(list);
/* 454 */     return l.listIterator();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\LazyList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */