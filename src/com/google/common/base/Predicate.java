package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface Predicate<T>
{
  public abstract boolean apply(@Nullable T paramT);
  
  public abstract boolean equals(@Nullable Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Predicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */