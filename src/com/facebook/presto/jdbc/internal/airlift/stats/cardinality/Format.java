/*    */ package com.facebook.presto.jdbc.internal.airlift.stats.cardinality;
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
/*    */  enum Format
/*    */ {
/* 18 */   SPARSE_V1(0), 
/* 19 */   DENSE_V1(1), 
/* 20 */   SPARSE_V2(2), 
/* 21 */   DENSE_V2(3);
/*    */   
/*    */   private byte tag;
/*    */   
/*    */   private Format(int tag)
/*    */   {
/* 27 */     this.tag = ((byte)tag);
/*    */   }
/*    */   
/*    */   public byte getTag()
/*    */   {
/* 32 */     return this.tag;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\cardinality\Format.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */