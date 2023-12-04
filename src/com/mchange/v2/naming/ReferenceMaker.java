package com.mchange.v2.naming;

import javax.naming.NamingException;
import javax.naming.Reference;

public abstract interface ReferenceMaker
{
  public abstract Reference createReference(Object paramObject)
    throws NamingException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\naming\ReferenceMaker.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */