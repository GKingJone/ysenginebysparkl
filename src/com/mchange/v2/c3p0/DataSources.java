/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.sql.ConnectionPoolDataSource;
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
/*     */ public final class DataSources
/*     */ {
/*  59 */   static final MLogger logger = MLog.getLogger(DataSources.class);
/*     */   
/*     */ 
/*     */   static final Set WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS;
/*     */   
/*     */ 
/*     */   static final Set POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS;
/*     */   
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  71 */     String[] props = { "checkoutTimeout", "acquireIncrement", "acquireRetryAttempts", "acquireRetryDelay", "autoCommitOnClose", "connectionTesterClassName", "forceIgnoreUnresolvedTransactions", "idleConnectionTestPeriod", "initialPoolSize", "maxIdleTime", "maxPoolSize", "maxStatements", "maxStatementsPerConnection", "minPoolSize", "propertyCycle", "breakAfterAcquireFailure", "testConnectionOnCheckout", "testConnectionOnCheckin", "usesTraditionalReflectiveProxies", "preferredTestQuery", "automaticTestTable", "factoryClassLocation" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */     WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS = Collections.unmodifiableSet(new HashSet(Arrays.asList(props)));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 104 */     props = new String[] { "numHelperThreads", "factoryClassLocation" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 110 */     POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS = Collections.unmodifiableSet(new HashSet(Arrays.asList(props)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DataSource unpooledDataSource()
/*     */     throws SQLException
/*     */   {
/* 119 */     DriverManagerDataSource out = new DriverManagerDataSource();
/* 120 */     return out;
/*     */   }
/*     */   
/*     */   public static DataSource unpooledDataSource(String jdbcUrl) throws SQLException
/*     */   {
/* 125 */     DriverManagerDataSource out = new DriverManagerDataSource();
/* 126 */     out.setJdbcUrl(jdbcUrl);
/* 127 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static DataSource unpooledDataSource(String jdbcUrl, String user, String password)
/*     */     throws SQLException
/*     */   {
/* 135 */     Properties props = new Properties();
/* 136 */     props.put("user", user);
/* 137 */     props.put("password", password);
/* 138 */     return unpooledDataSource(jdbcUrl, props);
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
/*     */   public static DataSource unpooledDataSource(String jdbcUrl, Properties driverProps)
/*     */     throws SQLException
/*     */   {
/* 152 */     DriverManagerDataSource out = new DriverManagerDataSource();
/* 153 */     out.setJdbcUrl(jdbcUrl);
/* 154 */     out.setProperties(driverProps);
/* 155 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource)
/*     */     throws SQLException
/*     */   {
/* 167 */     return pooledDataSource(unpooledDataSource, null, (Map)null);
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
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource, int statement_cache_size)
/*     */     throws SQLException
/*     */   {
/* 181 */     Map overrideProps = new HashMap();
/* 182 */     overrideProps.put("maxStatements", new Integer(statement_cache_size));
/* 183 */     return pooledDataSource(unpooledDataSource, null, overrideProps);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource, PoolConfig pcfg)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 199 */       WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource();
/* 200 */       wcpds.setNestedDataSource(unpooledDataSource);
/*     */       
/*     */ 
/* 203 */       BeansUtils.overwriteSpecificAccessibleProperties(pcfg, wcpds, WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS);
/*     */       
/* 205 */       PoolBackedDataSource nascent_pbds = new PoolBackedDataSource();
/* 206 */       nascent_pbds.setConnectionPoolDataSource(wcpds);
/* 207 */       BeansUtils.overwriteSpecificAccessibleProperties(pcfg, nascent_pbds, POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS);
/*     */       
/* 209 */       return nascent_pbds;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 220 */       SQLException sqle = SqlUtils.toSQLException("Exception configuring pool-backed DataSource: " + e, e);
/* 221 */       if ((logger.isLoggable(MLevel.FINE)) && (e != sqle))
/* 222 */         logger.log(MLevel.FINE, "Converted exception to throwable SQLException", e);
/* 223 */       throw sqle;
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
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource, String configName)
/*     */     throws SQLException
/*     */   {
/* 246 */     return pooledDataSource(unpooledDataSource, configName, null);
/*     */   }
/*     */   
/* 249 */   public static DataSource pooledDataSource(DataSource unpooledDataSource, Map overrideProps) throws SQLException { return pooledDataSource(unpooledDataSource, null, overrideProps); }
/*     */   
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource, String configName, Map overrideProps) throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 255 */       WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource(configName);
/* 256 */       wcpds.setNestedDataSource(unpooledDataSource);
/* 257 */       if (overrideProps != null) {
/* 258 */         BeansUtils.overwriteAccessiblePropertiesFromMap(overrideProps, wcpds, false, null, true, MLevel.WARNING, MLevel.WARNING, false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 267 */       PoolBackedDataSource nascent_pbds = new PoolBackedDataSource(configName);
/* 268 */       nascent_pbds.setConnectionPoolDataSource(wcpds);
/* 269 */       if (overrideProps != null) {
/* 270 */         BeansUtils.overwriteAccessiblePropertiesFromMap(overrideProps, nascent_pbds, false, null, true, MLevel.WARNING, MLevel.WARNING, false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */       return nascent_pbds;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 290 */       SQLException sqle = SqlUtils.toSQLException("Exception configuring pool-backed DataSource: " + e, e);
/* 291 */       if ((logger.isLoggable(MLevel.FINE)) && (e != sqle))
/* 292 */         logger.log(MLevel.FINE, "Converted exception to throwable SQLException", e);
/* 293 */       throw sqle;
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
/*     */   public static DataSource pooledDataSource(DataSource unpooledDataSource, Properties props)
/*     */     throws SQLException
/*     */   {
/* 308 */     Properties peeledProps = new Properties();
/* 309 */     for (Iterator ii = props.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 311 */       String propKey = (String)ii.next();
/* 312 */       String propVal = props.getProperty(propKey);
/* 313 */       String peeledKey = propKey.startsWith("c3p0.") ? propKey.substring(5) : propKey;
/* 314 */       peeledProps.put(peeledKey, propVal);
/*     */     }
/* 316 */     return pooledDataSource(unpooledDataSource, null, peeledProps);
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
/*     */   public static void destroy(DataSource pooledDataSource)
/*     */     throws SQLException
/*     */   {
/* 335 */     destroy(pooledDataSource, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static void forceDestroy(DataSource pooledDataSource)
/*     */     throws SQLException
/*     */   {
/* 348 */     destroy(pooledDataSource, true);
/*     */   }
/*     */   
/*     */   private static void destroy(DataSource pooledDataSource, boolean force) throws SQLException {
/* 352 */     if ((pooledDataSource instanceof PoolBackedDataSource))
/*     */     {
/* 354 */       ConnectionPoolDataSource cpds = ((PoolBackedDataSource)pooledDataSource).getConnectionPoolDataSource();
/* 355 */       if ((cpds instanceof WrapperConnectionPoolDataSource))
/* 356 */         destroy(((WrapperConnectionPoolDataSource)cpds).getNestedDataSource(), force);
/*     */     }
/* 358 */     if ((pooledDataSource instanceof PooledDataSource)) {
/* 359 */       ((PooledDataSource)pooledDataSource).close(force);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\DataSources.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */