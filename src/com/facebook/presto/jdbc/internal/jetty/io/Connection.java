package com.facebook.presto.jdbc.internal.jetty.io;

import java.io.Closeable;
import java.nio.ByteBuffer;

public abstract interface Connection
  extends Closeable
{
  public abstract void addListener(Listener paramListener);
  
  public abstract void removeListener(Listener paramListener);
  
  public abstract void onOpen();
  
  public abstract void onClose();
  
  public abstract EndPoint getEndPoint();
  
  public abstract void close();
  
  public abstract boolean onIdleExpired();
  
  public abstract int getMessagesIn();
  
  public abstract int getMessagesOut();
  
  public abstract long getBytesIn();
  
  public abstract long getBytesOut();
  
  public abstract long getCreatedTimeStamp();
  
  public static abstract interface Listener
  {
    public abstract void onOpened(Connection paramConnection);
    
    public abstract void onClosed(Connection paramConnection);
    
    public static class Adapter
      implements Listener
    {
      public void onOpened(Connection connection) {}
      
      public void onClosed(Connection connection) {}
    }
  }
  
  public static abstract interface UpgradeTo
    extends Connection
  {
    public abstract void onUpgradeTo(ByteBuffer paramByteBuffer);
  }
  
  public static abstract interface UpgradeFrom
    extends Connection
  {
    public abstract ByteBuffer onUpgradeFrom();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */