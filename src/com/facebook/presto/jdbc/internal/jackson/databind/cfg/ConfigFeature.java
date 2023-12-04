package com.facebook.presto.jdbc.internal.jackson.databind.cfg;

public abstract interface ConfigFeature
{
  public abstract boolean enabledByDefault();
  
  public abstract int getMask();
  
  public abstract boolean enabledIn(int paramInt);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\cfg\ConfigFeature.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */