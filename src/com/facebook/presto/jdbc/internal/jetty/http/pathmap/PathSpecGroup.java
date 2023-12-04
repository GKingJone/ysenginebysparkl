package com.facebook.presto.jdbc.internal.jetty.http.pathmap;

public enum PathSpecGroup
{
  EXACT,  MIDDLE_GLOB,  PREFIX_GLOB,  SUFFIX_GLOB,  ROOT,  DEFAULT;
  
  private PathSpecGroup() {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\PathSpecGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */