package com.facebook.presto.jdbc.internal.jackson.databind.deser;

import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;

public abstract interface ResolvableDeserializer
{
  public abstract void resolve(DeserializationContext paramDeserializationContext)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\ResolvableDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */