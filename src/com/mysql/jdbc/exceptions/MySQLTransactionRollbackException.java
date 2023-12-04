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
/*    */ 
/*    */ 
/*    */ public class MySQLTransactionRollbackException
/*    */   extends MySQLTransientException
/*    */   implements DeadlockTimeoutRollbackMarker
/*    */ {
/*    */   static final long serialVersionUID = 6034999468737801730L;
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
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 31 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException(String reason, String SQLState) {
/* 35 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException(String reason) {
/* 39 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTransactionRollbackException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLTransactionRollbackException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */