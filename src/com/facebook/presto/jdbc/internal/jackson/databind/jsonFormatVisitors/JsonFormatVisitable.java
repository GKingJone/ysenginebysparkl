package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;

import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;

public abstract interface JsonFormatVisitable
{
  public abstract void acceptJsonFormatVisitor(JsonFormatVisitorWrapper paramJsonFormatVisitorWrapper, JavaType paramJavaType)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonFormatVisitable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */