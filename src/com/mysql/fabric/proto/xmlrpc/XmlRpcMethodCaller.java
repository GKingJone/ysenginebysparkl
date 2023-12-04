package com.mysql.fabric.proto.xmlrpc;

import com.mysql.fabric.FabricCommunicationException;
import java.util.List;

public abstract interface XmlRpcMethodCaller
{
  public abstract List<?> call(String paramString, Object[] paramArrayOfObject)
    throws FabricCommunicationException;
  
  public abstract void setHeader(String paramString1, String paramString2);
  
  public abstract void clearHeader(String paramString);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\XmlRpcMethodCaller.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */