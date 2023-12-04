package com.facebook.presto.jdbc.internal.guava.io;

import java.io.IOException;

@Deprecated
public abstract interface InputSupplier<T>
{
  public abstract T getInput()
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\InputSupplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */