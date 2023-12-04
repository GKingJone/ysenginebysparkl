/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.sql.StatementEvent;
/*    */ import javax.sql.StatementEventListener;
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
/*    */ public class JDBC4MysqlXAConnection
/*    */   extends MysqlXAConnection
/*    */ {
/* 39 */   private final Map<StatementEventListener, StatementEventListener> statementEventListeners = new HashMap();
/*    */   
/*    */   public JDBC4MysqlXAConnection(Connection connection, boolean logXaCommands) throws SQLException {
/* 42 */     super(connection, logXaCommands);
/*    */   }
/*    */   
/*    */   public synchronized void close() throws SQLException {
/* 46 */     super.close();
/*    */     
/* 48 */     this.statementEventListeners.clear();
/*    */   }
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
/*    */   public void addStatementEventListener(StatementEventListener listener)
/*    */   {
/* 63 */     synchronized (this.statementEventListeners) {
/* 64 */       this.statementEventListeners.put(listener, listener);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void removeStatementEventListener(StatementEventListener listener)
/*    */   {
/* 78 */     synchronized (this.statementEventListeners) {
/* 79 */       this.statementEventListeners.remove(listener);
/*    */     }
/*    */   }
/*    */   
/*    */   void fireStatementEvent(StatementEvent event) throws SQLException {
/* 84 */     synchronized (this.statementEventListeners) {
/* 85 */       for (StatementEventListener listener : this.statementEventListeners.keySet()) {
/* 86 */         listener.statementClosed(event);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4MysqlXAConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */