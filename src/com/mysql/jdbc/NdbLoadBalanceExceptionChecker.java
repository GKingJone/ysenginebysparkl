/*    */ package com.mysql.jdbc;
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
/*    */ public class NdbLoadBalanceExceptionChecker
/*    */   extends StandardLoadBalanceExceptionChecker
/*    */ {
/*    */   public boolean shouldExceptionTriggerFailover(SQLException ex)
/*    */   {
/* 32 */     return (super.shouldExceptionTriggerFailover(ex)) || (checkNdbException(ex));
/*    */   }
/*    */   
/*    */   private boolean checkNdbException(SQLException ex)
/*    */   {
/* 37 */     return (ex.getMessage().startsWith("Lock wait timeout exceeded")) || ((ex.getMessage().startsWith("Got temporary error")) && (ex.getMessage().endsWith("from NDB")));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NdbLoadBalanceExceptionChecker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */