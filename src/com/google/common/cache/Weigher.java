package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public abstract interface Weigher<K, V>
{
  public abstract int weigh(K paramK, V paramV);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\Weigher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */