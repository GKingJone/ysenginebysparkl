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
/*    */ 
/*    */ public class MySQLTimeoutException
/*    */   extends MySQLTransientException
/*    */ {
/*    */   static final long serialVersionUID = -789621240523230339L;
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
/*    */   public MySQLTimeoutException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 31 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason, String SQLState) {
/* 35 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason) {
/* 39 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException() {
/* 43 */     super("Statement cancelled due to timeout or client request");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLTimeoutException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */