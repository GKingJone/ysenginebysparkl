package com.mchange.v2.resourcepool;

import com.mchange.v1.util.ClosableResource;

public abstract interface ResourcePool
  extends ClosableResource
{
  public static final int KNOWN_AND_AVAILABLE = 0;
  public static final int KNOWN_AND_CHECKED_OUT = 1;
  public static final int UNKNOWN_OR_PURGED = -1;
  
  public abstract Object checkoutResource()
    throws ResourcePoolException, InterruptedException;
  
  public abstract Object checkoutResource(long paramLong)
    throws TimeoutException, ResourcePoolException, InterruptedException;
  
  public abstract void checkinResource(Object paramObject)
    throws ResourcePoolException;
  
  public abstract void checkinAll()
    throws ResourcePoolException;
  
  public abstract int statusInPool(Object paramObject)
    throws ResourcePoolException;
  
  public abstract void markBroken(Object paramObject)
    throws ResourcePoolException;
  
  public abstract int getMinPoolSize()
    throws ResourcePoolException;
  
  public abstract int getMaxPoolSize()
    throws ResourcePoolException;
  
  public abstract int getPoolSize()
    throws ResourcePoolException;
  
  public abstract void setPoolSize(int paramInt)
    throws ResourcePoolException;
  
  public abstract int getAvailableCount()
    throws ResourcePoolException;
  
  public abstract int getExcludedCount()
    throws ResourcePoolException;
  
  public abstract int getAwaitingCheckinCount()
    throws ResourcePoolException;
  
  public abstract long getEffectiveExpirationEnforcementDelay()
    throws ResourcePoolException;
  
  public abstract long getStartTime()
    throws ResourcePoolException;
  
  public abstract long getUpTime()
    throws ResourcePoolException;
  
  public abstract long getNumFailedCheckins()
    throws ResourcePoolException;
  
  public abstract long getNumFailedCheckouts()
    throws ResourcePoolException;
  
  public abstract long getNumFailedIdleTests()
    throws ResourcePoolException;
  
  public abstract int getNumCheckoutWaiters()
    throws ResourcePoolException;
  
  public abstract Throwable getLastAcquisitionFailure()
    throws ResourcePoolException;
  
  public abstract Throwable getLastCheckinFailure()
    throws ResourcePoolException;
  
  public abstract Throwable getLastCheckoutFailure()
    throws ResourcePoolException;
  
  public abstract Throwable getLastIdleCheckFailure()
    throws ResourcePoolException;
  
  public abstract Throwable getLastResourceTestFailure()
    throws ResourcePoolException;
  
  public abstract void resetPool()
    throws ResourcePoolException;
  
  public abstract void close()
    throws ResourcePoolException;
  
  public abstract void close(boolean paramBoolean)
    throws ResourcePoolException;
  
  public static abstract interface Manager
  {
    public abstract Object acquireResource()
      throws Exception;
    
    public abstract void refurbishIdleResource(Object paramObject)
      throws Exception;
    
    public abstract void refurbishResourceOnCheckout(Object paramObject)
      throws Exception;
    
    public abstract void refurbishResourceOnCheckin(Object paramObject)
      throws Exception;
    
    public abstract void destroyResource(Object paramObject)
      throws Exception;
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePool.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */