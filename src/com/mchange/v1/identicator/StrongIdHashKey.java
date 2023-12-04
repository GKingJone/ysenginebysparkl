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
/*    */ 
/*    */ final class StrongIdHashKey
/*    */   extends IdHashKey
/*    */ {
/*    */   Object keyObj;
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
/*    */   public StrongIdHashKey(Object keyObj, Identicator id)
/*    */   {
/* 34 */     super(id);
/* 35 */     this.keyObj = keyObj;
/*    */   }
/*    */   
/*    */   public Object getKeyObj() {
/* 39 */     return this.keyObj;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 44 */     if ((o instanceof StrongIdHashKey)) {
/* 45 */       return this.id.identical(this.keyObj, ((StrongIdHashKey)o).keyObj);
/*    */     }
/* 47 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 51 */     return this.id.hash(this.keyObj);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\StrongIdHashKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */