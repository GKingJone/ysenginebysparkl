package com.facebook.presto.jdbc.internal.spi.block;

public abstract interface LazyBlockLoader<T extends Block>
{
  public abstract void load(T paramT);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LazyBlockLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */