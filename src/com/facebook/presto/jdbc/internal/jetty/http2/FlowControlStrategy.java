package com.facebook.presto.jdbc.internal.jetty.http2;

import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;

public abstract interface FlowControlStrategy
{
  public static final int DEFAULT_WINDOW_SIZE = 65535;
  
  public abstract void onStreamCreated(IStream paramIStream);
  
  public abstract void onStreamDestroyed(IStream paramIStream);
  
  public abstract void updateInitialStreamWindow(ISession paramISession, int paramInt, boolean paramBoolean);
  
  public abstract void onWindowUpdate(ISession paramISession, IStream paramIStream, WindowUpdateFrame paramWindowUpdateFrame);
  
  public abstract void onDataReceived(ISession paramISession, IStream paramIStream, int paramInt);
  
  public abstract void onDataConsumed(ISession paramISession, IStream paramIStream, int paramInt);
  
  public abstract void windowUpdate(ISession paramISession, IStream paramIStream, WindowUpdateFrame paramWindowUpdateFrame);
  
  public abstract void onDataSending(IStream paramIStream, int paramInt);
  
  public abstract void onDataSent(IStream paramIStream, int paramInt);
  
  public static abstract interface Factory
  {
    public abstract FlowControlStrategy newFlowControlStrategy();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\FlowControlStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */