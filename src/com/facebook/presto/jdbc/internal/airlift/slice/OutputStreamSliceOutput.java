/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
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
/*     */ public final class OutputStreamSliceOutput
/*     */   extends SliceOutput
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   private static final int MINIMUM_CHUNK_SIZE = 1024;
/*  36 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(OutputStreamSliceOutput.class).instanceSize();
/*     */   
/*     */ 
/*     */   private final OutputStream outputStream;
/*     */   
/*     */ 
/*     */   private final Slice slice;
/*     */   
/*     */ 
/*     */   private final byte[] buffer;
/*     */   
/*     */   private long bufferOffset;
/*     */   
/*     */   private int bufferPosition;
/*     */   
/*     */ 
/*     */   public OutputStreamSliceOutput(OutputStream inputStream)
/*     */   {
/*  54 */     this(inputStream, 4096);
/*     */   }
/*     */   
/*     */   public OutputStreamSliceOutput(OutputStream outputStream, int bufferSize)
/*     */   {
/*  59 */     Preconditions.checkArgument(bufferSize >= 1024, "minimum buffer size of 1024 required");
/*  60 */     if (outputStream == null) {
/*  61 */       throw new NullPointerException("outputStream is null");
/*     */     }
/*     */     
/*  64 */     this.outputStream = outputStream;
/*  65 */     this.buffer = new byte[bufferSize];
/*  66 */     this.slice = Slices.wrappedBuffer(this.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*  73 */     flushBufferToOutputStream();
/*  74 */     this.outputStream.flush();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  81 */     flushBufferToOutputStream();
/*  82 */     this.outputStream.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  88 */     throw new UnsupportedOperationException("OutputStream can not be reset");
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(int position)
/*     */   {
/*  94 */     throw new UnsupportedOperationException("OutputStream can not be reset");
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 100 */     return checkedCast(this.bufferOffset + this.bufferPosition);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSize()
/*     */   {
/* 106 */     return this.slice.getRetainedSize() + INSTANCE_SIZE;
/*     */   }
/*     */   
/*     */ 
/*     */   public int writableBytes()
/*     */   {
/* 112 */     return Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWritable()
/*     */   {
/* 118 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeByte(int value)
/*     */   {
/* 124 */     ensureWritableBytes(1);
/* 125 */     this.slice.setByteUnchecked(this.bufferPosition, value);
/* 126 */     this.bufferPosition += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeShort(int value)
/*     */   {
/* 132 */     ensureWritableBytes(2);
/* 133 */     this.slice.setShortUnchecked(this.bufferPosition, value);
/* 134 */     this.bufferPosition += 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeInt(int value)
/*     */   {
/* 140 */     ensureWritableBytes(4);
/* 141 */     this.slice.setIntUnchecked(this.bufferPosition, value);
/* 142 */     this.bufferPosition += 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(long value)
/*     */   {
/* 148 */     ensureWritableBytes(8);
/* 149 */     this.slice.setLongUnchecked(this.bufferPosition, value);
/* 150 */     this.bufferPosition += 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFloat(float value)
/*     */   {
/* 156 */     writeInt(Float.floatToIntBits(value));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeDouble(double value)
/*     */   {
/* 162 */     writeLong(Double.doubleToLongBits(value));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(Slice source)
/*     */   {
/* 168 */     writeBytes(source, 0, source.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeBytes(Slice source, int sourceIndex, int length)
/*     */   {
/* 175 */     if (length >= 1024) {
/* 176 */       flushBufferToOutputStream();
/* 177 */       writeToOutputStream(source, sourceIndex, length);
/* 178 */       this.bufferOffset += length;
/*     */     }
/*     */     else {
/* 181 */       ensureWritableBytes(length);
/* 182 */       this.slice.setBytes(this.bufferPosition, source, sourceIndex, length);
/* 183 */       this.bufferPosition += length;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(byte[] source)
/*     */   {
/* 190 */     writeBytes(source, 0, source.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 197 */     if (length >= 1024) {
/* 198 */       flushBufferToOutputStream();
/* 199 */       writeToOutputStream(source, sourceIndex, length);
/* 200 */       this.bufferOffset += length;
/*     */     }
/*     */     else {
/* 203 */       ensureWritableBytes(length);
/* 204 */       this.slice.setBytes(this.bufferPosition, source, sourceIndex, length);
/* 205 */       this.bufferPosition += length;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(InputStream in, int length)
/*     */     throws IOException
/*     */   {
/* 213 */     while (length > 0) {
/* 214 */       int batch = ensureBatchSize(length);
/* 215 */       this.slice.setBytes(this.bufferPosition, in, batch);
/* 216 */       this.bufferPosition += batch;
/* 217 */       length -= batch;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeZero(int length)
/*     */   {
/* 224 */     Preconditions.checkArgument(length >= 0, "length must be 0 or greater than 0.");
/*     */     
/* 226 */     while (length > 0) {
/* 227 */       int batch = ensureBatchSize(length);
/* 228 */       Arrays.fill(this.buffer, this.bufferPosition, this.bufferPosition + batch, (byte)0);
/* 229 */       this.bufferPosition += batch;
/* 230 */       length -= batch;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendByte(int value)
/*     */   {
/* 237 */     writeByte(value);
/* 238 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendShort(int value)
/*     */   {
/* 244 */     writeShort(value);
/* 245 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendInt(int value)
/*     */   {
/* 251 */     writeInt(value);
/* 252 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendLong(long value)
/*     */   {
/* 258 */     writeLong(value);
/* 259 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendDouble(double value)
/*     */   {
/* 265 */     writeDouble(value);
/* 266 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 272 */     writeBytes(source, sourceIndex, length);
/* 273 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendBytes(byte[] source)
/*     */   {
/* 279 */     writeBytes(source);
/* 280 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendBytes(Slice slice)
/*     */   {
/* 286 */     writeBytes(slice);
/* 287 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice slice()
/*     */   {
/* 293 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getUnderlyingSlice()
/*     */   {
/* 299 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString(Charset charset)
/*     */   {
/* 305 */     return toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 311 */     StringBuilder builder = new StringBuilder("OutputStreamSliceOutputAdapter{");
/* 312 */     builder.append("outputStream=").append(this.outputStream);
/* 313 */     builder.append("bufferSize=").append(this.slice.length());
/* 314 */     builder.append('}');
/* 315 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private void ensureWritableBytes(int minWritableBytes)
/*     */   {
/* 320 */     if (this.bufferPosition + minWritableBytes > this.slice.length()) {
/* 321 */       flushBufferToOutputStream();
/*     */     }
/*     */   }
/*     */   
/*     */   private int ensureBatchSize(int length)
/*     */   {
/* 327 */     ensureWritableBytes(Math.min(1024, length));
/* 328 */     return Math.min(length, this.slice.length() - this.bufferPosition);
/*     */   }
/*     */   
/*     */   private void flushBufferToOutputStream()
/*     */   {
/* 333 */     writeToOutputStream(this.buffer, 0, this.bufferPosition);
/* 334 */     this.bufferOffset += this.bufferPosition;
/* 335 */     this.bufferPosition = 0;
/*     */   }
/*     */   
/*     */   private void writeToOutputStream(byte[] source, int sourceIndex, int length)
/*     */   {
/*     */     try {
/* 341 */       this.outputStream.write(source, sourceIndex, length);
/*     */     }
/*     */     catch (IOException e) {
/* 344 */       throw new RuntimeIOException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeToOutputStream(Slice source, int sourceIndex, int length)
/*     */   {
/*     */     try {
/* 351 */       source.getBytes(sourceIndex, this.outputStream, length);
/*     */     }
/*     */     catch (IOException e) {
/* 354 */       throw new RuntimeIOException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static int checkedCast(long value)
/*     */   {
/* 360 */     int result = (int)value;
/* 361 */     Preconditions.checkArgument(result == value, "Size is greater than maximum int value");
/* 362 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\OutputStreamSliceOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */