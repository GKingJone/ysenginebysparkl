package com.mchange.v2.async;

import com.mchange.v1.util.ClosableResource;

public abstract interface AsynchronousRunner
  extends ClosableResource
{
  public abstract void postRunnable(Runnable paramRunnable);
  
  public abstract void close(boolean paramBoolean);
  
  public abstract void close();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\async\AsynchronousRunner.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */