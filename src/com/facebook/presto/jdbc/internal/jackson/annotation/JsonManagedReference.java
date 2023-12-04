package com.facebook.presto.jdbc.internal.jackson.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonManagedReference
{
  String value() default "defaultReference";
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\JsonManagedReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */