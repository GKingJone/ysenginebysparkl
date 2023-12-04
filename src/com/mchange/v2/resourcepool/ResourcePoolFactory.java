/*    */ package com.mchange.v2.resourcepool;
/*    */ 
/*    */ import com.mchange.v2.async.AsynchronousRunner;
/*    */ import com.mchange.v2.async.Queuable;
/*    */ import com.mchange.v2.async.RunnableQueue;
/*    */ import java.util.Timer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ResourcePoolFactory
/*    */ {
/* 57 */   static final ResourcePoolFactory SHARED_INSTANCE = new BasicResourcePoolFactory();
/*    */   static final int DEFAULT_NUM_TASK_THREADS = 3;
/*    */   
/*    */   public static ResourcePoolFactory getSharedInstance()
/*    */     throws ResourcePoolException
/*    */   {
/* 63 */     return SHARED_INSTANCE;
/*    */   }
/*    */   
/* 66 */   public static ResourcePoolFactory createInstance() { return new BasicResourcePoolFactory(); }
/*    */   
/*    */   public static ResourcePoolFactory createInstance(int num_task_threads) {
/* 69 */     return new BasicResourcePoolFactory(num_task_threads);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static ResourcePoolFactory createInstance(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer cullTimer)
/*    */   {
/* 78 */     return new BasicResourcePoolFactory(taskRunner, asyncEventQueue, cullTimer);
/*    */   }
/*    */   
/*    */   public static ResourcePoolFactory createInstance(Queuable taskRunnerEventQueue, Timer cullTimer)
/*    */   {
/* 83 */     return createInstance(taskRunnerEventQueue, taskRunnerEventQueue == null ? null : taskRunnerEventQueue.asRunnableQueue(), cullTimer);
/*    */   }
/*    */   
/*    */   public abstract void setMin(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getMin()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setMax(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getStart()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setStart(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getMax()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setIncrement(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getIncrement()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setAcquisitionRetryAttempts(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getAcquisitionRetryAttempts()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setAcquisitionRetryDelay(int paramInt)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract int getAcquisitionRetryDelay()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setIdleResourceTestPeriod(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getIdleResourceTestPeriod()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setResourceMaxAge(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getResourceMaxAge()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setResourceMaxIdleTime(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getResourceMaxIdleTime()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setExcessResourceMaxIdleTime(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getExcessResourceMaxIdleTime()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getDestroyOverdueResourceTime()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setDestroyOverdueResourceTime(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setExpirationEnforcementDelay(long paramLong)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract long getExpirationEnforcementDelay()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setBreakOnAcquisitionFailure(boolean paramBoolean)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract boolean getBreakOnAcquisitionFailure()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract void setDebugStoreCheckoutStackTrace(boolean paramBoolean)
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract boolean getDebugStoreCheckoutStackTrace()
/*    */     throws ResourcePoolException;
/*    */   
/*    */   public abstract ResourcePool createPool(ResourcePool.Manager paramManager)
/*    */     throws ResourcePoolException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */