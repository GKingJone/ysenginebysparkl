/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLSyntaxErrorException;
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
/*    */ public class MySQLSyntaxErrorException
/*    */   extends SQLSyntaxErrorException
/*    */ {
/*    */   static final long serialVersionUID = 6919059513432113764L;
/*    */   
/*    */   public MySQLSyntaxErrorException() {}
/*    */   
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLSyntaxErrorException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLSyntaxErrorException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLSyntaxErrorException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */