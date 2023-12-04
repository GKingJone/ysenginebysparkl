package com.facebook.presto.jdbc.internal.jetty.util.component;

import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedOperation;
import java.util.EventListener;

@ManagedObject("Lifecycle Interface for startable components")
public abstract interface LifeCycle
{
  @ManagedOperation(value="Starts the instance", impact="ACTION")
  public abstract void start()
    throws Exception;
  
  @ManagedOperation(value="Stops the instance", impact="ACTION")
  public abstract void stop()
    throws Exception;
  
  public abstract boolean isRunning();
  
  public abstract boolean isStarted();
  
  public abstract boolean isStarting();
  
  public abstract boolean isStopping();
  
  public abstract boolean isStopped();
  
  public abstract boolean isFailed();
  
  public abstract void addLifeCycleListener(Listener paramListener);
  
  public abstract void removeLifeCycleListener(Listener paramListener);
  
  public static abstract interface Listener
    extends EventListener
  {
    public abstract void lifeCycleStarting(LifeCycle paramLifeCycle);
    
    public abstract void lifeCycleStarted(LifeCycle paramLifeCycle);
    
    public abstract void lifeCycleFailure(LifeCycle paramLifeCycle, Throwable paramThrowable);
    
    public abstract void lifeCycleStopping(LifeCycle paramLifeCycle);
    
    public abstract void lifeCycleStopped(LifeCycle paramLifeCycle);
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\LifeCycle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */