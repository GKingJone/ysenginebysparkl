package com.mchange.v2.c3p0;

import java.io.Serializable;
import java.sql.Connection;

public abstract interface ConnectionTester
  extends Serializable
{
  public static final int CONNECTION_IS_OKAY = 0;
  public static final int CONNECTION_IS_INVALID = -1;
  public static final int DATABASE_IS_INVALID = -8;
  
  public abstract int activeCheckConnection(Connection paramConnection);
  
  public abstract int statusOnException(Connection paramConnection, Throwable paramThrowable);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\ConnectionTester.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */