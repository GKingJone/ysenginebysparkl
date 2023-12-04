package com.facebook.presto.jdbc.internal.jetty.util.component;

import java.util.concurrent.Future;

public abstract interface Graceful
{
  public abstract Future<Void> shutdown();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\Graceful.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */