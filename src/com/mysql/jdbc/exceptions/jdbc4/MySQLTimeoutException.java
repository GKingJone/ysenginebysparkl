/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTimeoutException;
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
/*    */ public class MySQLTimeoutException
/*    */   extends SQLTimeoutException
/*    */ {
/*    */   static final long serialVersionUID = -789621240523230339L;
/*    */   
/*    */   public MySQLTimeoutException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTimeoutException() {
/* 45 */     super("Statement cancelled due to timeout or client request");
/*    */   }
/*    */   
/*    */   public int getErrorCode() {
/* 49 */     return super.getErrorCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */