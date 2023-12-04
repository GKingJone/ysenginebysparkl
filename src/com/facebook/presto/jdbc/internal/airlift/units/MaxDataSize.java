package com.facebook.presto.jdbc.internal.airlift.units;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy={MaxDataSizeValidator.class})
public @interface MaxDataSize
{
  String value();
  
  String message() default "{io.airlift.units.MaxDataSize.message}";
  
  Class<?>[] groups() default {};
  
  Class<? extends Payload>[] payload() default {};
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\MaxDataSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */