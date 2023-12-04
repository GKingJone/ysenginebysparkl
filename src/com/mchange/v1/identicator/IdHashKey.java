/*    */ package com.mchange.v1.identicator;
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
/*    */ abstract class IdHashKey
/*    */ {
/*    */   Identicator id;
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
/*    */   public IdHashKey(Identicator id)
/*    */   {
/* 31 */     this.id = id;
/*    */   }
/*    */   
/*    */   public abstract Object getKeyObj();
/*    */   
/* 36 */   public Identicator getIdenticator() { return this.id; }
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   public abstract int hashCode();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\IdHashKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */