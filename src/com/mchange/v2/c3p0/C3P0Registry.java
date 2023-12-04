/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0Defaults;
/*     */ import com.mchange.v2.c3p0.impl.IdentityTokenized;
/*     */ import com.mchange.v2.c3p0.impl.IdentityTokenizedCoalesceChecker;
/*     */ import com.mchange.v2.c3p0.management.ManagementCoordinator;
/*     */ import com.mchange.v2.c3p0.management.NullManagementCoordinator;
/*     */ import com.mchange.v2.coalesce.CoalesceChecker;
/*     */ import com.mchange.v2.coalesce.Coalescer;
/*     */ import com.mchange.v2.coalesce.CoalescerFactory;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import com.mchange.v2.util.DoubleWeakHashMap;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class C3P0Registry
/*     */ {
/*     */   private static final String MC_PARAM = "com.mchange.v2.c3p0.management.ManagementCoordinator";
/*  78 */   static final MLogger logger = MLog.getLogger(C3P0Registry.class);
/*     */   
/*     */ 
/*  81 */   static boolean banner_printed = false;
/*     */   
/*     */ 
/*  84 */   static boolean registry_mbean_registered = false;
/*     */   
/*     */ 
/*  87 */   private static CoalesceChecker CC = IdentityTokenizedCoalesceChecker.INSTANCE;
/*     */   
/*     */ 
/*     */ 
/*  91 */   private static Coalescer idtCoalescer = CoalescerFactory.createCoalescer(CC, true, false);
/*     */   
/*     */ 
/*  94 */   private static Map tokensToTokenized = new DoubleWeakHashMap();
/*     */   
/*     */ 
/*  97 */   private static HashSet unclosedPooledDataSources = new HashSet();
/*     */   
/*     */ 
/* 100 */   private static Map classNamesToConnectionTesters = Collections.synchronizedMap(new HashMap());
/*     */   
/*     */ 
/* 103 */   private static Map classNamesToConnectionCustomizers = Collections.synchronizedMap(new HashMap());
/*     */   
/*     */   private static ManagementCoordinator mc;
/*     */   
/*     */   static
/*     */   {
/* 109 */     classNamesToConnectionTesters.put(C3P0Defaults.connectionTesterClassName(), C3P0Defaults.connectionTester());
/*     */     
/* 111 */     String userManagementCoordinator = C3P0ConfigUtils.getPropFileConfigProperty("com.mchange.v2.c3p0.management.ManagementCoordinator");
/* 112 */     if (userManagementCoordinator != null)
/*     */     {
/*     */       try
/*     */       {
/* 116 */         mc = (ManagementCoordinator)Class.forName(userManagementCoordinator).newInstance();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 120 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 121 */           logger.log(MLevel.WARNING, "Could not instantiate user-specified ManagementCoordinator " + userManagementCoordinator + ". Using NullManagementCoordinator (c3p0 JMX management disabled!)", e);
/*     */         }
/*     */         
/*     */ 
/* 125 */         mc = new NullManagementCoordinator();
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 132 */         Class.forName("java.lang.management.ManagementFactory");
/*     */         
/* 134 */         mc = (ManagementCoordinator)Class.forName("com.mchange.v2.c3p0.management.ActiveManagementCoordinator").newInstance();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 138 */         if (logger.isLoggable(MLevel.INFO)) {
/* 139 */           logger.log(MLevel.INFO, "jdk1.5 management interfaces unavailable... JMX support disabled.", e);
/*     */         }
/*     */         
/* 142 */         mc = new NullManagementCoordinator();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static ConnectionTester getConnectionTester(String className)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       ConnectionTester out = (ConnectionTester)classNamesToConnectionTesters.get(className);
/* 152 */       if (out == null)
/*     */       {
/* 154 */         out = (ConnectionTester)Class.forName(className).newInstance();
/* 155 */         classNamesToConnectionTesters.put(className, out);
/*     */       }
/* 157 */       return out;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 161 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 162 */         logger.log(MLevel.WARNING, "Could not create for find ConnectionTester with class name '" + className + "'. Using default.", e);
/*     */       }
/*     */     }
/*     */     
/* 166 */     return C3P0Defaults.connectionTester();
/*     */   }
/*     */   
/*     */   public static ConnectionCustomizer getConnectionCustomizer(String className)
/*     */     throws SQLException
/*     */   {
/* 172 */     if (className == null) {
/* 173 */       return null;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 178 */       ConnectionCustomizer out = (ConnectionCustomizer)classNamesToConnectionCustomizers.get(className);
/* 179 */       if (out == null)
/*     */       {
/* 181 */         out = (ConnectionCustomizer)Class.forName(className).newInstance();
/* 182 */         classNamesToConnectionCustomizers.put(className, out);
/*     */       }
/* 184 */       return out;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 188 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 189 */         logger.log(MLevel.WARNING, "Could not create for find ConnectionCustomizer with class name '" + className + "'.", e);
/*     */       }
/*     */       
/*     */ 
/* 193 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void banner()
/*     */   {
/* 201 */     if (!banner_printed)
/*     */     {
/* 203 */       if (logger.isLoggable(MLevel.INFO)) {
/* 204 */         logger.info("Initializing c3p0-0.9.1.2 [built 21-May-2007 15:04:56; debug? true; trace: 10]");
/*     */       }
/*     */       
/*     */ 
/* 208 */       banner_printed = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void attemptRegisterRegistryMBean()
/*     */   {
/* 215 */     if (!registry_mbean_registered)
/*     */     {
/* 217 */       mc.attemptManageC3P0Registry();
/* 218 */       registry_mbean_registered = true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isIncorporated(IdentityTokenized idt)
/*     */   {
/* 224 */     return tokensToTokenized.keySet().contains(idt.getIdentityToken());
/*     */   }
/*     */   
/*     */   private static void incorporate(IdentityTokenized idt)
/*     */   {
/* 229 */     tokensToTokenized.put(idt.getIdentityToken(), idt);
/* 230 */     if ((idt instanceof PooledDataSource))
/*     */     {
/* 232 */       unclosedPooledDataSources.add(idt);
/* 233 */       mc.attemptManagePooledDataSource((PooledDataSource)idt);
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized IdentityTokenized reregister(IdentityTokenized idt)
/*     */   {
/* 239 */     if ((idt instanceof PooledDataSource))
/*     */     {
/* 241 */       banner();
/* 242 */       attemptRegisterRegistryMBean();
/*     */     }
/*     */     
/* 245 */     if (idt.getIdentityToken() == null) {
/* 246 */       throw new RuntimeException("[c3p0 issue] The identityToken of a registered object should be set prior to registration.");
/*     */     }
/* 248 */     IdentityTokenized coalesceCheck = (IdentityTokenized)idtCoalescer.coalesce(idt);
/*     */     
/* 250 */     if (!isIncorporated(coalesceCheck)) {
/* 251 */       incorporate(coalesceCheck);
/*     */     }
/* 253 */     return coalesceCheck;
/*     */   }
/*     */   
/*     */   public static synchronized void markClosed(PooledDataSource pds)
/*     */   {
/* 258 */     unclosedPooledDataSources.remove(pds);
/* 259 */     mc.attemptUnmanagePooledDataSource(pds);
/* 260 */     if (unclosedPooledDataSources.isEmpty())
/*     */     {
/* 262 */       mc.attemptUnmanageC3P0Registry();
/* 263 */       registry_mbean_registered = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized Set getPooledDataSources() {
/* 268 */     return (Set)unclosedPooledDataSources.clone();
/*     */   }
/*     */   
/*     */   public static synchronized Set pooledDataSourcesByName(String dataSourceName) {
/* 272 */     Set out = new HashSet();
/* 273 */     for (Iterator ii = unclosedPooledDataSources.iterator(); ii.hasNext();)
/*     */     {
/* 275 */       PooledDataSource pds = (PooledDataSource)ii.next();
/* 276 */       if (pds.getDataSourceName().equals(dataSourceName))
/* 277 */         out.add(pds);
/*     */     }
/* 279 */     return out;
/*     */   }
/*     */   
/*     */   public static synchronized PooledDataSource pooledDataSourceByName(String dataSourceName)
/*     */   {
/* 284 */     for (Iterator ii = unclosedPooledDataSources.iterator(); ii.hasNext();)
/*     */     {
/* 286 */       PooledDataSource pds = (PooledDataSource)ii.next();
/* 287 */       if (pds.getDataSourceName().equals(dataSourceName))
/* 288 */         return pds;
/*     */     }
/* 290 */     return null;
/*     */   }
/*     */   
/*     */   public static synchronized Set allIdentityTokens()
/*     */   {
/* 295 */     Set out = Collections.unmodifiableSet(tokensToTokenized.keySet());
/*     */     
/* 297 */     return out;
/*     */   }
/*     */   
/*     */   public static synchronized Set allIdentityTokenized()
/*     */   {
/* 302 */     HashSet out = new HashSet();
/* 303 */     out.addAll(tokensToTokenized.values());
/*     */     
/* 305 */     return Collections.unmodifiableSet(out);
/*     */   }
/*     */   
/*     */   public static synchronized Set allPooledDataSources()
/*     */   {
/* 310 */     Set out = Collections.unmodifiableSet(unclosedPooledDataSources);
/*     */     
/* 312 */     return out;
/*     */   }
/*     */   
/*     */   public static synchronized int getNumPooledDataSources() {
/* 316 */     return unclosedPooledDataSources.size();
/*     */   }
/*     */   
/*     */   public static synchronized int getNumPoolsAllDataSources() throws SQLException {
/* 320 */     int count = 0;
/* 321 */     for (Iterator ii = unclosedPooledDataSources.iterator(); ii.hasNext();)
/*     */     {
/* 323 */       PooledDataSource pds = (PooledDataSource)ii.next();
/* 324 */       count += pds.getNumUserPools();
/*     */     }
/* 326 */     return count;
/*     */   }
/*     */   
/*     */   public synchronized int getNumThreadsAllThreadPools() throws SQLException
/*     */   {
/* 331 */     int count = 0;
/* 332 */     for (Iterator ii = unclosedPooledDataSources.iterator(); ii.hasNext();)
/*     */     {
/* 334 */       PooledDataSource pds = (PooledDataSource)ii.next();
/* 335 */       count += pds.getNumHelperThreads();
/*     */     }
/* 337 */     return count;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\C3P0Registry.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */