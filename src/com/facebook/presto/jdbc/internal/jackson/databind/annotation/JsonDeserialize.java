package com.facebook.presto.jdbc.internal.jackson.databind.annotation;

import com.facebook.presto.jdbc.internal.jackson.annotation.JacksonAnnotation;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer.None;
import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer.None;
import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.ANNOTATION_TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonDeserialize
{
  Class<? extends JsonDeserializer> using() default JsonDeserializer.None.class;
  
  Class<? extends JsonDeserializer> contentUsing() default JsonDeserializer.None.class;
  
  Class<? extends KeyDeserializer> keyUsing() default KeyDeserializer.None.class;
  
  Class<?> builder() default Void.class;
  
  Class<? extends Converter> converter() default None.class;
  
  Class<? extends Converter> contentConverter() default None.class;
  
  Class<?> as() default Void.class;
  
  Class<?> keyAs() default Void.class;
  
  Class<?> contentAs() default Void.class;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\annotation\JsonDeserialize.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */