package com.mchange.v2.ser;

import java.io.IOException;
import java.io.Serializable;

public abstract interface IndirectlySerialized
  extends Serializable
{
  public abstract Object getObject()
    throws ClassNotFoundException, IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\ser\IndirectlySerialized.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */