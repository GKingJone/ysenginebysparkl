package com.mchange.v2.c3p0;

import java.sql.Connection;

public abstract class AbstractConnectionCustomizer
  implements ConnectionCustomizer
{
  public void onAcquire(Connection c, String parentDataSourceIdentityToken)
    throws Exception
  {}
  
  public void onDestroy(Connection c, String parentDataSourceIdentityToken)
    throws Exception
  {}
  
  public void onCheckOut(Connection c, String parentDataSourceIdentityToken)
    throws Exception
  {}
  
  public void onCheckIn(Connection c, String parentDataSourceIdentityToken)
    throws Exception
  {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\AbstractConnectionCustomizer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */