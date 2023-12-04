package com.facebook.presto.jdbc.internal.jackson.databind.introspect;

import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;

public abstract class ClassIntrospector
{
  public abstract BeanDescription forSerialization(SerializationConfig paramSerializationConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public abstract BeanDescription forDeserialization(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public abstract BeanDescription forDeserializationWithBuilder(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public abstract BeanDescription forCreation(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public abstract BeanDescription forClassAnnotations(MapperConfig<?> paramMapperConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public abstract BeanDescription forDirectClassAnnotations(MapperConfig<?> paramMapperConfig, JavaType paramJavaType, MixInResolver paramMixInResolver);
  
  public static abstract interface MixInResolver
  {
    public abstract Class<?> findMixInClassFor(Class<?> paramClass);
    
    public abstract MixInResolver copy();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\ClassIntrospector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */