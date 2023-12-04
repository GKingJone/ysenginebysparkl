/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
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
/*     */ public final class InputStreamSliceInput
/*     */   extends SliceInput
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   private static final int MINIMUM_CHUNK_SIZE = 1024;
/*     */   private final InputStream inputStream;
/*     */   private final byte[] buffer;
/*     */   private final Slice slice;
/*     */   private long bufferOffset;
/*     */   private int bufferPosition;
/*     */   private int bufferFill;
/*     */   
/*     */   public InputStreamSliceInput(InputStream inputStream)
/*     */   {
/*  49 */     this(inputStream, 4096);
/*     */   }
/*     */   
/*     */   public InputStreamSliceInput(InputStream inputStream, int bufferSize)
/*     */   {
/*  54 */     Preconditions.checkArgument(bufferSize >= 1024, "minimum buffer size of 1024 required");
/*  55 */     if (inputStream == null) {
/*  56 */       throw new NullPointerException("inputStream is null");
/*     */     }
/*     */     
/*  59 */     this.inputStream = inputStream;
/*  60 */     this.buffer = new byte[bufferSize];
/*  61 */     this.slice = Slices.wrappedBuffer(this.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */   public long position()
/*     */   {
/*  67 */     return checkedCast(this.bufferOffset + this.bufferPosition);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPosition(long position)
/*     */   {
/*  73 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public int available()
/*     */   {
/*  79 */     if (this.bufferPosition < this.bufferFill) {
/*  80 */       return availableBytes();
/*     */     }
/*     */     
/*  83 */     return fillBuffer();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/*  89 */     return available() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int skipBytes(int n)
/*     */   {
/*  95 */     return (int)skip(n);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean readBoolean()
/*     */   {
/* 101 */     return readByte() != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte readByte()
/*     */   {
/* 107 */     ensureAvailable(1);
/* 108 */     byte v = this.slice.getByteUnchecked(this.bufferPosition);
/* 109 */     this.bufferPosition += 1;
/* 110 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedByte()
/*     */   {
/* 116 */     return readByte() & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public short readShort()
/*     */   {
/* 122 */     ensureAvailable(2);
/* 123 */     short v = this.slice.getShortUnchecked(this.bufferPosition);
/* 124 */     this.bufferPosition += 2;
/* 125 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedShort()
/*     */   {
/* 131 */     return readShort() & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readInt()
/*     */   {
/* 137 */     ensureAvailable(4);
/* 138 */     int v = this.slice.getIntUnchecked(this.bufferPosition);
/* 139 */     this.bufferPosition += 4;
/* 140 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public long readLong()
/*     */   {
/* 146 */     ensureAvailable(8);
/* 147 */     long v = this.slice.getLongUnchecked(this.bufferPosition);
/* 148 */     this.bufferPosition += 8;
/* 149 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public float readFloat()
/*     */   {
/* 155 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */   
/*     */ 
/*     */   public double readDouble()
/*     */   {
/* 161 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */   
/*     */ 
/*     */   public int read()
/*     */   {
/* 167 */     if (available() == 0) {
/* 168 */       return -1;
/*     */     }
/*     */     
/* 171 */     assert (availableBytes() > 0);
/* 172 */     int v = this.slice.getByteUnchecked(this.bufferPosition) & 0xFF;
/* 173 */     this.bufferPosition += 1;
/* 174 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public long skip(long length)
/*     */   {
/* 180 */     int availableBytes = availableBytes();
/*     */     
/* 182 */     if (availableBytes >= length) {
/* 183 */       this.bufferPosition = ((int)(this.bufferPosition + length));
/* 184 */       return length;
/*     */     }
/*     */     
/*     */ 
/* 188 */     this.bufferPosition = this.bufferFill;
/*     */     
/*     */     try
/*     */     {
/* 192 */       long inputStreamSkip = this.inputStream.skip(length - availableBytes);
/* 193 */       this.bufferOffset += inputStreamSkip;
/* 194 */       return availableBytes + inputStreamSkip;
/*     */     }
/*     */     catch (IOException e) {
/* 197 */       throw new RuntimeIOException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int read(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 204 */     if (available() == 0) {
/* 205 */       return -1;
/*     */     }
/*     */     
/* 208 */     assert (availableBytes() > 0);
/* 209 */     int batch = Math.min(availableBytes(), length);
/* 210 */     this.slice.getBytes(this.bufferPosition, destination, destinationIndex, batch);
/* 211 */     this.bufferPosition += batch;
/* 212 */     return batch;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 218 */     while (length > 0) {
/* 219 */       int batch = Math.min(availableBytes(), length);
/* 220 */       this.slice.getBytes(this.bufferPosition, destination, destinationIndex, batch);
/*     */       
/* 222 */       this.bufferPosition += batch;
/* 223 */       destinationIndex += batch;
/* 224 */       length -= batch;
/*     */       
/* 226 */       ensureAvailable(Math.min(length, 1024));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice readSlice(int length)
/*     */   {
/* 233 */     if (length == 0) {
/* 234 */       return Slices.EMPTY_SLICE;
/*     */     }
/*     */     
/* 237 */     Slice newSlice = Slices.allocate(length);
/* 238 */     readBytes(newSlice, 0, length);
/* 239 */     return newSlice;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(Slice destination, int destinationIndex, int length)
/*     */   {
/* 245 */     while (length > 0) {
/* 246 */       int batch = Math.min(availableBytes(), length);
/* 247 */       this.slice.getBytes(this.bufferPosition, destination, destinationIndex, batch);
/*     */       
/* 249 */       this.bufferPosition += batch;
/* 250 */       destinationIndex += batch;
/* 251 */       length -= batch;
/*     */       
/* 253 */       ensureAvailable(Math.min(length, 1024));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(OutputStream out, int length)
/*     */     throws IOException
/*     */   {
/* 261 */     while (length > 0) {
/* 262 */       int batch = Math.min(availableBytes(), length);
/* 263 */       out.write(this.buffer, this.bufferPosition, batch);
/*     */       
/* 265 */       this.bufferPosition += batch;
/* 266 */       length -= batch;
/*     */       
/* 268 */       ensureAvailable(Math.min(length, 1024));
/*     */     }
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 276 */       this.inputStream.close();
/*     */     }
/*     */     catch (IOException e) {
/* 279 */       throw new RuntimeIOException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private int availableBytes()
/*     */   {
/* 285 */     return this.bufferFill - this.bufferPosition;
/*     */   }
/*     */   
/*     */   private void ensureAvailable(int size)
/*     */   {
/* 290 */     if (this.bufferPosition + size < this.bufferFill) {
/* 291 */       return;
/*     */     }
/*     */     
/* 294 */     if (fillBuffer() < size) {
/* 295 */       throw new IndexOutOfBoundsException("End of stream");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private int fillBuffer()
/*     */   {
/* 302 */     int rest = this.bufferFill - this.bufferPosition;
/*     */     
/* 304 */     System.arraycopy(this.buffer, this.bufferPosition, this.buffer, 0, rest);
/*     */     
/* 306 */     this.bufferFill = rest;
/* 307 */     this.bufferOffset += this.bufferPosition;
/* 308 */     this.bufferPosition = 0;
/*     */     
/* 310 */     while (this.bufferFill < 1024) {
/*     */       try {
/* 312 */         int bytesRead = this.inputStream.read(this.buffer, this.bufferFill, this.buffer.length - this.bufferFill);
/* 313 */         if (bytesRead >= 0)
/*     */         {
/*     */ 
/*     */ 
/* 317 */           this.bufferFill += bytesRead;
/*     */         }
/*     */       } catch (IOException e) {
/* 320 */         throw new RuntimeIOException(e);
/*     */       }
/*     */     }
/*     */     
/* 324 */     return this.bufferFill;
/*     */   }
/*     */   
/*     */   private static int checkedCast(long value)
/*     */   {
/* 329 */     int result = (int)value;
/* 330 */     Preconditions.checkArgument(result == value, "Size is greater than maximum int value");
/* 331 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\InputStreamSliceInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */