/*    */ package com.mchange.v1.lang;
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
/*    */ public class AmbiguousClassNameException
/*    */   extends Exception
/*    */ {
/*    */   AmbiguousClassNameException(String simpleName, Class c1, Class c2)
/*    */   {
/* 30 */     super(simpleName + " could refer either to " + c1.getName() + " or " + c2.getName());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\lang\AmbiguousClassNameException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */