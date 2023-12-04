package com.mchange.v2.c3p0;

import java.sql.Connection;

public abstract interface ConnectionCustomizer
{
  public abstract void onAcquire(Connection paramConnection, String paramString)
    throws Exception;
  
  public abstract void onDestroy(Connection paramConnection, String paramString)
    throws Exception;
  
  public abstract void onCheckOut(Connection paramConnection, String paramString)
    throws Exception;
  
  public abstract void onCheckIn(Connection paramConnection, String paramString)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\ConnectionCustomizer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */