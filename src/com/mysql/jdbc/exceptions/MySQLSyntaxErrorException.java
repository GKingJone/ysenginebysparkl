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
/*    */ public class MySQLSyntaxErrorException
/*    */   extends MySQLNonTransientException
/*    */ {
/*    */   static final long serialVersionUID = 6919059513432113764L;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public MySQLSyntaxErrorException() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 35 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState) {
/* 39 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLSyntaxErrorException(String reason) {
/* 43 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\MySQLSyntaxErrorException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */