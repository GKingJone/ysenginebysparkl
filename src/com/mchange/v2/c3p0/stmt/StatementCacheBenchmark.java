/*     */ package com.mchange.v2.c3p0.stmt;
/*     */ 
/*     */ import com.mchange.v1.db.sql.ConnectionUtils;
/*     */ import com.mchange.v1.db.sql.StatementUtils;
/*     */ import com.mchange.v2.c3p0.DriverManagerDataSourceFactory;
/*     */ import com.mchange.v2.c3p0.PoolBackedDataSourceFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import javax.sql.DataSource;
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
/*     */ public final class StatementCacheBenchmark
/*     */ {
/*     */   static final String EMPTY_TABLE_CREATE = "CREATE TABLE emptyyukyuk (a varchar(8), b varchar(8))";
/*     */   static final String EMPTY_TABLE_SELECT = "SELECT * FROM emptyyukyuk";
/*     */   static final String EMPTY_TABLE_DROP = "DROP TABLE emptyyukyuk";
/*     */   static final String EMPTY_TABLE_CONDITIONAL_SELECT = "SELECT * FROM emptyyukyuk where a = ?";
/*     */   static final int NUM_ITERATIONS = 2000;
/*     */   
/*     */   public static void main(String[] argv)
/*     */   {
/*  44 */     DataSource ds_unpooled = null;
/*  45 */     DataSource ds_pooled = null;
/*     */     
/*     */     try
/*     */     {
/*  49 */       String jdbc_url = null;
/*  50 */       String username = null;
/*  51 */       String password = null;
/*  52 */       if (argv.length == 3)
/*     */       {
/*  54 */         jdbc_url = argv[0];
/*  55 */         username = argv[1];
/*  56 */         password = argv[2];
/*     */       }
/*  58 */       else if (argv.length == 1)
/*     */       {
/*  60 */         jdbc_url = argv[0];
/*  61 */         username = null;
/*  62 */         password = null;
/*     */       }
/*     */       else {
/*  65 */         usage();
/*     */       }
/*  67 */       if (!jdbc_url.startsWith("jdbc:")) {
/*  68 */         usage();
/*     */       }
/*  70 */       ds_unpooled = DriverManagerDataSourceFactory.create(jdbc_url, username, password);
/*  71 */       ds_pooled = PoolBackedDataSourceFactory.create(jdbc_url, username, password, 5, 20, 5, 0, 100);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  81 */       create(ds_pooled);
/*     */       
/*  83 */       perform(ds_pooled, "pooled");
/*  84 */       perform(ds_unpooled, "unpooled"); return;
/*     */     }
/*     */     catch (Exception e) {
/*  87 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/*  90 */         drop(ds_pooled);
/*     */       } catch (Exception e) {
/*  92 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static void perform(DataSource ds, String name) throws SQLException
/*     */   {
/*  99 */     Connection c = null;
/* 100 */     PreparedStatement ps = null;
/*     */     try
/*     */     {
/* 103 */       c = ds.getConnection();
/* 104 */       long start = System.currentTimeMillis();
/* 105 */       for (int i = 0; i < 2000; i++)
/*     */       {
/* 107 */         PreparedStatement test = c.prepareStatement("SELECT * FROM emptyyukyuk where a = ?");
/*     */         
/* 109 */         test.close();
/*     */       }
/* 111 */       long end = System.currentTimeMillis();
/* 112 */       System.err.println(name + " --> " + (float)(end - start) / 2000.0F + " [" + 2000 + " iterations]");
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 118 */       StatementUtils.attemptClose(ps);
/* 119 */       ConnectionUtils.attemptClose(c);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void usage()
/*     */   {
/* 125 */     System.err.println("java -Djdbc.drivers=<comma_sep_list_of_drivers> " + StatementCacheBenchmark.class.getName() + " <jdbc_url> [<username> <password>]");
/*     */     
/*     */ 
/*     */ 
/* 129 */     System.exit(-1);
/*     */   }
/*     */   
/*     */   static void create(DataSource ds)
/*     */     throws SQLException
/*     */   {
/* 135 */     System.err.println("Creating test schema.");
/* 136 */     Connection con = null;
/* 137 */     PreparedStatement ps1 = null;
/*     */     try
/*     */     {
/* 140 */       con = ds.getConnection();
/* 141 */       ps1 = con.prepareStatement("CREATE TABLE emptyyukyuk (a varchar(8), b varchar(8))");
/* 142 */       ps1.executeUpdate();
/* 143 */       System.err.println("Test schema created.");
/*     */     }
/*     */     finally
/*     */     {
/* 147 */       StatementUtils.attemptClose(ps1);
/* 148 */       ConnectionUtils.attemptClose(con);
/*     */     }
/*     */   }
/*     */   
/*     */   static void drop(DataSource ds)
/*     */     throws SQLException
/*     */   {
/* 155 */     Connection con = null;
/* 156 */     PreparedStatement ps1 = null;
/*     */     try
/*     */     {
/* 159 */       con = ds.getConnection();
/* 160 */       ps1 = con.prepareStatement("DROP TABLE emptyyukyuk");
/* 161 */       ps1.executeUpdate();
/*     */     }
/*     */     finally
/*     */     {
/* 165 */       StatementUtils.attemptClose(ps1);
/* 166 */       ConnectionUtils.attemptClose(con);
/*     */     }
/* 168 */     System.err.println("Test schema dropped.");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\StatementCacheBenchmark.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */