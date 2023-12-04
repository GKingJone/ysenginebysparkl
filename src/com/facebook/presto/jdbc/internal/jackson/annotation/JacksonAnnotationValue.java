package com.facebook.presto.jdbc.internal.jackson.annotation;

import java.lang.annotation.Annotation;

public abstract interface JacksonAnnotationValue<A extends Annotation>
{
  public abstract Class<A> valueFor();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\JacksonAnnotationValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */