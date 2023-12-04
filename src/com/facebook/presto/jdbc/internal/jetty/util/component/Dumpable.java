package com.facebook.presto.jdbc.internal.jetty.util.component;

import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedOperation;
import java.io.IOException;

@ManagedObject("Dumpable Object")
public abstract interface Dumpable
{
  @ManagedOperation(value="Dump the nested Object state as a String", impact="INFO")
  public abstract String dump();
  
  public abstract void dump(Appendable paramAppendable, String paramString)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\Dumpable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */