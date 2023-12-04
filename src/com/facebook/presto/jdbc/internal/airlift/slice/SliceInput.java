/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.io.DataInput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SliceInput
/*     */   extends InputStream
/*     */   implements DataInput
/*     */ {
/*     */   public abstract long position();
/*     */   
/*     */   public abstract void setPosition(long paramLong);
/*     */   
/*     */   public abstract boolean isReadable();
/*     */   
/*     */   public abstract int available();
/*     */   
/*     */   public abstract int read();
/*     */   
/*     */   public abstract boolean readBoolean();
/*     */   
/*     */   public abstract byte readByte();
/*     */   
/*     */   public abstract int readUnsignedByte();
/*     */   
/*     */   public abstract short readShort();
/*     */   
/*     */   public abstract int readUnsignedShort();
/*     */   
/*     */   public abstract int readInt();
/*     */   
/*     */   public final long readUnsignedInt()
/*     */   {
/* 119 */     return readInt() & 0xFFFFFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long readLong();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract float readFloat();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract double readDouble();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Slice readSlice(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void readFully(byte[] destination)
/*     */   {
/* 163 */     readBytes(destination);
/*     */   }
/*     */   
/*     */ 
/*     */   public final int read(byte[] destination)
/*     */   {
/* 169 */     return read(destination, 0, destination.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void readBytes(byte[] destination)
/*     */   {
/* 184 */     readBytes(destination, 0, destination.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public final void readFully(byte[] destination, int offset, int length)
/*     */   {
/* 190 */     readBytes(destination, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void readBytes(Slice destination)
/*     */   {
/* 221 */     readBytes(destination, 0, destination.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void readBytes(Slice destination, int length)
/*     */   {
/* 238 */     readBytes(destination, 0, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void readBytes(Slice paramSlice, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void readBytes(OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract long skip(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract int skipBytes(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void mark(int readLimit)
/*     */   {
/* 284 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void reset()
/*     */   {
/* 291 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public final boolean markSupported()
/*     */   {
/* 297 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public final char readChar()
/*     */   {
/* 303 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public final String readLine()
/*     */   {
/* 309 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public final String readUTF()
/*     */   {
/* 315 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\SliceInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */