/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DataTruncation;
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
/*    */ public class MysqlDataTruncation
/*    */   extends DataTruncation
/*    */ {
/*    */   static final long serialVersionUID = 3263928195256986226L;
/*    */   private String message;
/*    */   private int vendorErrorCode;
/*    */   
/*    */   public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize, int vendorErrorCode)
/*    */   {
/* 56 */     super(index, parameter, read, dataSize, transferSize);
/*    */     
/* 58 */     this.message = message;
/* 59 */     this.vendorErrorCode = vendorErrorCode;
/*    */   }
/*    */   
/*    */   public int getErrorCode()
/*    */   {
/* 64 */     return this.vendorErrorCode;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 74 */     return super.getMessage() + ": " + this.message;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MysqlDataTruncation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */