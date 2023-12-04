package com.facebook.presto.jdbc.internal.jackson.databind.deser;

import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;

public abstract interface ContextualDeserializer
{
  public abstract JsonDeserializer<?> createContextual(DeserializationContext paramDeserializationContext, BeanProperty paramBeanProperty)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\ContextualDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */