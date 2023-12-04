/*    */ package com.mchange.v2.resourcepool;
/*    */ 
/*    */ import java.util.EventObject;
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
/*    */ public class ResourcePoolEvent
/*    */   extends EventObject
/*    */ {
/*    */   Object resc;
/*    */   boolean checked_out_resource;
/*    */   int pool_size;
/*    */   int available_size;
/*    */   int removed_but_unreturned_size;
/*    */   
/*    */   public ResourcePoolEvent(ResourcePool pool, Object resc, boolean checked_out_resource, int pool_size, int available_size, int removed_but_unreturned_size)
/*    */   {
/* 43 */     super(pool);
/* 44 */     this.resc = resc;
/* 45 */     this.checked_out_resource = checked_out_resource;
/* 46 */     this.pool_size = pool_size;
/* 47 */     this.available_size = available_size;
/* 48 */     this.removed_but_unreturned_size = removed_but_unreturned_size;
/*    */   }
/*    */   
/*    */   public Object getResource() {
/* 52 */     return this.resc;
/*    */   }
/*    */   
/* 55 */   public boolean isCheckedOutResource() { return this.checked_out_resource; }
/*    */   
/*    */   public int getPoolSize() {
/* 58 */     return this.pool_size;
/*    */   }
/*    */   
/* 61 */   public int getAvailableSize() { return this.available_size; }
/*    */   
/*    */   public int getRemovedButUnreturnedSize() {
/* 64 */     return this.removed_but_unreturned_size;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */