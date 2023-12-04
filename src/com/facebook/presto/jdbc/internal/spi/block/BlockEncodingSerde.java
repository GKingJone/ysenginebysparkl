package com.facebook.presto.jdbc.internal.spi.block;

import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;

public abstract interface BlockEncodingSerde
{
  public abstract BlockEncoding readBlockEncoding(SliceInput paramSliceInput);
  
  public abstract void writeBlockEncoding(SliceOutput paramSliceOutput, BlockEncoding paramBlockEncoding);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\BlockEncodingSerde.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */