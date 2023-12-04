package com.facebook.presto.jdbc.internal.spi.function;

import com.facebook.presto.jdbc.internal.spi.block.Block;
import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
import com.facebook.presto.jdbc.internal.spi.type.Type;

public abstract interface AccumulatorStateSerializer<T>
{
  public abstract Type getSerializedType();
  
  public abstract void serialize(T paramT, BlockBuilder paramBlockBuilder);
  
  public abstract void deserialize(Block paramBlock, int paramInt, T paramT);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\AccumulatorStateSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */