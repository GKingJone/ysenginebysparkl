package com.facebook.presto.jdbc.internal.jackson.databind.jsontype;

import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
import com.facebook.presto.jdbc.internal.jackson.databind.DatabindContext;
import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import java.io.IOException;

public abstract interface TypeIdResolver
{
  public abstract void init(JavaType paramJavaType);
  
  public abstract String idFromValue(Object paramObject);
  
  public abstract String idFromValueAndType(Object paramObject, Class<?> paramClass);
  
  public abstract String idFromBaseType();
  
  public abstract JavaType typeFromId(DatabindContext paramDatabindContext, String paramString)
    throws IOException;
  
  public abstract String getDescForKnownTypeIds();
  
  public abstract JsonTypeInfo.Id getMechanism();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\TypeIdResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */