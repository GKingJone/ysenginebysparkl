package com.mchange.v2.coalesce;

public abstract interface CoalesceChecker
{
  public abstract boolean checkCoalesce(Object paramObject1, Object paramObject2);
  
  public abstract int coalesceHash(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\CoalesceChecker.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */