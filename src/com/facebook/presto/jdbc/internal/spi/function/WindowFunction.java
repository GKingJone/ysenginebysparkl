package com.facebook.presto.jdbc.internal.spi.function;

import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;

public abstract interface WindowFunction
{
  public abstract void reset(WindowIndex paramWindowIndex);
  
  public abstract void processRow(BlockBuilder paramBlockBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\WindowFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */