package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public abstract interface StatementInterceptor
  extends Extension
{
  public abstract void init(Connection paramConnection, Properties paramProperties)
    throws SQLException;
  
  public abstract ResultSetInternalMethods preProcess(String paramString, Statement paramStatement, Connection paramConnection)
    throws SQLException;
  
  public abstract ResultSetInternalMethods postProcess(String paramString, Statement paramStatement, ResultSetInternalMethods paramResultSetInternalMethods, Connection paramConnection)
    throws SQLException;
  
  public abstract boolean executeTopLevelOnly();
  
  public abstract void destroy();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\StatementInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */