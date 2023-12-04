package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface LoadBalanceExceptionChecker
  extends Extension
{
  public abstract boolean shouldExceptionTriggerFailover(SQLException paramSQLException);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\LoadBalanceExceptionChecker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */