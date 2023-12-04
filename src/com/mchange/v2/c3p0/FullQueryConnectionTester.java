package com.mchange.v2.c3p0;

import java.sql.Connection;

public abstract interface FullQueryConnectionTester
  extends QueryConnectionTester
{
  public abstract int statusOnException(Connection paramConnection, Throwable paramThrowable, String paramString);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\FullQueryConnectionTester.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */