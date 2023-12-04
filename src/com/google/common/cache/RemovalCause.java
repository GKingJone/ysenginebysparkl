package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public enum RemovalCause
{
  EXPLICIT,  REPLACED,  COLLECTED,  EXPIRED,  SIZE;
  
  private RemovalCause() {}
  
  abstract boolean wasEvicted();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\RemovalCause.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */