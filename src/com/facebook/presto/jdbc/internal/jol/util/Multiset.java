/*    */ package com.facebook.presto.jdbc.internal.jol.util;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Multiset<T>
/*    */ {
/* 39 */   private final Map<T, Integer> map = new HashMap();
/*    */   
/*    */   public void add(T t) {
/* 42 */     add(t, 1);
/*    */   }
/*    */   
/*    */   public void add(T key, int count) {
/* 46 */     Integer v = (Integer)this.map.get(key);
/* 47 */     if (v == null) {
/* 48 */       v = Integer.valueOf(0);
/*    */     }
/* 50 */     v = Integer.valueOf(v.intValue() + count);
/* 51 */     this.map.put(key, v);
/*    */   }
/*    */   
/*    */   public int count(T key) {
/* 55 */     Integer v = (Integer)this.map.get(key);
/* 56 */     return v == null ? 0 : v.intValue();
/*    */   }
/*    */   
/*    */   public Collection<T> keys() {
/* 60 */     return this.map.keySet();
/*    */   }
/*    */   
/*    */   public long size() {
/* 64 */     long size = 0L;
/* 65 */     for (T k : keys()) {
/* 66 */       size += count(k);
/*    */     }
/* 68 */     return size;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\util\Multiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */