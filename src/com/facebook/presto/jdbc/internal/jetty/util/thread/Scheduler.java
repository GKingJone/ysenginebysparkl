package com.facebook.presto.jdbc.internal.jetty.util.thread;

import com.facebook.presto.jdbc.internal.jetty.util.component.LifeCycle;
import java.util.concurrent.TimeUnit;

public abstract interface Scheduler
  extends LifeCycle
{
  public abstract Task schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
  
  public static abstract interface Task
  {
    public abstract boolean cancel();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\Scheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */