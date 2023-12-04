/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientConnectionException;
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
/*    */ public class MySQLTransientConnectionException
/*    */   extends SQLTransientConnectionException
/*    */ {
/*    */   static final long serialVersionUID = 8699144578759941201L;
/*    */   
/*    */   public MySQLTransientConnectionException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 33 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException(String reason, String SQLState) {
/* 37 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException(String reason) {
/* 41 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTransientConnectionException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLTransientConnectionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */