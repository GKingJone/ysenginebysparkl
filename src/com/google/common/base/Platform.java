/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static char[] charBufferFromThreadLocal()
/*    */   {
/* 32 */     return (char[])DEST_TL.get();
/*    */   }
/*    */   
/*    */   static long systemNanoTime()
/*    */   {
/* 37 */     return System.nanoTime();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 45 */   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal()
/*    */   {
/*    */     protected char[] initialValue() {
/* 48 */       return new char['Ѐ'];
/*    */     }
/*    */   };
/*    */   
/*    */   static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
/* 53 */     return matcher.precomputedInternal();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Platform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */