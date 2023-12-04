package com.facebook.presto.jdbc.internal.spi.predicate;

import java.util.Collection;

public abstract interface DiscreteValues
{
  public abstract boolean isWhiteList();
  
  public abstract Collection<Object> getValues();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\DiscreteValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */