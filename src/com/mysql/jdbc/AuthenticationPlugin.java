package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;

public abstract interface AuthenticationPlugin
  extends Extension
{
  public abstract String getProtocolPluginName();
  
  public abstract boolean requiresConfidentiality();
  
  public abstract boolean isReusable();
  
  public abstract void setAuthenticationParameters(String paramString1, String paramString2);
  
  public abstract boolean nextAuthenticationStep(Buffer paramBuffer, List<Buffer> paramList)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\AuthenticationPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */