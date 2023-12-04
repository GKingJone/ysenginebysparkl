/*    */ package com.mchange.v1.util;
/*    */ 
/*    */ import com.mchange.util.AssertException;
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
/*    */ public class DebugUtils
/*    */ {
/*    */   public static void myAssert(boolean bool)
/*    */   {
/* 33 */     if (!bool) throw new AssertException();
/*    */   }
/*    */   
/* 36 */   public static void myAssert(boolean bool, String message) { if (!bool) throw new AssertException(message);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\DebugUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */