package com.facebook.presto.jdbc.internal.jol.layouters;

import com.facebook.presto.jdbc.internal.jol.info.ClassData;
import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;

public abstract interface Layouter
{
  public abstract ClassLayout layout(ClassData paramClassData);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\layouters\Layouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */