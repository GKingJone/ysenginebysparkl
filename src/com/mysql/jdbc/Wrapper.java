package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface Wrapper
{
  public abstract <T> T unwrap(Class<T> paramClass)
    throws SQLException;
  
  public abstract boolean isWrapperFor(Class<?> paramClass)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Wrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */