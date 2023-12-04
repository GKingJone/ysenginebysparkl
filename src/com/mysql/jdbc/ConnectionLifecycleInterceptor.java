package com.mysql.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public abstract interface ConnectionLifecycleInterceptor
  extends Extension
{
  public abstract void close()
    throws SQLException;
  
  public abstract boolean commit()
    throws SQLException;
  
  public abstract boolean rollback()
    throws SQLException;
  
  public abstract boolean rollback(Savepoint paramSavepoint)
    throws SQLException;
  
  public abstract boolean setAutoCommit(boolean paramBoolean)
    throws SQLException;
  
  public abstract boolean setCatalog(String paramString)
    throws SQLException;
  
  public abstract boolean transactionBegun()
    throws SQLException;
  
  public abstract boolean transactionCompleted()
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionLifecycleInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */