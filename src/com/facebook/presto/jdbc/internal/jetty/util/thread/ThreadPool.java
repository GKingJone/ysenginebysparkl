package com.facebook.presto.jdbc.internal.jetty.util.thread;

import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
import java.util.concurrent.Executor;

@ManagedObject("Pool of Threads")
public abstract interface ThreadPool
  extends Executor
{
  public abstract void join()
    throws InterruptedException;
  
  @ManagedAttribute("number of threads in pool")
  public abstract int getThreads();
  
  @ManagedAttribute("number of idle threads in pool")
  public abstract int getIdleThreads();
  
  @ManagedAttribute("indicates the pool is low on available threads")
  public abstract boolean isLowOnThreads();
  
  public static abstract interface SizedThreadPool
    extends ThreadPool
  {
    public abstract int getMinThreads();
    
    public abstract int getMaxThreads();
    
    public abstract void setMinThreads(int paramInt);
    
    public abstract void setMaxThreads(int paramInt);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ThreadPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */