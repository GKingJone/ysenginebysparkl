/*    */ package com.mchange.v1.identicator;
/*    */ 
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
/*    */ public final class IdHashMap
/*    */   extends IdMap
/*    */   implements Map
/*    */ {
/*    */   public IdHashMap(Identicator id)
/*    */   {
/* 31 */     super(new HashMap(), id);
/*    */   }
/*    */   
/* 34 */   protected IdHashKey createIdKey(Object o) { return new StrongIdHashKey(o, this.id); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\IdHashMap.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */