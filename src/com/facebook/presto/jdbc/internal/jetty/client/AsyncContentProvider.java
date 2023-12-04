package com.facebook.presto.jdbc.internal.jetty.client;

import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
import java.util.EventListener;

public abstract interface AsyncContentProvider
  extends ContentProvider
{
  public abstract void setListener(Listener paramListener);
  
  public static abstract interface Listener
    extends EventListener
  {
    public abstract void onContent();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\AsyncContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */