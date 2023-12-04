/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map.Entry;
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
/*    */ public class LRUCache
/*    */   extends LinkedHashMap<Object, Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected int maxElements;
/*    */   
/*    */   public LRUCache(int maxSize)
/*    */   {
/* 34 */     super(maxSize, 0.75F, true);
/* 35 */     this.maxElements = maxSize;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest)
/*    */   {
/* 45 */     return size() > this.maxElements;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\LRUCache.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */