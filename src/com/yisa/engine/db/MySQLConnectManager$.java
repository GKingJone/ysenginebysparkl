/*    */ package com.yisa.engine.db;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ 
/*    */ public final class MySQLConnectManager$
/*    */   implements scala.Serializable
/*    */ {
/*    */   public static final  MODULE$;
/*    */   
/* 10 */   private Object readResolve() { return MODULE$; } private MySQLConnectManager$() { MODULE$ = this; }
/*    */   
/*    */   public Connection getConnet(String zkHostport)
/*    */   {
/* 14 */     Connection conn = C3p0ConnectionPool..MODULE$.getConnet(zkHostport);
/* 15 */     return conn;
/*    */   }
/*    */   
/*    */   public void initConnectionPool(String zkHostport) {
/* 19 */     C3p0ConnectionPool..MODULE$.initConnectionPool(zkHostport);
/*    */   }
/*    */   
/*    */   static
/*    */   {
/*    */     new ();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\db\MySQLConnectManager$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */