/*    */ package com.mysql.jdbc.exceptions.jdbc4;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MySQLNonTransientException
/*    */   extends SQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = -8714521137552613517L;
/*    */   
/*    */   public MySQLNonTransientException() {}
/*    */   
/*    */   public MySQLNonTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 38 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientException(String reason, String SQLState) {
/* 42 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientException(String reason) {
/* 46 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLNonTransientException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */