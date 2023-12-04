package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;

import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;

public abstract interface JsonIntegerFormatVisitor
  extends JsonValueFormatVisitor
{
  public abstract void numberType(JsonParser.NumberType paramNumberType);
  
  public static class Base
    extends JsonValueFormatVisitor.Base
    implements JsonIntegerFormatVisitor
  {
    public void numberType(JsonParser.NumberType type) {}
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonIntegerFormatVisitor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */