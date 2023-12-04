package com.facebook.presto.jdbc.internal.jetty.client;

import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
import java.net.InetSocketAddress;
import java.util.Map;

public abstract interface HttpClientTransport
  extends ClientConnectionFactory
{
  public static final String HTTP_DESTINATION_CONTEXT_KEY = "http.destination";
  public static final String HTTP_CONNECTION_PROMISE_CONTEXT_KEY = "http.connection.promise";
  
  public abstract void setHttpClient(HttpClient paramHttpClient);
  
  public abstract HttpDestination newHttpDestination(Origin paramOrigin);
  
  public abstract void connect(InetSocketAddress paramInetSocketAddress, Map<String, Object> paramMap);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpClientTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */