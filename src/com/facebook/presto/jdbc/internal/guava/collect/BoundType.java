/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ @GwtCompatible
/*    */ public enum BoundType
/*    */ {
/* 31 */   OPEN, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 40 */   CLOSED;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private BoundType() {}
/*    */   
/*    */ 
/*    */ 
/*    */   static BoundType forBoolean(boolean inclusive)
/*    */   {
/* 51 */     return inclusive ? CLOSED : OPEN;
/*    */   }
/*    */   
/*    */   abstract BoundType flip();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\BoundType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */