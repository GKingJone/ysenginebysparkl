package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface PingTarget
{
  public abstract void doPing()
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\PingTarget.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */