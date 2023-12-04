package com.facebook.presto.jdbc.internal.jackson.databind.ser;

import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;

public abstract class SerializerFactory
{
  public abstract SerializerFactory withAdditionalSerializers(Serializers paramSerializers);
  
  public abstract SerializerFactory withAdditionalKeySerializers(Serializers paramSerializers);
  
  public abstract SerializerFactory withSerializerModifier(BeanSerializerModifier paramBeanSerializerModifier);
  
  public abstract JsonSerializer<Object> createSerializer(SerializerProvider paramSerializerProvider, JavaType paramJavaType)
    throws JsonMappingException;
  
  public abstract TypeSerializer createTypeSerializer(SerializationConfig paramSerializationConfig, JavaType paramJavaType)
    throws JsonMappingException;
  
  public abstract JsonSerializer<Object> createKeySerializer(SerializationConfig paramSerializationConfig, JavaType paramJavaType, JsonSerializer<Object> paramJsonSerializer)
    throws JsonMappingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\SerializerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */