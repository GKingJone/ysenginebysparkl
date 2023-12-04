package com.facebook.presto.jdbc.internal.jackson.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

public abstract class OutputDecorator
  implements Serializable
{
  public abstract OutputStream decorate(IOContext paramIOContext, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract Writer decorate(IOContext paramIOContext, Writer paramWriter)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\OutputDecorator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */