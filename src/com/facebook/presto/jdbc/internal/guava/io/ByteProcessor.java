package com.facebook.presto.jdbc.internal.guava.io;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface ByteProcessor<T>
{
  public abstract boolean processBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract T getResult();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\ByteProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */