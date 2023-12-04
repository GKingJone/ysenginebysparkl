package com.facebook.presto.jdbc.internal.spi.type;

import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;

public abstract interface FixedWidthType
  extends Type
{
  public abstract int getFixedSize();
  
  public abstract BlockBuilder createFixedSizeBlockBuilder(int paramInt);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\FixedWidthType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */