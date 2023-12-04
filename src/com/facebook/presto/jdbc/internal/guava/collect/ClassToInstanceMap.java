package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface ClassToInstanceMap<B>
  extends Map<Class<? extends B>, B>
{
  public abstract <T extends B> T getInstance(Class<T> paramClass);
  
  public abstract <T extends B> T putInstance(Class<T> paramClass, @Nullable T paramT);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ClassToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */