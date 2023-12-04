/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
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
/*    */ public class LoggingProfilerEventHandler
/*    */   implements ProfilerEventHandler
/*    */ {
/*    */   private Log log;
/*    */   
/*    */   public void consumeEvent(ProfilerEvent evt)
/*    */   {
/* 42 */     if (evt.eventType == 0) {
/* 43 */       this.log.logWarn(evt);
/*    */     } else {
/* 45 */       this.log.logInfo(evt);
/*    */     }
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 50 */     this.log = null;
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 54 */     this.log = conn.getLog();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\profiler\LoggingProfilerEventHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */