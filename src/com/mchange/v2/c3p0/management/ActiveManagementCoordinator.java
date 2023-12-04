/*     */ package com.mchange.v2.c3p0.management;
/*     */ 
/*     */ import com.mchange.v2.c3p0.PooledDataSource;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ActiveManagementCoordinator
/*     */   implements ManagementCoordinator
/*     */ {
/*     */   private static final String C3P0_REGISTRY_NAME = "com.mchange.v2.c3p0:type=C3P0Registry";
/*  36 */   static final MLogger logger = MLog.getLogger(ActiveManagementCoordinator.class);
/*     */   MBeanServer mbs;
/*     */   
/*     */   public ActiveManagementCoordinator()
/*     */     throws Exception
/*     */   {
/*  42 */     this.mbs = ManagementFactory.getPlatformMBeanServer();
/*     */   }
/*     */   
/*     */   public void attemptManageC3P0Registry()
/*     */   {
/*     */     try
/*     */     {
/*  49 */       ObjectName name = new ObjectName("com.mchange.v2.c3p0:type=C3P0Registry");
/*  50 */       C3P0RegistryManager mbean = new C3P0RegistryManager();
/*     */       
/*  52 */       if (this.mbs.isRegistered(name))
/*     */       {
/*  54 */         if (logger.isLoggable(MLevel.WARNING))
/*     */         {
/*  56 */           logger.warning("A C3P0Registry mbean is already registered. This probably means that an application using c3p0 was undeployed, but not all PooledDataSources were closed prior to undeployment. This may lead to resource leaks over time. Please take care to close all PooledDataSources.");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  62 */         this.mbs.unregisterMBean(name);
/*     */       }
/*  64 */       this.mbs.registerMBean(mbean, name);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  68 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  69 */         logger.log(MLevel.WARNING, "Failed to set up C3P0RegistryManager mBean. [c3p0 will still function normally, but management via JMX may not be possible.]", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void attemptUnmanageC3P0Registry()
/*     */   {
/*     */     try
/*     */     {
/*  80 */       ObjectName name = new ObjectName("com.mchange.v2.c3p0:type=C3P0Registry");
/*  81 */       if (this.mbs.isRegistered(name))
/*     */       {
/*  83 */         this.mbs.unregisterMBean(name);
/*  84 */         if (logger.isLoggable(MLevel.FINER)) {
/*  85 */           logger.log(MLevel.FINER, "C3P0Registry mbean unregistered.");
/*     */         }
/*  87 */       } else if (logger.isLoggable(MLevel.FINE)) {
/*  88 */         logger.fine("The C3P0Registry mbean was not found in the registry, so could not be unregistered.");
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  92 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  93 */         logger.log(MLevel.WARNING, "An Exception occurred while trying to unregister the C3P0RegistryManager mBean." + e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void attemptManagePooledDataSource(PooledDataSource pds)
/*     */   {
/* 101 */     String name = getPdsObjectNameStr(pds);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 110 */       mbean = new DynamicPooledDataSourceManagerMBean(pds, name, this.mbs);
/*     */     }
/*     */     catch (Exception e) {
/*     */       DynamicPooledDataSourceManagerMBean mbean;
/* 114 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 115 */         logger.log(MLevel.WARNING, "Failed to set up a PooledDataSourceManager mBean. [" + name + "] " + "[c3p0 will still functioning normally, but management via JMX may not be possible.]", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void attemptUnmanagePooledDataSource(PooledDataSource pds)
/*     */   {
/* 125 */     String nameStr = getPdsObjectNameStr(pds);
/*     */     try
/*     */     {
/* 128 */       ObjectName name = new ObjectName(nameStr);
/* 129 */       if (this.mbs.isRegistered(name))
/*     */       {
/* 131 */         this.mbs.unregisterMBean(name);
/* 132 */         if (logger.isLoggable(MLevel.FINER)) {
/* 133 */           logger.log(MLevel.FINER, "MBean: " + nameStr + " unregistered.");
/*     */         }
/*     */       }
/* 136 */       else if (logger.isLoggable(MLevel.FINE)) {
/* 137 */         logger.fine("The mbean " + nameStr + " was not found in the registry, so could not be unregistered.");
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 141 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 142 */         logger.log(MLevel.WARNING, "An Exception occurred while unregistering mBean. [" + nameStr + "] " + e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String getPdsObjectNameStr(PooledDataSource pds)
/*     */   {
/* 149 */     return "com.mchange.v2.c3p0:type=PooledDataSource[" + pds.getIdentityToken() + "]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\ActiveManagementCoordinator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */