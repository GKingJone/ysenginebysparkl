/*    */ package com.mchange.v2.c3p0.util;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import javax.sql.ConnectionEvent;
/*    */ import javax.sql.ConnectionEventListener;
/*    */ import javax.sql.PooledConnection;
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
/*    */ public class ConnectionEventSupport
/*    */ {
/*    */   PooledConnection source;
/* 33 */   HashSet mlisteners = new HashSet();
/*    */   
/*    */   public ConnectionEventSupport(PooledConnection source) {
/* 36 */     this.source = source;
/*    */   }
/*    */   
/* 39 */   public synchronized void addConnectionEventListener(ConnectionEventListener mlistener) { this.mlisteners.add(mlistener); }
/*    */   
/*    */   public synchronized void removeConnectionEventListener(ConnectionEventListener mlistener) {
/* 42 */     this.mlisteners.remove(mlistener);
/*    */   }
/*    */   
/*    */   public void fireConnectionClosed()
/*    */   {
/*    */     Set mlCopy;
/* 48 */     synchronized (this) {
/* 49 */       mlCopy = (Set)this.mlisteners.clone();
/*    */     }
/* 51 */     ConnectionEvent evt = new ConnectionEvent(this.source);
/* 52 */     for (Iterator i = mlCopy.iterator(); i.hasNext();)
/*    */     {
/* 54 */       ConnectionEventListener cl = (ConnectionEventListener)i.next();
/* 55 */       cl.connectionClosed(evt);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void fireConnectionErrorOccurred(SQLException error)
/*    */   {
/*    */     Set mlCopy;
/* 63 */     synchronized (this) {
/* 64 */       mlCopy = (Set)this.mlisteners.clone();
/*    */     }
/* 66 */     ConnectionEvent evt = new ConnectionEvent(this.source, error);
/* 67 */     for (Iterator i = mlCopy.iterator(); i.hasNext();)
/*    */     {
/* 69 */       ConnectionEventListener cl = (ConnectionEventListener)i.next();
/* 70 */       cl.connectionErrorOccurred(evt);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\util\ConnectionEventSupport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */