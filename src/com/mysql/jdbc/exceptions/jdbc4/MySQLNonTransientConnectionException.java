/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLNonTransientConnectionException;
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
/*    */ public class MySQLNonTransientConnectionException
/*    */   extends SQLNonTransientConnectionException
/*    */ {
/*    */   static final long serialVersionUID = -3050543822763367670L;
/*    */   
/*    */   public MySQLNonTransientConnectionException() {}
/*    */   
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientConnectionException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLNonTransientConnectionException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLNonTransientConnectionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */