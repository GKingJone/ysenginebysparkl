/*    */ package com.mchange.v2.coalesce;
/*    */ 
/*    */ import java.util.WeakHashMap;
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
/*    */ class WeakEqualsCoalescer
/*    */   extends AbstractWeakCoalescer
/*    */ {
/*    */   WeakEqualsCoalescer()
/*    */   {
/* 32 */     super(new WeakHashMap());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\WeakEqualsCoalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */