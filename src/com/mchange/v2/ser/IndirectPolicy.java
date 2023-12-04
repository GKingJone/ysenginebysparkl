/*    */ package com.mchange.v2.ser;
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
/*    */ public final class IndirectPolicy
/*    */ {
/* 28 */   public static final IndirectPolicy DEFINITELY_INDIRECT = new IndirectPolicy("DEFINITELY_INDIRECT");
/* 29 */   public static final IndirectPolicy INDIRECT_ON_EXCEPTION = new IndirectPolicy("INDIRECT_ON_EXCEPTION");
/* 30 */   public static final IndirectPolicy DEFINITELY_DIRECT = new IndirectPolicy("DEFINITELY_DIRECT");
/*    */   String name;
/*    */   
/*    */   private IndirectPolicy(String name)
/*    */   {
/* 35 */     this.name = name;
/*    */   }
/*    */   
/* 38 */   public String toString() { return "[IndirectPolicy: " + this.name + ']'; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\ser\IndirectPolicy.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */