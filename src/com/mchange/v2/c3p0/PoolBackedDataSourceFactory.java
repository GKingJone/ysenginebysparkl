/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.sql.SqlUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public final class PoolBackedDataSourceFactory
/*     */ {
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createReferenceable(DataSource unpooledDataSource, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  75 */       WrapperConnectionPoolDataSource cpds = new WrapperConnectionPoolDataSource();
/*  76 */       cpds.setNestedDataSource(unpooledDataSource);
/*  77 */       cpds.setMinPoolSize(minPoolSize);
/*  78 */       cpds.setMaxPoolSize(maxPoolSize);
/*  79 */       cpds.setAcquireIncrement(acquireIncrement);
/*  80 */       cpds.setMaxIdleTime(maxIdleTime);
/*  81 */       cpds.setMaxStatements(maxStatements);
/*  82 */       cpds.setFactoryClassLocation(factoryLocation);
/*     */       
/*     */ 
/*  85 */       PoolBackedDataSource out = new PoolBackedDataSource();
/*  86 */       out.setConnectionPoolDataSource(cpds);
/*  87 */       return out;
/*     */     }
/*     */     catch (Exception e) {
/*  90 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createReferenceable(DataSource unpooledDataSource, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 114 */       WrapperConnectionPoolDataSource cpds = new WrapperConnectionPoolDataSource();
/* 115 */       cpds.setNestedDataSource(unpooledDataSource);
/* 116 */       cpds.setFactoryClassLocation(factoryLocation);
/*     */       
/* 118 */       PoolBackedDataSource out = new PoolBackedDataSource();
/* 119 */       out.setConnectionPoolDataSource(cpds);
/* 120 */       return out;
/*     */     }
/*     */     catch (Exception e) {
/* 123 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
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
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createReferenceable(String jdbcDriverClass, String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/* 163 */     DataSource nested = DriverManagerDataSourceFactory.create(jdbcDriverClass, jdbcUrl, user, password);
/*     */     
/*     */ 
/*     */ 
/* 167 */     return createReferenceable(nested, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, factoryLocation);
/*     */   }
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createReferenceable(String jdbcDriverClass, String jdbcUrl, String user, String password, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/* 198 */     DataSource nested = DriverManagerDataSourceFactory.create(jdbcDriverClass, jdbcUrl, user, password);
/*     */     
/*     */ 
/*     */ 
/* 202 */     return createReferenceable(nested, factoryLocation);
/*     */   }
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createSerializable(DataSource unpooledDataSource, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 239 */       WrapperConnectionPoolDataSource cpds = new WrapperConnectionPoolDataSource();
/* 240 */       cpds.setNestedDataSource(unpooledDataSource);
/* 241 */       cpds.setMinPoolSize(minPoolSize);
/* 242 */       cpds.setMaxPoolSize(maxPoolSize);
/* 243 */       cpds.setAcquireIncrement(acquireIncrement);
/* 244 */       cpds.setMaxIdleTime(maxIdleTime);
/* 245 */       cpds.setMaxStatements(maxStatements);
/*     */       
/* 247 */       PoolBackedDataSource out = new PoolBackedDataSource();
/* 248 */       out.setConnectionPoolDataSource(cpds);
/* 249 */       return out;
/*     */     }
/*     */     catch (Exception e) {
/* 252 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createSerializable(DataSource unpooledDataSource)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 271 */       WrapperConnectionPoolDataSource cpds = new WrapperConnectionPoolDataSource();
/* 272 */       cpds.setNestedDataSource(unpooledDataSource);
/*     */       
/* 274 */       PoolBackedDataSource out = new PoolBackedDataSource();
/* 275 */       out.setConnectionPoolDataSource(cpds);
/* 276 */       return out;
/*     */     }
/*     */     catch (Exception e) {
/* 279 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createSerializable(String jdbcDriverClass, String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements)
/*     */     throws SQLException
/*     */   {
/* 317 */     DataSource nested = DriverManagerDataSourceFactory.create(jdbcDriverClass, jdbcUrl, user, password);
/*     */     
/*     */ 
/*     */ 
/* 321 */     return createSerializable(nested, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements);
/*     */   }
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
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource createSerializable(String jdbcDriverClass, String jdbcUrl, String user, String password)
/*     */     throws SQLException
/*     */   {
/* 347 */     DataSource nested = DriverManagerDataSourceFactory.create(jdbcDriverClass, jdbcUrl, user, password);
/*     */     
/*     */ 
/*     */ 
/* 351 */     return createSerializable(nested);
/*     */   }
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
/*     */   public static DataSource create(DataSource unpooledDataSource, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/* 385 */     return createReferenceable(unpooledDataSource, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, factoryLocation);
/*     */   }
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
/*     */ 
/*     */   public static DataSource create(DataSource unpooledDataSource, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements)
/*     */     throws SQLException
/*     */   {
/* 420 */     return createReferenceable(unpooledDataSource, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, null);
/*     */   }
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
/*     */   public static DataSource create(DataSource unpooledDataSource)
/*     */     throws SQLException
/*     */   {
/* 437 */     return createSerializable(unpooledDataSource);
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DataSource create(String jdbcDriverClass, String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/* 475 */     return createReferenceable(jdbcDriverClass, jdbcUrl, user, password, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, factoryLocation);
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DataSource create(String jdbcDriverClass, String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements)
/*     */     throws SQLException
/*     */   {
/* 518 */     return createReferenceable(jdbcDriverClass, jdbcUrl, user, password, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, null);
/*     */   }
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
/*     */   public static DataSource create(String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements, String factoryLocation)
/*     */     throws SQLException
/*     */   {
/* 566 */     return create(null, jdbcUrl, user, password, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, factoryLocation);
/*     */   }
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
/*     */   public static DataSource create(String jdbcUrl, String user, String password, int minPoolSize, int maxPoolSize, int acquireIncrement, int maxIdleTime, int maxStatements)
/*     */     throws SQLException
/*     */   {
/* 610 */     return create(null, jdbcUrl, user, password, minPoolSize, maxPoolSize, acquireIncrement, maxIdleTime, maxStatements, null);
/*     */   }
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
/*     */   public static DataSource create(String jdbcDriverClass, String jdbcUrl, String user, String password)
/*     */     throws SQLException
/*     */   {
/* 636 */     return createSerializable(jdbcDriverClass, jdbcUrl, user, password);
/*     */   }
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
/*     */   public static DataSource create(String jdbcUrl, String user, String password)
/*     */     throws SQLException
/*     */   {
/* 658 */     return create(null, jdbcUrl, user, password);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\PoolBackedDataSourceFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */