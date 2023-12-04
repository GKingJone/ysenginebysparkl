package com.mchange.v2.log;

public abstract interface NameTransformer
{
  public abstract String transformName(String paramString);
  
  public abstract String transformName(Class paramClass);
  
  public abstract String transformName();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\NameTransformer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */