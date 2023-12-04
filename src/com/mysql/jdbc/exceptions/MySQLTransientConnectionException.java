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
/*    */ public class MySQLTransientConnectionException
/*    */   extends MySQLTransientException
/*    */ {
/*    */   static final long serialVersionUID = 8699144578759941201L;
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
/*    */   public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 31 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException(String reason, String SQLState) {
/* 35 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException(String reason) {
/* 39 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLTransientConnectionException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */