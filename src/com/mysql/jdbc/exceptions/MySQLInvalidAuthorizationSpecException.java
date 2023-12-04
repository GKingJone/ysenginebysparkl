/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLInvalidAuthorizationSpecException
/*    */   extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = 6878889837492500030L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 35 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState) {
/* 39 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException(String reason) {
/* 43 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLInvalidAuthorizationSpecException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */