/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
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
/*    */ public abstract class FixedLengthSliceInput
/*    */   extends SliceInput
/*    */ {
/*    */   public abstract long length();
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
/*    */   public final long remaining()
/*    */   {
/* 29 */     return length() - position();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\FixedLengthSliceInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */