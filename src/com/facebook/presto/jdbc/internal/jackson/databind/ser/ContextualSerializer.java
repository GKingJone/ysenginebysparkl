package com.facebook.presto.jdbc.internal.jackson.databind.ser;

import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;

public abstract interface ContextualSerializer
{
  public abstract JsonSerializer<?> createContextual(SerializerProvider paramSerializerProvider, BeanProperty paramBeanProperty)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\ContextualSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */