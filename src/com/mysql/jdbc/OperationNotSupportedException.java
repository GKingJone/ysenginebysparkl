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
/*    */ class OperationNotSupportedException
/*    */   extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = 474918612056813430L;
/*    */   
/*    */   OperationNotSupportedException()
/*    */   {
/* 33 */     super(Messages.getString("RowDataDynamic.10"), "S1009");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\OperationNotSupportedException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */