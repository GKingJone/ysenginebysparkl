/*     */ package com.mysql.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  43 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   private transient Method pingMethod;
/*     */   
/*     */   public MysqlConnectionTester()
/*     */   {
/*     */     try {
/*  49 */       this.pingMethod = com.mysql.jdbc.Connection.class.getMethod("ping", (Class[])null);
/*     */     }
/*     */     catch (Exception ex) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection con)
/*     */   {
/*     */     try
/*     */     {
/*  62 */       if (this.pingMethod != null) {
/*  63 */         if ((con instanceof com.mysql.jdbc.Connection))
/*     */         {
/*  65 */           ((com.mysql.jdbc.Connection)con).ping();
/*     */         }
/*     */         else {
/*  68 */           C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*  69 */           castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */         }
/*     */       } else {
/*  72 */         Statement pingStatement = null;
/*     */         try
/*     */         {
/*  75 */           pingStatement = con.createStatement();
/*  76 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  78 */           if (pingStatement != null) {
/*  79 */             pingStatement.close();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  84 */       return 0;
/*     */     } catch (Exception ex) {}
/*  86 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int statusOnException(java.sql.Connection arg0, Throwable throwable)
/*     */   {
/*  96 */     if (((throwable instanceof CommunicationsException)) || ("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName()))) {
/*  97 */       return -1;
/*     */     }
/*     */     
/* 100 */     if ((throwable instanceof SQLException)) {
/* 101 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */       
/* 103 */       if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 104 */         return -1;
/*     */       }
/*     */       
/* 107 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 112 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection arg0, String arg1)
/*     */   {
/* 121 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\integration\c3p0\MysqlConnectionTester.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */