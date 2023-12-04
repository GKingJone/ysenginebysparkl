package com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors;

import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;

public abstract interface JsonFormatVisitorWithSerializerProvider
{
  public abstract SerializerProvider getProvider();
  
  public abstract void setProvider(SerializerProvider paramSerializerProvider);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonFormatVisitors\JsonFormatVisitorWithSerializerProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */