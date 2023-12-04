/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ public abstract class SliceOutput
/*     */   extends OutputStream
/*     */   implements DataOutput
/*     */ {
/*     */   public abstract void reset();
/*     */   
/*     */   public abstract void reset(int paramInt);
/*     */   
/*     */   public abstract int size();
/*     */   
/*     */   public abstract int getRetainedSize();
/*     */   
/*     */   public abstract int writableBytes();
/*     */   
/*     */   public abstract boolean isWritable();
/*     */   
/*     */   public final void writeBoolean(boolean value)
/*     */   {
/*  63 */     writeByte(value ? 1 : 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public final void write(int value)
/*     */   {
/*  69 */     writeByte(value);
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
/*     */   public abstract void writeByte(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeShort(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeInt(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeLong(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeFloat(float paramFloat);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeDouble(double paramDouble);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeBytes(Slice paramSlice);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeBytes(Slice paramSlice, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void write(byte[] source)
/*     */     throws IOException
/*     */   {
/* 162 */     writeBytes(source);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writeBytes(byte[] paramArrayOfByte);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void write(byte[] source, int sourceIndex, int length)
/*     */   {
/* 177 */     writeBytes(source, sourceIndex, length);
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
/*     */   public abstract void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
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
/*     */   public abstract void writeBytes(InputStream paramInputStream, int paramInt)
/*     */     throws IOException;
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
/*     */   public void writeZero(int length)
/*     */   {
/* 215 */     if (length == 0) {
/* 216 */       return;
/*     */     }
/* 218 */     if (length < 0) {
/* 219 */       throw new IllegalArgumentException("length must be 0 or greater than 0.");
/*     */     }
/*     */     
/* 222 */     int nLong = length >>> 3;
/* 223 */     int nBytes = length & 0x7;
/* 224 */     for (int i = nLong; i > 0; i--) {
/* 225 */       writeLong(0L);
/*     */     }
/* 227 */     if (nBytes == 4) {
/* 228 */       writeInt(0);
/*     */     }
/* 230 */     else if (nBytes < 4) {
/* 231 */       for (int i = nBytes; i > 0; i--) {
/* 232 */         writeByte(0);
/*     */       }
/*     */     }
/*     */     else {
/* 236 */       writeInt(0);
/* 237 */       for (int i = nBytes - 4; i > 0; i--) {
/* 238 */         writeByte(0);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Slice slice();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Slice getUnderlyingSlice();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String toString(Charset paramCharset);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendLong(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendDouble(double paramDouble);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendInt(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendShort(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendByte(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendBytes(byte[] paramArrayOfByte);
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract SliceOutput appendBytes(Slice paramSlice);
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeChar(int value)
/*     */   {
/* 296 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeChars(String s)
/*     */   {
/* 307 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeUTF(String s)
/*     */   {
/* 318 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeBytes(String s)
/*     */   {
/* 329 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\SliceOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */