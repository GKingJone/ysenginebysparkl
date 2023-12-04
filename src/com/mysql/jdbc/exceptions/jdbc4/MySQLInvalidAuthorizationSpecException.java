/*    */ package com.mysql.jdbc.exceptions.jdbc4;
/*    */ 
/*    */ import java.sql.SQLInvalidAuthorizationSpecException;
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
/*    */ public class MySQLInvalidAuthorizationSpecException
/*    */   extends SQLInvalidAuthorizationSpecException
/*    */ {
/*    */   static final long serialVersionUID = 6878889837492500030L;
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException() {}
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState, int vendorCode)
/*    */   {
/* 37 */     super(reason, SQLState, vendorCode);
/*    */   }
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException(String reason, String SQLState) {
/* 41 */     super(reason, SQLState);
/*    */   }
/*    */   
/*    */   public MySQLInvalidAuthorizationSpecException(String reason) {
/* 45 */     super(reason);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\exceptions\jdbc4\MySQLInvalidAuthorizationSpecException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */