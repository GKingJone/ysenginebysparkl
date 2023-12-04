package com.facebook.presto.jdbc.internal.guava.hash;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public abstract interface PrimitiveSink
{
  public abstract PrimitiveSink putByte(byte paramByte);
  
  public abstract PrimitiveSink putBytes(byte[] paramArrayOfByte);
  
  public abstract PrimitiveSink putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract PrimitiveSink putShort(short paramShort);
  
  public abstract PrimitiveSink putInt(int paramInt);
  
  public abstract PrimitiveSink putLong(long paramLong);
  
  public abstract PrimitiveSink putFloat(float paramFloat);
  
  public abstract PrimitiveSink putDouble(double paramDouble);
  
  public abstract PrimitiveSink putBoolean(boolean paramBoolean);
  
  public abstract PrimitiveSink putChar(char paramChar);
  
  public abstract PrimitiveSink putUnencodedChars(CharSequence paramCharSequence);
  
  public abstract PrimitiveSink putString(CharSequence paramCharSequence, Charset paramCharset);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\hash\PrimitiveSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */