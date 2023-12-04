package com.facebook.presto.jdbc.internal.spi.type;

import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
import com.facebook.presto.jdbc.internal.spi.block.Block;
import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
import java.util.List;

public abstract interface Type
{
  @JsonValue
  public abstract TypeSignature getTypeSignature();
  
  public abstract String getDisplayName();
  
  public abstract boolean isComparable();
  
  public abstract boolean isOrderable();
  
  public abstract Class<?> getJavaType();
  
  public abstract List<Type> getTypeParameters();
  
  public abstract BlockBuilder createBlockBuilder(BlockBuilderStatus paramBlockBuilderStatus, int paramInt1, int paramInt2);
  
  public abstract BlockBuilder createBlockBuilder(BlockBuilderStatus paramBlockBuilderStatus, int paramInt);
  
  public abstract Object getObjectValue(ConnectorSession paramConnectorSession, Block paramBlock, int paramInt);
  
  public abstract boolean getBoolean(Block paramBlock, int paramInt);
  
  public abstract long getLong(Block paramBlock, int paramInt);
  
  public abstract double getDouble(Block paramBlock, int paramInt);
  
  public abstract Slice getSlice(Block paramBlock, int paramInt);
  
  public abstract Object getObject(Block paramBlock, int paramInt);
  
  public abstract void writeBoolean(BlockBuilder paramBlockBuilder, boolean paramBoolean);
  
  public abstract void writeLong(BlockBuilder paramBlockBuilder, long paramLong);
  
  public abstract void writeDouble(BlockBuilder paramBlockBuilder, double paramDouble);
  
  public abstract void writeSlice(BlockBuilder paramBlockBuilder, Slice paramSlice);
  
  public abstract void writeSlice(BlockBuilder paramBlockBuilder, Slice paramSlice, int paramInt1, int paramInt2);
  
  public abstract void writeObject(BlockBuilder paramBlockBuilder, Object paramObject);
  
  public abstract void appendTo(Block paramBlock, int paramInt, BlockBuilder paramBlockBuilder);
  
  public abstract boolean equalTo(Block paramBlock1, int paramInt1, Block paramBlock2, int paramInt2);
  
  public abstract long hash(Block paramBlock, int paramInt);
  
  public abstract int compareTo(Block paramBlock1, int paramInt1, Block paramBlock2, int paramInt2);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */