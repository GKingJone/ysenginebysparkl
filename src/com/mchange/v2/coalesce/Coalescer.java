package com.mchange.v2.coalesce;

import java.util.Iterator;

public abstract interface Coalescer
{
  public abstract Object coalesce(Object paramObject);
  
  public abstract int countCoalesced();
  
  public abstract Iterator iterator();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\coalesce\Coalescer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */