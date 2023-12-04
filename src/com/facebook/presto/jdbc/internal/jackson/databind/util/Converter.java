package com.facebook.presto.jdbc.internal.jackson.databind.util;

import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;

public abstract interface Converter<IN, OUT>
{
  public abstract OUT convert(IN paramIN);
  
  public abstract JavaType getInputType(TypeFactory paramTypeFactory);
  
  public abstract JavaType getOutputType(TypeFactory paramTypeFactory);
  
  public static abstract class None
    implements Converter<Object, Object>
  {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\Converter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */