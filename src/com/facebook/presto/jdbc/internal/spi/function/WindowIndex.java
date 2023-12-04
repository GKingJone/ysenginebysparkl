package com.facebook.presto.jdbc.internal.spi.function;

import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;

public abstract interface WindowIndex
{
  public abstract int size();
  
  public abstract boolean isNull(int paramInt1, int paramInt2);
  
  public abstract boolean getBoolean(int paramInt1, int paramInt2);
  
  public abstract long getLong(int paramInt1, int paramInt2);
  
  public abstract double getDouble(int paramInt1, int paramInt2);
  
  public abstract Slice getSlice(int paramInt1, int paramInt2);
  
  public abstract void appendTo(int paramInt1, int paramInt2, BlockBuilder paramBlockBuilder);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\WindowIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */