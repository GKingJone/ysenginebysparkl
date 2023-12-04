package com.facebook.presto.jdbc.internal.jackson.databind.type;

import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
import java.lang.reflect.Type;

public abstract class TypeModifier
{
  public abstract JavaType modifyType(JavaType paramJavaType, Type paramType, TypeBindings paramTypeBindings, TypeFactory paramTypeFactory);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\type\TypeModifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */