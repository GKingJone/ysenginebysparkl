package com.facebook.presto.jdbc.internal.jetty.http2;

import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
import com.facebook.presto.jdbc.internal.jetty.util.Callback;
import com.facebook.presto.jdbc.internal.jetty.util.Promise;

public abstract interface ISession
  extends Session
{
  public abstract IStream getStream(int paramInt);
  
  public abstract void removeStream(IStream paramIStream);
  
  public abstract void frames(IStream paramIStream, Callback paramCallback, Frame paramFrame, Frame... paramVarArgs);
  
  public abstract void push(IStream paramIStream, Promise<Stream> paramPromise, PushPromiseFrame paramPushPromiseFrame, Stream.Listener paramListener);
  
  public abstract void data(IStream paramIStream, Callback paramCallback, DataFrame paramDataFrame);
  
  public abstract int updateSendWindow(int paramInt);
  
  public abstract int updateRecvWindow(int paramInt);
  
  public abstract void onWindowUpdate(IStream paramIStream, WindowUpdateFrame paramWindowUpdateFrame);
  
  public abstract boolean isPushEnabled();
  
  public abstract void onShutdown();
  
  public abstract boolean onIdleTimeout();
  
  public abstract void onFrame(Frame paramFrame);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\ISession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */