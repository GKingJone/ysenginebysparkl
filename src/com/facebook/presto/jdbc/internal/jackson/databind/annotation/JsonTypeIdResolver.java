package com.facebook.presto.jdbc.internal.jackson.databind.annotation;

import com.facebook.presto.jdbc.internal.jackson.annotation.JacksonAnnotation;
import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonTypeIdResolver
{
  Class<? extends TypeIdResolver> value();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\annotation\JsonTypeIdResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */