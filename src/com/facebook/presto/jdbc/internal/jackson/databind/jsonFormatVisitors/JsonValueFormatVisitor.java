package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;

import java.util.Set;

public abstract interface JsonValueFormatVisitor
{
  public abstract void format(JsonValueFormat paramJsonValueFormat);
  
  public abstract void enumTypes(Set<String> paramSet);
  
  public static class Base
    implements JsonValueFormatVisitor
  {
    public void format(JsonValueFormat format) {}
    
    public void enumTypes(Set<String> enums) {}
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonValueFormatVisitor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */