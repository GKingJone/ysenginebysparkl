package com.mchange.v2.codegen.bean;

import java.io.IOException;
import java.io.Writer;

public abstract interface PropertyBeanGenerator
{
  public abstract void generate(ClassInfo paramClassInfo, Property[] paramArrayOfProperty, Writer paramWriter)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\PropertyBeanGenerator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */