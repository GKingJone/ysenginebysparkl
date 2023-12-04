/*    */ package com.mchange.v2.c3p0;
/*    */ 
/*    */ import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
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
/*    */ public final class PoolBackedDataSource
/*    */   extends AbstractPoolBackedDataSource
/*    */   implements PooledDataSource
/*    */ {
/*    */   public PoolBackedDataSource(boolean autoregister)
/*    */   {
/* 31 */     super(autoregister);
/*    */   }
/*    */   
/* 34 */   public PoolBackedDataSource() { this(true); }
/*    */   
/*    */   public PoolBackedDataSource(String configName) {
/* 37 */     super(configName);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\PoolBackedDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */