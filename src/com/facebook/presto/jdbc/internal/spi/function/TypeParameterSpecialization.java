package com.facebook.presto.jdbc.internal.spi.function;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
public @interface TypeParameterSpecialization
{
  String name();
  
  Class<?> nativeContainerType();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\TypeParameterSpecialization.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */