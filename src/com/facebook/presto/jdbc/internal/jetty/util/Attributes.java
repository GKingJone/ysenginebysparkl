package com.facebook.presto.jdbc.internal.jetty.util;

import java.util.Enumeration;

public abstract interface Attributes
{
  public abstract void removeAttribute(String paramString);
  
  public abstract void setAttribute(String paramString, Object paramObject);
  
  public abstract Object getAttribute(String paramString);
  
  public abstract Enumeration<String> getAttributeNames();
  
  public abstract void clearAttributes();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */