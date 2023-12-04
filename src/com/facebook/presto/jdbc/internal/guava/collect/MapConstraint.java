package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
public abstract interface MapConstraint<K, V>
{
  public abstract void checkKeyValue(@Nullable K paramK, @Nullable V paramV);
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\MapConstraint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */