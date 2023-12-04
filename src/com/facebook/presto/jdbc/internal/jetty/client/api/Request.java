package com.facebook.presto.jdbc.internal.jetty.client.api;

import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
import com.facebook.presto.jdbc.internal.jetty.util.Fields;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract interface Request
{
  public abstract String getScheme();
  
  public abstract Request scheme(String paramString);
  
  public abstract String getHost();
  
  public abstract int getPort();
  
  public abstract String getMethod();
  
  public abstract Request method(HttpMethod paramHttpMethod);
  
  public abstract Request method(String paramString);
  
  public abstract String getPath();
  
  public abstract Request path(String paramString);
  
  public abstract String getQuery();
  
  public abstract URI getURI();
  
  public abstract HttpVersion getVersion();
  
  public abstract Request version(HttpVersion paramHttpVersion);
  
  public abstract Fields getParams();
  
  public abstract Request param(String paramString1, String paramString2);
  
  public abstract HttpFields getHeaders();
  
  public abstract Request header(String paramString1, String paramString2);
  
  public abstract Request header(HttpHeader paramHttpHeader, String paramString);
  
  public abstract List<HttpCookie> getCookies();
  
  public abstract Request cookie(HttpCookie paramHttpCookie);
  
  public abstract Request attribute(String paramString, Object paramObject);
  
  public abstract Map<String, Object> getAttributes();
  
  public abstract ContentProvider getContent();
  
  public abstract Request content(ContentProvider paramContentProvider);
  
  public abstract Request content(ContentProvider paramContentProvider, String paramString);
  
  public abstract Request file(Path paramPath)
    throws IOException;
  
  public abstract Request file(Path paramPath, String paramString)
    throws IOException;
  
  public abstract String getAgent();
  
  public abstract Request agent(String paramString);
  
  public abstract Request accept(String... paramVarArgs);
  
  public abstract long getIdleTimeout();
  
  public abstract Request idleTimeout(long paramLong, TimeUnit paramTimeUnit);
  
  public abstract long getTimeout();
  
  public abstract Request timeout(long paramLong, TimeUnit paramTimeUnit);
  
  public abstract boolean isFollowRedirects();
  
  public abstract Request followRedirects(boolean paramBoolean);
  
  public abstract <T extends RequestListener> List<T> getRequestListeners(Class<T> paramClass);
  
  public abstract Request listener(Listener paramListener);
  
  public abstract Request onRequestQueued(QueuedListener paramQueuedListener);
  
  public abstract Request onRequestBegin(BeginListener paramBeginListener);
  
  public abstract Request onRequestHeaders(HeadersListener paramHeadersListener);
  
  public abstract Request onRequestCommit(CommitListener paramCommitListener);
  
  public abstract Request onRequestContent(ContentListener paramContentListener);
  
  public abstract Request onRequestSuccess(SuccessListener paramSuccessListener);
  
  public abstract Request onRequestFailure(FailureListener paramFailureListener);
  
  public abstract Request onResponseBegin(Response.BeginListener paramBeginListener);
  
  public abstract Request onResponseHeader(Response.HeaderListener paramHeaderListener);
  
  public abstract Request onResponseHeaders(Response.HeadersListener paramHeadersListener);
  
  public abstract Request onResponseContent(Response.ContentListener paramContentListener);
  
  public abstract Request onResponseContentAsync(Response.AsyncContentListener paramAsyncContentListener);
  
  public abstract Request onResponseSuccess(Response.SuccessListener paramSuccessListener);
  
  public abstract Request onResponseFailure(Response.FailureListener paramFailureListener);
  
  public abstract Request onComplete(Response.CompleteListener paramCompleteListener);
  
  public abstract ContentResponse send()
    throws InterruptedException, TimeoutException, ExecutionException;
  
  public abstract void send(Response.CompleteListener paramCompleteListener);
  
  public abstract boolean abort(Throwable paramThrowable);
  
  public abstract Throwable getAbortCause();
  
  public static abstract interface Listener
    extends QueuedListener, BeginListener, HeadersListener, CommitListener, ContentListener, SuccessListener, FailureListener
  {
    public static class Adapter
      implements Listener
    {
      public void onQueued(Request request) {}
      
      public void onBegin(Request request) {}
      
      public void onHeaders(Request request) {}
      
      public void onCommit(Request request) {}
      
      public void onContent(Request request, ByteBuffer content) {}
      
      public void onSuccess(Request request) {}
      
      public void onFailure(Request request, Throwable failure) {}
    }
  }
  
  public static abstract interface FailureListener
    extends RequestListener
  {
    public abstract void onFailure(Request paramRequest, Throwable paramThrowable);
  }
  
  public static abstract interface SuccessListener
    extends RequestListener
  {
    public abstract void onSuccess(Request paramRequest);
  }
  
  public static abstract interface ContentListener
    extends RequestListener
  {
    public abstract void onContent(Request paramRequest, ByteBuffer paramByteBuffer);
  }
  
  public static abstract interface CommitListener
    extends RequestListener
  {
    public abstract void onCommit(Request paramRequest);
  }
  
  public static abstract interface HeadersListener
    extends RequestListener
  {
    public abstract void onHeaders(Request paramRequest);
  }
  
  public static abstract interface BeginListener
    extends RequestListener
  {
    public abstract void onBegin(Request paramRequest);
  }
  
  public static abstract interface QueuedListener
    extends RequestListener
  {
    public abstract void onQueued(Request paramRequest);
  }
  
  public static abstract interface RequestListener
    extends EventListener
  {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */