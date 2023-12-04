/*    */ package com.facebook.presto.jdbc.internal.guava.escape;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static char[] charBufferFromThreadLocal()
/*    */   {
/* 32 */     return (char[])DEST_TL.get();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 40 */   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal()
/*    */   {
/*    */     protected char[] initialValue() {
/* 43 */       return new char['Ѐ'];
/*    */     }
/*    */   };
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\escape\Platform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */