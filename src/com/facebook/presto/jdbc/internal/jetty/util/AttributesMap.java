/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AttributesMap
/*     */   implements Attributes
/*     */ {
/*  33 */   private final AtomicReference<ConcurrentMap<String, Object>> _map = new AtomicReference();
/*     */   
/*     */ 
/*     */   public AttributesMap() {}
/*     */   
/*     */ 
/*     */   public AttributesMap(AttributesMap attributes)
/*     */   {
/*  41 */     ConcurrentMap<String, Object> map = attributes.map();
/*  42 */     if (map != null) {
/*  43 */       this._map.set(new ConcurrentHashMap(map));
/*     */     }
/*     */   }
/*     */   
/*     */   private ConcurrentMap<String, Object> map() {
/*  48 */     return (ConcurrentMap)this._map.get();
/*     */   }
/*     */   
/*     */   private ConcurrentMap<String, Object> ensureMap()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  55 */       ConcurrentMap<String, Object> map = map();
/*  56 */       if (map != null)
/*  57 */         return map;
/*  58 */       map = new ConcurrentHashMap();
/*  59 */       if (this._map.compareAndSet(null, map)) {
/*  60 */         return map;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeAttribute(String name)
/*     */   {
/*  67 */     Map<String, Object> map = map();
/*  68 */     if (map != null) {
/*  69 */       map.remove(name);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAttribute(String name, Object attribute)
/*     */   {
/*  75 */     if (attribute == null) {
/*  76 */       removeAttribute(name);
/*     */     } else {
/*  78 */       ensureMap().put(name, attribute);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getAttribute(String name)
/*     */   {
/*  84 */     Map<String, Object> map = map();
/*  85 */     return map == null ? null : map.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */   public Enumeration<String> getAttributeNames()
/*     */   {
/*  91 */     return Collections.enumeration(getAttributeNameSet());
/*     */   }
/*     */   
/*     */   public Set<String> getAttributeNameSet()
/*     */   {
/*  96 */     return keySet();
/*     */   }
/*     */   
/*     */   public Set<Entry<String, Object>> getAttributeEntrySet()
/*     */   {
/* 101 */     Map<String, Object> map = map();
/* 102 */     return map == null ? Collections.emptySet() : map.entrySet();
/*     */   }
/*     */   
/*     */   public static Enumeration<String> getAttributeNamesCopy(Attributes attrs)
/*     */   {
/* 107 */     if ((attrs instanceof AttributesMap)) {
/* 108 */       return Collections.enumeration(((AttributesMap)attrs).keySet());
/*     */     }
/* 110 */     List<String> names = new ArrayList();
/* 111 */     names.addAll(Collections.list(attrs.getAttributeNames()));
/* 112 */     return Collections.enumeration(names);
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearAttributes()
/*     */   {
/* 118 */     Map<String, Object> map = map();
/* 119 */     if (map != null) {
/* 120 */       map.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public int size() {
/* 125 */     Map<String, Object> map = map();
/* 126 */     return map == null ? 0 : map.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 132 */     Map<String, Object> map = map();
/* 133 */     return map == null ? "{}" : map.toString();
/*     */   }
/*     */   
/*     */   private Set<String> keySet()
/*     */   {
/* 138 */     Map<String, Object> map = map();
/* 139 */     return map == null ? Collections.emptySet() : map.keySet();
/*     */   }
/*     */   
/*     */   public void addAll(Attributes attributes)
/*     */   {
/* 144 */     Enumeration<String> e = attributes.getAttributeNames();
/* 145 */     while (e.hasMoreElements())
/*     */     {
/* 147 */       String name = (String)e.nextElement();
/* 148 */       setAttribute(name, attributes.getAttribute(name));
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\AttributesMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */