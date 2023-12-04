package com.mchange.v2.resourcepool;

import java.util.EventListener;

public abstract interface ResourcePoolListener
  extends EventListener
{
  public abstract void resourceAcquired(ResourcePoolEvent paramResourcePoolEvent);
  
  public abstract void resourceCheckedIn(ResourcePoolEvent paramResourcePoolEvent);
  
  public abstract void resourceCheckedOut(ResourcePoolEvent paramResourcePoolEvent);
  
  public abstract void resourceRemoved(ResourcePoolEvent paramResourcePoolEvent);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolListener.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */