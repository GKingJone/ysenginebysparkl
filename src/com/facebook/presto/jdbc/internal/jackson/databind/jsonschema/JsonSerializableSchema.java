package com.facebook.presto.jdbc.internal.jackson.databind.jsonschema;

import com.facebook.presto.jdbc.internal.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerializableSchema
{
  public static final String NO_VALUE = "##irrelevant";
  
  String id() default "";
  
  String schemaType() default "any";
  
  @Deprecated
  String schemaObjectPropertiesDefinition() default "##irrelevant";
  
  @Deprecated
  String schemaItemDefinition() default "##irrelevant";
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsonschema\JsonSerializableSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */