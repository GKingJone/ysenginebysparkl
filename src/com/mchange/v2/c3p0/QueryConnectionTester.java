package com.mchange.v2.c3p0;

import java.sql.Connection;

public abstract interface QueryConnectionTester
  extends ConnectionTester
{
  public abstract int activeCheckConnection(Connection paramConnection, String paramString);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\QueryConnectionTester.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */