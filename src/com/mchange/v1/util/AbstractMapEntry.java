/*    */ package com.mchange.v1.util;
/*    */ 
/*    */ import com.mchange.v2.lang.ObjectUtils;
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
/*    */ 
/*    */ public abstract class AbstractMapEntry
/*    */   implements Map.Entry
/*    */ {
/*    */   public abstract Object getKey();
/*    */   
/*    */   public abstract Object getValue();
/*    */   
/*    */   public abstract Object setValue(Object paramObject);
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 39 */     if ((o instanceof Map.Entry))
/*    */     {
/* 41 */       Map.Entry other = (Map.Entry)o;
/* 42 */       return (ObjectUtils.eqOrBothNull(getKey(), other.getKey())) && (ObjectUtils.eqOrBothNull(getValue(), other.getValue()));
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 47 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 52 */     return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\AbstractMapEntry.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */