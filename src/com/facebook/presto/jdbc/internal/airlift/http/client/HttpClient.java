package com.facebook.presto.jdbc.internal.airlift.http.client;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.util.concurrent.ListenableFuture;
import java.io.Closeable;

@Beta
public abstract interface HttpClient
  extends Closeable
{
  public abstract <T, E extends Exception> T execute(Request paramRequest, ResponseHandler<T, E> paramResponseHandler)
    throws Exception;
  
  public abstract <T, E extends Exception> HttpResponseFuture<T> executeAsync(Request paramRequest, ResponseHandler<T, E> paramResponseHandler);
  
  public abstract RequestStats getStats();
  
  public abstract long getMaxContentLength();
  
  public abstract void close();
  
  public static abstract interface HttpResponseFuture<T>
    extends ListenableFuture<T>
  {
    public abstract String getState();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */