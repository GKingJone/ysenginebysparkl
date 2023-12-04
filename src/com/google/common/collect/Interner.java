package com.google.common.collect;

import com.google.common.annotations.Beta;

@Beta
public abstract interface Interner<E>
{
  public abstract E intern(E paramE);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\Interner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */