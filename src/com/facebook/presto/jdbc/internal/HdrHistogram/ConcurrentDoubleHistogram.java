/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConcurrentDoubleHistogram
/*    */   extends DoubleHistogram
/*    */ {
/*    */   public ConcurrentDoubleHistogram(int numberOfSignificantValueDigits)
/*    */   {
/* 66 */     this(2L, numberOfSignificantValueDigits);
/* 67 */     setAutoResize(true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*    */   {
/* 80 */     super(highestToLowestValueRatio, numberOfSignificantValueDigits, ConcurrentHistogram.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ConcurrentDoubleHistogram(ConcurrentDoubleHistogram source)
/*    */   {
/* 89 */     super(source);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\ConcurrentDoubleHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */