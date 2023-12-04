package com.facebook.presto.jdbc.internal.jackson.databind.jsontype;

import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
import java.util.Collection;

public abstract interface TypeResolverBuilder<T extends TypeResolverBuilder<T>>
{
  public abstract Class<?> getDefaultImpl();
  
  public abstract TypeSerializer buildTypeSerializer(SerializationConfig paramSerializationConfig, JavaType paramJavaType, Collection<NamedType> paramCollection);
  
  public abstract TypeDeserializer buildTypeDeserializer(DeserializationConfig paramDeserializationConfig, JavaType paramJavaType, Collection<NamedType> paramCollection);
  
  public abstract T init(JsonTypeInfo.Id paramId, TypeIdResolver paramTypeIdResolver);
  
  public abstract T inclusion(JsonTypeInfo.As paramAs);
  
  public abstract T typeProperty(String paramString);
  
  public abstract T defaultImpl(Class<?> paramClass);
  
  public abstract T typeIdVisibility(boolean paramBoolean);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\TypeResolverBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */