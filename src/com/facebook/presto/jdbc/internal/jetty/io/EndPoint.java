package com.facebook.presto.jdbc.internal.jetty.io;

import com.facebook.presto.jdbc.internal.jetty.util.Callback;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;

public abstract interface EndPoint
  extends Closeable
{
  public abstract InetSocketAddress getLocalAddress();
  
  public abstract InetSocketAddress getRemoteAddress();
  
  public abstract boolean isOpen();
  
  public abstract long getCreatedTimeStamp();
  
  public abstract void shutdownOutput();
  
  public abstract boolean isOutputShutdown();
  
  public abstract boolean isInputShutdown();
  
  public abstract void close();
  
  public abstract int fill(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract boolean flush(ByteBuffer... paramVarArgs)
    throws IOException;
  
  public abstract Object getTransport();
  
  public abstract long getIdleTimeout();
  
  public abstract void setIdleTimeout(long paramLong);
  
  public abstract void fillInterested(Callback paramCallback)
    throws ReadPendingException;
  
  public abstract boolean isFillInterested();
  
  public abstract void write(Callback paramCallback, ByteBuffer... paramVarArgs)
    throws WritePendingException;
  
  public abstract Connection getConnection();
  
  public abstract void setConnection(Connection paramConnection);
  
  public abstract void onOpen();
  
  public abstract void onClose();
  
  public abstract boolean isOptimizedForDirectBuffers();
  
  public abstract void upgrade(Connection paramConnection);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\EndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */