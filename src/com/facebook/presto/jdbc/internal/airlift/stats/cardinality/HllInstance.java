package com.facebook.presto.jdbc.internal.airlift.stats.cardinality;

import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;

abstract interface HllInstance
{
  public abstract void insertHash(long paramLong);
  
  public abstract long cardinality();
  
  public abstract int getIndexBitLength();
  
  public abstract int estimatedInMemorySize();
  
  public abstract int estimatedSerializedSize();
  
  public abstract Slice serialize();
  
  public abstract DenseHll toDense();
  
  @VisibleForTesting
  public abstract void verify();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\cardinality\HllInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */