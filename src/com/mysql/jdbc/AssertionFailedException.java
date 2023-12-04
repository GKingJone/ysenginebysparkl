/*    */ package com.mysql.jdbc;
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
/*    */ public class AssertionFailedException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
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
/*    */   public static void shouldNotHappen(Exception ex)
/*    */     throws AssertionFailedException
/*    */   {
/* 42 */     throw new AssertionFailedException(ex);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AssertionFailedException(Exception ex)
/*    */   {
/* 53 */     super(Messages.getString("AssertionFailedException.0") + ex.toString() + Messages.getString("AssertionFailedException.1"));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\AssertionFailedException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */