/*    */ package com.yisa.engine.db;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ 
/*    */ public final class MySQLConnectionPool$ {
/*    */   public static final  MODULE$;
/*    */   private final int max;
/*    */   private final int connectionNum;
/*    */   private int com$yisa$engine$db$MySQLConnectionPool$$conNum;
/*    */   private final java.util.LinkedList<Connection> com$yisa$engine$db$MySQLConnectionPool$$pool;
/*    */   
/*    */   static { new ();
/*    */   }
/*    */   
/* 15 */   private int max() { return this.max; }
/* 16 */   private int connectionNum() { return this.connectionNum; }
/* 17 */   public int com$yisa$engine$db$MySQLConnectionPool$$conNum() { return this.com$yisa$engine$db$MySQLConnectionPool$$conNum; } public void com$yisa$engine$db$MySQLConnectionPool$$conNum_$eq(int x$1) { this.com$yisa$engine$db$MySQLConnectionPool$$conNum = x$1; }
/* 18 */   public java.util.LinkedList<Connection> com$yisa$engine$db$MySQLConnectionPool$$pool() { return this.com$yisa$engine$db$MySQLConnectionPool$$pool; }
/*    */   
/*    */ 
/*    */   public Connection getConnet(String zkHostport)
/*    */   {
/* 23 */     synchronized (scala.package..MODULE$.AnyRef())
/*    */     {
/* 25 */       if (com$yisa$engine$db$MySQLConnectionPool$$pool().isEmpty())
/*    */       {
/* 27 */         preGetConn();scala.runtime.RichInt..MODULE$
/* 28 */           .to$extension0(scala.Predef..MODULE$.intWrapper(1), connectionNum()).foreach$mVc$sp(new scala.runtime.AbstractFunction1.mcVI.sp() { public final void apply(int i) { apply$mcVI$sp(i); }
/* 29 */             public void apply$mcVI$sp(int i) { com.yisa.wifi.zookeeper.ZookeeperUtil zkUtil = new com.yisa.wifi.zookeeper.ZookeeperUtil();
/* 30 */               java.util.Map configs = zkUtil.getAllConfig(MySQLConnectionPool..this, "spark_engine", false);
/* 31 */               String jdbc = (String)configs.get("spark_engine_jdbc");
/* 32 */               String URL = jdbc;
/* 33 */               String USERNAME = (String)configs.get("spark_engine_jdbc_username");
/* 34 */               String PASSWORD = (String)configs.get("spark_engine_jdbc_password");
/* 35 */               Connection conn = java.sql.DriverManager.getConnection(URL, USERNAME, PASSWORD);
/*    */               
/* 37 */               MySQLConnectionPool..MODULE$.com$yisa$engine$db$MySQLConnectionPool$$pool().push(conn);
/* 38 */               MySQLConnectionPool..MODULE$.com$yisa$engine$db$MySQLConnectionPool$$conNum_$eq(MySQLConnectionPool..MODULE$.com$yisa$engine$db$MySQLConnectionPool$$conNum() + 1);
/*    */             }
/*    */           });
/*    */       }
/* 42 */       Connection connection = (Connection)com$yisa$engine$db$MySQLConnectionPool$$pool().poll();
/* 43 */       if (connection.isClosed()) {
/* 44 */         com$yisa$engine$db$MySQLConnectionPool$$conNum_$eq(com$yisa$engine$db$MySQLConnectionPool$$conNum() - 1);
/* 45 */         return getConnet(zkHostport);
/*    */       }
/* 47 */       return connection;
/*    */     }
/*    */   }
/*    */   
/*    */   public static final long serialVersionUID = 0L;
/*    */   public void releaseConn(Connection conn)
/*    */   {
/* 54 */     com$yisa$engine$db$MySQLConnectionPool$$pool().push(conn);
/*    */   }
/*    */   
/*    */ 
/*    */   private void preGetConn()
/*    */   {
/* 60 */     while ((com$yisa$engine$db$MySQLConnectionPool$$conNum() > max()) && (com$yisa$engine$db$MySQLConnectionPool$$pool().isEmpty())) {
/* 61 */       scala.Predef..MODULE$.println("Jdbc Pool has no connection now, please wait a moments!");
/* 62 */       Thread.sleep(2000L);
/*    */     }
/*    */     
/* 65 */     Class.forName("com.mysql.jdbc.Driver");
/*    */   }
/*    */   
/* 68 */   private MySQLConnectionPool$() { MODULE$ = this;this.max = 3;this.connectionNum = 5;this.com$yisa$engine$db$MySQLConnectionPool$$conNum = 0;this.com$yisa$engine$db$MySQLConnectionPool$$pool = new java.util.LinkedList();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\db\MySQLConnectionPool$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */