/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
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
/*    */ 
/*    */ 
/*    */ public class ProfilerEventHandlerFactory
/*    */ {
/* 33 */   private Connection ownerConnection = null;
/*    */   
/* 35 */   protected Log log = null;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static synchronized ProfilerEventHandler getInstance(MySQLConnection conn)
/*    */     throws SQLException
/*    */   {
/* 46 */     ProfilerEventHandler handler = conn.getProfilerEventHandlerInstance();
/*    */     
/* 48 */     if (handler == null) {
/* 49 */       handler = (ProfilerEventHandler)Util.getInstance(conn.getProfilerEventHandler(), new Class[0], new Object[0], conn.getExceptionInterceptor());
/*    */       
/*    */ 
/* 52 */       conn.initializeExtension(handler);
/* 53 */       conn.setProfilerEventHandlerInstance(handler);
/*    */     }
/*    */     
/* 56 */     return handler;
/*    */   }
/*    */   
/*    */   public static synchronized void removeInstance(MySQLConnection conn) {
/* 60 */     ProfilerEventHandler handler = conn.getProfilerEventHandlerInstance();
/*    */     
/* 62 */     if (handler != null) {
/* 63 */       handler.destroy();
/*    */     }
/*    */   }
/*    */   
/*    */   private ProfilerEventHandlerFactory(Connection conn) {
/* 68 */     this.ownerConnection = conn;
/*    */     try
/*    */     {
/* 71 */       this.log = this.ownerConnection.getLog();
/*    */     } catch (SQLException sqlEx) {
/* 73 */       throw new RuntimeException("Unable to get logger from connection");
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ProfilerEventHandlerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */