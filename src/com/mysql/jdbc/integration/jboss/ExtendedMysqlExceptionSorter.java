/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter;
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
/*    */ public final class ExtendedMysqlExceptionSorter
/*    */   extends MySQLExceptionSorter
/*    */ {
/*    */   static final long serialVersionUID = -2454582336945931069L;
/*    */   
/*    */   public boolean isExceptionFatal(SQLException ex)
/*    */   {
/* 44 */     String sqlState = ex.getSQLState();
/*    */     
/* 46 */     if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 47 */       return true;
/*    */     }
/*    */     
/* 50 */     return super.isExceptionFatal(ex);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\integration\jboss\ExtendedMysqlExceptionSorter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */