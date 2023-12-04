/*    */ package com.facebook.presto.jdbc;
/*    */ 
/*    */ import java.sql.SQLNonTransientException;
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
/*    */ public class NotImplementedException
/*    */   extends SQLNonTransientException
/*    */ {
/*    */   public NotImplementedException(String reason)
/*    */   {
/* 28 */     super(reason);
/*    */   }
/*    */   
/*    */   public NotImplementedException(String clazz, String method)
/*    */   {
/* 33 */     this(String.format("Method %s.%s is not yet implemented", new Object[] { clazz, method }));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\NotImplementedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */