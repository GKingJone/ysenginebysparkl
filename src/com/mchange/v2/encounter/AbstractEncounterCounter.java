/*    */ package com.mchange.v2.encounter;
/*    */ 
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
/*    */ class AbstractEncounterCounter
/*    */   implements EncounterCounter
/*    */ {
/* 30 */   static final Long ONE = new Long(1L);
/*    */   Map m;
/*    */   
/*    */   AbstractEncounterCounter(Map m) {
/* 34 */     this.m = m;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public long encounter(Object o)
/*    */   {
/* 41 */     Long oldLong = (Long)this.m.get(o);
/*    */     Long newLong;
/*    */     long out;
/* 44 */     Long newLong; if (oldLong == null)
/*    */     {
/* 46 */       long out = 0L;
/* 47 */       newLong = ONE;
/*    */     }
/*    */     else
/*    */     {
/* 51 */       out = oldLong.longValue();
/* 52 */       newLong = new Long(out + 1L);
/*    */     }
/* 54 */     this.m.put(o, newLong);
/* 55 */     return out;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\encounter\AbstractEncounterCounter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */