package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;

public abstract interface JsonBooleanFormatVisitor
  extends JsonValueFormatVisitor
{
  public static class Base
    extends JsonValueFormatVisitor.Base
    implements JsonBooleanFormatVisitor
  {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonBooleanFormatVisitor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */