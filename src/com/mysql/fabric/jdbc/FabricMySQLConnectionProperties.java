package com.mysql.fabric.jdbc;

import com.mysql.jdbc.ConnectionProperties;

public abstract interface FabricMySQLConnectionProperties
  extends ConnectionProperties
{
  public abstract void setFabricShardKey(String paramString);
  
  public abstract String getFabricShardKey();
  
  public abstract void setFabricShardTable(String paramString);
  
  public abstract String getFabricShardTable();
  
  public abstract void setFabricServerGroup(String paramString);
  
  public abstract String getFabricServerGroup();
  
  public abstract void setFabricProtocol(String paramString);
  
  public abstract String getFabricProtocol();
  
  public abstract void setFabricUsername(String paramString);
  
  public abstract String getFabricUsername();
  
  public abstract void setFabricPassword(String paramString);
  
  public abstract String getFabricPassword();
  
  public abstract void setFabricReportErrors(boolean paramBoolean);
  
  public abstract boolean getFabricReportErrors();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\FabricMySQLConnectionProperties.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */