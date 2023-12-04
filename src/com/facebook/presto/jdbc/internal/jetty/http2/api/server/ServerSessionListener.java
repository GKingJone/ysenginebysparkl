package com.facebook.presto.jdbc.internal.jetty.http2.api.server;

import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
import com.facebook.presto.jdbc.internal.jetty.http2.api.Session.Listener;
import com.facebook.presto.jdbc.internal.jetty.http2.api.Session.Listener.Adapter;

public abstract interface ServerSessionListener
  extends Session.Listener
{
  public abstract void onAccept(Session paramSession);
  
  public static class Adapter
    extends Session.Listener.Adapter
    implements ServerSessionListener
  {
    public void onAccept(Session session) {}
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\api\server\ServerSessionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */