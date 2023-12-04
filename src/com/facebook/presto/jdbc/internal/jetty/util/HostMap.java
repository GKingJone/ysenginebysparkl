/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HostMap<TYPE>
/*     */   extends HashMap<String, TYPE>
/*     */ {
/*     */   public HostMap()
/*     */   {
/*  38 */     super(11);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HostMap(int capacity)
/*     */   {
/*  48 */     super(capacity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TYPE put(String host, TYPE object)
/*     */     throws IllegalArgumentException
/*     */   {
/*  59 */     return (TYPE)super.put(host, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TYPE get(Object key)
/*     */   {
/*  69 */     return (TYPE)super.get(key);
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
/*     */   public Object getLazyMatches(String host)
/*     */   {
/*  82 */     if (host == null) {
/*  83 */       return LazyList.getList(super.entrySet());
/*     */     }
/*  85 */     int idx = 0;
/*  86 */     String domain = host.trim();
/*  87 */     HashSet<String> domains = new HashSet();
/*     */     do {
/*  89 */       domains.add(domain);
/*  90 */       if ((idx = domain.indexOf('.')) > 0)
/*     */       {
/*  92 */         domain = domain.substring(idx + 1);
/*     */       }
/*  94 */     } while (idx > 0);
/*     */     
/*  96 */     Object entries = null;
/*  97 */     for (Map.Entry<String, TYPE> entry : super.entrySet())
/*     */     {
/*  99 */       if (domains.contains(entry.getKey()))
/*     */       {
/* 101 */         entries = LazyList.add(entries, entry);
/*     */       }
/*     */     }
/*     */     
/* 105 */     return entries;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\HostMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */