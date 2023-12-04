package com.facebook.presto.jdbc.internal.jetty.io;

import java.net.Socket;
import java.nio.ByteBuffer;

public abstract interface NetworkTrafficListener
{
  public abstract void opened(Socket paramSocket);
  
  public abstract void incoming(Socket paramSocket, ByteBuffer paramByteBuffer);
  
  public abstract void outgoing(Socket paramSocket, ByteBuffer paramByteBuffer);
  
  public abstract void closed(Socket paramSocket);
  
  public static class Adapter
    implements NetworkTrafficListener
  {
    public void opened(Socket socket) {}
    
    public void incoming(Socket socket, ByteBuffer bytes) {}
    
    public void outgoing(Socket socket, ByteBuffer bytes) {}
    
    public void closed(Socket socket) {}
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\NetworkTrafficListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */