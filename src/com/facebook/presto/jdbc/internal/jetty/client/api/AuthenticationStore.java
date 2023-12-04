package com.facebook.presto.jdbc.internal.jetty.client.api;

import java.net.URI;

public abstract interface AuthenticationStore
{
  public abstract void addAuthentication(Authentication paramAuthentication);
  
  public abstract void removeAuthentication(Authentication paramAuthentication);
  
  public abstract void clearAuthentications();
  
  public abstract Authentication findAuthentication(String paramString1, URI paramURI, String paramString2);
  
  public abstract void addAuthenticationResult(Authentication.Result paramResult);
  
  public abstract void removeAuthenticationResult(Authentication.Result paramResult);
  
  public abstract void clearAuthenticationResults();
  
  public abstract Authentication.Result findAuthenticationResult(URI paramURI);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\AuthenticationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */