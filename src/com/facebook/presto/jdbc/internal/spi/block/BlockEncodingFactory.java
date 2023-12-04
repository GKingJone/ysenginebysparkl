package com.facebook.presto.jdbc.internal.spi.block;

import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
import com.facebook.presto.jdbc.internal.spi.type.TypeManager;

public abstract interface BlockEncodingFactory<T extends BlockEncoding>
{
  public abstract String getName();
  
  public abstract T readEncoding(TypeManager paramTypeManager, BlockEncodingSerde paramBlockEncodingSerde, SliceInput paramSliceInput);
  
  public abstract void writeEncoding(BlockEncodingSerde paramBlockEncodingSerde, SliceOutput paramSliceOutput, T paramT);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\BlockEncodingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */