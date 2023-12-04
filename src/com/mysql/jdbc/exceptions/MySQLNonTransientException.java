/*    */ package com.mysql.jdbc.exceptions;
/*    */ 
/*    */ import java.sql.SQLException;
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
/*    */ public class MySQLNonTransientException
/*    */   extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = -8714521137552613517L;
/*    */   
/*    */   public MySQLNonTransientException() {}
/*    */   
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLNonTransientException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */