package com.facebook.presto.jdbc.internal.jackson.databind.deser;

import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;

public abstract interface KeyDeserializers
{
  public abstract KeyDeserializer findKeyDeserializer(JavaType paramJavaType, DeserializationConfig paramDeserializationConfig, BeanDescription paramBeanDescription)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\KeyDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */