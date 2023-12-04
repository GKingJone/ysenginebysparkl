package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import java.io.IOException;
import java.util.Collection;

public abstract interface GeneratorExtension
{
  public abstract Collection extraGeneralImports();
  
  public abstract Collection extraSpecificImports();
  
  public abstract Collection extraInterfaceNames();
  
  public abstract void generate(ClassInfo paramClassInfo, Class paramClass, Property[] paramArrayOfProperty, Class[] paramArrayOfClass, IndentedWriter paramIndentedWriter)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\GeneratorExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */