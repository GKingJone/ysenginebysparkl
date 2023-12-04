package com.google.gson;

public abstract interface ExclusionStrategy
{
  public abstract boolean shouldSkipField(FieldAttributes paramFieldAttributes);
  
  public abstract boolean shouldSkipClass(Class<?> paramClass);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\ExclusionStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */