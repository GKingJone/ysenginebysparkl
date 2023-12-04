/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLTransientException;
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
/*    */ public class MySQLTransientException
/*    */   extends SQLTransientException
/*    */ {
/*    */   static final long serialVersionUID = -1885878228558607563L;
/*    */   
/*    */   public MySQLTransientException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 34 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLTransientException(String reason, String SQLState) {
/* 38 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLTransientException(String reason) {
/* 42 */     super(reason);
/*    */   }
/*    */   
/*    */   public MySQLTransientException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLTransientException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */