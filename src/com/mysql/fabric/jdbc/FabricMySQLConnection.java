package com.mysql.fabric.jdbc;

import com.mysql.fabric.ServerGroup;
import com.mysql.jdbc.MySQLConnection;
import java.sql.SQLException;
import java.util.Set;

public abstract interface FabricMySQLConnection
  extends MySQLConnection
{
  public abstract void clearServerSelectionCriteria()
    throws SQLException;
  
  public abstract void setShardKey(String paramString)
    throws SQLException;
  
  public abstract String getShardKey();
  
  public abstract void setShardTable(String paramString)
    throws SQLException;
  
  public abstract String getShardTable();
  
  public abstract void setServerGroupName(String paramString)
    throws SQLException;
  
  public abstract String getServerGroupName();
  
  public abstract ServerGroup getCurrentServerGroup();
  
  public abstract void clearQueryTables()
    throws SQLException;
  
  public abstract void addQueryTable(String paramString)
    throws SQLException;
  
  public abstract Set<String> getQueryTables();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\FabricMySQLConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */