/*    */ package com.mchange.v1.util;
/*    */ 
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
/*    */ 
/*    */ public class SimpleMapEntry
/*    */   extends AbstractMapEntry
/*    */   implements Map.Entry
/*    */ {
/*    */   Object key;
/*    */   Object value;
/*    */   
/*    */   public SimpleMapEntry(Object key, Object value)
/*    */   {
/* 35 */     this.key = key;
/* 36 */     this.value = value;
/*    */   }
/*    */   
/*    */   public Object getKey() {
/* 40 */     return this.key;
/*    */   }
/*    */   
/* 43 */   public Object getValue() { return this.value; }
/*    */   
/*    */   public Object setValue(Object value)
/*    */   {
/* 47 */     Object old = value;
/* 48 */     this.value = value;
/* 49 */     return old;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\SimpleMapEntry.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */