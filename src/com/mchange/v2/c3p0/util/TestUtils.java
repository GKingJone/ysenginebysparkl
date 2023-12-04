/*     */ package com.mchange.v2.c3p0.util;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Random;
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
/*     */ public final class TestUtils
/*     */ {
/*     */   private static final Method OBJECT_EQUALS;
/*     */   private static final Method IDENTITY_HASHCODE;
/*     */   private static final Method IPCFP;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  44 */       OBJECT_EQUALS = class$java$lang$Object.getMethod("equals", new Class[] { Object.class });
/*  45 */       IDENTITY_HASHCODE = class$java$lang$System.getMethod("identityHashCode", new Class[] { Object.class });
/*     */       
/*     */ 
/*  48 */       IPCFP = class$com$mchange$v2$c3p0$util$TestUtils.getMethod("isPhysicalConnectionForProxy", new Class[] { Connection.class, C3P0ProxyConnection.class });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  52 */       e.printStackTrace();
/*  53 */       throw new RuntimeException("Huh? Can't reflectively get ahold of expected methods?");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean samePhysicalConnection(C3P0ProxyConnection con1, C3P0ProxyConnection con2)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  66 */       Object out = con1.rawConnectionOperation(IPCFP, null, new Object[] { C3P0ProxyConnection.RAW_CONNECTION, con2 });
/*  67 */       return ((Boolean)out).booleanValue();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  71 */       e.printStackTrace();
/*  72 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isPhysicalConnectionForProxy(Connection physicalConnection, C3P0ProxyConnection proxy) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  80 */       Object out = proxy.rawConnectionOperation(OBJECT_EQUALS, physicalConnection, new Object[] { C3P0ProxyConnection.RAW_CONNECTION });
/*  81 */       return ((Boolean)out).booleanValue();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  85 */       e.printStackTrace();
/*  86 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static int physicalConnectionIdentityHashCode(C3P0ProxyConnection conn) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  94 */       Object out = conn.rawConnectionOperation(IDENTITY_HASHCODE, null, new Object[] { C3P0ProxyConnection.RAW_CONNECTION });
/*  95 */       return ((Integer)out).intValue();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  99 */       e.printStackTrace();
/* 100 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static DataSource unreliableCommitDataSource(DataSource ds)
/*     */     throws Exception
/*     */   {
/* 107 */     return (DataSource)Proxy.newProxyInstance(class$com$mchange$v2$c3p0$util$TestUtils.getClassLoader(), new Class[] { DataSource.class }, new StupidDataSourceInvocationHandler(ds));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static class StupidDataSourceInvocationHandler
/*     */     implements InvocationHandler
/*     */   {
/*     */     DataSource ds;
/*     */     
/*     */ 
/* 118 */     Random r = new Random();
/*     */     
/*     */     StupidDataSourceInvocationHandler(DataSource ds) {
/* 121 */       this.ds = ds;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/* 126 */       if ("getConnection".equals(method.getName()))
/*     */       {
/* 128 */         Connection conn = (Connection)method.invoke(this.ds, args);
/* 129 */         return Proxy.newProxyInstance(TestUtils.class$com$mchange$v2$c3p0$util$TestUtils.getClassLoader(), new Class[] { Connection.class }, new StupidConnectionInvocationHandler(conn));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 134 */       return method.invoke(this.ds, args);
/*     */     }
/*     */   }
/*     */   
/*     */   static class StupidConnectionInvocationHandler implements InvocationHandler
/*     */   {
/*     */     Connection conn;
/* 141 */     Random r = new Random();
/*     */     
/* 143 */     boolean invalid = false;
/*     */     
/*     */     StupidConnectionInvocationHandler(Connection conn) {
/* 146 */       this.conn = conn;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */     {
/* 151 */       if ("close".equals(method.getName()))
/*     */       {
/* 153 */         if (this.invalid)
/*     */         {
/* 155 */           new Exception("Duplicate close() called on Connection!!!").printStackTrace();
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 160 */           this.invalid = true;
/*     */         }
/* 162 */         return null;
/*     */       }
/* 164 */       if (this.invalid)
/* 165 */         throw new SQLException("Connection closed -- cannot " + method.getName());
/* 166 */       if (("commit".equals(method.getName())) && (this.r.nextInt(100) == 0))
/*     */       {
/* 168 */         this.conn.rollback();
/* 169 */         throw new SQLException("Random commit exception!!!");
/*     */       }
/* 171 */       if (this.r.nextInt(200) == 0)
/*     */       {
/* 173 */         this.conn.rollback();
/* 174 */         this.conn.close();
/* 175 */         throw new SQLException("Random Fatal Exception Occurred!!!");
/*     */       }
/*     */       
/* 178 */       return method.invoke(this.conn, args);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\util\TestUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */