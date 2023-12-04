/*    */ package com.facebook.presto.jdbc.internal.joda.time.tz;
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
/*    */ public class ZoneInfoLogger
/*    */ {
/* 23 */   static ThreadLocal<Boolean> cVerbose = new ThreadLocal() {
/*    */     protected Boolean initialValue() {
/* 25 */       return Boolean.FALSE;
/*    */     }
/*    */   };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean verbose()
/*    */   {
/* 34 */     return ((Boolean)cVerbose.get()).booleanValue();
/*    */   }
/*    */   
/*    */   public static void set(boolean paramBoolean) {
/* 38 */     cVerbose.set(Boolean.valueOf(paramBoolean));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\ZoneInfoLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */