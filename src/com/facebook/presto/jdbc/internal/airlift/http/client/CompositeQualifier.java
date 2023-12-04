package com.facebook.presto.jdbc.internal.airlift.http.client;

import com.facebook.presto.jdbc.internal.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@interface CompositeQualifier
{
  Class<?>[] value();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\CompositeQualifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */