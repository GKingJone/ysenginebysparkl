/*    */ package com.mysql.fabric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FabricStateResponse<T>
/*    */ {
/*    */   private T data;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private long expireTimeMillis;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public FabricStateResponse(T data, int secsTtl)
/*    */   {
/* 31 */     this.data = data;
/* 32 */     this.expireTimeMillis = (System.currentTimeMillis() + 1000 * secsTtl);
/*    */   }
/*    */   
/*    */   public FabricStateResponse(T data, long expireTimeMillis) {
/* 36 */     this.data = data;
/* 37 */     this.expireTimeMillis = expireTimeMillis;
/*    */   }
/*    */   
/*    */   public T getData() {
/* 41 */     return (T)this.data;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public long getExpireTimeMillis()
/*    */   {
/* 48 */     return this.expireTimeMillis;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\FabricStateResponse.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */