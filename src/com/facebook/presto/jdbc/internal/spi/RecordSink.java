package com.facebook.presto.jdbc.internal.spi;

import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
import com.facebook.presto.jdbc.internal.spi.type.Type;
import java.util.Collection;
import java.util.List;

public abstract interface RecordSink
{
  public abstract void beginRecord(long paramLong);
  
  public abstract void finishRecord();
  
  public abstract void appendNull();
  
  public abstract void appendBoolean(boolean paramBoolean);
  
  public abstract void appendLong(long paramLong);
  
  public abstract void appendDouble(double paramDouble);
  
  public abstract void appendString(byte[] paramArrayOfByte);
  
  public abstract void appendObject(Object paramObject);
  
  public abstract Collection<Slice> commit();
  
  public abstract void rollback();
  
  public abstract List<Type> getColumnTypes();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\RecordSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */