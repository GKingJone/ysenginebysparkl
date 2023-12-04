package com.mchange.v1.identicator;

public abstract interface Identicator
{
  public abstract boolean identical(Object paramObject1, Object paramObject2);
  
  public abstract int hash(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\Identicator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */